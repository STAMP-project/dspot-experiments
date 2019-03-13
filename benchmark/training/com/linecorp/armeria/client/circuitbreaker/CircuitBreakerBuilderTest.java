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


import java.time.Duration;
import org.junit.Test;


public class CircuitBreakerBuilderTest {
    private static final String remoteServiceName = "testService";

    private static final Duration minusDuration = Duration.ZERO.minusMillis(1);

    private static final Duration oneSecond = Duration.ofSeconds(1);

    private static final Duration twoSeconds = Duration.ofSeconds(2);

    @Test
    public void testConstructor() {
        assertThat(new CircuitBreakerBuilder(CircuitBreakerBuilderTest.remoteServiceName).build().name()).isEqualTo(CircuitBreakerBuilderTest.remoteServiceName);
        assertThat(new CircuitBreakerBuilder().build().name()).startsWith("circuit-breaker-");
    }

    @Test
    public void testConstructorWithInvalidArgument() {
        CircuitBreakerBuilderTest.throwsException(() -> new CircuitBreakerBuilder(null));
        CircuitBreakerBuilderTest.throwsException(() -> new CircuitBreakerBuilder(""));
    }

    @Test
    public void testFailureRateThreshold() {
        assertThat(confOf(CircuitBreakerBuilderTest.builder().failureRateThreshold(0.123).build()).failureRateThreshold()).isEqualTo(0.123);
        assertThat(confOf(CircuitBreakerBuilderTest.builder().failureRateThreshold(1).build()).failureRateThreshold()).isEqualTo(1.0);
    }

    @Test
    public void testFailureRateThresholdWithInvalidArgument() {
        CircuitBreakerBuilderTest.throwsException(() -> CircuitBreakerBuilderTest.builder().failureRateThreshold(0));
        CircuitBreakerBuilderTest.throwsException(() -> CircuitBreakerBuilderTest.builder().failureRateThreshold((-1)));
        CircuitBreakerBuilderTest.throwsException(() -> CircuitBreakerBuilderTest.builder().failureRateThreshold(1.1));
    }

    @Test
    public void testMinimumRequestThreshold() {
        final CircuitBreakerConfig config1 = confOf(CircuitBreakerBuilderTest.builder().minimumRequestThreshold(Long.MAX_VALUE).build());
        assertThat(config1.minimumRequestThreshold()).isEqualTo(Long.MAX_VALUE);
        final CircuitBreakerConfig config2 = confOf(CircuitBreakerBuilderTest.builder().minimumRequestThreshold(0).build());
        assertThat(config2.minimumRequestThreshold()).isEqualTo(0L);
    }

    @Test
    public void testMinimumRequestThresholdWithInvalidArgument() {
        CircuitBreakerBuilderTest.throwsException(() -> CircuitBreakerBuilderTest.builder().minimumRequestThreshold((-1)));
    }

    @Test
    public void testTrialRequestInterval() {
        final CircuitBreakerConfig config = confOf(CircuitBreakerBuilderTest.builder().trialRequestInterval(CircuitBreakerBuilderTest.oneSecond).build());
        assertThat(config.trialRequestInterval()).isEqualTo(CircuitBreakerBuilderTest.oneSecond);
    }

    @Test
    public void testTrialRequestIntervalInMillis() {
        final CircuitBreakerConfig config = confOf(CircuitBreakerBuilderTest.builder().trialRequestIntervalMillis(CircuitBreakerBuilderTest.oneSecond.toMillis()).build());
        assertThat(config.trialRequestInterval()).isEqualTo(CircuitBreakerBuilderTest.oneSecond);
    }

    @Test
    public void testTrialRequestIntervalWithInvalidArgument() {
        CircuitBreakerBuilderTest.throwsException(() -> CircuitBreakerBuilderTest.builder().trialRequestInterval(null));
        CircuitBreakerBuilderTest.throwsException(() -> CircuitBreakerBuilderTest.builder().trialRequestInterval(Duration.ZERO));
        CircuitBreakerBuilderTest.throwsException(() -> CircuitBreakerBuilderTest.builder().trialRequestInterval(CircuitBreakerBuilderTest.minusDuration));
        CircuitBreakerBuilderTest.throwsException(() -> CircuitBreakerBuilderTest.builder().trialRequestIntervalMillis((-1)));
        CircuitBreakerBuilderTest.throwsException(() -> CircuitBreakerBuilderTest.builder().trialRequestIntervalMillis(0));
    }

    @Test
    public void testCircuitOpenWindow() {
        final CircuitBreakerConfig config = confOf(CircuitBreakerBuilderTest.builder().circuitOpenWindow(CircuitBreakerBuilderTest.oneSecond).build());
        assertThat(config.circuitOpenWindow()).isEqualTo(CircuitBreakerBuilderTest.oneSecond);
    }

    @Test
    public void testCircuitOpenWindowInMillis() {
        final CircuitBreakerConfig config = confOf(CircuitBreakerBuilderTest.builder().circuitOpenWindowMillis(CircuitBreakerBuilderTest.oneSecond.toMillis()).build());
        assertThat(config.circuitOpenWindow()).isEqualTo(CircuitBreakerBuilderTest.oneSecond);
    }

    @Test
    public void testCircuitOpenWindowWithInvalidArgument() {
        CircuitBreakerBuilderTest.throwsException(() -> CircuitBreakerBuilderTest.builder().circuitOpenWindow(null));
        CircuitBreakerBuilderTest.throwsException(() -> CircuitBreakerBuilderTest.builder().circuitOpenWindow(Duration.ZERO));
        CircuitBreakerBuilderTest.throwsException(() -> CircuitBreakerBuilderTest.builder().circuitOpenWindow(CircuitBreakerBuilderTest.minusDuration));
        CircuitBreakerBuilderTest.throwsException(() -> CircuitBreakerBuilderTest.builder().circuitOpenWindowMillis((-1)));
        CircuitBreakerBuilderTest.throwsException(() -> CircuitBreakerBuilderTest.builder().circuitOpenWindowMillis(0));
    }

    @Test
    public void testCounterSlidingWindow() {
        final CircuitBreakerConfig config = confOf(CircuitBreakerBuilderTest.builder().counterSlidingWindow(CircuitBreakerBuilderTest.twoSeconds).build());
        assertThat(config.counterSlidingWindow()).isEqualTo(CircuitBreakerBuilderTest.twoSeconds);
    }

    @Test
    public void testCounterSlidingWindowInMillis() {
        final CircuitBreakerConfig config = confOf(CircuitBreakerBuilderTest.builder().counterSlidingWindowMillis(CircuitBreakerBuilderTest.twoSeconds.toMillis()).build());
        assertThat(config.counterSlidingWindow()).isEqualTo(CircuitBreakerBuilderTest.twoSeconds);
    }

    @Test
    public void testCounterSlidingWindowWithInvalidArgument() {
        CircuitBreakerBuilderTest.throwsException(() -> CircuitBreakerBuilderTest.builder().counterSlidingWindow(null));
        CircuitBreakerBuilderTest.throwsException(() -> CircuitBreakerBuilderTest.builder().counterSlidingWindow(Duration.ZERO));
        CircuitBreakerBuilderTest.throwsException(() -> CircuitBreakerBuilderTest.builder().counterSlidingWindow(CircuitBreakerBuilderTest.minusDuration));
        CircuitBreakerBuilderTest.throwsException(() -> CircuitBreakerBuilderTest.builder().counterSlidingWindowMillis((-1)));
        CircuitBreakerBuilderTest.throwsException(() -> CircuitBreakerBuilderTest.builder().counterSlidingWindowMillis(0));
        CircuitBreakerBuilderTest.throwsException(() -> CircuitBreakerBuilderTest.builder().counterSlidingWindow(CircuitBreakerBuilderTest.oneSecond).counterUpdateInterval(CircuitBreakerBuilderTest.twoSeconds).build());
    }

    @Test
    public void testCounterUpdateInterval() {
        final CircuitBreakerConfig config = confOf(CircuitBreakerBuilderTest.builder().counterUpdateInterval(CircuitBreakerBuilderTest.oneSecond).build());
        assertThat(config.counterUpdateInterval()).isEqualTo(CircuitBreakerBuilderTest.oneSecond);
    }

    @Test
    public void testCounterUpdateIntervalInMillis() {
        final CircuitBreakerConfig config = confOf(CircuitBreakerBuilderTest.builder().counterUpdateIntervalMillis(CircuitBreakerBuilderTest.oneSecond.toMillis()).build());
        assertThat(config.counterUpdateInterval()).isEqualTo(CircuitBreakerBuilderTest.oneSecond);
    }

    @Test
    public void testCounterUpdateIntervalWithInvalidArgument() {
        CircuitBreakerBuilderTest.throwsException(() -> CircuitBreakerBuilderTest.builder().counterUpdateInterval(null));
        CircuitBreakerBuilderTest.throwsException(() -> CircuitBreakerBuilderTest.builder().counterUpdateInterval(Duration.ZERO));
        CircuitBreakerBuilderTest.throwsException(() -> CircuitBreakerBuilderTest.builder().counterUpdateInterval(CircuitBreakerBuilderTest.minusDuration));
        CircuitBreakerBuilderTest.throwsException(() -> CircuitBreakerBuilderTest.builder().counterUpdateIntervalMillis((-1)));
        CircuitBreakerBuilderTest.throwsException(() -> CircuitBreakerBuilderTest.builder().counterUpdateIntervalMillis(0));
    }
}

