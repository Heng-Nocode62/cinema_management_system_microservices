package com.heng.cms.movieservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class MovieserviceApplication {

	public static void main(String[] args) {
		SpringApplication.run(MovieserviceApplication.class, args);
	}

}
