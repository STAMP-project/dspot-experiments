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


import HConstants.NO_SEQNUM;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.HBaseZKTestingUtility;
import org.apache.hadoop.hbase.ServerName;
import org.apache.hadoop.hbase.testclassification.MediumTests;
import org.apache.hadoop.hbase.testclassification.ReplicationTests;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.hbase.util.MD5Hash;
import org.apache.hadoop.hbase.util.Pair;
import org.apache.hbase.thirdparty.com.google.common.collect.ImmutableMap;
import org.apache.zookeeper.KeeperException;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.experimental.categories.Category;


@Category({ ReplicationTests.class, MediumTests.class })
public class TestZKReplicationQueueStorage {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestZKReplicationQueueStorage.class);

    private static final HBaseZKTestingUtility UTIL = new HBaseZKTestingUtility();

    private static ZKReplicationQueueStorage STORAGE;

    @Test
    public void testReplicator() throws ReplicationException {
        Assert.assertTrue(TestZKReplicationQueueStorage.STORAGE.getListOfReplicators().isEmpty());
        String queueId = "1";
        for (int i = 0; i < 10; i++) {
            TestZKReplicationQueueStorage.STORAGE.addWAL(getServerName(i), queueId, ("file" + i));
        }
        List<ServerName> replicators = TestZKReplicationQueueStorage.STORAGE.getListOfReplicators();
        Assert.assertEquals(10, replicators.size());
        for (int i = 0; i < 10; i++) {
            Assert.assertThat(replicators, CoreMatchers.hasItems(getServerName(i)));
        }
        for (int i = 0; i < 5; i++) {
            TestZKReplicationQueueStorage.STORAGE.removeQueue(getServerName(i), queueId);
        }
        for (int i = 0; i < 10; i++) {
            TestZKReplicationQueueStorage.STORAGE.removeReplicatorIfQueueIsEmpty(getServerName(i));
        }
        replicators = TestZKReplicationQueueStorage.STORAGE.getListOfReplicators();
        Assert.assertEquals(5, replicators.size());
        for (int i = 5; i < 10; i++) {
            Assert.assertThat(replicators, CoreMatchers.hasItems(getServerName(i)));
        }
    }

    @Test
    public void testAddRemoveLog() throws ReplicationException {
        ServerName serverName1 = ServerName.valueOf("127.0.0.1", 8000, 10000);
        Assert.assertTrue(TestZKReplicationQueueStorage.STORAGE.getAllQueues(serverName1).isEmpty());
        String queue1 = "1";
        String queue2 = "2";
        for (int i = 0; i < 10; i++) {
            TestZKReplicationQueueStorage.STORAGE.addWAL(serverName1, queue1, getFileName("file1", i));
            TestZKReplicationQueueStorage.STORAGE.addWAL(serverName1, queue2, getFileName("file2", i));
        }
        List<String> queueIds = TestZKReplicationQueueStorage.STORAGE.getAllQueues(serverName1);
        Assert.assertEquals(2, queueIds.size());
        Assert.assertThat(queueIds, CoreMatchers.hasItems("1", "2"));
        List<String> wals1 = TestZKReplicationQueueStorage.STORAGE.getWALsInQueue(serverName1, queue1);
        List<String> wals2 = TestZKReplicationQueueStorage.STORAGE.getWALsInQueue(serverName1, queue2);
        Assert.assertEquals(10, wals1.size());
        Assert.assertEquals(10, wals2.size());
        for (int i = 0; i < 10; i++) {
            Assert.assertThat(wals1, CoreMatchers.hasItems(getFileName("file1", i)));
            Assert.assertThat(wals2, CoreMatchers.hasItems(getFileName("file2", i)));
        }
        for (int i = 0; i < 10; i++) {
            Assert.assertEquals(0, TestZKReplicationQueueStorage.STORAGE.getWALPosition(serverName1, queue1, getFileName("file1", i)));
            Assert.assertEquals(0, TestZKReplicationQueueStorage.STORAGE.getWALPosition(serverName1, queue2, getFileName("file2", i)));
            TestZKReplicationQueueStorage.STORAGE.setWALPosition(serverName1, queue1, getFileName("file1", i), ((i + 1) * 100), Collections.emptyMap());
            TestZKReplicationQueueStorage.STORAGE.setWALPosition(serverName1, queue2, getFileName("file2", i), (((i + 1) * 100) + 10), Collections.emptyMap());
        }
        for (int i = 0; i < 10; i++) {
            Assert.assertEquals(((i + 1) * 100), TestZKReplicationQueueStorage.STORAGE.getWALPosition(serverName1, queue1, getFileName("file1", i)));
            Assert.assertEquals((((i + 1) * 100) + 10), TestZKReplicationQueueStorage.STORAGE.getWALPosition(serverName1, queue2, getFileName("file2", i)));
        }
        for (int i = 0; i < 10; i++) {
            if ((i % 2) == 0) {
                TestZKReplicationQueueStorage.STORAGE.removeWAL(serverName1, queue1, getFileName("file1", i));
            } else {
                TestZKReplicationQueueStorage.STORAGE.removeWAL(serverName1, queue2, getFileName("file2", i));
            }
        }
        queueIds = TestZKReplicationQueueStorage.STORAGE.getAllQueues(serverName1);
        Assert.assertEquals(2, queueIds.size());
        Assert.assertThat(queueIds, CoreMatchers.hasItems("1", "2"));
        ServerName serverName2 = ServerName.valueOf("127.0.0.1", 8001, 10001);
        Pair<String, SortedSet<String>> peer1 = TestZKReplicationQueueStorage.STORAGE.claimQueue(serverName1, "1", serverName2);
        Assert.assertEquals(("1-" + (serverName1.getServerName())), peer1.getFirst());
        Assert.assertEquals(5, peer1.getSecond().size());
        int i = 1;
        for (String wal : peer1.getSecond()) {
            Assert.assertEquals(getFileName("file1", i), wal);
            Assert.assertEquals(((i + 1) * 100), TestZKReplicationQueueStorage.STORAGE.getWALPosition(serverName2, peer1.getFirst(), getFileName("file1", i)));
            i += 2;
        }
        queueIds = TestZKReplicationQueueStorage.STORAGE.getAllQueues(serverName1);
        Assert.assertEquals(1, queueIds.size());
        Assert.assertThat(queueIds, CoreMatchers.hasItems("2"));
        wals2 = TestZKReplicationQueueStorage.STORAGE.getWALsInQueue(serverName1, queue2);
        Assert.assertEquals(5, wals2.size());
        for (i = 0; i < 10; i += 2) {
            Assert.assertThat(wals2, CoreMatchers.hasItems(getFileName("file2", i)));
        }
        queueIds = TestZKReplicationQueueStorage.STORAGE.getAllQueues(serverName2);
        Assert.assertEquals(1, queueIds.size());
        Assert.assertThat(queueIds, CoreMatchers.hasItems(peer1.getFirst()));
        wals1 = TestZKReplicationQueueStorage.STORAGE.getWALsInQueue(serverName2, peer1.getFirst());
        Assert.assertEquals(5, wals1.size());
        for (i = 1; i < 10; i += 2) {
            Assert.assertThat(wals1, CoreMatchers.hasItems(getFileName("file1", i)));
        }
        Set<String> allWals = TestZKReplicationQueueStorage.STORAGE.getAllWALs();
        Assert.assertEquals(10, allWals.size());
        for (i = 0; i < 10; i++) {
            Assert.assertThat(allWals, CoreMatchers.hasItems(((i % 2) == 0 ? getFileName("file2", i) : getFileName("file1", i))));
        }
    }

    // For HBASE-12865
    @Test
    public void testClaimQueueChangeCversion() throws ReplicationException, KeeperException {
        ServerName serverName1 = ServerName.valueOf("127.0.0.1", 8000, 10000);
        TestZKReplicationQueueStorage.STORAGE.addWAL(serverName1, "1", "file");
        int v0 = TestZKReplicationQueueStorage.STORAGE.getQueuesZNodeCversion();
        ServerName serverName2 = ServerName.valueOf("127.0.0.1", 8001, 10001);
        TestZKReplicationQueueStorage.STORAGE.claimQueue(serverName1, "1", serverName2);
        int v1 = TestZKReplicationQueueStorage.STORAGE.getQueuesZNodeCversion();
        // cversion should increase by 1 since a child node is deleted
        Assert.assertEquals(1, (v1 - v0));
    }

    @Test
    public void testGetAllWALsCversionChange() throws IOException, ReplicationException {
        ZKReplicationQueueStorage storage = createWithUnstableVersion();
        storage.addWAL(getServerName(0), "1", "file");
        // This should return eventually when cversion stabilizes
        Set<String> allWals = storage.getAllWALs();
        Assert.assertEquals(1, allWals.size());
        Assert.assertThat(allWals, CoreMatchers.hasItems("file"));
    }

    // For HBASE-14621
    @Test
    public void testGetAllHFileRefsCversionChange() throws IOException, ReplicationException {
        ZKReplicationQueueStorage storage = createWithUnstableVersion();
        storage.addPeerToHFileRefs("1");
        Path p = new Path("/test");
        storage.addHFileRefs("1", Arrays.asList(Pair.newPair(p, p)));
        // This should return eventually when cversion stabilizes
        Set<String> allHFileRefs = storage.getAllHFileRefs();
        Assert.assertEquals(1, allHFileRefs.size());
        Assert.assertThat(allHFileRefs, CoreMatchers.hasItems("test"));
    }

    // For HBASE-20138
    @Test
    public void testSetWALPositionBadVersion() throws IOException, ReplicationException {
        ZKReplicationQueueStorage storage = createWithUnstableVersion();
        ServerName serverName1 = ServerName.valueOf("128.0.0.1", 8000, 10000);
        Assert.assertTrue(storage.getAllQueues(serverName1).isEmpty());
        String queue1 = "1";
        String fileName = getFileName("file1", 0);
        String encodedRegionName = "31d9792f4435b99d9fb1016f6fbc8dc6";
        storage.addWAL(serverName1, queue1, fileName);
        List<String> wals1 = storage.getWALsInQueue(serverName1, queue1);
        Assert.assertEquals(1, wals1.size());
        Assert.assertEquals(0, storage.getWALPosition(serverName1, queue1, fileName));
        // This should return eventually when data version stabilizes
        storage.setWALPosition(serverName1, queue1, fileName, 100, ImmutableMap.of(encodedRegionName, 120L));
        Assert.assertEquals(100, storage.getWALPosition(serverName1, queue1, fileName));
        Assert.assertEquals(120L, storage.getLastSequenceId(encodedRegionName, queue1));
    }

    @Test
    public void testRegionsZNodeLayout() throws Exception {
        String peerId = "1";
        String encodedRegionName = "31d9792f4435b99d9fb1016f6fbc8dc7";
        String expectedPath = "/hbase/replication/regions/31/d9/792f4435b99d9fb1016f6fbc8dc7-" + peerId;
        String path = TestZKReplicationQueueStorage.STORAGE.getSerialReplicationRegionPeerNode(encodedRegionName, peerId);
        Assert.assertEquals(expectedPath, path);
    }

    @Test
    public void testRemoveAllLastPushedSeqIdsForPeer() throws Exception {
        String peerId = "1";
        String peerIdToDelete = "2";
        for (int i = 0; i < 100; i++) {
            String encodedRegionName = MD5Hash.getMD5AsHex(Bytes.toBytes(i));
            TestZKReplicationQueueStorage.STORAGE.setLastSequenceIds(peerId, ImmutableMap.of(encodedRegionName, ((long) (i))));
            TestZKReplicationQueueStorage.STORAGE.setLastSequenceIds(peerIdToDelete, ImmutableMap.of(encodedRegionName, ((long) (i))));
        }
        for (int i = 0; i < 100; i++) {
            String encodedRegionName = MD5Hash.getMD5AsHex(Bytes.toBytes(i));
            Assert.assertEquals(i, TestZKReplicationQueueStorage.STORAGE.getLastSequenceId(encodedRegionName, peerId));
            Assert.assertEquals(i, TestZKReplicationQueueStorage.STORAGE.getLastSequenceId(encodedRegionName, peerIdToDelete));
        }
        TestZKReplicationQueueStorage.STORAGE.removeLastSequenceIds(peerIdToDelete);
        for (int i = 0; i < 100; i++) {
            String encodedRegionName = MD5Hash.getMD5AsHex(Bytes.toBytes(i));
            Assert.assertEquals(i, TestZKReplicationQueueStorage.STORAGE.getLastSequenceId(encodedRegionName, peerId));
            Assert.assertEquals(NO_SEQNUM, TestZKReplicationQueueStorage.STORAGE.getLastSequenceId(encodedRegionName, peerIdToDelete));
        }
    }
}

