package com.indytskyi.moviesreviewsservice.unit;


import com.indytskyi.moviesreviewsservice.exception.handler.GlobalErrorHandler;
import com.indytskyi.moviesreviewsservice.model.Review;
import com.indytskyi.moviesreviewsservice.handler.impl.ReviewHandlerImpl;
import com.indytskyi.moviesreviewsservice.repository.ReviewReactiveRepository;
import com.indytskyi.moviesreviewsservice.router.ReviewRouter;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.when;

@WebFluxTest
@ContextConfiguration(classes = {ReviewRouter.class, ReviewHandlerImpl.class, GlobalErrorHandler.class})
@AutoConfigureWebTestClient
public class ReviewsUnitTest {

    @MockBean
    private ReviewReactiveRepository reviewReactiveRepository;

    private static final String REVIEWS_URL = "/v1/reviews";

    @Autowired
    private WebTestClient webTestClient;

    @Test
    void addReview() {
        //given
        var review = new Review(null, 1L, "Awesome Movie", 9.0);

        //when
        when(reviewReactiveRepository.save(isA(Review.class)))
                .thenReturn(Mono.just(new Review("abc", 1L, "Awesome Movie", 9.0)));

        webTestClient
                .post()
                .uri(REVIEWS_URL)
                .bodyValue(review)
                .exchange()
                .expectStatus()
                .isCreated()
                .expectBody(Review.class)
                .consumeWith(reviewEntityExchangeResult -> {
                    var responseBody = reviewEntityExchangeResult.getResponseBody();
                    assert responseBody != null;
                    assert responseBody.getReviewId() != null;
                });

        //then
    }

    @Test
    void addReviewWithInvalidData() {
        //given
        var review = new Review(null, null, "", -9.0);

        //when
        webTestClient
                .post()
                .uri(REVIEWS_URL)
                .bodyValue(review)
                .exchange()
                .expectStatus()
                .isBadRequest();
        //then
    }

    @Test
    void getAllReviews() {
        //given
        var reviewList = List.of(
                new Review(null, 1L, "Awesome Movie", 9.0),
                new Review(null, 1L, "Awesome Movie1", 9.0),
                new Review(null, 2L, "Excellent Movie", 8.0));

        //when
        when(reviewReactiveRepository.findAll()).thenReturn(Flux.fromIterable(reviewList));

        webTestClient
                .get()
                .uri("/v1/reviews")
                .exchange()
                .expectStatus()
                .is2xxSuccessful()
                .expectBodyList(Review.class)
                .value(reviews -> {
                    assertEquals(3, reviews.size());
                });

    }

    @Test
    void updateReview() {
        //given

        var reviewUpdate = new Review(null, 1L, "Not an Awesome Movie", 8.0);

        //when
        when(reviewReactiveRepository.save(isA(Review.class))).thenReturn(Mono.just(new Review("abc", 1L, "Not an Awesome Movie", 8.0)));
        when(reviewReactiveRepository.findById((String) any())).thenReturn(Mono.just(new Review("abc", 1L, "Awesome Movie", 9.0)));


        webTestClient
                .put()
                .uri("/v1/reviews/{id}", "abc")
                .bodyValue(reviewUpdate)
                .exchange()
                .expectStatus().isOk()
                .expectBody(Review.class)
                .consumeWith(reviewResponse ->{
                    var updatedReview = reviewResponse.getResponseBody();
                    assert updatedReview != null;
                    System.out.println("updatedReview : "+ updatedReview);
                    assertEquals(8.0,updatedReview.getRating());
                    assertEquals("Not an Awesome Movie", updatedReview.getComment());
                });

    }

    @Test
    void updateReviewWithNonExistentId() {
        //given
        var id = "abc";
        var reviewUpdate = new Review(null, 1L, "Not an Awesome Movie", 8.0);
        var expectedExceptionMessage = "Review not found for the given Review id " + id;

        //when
        when(reviewReactiveRepository.findById((String) any())).thenReturn(Mono.empty());


        webTestClient
                .put()
                .uri("/v1/reviews/{id}", id)
                .bodyValue(reviewUpdate)
                .exchange()
                .expectStatus()
                .isNotFound()
                .expectBody(String.class)
                .isEqualTo(expectedExceptionMessage);
    }

    @Test
    void deleteReview() {
        //given
        var reviewId= "abc";


        //when
        when(reviewReactiveRepository.findById((String) any())).thenReturn(Mono.just(new Review("abc", 1L, "Awesome Movie", 9.0)));
        when(reviewReactiveRepository.deleteById((String) any())).thenReturn(Mono.empty());

        webTestClient
                .delete()
                .uri("/v1/reviews/{id}", reviewId)
                .exchange()
                .expectStatus().isNoContent();
    }
}
