package io.github.resilience4j.timelimiter.internal;


import io.github.resilience4j.timelimiter.TimeLimiterConfig;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;


@RunWith(PowerMockRunner.class)
@PrepareForTest(TimeLimiterImpl.class)
public class TimeLimiterImplTest {
    private TimeLimiterConfig timeLimiterConfig;

    private TimeLimiterImpl timeout;

    @Test
    public void configPropagation() {
        then(timeout.getTimeLimiterConfig()).isEqualTo(timeLimiterConfig);
    }
}

