# retry

![Build Status](https://github.com/aukhatov/retry/workflows/Java%20CI%20with%20Gradle/badge.svg?branch=master)
[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=aukhatov_retry&metric=alert_status)](https://sonarcloud.io/dashboard?id=aukhatov_retry)

This is a simple and lightweight retry library for Java. It helps you transparently retry failed operations.

### Predefined Functions

- Exponential
- Fixed
- Randomized

## Usage

### Methods Definition

- `retry()` - initiate the retry function
- `backoff(BackoffFunction function)` - a function to compute next delay interval
- `delay(Duration duration)` - an initial delay interval
- `maxDelay(Duration duration)` - the maximum delay interval value
- `handle(Class<? extends Throwable>)` - specifies a type of Exception which has to be handled to retry (if it doesn't specify any exception will be handled by default)
- `abortIf(Class<? extends Throwable>)` - when the exception has occurred the retryable function will be interrupted
- `onFailure(Consumer<? super Throwable>)` - specifies a function to handle the exception
- `run()` - perform an action (function) which can be retried
- `call()` - compute a result which can be retried

### Exponential Backoff

The **exponential** backoff algorithm implementation.
It uses to **gradually reduce the rate** of the operation if the exception _(or any)_ occurred.

```java
import static io.github.pepperkit.retry.Retry.retry;

    retry(3)
        .backoff(new BackoffFunction.Exponential())
        .delay(Duration.ofMillis(500))
        .maxDelay(Duration.ofSeconds(3))
        .handle(ConnectionException.class)
        .run(()->{
            // do someting retryable
            // it can throw the ConnectionException
        });
```

### Fixed Backoff

This is a **fixed** backoff algorithm implementation.
There **each attempt of the operation** will be retried by **the same timeout** if the exception _(or any)_ occurred.

```java
import static io.github.pepperkit.retry.Retry.retry;

    retry(3)
        .backoff(new BackoffFunction.Fixed())
        .delay(Duration.ofMillis(500))
        .handle(ConnectionException.class)
        .run(()->{
            // do someting retryable
            // it can throw the ConnectionException
        });
```

### Randomized Backoff

This is a **randomized** backoff algorithm implementation.

```java
import static io.github.pepperkit.retry.Retry.retry;

    retry(3)
        .backoff(new BackoffFunction.Randomized(5))
        .delay(Duration.ofMillis(500))
        .maxDelay(Duration.ofSeconds(3))
        .handle(ConnectionException.class)
        .run(()->{
            // do someting retryable
            // it can throw the ConnectionException
        });
```
