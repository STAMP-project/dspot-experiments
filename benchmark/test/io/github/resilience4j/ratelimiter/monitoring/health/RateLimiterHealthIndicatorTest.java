package io.github.resilience4j.ratelimiter.monitoring.health;


import AtomicRateLimiter.AtomicRateLimiterMetrics;
import Status.DOWN;
import Status.UNKNOWN;
import Status.UP;
import io.github.resilience4j.ratelimiter.RateLimiterConfig;
import io.github.resilience4j.ratelimiter.internal.AtomicRateLimiter;
import java.time.Duration;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.boot.actuate.health.Health;


/**
 *
 *
 * @author bstorozhuk
 */
public class RateLimiterHealthIndicatorTest {
    @Test
    public void health() throws Exception {
        // given
        RateLimiterConfig config = Mockito.mock(RateLimiterConfig.class);
        AtomicRateLimiter.AtomicRateLimiterMetrics metrics = Mockito.mock(AtomicRateLimiterMetrics.class);
        AtomicRateLimiter rateLimiter = Mockito.mock(AtomicRateLimiter.class);
        // when
        Mockito.when(rateLimiter.getRateLimiterConfig()).thenReturn(config);
        Mockito.when(rateLimiter.getMetrics()).thenReturn(metrics);
        Mockito.when(rateLimiter.getDetailedMetrics()).thenReturn(metrics);
        Mockito.when(config.getTimeoutDuration()).thenReturn(Duration.ofNanos(30L));
        Mockito.when(metrics.getAvailablePermissions()).thenReturn(5, (-1), (-2));
        Mockito.when(metrics.getNumberOfWaitingThreads()).thenReturn(0, 1, 2);
        Mockito.when(metrics.getNanosToWait()).thenReturn(20L, 40L);
        // then
        RateLimiterHealthIndicator healthIndicator = new RateLimiterHealthIndicator(rateLimiter);
        Health health = healthIndicator.health();
        then(health.getStatus()).isEqualTo(UP);
        health = healthIndicator.health();
        then(health.getStatus()).isEqualTo(UNKNOWN);
        health = healthIndicator.health();
        then(health.getStatus()).isEqualTo(DOWN);
        then(health.getDetails()).contains(entry("availablePermissions", (-2)), entry("numberOfWaitingThreads", 2));
    }
}

