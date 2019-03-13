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


import TableState.State.DISABLED;
import TableState.State.DISABLING;
import TableState.State.ENABLED;
import TableState.State.ENABLING;
import java.io.IOException;
import java.util.Collections;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.RegionInfo;
import org.apache.hadoop.hbase.client.Table;
import org.apache.hadoop.hbase.master.TableStateManager;
import org.apache.hadoop.hbase.regionserver.HRegionServer;
import org.apache.hadoop.hbase.testclassification.MediumTests;
import org.apache.hadoop.hbase.testclassification.ReplicationTests;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hbase.thirdparty.com.google.common.collect.ImmutableMap;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.experimental.categories.Category;


/**
 * Testcase for HBASE-20147.
 */
@Category({ ReplicationTests.class, MediumTests.class })
public class TestAddToSerialReplicationPeer extends SerialReplicationTestBase {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestAddToSerialReplicationPeer.class);

    @Test
    public void testAddPeer() throws Exception {
        TableName tableName = createTable();
        try (Table table = SerialReplicationTestBase.UTIL.getConnection().getTable(tableName)) {
            for (int i = 0; i < 100; i++) {
                table.put(new org.apache.hadoop.hbase.client.Put(Bytes.toBytes(i)).addColumn(SerialReplicationTestBase.CF, SerialReplicationTestBase.CQ, Bytes.toBytes(i)));
            }
        }
        RegionInfo region = SerialReplicationTestBase.UTIL.getAdmin().getRegions(tableName).get(0);
        HRegionServer rs = SerialReplicationTestBase.UTIL.getOtherRegionServer(SerialReplicationTestBase.UTIL.getRSForFirstRegionInTable(tableName));
        moveRegionAndArchiveOldWals(region, rs);
        addPeer(true);
        try (Table table = SerialReplicationTestBase.UTIL.getConnection().getTable(tableName)) {
            for (int i = 0; i < 100; i++) {
                table.put(new org.apache.hadoop.hbase.client.Put(Bytes.toBytes(i)).addColumn(SerialReplicationTestBase.CF, SerialReplicationTestBase.CQ, Bytes.toBytes(i)));
            }
        }
        waitUntilReplicationDone(100);
        checkOrder(100);
    }

    @Test
    public void testChangeToSerial() throws Exception {
        ReplicationPeerConfig peerConfig = ReplicationPeerConfig.newBuilder().setClusterKey("127.0.0.1:2181:/hbase").setReplicationEndpointImpl(SerialReplicationTestBase.LocalReplicationEndpoint.class.getName()).build();
        SerialReplicationTestBase.UTIL.getAdmin().addReplicationPeer(SerialReplicationTestBase.PEER_ID, peerConfig, true);
        TableName tableName = createTable();
        try (Table table = SerialReplicationTestBase.UTIL.getConnection().getTable(tableName)) {
            for (int i = 0; i < 100; i++) {
                table.put(new org.apache.hadoop.hbase.client.Put(Bytes.toBytes(i)).addColumn(SerialReplicationTestBase.CF, SerialReplicationTestBase.CQ, Bytes.toBytes(i)));
            }
        }
        RegionInfo region = SerialReplicationTestBase.UTIL.getAdmin().getRegions(tableName).get(0);
        HRegionServer srcRs = SerialReplicationTestBase.UTIL.getRSForFirstRegionInTable(tableName);
        HRegionServer rs = SerialReplicationTestBase.UTIL.getOtherRegionServer(srcRs);
        moveRegionAndArchiveOldWals(region, rs);
        waitUntilReplicationDone(100);
        waitUntilReplicatedToTheCurrentWALFile(srcRs);
        SerialReplicationTestBase.UTIL.getAdmin().disableReplicationPeer(SerialReplicationTestBase.PEER_ID);
        SerialReplicationTestBase.UTIL.getAdmin().updateReplicationPeerConfig(SerialReplicationTestBase.PEER_ID, ReplicationPeerConfig.newBuilder(peerConfig).setSerial(true).build());
        SerialReplicationTestBase.UTIL.getAdmin().enableReplicationPeer(SerialReplicationTestBase.PEER_ID);
        try (Table table = SerialReplicationTestBase.UTIL.getConnection().getTable(tableName)) {
            for (int i = 0; i < 100; i++) {
                table.put(new org.apache.hadoop.hbase.client.Put(Bytes.toBytes(i)).addColumn(SerialReplicationTestBase.CF, SerialReplicationTestBase.CQ, Bytes.toBytes(i)));
            }
        }
        waitUntilReplicationDone(200);
        checkOrder(200);
    }

    @Test
    public void testAddToSerialPeer() throws Exception {
        ReplicationPeerConfig peerConfig = ReplicationPeerConfig.newBuilder().setClusterKey("127.0.0.1:2181:/hbase").setReplicationEndpointImpl(SerialReplicationTestBase.LocalReplicationEndpoint.class.getName()).setReplicateAllUserTables(false).setSerial(true).build();
        SerialReplicationTestBase.UTIL.getAdmin().addReplicationPeer(SerialReplicationTestBase.PEER_ID, peerConfig, true);
        TableName tableName = createTable();
        try (Table table = SerialReplicationTestBase.UTIL.getConnection().getTable(tableName)) {
            for (int i = 0; i < 100; i++) {
                table.put(new org.apache.hadoop.hbase.client.Put(Bytes.toBytes(i)).addColumn(SerialReplicationTestBase.CF, SerialReplicationTestBase.CQ, Bytes.toBytes(i)));
            }
        }
        RegionInfo region = SerialReplicationTestBase.UTIL.getAdmin().getRegions(tableName).get(0);
        HRegionServer srcRs = SerialReplicationTestBase.UTIL.getRSForFirstRegionInTable(tableName);
        HRegionServer rs = SerialReplicationTestBase.UTIL.getOtherRegionServer(srcRs);
        moveRegionAndArchiveOldWals(region, rs);
        waitUntilReplicatedToTheCurrentWALFile(rs);
        SerialReplicationTestBase.UTIL.getAdmin().disableReplicationPeer(SerialReplicationTestBase.PEER_ID);
        SerialReplicationTestBase.UTIL.getAdmin().updateReplicationPeerConfig(SerialReplicationTestBase.PEER_ID, ReplicationPeerConfig.newBuilder(peerConfig).setTableCFsMap(ImmutableMap.of(tableName, Collections.emptyList())).build());
        SerialReplicationTestBase.UTIL.getAdmin().enableReplicationPeer(SerialReplicationTestBase.PEER_ID);
        try (Table table = SerialReplicationTestBase.UTIL.getConnection().getTable(tableName)) {
            for (int i = 0; i < 100; i++) {
                table.put(new org.apache.hadoop.hbase.client.Put(Bytes.toBytes(i)).addColumn(SerialReplicationTestBase.CF, SerialReplicationTestBase.CQ, Bytes.toBytes(i)));
            }
        }
        waitUntilReplicationDone(100);
        checkOrder(100);
    }

    @Test
    public void testDisabledTable() throws Exception {
        TableName tableName = createTable();
        try (Table table = SerialReplicationTestBase.UTIL.getConnection().getTable(tableName)) {
            for (int i = 0; i < 100; i++) {
                table.put(new org.apache.hadoop.hbase.client.Put(Bytes.toBytes(i)).addColumn(SerialReplicationTestBase.CF, SerialReplicationTestBase.CQ, Bytes.toBytes(i)));
            }
        }
        SerialReplicationTestBase.UTIL.getAdmin().disableTable(tableName);
        SerialReplicationTestBase.rollAllWALs();
        addPeer(true);
        SerialReplicationTestBase.UTIL.getAdmin().enableTable(tableName);
        try (Table table = SerialReplicationTestBase.UTIL.getConnection().getTable(tableName)) {
            for (int i = 0; i < 100; i++) {
                table.put(new org.apache.hadoop.hbase.client.Put(Bytes.toBytes(i)).addColumn(SerialReplicationTestBase.CF, SerialReplicationTestBase.CQ, Bytes.toBytes(i)));
            }
        }
        waitUntilReplicationDone(100);
        checkOrder(100);
    }

    @Test
    public void testDisablingTable() throws Exception {
        TableName tableName = createTable();
        try (Table table = SerialReplicationTestBase.UTIL.getConnection().getTable(tableName)) {
            for (int i = 0; i < 100; i++) {
                table.put(new org.apache.hadoop.hbase.client.Put(Bytes.toBytes(i)).addColumn(SerialReplicationTestBase.CF, SerialReplicationTestBase.CQ, Bytes.toBytes(i)));
            }
        }
        SerialReplicationTestBase.UTIL.getAdmin().disableTable(tableName);
        SerialReplicationTestBase.rollAllWALs();
        TableStateManager tsm = SerialReplicationTestBase.UTIL.getMiniHBaseCluster().getMaster().getTableStateManager();
        tsm.setTableState(tableName, DISABLING);
        Thread t = new Thread(() -> {
            try {
                addPeer(true);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        t.start();
        Thread.sleep(5000);
        // we will wait on the disabling table so the thread should still be alive.
        Assert.assertTrue(t.isAlive());
        tsm.setTableState(tableName, DISABLED);
        t.join();
        SerialReplicationTestBase.UTIL.getAdmin().enableTable(tableName);
        try (Table table = SerialReplicationTestBase.UTIL.getConnection().getTable(tableName)) {
            for (int i = 0; i < 100; i++) {
                table.put(new org.apache.hadoop.hbase.client.Put(Bytes.toBytes(i)).addColumn(SerialReplicationTestBase.CF, SerialReplicationTestBase.CQ, Bytes.toBytes(i)));
            }
        }
        waitUntilReplicationDone(100);
        checkOrder(100);
    }

    @Test
    public void testEnablingTable() throws Exception {
        TableName tableName = createTable();
        try (Table table = SerialReplicationTestBase.UTIL.getConnection().getTable(tableName)) {
            for (int i = 0; i < 100; i++) {
                table.put(new org.apache.hadoop.hbase.client.Put(Bytes.toBytes(i)).addColumn(SerialReplicationTestBase.CF, SerialReplicationTestBase.CQ, Bytes.toBytes(i)));
            }
        }
        RegionInfo region = SerialReplicationTestBase.UTIL.getAdmin().getRegions(tableName).get(0);
        HRegionServer rs = SerialReplicationTestBase.UTIL.getOtherRegionServer(SerialReplicationTestBase.UTIL.getRSForFirstRegionInTable(tableName));
        moveRegionAndArchiveOldWals(region, rs);
        TableStateManager tsm = SerialReplicationTestBase.UTIL.getMiniHBaseCluster().getMaster().getTableStateManager();
        tsm.setTableState(tableName, ENABLING);
        Thread t = new Thread(() -> {
            try {
                addPeer(true);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        t.start();
        Thread.sleep(5000);
        // we will wait on the disabling table so the thread should still be alive.
        Assert.assertTrue(t.isAlive());
        tsm.setTableState(tableName, ENABLED);
        t.join();
        try (Table table = SerialReplicationTestBase.UTIL.getConnection().getTable(tableName)) {
            for (int i = 0; i < 100; i++) {
                table.put(new org.apache.hadoop.hbase.client.Put(Bytes.toBytes(i)).addColumn(SerialReplicationTestBase.CF, SerialReplicationTestBase.CQ, Bytes.toBytes(i)));
            }
        }
        waitUntilReplicationDone(100);
        checkOrder(100);
    }
}

