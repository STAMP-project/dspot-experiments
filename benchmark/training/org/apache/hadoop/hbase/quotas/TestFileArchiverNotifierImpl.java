/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to you under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.hadoop.hbase.quotas;


import QuotaTableUtil.QUOTA_FAMILY_USAGE;
import QuotaUtil.QUOTA_FAMILY_INFO;
import QuotaUtil.QUOTA_TABLE_NAME;
import SpaceViolationPolicy.NO_INSERTS;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.HBaseTestingUtility;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Admin;
import org.apache.hadoop.hbase.client.ColumnFamilyDescriptorBuilder;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.SnapshotType;
import org.apache.hadoop.hbase.client.Table;
import org.apache.hadoop.hbase.client.TableDescriptor;
import org.apache.hadoop.hbase.client.TableDescriptorBuilder;
import org.apache.hadoop.hbase.quotas.FileArchiverNotifierImpl.SnapshotWithSize;
import org.apache.hadoop.hbase.testclassification.MediumTests;
import org.apache.hbase.thirdparty.com.google.common.collect.ImmutableSet;
import org.apache.hbase.thirdparty.com.google.common.collect.Iterables;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.TestName;


/**
 * Test class for {@link FileArchiverNotifierImpl}.
 */
@Category(MediumTests.class)
public class TestFileArchiverNotifierImpl {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestFileArchiverNotifierImpl.class);

    private static final HBaseTestingUtility TEST_UTIL = new HBaseTestingUtility();

    private static final AtomicLong COUNTER = new AtomicLong();

    @Rule
    public TestName testName = new TestName();

    private Connection conn;

    private Admin admin;

    private SpaceQuotaHelperForTests helper;

    private FileSystem fs;

    private Configuration conf;

    @Test
    public void testSnapshotSizePersistence() throws IOException {
        final Admin admin = TestFileArchiverNotifierImpl.TEST_UTIL.getAdmin();
        final TableName tn = TableName.valueOf(testName.getMethodName());
        if (admin.tableExists(tn)) {
            admin.disableTable(tn);
            admin.deleteTable(tn);
        }
        TableDescriptor desc = TableDescriptorBuilder.newBuilder(tn).setColumnFamily(ColumnFamilyDescriptorBuilder.of(QUOTA_FAMILY_USAGE)).build();
        admin.createTable(desc);
        FileArchiverNotifierImpl notifier = new FileArchiverNotifierImpl(conn, conf, fs, tn);
        List<SnapshotWithSize> snapshotsWithSizes = new ArrayList<>();
        try (Table table = conn.getTable(tn)) {
            // Writing no values will result in no records written.
            verify(table, () -> {
                notifier.persistSnapshotSizes(table, snapshotsWithSizes);
                assertEquals(0, count(table));
            });
            verify(table, () -> {
                snapshotsWithSizes.add(new SnapshotWithSize("ss1", 1024L));
                snapshotsWithSizes.add(new SnapshotWithSize("ss2", 4096L));
                notifier.persistSnapshotSizes(table, snapshotsWithSizes);
                assertEquals(2, count(table));
                assertEquals(1024L, extractSnapshotSize(table, tn, "ss1"));
                assertEquals(4096L, extractSnapshotSize(table, tn, "ss2"));
            });
        }
    }

    @Test
    public void testIncrementalFileArchiving() throws Exception {
        final Admin admin = TestFileArchiverNotifierImpl.TEST_UTIL.getAdmin();
        final TableName tn = TableName.valueOf(testName.getMethodName());
        if (admin.tableExists(tn)) {
            admin.disableTable(tn);
            admin.deleteTable(tn);
        }
        final Table quotaTable = conn.getTable(QUOTA_TABLE_NAME);
        final TableName tn1 = helper.createTableWithRegions(1);
        admin.setQuota(QuotaSettingsFactory.limitTableSpace(tn1, SpaceQuotaHelperForTests.ONE_GIGABYTE, NO_INSERTS));
        // Write some data and flush it
        helper.writeData(tn1, (256L * (SpaceQuotaHelperForTests.ONE_KILOBYTE)));
        admin.flush(tn1);
        // Create a snapshot on the table
        final String snapshotName1 = tn1 + "snapshot1";
        admin.snapshot(new org.apache.hadoop.hbase.client.SnapshotDescription(snapshotName1, tn1, SnapshotType.SKIPFLUSH));
        FileArchiverNotifierImpl notifier = new FileArchiverNotifierImpl(conn, conf, fs, tn);
        long t1 = notifier.getLastFullCompute();
        long snapshotSize = notifier.computeAndStoreSnapshotSizes(Arrays.asList(snapshotName1));
        Assert.assertEquals("The size of the snapshots should be zero", 0, snapshotSize);
        Assert.assertTrue("Last compute time was not less than current compute time", (t1 < (notifier.getLastFullCompute())));
        // No recently archived files and the snapshot should have no size
        Assert.assertEquals(0, extractSnapshotSize(quotaTable, tn, snapshotName1));
        // Invoke the addArchivedFiles method with no files
        notifier.addArchivedFiles(Collections.emptySet());
        // The size should not have changed
        Assert.assertEquals(0, extractSnapshotSize(quotaTable, tn, snapshotName1));
        notifier.addArchivedFiles(ImmutableSet.of(entry("a", 1024L), entry("b", 1024L)));
        // The size should not have changed
        Assert.assertEquals(0, extractSnapshotSize(quotaTable, tn, snapshotName1));
        // Pull one file referenced by the snapshot out of the manifest
        Set<String> referencedFiles = getFilesReferencedBySnapshot(snapshotName1);
        Assert.assertTrue(("Found snapshot referenced files: " + referencedFiles), ((referencedFiles.size()) >= 1));
        String referencedFile = Iterables.getFirst(referencedFiles, null);
        Assert.assertNotNull(referencedFile);
        // Report that a file this snapshot referenced was moved to the archive. This is a sign
        // that the snapshot should now "own" the size of this file
        final long fakeFileSize = 2048L;
        notifier.addArchivedFiles(ImmutableSet.of(entry(referencedFile, fakeFileSize)));
        // Verify that the snapshot owns this file.
        Assert.assertEquals(fakeFileSize, extractSnapshotSize(quotaTable, tn, snapshotName1));
        // In reality, we did not actually move the file, so a "full" computation should re-set the
        // size of the snapshot back to 0.
        long t2 = notifier.getLastFullCompute();
        snapshotSize = notifier.computeAndStoreSnapshotSizes(Arrays.asList(snapshotName1));
        Assert.assertEquals(0, snapshotSize);
        Assert.assertEquals(0, extractSnapshotSize(quotaTable, tn, snapshotName1));
        // We should also have no recently archived files after a re-computation
        Assert.assertTrue("Last compute time was not less than current compute time", (t2 < (notifier.getLastFullCompute())));
    }

    @Test
    public void testParseOldNamespaceSnapshotSize() throws Exception {
        final Admin admin = TestFileArchiverNotifierImpl.TEST_UTIL.getAdmin();
        final TableName fakeQuotaTableName = TableName.valueOf(testName.getMethodName());
        final TableName tn = TableName.valueOf(((testName.getMethodName()) + "1"));
        if (admin.tableExists(fakeQuotaTableName)) {
            admin.disableTable(fakeQuotaTableName);
            admin.deleteTable(fakeQuotaTableName);
        }
        TableDescriptor desc = TableDescriptorBuilder.newBuilder(fakeQuotaTableName).setColumnFamily(ColumnFamilyDescriptorBuilder.of(QUOTA_FAMILY_USAGE)).setColumnFamily(ColumnFamilyDescriptorBuilder.of(QUOTA_FAMILY_INFO)).build();
        admin.createTable(desc);
        final String ns = "";
        try (Table fakeQuotaTable = conn.getTable(fakeQuotaTableName)) {
            FileArchiverNotifierImpl notifier = new FileArchiverNotifierImpl(conn, conf, fs, tn);
            // Verify no record is treated as zero
            Assert.assertEquals(0, notifier.getPreviousNamespaceSnapshotSize(fakeQuotaTable, ns));
            // Set an explicit value of zero
            fakeQuotaTable.put(QuotaTableUtil.createPutForNamespaceSnapshotSize(ns, 0L));
            Assert.assertEquals(0, notifier.getPreviousNamespaceSnapshotSize(fakeQuotaTable, ns));
            // Set a non-zero value
            fakeQuotaTable.put(QuotaTableUtil.createPutForNamespaceSnapshotSize(ns, 1024L));
            Assert.assertEquals(1024L, notifier.getPreviousNamespaceSnapshotSize(fakeQuotaTable, ns));
        }
    }

    @FunctionalInterface
    private interface IOThrowingRunnable {
        void run() throws IOException;
    }
}

