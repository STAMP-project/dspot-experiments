package com.alicp.jetcache;


import FastjsonKeyConvertor.INSTANCE;
import com.alicp.jetcache.embedded.CaffeineCacheBuilder;
import com.alicp.jetcache.support.DefaultCacheMonitorTest;
import com.alicp.jetcache.test.AbstractCacheTest;
import java.util.LinkedHashMap;
import java.util.concurrent.TimeUnit;
import org.junit.Assert;
import org.junit.Test;


/**
 * Created on 2016/10/8.
 *
 * @author <a href="mailto:areyouok@gmail.com">huangli</a>
 */
public class MultiLevelCacheTest extends AbstractCacheTest {
    private Cache<Object, Object> l1Cache;

    private Cache<Object, Object> l2Cache;

    private static final int LIMIT = 1000;

    @Test
    public void testConstructor() {
        try {
            cache = MultiLevelCacheBuilder.createMultiLevelCacheBuilder().buildCache();
            Assert.fail();
        } catch (IllegalArgumentException e) {
        }
        try {
            new MultiLevelCache(new Cache[0]);
            Assert.fail();
        } catch (IllegalArgumentException e) {
        }
        initL1L2(100);
        l1Cache = CaffeineCacheBuilder.createCaffeineCacheBuilder().limit(10).expireAfterWrite(1000, TimeUnit.MILLISECONDS).keyConvertor(INSTANCE).loader(( key) -> null).buildCache();
        try {
            cache = MultiLevelCacheBuilder.createMultiLevelCacheBuilder().addCache(l1Cache, l2Cache).buildCache();
            Assert.fail();
        } catch (CacheConfigException e) {
        }
    }

    @Test
    public void testUnwrap() {
        initL1L2(100);
        cache = MultiLevelCacheBuilder.createMultiLevelCacheBuilder().addCache(l1Cache, l2Cache).buildCache();
        Assert.assertTrue(((cache.unwrap(LinkedHashMap.class)) instanceof LinkedHashMap));
        Assert.assertTrue(((cache.unwrap(com.github.benmanes.caffeine.cache.Cache.class)) instanceof com.github.benmanes.caffeine.cache.Cache));
    }

    @Test
    public void test() throws Exception {
        initL1L2(200);
        cache = new MultiLevelCache(l1Cache, l2Cache);
        doTest(200);
        expireAfterWriteTest(200);
        DefaultCacheMonitorTest.testMonitor(cache);
        initL1L2(2000);
        cache = MultiLevelCacheBuilder.createMultiLevelCacheBuilder().addCache(l1Cache, l2Cache).buildCache();
        concurrentTest(200, MultiLevelCacheTest.LIMIT, 3000);
        initL1L2(200);
        LoadingCacheTest.loadingCacheTest(MultiLevelCacheBuilder.createMultiLevelCacheBuilder().addCache(l1Cache, l2Cache), 0);
        initL1L2(200);
        RefreshCacheTest.refreshCacheTest(MultiLevelCacheBuilder.createMultiLevelCacheBuilder().addCache(l1Cache, l2Cache), 80, 40);
        doMonitoredTest(200, true, () -> {
            try {
                doTest(200);
            } catch (Exception e) {
                e.printStackTrace();
                Assert.fail();
            }
        });
        doMonitoredTest(2000, false, () -> {
            try {
                concurrentTest(200, MultiLevelCacheTest.LIMIT, 3000);
            } catch (Exception e) {
                e.printStackTrace();
                Assert.fail();
            }
        });
    }
}

