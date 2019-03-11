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
package org.apache.hadoop.hdfs.server.datanode;


import BlockManager.blockLog;
import BlockStatus.RECEIVED_BLOCK;
import DataNode.LOG;
import NameNode.blockStateChangeLog;
import NameNode.stateChangeLog;
import java.io.IOException;
import java.util.UUID;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hdfs.DFSClient;
import org.apache.hadoop.hdfs.DFSTestUtil;
import org.apache.hadoop.hdfs.DistributedFileSystem;
import org.apache.hadoop.hdfs.MiniDFSCluster;
import org.apache.hadoop.hdfs.server.blockmanagement.DatanodeStorageInfo;
import org.apache.hadoop.hdfs.server.namenode.FSNamesystem;
import org.apache.hadoop.hdfs.server.protocol.DatanodeStorage;
import org.apache.hadoop.test.GenericTestUtils;
import org.apache.hadoop.test.MetricsAsserts;
import org.hamcrest.core.Is;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.event.Level;


/**
 * This test verifies that incremental block reports from a single DataNode are
 * correctly handled by NN. Tests the following variations:
 *  #1 - Incremental BRs from all storages combined in a single call.
 *  #2 - Incremental BRs from separate storages sent in separate calls.
 *  #3 - Incremental BR from an unknown storage should be rejected.
 *
 *  We also verify that the DataNode is not splitting the reports (it may do so
 *  in the future).
 */
public class TestIncrementalBrVariations {
    public static final Logger LOG = LoggerFactory.getLogger(TestIncrementalBrVariations.class);

    private static final short NUM_DATANODES = 1;

    static final int BLOCK_SIZE = 1024;

    static final int NUM_BLOCKS = 10;

    private static final long seed = 4207869677L;

    private static final String NN_METRICS = "NameNodeActivity";

    private MiniDFSCluster cluster;

    private DistributedFileSystem fs;

    private DFSClient client;

    private static Configuration conf;

    private String poolId;

    private DataNode dn0;// DataNode at index0 in the MiniDFSCluster


    private DatanodeRegistration dn0Reg;// DataNodeRegistration for dn0


    static {
        GenericTestUtils.setLogLevel(stateChangeLog, Level.TRACE);
        GenericTestUtils.setLogLevel(blockLog, Level.TRACE);
        GenericTestUtils.setLogLevel(blockStateChangeLog, Level.TRACE);
        GenericTestUtils.setLogLevel(LoggerFactory.getLogger(FSNamesystem.class), Level.TRACE);
        GenericTestUtils.setLogLevel(DataNode.LOG, Level.TRACE);
        GenericTestUtils.setLogLevel(TestIncrementalBrVariations.LOG, Level.TRACE);
    }

    /**
     * Incremental BRs from all storages combined in a single message.
     */
    @Test
    public void testCombinedIncrementalBlockReport() throws IOException {
        verifyIncrementalBlockReports(false);
    }

    /**
     * One incremental BR per storage.
     */
    @Test
    public void testSplitIncrementalBlockReport() throws IOException {
        verifyIncrementalBlockReports(true);
    }

    /**
     * Verify that the DataNode sends a single incremental block report for all
     * storages.
     *
     * @throws IOException
     * 		
     * @throws InterruptedException
     * 		
     */
    @Test(timeout = 60000)
    public void testDataNodeDoesNotSplitReports() throws IOException, InterruptedException {
        LocatedBlocks blocks = createFileGetBlocks(GenericTestUtils.getMethodName());
        Assert.assertThat(cluster.getDataNodes().size(), Is.is(1));
        // Remove all blocks from the DataNode.
        for (LocatedBlock block : blocks.getLocatedBlocks()) {
            dn0.notifyNamenodeDeletedBlock(block.getBlock(), block.getStorageIDs()[0]);
        }
        TestIncrementalBrVariations.LOG.info("Triggering report after deleting blocks");
        long ops = MetricsAsserts.getLongCounter("BlockReceivedAndDeletedOps", MetricsAsserts.getMetrics(TestIncrementalBrVariations.NN_METRICS));
        // Trigger a report to the NameNode and give it a few seconds.
        DataNodeTestUtils.triggerBlockReport(dn0);
        Thread.sleep(5000);
        // Ensure that NameNodeRpcServer.blockReceivedAndDeletes is invoked
        // exactly once after we triggered the report.
        MetricsAsserts.assertCounter("BlockReceivedAndDeletedOps", (ops + 1), MetricsAsserts.getMetrics(TestIncrementalBrVariations.NN_METRICS));
    }

    /**
     * Verify that the NameNode can learn about new storages from incremental
     * block reports.
     * This tests the fix for the error condition seen in HDFS-6904.
     *
     * @throws IOException
     * 		
     * @throws InterruptedException
     * 		
     */
    @Test(timeout = 60000)
    public void testNnLearnsNewStorages() throws IOException, InterruptedException {
        // Generate a report for a fake block on a fake storage.
        final String newStorageUuid = UUID.randomUUID().toString();
        final DatanodeStorage newStorage = new DatanodeStorage(newStorageUuid);
        StorageReceivedDeletedBlocks[] reports = DFSTestUtil.makeReportForReceivedBlock(TestIncrementalBrVariations.getDummyBlock(), RECEIVED_BLOCK, newStorage);
        // Send the report to the NN.
        cluster.getNameNodeRpc().blockReceivedAndDeleted(dn0Reg, poolId, reports);
        // IBRs are async, make sure the NN processes all of them.
        cluster.getNamesystem().getBlockManager().flushBlockOps();
        // Make sure that the NN has learned of the new storage.
        DatanodeStorageInfo storageInfo = cluster.getNameNode().getNamesystem().getBlockManager().getDatanodeManager().getDatanode(dn0.getDatanodeId()).getStorageInfo(newStorageUuid);
        Assert.assertNotNull(storageInfo);
    }
}

