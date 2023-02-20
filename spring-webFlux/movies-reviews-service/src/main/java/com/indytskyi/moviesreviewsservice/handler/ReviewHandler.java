package com.indytskyi.moviesreviewsservice.handler;

import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

public interface ReviewHandler {
    Mono<ServerResponse> addReview(ServerRequest request);

    Mono<ServerResponse> getReviews(ServerRequest serverRequest);

    Mono<ServerResponse> updateReview(ServerRequest request);

    Mono<ServerResponse> deleteReview(ServerRequest serverRequest);

    Mono<ServerResponse> getReviewsStream(ServerRequest serverRequest);
}
