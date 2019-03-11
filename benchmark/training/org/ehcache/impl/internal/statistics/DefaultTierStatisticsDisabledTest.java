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
package org.ehcache.impl.internal.statistics;


import org.ehcache.Cache;
import org.ehcache.CacheManager;
import org.ehcache.internal.TestTimeSource;
import org.junit.Test;


/**
 * Test the behavior of statistics when they are disabled (the default with one tier) on a store.
 */
public class DefaultTierStatisticsDisabledTest {
    private static final int TIME_TO_EXPIRATION = 100;

    private DefaultTierStatistics onHeap;

    private CacheManager cacheManager;

    private Cache<Long, String> cache;

    private TestTimeSource timeSource = new TestTimeSource(System.currentTimeMillis());

    @Test
    public void getKnownStatistics() {
        // Passthrough are there. Special ones needed for the cache statistics are there
        assertThat(onHeap.getKnownStatistics()).containsOnlyKeys("OnHeap:EvictionCount", "OnHeap:ExpirationCount", "OnHeap:MappingCount");
    }

    @Test
    public void getHits() {
        cache.put(1L, "a");
        cache.get(1L);
        assertThat(onHeap.getHits()).isEqualTo(0L);
        assertNoStat("OnHeap:HitCount");
    }

    @Test
    public void getMisses() {
        cache.get(1L);
        assertThat(onHeap.getMisses()).isEqualTo(0L);
        assertNoStat("OnHeap:MissCount");
    }

    @Test
    public void getPuts() {
        cache.put(1L, "a");
        assertThat(onHeap.getPuts()).isEqualTo(0L);
        assertNoStat("OnHeap:PutCount");
    }

    @Test
    public void getUpdates() {
        cache.put(1L, "a");
        cache.put(1L, "b");
        assertThat(onHeap.getPuts()).isEqualTo(0L);
        assertNoStat("OnHeap:PutCount");
    }

    @Test
    public void getRemovals() {
        cache.put(1L, "a");
        cache.remove(1L);
        assertThat(onHeap.getRemovals()).isEqualTo(0L);
        assertNoStat("OnHeap:RemovalCount");
    }

    @Test
    public void getEvictions() {
        for (long i = 0; i < 11; i++) {
            cache.put(i, "a");
        }
        assertThat(onHeap.getEvictions()).isEqualTo(1L);
        assertStat("OnHeap:EvictionCount").isEqualTo(1L);
    }

    @Test
    public void getExpirations() {
        cache.put(1L, "a");
        timeSource.advanceTime(DefaultTierStatisticsDisabledTest.TIME_TO_EXPIRATION);
        cache.get(1L);
        assertThat(onHeap.getExpirations()).isEqualTo(1L);
        assertStat("OnHeap:ExpirationCount").isEqualTo(1L);
    }

    @Test
    public void getMappings() {
        cache.put(1L, "a");
        assertThat(onHeap.getMappings()).isEqualTo(1L);
        assertStat("OnHeap:MappingCount").isEqualTo(1L);
    }

    @Test
    public void getAllocatedByteSize() {
        cache.put(1L, "a");
        assertThat(onHeap.getAllocatedByteSize()).isEqualTo((-1L));
    }

    @Test
    public void getOccupiedByteSize() {
        cache.put(1L, "a");
        assertThat(onHeap.getOccupiedByteSize()).isEqualTo((-1L));
    }
}

