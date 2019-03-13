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
package org.apache.hadoop.hdfs.client.impl;


import HdfsClientConfigKeys.Read.ShortCircuit.METRICS_SAMPLING_PERCENTAGE_KEY;
import com.google.common.base.Supplier;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import org.apache.hadoop.hdfs.HdfsConfiguration;
import org.apache.hadoop.hdfs.client.impl.metrics.BlockReaderIoProvider;
import org.apache.hadoop.hdfs.client.impl.metrics.BlockReaderLocalMetrics;
import org.apache.hadoop.metrics2.MetricsRecordBuilder;
import org.apache.hadoop.metrics2.lib.MetricsTestHelper;
import org.apache.hadoop.metrics2.lib.MutableRollingAverages;
import org.apache.hadoop.test.GenericTestUtils;
import org.apache.hadoop.test.MetricsAsserts;
import org.apache.hadoop.util.FakeTimer;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;


/**
 * Tests {@link BlockReaderLocalMetrics}'s statistics.
 */
public class TestBlockReaderLocalMetrics {
    private static final long ROLLING_AVERAGES_WINDOW_LENGTH_MS = 1000;

    private static final int ROLLING_AVERAGE_NUM_WINDOWS = 5;

    private static final long SLOW_READ_DELAY = 2000;

    private static final String SHORT_CIRCUIT_READ_METRIC_REGISTERED_NAME = "HdfsShortCircuitReads";

    private static final String SHORT_CIRCUIT_LOCAL_READS_METRIC_VALUE_FULL_NAME = "[ShortCircuitLocalReads]RollingAvgLatencyMs";

    private static final FakeTimer TIMER = new FakeTimer();

    private static HdfsConfiguration conf = new HdfsConfiguration();

    private static DfsClientConf clientConf;

    static {
        TestBlockReaderLocalMetrics.conf = new HdfsConfiguration();
        TestBlockReaderLocalMetrics.conf.setInt(METRICS_SAMPLING_PERCENTAGE_KEY, 100);
        TestBlockReaderLocalMetrics.clientConf = new DfsClientConf(TestBlockReaderLocalMetrics.conf);
    }

    @Test(timeout = 300000)
    public void testSlowShortCircuitReadsStatsRecorded() throws IOException, InterruptedException, TimeoutException {
        BlockReaderLocalMetrics metrics = BlockReaderLocalMetrics.create();
        MutableRollingAverages shortCircuitReadRollingAverages = metrics.getShortCircuitReadRollingAverages();
        MetricsTestHelper.replaceRollingAveragesScheduler(shortCircuitReadRollingAverages, TestBlockReaderLocalMetrics.ROLLING_AVERAGE_NUM_WINDOWS, TestBlockReaderLocalMetrics.ROLLING_AVERAGES_WINDOW_LENGTH_MS, TimeUnit.MILLISECONDS);
        FileChannel dataIn = Mockito.mock(FileChannel.class);
        Mockito.when(dataIn.read(ArgumentMatchers.any(), ArgumentMatchers.anyLong())).thenAnswer(new Answer<Object>() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                TestBlockReaderLocalMetrics.TIMER.advance(TestBlockReaderLocalMetrics.SLOW_READ_DELAY);
                return 0;
            }
        });
        BlockReaderIoProvider blockReaderIoProvider = new BlockReaderIoProvider(TestBlockReaderLocalMetrics.clientConf.getShortCircuitConf(), metrics, TestBlockReaderLocalMetrics.TIMER);
        blockReaderIoProvider.read(dataIn, ArgumentMatchers.any(), ArgumentMatchers.anyLong());
        blockReaderIoProvider.read(dataIn, ArgumentMatchers.any(), ArgumentMatchers.anyLong());
        GenericTestUtils.waitFor(new Supplier<Boolean>() {
            @Override
            public Boolean get() {
                metrics.collectThreadLocalStates();
                return (shortCircuitReadRollingAverages.getStats(0).size()) > 0;
            }
        }, 500, 10000);
        MetricsRecordBuilder rb = MetricsAsserts.getMetrics(TestBlockReaderLocalMetrics.SHORT_CIRCUIT_READ_METRIC_REGISTERED_NAME);
        double averageLatency = MetricsAsserts.getDoubleGauge(TestBlockReaderLocalMetrics.SHORT_CIRCUIT_LOCAL_READS_METRIC_VALUE_FULL_NAME, rb);
        Assert.assertTrue("Average Latency of Short Circuit Reads lower than expected", (averageLatency >= (TestBlockReaderLocalMetrics.SLOW_READ_DELAY)));
    }

    @Test(timeout = 300000)
    public void testMutlipleBlockReaderIoProviderStats() throws IOException, InterruptedException, TimeoutException {
        BlockReaderLocalMetrics metrics = BlockReaderLocalMetrics.create();
        MutableRollingAverages shortCircuitReadRollingAverages = metrics.getShortCircuitReadRollingAverages();
        MetricsTestHelper.replaceRollingAveragesScheduler(shortCircuitReadRollingAverages, TestBlockReaderLocalMetrics.ROLLING_AVERAGE_NUM_WINDOWS, TestBlockReaderLocalMetrics.ROLLING_AVERAGES_WINDOW_LENGTH_MS, TimeUnit.MILLISECONDS);
        FileChannel dataIn1 = Mockito.mock(FileChannel.class);
        FileChannel dataIn2 = Mockito.mock(FileChannel.class);
        Mockito.when(dataIn1.read(ArgumentMatchers.any(), ArgumentMatchers.anyLong())).thenAnswer(new Answer<Object>() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                TestBlockReaderLocalMetrics.TIMER.advance(TestBlockReaderLocalMetrics.SLOW_READ_DELAY);
                return 0;
            }
        });
        Mockito.when(dataIn2.read(ArgumentMatchers.any(), ArgumentMatchers.anyLong())).thenAnswer(new Answer<Object>() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                TestBlockReaderLocalMetrics.TIMER.advance(((TestBlockReaderLocalMetrics.SLOW_READ_DELAY) * 3));
                return 0;
            }
        });
        BlockReaderIoProvider blockReaderIoProvider1 = new BlockReaderIoProvider(TestBlockReaderLocalMetrics.clientConf.getShortCircuitConf(), metrics, TestBlockReaderLocalMetrics.TIMER);
        BlockReaderIoProvider blockReaderIoProvider2 = new BlockReaderIoProvider(TestBlockReaderLocalMetrics.clientConf.getShortCircuitConf(), metrics, TestBlockReaderLocalMetrics.TIMER);
        blockReaderIoProvider1.read(dataIn1, ArgumentMatchers.any(), ArgumentMatchers.anyLong());
        blockReaderIoProvider2.read(dataIn2, ArgumentMatchers.any(), ArgumentMatchers.anyLong());
        GenericTestUtils.waitFor(new Supplier<Boolean>() {
            @Override
            public Boolean get() {
                metrics.collectThreadLocalStates();
                return (shortCircuitReadRollingAverages.getStats(0).size()) > 0;
            }
        }, 500, 10000);
        MetricsRecordBuilder rb = MetricsAsserts.getMetrics(TestBlockReaderLocalMetrics.SHORT_CIRCUIT_READ_METRIC_REGISTERED_NAME);
        double averageLatency = MetricsAsserts.getDoubleGauge(TestBlockReaderLocalMetrics.SHORT_CIRCUIT_LOCAL_READS_METRIC_VALUE_FULL_NAME, rb);
        Assert.assertTrue("Average Latency of Short Circuit Reads lower than expected", (averageLatency >= ((TestBlockReaderLocalMetrics.SLOW_READ_DELAY) * 2)));
    }

    @Test(timeout = 300000)
    public void testSlowShortCircuitReadsAverageLatencyValue() throws IOException, InterruptedException, TimeoutException {
        BlockReaderLocalMetrics metrics = BlockReaderLocalMetrics.create();
        final MutableRollingAverages shortCircuitReadRollingAverages = metrics.getShortCircuitReadRollingAverages();
        MetricsTestHelper.replaceRollingAveragesScheduler(shortCircuitReadRollingAverages, TestBlockReaderLocalMetrics.ROLLING_AVERAGE_NUM_WINDOWS, TestBlockReaderLocalMetrics.ROLLING_AVERAGES_WINDOW_LENGTH_MS, TimeUnit.MILLISECONDS);
        Random random = new Random();
        FileChannel[] dataIns = new FileChannel[5];
        long totalDelay = 0;
        for (int i = 0; i < 5; i++) {
            dataIns[i] = Mockito.mock(FileChannel.class);
            long delay = (TestBlockReaderLocalMetrics.SLOW_READ_DELAY) * (random.nextInt(5));
            Mockito.when(dataIns[i].read(ArgumentMatchers.any(), ArgumentMatchers.anyLong())).thenAnswer(new Answer<Object>() {
                @Override
                public Object answer(InvocationOnMock invocation) throws Throwable {
                    TestBlockReaderLocalMetrics.TIMER.advance(delay);
                    return 0;
                }
            });
            totalDelay += delay;
        }
        long expectedAvgLatency = totalDelay / 5;
        BlockReaderIoProvider blockReaderIoProvider = new BlockReaderIoProvider(TestBlockReaderLocalMetrics.clientConf.getShortCircuitConf(), metrics, TestBlockReaderLocalMetrics.TIMER);
        for (int i = 0; i < 5; i++) {
            blockReaderIoProvider.read(dataIns[i], ArgumentMatchers.any(), ArgumentMatchers.anyLong());
        }
        GenericTestUtils.waitFor(new Supplier<Boolean>() {
            @Override
            public Boolean get() {
                metrics.collectThreadLocalStates();
                return (shortCircuitReadRollingAverages.getStats(0).size()) > 0;
            }
        }, 500, 10000);
        MetricsRecordBuilder rb = MetricsAsserts.getMetrics(TestBlockReaderLocalMetrics.SHORT_CIRCUIT_READ_METRIC_REGISTERED_NAME);
        double averageLatency = MetricsAsserts.getDoubleGauge(TestBlockReaderLocalMetrics.SHORT_CIRCUIT_LOCAL_READS_METRIC_VALUE_FULL_NAME, rb);
        Assert.assertTrue("Average Latency of Short Circuit Reads lower than expected", (averageLatency >= expectedAvgLatency));
    }
}

