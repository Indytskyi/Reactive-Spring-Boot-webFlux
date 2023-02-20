package com.indytskyi.moviesservice.controller;


import com.indytskyi.moviesservice.domain.Movie;
import com.indytskyi.moviesservice.domain.MovieInfo;
import com.indytskyi.moviesservice.service.impl.MovieServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/v1/movies")
@RequiredArgsConstructor
public class MoviesController {

    private final MovieServiceImpl movieServiceImpl;

    @GetMapping("/{id}")
    public Mono<Movie> retrieveMovieById(@PathVariable("id") String movieId) {
         return movieServiceImpl.retrieveMovieById(movieId);
    }

    @GetMapping(value = "/stream", produces = MediaType.APPLICATION_NDJSON_VALUE)
    public Flux<MovieInfo> retrieveMovieInfos() {
        return movieServiceImpl.retrieveMovieInfosStream();
    }
}
