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
package org.apache.hadoop.hbase.util;


import HConstants.REGIONSERVER_PORT;
import RegionMover.SERVERSTART_WAIT_MAX_KEY;
import java.io.File;
import java.io.FileWriter;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.HBaseTestingUtility;
import org.apache.hadoop.hbase.MiniHBaseCluster;
import org.apache.hadoop.hbase.regionserver.HRegionServer;
import org.apache.hadoop.hbase.testclassification.MediumTests;
import org.apache.hadoop.hbase.testclassification.MiscTests;
import org.apache.hadoop.hbase.util.RegionMover.RegionMoverBuilder;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Tests for Region Mover Load/Unload functionality with and without ack mode and also to test
 * exclude functionality useful for rack decommissioning
 */
@Category({ MiscTests.class, MediumTests.class })
public class TestRegionMover {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestRegionMover.class);

    private static final Logger LOG = LoggerFactory.getLogger(TestRegionMover.class);

    private static final HBaseTestingUtility TEST_UTIL = new HBaseTestingUtility();

    @Test
    public void testWithAck() throws Exception {
        MiniHBaseCluster cluster = TestRegionMover.TEST_UTIL.getHBaseCluster();
        HRegionServer regionServer = cluster.getRegionServer(0);
        String rsName = regionServer.getServerName().getAddress().toString();
        int numRegions = regionServer.getNumberOfOnlineRegions();
        RegionMoverBuilder rmBuilder = ack(true).maxthreads(8);
        try (RegionMover rm = rmBuilder.build()) {
            TestRegionMover.LOG.info(("Unloading " + (regionServer.getServerName())));
            rm.unload();
            Assert.assertEquals(0, regionServer.getNumberOfOnlineRegions());
            TestRegionMover.LOG.info("Successfully Unloaded\nNow Loading");
            rm.load();
            Assert.assertEquals(numRegions, regionServer.getNumberOfOnlineRegions());
            // Repeat the same load. It should be very fast because all regions are already moved.
            rm.load();
        }
    }

    /**
     * Test to unload a regionserver first and then load it using no Ack mode.
     */
    @Test
    public void testWithoutAck() throws Exception {
        MiniHBaseCluster cluster = TestRegionMover.TEST_UTIL.getHBaseCluster();
        HRegionServer regionServer = cluster.getRegionServer(0);
        String rsName = regionServer.getServerName().getAddress().toString();
        int numRegions = regionServer.getNumberOfOnlineRegions();
        RegionMoverBuilder rmBuilder = new RegionMoverBuilder(rsName, TestRegionMover.TEST_UTIL.getConfiguration()).ack(false);
        try (RegionMover rm = rmBuilder.build()) {
            TestRegionMover.LOG.info(("Unloading " + (regionServer.getServerName())));
            rm.unload();
            TestRegionMover.TEST_UTIL.waitFor(30000, 1000, new org.apache.hadoop.hbase.Waiter.Predicate<Exception>() {
                @Override
                public boolean evaluate() throws Exception {
                    return (regionServer.getNumberOfOnlineRegions()) == 0;
                }
            });
            TestRegionMover.LOG.info("Successfully Unloaded\nNow Loading");
            rm.load();
            // In UT we only have 10 regions so it is not likely to fail, so here we check for all
            // regions, in the real production this may not be true.
            waitFor(30000, 1000, new org.apache.hadoop.hbase.Waiter.Predicate<Exception>() {
                @Override
                public boolean evaluate() throws Exception {
                    return (regionServer.getNumberOfOnlineRegions()) == numRegions;
                }
            });
        }
    }

    /**
     * To test that we successfully exclude a server from the unloading process We test for the number
     * of regions on Excluded server and also test that regions are unloaded successfully
     */
    @Test
    public void testExclude() throws Exception {
        MiniHBaseCluster cluster = TestRegionMover.TEST_UTIL.getHBaseCluster();
        File excludeFile = new File(getDataTestDir().toUri().getPath(), "exclude_file");
        FileWriter fos = new FileWriter(excludeFile);
        HRegionServer excludeServer = cluster.getRegionServer(1);
        String excludeHostname = excludeServer.getServerName().getHostname();
        int excludeServerPort = excludeServer.getServerName().getPort();
        int regionsExcludeServer = excludeServer.getNumberOfOnlineRegions();
        String excludeServerName = (excludeHostname + ":") + (Integer.toString(excludeServerPort));
        fos.write(excludeServerName);
        fos.close();
        HRegionServer regionServer = cluster.getRegionServer(0);
        String rsName = regionServer.getServerName().getHostname();
        int port = regionServer.getServerName().getPort();
        String rs = (rsName + ":") + (Integer.toString(port));
        RegionMoverBuilder rmBuilder = ack(true).excludeFile(excludeFile.getCanonicalPath());
        try (RegionMover rm = rmBuilder.build()) {
            rm.unload();
            TestRegionMover.LOG.info(("Unloading " + rs));
            Assert.assertEquals(0, regionServer.getNumberOfOnlineRegions());
            Assert.assertEquals(regionsExcludeServer, cluster.getRegionServer(1).getNumberOfOnlineRegions());
            TestRegionMover.LOG.info(((("Before:" + regionsExcludeServer) + " After:") + (cluster.getRegionServer(1).getNumberOfOnlineRegions())));
        }
    }

    @Test
    public void testRegionServerPort() {
        MiniHBaseCluster cluster = TestRegionMover.TEST_UTIL.getHBaseCluster();
        HRegionServer regionServer = cluster.getRegionServer(0);
        String rsName = regionServer.getServerName().getHostname();
        final int PORT = 16021;
        Configuration conf = TestRegionMover.TEST_UTIL.getConfiguration();
        String originalPort = conf.get(REGIONSERVER_PORT);
        conf.set(REGIONSERVER_PORT, Integer.toString(PORT));
        RegionMoverBuilder rmBuilder = new RegionMoverBuilder(rsName, conf);
        Assert.assertEquals(PORT, rmBuilder.port);
        if (originalPort != null) {
            conf.set(REGIONSERVER_PORT, originalPort);
        }
    }

    /**
     * UT for HBASE-21746
     */
    @Test
    public void testLoadMetaRegion() throws Exception {
        HRegionServer rsWithMeta = TestRegionMover.TEST_UTIL.getMiniHBaseCluster().getRegionServerThreads().stream().map(( t) -> t.getRegionServer()).filter(( rs) -> (rs.getRegions(TableName.META_TABLE_NAME).size()) > 0).findFirst().get();
        int onlineRegions = rsWithMeta.getNumberOfOnlineRegions();
        String rsName = rsWithMeta.getServerName().getAddress().toString();
        try (RegionMover rm = ack(true).build()) {
            TestRegionMover.LOG.info(("Unloading " + (rsWithMeta.getServerName())));
            rm.unload();
            Assert.assertEquals(0, rsWithMeta.getNumberOfOnlineRegions());
            TestRegionMover.LOG.info(("Loading " + (rsWithMeta.getServerName())));
            rm.load();
            Assert.assertEquals(onlineRegions, rsWithMeta.getNumberOfOnlineRegions());
        }
    }

    /**
     * UT for HBASE-21746
     */
    @Test
    public void testTargetServerDeadWhenLoading() throws Exception {
        HRegionServer rs = TestRegionMover.TEST_UTIL.getMiniHBaseCluster().getRegionServer(0);
        String rsName = rs.getServerName().getAddress().toString();
        Configuration conf = new Configuration(TestRegionMover.TEST_UTIL.getConfiguration());
        // wait 5 seconds at most
        conf.setInt(SERVERSTART_WAIT_MAX_KEY, 5);
        String filename = new org.apache.hadoop.fs.Path(getDataTestDir(), "testTargetServerDeadWhenLoading").toString();
        // unload the region server
        try (RegionMover rm = ack(true).build()) {
            TestRegionMover.LOG.info(("Unloading " + (rs.getServerName())));
            rm.unload();
            Assert.assertEquals(0, rs.getNumberOfOnlineRegions());
        }
        String inexistRsName = "whatever:123";
        try (RegionMover rm = ack(true).build()) {
            // load the regions to an inexist region server, which should fail and return false
            TestRegionMover.LOG.info("Loading to an inexist region server {}", inexistRsName);
            Assert.assertFalse(rm.load());
        }
    }
}

