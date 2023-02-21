package com.indytskyi.moviesreviewsservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

@EnableEurekaClient
@SpringBootApplication
public class MoviesReviewsServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(MoviesReviewsServiceApplication.class, args);
    }

}
