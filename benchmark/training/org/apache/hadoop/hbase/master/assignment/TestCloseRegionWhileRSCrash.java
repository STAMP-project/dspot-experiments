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
package org.apache.hadoop.hbase.master.assignment;


import java.io.IOException;
import java.util.concurrent.CountDownLatch;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.HBaseTestingUtility;
import org.apache.hadoop.hbase.ProcedureTestUtil;
import org.apache.hadoop.hbase.ServerName;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.RegionInfo;
import org.apache.hadoop.hbase.client.Table;
import org.apache.hadoop.hbase.master.procedure.MasterProcedureEnv;
import org.apache.hadoop.hbase.master.procedure.ServerCrashProcedure;
import org.apache.hadoop.hbase.master.procedure.ServerProcedureInterface;
import org.apache.hadoop.hbase.procedure2.Procedure;
import org.apache.hadoop.hbase.procedure2.ProcedureExecutor;
import org.apache.hadoop.hbase.procedure2.ProcedureStateSerializer;
import org.apache.hadoop.hbase.procedure2.ProcedureSuspendedException;
import org.apache.hadoop.hbase.procedure2.ProcedureYieldException;
import org.apache.hadoop.hbase.regionserver.HRegionServer;
import org.apache.hadoop.hbase.testclassification.MasterTests;
import org.apache.hadoop.hbase.testclassification.MediumTests;
import org.apache.hadoop.hbase.util.Bytes;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import static LockState.LOCK_ACQUIRED;
import static LockState.LOCK_EVENT_WAIT;
import static ServerOperationType.CRASH_HANDLER;


/**
 * Confirm that we will do backoff when retrying on closing a region, to avoid consuming all the
 * CPUs.
 */
@Category({ MasterTests.class, MediumTests.class })
public class TestCloseRegionWhileRSCrash {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestCloseRegionWhileRSCrash.class);

    private static final HBaseTestingUtility UTIL = new HBaseTestingUtility();

    private static TableName TABLE_NAME = TableName.valueOf("Backoff");

    private static byte[] CF = Bytes.toBytes("cf");

    private static CountDownLatch ARRIVE = new CountDownLatch(1);

    private static CountDownLatch RESUME = new CountDownLatch(1);

    public static final class DummyServerProcedure extends Procedure<MasterProcedureEnv> implements ServerProcedureInterface {
        private ServerName serverName;

        public DummyServerProcedure() {
        }

        public DummyServerProcedure(ServerName serverName) {
            this.serverName = serverName;
        }

        @Override
        public ServerName getServerName() {
            return serverName;
        }

        @Override
        public boolean hasMetaTableRegion() {
            return false;
        }

        @Override
        public ServerOperationType getServerOperationType() {
            return CRASH_HANDLER;
        }

        @Override
        protected Procedure<MasterProcedureEnv>[] execute(MasterProcedureEnv env) throws InterruptedException, ProcedureSuspendedException, ProcedureYieldException {
            TestCloseRegionWhileRSCrash.ARRIVE.countDown();
            TestCloseRegionWhileRSCrash.RESUME.await();
            return null;
        }

        @Override
        protected LockState acquireLock(final MasterProcedureEnv env) {
            if (env.getProcedureScheduler().waitServerExclusiveLock(this, getServerName())) {
                return LOCK_EVENT_WAIT;
            }
            return LOCK_ACQUIRED;
        }

        @Override
        protected void releaseLock(final MasterProcedureEnv env) {
            env.getProcedureScheduler().wakeServerExclusiveLock(this, getServerName());
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
        }

        @Override
        protected void deserializeStateData(ProcedureStateSerializer serializer) throws IOException {
        }
    }

    @Test
    public void testRetryBackoff() throws IOException, InterruptedException {
        HRegionServer srcRs = TestCloseRegionWhileRSCrash.UTIL.getRSForFirstRegionInTable(TestCloseRegionWhileRSCrash.TABLE_NAME);
        RegionInfo region = srcRs.getRegions(TestCloseRegionWhileRSCrash.TABLE_NAME).get(0).getRegionInfo();
        HRegionServer dstRs = TestCloseRegionWhileRSCrash.UTIL.getOtherRegionServer(srcRs);
        ProcedureExecutor<MasterProcedureEnv> procExec = TestCloseRegionWhileRSCrash.UTIL.getMiniHBaseCluster().getMaster().getMasterProcedureExecutor();
        long dummyProcId = procExec.submitProcedure(new TestCloseRegionWhileRSCrash.DummyServerProcedure(srcRs.getServerName()));
        TestCloseRegionWhileRSCrash.ARRIVE.await();
        TestCloseRegionWhileRSCrash.UTIL.getMiniHBaseCluster().killRegionServer(srcRs.getServerName());
        waitFor(30000, () -> procExec.getProcedures().stream().anyMatch(( p) -> p instanceof ServerCrashProcedure));
        Thread t = new Thread(() -> {
            try {
                TestCloseRegionWhileRSCrash.UTIL.getAdmin().move(region.getEncodedNameAsBytes(), Bytes.toBytes(dstRs.getServerName().getServerName()));
            } catch (IOException e) {
            }
        });
        t.start();
        // wait until we enter the WAITING_TIMEOUT state
        ProcedureTestUtil.waitUntilProcedureWaitingTimeout(TestCloseRegionWhileRSCrash.UTIL, TransitRegionStateProcedure.class, 30000);
        // wait until the timeout value increase three times
        ProcedureTestUtil.waitUntilProcedureTimeoutIncrease(TestCloseRegionWhileRSCrash.UTIL, TransitRegionStateProcedure.class, 3);
        // let's close the connection to make sure that the SCP can not update meta successfully
        TestCloseRegionWhileRSCrash.UTIL.getMiniHBaseCluster().getMaster().getConnection().close();
        TestCloseRegionWhileRSCrash.RESUME.countDown();
        waitFor(30000, () -> procExec.isFinished(dummyProcId));
        Thread.sleep(2000);
        // here we restart
        TestCloseRegionWhileRSCrash.UTIL.getMiniHBaseCluster().stopMaster(0).join();
        TestCloseRegionWhileRSCrash.UTIL.getMiniHBaseCluster().startMaster();
        t.join();
        // Make sure that the region is online, it may not on the original target server, as we will set
        // forceNewPlan to true if there is a server crash
        try (Table table = TestCloseRegionWhileRSCrash.UTIL.getConnection().getTable(TestCloseRegionWhileRSCrash.TABLE_NAME)) {
            table.put(new org.apache.hadoop.hbase.client.Put(Bytes.toBytes(1)).addColumn(TestCloseRegionWhileRSCrash.CF, Bytes.toBytes("cq"), Bytes.toBytes(1)));
        }
    }
}

