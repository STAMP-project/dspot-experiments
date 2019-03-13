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


import java.io.IOException;
import java.util.concurrent.CountDownLatch;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.HBaseTestingUtility;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.RegionInfo;
import org.apache.hadoop.hbase.client.TableDescriptor;
import org.apache.hadoop.hbase.master.HMaster;
import org.apache.hadoop.hbase.procedure2.store.ProcedureStore;
import org.apache.hadoop.hbase.procedure2.store.wal.WALProcedureStore;
import org.apache.hadoop.hbase.testclassification.LargeTests;
import org.apache.hadoop.hbase.testclassification.MasterTests;
import org.apache.hadoop.hbase.util.ModifyRegionUtils;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.TestName;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Category({ MasterTests.class, LargeTests.class })
@Ignore
public class TestMasterProcedureWalLease {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestMasterProcedureWalLease.class);

    private static final Logger LOG = LoggerFactory.getLogger(TestMasterProcedureWalLease.class);

    @Rule
    public TestName name = new TestName();

    protected static final HBaseTestingUtility UTIL = new HBaseTestingUtility();

    @Test
    public void testWalRecoverLease() throws Exception {
        final ProcedureStore masterStore = getMasterProcedureExecutor().getStore();
        Assert.assertTrue("expected WALStore for this test", (masterStore instanceof WALProcedureStore));
        HMaster firstMaster = TestMasterProcedureWalLease.UTIL.getHBaseCluster().getMaster();
        // Abort Latch for the master store
        final CountDownLatch masterStoreAbort = new CountDownLatch(1);
        masterStore.registerListener(new ProcedureStore.ProcedureStoreListener() {
            @Override
            public void postSync() {
            }

            @Override
            public void abortProcess() {
                TestMasterProcedureWalLease.LOG.debug("Abort store of Master");
                masterStoreAbort.countDown();
            }
        });
        // startup a fake master the new WAL store will take the lease
        // and the active master should abort.
        HMaster backupMaster3 = Mockito.mock(HMaster.class);
        Mockito.doReturn(firstMaster.getConfiguration()).when(backupMaster3).getConfiguration();
        Mockito.doReturn(true).when(backupMaster3).isActiveMaster();
        final WALProcedureStore backupStore3 = new WALProcedureStore(firstMaster.getConfiguration(), getWALDir(), null, new MasterProcedureEnv.WALStoreLeaseRecovery(backupMaster3));
        // Abort Latch for the test store
        final CountDownLatch backupStore3Abort = new CountDownLatch(1);
        backupStore3.registerListener(new ProcedureStore.ProcedureStoreListener() {
            @Override
            public void postSync() {
            }

            @Override
            public void abortProcess() {
                TestMasterProcedureWalLease.LOG.debug("Abort store of backupMaster3");
                backupStore3Abort.countDown();
                backupStore3.stop(true);
            }
        });
        backupStore3.start(1);
        backupStore3.recoverLease();
        // Try to trigger a command on the master (WAL lease expired on the active one)
        TableDescriptor htd = MasterProcedureTestingUtility.createHTD(TableName.valueOf(name.getMethodName()), "f");
        RegionInfo[] regions = ModifyRegionUtils.createRegionInfos(htd, null);
        TestMasterProcedureWalLease.LOG.debug("submit proc");
        try {
            getMasterProcedureExecutor().submitProcedure(new CreateTableProcedure(getMasterProcedureExecutor().getEnvironment(), htd, regions));
            Assert.fail("expected RuntimeException 'sync aborted'");
        } catch (RuntimeException e) {
            TestMasterProcedureWalLease.LOG.info(("got " + (e.getMessage())));
        }
        TestMasterProcedureWalLease.LOG.debug("wait master store abort");
        masterStoreAbort.await();
        // Now the real backup master should start up
        TestMasterProcedureWalLease.LOG.debug("wait backup master to startup");
        MasterProcedureTestingUtility.waitBackupMaster(TestMasterProcedureWalLease.UTIL, firstMaster);
        Assert.assertEquals(true, firstMaster.isStopped());
        // wait the store in here to abort (the test will fail due to timeout if it doesn't)
        TestMasterProcedureWalLease.LOG.debug("wait the store to abort");
        backupStore3.getStoreTracker().setDeleted(1, false);
        try {
            backupStore3.delete(1);
            Assert.fail("expected RuntimeException 'sync aborted'");
        } catch (RuntimeException e) {
            TestMasterProcedureWalLease.LOG.info(("got " + (e.getMessage())));
        }
        backupStore3Abort.await();
    }

    /**
     * Tests proper fencing in case the current WAL store is fenced
     */
    @Test
    public void testWALfencingWithoutWALRolling() throws IOException {
        testWALfencing(false);
    }

    /**
     * Tests proper fencing in case the current WAL store does not receive writes until after the
     * new WAL does a couple of WAL rolls.
     */
    @Test
    public void testWALfencingWithWALRolling() throws IOException {
        testWALfencing(true);
    }
}

