package art.aukhatov.retry;

import java.time.Duration;
import java.util.Collections;
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

    public static Retry retry() {
        return new Retry(DEFAULT_MAX_ATTEMPTS);
    }

    public static Retry retry(int maxAttempts) {
        return new Retry(maxAttempts);
    }

    public Retry backoff(BackoffFunction backoffFunc) {
        this.backoffFunc = backoffFunc;
        return this;
    }

    public Retry delay(Duration delay) {
        this.delay = delay;
        return this;
    }

    public Retry maxDelay(Duration maxDelay) {
        this.maxDelay = maxDelay;
        return this;
    }

    public Retry handle(Class<? extends Throwable>... failure) {
        Collections.addAll(failureConditions, failure);
        return this;
    }

    public Retry abortIf(Class<? extends Throwable>... failure) {
        Collections.addAll(abortConditions, failure);
        return this;
    }

    public Retry onFailure(Consumer<? super Throwable> fn) {
        failureConsumers.add(fn);
        return this;
    }

    public void run(RetryRunnable retry) throws RetryInterruptedException {
        for (int attempt = 1; attempt <= maxAttempts; attempt++) {
            try {
                retry.run();
                return;
            } catch (final Throwable ex) {

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

    private boolean isAllowedRetry(int attempt, Throwable e) {
        return (failureConditions.isEmpty() || failureConditions.contains(e.getClass())) && attempt < maxAttempts;
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

    public <R> Optional<R> call(RetryCallable<R> retry) throws RetryInterruptedException {
        for (int attempt = 1; attempt <= maxAttempts; attempt++) {
            try {
                return Optional.of(retry.call());
            } catch (Throwable ex) {
                if (abortConditions.contains(ex.getClass())) {
                    break;
                }

                if (isAllowedRetry(attempt, ex)) {
                    Duration delayMillis = backoffDelay(attempt);
                    sleep(delayMillis);
                }
            }
        }
        return Optional.empty();
    }
}
