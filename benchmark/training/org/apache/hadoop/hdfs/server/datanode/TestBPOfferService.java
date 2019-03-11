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


import DataNode.LOG;
import DatanodeProtocol.INVALID_BLOCK;
import com.google.common.base.Supplier;
import java.io.File;
import java.io.IOException;
import java.net.ConnectException;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.ha.HAServiceProtocol.HAServiceState;
import org.apache.hadoop.hdfs.DFSConfigKeys;
import org.apache.hadoop.hdfs.protocol.Block;
import org.apache.hadoop.hdfs.protocol.ExtendedBlock;
import org.apache.hadoop.hdfs.protocol.LocatedBlock;
import org.apache.hadoop.hdfs.protocolPB.DatanodeProtocolClientSideTranslatorPB;
import org.apache.hadoop.hdfs.server.datanode.fsdataset.FsDatasetSpi;
import org.apache.hadoop.hdfs.server.datanode.metrics.DataNodeMetrics;
import org.apache.hadoop.hdfs.server.protocol.BlockReportContext;
import org.apache.hadoop.hdfs.server.protocol.DatanodeCommand;
import org.apache.hadoop.hdfs.server.protocol.DatanodeProtocol;
import org.apache.hadoop.hdfs.server.protocol.DatanodeRegistration;
import org.apache.hadoop.hdfs.server.protocol.DatanodeStorage;
import org.apache.hadoop.hdfs.server.protocol.HeartbeatResponse;
import org.apache.hadoop.hdfs.server.protocol.NNHAStatusHeartbeat;
import org.apache.hadoop.hdfs.server.protocol.NamespaceInfo;
import org.apache.hadoop.hdfs.server.protocol.ReceivedDeletedBlockInfo;
import org.apache.hadoop.hdfs.server.protocol.RegisterCommand;
import org.apache.hadoop.hdfs.server.protocol.SlowDiskReports;
import org.apache.hadoop.hdfs.server.protocol.SlowPeerReports;
import org.apache.hadoop.hdfs.server.protocol.StorageBlockReport;
import org.apache.hadoop.hdfs.server.protocol.StorageReceivedDeletedBlocks;
import org.apache.hadoop.hdfs.server.protocol.StorageReport;
import org.apache.hadoop.hdfs.server.protocol.VolumeFailureSummary;
import org.apache.hadoop.ipc.RemoteException;
import org.apache.hadoop.ipc.StandbyException;
import org.apache.hadoop.ipc.protobuf.RpcHeaderProtos.RpcResponseHeaderProto.RpcErrorCodeProto;
import org.apache.hadoop.test.GenericTestUtils;
import org.apache.hadoop.test.PathUtils;
import org.apache.hadoop.util.Time;
import org.apache.log4j.Level;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class TestBPOfferService {
    private static final String FAKE_BPID = "fake bpid";

    private static final String FAKE_CLUSTERID = "fake cluster";

    protected static final Logger LOG = LoggerFactory.getLogger(TestBPOfferService.class);

    private static final ExtendedBlock FAKE_BLOCK = new ExtendedBlock(TestBPOfferService.FAKE_BPID, 12345L);

    private static final File TEST_BUILD_DATA = PathUtils.getTestDir(TestBPOfferService.class);

    private long firstCallTime = 0;

    private long secondCallTime = 0;

    private long firstLeaseId = 0;

    private long secondLeaseId = 0;

    private long nextFullBlockReportLeaseId = 1L;

    static {
        GenericTestUtils.setLogLevel(DataNode.LOG, Level.ALL);
    }

    private DatanodeProtocolClientSideTranslatorPB mockNN1;

    private DatanodeProtocolClientSideTranslatorPB mockNN2;

    private final NNHAStatusHeartbeat[] mockHaStatuses = new NNHAStatusHeartbeat[3];

    private final DatanodeCommand[][] datanodeCommands = new DatanodeCommand[3][0];

    private final int[] heartbeatCounts = new int[3];

    private DataNode mockDn;

    private FsDatasetSpi<?> mockFSDataset;

    /**
     * Mock answer for heartbeats which returns an empty set of commands
     * and the HA status for the chosen NN from the
     * {@link TestBPOfferService#mockHaStatuses} array.
     */
    private class HeartbeatAnswer implements Answer<HeartbeatResponse> {
        private final int nnIdx;

        HeartbeatAnswer(int nnIdx) {
            this.nnIdx = nnIdx;
        }

        @Override
        public HeartbeatResponse answer(InvocationOnMock invocation) throws Throwable {
            (heartbeatCounts[nnIdx])++;
            Boolean requestFullBlockReportLease = ((Boolean) (invocation.getArguments()[8]));
            long fullBlockReportLeaseId = 0;
            if (requestFullBlockReportLease) {
                fullBlockReportLeaseId = (nextFullBlockReportLeaseId)++;
            }
            TestBPOfferService.LOG.info(("fullBlockReportLeaseId=" + fullBlockReportLeaseId));
            HeartbeatResponse heartbeatResponse = new HeartbeatResponse(datanodeCommands[nnIdx], mockHaStatuses[nnIdx], null, fullBlockReportLeaseId);
            // reset the command
            datanodeCommands[nnIdx] = new DatanodeCommand[0];
            return heartbeatResponse;
        }
    }

    private class HeartbeatRegisterAnswer implements Answer<HeartbeatResponse> {
        private final int nnIdx;

        HeartbeatRegisterAnswer(int nnIdx) {
            this.nnIdx = nnIdx;
        }

        @Override
        public HeartbeatResponse answer(InvocationOnMock invocation) throws Throwable {
            (heartbeatCounts[nnIdx])++;
            DatanodeCommand[] cmds = new DatanodeCommand[1];
            cmds[0] = new RegisterCommand();
            return new HeartbeatResponse(cmds, mockHaStatuses[nnIdx], null, 0L);
        }
    }

    /**
     * Test that the BPOS can register to talk to two different NNs,
     * sends block reports to both, etc.
     */
    @Test
    public void testBasicFunctionality() throws Exception {
        BPOfferService bpos = setupBPOSForNNs(mockNN1, mockNN2);
        bpos.start();
        try {
            waitForBothActors(bpos);
            // The DN should have register to both NNs.
            Mockito.verify(mockNN1).registerDatanode(Mockito.any());
            Mockito.verify(mockNN2).registerDatanode(Mockito.any());
            // Should get block reports from both NNs
            waitForBlockReport(mockNN1);
            waitForBlockReport(mockNN2);
            // When we receive a block, it should report it to both NNs
            bpos.notifyNamenodeReceivedBlock(TestBPOfferService.FAKE_BLOCK, null, "", false);
            ReceivedDeletedBlockInfo[] ret = waitForBlockReceived(TestBPOfferService.FAKE_BLOCK, mockNN1);
            Assert.assertEquals(1, ret.length);
            Assert.assertEquals(TestBPOfferService.FAKE_BLOCK.getLocalBlock(), ret[0].getBlock());
            ret = waitForBlockReceived(TestBPOfferService.FAKE_BLOCK, mockNN2);
            Assert.assertEquals(1, ret.length);
            Assert.assertEquals(TestBPOfferService.FAKE_BLOCK.getLocalBlock(), ret[0].getBlock());
        } finally {
            bpos.stop();
            bpos.join();
        }
    }

    @Test
    public void testLocklessBlockPoolId() throws Exception {
        BPOfferService bpos = Mockito.spy(setupBPOSForNNs(mockNN1));
        // bpNSInfo is not set, should take lock to check nsInfo.
        Assert.assertNull(bpos.getBlockPoolId());
        Mockito.verify(bpos).readLock();
        // setting the bpNSInfo should cache the bp id, thus no locking.
        Mockito.reset(bpos);
        NamespaceInfo nsInfo = new NamespaceInfo(1, TestBPOfferService.FAKE_CLUSTERID, TestBPOfferService.FAKE_BPID, 0);
        Assert.assertNull(bpos.setNamespaceInfo(nsInfo));
        Assert.assertEquals(TestBPOfferService.FAKE_BPID, bpos.getBlockPoolId());
        Mockito.verify(bpos, Mockito.never()).readLock();
        // clearing the bpNSInfo should clear the cached bp id, thus requiring
        // locking to check the bpNSInfo.
        Mockito.reset(bpos);
        Assert.assertEquals(nsInfo, bpos.setNamespaceInfo(null));
        Assert.assertNull(bpos.getBlockPoolId());
        Mockito.verify(bpos).readLock();
        // test setting it again.
        Mockito.reset(bpos);
        Assert.assertNull(bpos.setNamespaceInfo(nsInfo));
        Assert.assertEquals(TestBPOfferService.FAKE_BPID, bpos.getBlockPoolId());
        Mockito.verify(bpos, Mockito.never()).readLock();
    }

    /**
     * Test that DNA_INVALIDATE commands from the standby are ignored.
     */
    @Test
    public void testIgnoreDeletionsFromNonActive() throws Exception {
        BPOfferService bpos = setupBPOSForNNs(mockNN1, mockNN2);
        // Ask to invalidate FAKE_BLOCK when block report hits the
        // standby
        Mockito.doReturn(new org.apache.hadoop.hdfs.server.protocol.BlockCommand(DatanodeProtocol.DNA_INVALIDATE, TestBPOfferService.FAKE_BPID, new Block[]{ TestBPOfferService.FAKE_BLOCK.getLocalBlock() })).when(mockNN2).blockReport(Mockito.any(), Mockito.eq(TestBPOfferService.FAKE_BPID), Mockito.any(), Mockito.any());
        bpos.start();
        try {
            waitForInitialization(bpos);
            // Should get block reports from both NNs
            waitForBlockReport(mockNN1);
            waitForBlockReport(mockNN2);
        } finally {
            bpos.stop();
            bpos.join();
        }
        // Should ignore the delete command from the standby
        Mockito.verify(mockFSDataset, Mockito.never()).invalidate(Mockito.eq(TestBPOfferService.FAKE_BPID), Mockito.any());
    }

    /**
     * Ensure that, if the two NNs configured for a block pool
     * have different block pool IDs, they will refuse to both
     * register.
     */
    @Test
    public void testNNsFromDifferentClusters() throws Exception {
        Mockito.doReturn(new NamespaceInfo(1, "fake foreign cluster", TestBPOfferService.FAKE_BPID, 0)).when(mockNN1).versionRequest();
        BPOfferService bpos = setupBPOSForNNs(mockNN1, mockNN2);
        bpos.start();
        try {
            waitForOneToFail(bpos);
        } finally {
            bpos.stop();
            bpos.join();
        }
    }

    /**
     * Test that the DataNode determines the active NameNode correctly
     * based on the HA-related information in heartbeat responses.
     * See HDFS-2627.
     */
    @Test
    public void testPickActiveNameNode() throws Exception {
        BPOfferService bpos = setupBPOSForNNs(mockNN1, mockNN2);
        bpos.start();
        try {
            waitForInitialization(bpos);
            // Should start with neither NN as active.
            Assert.assertNull(bpos.getActiveNN());
            // Have NN1 claim active at txid 1
            mockHaStatuses[0] = new NNHAStatusHeartbeat(HAServiceState.ACTIVE, 1);
            bpos.triggerHeartbeatForTests();
            Assert.assertSame(mockNN1, bpos.getActiveNN());
            // NN2 claims active at a higher txid
            mockHaStatuses[1] = new NNHAStatusHeartbeat(HAServiceState.ACTIVE, 2);
            bpos.triggerHeartbeatForTests();
            Assert.assertSame(mockNN2, bpos.getActiveNN());
            // Even after another heartbeat from the first NN, it should
            // think NN2 is active, since it claimed a higher txid
            bpos.triggerHeartbeatForTests();
            Assert.assertSame(mockNN2, bpos.getActiveNN());
            // Even if NN2 goes to standby, DN shouldn't reset to talking to NN1,
            // because NN1's txid is lower than the last active txid. Instead,
            // it should consider neither active.
            mockHaStatuses[1] = new NNHAStatusHeartbeat(HAServiceState.STANDBY, 2);
            bpos.triggerHeartbeatForTests();
            Assert.assertNull(bpos.getActiveNN());
            // Now if NN1 goes back to a higher txid, it should be considered active
            mockHaStatuses[0] = new NNHAStatusHeartbeat(HAServiceState.ACTIVE, 3);
            bpos.triggerHeartbeatForTests();
            Assert.assertSame(mockNN1, bpos.getActiveNN());
        } finally {
            bpos.stop();
            bpos.join();
        }
    }

    /**
     * Test datanode block pool initialization error handling.
     * Failure in initializing a block pool should not cause NPE.
     */
    @Test
    public void testBPInitErrorHandling() throws Exception {
        final DataNode mockDn = Mockito.mock(DataNode.class);
        Mockito.doReturn(true).when(mockDn).shouldRun();
        Configuration conf = new Configuration();
        File dnDataDir = new File(new File(TestBPOfferService.TEST_BUILD_DATA, "testBPInitErrorHandling"), "data");
        conf.set(DFSConfigKeys.DFS_DATANODE_DATA_DIR_KEY, dnDataDir.toURI().toString());
        Mockito.doReturn(conf).when(mockDn).getConf();
        Mockito.doReturn(new DNConf(mockDn)).when(mockDn).getDnConf();
        Mockito.doReturn(DataNodeMetrics.create(conf, "fake dn")).when(mockDn).getMetrics();
        final AtomicInteger count = new AtomicInteger();
        Mockito.doAnswer(new Answer<Void>() {
            @Override
            public Void answer(InvocationOnMock invocation) throws Throwable {
                if ((count.getAndIncrement()) == 0) {
                    throw new IOException("faked initBlockPool exception");
                }
                // The initBlockPool is called again. Now mock init is done.
                Mockito.doReturn(mockFSDataset).when(mockDn).getFSDataset();
                return null;
            }
        }).when(mockDn).initBlockPool(Mockito.any(BPOfferService.class));
        BPOfferService bpos = setupBPOSForNNs(mockDn, mockNN1, mockNN2);
        List<BPServiceActor> actors = bpos.getBPServiceActors();
        Assert.assertEquals(2, actors.size());
        bpos.start();
        try {
            waitForInitialization(bpos);
            // even if one of the actor initialization fails, the other one will be
            // finish block report.
            waitForBlockReport(mockNN1, mockNN2);
        } finally {
            bpos.stop();
            bpos.join();
        }
    }

    private class BPOfferServiceSynchronousCallAnswer implements Answer<Void> {
        private final int nnIdx;

        public BPOfferServiceSynchronousCallAnswer(int nnIdx) {
            this.nnIdx = nnIdx;
        }

        // For active namenode we will record the processTime and for standby
        // namenode we will sleep for 5 seconds (This will simulate the situation
        // where the standby namenode is down ) .
        @Override
        public Void answer(InvocationOnMock invocation) throws Throwable {
            if ((nnIdx) == 0) {
                setTimeForSynchronousBPOSCalls();
            } else {
                Thread.sleep(5000);
            }
            return null;
        }
    }

    /**
     * This test case test the {@link BPOfferService#reportBadBlocks} method
     * such that if call to standby namenode times out then that should not
     * affect the active namenode heartbeat processing since this function
     * are in writeLock.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testReportBadBlockWhenStandbyNNTimesOut() throws Exception {
        BPOfferService bpos = setupBPOSForNNs(mockNN1, mockNN2);
        bpos.start();
        try {
            waitForInitialization(bpos);
            // Should start with neither NN as active.
            Assert.assertNull(bpos.getActiveNN());
            // Have NN1 claim active at txid 1
            mockHaStatuses[0] = new NNHAStatusHeartbeat(HAServiceState.ACTIVE, 1);
            bpos.triggerHeartbeatForTests();
            // Now mockNN1 is acting like active namenode and mockNN2 as Standby
            Assert.assertSame(mockNN1, bpos.getActiveNN());
            Mockito.doAnswer(new TestBPOfferService.BPOfferServiceSynchronousCallAnswer(0)).when(mockNN1).reportBadBlocks(Mockito.any());
            Mockito.doAnswer(new TestBPOfferService.BPOfferServiceSynchronousCallAnswer(1)).when(mockNN2).reportBadBlocks(Mockito.any());
            bpos.reportBadBlocks(TestBPOfferService.FAKE_BLOCK, mockFSDataset.getVolume(TestBPOfferService.FAKE_BLOCK).getStorageID(), mockFSDataset.getVolume(TestBPOfferService.FAKE_BLOCK).getStorageType());
            bpos.reportBadBlocks(TestBPOfferService.FAKE_BLOCK, mockFSDataset.getVolume(TestBPOfferService.FAKE_BLOCK).getStorageID(), mockFSDataset.getVolume(TestBPOfferService.FAKE_BLOCK).getStorageType());
            Thread.sleep(10000);
            long difference = (secondCallTime) - (firstCallTime);
            Assert.assertTrue(("Active namenode reportBadBlock processing should be " + "independent of standby namenode reportBadBlock processing "), (difference < 5000));
        } finally {
            bpos.stop();
            bpos.join();
        }
    }

    /**
     * This test case test the {@link BPOfferService#trySendErrorReport} method
     * such that if call to standby namenode times out then that should not
     * affect the active namenode heartbeat processing since this function
     * are in writeLock.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testTrySendErrorReportWhenStandbyNNTimesOut() throws Exception {
        BPOfferService bpos = setupBPOSForNNs(mockNN1, mockNN2);
        bpos.start();
        try {
            waitForInitialization(bpos);
            // Should start with neither NN as active.
            Assert.assertNull(bpos.getActiveNN());
            // Have NN1 claim active at txid 1
            mockHaStatuses[0] = new NNHAStatusHeartbeat(HAServiceState.ACTIVE, 1);
            bpos.triggerHeartbeatForTests();
            // Now mockNN1 is acting like active namenode and mockNN2 as Standby
            Assert.assertSame(mockNN1, bpos.getActiveNN());
            Mockito.doAnswer(new TestBPOfferService.BPOfferServiceSynchronousCallAnswer(0)).when(mockNN1).errorReport(Mockito.any(), Mockito.anyInt(), Mockito.anyString());
            Mockito.doAnswer(new TestBPOfferService.BPOfferServiceSynchronousCallAnswer(1)).when(mockNN2).errorReport(Mockito.any(), Mockito.anyInt(), Mockito.anyString());
            String errorString = "Can't send invalid block " + (TestBPOfferService.FAKE_BLOCK);
            bpos.trySendErrorReport(INVALID_BLOCK, errorString);
            bpos.trySendErrorReport(INVALID_BLOCK, errorString);
            Thread.sleep(10000);
            long difference = (secondCallTime) - (firstCallTime);
            Assert.assertTrue(("Active namenode trySendErrorReport processing " + ("should be independent of standby namenode trySendErrorReport" + " processing ")), (difference < 5000));
        } finally {
            bpos.stop();
            bpos.join();
        }
    }

    /**
     * This test case tests whether the {@BPServiceActor#processQueueMessages }
     * adds back the error report back to the queue when
     * {BPServiceActorAction#reportTo} throws an IOException
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testTrySendErrorReportWhenNNThrowsIOException() throws Exception {
        BPOfferService bpos = setupBPOSForNNs(mockNN1, mockNN2);
        bpos.start();
        try {
            waitForInitialization(bpos);
            // Should start with neither NN as active.
            Assert.assertNull(bpos.getActiveNN());
            // Have NN1 claim active at txid 1
            mockHaStatuses[0] = new NNHAStatusHeartbeat(HAServiceState.ACTIVE, 1);
            bpos.triggerHeartbeatForTests();
            // Now mockNN1 is acting like active namenode and mockNN2 as Standby
            Assert.assertSame(mockNN1, bpos.getActiveNN());
            // Throw an IOException when this function is first called which will
            // in turn add that errorReport back to the bpThreadQueue and let it
            // process the next time.
            Mockito.doThrow(new IOException("Throw IOException in the first call.")).doAnswer(((Answer<Void>) (( invocation) -> {
                secondCallTime = Time.now();
                return null;
            }))).when(mockNN1).errorReport(Mockito.any(DatanodeRegistration.class), Mockito.anyInt(), Mockito.anyString());
            String errorString = "Can't send invalid block " + (TestBPOfferService.FAKE_BLOCK);
            bpos.trySendErrorReport(INVALID_BLOCK, errorString);
            Thread.sleep(10000);
            Assert.assertTrue(("Active namenode didn't add the report back to the queue " + "when errorReport threw IOException"), ((secondCallTime) != 0));
        } finally {
            bpos.stop();
            bpos.join();
        }
    }

    /**
     * This test case doesn't add the reportBadBlock request to
     * {@link BPServiceActor#bpThreadEnqueue} when the Standby namenode throws
     * {@link StandbyException}
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testReportBadBlocksWhenNNThrowsStandbyException() throws Exception {
        BPOfferService bpos = setupBPOSForNNs(mockNN1, mockNN2);
        bpos.start();
        try {
            waitForInitialization(bpos);
            // Should start with neither NN as active.
            Assert.assertNull(bpos.getActiveNN());
            // Have NN1 claim active at txid 1
            mockHaStatuses[0] = new NNHAStatusHeartbeat(HAServiceState.ACTIVE, 1);
            bpos.triggerHeartbeatForTests();
            // Now mockNN1 is acting like active namenode and mockNN2 as Standby
            Assert.assertSame(mockNN1, bpos.getActiveNN());
            // Return nothing when active Active Namenode calls reportBadBlocks
            Mockito.doNothing().when(mockNN1).reportBadBlocks(Mockito.any(LocatedBlock[].class));
            RemoteException re = new RemoteException(StandbyException.class.getName(), ("Operation category WRITE is not supported in state " + "standby"), RpcErrorCodeProto.ERROR_APPLICATION);
            // Return StandbyException wrapped in RemoteException when Standby NN
            // calls reportBadBlocks
            Mockito.doThrow(re).when(mockNN2).reportBadBlocks(Mockito.any(LocatedBlock[].class));
            bpos.reportBadBlocks(TestBPOfferService.FAKE_BLOCK, mockFSDataset.getVolume(TestBPOfferService.FAKE_BLOCK).getStorageID(), mockFSDataset.getVolume(TestBPOfferService.FAKE_BLOCK).getStorageType());
            // Send heartbeat so that the BpServiceActor can report bad block to
            // namenode
            bpos.triggerHeartbeatForTests();
            Mockito.verify(mockNN2, Mockito.times(1)).reportBadBlocks(Mockito.any(LocatedBlock[].class));
            // Trigger another heartbeat, this will send reportBadBlock again if it
            // is present in the queue.
            bpos.triggerHeartbeatForTests();
            Mockito.verify(mockNN2, Mockito.times(1)).reportBadBlocks(Mockito.any(LocatedBlock[].class));
        } finally {
            bpos.stop();
            bpos.join();
        }
    }

    /* HDFS-9917 : Standby IBR accumulation when Standby was down. */
    @Test
    public void testIBRClearanceForStandbyOnReRegister() throws Exception {
        final BPOfferService bpos = setupBPOSForNNs(mockNN1, mockNN2);
        bpos.start();
        try {
            waitForInitialization(bpos);
            // Should start with neither NN as active.
            Assert.assertNull(bpos.getActiveNN());
            // Have NN1 claim active at txid 1
            mockHaStatuses[0] = new NNHAStatusHeartbeat(HAServiceState.ACTIVE, 1);
            bpos.triggerHeartbeatForTests();
            // Now mockNN1 is acting like active namenode and mockNN2 as Standby
            Assert.assertSame(mockNN1, bpos.getActiveNN());
            // Return nothing when active Active Namenode gets IBRs
            Mockito.doNothing().when(mockNN1).blockReceivedAndDeleted(Mockito.any(DatanodeRegistration.class), Mockito.anyString(), Mockito.any(StorageReceivedDeletedBlocks[].class));
            final IOException re = new IOException("Standby NN is currently not able to process IBR");
            final AtomicBoolean ibrReported = new AtomicBoolean(false);
            // throw exception for standby when first IBR is receieved
            Mockito.doAnswer(new Answer<Void>() {
                @Override
                public Void answer(InvocationOnMock invocation) throws Throwable {
                    ibrReported.set(true);
                    throw re;
                }
            }).when(mockNN2).blockReceivedAndDeleted(Mockito.any(DatanodeRegistration.class), Mockito.anyString(), Mockito.any(StorageReceivedDeletedBlocks[].class));
            DatanodeStorage storage = Mockito.mock(DatanodeStorage.class);
            Mockito.doReturn(storage).when(mockFSDataset).getStorage("storage0");
            // Add IBRs
            bpos.notifyNamenodeReceivedBlock(TestBPOfferService.FAKE_BLOCK, null, "storage0", false);
            // Send heartbeat so that the BpServiceActor can send IBR to
            // namenode
            bpos.triggerHeartbeatForTests();
            // Wait till first IBR is received at standbyNN. Just for confirmation.
            GenericTestUtils.waitFor(new Supplier<Boolean>() {
                @Override
                public Boolean get() {
                    return ibrReported.get();
                }
            }, 100, 1000);
            // Send register command back to Datanode to reRegister().
            // After reRegister IBRs should be cleared.
            datanodeCommands[1] = new DatanodeCommand[]{ new RegisterCommand() };
            Assert.assertEquals("IBR size before reRegister should be non-0", 1, getStandbyIBRSize(bpos));
            bpos.triggerHeartbeatForTests();
            GenericTestUtils.waitFor(new Supplier<Boolean>() {
                @Override
                public Boolean get() {
                    return (getStandbyIBRSize(bpos)) == 0;
                }
            }, 100, 1000);
        } finally {
            bpos.stop();
            bpos.join();
        }
    }

    /*  */
    @Test
    public void testNNHAStateUpdateFromVersionRequest() throws Exception {
        final BPOfferService bpos = setupBPOSForNNs(mockNN1, mockNN2);
        Mockito.doReturn(true).when(mockDn).areHeartbeatsDisabledForTests();
        BPServiceActor actor = bpos.getBPServiceActors().get(0);
        bpos.start();
        waitForInitialization(bpos);
        // Should start with neither NN as active.
        Assert.assertNull(bpos.getActiveNN());
        // getNamespaceInfo() will not include HAServiceState
        NamespaceInfo nsInfo = mockNN1.versionRequest();
        bpos.verifyAndSetNamespaceInfo(actor, nsInfo);
        Assert.assertNull(bpos.getActiveNN());
        // Change mock so getNamespaceInfo() will include HAServiceState
        Mockito.doReturn(new NamespaceInfo(1, TestBPOfferService.FAKE_CLUSTERID, TestBPOfferService.FAKE_BPID, 0, HAServiceState.ACTIVE)).when(mockNN1).versionRequest();
        // Update the bpos NamespaceInfo
        nsInfo = mockNN1.versionRequest();
        bpos.verifyAndSetNamespaceInfo(actor, nsInfo);
        Assert.assertNotNull(bpos.getActiveNN());
    }

    @Test(timeout = 30000)
    public void testRefreshNameNodes() throws Exception {
        BPOfferService bpos = setupBPOSForNNs(mockDn, mockNN1, mockNN2);
        bpos.start();
        try {
            waitForBothActors(bpos);
            // The DN should have register to both NNs.
            Mockito.verify(mockNN1).registerDatanode(Mockito.any());
            Mockito.verify(mockNN2).registerDatanode(Mockito.any());
            // Should get block reports from both NNs
            waitForBlockReport(mockNN1);
            waitForBlockReport(mockNN2);
            // When we receive a block, it should report it to both NNs
            bpos.notifyNamenodeReceivedBlock(TestBPOfferService.FAKE_BLOCK, null, "", false);
            ReceivedDeletedBlockInfo[] ret = waitForBlockReceived(TestBPOfferService.FAKE_BLOCK, mockNN1);
            Assert.assertEquals(1, ret.length);
            Assert.assertEquals(TestBPOfferService.FAKE_BLOCK.getLocalBlock(), ret[0].getBlock());
            ret = waitForBlockReceived(TestBPOfferService.FAKE_BLOCK, mockNN2);
            Assert.assertEquals(1, ret.length);
            Assert.assertEquals(TestBPOfferService.FAKE_BLOCK.getLocalBlock(), ret[0].getBlock());
            // add new standby
            DatanodeProtocolClientSideTranslatorPB mockNN3 = setupNNMock(2);
            Mockito.doReturn(mockNN3).when(mockDn).connectToNN(Mockito.eq(new InetSocketAddress(2)));
            ArrayList<InetSocketAddress> addrs = new ArrayList<>();
            ArrayList<InetSocketAddress> lifelineAddrs = new ArrayList<>(addrs.size());
            // mockNN1
            addrs.add(new InetSocketAddress(0));
            lifelineAddrs.add(null);
            // mockNN3
            addrs.add(new InetSocketAddress(2));
            lifelineAddrs.add(null);
            ArrayList<String> nnIds = new ArrayList<>(addrs.size());
            for (int i = 0; i < (addrs.size()); i++) {
                nnIds.add(("nn" + i));
            }
            bpos.refreshNNList("serviceId", nnIds, addrs, lifelineAddrs);
            Assert.assertEquals(2, bpos.getBPServiceActors().size());
            // wait for handshake to run
            Thread.sleep(1000);
            // verify new NN registered
            Mockito.verify(mockNN3).registerDatanode(Mockito.any());
            // When we receive a block, it should report it to both NNs
            bpos.notifyNamenodeReceivedBlock(TestBPOfferService.FAKE_BLOCK, null, "", false);
            // veridfy new NN recieved block report
            ret = waitForBlockReceived(TestBPOfferService.FAKE_BLOCK, mockNN3);
            Assert.assertEquals(1, ret.length);
            Assert.assertEquals(TestBPOfferService.FAKE_BLOCK.getLocalBlock(), ret[0].getBlock());
        } finally {
            bpos.stop();
            bpos.join();
        }
    }

    @Test(timeout = 15000)
    public void testRefreshLeaseId() throws Exception {
        // heartbeat to new NN instance with Register Command
        // heartbeat to old NN instance
        Mockito.when(mockNN1.sendHeartbeat(Mockito.any(DatanodeRegistration.class), Mockito.any(StorageReport[].class), Mockito.anyLong(), Mockito.anyLong(), Mockito.anyInt(), Mockito.anyInt(), Mockito.anyInt(), Mockito.any(VolumeFailureSummary.class), Mockito.anyBoolean(), Mockito.any(SlowPeerReports.class), Mockito.any(SlowDiskReports.class))).thenAnswer(new TestBPOfferService.HeartbeatAnswer(0)).thenAnswer(new TestBPOfferService.HeartbeatRegisterAnswer(0)).thenAnswer(new TestBPOfferService.HeartbeatAnswer(0));
        Mockito.when(mockNN1.blockReport(Mockito.any(DatanodeRegistration.class), Mockito.anyString(), Mockito.any(StorageBlockReport[].class), Mockito.any(BlockReportContext.class))).thenAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                BlockReportContext context = ((BlockReportContext) (invocation.getArguments()[3]));
                long leaseId = context.getLeaseId();
                TestBPOfferService.LOG.info(("leaseId = " + leaseId));
                // leaseId == 1 means DN make block report with old leaseId
                // just reject and wait until DN request for a new leaseId
                if (leaseId == 1) {
                    firstLeaseId = leaseId;
                    throw new ConnectException("network is not reachable for test. ");
                } else {
                    secondLeaseId = leaseId;
                    return null;
                }
            }
        });
        BPOfferService bpos = setupBPOSForNNs(mockNN1);
        bpos.start();
        try {
            waitForInitialization(bpos);
            // Should call registration 2 times
            waitForRegistration(mockNN1, 2);
            Assert.assertEquals(1L, firstLeaseId);
            while ((secondLeaseId) != 2L) {
                Thread.sleep(1000);
            } 
        } finally {
            bpos.stop();
        }
    }
}

