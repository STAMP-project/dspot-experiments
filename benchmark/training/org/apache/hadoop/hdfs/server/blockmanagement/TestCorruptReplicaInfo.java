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


import BlockType.CONTIGUOUS;
import BlockType.STRIPED;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import org.apache.hadoop.hdfs.DFSTestUtil;
import org.apache.hadoop.hdfs.protocol.Block;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * This test makes sure that
 *   CorruptReplicasMap::numBlocksWithCorruptReplicas and
 *   CorruptReplicasMap::getCorruptReplicaBlockIds
 *   return the correct values
 */
public class TestCorruptReplicaInfo {
    private static final Logger LOG = LoggerFactory.getLogger(TestCorruptReplicaInfo.class);

    private final Map<Long, BlockInfo> replicaMap = new HashMap<>();

    private final Map<Long, BlockInfo> stripedBlocksMap = new HashMap<>();

    @Test
    public void testCorruptReplicaInfo() throws IOException, InterruptedException {
        CorruptReplicasMap crm = new CorruptReplicasMap();
        BlockIdManager bim = Mockito.mock(BlockIdManager.class);
        Mockito.when(bim.isLegacyBlock(ArgumentMatchers.any(Block.class))).thenReturn(false);
        Mockito.when(bim.isStripedBlock(ArgumentMatchers.any(Block.class))).thenCallRealMethod();
        Assert.assertTrue((!(bim.isLegacyBlock(new Block((-1))))));
        // Make sure initial values are returned correctly
        Assert.assertEquals("Total number of corrupt blocks must initially be 0!", 0, crm.size());
        Assert.assertEquals("Number of corrupt replicas must initially be 0!", 0, crm.getCorruptBlocks());
        Assert.assertEquals("Number of corrupt striped block groups must initially be 0!", 0, crm.getCorruptECBlockGroups());
        Assert.assertNull("Param n cannot be less than 0", crm.getCorruptBlockIdsForTesting(bim, CONTIGUOUS, (-1), null));
        Assert.assertNull("Param n cannot be greater than 100", crm.getCorruptBlockIdsForTesting(bim, CONTIGUOUS, 101, null));
        long[] l = crm.getCorruptBlockIdsForTesting(bim, CONTIGUOUS, 0, null);
        Assert.assertNotNull("n = 0 must return non-null", l);
        Assert.assertEquals("n = 0 must return an empty list", 0, l.length);
        // Create a list of block ids. A list is used to allow easy
        // validation of the output of getCorruptReplicaBlockIds.
        final int blockCount = 140;
        long[] replicaIds = new long[blockCount];
        long[] stripedIds = new long[blockCount];
        for (int i = 0; i < blockCount; i++) {
            replicaIds[i] = getReplica(i).getBlockId();
            stripedIds[i] = getStripedBlock(i).getBlockId();
        }
        DatanodeDescriptor dn1 = DFSTestUtil.getLocalDatanodeDescriptor();
        DatanodeDescriptor dn2 = DFSTestUtil.getLocalDatanodeDescriptor();
        // Add to corrupt blocks map.
        // Replicas
        TestCorruptReplicaInfo.addToCorruptReplicasMap(crm, getReplica(0), dn1);
        verifyCorruptBlocksCount(crm, 1, 0);
        TestCorruptReplicaInfo.addToCorruptReplicasMap(crm, getReplica(1), dn1);
        verifyCorruptBlocksCount(crm, 2, 0);
        TestCorruptReplicaInfo.addToCorruptReplicasMap(crm, getReplica(1), dn2);
        verifyCorruptBlocksCount(crm, 2, 0);
        // Striped blocks
        TestCorruptReplicaInfo.addToCorruptReplicasMap(crm, getStripedBlock(0), dn1);
        verifyCorruptBlocksCount(crm, 2, 1);
        TestCorruptReplicaInfo.addToCorruptReplicasMap(crm, getStripedBlock(1), dn1);
        verifyCorruptBlocksCount(crm, 2, 2);
        TestCorruptReplicaInfo.addToCorruptReplicasMap(crm, getStripedBlock(1), dn2);
        verifyCorruptBlocksCount(crm, 2, 2);
        // Remove from corrupt blocks map.
        // Replicas
        crm.removeFromCorruptReplicasMap(getReplica(1));
        verifyCorruptBlocksCount(crm, 1, 2);
        crm.removeFromCorruptReplicasMap(getReplica(0));
        verifyCorruptBlocksCount(crm, 0, 2);
        // Striped blocks
        crm.removeFromCorruptReplicasMap(getStripedBlock(1));
        verifyCorruptBlocksCount(crm, 0, 1);
        crm.removeFromCorruptReplicasMap(getStripedBlock(0));
        verifyCorruptBlocksCount(crm, 0, 0);
        for (int blockId = 0; blockId < blockCount; blockId++) {
            TestCorruptReplicaInfo.addToCorruptReplicasMap(crm, getReplica(blockId), dn1);
            TestCorruptReplicaInfo.addToCorruptReplicasMap(crm, getStripedBlock(blockId), dn1);
        }
        Assert.assertEquals("Number of corrupt blocks not returning correctly", (2 * blockCount), crm.size());
        Assert.assertTrue("First five corrupt replica blocks ids are not right!", Arrays.equals(Arrays.copyOfRange(replicaIds, 0, 5), crm.getCorruptBlockIdsForTesting(bim, CONTIGUOUS, 5, null)));
        Assert.assertTrue("First five corrupt striped blocks ids are not right!", Arrays.equals(Arrays.copyOfRange(stripedIds, 0, 5), crm.getCorruptBlockIdsForTesting(bim, STRIPED, 5, null)));
        Assert.assertTrue("10 replica blocks after 7 not returned correctly!", Arrays.equals(Arrays.copyOfRange(replicaIds, 7, 17), crm.getCorruptBlockIdsForTesting(bim, CONTIGUOUS, 10, 7L)));
        Assert.assertTrue("10 striped blocks after 7 not returned correctly!", Arrays.equals(Arrays.copyOfRange(stripedIds, 7, 17), crm.getCorruptBlockIdsForTesting(bim, STRIPED, 10, getStripedBlock(7).getBlockId())));
    }
}

