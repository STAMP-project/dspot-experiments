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
package org.apache.hadoop.hbase.quotas;


import SpaceViolationPolicy.DISABLE;
import SpaceViolationPolicy.NO_INSERTS;
import SpaceViolationPolicy.NO_WRITES;
import SpaceViolationPolicy.NO_WRITES_COMPACTIONS;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.hbase.DoNotRetryIOException;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.HBaseTestingUtility;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Admin;
import org.apache.hadoop.hbase.client.Append;
import org.apache.hadoop.hbase.client.ClientServiceCallable;
import org.apache.hadoop.hbase.client.Delete;
import org.apache.hadoop.hbase.client.Increment;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.RegionInfo;
import org.apache.hadoop.hbase.client.ResultScanner;
import org.apache.hadoop.hbase.client.RpcRetryingCaller;
import org.apache.hadoop.hbase.client.RpcRetryingCallerFactory;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.client.Table;
import org.apache.hadoop.hbase.quotas.policies.DefaultViolationPolicyEnforcement;
import org.apache.hadoop.hbase.regionserver.HRegionServer;
import org.apache.hadoop.hbase.security.AccessDeniedException;
import org.apache.hadoop.hbase.testclassification.LargeTests;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.util.StringUtils;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.TestName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static SpaceViolationPolicy.NO_INSERTS;


/**
 * End-to-end test class for filesystem space quotas.
 */
@Category(LargeTests.class)
public class TestSpaceQuotas {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestSpaceQuotas.class);

    private static final Logger LOG = LoggerFactory.getLogger(TestSpaceQuotas.class);

    private static final HBaseTestingUtility TEST_UTIL = new HBaseTestingUtility();

    // Global for all tests in the class
    private static final AtomicLong COUNTER = new AtomicLong(0);

    private static final int NUM_RETRIES = 10;

    @Rule
    public TestName testName = new TestName();

    private SpaceQuotaHelperForTests helper;

    private final TableName NON_EXISTENT_TABLE = TableName.valueOf("NON_EXISTENT_TABLE");

    @Test
    public void testNoInsertsWithPut() throws Exception {
        Put p = new Put(Bytes.toBytes("to_reject"));
        p.addColumn(Bytes.toBytes(SpaceQuotaHelperForTests.F1), Bytes.toBytes("to"), Bytes.toBytes("reject"));
        writeUntilViolationAndVerifyViolation(NO_INSERTS, p);
    }

    @Test
    public void testNoInsertsWithAppend() throws Exception {
        Append a = new Append(Bytes.toBytes("to_reject"));
        a.addColumn(Bytes.toBytes(SpaceQuotaHelperForTests.F1), Bytes.toBytes("to"), Bytes.toBytes("reject"));
        writeUntilViolationAndVerifyViolation(NO_INSERTS, a);
    }

    @Test
    public void testNoInsertsWithIncrement() throws Exception {
        Increment i = new Increment(Bytes.toBytes("to_reject"));
        i.addColumn(Bytes.toBytes(SpaceQuotaHelperForTests.F1), Bytes.toBytes("count"), 0);
        writeUntilViolationAndVerifyViolation(NO_INSERTS, i);
    }

    @Test
    public void testDeletesAfterNoInserts() throws Exception {
        final TableName tn = writeUntilViolation(NO_INSERTS);
        // Try a couple of times to verify that the quota never gets enforced, same as we
        // do when we're trying to catch the failure.
        Delete d = new Delete(Bytes.toBytes("should_not_be_rejected"));
        for (int i = 0; i < (TestSpaceQuotas.NUM_RETRIES); i++) {
            try (Table t = TestSpaceQuotas.TEST_UTIL.getConnection().getTable(tn)) {
                t.delete(d);
            }
        }
    }

    @Test
    public void testNoWritesWithPut() throws Exception {
        Put p = new Put(Bytes.toBytes("to_reject"));
        p.addColumn(Bytes.toBytes(SpaceQuotaHelperForTests.F1), Bytes.toBytes("to"), Bytes.toBytes("reject"));
        writeUntilViolationAndVerifyViolation(NO_WRITES, p);
    }

    @Test
    public void testNoWritesWithAppend() throws Exception {
        Append a = new Append(Bytes.toBytes("to_reject"));
        a.addColumn(Bytes.toBytes(SpaceQuotaHelperForTests.F1), Bytes.toBytes("to"), Bytes.toBytes("reject"));
        writeUntilViolationAndVerifyViolation(NO_WRITES, a);
    }

    @Test
    public void testNoWritesWithIncrement() throws Exception {
        Increment i = new Increment(Bytes.toBytes("to_reject"));
        i.addColumn(Bytes.toBytes(SpaceQuotaHelperForTests.F1), Bytes.toBytes("count"), 0);
        writeUntilViolationAndVerifyViolation(NO_WRITES, i);
    }

    @Test
    public void testNoWritesWithDelete() throws Exception {
        Delete d = new Delete(Bytes.toBytes("to_reject"));
        writeUntilViolationAndVerifyViolation(NO_WRITES, d);
    }

    @Test
    public void testNoCompactions() throws Exception {
        Put p = new Put(Bytes.toBytes("to_reject"));
        p.addColumn(Bytes.toBytes(SpaceQuotaHelperForTests.F1), Bytes.toBytes("to"), Bytes.toBytes("reject"));
        final TableName tn = writeUntilViolationAndVerifyViolation(NO_WRITES_COMPACTIONS, p);
        // We know the policy is active at this point
        // Major compactions should be rejected
        try {
            TestSpaceQuotas.TEST_UTIL.getAdmin().majorCompact(tn);
            Assert.fail("Expected that invoking the compaction should throw an Exception");
        } catch (DoNotRetryIOException e) {
            // Expected!
        }
        // Minor compactions should also be rejected.
        try {
            TestSpaceQuotas.TEST_UTIL.getAdmin().compact(tn);
            Assert.fail("Expected that invoking the compaction should throw an Exception");
        } catch (DoNotRetryIOException e) {
            // Expected!
        }
    }

    @Test
    public void testNoEnableAfterDisablePolicy() throws Exception {
        Put p = new Put(Bytes.toBytes("to_reject"));
        p.addColumn(Bytes.toBytes(SpaceQuotaHelperForTests.F1), Bytes.toBytes("to"), Bytes.toBytes("reject"));
        final TableName tn = writeUntilViolation(DISABLE);
        final Admin admin = TestSpaceQuotas.TEST_UTIL.getAdmin();
        // Disabling a table relies on some external action (over the other policies), so wait a bit
        // more than the other tests.
        for (int i = 0; i < ((TestSpaceQuotas.NUM_RETRIES) * 2); i++) {
            if (admin.isTableEnabled(tn)) {
                TestSpaceQuotas.LOG.info((tn + " is still enabled, expecting it to be disabled. Will wait and re-check."));
                Thread.sleep(2000);
            }
        }
        Assert.assertFalse((tn + " is still enabled but it should be disabled"), admin.isTableEnabled(tn));
        try {
            admin.enableTable(tn);
        } catch (AccessDeniedException e) {
            String exceptionContents = StringUtils.stringifyException(e);
            final String expectedText = "violated space quota";
            Assert.assertTrue(((("Expected the exception to contain " + expectedText) + ", but was: ") + exceptionContents), exceptionContents.contains(expectedText));
        }
    }

    @Test
    public void testNoBulkLoadsWithNoWrites() throws Exception {
        Put p = new Put(Bytes.toBytes("to_reject"));
        p.addColumn(Bytes.toBytes(SpaceQuotaHelperForTests.F1), Bytes.toBytes("to"), Bytes.toBytes("reject"));
        TableName tableName = writeUntilViolationAndVerifyViolation(NO_WRITES, p);
        // The table is now in violation. Try to do a bulk load
        ClientServiceCallable<Boolean> callable = helper.generateFileToLoad(tableName, 1, 50);
        RpcRetryingCallerFactory factory = new RpcRetryingCallerFactory(TestSpaceQuotas.TEST_UTIL.getConfiguration());
        RpcRetryingCaller<Boolean> caller = factory.newCaller();
        try {
            caller.callWithRetries(callable, Integer.MAX_VALUE);
            Assert.fail("Expected the bulk load call to fail!");
        } catch (SpaceLimitingException e) {
            // Pass
            TestSpaceQuotas.LOG.trace("Caught expected exception", e);
        }
    }

    @Test
    public void testAtomicBulkLoadUnderQuota() throws Exception {
        // Need to verify that if the batch of hfiles cannot be loaded, none are loaded.
        TableName tn = helper.createTableWithRegions(10);
        final long sizeLimit = 50L * (SpaceQuotaHelperForTests.ONE_KILOBYTE);
        QuotaSettings settings = QuotaSettingsFactory.limitTableSpace(tn, sizeLimit, NO_INSERTS);
        TestSpaceQuotas.TEST_UTIL.getAdmin().setQuota(settings);
        HRegionServer rs = TestSpaceQuotas.TEST_UTIL.getMiniHBaseCluster().getRegionServer(0);
        RegionServerSpaceQuotaManager spaceQuotaManager = rs.getRegionServerSpaceQuotaManager();
        Map<TableName, SpaceQuotaSnapshot> snapshots = spaceQuotaManager.copyQuotaSnapshots();
        Map<RegionInfo, Long> regionSizes = getReportedSizesForTable(tn);
        while (true) {
            SpaceQuotaSnapshot snapshot = snapshots.get(tn);
            if ((snapshot != null) && ((snapshot.getLimit()) > 0)) {
                break;
            }
            TestSpaceQuotas.LOG.debug(((("Snapshot does not yet realize quota limit: " + snapshots) + ", regionsizes: ") + regionSizes));
            Thread.sleep(3000);
            snapshots = spaceQuotaManager.copyQuotaSnapshots();
            regionSizes = getReportedSizesForTable(tn);
        } 
        // Our quota limit should be reflected in the latest snapshot
        SpaceQuotaSnapshot snapshot = snapshots.get(tn);
        Assert.assertEquals(0L, snapshot.getUsage());
        Assert.assertEquals(sizeLimit, snapshot.getLimit());
        // We would also not have a "real" policy in violation
        ActivePolicyEnforcement activePolicies = spaceQuotaManager.getActiveEnforcements();
        SpaceViolationPolicyEnforcement enforcement = activePolicies.getPolicyEnforcement(tn);
        Assert.assertTrue(("Expected to find Noop policy, but got " + (enforcement.getClass().getSimpleName())), (enforcement instanceof DefaultViolationPolicyEnforcement));
        // Should generate two files, each of which is over 25KB each
        ClientServiceCallable<Boolean> callable = helper.generateFileToLoad(tn, 2, 525);
        FileSystem fs = TestSpaceQuotas.TEST_UTIL.getTestFileSystem();
        FileStatus[] files = fs.listStatus(new org.apache.hadoop.fs.Path(fs.getHomeDirectory(), ((testName.getMethodName()) + "_files")));
        for (FileStatus file : files) {
            Assert.assertTrue(((("Expected the file, " + (file.getPath())) + ",  length to be larger than 25KB, but was ") + (file.getLen())), ((file.getLen()) > (25 * (SpaceQuotaHelperForTests.ONE_KILOBYTE))));
            TestSpaceQuotas.LOG.debug(((((file.getPath()) + " -> ") + (file.getLen())) + "B"));
        }
        RpcRetryingCallerFactory factory = new RpcRetryingCallerFactory(TestSpaceQuotas.TEST_UTIL.getConfiguration());
        RpcRetryingCaller<Boolean> caller = factory.newCaller();
        try {
            caller.callWithRetries(callable, Integer.MAX_VALUE);
            Assert.fail("Expected the bulk load call to fail!");
        } catch (SpaceLimitingException e) {
            // Pass
            TestSpaceQuotas.LOG.trace("Caught expected exception", e);
        }
        // Verify that we have no data in the table because neither file should have been
        // loaded even though one of the files could have.
        Table table = TestSpaceQuotas.TEST_UTIL.getConnection().getTable(tn);
        ResultScanner scanner = table.getScanner(new Scan());
        try {
            Assert.assertNull("Expected no results", scanner.next());
        } finally {
            scanner.close();
        }
    }

    @Test
    public void testTableQuotaOverridesNamespaceQuota() throws Exception {
        final SpaceViolationPolicy policy = NO_INSERTS;
        final TableName tn = helper.createTableWithRegions(10);
        // 2MB limit on the table, 1GB limit on the namespace
        final long tableLimit = 2L * (SpaceQuotaHelperForTests.ONE_MEGABYTE);
        final long namespaceLimit = 1024L * (SpaceQuotaHelperForTests.ONE_MEGABYTE);
        TestSpaceQuotas.TEST_UTIL.getAdmin().setQuota(QuotaSettingsFactory.limitTableSpace(tn, tableLimit, policy));
        TestSpaceQuotas.TEST_UTIL.getAdmin().setQuota(QuotaSettingsFactory.limitNamespaceSpace(tn.getNamespaceAsString(), namespaceLimit, policy));
        // Write more data than should be allowed and flush it to disk
        helper.writeData(tn, (3L * (SpaceQuotaHelperForTests.ONE_MEGABYTE)));
        // This should be sufficient time for the chores to run and see the change.
        Thread.sleep(5000);
        // The write should be rejected because the table quota takes priority over the namespace
        Put p = new Put(Bytes.toBytes("to_reject"));
        p.addColumn(Bytes.toBytes(SpaceQuotaHelperForTests.F1), Bytes.toBytes("to"), Bytes.toBytes("reject"));
        verifyViolation(policy, tn, p);
    }

    @Test
    public void testSetQuotaAndThenRemoveWithNoInserts() throws Exception {
        setQuotaAndThenRemove(NO_INSERTS);
    }

    @Test
    public void testSetQuotaAndThenRemoveWithNoWrite() throws Exception {
        setQuotaAndThenRemove(NO_WRITES);
    }

    @Test
    public void testSetQuotaAndThenRemoveWithNoWritesCompactions() throws Exception {
        setQuotaAndThenRemove(NO_WRITES_COMPACTIONS);
    }

    @Test
    public void testSetQuotaAndThenRemoveWithDisable() throws Exception {
        setQuotaAndThenRemove(DISABLE);
    }

    @Test
    public void testSetQuotaAndThenDropTableWithNoInserts() throws Exception {
        setQuotaAndThenDropTable(NO_INSERTS);
    }

    @Test
    public void testSetQuotaAndThenDropTableWithNoWrite() throws Exception {
        setQuotaAndThenDropTable(NO_WRITES);
    }

    @Test
    public void testSetQuotaAndThenDropTableeWithNoWritesCompactions() throws Exception {
        setQuotaAndThenDropTable(NO_WRITES_COMPACTIONS);
    }

    @Test
    public void testSetQuotaAndThenDropTableWithDisable() throws Exception {
        setQuotaAndThenDropTable(DISABLE);
    }

    @Test
    public void testSetQuotaAndThenIncreaseQuotaWithNoInserts() throws Exception {
        setQuotaAndThenIncreaseQuota(NO_INSERTS);
    }

    @Test
    public void testSetQuotaAndThenIncreaseQuotaWithNoWrite() throws Exception {
        setQuotaAndThenIncreaseQuota(NO_WRITES);
    }

    @Test
    public void testSetQuotaAndThenIncreaseQuotaWithNoWritesCompactions() throws Exception {
        setQuotaAndThenIncreaseQuota(NO_WRITES_COMPACTIONS);
    }

    @Test
    public void testSetQuotaAndThenRemoveInOneWithNoInserts() throws Exception {
        setQuotaAndThenRemoveInOneAmongTwoTables(NO_INSERTS);
    }

    @Test
    public void testSetQuotaAndThenRemoveInOneWithNoWrite() throws Exception {
        setQuotaAndThenRemoveInOneAmongTwoTables(NO_WRITES);
    }

    @Test
    public void testSetQuotaAndThenRemoveInOneWithNoWritesCompaction() throws Exception {
        setQuotaAndThenRemoveInOneAmongTwoTables(NO_WRITES_COMPACTIONS);
    }

    @Test
    public void testSetQuotaAndThenRemoveInOneWithDisable() throws Exception {
        setQuotaAndThenRemoveInOneAmongTwoTables(DISABLE);
    }

    @Test
    public void testSetQuotaOnNonExistingTableWithNoInserts() throws Exception {
        setQuotaLimit(NON_EXISTENT_TABLE, NO_INSERTS, 2L);
    }

    @Test
    public void testSetQuotaOnNonExistingTableWithNoWrites() throws Exception {
        setQuotaLimit(NON_EXISTENT_TABLE, NO_WRITES, 2L);
    }

    @Test
    public void testSetQuotaOnNonExistingTableWithNoWritesCompaction() throws Exception {
        setQuotaLimit(NON_EXISTENT_TABLE, NO_WRITES_COMPACTIONS, 2L);
    }

    @Test
    public void testSetQuotaOnNonExistingTableWithDisable() throws Exception {
        setQuotaLimit(NON_EXISTENT_TABLE, DISABLE, 2L);
    }
}

