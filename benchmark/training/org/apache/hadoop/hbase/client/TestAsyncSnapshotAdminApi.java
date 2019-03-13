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
package org.apache.hadoop.hbase.client;


import SnapshotType.FLUSH;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.testclassification.ClientTests;
import org.apache.hadoop.hbase.testclassification.LargeTests;
import org.apache.hadoop.hbase.util.Bytes;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;


@RunWith(Parameterized.class)
@Category({ LargeTests.class, ClientTests.class })
public class TestAsyncSnapshotAdminApi extends TestAsyncAdminBase {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestAsyncSnapshotAdminApi.class);

    private static final Pattern MATCH_ALL = Pattern.compile(".*");

    String snapshotName1 = "snapshotName1";

    String snapshotName2 = "snapshotName2";

    String snapshotName3 = "snapshotName3";

    @Test
    public void testTakeSnapshot() throws Exception {
        Admin syncAdmin = TestAsyncAdminBase.TEST_UTIL.getAdmin();
        Table table = TestAsyncAdminBase.TEST_UTIL.createTable(tableName, Bytes.toBytes("f1"));
        for (int i = 0; i < 3000; i++) {
            table.put(new Put(Bytes.toBytes(i)).addColumn(Bytes.toBytes("f1"), Bytes.toBytes("cq"), Bytes.toBytes(i)));
        }
        admin.snapshot(snapshotName1, tableName).get();
        admin.snapshot(snapshotName2, tableName).get();
        List<SnapshotDescription> snapshots = syncAdmin.listSnapshots();
        Collections.sort(snapshots, ( snap1, snap2) -> {
            assertNotNull(snap1);
            assertNotNull(snap1.getName());
            assertNotNull(snap2);
            assertNotNull(snap2.getName());
            return snap1.getName().compareTo(snap2.getName());
        });
        Assert.assertEquals(snapshotName1, snapshots.get(0).getName());
        Assert.assertEquals(tableName, snapshots.get(0).getTableName());
        Assert.assertEquals(FLUSH, snapshots.get(0).getType());
        Assert.assertEquals(snapshotName2, snapshots.get(1).getName());
        Assert.assertEquals(tableName, snapshots.get(1).getTableName());
        Assert.assertEquals(FLUSH, snapshots.get(1).getType());
    }

    @Test
    public void testCloneSnapshot() throws Exception {
        TableName tableName2 = TableName.valueOf("testCloneSnapshot2");
        Admin syncAdmin = TestAsyncAdminBase.TEST_UTIL.getAdmin();
        Table table = TestAsyncAdminBase.TEST_UTIL.createTable(tableName, Bytes.toBytes("f1"));
        for (int i = 0; i < 3000; i++) {
            table.put(new Put(Bytes.toBytes(i)).addColumn(Bytes.toBytes("f1"), Bytes.toBytes("cq"), Bytes.toBytes(i)));
        }
        admin.snapshot(snapshotName1, tableName).get();
        List<SnapshotDescription> snapshots = syncAdmin.listSnapshots();
        Assert.assertEquals(1, snapshots.size());
        Assert.assertEquals(snapshotName1, snapshots.get(0).getName());
        Assert.assertEquals(tableName, snapshots.get(0).getTableName());
        Assert.assertEquals(FLUSH, snapshots.get(0).getType());
        // cloneSnapshot into a existed table.
        boolean failed = false;
        try {
            admin.cloneSnapshot(snapshotName1, tableName).get();
        } catch (Exception e) {
            failed = true;
        }
        Assert.assertTrue(failed);
        // cloneSnapshot into a new table.
        Assert.assertTrue((!(syncAdmin.tableExists(tableName2))));
        admin.cloneSnapshot(snapshotName1, tableName2).get();
        syncAdmin.tableExists(tableName2);
    }

    @Test
    public void testRestoreSnapshot() throws Exception {
        Table table = TestAsyncAdminBase.TEST_UTIL.createTable(tableName, Bytes.toBytes("f1"));
        for (int i = 0; i < 3000; i++) {
            table.put(new Put(Bytes.toBytes(i)).addColumn(Bytes.toBytes("f1"), Bytes.toBytes("cq"), Bytes.toBytes(i)));
        }
        Assert.assertEquals(0, admin.listSnapshots().get().size());
        admin.snapshot(snapshotName1, tableName).get();
        admin.snapshot(snapshotName2, tableName).get();
        Assert.assertEquals(2, admin.listSnapshots().get().size());
        admin.disableTable(tableName).get();
        admin.restoreSnapshot(snapshotName1, true).get();
        admin.enableTable(tableName).get();
        assertResult(tableName, 3000);
        admin.disableTable(tableName).get();
        admin.restoreSnapshot(snapshotName2, false).get();
        admin.enableTable(tableName).get();
        assertResult(tableName, 3000);
    }

    @Test
    public void testListSnapshots() throws Exception {
        Table table = TestAsyncAdminBase.TEST_UTIL.createTable(tableName, Bytes.toBytes("f1"));
        for (int i = 0; i < 3000; i++) {
            table.put(new Put(Bytes.toBytes(i)).addColumn(Bytes.toBytes("f1"), Bytes.toBytes("cq"), Bytes.toBytes(i)));
        }
        Assert.assertEquals(0, admin.listSnapshots().get().size());
        admin.snapshot(snapshotName1, tableName).get();
        admin.snapshot(snapshotName2, tableName).get();
        admin.snapshot(snapshotName3, tableName).get();
        Assert.assertEquals(3, admin.listSnapshots().get().size());
        Assert.assertEquals(3, admin.listSnapshots(Pattern.compile("(.*)")).get().size());
        Assert.assertEquals(3, admin.listSnapshots(Pattern.compile("snapshotName(\\d+)")).get().size());
        Assert.assertEquals(2, admin.listSnapshots(Pattern.compile("snapshotName[1|3]")).get().size());
        Assert.assertEquals(3, admin.listSnapshots(Pattern.compile("snapshot(.*)")).get().size());
        Assert.assertEquals(3, admin.listTableSnapshots(Pattern.compile("testListSnapshots"), Pattern.compile("s(.*)")).get().size());
        Assert.assertEquals(0, admin.listTableSnapshots(Pattern.compile("fakeTableName"), Pattern.compile("snap(.*)")).get().size());
        Assert.assertEquals(2, admin.listTableSnapshots(Pattern.compile("test(.*)"), Pattern.compile("snap(.*)[1|3]")).get().size());
    }

    @Test
    public void testDeleteSnapshots() throws Exception {
        Table table = TestAsyncAdminBase.TEST_UTIL.createTable(tableName, Bytes.toBytes("f1"));
        for (int i = 0; i < 3000; i++) {
            table.put(new Put(Bytes.toBytes(i)).addColumn(Bytes.toBytes("f1"), Bytes.toBytes("cq"), Bytes.toBytes(i)));
        }
        Assert.assertEquals(0, admin.listSnapshots().get().size());
        admin.snapshot(snapshotName1, tableName).get();
        admin.snapshot(snapshotName2, tableName).get();
        admin.snapshot(snapshotName3, tableName).get();
        Assert.assertEquals(3, admin.listSnapshots().get().size());
        admin.deleteSnapshot(snapshotName1).get();
        Assert.assertEquals(2, admin.listSnapshots().get().size());
        admin.deleteSnapshots(Pattern.compile("(.*)abc")).get();
        Assert.assertEquals(2, admin.listSnapshots().get().size());
        admin.deleteSnapshots(Pattern.compile("(.*)1")).get();
        Assert.assertEquals(2, admin.listSnapshots().get().size());
        admin.deleteTableSnapshots(Pattern.compile("(.*)"), Pattern.compile("(.*)1")).get();
        Assert.assertEquals(2, admin.listSnapshots().get().size());
        admin.deleteTableSnapshots(Pattern.compile("(.*)"), Pattern.compile("(.*)2")).get();
        Assert.assertEquals(1, admin.listSnapshots().get().size());
        admin.deleteTableSnapshots(Pattern.compile("(.*)"), Pattern.compile("(.*)3")).get();
        Assert.assertEquals(0, admin.listSnapshots().get().size());
    }
}

