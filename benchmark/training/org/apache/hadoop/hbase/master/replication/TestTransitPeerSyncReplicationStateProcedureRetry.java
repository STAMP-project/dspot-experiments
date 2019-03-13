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
package org.apache.hadoop.hbase.master.replication;


import SyncReplicationState.ACTIVE;
import SyncReplicationState.DOWNGRADE_ACTIVE;
import SyncReplicationState.STANDBY;
import java.io.IOException;
import java.io.UncheckedIOException;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.master.HMaster;
import org.apache.hadoop.hbase.master.procedure.MasterProcedureEnv;
import org.apache.hadoop.hbase.master.procedure.MasterProcedureTestingUtility;
import org.apache.hadoop.hbase.procedure2.ProcedureExecutor;
import org.apache.hadoop.hbase.procedure2.ProcedureTestingUtility;
import org.apache.hadoop.hbase.replication.SyncReplicationTestBase;
import org.apache.hadoop.hbase.testclassification.LargeTests;
import org.apache.hadoop.hbase.testclassification.MasterTests;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.experimental.categories.Category;


@Category({ MasterTests.class, LargeTests.class })
public class TestTransitPeerSyncReplicationStateProcedureRetry extends SyncReplicationTestBase {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestTransitPeerSyncReplicationStateProcedureRetry.class);

    @Test
    public void testRecoveryAndDoubleExecution() throws Exception {
        SyncReplicationTestBase.UTIL2.getAdmin().transitReplicationPeerSyncReplicationState(SyncReplicationTestBase.PEER_ID, STANDBY);
        SyncReplicationTestBase.UTIL1.getAdmin().transitReplicationPeerSyncReplicationState(SyncReplicationTestBase.PEER_ID, ACTIVE);
        SyncReplicationTestBase.UTIL1.getAdmin().disableReplicationPeer(SyncReplicationTestBase.PEER_ID);
        write(SyncReplicationTestBase.UTIL1, 0, 100);
        Thread.sleep(2000);
        // peer is disabled so no data have been replicated
        verifyNotReplicatedThroughRegion(SyncReplicationTestBase.UTIL2, 0, 100);
        // transit the A to DA first to avoid too many error logs.
        SyncReplicationTestBase.UTIL1.getAdmin().transitReplicationPeerSyncReplicationState(SyncReplicationTestBase.PEER_ID, DOWNGRADE_ACTIVE);
        HMaster master = SyncReplicationTestBase.UTIL2.getHBaseCluster().getMaster();
        ProcedureExecutor<MasterProcedureEnv> procExec = master.getMasterProcedureExecutor();
        // Enable test flags and then queue the procedure.
        ProcedureTestingUtility.waitNoProcedureRunning(procExec);
        ProcedureTestingUtility.setKillAndToggleBeforeStoreUpdate(procExec, true);
        Thread t = new Thread() {
            @Override
            public void run() {
                try {
                    SyncReplicationTestBase.UTIL2.getAdmin().transitReplicationPeerSyncReplicationState(SyncReplicationTestBase.PEER_ID, DOWNGRADE_ACTIVE);
                } catch (IOException e) {
                    throw new UncheckedIOException(e);
                }
            }
        };
        t.start();
        waitFor(30000, () -> procExec.getProcedures().stream().anyMatch(( p) -> (p instanceof TransitPeerSyncReplicationStateProcedure) && (!(p.isFinished()))));
        long procId = procExec.getProcedures().stream().filter(( p) -> (p instanceof TransitPeerSyncReplicationStateProcedure) && (!(p.isFinished()))).mapToLong(Procedure::getProcId).min().getAsLong();
        MasterProcedureTestingUtility.testRecoveryAndDoubleExecution(procExec, procId);
        ProcedureTestingUtility.setKillAndToggleBeforeStoreUpdate(procExec, false);
        Assert.assertEquals(DOWNGRADE_ACTIVE, SyncReplicationTestBase.UTIL2.getAdmin().getReplicationPeerSyncReplicationState(SyncReplicationTestBase.PEER_ID));
        verify(SyncReplicationTestBase.UTIL2, 0, 100);
    }
}

