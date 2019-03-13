package com.alicp.jetcache;


import FastjsonKeyConvertor.INSTANCE;
import com.alicp.jetcache.embedded.LinkedHashMapCacheBuilder;
import com.alicp.jetcache.test.AbstractCacheTest;
import com.alicp.jetcache.test.MockRemoteCacheBuilder;
import java.sql.SQLException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.Assert;
import org.junit.Test;


/**
 * Created on 2017/5/31.
 *
 * @author <a href="mailto:areyouok@gmail.com">huangli</a>
 */
public class RefreshCacheTest extends AbstractCacheTest {
    @Test
    public void test() throws Exception {
        cache = LinkedHashMapCacheBuilder.createLinkedHashMapCacheBuilder().buildCache();
        cache = new MonitoredCache(cache);
        cache = new RefreshCache(cache);
        baseTest();
        cache.put("K1", "V1");
        cache.config().setLoader(( k) -> {
            throw new SQLException();
        });
        cache.config().setRefreshPolicy(RefreshPolicy.newPolicy(50, TimeUnit.MILLISECONDS));
        Assert.assertEquals("V1", cache.get("K1"));
        Thread.sleep(75);
        Assert.assertEquals("V1", cache.get("K1"));
        ((RefreshCache<Object, Object>) (cache)).stopRefresh();
        RefreshCacheTest.refreshCacheTest(cache, 200, 100);
    }

    @Test
    public void multiLevelCacheRefreshTest() throws Exception {
        long refresh = 500;
        long expire = 1000000;
        // use the same remote cache for multilevel caches to be tested,
        // so we can change the data in remote cache of multilevel cache directly in test purpose.
        Cache remote = new MockRemoteCacheBuilder().limit(10).expireAfterWrite(expire, TimeUnit.MILLISECONDS).keyConvertor(INSTANCE).buildCache();
        // build two multilevel cache to test
        MultiLevelCacheBuilder builder1 = MultiLevelCacheBuilder.createMultiLevelCacheBuilder().addCache(buildLocalCache(expire), remote);
        MultiLevelCacheBuilder builder2 = MultiLevelCacheBuilder.createMultiLevelCacheBuilder().addCache(buildLocalCache(expire), remote);
        // set refresh policy
        RefreshPolicy policy = RefreshPolicy.newPolicy(refresh, TimeUnit.MILLISECONDS);
        policy.setRefreshLockTimeoutMillis(10000);
        builder1.refreshPolicy(policy);
        builder2.refreshPolicy(policy);
        // loader for refresh
        AtomicInteger count = new AtomicInteger(0);
        AtomicLong blockMills = new AtomicLong(0);// block mills for loader

        CacheLoader loader = ( key) -> {
            if ((blockMills.get()) != 0) {
                Thread.sleep(blockMills.get());
            }
            return (key + "_V") + (count.getAndIncrement());
        };
        builder1.loader(loader);
        builder2.loader(loader);
        Cache cache1 = builder1.buildCache();
        Cache cache2 = builder2.buildCache();
        multiLevelCacheRefreshTest(cache1, cache2, remote, refresh, blockMills);
        cache1.close();
        cache2.close();
    }
}

