package com.indytskyi.moviesreviewsservice.exception;

public class ReviewNotFoundException extends RuntimeException {
    private final String message;

    public ReviewNotFoundException(String message) {
        super(message);
        this.message = message;
    }
}
