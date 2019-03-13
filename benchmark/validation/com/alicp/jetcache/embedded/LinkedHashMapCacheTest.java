/**
 * Created on  13-09-24 10:20
 */
package com.alicp.jetcache.embedded;


import CacheResultCode.EXPIRED;
import CacheResultCode.NOT_EXISTS;
import java.util.concurrent.TimeUnit;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author <a href="mailto:areyouok@gmail.com">huangli</a>
 */
public class LinkedHashMapCacheTest extends AbstractEmbeddedCacheTest {
    @Test
    public void test() throws Exception {
        super.test(100, true);
    }

    @Test
    public void cleanTest() throws Exception {
        cache = EmbeddedCacheBuilder.createEmbeddedCacheBuilder().buildFunc(getBuildFunc()).expireAfterWrite(2000, TimeUnit.MILLISECONDS).limit(3).buildCache();
        cache.put("K1", "V1", 1, TimeUnit.MILLISECONDS);
        Thread.sleep(1);
        Assert.assertEquals(EXPIRED, cache.GET("K1").getResultCode());
        cleanExpiredEntry();
        Assert.assertEquals(NOT_EXISTS, cache.GET("K1").getResultCode());
    }
}

