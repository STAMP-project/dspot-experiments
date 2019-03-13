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
package org.ehcache.integration.statistics;


import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.StreamSupport;
import org.assertj.core.data.MapEntry;
import org.ehcache.Cache;
import org.ehcache.CacheManager;
import org.ehcache.config.builders.ResourcePoolsBuilder;
import org.junit.Test;


/**
 * Check that calculations are accurate according to specification. Each cache method have a different impact on the statistics
 * so each method should be tested
 */
public class CacheCalculationTest extends AbstractCacheCalculationTest {
    private CacheManager cacheManager;

    private Cache<Integer, String> cache;

    public CacheCalculationTest(ResourcePoolsBuilder poolBuilder) {
        super(poolBuilder);
    }

    @Test
    public void clear() {
        cache.put(1, "a");
        cache.put(2, "b");
        changesOf(0, 0, 2, 0);
        cache.clear();
        changesOf(0, 0, 0, 0);
    }

    @Test
    public void containsKey() {
        AbstractCalculationTest.expect(cache.containsKey(1)).isFalse();
        changesOf(0, 0, 0, 0);
        cache.put(1, "a");
        changesOf(0, 0, 1, 0);
        AbstractCalculationTest.expect(cache.containsKey(1)).isTrue();
        changesOf(0, 0, 0, 0);
    }

    @Test
    public void get() {
        AbstractCalculationTest.expect(cache.get(1)).isNull();
        changesOf(0, 1, 0, 0);
        cache.put(1, "a");
        changesOf(0, 0, 1, 0);
        AbstractCalculationTest.expect(cache.get(1)).isEqualTo("a");
        changesOf(1, 0, 0, 0);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void getAll() {
        AbstractCalculationTest.expect(cache.getAll(AbstractCalculationTest.asSet(1))).containsExactly(MapEntry.entry(1, null));
        changesOf(0, 1, 0, 0);
        cache.put(1, "a");
        cache.put(2, "b");
        changesOf(0, 0, 2, 0);
        AbstractCalculationTest.expect(cache.getAll(AbstractCalculationTest.asSet(1, 2, 3))).containsKeys(1, 2);
        changesOf(2, 1, 0, 0);
    }

    @Test
    public void iterator() {
        cache.put(1, "a");
        cache.put(2, "b");
        changesOf(0, 0, 2, 0);
        Iterator<Cache.Entry<Integer, String>> iterator = cache.iterator();
        changesOf(0, 0, 0, 0);
        iterator.next().getKey();
        changesOf(1, 0, 0, 0);
        AbstractCalculationTest.expect(iterator.hasNext()).isTrue();
        changesOf(0, 0, 0, 0);
        iterator.next().getKey();
        changesOf(1, 0, 0, 0);
        AbstractCalculationTest.expect(iterator.hasNext()).isFalse();
        changesOf(0, 0, 0, 0);
        iterator.remove();
        changesOf(1, 0, 0, 1);// FIXME remove does hit

    }

    @Test
    public void foreach() {
        cache.put(1, "a");
        cache.put(2, "b");
        cache.put(3, "c");
        changesOf(0, 0, 3, 0);
        cache.forEach(( e) -> {
        });
        changesOf(3, 0, 0, 0);
    }

    @Test
    public void spliterator() {
        cache.put(1, "a");
        cache.put(2, "b");
        cache.put(3, "c");
        changesOf(0, 0, 3, 0);
        StreamSupport.stream(cache.spliterator(), false).forEach(( e) -> {
        });
        changesOf(3, 0, 0, 0);
    }

    @Test
    public void put() {
        cache.put(1, "a");
        changesOf(0, 0, 1, 0);
        cache.put(1, "b");
        changesOf(0, 0, 1, 0);
    }

    @Test
    public void putAll() {
        Map<Integer, String> vals = new HashMap<>();
        vals.put(1, "a");
        vals.put(2, "b");
        cache.putAll(vals);
        changesOf(0, 0, 2, 0);
        vals.put(3, "c");
        cache.putAll(vals);
        changesOf(0, 0, 3, 0);
    }

    @Test
    public void putIfAbsent() {
        AbstractCalculationTest.expect(cache.putIfAbsent(1, "a")).isNull();
        changesOf(0, 1, 1, 0);
        AbstractCalculationTest.expect(cache.putIfAbsent(1, "b")).isEqualTo("a");
        changesOf(1, 0, 0, 0);
    }

    @Test
    public void remove() {
        cache.remove(1);
        changesOf(0, 0, 0, 0);
        cache.put(1, "a");
        changesOf(0, 0, 1, 0);
        cache.remove(1);
        changesOf(0, 0, 0, 1);
    }

    @Test
    public void removeKV() {
        AbstractCalculationTest.expect(cache.remove(1, "a")).isFalse();
        changesOf(0, 1, 0, 0);
        cache.put(1, "a");
        changesOf(0, 0, 1, 0);
        AbstractCalculationTest.expect(cache.remove(1, "xxx")).isFalse();
        changesOf(1, 0, 0, 0);
        AbstractCalculationTest.expect(cache.remove(1, "a")).isTrue();
        changesOf(1, 0, 0, 1);
    }

    @Test
    public void removeAllKeys() {
        cache.put(1, "a");
        cache.put(2, "b");
        changesOf(0, 0, 2, 0);
        cache.removeAll(AbstractCalculationTest.asSet(1, 2, 3));
        changesOf(0, 0, 0, 2);
    }

    @Test
    public void replaceKV() {
        AbstractCalculationTest.expect(cache.replace(1, "a")).isNull();
        changesOf(0, 1, 0, 0);
        cache.put(1, "a");
        changesOf(0, 0, 1, 0);
        AbstractCalculationTest.expect(cache.replace(1, "b")).isEqualTo("a");
        changesOf(1, 0, 1, 0);
    }

    @Test
    public void replaceKON() {
        AbstractCalculationTest.expect(cache.replace(1, "a", "b")).isFalse();
        changesOf(0, 1, 0, 0);
        cache.put(1, "a");
        changesOf(0, 0, 1, 0);
        AbstractCalculationTest.expect(cache.replace(1, "xxx", "b")).isFalse();
        changesOf(1, 0, 0, 0);
        AbstractCalculationTest.expect(cache.replace(1, "a", "b")).isTrue();
        changesOf(1, 0, 1, 0);
    }

    @Test
    public void testClearingStats() {
        // We do it twice because the second time we already have compensating counters, so the result might fail
        innerClear();
        innerClear();
    }

    @Test
    public void testEviction() {
        String payload = "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa";
        // Wait until we reach the maximum that we can fit in
        int i = 0;
        long evictions;
        do {
            cache.put((i++), payload);
            evictions = cacheStatistics.getCacheEvictions();
        } while ((evictions == 0) && (i < 100000) );
        assertThat(evictions).isGreaterThan(0);
    }
}

