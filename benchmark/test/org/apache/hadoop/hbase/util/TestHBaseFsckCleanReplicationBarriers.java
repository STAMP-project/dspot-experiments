/**
 * Licensed to the Apache Software Foundation (ASF) under one or more contributor license
 * agreements. See the NOTICE file distributed with this work for additional information regarding
 * copyright ownership. The ASF licenses this file to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance with the License. You may obtain a
 * copy of the License at http://www.apache.org/licenses/LICENSE-2.0 Unless required by applicable
 * law or agreed to in writing, software distributed under the License is distributed on an "AS IS"
 * BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License
 * for the specific language governing permissions and limitations under the License.
 */
package org.apache.hadoop.hbase.util;


import HConstants.REPLICATION_BARRIER_FAMILY;
import HConstants.REPLICATION_SCOPE_LOCAL;
import MetaTableAccessor.QueryType.REGION;
import RegionState.State.OFFLINE;
import RegionState.State.OPEN;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.HBaseTestingUtility;
import org.apache.hadoop.hbase.MetaTableAccessor;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.ColumnFamilyDescriptorBuilder;
import org.apache.hadoop.hbase.client.RegionInfo;
import org.apache.hadoop.hbase.client.RegionInfoBuilder;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.ResultScanner;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.client.TableDescriptor;
import org.apache.hadoop.hbase.client.TableDescriptorBuilder;
import org.apache.hadoop.hbase.replication.ReplicationQueueStorage;
import org.apache.hadoop.hbase.testclassification.MediumTests;
import org.apache.hadoop.hbase.testclassification.ReplicationTests;
import org.apache.hadoop.hbase.util.hbck.HbckTestingUtil;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.experimental.categories.Category;


@Category({ ReplicationTests.class, MediumTests.class })
public class TestHBaseFsckCleanReplicationBarriers {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestHBaseFsckCleanReplicationBarriers.class);

    private static final HBaseTestingUtility UTIL = new HBaseTestingUtility();

    private static String PEER_1 = "1";

    private static String PEER_2 = "2";

    private static ReplicationQueueStorage QUEUE_STORAGE;

    private static String WAL_FILE_NAME = "test.wal";

    private static String TABLE_NAME = "test";

    private static String COLUMN_FAMILY = "info";

    @Test
    public void testCleanReplicationBarrierWithNonExistTable() throws IOException, ClassNotFoundException {
        TableName tableName = TableName.valueOf(((TestHBaseFsckCleanReplicationBarriers.TABLE_NAME) + "_non"));
        boolean cleaned = HbckTestingUtil.cleanReplicationBarrier(TestHBaseFsckCleanReplicationBarriers.UTIL.getConfiguration(), tableName);
        Assert.assertFalse(cleaned);
    }

    @Test
    public void testCleanReplicationBarrierWithDeletedTable() throws Exception {
        TableName tableName = TableName.valueOf(((TestHBaseFsckCleanReplicationBarriers.TABLE_NAME) + "_deleted"));
        List<RegionInfo> regionInfos = new ArrayList<>();
        // only write some barriers into meta table
        for (int i = 0; i < 110; i++) {
            RegionInfo regionInfo = RegionInfoBuilder.newBuilder(tableName).setStartKey(Bytes.toBytes(i)).setEndKey(Bytes.toBytes((i + 1))).build();
            regionInfos.add(regionInfo);
            addStateAndBarrier(regionInfo, OPEN, 10, 100);
            updatePushedSeqId(regionInfo, 10);
            Assert.assertEquals("check if there is lastPushedId", 10, TestHBaseFsckCleanReplicationBarriers.QUEUE_STORAGE.getLastSequenceId(regionInfo.getEncodedName(), TestHBaseFsckCleanReplicationBarriers.PEER_1));
            Assert.assertEquals("check if there is lastPushedId", 10, TestHBaseFsckCleanReplicationBarriers.QUEUE_STORAGE.getLastSequenceId(regionInfo.getEncodedName(), TestHBaseFsckCleanReplicationBarriers.PEER_2));
        }
        Scan barrierScan = new Scan();
        barrierScan.setCaching(100);
        barrierScan.addFamily(REPLICATION_BARRIER_FAMILY);
        barrierScan.withStartRow(MetaTableAccessor.getTableStartRowForMeta(tableName, REGION)).withStopRow(MetaTableAccessor.getTableStopRowForMeta(tableName, REGION));
        Result result;
        try (ResultScanner scanner = MetaTableAccessor.getMetaHTable(TestHBaseFsckCleanReplicationBarriers.UTIL.getConnection()).getScanner(barrierScan)) {
            while ((result = scanner.next()) != null) {
                Assert.assertTrue(((MetaTableAccessor.getReplicationBarriers(result).length) > 0));
            } 
        }
        boolean cleaned = HbckTestingUtil.cleanReplicationBarrier(TestHBaseFsckCleanReplicationBarriers.UTIL.getConfiguration(), tableName);
        Assert.assertTrue(cleaned);
        for (RegionInfo regionInfo : regionInfos) {
            Assert.assertEquals("check if there is lastPushedId", (-1), TestHBaseFsckCleanReplicationBarriers.QUEUE_STORAGE.getLastSequenceId(regionInfo.getEncodedName(), TestHBaseFsckCleanReplicationBarriers.PEER_1));
            Assert.assertEquals("check if there is lastPushedId", (-1), TestHBaseFsckCleanReplicationBarriers.QUEUE_STORAGE.getLastSequenceId(regionInfo.getEncodedName(), TestHBaseFsckCleanReplicationBarriers.PEER_2));
        }
        cleaned = HbckTestingUtil.cleanReplicationBarrier(TestHBaseFsckCleanReplicationBarriers.UTIL.getConfiguration(), tableName);
        Assert.assertFalse(cleaned);
        for (RegionInfo region : regionInfos) {
            Assert.assertEquals(0, MetaTableAccessor.getReplicationBarrier(TestHBaseFsckCleanReplicationBarriers.UTIL.getConnection(), region.getRegionName()).length);
        }
    }

    @Test
    public void testCleanReplicationBarrierWithExistTable() throws Exception {
        TableName tableName = TableName.valueOf(TestHBaseFsckCleanReplicationBarriers.TABLE_NAME);
        String cf = TestHBaseFsckCleanReplicationBarriers.COLUMN_FAMILY;
        TableDescriptor tableDescriptor = TableDescriptorBuilder.newBuilder(tableName).setColumnFamily(ColumnFamilyDescriptorBuilder.newBuilder(Bytes.toBytes(cf)).build()).setReplicationScope(REPLICATION_SCOPE_LOCAL).build();
        TestHBaseFsckCleanReplicationBarriers.UTIL.createTable(tableDescriptor, Bytes.split(Bytes.toBytes(1), Bytes.toBytes(256), 123));
        Assert.assertTrue(((TestHBaseFsckCleanReplicationBarriers.UTIL.getAdmin().getRegions(tableName).size()) > 0));
        for (RegionInfo region : TestHBaseFsckCleanReplicationBarriers.UTIL.getAdmin().getRegions(tableName)) {
            addStateAndBarrier(region, OFFLINE, 10, 100);
            updatePushedSeqId(region, 10);
            Assert.assertEquals("check if there is lastPushedId", 10, TestHBaseFsckCleanReplicationBarriers.QUEUE_STORAGE.getLastSequenceId(region.getEncodedName(), TestHBaseFsckCleanReplicationBarriers.PEER_1));
            Assert.assertEquals("check if there is lastPushedId", 10, TestHBaseFsckCleanReplicationBarriers.QUEUE_STORAGE.getLastSequenceId(region.getEncodedName(), TestHBaseFsckCleanReplicationBarriers.PEER_2));
        }
        boolean cleaned = HbckTestingUtil.cleanReplicationBarrier(TestHBaseFsckCleanReplicationBarriers.UTIL.getConfiguration(), tableName);
        Assert.assertTrue(cleaned);
        for (RegionInfo region : TestHBaseFsckCleanReplicationBarriers.UTIL.getAdmin().getRegions(tableName)) {
            Assert.assertEquals("check if there is lastPushedId", (-1), TestHBaseFsckCleanReplicationBarriers.QUEUE_STORAGE.getLastSequenceId(region.getEncodedName(), TestHBaseFsckCleanReplicationBarriers.PEER_1));
            Assert.assertEquals("check if there is lastPushedId", (-1), TestHBaseFsckCleanReplicationBarriers.QUEUE_STORAGE.getLastSequenceId(region.getEncodedName(), TestHBaseFsckCleanReplicationBarriers.PEER_2));
        }
        cleaned = HbckTestingUtil.cleanReplicationBarrier(TestHBaseFsckCleanReplicationBarriers.UTIL.getConfiguration(), tableName);
        Assert.assertFalse(cleaned);
        for (RegionInfo region : TestHBaseFsckCleanReplicationBarriers.UTIL.getAdmin().getRegions(tableName)) {
            Assert.assertEquals(0, MetaTableAccessor.getReplicationBarrier(TestHBaseFsckCleanReplicationBarriers.UTIL.getConnection(), region.getRegionName()).length);
        }
    }
}

