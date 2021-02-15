package art.aukhatov.retry;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import java.time.Duration;
import java.util.Optional;

import static art.aukhatov.retry.Retry.retry;
import static org.junit.jupiter.api.Assertions.assertEquals;

class RetryTest {

    @Test
    @Timeout(6)
    void fixedRetry() {
        retry(3)
                .backoff(new BackoffFunction.Fixed())
                .delay(Duration.ofSeconds(2))
                .handle(IllegalArgumentException.class)
                .onFailure(e -> {
                    if (e instanceof IllegalArgumentException) {
                        System.out.println("I caught you!");
                    }
                })
                .run(() -> {
                    throw new IllegalArgumentException("Just to test");
                });
    }

    @Test
    void abortWhen() {
        Optional<String> result = retry(1)
                .backoff(new BackoffFunction.Fixed())
                .delay(Duration.ofSeconds(2))
                .call(() -> "ABORT", r -> r.equals("ABORT"));

        assertEquals(Optional.of("ABORT"), result);
    }
}