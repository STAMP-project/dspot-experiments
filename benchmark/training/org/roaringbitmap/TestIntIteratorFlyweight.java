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


public class TestIntIteratorFlyweight {
    @Test
    public void testEmptyIteration() {
        IntIteratorFlyweight iter = new IntIteratorFlyweight();
        ReverseIntIteratorFlyweight reverseIter = new ReverseIntIteratorFlyweight();
        RoaringBitmap bitmap = RoaringBitmap.bitmapOf();
        iter.wrap(bitmap);
        reverseIter.wrap(bitmap);
        Assert.assertFalse(iter.hasNext());
        Assert.assertFalse(reverseIter.hasNext());
    }

    @Test
    public void testIteration() {
        final Random source = new Random(-3819041301603819594L);
        final int[] data = TestIntIteratorFlyweight.takeSortedAndDistinct(source, 450000);
        // make at least one long run
        for (int i = 0; i < 25000; ++i) {
            data[(70000 + i)] = (data[70000]) + i;
        }
        RoaringBitmap bitmap = RoaringBitmap.bitmapOf(data);
        bitmap.runOptimize();
        IntIteratorFlyweight iter = new IntIteratorFlyweight();
        iter.wrap(bitmap);
        IntIteratorFlyweight iter2 = new IntIteratorFlyweight(bitmap);
        PeekableIntIterator j = bitmap.getIntIterator();
        for (int k = 0; k < (data.length); k += 3) {
            iter2.advanceIfNeeded(data[k]);
            iter2.advanceIfNeeded(data[k]);
            j.advanceIfNeeded(data[k]);
            j.advanceIfNeeded(data[k]);
            Assert.assertEquals(j.peekNext(), data[k]);
            Assert.assertEquals(iter2.peekNext(), data[k]);
        }
        advanceIfNeeded((-1));
        advanceIfNeeded((-1));// should not crash

        ReverseIntIteratorFlyweight reverseIter = new ReverseIntIteratorFlyweight();
        reverseIter.wrap(bitmap);
        final List<Integer> intIteratorCopy = TestIntIteratorFlyweight.asList(iter);
        final List<Integer> reverseIntIteratorCopy = TestIntIteratorFlyweight.asList(reverseIter);
        Assert.assertEquals(bitmap.getCardinality(), intIteratorCopy.size());
        Assert.assertEquals(bitmap.getCardinality(), reverseIntIteratorCopy.size());
        Assert.assertEquals(Ints.asList(data), intIteratorCopy);
        Assert.assertEquals(Lists.reverse(Ints.asList(data)), reverseIntIteratorCopy);
    }

    @Test
    public void testIterationFromBitmap() {
        final Random source = new Random(-3819041301603819594L);
        final int[] data = TestIntIteratorFlyweight.takeSortedAndDistinct(source, 450000);
        // make at least one long run
        for (int i = 0; i < 25000; ++i) {
            data[(70000 + i)] = (data[70000]) + i;
        }
        RoaringBitmap bitmap = RoaringBitmap.bitmapOf(data);
        bitmap.runOptimize();
        IntIteratorFlyweight iter = new IntIteratorFlyweight(bitmap);
        Assert.assertEquals(iter.peekNext(), data[0]);
        Assert.assertEquals(iter.peekNext(), data[0]);
        IntIteratorFlyweight iter2 = new IntIteratorFlyweight(bitmap);
        PeekableIntIterator j = bitmap.getIntIterator();
        for (int k = 0; k < (data.length); k += 3) {
            iter2.advanceIfNeeded(data[k]);
            iter2.advanceIfNeeded(data[k]);
            j.advanceIfNeeded(data[k]);
            j.advanceIfNeeded(data[k]);
            Assert.assertEquals(j.peekNext(), data[k]);
            Assert.assertEquals(iter2.peekNext(), data[k]);
        }
        ReverseIntIteratorFlyweight reverseIter = new ReverseIntIteratorFlyweight(bitmap);
        final List<Integer> intIteratorCopy = TestIntIteratorFlyweight.asList(iter);
        final List<Integer> reverseIntIteratorCopy = TestIntIteratorFlyweight.asList(reverseIter);
        Assert.assertEquals(bitmap.getCardinality(), intIteratorCopy.size());
        Assert.assertEquals(bitmap.getCardinality(), reverseIntIteratorCopy.size());
        Assert.assertEquals(Ints.asList(data), intIteratorCopy);
        Assert.assertEquals(Lists.reverse(Ints.asList(data)), reverseIntIteratorCopy);
    }

    @Test
    public void testIterationFromBitmapClone() {
        final Random source = new Random(-3819041301603819594L);
        final int[] data = TestIntIteratorFlyweight.takeSortedAndDistinct(source, 450000);
        // make at least one long run
        for (int i = 0; i < 25000; ++i) {
            data[(70000 + i)] = (data[70000]) + i;
        }
        RoaringBitmap bitmap = RoaringBitmap.bitmapOf(data);
        bitmap.runOptimize();
        IntIteratorFlyweight iter = ((IntIteratorFlyweight) (new IntIteratorFlyweight(bitmap).clone()));
        ReverseIntIteratorFlyweight reverseIter = ((ReverseIntIteratorFlyweight) (new ReverseIntIteratorFlyweight(bitmap).clone()));
        final List<Integer> intIteratorCopy = TestIntIteratorFlyweight.asList(iter);
        final List<Integer> reverseIntIteratorCopy = TestIntIteratorFlyweight.asList(reverseIter);
        Assert.assertEquals(bitmap.getCardinality(), intIteratorCopy.size());
        Assert.assertEquals(bitmap.getCardinality(), reverseIntIteratorCopy.size());
        Assert.assertEquals(Ints.asList(data), intIteratorCopy);
        Assert.assertEquals(Lists.reverse(Ints.asList(data)), reverseIntIteratorCopy);
    }

    @Test
    public void testIterationSmall() {
        final int[] data = new int[]{ 1, 2, 3, 4, 5, 6, 100, 101, 102, 103, 104, 105, 50000, 50001, 50002, 1000000, 1000005, 1000007 };// runcontainer then arraycontainer

        RoaringBitmap bitmap = RoaringBitmap.bitmapOf(data);
        bitmap.runOptimize();
        IntIteratorFlyweight iter = new IntIteratorFlyweight();
        iter.wrap(bitmap);
        ReverseIntIteratorFlyweight reverseIter = new ReverseIntIteratorFlyweight();
        reverseIter.wrap(bitmap);
        final List<Integer> intIteratorCopy = TestIntIteratorFlyweight.asList(iter);
        final List<Integer> reverseIntIteratorCopy = TestIntIteratorFlyweight.asList(reverseIter);
        Assert.assertEquals(bitmap.getCardinality(), intIteratorCopy.size());
        Assert.assertEquals(bitmap.getCardinality(), reverseIntIteratorCopy.size());
        Assert.assertEquals(Ints.asList(data), intIteratorCopy);
        Assert.assertEquals(Lists.reverse(Ints.asList(data)), reverseIntIteratorCopy);
    }

    @Test
    public void testSmallIteration() {
        RoaringBitmap bitmap = RoaringBitmap.bitmapOf(1, 2, 3);
        IntIteratorFlyweight iter = new IntIteratorFlyweight();
        iter.wrap(bitmap);
        ReverseIntIteratorFlyweight reverseIter = new ReverseIntIteratorFlyweight();
        reverseIter.wrap(bitmap);
        final List<Integer> intIteratorCopy = TestIntIteratorFlyweight.asList(iter);
        final List<Integer> reverseIntIteratorCopy = TestIntIteratorFlyweight.asList(reverseIter);
        Assert.assertEquals(ImmutableList.of(1, 2, 3), intIteratorCopy);
        Assert.assertEquals(ImmutableList.of(3, 2, 1), reverseIntIteratorCopy);
    }

    @Test
    public void testClone() {
        RoaringBitmap bitmap = RoaringBitmap.bitmapOf(1, 2, 3, 4, 5);
        IntIteratorFlyweight iter = new IntIteratorFlyweight(bitmap);
        PeekableIntIterator iterClone = iter.clone();
        final List<Integer> iterList = TestIntIteratorFlyweight.asList(iter);
        final List<Integer> iterCloneList = TestIntIteratorFlyweight.asList(iterClone);
        Assert.assertEquals(iterList.toString(), iterCloneList.toString());
    }
}

