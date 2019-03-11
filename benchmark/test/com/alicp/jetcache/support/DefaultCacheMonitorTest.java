package com.alicp.jetcache.support;


import com.alicp.jetcache.Cache;
import java.util.concurrent.TimeUnit;
import org.junit.Test;


/**
 * Created on 2016/11/1.
 *
 * @author <a href="mailto:areyouok@gmail.com">huangli</a>
 */
public class DefaultCacheMonitorTest {
    @Test
    public void testWithoutLogger() {
        Cache cache = createCache();
        DefaultCacheMonitorTest.testMonitor(cache);
    }

    @Test
    public void testWithManager() throws Exception {
        Cache c1 = createCache();
        DefaultCacheMonitor m1 = new DefaultCacheMonitor("cache1");
        Cache c2 = createCache();
        DefaultCacheMonitor m2 = new DefaultCacheMonitor("cache2");
        c1 = new com.alicp.jetcache.MonitoredCache(c1, m1);
        c2 = new com.alicp.jetcache.MonitoredCache(c2, m2);
        DefaultCacheMonitorManager manager = new DefaultCacheMonitorManager(10, TimeUnit.SECONDS, true);
        manager.start();
        manager.add(m1, m2);
        DefaultCacheMonitorTest.basetest(c1, m1);
        DefaultCacheMonitorTest.basetest(c2, m2);
        Cache mc = new com.alicp.jetcache.MultiLevelCache(c1, c2);
        DefaultCacheMonitor mcm = new DefaultCacheMonitor("multiCache");
        mc = new com.alicp.jetcache.MonitoredCache(mc, mcm);
        manager.add(mcm);
        DefaultCacheMonitorTest.basetest(mc, mcm);
        manager.cmd.run();
        manager.stop();
        // Thread.sleep(10000);
    }
}

