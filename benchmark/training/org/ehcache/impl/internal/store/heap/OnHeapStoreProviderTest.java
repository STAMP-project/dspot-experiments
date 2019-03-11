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


import java.util.Collections;
import org.ehcache.config.ResourcePool;
import org.ehcache.config.ResourceType;
import org.ehcache.impl.internal.util.UnmatchedResourceType;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;

import static org.ehcache.config.ResourceType.Core.DISK;
import static org.ehcache.config.ResourceType.Core.HEAP;
import static org.ehcache.config.ResourceType.Core.OFFHEAP;


/**
 * Basic tests for {@link org.ehcache.impl.internal.store.heap.OnHeapStore.Provider}.
 */
public class OnHeapStoreProviderTest {
    @Test
    public void testRank() throws Exception {
        OnHeapStore.Provider provider = new OnHeapStore.Provider();
        assertRank(provider, 1, HEAP);
        assertRank(provider, 0, DISK);
        assertRank(provider, 0, OFFHEAP);
        assertRank(provider, 0, DISK, OFFHEAP);
        assertRank(provider, 0, DISK, HEAP);
        assertRank(provider, 0, OFFHEAP, HEAP);
        assertRank(provider, 0, DISK, OFFHEAP, HEAP);
        final ResourceType<ResourcePool> unmatchedResourceType = new ResourceType<ResourcePool>() {
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
        };
        assertRank(provider, 0, unmatchedResourceType);
        assertRank(provider, 0, HEAP, unmatchedResourceType);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testRankCachingTier() throws Exception {
        OnHeapStore.Provider provider = new OnHeapStore.Provider();
        MatcherAssert.assertThat(provider.rankCachingTier(Collections.<ResourceType<?>>singleton(HEAP), Collections.EMPTY_LIST), Matchers.is(1));
        MatcherAssert.assertThat(provider.rankCachingTier(Collections.<ResourceType<?>>singleton(new UnmatchedResourceType()), Collections.EMPTY_LIST), Matchers.is(0));
    }
}

