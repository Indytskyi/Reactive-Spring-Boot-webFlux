package com.indytskyi.moviesservice.intg;


import com.github.tomakehurst.wiremock.client.WireMock;
import com.indytskyi.moviesservice.domain.Movie;
import com.indytskyi.moviesservice.domain.MovieInfo;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.util.Objects;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@AutoConfigureWebTestClient
@AutoConfigureWireMock(port = 8084) // spin up a http server in port 8084
@TestPropertySource(properties = {
        "restClient.moviesInfoUrl=http://localhost:8084/v1/movie-info",
        "restClient.reviewsUrl=http://localhost:8084/v1/reviews"
})
public class MoviesControllerIntegrationTest {

    @Autowired
    WebTestClient webTestClient;

    @Test
    void retrieveMovieById() {
        //given
        var movieId = 1L;
        stubFor(get(urlEqualTo("/v1/movie-info" + "/" + movieId))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBodyFile("movieinfo.json")));

        stubFor(get(urlPathEqualTo("/v1/reviews"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBodyFile("reviews.json")));

        //when
        webTestClient
                .get()
                .uri("/v1/movies/{id}", movieId)
                .exchange()
                .expectStatus().isOk()
                .expectBody(Movie.class)
                .consumeWith(movieEntityExchangeResult -> {
                    var movie = movieEntityExchangeResult.getResponseBody();
                    assert Objects.requireNonNull(movie).getReviewList().size() == 2;
                    assertEquals("Batman Begins", movie.getMovieInfo().getName());
                });

        //then
    }

    @Test
    void retrieveMovieById_movieInfo_404() {
        //given
        var movieId = 1L;
        var expectedBody = "There is no MovieInfo Available  for the passed in Id : 1";
        stubFor(get(urlEqualTo("/v1/movie-info" + "/" + movieId))
                .willReturn(aResponse()
                        .withStatus(404)));

        stubFor(get(urlPathEqualTo("/v1/reviews"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBodyFile("reviews.json")));

        //when
        webTestClient
                .get()
                .uri("/v1/movies/{id}", movieId)
                .exchange()
                .expectStatus()
                .is4xxClientError()
                .expectBody(String.class)
                .isEqualTo(expectedBody);

        //then
    }

    @Test
    void retrieveMovieById_reviews_404() {
        //given
        var movieId = 1L;
        stubFor(get(urlEqualTo("/v1/movie-info" + "/" + movieId))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBodyFile("movieinfo.json")));

        stubFor(get(urlPathEqualTo("/v1/reviews"))
                .willReturn(aResponse()
                        .withStatus(404)));

        //when
        webTestClient
                .get()
                .uri("/v1/movies/{id}", movieId)
                .exchange()
                .expectStatus().isOk()
                .expectBody(Movie.class)
                .consumeWith(movieEntityExchangeResult -> {
                    var movie = movieEntityExchangeResult.getResponseBody();
                    assert Objects.requireNonNull(movie).getReviewList().size() == 0;
                    assertEquals("Batman Begins", movie.getMovieInfo().getName());
                });

        //then
    }

    @Test
    void retrieveMovieById_movieInfo_5XX() {
        //given
        var movieId = 1L;
        stubFor(get(urlEqualTo("/v1/movie-info" + "/" + movieId))
                .willReturn(aResponse()
                        .withStatus(500)
                        .withBody(" Unavailable")));

        stubFor(get(urlPathEqualTo("/v1/reviews"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBodyFile("reviews.json")));

        //when
        webTestClient
                .get()
                .uri("/v1/movies/{id}", movieId)
                .exchange()
                .expectStatus()
                .is5xxServerError()
                .expectBody(String.class)
                .isEqualTo("Server exception in MovieInfo service Unavailable");

        //then
        WireMock.verify(8, getRequestedFor(urlEqualTo("/v1/movie-info" + "/" + movieId)));
    }

    @Test
    void retrieveMovieById_reviews_5XX() {
        //given
        var movieId = 1L;
        stubFor(get(urlEqualTo("/v1/movie-info" + "/" + movieId))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBodyFile("movieinfo.json")));

        stubFor(get(urlPathEqualTo("/v1/reviews"))
                .willReturn(aResponse()
                        .withStatus(500)
                        .withBody(" Unavailable")));

        //when
        webTestClient
                .get()
                .uri("/v1/movies/{id}", movieId)
                .exchange()
                .expectStatus()
                .is5xxServerError()
                .expectBody(String.class)
                .isEqualTo("Server exception in Review service Unavailable");

        //then
        WireMock.verify(6, getRequestedFor(urlPathMatching("/v1/reviews*")));
    }

    @Test
    void retrieveMovieStream() {
        //given
        var id = "retrieveMovieById";

        //when
        stubFor(get(urlEqualTo("/v1/movie-info/stream"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", MediaType.APPLICATION_NDJSON_VALUE)
                        .withBodyFile("movieinfo.json")));
        Flux<MovieInfo> responseBody = webTestClient
                .get()
                .uri("/v1/movies/stream", id)
                .exchange()
                .expectStatus().isOk()
                .returnResult(MovieInfo.class)
                .getResponseBody();
        StepVerifier.create(responseBody)
                .assertNext(movieInfo -> {
                    assert Objects.equals(movieInfo.getMovieInfoId(), "1");
                })
                .thenCancel()
                .verify();
    }


}
