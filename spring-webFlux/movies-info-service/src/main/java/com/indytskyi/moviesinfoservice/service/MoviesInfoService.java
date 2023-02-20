package com.indytskyi.moviesinfoservice.service;

import com.indytskyi.moviesinfoservice.model.MovieInfo;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface MoviesInfoService {
    Mono<MovieInfo> addMovieInfo(MovieInfo movieInfo);

    Flux<MovieInfo> getAllMoviesInfo();

    Mono<MovieInfo> getMovieInfoById(String id);

    Mono<MovieInfo> updateMovieInfo(MovieInfo updatedMovieInfo, String id);

    Mono<Void> deleteMovieInfo(String id);

    Flux<MovieInfo> getMovieInfoByYear(Integer year);
}
