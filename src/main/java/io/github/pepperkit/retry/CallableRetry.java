/*
 * Copyright (C) 2022 PepperKit
 *
 * This software may be modified and distributed under the terms
 * of the MIT license. See the LICENSE file for details.
 */
package io.github.pepperkit.retry;

/**
 * Callable version of a retry action.
 * @param <V> the type of returned value
 */
public interface CallableRetry<V> {

    /**
     * Performs a retryable action and returns a result.
     *
     * @return a result of the operation
     */
    V call();
}
