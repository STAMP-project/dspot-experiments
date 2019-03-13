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


import RegionInfoBuilder.FIRST_META_REGIONINFO;
import State.CLOSED;
import State.OPEN;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.HBaseTestingUtility;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.RegionInfo;
import org.apache.hadoop.hbase.testclassification.LargeTests;
import org.apache.hadoop.hbase.testclassification.MasterTests;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.apache.hadoop.hbase.master.assignment.TestAssignmentManagerBase.HangOnCloseThenRSCrashExecutor.TYPES_OF_FAILURE;
import static org.apache.hadoop.hbase.master.assignment.TestAssignmentManagerBase.MockRSProcedureDispatcher.<init>;


@Category({ MasterTests.class, LargeTests.class })
public class TestAssignmentManager extends TestAssignmentManagerBase {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestAssignmentManager.class);

    private static final Logger LOG = LoggerFactory.getLogger(TestAssignmentManager.class);

    @Test
    public void testAssignWithGoodExec() throws Exception {
        // collect AM metrics before test
        collectAssignmentManagerMetrics();
        testAssign(new TestAssignmentManagerBase.GoodRsExecutor());
        Assert.assertEquals(((assignSubmittedCount) + (TestAssignmentManagerBase.NREGIONS)), assignProcMetrics.getSubmittedCounter().getCount());
        Assert.assertEquals(assignFailedCount, assignProcMetrics.getFailedCounter().getCount());
    }

    @Test
    public void testAssignAndCrashBeforeResponse() throws Exception {
        TableName tableName = TableName.valueOf("testAssignAndCrashBeforeResponse");
        RegionInfo hri = createRegionInfo(tableName, 1);
        rsDispatcher.setMockRsExecutor(new TestAssignmentManagerBase.HangThenRSCrashExecutor());
        TransitRegionStateProcedure proc = createAssignProcedure(hri);
        waitOnFuture(submitProcedure(proc));
    }

    @Test
    public void testUnassignAndCrashBeforeResponse() throws Exception {
        TableName tableName = TableName.valueOf("testAssignAndCrashBeforeResponse");
        RegionInfo hri = createRegionInfo(tableName, 1);
        rsDispatcher.setMockRsExecutor(new TestAssignmentManagerBase.HangOnCloseThenRSCrashExecutor());
        for (int i = 0; i < (TYPES_OF_FAILURE); i++) {
            TransitRegionStateProcedure assign = createAssignProcedure(hri);
            waitOnFuture(submitProcedure(assign));
            TransitRegionStateProcedure unassign = createUnassignProcedure(hri);
            waitOnFuture(submitProcedure(unassign));
        }
    }

    @Test
    public void testAssignSocketTimeout() throws Exception {
        TableName tableName = TableName.valueOf(this.name.getMethodName());
        RegionInfo hri = createRegionInfo(tableName, 1);
        // collect AM metrics before test
        collectAssignmentManagerMetrics();
        rsDispatcher.setMockRsExecutor(new TestAssignmentManagerBase.SocketTimeoutRsExecutor(20));
        waitOnFuture(submitProcedure(createAssignProcedure(hri)));
        Assert.assertEquals(((assignSubmittedCount) + 1), assignProcMetrics.getSubmittedCounter().getCount());
        Assert.assertEquals(assignFailedCount, assignProcMetrics.getFailedCounter().getCount());
    }

    @Test
    public void testAssignQueueFullOnce() throws Exception {
        TableName tableName = TableName.valueOf(this.name.getMethodName());
        RegionInfo hri = createRegionInfo(tableName, 1);
        // collect AM metrics before test
        collectAssignmentManagerMetrics();
        rsDispatcher.setMockRsExecutor(new TestAssignmentManagerBase.CallQueueTooBigOnceRsExecutor());
        waitOnFuture(submitProcedure(createAssignProcedure(hri)));
        Assert.assertEquals(((assignSubmittedCount) + 1), assignProcMetrics.getSubmittedCounter().getCount());
        Assert.assertEquals(assignFailedCount, assignProcMetrics.getFailedCounter().getCount());
    }

    @Test
    public void testTimeoutThenQueueFull() throws Exception {
        TableName tableName = TableName.valueOf(this.name.getMethodName());
        RegionInfo hri = createRegionInfo(tableName, 1);
        // collect AM metrics before test
        collectAssignmentManagerMetrics();
        rsDispatcher.setMockRsExecutor(new TestAssignmentManagerBase.TimeoutThenCallQueueTooBigRsExecutor(10));
        waitOnFuture(submitProcedure(createAssignProcedure(hri)));
        rsDispatcher.setMockRsExecutor(new TestAssignmentManagerBase.TimeoutThenCallQueueTooBigRsExecutor(15));
        waitOnFuture(submitProcedure(createUnassignProcedure(hri)));
        Assert.assertEquals(((assignSubmittedCount) + 1), assignProcMetrics.getSubmittedCounter().getCount());
        Assert.assertEquals(assignFailedCount, assignProcMetrics.getFailedCounter().getCount());
        Assert.assertEquals(((unassignSubmittedCount) + 1), unassignProcMetrics.getSubmittedCounter().getCount());
        Assert.assertEquals(unassignFailedCount, unassignProcMetrics.getFailedCounter().getCount());
    }

    @Test
    public void testAssignAnAssignedRegion() throws Exception {
        final TableName tableName = TableName.valueOf("testAssignAnAssignedRegion");
        final RegionInfo hri = createRegionInfo(tableName, 1);
        // collect AM metrics before test
        collectAssignmentManagerMetrics();
        rsDispatcher.setMockRsExecutor(new TestAssignmentManagerBase.GoodRsExecutor());
        Future<byte[]> futureA = submitProcedure(createAssignProcedure(hri));
        // wait first assign
        waitOnFuture(futureA);
        am.getRegionStates().isRegionInState(hri, OPEN);
        // Second should be a noop. We should recognize region is already OPEN internally
        // and skip out doing nothing.
        // wait second assign
        Future<byte[]> futureB = submitProcedure(createAssignProcedure(hri));
        waitOnFuture(futureB);
        am.getRegionStates().isRegionInState(hri, OPEN);
        // TODO: What else can we do to ensure just a noop.
        // TODO: Though second assign is noop, it's considered success, can noop be handled in a
        // better way?
        Assert.assertEquals(((assignSubmittedCount) + 2), assignProcMetrics.getSubmittedCounter().getCount());
        Assert.assertEquals(assignFailedCount, assignProcMetrics.getFailedCounter().getCount());
    }

    @Test
    public void testUnassignAnUnassignedRegion() throws Exception {
        final TableName tableName = TableName.valueOf("testUnassignAnUnassignedRegion");
        final RegionInfo hri = createRegionInfo(tableName, 1);
        // collect AM metrics before test
        collectAssignmentManagerMetrics();
        rsDispatcher.setMockRsExecutor(new TestAssignmentManagerBase.GoodRsExecutor());
        // assign the region first
        waitOnFuture(submitProcedure(createAssignProcedure(hri)));
        final Future<byte[]> futureA = submitProcedure(createUnassignProcedure(hri));
        // Wait first unassign.
        waitOnFuture(futureA);
        am.getRegionStates().isRegionInState(hri, CLOSED);
        // Second should be a noop. We should recognize region is already CLOSED internally
        // and skip out doing nothing.
        final Future<byte[]> futureB = submitProcedure(createUnassignProcedure(hri));
        waitOnFuture(futureB);
        // Ensure we are still CLOSED.
        am.getRegionStates().isRegionInState(hri, CLOSED);
        // TODO: What else can we do to ensure just a noop.
        Assert.assertEquals(((assignSubmittedCount) + 1), assignProcMetrics.getSubmittedCounter().getCount());
        Assert.assertEquals(assignFailedCount, assignProcMetrics.getFailedCounter().getCount());
        // TODO: Though second unassign is noop, it's considered success, can noop be handled in a
        // better way?
        Assert.assertEquals(((unassignSubmittedCount) + 2), unassignProcMetrics.getSubmittedCounter().getCount());
        Assert.assertEquals(unassignFailedCount, unassignProcMetrics.getFailedCounter().getCount());
    }

    /**
     * It is possible that when AM send assign meta request to a RS successfully, but RS can not send
     * back any response, which cause master startup hangs forever
     */
    @Test
    public void testAssignMetaAndCrashBeforeResponse() throws Exception {
        tearDown();
        // See setUp(), start HBase until set up meta
        util = new HBaseTestingUtility();
        this.executor = Executors.newSingleThreadScheduledExecutor();
        setupConfiguration(util.getConfiguration());
        master = new MockMasterServices(util.getConfiguration(), this.regionsToRegionServers);
        rsDispatcher = new TestAssignmentManagerBase.MockRSProcedureDispatcher(master);
        master.start(TestAssignmentManagerBase.NSERVERS, rsDispatcher);
        am = master.getAssignmentManager();
        // Assign meta
        rsDispatcher.setMockRsExecutor(new TestAssignmentManagerBase.HangThenRSRestartExecutor());
        am.assign(FIRST_META_REGIONINFO);
        Assert.assertEquals(true, am.isMetaAssigned());
        // set it back as default, see setUpMeta()
        am.wakeMetaLoadedEvent();
    }

    @Test
    public void testMove() throws Exception {
        TableName tableName = TableName.valueOf("testMove");
        RegionInfo hri = createRegionInfo(tableName, 1);
        rsDispatcher.setMockRsExecutor(new TestAssignmentManagerBase.GoodRsExecutor());
        am.assign(hri);
        // collect AM metrics before test
        collectAssignmentManagerMetrics();
        am.move(hri);
        Assert.assertEquals(((moveSubmittedCount) + 1), moveProcMetrics.getSubmittedCounter().getCount());
        Assert.assertEquals(moveFailedCount, moveProcMetrics.getFailedCounter().getCount());
        assertCloseThenOpen();
    }

    @Test
    public void testReopen() throws Exception {
        TableName tableName = TableName.valueOf("testReopen");
        RegionInfo hri = createRegionInfo(tableName, 1);
        rsDispatcher.setMockRsExecutor(new TestAssignmentManagerBase.GoodRsExecutor());
        am.assign(hri);
        // collect AM metrics before test
        collectAssignmentManagerMetrics();
        TransitRegionStateProcedure proc = TransitRegionStateProcedure.reopen(master.getMasterProcedureExecutor().getEnvironment(), hri);
        am.getRegionStates().getRegionStateNode(hri).setProcedure(proc);
        waitOnFuture(submitProcedure(proc));
        Assert.assertEquals(((reopenSubmittedCount) + 1), reopenProcMetrics.getSubmittedCounter().getCount());
        Assert.assertEquals(reopenFailedCount, reopenProcMetrics.getFailedCounter().getCount());
        assertCloseThenOpen();
    }
}

