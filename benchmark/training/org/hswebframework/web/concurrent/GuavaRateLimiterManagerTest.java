package org.hswebframework.web.concurrent;


import java.util.concurrent.TimeUnit;
import lombok.SneakyThrows;
import org.junit.Test;


/**
 *
 *
 * @author zhouhao
 * @since 3.0.4
 */
public class GuavaRateLimiterManagerTest {
    GuavaRateLimiterManager manager = new GuavaRateLimiterManager();

    @SneakyThrows
    @Test
    public void testRateLimiter() {
        RateLimiter limiter = manager.getRateLimiter("test", 1, TimeUnit.SECONDS);
        for (int i = 0; i < 10; i++) {
            if (!(limiter.tryAcquire(10, TimeUnit.SECONDS))) {
                throw new UnsupportedOperationException();
            }
            System.out.println(((i + ":") + (System.currentTimeMillis())));
        }
    }
}

