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


import NamespaceDescriptor.DEFAULT_NAMESPACE_NAME_STR;
import QuotaUtil.QUOTA_TABLE_NAME;
import SpaceViolationPolicy.NO_INSERTS;
import ThrottleType.WRITE_NUMBER;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.hbase.Cell;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.HBaseTestingUtility;
import org.apache.hadoop.hbase.NamespaceDescriptor;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Admin;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.SnapshotType;
import org.apache.hadoop.hbase.client.Table;
import org.apache.hadoop.hbase.master.HMaster;
import org.apache.hadoop.hbase.testclassification.MediumTests;
import org.apache.hbase.thirdparty.com.google.common.collect.HashMultimap;
import org.apache.hbase.thirdparty.com.google.common.collect.ImmutableMap;
import org.apache.hbase.thirdparty.com.google.common.collect.Multimap;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.TestName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Test class for the {@link SnapshotQuotaObserverChore}.
 */
@Category(MediumTests.class)
public class TestSnapshotQuotaObserverChore {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestSnapshotQuotaObserverChore.class);

    private static final Logger LOG = LoggerFactory.getLogger(TestSnapshotQuotaObserverChore.class);

    private static final HBaseTestingUtility TEST_UTIL = new HBaseTestingUtility();

    private static final AtomicLong COUNTER = new AtomicLong();

    @Rule
    public TestName testName = new TestName();

    private Connection conn;

    private Admin admin;

    private SpaceQuotaHelperForTests helper;

    private HMaster master;

    private SnapshotQuotaObserverChore testChore;

    @Test
    public void testSnapshotsFromTables() throws Exception {
        TableName tn1 = helper.createTableWithRegions(1);
        TableName tn2 = helper.createTableWithRegions(1);
        TableName tn3 = helper.createTableWithRegions(1);
        // Set a space quota on table 1 and 2 (but not 3)
        admin.setQuota(QuotaSettingsFactory.limitTableSpace(tn1, SpaceQuotaHelperForTests.ONE_GIGABYTE, NO_INSERTS));
        admin.setQuota(QuotaSettingsFactory.limitTableSpace(tn2, SpaceQuotaHelperForTests.ONE_GIGABYTE, NO_INSERTS));
        // Create snapshots on each table (we didn't write any data, so just skipflush)
        admin.snapshot(new org.apache.hadoop.hbase.client.SnapshotDescription((tn1 + "snapshot"), tn1, SnapshotType.SKIPFLUSH));
        admin.snapshot(new org.apache.hadoop.hbase.client.SnapshotDescription((tn2 + "snapshot"), tn2, SnapshotType.SKIPFLUSH));
        admin.snapshot(new org.apache.hadoop.hbase.client.SnapshotDescription((tn3 + "snapshot"), tn3, SnapshotType.SKIPFLUSH));
        Multimap<TableName, String> mapping = testChore.getSnapshotsToComputeSize();
        Assert.assertEquals(2, mapping.size());
        Assert.assertEquals(1, mapping.get(tn1).size());
        Assert.assertEquals((tn1 + "snapshot"), mapping.get(tn1).iterator().next());
        Assert.assertEquals(1, mapping.get(tn2).size());
        Assert.assertEquals((tn2 + "snapshot"), mapping.get(tn2).iterator().next());
        admin.snapshot(new org.apache.hadoop.hbase.client.SnapshotDescription((tn2 + "snapshot1"), tn2, SnapshotType.SKIPFLUSH));
        admin.snapshot(new org.apache.hadoop.hbase.client.SnapshotDescription((tn3 + "snapshot1"), tn3, SnapshotType.SKIPFLUSH));
        mapping = testChore.getSnapshotsToComputeSize();
        Assert.assertEquals(3, mapping.size());
        Assert.assertEquals(1, mapping.get(tn1).size());
        Assert.assertEquals((tn1 + "snapshot"), mapping.get(tn1).iterator().next());
        Assert.assertEquals(2, mapping.get(tn2).size());
        Assert.assertEquals(new HashSet<String>(Arrays.asList((tn2 + "snapshot"), (tn2 + "snapshot1"))), mapping.get(tn2));
    }

    @Test
    public void testSnapshotsFromNamespaces() throws Exception {
        NamespaceDescriptor ns = NamespaceDescriptor.create("snapshots_from_namespaces").build();
        admin.createNamespace(ns);
        TableName tn1 = helper.createTableWithRegions(ns.getName(), 1);
        TableName tn2 = helper.createTableWithRegions(ns.getName(), 1);
        TableName tn3 = helper.createTableWithRegions(1);
        // Set a throttle quota on 'default' namespace
        admin.setQuota(QuotaSettingsFactory.throttleNamespace(tn3.getNamespaceAsString(), WRITE_NUMBER, 100, TimeUnit.SECONDS));
        // Set a user throttle quota
        admin.setQuota(QuotaSettingsFactory.throttleUser("user", WRITE_NUMBER, 100, TimeUnit.MINUTES));
        // Set a space quota on the namespace
        admin.setQuota(QuotaSettingsFactory.limitNamespaceSpace(ns.getName(), SpaceQuotaHelperForTests.ONE_GIGABYTE, NO_INSERTS));
        // Create snapshots on each table (we didn't write any data, so just skipflush)
        admin.snapshot(new org.apache.hadoop.hbase.client.SnapshotDescription(((tn1.getQualifierAsString()) + "snapshot"), tn1, SnapshotType.SKIPFLUSH));
        admin.snapshot(new org.apache.hadoop.hbase.client.SnapshotDescription(((tn2.getQualifierAsString()) + "snapshot"), tn2, SnapshotType.SKIPFLUSH));
        admin.snapshot(new org.apache.hadoop.hbase.client.SnapshotDescription(((tn3.getQualifierAsString()) + "snapshot"), tn3, SnapshotType.SKIPFLUSH));
        Multimap<TableName, String> mapping = testChore.getSnapshotsToComputeSize();
        Assert.assertEquals(2, mapping.size());
        Assert.assertEquals(1, mapping.get(tn1).size());
        Assert.assertEquals(((tn1.getQualifierAsString()) + "snapshot"), mapping.get(tn1).iterator().next());
        Assert.assertEquals(1, mapping.get(tn2).size());
        Assert.assertEquals(((tn2.getQualifierAsString()) + "snapshot"), mapping.get(tn2).iterator().next());
        admin.snapshot(new org.apache.hadoop.hbase.client.SnapshotDescription(((tn2.getQualifierAsString()) + "snapshot1"), tn2, SnapshotType.SKIPFLUSH));
        admin.snapshot(new org.apache.hadoop.hbase.client.SnapshotDescription(((tn3.getQualifierAsString()) + "snapshot2"), tn3, SnapshotType.SKIPFLUSH));
        mapping = testChore.getSnapshotsToComputeSize();
        Assert.assertEquals(3, mapping.size());
        Assert.assertEquals(1, mapping.get(tn1).size());
        Assert.assertEquals(((tn1.getQualifierAsString()) + "snapshot"), mapping.get(tn1).iterator().next());
        Assert.assertEquals(2, mapping.get(tn2).size());
        Assert.assertEquals(new HashSet<String>(Arrays.asList(((tn2.getQualifierAsString()) + "snapshot"), ((tn2.getQualifierAsString()) + "snapshot1"))), mapping.get(tn2));
    }

    @Test
    public void testSnapshotSize() throws Exception {
        // Create a table and set a quota
        TableName tn1 = helper.createTableWithRegions(5);
        admin.setQuota(QuotaSettingsFactory.limitTableSpace(tn1, SpaceQuotaHelperForTests.ONE_GIGABYTE, NO_INSERTS));
        // Write some data and flush it
        helper.writeData(tn1, (256L * (SpaceQuotaHelperForTests.ONE_KILOBYTE)));
        admin.flush(tn1);
        final long snapshotSize = TestSnapshotQuotaObserverChore.TEST_UTIL.getMiniHBaseCluster().getRegions(tn1).stream().flatMap(( r) -> r.getStores().stream()).mapToLong(HStore::getHFilesSize).sum();
        // Wait for the Master chore to run to see the usage (with a fudge factor)
        TestSnapshotQuotaObserverChore.TEST_UTIL.waitFor(30000, new SpaceQuotaHelperForTests.SpaceQuotaSnapshotPredicate(conn, tn1) {
            @Override
            boolean evaluate(SpaceQuotaSnapshot snapshot) throws Exception {
                return (snapshot.getUsage()) == snapshotSize;
            }
        });
        // Create a snapshot on the table
        final String snapshotName = tn1 + "snapshot";
        admin.snapshot(new org.apache.hadoop.hbase.client.SnapshotDescription(snapshotName, tn1, SnapshotType.SKIPFLUSH));
        // Get the snapshots
        Multimap<TableName, String> snapshotsToCompute = testChore.getSnapshotsToComputeSize();
        Assert.assertEquals(("Expected to see the single snapshot: " + snapshotsToCompute), 1, snapshotsToCompute.size());
        // Get the size of our snapshot
        Map<String, Long> namespaceSnapshotSizes = testChore.computeSnapshotSizes(snapshotsToCompute);
        Assert.assertEquals(1, namespaceSnapshotSizes.size());
        Long size = namespaceSnapshotSizes.get(tn1.getNamespaceAsString());
        Assert.assertNotNull(size);
        // The snapshot should take up no space since the table refers to it completely
        Assert.assertEquals(0, size.longValue());
        // Write some more data, flush it, and then major_compact the table
        helper.writeData(tn1, (256L * (SpaceQuotaHelperForTests.ONE_KILOBYTE)));
        admin.flush(tn1);
        TestSnapshotQuotaObserverChore.TEST_UTIL.compact(tn1, true);
        // Test table should reflect it's original size since ingest was deterministic
        TestSnapshotQuotaObserverChore.TEST_UTIL.waitFor(30000, new SpaceQuotaHelperForTests.SpaceQuotaSnapshotPredicate(conn, tn1) {
            private final long regionSize = TestSnapshotQuotaObserverChore.TEST_UTIL.getMiniHBaseCluster().getRegions(tn1).stream().flatMap(( r) -> r.getStores().stream()).mapToLong(HStore::getHFilesSize).sum();

            @Override
            boolean evaluate(SpaceQuotaSnapshot snapshot) throws Exception {
                TestSnapshotQuotaObserverChore.LOG.debug(((("Current usage=" + (snapshot.getUsage())) + " snapshotSize=") + snapshotSize));
                // The usage of table space consists of region size and snapshot size
                return closeInSize(snapshot.getUsage(), (snapshotSize + (regionSize)), SpaceQuotaHelperForTests.ONE_KILOBYTE);
            }
        });
        // Wait for no compacted files on the regions of our table
        TestSnapshotQuotaObserverChore.TEST_UTIL.waitFor(30000, new SpaceQuotaHelperForTests.NoFilesToDischarge(TestSnapshotQuotaObserverChore.TEST_UTIL.getMiniHBaseCluster(), tn1));
        // Still should see only one snapshot
        snapshotsToCompute = testChore.getSnapshotsToComputeSize();
        Assert.assertEquals(("Expected to see the single snapshot: " + snapshotsToCompute), 1, snapshotsToCompute.size());
        namespaceSnapshotSizes = testChore.computeSnapshotSizes(snapshotsToCompute);
        Assert.assertEquals(1, namespaceSnapshotSizes.size());
        size = namespaceSnapshotSizes.get(tn1.getNamespaceAsString());
        Assert.assertNotNull(size);
        // The snapshot should take up the size the table originally took up
        Assert.assertEquals(snapshotSize, size.longValue());
    }

    @Test
    public void testPersistingSnapshotsForNamespaces() throws Exception {
        TableName tn1 = TableName.valueOf("ns1:tn1");
        TableName tn2 = TableName.valueOf("ns1:tn2");
        TableName tn3 = TableName.valueOf("ns2:tn1");
        TableName tn4 = TableName.valueOf("ns2:tn2");
        TableName tn5 = TableName.valueOf("tn1");
        // Shim in a custom factory to avoid computing snapshot sizes.
        FileArchiverNotifierFactory test = new FileArchiverNotifierFactory() {
            Map<TableName, Long> tableToSize = ImmutableMap.of(tn1, 1024L, tn2, 1024L, tn3, 512L, tn4, 1024L, tn5, 3072L);

            @Override
            public FileArchiverNotifier get(Connection conn, Configuration conf, FileSystem fs, TableName tn) {
                return new FileArchiverNotifier() {
                    @Override
                    public void addArchivedFiles(Set<Map.Entry<String, Long>> fileSizes) throws IOException {
                    }

                    @Override
                    public long computeAndStoreSnapshotSizes(Collection<String> currentSnapshots) throws IOException {
                        return tableToSize.get(tn);
                    }
                };
            }
        };
        try {
            FileArchiverNotifierFactoryImpl.setInstance(test);
            Multimap<TableName, String> snapshotsToCompute = HashMultimap.create();
            snapshotsToCompute.put(tn1, "");
            snapshotsToCompute.put(tn2, "");
            snapshotsToCompute.put(tn3, "");
            snapshotsToCompute.put(tn4, "");
            snapshotsToCompute.put(tn5, "");
            Map<String, Long> nsSizes = testChore.computeSnapshotSizes(snapshotsToCompute);
            Assert.assertEquals(3, nsSizes.size());
            Assert.assertEquals(2048L, ((long) (nsSizes.get("ns1"))));
            Assert.assertEquals(1536L, ((long) (nsSizes.get("ns2"))));
            Assert.assertEquals(3072L, ((long) (nsSizes.get(DEFAULT_NAMESPACE_NAME_STR))));
        } finally {
            FileArchiverNotifierFactoryImpl.reset();
        }
    }

    @Test
    public void testBucketingFilesToSnapshots() throws Exception {
        // Create a table and set a quota
        TableName tn1 = helper.createTableWithRegions(1);
        admin.setQuota(QuotaSettingsFactory.limitTableSpace(tn1, SpaceQuotaHelperForTests.ONE_GIGABYTE, NO_INSERTS));
        // Write some data and flush it
        helper.writeData(tn1, (256L * (SpaceQuotaHelperForTests.ONE_KILOBYTE)));
        admin.flush(tn1);
        final AtomicReference<Long> lastSeenSize = new AtomicReference<>();
        // Wait for the Master chore to run to see the usage (with a fudge factor)
        TestSnapshotQuotaObserverChore.TEST_UTIL.waitFor(30000, new SpaceQuotaHelperForTests.SpaceQuotaSnapshotPredicate(conn, tn1) {
            @Override
            boolean evaluate(SpaceQuotaSnapshot snapshot) throws Exception {
                lastSeenSize.set(snapshot.getUsage());
                return (snapshot.getUsage()) > (230L * (SpaceQuotaHelperForTests.ONE_KILOBYTE));
            }
        });
        // Create a snapshot on the table
        final String snapshotName1 = tn1 + "snapshot1";
        admin.snapshot(new org.apache.hadoop.hbase.client.SnapshotDescription(snapshotName1, tn1, SnapshotType.SKIPFLUSH));
        // Major compact the table to force a rewrite
        TestSnapshotQuotaObserverChore.TEST_UTIL.compact(tn1, true);
        // Make sure that the snapshot owns the size
        final Table quotaTable = conn.getTable(QUOTA_TABLE_NAME);
        TestSnapshotQuotaObserverChore.TEST_UTIL.waitFor(30000, new org.apache.hadoop.hbase.Waiter.Predicate<Exception>() {
            @Override
            public boolean evaluate() throws Exception {
                Get g = QuotaTableUtil.makeGetForSnapshotSize(tn1, snapshotName1);
                Result r = quotaTable.get(g);
                if ((r == null) || (r.isEmpty())) {
                    return false;
                }
                r.advance();
                Cell c = r.current();
                // The compaction result file has an additional compaction event tracker
                return (lastSeenSize.get()) <= (QuotaTableUtil.parseSnapshotSize(c));
            }
        });
        // Create another snapshot on the table
        final String snapshotName2 = tn1 + "snapshot2";
        admin.snapshot(new org.apache.hadoop.hbase.client.SnapshotDescription(snapshotName2, tn1, SnapshotType.SKIPFLUSH));
        // Major compact the table to force a rewrite
        TestSnapshotQuotaObserverChore.TEST_UTIL.compact(tn1, true);
        // Make sure that the snapshot owns the size
        waitFor(30000, new org.apache.hadoop.hbase.Waiter.Predicate<Exception>() {
            @Override
            public boolean evaluate() throws Exception {
                Get g = QuotaTableUtil.makeGetForSnapshotSize(tn1, snapshotName2);
                Result r = quotaTable.get(g);
                if ((r == null) || (r.isEmpty())) {
                    return false;
                }
                r.advance();
                Cell c = r.current();
                // The compaction result file has an additional compaction event tracker
                return (lastSeenSize.get()) <= (QuotaTableUtil.parseSnapshotSize(c));
            }
        });
        Get g = QuotaTableUtil.createGetNamespaceSnapshotSize(tn1.getNamespaceAsString());
        Result r = quotaTable.get(g);
        Assert.assertNotNull(r);
        Assert.assertFalse(r.isEmpty());
        r.advance();
        long size = QuotaTableUtil.parseSnapshotSize(r.current());
        Assert.assertTrue((((lastSeenSize.get()) * 2) <= size));
    }
}

