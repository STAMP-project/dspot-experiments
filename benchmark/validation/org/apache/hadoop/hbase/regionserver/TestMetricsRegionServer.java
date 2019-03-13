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


import JvmPauseMonitor.INFO_THRESHOLD_KEY;
import JvmPauseMonitor.WARN_THRESHOLD_KEY;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.CompatibilityFactory;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.test.MetricsAssertHelper;
import org.apache.hadoop.hbase.testclassification.RegionServerTests;
import org.apache.hadoop.hbase.testclassification.SmallTests;
import org.apache.hadoop.hbase.util.JvmPauseMonitor;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.experimental.categories.Category;


/**
 * Unit test version of rs metrics tests.
 */
@Category({ RegionServerTests.class, SmallTests.class })
public class TestMetricsRegionServer {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestMetricsRegionServer.class);

    public static MetricsAssertHelper HELPER = CompatibilityFactory.getInstance(MetricsAssertHelper.class);

    private MetricsRegionServerWrapperStub wrapper;

    private MetricsRegionServer rsm;

    private MetricsRegionServerSource serverSource;

    @Test
    public void testWrapperSource() {
        TestMetricsRegionServer.HELPER.assertTag("serverName", "test", serverSource);
        TestMetricsRegionServer.HELPER.assertTag("clusterId", "tClusterId", serverSource);
        TestMetricsRegionServer.HELPER.assertTag("zookeeperQuorum", "zk", serverSource);
        TestMetricsRegionServer.HELPER.assertGauge("regionServerStartTime", 100, serverSource);
        TestMetricsRegionServer.HELPER.assertGauge("regionCount", 101, serverSource);
        TestMetricsRegionServer.HELPER.assertGauge("storeCount", 2, serverSource);
        TestMetricsRegionServer.HELPER.assertGauge("maxStoreFileAge", 2, serverSource);
        TestMetricsRegionServer.HELPER.assertGauge("minStoreFileAge", 2, serverSource);
        TestMetricsRegionServer.HELPER.assertGauge("avgStoreFileAge", 2, serverSource);
        TestMetricsRegionServer.HELPER.assertGauge("numReferenceFiles", 2, serverSource);
        TestMetricsRegionServer.HELPER.assertGauge("hlogFileCount", 10, serverSource);
        TestMetricsRegionServer.HELPER.assertGauge("hlogFileSize", 1024000, serverSource);
        TestMetricsRegionServer.HELPER.assertGauge("storeFileCount", 300, serverSource);
        TestMetricsRegionServer.HELPER.assertGauge("memstoreSize", 1025, serverSource);
        TestMetricsRegionServer.HELPER.assertGauge("storeFileSize", 1900, serverSource);
        TestMetricsRegionServer.HELPER.assertGauge("storeFileSizeGrowthRate", 50.0, serverSource);
        TestMetricsRegionServer.HELPER.assertCounter("totalRequestCount", 899, serverSource);
        TestMetricsRegionServer.HELPER.assertCounter("totalRowActionRequestCount", ((TestMetricsRegionServer.HELPER.getCounter("readRequestCount", serverSource)) + (TestMetricsRegionServer.HELPER.getCounter("writeRequestCount", serverSource))), serverSource);
        TestMetricsRegionServer.HELPER.assertCounter("readRequestCount", 997, serverSource);
        TestMetricsRegionServer.HELPER.assertCounter("cpRequestCount", 998, serverSource);
        TestMetricsRegionServer.HELPER.assertCounter("filteredReadRequestCount", 1997, serverSource);
        TestMetricsRegionServer.HELPER.assertCounter("writeRequestCount", 707, serverSource);
        TestMetricsRegionServer.HELPER.assertCounter("checkMutateFailedCount", 401, serverSource);
        TestMetricsRegionServer.HELPER.assertCounter("checkMutatePassedCount", 405, serverSource);
        TestMetricsRegionServer.HELPER.assertGauge("storeFileIndexSize", 406, serverSource);
        TestMetricsRegionServer.HELPER.assertGauge("staticIndexSize", 407, serverSource);
        TestMetricsRegionServer.HELPER.assertGauge("staticBloomSize", 408, serverSource);
        TestMetricsRegionServer.HELPER.assertGauge("mutationsWithoutWALCount", 409, serverSource);
        TestMetricsRegionServer.HELPER.assertGauge("mutationsWithoutWALSize", 410, serverSource);
        TestMetricsRegionServer.HELPER.assertGauge("percentFilesLocal", 99, serverSource);
        TestMetricsRegionServer.HELPER.assertGauge("percentFilesLocalSecondaryRegions", 99, serverSource);
        TestMetricsRegionServer.HELPER.assertGauge("compactionQueueLength", 411, serverSource);
        TestMetricsRegionServer.HELPER.assertGauge("flushQueueLength", 412, serverSource);
        TestMetricsRegionServer.HELPER.assertGauge("blockCacheFreeSize", 413, serverSource);
        TestMetricsRegionServer.HELPER.assertGauge("blockCacheCount", 414, serverSource);
        TestMetricsRegionServer.HELPER.assertGauge("blockCacheSize", 415, serverSource);
        TestMetricsRegionServer.HELPER.assertCounter("blockCacheHitCount", 416, serverSource);
        TestMetricsRegionServer.HELPER.assertCounter("blockCacheMissCount", 417, serverSource);
        TestMetricsRegionServer.HELPER.assertCounter("blockCacheEvictionCount", 418, serverSource);
        TestMetricsRegionServer.HELPER.assertGauge("blockCacheCountHitPercent", 98, serverSource);
        TestMetricsRegionServer.HELPER.assertGauge("blockCacheExpressHitPercent", 97, serverSource);
        TestMetricsRegionServer.HELPER.assertCounter("blockCacheFailedInsertionCount", 36, serverSource);
        TestMetricsRegionServer.HELPER.assertGauge("l1CacheHitCount", 200, serverSource);
        TestMetricsRegionServer.HELPER.assertGauge("l1CacheMissCount", 100, serverSource);
        TestMetricsRegionServer.HELPER.assertGauge("l1CacheHitRatio", 80, serverSource);
        TestMetricsRegionServer.HELPER.assertGauge("l1CacheMissRatio", 20, serverSource);
        TestMetricsRegionServer.HELPER.assertGauge("l2CacheHitCount", 800, serverSource);
        TestMetricsRegionServer.HELPER.assertGauge("l2CacheMissCount", 200, serverSource);
        TestMetricsRegionServer.HELPER.assertGauge("l2CacheHitRatio", 90, serverSource);
        TestMetricsRegionServer.HELPER.assertGauge("l2CacheMissRatio", 10, serverSource);
        TestMetricsRegionServer.HELPER.assertCounter("updatesBlockedTime", 419, serverSource);
    }

    @Test
    public void testConstuctor() {
        Assert.assertNotNull("There should be a hadoop1/hadoop2 metrics source", rsm.getMetricsSource());
        Assert.assertNotNull("The RegionServerMetricsWrapper should be accessable", rsm.getRegionServerWrapper());
    }

    @Test
    public void testSlowCount() {
        for (int i = 0; i < 12; i++) {
            rsm.updateAppend(null, 12);
            rsm.updateAppend(null, 1002);
        }
        for (int i = 0; i < 13; i++) {
            rsm.updateDeleteBatch(null, 13);
            rsm.updateDeleteBatch(null, 1003);
        }
        for (int i = 0; i < 14; i++) {
            rsm.updateGet(null, 14);
            rsm.updateGet(null, 1004);
        }
        for (int i = 0; i < 15; i++) {
            rsm.updateIncrement(null, 15);
            rsm.updateIncrement(null, 1005);
        }
        for (int i = 0; i < 16; i++) {
            rsm.updatePutBatch(null, 16);
            rsm.updatePutBatch(null, 1006);
        }
        for (int i = 0; i < 17; i++) {
            rsm.updatePut(null, 17);
            rsm.updateDelete(null, 17);
            rsm.updateCheckAndDelete(17);
            rsm.updateCheckAndPut(17);
        }
        TestMetricsRegionServer.HELPER.assertCounter("appendNumOps", 24, serverSource);
        TestMetricsRegionServer.HELPER.assertCounter("deleteBatchNumOps", 26, serverSource);
        TestMetricsRegionServer.HELPER.assertCounter("getNumOps", 28, serverSource);
        TestMetricsRegionServer.HELPER.assertCounter("incrementNumOps", 30, serverSource);
        TestMetricsRegionServer.HELPER.assertCounter("putBatchNumOps", 32, serverSource);
        TestMetricsRegionServer.HELPER.assertCounter("putNumOps", 17, serverSource);
        TestMetricsRegionServer.HELPER.assertCounter("deleteNumOps", 17, serverSource);
        TestMetricsRegionServer.HELPER.assertCounter("checkAndDeleteNumOps", 17, serverSource);
        TestMetricsRegionServer.HELPER.assertCounter("checkAndPutNumOps", 17, serverSource);
        TestMetricsRegionServer.HELPER.assertCounter("slowAppendCount", 12, serverSource);
        TestMetricsRegionServer.HELPER.assertCounter("slowDeleteCount", 13, serverSource);
        TestMetricsRegionServer.HELPER.assertCounter("slowGetCount", 14, serverSource);
        TestMetricsRegionServer.HELPER.assertCounter("slowIncrementCount", 15, serverSource);
        TestMetricsRegionServer.HELPER.assertCounter("slowPutCount", 16, serverSource);
    }

    String FLUSH_TIME = "flushTime";

    String FLUSH_TIME_DESC = "Histogram for the time in millis for memstore flush";

    String FLUSH_MEMSTORE_SIZE = "flushMemstoreSize";

    String FLUSH_MEMSTORE_SIZE_DESC = "Histogram for number of bytes in the memstore for a flush";

    String FLUSH_FILE_SIZE = "flushFileSize";

    String FLUSH_FILE_SIZE_DESC = "Histogram for number of bytes in the resulting file for a flush";

    String FLUSHED_OUTPUT_BYTES = "flushedOutputBytes";

    String FLUSHED_OUTPUT_BYTES_DESC = "Total number of bytes written from flush";

    String FLUSHED_MEMSTORE_BYTES = "flushedMemstoreBytes";

    String FLUSHED_MEMSTORE_BYTES_DESC = "Total number of bytes of cells in memstore from flush";

    @Test
    public void testFlush() {
        rsm.updateFlush(null, 1, 2, 3);
        TestMetricsRegionServer.HELPER.assertCounter("flushTime_num_ops", 1, serverSource);
        TestMetricsRegionServer.HELPER.assertCounter("flushMemstoreSize_num_ops", 1, serverSource);
        TestMetricsRegionServer.HELPER.assertCounter("flushOutputSize_num_ops", 1, serverSource);
        TestMetricsRegionServer.HELPER.assertCounter("flushedMemstoreBytes", 2, serverSource);
        TestMetricsRegionServer.HELPER.assertCounter("flushedOutputBytes", 3, serverSource);
        rsm.updateFlush(null, 10, 20, 30);
        TestMetricsRegionServer.HELPER.assertCounter("flushTimeNumOps", 2, serverSource);
        TestMetricsRegionServer.HELPER.assertCounter("flushMemstoreSize_num_ops", 2, serverSource);
        TestMetricsRegionServer.HELPER.assertCounter("flushOutputSize_num_ops", 2, serverSource);
        TestMetricsRegionServer.HELPER.assertCounter("flushedMemstoreBytes", 22, serverSource);
        TestMetricsRegionServer.HELPER.assertCounter("flushedOutputBytes", 33, serverSource);
    }

    @Test
    public void testCompaction() {
        rsm.updateCompaction(null, false, 1, 2, 3, 4, 5);
        TestMetricsRegionServer.HELPER.assertCounter("compactionTime_num_ops", 1, serverSource);
        TestMetricsRegionServer.HELPER.assertCounter("compactionInputFileCount_num_ops", 1, serverSource);
        TestMetricsRegionServer.HELPER.assertCounter("compactionInputSize_num_ops", 1, serverSource);
        TestMetricsRegionServer.HELPER.assertCounter("compactionOutputFileCount_num_ops", 1, serverSource);
        TestMetricsRegionServer.HELPER.assertCounter("compactedInputBytes", 4, serverSource);
        TestMetricsRegionServer.HELPER.assertCounter("compactedoutputBytes", 5, serverSource);
        rsm.updateCompaction(null, false, 10, 20, 30, 40, 50);
        TestMetricsRegionServer.HELPER.assertCounter("compactionTime_num_ops", 2, serverSource);
        TestMetricsRegionServer.HELPER.assertCounter("compactionInputFileCount_num_ops", 2, serverSource);
        TestMetricsRegionServer.HELPER.assertCounter("compactionInputSize_num_ops", 2, serverSource);
        TestMetricsRegionServer.HELPER.assertCounter("compactionOutputFileCount_num_ops", 2, serverSource);
        TestMetricsRegionServer.HELPER.assertCounter("compactedInputBytes", 44, serverSource);
        TestMetricsRegionServer.HELPER.assertCounter("compactedoutputBytes", 55, serverSource);
        // do major compaction
        rsm.updateCompaction(null, true, 100, 200, 300, 400, 500);
        TestMetricsRegionServer.HELPER.assertCounter("compactionTime_num_ops", 3, serverSource);
        TestMetricsRegionServer.HELPER.assertCounter("compactionInputFileCount_num_ops", 3, serverSource);
        TestMetricsRegionServer.HELPER.assertCounter("compactionInputSize_num_ops", 3, serverSource);
        TestMetricsRegionServer.HELPER.assertCounter("compactionOutputFileCount_num_ops", 3, serverSource);
        TestMetricsRegionServer.HELPER.assertCounter("compactedInputBytes", 444, serverSource);
        TestMetricsRegionServer.HELPER.assertCounter("compactedoutputBytes", 555, serverSource);
        TestMetricsRegionServer.HELPER.assertCounter("majorCompactionTime_num_ops", 1, serverSource);
        TestMetricsRegionServer.HELPER.assertCounter("majorCompactionInputFileCount_num_ops", 1, serverSource);
        TestMetricsRegionServer.HELPER.assertCounter("majorCompactionInputSize_num_ops", 1, serverSource);
        TestMetricsRegionServer.HELPER.assertCounter("majorCompactionOutputFileCount_num_ops", 1, serverSource);
        TestMetricsRegionServer.HELPER.assertCounter("majorCompactedInputBytes", 400, serverSource);
        TestMetricsRegionServer.HELPER.assertCounter("majorCompactedoutputBytes", 500, serverSource);
    }

    @Test
    public void testPauseMonitor() {
        Configuration conf = new Configuration();
        conf.setLong(INFO_THRESHOLD_KEY, 1000L);
        conf.setLong(WARN_THRESHOLD_KEY, 10000L);
        JvmPauseMonitor monitor = new JvmPauseMonitor(conf, serverSource);
        monitor.updateMetrics(1500, false);
        TestMetricsRegionServer.HELPER.assertCounter("pauseInfoThresholdExceeded", 1, serverSource);
        TestMetricsRegionServer.HELPER.assertCounter("pauseWarnThresholdExceeded", 0, serverSource);
        TestMetricsRegionServer.HELPER.assertCounter("pauseTimeWithoutGc_num_ops", 1, serverSource);
        TestMetricsRegionServer.HELPER.assertCounter("pauseTimeWithGc_num_ops", 0, serverSource);
        monitor.updateMetrics(15000, true);
        TestMetricsRegionServer.HELPER.assertCounter("pauseInfoThresholdExceeded", 1, serverSource);
        TestMetricsRegionServer.HELPER.assertCounter("pauseWarnThresholdExceeded", 1, serverSource);
        TestMetricsRegionServer.HELPER.assertCounter("pauseTimeWithoutGc_num_ops", 1, serverSource);
        TestMetricsRegionServer.HELPER.assertCounter("pauseTimeWithGc_num_ops", 1, serverSource);
    }
}

