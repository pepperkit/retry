package io.github.pepperkit.retry;

public class RetryInterruptedException extends RuntimeException {

    private static final long serialVersionUID = 5443040601324416580L;

    public RetryInterruptedException(Throwable cause) {
        super(cause);
    }
}
