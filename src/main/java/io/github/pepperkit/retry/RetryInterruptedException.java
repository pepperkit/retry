package io.github.pepperkit.retry;

public class RetryInterruptedException extends RuntimeException {

    private static final long serialVersionUID = 5443040601324416580L;

    public RetryInterruptedException() {
        super();
    }

    public RetryInterruptedException(String message) {
        super(message);
    }

    public RetryInterruptedException(String message, Throwable cause) {
        super(message, cause);
    }

    public RetryInterruptedException(Throwable cause) {
        super(cause);
    }
}
