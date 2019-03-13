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
package org.apache.hadoop.hdfs;


import AdminStates.DECOMMISSIONED;
import DatanodeReportType.LIVE;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileChecksum;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hdfs.protocol.DatanodeInfo;
import org.apache.hadoop.hdfs.protocol.ErasureCodingPolicy;
import org.apache.hadoop.hdfs.protocol.LocatedBlock;
import org.apache.hadoop.hdfs.server.blockmanagement.BlockManager;
import org.apache.hadoop.hdfs.server.datanode.DataNode;
import org.apache.hadoop.hdfs.server.namenode.FSNamesystem;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * This class tests the decommissioning of datanode with striped blocks.
 */
public class TestDecommissionWithStriped {
    private static final Logger LOG = LoggerFactory.getLogger(TestDecommissionWithStriped.class);

    // heartbeat interval in seconds
    private static final int HEARTBEAT_INTERVAL = 1;

    // block report in msec
    private static final int BLOCKREPORT_INTERVAL_MSEC = 1000;

    // replication interval
    private static final int NAMENODE_REPLICATION_INTERVAL = 1;

    private Path decommissionDir;

    private Path hostsFile;

    private Path excludeFile;

    private FileSystem localFileSys;

    private Configuration conf;

    private MiniDFSCluster cluster;

    private DistributedFileSystem dfs;

    private final ErasureCodingPolicy ecPolicy = StripedFileTestUtil.getDefaultECPolicy();

    private int numDNs;

    private final int cellSize = ecPolicy.getCellSize();

    private final int dataBlocks = ecPolicy.getNumDataUnits();

    private final int parityBlocks = ecPolicy.getNumParityUnits();

    private final int blockSize = (cellSize) * 4;

    private final int blockGroupSize = (blockSize) * (dataBlocks);

    private final Path ecDir = new Path(("/" + (this.getClass().getSimpleName())));

    private FSNamesystem fsn;

    private BlockManager bm;

    private DFSClient client;

    @Test(timeout = 120000)
    public void testFileFullBlockGroup() throws Exception {
        TestDecommissionWithStriped.LOG.info("Starting test testFileFullBlockGroup");
        testDecommission(((blockSize) * (dataBlocks)), 9, 1, "testFileFullBlockGroup");
    }

    @Test(timeout = 120000)
    public void testFileMultipleBlockGroups() throws Exception {
        TestDecommissionWithStriped.LOG.info("Starting test testFileMultipleBlockGroups");
        int writeBytes = (2 * (blockSize)) * (dataBlocks);
        testDecommission(writeBytes, 9, 1, "testFileMultipleBlockGroups");
    }

    @Test(timeout = 120000)
    public void testFileSmallerThanOneCell() throws Exception {
        TestDecommissionWithStriped.LOG.info("Starting test testFileSmallerThanOneCell");
        testDecommission(((cellSize) - 1), 4, 1, "testFileSmallerThanOneCell");
    }

    @Test(timeout = 120000)
    public void testFileSmallerThanOneStripe() throws Exception {
        TestDecommissionWithStriped.LOG.info("Starting test testFileSmallerThanOneStripe");
        testDecommission(((cellSize) * 2), 5, 1, "testFileSmallerThanOneStripe");
    }

    @Test(timeout = 120000)
    public void testDecommissionTwoNodes() throws Exception {
        TestDecommissionWithStriped.LOG.info("Starting test testDecommissionTwoNodes");
        testDecommission(((blockSize) * (dataBlocks)), 9, 2, "testDecommissionTwoNodes");
    }

    @Test(timeout = 120000)
    public void testDecommissionWithURBlockForSameBlockGroup() throws Exception {
        TestDecommissionWithStriped.LOG.info("Starting test testDecommissionWithURBlocksForSameBlockGroup");
        final Path ecFile = new Path(ecDir, "testDecommissionWithCorruptBlocks");
        int writeBytes = ((cellSize) * (dataBlocks)) * 2;
        writeStripedFile(dfs, ecFile, writeBytes);
        Assert.assertEquals(0, bm.numOfUnderReplicatedBlocks());
        final List<DatanodeInfo> decommisionNodes = new ArrayList<DatanodeInfo>();
        LocatedBlock lb = dfs.getClient().getLocatedBlocks(ecFile.toString(), 0).get(0);
        DatanodeInfo[] dnLocs = lb.getLocations();
        Assert.assertEquals(((dataBlocks) + (parityBlocks)), dnLocs.length);
        int decommNodeIndex = (dataBlocks) - 1;
        int stopNodeIndex = 1;
        // add the nodes which will be decommissioning
        decommisionNodes.add(dnLocs[decommNodeIndex]);
        // stop excess dns to avoid immediate reconstruction.
        DatanodeInfo[] info = client.datanodeReport(LIVE);
        List<MiniDFSCluster.DataNodeProperties> stoppedDns = new ArrayList<>();
        for (DatanodeInfo liveDn : info) {
            boolean usedNode = false;
            for (DatanodeInfo datanodeInfo : dnLocs) {
                if (liveDn.getXferAddr().equals(datanodeInfo.getXferAddr())) {
                    usedNode = true;
                    break;
                }
            }
            if (!usedNode) {
                DataNode dn = cluster.getDataNode(liveDn.getIpcPort());
                stoppedDns.add(cluster.stopDataNode(liveDn.getXferAddr()));
                cluster.setDataNodeDead(dn.getDatanodeId());
                TestDecommissionWithStriped.LOG.info(("stop datanode " + (dn.getDatanodeId().getHostName())));
            }
        }
        DataNode dn = cluster.getDataNode(dnLocs[stopNodeIndex].getIpcPort());
        cluster.stopDataNode(dnLocs[stopNodeIndex].getXferAddr());
        cluster.setDataNodeDead(dn.getDatanodeId());
        numDNs = (numDNs) - 1;
        // Decommission node in a new thread. Verify that node is decommissioned.
        final CountDownLatch decomStarted = new CountDownLatch(0);
        Thread decomTh = new Thread() {
            public void run() {
                try {
                    decomStarted.countDown();
                    decommissionNode(0, decommisionNodes, DECOMMISSIONED);
                } catch (Exception e) {
                    TestDecommissionWithStriped.LOG.error("Exception while decommissioning", e);
                    Assert.fail("Shouldn't throw exception!");
                }
            }
        };
        int deadDecommissioned = fsn.getNumDecomDeadDataNodes();
        int liveDecommissioned = fsn.getNumDecomLiveDataNodes();
        decomTh.start();
        decomStarted.await(5, TimeUnit.SECONDS);
        Thread.sleep(3000);// grace period to trigger decommissioning call

        // start datanode so that decommissioning live node will be finished
        for (MiniDFSCluster.DataNodeProperties dnp : stoppedDns) {
            cluster.restartDataNode(dnp);
            TestDecommissionWithStriped.LOG.info("Restarts stopped datanode:{} to trigger block reconstruction", dnp.datanode);
        }
        cluster.waitActive();
        TestDecommissionWithStriped.LOG.info("Waiting to finish decommissioning node:{}", decommisionNodes);
        decomTh.join(20000);// waiting 20secs to finish decommission

        TestDecommissionWithStriped.LOG.info("Finished decommissioning node:{}", decommisionNodes);
        Assert.assertEquals(deadDecommissioned, fsn.getNumDecomDeadDataNodes());
        Assert.assertEquals((liveDecommissioned + (decommisionNodes.size())), fsn.getNumDecomLiveDataNodes());
        // Ensure decommissioned datanode is not automatically shutdown
        DFSClient client = TestDecommissionWithStriped.getDfsClient(cluster.getNameNode(0), conf);
        Assert.assertEquals("All datanodes must be alive", numDNs, client.datanodeReport(LIVE).length);
        Assert.assertNull(TestDecommissionWithStriped.checkFile(dfs, ecFile, 9, decommisionNodes, numDNs));
        StripedFileTestUtil.checkData(dfs, ecFile, writeBytes, decommisionNodes, null, blockGroupSize);
        cleanupFile(dfs, ecFile);
    }

    /**
     * Tests to verify that the file checksum should be able to compute after the
     * decommission operation.
     *
     * Below is the block indices list after the decommission. ' represents
     * decommissioned node index.
     *
     * 0, 2, 3, 4, 5, 6, 7, 8, 1, 1'
     *
     * Here, this list contains duplicated blocks and does not maintaining any
     * order.
     */
    @Test(timeout = 120000)
    public void testFileChecksumAfterDecommission() throws Exception {
        TestDecommissionWithStriped.LOG.info("Starting test testFileChecksumAfterDecommission");
        final Path ecFile = new Path(ecDir, "testFileChecksumAfterDecommission");
        int writeBytes = (cellSize) * (dataBlocks);
        writeStripedFile(dfs, ecFile, writeBytes);
        Assert.assertEquals(0, bm.numOfUnderReplicatedBlocks());
        FileChecksum fileChecksum1 = dfs.getFileChecksum(ecFile, writeBytes);
        final List<DatanodeInfo> decommisionNodes = new ArrayList<DatanodeInfo>();
        LocatedBlock lb = dfs.getClient().getLocatedBlocks(ecFile.toString(), 0).get(0);
        DatanodeInfo[] dnLocs = lb.getLocations();
        Assert.assertEquals(((dataBlocks) + (parityBlocks)), dnLocs.length);
        int decommNodeIndex = 1;
        // add the node which will be decommissioning
        decommisionNodes.add(dnLocs[decommNodeIndex]);
        decommissionNode(0, decommisionNodes, DECOMMISSIONED);
        Assert.assertEquals(decommisionNodes.size(), fsn.getNumDecomLiveDataNodes());
        Assert.assertNull(TestDecommissionWithStriped.checkFile(dfs, ecFile, 9, decommisionNodes, numDNs));
        StripedFileTestUtil.checkData(dfs, ecFile, writeBytes, decommisionNodes, null, blockGroupSize);
        // verify checksum
        FileChecksum fileChecksum2 = dfs.getFileChecksum(ecFile, writeBytes);
        TestDecommissionWithStriped.LOG.info(("fileChecksum1:" + fileChecksum1));
        TestDecommissionWithStriped.LOG.info(("fileChecksum2:" + fileChecksum2));
        Assert.assertTrue("Checksum mismatches!", fileChecksum1.equals(fileChecksum2));
    }
}

