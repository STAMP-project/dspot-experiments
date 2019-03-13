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
package org.apache.hadoop.hbase.util;


import SyncReplicationState.NONE;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Stream;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.HBaseTestingUtility;
import org.apache.hadoop.hbase.ServerName;
import org.apache.hadoop.hbase.replication.ReplicationPeerConfig;
import org.apache.hadoop.hbase.replication.ReplicationPeerStorage;
import org.apache.hadoop.hbase.replication.ReplicationQueueStorage;
import org.apache.hadoop.hbase.replication.ReplicationStorageFactory;
import org.apache.hadoop.hbase.testclassification.MediumTests;
import org.apache.hadoop.hbase.testclassification.ReplicationTests;
import org.apache.hadoop.hbase.util.HBaseFsck.ErrorReporter.ERROR_CODE;
import org.apache.hadoop.hbase.util.hbck.HbckTestingUtil;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.experimental.categories.Category;


@Category({ ReplicationTests.class, MediumTests.class })
public class TestHBaseFsckReplication {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestHBaseFsckReplication.class);

    private static final HBaseTestingUtility UTIL = new HBaseTestingUtility();

    @Test
    public void test() throws Exception {
        ReplicationPeerStorage peerStorage = ReplicationStorageFactory.getReplicationPeerStorage(getZooKeeperWatcher(), TestHBaseFsckReplication.UTIL.getConfiguration());
        ReplicationQueueStorage queueStorage = ReplicationStorageFactory.getReplicationQueueStorage(getZooKeeperWatcher(), TestHBaseFsckReplication.UTIL.getConfiguration());
        String peerId1 = "1";
        String peerId2 = "2";
        peerStorage.addPeer(peerId1, ReplicationPeerConfig.newBuilder().setClusterKey("key").build(), true, NONE);
        peerStorage.addPeer(peerId2, ReplicationPeerConfig.newBuilder().setClusterKey("key").build(), true, NONE);
        for (int i = 0; i < 10; i++) {
            queueStorage.addWAL(ServerName.valueOf("localhost", (10000 + i), (100000 + i)), peerId1, ("file-" + i));
        }
        queueStorage.addWAL(ServerName.valueOf("localhost", 10000, 100000), peerId2, "file");
        HBaseFsck fsck = HbckTestingUtil.doFsck(TestHBaseFsckReplication.UTIL.getConfiguration(), true);
        HbckTestingUtil.assertNoErrors(fsck);
        // should not remove anything since the replication peer is still alive
        Assert.assertEquals(10, queueStorage.getListOfReplicators().size());
        peerStorage.removePeer(peerId1);
        // there should be orphan queues
        Assert.assertEquals(10, queueStorage.getListOfReplicators().size());
        fsck = HbckTestingUtil.doFsck(TestHBaseFsckReplication.UTIL.getConfiguration(), false);
        HbckTestingUtil.assertErrors(fsck, Stream.generate(() -> {
            return ERROR_CODE.UNDELETED_REPLICATION_QUEUE;
        }).limit(10).toArray(ERROR_CODE[]::new));
        // should not delete anything when fix is false
        Assert.assertEquals(10, queueStorage.getListOfReplicators().size());
        fsck = HbckTestingUtil.doFsck(TestHBaseFsckReplication.UTIL.getConfiguration(), true);
        HbckTestingUtil.assertErrors(fsck, Stream.generate(() -> {
            return ERROR_CODE.UNDELETED_REPLICATION_QUEUE;
        }).limit(10).toArray(ERROR_CODE[]::new));
        List<ServerName> replicators = queueStorage.getListOfReplicators();
        // should not remove the server with queue for peerId2
        Assert.assertEquals(1, replicators.size());
        Assert.assertEquals(ServerName.valueOf("localhost", 10000, 100000), replicators.get(0));
        for (String queueId : queueStorage.getAllQueues(replicators.get(0))) {
            Assert.assertEquals(peerId2, queueId);
        }
    }
}

