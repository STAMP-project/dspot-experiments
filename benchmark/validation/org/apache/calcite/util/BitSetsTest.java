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


import java.util.Arrays;
import java.util.BitSet;
import java.util.Collections;
import java.util.SortedMap;
import java.util.TreeMap;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


/**
 * Unit test for {@link org.apache.calcite.util.BitSets}.
 */
public class BitSetsTest {
    /**
     * Tests the method
     * {@link org.apache.calcite.util.BitSets#toIter(java.util.BitSet)}.
     */
    @Test
    public void testToIterBitSet() {
        BitSet bitSet = new BitSet();
        assertToIterBitSet("", bitSet);
        bitSet.set(0);
        assertToIterBitSet("0", bitSet);
        bitSet.set(1);
        assertToIterBitSet("0, 1", bitSet);
        bitSet.clear();
        bitSet.set(10);
        assertToIterBitSet("10", bitSet);
    }

    /**
     * Tests the method
     * {@link org.apache.calcite.util.BitSets#toList(java.util.BitSet)}.
     */
    @Test
    public void testToListBitSet() {
        BitSet bitSet = new BitSet(10);
        Assert.assertEquals(BitSets.toList(bitSet), Collections.<Integer>emptyList());
        bitSet.set(5);
        Assert.assertEquals(BitSets.toList(bitSet), Arrays.asList(5));
        bitSet.set(3);
        Assert.assertEquals(BitSets.toList(bitSet), Arrays.asList(3, 5));
    }

    /**
     * Tests the method {@link org.apache.calcite.util.BitSets#of(int...)}.
     */
    @Test
    public void testBitSetOf() {
        Assert.assertEquals(BitSets.toList(BitSets.of(0, 4, 2)), Arrays.asList(0, 2, 4));
        Assert.assertEquals(BitSets.toList(BitSets.of()), Collections.<Integer>emptyList());
    }

    /**
     * Tests the method {@link org.apache.calcite.util.BitSets#range(int, int)}.
     */
    @Test
    public void testBitSetsRange() {
        Assert.assertEquals(BitSets.toList(BitSets.range(0, 4)), Arrays.asList(0, 1, 2, 3));
        Assert.assertEquals(BitSets.toList(BitSets.range(1, 4)), Arrays.asList(1, 2, 3));
        Assert.assertEquals(BitSets.toList(BitSets.range(2, 2)), Collections.<Integer>emptyList());
    }

    /**
     * Tests the method
     * {@link org.apache.calcite.util.BitSets#toArray(java.util.BitSet)}.
     */
    @Test
    public void testBitSetsToArray() {
        int[][] arrays = new int[][]{ new int[]{  }, new int[]{ 0 }, new int[]{ 0, 2 }, new int[]{ 1, 65 }, new int[]{ 100 } };
        for (int[] array : arrays) {
            Assert.assertThat(BitSets.toArray(BitSets.of(array)), CoreMatchers.equalTo(array));
        }
    }

    /**
     * Tests the method
     * {@link org.apache.calcite.util.BitSets#union(java.util.BitSet, java.util.BitSet...)}.
     */
    @Test
    public void testBitSetsUnion() {
        Assert.assertThat(BitSets.union(BitSets.of(1), BitSets.of(3)).toString(), CoreMatchers.equalTo("{1, 3}"));
        Assert.assertThat(BitSets.union(BitSets.of(1), BitSets.of(3, 100)).toString(), CoreMatchers.equalTo("{1, 3, 100}"));
        Assert.assertThat(BitSets.union(BitSets.of(1), BitSets.of(2), BitSets.of(), BitSets.of(3)).toString(), CoreMatchers.equalTo("{1, 2, 3}"));
    }

    /**
     * Tests the method
     * {@link org.apache.calcite.util.BitSets#contains(java.util.BitSet, java.util.BitSet)}.
     */
    @Test
    public void testBitSetsContains() {
        Assert.assertTrue(BitSets.contains(BitSets.range(0, 5), BitSets.range(2, 4)));
        Assert.assertTrue(BitSets.contains(BitSets.range(0, 5), BitSets.of(4)));
        Assert.assertFalse(BitSets.contains(BitSets.range(0, 5), BitSets.of(14)));
        Assert.assertFalse(BitSets.contains(BitSets.range(20, 25), BitSets.of(14)));
        final BitSet empty = BitSets.of();
        Assert.assertTrue(BitSets.contains(BitSets.range(20, 25), empty));
        Assert.assertTrue(BitSets.contains(empty, empty));
        Assert.assertFalse(BitSets.contains(empty, BitSets.of(0)));
        Assert.assertFalse(BitSets.contains(empty, BitSets.of(1)));
        Assert.assertFalse(BitSets.contains(empty, BitSets.of(1000)));
        Assert.assertTrue(BitSets.contains(BitSets.of(1, 4, 7), BitSets.of(1, 4, 7)));
    }

    /**
     * Tests the method
     * {@link org.apache.calcite.util.BitSets#of(ImmutableIntList)}.
     */
    @Test
    public void testBitSetOfImmutableIntList() {
        ImmutableIntList list = ImmutableIntList.of();
        Assert.assertThat(BitSets.of(list), CoreMatchers.equalTo(new BitSet()));
        list = ImmutableIntList.of(2, 70, 5, 0);
        Assert.assertThat(BitSets.of(list), CoreMatchers.equalTo(BitSets.of(0, 2, 5, 70)));
    }

    /**
     * Tests the method
     * {@link org.apache.calcite.util.BitSets#previousClearBit(java.util.BitSet, int)}.
     */
    @Test
    public void testPreviousClearBit() {
        Assert.assertThat(BitSets.previousClearBit(BitSets.of(), 10), CoreMatchers.equalTo(10));
        Assert.assertThat(BitSets.previousClearBit(BitSets.of(), 0), CoreMatchers.equalTo(0));
        Assert.assertThat(BitSets.previousClearBit(BitSets.of(), (-1)), CoreMatchers.equalTo((-1)));
        try {
            final int actual = BitSets.previousClearBit(BitSets.of(), (-2));
            Assert.fail(("expected exception, got " + actual));
        } catch (IndexOutOfBoundsException e) {
            // ok
        }
        Assert.assertThat(BitSets.previousClearBit(BitSets.of(0, 1, 3, 4), 4), CoreMatchers.equalTo(2));
        Assert.assertThat(BitSets.previousClearBit(BitSets.of(0, 1, 3, 4), 3), CoreMatchers.equalTo(2));
        Assert.assertThat(BitSets.previousClearBit(BitSets.of(0, 1, 3, 4), 2), CoreMatchers.equalTo(2));
        Assert.assertThat(BitSets.previousClearBit(BitSets.of(0, 1, 3, 4), 1), CoreMatchers.equalTo((-1)));
        Assert.assertThat(BitSets.previousClearBit(BitSets.of(1, 3, 4), 1), CoreMatchers.equalTo(0));
    }

    /**
     * Tests the method {@link BitSets#closure(java.util.SortedMap)}
     */
    @Test
    public void testClosure() {
        final SortedMap<Integer, BitSet> empty = new TreeMap<>();
        Assert.assertThat(BitSets.closure(empty), CoreMatchers.equalTo(empty));
        // Map with an an entry for each position.
        final SortedMap<Integer, BitSet> map = new TreeMap<>();
        map.put(0, BitSets.of(3));
        map.put(1, BitSets.of());
        map.put(2, BitSets.of(7));
        map.put(3, BitSets.of(4, 12));
        map.put(4, BitSets.of());
        map.put(5, BitSets.of());
        map.put(6, BitSets.of());
        map.put(7, BitSets.of());
        map.put(8, BitSets.of());
        map.put(9, BitSets.of());
        map.put(10, BitSets.of());
        map.put(11, BitSets.of());
        map.put(12, BitSets.of());
        final String original = map.toString();
        final String expected = "{0={3, 4, 12}, 1={}, 2={7}, 3={3, 4, 12}, 4={4, 12}, 5={}, 6={}, 7={7}, 8={}, 9={}, 10={}, 11={}, 12={4, 12}}";
        Assert.assertThat(BitSets.closure(map).toString(), CoreMatchers.equalTo(expected));
        Assert.assertThat("argument modified", map.toString(), CoreMatchers.equalTo(original));
        // Now a similar map with missing entries. Same result.
        final SortedMap<Integer, BitSet> map2 = new TreeMap<>();
        map2.put(0, BitSets.of(3));
        map2.put(2, BitSets.of(7));
        map2.put(3, BitSets.of(4, 12));
        map2.put(9, BitSets.of());
        final String original2 = map2.toString();
        Assert.assertThat(BitSets.closure(map2).toString(), CoreMatchers.equalTo(expected));
        Assert.assertThat("argument modified", map2.toString(), CoreMatchers.equalTo(original2));
    }
}

/**
 * End BitSetsTest.java
 */
