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


import java.util.List;
import java.util.Optional;
import java.util.concurrent.CountDownLatch;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.HBaseTestingUtility;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Admin;
import org.apache.hadoop.hbase.client.Mutation;
import org.apache.hadoop.hbase.client.RegionInfo;
import org.apache.hadoop.hbase.coprocessor.MasterCoprocessor;
import org.apache.hadoop.hbase.coprocessor.MasterCoprocessorEnvironment;
import org.apache.hadoop.hbase.coprocessor.MasterObserver;
import org.apache.hadoop.hbase.coprocessor.ObserverContext;
import org.apache.hadoop.hbase.testclassification.MasterTests;
import org.apache.hadoop.hbase.testclassification.MediumTests;
import org.apache.hadoop.hbase.util.Bytes;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Category({ MasterTests.class, MediumTests.class })
public class TestMasterAbortWhileMergingTable {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestMasterAbortWhileMergingTable.class);

    private static final Logger LOG = LoggerFactory.getLogger(TestMasterAbortWhileMergingTable.class);

    protected static final HBaseTestingUtility UTIL = new HBaseTestingUtility();

    private static TableName TABLE_NAME = TableName.valueOf("test");

    private static Admin admin;

    private static byte[] CF = Bytes.toBytes("cf");

    private static byte[] SPLITKEY = Bytes.toBytes("bbbbbbb");

    private static CountDownLatch mergeCommitArrive = new CountDownLatch(1);

    @Test
    public void test() throws Exception {
        List<RegionInfo> regionInfos = TestMasterAbortWhileMergingTable.admin.getRegions(TestMasterAbortWhileMergingTable.TABLE_NAME);
        MergeTableRegionsProcedure mergeTableRegionsProcedure = new MergeTableRegionsProcedure(TestMasterAbortWhileMergingTable.UTIL.getMiniHBaseCluster().getMaster().getMasterProcedureExecutor().getEnvironment(), regionInfos.get(0), regionInfos.get(1));
        long procID = TestMasterAbortWhileMergingTable.UTIL.getMiniHBaseCluster().getMaster().getMasterProcedureExecutor().submitProcedure(mergeTableRegionsProcedure);
        TestMasterAbortWhileMergingTable.mergeCommitArrive.await();
        TestMasterAbortWhileMergingTable.UTIL.getMiniHBaseCluster().stopMaster(0);
        TestMasterAbortWhileMergingTable.UTIL.getMiniHBaseCluster().startMaster();
        // wait until master initialized
        waitFor(30000, () -> ((UTIL.getMiniHBaseCluster().getMaster()) != null) && (UTIL.getMiniHBaseCluster().getMaster().isInitialized()));
        waitFor(30000, () -> UTIL.getMiniHBaseCluster().getMaster().getMasterProcedureExecutor().isFinished(procID));
        Assert.assertTrue("Found region RIT, that's impossible!", ((TestMasterAbortWhileMergingTable.UTIL.getMiniHBaseCluster().getMaster().getAssignmentManager().getRegionsInTransition().size()) == 0));
    }

    public static class MergeRegionObserver implements MasterCoprocessor , MasterObserver {
        @Override
        public Optional<MasterObserver> getMasterObserver() {
            return Optional.of(this);
        }

        @Override
        public void preMergeRegionsCommitAction(ObserverContext<MasterCoprocessorEnvironment> ctx, RegionInfo[] regionsToMerge, List<Mutation> metaEntries) {
            TestMasterAbortWhileMergingTable.mergeCommitArrive.countDown();
            TestMasterAbortWhileMergingTable.LOG.error("mergeCommitArrive countdown");
        }
    }
}

