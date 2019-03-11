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
package org.ehcache.impl.config.serializer;


import java.nio.ByteBuffer;
import java.util.concurrent.atomic.AtomicInteger;
import org.ehcache.Cache;
import org.ehcache.CacheManager;
import org.ehcache.config.builders.CacheConfigurationBuilder;
import org.ehcache.config.builders.ResourcePoolsBuilder;
import org.ehcache.config.units.EntryUnit;
import org.ehcache.config.units.MemoryUnit;
import org.ehcache.core.spi.service.FileBasedPersistenceContext;
import org.ehcache.impl.config.copy.DefaultCopierConfiguration;
import org.ehcache.impl.copy.SerializingCopier;
import org.ehcache.impl.serialization.JavaSerializer;
import org.ehcache.spi.serialization.Serializer;
import org.ehcache.spi.serialization.SerializerException;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import static org.ehcache.impl.config.copy.DefaultCopierConfiguration.Type.KEY;
import static org.ehcache.impl.config.copy.DefaultCopierConfiguration.Type.VALUE;


/**
 * SerializerCountingTest
 */
public class SerializerCountingTest {
    private static final boolean PRINT_STACK_TRACES = false;

    private CacheManager cacheManager;

    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    @Test
    public void testOnHeapPutGet() {
        Cache<Long, String> cache = cacheManager.createCache("onHeap", CacheConfigurationBuilder.newCacheConfigurationBuilder(Long.class, String.class, ResourcePoolsBuilder.heap(10)).add(new DefaultCopierConfiguration<>(SerializingCopier.<Long>asCopierClass(), KEY)).add(new DefaultCopierConfiguration<>(SerializingCopier.<String>asCopierClass(), VALUE)).build());
        cache.put(42L, "TheAnswer!");
        assertCounters(2, 2, 0, 1, 0, 0);
        printSerializationCounters("Put OnHeap (create)");
        cache.get(42L);
        assertCounters(0, 0, 0, 0, 1, 0);
        printSerializationCounters("Get OnHeap");
        cache.put(42L, "Wrong ...");
        assertCounters(2, 2, 0, 1, 0, 0);
        printSerializationCounters("Put OnHeap (update)");
    }

    @Test
    public void testOffHeapPutGet() {
        Cache<Long, String> cache = cacheManager.createCache("offHeap", CacheConfigurationBuilder.newCacheConfigurationBuilder(Long.class, String.class, ResourcePoolsBuilder.newResourcePoolsBuilder().heap(10, EntryUnit.ENTRIES).offheap(10, MemoryUnit.MB)).build());
        cache.put(42L, "TheAnswer");
        assertCounters(1, 0, 0, 1, 0, 0);
        printSerializationCounters("Put Offheap");
        cache.get(42L);
        assertCounters(0, 0, 1, 0, 1, 0);
        printSerializationCounters("Get Offheap fault");
        cache.get(42L);
        assertCounters(0, 0, 0, 0, 0, 0);
        printSerializationCounters("Get Offheap faulted");
        cache.put(42L, "Wrong ...");
        assertCounters(1, 0, 2, 1, 0, 0);
        printSerializationCounters("Put OffHeap (update faulted)");
    }

    @Test
    public void testOffHeapOnHeapCopyPutGet() {
        Cache<Long, String> cache = cacheManager.createCache("offHeap", CacheConfigurationBuilder.newCacheConfigurationBuilder(Long.class, String.class, ResourcePoolsBuilder.newResourcePoolsBuilder().heap(10, EntryUnit.ENTRIES).offheap(10, MemoryUnit.MB)).add(new DefaultCopierConfiguration<>(SerializingCopier.<Long>asCopierClass(), KEY)).add(new DefaultCopierConfiguration<>(SerializingCopier.<String>asCopierClass(), VALUE)).build());
        cache.put(42L, "TheAnswer");
        assertCounters(2, 1, 0, 1, 0, 0);
        printSerializationCounters("Put OffheapOnHeapCopy");
        cache.get(42L);
        assertCounters(1, 1, 1, 0, 2, 0);
        printSerializationCounters("Get OffheapOnHeapCopy fault");
        cache.get(42L);
        assertCounters(0, 0, 0, 0, 1, 0);
        printSerializationCounters("Get OffheapOnHeapCopy faulted");
        cache.put(42L, "Wrong ...");
        assertCounters(3, 2, 2, 1, 0, 0);
        printSerializationCounters("Put OffheapOnHeapCopy (update faulted)");
    }

    @Test
    public void testDiskOffHeapOnHeapCopyPutGet() {
        Cache<Long, String> cache = cacheManager.createCache("offHeap", CacheConfigurationBuilder.newCacheConfigurationBuilder(Long.class, String.class, ResourcePoolsBuilder.newResourcePoolsBuilder().heap(2, EntryUnit.ENTRIES).offheap(10, MemoryUnit.MB).disk(100, MemoryUnit.MB)).add(new DefaultCopierConfiguration<>(SerializingCopier.<Long>asCopierClass(), KEY)).add(new DefaultCopierConfiguration<>(SerializingCopier.<String>asCopierClass(), VALUE)).build());
        cache.put(42L, "TheAnswer");
        assertCounters(3, 2, 0, 1, 0, 0);
        printSerializationCounters("Put DiskOffHeapOnHeapCopy");
        cache.get(42L);
        assertCounters(1, 1, 1, 0, 2, 0);
        printSerializationCounters("Get DiskOffHeapOnHeapCopy fault");
        cache.get(42L);
        assertCounters(0, 0, 0, 0, 1, 0);
        printSerializationCounters("Get DiskOffHeapOnHeapCopy faulted");
        cache.put(42L, "Wrong ...");
        assertCounters(3, 2, 2, 1, 0, 0);
        printSerializationCounters("Put DiskOffHeapOnHeapCopy (update faulted)");
    }

    public static class CountingSerializer<T> implements Serializer<T> {
        static AtomicInteger serializeCounter = new AtomicInteger(0);

        static AtomicInteger deserializeCounter = new AtomicInteger(0);

        static AtomicInteger equalsCounter = new AtomicInteger(0);

        static AtomicInteger keySerializeCounter = new AtomicInteger(0);

        static AtomicInteger keyDeserializeCounter = new AtomicInteger(0);

        static AtomicInteger keyEqualsCounter = new AtomicInteger(0);

        private final JavaSerializer<T> serializer;

        public CountingSerializer(ClassLoader classLoader) {
            serializer = new JavaSerializer<>(classLoader);
        }

        public CountingSerializer(ClassLoader classLoader, FileBasedPersistenceContext persistenceContext) {
            serializer = new JavaSerializer<>(classLoader);
        }

        @Override
        public ByteBuffer serialize(T object) throws SerializerException {
            if (SerializerCountingTest.PRINT_STACK_TRACES) {
                new Exception().printStackTrace();
            }
            if (object.getClass().equals(String.class)) {
                SerializerCountingTest.CountingSerializer.serializeCounter.incrementAndGet();
            } else {
                SerializerCountingTest.CountingSerializer.keySerializeCounter.incrementAndGet();
            }
            return serializer.serialize(object);
        }

        @Override
        public T read(ByteBuffer binary) throws ClassNotFoundException, SerializerException {
            if (SerializerCountingTest.PRINT_STACK_TRACES) {
                new Exception().printStackTrace();
            }
            T value = serializer.read(binary);
            if (value.getClass().equals(String.class)) {
                SerializerCountingTest.CountingSerializer.deserializeCounter.incrementAndGet();
            } else {
                SerializerCountingTest.CountingSerializer.keyDeserializeCounter.incrementAndGet();
            }
            return value;
        }

        @Override
        public boolean equals(T object, ByteBuffer binary) throws ClassNotFoundException, SerializerException {
            if (SerializerCountingTest.PRINT_STACK_TRACES) {
                new Exception().printStackTrace();
            }
            if (object.getClass().equals(String.class)) {
                SerializerCountingTest.CountingSerializer.equalsCounter.incrementAndGet();
            } else {
                SerializerCountingTest.CountingSerializer.keyEqualsCounter.incrementAndGet();
            }
            return serializer.equals(object, binary);
        }
    }
}

