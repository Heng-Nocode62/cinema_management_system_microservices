package com.heng.cms.concessionservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class ConcessionserviceApplication {

	public static void main(String[] args) {
		SpringApplication.run(ConcessionserviceApplication.class, args);
	}

}
