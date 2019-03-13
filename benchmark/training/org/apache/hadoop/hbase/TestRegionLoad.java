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
package org.apache.hadoop.hbase;


import Option.LIVE_SERVERS;
import java.util.Collection;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import org.apache.hadoop.hbase.client.Admin;
import org.apache.hadoop.hbase.testclassification.MediumTests;
import org.apache.hadoop.hbase.testclassification.MiscTests;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hbase.thirdparty.com.google.common.collect.Lists;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Category({ MiscTests.class, MediumTests.class })
public class TestRegionLoad {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestRegionLoad.class);

    private static final Logger LOG = LoggerFactory.getLogger(TestRegionLoad.class);

    private static final HBaseTestingUtility UTIL = new HBaseTestingUtility();

    private static Admin admin;

    private static final TableName TABLE_1 = TableName.valueOf("table_1");

    private static final TableName TABLE_2 = TableName.valueOf("table_2");

    private static final TableName TABLE_3 = TableName.valueOf("table_3");

    private static final TableName[] tables = new TableName[]{ TestRegionLoad.TABLE_1, TestRegionLoad.TABLE_2, TestRegionLoad.TABLE_3 };

    private static final int MSG_INTERVAL = 500;// ms


    @Test
    public void testRegionLoad() throws Exception {
        // Check if regions match with the regionLoad from the server
        for (ServerName serverName : TestRegionLoad.admin.getClusterMetrics(EnumSet.of(LIVE_SERVERS)).getLiveServerMetrics().keySet()) {
            List<HRegionInfo> regions = TestRegionLoad.admin.getOnlineRegions(serverName);
            TestRegionLoad.LOG.info(((("serverName=" + serverName) + ", regions=") + (regions.stream().map(( r) -> r.getRegionNameAsString()).collect(Collectors.toList()))));
            Collection<RegionLoad> regionLoads = TestRegionLoad.admin.getRegionMetrics(serverName).stream().map(( r) -> new RegionLoad(r)).collect(Collectors.toList());
            TestRegionLoad.LOG.info(((("serverName=" + serverName) + ", regionLoads=") + (regionLoads.stream().map(( r) -> Bytes.toString(r.getRegionName())).collect(Collectors.toList()))));
            checkRegionsAndRegionLoads(regions, regionLoads);
        }
        // Check if regionLoad matches the table's regions and nothing is missed
        for (TableName table : new TableName[]{ TestRegionLoad.TABLE_1, TestRegionLoad.TABLE_2, TestRegionLoad.TABLE_3 }) {
            List<HRegionInfo> tableRegions = TestRegionLoad.admin.getTableRegions(table);
            List<RegionLoad> regionLoads = Lists.newArrayList();
            for (ServerName serverName : TestRegionLoad.admin.getClusterMetrics(EnumSet.of(LIVE_SERVERS)).getLiveServerMetrics().keySet()) {
                regionLoads.addAll(TestRegionLoad.admin.getRegionMetrics(serverName, table).stream().map(( r) -> new RegionLoad(r)).collect(Collectors.toList()));
            }
            checkRegionsAndRegionLoads(tableRegions, regionLoads);
        }
        // Just wait here. If this fixes the test, come back and do a better job.
        // Would have to redo the below so can wait on cluster status changing.
        // Admin#getClusterMetrics retrieves data from HMaster. Admin#getRegionMetrics, by contrast,
        // get the data from RS. Hence, it will fail if we do the assert check before RS has done
        // the report.
        TimeUnit.MILLISECONDS.sleep((3 * (TestRegionLoad.MSG_INTERVAL)));
        // Check RegionLoad matches the regionLoad from ClusterStatus
        ClusterStatus clusterStatus = new ClusterStatus(TestRegionLoad.admin.getClusterMetrics(EnumSet.of(LIVE_SERVERS)));
        for (ServerName serverName : clusterStatus.getServers()) {
            ServerLoad serverLoad = clusterStatus.getLoad(serverName);
            Map<byte[], RegionLoad> regionLoads = TestRegionLoad.admin.getRegionMetrics(serverName).stream().collect(Collectors.toMap(( e) -> e.getRegionName(), ( e) -> new RegionLoad(e), ( v1, v2) -> {
                throw new RuntimeException("impossible!!");
            }, () -> new TreeMap<>(Bytes.BYTES_COMPARATOR)));
            TestRegionLoad.LOG.debug(((("serverName=" + serverName) + ", getRegionLoads=") + (serverLoad.getRegionsLoad().keySet().stream().map(( r) -> Bytes.toString(r)).collect(Collectors.toList()))));
            TestRegionLoad.LOG.debug(((("serverName=" + serverName) + ", regionLoads=") + (regionLoads.keySet().stream().map(( r) -> Bytes.toString(r)).collect(Collectors.toList()))));
            compareRegionLoads(serverLoad.getRegionsLoad(), regionLoads);
        }
    }
}

