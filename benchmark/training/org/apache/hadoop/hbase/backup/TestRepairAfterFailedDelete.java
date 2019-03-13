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
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.backup.impl.BackupSystemTable;
import org.apache.hadoop.hbase.client.Admin;
import org.apache.hadoop.hbase.client.Connection;
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
public class TestRepairAfterFailedDelete extends TestBackupBase {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestRepairAfterFailedDelete.class);

    private static final Logger LOG = LoggerFactory.getLogger(TestRepairAfterFailedDelete.class);

    @Test
    public void testRepairBackupDelete() throws Exception {
        TestRepairAfterFailedDelete.LOG.info("test repair backup delete on a single table with data");
        List<TableName> tableList = Lists.newArrayList(TestBackupBase.table1);
        String backupId = fullTableBackup(tableList);
        Assert.assertTrue(checkSucceeded(backupId));
        TestRepairAfterFailedDelete.LOG.info("backup complete");
        String[] backupIds = new String[]{ backupId };
        BackupSystemTable table = new BackupSystemTable(TestBackupBase.TEST_UTIL.getConnection());
        BackupInfo info = table.readBackupInfo(backupId);
        Path path = new Path(info.getBackupRootDir(), backupId);
        FileSystem fs = FileSystem.get(path.toUri(), TestBackupBase.conf1);
        Assert.assertTrue(fs.exists(path));
        // Snapshot backup system table before delete
        String snapshotName = "snapshot-backup";
        Connection conn = TestBackupBase.TEST_UTIL.getConnection();
        Admin admin = conn.getAdmin();
        admin.snapshot(snapshotName, BackupSystemTable.getTableName(TestBackupBase.conf1));
        int deleted = getBackupAdmin().deleteBackups(backupIds);
        Assert.assertTrue((!(fs.exists(path))));
        Assert.assertTrue(fs.exists(new Path(info.getBackupRootDir())));
        Assert.assertTrue((1 == deleted));
        // Emulate delete failure
        // Restore backup system table
        admin.disableTable(BackupSystemTable.getTableName(TestBackupBase.conf1));
        admin.restoreSnapshot(snapshotName);
        admin.enableTable(BackupSystemTable.getTableName(TestBackupBase.conf1));
        // Start backup session
        table.startBackupExclusiveOperation();
        // Start delete operation
        table.startDeleteOperation(backupIds);
        // Now run repair command to repair "failed" delete operation
        String[] args = new String[]{ "repair" };
        // Run restore
        int ret = ToolRunner.run(TestBackupBase.conf1, new BackupDriver(), args);
        Assert.assertTrue((ret == 0));
        // Verify that history length == 0
        Assert.assertTrue(((table.getBackupHistory().size()) == 0));
        table.close();
        admin.close();
    }
}

