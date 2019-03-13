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
import com.hazelcast.config.CacheConfig;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.test.HazelcastParallelClassRunner;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.TestHazelcastInstanceFactory;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import javax.cache.Cache;
import javax.cache.CacheManager;
import javax.cache.configuration.FactoryBuilder;
import javax.cache.integration.CacheLoader;
import javax.cache.integration.CacheLoaderException;
import javax.cache.integration.CacheWriter;
import javax.cache.integration.CacheWriterException;
import javax.cache.spi.CachingProvider;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;


@RunWith(HazelcastParallelClassRunner.class)
@Category({ QuickTest.class, ParallelTest.class })
public class CacheReadWriteThroughTest extends HazelcastTestSupport {
    protected TestHazelcastInstanceFactory factory;

    protected HazelcastInstance hz;

    protected CachingProvider cachingProvider;

    @Test
    public void test_getAll_readThrough() throws Exception {
        final String cacheName = HazelcastTestSupport.randomName();
        CacheManager cacheManager = cachingProvider.getCacheManager();
        Assert.assertNotNull(cacheManager);
        Assert.assertNull(cacheManager.getCache(cacheName));
        CacheConfig<Integer, Integer> config = createCacheConfig();
        config.setReadThrough(true);
        config.setCacheLoaderFactory(FactoryBuilder.factoryOf(new CacheReadWriteThroughTest.GetAllAsyncCacheLoader(false)));
        Cache<Integer, Integer> cache = cacheManager.createCache(cacheName, config);
        Assert.assertNotNull(cache);
        Set<Integer> keys = new HashSet<Integer>();
        for (int i = 0; i < 150; i++) {
            keys.add(i);
        }
        Map<Integer, Integer> loaded = cache.getAll(keys);
        Assert.assertEquals(100, loaded.size());
    }

    @Test
    public void test_loadAll_readThrough() throws Exception {
        loadAll_readThrough(false);
    }

    @Test
    public void test_loadAll_readThrough_whenThereIsAnThrowableButNotAnException() throws Exception {
        loadAll_readThrough(true);
    }

    public static class GetAllAsyncCacheLoader implements Serializable , CacheLoader<Integer, Integer> {
        private final boolean throwError;

        private GetAllAsyncCacheLoader(boolean throwError) {
            this.throwError = throwError;
        }

        @Override
        public Integer load(Integer key) {
            return (key != null) && (key < 100) ? key : null;
        }

        @Override
        public Map<Integer, Integer> loadAll(Iterable<? extends Integer> keys) throws CacheLoaderException {
            if (throwError) {
                return new HashMap<Integer, Integer>() {
                    @Override
                    public Integer get(Object key) {
                        throw new IllegalAccessError("Bazinga !!!");
                    }
                };
            }
            Map<Integer, Integer> result = new HashMap<Integer, Integer>();
            for (Integer key : keys) {
                Integer value = load(key);
                if (value != null) {
                    result.put(key, value);
                }
            }
            return result;
        }
    }

    @Test
    public void test_putAsAdd_writeThrough_allKeysAccepted() {
        do_putAsAdd_writeThrough(true);
    }

    @Test
    public void test_putAsAdd_writeThrough_someKeysBanned() {
        do_putAsAdd_writeThrough(false);
    }

    @Test
    public void test_putAsUpdate_writeThrough_allKeysAccepted() {
        do_putAsUpdate_writeThrough(true);
    }

    @Test
    public void test_putAsUpdate_writeThrough_someKeysBanned() {
        do_putAsUpdate_writeThrough(false);
    }

    public static class PutCacheWriter implements Serializable , CacheWriter<Integer, Integer> {
        private final CacheReadWriteThroughTest.ValueChecker valueChecker;

        private PutCacheWriter() {
            this.valueChecker = null;
        }

        private PutCacheWriter(CacheReadWriteThroughTest.ValueChecker valueChecker) {
            this.valueChecker = valueChecker;
        }

        private boolean isAcceptableValue(int value) {
            if ((valueChecker) == null) {
                return true;
            }
            return valueChecker.isAcceptableValue(value);
        }

        @Override
        public void write(Entry<? extends Integer, ? extends Integer> entry) throws CacheWriterException {
            Integer value = entry.getValue();
            if (!(isAcceptableValue(value))) {
                throw new CacheWriterException(("Value is invalid: " + value));
            }
        }

        @Override
        public void writeAll(Collection<Entry<? extends Integer, ? extends Integer>> entries) throws CacheWriterException {
        }

        @Override
        public void delete(Object key) throws CacheWriterException {
        }

        @Override
        public void deleteAll(Collection<?> keys) throws CacheWriterException {
        }
    }

    public interface ValueChecker extends Serializable {
        boolean isAcceptableValue(int value);
    }

    public static class ModValueChecker implements CacheReadWriteThroughTest.ValueChecker {
        private final int mod;

        private ModValueChecker(int mod) {
            this.mod = mod;
        }

        @Override
        public boolean isAcceptableValue(int value) {
            return (value % (mod)) != 0;
        }
    }

    public static class MaxValueChecker implements CacheReadWriteThroughTest.ValueChecker {
        private final int maxValue;

        private MaxValueChecker(int maxValue) {
            this.maxValue = maxValue;
        }

        @Override
        public boolean isAcceptableValue(int value) {
            return value < (maxValue);
        }
    }
}

