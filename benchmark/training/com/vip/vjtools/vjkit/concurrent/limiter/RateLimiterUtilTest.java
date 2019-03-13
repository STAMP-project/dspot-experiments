package com.vip.vjtools.vjkit.concurrent.limiter;


import com.google.common.util.concurrent.RateLimiter;
import java.lang.reflect.Field;
import org.junit.Assert;
import org.junit.Test;


public class RateLimiterUtilTest {
    @Test
    public void testCreate() throws Exception {
        RateLimiter rateLimiter = RateLimiterUtil.create(20000, 0.1);
        Class superClass = rateLimiter.getClass().getSuperclass();
        Field field = superClass.getDeclaredField("storedPermits");
        field.setAccessible(true);
        Assert.assertEquals(2000, ((int) (field.getDouble(rateLimiter))));
    }
}

