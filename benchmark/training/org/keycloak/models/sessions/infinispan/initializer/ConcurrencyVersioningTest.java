package org.keycloak.models.sessions.infinispan.initializer;


import InfinispanConnectionProvider.REALM_CACHE_NAME;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.infinispan.Cache;
import org.infinispan.manager.DefaultCacheManager;
import org.infinispan.manager.EmbeddedCacheManager;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;


/**
 * Unit tests to make sure our model caching concurrency model will work.
 *
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
@Ignore
public class ConcurrencyVersioningTest {
    public abstract static class AbstractThread implements Runnable {
        EmbeddedCacheManager cacheManager;

        boolean success;

        CountDownLatch latch = new CountDownLatch(1);

        public AbstractThread(EmbeddedCacheManager cacheManager) {
            this.cacheManager = cacheManager;
        }

        public boolean isSuccess() {
            return success;
        }

        public CountDownLatch getLatch() {
            return latch;
        }
    }

    public static class RemoveThread extends ConcurrencyVersioningTest.AbstractThread {
        public RemoveThread(EmbeddedCacheManager cacheManager) {
            super(cacheManager);
        }

        public void run() {
            Cache<String, String> cache = cacheManager.getCache(REALM_CACHE_NAME);
            try {
                ConcurrencyVersioningTest.startBatch(cache);
                cache.remove("key");
                // cache.getAdvancedCache().getTransactionManager().commit();
                ConcurrencyVersioningTest.endBatch(cache);
                success = true;
            } catch (Exception e) {
                success = false;
            }
            latch.countDown();
        }
    }

    public static class UpdateThread extends ConcurrencyVersioningTest.AbstractThread {
        public UpdateThread(EmbeddedCacheManager cacheManager) {
            super(cacheManager);
        }

        public void run() {
            Cache<String, String> cache = cacheManager.getCache(REALM_CACHE_NAME);
            try {
                ConcurrencyVersioningTest.startBatch(cache);
                cache.putForExternalRead("key", "value2");
                // cache.getAdvancedCache().getTransactionManager().commit();
                ConcurrencyVersioningTest.endBatch(cache);
                success = true;
            } catch (Exception e) {
                success = false;
            }
            latch.countDown();
        }
    }

    /**
     * Tests that if remove executes before put, then put still succeeds.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testGetRemovePutOnNonExisting() throws Exception {
        final DefaultCacheManager cacheManager = getVersionedCacheManager();
        ExecutorService executor = Executors.newSingleThreadExecutor();
        ConcurrencyVersioningTest.RemoveThread removeThread = new ConcurrencyVersioningTest.RemoveThread(cacheManager);
        Cache<String, String> cache = cacheManager.getCache(REALM_CACHE_NAME);
        cache.remove("key");
        ConcurrencyVersioningTest.startBatch(cache);
        cache.get("key");
        executor.execute(removeThread);
        removeThread.getLatch().await();
        cache.putForExternalRead("key", "value1");
        ConcurrencyVersioningTest.endBatch(cache);
        Assert.assertEquals(cache.get("key"), "value1");
        Assert.assertTrue(removeThread.isSuccess());
    }

    /**
     * Test that if a put of an existing key is removed after the put and before tx commit, it is evicted
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testGetRemovePutOnExisting() throws Exception {
        final DefaultCacheManager cacheManager = getVersionedCacheManager();
        ExecutorService executor = Executors.newSingleThreadExecutor();
        ConcurrencyVersioningTest.RemoveThread removeThread = new ConcurrencyVersioningTest.RemoveThread(cacheManager);
        Cache<String, String> cache = cacheManager.getCache(REALM_CACHE_NAME);
        cache.put("key", "value0");
        ConcurrencyVersioningTest.startBatch(cache);
        cache.get("key");
        executor.execute(removeThread);
        removeThread.getLatch().await();
        cache.put("key", "value1");
        try {
            ConcurrencyVersioningTest.endBatch(cache);
            Assert.fail("Write skew should be detected");
        } catch (Exception e) {
        }
        Assert.assertNull(cache.get("key"));
        Assert.assertTrue(removeThread.isSuccess());
    }

    /**
     * Test that if a put of an existing key is removed after the put and before tx commit, it is evicted
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testGetRemovePutEternalOnExisting() throws Exception {
        final DefaultCacheManager cacheManager = getVersionedCacheManager();
        ExecutorService executor = Executors.newSingleThreadExecutor();
        ConcurrencyVersioningTest.RemoveThread removeThread = new ConcurrencyVersioningTest.RemoveThread(cacheManager);
        Cache<String, String> cache = cacheManager.getCache(REALM_CACHE_NAME);
        cache.put("key", "value0");
        ConcurrencyVersioningTest.startBatch(cache);
        cache.get("key");
        executor.execute(removeThread);
        cache.putForExternalRead("key", "value1");
        removeThread.getLatch().await();
        try {
            ConcurrencyVersioningTest.endBatch(cache);
            // Assert.fail("Write skew should be detected");
        } catch (Exception e) {
        }
        Assert.assertNull(cache.get("key"));
        Assert.assertTrue(removeThread.isSuccess());
    }

    @Test
    public void testPutExternalRemoveOnExisting() throws Exception {
        final DefaultCacheManager cacheManager = getVersionedCacheManager();
        ExecutorService executor = Executors.newSingleThreadExecutor();
        ConcurrencyVersioningTest.RemoveThread removeThread = new ConcurrencyVersioningTest.RemoveThread(cacheManager);
        Cache<String, String> cache = cacheManager.getCache(REALM_CACHE_NAME);
        cache.put("key", "value0");
        ConcurrencyVersioningTest.startBatch(cache);
        cache.putForExternalRead("key", "value1");
        executor.execute(removeThread);
        removeThread.getLatch().await();
        try {
            ConcurrencyVersioningTest.endBatch(cache);
            // Assert.fail("Write skew should be detected");
        } catch (Exception e) {
        }
        Assert.assertNull(cache.get("key"));
        Assert.assertTrue(removeThread.isSuccess());
    }
}

