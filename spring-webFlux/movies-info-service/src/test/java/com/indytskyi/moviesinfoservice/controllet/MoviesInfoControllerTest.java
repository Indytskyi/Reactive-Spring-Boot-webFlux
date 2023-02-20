package com.indytskyi.moviesinfoservice.controllet;


import com.indytskyi.moviesinfoservice.model.MovieInfo;
import com.indytskyi.moviesinfoservice.service.impl.MoviesInfoServiceImpl;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.when;

@WebFluxTest(controllers = MoviesInfoController.class)
@AutoConfigureWebTestClient
class MoviesInfoControllerTest {

    @Autowired
    WebTestClient webTestClient;

    @MockBean
    private MoviesInfoServiceImpl moviesInfoServiceImpl;
    public static final String MOVIES_INFO_URL = "/v1/movie-info";

    @BeforeEach
    void setUp() {
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void addMovieInfo() {
        //given
        var movieInfo = new MovieInfo("mockId", "The Dark Knight",
                2008, List.of("Christian Bale", "HeathLedger"), LocalDate.parse("2008-07-18"));
        //when
        when(moviesInfoServiceImpl.addMovieInfo(isA(MovieInfo.class))).thenReturn(Mono.just(movieInfo));
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
    void addMovieInfoWithInvalidBody() {
        //given
        var movieInfo = new MovieInfo("mockId", "",
                -2008, List.of("Christian Bale", "HeathLedger"), LocalDate.parse("2008-07-18"));
        //when
        webTestClient
                .post()
                .uri(MOVIES_INFO_URL)
                .bodyValue(movieInfo)
                .exchange()
                .expectStatus()
                .isBadRequest()
                .expectBody(String.class)
                .consumeWith(stringEntityExchangeResult -> {
                    var responseBody = stringEntityExchangeResult.getResponseBody();
                    var expectedErrorMessage = "movieInfo.name must be present," +
                            " movieInfo.year must be a Positive number";
                    assert  responseBody != null;
                });



        //then
    }

    @Test
    void getAllMoviesInfo() {
        //given
        var movieInfos = List.of(new MovieInfo(null, "Batman Begins",
                        2005, List.of("Christian Bale", "Michael Cane"), LocalDate.parse("2005-06-15")),
                new MovieInfo(null, "The Dark Knight",
                        2008, List.of("Christian Bale", "HeathLedger"), LocalDate.parse("2008-07-18")),
                new MovieInfo("abc", "Dark Knight Rises",
                        2012, List.of("Christian Bale", "Tom Hardy"), LocalDate.parse("2012-07-20")));
        //when
        when(moviesInfoServiceImpl.getAllMoviesInfo())
                .thenReturn(Flux.fromIterable(movieInfos));

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
    void getMovieInfoById() {
        var id = "abc";

        when(moviesInfoServiceImpl.getMovieInfoById(isA(String.class)))
                .thenReturn(Mono.just(new MovieInfo("abc", "Dark Knight Rises",
                        2012, List.of("Christian Bale", "Tom Hardy"), LocalDate.parse("2012-07-20"))));

        webTestClient
                .get()
                .uri(MOVIES_INFO_URL + "/{id}", id)
                .exchange()
                .expectStatus()
                .is2xxSuccessful()
                .expectBody()
                .jsonPath("$.name").isEqualTo("Dark Knight Rises");
    }

    @Test
    void updateMoviesInfo() {
        //given
        var updatedMovieInfo = new MovieInfo("abc", "Dark Knight Rises 2",
                2012, List.of("Christian Bale", "Tom Hardy"), LocalDate.parse("2012-07-20"));
        //when
        when(moviesInfoServiceImpl.updateMovieInfo(isA(MovieInfo.class), isA(String.class)))
                .thenReturn(Mono.just(updatedMovieInfo));
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
    void deleteMoviesInfo() {
        //given
        //when
        when(moviesInfoServiceImpl.deleteMovieInfo(isA(String.class)))
                .thenReturn(Mono.empty());
        webTestClient
                .delete()
                .uri(MOVIES_INFO_URL + "/{id}", "abc")
                .exchange()
                .expectStatus()
                .isNoContent()
                .expectBody(Void.class);


        //then
    }
}