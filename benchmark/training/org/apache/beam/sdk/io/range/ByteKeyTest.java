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
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static ByteKey.EMPTY;


/**
 * Tests of {@link ByteKey}.
 */
@RunWith(JUnit4.class)
public class ByteKeyTest {
    /* A big list of byte[] keys, in ascending sorted order. */
    static final ByteKey[] TEST_KEYS = new ByteKey[]{ EMPTY, ByteKey.of(0), ByteKey.of(0, 1), ByteKey.of(0, 1, 1), ByteKey.of(0, 1, 2), ByteKey.of(0, 1, 2, 254), ByteKey.of(0, 1, 3, 254), ByteKey.of(0, 254, 254, 254), ByteKey.of(0, 254, 254, 255), ByteKey.of(0, 254, 255, 0), ByteKey.of(0, 255, 255, 0), ByteKey.of(0, 255, 255, 1), ByteKey.of(0, 255, 255, 254), ByteKey.of(0, 255, 255, 255), ByteKey.of(1), ByteKey.of(1, 2), ByteKey.of(1, 2, 3), ByteKey.of(3), ByteKey.of(221), ByteKey.of(254), ByteKey.of(254, 254), ByteKey.of(254, 255), ByteKey.of(255), ByteKey.of(255, 0), ByteKey.of(255, 254), ByteKey.of(255, 255), ByteKey.of(255, 255, 255), ByteKey.of(255, 255, 255, 255) };

    /**
     * Tests {@link ByteKey#compareTo(ByteKey)} using exhaustive testing within a large sorted list of
     * keys.
     */
    @Test
    public void testCompareToExhaustive() {
        // Verify that the comparison gives the correct result for all values in both directions.
        for (int i = 0; i < (ByteKeyTest.TEST_KEYS.length); ++i) {
            for (int j = 0; j < (ByteKeyTest.TEST_KEYS.length); ++j) {
                ByteKey left = ByteKeyTest.TEST_KEYS[i];
                ByteKey right = ByteKeyTest.TEST_KEYS[j];
                int cmp = left.compareTo(right);
                if ((i < j) && (!(cmp < 0))) {
                    Assert.fail(String.format("Expected that cmp(%s, %s) < 0, got %d [i=%d, j=%d]", left, right, cmp, i, j));
                } else
                    if ((i == j) && (!(cmp == 0))) {
                        Assert.fail(String.format("Expected that cmp(%s, %s) == 0, got %d [i=%d, j=%d]", left, right, cmp, i, j));
                    } else
                        if ((i > j) && (!(cmp > 0))) {
                            Assert.fail(String.format("Expected that cmp(%s, %s) > 0, got %d [i=%d, j=%d]", left, right, cmp, i, j));
                        }


            }
        }
    }

    /**
     * Tests {@link ByteKey#equals}.
     */
    @Test
    public void testEquals() {
        // Verify that the comparison gives the correct result for all values in both directions.
        for (int i = 0; i < (ByteKeyTest.TEST_KEYS.length); ++i) {
            for (int j = 0; j < (ByteKeyTest.TEST_KEYS.length); ++j) {
                ByteKey left = ByteKeyTest.TEST_KEYS[i];
                ByteKey right = ByteKeyTest.TEST_KEYS[j];
                boolean eq = left.equals(right);
                if (i == j) {
                    Assert.assertTrue(String.format("Expected that %s is equal to itself.", left), eq);
                    Assert.assertTrue(String.format("Expected that %s is equal to a copy of itself.", left), left.equals(ByteKey.copyFrom(right.getValue())));
                } else {
                    Assert.assertFalse(String.format("Expected that %s is not equal to %s", left, right), eq);
                }
            }
        }
    }

    /**
     * Tests {@link ByteKey#hashCode}.
     */
    @Test
    public void testHashCode() {
        // Verify that the hashCode is equal when i==j, and usually not equal otherwise.
        int collisions = 0;
        for (int i = 0; i < (ByteKeyTest.TEST_KEYS.length); ++i) {
            int left = ByteKeyTest.TEST_KEYS[i].hashCode();
            int leftClone = ByteKey.copyFrom(ByteKeyTest.TEST_KEYS[i].getValue()).hashCode();
            Assert.assertEquals(String.format("Expected same hash code for %s and a copy of itself", ByteKeyTest.TEST_KEYS[i]), left, leftClone);
            for (int j = i + 1; j < (ByteKeyTest.TEST_KEYS.length); ++j) {
                int right = ByteKeyTest.TEST_KEYS[j].hashCode();
                if (left == right) {
                    ++collisions;
                }
            }
        }
        int totalUnequalTests = ((ByteKeyTest.TEST_KEYS.length) * ((ByteKeyTest.TEST_KEYS.length) - 1)) / 2;
        Assert.assertThat("Too many hash collisions", collisions, Matchers.lessThan((totalUnequalTests / 2)));
    }

    /**
     * Tests {@link ByteKey#toString}.
     */
    @Test
    public void testToString() {
        Assert.assertEquals("[]", EMPTY.toString());
        Assert.assertEquals("[00]", ByteKey.of(0).toString());
        Assert.assertEquals("[0000]", ByteKey.of(0, 0).toString());
        Assert.assertEquals("[0123456789abcdef]", ByteKey.of(1, 35, 69, 103, 137, 171, 205, 239).toString());
    }

    /**
     * Tests {@link ByteKey#isEmpty}.
     */
    @Test
    public void testIsEmpty() {
        Assert.assertTrue("[] is empty", EMPTY.isEmpty());
        Assert.assertFalse("[00]", ByteKey.of(0).isEmpty());
    }

    /**
     * Tests {@link ByteKey#getBytes}.
     */
    @Test
    public void testGetBytes() {
        Assert.assertArrayEquals("[] equal after getBytes", new byte[]{  }, EMPTY.getBytes());
        Assert.assertArrayEquals("[00] equal after getBytes", new byte[]{ 0 }, ByteKey.of(0).getBytes());
    }
}

