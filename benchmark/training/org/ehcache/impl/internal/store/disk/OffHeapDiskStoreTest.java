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
package org.ehcache.impl.internal.store.disk;


import java.io.IOException;
import java.io.Serializable;
import java.util.Collections;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import org.ehcache.Cache;
import org.ehcache.CacheManager;
import org.ehcache.config.CacheConfiguration;
import org.ehcache.config.ResourcePool;
import org.ehcache.config.ResourceType;
import org.ehcache.config.builders.CacheConfigurationBuilder;
import org.ehcache.config.builders.CacheManagerBuilder;
import org.ehcache.config.builders.ExpiryPolicyBuilder;
import org.ehcache.config.builders.ResourcePoolsBuilder;
import org.ehcache.config.units.MemoryUnit;
import org.ehcache.core.spi.ServiceLocator;
import org.ehcache.core.spi.store.Store;
import org.ehcache.core.spi.time.SystemTimeSource;
import org.ehcache.core.statistics.LowerCachingTierOperationsOutcome;
import org.ehcache.impl.config.store.disk.OffHeapDiskStoreConfiguration;
import org.ehcache.impl.internal.persistence.TestDiskResourceService;
import org.ehcache.impl.internal.store.offheap.AbstractOffHeapStoreTest;
import org.ehcache.impl.internal.util.UnmatchedResourceType;
import org.ehcache.spi.loaderwriter.CacheLoaderWriter;
import org.ehcache.spi.persistence.PersistableResourceService;
import org.ehcache.spi.resilience.StoreAccessException;
import org.ehcache.test.MockitoUtil;
import org.hamcrest.CoreMatchers;
import org.hamcrest.core.Is;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.mockito.Mockito;
import org.terracotta.context.ContextManager;
import org.terracotta.context.query.Matcher;
import org.terracotta.context.query.Matchers;
import org.terracotta.context.query.Query;
import org.terracotta.context.query.QueryBuilder;
import org.terracotta.statistics.OperationStatistic;

import static org.ehcache.config.ResourceType.Core.DISK;
import static org.ehcache.config.ResourceType.Core.HEAP;
import static org.ehcache.config.ResourceType.Core.OFFHEAP;
import static org.ehcache.impl.internal.store.disk.OffHeapDiskStore.Provider.close;
import static org.ehcache.impl.internal.store.disk.OffHeapDiskStore.Provider.init;


public class OffHeapDiskStoreTest extends AbstractOffHeapStoreTest {
    @Rule
    public final TemporaryFolder temporaryFolder = new TemporaryFolder();

    @Rule
    public final TestDiskResourceService diskResourceService = new TestDiskResourceService();

    @Test
    public void testRecovery() throws IOException, StoreAccessException {
        OffHeapDiskStore<String, String> offHeapDiskStore = createAndInitStore(SystemTimeSource.INSTANCE, ExpiryPolicyBuilder.noExpiration());
        try {
            offHeapDiskStore.put("key1", "value1");
            Assert.assertThat(offHeapDiskStore.get("key1"), CoreMatchers.notNullValue());
            close(offHeapDiskStore);
            init(offHeapDiskStore);
            Assert.assertThat(offHeapDiskStore.get("key1"), CoreMatchers.notNullValue());
        } finally {
            destroyStore(offHeapDiskStore);
        }
    }

    @Test
    public void testRecoveryFailureWhenValueTypeChangesToIncompatibleClass() throws Exception {
        OffHeapDiskStore.Provider provider = new OffHeapDiskStore.Provider();
        ServiceLocator serviceLocator = ServiceLocator.dependencySet().with(diskResourceService).with(provider).build();
        serviceLocator.startAllServices();
        CacheConfiguration<Long, String> cacheConfiguration = MockitoUtil.mock(CacheConfiguration.class);
        Mockito.when(cacheConfiguration.getResourcePools()).thenReturn(ResourcePoolsBuilder.newResourcePoolsBuilder().disk(1, MemoryUnit.MB, false).build());
        PersistableResourceService.PersistenceSpaceIdentifier<?> space = diskResourceService.getPersistenceSpaceIdentifier("cache", cacheConfiguration);
        {
            @SuppressWarnings("unchecked")
            Store.Configuration<Long, String> storeConfig1 = MockitoUtil.mock(Store.Configuration.class);
            Mockito.when(storeConfig1.getKeyType()).thenReturn(Long.class);
            Mockito.when(storeConfig1.getValueType()).thenReturn(String.class);
            Mockito.when(storeConfig1.getResourcePools()).thenReturn(ResourcePoolsBuilder.newResourcePoolsBuilder().disk(10, MemoryUnit.MB).build());
            Mockito.when(storeConfig1.getDispatcherConcurrency()).thenReturn(1);
            OffHeapDiskStore<Long, String> offHeapDiskStore1 = provider.createStore(storeConfig1, space);
            provider.initStore(offHeapDiskStore1);
            destroyStore(offHeapDiskStore1);
        }
        {
            @SuppressWarnings("unchecked")
            Store.Configuration<Long, Serializable> storeConfig2 = MockitoUtil.mock(Store.Configuration.class);
            Mockito.when(storeConfig2.getKeyType()).thenReturn(Long.class);
            Mockito.when(storeConfig2.getValueType()).thenReturn(Serializable.class);
            Mockito.when(storeConfig2.getResourcePools()).thenReturn(ResourcePoolsBuilder.newResourcePoolsBuilder().disk(10, MemoryUnit.MB).build());
            Mockito.when(storeConfig2.getDispatcherConcurrency()).thenReturn(1);
            Mockito.when(storeConfig2.getClassLoader()).thenReturn(ClassLoader.getSystemClassLoader());
            OffHeapDiskStore<Long, Serializable> offHeapDiskStore2 = provider.createStore(storeConfig2, space);
            try {
                provider.initStore(offHeapDiskStore2);
                Assert.fail("expected IllegalArgumentException");
            } catch (IllegalArgumentException e) {
                // expected
            }
            destroyStore(offHeapDiskStore2);
        }
    }

    @Test
    public void testRecoveryWithArrayType() throws Exception {
        OffHeapDiskStore.Provider provider = new OffHeapDiskStore.Provider();
        ServiceLocator serviceLocator = ServiceLocator.dependencySet().with(diskResourceService).with(provider).build();
        serviceLocator.startAllServices();
        CacheConfiguration<?, ?> cacheConfiguration = MockitoUtil.mock(CacheConfiguration.class);
        Mockito.when(cacheConfiguration.getResourcePools()).thenReturn(ResourcePoolsBuilder.newResourcePoolsBuilder().disk(1, MemoryUnit.MB, false).build());
        PersistableResourceService.PersistenceSpaceIdentifier<?> space = diskResourceService.getPersistenceSpaceIdentifier("cache", cacheConfiguration);
        {
            @SuppressWarnings("unchecked")
            Store.Configuration<Long, Object[]> storeConfig1 = MockitoUtil.mock(Store.Configuration.class);
            Mockito.when(storeConfig1.getKeyType()).thenReturn(Long.class);
            Mockito.when(storeConfig1.getValueType()).thenReturn(Object[].class);
            Mockito.when(storeConfig1.getResourcePools()).thenReturn(ResourcePoolsBuilder.newResourcePoolsBuilder().disk(10, MemoryUnit.MB).build());
            Mockito.when(storeConfig1.getDispatcherConcurrency()).thenReturn(1);
            OffHeapDiskStore<Long, Object[]> offHeapDiskStore1 = provider.createStore(storeConfig1, space);
            provider.initStore(offHeapDiskStore1);
            destroyStore(offHeapDiskStore1);
        }
        {
            @SuppressWarnings("unchecked")
            Store.Configuration<Long, Object[]> storeConfig2 = MockitoUtil.mock(Store.Configuration.class);
            Mockito.when(storeConfig2.getKeyType()).thenReturn(Long.class);
            Mockito.when(storeConfig2.getValueType()).thenReturn(Object[].class);
            Mockito.when(storeConfig2.getResourcePools()).thenReturn(ResourcePoolsBuilder.newResourcePoolsBuilder().disk(10, MemoryUnit.MB).build());
            Mockito.when(storeConfig2.getDispatcherConcurrency()).thenReturn(1);
            Mockito.when(storeConfig2.getClassLoader()).thenReturn(ClassLoader.getSystemClassLoader());
            OffHeapDiskStore<Long, Object[]> offHeapDiskStore2 = provider.createStore(storeConfig2, space);
            provider.initStore(offHeapDiskStore2);
            destroyStore(offHeapDiskStore2);
        }
    }

    @Test
    public void testProvidingOffHeapDiskStoreConfiguration() throws Exception {
        OffHeapDiskStore.Provider provider = new OffHeapDiskStore.Provider();
        ServiceLocator serviceLocator = ServiceLocator.dependencySet().with(diskResourceService).with(provider).build();
        serviceLocator.startAllServices();
        CacheConfiguration<?, ?> cacheConfiguration = MockitoUtil.mock(CacheConfiguration.class);
        Mockito.when(cacheConfiguration.getResourcePools()).thenReturn(ResourcePoolsBuilder.newResourcePoolsBuilder().disk(1, MemoryUnit.MB, false).build());
        PersistableResourceService.PersistenceSpaceIdentifier<?> space = diskResourceService.getPersistenceSpaceIdentifier("cache", cacheConfiguration);
        @SuppressWarnings("unchecked")
        Store.Configuration<Long, Object[]> storeConfig1 = MockitoUtil.mock(Store.Configuration.class);
        Mockito.when(storeConfig1.getKeyType()).thenReturn(Long.class);
        Mockito.when(storeConfig1.getValueType()).thenReturn(Object[].class);
        Mockito.when(storeConfig1.getResourcePools()).thenReturn(ResourcePoolsBuilder.newResourcePoolsBuilder().disk(10, MemoryUnit.MB).build());
        Mockito.when(storeConfig1.getDispatcherConcurrency()).thenReturn(1);
        OffHeapDiskStore<Long, Object[]> offHeapDiskStore1 = provider.createStore(storeConfig1, space, new OffHeapDiskStoreConfiguration("pool", 2, 4));
        Assert.assertThat(offHeapDiskStore1.getThreadPoolAlias(), Is.is("pool"));
        Assert.assertThat(offHeapDiskStore1.getWriterConcurrency(), Is.is(2));
        Assert.assertThat(offHeapDiskStore1.getDiskSegments(), Is.is(4));
    }

    @Test
    public void testStoreInitFailsWithoutLocalPersistenceService() throws Exception {
        OffHeapDiskStore.Provider provider = new OffHeapDiskStore.Provider();
        try {
            ServiceLocator.dependencySet().with(provider).build();
            Assert.fail("IllegalStateException expected");
        } catch (IllegalStateException e) {
            Assert.assertThat(e.getMessage(), CoreMatchers.containsString(("Failed to find provider with satisfied dependency set for interface" + " org.ehcache.core.spi.service.DiskResourceService")));
        }
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testAuthoritativeRank() throws Exception {
        OffHeapDiskStore.Provider provider = new OffHeapDiskStore.Provider();
        Assert.assertThat(provider.rankAuthority(DISK, Collections.EMPTY_LIST), Is.is(1));
        Assert.assertThat(provider.rankAuthority(new UnmatchedResourceType(), Collections.EMPTY_LIST), Is.is(0));
    }

    @Test
    public void testRank() throws Exception {
        OffHeapDiskStore.Provider provider = new OffHeapDiskStore.Provider();
        assertRank(provider, 1, DISK);
        assertRank(provider, 0, HEAP);
        assertRank(provider, 0, OFFHEAP);
        assertRank(provider, 0, DISK, OFFHEAP);
        assertRank(provider, 0, DISK, HEAP);
        assertRank(provider, 0, OFFHEAP, HEAP);
        assertRank(provider, 0, DISK, OFFHEAP, HEAP);
        assertRank(provider, 0, ((ResourceType<ResourcePool>) (new UnmatchedResourceType())));
        assertRank(provider, 0, DISK, new UnmatchedResourceType());
    }

    @Test
    public void diskStoreShrinkingTest() throws Exception {
        try (CacheManager manager = CacheManagerBuilder.newCacheManagerBuilder().with(CacheManagerBuilder.persistence(temporaryFolder.newFolder("disk-stores").getAbsolutePath())).build(true)) {
            CacheConfigurationBuilder<Long, OffHeapDiskStoreTest.CacheValue> cacheConfigurationBuilder = CacheConfigurationBuilder.newCacheConfigurationBuilder(Long.class, OffHeapDiskStoreTest.CacheValue.class, ResourcePoolsBuilder.heap(1000).offheap(10, MemoryUnit.MB).disk(20, MemoryUnit.MB)).withLoaderWriter(new CacheLoaderWriter<Long, OffHeapDiskStoreTest.CacheValue>() {
                @Override
                public OffHeapDiskStoreTest.CacheValue load(Long key) {
                    return null;
                }

                @Override
                public Map<Long, OffHeapDiskStoreTest.CacheValue> loadAll(Iterable<? extends Long> keys) {
                    return Collections.emptyMap();
                }

                @Override
                public void write(Long key, OffHeapDiskStoreTest.CacheValue value) {
                }

                @Override
                public void writeAll(Iterable<? extends Map.Entry<? extends Long, ? extends OffHeapDiskStoreTest.CacheValue>> entries) {
                }

                @Override
                public void delete(Long key) {
                }

                @Override
                public void deleteAll(Iterable<? extends Long> keys) {
                }
            });
            Cache<Long, OffHeapDiskStoreTest.CacheValue> cache = manager.createCache("test", cacheConfigurationBuilder);
            for (long i = 0; i < 100000; i++) {
                cache.put(i, new OffHeapDiskStoreTest.CacheValue(((int) (i))));
            }
            Callable<Void> task = () -> {
                Random rndm = new Random();
                long start = System.nanoTime();
                while ((System.nanoTime()) < (start + (TimeUnit.SECONDS.toNanos(5)))) {
                    Long k = key(rndm);
                    switch (rndm.nextInt(4)) {
                        case 0 :
                            {
                                OffHeapDiskStoreTest.CacheValue v = value(rndm);
                                cache.putIfAbsent(k, v);
                                break;
                            }
                        case 1 :
                            {
                                OffHeapDiskStoreTest.CacheValue nv = value(rndm);
                                OffHeapDiskStoreTest.CacheValue ov = value(rndm);
                                cache.put(k, ov);
                                cache.replace(k, nv);
                                break;
                            }
                        case 2 :
                            {
                                OffHeapDiskStoreTest.CacheValue nv = value(rndm);
                                OffHeapDiskStoreTest.CacheValue ov = value(rndm);
                                cache.put(k, ov);
                                cache.replace(k, ov, nv);
                                break;
                            }
                        case 3 :
                            {
                                OffHeapDiskStoreTest.CacheValue v = value(rndm);
                                cache.put(k, v);
                                cache.remove(k, v);
                                break;
                            }
                    }
                } 
                return null;
            };
            ExecutorService executor = Executors.newCachedThreadPool();
            try {
                executor.invokeAll(Collections.nCopies(4, task));
            } finally {
                executor.shutdown();
            }
            Query invalidateAllQuery = QueryBuilder.queryBuilder().descendants().filter(Matchers.context(Matchers.attributes(Matchers.hasAttribute("tags", new Matcher<Set<String>>() {
                @Override
                protected boolean matchesSafely(Set<String> object) {
                    return object.contains("OffHeap");
                }
            })))).filter(Matchers.context(Matchers.attributes(Matchers.hasAttribute("name", "invalidateAll")))).ensureUnique().build();
            @SuppressWarnings("unchecked")
            OperationStatistic<LowerCachingTierOperationsOutcome.InvalidateAllOutcome> invalidateAll = ((OperationStatistic<LowerCachingTierOperationsOutcome.InvalidateAllOutcome>) (invalidateAllQuery.execute(Collections.singleton(ContextManager.nodeFor(cache))).iterator().next().getContext().attributes().get("this")));
            Assert.assertThat(invalidateAll.sum(), Is.is(0L));
        }
    }

    public static class CacheValue implements Serializable {
        private static final long serialVersionUID = 1L;

        private final int value;

        private final byte[] padding;

        public CacheValue(int value) {
            this.value = value;
            this.padding = new byte[800];
        }

        @Override
        public int hashCode() {
            return value;
        }

        public boolean equals(Object o) {
            if (o instanceof OffHeapDiskStoreTest.CacheValue) {
                return (value) == (((OffHeapDiskStoreTest.CacheValue) (o)).value);
            } else {
                return false;
            }
        }
    }
}

