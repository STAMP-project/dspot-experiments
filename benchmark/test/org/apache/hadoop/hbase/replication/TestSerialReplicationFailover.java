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


import HConstants.REPLICATION_SCOPE_GLOBAL;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.ColumnFamilyDescriptorBuilder;
import org.apache.hadoop.hbase.client.Table;
import org.apache.hadoop.hbase.client.TableDescriptorBuilder;
import org.apache.hadoop.hbase.testclassification.MediumTests;
import org.apache.hadoop.hbase.testclassification.ReplicationTests;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.hbase.util.JVMClusterUtil.RegionServerThread;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.experimental.categories.Category;


@Category({ ReplicationTests.class, MediumTests.class })
public class TestSerialReplicationFailover extends SerialReplicationTestBase {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestSerialReplicationFailover.class);

    @Test
    public void testKillRS() throws Exception {
        TableName tableName = TableName.valueOf(name.getMethodName());
        SerialReplicationTestBase.UTIL.getAdmin().createTable(TableDescriptorBuilder.newBuilder(tableName).setColumnFamily(ColumnFamilyDescriptorBuilder.newBuilder(SerialReplicationTestBase.CF).setScope(REPLICATION_SCOPE_GLOBAL).build()).build());
        SerialReplicationTestBase.UTIL.waitTableAvailable(tableName);
        try (Table table = SerialReplicationTestBase.UTIL.getConnection().getTable(tableName)) {
            for (int i = 0; i < 100; i++) {
                table.put(new org.apache.hadoop.hbase.client.Put(Bytes.toBytes(i)).addColumn(SerialReplicationTestBase.CF, SerialReplicationTestBase.CQ, Bytes.toBytes(i)));
            }
        }
        RegionServerThread thread = SerialReplicationTestBase.UTIL.getMiniHBaseCluster().getRegionServerThreads().stream().filter(( t) -> !(t.getRegionServer().getRegions(tableName).isEmpty())).findFirst().get();
        thread.getRegionServer().abort("for testing");
        thread.join();
        try (Table table = SerialReplicationTestBase.UTIL.getConnection().getTable(tableName)) {
            for (int i = 100; i < 200; i++) {
                table.put(new org.apache.hadoop.hbase.client.Put(Bytes.toBytes(i)).addColumn(SerialReplicationTestBase.CF, SerialReplicationTestBase.CQ, Bytes.toBytes(i)));
            }
        }
        enablePeerAndWaitUntilReplicationDone(200);
        checkOrder(200);
    }
}

