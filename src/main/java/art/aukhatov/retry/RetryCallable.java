package art.aukhatov.retry;

public interface RetryCallable<V> {

    V call();
}
