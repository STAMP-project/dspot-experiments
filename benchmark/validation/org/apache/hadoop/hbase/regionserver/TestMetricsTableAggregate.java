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
package org.apache.hadoop.hbase.regionserver;


import java.io.IOException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;
import java.util.function.IntFunction;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import org.apache.hadoop.hbase.CompatibilityFactory;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.test.MetricsAssertHelper;
import org.apache.hadoop.hbase.testclassification.RegionServerTests;
import org.apache.hadoop.hbase.testclassification.SmallTests;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Category({ RegionServerTests.class, SmallTests.class })
public class TestMetricsTableAggregate {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestMetricsTableAggregate.class);

    private static final Logger LOG = LoggerFactory.getLogger(TestMetricsTableAggregate.class);

    private static MetricsAssertHelper HELPER = CompatibilityFactory.getInstance(MetricsAssertHelper.class);

    private String tableName = "testTableMetrics";

    private String pre = ("Namespace_default_table_" + (tableName)) + "_metric_";

    private MetricsTableWrapperStub tableWrapper;

    private MetricsTable mt;

    private MetricsRegionServerWrapper rsWrapper;

    private MetricsRegionServer rsm;

    private MetricsTableAggregateSource agg;

    @Test
    public void testRequestMetrics() throws IOException {
        TestMetricsTableAggregate.HELPER.assertCounter(((pre) + "readRequestCount"), 10, agg);
        TestMetricsTableAggregate.HELPER.assertCounter(((pre) + "writeRequestCount"), 20, agg);
        TestMetricsTableAggregate.HELPER.assertCounter(((pre) + "totalRequestCount"), 30, agg);
    }

    @Test
    public void testRegionAndStoreMetrics() throws IOException {
        TestMetricsTableAggregate.HELPER.assertGauge(((pre) + "memstoreSize"), 1000, agg);
        TestMetricsTableAggregate.HELPER.assertGauge(((pre) + "storeFileSize"), 2000, agg);
        TestMetricsTableAggregate.HELPER.assertGauge(((pre) + "tableSize"), 3000, agg);
        TestMetricsTableAggregate.HELPER.assertGauge(((pre) + "regionCount"), 11, agg);
        TestMetricsTableAggregate.HELPER.assertGauge(((pre) + "storeCount"), 22, agg);
        TestMetricsTableAggregate.HELPER.assertGauge(((pre) + "storeFileCount"), 33, agg);
        TestMetricsTableAggregate.HELPER.assertGauge(((pre) + "maxStoreFileAge"), 44, agg);
        TestMetricsTableAggregate.HELPER.assertGauge(((pre) + "minStoreFileAge"), 55, agg);
        TestMetricsTableAggregate.HELPER.assertGauge(((pre) + "avgStoreFileAge"), 66, agg);
        TestMetricsTableAggregate.HELPER.assertGauge(((pre) + "numReferenceFiles"), 77, agg);
        TestMetricsTableAggregate.HELPER.assertGauge(((pre) + "averageRegionSize"), 88, agg);
    }

    @Test
    public void testFlush() {
        rsm.updateFlush(tableName, 1, 2, 3);
        TestMetricsTableAggregate.HELPER.assertCounter(((pre) + "flushTime_num_ops"), 1, agg);
        TestMetricsTableAggregate.HELPER.assertCounter(((pre) + "flushMemstoreSize_num_ops"), 1, agg);
        TestMetricsTableAggregate.HELPER.assertCounter(((pre) + "flushOutputSize_num_ops"), 1, agg);
        TestMetricsTableAggregate.HELPER.assertCounter(((pre) + "flushedMemstoreBytes"), 2, agg);
        TestMetricsTableAggregate.HELPER.assertCounter(((pre) + "flushedOutputBytes"), 3, agg);
        rsm.updateFlush(tableName, 10, 20, 30);
        TestMetricsTableAggregate.HELPER.assertCounter(((pre) + "flushTime_num_ops"), 2, agg);
        TestMetricsTableAggregate.HELPER.assertCounter(((pre) + "flushMemstoreSize_num_ops"), 2, agg);
        TestMetricsTableAggregate.HELPER.assertCounter(((pre) + "flushOutputSize_num_ops"), 2, agg);
        TestMetricsTableAggregate.HELPER.assertCounter(((pre) + "flushedMemstoreBytes"), 22, agg);
        TestMetricsTableAggregate.HELPER.assertCounter(((pre) + "flushedOutputBytes"), 33, agg);
    }

    @Test
    public void testCompaction() {
        rsm.updateCompaction(tableName, false, 1, 2, 3, 4, 5);
        TestMetricsTableAggregate.HELPER.assertCounter(((pre) + "compactionTime_num_ops"), 1, agg);
        TestMetricsTableAggregate.HELPER.assertCounter(((pre) + "compactionInputFileCount_num_ops"), 1, agg);
        TestMetricsTableAggregate.HELPER.assertCounter(((pre) + "compactionInputSize_num_ops"), 1, agg);
        TestMetricsTableAggregate.HELPER.assertCounter(((pre) + "compactionOutputFileCount_num_ops"), 1, agg);
        TestMetricsTableAggregate.HELPER.assertCounter(((pre) + "compactedInputBytes"), 4, agg);
        TestMetricsTableAggregate.HELPER.assertCounter(((pre) + "compactedoutputBytes"), 5, agg);
        rsm.updateCompaction(tableName, false, 10, 20, 30, 40, 50);
        TestMetricsTableAggregate.HELPER.assertCounter(((pre) + "compactionTime_num_ops"), 2, agg);
        TestMetricsTableAggregate.HELPER.assertCounter(((pre) + "compactionInputFileCount_num_ops"), 2, agg);
        TestMetricsTableAggregate.HELPER.assertCounter(((pre) + "compactionInputSize_num_ops"), 2, agg);
        TestMetricsTableAggregate.HELPER.assertCounter(((pre) + "compactionOutputFileCount_num_ops"), 2, agg);
        TestMetricsTableAggregate.HELPER.assertCounter(((pre) + "compactedInputBytes"), 44, agg);
        TestMetricsTableAggregate.HELPER.assertCounter(((pre) + "compactedoutputBytes"), 55, agg);
        // do major compaction
        rsm.updateCompaction(tableName, true, 100, 200, 300, 400, 500);
        TestMetricsTableAggregate.HELPER.assertCounter(((pre) + "compactionTime_num_ops"), 3, agg);
        TestMetricsTableAggregate.HELPER.assertCounter(((pre) + "compactionInputFileCount_num_ops"), 3, agg);
        TestMetricsTableAggregate.HELPER.assertCounter(((pre) + "compactionInputSize_num_ops"), 3, agg);
        TestMetricsTableAggregate.HELPER.assertCounter(((pre) + "compactionOutputFileCount_num_ops"), 3, agg);
        TestMetricsTableAggregate.HELPER.assertCounter(((pre) + "compactedInputBytes"), 444, agg);
        TestMetricsTableAggregate.HELPER.assertCounter(((pre) + "compactedoutputBytes"), 555, agg);
        TestMetricsTableAggregate.HELPER.assertCounter(((pre) + "majorCompactionTime_num_ops"), 1, agg);
        TestMetricsTableAggregate.HELPER.assertCounter(((pre) + "majorCompactionInputFileCount_num_ops"), 1, agg);
        TestMetricsTableAggregate.HELPER.assertCounter(((pre) + "majorCompactionInputSize_num_ops"), 1, agg);
        TestMetricsTableAggregate.HELPER.assertCounter(((pre) + "majorCompactionOutputFileCount_num_ops"), 1, agg);
        TestMetricsTableAggregate.HELPER.assertCounter(((pre) + "majorCompactedInputBytes"), 400, agg);
        TestMetricsTableAggregate.HELPER.assertCounter(((pre) + "majorCompactedoutputBytes"), 500, agg);
    }

    @Test
    public void testConcurrentUpdate() throws InterruptedException {
        int threadNumber = 10;
        int round = 100;
        AtomicBoolean succ = new AtomicBoolean(true);
        CyclicBarrier barrier = new CyclicBarrier(threadNumber);
        Thread[] threads = IntStream.range(0, threadNumber).mapToObj(( i) -> new Thread(() -> update(succ, round, barrier), ("Update-Worker-" + i))).toArray(Thread[]::new);
        Stream.of(threads).forEach(Thread::start);
        for (Thread t : threads) {
            t.join();
        }
        Assert.assertTrue(succ.get());
    }
}

