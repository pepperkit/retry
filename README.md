# retry

![Build Status](https://github.com/aukhatov/retry/workflows/Java%20CI%20with%20Gradle/badge.svg?branch=master)
[![Coverage](https://sonarcloud.io/api/project_badges/measure?project=aukhatov_retry&metric=coverage)](https://sonarcloud.io/dashboard?id=aukhatov_retry)

This is a simple and lightweight retry library for Java. It helps you transparently retry failed operations.

### Backoff Functions
The backoff function determines how much to wait between the retries.

#### Exponential
It waits progressively longer intervals between subsequent retries.
```text
3s -> 9s -> 27s -> 81s
```
```java
import io.github.pepperkit.retry.BackoffFunction;

new BackoffFunction.Exponential(3);
```

#### Fixed
This is an elementary implementation, just return a constant value.
```text
3s -> 3s -> 3s -> 3s
```
```java
import io.github.pepperkit.retry.BackoffFunction;

new BackoffFunction.Fixed();
```

#### Randomized
This function returns a random interval value. The algorithm doesn't declare how much the rate of an operation will be reduced. 
```text
12s -> 4s -> 3s -> 9s
```
```java
import io.github.pepperkit.retry.BackoffFunction;

new BackoffFunction.Randomized(5);
```

#### Custom
You can implement your own version of backoff function by the interface.
```java
@FunctionalInterface
public interface BackoffFunction {

    Duration delay(int attempt, Duration delay);
}
```

## Usage

### Methods Definition

- `retry()` - initiates the retry function
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
