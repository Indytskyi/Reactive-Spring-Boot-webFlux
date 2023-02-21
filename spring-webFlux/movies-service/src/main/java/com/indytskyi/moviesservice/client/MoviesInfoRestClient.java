package com.indytskyi.moviesservice.client;

import com.indytskyi.moviesservice.domain.MovieInfo;
import com.indytskyi.moviesservice.exception.MoviesInfoClientException;
import com.indytskyi.moviesservice.exception.MoviesInfoServerException;
import com.indytskyi.moviesservice.util.RetryUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
@Slf4j
public class MoviesInfoRestClient {

    @Value("${restClient.moviesInfoUrl}")
    private String moviesInfoUrl;
    private final WebClient webClient;

    public Mono<MovieInfo> retrieveMovieInfo(String movieId) {
        return webClient
                .get()
                .uri(moviesInfoUrl + "/" + movieId)
                .retrieve()
                .onStatus(
                        HttpStatus::is4xxClientError,
                        clientResponse -> errorHandler4xxRetrieveMovieInfo(clientResponse, movieId)
                )
                .onStatus(
                        HttpStatus::is5xxServerError,
                        this::errorHandler5xxRetrieveMovieInfo
                )
                .bodyToMono(MovieInfo.class)
                .retryWhen(RetryUtil.retrySpec());
    }

    public Flux<MovieInfo> retrieveMovieInfosStream() {
        return webClient
                .get()
                .uri(moviesInfoUrl + "/stream")
                .retrieve()
                .onStatus(
                        HttpStatus::is4xxClientError,
                        this::errorHandler4xxRetrieveMovieInfoStream
                )
                .onStatus(HttpStatus::is5xxServerError, this::errorHandler5xxRetrieveMovieInfo)
                .bodyToFlux(MovieInfo.class)
                .retryWhen(RetryUtil.retrySpec());
    }

    private Mono<? extends Throwable> errorHandler4xxRetrieveMovieInfoStream(
            ClientResponse clientResponse) {

        log.info("Status code is : {}", clientResponse.statusCode().value());

        return clientResponse.bodyToMono(String.class)
                .flatMap(responseMessage ->
                        Mono.error(
                                new MoviesInfoClientException(
                                        responseMessage, clientResponse.statusCode().value())
                        )
                );
    }

    private Mono<? extends Throwable> errorHandler4xxRetrieveMovieInfo(
            ClientResponse clientResponse,
            String movieId) {

        log.info("Status code is : {}", clientResponse.statusCode().value());

        if (clientResponse.statusCode().equals(HttpStatus.NOT_FOUND)) {
            return Mono.error(new MoviesInfoClientException(
                    "There is no MovieInfo Available  for the passed in Id : " + movieId,
                    clientResponse.statusCode().value()
            ));
        }

        return clientResponse.bodyToMono(String.class)
                .flatMap(responseMessage ->
                        Mono.error(new MoviesInfoClientException(
                                responseMessage, clientResponse.statusCode().value())
                        )
                );
    }

    private Mono<? extends Throwable> errorHandler5xxRetrieveMovieInfo(
            ClientResponse clientResponse) {

        log.info("Status code is : {}", clientResponse.statusCode().value());

        return clientResponse.bodyToMono(String.class)
                .flatMap(responseMessage ->
                        Mono.error(new MoviesInfoServerException(
                                "Server exception in MovieInfo service" + responseMessage)));
    }

}
