package com.indytskyi.moviesservice.exception;

import lombok.Getter;

@Getter
public class MoviesInfoClientException extends RuntimeException {
    private final String message;
    private final Integer statusCode;

    public MoviesInfoClientException(String message, Integer statusCode) {
        super(message);
        this.message = message;
        this.statusCode = statusCode;
    }

}
