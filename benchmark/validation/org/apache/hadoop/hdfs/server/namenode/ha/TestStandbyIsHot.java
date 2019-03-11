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
package org.apache.hadoop.hdfs.server.namenode.ha;


import DFSConfigKeys.DFS_BLOCK_SIZE_KEY;
import DFSConfigKeys.DFS_HA_TAILEDITS_PERIOD_KEY;
import DFSConfigKeys.DFS_NAMENODE_ACCESSTIME_PRECISION_KEY;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hdfs.DFSTestUtil;
import org.apache.hadoop.hdfs.HAUtil;
import org.apache.hadoop.hdfs.MiniDFSCluster;
import org.apache.hadoop.hdfs.MiniDFSNNTopology;
import org.apache.hadoop.hdfs.protocol.LocatedBlocks;
import org.apache.hadoop.hdfs.server.blockmanagement.BlockManagerTestUtil;
import org.apache.hadoop.hdfs.server.datanode.DataNode;
import org.apache.hadoop.hdfs.server.namenode.NameNode;
import org.apache.log4j.Level;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * The hotornot.com of unit tests: makes sure that the standby not only
 * has namespace information, but also has the correct block reports, etc.
 */
public class TestStandbyIsHot {
    protected static final Logger LOG = LoggerFactory.getLogger(TestStandbyIsHot.class);

    private static final String TEST_FILE_DATA = "hello highly available world";

    private static final String TEST_FILE = "/testStandbyIsHot";

    private static final Path TEST_FILE_PATH = new Path(TestStandbyIsHot.TEST_FILE);

    static {
        DFSTestUtil.setNameNodeLogLevel(Level.ALL);
    }

    @Test(timeout = 60000)
    public void testStandbyIsHot() throws Exception {
        Configuration conf = new Configuration();
        // We read from the standby to watch block locations
        HAUtil.setAllowStandbyReads(conf, true);
        conf.setInt(DFS_HA_TAILEDITS_PERIOD_KEY, 1);
        MiniDFSCluster cluster = new MiniDFSCluster.Builder(conf).nnTopology(MiniDFSNNTopology.simpleHATopology()).numDataNodes(3).build();
        try {
            cluster.waitActive();
            cluster.transitionToActive(0);
            NameNode nn1 = cluster.getNameNode(0);
            NameNode nn2 = cluster.getNameNode(1);
            FileSystem fs = HATestUtil.configureFailoverFs(cluster, conf);
            Thread.sleep(1000);
            System.err.println("==================================");
            DFSTestUtil.writeFile(fs, TestStandbyIsHot.TEST_FILE_PATH, TestStandbyIsHot.TEST_FILE_DATA);
            // Have to force an edit log roll so that the standby catches up
            nn1.getRpcServer().rollEditLog();
            System.err.println("==================================");
            // Block locations should show up on standby.
            TestStandbyIsHot.LOG.info("Waiting for block locations to appear on standby node");
            TestStandbyIsHot.waitForBlockLocations(cluster, nn2, TestStandbyIsHot.TEST_FILE, 3);
            // Trigger immediate heartbeats and block reports so
            // that the active "trusts" all of the DNs
            cluster.triggerHeartbeats();
            cluster.triggerBlockReports();
            // Change replication
            TestStandbyIsHot.LOG.info("Changing replication to 1");
            fs.setReplication(TestStandbyIsHot.TEST_FILE_PATH, ((short) (1)));
            BlockManagerTestUtil.computeAllPendingWork(nn1.getNamesystem().getBlockManager());
            TestStandbyIsHot.waitForBlockLocations(cluster, nn1, TestStandbyIsHot.TEST_FILE, 1);
            nn1.getRpcServer().rollEditLog();
            TestStandbyIsHot.LOG.info("Waiting for lowered replication to show up on standby");
            TestStandbyIsHot.waitForBlockLocations(cluster, nn2, TestStandbyIsHot.TEST_FILE, 1);
            // Change back to 3
            TestStandbyIsHot.LOG.info("Changing replication to 3");
            fs.setReplication(TestStandbyIsHot.TEST_FILE_PATH, ((short) (3)));
            BlockManagerTestUtil.computeAllPendingWork(nn1.getNamesystem().getBlockManager());
            nn1.getRpcServer().rollEditLog();
            TestStandbyIsHot.LOG.info("Waiting for higher replication to show up on standby");
            TestStandbyIsHot.waitForBlockLocations(cluster, nn2, TestStandbyIsHot.TEST_FILE, 3);
        } finally {
            cluster.shutdown();
        }
    }

    /**
     * Regression test for HDFS-2795:
     *  - Start an HA cluster with a DN.
     *  - Write several blocks to the FS with replication 1.
     *  - Shutdown the DN
     *  - Wait for the NNs to declare the DN dead. All blocks will be under-replicated.
     *  - Restart the DN.
     * In the bug, the standby node would only very slowly notice the blocks returning
     * to the cluster.
     */
    @Test(timeout = 60000)
    public void testDatanodeRestarts() throws Exception {
        Configuration conf = new Configuration();
        conf.setInt(DFS_BLOCK_SIZE_KEY, 1024);
        // We read from the standby to watch block locations
        HAUtil.setAllowStandbyReads(conf, true);
        conf.setLong(DFS_NAMENODE_ACCESSTIME_PRECISION_KEY, 0);
        conf.setInt(DFS_HA_TAILEDITS_PERIOD_KEY, 1);
        MiniDFSCluster cluster = new MiniDFSCluster.Builder(conf).nnTopology(MiniDFSNNTopology.simpleHATopology()).numDataNodes(1).build();
        try {
            NameNode nn0 = cluster.getNameNode(0);
            NameNode nn1 = cluster.getNameNode(1);
            cluster.transitionToActive(0);
            // Create 5 blocks.
            DFSTestUtil.createFile(cluster.getFileSystem(0), TestStandbyIsHot.TEST_FILE_PATH, (5 * 1024), ((short) (1)), 1L);
            HATestUtil.waitForStandbyToCatchUp(nn0, nn1);
            // Stop the DN.
            DataNode dn = cluster.getDataNodes().get(0);
            String dnName = dn.getDatanodeId().getXferAddr();
            MiniDFSCluster.DataNodeProperties dnProps = cluster.stopDataNode(0);
            // Make sure both NNs register it as dead.
            BlockManagerTestUtil.noticeDeadDatanode(nn0, dnName);
            BlockManagerTestUtil.noticeDeadDatanode(nn1, dnName);
            BlockManagerTestUtil.updateState(nn0.getNamesystem().getBlockManager());
            BlockManagerTestUtil.updateState(nn1.getNamesystem().getBlockManager());
            Assert.assertEquals(5, nn0.getNamesystem().getUnderReplicatedBlocks());
            // The SBN will not have any blocks in its neededReplication queue
            // since the SBN doesn't process replication.
            Assert.assertEquals(0, nn1.getNamesystem().getUnderReplicatedBlocks());
            LocatedBlocks locs = nn1.getRpcServer().getBlockLocations(TestStandbyIsHot.TEST_FILE, 0, 1);
            Assert.assertEquals("Standby should have registered that the block has no replicas", 0, locs.get(0).getLocations().length);
            cluster.restartDataNode(dnProps);
            // Wait for both NNs to re-register the DN.
            cluster.waitActive(0);
            cluster.waitActive(1);
            cluster.waitFirstBRCompleted(0, 10000);
            cluster.waitFirstBRCompleted(1, 10000);
            BlockManagerTestUtil.updateState(nn0.getNamesystem().getBlockManager());
            BlockManagerTestUtil.updateState(nn1.getNamesystem().getBlockManager());
            Assert.assertEquals(0, nn0.getNamesystem().getUnderReplicatedBlocks());
            Assert.assertEquals(0, nn1.getNamesystem().getUnderReplicatedBlocks());
            locs = nn1.getRpcServer().getBlockLocations(TestStandbyIsHot.TEST_FILE, 0, 1);
            Assert.assertEquals("Standby should have registered that the block has replicas again", 1, locs.get(0).getLocations().length);
        } finally {
            cluster.shutdown();
        }
    }
}

