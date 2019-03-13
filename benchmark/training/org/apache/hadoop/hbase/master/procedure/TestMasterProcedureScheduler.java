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


import LockType.EXCLUSIVE;
import LockType.SHARED;
import LockedResourceType.NAMESPACE;
import LockedResourceType.PEER;
import LockedResourceType.REGION;
import LockedResourceType.SERVER;
import LockedResourceType.TABLE;
import TableProcedureInterface.DUMMY_NAMESPACE_TABLE_NAME;
import java.util.Arrays;
import java.util.List;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.ServerName;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.RegionInfo;
import org.apache.hadoop.hbase.client.RegionInfoBuilder;
import org.apache.hadoop.hbase.master.locking.LockProcedure;
import org.apache.hadoop.hbase.master.procedure.TableProcedureInterface.TableOperationType;
import org.apache.hadoop.hbase.procedure2.LockedResource;
import org.apache.hadoop.hbase.procedure2.Procedure;
import org.apache.hadoop.hbase.procedure2.ProcedureEvent;
import org.apache.hadoop.hbase.procedure2.ProcedureTestingUtility.TestProcedure;
import org.apache.hadoop.hbase.testclassification.MasterTests;
import org.apache.hadoop.hbase.testclassification.SmallTests;
import org.apache.hadoop.hbase.util.Bytes;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.TestName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static TableProcedureInterface.DUMMY_NAMESPACE_TABLE_NAME;


@Category({ MasterTests.class, SmallTests.class })
public class TestMasterProcedureScheduler {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestMasterProcedureScheduler.class);

    private static final Logger LOG = LoggerFactory.getLogger(TestMasterProcedureScheduler.class);

    private MasterProcedureScheduler queue;

    @Rule
    public TestName name = new TestName();

    /**
     * Verify simple create/insert/fetch/delete of the table queue.
     */
    @Test
    public void testSimpleTableOpsQueues() throws Exception {
        final int NUM_TABLES = 10;
        final int NUM_ITEMS = 10;
        int count = 0;
        for (int i = 1; i <= NUM_TABLES; ++i) {
            TableName tableName = TableName.valueOf(String.format("test-%04d", i));
            // insert items
            for (int j = 1; j <= NUM_ITEMS; ++j) {
                queue.addBack(new TestMasterProcedureScheduler.TestTableProcedure(((i * 1000) + j), tableName, TableOperationType.EDIT));
                Assert.assertEquals((++count), queue.size());
            }
        }
        Assert.assertEquals((NUM_TABLES * NUM_ITEMS), queue.size());
        for (int j = 1; j <= NUM_ITEMS; ++j) {
            for (int i = 1; i <= NUM_TABLES; ++i) {
                Procedure<?> proc = queue.poll();
                Assert.assertTrue((proc != null));
                TableName tableName = ((TestMasterProcedureScheduler.TestTableProcedure) (proc)).getTableName();
                queue.waitTableExclusiveLock(proc, tableName);
                queue.wakeTableExclusiveLock(proc, tableName);
                queue.completionCleanup(proc);
                Assert.assertEquals((--count), queue.size());
                Assert.assertEquals(((i * 1000) + j), proc.getProcId());
            }
        }
        Assert.assertEquals(0, queue.size());
        for (int i = 1; i <= NUM_TABLES; ++i) {
            final TableName tableName = TableName.valueOf(String.format("test-%04d", i));
            final TestMasterProcedureScheduler.TestTableProcedure dummyProc = new TestMasterProcedureScheduler.TestTableProcedure(100, tableName, TableOperationType.DELETE);
            // complete the table deletion
            Assert.assertTrue(queue.markTableAsDeleted(tableName, dummyProc));
        }
    }

    /**
     * Check that the table queue is not deletable until every procedure in-progress is completed
     * (this is a special case for write-locks).
     */
    @Test
    public void testCreateDeleteTableOperationsWithWriteLock() throws Exception {
        final TableName tableName = TableName.valueOf(name.getMethodName());
        final TestMasterProcedureScheduler.TestTableProcedure dummyProc = new TestMasterProcedureScheduler.TestTableProcedure(100, tableName, TableOperationType.DELETE);
        queue.addBack(new TestMasterProcedureScheduler.TestTableProcedure(1, tableName, TableOperationType.EDIT));
        // table can't be deleted because one item is in the queue
        Assert.assertFalse(queue.markTableAsDeleted(tableName, dummyProc));
        // fetch item and take a lock
        Procedure<?> proc = queue.poll();
        Assert.assertEquals(1, proc.getProcId());
        // take the xlock
        Assert.assertEquals(false, queue.waitTableExclusiveLock(proc, tableName));
        // table can't be deleted because we have the lock
        Assert.assertEquals(0, queue.size());
        Assert.assertFalse(queue.markTableAsDeleted(tableName, dummyProc));
        // release the xlock
        queue.wakeTableExclusiveLock(proc, tableName);
        // complete the table deletion
        Assert.assertTrue(queue.markTableAsDeleted(tableName, proc));
    }

    /**
     * Check that the table queue is not deletable until every procedure in-progress is completed
     * (this is a special case for read-locks).
     */
    @Test
    public void testCreateDeleteTableOperationsWithReadLock() throws Exception {
        final TableName tableName = TableName.valueOf(name.getMethodName());
        final int nitems = 2;
        final TestMasterProcedureScheduler.TestTableProcedure dummyProc = new TestMasterProcedureScheduler.TestTableProcedure(100, tableName, TableOperationType.DELETE);
        for (int i = 1; i <= nitems; ++i) {
            queue.addBack(new TestMasterProcedureScheduler.TestTableProcedure(i, tableName, TableOperationType.READ));
        }
        // table can't be deleted because one item is in the queue
        Assert.assertFalse(queue.markTableAsDeleted(tableName, dummyProc));
        Procedure<?>[] procs = new Procedure[nitems];
        for (int i = 0; i < nitems; ++i) {
            // fetch item and take a lock
            Procedure<?> proc = queue.poll();
            procs[i] = proc;
            Assert.assertEquals((i + 1), proc.getProcId());
            // take the rlock
            Assert.assertEquals(false, queue.waitTableSharedLock(proc, tableName));
            // table can't be deleted because we have locks and/or items in the queue
            Assert.assertFalse(queue.markTableAsDeleted(tableName, dummyProc));
        }
        for (int i = 0; i < nitems; ++i) {
            // table can't be deleted because we have locks
            Assert.assertFalse(queue.markTableAsDeleted(tableName, dummyProc));
            // release the rlock
            queue.wakeTableSharedLock(procs[i], tableName);
        }
        // there are no items and no lock in the queeu
        Assert.assertEquals(0, queue.size());
        // complete the table deletion
        Assert.assertTrue(queue.markTableAsDeleted(tableName, dummyProc));
    }

    /**
     * Verify the correct logic of RWLocks on the queue
     */
    @Test
    public void testVerifyRwLocks() throws Exception {
        final TableName tableName = TableName.valueOf(name.getMethodName());
        queue.addBack(new TestMasterProcedureScheduler.TestTableProcedure(1, tableName, TableOperationType.EDIT));
        queue.addBack(new TestMasterProcedureScheduler.TestTableProcedure(2, tableName, TableOperationType.READ));
        queue.addBack(new TestMasterProcedureScheduler.TestTableProcedure(3, tableName, TableOperationType.EDIT));
        // Fetch the 1st item and take the write lock
        Procedure<?> proc = queue.poll();
        Assert.assertEquals(1, proc.getProcId());
        Assert.assertEquals(false, queue.waitTableExclusiveLock(proc, tableName));
        // Fetch the 2nd item and verify that the lock can't be acquired
        Assert.assertEquals(null, queue.poll(0));
        // Release the write lock and acquire the read lock
        queue.wakeTableExclusiveLock(proc, tableName);
        // Fetch the 2nd item and take the read lock
        Procedure<?> rdProc = queue.poll();
        Assert.assertEquals(2, rdProc.getProcId());
        Assert.assertEquals(false, queue.waitTableSharedLock(rdProc, tableName));
        // Fetch the 3rd item and verify that the lock can't be acquired
        Assert.assertEquals(null, queue.poll(0));
        // release the rdlock of item 2 and take the wrlock for the 3d item
        queue.wakeTableSharedLock(rdProc, tableName);
        queue.addBack(new TestMasterProcedureScheduler.TestTableProcedure(4, tableName, TableOperationType.READ));
        queue.addBack(new TestMasterProcedureScheduler.TestTableProcedure(5, tableName, TableOperationType.READ));
        // Fetch the 3rd item and take the write lock
        Procedure<?> wrProc = queue.poll();
        Assert.assertEquals(false, queue.waitTableExclusiveLock(wrProc, tableName));
        // Fetch 4th item and verify that the lock can't be acquired
        Assert.assertEquals(null, queue.poll(0));
        // Release the write lock and acquire the read lock
        queue.wakeTableExclusiveLock(wrProc, tableName);
        // Fetch the 4th item and take the read lock
        rdProc = queue.poll();
        Assert.assertEquals(4, rdProc.getProcId());
        Assert.assertEquals(false, queue.waitTableSharedLock(rdProc, tableName));
        // Fetch the 4th item and take the read lock
        Procedure<?> rdProc2 = queue.poll();
        Assert.assertEquals(5, rdProc2.getProcId());
        Assert.assertEquals(false, queue.waitTableSharedLock(rdProc2, tableName));
        // Release 4th and 5th read-lock
        queue.wakeTableSharedLock(rdProc, tableName);
        queue.wakeTableSharedLock(rdProc2, tableName);
        // remove table queue
        Assert.assertEquals(0, queue.size());
        Assert.assertTrue("queue should be deleted", queue.markTableAsDeleted(tableName, wrProc));
    }

    @Test
    public void testVerifyNamespaceRwLocks() throws Exception {
        String nsName1 = "ns1";
        String nsName2 = "ns2";
        TableName tableName1 = TableName.valueOf(nsName1, name.getMethodName());
        TableName tableName2 = TableName.valueOf(nsName2, name.getMethodName());
        queue.addBack(new TestMasterProcedureScheduler.TestNamespaceProcedure(1, nsName1, TableOperationType.EDIT));
        queue.addBack(new TestMasterProcedureScheduler.TestTableProcedure(2, tableName1, TableOperationType.EDIT));
        queue.addBack(new TestMasterProcedureScheduler.TestTableProcedure(3, tableName2, TableOperationType.EDIT));
        queue.addBack(new TestMasterProcedureScheduler.TestNamespaceProcedure(4, nsName2, TableOperationType.EDIT));
        // Fetch the 1st item and take the write lock
        Procedure<?> procNs1 = queue.poll();
        Assert.assertEquals(1, procNs1.getProcId());
        Assert.assertFalse(queue.waitNamespaceExclusiveLock(procNs1, nsName1));
        // namespace table has higher priority so we still return procedure for it
        Procedure<?> procNs2 = queue.poll();
        Assert.assertEquals(4, procNs2.getProcId());
        Assert.assertFalse(queue.waitNamespaceExclusiveLock(procNs2, nsName2));
        queue.wakeNamespaceExclusiveLock(procNs2, nsName2);
        // add procNs2 back in the queue
        queue.yield(procNs2);
        // again
        procNs2 = queue.poll();
        Assert.assertEquals(4, procNs2.getProcId());
        Assert.assertFalse(queue.waitNamespaceExclusiveLock(procNs2, nsName2));
        // ns1 and ns2 are both locked so we get nothing
        Assert.assertNull(queue.poll());
        // release the ns1 lock
        queue.wakeNamespaceExclusiveLock(procNs1, nsName1);
        // we are now able to execute table of ns1
        long procId = queue.poll().getProcId();
        Assert.assertEquals(2, procId);
        // release ns2
        queue.wakeNamespaceExclusiveLock(procNs2, nsName2);
        // we are now able to execute table of ns2
        procId = queue.poll().getProcId();
        Assert.assertEquals(3, procId);
    }

    @Test
    public void testVerifyNamespaceXLock() throws Exception {
        String nsName = "ns1";
        TableName tableName = TableName.valueOf(nsName, name.getMethodName());
        queue.addBack(new TestMasterProcedureScheduler.TestNamespaceProcedure(1, nsName, TableOperationType.CREATE));
        queue.addBack(new TestMasterProcedureScheduler.TestTableProcedure(2, tableName, TableOperationType.READ));
        // Fetch the ns item and take the xlock
        Procedure<?> proc = queue.poll();
        Assert.assertEquals(1, proc.getProcId());
        Assert.assertEquals(false, queue.waitNamespaceExclusiveLock(proc, nsName));
        // the table operation can't be executed because the ns is locked
        Assert.assertEquals(null, queue.poll(0));
        // release the ns lock
        queue.wakeNamespaceExclusiveLock(proc, nsName);
        proc = queue.poll();
        Assert.assertEquals(2, proc.getProcId());
        Assert.assertEquals(false, queue.waitTableExclusiveLock(proc, tableName));
        queue.wakeTableExclusiveLock(proc, tableName);
    }

    @Test
    public void testXLockWaitingForExecutingSharedLockToRelease() {
        final TableName tableName = TableName.valueOf(name.getMethodName());
        final RegionInfo regionA = RegionInfoBuilder.newBuilder(tableName).setStartKey(Bytes.toBytes("a")).setEndKey(Bytes.toBytes("b")).build();
        queue.addBack(new TestMasterProcedureScheduler.TestRegionProcedure(1, tableName, TableOperationType.REGION_ASSIGN, regionA));
        queue.addBack(new TestMasterProcedureScheduler.TestTableProcedure(2, tableName, TableOperationType.EDIT));
        // Fetch the 1st item and take the shared lock
        Procedure<?> proc = queue.poll();
        Assert.assertEquals(1, proc.getProcId());
        Assert.assertEquals(false, queue.waitRegion(proc, regionA));
        // the xlock operation in the queue can't be executed
        Assert.assertEquals(null, queue.poll(0));
        // release the shared lock
        queue.wakeRegion(proc, regionA);
        // Fetch the 2nd item and take the xlock
        proc = queue.poll();
        Assert.assertEquals(2, proc.getProcId());
        Assert.assertEquals(false, queue.waitTableExclusiveLock(proc, tableName));
        queue.addBack(new TestMasterProcedureScheduler.TestRegionProcedure(3, tableName, TableOperationType.REGION_UNASSIGN, regionA));
        // everything is locked by the table operation
        Assert.assertEquals(null, queue.poll(0));
        // release the table xlock
        queue.wakeTableExclusiveLock(proc, tableName);
        // grab the last item in the queue
        proc = queue.poll();
        Assert.assertEquals(3, proc.getProcId());
        // lock and unlock the region
        Assert.assertEquals(false, queue.waitRegion(proc, regionA));
        Assert.assertEquals(null, queue.poll(0));
        queue.wakeRegion(proc, regionA);
    }

    @Test
    public void testVerifyRegionLocks() throws Exception {
        final TableName tableName = TableName.valueOf(name.getMethodName());
        final RegionInfo regionA = RegionInfoBuilder.newBuilder(tableName).setStartKey(Bytes.toBytes("a")).setEndKey(Bytes.toBytes("b")).build();
        final RegionInfo regionB = RegionInfoBuilder.newBuilder(tableName).setStartKey(Bytes.toBytes("b")).setEndKey(Bytes.toBytes("c")).build();
        final RegionInfo regionC = RegionInfoBuilder.newBuilder(tableName).setStartKey(Bytes.toBytes("c")).setEndKey(Bytes.toBytes("d")).build();
        queue.addBack(new TestMasterProcedureScheduler.TestTableProcedure(1, tableName, TableOperationType.EDIT));
        queue.addBack(new TestMasterProcedureScheduler.TestRegionProcedure(2, tableName, TableOperationType.REGION_MERGE, regionA, regionB));
        queue.addBack(new TestMasterProcedureScheduler.TestRegionProcedure(3, tableName, TableOperationType.REGION_SPLIT, regionA));
        queue.addBack(new TestMasterProcedureScheduler.TestRegionProcedure(4, tableName, TableOperationType.REGION_SPLIT, regionB));
        queue.addBack(new TestMasterProcedureScheduler.TestRegionProcedure(5, tableName, TableOperationType.REGION_UNASSIGN, regionC));
        // Fetch the 1st item and take the write lock
        Procedure<?> proc = queue.poll();
        Assert.assertEquals(1, proc.getProcId());
        Assert.assertEquals(false, queue.waitTableExclusiveLock(proc, tableName));
        // everything is locked by the table operation
        Assert.assertEquals(null, queue.poll(0));
        // release the table lock
        queue.wakeTableExclusiveLock(proc, tableName);
        // Fetch the 2nd item and the the lock on regionA and regionB
        Procedure<?> mergeProc = queue.poll();
        Assert.assertEquals(2, mergeProc.getProcId());
        Assert.assertEquals(false, queue.waitRegions(mergeProc, tableName, regionA, regionB));
        // Fetch the 3rd item and the try to lock region A which will fail
        // because already locked. this procedure will go in waiting.
        // (this stuff will be explicit until we get rid of the zk-lock)
        Procedure<?> procA = queue.poll();
        Assert.assertEquals(3, procA.getProcId());
        Assert.assertEquals(true, queue.waitRegions(procA, tableName, regionA));
        // Fetch the 4th item, same story as the 3rd
        Procedure<?> procB = queue.poll();
        Assert.assertEquals(4, procB.getProcId());
        Assert.assertEquals(true, queue.waitRegions(procB, tableName, regionB));
        // Fetch the 5th item, since it is a non-locked region we are able to execute it
        Procedure<?> procC = queue.poll();
        Assert.assertEquals(5, procC.getProcId());
        Assert.assertEquals(false, queue.waitRegions(procC, tableName, regionC));
        // 3rd and 4th are in the region suspended queue
        Assert.assertEquals(null, queue.poll(0));
        // Release region A-B from merge operation (procId=2)
        queue.wakeRegions(mergeProc, tableName, regionA, regionB);
        // Fetch the 3rd item, now the lock on the region is available
        procA = queue.poll();
        Assert.assertEquals(3, procA.getProcId());
        Assert.assertEquals(false, queue.waitRegions(procA, tableName, regionA));
        // Fetch the 4th item, now the lock on the region is available
        procB = queue.poll();
        Assert.assertEquals(4, procB.getProcId());
        Assert.assertEquals(false, queue.waitRegions(procB, tableName, regionB));
        // release the locks on the regions
        queue.wakeRegions(procA, tableName, regionA);
        queue.wakeRegions(procB, tableName, regionB);
        queue.wakeRegions(procC, tableName, regionC);
    }

    @Test
    public void testVerifySubProcRegionLocks() throws Exception {
        final TableName tableName = TableName.valueOf(name.getMethodName());
        final RegionInfo regionA = RegionInfoBuilder.newBuilder(tableName).setStartKey(Bytes.toBytes("a")).setEndKey(Bytes.toBytes("b")).build();
        final RegionInfo regionB = RegionInfoBuilder.newBuilder(tableName).setStartKey(Bytes.toBytes("b")).setEndKey(Bytes.toBytes("c")).build();
        final RegionInfo regionC = RegionInfoBuilder.newBuilder(tableName).setStartKey(Bytes.toBytes("c")).setEndKey(Bytes.toBytes("d")).build();
        queue.addBack(new TestMasterProcedureScheduler.TestTableProcedure(1, tableName, TableOperationType.ENABLE));
        // Fetch the 1st item from the queue, "the root procedure" and take the table lock
        Procedure<?> rootProc = queue.poll();
        Assert.assertEquals(1, rootProc.getProcId());
        Assert.assertEquals(false, queue.waitTableExclusiveLock(rootProc, tableName));
        Assert.assertEquals(null, queue.poll(0));
        // Execute the 1st step of the root-proc.
        // we should get 3 sub-proc back, one for each region.
        // (this step is done by the executor/rootProc, we are simulating it)
        Procedure<?>[] subProcs = new Procedure[]{ new TestMasterProcedureScheduler.TestRegionProcedure(1, 2, tableName, TableOperationType.REGION_EDIT, regionA), new TestMasterProcedureScheduler.TestRegionProcedure(1, 3, tableName, TableOperationType.REGION_EDIT, regionB), new TestMasterProcedureScheduler.TestRegionProcedure(1, 4, tableName, TableOperationType.REGION_EDIT, regionC) };
        // at this point the rootProc is going in a waiting state
        // and the sub-procedures will be added in the queue.
        // (this step is done by the executor, we are simulating it)
        for (int i = (subProcs.length) - 1; i >= 0; --i) {
            queue.addFront(subProcs[i]);
        }
        Assert.assertEquals(subProcs.length, queue.size());
        // we should be able to fetch and execute all the sub-procs,
        // since they are operating on different regions
        for (int i = 0; i < (subProcs.length); ++i) {
            TestMasterProcedureScheduler.TestRegionProcedure regionProc = ((TestMasterProcedureScheduler.TestRegionProcedure) (queue.poll(0)));
            Assert.assertEquals(getProcId(), getProcId());
            Assert.assertEquals(false, queue.waitRegions(regionProc, tableName, regionProc.getRegionInfo()));
        }
        // nothing else in the queue
        Assert.assertEquals(null, queue.poll(0));
        // release all the region locks
        for (int i = 0; i < (subProcs.length); ++i) {
            TestMasterProcedureScheduler.TestRegionProcedure regionProc = ((TestMasterProcedureScheduler.TestRegionProcedure) (subProcs[i]));
            queue.wakeRegions(regionProc, tableName, regionProc.getRegionInfo());
        }
        // nothing else in the queue
        Assert.assertEquals(null, queue.poll(0));
        // release the table lock (for the root procedure)
        queue.wakeTableExclusiveLock(rootProc, tableName);
    }

    @Test
    public void testInheritedRegionXLock() {
        final TableName tableName = TableName.valueOf(name.getMethodName());
        final RegionInfo region = RegionInfoBuilder.newBuilder(tableName).setStartKey(Bytes.toBytes("a")).setEndKey(Bytes.toBytes("b")).build();
        queue.addBack(new TestMasterProcedureScheduler.TestRegionProcedure(1, tableName, TableOperationType.REGION_SPLIT, region));
        queue.addBack(new TestMasterProcedureScheduler.TestRegionProcedure(1, 2, tableName, TableOperationType.REGION_UNASSIGN, region));
        queue.addBack(new TestMasterProcedureScheduler.TestRegionProcedure(3, tableName, TableOperationType.REGION_EDIT, region));
        // fetch the root proc and take the lock on the region
        Procedure<?> rootProc = queue.poll();
        Assert.assertEquals(1, rootProc.getProcId());
        Assert.assertEquals(false, queue.waitRegion(rootProc, region));
        // fetch the sub-proc and take the lock on the region (inherited lock)
        Procedure<?> childProc = queue.poll();
        Assert.assertEquals(2, childProc.getProcId());
        Assert.assertEquals(false, queue.waitRegion(childProc, region));
        // proc-3 will be fetched but it can't take the lock
        Procedure<?> proc = queue.poll();
        Assert.assertEquals(3, proc.getProcId());
        Assert.assertEquals(true, queue.waitRegion(proc, region));
        // release the child lock
        queue.wakeRegion(childProc, region);
        // nothing in the queue (proc-3 is suspended)
        Assert.assertEquals(null, queue.poll(0));
        // release the root lock
        queue.wakeRegion(rootProc, region);
        // proc-3 should be now available
        proc = queue.poll();
        Assert.assertEquals(3, proc.getProcId());
        Assert.assertEquals(false, queue.waitRegion(proc, region));
        queue.wakeRegion(proc, region);
    }

    @Test
    public void testSuspendedProcedure() throws Exception {
        final TableName tableName = TableName.valueOf(name.getMethodName());
        queue.addBack(new TestMasterProcedureScheduler.TestTableProcedure(1, tableName, TableOperationType.READ));
        queue.addBack(new TestMasterProcedureScheduler.TestTableProcedure(2, tableName, TableOperationType.READ));
        Procedure<?> proc = queue.poll();
        Assert.assertEquals(1, proc.getProcId());
        // suspend
        ProcedureEvent<?> event = new ProcedureEvent("testSuspendedProcedureEvent");
        Assert.assertEquals(true, event.suspendIfNotReady(proc));
        proc = queue.poll();
        Assert.assertEquals(2, proc.getProcId());
        Assert.assertEquals(null, queue.poll(0));
        // resume
        event.wake(queue);
        proc = queue.poll();
        Assert.assertEquals(1, proc.getProcId());
        Assert.assertEquals(null, queue.poll(0));
    }

    @Test
    public void testParentXLockAndChildrenSharedLock() throws Exception {
        final TableName tableName = TableName.valueOf(name.getMethodName());
        final RegionInfo[] regions = TestMasterProcedureScheduler.generateRegionInfo(tableName);
        final TestMasterProcedureScheduler.TestRegionProcedure[] childProcs = new TestMasterProcedureScheduler.TestRegionProcedure[regions.length];
        for (int i = 0; i < (regions.length); ++i) {
            childProcs[i] = new TestMasterProcedureScheduler.TestRegionProcedure(1, (2 + i), tableName, TableOperationType.REGION_ASSIGN, regions[i]);
        }
        testInheritedXLockAndChildrenSharedLock(tableName, new TestMasterProcedureScheduler.TestTableProcedure(1, tableName, TableOperationType.CREATE), childProcs);
    }

    @Test
    public void testRootXLockAndChildrenSharedLock() throws Exception {
        final TableName tableName = TableName.valueOf(name.getMethodName());
        final RegionInfo[] regions = TestMasterProcedureScheduler.generateRegionInfo(tableName);
        final TestMasterProcedureScheduler.TestRegionProcedure[] childProcs = new TestMasterProcedureScheduler.TestRegionProcedure[regions.length];
        for (int i = 0; i < (regions.length); ++i) {
            childProcs[i] = new TestMasterProcedureScheduler.TestRegionProcedure(1, 2, (3 + i), tableName, TableOperationType.REGION_ASSIGN, regions[i]);
        }
        testInheritedXLockAndChildrenSharedLock(tableName, new TestMasterProcedureScheduler.TestTableProcedure(1, tableName, TableOperationType.CREATE), childProcs);
    }

    @Test
    public void testParentXLockAndChildrenXLock() throws Exception {
        final TableName tableName = TableName.valueOf(name.getMethodName());
        testInheritedXLockAndChildrenXLock(tableName, new TestMasterProcedureScheduler.TestTableProcedure(1, tableName, TableOperationType.EDIT), new TestMasterProcedureScheduler.TestTableProcedure(1, 2, tableName, TableOperationType.EDIT));
    }

    @Test
    public void testRootXLockAndChildrenXLock() throws Exception {
        final TableName tableName = TableName.valueOf(name.getMethodName());
        // simulate 3 procedures: 1 (root), (2) child of root, (3) child of proc-2
        testInheritedXLockAndChildrenXLock(tableName, new TestMasterProcedureScheduler.TestTableProcedure(1, tableName, TableOperationType.EDIT), new TestMasterProcedureScheduler.TestTableProcedure(1, 2, 3, tableName, TableOperationType.EDIT));
    }

    @Test
    public void testYieldWithXLockHeld() throws Exception {
        final TableName tableName = TableName.valueOf(name.getMethodName());
        queue.addBack(new TestMasterProcedureScheduler.TestTableProcedure(1, tableName, TableOperationType.EDIT));
        queue.addBack(new TestMasterProcedureScheduler.TestTableProcedure(2, tableName, TableOperationType.EDIT));
        // fetch from the queue and acquire xlock for the first proc
        Procedure<?> proc = queue.poll();
        Assert.assertEquals(1, proc.getProcId());
        Assert.assertEquals(false, queue.waitTableExclusiveLock(proc, tableName));
        // nothing available, until xlock release
        Assert.assertEquals(null, queue.poll(0));
        // put the proc in the queue
        queue.yield(proc);
        // fetch from the queue, it should be the one with just added back
        proc = queue.poll();
        Assert.assertEquals(1, proc.getProcId());
        // release the xlock
        queue.wakeTableExclusiveLock(proc, tableName);
        proc = queue.poll();
        Assert.assertEquals(2, proc.getProcId());
    }

    @Test
    public void testYieldWithSharedLockHeld() throws Exception {
        final TableName tableName = TableName.valueOf(name.getMethodName());
        queue.addBack(new TestMasterProcedureScheduler.TestTableProcedure(1, tableName, TableOperationType.READ));
        queue.addBack(new TestMasterProcedureScheduler.TestTableProcedure(2, tableName, TableOperationType.READ));
        queue.addBack(new TestMasterProcedureScheduler.TestTableProcedure(3, tableName, TableOperationType.EDIT));
        // fetch and acquire the first shared-lock
        Procedure<?> proc1 = queue.poll();
        Assert.assertEquals(1, proc1.getProcId());
        Assert.assertEquals(false, queue.waitTableSharedLock(proc1, tableName));
        // fetch and acquire the second shared-lock
        Procedure<?> proc2 = queue.poll();
        Assert.assertEquals(2, proc2.getProcId());
        Assert.assertEquals(false, queue.waitTableSharedLock(proc2, tableName));
        // nothing available, until xlock release
        Assert.assertEquals(null, queue.poll(0));
        // put the procs back in the queue
        queue.yield(proc1);
        queue.yield(proc2);
        // fetch from the queue, it should fetch the ones with just added back
        proc1 = queue.poll();
        Assert.assertEquals(1, proc1.getProcId());
        proc2 = queue.poll();
        Assert.assertEquals(2, proc2.getProcId());
        // release the xlock
        queue.wakeTableSharedLock(proc1, tableName);
        queue.wakeTableSharedLock(proc2, tableName);
        Procedure<?> proc3 = queue.poll();
        Assert.assertEquals(3, proc3.getProcId());
    }

    public static class TestTableProcedure extends TestProcedure implements TableProcedureInterface {
        private final TableOperationType opType;

        private final TableName tableName;

        public TestTableProcedure() {
            throw new UnsupportedOperationException("recovery should not be triggered here");
        }

        public TestTableProcedure(long procId, TableName tableName, TableOperationType opType) {
            this((-1), procId, tableName, opType);
        }

        public TestTableProcedure(long parentProcId, long procId, TableName tableName, TableOperationType opType) {
            this((-1), parentProcId, procId, tableName, opType);
        }

        public TestTableProcedure(long rootProcId, long parentProcId, long procId, TableName tableName, TableOperationType opType) {
            super(procId, parentProcId, rootProcId, null);
            this.tableName = tableName;
            this.opType = opType;
        }

        @Override
        public TableName getTableName() {
            return tableName;
        }

        @Override
        public TableOperationType getTableOperationType() {
            return opType;
        }

        @Override
        public void toStringClassDetails(final StringBuilder sb) {
            sb.append(getClass().getSimpleName());
            sb.append("(table=");
            sb.append(getTableName());
            sb.append(")");
        }
    }

    public static class TestTableProcedureWithEvent extends TestMasterProcedureScheduler.TestTableProcedure {
        private final ProcedureEvent<?> event;

        public TestTableProcedureWithEvent(long procId, TableName tableName, TableOperationType opType) {
            super(procId, tableName, opType);
            event = new ProcedureEvent(((tableName + " procId=") + procId));
        }

        public ProcedureEvent<?> getEvent() {
            return event;
        }
    }

    public static class TestRegionProcedure extends TestMasterProcedureScheduler.TestTableProcedure {
        private final RegionInfo[] regionInfo;

        public TestRegionProcedure() {
            throw new UnsupportedOperationException("recovery should not be triggered here");
        }

        public TestRegionProcedure(long procId, TableName tableName, TableOperationType opType, RegionInfo... regionInfo) {
            this((-1), procId, tableName, opType, regionInfo);
        }

        public TestRegionProcedure(long parentProcId, long procId, TableName tableName, TableOperationType opType, RegionInfo... regionInfo) {
            this((-1), parentProcId, procId, tableName, opType, regionInfo);
        }

        public TestRegionProcedure(long rootProcId, long parentProcId, long procId, TableName tableName, TableOperationType opType, RegionInfo... regionInfo) {
            super(rootProcId, parentProcId, procId, tableName, opType);
            this.regionInfo = regionInfo;
        }

        public RegionInfo[] getRegionInfo() {
            return regionInfo;
        }

        @Override
        public void toStringClassDetails(final StringBuilder sb) {
            sb.append(getClass().getSimpleName());
            sb.append("(regions=");
            sb.append(Arrays.toString(getRegionInfo()));
            sb.append(")");
        }
    }

    public static class TestNamespaceProcedure extends TestProcedure implements TableProcedureInterface {
        private final TableOperationType opType;

        private final String nsName;

        public TestNamespaceProcedure() {
            throw new UnsupportedOperationException("recovery should not be triggered here");
        }

        public TestNamespaceProcedure(long procId, String nsName, TableOperationType opType) {
            super(procId);
            this.nsName = nsName;
            this.opType = opType;
        }

        @Override
        public TableName getTableName() {
            return DUMMY_NAMESPACE_TABLE_NAME;
        }

        @Override
        public TableOperationType getTableOperationType() {
            return opType;
        }

        @Override
        public void toStringClassDetails(final StringBuilder sb) {
            sb.append(getClass().getSimpleName());
            sb.append("(ns=");
            sb.append(nsName);
            sb.append(")");
        }
    }

    public static class TestPeerProcedure extends TestProcedure implements PeerProcedureInterface {
        private final String peerId;

        private final PeerOperationType opType;

        public TestPeerProcedure(long procId, String peerId, PeerOperationType opType) {
            super(procId);
            this.peerId = peerId;
            this.opType = opType;
        }

        @Override
        public String getPeerId() {
            return peerId;
        }

        @Override
        public PeerOperationType getPeerOperationType() {
            return opType;
        }
    }

    @Test
    public void testListLocksServer() throws Exception {
        LockProcedure procedure = TestMasterProcedureScheduler.createExclusiveLockProcedure(0);
        queue.waitServerExclusiveLock(procedure, ServerName.valueOf("server1,1234,0"));
        List<LockedResource> resources = queue.getLocks();
        Assert.assertEquals(1, resources.size());
        LockedResource serverResource = resources.get(0);
        TestMasterProcedureScheduler.assertLockResource(serverResource, SERVER, "server1,1234,0");
        TestMasterProcedureScheduler.assertExclusiveLock(serverResource, procedure);
        Assert.assertTrue(serverResource.getWaitingProcedures().isEmpty());
    }

    @Test
    public void testListLocksNamespace() throws Exception {
        LockProcedure procedure = TestMasterProcedureScheduler.createExclusiveLockProcedure(1);
        queue.waitNamespaceExclusiveLock(procedure, "ns1");
        List<LockedResource> locks = queue.getLocks();
        Assert.assertEquals(2, locks.size());
        LockedResource namespaceResource = locks.get(0);
        TestMasterProcedureScheduler.assertLockResource(namespaceResource, NAMESPACE, "ns1");
        TestMasterProcedureScheduler.assertExclusiveLock(namespaceResource, procedure);
        Assert.assertTrue(namespaceResource.getWaitingProcedures().isEmpty());
        LockedResource tableResource = locks.get(1);
        TestMasterProcedureScheduler.assertLockResource(tableResource, TABLE, DUMMY_NAMESPACE_TABLE_NAME.getNameAsString());
        TestMasterProcedureScheduler.assertSharedLock(tableResource, 1);
        Assert.assertTrue(tableResource.getWaitingProcedures().isEmpty());
    }

    @Test
    public void testListLocksTable() throws Exception {
        LockProcedure procedure = TestMasterProcedureScheduler.createExclusiveLockProcedure(2);
        queue.waitTableExclusiveLock(procedure, TableName.valueOf("ns2", "table2"));
        List<LockedResource> locks = queue.getLocks();
        Assert.assertEquals(2, locks.size());
        LockedResource namespaceResource = locks.get(0);
        TestMasterProcedureScheduler.assertLockResource(namespaceResource, NAMESPACE, "ns2");
        TestMasterProcedureScheduler.assertSharedLock(namespaceResource, 1);
        Assert.assertTrue(namespaceResource.getWaitingProcedures().isEmpty());
        LockedResource tableResource = locks.get(1);
        TestMasterProcedureScheduler.assertLockResource(tableResource, TABLE, "ns2:table2");
        TestMasterProcedureScheduler.assertExclusiveLock(tableResource, procedure);
        Assert.assertTrue(tableResource.getWaitingProcedures().isEmpty());
    }

    @Test
    public void testListLocksRegion() throws Exception {
        LockProcedure procedure = TestMasterProcedureScheduler.createExclusiveLockProcedure(3);
        RegionInfo regionInfo = RegionInfoBuilder.newBuilder(TableName.valueOf("ns3", "table3")).build();
        queue.waitRegion(procedure, regionInfo);
        List<LockedResource> resources = queue.getLocks();
        Assert.assertEquals(3, resources.size());
        LockedResource namespaceResource = resources.get(0);
        TestMasterProcedureScheduler.assertLockResource(namespaceResource, NAMESPACE, "ns3");
        TestMasterProcedureScheduler.assertSharedLock(namespaceResource, 1);
        Assert.assertTrue(namespaceResource.getWaitingProcedures().isEmpty());
        LockedResource tableResource = resources.get(1);
        TestMasterProcedureScheduler.assertLockResource(tableResource, TABLE, "ns3:table3");
        TestMasterProcedureScheduler.assertSharedLock(tableResource, 1);
        Assert.assertTrue(tableResource.getWaitingProcedures().isEmpty());
        LockedResource regionResource = resources.get(2);
        TestMasterProcedureScheduler.assertLockResource(regionResource, REGION, regionInfo.getEncodedName());
        TestMasterProcedureScheduler.assertExclusiveLock(regionResource, procedure);
        Assert.assertTrue(regionResource.getWaitingProcedures().isEmpty());
    }

    @Test
    public void testListLocksPeer() throws Exception {
        String peerId = "1";
        LockProcedure procedure = TestMasterProcedureScheduler.createExclusiveLockProcedure(4);
        queue.waitPeerExclusiveLock(procedure, peerId);
        List<LockedResource> locks = queue.getLocks();
        Assert.assertEquals(1, locks.size());
        LockedResource resource = locks.get(0);
        TestMasterProcedureScheduler.assertLockResource(resource, PEER, peerId);
        TestMasterProcedureScheduler.assertExclusiveLock(resource, procedure);
        Assert.assertTrue(resource.getWaitingProcedures().isEmpty());
        // Try to acquire the exclusive lock again with same procedure
        Assert.assertFalse(queue.waitPeerExclusiveLock(procedure, peerId));
        // Try to acquire the exclusive lock again with new procedure
        LockProcedure procedure2 = TestMasterProcedureScheduler.createExclusiveLockProcedure(5);
        Assert.assertTrue(queue.waitPeerExclusiveLock(procedure2, peerId));
        // Same peerId, still only has 1 LockedResource
        locks = queue.getLocks();
        Assert.assertEquals(1, locks.size());
        resource = locks.get(0);
        TestMasterProcedureScheduler.assertLockResource(resource, PEER, peerId);
        // LockedResource owner still is the origin procedure
        TestMasterProcedureScheduler.assertExclusiveLock(resource, procedure);
        // The new procedure should in the waiting list
        Assert.assertEquals(1, resource.getWaitingProcedures().size());
    }

    @Test
    public void testListLocksWaiting() throws Exception {
        LockProcedure procedure1 = TestMasterProcedureScheduler.createExclusiveLockProcedure(1);
        queue.waitTableExclusiveLock(procedure1, TableName.valueOf("ns4", "table4"));
        LockProcedure procedure2 = TestMasterProcedureScheduler.createSharedLockProcedure(2);
        queue.waitTableSharedLock(procedure2, TableName.valueOf("ns4", "table4"));
        LockProcedure procedure3 = TestMasterProcedureScheduler.createExclusiveLockProcedure(3);
        queue.waitTableExclusiveLock(procedure3, TableName.valueOf("ns4", "table4"));
        List<LockedResource> resources = queue.getLocks();
        Assert.assertEquals(2, resources.size());
        LockedResource namespaceResource = resources.get(0);
        TestMasterProcedureScheduler.assertLockResource(namespaceResource, NAMESPACE, "ns4");
        TestMasterProcedureScheduler.assertSharedLock(namespaceResource, 1);
        Assert.assertTrue(namespaceResource.getWaitingProcedures().isEmpty());
        LockedResource tableLock = resources.get(1);
        TestMasterProcedureScheduler.assertLockResource(tableLock, TABLE, "ns4:table4");
        TestMasterProcedureScheduler.assertExclusiveLock(tableLock, procedure1);
        List<Procedure<?>> waitingProcedures = tableLock.getWaitingProcedures();
        Assert.assertEquals(2, waitingProcedures.size());
        LockProcedure waitingProcedure2 = ((LockProcedure) (waitingProcedures.get(0)));
        Assert.assertEquals(SHARED, waitingProcedure2.getType());
        Assert.assertEquals(procedure2, waitingProcedure2);
        LockProcedure waitingProcedure3 = ((LockProcedure) (waitingProcedures.get(1)));
        Assert.assertEquals(EXCLUSIVE, waitingProcedure3.getType());
        Assert.assertEquals(procedure3, waitingProcedure3);
    }

    @Test
    public void testAcquireSharedLockWhileParentHoldingExclusiveLock() {
        TableName tableName = TableName.valueOf(name.getMethodName());
        RegionInfo regionInfo = RegionInfoBuilder.newBuilder(tableName).build();
        TestMasterProcedureScheduler.TestTableProcedure parentProc = new TestMasterProcedureScheduler.TestTableProcedure(1, tableName, TableOperationType.EDIT);
        TestMasterProcedureScheduler.TestRegionProcedure proc = new TestMasterProcedureScheduler.TestRegionProcedure(1, 2, tableName, TableOperationType.REGION_EDIT, regionInfo);
        queue.addBack(parentProc);
        Assert.assertSame(parentProc, queue.poll());
        Assert.assertFalse(queue.waitTableExclusiveLock(parentProc, tableName));
        // The queue for this table should be added back to run queue as the parent has the xlock, so we
        // can poll it out.
        queue.addFront(proc);
        Assert.assertSame(proc, queue.poll());
        // the parent has xlock on the table, and it is OK for us to acquire shared lock on the table,
        // this is what this test wants to confirm
        Assert.assertFalse(queue.waitRegion(proc, regionInfo));
        queue.wakeRegion(proc, regionInfo);
        queue.wakeTableExclusiveLock(parentProc, tableName);
    }
}

