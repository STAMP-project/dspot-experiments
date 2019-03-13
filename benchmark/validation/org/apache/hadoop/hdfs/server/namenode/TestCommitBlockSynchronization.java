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
import org.apache.hadoop.hdfs.protocol.Block;
import org.apache.hadoop.hdfs.protocol.DatanodeID;
import org.apache.hadoop.hdfs.protocol.ExtendedBlock;
import org.apache.hadoop.hdfs.server.blockmanagement.BlockInfo;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


/**
 * Verify that TestCommitBlockSynchronization is idempotent.
 */
public class TestCommitBlockSynchronization {
    private static final long blockId = 100;

    private static final long length = 200;

    private static final long genStamp = 300;

    @Test
    public void testCommitBlockSynchronization() throws IOException {
        INodeFile file = mockFileUnderConstruction();
        Block block = new Block(TestCommitBlockSynchronization.blockId, TestCommitBlockSynchronization.length, TestCommitBlockSynchronization.genStamp);
        FSNamesystem namesystemSpy = makeNameSystemSpy(block, file);
        DatanodeID[] newTargets = new DatanodeID[0];
        ExtendedBlock lastBlock = new ExtendedBlock();
        namesystemSpy.commitBlockSynchronization(lastBlock, TestCommitBlockSynchronization.genStamp, TestCommitBlockSynchronization.length, false, false, newTargets, null);
        // Repeat the call to make sure it does not throw
        namesystemSpy.commitBlockSynchronization(lastBlock, TestCommitBlockSynchronization.genStamp, TestCommitBlockSynchronization.length, false, false, newTargets, null);
        // Simulate 'completing' the block.
        BlockInfo completedBlockInfo = new org.apache.hadoop.hdfs.server.blockmanagement.BlockInfoContiguous(block, ((short) (1)));
        completedBlockInfo.setBlockCollectionId(file.getId());
        completedBlockInfo.setGenerationStamp(TestCommitBlockSynchronization.genStamp);
        Mockito.doReturn(completedBlockInfo).when(namesystemSpy).getStoredBlock(ArgumentMatchers.any(Block.class));
        Mockito.doReturn(completedBlockInfo).when(file).getLastBlock();
        // Repeat the call to make sure it does not throw
        namesystemSpy.commitBlockSynchronization(lastBlock, TestCommitBlockSynchronization.genStamp, TestCommitBlockSynchronization.length, false, false, newTargets, null);
    }

    @Test
    public void testCommitBlockSynchronization2() throws IOException {
        INodeFile file = mockFileUnderConstruction();
        Block block = new Block(TestCommitBlockSynchronization.blockId, TestCommitBlockSynchronization.length, TestCommitBlockSynchronization.genStamp);
        FSNamesystem namesystemSpy = makeNameSystemSpy(block, file);
        DatanodeID[] newTargets = new DatanodeID[0];
        ExtendedBlock lastBlock = new ExtendedBlock();
        namesystemSpy.commitBlockSynchronization(lastBlock, TestCommitBlockSynchronization.genStamp, TestCommitBlockSynchronization.length, false, false, newTargets, null);
        // Make sure the call fails if the generation stamp does not match
        // the block recovery ID.
        try {
            namesystemSpy.commitBlockSynchronization(lastBlock, ((TestCommitBlockSynchronization.genStamp) - 1), TestCommitBlockSynchronization.length, false, false, newTargets, null);
            Assert.fail(("Failed to get expected IOException on generation stamp/" + "recovery ID mismatch"));
        } catch (IOException ioe) {
            // Expected exception.
        }
    }

    @Test
    public void testCommitBlockSynchronizationWithDelete() throws IOException {
        INodeFile file = mockFileUnderConstruction();
        Block block = new Block(TestCommitBlockSynchronization.blockId, TestCommitBlockSynchronization.length, TestCommitBlockSynchronization.genStamp);
        FSNamesystem namesystemSpy = makeNameSystemSpy(block, file);
        DatanodeID[] newTargets = new DatanodeID[0];
        ExtendedBlock lastBlock = new ExtendedBlock();
        namesystemSpy.commitBlockSynchronization(lastBlock, TestCommitBlockSynchronization.genStamp, TestCommitBlockSynchronization.length, false, true, newTargets, null);
        // Simulate removing the last block from the file.
        Mockito.doReturn(null).when(file).removeLastBlock(ArgumentMatchers.any(Block.class));
        // Repeat the call to make sure it does not throw
        namesystemSpy.commitBlockSynchronization(lastBlock, TestCommitBlockSynchronization.genStamp, TestCommitBlockSynchronization.length, false, true, newTargets, null);
    }

    @Test
    public void testCommitBlockSynchronizationWithClose() throws IOException {
        INodeFile file = mockFileUnderConstruction();
        Block block = new Block(TestCommitBlockSynchronization.blockId, TestCommitBlockSynchronization.length, TestCommitBlockSynchronization.genStamp);
        FSNamesystem namesystemSpy = makeNameSystemSpy(block, file);
        DatanodeID[] newTargets = new DatanodeID[0];
        ExtendedBlock lastBlock = new ExtendedBlock();
        namesystemSpy.commitBlockSynchronization(lastBlock, TestCommitBlockSynchronization.genStamp, TestCommitBlockSynchronization.length, true, false, newTargets, null);
        // Repeat the call to make sure it returns true
        namesystemSpy.commitBlockSynchronization(lastBlock, TestCommitBlockSynchronization.genStamp, TestCommitBlockSynchronization.length, true, false, newTargets, null);
        BlockInfo completedBlockInfo = new org.apache.hadoop.hdfs.server.blockmanagement.BlockInfoContiguous(block, ((short) (1)));
        completedBlockInfo.setBlockCollectionId(file.getId());
        completedBlockInfo.setGenerationStamp(TestCommitBlockSynchronization.genStamp);
        Mockito.doReturn(completedBlockInfo).when(namesystemSpy).getStoredBlock(ArgumentMatchers.any(Block.class));
        Mockito.doReturn(completedBlockInfo).when(file).getLastBlock();
        namesystemSpy.commitBlockSynchronization(lastBlock, TestCommitBlockSynchronization.genStamp, TestCommitBlockSynchronization.length, true, false, newTargets, null);
    }

    @Test
    public void testCommitBlockSynchronizationWithCloseAndNonExistantTarget() throws IOException {
        INodeFile file = mockFileUnderConstruction();
        Block block = new Block(TestCommitBlockSynchronization.blockId, TestCommitBlockSynchronization.length, TestCommitBlockSynchronization.genStamp);
        FSNamesystem namesystemSpy = makeNameSystemSpy(block, file);
        DatanodeID[] newTargets = new DatanodeID[]{ new DatanodeID("0.0.0.0", "nonexistantHost", "1", 0, 0, 0, 0) };
        String[] storageIDs = new String[]{ "fake-storage-ID" };
        ExtendedBlock lastBlock = new ExtendedBlock();
        namesystemSpy.commitBlockSynchronization(lastBlock, TestCommitBlockSynchronization.genStamp, TestCommitBlockSynchronization.length, true, false, newTargets, storageIDs);
        // Repeat the call to make sure it returns true
        namesystemSpy.commitBlockSynchronization(lastBlock, TestCommitBlockSynchronization.genStamp, TestCommitBlockSynchronization.length, true, false, newTargets, storageIDs);
    }
}

