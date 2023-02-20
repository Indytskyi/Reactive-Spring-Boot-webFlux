package com.indytskyi.moviesservice.util;

import com.indytskyi.moviesservice.exception.MoviesInfoServerException;
import com.indytskyi.moviesservice.exception.ReviewsServerException;
import java.time.Duration;
import reactor.core.Exceptions;
import reactor.util.retry.Retry;

public class RetryUtil {

    public static Retry retrySpec() {
        return Retry.fixedDelay(3, Duration.ofSeconds(1))
                .filter(ex -> ex instanceof MoviesInfoServerException
                        || ex instanceof ReviewsServerException)
                .onRetryExhaustedThrow(((retryBackoffSpec, retrySignal) ->
                        Exceptions.propagate(retrySignal.failure())));
    }
}
