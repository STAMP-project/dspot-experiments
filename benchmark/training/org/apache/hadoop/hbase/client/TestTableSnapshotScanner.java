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
package org.apache.hadoop.hbase.client;


import java.util.Arrays;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.HBaseTestingUtility;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.snapshot.RestoreSnapshotHelper;
import org.apache.hadoop.hbase.snapshot.SnapshotTestingUtils;
import org.apache.hadoop.hbase.testclassification.ClientTests;
import org.apache.hadoop.hbase.testclassification.LargeTests;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.hbase.util.FSUtils;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Category({ LargeTests.class, ClientTests.class })
public class TestTableSnapshotScanner {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestTableSnapshotScanner.class);

    private static final Logger LOG = LoggerFactory.getLogger(TestTableSnapshotScanner.class);

    private final HBaseTestingUtility UTIL = new HBaseTestingUtility();

    private static final int NUM_REGION_SERVERS = 2;

    private static final byte[][] FAMILIES = new byte[][]{ Bytes.toBytes("f1"), Bytes.toBytes("f2") };

    public static byte[] bbb = Bytes.toBytes("bbb");

    public static byte[] yyy = Bytes.toBytes("yyy");

    private FileSystem fs;

    private Path rootDir;

    @Test
    public void testNoDuplicateResultsWhenSplitting() throws Exception {
        setupCluster();
        TableName tableName = TableName.valueOf("testNoDuplicateResultsWhenSplitting");
        String snapshotName = "testSnapshotBug";
        try {
            if (UTIL.getAdmin().tableExists(tableName)) {
                UTIL.deleteTable(tableName);
            }
            UTIL.createTable(tableName, TestTableSnapshotScanner.FAMILIES);
            Admin admin = UTIL.getAdmin();
            // put some stuff in the table
            Table table = UTIL.getConnection().getTable(tableName);
            UTIL.loadTable(table, TestTableSnapshotScanner.FAMILIES);
            // split to 2 regions
            admin.split(tableName, Bytes.toBytes("eee"));
            TestTableSnapshotScanner.blockUntilSplitFinished(UTIL, tableName, 2);
            Path rootDir = FSUtils.getRootDir(UTIL.getConfiguration());
            FileSystem fs = rootDir.getFileSystem(UTIL.getConfiguration());
            SnapshotTestingUtils.createSnapshotAndValidate(admin, tableName, Arrays.asList(TestTableSnapshotScanner.FAMILIES), null, snapshotName, rootDir, fs, true);
            // load different values
            byte[] value = Bytes.toBytes("after_snapshot_value");
            UTIL.loadTable(table, TestTableSnapshotScanner.FAMILIES, value);
            // cause flush to create new files in the region
            admin.flush(tableName);
            table.close();
            Path restoreDir = UTIL.getDataTestDirOnTestFS(snapshotName);
            Scan scan = new Scan().withStartRow(TestTableSnapshotScanner.bbb).withStopRow(TestTableSnapshotScanner.yyy);// limit the scan

            TableSnapshotScanner scanner = new TableSnapshotScanner(UTIL.getConfiguration(), restoreDir, snapshotName, scan);
            verifyScanner(scanner, TestTableSnapshotScanner.bbb, TestTableSnapshotScanner.yyy);
            scanner.close();
        } finally {
            UTIL.getAdmin().deleteSnapshot(snapshotName);
            UTIL.deleteTable(tableName);
            tearDownCluster();
        }
    }

    @Test
    public void testWithSingleRegion() throws Exception {
        testScanner(UTIL, "testWithSingleRegion", 1, false);
    }

    @Test
    public void testWithMultiRegion() throws Exception {
        testScanner(UTIL, "testWithMultiRegion", 10, false);
    }

    @Test
    public void testWithOfflineHBaseMultiRegion() throws Exception {
        testScanner(UTIL, "testWithMultiRegion", 20, true);
    }

    @Test
    public void testScannerWithRestoreScanner() throws Exception {
        setupCluster();
        TableName tableName = TableName.valueOf("testScanner");
        String snapshotName = "testScannerWithRestoreScanner";
        try {
            TestTableSnapshotScanner.createTableAndSnapshot(UTIL, tableName, snapshotName, 50);
            Path restoreDir = UTIL.getDataTestDirOnTestFS(snapshotName);
            Scan scan = new Scan(TestTableSnapshotScanner.bbb, TestTableSnapshotScanner.yyy);// limit the scan

            Configuration conf = UTIL.getConfiguration();
            Path rootDir = FSUtils.getRootDir(conf);
            TableSnapshotScanner scanner0 = new TableSnapshotScanner(conf, restoreDir, snapshotName, scan);
            verifyScanner(scanner0, TestTableSnapshotScanner.bbb, TestTableSnapshotScanner.yyy);
            scanner0.close();
            // restore snapshot.
            RestoreSnapshotHelper.copySnapshotForScanner(conf, fs, rootDir, restoreDir, snapshotName);
            // scan the snapshot without restoring snapshot
            TableSnapshotScanner scanner = new TableSnapshotScanner(conf, rootDir, restoreDir, snapshotName, scan, true);
            verifyScanner(scanner, TestTableSnapshotScanner.bbb, TestTableSnapshotScanner.yyy);
            scanner.close();
            // check whether the snapshot has been deleted by the close of scanner.
            scanner = new TableSnapshotScanner(conf, rootDir, restoreDir, snapshotName, scan, true);
            verifyScanner(scanner, TestTableSnapshotScanner.bbb, TestTableSnapshotScanner.yyy);
            scanner.close();
            // restore snapshot again.
            RestoreSnapshotHelper.copySnapshotForScanner(conf, fs, rootDir, restoreDir, snapshotName);
            // check whether the snapshot has been deleted by the close of scanner.
            scanner = new TableSnapshotScanner(conf, rootDir, restoreDir, snapshotName, scan, true);
            verifyScanner(scanner, TestTableSnapshotScanner.bbb, TestTableSnapshotScanner.yyy);
            scanner.close();
        } finally {
            UTIL.getAdmin().deleteSnapshot(snapshotName);
            UTIL.deleteTable(tableName);
            tearDownCluster();
        }
    }
}

