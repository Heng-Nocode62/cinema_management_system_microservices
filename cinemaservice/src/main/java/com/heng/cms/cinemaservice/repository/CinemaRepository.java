package com.heng.cms.cinemaservice.repository;

import com.heng.cms.cinemaservice.domain.Cinema;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface CinemaRepository extends JpaRepository<Cinema, UUID>{

}
