/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.hadoop.hdfs.server.blockmanagement;


import DiskOp.METADATA;
import DiskOp.READ;
import DiskOp.WRITE;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.google.common.base.Supplier;
import com.google.common.collect.ImmutableMap;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hdfs.DFSConfigKeys;
import org.apache.hadoop.hdfs.HdfsConfiguration;
import org.apache.hadoop.hdfs.MiniDFSCluster;
import org.apache.hadoop.hdfs.server.blockmanagement.SlowDiskTracker.DiskLatency;
import org.apache.hadoop.hdfs.server.datanode.DataNode;
import org.apache.hadoop.hdfs.server.namenode.NameNode;
import org.apache.hadoop.test.GenericTestUtils;
import org.apache.hadoop.util.FakeTimer;
import org.hamcrest.core.Is;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.Timeout;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Tests for {@link SlowDiskTracker}.
 */
public class TestSlowDiskTracker {
    public static final Logger LOG = LoggerFactory.getLogger(TestSlowDiskTracker.class);

    /**
     * Set a timeout for every test case.
     */
    @Rule
    public Timeout testTimeout = new Timeout(300000);

    private static Configuration conf;

    private SlowDiskTracker tracker;

    private FakeTimer timer;

    private long reportValidityMs;

    private static final long OUTLIERS_REPORT_INTERVAL = 1000;

    private static final ObjectReader READER = new ObjectMapper().readerFor(new TypeReference<ArrayList<DiskLatency>>() {});

    static {
        TestSlowDiskTracker.conf = new HdfsConfiguration();
        TestSlowDiskTracker.conf.setLong(DFSConfigKeys.DFS_HEARTBEAT_INTERVAL_KEY, 1L);
        TestSlowDiskTracker.conf.setInt(DFSConfigKeys.DFS_DATANODE_FILEIO_PROFILING_SAMPLING_PERCENTAGE_KEY, 100);
        TestSlowDiskTracker.conf.setTimeDuration(DFSConfigKeys.DFS_DATANODE_OUTLIERS_REPORT_INTERVAL_KEY, TestSlowDiskTracker.OUTLIERS_REPORT_INTERVAL, TimeUnit.MILLISECONDS);
    }

    @Test
    public void testDataNodeHeartbeatSlowDiskReport() throws Exception {
        MiniDFSCluster cluster = new MiniDFSCluster.Builder(TestSlowDiskTracker.conf).numDataNodes(2).build();
        try {
            DataNode dn1 = cluster.getDataNodes().get(0);
            DataNode dn2 = cluster.getDataNodes().get(1);
            NameNode nn = cluster.getNameNode(0);
            DatanodeManager datanodeManager = nn.getNamesystem().getBlockManager().getDatanodeManager();
            SlowDiskTracker slowDiskTracker = datanodeManager.getSlowDiskTracker();
            slowDiskTracker.setReportValidityMs(((TestSlowDiskTracker.OUTLIERS_REPORT_INTERVAL) * 100));
            dn1.getDiskMetrics().addSlowDiskForTesting("disk1", ImmutableMap.of(WRITE, 1.3));
            dn1.getDiskMetrics().addSlowDiskForTesting("disk2", ImmutableMap.of(READ, 1.6, WRITE, 1.1));
            dn2.getDiskMetrics().addSlowDiskForTesting("disk1", ImmutableMap.of(METADATA, 0.8));
            dn2.getDiskMetrics().addSlowDiskForTesting("disk2", ImmutableMap.of(WRITE, 1.3));
            String dn1ID = dn1.getDatanodeId().getIpcAddr(false);
            String dn2ID = dn2.getDatanodeId().getIpcAddr(false);
            // Advance the timer and wait for NN to receive reports from DataNodes.
            Thread.sleep(TestSlowDiskTracker.OUTLIERS_REPORT_INTERVAL);
            // Wait for NN to receive reports from all DNs
            GenericTestUtils.waitFor(new Supplier<Boolean>() {
                @Override
                public Boolean get() {
                    return (slowDiskTracker.getSlowDisksReport().size()) == 4;
                }
            }, 1000, 100000);
            Map<String, DiskLatency> slowDisksReport = getSlowDisksReportForTesting(slowDiskTracker);
            Assert.assertThat(slowDisksReport.size(), Is.is(4));
            Assert.assertTrue(((Math.abs(((slowDisksReport.get((dn1ID + ":disk1")).getLatency(WRITE)) - 1.3))) < 1.0E-7));
            Assert.assertTrue(((Math.abs(((slowDisksReport.get((dn1ID + ":disk2")).getLatency(READ)) - 1.6))) < 1.0E-7));
            Assert.assertTrue(((Math.abs(((slowDisksReport.get((dn1ID + ":disk2")).getLatency(WRITE)) - 1.1))) < 1.0E-7));
            Assert.assertTrue(((Math.abs(((slowDisksReport.get((dn2ID + ":disk1")).getLatency(METADATA)) - 0.8))) < 1.0E-7));
            Assert.assertTrue(((Math.abs(((slowDisksReport.get((dn2ID + ":disk2")).getLatency(WRITE)) - 1.3))) < 1.0E-7));
            // Test the slow disk report JSON string
            ArrayList<DiskLatency> jsonReport = getAndDeserializeJson(slowDiskTracker.getSlowDiskReportAsJsonString());
            Assert.assertThat(jsonReport.size(), Is.is(4));
            Assert.assertTrue(isDiskInReports(jsonReport, dn1ID, "disk1", WRITE, 1.3));
            Assert.assertTrue(isDiskInReports(jsonReport, dn1ID, "disk2", READ, 1.6));
            Assert.assertTrue(isDiskInReports(jsonReport, dn1ID, "disk2", WRITE, 1.1));
            Assert.assertTrue(isDiskInReports(jsonReport, dn2ID, "disk1", METADATA, 0.8));
            Assert.assertTrue(isDiskInReports(jsonReport, dn2ID, "disk2", WRITE, 1.3));
        } finally {
            cluster.shutdown();
        }
    }

    /**
     * Edge case, there are no reports to retrieve.
     */
    @Test
    public void testEmptyReports() {
        tracker.updateSlowDiskReportAsync(timer.monotonicNow());
        Assert.assertTrue(getSlowDisksReportForTesting(tracker).isEmpty());
    }

    @Test
    public void testReportsAreRetrieved() throws Exception {
        addSlowDiskForTesting("dn1", "disk1", ImmutableMap.of(METADATA, 1.1, READ, 1.8));
        addSlowDiskForTesting("dn1", "disk2", ImmutableMap.of(READ, 1.3));
        addSlowDiskForTesting("dn2", "disk2", ImmutableMap.of(READ, 1.1));
        tracker.updateSlowDiskReportAsync(timer.monotonicNow());
        GenericTestUtils.waitFor(new Supplier<Boolean>() {
            @Override
            public Boolean get() {
                return !(tracker.getSlowDisksReport().isEmpty());
            }
        }, 500, 5000);
        Map<String, DiskLatency> reports = getSlowDisksReportForTesting(tracker);
        Assert.assertThat(reports.size(), Is.is(3));
        Assert.assertTrue(((Math.abs(((reports.get("dn1:disk1").getLatency(METADATA)) - 1.1))) < 1.0E-7));
        Assert.assertTrue(((Math.abs(((reports.get("dn1:disk1").getLatency(READ)) - 1.8))) < 1.0E-7));
        Assert.assertTrue(((Math.abs(((reports.get("dn1:disk2").getLatency(READ)) - 1.3))) < 1.0E-7));
        Assert.assertTrue(((Math.abs(((reports.get("dn2:disk2").getLatency(READ)) - 1.1))) < 1.0E-7));
    }

    /**
     * Test that when all reports are expired, we get back nothing.
     */
    @Test
    public void testAllReportsAreExpired() throws Exception {
        addSlowDiskForTesting("dn1", "disk1", ImmutableMap.of(METADATA, 1.1, READ, 1.8));
        addSlowDiskForTesting("dn1", "disk2", ImmutableMap.of(READ, 1.3));
        addSlowDiskForTesting("dn2", "disk2", ImmutableMap.of(WRITE, 1.1));
        // No reports should expire after 1ms.
        timer.advance(1);
        tracker.updateSlowDiskReportAsync(timer.monotonicNow());
        GenericTestUtils.waitFor(new Supplier<Boolean>() {
            @Override
            public Boolean get() {
                return !(tracker.getSlowDisksReport().isEmpty());
            }
        }, 500, 5000);
        Map<String, DiskLatency> reports = getSlowDisksReportForTesting(tracker);
        Assert.assertThat(reports.size(), Is.is(3));
        Assert.assertTrue(((Math.abs(((reports.get("dn1:disk1").getLatency(METADATA)) - 1.1))) < 1.0E-7));
        Assert.assertTrue(((Math.abs(((reports.get("dn1:disk1").getLatency(READ)) - 1.8))) < 1.0E-7));
        Assert.assertTrue(((Math.abs(((reports.get("dn1:disk2").getLatency(READ)) - 1.3))) < 1.0E-7));
        Assert.assertTrue(((Math.abs(((reports.get("dn2:disk2").getLatency(WRITE)) - 1.1))) < 1.0E-7));
        // All reports should expire after REPORT_VALIDITY_MS.
        timer.advance(reportValidityMs);
        tracker.updateSlowDiskReportAsync(timer.monotonicNow());
        GenericTestUtils.waitFor(new Supplier<Boolean>() {
            @Override
            public Boolean get() {
                return tracker.getSlowDisksReport().isEmpty();
            }
        }, 500, 3000);
        reports = getSlowDisksReportForTesting(tracker);
        Assert.assertThat(reports.size(), Is.is(0));
    }

    /**
     * Test the case when a subset of reports has expired.
     * Ensure that we only get back non-expired reports.
     */
    @Test
    public void testSomeReportsAreExpired() throws Exception {
        addSlowDiskForTesting("dn1", "disk1", ImmutableMap.of(METADATA, 1.1, READ, 1.8));
        addSlowDiskForTesting("dn1", "disk2", ImmutableMap.of(READ, 1.3));
        timer.advance(reportValidityMs);
        addSlowDiskForTesting("dn2", "disk2", ImmutableMap.of(WRITE, 1.1));
        tracker.updateSlowDiskReportAsync(timer.monotonicNow());
        GenericTestUtils.waitFor(new Supplier<Boolean>() {
            @Override
            public Boolean get() {
                return !(tracker.getSlowDisksReport().isEmpty());
            }
        }, 500, 5000);
        Map<String, DiskLatency> reports = getSlowDisksReportForTesting(tracker);
        Assert.assertThat(reports.size(), Is.is(1));
        Assert.assertTrue(((Math.abs(((reports.get("dn2:disk2").getLatency(WRITE)) - 1.1))) < 1.0E-7));
    }

    /**
     * Test the case when an expired report is replaced by a valid one.
     */
    @Test
    public void testReplacement() throws Exception {
        addSlowDiskForTesting("dn1", "disk1", ImmutableMap.of(METADATA, 1.1, READ, 1.8));
        timer.advance(reportValidityMs);
        addSlowDiskForTesting("dn1", "disk1", ImmutableMap.of(READ, 1.4));
        tracker.updateSlowDiskReportAsync(timer.monotonicNow());
        GenericTestUtils.waitFor(new Supplier<Boolean>() {
            @Override
            public Boolean get() {
                return !(tracker.getSlowDisksReport().isEmpty());
            }
        }, 500, 5000);
        Map<String, DiskLatency> reports = getSlowDisksReportForTesting(tracker);
        Assert.assertThat(reports.size(), Is.is(1));
        Assert.assertTrue(((reports.get("dn1:disk1").getLatency(METADATA)) == null));
        Assert.assertTrue(((Math.abs(((reports.get("dn1:disk1").getLatency(READ)) - 1.4))) < 1.0E-7));
    }

    @Test
    public void testGetJson() throws Exception {
        addSlowDiskForTesting("dn1", "disk1", ImmutableMap.of(METADATA, 1.1, READ, 1.8));
        addSlowDiskForTesting("dn1", "disk2", ImmutableMap.of(READ, 1.3));
        addSlowDiskForTesting("dn2", "disk2", ImmutableMap.of(WRITE, 1.1));
        addSlowDiskForTesting("dn3", "disk1", ImmutableMap.of(WRITE, 1.1));
        tracker.updateSlowDiskReportAsync(timer.monotonicNow());
        GenericTestUtils.waitFor(new Supplier<Boolean>() {
            @Override
            public Boolean get() {
                return (tracker.getSlowDiskReportAsJsonString()) != null;
            }
        }, 500, 5000);
        ArrayList<DiskLatency> jsonReport = getAndDeserializeJson(tracker.getSlowDiskReportAsJsonString());
        // And ensure its contents are what we expect.
        Assert.assertThat(jsonReport.size(), Is.is(4));
        Assert.assertTrue(isDiskInReports(jsonReport, "dn1", "disk1", METADATA, 1.1));
        Assert.assertTrue(isDiskInReports(jsonReport, "dn1", "disk1", READ, 1.8));
        Assert.assertTrue(isDiskInReports(jsonReport, "dn1", "disk2", READ, 1.3));
        Assert.assertTrue(isDiskInReports(jsonReport, "dn2", "disk2", WRITE, 1.1));
        Assert.assertTrue(isDiskInReports(jsonReport, "dn3", "disk1", WRITE, 1.1));
    }

    @Test
    public void testGetJsonSizeIsLimited() throws Exception {
        addSlowDiskForTesting("dn1", "disk1", ImmutableMap.of(READ, 1.1));
        addSlowDiskForTesting("dn1", "disk2", ImmutableMap.of(READ, 1.2));
        addSlowDiskForTesting("dn1", "disk3", ImmutableMap.of(READ, 1.3));
        addSlowDiskForTesting("dn2", "disk1", ImmutableMap.of(READ, 1.4));
        addSlowDiskForTesting("dn2", "disk2", ImmutableMap.of(READ, 1.5));
        addSlowDiskForTesting("dn3", "disk1", ImmutableMap.of(WRITE, 1.6));
        addSlowDiskForTesting("dn3", "disk2", ImmutableMap.of(READ, 1.7));
        addSlowDiskForTesting("dn3", "disk3", ImmutableMap.of(READ, 1.2));
        tracker.updateSlowDiskReportAsync(timer.monotonicNow());
        GenericTestUtils.waitFor(new Supplier<Boolean>() {
            @Override
            public Boolean get() {
                return (tracker.getSlowDiskReportAsJsonString()) != null;
            }
        }, 500, 5000);
        ArrayList<DiskLatency> jsonReport = getAndDeserializeJson(tracker.getSlowDiskReportAsJsonString());
        // Ensure that only the top 5 highest latencies are in the report.
        Assert.assertThat(jsonReport.size(), Is.is(5));
        Assert.assertTrue(isDiskInReports(jsonReport, "dn3", "disk2", READ, 1.7));
        Assert.assertTrue(isDiskInReports(jsonReport, "dn3", "disk1", WRITE, 1.6));
        Assert.assertTrue(isDiskInReports(jsonReport, "dn2", "disk2", READ, 1.5));
        Assert.assertTrue(isDiskInReports(jsonReport, "dn2", "disk1", READ, 1.4));
        Assert.assertTrue(isDiskInReports(jsonReport, "dn1", "disk3", READ, 1.3));
        // Remaining nodes should be in the list.
        Assert.assertFalse(isDiskInReports(jsonReport, "dn1", "disk1", READ, 1.1));
        Assert.assertFalse(isDiskInReports(jsonReport, "dn1", "disk2", READ, 1.2));
        Assert.assertFalse(isDiskInReports(jsonReport, "dn3", "disk3", READ, 1.2));
    }

    @Test
    public void testEmptyReport() throws Exception {
        addSlowDiskForTesting("dn1", "disk1", ImmutableMap.of(READ, 1.1));
        timer.advance(reportValidityMs);
        tracker.updateSlowDiskReportAsync(timer.monotonicNow());
        Thread.sleep(((TestSlowDiskTracker.OUTLIERS_REPORT_INTERVAL) * 2));
        Assert.assertTrue(((tracker.getSlowDiskReportAsJsonString()) == null));
    }
}

