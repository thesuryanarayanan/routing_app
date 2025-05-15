package com.example.routing_backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class RoutingBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(RoutingBackendApplication.class, args);
    }

}
