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
package org.apache.hadoop.hbase.master;


import ClusterStatusProtos.ServerLoad;
import RegionServerStatusProtos.RegionServerReportRequest.Builder;
import java.io.IOException;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.CompatibilityFactory;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.HBaseTestingUtility;
import org.apache.hadoop.hbase.MiniHBaseCluster;
import org.apache.hadoop.hbase.ServerName;
import org.apache.hadoop.hbase.shaded.protobuf.ProtobufUtil;
import org.apache.hadoop.hbase.shaded.protobuf.generated.ClusterStatusProtos;
import org.apache.hadoop.hbase.shaded.protobuf.generated.RegionServerStatusProtos;
import org.apache.hadoop.hbase.test.MetricsAssertHelper;
import org.apache.hadoop.hbase.testclassification.MasterTests;
import org.apache.hadoop.hbase.testclassification.MediumTests;
import org.apache.zookeeper.KeeperException;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Category({ MasterTests.class, MediumTests.class })
public class TestMasterMetrics {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestMasterMetrics.class);

    private static final Logger LOG = LoggerFactory.getLogger(TestMasterMetrics.class);

    private static final MetricsAssertHelper metricsHelper = CompatibilityFactory.getInstance(MetricsAssertHelper.class);

    private static MiniHBaseCluster cluster;

    private static HMaster master;

    private static HBaseTestingUtility TEST_UTIL = new HBaseTestingUtility();

    public static class MyMaster extends HMaster {
        public MyMaster(Configuration conf) throws IOException, InterruptedException, KeeperException {
            super(conf);
        }

        @Override
        protected void tryRegionServerReport(long reportStartTime, long reportEndTime) {
            // do nothing
        }
    }

    public static class MyRegionServer extends MiniHBaseCluster.MiniHBaseClusterRegionServer {
        public MyRegionServer(Configuration conf) throws IOException, InterruptedException {
            super(conf);
        }

        @Override
        protected void tryRegionServerReport(long reportStartTime, long reportEndTime) {
            // do nothing
        }
    }

    @Test
    public void testClusterRequests() throws Exception {
        // sending fake request to master to see how metric value has changed
        RegionServerStatusProtos.RegionServerReportRequest.Builder request = RegionServerStatusProtos.RegionServerReportRequest.newBuilder();
        ServerName serverName = TestMasterMetrics.cluster.getMaster(0).getServerName();
        request.setServer(ProtobufUtil.toServerName(serverName));
        long expectedRequestNumber = 10000;
        MetricsMasterSource masterSource = TestMasterMetrics.master.getMasterMetrics().getMetricsSource();
        ClusterStatusProtos.ServerLoad sl = ServerLoad.newBuilder().setTotalNumberOfRequests(expectedRequestNumber).build();
        request.setLoad(sl);
        TestMasterMetrics.master.getMasterRpcServices().regionServerReport(null, request.build());
        TestMasterMetrics.metricsHelper.assertCounter("cluster_requests", expectedRequestNumber, masterSource);
        expectedRequestNumber = 15000;
        sl = ServerLoad.newBuilder().setTotalNumberOfRequests(expectedRequestNumber).build();
        request.setLoad(sl);
        TestMasterMetrics.master.getMasterRpcServices().regionServerReport(null, request.build());
        TestMasterMetrics.metricsHelper.assertCounter("cluster_requests", expectedRequestNumber, masterSource);
    }

    @Test
    public void testDefaultMasterMetrics() throws Exception {
        MetricsMasterSource masterSource = TestMasterMetrics.master.getMasterMetrics().getMetricsSource();
        boolean tablesOnMaster = LoadBalancer.isTablesOnMaster(TestMasterMetrics.TEST_UTIL.getConfiguration());
        TestMasterMetrics.metricsHelper.assertGauge("numRegionServers", (1 + (tablesOnMaster ? 1 : 0)), masterSource);
        TestMasterMetrics.metricsHelper.assertGauge("averageLoad", 1, masterSource);
        TestMasterMetrics.metricsHelper.assertGauge("numDeadRegionServers", 0, masterSource);
        TestMasterMetrics.metricsHelper.assertGauge("masterStartTime", TestMasterMetrics.master.getMasterStartTime(), masterSource);
        TestMasterMetrics.metricsHelper.assertGauge("masterActiveTime", TestMasterMetrics.master.getMasterActiveTime(), masterSource);
        TestMasterMetrics.metricsHelper.assertTag("isActiveMaster", "true", masterSource);
        TestMasterMetrics.metricsHelper.assertTag("serverName", TestMasterMetrics.master.getServerName().toString(), masterSource);
        TestMasterMetrics.metricsHelper.assertTag("clusterId", TestMasterMetrics.master.getClusterId(), masterSource);
        TestMasterMetrics.metricsHelper.assertTag("zookeeperQuorum", TestMasterMetrics.master.getZooKeeper().getQuorum(), masterSource);
    }

    @Test
    public void testDefaultMasterProcMetrics() throws Exception {
        MetricsMasterProcSource masterSource = TestMasterMetrics.master.getMasterMetrics().getMetricsProcSource();
        TestMasterMetrics.metricsHelper.assertGauge("numMasterWALs", TestMasterMetrics.master.getNumWALFiles(), masterSource);
    }
}

