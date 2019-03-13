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


import SyncReplicationState.ACTIVE;
import SyncReplicationState.DOWNGRADE_ACTIVE;
import SyncReplicationState.STANDBY;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import org.apache.hadoop.hbase.DoNotRetryIOException;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.client.AsyncConnection;
import org.apache.hadoop.hbase.client.AsyncTable;
import org.apache.hadoop.hbase.client.ConnectionFactory;
import org.apache.hadoop.hbase.client.RegionInfoBuilder;
import org.apache.hadoop.hbase.client.RetriesExhaustedException;
import org.apache.hadoop.hbase.client.Table;
import org.apache.hadoop.hbase.regionserver.HRegion;
import org.apache.hadoop.hbase.regionserver.HRegionServer;
import org.apache.hadoop.hbase.testclassification.LargeTests;
import org.apache.hadoop.hbase.testclassification.ReplicationTests;
import org.apache.hadoop.hbase.util.Bytes;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Category({ ReplicationTests.class, LargeTests.class })
public class TestSyncReplicationMoreLogsInLocalGiveUpSplitting extends SyncReplicationTestBase {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestSyncReplicationMoreLogsInLocalGiveUpSplitting.class);

    private static final Logger LOG = LoggerFactory.getLogger(TestSyncReplicationMoreLogsInLocalGiveUpSplitting.class);

    @Test
    public void testSplitLog() throws Exception {
        SyncReplicationTestBase.UTIL1.getAdmin().disableReplicationPeer(SyncReplicationTestBase.PEER_ID);
        SyncReplicationTestBase.UTIL2.getAdmin().disableReplicationPeer(SyncReplicationTestBase.PEER_ID);
        SyncReplicationTestBase.UTIL2.getAdmin().transitReplicationPeerSyncReplicationState(SyncReplicationTestBase.PEER_ID, STANDBY);
        SyncReplicationTestBase.UTIL1.getAdmin().transitReplicationPeerSyncReplicationState(SyncReplicationTestBase.PEER_ID, ACTIVE);
        try (Table table = SyncReplicationTestBase.UTIL1.getConnection().getTable(SyncReplicationTestBase.TABLE_NAME)) {
            table.put(new org.apache.hadoop.hbase.client.Put(Bytes.toBytes(0)).addColumn(SyncReplicationTestBase.CF, SyncReplicationTestBase.CQ, Bytes.toBytes(0)));
        }
        HRegionServer rs = SyncReplicationTestBase.UTIL1.getRSForFirstRegionInTable(SyncReplicationTestBase.TABLE_NAME);
        DualAsyncFSWALForTest wal = ((DualAsyncFSWALForTest) (rs.getWAL(RegionInfoBuilder.newBuilder(SyncReplicationTestBase.TABLE_NAME).build())));
        wal.setRemoteBroken();
        wal.suspendLogRoll();
        try (AsyncConnection conn = ConnectionFactory.createAsyncConnection(SyncReplicationTestBase.UTIL1.getConfiguration()).get()) {
            AsyncTable<?> table = conn.getTableBuilder(SyncReplicationTestBase.TABLE_NAME).setMaxAttempts(1).setWriteRpcTimeout(5, TimeUnit.SECONDS).build();
            try {
                table.put(new org.apache.hadoop.hbase.client.Put(Bytes.toBytes(1)).addColumn(SyncReplicationTestBase.CF, SyncReplicationTestBase.CQ, Bytes.toBytes(1))).get();
                Assert.fail("Should fail since the rs will hang and we will get a rpc timeout");
            } catch (ExecutionException e) {
                // expected
                TestSyncReplicationMoreLogsInLocalGiveUpSplitting.LOG.info("Expected error:", e);
            }
        }
        wal.waitUntilArrive();
        SyncReplicationTestBase.UTIL2.getAdmin().transitReplicationPeerSyncReplicationState(SyncReplicationTestBase.PEER_ID, DOWNGRADE_ACTIVE);
        wal.resumeLogRoll();
        try (Table table = SyncReplicationTestBase.UTIL2.getConnection().getTable(SyncReplicationTestBase.TABLE_NAME)) {
            Assert.assertEquals(0, Bytes.toInt(table.get(new org.apache.hadoop.hbase.client.Get(Bytes.toBytes(0))).getValue(SyncReplicationTestBase.CF, SyncReplicationTestBase.CQ)));
            // we failed to write this entry to remote so it should not exist
            Assert.assertFalse(table.exists(new org.apache.hadoop.hbase.client.Get(Bytes.toBytes(1))));
        }
        SyncReplicationTestBase.UTIL1.getAdmin().transitReplicationPeerSyncReplicationState(SyncReplicationTestBase.PEER_ID, STANDBY);
        // make sure that the region is online. We can not use waitTableAvailable since the table in
        // stand by state can not be read from client.
        try (Table table = SyncReplicationTestBase.UTIL1.getConnection().getTable(SyncReplicationTestBase.TABLE_NAME)) {
            try {
                table.exists(new org.apache.hadoop.hbase.client.Get(Bytes.toBytes(0)));
            } catch (DoNotRetryIOException | RetriesExhaustedException e) {
                // expected
                Assert.assertThat(getMessage(), CoreMatchers.containsString("STANDBY"));
            }
        }
        HRegion region = SyncReplicationTestBase.UTIL1.getMiniHBaseCluster().getRegions(SyncReplicationTestBase.TABLE_NAME).get(0);
        // we give up splitting the whole wal file so this record will also be gone.
        Assert.assertTrue(region.get(new org.apache.hadoop.hbase.client.Get(Bytes.toBytes(0))).isEmpty());
        SyncReplicationTestBase.UTIL2.getAdmin().enableReplicationPeer(SyncReplicationTestBase.PEER_ID);
        // finally it should be replicated back
        waitUntilReplicationDone(SyncReplicationTestBase.UTIL1, 1);
    }
}

