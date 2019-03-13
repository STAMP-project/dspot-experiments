/**
 * HistogramTest.java
 * Written by Gil Tene of Azul Systems, and released to the public domain,
 * as explained at http://creativecommons.org/publicdomain/zero/1.0/
 *
 * @author Gil Tene
 */
package org.HdrHistogram;


import org.junit.Test;


/**
 * JUnit test for {@link Histogram}
 */
public class HistogramShiftTest {
    static final long highestTrackableValue = (3600L * 1000) * 1000;// e.g. for 1 hr in usec units


    @Test
    public void testHistogramShift() throws Exception {
        Histogram histogram = new Histogram(HistogramShiftTest.highestTrackableValue, 3);
        testShiftLowestBucket(histogram);
        testShiftNonLowestBucket(histogram);
    }

    @Test
    public void testIntHistogramShift() throws Exception {
        IntCountsHistogram intCountsHistogram = new IntCountsHistogram(HistogramShiftTest.highestTrackableValue, 3);
        testShiftLowestBucket(intCountsHistogram);
        testShiftNonLowestBucket(intCountsHistogram);
    }

    @Test
    public void testShortHistogramShift() throws Exception {
        ShortCountsHistogram shortCountsHistogram = new ShortCountsHistogram(HistogramShiftTest.highestTrackableValue, 3);
        testShiftLowestBucket(shortCountsHistogram);
        testShiftNonLowestBucket(shortCountsHistogram);
    }

    @Test
    public void testSynchronizedHistogramShift() throws Exception {
        SynchronizedHistogram synchronizedHistogram = new SynchronizedHistogram(HistogramShiftTest.highestTrackableValue, 3);
        testShiftLowestBucket(synchronizedHistogram);
        testShiftNonLowestBucket(synchronizedHistogram);
    }

    @Test(expected = IllegalStateException.class)
    public void testAtomicHistogramShift() throws Exception {
        AtomicHistogram atomicHistogram = new AtomicHistogram(HistogramShiftTest.highestTrackableValue, 3);
        testShiftLowestBucket(atomicHistogram);
        testShiftNonLowestBucket(atomicHistogram);
    }

    @Test
    public void testConcurrentHistogramShift() throws Exception {
        ConcurrentHistogram concurrentHistogram = new ConcurrentHistogram(HistogramShiftTest.highestTrackableValue, 3);
        testShiftLowestBucket(concurrentHistogram);
        testShiftNonLowestBucket(concurrentHistogram);
    }
}

