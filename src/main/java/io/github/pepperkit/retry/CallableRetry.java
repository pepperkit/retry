package io.github.pepperkit.retry;

public interface CallableRetry<V> {

    V call();
}
