/*
 * Copyright (C) 2022 PepperKit
 *
 * This software may be modified and distributed under the terms
 * of the MIT license. See the LICENSE file for details.
 */
package io.github.pepperkit.retry;

import java.security.SecureRandom;
import java.time.Duration;

@FunctionalInterface
public interface BackoffFunction {

    Duration delay(int attempt, Duration delay);

    class Fixed implements BackoffFunction {

        public Fixed() {
            // default implementation
        }

        @Override
        public Duration delay(int attempt, Duration delay) {
            return delay;
        }
    }

    class Exponential implements BackoffFunction {

        private static final int DEFAULT_FACTOR = 3;
        private final int factor;

        public Exponential(int factor) {
            if (factor < 1) {
                throw new IllegalArgumentException("Factor must be greater than 1");
            }

            this.factor = factor;
        }

        public Exponential() {
            this.factor = DEFAULT_FACTOR;
        }

        @Override
        public Duration delay(int attempt, Duration delay) {
            double rate = Math.pow(factor, attempt);
            int millis = (int) (rate * delay.toMillis());
            return Duration.ofMillis(millis);
        }
    }

    class Randomized implements BackoffFunction {

        private final int bound;
        private final SecureRandom generator;

        public Randomized(int bound) {
            if (bound < 1) {
                throw new IllegalArgumentException("Factor must be greater than 1");
            }

            this.bound = bound;
            this.generator = new SecureRandom();
        }

        @Override
        public Duration delay(int attempt, Duration delay) {
            int r1 = generator.nextInt(bound) + 1;
            int r2 = generator.nextInt(bound + r1) + 1;

            long millis = (delay.toMillis() * attempt * r1) / r2;
            return Duration.ofMillis(millis);
        }
    }
}
