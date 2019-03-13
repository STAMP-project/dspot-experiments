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
package org.apache.hadoop.hbase.backup.master;


import BackupType.INCREMENTAL;
import java.util.List;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.backup.TestBackupBase;
import org.apache.hadoop.hbase.backup.impl.BackupSystemTable;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.ConnectionFactory;
import org.apache.hadoop.hbase.client.HTable;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.testclassification.LargeTests;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hbase.thirdparty.com.google.common.collect.Iterables;
import org.apache.hbase.thirdparty.com.google.common.collect.Lists;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Category(LargeTests.class)
public class TestBackupLogCleaner extends TestBackupBase {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestBackupLogCleaner.class);

    private static final Logger LOG = LoggerFactory.getLogger(TestBackupLogCleaner.class);

    // implements all test cases in 1 test since incremental full backup/
    // incremental backup has dependencies
    @Test
    public void testBackupLogCleaner() throws Exception {
        // #1 - create full backup for all tables
        TestBackupLogCleaner.LOG.info("create full backup image for all tables");
        List<TableName> tableSetFullList = Lists.newArrayList(TestBackupBase.table1, TestBackupBase.table2, TestBackupBase.table3, TestBackupBase.table4);
        try (BackupSystemTable systemTable = new BackupSystemTable(TestBackupBase.TEST_UTIL.getConnection())) {
            // Verify that we have no backup sessions yet
            Assert.assertFalse(systemTable.hasBackupSessions());
            List<FileStatus> walFiles = getListOfWALFiles(TestBackupBase.TEST_UTIL.getConfiguration());
            List<String> swalFiles = convert(walFiles);
            BackupLogCleaner cleaner = new BackupLogCleaner();
            cleaner.setConf(TestBackupBase.TEST_UTIL.getConfiguration());
            cleaner.init(null);
            cleaner.setConf(TestBackupBase.TEST_UTIL.getConfiguration());
            Iterable<FileStatus> deletable = cleaner.getDeletableFiles(walFiles);
            int size = Iterables.size(deletable);
            // We can delete all files because we do not have yet recorded backup sessions
            Assert.assertTrue((size == (walFiles.size())));
            systemTable.addWALFiles(swalFiles, "backup", "root");
            String backupIdFull = fullTableBackup(tableSetFullList);
            Assert.assertTrue(checkSucceeded(backupIdFull));
            // Check one more time
            deletable = cleaner.getDeletableFiles(walFiles);
            // We can delete wal files because they were saved into backup system table table
            size = Iterables.size(deletable);
            Assert.assertTrue((size == (walFiles.size())));
            List<FileStatus> newWalFiles = getListOfWALFiles(TestBackupBase.TEST_UTIL.getConfiguration());
            TestBackupLogCleaner.LOG.debug("WAL list after full backup");
            convert(newWalFiles);
            // New list of wal files is greater than the previous one,
            // because new wal per RS have been opened after full backup
            Assert.assertTrue(((walFiles.size()) < (newWalFiles.size())));
            Connection conn = ConnectionFactory.createConnection(TestBackupBase.conf1);
            // #2 - insert some data to table
            HTable t1 = ((HTable) (conn.getTable(TestBackupBase.table1)));
            Put p1;
            for (int i = 0; i < (TestBackupBase.NB_ROWS_IN_BATCH); i++) {
                p1 = new Put(Bytes.toBytes(("row-t1" + i)));
                p1.addColumn(TestBackupBase.famName, TestBackupBase.qualName, Bytes.toBytes(("val" + i)));
                t1.put(p1);
            }
            t1.close();
            HTable t2 = ((HTable) (conn.getTable(TestBackupBase.table2)));
            Put p2;
            for (int i = 0; i < 5; i++) {
                p2 = new Put(Bytes.toBytes(("row-t2" + i)));
                p2.addColumn(TestBackupBase.famName, TestBackupBase.qualName, Bytes.toBytes(("val" + i)));
                t2.put(p2);
            }
            t2.close();
            // #3 - incremental backup for multiple tables
            List<TableName> tableSetIncList = Lists.newArrayList(TestBackupBase.table1, TestBackupBase.table2, TestBackupBase.table3);
            String backupIdIncMultiple = backupTables(INCREMENTAL, tableSetIncList, TestBackupBase.BACKUP_ROOT_DIR);
            Assert.assertTrue(checkSucceeded(backupIdIncMultiple));
            deletable = cleaner.getDeletableFiles(newWalFiles);
            Assert.assertTrue(((Iterables.size(deletable)) == (newWalFiles.size())));
            conn.close();
        }
    }
}

