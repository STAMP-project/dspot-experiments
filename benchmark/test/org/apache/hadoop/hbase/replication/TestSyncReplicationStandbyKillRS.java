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


import JVMClusterUtil.MasterThread;
import JVMClusterUtil.RegionServerThread;
import SyncReplicationState.ACTIVE;
import SyncReplicationState.DOWNGRADE_ACTIVE;
import SyncReplicationState.STANDBY;
import java.util.List;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.ServerName;
import org.apache.hadoop.hbase.master.MasterFileSystem;
import org.apache.hadoop.hbase.testclassification.LargeTests;
import org.apache.hadoop.hbase.testclassification.ReplicationTests;
import org.apache.hadoop.hbase.util.JVMClusterUtil;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static SyncReplicationState.DOWNGRADE_ACTIVE;


@Category({ ReplicationTests.class, LargeTests.class })
public class TestSyncReplicationStandbyKillRS extends SyncReplicationTestBase {
    private static final Logger LOG = LoggerFactory.getLogger(TestSyncReplicationStandbyKillRS.class);

    private final long SLEEP_TIME = 1000;

    private final int COUNT = 1000;

    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestSyncReplicationStandbyKillRS.class);

    @Test
    public void testStandbyKillRegionServer() throws Exception {
        MasterFileSystem mfs = SyncReplicationTestBase.UTIL2.getHBaseCluster().getMaster().getMasterFileSystem();
        Path remoteWALDir = getRemoteWALDir(mfs, SyncReplicationTestBase.PEER_ID);
        Assert.assertFalse(mfs.getWALFileSystem().exists(remoteWALDir));
        SyncReplicationTestBase.UTIL2.getAdmin().transitReplicationPeerSyncReplicationState(SyncReplicationTestBase.PEER_ID, STANDBY);
        Assert.assertTrue(mfs.getWALFileSystem().exists(remoteWALDir));
        SyncReplicationTestBase.UTIL1.getAdmin().transitReplicationPeerSyncReplicationState(SyncReplicationTestBase.PEER_ID, ACTIVE);
        // Disable async replication and write data, then shutdown
        SyncReplicationTestBase.UTIL1.getAdmin().disableReplicationPeer(SyncReplicationTestBase.PEER_ID);
        write(SyncReplicationTestBase.UTIL1, 0, COUNT);
        SyncReplicationTestBase.UTIL1.shutdownMiniCluster();
        JVMClusterUtil.MasterThread activeMaster = SyncReplicationTestBase.UTIL2.getMiniHBaseCluster().getMasterThread();
        Thread t = new Thread(() -> {
            try {
                List<JVMClusterUtil.RegionServerThread> regionServers = SyncReplicationTestBase.UTIL2.getMiniHBaseCluster().getLiveRegionServerThreads();
                for (JVMClusterUtil.RegionServerThread rst : regionServers) {
                    ServerName serverName = rst.getRegionServer().getServerName();
                    rst.getRegionServer().stop("Stop RS for test");
                    waitForRSShutdownToStartAndFinish(activeMaster, serverName);
                    JVMClusterUtil.RegionServerThread restarted = SyncReplicationTestBase.UTIL2.getMiniHBaseCluster().startRegionServer();
                    restarted.waitForServerOnline();
                }
            } catch (Exception e) {
                TestSyncReplicationStandbyKillRS.LOG.error("Failed to kill RS", e);
            }
        });
        t.start();
        // Transit standby to DA to replay logs
        try {
            SyncReplicationTestBase.UTIL2.getAdmin().transitReplicationPeerSyncReplicationState(SyncReplicationTestBase.PEER_ID, DOWNGRADE_ACTIVE);
        } catch (Exception e) {
            TestSyncReplicationStandbyKillRS.LOG.error(("Failed to transit standby cluster to " + (DOWNGRADE_ACTIVE)), e);
        }
        while ((SyncReplicationTestBase.UTIL2.getAdmin().getReplicationPeerSyncReplicationState(SyncReplicationTestBase.PEER_ID)) != (DOWNGRADE_ACTIVE)) {
            Thread.sleep(SLEEP_TIME);
        } 
        verify(SyncReplicationTestBase.UTIL2, 0, COUNT);
    }
}

