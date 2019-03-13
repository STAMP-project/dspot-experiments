/**
 * Copyright 2012-2018 Chronicle Map Contributors
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
package net.openhft.chronicle.map.jsr166.map;


import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import net.openhft.chronicle.map.ChronicleMap;
import net.openhft.chronicle.map.jsr166.JSR166TestCase;
import org.junit.Assert;
import org.junit.Test;


/* Written by Doug Lea with assistance from members of JCP JSR-166
Expert Group and released to the public domain, as explained at
http://creativecommons.org/publicdomain/zero/1.0/
Other contributors include Andrew Wright, Jeffrey Hayes,
Pat Fisher, Mike Judd.
 */
/**
 * A deserialized map equals original
 */
/* @Test public void testSerialization()   {
Map x = map5();
Map y = serialClone(x);

assertNotSame(x, y);
assertEquals(x.size(), y.size());
assertEquals(x, y);
assertEquals(y, x);
}
 */
/**
 * TODO : SetValue of an EntrySet entry sets value in the map.
 */
/* @Test public void testSetValueWriteThrough() {
// Adapted from a bug report by Eric Zoerner
ChronicleMap map = newShmIntString(2, 5.0f, 1);
assertTrue(map.isEmpty());
for (int i = 0; i < 20; i++)
map.put(new Integer(i), new Integer(i));
assertFalse(map.isEmpty());
Map.Entry entry1 = (Map.Entry) map.entrySet().iterator().next();
// Unless it happens to be first (in which case remainder of
// test is skipped), remove a possibly-colliding key from map
// which, under some implementations, may cause entry1 to be
// cloned in map
if (!entry1.getKey().equals(new Integer(16))) {
map.remove(new Integer(16));
entry1.setValue("XYZ");
assertTrue(map.containsValue("XYZ")); // fails if write-through broken
}
}
 */
public class ChronicleMapTest extends JSR166TestCase {
    /**
     * clear removes all pairs
     */
    @Test(timeout = 5000)
    public void testClear() throws IOException {
        try (ChronicleMap<Integer, CharSequence> map = ChronicleMapTest.map5()) {
            map.clear();
            Assert.assertEquals(0, map.size());
        }
    }

    /**
     * contains returns true for contained value
     */
    @Test(timeout = 5000)
    public void testContains() throws IOException {
        try (ChronicleMap map = ChronicleMapTest.map5()) {
            Assert.assertTrue(map.containsValue("A"));
            Assert.assertFalse(map.containsValue("Z"));
        }
    }

    /**
     * containsKey returns true for contained key
     */
    @Test(timeout = 5000)
    public void testContainsKey() throws IOException {
        try (ChronicleMap map = ChronicleMapTest.map5()) {
            Assert.assertTrue(map.containsKey(JSR166TestCase.one));
            Assert.assertFalse(map.containsKey(JSR166TestCase.zero));
        }
    }

    /**
     * containsValue returns true for held values
     */
    @Test(timeout = 5000)
    public void testContainsValue() throws IOException {
        try (ChronicleMap map = ChronicleMapTest.map5()) {
            Assert.assertTrue(map.containsValue("A"));
            Assert.assertFalse(map.containsValue("Z"));
        }
    }

    /**
     * get returns the correct element at the given key, or null if not present
     */
    @Test(timeout = 5000)
    public void testGet() throws IOException {
        try (ChronicleMap map = ChronicleMapTest.map5()) {
            Assert.assertEquals("A", map.get(JSR166TestCase.one).toString());
            try (ChronicleMap empty = ChronicleMapTest.newStrStrMap(8078)) {
                Assert.assertNull(map.get(JSR166TestCase.notPresent));
            }
        }
    }

    /**
     * isEmpty is true of empty map and false for non-empty
     */
    @Test(timeout = 5000)
    public void testIsEmpty() throws IOException {
        try (ChronicleMap empty = ChronicleMapTest.newShmIntString(8078)) {
            try (ChronicleMap map = ChronicleMapTest.map5()) {
                if (!(empty.isEmpty())) {
                    System.out.print(("not empty " + empty));
                }
                Assert.assertTrue(empty.isEmpty());
                Assert.assertFalse(map.isEmpty());
            }
        }
    }

    /**
     * keySet returns a Set containing all the keys
     */
    @Test(timeout = 5000)
    public void testKeySet() throws IOException {
        try (ChronicleMap map = ChronicleMapTest.map5()) {
            Set s = map.keySet();
            Assert.assertEquals(5, s.size());
            Assert.assertTrue(s.contains(JSR166TestCase.one));
            Assert.assertTrue(s.contains(JSR166TestCase.two));
            Assert.assertTrue(s.contains(JSR166TestCase.three));
            Assert.assertTrue(s.contains(JSR166TestCase.four));
            Assert.assertTrue(s.contains(JSR166TestCase.five));
        }
    }

    /**
     * keySet.toArray returns contains all keys
     */
    @Test(timeout = 5000)
    public void testKeySetToArray() throws IOException {
        try (ChronicleMap map = ChronicleMapTest.map5()) {
            Set s = map.keySet();
            Object[] ar = s.toArray();
            Assert.assertTrue(s.containsAll(Arrays.asList(ar)));
            Assert.assertEquals(5, ar.length);
            ar[0] = JSR166TestCase.m10;
            Assert.assertFalse(s.containsAll(Arrays.asList(ar)));
        }
    }

    /**
     * Values.toArray contains all values
     */
    @Test(timeout = 5000)
    public void testValuesToArray() throws IOException {
        try (ChronicleMap<Integer, CharSequence> map = ChronicleMapTest.map5()) {
            Collection<CharSequence> vs = map.values();
            ArrayList<CharSequence> s = new ArrayList<>(vs);
            Assert.assertEquals(5, s.size());
            Assert.assertTrue(s.stream().anyMatch("A"::contentEquals));
            Assert.assertTrue(s.stream().anyMatch("B"::contentEquals));
            Assert.assertTrue(s.stream().anyMatch("C"::contentEquals));
            Assert.assertTrue(s.stream().anyMatch("D"::contentEquals));
            Assert.assertTrue(s.stream().anyMatch("E"::contentEquals));
        }
    }

    /**
     * entrySet.toArray contains all entries
     */
    @Test(timeout = 5000)
    public void testEntrySetToArray() throws IOException {
        try (ChronicleMap map = ChronicleMapTest.map5()) {
            Set s = map.entrySet();
            Object[] ar = s.toArray();
            Assert.assertEquals(5, ar.length);
            for (int i = 0; i < 5; ++i) {
                Assert.assertTrue(map.containsKey(((Map.Entry) (ar[i])).getKey()));
                Assert.assertTrue(map.containsValue(((Map.Entry) (ar[i])).getValue()));
            }
        }
    }

    /**
     * values collection contains all values
     */
    @Test(timeout = 5000)
    public void testValues() throws IOException {
        try (ChronicleMap map = ChronicleMapTest.map5()) {
            Collection s = map.values();
            Assert.assertEquals(5, s.size());
            Assert.assertTrue(s.contains("A"));
            Assert.assertTrue(s.contains("B"));
            Assert.assertTrue(s.contains("C"));
            Assert.assertTrue(s.contains("D"));
            Assert.assertTrue(s.contains("E"));
        }
    }

    /**
     * entrySet contains all pairs
     */
    @Test(timeout = 5000)
    public void testEntrySet() throws IOException {
        try (ChronicleMap<Integer, CharSequence> map = ChronicleMapTest.map5()) {
            Set<Map.Entry<Integer, CharSequence>> s = map.entrySet();
            Assert.assertEquals(5, s.size());
            Iterator<Map.Entry<Integer, CharSequence>> it = s.iterator();
            while (it.hasNext()) {
                Map.Entry<Integer, CharSequence> e = it.next();
                Assert.assertTrue(((((((e.getKey().equals(JSR166TestCase.one)) && ("A".contentEquals(e.getValue()))) || ((e.getKey().equals(JSR166TestCase.two)) && ("B".contentEquals(e.getValue())))) || ((e.getKey().equals(JSR166TestCase.three)) && ("C".contentEquals(e.getValue())))) || ((e.getKey().equals(JSR166TestCase.four)) && ("D".contentEquals(e.getValue())))) || ((e.getKey().equals(JSR166TestCase.five)) && ("E".contentEquals(e.getValue())))));
            } 
        }
    }

    /**
     * putAll adds all key-value pairs from the given map
     */
    @Test(timeout = 5000)
    public void testPutAll() throws IOException {
        try (ChronicleMap empty = ChronicleMapTest.newShmIntString(8076)) {
            try (ChronicleMap map = ChronicleMapTest.map5()) {
                empty.putAll(map);
                Assert.assertEquals(5, empty.size());
                Assert.assertTrue(empty.containsKey(JSR166TestCase.one));
                Assert.assertTrue(empty.containsKey(JSR166TestCase.two));
                Assert.assertTrue(empty.containsKey(JSR166TestCase.three));
                Assert.assertTrue(empty.containsKey(JSR166TestCase.four));
                Assert.assertTrue(empty.containsKey(JSR166TestCase.five));
            }
        }
    }

    /**
     * putIfAbsent works when the given key is not present
     */
    @Test(timeout = 5000)
    public void testPutIfAbsent() throws IOException {
        try (ChronicleMap map = ChronicleMapTest.map5()) {
            map.putIfAbsent(JSR166TestCase.six, "Z");
            Assert.assertTrue(map.containsKey(JSR166TestCase.six));
        }
    }

    /**
     * putIfAbsent does not add the pair if the key is already present
     */
    @Test(timeout = 5000)
    public void testPutIfAbsent2() throws IOException {
        try (ChronicleMap map = ChronicleMapTest.map5()) {
            Assert.assertEquals("A", map.putIfAbsent(JSR166TestCase.one, "Z").toString());
        }
    }

    /**
     * replace fails when the given key is not present
     */
    @Test(timeout = 5000)
    public void testReplace() throws IOException {
        try (ChronicleMap map = ChronicleMapTest.map5()) {
            Assert.assertNull(map.replace(JSR166TestCase.six, "Z"));
            Assert.assertFalse(map.containsKey(JSR166TestCase.six));
        }
    }

    /**
     * replace succeeds if the key is already present
     */
    @Test(timeout = 5000)
    public void testReplace2() throws IOException {
        try (ChronicleMap map = ChronicleMapTest.map5()) {
            Assert.assertNotNull(map.replace(JSR166TestCase.one, "Z"));
            Assert.assertEquals("Z", map.get(JSR166TestCase.one).toString());
        }
    }

    /**
     * replace value fails when the given key not mapped to expected value
     */
    @Test(timeout = 5000)
    public void testReplaceValue() throws IOException {
        try (ChronicleMap map = ChronicleMapTest.map5()) {
            Assert.assertEquals("A", map.get(JSR166TestCase.one).toString());
            Assert.assertFalse(map.replace(JSR166TestCase.one, "Z", "Z"));
            Assert.assertEquals("A", map.get(JSR166TestCase.one).toString());
        }
    }

    /**
     * replace value succeeds when the given key mapped to expected value
     */
    @Test(timeout = 5000)
    public void testReplaceValue2() throws IOException {
        try (ChronicleMap map = ChronicleMapTest.map5()) {
            Assert.assertEquals("A", map.get(JSR166TestCase.one).toString());
            Assert.assertTrue(map.replace(JSR166TestCase.one, "A", "Z"));
            Assert.assertEquals("Z", map.get(JSR166TestCase.one).toString());
        }
    }

    /**
     * remove removes the correct key-value pair from the map
     */
    @Test(timeout = 5000)
    public void testRemove() throws IOException {
        try (ChronicleMap map = ChronicleMapTest.map5()) {
            map.remove(JSR166TestCase.five);
            Assert.assertEquals(4, map.size());
            Assert.assertFalse(map.containsKey(JSR166TestCase.five));
        }
    }

    /**
     * remove(key,value) removes only if pair present
     */
    @Test(timeout = 5000)
    public void testRemove2() throws IOException {
        /* try(   ChronicleMap map = map5(8076)) {
        map.remove(five, "E");
        assertEquals(4, map.size());
        assertFalse(map.containsKey(five));
        map.remove(four, "A");
        assertEquals(4, map.size());
        assertTrue(map.containsKey(four));
         */
    }

    /**
     * size returns the correct values
     */
    @Test(timeout = 5000)
    public void testSize() throws IOException {
        try (ChronicleMap map = ChronicleMapTest.map5()) {
            try (ChronicleMap empty = ChronicleMapTest.newShmIntString(8078)) {
                Assert.assertEquals(0, empty.size());
                Assert.assertEquals(5, map.size());
            }
        }
    }

    /**
     * size returns the correct values
     */
    @Test(timeout = 10000)
    public void testSize2() throws IOException {
        try (ChronicleMap map = ChronicleMapTest.map5()) {
            try (ChronicleMap empty = ChronicleMapTest.newShmIntString(8078)) {
                Assert.assertEquals(0, empty.size());
                Assert.assertEquals(5, map.size());
            }
        }
    }

    /**
     * size returns the correct values
     */
    @Test(timeout = 5000)
    public void testSize3() throws IOException {
        try (ChronicleMap map = ChronicleMapTest.map5()) {
            try (ChronicleMap empty = ChronicleMapTest.newShmIntString(8078)) {
                Assert.assertEquals(0, empty.size());
                Assert.assertEquals(5, map.size());
            }
        }
    }

    /**
     * toString contains toString of elements
     */
    @Test(timeout = 5000)
    public void testToString() throws IOException {
        try (ChronicleMap map = ChronicleMapTest.map5()) {
            String s = map.toString();
            for (int i = 1; i <= 5; ++i) {
                Assert.assertTrue(s.contains(String.valueOf(i)));
            }
        }
    }

    /**
     * get(null) throws NPE
     */
    @Test(timeout = 5000)
    public void testGet_NullPointerException() throws IOException {
        try (ChronicleMap c = ChronicleMapTest.newShmIntString(8076)) {
            c.get(null);
            shouldThrow();
        } catch (NullPointerException success) {
        }
    }

    /**
     * containsKey(null) throws NPE
     */
    @Test(timeout = 5000)
    public void testContainsKey_NullPointerException() throws IOException {
        try (ChronicleMap c = ChronicleMapTest.newShmIntString(8076)) {
            c.containsKey(null);
            shouldThrow();
        } catch (NullPointerException success) {
        }
    }

    /**
     * put(null,x) throws NPE
     */
    @Test(timeout = 5000)
    public void testPut1_NullPointerException() throws IOException {
        try (ChronicleMap c = ChronicleMapTest.newShmIntString(8076)) {
            c.put(null, "whatever");
            shouldThrow();
        } catch (NullPointerException success) {
        }
    }

    /**
     * put(x, null) throws NPE
     */
    @Test(timeout = 5000)
    public void testPut2_NullPointerException() throws IOException {
        try (ChronicleMap c = ChronicleMapTest.newShmIntString(8076)) {
            c.put(JSR166TestCase.notPresent, null);
            shouldThrow();
        } catch (NullPointerException success) {
        }
    }

    /**
     * putIfAbsent(null, x) throws NPE
     */
    @Test(timeout = 5000)
    public void testPutIfAbsent1_NullPointerException() throws IOException {
        try (ChronicleMap c = ChronicleMapTest.newShmIntString(8076)) {
            c.putIfAbsent(null, "whatever");
            shouldThrow();
        } catch (NullPointerException success) {
        }
    }

    /**
     * replace(null, x) throws NPE
     */
    @Test(timeout = 5000)
    public void testReplace_NullPointerException() throws IOException {
        try (ChronicleMap c = ChronicleMapTest.newShmIntString(8076)) {
            c.replace(null, "whatever");
            shouldThrow();
        } catch (NullPointerException success) {
        }
    }

    /**
     * replace(null, x, y) throws NPE
     */
    @Test(timeout = 5000)
    public void testReplaceValue_NullPointerException() throws IOException {
        try (ChronicleMap c = ChronicleMapTest.newShmIntString(8076)) {
            c.replace(null, "A", "whatever");
            shouldThrow();
        } catch (NullPointerException success) {
        }
    }

    /**
     * putIfAbsent(x, null) throws NPE
     */
    @Test(timeout = 5000)
    public void testPutIfAbsent2_NullPointerException() throws IOException {
        try (ChronicleMap c = ChronicleMapTest.newShmIntString(8076)) {
            c.putIfAbsent(JSR166TestCase.notPresent, null);
            shouldThrow();
        } catch (NullPointerException success) {
        }
    }

    /**
     * replace(x, null) throws NPE
     */
    @Test(timeout = 5000)
    public void testReplace2_NullPointerException() throws IOException {
        try (ChronicleMap c = ChronicleMapTest.newShmIntString(8076)) {
            c.replace(JSR166TestCase.notPresent, null);
            shouldThrow();
        } catch (NullPointerException success) {
        }
    }

    /**
     * replace(x, null, y) throws NPE
     */
    @Test(timeout = 5000)
    public void testReplaceValue2_NullPointerException() throws IOException {
        try (ChronicleMap c = ChronicleMapTest.newShmIntString(8076)) {
            c.replace(JSR166TestCase.notPresent, null, "A");
            shouldThrow();
        } catch (NullPointerException success) {
        }
    }

    /**
     * replace(x, y, null) throws NPE
     */
    @Test(timeout = 5000)
    public void testReplaceValue3_NullPointerException() throws IOException {
        try (ChronicleMap c = ChronicleMapTest.newShmIntString(8076)) {
            c.replace(JSR166TestCase.notPresent, "A", null);
            shouldThrow();
        } catch (NullPointerException success) {
        }
    }

    /**
     * remove(null) throws NPE
     */
    @Test(timeout = 5000)
    public void testRemove1_NullPointerException() throws IOException {
        try (ChronicleMap c = ChronicleMapTest.newStrStrMap(8076)) {
            c.put("sadsdf", "asdads");
            c.remove(null);
            shouldThrow();
        } catch (NullPointerException success) {
        }
    }

    /**
     * remove(null, x) throws NPE
     */
    @Test(timeout = 5000)
    public void testRemove2_NullPointerException() throws IOException {
        try (ChronicleMap c = ChronicleMapTest.newStrStrMap(8086)) {
            c.put("sadsdf", "asdads");
            c.remove(null, "whatever");
            shouldThrow();
        } catch (NullPointerException success) {
        }
    }

    /**
     * remove(x, null) returns false
     */
    @Test(timeout = 5000)
    public void testRemove3() throws IOException {
        try (ChronicleMap c = ChronicleMapTest.newStrStrMap(8076)) {
            c.put("sadsdf", "asdads");
            Assert.assertFalse(c.remove("sadsdf", null));
        }
    }

    // classes for testing Comparable fallbacks
    static class BI implements Comparable<ChronicleMapTest.BI> {
        private final int value;

        BI(int value) {
            this.value = value;
        }

        public int compareTo(ChronicleMapTest.BI other) {
            return Integer.compare(value, other.value);
        }

        public boolean equals(Object x) {
            return (x instanceof ChronicleMapTest.BI) && ((((ChronicleMapTest.BI) (x)).value) == (value));
        }

        public int hashCode() {
            return 42;
        }
    }

    static class CI extends ChronicleMapTest.BI {
        CI(int value) {
            super(value);
        }
    }

    static class DI extends ChronicleMapTest.BI {
        DI(int value) {
            super(value);
        }
    }

    static class BS implements Comparable<ChronicleMapTest.BS> {
        private final String value;

        BS(String value) {
            this.value = value;
        }

        public int compareTo(ChronicleMapTest.BS other) {
            return value.compareTo(other.value);
        }

        public boolean equals(Object x) {
            return (x instanceof ChronicleMapTest.BS) && (value.equals(((ChronicleMapTest.BS) (x)).value));
        }

        public int hashCode() {
            return 42;
        }
    }

    static class LexicographicList<E extends Comparable<E>> extends ArrayList<E> implements Comparable<ChronicleMapTest.LexicographicList<E>> {
        private static final long serialVersionUID = 0;

        static long total;

        static long n;

        LexicographicList(Collection<E> c) {
            super(c);
        }

        LexicographicList(E e) {
            super(Collections.singleton(e));
        }

        public int compareTo(ChronicleMapTest.LexicographicList<E> other) {
            long start = System.currentTimeMillis();
            int common = Math.min(size(), other.size());
            int r = 0;
            for (int i = 0; i < common; i++) {
                if ((r = get(i).compareTo(other.get(i))) != 0)
                    break;

            }
            if (r == 0)
                r = Integer.compare(size(), other.size());

            ChronicleMapTest.LexicographicList.total += (System.currentTimeMillis()) - start;
            (ChronicleMapTest.LexicographicList.n)++;
            return r;
        }
    }
}

