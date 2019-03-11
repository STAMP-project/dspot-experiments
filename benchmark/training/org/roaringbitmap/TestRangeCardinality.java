package org.roaringbitmap;


import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;


@RunWith(Parameterized.class)
public class TestRangeCardinality {
    private int[] elements;

    private int begin;

    private int end;

    private int expected;

    public TestRangeCardinality(int[] elements, int begin, int end, int expected) {
        this.elements = elements;
        this.begin = begin;
        this.end = end;
        this.expected = expected;
    }

    @Test
    public void testCardinalityInBitmapWordRange() {
        BitmapContainer bc = new BitmapContainer();
        for (int e : elements) {
            bc.add(((short) (e)));
        }
        Assert.assertEquals(expected, Util.cardinalityInBitmapRange(bc.bitmap, begin, end));
    }
}

