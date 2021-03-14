package io.github.pepperkit.retry;

public interface RunnableRetry {

    /**
     * Performs a retryable action.
     */
    void run();
}
