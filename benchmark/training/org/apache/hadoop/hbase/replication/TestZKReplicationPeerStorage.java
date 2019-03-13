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


import SyncReplicationState.NONE;
import java.io.IOException;
import java.util.List;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.HBaseZKTestingUtility;
import org.apache.hadoop.hbase.testclassification.MediumTests;
import org.apache.hadoop.hbase.testclassification.ReplicationTests;
import org.apache.hadoop.hbase.zookeeper.ZKUtil;
import org.apache.zookeeper.KeeperException;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.experimental.categories.Category;


@Category({ ReplicationTests.class, MediumTests.class })
public class TestZKReplicationPeerStorage {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestZKReplicationPeerStorage.class);

    private static final HBaseZKTestingUtility UTIL = new HBaseZKTestingUtility();

    private static ZKReplicationPeerStorage STORAGE;

    @Test
    public void test() throws ReplicationException {
        int peerCount = 10;
        for (int i = 0; i < peerCount; i++) {
            TestZKReplicationPeerStorage.STORAGE.addPeer(Integer.toString(i), getConfig(i), ((i % 2) == 0), SyncReplicationState.valueOf((i % 4)));
        }
        List<String> peerIds = TestZKReplicationPeerStorage.STORAGE.listPeerIds();
        Assert.assertEquals(peerCount, peerIds.size());
        for (String peerId : peerIds) {
            int seed = Integer.parseInt(peerId);
            assertConfigEquals(getConfig(seed), TestZKReplicationPeerStorage.STORAGE.getPeerConfig(peerId));
        }
        for (int i = 0; i < peerCount; i++) {
            TestZKReplicationPeerStorage.STORAGE.updatePeerConfig(Integer.toString(i), getConfig((i + 1)));
        }
        for (String peerId : peerIds) {
            int seed = Integer.parseInt(peerId);
            assertConfigEquals(getConfig((seed + 1)), TestZKReplicationPeerStorage.STORAGE.getPeerConfig(peerId));
        }
        for (int i = 0; i < peerCount; i++) {
            Assert.assertEquals(((i % 2) == 0), TestZKReplicationPeerStorage.STORAGE.isPeerEnabled(Integer.toString(i)));
        }
        for (int i = 0; i < peerCount; i++) {
            TestZKReplicationPeerStorage.STORAGE.setPeerState(Integer.toString(i), ((i % 2) != 0));
        }
        for (int i = 0; i < peerCount; i++) {
            Assert.assertEquals(((i % 2) != 0), TestZKReplicationPeerStorage.STORAGE.isPeerEnabled(Integer.toString(i)));
        }
        for (int i = 0; i < peerCount; i++) {
            Assert.assertEquals(SyncReplicationState.valueOf((i % 4)), TestZKReplicationPeerStorage.STORAGE.getPeerSyncReplicationState(Integer.toString(i)));
        }
        String toRemove = Integer.toString((peerCount / 2));
        TestZKReplicationPeerStorage.STORAGE.removePeer(toRemove);
        peerIds = TestZKReplicationPeerStorage.STORAGE.listPeerIds();
        Assert.assertEquals((peerCount - 1), peerIds.size());
        Assert.assertFalse(peerIds.contains(toRemove));
        try {
            TestZKReplicationPeerStorage.STORAGE.getPeerConfig(toRemove);
            Assert.fail("Should throw a ReplicationException when getting peer config of a removed peer");
        } catch (ReplicationException e) {
        }
    }

    @Test
    public void testNoSyncReplicationState() throws IOException, ReplicationException, KeeperException {
        // This could happen for a peer created before we introduce sync replication.
        String peerId = "testNoSyncReplicationState";
        try {
            TestZKReplicationPeerStorage.STORAGE.getPeerSyncReplicationState(peerId);
            Assert.fail("Should throw a ReplicationException when getting state of inexist peer");
        } catch (ReplicationException e) {
            // expected
        }
        try {
            TestZKReplicationPeerStorage.STORAGE.getPeerNewSyncReplicationState(peerId);
            Assert.fail("Should throw a ReplicationException when getting state of inexist peer");
        } catch (ReplicationException e) {
            // expected
        }
        TestZKReplicationPeerStorage.STORAGE.addPeer(peerId, getConfig(0), true, NONE);
        // delete the sync replication state node to simulate
        ZKUtil.deleteNode(TestZKReplicationPeerStorage.UTIL.getZooKeeperWatcher(), TestZKReplicationPeerStorage.STORAGE.getSyncReplicationStateNode(peerId));
        ZKUtil.deleteNode(TestZKReplicationPeerStorage.UTIL.getZooKeeperWatcher(), TestZKReplicationPeerStorage.STORAGE.getNewSyncReplicationStateNode(peerId));
        // should not throw exception as the peer exists
        Assert.assertEquals(NONE, TestZKReplicationPeerStorage.STORAGE.getPeerSyncReplicationState(peerId));
        Assert.assertEquals(NONE, TestZKReplicationPeerStorage.STORAGE.getPeerNewSyncReplicationState(peerId));
        // make sure we create the node for the old format peer
        Assert.assertNotEquals((-1), ZKUtil.checkExists(TestZKReplicationPeerStorage.UTIL.getZooKeeperWatcher(), TestZKReplicationPeerStorage.STORAGE.getSyncReplicationStateNode(peerId)));
        Assert.assertNotEquals((-1), ZKUtil.checkExists(TestZKReplicationPeerStorage.UTIL.getZooKeeperWatcher(), TestZKReplicationPeerStorage.STORAGE.getNewSyncReplicationStateNode(peerId)));
    }
}

