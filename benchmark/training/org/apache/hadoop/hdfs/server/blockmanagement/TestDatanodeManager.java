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
package org.apache.hadoop.hdfs.server.blockmanagement;


import CommonConfigurationKeysPublic.NET_TOPOLOGY_IMPL_KEY;
import CommonConfigurationKeysPublic.NET_TOPOLOGY_NODE_SWITCH_MAPPING_IMPL_KEY;
import DFSConfigKeys.DFS_HEARTBEAT_INTERVAL_KEY;
import DFSConfigKeys.DFS_NAMENODE_HEARTBEAT_RECHECK_INTERVAL_KEY;
import DFSConfigKeys.DFS_NET_TOPOLOGY_IMPL_KEY;
import DFSConfigKeys.DFS_REJECT_UNRESOLVED_DN_TOPOLOGY_MAPPING_KEY;
import DFSConfigKeys.DFS_USE_DFS_NETWORK_TOPOLOGY_KEY;
import HdfsConstants.DatanodeReportType.ALL;
import HdfsServerConstants.NodeType;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hdfs.HdfsConfiguration;
import org.apache.hadoop.hdfs.net.DFSNetworkTopology;
import org.apache.hadoop.hdfs.protocol.DatanodeID;
import org.apache.hadoop.hdfs.security.token.block.ExportedBlockKeys;
import org.apache.hadoop.hdfs.server.namenode.FSNamesystem;
import org.apache.hadoop.hdfs.server.protocol.DatanodeRegistration;
import org.apache.hadoop.net.DNSToSwitchMapping;
import org.apache.hadoop.net.NetworkTopology;
import org.apache.hadoop.test.Whitebox;
import org.apache.hadoop.util.Shell;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class TestDatanodeManager {
    public static final Logger LOG = LoggerFactory.getLogger(TestDatanodeManager.class);

    // The number of times the registration / removal of nodes should happen
    final int NUM_ITERATIONS = 500;

    /**
     * This test checks that if a node is re-registered with a new software
     * version after the heartbeat expiry interval but before the HeartbeatManager
     * has a chance to detect this and remove it, the node's version will still
     * be correctly decremented.
     */
    @Test
    public void testNumVersionsCorrectAfterReregister() throws IOException, InterruptedException {
        // Create the DatanodeManager which will be tested
        FSNamesystem fsn = Mockito.mock(FSNamesystem.class);
        Mockito.when(fsn.hasWriteLock()).thenReturn(true);
        Configuration conf = new Configuration();
        conf.setLong(DFS_HEARTBEAT_INTERVAL_KEY, 0);
        conf.setLong(DFS_NAMENODE_HEARTBEAT_RECHECK_INTERVAL_KEY, 10);
        DatanodeManager dm = TestDatanodeManager.mockDatanodeManager(fsn, conf);
        String storageID = "someStorageID1";
        String ip = "someIP" + storageID;
        // Register then reregister the same node but with a different version
        for (int i = 0; i <= 1; i++) {
            dm.registerDatanode(new DatanodeRegistration(new DatanodeID(ip, "", storageID, 9000, 0, 0, 0), null, null, ("version" + i)));
            if (i == 0) {
                Thread.sleep(25);
            }
        }
        // Verify DatanodeManager has the correct count
        Map<String, Integer> mapToCheck = dm.getDatanodesSoftwareVersions();
        Assert.assertNull("should be no more version0 nodes", mapToCheck.get("version0"));
        Assert.assertEquals("should be one version1 node", mapToCheck.get("version1").intValue(), 1);
    }

    /**
     * This test sends a random sequence of node registrations and node removals
     * to the DatanodeManager (of nodes with different IDs and versions), and
     * checks that the DatanodeManager keeps a correct count of different software
     * versions at all times.
     */
    @Test
    public void testNumVersionsReportedCorrect() throws IOException {
        // Create the DatanodeManager which will be tested
        FSNamesystem fsn = Mockito.mock(FSNamesystem.class);
        Mockito.when(fsn.hasWriteLock()).thenReturn(true);
        DatanodeManager dm = TestDatanodeManager.mockDatanodeManager(fsn, new Configuration());
        // Seed the RNG with a known value so test failures are easier to reproduce
        Random rng = new Random();
        int seed = rng.nextInt();
        rng = new Random(seed);
        TestDatanodeManager.LOG.info((("Using seed " + seed) + " for testing"));
        // A map of the Storage IDs to the DN registration it was registered with
        HashMap<String, DatanodeRegistration> sIdToDnReg = new HashMap<String, DatanodeRegistration>();
        for (int i = 0; i < (NUM_ITERATIONS); ++i) {
            // If true, remove a node for every 3rd time (if there's one)
            if (((rng.nextBoolean()) && ((i % 3) == 0)) && ((sIdToDnReg.size()) != 0)) {
                // Pick a random node.
                int randomIndex = (rng.nextInt()) % (sIdToDnReg.size());
                // Iterate to that random position
                Iterator<Map.Entry<String, DatanodeRegistration>> it = sIdToDnReg.entrySet().iterator();
                for (int j = 0; j < (randomIndex - 1); ++j) {
                    it.next();
                }
                DatanodeRegistration toRemove = it.next().getValue();
                TestDatanodeManager.LOG.info(((((("Removing node " + (toRemove.getDatanodeUuid())) + " ip ") + (toRemove.getXferAddr())) + " version : ") + (toRemove.getSoftwareVersion())));
                // Remove that random node
                dm.removeDatanode(toRemove);
                it.remove();
            } else // Otherwise register a node. This node may be a new / an old one
            {
                // Pick a random storageID to register.
                String storageID = "someStorageID" + (rng.nextInt(5000));
                DatanodeRegistration dr = Mockito.mock(DatanodeRegistration.class);
                Mockito.when(dr.getDatanodeUuid()).thenReturn(storageID);
                // If this storageID had already been registered before
                if (sIdToDnReg.containsKey(storageID)) {
                    dr = sIdToDnReg.get(storageID);
                    // Half of the times, change the IP address
                    if (rng.nextBoolean()) {
                        dr.setIpAddr(((dr.getIpAddr()) + "newIP"));
                    }
                } else {
                    // This storageID has never been registered
                    // Ensure IP address is unique to storageID
                    String ip = "someIP" + storageID;
                    Mockito.when(dr.getIpAddr()).thenReturn(ip);
                    Mockito.when(dr.getXferAddr()).thenReturn((ip + ":9000"));
                    Mockito.when(dr.getXferPort()).thenReturn(9000);
                }
                // Pick a random version to register with
                Mockito.when(dr.getSoftwareVersion()).thenReturn(("version" + (rng.nextInt(5))));
                TestDatanodeManager.LOG.info(((((("Registering node storageID: " + (dr.getDatanodeUuid())) + ", version: ") + (dr.getSoftwareVersion())) + ", IP address: ") + (dr.getXferAddr())));
                // Register this random node
                dm.registerDatanode(dr);
                sIdToDnReg.put(storageID, dr);
            }
            // Verify DatanodeManager still has the right count
            Map<String, Integer> mapToCheck = dm.getDatanodesSoftwareVersions();
            // Remove counts from versions and make sure that after removing all nodes
            // mapToCheck is empty
            for (Map.Entry<String, DatanodeRegistration> it : sIdToDnReg.entrySet()) {
                String ver = it.getValue().getSoftwareVersion();
                if (!(mapToCheck.containsKey(ver))) {
                    throw new AssertionError((("The correct number of datanodes of a " + "version was not found on iteration ") + i));
                }
                mapToCheck.put(ver, ((mapToCheck.get(ver)) - 1));
                if ((mapToCheck.get(ver)) == 0) {
                    mapToCheck.remove(ver);
                }
            }
            for (Map.Entry<String, Integer> entry : mapToCheck.entrySet()) {
                TestDatanodeManager.LOG.info(((("Still in map: " + (entry.getKey())) + " has ") + (entry.getValue())));
            }
            Assert.assertEquals((("The map of version counts returned by DatanodeManager was" + " not what it was expected to be on iteration ") + i), 0, mapToCheck.size());
        }
    }

    @Test(timeout = 100000)
    public void testRejectUnresolvedDatanodes() throws IOException {
        // Create the DatanodeManager which will be tested
        FSNamesystem fsn = Mockito.mock(FSNamesystem.class);
        Mockito.when(fsn.hasWriteLock()).thenReturn(true);
        Configuration conf = new Configuration();
        // Set configuration property for rejecting unresolved topology mapping
        conf.setBoolean(DFS_REJECT_UNRESOLVED_DN_TOPOLOGY_MAPPING_KEY, true);
        // set TestDatanodeManager.MyResolver to be used for topology resolving
        conf.setClass(NET_TOPOLOGY_NODE_SWITCH_MAPPING_IMPL_KEY, TestDatanodeManager.MyResolver.class, DNSToSwitchMapping.class);
        // create DatanodeManager
        DatanodeManager dm = TestDatanodeManager.mockDatanodeManager(fsn, conf);
        // storageID to register.
        String storageID = "someStorageID-123";
        DatanodeRegistration dr = Mockito.mock(DatanodeRegistration.class);
        Mockito.when(dr.getDatanodeUuid()).thenReturn(storageID);
        try {
            // Register this node
            dm.registerDatanode(dr);
            Assert.fail("Expected an UnresolvedTopologyException");
        } catch (UnresolvedTopologyException ute) {
            TestDatanodeManager.LOG.info(("Expected - topology is not resolved and " + "registration is rejected."));
        } catch (Exception e) {
            Assert.fail("Expected an UnresolvedTopologyException");
        }
    }

    /**
     * MyResolver class provides resolve method which always returns null
     * in order to simulate unresolved topology mapping.
     */
    public static class MyResolver implements DNSToSwitchMapping {
        @Override
        public List<String> resolve(List<String> names) {
            return null;
        }

        @Override
        public void reloadCachedMappings() {
        }

        @Override
        public void reloadCachedMappings(List<String> names) {
        }
    }

    /**
     * This test creates a LocatedBlock with 5 locations, sorts the locations
     * based on the network topology, and ensures the locations are still aligned
     * with the storage ids and storage types.
     */
    @Test
    public void testSortLocatedBlocks() throws IOException, URISyntaxException {
        HelperFunction(null, 0);
    }

    /**
     * Execute a functional topology script and make sure that helper
     * function works correctly
     *
     * @throws IOException
     * 		
     * @throws URISyntaxException
     * 		
     */
    @Test
    public void testgoodScript() throws IOException, URISyntaxException {
        HelperFunction(("/" + (Shell.appendScriptExtension("topology-script"))), 0);
    }

    /**
     * Run a broken script and verify that helper function is able to
     * ignore the broken script and work correctly
     *
     * @throws IOException
     * 		
     * @throws URISyntaxException
     * 		
     */
    @Test
    public void testBadScript() throws IOException, URISyntaxException {
        HelperFunction(("/" + (Shell.appendScriptExtension("topology-broken-script"))), 0);
    }

    /**
     * Test with different sorting functions but include datanodes.
     * with provided storage
     *
     * @throws IOException
     * 		
     * @throws URISyntaxException
     * 		
     */
    @Test
    public void testWithProvidedTypes() throws IOException, URISyntaxException {
        HelperFunction(null, 1);
        HelperFunction(null, 3);
        HelperFunction(("/" + (Shell.appendScriptExtension("topology-script"))), 1);
        HelperFunction(("/" + (Shell.appendScriptExtension("topology-script"))), 2);
    }

    /**
     * Test whether removing a host from the includes list without adding it to
     * the excludes list will exclude it from data node reports.
     */
    @Test
    public void testRemoveIncludedNode() throws IOException {
        FSNamesystem fsn = Mockito.mock(FSNamesystem.class);
        // Set the write lock so that the DatanodeManager can start
        Mockito.when(fsn.hasWriteLock()).thenReturn(true);
        DatanodeManager dm = TestDatanodeManager.mockDatanodeManager(fsn, new Configuration());
        HostFileManager hm = new HostFileManager();
        HostSet noNodes = new HostSet();
        HostSet oneNode = new HostSet();
        HostSet twoNodes = new HostSet();
        DatanodeRegistration dr1 = new DatanodeRegistration(new DatanodeID("127.0.0.1", "127.0.0.1", "someStorageID-123", 12345, 12345, 12345, 12345), new org.apache.hadoop.hdfs.server.common.StorageInfo(NodeType.DATA_NODE), new ExportedBlockKeys(), "test");
        DatanodeRegistration dr2 = new DatanodeRegistration(new DatanodeID("127.0.0.1", "127.0.0.1", "someStorageID-234", 23456, 23456, 23456, 23456), new org.apache.hadoop.hdfs.server.common.StorageInfo(NodeType.DATA_NODE), new ExportedBlockKeys(), "test");
        twoNodes.add(TestDatanodeManager.entry("127.0.0.1:12345"));
        twoNodes.add(TestDatanodeManager.entry("127.0.0.1:23456"));
        oneNode.add(TestDatanodeManager.entry("127.0.0.1:23456"));
        hm.refresh(twoNodes, noNodes);
        Whitebox.setInternalState(dm, "hostConfigManager", hm);
        // Register two data nodes to simulate them coming up.
        // We need to add two nodes, because if we have only one node, removing it
        // will cause the includes list to be empty, which means all hosts will be
        // allowed.
        dm.registerDatanode(dr1);
        dm.registerDatanode(dr2);
        // Make sure that both nodes are reported
        List<DatanodeDescriptor> both = dm.getDatanodeListForReport(ALL);
        // Sort the list so that we know which one is which
        Collections.sort(both);
        Assert.assertEquals("Incorrect number of hosts reported", 2, both.size());
        Assert.assertEquals("Unexpected host or host in unexpected position", "127.0.0.1:12345", both.get(0).getInfoAddr());
        Assert.assertEquals("Unexpected host or host in unexpected position", "127.0.0.1:23456", both.get(1).getInfoAddr());
        // Remove one node from includes, but do not add it to excludes.
        hm.refresh(oneNode, noNodes);
        // Make sure that only one node is still reported
        List<DatanodeDescriptor> onlyOne = dm.getDatanodeListForReport(ALL);
        Assert.assertEquals("Incorrect number of hosts reported", 1, onlyOne.size());
        Assert.assertEquals("Unexpected host reported", "127.0.0.1:23456", onlyOne.get(0).getInfoAddr());
        // Remove all nodes from includes
        hm.refresh(noNodes, noNodes);
        // Check that both nodes are reported again
        List<DatanodeDescriptor> bothAgain = dm.getDatanodeListForReport(ALL);
        // Sort the list so that we know which one is which
        Collections.sort(bothAgain);
        Assert.assertEquals("Incorrect number of hosts reported", 2, bothAgain.size());
        Assert.assertEquals("Unexpected host or host in unexpected position", "127.0.0.1:12345", bothAgain.get(0).getInfoAddr());
        Assert.assertEquals("Unexpected host or host in unexpected position", "127.0.0.1:23456", bothAgain.get(1).getInfoAddr());
    }

    @Test
    public void testPendingRecoveryTasks() throws IOException {
        // Tasks are slitted according to the ratio between queue lengths.
        verifyPendingRecoveryTasks(20, 20, 20, 10, 10);
        verifyPendingRecoveryTasks(40, 10, 20, 16, 4);
        // Approximately load tasks if the ratio between queue length is large.
        verifyPendingRecoveryTasks(400, 1, 20, 20, 1);
    }

    @Test
    public void testNetworkTopologyInstantiation() throws Exception {
        // case 1, dfs.use.dfs.network.topology=true, use the default
        // DFSNetworkTopology impl.
        Configuration conf1 = new HdfsConfiguration();
        FSNamesystem fsn = Mockito.mock(FSNamesystem.class);
        DatanodeManager dm1 = TestDatanodeManager.mockDatanodeManager(fsn, conf1);
        Assert.assertEquals(DFSNetworkTopology.class, dm1.getNetworkTopology().getClass());
        // case 2, dfs.use.dfs.network.topology=false, use the default
        // NetworkTopology impl.
        Configuration conf2 = new HdfsConfiguration();
        conf2.setBoolean(DFS_USE_DFS_NETWORK_TOPOLOGY_KEY, false);
        DatanodeManager dm2 = TestDatanodeManager.mockDatanodeManager(fsn, conf2);
        Assert.assertEquals(NetworkTopology.class, dm2.getNetworkTopology().getClass());
        // case 3, dfs.use.dfs.network.topology=false, and specify the
        // net.topology.impl property.
        Configuration conf3 = new HdfsConfiguration();
        conf3.setClass(NET_TOPOLOGY_IMPL_KEY, TestDatanodeManager.MockDfsNetworkTopology.class, NetworkTopology.class);
        conf3.setBoolean(DFS_USE_DFS_NETWORK_TOPOLOGY_KEY, false);
        DatanodeManager dm3 = TestDatanodeManager.mockDatanodeManager(fsn, conf3);
        Assert.assertEquals(TestDatanodeManager.MockDfsNetworkTopology.class, dm3.getNetworkTopology().getClass());
        // case 4, dfs.use.dfs.network.topology=true, and specify the
        // dfs.net.topology.impl property.
        Configuration conf4 = new HdfsConfiguration();
        conf4.setClass(DFS_NET_TOPOLOGY_IMPL_KEY, TestDatanodeManager.MockDfsNetworkTopology.class, NetworkTopology.class);
        conf4.setBoolean(DFS_USE_DFS_NETWORK_TOPOLOGY_KEY, true);
        DatanodeManager dm4 = TestDatanodeManager.mockDatanodeManager(fsn, conf4);
        Assert.assertEquals(TestDatanodeManager.MockDfsNetworkTopology.class, dm4.getNetworkTopology().getClass());
    }

    /**
     * A NetworkTopology implementation for test.
     */
    public static class MockDfsNetworkTopology extends DFSNetworkTopology {
        public MockDfsNetworkTopology() {
            super();
        }
    }
}

