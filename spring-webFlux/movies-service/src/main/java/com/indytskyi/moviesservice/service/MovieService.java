package com.indytskyi.moviesservice.service;

import com.indytskyi.moviesservice.domain.Movie;
import com.indytskyi.moviesservice.domain.MovieInfo;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface MovieService {
    Mono<Movie> retrieveMovieById(String movieId);

    Flux<MovieInfo> retrieveMovieInfosStream();
}
