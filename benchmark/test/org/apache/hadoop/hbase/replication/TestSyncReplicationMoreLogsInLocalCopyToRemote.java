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
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.client.AsyncConnection;
import org.apache.hadoop.hbase.client.AsyncTable;
import org.apache.hadoop.hbase.client.ConnectionFactory;
import org.apache.hadoop.hbase.client.RegionInfoBuilder;
import org.apache.hadoop.hbase.client.Table;
import org.apache.hadoop.hbase.regionserver.HRegionServer;
import org.apache.hadoop.hbase.testclassification.LargeTests;
import org.apache.hadoop.hbase.testclassification.ReplicationTests;
import org.apache.hadoop.hbase.util.Bytes;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Category({ ReplicationTests.class, LargeTests.class })
public class TestSyncReplicationMoreLogsInLocalCopyToRemote extends SyncReplicationTestBase {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestSyncReplicationMoreLogsInLocalCopyToRemote.class);

    private static final Logger LOG = LoggerFactory.getLogger(TestSyncReplicationMoreLogsInLocalCopyToRemote.class);

    @Test
    public void testSplitLog() throws Exception {
        SyncReplicationTestBase.UTIL1.getAdmin().disableReplicationPeer(SyncReplicationTestBase.PEER_ID);
        SyncReplicationTestBase.UTIL2.getAdmin().transitReplicationPeerSyncReplicationState(SyncReplicationTestBase.PEER_ID, STANDBY);
        SyncReplicationTestBase.UTIL1.getAdmin().transitReplicationPeerSyncReplicationState(SyncReplicationTestBase.PEER_ID, ACTIVE);
        HRegionServer rs = SyncReplicationTestBase.UTIL1.getRSForFirstRegionInTable(SyncReplicationTestBase.TABLE_NAME);
        DualAsyncFSWALForTest wal = ((DualAsyncFSWALForTest) (rs.getWAL(RegionInfoBuilder.newBuilder(SyncReplicationTestBase.TABLE_NAME).build())));
        wal.setRemoteBroken();
        try (AsyncConnection conn = ConnectionFactory.createAsyncConnection(SyncReplicationTestBase.UTIL1.getConfiguration()).get()) {
            AsyncTable<?> table = conn.getTableBuilder(SyncReplicationTestBase.TABLE_NAME).setMaxAttempts(1).build();
            try {
                table.put(new org.apache.hadoop.hbase.client.Put(Bytes.toBytes(0)).addColumn(SyncReplicationTestBase.CF, SyncReplicationTestBase.CQ, Bytes.toBytes(0))).get();
                Assert.fail("Should fail since the rs will crash and we will not retry");
            } catch (ExecutionException e) {
                // expected
                TestSyncReplicationMoreLogsInLocalCopyToRemote.LOG.info("Expected error:", e);
            }
        }
        waitFor(60000, new org.apache.hadoop.hbase.Waiter.ExplainingPredicate<Exception>() {
            @Override
            public boolean evaluate() throws Exception {
                try (Table table = SyncReplicationTestBase.UTIL1.getConnection().getTable(SyncReplicationTestBase.TABLE_NAME)) {
                    return table.exists(new org.apache.hadoop.hbase.client.Get(Bytes.toBytes(0)));
                }
            }

            @Override
            public String explainFailure() throws Exception {
                return "The row is still not available";
            }
        });
        SyncReplicationTestBase.UTIL2.getAdmin().transitReplicationPeerSyncReplicationState(SyncReplicationTestBase.PEER_ID, DOWNGRADE_ACTIVE);
        // We should have copied the local log to remote, so we should be able to get the value
        try (Table table = SyncReplicationTestBase.UTIL2.getConnection().getTable(SyncReplicationTestBase.TABLE_NAME)) {
            Assert.assertEquals(0, Bytes.toInt(table.get(new org.apache.hadoop.hbase.client.Get(Bytes.toBytes(0))).getValue(SyncReplicationTestBase.CF, SyncReplicationTestBase.CQ)));
        }
    }
}

