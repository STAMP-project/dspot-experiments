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


import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.guava.CaffeinatedGuava;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.common.testing.FakeTicker;
import com.google.common.testing.TestLogHandler;
import com.google.common.util.concurrent.Callables;
import com.google.common.util.concurrent.ExecutionError;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.MoreExecutors;
import com.google.common.util.concurrent.UncheckedExecutionException;
import com.google.common.util.concurrent.Uninterruptibles;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;
import junit.framework.TestCase;


/**
 * Tests relating to cache loading: concurrent loading, exceptions during loading, etc.
 *
 * @author mike nonemacher
 */
@SuppressWarnings("ThreadPriorityCheck")
public class CacheLoadingTest extends TestCase {
    Logger logger = Logger.getLogger("com.github.benmanes.caffeine.cache.BoundedLocalCache");

    TestLogHandler logHandler;

    public void testLoad() throws ExecutionException {
        LoadingCache<Object, Object> cache = CaffeinatedGuava.build(Caffeine.newBuilder().recordStats().executor(MoreExecutors.directExecutor()), TestingCacheLoaders.identityLoader());
        // LoadingCache<Object, Object> cache = CacheBuilder.newBuilder().recordStats().build(identityLoader());
        CacheStats stats = cache.stats();
        TestCase.assertEquals(0, stats.missCount());
        TestCase.assertEquals(0, stats.loadSuccessCount());
        TestCase.assertEquals(0, stats.loadExceptionCount());
        TestCase.assertEquals(0, stats.hitCount());
        Object key = new Object();
        TestCase.assertSame(key, cache.get(key));
        stats = cache.stats();
        TestCase.assertEquals(1, stats.missCount());
        TestCase.assertEquals(1, stats.loadSuccessCount());
        TestCase.assertEquals(0, stats.loadExceptionCount());
        TestCase.assertEquals(0, stats.hitCount());
        key = new Object();
        TestCase.assertSame(key, cache.getUnchecked(key));
        stats = cache.stats();
        TestCase.assertEquals(2, stats.missCount());
        TestCase.assertEquals(2, stats.loadSuccessCount());
        TestCase.assertEquals(0, stats.loadExceptionCount());
        TestCase.assertEquals(0, stats.hitCount());
        key = new Object();
        cache.refresh(key);
        checkNothingLogged();
        stats = cache.stats();
        TestCase.assertEquals(2, stats.missCount());
        TestCase.assertEquals(3, stats.loadSuccessCount());
        TestCase.assertEquals(0, stats.loadExceptionCount());
        TestCase.assertEquals(0, stats.hitCount());
        TestCase.assertSame(key, cache.get(key));
        stats = cache.stats();
        TestCase.assertEquals(2, stats.missCount());
        TestCase.assertEquals(3, stats.loadSuccessCount());
        TestCase.assertEquals(0, stats.loadExceptionCount());
        TestCase.assertEquals(1, stats.hitCount());
        Object value = new Object();
        // callable is not called
        TestCase.assertSame(key, cache.get(key, CacheLoadingTest.throwing(new Exception())));
        stats = cache.stats();
        TestCase.assertEquals(2, stats.missCount());
        TestCase.assertEquals(3, stats.loadSuccessCount());
        TestCase.assertEquals(0, stats.loadExceptionCount());
        TestCase.assertEquals(2, stats.hitCount());
        key = new Object();
        TestCase.assertSame(value, cache.get(key, Callables.returning(value)));
        stats = cache.stats();
        TestCase.assertEquals(3, stats.missCount());
        TestCase.assertEquals(4, stats.loadSuccessCount());
        TestCase.assertEquals(0, stats.loadExceptionCount());
        TestCase.assertEquals(2, stats.hitCount());
    }

    public void testReload() throws ExecutionException {
        final Object one = new Object();
        final Object two = new Object();
        CacheLoader<Object, Object> loader = new CacheLoader<Object, Object>() {
            @Override
            public Object load(Object key) {
                return one;
            }

            @Override
            public ListenableFuture<Object> reload(Object key, Object oldValue) {
                return Futures.immediateFuture(two);
            }
        };
        LoadingCache<Object, Object> cache = CaffeinatedGuava.build(Caffeine.newBuilder().recordStats().executor(MoreExecutors.directExecutor()), loader);
        Object key = new Object();
        CacheStats stats = cache.stats();
        TestCase.assertEquals(0, stats.missCount());
        TestCase.assertEquals(0, stats.loadSuccessCount());
        TestCase.assertEquals(0, stats.loadExceptionCount());
        TestCase.assertEquals(0, stats.hitCount());
        TestCase.assertSame(one, cache.getUnchecked(key));
        stats = cache.stats();
        TestCase.assertEquals(1, stats.missCount());
        TestCase.assertEquals(1, stats.loadSuccessCount());
        TestCase.assertEquals(0, stats.loadExceptionCount());
        TestCase.assertEquals(0, stats.hitCount());
        cache.refresh(key);
        checkNothingLogged();
        stats = cache.stats();
        TestCase.assertEquals(1, stats.missCount());
        TestCase.assertEquals(2, stats.loadSuccessCount());
        TestCase.assertEquals(0, stats.loadExceptionCount());
        TestCase.assertEquals(0, stats.hitCount());
        TestCase.assertSame(two, cache.getUnchecked(key));
        stats = cache.stats();
        TestCase.assertEquals(1, stats.missCount());
        TestCase.assertEquals(2, stats.loadSuccessCount());
        TestCase.assertEquals(0, stats.loadExceptionCount());
        TestCase.assertEquals(1, stats.hitCount());
    }

    public void testRefresh() {
        final Object one = new Object();
        final Object two = new Object();
        FakeTicker ticker = new FakeTicker();
        CacheLoader<Object, Object> loader = new CacheLoader<Object, Object>() {
            @Override
            public Object load(Object key) {
                return one;
            }

            @Override
            public ListenableFuture<Object> reload(Object key, Object oldValue) {
                return Futures.immediateFuture(two);
            }
        };
        LoadingCache<Object, Object> cache = CaffeinatedGuava.build(Caffeine.newBuilder().recordStats().ticker(ticker::read).refreshAfterWrite(1, TimeUnit.MILLISECONDS).executor(MoreExecutors.directExecutor()), loader);
        Object key = new Object();
        CacheStats stats = cache.stats();
        TestCase.assertEquals(0, stats.missCount());
        TestCase.assertEquals(0, stats.loadSuccessCount());
        TestCase.assertEquals(0, stats.loadExceptionCount());
        TestCase.assertEquals(0, stats.hitCount());
        TestCase.assertSame(one, cache.getUnchecked(key));
        stats = cache.stats();
        TestCase.assertEquals(1, stats.missCount());
        TestCase.assertEquals(1, stats.loadSuccessCount());
        TestCase.assertEquals(0, stats.loadExceptionCount());
        TestCase.assertEquals(0, stats.hitCount());
        ticker.advance(1, TimeUnit.MILLISECONDS);
        TestCase.assertSame(one, cache.getUnchecked(key));
        stats = cache.stats();
        TestCase.assertEquals(1, stats.missCount());
        TestCase.assertEquals(1, stats.loadSuccessCount());
        TestCase.assertEquals(0, stats.loadExceptionCount());
        TestCase.assertEquals(1, stats.hitCount());
        ticker.advance(1, TimeUnit.MILLISECONDS);
        cache.getUnchecked(key);// Allow refresh to return old value while refreshing

        TestCase.assertSame(two, cache.getUnchecked(key));
        stats = cache.stats();
        TestCase.assertEquals(1, stats.missCount());
        TestCase.assertEquals(2, stats.loadSuccessCount());
        TestCase.assertEquals(0, stats.loadExceptionCount());
        TestCase.assertEquals(3, stats.hitCount());
        ticker.advance(1, TimeUnit.MILLISECONDS);
        TestCase.assertSame(two, cache.getUnchecked(key));
        stats = cache.stats();
        TestCase.assertEquals(1, stats.missCount());
        TestCase.assertEquals(2, stats.loadSuccessCount());
        TestCase.assertEquals(0, stats.loadExceptionCount());
        TestCase.assertEquals(4, stats.hitCount());
    }

    public void testRefresh_getIfPresent() {
        final Object one = new Object();
        final Object two = new Object();
        FakeTicker ticker = new FakeTicker();
        CacheLoader<Object, Object> loader = new CacheLoader<Object, Object>() {
            @Override
            public Object load(Object key) {
                return one;
            }

            @Override
            public ListenableFuture<Object> reload(Object key, Object oldValue) {
                return Futures.immediateFuture(two);
            }
        };
        LoadingCache<Object, Object> cache = CaffeinatedGuava.build(Caffeine.newBuilder().recordStats().ticker(ticker::read).refreshAfterWrite(1, TimeUnit.MILLISECONDS).executor(MoreExecutors.directExecutor()), loader);
        Object key = new Object();
        CacheStats stats = cache.stats();
        TestCase.assertEquals(0, stats.missCount());
        TestCase.assertEquals(0, stats.loadSuccessCount());
        TestCase.assertEquals(0, stats.loadExceptionCount());
        TestCase.assertEquals(0, stats.hitCount());
        TestCase.assertSame(one, cache.getUnchecked(key));
        stats = cache.stats();
        TestCase.assertEquals(1, stats.missCount());
        TestCase.assertEquals(1, stats.loadSuccessCount());
        TestCase.assertEquals(0, stats.loadExceptionCount());
        TestCase.assertEquals(0, stats.hitCount());
        ticker.advance(1, TimeUnit.MILLISECONDS);
        TestCase.assertSame(one, cache.getIfPresent(key));
        stats = cache.stats();
        TestCase.assertEquals(1, stats.missCount());
        TestCase.assertEquals(1, stats.loadSuccessCount());
        TestCase.assertEquals(0, stats.loadExceptionCount());
        TestCase.assertEquals(1, stats.hitCount());
        ticker.advance(1, TimeUnit.MILLISECONDS);
        TestCase.assertSame(one, cache.getIfPresent(key));
        stats = cache.stats();
        TestCase.assertEquals(1, stats.missCount());
        TestCase.assertEquals(2, stats.loadSuccessCount());
        TestCase.assertEquals(0, stats.loadExceptionCount());
        TestCase.assertEquals(2, stats.hitCount());
        ticker.advance(1, TimeUnit.MILLISECONDS);
        TestCase.assertSame(two, cache.getIfPresent(key));
        stats = cache.stats();
        TestCase.assertEquals(1, stats.missCount());
        TestCase.assertEquals(2, stats.loadSuccessCount());
        TestCase.assertEquals(0, stats.loadExceptionCount());
        TestCase.assertEquals(3, stats.hitCount());
    }

    public void testBulkLoad_default() throws ExecutionException {
        LoadingCache<Integer, Integer> cache = CaffeinatedGuava.build(Caffeine.newBuilder().recordStats(), TestingCacheLoaders.<Integer>identityLoader());
        CacheStats stats = cache.stats();
        TestCase.assertEquals(0, stats.missCount());
        TestCase.assertEquals(0, stats.loadSuccessCount());
        TestCase.assertEquals(0, stats.loadExceptionCount());
        TestCase.assertEquals(0, stats.hitCount());
        TestCase.assertEquals(ImmutableMap.of(), cache.getAll(ImmutableList.<Integer>of()));
        TestCase.assertEquals(0, stats.missCount());
        TestCase.assertEquals(0, stats.loadSuccessCount());
        TestCase.assertEquals(0, stats.loadExceptionCount());
        TestCase.assertEquals(0, stats.hitCount());
        TestCase.assertEquals(ImmutableMap.of(1, 1), cache.getAll(Arrays.asList(1)));
        stats = cache.stats();
        TestCase.assertEquals(1, stats.missCount());
        TestCase.assertEquals(1, stats.loadSuccessCount());
        TestCase.assertEquals(0, stats.loadExceptionCount());
        TestCase.assertEquals(0, stats.hitCount());
        TestCase.assertEquals(ImmutableMap.of(1, 1, 2, 2, 3, 3, 4, 4), cache.getAll(Arrays.asList(1, 2, 3, 4)));
        stats = cache.stats();
        TestCase.assertEquals(4, stats.missCount());
        TestCase.assertEquals(4, stats.loadSuccessCount());
        TestCase.assertEquals(0, stats.loadExceptionCount());
        TestCase.assertEquals(1, stats.hitCount());
        TestCase.assertEquals(ImmutableMap.of(2, 2, 3, 3), cache.getAll(Arrays.asList(2, 3)));
        stats = cache.stats();
        TestCase.assertEquals(4, stats.missCount());
        TestCase.assertEquals(4, stats.loadSuccessCount());
        TestCase.assertEquals(0, stats.loadExceptionCount());
        TestCase.assertEquals(3, stats.hitCount());
        // duplicate keys are ignored, and don't impact stats
        TestCase.assertEquals(ImmutableMap.of(4, 4, 5, 5), cache.getAll(Arrays.asList(4, 5)));
        stats = cache.stats();
        TestCase.assertEquals(5, stats.missCount());
        TestCase.assertEquals(5, stats.loadSuccessCount());
        TestCase.assertEquals(0, stats.loadExceptionCount());
        TestCase.assertEquals(4, stats.hitCount());
    }

    public void testBulkLoad_loadAll() throws ExecutionException {
        TestingCacheLoaders.IdentityLoader<Integer> backingLoader = TestingCacheLoaders.identityLoader();
        LoadingCache<Integer, Integer> cache = CaffeinatedGuava.build(Caffeine.newBuilder().recordStats().executor(MoreExecutors.directExecutor()), TestingCacheLoaders.bulkLoader(backingLoader));
        CacheStats stats = cache.stats();
        TestCase.assertEquals(0, stats.missCount());
        TestCase.assertEquals(0, stats.loadSuccessCount());
        TestCase.assertEquals(0, stats.loadExceptionCount());
        TestCase.assertEquals(0, stats.hitCount());
        TestCase.assertEquals(ImmutableMap.of(), cache.getAll(ImmutableList.<Integer>of()));
        TestCase.assertEquals(0, stats.missCount());
        TestCase.assertEquals(0, stats.loadSuccessCount());
        TestCase.assertEquals(0, stats.loadExceptionCount());
        TestCase.assertEquals(0, stats.hitCount());
        TestCase.assertEquals(ImmutableMap.of(1, 1), cache.getAll(Arrays.asList(1)));
        stats = cache.stats();
        TestCase.assertEquals(1, stats.missCount());
        TestCase.assertEquals(1, stats.loadSuccessCount());
        TestCase.assertEquals(0, stats.loadExceptionCount());
        TestCase.assertEquals(0, stats.hitCount());
        TestCase.assertEquals(ImmutableMap.of(1, 1, 2, 2, 3, 3, 4, 4), cache.getAll(Arrays.asList(1, 2, 3, 4)));
        stats = cache.stats();
        TestCase.assertEquals(4, stats.missCount());
        TestCase.assertEquals(2, stats.loadSuccessCount());
        TestCase.assertEquals(0, stats.loadExceptionCount());
        TestCase.assertEquals(1, stats.hitCount());
        TestCase.assertEquals(ImmutableMap.of(2, 2, 3, 3), cache.getAll(Arrays.asList(2, 3)));
        stats = cache.stats();
        TestCase.assertEquals(4, stats.missCount());
        TestCase.assertEquals(2, stats.loadSuccessCount());
        TestCase.assertEquals(0, stats.loadExceptionCount());
        TestCase.assertEquals(3, stats.hitCount());
        // duplicate keys are ignored, and don't impact stats
        TestCase.assertEquals(ImmutableMap.of(4, 4, 5, 5), cache.getAll(Arrays.asList(4, 5)));
        stats = cache.stats();
        TestCase.assertEquals(5, stats.missCount());
        TestCase.assertEquals(3, stats.loadSuccessCount());
        TestCase.assertEquals(0, stats.loadExceptionCount());
        TestCase.assertEquals(4, stats.hitCount());
    }

    public void testBulkLoad_extra() throws ExecutionException {
        CacheLoader<Object, Object> loader = new CacheLoader<Object, Object>() {
            @Override
            public Object load(Object key) {
                return new Object();
            }

            @Override
            public Map<Object, Object> loadAll(Iterable<? extends Object> keys) {
                Map<Object, Object> result = Maps.newHashMap();
                for (Object key : keys) {
                    Object value = new Object();
                    result.put(key, value);
                    // add extra entries
                    result.put(value, key);
                }
                return result;
            }
        };
        LoadingCache<Object, Object> cache = CaffeinatedGuava.build(Caffeine.newBuilder().executor(MoreExecutors.directExecutor()), loader);
        Object[] lookupKeys = new Object[]{ new Object(), new Object(), new Object() };
        Map<Object, Object> result = cache.getAll(Arrays.asList(lookupKeys));
        assertThat(result.keySet()).containsExactlyElementsIn(Arrays.asList(lookupKeys));
        for (Map.Entry<Object, Object> entry : result.entrySet()) {
            Object key = entry.getKey();
            Object value = entry.getValue();
            TestCase.assertSame(value, result.get(key));
            TestCase.assertNull(result.get(value));
            TestCase.assertSame(value, cache.asMap().get(key));
            TestCase.assertSame(key, cache.asMap().get(value));
        }
    }

    public void testBulkLoad_clobber() throws ExecutionException {
        final Object extraKey = new Object();
        final Object extraValue = new Object();
        CacheLoader<Object, Object> loader = new CacheLoader<Object, Object>() {
            @Override
            public Object load(Object key) {
                throw new AssertionError();
            }

            @Override
            public Map<Object, Object> loadAll(Iterable<? extends Object> keys) {
                Map<Object, Object> result = Maps.newHashMap();
                for (Object key : keys) {
                    Object value = new Object();
                    result.put(key, value);
                }
                result.put(extraKey, extraValue);
                return result;
            }
        };
        LoadingCache<Object, Object> cache = CaffeinatedGuava.build(Caffeine.newBuilder(), loader);
        cache.asMap().put(extraKey, extraKey);
        TestCase.assertSame(extraKey, cache.asMap().get(extraKey));
        Object[] lookupKeys = new Object[]{ new Object(), new Object(), new Object() };
        Map<Object, Object> result = cache.getAll(Arrays.asList(lookupKeys));
        assertThat(result.keySet()).containsExactlyElementsIn(Arrays.asList(lookupKeys));
        for (Map.Entry<Object, Object> entry : result.entrySet()) {
            Object key = entry.getKey();
            Object value = entry.getValue();
            TestCase.assertSame(value, result.get(key));
            TestCase.assertSame(value, cache.asMap().get(key));
        }
        TestCase.assertNull(result.get(extraKey));
        TestCase.assertSame(extraValue, cache.asMap().get(extraKey));
    }

    public void testBulkLoad_clobberNullValue() throws ExecutionException {
        final Object extraKey = new Object();
        final Object extraValue = new Object();
        CacheLoader<Object, Object> loader = new CacheLoader<Object, Object>() {
            @Override
            public Object load(Object key) {
                throw new AssertionError();
            }

            @Override
            public Map<Object, Object> loadAll(Iterable<? extends Object> keys) {
                Map<Object, Object> result = Maps.newHashMap();
                for (Object key : keys) {
                    Object value = new Object();
                    result.put(key, value);
                }
                result.put(extraKey, extraValue);
                result.put(extraValue, null);
                return result;
            }
        };
        LoadingCache<Object, Object> cache = CaffeinatedGuava.build(Caffeine.newBuilder(), loader);
        cache.asMap().put(extraKey, extraKey);
        TestCase.assertSame(extraKey, cache.asMap().get(extraKey));
        Object[] lookupKeys = new Object[]{ new Object(), new Object(), new Object() };
        try {
            cache.getAll(Arrays.asList(lookupKeys));
            TestCase.fail();
        } catch (CacheLoader.InvalidCacheLoadException expected) {
        }
        for (Object key : lookupKeys) {
            TestCase.assertTrue(cache.asMap().containsKey(key));
        }
        TestCase.assertSame(extraValue, cache.asMap().get(extraKey));
        TestCase.assertFalse(cache.asMap().containsKey(extraValue));
    }

    public void testBulkLoad_clobberNullKey() throws ExecutionException {
        final Object extraKey = new Object();
        final Object extraValue = new Object();
        CacheLoader<Object, Object> loader = new CacheLoader<Object, Object>() {
            @Override
            public Object load(Object key) {
                throw new AssertionError();
            }

            @Override
            public Map<Object, Object> loadAll(Iterable<? extends Object> keys) {
                Map<Object, Object> result = Maps.newHashMap();
                for (Object key : keys) {
                    Object value = new Object();
                    result.put(key, value);
                }
                result.put(extraKey, extraValue);
                result.put(null, extraKey);
                return result;
            }
        };
        LoadingCache<Object, Object> cache = CaffeinatedGuava.build(Caffeine.newBuilder(), loader);
        cache.asMap().put(extraKey, extraKey);
        TestCase.assertSame(extraKey, cache.asMap().get(extraKey));
        Object[] lookupKeys = new Object[]{ new Object(), new Object(), new Object() };
        try {
            cache.getAll(Arrays.asList(lookupKeys));
            TestCase.fail();
        } catch (CacheLoader.InvalidCacheLoadException expected) {
        }
        for (Object key : lookupKeys) {
            TestCase.assertTrue(cache.asMap().containsKey(key));
        }
        TestCase.assertSame(extraValue, cache.asMap().get(extraKey));
        TestCase.assertFalse(cache.asMap().containsValue(extraKey));
    }

    public void testBulkLoad_partial() throws ExecutionException {
        final Object extraKey = new Object();
        final Object extraValue = new Object();
        CacheLoader<Object, Object> loader = new CacheLoader<Object, Object>() {
            @Override
            public Object load(Object key) {
                throw new AssertionError();
            }

            @Override
            public Map<Object, Object> loadAll(Iterable<? extends Object> keys) {
                Map<Object, Object> result = Maps.newHashMap();
                // ignore request keys
                result.put(extraKey, extraValue);
                return result;
            }
        };
        LoadingCache<Object, Object> cache = CaffeinatedGuava.build(Caffeine.newBuilder(), loader);
        Object[] lookupKeys = new Object[]{ new Object(), new Object(), new Object() };
        try {
            cache.getAll(Arrays.asList(lookupKeys));
            TestCase.fail();
        } catch (CacheLoader.InvalidCacheLoadException expected) {
        }
        TestCase.assertSame(extraValue, cache.asMap().get(extraKey));
    }

    public void testLoadNull() throws ExecutionException {
        LoadingCache<Object, Object> cache = CaffeinatedGuava.build(Caffeine.newBuilder().executor(MoreExecutors.directExecutor()).recordStats(), TestingCacheLoaders.constantLoader(null));
        CacheStats stats = cache.stats();
        TestCase.assertEquals(0, stats.missCount());
        TestCase.assertEquals(0, stats.loadSuccessCount());
        TestCase.assertEquals(0, stats.loadExceptionCount());
        TestCase.assertEquals(0, stats.hitCount());
        try {
            cache.get(new Object());
            TestCase.fail();
        } catch (CacheLoader.InvalidCacheLoadException expected) {
        }
        stats = cache.stats();
        TestCase.assertEquals(1, stats.missCount());
        TestCase.assertEquals(0, stats.loadSuccessCount());
        TestCase.assertEquals(1, stats.loadExceptionCount());
        TestCase.assertEquals(0, stats.hitCount());
        try {
            cache.getUnchecked(new Object());
            TestCase.fail();
        } catch (CacheLoader.InvalidCacheLoadException expected) {
        }
        stats = cache.stats();
        TestCase.assertEquals(2, stats.missCount());
        TestCase.assertEquals(0, stats.loadSuccessCount());
        TestCase.assertEquals(2, stats.loadExceptionCount());
        TestCase.assertEquals(0, stats.hitCount());
        cache.refresh(new Object());
        checkLoggedInvalidLoad();
        stats = cache.stats();
        TestCase.assertEquals(2, stats.missCount());
        TestCase.assertEquals(0, stats.loadSuccessCount());
        TestCase.assertEquals(3, stats.loadExceptionCount());
        TestCase.assertEquals(0, stats.hitCount());
        try {
            cache.get(new Object(), Callables.returning(null));
            TestCase.fail();
        } catch (CacheLoader.InvalidCacheLoadException expected) {
        }
        stats = cache.stats();
        TestCase.assertEquals(3, stats.missCount());
        TestCase.assertEquals(0, stats.loadSuccessCount());
        TestCase.assertEquals(4, stats.loadExceptionCount());
        TestCase.assertEquals(0, stats.hitCount());
        try {
            cache.getAll(Arrays.asList(new Object()));
            TestCase.fail();
        } catch (CacheLoader.InvalidCacheLoadException expected) {
        }
        stats = cache.stats();
        TestCase.assertEquals(4, stats.missCount());
        TestCase.assertEquals(0, stats.loadSuccessCount());
        TestCase.assertEquals(5, stats.loadExceptionCount());
        TestCase.assertEquals(0, stats.hitCount());
    }

    public void testReloadNull() throws ExecutionException {
        final Object one = new Object();
        CacheLoader<Object, Object> loader = new CacheLoader<Object, Object>() {
            @Override
            public Object load(Object key) {
                return one;
            }

            @Override
            public ListenableFuture<Object> reload(Object key, Object oldValue) {
                return null;
            }
        };
        LoadingCache<Object, Object> cache = CaffeinatedGuava.build(Caffeine.newBuilder().recordStats().executor(MoreExecutors.directExecutor()), loader);
        Object key = new Object();
        CacheStats stats = cache.stats();
        TestCase.assertEquals(0, stats.missCount());
        TestCase.assertEquals(0, stats.loadSuccessCount());
        TestCase.assertEquals(0, stats.loadExceptionCount());
        TestCase.assertEquals(0, stats.hitCount());
        TestCase.assertSame(one, cache.getUnchecked(key));
        stats = cache.stats();
        TestCase.assertEquals(1, stats.missCount());
        TestCase.assertEquals(1, stats.loadSuccessCount());
        TestCase.assertEquals(0, stats.loadExceptionCount());
        TestCase.assertEquals(0, stats.hitCount());
        cache.refresh(key);
        checkLoggedInvalidLoad();
        stats = cache.stats();
        TestCase.assertEquals(1, stats.missCount());
        TestCase.assertEquals(1, stats.loadSuccessCount());
        TestCase.assertEquals(1, stats.loadExceptionCount());
        TestCase.assertEquals(0, stats.hitCount());
        TestCase.assertSame(one, cache.getUnchecked(key));
        stats = cache.stats();
        TestCase.assertEquals(1, stats.missCount());
        TestCase.assertEquals(1, stats.loadSuccessCount());
        TestCase.assertEquals(1, stats.loadExceptionCount());
        TestCase.assertEquals(1, stats.hitCount());
    }

    public void testReloadNullFuture() throws ExecutionException {
        final Object one = new Object();
        CacheLoader<Object, Object> loader = new CacheLoader<Object, Object>() {
            @Override
            public Object load(Object key) {
                return one;
            }

            @Override
            public ListenableFuture<Object> reload(Object key, Object oldValue) {
                return Futures.immediateFuture(null);
            }
        };
        LoadingCache<Object, Object> cache = CaffeinatedGuava.build(Caffeine.newBuilder().recordStats().executor(MoreExecutors.directExecutor()), loader);
        Object key = new Object();
        CacheStats stats = cache.stats();
        TestCase.assertEquals(0, stats.missCount());
        TestCase.assertEquals(0, stats.loadSuccessCount());
        TestCase.assertEquals(0, stats.loadExceptionCount());
        TestCase.assertEquals(0, stats.hitCount());
        TestCase.assertSame(one, cache.getUnchecked(key));
        stats = cache.stats();
        TestCase.assertEquals(1, stats.missCount());
        TestCase.assertEquals(1, stats.loadSuccessCount());
        TestCase.assertEquals(0, stats.loadExceptionCount());
        TestCase.assertEquals(0, stats.hitCount());
        cache.refresh(key);
        checkLoggedInvalidLoad();
        stats = cache.stats();
        TestCase.assertEquals(1, stats.missCount());
        TestCase.assertEquals(1, stats.loadSuccessCount());
        TestCase.assertEquals(1, stats.loadExceptionCount());
        TestCase.assertEquals(0, stats.hitCount());
        TestCase.assertSame(one, cache.getUnchecked(key));
        stats = cache.stats();
        TestCase.assertEquals(1, stats.missCount());
        TestCase.assertEquals(1, stats.loadSuccessCount());
        TestCase.assertEquals(1, stats.loadExceptionCount());
        TestCase.assertEquals(1, stats.hitCount());
    }

    public void testRefreshNull() {
        final Object one = new Object();
        FakeTicker ticker = new FakeTicker();
        CacheLoader<Object, Object> loader = new CacheLoader<Object, Object>() {
            @Override
            public Object load(Object key) {
                return one;
            }

            @Override
            public ListenableFuture<Object> reload(Object key, Object oldValue) {
                return Futures.immediateFuture(null);
            }
        };
        LoadingCache<Object, Object> cache = CaffeinatedGuava.build(Caffeine.newBuilder().recordStats().ticker(ticker::read).refreshAfterWrite(1, TimeUnit.MILLISECONDS).executor(MoreExecutors.directExecutor()), loader);
        Object key = new Object();
        CacheStats stats = cache.stats();
        TestCase.assertEquals(0, stats.missCount());
        TestCase.assertEquals(0, stats.loadSuccessCount());
        TestCase.assertEquals(0, stats.loadExceptionCount());
        TestCase.assertEquals(0, stats.hitCount());
        TestCase.assertSame(one, cache.getUnchecked(key));
        stats = cache.stats();
        TestCase.assertEquals(1, stats.missCount());
        TestCase.assertEquals(1, stats.loadSuccessCount());
        TestCase.assertEquals(0, stats.loadExceptionCount());
        TestCase.assertEquals(0, stats.hitCount());
        ticker.advance(1, TimeUnit.MILLISECONDS);
        TestCase.assertSame(one, cache.getUnchecked(key));
        stats = cache.stats();
        TestCase.assertEquals(1, stats.missCount());
        TestCase.assertEquals(1, stats.loadSuccessCount());
        TestCase.assertEquals(0, stats.loadExceptionCount());
        TestCase.assertEquals(1, stats.hitCount());
        ticker.advance(1, TimeUnit.MILLISECONDS);
        TestCase.assertSame(one, cache.getUnchecked(key));
        // refreshed
        stats = cache.stats();
        TestCase.assertEquals(1, stats.missCount());
        TestCase.assertEquals(1, stats.loadSuccessCount());
        TestCase.assertEquals(1, stats.loadExceptionCount());
        TestCase.assertEquals(2, stats.hitCount());
        ticker.advance(2, TimeUnit.MILLISECONDS);
        TestCase.assertSame(one, cache.getUnchecked(key));
        stats = cache.stats();
        TestCase.assertEquals(1, stats.missCount());
        TestCase.assertEquals(1, stats.loadSuccessCount());
        TestCase.assertEquals(2, stats.loadExceptionCount());
        TestCase.assertEquals(3, stats.hitCount());
    }

    public void testBulkLoadNull() throws ExecutionException {
        LoadingCache<Object, Object> cache = CaffeinatedGuava.build(Caffeine.newBuilder().recordStats().executor(MoreExecutors.directExecutor()), TestingCacheLoaders.bulkLoader(TestingCacheLoaders.constantLoader(null)));
        CacheStats stats = cache.stats();
        TestCase.assertEquals(0, stats.missCount());
        TestCase.assertEquals(0, stats.loadSuccessCount());
        TestCase.assertEquals(0, stats.loadExceptionCount());
        TestCase.assertEquals(0, stats.hitCount());
        try {
            cache.getAll(Arrays.asList(new Object()));
            TestCase.fail();
        } catch (CacheLoader.InvalidCacheLoadException expected) {
        }
        stats = cache.stats();
        TestCase.assertEquals(1, stats.missCount());
        TestCase.assertEquals(0, stats.loadSuccessCount());
        TestCase.assertEquals(1, stats.loadExceptionCount());
        TestCase.assertEquals(0, stats.hitCount());
    }

    public void testBulkLoadNullMap() throws ExecutionException {
        LoadingCache<Object, Object> cache = CaffeinatedGuava.build(Caffeine.newBuilder().recordStats(), new CacheLoader<Object, Object>() {
            @Override
            public Object load(Object key) {
                throw new AssertionError();
            }

            @Override
            public Map<Object, Object> loadAll(Iterable<? extends Object> keys) {
                return null;
            }
        });
        CacheStats stats = cache.stats();
        TestCase.assertEquals(0, stats.missCount());
        TestCase.assertEquals(0, stats.loadSuccessCount());
        TestCase.assertEquals(0, stats.loadExceptionCount());
        TestCase.assertEquals(0, stats.hitCount());
        try {
            cache.getAll(Arrays.asList(new Object()));
            TestCase.fail();
        } catch (CacheLoader.InvalidCacheLoadException expected) {
        }
        stats = cache.stats();
        TestCase.assertEquals(1, stats.missCount());
        TestCase.assertEquals(0, stats.loadSuccessCount());
        TestCase.assertEquals(1, stats.loadExceptionCount());
        TestCase.assertEquals(0, stats.hitCount());
    }

    public void testLoadError() throws ExecutionException {
        Error e = new Error();
        CacheLoader<Object, Object> loader = TestingCacheLoaders.errorLoader(e);
        LoadingCache<Object, Object> cache = CaffeinatedGuava.build(Caffeine.newBuilder().recordStats().executor(MoreExecutors.directExecutor()), loader);
        CacheStats stats = cache.stats();
        TestCase.assertEquals(0, stats.missCount());
        TestCase.assertEquals(0, stats.loadSuccessCount());
        TestCase.assertEquals(0, stats.loadExceptionCount());
        TestCase.assertEquals(0, stats.hitCount());
        try {
            cache.get(new Object());
            TestCase.fail();
        } catch (ExecutionError expected) {
            TestCase.assertSame(e, expected.getCause());
        }
        stats = cache.stats();
        TestCase.assertEquals(1, stats.missCount());
        TestCase.assertEquals(0, stats.loadSuccessCount());
        TestCase.assertEquals(1, stats.loadExceptionCount());
        TestCase.assertEquals(0, stats.hitCount());
        try {
            cache.getUnchecked(new Object());
            TestCase.fail();
        } catch (ExecutionError expected) {
            TestCase.assertSame(e, expected.getCause());
        }
        stats = cache.stats();
        TestCase.assertEquals(2, stats.missCount());
        TestCase.assertEquals(0, stats.loadSuccessCount());
        TestCase.assertEquals(2, stats.loadExceptionCount());
        TestCase.assertEquals(0, stats.hitCount());
        cache.refresh(new Object());
        checkLoggedCause(e);
        stats = cache.stats();
        TestCase.assertEquals(2, stats.missCount());
        TestCase.assertEquals(0, stats.loadSuccessCount());
        TestCase.assertEquals(3, stats.loadExceptionCount());
        TestCase.assertEquals(0, stats.hitCount());
        final Error callableError = new Error();
        try {
            cache.get(new Object(), new Callable<Object>() {
                @Override
                public Object call() {
                    throw callableError;
                }
            });
            TestCase.fail();
        } catch (ExecutionError expected) {
            TestCase.assertSame(callableError, expected.getCause());
        }
        stats = cache.stats();
        TestCase.assertEquals(3, stats.missCount());
        TestCase.assertEquals(0, stats.loadSuccessCount());
        TestCase.assertEquals(4, stats.loadExceptionCount());
        TestCase.assertEquals(0, stats.hitCount());
        try {
            cache.getAll(Arrays.asList(new Object()));
            TestCase.fail();
        } catch (ExecutionError expected) {
            TestCase.assertSame(e, expected.getCause());
        }
        stats = cache.stats();
        TestCase.assertEquals(4, stats.missCount());
        TestCase.assertEquals(0, stats.loadSuccessCount());
        TestCase.assertEquals(5, stats.loadExceptionCount());
        TestCase.assertEquals(0, stats.hitCount());
    }

    public void testReloadError() throws ExecutionException {
        final Object one = new Object();
        final Error e = new Error();
        CacheLoader<Object, Object> loader = new CacheLoader<Object, Object>() {
            @Override
            public Object load(Object key) {
                return one;
            }

            @Override
            public ListenableFuture<Object> reload(Object key, Object oldValue) {
                throw e;
            }
        };
        LoadingCache<Object, Object> cache = CaffeinatedGuava.build(Caffeine.newBuilder().recordStats().executor(MoreExecutors.directExecutor()), loader);
        Object key = new Object();
        CacheStats stats = cache.stats();
        TestCase.assertEquals(0, stats.missCount());
        TestCase.assertEquals(0, stats.loadSuccessCount());
        TestCase.assertEquals(0, stats.loadExceptionCount());
        TestCase.assertEquals(0, stats.hitCount());
        TestCase.assertSame(one, cache.getUnchecked(key));
        stats = cache.stats();
        TestCase.assertEquals(1, stats.missCount());
        TestCase.assertEquals(1, stats.loadSuccessCount());
        TestCase.assertEquals(0, stats.loadExceptionCount());
        TestCase.assertEquals(0, stats.hitCount());
        cache.refresh(key);
        checkLoggedCause(e);
        stats = cache.stats();
        TestCase.assertEquals(1, stats.missCount());
        TestCase.assertEquals(1, stats.loadSuccessCount());
        TestCase.assertEquals(1, stats.loadExceptionCount());
        TestCase.assertEquals(0, stats.hitCount());
        TestCase.assertSame(one, cache.getUnchecked(key));
        stats = cache.stats();
        TestCase.assertEquals(1, stats.missCount());
        TestCase.assertEquals(1, stats.loadSuccessCount());
        TestCase.assertEquals(1, stats.loadExceptionCount());
        TestCase.assertEquals(1, stats.hitCount());
    }

    public void testReloadFutureError() throws ExecutionException {
        final Object one = new Object();
        final Error e = new Error();
        CacheLoader<Object, Object> loader = new CacheLoader<Object, Object>() {
            @Override
            public Object load(Object key) {
                return one;
            }

            @Override
            public ListenableFuture<Object> reload(Object key, Object oldValue) {
                return Futures.immediateFailedFuture(e);
            }
        };
        LoadingCache<Object, Object> cache = CaffeinatedGuava.build(Caffeine.newBuilder().recordStats().executor(MoreExecutors.directExecutor()), loader);
        Object key = new Object();
        CacheStats stats = cache.stats();
        TestCase.assertEquals(0, stats.missCount());
        TestCase.assertEquals(0, stats.loadSuccessCount());
        TestCase.assertEquals(0, stats.loadExceptionCount());
        TestCase.assertEquals(0, stats.hitCount());
        TestCase.assertSame(one, cache.getUnchecked(key));
        stats = cache.stats();
        TestCase.assertEquals(1, stats.missCount());
        TestCase.assertEquals(1, stats.loadSuccessCount());
        TestCase.assertEquals(0, stats.loadExceptionCount());
        TestCase.assertEquals(0, stats.hitCount());
        cache.refresh(key);
        checkLoggedCause(e);
        stats = cache.stats();
        TestCase.assertEquals(1, stats.missCount());
        TestCase.assertEquals(1, stats.loadSuccessCount());
        TestCase.assertEquals(1, stats.loadExceptionCount());
        TestCase.assertEquals(0, stats.hitCount());
        TestCase.assertSame(one, cache.getUnchecked(key));
        stats = cache.stats();
        TestCase.assertEquals(1, stats.missCount());
        TestCase.assertEquals(1, stats.loadSuccessCount());
        TestCase.assertEquals(1, stats.loadExceptionCount());
        TestCase.assertEquals(1, stats.hitCount());
    }

    public void testRefreshError() {
        final Object one = new Object();
        final Error e = new Error();
        FakeTicker ticker = new FakeTicker();
        CacheLoader<Object, Object> loader = new CacheLoader<Object, Object>() {
            @Override
            public Object load(Object key) {
                return one;
            }

            @Override
            public ListenableFuture<Object> reload(Object key, Object oldValue) {
                return Futures.immediateFailedFuture(e);
            }
        };
        LoadingCache<Object, Object> cache = CaffeinatedGuava.build(Caffeine.newBuilder().recordStats().ticker(ticker::read).refreshAfterWrite(1, TimeUnit.MILLISECONDS).executor(MoreExecutors.directExecutor()), loader);
        Object key = new Object();
        CacheStats stats = cache.stats();
        TestCase.assertEquals(0, stats.missCount());
        TestCase.assertEquals(0, stats.loadSuccessCount());
        TestCase.assertEquals(0, stats.loadExceptionCount());
        TestCase.assertEquals(0, stats.hitCount());
        TestCase.assertSame(one, cache.getUnchecked(key));
        stats = cache.stats();
        TestCase.assertEquals(1, stats.missCount());
        TestCase.assertEquals(1, stats.loadSuccessCount());
        TestCase.assertEquals(0, stats.loadExceptionCount());
        TestCase.assertEquals(0, stats.hitCount());
        ticker.advance(1, TimeUnit.MILLISECONDS);
        TestCase.assertSame(one, cache.getUnchecked(key));
        stats = cache.stats();
        TestCase.assertEquals(1, stats.missCount());
        TestCase.assertEquals(1, stats.loadSuccessCount());
        TestCase.assertEquals(0, stats.loadExceptionCount());
        TestCase.assertEquals(1, stats.hitCount());
        ticker.advance(1, TimeUnit.MILLISECONDS);
        TestCase.assertSame(one, cache.getUnchecked(key));
        // refreshed
        stats = cache.stats();
        TestCase.assertEquals(1, stats.missCount());
        TestCase.assertEquals(1, stats.loadSuccessCount());
        TestCase.assertEquals(1, stats.loadExceptionCount());
        TestCase.assertEquals(2, stats.hitCount());
        ticker.advance(2, TimeUnit.MILLISECONDS);
        TestCase.assertSame(one, cache.getUnchecked(key));
        stats = cache.stats();
        TestCase.assertEquals(1, stats.missCount());
        TestCase.assertEquals(1, stats.loadSuccessCount());
        TestCase.assertEquals(2, stats.loadExceptionCount());
        TestCase.assertEquals(3, stats.hitCount());
    }

    public void testBulkLoadError() throws ExecutionException {
        Error e = new Error();
        CacheLoader<Object, Object> loader = TestingCacheLoaders.errorLoader(e);
        LoadingCache<Object, Object> cache = CaffeinatedGuava.build(Caffeine.newBuilder().recordStats(), TestingCacheLoaders.bulkLoader(loader));
        CacheStats stats = cache.stats();
        TestCase.assertEquals(0, stats.missCount());
        TestCase.assertEquals(0, stats.loadSuccessCount());
        TestCase.assertEquals(0, stats.loadExceptionCount());
        TestCase.assertEquals(0, stats.hitCount());
        try {
            cache.getAll(Arrays.asList(new Object()));
            TestCase.fail();
        } catch (ExecutionError expected) {
            TestCase.assertSame(e, expected.getCause());
        }
        stats = cache.stats();
        TestCase.assertEquals(1, stats.missCount());
        TestCase.assertEquals(0, stats.loadSuccessCount());
        TestCase.assertEquals(1, stats.loadExceptionCount());
        TestCase.assertEquals(0, stats.hitCount());
    }

    public void testLoadCheckedException() {
        Exception e = new Exception();
        CacheLoader<Object, Object> loader = TestingCacheLoaders.exceptionLoader(e);
        LoadingCache<Object, Object> cache = CaffeinatedGuava.build(Caffeine.newBuilder().recordStats().executor(MoreExecutors.directExecutor()), loader);
        CacheStats stats = cache.stats();
        TestCase.assertEquals(0, stats.missCount());
        TestCase.assertEquals(0, stats.loadSuccessCount());
        TestCase.assertEquals(0, stats.loadExceptionCount());
        TestCase.assertEquals(0, stats.hitCount());
        try {
            cache.get(new Object());
            TestCase.fail();
        } catch (ExecutionException expected) {
            TestCase.assertSame(e, expected.getCause());
        }
        stats = cache.stats();
        TestCase.assertEquals(1, stats.missCount());
        TestCase.assertEquals(0, stats.loadSuccessCount());
        TestCase.assertEquals(1, stats.loadExceptionCount());
        TestCase.assertEquals(0, stats.hitCount());
        try {
            cache.getUnchecked(new Object());
            TestCase.fail();
        } catch (UncheckedExecutionException expected) {
            TestCase.assertSame(e, expected.getCause());
        }
        stats = cache.stats();
        TestCase.assertEquals(2, stats.missCount());
        TestCase.assertEquals(0, stats.loadSuccessCount());
        TestCase.assertEquals(2, stats.loadExceptionCount());
        TestCase.assertEquals(0, stats.hitCount());
        cache.refresh(new Object());
        checkLoggedCause(e);
        stats = cache.stats();
        TestCase.assertEquals(2, stats.missCount());
        TestCase.assertEquals(0, stats.loadSuccessCount());
        TestCase.assertEquals(3, stats.loadExceptionCount());
        TestCase.assertEquals(0, stats.hitCount());
        Exception callableException = new Exception();
        try {
            cache.get(new Object(), CacheLoadingTest.throwing(callableException));
            TestCase.fail();
        } catch (ExecutionException expected) {
            TestCase.assertSame(callableException, expected.getCause());
        }
        stats = cache.stats();
        TestCase.assertEquals(3, stats.missCount());
        TestCase.assertEquals(0, stats.loadSuccessCount());
        TestCase.assertEquals(4, stats.loadExceptionCount());
        TestCase.assertEquals(0, stats.hitCount());
        try {
            cache.getAll(Arrays.asList(new Object()));
            TestCase.fail();
        } catch (ExecutionException expected) {
            TestCase.assertSame(e, expected.getCause());
        }
        stats = cache.stats();
        TestCase.assertEquals(4, stats.missCount());
        TestCase.assertEquals(0, stats.loadSuccessCount());
        TestCase.assertEquals(5, stats.loadExceptionCount());
        TestCase.assertEquals(0, stats.hitCount());
    }

    public void testLoadInterruptedException() {
        Exception e = new InterruptedException();
        CacheLoader<Object, Object> loader = TestingCacheLoaders.exceptionLoader(e);
        LoadingCache<Object, Object> cache = CaffeinatedGuava.build(Caffeine.newBuilder().recordStats().executor(MoreExecutors.directExecutor()), loader);
        CacheStats stats = cache.stats();
        TestCase.assertEquals(0, stats.missCount());
        TestCase.assertEquals(0, stats.loadSuccessCount());
        TestCase.assertEquals(0, stats.loadExceptionCount());
        TestCase.assertEquals(0, stats.hitCount());
        // Sanity check:
        TestCase.assertFalse(Thread.interrupted());
        try {
            cache.get(new Object());
            TestCase.fail();
        } catch (ExecutionException expected) {
            TestCase.assertSame(e, expected.getCause());
        }
        TestCase.assertTrue(Thread.interrupted());
        stats = cache.stats();
        TestCase.assertEquals(1, stats.missCount());
        TestCase.assertEquals(0, stats.loadSuccessCount());
        TestCase.assertEquals(1, stats.loadExceptionCount());
        TestCase.assertEquals(0, stats.hitCount());
        try {
            cache.getUnchecked(new Object());
            TestCase.fail();
        } catch (UncheckedExecutionException expected) {
            TestCase.assertSame(e, expected.getCause());
        }
        TestCase.assertTrue(Thread.interrupted());
        stats = cache.stats();
        TestCase.assertEquals(2, stats.missCount());
        TestCase.assertEquals(0, stats.loadSuccessCount());
        TestCase.assertEquals(2, stats.loadExceptionCount());
        TestCase.assertEquals(0, stats.hitCount());
        cache.refresh(new Object());
        TestCase.assertTrue(Thread.interrupted());
        checkLoggedCause(e);
        stats = cache.stats();
        TestCase.assertEquals(2, stats.missCount());
        TestCase.assertEquals(0, stats.loadSuccessCount());
        TestCase.assertEquals(3, stats.loadExceptionCount());
        TestCase.assertEquals(0, stats.hitCount());
        Exception callableException = new InterruptedException();
        try {
            cache.get(new Object(), CacheLoadingTest.throwing(callableException));
            TestCase.fail();
        } catch (ExecutionException expected) {
            TestCase.assertSame(callableException, expected.getCause());
        }
        TestCase.assertTrue(Thread.interrupted());
        stats = cache.stats();
        TestCase.assertEquals(3, stats.missCount());
        TestCase.assertEquals(0, stats.loadSuccessCount());
        TestCase.assertEquals(4, stats.loadExceptionCount());
        TestCase.assertEquals(0, stats.hitCount());
        try {
            cache.getAll(Arrays.asList(new Object()));
            TestCase.fail();
        } catch (ExecutionException expected) {
            TestCase.assertSame(e, expected.getCause());
        }
        TestCase.assertTrue(Thread.interrupted());
        stats = cache.stats();
        TestCase.assertEquals(4, stats.missCount());
        TestCase.assertEquals(0, stats.loadSuccessCount());
        TestCase.assertEquals(5, stats.loadExceptionCount());
        TestCase.assertEquals(0, stats.hitCount());
    }

    public void testReloadCheckedException() {
        final Object one = new Object();
        final RuntimeException e = new RuntimeException();
        CacheLoader<Object, Object> loader = new CacheLoader<Object, Object>() {
            @Override
            public Object load(Object key) {
                return one;
            }

            @Override
            public ListenableFuture<Object> reload(Object key, Object oldValue) {
                throw e;
            }
        };
        LoadingCache<Object, Object> cache = CaffeinatedGuava.build(Caffeine.newBuilder().recordStats().executor(MoreExecutors.directExecutor()), loader);
        Object key = new Object();
        CacheStats stats = cache.stats();
        TestCase.assertEquals(0, stats.missCount());
        TestCase.assertEquals(0, stats.loadSuccessCount());
        TestCase.assertEquals(0, stats.loadExceptionCount());
        TestCase.assertEquals(0, stats.hitCount());
        TestCase.assertSame(one, cache.getUnchecked(key));
        stats = cache.stats();
        TestCase.assertEquals(1, stats.missCount());
        TestCase.assertEquals(1, stats.loadSuccessCount());
        TestCase.assertEquals(0, stats.loadExceptionCount());
        TestCase.assertEquals(0, stats.hitCount());
        cache.refresh(key);
        checkLoggedCause(e);
        stats = cache.stats();
        TestCase.assertEquals(1, stats.missCount());
        TestCase.assertEquals(1, stats.loadSuccessCount());
        TestCase.assertEquals(1, stats.loadExceptionCount());
        TestCase.assertEquals(0, stats.hitCount());
        TestCase.assertSame(one, cache.getUnchecked(key));
        stats = cache.stats();
        TestCase.assertEquals(1, stats.missCount());
        TestCase.assertEquals(1, stats.loadSuccessCount());
        TestCase.assertEquals(1, stats.loadExceptionCount());
        TestCase.assertEquals(1, stats.hitCount());
    }

    public void testReloadFutureCheckedException() {
        final Object one = new Object();
        final Exception e = new Exception();
        CacheLoader<Object, Object> loader = new CacheLoader<Object, Object>() {
            @Override
            public Object load(Object key) {
                return one;
            }

            @Override
            public ListenableFuture<Object> reload(Object key, Object oldValue) {
                return Futures.immediateFailedFuture(e);
            }
        };
        LoadingCache<Object, Object> cache = CaffeinatedGuava.build(Caffeine.newBuilder().recordStats().executor(MoreExecutors.directExecutor()), loader);
        Object key = new Object();
        CacheStats stats = cache.stats();
        TestCase.assertEquals(0, stats.missCount());
        TestCase.assertEquals(0, stats.loadSuccessCount());
        TestCase.assertEquals(0, stats.loadExceptionCount());
        TestCase.assertEquals(0, stats.hitCount());
        TestCase.assertSame(one, cache.getUnchecked(key));
        stats = cache.stats();
        TestCase.assertEquals(1, stats.missCount());
        TestCase.assertEquals(1, stats.loadSuccessCount());
        TestCase.assertEquals(0, stats.loadExceptionCount());
        TestCase.assertEquals(0, stats.hitCount());
        cache.refresh(key);
        checkLoggedCause(e);
        stats = cache.stats();
        TestCase.assertEquals(1, stats.missCount());
        TestCase.assertEquals(1, stats.loadSuccessCount());
        TestCase.assertEquals(1, stats.loadExceptionCount());
        TestCase.assertEquals(0, stats.hitCount());
        TestCase.assertSame(one, cache.getUnchecked(key));
        stats = cache.stats();
        TestCase.assertEquals(1, stats.missCount());
        TestCase.assertEquals(1, stats.loadSuccessCount());
        TestCase.assertEquals(1, stats.loadExceptionCount());
        TestCase.assertEquals(1, stats.hitCount());
    }

    public void testRefreshCheckedException() {
        final Object one = new Object();
        final Exception e = new Exception();
        FakeTicker ticker = new FakeTicker();
        CacheLoader<Object, Object> loader = new CacheLoader<Object, Object>() {
            @Override
            public Object load(Object key) {
                return one;
            }

            @Override
            public ListenableFuture<Object> reload(Object key, Object oldValue) {
                return Futures.immediateFailedFuture(e);
            }
        };
        LoadingCache<Object, Object> cache = CaffeinatedGuava.build(Caffeine.newBuilder().recordStats().ticker(ticker::read).refreshAfterWrite(1, TimeUnit.MILLISECONDS).executor(MoreExecutors.directExecutor()), loader);
        Object key = new Object();
        CacheStats stats = cache.stats();
        TestCase.assertEquals(0, stats.missCount());
        TestCase.assertEquals(0, stats.loadSuccessCount());
        TestCase.assertEquals(0, stats.loadExceptionCount());
        TestCase.assertEquals(0, stats.hitCount());
        TestCase.assertSame(one, cache.getUnchecked(key));
        stats = cache.stats();
        TestCase.assertEquals(1, stats.missCount());
        TestCase.assertEquals(1, stats.loadSuccessCount());
        TestCase.assertEquals(0, stats.loadExceptionCount());
        TestCase.assertEquals(0, stats.hitCount());
        ticker.advance(1, TimeUnit.MILLISECONDS);
        TestCase.assertSame(one, cache.getUnchecked(key));
        stats = cache.stats();
        TestCase.assertEquals(1, stats.missCount());
        TestCase.assertEquals(1, stats.loadSuccessCount());
        TestCase.assertEquals(0, stats.loadExceptionCount());
        TestCase.assertEquals(1, stats.hitCount());
        ticker.advance(1, TimeUnit.MILLISECONDS);
        TestCase.assertSame(one, cache.getUnchecked(key));
        // refreshed
        stats = cache.stats();
        TestCase.assertEquals(1, stats.missCount());
        TestCase.assertEquals(1, stats.loadSuccessCount());
        TestCase.assertEquals(1, stats.loadExceptionCount());
        TestCase.assertEquals(2, stats.hitCount());
        ticker.advance(2, TimeUnit.MILLISECONDS);
        TestCase.assertSame(one, cache.getUnchecked(key));
        stats = cache.stats();
        TestCase.assertEquals(1, stats.missCount());
        TestCase.assertEquals(1, stats.loadSuccessCount());
        TestCase.assertEquals(2, stats.loadExceptionCount());
        TestCase.assertEquals(3, stats.hitCount());
    }

    public void testBulkLoadCheckedException() {
        Exception e = new Exception();
        CacheLoader<Object, Object> loader = TestingCacheLoaders.exceptionLoader(e);
        LoadingCache<Object, Object> cache = CaffeinatedGuava.build(Caffeine.newBuilder().recordStats(), TestingCacheLoaders.bulkLoader(loader));
        CacheStats stats = cache.stats();
        TestCase.assertEquals(0, stats.missCount());
        TestCase.assertEquals(0, stats.loadSuccessCount());
        TestCase.assertEquals(0, stats.loadExceptionCount());
        TestCase.assertEquals(0, stats.hitCount());
        try {
            cache.getAll(Arrays.asList(new Object()));
            TestCase.fail();
        } catch (ExecutionException expected) {
            TestCase.assertSame(e, expected.getCause());
        }
        stats = cache.stats();
        TestCase.assertEquals(1, stats.missCount());
        TestCase.assertEquals(0, stats.loadSuccessCount());
        TestCase.assertEquals(1, stats.loadExceptionCount());
        TestCase.assertEquals(0, stats.hitCount());
    }

    public void testBulkLoadInterruptedException() {
        Exception e = new InterruptedException();
        CacheLoader<Object, Object> loader = TestingCacheLoaders.exceptionLoader(e);
        LoadingCache<Object, Object> cache = CaffeinatedGuava.build(Caffeine.newBuilder().recordStats().executor(MoreExecutors.directExecutor()), TestingCacheLoaders.bulkLoader(loader));
        CacheStats stats = cache.stats();
        TestCase.assertEquals(0, stats.missCount());
        TestCase.assertEquals(0, stats.loadSuccessCount());
        TestCase.assertEquals(0, stats.loadExceptionCount());
        TestCase.assertEquals(0, stats.hitCount());
        try {
            cache.getAll(Arrays.asList(new Object()));
            TestCase.fail();
        } catch (ExecutionException expected) {
            TestCase.assertSame(e, expected.getCause());
        }
        TestCase.assertTrue(Thread.interrupted());
        stats = cache.stats();
        TestCase.assertEquals(1, stats.missCount());
        TestCase.assertEquals(0, stats.loadSuccessCount());
        TestCase.assertEquals(1, stats.loadExceptionCount());
        TestCase.assertEquals(0, stats.hitCount());
    }

    public void testLoadUncheckedException() throws ExecutionException {
        Exception e = new RuntimeException();
        CacheLoader<Object, Object> loader = TestingCacheLoaders.exceptionLoader(e);
        LoadingCache<Object, Object> cache = CaffeinatedGuava.build(Caffeine.newBuilder().recordStats().executor(MoreExecutors.directExecutor()), loader);
        CacheStats stats = cache.stats();
        TestCase.assertEquals(0, stats.missCount());
        TestCase.assertEquals(0, stats.loadSuccessCount());
        TestCase.assertEquals(0, stats.loadExceptionCount());
        TestCase.assertEquals(0, stats.hitCount());
        try {
            cache.get(new Object());
            TestCase.fail();
        } catch (UncheckedExecutionException expected) {
            TestCase.assertSame(e, expected.getCause());
        }
        stats = cache.stats();
        TestCase.assertEquals(1, stats.missCount());
        TestCase.assertEquals(0, stats.loadSuccessCount());
        TestCase.assertEquals(1, stats.loadExceptionCount());
        TestCase.assertEquals(0, stats.hitCount());
        try {
            cache.getUnchecked(new Object());
            TestCase.fail();
        } catch (UncheckedExecutionException expected) {
            TestCase.assertSame(e, expected.getCause());
        }
        stats = cache.stats();
        TestCase.assertEquals(2, stats.missCount());
        TestCase.assertEquals(0, stats.loadSuccessCount());
        TestCase.assertEquals(2, stats.loadExceptionCount());
        TestCase.assertEquals(0, stats.hitCount());
        cache.refresh(new Object());
        checkLoggedCause(e);
        stats = cache.stats();
        TestCase.assertEquals(2, stats.missCount());
        TestCase.assertEquals(0, stats.loadSuccessCount());
        TestCase.assertEquals(3, stats.loadExceptionCount());
        TestCase.assertEquals(0, stats.hitCount());
        Exception callableException = new RuntimeException();
        try {
            cache.get(new Object(), CacheLoadingTest.throwing(callableException));
            TestCase.fail();
        } catch (UncheckedExecutionException expected) {
            TestCase.assertSame(callableException, expected.getCause());
        }
        stats = cache.stats();
        TestCase.assertEquals(3, stats.missCount());
        TestCase.assertEquals(0, stats.loadSuccessCount());
        TestCase.assertEquals(4, stats.loadExceptionCount());
        TestCase.assertEquals(0, stats.hitCount());
        try {
            cache.getAll(Arrays.asList(new Object()));
            TestCase.fail();
        } catch (UncheckedExecutionException expected) {
            TestCase.assertSame(e, expected.getCause());
        }
        stats = cache.stats();
        TestCase.assertEquals(4, stats.missCount());
        TestCase.assertEquals(0, stats.loadSuccessCount());
        TestCase.assertEquals(5, stats.loadExceptionCount());
        TestCase.assertEquals(0, stats.hitCount());
    }

    public void testReloadUncheckedException() throws ExecutionException {
        final Object one = new Object();
        final RuntimeException e = new RuntimeException();
        CacheLoader<Object, Object> loader = new CacheLoader<Object, Object>() {
            @Override
            public Object load(Object key) {
                return one;
            }

            @Override
            public ListenableFuture<Object> reload(Object key, Object oldValue) {
                throw e;
            }
        };
        LoadingCache<Object, Object> cache = CaffeinatedGuava.build(Caffeine.newBuilder().recordStats().executor(MoreExecutors.directExecutor()), loader);
        Object key = new Object();
        CacheStats stats = cache.stats();
        TestCase.assertEquals(0, stats.missCount());
        TestCase.assertEquals(0, stats.loadSuccessCount());
        TestCase.assertEquals(0, stats.loadExceptionCount());
        TestCase.assertEquals(0, stats.hitCount());
        TestCase.assertSame(one, cache.getUnchecked(key));
        stats = cache.stats();
        TestCase.assertEquals(1, stats.missCount());
        TestCase.assertEquals(1, stats.loadSuccessCount());
        TestCase.assertEquals(0, stats.loadExceptionCount());
        TestCase.assertEquals(0, stats.hitCount());
        cache.refresh(key);
        checkLoggedCause(e);
        stats = cache.stats();
        TestCase.assertEquals(1, stats.missCount());
        TestCase.assertEquals(1, stats.loadSuccessCount());
        TestCase.assertEquals(1, stats.loadExceptionCount());
        TestCase.assertEquals(0, stats.hitCount());
        TestCase.assertSame(one, cache.getUnchecked(key));
        stats = cache.stats();
        TestCase.assertEquals(1, stats.missCount());
        TestCase.assertEquals(1, stats.loadSuccessCount());
        TestCase.assertEquals(1, stats.loadExceptionCount());
        TestCase.assertEquals(1, stats.hitCount());
    }

    public void testReloadFutureUncheckedException() throws ExecutionException {
        final Object one = new Object();
        final Exception e = new RuntimeException();
        CacheLoader<Object, Object> loader = new CacheLoader<Object, Object>() {
            @Override
            public Object load(Object key) {
                return one;
            }

            @Override
            public ListenableFuture<Object> reload(Object key, Object oldValue) {
                return Futures.immediateFailedFuture(e);
            }
        };
        LoadingCache<Object, Object> cache = CaffeinatedGuava.build(Caffeine.newBuilder().recordStats().executor(MoreExecutors.directExecutor()), loader);
        Object key = new Object();
        CacheStats stats = cache.stats();
        TestCase.assertEquals(0, stats.missCount());
        TestCase.assertEquals(0, stats.loadSuccessCount());
        TestCase.assertEquals(0, stats.loadExceptionCount());
        TestCase.assertEquals(0, stats.hitCount());
        TestCase.assertSame(one, cache.getUnchecked(key));
        stats = cache.stats();
        TestCase.assertEquals(1, stats.missCount());
        TestCase.assertEquals(1, stats.loadSuccessCount());
        TestCase.assertEquals(0, stats.loadExceptionCount());
        TestCase.assertEquals(0, stats.hitCount());
        cache.refresh(key);
        checkLoggedCause(e);
        stats = cache.stats();
        TestCase.assertEquals(1, stats.missCount());
        TestCase.assertEquals(1, stats.loadSuccessCount());
        TestCase.assertEquals(1, stats.loadExceptionCount());
        TestCase.assertEquals(0, stats.hitCount());
        TestCase.assertSame(one, cache.getUnchecked(key));
        stats = cache.stats();
        TestCase.assertEquals(1, stats.missCount());
        TestCase.assertEquals(1, stats.loadSuccessCount());
        TestCase.assertEquals(1, stats.loadExceptionCount());
        TestCase.assertEquals(1, stats.hitCount());
    }

    public void testRefreshUncheckedException() {
        final Object one = new Object();
        final Exception e = new RuntimeException();
        FakeTicker ticker = new FakeTicker();
        CacheLoader<Object, Object> loader = new CacheLoader<Object, Object>() {
            @Override
            public Object load(Object key) {
                return one;
            }

            @Override
            public ListenableFuture<Object> reload(Object key, Object oldValue) {
                return Futures.immediateFailedFuture(e);
            }
        };
        LoadingCache<Object, Object> cache = CaffeinatedGuava.build(Caffeine.newBuilder().recordStats().ticker(ticker::read).refreshAfterWrite(1, TimeUnit.MILLISECONDS).executor(MoreExecutors.directExecutor()), loader);
        Object key = new Object();
        CacheStats stats = cache.stats();
        TestCase.assertEquals(0, stats.missCount());
        TestCase.assertEquals(0, stats.loadSuccessCount());
        TestCase.assertEquals(0, stats.loadExceptionCount());
        TestCase.assertEquals(0, stats.hitCount());
        TestCase.assertSame(one, cache.getUnchecked(key));
        stats = cache.stats();
        TestCase.assertEquals(1, stats.missCount());
        TestCase.assertEquals(1, stats.loadSuccessCount());
        TestCase.assertEquals(0, stats.loadExceptionCount());
        TestCase.assertEquals(0, stats.hitCount());
        ticker.advance(1, TimeUnit.MILLISECONDS);
        TestCase.assertSame(one, cache.getUnchecked(key));
        stats = cache.stats();
        TestCase.assertEquals(1, stats.missCount());
        TestCase.assertEquals(1, stats.loadSuccessCount());
        TestCase.assertEquals(0, stats.loadExceptionCount());
        TestCase.assertEquals(1, stats.hitCount());
        ticker.advance(1, TimeUnit.MILLISECONDS);
        TestCase.assertSame(one, cache.getUnchecked(key));
        // refreshed
        stats = cache.stats();
        TestCase.assertEquals(1, stats.missCount());
        TestCase.assertEquals(1, stats.loadSuccessCount());
        TestCase.assertEquals(1, stats.loadExceptionCount());
        TestCase.assertEquals(2, stats.hitCount());
        ticker.advance(2, TimeUnit.MILLISECONDS);
        TestCase.assertSame(one, cache.getUnchecked(key));
        stats = cache.stats();
        TestCase.assertEquals(1, stats.missCount());
        TestCase.assertEquals(1, stats.loadSuccessCount());
        TestCase.assertEquals(2, stats.loadExceptionCount());
        TestCase.assertEquals(3, stats.hitCount());
    }

    public void testBulkLoadUncheckedException() throws ExecutionException {
        Exception e = new RuntimeException();
        CacheLoader<Object, Object> loader = TestingCacheLoaders.exceptionLoader(e);
        LoadingCache<Object, Object> cache = CaffeinatedGuava.build(Caffeine.newBuilder().recordStats(), TestingCacheLoaders.bulkLoader(loader));
        CacheStats stats = cache.stats();
        TestCase.assertEquals(0, stats.missCount());
        TestCase.assertEquals(0, stats.loadSuccessCount());
        TestCase.assertEquals(0, stats.loadExceptionCount());
        TestCase.assertEquals(0, stats.hitCount());
        try {
            cache.getAll(Arrays.asList(new Object()));
            TestCase.fail();
        } catch (UncheckedExecutionException expected) {
            TestCase.assertSame(e, expected.getCause());
        }
        stats = cache.stats();
        TestCase.assertEquals(1, stats.missCount());
        TestCase.assertEquals(0, stats.loadSuccessCount());
        TestCase.assertEquals(1, stats.loadExceptionCount());
        TestCase.assertEquals(0, stats.hitCount());
    }

    public void testReloadAfterFailure() throws ExecutionException {
        final AtomicInteger count = new AtomicInteger();
        final RuntimeException e = new IllegalStateException("exception to trigger failure on first load()");
        CacheLoader<Integer, String> failOnceFunction = new CacheLoader<Integer, String>() {
            @Override
            public String load(Integer key) {
                if ((count.getAndIncrement()) == 0) {
                    throw e;
                }
                return key.toString();
            }
        };
        TestingRemovalListeners.CountingRemovalListener<Integer, String> removalListener = TestingRemovalListeners.countingRemovalListener();
        LoadingCache<Integer, String> cache = CaffeinatedGuava.build(Caffeine.newBuilder().removalListener(removalListener).executor(MoreExecutors.directExecutor()), failOnceFunction);
        try {
            cache.getUnchecked(1);
            TestCase.fail();
        } catch (UncheckedExecutionException ue) {
            TestCase.assertSame(e, ue.getCause());
        }
        TestCase.assertEquals("1", cache.getUnchecked(1));
        TestCase.assertEquals(0, removalListener.getCount());
        count.set(0);
        cache.refresh(2);
        checkLoggedCause(e);
        TestCase.assertEquals("2", cache.getUnchecked(2));
        TestCase.assertEquals(0, removalListener.getCount());
    }

    /**
     * Make sure LoadingCache correctly wraps ExecutionExceptions and UncheckedExecutionExceptions.
     */
    public void testLoadingExceptionWithCause() throws ExecutionException {
        final Exception cause = new Exception();
        final UncheckedExecutionException uee = new UncheckedExecutionException(cause);
        final ExecutionException ee = new ExecutionException(cause);
        LoadingCache<Object, Object> cacheUnchecked = CaffeinatedGuava.build(Caffeine.newBuilder(), TestingCacheLoaders.exceptionLoader(uee));
        LoadingCache<Object, Object> cacheChecked = CaffeinatedGuava.build(Caffeine.newBuilder(), TestingCacheLoaders.exceptionLoader(ee));
        try {
            cacheUnchecked.get(new Object());
            TestCase.fail();
        } catch (UncheckedExecutionException caughtEe) {
            TestCase.assertSame(uee, caughtEe.getCause());
        }
        try {
            cacheUnchecked.getUnchecked(new Object());
            TestCase.fail();
        } catch (UncheckedExecutionException caughtUee) {
            TestCase.assertSame(uee, caughtUee.getCause());
        }
        cacheUnchecked.refresh(new Object());
        checkLoggedCause(uee);
        try {
            cacheUnchecked.getAll(Arrays.asList(new Object()));
            TestCase.fail();
        } catch (UncheckedExecutionException caughtEe) {
            TestCase.assertSame(uee, caughtEe.getCause());
        }
        try {
            cacheChecked.get(new Object());
            TestCase.fail();
        } catch (ExecutionException caughtEe) {
            TestCase.assertSame(ee, caughtEe.getCause());
        }
        try {
            cacheChecked.getUnchecked(new Object());
            TestCase.fail();
        } catch (UncheckedExecutionException caughtUee) {
            TestCase.assertSame(ee, caughtUee.getCause());
        }
        cacheChecked.refresh(new Object());
        checkLoggedCause(ee);
        try {
            cacheChecked.getAll(Arrays.asList(new Object()));
            TestCase.fail();
        } catch (ExecutionException caughtEe) {
            TestCase.assertSame(ee, caughtEe.getCause());
        }
    }

    public void testBulkLoadingExceptionWithCause() throws ExecutionException {
        final Exception cause = new Exception();
        final UncheckedExecutionException uee = new UncheckedExecutionException(cause);
        final ExecutionException ee = new ExecutionException(cause);
        LoadingCache<Object, Object> cacheUnchecked = CaffeinatedGuava.build(Caffeine.newBuilder(), TestingCacheLoaders.bulkLoader(TestingCacheLoaders.exceptionLoader(uee)));
        LoadingCache<Object, Object> cacheChecked = CaffeinatedGuava.build(Caffeine.newBuilder(), TestingCacheLoaders.bulkLoader(TestingCacheLoaders.exceptionLoader(ee)));
        try {
            cacheUnchecked.getAll(Arrays.asList(new Object()));
            TestCase.fail();
        } catch (UncheckedExecutionException caughtEe) {
            TestCase.assertSame(uee, caughtEe.getCause());
        }
        try {
            cacheChecked.getAll(Arrays.asList(new Object()));
            TestCase.fail();
        } catch (ExecutionException caughtEe) {
            TestCase.assertSame(ee, caughtEe.getCause());
        }
    }

    public void testConcurrentLoading() throws InterruptedException {
        CacheLoadingTest.testConcurrentLoading(Caffeine.newBuilder());
    }

    public void testConcurrentExpirationLoading() throws InterruptedException {
        CacheLoadingTest.testConcurrentLoading(Caffeine.newBuilder().expireAfterWrite(10, TimeUnit.SECONDS).executor(MoreExecutors.directExecutor()));
    }

    public void testAsMapDuringLoading() throws InterruptedException, ExecutionException {
        final CountDownLatch getStartedSignal = new CountDownLatch(2);
        final CountDownLatch letGetFinishSignal = new CountDownLatch(1);
        final CountDownLatch getFinishedSignal = new CountDownLatch(2);
        final String getKey = "get";
        final String refreshKey = "refresh";
        final String suffix = "Suffix";
        CacheLoader<String, String> computeFunction = new CacheLoader<String, String>() {
            @Override
            public String load(String key) {
                getStartedSignal.countDown();
                TestCase.assertTrue(Uninterruptibles.awaitUninterruptibly(letGetFinishSignal, 300, TimeUnit.SECONDS));
                return key + suffix;
            }
        };
        final LoadingCache<String, String> cache = CaffeinatedGuava.build(Caffeine.newBuilder().initialCapacity(1000).executor(MoreExecutors.directExecutor()), computeFunction);
        ConcurrentMap<String, String> map = cache.asMap();
        map.put(refreshKey, refreshKey);
        TestCase.assertEquals(1, map.size());
        TestCase.assertFalse(map.containsKey(getKey));
        TestCase.assertSame(refreshKey, map.get(refreshKey));
        new Thread(() -> {
            cache.getUnchecked(getKey);
            getFinishedSignal.countDown();
        }).start();
        new Thread(() -> {
            cache.refresh(refreshKey);
            getFinishedSignal.countDown();
        }).start();
        TestCase.assertTrue(getStartedSignal.await(300, TimeUnit.SECONDS));
        // computation is in progress; asMap shouldn't have changed
        TestCase.assertEquals(1, map.size());
        TestCase.assertFalse(map.containsKey(getKey));
        TestCase.assertSame(refreshKey, map.get(refreshKey));
        // let computation complete
        letGetFinishSignal.countDown();
        TestCase.assertTrue(getFinishedSignal.await(300, TimeUnit.SECONDS));
        checkNothingLogged();
        // asMap view should have been updated
        TestCase.assertEquals(2, cache.size());
        TestCase.assertEquals((getKey + suffix), map.get(getKey));
        TestCase.assertEquals((refreshKey + suffix), map.get(refreshKey));
    }
}

