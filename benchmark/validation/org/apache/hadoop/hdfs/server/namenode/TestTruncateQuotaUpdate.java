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


import org.apache.hadoop.hdfs.server.blockmanagement.BlockInfo;
import org.junit.Assert;
import org.junit.Test;


/**
 * Make sure we correctly update the quota usage for truncate.
 * We need to cover the following cases:
 * 1. No snapshot, truncate to 0
 * 2. No snapshot, truncate at block boundary
 * 3. No snapshot, not on block boundary
 * 4~6. With snapshot, all the current blocks are included in latest
 *      snapshots, repeat 1~3
 * 7~9. With snapshot, blocks in the latest snapshot and blocks in the current
 *      file diverged, repeat 1~3
 */
public class TestTruncateQuotaUpdate {
    private static final int BLOCKSIZE = 1024;

    private static final short REPLICATION = 4;

    private long nextMockBlockId;

    private long nextMockGenstamp;

    private long nextMockINodeId;

    @Test
    public void testTruncateWithoutSnapshot() {
        INodeFile file = createMockFile((((TestTruncateQuotaUpdate.BLOCKSIZE) * 2) + ((TestTruncateQuotaUpdate.BLOCKSIZE) / 2)), TestTruncateQuotaUpdate.REPLICATION);
        // case 1: first truncate to 1.5 blocks
        // we truncate 1 blocks, but not on the boundary, thus the diff should
        // be -block + (block - 0.5 block) = -0.5 block
        QuotaCounts count = new QuotaCounts.Builder().build();
        file.computeQuotaDeltaForTruncate(((TestTruncateQuotaUpdate.BLOCKSIZE) + ((TestTruncateQuotaUpdate.BLOCKSIZE) / 2)), null, count);
        Assert.assertEquals((((-(TestTruncateQuotaUpdate.BLOCKSIZE)) / 2) * (TestTruncateQuotaUpdate.REPLICATION)), count.getStorageSpace());
        // case 2: truncate to 1 block
        count = new QuotaCounts.Builder().build();
        file.computeQuotaDeltaForTruncate(TestTruncateQuotaUpdate.BLOCKSIZE, null, count);
        Assert.assertEquals(((-((TestTruncateQuotaUpdate.BLOCKSIZE) + ((TestTruncateQuotaUpdate.BLOCKSIZE) / 2))) * (TestTruncateQuotaUpdate.REPLICATION)), count.getStorageSpace());
        // case 3: truncate to 0
        count = new QuotaCounts.Builder().build();
        file.computeQuotaDeltaForTruncate(0, null, count);
        Assert.assertEquals(((-(((TestTruncateQuotaUpdate.BLOCKSIZE) * 2) + ((TestTruncateQuotaUpdate.BLOCKSIZE) / 2))) * (TestTruncateQuotaUpdate.REPLICATION)), count.getStorageSpace());
    }

    @Test
    public void testTruncateWithSnapshotNoDivergence() {
        INodeFile file = createMockFile((((TestTruncateQuotaUpdate.BLOCKSIZE) * 2) + ((TestTruncateQuotaUpdate.BLOCKSIZE) / 2)), TestTruncateQuotaUpdate.REPLICATION);
        TestTruncateQuotaUpdate.addSnapshotFeature(file, file.getBlocks());
        // case 4: truncate to 1.5 blocks
        // all the blocks are in snapshot. truncate need to allocate a new block
        // diff should be +BLOCKSIZE
        QuotaCounts count = new QuotaCounts.Builder().build();
        file.computeQuotaDeltaForTruncate(((TestTruncateQuotaUpdate.BLOCKSIZE) + ((TestTruncateQuotaUpdate.BLOCKSIZE) / 2)), null, count);
        Assert.assertEquals(((TestTruncateQuotaUpdate.BLOCKSIZE) * (TestTruncateQuotaUpdate.REPLICATION)), count.getStorageSpace());
        // case 2: truncate to 1 block
        count = new QuotaCounts.Builder().build();
        file.computeQuotaDeltaForTruncate(TestTruncateQuotaUpdate.BLOCKSIZE, null, count);
        Assert.assertEquals(0, count.getStorageSpace());
        // case 3: truncate to 0
        count = new QuotaCounts.Builder().build();
        file.computeQuotaDeltaForTruncate(0, null, count);
        Assert.assertEquals(0, count.getStorageSpace());
    }

    @Test
    public void testTruncateWithSnapshotAndDivergence() {
        INodeFile file = createMockFile((((TestTruncateQuotaUpdate.BLOCKSIZE) * 2) + ((TestTruncateQuotaUpdate.BLOCKSIZE) / 2)), TestTruncateQuotaUpdate.REPLICATION);
        BlockInfo[] blocks = new BlockInfo[file.getBlocks().length];
        System.arraycopy(file.getBlocks(), 0, blocks, 0, blocks.length);
        TestTruncateQuotaUpdate.addSnapshotFeature(file, blocks);
        // Update the last two blocks in the current inode
        file.getBlocks()[1] = newBlock(TestTruncateQuotaUpdate.BLOCKSIZE, TestTruncateQuotaUpdate.REPLICATION);
        file.getBlocks()[2] = newBlock(((TestTruncateQuotaUpdate.BLOCKSIZE) / 2), TestTruncateQuotaUpdate.REPLICATION);
        // case 7: truncate to 1.5 block
        // the block for truncation is not in snapshot, diff should be the same
        // as case 1
        QuotaCounts count = new QuotaCounts.Builder().build();
        file.computeQuotaDeltaForTruncate(((TestTruncateQuotaUpdate.BLOCKSIZE) + ((TestTruncateQuotaUpdate.BLOCKSIZE) / 2)), null, count);
        Assert.assertEquals((((-(TestTruncateQuotaUpdate.BLOCKSIZE)) / 2) * (TestTruncateQuotaUpdate.REPLICATION)), count.getStorageSpace());
        // case 8: truncate to 2 blocks
        // the original 2.5 blocks are in snapshot. the block truncated is not
        // in snapshot. diff should be -0.5 block
        count = new QuotaCounts.Builder().build();
        file.computeQuotaDeltaForTruncate(((TestTruncateQuotaUpdate.BLOCKSIZE) + ((TestTruncateQuotaUpdate.BLOCKSIZE) / 2)), null, count);
        Assert.assertEquals((((-(TestTruncateQuotaUpdate.BLOCKSIZE)) / 2) * (TestTruncateQuotaUpdate.REPLICATION)), count.getStorageSpace());
        // case 9: truncate to 0
        count = new QuotaCounts.Builder().build();
        file.computeQuotaDeltaForTruncate(0, null, count);
        Assert.assertEquals(((-((TestTruncateQuotaUpdate.BLOCKSIZE) + ((TestTruncateQuotaUpdate.BLOCKSIZE) / 2))) * (TestTruncateQuotaUpdate.REPLICATION)), count.getStorageSpace());
    }
}

