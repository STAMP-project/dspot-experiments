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


public class DefaultTierStatisticsTest {
    private static final int TIME_TO_EXPIRATION = 100;

    private DefaultTierStatistics onHeap;

    private CacheManager cacheManager;

    private Cache<Long, String> cache;

    private TestTimeSource timeSource = new TestTimeSource(System.currentTimeMillis());

    @Test
    public void getKnownStatistics() {
        assertThat(onHeap.getKnownStatistics()).containsOnlyKeys("OnHeap:HitCount", "OnHeap:MissCount", "OnHeap:PutCount", "OnHeap:RemovalCount", "OnHeap:EvictionCount", "OnHeap:ExpirationCount", "OnHeap:MappingCount");
    }

    @Test
    public void getHits() throws Exception {
        cache.put(1L, "a");
        cache.get(1L);
        assertThat(onHeap.getHits()).isEqualTo(1L);
        assertStat("OnHeap:HitCount").isEqualTo(1L);
    }

    @Test
    public void getMisses() throws Exception {
        cache.get(1L);
        assertThat(onHeap.getMisses()).isEqualTo(1L);
        assertStat("OnHeap:MissCount").isEqualTo(1L);
    }

    @Test
    public void getPuts() throws Exception {
        cache.put(1L, "a");
        assertThat(onHeap.getPuts()).isEqualTo(1L);
        assertStat("OnHeap:PutCount").isEqualTo(1L);
    }

    @Test
    public void getUpdates() throws Exception {
        cache.put(1L, "a");
        cache.put(1L, "b");
        assertThat(onHeap.getPuts()).isEqualTo(2L);
        assertStat("OnHeap:PutCount").isEqualTo(2L);
    }

    @Test
    public void getRemovals() throws Exception {
        cache.put(1L, "a");
        cache.remove(1L);
        assertThat(onHeap.getRemovals()).isEqualTo(1L);
        assertStat("OnHeap:RemovalCount").isEqualTo(1L);
    }

    @Test
    public void getEvictions() throws Exception {
        for (long i = 0; i < 11; i++) {
            cache.put(i, "a");
        }
        assertThat(onHeap.getEvictions()).isEqualTo(1L);
        assertStat("OnHeap:EvictionCount").isEqualTo(1L);
    }

    @Test
    public void getExpirations() throws Exception {
        cache.put(1L, "a");
        timeSource.advanceTime(DefaultTierStatisticsTest.TIME_TO_EXPIRATION);
        cache.get(1L);
        assertThat(onHeap.getExpirations()).isEqualTo(1L);
        assertStat("OnHeap:ExpirationCount").isEqualTo(1L);
    }

    @Test
    public void getMappings() throws Exception {
        cache.put(1L, "a");
        assertThat(onHeap.getMappings()).isEqualTo(1L);
        assertStat("OnHeap:MappingCount").isEqualTo(1L);
    }

    @Test
    public void getAllocatedByteSize() throws Exception {
        cache.put(1L, "a");
        assertThat(onHeap.getAllocatedByteSize()).isEqualTo((-1L));
    }

    @Test
    public void getOccupiedByteSize() throws Exception {
        cache.put(1L, "a");
        assertThat(onHeap.getOccupiedByteSize()).isEqualTo((-1L));
    }
}

