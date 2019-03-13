/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.commons.lang3;


import java.util.Comparator;
import java.util.function.ToIntFunction;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;


/**
 * <p>
 * Tests the methods in the {@link org.apache.commons.lang3.Range} class.
 * </p>
 */
@SuppressWarnings("boxing")
public class RangeTest {
    private Range<Byte> byteRange;

    private Range<Byte> byteRange2;

    private Range<Byte> byteRange3;

    private Range<Integer> intRange;

    private Range<Long> longRange;

    private Range<Float> floatRange;

    private Range<Double> doubleRange;

    // -----------------------------------------------------------------------
    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Test
    public void testComparableConstructors() {
        final Comparable c = ( other) -> 1;
        final Range r1 = Range.is(c);
        final Range r2 = Range.between(c, c);
        Assertions.assertTrue(r1.isNaturalOrdering());
        Assertions.assertTrue(r2.isNaturalOrdering());
    }

    @Test
    public void testIsWithCompare() {
        // all integers are equal
        final Comparator<Integer> c = ( o1, o2) -> 0;
        Range<Integer> ri = Range.is(10);
        Assertions.assertFalse(ri.contains(null), "should not contain null");
        Assertions.assertTrue(ri.contains(10), "should contain 10");
        Assertions.assertFalse(ri.contains(11), "should not contain 11");
        ri = Range.is(10, c);
        Assertions.assertFalse(ri.contains(null), "should not contain null");
        Assertions.assertTrue(ri.contains(10), "should contain 10");
        Assertions.assertTrue(ri.contains(11), "should contain 11");
    }

    @Test
    public void testBetweenWithCompare() {
        // all integers are equal
        final Comparator<Integer> c = ( o1, o2) -> 0;
        final Comparator<String> lengthComp = Comparator.comparingInt(String::length);
        Range<Integer> rb = Range.between((-10), 20);
        Assertions.assertFalse(rb.contains(null), "should not contain null");
        Assertions.assertTrue(rb.contains(10), "should contain 10");
        Assertions.assertTrue(rb.contains((-10)), "should contain -10");
        Assertions.assertFalse(rb.contains(21), "should not contain 21");
        Assertions.assertFalse(rb.contains((-11)), "should not contain -11");
        rb = Range.between((-10), 20, c);
        Assertions.assertFalse(rb.contains(null), "should not contain null");
        Assertions.assertTrue(rb.contains(10), "should contain 10");
        Assertions.assertTrue(rb.contains((-10)), "should contain -10");
        Assertions.assertTrue(rb.contains(21), "should contain 21");
        Assertions.assertTrue(rb.contains((-11)), "should contain -11");
        Range<String> rbstr = Range.between("house", "i");
        Assertions.assertFalse(rbstr.contains(null), "should not contain null");
        Assertions.assertTrue(rbstr.contains("house"), "should contain house");
        Assertions.assertTrue(rbstr.contains("i"), "should contain i");
        Assertions.assertFalse(rbstr.contains("hose"), "should not contain hose");
        Assertions.assertFalse(rbstr.contains("ice"), "should not contain ice");
        rbstr = Range.between("house", "i", lengthComp);
        Assertions.assertFalse(rbstr.contains(null), "should not contain null");
        Assertions.assertTrue(rbstr.contains("house"), "should contain house");
        Assertions.assertTrue(rbstr.contains("i"), "should contain i");
        Assertions.assertFalse(rbstr.contains("houses"), "should not contain houses");
        Assertions.assertFalse(rbstr.contains(""), "should not contain ''");
    }

    // -----------------------------------------------------------------------
    @Test
    public void testRangeOfChars() {
        final Range<Character> chars = Range.between('a', 'z');
        Assertions.assertTrue(chars.contains('b'));
        Assertions.assertFalse(chars.contains('B'));
    }

    // -----------------------------------------------------------------------
    @Test
    public void testEqualsObject() {
        Assertions.assertEquals(byteRange, byteRange);
        Assertions.assertEquals(byteRange, byteRange2);
        Assertions.assertEquals(byteRange2, byteRange2);
        Assertions.assertEquals(byteRange, byteRange);
        Assertions.assertEquals(byteRange2, byteRange2);
        Assertions.assertEquals(byteRange3, byteRange3);
        Assertions.assertNotEquals(byteRange2, byteRange3);
        Assertions.assertNotEquals(null, byteRange2);
        Assertions.assertNotEquals("Ni!", byteRange2);
    }

    @Test
    public void testHashCode() {
        Assertions.assertEquals(byteRange.hashCode(), byteRange2.hashCode());
        Assertions.assertNotEquals(byteRange.hashCode(), byteRange3.hashCode());
        Assertions.assertEquals(intRange.hashCode(), intRange.hashCode());
        Assertions.assertTrue(((intRange.hashCode()) != 0));
    }

    @Test
    public void testToString() {
        Assertions.assertNotNull(byteRange.toString());
        final String str = intRange.toString();
        Assertions.assertEquals("[10..20]", str);
        Assertions.assertEquals("[-20..-10]", Range.between((-20), (-10)).toString());
    }

    @Test
    public void testToStringFormat() {
        final String str = intRange.toString("From %1$s to %2$s");
        Assertions.assertEquals("From 10 to 20", str);
    }

    // -----------------------------------------------------------------------
    @Test
    public void testGetMinimum() {
        Assertions.assertEquals(10, ((int) (intRange.getMinimum())));
        Assertions.assertEquals(10L, ((long) (longRange.getMinimum())));
        Assertions.assertEquals(10.0F, floatRange.getMinimum(), 1.0E-5F);
        Assertions.assertEquals(10.0, doubleRange.getMinimum(), 1.0E-5);
    }

    @Test
    public void testGetMaximum() {
        Assertions.assertEquals(20, ((int) (intRange.getMaximum())));
        Assertions.assertEquals(20L, ((long) (longRange.getMaximum())));
        Assertions.assertEquals(20.0F, floatRange.getMaximum(), 1.0E-5F);
        Assertions.assertEquals(20.0, doubleRange.getMaximum(), 1.0E-5);
    }

    @Test
    public void testContains() {
        Assertions.assertFalse(intRange.contains(null));
        Assertions.assertFalse(intRange.contains(5));
        Assertions.assertTrue(intRange.contains(10));
        Assertions.assertTrue(intRange.contains(15));
        Assertions.assertTrue(intRange.contains(20));
        Assertions.assertFalse(intRange.contains(25));
    }

    @Test
    public void testIsAfter() {
        Assertions.assertFalse(intRange.isAfter(null));
        Assertions.assertTrue(intRange.isAfter(5));
        Assertions.assertFalse(intRange.isAfter(10));
        Assertions.assertFalse(intRange.isAfter(15));
        Assertions.assertFalse(intRange.isAfter(20));
        Assertions.assertFalse(intRange.isAfter(25));
    }

    @Test
    public void testIsStartedBy() {
        Assertions.assertFalse(intRange.isStartedBy(null));
        Assertions.assertFalse(intRange.isStartedBy(5));
        Assertions.assertTrue(intRange.isStartedBy(10));
        Assertions.assertFalse(intRange.isStartedBy(15));
        Assertions.assertFalse(intRange.isStartedBy(20));
        Assertions.assertFalse(intRange.isStartedBy(25));
    }

    @Test
    public void testIsEndedBy() {
        Assertions.assertFalse(intRange.isEndedBy(null));
        Assertions.assertFalse(intRange.isEndedBy(5));
        Assertions.assertFalse(intRange.isEndedBy(10));
        Assertions.assertFalse(intRange.isEndedBy(15));
        Assertions.assertTrue(intRange.isEndedBy(20));
        Assertions.assertFalse(intRange.isEndedBy(25));
    }

    @Test
    public void testIsBefore() {
        Assertions.assertFalse(intRange.isBefore(null));
        Assertions.assertFalse(intRange.isBefore(5));
        Assertions.assertFalse(intRange.isBefore(10));
        Assertions.assertFalse(intRange.isBefore(15));
        Assertions.assertFalse(intRange.isBefore(20));
        Assertions.assertTrue(intRange.isBefore(25));
    }

    @Test
    public void testElementCompareTo() {
        Assertions.assertThrows(NullPointerException.class, () -> intRange.elementCompareTo(null));
        Assertions.assertEquals((-1), intRange.elementCompareTo(5));
        Assertions.assertEquals(0, intRange.elementCompareTo(10));
        Assertions.assertEquals(0, intRange.elementCompareTo(15));
        Assertions.assertEquals(0, intRange.elementCompareTo(20));
        Assertions.assertEquals(1, intRange.elementCompareTo(25));
    }

    // -----------------------------------------------------------------------
    @Test
    public void testContainsRange() {
        // null handling
        Assertions.assertFalse(intRange.containsRange(null));
        // easy inside range
        Assertions.assertTrue(intRange.containsRange(Range.between(12, 18)));
        // outside range on each side
        Assertions.assertFalse(intRange.containsRange(Range.between(32, 45)));
        Assertions.assertFalse(intRange.containsRange(Range.between(2, 8)));
        // equals range
        Assertions.assertTrue(intRange.containsRange(Range.between(10, 20)));
        // overlaps
        Assertions.assertFalse(intRange.containsRange(Range.between(9, 14)));
        Assertions.assertFalse(intRange.containsRange(Range.between(16, 21)));
        // touches lower boundary
        Assertions.assertTrue(intRange.containsRange(Range.between(10, 19)));
        Assertions.assertFalse(intRange.containsRange(Range.between(10, 21)));
        // touches upper boundary
        Assertions.assertTrue(intRange.containsRange(Range.between(11, 20)));
        Assertions.assertFalse(intRange.containsRange(Range.between(9, 20)));
        // negative
        Assertions.assertFalse(intRange.containsRange(Range.between((-11), (-18))));
    }

    @Test
    public void testIsAfterRange() {
        Assertions.assertFalse(intRange.isAfterRange(null));
        Assertions.assertTrue(intRange.isAfterRange(Range.between(5, 9)));
        Assertions.assertFalse(intRange.isAfterRange(Range.between(5, 10)));
        Assertions.assertFalse(intRange.isAfterRange(Range.between(5, 20)));
        Assertions.assertFalse(intRange.isAfterRange(Range.between(5, 25)));
        Assertions.assertFalse(intRange.isAfterRange(Range.between(15, 25)));
        Assertions.assertFalse(intRange.isAfterRange(Range.between(21, 25)));
        Assertions.assertFalse(intRange.isAfterRange(Range.between(10, 20)));
    }

    @Test
    public void testIsOverlappedBy() {
        // null handling
        Assertions.assertFalse(intRange.isOverlappedBy(null));
        // easy inside range
        Assertions.assertTrue(intRange.isOverlappedBy(Range.between(12, 18)));
        // outside range on each side
        Assertions.assertFalse(intRange.isOverlappedBy(Range.between(32, 45)));
        Assertions.assertFalse(intRange.isOverlappedBy(Range.between(2, 8)));
        // equals range
        Assertions.assertTrue(intRange.isOverlappedBy(Range.between(10, 20)));
        // overlaps
        Assertions.assertTrue(intRange.isOverlappedBy(Range.between(9, 14)));
        Assertions.assertTrue(intRange.isOverlappedBy(Range.between(16, 21)));
        // touches lower boundary
        Assertions.assertTrue(intRange.isOverlappedBy(Range.between(10, 19)));
        Assertions.assertTrue(intRange.isOverlappedBy(Range.between(10, 21)));
        // touches upper boundary
        Assertions.assertTrue(intRange.isOverlappedBy(Range.between(11, 20)));
        Assertions.assertTrue(intRange.isOverlappedBy(Range.between(9, 20)));
        // negative
        Assertions.assertFalse(intRange.isOverlappedBy(Range.between((-11), (-18))));
    }

    @Test
    public void testIsBeforeRange() {
        Assertions.assertFalse(intRange.isBeforeRange(null));
        Assertions.assertFalse(intRange.isBeforeRange(Range.between(5, 9)));
        Assertions.assertFalse(intRange.isBeforeRange(Range.between(5, 10)));
        Assertions.assertFalse(intRange.isBeforeRange(Range.between(5, 20)));
        Assertions.assertFalse(intRange.isBeforeRange(Range.between(5, 25)));
        Assertions.assertFalse(intRange.isBeforeRange(Range.between(15, 25)));
        Assertions.assertTrue(intRange.isBeforeRange(Range.between(21, 25)));
        Assertions.assertFalse(intRange.isBeforeRange(Range.between(10, 20)));
    }

    @Test
    public void testIntersectionWith() {
        Assertions.assertSame(intRange, intRange.intersectionWith(intRange));
        Assertions.assertSame(byteRange, byteRange.intersectionWith(byteRange));
        Assertions.assertSame(longRange, longRange.intersectionWith(longRange));
        Assertions.assertSame(floatRange, floatRange.intersectionWith(floatRange));
        Assertions.assertSame(doubleRange, doubleRange.intersectionWith(doubleRange));
        Assertions.assertEquals(Range.between(10, 15), intRange.intersectionWith(Range.between(5, 15)));
    }

    @Test
    public void testIntersectionWithNull() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> intRange.intersectionWith(null));
    }

    @Test
    public void testIntersectionWithNonOverlapping() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> intRange.intersectionWith(Range.between(0, 9)));
    }

    // -----------------------------------------------------------------------
    @Test
    public void testSerializing() {
        SerializationUtils.clone(intRange);
    }
}

