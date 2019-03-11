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


import BlockManager.blockLog;
import DFSClient.LOG;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hdfs.protocol.DatanodeID;
import org.apache.hadoop.hdfs.protocol.DatanodeInfo;
import org.apache.hadoop.hdfs.protocol.ErasureCodingPolicy;
import org.apache.hadoop.hdfs.protocol.ExtendedBlock;
import org.apache.hadoop.hdfs.server.blockmanagement.BlockManagerTestUtil;
import org.apache.hadoop.hdfs.server.blockmanagement.DatanodeStorageInfo;
import org.apache.hadoop.hdfs.server.datanode.DataNode;
import org.apache.hadoop.hdfs.server.protocol.BlockECReconstructionCommand.BlockECReconstructionInfo;
import org.apache.hadoop.hdfs.server.protocol.DatanodeStorage;
import org.apache.hadoop.test.GenericTestUtils;
import org.apache.log4j.Level;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class TestReconstructStripedFile {
    public static final Logger LOG = LoggerFactory.getLogger(TestReconstructStripedFile.class);

    private ErasureCodingPolicy ecPolicy;

    private int dataBlkNum;

    private int parityBlkNum;

    private int cellSize;

    private int blockSize;

    private int groupSize;

    private int dnNum;

    static {
        GenericTestUtils.setLogLevel(DFSClient.LOG, Level.ALL);
        GenericTestUtils.setLogLevel(BlockManager.LOG, Level.ALL);
        GenericTestUtils.setLogLevel(blockLog, Level.ALL);
    }

    enum ReconstructionType {

        DataOnly,
        ParityOnly,
        Any;}

    private Configuration conf;

    private MiniDFSCluster cluster;

    private DistributedFileSystem fs;

    // Map: DatanodeID -> datanode index in cluster
    private Map<DatanodeID, Integer> dnMap = new HashMap<>();

    private final Random random = new Random();

    @Test(timeout = 120000)
    public void testRecoverOneParityBlock() throws Exception {
        int fileLen = (((dataBlkNum) + 1) * (blockSize)) + ((blockSize) / 10);
        assertFileBlocksReconstruction("/testRecoverOneParityBlock", fileLen, TestReconstructStripedFile.ReconstructionType.ParityOnly, 1);
    }

    @Test(timeout = 120000)
    public void testRecoverOneParityBlock1() throws Exception {
        int fileLen = (cellSize) + ((cellSize) / 10);
        assertFileBlocksReconstruction("/testRecoverOneParityBlock1", fileLen, TestReconstructStripedFile.ReconstructionType.ParityOnly, 1);
    }

    @Test(timeout = 120000)
    public void testRecoverOneParityBlock2() throws Exception {
        int fileLen = 1;
        assertFileBlocksReconstruction("/testRecoverOneParityBlock2", fileLen, TestReconstructStripedFile.ReconstructionType.ParityOnly, 1);
    }

    @Test(timeout = 120000)
    public void testRecoverOneParityBlock3() throws Exception {
        int fileLen = (((dataBlkNum) - 1) * (blockSize)) + ((blockSize) / 10);
        assertFileBlocksReconstruction("/testRecoverOneParityBlock3", fileLen, TestReconstructStripedFile.ReconstructionType.ParityOnly, 1);
    }

    @Test(timeout = 120000)
    public void testRecoverAllParityBlocks() throws Exception {
        int fileLen = ((dataBlkNum) * (blockSize)) + ((blockSize) / 10);
        assertFileBlocksReconstruction("/testRecoverAllParityBlocks", fileLen, TestReconstructStripedFile.ReconstructionType.ParityOnly, parityBlkNum);
    }

    @Test(timeout = 120000)
    public void testRecoverAllDataBlocks() throws Exception {
        int fileLen = (((dataBlkNum) + (parityBlkNum)) * (blockSize)) + ((blockSize) / 10);
        assertFileBlocksReconstruction("/testRecoverAllDataBlocks", fileLen, TestReconstructStripedFile.ReconstructionType.DataOnly, parityBlkNum);
    }

    @Test(timeout = 120000)
    public void testRecoverAllDataBlocks1() throws Exception {
        int fileLen = ((parityBlkNum) * (blockSize)) + ((blockSize) / 10);
        assertFileBlocksReconstruction("/testRecoverAllDataBlocks1", fileLen, TestReconstructStripedFile.ReconstructionType.DataOnly, parityBlkNum);
    }

    @Test(timeout = 120000)
    public void testRecoverOneDataBlock() throws Exception {
        int fileLen = (((dataBlkNum) + 1) * (blockSize)) + ((blockSize) / 10);
        assertFileBlocksReconstruction("/testRecoverOneDataBlock", fileLen, TestReconstructStripedFile.ReconstructionType.DataOnly, 1);
    }

    @Test(timeout = 120000)
    public void testRecoverOneDataBlock1() throws Exception {
        int fileLen = (cellSize) + ((cellSize) / 10);
        assertFileBlocksReconstruction("/testRecoverOneDataBlock1", fileLen, TestReconstructStripedFile.ReconstructionType.DataOnly, 1);
    }

    @Test(timeout = 120000)
    public void testRecoverOneDataBlock2() throws Exception {
        int fileLen = 1;
        assertFileBlocksReconstruction("/testRecoverOneDataBlock2", fileLen, TestReconstructStripedFile.ReconstructionType.DataOnly, 1);
    }

    @Test(timeout = 120000)
    public void testRecoverAnyBlocks() throws Exception {
        int fileLen = ((parityBlkNum) * (blockSize)) + ((blockSize) / 10);
        assertFileBlocksReconstruction("/testRecoverAnyBlocks", fileLen, TestReconstructStripedFile.ReconstructionType.Any, ((random.nextInt(parityBlkNum)) + 1));
    }

    @Test(timeout = 120000)
    public void testRecoverAnyBlocks1() throws Exception {
        int fileLen = (((dataBlkNum) + (parityBlkNum)) * (blockSize)) + ((blockSize) / 10);
        assertFileBlocksReconstruction("/testRecoverAnyBlocks1", fileLen, TestReconstructStripedFile.ReconstructionType.Any, ((random.nextInt(parityBlkNum)) + 1));
    }

    /* Tests that processErasureCodingTasks should not throw exceptions out due to
    invalid ECTask submission.
     */
    @Test
    public void testProcessErasureCodingTasksSubmitionShouldSucceed() throws Exception {
        DataNode dataNode = cluster.dataNodes.get(0).datanode;
        // Pack invalid(dummy) parameters in ecTasks. Irrespective of parameters, each task
        // thread pool submission should succeed, so that it will not prevent
        // processing other tasks in the list if any exceptions.
        int size = cluster.dataNodes.size();
        byte[] liveIndices = new byte[size];
        DatanodeInfo[] dataDNs = new DatanodeInfo[size + 1];
        DatanodeStorageInfo targetDnInfos_1 = BlockManagerTestUtil.newDatanodeStorageInfo(DFSTestUtil.getLocalDatanodeDescriptor(), new DatanodeStorage("s01"));
        DatanodeStorageInfo[] dnStorageInfo = new DatanodeStorageInfo[]{ targetDnInfos_1 };
        BlockECReconstructionInfo invalidECInfo = new BlockECReconstructionInfo(new ExtendedBlock("bp-id", 123456), dataDNs, dnStorageInfo, liveIndices, ecPolicy);
        List<BlockECReconstructionInfo> ecTasks = new ArrayList<>();
        ecTasks.add(invalidECInfo);
        dataNode.getErasureCodingWorker().processErasureCodingTasks(ecTasks);
    }

    // HDFS-12044
    @Test(timeout = 120000)
    public void testNNSendsErasureCodingTasks() throws Exception {
        testNNSendsErasureCodingTasks(1);
        testNNSendsErasureCodingTasks(2);
    }

    @Test(timeout = 180000)
    public void testErasureCodingWorkerXmitsWeight() throws Exception {
        testErasureCodingWorkerXmitsWeight(1.0F, ecPolicy.getNumDataUnits());
        testErasureCodingWorkerXmitsWeight(0.0F, 1);
        testErasureCodingWorkerXmitsWeight(10.0F, (10 * (ecPolicy.getNumDataUnits())));
    }
}

