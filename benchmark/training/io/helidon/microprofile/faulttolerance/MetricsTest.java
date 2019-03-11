/**
 * Copyright (c) 2018, 2019 Oracle and/or its affiliates. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.helidon.microprofile.faulttolerance;


import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;
import java.util.function.Supplier;
import org.eclipse.microprofile.faulttolerance.exceptions.CircuitBreakerOpenException;
import org.eclipse.microprofile.metrics.MetricRegistry;
import org.eclipse.microprofile.metrics.MetricType;
import org.eclipse.microprofile.metrics.MetricUnits;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;


/**
 * Class MetricsTest.
 */
public class MetricsTest extends FaultToleranceTest {
    @Test
    public void testEnable() {
        MatcherAssert.assertThat(FaultToleranceMetrics.enabled(), Matchers.is(true));
    }

    @Test
    public void testInjectCounter() {
        MetricsBean bean = newBean(MetricsBean.class);
        MatcherAssert.assertThat(bean, Matchers.notNullValue());
        bean.getCounter().inc();
        MatcherAssert.assertThat(bean.getCounter().getCount(), Matchers.is(1L));
    }

    @Test
    public void testInjectCounterProgrammatically() {
        MetricRegistry metricRegistry = FaultToleranceMetrics.getMetricRegistry();
        metricRegistry.counter(new org.eclipse.microprofile.metrics.Metadata("dcounter", "", "", MetricType.COUNTER, MetricUnits.NONE));
        metricRegistry.counter("dcounter").inc();
        MatcherAssert.assertThat(metricRegistry.counter("dcounter").getCount(), Matchers.is(1L));
    }

    @Test
    public void testGlobalCountersSuccess() throws Exception {
        MetricsBean bean = newBean(MetricsBean.class);
        bean.retryOne(5);
        MatcherAssert.assertThat(FaultToleranceMetrics.getCounter(bean, "retryOne", FaultToleranceMetrics.INVOCATIONS_TOTAL, int.class), Matchers.is(1L));
        MatcherAssert.assertThat(FaultToleranceMetrics.getCounter(bean, "retryOne", FaultToleranceMetrics.INVOCATIONS_FAILED_TOTAL, int.class), Matchers.is(0L));
    }

    @Test
    public void testGlobalCountersFailure() throws Exception {
        MetricsBean bean = newBean(MetricsBean.class);
        try {
            bean.retryTwo(10);
        } catch (Exception e) {
            // falls through
        }
        MatcherAssert.assertThat(FaultToleranceMetrics.getCounter(bean, "retryTwo", FaultToleranceMetrics.INVOCATIONS_TOTAL, int.class), Matchers.is(1L));
        MatcherAssert.assertThat(FaultToleranceMetrics.getCounter(bean, "retryTwo", FaultToleranceMetrics.INVOCATIONS_FAILED_TOTAL, int.class), Matchers.is(1L));
    }

    @Test
    public void testRetryCounters() throws Exception {
        MetricsBean bean = newBean(MetricsBean.class);
        bean.retryThree(5);
        MatcherAssert.assertThat(FaultToleranceMetrics.getCounter(bean, "retryThree", FaultToleranceMetrics.RETRY_CALLS_SUCCEEDED_NOT_RETRIED_TOTAL, int.class), Matchers.is(0L));
        MatcherAssert.assertThat(FaultToleranceMetrics.getCounter(bean, "retryThree", FaultToleranceMetrics.RETRY_CALLS_SUCCEEDED_RETRIED_TOTAL, int.class), Matchers.is(1L));
        MatcherAssert.assertThat(FaultToleranceMetrics.getCounter(bean, "retryThree", FaultToleranceMetrics.RETRY_CALLS_FAILED_TOTAL, int.class), Matchers.is(0L));
        MatcherAssert.assertThat(FaultToleranceMetrics.getCounter(bean, "retryThree", FaultToleranceMetrics.RETRY_RETRIES_TOTAL, int.class), Matchers.is(5L));
    }

    @Test
    public void testRetryCountersFailure() throws Exception {
        MetricsBean bean = newBean(MetricsBean.class);
        try {
            bean.retryFour(10);
        } catch (Exception e) {
            // falls through
        }
        MatcherAssert.assertThat(FaultToleranceMetrics.getCounter(bean, "retryFour", FaultToleranceMetrics.RETRY_CALLS_SUCCEEDED_NOT_RETRIED_TOTAL, int.class), Matchers.is(0L));
        MatcherAssert.assertThat(FaultToleranceMetrics.getCounter(bean, "retryFour", FaultToleranceMetrics.RETRY_CALLS_SUCCEEDED_RETRIED_TOTAL, int.class), Matchers.is(0L));
        MatcherAssert.assertThat(FaultToleranceMetrics.getCounter(bean, "retryFour", FaultToleranceMetrics.RETRY_CALLS_FAILED_TOTAL, int.class), Matchers.is(1L));
        MatcherAssert.assertThat(FaultToleranceMetrics.getCounter(bean, "retryFour", FaultToleranceMetrics.RETRY_RETRIES_TOTAL, int.class), Matchers.is(5L));
    }

    @Test
    public void testRetryCountersSuccess() throws Exception {
        MetricsBean bean = newBean(MetricsBean.class);
        bean.retryFive(0);
        MatcherAssert.assertThat(FaultToleranceMetrics.getCounter(bean, "retryFive", FaultToleranceMetrics.RETRY_CALLS_SUCCEEDED_NOT_RETRIED_TOTAL, int.class), Matchers.is(1L));
        MatcherAssert.assertThat(FaultToleranceMetrics.getCounter(bean, "retryFive", FaultToleranceMetrics.RETRY_CALLS_SUCCEEDED_RETRIED_TOTAL, int.class), Matchers.is(0L));
        MatcherAssert.assertThat(FaultToleranceMetrics.getCounter(bean, "retryFive", FaultToleranceMetrics.RETRY_CALLS_FAILED_TOTAL, int.class), Matchers.is(0L));
        MatcherAssert.assertThat(FaultToleranceMetrics.getCounter(bean, "retryFive", FaultToleranceMetrics.RETRY_RETRIES_TOTAL, int.class), Matchers.is(0L));
    }

    @Test
    public void testTimeoutSuccess() throws Exception {
        MetricsBean bean = newBean(MetricsBean.class);
        bean.noTimeout();
        MatcherAssert.assertThat(FaultToleranceMetrics.getHistogram(bean, "noTimeout", FaultToleranceMetrics.TIMEOUT_EXECUTION_DURATION).getCount(), Matchers.is(1L));
        MatcherAssert.assertThat(FaultToleranceMetrics.getCounter(bean, "noTimeout", FaultToleranceMetrics.TIMEOUT_CALLS_NOT_TIMED_OUT_TOTAL), Matchers.is(1L));
        MatcherAssert.assertThat(FaultToleranceMetrics.getCounter(bean, "noTimeout", FaultToleranceMetrics.TIMEOUT_CALLS_TIMED_OUT_TOTAL), Matchers.is(0L));
    }

    @Test
    public void testTimeoutFailure() throws Exception {
        MetricsBean bean = newBean(MetricsBean.class);
        try {
            bean.forceTimeout();
        } catch (Exception e) {
            // falls through
        }
        MatcherAssert.assertThat(FaultToleranceMetrics.getHistogram(bean, "forceTimeout", FaultToleranceMetrics.TIMEOUT_EXECUTION_DURATION).getCount(), Matchers.is(1L));
        MatcherAssert.assertThat(FaultToleranceMetrics.getCounter(bean, "forceTimeout", FaultToleranceMetrics.TIMEOUT_CALLS_NOT_TIMED_OUT_TOTAL), Matchers.is(0L));
        MatcherAssert.assertThat(FaultToleranceMetrics.getCounter(bean, "forceTimeout", FaultToleranceMetrics.TIMEOUT_CALLS_TIMED_OUT_TOTAL), Matchers.is(1L));
    }

    @Test
    public void testBreakerTrip() throws Exception {
        MetricsBean bean = newBean(MetricsBean.class);
        for (int i = 0; i < (CircuitBreakerBean.REQUEST_VOLUME_THRESHOLD); i++) {
            Assertions.assertThrows(RuntimeException.class, () -> bean.exerciseBreaker(false));
        }
        Assertions.assertThrows(CircuitBreakerOpenException.class, () -> bean.exerciseBreaker(false));
        MatcherAssert.assertThat(FaultToleranceMetrics.getCounter(bean, "exerciseBreaker", FaultToleranceMetrics.BREAKER_OPENED_TOTAL, boolean.class), Matchers.is(1L));
        MatcherAssert.assertThat(FaultToleranceMetrics.getCounter(bean, "exerciseBreaker", FaultToleranceMetrics.BREAKER_CALLS_SUCCEEDED_TOTAL, boolean.class), Matchers.is(0L));
        MatcherAssert.assertThat(FaultToleranceMetrics.getCounter(bean, "exerciseBreaker", FaultToleranceMetrics.BREAKER_CALLS_FAILED_TOTAL, boolean.class), Matchers.is(((long) (CircuitBreakerBean.REQUEST_VOLUME_THRESHOLD))));
        MatcherAssert.assertThat(FaultToleranceMetrics.getCounter(bean, "exerciseBreaker", FaultToleranceMetrics.BREAKER_CALLS_PREVENTED_TOTAL, boolean.class), Matchers.is(1L));
    }

    @Test
    public void testBreakerGauges() throws Exception {
        MetricsBean bean = newBean(MetricsBean.class);
        for (int i = 0; i < ((CircuitBreakerBean.REQUEST_VOLUME_THRESHOLD) - 1); i++) {
            Assertions.assertThrows(RuntimeException.class, () -> bean.exerciseGauges(false));
            MatcherAssert.assertThat(FaultToleranceMetrics.getGauge(bean, "exerciseGauges", FaultToleranceMetrics.BREAKER_CLOSED_TOTAL, boolean.class).getValue(), Matchers.is(Matchers.not(0L)));
            MatcherAssert.assertThat(FaultToleranceMetrics.getGauge(bean, "exerciseGauges", FaultToleranceMetrics.BREAKER_OPEN_TOTAL, boolean.class).getValue(), Matchers.is(0L));
            MatcherAssert.assertThat(FaultToleranceMetrics.getGauge(bean, "exerciseGauges", FaultToleranceMetrics.BREAKER_HALF_OPEN_TOTAL, boolean.class).getValue(), Matchers.is(0L));
        }
        Assertions.assertThrows(RuntimeException.class, () -> bean.exerciseGauges(false));
        Assertions.assertThrows(CircuitBreakerOpenException.class, () -> bean.exerciseGauges(false));
        MatcherAssert.assertThat(FaultToleranceMetrics.getGauge(bean, "exerciseGauges", FaultToleranceMetrics.BREAKER_CLOSED_TOTAL, boolean.class).getValue(), Matchers.is(Matchers.not(0L)));
        MatcherAssert.assertThat(FaultToleranceMetrics.getGauge(bean, "exerciseGauges", FaultToleranceMetrics.BREAKER_OPEN_TOTAL, boolean.class).getValue(), Matchers.is(Matchers.not(0L)));
        MatcherAssert.assertThat(FaultToleranceMetrics.getGauge(bean, "exerciseGauges", FaultToleranceMetrics.BREAKER_HALF_OPEN_TOTAL, boolean.class).getValue(), Matchers.is(0L));
    }

    @Test
    public void testFallbackMetrics() throws Exception {
        MetricsBean bean = newBean(MetricsBean.class);
        MatcherAssert.assertThat(FaultToleranceMetrics.getCounter(bean, "fallback", FaultToleranceMetrics.FALLBACK_CALLS_TOTAL), Matchers.is(0L));
        bean.fallback();
        MatcherAssert.assertThat(FaultToleranceMetrics.getCounter(bean, "fallback", FaultToleranceMetrics.FALLBACK_CALLS_TOTAL), Matchers.is(1L));
    }

    @Test
    public void testBulkheadMetrics() throws Exception {
        MetricsBean bean = newBean(MetricsBean.class);
        Future<String>[] calls = FaultToleranceTest.getAsyncConcurrentCalls(() -> bean.concurrent(100), BulkheadBean.MAX_CONCURRENT_CALLS);
        FaultToleranceTest.getThreadNames(calls);
        MatcherAssert.assertThat(FaultToleranceMetrics.getGauge(bean, "concurrent", FaultToleranceMetrics.BULKHEAD_CONCURRENT_EXECUTIONS, long.class).getValue(), Matchers.is(0L));
        MatcherAssert.assertThat(FaultToleranceMetrics.getCounter(bean, "concurrent", FaultToleranceMetrics.BULKHEAD_CALLS_ACCEPTED_TOTAL, long.class), Matchers.is(((long) (BulkheadBean.MAX_CONCURRENT_CALLS))));
        MatcherAssert.assertThat(FaultToleranceMetrics.getCounter(bean, "concurrent", FaultToleranceMetrics.BULKHEAD_CALLS_REJECTED_TOTAL, long.class), Matchers.is(0L));
        MatcherAssert.assertThat(FaultToleranceMetrics.getHistogram(bean, "concurrent", FaultToleranceMetrics.BULKHEAD_EXECUTION_DURATION, long.class).getCount(), Matchers.is(((long) (BulkheadBean.MAX_CONCURRENT_CALLS))));
    }

    @Test
    public void testBulkheadMetricsAsync() throws Exception {
        MetricsBean bean = newBean(MetricsBean.class);
        CompletableFuture<String>[] calls = FaultToleranceTest.getConcurrentCalls(() -> {
            try {
                return bean.concurrentAsync(100).get();
            } catch (Exception e) {
                return "failure";
            }
        }, BulkheadBean.MAX_CONCURRENT_CALLS);
        CompletableFuture.allOf(calls).get();
        MatcherAssert.assertThat(FaultToleranceMetrics.getHistogram(bean, "concurrentAsync", FaultToleranceMetrics.BULKHEAD_EXECUTION_DURATION, long.class).getCount(), Matchers.is(((long) (BulkheadBean.MAX_CONCURRENT_CALLS))));
    }
}

