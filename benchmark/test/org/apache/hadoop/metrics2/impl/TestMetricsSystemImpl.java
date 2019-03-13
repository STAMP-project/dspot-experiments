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
package org.apache.hadoop.metrics2.impl;


import com.google.common.base.Predicate;
import com.google.common.base.Supplier;
import com.google.common.collect.Iterables;
import java.io.Closeable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicLong;
import javax.annotation.Nullable;
import org.apache.commons.configuration2.SubsetConfiguration;
import org.apache.hadoop.metrics2.AbstractMetric;
import org.apache.hadoop.metrics2.MetricsException;
import org.apache.hadoop.metrics2.MetricsRecord;
import org.apache.hadoop.metrics2.MetricsSink;
import org.apache.hadoop.metrics2.MetricsSource;
import org.apache.hadoop.metrics2.MetricsSystem;
import org.apache.hadoop.metrics2.lib.DefaultMetricsSystem;
import org.apache.hadoop.metrics2.lib.MetricsRegistry;
import org.apache.hadoop.metrics2.lib.MutableCounterLong;
import org.apache.hadoop.metrics2.lib.MutableGaugeLong;
import org.apache.hadoop.metrics2.lib.MutableRate;
import org.apache.hadoop.test.GenericTestUtils;
import org.apache.hadoop.util.StringUtils;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static MetricsConfig.PERIOD_DEFAULT;
import static MetricsConfig.QUEUE_CAPACITY_KEY;
import static org.mockito.ArgumentMatchers.any;


/**
 * Test the MetricsSystemImpl class
 */
@RunWith(MockitoJUnitRunner.class)
public class TestMetricsSystemImpl {
    private static final Logger LOG = LoggerFactory.getLogger(TestMetricsSystemImpl.class);

    static {
        DefaultMetricsSystem.setMiniClusterMode(true);
    }

    @Captor
    private ArgumentCaptor<MetricsRecord> r1;

    @Captor
    private ArgumentCaptor<MetricsRecord> r2;

    private static String hostname = MetricsSystemImpl.getHostname();

    public static class TestSink implements MetricsSink {
        private List<Iterable<AbstractMetric>> metricValues = new ArrayList<>();

        @Override
        public void putMetrics(MetricsRecord record) {
            TestMetricsSystemImpl.LOG.debug(record.toString());
            metricValues.add(record.metrics());
        }

        @Override
        public void flush() {
        }

        @Override
        public void init(SubsetConfiguration conf) {
            TestMetricsSystemImpl.LOG.debug(MetricsConfig.toString(conf));
        }

        List<Iterable<AbstractMetric>> getMetricValues() {
            return metricValues;
        }
    }

    @Test
    public void testInitFirstVerifyStopInvokedImmediately() throws Exception {
        DefaultMetricsSystem.shutdown();
        // .add("test.sink.plugin.urls", getPluginUrlsAsString())
        new ConfigBuilder().add("*.period", 8).add("test.sink.test.class", TestMetricsSystemImpl.TestSink.class.getName()).add("test.*.source.filter.exclude", "s0").add("test.source.s1.metric.filter.exclude", "X*").add("test.sink.sink1.metric.filter.exclude", "Y*").add("test.sink.sink2.metric.filter.exclude", "Y*").save(TestMetricsConfig.getTestFilename("hadoop-metrics2-test"));
        MetricsSystemImpl ms = new MetricsSystemImpl("Test");
        ms.start();
        ms.register("s0", "s0 desc", new TestMetricsSystemImpl.TestSource("s0rec"));
        TestMetricsSystemImpl.TestSource s1 = ms.register("s1", "s1 desc", new TestMetricsSystemImpl.TestSource("s1rec"));
        s1.c1.incr();
        s1.xxx.incr();
        s1.g1.set(2);
        s1.yyy.incr(2);
        s1.s1.add(0);
        MetricsSink sink1 = Mockito.mock(MetricsSink.class);
        MetricsSink sink2 = Mockito.mock(MetricsSink.class);
        ms.registerSink("sink1", "sink1 desc", sink1);
        ms.registerSink("sink2", "sink2 desc", sink2);
        ms.publishMetricsNow();// publish the metrics

        ms.stop();
        ms.shutdown();
        // When we call stop, at most two sources will be consumed by each sink thread.
        Mockito.verify(sink1, Mockito.atMost(2)).putMetrics(r1.capture());
        List<MetricsRecord> mr1 = r1.getAllValues();
        Mockito.verify(sink2, Mockito.atMost(2)).putMetrics(r2.capture());
        List<MetricsRecord> mr2 = r2.getAllValues();
        if (((mr1.size()) != 0) && ((mr2.size()) != 0)) {
            checkMetricsRecords(mr1);
            Assert.assertEquals("output", mr1, mr2);
        } else
            if ((mr1.size()) != 0) {
                checkMetricsRecords(mr1);
            } else
                if ((mr2.size()) != 0) {
                    checkMetricsRecords(mr2);
                }


    }

    @Test
    public void testInitFirstVerifyCallBacks() throws Exception {
        DefaultMetricsSystem.shutdown();
        // .add("test.sink.plugin.urls", getPluginUrlsAsString())
        new ConfigBuilder().add("*.period", 8).add("test.sink.test.class", TestMetricsSystemImpl.TestSink.class.getName()).add("test.*.source.filter.exclude", "s0").add("test.source.s1.metric.filter.exclude", "X*").add("test.sink.sink1.metric.filter.exclude", "Y*").add("test.sink.sink2.metric.filter.exclude", "Y*").save(TestMetricsConfig.getTestFilename("hadoop-metrics2-test"));
        MetricsSystemImpl ms = new MetricsSystemImpl("Test");
        ms.start();
        ms.register("s0", "s0 desc", new TestMetricsSystemImpl.TestSource("s0rec"));
        TestMetricsSystemImpl.TestSource s1 = ms.register("s1", "s1 desc", new TestMetricsSystemImpl.TestSource("s1rec"));
        s1.c1.incr();
        s1.xxx.incr();
        s1.g1.set(2);
        s1.yyy.incr(2);
        s1.s1.add(0);
        MetricsSink sink1 = Mockito.mock(MetricsSink.class);
        MetricsSink sink2 = Mockito.mock(MetricsSink.class);
        ms.registerSink("sink1", "sink1 desc", sink1);
        ms.registerSink("sink2", "sink2 desc", sink2);
        ms.publishMetricsNow();// publish the metrics

        try {
            Mockito.verify(sink1, Mockito.timeout(200).times(2)).putMetrics(r1.capture());
            Mockito.verify(sink2, Mockito.timeout(200).times(2)).putMetrics(r2.capture());
        } finally {
            ms.stop();
            ms.shutdown();
        }
        // When we call stop, at most two sources will be consumed by each sink thread.
        List<MetricsRecord> mr1 = r1.getAllValues();
        List<MetricsRecord> mr2 = r2.getAllValues();
        checkMetricsRecords(mr1);
        Assert.assertEquals("output", mr1, mr2);
    }

    @Test
    public void testMultiThreadedPublish() throws Exception {
        final int numThreads = 10;
        new ConfigBuilder().add("*.period", 80).add(("test.sink.collector." + (QUEUE_CAPACITY_KEY)), numThreads).save(TestMetricsConfig.getTestFilename("hadoop-metrics2-test"));
        final MetricsSystemImpl ms = new MetricsSystemImpl("Test");
        ms.start();
        final TestMetricsSystemImpl.CollectingSink sink = new TestMetricsSystemImpl.CollectingSink(numThreads);
        ms.registerSink("collector", "Collector of values from all threads.", sink);
        final TestMetricsSystemImpl.TestSource[] sources = new TestMetricsSystemImpl.TestSource[numThreads];
        final Thread[] threads = new Thread[numThreads];
        final String[] results = new String[numThreads];
        final CyclicBarrier barrier1 = new CyclicBarrier(numThreads);
        final CyclicBarrier barrier2 = new CyclicBarrier(numThreads);
        for (int i = 0; i < numThreads; i++) {
            sources[i] = ms.register(("threadSource" + i), "A source of my threaded goodness.", new TestMetricsSystemImpl.TestSource(("threadSourceRec" + i)));
            threads[i] = new Thread(new Runnable() {
                private boolean safeAwait(int mySource, CyclicBarrier barrier) {
                    try {
                        barrier.await(2, TimeUnit.SECONDS);
                    } catch (InterruptedException e) {
                        results[mySource] = "Interrupted";
                        return false;
                    } catch (BrokenBarrierException e) {
                        results[mySource] = "Broken Barrier";
                        return false;
                    } catch (TimeoutException e) {
                        results[mySource] = "Timed out on barrier";
                        return false;
                    }
                    return true;
                }

                @Override
                public void run() {
                    int mySource = Integer.parseInt(Thread.currentThread().getName());
                    if ((sink.collected[mySource].get()) != 0L) {
                        results[mySource] = "Someone else collected my metric!";
                        return;
                    }
                    // Wait for all the threads to come here so we can hammer
                    // the system at the same time
                    if (!(safeAwait(mySource, barrier1)))
                        return;

                    sources[mySource].g1.set(230);
                    ms.publishMetricsNow();
                    // Since some other thread may have snatched my metric,
                    // I need to wait for the threads to finish before checking.
                    if (!(safeAwait(mySource, barrier2)))
                        return;

                    if ((sink.collected[mySource].get()) != 230L) {
                        results[mySource] = "Metric not collected!";
                        return;
                    }
                    results[mySource] = "Passed";
                }
            }, ("" + i));
        }
        for (Thread t : threads)
            t.start();

        for (Thread t : threads)
            t.join();

        Assert.assertEquals(0L, ms.droppedPubAll.value());
        Assert.assertTrue(StringUtils.join("\n", Arrays.asList(results)), Iterables.all(Arrays.asList(results), new Predicate<String>() {
            @Override
            public boolean apply(@Nullable
            String input) {
                return input.equalsIgnoreCase("Passed");
            }
        }));
        ms.stop();
        ms.shutdown();
    }

    private static class CollectingSink implements MetricsSink {
        private final AtomicLong[] collected;

        public CollectingSink(int capacity) {
            collected = new AtomicLong[capacity];
            for (int i = 0; i < capacity; i++) {
                collected[i] = new AtomicLong();
            }
        }

        @Override
        public void init(SubsetConfiguration conf) {
        }

        @Override
        public void putMetrics(MetricsRecord record) {
            final String prefix = "threadSourceRec";
            if (record.name().startsWith(prefix)) {
                final int recordNumber = Integer.parseInt(record.name().substring(prefix.length()));
                ArrayList<String> names = new ArrayList<String>();
                for (AbstractMetric m : record.metrics()) {
                    if (m.name().equalsIgnoreCase("g1")) {
                        collected[recordNumber].set(m.value().longValue());
                        return;
                    }
                    names.add(m.name());
                }
            }
        }

        @Override
        public void flush() {
        }
    }

    @Test
    public void testHangingSink() {
        new ConfigBuilder().add("*.period", 8).add("test.sink.test.class", TestMetricsSystemImpl.TestSink.class.getName()).add("test.sink.hanging.retry.delay", "1").add("test.sink.hanging.retry.backoff", "1.01").add("test.sink.hanging.retry.count", "0").save(TestMetricsConfig.getTestFilename("hadoop-metrics2-test"));
        MetricsSystemImpl ms = new MetricsSystemImpl("Test");
        ms.start();
        TestMetricsSystemImpl.TestSource s = ms.register("s3", "s3 desc", new TestMetricsSystemImpl.TestSource("s3rec"));
        s.c1.incr();
        TestMetricsSystemImpl.HangingSink hanging = new TestMetricsSystemImpl.HangingSink();
        ms.registerSink("hanging", "Hang the sink!", hanging);
        ms.publishMetricsNow();
        Assert.assertEquals(1L, ms.droppedPubAll.value());
        Assert.assertFalse(hanging.getInterrupted());
        ms.stop();
        ms.shutdown();
        Assert.assertTrue(hanging.getInterrupted());
        Assert.assertTrue(("The sink didn't get called after its first hang " + "for subsequent records."), hanging.getGotCalledSecondTime());
    }

    private static class HangingSink implements MetricsSink {
        private volatile boolean interrupted;

        private boolean gotCalledSecondTime;

        private boolean firstTime = true;

        public boolean getGotCalledSecondTime() {
            return gotCalledSecondTime;
        }

        public boolean getInterrupted() {
            return interrupted;
        }

        @Override
        public void init(SubsetConfiguration conf) {
        }

        @Override
        public void putMetrics(MetricsRecord record) {
            // No need to hang every time, just the first record.
            if (!(firstTime)) {
                gotCalledSecondTime = true;
                return;
            }
            firstTime = false;
            try {
                Thread.sleep((10 * 1000));
            } catch (InterruptedException ex) {
                interrupted = true;
            }
        }

        @Override
        public void flush() {
        }
    }

    @Test
    public void testRegisterDups() {
        MetricsSystem ms = new MetricsSystemImpl();
        TestMetricsSystemImpl.TestSource ts1 = new TestMetricsSystemImpl.TestSource("ts1");
        TestMetricsSystemImpl.TestSource ts2 = new TestMetricsSystemImpl.TestSource("ts2");
        ms.register("ts1", "", ts1);
        MetricsSource s1 = ms.getSource("ts1");
        Assert.assertNotNull(s1);
        // should work when metrics system is not started
        ms.register("ts1", "", ts2);
        MetricsSource s2 = ms.getSource("ts1");
        Assert.assertNotNull(s2);
        Assert.assertNotSame(s1, s2);
        ms.shutdown();
    }

    @Test(expected = MetricsException.class)
    public void testRegisterDupError() {
        MetricsSystem ms = new MetricsSystemImpl("test");
        TestMetricsSystemImpl.TestSource ts = new TestMetricsSystemImpl.TestSource("ts");
        ms.register(ts);
        ms.register(ts);
    }

    @Test
    public void testStartStopStart() {
        DefaultMetricsSystem.shutdown();// Clear pre-existing source names.

        MetricsSystemImpl ms = new MetricsSystemImpl("test");
        TestMetricsSystemImpl.TestSource ts = new TestMetricsSystemImpl.TestSource("ts");
        ms.start();
        ms.register("ts", "", ts);
        MetricsSourceAdapter sa = ms.getSourceAdapter("ts");
        Assert.assertNotNull(sa);
        Assert.assertNotNull(sa.getMBeanName());
        ms.stop();
        ms.shutdown();
        ms.start();
        sa = ms.getSourceAdapter("ts");
        Assert.assertNotNull(sa);
        Assert.assertNotNull(sa.getMBeanName());
        ms.stop();
        ms.shutdown();
    }

    @Test
    public void testUnregisterSource() {
        MetricsSystem ms = new MetricsSystemImpl();
        TestMetricsSystemImpl.TestSource ts1 = new TestMetricsSystemImpl.TestSource("ts1");
        TestMetricsSystemImpl.TestSource ts2 = new TestMetricsSystemImpl.TestSource("ts2");
        ms.register("ts1", "", ts1);
        ms.register("ts2", "", ts2);
        MetricsSource s1 = ms.getSource("ts1");
        Assert.assertNotNull(s1);
        // should work when metrics system is not started
        ms.unregisterSource("ts1");
        s1 = ms.getSource("ts1");
        Assert.assertNull(s1);
        MetricsSource s2 = ms.getSource("ts2");
        Assert.assertNotNull(s2);
        ms.shutdown();
    }

    @Test
    public void testRegisterSourceWithoutName() {
        MetricsSystem ms = new MetricsSystemImpl();
        TestMetricsSystemImpl.TestSource ts = new TestMetricsSystemImpl.TestSource("ts");
        TestMetricsSystemImpl.TestSource2 ts2 = new TestMetricsSystemImpl.TestSource2("ts2");
        ms.register(ts);
        ms.register(ts2);
        ms.init("TestMetricsSystem");
        // if metrics source is registered without name,
        // the class name will be used as the name
        MetricsSourceAdapter sa = getSourceAdapter("TestSource");
        Assert.assertNotNull(sa);
        MetricsSourceAdapter sa2 = getSourceAdapter("TestSource2");
        Assert.assertNotNull(sa2);
        ms.shutdown();
    }

    @Test
    public void testQSize() throws Exception {
        new ConfigBuilder().add("*.period", 8).add("*.queue.capacity", 2).add("test.sink.test.class", TestMetricsSystemImpl.TestSink.class.getName()).save(TestMetricsConfig.getTestFilename("hadoop-metrics2-test"));
        MetricsSystemImpl ms = new MetricsSystemImpl("Test");
        final CountDownLatch proceedSignal = new CountDownLatch(1);
        final CountDownLatch reachedPutMetricSignal = new CountDownLatch(1);
        ms.start();
        try {
            MetricsSink slowSink = Mockito.mock(MetricsSink.class);
            MetricsSink dataSink = Mockito.mock(MetricsSink.class);
            ms.registerSink("slowSink", "The sink that will wait on putMetric", slowSink);
            ms.registerSink("dataSink", "The sink I'll use to get info about slowSink", dataSink);
            Mockito.doAnswer(new org.mockito.stubbing.Answer() {
                @Override
                public Object answer(org.mockito.invocation.InvocationOnMock invocation) throws Throwable {
                    reachedPutMetricSignal.countDown();
                    proceedSignal.await();
                    return null;
                }
            }).when(slowSink).putMetrics(any(MetricsRecord.class));
            // trigger metric collection first time
            ms.onTimerEvent();
            Assert.assertTrue(reachedPutMetricSignal.await(1, TimeUnit.SECONDS));
            // Now that the slow sink is still processing the first metric,
            // its queue length should be 1 for the second collection.
            ms.onTimerEvent();
            Mockito.verify(dataSink, Mockito.timeout(500).times(2)).putMetrics(r1.capture());
            List<MetricsRecord> mr = r1.getAllValues();
            Number qSize = Iterables.find(mr.get(1).metrics(), new Predicate<AbstractMetric>() {
                @Override
                public boolean apply(@Nullable
                AbstractMetric input) {
                    assert input != null;
                    return input.name().equals("Sink_slowSinkQsize");
                }
            }).value();
            Assert.assertEquals(1, qSize);
        } finally {
            proceedSignal.countDown();
            ms.stop();
        }
    }

    /**
     * Class to verify HADOOP-11932. Instead of reading from HTTP, going in loop
     * until closed.
     */
    private static class TestClosableSink implements Closeable , MetricsSink {
        boolean closed = false;

        CountDownLatch collectingLatch;

        public TestClosableSink(CountDownLatch collectingLatch) {
            this.collectingLatch = collectingLatch;
        }

        @Override
        public void init(SubsetConfiguration conf) {
        }

        @Override
        public void close() throws IOException {
            closed = true;
        }

        @Override
        public void putMetrics(MetricsRecord record) {
            while (!(closed)) {
                collectingLatch.countDown();
            } 
        }

        @Override
        public void flush() {
        }
    }

    /**
     * HADOOP-11932
     */
    @Test(timeout = 5000)
    public void testHangOnSinkRead() throws Exception {
        new ConfigBuilder().add("*.period", 8).add("test.sink.test.class", TestMetricsSystemImpl.TestSink.class.getName()).save(TestMetricsConfig.getTestFilename("hadoop-metrics2-test"));
        MetricsSystemImpl ms = new MetricsSystemImpl("Test");
        ms.start();
        try {
            CountDownLatch collectingLatch = new CountDownLatch(1);
            MetricsSink sink = new TestMetricsSystemImpl.TestClosableSink(collectingLatch);
            ms.registerSink("closeableSink", "The sink will be used to test closeability", sink);
            // trigger metric collection first time
            ms.onTimerEvent();
            // Make sure that sink is collecting metrics
            Assert.assertTrue(collectingLatch.await(1, TimeUnit.SECONDS));
        } finally {
            ms.stop();
        }
    }

    @Test
    public void testRegisterSourceJmxCacheTTL() {
        MetricsSystem ms = new MetricsSystemImpl();
        ms.init("TestMetricsSystem");
        TestMetricsSystemImpl.TestSource ts = new TestMetricsSystemImpl.TestSource("ts");
        ms.register(ts);
        MetricsSourceAdapter sa = getSourceAdapter("TestSource");
        Assert.assertEquals((((PERIOD_DEFAULT) * 1000) + 1), sa.getJmxCacheTTL());
        ms.shutdown();
    }

    @Test
    public void testRegisterSinksMultiplePeriods() throws Exception {
        new ConfigBuilder().add("test.sink.test1.period", 100000).add("test.sink.test1.class", TestMetricsSystemImpl.TestSink.class.getName()).add("test.sink.test2.period", 200000).add("test.sink.test2.class", TestMetricsSystemImpl.TestSink.class.getName()).save(TestMetricsConfig.getTestFilename("hadoop-metrics2-test"));
        MetricsSystemImpl ms = new MetricsSystemImpl();
        try {
            ms.init("test");
            TestMetricsSystemImpl.TestSink sink1 = ((TestMetricsSystemImpl.TestSink) (ms.getSinkAdapter("test1").sink()));
            TestMetricsSystemImpl.TestSink sink2 = ((TestMetricsSystemImpl.TestSink) (ms.getSinkAdapter("test2").sink()));
            Assert.assertEquals(0, sink1.getMetricValues().size());
            Assert.assertEquals(0, sink2.getMetricValues().size());
            ms.onTimerEvent();
            // Give some time for the publish event to go through
            GenericTestUtils.waitFor(new Supplier<Boolean>() {
                @Override
                public Boolean get() {
                    return (sink1.getMetricValues().size()) > 0;
                }
            }, 10, 10000);
            Assert.assertEquals(1, sink1.getMetricValues().size());
            Assert.assertEquals(0, sink2.getMetricValues().size());
            ms.onTimerEvent();
            // Give some time for the publish event to go through
            GenericTestUtils.waitFor(new Supplier<Boolean>() {
                @Override
                public Boolean get() {
                    return ((sink1.getMetricValues().size()) > 1) && ((sink2.getMetricValues().size()) > 0);
                }
            }, 10, 10000);
            Assert.assertEquals(2, sink1.getMetricValues().size());
            Assert.assertEquals(1, sink2.getMetricValues().size());
        } finally {
            ms.shutdown();
        }
    }

    @Metrics(context = "test")
    private static class TestSource {
        @Metric("C1 desc")
        MutableCounterLong c1;

        @Metric("XXX desc")
        MutableCounterLong xxx;

        @Metric("G1 desc")
        MutableGaugeLong g1;

        @Metric("YYY desc")
        MutableGaugeLong yyy;

        @Metric
        MutableRate s1;

        final MetricsRegistry registry;

        TestSource(String recName) {
            registry = new MetricsRegistry(recName);
        }
    }

    @Metrics(context = "test")
    private static class TestSource2 {
        @Metric("C1 desc")
        MutableCounterLong c1;

        @Metric("XXX desc")
        MutableCounterLong xxx;

        @Metric("G1 desc")
        MutableGaugeLong g1;

        @Metric("YYY desc")
        MutableGaugeLong yyy;

        @Metric
        MutableRate s1;

        final MetricsRegistry registry;

        TestSource2(String recName) {
            registry = new MetricsRegistry(recName);
        }
    }
}

