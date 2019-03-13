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
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.master.MasterFileSystem;
import org.apache.hadoop.hbase.testclassification.LargeTests;
import org.apache.hadoop.hbase.testclassification.ReplicationTests;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static SyncReplicationState.DOWNGRADE_ACTIVE;


@Category({ ReplicationTests.class, LargeTests.class })
public class TestSyncReplicationStandbyKillMaster extends SyncReplicationTestBase {
    private static final Logger LOG = LoggerFactory.getLogger(TestSyncReplicationStandbyKillMaster.class);

    private final long SLEEP_TIME = 2000;

    private final int COUNT = 1000;

    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestSyncReplicationStandbyKillMaster.class);

    @Test
    public void testStandbyKillMaster() throws Exception {
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
        Thread t = new Thread(() -> {
            try {
                Thread.sleep(SLEEP_TIME);
                SyncReplicationTestBase.UTIL2.getMiniHBaseCluster().getMaster().stop("Stop master for test");
            } catch (Exception e) {
                TestSyncReplicationStandbyKillMaster.LOG.error("Failed to stop master", e);
            }
        });
        t.start();
        // Transit standby to DA to replay logs
        try {
            SyncReplicationTestBase.UTIL2.getAdmin().transitReplicationPeerSyncReplicationState(SyncReplicationTestBase.PEER_ID, DOWNGRADE_ACTIVE);
        } catch (Exception e) {
            TestSyncReplicationStandbyKillMaster.LOG.error(("Failed to transit standby cluster to " + (DOWNGRADE_ACTIVE)));
        }
        while ((SyncReplicationTestBase.UTIL2.getAdmin().getReplicationPeerSyncReplicationState(SyncReplicationTestBase.PEER_ID)) != (DOWNGRADE_ACTIVE)) {
            Thread.sleep(SLEEP_TIME);
        } 
        verify(SyncReplicationTestBase.UTIL2, 0, COUNT);
    }
}

