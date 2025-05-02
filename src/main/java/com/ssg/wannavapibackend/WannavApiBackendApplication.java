package com.ssg.wannavapibackend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class WannavApiBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(WannavApiBackendApplication.class, args);
    }

}