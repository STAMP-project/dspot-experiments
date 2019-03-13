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
import org.apache.hadoop.hbase.master.MasterFileSystem;
import org.apache.hadoop.hbase.regionserver.HRegionServer;
import org.apache.hadoop.hbase.testclassification.LargeTests;
import org.apache.hadoop.hbase.testclassification.ReplicationTests;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import static ReplicationUtils.REMOTE_WAL_DIR_NAME;


@Category({ ReplicationTests.class, LargeTests.class })
public class TestSyncReplicationRemoveRemoteWAL extends SyncReplicationTestBase {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestSyncReplicationRemoveRemoteWAL.class);

    @Test
    public void testRemoveRemoteWAL() throws Exception {
        SyncReplicationTestBase.UTIL2.getAdmin().transitReplicationPeerSyncReplicationState(SyncReplicationTestBase.PEER_ID, STANDBY);
        SyncReplicationTestBase.UTIL1.getAdmin().transitReplicationPeerSyncReplicationState(SyncReplicationTestBase.PEER_ID, ACTIVE);
        MasterFileSystem mfs = SyncReplicationTestBase.UTIL2.getMiniHBaseCluster().getMaster().getMasterFileSystem();
        Path remoteWALDir = ReplicationUtils.getPeerRemoteWALDir(new Path(mfs.getWALRootDir(), REMOTE_WAL_DIR_NAME), SyncReplicationTestBase.PEER_ID);
        FileStatus[] remoteWALStatus = mfs.getWALFileSystem().listStatus(remoteWALDir);
        Assert.assertEquals(1, remoteWALStatus.length);
        Path remoteWAL = remoteWALStatus[0].getPath();
        Assert.assertThat(remoteWAL.getName(), CoreMatchers.endsWith(SYNC_WAL_SUFFIX));
        writeAndVerifyReplication(SyncReplicationTestBase.UTIL1, SyncReplicationTestBase.UTIL2, 0, 100);
        HRegionServer rs = SyncReplicationTestBase.UTIL1.getRSForFirstRegionInTable(SyncReplicationTestBase.TABLE_NAME);
        rs.getWalRoller().requestRollAll();
        // The replicated wal file should be deleted finally
        waitUntilDeleted(SyncReplicationTestBase.UTIL2, remoteWAL);
        remoteWALStatus = mfs.getWALFileSystem().listStatus(remoteWALDir);
        Assert.assertEquals(1, remoteWALStatus.length);
        remoteWAL = remoteWALStatus[0].getPath();
        Assert.assertThat(remoteWAL.getName(), CoreMatchers.endsWith(SYNC_WAL_SUFFIX));
        SyncReplicationTestBase.UTIL1.getAdmin().disableReplicationPeer(SyncReplicationTestBase.PEER_ID);
        write(SyncReplicationTestBase.UTIL1, 100, 200);
        SyncReplicationTestBase.UTIL1.getAdmin().transitReplicationPeerSyncReplicationState(SyncReplicationTestBase.PEER_ID, DOWNGRADE_ACTIVE);
        // should still be there since the peer is disabled and we haven't replicated the data yet
        Assert.assertTrue(mfs.getWALFileSystem().exists(remoteWAL));
        SyncReplicationTestBase.UTIL1.getAdmin().enableReplicationPeer(SyncReplicationTestBase.PEER_ID);
        waitUntilReplicationDone(SyncReplicationTestBase.UTIL2, 200);
        verifyThroughRegion(SyncReplicationTestBase.UTIL2, 100, 200);
        // Confirm that we will also remove the remote wal files in DA state
        waitUntilDeleted(SyncReplicationTestBase.UTIL2, remoteWAL);
    }
}

