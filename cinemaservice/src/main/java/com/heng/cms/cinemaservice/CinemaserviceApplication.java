package com.heng.cms.cinemaservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class CinemaserviceApplication {

	public static void main(String[] args) {
		SpringApplication.run(CinemaserviceApplication.class, args);
	}

}
