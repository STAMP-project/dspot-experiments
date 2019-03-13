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
package org.ehcache.clustered.client.internal.loaderwriter.writebehind;


import ClusteredResourceType.Types.DEDICATED;
import ClusteredWriteBehindStore.Provider;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import org.ehcache.clustered.client.internal.store.ClusteredStoreProviderTest;
import org.ehcache.clustered.client.service.ClusteringService;
import org.ehcache.core.spi.ServiceLocator;
import org.ehcache.core.spi.service.DiskResourceService;
import org.ehcache.impl.internal.store.disk.OffHeapDiskStore;
import org.ehcache.impl.internal.store.heap.OnHeapStore;
import org.ehcache.impl.internal.store.offheap.OffHeapStore;
import org.ehcache.impl.internal.store.tiering.TieredStore;
import org.ehcache.spi.loaderwriter.CacheLoaderWriterConfiguration;
import org.ehcache.spi.loaderwriter.WriteBehindConfiguration;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;


public class ClusteredWriteBehindStoreProviderTest {
    private final CacheLoaderWriterConfiguration cacheLoaderWriterConfiguration = Mockito.mock(CacheLoaderWriterConfiguration.class);

    private final WriteBehindConfiguration writeBehindConfiguration = Mockito.mock(WriteBehindConfiguration.class);

    @Test
    public void testRank() {
        ClusteredWriteBehindStore.Provider provider = new ClusteredWriteBehindStore.Provider();
        ServiceLocator serviceLocator = ServiceLocator.dependencySet().with(new TieredStore.Provider()).with(new OnHeapStore.Provider()).with(new OffHeapStore.Provider()).with(Mockito.mock(DiskResourceService.class)).with(new OffHeapDiskStore.Provider()).with(Mockito.mock(ClusteringService.class)).build();
        provider.start(serviceLocator);
        Assert.assertThat(provider.rank(new HashSet(Collections.singletonList(DEDICATED)), Arrays.asList(cacheLoaderWriterConfiguration, writeBehindConfiguration)), Matchers.is(3));
        Assert.assertThat(provider.rank(new HashSet(Collections.singletonList(DEDICATED)), Collections.singletonList(cacheLoaderWriterConfiguration)), Matchers.is(0));
        Assert.assertThat(provider.rank(new HashSet(Collections.singletonList(new ClusteredStoreProviderTest.UnmatchedResourceType())), Arrays.asList(cacheLoaderWriterConfiguration, writeBehindConfiguration)), Matchers.is(0));
    }

    @Test
    public void testAuthoritativeRank() {
        ClusteredWriteBehindStore.Provider provider = new ClusteredWriteBehindStore.Provider();
        ServiceLocator serviceLocator = ServiceLocator.dependencySet().with(Mockito.mock(ClusteringService.class)).build();
        provider.start(serviceLocator);
        Assert.assertThat(provider.rankAuthority(DEDICATED, Arrays.asList(cacheLoaderWriterConfiguration, writeBehindConfiguration)), Matchers.is(3));
        Assert.assertThat(provider.rankAuthority(DEDICATED, Collections.singletonList(writeBehindConfiguration)), Matchers.is(0));
        Assert.assertThat(provider.rankAuthority(new ClusteredStoreProviderTest.UnmatchedResourceType(), Arrays.asList(cacheLoaderWriterConfiguration, writeBehindConfiguration)), Matchers.is(0));
    }
}

