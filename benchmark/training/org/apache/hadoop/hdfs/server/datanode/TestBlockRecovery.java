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


import DFSConfigKeys.DFS_DATANODE_XCEIVER_STOP_TIMEOUT_MILLIS_KEY;
import DataChecksum.Type.CRC32;
import DatanodeID.EMPTY_ARRAY;
import FSNamesystem.LOG;
import StorageType.DEFAULT;
import com.google.common.base.Supplier;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hdfs.AppendTestUtil;
import org.apache.hadoop.hdfs.DFSClient;
import org.apache.hadoop.hdfs.DFSConfigKeys;
import org.apache.hadoop.hdfs.DFSTestUtil;
import org.apache.hadoop.hdfs.DistributedFileSystem;
import org.apache.hadoop.hdfs.HdfsConfiguration;
import org.apache.hadoop.hdfs.MiniDFSCluster;
import org.apache.hadoop.hdfs.StripedFileTestUtil;
import org.apache.hadoop.hdfs.protocol.DatanodeID;
import org.apache.hadoop.hdfs.protocol.DatanodeInfo;
import org.apache.hadoop.hdfs.protocol.ErasureCodingPolicy;
import org.apache.hadoop.hdfs.protocol.ExtendedBlock;
import org.apache.hadoop.hdfs.protocol.LocatedBlock;
import org.apache.hadoop.hdfs.protocol.RecoveryInProgressException;
import org.apache.hadoop.hdfs.server.common.HdfsServerConstants.ReplicaState;
import org.apache.hadoop.hdfs.server.datanode.BlockRecoveryWorker.BlockRecord;
import org.apache.hadoop.hdfs.server.datanode.fsdataset.ReplicaOutputStreams;
import org.apache.hadoop.hdfs.server.namenode.FSNamesystem;
import org.apache.hadoop.hdfs.server.protocol.BlockRecoveryCommand.RecoveringBlock;
import org.apache.hadoop.hdfs.server.protocol.BlockRecoveryCommand.RecoveringStripedBlock;
import org.apache.hadoop.hdfs.server.protocol.DatanodeProtocol;
import org.apache.hadoop.hdfs.server.protocol.InterDatanodeProtocol;
import org.apache.hadoop.hdfs.server.protocol.ReplicaRecoveryInfo;
import org.apache.hadoop.test.GenericTestUtils;
import org.apache.hadoop.test.GenericTestUtils.SleepAnswer;
import org.apache.hadoop.util.AutoCloseableLock;
import org.apache.hadoop.util.DataChecksum;
import org.apache.hadoop.util.Time;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.event.Level;


/**
 * This tests if sync all replicas in block recovery works correctly.
 */
public class TestBlockRecovery {
    private static final Logger LOG = LoggerFactory.getLogger(TestBlockRecovery.class);

    private static final String DATA_DIR = (MiniDFSCluster.getBaseDirectory()) + "data";

    private DataNode dn;

    private DataNode spyDN;

    private BlockRecoveryWorker recoveryWorker;

    private Configuration conf;

    private boolean tearDownDone;

    private static final long RECOVERY_ID = 3000L;

    private static final String CLUSTER_ID = "testClusterID";

    private static final String POOL_ID = "BP-TEST";

    private static final InetSocketAddress NN_ADDR = new InetSocketAddress("localhost", 5020);

    private static final long BLOCK_ID = 1000L;

    private static final long GEN_STAMP = 2000L;

    private static final long BLOCK_LEN = 3000L;

    private static final long REPLICA_LEN1 = 6000L;

    private static final long REPLICA_LEN2 = 5000L;

    private static final ExtendedBlock block = new ExtendedBlock(TestBlockRecovery.POOL_ID, TestBlockRecovery.BLOCK_ID, TestBlockRecovery.BLOCK_LEN, TestBlockRecovery.GEN_STAMP);

    @Rule
    public TestName currentTestName = new TestName();

    private final int cellSize = StripedFileTestUtil.getDefaultECPolicy().getCellSize();

    private final int bytesPerChecksum = 512;

    private final int[][][] blockLengthsSuite = new int[][][]{ new int[][]{ new int[]{ 11 * (cellSize), 10 * (cellSize), 9 * (cellSize), 8 * (cellSize), 7 * (cellSize), 6 * (cellSize), 5 * (cellSize), 4 * (cellSize), 3 * (cellSize) }, new int[]{ 36 * (cellSize) } }, new int[][]{ new int[]{ 3 * (cellSize), 4 * (cellSize), 5 * (cellSize), 6 * (cellSize), 7 * (cellSize), 8 * (cellSize), 9 * (cellSize), 10 * (cellSize), 11 * (cellSize) }, new int[]{ 36 * (cellSize) } }, new int[][]{ new int[]{ 11 * (cellSize), 7 * (cellSize), 6 * (cellSize), 5 * (cellSize), 4 * (cellSize), 2 * (cellSize), 9 * (cellSize), 10 * (cellSize), 11 * (cellSize) }, new int[]{ 36 * (cellSize) } }, new int[][]{ new int[]{ (8 * (cellSize)) + (bytesPerChecksum), (7 * (cellSize)) + ((bytesPerChecksum) * 2), (6 * (cellSize)) + ((bytesPerChecksum) * 2), (5 * (cellSize)) - ((bytesPerChecksum) * 3), (4 * (cellSize)) - ((bytesPerChecksum) * 4), (3 * (cellSize)) - ((bytesPerChecksum) * 4), 9 * (cellSize), 10 * (cellSize), 11 * (cellSize) }, new int[]{ 36 * (cellSize) } } };

    static {
        GenericTestUtils.setLogLevel(FSNamesystem.LOG, Level.TRACE);
        GenericTestUtils.setLogLevel(TestBlockRecovery.LOG, Level.TRACE);
    }

    private final long TEST_STOP_WORKER_XCEIVER_STOP_TIMEOUT_MILLIS = 1000000000L;

    /**
     * BlockRecovery_02.8.
     * Two replicas are in Finalized state
     *
     * @throws IOException
     * 		in case of an error
     */
    @Test(timeout = 60000)
    public void testFinalizedReplicas() throws IOException {
        if (TestBlockRecovery.LOG.isDebugEnabled()) {
            TestBlockRecovery.LOG.debug(("Running " + (GenericTestUtils.getMethodName())));
        }
        ReplicaRecoveryInfo replica1 = new ReplicaRecoveryInfo(TestBlockRecovery.BLOCK_ID, TestBlockRecovery.REPLICA_LEN1, ((TestBlockRecovery.GEN_STAMP) - 1), ReplicaState.FINALIZED);
        ReplicaRecoveryInfo replica2 = new ReplicaRecoveryInfo(TestBlockRecovery.BLOCK_ID, TestBlockRecovery.REPLICA_LEN1, ((TestBlockRecovery.GEN_STAMP) - 2), ReplicaState.FINALIZED);
        InterDatanodeProtocol dn1 = Mockito.mock(InterDatanodeProtocol.class);
        InterDatanodeProtocol dn2 = Mockito.mock(InterDatanodeProtocol.class);
        testSyncReplicas(replica1, replica2, dn1, dn2, TestBlockRecovery.REPLICA_LEN1);
        Mockito.verify(dn1).updateReplicaUnderRecovery(TestBlockRecovery.block, TestBlockRecovery.RECOVERY_ID, TestBlockRecovery.BLOCK_ID, TestBlockRecovery.REPLICA_LEN1);
        Mockito.verify(dn2).updateReplicaUnderRecovery(TestBlockRecovery.block, TestBlockRecovery.RECOVERY_ID, TestBlockRecovery.BLOCK_ID, TestBlockRecovery.REPLICA_LEN1);
        // two finalized replicas have different length
        replica1 = new ReplicaRecoveryInfo(TestBlockRecovery.BLOCK_ID, TestBlockRecovery.REPLICA_LEN1, ((TestBlockRecovery.GEN_STAMP) - 1), ReplicaState.FINALIZED);
        replica2 = new ReplicaRecoveryInfo(TestBlockRecovery.BLOCK_ID, TestBlockRecovery.REPLICA_LEN2, ((TestBlockRecovery.GEN_STAMP) - 2), ReplicaState.FINALIZED);
        try {
            testSyncReplicas(replica1, replica2, dn1, dn2, TestBlockRecovery.REPLICA_LEN1);
            Assert.fail("Two finalized replicas should not have different lengthes!");
        } catch (IOException e) {
            Assert.assertTrue(e.getMessage().startsWith("Inconsistent size of finalized replicas. "));
        }
    }

    /**
     * BlockRecovery_02.9.
     * One replica is Finalized and another is RBW.
     *
     * @throws IOException
     * 		in case of an error
     */
    @Test(timeout = 60000)
    public void testFinalizedRbwReplicas() throws IOException {
        if (TestBlockRecovery.LOG.isDebugEnabled()) {
            TestBlockRecovery.LOG.debug(("Running " + (GenericTestUtils.getMethodName())));
        }
        // rbw and finalized replicas have the same length
        ReplicaRecoveryInfo replica1 = new ReplicaRecoveryInfo(TestBlockRecovery.BLOCK_ID, TestBlockRecovery.REPLICA_LEN1, ((TestBlockRecovery.GEN_STAMP) - 1), ReplicaState.FINALIZED);
        ReplicaRecoveryInfo replica2 = new ReplicaRecoveryInfo(TestBlockRecovery.BLOCK_ID, TestBlockRecovery.REPLICA_LEN1, ((TestBlockRecovery.GEN_STAMP) - 2), ReplicaState.RBW);
        InterDatanodeProtocol dn1 = Mockito.mock(InterDatanodeProtocol.class);
        InterDatanodeProtocol dn2 = Mockito.mock(InterDatanodeProtocol.class);
        testSyncReplicas(replica1, replica2, dn1, dn2, TestBlockRecovery.REPLICA_LEN1);
        Mockito.verify(dn1).updateReplicaUnderRecovery(TestBlockRecovery.block, TestBlockRecovery.RECOVERY_ID, TestBlockRecovery.BLOCK_ID, TestBlockRecovery.REPLICA_LEN1);
        Mockito.verify(dn2).updateReplicaUnderRecovery(TestBlockRecovery.block, TestBlockRecovery.RECOVERY_ID, TestBlockRecovery.BLOCK_ID, TestBlockRecovery.REPLICA_LEN1);
        // rbw replica has a different length from the finalized one
        replica1 = new ReplicaRecoveryInfo(TestBlockRecovery.BLOCK_ID, TestBlockRecovery.REPLICA_LEN1, ((TestBlockRecovery.GEN_STAMP) - 1), ReplicaState.FINALIZED);
        replica2 = new ReplicaRecoveryInfo(TestBlockRecovery.BLOCK_ID, TestBlockRecovery.REPLICA_LEN2, ((TestBlockRecovery.GEN_STAMP) - 2), ReplicaState.RBW);
        dn1 = Mockito.mock(InterDatanodeProtocol.class);
        dn2 = Mockito.mock(InterDatanodeProtocol.class);
        testSyncReplicas(replica1, replica2, dn1, dn2, TestBlockRecovery.REPLICA_LEN1);
        Mockito.verify(dn1).updateReplicaUnderRecovery(TestBlockRecovery.block, TestBlockRecovery.RECOVERY_ID, TestBlockRecovery.BLOCK_ID, TestBlockRecovery.REPLICA_LEN1);
        Mockito.verify(dn2, Mockito.never()).updateReplicaUnderRecovery(TestBlockRecovery.block, TestBlockRecovery.RECOVERY_ID, TestBlockRecovery.BLOCK_ID, TestBlockRecovery.REPLICA_LEN1);
    }

    /**
     * BlockRecovery_02.10.
     * One replica is Finalized and another is RWR.
     *
     * @throws IOException
     * 		in case of an error
     */
    @Test(timeout = 60000)
    public void testFinalizedRwrReplicas() throws IOException {
        if (TestBlockRecovery.LOG.isDebugEnabled()) {
            TestBlockRecovery.LOG.debug(("Running " + (GenericTestUtils.getMethodName())));
        }
        // rbw and finalized replicas have the same length
        ReplicaRecoveryInfo replica1 = new ReplicaRecoveryInfo(TestBlockRecovery.BLOCK_ID, TestBlockRecovery.REPLICA_LEN1, ((TestBlockRecovery.GEN_STAMP) - 1), ReplicaState.FINALIZED);
        ReplicaRecoveryInfo replica2 = new ReplicaRecoveryInfo(TestBlockRecovery.BLOCK_ID, TestBlockRecovery.REPLICA_LEN1, ((TestBlockRecovery.GEN_STAMP) - 2), ReplicaState.RWR);
        InterDatanodeProtocol dn1 = Mockito.mock(InterDatanodeProtocol.class);
        InterDatanodeProtocol dn2 = Mockito.mock(InterDatanodeProtocol.class);
        testSyncReplicas(replica1, replica2, dn1, dn2, TestBlockRecovery.REPLICA_LEN1);
        Mockito.verify(dn1).updateReplicaUnderRecovery(TestBlockRecovery.block, TestBlockRecovery.RECOVERY_ID, TestBlockRecovery.BLOCK_ID, TestBlockRecovery.REPLICA_LEN1);
        Mockito.verify(dn2, Mockito.never()).updateReplicaUnderRecovery(TestBlockRecovery.block, TestBlockRecovery.RECOVERY_ID, TestBlockRecovery.BLOCK_ID, TestBlockRecovery.REPLICA_LEN1);
        // rbw replica has a different length from the finalized one
        replica1 = new ReplicaRecoveryInfo(TestBlockRecovery.BLOCK_ID, TestBlockRecovery.REPLICA_LEN1, ((TestBlockRecovery.GEN_STAMP) - 1), ReplicaState.FINALIZED);
        replica2 = new ReplicaRecoveryInfo(TestBlockRecovery.BLOCK_ID, TestBlockRecovery.REPLICA_LEN2, ((TestBlockRecovery.GEN_STAMP) - 2), ReplicaState.RBW);
        dn1 = Mockito.mock(InterDatanodeProtocol.class);
        dn2 = Mockito.mock(InterDatanodeProtocol.class);
        testSyncReplicas(replica1, replica2, dn1, dn2, TestBlockRecovery.REPLICA_LEN1);
        Mockito.verify(dn1).updateReplicaUnderRecovery(TestBlockRecovery.block, TestBlockRecovery.RECOVERY_ID, TestBlockRecovery.BLOCK_ID, TestBlockRecovery.REPLICA_LEN1);
        Mockito.verify(dn2, Mockito.never()).updateReplicaUnderRecovery(TestBlockRecovery.block, TestBlockRecovery.RECOVERY_ID, TestBlockRecovery.BLOCK_ID, TestBlockRecovery.REPLICA_LEN1);
    }

    /**
     * BlockRecovery_02.11.
     * Two replicas are RBW.
     *
     * @throws IOException
     * 		in case of an error
     */
    @Test(timeout = 60000)
    public void testRBWReplicas() throws IOException {
        if (TestBlockRecovery.LOG.isDebugEnabled()) {
            TestBlockRecovery.LOG.debug(("Running " + (GenericTestUtils.getMethodName())));
        }
        ReplicaRecoveryInfo replica1 = new ReplicaRecoveryInfo(TestBlockRecovery.BLOCK_ID, TestBlockRecovery.REPLICA_LEN1, ((TestBlockRecovery.GEN_STAMP) - 1), ReplicaState.RBW);
        ReplicaRecoveryInfo replica2 = new ReplicaRecoveryInfo(TestBlockRecovery.BLOCK_ID, TestBlockRecovery.REPLICA_LEN2, ((TestBlockRecovery.GEN_STAMP) - 2), ReplicaState.RBW);
        InterDatanodeProtocol dn1 = Mockito.mock(InterDatanodeProtocol.class);
        InterDatanodeProtocol dn2 = Mockito.mock(InterDatanodeProtocol.class);
        long minLen = Math.min(TestBlockRecovery.REPLICA_LEN1, TestBlockRecovery.REPLICA_LEN2);
        testSyncReplicas(replica1, replica2, dn1, dn2, minLen);
        Mockito.verify(dn1).updateReplicaUnderRecovery(TestBlockRecovery.block, TestBlockRecovery.RECOVERY_ID, TestBlockRecovery.BLOCK_ID, minLen);
        Mockito.verify(dn2).updateReplicaUnderRecovery(TestBlockRecovery.block, TestBlockRecovery.RECOVERY_ID, TestBlockRecovery.BLOCK_ID, minLen);
    }

    /**
     * BlockRecovery_02.12.
     * One replica is RBW and another is RWR.
     *
     * @throws IOException
     * 		in case of an error
     */
    @Test(timeout = 60000)
    public void testRBW_RWRReplicas() throws IOException {
        if (TestBlockRecovery.LOG.isDebugEnabled()) {
            TestBlockRecovery.LOG.debug(("Running " + (GenericTestUtils.getMethodName())));
        }
        ReplicaRecoveryInfo replica1 = new ReplicaRecoveryInfo(TestBlockRecovery.BLOCK_ID, TestBlockRecovery.REPLICA_LEN1, ((TestBlockRecovery.GEN_STAMP) - 1), ReplicaState.RBW);
        ReplicaRecoveryInfo replica2 = new ReplicaRecoveryInfo(TestBlockRecovery.BLOCK_ID, TestBlockRecovery.REPLICA_LEN1, ((TestBlockRecovery.GEN_STAMP) - 2), ReplicaState.RWR);
        InterDatanodeProtocol dn1 = Mockito.mock(InterDatanodeProtocol.class);
        InterDatanodeProtocol dn2 = Mockito.mock(InterDatanodeProtocol.class);
        testSyncReplicas(replica1, replica2, dn1, dn2, TestBlockRecovery.REPLICA_LEN1);
        Mockito.verify(dn1).updateReplicaUnderRecovery(TestBlockRecovery.block, TestBlockRecovery.RECOVERY_ID, TestBlockRecovery.BLOCK_ID, TestBlockRecovery.REPLICA_LEN1);
        Mockito.verify(dn2, Mockito.never()).updateReplicaUnderRecovery(TestBlockRecovery.block, TestBlockRecovery.RECOVERY_ID, TestBlockRecovery.BLOCK_ID, TestBlockRecovery.REPLICA_LEN1);
    }

    /**
     * BlockRecovery_02.13.
     * Two replicas are RWR.
     *
     * @throws IOException
     * 		in case of an error
     */
    @Test(timeout = 60000)
    public void testRWRReplicas() throws IOException {
        if (TestBlockRecovery.LOG.isDebugEnabled()) {
            TestBlockRecovery.LOG.debug(("Running " + (GenericTestUtils.getMethodName())));
        }
        ReplicaRecoveryInfo replica1 = new ReplicaRecoveryInfo(TestBlockRecovery.BLOCK_ID, TestBlockRecovery.REPLICA_LEN1, ((TestBlockRecovery.GEN_STAMP) - 1), ReplicaState.RWR);
        ReplicaRecoveryInfo replica2 = new ReplicaRecoveryInfo(TestBlockRecovery.BLOCK_ID, TestBlockRecovery.REPLICA_LEN2, ((TestBlockRecovery.GEN_STAMP) - 2), ReplicaState.RWR);
        InterDatanodeProtocol dn1 = Mockito.mock(InterDatanodeProtocol.class);
        InterDatanodeProtocol dn2 = Mockito.mock(InterDatanodeProtocol.class);
        long minLen = Math.min(TestBlockRecovery.REPLICA_LEN1, TestBlockRecovery.REPLICA_LEN2);
        testSyncReplicas(replica1, replica2, dn1, dn2, minLen);
        Mockito.verify(dn1).updateReplicaUnderRecovery(TestBlockRecovery.block, TestBlockRecovery.RECOVERY_ID, TestBlockRecovery.BLOCK_ID, minLen);
        Mockito.verify(dn2).updateReplicaUnderRecovery(TestBlockRecovery.block, TestBlockRecovery.RECOVERY_ID, TestBlockRecovery.BLOCK_ID, minLen);
    }

    /**
     * BlockRecoveryFI_05. One DN throws RecoveryInProgressException.
     *
     * @throws IOException
     * 		in case of an error
     */
    @Test(timeout = 60000)
    public void testRecoveryInProgressException() throws IOException, InterruptedException {
        if (TestBlockRecovery.LOG.isDebugEnabled()) {
            TestBlockRecovery.LOG.debug(("Running " + (GenericTestUtils.getMethodName())));
        }
        Mockito.doThrow(new RecoveryInProgressException("Replica recovery is in progress")).when(spyDN).initReplicaRecovery(ArgumentMatchers.any(RecoveringBlock.class));
        for (RecoveringBlock rBlock : initRecoveringBlocks()) {
            BlockRecoveryWorker.RecoveryTaskContiguous RecoveryTaskContiguous = recoveryWorker.new RecoveryTaskContiguous(rBlock);
            BlockRecoveryWorker.RecoveryTaskContiguous spyTask = Mockito.spy(RecoveryTaskContiguous);
            spyTask.recover();
            Mockito.verify(spyTask, Mockito.never()).syncBlock(ArgumentMatchers.anyList());
        }
    }

    /**
     * BlockRecoveryFI_06. all datanodes throws an exception.
     *
     * @throws IOException
     * 		in case of an error
     */
    @Test(timeout = 60000)
    public void testErrorReplicas() throws IOException, InterruptedException {
        if (TestBlockRecovery.LOG.isDebugEnabled()) {
            TestBlockRecovery.LOG.debug(("Running " + (GenericTestUtils.getMethodName())));
        }
        Mockito.doThrow(new IOException()).when(spyDN).initReplicaRecovery(ArgumentMatchers.any(RecoveringBlock.class));
        for (RecoveringBlock rBlock : initRecoveringBlocks()) {
            BlockRecoveryWorker.RecoveryTaskContiguous RecoveryTaskContiguous = recoveryWorker.new RecoveryTaskContiguous(rBlock);
            BlockRecoveryWorker.RecoveryTaskContiguous spyTask = Mockito.spy(RecoveryTaskContiguous);
            try {
                spyTask.recover();
                Assert.fail();
            } catch (IOException e) {
                GenericTestUtils.assertExceptionContains("All datanodes failed", e);
            }
            Mockito.verify(spyTask, Mockito.never()).syncBlock(ArgumentMatchers.anyList());
        }
    }

    /**
     * BlockRecoveryFI_07. max replica length from all DNs is zero.
     *
     * @throws IOException
     * 		in case of an error
     */
    @Test(timeout = 60000)
    public void testZeroLenReplicas() throws IOException, InterruptedException {
        if (TestBlockRecovery.LOG.isDebugEnabled()) {
            TestBlockRecovery.LOG.debug(("Running " + (GenericTestUtils.getMethodName())));
        }
        Mockito.doReturn(new ReplicaRecoveryInfo(TestBlockRecovery.block.getBlockId(), 0, TestBlockRecovery.block.getGenerationStamp(), ReplicaState.FINALIZED)).when(spyDN).initReplicaRecovery(ArgumentMatchers.any(RecoveringBlock.class));
        for (RecoveringBlock rBlock : initRecoveringBlocks()) {
            BlockRecoveryWorker.RecoveryTaskContiguous RecoveryTaskContiguous = recoveryWorker.new RecoveryTaskContiguous(rBlock);
            BlockRecoveryWorker.RecoveryTaskContiguous spyTask = Mockito.spy(RecoveryTaskContiguous);
            spyTask.recover();
        }
        DatanodeProtocol dnP = recoveryWorker.getActiveNamenodeForBP(TestBlockRecovery.POOL_ID);
        Mockito.verify(dnP).commitBlockSynchronization(TestBlockRecovery.block, TestBlockRecovery.RECOVERY_ID, 0, true, true, EMPTY_ARRAY, null);
    }

    private static final RecoveringBlock rBlock = new RecoveringBlock(TestBlockRecovery.block, null, TestBlockRecovery.RECOVERY_ID);

    /**
     * BlockRecoveryFI_09. some/all DNs failed to update replicas.
     *
     * @throws IOException
     * 		in case of an error
     */
    @Test(timeout = 60000)
    public void testFailedReplicaUpdate() throws IOException {
        if (TestBlockRecovery.LOG.isDebugEnabled()) {
            TestBlockRecovery.LOG.debug(("Running " + (GenericTestUtils.getMethodName())));
        }
        Mockito.doThrow(new IOException()).when(spyDN).updateReplicaUnderRecovery(TestBlockRecovery.block, TestBlockRecovery.RECOVERY_ID, TestBlockRecovery.BLOCK_ID, TestBlockRecovery.block.getNumBytes());
        try {
            BlockRecoveryWorker.RecoveryTaskContiguous RecoveryTaskContiguous = recoveryWorker.new RecoveryTaskContiguous(TestBlockRecovery.rBlock);
            RecoveryTaskContiguous.syncBlock(initBlockRecords(spyDN));
            Assert.fail("Sync should fail");
        } catch (IOException e) {
            e.getMessage().startsWith("Cannot recover ");
        }
    }

    /**
     * BlockRecoveryFI_10. DN has no ReplicaUnderRecovery.
     *
     * @throws IOException
     * 		in case of an error
     */
    @Test(timeout = 60000)
    public void testNoReplicaUnderRecovery() throws IOException {
        if (TestBlockRecovery.LOG.isDebugEnabled()) {
            TestBlockRecovery.LOG.debug(("Running " + (GenericTestUtils.getMethodName())));
        }
        dn.data.createRbw(DEFAULT, null, TestBlockRecovery.block, false);
        BlockRecoveryWorker.RecoveryTaskContiguous RecoveryTaskContiguous = recoveryWorker.new RecoveryTaskContiguous(TestBlockRecovery.rBlock);
        try {
            RecoveryTaskContiguous.syncBlock(initBlockRecords(dn));
            Assert.fail("Sync should fail");
        } catch (IOException e) {
            e.getMessage().startsWith("Cannot recover ");
        }
        DatanodeProtocol namenode = recoveryWorker.getActiveNamenodeForBP(TestBlockRecovery.POOL_ID);
        Mockito.verify(namenode, Mockito.never()).commitBlockSynchronization(ArgumentMatchers.any(ExtendedBlock.class), ArgumentMatchers.anyLong(), ArgumentMatchers.anyLong(), ArgumentMatchers.anyBoolean(), ArgumentMatchers.anyBoolean(), ArgumentMatchers.any(DatanodeID[].class), ArgumentMatchers.any(String[].class));
    }

    /**
     * BlockRecoveryFI_11. a replica's recovery id does not match new GS.
     *
     * @throws IOException
     * 		in case of an error
     */
    @Test(timeout = 60000)
    public void testNotMatchedReplicaID() throws IOException {
        if (TestBlockRecovery.LOG.isDebugEnabled()) {
            TestBlockRecovery.LOG.debug(("Running " + (GenericTestUtils.getMethodName())));
        }
        ReplicaInPipeline replicaInfo = dn.data.createRbw(DEFAULT, null, TestBlockRecovery.block, false).getReplica();
        ReplicaOutputStreams streams = null;
        try {
            streams = replicaInfo.createStreams(true, DataChecksum.newDataChecksum(CRC32, 512));
            streams.getChecksumOut().write('a');
            dn.data.initReplicaRecovery(new RecoveringBlock(TestBlockRecovery.block, null, ((TestBlockRecovery.RECOVERY_ID) + 1)));
            BlockRecoveryWorker.RecoveryTaskContiguous RecoveryTaskContiguous = recoveryWorker.new RecoveryTaskContiguous(TestBlockRecovery.rBlock);
            try {
                RecoveryTaskContiguous.syncBlock(initBlockRecords(dn));
                Assert.fail("Sync should fail");
            } catch (IOException e) {
                e.getMessage().startsWith("Cannot recover ");
            }
            DatanodeProtocol namenode = recoveryWorker.getActiveNamenodeForBP(TestBlockRecovery.POOL_ID);
            Mockito.verify(namenode, Mockito.never()).commitBlockSynchronization(ArgumentMatchers.any(ExtendedBlock.class), ArgumentMatchers.anyLong(), ArgumentMatchers.anyLong(), ArgumentMatchers.anyBoolean(), ArgumentMatchers.anyBoolean(), ArgumentMatchers.any(DatanodeID[].class), ArgumentMatchers.any(String[].class));
        } finally {
            streams.close();
        }
    }

    /**
     * Test to verify the race between finalizeBlock and Lease recovery
     *
     * @throws Exception
     * 		
     */
    @Test(timeout = 20000)
    public void testRaceBetweenReplicaRecoveryAndFinalizeBlock() throws Exception {
        tearDown();// Stop the Mocked DN started in startup()

        Configuration conf = new HdfsConfiguration();
        conf.set(DFS_DATANODE_XCEIVER_STOP_TIMEOUT_MILLIS_KEY, "1000");
        MiniDFSCluster cluster = new MiniDFSCluster.Builder(conf).numDataNodes(1).build();
        try {
            cluster.waitClusterUp();
            DistributedFileSystem fs = cluster.getFileSystem();
            Path path = new Path("/test");
            FSDataOutputStream out = fs.create(path);
            out.writeBytes("data");
            out.hsync();
            List<LocatedBlock> blocks = DFSTestUtil.getAllBlocks(fs.open(path));
            final LocatedBlock block = blocks.get(0);
            final DataNode dataNode = cluster.getDataNodes().get(0);
            final AtomicBoolean recoveryInitResult = new AtomicBoolean(true);
            Thread recoveryThread = new Thread() {
                @Override
                public void run() {
                    try {
                        DatanodeInfo[] locations = block.getLocations();
                        final RecoveringBlock recoveringBlock = new RecoveringBlock(block.getBlock(), locations, ((block.getBlock().getGenerationStamp()) + 1));
                        try (AutoCloseableLock lock = dataNode.data.acquireDatasetLock()) {
                            Thread.sleep(2000);
                            dataNode.initReplicaRecovery(recoveringBlock);
                        }
                    } catch (Exception e) {
                        recoveryInitResult.set(false);
                    }
                }
            };
            recoveryThread.start();
            try {
                out.close();
            } catch (IOException e) {
                Assert.assertTrue("Writing should fail", e.getMessage().contains("are bad. Aborting..."));
            } finally {
                recoveryThread.join();
            }
            Assert.assertTrue("Recovery should be initiated successfully", recoveryInitResult.get());
            dataNode.updateReplicaUnderRecovery(block.getBlock(), ((block.getBlock().getGenerationStamp()) + 1), block.getBlock().getBlockId(), block.getBlockSize());
        } finally {
            if (null != cluster) {
                cluster.shutdown();
                cluster = null;
            }
        }
    }

    /**
     * DNs report RUR instead of RBW, RWR or FINALIZED. Primary DN expected to
     * throw an exception.
     *
     * @throws Exception
     * 		
     */
    @Test(timeout = 60000)
    public void testRURReplicas() throws Exception {
        if (TestBlockRecovery.LOG.isDebugEnabled()) {
            TestBlockRecovery.LOG.debug(("Running " + (GenericTestUtils.getMethodName())));
        }
        Mockito.doReturn(new ReplicaRecoveryInfo(TestBlockRecovery.block.getBlockId(), TestBlockRecovery.block.getNumBytes(), TestBlockRecovery.block.getGenerationStamp(), ReplicaState.RUR)).when(spyDN).initReplicaRecovery(ArgumentMatchers.any(RecoveringBlock.class));
        boolean exceptionThrown = false;
        try {
            for (RecoveringBlock rBlock : initRecoveringBlocks()) {
                BlockRecoveryWorker.RecoveryTaskContiguous RecoveryTaskContiguous = recoveryWorker.new RecoveryTaskContiguous(rBlock);
                BlockRecoveryWorker.RecoveryTaskContiguous spyTask = Mockito.spy(RecoveryTaskContiguous);
                spyTask.recover();
            }
        } catch (IOException e) {
            // expect IOException to be thrown here
            e.printStackTrace();
            Assert.assertTrue(("Wrong exception was thrown: " + (e.getMessage())), e.getMessage().contains((("Found 1 replica(s) for block " + (TestBlockRecovery.block)) + " but none is in RWR or better state")));
            exceptionThrown = true;
        } finally {
            Assert.assertTrue(exceptionThrown);
        }
    }

    @Test(timeout = 60000)
    public void testSafeLength() throws Exception {
        // hard coded policy to work with hard coded test suite
        ErasureCodingPolicy ecPolicy = StripedFileTestUtil.getDefaultECPolicy();
        RecoveringStripedBlock rBlockStriped = new RecoveringStripedBlock(TestBlockRecovery.rBlock, new byte[9], ecPolicy);
        BlockRecoveryWorker recoveryWorker = new BlockRecoveryWorker(dn);
        BlockRecoveryWorker.RecoveryTaskStriped recoveryTask = recoveryWorker.new RecoveryTaskStriped(rBlockStriped);
        for (int i = 0; i < (blockLengthsSuite.length); i++) {
            int[] blockLengths = blockLengthsSuite[i][0];
            int safeLength = blockLengthsSuite[i][1][0];
            Map<Long, BlockRecord> syncList = new HashMap<>();
            for (int id = 0; id < (blockLengths.length); id++) {
                ReplicaRecoveryInfo rInfo = new ReplicaRecoveryInfo(id, blockLengths[id], 0, null);
                syncList.put(((long) (id)), new BlockRecord(null, null, rInfo));
            }
            Assert.assertEquals((("BLOCK_LENGTHS_SUITE[" + i) + "]"), safeLength, recoveryTask.getSafeLength(syncList));
        }
    }

    private static class TestStopWorkerSemaphore {
        final Semaphore sem;

        final AtomicBoolean gotInterruption = new AtomicBoolean(false);

        TestStopWorkerSemaphore() {
            this.sem = new Semaphore(0);
        }

        /**
         * Attempt to acquire a sempahore within a given timeout.
         *
         * This is useful for unit tests where we need to ignore InterruptedException
         * when attempting to take a semaphore, but still want to honor the overall
         * test timeout.
         *
         * @param timeoutMs
         * 		The timeout in miliseconds.
         */
        private void uninterruptiblyAcquire(long timeoutMs) throws Exception {
            long startTimeMs = Time.monotonicNow();
            while (true) {
                long remTime = (startTimeMs + timeoutMs) - (Time.monotonicNow());
                if (remTime < 0) {
                    throw new RuntimeException((("Failed to acquire the semaphore within " + timeoutMs) + " milliseconds."));
                }
                try {
                    if (sem.tryAcquire(1, remTime, TimeUnit.MILLISECONDS)) {
                        return;
                    }
                } catch (InterruptedException e) {
                    gotInterruption.set(true);
                }
            } 
        }
    }

    private interface TestStopWorkerRunnable {
        /**
         * Return the name of the operation that this runnable performs.
         */
        String opName();

        /**
         * Perform the operation.
         */
        void run(RecoveringBlock recoveringBlock) throws Exception;
    }

    @Test(timeout = 90000)
    public void testInitReplicaRecoveryDoesNotHoldLock() throws Exception {
        testStopWorker(new TestBlockRecovery.TestStopWorkerRunnable() {
            @Override
            public String opName() {
                return "initReplicaRecovery";
            }

            @Override
            public void run(RecoveringBlock recoveringBlock) throws Exception {
                try {
                    spyDN.initReplicaRecovery(recoveringBlock);
                } catch (Exception e) {
                    if (!(e.getMessage().contains("meta does not exist"))) {
                        throw e;
                    }
                }
            }
        });
    }

    @Test(timeout = 90000)
    public void testRecoverAppendDoesNotHoldLock() throws Exception {
        testStopWorker(new TestBlockRecovery.TestStopWorkerRunnable() {
            @Override
            public String opName() {
                return "recoverAppend";
            }

            @Override
            public void run(RecoveringBlock recoveringBlock) throws Exception {
                try {
                    ExtendedBlock extBlock = recoveringBlock.getBlock();
                    spyDN.getFSDataset().recoverAppend(extBlock, ((extBlock.getGenerationStamp()) + 1), extBlock.getNumBytes());
                } catch (Exception e) {
                    if (!(e.getMessage().contains("Corrupted replica ReplicaBeingWritten"))) {
                        throw e;
                    }
                }
            }
        });
    }

    @Test(timeout = 90000)
    public void testRecoverCloseDoesNotHoldLock() throws Exception {
        testStopWorker(new TestBlockRecovery.TestStopWorkerRunnable() {
            @Override
            public String opName() {
                return "recoverClose";
            }

            @Override
            public void run(RecoveringBlock recoveringBlock) throws Exception {
                try {
                    ExtendedBlock extBlock = recoveringBlock.getBlock();
                    spyDN.getFSDataset().recoverClose(extBlock, ((extBlock.getGenerationStamp()) + 1), extBlock.getNumBytes());
                } catch (Exception e) {
                    if (!(e.getMessage().contains("Corrupted replica ReplicaBeingWritten"))) {
                        throw e;
                    }
                }
            }
        });
    }

    /**
     * Test for block recovery taking longer than the heartbeat interval.
     */
    @Test(timeout = 300000L)
    public void testRecoverySlowerThanHeartbeat() throws Exception {
        tearDown();// Stop the Mocked DN started in startup()

        SleepAnswer delayer = new SleepAnswer(3000, 6000);
        testRecoveryWithDatanodeDelayed(delayer);
    }

    /**
     * Test for block recovery timeout. All recovery attempts will be delayed
     * and the first attempt will be lost to trigger recovery timeout and retry.
     */
    @Test(timeout = 300000L)
    public void testRecoveryTimeout() throws Exception {
        tearDown();// Stop the Mocked DN started in startup()

        final Random r = new Random();
        // Make sure first commitBlockSynchronization call from the DN gets lost
        // for the recovery timeout to expire and new recovery attempt
        // to be started.
        SleepAnswer delayer = new SleepAnswer(3000) {
            private final AtomicBoolean callRealMethod = new AtomicBoolean();

            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                boolean interrupted = false;
                try {
                    Thread.sleep(((r.nextInt(3000)) + 6000));
                } catch (InterruptedException ie) {
                    interrupted = true;
                }
                try {
                    if (callRealMethod.get()) {
                        return invocation.callRealMethod();
                    }
                    callRealMethod.set(true);
                    return null;
                } finally {
                    if (interrupted) {
                        Thread.currentThread().interrupt();
                    }
                }
            }
        };
        testRecoveryWithDatanodeDelayed(delayer);
    }

    /**
     * Test that block will be recovered even if there are less than the
     * specified minReplication datanodes involved in its recovery.
     *
     * Check that, after recovering, the block will be successfully replicated.
     */
    @Test(timeout = 300000L)
    public void testRecoveryWillIgnoreMinReplication() throws Exception {
        tearDown();// Stop the Mocked DN started in startup()

        final int blockSize = 4096;
        final int numReplicas = 3;
        final String filename = "/testIgnoreMinReplication";
        final Path filePath = new Path(filename);
        Configuration configuration = new HdfsConfiguration();
        configuration.setInt(DFSConfigKeys.DFS_NAMENODE_HEARTBEAT_RECHECK_INTERVAL_KEY, 2000);
        configuration.setInt(DFSConfigKeys.DFS_NAMENODE_REPLICATION_MIN_KEY, 2);
        configuration.setLong(DFSConfigKeys.DFS_BLOCK_SIZE_KEY, blockSize);
        MiniDFSCluster cluster = null;
        try {
            cluster = new MiniDFSCluster.Builder(configuration).numDataNodes(5).build();
            cluster.waitActive();
            final DistributedFileSystem dfs = cluster.getFileSystem();
            final FSNamesystem fsn = cluster.getNamesystem();
            // Create a file and never close the output stream to trigger recovery
            FSDataOutputStream out = dfs.create(filePath, ((short) (numReplicas)));
            out.write(AppendTestUtil.randomBytes(0, blockSize));
            out.hsync();
            DFSClient dfsClient = new DFSClient(new InetSocketAddress("localhost", cluster.getNameNodePort()), configuration);
            LocatedBlock blk = dfsClient.getNamenode().getBlockLocations(filename, 0, blockSize).getLastLocatedBlock();
            // Kill 2 out of 3 datanodes so that only 1 alive, thus < minReplication
            List<DatanodeInfo> dataNodes = Arrays.asList(blk.getLocations());
            Assert.assertEquals(dataNodes.size(), numReplicas);
            for (DatanodeInfo dataNode : dataNodes.subList(0, (numReplicas - 1))) {
                cluster.stopDataNode(dataNode.getName());
            }
            GenericTestUtils.waitFor(new Supplier<Boolean>() {
                @Override
                public Boolean get() {
                    return (fsn.getNumDeadDataNodes()) == 2;
                }
            }, 300, 300000);
            // Make sure hard lease expires to trigger replica recovery
            cluster.setLeasePeriod(100L, 100L);
            // Wait for recovery to succeed
            GenericTestUtils.waitFor(new Supplier<Boolean>() {
                @Override
                public Boolean get() {
                    try {
                        return dfs.isFileClosed(filePath);
                    } catch (IOException e) {
                    }
                    return false;
                }
            }, 300, 300000);
            // Wait for the block to be replicated
            DFSTestUtil.waitForReplication(cluster, DFSTestUtil.getFirstBlock(dfs, filePath), 1, numReplicas, 0);
        } finally {
            if (cluster != null) {
                cluster.shutdown();
            }
        }
    }
}

