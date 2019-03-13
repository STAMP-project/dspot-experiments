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
import org.apache.hadoop.hbase.regionserver.HRegionServer;
import org.apache.hadoop.hbase.regionserver.wal.DualAsyncFSWAL;
import org.apache.hadoop.hbase.replication.ReplicationPeerConfig;
import org.apache.hadoop.hbase.replication.SyncReplicationTestBase;
import org.apache.hadoop.hbase.testclassification.LargeTests;
import org.apache.hadoop.hbase.testclassification.ReplicationTests;
import org.apache.hadoop.hbase.wal.AbstractFSWALProvider;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.experimental.categories.Category;


/**
 * Testcase for HBASE-20456.
 */
@Category({ ReplicationTests.class, LargeTests.class })
public class TestSyncReplicationShipperQuit extends SyncReplicationTestBase {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestSyncReplicationShipperQuit.class);

    @Test
    public void testShipperQuitWhenDA() throws Exception {
        // set to serial replication
        SyncReplicationTestBase.UTIL1.getAdmin().updateReplicationPeerConfig(SyncReplicationTestBase.PEER_ID, ReplicationPeerConfig.newBuilder(SyncReplicationTestBase.UTIL1.getAdmin().getReplicationPeerConfig(SyncReplicationTestBase.PEER_ID)).setSerial(true).build());
        SyncReplicationTestBase.UTIL2.getAdmin().updateReplicationPeerConfig(SyncReplicationTestBase.PEER_ID, ReplicationPeerConfig.newBuilder(SyncReplicationTestBase.UTIL2.getAdmin().getReplicationPeerConfig(SyncReplicationTestBase.PEER_ID)).setSerial(true).build());
        SyncReplicationTestBase.UTIL2.getAdmin().transitReplicationPeerSyncReplicationState(SyncReplicationTestBase.PEER_ID, STANDBY);
        SyncReplicationTestBase.UTIL1.getAdmin().transitReplicationPeerSyncReplicationState(SyncReplicationTestBase.PEER_ID, ACTIVE);
        writeAndVerifyReplication(SyncReplicationTestBase.UTIL1, SyncReplicationTestBase.UTIL2, 0, 100);
        HRegionServer rs = SyncReplicationTestBase.UTIL1.getRSForFirstRegionInTable(SyncReplicationTestBase.TABLE_NAME);
        DualAsyncFSWAL wal = ((DualAsyncFSWAL) (rs.getWAL(RegionInfoBuilder.newBuilder(SyncReplicationTestBase.TABLE_NAME).build())));
        String walGroupId = AbstractFSWALProvider.getWALPrefixFromWALName(wal.getCurrentFileName().getName());
        ReplicationSourceShipper shipper = ((ReplicationSource) (getReplicationManager().getSource(SyncReplicationTestBase.PEER_ID))).workerThreads.get(walGroupId);
        Assert.assertFalse(shipper.isFinished());
        SyncReplicationTestBase.UTIL1.getAdmin().transitReplicationPeerSyncReplicationState(SyncReplicationTestBase.PEER_ID, DOWNGRADE_ACTIVE);
        writeAndVerifyReplication(SyncReplicationTestBase.UTIL1, SyncReplicationTestBase.UTIL2, 100, 200);
        ReplicationSource source = ((ReplicationSource) (getReplicationManager().getSource(SyncReplicationTestBase.PEER_ID)));
        // the peer is serial so here we can make sure that the previous wals have already been
        // replicated, and finally the shipper should be removed from the worker pool
        waitFor(10000, () -> !(source.workerThreads.containsKey(walGroupId)));
        Assert.assertTrue(shipper.isFinished());
    }
}

