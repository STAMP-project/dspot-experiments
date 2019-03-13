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
package org.ehcache.clustered.client.internal.store;


import ClusteredResourceType.Types.DEDICATED;
import ClusteredResourceType.Types.SHARED;
import ClusteredStore.Provider;
import java.util.Collections;
import org.ehcache.clustered.client.service.ClusteringService;
import org.ehcache.config.ResourcePool;
import org.ehcache.config.ResourceType;
import org.ehcache.core.spi.ServiceLocator;
import org.ehcache.core.spi.service.DiskResourceService;
import org.ehcache.impl.internal.store.disk.OffHeapDiskStore;
import org.ehcache.impl.internal.store.heap.OnHeapStore;
import org.ehcache.impl.internal.store.offheap.OffHeapStore;
import org.ehcache.impl.internal.store.tiering.TieredStore;
import org.ehcache.spi.service.ServiceConfiguration;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

import static org.ehcache.config.ResourceType.Core.DISK;
import static org.ehcache.config.ResourceType.Core.HEAP;
import static org.ehcache.config.ResourceType.Core.OFFHEAP;


/**
 * Provides basic tests for {@link org.ehcache.clustered.client.internal.store.ClusteredStore.Provider ClusteredStore.Provider}.
 */
public class ClusteredStoreProviderTest {
    @Test
    public void testRank() throws Exception {
        ClusteredStore.Provider provider = new ClusteredStore.Provider();
        ServiceLocator serviceLocator = ServiceLocator.dependencySet().with(new TieredStore.Provider()).with(new OnHeapStore.Provider()).with(new OffHeapStore.Provider()).with(Mockito.mock(DiskResourceService.class)).with(new OffHeapDiskStore.Provider()).with(Mockito.mock(ClusteringService.class)).build();
        provider.start(serviceLocator);
        assertRank(provider, 1, DEDICATED);
        assertRank(provider, 1, SHARED);
        assertRank(provider, 0, new ClusteredStoreProviderTest.UnmatchedResourceType());
    }

    @Test
    public void testRankTiered() throws Exception {
        TieredStore.Provider provider = new TieredStore.Provider();
        ServiceLocator serviceLocator = ServiceLocator.dependencySet().with(provider).with(new ClusteredStore.Provider()).with(new OnHeapStore.Provider()).with(new OffHeapStore.Provider()).with(new OffHeapDiskStore.Provider()).with(Mockito.mock(DiskResourceService.class)).with(Mockito.mock(ClusteringService.class)).build();
        serviceLocator.startAllServices();
        assertRank(provider, 0, DEDICATED, DISK);
        assertRank(provider, 2, DEDICATED, HEAP);
        assertRank(provider, 0, DEDICATED, OFFHEAP);
        assertRank(provider, 0, DEDICATED, DISK, OFFHEAP);
        assertRank(provider, 0, DEDICATED, DISK, HEAP);
        assertRank(provider, 3, DEDICATED, OFFHEAP, HEAP);
        assertRank(provider, 0, DEDICATED, DISK, OFFHEAP, HEAP);
        assertRank(provider, 0, SHARED, DISK);
        assertRank(provider, 2, SHARED, HEAP);
        assertRank(provider, 0, SHARED, OFFHEAP);
        assertRank(provider, 0, SHARED, DISK, OFFHEAP);
        assertRank(provider, 0, SHARED, DISK, HEAP);
        assertRank(provider, 3, SHARED, OFFHEAP, HEAP);
        assertRank(provider, 0, SHARED, DISK, OFFHEAP, HEAP);
        // Multiple clustered resources not currently supported
        assertRank(provider, 0, DEDICATED, SHARED, DISK);
        assertRank(provider, 0, DEDICATED, SHARED, HEAP);
        assertRank(provider, 0, DEDICATED, SHARED, OFFHEAP);
        assertRank(provider, 0, DEDICATED, SHARED, DISK, OFFHEAP);
        assertRank(provider, 0, DEDICATED, SHARED, DISK, HEAP);
        assertRank(provider, 0, DEDICATED, SHARED, OFFHEAP, HEAP);
        assertRank(provider, 0, DEDICATED, SHARED, DISK, OFFHEAP, HEAP);
    }

    @Test
    public void testAuthoritativeRank() throws Exception {
        ClusteredStore.Provider provider = new ClusteredStore.Provider();
        ServiceLocator serviceLocator = ServiceLocator.dependencySet().with(Mockito.mock(ClusteringService.class)).build();
        provider.start(serviceLocator);
        Assert.assertThat(provider.rankAuthority(DEDICATED, Collections.<ServiceConfiguration<?>>emptyList()), Matchers.is(1));
        Assert.assertThat(provider.rankAuthority(SHARED, Collections.<ServiceConfiguration<?>>emptyList()), Matchers.is(1));
        Assert.assertThat(provider.rankAuthority(new ClusteredStoreProviderTest.UnmatchedResourceType(), Collections.<ServiceConfiguration<?>>emptyList()), Matchers.is(0));
    }

    public static class UnmatchedResourceType implements ResourceType<ResourcePool> {
        @Override
        public Class<ResourcePool> getResourcePoolClass() {
            return ResourcePool.class;
        }

        @Override
        public boolean isPersistable() {
            return true;
        }

        @Override
        public boolean requiresSerialization() {
            return true;
        }

        @Override
        public int getTierHeight() {
            return 10;
        }
    }
}

