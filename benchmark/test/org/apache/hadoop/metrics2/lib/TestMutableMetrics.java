/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.hadoop.metrics2.lib;


import java.util.Map;
import java.util.Random;
import java.util.concurrent.CountDownLatch;
import org.apache.hadoop.metrics2.MetricsRecordBuilder;
import org.apache.hadoop.metrics2.util.Quantile;
import org.apache.hadoop.test.MetricsAsserts;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.AdditionalMatchers;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static MutableQuantiles.quantiles;


/**
 * Test metrics record builder interface and mutable metrics
 */
public class TestMutableMetrics {
    private static final Logger LOG = LoggerFactory.getLogger(TestMutableMetrics.class);

    private final double EPSILON = 1.0E-42;

    /**
     * Test the snapshot method
     */
    @Test
    public void testSnapshot() {
        MetricsRecordBuilder mb = MetricsAsserts.mockMetricsRecordBuilder();
        MetricsRegistry registry = new MetricsRegistry("test");
        registry.newCounter("c1", "int counter", 1);
        registry.newCounter("c2", "long counter", 2L);
        registry.newGauge("g1", "int gauge", 3);
        registry.newGauge("g2", "long gauge", 4L);
        registry.newGauge("g3", "float gauge", 5.0F);
        registry.newStat("s1", "stat", "Ops", "Time", true).add(0);
        registry.newRate("s2", "stat", false).add(0);
        registry.snapshot(mb, true);
        MutableStat s2 = ((MutableStat) (registry.get("s2")));
        s2.snapshot(mb, true);// should get the same back.

        s2.add(1);
        s2.snapshot(mb, true);// should get new interval values back

        Mockito.verify(mb).addCounter(Interns.info("c1", "int counter"), 1);
        Mockito.verify(mb).addCounter(Interns.info("c2", "long counter"), 2L);
        Mockito.verify(mb).addGauge(Interns.info("g1", "int gauge"), 3);
        Mockito.verify(mb).addGauge(Interns.info("g2", "long gauge"), 4L);
        Mockito.verify(mb).addGauge(Interns.info("g3", "float gauge"), 5.0F);
        Mockito.verify(mb).addCounter(Interns.info("S1NumOps", "Number of ops for stat"), 1L);
        Mockito.verify(mb).addGauge(AdditionalMatchers.eq(Interns.info("S1AvgTime", "Average time for stat")), AdditionalMatchers.eq(0.0, EPSILON));
        Mockito.verify(mb).addGauge(AdditionalMatchers.eq(Interns.info("S1StdevTime", "Standard deviation of time for stat")), AdditionalMatchers.eq(0.0, EPSILON));
        Mockito.verify(mb).addGauge(AdditionalMatchers.eq(Interns.info("S1IMinTime", "Interval min time for stat")), AdditionalMatchers.eq(0.0, EPSILON));
        Mockito.verify(mb).addGauge(AdditionalMatchers.eq(Interns.info("S1IMaxTime", "Interval max time for stat")), AdditionalMatchers.eq(0.0, EPSILON));
        Mockito.verify(mb).addGauge(AdditionalMatchers.eq(Interns.info("S1MinTime", "Min time for stat")), AdditionalMatchers.eq(0.0, EPSILON));
        Mockito.verify(mb).addGauge(AdditionalMatchers.eq(Interns.info("S1MaxTime", "Max time for stat")), AdditionalMatchers.eq(0.0, EPSILON));
        Mockito.verify(mb).addGauge(AdditionalMatchers.eq(Interns.info("S1INumOps", "Interval number of ops for stat")), ArgumentMatchers.eq(1L));
        Mockito.verify(mb, Mockito.times(2)).addCounter(Interns.info("S2NumOps", "Number of ops for stat"), 1L);
        Mockito.verify(mb, Mockito.times(2)).addGauge(AdditionalMatchers.eq(Interns.info("S2AvgTime", "Average time for stat")), AdditionalMatchers.eq(0.0, EPSILON));
        Mockito.verify(mb).addCounter(Interns.info("S2NumOps", "Number of ops for stat"), 2L);
        Mockito.verify(mb).addGauge(AdditionalMatchers.eq(Interns.info("S2AvgTime", "Average time for stat")), AdditionalMatchers.eq(1.0, EPSILON));
        // Add one more sample to s1 and verify that total number of ops
        // has increased to 2, but interval number is 1 for both intervals.
        MutableStat s1 = ((MutableStat) (registry.get("s1")));
        s1.add(0);
        registry.snapshot(mb, true);
        Mockito.verify(mb).addCounter(Interns.info("S1NumOps", "Number of ops for stat"), 2L);
        Mockito.verify(mb, Mockito.times(2)).addGauge(AdditionalMatchers.eq(Interns.info("S1INumOps", "Interval number of ops for stat")), ArgumentMatchers.eq(1L));
    }

    interface TestProtocol {
        void foo();

        void bar();
    }

    @Test
    public void testMutableRates() {
        MetricsRecordBuilder rb = MetricsAsserts.mockMetricsRecordBuilder();
        MetricsRegistry registry = new MetricsRegistry("test");
        MutableRates rates = new MutableRates(registry);
        rates.init(TestMutableMetrics.TestProtocol.class);
        registry.snapshot(rb, false);
        MetricsAsserts.assertCounter("FooNumOps", 0L, rb);
        MetricsAsserts.assertGauge("FooAvgTime", 0.0, rb);
        MetricsAsserts.assertCounter("BarNumOps", 0L, rb);
        MetricsAsserts.assertGauge("BarAvgTime", 0.0, rb);
    }

    @Test
    public void testMutableRatesWithAggregationInit() {
        MetricsRecordBuilder rb = MetricsAsserts.mockMetricsRecordBuilder();
        MutableRatesWithAggregation rates = new MutableRatesWithAggregation();
        rates.init(TestMutableMetrics.TestProtocol.class);
        rates.snapshot(rb, false);
        MetricsAsserts.assertCounter("FooNumOps", 0L, rb);
        MetricsAsserts.assertGauge("FooAvgTime", 0.0, rb);
        MetricsAsserts.assertCounter("BarNumOps", 0L, rb);
        MetricsAsserts.assertGauge("BarAvgTime", 0.0, rb);
    }

    @Test
    public void testMutableRatesWithAggregationSingleThread() {
        MutableRatesWithAggregation rates = new MutableRatesWithAggregation();
        rates.add("foo", 1);
        rates.add("bar", 5);
        MetricsRecordBuilder rb = MetricsAsserts.mockMetricsRecordBuilder();
        rates.snapshot(rb, false);
        MetricsAsserts.assertCounter("FooNumOps", 1L, rb);
        MetricsAsserts.assertGauge("FooAvgTime", 1.0, rb);
        MetricsAsserts.assertCounter("BarNumOps", 1L, rb);
        MetricsAsserts.assertGauge("BarAvgTime", 5.0, rb);
        rates.add("foo", 1);
        rates.add("foo", 3);
        rates.add("bar", 6);
        rb = MetricsAsserts.mockMetricsRecordBuilder();
        rates.snapshot(rb, false);
        MetricsAsserts.assertCounter("FooNumOps", 3L, rb);
        MetricsAsserts.assertGauge("FooAvgTime", 2.0, rb);
        MetricsAsserts.assertCounter("BarNumOps", 2L, rb);
        MetricsAsserts.assertGauge("BarAvgTime", 6.0, rb);
    }

    @Test
    public void testMutableRatesWithAggregationManyThreads() throws InterruptedException {
        final MutableRatesWithAggregation rates = new MutableRatesWithAggregation();
        final int n = 10;
        long[] opCount = new long[n];
        double[] opTotalTime = new double[n];
        for (int i = 0; i < n; i++) {
            opCount[i] = 0;
            opTotalTime[i] = 0;
            // Initialize so that the getLongCounter() method doesn't complain
            rates.add(("metric" + i), 0);
        }
        Thread[] threads = new Thread[n];
        final CountDownLatch firstAddsFinished = new CountDownLatch(threads.length);
        final CountDownLatch firstSnapshotsFinished = new CountDownLatch(1);
        final CountDownLatch secondAddsFinished = new CountDownLatch(threads.length);
        final CountDownLatch secondSnapshotsFinished = new CountDownLatch(1);
        long seed = new Random().nextLong();
        TestMutableMetrics.LOG.info(("Random seed = " + seed));
        final Random sleepRandom = new Random(seed);
        for (int tIdx = 0; tIdx < (threads.length); tIdx++) {
            final int threadIdx = tIdx;
            threads[threadIdx] = new Thread() {
                @Override
                public void run() {
                    try {
                        for (int i = 0; i < 1000; i++) {
                            rates.add(("metric" + (i % n)), (((i / n) % 2) == 0 ? 1 : 2));
                            // Sleep so additions can be interleaved with snapshots
                            Thread.sleep(sleepRandom.nextInt(5));
                        }
                        firstAddsFinished.countDown();
                        // Make sure all threads stay alive long enough for the first
                        // snapshot to complete; else their metrics may be lost to GC
                        firstSnapshotsFinished.await();
                        // Let half the threads continue with more metrics and let half die
                        if ((threadIdx % 2) == 0) {
                            for (int i = 0; i < 1000; i++) {
                                rates.add(("metric" + (i % n)), (((i / n) % 2) == 0 ? 1 : 2));
                            }
                            secondAddsFinished.countDown();
                            secondSnapshotsFinished.await();
                        } else {
                            secondAddsFinished.countDown();
                        }
                    } catch (InterruptedException e) {
                        // Ignore
                    }
                }
            };
        }
        for (Thread t : threads) {
            t.start();
        }
        // Snapshot concurrently with additions but aggregate the totals into
        // opCount / opTotalTime
        for (int i = 0; i < 100; i++) {
            TestMutableMetrics.snapshotMutableRatesWithAggregation(rates, opCount, opTotalTime);
            Thread.sleep(sleepRandom.nextInt(20));
        }
        firstAddsFinished.await();
        // Final snapshot to grab any remaining metrics and then verify that
        // the totals are as expected
        TestMutableMetrics.snapshotMutableRatesWithAggregation(rates, opCount, opTotalTime);
        for (int i = 0; i < n; i++) {
            Assert.assertEquals((("metric" + i) + " count"), 1001, opCount[i]);
            Assert.assertEquals((("metric" + i) + " total"), 1500, opTotalTime[i], 1.0);
        }
        firstSnapshotsFinished.countDown();
        // After half of the threads die, ensure that the remaining ones still
        // add metrics correctly and that snapshot occurs correctly
        secondAddsFinished.await();
        TestMutableMetrics.snapshotMutableRatesWithAggregation(rates, opCount, opTotalTime);
        for (int i = 0; i < n; i++) {
            Assert.assertEquals((("metric" + i) + " count"), 1501, opCount[i]);
            Assert.assertEquals((("metric" + i) + " total"), 2250, opTotalTime[i], 1.0);
        }
        secondSnapshotsFinished.countDown();
    }

    /**
     * Tests that when using {@link MutableStat#add(long, long)}, even with a high
     * sample count, the mean does not lose accuracy.
     */
    @Test
    public void testMutableStatWithBulkAdd() {
        MetricsRecordBuilder rb = MetricsAsserts.mockMetricsRecordBuilder();
        MetricsRegistry registry = new MetricsRegistry("test");
        MutableStat stat = registry.newStat("Test", "Test", "Ops", "Val", false);
        stat.add(1000, 1000);
        stat.add(1000, 2000);
        registry.snapshot(rb, false);
        MetricsAsserts.assertCounter("TestNumOps", 2000L, rb);
        MetricsAsserts.assertGauge("TestAvgVal", 1.5, rb);
    }

    /**
     * Ensure that quantile estimates from {@link MutableQuantiles} are within
     * specified error bounds.
     */
    @Test(timeout = 30000)
    public void testMutableQuantilesError() throws Exception {
        MetricsRecordBuilder mb = MetricsAsserts.mockMetricsRecordBuilder();
        MetricsRegistry registry = new MetricsRegistry("test");
        // Use a 5s rollover period
        MutableQuantiles quantiles = registry.newQuantiles("foo", "stat", "Ops", "Latency", 5);
        // Push some values in and wait for it to publish
        long start = (System.nanoTime()) / 1000000;
        for (long i = 1; i <= 1000; i++) {
            quantiles.add(i);
            quantiles.add((1001 - i));
        }
        long end = (System.nanoTime()) / 1000000;
        Thread.sleep((6000 - (end - start)));
        registry.snapshot(mb, false);
        // Print out the snapshot
        Map<Quantile, Long> previousSnapshot = quantiles.previousSnapshot;
        for (Map.Entry<Quantile, Long> item : previousSnapshot.entrySet()) {
            System.out.println(String.format("Quantile %.2f has value %d", item.getKey().quantile, item.getValue()));
        }
        // Verify the results are within our requirements
        Mockito.verify(mb).addGauge(Interns.info("FooNumOps", "Number of ops for stat with 5s interval"), ((long) (2000)));
        Quantile[] quants = quantiles;
        String name = "Foo%dthPercentileLatency";
        String desc = "%d percentile latency with 5 second interval for stat";
        for (Quantile q : quants) {
            int percentile = ((int) (100 * (q.quantile)));
            int error = ((int) (1000 * (q.error)));
            String n = String.format(name, percentile);
            String d = String.format(desc, percentile);
            long expected = ((long) ((q.quantile) * 1000));
            Mockito.verify(mb).addGauge(AdditionalMatchers.eq(Interns.info(n, d)), AdditionalMatchers.leq((expected + error)));
            Mockito.verify(mb).addGauge(AdditionalMatchers.eq(Interns.info(n, d)), AdditionalMatchers.geq((expected - error)));
        }
    }

    /**
     * Test that {@link MutableQuantiles} rolls the window over at the specified
     * interval.
     */
    @Test(timeout = 30000)
    public void testMutableQuantilesRollover() throws Exception {
        MetricsRecordBuilder mb = MetricsAsserts.mockMetricsRecordBuilder();
        MetricsRegistry registry = new MetricsRegistry("test");
        // Use a 5s rollover period
        MutableQuantiles quantiles = registry.newQuantiles("foo", "stat", "Ops", "Latency", 5);
        Quantile[] quants = quantiles;
        String name = "Foo%dthPercentileLatency";
        String desc = "%d percentile latency with 5 second interval for stat";
        // Push values for three intervals
        long start = (System.nanoTime()) / 1000000;
        for (int i = 1; i <= 3; i++) {
            // Insert the values
            for (long j = 1; j <= 1000; j++) {
                quantiles.add(i);
            }
            // Sleep until 1s after the next 5s interval, to let the metrics
            // roll over
            long sleep = ((start + (5000 * i)) + 1000) - ((System.nanoTime()) / 1000000);
            Thread.sleep(sleep);
            // Verify that the window reset, check it has the values we pushed in
            registry.snapshot(mb, false);
            for (Quantile q : quants) {
                int percentile = ((int) (100 * (q.quantile)));
                String n = String.format(name, percentile);
                String d = String.format(desc, percentile);
                Mockito.verify(mb).addGauge(Interns.info(n, d), ((long) (i)));
            }
        }
        // Verify the metrics were added the right number of times
        Mockito.verify(mb, Mockito.times(3)).addGauge(Interns.info("FooNumOps", "Number of ops for stat with 5s interval"), ((long) (1000)));
        for (Quantile q : quants) {
            int percentile = ((int) (100 * (q.quantile)));
            String n = String.format(name, percentile);
            String d = String.format(desc, percentile);
            Mockito.verify(mb, Mockito.times(3)).addGauge(AdditionalMatchers.eq(Interns.info(n, d)), ArgumentMatchers.anyLong());
        }
    }

    /**
     * Test that {@link MutableQuantiles} rolls over correctly even if no items
     * have been added to the window
     */
    @Test(timeout = 30000)
    public void testMutableQuantilesEmptyRollover() throws Exception {
        MetricsRecordBuilder mb = MetricsAsserts.mockMetricsRecordBuilder();
        MetricsRegistry registry = new MetricsRegistry("test");
        // Use a 5s rollover period
        MutableQuantiles quantiles = registry.newQuantiles("foo", "stat", "Ops", "Latency", 5);
        // Check it initially
        quantiles.snapshot(mb, true);
        Mockito.verify(mb).addGauge(Interns.info("FooNumOps", "Number of ops for stat with 5s interval"), ((long) (0)));
        Thread.sleep(6000);
        quantiles.snapshot(mb, false);
        Mockito.verify(mb, Mockito.times(2)).addGauge(Interns.info("FooNumOps", "Number of ops for stat with 5s interval"), ((long) (0)));
    }
}

