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
package net.openhft.chronicle.map;


import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import net.openhft.chronicle.map.jsr166.JSR166TestCase;
import org.junit.Assert;
import org.junit.Test;


/* Originally written by Doug Lea with assistance from members of JCP JSR-166
Expert Group and released to the public domain, as explained at
http://creativecommons.org/publicdomain/zero/1.0/
Other contributors include Andrew Wright, Jeffrey Hayes,
Pat Fisher, Mike Judd. Then modified by the Open HFT team.
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
public class ReplicatedChronicleMapTest extends JSR166TestCase {
    /**
     * clear removes all pairs
     */
    @Test
    public void testClear() throws IOException {
        ChronicleMap map = map5();
        map.clear();
        Assert.assertEquals(0, map.size());
    }

    /**
     * Maps with same contents are equal
     */
    @Test
    public void testEquals() throws IOException {
        ChronicleMap map1 = map5();
        ChronicleMap map2 = map5();
        Assert.assertEquals(map1, map2);
        Assert.assertEquals(map2, map1);
        map1.clear();
        Assert.assertFalse(map1.equals(map2));
        Assert.assertFalse(map2.equals(map1));
    }

    /**
     * contains returns true for contained value
     */
    @Test
    public void testContains() throws IOException {
        ChronicleMap map = map5();
        Assert.assertTrue(map.containsValue("A"));
        Assert.assertFalse(map.containsValue("Z"));
    }

    /**
     * containsKey returns true for contained key
     */
    @Test
    public void testContainsKey() throws IOException {
        ChronicleMap map = map5();
        Assert.assertTrue(map.containsKey(JSR166TestCase.one));
        Assert.assertFalse(map.containsKey(JSR166TestCase.zero));
    }

    /**
     * containsValue returns true for held values
     */
    @Test
    public void testContainsValue() throws IOException {
        ChronicleMap map = map5();
        Assert.assertTrue(map.containsValue("A"));
        Assert.assertFalse(map.containsValue("Z"));
    }

    /**
     * Inserted elements that are subclasses of the same Comparable
     * class are found.
     */
    /* @Test public void testComparableFamily() throws IOException {
    ChronicleMap<BI, Boolean> m =
    newShmBiBoolean();
    for (int i = 0; i < 1000; i++) {
    assertTrue(m.put(new CI(i), true) == null);
    }
    for (int i = 0; i < 1000; i++) {
    assertTrue(m.containsKey(new CI(i)));
    assertTrue(m.containsKey(new DI(i)));
    }
    }
     */
    /**
     * TODO :     fix test
     * Elements of classes with erased generic type parameters based
     * on Comparable can be inserted and found.
     */
    /* @Test public void testGenericComparable() throws IOException {
    ChronicleMap<Object, Boolean> m =
    newShmBiBoolean();
    for (int i = 0; i < 1000; i++) {
    BI bi = new BI(i);
    BS bs = new BS(String.valueOf(i));
    LexicographicList<BI> bis = new LexicographicList<BI>(bi);
    LexicographicList<BS> bss = new LexicographicList<BS>(bs);
    assertTrue(m.putIfAbsent(bis, true) == null);
    assertTrue(m.containsKey(bis));
    if (m.putIfAbsent(bss, true) == null)
    assertTrue(m.containsKey(bss));
    assertTrue(m.containsKey(bis));
    }
    for (int i = 0; i < 1000; i++) {
    assertTrue(m.containsKey(new ArrayList(Collections.singleton(new BI(i)))));
    }
    }
     */
    /**
     * Elements of non-comparable classes equal to those of classes
     * with erased generic type parameters based on Comparable can be
     * inserted and found.
     */
    /* @Test public void testGenericComparable2() throws IOException {
    ChronicleMap<Object, Boolean> m =
    newShmListBoolean();
    for (int i = 0; i < 1000; i++) {
    m.put(new ArrayList(Collections.singleton(new BI(i))), true);
    }

    for (int i = 0; i < 1000; i++) {
    LexicographicList<BI> bis = new LexicographicList<BI>(new BI(i));
    assertTrue(m.containsKey(bis));
    }
    }
     */
    /**
     * get returns the correct element at the given key, or null if not present
     */
    @Test
    public void testGet() throws IOException {
        ChronicleMap map = map5();
        Assert.assertEquals("A", map.get(JSR166TestCase.one).toString());
        ChronicleMap empty = newShmIntString();
        Assert.assertNull(map.get(JSR166TestCase.notPresent));
    }

    /**
     * isEmpty is true of empty map and false for non-empty
     */
    @Test
    public void testIsEmpty() throws IOException {
        ChronicleMap empty = newShmIntString();
        ChronicleMap map = map5();
        Assert.assertTrue(empty.isEmpty());
        Assert.assertFalse(map.isEmpty());
    }

    /**
     * keySet returns a Set containing all the keys
     */
    @Test
    public void testKeySet() throws IOException {
        ChronicleMap map = map5();
        Set s = map.keySet();
        Assert.assertEquals(5, s.size());
        Assert.assertTrue(s.contains(JSR166TestCase.one));
        Assert.assertTrue(s.contains(JSR166TestCase.two));
        Assert.assertTrue(s.contains(JSR166TestCase.three));
        Assert.assertTrue(s.contains(JSR166TestCase.four));
        Assert.assertTrue(s.contains(JSR166TestCase.five));
    }

    /**
     * keySet.toArray returns contains all keys
     */
    @Test
    public void testKeySetToArray() throws IOException {
        ChronicleMap map = map5();
        Set s = map.keySet();
        Object[] ar = s.toArray();
        Assert.assertTrue(s.containsAll(Arrays.asList(ar)));
        Assert.assertEquals(5, ar.length);
        ar[0] = JSR166TestCase.m10;
        Assert.assertFalse(s.containsAll(Arrays.asList(ar)));
    }

    /**
     * Values.toArray contains all values
     */
    @Test
    public void testValuesToArray() throws IOException {
        ChronicleMap map = map5();
        Collection v = map.values();
        ArrayList<CharSequence> s = new ArrayList(map.values());
        Assert.assertEquals(5, s.size());
        Assert.assertTrue(s.stream().anyMatch("A"::contentEquals));
        Assert.assertTrue(s.stream().anyMatch("B"::contentEquals));
        Assert.assertTrue(s.stream().anyMatch("C"::contentEquals));
        Assert.assertTrue(s.stream().anyMatch("D"::contentEquals));
        Assert.assertTrue(s.stream().anyMatch("E"::contentEquals));
    }

    /**
     * TODO : enumeration returns an enumeration containing the correct
     * elements
     */
    /* @Test public void testEnumeration() throws IOException {
    ChronicleMap map = map5();
    Enumeration e = map.elements();
    int count = 0;
    while (e.hasMoreElements()) {
    count++;
    e.nextElement();
    }
    assertEquals(5, count);
    }
     */
    /**
     * entrySet.toArray contains all entries
     */
    @Test
    public void testEntrySetToArray() throws IOException {
        ChronicleMap map = map5();
        Set s = map.entrySet();
        Object[] ar = s.toArray();
        Assert.assertEquals(5, ar.length);
        for (int i = 0; i < 5; ++i) {
            Assert.assertTrue(map.containsKey(((Map.Entry) (ar[i])).getKey()));
            Assert.assertTrue(map.containsValue(((Map.Entry) (ar[i])).getValue()));
        }
    }

    /**
     * values collection contains all values
     */
    @Test
    public void testValues() throws IOException {
        ChronicleMap map = map5();
        Collection s = map.values();
        Assert.assertEquals(5, s.size());
        Assert.assertTrue(s.contains("A"));
        Assert.assertTrue(s.contains("B"));
        Assert.assertTrue(s.contains("C"));
        Assert.assertTrue(s.contains("D"));
        Assert.assertTrue(s.contains("E"));
    }

    /**
     * TODO : keys returns an enumeration containing all the keys from the map
     */
    /* @Test public void testKeys() throws IOException {
    ChronicleMap map = map5();
    Enumeration e = map.keys();
    int count = 0;
    while (e.hasMoreElements()) {
    count++;
    e.nextElement();
    }
    assertEquals(5, count);
    }
     */
    /**
     * entrySet contains all pairs
     */
    @Test
    public void testEntrySet() throws IOException {
        ChronicleMap map = map5();
        Set s = map.entrySet();
        Assert.assertEquals(5, s.size());
        Iterator it = s.iterator();
        while (it.hasNext()) {
            Map.Entry<Integer, CharSequence> e = ((Map.Entry<Integer, CharSequence>) (it.next()));
            Assert.assertTrue(((((((e.getKey().equals(JSR166TestCase.one)) && ("A".contentEquals(e.getValue()))) || ((e.getKey().equals(JSR166TestCase.two)) && ("B".contentEquals(e.getValue())))) || ((e.getKey().equals(JSR166TestCase.three)) && ("C".contentEquals(e.getValue())))) || ((e.getKey().equals(JSR166TestCase.four)) && ("D".contentEquals(e.getValue())))) || ((e.getKey().equals(JSR166TestCase.five)) && ("E".contentEquals(e.getValue())))));
        } 
    }

    /**
     * putAll adds all key-value pairs from the given map
     */
    @Test
    public void testPutAll() throws IOException {
        ChronicleMap empty = newShmIntString();
        ChronicleMap map = map5();
        empty.putAll(map);
        Assert.assertEquals(5, empty.size());
        Assert.assertTrue(empty.containsKey(JSR166TestCase.one));
        Assert.assertTrue(empty.containsKey(JSR166TestCase.two));
        Assert.assertTrue(empty.containsKey(JSR166TestCase.three));
        Assert.assertTrue(empty.containsKey(JSR166TestCase.four));
        Assert.assertTrue(empty.containsKey(JSR166TestCase.five));
    }

    /**
     * putIfAbsent works when the given key is not present
     */
    @Test
    public void testPutIfAbsent() throws IOException {
        ChronicleMap map = map5();
        map.putIfAbsent(JSR166TestCase.six, "Z");
        Assert.assertTrue(map.containsKey(JSR166TestCase.six));
    }

    /**
     * putIfAbsent does not add the ?pair if the key is already present
     */
    @Test
    public void testPutIfAbsent2() throws IOException {
        ChronicleMap map = map5();
        Assert.assertEquals("A", map.putIfAbsent(JSR166TestCase.one, "Z").toString());
    }

    /**
     * replace fails when the given key is not present
     */
    @Test
    public void testReplace() throws IOException {
        ChronicleMap map = map5();
        Assert.assertNull(map.replace(JSR166TestCase.six, "Z"));
        Assert.assertFalse(map.containsKey(JSR166TestCase.six));
    }

    /**
     * replace succeeds if the key is already present
     */
    @Test
    public void testReplace2() throws IOException {
        ChronicleMap map = map5();
        Assert.assertNotNull(map.replace(JSR166TestCase.one, "Z"));
        Assert.assertEquals("Z", map.get(JSR166TestCase.one).toString());
    }

    /**
     * replace value fails when the given key not mapped to expected value
     */
    @Test
    public void testReplaceValue() throws IOException {
        ChronicleMap map = map5();
        Assert.assertEquals("A", map.get(JSR166TestCase.one).toString());
        Assert.assertFalse(map.replace(JSR166TestCase.one, "Z", "Z"));
        Assert.assertEquals("A", map.get(JSR166TestCase.one).toString());
    }

    /**
     * replace value succeeds when the given key mapped to expected value
     */
    @Test
    public void testReplaceValue2() throws IOException {
        ChronicleMap map = map5();
        Assert.assertEquals("A", map.get(JSR166TestCase.one).toString());
        Assert.assertTrue(map.replace(JSR166TestCase.one, "A", "Z"));
        Assert.assertEquals("Z", map.get(JSR166TestCase.one).toString());
    }

    /**
     * remove removes the correct key-value pair from the map
     */
    @Test
    public void testRemove() throws IOException {
        ChronicleMap map = map5();
        map.remove(JSR166TestCase.five);
        Assert.assertEquals(4, map.size());
        Assert.assertFalse(map.containsKey(JSR166TestCase.five));
    }

    /**
     * remove(key,value) removes only if pair present
     */
    @Test
    public void testRemove2() throws IOException {
        ChronicleMap map = map5();
        map.remove(JSR166TestCase.five, "E");
        Assert.assertEquals(4, map.size());
        Assert.assertFalse(map.containsKey(JSR166TestCase.five));
        map.remove(JSR166TestCase.four, "A");
        Assert.assertEquals(4, map.size());
        Assert.assertTrue(map.containsKey(JSR166TestCase.four));
    }

    /**
     * size returns the correct values
     */
    @Test
    public void testSize() throws IOException {
        ChronicleMap map = map5();
        ChronicleMap empty = newShmIntString();
        Assert.assertEquals(0, empty.size());
        Assert.assertEquals(5, map.size());
    }

    /**
     * toString contains toString of elements
     */
    @Test
    public void testToString() throws IOException {
        ChronicleMap map = map5();
        String s = map.toString();
        for (int i = 1; i <= 5; ++i) {
            Assert.assertTrue(s.contains(String.valueOf(i)));
        }
    }

    /**
     * get(null) throws NPE
     */
    @Test
    public void testGet_NullPointerException() throws IOException {
        try {
            ChronicleMap c = newShmIntString();
            c.get(null);
            shouldThrow();
        } catch (NullPointerException success) {
        }
    }

    /**
     * containsKey(null) throws NPE
     */
    @Test
    public void testContainsKey_NullPointerException() throws IOException {
        try {
            ChronicleMap c = newShmIntString();
            c.containsKey(null);
            shouldThrow();
        } catch (NullPointerException success) {
        }
    }

    /**
     * put(null,x) throws NPE
     */
    @Test
    public void testPut1_NullPointerException() throws IOException {
        try {
            ChronicleMap c = newShmIntString();
            c.put(null, "whatever");
            shouldThrow();
        } catch (NullPointerException success) {
        }
    }

    /**
     * put(x, null) throws NPE
     */
    @Test
    public void testPut2_NullPointerException() throws IOException {
        try {
            ChronicleMap c = newShmIntString();
            c.put(JSR166TestCase.notPresent, null);
            shouldThrow();
        } catch (NullPointerException success) {
        }
    }

    /**
     * putIfAbsent(null, x) throws NPE
     */
    @Test
    public void testPutIfAbsent1_NullPointerException() throws IOException {
        try {
            ChronicleMap c = newShmIntString();
            c.putIfAbsent(null, "whatever");
            shouldThrow();
        } catch (NullPointerException success) {
        }
    }

    // Exception tests
    /**
     * TODO : Cannot create with negative capacity
     */
    /* @Test public void testConstructor1() {
    try {
    newShmIntString(-1, 0, 1);
    shouldThrow();
    } catch (IllegalArgumentException success) {
    }
    }
     */
    /**
     * TODO : Cannot create with negative concurrency level
     */
    /* @Test public void testConstructor2() {
    try {
    newShmIntString(1, 0, -1);
    shouldThrow();
    } catch (IllegalArgumentException success) {
    }
    }
     */
    /**
     * TODO :Cannot create with only negative capacity
     */
    /* @Test public void testConstructor3() {
    try {
    newShmIntString(-1);
    shouldThrow();
    } catch (IllegalArgumentException success) {
    }
    }
     */
    /**
     * replace(null, x) throws NPE
     */
    @Test
    public void testReplace_NullPointerException() throws IOException {
        try {
            ChronicleMap c = newShmIntString();
            c.replace(null, "whatever");
            shouldThrow();
        } catch (NullPointerException success) {
        }
    }

    /**
     * replace(null, x, y) throws NPE
     */
    @Test
    public void testReplaceValue_NullPointerException() throws IOException {
        try {
            ChronicleMap c = newShmIntString();
            c.replace(null, "A", "whatever");
            shouldThrow();
        } catch (NullPointerException success) {
        }
    }

    /**
     * containsValue(null) throws NPE
     */
    /* @Test public void testContainsValue_NullPointerException() throws IOException {
    try {
    ChronicleMap c = newShmIntString(5);
    c.containsValue(null);
    shouldThrow();
    } catch (NullPointerException success) {
    }
    }
     */
    /**
     * todo  : contains(null) throws NPE
     */
    /* @Test public void testContains_NullPointerException() throws IOException {
    try {
    ChronicleMap c = newShmIntString(5);
    c.contains(null);
    shouldThrow();
    } catch (NullPointerException success) {
    }
    }
     */
    /**
     * putIfAbsent(x, null) throws NPE
     */
    @Test
    public void testPutIfAbsent2_NullPointerException() throws IOException {
        try {
            ChronicleMap c = newShmIntString();
            c.putIfAbsent(JSR166TestCase.notPresent, null);
            shouldThrow();
        } catch (NullPointerException success) {
        }
    }

    /**
     * replace(x, null) throws NPE
     */
    @Test
    public void testReplace2_NullPointerException() throws IOException {
        try {
            ChronicleMap c = newShmIntString();
            c.replace(JSR166TestCase.notPresent, null);
            shouldThrow();
        } catch (NullPointerException success) {
        }
    }

    /**
     * replace(x, null, y) throws NPE
     */
    @Test
    public void testReplaceValue2_NullPointerException() throws IOException {
        try {
            ChronicleMap c = newShmIntString();
            c.replace(JSR166TestCase.notPresent, null, "A");
            shouldThrow();
        } catch (NullPointerException success) {
        }
    }

    /**
     * replace(x, y, null) throws NPE
     */
    @Test
    public void testReplaceValue3_NullPointerException() throws IOException {
        try {
            ChronicleMap c = newShmIntString();
            c.replace(JSR166TestCase.notPresent, "A", null);
            shouldThrow();
        } catch (NullPointerException success) {
        }
    }

    /**
     * remove(null) throws NPE
     */
    @Test
    public void testRemove1_NullPointerException() throws IOException {
        try {
            ChronicleMap c = newShmStringString();
            c.put("sadsdf", "asdads");
            c.remove(null);
            shouldThrow();
        } catch (NullPointerException success) {
        }
    }

    /**
     * remove(null, x) throws NPE
     */
    @Test
    public void testRemove2_NullPointerException() throws IOException {
        try {
            ChronicleMap c = newShmStringString();
            c.put("sadsdf", "asdads");
            c.remove(null, "whatever");
            shouldThrow();
        } catch (NullPointerException success) {
        }
    }

    /**
     * remove(x, null) returns false
     */
    @Test
    public void testRemove3() throws IOException {
        ChronicleMap c = newShmStringString();
        c.put("sadsdf", "asdads");
        Assert.assertFalse(c.remove("sadsdf", null));
    }

    // classes for testing Comparable fallbacks
    static class BI implements Comparable<ReplicatedChronicleMapTest.BI> {
        private final int value;

        BI(int value) {
            this.value = value;
        }

        public int compareTo(ReplicatedChronicleMapTest.BI other) {
            return Integer.compare(value, other.value);
        }

        public boolean equals(Object x) {
            return (x instanceof ReplicatedChronicleMapTest.BI) && ((((ReplicatedChronicleMapTest.BI) (x)).value) == (value));
        }

        public int hashCode() {
            return 42;
        }
    }

    static class CI extends ReplicatedChronicleMapTest.BI {
        CI(int value) {
            super(value);
        }
    }

    static class DI extends ReplicatedChronicleMapTest.BI {
        DI(int value) {
            super(value);
        }
    }

    static class BS implements Comparable<ReplicatedChronicleMapTest.BS> {
        private final String value;

        BS(String value) {
            this.value = value;
        }

        public int compareTo(ReplicatedChronicleMapTest.BS other) {
            return value.compareTo(other.value);
        }

        public boolean equals(Object x) {
            return (x instanceof ReplicatedChronicleMapTest.BS) && (value.equals(((ReplicatedChronicleMapTest.BS) (x)).value));
        }

        public int hashCode() {
            return 42;
        }
    }

    static class LexicographicList<E extends Comparable<E>> extends ArrayList<E> implements Comparable<ReplicatedChronicleMapTest.LexicographicList<E>> {
        private static final long serialVersionUID = 0;

        static long total;

        static long n;

        LexicographicList(Collection<E> c) {
            super(c);
        }

        LexicographicList(E e) {
            super(Collections.singleton(e));
        }

        public int compareTo(ReplicatedChronicleMapTest.LexicographicList<E> other) {
            long start = System.currentTimeMillis();
            int common = Math.min(size(), other.size());
            int r = 0;
            for (int i = 0; i < common; i++) {
                if ((r = get(i).compareTo(other.get(i))) != 0)
                    break;

            }
            if (r == 0)
                r = Integer.compare(size(), other.size());

            ReplicatedChronicleMapTest.LexicographicList.total += (System.currentTimeMillis()) - start;
            (ReplicatedChronicleMapTest.LexicographicList.n)++;
            return r;
        }
    }
}

