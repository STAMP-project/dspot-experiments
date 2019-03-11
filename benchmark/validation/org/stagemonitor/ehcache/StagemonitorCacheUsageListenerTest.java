package org.stagemonitor.ehcache;


import org.junit.Assert;
import org.junit.Test;
import org.stagemonitor.core.metrics.metrics2.Metric2Registry;
import org.stagemonitor.core.metrics.metrics2.MetricName;


public class StagemonitorCacheUsageListenerTest {
    private final Metric2Registry registry = new Metric2Registry();

    private StagemonitorCacheUsageListener cacheUsageListener = new StagemonitorCacheUsageListener("cache", registry, true);

    @Test
    public void testEvicted() throws Exception {
        cacheUsageListener.notifyCacheElementEvicted();
        final MetricName name = name("cache_delete").tag("cache_name", "cache").tag("reason", "eviction").tier("All").build();
        Assert.assertNotNull(registry.getMeters().get(name));
        Assert.assertEquals(1, registry.getMeters().get(name).getCount());
    }

    @Test
    public void testExpired() throws Exception {
        cacheUsageListener.notifyCacheElementExpired();
        final MetricName name = name("cache_delete").tag("cache_name", "cache").tag("reason", "expire").tier("All").build();
        Assert.assertNotNull(registry.getMeters().get(name));
        Assert.assertEquals(1, registry.getMeters().get(name).getCount());
    }

    @Test
    public void testRemoved() throws Exception {
        cacheUsageListener.notifyCacheElementRemoved();
        final MetricName name = name("cache_delete").tag("cache_name", "cache").tag("reason", "remove").tier("All").build();
        Assert.assertNotNull(registry.getMeters().get(name));
        Assert.assertEquals(1, registry.getMeters().get(name).getCount());
    }

    @Test
    public void testCacheHit() throws Exception {
        cacheUsageListener.notifyCacheHitInMemory();
        cacheUsageListener.notifyCacheHitOffHeap();
        cacheUsageListener.notifyCacheHitOnDisk();
        final MetricName name = name("cache_hits").tag("cache_name", "cache").tier("All").build();
        Assert.assertNotNull(registry.getMeters().get(name));
        Assert.assertEquals(3, registry.getMeters().get(name).getCount());
    }

    @Test
    public void testCacheMiss() throws Exception {
        cacheUsageListener.notifyCacheMissedWithExpired();
        cacheUsageListener.notifyCacheMissedWithNotFound();
        cacheUsageListener.notifyCacheMissInMemory();
        cacheUsageListener.notifyCacheMissOffHeap();
        cacheUsageListener.notifyCacheMissOnDisk();
        final MetricName name = name("cache_misses").tag("cache_name", "cache").tier("All").build();
        Assert.assertNotNull(registry.getMeters().get(name));
        Assert.assertEquals(5, registry.getMeters().get(name).getCount());
    }

    @Test
    public void testHitRate() throws Exception {
        cacheUsageListener.notifyCacheHitInMemory();
        cacheUsageListener.notifyCacheMissOnDisk();
        Assert.assertNotNull(cacheUsageListener.getHitRatio1Min());
    }

    @Test
    public void testGet() {
        cacheUsageListener.notifyGetTimeNanos(1);
        Assert.assertNotNull(registry.getTimers().get(name("cache_get").tag("cache_name", "cache").tier("All").build()));
    }

    @Test
    public void testGetMeter() {
        cacheUsageListener = new StagemonitorCacheUsageListener("cache", registry, false);
        cacheUsageListener.notifyGetTimeNanos(1);
        final MetricName name = name("cache_get").tag("cache_name", "cache").tier("All").build();
        Assert.assertNull(registry.getTimers().get(name));
        Assert.assertNotNull(registry.getMeters().get(name));
    }

    @Test
    public void testEmpty() {
        cacheUsageListener.notifyRemoveAll();
        cacheUsageListener.notifyStatisticsAccuracyChanged(0);
        cacheUsageListener.dispose();
        cacheUsageListener.notifyCacheSearch(0);
        cacheUsageListener.notifyXaCommit();
        cacheUsageListener.notifyXaRollback();
        cacheUsageListener.notifyStatisticsEnabledChanged(true);
        cacheUsageListener.notifyStatisticsCleared();
        cacheUsageListener.notifyCacheElementPut();
        cacheUsageListener.notifyCacheElementUpdated();
        cacheUsageListener.notifyTimeTakenForGet(0);
        Assert.assertEquals(0, registry.getMeters().size());
        Assert.assertEquals(0, registry.getCounters().size());
        Assert.assertEquals(0, registry.getTimers().size());
        Assert.assertEquals(0, registry.getGauges().size());
        Assert.assertEquals(0, registry.getHistograms().size());
    }
}

