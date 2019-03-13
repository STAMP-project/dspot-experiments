package org.roaringbitmap;


import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;
import java.util.Iterator;
import java.util.Random;
import org.junit.Assert;
import org.junit.Test;


public class TestIteratorMemory {
    final IntIteratorFlyweight flyweightIterator = new IntIteratorFlyweight();

    final ReverseIntIteratorFlyweight flyweightReverseIterator = new ReverseIntIteratorFlyweight();

    // See org.roaringbitmap.iteration.IteratorsBenchmark32.BenchmarkState
    final RoaringBitmap bitmap_a;

    final RoaringBitmap bitmap_b;

    final RoaringBitmap bitmap_c;

    {
        final int[] data = takeSortedAndDistinct(new Random(-3819041301603819594L), 100000);
        bitmap_a = RoaringBitmap.bitmapOf(data);
        bitmap_b = new RoaringBitmap();
        for (int k = 0; k < (1 << 30); k += 32)
            bitmap_b.add(k);

        bitmap_c = new RoaringBitmap();
        for (int k = 0; k < (1 << 30); k += 3)
            bitmap_c.add(k);

    }

    protected static final ThreadMXBean THREAD_MBEAN = ManagementFactory.getThreadMXBean();

    @Test
    public void measureBoxedIterationAllocation() {
        if (TestIteratorMemory.isThreadAllocatedMemorySupported(TestIteratorMemory.THREAD_MBEAN)) {
            long before = TestIteratorMemory.getThreadAllocatedBytes(TestIteratorMemory.THREAD_MBEAN, Thread.currentThread().getId());
            Iterator<Integer> intIterator = bitmap_a.iterator();
            long result = 0;
            while (intIterator.hasNext()) {
                result += intIterator.next();
            } 
            // A small check for iterator consistency
            Assert.assertEquals(407, (result % 1024));
            long after = TestIteratorMemory.getThreadAllocatedBytes(TestIteratorMemory.THREAD_MBEAN, Thread.currentThread().getId());
            System.out.println(("Boxed Iteration allocated: " + (after - before)));
        }
    }

    @Test
    public void measureStandardIterationAllocation() {
        if (TestIteratorMemory.isThreadAllocatedMemorySupported(TestIteratorMemory.THREAD_MBEAN)) {
            long before = TestIteratorMemory.getThreadAllocatedBytes(TestIteratorMemory.THREAD_MBEAN, Thread.currentThread().getId());
            IntIterator intIterator = bitmap_a.getIntIterator();
            long result = 0;
            while (intIterator.hasNext()) {
                result += intIterator.next();
            } 
            // A small check for iterator consistency
            Assert.assertEquals(407, (result % 1024));
            long after = TestIteratorMemory.getThreadAllocatedBytes(TestIteratorMemory.THREAD_MBEAN, Thread.currentThread().getId());
            System.out.println(("Standard Iteration allocated: " + (after - before)));
        }
    }

    @Test
    public void measureFlyWeightIterationAllocation() {
        if (TestIteratorMemory.isThreadAllocatedMemorySupported(TestIteratorMemory.THREAD_MBEAN)) {
            long before = TestIteratorMemory.getThreadAllocatedBytes(TestIteratorMemory.THREAD_MBEAN, Thread.currentThread().getId());
            IntIteratorFlyweight intIterator = new IntIteratorFlyweight();
            intIterator.wrap(bitmap_a);
            long result = 0;
            while (intIterator.hasNext()) {
                result += intIterator.next();
            } 
            // A small check for iterator consistency
            Assert.assertEquals(407, (result % 1024));
            long after = TestIteratorMemory.getThreadAllocatedBytes(TestIteratorMemory.THREAD_MBEAN, Thread.currentThread().getId());
            System.out.println(("FlyWeight Iteration allocated: " + (after - before)));
        }
    }
}

