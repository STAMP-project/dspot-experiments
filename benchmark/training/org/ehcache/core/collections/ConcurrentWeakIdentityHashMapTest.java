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
package org.ehcache.core.collections;


import java.util.AbstractMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import org.hamcrest.Matchers;
import org.hamcrest.core.Is;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Alex Snaps
 */
public class ConcurrentWeakIdentityHashMapTest {
    @Test
    public void testBasicOperations() {
        final ConcurrentWeakIdentityHashMap<Integer, String> map = new ConcurrentWeakIdentityHashMap<>();
        final Integer key = 1;
        final String firstValue = "foo";
        final String otherValue = "bar";
        Assert.assertThat(map.containsKey(key), Is.is(false));
        Assert.assertThat(map.get(key), Matchers.nullValue());
        Assert.assertThat(map.put(key, firstValue), Matchers.nullValue());
        Assert.assertThat(map.containsKey(key), Is.is(true));
        Assert.assertThat(map.putIfAbsent(key, otherValue), Is.is(firstValue));
        Assert.assertThat(map.replace(key, otherValue, firstValue), Is.is(false));
        Assert.assertThat(map.get(key), Is.is(firstValue));
        Assert.assertThat(map.replace(key, firstValue, otherValue), Is.is(true));
        Assert.assertThat(map.get(key), Is.is(otherValue));
        Assert.assertThat(map.remove(key, firstValue), Is.is(false));
        Assert.assertThat(map.get(key), Is.is(otherValue));
        Assert.assertThat(map.containsKey(key), Is.is(true));
        Assert.assertThat(map.remove(key, otherValue), Is.is(true));
        Assert.assertThat(map.containsKey(key), Is.is(false));
        Assert.assertThat(map.get(key), Matchers.nullValue());
        Assert.assertThat(map.putIfAbsent(key, otherValue), Matchers.nullValue());
        Assert.assertThat(map.get(key), Is.is(otherValue));
        Assert.assertThat(map.remove(key), Is.is(otherValue));
        Assert.assertThat(map.containsKey(key), Is.is(false));
        Assert.assertThat(map.get(key), Matchers.nullValue());
    }

    @Test
    public void testSizeAccountsForGCedKeys() {
        final ConcurrentWeakIdentityHashMap<Object, String> map = new ConcurrentWeakIdentityHashMap<>();
        final String v = "present";
        addToMap(map, "gone");
        map.put(1, v);
        while ((map.size()) > 1) {
            System.gc();
        } 
        Assert.assertThat(map.values().size(), Is.is(1));
        Assert.assertThat(map.values().iterator().next(), Is.is(v));
    }

    @Test
    public void testRemoveAccountsForReference() {
        final ConcurrentWeakIdentityHashMap<Object, String> map = new ConcurrentWeakIdentityHashMap<>();
        final Integer key = 1;
        final String v = "present";
        map.put(key, v);
        Assert.assertThat(map.remove(key), Is.is(v));
    }

    @Test
    public void testIteration() throws InterruptedException {
        final ConcurrentWeakIdentityHashMap<Object, Integer> map = new ConcurrentWeakIdentityHashMap<>();
        int i = 0;
        while (i < 10240) {
            if ((i % 1024) == 0) {
                map.put((i++), i);
            } else {
                addToMap(map, (i++));
            }
        } 
        System.gc();
        System.gc();
        Thread.sleep(500);
        System.gc();
        int size = 0;
        // This relies on the entrySet keeping a hard ref to all keys in the map at invocation time
        for (Map.Entry<Object, Integer> entry : map.entrySet()) {
            i--;
            size = map.size();
            Assert.assertThat(entry.getKey(), Matchers.notNullValue());
        }
        Assert.assertThat(i, Matchers.not(Is.is(0)));
        Assert.assertThat(size, Matchers.not(Is.is(0)));
    }

    @Test
    public void testKeySetSize() throws Exception {
        ConcurrentWeakIdentityHashMap<String, String> map = new ConcurrentWeakIdentityHashMap<>();
        Set<String> keys = map.keySet();
        String key1 = "key1";
        String key2 = "key2";
        String key3 = "key3";
        map.put(key1, "value1");
        map.put(key2, "value2");
        map.put(key3, "value3");
        Assert.assertThat(keys.size(), Is.is(3));
    }

    @Test
    public void testKeySetContainsReflectsMapChanges() throws Exception {
        ConcurrentWeakIdentityHashMap<String, String> map = new ConcurrentWeakIdentityHashMap<>();
        Set<String> keys = map.keySet();
        String key1 = "key1";
        String key2 = "key2";
        String key3 = "key3";
        map.put(key1, "value1");
        map.put(key2, "value2");
        map.put(key3, "value3");
        Assert.assertThat(keys.contains(key1), Is.is(true));
        Assert.assertThat(keys.contains(key2), Is.is(true));
        Assert.assertThat(keys.contains(key3), Is.is(true));
    }

    @Test
    public void testKeySetIteratorReflectsMapChanges() {
        ConcurrentWeakIdentityHashMap<String, String> map = new ConcurrentWeakIdentityHashMap<>();
        Set<String> keys = map.keySet();
        String key1 = "key1";
        String key2 = "key2";
        String key3 = "key3";
        map.put(key1, "value1");
        map.put(key2, "value2");
        map.put(key3, "value3");
        Iterator<String> iterator = keys.iterator();
        Assert.assertThat(iterator.hasNext(), Is.is(true));
        Assert.assertThat(iterator.next(), Matchers.startsWith("key"));
        Assert.assertThat(iterator.hasNext(), Is.is(true));
        Assert.assertThat(iterator.next(), Matchers.startsWith("key"));
        Assert.assertThat(iterator.hasNext(), Is.is(true));
        Assert.assertThat(iterator.next(), Matchers.startsWith("key"));
        Assert.assertThat(iterator.hasNext(), Is.is(false));
    }

    @Test
    public void testEntrySetSize() throws Exception {
        ConcurrentWeakIdentityHashMap<String, String> map = new ConcurrentWeakIdentityHashMap<>();
        Set<Map.Entry<String, String>> entries = map.entrySet();
        String key1 = "key1";
        String key2 = "key2";
        String key3 = "key3";
        map.put(key1, "value1");
        map.put(key2, "value2");
        map.put(key3, "value3");
        Assert.assertThat(entries.size(), Is.is(3));
    }

    @Test
    public void testEntrySetContainsReflectsMapChanges() throws Exception {
        ConcurrentWeakIdentityHashMap<String, String> map = new ConcurrentWeakIdentityHashMap<>();
        Set<Map.Entry<String, String>> entries = map.entrySet();
        String key1 = "key1";
        String key2 = "key2";
        String key3 = "key3";
        map.put(key1, "value1");
        map.put(key2, "value2");
        map.put(key3, "value3");
        Assert.assertThat(entries.contains(new AbstractMap.SimpleEntry<>("key1", "value1")), Is.is(true));
        Assert.assertThat(entries.contains(new AbstractMap.SimpleEntry<>("key1", "value1")), Is.is(true));
        Assert.assertThat(entries.contains(new AbstractMap.SimpleEntry<>("key1", "value1")), Is.is(true));
    }

    @Test
    public void testEntrySetIteratorReflectsMapChanges() {
        ConcurrentWeakIdentityHashMap<String, String> map = new ConcurrentWeakIdentityHashMap<>();
        Set<Map.Entry<String, String>> entries = map.entrySet();
        String key1 = "key1";
        String key2 = "key2";
        String key3 = "key3";
        map.put(key1, "value1");
        map.put(key2, "value2");
        map.put(key3, "value3");
        Iterator<Map.Entry<String, String>> iterator = entries.iterator();
        Assert.assertThat(iterator.hasNext(), Is.is(true));
        Assert.assertThat(iterator.next().getKey(), Matchers.startsWith("key"));
        Assert.assertThat(iterator.hasNext(), Is.is(true));
        Assert.assertThat(iterator.next().getKey(), Matchers.startsWith("key"));
        Assert.assertThat(iterator.hasNext(), Is.is(true));
        Assert.assertThat(iterator.next().getKey(), Matchers.startsWith("key"));
        Assert.assertThat(iterator.hasNext(), Is.is(false));
    }
}

