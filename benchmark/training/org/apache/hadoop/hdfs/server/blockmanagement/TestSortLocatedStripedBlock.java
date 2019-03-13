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


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.apache.hadoop.hdfs.StripedFileTestUtil;
import org.apache.hadoop.hdfs.protocol.DatanodeInfo;
import org.apache.hadoop.hdfs.protocol.ErasureCodingPolicy;
import org.apache.hadoop.hdfs.protocol.LocatedBlock;
import org.apache.hadoop.hdfs.security.token.block.BlockTokenIdentifier;
import org.apache.hadoop.security.token.Token;
import org.apache.hadoop.util.Time;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * This class tests the sorting of located striped blocks based on
 * decommissioned states.
 */
public class TestSortLocatedStripedBlock {
    static final Logger LOG = LoggerFactory.getLogger(TestSortLocatedStripedBlock.class);

    private final ErasureCodingPolicy ecPolicy = StripedFileTestUtil.getDefaultECPolicy();

    private final int cellSize = ecPolicy.getCellSize();

    private final short dataBlocks = ((short) (ecPolicy.getNumDataUnits()));

    private final short parityBlocks = ((short) (ecPolicy.getNumParityUnits()));

    private final int groupSize = (dataBlocks) + (parityBlocks);

    static DatanodeManager dm;

    static final long STALE_INTERVAL = (30 * 1000) * 60;

    /**
     * Test to verify sorting with multiple decommissioned datanodes exists in
     * storage lists.
     *
     * We have storage list, marked decommissioned internal blocks with a '
     * d0, d1, d2, d3, d4, d5, d6, d7, d8, d9, d10, d11, d12
     * mapping to indices
     * 0', 1', 2, 3, 4, 5, 6, 7', 8', 0, 1, 7, 8
     *
     * Decommissioned node indices: 0, 1, 7, 8
     *
     * So in the original list nodes d0, d1, d7, d8 are decommissioned state.
     *
     * After sorting the expected block indices list should be,
     * 0, 1, 2, 3, 4, 5, 6, 7, 8, 0', 1', 7', 8'
     *
     * After sorting the expected storage list will be,
     * d9, d10, d2, d3, d4, d5, d6, d11, d12, d0, d1, d7, d8.
     *
     * Note: after sorting block indices will not be in ascending order.
     */
    @Test(timeout = 10000)
    public void testWithMultipleDecommnDatanodes() {
        TestSortLocatedStripedBlock.LOG.info("Starting test testSortWithMultipleDecommnDatanodes");
        int lbsCount = 2;// two located block groups

        List<Integer> decommnNodeIndices = new ArrayList<>();
        decommnNodeIndices.add(0);
        decommnNodeIndices.add(1);
        decommnNodeIndices.add(7);
        decommnNodeIndices.add(8);
        List<Integer> targetNodeIndices = new ArrayList<>();
        targetNodeIndices.addAll(decommnNodeIndices);
        // map contains decommissioned node details in each located strip block
        // which will be used for assertions
        HashMap<Integer, List<String>> decommissionedNodes = new HashMap<>((lbsCount * (decommnNodeIndices.size())));
        List<LocatedBlock> lbs = createLocatedStripedBlocks(lbsCount, dataBlocks, parityBlocks, decommnNodeIndices, targetNodeIndices, decommissionedNodes);
        // prepare expected block index and token list.
        List<HashMap<DatanodeInfo, Byte>> locToIndexList = new ArrayList<>();
        List<HashMap<DatanodeInfo, Token<BlockTokenIdentifier>>> locToTokenList = new ArrayList<>();
        prepareBlockIndexAndTokenList(lbs, locToIndexList, locToTokenList);
        TestSortLocatedStripedBlock.dm.sortLocatedBlocks(null, lbs);
        assertDecommnNodePosition(groupSize, decommissionedNodes, lbs);
        assertBlockIndexAndTokenPosition(lbs, locToIndexList, locToTokenList);
    }

    /**
     * Test to verify sorting with two decommissioned datanodes exists in
     * storage lists for the same block index.
     *
     * We have storage list, marked decommissioned internal blocks with a '
     * d0, d1, d2, d3, d4, d5, d6, d7, d8, d9, d10, d11, d12, d13
     * mapping to indices
     * 0', 1', 2, 3, 4', 5', 6, 7, 8, 0, 1', 4, 5, 1
     *
     * Decommissioned node indices: 0', 1', 4', 5', 1'
     *
     * Here decommissioned has done twice to the datanode block index 1.
     * So in the original list nodes d0, d1, d4, d5, d10 are decommissioned state.
     *
     * After sorting the expected block indices list will be,
     * 0, 1, 2, 3, 4, 5, 6, 7, 8, 0', 1', 1', 4', 5'
     *
     * After sorting the expected storage list will be,
     * d9, d13, d2, d3, d11, d12, d6, d7, d8, d0, d1, d10, d4, d5.
     *
     * Note: after sorting block indices will not be in ascending order.
     */
    @Test(timeout = 10000)
    public void testTwoDatanodesWithSameBlockIndexAreDecommn() {
        TestSortLocatedStripedBlock.LOG.info("Starting test testTwoDatanodesWithSameBlockIndexAreDecommn");
        int lbsCount = 2;// two located block groups

        List<Integer> decommnNodeIndices = new ArrayList<>();
        decommnNodeIndices.add(0);
        decommnNodeIndices.add(1);
        decommnNodeIndices.add(4);
        decommnNodeIndices.add(5);
        // representing blockIndex 1, later this also decommissioned
        decommnNodeIndices.add(1);
        List<Integer> targetNodeIndices = new ArrayList<>();
        targetNodeIndices.addAll(decommnNodeIndices);
        // map contains decommissioned node details in each located strip block
        // which will be used for assertions
        HashMap<Integer, List<String>> decommissionedNodes = new HashMap<>((lbsCount * (decommnNodeIndices.size())));
        List<LocatedBlock> lbs = createLocatedStripedBlocks(lbsCount, dataBlocks, parityBlocks, decommnNodeIndices, targetNodeIndices, decommissionedNodes);
        // prepare expected block index and token list.
        List<HashMap<DatanodeInfo, Byte>> locToIndexList = new ArrayList<>();
        List<HashMap<DatanodeInfo, Token<BlockTokenIdentifier>>> locToTokenList = new ArrayList<>();
        prepareBlockIndexAndTokenList(lbs, locToIndexList, locToTokenList);
        TestSortLocatedStripedBlock.dm.sortLocatedBlocks(null, lbs);
        assertDecommnNodePosition(groupSize, decommissionedNodes, lbs);
        assertBlockIndexAndTokenPosition(lbs, locToIndexList, locToTokenList);
    }

    /**
     * Test to verify sorting with decommissioned datanodes exists in storage
     * list which is smaller than stripe size.
     *
     * We have storage list, marked decommissioned internal blocks with a '
     * d0, d1, d2, d3, d6, d7, d8, d9, d10, d11
     * mapping to indices
     * 0', 1, 2', 3, 6, 7, 8, 0, 2', 2
     *
     * Decommissioned node indices: 0', 2', 2'
     *
     * Here decommissioned has done twice to the datanode block index 2.
     * So in the original list nodes d0, d2, d10 are decommissioned state.
     *
     * After sorting the expected block indices list should be,
     * 0, 1, 2, 3, 6, 7, 8, 0', 2', 2'
     *
     * After sorting the expected storage list will be,
     * d9, d1, d11, d3, d6, d7, d8, d0, d2, d10.
     *
     * Note: after sorting block indices will not be in ascending order.
     */
    @Test(timeout = 10000)
    public void testSmallerThanOneStripeWithMultpleDecommnNodes() throws Exception {
        TestSortLocatedStripedBlock.LOG.info("Starting test testSmallerThanOneStripeWithDecommn");
        int lbsCount = 2;// two located block groups

        List<Integer> decommnNodeIndices = new ArrayList<>();
        decommnNodeIndices.add(0);
        decommnNodeIndices.add(2);
        // representing blockIndex 1, later this also decommissioned
        decommnNodeIndices.add(2);
        List<Integer> targetNodeIndices = new ArrayList<>();
        targetNodeIndices.addAll(decommnNodeIndices);
        // map contains decommissioned node details in each located strip block
        // which will be used for assertions
        HashMap<Integer, List<String>> decommissionedNodes = new HashMap<>((lbsCount * (decommnNodeIndices.size())));
        int dataBlksNum = (dataBlocks) - 2;
        List<LocatedBlock> lbs = createLocatedStripedBlocks(lbsCount, dataBlksNum, parityBlocks, decommnNodeIndices, targetNodeIndices, decommissionedNodes);
        // prepare expected block index and token list.
        List<HashMap<DatanodeInfo, Byte>> locToIndexList = new ArrayList<>();
        List<HashMap<DatanodeInfo, Token<BlockTokenIdentifier>>> locToTokenList = new ArrayList<>();
        prepareBlockIndexAndTokenList(lbs, locToIndexList, locToTokenList);
        TestSortLocatedStripedBlock.dm.sortLocatedBlocks(null, lbs);
        // After this index all are decommissioned nodes.
        int blkGrpWidth = dataBlksNum + (parityBlocks);
        assertDecommnNodePosition(blkGrpWidth, decommissionedNodes, lbs);
        assertBlockIndexAndTokenPosition(lbs, locToIndexList, locToTokenList);
    }

    /**
     * Test to verify sorting with decommissioned datanodes exists in storage
     * list but the corresponding new target datanode doesn't exists.
     *
     * We have storage list, marked decommissioned internal blocks with a '
     * d0, d1, d2, d3, d4, d5, d6, d7, d8, d9, d10, d11
     * mapping to indices
     * 0', 1', 2', 3, 4', 5', 6, 7, 8, 0, 2, 4
     *
     * Decommissioned node indices: 0', 1', 2', 4', 5'
     *
     * 1 and 5 nodes doesn't exists in the target list. This can happen, the
     * target node block corrupted or lost after the successful decommissioning.
     * So in the original list nodes corresponding to the decommissioned block
     * index 1 and 5 doesn't have any target entries.
     *
     * After sorting the expected block indices list should be,
     * 0, 2, 3, 4, 6, 7, 8, 0', 1', 2', 4', 5'
     *
     * After sorting the expected storage list will be,
     * d9, d10, d3, d11, d6, d7, d8, d0, d1, d2, d4, d5.
     *
     * Note: after sorting block indices will not be in ascending order.
     */
    @Test(timeout = 10000)
    public void testTargetDecommnDatanodeDoesntExists() {
        TestSortLocatedStripedBlock.LOG.info("Starting test testTargetDecommnDatanodeDoesntExists");
        int lbsCount = 2;// two located block groups

        List<Integer> decommnNodeIndices = new ArrayList<>();
        decommnNodeIndices.add(0);
        decommnNodeIndices.add(1);
        decommnNodeIndices.add(2);
        decommnNodeIndices.add(4);
        decommnNodeIndices.add(5);
        List<Integer> targetNodeIndices = new ArrayList<>();
        targetNodeIndices.add(0);
        targetNodeIndices.add(2);
        targetNodeIndices.add(4);
        // 1 and 5 nodes doesn't exists in the target list. One such case is, the
        // target node block corrupted or lost after the successful decommissioning
        // map contains decommissioned node details in each located strip block
        // which will be used for assertions
        HashMap<Integer, List<String>> decommissionedNodes = new HashMap<>((lbsCount * (decommnNodeIndices.size())));
        List<LocatedBlock> lbs = createLocatedStripedBlocks(lbsCount, dataBlocks, parityBlocks, decommnNodeIndices, targetNodeIndices, decommissionedNodes);
        // prepare expected block index and token list.
        List<HashMap<DatanodeInfo, Byte>> locToIndexList = new ArrayList<>();
        List<HashMap<DatanodeInfo, Token<BlockTokenIdentifier>>> locToTokenList = new ArrayList<>();
        prepareBlockIndexAndTokenList(lbs, locToIndexList, locToTokenList);
        TestSortLocatedStripedBlock.dm.sortLocatedBlocks(null, lbs);
        // After this index all are decommissioned nodes. Needs to reconstruct two
        // more block indices.
        int blkGrpWidth = ((dataBlocks) + (parityBlocks)) - 2;
        assertDecommnNodePosition(blkGrpWidth, decommissionedNodes, lbs);
        assertBlockIndexAndTokenPosition(lbs, locToIndexList, locToTokenList);
    }

    /**
     * Test to verify sorting with multiple in-service and decommissioned
     * datanodes exists in storage lists.
     *
     * We have storage list, marked decommissioned internal blocks with a '
     * d0, d1, d2, d3, d4, d5, d6, d7, d8, d9, d10, d11, d12, d13
     * mapping to indices
     * 0', 1', 2, 3, 4, 5, 6, 7', 8', 0, 1, 7, 8, 1
     *
     * Decommissioned node indices: 0', 1', 7', 8'
     *
     * Additional In-Service node d13 at the end, block index: 1
     *
     * So in the original list nodes d0, d1, d7, d8 are decommissioned state.
     *
     * After sorting the expected block indices list will be,
     * 0, 1, 2, 3, 4, 5, 6, 7, 8, 1, 0', 1', 7', 8'
     *
     * After sorting the expected storage list will be,
     * d9, d10, d2, d3, d4, d5, d6, d11, d12, d13, d0, d1, d7, d8.
     *
     * Note: after sorting block indices will not be in ascending order.
     */
    @Test(timeout = 10000)
    public void testWithMultipleInServiceAndDecommnDatanodes() {
        TestSortLocatedStripedBlock.LOG.info("Starting test testWithMultipleInServiceAndDecommnDatanodes");
        int lbsCount = 2;// two located block groups

        List<Integer> decommnNodeIndices = new ArrayList<>();
        decommnNodeIndices.add(0);
        decommnNodeIndices.add(1);
        decommnNodeIndices.add(7);
        decommnNodeIndices.add(8);
        List<Integer> targetNodeIndices = new ArrayList<>();
        targetNodeIndices.addAll(decommnNodeIndices);
        // at the end add an additional In-Service node to blockIndex=1
        targetNodeIndices.add(1);
        // map contains decommissioned node details in each located strip block
        // which will be used for assertions
        HashMap<Integer, List<String>> decommissionedNodes = new HashMap<>((lbsCount * (decommnNodeIndices.size())));
        List<LocatedBlock> lbs = createLocatedStripedBlocks(lbsCount, dataBlocks, parityBlocks, decommnNodeIndices, targetNodeIndices, decommissionedNodes);
        List<DatanodeInfo> staleDns = new ArrayList<>();
        for (LocatedBlock lb : lbs) {
            DatanodeInfo[] locations = lb.getLocations();
            DatanodeInfo staleDn = locations[((locations.length) - 1)];
            staleDn.setLastUpdateMonotonic(((Time.monotonicNow()) - ((TestSortLocatedStripedBlock.STALE_INTERVAL) * 2)));
            staleDns.add(staleDn);
        }
        // prepare expected block index and token list.
        List<HashMap<DatanodeInfo, Byte>> locToIndexList = new ArrayList<>();
        List<HashMap<DatanodeInfo, Token<BlockTokenIdentifier>>> locToTokenList = new ArrayList<>();
        prepareBlockIndexAndTokenList(lbs, locToIndexList, locToTokenList);
        TestSortLocatedStripedBlock.dm.sortLocatedBlocks(null, lbs);
        assertDecommnNodePosition(((groupSize) + 1), decommissionedNodes, lbs);
        assertBlockIndexAndTokenPosition(lbs, locToIndexList, locToTokenList);
        for (LocatedBlock lb : lbs) {
            byte[] blockIndices = getBlockIndices();
            // after sorting stale block index will be placed after normal nodes.
            Assert.assertEquals("Failed to move stale node to bottom!", 1, blockIndices[9]);
            DatanodeInfo[] locations = lb.getLocations();
            // After sorting stale node d13 will be placed after normal nodes
            Assert.assertEquals("Failed to move stale dn after normal one!", staleDns.remove(0), locations[9]);
        }
    }
}

