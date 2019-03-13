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
import org.apache.hadoop.hbase.backup.impl.BackupSystemTable;
import org.apache.hadoop.hbase.client.HBaseAdmin;
import org.apache.hadoop.hbase.testclassification.LargeTests;
import org.apache.hadoop.util.ToolRunner;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Category(LargeTests.class)
public class TestFullBackupSet extends TestBackupBase {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestFullBackupSet.class);

    private static final Logger LOG = LoggerFactory.getLogger(TestFullBackupSet.class);

    /**
     * Verify that full backup is created on a single table with data correctly.
     *
     * @throws Exception
     * 		if doing the backup or an operation on the tables fails
     */
    @Test
    public void testFullBackupSetExist() throws Exception {
        TestFullBackupSet.LOG.info("Test full backup, backup set exists");
        // Create set
        try (BackupSystemTable table = new BackupSystemTable(TestBackupBase.TEST_UTIL.getConnection())) {
            String name = "name";
            table.addToBackupSet(name, new String[]{ TestBackupBase.table1.getNameAsString() });
            List<TableName> names = table.describeBackupSet(name);
            Assert.assertNotNull(names);
            Assert.assertTrue(((names.size()) == 1));
            Assert.assertTrue(names.get(0).equals(TestBackupBase.table1));
            String[] args = new String[]{ "create", "full", TestBackupBase.BACKUP_ROOT_DIR, "-s", name };
            // Run backup
            int ret = ToolRunner.run(TestBackupBase.conf1, new BackupDriver(), args);
            Assert.assertTrue((ret == 0));
            List<BackupInfo> backups = table.getBackupHistory();
            Assert.assertTrue(((backups.size()) == 1));
            String backupId = backups.get(0).getBackupId();
            Assert.assertTrue(checkSucceeded(backupId));
            TestFullBackupSet.LOG.info("backup complete");
            // Restore from set into other table
            args = new String[]{ TestBackupBase.BACKUP_ROOT_DIR, backupId, "-s", name, "-m", TestBackupBase.table1_restore.getNameAsString(), "-o" };
            // Run backup
            ret = ToolRunner.run(TestBackupBase.conf1, new RestoreDriver(), args);
            Assert.assertTrue((ret == 0));
            HBaseAdmin hba = TestBackupBase.TEST_UTIL.getHBaseAdmin();
            Assert.assertTrue(hba.tableExists(TestBackupBase.table1_restore));
            // Verify number of rows in both tables
            Assert.assertEquals(TestBackupBase.TEST_UTIL.countRows(TestBackupBase.table1), TestBackupBase.TEST_UTIL.countRows(TestBackupBase.table1_restore));
            TestBackupBase.TEST_UTIL.deleteTable(TestBackupBase.table1_restore);
            TestFullBackupSet.LOG.info("restore into other table is complete");
            hba.close();
        }
    }

    @Test
    public void testFullBackupSetDoesNotExist() throws Exception {
        TestFullBackupSet.LOG.info("test full backup, backup set does not exist");
        String name = "name1";
        String[] args = new String[]{ "create", "full", TestBackupBase.BACKUP_ROOT_DIR, "-s", name };
        // Run backup
        int ret = ToolRunner.run(TestBackupBase.conf1, new BackupDriver(), args);
        Assert.assertTrue((ret != 0));
    }
}

