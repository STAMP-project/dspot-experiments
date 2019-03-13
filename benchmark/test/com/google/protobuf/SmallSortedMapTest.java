/**
 * Protocol Buffers - Google's data interchange format
 */
/**
 * Copyright 2008 Google Inc.  All rights reserved.
 */
/**
 * https://developers.google.com/protocol-buffers/
 */
/**
 *
 */
/**
 * Redistribution and use in source and binary forms, with or without
 */
/**
 * modification, are permitted provided that the following conditions are
 */
/**
 * met:
 */
/**
 *
 */
/**
 * * Redistributions of source code must retain the above copyright
 */
/**
 * notice, this list of conditions and the following disclaimer.
 */
/**
 * * Redistributions in binary form must reproduce the above
 */
/**
 * copyright notice, this list of conditions and the following disclaimer
 */
/**
 * in the documentation and/or other materials provided with the
 */
/**
 * distribution.
 */
/**
 * * Neither the name of Google Inc. nor the names of its
 */
/**
 * contributors may be used to endorse or promote products derived from
 */
/**
 * this software without specific prior written permission.
 */
/**
 *
 */
/**
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 */
/**
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 */
/**
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 */
/**
 * A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
 */
/**
 * OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 */
/**
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 */
/**
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 */
/**
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 */
/**
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 */
/**
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 */
/**
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.google.protobuf;


import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import junit.framework.TestCase;


/**
 *
 *
 * @author darick@google.com Darick Tong
 */
public class SmallSortedMapTest extends TestCase {
    // java.util.AbstractMap.SimpleEntry is private in JDK 1.5. We re-implement it
    // here for JDK 1.5 users.
    private static class SimpleEntry<K, V> implements Map.Entry<K, V> {
        private final K key;

        private V value;

        SimpleEntry(K key, V value) {
            this.key = key;
            this.value = value;
        }

        @Override
        public K getKey() {
            return key;
        }

        @Override
        public V getValue() {
            return value;
        }

        @Override
        public V setValue(V value) {
            V oldValue = this.value;
            this.value = value;
            return oldValue;
        }

        private static boolean eq(Object o1, Object o2) {
            return o1 == null ? o2 == null : o1.equals(o2);
        }

        @Override
        public boolean equals(Object o) {
            if (!(o instanceof Map.Entry))
                return false;

            Map.Entry e = ((Map.Entry) (o));
            return (SmallSortedMapTest.SimpleEntry.eq(key, e.getKey())) && (SmallSortedMapTest.SimpleEntry.eq(value, e.getValue()));
        }

        @Override
        public int hashCode() {
            return ((key) == null ? 0 : key.hashCode()) ^ ((value) == null ? 0 : value.hashCode());
        }
    }

    public void testPutAndGetArrayEntriesOnly() {
        runPutAndGetTest(3);
    }

    public void testPutAndGetOverflowEntries() {
        runPutAndGetTest(6);
    }

    public void testReplacingPut() {
        SmallSortedMap<Integer, Integer> map = SmallSortedMap.newInstanceForTest(3);
        for (int i = 0; i < 6; i++) {
            TestCase.assertNull(map.put(i, (i + 1)));
            TestCase.assertNull(map.remove((i + 1)));
        }
        for (int i = 0; i < 6; i++) {
            TestCase.assertEquals(new Integer((i + 1)), map.put(i, (i + 2)));
        }
    }

    public void testRemove() {
        SmallSortedMap<Integer, Integer> map = SmallSortedMap.newInstanceForTest(3);
        for (int i = 0; i < 6; i++) {
            TestCase.assertNull(map.put(i, (i + 1)));
            TestCase.assertNull(map.remove((i + 1)));
        }
        TestCase.assertEquals(3, map.getNumArrayEntries());
        TestCase.assertEquals(3, map.getNumOverflowEntries());
        TestCase.assertEquals(6, map.size());
        TestCase.assertEquals(makeSortedKeySet(0, 1, 2, 3, 4, 5), map.keySet());
        TestCase.assertEquals(new Integer(2), map.remove(1));
        TestCase.assertEquals(3, map.getNumArrayEntries());
        TestCase.assertEquals(2, map.getNumOverflowEntries());
        TestCase.assertEquals(5, map.size());
        TestCase.assertEquals(makeSortedKeySet(0, 2, 3, 4, 5), map.keySet());
        TestCase.assertEquals(new Integer(5), map.remove(4));
        TestCase.assertEquals(3, map.getNumArrayEntries());
        TestCase.assertEquals(1, map.getNumOverflowEntries());
        TestCase.assertEquals(4, map.size());
        TestCase.assertEquals(makeSortedKeySet(0, 2, 3, 5), map.keySet());
        TestCase.assertEquals(new Integer(4), map.remove(3));
        TestCase.assertEquals(3, map.getNumArrayEntries());
        TestCase.assertEquals(0, map.getNumOverflowEntries());
        TestCase.assertEquals(3, map.size());
        TestCase.assertEquals(makeSortedKeySet(0, 2, 5), map.keySet());
        TestCase.assertNull(map.remove(3));
        TestCase.assertEquals(3, map.getNumArrayEntries());
        TestCase.assertEquals(0, map.getNumOverflowEntries());
        TestCase.assertEquals(3, map.size());
        TestCase.assertEquals(new Integer(1), map.remove(0));
        TestCase.assertEquals(2, map.getNumArrayEntries());
        TestCase.assertEquals(0, map.getNumOverflowEntries());
        TestCase.assertEquals(2, map.size());
    }

    public void testClear() {
        SmallSortedMap<Integer, Integer> map = SmallSortedMap.newInstanceForTest(3);
        for (int i = 0; i < 6; i++) {
            TestCase.assertNull(map.put(i, (i + 1)));
        }
        map.clear();
        TestCase.assertEquals(0, map.getNumArrayEntries());
        TestCase.assertEquals(0, map.getNumOverflowEntries());
        TestCase.assertEquals(0, map.size());
    }

    public void testGetArrayEntryAndOverflowEntries() {
        SmallSortedMap<Integer, Integer> map = SmallSortedMap.newInstanceForTest(3);
        for (int i = 0; i < 6; i++) {
            TestCase.assertNull(map.put(i, (i + 1)));
        }
        TestCase.assertEquals(3, map.getNumArrayEntries());
        for (int i = 0; i < 3; i++) {
            Map.Entry<Integer, Integer> entry = map.getArrayEntryAt(i);
            TestCase.assertEquals(new Integer(i), entry.getKey());
            TestCase.assertEquals(new Integer((i + 1)), entry.getValue());
        }
        Iterator<Map.Entry<Integer, Integer>> it = map.getOverflowEntries().iterator();
        for (int i = 3; i < 6; i++) {
            TestCase.assertTrue(it.hasNext());
            Map.Entry<Integer, Integer> entry = it.next();
            TestCase.assertEquals(new Integer(i), entry.getKey());
            TestCase.assertEquals(new Integer((i + 1)), entry.getValue());
        }
        TestCase.assertFalse(it.hasNext());
    }

    public void testEntrySetContains() {
        SmallSortedMap<Integer, Integer> map = SmallSortedMap.newInstanceForTest(3);
        for (int i = 0; i < 6; i++) {
            TestCase.assertNull(map.put(i, (i + 1)));
        }
        Set<Map.Entry<Integer, Integer>> entrySet = map.entrySet();
        for (int i = 0; i < 6; i++) {
            TestCase.assertTrue(entrySet.contains(new SmallSortedMapTest.SimpleEntry<Integer, Integer>(i, (i + 1))));
            TestCase.assertFalse(entrySet.contains(new SmallSortedMapTest.SimpleEntry<Integer, Integer>(i, i)));
        }
    }

    public void testEntrySetAdd() {
        SmallSortedMap<Integer, Integer> map = SmallSortedMap.newInstanceForTest(3);
        Set<Map.Entry<Integer, Integer>> entrySet = map.entrySet();
        for (int i = 0; i < 6; i++) {
            Map.Entry<Integer, Integer> entry = new SmallSortedMapTest.SimpleEntry<Integer, Integer>(i, (i + 1));
            TestCase.assertTrue(entrySet.add(entry));
            TestCase.assertFalse(entrySet.add(entry));
        }
        for (int i = 0; i < 6; i++) {
            TestCase.assertEquals(new Integer((i + 1)), map.get(i));
        }
        TestCase.assertEquals(3, map.getNumArrayEntries());
        TestCase.assertEquals(3, map.getNumOverflowEntries());
        TestCase.assertEquals(6, map.size());
    }

    public void testEntrySetRemove() {
        SmallSortedMap<Integer, Integer> map = SmallSortedMap.newInstanceForTest(3);
        Set<Map.Entry<Integer, Integer>> entrySet = map.entrySet();
        for (int i = 0; i < 6; i++) {
            TestCase.assertNull(map.put(i, (i + 1)));
        }
        for (int i = 0; i < 6; i++) {
            Map.Entry<Integer, Integer> entry = new SmallSortedMapTest.SimpleEntry<Integer, Integer>(i, (i + 1));
            TestCase.assertTrue(entrySet.remove(entry));
            TestCase.assertFalse(entrySet.remove(entry));
        }
        TestCase.assertTrue(map.isEmpty());
        TestCase.assertEquals(0, map.getNumArrayEntries());
        TestCase.assertEquals(0, map.getNumOverflowEntries());
        TestCase.assertEquals(0, map.size());
    }

    public void testEntrySetClear() {
        SmallSortedMap<Integer, Integer> map = SmallSortedMap.newInstanceForTest(3);
        for (int i = 0; i < 6; i++) {
            TestCase.assertNull(map.put(i, (i + 1)));
        }
        map.entrySet().clear();
        TestCase.assertTrue(map.isEmpty());
        TestCase.assertEquals(0, map.getNumArrayEntries());
        TestCase.assertEquals(0, map.getNumOverflowEntries());
        TestCase.assertEquals(0, map.size());
    }

    public void testEntrySetIteratorNext() {
        SmallSortedMap<Integer, Integer> map = SmallSortedMap.newInstanceForTest(3);
        for (int i = 0; i < 6; i++) {
            TestCase.assertNull(map.put(i, (i + 1)));
        }
        Iterator<Map.Entry<Integer, Integer>> it = map.entrySet().iterator();
        for (int i = 0; i < 6; i++) {
            TestCase.assertTrue(it.hasNext());
            Map.Entry<Integer, Integer> entry = it.next();
            TestCase.assertEquals(new Integer(i), entry.getKey());
            TestCase.assertEquals(new Integer((i + 1)), entry.getValue());
        }
        TestCase.assertFalse(it.hasNext());
    }

    public void testEntrySetIteratorRemove() {
        SmallSortedMap<Integer, Integer> map = SmallSortedMap.newInstanceForTest(3);
        for (int i = 0; i < 6; i++) {
            TestCase.assertNull(map.put(i, (i + 1)));
        }
        Iterator<Map.Entry<Integer, Integer>> it = map.entrySet().iterator();
        for (int i = 0; i < 6; i++) {
            TestCase.assertTrue(map.containsKey(i));
            it.next();
            it.remove();
            TestCase.assertFalse(map.containsKey(i));
            TestCase.assertEquals(((6 - i) - 1), map.size());
        }
    }

    public void testMapEntryModification() {
        SmallSortedMap<Integer, Integer> map = SmallSortedMap.newInstanceForTest(3);
        for (int i = 0; i < 6; i++) {
            TestCase.assertNull(map.put(i, (i + 1)));
        }
        Iterator<Map.Entry<Integer, Integer>> it = map.entrySet().iterator();
        for (int i = 0; i < 6; i++) {
            Map.Entry<Integer, Integer> entry = it.next();
            entry.setValue((i + 23));
        }
        for (int i = 0; i < 6; i++) {
            TestCase.assertEquals(new Integer((i + 23)), map.get(i));
        }
    }

    public void testMakeImmutable() {
        SmallSortedMap<Integer, Integer> map = SmallSortedMap.newInstanceForTest(3);
        for (int i = 0; i < 6; i++) {
            TestCase.assertNull(map.put(i, (i + 1)));
        }
        map.makeImmutable();
        TestCase.assertEquals(new Integer(1), map.get(0));
        TestCase.assertEquals(6, map.size());
        try {
            map.put(23, 23);
            TestCase.fail("Expected UnsupportedOperationException");
        } catch (UnsupportedOperationException expected) {
        }
        Map<Integer, Integer> other = new HashMap<Integer, Integer>();
        other.put(23, 23);
        try {
            map.putAll(other);
            TestCase.fail("Expected UnsupportedOperationException");
        } catch (UnsupportedOperationException expected) {
        }
        try {
            map.remove(0);
            TestCase.fail("Expected UnsupportedOperationException");
        } catch (UnsupportedOperationException expected) {
        }
        try {
            map.clear();
            TestCase.fail("Expected UnsupportedOperationException");
        } catch (UnsupportedOperationException expected) {
        }
        Set<Map.Entry<Integer, Integer>> entrySet = map.entrySet();
        try {
            entrySet.clear();
            TestCase.fail("Expected UnsupportedOperationException");
        } catch (UnsupportedOperationException expected) {
        }
        Iterator<Map.Entry<Integer, Integer>> it = entrySet.iterator();
        while (it.hasNext()) {
            Map.Entry<Integer, Integer> entry = it.next();
            try {
                entry.setValue(0);
                TestCase.fail("Expected UnsupportedOperationException");
            } catch (UnsupportedOperationException expected) {
            }
            try {
                it.remove();
                TestCase.fail("Expected UnsupportedOperationException");
            } catch (UnsupportedOperationException expected) {
            }
        } 
        Set<Integer> keySet = map.keySet();
        try {
            keySet.clear();
            TestCase.fail("Expected UnsupportedOperationException");
        } catch (UnsupportedOperationException expected) {
        }
        Iterator<Integer> keys = keySet.iterator();
        while (keys.hasNext()) {
            Integer key = keys.next();
            try {
                keySet.remove(key);
                TestCase.fail("Expected UnsupportedOperationException");
            } catch (UnsupportedOperationException expected) {
            }
            try {
                keys.remove();
                TestCase.fail("Expected UnsupportedOperationException");
            } catch (UnsupportedOperationException expected) {
            }
        } 
    }
}

