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
package org.apache.hadoop.hbase.namespace;


import RegionInfo.COMPARATOR;
import TableNamespaceManager.KEY_MAX_REGIONS;
import TableNamespaceManager.KEY_MAX_TABLES;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hbase.Coprocessor;
import org.apache.hadoop.hbase.CoprocessorEnvironment;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.HBaseTestingUtility;
import org.apache.hadoop.hbase.HColumnDescriptor;
import org.apache.hadoop.hbase.HRegionInfo;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.MiniHBaseCluster;
import org.apache.hadoop.hbase.NamespaceDescriptor;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.Waiter;
import org.apache.hadoop.hbase.client.Admin;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.ConnectionFactory;
import org.apache.hadoop.hbase.client.DoNotRetryRegionException;
import org.apache.hadoop.hbase.client.RegionInfo;
import org.apache.hadoop.hbase.client.RegionLocator;
import org.apache.hadoop.hbase.client.Table;
import org.apache.hadoop.hbase.client.TableDescriptor;
import org.apache.hadoop.hbase.coprocessor.MasterCoprocessor;
import org.apache.hadoop.hbase.coprocessor.MasterCoprocessorEnvironment;
import org.apache.hadoop.hbase.coprocessor.MasterObserver;
import org.apache.hadoop.hbase.coprocessor.ObserverContext;
import org.apache.hadoop.hbase.coprocessor.RegionCoprocessor;
import org.apache.hadoop.hbase.coprocessor.RegionCoprocessorEnvironment;
import org.apache.hadoop.hbase.coprocessor.RegionObserver;
import org.apache.hadoop.hbase.coprocessor.RegionServerCoprocessor;
import org.apache.hadoop.hbase.coprocessor.RegionServerObserver;
import org.apache.hadoop.hbase.master.MasterCoprocessorHost;
import org.apache.hadoop.hbase.quotas.QuotaExceededException;
import org.apache.hadoop.hbase.regionserver.HRegion;
import org.apache.hadoop.hbase.regionserver.Store;
import org.apache.hadoop.hbase.regionserver.StoreFile;
import org.apache.hadoop.hbase.regionserver.compactions.CompactionLifeCycleTracker;
import org.apache.hadoop.hbase.regionserver.compactions.CompactionRequest;
import org.apache.hadoop.hbase.snapshot.RestoreSnapshotException;
import org.apache.hadoop.hbase.testclassification.MediumTests;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.hbase.util.FSUtils;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Category(MediumTests.class)
public class TestNamespaceAuditor {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestNamespaceAuditor.class);

    private static final Logger LOG = LoggerFactory.getLogger(TestNamespaceAuditor.class);

    private static final HBaseTestingUtility UTIL = new HBaseTestingUtility();

    private static Admin ADMIN;

    private String prefix = "TestNamespaceAuditor";

    @Test
    public void testTableOperations() throws Exception {
        String nsp = (prefix) + "_np2";
        NamespaceDescriptor nspDesc = NamespaceDescriptor.create(nsp).addConfiguration(KEY_MAX_REGIONS, "5").addConfiguration(KEY_MAX_TABLES, "2").build();
        TestNamespaceAuditor.ADMIN.createNamespace(nspDesc);
        Assert.assertNotNull("Namespace descriptor found null.", TestNamespaceAuditor.ADMIN.getNamespaceDescriptor(nsp));
        Assert.assertEquals(3, TestNamespaceAuditor.ADMIN.listNamespaceDescriptors().length);
        HColumnDescriptor fam1 = new HColumnDescriptor("fam1");
        HTableDescriptor tableDescOne = new HTableDescriptor(TableName.valueOf(((nsp + (TableName.NAMESPACE_DELIM)) + "table1")));
        tableDescOne.addFamily(fam1);
        HTableDescriptor tableDescTwo = new HTableDescriptor(TableName.valueOf(((nsp + (TableName.NAMESPACE_DELIM)) + "table2")));
        tableDescTwo.addFamily(fam1);
        HTableDescriptor tableDescThree = new HTableDescriptor(TableName.valueOf(((nsp + (TableName.NAMESPACE_DELIM)) + "table3")));
        tableDescThree.addFamily(fam1);
        TestNamespaceAuditor.ADMIN.createTable(tableDescOne);
        boolean constraintViolated = false;
        try {
            TestNamespaceAuditor.ADMIN.createTable(tableDescTwo, Bytes.toBytes("AAA"), Bytes.toBytes("ZZZ"), 5);
        } catch (Exception exp) {
            Assert.assertTrue((exp instanceof IOException));
            constraintViolated = true;
        } finally {
            Assert.assertTrue(("Constraint not violated for table " + (tableDescTwo.getTableName())), constraintViolated);
        }
        TestNamespaceAuditor.ADMIN.createTable(tableDescTwo, Bytes.toBytes("AAA"), Bytes.toBytes("ZZZ"), 4);
        NamespaceTableAndRegionInfo nspState = getQuotaManager().getState(nsp);
        Assert.assertNotNull(nspState);
        Assert.assertTrue(((nspState.getTables().size()) == 2));
        Assert.assertTrue(((nspState.getRegionCount()) == 5));
        constraintViolated = false;
        try {
            TestNamespaceAuditor.ADMIN.createTable(tableDescThree);
        } catch (Exception exp) {
            Assert.assertTrue((exp instanceof IOException));
            constraintViolated = true;
        } finally {
            Assert.assertTrue(("Constraint not violated for table " + (tableDescThree.getTableName())), constraintViolated);
        }
    }

    @Test
    public void testValidQuotas() throws Exception {
        boolean exceptionCaught = false;
        FileSystem fs = TestNamespaceAuditor.UTIL.getHBaseCluster().getMaster().getMasterFileSystem().getFileSystem();
        Path rootDir = TestNamespaceAuditor.UTIL.getHBaseCluster().getMaster().getMasterFileSystem().getRootDir();
        NamespaceDescriptor nspDesc = NamespaceDescriptor.create(((prefix) + "vq1")).addConfiguration(KEY_MAX_REGIONS, "hihdufh").addConfiguration(KEY_MAX_TABLES, "2").build();
        try {
            TestNamespaceAuditor.ADMIN.createNamespace(nspDesc);
        } catch (Exception exp) {
            TestNamespaceAuditor.LOG.warn(exp.toString(), exp);
            exceptionCaught = true;
        } finally {
            Assert.assertTrue(exceptionCaught);
            Assert.assertFalse(fs.exists(FSUtils.getNamespaceDir(rootDir, nspDesc.getName())));
        }
        nspDesc = NamespaceDescriptor.create(((prefix) + "vq2")).addConfiguration(KEY_MAX_REGIONS, "-456").addConfiguration(KEY_MAX_TABLES, "2").build();
        try {
            TestNamespaceAuditor.ADMIN.createNamespace(nspDesc);
        } catch (Exception exp) {
            TestNamespaceAuditor.LOG.warn(exp.toString(), exp);
            exceptionCaught = true;
        } finally {
            Assert.assertTrue(exceptionCaught);
            Assert.assertFalse(fs.exists(FSUtils.getNamespaceDir(rootDir, nspDesc.getName())));
        }
        nspDesc = NamespaceDescriptor.create(((prefix) + "vq3")).addConfiguration(KEY_MAX_REGIONS, "10").addConfiguration(KEY_MAX_TABLES, "sciigd").build();
        try {
            TestNamespaceAuditor.ADMIN.createNamespace(nspDesc);
        } catch (Exception exp) {
            TestNamespaceAuditor.LOG.warn(exp.toString(), exp);
            exceptionCaught = true;
        } finally {
            Assert.assertTrue(exceptionCaught);
            Assert.assertFalse(fs.exists(FSUtils.getNamespaceDir(rootDir, nspDesc.getName())));
        }
        nspDesc = NamespaceDescriptor.create(((prefix) + "vq4")).addConfiguration(KEY_MAX_REGIONS, "10").addConfiguration(KEY_MAX_TABLES, "-1500").build();
        try {
            TestNamespaceAuditor.ADMIN.createNamespace(nspDesc);
        } catch (Exception exp) {
            TestNamespaceAuditor.LOG.warn(exp.toString(), exp);
            exceptionCaught = true;
        } finally {
            Assert.assertTrue(exceptionCaught);
            Assert.assertFalse(fs.exists(FSUtils.getNamespaceDir(rootDir, nspDesc.getName())));
        }
    }

    @Test
    public void testDeleteTable() throws Exception {
        String namespace = (prefix) + "_dummy";
        NamespaceDescriptor nspDesc = NamespaceDescriptor.create(namespace).addConfiguration(KEY_MAX_REGIONS, "100").addConfiguration(KEY_MAX_TABLES, "3").build();
        TestNamespaceAuditor.ADMIN.createNamespace(nspDesc);
        Assert.assertNotNull("Namespace descriptor found null.", TestNamespaceAuditor.ADMIN.getNamespaceDescriptor(namespace));
        NamespaceTableAndRegionInfo stateInfo = getNamespaceState(nspDesc.getName());
        Assert.assertNotNull(("Namespace state found null for " + namespace), stateInfo);
        HColumnDescriptor fam1 = new HColumnDescriptor("fam1");
        HTableDescriptor tableDescOne = new HTableDescriptor(TableName.valueOf(((namespace + (TableName.NAMESPACE_DELIM)) + "table1")));
        tableDescOne.addFamily(fam1);
        HTableDescriptor tableDescTwo = new HTableDescriptor(TableName.valueOf(((namespace + (TableName.NAMESPACE_DELIM)) + "table2")));
        tableDescTwo.addFamily(fam1);
        TestNamespaceAuditor.ADMIN.createTable(tableDescOne);
        TestNamespaceAuditor.ADMIN.createTable(tableDescTwo, Bytes.toBytes("AAA"), Bytes.toBytes("ZZZ"), 5);
        stateInfo = getNamespaceState(nspDesc.getName());
        Assert.assertNotNull("Namespace state found to be null.", stateInfo);
        Assert.assertEquals(2, stateInfo.getTables().size());
        Assert.assertEquals(5, stateInfo.getRegionCountOfTable(tableDescTwo.getTableName()));
        Assert.assertEquals(6, stateInfo.getRegionCount());
        TestNamespaceAuditor.ADMIN.disableTable(tableDescOne.getTableName());
        deleteTable(tableDescOne.getTableName());
        stateInfo = getNamespaceState(nspDesc.getName());
        Assert.assertNotNull("Namespace state found to be null.", stateInfo);
        Assert.assertEquals(5, stateInfo.getRegionCount());
        Assert.assertEquals(1, stateInfo.getTables().size());
        TestNamespaceAuditor.ADMIN.disableTable(tableDescTwo.getTableName());
        deleteTable(tableDescTwo.getTableName());
        TestNamespaceAuditor.ADMIN.deleteNamespace(namespace);
        stateInfo = getNamespaceState(namespace);
        Assert.assertNull("Namespace state not found to be null.", stateInfo);
    }

    public static class CPRegionServerObserver implements RegionServerCoprocessor , RegionServerObserver {
        private volatile boolean shouldFailMerge = false;

        public void failMerge(boolean fail) {
            shouldFailMerge = fail;
        }

        private boolean triggered = false;

        public synchronized void waitUtilTriggered() throws InterruptedException {
            while (!(triggered)) {
                wait();
            } 
        }

        @Override
        public Optional<RegionServerObserver> getRegionServerObserver() {
            return Optional.of(this);
        }
    }

    public static class CPMasterObserver implements MasterCoprocessor , MasterObserver {
        private volatile boolean shouldFailMerge = false;

        public void failMerge(boolean fail) {
            shouldFailMerge = fail;
        }

        @Override
        public Optional<MasterObserver> getMasterObserver() {
            return Optional.of(this);
        }

        @Override
        public synchronized void preMergeRegionsAction(final ObserverContext<MasterCoprocessorEnvironment> ctx, final RegionInfo[] regionsToMerge) throws IOException {
            notifyAll();
            if (shouldFailMerge) {
                throw new IOException("fail merge");
            }
        }
    }

    @Test
    public void testRegionMerge() throws Exception {
        String nsp1 = (prefix) + "_regiontest";
        final int initialRegions = 3;
        NamespaceDescriptor nspDesc = NamespaceDescriptor.create(nsp1).addConfiguration(KEY_MAX_REGIONS, ("" + initialRegions)).addConfiguration(KEY_MAX_TABLES, "2").build();
        TestNamespaceAuditor.ADMIN.createNamespace(nspDesc);
        final TableName tableTwo = TableName.valueOf(((nsp1 + (TableName.NAMESPACE_DELIM)) + "table2"));
        byte[] columnFamily = Bytes.toBytes("info");
        HTableDescriptor tableDescOne = new HTableDescriptor(tableTwo);
        tableDescOne.addFamily(new HColumnDescriptor(columnFamily));
        TestNamespaceAuditor.ADMIN.createTable(tableDescOne, Bytes.toBytes("0"), Bytes.toBytes("9"), initialRegions);
        Connection connection = ConnectionFactory.createConnection(TestNamespaceAuditor.UTIL.getConfiguration());
        try (Table table = connection.getTable(tableTwo)) {
            TestNamespaceAuditor.UTIL.loadNumericRows(table, Bytes.toBytes("info"), 1000, 1999);
        }
        TestNamespaceAuditor.ADMIN.flush(tableTwo);
        List<RegionInfo> hris = TestNamespaceAuditor.ADMIN.getRegions(tableTwo);
        Assert.assertEquals(initialRegions, hris.size());
        Collections.sort(hris, COMPARATOR);
        Future<?> f = TestNamespaceAuditor.ADMIN.mergeRegionsAsync(hris.get(0).getEncodedNameAsBytes(), hris.get(1).getEncodedNameAsBytes(), false);
        f.get(10, TimeUnit.SECONDS);
        hris = TestNamespaceAuditor.ADMIN.getRegions(tableTwo);
        Assert.assertEquals((initialRegions - 1), hris.size());
        Collections.sort(hris, COMPARATOR);
        byte[] splitKey = Bytes.toBytes("3");
        HRegion regionToSplit = TestNamespaceAuditor.UTIL.getMiniHBaseCluster().getRegions(tableTwo).stream().filter(( r) -> r.getRegionInfo().containsRow(splitKey)).findFirst().get();
        regionToSplit.compact(true);
        // Waiting for compaction to finish
        TestNamespaceAuditor.UTIL.waitFor(30000, new Waiter.Predicate<Exception>() {
            @Override
            public boolean evaluate() throws Exception {
                return CompactionState.NONE == (TestNamespaceAuditor.ADMIN.getCompactionStateForRegion(regionToSplit.getRegionInfo().getRegionName()));
            }
        });
        // Cleaning compacted references for split to proceed
        regionToSplit.getStores().stream().forEach(( s) -> {
            try {
                s.closeAndArchiveCompactedFiles();
            } catch ( e1) {
                LOG.error("Error whiling cleaning compacted file");
            }
        });
        // the above compact may quit immediately if there is a compaction ongoing, so here we need to
        // wait a while to let the ongoing compaction finish.
        TestNamespaceAuditor.UTIL.waitFor(10000, regionToSplit::isSplittable);
        TestNamespaceAuditor.ADMIN.splitRegionAsync(regionToSplit.getRegionInfo().getRegionName(), splitKey).get(10, TimeUnit.SECONDS);
        hris = TestNamespaceAuditor.ADMIN.getRegions(tableTwo);
        Assert.assertEquals(initialRegions, hris.size());
        Collections.sort(hris, COMPARATOR);
        // Fail region merge through Coprocessor hook
        MiniHBaseCluster cluster = TestNamespaceAuditor.UTIL.getHBaseCluster();
        MasterCoprocessorHost cpHost = cluster.getMaster().getMasterCoprocessorHost();
        Coprocessor coprocessor = cpHost.findCoprocessor(TestNamespaceAuditor.CPMasterObserver.class);
        TestNamespaceAuditor.CPMasterObserver masterObserver = ((TestNamespaceAuditor.CPMasterObserver) (coprocessor));
        masterObserver.failMerge(true);
        f = TestNamespaceAuditor.ADMIN.mergeRegionsAsync(hris.get(1).getEncodedNameAsBytes(), hris.get(2).getEncodedNameAsBytes(), false);
        try {
            f.get(10, TimeUnit.SECONDS);
            Assert.fail("Merge was supposed to fail!");
        } catch (ExecutionException ee) {
            // Expected.
        }
        hris = TestNamespaceAuditor.ADMIN.getRegions(tableTwo);
        Assert.assertEquals(initialRegions, hris.size());
        Collections.sort(hris, COMPARATOR);
        // verify that we cannot split
        try {
            TestNamespaceAuditor.ADMIN.split(tableTwo, Bytes.toBytes("6"));
            Assert.fail();
        } catch (DoNotRetryRegionException e) {
            // Expected
        }
        Thread.sleep(2000);
        Assert.assertEquals(initialRegions, TestNamespaceAuditor.ADMIN.getRegions(tableTwo).size());
    }

    /* Create a table and make sure that the table creation fails after adding this table entry into
    namespace quota cache. Now correct the failure and recreate the table with same name.
    HBASE-13394
     */
    @Test
    public void testRecreateTableWithSameNameAfterFirstTimeFailure() throws Exception {
        String nsp1 = (prefix) + "_testRecreateTable";
        NamespaceDescriptor nspDesc = NamespaceDescriptor.create(nsp1).addConfiguration(KEY_MAX_REGIONS, "20").addConfiguration(KEY_MAX_TABLES, "1").build();
        TestNamespaceAuditor.ADMIN.createNamespace(nspDesc);
        final TableName tableOne = TableName.valueOf(((nsp1 + (TableName.NAMESPACE_DELIM)) + "table1"));
        byte[] columnFamily = Bytes.toBytes("info");
        HTableDescriptor tableDescOne = new HTableDescriptor(tableOne);
        tableDescOne.addFamily(new HColumnDescriptor(columnFamily));
        TestNamespaceAuditor.MasterSyncObserver.throwExceptionInPreCreateTableAction = true;
        try {
            try {
                TestNamespaceAuditor.ADMIN.createTable(tableDescOne);
                Assert.fail((("Table " + (tableOne.toString())) + "creation should fail."));
            } catch (Exception exp) {
                TestNamespaceAuditor.LOG.error(exp.toString(), exp);
            }
            Assert.assertFalse(TestNamespaceAuditor.ADMIN.tableExists(tableOne));
            NamespaceTableAndRegionInfo nstate = getNamespaceState(nsp1);
            Assert.assertEquals(("First table creation failed in namespace so number of tables in namespace " + "should be 0."), 0, nstate.getTables().size());
            TestNamespaceAuditor.MasterSyncObserver.throwExceptionInPreCreateTableAction = false;
            try {
                TestNamespaceAuditor.ADMIN.createTable(tableDescOne);
            } catch (Exception e) {
                Assert.fail((("Table " + (tableOne.toString())) + "creation should succeed."));
                TestNamespaceAuditor.LOG.error(e.toString(), e);
            }
            Assert.assertTrue(TestNamespaceAuditor.ADMIN.tableExists(tableOne));
            nstate = getNamespaceState(nsp1);
            Assert.assertEquals(("First table was created successfully so table size in namespace should " + "be one now."), 1, nstate.getTables().size());
        } finally {
            TestNamespaceAuditor.MasterSyncObserver.throwExceptionInPreCreateTableAction = false;
            if (TestNamespaceAuditor.ADMIN.tableExists(tableOne)) {
                TestNamespaceAuditor.ADMIN.disableTable(tableOne);
                deleteTable(tableOne);
            }
            TestNamespaceAuditor.ADMIN.deleteNamespace(nsp1);
        }
    }

    public static class CustomObserver implements RegionCoprocessor , RegionObserver {
        volatile CountDownLatch postCompact;

        @Override
        public void postCompact(ObserverContext<RegionCoprocessorEnvironment> e, Store store, StoreFile resultFile, CompactionLifeCycleTracker tracker, CompactionRequest request) throws IOException {
            postCompact.countDown();
        }

        @Override
        public void start(CoprocessorEnvironment e) throws IOException {
            postCompact = new CountDownLatch(1);
        }

        @Override
        public Optional<RegionObserver> getRegionObserver() {
            return Optional.of(this);
        }
    }

    @Test
    public void testStatePreserve() throws Exception {
        final String nsp1 = (prefix) + "_testStatePreserve";
        NamespaceDescriptor nspDesc = NamespaceDescriptor.create(nsp1).addConfiguration(KEY_MAX_REGIONS, "20").addConfiguration(KEY_MAX_TABLES, "10").build();
        TestNamespaceAuditor.ADMIN.createNamespace(nspDesc);
        TableName tableOne = TableName.valueOf(((nsp1 + (TableName.NAMESPACE_DELIM)) + "table1"));
        TableName tableTwo = TableName.valueOf(((nsp1 + (TableName.NAMESPACE_DELIM)) + "table2"));
        TableName tableThree = TableName.valueOf(((nsp1 + (TableName.NAMESPACE_DELIM)) + "table3"));
        HColumnDescriptor fam1 = new HColumnDescriptor("fam1");
        HTableDescriptor tableDescOne = new HTableDescriptor(tableOne);
        tableDescOne.addFamily(fam1);
        HTableDescriptor tableDescTwo = new HTableDescriptor(tableTwo);
        tableDescTwo.addFamily(fam1);
        HTableDescriptor tableDescThree = new HTableDescriptor(tableThree);
        tableDescThree.addFamily(fam1);
        TestNamespaceAuditor.ADMIN.createTable(tableDescOne, Bytes.toBytes("1"), Bytes.toBytes("1000"), 3);
        TestNamespaceAuditor.ADMIN.createTable(tableDescTwo, Bytes.toBytes("1"), Bytes.toBytes("1000"), 3);
        TestNamespaceAuditor.ADMIN.createTable(tableDescThree, Bytes.toBytes("1"), Bytes.toBytes("1000"), 4);
        TestNamespaceAuditor.ADMIN.disableTable(tableThree);
        deleteTable(tableThree);
        // wait for chore to complete
        TestNamespaceAuditor.UTIL.waitFor(1000, new Waiter.Predicate<Exception>() {
            @Override
            public boolean evaluate() throws Exception {
                return (getNamespaceState(nsp1).getTables().size()) == 2;
            }
        });
        NamespaceTableAndRegionInfo before = getNamespaceState(nsp1);
        restartMaster();
        NamespaceTableAndRegionInfo after = getNamespaceState(nsp1);
        Assert.assertEquals(((("Expected: " + (before.getTables())) + " Found: ") + (after.getTables())), before.getTables().size(), after.getTables().size());
    }

    public static class MasterSyncObserver implements MasterCoprocessor , MasterObserver {
        volatile CountDownLatch tableDeletionLatch;

        static boolean throwExceptionInPreCreateTableAction;

        @Override
        public Optional<MasterObserver> getMasterObserver() {
            return Optional.of(this);
        }

        @Override
        public void preDeleteTable(ObserverContext<MasterCoprocessorEnvironment> ctx, TableName tableName) throws IOException {
            tableDeletionLatch = new CountDownLatch(1);
        }

        @Override
        public void postCompletedDeleteTableAction(final ObserverContext<MasterCoprocessorEnvironment> ctx, final TableName tableName) throws IOException {
            tableDeletionLatch.countDown();
        }

        @Override
        public void preCreateTableAction(ObserverContext<MasterCoprocessorEnvironment> ctx, TableDescriptor desc, RegionInfo[] regions) throws IOException {
            if (TestNamespaceAuditor.MasterSyncObserver.throwExceptionInPreCreateTableAction) {
                throw new IOException("Throw exception as it is demanded.");
            }
        }
    }

    @Test(expected = QuotaExceededException.class)
    public void testExceedTableQuotaInNamespace() throws Exception {
        String nsp = (prefix) + "_testExceedTableQuotaInNamespace";
        NamespaceDescriptor nspDesc = NamespaceDescriptor.create(nsp).addConfiguration(KEY_MAX_TABLES, "1").build();
        TestNamespaceAuditor.ADMIN.createNamespace(nspDesc);
        Assert.assertNotNull("Namespace descriptor found null.", TestNamespaceAuditor.ADMIN.getNamespaceDescriptor(nsp));
        Assert.assertEquals(3, TestNamespaceAuditor.ADMIN.listNamespaceDescriptors().length);
        HColumnDescriptor fam1 = new HColumnDescriptor("fam1");
        HTableDescriptor tableDescOne = new HTableDescriptor(TableName.valueOf(((nsp + (TableName.NAMESPACE_DELIM)) + "table1")));
        tableDescOne.addFamily(fam1);
        HTableDescriptor tableDescTwo = new HTableDescriptor(TableName.valueOf(((nsp + (TableName.NAMESPACE_DELIM)) + "table2")));
        tableDescTwo.addFamily(fam1);
        TestNamespaceAuditor.ADMIN.createTable(tableDescOne);
        TestNamespaceAuditor.ADMIN.createTable(tableDescTwo, Bytes.toBytes("AAA"), Bytes.toBytes("ZZZ"), 4);
    }

    @Test(expected = QuotaExceededException.class)
    public void testCloneSnapshotQuotaExceed() throws Exception {
        String nsp = (prefix) + "_testTableQuotaExceedWithCloneSnapshot";
        NamespaceDescriptor nspDesc = NamespaceDescriptor.create(nsp).addConfiguration(KEY_MAX_TABLES, "1").build();
        TestNamespaceAuditor.ADMIN.createNamespace(nspDesc);
        Assert.assertNotNull("Namespace descriptor found null.", TestNamespaceAuditor.ADMIN.getNamespaceDescriptor(nsp));
        TableName tableName = TableName.valueOf(((nsp + (TableName.NAMESPACE_DELIM)) + "table1"));
        TableName cloneTableName = TableName.valueOf(((nsp + (TableName.NAMESPACE_DELIM)) + "table2"));
        HColumnDescriptor fam1 = new HColumnDescriptor("fam1");
        HTableDescriptor tableDescOne = new HTableDescriptor(tableName);
        tableDescOne.addFamily(fam1);
        TestNamespaceAuditor.ADMIN.createTable(tableDescOne);
        String snapshot = "snapshot_testTableQuotaExceedWithCloneSnapshot";
        TestNamespaceAuditor.ADMIN.snapshot(snapshot, tableName);
        TestNamespaceAuditor.ADMIN.cloneSnapshot(snapshot, cloneTableName);
        TestNamespaceAuditor.ADMIN.deleteSnapshot(snapshot);
    }

    @Test
    public void testCloneSnapshot() throws Exception {
        String nsp = (prefix) + "_testCloneSnapshot";
        NamespaceDescriptor nspDesc = NamespaceDescriptor.create(nsp).addConfiguration(KEY_MAX_TABLES, "2").addConfiguration(KEY_MAX_REGIONS, "20").build();
        TestNamespaceAuditor.ADMIN.createNamespace(nspDesc);
        Assert.assertNotNull("Namespace descriptor found null.", TestNamespaceAuditor.ADMIN.getNamespaceDescriptor(nsp));
        TableName tableName = TableName.valueOf(((nsp + (TableName.NAMESPACE_DELIM)) + "table1"));
        TableName cloneTableName = TableName.valueOf(((nsp + (TableName.NAMESPACE_DELIM)) + "table2"));
        HColumnDescriptor fam1 = new HColumnDescriptor("fam1");
        HTableDescriptor tableDescOne = new HTableDescriptor(tableName);
        tableDescOne.addFamily(fam1);
        TestNamespaceAuditor.ADMIN.createTable(tableDescOne, Bytes.toBytes("AAA"), Bytes.toBytes("ZZZ"), 4);
        String snapshot = "snapshot_testCloneSnapshot";
        TestNamespaceAuditor.ADMIN.snapshot(snapshot, tableName);
        TestNamespaceAuditor.ADMIN.cloneSnapshot(snapshot, cloneTableName);
        int tableLength;
        try (RegionLocator locator = TestNamespaceAuditor.ADMIN.getConnection().getRegionLocator(tableName)) {
            tableLength = locator.getStartKeys().length;
        }
        Assert.assertEquals(((tableName.getNameAsString()) + " should have four regions."), 4, tableLength);
        try (RegionLocator locator = TestNamespaceAuditor.ADMIN.getConnection().getRegionLocator(cloneTableName)) {
            tableLength = locator.getStartKeys().length;
        }
        Assert.assertEquals(((cloneTableName.getNameAsString()) + " should have four regions."), 4, tableLength);
        NamespaceTableAndRegionInfo nstate = getNamespaceState(nsp);
        Assert.assertEquals("Total tables count should be 2.", 2, nstate.getTables().size());
        Assert.assertEquals("Total regions count should be.", 8, nstate.getRegionCount());
        TestNamespaceAuditor.ADMIN.deleteSnapshot(snapshot);
    }

    @Test
    public void testRestoreSnapshot() throws Exception {
        String nsp = (prefix) + "_testRestoreSnapshot";
        NamespaceDescriptor nspDesc = NamespaceDescriptor.create(nsp).addConfiguration(KEY_MAX_REGIONS, "10").build();
        TestNamespaceAuditor.ADMIN.createNamespace(nspDesc);
        Assert.assertNotNull("Namespace descriptor found null.", TestNamespaceAuditor.ADMIN.getNamespaceDescriptor(nsp));
        TableName tableName1 = TableName.valueOf(((nsp + (TableName.NAMESPACE_DELIM)) + "table1"));
        HTableDescriptor tableDescOne = new HTableDescriptor(tableName1);
        HColumnDescriptor fam1 = new HColumnDescriptor("fam1");
        tableDescOne.addFamily(fam1);
        TestNamespaceAuditor.ADMIN.createTable(tableDescOne, Bytes.toBytes("AAA"), Bytes.toBytes("ZZZ"), 4);
        NamespaceTableAndRegionInfo nstate = getNamespaceState(nsp);
        Assert.assertEquals("Intial region count should be 4.", 4, nstate.getRegionCount());
        String snapshot = "snapshot_testRestoreSnapshot";
        TestNamespaceAuditor.ADMIN.snapshot(snapshot, tableName1);
        List<HRegionInfo> regions = TestNamespaceAuditor.ADMIN.getTableRegions(tableName1);
        Collections.sort(regions);
        TestNamespaceAuditor.ADMIN.split(tableName1, Bytes.toBytes("JJJ"));
        Thread.sleep(2000);
        Assert.assertEquals("Total regions count should be 5.", 5, nstate.getRegionCount());
        TestNamespaceAuditor.ADMIN.disableTable(tableName1);
        TestNamespaceAuditor.ADMIN.restoreSnapshot(snapshot);
        Assert.assertEquals("Total regions count should be 4 after restore.", 4, nstate.getRegionCount());
        TestNamespaceAuditor.ADMIN.enableTable(tableName1);
        TestNamespaceAuditor.ADMIN.deleteSnapshot(snapshot);
    }

    @Test
    public void testRestoreSnapshotQuotaExceed() throws Exception {
        String nsp = (prefix) + "_testRestoreSnapshotQuotaExceed";
        NamespaceDescriptor nspDesc = NamespaceDescriptor.create(nsp).addConfiguration(KEY_MAX_REGIONS, "10").build();
        TestNamespaceAuditor.ADMIN.createNamespace(nspDesc);
        NamespaceDescriptor ndesc = TestNamespaceAuditor.ADMIN.getNamespaceDescriptor(nsp);
        Assert.assertNotNull("Namespace descriptor found null.", ndesc);
        TableName tableName1 = TableName.valueOf(((nsp + (TableName.NAMESPACE_DELIM)) + "table1"));
        HTableDescriptor tableDescOne = new HTableDescriptor(tableName1);
        HColumnDescriptor fam1 = new HColumnDescriptor("fam1");
        tableDescOne.addFamily(fam1);
        TestNamespaceAuditor.ADMIN.createTable(tableDescOne, Bytes.toBytes("AAA"), Bytes.toBytes("ZZZ"), 4);
        NamespaceTableAndRegionInfo nstate = getNamespaceState(nsp);
        Assert.assertEquals("Intial region count should be 4.", 4, nstate.getRegionCount());
        String snapshot = "snapshot_testRestoreSnapshotQuotaExceed";
        // snapshot has 4 regions
        TestNamespaceAuditor.ADMIN.snapshot(snapshot, tableName1);
        // recreate table with 1 region and set max regions to 3 for namespace
        TestNamespaceAuditor.ADMIN.disableTable(tableName1);
        TestNamespaceAuditor.ADMIN.deleteTable(tableName1);
        TestNamespaceAuditor.ADMIN.createTable(tableDescOne);
        ndesc.setConfiguration(KEY_MAX_REGIONS, "3");
        TestNamespaceAuditor.ADMIN.modifyNamespace(ndesc);
        TestNamespaceAuditor.ADMIN.disableTable(tableName1);
        try {
            TestNamespaceAuditor.ADMIN.restoreSnapshot(snapshot);
            Assert.fail(("Region quota is exceeded so QuotaExceededException should be thrown but HBaseAdmin" + " wraps IOException into RestoreSnapshotException"));
        } catch (RestoreSnapshotException ignore) {
            Assert.assertTrue(((ignore.getCause()) instanceof QuotaExceededException));
        }
        Assert.assertEquals(1, getNamespaceState(nsp).getRegionCount());
        TestNamespaceAuditor.ADMIN.enableTable(tableName1);
        TestNamespaceAuditor.ADMIN.deleteSnapshot(snapshot);
    }
}

