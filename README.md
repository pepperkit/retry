# retry

This is a simple and lightweight retry library for Java.

## Motivation
I answer one of the most popular questions: **Why?!**

The short answer is - I'm a programmer and I can!

To be serious I had to solve an issue on my work project. Where some actions must be retried if an error occurred according to specific responses.
First, to solve this issue I looked up open-source libraries that may help me. I found two popular libs: resilience4j, Failsafe.
But they do more than retry and I must prove that these libraries are reliable, safe, etc.

## Code Examples

### Exponential Backoff

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

### Fixed Backoff

This is a **fixed** backoff algorithm implementation.
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

### Randomized Backoff

This is a **randomized** backoff algorithm implementation.

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
