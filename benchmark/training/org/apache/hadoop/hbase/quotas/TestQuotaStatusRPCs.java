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


import SpaceViolationPolicy.NO_INSERTS;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.HBaseTestingUtility;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.Waiter;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.RegionInfo;
import org.apache.hadoop.hbase.client.RetriesExhaustedWithDetailsException;
import org.apache.hadoop.hbase.master.HMaster;
import org.apache.hadoop.hbase.quotas.SpaceQuotaSnapshot.SpaceQuotaStatus;
import org.apache.hadoop.hbase.quotas.policies.MissingSnapshotViolationPolicyEnforcement;
import org.apache.hadoop.hbase.regionserver.HRegionServer;
import org.apache.hadoop.hbase.testclassification.MediumTests;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.TestName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Test class for the quota status RPCs in the master and regionserver.
 */
@Category({ MediumTests.class })
public class TestQuotaStatusRPCs {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestQuotaStatusRPCs.class);

    private static final Logger LOG = LoggerFactory.getLogger(TestQuotaStatusRPCs.class);

    private static final HBaseTestingUtility TEST_UTIL = new HBaseTestingUtility();

    private static final AtomicLong COUNTER = new AtomicLong(0);

    @Rule
    public TestName testName = new TestName();

    private SpaceQuotaHelperForTests helper;

    @Test
    public void testRegionSizesFromMaster() throws Exception {
        final long tableSize = 1024L * 10L;// 10KB

        final int numRegions = 10;
        final TableName tn = helper.createTableWithRegions(numRegions);
        // Will write at least `tableSize` data
        helper.writeData(tn, tableSize);
        final HMaster master = TestQuotaStatusRPCs.TEST_UTIL.getMiniHBaseCluster().getMaster();
        final MasterQuotaManager quotaManager = master.getMasterQuotaManager();
        // Make sure the master has all of the reports
        Waiter.waitFor(TestQuotaStatusRPCs.TEST_UTIL.getConfiguration(), (30 * 1000), new org.apache.hadoop.hbase.Waiter.Predicate<Exception>() {
            @Override
            public boolean evaluate() throws Exception {
                Map<RegionInfo, Long> regionSizes = quotaManager.snapshotRegionSizes();
                TestQuotaStatusRPCs.LOG.trace(("Region sizes=" + regionSizes));
                return (numRegions == (countRegionsForTable(tn, regionSizes))) && (tableSize <= (getTableSize(tn, regionSizes)));
            }
        });
        Map<TableName, Long> sizes = TestQuotaStatusRPCs.TEST_UTIL.getAdmin().getSpaceQuotaTableSizes();
        Long size = sizes.get(tn);
        Assert.assertNotNull(("No reported size for " + tn), size);
        Assert.assertTrue(("Reported table size was " + size), ((size.longValue()) >= tableSize));
    }

    @Test
    public void testQuotaSnapshotsFromRS() throws Exception {
        final long sizeLimit = 1024L * 1024L;// 1MB

        final long tableSize = 1024L * 10L;// 10KB

        final int numRegions = 10;
        final TableName tn = helper.createTableWithRegions(numRegions);
        // Define the quota
        QuotaSettings settings = QuotaSettingsFactory.limitTableSpace(tn, sizeLimit, NO_INSERTS);
        TestQuotaStatusRPCs.TEST_UTIL.getAdmin().setQuota(settings);
        // Write at least `tableSize` data
        helper.writeData(tn, tableSize);
        final HRegionServer rs = TestQuotaStatusRPCs.TEST_UTIL.getMiniHBaseCluster().getRegionServer(0);
        final RegionServerSpaceQuotaManager manager = rs.getRegionServerSpaceQuotaManager();
        Waiter.waitFor(TestQuotaStatusRPCs.TEST_UTIL.getConfiguration(), (30 * 1000), new org.apache.hadoop.hbase.Waiter.Predicate<Exception>() {
            @Override
            public boolean evaluate() throws Exception {
                SpaceQuotaSnapshot snapshot = manager.copyQuotaSnapshots().get(tn);
                if (snapshot == null) {
                    return false;
                }
                return (snapshot.getUsage()) >= tableSize;
            }
        });
        @SuppressWarnings("unchecked")
        Map<TableName, SpaceQuotaSnapshot> snapshots = ((Map<TableName, SpaceQuotaSnapshot>) (TestQuotaStatusRPCs.TEST_UTIL.getAdmin().getRegionServerSpaceQuotaSnapshots(rs.getServerName())));
        SpaceQuotaSnapshot snapshot = snapshots.get(tn);
        Assert.assertNotNull(("Did not find snapshot for " + tn), snapshot);
        Assert.assertTrue(("Observed table usage was " + (snapshot.getUsage())), ((snapshot.getUsage()) >= tableSize));
        Assert.assertEquals(sizeLimit, snapshot.getLimit());
        SpaceQuotaStatus pbStatus = snapshot.getQuotaStatus();
        Assert.assertFalse(pbStatus.isInViolation());
    }

    @Test
    public void testQuotaEnforcementsFromRS() throws Exception {
        final long sizeLimit = 1024L * 8L;// 8KB

        final long tableSize = 1024L * 10L;// 10KB

        final int numRegions = 10;
        final TableName tn = helper.createTableWithRegions(numRegions);
        // Define the quota
        QuotaSettings settings = QuotaSettingsFactory.limitTableSpace(tn, sizeLimit, NO_INSERTS);
        TestQuotaStatusRPCs.TEST_UTIL.getAdmin().setQuota(settings);
        // Write at least `tableSize` data
        try {
            helper.writeData(tn, tableSize);
        } catch (RetriesExhaustedWithDetailsException | SpaceLimitingException e) {
            // Pass
        }
        final HRegionServer rs = TestQuotaStatusRPCs.TEST_UTIL.getMiniHBaseCluster().getRegionServer(0);
        final RegionServerSpaceQuotaManager manager = rs.getRegionServerSpaceQuotaManager();
        Waiter.waitFor(TestQuotaStatusRPCs.TEST_UTIL.getConfiguration(), (30 * 1000), new org.apache.hadoop.hbase.Waiter.Predicate<Exception>() {
            @Override
            public boolean evaluate() throws Exception {
                ActivePolicyEnforcement enforcements = manager.getActiveEnforcements();
                SpaceViolationPolicyEnforcement enforcement = enforcements.getPolicyEnforcement(tn);
                // Signifies that we're waiting on the quota snapshot to be fetched
                if (enforcement instanceof MissingSnapshotViolationPolicyEnforcement) {
                    return false;
                }
                return enforcement.getQuotaSnapshot().getQuotaStatus().isInViolation();
            }
        });
        // We obtain the violations for a RegionServer by observing the snapshots
        @SuppressWarnings("unchecked")
        Map<TableName, SpaceQuotaSnapshot> snapshots = ((Map<TableName, SpaceQuotaSnapshot>) (TestQuotaStatusRPCs.TEST_UTIL.getAdmin().getRegionServerSpaceQuotaSnapshots(rs.getServerName())));
        SpaceQuotaSnapshot snapshot = snapshots.get(tn);
        Assert.assertNotNull(("Did not find snapshot for " + tn), snapshot);
        Assert.assertTrue(snapshot.getQuotaStatus().isInViolation());
        Assert.assertEquals(NO_INSERTS, snapshot.getQuotaStatus().getPolicy().get());
    }

    @Test
    public void testQuotaStatusFromMaster() throws Exception {
        final long sizeLimit = 1024L * 25L;// 25KB

        // As of 2.0.0-beta-2, this 1KB of "Cells" actually results in about 15KB on disk (HFiles)
        // This is skewed a bit since we're writing such little data, so the test needs to keep
        // this in mind; else, the quota will be in violation before the test expects it to be.
        final long tableSize = 1024L * 1;// 1KB

        final long nsLimit = Long.MAX_VALUE;
        final int numRegions = 10;
        final TableName tn = helper.createTableWithRegions(numRegions);
        // Define the quota
        QuotaSettings settings = QuotaSettingsFactory.limitTableSpace(tn, sizeLimit, NO_INSERTS);
        TestQuotaStatusRPCs.TEST_UTIL.getAdmin().setQuota(settings);
        QuotaSettings nsSettings = QuotaSettingsFactory.limitNamespaceSpace(tn.getNamespaceAsString(), nsLimit, NO_INSERTS);
        TestQuotaStatusRPCs.TEST_UTIL.getAdmin().setQuota(nsSettings);
        // Write at least `tableSize` data
        helper.writeData(tn, tableSize);
        final Connection conn = TestQuotaStatusRPCs.TEST_UTIL.getConnection();
        // Make sure the master has a snapshot for our table
        Waiter.waitFor(TestQuotaStatusRPCs.TEST_UTIL.getConfiguration(), (30 * 1000), new org.apache.hadoop.hbase.Waiter.Predicate<Exception>() {
            @Override
            public boolean evaluate() throws Exception {
                SpaceQuotaSnapshot snapshot = ((SpaceQuotaSnapshot) (conn.getAdmin().getCurrentSpaceQuotaSnapshot(tn)));
                TestQuotaStatusRPCs.LOG.info(("Table snapshot after initial ingest: " + snapshot));
                if (snapshot == null) {
                    return false;
                }
                return ((snapshot.getLimit()) == sizeLimit) && ((snapshot.getUsage()) > 0L);
            }
        });
        final AtomicReference<Long> nsUsage = new AtomicReference<>();
        // If we saw the table snapshot, we should also see the namespace snapshot
        Waiter.waitFor(TestQuotaStatusRPCs.TEST_UTIL.getConfiguration(), ((30 * 1000) * 1000), new org.apache.hadoop.hbase.Waiter.Predicate<Exception>() {
            @Override
            public boolean evaluate() throws Exception {
                SpaceQuotaSnapshot snapshot = ((SpaceQuotaSnapshot) (conn.getAdmin().getCurrentSpaceQuotaSnapshot(tn.getNamespaceAsString())));
                TestQuotaStatusRPCs.LOG.debug(("Namespace snapshot after initial ingest: " + snapshot));
                if (snapshot == null) {
                    return false;
                }
                nsUsage.set(snapshot.getUsage());
                return ((snapshot.getLimit()) == nsLimit) && ((snapshot.getUsage()) > 0);
            }
        });
        // Sanity check: the below assertions will fail if we somehow write too much data
        // and force the table to move into violation before we write the second bit of data.
        SpaceQuotaSnapshot snapshot = ((SpaceQuotaSnapshot) (conn.getAdmin().getCurrentSpaceQuotaSnapshot(tn)));
        Assert.assertTrue((("QuotaSnapshot for " + tn) + " should be non-null and not in violation"), ((snapshot != null) && (!(snapshot.getQuotaStatus().isInViolation()))));
        try {
            helper.writeData(tn, (tableSize * 2L));
        } catch (RetriesExhaustedWithDetailsException | SpaceLimitingException e) {
            // Pass
        }
        // Wait for the status to move to violation
        Waiter.waitFor(TestQuotaStatusRPCs.TEST_UTIL.getConfiguration(), (30 * 1000), new org.apache.hadoop.hbase.Waiter.Predicate<Exception>() {
            @Override
            public boolean evaluate() throws Exception {
                SpaceQuotaSnapshot snapshot = ((SpaceQuotaSnapshot) (conn.getAdmin().getCurrentSpaceQuotaSnapshot(tn)));
                TestQuotaStatusRPCs.LOG.info(("Table snapshot after second ingest: " + snapshot));
                if (snapshot == null) {
                    return false;
                }
                return snapshot.getQuotaStatus().isInViolation();
            }
        });
        // The namespace should still not be in violation, but have a larger usage than previously
        Waiter.waitFor(TestQuotaStatusRPCs.TEST_UTIL.getConfiguration(), (30 * 1000), new org.apache.hadoop.hbase.Waiter.Predicate<Exception>() {
            @Override
            public boolean evaluate() throws Exception {
                SpaceQuotaSnapshot snapshot = ((SpaceQuotaSnapshot) (conn.getAdmin().getCurrentSpaceQuotaSnapshot(tn.getNamespaceAsString())));
                TestQuotaStatusRPCs.LOG.debug(("Namespace snapshot after second ingest: " + snapshot));
                if (snapshot == null) {
                    return false;
                }
                return ((snapshot.getUsage()) > (nsUsage.get())) && (!(snapshot.getQuotaStatus().isInViolation()));
            }
        });
    }
}

