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
package com.hazelcast.cache.eviction;


import com.hazelcast.cache.CacheTestSupport;
import com.hazelcast.cache.HazelcastExpiryPolicy;
import com.hazelcast.cache.ICache;
import com.hazelcast.config.CacheConfig;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.test.HazelcastParametersRunnerFactory;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.OverridePropertyRule;
import com.hazelcast.test.TestHazelcastInstanceFactory;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import com.hazelcast.test.backup.BackupAccessor;
import com.hazelcast.test.backup.TestBackupUtils;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import javax.cache.Cache;
import javax.cache.event.CacheEntryEvent;
import javax.cache.event.CacheEntryExpiredListener;
import javax.cache.event.CacheEntryListenerException;
import javax.cache.expiry.Duration;
import javax.cache.expiry.EternalExpiryPolicy;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;


@RunWith(Parameterized.class)
@Parameterized.UseParametersRunnerFactory(HazelcastParametersRunnerFactory.class)
@Category({ QuickTest.class, ParallelTest.class })
public class CacheExpirationTest extends CacheTestSupport {
    @Rule
    public final OverridePropertyRule overrideTaskSecondsRule = OverridePropertyRule.set(PROP_TASK_PERIOD_SECONDS, "1");

    @Parameterized.Parameter(0)
    public boolean useSyncBackups;

    private final Duration FIVE_SECONDS = new Duration(TimeUnit.SECONDS, 5);

    private static final int CLUSTER_SIZE = 3;

    private static final int KEY_RANGE = 10000;

    private HazelcastInstance[] instances = new HazelcastInstance[3];

    private TestHazelcastInstanceFactory factory;

    @Test
    public void testSimpleExpiration_put() {
        CacheExpirationTest.SimpleExpiryListener listener = new CacheExpirationTest.SimpleExpiryListener();
        CacheConfig<String, String> cacheConfig = createCacheConfig(new HazelcastExpiryPolicy(1, 1, 1), listener);
        Cache<String, String> cache = createCache(cacheConfig);
        cache.put("key", "value");
        HazelcastTestSupport.assertEqualsEventually(1, listener.getExpirationCount());
    }

    @Test
    public void testSimpleExpiration_putAsync() {
        CacheExpirationTest.SimpleExpiryListener listener = new CacheExpirationTest.SimpleExpiryListener();
        CacheConfig<String, String> cacheConfig = createCacheConfig(new HazelcastExpiryPolicy(1, 1, 1), listener);
        Cache<String, String> cache = createCache(cacheConfig);
        ((ICache<String, String>) (cache)).putAsync("key", "value");
        HazelcastTestSupport.assertEqualsEventually(1, listener.getExpirationCount());
    }

    @Test
    public void testSimpleExpiration_putAll() {
        CacheExpirationTest.SimpleExpiryListener listener = new CacheExpirationTest.SimpleExpiryListener();
        CacheConfig<String, String> cacheConfig = createCacheConfig(new HazelcastExpiryPolicy(1, 1, 1), listener);
        Cache<String, String> cache = createCache(cacheConfig);
        Map<String, String> entries = new HashMap<String, String>();
        entries.put("key1", "value1");
        entries.put("key2", "value2");
        cache.putAll(entries);
        HazelcastTestSupport.assertEqualsEventually(2, listener.getExpirationCount());
    }

    @Test
    public void testSimpleExpiration_getAndPut() {
        CacheExpirationTest.SimpleExpiryListener listener = new CacheExpirationTest.SimpleExpiryListener();
        CacheConfig<String, String> cacheConfig = createCacheConfig(new HazelcastExpiryPolicy(1, 1, 1), listener);
        Cache<String, String> cache = createCache(cacheConfig);
        cache.getAndPut("key", "value");
        HazelcastTestSupport.assertEqualsEventually(1, listener.getExpirationCount());
    }

    @Test
    public void testSimpleExpiration_getAndPutAsync() {
        CacheExpirationTest.SimpleExpiryListener listener = new CacheExpirationTest.SimpleExpiryListener();
        CacheConfig<String, String> cacheConfig = createCacheConfig(new HazelcastExpiryPolicy(1, 1, 1), listener);
        Cache<String, String> cache = createCache(cacheConfig);
        ((ICache<String, String>) (cache)).getAndPutAsync("key", "value");
        HazelcastTestSupport.assertEqualsEventually(1, listener.getExpirationCount());
    }

    @Test
    public void testSimpleExpiration_getAndReplace() {
        CacheExpirationTest.SimpleExpiryListener listener = new CacheExpirationTest.SimpleExpiryListener();
        CacheConfig<String, String> cacheConfig = createCacheConfig(new EternalExpiryPolicy(), listener);
        Cache<String, String> cache = createCache(cacheConfig);
        cache.put("key", "value");
        cache.unwrap(ICache.class).getAndReplace("key", "value", new HazelcastExpiryPolicy(1, 1, 1));
        HazelcastTestSupport.assertEqualsEventually(1, listener.getExpirationCount());
    }

    @Test
    public void testSimpleExpiration_getAndReplaceAsync() {
        CacheExpirationTest.SimpleExpiryListener listener = new CacheExpirationTest.SimpleExpiryListener();
        CacheConfig<String, String> cacheConfig = createCacheConfig(new EternalExpiryPolicy(), listener);
        Cache<String, String> cache = createCache(cacheConfig);
        cache.put("key", "value");
        cache.unwrap(ICache.class).getAndReplaceAsync("key", "value", new HazelcastExpiryPolicy(1, 1, 1));
        HazelcastTestSupport.assertEqualsEventually(1, listener.getExpirationCount());
    }

    @Test
    public void testSimpleExpiration_putIfAbsent() {
        CacheExpirationTest.SimpleExpiryListener listener = new CacheExpirationTest.SimpleExpiryListener();
        CacheConfig<String, String> cacheConfig = createCacheConfig(new HazelcastExpiryPolicy(1, 1, 1), listener);
        Cache<String, String> cache = createCache(cacheConfig);
        cache.putIfAbsent("key", "value");
        HazelcastTestSupport.assertEqualsEventually(1, listener.getExpirationCount());
    }

    @Test
    public void testSimpleExpiration_putIfAbsentAsync() {
        CacheExpirationTest.SimpleExpiryListener listener = new CacheExpirationTest.SimpleExpiryListener();
        CacheConfig<String, String> cacheConfig = createCacheConfig(new HazelcastExpiryPolicy(1, 1, 1), listener);
        Cache<String, String> cache = createCache(cacheConfig);
        ((ICache<String, String>) (cache)).putIfAbsentAsync("key", "value");
        HazelcastTestSupport.assertEqualsEventually(1, listener.getExpirationCount());
    }

    @Test
    public void testBackupsAreEmptyAfterExpiration() {
        CacheExpirationTest.SimpleExpiryListener listener = new CacheExpirationTest.SimpleExpiryListener();
        CacheConfig cacheConfig = createCacheConfig(new HazelcastExpiryPolicy(100, 100, 100), listener);
        Cache cache = createCache(cacheConfig);
        for (int i = 0; i < (CacheExpirationTest.KEY_RANGE); i++) {
            cache.put(i, i);
        }
        HazelcastTestSupport.assertEqualsEventually(CacheExpirationTest.KEY_RANGE, listener.getExpirationCount());
        for (int i = 1; i < (CacheExpirationTest.CLUSTER_SIZE); i++) {
            BackupAccessor backupAccessor = TestBackupUtils.newCacheAccessor(instances, cache.getName(), i);
            TestBackupUtils.assertBackupSizeEventually(0, backupAccessor);
        }
    }

    @Test
    public void test_whenEntryIsAccessedBackupIsNotCleaned() {
        CacheConfig<Integer, Integer> cacheConfig = createCacheConfig(new HazelcastExpiryPolicy(FIVE_SECONDS, Duration.ETERNAL, FIVE_SECONDS));
        Cache<Integer, Integer> cache = createCache(cacheConfig);
        for (int i = 0; i < (CacheExpirationTest.KEY_RANGE); i++) {
            cache.put(i, i);
            cache.get(i);
        }
        HazelcastTestSupport.sleepAtLeastSeconds(5);
        for (int i = 1; i < (CacheExpirationTest.CLUSTER_SIZE); i++) {
            BackupAccessor backupAccessor = TestBackupUtils.newCacheAccessor(instances, cache.getName(), i);
            for (int j = 0; j < (CacheExpirationTest.KEY_RANGE); j++) {
                Assert.assertEquals(i, backupAccessor.get(i));
            }
        }
    }

    @Test
    public void test_whenEntryIsUpdatedBackupIsNotCleaned() {
        CacheConfig<Integer, Integer> cacheConfig = createCacheConfig(new HazelcastExpiryPolicy(FIVE_SECONDS, FIVE_SECONDS, Duration.ETERNAL));
        Cache<Integer, Integer> cache = createCache(cacheConfig);
        for (int i = 0; i < (CacheExpirationTest.KEY_RANGE); i++) {
            cache.put(i, i);
            cache.put(i, i);
        }
        cache.put(1, 1);
        HazelcastTestSupport.sleepAtLeastSeconds(5);
        for (int i = 1; i < (CacheExpirationTest.CLUSTER_SIZE); i++) {
            BackupAccessor backupAccessor = TestBackupUtils.newCacheAccessor(instances, cache.getName(), i);
            for (int j = 0; j < (CacheExpirationTest.KEY_RANGE); j++) {
                Assert.assertEquals(i, backupAccessor.get(i));
            }
        }
    }

    @Test
    public void test_backupOperationAppliesDefaultExpiryPolicy() {
        CacheExpirationTest.SimpleExpiryListener listener = new CacheExpirationTest.SimpleExpiryListener();
        HazelcastExpiryPolicy defaultExpiryPolicy = new HazelcastExpiryPolicy(FIVE_SECONDS, FIVE_SECONDS, FIVE_SECONDS);
        CacheConfig cacheConfig = createCacheConfig(defaultExpiryPolicy, listener);
        ICache cache = createCache(cacheConfig);
        for (int i = 0; i < 100; i++) {
            cache.put(i, i);
        }
        // Check if all backup entries have applied the default expiry policy
        for (int i = 1; i < (CacheExpirationTest.CLUSTER_SIZE); i++) {
            BackupAccessor backupAccessor = TestBackupUtils.newCacheAccessor(instances, cache.getName(), i);
            for (int j = 0; j < 100; j++) {
                TestBackupUtils.assertExpirationTimeExistsEventually(j, backupAccessor);
            }
        }
        // terminate 2 nodes to cause backup promotion at the 0th member
        HazelcastTestSupport.getNode(instances[1]).shutdown(true);
        HazelcastTestSupport.getNode(instances[2]).shutdown(true);
        // expiration time is over.
        HazelcastTestSupport.sleepAtLeastSeconds(5);
        // Check if there are unexpired entries after backup promotion
        int unExpiredCount = 0;
        for (int i = 0; i < 100; i++) {
            if ((cache.get(i)) != null) {
                unExpiredCount++;
                break;
            }
        }
        Assert.assertEquals(0, unExpiredCount);
        HazelcastTestSupport.assertEqualsEventually(100, listener.expirationCount);
    }

    @Test
    public void test_whenEntryIsRemovedBackupIsCleaned() {
        CacheExpirationTest.SimpleExpiryListener listener = new CacheExpirationTest.SimpleExpiryListener();
        int ttlSeconds = 10;
        Duration duration = new Duration(TimeUnit.SECONDS, ttlSeconds);
        HazelcastExpiryPolicy expiryPolicy = new HazelcastExpiryPolicy(duration, duration, duration);
        CacheConfig<Integer, Integer> cacheConfig = createCacheConfig(expiryPolicy, listener);
        Cache<Integer, Integer> cache = createCache(cacheConfig);
        for (int i = 0; i < (CacheExpirationTest.KEY_RANGE); i++) {
            cache.put(i, i);
            Assert.assertTrue(((("Expected to remove entry " + i) + " but entry was not present. Expired entry count: ") + (listener.getExpirationCount().get())), cache.remove(i));
        }
        HazelcastTestSupport.sleepAtLeastSeconds(ttlSeconds);
        Assert.assertEquals(0, listener.getExpirationCount().get());
        for (int i = 1; i < (CacheExpirationTest.CLUSTER_SIZE); i++) {
            BackupAccessor backupAccessor = TestBackupUtils.newCacheAccessor(instances, cache.getName(), i);
            TestBackupUtils.assertBackupSizeEventually(0, backupAccessor);
        }
    }

    @Test
    public void test_whenEntryIsRemovedBackupIsCleaned_eternalDuration() {
        CacheConfig<Integer, Integer> cacheConfig = createCacheConfig(new HazelcastExpiryPolicy(Duration.ETERNAL, Duration.ETERNAL, Duration.ETERNAL));
        Cache<Integer, Integer> cache = createCache(cacheConfig);
        for (int i = 0; i < (CacheExpirationTest.KEY_RANGE); i++) {
            cache.put(i, i);
            cache.remove(i);
        }
        for (int i = 1; i < (CacheExpirationTest.CLUSTER_SIZE); i++) {
            BackupAccessor backupAccessor = TestBackupUtils.newCacheAccessor(instances, cache.getName(), i);
            TestBackupUtils.assertBackupSizeEventually(0, backupAccessor);
        }
    }

    public static class SimpleExpiryListener<K, V> implements Serializable , CacheEntryExpiredListener<K, V> {
        private AtomicInteger expirationCount = new AtomicInteger();

        @Override
        public void onExpired(Iterable<CacheEntryEvent<? extends K, ? extends V>> cacheEntryEvents) throws CacheEntryListenerException {
            expirationCount.incrementAndGet();
        }

        public AtomicInteger getExpirationCount() {
            return expirationCount;
        }
    }
}

