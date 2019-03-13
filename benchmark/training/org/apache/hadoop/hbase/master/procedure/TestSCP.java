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


import Procedure.LockState.LOCK_EVENT_WAIT;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.HBaseTestingUtility;
import org.apache.hadoop.hbase.ServerName;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.RegionInfo;
import org.apache.hadoop.hbase.client.Table;
import org.apache.hadoop.hbase.master.HMaster;
import org.apache.hadoop.hbase.master.assignment.AssignmentTestingUtil;
import org.apache.hadoop.hbase.procedure2.Procedure;
import org.apache.hadoop.hbase.procedure2.ProcedureExecutor;
import org.apache.hadoop.hbase.testclassification.LargeTests;
import org.apache.hadoop.hbase.testclassification.MasterTests;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Category({ MasterTests.class, LargeTests.class })
public class TestSCP {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestSCP.class);

    private static final Logger LOG = LoggerFactory.getLogger(TestSCP.class);

    protected HBaseTestingUtility util;

    @Test
    public void testCrashTargetRs() throws Exception {
        testRecoveryAndDoubleExecution(false, false);
    }

    @Test
    public void testRecoveryAndDoubleExecutionOnRsWithMeta() throws Exception {
        testRecoveryAndDoubleExecution(true, true);
    }

    @Test
    public void testRecoveryAndDoubleExecutionOnRsWithoutMeta() throws Exception {
        testRecoveryAndDoubleExecution(false, true);
    }

    @Test
    public void testConcurrentSCPForSameServer() throws Exception {
        final TableName tableName = TableName.valueOf("testConcurrentSCPForSameServer");
        try (Table t = createTable(tableName)) {
            // Load the table with a bit of data so some logs to split and some edits in each region.
            this.util.loadTable(t, HBaseTestingUtility.COLUMNS[0]);
            final int count = util.countRows(t);
            Assert.assertTrue("expected some rows", (count > 0));
            // find the first server that match the request and executes the test
            ServerName rsToKill = null;
            for (RegionInfo hri : util.getAdmin().getRegions(tableName)) {
                final ServerName serverName = AssignmentTestingUtil.getServerHoldingRegion(util, hri);
                if ((AssignmentTestingUtil.isServerHoldingMeta(util, serverName)) == true) {
                    rsToKill = serverName;
                    break;
                }
            }
            HMaster master = util.getHBaseCluster().getMaster();
            final ProcedureExecutor<MasterProcedureEnv> pExecutor = master.getMasterProcedureExecutor();
            ServerCrashProcedure procB = new ServerCrashProcedure(pExecutor.getEnvironment(), rsToKill, false, false);
            AssignmentTestingUtil.killRs(util, rsToKill);
            long procId = getSCPProcId(pExecutor);
            Procedure procA = pExecutor.getProcedure(procId);
            TestSCP.LOG.info("submit SCP procedureA");
            waitFor(5000, () -> procA.hasLock());
            TestSCP.LOG.info("procedureA acquired the lock");
            Assert.assertEquals(LOCK_EVENT_WAIT, procB.acquireLock(pExecutor.getEnvironment()));
            TestSCP.LOG.info("procedureB should not be able to get the lock");
            waitFor(60000, () -> (procB.acquireLock(pExecutor.getEnvironment())) == Procedure.LockState.LOCK_ACQUIRED);
            TestSCP.LOG.info("when procedure B get the lock, procedure A should be finished");
            Assert.assertTrue(procA.isFinished());
        }
    }
}

