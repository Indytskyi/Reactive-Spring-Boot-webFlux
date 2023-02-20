package com.indytskyi.moviesreviewsservice.repository;

import com.indytskyi.moviesreviewsservice.model.Review;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;

public interface ReviewReactiveRepository extends ReactiveMongoRepository<Review, String> {

    Flux<Review> findAllByMovieInfoId(Long movieInfoId);
}
