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
package org.ehcache.integration;


import java.time.Duration;
import org.ehcache.Cache;
import org.ehcache.CacheManager;
import org.ehcache.config.builders.CacheConfigurationBuilder;
import org.ehcache.config.builders.ExpiryPolicyBuilder;
import org.ehcache.config.builders.ResourcePoolsBuilder;
import org.ehcache.config.units.EntryUnit;
import org.ehcache.config.units.MemoryUnit;
import org.ehcache.impl.config.copy.DefaultCopierConfiguration;
import org.ehcache.impl.copy.SerializingCopier;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import static org.ehcache.impl.config.copy.DefaultCopierConfiguration.Type.VALUE;


/**
 * Created by alsu on 06/08/15.
 */
public class ExpiryEventsTest {
    private static final ResourcePoolsBuilder resourcePoolsBuilder = ResourcePoolsBuilder.newResourcePoolsBuilder().heap(3, EntryUnit.ENTRIES);

    private static final CacheConfigurationBuilder<Long, String> byRefCacheConfigBuilder = CacheConfigurationBuilder.newCacheConfigurationBuilder(Long.class, String.class, ResourcePoolsBuilder.heap(10)).withExpiry(ExpiryPolicyBuilder.timeToLiveExpiration(Duration.ofSeconds(1)));

    private static final CacheConfigurationBuilder<Long, String> byValueCacheConfigBuilder = ExpiryEventsTest.byRefCacheConfigBuilder.add(new DefaultCopierConfiguration<>(SerializingCopier.<String>asCopierClass(), VALUE));

    private static final TestTimeSource testTimeSource = new TestTimeSource();

    private CacheManager cacheManager;

    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    @Test
    public void testExpiredEventsOnHeapByReference() throws Exception {
        Cache<Long, String> testCache = cacheManager.createCache("onHeapCache", ExpiryEventsTest.byRefCacheConfigBuilder.build());
        performActualTest(testCache);
    }

    @Test
    public void testExpiredEventsOnHeapByValue() throws Exception {
        Cache<Long, String> testCache = cacheManager.createCache("onHeapCache", ExpiryEventsTest.byValueCacheConfigBuilder.build());
        performActualTest(testCache);
    }

    @Test
    public void testExpiredEventsOnHeapAndOffHeapByReference() throws Exception {
        CacheConfigurationBuilder<Long, String> configBuilder = ExpiryEventsTest.byRefCacheConfigBuilder.withResourcePools(ExpiryEventsTest.resourcePoolsBuilder.offheap(1, MemoryUnit.MB));
        Cache<Long, String> testCache = cacheManager.createCache("onHeapOffHeapCache", configBuilder.build());
        performActualTest(testCache);
    }

    @Test
    public void testExpiredEventsOnHeapAndOffHeapByValue() throws Exception {
        CacheConfigurationBuilder<Long, String> configBuilder = ExpiryEventsTest.byValueCacheConfigBuilder.withResourcePools(ExpiryEventsTest.resourcePoolsBuilder.offheap(1, MemoryUnit.MB));
        Cache<Long, String> testCache = cacheManager.createCache("onHeapOffHeapCache", configBuilder.build());
        performActualTest(testCache);
    }

    @Test
    public void testExpiredEventsOnHeapAndDiskByReference() throws Exception {
        CacheConfigurationBuilder<Long, String> configBuilder = ExpiryEventsTest.byRefCacheConfigBuilder.withResourcePools(ExpiryEventsTest.resourcePoolsBuilder.disk(1, MemoryUnit.MB));
        Cache<Long, String> testCache = cacheManager.createCache("onHeapDiskCache", configBuilder.build());
        performActualTest(testCache);
    }

    @Test
    public void testExpiredEventsOnHeapAndDiskByValue() throws Exception {
        CacheConfigurationBuilder<Long, String> configBuilder = ExpiryEventsTest.byValueCacheConfigBuilder.withResourcePools(ExpiryEventsTest.resourcePoolsBuilder.disk(1, MemoryUnit.MB));
        Cache<Long, String> testCache = cacheManager.createCache("onHeapDiskCache", configBuilder.build());
        performActualTest(testCache);
    }

    @Test
    public void testExpiredEventsOnHeapAndOffHeapAndDiskByReference() throws Exception {
        CacheConfigurationBuilder<Long, String> configBuilder = ExpiryEventsTest.byRefCacheConfigBuilder.withResourcePools(ExpiryEventsTest.resourcePoolsBuilder.offheap(1, MemoryUnit.MB).disk(2, MemoryUnit.MB));
        Cache<Long, String> testCache = cacheManager.createCache("onHeapOffHeapDiskCache", configBuilder.build());
        performActualTest(testCache);
    }

    @Test
    public void testExpiredEventsOnHeapAndOffHeapAndDiskByValue() throws Exception {
        CacheConfigurationBuilder<Long, String> configBuilder = ExpiryEventsTest.byValueCacheConfigBuilder.withResourcePools(ExpiryEventsTest.resourcePoolsBuilder.offheap(1, MemoryUnit.MB).disk(2, MemoryUnit.MB));
        Cache<Long, String> testCache = cacheManager.createCache("onHeapOffHeapDiskCache", configBuilder.build());
        performActualTest(testCache);
    }
}

