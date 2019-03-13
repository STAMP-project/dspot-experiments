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
package org.apache.hadoop.hbase.master;


import HConstants.NO_NONCE;
import JVMClusterUtil.RegionServerThread;
import MasterProcedureProtos.SplitWALData.Builder;
import MasterProcedureProtos.SplitWALState;
import MasterWalManager.META_FILTER;
import MasterWalManager.NON_META_FILTER;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.HBaseTestingUtility;
import org.apache.hadoop.hbase.ServerName;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.master.procedure.MasterProcedureEnv;
import org.apache.hadoop.hbase.master.procedure.ServerProcedureInterface;
import org.apache.hadoop.hbase.procedure2.Procedure;
import org.apache.hadoop.hbase.procedure2.ProcedureExecutor;
import org.apache.hadoop.hbase.procedure2.ProcedureStateSerializer;
import org.apache.hadoop.hbase.procedure2.ProcedureSuspendedException;
import org.apache.hadoop.hbase.procedure2.ProcedureTestingUtility;
import org.apache.hadoop.hbase.procedure2.ProcedureYieldException;
import org.apache.hadoop.hbase.procedure2.StateMachineProcedure;
import org.apache.hadoop.hbase.shaded.protobuf.ProtobufUtil;
import org.apache.hadoop.hbase.shaded.protobuf.generated.MasterProcedureProtos;
import org.apache.hadoop.hbase.testclassification.MasterTests;
import org.apache.hadoop.hbase.testclassification.MediumTests;
import org.apache.hadoop.hbase.util.JVMClusterUtil;
import org.apache.hadoop.hbase.wal.AbstractFSWALProvider;
import org.apache.hbase.thirdparty.com.google.common.collect.Lists;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import static Flow.HAS_MORE_STATE;
import static Flow.NO_MORE_STATE;
import static ServerOperationType.SPLIT_WAL;


@Category({ MasterTests.class, MediumTests.class })
public class TestSplitWALManager {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestSplitWALManager.class);

    private static HBaseTestingUtility TEST_UTIL;

    private HMaster master;

    private SplitWALManager splitWALManager;

    private TableName TABLE_NAME;

    private byte[] FAMILY;

    @Test
    public void testAcquireAndRelease() throws Exception {
        List<TestSplitWALManager.FakeServerProcedure> testProcedures = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            testProcedures.add(new TestSplitWALManager.FakeServerProcedure(TestSplitWALManager.TEST_UTIL.getHBaseCluster().getServerHoldingMeta()));
        }
        ServerName server = splitWALManager.acquireSplitWALWorker(testProcedures.get(0));
        Assert.assertNotNull(server);
        Assert.assertNotNull(splitWALManager.acquireSplitWALWorker(testProcedures.get(1)));
        Assert.assertNotNull(splitWALManager.acquireSplitWALWorker(testProcedures.get(2)));
        Exception e = null;
        try {
            splitWALManager.acquireSplitWALWorker(testProcedures.get(3));
        } catch (ProcedureSuspendedException suspendException) {
            e = suspendException;
        }
        Assert.assertNotNull(e);
        Assert.assertTrue((e instanceof ProcedureSuspendedException));
        splitWALManager.releaseSplitWALWorker(server, TestSplitWALManager.TEST_UTIL.getHBaseCluster().getMaster().getMasterProcedureExecutor().getEnvironment().getProcedureScheduler());
        Assert.assertNotNull(splitWALManager.acquireSplitWALWorker(testProcedures.get(3)));
    }

    @Test
    public void testAddNewServer() throws Exception {
        List<TestSplitWALManager.FakeServerProcedure> testProcedures = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            testProcedures.add(new TestSplitWALManager.FakeServerProcedure(TestSplitWALManager.TEST_UTIL.getHBaseCluster().getServerHoldingMeta()));
        }
        ServerName server = splitWALManager.acquireSplitWALWorker(testProcedures.get(0));
        Assert.assertNotNull(server);
        Assert.assertNotNull(splitWALManager.acquireSplitWALWorker(testProcedures.get(1)));
        Assert.assertNotNull(splitWALManager.acquireSplitWALWorker(testProcedures.get(2)));
        Exception e = null;
        try {
            splitWALManager.acquireSplitWALWorker(testProcedures.get(3));
        } catch (ProcedureSuspendedException suspendException) {
            e = suspendException;
        }
        Assert.assertNotNull(e);
        Assert.assertTrue((e instanceof ProcedureSuspendedException));
        JVMClusterUtil.RegionServerThread newServer = TestSplitWALManager.TEST_UTIL.getHBaseCluster().startRegionServer();
        newServer.waitForServerOnline();
        Assert.assertNotNull(splitWALManager.acquireSplitWALWorker(testProcedures.get(3)));
    }

    @Test
    public void testCreateSplitWALProcedures() throws Exception {
        TestSplitWALManager.TEST_UTIL.createTable(TABLE_NAME, FAMILY, TestSplitWALManager.TEST_UTIL.KEYS_FOR_HBA_CREATE_TABLE);
        // load table
        TestSplitWALManager.TEST_UTIL.loadTable(TestSplitWALManager.TEST_UTIL.getConnection().getTable(TABLE_NAME), FAMILY);
        ProcedureExecutor<MasterProcedureEnv> masterPE = master.getMasterProcedureExecutor();
        ServerName metaServer = TestSplitWALManager.TEST_UTIL.getHBaseCluster().getServerHoldingMeta();
        Path metaWALDir = new Path(TestSplitWALManager.TEST_UTIL.getDefaultRootDirPath(), AbstractFSWALProvider.getWALDirectoryName(metaServer.toString()));
        // Test splitting meta wal
        FileStatus[] wals = TestSplitWALManager.TEST_UTIL.getTestFileSystem().listStatus(metaWALDir, META_FILTER);
        Assert.assertEquals(1, wals.length);
        List<Procedure> testProcedures = splitWALManager.createSplitWALProcedures(Lists.newArrayList(wals[0]), metaServer);
        Assert.assertEquals(1, testProcedures.size());
        ProcedureTestingUtility.submitAndWait(masterPE, testProcedures.get(0));
        Assert.assertFalse(TestSplitWALManager.TEST_UTIL.getTestFileSystem().exists(wals[0].getPath()));
        // Test splitting wal
        wals = TestSplitWALManager.TEST_UTIL.getTestFileSystem().listStatus(metaWALDir, NON_META_FILTER);
        Assert.assertEquals(1, wals.length);
        testProcedures = splitWALManager.createSplitWALProcedures(Lists.newArrayList(wals[0]), metaServer);
        Assert.assertEquals(1, testProcedures.size());
        ProcedureTestingUtility.submitAndWait(masterPE, testProcedures.get(0));
        Assert.assertFalse(TestSplitWALManager.TEST_UTIL.getTestFileSystem().exists(wals[0].getPath()));
    }

    @Test
    public void testAcquireAndReleaseSplitWALWorker() throws Exception {
        ProcedureExecutor<MasterProcedureEnv> masterPE = master.getMasterProcedureExecutor();
        List<TestSplitWALManager.FakeServerProcedure> testProcedures = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            TestSplitWALManager.FakeServerProcedure procedure = new TestSplitWALManager.FakeServerProcedure(TestSplitWALManager.TEST_UTIL.getHBaseCluster().getRegionServer(i).getServerName());
            testProcedures.add(procedure);
            ProcedureTestingUtility.submitProcedure(masterPE, procedure, NO_NONCE, NO_NONCE);
        }
        waitFor(10000, () -> testProcedures.get(2).isWorkerAcquired());
        TestSplitWALManager.FakeServerProcedure failedProcedure = new TestSplitWALManager.FakeServerProcedure(TestSplitWALManager.TEST_UTIL.getHBaseCluster().getServerHoldingMeta());
        ProcedureTestingUtility.submitProcedure(masterPE, failedProcedure, NO_NONCE, NO_NONCE);
        waitFor(20000, () -> failedProcedure.isTriedToAcquire());
        Assert.assertFalse(failedProcedure.isWorkerAcquired());
        // let one procedure finish and release worker
        testProcedures.get(0).countDown();
        waitFor(10000, () -> failedProcedure.isWorkerAcquired());
        Assert.assertTrue(isSuccess());
    }

    @Test
    public void testGetWALsToSplit() throws Exception {
        TestSplitWALManager.TEST_UTIL.createTable(TABLE_NAME, FAMILY, TestSplitWALManager.TEST_UTIL.KEYS_FOR_HBA_CREATE_TABLE);
        // load table
        TestSplitWALManager.TEST_UTIL.loadTable(TestSplitWALManager.TEST_UTIL.getConnection().getTable(TABLE_NAME), FAMILY);
        ServerName metaServer = TestSplitWALManager.TEST_UTIL.getHBaseCluster().getServerHoldingMeta();
        List<FileStatus> metaWals = splitWALManager.getWALsToSplit(metaServer, true);
        Assert.assertEquals(1, metaWals.size());
        List<FileStatus> wals = splitWALManager.getWALsToSplit(metaServer, false);
        Assert.assertEquals(1, wals.size());
        ServerName testServer = TestSplitWALManager.TEST_UTIL.getHBaseCluster().getRegionServerThreads().stream().map(( rs) -> rs.getRegionServer().getServerName()).filter(( rs) -> rs != metaServer).findAny().get();
        metaWals = splitWALManager.getWALsToSplit(testServer, true);
        Assert.assertEquals(0, metaWals.size());
    }

    @Test
    public void testSplitLogs() throws Exception {
        TestSplitWALManager.TEST_UTIL.createTable(TABLE_NAME, FAMILY, TestSplitWALManager.TEST_UTIL.KEYS_FOR_HBA_CREATE_TABLE);
        // load table
        TestSplitWALManager.TEST_UTIL.loadTable(TestSplitWALManager.TEST_UTIL.getConnection().getTable(TABLE_NAME), FAMILY);
        ProcedureExecutor<MasterProcedureEnv> masterPE = master.getMasterProcedureExecutor();
        ServerName metaServer = TestSplitWALManager.TEST_UTIL.getHBaseCluster().getServerHoldingMeta();
        ServerName testServer = TestSplitWALManager.TEST_UTIL.getHBaseCluster().getRegionServerThreads().stream().map(( rs) -> rs.getRegionServer().getServerName()).filter(( rs) -> rs != metaServer).findAny().get();
        List<Procedure> procedures = splitWALManager.splitWALs(testServer, false);
        Assert.assertEquals(1, procedures.size());
        ProcedureTestingUtility.submitAndWait(masterPE, procedures.get(0));
        Assert.assertEquals(0, splitWALManager.getWALsToSplit(testServer, false).size());
        procedures = splitWALManager.splitWALs(metaServer, true);
        Assert.assertEquals(1, procedures.size());
        ProcedureTestingUtility.submitAndWait(masterPE, procedures.get(0));
        Assert.assertEquals(0, splitWALManager.getWALsToSplit(metaServer, true).size());
        Assert.assertEquals(1, splitWALManager.getWALsToSplit(metaServer, false).size());
    }

    @Test
    public void testWorkerReloadWhenMasterRestart() throws Exception {
        List<TestSplitWALManager.FakeServerProcedure> testProcedures = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            TestSplitWALManager.FakeServerProcedure procedure = new TestSplitWALManager.FakeServerProcedure(TestSplitWALManager.TEST_UTIL.getHBaseCluster().getRegionServer(i).getServerName());
            testProcedures.add(procedure);
            ProcedureTestingUtility.submitProcedure(master.getMasterProcedureExecutor(), procedure, NO_NONCE, NO_NONCE);
        }
        waitFor(10000, () -> testProcedures.get(2).isWorkerAcquired());
        // Kill master
        TestSplitWALManager.TEST_UTIL.getHBaseCluster().killMaster(master.getServerName());
        TestSplitWALManager.TEST_UTIL.getHBaseCluster().waitForMasterToStop(master.getServerName(), 20000);
        // restart master
        TestSplitWALManager.TEST_UTIL.getHBaseCluster().startMaster();
        TestSplitWALManager.TEST_UTIL.getHBaseCluster().waitForActiveAndReadyMaster();
        this.master = TestSplitWALManager.TEST_UTIL.getHBaseCluster().getMaster();
        TestSplitWALManager.FakeServerProcedure failedProcedure = new TestSplitWALManager.FakeServerProcedure(TestSplitWALManager.TEST_UTIL.getHBaseCluster().getServerHoldingMeta());
        ProcedureTestingUtility.submitProcedure(master.getMasterProcedureExecutor(), failedProcedure, NO_NONCE, NO_NONCE);
        waitFor(20000, () -> failedProcedure.isTriedToAcquire());
        Assert.assertFalse(failedProcedure.isWorkerAcquired());
        for (int i = 0; i < 3; i++) {
            testProcedures.get(i).countDown();
        }
        failedProcedure.countDown();
    }

    public static final class FakeServerProcedure extends StateMachineProcedure<MasterProcedureEnv, MasterProcedureProtos.SplitWALState> implements ServerProcedureInterface {
        private ServerName serverName;

        private ServerName worker;

        private CountDownLatch barrier = new CountDownLatch(1);

        private boolean triedToAcquire = false;

        public FakeServerProcedure() {
        }

        public FakeServerProcedure(ServerName serverName) {
            this.serverName = serverName;
        }

        public ServerName getServerName() {
            return serverName;
        }

        @Override
        public boolean hasMetaTableRegion() {
            return false;
        }

        @Override
        public ServerOperationType getServerOperationType() {
            return SPLIT_WAL;
        }

        @Override
        protected Flow executeFromState(MasterProcedureEnv env, MasterProcedureProtos.SplitWALState state) throws InterruptedException, ProcedureSuspendedException, ProcedureYieldException {
            SplitWALManager splitWALManager = env.getMasterServices().getSplitWALManager();
            switch (state) {
                case ACQUIRE_SPLIT_WAL_WORKER :
                    triedToAcquire = true;
                    worker = splitWALManager.acquireSplitWALWorker(this);
                    setNextState(MasterProcedureProtos.SplitWALState.DISPATCH_WAL_TO_WORKER);
                    return HAS_MORE_STATE;
                case DISPATCH_WAL_TO_WORKER :
                    barrier.await();
                    setNextState(MasterProcedureProtos.SplitWALState.RELEASE_SPLIT_WORKER);
                    return HAS_MORE_STATE;
                case RELEASE_SPLIT_WORKER :
                    splitWALManager.releaseSplitWALWorker(worker, env.getProcedureScheduler());
                    return NO_MORE_STATE;
                default :
                    throw new UnsupportedOperationException(("unhandled state=" + state));
            }
        }

        public boolean isWorkerAcquired() {
            return (worker) != null;
        }

        public boolean isTriedToAcquire() {
            return triedToAcquire;
        }

        public void countDown() {
            this.barrier.countDown();
        }

        @Override
        protected void rollbackState(MasterProcedureEnv env, MasterProcedureProtos.SplitWALState state) throws IOException, InterruptedException {
        }

        @Override
        protected SplitWALState getState(int stateId) {
            return SplitWALState.forNumber(stateId);
        }

        @Override
        protected int getStateId(MasterProcedureProtos.SplitWALState state) {
            return state.getNumber();
        }

        @Override
        protected SplitWALState getInitialState() {
            return SplitWALState.ACQUIRE_SPLIT_WAL_WORKER;
        }

        @Override
        protected boolean holdLock(MasterProcedureEnv env) {
            return true;
        }

        @Override
        protected void rollback(MasterProcedureEnv env) throws IOException, InterruptedException {
        }

        @Override
        protected boolean abort(MasterProcedureEnv env) {
            return false;
        }

        @Override
        protected void serializeStateData(ProcedureStateSerializer serializer) throws IOException {
            MasterProcedureProtos.SplitWALData.Builder builder = MasterProcedureProtos.SplitWALData.newBuilder();
            builder.setWalPath("test").setCrashedServer(ProtobufUtil.toServerName(serverName));
            serializer.serialize(builder.build());
        }

        @Override
        protected void deserializeStateData(ProcedureStateSerializer serializer) throws IOException {
            MasterProcedureProtos.SplitWALData data = serializer.deserialize(MasterProcedureProtos.SplitWALData.class);
            serverName = ProtobufUtil.toServerName(data.getCrashedServer());
        }
    }
}

