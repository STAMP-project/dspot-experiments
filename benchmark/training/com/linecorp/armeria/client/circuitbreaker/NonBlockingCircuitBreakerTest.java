/**
 * Copyright 2016 LINE Corporation
 *
 * LINE Corporation licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *   https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package com.linecorp.armeria.client.circuitbreaker;


import CircuitState.CLOSED;
import CircuitState.HALF_OPEN;
import CircuitState.OPEN;
import EventCount.ZERO;
import com.google.common.testing.FakeTicker;
import java.time.Duration;
import org.junit.Test;
import org.mockito.Mockito;


public class NonBlockingCircuitBreakerTest {
    private static final String remoteServiceName = "testService";

    private static final FakeTicker ticker = new FakeTicker();

    private static final Duration circuitOpenWindow = Duration.ofSeconds(1);

    private static final Duration trialRequestInterval = Duration.ofSeconds(1);

    private static final Duration counterUpdateInterval = Duration.ofSeconds(1);

    private static final CircuitBreakerListener listener = Mockito.mock(CircuitBreakerListener.class);

    @Test
    public void testClosed() {
        NonBlockingCircuitBreakerTest.closedState(2, 0.5);
    }

    @Test
    public void testMinimumRequestThreshold() {
        final NonBlockingCircuitBreaker cb = NonBlockingCircuitBreakerTest.create(4, 0.5);
        assertThat(((cb.state().isClosed()) && (cb.canRequest()))).isTrue();
        cb.onFailure();
        NonBlockingCircuitBreakerTest.ticker.advance(NonBlockingCircuitBreakerTest.counterUpdateInterval.toNanos());
        cb.onFailure();
        assertThat(cb.state().isClosed()).isTrue();
        assertThat(cb.canRequest()).isTrue();
        cb.onFailure();
        cb.onFailure();
        NonBlockingCircuitBreakerTest.ticker.advance(NonBlockingCircuitBreakerTest.counterUpdateInterval.toNanos());
        cb.onFailure();
        assertThat(cb.state().isOpen()).isTrue();
        assertThat(cb.canRequest()).isFalse();
    }

    @Test
    public void testFailureRateThreshold() {
        final NonBlockingCircuitBreaker cb = NonBlockingCircuitBreakerTest.create(10, 0.5);
        for (int i = 0; i < 10; i++) {
            cb.onSuccess();
        }
        for (int i = 0; i < 9; i++) {
            cb.onFailure();
        }
        NonBlockingCircuitBreakerTest.ticker.advance(NonBlockingCircuitBreakerTest.counterUpdateInterval.toNanos());
        cb.onFailure();
        assertThat(cb.state().isClosed()).isTrue();// 10 vs 9 (0.47)

        assertThat(cb.canRequest()).isTrue();
        NonBlockingCircuitBreakerTest.ticker.advance(NonBlockingCircuitBreakerTest.counterUpdateInterval.toNanos());
        cb.onFailure();
        assertThat(cb.state().isClosed()).isTrue();// 10 vs 10 (0.5)

        assertThat(cb.canRequest()).isTrue();
        NonBlockingCircuitBreakerTest.ticker.advance(NonBlockingCircuitBreakerTest.counterUpdateInterval.toNanos());
        cb.onFailure();
        assertThat(cb.state().isOpen()).isTrue();// 10 vs 11 (0.52)

        assertThat(cb.canRequest()).isFalse();
    }

    @Test
    public void testClosedToOpen() {
        NonBlockingCircuitBreakerTest.openState(2, 0.5);
    }

    @Test
    public void testOpenToHalfOpen() {
        NonBlockingCircuitBreakerTest.halfOpenState(2, 0.5);
    }

    @Test
    public void testHalfOpenToClosed() {
        final NonBlockingCircuitBreaker cb = NonBlockingCircuitBreakerTest.halfOpenState(2, 0.5);
        cb.onSuccess();
        assertThat(cb.state().isClosed()).isTrue();
        assertThat(cb.canRequest()).isTrue();
    }

    @Test
    public void testHalfOpenToOpen() {
        final NonBlockingCircuitBreaker cb = NonBlockingCircuitBreakerTest.halfOpenState(2, 0.5);
        cb.onFailure();
        assertThat(cb.state().isOpen()).isTrue();
        assertThat(cb.canRequest()).isFalse();
    }

    @Test
    public void testHalfOpenRetryRequest() {
        final NonBlockingCircuitBreaker cb = NonBlockingCircuitBreakerTest.halfOpenState(2, 0.5);
        NonBlockingCircuitBreakerTest.ticker.advance(NonBlockingCircuitBreakerTest.trialRequestInterval.toNanos());
        assertThat(cb.state().isHalfOpen()).isTrue();
        assertThat(cb.canRequest()).isTrue();// first request is allowed

        assertThat(cb.state().isHalfOpen()).isTrue();
        assertThat(cb.canRequest()).isFalse();// seconds request is refused

    }

    @Test
    public void testNotification() throws Exception {
        Mockito.reset(NonBlockingCircuitBreakerTest.listener);
        final NonBlockingCircuitBreaker cb = NonBlockingCircuitBreakerTest.create(4, 0.5);
        final String name = cb.name();
        // Notify initial state
        Mockito.verify(NonBlockingCircuitBreakerTest.listener, Mockito.times(1)).onEventCountUpdated(name, ZERO);
        Mockito.verify(NonBlockingCircuitBreakerTest.listener, Mockito.times(1)).onStateChanged(name, CLOSED);
        Mockito.reset(NonBlockingCircuitBreakerTest.listener);
        cb.onFailure();
        NonBlockingCircuitBreakerTest.ticker.advance(NonBlockingCircuitBreakerTest.counterUpdateInterval.toNanos());
        cb.onFailure();
        // Notify updated event count
        Mockito.verify(NonBlockingCircuitBreakerTest.listener, Mockito.times(1)).onEventCountUpdated(name, new EventCount(0, 1));
        Mockito.reset(NonBlockingCircuitBreakerTest.listener);
        // Notify circuit tripped
        cb.onFailure();
        cb.onFailure();
        NonBlockingCircuitBreakerTest.ticker.advance(NonBlockingCircuitBreakerTest.counterUpdateInterval.toNanos());
        cb.onFailure();
        Mockito.verify(NonBlockingCircuitBreakerTest.listener, Mockito.times(1)).onEventCountUpdated(name, ZERO);
        Mockito.verify(NonBlockingCircuitBreakerTest.listener, Mockito.times(1)).onStateChanged(name, OPEN);
        Mockito.reset(NonBlockingCircuitBreakerTest.listener);
        // Notify request rejected
        cb.canRequest();
        Mockito.verify(NonBlockingCircuitBreakerTest.listener, Mockito.times(1)).onRequestRejected(name);
        NonBlockingCircuitBreakerTest.ticker.advance(NonBlockingCircuitBreakerTest.circuitOpenWindow.toNanos());
        // Notify half open
        cb.canRequest();
        Mockito.verify(NonBlockingCircuitBreakerTest.listener, Mockito.times(1)).onEventCountUpdated(name, ZERO);
        Mockito.verify(NonBlockingCircuitBreakerTest.listener, Mockito.times(1)).onStateChanged(name, HALF_OPEN);
        Mockito.reset(NonBlockingCircuitBreakerTest.listener);
        // Notify circuit closed
        cb.onSuccess();
        Mockito.verify(NonBlockingCircuitBreakerTest.listener, Mockito.times(1)).onEventCountUpdated(name, ZERO);
        Mockito.verify(NonBlockingCircuitBreakerTest.listener, Mockito.times(1)).onStateChanged(name, CLOSED);
    }
}

