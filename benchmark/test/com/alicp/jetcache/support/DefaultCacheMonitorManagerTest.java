package com.alicp.jetcache.support;


import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;
import org.junit.Assert;
import org.junit.Test;


/**
 * Created on 2016/11/3.
 *
 * @author <a href="mailto:areyouok@gmail.com">huangli</a>
 */
public class DefaultCacheMonitorManagerTest {
    @Test
    public void testFirstResetTime() {
        LocalDateTime t = LocalDateTime.of(2016, 11, 11, 23, 50, 33, 123243242);
        LocalDateTime rt = DefaultCacheMonitorManager.computeFirstResetTime(t, 1, TimeUnit.SECONDS);
        Assert.assertEquals(t.withSecond(34).withNano(0), rt);
        rt = DefaultCacheMonitorManager.computeFirstResetTime(t, 13, TimeUnit.SECONDS);
        Assert.assertEquals(t.withSecond(34).withNano(0), rt);
        rt = DefaultCacheMonitorManager.computeFirstResetTime(t, 30, TimeUnit.SECONDS);
        Assert.assertEquals(t.withMinute(51).withSecond(0).withNano(0), rt);
        rt = DefaultCacheMonitorManager.computeFirstResetTime(t, 1, TimeUnit.MINUTES);
        Assert.assertEquals(t.withMinute(51).withSecond(0).withNano(0), rt);
        rt = DefaultCacheMonitorManager.computeFirstResetTime(t, 7, TimeUnit.MINUTES);
        Assert.assertEquals(t.withMinute(51).withSecond(0).withNano(0), rt);
        rt = DefaultCacheMonitorManager.computeFirstResetTime(t, 5, TimeUnit.MINUTES);
        Assert.assertEquals(t.withMinute(55).withSecond(0).withNano(0), rt);
        rt = DefaultCacheMonitorManager.computeFirstResetTime(t, 15, TimeUnit.MINUTES);
        Assert.assertEquals(t.withDayOfMonth(12).withHour(0).withMinute(0).withSecond(0).withNano(0), rt);
        rt = DefaultCacheMonitorManager.computeFirstResetTime(t, 1, TimeUnit.HOURS);
        Assert.assertEquals(t.withDayOfMonth(12).withHour(0).withMinute(0).withSecond(0).withNano(0), rt);
        rt = DefaultCacheMonitorManager.computeFirstResetTime(t, 5, TimeUnit.HOURS);
        Assert.assertEquals(t.withDayOfMonth(12).withHour(0).withMinute(0).withSecond(0).withNano(0), rt);
        rt = DefaultCacheMonitorManager.computeFirstResetTime(t, 6, TimeUnit.HOURS);
        Assert.assertEquals(t.withDayOfMonth(12).withHour(0).withMinute(0).withSecond(0).withNano(0), rt);
        rt = DefaultCacheMonitorManager.computeFirstResetTime(t, 1, TimeUnit.DAYS);
        Assert.assertEquals(t.withDayOfMonth(12).withHour(0).withMinute(0).withSecond(0).withNano(0), rt);
        rt = DefaultCacheMonitorManager.computeFirstResetTime(t, 2, TimeUnit.DAYS);
        Assert.assertEquals(t.withDayOfMonth(12).withHour(0).withMinute(0).withSecond(0).withNano(0), rt);
        try {
            DefaultCacheMonitorManager.computeFirstResetTime(t, 1, TimeUnit.MILLISECONDS);
            Assert.fail();
        } catch (Exception e) {
        }
    }
}

