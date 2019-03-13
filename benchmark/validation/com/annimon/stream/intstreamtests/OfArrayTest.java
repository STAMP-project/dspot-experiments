package com.annimon.stream.intstreamtests;


import com.annimon.stream.IntStream;
import org.junit.Assert;
import org.junit.Test;


public final class OfArrayTest {
    @Test
    public void testStreamOfInts() {
        int[] data1 = new int[]{ 1, 2, 3, 4, 5 };
        int[] data2 = new int[]{ 42 };
        int[] data3 = new int[]{  };
        Assert.assertEquals(5, IntStream.of(data1).count());
        Assert.assertEquals(42, IntStream.of(data2).findFirst().getAsInt());
        Assert.assertFalse(IntStream.of(data3).findFirst().isPresent());
    }

    @Test(expected = NullPointerException.class)
    public void testStreamOfIntsNull() {
        IntStream.of(((int[]) (null)));
    }

    @Test
    public void testStreamOfEmptyArray() {
        IntStream.of(new int[0]).custom(assertIsEmpty());
    }
}

