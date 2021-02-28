package io.github.pepperkit.retry;

import java.time.Duration;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import static io.github.pepperkit.retry.Retry.retry;
import static org.junit.jupiter.api.Assertions.assertEquals;

class RetryTest {

    @Test
    @Timeout(6)
    void fixedRetry() {

        AtomicInteger counter = new AtomicInteger();
        Retry.retry(3)
                .backoff(new BackoffFunction.Fixed())
                .delay(Duration.ofSeconds(2))
                .handle(IllegalArgumentException.class)
                .onFailure(e -> {
                    if (e instanceof IllegalArgumentException) {
                        System.out.println("I caught you!");
                    }
                })
                .run(() -> {
                    counter.incrementAndGet();
                    throw new IllegalArgumentException("Just to test");
                });

        assertEquals(3, counter.get());
    }

    @Test
    void exponentialWhenResultIsSuccess() {
        Optional<String> result = Retry.retry(1)
                .backoff(new BackoffFunction.Exponential(3))
                .delay(Duration.ofSeconds(2))
                .call(() -> "RESULT");

        assertEquals(Optional.of("RESULT"), result);
    }
}
