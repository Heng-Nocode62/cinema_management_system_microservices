package com.heng.cms.cinemaservice.mapper;


import com.heng.cms.cinemaservice.domain.Cinema;
import com.heng.cms.cinemaservice.dto.CinemaRequest;
import com.heng.cms.cinemaservice.dto.CinemaResponse;
import org.springframework.stereotype.Service;

@Service
public class CinemaMapper {
	public Cinema toCinema(CinemaRequest request) {
		return Cinema.builder()
				.name(request.getName())
				.location(request.getLocation())
				.city(request.getCity())
				.phone(request.getPhone())
				.email(request.getEmail())
				.build();
	}
	
	public CinemaResponse toCinemaResponse(Cinema cinema) {
		return CinemaResponse.builder()
				.id(cinema.getId())
				.name(cinema.getName())
				.location(cinema.getLocation())
				.city(cinema.getCity())
				.phone(cinema.getPhone())
				.email(cinema.getEmail())
				.build();
	}

}
