/*
 * Copyright (C) 2022 PepperKit
 *
 * This software may be modified and distributed under the terms
 * of the MIT license. See the LICENSE file for details.
 */
package io.github.pepperkit.retry;

public interface RunnableRetry {

    /**
     * Performs a retryable action.
     */
    void run();
}
