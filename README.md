# retry

This is simple retry library

## Code Examples

### Exponential Backoff Function

```java
import static art.aukhatov.retry.Retry.retry;

    retry(3)
        .backoff(new BackoffFunction.Exponential())
        .delay(Duration.ofMillis(500))
        .maxDelay(Duration.ofSeconds(3))
        .handle(ConnectionException.class)
        .run(()->{
            // do someting retryable
        });
```
