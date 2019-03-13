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


import HConstants.NO_SEQNUM;
import HConstants.REPLICATION_SCOPE_GLOBAL;
import WAL.Reader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.ColumnFamilyDescriptorBuilder;
import org.apache.hadoop.hbase.client.RegionInfo;
import org.apache.hadoop.hbase.client.Table;
import org.apache.hadoop.hbase.client.TableDescriptorBuilder;
import org.apache.hadoop.hbase.regionserver.HRegionServer;
import org.apache.hadoop.hbase.testclassification.MediumTests;
import org.apache.hadoop.hbase.testclassification.ReplicationTests;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.hbase.wal.WAL;
import org.apache.hadoop.hbase.wal.WAL.Entry;
import org.apache.hadoop.hbase.wal.WALFactory;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.experimental.categories.Category;


@Category({ ReplicationTests.class, MediumTests.class })
public class TestSerialReplication extends SerialReplicationTestBase {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestSerialReplication.class);

    @Test
    public void testRegionMove() throws Exception {
        TableName tableName = createTable();
        try (Table table = SerialReplicationTestBase.UTIL.getConnection().getTable(tableName)) {
            for (int i = 0; i < 100; i++) {
                table.put(new org.apache.hadoop.hbase.client.Put(Bytes.toBytes(i)).addColumn(SerialReplicationTestBase.CF, SerialReplicationTestBase.CQ, Bytes.toBytes(i)));
            }
        }
        RegionInfo region = SerialReplicationTestBase.UTIL.getAdmin().getRegions(tableName).get(0);
        HRegionServer rs = SerialReplicationTestBase.UTIL.getOtherRegionServer(SerialReplicationTestBase.UTIL.getRSForFirstRegionInTable(tableName));
        SerialReplicationTestBase.moveRegion(region, rs);
        try (Table table = SerialReplicationTestBase.UTIL.getConnection().getTable(tableName)) {
            for (int i = 100; i < 200; i++) {
                table.put(new org.apache.hadoop.hbase.client.Put(Bytes.toBytes(i)).addColumn(SerialReplicationTestBase.CF, SerialReplicationTestBase.CQ, Bytes.toBytes(i)));
            }
        }
        enablePeerAndWaitUntilReplicationDone(200);
        checkOrder(200);
    }

    @Test
    public void testRegionSplit() throws Exception {
        TableName tableName = createTable();
        try (Table table = SerialReplicationTestBase.UTIL.getConnection().getTable(tableName)) {
            for (int i = 0; i < 100; i++) {
                table.put(new org.apache.hadoop.hbase.client.Put(Bytes.toBytes(i)).addColumn(SerialReplicationTestBase.CF, SerialReplicationTestBase.CQ, Bytes.toBytes(i)));
            }
        }
        SerialReplicationTestBase.UTIL.flush(tableName);
        RegionInfo region = SerialReplicationTestBase.UTIL.getAdmin().getRegions(tableName).get(0);
        SerialReplicationTestBase.UTIL.getAdmin().splitRegionAsync(region.getEncodedNameAsBytes(), Bytes.toBytes(50)).get(30, TimeUnit.SECONDS);
        SerialReplicationTestBase.UTIL.waitUntilNoRegionsInTransition(30000);
        List<RegionInfo> regions = SerialReplicationTestBase.UTIL.getAdmin().getRegions(tableName);
        Assert.assertEquals(2, regions.size());
        try (Table table = SerialReplicationTestBase.UTIL.getConnection().getTable(tableName)) {
            for (int i = 0; i < 100; i++) {
                table.put(new org.apache.hadoop.hbase.client.Put(Bytes.toBytes(i)).addColumn(SerialReplicationTestBase.CF, SerialReplicationTestBase.CQ, Bytes.toBytes(i)));
            }
        }
        enablePeerAndWaitUntilReplicationDone(200);
        Map<String, Long> regionsToSeqId = new HashMap<>();
        regionsToSeqId.put(region.getEncodedName(), (-1L));
        regions.stream().map(RegionInfo::getEncodedName).forEach(( n) -> regionsToSeqId.put(n, (-1L)));
        try (WAL.Reader reader = WALFactory.createReader(SerialReplicationTestBase.UTIL.getTestFileSystem(), logPath, SerialReplicationTestBase.UTIL.getConfiguration())) {
            int count = 0;
            for (Entry entry; ;) {
                entry = reader.next();
                if (entry == null) {
                    break;
                }
                String encodedName = Bytes.toString(entry.getKey().getEncodedRegionName());
                Long seqId = regionsToSeqId.get(encodedName);
                Assert.assertNotNull(((((("Unexcepted entry " + entry) + ", expected regions ") + region) + ", or ") + regions), seqId);
                Assert.assertTrue(((((("Sequence id go backwards from " + seqId) + " to ") + (entry.getKey().getSequenceId())) + " for ") + encodedName), ((entry.getKey().getSequenceId()) >= (seqId.longValue())));
                if (count < 100) {
                    Assert.assertEquals(((encodedName + " is pushed before parent ") + (region.getEncodedName())), region.getEncodedName(), encodedName);
                } else {
                    Assert.assertNotEquals(region.getEncodedName(), encodedName);
                }
                count++;
            }
            Assert.assertEquals(200, count);
        }
    }

    @Test
    public void testRegionMerge() throws Exception {
        byte[] splitKey = Bytes.toBytes(50);
        TableName tableName = TableName.valueOf(name.getMethodName());
        SerialReplicationTestBase.UTIL.getAdmin().createTable(TableDescriptorBuilder.newBuilder(tableName).setColumnFamily(ColumnFamilyDescriptorBuilder.newBuilder(SerialReplicationTestBase.CF).setScope(REPLICATION_SCOPE_GLOBAL).build()).build(), new byte[][]{ splitKey });
        SerialReplicationTestBase.UTIL.waitTableAvailable(tableName);
        try (Table table = SerialReplicationTestBase.UTIL.getConnection().getTable(tableName)) {
            for (int i = 0; i < 100; i++) {
                table.put(new org.apache.hadoop.hbase.client.Put(Bytes.toBytes(i)).addColumn(SerialReplicationTestBase.CF, SerialReplicationTestBase.CQ, Bytes.toBytes(i)));
            }
        }
        List<RegionInfo> regions = SerialReplicationTestBase.UTIL.getAdmin().getRegions(tableName);
        SerialReplicationTestBase.UTIL.getAdmin().mergeRegionsAsync(regions.stream().map(RegionInfo::getEncodedNameAsBytes).toArray(byte[][]::new), false).get(30, TimeUnit.SECONDS);
        SerialReplicationTestBase.UTIL.waitUntilNoRegionsInTransition(30000);
        List<RegionInfo> regionsAfterMerge = SerialReplicationTestBase.UTIL.getAdmin().getRegions(tableName);
        Assert.assertEquals(1, regionsAfterMerge.size());
        try (Table table = SerialReplicationTestBase.UTIL.getConnection().getTable(tableName)) {
            for (int i = 0; i < 100; i++) {
                table.put(new org.apache.hadoop.hbase.client.Put(Bytes.toBytes(i)).addColumn(SerialReplicationTestBase.CF, SerialReplicationTestBase.CQ, Bytes.toBytes(i)));
            }
        }
        enablePeerAndWaitUntilReplicationDone(200);
        Map<String, Long> regionsToSeqId = new HashMap<>();
        RegionInfo region = regionsAfterMerge.get(0);
        regionsToSeqId.put(region.getEncodedName(), (-1L));
        regions.stream().map(RegionInfo::getEncodedName).forEach(( n) -> regionsToSeqId.put(n, (-1L)));
        try (WAL.Reader reader = WALFactory.createReader(SerialReplicationTestBase.UTIL.getTestFileSystem(), logPath, SerialReplicationTestBase.UTIL.getConfiguration())) {
            int count = 0;
            for (Entry entry; ;) {
                entry = reader.next();
                if (entry == null) {
                    break;
                }
                String encodedName = Bytes.toString(entry.getKey().getEncodedRegionName());
                Long seqId = regionsToSeqId.get(encodedName);
                Assert.assertNotNull(((((("Unexcepted entry " + entry) + ", expected regions ") + region) + ", or ") + regions), seqId);
                Assert.assertTrue(((((("Sequence id go backwards from " + seqId) + " to ") + (entry.getKey().getSequenceId())) + " for ") + encodedName), ((entry.getKey().getSequenceId()) >= (seqId.longValue())));
                if (count < 100) {
                    Assert.assertNotEquals(((encodedName + " is pushed before parents ") + (regions.stream().map(RegionInfo::getEncodedName).collect(Collectors.joining(" and ")))), region.getEncodedName(), encodedName);
                } else {
                    Assert.assertEquals(region.getEncodedName(), encodedName);
                }
                count++;
            }
            Assert.assertEquals(200, count);
        }
    }

    @Test
    public void testRemovePeerNothingReplicated() throws Exception {
        TableName tableName = createTable();
        String encodedRegionName = SerialReplicationTestBase.UTIL.getMiniHBaseCluster().getRegions(tableName).get(0).getRegionInfo().getEncodedName();
        ReplicationQueueStorage queueStorage = SerialReplicationTestBase.UTIL.getMiniHBaseCluster().getMaster().getReplicationPeerManager().getQueueStorage();
        Assert.assertEquals(NO_SEQNUM, queueStorage.getLastSequenceId(encodedRegionName, SerialReplicationTestBase.PEER_ID));
        SerialReplicationTestBase.UTIL.getAdmin().removeReplicationPeer(SerialReplicationTestBase.PEER_ID);
        Assert.assertEquals(NO_SEQNUM, queueStorage.getLastSequenceId(encodedRegionName, SerialReplicationTestBase.PEER_ID));
    }

    @Test
    public void testRemovePeer() throws Exception {
        TableName tableName = createTable();
        try (Table table = SerialReplicationTestBase.UTIL.getConnection().getTable(tableName)) {
            for (int i = 0; i < 100; i++) {
                table.put(new org.apache.hadoop.hbase.client.Put(Bytes.toBytes(i)).addColumn(SerialReplicationTestBase.CF, SerialReplicationTestBase.CQ, Bytes.toBytes(i)));
            }
        }
        enablePeerAndWaitUntilReplicationDone(100);
        checkOrder(100);
        String encodedRegionName = SerialReplicationTestBase.UTIL.getMiniHBaseCluster().getRegions(tableName).get(0).getRegionInfo().getEncodedName();
        ReplicationQueueStorage queueStorage = SerialReplicationTestBase.UTIL.getMiniHBaseCluster().getMaster().getReplicationPeerManager().getQueueStorage();
        Assert.assertTrue(((queueStorage.getLastSequenceId(encodedRegionName, SerialReplicationTestBase.PEER_ID)) > 0));
        SerialReplicationTestBase.UTIL.getAdmin().removeReplicationPeer(SerialReplicationTestBase.PEER_ID);
        // confirm that we delete the last pushed sequence id
        Assert.assertEquals(NO_SEQNUM, queueStorage.getLastSequenceId(encodedRegionName, SerialReplicationTestBase.PEER_ID));
    }

    @Test
    public void testRemoveSerialFlag() throws Exception {
        TableName tableName = createTable();
        try (Table table = SerialReplicationTestBase.UTIL.getConnection().getTable(tableName)) {
            for (int i = 0; i < 100; i++) {
                table.put(new org.apache.hadoop.hbase.client.Put(Bytes.toBytes(i)).addColumn(SerialReplicationTestBase.CF, SerialReplicationTestBase.CQ, Bytes.toBytes(i)));
            }
        }
        enablePeerAndWaitUntilReplicationDone(100);
        checkOrder(100);
        String encodedRegionName = SerialReplicationTestBase.UTIL.getMiniHBaseCluster().getRegions(tableName).get(0).getRegionInfo().getEncodedName();
        ReplicationQueueStorage queueStorage = SerialReplicationTestBase.UTIL.getMiniHBaseCluster().getMaster().getReplicationPeerManager().getQueueStorage();
        Assert.assertTrue(((queueStorage.getLastSequenceId(encodedRegionName, SerialReplicationTestBase.PEER_ID)) > 0));
        ReplicationPeerConfig peerConfig = SerialReplicationTestBase.UTIL.getAdmin().getReplicationPeerConfig(SerialReplicationTestBase.PEER_ID);
        SerialReplicationTestBase.UTIL.getAdmin().updateReplicationPeerConfig(SerialReplicationTestBase.PEER_ID, ReplicationPeerConfig.newBuilder(peerConfig).setSerial(false).build());
        // confirm that we delete the last pushed sequence id
        Assert.assertEquals(NO_SEQNUM, queueStorage.getLastSequenceId(encodedRegionName, SerialReplicationTestBase.PEER_ID));
    }
}

