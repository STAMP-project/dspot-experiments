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


import CalciteSystemProperty.TEST_SLOW;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.function.Function;
import org.apache.calcite.test.SlowTests;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Test;
import org.junit.experimental.categories.Category;


/**
 * Unit test for {@link PartiallyOrderedSet}.
 */
@Category(SlowTests.class)
public class PartiallyOrderedSetTest {
    private static final boolean DEBUG = false;

    // 100, 250, 1000, 3000 are reasonable
    private static final int SCALE = (TEST_SLOW.value()) ? 250 : 50;

    final long seed = new Random().nextLong();

    final Random random = new Random(seed);

    static final PartiallyOrderedSet.Ordering<String> STRING_SUBSET_ORDERING = ( e1, e2) -> {
        // e1 < e2 if every char in e1 is also in e2
        for (int i = 0; i < (e1.length()); i++) {
            if ((e2.indexOf(e1.charAt(i))) < 0) {
                return false;
            }
        }
        return true;
    };

    @Test
    public void testPoset() {
        String empty = "''";
        String abcd = "'abcd'";
        final PartiallyOrderedSet<String> poset = new PartiallyOrderedSet(STRING_SUBSET_ORDERING);
        Assert.assertEquals(0, poset.size());
        final StringBuilder buf = new StringBuilder();
        poset.out(buf);
        TestUtil.assertEqualsVerbose(("PartiallyOrderedSet size: 0 elements: {\n" + "}"), buf.toString());
        poset.add("a");
        printValidate(poset);
        poset.add("b");
        printValidate(poset);
        poset.clear();
        Assert.assertEquals(0, poset.size());
        poset.add(empty);
        printValidate(poset);
        poset.add(abcd);
        printValidate(poset);
        Assert.assertEquals(2, poset.size());
        Assert.assertEquals("['abcd']", poset.getNonChildren().toString());
        Assert.assertEquals("['']", poset.getNonParents().toString());
        final String ab = "'ab'";
        poset.add(ab);
        printValidate(poset);
        Assert.assertEquals(3, poset.size());
        Assert.assertEquals("[]", poset.getChildren(empty).toString());
        Assert.assertEquals("['ab']", poset.getParents(empty).toString());
        Assert.assertEquals("['ab']", poset.getChildren(abcd).toString());
        Assert.assertEquals("[]", poset.getParents(abcd).toString());
        Assert.assertEquals("['']", poset.getChildren(ab).toString());
        Assert.assertEquals("['abcd']", poset.getParents(ab).toString());
        // "bcd" is child of "abcd" and parent of ""
        final String bcd = "'bcd'";
        Assert.assertEquals("['abcd']", poset.getParents(bcd, true).toString());
        Assert.assertThat(poset.getParents(bcd, false), CoreMatchers.nullValue());
        Assert.assertThat(poset.getParents(bcd), CoreMatchers.nullValue());
        Assert.assertEquals("['']", poset.getChildren(bcd, true).toString());
        Assert.assertThat(poset.getChildren(bcd, false), CoreMatchers.nullValue());
        Assert.assertThat(poset.getChildren(bcd), CoreMatchers.nullValue());
        poset.add(bcd);
        printValidate(poset);
        Assert.assertTrue(poset.isValid(false));
        Assert.assertEquals("['']", poset.getChildren(bcd).toString());
        Assert.assertEquals("['abcd']", poset.getParents(bcd).toString());
        Assert.assertEquals("['ab', 'bcd']", poset.getChildren(abcd).toString());
        buf.setLength(0);
        poset.out(buf);
        TestUtil.assertEqualsVerbose(("PartiallyOrderedSet size: 4 elements: {\n" + (((("  \'abcd\' parents: [] children: [\'ab\', \'bcd\']\n" + "  \'ab\' parents: [\'abcd\'] children: [\'\']\n") + "  \'bcd\' parents: [\'abcd\'] children: [\'\']\n") + "  \'\' parents: [\'ab\', \'bcd\'] children: []\n") + "}")), buf.toString());
        final String b = "'b'";
        // ancestors of an element not in the set
        PartiallyOrderedSetTest.assertEqualsList("['ab', 'abcd', 'bcd']", poset.getAncestors(b));
        poset.add(b);
        printValidate(poset);
        Assert.assertEquals("['abcd']", poset.getNonChildren().toString());
        Assert.assertEquals("['']", poset.getNonParents().toString());
        Assert.assertEquals("['']", poset.getChildren(b).toString());
        PartiallyOrderedSetTest.assertEqualsList("['ab', 'bcd']", poset.getParents(b));
        Assert.assertEquals("['']", poset.getChildren(b).toString());
        Assert.assertEquals("['ab', 'bcd']", poset.getChildren(abcd).toString());
        Assert.assertEquals("['b']", poset.getChildren(bcd).toString());
        Assert.assertEquals("['b']", poset.getChildren(ab).toString());
        PartiallyOrderedSetTest.assertEqualsList("['ab', 'abcd', 'bcd']", poset.getAncestors(b));
        // descendants and ancestors of an element with no descendants
        Assert.assertEquals("[]", poset.getDescendants(empty).toString());
        PartiallyOrderedSetTest.assertEqualsList("['ab', 'abcd', 'b', 'bcd']", poset.getAncestors(empty));
        // some more ancestors of missing elements
        PartiallyOrderedSetTest.assertEqualsList("['abcd']", poset.getAncestors("'ac'"));
        PartiallyOrderedSetTest.assertEqualsList("[]", poset.getAncestors("'z'"));
        PartiallyOrderedSetTest.assertEqualsList("['ab', 'abcd']", poset.getAncestors("'a'"));
    }

    @Test
    public void testPosetTricky() {
        final PartiallyOrderedSet<String> poset = new PartiallyOrderedSet(STRING_SUBSET_ORDERING);
        // A tricky little poset with 4 elements:
        // {a <= ab and ac, b < ab, ab, ac}
        poset.clear();
        poset.add("'a'");
        printValidate(poset);
        poset.add("'b'");
        printValidate(poset);
        poset.add("'ac'");
        printValidate(poset);
        poset.add("'ab'");
        printValidate(poset);
    }

    @Test
    public void testPosetBits() {
        final PartiallyOrderedSet<Integer> poset = new PartiallyOrderedSet(PartiallyOrderedSetTest::isBitSuperset);
        poset.add(2112);// {6, 11} i.e. 64 + 2048

        poset.add(2240);// {6, 7, 11} i.e. 64 + 128 + 2048

        poset.add(2496);// {6, 7, 8, 11} i.e. 64 + 128 + 256 + 2048

        printValidate(poset);
        poset.remove(2240);
        printValidate(poset);
        poset.add(2240);// {6, 7, 11} i.e. 64 + 128 + 2048

        printValidate(poset);
    }

    @Test
    public void testPosetBitsLarge() {
        Assume.assumeTrue("it takes 80 seconds, and the computations are exactly the same every time", TEST_SLOW.value());
        final PartiallyOrderedSet<Integer> poset = new PartiallyOrderedSet(PartiallyOrderedSetTest::isBitSuperset);
        checkPosetBitsLarge(poset, 30000, 2921, 164782);
    }

    @Test
    public void testPosetBitsLarge2() {
        Assume.assumeTrue("too slow to run every day", TEST_SLOW.value());
        final int n = 30000;
        final PartiallyOrderedSet<Integer> poset = new PartiallyOrderedSet(PartiallyOrderedSetTest::isBitSuperset, ((Function<Integer, Iterable<Integer>>) (( i) -> {
            int r = Objects.requireNonNull(i);// bits not yet cleared

            final List<Integer> list = new ArrayList<>();
            for (int z = 1; r != 0; z <<= 1) {
                if ((i & z) != 0) {
                    list.add((i ^ z));
                    r ^= z;
                }
            }
            return list;
        })), ( i) -> {
            Objects.requireNonNull(i);
            final List<Integer> list = new ArrayList<>();
            for (int z = 1; z <= n; z <<= 1) {
                if ((i & z) == 0) {
                    list.add((i | z));
                }
            }
            return list;
        });
        checkPosetBitsLarge(poset, n, 2921, 11961);
    }

    @Test
    public void testPosetBitsRemoveParent() {
        final PartiallyOrderedSet<Integer> poset = new PartiallyOrderedSet(PartiallyOrderedSetTest::isBitSuperset);
        poset.add(66);// {bit 2, bit 6}

        poset.add(68);// {bit 3, bit 6}

        poset.add(72);// {bit 4, bit 6}

        poset.add(64);// {bit 6}

        printValidate(poset);
        poset.remove(64);// {bit 6}

        printValidate(poset);
    }

    @Test
    public void testDivisorPoset() {
        PartiallyOrderedSet<Integer> integers = new PartiallyOrderedSet(PartiallyOrderedSetTest::isDivisor, PartiallyOrderedSetTest.range(1, 1000));
        Assert.assertEquals("[1, 2, 3, 4, 5, 6, 8, 10, 12, 15, 20, 24, 30, 40, 60]", new java.util.TreeSet(integers.getDescendants(120)).toString());
        Assert.assertEquals("[240, 360, 480, 600, 720, 840, 960]", new java.util.TreeSet(integers.getAncestors(120)).toString());
        Assert.assertTrue(integers.getDescendants(1).isEmpty());
        Assert.assertEquals(998, integers.getAncestors(1).size());
        Assert.assertTrue(integers.isValid(true));
    }

    @Test
    public void testDivisorSeries() {
        checkPoset(PartiallyOrderedSetTest::isDivisor, PartiallyOrderedSetTest.DEBUG, PartiallyOrderedSetTest.range(1, ((PartiallyOrderedSetTest.SCALE) * 3)), false);
    }

    @Test
    public void testDivisorRandom() {
        boolean ok = false;
        try {
            checkPoset(PartiallyOrderedSetTest::isDivisor, PartiallyOrderedSetTest.DEBUG, PartiallyOrderedSetTest.random(random, PartiallyOrderedSetTest.SCALE, ((PartiallyOrderedSetTest.SCALE) * 3)), false);
            ok = true;
        } finally {
            if (!ok) {
                System.out.println(("Random seed: " + (seed)));
            }
        }
    }

    @Test
    public void testDivisorRandomWithRemoval() {
        boolean ok = false;
        try {
            checkPoset(PartiallyOrderedSetTest::isDivisor, PartiallyOrderedSetTest.DEBUG, PartiallyOrderedSetTest.random(random, PartiallyOrderedSetTest.SCALE, ((PartiallyOrderedSetTest.SCALE) * 3)), true);
            ok = true;
        } finally {
            if (!ok) {
                System.out.println(("Random seed: " + (seed)));
            }
        }
    }

    @Test
    public void testDivisorInverseSeries() {
        checkPoset(PartiallyOrderedSetTest::isDivisorInverse, PartiallyOrderedSetTest.DEBUG, PartiallyOrderedSetTest.range(1, ((PartiallyOrderedSetTest.SCALE) * 3)), false);
    }

    @Test
    public void testDivisorInverseRandom() {
        boolean ok = false;
        try {
            checkPoset(PartiallyOrderedSetTest::isDivisorInverse, PartiallyOrderedSetTest.DEBUG, PartiallyOrderedSetTest.random(random, PartiallyOrderedSetTest.SCALE, ((PartiallyOrderedSetTest.SCALE) * 3)), false);
            ok = true;
        } finally {
            if (!ok) {
                System.out.println(("Random seed: " + (seed)));
            }
        }
    }

    @Test
    public void testDivisorInverseRandomWithRemoval() {
        boolean ok = false;
        try {
            checkPoset(PartiallyOrderedSetTest::isDivisorInverse, PartiallyOrderedSetTest.DEBUG, PartiallyOrderedSetTest.random(random, PartiallyOrderedSetTest.SCALE, ((PartiallyOrderedSetTest.SCALE) * 3)), true);
            ok = true;
        } finally {
            if (!ok) {
                System.out.println(("Random seed: " + (seed)));
            }
        }
    }

    @Test
    public void testSubsetSeries() {
        checkPoset(PartiallyOrderedSetTest::isBitSubset, PartiallyOrderedSetTest.DEBUG, PartiallyOrderedSetTest.range(1, ((PartiallyOrderedSetTest.SCALE) / 2)), false);
    }

    @Test
    public void testSubsetRandom() {
        boolean ok = false;
        try {
            checkPoset(PartiallyOrderedSetTest::isBitSubset, PartiallyOrderedSetTest.DEBUG, PartiallyOrderedSetTest.random(random, ((PartiallyOrderedSetTest.SCALE) / 4), PartiallyOrderedSetTest.SCALE), false);
            ok = true;
        } finally {
            if (!ok) {
                System.out.println(("Random seed: " + (seed)));
            }
        }
    }
}

/**
 * End PartiallyOrderedSetTest.java
 */
