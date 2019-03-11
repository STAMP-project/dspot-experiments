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


import Cache.Entry;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.stream.StreamSupport;
import javax.cache.Cache;
import javax.cache.CacheManager;
import org.ehcache.config.builders.ResourcePoolsBuilder;
import org.junit.Test;


/**
 * Check that calculations are accurate according to specification. Each cache method have a different impact on the statistics
 * so each method should be tested
 */
public class JCacheCalculationTest extends AbstractCacheCalculationTest {
    private CacheManager cacheManager;

    private Cache<Integer, String> cache;

    public JCacheCalculationTest(ResourcePoolsBuilder poolBuilder) {
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

    @Test
    public void getAll() {
        AbstractCalculationTest.expect(cache.getAll(AbstractCalculationTest.asSet(1))).isEmpty();
        changesOf(0, 1, 0, 0);
        cache.put(1, "a");
        cache.put(2, "b");
        changesOf(0, 0, 2, 0);
        AbstractCalculationTest.expect(cache.getAll(AbstractCalculationTest.asSet(1, 2, 3))).containsKeys(1, 2);
        changesOf(2, 1, 0, 0);
    }

    @Test
    public void getAndPut() {
        AbstractCalculationTest.expect(cache.getAndPut(1, "a")).isNull();
        changesOf(0, 1, 1, 0);
        cache.getAndPut(1, "b");
        changesOf(1, 0, 1, 0);
        cache.getAndPut(1, "b");// again with the same value

        changesOf(1, 0, 1, 0);
    }

    @Test
    public void getAndRemove() {
        AbstractCalculationTest.expect(cache.getAndRemove(1)).isNull();
        changesOf(0, 1, 0, 0);
        cache.put(1, "a");
        changesOf(0, 0, 1, 0);
        AbstractCalculationTest.expect(cache.getAndRemove(1)).isEqualTo("a");
        changesOf(1, 0, 0, 1);
    }

    @Test
    public void getAndReplace() {
        AbstractCalculationTest.expect(cache.getAndReplace(1, "a")).isNull();
        changesOf(0, 1, 0, 0);
        cache.put(1, "a");
        changesOf(0, 0, 1, 0);
        AbstractCalculationTest.expect(cache.getAndReplace(1, "b")).isEqualTo("a");
        changesOf(1, 0, 1, 0);
    }

    @Test
    public void invoke() {
        AbstractCalculationTest.expect(cache.invoke(1, new GetEntryProcessor())).isNull();// miss

        changesOf(0, 1, 0, 0);
        AbstractCalculationTest.expect(cache.invoke(1, new GetKeyEntryProcessor())).isEqualTo(1);// miss

        changesOf(0, 1, 0, 0);
        AbstractCalculationTest.expect(cache.invoke(1, new ExistEntryProcessor())).isEqualTo(false);// miss

        changesOf(0, 1, 0, 0);
        AbstractCalculationTest.expect(cache.invoke(1, new SetEntryProcessor("a"))).isEqualTo("a");// put

        changesOf(0, 1, 1, 0);// FIXME Why is there a miss?

        AbstractCalculationTest.expect(cache.invoke(1, new SetEntryProcessor("b"))).isEqualTo("b");// update

        changesOf(1, 0, 1, 0);
        AbstractCalculationTest.expect(cache.invoke(1, new GetEntryProcessor())).isEqualTo("b");// hit

        changesOf(1, 0, 0, 0);
        AbstractCalculationTest.expect(cache.invoke(1, new GetKeyEntryProcessor())).isEqualTo(1);// hit

        changesOf(1, 0, 0, 0);
        AbstractCalculationTest.expect(cache.invoke(1, new ExistEntryProcessor())).isEqualTo(true);// hit

        changesOf(1, 0, 0, 0);
        AbstractCalculationTest.expect(cache.invoke(1, new RemoveEntryProcessor())).isNull();// hit

        changesOf(1, 0, 0, 1);// FIXME Why is there a hit?

        AbstractCalculationTest.expect(cache.invoke(1, new RemoveEntryProcessor())).isNull();// miss

        changesOf(0, 1, 0, 1);// FIXME Why is there a remove?

    }

    @Test
    public void invokeAll() {
        Set<Integer> keys = AbstractCalculationTest.asSet(1, 2, 3);
        cache.invokeAll(keys, new GetEntryProcessor());// miss

        changesOf(0, 3, 0, 0);
        cache.invokeAll(keys, new GetKeyEntryProcessor());// miss

        changesOf(0, 3, 0, 0);
        cache.invokeAll(keys, new ExistEntryProcessor());// miss

        changesOf(0, 3, 0, 0);
        cache.invokeAll(keys, new SetEntryProcessor("a"));// put

        changesOf(0, 3, 3, 0);// FIXME Why is there misses?

        cache.invokeAll(keys, new SetEntryProcessor("b"));// update

        changesOf(3, 0, 3, 0);// FIXME Why is there hits?

        cache.invokeAll(keys, new GetEntryProcessor());// hit

        changesOf(3, 0, 0, 0);
        cache.invokeAll(keys, new GetKeyEntryProcessor());// hit

        changesOf(3, 0, 0, 0);
        cache.invokeAll(keys, new ExistEntryProcessor());// hit

        changesOf(3, 0, 0, 0);
        cache.invokeAll(AbstractCalculationTest.asSet(2, 3, 4), new GetEntryProcessor());// asymetric get

        changesOf(2, 1, 0, 0);
        cache.invokeAll(keys, new RemoveEntryProcessor());// hit

        changesOf(3, 0, 0, 3);// FIXME Why is there a hit?

        cache.invokeAll(keys, new RemoveEntryProcessor());// miss

        changesOf(0, 3, 0, 3);// FIXME Why is there a remove?

    }

    @Test
    public void iterator() {
        cache.put(1, "a");
        cache.put(2, "b");
        changesOf(0, 0, 2, 0);
        Iterator<Entry<Integer, String>> iterator = cache.iterator();
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
        changesOf(0, 0, 0, 1);
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
    public void loadAll() throws InterruptedException {
        // Skipping loadAll for now
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
        AbstractCalculationTest.expect(cache.putIfAbsent(1, "a")).isTrue();
        changesOf(0, 1, 1, 0);
        AbstractCalculationTest.expect(cache.putIfAbsent(1, "b")).isFalse();
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
    public void removeAll() {
        cache.put(1, "a");
        cache.put(2, "b");
        changesOf(0, 0, 2, 0);
        cache.removeAll();
        changesOf(0, 0, 0, 2);
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
        AbstractCalculationTest.expect(cache.replace(1, "a")).isFalse();
        changesOf(0, 1, 0, 0);
        cache.put(1, "a");
        changesOf(0, 0, 1, 0);
        AbstractCalculationTest.expect(cache.replace(1, "b")).isTrue();
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
}

