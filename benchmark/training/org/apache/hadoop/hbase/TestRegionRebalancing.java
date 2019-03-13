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


import java.io.IOException;
import java.util.Arrays;
import org.apache.hadoop.hbase.client.Admin;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.ConnectionFactory;
import org.apache.hadoop.hbase.client.RegionLocator;
import org.apache.hadoop.hbase.testclassification.FlakeyTests;
import org.apache.hadoop.hbase.testclassification.LargeTests;
import org.apache.hadoop.hbase.util.Bytes;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Test whether region re-balancing works. (HBASE-71)
 * The test only works for cluster wide balancing, not per table wide.
 * Increase the margin a little to make StochasticLoadBalancer result acceptable.
 */
@Category({ FlakeyTests.class, LargeTests.class })
@RunWith(Parameterized.class)
public class TestRegionRebalancing {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestRegionRebalancing.class);

    private static final byte[] FAMILY_NAME = Bytes.toBytes("col");

    private static final Logger LOG = LoggerFactory.getLogger(TestRegionRebalancing.class);

    private final HBaseTestingUtility UTIL = new HBaseTestingUtility();

    private RegionLocator regionLocator;

    private HTableDescriptor desc;

    private String balancerName;

    public TestRegionRebalancing(String balancerName) {
        this.balancerName = balancerName;
    }

    /**
     * For HBASE-71. Try a few different configurations of starting and stopping
     * region servers to see if the assignment or regions is pretty balanced.
     *
     * @throws IOException
     * 		
     * @throws InterruptedException
     * 		
     */
    @Test
    public void testRebalanceOnRegionServerNumberChange() throws IOException, InterruptedException {
        try (Connection connection = ConnectionFactory.createConnection(UTIL.getConfiguration());Admin admin = connection.getAdmin()) {
            admin.createTable(this.desc, Arrays.copyOfRange(HBaseTestingUtility.KEYS, 1, HBaseTestingUtility.KEYS.length));
            this.regionLocator = connection.getRegionLocator(this.desc.getTableName());
            MetaTableAccessor.fullScanMetaAndPrint(admin.getConnection());
            Assert.assertEquals("Test table should have right number of regions", HBaseTestingUtility.KEYS.length, this.regionLocator.getStartKeys().length);
            // verify that the region assignments are balanced to start out
            assertRegionsAreBalanced();
            // add a region server - total of 2
            TestRegionRebalancing.LOG.info(("Started second server=" + (UTIL.getHBaseCluster().startRegionServer().getRegionServer().getServerName())));
            UTIL.getHBaseCluster().getMaster().balance();
            assertRegionsAreBalanced();
            // On a balanced cluster, calling balance() should return true
            assert (UTIL.getHBaseCluster().getMaster().balance()) == true;
            // if we add a server, then the balance() call should return true
            // add a region server - total of 3
            TestRegionRebalancing.LOG.info(("Started third server=" + (UTIL.getHBaseCluster().startRegionServer().getRegionServer().getServerName())));
            waitForAllRegionsAssigned();
            assert (UTIL.getHBaseCluster().getMaster().balance()) == true;
            assertRegionsAreBalanced();
            // kill a region server - total of 2
            TestRegionRebalancing.LOG.info(("Stopped third server=" + (UTIL.getHBaseCluster().stopRegionServer(2, false))));
            UTIL.getHBaseCluster().waitOnRegionServer(2);
            waitOnCrashProcessing();
            UTIL.getHBaseCluster().getMaster().balance();
            assertRegionsAreBalanced();
            // start two more region servers - total of 4
            TestRegionRebalancing.LOG.info(("Readding third server=" + (UTIL.getHBaseCluster().startRegionServer().getRegionServer().getServerName())));
            TestRegionRebalancing.LOG.info(("Added fourth server=" + (UTIL.getHBaseCluster().startRegionServer().getRegionServer().getServerName())));
            waitOnCrashProcessing();
            waitForAllRegionsAssigned();
            assert (UTIL.getHBaseCluster().getMaster().balance()) == true;
            assertRegionsAreBalanced();
            for (int i = 0; i < 6; i++) {
                TestRegionRebalancing.LOG.info((("Adding " + (i + 5)) + "th region server"));
                UTIL.getHBaseCluster().startRegionServer();
            }
            waitForAllRegionsAssigned();
            assert (UTIL.getHBaseCluster().getMaster().balance()) == true;
            assertRegionsAreBalanced();
            regionLocator.close();
        }
    }
}

