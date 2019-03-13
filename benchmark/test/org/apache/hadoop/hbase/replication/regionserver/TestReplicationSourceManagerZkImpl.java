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
package org.apache.hadoop.hbase.replication.regionserver;


import java.util.List;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.Server;
import org.apache.hadoop.hbase.ServerName;
import org.apache.hadoop.hbase.replication.ReplicationQueueInfo;
import org.apache.hadoop.hbase.replication.ReplicationQueueStorage;
import org.apache.hadoop.hbase.replication.ReplicationStorageFactory;
import org.apache.hadoop.hbase.testclassification.MediumTests;
import org.apache.hadoop.hbase.testclassification.ReplicationTests;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.experimental.categories.Category;


/**
 * Tests the ReplicationSourceManager with ReplicationQueueZkImpl's and
 * ReplicationQueuesClientZkImpl. Also includes extra tests outside of those in
 * TestReplicationSourceManager that test ReplicationQueueZkImpl-specific behaviors.
 */
@Category({ ReplicationTests.class, MediumTests.class })
public class TestReplicationSourceManagerZkImpl extends TestReplicationSourceManager {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestReplicationSourceManagerZkImpl.class);

    // Tests the naming convention of adopted queues for ReplicationQueuesZkImpl
    @Test
    public void testNodeFailoverDeadServerParsing() throws Exception {
        Server server = new TestReplicationSourceManager.DummyServer("ec2-54-234-230-108.compute-1.amazonaws.com");
        ReplicationQueueStorage queueStorage = ReplicationStorageFactory.getReplicationQueueStorage(server.getZooKeeper(), TestReplicationSourceManager.conf);
        // populate some znodes in the peer znode
        TestReplicationSourceManager.files.add("log1");
        TestReplicationSourceManager.files.add("log2");
        for (String file : TestReplicationSourceManager.files) {
            queueStorage.addWAL(server.getServerName(), "1", file);
        }
        // create 3 DummyServers
        Server s1 = new TestReplicationSourceManager.DummyServer("ip-10-8-101-114.ec2.internal");
        Server s2 = new TestReplicationSourceManager.DummyServer("ec2-107-20-52-47.compute-1.amazonaws.com");
        Server s3 = new TestReplicationSourceManager.DummyServer("ec2-23-20-187-167.compute-1.amazonaws.com");
        // simulate three servers fail sequentially
        ServerName serverName = server.getServerName();
        List<String> unclaimed = queueStorage.getAllQueues(serverName);
        queueStorage.claimQueue(serverName, unclaimed.get(0), s1.getServerName());
        queueStorage.removeReplicatorIfQueueIsEmpty(serverName);
        serverName = s1.getServerName();
        unclaimed = queueStorage.getAllQueues(serverName);
        queueStorage.claimQueue(serverName, unclaimed.get(0), s2.getServerName());
        queueStorage.removeReplicatorIfQueueIsEmpty(serverName);
        serverName = s2.getServerName();
        unclaimed = queueStorage.getAllQueues(serverName);
        String queue3 = queueStorage.claimQueue(serverName, unclaimed.get(0), s3.getServerName()).getFirst();
        queueStorage.removeReplicatorIfQueueIsEmpty(serverName);
        ReplicationQueueInfo replicationQueueInfo = new ReplicationQueueInfo(queue3);
        List<ServerName> result = replicationQueueInfo.getDeadRegionServers();
        // verify
        Assert.assertTrue(result.contains(server.getServerName()));
        Assert.assertTrue(result.contains(s1.getServerName()));
        Assert.assertTrue(result.contains(s2.getServerName()));
        server.stop("");
    }
}

