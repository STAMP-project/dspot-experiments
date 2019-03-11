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


import LowRedundancyBlocks.QUEUE_HIGHEST_PRIORITY;
import LowRedundancyBlocks.QUEUE_LOW_REDUNDANCY;
import LowRedundancyBlocks.QUEUE_VERY_LOW_REDUNDANCY;
import LowRedundancyBlocks.QUEUE_WITH_CORRUPT_BLOCKS;
import org.apache.hadoop.hdfs.protocol.ErasureCodingPolicy;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;


/**
 * Test {@link LowRedundancyBlocks}.
 */
@RunWith(Parameterized.class)
public class TestLowRedundancyBlockQueues {
    private final ErasureCodingPolicy ecPolicy;

    public TestLowRedundancyBlockQueues(ErasureCodingPolicy policy) {
        ecPolicy = policy;
    }

    /**
     * Test that adding blocks with different replication counts puts them
     * into different queues.
     *
     * @throws Throwable
     * 		if something goes wrong
     */
    @Test
    public void testBlockPriorities() throws Throwable {
        LowRedundancyBlocks queues = new LowRedundancyBlocks();
        BlockInfo block1 = genBlockInfo(1);
        BlockInfo block2 = genBlockInfo(2);
        BlockInfo block_very_low_redundancy = genBlockInfo(3);
        BlockInfo block_corrupt = genBlockInfo(4);
        BlockInfo block_corrupt_repl_one = genBlockInfo(5);
        // Add a block with a single entry
        assertAdded(queues, block1, 1, 0, 3);
        assertInLevel(queues, block1, QUEUE_HIGHEST_PRIORITY);
        verifyBlockStats(queues, 1, 0, 0, 0, 0, 1, 0);
        // Repeated additions fail
        Assert.assertFalse(queues.add(block1, 1, 0, 0, 3));
        verifyBlockStats(queues, 1, 0, 0, 0, 0, 1, 0);
        // Add a second block with two replicas
        assertAdded(queues, block2, 2, 0, 3);
        assertInLevel(queues, block2, QUEUE_LOW_REDUNDANCY);
        verifyBlockStats(queues, 2, 0, 0, 0, 0, 1, 0);
        // Now try to add a block that is corrupt
        assertAdded(queues, block_corrupt, 0, 0, 3);
        assertInLevel(queues, block_corrupt, QUEUE_WITH_CORRUPT_BLOCKS);
        verifyBlockStats(queues, 2, 1, 0, 0, 0, 1, 0);
        // Insert a very insufficiently redundancy block
        assertAdded(queues, block_very_low_redundancy, 4, 0, 25);
        assertInLevel(queues, block_very_low_redundancy, QUEUE_VERY_LOW_REDUNDANCY);
        verifyBlockStats(queues, 3, 1, 0, 0, 0, 1, 0);
        // Insert a corrupt block with replication factor 1
        assertAdded(queues, block_corrupt_repl_one, 0, 0, 1);
        verifyBlockStats(queues, 3, 2, 1, 0, 0, 1, 0);
        // Bump up the expected count for corrupt replica one block from 1 to 3
        queues.update(block_corrupt_repl_one, 0, 0, 0, 3, 0, 2);
        verifyBlockStats(queues, 3, 2, 0, 0, 0, 1, 0);
        // Reduce the expected replicas to 1
        queues.update(block_corrupt, 0, 0, 0, 1, 0, (-2));
        verifyBlockStats(queues, 3, 2, 1, 0, 0, 1, 0);
        queues.update(block_very_low_redundancy, 0, 0, 0, 1, (-4), (-24));
        verifyBlockStats(queues, 2, 3, 2, 0, 0, 1, 0);
        // Reduce the expected replicas to 1 for block1
        queues.update(block1, 1, 0, 0, 1, 0, 0);
        verifyBlockStats(queues, 2, 3, 2, 0, 0, 0, 0);
    }

    @Test
    public void testRemoveWithWrongPriority() {
        final LowRedundancyBlocks queues = new LowRedundancyBlocks();
        final BlockInfo corruptBlock = genBlockInfo(1);
        assertAdded(queues, corruptBlock, 0, 0, 3);
        assertInLevel(queues, corruptBlock, QUEUE_WITH_CORRUPT_BLOCKS);
        verifyBlockStats(queues, 0, 1, 0, 0, 0, 0, 0);
        // Remove with wrong priority
        queues.remove(corruptBlock, QUEUE_LOW_REDUNDANCY);
        // Verify the number of corrupt block is decremented
        verifyBlockStats(queues, 0, 0, 0, 0, 0, 0, 0);
    }

    @Test
    public void testStripedBlockPriorities() throws Throwable {
        int dataBlkNum = ecPolicy.getNumDataUnits();
        int parityBlkNUm = ecPolicy.getNumParityUnits();
        doTestStripedBlockPriorities(1, parityBlkNUm);
        doTestStripedBlockPriorities(dataBlkNum, parityBlkNUm);
    }
}

