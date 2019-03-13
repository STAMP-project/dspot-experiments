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


import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.List;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.backup.BackupInfo.BackupState;
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
public class TestBackupStatusProgress extends TestBackupBase {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestBackupStatusProgress.class);

    private static final Logger LOG = LoggerFactory.getLogger(TestBackupStatusProgress.class);

    /**
     * Verify that full backup is created on a single table with data correctly.
     *
     * @throws Exception
     * 		if doing the backup or an operation on the tables fails
     */
    @Test
    public void testBackupStatusProgress() throws Exception {
        TestBackupStatusProgress.LOG.info("test backup status/progress on a single table with data");
        List<TableName> tableList = Lists.newArrayList(TestBackupBase.table1);
        String backupId = fullTableBackup(tableList);
        TestBackupStatusProgress.LOG.info("backup complete");
        Assert.assertTrue(checkSucceeded(backupId));
        BackupInfo info = getBackupAdmin().getBackupInfo(backupId);
        Assert.assertTrue(((info.getState()) == (BackupState.COMPLETE)));
        TestBackupStatusProgress.LOG.debug(info.getShortDescription());
        Assert.assertTrue(((info.getProgress()) > 0));
    }

    @Test
    public void testBackupStatusProgressCommand() throws Exception {
        TestBackupStatusProgress.LOG.info("test backup status/progress on a single table with data: command-line");
        List<TableName> tableList = Lists.newArrayList(TestBackupBase.table1);
        String backupId = fullTableBackup(tableList);
        TestBackupStatusProgress.LOG.info("backup complete");
        Assert.assertTrue(checkSucceeded(backupId));
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        System.setOut(new PrintStream(baos));
        String[] args = new String[]{ "describe", backupId };
        int ret = ToolRunner.run(TestBackupBase.conf1, new BackupDriver(), args);
        Assert.assertTrue((ret == 0));
        String responce = baos.toString();
        Assert.assertTrue(((responce.indexOf(backupId)) > 0));
        Assert.assertTrue(((responce.indexOf("COMPLETE")) > 0));
        baos = new ByteArrayOutputStream();
        System.setOut(new PrintStream(baos));
        args = new String[]{ "progress", backupId };
        ret = ToolRunner.run(TestBackupBase.conf1, new BackupDriver(), args);
        Assert.assertTrue((ret == 0));
        responce = baos.toString();
        Assert.assertTrue(((responce.indexOf(backupId)) >= 0));
        Assert.assertTrue(((responce.indexOf("progress")) > 0));
        Assert.assertTrue(((responce.indexOf("100")) > 0));
    }
}

