package com.alicp.jetcache;


import com.alicp.jetcache.embedded.LinkedHashMapCacheBuilder;
import com.alicp.jetcache.test.AbstractCacheTest;
import org.junit.Test;


/**
 * Created on 2017/5/24.
 *
 * @author <a href="mailto:areyouok@gmail.com">huangli</a>
 */
public class LoadingCacheTest extends AbstractCacheTest {
    @Test
    public void test() throws Exception {
        cache = LinkedHashMapCacheBuilder.createLinkedHashMapCacheBuilder().buildCache();
        cache = new MonitoredCache(cache);
        cache = new LoadingCache(cache);
        baseTest();
        LoadingCacheTest.loadingCacheTest(cache, 0);
        errorTest();
    }
}

