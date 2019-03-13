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


import ReplicationUtils.SYNC_WAL_SUFFIX;
import SyncReplicationState.ACTIVE;
import SyncReplicationState.DOWNGRADE_ACTIVE;
import SyncReplicationState.STANDBY;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.client.RegionInfo;
import org.apache.hadoop.hbase.master.MasterFileSystem;
import org.apache.hadoop.hbase.regionserver.HRegionServer;
import org.apache.hadoop.hbase.regionserver.LogRoller;
import org.apache.hadoop.hbase.testclassification.LargeTests;
import org.apache.hadoop.hbase.testclassification.ReplicationTests;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.hbase.util.JVMClusterUtil.RegionServerThread;
import org.apache.hbase.thirdparty.com.google.common.collect.Iterables;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import static ReplicationUtils.REMOTE_WAL_DIR_NAME;


/**
 * Testcase to confirm that serial replication will not be stuck when using along with synchronous
 * replication. See HBASE-21486 for more details.
 */
@Category({ ReplicationTests.class, LargeTests.class })
public class TestSerialSyncReplication extends SyncReplicationTestBase {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestSerialSyncReplication.class);

    @Test
    public void test() throws Exception {
        // change to serial
        SyncReplicationTestBase.UTIL1.getAdmin().updateReplicationPeerConfig(SyncReplicationTestBase.PEER_ID, ReplicationPeerConfig.newBuilder(SyncReplicationTestBase.UTIL1.getAdmin().getReplicationPeerConfig(SyncReplicationTestBase.PEER_ID)).setSerial(true).build());
        SyncReplicationTestBase.UTIL2.getAdmin().updateReplicationPeerConfig(SyncReplicationTestBase.PEER_ID, ReplicationPeerConfig.newBuilder(SyncReplicationTestBase.UTIL2.getAdmin().getReplicationPeerConfig(SyncReplicationTestBase.PEER_ID)).setSerial(true).build());
        SyncReplicationTestBase.UTIL2.getAdmin().transitReplicationPeerSyncReplicationState(SyncReplicationTestBase.PEER_ID, STANDBY);
        SyncReplicationTestBase.UTIL1.getAdmin().transitReplicationPeerSyncReplicationState(SyncReplicationTestBase.PEER_ID, ACTIVE);
        SyncReplicationTestBase.UTIL2.getAdmin().disableReplicationPeer(SyncReplicationTestBase.PEER_ID);
        writeAndVerifyReplication(SyncReplicationTestBase.UTIL1, SyncReplicationTestBase.UTIL2, 0, 100);
        MasterFileSystem mfs = SyncReplicationTestBase.UTIL2.getMiniHBaseCluster().getMaster().getMasterFileSystem();
        Path remoteWALDir = ReplicationUtils.getPeerRemoteWALDir(new Path(mfs.getWALRootDir(), REMOTE_WAL_DIR_NAME), SyncReplicationTestBase.PEER_ID);
        FileStatus[] remoteWALStatus = mfs.getWALFileSystem().listStatus(remoteWALDir);
        Assert.assertEquals(1, remoteWALStatus.length);
        Path remoteWAL = remoteWALStatus[0].getPath();
        Assert.assertThat(remoteWAL.getName(), CoreMatchers.endsWith(SYNC_WAL_SUFFIX));
        // roll the wal writer, so that we will delete the remore wal. This is used to make sure that we
        // will not replay this wal when transiting to DA.
        for (RegionServerThread t : SyncReplicationTestBase.UTIL1.getMiniHBaseCluster().getRegionServerThreads()) {
            LogRoller roller = t.getRegionServer().getWalRoller();
            roller.requestRollAll();
            roller.waitUntilWalRollFinished();
        }
        waitUntilDeleted(SyncReplicationTestBase.UTIL2, remoteWAL);
        SyncReplicationTestBase.UTIL2.getAdmin().transitReplicationPeerSyncReplicationState(SyncReplicationTestBase.PEER_ID, DOWNGRADE_ACTIVE);
        SyncReplicationTestBase.UTIL1.getAdmin().transitReplicationPeerSyncReplicationState(SyncReplicationTestBase.PEER_ID, STANDBY);
        // let's reopen the region
        RegionInfo region = Iterables.getOnlyElement(SyncReplicationTestBase.UTIL2.getAdmin().getRegions(SyncReplicationTestBase.TABLE_NAME));
        HRegionServer target = SyncReplicationTestBase.UTIL2.getOtherRegionServer(SyncReplicationTestBase.UTIL2.getRSForFirstRegionInTable(SyncReplicationTestBase.TABLE_NAME));
        SyncReplicationTestBase.UTIL2.getAdmin().move(region.getEncodedNameAsBytes(), Bytes.toBytes(target.getServerName().getServerName()));
        // here we will remove all the pending wals. This is not a normal operation sequence but anyway,
        // user could do this.
        SyncReplicationTestBase.UTIL2.getAdmin().transitReplicationPeerSyncReplicationState(SyncReplicationTestBase.PEER_ID, STANDBY);
        // transit back to DA
        SyncReplicationTestBase.UTIL2.getAdmin().transitReplicationPeerSyncReplicationState(SyncReplicationTestBase.PEER_ID, DOWNGRADE_ACTIVE);
        SyncReplicationTestBase.UTIL2.getAdmin().enableReplicationPeer(SyncReplicationTestBase.PEER_ID);
        // make sure that the async replication still works
        writeAndVerifyReplication(SyncReplicationTestBase.UTIL2, SyncReplicationTestBase.UTIL1, 100, 200);
    }
}

