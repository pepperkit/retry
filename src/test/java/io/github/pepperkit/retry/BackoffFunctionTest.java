package io.github.pepperkit.retry;

import java.time.Duration;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BackoffFunctionTest {

    @Test
    void fixedBackoff() {
        Duration initialDelay = Duration.ofSeconds(3);
        BackoffFunction.Fixed function = new BackoffFunction.Fixed();

        Duration delay1 = function.delay(1, initialDelay);
        Duration delay2 = function.delay(2, initialDelay);
        Duration delay3 = function.delay(3, initialDelay);

        assertEquals(initialDelay, delay1);
        assertEquals(initialDelay, delay2);
        assertEquals(initialDelay, delay3);
    }

    @Test
    void exponentialBackoffWhenFactor2() {
        Duration initialDelay = Duration.ofSeconds(3);
        BackoffFunction.Exponential function = new BackoffFunction.Exponential(2);

        Duration delay1 = function.delay(1, initialDelay);
        Duration delay2 = function.delay(2, initialDelay);
        Duration delay3 = function.delay(3, initialDelay);

        assertEquals(Duration.ofSeconds(6), delay1);
        assertEquals(Duration.ofSeconds(12), delay2);
        assertEquals(Duration.ofSeconds(24), delay3);
    }

    @Test
    void defaultExponentialBackoff() {
        Duration initialDelay = Duration.ofSeconds(3);
        BackoffFunction.Exponential function = new BackoffFunction.Exponential();

        Duration delay1 = function.delay(1, initialDelay);
        Duration delay2 = function.delay(2, initialDelay);
        Duration delay3 = function.delay(3, initialDelay);

        assertEquals(Duration.ofSeconds(9), delay1);
        assertEquals(Duration.ofSeconds(27), delay2);
        assertEquals(Duration.ofSeconds(81), delay3);
    }

    @Test
    void randomizedBackoff() {
        Duration initialDelay = Duration.ofSeconds(3);
        BackoffFunction.Randomized function = new BackoffFunction.Randomized(5);

        Duration delay1 = function.delay(1, initialDelay);
        assertTrue(delay1.toMillis() > 100L);

        Duration delay2 = function.delay(2, initialDelay);
        assertTrue(delay2.toMillis() > 100L);

        Duration delay3 = function.delay(3, initialDelay);
        assertTrue(delay3.toMillis() > 100L);
    }
}
