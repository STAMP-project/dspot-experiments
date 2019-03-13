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


import LockManager.MasterLock;
import LockType.EXCLUSIVE;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.HBaseTestingUtility;
import org.apache.hadoop.hbase.HRegionInfo;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.master.MasterServices;
import org.apache.hadoop.hbase.testclassification.MasterTests;
import org.apache.hadoop.hbase.testclassification.MediumTests;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.TestName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Category({ MasterTests.class, MediumTests.class })
public class TestLockManager {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestLockManager.class);

    @Rule
    public TestName testName = new TestName();

    // crank this up if this test turns out to be flaky.
    private static final int LOCAL_LOCKS_TIMEOUT = 1000;

    private static final Logger LOG = LoggerFactory.getLogger(TestLockManager.class);

    protected static final HBaseTestingUtility UTIL = new HBaseTestingUtility();

    private static MasterServices masterServices;

    private static String namespace = "namespace";

    private static TableName tableName = TableName.valueOf(TestLockManager.namespace, "table");

    private static HRegionInfo[] tableRegions;

    /**
     * Tests that basic lock functionality works.
     */
    @Test
    public void testMasterLockAcquire() throws Exception {
        LockManager.MasterLock lock = TestLockManager.masterServices.getLockManager().createMasterLock(TestLockManager.namespace, EXCLUSIVE, "desc");
        Assert.assertTrue(lock.tryAcquire(2000));
        Assert.assertTrue(lock.getProc().isLocked());
        lock.release();
        Assert.assertEquals(null, lock.getProc());
    }

    /**
     * Two locks try to acquire lock on same table, assert that later one times out.
     */
    @Test
    public void testMasterLockAcquireTimeout() throws Exception {
        LockManager.MasterLock lock = TestLockManager.masterServices.getLockManager().createMasterLock(TestLockManager.tableName, EXCLUSIVE, "desc");
        LockManager.MasterLock lock2 = TestLockManager.masterServices.getLockManager().createMasterLock(TestLockManager.tableName, EXCLUSIVE, "desc");
        Assert.assertTrue(lock.tryAcquire(2000));
        Assert.assertFalse(lock2.tryAcquire(((TestLockManager.LOCAL_LOCKS_TIMEOUT) / 2)));// wait less than other lock's timeout

        Assert.assertEquals(null, lock2.getProc());
        lock.release();
        Assert.assertTrue(lock2.tryAcquire(2000));
        Assert.assertTrue(lock2.getProc().isLocked());
        lock2.release();
    }

    /**
     * Take region lock, they try table exclusive lock, later one should time out.
     */
    @Test
    public void testMasterLockAcquireTimeoutRegionVsTableExclusive() throws Exception {
        LockManager.MasterLock lock = TestLockManager.masterServices.getLockManager().createMasterLock(TestLockManager.tableRegions, "desc");
        LockManager.MasterLock lock2 = TestLockManager.masterServices.getLockManager().createMasterLock(TestLockManager.tableName, EXCLUSIVE, "desc");
        Assert.assertTrue(lock.tryAcquire(2000));
        Assert.assertFalse(lock2.tryAcquire(((TestLockManager.LOCAL_LOCKS_TIMEOUT) / 2)));// wait less than other lock's timeout

        Assert.assertEquals(null, lock2.getProc());
        lock.release();
        Assert.assertTrue(lock2.tryAcquire(2000));
        Assert.assertTrue(lock2.getProc().isLocked());
        lock2.release();
    }
}

