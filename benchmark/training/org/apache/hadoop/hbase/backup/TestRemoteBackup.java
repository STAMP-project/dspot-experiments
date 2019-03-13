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
import java.io.IOException;
import java.util.concurrent.CountDownLatch;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.HBaseTestingUtility;
import org.apache.hadoop.hbase.HColumnDescriptor;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.backup.util.BackupUtils;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.ConnectionFactory;
import org.apache.hadoop.hbase.client.HBaseAdmin;
import org.apache.hadoop.hbase.client.HTable;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.snapshot.MobSnapshotTestingUtils;
import org.apache.hadoop.hbase.snapshot.SnapshotTestingUtils;
import org.apache.hadoop.hbase.testclassification.LargeTests;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hbase.thirdparty.com.google.common.collect.Lists;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Category(LargeTests.class)
public class TestRemoteBackup extends TestBackupBase {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestRemoteBackup.class);

    private static final Logger LOG = LoggerFactory.getLogger(TestRemoteBackup.class);

    /**
     * Verify that a remote full backup is created on a single table with data correctly.
     *
     * @throws Exception
     * 		if an operation on the table fails
     */
    @Test
    public void testFullBackupRemote() throws Exception {
        TestRemoteBackup.LOG.info("test remote full backup on a single table");
        final CountDownLatch latch = new CountDownLatch(1);
        final int NB_ROWS_IN_FAM3 = 6;
        final byte[] fam3Name = Bytes.toBytes("f3");
        final byte[] fam2Name = Bytes.toBytes("f2");
        final Connection conn = ConnectionFactory.createConnection(TestBackupBase.conf1);
        Thread t = new Thread(() -> {
            try {
                latch.await();
            } catch (InterruptedException ie) {
            }
            try {
                HTable t1 = ((HTable) (conn.getTable(TestBackupBase.table1)));
                Put p1;
                for (int i = 0; i < NB_ROWS_IN_FAM3; i++) {
                    p1 = new Put(Bytes.toBytes(("row-t1" + i)));
                    p1.addColumn(fam3Name, TestBackupBase.qualName, Bytes.toBytes(("val" + i)));
                    t1.put(p1);
                }
                TestRemoteBackup.LOG.debug((("Wrote " + NB_ROWS_IN_FAM3) + " rows into family3"));
                t1.close();
            } catch (IOException ioe) {
                throw new RuntimeException(ioe);
            }
        });
        t.start();
        TestBackupBase.table1Desc.addFamily(new HColumnDescriptor(fam3Name));
        // family 2 is MOB enabled
        HColumnDescriptor hcd = new HColumnDescriptor(fam2Name);
        hcd.setMobEnabled(true);
        hcd.setMobThreshold(0L);
        TestBackupBase.table1Desc.addFamily(hcd);
        HBaseTestingUtility.modifyTableSync(TestBackupBase.TEST_UTIL.getAdmin(), TestBackupBase.table1Desc);
        SnapshotTestingUtils.loadData(TestBackupBase.TEST_UTIL, TestBackupBase.table1, 50, fam2Name);
        HTable t1 = ((HTable) (conn.getTable(TestBackupBase.table1)));
        int rows0 = MobSnapshotTestingUtils.countMobRows(t1, fam2Name);
        latch.countDown();
        String backupId = backupTables(FULL, Lists.newArrayList(TestBackupBase.table1), TestBackupBase.BACKUP_REMOTE_ROOT_DIR);
        Assert.assertTrue(checkSucceeded(backupId));
        TestRemoteBackup.LOG.info(("backup complete " + backupId));
        Assert.assertEquals(TestBackupBase.TEST_UTIL.countRows(t1, TestBackupBase.famName), TestBackupBase.NB_ROWS_IN_BATCH);
        t.join();
        Assert.assertEquals(TestBackupBase.TEST_UTIL.countRows(t1, fam3Name), NB_ROWS_IN_FAM3);
        t1.close();
        TableName[] tablesRestoreFull = new TableName[]{ TestBackupBase.table1 };
        TableName[] tablesMapFull = new TableName[]{ TestBackupBase.table1_restore };
        BackupAdmin client = getBackupAdmin();
        client.restore(BackupUtils.createRestoreRequest(TestBackupBase.BACKUP_REMOTE_ROOT_DIR, backupId, false, tablesRestoreFull, tablesMapFull, false));
        // check tables for full restore
        HBaseAdmin hAdmin = TestBackupBase.TEST_UTIL.getHBaseAdmin();
        Assert.assertTrue(hAdmin.tableExists(TestBackupBase.table1_restore));
        // #5.2 - checking row count of tables for full restore
        HTable hTable = ((HTable) (conn.getTable(TestBackupBase.table1_restore)));
        Assert.assertEquals(TestBackupBase.TEST_UTIL.countRows(hTable, TestBackupBase.famName), TestBackupBase.NB_ROWS_IN_BATCH);
        int cnt3 = TestBackupBase.TEST_UTIL.countRows(hTable, fam3Name);
        Assert.assertTrue(((cnt3 >= 0) && (cnt3 <= NB_ROWS_IN_FAM3)));
        int rows1 = MobSnapshotTestingUtils.countMobRows(t1, fam2Name);
        Assert.assertEquals(rows0, rows1);
        hTable.close();
        hAdmin.close();
    }
}

