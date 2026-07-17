package com.heng.cms.cinemaservice.repository;

import com.heng.cms.cinemaservice.domain.Seat;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface SeatRepository extends JpaRepository<Seat, UUID> {

}
