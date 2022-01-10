/*
 * Copyright (C) 2022 PepperKit
 *
 * This software may be modified and distributed under the terms
 * of the MIT license. See the LICENSE file for details.
 */
package io.github.pepperkit.retry;

/**
 * Exception, which occurs if retry attempt was interrupted.
 */
public class RetryInterruptedException extends RuntimeException {

    private static final long serialVersionUID = 5443040601324416580L;

    public RetryInterruptedException(Throwable cause) {
        super(cause);
    }
}
