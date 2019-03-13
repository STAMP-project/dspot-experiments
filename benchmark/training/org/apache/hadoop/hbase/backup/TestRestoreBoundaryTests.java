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


import java.util.List;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.backup.util.BackupUtils;
import org.apache.hadoop.hbase.client.HBaseAdmin;
import org.apache.hadoop.hbase.testclassification.LargeTests;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Category(LargeTests.class)
public class TestRestoreBoundaryTests extends TestBackupBase {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestRestoreBoundaryTests.class);

    private static final Logger LOG = LoggerFactory.getLogger(TestRestoreBoundaryTests.class);

    /**
     * Verify that a single empty table is restored to a new table.
     *
     * @throws Exception
     * 		if doing the backup or an operation on the tables fails
     */
    @Test
    public void testFullRestoreSingleEmpty() throws Exception {
        TestRestoreBoundaryTests.LOG.info("test full restore on a single table empty table");
        String backupId = fullTableBackup(toList(TestBackupBase.table1.getNameAsString()));
        TestRestoreBoundaryTests.LOG.info("backup complete");
        TableName[] tableset = new TableName[]{ TestBackupBase.table1 };
        TableName[] tablemap = new TableName[]{ TestBackupBase.table1_restore };
        getBackupAdmin().restore(BackupUtils.createRestoreRequest(TestBackupBase.BACKUP_ROOT_DIR, backupId, false, tableset, tablemap, false));
        HBaseAdmin hba = TestBackupBase.TEST_UTIL.getHBaseAdmin();
        Assert.assertTrue(hba.tableExists(TestBackupBase.table1_restore));
        TestBackupBase.TEST_UTIL.deleteTable(TestBackupBase.table1_restore);
    }

    /**
     * Verify that multiple tables are restored to new tables.
     *
     * @throws Exception
     * 		if doing the backup or an operation on the tables fails
     */
    @Test
    public void testFullRestoreMultipleEmpty() throws Exception {
        TestRestoreBoundaryTests.LOG.info("create full backup image on multiple tables");
        List<TableName> tables = toList(TestBackupBase.table2.getNameAsString(), TestBackupBase.table3.getNameAsString());
        String backupId = fullTableBackup(tables);
        TableName[] restore_tableset = new TableName[]{ TestBackupBase.table2, TestBackupBase.table3 };
        TableName[] tablemap = new TableName[]{ TestBackupBase.table2_restore, TestBackupBase.table3_restore };
        getBackupAdmin().restore(BackupUtils.createRestoreRequest(TestBackupBase.BACKUP_ROOT_DIR, backupId, false, restore_tableset, tablemap, false));
        HBaseAdmin hba = TestBackupBase.TEST_UTIL.getHBaseAdmin();
        Assert.assertTrue(hba.tableExists(TestBackupBase.table2_restore));
        Assert.assertTrue(hba.tableExists(TestBackupBase.table3_restore));
        TestBackupBase.TEST_UTIL.deleteTable(TestBackupBase.table2_restore);
        TestBackupBase.TEST_UTIL.deleteTable(TestBackupBase.table3_restore);
        hba.close();
    }
}

