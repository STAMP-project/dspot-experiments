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
package org.apache.hadoop.hbase.master.balancer;


import java.util.ArrayList;
import java.util.List;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.HBaseTestingUtility;
import org.apache.hadoop.hbase.HDFSBlocksDistribution;
import org.apache.hadoop.hbase.MiniHBaseCluster;
import org.apache.hadoop.hbase.ServerName;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.RegionInfo;
import org.apache.hadoop.hbase.client.Table;
import org.apache.hadoop.hbase.regionserver.HRegion;
import org.apache.hadoop.hbase.regionserver.HRegionServer;
import org.apache.hadoop.hbase.testclassification.MasterTests;
import org.apache.hadoop.hbase.testclassification.MediumTests;
import org.apache.hadoop.hbase.util.Bytes;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.experimental.categories.Category;


@Category({ MasterTests.class, MediumTests.class })
public class TestRegionLocationFinder {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestRegionLocationFinder.class);

    private static final HBaseTestingUtility TEST_UTIL = new HBaseTestingUtility();

    private static MiniHBaseCluster cluster;

    private static final TableName tableName = TableName.valueOf("table");

    private static final byte[] FAMILY = Bytes.toBytes("cf");

    private static Table table;

    private static final int ServerNum = 5;

    private static RegionLocationFinder finder = new RegionLocationFinder();

    @Test
    public void testInternalGetTopBlockLocation() throws Exception {
        for (int i = 0; i < (TestRegionLocationFinder.ServerNum); i++) {
            HRegionServer server = TestRegionLocationFinder.cluster.getRegionServer(i);
            for (HRegion region : server.getRegions(TestRegionLocationFinder.tableName)) {
                // get region's hdfs block distribution by region and RegionLocationFinder,
                // they should have same result
                HDFSBlocksDistribution blocksDistribution1 = region.getHDFSBlocksDistribution();
                HDFSBlocksDistribution blocksDistribution2 = TestRegionLocationFinder.finder.getBlockDistribution(region.getRegionInfo());
                Assert.assertEquals(blocksDistribution1.getUniqueBlocksTotalWeight(), blocksDistribution2.getUniqueBlocksTotalWeight());
                if ((blocksDistribution1.getUniqueBlocksTotalWeight()) != 0) {
                    Assert.assertEquals(blocksDistribution1.getTopHosts().get(0), blocksDistribution2.getTopHosts().get(0));
                }
            }
        }
    }

    @Test
    public void testMapHostNameToServerName() throws Exception {
        List<String> topHosts = new ArrayList<>();
        for (int i = 0; i < (TestRegionLocationFinder.ServerNum); i++) {
            HRegionServer server = TestRegionLocationFinder.cluster.getRegionServer(i);
            String serverHost = server.getServerName().getHostname();
            if (!(topHosts.contains(serverHost))) {
                topHosts.add(serverHost);
            }
        }
        List<ServerName> servers = TestRegionLocationFinder.finder.mapHostNameToServerName(topHosts);
        // mini cluster, all rs in one host
        Assert.assertEquals(1, topHosts.size());
        for (int i = 0; i < (TestRegionLocationFinder.ServerNum); i++) {
            ServerName server = TestRegionLocationFinder.cluster.getRegionServer(i).getServerName();
            Assert.assertTrue(servers.contains(server));
        }
    }

    @Test
    public void testGetTopBlockLocations() throws Exception {
        for (int i = 0; i < (TestRegionLocationFinder.ServerNum); i++) {
            HRegionServer server = TestRegionLocationFinder.cluster.getRegionServer(i);
            for (HRegion region : server.getRegions(TestRegionLocationFinder.tableName)) {
                List<ServerName> servers = TestRegionLocationFinder.finder.getTopBlockLocations(region.getRegionInfo());
                // test table may have empty region
                if ((region.getHDFSBlocksDistribution().getUniqueBlocksTotalWeight()) == 0) {
                    continue;
                }
                List<String> topHosts = region.getHDFSBlocksDistribution().getTopHosts();
                // rs and datanode may have different host in local machine test
                if (!(topHosts.contains(server.getServerName().getHostname()))) {
                    continue;
                }
                for (int j = 0; j < (TestRegionLocationFinder.ServerNum); j++) {
                    ServerName serverName = TestRegionLocationFinder.cluster.getRegionServer(j).getServerName();
                    Assert.assertTrue(servers.contains(serverName));
                }
            }
        }
    }

    @Test
    public void testRefreshAndWait() throws Exception {
        TestRegionLocationFinder.finder.getCache().invalidateAll();
        for (int i = 0; i < (TestRegionLocationFinder.ServerNum); i++) {
            HRegionServer server = TestRegionLocationFinder.cluster.getRegionServer(i);
            List<HRegion> regions = server.getRegions(TestRegionLocationFinder.tableName);
            if ((regions.size()) <= 0) {
                continue;
            }
            List<RegionInfo> regionInfos = new ArrayList(regions.size());
            for (HRegion region : regions) {
                regionInfos.add(region.getRegionInfo());
            }
            TestRegionLocationFinder.finder.refreshAndWait(regionInfos);
            for (RegionInfo regionInfo : regionInfos) {
                Assert.assertNotNull(TestRegionLocationFinder.finder.getCache().getIfPresent(regionInfo));
            }
        }
    }
}

