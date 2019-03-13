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
package org.apache.hadoop.hbase.master.locking;


import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.HBaseTestingUtility;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.RegionInfo;
import org.apache.hadoop.hbase.master.MasterRpcServices;
import org.apache.hadoop.hbase.master.procedure.MasterProcedureEnv;
import org.apache.hadoop.hbase.procedure2.LockType;
import org.apache.hadoop.hbase.procedure2.Procedure;
import org.apache.hadoop.hbase.procedure2.ProcedureExecutor;
import org.apache.hadoop.hbase.procedure2.ProcedureTestingUtility;
import org.apache.hadoop.hbase.shaded.protobuf.generated.LockServiceProtos.LockRequest;
import org.apache.hadoop.hbase.testclassification.MasterTests;
import org.apache.hadoop.hbase.testclassification.MediumTests;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.ExpectedException;
import org.junit.rules.TestName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Category({ MasterTests.class, MediumTests.class })
public class TestLockProcedure {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestLockProcedure.class);

    @Rule
    public final ExpectedException exception = ExpectedException.none();

    @Rule
    public TestName testName = new TestName();

    // crank this up if this test turns out to be flaky.
    private static final int HEARTBEAT_TIMEOUT = 2000;

    private static final int LOCAL_LOCKS_TIMEOUT = 4000;

    private static final Logger LOG = LoggerFactory.getLogger(TestLockProcedure.class);

    protected static final HBaseTestingUtility UTIL = new HBaseTestingUtility();

    private static MasterRpcServices masterRpcService;

    private static ProcedureExecutor<MasterProcedureEnv> procExec;

    private static String namespace = "namespace";

    private static TableName tableName1 = TableName.valueOf(TestLockProcedure.namespace, "table1");

    private static List<RegionInfo> tableRegions1;

    private static TableName tableName2 = TableName.valueOf(TestLockProcedure.namespace, "table2");

    private static List<RegionInfo> tableRegions2;

    private String testMethodName;

    @Test
    public void testLockRequestValidationEmptyDescription() throws Exception {
        validateLockRequestException(getNamespaceLock("", ""), "Empty description");
    }

    @Test
    public void testLockRequestValidationEmptyNamespaceName() throws Exception {
        validateLockRequestException(getNamespaceLock("", "desc"), "Empty namespace");
    }

    @Test
    public void testLockRequestValidationRegionsFromDifferentTable() throws Exception {
        List<RegionInfo> regions = new ArrayList<>();
        regions.addAll(TestLockProcedure.tableRegions1);
        regions.addAll(TestLockProcedure.tableRegions2);
        validateLockRequestException(getRegionLock(regions, "desc"), "All regions should be from same table");
    }

    @Test
    public void testUpdateHeartbeatAndUnlockForTable() throws Exception {
        LockRequest lock = getTableExclusiveLock(TestLockProcedure.tableName1, testMethodName);
        final long procId = queueLock(lock);
        Assert.assertTrue(awaitForLocked(procId, 2000));
        Thread.sleep(((TestLockProcedure.HEARTBEAT_TIMEOUT) / 2));
        sendHeartbeatAndCheckLocked(procId, true);
        Thread.sleep(((TestLockProcedure.HEARTBEAT_TIMEOUT) / 2));
        sendHeartbeatAndCheckLocked(procId, true);
        Thread.sleep(((TestLockProcedure.HEARTBEAT_TIMEOUT) / 2));
        sendHeartbeatAndCheckLocked(procId, true);
        releaseLock(procId);
        sendHeartbeatAndCheckLocked(procId, false);
        ProcedureTestingUtility.waitProcedure(TestLockProcedure.procExec, procId);
        ProcedureTestingUtility.assertProcNotFailed(TestLockProcedure.procExec, procId);
    }

    @Test
    public void testAbort() throws Exception {
        LockRequest lock = getTableExclusiveLock(TestLockProcedure.tableName1, testMethodName);
        final long procId = queueLock(lock);
        Assert.assertTrue(awaitForLocked(procId, 2000));
        Assert.assertTrue(TestLockProcedure.procExec.abort(procId));
        sendHeartbeatAndCheckLocked(procId, false);
        ProcedureTestingUtility.waitProcedure(TestLockProcedure.procExec, procId);
        ProcedureTestingUtility.assertProcNotFailed(TestLockProcedure.procExec, procId);
    }

    @Test
    public void testUpdateHeartbeatAndUnlockForNamespace() throws Exception {
        LockRequest lock = getNamespaceLock(TestLockProcedure.namespace, testMethodName);
        final long procId = queueLock(lock);
        Assert.assertTrue(awaitForLocked(procId, 2000));
        Thread.sleep(((TestLockProcedure.HEARTBEAT_TIMEOUT) / 2));
        sendHeartbeatAndCheckLocked(procId, true);
        Thread.sleep(((TestLockProcedure.HEARTBEAT_TIMEOUT) / 2));
        sendHeartbeatAndCheckLocked(procId, true);
        Thread.sleep(((TestLockProcedure.HEARTBEAT_TIMEOUT) / 2));
        sendHeartbeatAndCheckLocked(procId, true);
        releaseLock(procId);
        sendHeartbeatAndCheckLocked(procId, false);
        ProcedureTestingUtility.waitProcedure(TestLockProcedure.procExec, procId);
        ProcedureTestingUtility.assertProcNotFailed(TestLockProcedure.procExec, procId);
    }

    @Test
    public void testTimeout() throws Exception {
        LockRequest lock = getNamespaceLock(TestLockProcedure.namespace, testMethodName);
        final long procId = queueLock(lock);
        Assert.assertTrue(awaitForLocked(procId, 2000));
        Thread.sleep(((TestLockProcedure.HEARTBEAT_TIMEOUT) / 2));
        sendHeartbeatAndCheckLocked(procId, true);
        Thread.sleep(((TestLockProcedure.HEARTBEAT_TIMEOUT) / 2));
        sendHeartbeatAndCheckLocked(procId, true);
        Thread.sleep((4 * (TestLockProcedure.HEARTBEAT_TIMEOUT)));
        sendHeartbeatAndCheckLocked(procId, false);
        ProcedureTestingUtility.waitProcedure(TestLockProcedure.procExec, procId);
        ProcedureTestingUtility.assertProcNotFailed(TestLockProcedure.procExec, procId);
    }

    @Test
    public void testMultipleLocks() throws Exception {
        LockRequest nsLock = getNamespaceLock(TestLockProcedure.namespace, testMethodName);
        LockRequest tableLock1 = getTableExclusiveLock(TestLockProcedure.tableName1, testMethodName);
        LockRequest tableLock2 = getTableExclusiveLock(TestLockProcedure.tableName2, testMethodName);
        LockRequest regionsLock1 = getRegionLock(TestLockProcedure.tableRegions1, testMethodName);
        LockRequest regionsLock2 = getRegionLock(TestLockProcedure.tableRegions2, testMethodName);
        // Acquire namespace lock, then queue other locks.
        long nsProcId = queueLock(nsLock);
        Assert.assertTrue(awaitForLocked(nsProcId, 2000));
        long start = System.currentTimeMillis();
        sendHeartbeatAndCheckLocked(nsProcId, true);
        long table1ProcId = queueLock(tableLock1);
        long table2ProcId = queueLock(tableLock2);
        long regions1ProcId = queueLock(regionsLock1);
        long regions2ProcId = queueLock(regionsLock2);
        // Assert tables & region locks are waiting because of namespace lock.
        long now = System.currentTimeMillis();
        // leave extra 10 msec in case more than half the HEARTBEAT_TIMEOUT has passed
        Thread.sleep(Math.min(((TestLockProcedure.HEARTBEAT_TIMEOUT) / 2), Math.max((((TestLockProcedure.HEARTBEAT_TIMEOUT) - (now - start)) - 10), 0)));
        sendHeartbeatAndCheckLocked(nsProcId, true);
        sendHeartbeatAndCheckLocked(table1ProcId, false);
        sendHeartbeatAndCheckLocked(table2ProcId, false);
        sendHeartbeatAndCheckLocked(regions1ProcId, false);
        sendHeartbeatAndCheckLocked(regions2ProcId, false);
        // Release namespace lock and assert tables locks are acquired but not region lock
        releaseLock(nsProcId);
        Assert.assertTrue(awaitForLocked(table1ProcId, 2000));
        Assert.assertTrue(awaitForLocked(table2ProcId, 2000));
        sendHeartbeatAndCheckLocked(regions1ProcId, false);
        sendHeartbeatAndCheckLocked(regions2ProcId, false);
        // Release table1 lock and assert region lock is acquired.
        releaseLock(table1ProcId);
        sendHeartbeatAndCheckLocked(table1ProcId, false);
        Assert.assertTrue(awaitForLocked(regions1ProcId, 2000));
        sendHeartbeatAndCheckLocked(table2ProcId, true);
        sendHeartbeatAndCheckLocked(regions2ProcId, false);
        // Release table2 lock and assert region lock is acquired.
        releaseLock(table2ProcId);
        sendHeartbeatAndCheckLocked(table2ProcId, false);
        Assert.assertTrue(awaitForLocked(regions2ProcId, 2000));
        sendHeartbeatAndCheckLocked(regions1ProcId, true);
        sendHeartbeatAndCheckLocked(regions2ProcId, true);
        // Release region locks.
        releaseLock(regions1ProcId);
        releaseLock(regions2ProcId);
        sendHeartbeatAndCheckLocked(regions1ProcId, false);
        sendHeartbeatAndCheckLocked(regions2ProcId, false);
        ProcedureTestingUtility.waitAllProcedures(TestLockProcedure.procExec);
        ProcedureTestingUtility.assertProcNotFailed(TestLockProcedure.procExec, nsProcId);
        ProcedureTestingUtility.assertProcNotFailed(TestLockProcedure.procExec, table1ProcId);
        ProcedureTestingUtility.assertProcNotFailed(TestLockProcedure.procExec, table2ProcId);
        ProcedureTestingUtility.assertProcNotFailed(TestLockProcedure.procExec, regions1ProcId);
        ProcedureTestingUtility.assertProcNotFailed(TestLockProcedure.procExec, regions2ProcId);
    }

    // Test latch is decreased in count when lock is acquired.
    @Test
    public void testLatch() throws Exception {
        CountDownLatch latch = new CountDownLatch(1);
        // MasterRpcServices don't set latch with LockProcedure, so create one and submit it directly.
        LockProcedure lockProc = new LockProcedure(TestLockProcedure.UTIL.getConfiguration(), TableName.valueOf("table"), LockType.EXCLUSIVE, "desc", latch);
        TestLockProcedure.procExec.submitProcedure(lockProc);
        Assert.assertTrue(latch.await(2000, TimeUnit.MILLISECONDS));
        releaseLock(lockProc.getProcId());
        ProcedureTestingUtility.waitProcedure(TestLockProcedure.procExec, lockProc.getProcId());
        ProcedureTestingUtility.assertProcNotFailed(TestLockProcedure.procExec, lockProc.getProcId());
    }

    // LockProcedures with latch are considered local locks.
    @Test
    public void testLocalLockTimeout() throws Exception {
        CountDownLatch latch = new CountDownLatch(1);
        // MasterRpcServices don't set latch with LockProcedure, so create one and submit it directly.
        LockProcedure lockProc = new LockProcedure(TestLockProcedure.UTIL.getConfiguration(), TableName.valueOf("table"), LockType.EXCLUSIVE, "desc", latch);
        TestLockProcedure.procExec.submitProcedure(lockProc);
        Assert.assertTrue(awaitForLocked(lockProc.getProcId(), 2000));
        Thread.sleep(((TestLockProcedure.LOCAL_LOCKS_TIMEOUT) / 2));
        Assert.assertTrue(lockProc.isLocked());
        Thread.sleep((2 * (TestLockProcedure.LOCAL_LOCKS_TIMEOUT)));
        Assert.assertFalse(lockProc.isLocked());
        releaseLock(lockProc.getProcId());
        ProcedureTestingUtility.waitProcedure(TestLockProcedure.procExec, lockProc.getProcId());
        ProcedureTestingUtility.assertProcNotFailed(TestLockProcedure.procExec, lockProc.getProcId());
    }

    @Test
    public void testRemoteTableLockRecovery() throws Exception {
        LockRequest lock = getTableExclusiveLock(TestLockProcedure.tableName1, testMethodName);
        testRemoteLockRecovery(lock);
    }

    @Test
    public void testRemoteNamespaceLockRecovery() throws Exception {
        LockRequest lock = getNamespaceLock(TestLockProcedure.namespace, testMethodName);
        testRemoteLockRecovery(lock);
    }

    @Test
    public void testRemoteRegionLockRecovery() throws Exception {
        LockRequest lock = getRegionLock(TestLockProcedure.tableRegions1, testMethodName);
        testRemoteLockRecovery(lock);
    }

    @Test
    public void testLocalMasterLockRecovery() throws Exception {
        ProcedureTestingUtility.setKillAndToggleBeforeStoreUpdate(TestLockProcedure.procExec, true);
        CountDownLatch latch = new CountDownLatch(1);
        LockProcedure lockProc = new LockProcedure(TestLockProcedure.UTIL.getConfiguration(), TableName.valueOf("table"), LockType.EXCLUSIVE, "desc", latch);
        TestLockProcedure.procExec.submitProcedure(lockProc);
        Assert.assertTrue(latch.await(2000, TimeUnit.MILLISECONDS));
        // wait for proc Executor to die, then restart it and wait for Lock Procedure to get started.
        ProcedureTestingUtility.waitProcedure(TestLockProcedure.procExec, lockProc.getProcId());
        Assert.assertEquals(false, TestLockProcedure.procExec.isRunning());
        ProcedureTestingUtility.setKillAndToggleBeforeStoreUpdate(TestLockProcedure.procExec, false);
        // remove zk lock node otherwise recovered lock will keep waiting on it.
        ProcedureTestingUtility.restart(TestLockProcedure.procExec);
        while (!(TestLockProcedure.procExec.isStarted(lockProc.getProcId()))) {
            Thread.sleep(250);
        } 
        Assert.assertEquals(true, TestLockProcedure.procExec.isRunning());
        ProcedureTestingUtility.waitProcedure(TestLockProcedure.procExec, lockProc.getProcId());
        Procedure<?> result = TestLockProcedure.procExec.getResultOrProcedure(lockProc.getProcId());
        Assert.assertTrue(((result != null) && (!(result.isFailed()))));
        ProcedureTestingUtility.assertProcNotFailed(TestLockProcedure.procExec, lockProc.getProcId());
    }
}

