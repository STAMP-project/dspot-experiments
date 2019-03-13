/**
 * Copyright (c) 2008-2019, Hazelcast, Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.hazelcast.map;


import IMap.Entry;
import com.hazelcast.core.IMap;
import com.hazelcast.test.HazelcastParallelClassRunner;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;


@RunWith(HazelcastParallelClassRunner.class)
@Category({ QuickTest.class, ParallelTest.class })
public class MapPredicateTest extends HazelcastTestSupport {
    @Test
    public void testMapKeySet() {
        IMap<String, String> map = getMapWithNodeCount(3);
        map.put("key1", "value1");
        map.put("key2", "value2");
        map.put("key3", "value3");
        List<String> listExpected = new ArrayList<String>();
        listExpected.add("key1");
        listExpected.add("key2");
        listExpected.add("key3");
        final List<String> list = new ArrayList<String>(map.keySet());
        Collections.sort(list);
        Collections.sort(listExpected);
        Assert.assertEquals(listExpected, list);
    }

    @Test
    public void testMapLocalKeySet() {
        IMap<String, String> map = getMapWithNodeCount(3);
        map.put("key1", "value1");
        map.put("key2", "value2");
        map.put("key3", "value3");
        List<String> listExpected = new ArrayList<String>();
        listExpected.add("key1");
        listExpected.add("key2");
        listExpected.add("key3");
        final List<String> list = new ArrayList<String>(map.keySet());
        Collections.sort(list);
        Collections.sort(listExpected);
        Assert.assertEquals(listExpected, list);
    }

    @Test
    public void testMapValues() {
        IMap<String, String> map = getMapWithNodeCount(3);
        map.put("key1", "value1");
        map.put("key2", "value2");
        map.put("key3", "value3");
        map.put("key4", "value3");
        List<String> values = new ArrayList<String>(map.values());
        List<String> expected = new ArrayList<String>();
        expected.add("value1");
        expected.add("value2");
        expected.add("value3");
        expected.add("value3");
        Collections.sort(values);
        Collections.sort(expected);
        Assert.assertEquals(expected, values);
    }

    @Test
    public void valuesToArray() {
        IMap<String, String> map = getMapWithNodeCount(3);
        Assert.assertEquals(0, map.size());
        map.put("a", "1");
        map.put("b", "2");
        map.put("c", "3");
        Assert.assertEquals(3, map.size());
        // toArray() without array
        Object[] values = map.values().toArray();
        Arrays.sort(values);
        Assert.assertArrayEquals(new Object[]{ "1", "2", "3" }, values);
        // toArray() with empty array (too small)
        values = map.values().toArray(new String[0]);
        Arrays.sort(values);
        Assert.assertArrayEquals(new String[]{ "1", "2", "3" }, values);
        // toArray() with correctly sized array
        values = map.values().toArray(new String[3]);
        Arrays.sort(values);
        Assert.assertArrayEquals(new String[]{ "1", "2", "3" }, values);
        // toArray() with too large array
        values = map.values().toArray(new String[5]);
        Arrays.sort(values, 0, 3);
        Assert.assertArrayEquals(new String[]{ "1", "2", "3", null, null }, values);
    }

    @Test
    public void testMapEntrySetWhenRemoved() {
        IMap<String, String> map = getMapWithNodeCount(3);
        map.put("Hello", "World");
        map.remove("Hello");
        Set<Entry<String, String>> set = map.entrySet();
        for (Entry<String, String> e : set) {
            Assert.fail(("Iterator should not contain removed entry, found " + (e.getKey())));
        }
    }

    @Test
    public void testEntrySet() {
        IMap<Integer, Integer> map = getMapWithNodeCount(3);
        map.put(1, 1);
        map.put(2, 2);
        map.put(3, 3);
        map.put(4, 4);
        map.put(5, 5);
        Set<Map.Entry<Integer, Integer>> entrySet = new HashSet<Map.Entry<Integer, Integer>>();
        entrySet.add(new AbstractMap.SimpleImmutableEntry<Integer, Integer>(1, 1));
        entrySet.add(new AbstractMap.SimpleImmutableEntry<Integer, Integer>(2, 2));
        entrySet.add(new AbstractMap.SimpleImmutableEntry<Integer, Integer>(3, 3));
        entrySet.add(new AbstractMap.SimpleImmutableEntry<Integer, Integer>(4, 4));
        entrySet.add(new AbstractMap.SimpleImmutableEntry<Integer, Integer>(5, 5));
        Assert.assertEquals(entrySet, map.entrySet());
    }
}

