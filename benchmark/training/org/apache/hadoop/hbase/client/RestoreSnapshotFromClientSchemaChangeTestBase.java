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


import java.util.Set;
import org.apache.hadoop.hbase.regionserver.NoSuchColumnFamilyException;
import org.apache.hadoop.hbase.snapshot.SnapshotTestingUtils;
import org.apache.hadoop.hbase.util.Bytes;
import org.junit.Assert;
import org.junit.Test;


public class RestoreSnapshotFromClientSchemaChangeTestBase extends RestoreSnapshotFromClientTestBase {
    @Test
    public void testRestoreSchemaChange() throws Exception {
        Table table = RestoreSnapshotFromClientTestBase.TEST_UTIL.getConnection().getTable(tableName);
        // Add one column family and put some data in it
        admin.disableTable(tableName);
        admin.addColumnFamily(tableName, getTestRestoreSchemaChangeHCD());
        admin.enableTable(tableName);
        Assert.assertEquals(2, table.getDescriptor().getColumnFamilyCount());
        TableDescriptor htd = admin.getDescriptor(tableName);
        Assert.assertEquals(2, htd.getColumnFamilyCount());
        SnapshotTestingUtils.loadData(RestoreSnapshotFromClientTestBase.TEST_UTIL, tableName, 500, TEST_FAMILY2);
        long snapshot2Rows = (snapshot1Rows) + 500L;
        Assert.assertEquals(snapshot2Rows, countRows(table));
        Assert.assertEquals(500, countRows(table, TEST_FAMILY2));
        Set<String> fsFamilies = getFamiliesFromFS(tableName);
        Assert.assertEquals(2, fsFamilies.size());
        // Take a snapshot
        admin.disableTable(tableName);
        admin.snapshot(snapshotName2, tableName);
        // Restore the snapshot (without the cf)
        admin.restoreSnapshot(snapshotName0);
        admin.enableTable(tableName);
        Assert.assertEquals(1, table.getDescriptor().getColumnFamilyCount());
        try {
            countRows(table, TEST_FAMILY2);
            Assert.fail((("family '" + (Bytes.toString(TEST_FAMILY2))) + "' should not exists"));
        } catch (NoSuchColumnFamilyException e) {
            // expected
        }
        Assert.assertEquals(snapshot0Rows, countRows(table));
        htd = admin.getDescriptor(tableName);
        Assert.assertEquals(1, htd.getColumnFamilyCount());
        fsFamilies = getFamiliesFromFS(tableName);
        Assert.assertEquals(1, fsFamilies.size());
        // Restore back the snapshot (with the cf)
        admin.disableTable(tableName);
        admin.restoreSnapshot(snapshotName2);
        admin.enableTable(tableName);
        htd = admin.getDescriptor(tableName);
        Assert.assertEquals(2, htd.getColumnFamilyCount());
        Assert.assertEquals(2, table.getDescriptor().getColumnFamilyCount());
        Assert.assertEquals(500, countRows(table, TEST_FAMILY2));
        Assert.assertEquals(snapshot2Rows, countRows(table));
        fsFamilies = getFamiliesFromFS(tableName);
        Assert.assertEquals(2, fsFamilies.size());
        table.close();
    }
}

