package com.alicp.jetcache.embedded;


import CacheResultCode.EXPIRED;
import CacheResultCode.NOT_EXISTS;
import Cleaner.linkedHashMapCaches;
import com.alicp.jetcache.Cache;
import java.util.concurrent.TimeUnit;
import org.junit.Assert;
import org.junit.Test;


/**
 * Created on 2017/3/1.
 *
 * @author <a href="mailto:areyouok@gmail.com">huangli</a>
 */
public class CleanerTest {
    @Test
    public void test() throws Exception {
        linkedHashMapCaches.clear();
        Cache c1 = LinkedHashMapCacheBuilder.createLinkedHashMapCacheBuilder().expireAfterWrite(2000, TimeUnit.MILLISECONDS).limit(3).buildCache();
        Cache c2 = LinkedHashMapCacheBuilder.createLinkedHashMapCacheBuilder().expireAfterWrite(2000, TimeUnit.MILLISECONDS).limit(3).buildCache();
        c1.put("K1", "V1", 1, TimeUnit.MILLISECONDS);
        c2.put("K1", "V1", 1, TimeUnit.MILLISECONDS);
        Thread.sleep(1);
        Assert.assertEquals(EXPIRED, c1.GET("K1").getResultCode());
        Assert.assertEquals(EXPIRED, c1.GET("K1").getResultCode());
        Cleaner.run();
        Assert.assertEquals(NOT_EXISTS, c1.GET("K1").getResultCode());
        Assert.assertEquals(NOT_EXISTS, c1.GET("K1").getResultCode());
        Assert.assertEquals(2, linkedHashMapCaches.size());
        c1 = null;
        System.gc();
        Cleaner.run();
        Assert.assertEquals(1, linkedHashMapCaches.size());
    }
}

