package com.indytskyi.moviesreviewsservice.intg;

import com.indytskyi.moviesreviewsservice.model.Review;
import com.indytskyi.moviesreviewsservice.repository.ReviewReactiveRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.test.StepVerifier;

import java.util.List;

import static com.mongodb.assertions.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@AutoConfigureWebTestClient
public class ReviewsIntegrationTest {

    private static final String REVIEWS_URL = "/v1/reviews";
    @Autowired
    WebTestClient webTestClient;

    @Autowired
    ReviewReactiveRepository reviewReactiveRepository;

    @BeforeEach
    void setUp() {
        var reviewsList = List.of(
                new Review(null, 1L, "Awesome Movie", 9.0),
                new Review(null, 1L, "Awesome Movie1", 9.0),
                new Review(null, 2L, "Excellent Movie", 8.0));
        reviewReactiveRepository.saveAll(reviewsList)
                .blockLast();
    }

    @AfterEach
    void tearDown() {
        reviewReactiveRepository.deleteAll().block();
    }

    @Test
    void addReview() {
        //given
        var review = new Review(null, 1L, "Awesome Movie", 9.0);

        //when
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
    void getReviews() {
        //given

        //when
        webTestClient
                .get()
                .uri(REVIEWS_URL)
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
        var review = new Review(null, 1L, "Awesome Movie", 9.0);
        var savedReview = reviewReactiveRepository.save(review).block();
        var reviewUpdate = new Review(null, 1L, "Not an Awesome Movie", 8.0);
        //when
        assert savedReview != null;

        webTestClient
                .put()
                .uri(REVIEWS_URL+"/{id}", savedReview.getReviewId())
                .bodyValue(reviewUpdate)
                .exchange()
                .expectStatus().isOk()
                .expectBody(Review.class)
                .consumeWith(reviewResponse -> {
                    var updatedReview = reviewResponse.getResponseBody();
                    assert updatedReview != null;
                    System.out.println("updatedReview : " + updatedReview);
                    assertNotNull(savedReview.getReviewId());
                    assertEquals(8.0, updatedReview.getRating());
                    assertEquals("Not an Awesome Movie", updatedReview.getComment());
                });

    }

    @Test
    void deleteReview() {
        //given
        var review = new Review(null, 1L, "Awesome Movie", 9.0);
        var savedReview = reviewReactiveRepository.save(review).block();
        //when
        assert savedReview != null;
        webTestClient
                .delete()
                .uri(REVIEWS_URL+"/{id}", savedReview.getReviewId())
                .exchange()
                .expectStatus().isNoContent();
    }


    @Test
    void getReviewsByMovieInfoId() {
        //given

        //when
        webTestClient
                .get()
                .uri(uriBuilder -> {
                    return uriBuilder.path(REVIEWS_URL)
                            .queryParam("movieInfoId", "1")
                            .build();
                })
                .exchange()
                .expectStatus()
                .is2xxSuccessful()
                .expectBodyList(Review.class)
                .value(reviewList -> {
                    System.out.println("reviewList : " + reviewList);
                    assertEquals(2, reviewList.size());
                });

    }


    @Test
    void getReviews_stream() {
        //given
        var review = new Review(null, 1L, "Awesome Movie", 9.0);

        //when
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

        var reviewsStreamFlux = webTestClient
                .get()
                .uri(REVIEWS_URL + "/stream")
                .exchange()
                .expectStatus()
                .is2xxSuccessful()
                .returnResult(Review.class)
                .getResponseBody();

        //then
        StepVerifier.create(reviewsStreamFlux)
                .assertNext(movieInfo1 -> {
                    assert movieInfo1.getReviewId() != null;
                })
                .thenCancel()
                .verify();
    }

}
