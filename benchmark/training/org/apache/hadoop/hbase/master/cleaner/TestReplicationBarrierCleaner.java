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
package org.apache.hadoop.hbase.master.cleaner;


import HConstants.REPLICATION_BARRIER_FAMILY;
import TableName.META_TABLE_NAME;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.HBaseTestingUtility;
import org.apache.hadoop.hbase.MetaTableAccessor;
import org.apache.hadoop.hbase.Stoppable;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.RegionInfo;
import org.apache.hadoop.hbase.client.RegionInfoBuilder;
import org.apache.hadoop.hbase.client.Table;
import org.apache.hadoop.hbase.master.replication.ReplicationPeerManager;
import org.apache.hadoop.hbase.replication.ReplicationException;
import org.apache.hadoop.hbase.replication.ReplicationQueueStorage;
import org.apache.hadoop.hbase.testclassification.MasterTests;
import org.apache.hadoop.hbase.testclassification.MediumTests;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hbase.thirdparty.com.google.common.collect.Lists;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.TestName;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Category({ MasterTests.class, MediumTests.class })
public class TestReplicationBarrierCleaner {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestReplicationBarrierCleaner.class);

    private static final Logger LOG = LoggerFactory.getLogger(TestHFileCleaner.class);

    private static final HBaseTestingUtility UTIL = new HBaseTestingUtility();

    @Rule
    public final TestName name = new TestName();

    @Test
    public void testNothing() throws IOException {
        ReplicationPeerManager peerManager = Mockito.mock(ReplicationPeerManager.class);
        ReplicationBarrierCleaner cleaner = create(peerManager);
        cleaner.chore();
        Mockito.verify(peerManager, Mockito.never()).getSerialPeerIdsBelongsTo(ArgumentMatchers.any(TableName.class));
        Mockito.verify(peerManager, Mockito.never()).getQueueStorage();
    }

    @Test
    public void testCleanNoPeers() throws IOException {
        TableName tableName1 = TableName.valueOf(((name.getMethodName()) + "_1"));
        RegionInfo region11 = RegionInfoBuilder.newBuilder(tableName1).setEndKey(Bytes.toBytes(1)).build();
        addBarrier(region11, 10, 20, 30, 40, 50, 60);
        RegionInfo region12 = RegionInfoBuilder.newBuilder(tableName1).setStartKey(Bytes.toBytes(1)).build();
        addBarrier(region12, 20, 30, 40, 50, 60, 70);
        TableName tableName2 = TableName.valueOf(((name.getMethodName()) + "_2"));
        RegionInfo region21 = RegionInfoBuilder.newBuilder(tableName2).setEndKey(Bytes.toBytes(1)).build();
        addBarrier(region21, 100, 200, 300, 400);
        RegionInfo region22 = RegionInfoBuilder.newBuilder(tableName2).setStartKey(Bytes.toBytes(1)).build();
        addBarrier(region22, 200, 300, 400, 500, 600);
        @SuppressWarnings("unchecked")
        ReplicationPeerManager peerManager = create(null, Collections.emptyList(), Collections.emptyList());
        ReplicationBarrierCleaner cleaner = create(peerManager);
        cleaner.chore();
        // should never call this method
        Mockito.verify(peerManager, Mockito.never()).getQueueStorage();
        // should only be called twice although we have 4 regions to clean
        Mockito.verify(peerManager, Mockito.times(2)).getSerialPeerIdsBelongsTo(ArgumentMatchers.any(TableName.class));
        Assert.assertArrayEquals(new long[]{ 60 }, MetaTableAccessor.getReplicationBarrier(TestReplicationBarrierCleaner.UTIL.getConnection(), region11.getRegionName()));
        Assert.assertArrayEquals(new long[]{ 70 }, MetaTableAccessor.getReplicationBarrier(TestReplicationBarrierCleaner.UTIL.getConnection(), region12.getRegionName()));
        Assert.assertArrayEquals(new long[]{ 400 }, MetaTableAccessor.getReplicationBarrier(TestReplicationBarrierCleaner.UTIL.getConnection(), region21.getRegionName()));
        Assert.assertArrayEquals(new long[]{ 600 }, MetaTableAccessor.getReplicationBarrier(TestReplicationBarrierCleaner.UTIL.getConnection(), region22.getRegionName()));
    }

    @Test
    public void testDeleteBarriers() throws IOException, ReplicationException {
        TableName tableName = TableName.valueOf(name.getMethodName());
        RegionInfo region = RegionInfoBuilder.newBuilder(tableName).build();
        addBarrier(region, 10, 20, 30, 40, 50, 60);
        // two peers
        ReplicationQueueStorage queueStorage = create((-1L), 2L, 15L, 25L, 20L, 25L, 65L, 55L, 70L, 70L);
        List<String> peerIds = Lists.newArrayList("1", "2");
        @SuppressWarnings("unchecked")
        ReplicationPeerManager peerManager = create(queueStorage, peerIds, peerIds, peerIds, peerIds, peerIds);
        ReplicationBarrierCleaner cleaner = create(peerManager);
        // beyond the first barrier, no deletion
        cleaner.chore();
        Assert.assertArrayEquals(new long[]{ 10, 20, 30, 40, 50, 60 }, MetaTableAccessor.getReplicationBarrier(TestReplicationBarrierCleaner.UTIL.getConnection(), region.getRegionName()));
        // in the first range, still no deletion
        cleaner.chore();
        Assert.assertArrayEquals(new long[]{ 10, 20, 30, 40, 50, 60 }, MetaTableAccessor.getReplicationBarrier(TestReplicationBarrierCleaner.UTIL.getConnection(), region.getRegionName()));
        // in the second range, 10 is deleted
        cleaner.chore();
        Assert.assertArrayEquals(new long[]{ 20, 30, 40, 50, 60 }, MetaTableAccessor.getReplicationBarrier(TestReplicationBarrierCleaner.UTIL.getConnection(), region.getRegionName()));
        // between 50 and 60, so the barriers before 50 will be deleted
        cleaner.chore();
        Assert.assertArrayEquals(new long[]{ 50, 60 }, MetaTableAccessor.getReplicationBarrier(TestReplicationBarrierCleaner.UTIL.getConnection(), region.getRegionName()));
        // in the last open range, 50 is deleted
        cleaner.chore();
        Assert.assertArrayEquals(new long[]{ 60 }, MetaTableAccessor.getReplicationBarrier(TestReplicationBarrierCleaner.UTIL.getConnection(), region.getRegionName()));
    }

    @Test
    public void testDeleteRowForDeletedRegion() throws IOException, ReplicationException {
        TableName tableName = TableName.valueOf(name.getMethodName());
        RegionInfo region = RegionInfoBuilder.newBuilder(tableName).build();
        addBarrier(region, 40, 50, 60);
        fillCatalogFamily(region);
        String peerId = "1";
        ReplicationQueueStorage queueStorage = create(59L);
        @SuppressWarnings("unchecked")
        ReplicationPeerManager peerManager = create(queueStorage, Lists.newArrayList(peerId));
        ReplicationBarrierCleaner cleaner = create(peerManager);
        // we have something in catalog family, so only delete 40
        cleaner.chore();
        Assert.assertArrayEquals(new long[]{ 50, 60 }, MetaTableAccessor.getReplicationBarrier(TestReplicationBarrierCleaner.UTIL.getConnection(), region.getRegionName()));
        Mockito.verify(queueStorage, Mockito.never()).removeLastSequenceIds(ArgumentMatchers.anyString(), ArgumentMatchers.anyList());
        // No catalog family, then we should remove the whole row
        clearCatalogFamily(region);
        cleaner.chore();
        try (Table table = TestReplicationBarrierCleaner.UTIL.getConnection().getTable(META_TABLE_NAME)) {
            Assert.assertFalse(table.exists(new org.apache.hadoop.hbase.client.Get(region.getRegionName()).addFamily(REPLICATION_BARRIER_FAMILY)));
        }
        Mockito.verify(queueStorage, Mockito.times(1)).removeLastSequenceIds(peerId, Arrays.asList(region.getEncodedName()));
    }

    private static class WarnOnlyStoppable implements Stoppable {
        @Override
        public void stop(String why) {
            TestReplicationBarrierCleaner.LOG.warn(("TestReplicationBarrierCleaner received stop, ignoring. Reason: " + why));
        }

        @Override
        public boolean isStopped() {
            return false;
        }
    }
}

