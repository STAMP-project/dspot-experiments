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


import java.io.IOException;
import java.util.List;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hdfs.DistributedFileSystem;
import org.apache.hadoop.hdfs.MiniDFSCluster;
import org.apache.hadoop.hdfs.TestFileCreation;
import org.apache.hadoop.hdfs.protocol.Block;
import org.apache.hadoop.hdfs.protocol.LocatedBlock;
import org.apache.hadoop.hdfs.protocol.LocatedBlocks;
import org.apache.hadoop.hdfs.server.blockmanagement.BlockManager;
import org.apache.hadoop.hdfs.server.blockmanagement.BlockUnderConstructionFeature;
import org.apache.hadoop.hdfs.server.protocol.NamenodeProtocols;
import org.junit.Assert;
import org.junit.Test;


public class TestBlockUnderConstruction {
    static final String BASE_DIR = "/test/TestBlockUnderConstruction";

    static final int BLOCK_SIZE = 8192;// same as TestFileCreation.blocksize


    static final int NUM_BLOCKS = 5;// number of blocks to write


    private static MiniDFSCluster cluster;

    private static DistributedFileSystem hdfs;

    @Test
    public void testBlockCreation() throws IOException {
        Path file1 = new Path(TestBlockUnderConstruction.BASE_DIR, "file1.dat");
        FSDataOutputStream out = TestFileCreation.createFile(TestBlockUnderConstruction.hdfs, file1, 3);
        for (int idx = 0; idx < (TestBlockUnderConstruction.NUM_BLOCKS); idx++) {
            // write one block
            writeFile(file1, out, TestBlockUnderConstruction.BLOCK_SIZE);
            // verify consistency
            verifyFileBlocks(file1.toString(), true);
        }
        // close file
        out.close();
        // verify consistency
        verifyFileBlocks(file1.toString(), false);
    }

    /**
     * Test NameNode.getBlockLocations(..) on reading un-closed files.
     */
    @Test
    public void testGetBlockLocations() throws IOException {
        final NamenodeProtocols namenode = TestBlockUnderConstruction.cluster.getNameNodeRpc();
        final BlockManager blockManager = TestBlockUnderConstruction.cluster.getNamesystem().getBlockManager();
        final Path p = new Path(TestBlockUnderConstruction.BASE_DIR, "file2.dat");
        final String src = p.toString();
        final FSDataOutputStream out = TestFileCreation.createFile(TestBlockUnderConstruction.hdfs, p, 3);
        // write a half block
        int len = (TestBlockUnderConstruction.BLOCK_SIZE) >>> 1;
        writeFile(p, out, len);
        for (int i = 1; i < (TestBlockUnderConstruction.NUM_BLOCKS);) {
            // verify consistency
            final LocatedBlocks lb = namenode.getBlockLocations(src, 0, len);
            final List<LocatedBlock> blocks = lb.getLocatedBlocks();
            Assert.assertEquals(i, blocks.size());
            final Block b = blocks.get(((blocks.size()) - 1)).getBlock().getLocalBlock();
            Assert.assertFalse(blockManager.getStoredBlock(b).isComplete());
            if ((++i) < (TestBlockUnderConstruction.NUM_BLOCKS)) {
                // write one more block
                writeFile(p, out, TestBlockUnderConstruction.BLOCK_SIZE);
                len += TestBlockUnderConstruction.BLOCK_SIZE;
            }
        }
        // close file
        out.close();
    }

    /**
     * A storage ID can be invalid if the storage failed or the node
     * reregisters. When the node heart-beats, the storage report in it
     * causes storage volumes to be added back. An invalid storage ID
     * should not cause an NPE.
     */
    @Test
    public void testEmptyExpectedLocations() throws Exception {
        final NamenodeProtocols namenode = TestBlockUnderConstruction.cluster.getNameNodeRpc();
        final FSNamesystem fsn = TestBlockUnderConstruction.cluster.getNamesystem();
        final BlockManager bm = fsn.getBlockManager();
        final Path p = new Path(TestBlockUnderConstruction.BASE_DIR, "file2.dat");
        final String src = p.toString();
        final FSDataOutputStream out = TestFileCreation.createFile(TestBlockUnderConstruction.hdfs, p, 1);
        writeFile(p, out, 256);
        out.hflush();
        // make sure the block is readable
        LocatedBlocks lbs = namenode.getBlockLocations(src, 0, 256);
        LocatedBlock lastLB = lbs.getLocatedBlocks().get(0);
        final Block b = lastLB.getBlock().getLocalBlock();
        // fake a block recovery
        long blockRecoveryId = bm.nextGenerationStamp(false);
        BlockUnderConstructionFeature uc = bm.getStoredBlock(b).getUnderConstructionFeature();
        uc.initializeBlockRecovery(null, blockRecoveryId, false);
        try {
            String[] storages = new String[]{ "invalid-storage-id1" };
            fsn.commitBlockSynchronization(lastLB.getBlock(), blockRecoveryId, 256L, true, false, lastLB.getLocations(), storages);
        } catch (IllegalStateException ise) {
            // Although a failure is expected as of now, future commit policy
            // changes may make it not fail. This is not critical to the test.
        }
        // Invalid storage should not trigger an exception.
        lbs = namenode.getBlockLocations(src, 0, 256);
    }
}

