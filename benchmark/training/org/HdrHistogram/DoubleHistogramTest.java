/**
 * HistogramTest.java
 * Written by Gil Tene of Azul Systems, and released to the public domain,
 * as explained at http://creativecommons.org/publicdomain/zero/1.0/
 *
 * @author Gil Tene
 */
package org.HdrHistogram;


import org.junit.Assert;
import org.junit.Test;


/**
 * JUnit test for {@link Histogram}
 */
public class DoubleHistogramTest {
    static final long trackableValueRangeSize = (3600L * 1000) * 1000;// e.g. for 1 hr in usec units


    static final int numberOfSignificantValueDigits = 3;

    // static final long testValueLevel = 12340;
    static final double testValueLevel = 4.0;

    @Test(expected = IllegalArgumentException.class)
    public void testTrackableValueRangeMustBeGreaterThanTwo() throws Exception {
        new DoubleHistogram(1, DoubleHistogramTest.numberOfSignificantValueDigits);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNumberOfSignificantValueDigitsMustBeLessThanSix() throws Exception {
        new DoubleHistogram(DoubleHistogramTest.trackableValueRangeSize, 6);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNumberOfSignificantValueDigitsMustBePositive() throws Exception {
        new DoubleHistogram(DoubleHistogramTest.trackableValueRangeSize, (-1));
    }

    @Test
    public void testConstructionArgumentGets() throws Exception {
        DoubleHistogram histogram = new DoubleHistogram(DoubleHistogramTest.trackableValueRangeSize, DoubleHistogramTest.numberOfSignificantValueDigits);
        // Record 1.0, and verify that the range adjust to it:
        histogram.recordValue(Math.pow(2.0, 20));
        histogram.recordValue(1.0);
        Assert.assertEquals(1.0, histogram.getCurrentLowestTrackableNonZeroValue(), 0.001);
        Assert.assertEquals(DoubleHistogramTest.trackableValueRangeSize, histogram.getHighestToLowestValueRatio());
        Assert.assertEquals(DoubleHistogramTest.numberOfSignificantValueDigits, histogram.getNumberOfSignificantValueDigits());
        DoubleHistogram histogram2 = new DoubleHistogram(DoubleHistogramTest.trackableValueRangeSize, DoubleHistogramTest.numberOfSignificantValueDigits);
        // Record a larger value, and verify that the range adjust to it too:
        histogram2.recordValue(((2048.0 * 1024.0) * 1024.0));
        Assert.assertEquals(((2048.0 * 1024.0) * 1024.0), histogram2.getCurrentLowestTrackableNonZeroValue(), 0.001);
        DoubleHistogram histogram3 = new DoubleHistogram(DoubleHistogramTest.trackableValueRangeSize, DoubleHistogramTest.numberOfSignificantValueDigits);
        // Record a value that is 1000x outside of the initially set range, which should scale us by 1/1024x:
        histogram3.recordValue((1 / 1000.0));
        Assert.assertEquals((1.0 / 1024), histogram3.getCurrentLowestTrackableNonZeroValue(), 0.001);
    }

    @Test
    public void testDataRange() {
        // A trackableValueRangeSize histigram
        DoubleHistogram histogram = new DoubleHistogram(DoubleHistogramTest.trackableValueRangeSize, DoubleHistogramTest.numberOfSignificantValueDigits);
        histogram.recordValue(0.0);// Include a zero value to make sure things are handled right.

        Assert.assertEquals(1L, histogram.getCountAtValue(0.0));
        double topValue = 1.0;
        try {
            while (true) {
                histogram.recordValue(topValue);
                topValue *= 2.0;
            } 
        } catch (ArrayIndexOutOfBoundsException ex) {
        }
        Assert.assertEquals((1L << 33), topValue, 1.0E-5);
        Assert.assertEquals(1L, histogram.getCountAtValue(0.0));
        histogram = new DoubleHistogram(DoubleHistogramTest.trackableValueRangeSize, DoubleHistogramTest.numberOfSignificantValueDigits);
        histogram.recordValue(0.0);// Include a zero value to make sure things are handled right.

        double bottomValue = 1L << 33;
        try {
            while (true) {
                histogram.recordValue(bottomValue);
                bottomValue /= 2.0;
            } 
        } catch (ArrayIndexOutOfBoundsException ex) {
            System.out.println(("Bottom value at exception point = " + bottomValue));
        }
        Assert.assertEquals(1.0, bottomValue, 1.0E-5);
        long expectedRange = 1L << ((findContainingBinaryOrderOfMagnitude(DoubleHistogramTest.trackableValueRangeSize)) + 1);
        Assert.assertEquals(expectedRange, (topValue / bottomValue), 1.0E-5);
        Assert.assertEquals(1L, histogram.getCountAtValue(0.0));
    }

    @Test
    public void testRecordValue() throws Exception {
        DoubleHistogram histogram = new DoubleHistogram(DoubleHistogramTest.trackableValueRangeSize, DoubleHistogramTest.numberOfSignificantValueDigits);
        histogram.recordValue(DoubleHistogramTest.testValueLevel);
        Assert.assertEquals(1L, histogram.getCountAtValue(DoubleHistogramTest.testValueLevel));
        Assert.assertEquals(1L, histogram.getTotalCount());
    }

    @Test(expected = ArrayIndexOutOfBoundsException.class)
    public void testRecordValue_Overflow_ShouldThrowException() throws Exception {
        DoubleHistogram histogram = new DoubleHistogram(DoubleHistogramTest.trackableValueRangeSize, DoubleHistogramTest.numberOfSignificantValueDigits);
        histogram.recordValue(((DoubleHistogramTest.trackableValueRangeSize) * 3));
        histogram.recordValue(1.0);
    }

    @Test
    public void testRecordValueWithExpectedInterval() throws Exception {
        DoubleHistogram histogram = new DoubleHistogram(DoubleHistogramTest.trackableValueRangeSize, DoubleHistogramTest.numberOfSignificantValueDigits);
        histogram.recordValue(0);
        histogram.recordValueWithExpectedInterval(DoubleHistogramTest.testValueLevel, ((DoubleHistogramTest.testValueLevel) / 4));
        DoubleHistogram rawHistogram = new DoubleHistogram(DoubleHistogramTest.trackableValueRangeSize, DoubleHistogramTest.numberOfSignificantValueDigits);
        rawHistogram.recordValue(0);
        rawHistogram.recordValue(DoubleHistogramTest.testValueLevel);
        // The raw data will not include corrected samples:
        Assert.assertEquals(1L, rawHistogram.getCountAtValue(0));
        Assert.assertEquals(0L, rawHistogram.getCountAtValue((((DoubleHistogramTest.testValueLevel) * 1) / 4)));
        Assert.assertEquals(0L, rawHistogram.getCountAtValue((((DoubleHistogramTest.testValueLevel) * 2) / 4)));
        Assert.assertEquals(0L, rawHistogram.getCountAtValue((((DoubleHistogramTest.testValueLevel) * 3) / 4)));
        Assert.assertEquals(1L, rawHistogram.getCountAtValue((((DoubleHistogramTest.testValueLevel) * 4) / 4)));
        Assert.assertEquals(2L, rawHistogram.getTotalCount());
        // The data will include corrected samples:
        Assert.assertEquals(1L, histogram.getCountAtValue(0));
        Assert.assertEquals(1L, histogram.getCountAtValue((((DoubleHistogramTest.testValueLevel) * 1) / 4)));
        Assert.assertEquals(1L, histogram.getCountAtValue((((DoubleHistogramTest.testValueLevel) * 2) / 4)));
        Assert.assertEquals(1L, histogram.getCountAtValue((((DoubleHistogramTest.testValueLevel) * 3) / 4)));
        Assert.assertEquals(1L, histogram.getCountAtValue((((DoubleHistogramTest.testValueLevel) * 4) / 4)));
        Assert.assertEquals(5L, histogram.getTotalCount());
    }

    @Test
    public void testReset() throws Exception {
        DoubleHistogram histogram = new DoubleHistogram(DoubleHistogramTest.trackableValueRangeSize, DoubleHistogramTest.numberOfSignificantValueDigits);
        histogram.recordValue(DoubleHistogramTest.testValueLevel);
        histogram.recordValue(10);
        histogram.recordValue(100);
        Assert.assertEquals(histogram.getMinValue(), Math.min(10.0, DoubleHistogramTest.testValueLevel), 1.0);
        Assert.assertEquals(histogram.getMaxValue(), Math.max(100.0, DoubleHistogramTest.testValueLevel), 1.0);
        histogram.reset();
        Assert.assertEquals(0L, histogram.getCountAtValue(DoubleHistogramTest.testValueLevel));
        Assert.assertEquals(0L, histogram.getTotalCount());
        histogram.recordValue(20);
        histogram.recordValue(80);
        Assert.assertEquals(histogram.getMinValue(), 20.0, 1.0);
        Assert.assertEquals(histogram.getMaxValue(), 80.0, 1.0);
    }

    @Test
    public void testAdd() throws Exception {
        DoubleHistogram histogram = new DoubleHistogram(DoubleHistogramTest.trackableValueRangeSize, DoubleHistogramTest.numberOfSignificantValueDigits);
        DoubleHistogram other = new DoubleHistogram(DoubleHistogramTest.trackableValueRangeSize, DoubleHistogramTest.numberOfSignificantValueDigits);
        histogram.recordValue(DoubleHistogramTest.testValueLevel);
        histogram.recordValue(((DoubleHistogramTest.testValueLevel) * 1000));
        other.recordValue(DoubleHistogramTest.testValueLevel);
        other.recordValue(((DoubleHistogramTest.testValueLevel) * 1000));
        histogram.add(other);
        Assert.assertEquals(2L, histogram.getCountAtValue(DoubleHistogramTest.testValueLevel));
        Assert.assertEquals(2L, histogram.getCountAtValue(((DoubleHistogramTest.testValueLevel) * 1000)));
        Assert.assertEquals(4L, histogram.getTotalCount());
        DoubleHistogram biggerOther = new DoubleHistogram(((DoubleHistogramTest.trackableValueRangeSize) * 2), DoubleHistogramTest.numberOfSignificantValueDigits);
        biggerOther.recordValue(DoubleHistogramTest.testValueLevel);
        biggerOther.recordValue(((DoubleHistogramTest.testValueLevel) * 1000));
        // Adding the smaller histogram to the bigger one should work:
        biggerOther.add(histogram);
        Assert.assertEquals(3L, biggerOther.getCountAtValue(DoubleHistogramTest.testValueLevel));
        Assert.assertEquals(3L, biggerOther.getCountAtValue(((DoubleHistogramTest.testValueLevel) * 1000)));
        Assert.assertEquals(6L, biggerOther.getTotalCount());
        // Since we are auto-sized, trying to add a larger histogram into a smaller one should work if no
        // overflowing data is there:
        try {
            // This should throw:
            histogram.add(biggerOther);
        } catch (ArrayIndexOutOfBoundsException e) {
            Assert.fail("Should not thow with out of bounds error");
        }
        // But trying to add smaller values to a larger histogram that actually uses it's range should throw an AIOOB:
        histogram.recordValue(1.0);
        other.recordValue(1.0);
        biggerOther.recordValue(((DoubleHistogramTest.trackableValueRangeSize) * 8));
        try {
            // This should throw:
            biggerOther.add(histogram);
            Assert.fail("Should have thown with out of bounds error");
        } catch (ArrayIndexOutOfBoundsException e) {
        }
    }

    @Test
    public void testAddWithAutoResize() {
        DoubleHistogram histo1 = new DoubleHistogram(3);
        histo1.setAutoResize(true);
        histo1.recordValue(6.0);
        histo1.recordValue(1.0);
        histo1.recordValue(5.0);
        histo1.recordValue(8.0);
        histo1.recordValue(3.0);
        histo1.recordValue(7.0);
        DoubleHistogram histo2 = new DoubleHistogram(3);
        histo2.setAutoResize(true);
        histo2.recordValue(9.0);
        DoubleHistogram histo3 = new DoubleHistogram(3);
        histo3.setAutoResize(true);
        histo3.recordValue(4.0);
        histo3.recordValue(2.0);
        histo3.recordValue(10.0);
        DoubleHistogram merged = new DoubleHistogram(3);
        merged.setAutoResize(true);
        merged.add(histo1);
        merged.add(histo2);
        merged.add(histo3);
        Assert.assertEquals(merged.getTotalCount(), (((histo1.getTotalCount()) + (histo2.getTotalCount())) + (histo3.getTotalCount())));
        Assert.assertEquals(1.0, merged.getMinValue(), 0.01);
        Assert.assertEquals(10.0, merged.getMaxValue(), 0.01);
    }

    @Test
    public void testSizeOfEquivalentValueRange() {
        DoubleHistogram histogram = new DoubleHistogram(DoubleHistogramTest.trackableValueRangeSize, DoubleHistogramTest.numberOfSignificantValueDigits);
        histogram.recordValue(1.0);
        Assert.assertEquals("Size of equivalent range for value 1 is 1", (1.0 / 1024.0), histogram.sizeOfEquivalentValueRange(1), 0.001);
        Assert.assertEquals("Size of equivalent range for value 2500 is 2", 2, histogram.sizeOfEquivalentValueRange(2500), 0.001);
        Assert.assertEquals("Size of equivalent range for value 8191 is 4", 4, histogram.sizeOfEquivalentValueRange(8191), 0.001);
        Assert.assertEquals("Size of equivalent range for value 8192 is 8", 8, histogram.sizeOfEquivalentValueRange(8192), 0.001);
        Assert.assertEquals("Size of equivalent range for value 10000 is 8", 8, histogram.sizeOfEquivalentValueRange(10000), 0.001);
    }

    @Test
    public void testLowestEquivalentValue() {
        DoubleHistogram histogram = new DoubleHistogram(DoubleHistogramTest.trackableValueRangeSize, DoubleHistogramTest.numberOfSignificantValueDigits);
        histogram.recordValue(1.0);
        Assert.assertEquals("The lowest equivalent value to 10007 is 10000", 10000, histogram.lowestEquivalentValue(10007), 0.001);
        Assert.assertEquals("The lowest equivalent value to 10009 is 10008", 10008, histogram.lowestEquivalentValue(10009), 0.001);
    }

    @Test
    public void testHighestEquivalentValue() {
        DoubleHistogram histogram = new DoubleHistogram(DoubleHistogramTest.trackableValueRangeSize, DoubleHistogramTest.numberOfSignificantValueDigits);
        histogram.recordValue(1.0);
        Assert.assertEquals("The highest equivalent value to 8180 is 8183", 8183.99999, histogram.highestEquivalentValue(8180), 0.001);
        Assert.assertEquals("The highest equivalent value to 8187 is 8191", 8191.99999, histogram.highestEquivalentValue(8191), 0.001);
        Assert.assertEquals("The highest equivalent value to 8193 is 8199", 8199.99999, histogram.highestEquivalentValue(8193), 0.001);
        Assert.assertEquals("The highest equivalent value to 9995 is 9999", 9999.99999, histogram.highestEquivalentValue(9995), 0.001);
        Assert.assertEquals("The highest equivalent value to 10007 is 10007", 10007.99999, histogram.highestEquivalentValue(10007), 0.001);
        Assert.assertEquals("The highest equivalent value to 10008 is 10015", 10015.99999, histogram.highestEquivalentValue(10008), 0.001);
    }

    @Test
    public void testMedianEquivalentValue() {
        DoubleHistogram histogram = new DoubleHistogram(DoubleHistogramTest.trackableValueRangeSize, DoubleHistogramTest.numberOfSignificantValueDigits);
        histogram.recordValue(1.0);
        Assert.assertEquals("The median equivalent value to 4 is 4", 4.002, histogram.medianEquivalentValue(4), 0.001);
        Assert.assertEquals("The median equivalent value to 5 is 5", 5.002, histogram.medianEquivalentValue(5), 0.001);
        Assert.assertEquals("The median equivalent value to 4000 is 4001", 4001, histogram.medianEquivalentValue(4000), 0.001);
        Assert.assertEquals("The median equivalent value to 8000 is 8002", 8002, histogram.medianEquivalentValue(8000), 0.001);
        Assert.assertEquals("The median equivalent value to 10007 is 10004", 10004, histogram.medianEquivalentValue(10007), 0.001);
    }

    @Test
    public void testNextNonEquivalentValue() {
        DoubleHistogram histogram = new DoubleHistogram(DoubleHistogramTest.trackableValueRangeSize, DoubleHistogramTest.numberOfSignificantValueDigits);
        Assert.assertNotSame(null, histogram);
    }

    @Test
    public void equalsWillNotThrowClassCastException() {
        SynchronizedDoubleHistogram synchronizedDoubleHistogram = new SynchronizedDoubleHistogram(1);
        IntCountsHistogram other = new IntCountsHistogram(1);
        synchronizedDoubleHistogram.equals(other);
    }

    @Test
    public void testSerialization() throws Exception {
        DoubleHistogram histogram = new DoubleHistogram(DoubleHistogramTest.trackableValueRangeSize, 3);
        testDoubleHistogramSerialization(histogram);
        DoubleHistogram withIntHistogram = new DoubleHistogram(DoubleHistogramTest.trackableValueRangeSize, 3, IntCountsHistogram.class);
        testDoubleHistogramSerialization(withIntHistogram);
        DoubleHistogram withShortHistogram = new DoubleHistogram(DoubleHistogramTest.trackableValueRangeSize, 3, ShortCountsHistogram.class);
        testDoubleHistogramSerialization(withShortHistogram);
        histogram = new DoubleHistogram(DoubleHistogramTest.trackableValueRangeSize, 2, Histogram.class);
        testDoubleHistogramSerialization(histogram);
        withIntHistogram = new DoubleHistogram(DoubleHistogramTest.trackableValueRangeSize, 2, IntCountsHistogram.class);
        testDoubleHistogramSerialization(withIntHistogram);
        withShortHistogram = new DoubleHistogram(DoubleHistogramTest.trackableValueRangeSize, 2, ShortCountsHistogram.class);
        testDoubleHistogramSerialization(withShortHistogram);
    }

    @Test
    public void testCopy() throws Exception {
        DoubleHistogram histogram = new DoubleHistogram(DoubleHistogramTest.trackableValueRangeSize, DoubleHistogramTest.numberOfSignificantValueDigits);
        histogram.recordValue(DoubleHistogramTest.testValueLevel);
        histogram.recordValue(((DoubleHistogramTest.testValueLevel) * 10));
        histogram.recordValueWithExpectedInterval(((histogram.getCurrentHighestTrackableValue()) - 1), 31000);
        System.out.println("Testing copy of DoubleHistogram:");
        assertEqual(histogram, histogram.copy());
        DoubleHistogram withIntHistogram = new DoubleHistogram(DoubleHistogramTest.trackableValueRangeSize, DoubleHistogramTest.numberOfSignificantValueDigits, IntCountsHistogram.class);
        withIntHistogram.recordValue(DoubleHistogramTest.testValueLevel);
        withIntHistogram.recordValue(((DoubleHistogramTest.testValueLevel) * 10));
        withIntHistogram.recordValueWithExpectedInterval(((withIntHistogram.getCurrentHighestTrackableValue()) - 1), 31000);
        System.out.println("Testing copy of DoubleHistogram backed by IntHistogram:");
        assertEqual(withIntHistogram, withIntHistogram.copy());
        DoubleHistogram withShortHistogram = new DoubleHistogram(DoubleHistogramTest.trackableValueRangeSize, DoubleHistogramTest.numberOfSignificantValueDigits, ShortCountsHistogram.class);
        withShortHistogram.recordValue(DoubleHistogramTest.testValueLevel);
        withShortHistogram.recordValue(((DoubleHistogramTest.testValueLevel) * 10));
        withShortHistogram.recordValueWithExpectedInterval(((withShortHistogram.getCurrentHighestTrackableValue()) - 1), 31000);
        System.out.println("Testing copy of DoubleHistogram backed by ShortHistogram:");
        assertEqual(withShortHistogram, withShortHistogram.copy());
        DoubleHistogram withConcurrentHistogram = new DoubleHistogram(DoubleHistogramTest.trackableValueRangeSize, DoubleHistogramTest.numberOfSignificantValueDigits, ConcurrentHistogram.class);
        withConcurrentHistogram.recordValue(DoubleHistogramTest.testValueLevel);
        withConcurrentHistogram.recordValue(((DoubleHistogramTest.testValueLevel) * 10));
        withConcurrentHistogram.recordValueWithExpectedInterval(((withConcurrentHistogram.getCurrentHighestTrackableValue()) - 1), 31000);
        System.out.println("Testing copy of DoubleHistogram backed by ConcurrentHistogram:");
        assertEqual(withConcurrentHistogram, withConcurrentHistogram.copy());
        DoubleHistogram withSyncHistogram = new DoubleHistogram(DoubleHistogramTest.trackableValueRangeSize, DoubleHistogramTest.numberOfSignificantValueDigits, SynchronizedHistogram.class);
        withSyncHistogram.recordValue(DoubleHistogramTest.testValueLevel);
        withSyncHistogram.recordValue(((DoubleHistogramTest.testValueLevel) * 10));
        withSyncHistogram.recordValueWithExpectedInterval(((withSyncHistogram.getCurrentHighestTrackableValue()) - 1), 31000);
        System.out.println("Testing copy of DoubleHistogram backed by SynchronizedHistogram:");
        assertEqual(withSyncHistogram, withSyncHistogram.copy());
    }

    @Test
    public void testCopyInto() throws Exception {
        DoubleHistogram histogram = new DoubleHistogram(DoubleHistogramTest.trackableValueRangeSize, DoubleHistogramTest.numberOfSignificantValueDigits);
        DoubleHistogram targetHistogram = new DoubleHistogram(DoubleHistogramTest.trackableValueRangeSize, DoubleHistogramTest.numberOfSignificantValueDigits);
        histogram.recordValue(DoubleHistogramTest.testValueLevel);
        histogram.recordValue(((DoubleHistogramTest.testValueLevel) * 10));
        histogram.recordValueWithExpectedInterval(((histogram.getCurrentHighestTrackableValue()) - 1), ((histogram.getCurrentHighestTrackableValue()) / 1000));
        System.out.println("Testing copyInto for DoubleHistogram:");
        histogram.copyInto(targetHistogram);
        assertEqual(histogram, targetHistogram);
        histogram.recordValue(((DoubleHistogramTest.testValueLevel) * 20));
        histogram.copyInto(targetHistogram);
        assertEqual(histogram, targetHistogram);
        DoubleHistogram withIntHistogram = new DoubleHistogram(DoubleHistogramTest.trackableValueRangeSize, DoubleHistogramTest.numberOfSignificantValueDigits, IntCountsHistogram.class);
        DoubleHistogram targetWithIntHistogram = new DoubleHistogram(DoubleHistogramTest.trackableValueRangeSize, DoubleHistogramTest.numberOfSignificantValueDigits, IntCountsHistogram.class);
        withIntHistogram.recordValue(DoubleHistogramTest.testValueLevel);
        withIntHistogram.recordValue(((DoubleHistogramTest.testValueLevel) * 10));
        withIntHistogram.recordValueWithExpectedInterval(((withIntHistogram.getCurrentHighestTrackableValue()) - 1), ((withIntHistogram.getCurrentHighestTrackableValue()) / 1000));
        System.out.println("Testing copyInto for DoubleHistogram backed by IntHistogram:");
        withIntHistogram.copyInto(targetWithIntHistogram);
        assertEqual(withIntHistogram, targetWithIntHistogram);
        withIntHistogram.recordValue(((DoubleHistogramTest.testValueLevel) * 20));
        withIntHistogram.copyInto(targetWithIntHistogram);
        assertEqual(withIntHistogram, targetWithIntHistogram);
        DoubleHistogram withShortHistogram = new DoubleHistogram(DoubleHistogramTest.trackableValueRangeSize, DoubleHistogramTest.numberOfSignificantValueDigits, ShortCountsHistogram.class);
        DoubleHistogram targetWithShortHistogram = new DoubleHistogram(DoubleHistogramTest.trackableValueRangeSize, DoubleHistogramTest.numberOfSignificantValueDigits, ShortCountsHistogram.class);
        withShortHistogram.recordValue(DoubleHistogramTest.testValueLevel);
        withShortHistogram.recordValue(((DoubleHistogramTest.testValueLevel) * 10));
        withShortHistogram.recordValueWithExpectedInterval(((withShortHistogram.getCurrentHighestTrackableValue()) - 1), ((withShortHistogram.getCurrentHighestTrackableValue()) / 1000));
        System.out.println("Testing copyInto for DoubleHistogram backed by a ShortHistogram:");
        withShortHistogram.copyInto(targetWithShortHistogram);
        assertEqual(withShortHistogram, targetWithShortHistogram);
        withShortHistogram.recordValue(((DoubleHistogramTest.testValueLevel) * 20));
        withShortHistogram.copyInto(targetWithShortHistogram);
        assertEqual(withShortHistogram, targetWithShortHistogram);
        DoubleHistogram withConcurrentHistogram = new DoubleHistogram(DoubleHistogramTest.trackableValueRangeSize, DoubleHistogramTest.numberOfSignificantValueDigits, ConcurrentHistogram.class);
        DoubleHistogram targetWithConcurrentHistogram = new DoubleHistogram(DoubleHistogramTest.trackableValueRangeSize, DoubleHistogramTest.numberOfSignificantValueDigits, ConcurrentHistogram.class);
        withConcurrentHistogram.recordValue(DoubleHistogramTest.testValueLevel);
        withConcurrentHistogram.recordValue(((DoubleHistogramTest.testValueLevel) * 10));
        withConcurrentHistogram.recordValueWithExpectedInterval(((withConcurrentHistogram.getCurrentHighestTrackableValue()) - 1), ((withConcurrentHistogram.getCurrentHighestTrackableValue()) / 1000));
        System.out.println("Testing copyInto for DoubleHistogram backed by ConcurrentHistogram:");
        withConcurrentHistogram.copyInto(targetWithConcurrentHistogram);
        assertEqual(withConcurrentHistogram, targetWithConcurrentHistogram);
        withConcurrentHistogram.recordValue(((DoubleHistogramTest.testValueLevel) * 20));
        withConcurrentHistogram.copyInto(targetWithConcurrentHistogram);
        assertEqual(withConcurrentHistogram, targetWithConcurrentHistogram);
        ConcurrentDoubleHistogram concurrentHistogram = new ConcurrentDoubleHistogram(DoubleHistogramTest.trackableValueRangeSize, DoubleHistogramTest.numberOfSignificantValueDigits);
        ConcurrentDoubleHistogram targetConcurrentHistogram = new ConcurrentDoubleHistogram(DoubleHistogramTest.trackableValueRangeSize, DoubleHistogramTest.numberOfSignificantValueDigits);
        concurrentHistogram.recordValue(DoubleHistogramTest.testValueLevel);
        concurrentHistogram.recordValue(((DoubleHistogramTest.testValueLevel) * 10));
        concurrentHistogram.recordValueWithExpectedInterval(((concurrentHistogram.getCurrentHighestTrackableValue()) - 1), ((concurrentHistogram.getCurrentHighestTrackableValue()) / 1000));
        System.out.println("Testing copyInto for actual ConcurrentHistogram:");
        concurrentHistogram.copyInto(targetConcurrentHistogram);
        assertEqual(concurrentHistogram, targetConcurrentHistogram);
        concurrentHistogram.recordValue(((DoubleHistogramTest.testValueLevel) * 20));
        concurrentHistogram.copyInto(targetConcurrentHistogram);
        assertEqual(concurrentHistogram, targetConcurrentHistogram);
        DoubleHistogram withSyncHistogram = new DoubleHistogram(DoubleHistogramTest.trackableValueRangeSize, DoubleHistogramTest.numberOfSignificantValueDigits, SynchronizedHistogram.class);
        DoubleHistogram targetWithSyncHistogram = new DoubleHistogram(DoubleHistogramTest.trackableValueRangeSize, DoubleHistogramTest.numberOfSignificantValueDigits, SynchronizedHistogram.class);
        withSyncHistogram.recordValue(DoubleHistogramTest.testValueLevel);
        withSyncHistogram.recordValue(((DoubleHistogramTest.testValueLevel) * 10));
        withSyncHistogram.recordValueWithExpectedInterval(((withSyncHistogram.getCurrentHighestTrackableValue()) - 1), ((withSyncHistogram.getCurrentHighestTrackableValue()) / 1000));
        System.out.println("Testing copyInto for DoubleHistogram backed by SynchronizedHistogram:");
        withSyncHistogram.copyInto(targetWithSyncHistogram);
        assertEqual(withSyncHistogram, targetWithSyncHistogram);
        withSyncHistogram.recordValue(((DoubleHistogramTest.testValueLevel) * 20));
        withSyncHistogram.copyInto(targetWithSyncHistogram);
        assertEqual(withSyncHistogram, targetWithSyncHistogram);
        SynchronizedDoubleHistogram syncHistogram = new SynchronizedDoubleHistogram(DoubleHistogramTest.trackableValueRangeSize, DoubleHistogramTest.numberOfSignificantValueDigits);
        SynchronizedDoubleHistogram targetSyncHistogram = new SynchronizedDoubleHistogram(DoubleHistogramTest.trackableValueRangeSize, DoubleHistogramTest.numberOfSignificantValueDigits);
        syncHistogram.recordValue(DoubleHistogramTest.testValueLevel);
        syncHistogram.recordValue(((DoubleHistogramTest.testValueLevel) * 10));
        syncHistogram.recordValueWithExpectedInterval(((syncHistogram.getCurrentHighestTrackableValue()) - 1), ((syncHistogram.getCurrentHighestTrackableValue()) / 1000));
        System.out.println("Testing copyInto for actual SynchronizedDoubleHistogram:");
        syncHistogram.copyInto(targetSyncHistogram);
        assertEqual(syncHistogram, targetSyncHistogram);
        syncHistogram.recordValue(((DoubleHistogramTest.testValueLevel) * 20));
        syncHistogram.copyInto(targetSyncHistogram);
        assertEqual(syncHistogram, targetSyncHistogram);
    }

    @Test
    public void testResize() {
        // Verify resize behvaior for various underlying internal integer histogram implementations:
        genericResizeTest(new DoubleHistogram(2));
        genericResizeTest(new DoubleHistogram(2, IntCountsHistogram.class));
        genericResizeTest(new DoubleHistogram(2, ShortCountsHistogram.class));
        genericResizeTest(new DoubleHistogram(2, ConcurrentHistogram.class));
        genericResizeTest(new DoubleHistogram(2, SynchronizedHistogram.class));
    }
}

