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
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.client.AsyncConnection;
import org.apache.hadoop.hbase.client.AsyncTable;
import org.apache.hadoop.hbase.client.ConnectionFactory;
import org.apache.hadoop.hbase.regionserver.HRegion;
import org.apache.hadoop.hbase.testclassification.LargeTests;
import org.apache.hadoop.hbase.testclassification.ReplicationTests;
import org.apache.hadoop.hbase.util.Bytes;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.experimental.categories.Category;


@Category({ ReplicationTests.class, LargeTests.class })
public class TestSyncReplicationActive extends SyncReplicationTestBase {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestSyncReplicationActive.class);

    @Test
    public void testActive() throws Exception {
        SyncReplicationTestBase.UTIL2.getAdmin().transitReplicationPeerSyncReplicationState(SyncReplicationTestBase.PEER_ID, STANDBY);
        SyncReplicationTestBase.UTIL1.getAdmin().transitReplicationPeerSyncReplicationState(SyncReplicationTestBase.PEER_ID, ACTIVE);
        // confirm that peer with state A will reject replication request.
        verifyReplicationRequestRejection(SyncReplicationTestBase.UTIL1, true);
        verifyReplicationRequestRejection(SyncReplicationTestBase.UTIL2, false);
        SyncReplicationTestBase.UTIL1.getAdmin().disableReplicationPeer(SyncReplicationTestBase.PEER_ID);
        write(SyncReplicationTestBase.UTIL1, 0, 100);
        Thread.sleep(2000);
        // peer is disabled so no data have been replicated
        verifyNotReplicatedThroughRegion(SyncReplicationTestBase.UTIL2, 0, 100);
        // Ensure that there's no cluster id in remote log entries.
        verifyNoClusterIdInRemoteLog(SyncReplicationTestBase.UTIL2, SyncReplicationTestBase.REMOTE_WAL_DIR2, SyncReplicationTestBase.PEER_ID);
        SyncReplicationTestBase.UTIL2.getAdmin().transitReplicationPeerSyncReplicationState(SyncReplicationTestBase.PEER_ID, DOWNGRADE_ACTIVE);
        // confirm that peer with state DA will reject replication request.
        verifyReplicationRequestRejection(SyncReplicationTestBase.UTIL2, true);
        // confirm that the data is there after we convert the peer to DA
        verify(SyncReplicationTestBase.UTIL2, 0, 100);
        try (AsyncConnection conn = ConnectionFactory.createAsyncConnection(SyncReplicationTestBase.UTIL1.getConfiguration()).get()) {
            AsyncTable<?> table = conn.getTableBuilder(SyncReplicationTestBase.TABLE_NAME).setMaxAttempts(1).build();
            CompletableFuture<Void> future = table.put(new org.apache.hadoop.hbase.client.Put(Bytes.toBytes(1000)).addColumn(SyncReplicationTestBase.CF, SyncReplicationTestBase.CQ, Bytes.toBytes(1000)));
            Thread.sleep(2000);
            // should hang on rolling
            Assert.assertFalse(future.isDone());
            SyncReplicationTestBase.UTIL1.getAdmin().transitReplicationPeerSyncReplicationState(SyncReplicationTestBase.PEER_ID, STANDBY);
            try {
                future.get();
                Assert.fail("should fail because of the wal is closing");
            } catch (ExecutionException e) {
                // expected
                Assert.assertThat(e.getCause().getMessage(), CoreMatchers.containsString("only marker edit is allowed"));
            }
        }
        // confirm that the data has not been persisted
        HRegion region = SyncReplicationTestBase.UTIL1.getMiniHBaseCluster().getRegions(SyncReplicationTestBase.TABLE_NAME).get(0);
        Assert.assertTrue(region.get(new org.apache.hadoop.hbase.client.Get(Bytes.toBytes(1000))).isEmpty());
        SyncReplicationTestBase.UTIL2.getAdmin().transitReplicationPeerSyncReplicationState(SyncReplicationTestBase.PEER_ID, ACTIVE);
        writeAndVerifyReplication(SyncReplicationTestBase.UTIL2, SyncReplicationTestBase.UTIL1, 100, 200);
        // shutdown the cluster completely
        SyncReplicationTestBase.UTIL1.shutdownMiniCluster();
        // confirm that we can convert to DA even if the remote slave cluster is down
        SyncReplicationTestBase.UTIL2.getAdmin().transitReplicationPeerSyncReplicationState(SyncReplicationTestBase.PEER_ID, DOWNGRADE_ACTIVE);
        // confirm that peer with state DA will reject replication request.
        verifyReplicationRequestRejection(SyncReplicationTestBase.UTIL2, true);
        write(SyncReplicationTestBase.UTIL2, 200, 300);
    }
}

