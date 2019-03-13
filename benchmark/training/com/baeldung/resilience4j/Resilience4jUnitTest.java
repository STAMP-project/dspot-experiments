package com.baeldung.resilience4j;


import io.github.resilience4j.bulkhead.Bulkhead;
import io.github.resilience4j.bulkhead.BulkheadConfig;
import io.github.resilience4j.bulkhead.BulkheadRegistry;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.github.resilience4j.retry.Retry;
import io.github.resilience4j.retry.RetryConfig;
import io.github.resilience4j.retry.RetryRegistry;
import io.github.resilience4j.timelimiter.TimeLimiter;
import io.github.resilience4j.timelimiter.TimeLimiterConfig;
import java.time.Duration;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


public class Resilience4jUnitTest {
    interface RemoteService {
        int process(int i);
    }

    private Resilience4jUnitTest.RemoteService service;

    @Test
    public void whenCircuitBreakerIsUsed_thenItWorksAsExpected() {
        CircuitBreakerConfig config = // Min number of call attempts
        // Percentage of failures to start short-circuit
        CircuitBreakerConfig.custom().failureRateThreshold(20).ringBufferSizeInClosedState(5).build();
        CircuitBreakerRegistry registry = CircuitBreakerRegistry.of(config);
        CircuitBreaker circuitBreaker = registry.circuitBreaker("my");
        Function<Integer, Integer> decorated = CircuitBreaker.decorateFunction(circuitBreaker, service::process);
        Mockito.when(service.process(ArgumentMatchers.anyInt())).thenThrow(new RuntimeException());
        for (int i = 0; i < 10; i++) {
            try {
                decorated.apply(i);
            } catch (Exception ignore) {
            }
        }
        Mockito.verify(service, Mockito.times(5)).process(ArgumentMatchers.any(Integer.class));
    }

    @Test
    public void whenBulkheadIsUsed_thenItWorksAsExpected() throws InterruptedException {
        BulkheadConfig config = BulkheadConfig.custom().maxConcurrentCalls(1).build();
        BulkheadRegistry registry = BulkheadRegistry.of(config);
        Bulkhead bulkhead = registry.bulkhead("my");
        Function<Integer, Integer> decorated = Bulkhead.decorateFunction(bulkhead, service::process);
        Future<?> taskInProgress = callAndBlock(decorated);
        try {
            assertThat(bulkhead.isCallPermitted()).isFalse();
        } finally {
            taskInProgress.cancel(true);
        }
    }

    @Test
    public void whenRetryIsUsed_thenItWorksAsExpected() {
        RetryConfig config = RetryConfig.custom().maxAttempts(2).build();
        RetryRegistry registry = RetryRegistry.of(config);
        Retry retry = registry.retry("my");
        Function<Integer, Void> decorated = Retry.decorateFunction(retry, (Integer s) -> {
            service.process(s);
            return null;
        });
        Mockito.when(service.process(ArgumentMatchers.anyInt())).thenThrow(new RuntimeException());
        try {
            decorated.apply(1);
            fail("Expected an exception to be thrown if all retries failed");
        } catch (Exception e) {
            Mockito.verify(service, Mockito.times(2)).process(ArgumentMatchers.any(Integer.class));
        }
    }

    @SuppressWarnings("unchecked")
    @Test
    public void whenTimeLimiterIsUsed_thenItWorksAsExpected() throws Exception {
        long ttl = 1;
        TimeLimiterConfig config = TimeLimiterConfig.custom().timeoutDuration(Duration.ofMillis(ttl)).build();
        TimeLimiter timeLimiter = TimeLimiter.of(config);
        Future futureMock = Mockito.mock(Future.class);
        Callable restrictedCall = TimeLimiter.decorateFutureSupplier(timeLimiter, () -> futureMock);
        restrictedCall.call();
        Mockito.verify(futureMock).get(ttl, TimeUnit.MILLISECONDS);
    }
}

