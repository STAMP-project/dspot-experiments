package com.vip.vjtools.vjkit.concurrent.limiter;


import com.vip.vjtools.vjkit.concurrent.Concurrents;
import java.util.concurrent.TimeUnit;
import org.junit.Test;


public class TimeIntervalLimiterTest {
    @Test
    public void testTryAcquire() throws Exception {
        int interval = 100;
        TimeUnit timeUnit = TimeUnit.MILLISECONDS;
        TimeIntervalLimiter limiter = Concurrents.timeIntervalLimiter(interval, timeUnit);
        assertThat(limiter.tryAcquire()).isTrue();
        assertThat(limiter.tryAcquire()).isFalse();
        timeUnit.sleep(interval);
        assertThat(limiter.tryAcquire()).isTrue();
    }
}

