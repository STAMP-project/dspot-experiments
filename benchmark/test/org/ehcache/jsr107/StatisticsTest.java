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
package org.ehcache.jsr107;


import java.util.HashSet;
import javax.cache.Cache;
import javax.cache.CacheManager;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;


/**
 *
 *
 * @author Ludovic Orban
 */
public class StatisticsTest {
    private CacheManager cacheManager;

    private Eh107CacheStatisticsMXBean heapStatistics;

    private Cache<String, String> heapCache;

    private Eh107CacheStatisticsMXBean offheapStatistics;

    private Cache<String, String> offheapCache;

    private Eh107CacheStatisticsMXBean diskStatistics;

    private Cache<String, String> diskCache;

    @Test
    public void test_getCacheGets() throws Exception {
        heapCache.get("key");
        heapCache.get("key");
        heapCache.get("key");
        heapCache.get("key");
        heapCache.get("key");
        MatcherAssert.assertThat(heapStatistics.getCacheGets(), Matchers.is(5L));
    }

    @Test
    public void test_getCachePuts() throws Exception {
        heapCache.put("key", "value");
        heapCache.put("key", "value");
        heapCache.put("key", "value");
        heapCache.put("key", "value");
        heapCache.put("key", "value");
        MatcherAssert.assertThat(heapStatistics.getCachePuts(), Matchers.is(5L));
    }

    @Test
    public void test_getCacheRemovals() throws Exception {
        heapCache.put("key0", "value");
        heapCache.put("key1", "value");
        heapCache.put("key2", "value");
        heapCache.put("key3", "value");
        heapCache.put("key4", "value");
        heapCache.remove("key0");
        heapCache.remove("key1");
        heapCache.remove("key2");
        heapCache.remove("key3");
        heapCache.remove("key4");
        MatcherAssert.assertThat(heapStatistics.getCacheRemovals(), Matchers.is(5L));
    }

    @Test
    public void test_getCacheHits() throws Exception {
        heapCache.put("key", "value");
        heapCache.get("key");
        heapCache.get("key");
        heapCache.get("key");
        heapCache.get("key");
        heapCache.get("key");
        MatcherAssert.assertThat(heapStatistics.getCacheHits(), Matchers.is(5L));
    }

    @Test
    public void test_getCacheMisses() throws Exception {
        heapCache.get("key");
        heapCache.get("key");
        heapCache.get("key");
        heapCache.get("key");
        heapCache.get("key");
        MatcherAssert.assertThat(heapStatistics.getCacheMisses(), Matchers.is(5L));
    }

    @Test
    public void test_getCacheHitsAndMisses() {
        heapCache.put("key1", "value1");
        heapCache.put("key3", "value3");
        heapCache.put("key5", "value5");
        HashSet<String> keys = new HashSet<>(5);
        for (int i = 1; i <= 5; i++) {
            keys.add(("key" + i));
        }
        heapCache.getAll(keys);
        MatcherAssert.assertThat(heapStatistics.getCacheHits(), Matchers.is(3L));
        MatcherAssert.assertThat(heapStatistics.getCacheMisses(), Matchers.is(2L));
    }

    @Test
    public void test_getCacheEvictions_heapOnly() throws Exception {
        for (int i = 0; i < 20; i++) {
            heapCache.put(("key" + i), "value");
        }
        MatcherAssert.assertThat(heapStatistics.getCacheEvictions(), Matchers.is(10L));
    }

    @Test
    public void test_getCacheEvictions_heapAndOffheap() throws Exception {
        String ONE_MB = new String(new byte[1024 * 512]);
        for (int i = 0; i < 20; i++) {
            offheapCache.put(("key" + i), ONE_MB);
        }
        MatcherAssert.assertThat(offheapStatistics.getCacheEvictions(), Matchers.greaterThan(0L));
    }

    @Test
    public void test_getCacheEvictions_heapAndDisk() throws Exception {
        String ONE_MB = new String(new byte[1024 * 512]);
        for (int i = 0; i < 20; i++) {
            diskCache.put(("key" + i), ONE_MB);
        }
        MatcherAssert.assertThat(diskStatistics.getCacheEvictions(), Matchers.greaterThan(0L));
    }

    @Test
    public void test_getCacheHitPercentage() throws Exception {
        heapCache.put("key", "value");
        heapCache.get("key");
        heapCache.get("key");
        heapCache.get("key");
        heapCache.get("nokey");
        heapCache.get("nokey");
        MatcherAssert.assertThat(heapStatistics.getCacheHitPercentage(), Matchers.is(Matchers.allOf(Matchers.greaterThan(59.0F), Matchers.lessThan(61.0F))));
    }

    @Test
    public void test_getCacheMissPercentage() throws Exception {
        heapCache.put("key", "value");
        heapCache.get("key");
        heapCache.get("key");
        heapCache.get("key");
        heapCache.get("nokey");
        heapCache.get("nokey");
        MatcherAssert.assertThat(heapStatistics.getCacheMissPercentage(), Matchers.is(Matchers.allOf(Matchers.greaterThan(39.0F), Matchers.lessThan(41.0F))));
    }

    @Test
    public void test_getAverageGetTime() throws Exception {
        MatcherAssert.assertThat(heapStatistics.getAverageGetTime(), Matchers.is(0.0F));
        heapCache.get("key");
        heapCache.get("key");
        heapCache.get("key");
        heapCache.get("key");
        heapCache.get("key");
        StatisticsTest.assertFor(1100L, () -> heapStatistics.getAverageGetTime(), Matchers.is(Matchers.not(0.0F)));
        MatcherAssert.assertThat(heapStatistics.getAverageGetTime(), Matchers.greaterThan(0.0F));
    }

    @Test
    public void test_getAveragePutTime() throws Exception {
        MatcherAssert.assertThat(heapStatistics.getAveragePutTime(), Matchers.is(0.0F));
        heapCache.put("key", "value");
        heapCache.put("key", "value");
        heapCache.put("key", "value");
        heapCache.put("key", "value");
        heapCache.put("key", "value");
        StatisticsTest.assertFor(1100L, () -> heapStatistics.getAveragePutTime(), Matchers.is(Matchers.not(0.0F)));
        MatcherAssert.assertThat(heapStatistics.getAveragePutTime(), Matchers.greaterThan(0.0F));
    }

    @Test
    public void test_getAverageRemoveTime() throws Exception {
        MatcherAssert.assertThat(heapStatistics.getAverageRemoveTime(), Matchers.is(0.0F));
        heapCache.put("key0", "value");
        heapCache.put("key1", "value");
        heapCache.put("key2", "value");
        heapCache.put("key3", "value");
        heapCache.put("key4", "value");
        heapCache.remove("key0");
        heapCache.remove("key1");
        heapCache.remove("key2");
        heapCache.remove("key3");
        heapCache.remove("key4");
        StatisticsTest.assertFor(1100L, () -> heapStatistics.getAverageRemoveTime(), Matchers.is(Matchers.not(0.0F)));
        MatcherAssert.assertThat(heapStatistics.getAverageRemoveTime(), Matchers.greaterThan(0.0F));
    }
}

