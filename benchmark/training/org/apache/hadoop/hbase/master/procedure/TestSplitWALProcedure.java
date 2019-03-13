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
package org.apache.hadoop.hbase.master.procedure;


import HConstants.NO_NONCE;
import java.util.List;
import java.util.Optional;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.HBaseTestingUtility;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Table;
import org.apache.hadoop.hbase.master.HMaster;
import org.apache.hadoop.hbase.master.SplitWALManager;
import org.apache.hadoop.hbase.procedure2.Procedure;
import org.apache.hadoop.hbase.procedure2.ProcedureExecutor;
import org.apache.hadoop.hbase.procedure2.ProcedureTestingUtility;
import org.apache.hadoop.hbase.regionserver.HRegionServer;
import org.apache.hadoop.hbase.testclassification.MasterTests;
import org.apache.hadoop.hbase.testclassification.MediumTests;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.experimental.categories.Category;


@Category({ MasterTests.class, MediumTests.class })
public class TestSplitWALProcedure {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestSplitWALProcedure.class);

    private static HBaseTestingUtility TEST_UTIL;

    private HMaster master;

    private TableName TABLE_NAME;

    private SplitWALManager splitWALManager;

    private byte[] FAMILY;

    @Test
    public void testHandleDeadWorker() throws Exception {
        Table table = TestSplitWALProcedure.TEST_UTIL.createTable(TABLE_NAME, FAMILY, TestSplitWALProcedure.TEST_UTIL.KEYS_FOR_HBA_CREATE_TABLE);
        for (int i = 0; i < 10; i++) {
            TestSplitWALProcedure.TEST_UTIL.loadTable(table, FAMILY);
        }
        HRegionServer testServer = TestSplitWALProcedure.TEST_UTIL.getHBaseCluster().getRegionServer(0);
        ProcedureExecutor<MasterProcedureEnv> masterPE = master.getMasterProcedureExecutor();
        List<FileStatus> wals = splitWALManager.getWALsToSplit(testServer.getServerName(), false);
        Assert.assertEquals(1, wals.size());
        TestSplitWALProcedure.TEST_UTIL.getHBaseCluster().killRegionServer(testServer.getServerName());
        waitFor(30000, () -> master.getProcedures().stream().anyMatch(( procedure) -> procedure instanceof SplitWALProcedure));
        Procedure splitWALProcedure = master.getProcedures().stream().filter(( procedure) -> procedure instanceof SplitWALProcedure).findAny().get();
        Assert.assertNotNull(splitWALProcedure);
        waitFor(5000, () -> (getWorker()) != null);
        TestSplitWALProcedure.TEST_UTIL.getHBaseCluster().killRegionServer(getWorker());
        ProcedureTestingUtility.waitProcedure(masterPE, splitWALProcedure.getProcId());
        Assert.assertTrue(splitWALProcedure.isSuccess());
        ProcedureTestingUtility.waitAllProcedures(masterPE);
    }

    @Test
    public void testMasterRestart() throws Exception {
        Table table = TestSplitWALProcedure.TEST_UTIL.createTable(TABLE_NAME, FAMILY, TestSplitWALProcedure.TEST_UTIL.KEYS_FOR_HBA_CREATE_TABLE);
        for (int i = 0; i < 10; i++) {
            TestSplitWALProcedure.TEST_UTIL.loadTable(table, FAMILY);
        }
        HRegionServer testServer = TestSplitWALProcedure.TEST_UTIL.getHBaseCluster().getRegionServer(0);
        List<FileStatus> wals = splitWALManager.getWALsToSplit(testServer.getServerName(), false);
        Assert.assertEquals(1, wals.size());
        SplitWALProcedure splitWALProcedure = new SplitWALProcedure(wals.get(0).getPath().toString(), testServer.getServerName());
        long pid = ProcedureTestingUtility.submitProcedure(master.getMasterProcedureExecutor(), splitWALProcedure, NO_NONCE, NO_NONCE);
        waitFor(5000, () -> (splitWALProcedure.getWorker()) != null);
        // Kill master
        TestSplitWALProcedure.TEST_UTIL.getHBaseCluster().killMaster(master.getServerName());
        TestSplitWALProcedure.TEST_UTIL.getHBaseCluster().waitForMasterToStop(master.getServerName(), 20000);
        // restart master
        TestSplitWALProcedure.TEST_UTIL.getHBaseCluster().startMaster();
        TestSplitWALProcedure.TEST_UTIL.getHBaseCluster().waitForActiveAndReadyMaster();
        this.master = TestSplitWALProcedure.TEST_UTIL.getHBaseCluster().getMaster();
        ProcedureTestingUtility.waitProcedure(master.getMasterProcedureExecutor(), pid);
        Optional<Procedure<?>> procedure = master.getProcedures().stream().filter(( p) -> (p.getProcId()) == pid).findAny();
        // make sure procedure is successful and wal is deleted
        Assert.assertTrue(procedure.isPresent());
        Assert.assertTrue(procedure.get().isSuccess());
        Assert.assertFalse(TestSplitWALProcedure.TEST_UTIL.getTestFileSystem().exists(wals.get(0).getPath()));
    }
}

