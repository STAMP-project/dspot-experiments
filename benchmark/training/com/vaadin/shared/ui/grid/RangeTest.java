package com.vaadin.shared.ui.grid;


import com.vaadin.shared.Range;
import org.junit.Assert;
import org.junit.Test;


@SuppressWarnings("static-method")
public class RangeTest {
    @Test(expected = IllegalArgumentException.class)
    public void startAfterEndTest() {
        Range.between(10, 9);
    }

    @Test(expected = IllegalArgumentException.class)
    public void negativeLengthTest() {
        Range.withLength(10, (-1));
    }

    @Test
    public void constructorEquivalenceTest() {
        Assert.assertEquals("10 == [10,11[", Range.withOnly(10), Range.between(10, 11));
        Assert.assertEquals("[10,20[ == 10, length 10", Range.between(10, 20), Range.withLength(10, 10));
        Assert.assertEquals("10 == 10, length 1", Range.withOnly(10), Range.withLength(10, 1));
    }

    @Test
    public void boundsTest() {
        {
            final Range range = Range.between(0, 10);
            Assert.assertEquals("between(0, 10) start", 0, range.getStart());
            Assert.assertEquals("between(0, 10) end", 10, range.getEnd());
        }
        {
            final Range single = Range.withOnly(10);
            Assert.assertEquals("withOnly(10) start", 10, single.getStart());
            Assert.assertEquals("withOnly(10) end", 11, single.getEnd());
        }
        {
            final Range length = Range.withLength(10, 5);
            Assert.assertEquals("withLength(10, 5) start", 10, length.getStart());
            Assert.assertEquals("withLength(10, 5) end", 15, length.getEnd());
        }
    }

    @Test
    @SuppressWarnings("boxing")
    public void equalsTest() {
        final Range range1 = Range.between(0, 10);
        final Range range2 = Range.withLength(0, 11);
        Assert.assertTrue("null", (!(range1.equals(null))));
        Assert.assertTrue("reflexive", range1.equals(range1));
        Assert.assertEquals("symmetric", range1.equals(range2), range2.equals(range1));
    }

    @Test
    public void containsTest() {
        final int start = 0;
        final int end = 10;
        final Range range = Range.between(start, end);
        Assert.assertTrue("start should be contained", range.contains(start));
        Assert.assertTrue("start-1 should not be contained", (!(range.contains((start - 1)))));
        Assert.assertTrue("end should not be contained", (!(range.contains(end))));
        Assert.assertTrue("end-1 should be contained", range.contains((end - 1)));
        Assert.assertTrue("[0..10[ contains 5", Range.between(0, 10).contains(5));
        Assert.assertTrue("empty range does not contain 5", (!(Range.between(5, 5).contains(5))));
    }

    @Test
    public void emptyTest() {
        Assert.assertTrue("[0..0[ should be empty", Range.between(0, 0).isEmpty());
        Assert.assertTrue("Range of length 0 should be empty", Range.withLength(0, 0).isEmpty());
        Assert.assertTrue("[0..1[ should not be empty", (!(Range.between(0, 1).isEmpty())));
        Assert.assertTrue("Range of length 1 should not be empty", (!(Range.withLength(0, 1).isEmpty())));
    }

    @Test
    public void splitTest() {
        final Range startRange = Range.between(0, 10);
        final Range[] splitRanges = startRange.splitAt(5);
        Assert.assertEquals("[0..10[ split at 5, lower", Range.between(0, 5), splitRanges[0]);
        Assert.assertEquals("[0..10[ split at 5, upper", Range.between(5, 10), splitRanges[1]);
    }

    @Test
    public void split_valueBefore() {
        Range range = Range.between(10, 20);
        Range[] splitRanges = range.splitAt(5);
        Assert.assertEquals(Range.between(10, 10), splitRanges[0]);
        Assert.assertEquals(range, splitRanges[1]);
    }

    @Test
    public void split_valueAfter() {
        Range range = Range.between(10, 20);
        Range[] splitRanges = range.splitAt(25);
        Assert.assertEquals(range, splitRanges[0]);
        Assert.assertEquals(Range.between(20, 20), splitRanges[1]);
    }

    @Test
    public void emptySplitTest() {
        final Range range = Range.between(5, 10);
        final Range[] split1 = range.splitAt(0);
        Assert.assertTrue("split1, [0]", split1[0].isEmpty());
        Assert.assertEquals("split1, [1]", range, split1[1]);
        final Range[] split2 = range.splitAt(15);
        Assert.assertEquals("split2, [0]", range, split2[0]);
        Assert.assertTrue("split2, [1]", split2[1].isEmpty());
    }

    @Test
    public void lengthTest() {
        Assert.assertEquals("withLength length", 5, Range.withLength(10, 5).length());
        Assert.assertEquals("between length", 5, Range.between(10, 15).length());
        Assert.assertEquals("withOnly 10 length", 1, Range.withOnly(10).length());
    }

    @Test
    public void intersectsTest() {
        Assert.assertTrue("[0..10[ intersects [5..15[", Range.between(0, 10).intersects(Range.between(5, 15)));
        Assert.assertTrue("[0..10[ does not intersect [10..20[", (!(Range.between(0, 10).intersects(Range.between(10, 20)))));
    }

    @Test
    public void intersects_emptyInside() {
        Assert.assertTrue("[5..5[ does intersect with [0..10[", Range.between(5, 5).intersects(Range.between(0, 10)));
        Assert.assertTrue("[0..10[ does intersect with [5..5[", Range.between(0, 10).intersects(Range.between(5, 5)));
    }

    @Test
    public void intersects_emptyOutside() {
        Assert.assertTrue("[15..15[ does not intersect with [0..10[", (!(Range.between(15, 15).intersects(Range.between(0, 10)))));
        Assert.assertTrue("[0..10[ does not intersect with [15..15[", (!(Range.between(0, 10).intersects(Range.between(15, 15)))));
    }

    @Test
    public void subsetTest() {
        Assert.assertTrue("[5..10[ is subset of [0..20[", Range.between(5, 10).isSubsetOf(Range.between(0, 20)));
        final Range range = Range.between(0, 10);
        Assert.assertTrue("range is subset of self", range.isSubsetOf(range));
        Assert.assertTrue("[0..10[ is not subset of [5..15[", (!(Range.between(0, 10).isSubsetOf(Range.between(5, 15)))));
    }

    @Test
    public void offsetTest() {
        Assert.assertEquals(Range.between(5, 15), Range.between(0, 10).offsetBy(5));
    }

    @Test
    public void rangeStartsBeforeTest() {
        final Range former = Range.between(0, 5);
        final Range latter = Range.between(1, 5);
        Assert.assertTrue("former should starts before latter", former.startsBefore(latter));
        Assert.assertTrue("latter shouldn't start before latter", (!(latter.startsBefore(former))));
        Assert.assertTrue("no overlap allowed", (!(Range.between(0, 5).startsBefore(Range.between(0, 10)))));
    }

    @Test
    public void rangeStartsAfterTest() {
        final Range former = Range.between(0, 5);
        final Range latter = Range.between(5, 10);
        Assert.assertTrue("latter should start after former", latter.startsAfter(former));
        Assert.assertTrue("former shouldn't start after latter", (!(former.startsAfter(latter))));
        Assert.assertTrue("no overlap allowed", (!(Range.between(5, 10).startsAfter(Range.between(0, 6)))));
    }

    @Test
    public void rangeEndsBeforeTest() {
        final Range former = Range.between(0, 5);
        final Range latter = Range.between(5, 10);
        Assert.assertTrue("latter should end before former", former.endsBefore(latter));
        Assert.assertTrue("former shouldn't end before latter", (!(latter.endsBefore(former))));
        Assert.assertTrue("no overlap allowed", (!(Range.between(5, 10).endsBefore(Range.between(9, 15)))));
    }

    @Test
    public void rangeEndsAfterTest() {
        final Range former = Range.between(1, 5);
        final Range latter = Range.between(1, 6);
        Assert.assertTrue("latter should end after former", latter.endsAfter(former));
        Assert.assertTrue("former shouldn't end after latter", (!(former.endsAfter(latter))));
        Assert.assertTrue("no overlap allowed", (!(Range.between(0, 10).endsAfter(Range.between(5, 10)))));
    }

    @Test(expected = IllegalArgumentException.class)
    public void combine_notOverlappingFirstSmaller() {
        Range.between(0, 10).combineWith(Range.between(11, 20));
    }

    @Test(expected = IllegalArgumentException.class)
    public void combine_notOverlappingSecondLarger() {
        Range.between(11, 20).combineWith(Range.between(0, 10));
    }

    @Test(expected = IllegalArgumentException.class)
    public void combine_firstEmptyNotOverlapping() {
        Range.between(15, 15).combineWith(Range.between(0, 10));
    }

    @Test(expected = IllegalArgumentException.class)
    public void combine_secondEmptyNotOverlapping() {
        Range.between(0, 10).combineWith(Range.between(15, 15));
    }

    @Test
    public void combine_barelyOverlapping() {
        Range r1 = Range.between(0, 10);
        Range r2 = Range.between(10, 20);
        // Test both ways, should give the same result
        Range combined1 = r1.combineWith(r2);
        Range combined2 = r2.combineWith(r1);
        Assert.assertEquals(combined1, combined2);
        Assert.assertEquals(0, combined1.getStart());
        Assert.assertEquals(20, combined1.getEnd());
    }

    @Test
    public void combine_subRange() {
        Range r1 = Range.between(0, 10);
        Range r2 = Range.between(2, 8);
        // Test both ways, should give the same result
        Range combined1 = r1.combineWith(r2);
        Range combined2 = r2.combineWith(r1);
        Assert.assertEquals(combined1, combined2);
        Assert.assertEquals(r1, combined1);
    }

    @Test
    public void combine_intersecting() {
        Range r1 = Range.between(0, 10);
        Range r2 = Range.between(5, 15);
        // Test both ways, should give the same result
        Range combined1 = r1.combineWith(r2);
        Range combined2 = r2.combineWith(r1);
        Assert.assertEquals(combined1, combined2);
        Assert.assertEquals(0, combined1.getStart());
        Assert.assertEquals(15, combined1.getEnd());
    }

    @Test
    public void combine_emptyInside() {
        Range r1 = Range.between(0, 10);
        Range r2 = Range.between(5, 5);
        // Test both ways, should give the same result
        Range combined1 = r1.combineWith(r2);
        Range combined2 = r2.combineWith(r1);
        Assert.assertEquals(combined1, combined2);
        Assert.assertEquals(r1, combined1);
    }

    @Test
    public void expand_basic() {
        Range r1 = Range.between(5, 10);
        Range r2 = r1.expand(2, 3);
        Assert.assertEquals(Range.between(3, 13), r2);
    }

    @Test
    public void expand_negativeLegal() {
        Range r1 = Range.between(5, 10);
        Range r2 = r1.expand((-2), (-2));
        Assert.assertEquals(Range.between(7, 8), r2);
        Range r3 = r1.expand((-3), (-2));
        Assert.assertEquals(Range.between(8, 8), r3);
        Range r4 = r1.expand(3, (-8));
        Assert.assertEquals(Range.between(2, 2), r4);
    }

    @Test(expected = IllegalArgumentException.class)
    public void expand_negativeIllegal1() {
        Range r1 = Range.between(5, 10);
        // Should throw because the start would contract beyond the end
        r1.expand((-3), (-3));
    }

    @Test(expected = IllegalArgumentException.class)
    public void expand_negativeIllegal2() {
        Range r1 = Range.between(5, 10);
        // Should throw because the end would contract beyond the start
        r1.expand(3, (-9));
    }

    @Test
    public void restrictTo_fullyInside() {
        Range r1 = Range.between(5, 10);
        Range r2 = Range.between(4, 11);
        Range r3 = r1.restrictTo(r2);
        Assert.assertTrue((r1 == r3));
    }

    @Test
    public void restrictTo_fullyOutside() {
        Range r1 = Range.between(4, 11);
        Range r2 = Range.between(5, 10);
        Range r3 = r1.restrictTo(r2);
        Assert.assertTrue((r2 == r3));
    }

    @Test
    public void restrictTo_notInterstecting() {
        Range r1 = Range.between(5, 10);
        Range r2 = Range.between(15, 20);
        Range r3 = r1.restrictTo(r2);
        Assert.assertTrue("Non-intersecting ranges should produce an empty result", r3.isEmpty());
        Range r4 = r2.restrictTo(r1);
        Assert.assertTrue("Non-intersecting ranges should produce an empty result", r4.isEmpty());
    }

    @Test
    public void restrictTo_startOutside() {
        Range r1 = Range.between(5, 10);
        Range r2 = Range.between(7, 15);
        Range r3 = r1.restrictTo(r2);
        Assert.assertEquals(Range.between(7, 10), r3);
        Assert.assertEquals(r2.restrictTo(r1), r3);
    }

    @Test
    public void restrictTo_endOutside() {
        Range r1 = Range.between(5, 10);
        Range r2 = Range.between(4, 7);
        Range r3 = r1.restrictTo(r2);
        Assert.assertEquals(Range.between(5, 7), r3);
        Assert.assertEquals(r2.restrictTo(r1), r3);
    }

    @Test
    public void restrictTo_empty() {
        Range r1 = Range.between(5, 10);
        Range r2 = Range.between(0, 0);
        Range r3 = r1.restrictTo(r2);
        Assert.assertTrue(r3.isEmpty());
        Range r4 = r2.restrictTo(r1);
        Assert.assertTrue(r4.isEmpty());
    }
}

