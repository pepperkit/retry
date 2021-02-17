package art.aukhatov.retry;

public interface CallableRetry<V> {

    V call();
}
