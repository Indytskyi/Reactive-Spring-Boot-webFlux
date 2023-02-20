package com.indytskyi.moviesinfoservice.intg.controller;


import com.indytskyi.moviesinfoservice.model.MovieInfo;
import com.indytskyi.moviesinfoservice.repository.MovieInfoRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.test.StepVerifier;

import java.time.LocalDate;
import java.util.List;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
//@ActiveProfiles("test")
@AutoConfigureWebTestClient
class MoviesInfoControllerIntegrationTest {

    @Autowired
    MovieInfoRepository movieInfoRepository;

    @Autowired
    WebTestClient webTestClient;

    public static final String MOVIES_INFO_URL = "/v1/movie-info";

    @BeforeEach
    void setUp() {
        var movieInfos = List.of(new MovieInfo(null, "Batman Begins",
                        2005, List.of("Christian Bale", "Michael Cane"), LocalDate.parse("2005-06-15")),
                new MovieInfo(null, "The Dark Knight",
                        2008, List.of("Christian Bale", "HeathLedger"), LocalDate.parse("2008-07-18")),
                new MovieInfo("abc", "Dark Knight Rises",
                        2012, List.of("Christian Bale", "Tom Hardy"), LocalDate.parse("2012-07-20")));

        movieInfoRepository.saveAll(movieInfos)
                .blockLast();
    }

    @AfterEach
    void tearDown() {
        movieInfoRepository.deleteAll().block();
    }

    @Test
    void getAllMovieInfo() {
        //given
        //when
        webTestClient
                .get()
                .uri(MOVIES_INFO_URL)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBodyList(MovieInfo.class)
                .hasSize(3);


        //then
    }

    @Test
    void getMovieInfoByYear() {
        //given
        var uri = UriComponentsBuilder.fromUriString(MOVIES_INFO_URL)
                .queryParam("year", 2005)
                .buildAndExpand().toUri();
        //when
        webTestClient
                .get()
                .uri(uri)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBodyList(MovieInfo.class)
                .hasSize(1);


        //then
    }

    @Test
    void getMovieInfoById() {
        //given
        //when
        webTestClient
                .get()
                .uri(MOVIES_INFO_URL + "/{id}", "abc")
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody() //MovieInfo.class
                .jsonPath("$.name").isEqualTo("Dark Knight Rises");
//                .consumeWith(movieInfoEntityExchangeResult -> {
//                    var responseBody = movieInfoEntityExchangeResult.getResponseBody();
//                    assertNotNull(responseBody);
//                });

        //then
    }

    @Test
    @DisplayName("Update movie info with non-existent Id")
    void getMovieInfoWithNonExistentId() {
        //given
        var id = "bad";
        //when
        webTestClient
                .get()
                .uri(MOVIES_INFO_URL + "/{id}", id)
                .exchange()
                .expectStatus()
                .isNotFound();


        //then
    }

    @Test
    void updateMovieInfo() {
        //given
        var updatedMovieInfo = new MovieInfo("abc", "Dark Knight Rises 2",
                2012, List.of("Christian Bale", "Tom Hardy"), LocalDate.parse("2012-07-20"));
        //when
        webTestClient
                .put()
                .uri(MOVIES_INFO_URL + "/{id}", "abc")
                .bodyValue(updatedMovieInfo)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody() //MovieInfo.class
                .jsonPath("$.name").isEqualTo("Dark Knight Rises 2");

        //then
    }

    @Test
    @DisplayName("Update movie info with non-existent Id")
    void updateMovieInfoWithNonExistentId() {
        //given
        var id = "bad";
        var updatedMovieInfo = new MovieInfo("abc", "Dark Knight Rises 2",
                2012, List.of("Christian Bale", "Tom Hardy"), LocalDate.parse("2012-07-20"));
        //when
        webTestClient
                .put()
                .uri(MOVIES_INFO_URL + "/{id}", id)
                .bodyValue(updatedMovieInfo)
                .exchange()
                .expectStatus()
                .isNotFound();

        //then
    }

    @Test
    void addMovieInfo() {
        //given
        var movieInfo = new MovieInfo(null, "The Dark Knight",
                2008, List.of("Christian Bale", "HeathLedger"), LocalDate.parse("2008-07-18"));
        //when
        webTestClient
                .post()
                .uri(MOVIES_INFO_URL)
                .bodyValue(movieInfo)
                .exchange()
                .expectStatus()
                .isCreated()
                .expectBody(MovieInfo.class)
                .consumeWith(movieInfoEntityExchangeResult -> {
                    var responseBody = movieInfoEntityExchangeResult.getResponseBody();
                    assert responseBody != null;
                    assert responseBody.getMovieInfoId() != null;
                });

        //then
    }

    @Test
    void deleteMovieInfoById() {
        //given
        //when
        webTestClient
                .delete()
                .uri(MOVIES_INFO_URL + "/{id}", "abc")
                .exchange()
                .expectStatus()
                .isNoContent()
                .expectBody(Void.class);
        //then
    }

    @Test
    void getAllMovieInfo_stream() {
        //given
        var movieInfo = new MovieInfo(null, "The Dark Knight",
                2008, List.of("Christian Bale", "HeathLedger"), LocalDate.parse("2008-07-18"));
        //when
        webTestClient
                .post()
                .uri(MOVIES_INFO_URL)
                .bodyValue(movieInfo)
                .exchange()
                .expectStatus()
                .isCreated()
                .expectBody(MovieInfo.class)
                .consumeWith(movieInfoEntityExchangeResult -> {
                    var responseBody = movieInfoEntityExchangeResult.getResponseBody();
                    assert responseBody != null;
                    assert responseBody.getMovieInfoId() != null;
                });

        var movieStreamFlux = webTestClient
                .get()
                .uri(MOVIES_INFO_URL + "/stream")
                .exchange()
                .expectStatus()
                .isOk()
                .returnResult(MovieInfo.class)
                .getResponseBody();

        //then
        StepVerifier.create(movieStreamFlux)
                .assertNext(movieInfo1 -> {
                    assert movieInfo1.getMovieInfoId() != null;
                })
                .thenCancel()
                .verify();
    }

}