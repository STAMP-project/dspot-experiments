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
package org.ehcache.clustered.client;


import ClusteredStore.Provider;
import java.util.Spliterators;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import org.ehcache.CacheManager;
import org.ehcache.clustered.client.service.ClusteringService;
import org.ehcache.config.CacheConfiguration;
import org.ehcache.config.builders.CacheConfigurationBuilder;
import org.ehcache.config.builders.CacheManagerBuilder;
import org.ehcache.config.builders.ResourcePoolsBuilder;
import org.ehcache.config.units.EntryUnit;
import org.ehcache.config.units.MemoryUnit;
import org.ehcache.core.spi.service.ServiceFactory;
import org.ehcache.core.util.ClassLoading;
import org.hamcrest.core.IsCollectionContaining;
import org.junit.Assert;
import org.junit.Test;


/**
 * Ensures that a non-clustered {@code CacheManager} can be created when clustered classes are
 * available in classpath.
 */
public class NonClusteredCacheTest {
    @Test
    public void testNonClustered() throws Exception {
        /* Ensure the cluster provider classes are loadable through the ServiceLoader mechanism. */
        Assert.assertThat(StreamSupport.stream(Spliterators.spliterator(ClassLoading.servicesOfType(ServiceFactory.class).iterator(), Long.MAX_VALUE, 0), false).map(( f) -> f.getServiceType()).collect(Collectors.toList()), IsCollectionContaining.hasItems(Provider.class, ClusteringService.class));
        CacheConfiguration<String, String> cacheConfiguration = CacheConfigurationBuilder.newCacheConfigurationBuilder(String.class, String.class, ResourcePoolsBuilder.newResourcePoolsBuilder().heap(10, EntryUnit.ENTRIES).offheap(1, MemoryUnit.MB).build()).build();
        CacheManager cacheManager = CacheManagerBuilder.newCacheManagerBuilder().build(true);
        cacheManager.createCache("cache-1", cacheConfiguration);
        cacheManager.createCache("cache-2", cacheConfiguration);
        cacheManager.close();
    }
}

