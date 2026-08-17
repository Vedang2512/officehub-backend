package com.officehub;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class OfficehubBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(
                OfficehubBackendApplication.class,
                args
        );
    }
}