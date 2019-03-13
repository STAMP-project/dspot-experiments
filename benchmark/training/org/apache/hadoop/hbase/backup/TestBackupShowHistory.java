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


import BackupInfo.Filter;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.List;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.backup.util.BackupUtils;
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
public class TestBackupShowHistory extends TestBackupBase {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestBackupShowHistory.class);

    private static final Logger LOG = LoggerFactory.getLogger(TestBackupShowHistory.class);

    /**
     * Verify that full backup is created on a single table with data correctly. Verify that history
     * works as expected.
     *
     * @throws Exception
     * 		if doing the backup or an operation on the tables fails
     */
    @Test
    public void testBackupHistory() throws Exception {
        TestBackupShowHistory.LOG.info("test backup history on a single table with data");
        List<TableName> tableList = Lists.newArrayList(TestBackupBase.table1);
        String backupId = fullTableBackup(tableList);
        Assert.assertTrue(checkSucceeded(backupId));
        TestBackupShowHistory.LOG.info("backup complete");
        List<BackupInfo> history = getBackupAdmin().getHistory(10);
        Assert.assertTrue(findBackup(history, backupId));
        BackupInfo.Filter nullFilter = ( info) -> true;
        history = BackupUtils.getHistory(TestBackupBase.conf1, 10, new Path(TestBackupBase.BACKUP_ROOT_DIR), nullFilter);
        Assert.assertTrue(findBackup(history, backupId));
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        System.setOut(new PrintStream(baos));
        String[] args = new String[]{ "history", "-n", "10", "-p", TestBackupBase.BACKUP_ROOT_DIR };
        // Run backup
        int ret = ToolRunner.run(TestBackupBase.conf1, new BackupDriver(), args);
        Assert.assertTrue((ret == 0));
        TestBackupShowHistory.LOG.info("show_history");
        String output = baos.toString();
        TestBackupShowHistory.LOG.info(output);
        baos.close();
        Assert.assertTrue(((output.indexOf(backupId)) > 0));
        tableList = Lists.newArrayList(TestBackupBase.table2);
        String backupId2 = fullTableBackup(tableList);
        Assert.assertTrue(checkSucceeded(backupId2));
        TestBackupShowHistory.LOG.info(("backup complete: " + (TestBackupBase.table2)));
        BackupInfo.Filter tableNameFilter = ( image) -> {
            if ((TestBackupBase.table1) == null) {
                return true;
            }
            List<TableName> names = image.getTableNames();
            return names.contains(TestBackupBase.table1);
        };
        BackupInfo.Filter tableSetFilter = ( info) -> {
            String backupId1 = info.getBackupId();
            return backupId1.startsWith("backup");
        };
        history = getBackupAdmin().getHistory(10, tableNameFilter, tableSetFilter);
        Assert.assertTrue(((history.size()) > 0));
        boolean success = true;
        for (BackupInfo info : history) {
            if (!(info.getTableNames().contains(TestBackupBase.table1))) {
                success = false;
                break;
            }
        }
        Assert.assertTrue(success);
        history = BackupUtils.getHistory(TestBackupBase.conf1, 10, new Path(TestBackupBase.BACKUP_ROOT_DIR), tableNameFilter, tableSetFilter);
        Assert.assertTrue(((history.size()) > 0));
        success = true;
        for (BackupInfo info : history) {
            if (!(info.getTableNames().contains(TestBackupBase.table1))) {
                success = false;
                break;
            }
        }
        Assert.assertTrue(success);
        args = new String[]{ "history", "-n", "10", "-p", TestBackupBase.BACKUP_ROOT_DIR, "-t", "table1", "-s", "backup" };
        // Run backup
        ret = ToolRunner.run(TestBackupBase.conf1, new BackupDriver(), args);
        Assert.assertTrue((ret == 0));
        TestBackupShowHistory.LOG.info("show_history");
    }
}

