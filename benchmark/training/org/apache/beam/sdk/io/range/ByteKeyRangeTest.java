/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.beam.sdk.io.range;


import ByteKey.EMPTY;
import ByteKeyRange.ALL_KEYS;
import java.util.Arrays;
import java.util.List;
import org.apache.beam.vendor.guava.v20_0.com.google.common.collect.ImmutableList;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static ByteKeyRange.ALL_KEYS;


/**
 * Tests for {@link ByteKeyRange}.
 */
@RunWith(JUnit4.class)
public class ByteKeyRangeTest {
    // A set of ranges for testing.
    private static final ByteKeyRange RANGE_1_10 = ByteKeyRange.of(ByteKey.of(1), ByteKey.of(10));

    private static final ByteKeyRange RANGE_5_10 = ByteKeyRange.of(ByteKey.of(5), ByteKey.of(10));

    private static final ByteKeyRange RANGE_5_50 = ByteKeyRange.of(ByteKey.of(5), ByteKey.of(50));

    private static final ByteKeyRange RANGE_10_50 = ByteKeyRange.of(ByteKey.of(10), ByteKey.of(50));

    private static final ByteKeyRange UP_TO_1 = ByteKeyRange.of(EMPTY, ByteKey.of(1));

    private static final ByteKeyRange UP_TO_5 = ByteKeyRange.of(EMPTY, ByteKey.of(5));

    private static final ByteKeyRange UP_TO_10 = ByteKeyRange.of(EMPTY, ByteKey.of(10));

    private static final ByteKeyRange UP_TO_50 = ByteKeyRange.of(EMPTY, ByteKey.of(50));

    private static final ByteKeyRange AFTER_1 = ByteKeyRange.of(ByteKey.of(1), EMPTY);

    private static final ByteKeyRange AFTER_5 = ByteKeyRange.of(ByteKey.of(5), EMPTY);

    private static final ByteKeyRange AFTER_10 = ByteKeyRange.of(ByteKey.of(10), EMPTY);

    private static final ByteKeyRange[] TEST_RANGES = new ByteKeyRange[]{ ALL_KEYS, ByteKeyRangeTest.RANGE_1_10, ByteKeyRangeTest.RANGE_5_10, ByteKeyRangeTest.RANGE_5_50, ByteKeyRangeTest.RANGE_10_50, ByteKeyRangeTest.UP_TO_1, ByteKeyRangeTest.UP_TO_5, ByteKeyRangeTest.UP_TO_10, ByteKeyRangeTest.UP_TO_50, ByteKeyRangeTest.AFTER_1, ByteKeyRangeTest.AFTER_5, ByteKeyRangeTest.AFTER_10 };

    static final ByteKey[] RANGE_TEST_KEYS = ImmutableList.<ByteKey>builder().addAll(Arrays.asList(ByteKeyTest.TEST_KEYS)).add(EMPTY).build().toArray(ByteKeyTest.TEST_KEYS);

    /**
     * Tests of {@link ByteKeyRange#overlaps(ByteKeyRange)} with cases that should return true.
     */
    @Test
    public void testOverlappingRanges() {
        ByteKeyRangeTest.bidirectionalOverlap(ALL_KEYS, ALL_KEYS);
        ByteKeyRangeTest.bidirectionalOverlap(ALL_KEYS, ByteKeyRangeTest.RANGE_1_10);
        ByteKeyRangeTest.bidirectionalOverlap(ByteKeyRangeTest.UP_TO_1, ByteKeyRangeTest.UP_TO_1);
        ByteKeyRangeTest.bidirectionalOverlap(ByteKeyRangeTest.UP_TO_1, ByteKeyRangeTest.UP_TO_5);
        ByteKeyRangeTest.bidirectionalOverlap(ByteKeyRangeTest.UP_TO_50, ByteKeyRangeTest.AFTER_10);
        ByteKeyRangeTest.bidirectionalOverlap(ByteKeyRangeTest.UP_TO_50, ByteKeyRangeTest.RANGE_1_10);
        ByteKeyRangeTest.bidirectionalOverlap(ByteKeyRangeTest.UP_TO_10, ByteKeyRangeTest.UP_TO_50);
        ByteKeyRangeTest.bidirectionalOverlap(ByteKeyRangeTest.RANGE_1_10, ByteKeyRangeTest.RANGE_5_50);
        ByteKeyRangeTest.bidirectionalOverlap(ByteKeyRangeTest.AFTER_1, ByteKeyRangeTest.AFTER_5);
        ByteKeyRangeTest.bidirectionalOverlap(ByteKeyRangeTest.RANGE_5_10, ByteKeyRangeTest.RANGE_1_10);
        ByteKeyRangeTest.bidirectionalOverlap(ByteKeyRangeTest.RANGE_5_10, ByteKeyRangeTest.RANGE_5_50);
    }

    /**
     * Tests of {@link ByteKeyRange#overlaps(ByteKeyRange)} with cases that should return false.
     */
    @Test
    public void testNonOverlappingRanges() {
        ByteKeyRangeTest.bidirectionalNonOverlap(ByteKeyRangeTest.UP_TO_1, ByteKeyRangeTest.AFTER_1);
        ByteKeyRangeTest.bidirectionalNonOverlap(ByteKeyRangeTest.UP_TO_1, ByteKeyRangeTest.AFTER_5);
        ByteKeyRangeTest.bidirectionalNonOverlap(ByteKeyRangeTest.RANGE_5_10, ByteKeyRangeTest.RANGE_10_50);
    }

    /**
     * Tests for {@link ByteKeyRange#split(int)} with invalid inputs.
     */
    @Test
    public void testRejectsInvalidSplit() {
        try {
            Assert.fail(String.format("%s.split(0) should fail: %s", ByteKeyRangeTest.RANGE_1_10, ByteKeyRangeTest.RANGE_1_10.split(0)));
        } catch (IllegalArgumentException expected) {
            // pass
        }
        try {
            Assert.fail(String.format("%s.split(-3) should fail: %s", ByteKeyRangeTest.RANGE_1_10, ByteKeyRangeTest.RANGE_1_10.split((-3))));
        } catch (IllegalArgumentException expected) {
            // pass
        }
    }

    /**
     * Tests for {@link ByteKeyRange#split(int)} with weird inputs.
     */
    @Test
    public void testSplitSpecialInputs() {
        // Range split by 1 returns list of its keys.
        Assert.assertEquals("Split 1 should return input", ImmutableList.of(ByteKeyRangeTest.RANGE_1_10.getStartKey(), ByteKeyRangeTest.RANGE_1_10.getEndKey()), ByteKeyRangeTest.RANGE_1_10.split(1));
        // Unsplittable range returns list of its keys.
        ByteKeyRange unsplittable = ByteKeyRange.of(ByteKey.of(), ByteKey.of(0, 0, 0, 0));
        Assert.assertEquals("Unsplittable should return input", ImmutableList.of(unsplittable.getStartKey(), unsplittable.getEndKey()), unsplittable.split(5));
    }

    /**
     * Tests for {@link ByteKeyRange#split(int)}.
     */
    @Test
    public void testSplitKeysCombinatorial() {
        List<Integer> sizes = ImmutableList.of(1, 2, 5, 10, 25, 32, 64);
        for (int i = 0; i < (ByteKeyRangeTest.RANGE_TEST_KEYS.length); ++i) {
            for (int j = i + 1; j < (ByteKeyRangeTest.RANGE_TEST_KEYS.length); ++j) {
                ByteKeyRange range = ByteKeyRange.of(ByteKeyRangeTest.RANGE_TEST_KEYS[i], ByteKeyRangeTest.RANGE_TEST_KEYS[j]);
                for (int s : sizes) {
                    List<ByteKey> splits = range.split(s);
                    ByteKeyRangeTest.ensureOrderedKeys(splits);
                    Assert.assertThat("At least two entries in splits", splits.size(), Matchers.greaterThanOrEqualTo(2));
                    Assert.assertEquals("First split equals start of range", splits.get(0), ByteKeyRangeTest.RANGE_TEST_KEYS[i]);
                    Assert.assertEquals("Last split equals end of range", splits.get(((splits.size()) - 1)), ByteKeyRangeTest.RANGE_TEST_KEYS[j]);
                }
            }
        }
    }

    /**
     * Manual tests for {@link ByteKeyRange#estimateFractionForKey}.
     */
    @Test
    public void testEstimateFractionForKey() {
        final double delta = 1.0E-7;
        /* 0x80 is halfway between [] and [] */
        Assert.assertEquals(0.5, ALL_KEYS.estimateFractionForKey(ByteKey.of(128)), delta);
        /* 0x80 is halfway between [00] and [] */
        ByteKeyRange after0 = ByteKeyRange.of(ByteKey.of(0), EMPTY);
        Assert.assertEquals(0.5, after0.estimateFractionForKey(ByteKey.of(128)), delta);
        /* 0x80 is halfway between [0000] and [] */
        ByteKeyRange after00 = ByteKeyRange.of(ByteKey.of(0, 0), EMPTY);
        Assert.assertEquals(0.5, after00.estimateFractionForKey(ByteKey.of(128)), delta);
        /* 0x7f is halfway between [] and [fe] */
        ByteKeyRange upToFE = ByteKeyRange.of(EMPTY, ByteKey.of(254));
        Assert.assertEquals(0.5, upToFE.estimateFractionForKey(ByteKey.of(127)), delta);
        /* 0x40 is one-quarter of the way between [] and [] */
        Assert.assertEquals(0.25, ALL_KEYS.estimateFractionForKey(ByteKey.of(64)), delta);
        /* 0x40 is one-half of the way between [] and [0x80] */
        ByteKeyRange upTo80 = ByteKeyRange.of(EMPTY, ByteKey.of(128));
        Assert.assertEquals(0.5, upTo80.estimateFractionForKey(ByteKey.of(64)), delta);
        /* 0x40 is one-half of the way between [0x30] and [0x50] */
        ByteKeyRange range30to50 = ByteKeyRange.of(ByteKey.of(48), ByteKey.of(80));
        Assert.assertEquals(0.5, range30to50.estimateFractionForKey(ByteKey.of(64)), delta);
        /* 0x40 is one-half of the way between [0x30, 0, 1] and [0x4f, 0xff, 0xff, 0, 0] */
        ByteKeyRange range31to4f = ByteKeyRange.of(ByteKey.of(48, 0, 1), ByteKey.of(79, 255, 255, 0, 0));
        Assert.assertEquals(0.5, range31to4f.estimateFractionForKey(ByteKey.of(64)), delta);
        /* Exact fractions from 0 to 47 for a prime range. */
        ByteKeyRange upTo47 = ByteKeyRange.of(EMPTY, ByteKey.of(47));
        for (int i = 0; i <= 47; ++i) {
            Assert.assertEquals(("i=" + i), (i / 47.0), upTo47.estimateFractionForKey(ByteKey.of(i)), delta);
        }
        /* Exact fractions from 0 to 83 for a prime range. */
        ByteKeyRange rangeFDECtoFDEC83 = ByteKeyRange.of(ByteKey.of(253, 236), ByteKey.of(253, 236, 83));
        for (int i = 0; i <= 83; ++i) {
            Assert.assertEquals(("i=" + i), (i / 83.0), rangeFDECtoFDEC83.estimateFractionForKey(ByteKey.of(253, 236, i)), delta);
        }
    }

    /**
     * Manual tests for {@link ByteKeyRange#interpolateKey}.
     */
    @Test
    public void testInterpolateKey() {
        /* 0x80 is halfway between [] and [] */
        ByteKeyRangeTest.assertEqualExceptPadding(ByteKey.of(128), ALL_KEYS.interpolateKey(0.5));
        /* 0x80 is halfway between [00] and [] */
        ByteKeyRange after0 = ByteKeyRange.of(ByteKey.of(0), EMPTY);
        ByteKeyRangeTest.assertEqualExceptPadding(ByteKey.of(128), after0.interpolateKey(0.5));
        /* 0x80 is halfway between [0000] and [] -- padding to longest key */
        ByteKeyRange after00 = ByteKeyRange.of(ByteKey.of(0, 0), EMPTY);
        ByteKeyRangeTest.assertEqualExceptPadding(ByteKey.of(128), after00.interpolateKey(0.5));
        /* 0x7f is halfway between [] and [fe] */
        ByteKeyRange upToFE = ByteKeyRange.of(EMPTY, ByteKey.of(254));
        ByteKeyRangeTest.assertEqualExceptPadding(ByteKey.of(127), upToFE.interpolateKey(0.5));
        /* 0x40 is one-quarter of the way between [] and [] */
        ByteKeyRangeTest.assertEqualExceptPadding(ByteKey.of(64), ALL_KEYS.interpolateKey(0.25));
        /* 0x40 is halfway between [] and [0x80] */
        ByteKeyRange upTo80 = ByteKeyRange.of(EMPTY, ByteKey.of(128));
        ByteKeyRangeTest.assertEqualExceptPadding(ByteKey.of(64), upTo80.interpolateKey(0.5));
        /* 0x40 is halfway between [0x30] and [0x50] */
        ByteKeyRange range30to50 = ByteKeyRange.of(ByteKey.of(48), ByteKey.of(80));
        ByteKeyRangeTest.assertEqualExceptPadding(ByteKey.of(64), range30to50.interpolateKey(0.5));
        /* 0x40 is halfway between [0x30, 0, 1] and [0x4f, 0xff, 0xff, 0, 0] */
        ByteKeyRange range31to4f = ByteKeyRange.of(ByteKey.of(48, 0, 1), ByteKey.of(79, 255, 255, 0, 0));
        ByteKeyRangeTest.assertEqualExceptPadding(ByteKey.of(64), range31to4f.interpolateKey(0.5));
    }

    /**
     * Tests that {@link ByteKeyRange#interpolateKey} does not return the empty key.
     */
    @Test
    public void testInterpolateKeyIsNotEmpty() {
        String fmt = "Interpolating %s at fraction 0.0 should not return the empty key";
        for (ByteKeyRange range : ByteKeyRangeTest.TEST_RANGES) {
            range = ALL_KEYS;
            Assert.assertFalse(String.format(fmt, range), range.interpolateKey(0.0).isEmpty());
        }
    }

    /**
     * Test {@link ByteKeyRange} getters.
     */
    @Test
    public void testKeyGetters() {
        // [1,)
        Assert.assertEquals(ByteKeyRangeTest.AFTER_1.getStartKey(), ByteKey.of(1));
        Assert.assertEquals(ByteKeyRangeTest.AFTER_1.getEndKey(), EMPTY);
        // [1, 10)
        Assert.assertEquals(ByteKeyRangeTest.RANGE_1_10.getStartKey(), ByteKey.of(1));
        Assert.assertEquals(ByteKeyRangeTest.RANGE_1_10.getEndKey(), ByteKey.of(10));
        // [, 10)
        Assert.assertEquals(ByteKeyRangeTest.UP_TO_10.getStartKey(), EMPTY);
        Assert.assertEquals(ByteKeyRangeTest.UP_TO_10.getEndKey(), ByteKey.of(10));
    }

    /**
     * Test {@link ByteKeyRange#toString}.
     */
    @Test
    public void testToString() {
        Assert.assertEquals("ByteKeyRange{startKey=[], endKey=[0a]}", ByteKeyRangeTest.UP_TO_10.toString());
    }

    /**
     * Test {@link ByteKeyRange#equals}.
     */
    @Test
    public void testEquals() {
        // Verify that the comparison gives the correct result for all values in both directions.
        for (int i = 0; i < (ByteKeyRangeTest.TEST_RANGES.length); ++i) {
            for (int j = 0; j < (ByteKeyRangeTest.TEST_RANGES.length); ++j) {
                ByteKeyRange left = ByteKeyRangeTest.TEST_RANGES[i];
                ByteKeyRange right = ByteKeyRangeTest.TEST_RANGES[j];
                boolean eq = left.equals(right);
                if (i == j) {
                    Assert.assertTrue(String.format("Expected that %s is equal to itself.", left), eq);
                    Assert.assertTrue(String.format("Expected that %s is equal to a copy of itself.", left), left.equals(ByteKeyRange.of(right.getStartKey(), right.getEndKey())));
                } else {
                    Assert.assertFalse(String.format("Expected that %s is not equal to %s", left, right), eq);
                }
            }
        }
    }

    /**
     * Test that {@link ByteKeyRange#of} rejects invalid ranges.
     */
    @Test
    public void testRejectsInvalidRanges() {
        ByteKey[] testKeys = ByteKeyTest.TEST_KEYS;
        for (int i = 0; i < (testKeys.length); ++i) {
            for (int j = i; j < (testKeys.length); ++j) {
                if (((testKeys[i].isEmpty()) || (testKeys[j].isEmpty())) || (testKeys[j].equals(testKeys[i]))) {
                    continue;// these are valid ranges.

                }
                try {
                    ByteKeyRange range = ByteKeyRange.of(testKeys[j], testKeys[i]);
                    Assert.fail(String.format("Expected failure constructing %s", range));
                } catch (IllegalArgumentException expected) {
                    // pass
                }
            }
        }
    }

    /**
     * Test {@link ByteKeyRange#hashCode}.
     */
    @Test
    public void testHashCode() {
        // Verify that the hashCode is equal when i==j, and usually not equal otherwise.
        int collisions = 0;
        for (int i = 0; i < (ByteKeyRangeTest.TEST_RANGES.length); ++i) {
            ByteKeyRange current = ByteKeyRangeTest.TEST_RANGES[i];
            int left = current.hashCode();
            int leftClone = ByteKeyRange.of(current.getStartKey(), current.getEndKey()).hashCode();
            Assert.assertEquals(String.format("Expected same hash code for %s and a copy of itself", current), left, leftClone);
            for (int j = i + 1; j < (ByteKeyRangeTest.TEST_RANGES.length); ++j) {
                int right = ByteKeyRangeTest.TEST_RANGES[j].hashCode();
                if (left == right) {
                    ++collisions;
                }
            }
        }
        int totalUnequalTests = ((ByteKeyRangeTest.TEST_RANGES.length) * ((ByteKeyRangeTest.TEST_RANGES.length) - 1)) / 2;
        Assert.assertThat("Too many hash collisions", collisions, Matchers.lessThan((totalUnequalTests / 2)));
    }
}

