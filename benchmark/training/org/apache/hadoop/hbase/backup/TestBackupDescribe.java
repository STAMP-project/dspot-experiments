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


import BackupCommands.NO_ACTIVE_SESSION_FOUND;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.List;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.backup.BackupInfo.BackupState;
import org.apache.hadoop.hbase.backup.impl.BackupSystemTable;
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
public class TestBackupDescribe extends TestBackupBase {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestBackupDescribe.class);

    private static final Logger LOG = LoggerFactory.getLogger(TestBackupDescribe.class);

    /**
     * Verify that describe works as expected if incorrect backup Id is supplied.
     *
     * @throws Exception
     * 		if creating the {@link BackupDriver} fails
     */
    @Test
    public void testBackupDescribe() throws Exception {
        TestBackupDescribe.LOG.info("test backup describe on a single table with data");
        String[] args = new String[]{ "describe", "backup_2" };
        int ret = ToolRunner.run(TestBackupBase.conf1, new BackupDriver(), args);
        Assert.assertTrue((ret < 0));
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        System.setErr(new PrintStream(baos));
        args = new String[]{ "progress" };
        ToolRunner.run(TestBackupBase.TEST_UTIL.getConfiguration(), new BackupDriver(), args);
        String output = baos.toString();
        TestBackupDescribe.LOG.info(("Output from progress: " + output));
        Assert.assertTrue(((output.indexOf(NO_ACTIVE_SESSION_FOUND)) >= 0));
    }

    @Test
    public void testBackupSetCommandWithNonExistentTable() throws Exception {
        String[] args = new String[]{ "set", "add", "some_set", "table" };
        // Run backup
        int ret = ToolRunner.run(TestBackupBase.conf1, new BackupDriver(), args);
        Assert.assertNotEquals(ret, 0);
    }

    @Test
    public void testBackupDescribeCommand() throws Exception {
        TestBackupDescribe.LOG.info("test backup describe on a single table with data: command-line");
        List<TableName> tableList = Lists.newArrayList(TestBackupBase.table1);
        String backupId = fullTableBackup(tableList);
        TestBackupDescribe.LOG.info("backup complete");
        Assert.assertTrue(checkSucceeded(backupId));
        BackupInfo info = getBackupAdmin().getBackupInfo(backupId);
        Assert.assertTrue(((info.getState()) == (BackupState.COMPLETE)));
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        System.setOut(new PrintStream(baos));
        String[] args = new String[]{ "describe", backupId };
        // Run backup
        int ret = ToolRunner.run(TestBackupBase.conf1, new BackupDriver(), args);
        Assert.assertTrue((ret == 0));
        String response = baos.toString();
        Assert.assertTrue(((response.indexOf(backupId)) > 0));
        Assert.assertTrue(((response.indexOf("COMPLETE")) > 0));
        BackupSystemTable table = new BackupSystemTable(TestBackupBase.TEST_UTIL.getConnection());
        BackupInfo status = table.readBackupInfo(backupId);
        String desc = status.getShortDescription();
        table.close();
        Assert.assertTrue(((response.indexOf(desc)) >= 0));
    }
}

