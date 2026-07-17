package com.heng.cms.cinemaservice.service.impl;


import com.heng.cms.cinemaservice.domain.Cinema;
import com.heng.cms.cinemaservice.dto.CinemaRequest;
import com.heng.cms.cinemaservice.dto.CinemaResponse;
import com.heng.cms.cinemaservice.dto.CinemaUpdateRequest;
import com.heng.cms.cinemaservice.dto.PageResponse;
import com.heng.cms.cinemaservice.exception.ResourceNotFoundException;
import com.heng.cms.cinemaservice.mapper.CinemaMapper;
import com.heng.cms.cinemaservice.repository.CinemaRepository;
import com.heng.cms.cinemaservice.service.CinemaService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class CinemaServiceImpl implements CinemaService {
	private final CinemaRepository cinemaRepository;
	private final CinemaMapper cinemaMapper;
	private final Clock clock;
	

	@Override
	public CinemaResponse create(CinemaRequest request) {
		Instant now = clock.instant();
		Cinema cinema = cinemaMapper.toCinema(request);
		cinema.setCreatedAt(now);
		cinema.setLastUpdatedAt(now);
		Cinema savedCinema = cinemaRepository.save(cinema);
		log.info("cinema id = {} is created",savedCinema);
		return cinemaMapper.toCinemaResponse(savedCinema);
	}
	
	@Override
	public PageResponse<CinemaResponse> getAll(int page, int size) {
		Pageable pageable = PageRequest.of(page, size);
		
		Page<Cinema> cinemas = cinemaRepository.findAll(pageable);
		List<CinemaResponse> cinemaResponses = cinemas.stream().map(cinemaMapper::toCinemaResponse).toList();
		
		return new PageResponse<>(cinemaResponses,cinemas);
	}
	@Override
	public CinemaResponse getById(UUID id) {
		Cinema cinema = cinemaRepository.findById(id).orElseThrow(()-> new ResourceNotFoundException("cinema not found"));
		return cinemaMapper.toCinemaResponse(cinema);
	}

	@Override
	@Transactional
	//TODO this logic is not correct, validate the request
	public void update(UUID id, CinemaUpdateRequest request) {

		Cinema cinema = cinemaRepository.findById(id)
				.orElseThrow(()-> new ResourceNotFoundException("cinema",id));

		if (request.getName() != null) {
			cinema.setName(request.getName());
		}
		if (request.getPhone() != null) {
			cinema.setPhone(request.getPhone());
		}
		if (request.getCity() != null) {
			cinema.setCity(request.getCity());
		}
		if (request.getLocation() != null) {
			cinema.setLocation(request.getLocation());
		}
		if (request.getEmail() != null) {
			cinema.setEmail(request.getEmail());
		}
		cinema.setLastUpdatedAt(clock.instant());
		log.info("cinema id = {} is updated",cinema);
	}

	@Override
	public List<CinemaResponse> getAllByIds(List<UUID> cinemaIds) {
		List<Cinema> cinemas = cinemaRepository.findAllById(cinemaIds);
		return cinemas.stream().map( cinemaMapper::toCinemaResponse).toList();
	}

}
