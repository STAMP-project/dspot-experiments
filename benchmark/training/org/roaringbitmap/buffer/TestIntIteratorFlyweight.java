/**
 * (c) the authors Licensed under the Apache License, Version 2.0.
 */
package org.roaringbitmap.buffer;


import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.primitives.Ints;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Random;
import org.junit.Assert;
import org.junit.Test;
import org.roaringbitmap.PeekableIntIterator;


public class TestIntIteratorFlyweight {
    @Test
    public void testEmptyIteration() {
        BufferIntIteratorFlyweight iter = new BufferIntIteratorFlyweight();
        BufferReverseIntIteratorFlyweight reverseIter = new BufferReverseIntIteratorFlyweight();
        MutableRoaringBitmap bitmap = MutableRoaringBitmap.bitmapOf();
        iter.wrap(bitmap);
        reverseIter.wrap(bitmap);
        Assert.assertFalse(iter.hasNext());
        Assert.assertFalse(reverseIter.hasNext());
    }

    @Test
    public void testIteration() {
        final Random source = new Random(-3819041301603819594L);
        final int[] data = TestIntIteratorFlyweight.takeSortedAndDistinct(source, 450000);
        MutableRoaringBitmap bitmap = MutableRoaringBitmap.bitmapOf(data);
        BufferIntIteratorFlyweight iter = new BufferIntIteratorFlyweight();
        iter.wrap(bitmap);
        BufferIntIteratorFlyweight iter2 = new BufferIntIteratorFlyweight(bitmap);
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

        BufferReverseIntIteratorFlyweight reverseIter = new BufferReverseIntIteratorFlyweight();
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
        MutableRoaringBitmap bitmap = MutableRoaringBitmap.bitmapOf(data);
        BufferIntIteratorFlyweight iter = new BufferIntIteratorFlyweight(bitmap);
        Assert.assertEquals(iter.peekNext(), data[0]);
        Assert.assertEquals(iter.peekNext(), data[0]);
        BufferIntIteratorFlyweight iter2 = new BufferIntIteratorFlyweight(bitmap);
        PeekableIntIterator j = bitmap.getIntIterator();
        for (int k = 0; k < (data.length); k += 3) {
            iter2.advanceIfNeeded(data[k]);
            iter2.advanceIfNeeded(data[k]);
            j.advanceIfNeeded(data[k]);
            j.advanceIfNeeded(data[k]);
            Assert.assertEquals(j.peekNext(), data[k]);
            Assert.assertEquals(iter2.peekNext(), data[k]);
        }
        BufferReverseIntIteratorFlyweight reverseIter = new BufferReverseIntIteratorFlyweight(bitmap);
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
        MutableRoaringBitmap bitmap = MutableRoaringBitmap.bitmapOf(data);
        BufferIntIteratorFlyweight iter = new BufferIntIteratorFlyweight(bitmap);
        BufferReverseIntIteratorFlyweight reverseIter = ((BufferReverseIntIteratorFlyweight) (new BufferReverseIntIteratorFlyweight(bitmap).clone()));
        final List<Integer> intIteratorCopy = TestIntIteratorFlyweight.asList(iter);
        final List<Integer> reverseIntIteratorCopy = TestIntIteratorFlyweight.asList(reverseIter);
        Assert.assertEquals(bitmap.getCardinality(), intIteratorCopy.size());
        Assert.assertEquals(bitmap.getCardinality(), reverseIntIteratorCopy.size());
        Assert.assertEquals(Ints.asList(data), intIteratorCopy);
        Assert.assertEquals(Lists.reverse(Ints.asList(data)), reverseIntIteratorCopy);
    }

    @Test
    public void testIteration1() {
        final Random source = new Random(-3819041301603819594L);
        final int[] data1 = TestIntIteratorFlyweight.takeSortedAndDistinct(source, 450000);
        final int[] data = Arrays.copyOf(data1, ((data1.length) + 50000));
        LinkedHashSet<Integer> data1Members = new LinkedHashSet<Integer>();
        for (int i : data1) {
            data1Members.add(i);
        }
        int counter = 77777;
        for (int i = data1.length; i < (data.length); ++i) {
            // ensure uniqueness
            while (data1Members.contains(counter)) {
                ++counter;
            } 
            data[i] = counter;// must be unique

            counter++;
            if ((i % 15) == 0) {
                counter += 10;// runs of length 15 or so, with gaps of 10

            }
        }
        Arrays.sort(data);
        MutableRoaringBitmap bitmap = MutableRoaringBitmap.bitmapOf(data);
        BufferIntIteratorFlyweight iter = new BufferIntIteratorFlyweight();
        iter.wrap(bitmap);
        BufferReverseIntIteratorFlyweight reverseIter = new BufferReverseIntIteratorFlyweight();
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
        MutableRoaringBitmap bitmap = MutableRoaringBitmap.bitmapOf(1, 2, 3);
        BufferIntIteratorFlyweight iter = new BufferIntIteratorFlyweight();
        iter.wrap(bitmap);
        BufferReverseIntIteratorFlyweight reverseIter = new BufferReverseIntIteratorFlyweight();
        reverseIter.wrap(bitmap);
        final List<Integer> intIteratorCopy = TestIntIteratorFlyweight.asList(iter);
        final List<Integer> reverseIntIteratorCopy = TestIntIteratorFlyweight.asList(reverseIter);
        Assert.assertEquals(ImmutableList.of(1, 2, 3), intIteratorCopy);
        Assert.assertEquals(ImmutableList.of(3, 2, 1), reverseIntIteratorCopy);
    }
}

