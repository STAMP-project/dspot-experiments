package org.roaringbitmap.buffer;


import java.nio.ByteBuffer;
import java.nio.LongBuffer;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import static MappeableBitmapContainer.MAX_CAPACITY;


@RunWith(Parameterized.class)
public class TestBufferRangeCardinality {
    private int[] elements;

    private int begin;

    private int end;

    private int expected;

    public TestBufferRangeCardinality(int[] elements, int begin, int end, int expected) {
        this.elements = elements;
        this.begin = begin;
        this.end = end;
        this.expected = expected;
    }

    @Test
    public void testCardinalityInBitmapWordRange() {
        LongBuffer array = ByteBuffer.allocateDirect(((MAX_CAPACITY) / 8)).asLongBuffer();
        MappeableBitmapContainer bc = new MappeableBitmapContainer(array, 0);
        for (int e : elements) {
            bc.add(((short) (e)));
        }
        Assert.assertEquals(false, bc.isArrayBacked());
        Assert.assertEquals(expected, BufferUtil.cardinalityInBitmapRange(bc.bitmap, begin, end));
    }
}

