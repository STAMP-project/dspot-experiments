package io.github.resilience4j.metrics;


import CircuitBreaker.State.CLOSED;
import CircuitBreaker.State.HALF_OPEN;
import CircuitBreaker.State.OPEN;
import com.codahale.metrics.Gauge;
import com.codahale.metrics.MetricRegistry;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import java.util.SortedMap;
import java.util.concurrent.TimeUnit;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public class StateTransitionMetricsTest {
    private MetricRegistry metricRegistry = new MetricRegistry();

    private CircuitBreaker circuitBreaker;

    @Test
    public void circuitBreakerMetricsUsesFirstStateObjectInstance() throws Exception {
        SortedMap<String, Gauge> gauges = metricRegistry.getGauges();
        Assert.assertThat(circuitBreaker.getState(), CoreMatchers.equalTo(CLOSED));
        Assert.assertThat(circuitBreaker.getMetrics().getNumberOfBufferedCalls(), CoreMatchers.equalTo(0));
        Assert.assertThat(circuitBreaker.getMetrics().getNumberOfFailedCalls(), CoreMatchers.equalTo(0));
        Assert.assertThat(circuitBreaker.getMetrics().getNumberOfSuccessfulCalls(), CoreMatchers.equalTo(0));
        Assert.assertThat(gauges.get("resilience4j.circuitbreaker.test.state").getValue(), CoreMatchers.equalTo(0));
        Assert.assertThat(gauges.get("resilience4j.circuitbreaker.test.buffered").getValue(), CoreMatchers.equalTo(0));
        Assert.assertThat(gauges.get("resilience4j.circuitbreaker.test.failed").getValue(), CoreMatchers.equalTo(0));
        Assert.assertThat(gauges.get("resilience4j.circuitbreaker.test.successful").getValue(), CoreMatchers.equalTo(0));
        circuitBreaker.onError(0, new RuntimeException());
        Assert.assertThat(circuitBreaker.getState(), CoreMatchers.equalTo(CLOSED));
        Assert.assertThat(circuitBreaker.getMetrics().getNumberOfBufferedCalls(), CoreMatchers.equalTo(1));
        Assert.assertThat(circuitBreaker.getMetrics().getNumberOfFailedCalls(), CoreMatchers.equalTo(1));
        Assert.assertThat(circuitBreaker.getMetrics().getNumberOfSuccessfulCalls(), CoreMatchers.equalTo(0));
        Assert.assertThat(gauges.get("resilience4j.circuitbreaker.test.state").getValue(), CoreMatchers.equalTo(0));
        Assert.assertThat(gauges.get("resilience4j.circuitbreaker.test.buffered").getValue(), CoreMatchers.equalTo(1));
        Assert.assertThat(gauges.get("resilience4j.circuitbreaker.test.failed").getValue(), CoreMatchers.equalTo(1));
        Assert.assertThat(gauges.get("resilience4j.circuitbreaker.test.successful").getValue(), CoreMatchers.equalTo(0));
        for (int i = 0; i < 9; i++) {
            circuitBreaker.onError(0, new RuntimeException());
        }
        Assert.assertThat(circuitBreaker.getState(), CoreMatchers.equalTo(OPEN));
        Assert.assertThat(circuitBreaker.getMetrics().getNumberOfBufferedCalls(), CoreMatchers.equalTo(10));
        Assert.assertThat(circuitBreaker.getMetrics().getNumberOfFailedCalls(), CoreMatchers.equalTo(10));
        Assert.assertThat(circuitBreaker.getMetrics().getNumberOfSuccessfulCalls(), CoreMatchers.equalTo(0));
        Assert.assertThat(gauges.get("resilience4j.circuitbreaker.test.state").getValue(), CoreMatchers.equalTo(1));
        Assert.assertThat(gauges.get("resilience4j.circuitbreaker.test.buffered").getValue(), CoreMatchers.equalTo(10));
        Assert.assertThat(gauges.get("resilience4j.circuitbreaker.test.failed").getValue(), CoreMatchers.equalTo(10));
        Assert.assertThat(gauges.get("resilience4j.circuitbreaker.test.successful").getValue(), CoreMatchers.equalTo(0));
        await().atMost(1500, TimeUnit.MILLISECONDS).until(() -> {
            circuitBreaker.isCallPermitted();
            return circuitBreaker.getState().equals(CircuitBreaker.State.HALF_OPEN);
        });
        circuitBreaker.onSuccess(0);
        Assert.assertThat(circuitBreaker.getState(), CoreMatchers.equalTo(HALF_OPEN));
        Assert.assertThat(circuitBreaker.getMetrics().getNumberOfBufferedCalls(), CoreMatchers.equalTo(1));
        Assert.assertThat(circuitBreaker.getMetrics().getNumberOfFailedCalls(), CoreMatchers.equalTo(0));
        Assert.assertThat(circuitBreaker.getMetrics().getNumberOfSuccessfulCalls(), CoreMatchers.equalTo(1));
        Assert.assertThat(gauges.get("resilience4j.circuitbreaker.test.state").getValue(), CoreMatchers.equalTo(2));
        Assert.assertThat(gauges.get("resilience4j.circuitbreaker.test.buffered").getValue(), CoreMatchers.equalTo(1));
        Assert.assertThat(gauges.get("resilience4j.circuitbreaker.test.failed").getValue(), CoreMatchers.equalTo(0));
        Assert.assertThat(gauges.get("resilience4j.circuitbreaker.test.successful").getValue(), CoreMatchers.equalTo(1));
        circuitBreaker.onSuccess(0);
        Assert.assertThat(circuitBreaker.getState(), CoreMatchers.equalTo(HALF_OPEN));
        Assert.assertThat(circuitBreaker.getMetrics().getNumberOfBufferedCalls(), CoreMatchers.equalTo(2));
        Assert.assertThat(circuitBreaker.getMetrics().getNumberOfFailedCalls(), CoreMatchers.equalTo(0));
        Assert.assertThat(circuitBreaker.getMetrics().getNumberOfSuccessfulCalls(), CoreMatchers.equalTo(2));
        Assert.assertThat(gauges.get("resilience4j.circuitbreaker.test.state").getValue(), CoreMatchers.equalTo(2));
        Assert.assertThat(gauges.get("resilience4j.circuitbreaker.test.buffered").getValue(), CoreMatchers.equalTo(2));
        Assert.assertThat(gauges.get("resilience4j.circuitbreaker.test.failed").getValue(), CoreMatchers.equalTo(0));
        Assert.assertThat(gauges.get("resilience4j.circuitbreaker.test.successful").getValue(), CoreMatchers.equalTo(2));
        circuitBreaker.onSuccess(0);
        Assert.assertThat(circuitBreaker.getState(), CoreMatchers.equalTo(CLOSED));
        Assert.assertThat(circuitBreaker.getMetrics().getNumberOfBufferedCalls(), CoreMatchers.equalTo(3));
        Assert.assertThat(circuitBreaker.getMetrics().getNumberOfFailedCalls(), CoreMatchers.equalTo(0));
        Assert.assertThat(circuitBreaker.getMetrics().getNumberOfSuccessfulCalls(), CoreMatchers.equalTo(3));
        Assert.assertThat(gauges.get("resilience4j.circuitbreaker.test.state").getValue(), CoreMatchers.equalTo(0));
        Assert.assertThat(gauges.get("resilience4j.circuitbreaker.test.buffered").getValue(), CoreMatchers.equalTo(3));
        Assert.assertThat(gauges.get("resilience4j.circuitbreaker.test.failed").getValue(), CoreMatchers.equalTo(0));
        Assert.assertThat(gauges.get("resilience4j.circuitbreaker.test.successful").getValue(), CoreMatchers.equalTo(3));
    }
}

