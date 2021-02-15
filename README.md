# retry

This is a simple and lightweight retry library for Java.

## Code Examples

### Exponential Backoff Function

The **exponential** backoff algorithm implementation.
It uses to **gradually reduce the rate** of the operation if the exception _(or any)_ occurred.

```java
import static art.aukhatov.retry.Retry.retry;

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

### Fixed Backoff Function

This is **fixed** backoff algorithm implementation.
There **each attempt of the operation** will be retried by **the same timeout** if the exception _(or any)_ occurred.

```java
import static art.aukhatov.retry.Retry.retry;

    retry(3)
        .backoff(new BackoffFunction.Fixed())
        .delay(Duration.ofMillis(500))
        .handle(ConnectionException.class)
        .run(()->{
            // do someting retryable
            // it can throw the ConnectionException
        });
```

### Randomized Backoff Function

This is **randomized** backoff algorithm implementation.

```java
import static art.aukhatov.retry.Retry.retry;

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
