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


import java.util.Arrays;
import java.util.BitSet;
import java.util.List;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hdfs.DFSTestUtil;
import org.apache.hadoop.hdfs.DistributedFileSystem;
import org.apache.hadoop.hdfs.MiniDFSCluster;
import org.apache.hadoop.hdfs.StripedFileTestUtil;
import org.apache.hadoop.hdfs.protocol.Block;
import org.apache.hadoop.hdfs.protocol.DatanodeInfo;
import org.apache.hadoop.hdfs.protocol.ErasureCodingPolicy;
import org.apache.hadoop.hdfs.protocol.LocatedBlocks;
import org.apache.hadoop.hdfs.protocol.LocatedStripedBlock;
import org.apache.hadoop.hdfs.server.blockmanagement.BlockInfoStriped;
import org.apache.hadoop.hdfs.server.blockmanagement.BlockManager;
import org.apache.hadoop.hdfs.server.datanode.DataNode;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.Timeout;


public class TestAddOverReplicatedStripedBlocks {
    private MiniDFSCluster cluster;

    private DistributedFileSystem fs;

    private final Path dirPath = new Path("/striped");

    private Path filePath = new Path(dirPath, "file");

    private final ErasureCodingPolicy ecPolicy = StripedFileTestUtil.getDefaultECPolicy();

    private final short dataBlocks = ((short) (ecPolicy.getNumDataUnits()));

    private final short parityBlocks = ((short) (ecPolicy.getNumParityUnits()));

    private final short groupSize = ((short) ((dataBlocks) + (parityBlocks)));

    private final int cellSize = ecPolicy.getCellSize();

    private final int stripesPerBlock = 4;

    private final int blockSize = (stripesPerBlock) * (cellSize);

    private final int numDNs = (groupSize) + 3;

    @Rule
    public Timeout globalTimeout = new Timeout(300000);

    @Test
    public void testProcessOverReplicatedStripedBlock() throws Exception {
        // create a file which has exact one block group to the first GROUP_SIZE DNs
        long fileLen = (dataBlocks) * (blockSize);
        DFSTestUtil.createStripedFile(cluster, filePath, null, 1, stripesPerBlock, false);
        LocatedBlocks lbs = cluster.getNameNodeRpc().getBlockLocations(filePath.toString(), 0, fileLen);
        LocatedStripedBlock bg = ((LocatedStripedBlock) (lbs.get(0)));
        long gs = bg.getBlock().getGenerationStamp();
        String bpid = bg.getBlock().getBlockPoolId();
        long groupId = bg.getBlock().getBlockId();
        Block blk = new Block(groupId, blockSize, gs);
        for (int i = 0; i < (groupSize); i++) {
            blk.setBlockId((groupId + i));
            cluster.injectBlocks(i, Arrays.asList(blk), bpid);
        }
        cluster.triggerBlockReports();
        // let a internal block be over replicated with 2 redundant blocks.
        blk.setBlockId(groupId);
        cluster.injectBlocks(((numDNs) - 3), Arrays.asList(blk), bpid);
        cluster.injectBlocks(((numDNs) - 2), Arrays.asList(blk), bpid);
        // let a internal block be over replicated with 1 redundant block.
        blk.setBlockId((groupId + (dataBlocks)));
        cluster.injectBlocks(((numDNs) - 1), Arrays.asList(blk), bpid);
        // update blocksMap
        cluster.triggerBlockReports();
        // add to invalidates
        cluster.triggerHeartbeats();
        // datanode delete block
        cluster.triggerHeartbeats();
        // update blocksMap
        cluster.triggerBlockReports();
        // verify that all internal blocks exists
        lbs = cluster.getNameNodeRpc().getBlockLocations(filePath.toString(), 0, fileLen);
        StripedFileTestUtil.verifyLocatedStripedBlocks(lbs, groupSize);
    }

    @Test
    public void testProcessOverReplicatedSBSmallerThanFullBlocks() throws Exception {
        // Create a EC file which doesn't fill full internal blocks.
        int fileLen = (cellSize) * ((dataBlocks) - 1);
        byte[] content = new byte[fileLen];
        DFSTestUtil.writeFile(fs, filePath, new String(content));
        LocatedBlocks lbs = cluster.getNameNodeRpc().getBlockLocations(filePath.toString(), 0, fileLen);
        LocatedStripedBlock bg = ((LocatedStripedBlock) (lbs.get(0)));
        long gs = bg.getBlock().getGenerationStamp();
        String bpid = bg.getBlock().getBlockPoolId();
        long groupId = bg.getBlock().getBlockId();
        Block blk = new Block(groupId, blockSize, gs);
        cluster.triggerBlockReports();
        List<DatanodeInfo> infos = Arrays.asList(bg.getLocations());
        // let a internal block be over replicated with (numDNs - GROUP_SIZE + 1)
        // redundant blocks. Therefor number of internal blocks is over GROUP_SIZE.
        blk.setBlockId(groupId);
        List<DataNode> dataNodeList = cluster.getDataNodes();
        for (int i = 0; i < (numDNs); i++) {
            if (!(infos.contains(dataNodeList.get(i).getDatanodeId()))) {
                cluster.injectBlocks(i, Arrays.asList(blk), bpid);
                System.out.println(("XXX: inject block into datanode " + i));
            }
        }
        // update blocksMap
        cluster.triggerBlockReports();
        // add to invalidates
        cluster.triggerHeartbeats();
        // datanode delete block
        cluster.triggerHeartbeats();
        // update blocksMap
        cluster.triggerBlockReports();
        // verify that all internal blocks exists
        lbs = cluster.getNameNodeRpc().getBlockLocations(filePath.toString(), 0, fileLen);
        StripedFileTestUtil.verifyLocatedStripedBlocks(lbs, ((groupSize) - 1));
    }

    @Test
    public void testProcessOverReplicatedAndCorruptStripedBlock() throws Exception {
        long fileLen = (dataBlocks) * (blockSize);
        DFSTestUtil.createStripedFile(cluster, filePath, null, 1, stripesPerBlock, false);
        LocatedBlocks lbs = cluster.getNameNodeRpc().getBlockLocations(filePath.toString(), 0, fileLen);
        LocatedStripedBlock bg = ((LocatedStripedBlock) (lbs.get(0)));
        long gs = bg.getBlock().getGenerationStamp();
        String bpid = bg.getBlock().getBlockPoolId();
        long groupId = bg.getBlock().getBlockId();
        Block blk = new Block(groupId, blockSize, gs);
        BlockInfoStriped blockInfo = new BlockInfoStriped(blk, StripedFileTestUtil.getDefaultECPolicy());
        for (int i = 0; i < (groupSize); i++) {
            blk.setBlockId((groupId + i));
            cluster.injectBlocks(i, Arrays.asList(blk), bpid);
        }
        cluster.triggerBlockReports();
        // let a internal block be corrupt
        BlockManager bm = cluster.getNamesystem().getBlockManager();
        List<DatanodeInfo> infos = Arrays.asList(bg.getLocations());
        List<String> storages = Arrays.asList(bg.getStorageIDs());
        cluster.getNamesystem().writeLock();
        try {
            bm.findAndMarkBlockAsCorrupt(lbs.getLastLocatedBlock().getBlock(), infos.get(0), storages.get(0), "TEST");
        } finally {
            cluster.getNamesystem().writeUnlock();
        }
        Assert.assertEquals(1, bm.countNodes(bm.getStoredBlock(blockInfo)).corruptReplicas());
        // let a internal block be over replicated with 2 redundant block.
        blk.setBlockId((groupId + 2));
        cluster.injectBlocks(((numDNs) - 3), Arrays.asList(blk), bpid);
        cluster.injectBlocks(((numDNs) - 2), Arrays.asList(blk), bpid);
        // update blocksMap
        cluster.triggerBlockReports();
        // verify that all internal blocks exists except b0
        // the redundant internal blocks will not be deleted before the corrupted
        // block gets reconstructed. but since we set
        // DFS_NAMENODE_REPLICATION_MAX_STREAMS_KEY to 0, the reconstruction will
        // not happen
        lbs = cluster.getNameNodeRpc().getBlockLocations(filePath.toString(), 0, fileLen);
        bg = ((LocatedStripedBlock) (lbs.get(0)));
        Assert.assertEquals(((groupSize) + 1), bg.getBlockIndices().length);
        Assert.assertEquals(((groupSize) + 1), bg.getLocations().length);
        BitSet set = new BitSet(groupSize);
        for (byte index : bg.getBlockIndices()) {
            set.set(index);
        }
        Assert.assertFalse(set.get(0));
        for (int i = 1; i < (groupSize); i++) {
            Assert.assertTrue(set.get(i));
        }
    }
}

