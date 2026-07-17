package com.heng.cms.paymentservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableDiscoveryClient
@SpringBootApplication
@EnableScheduling
public class PaymentserviceApplication {

	public static void main(String[] args) {
		SpringApplication.run(PaymentserviceApplication.class, args);
	}

}
