/**
 * Copyright 2016 Robert Winkler
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package io.github.resilience4j.circuitbreaker;


import CircuitBreakerConfig.Builder;
import CircuitBreakerConfig.DEFAULT_MAX_FAILURE_THRESHOLD;
import CircuitBreakerConfig.DEFAULT_RING_BUFFER_SIZE_IN_CLOSED_STATE;
import CircuitBreakerConfig.DEFAULT_RING_BUFFER_SIZE_IN_HALF_OPEN_STATE;
import CircuitBreakerConfig.DEFAULT_WAIT_DURATION_IN_OPEN_STATE;
import java.time.Duration;
import java.util.function.Predicate;
import org.junit.Test;


public class CircuitBreakerConfigTest {
    private static final Predicate<Throwable> TEST_PREDICATE = ( e) -> "test".equals(e.getMessage());

    @Test(expected = IllegalArgumentException.class)
    public void zeroMaxFailuresShouldFail() {
        CircuitBreakerConfig.custom().failureRateThreshold(0).build();
    }

    @Test(expected = IllegalArgumentException.class)
    public void zeroWaitIntervalShouldFail() {
        CircuitBreakerConfig.custom().waitDurationInOpenState(Duration.ofMillis(0)).build();
    }

    @Test(expected = IllegalArgumentException.class)
    public void ringBufferSizeInHalfOpenStateBelowOneShouldFail() {
        CircuitBreakerConfig.custom().ringBufferSizeInHalfOpenState(0).build();
    }

    @Test(expected = IllegalArgumentException.class)
    public void ringBufferSizeInClosedStateBelowOneThenShouldFail() {
        CircuitBreakerConfig.custom().ringBufferSizeInClosedState(0).build();
    }

    @Test(expected = IllegalArgumentException.class)
    public void zeroFailureRateThresholdShouldFail() {
        CircuitBreakerConfig.custom().failureRateThreshold(0).build();
    }

    @Test(expected = IllegalArgumentException.class)
    public void failureRateThresholdAboveHundredShouldFail() {
        CircuitBreakerConfig.custom().failureRateThreshold(101).build();
    }

    @Test
    public void shouldSetDefaultSettings() {
        CircuitBreakerConfig circuitBreakerConfig = CircuitBreakerConfig.ofDefaults();
        then(circuitBreakerConfig.getFailureRateThreshold()).isEqualTo(DEFAULT_MAX_FAILURE_THRESHOLD);
        then(circuitBreakerConfig.getRingBufferSizeInHalfOpenState()).isEqualTo(DEFAULT_RING_BUFFER_SIZE_IN_HALF_OPEN_STATE);
        then(circuitBreakerConfig.getRingBufferSizeInClosedState()).isEqualTo(DEFAULT_RING_BUFFER_SIZE_IN_CLOSED_STATE);
        then(circuitBreakerConfig.getWaitDurationInOpenState().getSeconds()).isEqualTo(DEFAULT_WAIT_DURATION_IN_OPEN_STATE);
        then(circuitBreakerConfig.getRecordFailurePredicate()).isNotNull();
    }

    @Test
    public void shouldSetFailureRateThreshold() {
        CircuitBreakerConfig circuitBreakerConfig = CircuitBreakerConfig.custom().failureRateThreshold(25).build();
        then(circuitBreakerConfig.getFailureRateThreshold()).isEqualTo(25);
    }

    @Test
    public void shouldSetLowFailureRateThreshold() {
        CircuitBreakerConfig circuitBreakerConfig = CircuitBreakerConfig.custom().failureRateThreshold(0.001F).build();
        then(circuitBreakerConfig.getFailureRateThreshold()).isEqualTo(0.001F);
    }

    @Test
    public void shouldSetRingBufferSizeInClosedState() {
        CircuitBreakerConfig circuitBreakerConfig = CircuitBreakerConfig.custom().ringBufferSizeInClosedState(1000).build();
        then(circuitBreakerConfig.getRingBufferSizeInClosedState()).isEqualTo(1000);
    }

    @Test
    public void shouldSetRingBufferSizeInHalfOpenState() {
        CircuitBreakerConfig circuitBreakerConfig = CircuitBreakerConfig.custom().ringBufferSizeInHalfOpenState(100).build();
        then(circuitBreakerConfig.getRingBufferSizeInHalfOpenState()).isEqualTo(100);
    }

    @Test
    public void shouldSetWaitInterval() {
        CircuitBreakerConfig circuitBreakerConfig = CircuitBreakerConfig.custom().waitDurationInOpenState(Duration.ofSeconds(1)).build();
        then(circuitBreakerConfig.getWaitDurationInOpenState().getSeconds()).isEqualTo(1);
    }

    @Test
    public void shouldUseRecordFailureThrowablePredicate() {
        CircuitBreakerConfig circuitBreakerConfig = CircuitBreakerConfig.custom().recordFailure(CircuitBreakerConfigTest.TEST_PREDICATE).build();
        then(circuitBreakerConfig.getRecordFailurePredicate().test(new Error("test"))).isEqualTo(true);
        then(circuitBreakerConfig.getRecordFailurePredicate().test(new Error("fail"))).isEqualTo(false);
        then(circuitBreakerConfig.getRecordFailurePredicate().test(new RuntimeException("test"))).isEqualTo(true);
        then(circuitBreakerConfig.getRecordFailurePredicate().test(new Error())).isEqualTo(false);
        then(circuitBreakerConfig.getRecordFailurePredicate().test(new RuntimeException())).isEqualTo(false);
    }

    private static class ExtendsException extends Exception {
        ExtendsException() {
        }

        ExtendsException(String message) {
            super(message);
        }
    }

    private static class ExtendsRuntimeException extends RuntimeException {}

    private static class ExtendsExtendsException extends CircuitBreakerConfigTest.ExtendsException {}

    private static class ExtendsException2 extends Exception {}

    private static class ExtendsError extends Error {}

    @Test
    public void shouldUseIgnoreExceptionToBuildPredicate() {
        CircuitBreakerConfig circuitBreakerConfig = CircuitBreakerConfig.custom().ignoreExceptions(RuntimeException.class, CircuitBreakerConfigTest.ExtendsExtendsException.class).build();
        final Predicate<? super Throwable> failurePredicate = circuitBreakerConfig.getRecordFailurePredicate();
        then(failurePredicate.test(new Exception())).isEqualTo(true);// not explicitly excluded

        then(failurePredicate.test(new CircuitBreakerConfigTest.ExtendsError())).isEqualTo(true);// not explicitly excluded

        then(failurePredicate.test(new CircuitBreakerConfigTest.ExtendsException())).isEqualTo(true);// not explicitly excluded

        then(failurePredicate.test(new CircuitBreakerConfigTest.ExtendsException2())).isEqualTo(true);// not explicitly excluded

        then(failurePredicate.test(new RuntimeException())).isEqualTo(false);// explicitly excluded

        then(failurePredicate.test(new CircuitBreakerConfigTest.ExtendsRuntimeException())).isEqualTo(false);// inherits excluded from ExtendsException

        then(failurePredicate.test(new CircuitBreakerConfigTest.ExtendsExtendsException())).isEqualTo(false);// explicitly excluded

    }

    @Test
    public void shouldUseRecordExceptionToBuildPredicate() {
        CircuitBreakerConfig circuitBreakerConfig = CircuitBreakerConfig.custom().recordExceptions(RuntimeException.class, CircuitBreakerConfigTest.ExtendsExtendsException.class).build();
        final Predicate<? super Throwable> failurePredicate = circuitBreakerConfig.getRecordFailurePredicate();
        then(failurePredicate.test(new Exception())).isEqualTo(false);// not explicitly included

        then(failurePredicate.test(new CircuitBreakerConfigTest.ExtendsError())).isEqualTo(false);// not explicitly included

        then(failurePredicate.test(new CircuitBreakerConfigTest.ExtendsException())).isEqualTo(false);// not explicitly included

        then(failurePredicate.test(new CircuitBreakerConfigTest.ExtendsException2())).isEqualTo(false);// not explicitly included

        then(failurePredicate.test(new RuntimeException())).isEqualTo(true);// explicitly included

        then(failurePredicate.test(new CircuitBreakerConfigTest.ExtendsRuntimeException())).isEqualTo(true);// inherits included from ExtendsException

        then(failurePredicate.test(new CircuitBreakerConfigTest.ExtendsExtendsException())).isEqualTo(true);// explicitly included

    }

    @Test
    public void shouldUseIgnoreExceptionOverRecordToBuildPredicate() {
        CircuitBreakerConfig circuitBreakerConfig = CircuitBreakerConfig.custom().recordExceptions(RuntimeException.class, CircuitBreakerConfigTest.ExtendsExtendsException.class).ignoreExceptions(CircuitBreakerConfigTest.ExtendsException.class, CircuitBreakerConfigTest.ExtendsRuntimeException.class).build();
        final Predicate<? super Throwable> failurePredicate = circuitBreakerConfig.getRecordFailurePredicate();
        then(failurePredicate.test(new Exception())).isEqualTo(false);// not explicitly included

        then(failurePredicate.test(new CircuitBreakerConfigTest.ExtendsError())).isEqualTo(false);// not explicitly included

        then(failurePredicate.test(new CircuitBreakerConfigTest.ExtendsException())).isEqualTo(false);// explicitly excluded

        then(failurePredicate.test(new CircuitBreakerConfigTest.ExtendsException2())).isEqualTo(false);// not explicitly included

        then(failurePredicate.test(new RuntimeException())).isEqualTo(true);// explicitly included

        then(failurePredicate.test(new CircuitBreakerConfigTest.ExtendsRuntimeException())).isEqualTo(false);// explicitly excluded

        then(failurePredicate.test(new CircuitBreakerConfigTest.ExtendsExtendsException())).isEqualTo(false);// inherits excluded from ExtendsException

    }

    @Test
    public void shouldUseBothRecordToBuildPredicate() {
        CircuitBreakerConfig circuitBreakerConfig = // 3
        // 2
        // 1
        CircuitBreakerConfig.custom().recordFailure(CircuitBreakerConfigTest.TEST_PREDICATE).recordExceptions(RuntimeException.class, CircuitBreakerConfigTest.ExtendsExtendsException.class).ignoreExceptions(CircuitBreakerConfigTest.ExtendsException.class, CircuitBreakerConfigTest.ExtendsRuntimeException.class).build();
        final Predicate<? super Throwable> failurePredicate = circuitBreakerConfig.getRecordFailurePredicate();
        then(failurePredicate.test(new Exception())).isEqualTo(false);// not explicitly included

        then(failurePredicate.test(new Exception("test"))).isEqualTo(true);// explicitly included by 1

        then(failurePredicate.test(new CircuitBreakerConfigTest.ExtendsError())).isEqualTo(false);// ot explicitly included

        then(failurePredicate.test(new CircuitBreakerConfigTest.ExtendsException())).isEqualTo(false);// explicitly excluded by 3

        then(failurePredicate.test(new CircuitBreakerConfigTest.ExtendsException("test"))).isEqualTo(false);// explicitly excluded by 3 even if included by 1

        then(failurePredicate.test(new CircuitBreakerConfigTest.ExtendsException2())).isEqualTo(false);// not explicitly included

        then(failurePredicate.test(new RuntimeException())).isEqualTo(true);// explicitly included by 2

        then(failurePredicate.test(new CircuitBreakerConfigTest.ExtendsRuntimeException())).isEqualTo(false);// explicitly excluded by 3

        then(failurePredicate.test(new CircuitBreakerConfigTest.ExtendsExtendsException())).isEqualTo(false);// inherits excluded from ExtendsException by 3

    }

    @Test
    public void builderMakePredicateShouldBuildPredicateAcceptingChildClass() {
        final Predicate<Throwable> predicate = Builder.makePredicate(RuntimeException.class);
        then(predicate.test(new RuntimeException())).isEqualTo(true);
        then(predicate.test(new Exception())).isEqualTo(false);
        then(predicate.test(new Throwable())).isEqualTo(false);
        then(predicate.test(new IllegalArgumentException())).isEqualTo(true);
        then(predicate.test(new RuntimeException() {})).isEqualTo(true);
        then(predicate.test(new Exception() {})).isEqualTo(false);
    }

    @Test
    public void shouldBuilderCreateConfigEveryTime() {
        final CircuitBreakerConfig.Builder builder = CircuitBreakerConfig.custom();
        builder.ringBufferSizeInClosedState(5);
        final CircuitBreakerConfig config1 = builder.build();
        builder.ringBufferSizeInClosedState(3);
        final CircuitBreakerConfig config2 = builder.build();
        assertThat(config2.getRingBufferSizeInClosedState()).isEqualTo(3);
        assertThat(config1.getRingBufferSizeInClosedState()).isEqualTo(5);
    }
}

