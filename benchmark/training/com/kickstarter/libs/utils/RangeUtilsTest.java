package com.kickstarter.libs.utils;


import com.kickstarter.libs.Range;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import junit.framework.TestCase;


public final class RangeUtilsTest extends TestCase {
    public void testRanges() {
        final List<Integer> xs = Arrays.asList(1, 3, 4, 5, 6, 8, 10, 11);
        final List<Range> expected = Arrays.asList(Range.create(0, 1), Range.create(1, 4), Range.create(5, 1), Range.create(6, 2));
        TestCase.assertEquals(expected, RangeUtils.consecutiveRanges(xs));
    }

    public void testRanges_WithEmptyArray() {
        final List<Integer> xs = Collections.emptyList();
        final List<Range> expected = Collections.emptyList();
        TestCase.assertEquals(expected, RangeUtils.consecutiveRanges(xs));
    }

    public void testRanges_WithRepeatedEntries() {
        final List<Integer> xs = Arrays.asList(1, 1, 1, 1);
        final List<Range> expected = Arrays.asList(Range.create(0, 4));
        TestCase.assertEquals(expected, RangeUtils.consecutiveRanges(xs));
    }

    public void testRanges_WithNonMonotonicArray() {
        final List<Integer> xs = Arrays.asList(1, 2, 1, 2, 1, 2);
        final List<Range> expected = Arrays.asList(Range.create(0, 2), Range.create(2, 2), Range.create(4, 2));
        TestCase.assertEquals(expected, RangeUtils.consecutiveRanges(xs));
    }

    public void testRanges_WithSingleton() {
        final List<Integer> xs = Arrays.asList(1);
        final List<Range> expected = Arrays.asList(Range.create(0, 1));
        TestCase.assertEquals(expected, RangeUtils.consecutiveRanges(xs));
    }
}

