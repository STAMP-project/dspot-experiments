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
package org.apache.hadoop.hbase.wal;


import SyncReplicationState.ACTIVE;
import SyncReplicationState.DOWNGRADE_ACTIVE;
import java.util.Optional;
import java.util.function.BiPredicate;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.HBaseTestingUtility;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.RegionInfo;
import org.apache.hadoop.hbase.client.RegionInfoBuilder;
import org.apache.hadoop.hbase.regionserver.wal.DualAsyncFSWAL;
import org.apache.hadoop.hbase.replication.SyncReplicationState;
import org.apache.hadoop.hbase.replication.regionserver.SyncReplicationPeerInfoProvider;
import org.apache.hadoop.hbase.testclassification.MediumTests;
import org.apache.hadoop.hbase.testclassification.RegionServerTests;
import org.apache.hadoop.hbase.util.Pair;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.experimental.categories.Category;


@Category({ RegionServerTests.class, MediumTests.class })
public class TestSyncReplicationWALProvider {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestSyncReplicationWALProvider.class);

    private static final HBaseTestingUtility UTIL = new HBaseTestingUtility();

    private static String PEER_ID = "1";

    private static String REMOTE_WAL_DIR = "/RemoteWAL";

    private static TableName TABLE = TableName.valueOf("table");

    private static TableName TABLE_NO_REP = TableName.valueOf("table-no-rep");

    private static RegionInfo REGION = RegionInfoBuilder.newBuilder(TestSyncReplicationWALProvider.TABLE).build();

    private static RegionInfo REGION_NO_REP = RegionInfoBuilder.newBuilder(TestSyncReplicationWALProvider.TABLE_NO_REP).build();

    private static WALFactory FACTORY;

    public static final class InfoProvider implements SyncReplicationPeerInfoProvider {
        @Override
        public Optional<Pair<String, String>> getPeerIdAndRemoteWALDir(TableName table) {
            if ((table != null) && (table.equals(TestSyncReplicationWALProvider.TABLE))) {
                return Optional.of(Pair.newPair(TestSyncReplicationWALProvider.PEER_ID, TestSyncReplicationWALProvider.REMOTE_WAL_DIR));
            } else {
                return Optional.empty();
            }
        }

        @Override
        public boolean checkState(TableName table, BiPredicate<SyncReplicationState, SyncReplicationState> checker) {
            return false;
        }
    }

    @Test
    public void test() throws Exception {
        WAL walNoRep = TestSyncReplicationWALProvider.FACTORY.getWAL(TestSyncReplicationWALProvider.REGION_NO_REP);
        Assert.assertThat(walNoRep, CoreMatchers.not(CoreMatchers.instanceOf(DualAsyncFSWAL.class)));
        DualAsyncFSWAL wal = ((DualAsyncFSWAL) (TestSyncReplicationWALProvider.FACTORY.getWAL(TestSyncReplicationWALProvider.REGION)));
        Assert.assertEquals(2, TestSyncReplicationWALProvider.FACTORY.getWALs().size());
        testReadWrite(wal);
        SyncReplicationWALProvider walProvider = ((SyncReplicationWALProvider) (TestSyncReplicationWALProvider.FACTORY.getWALProvider()));
        walProvider.peerSyncReplicationStateChange(TestSyncReplicationWALProvider.PEER_ID, ACTIVE, DOWNGRADE_ACTIVE, 1);
        Assert.assertEquals(1, TestSyncReplicationWALProvider.FACTORY.getWALs().size());
    }
}

