package io.github.pepperkit.retry;

import java.time.Duration;
import java.util.HashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public final class Retry {

    private static final int DEFAULT_MAX_ATTEMPTS = 3;
    private static final Duration DEFAULT_DELAY = Duration.ofSeconds(3);

    private final int maxAttempts;
    private final Set<Class<? extends Throwable>> failureConditions;
    private final Set<Class<? extends Throwable>> abortConditions;
    private final Set<Consumer<? super Throwable>> failureConsumers;

    private BackoffFunction backoffFunc;
    private Duration maxDelay;
    private Duration delay;

    private Retry(int maxAttempts) {
        this.maxAttempts = maxAttempts;
        this.failureConditions = new HashSet<>();
        this.abortConditions = new HashSet<>();
        this.failureConsumers = new HashSet<>();
        this.delay = DEFAULT_DELAY;
    }

    /**
     * Default fabric method with 3 attempts.
     */
    public static Retry retry() {
        return new Retry(DEFAULT_MAX_ATTEMPTS);
    }

    /**
     * Specifies how many attempts an action will be retried.
     *
     * @param maxAttempts number of attempts
     * @return this instance
     */
    public static Retry retry(int maxAttempts) {
        return new Retry(maxAttempts);
    }

    /**
     * Specifies the backoff function before call next attempt.
     */
    public Retry backoff(BackoffFunction backoffFunc) {
        this.backoffFunc = backoffFunc;
        return this;
    }

    /**
     * Specifies an initial timeout {@link Duration} that uses in {@link BackoffFunction} before call next attempt.
     */
    public Retry delay(Duration delay) {
        this.delay = delay;
        return this;
    }

    /**
     * Specifies max timeout value.
     * If the value is set, a backoff function can't exceed it.
     */
    public Retry maxDelay(Duration maxDelay) {
        this.maxDelay = maxDelay;
        return this;
    }

    /**
     * Specifies the failures to handle.
     * Any failures that are assignable from the {@code failures} will be handled.
     */
    public Retry handle(Class<? extends Throwable> failure) {
        failureConditions.add(failure);
        return this;
    }

    /**
     * Specifies the failures to handle.
     * Any failures that are assignable from the {@code failures} will be handled.
     */
    public Retry handle(Set<Class<? extends Throwable>> failures) {
        failureConditions.addAll(failures);
        return this;
    }

    /**
     * Specifies that retries have to be aborted.
     * Any failures that are assignable from the {@code failures} will be aborted.
     */
    public Retry abortIf(Class<? extends Throwable> failure) {
        abortConditions.add(failure);
        return this;
    }

    /**
     * Specifies that retries have to be aborted.
     * Any failures that are assignable from the {@code failures} will be aborted.
     */
    public Retry abortIf(Set<Class<? extends Throwable>> failures) {
        abortConditions.addAll(failures);
        return this;
    }

    /**
     * Registers the handler to be called when an execution attempt fails.
     *
     * @param fn the handler
     */
    public Retry onFailure(Consumer<? super Throwable> fn) {
        failureConsumers.add(fn);
        return this;
    }

    /**
     * The method performs an action that can retry it when specified exceptions occurred.
     *
     * @param retry the operation with retry
     * @throws RetryInterruptedException when something went wrong during delay
     */
    public void run(RunnableRetry retry) throws RetryInterruptedException {
        for (int attempt = 1; attempt <= maxAttempts; attempt++) {
            try {
                retry.run();
                return;
            } catch (final Exception ex) {

                if (abortConditions.contains(ex.getClass())) {
                    break;
                }

                failureConsumers.forEach(f -> f.accept(ex));

                if (isAllowedRetry(attempt, ex)) {
                    Duration delayMillis = backoffDelay(attempt);
                    sleep(delayMillis);
                }
            }
        }
    }

    /**
     * The method computes a result that can retry it when specified exceptions occurred.
     *
     * @param retry the operation with retry
     * @param <R>   the result type
     * @return a result of the operation
     * @throws RetryInterruptedException when something went wrong during delay
     */
    public <R> Optional<R> call(CallableRetry<R> retry) throws RetryInterruptedException {
        for (int attempt = 1; attempt <= maxAttempts; attempt++) {
            try {
                return Optional.of(retry.call());
            } catch (Exception ex) {
                if (abortConditions.contains(ex.getClass())) {
                    break;
                }

                failureConsumers.forEach(f -> f.accept(ex));

                if (isAllowedRetry(attempt, ex)) {
                    Duration delayMillis = backoffDelay(attempt);
                    sleep(delayMillis);
                }
            }
        }
        return Optional.empty();
    }

    private boolean isAllowedRetry(int attempt, Throwable ex) {
        return (failureConditions.isEmpty() || failureConditions.contains(ex.getClass())) && attempt < maxAttempts;
    }

    private Duration backoffDelay(int attempt) {
        Duration delayMillis = backoffFunc.delay(attempt, delay);

        if (Objects.nonNull(maxDelay) && maxDelay.toMillis() > 0) {
            delayMillis = min(delayMillis, maxDelay);
        }
        return delayMillis;
    }

    private Duration min(Duration duration1, Duration duration2) {
        if (duration1.compareTo(duration2) < 0) {
            return duration1;
        } else {
            return duration2;
        }
    }

    private void sleep(Duration timeout) {
        try {
            TimeUnit.MILLISECONDS.sleep(timeout.toMillis());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RetryInterruptedException(e);
        }
    }
}
