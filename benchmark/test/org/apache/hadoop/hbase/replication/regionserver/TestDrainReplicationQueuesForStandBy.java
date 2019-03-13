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
package org.apache.hadoop.hbase.replication.regionserver;


import SyncReplicationState.ACTIVE;
import SyncReplicationState.DOWNGRADE_ACTIVE;
import SyncReplicationState.STANDBY;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.client.RegionInfoBuilder;
import org.apache.hadoop.hbase.client.Table;
import org.apache.hadoop.hbase.regionserver.HRegion;
import org.apache.hadoop.hbase.regionserver.HRegionServer;
import org.apache.hadoop.hbase.regionserver.wal.AbstractFSWAL;
import org.apache.hadoop.hbase.replication.SyncReplicationTestBase;
import org.apache.hadoop.hbase.testclassification.MediumTests;
import org.apache.hadoop.hbase.testclassification.ReplicationTests;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.hbase.wal.AbstractFSWALProvider;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.experimental.categories.Category;


@Category({ ReplicationTests.class, MediumTests.class })
public class TestDrainReplicationQueuesForStandBy extends SyncReplicationTestBase {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestDrainReplicationQueuesForStandBy.class);

    @Test
    public void test() throws Exception {
        SyncReplicationTestBase.UTIL2.getAdmin().transitReplicationPeerSyncReplicationState(SyncReplicationTestBase.PEER_ID, STANDBY);
        SyncReplicationTestBase.UTIL1.getAdmin().transitReplicationPeerSyncReplicationState(SyncReplicationTestBase.PEER_ID, ACTIVE);
        SyncReplicationTestBase.UTIL1.getAdmin().disableReplicationPeer(SyncReplicationTestBase.PEER_ID);
        write(SyncReplicationTestBase.UTIL1, 0, 100);
        HRegionServer rs = SyncReplicationTestBase.UTIL1.getRSForFirstRegionInTable(SyncReplicationTestBase.TABLE_NAME);
        String walGroupId = AbstractFSWALProvider.getWALPrefixFromWALName(((AbstractFSWAL<?>) (rs.getWAL(RegionInfoBuilder.newBuilder(SyncReplicationTestBase.TABLE_NAME).build()))).getCurrentFileName().getName());
        SyncReplicationTestBase.UTIL2.getAdmin().transitReplicationPeerSyncReplicationState(SyncReplicationTestBase.PEER_ID, DOWNGRADE_ACTIVE);
        // transit cluster2 to DA and cluster 1 to S
        verify(SyncReplicationTestBase.UTIL2, 0, 100);
        SyncReplicationTestBase.UTIL1.getAdmin().transitReplicationPeerSyncReplicationState(SyncReplicationTestBase.PEER_ID, STANDBY);
        // delete the original value, and then major compact
        try (Table table = SyncReplicationTestBase.UTIL2.getConnection().getTable(SyncReplicationTestBase.TABLE_NAME)) {
            for (int i = 0; i < 100; i++) {
                table.delete(new org.apache.hadoop.hbase.client.Delete(Bytes.toBytes(i)));
            }
        }
        SyncReplicationTestBase.UTIL2.flush(SyncReplicationTestBase.TABLE_NAME);
        SyncReplicationTestBase.UTIL2.compact(SyncReplicationTestBase.TABLE_NAME, true);
        // wait until the new values are replicated back to cluster1
        HRegion region = rs.getRegions(SyncReplicationTestBase.TABLE_NAME).get(0);
        SyncReplicationTestBase.UTIL1.waitFor(30000, new org.apache.hadoop.hbase.Waiter.ExplainingPredicate<Exception>() {
            @Override
            public boolean evaluate() throws Exception {
                return region.get(new org.apache.hadoop.hbase.client.Get(Bytes.toBytes(99))).isEmpty();
            }

            @Override
            public String explainFailure() throws Exception {
                return "Replication has not been catched up yet";
            }
        });
        // transit cluster1 to DA and cluster2 to S, then we will start replicating from cluster1 to
        // cluster2
        SyncReplicationTestBase.UTIL1.getAdmin().transitReplicationPeerSyncReplicationState(SyncReplicationTestBase.PEER_ID, DOWNGRADE_ACTIVE);
        SyncReplicationTestBase.UTIL2.getAdmin().transitReplicationPeerSyncReplicationState(SyncReplicationTestBase.PEER_ID, STANDBY);
        SyncReplicationTestBase.UTIL1.getAdmin().enableReplicationPeer(SyncReplicationTestBase.PEER_ID);
        // confirm that we will not replicate the old data which causes inconsistency
        ReplicationSource source = ((ReplicationSource) (getReplicationManager().getSource(SyncReplicationTestBase.PEER_ID)));
        waitFor(30000, new org.apache.hadoop.hbase.Waiter.ExplainingPredicate<Exception>() {
            @Override
            public boolean evaluate() throws Exception {
                return !(source.workerThreads.containsKey(walGroupId));
            }

            @Override
            public String explainFailure() throws Exception {
                return "Replication has not been catched up yet";
            }
        });
        HRegion region2 = SyncReplicationTestBase.UTIL2.getMiniHBaseCluster().getRegions(SyncReplicationTestBase.TABLE_NAME).get(0);
        for (int i = 0; i < 100; i++) {
            Assert.assertTrue(region2.get(new org.apache.hadoop.hbase.client.Get(Bytes.toBytes(i))).isEmpty());
        }
    }
}

