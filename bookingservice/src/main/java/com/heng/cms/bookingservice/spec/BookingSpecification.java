package com.heng.cms.bookingservice.spec;


import com.heng.cms.bookingservice.domain.Booking;
import com.heng.cms.bookingservice.domain.enumeric.BookingStatus;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.UUID;

@Component
public class BookingSpecification {

    private Specification<Booking> hasUserId(UUID userId){
        if (userId == null) return null;
        return (root, query, cb) ->
                cb.equal(root.get("createdBy"), userId);
    }

    //TODO
//    private Specification<Booking> hasShowtimeId(Long showtimeId){
//        if (showtimeId == null) return null;
//        return (root, query, cb) ->{
//            Join<Booking, Showtime> showtime = root.join("showtime", JoinType.INNER);
//            return cb.equal(showtime.get("id"), showtimeId);
//        };
//
//    }

    private Specification<Booking> hasStatus(BookingStatus status){
        if (status == null) return null;
        return (root, query, cb) ->
                cb.equal(root.get("status"), status);
    }
    private Specification<Booking> createdAfter(Instant createdAfter){
        if (createdAfter == null) return null;
        return (root, query, cb) ->
                cb.greaterThanOrEqualTo(root.get("createdAt"), createdAfter);
    }

    public Specification<Booking> filter(BookingFilter filter){
        if (filter == null) return null;
        return Specification.allOf(
                hasUserId(filter.getUserId()),
                createdAfter(filter.getCreatedAfter())

        );
//        return Specification.where(hasUserId(filter.getUserId()))
//                .and(hasStatus(filter.getStatus()))
//                .and(createdAfter(filter.getCreatedAfter()))
//                .and(hasShowtimeId(filter.getShowtimeId()));
    }
}
