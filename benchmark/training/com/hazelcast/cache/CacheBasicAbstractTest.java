/**
 * Copyright (c) 2008-2019, Hazelcast, Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.hazelcast.cache;


import Cache.Entry;
import Duration.ONE_DAY;
import com.hazelcast.config.CacheConfig;
import com.hazelcast.core.ICompletableFuture;
import com.hazelcast.internal.serialization.impl.DefaultSerializationServiceBuilder;
import com.hazelcast.logging.Logger;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.spi.serialization.SerializationService;
import com.hazelcast.test.AssertTask;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.util.SampleableConcurrentHashMap;
import java.io.Serializable;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.LockSupport;
import javax.cache.Cache;
import javax.cache.CacheManager;
import javax.cache.configuration.FactoryBuilder;
import javax.cache.configuration.MutableCacheEntryListenerConfiguration;
import javax.cache.configuration.MutableConfiguration;
import javax.cache.event.CacheEntryEvent;
import javax.cache.event.CacheEntryExpiredListener;
import javax.cache.event.CacheEntryListenerException;
import javax.cache.expiry.CreatedExpiryPolicy;
import javax.cache.expiry.Duration;
import javax.cache.expiry.TouchedExpiryPolicy;
import javax.cache.processor.EntryProcessor;
import javax.cache.processor.EntryProcessorException;
import javax.cache.processor.EntryProcessorResult;
import javax.cache.processor.MutableEntry;
import org.junit.Assert;
import org.junit.Test;


public abstract class CacheBasicAbstractTest extends CacheTestSupport {
    @Test
    public void testPutGetRemoveReplace() {
        ICache<String, String> cache = createCache();
        cache.put("key1", "value1");
        Assert.assertEquals("value1", cache.get("key1"));
        Assert.assertEquals("value1", cache.getAndPut("key1", "value2"));
        Assert.assertEquals(1, cache.size());
        Assert.assertTrue(cache.remove("key1"));
        cache.put("key1", "value3");
        Assert.assertFalse(cache.remove("key1", "xx"));
        Assert.assertTrue(cache.remove("key1", "value3"));
        Assert.assertNull(cache.get("key1"));
        Assert.assertTrue(cache.putIfAbsent("key1", "value1"));
        Assert.assertFalse(cache.putIfAbsent("key1", "value1"));
        Assert.assertEquals("value1", cache.getAndRemove("key1"));
        Assert.assertNull(cache.get("key1"));
        cache.put("key1", "value1");
        Assert.assertTrue(cache.containsKey("key1"));
        Assert.assertFalse(cache.replace("key2", "value2"));
        Assert.assertTrue(cache.replace("key1", "value2"));
        Assert.assertEquals("value2", cache.get("key1"));
        Assert.assertFalse(cache.replace("key1", "xx", "value3"));
        Assert.assertTrue(cache.replace("key1", "value2", "value3"));
        Assert.assertEquals("value3", cache.get("key1"));
        Assert.assertEquals("value3", cache.getAndReplace("key1", "value4"));
        Assert.assertEquals("value4", cache.get("key1"));
    }

    @Test
    public void testAsyncGetPutRemove() throws Exception {
        final ICache<String, String> cache = createCache();
        final String key = "key";
        cache.put(key, "value1");
        Future future = cache.getAsync(key);
        Assert.assertEquals("value1", future.get());
        cache.putAsync(key, "value2");
        HazelcastTestSupport.assertTrueEventually(new AssertTask() {
            @Override
            public void run() {
                Assert.assertEquals("value2", cache.get(key));
            }
        });
        future = cache.getAndPutAsync(key, "value3");
        Assert.assertEquals("value2", future.get());
        Assert.assertEquals("value3", cache.get(key));
        future = cache.removeAsync("key2");
        Assert.assertFalse(((Boolean) (future.get())));
        future = cache.removeAsync(key);
        Assert.assertTrue(((Boolean) (future.get())));
        cache.put(key, "value4");
        future = cache.getAndRemoveAsync("key2");
        Assert.assertNull(future.get());
        future = cache.getAndRemoveAsync(key);
        Assert.assertEquals("value4", future.get());
    }

    @Test
    public void testPutIfAbsentAsync_success() throws Exception {
        ICache<String, String> cache = createCache();
        String key = HazelcastTestSupport.randomString();
        ICompletableFuture<Boolean> iCompletableFuture = cache.putIfAbsentAsync(key, HazelcastTestSupport.randomString());
        Assert.assertTrue(iCompletableFuture.get());
    }

    @Test
    public void testPutIfAbsentAsync_fail() throws Exception {
        ICache<String, String> cache = createCache();
        String key = HazelcastTestSupport.randomString();
        cache.put(key, HazelcastTestSupport.randomString());
        ICompletableFuture<Boolean> iCompletableFuture = cache.putIfAbsentAsync(key, HazelcastTestSupport.randomString());
        Assert.assertFalse(iCompletableFuture.get());
    }

    @Test
    public void testPutIfAbsentAsync_withExpiryPolicy() {
        final ICache<String, String> cache = createCache();
        final String key = HazelcastTestSupport.randomString();
        HazelcastExpiryPolicy expiryPolicy = new HazelcastExpiryPolicy(1, 1, 1);
        cache.putIfAbsentAsync(key, HazelcastTestSupport.randomString(), expiryPolicy);
        HazelcastTestSupport.assertTrueEventually(new AssertTask() {
            @Override
            public void run() {
                Assert.assertNull(cache.get(key));
            }
        });
    }

    @Test
    public void testGetAndReplaceAsync() throws Exception {
        ICache<String, String> cache = createCache();
        String key = HazelcastTestSupport.randomString();
        String oldValue = HazelcastTestSupport.randomString();
        String newValue = HazelcastTestSupport.randomString();
        cache.put(key, oldValue);
        ICompletableFuture<String> iCompletableFuture = cache.getAndReplaceAsync(key, newValue);
        Assert.assertEquals(iCompletableFuture.get(), oldValue);
        Assert.assertEquals(cache.get(key), newValue);
    }

    @Test
    public void testGetAll_withEmptySet() {
        ICache<String, String> cache = createCache();
        Map<String, String> map = cache.getAll(Collections.<String>emptySet());
        Assert.assertEquals(0, map.size());
    }

    @Test
    public void testClear() {
        ICache<String, String> cache = createCache();
        for (int i = 0; i < 10; i++) {
            cache.put(("key" + i), ("value" + i));
        }
        cache.clear();
        Assert.assertEquals(0, cache.size());
    }

    @Test
    public void testRemoveAll() {
        ICache<String, String> cache = createCache();
        for (int i = 0; i < 10; i++) {
            cache.put(("key" + i), ("value" + i));
        }
        cache.removeAll();
        Assert.assertEquals(0, cache.size());
    }

    @Test
    public void testPutWithTtl() throws Exception {
        final ICache<String, String> cache = createCache();
        final String key = "key";
        cache.put(key, "value1", ttlToExpiryPolicy(1, TimeUnit.SECONDS));
        HazelcastTestSupport.assertTrueEventually(new AssertTask() {
            public void run() {
                Assert.assertNull(cache.get(key));
            }
        });
        Assert.assertEquals(0, cache.size());
        cache.putAsync(key, "value1", ttlToExpiryPolicy(1, TimeUnit.SECONDS));
        HazelcastTestSupport.assertTrueEventually(new AssertTask() {
            @Override
            public void run() {
                Assert.assertNull(cache.get(key));
            }
        });
        Assert.assertEquals(0, cache.size());
        cache.put(key, "value2");
        String value = cache.getAndPut(key, "value3", ttlToExpiryPolicy(1, TimeUnit.SECONDS));
        Assert.assertEquals("value2", value);
        HazelcastTestSupport.assertTrueEventually(new AssertTask() {
            @Override
            public void run() {
                Assert.assertNull(cache.get(key));
            }
        });
        Assert.assertEquals(0, cache.size());
        cache.put(key, "value4");
        Future future = cache.getAndPutAsync(key, "value5", ttlToExpiryPolicy(1, TimeUnit.SECONDS));
        Assert.assertEquals("value4", future.get());
        HazelcastTestSupport.assertTrueEventually(new AssertTask() {
            @Override
            public void run() {
                Assert.assertNull(cache.get(key));
            }
        });
        Assert.assertEquals(0, cache.size());
    }

    @Test
    @SuppressWarnings("WhileLoopReplaceableByForEach")
    public void testIterator() {
        ICache<Integer, Integer> cache = createCache();
        int size = 1111;
        int multiplier = 11;
        for (int i = 0; i < size; i++) {
            cache.put(i, (i * multiplier));
        }
        int[] keys = new int[size];
        int keyIndex = 0;
        Iterator<Entry<Integer, Integer>> iterator = cache.iterator();
        while (iterator.hasNext()) {
            Entry<Integer, Integer> e = iterator.next();
            int key = e.getKey();
            int value = e.getValue();
            Assert.assertEquals((key * multiplier), value);
            keys[(keyIndex++)] = key;
        } 
        Assert.assertEquals(size, keyIndex);
        Arrays.sort(keys);
        for (int i = 0; i < size; i++) {
            Assert.assertEquals(i, keys[i]);
        }
    }

    @Test
    public void testExpiration() {
        CacheConfig<Integer, String> config = new CacheConfig<Integer, String>();
        final CacheBasicAbstractTest.SimpleExpiryListener<Integer, String> listener = new CacheBasicAbstractTest.SimpleExpiryListener<Integer, String>();
        MutableCacheEntryListenerConfiguration<Integer, String> listenerConfiguration = new MutableCacheEntryListenerConfiguration<Integer, String>(FactoryBuilder.factoryOf(listener), null, true, true);
        config.addCacheEntryListenerConfiguration(listenerConfiguration);
        config.setExpiryPolicyFactory(FactoryBuilder.factoryOf(new HazelcastExpiryPolicy(100, 100, 100)));
        Cache<Integer, String> instanceCache = createCache(config);
        instanceCache.put(1, "value");
        HazelcastTestSupport.assertTrueEventually(new AssertTask() {
            @Override
            public void run() throws Exception {
                Assert.assertEquals(1, listener.expired.get());
            }
        });
    }

    @Test
    public void testExpiration_entryWithOwnTtl() {
        CacheConfig<Integer, String> config = new CacheConfig<Integer, String>();
        final CacheBasicAbstractTest.SimpleExpiryListener<Integer, String> listener = new CacheBasicAbstractTest.SimpleExpiryListener<Integer, String>();
        MutableCacheEntryListenerConfiguration<Integer, String> listenerConfiguration = new MutableCacheEntryListenerConfiguration<Integer, String>(FactoryBuilder.factoryOf(listener), null, true, true);
        config.addCacheEntryListenerConfiguration(listenerConfiguration);
        config.setExpiryPolicyFactory(FactoryBuilder.factoryOf(new HazelcastExpiryPolicy(Duration.ETERNAL, Duration.ETERNAL, Duration.ETERNAL)));
        ICache<Integer, String> instanceCache = createCache(config);
        instanceCache.put(1, "value", ttlToExpiryPolicy(1, TimeUnit.SECONDS));
        HazelcastTestSupport.assertTrueEventually(new AssertTask() {
            @Override
            public void run() throws Exception {
                Assert.assertEquals(1, listener.expired.get());
            }
        });
    }

    @Test
    public void testIteratorRemove() {
        ICache<Integer, Integer> cache = createCache();
        int size = 1111;
        for (int i = 0; i < size; i++) {
            cache.put(i, i);
        }
        Iterator<Entry<Integer, Integer>> iterator = cache.iterator();
        while (iterator.hasNext()) {
            iterator.next();
            iterator.remove();
        } 
        Assert.assertEquals(0, cache.size());
    }

    @Test(expected = IllegalStateException.class)
    public void testIteratorIllegalRemove() {
        ICache<Integer, Integer> cache = createCache();
        int size = 10;
        for (int i = 0; i < size; i++) {
            cache.put(i, i);
        }
        Iterator<Entry<Integer, Integer>> iterator = cache.iterator();
        if (iterator.hasNext()) {
            iterator.remove();
        }
    }

    @Test
    public void testIteratorDuringInsertion_withoutEviction() {
        testIteratorDuringInsertion(true);
    }

    @Test
    public void testIteratorDuringInsertion_withEviction() {
        testIteratorDuringInsertion(false);
    }

    @Test
    public void testIteratorDuringUpdate_withoutEviction() {
        testIteratorDuringUpdate(true);
    }

    @Test
    public void testIteratorDuringUpdate_withEviction() {
        testIteratorDuringUpdate(false);
    }

    @Test
    public void testIteratorDuringRemoval_withoutEviction() {
        testIteratorDuringRemoval(true);
    }

    @Test
    public void testIteratorDuringRemoval_withEviction() {
        testIteratorDuringRemoval(false);
    }

    @Test
    public void testRemoveAsync() throws Exception {
        ICache<String, String> cache = createCache();
        String key = HazelcastTestSupport.randomString();
        String value = HazelcastTestSupport.randomString();
        cache.put(key, value);
        ICompletableFuture<Boolean> future = cache.removeAsync(key);
        Assert.assertTrue(future.get());
    }

    @Test
    public void testRemoveAsyncWhenEntryNotFound() throws Exception {
        ICache<String, String> cache = createCache();
        ICompletableFuture<Boolean> future = cache.removeAsync(HazelcastTestSupport.randomString());
        Assert.assertFalse(future.get());
    }

    @Test
    public void testRemoveAsync_withOldValue() throws Exception {
        ICache<String, String> cache = createCache();
        String key = HazelcastTestSupport.randomString();
        String value = HazelcastTestSupport.randomString();
        cache.put(key, value);
        ICompletableFuture<Boolean> future = cache.removeAsync(key, value);
        Assert.assertTrue(future.get());
    }

    @Test
    public void testRemoveAsyncWhenEntryNotFound_withOldValue() throws Exception {
        ICache<String, String> cache = createCache();
        ICompletableFuture<Boolean> future = cache.removeAsync(HazelcastTestSupport.randomString(), HazelcastTestSupport.randomString());
        Assert.assertFalse(future.get());
    }

    @Test
    public void testGetCacheWithNullName() {
        Assert.assertNull(cacheManager.getCache(null));
    }

    @Test
    public void testRemovingSameEntryTwiceShouldTriggerEntryListenerOnlyOnce() {
        String cacheName = HazelcastTestSupport.randomString();
        CacheConfig<Integer, String> config = createCacheConfig();
        final CacheFromDifferentNodesTest.SimpleEntryListener<Integer, String> listener = new CacheFromDifferentNodesTest.SimpleEntryListener<Integer, String>();
        MutableCacheEntryListenerConfiguration<Integer, String> listenerConfiguration = new MutableCacheEntryListenerConfiguration<Integer, String>(FactoryBuilder.factoryOf(listener), null, true, true);
        config.addCacheEntryListenerConfiguration(listenerConfiguration);
        Cache<Integer, String> cache = cacheManager.createCache(cacheName, config);
        Assert.assertNotNull(cache);
        Integer key = 1;
        String value = "value";
        cache.put(key, value);
        HazelcastTestSupport.assertTrueEventually(new AssertTask() {
            @Override
            public void run() {
                Assert.assertEquals(1, listener.created.get());
            }
        });
        cache.removeAll();
        cache.removeAll();
        HazelcastTestSupport.assertTrueEventually(new AssertTask() {
            @Override
            public void run() {
                Assert.assertEquals(1, listener.removed.get());
            }
        });
    }

    @Test
    public void testCompletionEvent() {
        String cacheName = HazelcastTestSupport.randomString();
        CacheConfig<Integer, String> config = createCacheConfig();
        final CacheFromDifferentNodesTest.SimpleEntryListener<Integer, String> listener = new CacheFromDifferentNodesTest.SimpleEntryListener<Integer, String>();
        MutableCacheEntryListenerConfiguration<Integer, String> listenerConfiguration = new MutableCacheEntryListenerConfiguration<Integer, String>(FactoryBuilder.factoryOf(listener), null, true, true);
        config.addCacheEntryListenerConfiguration(listenerConfiguration);
        Cache<Integer, String> cache = cacheManager.createCache(cacheName, config);
        Assert.assertNotNull(cache);
        Integer key1 = 1;
        String value1 = "value1";
        cache.put(key1, value1);
        HazelcastTestSupport.assertTrueEventually(new AssertTask() {
            @Override
            public void run() {
                Assert.assertEquals(1, listener.created.get());
            }
        });
        Integer key2 = 2;
        String value2 = "value2";
        cache.put(key2, value2);
        HazelcastTestSupport.assertTrueEventually(new AssertTask() {
            @Override
            public void run() {
                Assert.assertEquals(2, listener.created.get());
            }
        });
        Set<Integer> keys = new HashSet<Integer>();
        keys.add(key1);
        keys.add(key2);
        cache.removeAll(keys);
        HazelcastTestSupport.assertTrueEventually(new AssertTask() {
            @Override
            public void run() {
                Assert.assertEquals(2, listener.removed.get());
            }
        });
    }

    @Test
    public void testJSRCreateDestroyCreate() {
        String cacheName = HazelcastTestSupport.randomString();
        Assert.assertNull(cacheManager.getCache(cacheName));
        CacheConfig<Integer, String> config = new CacheConfig<Integer, String>();
        Cache<Integer, String> cache = cacheManager.createCache(cacheName, config);
        Assert.assertNotNull(cache);
        Integer key = 1;
        String value1 = "value";
        cache.put(key, value1);
        String value2 = cache.get(key);
        Assert.assertEquals(value1, value2);
        cache.remove(key);
        Assert.assertNull(cache.get(key));
        cacheManager.destroyCache(cacheName);
        Assert.assertNull(cacheManager.getCache(cacheName));
        Cache<Integer, String> cacheAfterDestroy = cacheManager.createCache(cacheName, config);
        Assert.assertNotNull(cacheAfterDestroy);
    }

    @Test
    public void testCaches_NotEmpty() {
        ArrayList<String> expected = new ArrayList<String>();
        ArrayList<String> real = new ArrayList<String>();
        cacheManager.createCache("c1", new MutableConfiguration());
        cacheManager.createCache("c2", new MutableConfiguration());
        cacheManager.createCache("c3", new MutableConfiguration());
        expected.add(cacheManager.getCache("c1").getName());
        expected.add(cacheManager.getCache("c2").getName());
        expected.add(cacheManager.getCache("c3").getName());
        Iterable<String> cacheNames = cacheManager.getCacheNames();
        for (String cacheName : cacheNames) {
            real.add(cacheName);
        }
        expected.removeAll(real);
        Assert.assertTrue(expected.isEmpty());
    }

    @Test
    public void testInitableIterator() {
        int testSize = 3007;
        SerializationService serializationService = new DefaultSerializationServiceBuilder().build();
        for (int fetchSize = 1; fetchSize < 102; fetchSize++) {
            SampleableConcurrentHashMap<Data, String> map = new SampleableConcurrentHashMap<Data, String>(1000);
            for (int i = 0; i < testSize; i++) {
                Integer key = i;
                Data data = serializationService.toData(key);
                String value1 = "value" + i;
                map.put(data, value1);
            }
            int nextTableIndex = Integer.MAX_VALUE;
            int total = 0;
            int remaining = testSize;
            while ((remaining > 0) && (nextTableIndex > 0)) {
                int size = (remaining > fetchSize) ? fetchSize : remaining;
                List<Data> keys = new ArrayList<Data>(size);
                nextTableIndex = map.fetchKeys(nextTableIndex, size, keys);
                remaining -= keys.size();
                total += keys.size();
            } 
            Assert.assertEquals(testSize, total);
        }
    }

    @Test
    public void testCachesTypedConfig() {
        CacheConfig<Integer, Long> config = createCacheConfig();
        String cacheName = HazelcastTestSupport.randomString();
        config.setName(cacheName);
        config.setTypes(Integer.class, Long.class);
        Cache<Integer, Long> cache = cacheManager.createCache(cacheName, config);
        Cache<Integer, Long> cache2 = cacheManager.getCache(cacheName, config.getKeyType(), config.getValueType());
        Assert.assertNotNull(cache);
        Assert.assertNotNull(cache2);
    }

    @Test
    public void getAndOperateOnCacheAfterClose() {
        String cacheName = HazelcastTestSupport.randomString();
        ICache<Integer, Integer> cache = createCache(cacheName);
        cache.close();
        Assert.assertTrue("The cache should be closed", cache.isClosed());
        Assert.assertFalse("The cache should be destroyed", cache.isDestroyed());
        Cache<Object, Object> cacheAfterClose = cacheManager.getCache(cacheName);
        Assert.assertNotNull("Expected cacheAfterClose not to be null", cacheAfterClose);
        Assert.assertFalse("The cacheAfterClose should not be closed", cacheAfterClose.isClosed());
        cache.put(1, 1);
    }

    @Test
    public void getButCantOperateOnCacheAfterDestroy() {
        String cacheName = HazelcastTestSupport.randomString();
        ICache<Integer, Integer> cache = createCache(cacheName);
        cache.destroy();
        Assert.assertTrue("The cache should be closed", cache.isClosed());
        Assert.assertTrue("The cache should be destroyed", cache.isDestroyed());
        Cache<Object, Object> cacheAfterDestroy = cacheManager.getCache(cacheName);
        Assert.assertNull("Expected cacheAfterDestroy to be null", cacheAfterDestroy);
        try {
            cache.put(1, 1);
            Assert.fail("Since the cache is destroyed, an operation on the cache has to fail with 'IllegalStateException'");
        } catch (IllegalStateException expected) {
            // expect this exception since cache is closed and destroyed
        } catch (Throwable t) {
            t.printStackTrace();
            Assert.fail((("Since the cache is destroyed, an operation on the cache has to fail with 'IllegalStateException'," + " but failed with ") + (t.getMessage())));
        }
        Assert.assertTrue("The existing cache should still be closed", cache.isClosed());
        Assert.assertTrue("The existing cache should still be destroyed", cache.isDestroyed());
    }

    @Test
    public void testEntryProcessor_invoke() {
        ICache<String, String> cache = createCache();
        String value = HazelcastTestSupport.randomString();
        String key = HazelcastTestSupport.randomString();
        String postFix = HazelcastTestSupport.randomString();
        cache.put(key, value);
        String result = cache.invoke(key, new CacheBasicAbstractTest.AppendEntryProcessor(), postFix);
        String expectedResult = value + postFix;
        Assert.assertEquals(expectedResult, result);
        Assert.assertEquals(expectedResult, cache.get(key));
    }

    @Test
    public void testEntryProcessor_invokeAll() {
        ICache<String, String> cache = createCache();
        int entryCount = 10;
        Map<String, String> localMap = new HashMap<String, String>();
        for (int i = 0; i < entryCount; i++) {
            localMap.put(HazelcastTestSupport.randomString(), HazelcastTestSupport.randomString());
        }
        cache.putAll(localMap);
        String postFix = HazelcastTestSupport.randomString();
        Map<String, EntryProcessorResult<String>> resultMap = cache.invokeAll(localMap.keySet(), new CacheBasicAbstractTest.AppendEntryProcessor(), postFix);
        for (Map.Entry<String, String> localEntry : localMap.entrySet()) {
            EntryProcessorResult<String> entryProcessorResult = resultMap.get(localEntry.getKey());
            Assert.assertEquals(((localEntry.getValue()) + postFix), entryProcessorResult.get());
            Assert.assertEquals(((localEntry.getValue()) + postFix), cache.get(localEntry.getKey()));
        }
    }

    public static class AppendEntryProcessor implements Serializable , EntryProcessor<String, String, String> {
        private static final long serialVersionUID = -396575576353368113L;

        @Override
        public String process(MutableEntry<String, String> entry, Object... arguments) throws EntryProcessorException {
            String postFix = ((String) (arguments[0]));
            String value = entry.getValue();
            String result = value + postFix;
            entry.setValue(result);
            return result;
        }
    }

    private abstract static class AbstractCacheWorker {
        private final Random random = new Random();

        private final CountDownLatch firstIterationDone = new CountDownLatch(1);

        private final AtomicBoolean isRunning = new AtomicBoolean(true);

        private final CacheBasicAbstractTest.AbstractCacheWorker.CacheWorkerThread thread = new CacheBasicAbstractTest.AbstractCacheWorker.CacheWorkerThread();

        AbstractCacheWorker() {
            thread.start();
        }

        void awaitFirstIteration() {
            try {
                firstIterationDone.await();
            } catch (InterruptedException e) {
                Assert.fail(("CacheWorkerThread did not start! " + (e.getMessage())));
            }
        }

        public void shutdown() {
            isRunning.set(false);
            try {
                thread.join();
            } catch (InterruptedException e) {
                Assert.fail(("CacheWorkerThread did not stop! " + (e.getMessage())));
            }
        }

        private class CacheWorkerThread extends Thread {
            public void run() {
                try {
                    doRun(random);
                    LockSupport.parkNanos(1);
                } catch (Exception e) {
                    HazelcastTestSupport.ignore(e);
                }
                firstIterationDone.countDown();
                while (isRunning.get()) {
                    try {
                        doRun(random);
                        LockSupport.parkNanos(1);
                    } catch (Exception e) {
                        HazelcastTestSupport.ignore(e);
                    }
                } 
            }
        }

        abstract void doRun(Random random);
    }

    // https://github.com/hazelcast/hazelcast/issues/7236
    @Test
    public void expiryTimeShouldNotBeChangedOnUpdateWhenCreatedExpiryPolicyIsUsed() {
        final int createdExpiryTimeMillis = 100;
        Duration duration = new Duration(TimeUnit.MILLISECONDS, createdExpiryTimeMillis);
        CacheConfig<Integer, String> cacheConfig = new CacheConfig<Integer, String>();
        cacheConfig.setExpiryPolicyFactory(CreatedExpiryPolicy.factoryOf(duration));
        Cache<Integer, String> cache = createCache(cacheConfig);
        cache.put(1, "value");
        cache.put(1, "value");
        HazelcastTestSupport.sleepAtLeastMillis((createdExpiryTimeMillis + 1));
        Assert.assertNull(cache.get(1));
    }

    @Test
    public void testSetExpiryPolicyReturnsTrue() {
        ICache<Integer, String> cache = createCache();
        cache.put(1, "value");
        Assert.assertTrue(cache.setExpiryPolicy(1, new TouchedExpiryPolicy(Duration.FIVE_MINUTES)));
    }

    @Test
    public void testSetExpiryPolicyReturnsFalse_whenKeyDoesNotExist() {
        ICache<Integer, String> cache = createCache();
        Assert.assertFalse(cache.setExpiryPolicy(1, new TouchedExpiryPolicy(Duration.FIVE_MINUTES)));
    }

    @Test
    public void testSetExpiryPolicyReturnsFalse_whenKeyIsAlreadyExpired() {
        ICache<Integer, String> cache = createCache();
        cache.put(1, "value", new CreatedExpiryPolicy(new Duration(TimeUnit.SECONDS, 1)));
        HazelcastTestSupport.sleepAtLeastSeconds(2);
        Assert.assertFalse(cache.setExpiryPolicy(1, new TouchedExpiryPolicy(Duration.FIVE_MINUTES)));
    }

    @Test
    public void testRecordExpiryPolicyTakesPrecedenceOverCachePolicy() {
        final int updatedTtlMillis = 1000;
        CacheConfig<Integer, String> cacheConfig = new CacheConfig<Integer, String>();
        cacheConfig.setExpiryPolicyFactory(TouchedExpiryPolicy.factoryOf(ONE_DAY));
        ICache<Integer, String> cache = createCache(cacheConfig);
        cache.put(1, "value");
        cache.setExpiryPolicy(1, new TouchedExpiryPolicy(new Duration(TimeUnit.MILLISECONDS, updatedTtlMillis)));
        HazelcastTestSupport.sleepAtLeastMillis((updatedTtlMillis + 1));
        Assert.assertNull(cache.get(1));
    }

    @Test
    public void testRecordExpiryPolicyTakesPrecedenceOverPolicyAtCreation() {
        final int updatedTtlMillis = 1000;
        ICache<Integer, String> cache = createCache();
        cache.put(1, "value", new TouchedExpiryPolicy(Duration.ONE_DAY));
        cache.setExpiryPolicy(1, new TouchedExpiryPolicy(new Duration(TimeUnit.MILLISECONDS, updatedTtlMillis)));
        HazelcastTestSupport.sleepAtLeastMillis((updatedTtlMillis + 1));
        Assert.assertNull(cache.get(1));
    }

    @Test
    public void testRecordExpiryPolicyTakesPrecedence() {
        final int ttlMillis = 1000;
        Duration modifiedDuration = new Duration(TimeUnit.MILLISECONDS, ttlMillis);
        ICache<Integer, String> cache = createCache();
        cache.put(1, "value");
        cache.setExpiryPolicy(1, TouchedExpiryPolicy.factoryOf(modifiedDuration).create());
        HazelcastTestSupport.sleepAtLeastMillis((ttlMillis + 1));
        Assert.assertNull(cache.get(1));
    }

    @Test
    public void test_whenExpiryPolicyIsOverridden_thenNewPolicyIsInEffect() {
        final int ttlMillis = 1000;
        ICache<Integer, String> cache = createCache();
        cache.put(1, "value");
        cache.setExpiryPolicy(1, new TouchedExpiryPolicy(new Duration(TimeUnit.MILLISECONDS, ttlMillis)));
        cache.setExpiryPolicy(1, new TouchedExpiryPolicy(Duration.ETERNAL));
        HazelcastTestSupport.sleepAtLeastMillis((ttlMillis + 1));
        Assert.assertEquals("value", cache.get(1));
    }

    @Test
    public void test_CustomExpiryPolicyIsUsedWhenEntryIsUpdated() {
        ICache<Integer, String> cache = createCache();
        cache.put(1, "value");
        cache.setExpiryPolicy(1, new HazelcastExpiryPolicy(10000, 10000, 10000));
        HazelcastTestSupport.sleepAtLeastSeconds(5);
        cache.put(1, "value2");
        HazelcastTestSupport.sleepAtLeastSeconds(5);
        Assert.assertEquals("value2", cache.get(1));
    }

    @Test
    public void removeCacheFromOwnerCacheManagerWhenCacheIsDestroyed() {
        ICache cache = createCache();
        Assert.assertTrue("Expected the cache to be found in the CacheManager", cacheExistsInCacheManager(cache));
        cache.destroy();
        Assert.assertFalse("Expected the cache not to be found in the CacheManager", cacheExistsInCacheManager(cache));
    }

    @Test
    public void entryShouldNotBeExpiredWhenTimeUnitIsNullAndDurationIsZero() {
        Duration duration = new Duration(null, 0);
        CacheConfig<Integer, String> cacheConfig = new CacheConfig<Integer, String>();
        cacheConfig.setExpiryPolicyFactory(CreatedExpiryPolicy.factoryOf(duration));
        Cache<Integer, String> cache = createCache(cacheConfig);
        cache.put(1, "value");
        HazelcastTestSupport.sleepAtLeastMillis(1);
        Assert.assertNotNull(cache.get(1));
    }

    @Test(expected = IllegalStateException.class)
    public void cacheManagerOfCache_cannotBeOverwritten() throws Exception {
        Properties properties = HazelcastCachingProvider.propertiesByInstanceItself(getHazelcastInstance());
        CacheManager cacheManagerFooURI = cachingProvider.getCacheManager(new URI("foo"), null, properties);
        CacheManager cacheManagerFooClassLoader = cachingProvider.getCacheManager(null, new CacheBasicAbstractTest.MaliciousClassLoader(CacheBasicAbstractTest.class.getClassLoader()), properties);
        CacheConfig cacheConfig = new CacheConfig("the-cache");
        Cache cache1 = cacheManagerFooURI.createCache("the-cache", cacheConfig);
        // cache1.cacheManager is cacheManagerFooURI
        Assert.assertEquals(cacheManagerFooURI, cache1.getCacheManager());
        // assert one can still get the cache
        Cache cache2 = cacheManagerFooURI.getCache("the-cache");
        Assert.assertEquals(cache1, cache2);
        // attempt to overwrite existing cache1's CacheManager -> fails with IllegalStateException
        cacheManagerFooClassLoader.getCache("the-cache");
    }

    @Test
    public void cacheManagerOfCache_cannotBeOverwrittenConcurrently() throws Exception {
        final CountDownLatch latch = new CountDownLatch(1);
        final AtomicInteger illegalStateExceptionCount = new AtomicInteger();
        Properties properties = HazelcastCachingProvider.propertiesByInstanceItself(getHazelcastInstance());
        final CacheManager cacheManagerFooURI = cachingProvider.getCacheManager(new URI("foo"), null, properties);
        final CacheManager cacheManagerFooClassLoader = cachingProvider.getCacheManager(null, new CacheBasicAbstractTest.MaliciousClassLoader(CacheBasicAbstractTest.class.getClassLoader()), properties);
        Future createCacheFromFooURI = HazelcastTestSupport.spawn(new Runnable() {
            @Override
            public void run() {
                CacheBasicAbstractTest.createCacheConcurrently(latch, cacheManagerFooURI, illegalStateExceptionCount);
            }
        });
        Future createCacheFromFooClassLoader = HazelcastTestSupport.spawn(new Runnable() {
            @Override
            public void run() {
                CacheBasicAbstractTest.createCacheConcurrently(latch, cacheManagerFooClassLoader, illegalStateExceptionCount);
            }
        });
        latch.countDown();
        try {
            createCacheFromFooURI.get();
            createCacheFromFooClassLoader.get();
        } catch (ExecutionException e) {
            Throwable cause = e.getCause();
            if (!(cause instanceof IllegalStateException)) {
                Logger.getLogger(CacheBasicAbstractTest.class).severe("Unexpected exception", cause);
                throw new AssertionError(String.format("Unexpected exception thrown: %s", cause.getMessage()));
            }
        }
        // one of two failed
        Assert.assertEquals(1, illegalStateExceptionCount.get());
    }

    public static class MaliciousClassLoader extends ClassLoader {
        MaliciousClassLoader(ClassLoader parent) {
            super(parent);
        }

        @Override
        public String toString() {
            return "foo";
        }
    }

    public static class SimpleExpiryListener<K, V> implements Serializable , CacheEntryExpiredListener<K, V> {
        public AtomicInteger expired = new AtomicInteger();

        public SimpleExpiryListener() {
        }

        @Override
        public void onExpired(Iterable<CacheEntryEvent<? extends K, ? extends V>> cacheEntryEvents) throws CacheEntryListenerException {
            for (CacheEntryEvent<? extends K, ? extends V> cacheEntryEvent : cacheEntryEvents) {
                expired.incrementAndGet();
            }
        }
    }
}

