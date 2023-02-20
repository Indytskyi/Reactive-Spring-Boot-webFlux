package com.indytskyi.moviesservice.client;

import com.indytskyi.moviesservice.domain.Review;
import com.indytskyi.moviesservice.exception.ReviewsClientException;
import com.indytskyi.moviesservice.exception.ReviewsServerException;
import com.indytskyi.moviesservice.util.RetryUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
@Slf4j
public class ReviewsRestClient {

    private final WebClient webClient;

    @Value("${restClient.reviewsUrl}")
    private String reviewsUrl;

    public Flux<Review> retrieveReviews(String movieId) {
        var url = UriComponentsBuilder
                .fromHttpUrl(reviewsUrl)
                .queryParam("movieInfoId", movieId)
                .buildAndExpand().toUriString();

        return webClient
                .get()
                .uri(url)
                .retrieve()
                .onStatus(HttpStatus::is4xxClientError, this::errorHandler4xxRetrieveMovieInfo)
                .onStatus(HttpStatus::is5xxServerError, this::errorHandler5xxRetrieveMovieInfo)
                .bodyToFlux(Review.class)
                .retryWhen(RetryUtil.retrySpec());
    }

    private Mono<? extends Throwable> errorHandler4xxRetrieveMovieInfo(
            ClientResponse clientResponse) {

        log.info("Status code is : {}", clientResponse.statusCode().value());

        if (clientResponse.statusCode().equals(HttpStatus.NOT_FOUND)) {
            return Mono.empty();
        }

        return clientResponse.bodyToMono(String.class)
                .flatMap(responseMessage -> Mono.error(
                        new ReviewsClientException(responseMessage)));
    }

    private Mono<? extends Throwable> errorHandler5xxRetrieveMovieInfo(
            ClientResponse clientResponse) {
        log.info("Status code is : {}", clientResponse.statusCode().value());

        return clientResponse.bodyToMono(String.class)
                .flatMap(responseMessage ->
                        Mono.error(new ReviewsServerException(
                                "Server exception in Review service" + responseMessage)));
    }
}
