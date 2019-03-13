/**
 * HistogramDataAccessTest.java
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
public class DoubleHistogramDataAccessTest {
    static final long highestTrackableValue = (3600L * 1000) * 1000;// 1 hour in usec units


    static final int numberOfSignificantValueDigits = 3;// Maintain at least 3 decimal points of accuracy


    static final DoubleHistogram histogram;

    static final DoubleHistogram scaledHistogram;

    static final DoubleHistogram rawHistogram;

    static final DoubleHistogram scaledRawHistogram;

    static final DoubleHistogram postCorrectedHistogram;

    static final DoubleHistogram postCorrectedScaledHistogram;

    static {
        histogram = new DoubleHistogram(DoubleHistogramDataAccessTest.highestTrackableValue, DoubleHistogramDataAccessTest.numberOfSignificantValueDigits);
        scaledHistogram = new DoubleHistogram(((DoubleHistogramDataAccessTest.highestTrackableValue) / 2), DoubleHistogramDataAccessTest.numberOfSignificantValueDigits);
        rawHistogram = new DoubleHistogram(DoubleHistogramDataAccessTest.highestTrackableValue, DoubleHistogramDataAccessTest.numberOfSignificantValueDigits);
        scaledRawHistogram = new DoubleHistogram(((DoubleHistogramDataAccessTest.highestTrackableValue) / 2), DoubleHistogramDataAccessTest.numberOfSignificantValueDigits);
        // Log hypothetical scenario: 100 seconds of "perfect" 1msec results, sampled
        // 100 times per second (10,000 results), followed by a 100 second pause with
        // a single (100 second) recorded result. Recording is done indicating an expected
        // interval between samples of 10 msec:
        for (int i = 0; i < 10000; i++) {
            /* 1 msec */
            /* 10 msec expected interval */
            DoubleHistogramDataAccessTest.histogram.recordValueWithExpectedInterval(1000, 10000);
            /* 1 msec */
            /* 10 msec expected interval */
            DoubleHistogramDataAccessTest.scaledHistogram.recordValueWithExpectedInterval((1000 * 512), (10000 * 512));
            /* 1 msec */
            DoubleHistogramDataAccessTest.rawHistogram.recordValue(1000);
            /* 1 msec */
            DoubleHistogramDataAccessTest.scaledRawHistogram.recordValue((1000 * 512));
        }
        /* 100 sec */
        /* 10 msec expected interval */
        DoubleHistogramDataAccessTest.histogram.recordValueWithExpectedInterval(100000000L, 10000);
        /* 100 sec */
        /* 10 msec expected interval */
        DoubleHistogramDataAccessTest.scaledHistogram.recordValueWithExpectedInterval((100000000L * 512), (10000 * 512));
        /* 100 sec */
        DoubleHistogramDataAccessTest.rawHistogram.recordValue(100000000L);
        /* 100 sec */
        DoubleHistogramDataAccessTest.scaledRawHistogram.recordValue((100000000L * 512));
        postCorrectedHistogram = /* 10 msec expected interval */
        DoubleHistogramDataAccessTest.rawHistogram.copyCorrectedForCoordinatedOmission(10000);
        postCorrectedScaledHistogram = /* 10 msec expected interval */
        DoubleHistogramDataAccessTest.scaledRawHistogram.copyCorrectedForCoordinatedOmission((10000 * 512));
    }

    @Test
    public void testScalingEquivalence() {
        Assert.assertEquals("averages should be equivalent", ((DoubleHistogramDataAccessTest.histogram.getMean()) * 512), DoubleHistogramDataAccessTest.scaledHistogram.getMean(), ((DoubleHistogramDataAccessTest.scaledHistogram.getMean()) * 1.0E-6));
        Assert.assertEquals("total count should be the same", DoubleHistogramDataAccessTest.histogram.getTotalCount(), DoubleHistogramDataAccessTest.scaledHistogram.getTotalCount());
        Assert.assertEquals("99%'iles should be equivalent", DoubleHistogramDataAccessTest.scaledHistogram.highestEquivalentValue(((DoubleHistogramDataAccessTest.histogram.getValueAtPercentile(99.0)) * 512)), DoubleHistogramDataAccessTest.scaledHistogram.highestEquivalentValue(DoubleHistogramDataAccessTest.scaledHistogram.getValueAtPercentile(99.0)), ((DoubleHistogramDataAccessTest.scaledHistogram.highestEquivalentValue(DoubleHistogramDataAccessTest.scaledHistogram.getValueAtPercentile(99.0))) * 1.0E-6));
        Assert.assertEquals("Max should be equivalent", DoubleHistogramDataAccessTest.scaledHistogram.highestEquivalentValue(((DoubleHistogramDataAccessTest.histogram.getMaxValue()) * 512)), DoubleHistogramDataAccessTest.scaledHistogram.getMaxValue(), ((DoubleHistogramDataAccessTest.scaledHistogram.getMaxValue()) * 1.0E-6));
        // Same for post-corrected:
        Assert.assertEquals("averages should be equivalent", ((DoubleHistogramDataAccessTest.histogram.getMean()) * 512), DoubleHistogramDataAccessTest.scaledHistogram.getMean(), ((DoubleHistogramDataAccessTest.scaledHistogram.getMean()) * 1.0E-6));
        Assert.assertEquals("total count should be the same", DoubleHistogramDataAccessTest.postCorrectedHistogram.getTotalCount(), DoubleHistogramDataAccessTest.postCorrectedScaledHistogram.getTotalCount());
        Assert.assertEquals("99%'iles should be equivalent", ((DoubleHistogramDataAccessTest.postCorrectedHistogram.lowestEquivalentValue(DoubleHistogramDataAccessTest.postCorrectedHistogram.getValueAtPercentile(99.0))) * 512), DoubleHistogramDataAccessTest.postCorrectedScaledHistogram.lowestEquivalentValue(DoubleHistogramDataAccessTest.postCorrectedScaledHistogram.getValueAtPercentile(99.0)), ((DoubleHistogramDataAccessTest.postCorrectedScaledHistogram.lowestEquivalentValue(DoubleHistogramDataAccessTest.postCorrectedScaledHistogram.getValueAtPercentile(99.0))) * 1.0E-6));
        Assert.assertEquals("Max should be equivalent", DoubleHistogramDataAccessTest.postCorrectedScaledHistogram.highestEquivalentValue(((DoubleHistogramDataAccessTest.postCorrectedHistogram.getMaxValue()) * 512)), DoubleHistogramDataAccessTest.postCorrectedScaledHistogram.getMaxValue(), ((DoubleHistogramDataAccessTest.postCorrectedScaledHistogram.getMaxValue()) * 1.0E-6));
    }

    @Test
    public void testPreVsPostCorrectionValues() {
        // Loop both ways (one would be enough, but good practice just for fun:
        Assert.assertEquals("pre and post corrected count totals ", DoubleHistogramDataAccessTest.histogram.getTotalCount(), DoubleHistogramDataAccessTest.postCorrectedHistogram.getTotalCount());
        // The following comparison loops would have worked in a perfect accuracy world, but since post
        // correction is done based on the value extracted from the bucket, and the during-recording is done
        // based on the actual (not pixelized) value, there will be subtle differences due to roundoffs:
        // for (HistogramIterationValue v : histogram.allValues()) {
        // long preCorrectedCount = v.getCountAtValueIteratedTo();
        // long postCorrectedCount = postCorrectedHistogram.getCountAtValue(v.getValueIteratedTo());
        // Assert.assertEquals("pre and post corrected count at value " + v.getValueIteratedTo(),
        // preCorrectedCount, postCorrectedCount);
        // }
        // 
        // for (HistogramIterationValue v : postCorrectedHistogram.allValues()) {
        // long preCorrectedCount = v.getCountAtValueIteratedTo();
        // long postCorrectedCount = histogram.getCountAtValue(v.getValueIteratedTo());
        // Assert.assertEquals("pre and post corrected count at value " + v.getValueIteratedTo(),
        // preCorrectedCount, postCorrectedCount);
        // }
    }

    @Test
    public void testGetTotalCount() throws Exception {
        // The overflow value should count in the total count:
        Assert.assertEquals("Raw total count is 10,001", 10001L, DoubleHistogramDataAccessTest.rawHistogram.getTotalCount());
        Assert.assertEquals("Total count is 20,000", 20000L, DoubleHistogramDataAccessTest.histogram.getTotalCount());
    }

    @Test
    public void testGetMaxValue() throws Exception {
        Assert.assertTrue(DoubleHistogramDataAccessTest.histogram.valuesAreEquivalent(((100L * 1000) * 1000), DoubleHistogramDataAccessTest.histogram.getMaxValue()));
    }

    @Test
    public void testGetMinValue() throws Exception {
        Assert.assertTrue(DoubleHistogramDataAccessTest.histogram.valuesAreEquivalent(1000, DoubleHistogramDataAccessTest.histogram.getMinValue()));
    }

    @Test
    public void testGetMean() throws Exception {
        double expectedRawMean = ((10000.0 * 1000) + (1.0 * 100000000)) / 10001;/* direct avg. of raw results */

        double expectedMean = (1000.0 + 5.0E7) / 2;/* avg. 1 msec for half the time, and 50 sec for other half */

        // We expect to see the mean to be accurate to ~3 decimal points (~0.1%):
        Assert.assertEquals((("Raw mean is " + expectedRawMean) + " +/- 0.1%"), expectedRawMean, DoubleHistogramDataAccessTest.rawHistogram.getMean(), (expectedRawMean * 0.001));
        Assert.assertEquals((("Mean is " + expectedMean) + " +/- 0.1%"), expectedMean, DoubleHistogramDataAccessTest.histogram.getMean(), (expectedMean * 0.001));
    }

    @Test
    public void testGetStdDeviation() throws Exception {
        double expectedRawMean = ((10000.0 * 1000) + (1.0 * 100000000)) / 10001;/* direct avg. of raw results */

        double expectedRawStdDev = Math.sqrt((((10000.0 * (Math.pow((1000.0 - expectedRawMean), 2))) + (Math.pow((1.0E8 - expectedRawMean), 2))) / 10001));
        double expectedMean = (1000.0 + 5.0E7) / 2;/* avg. 1 msec for half the time, and 50 sec for other half */

        double expectedSquareDeviationSum = 10000 * (Math.pow((1000.0 - expectedMean), 2));
        for (long value = 10000; value <= 100000000; value += 10000) {
            expectedSquareDeviationSum += Math.pow((value - expectedMean), 2);
        }
        double expectedStdDev = Math.sqrt((expectedSquareDeviationSum / 20000));
        // We expect to see the standard deviations to be accurate to ~3 decimal points (~0.1%):
        Assert.assertEquals((("Raw standard deviation is " + expectedRawStdDev) + " +/- 0.1%"), expectedRawStdDev, DoubleHistogramDataAccessTest.rawHistogram.getStdDeviation(), (expectedRawStdDev * 0.001));
        Assert.assertEquals((("Standard deviation is " + expectedStdDev) + " +/- 0.1%"), expectedStdDev, DoubleHistogramDataAccessTest.histogram.getStdDeviation(), (expectedStdDev * 0.001));
    }

    @Test
    public void testGetValueAtPercentile() throws Exception {
        Assert.assertEquals("raw 30%'ile is 1 msec +/- 0.1%", 1000.0, ((double) (DoubleHistogramDataAccessTest.rawHistogram.getValueAtPercentile(30.0))), (1000.0 * 0.001));
        Assert.assertEquals("raw 99%'ile is 1 msec +/- 0.1%", 1000.0, ((double) (DoubleHistogramDataAccessTest.rawHistogram.getValueAtPercentile(99.0))), (1000.0 * 0.001));
        Assert.assertEquals("raw 99.99%'ile is 1 msec +/- 0.1%", 1000.0, ((double) (DoubleHistogramDataAccessTest.rawHistogram.getValueAtPercentile(99.99))), (1000.0 * 0.001));
        Assert.assertEquals("raw 99.999%'ile is 100 sec +/- 0.1%", 1.0E8, ((double) (DoubleHistogramDataAccessTest.rawHistogram.getValueAtPercentile(99.999))), (1.0E8 * 0.001));
        Assert.assertEquals("raw 100%'ile is 100 sec +/- 0.1%", 1.0E8, ((double) (DoubleHistogramDataAccessTest.rawHistogram.getValueAtPercentile(100.0))), (1.0E8 * 0.001));
        Assert.assertEquals("30%'ile is 1 msec +/- 0.1%", 1000.0, ((double) (DoubleHistogramDataAccessTest.histogram.getValueAtPercentile(30.0))), (1000.0 * 0.001));
        Assert.assertEquals("50%'ile is 1 msec +/- 0.1%", 1000.0, ((double) (DoubleHistogramDataAccessTest.histogram.getValueAtPercentile(50.0))), (1000.0 * 0.001));
        Assert.assertEquals("75%'ile is 50 sec +/- 0.1%", 5.0E7, ((double) (DoubleHistogramDataAccessTest.histogram.getValueAtPercentile(75.0))), (5.0E7 * 0.001));
        Assert.assertEquals("90%'ile is 80 sec +/- 0.1%", 8.0E7, ((double) (DoubleHistogramDataAccessTest.histogram.getValueAtPercentile(90.0))), (8.0E7 * 0.001));
        Assert.assertEquals("99%'ile is 98 sec +/- 0.1%", 9.8E7, ((double) (DoubleHistogramDataAccessTest.histogram.getValueAtPercentile(99.0))), (9.8E7 * 0.001));
        Assert.assertEquals("99.999%'ile is 100 sec +/- 0.1%", 1.0E8, ((double) (DoubleHistogramDataAccessTest.histogram.getValueAtPercentile(99.999))), (1.0E8 * 0.001));
        Assert.assertEquals("100%'ile is 100 sec +/- 0.1%", 1.0E8, ((double) (DoubleHistogramDataAccessTest.histogram.getValueAtPercentile(100.0))), (1.0E8 * 0.001));
    }

    @Test
    public void testGetValueAtPercentileForLargeHistogram() {
        long largestValue = 1000000000000L;
        Histogram h = new Histogram(largestValue, 5);
        h.recordValue(largestValue);
        Assert.assertTrue(((h.getValueAtPercentile(100.0)) > 0));
    }

    @Test
    public void testGetPercentileAtOrBelowValue() throws Exception {
        Assert.assertEquals("Raw percentile at or below 5 msec is 99.99% +/- 0.0001", 99.99, DoubleHistogramDataAccessTest.rawHistogram.getPercentileAtOrBelowValue(5000), 1.0E-4);
        Assert.assertEquals("Percentile at or below 5 msec is 50% +/- 0.0001%", 50.0, DoubleHistogramDataAccessTest.histogram.getPercentileAtOrBelowValue(5000), 1.0E-4);
        Assert.assertEquals("Percentile at or below 100 sec is 100% +/- 0.0001%", 100.0, DoubleHistogramDataAccessTest.histogram.getPercentileAtOrBelowValue(100000000L), 1.0E-4);
    }

    @Test
    public void testGetCountBetweenValues() throws Exception {
        Assert.assertEquals("Count of raw values between 1 msec and 1 msec is 1", 10000, DoubleHistogramDataAccessTest.rawHistogram.getCountBetweenValues(1000L, 1000L), (10000 * 1.0E-6));
        Assert.assertEquals("Count of raw values between 5 msec and 150 sec is 1", 1, DoubleHistogramDataAccessTest.rawHistogram.getCountBetweenValues(5000L, 150000000L), (1 * 1.0E-6));
        Assert.assertEquals("Count of values between 5 msec and 150 sec is 10,000", 10000, DoubleHistogramDataAccessTest.histogram.getCountBetweenValues(5000L, 150000000L), (10000 * 1.0E-6));
    }

    @Test
    public void testGetCountAtValue() throws Exception {
        Assert.assertEquals("Count of raw values at 10 msec is 0", 0, DoubleHistogramDataAccessTest.rawHistogram.getCountBetweenValues(10000L, 10010L), 1.0E-6);
        Assert.assertEquals("Count of values at 10 msec is 0", 1, DoubleHistogramDataAccessTest.histogram.getCountBetweenValues(10000L, 10010L), 1.0E-6);
        Assert.assertEquals("Count of raw values at 1 msec is 10,000", 10000, DoubleHistogramDataAccessTest.rawHistogram.getCountAtValue(1000L), (10000 * 1.0E-6));
        Assert.assertEquals("Count of values at 1 msec is 10,000", 10000, DoubleHistogramDataAccessTest.histogram.getCountAtValue(1000L), (10000 * 1.0E-6));
    }

    @Test
    public void testPercentiles() throws Exception {
        int i = 0;
        for (DoubleHistogramIterationValue v : /* ticks per half */
        DoubleHistogramDataAccessTest.histogram.percentiles(5)) {
            Assert.assertEquals(((((((((((((((((((("i = " + i) + ", Value at Iterated-to Percentile is the same as the matching getValueAtPercentile():\n") + "getPercentileLevelIteratedTo = ") + (v.getPercentileLevelIteratedTo())) + "\ngetValueIteratedTo = ") + (v.getValueIteratedTo())) + "\ngetValueIteratedFrom = ") + (v.getValueIteratedFrom())) + "\ngetValueAtPercentile(getPercentileLevelIteratedTo()) = ") + (DoubleHistogramDataAccessTest.histogram.getValueAtPercentile(v.getPercentileLevelIteratedTo()))) + "\ngetPercentile = ") + (v.getPercentile())) + "\ngetValueAtPercentile(getPercentile())") + (DoubleHistogramDataAccessTest.histogram.getValueAtPercentile(v.getPercentile()))) + "\nequivalent1 = ") + (DoubleHistogramDataAccessTest.histogram.highestEquivalentValue(DoubleHistogramDataAccessTest.histogram.getValueAtPercentile(v.getPercentileLevelIteratedTo())))) + "\nequivalent2 = ") + (DoubleHistogramDataAccessTest.histogram.highestEquivalentValue(DoubleHistogramDataAccessTest.histogram.getValueAtPercentile(v.getPercentile())))) + "\n"), v.getValueIteratedTo(), DoubleHistogramDataAccessTest.histogram.highestEquivalentValue(DoubleHistogramDataAccessTest.histogram.getValueAtPercentile(v.getPercentile())), ((v.getValueIteratedTo()) * 0.001));
        }
    }

    @Test
    public void testLinearBucketValues() throws Exception {
        int index = 0;
        // Note that using linear buckets should work "as expected" as long as the number of linear buckets
        // is lower than the resolution level determined by largestValueWithSingleUnitResolution
        // (2000 in this case). Above that count, some of the linear buckets can end up rounded up in size
        // (to the nearest local resolution unit level), which can result in a smaller number of buckets that
        // expected covering the range.
        // Iterate raw data using linear buckets of 100 msec each.
        for (DoubleHistogramIterationValue v : DoubleHistogramDataAccessTest.rawHistogram.linearBucketValues(100000)) {
            long countAddedInThisBucket = v.getCountAddedInThisIterationStep();
            if (index == 0) {
                Assert.assertEquals("Raw Linear 100 msec bucket # 0 added a count of 10000", 10000, countAddedInThisBucket);
            } else
                if (index == 999) {
                    Assert.assertEquals("Raw Linear 100 msec bucket # 999 added a count of 1", 1, countAddedInThisBucket);
                } else {
                    Assert.assertEquals((("Raw Linear 100 msec bucket # " + index) + " added a count of 0"), 0, countAddedInThisBucket);
                }

            index++;
        }
        Assert.assertEquals(1000, index);
        index = 0;
        long totalAddedCounts = 0;
        // Iterate data using linear buckets of 10 msec each.
        for (DoubleHistogramIterationValue v : DoubleHistogramDataAccessTest.histogram.linearBucketValues(10000)) {
            long countAddedInThisBucket = v.getCountAddedInThisIterationStep();
            if (index == 0) {
                Assert.assertEquals((((("Linear 1 sec bucket # 0 [" + (v.getValueIteratedFrom())) + "..") + (v.getValueIteratedTo())) + "] added a count of 10000"), 10000, countAddedInThisBucket);
            }
            // Because value resolution is low enough (3 digits) that multiple linear buckets will end up
            // residing in a single value-equivalent range, some linear buckets will have counts of 2 or
            // more, and some will have 0 (when the first bucket in the equivalent range was the one that
            // got the total count bump).
            // However, we can still verify the sum of counts added in all the buckets...
            totalAddedCounts += v.getCountAddedInThisIterationStep();
            index++;
        }
        Assert.assertEquals("There should be 10000 linear buckets of size 10000 usec between 0 and 100 sec.", 10000, index);
        Assert.assertEquals("Total added counts should be 20000", 20000, totalAddedCounts);
        index = 0;
        totalAddedCounts = 0;
        // Iterate data using linear buckets of 1 msec each.
        for (DoubleHistogramIterationValue v : DoubleHistogramDataAccessTest.histogram.linearBucketValues(1000)) {
            long countAddedInThisBucket = v.getCountAddedInThisIterationStep();
            if (index == 1) {
                Assert.assertEquals((((("Linear 1 sec bucket # 0 [" + (v.getValueIteratedFrom())) + "..") + (v.getValueIteratedTo())) + "] added a count of 10000"), 10000, countAddedInThisBucket);
            }
            // Because value resolution is low enough (3 digits) that multiple linear buckets will end up
            // residing in a single value-equivalent range, some linear buckets will have counts of 2 or
            // more, and some will have 0 (when the first bucket in the equivalent range was the one that
            // got the total count bump).
            // However, we can still verify the sum of counts added in all the buckets...
            totalAddedCounts += v.getCountAddedInThisIterationStep();
            index++;
        }
        // You may ask "why 100007 and not 100000?" for the value below? The answer is that at this fine
        // a linear stepping resolution, the final populated sub-bucket (at 100 seconds with 3 decimal
        // point resolution) is larger than our liner stepping, and holds more than one linear 1 msec
        // step in it.
        // Since we only know we're done with linear iteration when the next iteration step will step
        // out of the last populated bucket, there is not way to tell if the iteration should stop at
        // 100000 or 100007 steps. The proper thing to do is to run to the end of the sub-bucket quanta...
        Assert.assertEquals("There should be 100007 linear buckets of size 1000 usec between 0 and 100 sec.", 100007, index);
        Assert.assertEquals("Total added counts should be 20000", 20000, totalAddedCounts);
    }

    @Test
    public void testLogarithmicBucketValues() throws Exception {
        int index = 0;
        // Iterate raw data using logarithmic buckets starting at 10 msec.
        for (DoubleHistogramIterationValue v : DoubleHistogramDataAccessTest.rawHistogram.logarithmicBucketValues(10000, 2)) {
            long countAddedInThisBucket = v.getCountAddedInThisIterationStep();
            if (index == 0) {
                Assert.assertEquals("Raw Logarithmic 10 msec bucket # 0 added a count of 10000", 10000, countAddedInThisBucket);
            } else
                if (index == 14) {
                    Assert.assertEquals("Raw Logarithmic 10 msec bucket # 14 added a count of 1", 1, countAddedInThisBucket);
                } else {
                    Assert.assertEquals((("Raw Logarithmic 100 msec bucket # " + index) + " added a count of 0"), 0, countAddedInThisBucket);
                }

            index++;
        }
        Assert.assertEquals(14, (index - 1));
        index = 0;
        long totalAddedCounts = 0;
        // Iterate data using linear buckets of 1 sec each.
        for (DoubleHistogramIterationValue v : DoubleHistogramDataAccessTest.histogram.logarithmicBucketValues(10000, 2)) {
            long countAddedInThisBucket = v.getCountAddedInThisIterationStep();
            if (index == 0) {
                Assert.assertEquals((((("Logarithmic 10 msec bucket # 0 [" + (v.getValueIteratedFrom())) + "..") + (v.getValueIteratedTo())) + "] added a count of 10000"), 10000, countAddedInThisBucket);
            }
            totalAddedCounts += v.getCountAddedInThisIterationStep();
            index++;
        }
        Assert.assertEquals("There should be 14 Logarithmic buckets of size 10000 usec between 0 and 100 sec.", 14, (index - 1));
        Assert.assertEquals("Total added counts should be 20000", 20000, totalAddedCounts);
    }

    @Test
    public void testRecordedValues() throws Exception {
        int index = 0;
        // Iterate raw data by stepping through every value that has a count recorded:
        for (DoubleHistogramIterationValue v : DoubleHistogramDataAccessTest.rawHistogram.recordedValues()) {
            long countAddedInThisBucket = v.getCountAddedInThisIterationStep();
            if (index == 0) {
                Assert.assertEquals("Raw recorded value bucket # 0 added a count of 10000", 10000, countAddedInThisBucket);
            } else {
                Assert.assertEquals((("Raw recorded value bucket # " + index) + " added a count of 1"), 1, countAddedInThisBucket);
            }
            index++;
        }
        Assert.assertEquals(2, index);
        index = 0;
        long totalAddedCounts = 0;
        // Iterate data using linear buckets of 1 sec each.
        for (DoubleHistogramIterationValue v : DoubleHistogramDataAccessTest.histogram.recordedValues()) {
            long countAddedInThisBucket = v.getCountAddedInThisIterationStep();
            if (index == 0) {
                Assert.assertEquals((((("Recorded bucket # 0 [" + (v.getValueIteratedFrom())) + "..") + (v.getValueIteratedTo())) + "] added a count of 10000"), 10000, countAddedInThisBucket);
            }
            Assert.assertTrue((("The count in recorded bucket #" + index) + " is not 0"), ((v.getCountAtValueIteratedTo()) != 0));
            Assert.assertEquals((("The count in recorded bucket #" + index) + " is exactly the amount added since the last iteration "), v.getCountAtValueIteratedTo(), v.getCountAddedInThisIterationStep());
            totalAddedCounts += v.getCountAddedInThisIterationStep();
            index++;
        }
        Assert.assertEquals("Total added counts should be 20000", 20000, totalAddedCounts);
    }

    @Test
    public void testAllValues() throws Exception {
        int index = 0;
        double latestValueAtIndex = 0;
        double totalCountToThisPoint = 0;
        double totalValueToThisPoint = 0;
        // Iterate raw data by stepping through every value that ahs a count recorded:
        for (DoubleHistogramIterationValue v : DoubleHistogramDataAccessTest.rawHistogram.allValues()) {
            long countAddedInThisBucket = v.getCountAddedInThisIterationStep();
            if (index == 2000) {
                Assert.assertEquals("Raw allValues bucket # 0 added a count of 10000", 10000, countAddedInThisBucket);
            } else
                if (DoubleHistogramDataAccessTest.histogram.valuesAreEquivalent(v.getValueIteratedTo(), 100000000)) {
                    Assert.assertEquals((("Raw allValues value bucket # " + index) + " added a count of 1"), 1, countAddedInThisBucket);
                } else {
                    Assert.assertEquals((("Raw allValues value bucket # " + index) + " added a count of 0"), 0, countAddedInThisBucket);
                }

            latestValueAtIndex = v.getValueIteratedTo();
            totalCountToThisPoint += v.getCountAtValueIteratedTo();
            Assert.assertEquals("total Count should match", totalCountToThisPoint, v.getTotalCountToThisValue(), 1.0E-8);
            totalValueToThisPoint += (v.getCountAtValueIteratedTo()) * latestValueAtIndex;
            Assert.assertEquals("total Value should match", totalValueToThisPoint, v.getTotalValueToThisValue(), 1.0E-8);
            index++;
        }
        Assert.assertEquals("index should be equal to countsArrayLength", DoubleHistogramDataAccessTest.histogram.integerValuesHistogram.countsArrayLength, index);
        index = 0;
        long totalAddedCounts = 0;
        // Iterate data using linear buckets of 1 sec each.
        for (DoubleHistogramIterationValue v : DoubleHistogramDataAccessTest.histogram.allValues()) {
            long countAddedInThisBucket = v.getCountAddedInThisIterationStep();
            if (index == 2000) {
                Assert.assertEquals((((("AllValues bucket # 0 [" + (v.getValueIteratedFrom())) + "..") + (v.getValueIteratedTo())) + "] added a count of 10000"), 10000, countAddedInThisBucket);
            }
            Assert.assertEquals((("The count in AllValues bucket #" + index) + " is exactly the amount added since the last iteration "), v.getCountAtValueIteratedTo(), v.getCountAddedInThisIterationStep());
            totalAddedCounts += v.getCountAddedInThisIterationStep();
            index++;
        }
        Assert.assertEquals("index should be equal to countsArrayLength", DoubleHistogramDataAccessTest.histogram.integerValuesHistogram.countsArrayLength, index);
        Assert.assertEquals("Total added counts should be 20000", 20000, totalAddedCounts);
    }
}

