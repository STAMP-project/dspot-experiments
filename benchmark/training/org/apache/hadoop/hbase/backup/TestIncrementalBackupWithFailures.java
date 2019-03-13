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
import java.util.List;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.HBaseTestingUtility;
import org.apache.hadoop.hbase.HColumnDescriptor;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.backup.impl.BackupAdminImpl;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.ConnectionFactory;
import org.apache.hadoop.hbase.client.HBaseAdmin;
import org.apache.hadoop.hbase.client.HTable;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.testclassification.LargeTests;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hbase.thirdparty.com.google.common.collect.Lists;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Category(LargeTests.class)
@RunWith(Parameterized.class)
public class TestIncrementalBackupWithFailures extends TestBackupBase {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestIncrementalBackupWithFailures.class);

    private static final Logger LOG = LoggerFactory.getLogger(TestIncrementalBackupWithFailures.class);

    public TestIncrementalBackupWithFailures(Boolean b) {
    }

    // implement all test cases in 1 test since incremental backup/restore has dependencies
    @Test
    public void testIncBackupRestore() throws Exception {
        int ADD_ROWS = 99;
        // #1 - create full backup for all tables
        TestIncrementalBackupWithFailures.LOG.info("create full backup image for all tables");
        List<TableName> tables = Lists.newArrayList(TestBackupBase.table1, TestBackupBase.table2);
        final byte[] fam3Name = Bytes.toBytes("f3");
        TestBackupBase.table1Desc.addFamily(new HColumnDescriptor(fam3Name));
        HBaseTestingUtility.modifyTableSync(TestBackupBase.TEST_UTIL.getAdmin(), TestBackupBase.table1Desc);
        Connection conn = ConnectionFactory.createConnection(TestBackupBase.conf1);
        int NB_ROWS_FAM3 = 6;
        insertIntoTable(conn, TestBackupBase.table1, fam3Name, 3, NB_ROWS_FAM3).close();
        HBaseAdmin admin = null;
        admin = ((HBaseAdmin) (conn.getAdmin()));
        BackupAdminImpl client = new BackupAdminImpl(conn);
        BackupRequest request = createBackupRequest(FULL, tables, TestBackupBase.BACKUP_ROOT_DIR);
        String backupIdFull = client.backupTables(request);
        Assert.assertTrue(checkSucceeded(backupIdFull));
        // #2 - insert some data to table
        HTable t1 = insertIntoTable(conn, TestBackupBase.table1, TestBackupBase.famName, 1, ADD_ROWS);
        TestIncrementalBackupWithFailures.LOG.debug(((("writing " + ADD_ROWS) + " rows to ") + (TestBackupBase.table1)));
        Assert.assertEquals(TestBackupBase.TEST_UTIL.countRows(t1), (((TestBackupBase.NB_ROWS_IN_BATCH) + ADD_ROWS) + NB_ROWS_FAM3));
        t1.close();
        TestIncrementalBackupWithFailures.LOG.debug(((("written " + ADD_ROWS) + " rows to ") + (TestBackupBase.table1)));
        HTable t2 = ((HTable) (conn.getTable(TestBackupBase.table2)));
        Put p2;
        for (int i = 0; i < 5; i++) {
            p2 = new Put(Bytes.toBytes(("row-t2" + i)));
            p2.addColumn(TestBackupBase.famName, TestBackupBase.qualName, Bytes.toBytes(("val" + i)));
            t2.put(p2);
        }
        Assert.assertEquals(TestBackupBase.TEST_UTIL.countRows(t2), ((TestBackupBase.NB_ROWS_IN_BATCH) + 5));
        t2.close();
        TestIncrementalBackupWithFailures.LOG.debug(((("written " + 5) + " rows to ") + (TestBackupBase.table2)));
        // #3 - incremental backup for multiple tables
        incrementalBackupWithFailures();
        admin.close();
        conn.close();
    }
}

