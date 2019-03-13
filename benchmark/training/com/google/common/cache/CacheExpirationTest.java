/**
 * Copyright (C) 2011 The Guava Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package com.google.common.cache;


import com.github.benmanes.caffeine.guava.CaffeinatedGuava;
import com.google.common.testing.FakeTicker;
import com.google.common.util.concurrent.Callables;
import java.util.Arrays;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicInteger;
import junit.framework.TestCase;

import static com.google.common.util.concurrent.MoreExecutors.directExecutor;
import static java.util.concurrent.TimeUnit.MILLISECONDS;


/**
 * Tests relating to cache expiration: make sure entries expire at the right times, make sure
 * expired entries don't show up, etc.
 *
 * @author mike nonemacher
 */
public class CacheExpirationTest extends TestCase {
    private static final long EXPIRING_TIME = 1000;

    private static final int VALUE_PREFIX = 12345;

    private static final String KEY_PREFIX = "key prefix:";

    public void testExpiration_expireAfterWrite() {
        FakeTicker ticker = new FakeTicker();
        TestingRemovalListeners.CountingRemovalListener<String, Integer> removalListener = TestingRemovalListeners.countingRemovalListener();
        CacheExpirationTest.WatchedCreatorLoader loader = new CacheExpirationTest.WatchedCreatorLoader();
        LoadingCache<String, Integer> cache = CaffeinatedGuava.build(com.github.benmanes.caffeine.cache.Caffeine.newBuilder().expireAfterWrite(CacheExpirationTest.EXPIRING_TIME, MILLISECONDS).executor(directExecutor()).removalListener(removalListener).ticker(ticker::read), loader);
        checkExpiration(cache, loader, ticker, removalListener);
    }

    public void testExpiration_expireAfterAccess() {
        FakeTicker ticker = new FakeTicker();
        TestingRemovalListeners.CountingRemovalListener<String, Integer> removalListener = TestingRemovalListeners.countingRemovalListener();
        CacheExpirationTest.WatchedCreatorLoader loader = new CacheExpirationTest.WatchedCreatorLoader();
        LoadingCache<String, Integer> cache = CaffeinatedGuava.build(com.github.benmanes.caffeine.cache.Caffeine.newBuilder().expireAfterAccess(CacheExpirationTest.EXPIRING_TIME, MILLISECONDS).executor(directExecutor()).removalListener(removalListener).ticker(ticker::read), loader);
        checkExpiration(cache, loader, ticker, removalListener);
    }

    public void testExpiringGet_expireAfterWrite() {
        FakeTicker ticker = new FakeTicker();
        TestingRemovalListeners.CountingRemovalListener<String, Integer> removalListener = TestingRemovalListeners.countingRemovalListener();
        CacheExpirationTest.WatchedCreatorLoader loader = new CacheExpirationTest.WatchedCreatorLoader();
        LoadingCache<String, Integer> cache = CaffeinatedGuava.build(com.github.benmanes.caffeine.cache.Caffeine.newBuilder().expireAfterWrite(CacheExpirationTest.EXPIRING_TIME, MILLISECONDS).executor(directExecutor()).removalListener(removalListener).ticker(ticker::read), loader);
        runExpirationTest(cache, loader, ticker, removalListener);
    }

    public void testExpiringGet_expireAfterAccess() {
        FakeTicker ticker = new FakeTicker();
        TestingRemovalListeners.CountingRemovalListener<String, Integer> removalListener = TestingRemovalListeners.countingRemovalListener();
        CacheExpirationTest.WatchedCreatorLoader loader = new CacheExpirationTest.WatchedCreatorLoader();
        LoadingCache<String, Integer> cache = CaffeinatedGuava.build(com.github.benmanes.caffeine.cache.Caffeine.newBuilder().expireAfterAccess(CacheExpirationTest.EXPIRING_TIME, MILLISECONDS).executor(directExecutor()).removalListener(removalListener).ticker(ticker::read), loader);
        runExpirationTest(cache, loader, ticker, removalListener);
    }

    public void testRemovalListener_expireAfterWrite() {
        FakeTicker ticker = new FakeTicker();
        final AtomicInteger evictionCount = new AtomicInteger();
        final AtomicInteger applyCount = new AtomicInteger();
        final AtomicInteger totalSum = new AtomicInteger();
        RemovalListener<Integer, AtomicInteger> removalListener = new RemovalListener<Integer, AtomicInteger>() {
            @Override
            public void onRemoval(Integer key, AtomicInteger value, RemovalCause cause) {
                if (cause.wasEvicted()) {
                    evictionCount.incrementAndGet();
                    totalSum.addAndGet(value.get());
                }
            }
        };
        CacheLoader<Integer, AtomicInteger> loader = new CacheLoader<Integer, AtomicInteger>() {
            @Override
            public AtomicInteger load(Integer key) {
                applyCount.incrementAndGet();
                return new AtomicInteger();
            }
        };
        LoadingCache<Integer, AtomicInteger> cache = CaffeinatedGuava.build(com.github.benmanes.caffeine.cache.Caffeine.newBuilder().removalListener(removalListener).expireAfterWrite(10, MILLISECONDS).executor(directExecutor()).ticker(ticker::read), loader);
        // Increment 100 times
        for (int i = 0; i < 100; ++i) {
            cache.getUnchecked(10).incrementAndGet();
            ticker.advance(1, MILLISECONDS);
        }
        TestCase.assertEquals(((evictionCount.get()) + 1), applyCount.get());
        int remaining = cache.getUnchecked(10).get();
        TestCase.assertEquals(100, ((totalSum.get()) + remaining));
    }

    public void testRemovalScheduler_expireAfterWrite() {
        FakeTicker ticker = new FakeTicker();
        TestingRemovalListeners.CountingRemovalListener<String, Integer> removalListener = TestingRemovalListeners.countingRemovalListener();
        CacheExpirationTest.WatchedCreatorLoader loader = new CacheExpirationTest.WatchedCreatorLoader();
        LoadingCache<String, Integer> cache = CaffeinatedGuava.build(com.github.benmanes.caffeine.cache.Caffeine.newBuilder().expireAfterWrite(CacheExpirationTest.EXPIRING_TIME, MILLISECONDS).executor(directExecutor()).removalListener(removalListener).ticker(ticker::read), loader);
        runRemovalScheduler(cache, removalListener, loader, ticker, CacheExpirationTest.KEY_PREFIX, CacheExpirationTest.EXPIRING_TIME);
    }

    public void testRemovalScheduler_expireAfterAccess() {
        FakeTicker ticker = new FakeTicker();
        TestingRemovalListeners.CountingRemovalListener<String, Integer> removalListener = TestingRemovalListeners.countingRemovalListener();
        CacheExpirationTest.WatchedCreatorLoader loader = new CacheExpirationTest.WatchedCreatorLoader();
        LoadingCache<String, Integer> cache = CaffeinatedGuava.build(com.github.benmanes.caffeine.cache.Caffeine.newBuilder().expireAfterAccess(CacheExpirationTest.EXPIRING_TIME, MILLISECONDS).executor(directExecutor()).removalListener(removalListener).ticker(ticker::read), loader);
        runRemovalScheduler(cache, removalListener, loader, ticker, CacheExpirationTest.KEY_PREFIX, CacheExpirationTest.EXPIRING_TIME);
    }

    public void testRemovalScheduler_expireAfterBoth() {
        FakeTicker ticker = new FakeTicker();
        TestingRemovalListeners.CountingRemovalListener<String, Integer> removalListener = TestingRemovalListeners.countingRemovalListener();
        CacheExpirationTest.WatchedCreatorLoader loader = new CacheExpirationTest.WatchedCreatorLoader();
        LoadingCache<String, Integer> cache = CaffeinatedGuava.build(com.github.benmanes.caffeine.cache.Caffeine.newBuilder().expireAfterAccess(CacheExpirationTest.EXPIRING_TIME, MILLISECONDS).expireAfterWrite(CacheExpirationTest.EXPIRING_TIME, MILLISECONDS).executor(directExecutor()).removalListener(removalListener).ticker(ticker::read), loader);
        runRemovalScheduler(cache, removalListener, loader, ticker, CacheExpirationTest.KEY_PREFIX, CacheExpirationTest.EXPIRING_TIME);
    }

    public void testExpirationOrder_access() {
        // test lru within a single segment
        FakeTicker ticker = new FakeTicker();
        TestingCacheLoaders.IdentityLoader<Integer> loader = TestingCacheLoaders.identityLoader();
        LoadingCache<Integer, Integer> cache = CaffeinatedGuava.build(com.github.benmanes.caffeine.cache.Caffeine.newBuilder().expireAfterAccess(11, MILLISECONDS).ticker(ticker::read), loader);
        for (int i = 0; i < 10; i++) {
            cache.getUnchecked(i);
            ticker.advance(1, MILLISECONDS);
        }
        Set<Integer> keySet = cache.asMap().keySet();
        assertThat(keySet).containsExactly(0, 1, 2, 3, 4, 5, 6, 7, 8, 9);
        // 0 expires
        ticker.advance(1, MILLISECONDS);
        assertThat(keySet).containsExactly(1, 2, 3, 4, 5, 6, 7, 8, 9);
        // reorder
        getAll(cache, Arrays.asList(0, 1, 2));
        CacheTesting.drainRecencyQueues(cache);
        ticker.advance(2, MILLISECONDS);
        assertThat(keySet).containsExactly(3, 4, 5, 6, 7, 8, 9, 0, 1, 2);
        // 3 expires
        ticker.advance(1, MILLISECONDS);
        assertThat(keySet).containsExactly(4, 5, 6, 7, 8, 9, 0, 1, 2);
        // reorder
        getAll(cache, Arrays.asList(5, 7, 9));
        CacheTesting.drainRecencyQueues(cache);
        assertThat(keySet).containsExactly(4, 6, 8, 0, 1, 2, 5, 7, 9);
        // 4 expires
        ticker.advance(1, MILLISECONDS);
        assertThat(keySet).containsExactly(6, 8, 0, 1, 2, 5, 7, 9);
        ticker.advance(1, MILLISECONDS);
        assertThat(keySet).containsExactly(6, 8, 0, 1, 2, 5, 7, 9);
        // 6 expires
        ticker.advance(1, MILLISECONDS);
        assertThat(keySet).containsExactly(8, 0, 1, 2, 5, 7, 9);
        ticker.advance(1, MILLISECONDS);
        assertThat(keySet).containsExactly(8, 0, 1, 2, 5, 7, 9);
        // 8 expires
        ticker.advance(1, MILLISECONDS);
        assertThat(keySet).containsExactly(0, 1, 2, 5, 7, 9);
    }

    public void testExpirationOrder_write() throws ExecutionException {
        // test lru within a single segment
        FakeTicker ticker = new FakeTicker();
        TestingCacheLoaders.IdentityLoader<Integer> loader = TestingCacheLoaders.identityLoader();
        LoadingCache<Integer, Integer> cache = CaffeinatedGuava.build(com.github.benmanes.caffeine.cache.Caffeine.newBuilder().expireAfterWrite(11, MILLISECONDS).ticker(ticker::read), loader);
        for (int i = 0; i < 10; i++) {
            cache.getUnchecked(i);
            ticker.advance(1, MILLISECONDS);
        }
        Set<Integer> keySet = cache.asMap().keySet();
        assertThat(keySet).containsExactly(0, 1, 2, 3, 4, 5, 6, 7, 8, 9);
        // 0 expires
        ticker.advance(1, MILLISECONDS);
        assertThat(keySet).containsExactly(1, 2, 3, 4, 5, 6, 7, 8, 9);
        // get doesn't stop 1 from expiring
        getAll(cache, Arrays.asList(0, 1, 2));
        CacheTesting.drainRecencyQueues(cache);
        ticker.advance(1, MILLISECONDS);
        assertThat(keySet).containsExactly(2, 3, 4, 5, 6, 7, 8, 9, 0);
        // get(K, Callable) doesn't stop 2 from expiring
        cache.get(2, Callables.returning((-2)));
        CacheTesting.drainRecencyQueues(cache);
        ticker.advance(1, MILLISECONDS);
        assertThat(keySet).containsExactly(3, 4, 5, 6, 7, 8, 9, 0);
        // asMap.put saves 3
        cache.asMap().put(3, (-3));
        ticker.advance(1, MILLISECONDS);
        assertThat(keySet).containsExactly(4, 5, 6, 7, 8, 9, 0, 3);
        // asMap.replace saves 4
        cache.asMap().replace(4, (-4));
        ticker.advance(1, MILLISECONDS);
        assertThat(keySet).containsExactly(5, 6, 7, 8, 9, 0, 3, 4);
        // 5 expires
        ticker.advance(1, MILLISECONDS);
        assertThat(keySet).containsExactly(6, 7, 8, 9, 0, 3, 4);
    }

    public void testExpirationOrder_writeAccess() throws ExecutionException {
        // test lru within a single segment
        FakeTicker ticker = new FakeTicker();
        TestingCacheLoaders.IdentityLoader<Integer> loader = TestingCacheLoaders.identityLoader();
        LoadingCache<Integer, Integer> cache = CaffeinatedGuava.build(com.github.benmanes.caffeine.cache.Caffeine.newBuilder().expireAfterWrite(5, MILLISECONDS).expireAfterAccess(3, MILLISECONDS).ticker(ticker::read), loader);
        for (int i = 0; i < 5; i++) {
            cache.getUnchecked(i);
        }
        ticker.advance(1, MILLISECONDS);
        for (int i = 5; i < 10; i++) {
            cache.getUnchecked(i);
        }
        ticker.advance(1, MILLISECONDS);
        Set<Integer> keySet = cache.asMap().keySet();
        assertThat(keySet).containsExactly(0, 1, 2, 3, 4, 5, 6, 7, 8, 9);
        // get saves 1, 3; 0, 2, 4 expire
        getAll(cache, Arrays.asList(1, 3));
        CacheTesting.drainRecencyQueues(cache);
        ticker.advance(1, MILLISECONDS);
        assertThat(keySet).containsExactly(5, 6, 7, 8, 9, 1, 3);
        // get saves 6, 8; 5, 7, 9 expire
        getAll(cache, Arrays.asList(6, 8));
        CacheTesting.drainRecencyQueues(cache);
        ticker.advance(1, MILLISECONDS);
        assertThat(keySet).containsExactly(1, 3, 6, 8);
        // get fails to save 1, put saves 3
        cache.asMap().put(3, (-3));
        getAll(cache, Arrays.asList(1));
        CacheTesting.drainRecencyQueues(cache);
        ticker.advance(1, MILLISECONDS);
        assertThat(keySet).containsExactly(6, 8, 3);
        // get(K, Callable) fails to save 8, replace saves 6
        cache.asMap().replace(6, (-6));
        cache.get(8, Callables.returning((-8)));
        CacheTesting.drainRecencyQueues(cache);
        ticker.advance(1, MILLISECONDS);
        assertThat(keySet).containsExactly(3, 6);
    }

    private static class WatchedCreatorLoader extends CacheLoader<String, Integer> {
        boolean wasCalled = false;// must be set in load()


        String keyPrefix = CacheExpirationTest.KEY_PREFIX;

        int valuePrefix = CacheExpirationTest.VALUE_PREFIX;

        public WatchedCreatorLoader() {
        }

        public void reset() {
            wasCalled = false;
        }

        public boolean wasCalled() {
            return wasCalled;
        }

        public void setValuePrefix(int valuePrefix) {
            this.valuePrefix = valuePrefix;
        }

        @Override
        public Integer load(String key) {
            wasCalled = true;
            return (valuePrefix) + (Integer.parseInt(key.substring(keyPrefix.length())));
        }
    }
}

