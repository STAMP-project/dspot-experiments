/**
 * Copyright 2017 Alexey Andreev.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.teavm.classlib.java.util;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.teavm.junit.TeaVMTestRunner;


@RunWith(TeaVMTestRunner.class)
public class EnumMapTest {
    @Test
    public void emptyCreated() {
        EnumMap<EnumMapTest.L, String> map = new EnumMap<>(EnumMapTest.L.class);
        Assert.assertEquals(0, map.size());
        Assert.assertFalse(map.entrySet().iterator().hasNext());
        Assert.assertNull(map.get(EnumMapTest.L.A));
        Assert.assertFalse(map.containsKey(EnumMapTest.L.A));
    }

    @Test
    public void createdFromOtherEnumMap() {
        EnumMap<EnumMapTest.L, String> map = new EnumMap<>(EnumMapTest.L.class);
        Assert.assertNull(map.put(EnumMapTest.L.A, "A"));
        EnumMap<EnumMapTest.L, String> otherMap = new EnumMap<>(map);
        map.clear();
        Assert.assertEquals(1, otherMap.size());
        Assert.assertEquals("A", otherMap.get(EnumMapTest.L.A));
        otherMap = new EnumMap<>(((Map<EnumMapTest.L, String>) (map)));
        Assert.assertEquals(0, otherMap.size());
        Assert.assertEquals(null, otherMap.get(EnumMapTest.L.A));
    }

    @Test
    public void createdFromOtherMap() {
        Map<EnumMapTest.L, String> map = new HashMap<>();
        Assert.assertNull(map.put(EnumMapTest.L.A, "A"));
        EnumMap<EnumMapTest.L, String> otherMap = new EnumMap<>(map);
        map.clear();
        Assert.assertEquals(1, otherMap.size());
        Assert.assertEquals("A", otherMap.get(EnumMapTest.L.A));
        try {
            new EnumMap<>(map);
            Assert.fail("Should throw exception when creating from empty map");
        } catch (IllegalArgumentException e) {
            // expected
        }
    }

    @Test
    public void entriesAdded() {
        EnumMap<EnumMapTest.L, String> map = new EnumMap<>(EnumMapTest.L.class);
        Assert.assertNull(map.put(EnumMapTest.L.A, "A"));
        Assert.assertEquals(1, map.size());
        Assert.assertEquals("A", map.get(EnumMapTest.L.A));
        Assert.assertTrue(map.containsKey(EnumMapTest.L.A));
        Assert.assertEquals("A", map.put(EnumMapTest.L.A, "A0"));
        Assert.assertEquals(1, map.size());
        Assert.assertEquals("A0", map.get(EnumMapTest.L.A));
        Assert.assertTrue(map.containsKey(EnumMapTest.L.A));
        Assert.assertNull(map.put(EnumMapTest.L.B, "B"));
        Assert.assertEquals(2, map.size());
        Assert.assertEquals("B", map.get(EnumMapTest.L.B));
        Assert.assertTrue(map.containsKey(EnumMapTest.L.B));
        List<String> values = new ArrayList<>();
        List<EnumMapTest.L> keys = new ArrayList<>();
        for (Map.Entry<EnumMapTest.L, String> entry : map.entrySet()) {
            values.add(entry.getValue());
            keys.add(entry.getKey());
        }
        Assert.assertEquals(Arrays.asList("A0", "B"), values);
        Assert.assertEquals(Arrays.asList(EnumMapTest.L.A, EnumMapTest.L.B), keys);
    }

    @Test
    public void multipleEntriesAdded() {
        EnumMap<EnumMapTest.L, String> map = new EnumMap<>(EnumMapTest.L.class);
        map.put(EnumMapTest.L.A, "A");
        map.put(EnumMapTest.L.B, "B");
        Map<EnumMapTest.L, String> otherMap = new HashMap<>();
        otherMap.put(EnumMapTest.L.B, "B0");
        otherMap.put(EnumMapTest.L.C, "C0");
        map.putAll(otherMap);
        Assert.assertEquals(3, map.size());
        Assert.assertEquals("A", map.get(EnumMapTest.L.A));
        Assert.assertEquals("B0", map.get(EnumMapTest.L.B));
        Assert.assertEquals("C0", map.get(EnumMapTest.L.C));
    }

    @Test
    public void entriesRemoved() {
        EnumMap<EnumMapTest.L, String> map = new EnumMap<>(EnumMapTest.L.class);
        map.put(EnumMapTest.L.A, "A");
        map.put(EnumMapTest.L.B, "B");
        Assert.assertEquals(2, map.size());
        Assert.assertEquals("A", map.remove(EnumMapTest.L.A));
        Assert.assertEquals(1, map.size());
        Assert.assertNull(map.get(EnumMapTest.L.A));
        Assert.assertNull(map.remove(EnumMapTest.L.A));
        Assert.assertEquals(1, map.size());
        Assert.assertNull(map.remove("Dummy"));
        List<String> values = new ArrayList<>();
        List<EnumMapTest.L> keys = new ArrayList<>();
        for (Map.Entry<EnumMapTest.L, String> entry : map.entrySet()) {
            values.add(entry.getValue());
            keys.add(entry.getKey());
        }
        Assert.assertEquals(Arrays.asList("B"), values);
        Assert.assertEquals(Arrays.asList(EnumMapTest.L.B), keys);
    }

    @Test
    public void containsNullValue() {
        EnumMap<EnumMapTest.L, String> map = new EnumMap<>(EnumMapTest.L.class);
        map.put(EnumMapTest.L.A, null);
        Assert.assertEquals(1, map.size());
        Assert.assertNull(map.get(EnumMapTest.L.A));
        Assert.assertTrue(map.containsKey(EnumMapTest.L.A));
        Assert.assertNull(map.values().iterator().next());
        Assert.assertEquals(EnumMapTest.L.A, map.keySet().iterator().next());
    }

    @Test
    public void clearWorks() {
        EnumMap<EnumMapTest.L, String> map = new EnumMap<>(EnumMapTest.L.class);
        map.put(EnumMapTest.L.A, "A");
        map.put(EnumMapTest.L.B, "B");
        Assert.assertEquals(2, map.size());
        map.clear();
        Assert.assertEquals(0, map.size());
        Assert.assertFalse(map.entrySet().iterator().hasNext());
        Assert.assertFalse(map.containsKey(EnumMapTest.L.A));
        Assert.assertNull(map.get(EnumMapTest.L.A));
    }

    @Test
    public void iteratorReplacesValue() {
        EnumMap<EnumMapTest.L, String> map = new EnumMap<>(EnumMapTest.L.class);
        map.put(EnumMapTest.L.A, "A");
        map.put(EnumMapTest.L.B, "B");
        Iterator<Map.Entry<EnumMapTest.L, String>> iter = map.entrySet().iterator();
        Assert.assertTrue(iter.hasNext());
        Map.Entry<EnumMapTest.L, String> entry = iter.next();
        Assert.assertEquals(EnumMapTest.L.A, entry.getKey());
        Assert.assertEquals("A", entry.setValue("A0"));
        Assert.assertTrue(iter.hasNext());
        entry = iter.next();
        Assert.assertEquals(EnumMapTest.L.B, entry.getKey());
        Assert.assertEquals("B", entry.setValue("B0"));
        Assert.assertFalse(iter.hasNext());
        Assert.assertEquals("A0", map.get(EnumMapTest.L.A));
        Assert.assertEquals("B0", map.get(EnumMapTest.L.B));
    }

    @Test
    public void iteratorRemovesValue() {
        EnumMap<EnumMapTest.L, String> map = new EnumMap<>(EnumMapTest.L.class);
        map.put(EnumMapTest.L.A, "A");
        map.put(EnumMapTest.L.B, "B");
        Iterator<Map.Entry<EnumMapTest.L, String>> iter = map.entrySet().iterator();
        try {
            iter.remove();
            Assert.fail("Remove without calling next should throw exception");
        } catch (IllegalStateException e) {
            // It's expected
        }
        Assert.assertTrue(iter.hasNext());
        Map.Entry<EnumMapTest.L, String> entry = iter.next();
        Assert.assertEquals(EnumMapTest.L.A, entry.getKey());
        iter.remove();
        try {
            iter.remove();
            Assert.fail("Repeated remove should throw exception");
        } catch (IllegalStateException e) {
            // It's expected
        }
        Assert.assertTrue(iter.hasNext());
        iter.next();
        Assert.assertFalse(iter.hasNext());
        Assert.assertEquals(null, map.get(EnumMapTest.L.A));
        Assert.assertEquals("B", map.get(EnumMapTest.L.B));
        Assert.assertEquals(1, map.size());
    }

    enum L {

        A,
        B,
        C;}
}

