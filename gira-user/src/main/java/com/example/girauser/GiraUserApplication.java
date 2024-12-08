package com.example.girauser;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;


@SpringBootApplication
@EnableFeignClients
public class GiraUserApplication {

	public static void main(String[] args) {
		SpringApplication.run(GiraUserApplication.class, args);
	}

}
