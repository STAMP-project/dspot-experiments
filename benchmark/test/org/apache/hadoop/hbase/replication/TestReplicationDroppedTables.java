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
package org.apache.hadoop.hbase.replication;


import HConstants.REPLICATION_DROP_ON_DELETED_TABLE_KEY;
import HConstants.REPLICATION_SCOPE_GLOBAL;
import HConstants.REPLICATION_SOURCE_MAXTHREADS_KEY;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.NamespaceDescriptor;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Admin;
import org.apache.hadoop.hbase.client.ColumnFamilyDescriptorBuilder;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.ConnectionFactory;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Table;
import org.apache.hadoop.hbase.client.TableDescriptor;
import org.apache.hadoop.hbase.client.TableDescriptorBuilder;
import org.apache.hadoop.hbase.testclassification.LargeTests;
import org.apache.hadoop.hbase.testclassification.ReplicationTests;
import org.apache.hadoop.hbase.util.Bytes;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Category({ ReplicationTests.class, LargeTests.class })
public class TestReplicationDroppedTables extends TestReplicationBase {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestReplicationDroppedTables.class);

    private static final Logger LOG = LoggerFactory.getLogger(TestReplicationDroppedTables.class);

    private static final int ROWS_COUNT = 1000;

    @Test
    public void testEditsStuckBehindDroppedTable() throws Exception {
        // Sanity check Make sure by default edits for dropped tables stall the replication queue, even
        // when the table(s) in question have been deleted on both ends.
        testEditsBehindDroppedTable(false, "test_dropped");
    }

    @Test
    public void testEditsDroppedWithDroppedTable() throws Exception {
        // Make sure by default edits for dropped tables are themselves dropped when the
        // table(s) in question have been deleted on both ends.
        testEditsBehindDroppedTable(true, "test_dropped");
    }

    @Test
    public void testEditsDroppedWithDroppedTableNS() throws Exception {
        // also try with a namespace
        Connection connection1 = ConnectionFactory.createConnection(TestReplicationBase.conf1);
        try (Admin admin1 = connection1.getAdmin()) {
            admin1.createNamespace(NamespaceDescriptor.create("NS").build());
        }
        Connection connection2 = ConnectionFactory.createConnection(TestReplicationBase.conf2);
        try (Admin admin2 = connection2.getAdmin()) {
            admin2.createNamespace(NamespaceDescriptor.create("NS").build());
        }
        testEditsBehindDroppedTable(true, "NS:test_dropped");
        try (Admin admin1 = connection1.getAdmin()) {
            admin1.deleteNamespace("NS");
        }
        try (Admin admin2 = connection2.getAdmin()) {
            admin2.deleteNamespace("NS");
        }
    }

    @Test
    public void testEditsBehindDroppedTableTiming() throws Exception {
        TestReplicationBase.conf1.setBoolean(REPLICATION_DROP_ON_DELETED_TABLE_KEY, true);
        TestReplicationBase.conf1.setInt(REPLICATION_SOURCE_MAXTHREADS_KEY, 1);
        // make sure we have a single region server only, so that all
        // edits for all tables go there
        TestReplicationBase.utility1.shutdownMiniHBaseCluster();
        TestReplicationBase.utility1.startMiniHBaseCluster();
        TableName tablename = TableName.valueOf("testdroppedtimed");
        byte[] familyName = Bytes.toBytes("fam");
        byte[] row = Bytes.toBytes("row");
        TableDescriptor table = TableDescriptorBuilder.newBuilder(tablename).setColumnFamily(ColumnFamilyDescriptorBuilder.newBuilder(familyName).setScope(REPLICATION_SCOPE_GLOBAL).build()).build();
        Connection connection1 = ConnectionFactory.createConnection(TestReplicationBase.conf1);
        Connection connection2 = ConnectionFactory.createConnection(TestReplicationBase.conf2);
        try (Admin admin1 = connection1.getAdmin()) {
            admin1.createTable(table);
        }
        try (Admin admin2 = connection2.getAdmin()) {
            admin2.createTable(table);
        }
        TestReplicationBase.utility1.waitUntilAllRegionsAssigned(tablename);
        TestReplicationBase.utility2.waitUntilAllRegionsAssigned(tablename);
        // now suspend replication
        try (Admin admin1 = connection1.getAdmin()) {
            admin1.disableReplicationPeer(TestReplicationBase.PEER_ID2);
        }
        // put some data (lead with 0 so the edit gets sorted before the other table's edits
        // in the replication batch) write a bunch of edits, making sure we fill a batch
        try (Table droppedTable = connection1.getTable(tablename)) {
            byte[] rowKey = Bytes.toBytes((0 + " put on table to be dropped"));
            Put put = new Put(rowKey);
            put.addColumn(familyName, row, row);
            droppedTable.put(put);
        }
        try (Table table1 = connection1.getTable(TestReplicationBase.tableName)) {
            for (int i = 0; i < (TestReplicationDroppedTables.ROWS_COUNT); i++) {
                Put put = new Put(generateRowKey(i)).addColumn(TestReplicationBase.famName, row, row);
                table1.put(put);
            }
        }
        try (Admin admin2 = connection2.getAdmin()) {
            admin2.disableTable(tablename);
            admin2.deleteTable(tablename);
        }
        // edit should still be stuck
        try (Admin admin1 = connection1.getAdmin()) {
            // enable the replication peer.
            admin1.enableReplicationPeer(TestReplicationBase.PEER_ID2);
            // the source table still exists, replication should be stalled
            verifyReplicationStuck();
            admin1.disableTable(tablename);
            // still stuck, source table still exists
            verifyReplicationStuck();
            admin1.deleteTable(tablename);
            // now the source table is gone, replication should proceed, the
            // offending edits be dropped
            verifyReplicationProceeded();
        }
        // just to be safe
        TestReplicationBase.conf1.setBoolean(REPLICATION_DROP_ON_DELETED_TABLE_KEY, false);
    }
}

