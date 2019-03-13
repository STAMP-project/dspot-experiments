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
package org.apache.hadoop.hdfs.server.namenode;


import DatanodeReportType.LIVE;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.BlockLocation;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.LocatedFileStatus;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.RemoteIterator;
import org.apache.hadoop.hdfs.AdminStatesBaseTest;
import org.apache.hadoop.hdfs.DFSClient;
import org.apache.hadoop.hdfs.DFSTestUtil;
import org.apache.hadoop.hdfs.DistributedFileSystem;
import org.apache.hadoop.hdfs.MiniDFSCluster;
import org.apache.hadoop.hdfs.protocol.DatanodeID;
import org.apache.hadoop.hdfs.protocol.DatanodeInfo;
import org.apache.hadoop.hdfs.server.blockmanagement.BlockManager;
import org.apache.hadoop.hdfs.server.blockmanagement.BlockManagerTestUtil;
import org.apache.hadoop.hdfs.server.blockmanagement.DatanodeAdminManager;
import org.apache.hadoop.hdfs.server.blockmanagement.DatanodeDescriptor;
import org.apache.hadoop.hdfs.server.blockmanagement.DatanodeManager;
import org.apache.hadoop.hdfs.server.datanode.DataNode;
import org.apache.hadoop.hdfs.server.datanode.DataNodeTestUtils;
import org.apache.hadoop.hdfs.tools.DFSAdmin;
import org.apache.hadoop.hdfs.util.HostsFileWriter;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Test;


/**
 * This class tests the decommissioning of nodes.
 */
public class TestDecommissioningStatus {
    private static final long seed = 3735928559L;

    private static final int blockSize = 8192;

    private static final int fileSize = 16384;

    private static final int numDatanodes = 2;

    private static MiniDFSCluster cluster;

    private static FileSystem fileSys;

    private static HostsFileWriter hostsFileWriter;

    private static Configuration conf;

    private Logger LOG;

    final ArrayList<String> decommissionedNodes = new ArrayList<String>(TestDecommissioningStatus.numDatanodes);

    /**
     * Tests Decommissioning Status in DFS.
     */
    @Test
    public void testDecommissionStatus() throws Exception {
        InetSocketAddress addr = new InetSocketAddress("localhost", TestDecommissioningStatus.cluster.getNameNodePort());
        DFSClient client = new DFSClient(addr, TestDecommissioningStatus.conf);
        DatanodeInfo[] info = client.datanodeReport(LIVE);
        Assert.assertEquals("Number of Datanodes ", 2, info.length);
        DistributedFileSystem fileSys = TestDecommissioningStatus.cluster.getFileSystem();
        DFSAdmin admin = new DFSAdmin(TestDecommissioningStatus.cluster.getConfiguration(0));
        short replicas = TestDecommissioningStatus.numDatanodes;
        // 
        // Decommission one node. Verify the decommission status
        // 
        Path file1 = new Path("decommission.dat");
        DFSTestUtil.createFile(fileSys, file1, TestDecommissioningStatus.fileSize, TestDecommissioningStatus.fileSize, TestDecommissioningStatus.blockSize, replicas, TestDecommissioningStatus.seed);
        Path file2 = new Path("decommission1.dat");
        FSDataOutputStream st1 = AdminStatesBaseTest.writeIncompleteFile(fileSys, file2, replicas, ((short) ((TestDecommissioningStatus.fileSize) / (TestDecommissioningStatus.blockSize))));
        for (DataNode d : TestDecommissioningStatus.cluster.getDataNodes()) {
            DataNodeTestUtils.triggerBlockReport(d);
        }
        FSNamesystem fsn = TestDecommissioningStatus.cluster.getNamesystem();
        final DatanodeManager dm = fsn.getBlockManager().getDatanodeManager();
        for (int iteration = 0; iteration < (TestDecommissioningStatus.numDatanodes); iteration++) {
            String downnode = decommissionNode(client, iteration);
            dm.refreshNodes(TestDecommissioningStatus.conf);
            decommissionedNodes.add(downnode);
            BlockManagerTestUtil.recheckDecommissionState(dm);
            final List<DatanodeDescriptor> decommissioningNodes = dm.getDecommissioningNodes();
            if (iteration == 0) {
                Assert.assertEquals(decommissioningNodes.size(), 1);
                DatanodeDescriptor decommNode = decommissioningNodes.get(0);
                checkDecommissionStatus(decommNode, 3, 0, 1);
                checkDFSAdminDecommissionStatus(decommissioningNodes.subList(0, 1), fileSys, admin);
            } else {
                Assert.assertEquals(decommissioningNodes.size(), 2);
                DatanodeDescriptor decommNode1 = decommissioningNodes.get(0);
                DatanodeDescriptor decommNode2 = decommissioningNodes.get(1);
                // This one is still 3,3,1 since it passed over the UC block
                // earlier, before node 2 was decommed
                checkDecommissionStatus(decommNode1, 3, 3, 1);
                // This one is 4,4,2 since it has the full state
                checkDecommissionStatus(decommNode2, 4, 4, 2);
                checkDFSAdminDecommissionStatus(decommissioningNodes.subList(0, 2), fileSys, admin);
            }
        }
        // Call refreshNodes on FSNamesystem with empty exclude file.
        // This will remove the datanodes from decommissioning list and
        // make them available again.
        TestDecommissioningStatus.hostsFileWriter.initExcludeHost("");
        dm.refreshNodes(TestDecommissioningStatus.conf);
        st1.close();
        AdminStatesBaseTest.cleanupFile(fileSys, file1);
        AdminStatesBaseTest.cleanupFile(fileSys, file2);
    }

    /**
     * Verify a DN remains in DECOMMISSION_INPROGRESS state if it is marked
     * as dead before decommission has completed. That will allow DN to resume
     * the replication process after it rejoins the cluster.
     */
    @Test(timeout = 120000)
    public void testDecommissionStatusAfterDNRestart() throws Exception {
        DistributedFileSystem fileSys = ((DistributedFileSystem) (TestDecommissioningStatus.cluster.getFileSystem()));
        // Create a file with one block. That block has one replica.
        Path f = new Path("decommission.dat");
        DFSTestUtil.createFile(fileSys, f, TestDecommissioningStatus.fileSize, TestDecommissioningStatus.fileSize, TestDecommissioningStatus.fileSize, ((short) (1)), TestDecommissioningStatus.seed);
        // Find the DN that owns the only replica.
        RemoteIterator<LocatedFileStatus> fileList = fileSys.listLocatedStatus(f);
        BlockLocation[] blockLocations = fileList.next().getBlockLocations();
        String dnName = blockLocations[0].getNames()[0];
        // Decommission the DN.
        FSNamesystem fsn = TestDecommissioningStatus.cluster.getNamesystem();
        final DatanodeManager dm = fsn.getBlockManager().getDatanodeManager();
        decommissionNode(dnName);
        dm.refreshNodes(TestDecommissioningStatus.conf);
        // Stop the DN when decommission is in progress.
        // Given DFS_DATANODE_BALANCE_BANDWIDTHPERSEC_KEY is to 1 and the size of
        // the block, it will take much longer time that test timeout value for
        // the decommission to complete. So when stopDataNode is called,
        // decommission should be in progress.
        MiniDFSCluster.DataNodeProperties dataNodeProperties = TestDecommissioningStatus.cluster.stopDataNode(dnName);
        final List<DatanodeDescriptor> dead = new ArrayList<DatanodeDescriptor>();
        while (true) {
            dm.fetchDatanodes(null, dead, false);
            if ((dead.size()) == 1) {
                break;
            }
            Thread.sleep(1000);
        } 
        // Force removal of the dead node's blocks.
        BlockManagerTestUtil.checkHeartbeat(fsn.getBlockManager());
        // Force DatanodeManager to check decommission state.
        BlockManagerTestUtil.recheckDecommissionState(dm);
        // Verify that the DN remains in DECOMMISSION_INPROGRESS state.
        Assert.assertTrue("the node should be DECOMMISSION_IN_PROGRESSS", dead.get(0).isDecommissionInProgress());
        // Check DatanodeManager#getDecommissionNodes, make sure it returns
        // the node as decommissioning, even if it's dead
        List<DatanodeDescriptor> decomlist = dm.getDecommissioningNodes();
        Assert.assertTrue("The node should be be decommissioning", ((decomlist.size()) == 1));
        // Delete the under-replicated file, which should let the
        // DECOMMISSION_IN_PROGRESS node become DECOMMISSIONED
        AdminStatesBaseTest.cleanupFile(fileSys, f);
        BlockManagerTestUtil.recheckDecommissionState(dm);
        Assert.assertTrue("the node should be decommissioned", dead.get(0).isDecommissioned());
        // Add the node back
        TestDecommissioningStatus.cluster.restartDataNode(dataNodeProperties, true);
        TestDecommissioningStatus.cluster.waitActive();
        // Call refreshNodes on FSNamesystem with empty exclude file.
        // This will remove the datanodes from decommissioning list and
        // make them available again.
        TestDecommissioningStatus.hostsFileWriter.initExcludeHost("");
        dm.refreshNodes(TestDecommissioningStatus.conf);
    }

    /**
     * Verify the support for decommissioning a datanode that is already dead.
     * Under this scenario the datanode should immediately be marked as
     * DECOMMISSIONED
     */
    @Test(timeout = 120000)
    public void testDecommissionDeadDN() throws Exception {
        Logger log = Logger.getLogger(DatanodeAdminManager.class);
        log.setLevel(Level.DEBUG);
        DatanodeID dnID = TestDecommissioningStatus.cluster.getDataNodes().get(0).getDatanodeId();
        String dnName = dnID.getXferAddr();
        MiniDFSCluster.DataNodeProperties stoppedDN = TestDecommissioningStatus.cluster.stopDataNode(0);
        DFSTestUtil.waitForDatanodeState(TestDecommissioningStatus.cluster, dnID.getDatanodeUuid(), false, 30000);
        FSNamesystem fsn = TestDecommissioningStatus.cluster.getNamesystem();
        final DatanodeManager dm = fsn.getBlockManager().getDatanodeManager();
        DatanodeDescriptor dnDescriptor = dm.getDatanode(dnID);
        decommissionNode(dnName);
        dm.refreshNodes(TestDecommissioningStatus.conf);
        BlockManagerTestUtil.recheckDecommissionState(dm);
        Assert.assertTrue(dnDescriptor.isDecommissioned());
        // Add the node back
        TestDecommissioningStatus.cluster.restartDataNode(stoppedDN, true);
        TestDecommissioningStatus.cluster.waitActive();
        // Call refreshNodes on FSNamesystem with empty exclude file to remove the
        // datanode from decommissioning list and make it available again.
        TestDecommissioningStatus.hostsFileWriter.initExcludeHost("");
        dm.refreshNodes(TestDecommissioningStatus.conf);
    }

    @Test(timeout = 120000)
    public void testDecommissionLosingData() throws Exception {
        ArrayList<String> nodes = new ArrayList<String>(2);
        FSNamesystem fsn = TestDecommissioningStatus.cluster.getNamesystem();
        BlockManager bm = fsn.getBlockManager();
        DatanodeManager dm = bm.getDatanodeManager();
        Path file1 = new Path("decommissionLosingData.dat");
        DFSTestUtil.createFile(TestDecommissioningStatus.fileSys, file1, TestDecommissioningStatus.fileSize, TestDecommissioningStatus.fileSize, TestDecommissioningStatus.blockSize, ((short) (2)), TestDecommissioningStatus.seed);
        Thread.sleep(1000);
        // Shutdown dn1
        LOG.info("Shutdown dn1");
        DatanodeID dnID = TestDecommissioningStatus.cluster.getDataNodes().get(1).getDatanodeId();
        String dnName = dnID.getXferAddr();
        DatanodeDescriptor dnDescriptor1 = dm.getDatanode(dnID);
        nodes.add(dnName);
        MiniDFSCluster.DataNodeProperties stoppedDN1 = TestDecommissioningStatus.cluster.stopDataNode(1);
        DFSTestUtil.waitForDatanodeState(TestDecommissioningStatus.cluster, dnID.getDatanodeUuid(), false, 30000);
        // Shutdown dn0
        LOG.info("Shutdown dn0");
        dnID = TestDecommissioningStatus.cluster.getDataNodes().get(0).getDatanodeId();
        dnName = dnID.getXferAddr();
        DatanodeDescriptor dnDescriptor0 = dm.getDatanode(dnID);
        nodes.add(dnName);
        MiniDFSCluster.DataNodeProperties stoppedDN0 = TestDecommissioningStatus.cluster.stopDataNode(0);
        DFSTestUtil.waitForDatanodeState(TestDecommissioningStatus.cluster, dnID.getDatanodeUuid(), false, 30000);
        // Decommission the nodes.
        LOG.info("Decommissioning nodes");
        TestDecommissioningStatus.hostsFileWriter.initExcludeHosts(nodes);
        dm.refreshNodes(TestDecommissioningStatus.conf);
        BlockManagerTestUtil.recheckDecommissionState(dm);
        Assert.assertTrue(dnDescriptor0.isDecommissioned());
        Assert.assertTrue(dnDescriptor1.isDecommissioned());
        // All nodes are dead and decommed. Blocks should be missing.
        long missingBlocks = bm.getMissingBlocksCount();
        long underreplicated = bm.getLowRedundancyBlocksCount();
        Assert.assertTrue((missingBlocks > 0));
        Assert.assertTrue((underreplicated > 0));
        // Bring back dn0
        LOG.info("Bring back dn0");
        TestDecommissioningStatus.cluster.restartDataNode(stoppedDN0, true);
        do {
            dnID = TestDecommissioningStatus.cluster.getDataNodes().get(0).getDatanodeId();
        } while (dnID == null );
        dnDescriptor0 = dm.getDatanode(dnID);
        // Wait until it sends a block report.
        while ((dnDescriptor0.numBlocks()) == 0) {
            Thread.sleep(100);
        } 
        // Bring back dn1
        LOG.info("Bring back dn1");
        TestDecommissioningStatus.cluster.restartDataNode(stoppedDN1, true);
        do {
            dnID = TestDecommissioningStatus.cluster.getDataNodes().get(1).getDatanodeId();
        } while (dnID == null );
        dnDescriptor1 = dm.getDatanode(dnID);
        // Wait until it sends a block report.
        while ((dnDescriptor1.numBlocks()) == 0) {
            Thread.sleep(100);
        } 
        // Blocks should be still be under-replicated
        Thread.sleep(2000);// Let replication monitor run

        Assert.assertEquals(underreplicated, bm.getLowRedundancyBlocksCount());
        // Start up a node.
        LOG.info("Starting two more nodes");
        TestDecommissioningStatus.cluster.startDataNodes(TestDecommissioningStatus.conf, 2, true, null, null);
        TestDecommissioningStatus.cluster.waitActive();
        // Replication should fix it.
        int count = 0;
        while ((((bm.getLowRedundancyBlocksCount()) > 0) || ((bm.getPendingReconstructionBlocksCount()) > 0)) && ((count++) < 10)) {
            Thread.sleep(1000);
        } 
        Assert.assertEquals(0, bm.getLowRedundancyBlocksCount());
        Assert.assertEquals(0, bm.getPendingReconstructionBlocksCount());
        Assert.assertEquals(0, bm.getMissingBlocksCount());
        // Shutdown the extra nodes.
        dnID = TestDecommissioningStatus.cluster.getDataNodes().get(3).getDatanodeId();
        TestDecommissioningStatus.cluster.stopDataNode(3);
        DFSTestUtil.waitForDatanodeState(TestDecommissioningStatus.cluster, dnID.getDatanodeUuid(), false, 30000);
        dnID = TestDecommissioningStatus.cluster.getDataNodes().get(2).getDatanodeId();
        TestDecommissioningStatus.cluster.stopDataNode(2);
        DFSTestUtil.waitForDatanodeState(TestDecommissioningStatus.cluster, dnID.getDatanodeUuid(), false, 30000);
        // Call refreshNodes on FSNamesystem with empty exclude file to remove the
        // datanode from decommissioning list and make it available again.
        TestDecommissioningStatus.hostsFileWriter.initExcludeHost("");
        dm.refreshNodes(TestDecommissioningStatus.conf);
        TestDecommissioningStatus.fileSys.delete(file1, false);
    }
}

