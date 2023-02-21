package com.indytskyi.moviesreviewsservice.router;

import com.indytskyi.moviesreviewsservice.handler.ReviewHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

@Configuration
public class ReviewRouter {

    @Bean
    public RouterFunction<ServerResponse> reviewsRoute(ReviewHandler reviewHandler) {

        return RouterFunctions
                .route(RequestPredicates.GET("/v1/reviews"),
                        reviewHandler::getReviews)
                .andRoute(RequestPredicates.POST("/v1/reviews"),
                        reviewHandler::addReview)
                .andRoute(RequestPredicates.PUT("/v1/reviews/{id}"),
                        reviewHandler::updateReview)
                .andRoute(RequestPredicates.DELETE("/v1/reviews/{id}"),
                        reviewHandler::deleteReview)
                .andRoute(RequestPredicates.GET("/v1/reviews/stream"),
                        reviewHandler::getReviewsStream);
    }
}
