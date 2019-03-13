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
package org.apache.hadoop.hbase.snapshot;


import SnapshotType.FLUSH;
import java.io.IOException;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.HBaseTestingUtility;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Admin;
import org.apache.hadoop.hbase.testclassification.LargeTests;
import org.apache.hadoop.hbase.testclassification.RegionServerTests;
import org.apache.hadoop.hbase.util.Bytes;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Test clone/restore snapshots from the client
 *
 * TODO This is essentially a clone of TestRestoreSnapshotFromClient.  This is worth refactoring
 * this because there will be a few more flavors of snapshots that need to run these tests.
 */
@Category({ RegionServerTests.class, LargeTests.class })
public class TestRestoreFlushSnapshotFromClient {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestRestoreFlushSnapshotFromClient.class);

    private static final Logger LOG = LoggerFactory.getLogger(TestRestoreFlushSnapshotFromClient.class);

    protected static final HBaseTestingUtility UTIL = new HBaseTestingUtility();

    protected final byte[] FAMILY = Bytes.toBytes("cf");

    protected byte[] snapshotName0;

    protected byte[] snapshotName1;

    protected byte[] snapshotName2;

    protected int snapshot0Rows;

    protected int snapshot1Rows;

    protected TableName tableName;

    protected Admin admin;

    @Test
    public void testTakeFlushSnapshot() throws IOException {
        // taking happens in setup.
    }

    @Test
    public void testRestoreSnapshot() throws IOException {
        verifyRowCount(TestRestoreFlushSnapshotFromClient.UTIL, tableName, snapshot1Rows);
        // Restore from snapshot-0
        admin.disableTable(tableName);
        admin.restoreSnapshot(snapshotName0);
        logFSTree();
        admin.enableTable(tableName);
        TestRestoreFlushSnapshotFromClient.LOG.info("=== after restore with 500 row snapshot");
        logFSTree();
        verifyRowCount(TestRestoreFlushSnapshotFromClient.UTIL, tableName, snapshot0Rows);
        // Restore from snapshot-1
        admin.disableTable(tableName);
        admin.restoreSnapshot(snapshotName1);
        admin.enableTable(tableName);
        verifyRowCount(TestRestoreFlushSnapshotFromClient.UTIL, tableName, snapshot1Rows);
    }

    @Test(expected = SnapshotDoesNotExistException.class)
    public void testCloneNonExistentSnapshot() throws IOException, InterruptedException {
        String snapshotName = "random-snapshot-" + (System.currentTimeMillis());
        TableName tableName = TableName.valueOf(("random-table-" + (System.currentTimeMillis())));
        admin.cloneSnapshot(snapshotName, tableName);
    }

    @Test
    public void testCloneSnapshot() throws IOException, InterruptedException {
        TableName clonedTableName = TableName.valueOf(("clonedtb-" + (System.currentTimeMillis())));
        testCloneSnapshot(clonedTableName, snapshotName0, snapshot0Rows);
        testCloneSnapshot(clonedTableName, snapshotName1, snapshot1Rows);
    }

    @Test
    public void testRestoreSnapshotOfCloned() throws IOException, InterruptedException {
        TableName clonedTableName = TableName.valueOf(("clonedtb-" + (System.currentTimeMillis())));
        admin.cloneSnapshot(snapshotName0, clonedTableName);
        verifyRowCount(TestRestoreFlushSnapshotFromClient.UTIL, clonedTableName, snapshot0Rows);
        admin.snapshot(Bytes.toString(snapshotName2), clonedTableName, FLUSH);
        TestRestoreFlushSnapshotFromClient.UTIL.deleteTable(clonedTableName);
        admin.cloneSnapshot(snapshotName2, clonedTableName);
        verifyRowCount(TestRestoreFlushSnapshotFromClient.UTIL, clonedTableName, snapshot0Rows);
        TestRestoreFlushSnapshotFromClient.UTIL.deleteTable(clonedTableName);
    }
}

