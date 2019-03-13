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


import java.io.IOException;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.backup.util.BackupUtils;
import org.apache.hadoop.hbase.client.HBaseAdmin;
import org.apache.hadoop.hbase.testclassification.LargeTests;
import org.apache.hadoop.util.ToolRunner;
import org.apache.hbase.thirdparty.com.google.common.collect.Lists;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Category(LargeTests.class)
public class TestFullRestore extends TestBackupBase {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestFullRestore.class);

    private static final Logger LOG = LoggerFactory.getLogger(TestFullRestore.class);

    /**
     * Verify that a single table is restored to a new table.
     *
     * @throws Exception
     * 		if doing the backup, restoring it or an operation on the tables fails
     */
    @Test
    public void testFullRestoreSingle() throws Exception {
        TestFullRestore.LOG.info("test full restore on a single table empty table");
        List<TableName> tables = Lists.newArrayList(TestBackupBase.table1);
        String backupId = fullTableBackup(tables);
        Assert.assertTrue(checkSucceeded(backupId));
        TestFullRestore.LOG.info("backup complete");
        TableName[] tableset = new TableName[]{ TestBackupBase.table1 };
        TableName[] tablemap = new TableName[]{ TestBackupBase.table1_restore };
        BackupAdmin client = getBackupAdmin();
        client.restore(BackupUtils.createRestoreRequest(TestBackupBase.BACKUP_ROOT_DIR, backupId, false, tableset, tablemap, false));
        HBaseAdmin hba = TestBackupBase.TEST_UTIL.getHBaseAdmin();
        Assert.assertTrue(hba.tableExists(TestBackupBase.table1_restore));
        TestBackupBase.TEST_UTIL.deleteTable(TestBackupBase.table1_restore);
        hba.close();
    }

    @Test
    public void testFullRestoreSingleCommand() throws Exception {
        TestFullRestore.LOG.info("test full restore on a single table empty table: command-line");
        List<TableName> tables = Lists.newArrayList(TestBackupBase.table1);
        String backupId = fullTableBackup(tables);
        TestFullRestore.LOG.info("backup complete");
        Assert.assertTrue(checkSucceeded(backupId));
        // restore <backup_root_path> <backup_id> <tables> [tableMapping]
        String[] args = new String[]{ TestBackupBase.BACKUP_ROOT_DIR, backupId, "-t", TestBackupBase.table1.getNameAsString(), "-m", TestBackupBase.table1_restore.getNameAsString() };
        // Run backup
        int ret = ToolRunner.run(TestBackupBase.conf1, new RestoreDriver(), args);
        Assert.assertTrue((ret == 0));
        HBaseAdmin hba = TestBackupBase.TEST_UTIL.getHBaseAdmin();
        Assert.assertTrue(hba.tableExists(TestBackupBase.table1_restore));
        TestBackupBase.TEST_UTIL.deleteTable(TestBackupBase.table1_restore);
        hba.close();
    }

    @Test
    public void testFullRestoreCheckCommand() throws Exception {
        TestFullRestore.LOG.info("test full restore on a single table: command-line, check only");
        List<TableName> tables = Lists.newArrayList(TestBackupBase.table1);
        String backupId = fullTableBackup(tables);
        TestFullRestore.LOG.info("backup complete");
        Assert.assertTrue(checkSucceeded(backupId));
        // restore <backup_root_path> <backup_id> <tables> [tableMapping]
        String[] args = new String[]{ TestBackupBase.BACKUP_ROOT_DIR, backupId, "-t", TestBackupBase.table1.getNameAsString(), "-m", TestBackupBase.table1_restore.getNameAsString(), "-c" };
        // Run backup
        int ret = ToolRunner.run(TestBackupBase.conf1, new RestoreDriver(), args);
        Assert.assertTrue((ret == 0));
        // Verify that table has not been restored
        HBaseAdmin hba = TestBackupBase.TEST_UTIL.getHBaseAdmin();
        Assert.assertFalse(hba.tableExists(TestBackupBase.table1_restore));
    }

    /**
     * Verify that multiple tables are restored to new tables.
     *
     * @throws Exception
     * 		if doing the backup, restoring it or an operation on the tables fails
     */
    @Test
    public void testFullRestoreMultiple() throws Exception {
        TestFullRestore.LOG.info("create full backup image on multiple tables");
        List<TableName> tables = Lists.newArrayList(TestBackupBase.table2, TestBackupBase.table3);
        String backupId = fullTableBackup(tables);
        Assert.assertTrue(checkSucceeded(backupId));
        TableName[] restore_tableset = new TableName[]{ TestBackupBase.table2, TestBackupBase.table3 };
        TableName[] tablemap = new TableName[]{ TestBackupBase.table2_restore, TestBackupBase.table3_restore };
        BackupAdmin client = getBackupAdmin();
        client.restore(BackupUtils.createRestoreRequest(TestBackupBase.BACKUP_ROOT_DIR, backupId, false, restore_tableset, tablemap, false));
        HBaseAdmin hba = TestBackupBase.TEST_UTIL.getHBaseAdmin();
        Assert.assertTrue(hba.tableExists(TestBackupBase.table2_restore));
        Assert.assertTrue(hba.tableExists(TestBackupBase.table3_restore));
        TestBackupBase.TEST_UTIL.deleteTable(TestBackupBase.table2_restore);
        TestBackupBase.TEST_UTIL.deleteTable(TestBackupBase.table3_restore);
        hba.close();
    }

    /**
     * Verify that multiple tables are restored to new tables.
     *
     * @throws Exception
     * 		if doing the backup, restoring it or an operation on the tables fails
     */
    @Test
    public void testFullRestoreMultipleCommand() throws Exception {
        TestFullRestore.LOG.info("create full backup image on multiple tables: command-line");
        List<TableName> tables = Lists.newArrayList(TestBackupBase.table2, TestBackupBase.table3);
        String backupId = fullTableBackup(tables);
        Assert.assertTrue(checkSucceeded(backupId));
        TableName[] restore_tableset = new TableName[]{ TestBackupBase.table2, TestBackupBase.table3 };
        TableName[] tablemap = new TableName[]{ TestBackupBase.table2_restore, TestBackupBase.table3_restore };
        // restore <backup_root_path> <backup_id> <tables> [tableMapping]
        String[] args = new String[]{ TestBackupBase.BACKUP_ROOT_DIR, backupId, "-t", StringUtils.join(restore_tableset, ","), "-m", StringUtils.join(tablemap, ",") };
        // Run backup
        int ret = ToolRunner.run(TestBackupBase.conf1, new RestoreDriver(), args);
        Assert.assertTrue((ret == 0));
        HBaseAdmin hba = TestBackupBase.TEST_UTIL.getHBaseAdmin();
        Assert.assertTrue(hba.tableExists(TestBackupBase.table2_restore));
        Assert.assertTrue(hba.tableExists(TestBackupBase.table3_restore));
        TestBackupBase.TEST_UTIL.deleteTable(TestBackupBase.table2_restore);
        TestBackupBase.TEST_UTIL.deleteTable(TestBackupBase.table3_restore);
        hba.close();
    }

    /**
     * Verify that a single table is restored using overwrite.
     *
     * @throws Exception
     * 		if doing the backup or restoring it fails
     */
    @Test
    public void testFullRestoreSingleOverwrite() throws Exception {
        TestFullRestore.LOG.info("test full restore on a single table empty table");
        List<TableName> tables = Lists.newArrayList(TestBackupBase.table1);
        String backupId = fullTableBackup(tables);
        Assert.assertTrue(checkSucceeded(backupId));
        TestFullRestore.LOG.info("backup complete");
        TableName[] tableset = new TableName[]{ TestBackupBase.table1 };
        BackupAdmin client = getBackupAdmin();
        client.restore(BackupUtils.createRestoreRequest(TestBackupBase.BACKUP_ROOT_DIR, backupId, false, tableset, null, true));
    }

    /**
     * Verify that a single table is restored using overwrite.
     *
     * @throws Exception
     * 		if doing the backup or an operation on the tables fails
     */
    @Test
    public void testFullRestoreSingleOverwriteCommand() throws Exception {
        TestFullRestore.LOG.info("test full restore on a single table empty table: command-line");
        List<TableName> tables = Lists.newArrayList(TestBackupBase.table1);
        String backupId = fullTableBackup(tables);
        Assert.assertTrue(checkSucceeded(backupId));
        TestFullRestore.LOG.info("backup complete");
        TableName[] tableset = new TableName[]{ TestBackupBase.table1 };
        // restore <backup_root_path> <backup_id> <tables> [tableMapping]
        String[] args = new String[]{ TestBackupBase.BACKUP_ROOT_DIR, backupId, "-t", StringUtils.join(tableset, ","), "-o" };
        // Run restore
        int ret = ToolRunner.run(TestBackupBase.conf1, new RestoreDriver(), args);
        Assert.assertTrue((ret == 0));
        HBaseAdmin hba = TestBackupBase.TEST_UTIL.getHBaseAdmin();
        Assert.assertTrue(hba.tableExists(TestBackupBase.table1));
        hba.close();
    }

    /**
     * Verify that multiple tables are restored to new tables using overwrite.
     *
     * @throws Exception
     * 		if doing the backup or restoring it fails
     */
    @Test
    public void testFullRestoreMultipleOverwrite() throws Exception {
        TestFullRestore.LOG.info("create full backup image on multiple tables");
        List<TableName> tables = Lists.newArrayList(TestBackupBase.table2, TestBackupBase.table3);
        String backupId = fullTableBackup(tables);
        Assert.assertTrue(checkSucceeded(backupId));
        TableName[] restore_tableset = new TableName[]{ TestBackupBase.table2, TestBackupBase.table3 };
        BackupAdmin client = getBackupAdmin();
        client.restore(BackupUtils.createRestoreRequest(TestBackupBase.BACKUP_ROOT_DIR, backupId, false, restore_tableset, null, true));
    }

    /**
     * Verify that multiple tables are restored to new tables using overwrite.
     *
     * @throws Exception
     * 		if doing the backup or an operation on the tables fails
     */
    @Test
    public void testFullRestoreMultipleOverwriteCommand() throws Exception {
        TestFullRestore.LOG.info("create full backup image on multiple tables: command-line");
        List<TableName> tables = Lists.newArrayList(TestBackupBase.table2, TestBackupBase.table3);
        String backupId = fullTableBackup(tables);
        Assert.assertTrue(checkSucceeded(backupId));
        TableName[] restore_tableset = new TableName[]{ TestBackupBase.table2, TestBackupBase.table3 };
        // restore <backup_root_path> <backup_id> <tables> [tableMapping]
        String[] args = new String[]{ TestBackupBase.BACKUP_ROOT_DIR, backupId, "-t", StringUtils.join(restore_tableset, ","), "-o" };
        // Run backup
        int ret = ToolRunner.run(TestBackupBase.conf1, new RestoreDriver(), args);
        Assert.assertTrue((ret == 0));
        HBaseAdmin hba = TestBackupBase.TEST_UTIL.getHBaseAdmin();
        Assert.assertTrue(hba.tableExists(TestBackupBase.table2));
        Assert.assertTrue(hba.tableExists(TestBackupBase.table3));
        hba.close();
    }

    /**
     * Verify that restore fails on a single table that does not exist.
     *
     * @throws Exception
     * 		if doing the backup or restoring it fails
     */
    @Test(expected = IOException.class)
    public void testFullRestoreSingleDNE() throws Exception {
        TestFullRestore.LOG.info("test restore fails on a single table that does not exist");
        List<TableName> tables = Lists.newArrayList(TestBackupBase.table1);
        String backupId = fullTableBackup(tables);
        Assert.assertTrue(checkSucceeded(backupId));
        TestFullRestore.LOG.info("backup complete");
        TableName[] tableset = new TableName[]{ TableName.valueOf("faketable") };
        TableName[] tablemap = new TableName[]{ TestBackupBase.table1_restore };
        BackupAdmin client = getBackupAdmin();
        client.restore(BackupUtils.createRestoreRequest(TestBackupBase.BACKUP_ROOT_DIR, backupId, false, tableset, tablemap, false));
    }

    /**
     * Verify that restore fails on a single table that does not exist.
     *
     * @throws Exception
     * 		if doing the backup or restoring it fails
     */
    @Test
    public void testFullRestoreSingleDNECommand() throws Exception {
        TestFullRestore.LOG.info("test restore fails on a single table that does not exist: command-line");
        List<TableName> tables = Lists.newArrayList(TestBackupBase.table1);
        String backupId = fullTableBackup(tables);
        Assert.assertTrue(checkSucceeded(backupId));
        TestFullRestore.LOG.info("backup complete");
        TableName[] tableset = new TableName[]{ TableName.valueOf("faketable") };
        TableName[] tablemap = new TableName[]{ TestBackupBase.table1_restore };
        String[] args = new String[]{ TestBackupBase.BACKUP_ROOT_DIR, backupId, StringUtils.join(tableset, ","), "-m", StringUtils.join(tablemap, ",") };
        // Run restore
        int ret = ToolRunner.run(TestBackupBase.conf1, new RestoreDriver(), args);
        Assert.assertTrue((ret != 0));
    }

    /**
     * Verify that restore fails on multiple tables that do not exist.
     *
     * @throws Exception
     * 		if doing the backup or restoring it fails
     */
    @Test(expected = IOException.class)
    public void testFullRestoreMultipleDNE() throws Exception {
        TestFullRestore.LOG.info("test restore fails on multiple tables that do not exist");
        List<TableName> tables = Lists.newArrayList(TestBackupBase.table2, TestBackupBase.table3);
        String backupId = fullTableBackup(tables);
        Assert.assertTrue(checkSucceeded(backupId));
        TableName[] restore_tableset = new TableName[]{ TableName.valueOf("faketable1"), TableName.valueOf("faketable2") };
        TableName[] tablemap = new TableName[]{ TestBackupBase.table2_restore, TestBackupBase.table3_restore };
        BackupAdmin client = getBackupAdmin();
        client.restore(BackupUtils.createRestoreRequest(TestBackupBase.BACKUP_ROOT_DIR, backupId, false, restore_tableset, tablemap, false));
    }

    /**
     * Verify that restore fails on multiple tables that do not exist.
     *
     * @throws Exception
     * 		if doing the backup or restoring it fails
     */
    @Test
    public void testFullRestoreMultipleDNECommand() throws Exception {
        TestFullRestore.LOG.info("test restore fails on multiple tables that do not exist: command-line");
        List<TableName> tables = Lists.newArrayList(TestBackupBase.table2, TestBackupBase.table3);
        String backupId = fullTableBackup(tables);
        Assert.assertTrue(checkSucceeded(backupId));
        TableName[] restore_tableset = new TableName[]{ TableName.valueOf("faketable1"), TableName.valueOf("faketable2") };
        TableName[] tablemap = new TableName[]{ TestBackupBase.table2_restore, TestBackupBase.table3_restore };
        String[] args = new String[]{ TestBackupBase.BACKUP_ROOT_DIR, backupId, StringUtils.join(restore_tableset, ","), "-m", StringUtils.join(tablemap, ",") };
        // Run restore
        int ret = ToolRunner.run(TestBackupBase.conf1, new RestoreDriver(), args);
        Assert.assertTrue((ret != 0));
    }
}

