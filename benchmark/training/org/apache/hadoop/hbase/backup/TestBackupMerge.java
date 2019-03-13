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
package org.apache.hadoop.hbase.backup;


import BackupType.FULL;
import BackupType.INCREMENTAL;
import java.util.List;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.backup.impl.BackupAdminImpl;
import org.apache.hadoop.hbase.backup.util.BackupUtils;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.ConnectionFactory;
import org.apache.hadoop.hbase.client.HBaseAdmin;
import org.apache.hadoop.hbase.client.HTable;
import org.apache.hadoop.hbase.client.Table;
import org.apache.hadoop.hbase.testclassification.LargeTests;
import org.apache.hbase.thirdparty.com.google.common.collect.Lists;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Category(LargeTests.class)
public class TestBackupMerge extends TestBackupBase {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestBackupMerge.class);

    private static final Logger LOG = LoggerFactory.getLogger(TestBackupMerge.class);

    @Test
    public void TestIncBackupMergeRestore() throws Exception {
        int ADD_ROWS = 99;
        // #1 - create full backup for all tables
        TestBackupMerge.LOG.info("create full backup image for all tables");
        List<TableName> tables = Lists.newArrayList(TestBackupBase.table1, TestBackupBase.table2);
        // Set custom Merge Job implementation
        Connection conn = ConnectionFactory.createConnection(TestBackupBase.conf1);
        HBaseAdmin admin = ((HBaseAdmin) (conn.getAdmin()));
        BackupAdminImpl client = new BackupAdminImpl(conn);
        BackupRequest request = createBackupRequest(FULL, tables, TestBackupBase.BACKUP_ROOT_DIR);
        String backupIdFull = client.backupTables(request);
        Assert.assertTrue(checkSucceeded(backupIdFull));
        // #2 - insert some data to table1
        HTable t1 = insertIntoTable(conn, TestBackupBase.table1, TestBackupBase.famName, 1, ADD_ROWS);
        TestBackupMerge.LOG.debug(((("writing " + ADD_ROWS) + " rows to ") + (TestBackupBase.table1)));
        Assert.assertEquals(TestBackupBase.TEST_UTIL.countRows(t1), ((TestBackupBase.NB_ROWS_IN_BATCH) + ADD_ROWS));
        t1.close();
        TestBackupMerge.LOG.debug(((("written " + ADD_ROWS) + " rows to ") + (TestBackupBase.table1)));
        HTable t2 = insertIntoTable(conn, TestBackupBase.table2, TestBackupBase.famName, 1, ADD_ROWS);
        Assert.assertEquals(TestBackupBase.TEST_UTIL.countRows(t2), ((TestBackupBase.NB_ROWS_IN_BATCH) + ADD_ROWS));
        t2.close();
        TestBackupMerge.LOG.debug(((("written " + ADD_ROWS) + " rows to ") + (TestBackupBase.table2)));
        // #3 - incremental backup for multiple tables
        tables = Lists.newArrayList(TestBackupBase.table1, TestBackupBase.table2);
        request = createBackupRequest(INCREMENTAL, tables, TestBackupBase.BACKUP_ROOT_DIR);
        String backupIdIncMultiple = client.backupTables(request);
        Assert.assertTrue(checkSucceeded(backupIdIncMultiple));
        t1 = insertIntoTable(conn, TestBackupBase.table1, TestBackupBase.famName, 2, ADD_ROWS);
        t1.close();
        t2 = insertIntoTable(conn, TestBackupBase.table2, TestBackupBase.famName, 2, ADD_ROWS);
        t2.close();
        // #3 - incremental backup for multiple tables
        request = createBackupRequest(INCREMENTAL, tables, TestBackupBase.BACKUP_ROOT_DIR);
        String backupIdIncMultiple2 = client.backupTables(request);
        Assert.assertTrue(checkSucceeded(backupIdIncMultiple2));
        try (BackupAdmin bAdmin = new BackupAdminImpl(conn)) {
            String[] backups = new String[]{ backupIdIncMultiple, backupIdIncMultiple2 };
            bAdmin.mergeBackups(backups);
        }
        // #6 - restore incremental backup for multiple tables, with overwrite
        TableName[] tablesRestoreIncMultiple = new TableName[]{ TestBackupBase.table1, TestBackupBase.table2 };
        TableName[] tablesMapIncMultiple = new TableName[]{ TestBackupBase.table1_restore, TestBackupBase.table2_restore };
        client.restore(BackupUtils.createRestoreRequest(TestBackupBase.BACKUP_ROOT_DIR, backupIdIncMultiple2, false, tablesRestoreIncMultiple, tablesMapIncMultiple, true));
        Table hTable = conn.getTable(TestBackupBase.table1_restore);
        TestBackupMerge.LOG.debug(("After incremental restore: " + (hTable.getTableDescriptor())));
        int countRows = TestBackupBase.TEST_UTIL.countRows(hTable, TestBackupBase.famName);
        TestBackupMerge.LOG.debug((("f1 has " + countRows) + " rows"));
        Assert.assertEquals(((TestBackupBase.NB_ROWS_IN_BATCH) + (2 * ADD_ROWS)), countRows);
        hTable.close();
        hTable = conn.getTable(TestBackupBase.table2_restore);
        Assert.assertEquals(TestBackupBase.TEST_UTIL.countRows(hTable), ((TestBackupBase.NB_ROWS_IN_BATCH) + (2 * ADD_ROWS)));
        hTable.close();
        admin.close();
        conn.close();
    }
}

