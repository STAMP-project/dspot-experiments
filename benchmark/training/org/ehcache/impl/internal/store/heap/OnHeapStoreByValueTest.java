/**
 * Copyright Terracotta, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.ehcache.impl.internal.store.heap;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import org.ehcache.Cache;
import org.ehcache.CacheManager;
import org.ehcache.config.Eviction;
import org.ehcache.config.builders.CacheConfigurationBuilder;
import org.ehcache.config.builders.CacheManagerBuilder;
import org.ehcache.config.builders.ExpiryPolicyBuilder;
import org.ehcache.config.builders.ResourcePoolsBuilder;
import org.ehcache.core.spi.store.AbstractValueHolder;
import org.ehcache.core.spi.store.Store;
import org.ehcache.core.spi.time.SystemTimeSource;
import org.ehcache.impl.config.copy.DefaultCopierConfiguration;
import org.ehcache.impl.copy.SerializingCopier;
import org.ehcache.impl.serialization.JavaSerializer;
import org.ehcache.spi.copy.Copier;
import org.ehcache.spi.resilience.StoreAccessException;
import org.ehcache.spi.serialization.SerializerException;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.hamcrest.core.Is;
import org.junit.Assert;
import org.junit.Test;

import static org.ehcache.impl.config.copy.DefaultCopierConfiguration.Type.VALUE;


@SuppressWarnings("serial")
public abstract class OnHeapStoreByValueTest extends BaseOnHeapStoreTest {
    static class LongCopier implements Copier<Long> {
        int copyForReadCount = 0;

        int copyForWriteCount = 0;

        @Override
        public Long copyForRead(Long obj) {
            (copyForReadCount)++;
            return obj;
        }

        @Override
        public Long copyForWrite(Long obj) {
            (copyForWriteCount)++;
            return obj;
        }
    }

    @Test
    public void testKeyCopierCalledOnGetOrComputeIfAbsent() throws Exception {
        OnHeapStoreByValueTest.LongCopier keyCopier = new OnHeapStoreByValueTest.LongCopier();
        OnHeapStore<Long, Long> store = newStore(SystemTimeSource.INSTANCE, ExpiryPolicyBuilder.noExpiration(), Eviction.noAdvice(), keyCopier, new SerializingCopier<>(new JavaSerializer<>(ClassLoader.getSystemClassLoader())), 100);
        Store.ValueHolder<Long> computed = store.getOrComputeIfAbsent(1L, ( key) -> new AbstractValueHolder<Long>((-1), (-1)) {
            @Override
            public Long get() {
                return key * 1000L;
            }
        });
        MatcherAssert.assertThat(computed.get(), Is.is(1000L));
        MatcherAssert.assertThat(keyCopier.copyForWriteCount, Is.is(1));
        MatcherAssert.assertThat(keyCopier.copyForReadCount, Is.is(0));
    }

    @Test
    public void testPutNotSerializableValue() throws Exception {
        OnHeapStore<Serializable, Serializable> store = newStore();
        try {
            store.put("key1", new ArrayList<Object>() {
                {
                    add(new Object());
                }
            });
            Assert.fail();
        } catch (StoreAccessException cae) {
            MatcherAssert.assertThat(cae.getCause(), CoreMatchers.instanceOf(SerializerException.class));
        }
    }

    @Test
    public void testPutNotSerializableKey() throws Exception {
        OnHeapStore<Serializable, Serializable> store = newStore();
        try {
            store.put(new ArrayList<Object>() {
                {
                    add(new Object());
                }
            }, "value");
            Assert.fail();
        } catch (StoreAccessException cae) {
            MatcherAssert.assertThat(cae.getCause(), CoreMatchers.instanceOf(SerializerException.class));
        }
    }

    @Test
    public void testValueUniqueObject() throws Exception {
        OnHeapStore<Serializable, Serializable> store = newStore();
        String key = "key";
        List<String> value = new ArrayList<>();
        value.add("value");
        store.put(key, ((Serializable) (value)));
        // mutate the value -- should not affect cache
        value.clear();
        Store.ValueHolder<Serializable> valueHolder = store.get(key);
        if (((valueHolder.get()) == value) || (!(valueHolder.get().equals(Collections.singletonList("value"))))) {
            throw new AssertionError();
        }
    }

    @Test
    public void testKeyUniqueObject() throws Exception {
        OnHeapStore<Serializable, Serializable> store = newStore();
        List<String> key = new ArrayList<>();
        key.add("key");
        String value = "value";
        store.put(((Serializable) (key)), value);
        // mutate the key -- should not affect cache
        key.clear();
        Serializable storeKey = store.iterator().next().getKey();
        if ((storeKey == key) || (!(storeKey.equals(Collections.singletonList("key"))))) {
            throw new AssertionError();
        }
    }

    @Test
    public void testStoreByValue() {
        CacheManager cacheManager = CacheManagerBuilder.newCacheManagerBuilder().build(false);
        cacheManager.init();
        DefaultCopierConfiguration<String> copierConfiguration = new DefaultCopierConfiguration<>(SerializingCopier.<String>asCopierClass(), VALUE);
        final Cache<Long, String> cache1 = cacheManager.createCache("cache1", CacheConfigurationBuilder.newCacheConfigurationBuilder(Long.class, String.class, ResourcePoolsBuilder.heap(1)).build());
        performAssertions(cache1, true);
        final Cache<Long, String> cache2 = cacheManager.createCache("cache2", CacheConfigurationBuilder.newCacheConfigurationBuilder(Long.class, String.class, ResourcePoolsBuilder.heap(1)).add(copierConfiguration).build());
        performAssertions(cache2, false);
        final Cache<Long, String> cache3 = cacheManager.createCache("cache3", CacheConfigurationBuilder.newCacheConfigurationBuilder(Long.class, String.class, ResourcePoolsBuilder.heap(1)).build());
        performAssertions(cache3, true);
        cacheManager.close();
    }
}

