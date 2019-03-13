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


import HRegionServer.MASTER_HOSTNAME_KEY;
import HRegionServer.RS_HOSTNAME_DISABLE_MASTER_REVERSEDNS_KEY;
import HRegionServer.RS_HOSTNAME_KEY;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;
import java.util.List;
import java.util.Locale;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.HBaseTestingUtility;
import org.apache.hadoop.hbase.StartMiniClusterOption;
import org.apache.hadoop.hbase.master.LoadBalancer;
import org.apache.hadoop.hbase.testclassification.MediumTests;
import org.apache.hadoop.hbase.testclassification.RegionServerTests;
import org.apache.hadoop.hbase.zookeeper.ZKUtil;
import org.apache.hadoop.hbase.zookeeper.ZKWatcher;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static HRegionServer.RS_HOSTNAME_DISABLE_MASTER_REVERSEDNS_KEY;
import static HRegionServer.RS_HOSTNAME_KEY;


/**
 * Tests for the hostname specification by region server
 */
@Category({ RegionServerTests.class, MediumTests.class })
public class TestRegionServerHostname {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestRegionServerHostname.class);

    private static final Logger LOG = LoggerFactory.getLogger(TestRegionServerHostname.class);

    private HBaseTestingUtility TEST_UTIL;

    private static final int NUM_MASTERS = 1;

    private static final int NUM_RS = 1;

    @Test
    public void testInvalidRegionServerHostnameAbortsServer() throws Exception {
        String invalidHostname = "hostAddr.invalid";
        TEST_UTIL.getConfiguration().set(RS_HOSTNAME_KEY, invalidHostname);
        HRegionServer hrs = null;
        try {
            hrs = new HRegionServer(TEST_UTIL.getConfiguration());
        } catch (IllegalArgumentException iae) {
            Assert.assertTrue(iae.getMessage(), ((iae.getMessage().contains(("Failed resolve of " + invalidHostname))) || (iae.getMessage().contains(("Problem binding to " + invalidHostname)))));
        }
        Assert.assertNull("Failed to validate against invalid hostname", hrs);
    }

    @Test
    public void testRegionServerHostname() throws Exception {
        Enumeration<NetworkInterface> netInterfaceList = NetworkInterface.getNetworkInterfaces();
        while (netInterfaceList.hasMoreElements()) {
            NetworkInterface ni = netInterfaceList.nextElement();
            Enumeration<InetAddress> addrList = ni.getInetAddresses();
            // iterate through host addresses and use each as hostname
            while (addrList.hasMoreElements()) {
                InetAddress addr = addrList.nextElement();
                if (((addr.isLoopbackAddress()) || (addr.isLinkLocalAddress())) || (addr.isMulticastAddress())) {
                    continue;
                }
                String hostName = addr.getHostName();
                TestRegionServerHostname.LOG.info(((("Found " + hostName) + " on ") + ni));
                TEST_UTIL.getConfiguration().set(MASTER_HOSTNAME_KEY, hostName);
                TEST_UTIL.getConfiguration().set(RS_HOSTNAME_KEY, hostName);
                StartMiniClusterOption option = StartMiniClusterOption.builder().numMasters(TestRegionServerHostname.NUM_MASTERS).numRegionServers(TestRegionServerHostname.NUM_RS).numDataNodes(TestRegionServerHostname.NUM_RS).build();
                TEST_UTIL.startMiniCluster(option);
                try {
                    ZKWatcher zkw = getZooKeeperWatcher();
                    List<String> servers = ZKUtil.listChildrenNoWatch(zkw, zkw.getZNodePaths().rsZNode);
                    // there would be NUM_RS+1 children - one for the master
                    Assert.assertTrue(((servers.size()) == ((TestRegionServerHostname.NUM_RS) + (LoadBalancer.isTablesOnMaster(TEST_UTIL.getConfiguration()) ? 1 : 0))));
                    for (String server : servers) {
                        Assert.assertTrue(((("From zookeeper: " + server) + " hostname: ") + hostName), server.startsWith(((hostName.toLowerCase(Locale.ROOT)) + ",")));
                    }
                    zkw.close();
                } finally {
                    TEST_UTIL.shutdownMiniCluster();
                }
            } 
        } 
    }

    @Test
    public void testConflictRegionServerHostnameConfigurationsAbortServer() throws Exception {
        Enumeration<NetworkInterface> netInterfaceList = NetworkInterface.getNetworkInterfaces();
        while (netInterfaceList.hasMoreElements()) {
            NetworkInterface ni = netInterfaceList.nextElement();
            Enumeration<InetAddress> addrList = ni.getInetAddresses();
            // iterate through host addresses and use each as hostname
            while (addrList.hasMoreElements()) {
                InetAddress addr = addrList.nextElement();
                if (((addr.isLoopbackAddress()) || (addr.isLinkLocalAddress())) || (addr.isMulticastAddress())) {
                    continue;
                }
                String hostName = addr.getHostName();
                TestRegionServerHostname.LOG.info(((("Found " + hostName) + " on ") + ni));
                TEST_UTIL.getConfiguration().set(MASTER_HOSTNAME_KEY, hostName);
                // "hbase.regionserver.hostname" and "hbase.regionserver.hostname.disable.master.reversedns"
                // are mutually exclusive. Exception should be thrown if both are used.
                TEST_UTIL.getConfiguration().set(RS_HOSTNAME_KEY, hostName);
                TEST_UTIL.getConfiguration().setBoolean(RS_HOSTNAME_DISABLE_MASTER_REVERSEDNS_KEY, true);
                try {
                    StartMiniClusterOption option = StartMiniClusterOption.builder().numMasters(TestRegionServerHostname.NUM_MASTERS).numRegionServers(TestRegionServerHostname.NUM_RS).numDataNodes(TestRegionServerHostname.NUM_RS).build();
                    TEST_UTIL.startMiniCluster(option);
                } catch (Exception e) {
                    Throwable t1 = e.getCause();
                    Throwable t2 = t1.getCause();
                    Assert.assertTrue((((t1.getMessage()) + " - ") + (t2.getMessage())), t2.getMessage().contains(((((RS_HOSTNAME_DISABLE_MASTER_REVERSEDNS_KEY) + " and ") + (RS_HOSTNAME_KEY)) + " are mutually exclusive")));
                    return;
                } finally {
                    TEST_UTIL.shutdownMiniCluster();
                }
                Assert.assertTrue("Failed to validate against conflict hostname configurations", false);
            } 
        } 
    }

    @Test
    public void testRegionServerHostnameReportedToMaster() throws Exception {
        TEST_UTIL.getConfiguration().setBoolean(RS_HOSTNAME_DISABLE_MASTER_REVERSEDNS_KEY, true);
        StartMiniClusterOption option = StartMiniClusterOption.builder().numMasters(TestRegionServerHostname.NUM_MASTERS).numRegionServers(TestRegionServerHostname.NUM_RS).numDataNodes(TestRegionServerHostname.NUM_RS).build();
        TEST_UTIL.startMiniCluster(option);
        boolean tablesOnMaster = LoadBalancer.isTablesOnMaster(TEST_UTIL.getConfiguration());
        int expectedRS = (TestRegionServerHostname.NUM_RS) + (tablesOnMaster ? 1 : 0);
        try (ZKWatcher zkw = getZooKeeperWatcher()) {
            List<String> servers = ZKUtil.listChildrenNoWatch(zkw, zkw.getZNodePaths().rsZNode);
            Assert.assertEquals(expectedRS, servers.size());
        }
    }
}

