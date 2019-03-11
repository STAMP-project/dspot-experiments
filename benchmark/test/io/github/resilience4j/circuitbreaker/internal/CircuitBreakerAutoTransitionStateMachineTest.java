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
package io.github.resilience4j.circuitbreaker.internal;


import CircuitBreaker.State.CLOSED;
import CircuitBreaker.State.HALF_OPEN;
import CircuitBreaker.State.OPEN;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;
import org.junit.Test;


public class CircuitBreakerAutoTransitionStateMachineTest {
    private final List<CircuitBreaker> circuitBreakersGroupA = new ArrayList<>();

    private final List<CircuitBreaker> circuitBreakersGroupB = new ArrayList<>();

    private final Map<Integer, Integer> stateTransitionFromOpenToHalfOpen = new HashMap<>();

    private static final int TOTAL_NUMBER_CIRCUIT_BREAKERS = 10;

    @Test
    public void testAutoTransition() throws InterruptedException {
        // A ring buffer with size 5 is used in closed state
        // Initially the CircuitBreakers are closed
        this.assertAllGroupACircuitBreakers(CircuitBreaker::isCallPermitted, true);
        this.assertAllGroupACircuitBreakers(CircuitBreaker::getState, CLOSED);
        assertThatAllGroupAMetricsAreReset();
        // Call 1 is a failure
        circuitBreakersGroupA.forEach(( cb) -> cb.onError(0, new RuntimeException()));// Should create a CircuitBreakerOnErrorEvent (1)

        this.assertAllGroupACircuitBreakers(CircuitBreaker::isCallPermitted, true);
        this.assertAllGroupACircuitBreakers(CircuitBreaker::getState, CLOSED);
        this.assertAllGroupAMetricsEqualTo((-1.0F), null, 1, null, 1, 0L);
        // Call 2 is a failure
        circuitBreakersGroupA.forEach(( cb) -> cb.onError(0, new RuntimeException()));// Should create a CircuitBreakerOnErrorEvent (2)

        this.assertAllGroupACircuitBreakers(CircuitBreaker::isCallPermitted, true);
        this.assertAllGroupACircuitBreakers(CircuitBreaker::getState, CLOSED);
        this.assertAllGroupAMetricsEqualTo((-1.0F), null, 2, null, 2, 0L);
        // Call 3 is a failure
        circuitBreakersGroupA.forEach(( cb) -> cb.onError(0, new RuntimeException()));// Should create a CircuitBreakerOnErrorEvent (3)

        this.assertAllGroupACircuitBreakers(CircuitBreaker::isCallPermitted, true);
        this.assertAllGroupACircuitBreakers(CircuitBreaker::getState, CLOSED);
        this.assertAllGroupAMetricsEqualTo((-1.0F), null, 3, null, 3, 0L);
        // Call 4 is a success
        circuitBreakersGroupA.forEach(( cb) -> cb.onSuccess(0));// Should create a CircuitBreakerOnSuccessEvent (4)

        this.assertAllGroupACircuitBreakers(CircuitBreaker::isCallPermitted, true);
        this.assertAllGroupACircuitBreakers(CircuitBreaker::getState, CLOSED);
        this.assertAllGroupAMetricsEqualTo((-1.0F), null, 4, null, 3, 0L);
        // Call 5 is a success
        circuitBreakersGroupA.forEach(( cb) -> cb.onSuccess(0));// Should create a CircuitBreakerOnSuccessEvent (4)

        // The ring buffer is filled and the failure rate is above 50%
        this.assertAllGroupACircuitBreakers(CircuitBreaker::getState, OPEN);// Should create a CircuitBreakerOnStateTransitionEvent (6)

        this.assertAllGroupACircuitBreakers((CircuitBreaker cb) -> cb.getMetrics().getNumberOfBufferedCalls(), 5);
        this.assertAllGroupACircuitBreakers((CircuitBreaker cb) -> cb.getMetrics().getNumberOfFailedCalls(), 3);
        this.assertAllGroupACircuitBreakers((CircuitBreaker cb) -> cb.getMetrics().getFailureRate(), 60.0F);
        this.assertAllGroupAMetricsEqualTo(60.0F, null, 5, null, 3, 0L);
        Thread.sleep(50);
        // Initially the CircuitBreakers are closed
        this.assertAllGroupBCircuitBreakers(CircuitBreaker::isCallPermitted, true);
        this.assertAllGroupBCircuitBreakers(CircuitBreaker::getState, CLOSED);
        assertThatAllGroupBMetricsAreReset();
        // Call 1 is a failure
        circuitBreakersGroupB.forEach(( cb) -> cb.onError(0, new RuntimeException()));// Should create a CircuitBreakerOnErrorEvent (1)

        this.assertAllGroupBCircuitBreakers(CircuitBreaker::isCallPermitted, true);
        this.assertAllGroupBCircuitBreakers(CircuitBreaker::getState, CLOSED);
        this.assertAllGroupBMetricsEqualTo((-1.0F), null, 1, null, 1, 0L);
        // Call 2 is a failure
        circuitBreakersGroupB.forEach(( cb) -> cb.onError(0, new RuntimeException()));// Should create a CircuitBreakerOnErrorEvent (2)

        this.assertAllGroupBCircuitBreakers(CircuitBreaker::isCallPermitted, true);
        this.assertAllGroupBCircuitBreakers(CircuitBreaker::getState, CLOSED);
        this.assertAllGroupBMetricsEqualTo((-1.0F), null, 2, null, 2, 0L);
        // Call 3 is a failure
        circuitBreakersGroupB.forEach(( cb) -> cb.onError(0, new RuntimeException()));// Should create a CircuitBreakerOnErrorEvent (3)

        this.assertAllGroupBCircuitBreakers(CircuitBreaker::isCallPermitted, true);
        this.assertAllGroupBCircuitBreakers(CircuitBreaker::getState, CLOSED);
        this.assertAllGroupBMetricsEqualTo((-1.0F), null, 3, null, 3, 0L);
        // Call 4 is a success
        circuitBreakersGroupB.forEach(( cb) -> cb.onSuccess(0));// Should create a CircuitBreakerOnSuccessEvent (4)

        this.assertAllGroupBCircuitBreakers(CircuitBreaker::isCallPermitted, true);
        this.assertAllGroupBCircuitBreakers(CircuitBreaker::getState, CLOSED);
        this.assertAllGroupBMetricsEqualTo((-1.0F), null, 4, null, 3, 0L);
        // Call 5 is a success
        circuitBreakersGroupB.forEach(( cb) -> cb.onSuccess(0));// Should create a CircuitBreakerOnSuccessEvent (4)

        // The ring buffer is filled and the failure rate is above 50%
        this.assertAllGroupBCircuitBreakers(CircuitBreaker::getState, OPEN);// Should create a CircuitBreakerOnStateTransitionEvent (6)

        this.assertAllGroupBCircuitBreakers((CircuitBreaker cb) -> cb.getMetrics().getNumberOfBufferedCalls(), 5);
        this.assertAllGroupBCircuitBreakers((CircuitBreaker cb) -> cb.getMetrics().getNumberOfFailedCalls(), 3);
        this.assertAllGroupBCircuitBreakers((CircuitBreaker cb) -> cb.getMetrics().getFailureRate(), 60.0F);
        this.assertAllGroupBMetricsEqualTo(60.0F, null, 5, null, 3, 0L);
        Thread.sleep(400);
        // The CircuitBreakers in group A are still open, because the wait duration of 2 seconds is not elapsed
        this.assertAllGroupACircuitBreakers(CircuitBreaker::getState, OPEN);
        this.assertAllGroupACircuitBreakers(CircuitBreaker::isCallPermitted, false);// Should create a CircuitBreakerOnCallNotPermittedEvent (7)

        this.assertAllGroupACircuitBreakers(CircuitBreaker::isCallPermitted, false);// Should create a CircuitBreakerOnCallNotPermittedEvent (8)

        // Two calls are tried, but not permitted, because the CircuitBreakers are open
        this.assertAllGroupACircuitBreakers((CircuitBreaker cb) -> cb.getMetrics().getNumberOfNotPermittedCalls(), 2L);
        // The CircuitBreakers in group B are still open, because the wait duration of 1 second is not elapsed
        this.assertAllGroupBCircuitBreakers(CircuitBreaker::getState, OPEN);
        this.assertAllGroupBCircuitBreakers(CircuitBreaker::isCallPermitted, false);// Should create a CircuitBreakerOnCallNotPermittedEvent (7)

        this.assertAllGroupBCircuitBreakers(CircuitBreaker::isCallPermitted, false);// Should create a CircuitBreakerOnCallNotPermittedEvent (8)

        // Two calls are tried, but not permitted, because the CircuitBreakers are open
        this.assertAllGroupBCircuitBreakers((CircuitBreaker cb) -> cb.getMetrics().getNumberOfNotPermittedCalls(), 2L);
        Thread.sleep(650);
        // The CircuitBreakers in group A are still open, because the wait duration of 2 seconds is not elapsed
        this.assertAllGroupACircuitBreakers(CircuitBreaker::getState, OPEN);
        this.assertAllGroupACircuitBreakers(CircuitBreaker::isCallPermitted, false);// Should create a CircuitBreakerOnCallNotPermittedEvent (9)

        this.assertAllGroupACircuitBreakers(CircuitBreaker::isCallPermitted, false);// Should create a CircuitBreakerOnCallNotPermittedEvent (10)

        // Two calls are tried, but not permitted, because the CircuitBreakers are open
        this.assertAllGroupACircuitBreakers((CircuitBreaker cb) -> cb.getMetrics().getNumberOfNotPermittedCalls(), 4L);
        // The CircuitBreakers in Group B switch to half open, because the wait duration of 1 second is elapsed
        this.assertAllGroupBCircuitBreakers(CircuitBreaker::getState, HALF_OPEN);
        assertThat(stateTransitionFromOpenToHalfOpen.values().stream().filter(( count) -> count.equals(1)).count()).isEqualTo((((long) (CircuitBreakerAutoTransitionStateMachineTest.TOTAL_NUMBER_CIRCUIT_BREAKERS)) / 2));
        // Metrics are reset
        this.assertAllGroupBCircuitBreakers((CircuitBreaker cb) -> cb.getMetrics().getNumberOfFailedCalls(), 0);
        this.assertAllGroupBMetricsEqualTo((-1.0F), null, 0, 3, 0, 0L);
        Thread.sleep(1000);
        // The CircuitBreakers switch to half open, because the wait duration of 2 second is elapsed
        this.assertAllGroupACircuitBreakers(CircuitBreaker::getState, HALF_OPEN);
        assertThat(stateTransitionFromOpenToHalfOpen.values().stream().allMatch(( count) -> count.equals(1))).isEqualTo(true);
        // Metrics are reset
        this.assertAllGroupACircuitBreakers((CircuitBreaker cb) -> cb.getMetrics().getNumberOfFailedCalls(), 0);
        this.assertAllGroupAMetricsEqualTo((-1.0F), null, 0, 3, 0, 0L);
    }
}

