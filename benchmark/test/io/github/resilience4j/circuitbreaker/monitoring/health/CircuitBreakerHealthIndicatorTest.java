package io.github.resilience4j.circuitbreaker.monitoring.health;


import CircuitBreaker.Metrics;
import Status.DOWN;
import Status.UNKNOWN;
import Status.UP;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.boot.actuate.health.Health;


/**
 *
 *
 * @author bstorozhuk
 */
public class CircuitBreakerHealthIndicatorTest {
    @Test
    public void health() throws Exception {
        // given
        CircuitBreakerConfig config = Mockito.mock(CircuitBreakerConfig.class);
        CircuitBreaker.Metrics metrics = Mockito.mock(Metrics.class);
        CircuitBreaker circuitBreaker = Mockito.mock(CircuitBreaker.class);
        CircuitBreakerHealthIndicator healthIndicator = new CircuitBreakerHealthIndicator(circuitBreaker);
        // when
        Mockito.when(config.getFailureRateThreshold()).thenReturn(0.3F);
        Mockito.when(metrics.getFailureRate()).thenReturn(0.2F);
        Mockito.when(metrics.getMaxNumberOfBufferedCalls()).thenReturn(100);
        Mockito.when(metrics.getNumberOfBufferedCalls()).thenReturn(100);
        Mockito.when(metrics.getNumberOfFailedCalls()).thenReturn(20);
        Mockito.when(metrics.getNumberOfNotPermittedCalls()).thenReturn(0L);
        Mockito.when(circuitBreaker.getCircuitBreakerConfig()).thenReturn(config);
        Mockito.when(circuitBreaker.getMetrics()).thenReturn(metrics);
        Mockito.when(circuitBreaker.getState()).thenReturn(State.CLOSED, State.OPEN, State.HALF_OPEN, State.CLOSED);
        // then
        Health health = healthIndicator.health();
        then(health.getStatus()).isEqualTo(UP);
        health = healthIndicator.health();
        then(health.getStatus()).isEqualTo(DOWN);
        health = healthIndicator.health();
        then(health.getStatus()).isEqualTo(UNKNOWN);
        health = healthIndicator.health();
        then(health.getStatus()).isEqualTo(UP);
        then(health.getDetails()).contains(entry("failureRate", "0.2%"), entry("failureRateThreshold", "0.3%"), entry("bufferedCalls", 100), entry("failedCalls", 20), entry("notPermittedCalls", 0L), entry("maxBufferedCalls", 100));
    }
}

