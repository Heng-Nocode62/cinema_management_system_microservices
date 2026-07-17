package com.heng.cms.bookingservice.service.impl;

import com.heng.cms.bookingservice.domain.Booking;
import com.heng.cms.bookingservice.domain.BookingConcession;
import com.heng.cms.bookingservice.domain.BookingSeat;
import com.heng.cms.bookingservice.domain.enumeric.BookingStatus;
import com.heng.cms.bookingservice.dto.BookingResponse;
import com.heng.cms.bookingservice.dto.CreateBookingRequest;
import com.heng.cms.bookingservice.dto.client.*;
import com.heng.cms.bookingservice.dto.client.enumeric.SeatStatus;
import com.heng.cms.bookingservice.exception.BadRequestException;
import com.heng.cms.bookingservice.exception.ResourceNotFoundException;
import com.heng.cms.bookingservice.factory.BookingFactory;
import com.heng.cms.bookingservice.factory.BookingSeatFactory;
import com.heng.cms.bookingservice.mapper.BookingMapper;
import com.heng.cms.bookingservice.repository.BookingConcessionRepository;
import com.heng.cms.bookingservice.repository.BookingRepository;
import com.heng.cms.bookingservice.repository.BookingSeatRepository;
import com.heng.cms.bookingservice.service.BookingService;
import com.heng.cms.bookingservice.service.client.ConcessionClient;
import com.heng.cms.bookingservice.service.client.PaymentClient;
import com.heng.cms.bookingservice.service.client.SchedulingClient;
import com.heng.cms.bookingservice.spec.BookingSpecification;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {
	private final SchedulingClient schedulingClient;
	private final BookingRepository bookingRepository;
	private final BookingSeatRepository bookingSeatRepository;
	private final BookingMapper bookingMapper;
	private final BookingSpecification bookingSpecification;
	private final Clock clock;
	private final BookingFactory bookingFactory;
	private final BookingSeatFactory bookingSeatFactory;
	private final ConcessionClient concessionClient;
	private final BookingConcessionRepository bookingConcessionRepository;
	private final PaymentClient paymentClient;
	
	@Override
	@Transactional
	public BookingResponse createBooking(CreateBookingRequest request) {
		Instant now = clock.instant();
		ShowtimeResponse showtimeResponse = schedulingClient.findShowtimeById(request.showtimeId());
		List<ShowtimeSeatResponse> showtimeSeatResponses = schedulingClient.findShowtimeSeatByIdsAndShowtimeId(request.showtimeSeatIds(), request.showtimeId());
		validateAvailability(showtimeSeatResponses);
		validateCoupleSeatSelection(showtimeSeatResponses);

		// mark seat to reserve to lock

		schedulingClient.lockSeat(request.showtimeSeatIds());

		UUID userAccountId = extractUserAccountId();
		//create booking
		Booking booking = bookingFactory.createPendingBooking(request.showtimeId(),userAccountId, now);

		Booking savedBooking = bookingRepository.save(booking);

		//Create booking Seat
		BigDecimal totalSeatPrice = BigDecimal.ZERO;
        for (ShowtimeSeatResponse seat : showtimeSeatResponses) {
            BookingSeat bookingSeat = bookingSeatFactory.createBookingSeat(savedBooking,seat,now);
            bookingSeatRepository.save(bookingSeat);
			savedBooking.getBookingSeats().add(bookingSeat);
            totalSeatPrice = totalSeatPrice.add(bookingSeat.getPrice());
        }

		ConcessionResponse concessionResponse = concessionClient.reserveConcession(showtimeResponse.getCinemaId(),request.combos(),request.items());
		log.info("concessionResponse={}", concessionResponse);

		List<BookingConcession> bookingConcessions = new ArrayList<>();
		for (ConcessionResponse.Item item : concessionResponse.items()) {
			BookingConcession concession = new BookingConcession();
			concession.setBooking(savedBooking);
			concession.setConcessionId(item.id());
			concession.setName(item.name());
			concession.setUnitPrice(item.unitPrice());
			concession.setQuantity(item.quantity());
			concession.setType(item.type());
			concession.setCreatedAt(now);

			bookingConcessions.add(concession);

		}
		bookingConcessionRepository.saveAll(bookingConcessions);

		BigDecimal totalPrice = totalSeatPrice.add(concessionResponse.totalPrice());
		savedBooking.setTotalPrice(totalPrice);
		savedBooking.setLastUpdatedAt(now);
		InitiatePaymentCommand command = new InitiatePaymentCommand(
				savedBooking.getId(),
				totalPrice,
				"USD",
				null,
				null
		);

		PaymentInitiateResponse paymentInitiateResponse = paymentClient.initiatePayment(command);

		return bookingMapper.toBookingResponse(savedBooking,paymentInitiateResponse);
	}

	@Override
	@Transactional
	public void confirmBooking(UUID bookingId) {
		Instant now = clock.instant();
		Booking booking = bookingRepository.findById(bookingId).orElseThrow(() -> new ResourceNotFoundException("booking", bookingId));
		if (booking.getStatus() == BookingStatus.CONFIRMED) {
			throw new BadRequestException("booking is already confirmed");
		}
		List<UUID> showtimeSeatIds = booking.getBookingSeats().stream().map(BookingSeat::getShowtimeSeatId).toList();
		schedulingClient.updateShowtimeSeatStatus(showtimeSeatIds,SeatStatus.BOOKED);
		booking.setStatus(BookingStatus.CONFIRMED);
		booking.setLastUpdatedAt(now);
		ShowtimeResponse showtimeResponse = schedulingClient.findShowtimeById(booking.getShowtimeId());
		List<BookingConcession> bookingConcessions = booking.getBookingConcessions();
		concessionClient.confirmConcession(showtimeResponse.getCinemaId(),bookingConcessions);
	}

	@Override
	public void cancelBookingById(UUID id) {
		Instant now = clock.instant();
		Booking booking = bookingRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("booking", id));
		if (booking.getStatus() == BookingStatus.CONFIRMED ) {
			throw new BadRequestException("booking is already confirmed");
		}
		List<UUID> showtimeSeatIds = booking.getBookingSeats().stream().map(BookingSeat::getShowtimeSeatId).toList();
		booking.setStatus(BookingStatus.CANCELLED);
		booking.setLastUpdatedAt(now);
		ShowtimeResponse showtimeResponse = schedulingClient.findShowtimeById(booking.getShowtimeId());
		List<BookingConcession> bookingConcessions = booking.getBookingConcessions();
		log.info("bookingConcessions={}", bookingConcessions);
		if (!bookingConcessions.isEmpty()) {
			concessionClient.cancelConcession(showtimeResponse.getCinemaId(),bookingConcessions);
			schedulingClient.unlockSeat(showtimeSeatIds);
		}

	}

	private UUID extractUserAccountId() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		Jwt jwt = (Jwt) authentication.getPrincipal();
		String userAccountId = jwt.getClaimAsString("userAccountId");
		return UUID.fromString(userAccountId) ;

	}

	private void validateCoupleSeatSelection(List<ShowtimeSeatResponse> showtimeSeatResponses) {
		Map<Long, List<ShowtimeSeatResponse>> grouped = showtimeSeatResponses.stream()
				.filter(s -> s.getGroupCoupleId() != null)
				.collect(Collectors.groupingBy(s -> s.getGroupCoupleId()));
		grouped.values().forEach(seats->{
			if (seats.size()!=2)
				throw new BadRequestException("Couple Seat must be book together");
				}
		);

	}

	private void validateAvailability(List<ShowtimeSeatResponse> showtimeSeatResponses) {
		for (ShowtimeSeatResponse s: showtimeSeatResponses){
			if(!s.getStatus().equals(SeatStatus.AVAILABLE)){
				throw new BadRequestException("Seat is not available");
			}
		}
	}


	@Transactional
	@Scheduled(fixedRate = 30000)
	@Override
	public void releaseExpirationBooking() {
		Instant now = clock.instant();
		List<Booking> expiredBookings = bookingRepository.findExpiredBookings(now,BookingStatus.PENDING);

		if (expiredBookings.isEmpty()) {
			return;
		}
		List<UUID> showtimeSeatIds = expiredBookings.stream().map(Booking::getBookingSeats)
				.flatMap(bookingSeats -> bookingSeats
						.stream()
						.map(BookingSeat::getShowtimeSeatId)
				).toList();
		schedulingClient.unlockSeat(showtimeSeatIds);
		for (Booking booking : expiredBookings) {
			ShowtimeResponse showtimeResponse = schedulingClient.findShowtimeById(booking.getShowtimeId());
			List<BookingConcession> bookingConcessions = booking.getBookingConcessions();
			concessionClient.cancelConcession(showtimeResponse.getCinemaId(),bookingConcessions);
		}
		expiredBookings.forEach(booking->{
			booking.setLastUpdatedAt(now);
			booking.setStatus(BookingStatus.CANCELLED);
		});
	}
//
//	@Override
//	public BookingResponse getById(Long id) {
//		Booking booking = bookingRepository.findById(id)
//				.orElseThrow(() -> new BadRequestException("Booking not found"));
//		return bookingMapper.toBookingResponse(booking);
//	}
//
//	@Override
//	public PageResponse<BookingResponse> getAllBooking(int page, int size, BookingFilter filter) {
//		Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
//		Specification<Booking> spec =  bookingSpecification.filter(filter);
//		Page<Booking> bookings = bookingRepository.findAll(spec, pageable);
//
//		List<BookingResponse> bookingResponses = bookings.stream().map(bookingMapper::toBookingResponse).toList();
//		return new PageResponse<>(bookingResponses,bookings);
//	}
//
//	@Override
//	@Transactional
//	public void cancelBookingById(Long bookingId){
//		Booking booking = bookingRepository.findById(bookingId)
//				.orElseThrow(() -> new BadRequestException("Booking not found"));
//		booking.setStatus(BookingStatus.CANCELLED);
//		List<ShowtimeSeat> seats = showtimeSeatRepository.findAllByBookingId(booking.getId());
//		seats.forEach(s -> s.setStatus(SeatStatus.AVAILABLE));
//	}

}
