/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.apache.harmony.tests.java.util;


import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import junit.framework.TestCase;


public abstract class SortedMapTestBase extends TestCase {
    final int N = 1000;

    final int TRIES = 100;

    SortedMap<Integer, Integer> map;

    SortedMap<Integer, Integer> ref;

    Random rnd;

    public final void testClear() {
        map.clear();
        TestCase.assertTrue(map.isEmpty());
    }

    public final void testContainsKey() {
        for (int i = 0; i < (TRIES); i++) {
            int key = rnd.nextInt(N);
            TestCase.assertEquals(ref.containsKey(key), map.containsKey(key));
        }
    }

    public final void testContainsValue() {
        for (int i = 0; i < (TRIES); i++) {
            int value = rnd.nextInt(N);
            TestCase.assertEquals(ref.containsValue(value), map.containsValue(value));
        }
    }

    public final void testEntrySet() {
        Set<Map.Entry<Integer, Integer>> refSet = ref.entrySet();
        Set<Map.Entry<Integer, Integer>> mapSet = map.entrySet();
        for (Map.Entry<Integer, Integer> e : refSet) {
            TestCase.assertTrue(mapSet.contains(e));
        }
        for (Map.Entry<Integer, Integer> e : mapSet) {
            TestCase.assertTrue(refSet.contains(e));
        }
        TestCase.assertEquals(ref.entrySet(), map.entrySet());
    }

    public final void testGet() {
        for (int i = 0; i < (TRIES); i++) {
            int key = rnd.nextInt(N);
            TestCase.assertEquals(ref.get(key), map.get(key));
        }
    }

    public final void testKeySet() {
        TestCase.assertEquals(ref.keySet(), map.keySet());
        Iterator<Integer> i = ref.keySet().iterator();
        Iterator<Integer> j = map.keySet().iterator();
        while (i.hasNext()) {
            TestCase.assertEquals(i.next(), j.next());
            if (rnd.nextBoolean()) {
                j.remove();
                i.remove();
            }
        } 
    }

    public final void testPut() {
        for (int i = 0; i < (TRIES); i++) {
            int key = rnd.nextInt(N);
            int value = rnd.nextInt(N);
            TestCase.assertEquals(ref.put(key, value), map.put(key, value));
            TestCase.assertEquals(ref.get(key), map.get(key));
            TestCase.assertEquals(ref, map);
        }
    }

    public final void testPut0() {
        ref.clear();
        map.clear();
        for (int i = 0; i < (N); i++) {
            int key = rnd.nextInt(N);
            int value = rnd.nextInt(N);
            TestCase.assertEquals(ref.put(key, value), map.put(key, value));
            TestCase.assertEquals(ref.get(key), map.get(key));
        }
    }

    public final void testPutAll() {
        Map<Integer, Integer> mixin = new HashMap<Integer, Integer>(TRIES);
        for (int i = 0; i < (TRIES); i++) {
            mixin.put(rnd.nextInt(N), rnd.nextInt(N));
        }
        ref.putAll(mixin);
        map.putAll(mixin);
        TestCase.assertEquals(ref, map);
    }

    public final void testRemove() {
        for (int i = 0; i < (N); i++) {
            int key = rnd.nextInt(N);
            TestCase.assertEquals(ref.remove(key), map.remove(key));
            if ((i % ((N) / (TRIES))) == 0) {
                TestCase.assertEquals(ref, map);
            }
        }
    }

    public final void testRemove0() {
        while (!(ref.isEmpty())) {
            int key = ref.tailMap((((ref.firstKey()) + (ref.lastKey())) / 2)).firstKey();
            TestCase.assertEquals(ref.remove(key), map.remove(key));
        } 
    }

    public final void testSize() {
        TestCase.assertEquals(ref.size(), map.size());
    }

    public final void testValues() {
        TestCase.assertEquals(ref.values().size(), map.values().size());
        TestCase.assertTrue(ref.values().containsAll(map.values()));
        TestCase.assertTrue(map.values().containsAll(ref.values()));
        Iterator<Integer> i = ref.values().iterator();
        Iterator<Integer> j = map.values().iterator();
        while (i.hasNext()) {
            TestCase.assertEquals(i.next(), j.next());
            if (rnd.nextBoolean()) {
                j.remove();
                i.remove();
            }
        } 
    }

    public final void testComparator() {
        TestCase.assertEquals(ref.comparator(), map.comparator());
    }

    public final void testFirstKey() {
        TestCase.assertEquals(ref.firstKey(), map.firstKey());
    }

    public final void testHeadMap() {
        for (int i = 0; i < (TRIES); i++) {
            int key = rnd.nextInt(N);
            checkSubMap(ref.headMap(key), map.headMap(key));
        }
        checkSubMap(ref.headMap((-1)), map.headMap((-1)));
    }

    public final void testLastKey() {
        TestCase.assertEquals(ref.lastKey(), map.lastKey());
    }

    public final void testSubMap() {
        for (int i = 0; i < (TRIES); i++) {
            int key0 = rnd.nextInt(((N) / 2));
            int key1 = (rnd.nextInt(((N) / 2))) + ((N) / 2);
            if (((ref.comparator()) != null) && ((ref.comparator().compare(key0, key1)) > 0)) {
                int tmp = key0;
                key0 = key1;
                key1 = tmp;
            }
            checkSubMap(ref.subMap(key0, key1), map.subMap(key0, key1));
        }
        boolean caught = false;
        try {
            if (((ref.comparator()) != null) && ((ref.comparator().compare(100, 0)) < 0)) {
                map.subMap(0, 100);
            } else {
                map.subMap(100, 0);
            }
        } catch (IllegalArgumentException e) {
            caught = true;
        }
        TestCase.assertTrue(caught);
        int firstKey = ref.firstKey();
        Map.Entry<Integer, Integer> refE = ref.entrySet().iterator().next();
        Map.Entry<Integer, Integer> mapE = map.entrySet().iterator().next();
        mapE.setValue((-1));
        refE.setValue((-1));
        TestCase.assertEquals(ref.get(firstKey), map.get(firstKey));
    }

    public final void testTailMap() {
        for (int i = 0; i < (TRIES); i++) {
            int key = rnd.nextInt((2 * (N)));
            checkSubMap(ref.tailMap(key), map.tailMap(key));
        }
        checkSubMap(ref.tailMap(((2 * (N)) + 1)), map.tailMap(((2 * (N)) + 1)));
    }

    public final void testHashCode() {
        TestCase.assertEquals(ref.hashCode(), map.hashCode());
    }

    public final void testEqualsObject() {
        TestCase.assertTrue(map.equals(ref));
        map.put(((N) + 1), ((N) + 1));
        TestCase.assertFalse(map.equals(ref));
    }

    public final void testIsEmpty() {
        TestCase.assertEquals(ref.isEmpty(), map.isEmpty());
    }

    public final void testIsEmpty2() {
        TreeMap<String, String> map = new TreeMap<String, String>();
        map.put("one", "1");
        TestCase.assertEquals("size should be one", 1, map.size());
        map.clear();
        TestCase.assertEquals("size should be zero", 0, map.size());
        TestCase.assertTrue("Should not have entries", (!(map.entrySet().iterator().hasNext())));
        map.put("one", "1");
        TestCase.assertEquals("size should be one", 1, map.size());
        map.remove("one");
        TestCase.assertEquals("size should be zero", 0, map.size());
        TestCase.assertTrue("Should not have entries", (!(map.entrySet().iterator().hasNext())));
        map.clear();
        map.put("0", "1");
        map.clear();
        TestCase.assertTrue(map.isEmpty());
        TestCase.assertFalse(map.entrySet().iterator().hasNext());
        TestCase.assertFalse(map.keySet().iterator().hasNext());
        TestCase.assertFalse(map.values().iterator().hasNext());
    }

    public final void testToString() {
        TestCase.assertEquals(ref.toString(), map.toString());
    }

    public final void testViews() {
        testViews(ref, map);
    }

    public final void testClone() throws Exception {
        Method refClone = ref.getClass().getMethod("clone", new Class[0]);
        Method mapClone = map.getClass().getMethod("clone", new Class[0]);
        SortedMap<Integer, Integer> map2 = ((SortedMap<Integer, Integer>) (mapClone.invoke(map, new Object[0])));
        TestCase.assertEquals(refClone.invoke(ref, new Object[0]), map2);
        map2.remove(map2.lastKey());
        TestCase.assertFalse(ref.equals(map2));
    }
}

