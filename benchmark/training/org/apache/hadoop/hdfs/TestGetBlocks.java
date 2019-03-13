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


import CommonConfigurationKeys.IO_FILE_BUFFER_SIZE_KEY;
import DFSConfigKeys.DFS_BALANCER_GETBLOCKS_MIN_BLOCK_SIZE_KEY;
import DFSConfigKeys.DFS_BLOCK_SIZE_KEY;
import DFSConfigKeys.DFS_NAMENODE_AVOID_STALE_DATANODE_FOR_READ_KEY;
import DFSConfigKeys.DFS_NAMENODE_STALE_DATANODE_INTERVAL_KEY;
import DatanodeReportType.LIVE;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hdfs.protocol.Block;
import org.apache.hadoop.hdfs.protocol.DatanodeInfo;
import org.apache.hadoop.hdfs.protocol.HdfsConstants;
import org.apache.hadoop.hdfs.protocol.LocatedBlock;
import org.apache.hadoop.hdfs.protocol.LocatedBlocks;
import org.apache.hadoop.hdfs.server.blockmanagement.DatanodeDescriptor;
import org.apache.hadoop.hdfs.server.datanode.DataNode;
import org.apache.hadoop.hdfs.server.datanode.DataNodeTestUtils;
import org.apache.hadoop.hdfs.server.protocol.BlocksWithLocations.BlockWithLocations;
import org.apache.hadoop.hdfs.server.protocol.NamenodeProtocol;
import org.junit.Assert;
import org.junit.Test;


/**
 * This class tests if getblocks request works correctly.
 */
public class TestGetBlocks {
    private static final int blockSize = 8192;

    private static final String[] racks = new String[]{ "/d1/r1", "/d1/r1", "/d1/r2", "/d1/r2", "/d1/r2", "/d2/r3", "/d2/r3" };

    private static final int numDatanodes = TestGetBlocks.racks.length;

    /**
     * Test if the datanodes returned by
     * {@link ClientProtocol#getBlockLocations(String, long, long)} is correct
     * when stale nodes checking is enabled. Also test during the scenario when 1)
     * stale nodes checking is enabled, 2) a writing is going on, 3) a datanode
     * becomes stale happen simultaneously
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testReadSelectNonStaleDatanode() throws Exception {
        HdfsConfiguration conf = new HdfsConfiguration();
        conf.setBoolean(DFS_NAMENODE_AVOID_STALE_DATANODE_FOR_READ_KEY, true);
        long staleInterval = (30 * 1000) * 60;
        conf.setLong(DFS_NAMENODE_STALE_DATANODE_INTERVAL_KEY, staleInterval);
        MiniDFSCluster cluster = new MiniDFSCluster.Builder(conf).numDataNodes(TestGetBlocks.numDatanodes).racks(TestGetBlocks.racks).build();
        cluster.waitActive();
        InetSocketAddress addr = new InetSocketAddress("localhost", cluster.getNameNodePort());
        DFSClient client = new DFSClient(addr, conf);
        List<DatanodeDescriptor> nodeInfoList = cluster.getNameNode().getNamesystem().getBlockManager().getDatanodeManager().getDatanodeListForReport(LIVE);
        Assert.assertEquals("Unexpected number of datanodes", TestGetBlocks.numDatanodes, nodeInfoList.size());
        FileSystem fileSys = cluster.getFileSystem();
        FSDataOutputStream stm = null;
        try {
            // do the writing but do not close the FSDataOutputStream
            // in order to mimic the ongoing writing
            final Path fileName = new Path("/file1");
            stm = fileSys.create(fileName, true, fileSys.getConf().getInt(IO_FILE_BUFFER_SIZE_KEY, 4096), ((short) (3)), TestGetBlocks.blockSize);
            stm.write(new byte[((TestGetBlocks.blockSize) * 3) / 2]);
            // We do not close the stream so that
            // the writing seems to be still ongoing
            stm.hflush();
            LocatedBlocks blocks = client.getNamenode().getBlockLocations(fileName.toString(), 0, TestGetBlocks.blockSize);
            DatanodeInfo[] nodes = blocks.get(0).getLocations();
            Assert.assertEquals(nodes.length, 3);
            DataNode staleNode = null;
            DatanodeDescriptor staleNodeInfo = null;
            // stop the heartbeat of the first node
            staleNode = this.stopDataNodeHeartbeat(cluster, nodes[0].getHostName());
            Assert.assertNotNull(staleNode);
            // set the first node as stale
            staleNodeInfo = cluster.getNameNode().getNamesystem().getBlockManager().getDatanodeManager().getDatanode(staleNode.getDatanodeId());
            DFSTestUtil.resetLastUpdatesWithOffset(staleNodeInfo, (-(staleInterval + 1)));
            LocatedBlocks blocksAfterStale = client.getNamenode().getBlockLocations(fileName.toString(), 0, TestGetBlocks.blockSize);
            DatanodeInfo[] nodesAfterStale = blocksAfterStale.get(0).getLocations();
            Assert.assertEquals(nodesAfterStale.length, 3);
            Assert.assertEquals(nodesAfterStale[2].getHostName(), nodes[0].getHostName());
            // restart the staleNode's heartbeat
            DataNodeTestUtils.setHeartbeatsDisabledForTests(staleNode, false);
            // reset the first node as non-stale, so as to avoid two stale nodes
            DFSTestUtil.resetLastUpdatesWithOffset(staleNodeInfo, 0);
            LocatedBlock lastBlock = client.getLocatedBlocks(fileName.toString(), 0, Long.MAX_VALUE).getLastLocatedBlock();
            nodes = lastBlock.getLocations();
            Assert.assertEquals(nodes.length, 3);
            // stop the heartbeat of the first node for the last block
            staleNode = this.stopDataNodeHeartbeat(cluster, nodes[0].getHostName());
            Assert.assertNotNull(staleNode);
            // set the node as stale
            DatanodeDescriptor dnDesc = cluster.getNameNode().getNamesystem().getBlockManager().getDatanodeManager().getDatanode(staleNode.getDatanodeId());
            DFSTestUtil.resetLastUpdatesWithOffset(dnDesc, (-(staleInterval + 1)));
            LocatedBlock lastBlockAfterStale = client.getLocatedBlocks(fileName.toString(), 0, Long.MAX_VALUE).getLastLocatedBlock();
            nodesAfterStale = lastBlockAfterStale.getLocations();
            Assert.assertEquals(nodesAfterStale.length, 3);
            Assert.assertEquals(nodesAfterStale[2].getHostName(), nodes[0].getHostName());
        } finally {
            if (stm != null) {
                stm.close();
            }
            client.close();
            cluster.shutdown();
        }
    }

    /**
     * test getBlocks
     */
    @Test
    public void testGetBlocks() throws Exception {
        final Configuration CONF = new HdfsConfiguration();
        final short REPLICATION_FACTOR = ((short) (2));
        final int DEFAULT_BLOCK_SIZE = 1024;
        CONF.setLong(DFS_BLOCK_SIZE_KEY, DEFAULT_BLOCK_SIZE);
        CONF.setLong(DFS_BALANCER_GETBLOCKS_MIN_BLOCK_SIZE_KEY, DEFAULT_BLOCK_SIZE);
        MiniDFSCluster cluster = new MiniDFSCluster.Builder(CONF).numDataNodes(REPLICATION_FACTOR).storagesPerDatanode(4).build();
        try {
            cluster.waitActive();
            // the third block will not be visible to getBlocks
            long fileLen = (12 * DEFAULT_BLOCK_SIZE) + 1;
            DFSTestUtil.createFile(cluster.getFileSystem(), new Path("/tmp.txt"), fileLen, REPLICATION_FACTOR, 0L);
            // get blocks & data nodes
            List<LocatedBlock> locatedBlocks;
            DatanodeInfo[] dataNodes = null;
            boolean notWritten;
            final DFSClient dfsclient = new DFSClient(DFSUtilClient.getNNAddress(CONF), CONF);
            do {
                locatedBlocks = dfsclient.getNamenode().getBlockLocations("/tmp.txt", 0, fileLen).getLocatedBlocks();
                Assert.assertEquals(13, locatedBlocks.size());
                notWritten = false;
                for (int i = 0; i < 2; i++) {
                    dataNodes = locatedBlocks.get(i).getLocations();
                    if ((dataNodes.length) != REPLICATION_FACTOR) {
                        notWritten = true;
                        try {
                            Thread.sleep(10);
                        } catch (InterruptedException e) {
                        }
                        break;
                    }
                }
            } while (notWritten );
            dfsclient.close();
            // get RPC client to namenode
            InetSocketAddress addr = new InetSocketAddress("localhost", cluster.getNameNodePort());
            NamenodeProtocol namenode = NameNodeProxies.createProxy(CONF, DFSUtilClient.getNNUri(addr), NamenodeProtocol.class).getProxy();
            // get blocks of size fileLen from dataNodes[0], with minBlockSize as
            // fileLen
            BlockWithLocations[] locs;
            // Should return all 13 blocks, as minBlockSize is not passed
            locs = namenode.getBlocks(dataNodes[0], fileLen, 0).getBlocks();
            Assert.assertEquals(13, locs.length);
            Assert.assertEquals(locs[0].getStorageIDs().length, 2);
            Assert.assertEquals(locs[1].getStorageIDs().length, 2);
            // Should return 12 blocks, as minBlockSize is DEFAULT_BLOCK_SIZE
            locs = namenode.getBlocks(dataNodes[0], fileLen, DEFAULT_BLOCK_SIZE).getBlocks();
            Assert.assertEquals(12, locs.length);
            Assert.assertEquals(locs[0].getStorageIDs().length, 2);
            Assert.assertEquals(locs[1].getStorageIDs().length, 2);
            // get blocks of size BlockSize from dataNodes[0]
            locs = namenode.getBlocks(dataNodes[0], DEFAULT_BLOCK_SIZE, DEFAULT_BLOCK_SIZE).getBlocks();
            Assert.assertEquals(locs.length, 1);
            Assert.assertEquals(locs[0].getStorageIDs().length, 2);
            // get blocks of size 1 from dataNodes[0]
            locs = namenode.getBlocks(dataNodes[0], 1, 1).getBlocks();
            Assert.assertEquals(locs.length, 1);
            Assert.assertEquals(locs[0].getStorageIDs().length, 2);
            // get blocks of size 0 from dataNodes[0]
            getBlocksWithException(namenode, dataNodes[0], 0, 0);
            // get blocks of size -1 from dataNodes[0]
            getBlocksWithException(namenode, dataNodes[0], (-1), 0);
            // minBlockSize is -1
            getBlocksWithException(namenode, dataNodes[0], DEFAULT_BLOCK_SIZE, (-1));
            // get blocks of size BlockSize from a non-existent datanode
            DatanodeInfo info = DFSTestUtil.getDatanodeInfo("1.2.3.4");
            getBlocksWithIncorrectDatanodeException(namenode, info, 2, 0);
            testBlockIterator(cluster);
        } finally {
            cluster.shutdown();
        }
    }

    @Test
    public void testBlockKey() {
        Map<Block, Long> map = new HashMap<Block, Long>();
        final Random RAN = new Random();
        final long seed = RAN.nextLong();
        System.out.println(("seed=" + seed));
        RAN.setSeed(seed);
        long[] blkids = new long[10];
        for (int i = 0; i < (blkids.length); i++) {
            blkids[i] = 1000L + (RAN.nextInt(100000));
            map.put(new Block(blkids[i], 0, blkids[i]), blkids[i]);
        }
        System.out.println(("map=" + (map.toString().replace(",", "\n  "))));
        for (int i = 0; i < (blkids.length); i++) {
            Block b = new Block(blkids[i], 0, HdfsConstants.GRANDFATHER_GENERATION_STAMP);
            Long v = map.get(b);
            System.out.println(((b + " => ") + v));
            Assert.assertEquals(blkids[i], v.longValue());
        }
    }
}

