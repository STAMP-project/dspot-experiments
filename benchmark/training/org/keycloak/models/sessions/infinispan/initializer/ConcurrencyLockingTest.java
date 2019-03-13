package org.keycloak.models.sessions.infinispan.initializer;


import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.infinispan.Cache;
import org.infinispan.manager.DefaultCacheManager;
import org.junit.Ignore;
import org.junit.Test;


/**
 *
 *
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
@Ignore
public class ConcurrencyLockingTest {
    @Test
    public void testLocking() throws Exception {
        final DefaultCacheManager cacheManager = getVersionedCacheManager();
        Cache<String, String> cache = cacheManager.getCache("COUNTER_CACHE");
        Map<String, String> map = new HashMap<>();
        map.put("key1", "val1");
        map.put("key2", "val2");
        cache.putAll(map);
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(new Runnable() {
            @Override
            public void run() {
                Cache<String, String> cache = cacheManager.getCache("COUNTER_CACHE");
                cache.startBatch();
                System.out.println("thread lock");
                cache.getAdvancedCache().lock("key");
                try {
                    Thread.sleep(100000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                cache.endBatch(true);
            }
        });
        Thread.sleep(10);
        cache.startBatch();
        cache.getAdvancedCache().lock("key");
        cache.put("key", "1234");
        System.out.println("after put");
        cache.endBatch(true);
        Thread.sleep(1000000);
    }
}

