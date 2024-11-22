package com.example.giraeureka;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

@SpringBootApplication
@EnableEurekaServer
public class GiraEurekaApplication {

    public static void main(String[] args) {
        SpringApplication.run(GiraEurekaApplication.class, args);
    }

}
