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
package org.apache.hadoop.hbase.client;


import TableState.State;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.HBaseTestingUtility;
import org.apache.hadoop.hbase.ServerName;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.master.RegionState;
import org.apache.hadoop.hbase.master.procedure.MasterProcedureEnv;
import org.apache.hadoop.hbase.master.procedure.TableProcedureInterface;
import org.apache.hadoop.hbase.procedure2.Procedure;
import org.apache.hadoop.hbase.procedure2.Procedure.NO_PROC_ID;
import org.apache.hadoop.hbase.procedure2.ProcedureExecutor;
import org.apache.hadoop.hbase.procedure2.ProcedureSuspendedException;
import org.apache.hadoop.hbase.procedure2.ProcedureTestingUtility;
import org.apache.hadoop.hbase.regionserver.HRegionServer;
import org.apache.hadoop.hbase.shaded.protobuf.ProtobufUtil;
import org.apache.hadoop.hbase.testclassification.ClientTests;
import org.apache.hadoop.hbase.testclassification.LargeTests;
import org.apache.hadoop.hbase.util.Bytes;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.TestName;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static TableOperationType.READ;


/**
 * Class to test HBaseHbck. Spins up the minicluster once at test start and then takes it down
 * afterward. Add any testing of HBaseHbck functionality here.
 */
@RunWith(Parameterized.class)
@Category({ LargeTests.class, ClientTests.class })
public class TestHbck {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestHbck.class);

    private static final Logger LOG = LoggerFactory.getLogger(TestHbck.class);

    private static final HBaseTestingUtility TEST_UTIL = new HBaseTestingUtility();

    @Rule
    public TestName name = new TestName();

    @Parameterized.Parameter
    public boolean async;

    private static final TableName TABLE_NAME = TableName.valueOf(TestHbck.class.getSimpleName());

    private static ProcedureExecutor<MasterProcedureEnv> procExec;

    private static AsyncConnection ASYNC_CONN;

    public static class SuspendProcedure extends ProcedureTestingUtility.NoopProcedure<MasterProcedureEnv> implements TableProcedureInterface {
        public SuspendProcedure() {
            super();
        }

        @SuppressWarnings({ "rawtypes", "unchecked" })
        @Override
        protected Procedure[] execute(final MasterProcedureEnv env) throws ProcedureSuspendedException {
            // Always suspend the procedure
            throw new ProcedureSuspendedException();
        }

        @Override
        public TableName getTableName() {
            return TestHbck.TABLE_NAME;
        }

        @Override
        public TableOperationType getTableOperationType() {
            return READ;
        }
    }

    @Test
    public void testBypassProcedure() throws Exception {
        // SuspendProcedure
        final TestHbck.SuspendProcedure proc = new TestHbck.SuspendProcedure();
        long procId = TestHbck.procExec.submitProcedure(proc);
        Thread.sleep(500);
        // bypass the procedure
        List<Long> pids = Arrays.<Long>asList(procId);
        List<Boolean> results = getHbck().bypassProcedure(pids, 30000, false, false);
        Assert.assertTrue("Failed to by pass procedure!", results.get(0));
        waitFor(5000, () -> (proc.isSuccess()) && (proc.isBypass()));
        TestHbck.LOG.info("{} finished", proc);
    }

    @Test
    public void testSetTableStateInMeta() throws Exception {
        Hbck hbck = getHbck();
        // set table state to DISABLED
        hbck.setTableStateInMeta(new TableState(TestHbck.TABLE_NAME, State.DISABLED));
        // Method {@link Hbck#setTableStateInMeta()} returns previous state, which in this case
        // will be DISABLED
        TableState prevState = hbck.setTableStateInMeta(new TableState(TestHbck.TABLE_NAME, State.ENABLED));
        Assert.assertTrue(("Incorrect previous state! expeced=DISABLED, found=" + (prevState.getState())), prevState.isDisabled());
    }

    @Test
    public void testAssigns() throws Exception {
        Hbck hbck = getHbck();
        try (Admin admin = TestHbck.TEST_UTIL.getConnection().getAdmin()) {
            List<RegionInfo> regions = admin.getRegions(TestHbck.TABLE_NAME);
            for (RegionInfo ri : regions) {
                RegionState rs = TestHbck.TEST_UTIL.getHBaseCluster().getMaster().getAssignmentManager().getRegionStates().getRegionState(ri.getEncodedName());
                TestHbck.LOG.info("RS: {}", rs.toString());
            }
            List<Long> pids = hbck.unassigns(regions.stream().map(( r) -> r.getEncodedName()).collect(Collectors.toList()));
            waitOnPids(pids);
            for (RegionInfo ri : regions) {
                RegionState rs = TestHbck.TEST_UTIL.getHBaseCluster().getMaster().getAssignmentManager().getRegionStates().getRegionState(ri.getEncodedName());
                TestHbck.LOG.info("RS: {}", rs.toString());
                Assert.assertTrue(rs.toString(), rs.isClosed());
            }
            pids = hbck.assigns(regions.stream().map(( r) -> r.getEncodedName()).collect(Collectors.toList()));
            waitOnPids(pids);
            for (RegionInfo ri : regions) {
                RegionState rs = TestHbck.TEST_UTIL.getHBaseCluster().getMaster().getAssignmentManager().getRegionStates().getRegionState(ri.getEncodedName());
                TestHbck.LOG.info("RS: {}", rs.toString());
                Assert.assertTrue(rs.toString(), rs.isOpened());
            }
            // What happens if crappy region list passed?
            pids = hbck.assigns(Arrays.stream(new String[]{ "a", "some rubbish name" }).collect(Collectors.toList()));
            for (long pid : pids) {
                Assert.assertEquals(NO_PROC_ID, pid);
            }
        }
    }

    @Test
    public void testScheduleSCP() throws Exception {
        HRegionServer testRs = TestHbck.TEST_UTIL.getRSForFirstRegionInTable(TestHbck.TABLE_NAME);
        TestHbck.TEST_UTIL.loadTable(TestHbck.TEST_UTIL.getConnection().getTable(TestHbck.TABLE_NAME), Bytes.toBytes("family1"), true);
        ServerName serverName = testRs.getServerName();
        Hbck hbck = getHbck();
        List<Long> pids = hbck.scheduleServerCrashProcedure(Arrays.asList(ProtobufUtil.toServerName(serverName)));
        Assert.assertTrue(((pids.get(0)) > 0));
        TestHbck.LOG.info("pid is {}", pids.get(0));
        List<Long> newPids = hbck.scheduleServerCrashProcedure(Arrays.asList(ProtobufUtil.toServerName(serverName)));
        Assert.assertTrue(((newPids.get(0)) < 0));
        TestHbck.LOG.info("pid is {}", newPids.get(0));
        waitOnPids(pids);
    }
}

