/**
 * (c) the authors Licensed under the Apache License, Version 2.0.
 */
package org.roaringbitmap;


import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.primitives.Ints;
import java.util.List;
import java.util.Random;
import org.junit.Assert;
import org.junit.Test;


public class TestIterators {
    @Test
    public void testBitmapIteration() {
        final BitmapContainer bits = new BitmapContainer(new long[]{ 1L, 1L << 63 }, 2);
        Assert.assertEquals(TestIterators.asList(bits.getShortIterator()), ImmutableList.of(0, 127));
        Assert.assertEquals(TestIterators.asList(bits.getReverseShortIterator()), ImmutableList.of(127, 0));
    }

    @Test
    public void testEmptyIteration() {
        Assert.assertFalse(RoaringBitmap.bitmapOf().iterator().hasNext());
        Assert.assertFalse(RoaringBitmap.bitmapOf().getIntIterator().hasNext());
        Assert.assertFalse(RoaringBitmap.bitmapOf().getReverseIntIterator().hasNext());
    }

    @Test
    public void testIteration() {
        final Random source = new Random(-3819041301603819594L);
        final int[] data = TestIterators.takeSortedAndDistinct(source, 450000);
        RoaringBitmap bitmap = RoaringBitmap.bitmapOf(data);
        final List<Integer> iteratorCopy = ImmutableList.copyOf(bitmap.iterator());
        final List<Integer> intIteratorCopy = TestIterators.asList(bitmap.getIntIterator());
        final List<Integer> reverseIntIteratorCopy = TestIterators.asList(bitmap.getReverseIntIterator());
        Assert.assertEquals(bitmap.getCardinality(), iteratorCopy.size());
        Assert.assertEquals(bitmap.getCardinality(), intIteratorCopy.size());
        Assert.assertEquals(bitmap.getCardinality(), reverseIntIteratorCopy.size());
        Assert.assertEquals(Ints.asList(data), iteratorCopy);
        Assert.assertEquals(Ints.asList(data), intIteratorCopy);
        Assert.assertEquals(Lists.reverse(Ints.asList(data)), reverseIntIteratorCopy);
    }

    @Test
    public void testSmallIteration() {
        RoaringBitmap bitmap = RoaringBitmap.bitmapOf(1, 2, 3);
        final List<Integer> iteratorCopy = ImmutableList.copyOf(bitmap.iterator());
        final List<Integer> intIteratorCopy = TestIterators.asList(bitmap.getIntIterator());
        final List<Integer> reverseIntIteratorCopy = TestIterators.asList(bitmap.getReverseIntIterator());
        Assert.assertEquals(ImmutableList.of(1, 2, 3), iteratorCopy);
        Assert.assertEquals(ImmutableList.of(1, 2, 3), intIteratorCopy);
        Assert.assertEquals(ImmutableList.of(3, 2, 1), reverseIntIteratorCopy);
    }

    @Test
    public void testSkips() {
        final Random source = new Random(-3819041301603819594L);
        final int[] data = TestIterators.takeSortedAndDistinct(source, 45000);
        RoaringBitmap bitmap = RoaringBitmap.bitmapOf(data);
        PeekableIntIterator pii = bitmap.getIntIterator();
        for (int i = 0; i < (data.length); ++i) {
            pii.advanceIfNeeded(data[i]);
            Assert.assertEquals(data[i], pii.peekNext());
        }
        pii = bitmap.getIntIterator();
        for (int i = 0; i < (data.length); ++i) {
            pii.advanceIfNeeded(data[i]);
            Assert.assertEquals(data[i], pii.next());
        }
        pii = bitmap.getIntIterator();
        for (int i = 1; i < (data.length); ++i) {
            pii.advanceIfNeeded(data[(i - 1)]);
            pii.next();
            Assert.assertEquals(data[i], pii.peekNext());
        }
        bitmap.getIntIterator().advanceIfNeeded((-1));// should not crash

    }

    @Test
    public void testSkipsDense() {
        RoaringBitmap bitmap = new RoaringBitmap();
        int N = 100000;
        for (int i = 0; i < N; ++i) {
            bitmap.add((2 * i));
        }
        for (int i = 0; i < N; ++i) {
            PeekableIntIterator pii = bitmap.getIntIterator();
            pii.advanceIfNeeded((2 * i));
            Assert.assertEquals(pii.peekNext(), (2 * i));
            Assert.assertEquals(pii.next(), (2 * i));
        }
    }

    @Test
    public void testSkipsRun() {
        RoaringBitmap bitmap = new RoaringBitmap();
        bitmap.add(4L, 100000L);
        bitmap.runOptimize();
        for (int i = 4; i < 100000; ++i) {
            PeekableIntIterator pii = bitmap.getIntIterator();
            pii.advanceIfNeeded(i);
            Assert.assertEquals(pii.peekNext(), i);
            Assert.assertEquals(pii.next(), i);
        }
    }

    @Test
    public void testIndexIterator4() throws Exception {
        RoaringBitmap b = new RoaringBitmap();
        for (int i = 0; i < 4096; i++) {
            b.add(i);
        }
        PeekableIntIterator it = b.getIntIterator();
        it.advanceIfNeeded(4096);
        while (it.hasNext()) {
            it.next();
        } 
    }

    @Test
    public void testEmptySkips() {
        RoaringBitmap bitmap = new RoaringBitmap();
        PeekableIntIterator it = bitmap.getIntIterator();
        it.advanceIfNeeded(0);
    }
}

