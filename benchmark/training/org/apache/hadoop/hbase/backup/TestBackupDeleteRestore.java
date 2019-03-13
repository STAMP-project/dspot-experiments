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
import org.apache.hadoop.hbase.client.Delete;
import org.apache.hadoop.hbase.client.HBaseAdmin;
import org.apache.hadoop.hbase.client.Table;
import org.apache.hadoop.hbase.testclassification.MediumTests;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hbase.thirdparty.com.google.common.collect.Lists;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Category(MediumTests.class)
public class TestBackupDeleteRestore extends TestBackupBase {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestBackupDeleteRestore.class);

    private static final Logger LOG = LoggerFactory.getLogger(TestBackupDeleteRestore.class);

    /**
     * Verify that load data- backup - delete some data - restore works as expected - deleted data get
     * restored.
     *
     * @throws Exception
     * 		if doing the backup or an operation on the tables fails
     */
    @Test
    public void testBackupDeleteRestore() throws Exception {
        TestBackupDeleteRestore.LOG.info("test full restore on a single table empty table");
        List<TableName> tables = Lists.newArrayList(TestBackupBase.table1);
        String backupId = fullTableBackup(tables);
        Assert.assertTrue(checkSucceeded(backupId));
        TestBackupDeleteRestore.LOG.info("backup complete");
        int numRows = TestBackupBase.TEST_UTIL.countRows(TestBackupBase.table1);
        HBaseAdmin hba = TestBackupBase.TEST_UTIL.getHBaseAdmin();
        // delete row
        try (Table table = TestBackupBase.TEST_UTIL.getConnection().getTable(TestBackupBase.table1)) {
            Delete delete = new Delete(Bytes.toBytes("row0"));
            table.delete(delete);
            hba.flush(TestBackupBase.table1);
        }
        TableName[] tableset = new TableName[]{ TestBackupBase.table1 };
        TableName[] tablemap = null;// new TableName[] { table1_restore };

        BackupAdmin client = getBackupAdmin();
        client.restore(BackupUtils.createRestoreRequest(TestBackupBase.BACKUP_ROOT_DIR, backupId, false, tableset, tablemap, true));
        int numRowsAfterRestore = TestBackupBase.TEST_UTIL.countRows(TestBackupBase.table1);
        Assert.assertEquals(numRows, numRowsAfterRestore);
        hba.close();
    }
}

