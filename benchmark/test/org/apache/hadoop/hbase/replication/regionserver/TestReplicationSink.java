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
package org.apache.hadoop.hbase.replication.regionserver;


import KeyValue.Type;
import KeyValue.Type.DeleteColumn;
import KeyValue.Type.DeleteFamily;
import KeyValue.Type.Put;
import Path.SEPARATOR;
import WALEntry.Builder;
import WALProtos.BulkLoadDescriptor;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.FileUtil;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hbase.Cell;
import org.apache.hadoop.hbase.CellUtil;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.HBaseTestingUtility;
import org.apache.hadoop.hbase.HRegionInfo;
import org.apache.hadoop.hbase.Stoppable;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.TableNotFoundException;
import org.apache.hadoop.hbase.client.Admin;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.ConnectionFactory;
import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.RegionLocator;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.ResultScanner;
import org.apache.hadoop.hbase.client.RetriesExhaustedWithDetailsException;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.client.Table;
import org.apache.hadoop.hbase.shaded.protobuf.ProtobufUtil;
import org.apache.hadoop.hbase.shaded.protobuf.generated.AdminProtos.WALEntry;
import org.apache.hadoop.hbase.shaded.protobuf.generated.WALProtos;
import org.apache.hadoop.hbase.testclassification.MediumTests;
import org.apache.hadoop.hbase.testclassification.ReplicationTests;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.hbase.util.HFileTestUtil;
import org.apache.hadoop.hbase.wal.WALEdit;
import org.apache.hbase.thirdparty.com.google.protobuf.UnsafeByteOperations;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Category({ ReplicationTests.class, MediumTests.class })
public class TestReplicationSink {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestReplicationSink.class);

    private static final Logger LOG = LoggerFactory.getLogger(TestReplicationSink.class);

    private static final int BATCH_SIZE = 10;

    protected static final HBaseTestingUtility TEST_UTIL = new HBaseTestingUtility();

    protected static ReplicationSink SINK;

    protected static final TableName TABLE_NAME1 = TableName.valueOf("table1");

    protected static final TableName TABLE_NAME2 = TableName.valueOf("table2");

    protected static final byte[] FAM_NAME1 = Bytes.toBytes("info1");

    protected static final byte[] FAM_NAME2 = Bytes.toBytes("info2");

    protected static Table table1;

    protected static Stoppable STOPPABLE = new Stoppable() {
        final AtomicBoolean stop = new AtomicBoolean(false);

        @Override
        public boolean isStopped() {
            return this.stop.get();
        }

        @Override
        public void stop(String why) {
            TestReplicationSink.LOG.info(("STOPPING BECAUSE: " + why));
            this.stop.set(true);
        }
    };

    protected static Table table2;

    protected static String baseNamespaceDir;

    protected static String hfileArchiveDir;

    protected static String replicationClusterId;

    /**
     * Insert a whole batch of entries
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testBatchSink() throws Exception {
        List<WALEntry> entries = new ArrayList<>(TestReplicationSink.BATCH_SIZE);
        List<Cell> cells = new ArrayList<>();
        for (int i = 0; i < (TestReplicationSink.BATCH_SIZE); i++) {
            entries.add(createEntry(TestReplicationSink.TABLE_NAME1, i, Put, cells));
        }
        TestReplicationSink.SINK.replicateEntries(entries, CellUtil.createCellScanner(cells.iterator()), TestReplicationSink.replicationClusterId, TestReplicationSink.baseNamespaceDir, TestReplicationSink.hfileArchiveDir);
        Scan scan = new Scan();
        ResultScanner scanRes = TestReplicationSink.table1.getScanner(scan);
        Assert.assertEquals(TestReplicationSink.BATCH_SIZE, scanRes.next(TestReplicationSink.BATCH_SIZE).length);
    }

    /**
     * Insert a mix of puts and deletes
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testMixedPutDelete() throws Exception {
        List<WALEntry> entries = new ArrayList<>(((TestReplicationSink.BATCH_SIZE) / 2));
        List<Cell> cells = new ArrayList<>();
        for (int i = 0; i < ((TestReplicationSink.BATCH_SIZE) / 2); i++) {
            entries.add(createEntry(TestReplicationSink.TABLE_NAME1, i, Put, cells));
        }
        TestReplicationSink.SINK.replicateEntries(entries, CellUtil.createCellScanner(cells), TestReplicationSink.replicationClusterId, TestReplicationSink.baseNamespaceDir, TestReplicationSink.hfileArchiveDir);
        entries = new ArrayList(TestReplicationSink.BATCH_SIZE);
        cells = new ArrayList();
        for (int i = 0; i < (TestReplicationSink.BATCH_SIZE); i++) {
            entries.add(createEntry(TestReplicationSink.TABLE_NAME1, i, ((i % 2) != 0 ? Type.Put : Type.DeleteColumn), cells));
        }
        TestReplicationSink.SINK.replicateEntries(entries, CellUtil.createCellScanner(cells.iterator()), TestReplicationSink.replicationClusterId, TestReplicationSink.baseNamespaceDir, TestReplicationSink.hfileArchiveDir);
        Scan scan = new Scan();
        ResultScanner scanRes = TestReplicationSink.table1.getScanner(scan);
        Assert.assertEquals(((TestReplicationSink.BATCH_SIZE) / 2), scanRes.next(TestReplicationSink.BATCH_SIZE).length);
    }

    /**
     * Insert to 2 different tables
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testMixedPutTables() throws Exception {
        List<WALEntry> entries = new ArrayList<>(((TestReplicationSink.BATCH_SIZE) / 2));
        List<Cell> cells = new ArrayList<>();
        for (int i = 0; i < (TestReplicationSink.BATCH_SIZE); i++) {
            entries.add(createEntry(((i % 2) == 0 ? TestReplicationSink.TABLE_NAME2 : TestReplicationSink.TABLE_NAME1), i, Put, cells));
        }
        TestReplicationSink.SINK.replicateEntries(entries, CellUtil.createCellScanner(cells.iterator()), TestReplicationSink.replicationClusterId, TestReplicationSink.baseNamespaceDir, TestReplicationSink.hfileArchiveDir);
        Scan scan = new Scan();
        ResultScanner scanRes = TestReplicationSink.table2.getScanner(scan);
        for (Result res : scanRes) {
            Assert.assertTrue((((Bytes.toInt(res.getRow())) % 2) == 0));
        }
    }

    /**
     * Insert then do different types of deletes
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testMixedDeletes() throws Exception {
        List<WALEntry> entries = new ArrayList<>(3);
        List<Cell> cells = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            entries.add(createEntry(TestReplicationSink.TABLE_NAME1, i, Put, cells));
        }
        TestReplicationSink.SINK.replicateEntries(entries, CellUtil.createCellScanner(cells.iterator()), TestReplicationSink.replicationClusterId, TestReplicationSink.baseNamespaceDir, TestReplicationSink.hfileArchiveDir);
        entries = new ArrayList(3);
        cells = new ArrayList();
        entries.add(createEntry(TestReplicationSink.TABLE_NAME1, 0, DeleteColumn, cells));
        entries.add(createEntry(TestReplicationSink.TABLE_NAME1, 1, DeleteFamily, cells));
        entries.add(createEntry(TestReplicationSink.TABLE_NAME1, 2, DeleteColumn, cells));
        TestReplicationSink.SINK.replicateEntries(entries, CellUtil.createCellScanner(cells.iterator()), TestReplicationSink.replicationClusterId, TestReplicationSink.baseNamespaceDir, TestReplicationSink.hfileArchiveDir);
        Scan scan = new Scan();
        ResultScanner scanRes = TestReplicationSink.table1.getScanner(scan);
        Assert.assertEquals(0, scanRes.next(3).length);
    }

    /**
     * Puts are buffered, but this tests when a delete (not-buffered) is applied
     * before the actual Put that creates it.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testApplyDeleteBeforePut() throws Exception {
        List<WALEntry> entries = new ArrayList<>(5);
        List<Cell> cells = new ArrayList<>();
        for (int i = 0; i < 2; i++) {
            entries.add(createEntry(TestReplicationSink.TABLE_NAME1, i, Put, cells));
        }
        entries.add(createEntry(TestReplicationSink.TABLE_NAME1, 1, DeleteFamily, cells));
        for (int i = 3; i < 5; i++) {
            entries.add(createEntry(TestReplicationSink.TABLE_NAME1, i, Put, cells));
        }
        TestReplicationSink.SINK.replicateEntries(entries, CellUtil.createCellScanner(cells.iterator()), TestReplicationSink.replicationClusterId, TestReplicationSink.baseNamespaceDir, TestReplicationSink.hfileArchiveDir);
        Get get = new Get(Bytes.toBytes(1));
        Result res = TestReplicationSink.table1.get(get);
        Assert.assertEquals(0, res.size());
    }

    @Test
    public void testRethrowRetriesExhaustedWithDetailsException() throws Exception {
        TableName notExistTable = TableName.valueOf("notExistTable");
        List<WALEntry> entries = new ArrayList<>();
        List<Cell> cells = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            entries.add(createEntry(notExistTable, i, Put, cells));
        }
        try {
            TestReplicationSink.SINK.replicateEntries(entries, CellUtil.createCellScanner(cells.iterator()), TestReplicationSink.replicationClusterId, TestReplicationSink.baseNamespaceDir, TestReplicationSink.hfileArchiveDir);
            Assert.fail("Should re-throw TableNotFoundException.");
        } catch (TableNotFoundException e) {
        }
        entries.clear();
        cells.clear();
        for (int i = 0; i < 10; i++) {
            entries.add(createEntry(TestReplicationSink.TABLE_NAME1, i, Put, cells));
        }
        try (Connection conn = ConnectionFactory.createConnection(TestReplicationSink.TEST_UTIL.getConfiguration())) {
            try (Admin admin = conn.getAdmin()) {
                admin.disableTable(TestReplicationSink.TABLE_NAME1);
                try {
                    TestReplicationSink.SINK.replicateEntries(entries, CellUtil.createCellScanner(cells.iterator()), TestReplicationSink.replicationClusterId, TestReplicationSink.baseNamespaceDir, TestReplicationSink.hfileArchiveDir);
                    Assert.fail("Should re-throw RetriesExhaustedWithDetailsException.");
                } catch (RetriesExhaustedWithDetailsException e) {
                } finally {
                    admin.enableTable(TestReplicationSink.TABLE_NAME1);
                }
            }
        }
    }

    /**
     * Test replicateEntries with a bulk load entry for 25 HFiles
     */
    @Test
    public void testReplicateEntriesForHFiles() throws Exception {
        Path dir = TestReplicationSink.TEST_UTIL.getDataTestDirOnTestFS("testReplicateEntries");
        Path familyDir = new Path(dir, Bytes.toString(TestReplicationSink.FAM_NAME1));
        int numRows = 10;
        List<Path> p = new ArrayList<>(1);
        final String hfilePrefix = "hfile-";
        // 1. Generate 25 hfile ranges
        Random rng = new SecureRandom();
        Set<Integer> numbers = new HashSet<>();
        while ((numbers.size()) < 50) {
            numbers.add(rng.nextInt(1000));
        } 
        List<Integer> numberList = new ArrayList<>(numbers);
        Collections.sort(numberList);
        Map<String, Long> storeFilesSize = new HashMap<>(1);
        // 2. Create 25 hfiles
        Configuration conf = TestReplicationSink.TEST_UTIL.getConfiguration();
        FileSystem fs = dir.getFileSystem(conf);
        Iterator<Integer> numbersItr = numberList.iterator();
        for (int i = 0; i < 25; i++) {
            Path hfilePath = new Path(familyDir, (hfilePrefix + i));
            HFileTestUtil.createHFile(conf, fs, hfilePath, TestReplicationSink.FAM_NAME1, TestReplicationSink.FAM_NAME1, Bytes.toBytes(numbersItr.next()), Bytes.toBytes(numbersItr.next()), numRows);
            p.add(hfilePath);
            storeFilesSize.put(hfilePath.getName(), fs.getFileStatus(hfilePath).getLen());
        }
        // 3. Create a BulkLoadDescriptor and a WALEdit
        Map<byte[], List<Path>> storeFiles = new HashMap<>(1);
        storeFiles.put(TestReplicationSink.FAM_NAME1, p);
        org.apache.hadoop.hbase.wal.WALEdit edit = null;
        WALProtos.BulkLoadDescriptor loadDescriptor = null;
        try (Connection c = ConnectionFactory.createConnection(conf);RegionLocator l = c.getRegionLocator(TestReplicationSink.TABLE_NAME1)) {
            HRegionInfo regionInfo = l.getAllRegionLocations().get(0).getRegionInfo();
            loadDescriptor = ProtobufUtil.toBulkLoadDescriptor(TestReplicationSink.TABLE_NAME1, UnsafeByteOperations.unsafeWrap(regionInfo.getEncodedNameAsBytes()), storeFiles, storeFilesSize, 1);
            edit = org.apache.hadoop.hbase.wal.WALEdit.createBulkLoadEvent(regionInfo, loadDescriptor);
        }
        List<WALEntry> entries = new ArrayList<>(1);
        // 4. Create a WALEntryBuilder
        WALEntry.Builder builder = TestReplicationSink.createWALEntryBuilder(TestReplicationSink.TABLE_NAME1);
        // 5. Copy the hfile to the path as it is in reality
        for (int i = 0; i < 25; i++) {
            String pathToHfileFromNS = new StringBuilder(100).append(TestReplicationSink.TABLE_NAME1.getNamespaceAsString()).append(SEPARATOR).append(Bytes.toString(TestReplicationSink.TABLE_NAME1.getName())).append(SEPARATOR).append(Bytes.toString(loadDescriptor.getEncodedRegionName().toByteArray())).append(SEPARATOR).append(Bytes.toString(TestReplicationSink.FAM_NAME1)).append(SEPARATOR).append((hfilePrefix + i)).toString();
            String dst = ((TestReplicationSink.baseNamespaceDir) + (Path.SEPARATOR)) + pathToHfileFromNS;
            Path dstPath = new Path(dst);
            FileUtil.copy(fs, p.get(0), fs, dstPath, false, conf);
        }
        entries.add(builder.build());
        try (ResultScanner scanner = TestReplicationSink.table1.getScanner(new Scan())) {
            // 6. Assert no existing data in table
            Assert.assertEquals(0, scanner.next(numRows).length);
        }
        // 7. Replicate the bulk loaded entry
        TestReplicationSink.SINK.replicateEntries(entries, CellUtil.createCellScanner(edit.getCells().iterator()), TestReplicationSink.replicationClusterId, TestReplicationSink.baseNamespaceDir, TestReplicationSink.hfileArchiveDir);
        try (ResultScanner scanner = TestReplicationSink.table1.getScanner(new Scan())) {
            // 8. Assert data is replicated
            Assert.assertEquals(numRows, scanner.next(numRows).length);
        }
        // Clean up the created hfiles or it will mess up subsequent tests
    }
}

