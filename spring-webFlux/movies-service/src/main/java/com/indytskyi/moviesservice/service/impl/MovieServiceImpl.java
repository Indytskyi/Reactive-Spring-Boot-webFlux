package com.indytskyi.moviesservice.service.impl;

import com.indytskyi.moviesservice.client.MoviesInfoRestClient;
import com.indytskyi.moviesservice.client.ReviewsRestClient;
import com.indytskyi.moviesservice.domain.Movie;
import com.indytskyi.moviesservice.domain.MovieInfo;
import com.indytskyi.moviesservice.service.MovieService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class MovieServiceImpl implements MovieService {

    private final MoviesInfoRestClient moviesInfoRestClient;
    private final ReviewsRestClient reviewsRestClient;

    @Override
    public Mono<Movie> retrieveMovieById(String movieId) {
        return moviesInfoRestClient
                .retrieveMovieInfo(movieId)
                .flatMap(movieInfo -> aggregateMovieInfoAndRetrievers(movieInfo, movieId));
    }

    private Mono<Movie> aggregateMovieInfoAndRetrievers(MovieInfo movieInfo, String movieId) {
        var reviewsListMovie = reviewsRestClient.retrieveReviews(movieId)
                .collectList();

        return reviewsListMovie.map(reviews -> new Movie(movieInfo, reviews));
    }

    @Override
    public Flux<MovieInfo> retrieveMovieInfosStream() {
        return moviesInfoRestClient.retrieveMovieInfosStream();
    }
}
