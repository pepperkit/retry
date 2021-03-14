package io.github.pepperkit.retry;

public interface CallableRetry<V> {

    /**
     * Performs a retryable action and returns a result.
     */
    V call();
}
