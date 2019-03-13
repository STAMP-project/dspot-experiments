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
package org.apache.hadoop.hbase.client;


import Option.LIVE_SERVERS;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.Collection;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.ClusterMetrics;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.RegionMetrics;
import org.apache.hadoop.hbase.ServerMetrics;
import org.apache.hadoop.hbase.ServerName;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.regionserver.HRegion;
import org.apache.hadoop.hbase.regionserver.HRegionServer;
import org.apache.hadoop.hbase.testclassification.ClientTests;
import org.apache.hadoop.hbase.testclassification.LargeTests;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.hbase.wal.AbstractFSWALProvider;
import org.apache.hbase.thirdparty.com.google.common.collect.Lists;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;


@RunWith(Parameterized.class)
@Category({ ClientTests.class, LargeTests.class })
public class TestAsyncClusterAdminApi extends TestAsyncAdminBase {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestAsyncClusterAdminApi.class);

    private final Path cnfPath = FileSystems.getDefault().getPath("target/test-classes/hbase-site.xml");

    private final Path cnf2Path = FileSystems.getDefault().getPath("target/test-classes/hbase-site2.xml");

    private final Path cnf3Path = FileSystems.getDefault().getPath("target/test-classes/hbase-site3.xml");

    @Test
    public void testGetMasterInfoPort() throws Exception {
        Assert.assertEquals(TestAsyncAdminBase.TEST_UTIL.getHBaseCluster().getMaster().getInfoServer().getPort(), ((int) (admin.getMasterInfoPort().get())));
    }

    @Test
    public void testRegionServerOnlineConfigChange() throws Exception {
        replaceHBaseSiteXML();
        admin.getRegionServers().get().forEach(( server) -> admin.updateConfiguration(server).join());
        // Check the configuration of the RegionServers
        TestAsyncAdminBase.TEST_UTIL.getMiniHBaseCluster().getRegionServerThreads().forEach(( thread) -> {
            Configuration conf = thread.getRegionServer().getConfiguration();
            assertEquals(1000, conf.getInt("hbase.custom.config", 0));
        });
        restoreHBaseSiteXML();
    }

    @Test
    public void testMasterOnlineConfigChange() throws Exception {
        replaceHBaseSiteXML();
        ServerName master = admin.getMaster().get();
        admin.updateConfiguration(master).join();
        admin.getBackupMasters().get().forEach(( backupMaster) -> admin.updateConfiguration(backupMaster).join());
        // Check the configuration of the Masters
        TestAsyncAdminBase.TEST_UTIL.getMiniHBaseCluster().getMasterThreads().forEach(( thread) -> {
            Configuration conf = thread.getMaster().getConfiguration();
            assertEquals(1000, conf.getInt("hbase.custom.config", 0));
        });
        restoreHBaseSiteXML();
    }

    @Test
    public void testAllClusterOnlineConfigChange() throws IOException {
        replaceHBaseSiteXML();
        admin.updateConfiguration().join();
        // Check the configuration of the Masters
        TestAsyncAdminBase.TEST_UTIL.getMiniHBaseCluster().getMasterThreads().forEach(( thread) -> {
            Configuration conf = thread.getMaster().getConfiguration();
            assertEquals(1000, conf.getInt("hbase.custom.config", 0));
        });
        // Check the configuration of the RegionServers
        TestAsyncAdminBase.TEST_UTIL.getMiniHBaseCluster().getRegionServerThreads().forEach(( thread) -> {
            Configuration conf = thread.getRegionServer().getConfiguration();
            assertEquals(1000, conf.getInt("hbase.custom.config", 0));
        });
        restoreHBaseSiteXML();
    }

    @Test
    public void testRollWALWALWriter() throws Exception {
        setUpforLogRolling();
        String className = this.getClass().getName();
        StringBuilder v = new StringBuilder(className);
        while ((v.length()) < 1000) {
            v.append(className);
        } 
        byte[] value = Bytes.toBytes(v.toString());
        HRegionServer regionServer = startAndWriteData(tableName, value);
        TestAsyncAdminBase.LOG.info((("after writing there are " + (AbstractFSWALProvider.getNumRolledLogFiles(regionServer.getWAL(null)))) + " log files"));
        // flush all regions
        for (HRegion r : regionServer.getOnlineRegionsLocalContext()) {
            r.flush(true);
        }
        admin.rollWALWriter(regionServer.getServerName()).join();
        int count = AbstractFSWALProvider.getNumRolledLogFiles(regionServer.getWAL(null));
        TestAsyncAdminBase.LOG.info((("after flushing all regions and rolling logs there are " + count) + " log files"));
        Assert.assertTrue(("actual count: " + count), (count <= 2));
    }

    @Test
    public void testGetRegionLoads() throws Exception {
        // Turn off the balancer
        admin.balancerSwitch(false).join();
        TableName[] tables = new TableName[]{ TableName.valueOf(((tableName.getNameAsString()) + "1")), TableName.valueOf(((tableName.getNameAsString()) + "2")), TableName.valueOf(((tableName.getNameAsString()) + "3")) };
        createAndLoadTable(tables);
        // Sleep to wait region server report
        Thread.sleep(((TestAsyncAdminBase.TEST_UTIL.getConfiguration().getInt("hbase.regionserver.msginterval", (3 * 1000))) * 2));
        // Check if regions match with the regionLoad from the server
        Collection<ServerName> servers = admin.getRegionServers().get();
        for (ServerName serverName : servers) {
            List<RegionInfo> regions = admin.getRegions(serverName).get();
            checkRegionsAndRegionLoads(regions, admin.getRegionMetrics(serverName).get());
        }
        // Check if regionLoad matches the table's regions and nothing is missed
        for (TableName table : tables) {
            List<RegionInfo> tableRegions = admin.getRegions(table).get();
            List<RegionMetrics> regionLoads = Lists.newArrayList();
            for (ServerName serverName : servers) {
                regionLoads.addAll(admin.getRegionMetrics(serverName, table).get());
            }
            checkRegionsAndRegionLoads(tableRegions, regionLoads);
        }
        // Check RegionLoad matches the regionLoad from ClusterStatus
        ClusterMetrics clusterStatus = admin.getClusterMetrics(EnumSet.of(LIVE_SERVERS)).get();
        for (Map.Entry<ServerName, ServerMetrics> entry : clusterStatus.getLiveServerMetrics().entrySet()) {
            ServerName sn = entry.getKey();
            ServerMetrics sm = entry.getValue();
            compareRegionLoads(sm.getRegionMetrics().values(), admin.getRegionMetrics(sn).get());
        }
        for (ServerName serverName : clusterStatus.getLiveServerMetrics().keySet()) {
            ServerMetrics serverLoad = clusterStatus.getLiveServerMetrics().get(serverName);
        }
    }
}

