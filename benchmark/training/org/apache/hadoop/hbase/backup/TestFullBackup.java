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
import org.apache.hadoop.hbase.backup.impl.BackupSystemTable;
import org.apache.hadoop.hbase.testclassification.LargeTests;
import org.apache.hadoop.util.ToolRunner;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Category(LargeTests.class)
public class TestFullBackup extends TestBackupBase {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestFullBackup.class);

    private static final Logger LOG = LoggerFactory.getLogger(TestFullBackup.class);

    @Test
    public void testFullBackupMultipleCommand() throws Exception {
        TestFullBackup.LOG.info("test full backup on a multiple tables with data: command-line");
        try (BackupSystemTable table = new BackupSystemTable(TestBackupBase.TEST_UTIL.getConnection())) {
            int before = table.getBackupHistory().size();
            String[] args = new String[]{ "create", "full", TestBackupBase.BACKUP_ROOT_DIR, "-t", ((TestBackupBase.table1.getNameAsString()) + ",") + (TestBackupBase.table2.getNameAsString()) };
            // Run backup
            int ret = ToolRunner.run(TestBackupBase.conf1, new BackupDriver(), args);
            Assert.assertTrue((ret == 0));
            List<BackupInfo> backups = table.getBackupHistory();
            int after = table.getBackupHistory().size();
            Assert.assertTrue((after == (before + 1)));
            for (BackupInfo data : backups) {
                String backupId = data.getBackupId();
                Assert.assertTrue(checkSucceeded(backupId));
            }
        }
        TestFullBackup.LOG.info("backup complete");
    }
}

