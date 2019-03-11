/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to you under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.calcite.util;


import ImmutableBitSet.COMPARATOR;
import com.google.common.collect.Iterables;
import java.nio.LongBuffer;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import org.apache.calcite.runtime.Utilities;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


/**
 * Unit test for {@link org.apache.calcite.util.ImmutableBitSet}.
 */
public class ImmutableBitSetTest {
    /**
     * Tests the method {@link ImmutableBitSet#iterator()}.
     */
    @Test
    public void testIterator() {
        assertToIterBitSet("", ImmutableBitSet.of());
        assertToIterBitSet("0", ImmutableBitSet.of(0));
        assertToIterBitSet("0, 1", ImmutableBitSet.of(0, 1));
        assertToIterBitSet("10", ImmutableBitSet.of(10));
    }

    /**
     * Tests the method
     * {@link org.apache.calcite.util.ImmutableBitSet#toList()}.
     */
    @Test
    public void testToList() {
        Assert.assertThat(ImmutableBitSet.of().toList(), CoreMatchers.equalTo(Collections.<Integer>emptyList()));
        Assert.assertThat(ImmutableBitSet.of(5).toList(), CoreMatchers.equalTo(Arrays.asList(5)));
        Assert.assertThat(ImmutableBitSet.of(3, 5).toList(), CoreMatchers.equalTo(Arrays.asList(3, 5)));
        Assert.assertThat(ImmutableBitSet.of(63).toList(), CoreMatchers.equalTo(Arrays.asList(63)));
        Assert.assertThat(ImmutableBitSet.of(64).toList(), CoreMatchers.equalTo(Arrays.asList(64)));
        Assert.assertThat(ImmutableBitSet.of(3, 63).toList(), CoreMatchers.equalTo(Arrays.asList(3, 63)));
        Assert.assertThat(ImmutableBitSet.of(3, 64).toList(), CoreMatchers.equalTo(Arrays.asList(3, 64)));
        Assert.assertThat(ImmutableBitSet.of(0, 4, 2).toList(), CoreMatchers.equalTo(Arrays.asList(0, 2, 4)));
    }

    /**
     * Tests the method {@link BitSets#range(int, int)}.
     */
    @Test
    public void testRange() {
        Assert.assertEquals(ImmutableBitSet.range(0, 4).toList(), Arrays.asList(0, 1, 2, 3));
        Assert.assertEquals(ImmutableBitSet.range(1, 4).toList(), Arrays.asList(1, 2, 3));
        Assert.assertEquals(ImmutableBitSet.range(4).toList(), Arrays.asList(0, 1, 2, 3));
        Assert.assertEquals(ImmutableBitSet.range(0).toList(), Collections.<Integer>emptyList());
        Assert.assertEquals(ImmutableBitSet.range(2, 2).toList(), Collections.<Integer>emptyList());
        Assert.assertThat(ImmutableBitSet.range(63, 66).toString(), CoreMatchers.equalTo("{63, 64, 65}"));
        Assert.assertThat(ImmutableBitSet.range(65, 68).toString(), CoreMatchers.equalTo("{65, 66, 67}"));
        Assert.assertThat(ImmutableBitSet.range(65, 65).toString(), CoreMatchers.equalTo("{}"));
        Assert.assertThat(ImmutableBitSet.range(65, 65).length(), CoreMatchers.equalTo(0));
        Assert.assertThat(ImmutableBitSet.range(65, 165).cardinality(), CoreMatchers.equalTo(100));
        // Same tests as above, using a builder.
        Assert.assertThat(ImmutableBitSet.builder().set(63, 66).build().toString(), CoreMatchers.equalTo("{63, 64, 65}"));
        Assert.assertThat(ImmutableBitSet.builder().set(65, 68).build().toString(), CoreMatchers.equalTo("{65, 66, 67}"));
        Assert.assertThat(ImmutableBitSet.builder().set(65, 65).build().toString(), CoreMatchers.equalTo("{}"));
        Assert.assertThat(ImmutableBitSet.builder().set(65, 65).build().length(), CoreMatchers.equalTo(0));
        Assert.assertThat(ImmutableBitSet.builder().set(65, 165).build().cardinality(), CoreMatchers.equalTo(100));
        final ImmutableBitSet e0 = ImmutableBitSet.range(0, 0);
        final ImmutableBitSet e1 = ImmutableBitSet.of();
        Assert.assertTrue(e0.equals(e1));
        Assert.assertThat(e0.hashCode(), CoreMatchers.equalTo(e1.hashCode()));
        // Empty builder returns the singleton empty set.
        Assert.assertTrue(((ImmutableBitSet.builder().build()) == (ImmutableBitSet.of())));
    }

    @Test
    public void testCompare() {
        final List<ImmutableBitSet> sorted = getSortedList();
        for (int i = 0; i < (sorted.size()); i++) {
            for (int j = 0; j < (sorted.size()); j++) {
                final ImmutableBitSet set0 = sorted.get(i);
                final ImmutableBitSet set1 = sorted.get(j);
                int c = set0.compareTo(set1);
                if (c == 0) {
                    Assert.assertTrue((((i == j) || ((i == 3) && (j == 4))) || ((i == 4) && (j == 3))));
                } else {
                    Assert.assertEquals(c, Utilities.compare(i, j));
                }
                Assert.assertEquals((c == 0), set0.equals(set1));
                Assert.assertEquals((c == 0), set1.equals(set0));
            }
        }
    }

    @Test
    public void testCompare2() {
        final List<ImmutableBitSet> sorted = getSortedList();
        sorted.sort(COMPARATOR);
        Assert.assertThat(sorted.toString(), CoreMatchers.equalTo("[{0, 1, 3}, {0, 1}, {1, 1000}, {1}, {1}, {2, 3}, {}]"));
    }

    /**
     * Tests the method
     * {@link org.apache.calcite.util.ImmutableBitSet#toArray}.
     */
    @Test
    public void testToArray() {
        int[][] arrays = new int[][]{ new int[]{  }, new int[]{ 0 }, new int[]{ 0, 2 }, new int[]{ 1, 65 }, new int[]{ 100 } };
        for (int[] array : arrays) {
            Assert.assertThat(ImmutableBitSet.of(array).toArray(), CoreMatchers.equalTo(array));
        }
    }

    /**
     * Tests the methods
     * {@link org.apache.calcite.util.ImmutableBitSet#toList}, and
     * {@link org.apache.calcite.util.ImmutableBitSet#asList} and
     * {@link org.apache.calcite.util.ImmutableBitSet#asSet}.
     */
    @Test
    public void testAsList() {
        final List<ImmutableBitSet> list = getSortedList();
        // create a set of integers in and not in the lists
        final Set<Integer> integers = new HashSet<>();
        for (ImmutableBitSet set : list) {
            for (Integer integer : set) {
                integers.add(integer);
                integers.add((integer + 1));
                integers.add((integer + 10));
            }
        }
        for (ImmutableBitSet bitSet : list) {
            final List<Integer> list1 = bitSet.toList();
            final List<Integer> listView = bitSet.asList();
            final Set<Integer> setView = bitSet.asSet();
            Assert.assertThat(list1.size(), CoreMatchers.equalTo(bitSet.cardinality()));
            Assert.assertThat(listView.size(), CoreMatchers.equalTo(bitSet.cardinality()));
            Assert.assertThat(setView.size(), CoreMatchers.equalTo(bitSet.cardinality()));
            Assert.assertThat(list1.toString(), CoreMatchers.equalTo(listView.toString()));
            Assert.assertThat(list1.toString(), CoreMatchers.equalTo(setView.toString()));
            Assert.assertTrue(list1.equals(listView));
            Assert.assertThat(list1.hashCode(), CoreMatchers.equalTo(listView.hashCode()));
            final Set<Integer> set = new HashSet<>(list1);
            Assert.assertThat(setView.hashCode(), CoreMatchers.is(set.hashCode()));
            Assert.assertThat(setView, CoreMatchers.equalTo(set));
            for (Integer integer : integers) {
                final boolean b = list1.contains(integer);
                Assert.assertThat(listView.contains(integer), CoreMatchers.is(b));
                Assert.assertThat(setView.contains(integer), CoreMatchers.is(b));
            }
        }
    }

    /**
     * Tests the method
     * {@link org.apache.calcite.util.ImmutableBitSet#union(ImmutableBitSet)}.
     */
    @Test
    public void testUnion() {
        Assert.assertThat(ImmutableBitSet.of(1).union(ImmutableBitSet.of(3)).toString(), CoreMatchers.equalTo("{1, 3}"));
        Assert.assertThat(ImmutableBitSet.of(1).union(ImmutableBitSet.of(3, 100)).toString(), CoreMatchers.equalTo("{1, 3, 100}"));
        ImmutableBitSet x = ImmutableBitSet.of(1).rebuild().addAll(ImmutableBitSet.of(2)).addAll(ImmutableBitSet.of()).addAll(ImmutableBitSet.of(3)).build();
        Assert.assertThat(x.toString(), CoreMatchers.equalTo("{1, 2, 3}"));
    }

    @Test
    public void testIntersect() {
        Assert.assertThat(ImmutableBitSet.of(1, 2, 3, 100, 200).intersect(ImmutableBitSet.of(2, 100)).toString(), CoreMatchers.equalTo("{2, 100}"));
        Assert.assertTrue(((ImmutableBitSet.of(1, 3, 5, 101, 20001).intersect(ImmutableBitSet.of(2, 100))) == (ImmutableBitSet.of())));
    }

    /**
     * Tests the method
     * {@link org.apache.calcite.util.ImmutableBitSet#contains(org.apache.calcite.util.ImmutableBitSet)}.
     */
    @Test
    public void testBitSetsContains() {
        Assert.assertTrue(ImmutableBitSet.range(0, 5).contains(ImmutableBitSet.range(2, 4)));
        Assert.assertTrue(ImmutableBitSet.range(0, 5).contains(ImmutableBitSet.range(4)));
        Assert.assertFalse(ImmutableBitSet.range(0, 5).contains(ImmutableBitSet.of(14)));
        Assert.assertFalse(ImmutableBitSet.range(20, 25).contains(ImmutableBitSet.of(14)));
        final ImmutableBitSet empty = ImmutableBitSet.of();
        Assert.assertTrue(ImmutableBitSet.range(20, 25).contains(empty));
        Assert.assertTrue(empty.contains(empty));
        Assert.assertFalse(empty.contains(ImmutableBitSet.of(0)));
        Assert.assertFalse(empty.contains(ImmutableBitSet.of(1)));
        Assert.assertFalse(empty.contains(ImmutableBitSet.of(63)));
        Assert.assertFalse(empty.contains(ImmutableBitSet.of(64)));
        Assert.assertFalse(empty.contains(ImmutableBitSet.of(1000)));
        Assert.assertTrue(ImmutableBitSet.of(1, 4, 7).contains(ImmutableBitSet.of(1, 4, 7)));
    }

    /**
     * Tests the method
     * {@link org.apache.calcite.util.ImmutableBitSet#of(org.apache.calcite.util.ImmutableIntList)}.
     */
    @Test
    public void testBitSetOfImmutableIntList() {
        ImmutableIntList list = ImmutableIntList.of();
        Assert.assertThat(ImmutableBitSet.of(list), CoreMatchers.equalTo(ImmutableBitSet.of()));
        list = ImmutableIntList.of(2, 70, 5, 0);
        Assert.assertThat(ImmutableBitSet.of(list), CoreMatchers.equalTo(ImmutableBitSet.of(0, 2, 5, 70)));
    }

    /**
     * Tests the method
     * {@link org.apache.calcite.util.ImmutableBitSet#previousClearBit(int)}.
     */
    @Test
    public void testPreviousClearBit() {
        Assert.assertThat(ImmutableBitSet.of().previousClearBit(10), CoreMatchers.equalTo(10));
        Assert.assertThat(ImmutableBitSet.of().previousClearBit(0), CoreMatchers.equalTo(0));
        Assert.assertThat(ImmutableBitSet.of().previousClearBit((-1)), CoreMatchers.equalTo((-1)));
        try {
            final int actual = ImmutableBitSet.of().previousClearBit((-2));
            Assert.fail(("expected exception, got " + actual));
        } catch (IndexOutOfBoundsException e) {
            // ok
        }
        Assert.assertThat(ImmutableBitSet.of(0, 1, 3, 4).previousClearBit(4), CoreMatchers.equalTo(2));
        Assert.assertThat(ImmutableBitSet.of(0, 1, 3, 4).previousClearBit(3), CoreMatchers.equalTo(2));
        Assert.assertThat(ImmutableBitSet.of(0, 1, 3, 4).previousClearBit(2), CoreMatchers.equalTo(2));
        Assert.assertThat(ImmutableBitSet.of(0, 1, 3, 4).previousClearBit(1), CoreMatchers.equalTo((-1)));
        Assert.assertThat(ImmutableBitSet.of(1, 3, 4).previousClearBit(1), CoreMatchers.equalTo(0));
    }

    @Test
    public void testBuilder() {
        Assert.assertThat(ImmutableBitSet.builder().set(9).set(100).set(1000).clear(250).set(88).clear(100).clear(1000).build().toString(), CoreMatchers.equalTo("{9, 88}"));
    }

    /**
     * Unit test for
     * {@link org.apache.calcite.util.ImmutableBitSet.Builder#build(ImmutableBitSet)}.
     */
    @Test
    public void testBuilderUseOriginal() {
        final ImmutableBitSet fives = ImmutableBitSet.of(5, 10, 15);
        final ImmutableBitSet fives1 = fives.rebuild().clear(2).set(10).build();
        Assert.assertTrue((fives1 == fives));
        final ImmutableBitSet fives2 = ImmutableBitSet.builder().addAll(fives).clear(2).set(10).build(fives);
        Assert.assertTrue((fives2 == fives));
        final ImmutableBitSet fives3 = ImmutableBitSet.builder().addAll(fives).clear(2).set(10).build();
        Assert.assertTrue((fives3 != fives));
        Assert.assertTrue(fives3.equals(fives));
        Assert.assertTrue(fives3.equals(fives2));
    }

    @Test
    public void testIndexOf() {
        Assert.assertThat(ImmutableBitSet.of(0, 2, 4).indexOf(0), CoreMatchers.equalTo(0));
        Assert.assertThat(ImmutableBitSet.of(0, 2, 4).indexOf(2), CoreMatchers.equalTo(1));
        Assert.assertThat(ImmutableBitSet.of(0, 2, 4).indexOf(3), CoreMatchers.equalTo((-1)));
        Assert.assertThat(ImmutableBitSet.of(0, 2, 4).indexOf(4), CoreMatchers.equalTo(2));
        Assert.assertThat(ImmutableBitSet.of(0, 2, 4).indexOf(5), CoreMatchers.equalTo((-1)));
        Assert.assertThat(ImmutableBitSet.of(0, 2, 4).indexOf((-1)), CoreMatchers.equalTo((-1)));
        Assert.assertThat(ImmutableBitSet.of(0, 2, 4).indexOf((-2)), CoreMatchers.equalTo((-1)));
        Assert.assertThat(ImmutableBitSet.of().indexOf((-1)), CoreMatchers.equalTo((-1)));
        Assert.assertThat(ImmutableBitSet.of().indexOf((-2)), CoreMatchers.equalTo((-1)));
        Assert.assertThat(ImmutableBitSet.of().indexOf(0), CoreMatchers.equalTo((-1)));
        Assert.assertThat(ImmutableBitSet.of().indexOf(1000), CoreMatchers.equalTo((-1)));
    }

    @Test
    public void testNth() {
        Assert.assertThat(ImmutableBitSet.of(0, 2, 4).nth(0), CoreMatchers.equalTo(0));
        Assert.assertThat(ImmutableBitSet.of(0, 2, 4).nth(1), CoreMatchers.equalTo(2));
        Assert.assertThat(ImmutableBitSet.of(0, 2, 4).nth(2), CoreMatchers.equalTo(4));
        Assert.assertThat(ImmutableBitSet.of(0, 2, 63).nth(2), CoreMatchers.equalTo(63));
        Assert.assertThat(ImmutableBitSet.of(0, 2, 64).nth(2), CoreMatchers.equalTo(64));
        Assert.assertThat(ImmutableBitSet.of(64).nth(0), CoreMatchers.equalTo(64));
        Assert.assertThat(ImmutableBitSet.of(64, 65).nth(0), CoreMatchers.equalTo(64));
        Assert.assertThat(ImmutableBitSet.of(64, 65).nth(1), CoreMatchers.equalTo(65));
        Assert.assertThat(ImmutableBitSet.of(64, 128).nth(1), CoreMatchers.equalTo(128));
        try {
            ImmutableBitSet.of().nth(0);
            Assert.fail("expected throw");
        } catch (IndexOutOfBoundsException e) {
            // ok
        }
        try {
            ImmutableBitSet.of().nth(1);
            Assert.fail("expected throw");
        } catch (IndexOutOfBoundsException e) {
            // ok
        }
        try {
            ImmutableBitSet.of(64).nth(1);
            Assert.fail("expected throw");
        } catch (IndexOutOfBoundsException e) {
            // ok
        }
        try {
            ImmutableBitSet.of(64).nth((-1));
            Assert.fail("expected throw");
        } catch (IndexOutOfBoundsException e) {
            // ok
        }
    }

    /**
     * Tests the method
     * {@link org.apache.calcite.util.BitSets#closure(java.util.SortedMap)}.
     */
    @Test
    public void testClosure() {
        final SortedMap<Integer, ImmutableBitSet> empty = new TreeMap<>();
        Assert.assertThat(ImmutableBitSet.closure(empty), CoreMatchers.equalTo(empty));
        // Currently you need an entry for each position, otherwise you get an NPE.
        // We should fix that.
        final SortedMap<Integer, ImmutableBitSet> map = new TreeMap<>();
        map.put(0, ImmutableBitSet.of(3));
        map.put(1, ImmutableBitSet.of());
        map.put(2, ImmutableBitSet.of(7));
        map.put(3, ImmutableBitSet.of(4, 12));
        map.put(4, ImmutableBitSet.of());
        map.put(5, ImmutableBitSet.of());
        map.put(6, ImmutableBitSet.of());
        map.put(7, ImmutableBitSet.of());
        map.put(8, ImmutableBitSet.of());
        map.put(9, ImmutableBitSet.of());
        map.put(10, ImmutableBitSet.of());
        map.put(11, ImmutableBitSet.of());
        map.put(12, ImmutableBitSet.of());
        final String original = map.toString();
        final String expected = "{0={3, 4, 12}, 1={}, 2={7}, 3={3, 4, 12}, 4={4, 12}, 5={}, 6={}, 7={7}, 8={}, 9={}, 10={}, 11={}, 12={4, 12}}";
        Assert.assertThat(ImmutableBitSet.closure(map).toString(), CoreMatchers.equalTo(expected));
        Assert.assertThat("argument modified", map.toString(), CoreMatchers.equalTo(original));
        // Now a similar map with missing entries. Same result.
        final SortedMap<Integer, ImmutableBitSet> map2 = new TreeMap<>();
        map2.put(0, ImmutableBitSet.of(3));
        map2.put(2, ImmutableBitSet.of(7));
        map2.put(3, ImmutableBitSet.of(4, 12));
        map2.put(9, ImmutableBitSet.of());
        final String original2 = map2.toString();
        Assert.assertThat(ImmutableBitSet.closure(map2).toString(), CoreMatchers.equalTo(expected));
        Assert.assertThat("argument modified", map2.toString(), CoreMatchers.equalTo(original2));
    }

    @Test
    public void testPowerSet() {
        final ImmutableBitSet empty = ImmutableBitSet.of();
        Assert.assertThat(Iterables.size(empty.powerSet()), CoreMatchers.equalTo(1));
        Assert.assertThat(empty.powerSet().toString(), CoreMatchers.equalTo("[{}]"));
        final ImmutableBitSet single = ImmutableBitSet.of(2);
        Assert.assertThat(Iterables.size(single.powerSet()), CoreMatchers.equalTo(2));
        Assert.assertThat(single.powerSet().toString(), CoreMatchers.equalTo("[{}, {2}]"));
        final ImmutableBitSet two = ImmutableBitSet.of(2, 10);
        Assert.assertThat(Iterables.size(two.powerSet()), CoreMatchers.equalTo(4));
        Assert.assertThat(two.powerSet().toString(), CoreMatchers.equalTo("[{}, {10}, {2}, {2, 10}]"));
        final ImmutableBitSet seventeen = ImmutableBitSet.range(3, 20);
        Assert.assertThat(Iterables.size(seventeen.powerSet()), CoreMatchers.equalTo(131072));
    }

    @Test
    public void testCreateLongs() {
        Assert.assertThat(ImmutableBitSet.valueOf(0L), CoreMatchers.equalTo(ImmutableBitSet.of()));
        Assert.assertThat(ImmutableBitSet.valueOf(10L), CoreMatchers.equalTo(ImmutableBitSet.of(1, 3)));
        Assert.assertThat(ImmutableBitSet.valueOf(10L, 0, 0), CoreMatchers.equalTo(ImmutableBitSet.of(1, 3)));
        Assert.assertThat(ImmutableBitSet.valueOf(0, 0, 10L, 0), CoreMatchers.equalTo(ImmutableBitSet.of(129, 131)));
    }

    @Test
    public void testCreateLongBuffer() {
        Assert.assertThat(ImmutableBitSet.valueOf(LongBuffer.wrap(new long[]{  })), CoreMatchers.equalTo(ImmutableBitSet.of()));
        Assert.assertThat(ImmutableBitSet.valueOf(LongBuffer.wrap(new long[]{ 10L })), CoreMatchers.equalTo(ImmutableBitSet.of(1, 3)));
        Assert.assertThat(ImmutableBitSet.valueOf(LongBuffer.wrap(new long[]{ 0, 0, 10L, 0 })), CoreMatchers.equalTo(ImmutableBitSet.of(129, 131)));
    }

    @Test
    public void testToLongArray() {
        final ImmutableBitSet bitSet = ImmutableBitSet.of(29, 4, 1969);
        Assert.assertThat(ImmutableBitSet.valueOf(bitSet.toLongArray()), CoreMatchers.equalTo(bitSet));
        Assert.assertThat(ImmutableBitSet.valueOf(LongBuffer.wrap(bitSet.toLongArray())), CoreMatchers.equalTo(bitSet));
    }

    @Test
    public void testSet() {
        final ImmutableBitSet bitSet = ImmutableBitSet.of(29, 4, 1969);
        final ImmutableBitSet bitSet2 = ImmutableBitSet.of(29, 4, 1969, 30);
        Assert.assertThat(bitSet.set(30), CoreMatchers.equalTo(bitSet2));
        Assert.assertThat(bitSet.set(30).set(30), CoreMatchers.equalTo(bitSet2));
        Assert.assertThat(bitSet.set(29), CoreMatchers.equalTo(bitSet));
        Assert.assertThat(bitSet.setIf(30, false), CoreMatchers.equalTo(bitSet));
        Assert.assertThat(bitSet.setIf(30, true), CoreMatchers.equalTo(bitSet2));
    }

    @Test
    public void testClear() {
        final ImmutableBitSet bitSet = ImmutableBitSet.of(29, 4, 1969);
        final ImmutableBitSet bitSet2 = ImmutableBitSet.of(4, 1969);
        Assert.assertThat(bitSet.clear(29), CoreMatchers.equalTo(bitSet2));
        Assert.assertThat(bitSet.clear(29).clear(29), CoreMatchers.equalTo(bitSet2));
        Assert.assertThat(bitSet.clear(29).clear(4).clear(29).clear(1969), CoreMatchers.equalTo(ImmutableBitSet.of()));
        Assert.assertThat(bitSet.clearIf(29, false), CoreMatchers.equalTo(bitSet));
        Assert.assertThat(bitSet.clearIf(29, true), CoreMatchers.equalTo(bitSet2));
    }

    @Test
    public void testSet2() {
        final ImmutableBitSet bitSet = ImmutableBitSet.of(29, 4, 1969);
        final ImmutableBitSet bitSet2 = ImmutableBitSet.of(29, 4, 1969, 30);
        Assert.assertThat(bitSet.set(30, false), CoreMatchers.sameInstance(bitSet));
        Assert.assertThat(bitSet.set(30, true), CoreMatchers.equalTo(bitSet2));
        Assert.assertThat(bitSet.set(29, true), CoreMatchers.sameInstance(bitSet));
    }

    @Test
    public void testShift() {
        final ImmutableBitSet bitSet = ImmutableBitSet.of(29, 4, 1969);
        Assert.assertThat(bitSet.shift(0), CoreMatchers.is(bitSet));
        Assert.assertThat(bitSet.shift(1), CoreMatchers.is(ImmutableBitSet.of(30, 5, 1970)));
        Assert.assertThat(bitSet.shift((-4)), CoreMatchers.is(ImmutableBitSet.of(25, 0, 1965)));
        try {
            final ImmutableBitSet x = bitSet.shift((-5));
            Assert.fail(("Expected error, got " + x));
        } catch (ArrayIndexOutOfBoundsException e) {
            Assert.assertThat(e.getMessage(), CoreMatchers.anyOf(CoreMatchers.is("-1"), CoreMatchers.is("Index -1 out of bounds for length 0")));
        }
        final ImmutableBitSet empty = ImmutableBitSet.of();
        Assert.assertThat(empty.shift((-100)), CoreMatchers.is(empty));
    }

    @Test
    public void testGet2() {
        final ImmutableBitSet bitSet = ImmutableBitSet.of(29, 4, 1969);
        Assert.assertThat(bitSet.get(0, 8), CoreMatchers.is(ImmutableBitSet.of(4)));
        Assert.assertThat(bitSet.get(0, 5), CoreMatchers.is(ImmutableBitSet.of(4)));
        Assert.assertThat(bitSet.get(0, 4), CoreMatchers.is(ImmutableBitSet.of()));
        Assert.assertThat(bitSet.get(4, 4), CoreMatchers.is(ImmutableBitSet.of()));
        Assert.assertThat(bitSet.get(5, 5), CoreMatchers.is(ImmutableBitSet.of()));
        Assert.assertThat(bitSet.get(4, 5), CoreMatchers.is(ImmutableBitSet.of(4)));
        Assert.assertThat(bitSet.get(4, 1000), CoreMatchers.is(ImmutableBitSet.of(4, 29)));
        Assert.assertThat(bitSet.get(4, 32), CoreMatchers.is(ImmutableBitSet.of(4, 29)));
        Assert.assertThat(bitSet.get(2000, 10000), CoreMatchers.is(ImmutableBitSet.of()));
        Assert.assertThat(bitSet.get(1000, 10000), CoreMatchers.is(ImmutableBitSet.of(1969)));
        Assert.assertThat(bitSet.get(5, 10000), CoreMatchers.is(ImmutableBitSet.of(29, 1969)));
        Assert.assertThat(bitSet.get(65, 10000), CoreMatchers.is(ImmutableBitSet.of(1969)));
        final ImmutableBitSet emptyBitSet = ImmutableBitSet.of();
        Assert.assertThat(emptyBitSet.get(0, 4), CoreMatchers.is(ImmutableBitSet.of()));
        Assert.assertThat(emptyBitSet.get(0, 0), CoreMatchers.is(ImmutableBitSet.of()));
        Assert.assertThat(emptyBitSet.get(0, 10000), CoreMatchers.is(ImmutableBitSet.of()));
        Assert.assertThat(emptyBitSet.get(7, 10000), CoreMatchers.is(ImmutableBitSet.of()));
        Assert.assertThat(emptyBitSet.get(73, 10000), CoreMatchers.is(ImmutableBitSet.of()));
    }
}

/**
 * End ImmutableBitSetTest.java
 */
