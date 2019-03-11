package org.roaringbitmap.buffer;


import java.util.ArrayList;
import org.junit.Assert;
import org.junit.Test;


public class TestFastAggregation {
    @Test
    public void testNaiveAnd() {
        int[] array1 = new int[]{ 39173, 39174 };
        int[] array2 = new int[]{ 39173, 39174, 39175, 39176, 39177, 39178, 39179 };
        int[] array3 = new int[]{ 39173, 39174 };
        int[] array4 = new int[]{  };
        MutableRoaringBitmap data1 = MutableRoaringBitmap.bitmapOf(array1);
        MutableRoaringBitmap data2 = MutableRoaringBitmap.bitmapOf(array2);
        MutableRoaringBitmap data3 = MutableRoaringBitmap.bitmapOf(array3);
        MutableRoaringBitmap data4 = MutableRoaringBitmap.bitmapOf(array4);
        Assert.assertEquals(data3, BufferFastAggregation.naive_and(data1, data2));
        Assert.assertEquals(new MutableRoaringBitmap(), BufferFastAggregation.naive_and(data4));
    }

    @Test
    public void testPriorityQueueOr() {
        int[] array1 = new int[]{ 1232, 3324, 123, 43243, 1322, 7897, 8767 };
        int[] array2 = new int[]{ 39173, 39174, 39175, 39176, 39177, 39178, 39179 };
        int[] array3 = new int[]{ 1232, 3324, 123, 43243, 1322, 7897, 8767, 39173, 39174, 39175, 39176, 39177, 39178, 39179 };
        int[] array4 = new int[]{  };
        ArrayList<MutableRoaringBitmap> data5 = new ArrayList<>();
        ArrayList<MutableRoaringBitmap> data6 = new ArrayList<>();
        MutableRoaringBitmap data1 = MutableRoaringBitmap.bitmapOf(array1);
        MutableRoaringBitmap data2 = MutableRoaringBitmap.bitmapOf(array2);
        MutableRoaringBitmap data3 = MutableRoaringBitmap.bitmapOf(array3);
        MutableRoaringBitmap data4 = MutableRoaringBitmap.bitmapOf(array4);
        data5.add(data1);
        data5.add(data2);
        Assert.assertEquals(data3, BufferFastAggregation.priorityqueue_or(data1, data2));
        Assert.assertEquals(data1, BufferFastAggregation.priorityqueue_or(data1));
        Assert.assertEquals(data1, BufferFastAggregation.priorityqueue_or(data1, data4));
        Assert.assertEquals(data3, BufferFastAggregation.priorityqueue_or(data5.iterator()));
        Assert.assertEquals(new MutableRoaringBitmap(), BufferFastAggregation.priorityqueue_or(data6.iterator()));
        data6.add(data1);
        Assert.assertEquals(data1, BufferFastAggregation.priorityqueue_or(data6.iterator()));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testPriorityQueueXor() {
        int[] array1 = new int[]{ 1232, 3324, 123, 43243, 1322, 7897, 8767 };
        int[] array2 = new int[]{ 39173, 39174, 39175, 39176, 39177, 39178, 39179 };
        int[] array3 = new int[]{ 1232, 3324, 123, 43243, 1322, 7897, 8767, 39173, 39174, 39175, 39176, 39177, 39178, 39179 };
        ImmutableRoaringBitmap data1 = MutableRoaringBitmap.bitmapOf(array1);
        ImmutableRoaringBitmap data2 = MutableRoaringBitmap.bitmapOf(array2);
        ImmutableRoaringBitmap data3 = MutableRoaringBitmap.bitmapOf(array3);
        Assert.assertEquals(data3, BufferFastAggregation.priorityqueue_xor(data1, data2));
        BufferFastAggregation.priorityqueue_xor(data1);
    }

    @Test
    public void testNaiveAndMapped() {
        int[] array1 = new int[]{ 39173, 39174 };
        int[] array2 = new int[]{ 39173, 39174, 39175, 39176, 39177, 39178, 39179 };
        int[] array3 = new int[]{ 39173, 39174 };
        int[] array4 = new int[]{  };
        ImmutableRoaringBitmap data1 = TestFastAggregation.toMapped(MutableRoaringBitmap.bitmapOf(array1));
        ImmutableRoaringBitmap data2 = TestFastAggregation.toMapped(MutableRoaringBitmap.bitmapOf(array2));
        ImmutableRoaringBitmap data3 = TestFastAggregation.toMapped(MutableRoaringBitmap.bitmapOf(array3));
        ImmutableRoaringBitmap data4 = TestFastAggregation.toMapped(MutableRoaringBitmap.bitmapOf(array4));
        Assert.assertEquals(data3, BufferFastAggregation.naive_and(data1, data2));
        Assert.assertEquals(new MutableRoaringBitmap(), BufferFastAggregation.naive_and(data4));
    }

    @Test
    public void testPriorityQueueOrMapped() {
        int[] array1 = new int[]{ 1232, 3324, 123, 43243, 1322, 7897, 8767 };
        int[] array2 = new int[]{ 39173, 39174, 39175, 39176, 39177, 39178, 39179 };
        int[] array3 = new int[]{ 1232, 3324, 123, 43243, 1322, 7897, 8767, 39173, 39174, 39175, 39176, 39177, 39178, 39179 };
        int[] array4 = new int[]{  };
        ArrayList<ImmutableRoaringBitmap> data5 = new ArrayList<>();
        ArrayList<ImmutableRoaringBitmap> data6 = new ArrayList<>();
        ImmutableRoaringBitmap data1 = TestFastAggregation.toMapped(MutableRoaringBitmap.bitmapOf(array1));
        ImmutableRoaringBitmap data2 = TestFastAggregation.toMapped(MutableRoaringBitmap.bitmapOf(array2));
        ImmutableRoaringBitmap data3 = TestFastAggregation.toMapped(MutableRoaringBitmap.bitmapOf(array3));
        ImmutableRoaringBitmap data4 = TestFastAggregation.toMapped(MutableRoaringBitmap.bitmapOf(array4));
        data5.add(data1);
        data5.add(data2);
        Assert.assertEquals(data3, BufferFastAggregation.priorityqueue_or(data1, data2));
        Assert.assertEquals(data1, BufferFastAggregation.priorityqueue_or(data1));
        Assert.assertEquals(data1, BufferFastAggregation.priorityqueue_or(data1, data4));
        Assert.assertEquals(data3, BufferFastAggregation.priorityqueue_or(data5.iterator()));
        Assert.assertEquals(new MutableRoaringBitmap(), BufferFastAggregation.priorityqueue_or(data6.iterator()));
        data6.add(data1);
        Assert.assertEquals(data1, BufferFastAggregation.priorityqueue_or(data6.iterator()));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testPriorityQueueXorMapped() {
        int[] array1 = new int[]{ 1232, 3324, 123, 43243, 1322, 7897, 8767 };
        int[] array2 = new int[]{ 39173, 39174, 39175, 39176, 39177, 39178, 39179 };
        int[] array3 = new int[]{ 1232, 3324, 123, 43243, 1322, 7897, 8767, 39173, 39174, 39175, 39176, 39177, 39178, 39179 };
        ImmutableRoaringBitmap data1 = TestFastAggregation.toMapped(MutableRoaringBitmap.bitmapOf(array1));
        ImmutableRoaringBitmap data2 = TestFastAggregation.toMapped(MutableRoaringBitmap.bitmapOf(array2));
        ImmutableRoaringBitmap data3 = TestFastAggregation.toMapped(MutableRoaringBitmap.bitmapOf(array3));
        Assert.assertEquals(data3, BufferFastAggregation.priorityqueue_xor(data1, data2));
        BufferFastAggregation.priorityqueue_xor(data1);
    }
}

