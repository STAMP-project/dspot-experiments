package org.roaringbitmap;


import java.util.Arrays;
import org.junit.Assert;
import org.junit.Test;


public class TestFastAggregation {
    @Test
    public void horizontal_or() {
        RoaringBitmap rb1 = RoaringBitmap.bitmapOf(0, 1, 2);
        RoaringBitmap rb2 = RoaringBitmap.bitmapOf(0, 5, 6);
        RoaringBitmap rb3 = RoaringBitmap.bitmapOf((1 << 16), (2 << 16));
        RoaringBitmap result = FastAggregation.horizontal_or(Arrays.asList(rb1, rb2, rb3));
        RoaringBitmap expected = RoaringBitmap.bitmapOf(0, 1, 2, 5, 6, (1 << 16), (2 << 16));
        Assert.assertEquals(expected, result);
    }

    @Test
    public void or() {
        RoaringBitmap rb1 = RoaringBitmap.bitmapOf(0, 1, 2);
        RoaringBitmap rb2 = RoaringBitmap.bitmapOf(0, 5, 6);
        RoaringBitmap rb3 = RoaringBitmap.bitmapOf((1 << 16), (2 << 16));
        RoaringBitmap result = FastAggregation.or(rb1, rb2, rb3);
        RoaringBitmap expected = RoaringBitmap.bitmapOf(0, 1, 2, 5, 6, (1 << 16), (2 << 16));
        Assert.assertEquals(expected, result);
    }

    @Test
    public void horizontal_or2() {
        RoaringBitmap rb1 = RoaringBitmap.bitmapOf(0, 1, 2);
        RoaringBitmap rb2 = RoaringBitmap.bitmapOf(0, 5, 6);
        RoaringBitmap rb3 = RoaringBitmap.bitmapOf((1 << 16), (2 << 16));
        RoaringBitmap result = FastAggregation.horizontal_or(rb1, rb2, rb3);
        RoaringBitmap expected = RoaringBitmap.bitmapOf(0, 1, 2, 5, 6, (1 << 16), (2 << 16));
        Assert.assertEquals(expected, result);
    }

    @Test
    public void priorityqueue_or() {
        RoaringBitmap rb1 = RoaringBitmap.bitmapOf(0, 1, 2);
        RoaringBitmap rb2 = RoaringBitmap.bitmapOf(0, 5, 6);
        RoaringBitmap rb3 = RoaringBitmap.bitmapOf((1 << 16), (2 << 16));
        RoaringBitmap result = FastAggregation.priorityqueue_or(Arrays.asList(rb1, rb2, rb3).iterator());
        RoaringBitmap expected = RoaringBitmap.bitmapOf(0, 1, 2, 5, 6, (1 << 16), (2 << 16));
        Assert.assertEquals(expected, result);
    }

    @Test
    public void priorityqueue_or2() {
        RoaringBitmap rb1 = RoaringBitmap.bitmapOf(0, 1, 2);
        RoaringBitmap rb2 = RoaringBitmap.bitmapOf(0, 5, 6);
        RoaringBitmap rb3 = RoaringBitmap.bitmapOf((1 << 16), (2 << 16));
        RoaringBitmap result = FastAggregation.priorityqueue_or(rb1, rb2, rb3);
        RoaringBitmap expected = RoaringBitmap.bitmapOf(0, 1, 2, 5, 6, (1 << 16), (2 << 16));
        Assert.assertEquals(expected, result);
    }

    private static class ExtendedRoaringBitmap extends RoaringBitmap {}

    @Test
    public void testAndWithIterator() {
        final RoaringBitmap b1 = RoaringBitmap.bitmapOf(1, 2);
        final RoaringBitmap b2 = RoaringBitmap.bitmapOf(2, 3);
        final RoaringBitmap bResult = FastAggregation.and(Arrays.asList(b1, b2).iterator());
        Assert.assertFalse(bResult.contains(1));
        Assert.assertTrue(bResult.contains(2));
        Assert.assertFalse(bResult.contains(3));
        final TestFastAggregation.ExtendedRoaringBitmap eb1 = new TestFastAggregation.ExtendedRoaringBitmap();
        add(1);
        add(2);
        final TestFastAggregation.ExtendedRoaringBitmap eb2 = new TestFastAggregation.ExtendedRoaringBitmap();
        add(2);
        add(3);
        final RoaringBitmap ebResult = FastAggregation.and(Arrays.asList(b1, b2).iterator());
        Assert.assertFalse(ebResult.contains(1));
        Assert.assertTrue(ebResult.contains(2));
        Assert.assertFalse(ebResult.contains(3));
    }

    @Test
    public void testNaiveAndWithIterator() {
        final RoaringBitmap b1 = RoaringBitmap.bitmapOf(1, 2);
        final RoaringBitmap b2 = RoaringBitmap.bitmapOf(2, 3);
        final RoaringBitmap bResult = FastAggregation.naive_and(Arrays.asList(b1, b2).iterator());
        Assert.assertFalse(bResult.contains(1));
        Assert.assertTrue(bResult.contains(2));
        Assert.assertFalse(bResult.contains(3));
        final TestFastAggregation.ExtendedRoaringBitmap eb1 = new TestFastAggregation.ExtendedRoaringBitmap();
        add(1);
        add(2);
        final TestFastAggregation.ExtendedRoaringBitmap eb2 = new TestFastAggregation.ExtendedRoaringBitmap();
        add(2);
        add(3);
        final RoaringBitmap ebResult = FastAggregation.naive_and(Arrays.asList(b1, b2).iterator());
        Assert.assertFalse(ebResult.contains(1));
        Assert.assertTrue(ebResult.contains(2));
        Assert.assertFalse(ebResult.contains(3));
    }

    @Test
    public void testOrWithIterator() {
        final RoaringBitmap b1 = RoaringBitmap.bitmapOf(1, 2);
        final RoaringBitmap b2 = RoaringBitmap.bitmapOf(2, 3);
        final RoaringBitmap bItResult = FastAggregation.or(Arrays.asList(b1, b2).iterator());
        Assert.assertTrue(bItResult.contains(1));
        Assert.assertTrue(bItResult.contains(2));
        Assert.assertTrue(bItResult.contains(3));
        final TestFastAggregation.ExtendedRoaringBitmap eb1 = new TestFastAggregation.ExtendedRoaringBitmap();
        add(1);
        add(2);
        final TestFastAggregation.ExtendedRoaringBitmap eb2 = new TestFastAggregation.ExtendedRoaringBitmap();
        add(2);
        add(3);
        final RoaringBitmap ebItResult = FastAggregation.or(Arrays.asList(b1, b2).iterator());
        Assert.assertTrue(ebItResult.contains(1));
        Assert.assertTrue(ebItResult.contains(2));
        Assert.assertTrue(ebItResult.contains(3));
    }

    @Test
    public void testNaiveOrWithIterator() {
        final RoaringBitmap b1 = RoaringBitmap.bitmapOf(1, 2);
        final RoaringBitmap b2 = RoaringBitmap.bitmapOf(2, 3);
        final RoaringBitmap bResult = FastAggregation.naive_or(Arrays.asList(b1, b2).iterator());
        Assert.assertTrue(bResult.contains(1));
        Assert.assertTrue(bResult.contains(2));
        Assert.assertTrue(bResult.contains(3));
        final TestFastAggregation.ExtendedRoaringBitmap eb1 = new TestFastAggregation.ExtendedRoaringBitmap();
        add(1);
        add(2);
        final TestFastAggregation.ExtendedRoaringBitmap eb2 = new TestFastAggregation.ExtendedRoaringBitmap();
        add(2);
        add(3);
        final RoaringBitmap ebResult = FastAggregation.naive_or(Arrays.asList(b1, b2).iterator());
        Assert.assertTrue(ebResult.contains(1));
        Assert.assertTrue(ebResult.contains(2));
        Assert.assertTrue(ebResult.contains(3));
    }

    @Test
    public void testNaiveXorWithIterator() {
        final RoaringBitmap b1 = RoaringBitmap.bitmapOf(1, 2);
        final RoaringBitmap b2 = RoaringBitmap.bitmapOf(2, 3);
        final RoaringBitmap bResult = FastAggregation.naive_xor(Arrays.asList(b1, b2).iterator());
        Assert.assertTrue(bResult.contains(1));
        Assert.assertFalse(bResult.contains(2));
        Assert.assertTrue(bResult.contains(3));
        final TestFastAggregation.ExtendedRoaringBitmap eb1 = new TestFastAggregation.ExtendedRoaringBitmap();
        add(1);
        add(2);
        final TestFastAggregation.ExtendedRoaringBitmap eb2 = new TestFastAggregation.ExtendedRoaringBitmap();
        add(2);
        add(3);
        final RoaringBitmap ebResult = FastAggregation.naive_xor(Arrays.asList(b1, b2).iterator());
        Assert.assertTrue(ebResult.contains(1));
        Assert.assertFalse(ebResult.contains(2));
        Assert.assertTrue(ebResult.contains(3));
    }
}

