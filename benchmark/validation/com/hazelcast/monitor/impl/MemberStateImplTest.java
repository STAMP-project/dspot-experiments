/**
 * Copyright (c) 2008-2019, Hazelcast, Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.hazelcast.monitor.impl;


import ClusterHotRestartStatusDTO.ClusterHotRestartStatus.UNKNOWN;
import EndpointQualifier.MEMBER;
import ProtocolType.WAN;
import WanSyncStatus.IN_PROGRESS;
import com.hazelcast.cache.impl.CacheStatisticsImpl;
import com.hazelcast.cluster.ClusterState;
import com.hazelcast.config.HotRestartClusterDataRecoveryPolicy;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.hotrestart.BackupTaskState;
import com.hazelcast.hotrestart.BackupTaskStatus;
import com.hazelcast.instance.EndpointQualifier;
import com.hazelcast.internal.management.TimedMemberState;
import com.hazelcast.internal.management.TimedMemberStateFactory;
import com.hazelcast.internal.management.dto.ClientEndPointDTO;
import com.hazelcast.internal.management.dto.ClusterHotRestartStatusDTO;
import com.hazelcast.monitor.HotRestartState;
import com.hazelcast.monitor.NodeState;
import com.hazelcast.monitor.WanSyncState;
import com.hazelcast.monitor.com.hazelcast.instance.NodeState;
import com.hazelcast.nio.Address;
import com.hazelcast.test.HazelcastParallelClassRunner;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import com.hazelcast.util.Clock;
import com.hazelcast.version.MemberVersion;
import com.hazelcast.version.Version;
import com.hazelcast.wan.WanSyncStatus;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.UUID;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;


@RunWith(HazelcastParallelClassRunner.class)
@Category({ QuickTest.class, ParallelTest.class })
public class MemberStateImplTest extends HazelcastTestSupport {
    @Test
    public void testDefaultConstructor() {
        MemberStateImpl memberState = new MemberStateImpl();
        Assert.assertNotNull(memberState.toString());
    }

    @Test
    public void testSerialization() throws UnknownHostException {
        HazelcastInstance hazelcastInstance = createHazelcastInstance();
        LocalReplicatedMapStatsImpl replicatedMapStats = new LocalReplicatedMapStatsImpl();
        replicatedMapStats.incrementPuts(30);
        CacheStatisticsImpl cacheStatistics = new CacheStatisticsImpl(Clock.currentTimeMillis());
        cacheStatistics.increaseCacheHits(5);
        Collection<ClientEndPointDTO> clients = new ArrayList<ClientEndPointDTO>();
        ClientEndPointDTO client = new ClientEndPointDTO();
        client.uuid = "abc123456";
        client.address = "localhost";
        client.clientType = "undefined";
        client.name = "aClient";
        client.labels = new HashSet<String>(Collections.singletonList("label"));
        clients.add(client);
        Map<String, Long> runtimeProps = new HashMap<String, Long>();
        runtimeProps.put("prop1", 598123L);
        ClusterState clusterState = ClusterState.ACTIVE;
        com.hazelcast.instance.NodeState nodeState = NodeState.PASSIVE;
        Version clusterVersion = Version.of("3.9.0");
        MemberVersion memberVersion = MemberVersion.of("3.8.0");
        NodeState state = new NodeStateImpl(clusterState, nodeState, clusterVersion, memberVersion);
        final BackupTaskStatus backupTaskStatus = new BackupTaskStatus(BackupTaskState.IN_PROGRESS, 5, 10);
        final String backupDirectory = "/hot/backup/dir";
        final HotRestartStateImpl hotRestartState = new HotRestartStateImpl(backupTaskStatus, true, backupDirectory);
        final WanSyncState wanSyncState = new WanSyncStateImpl(WanSyncStatus.IN_PROGRESS, 86, "atob", "B");
        Map<String, String> clientStats = new HashMap<String, String>();
        clientStats.put("abc123456", "someStats");
        Map<EndpointQualifier, Address> endpoints = new HashMap<EndpointQualifier, Address>();
        endpoints.put(MEMBER, new Address("127.0.0.1", 5701));
        endpoints.put(EndpointQualifier.resolve(WAN, "MyWAN"), new Address("127.0.0.1", 5901));
        TimedMemberStateFactory factory = new TimedMemberStateFactory(HazelcastTestSupport.getHazelcastInstanceImpl(hazelcastInstance));
        TimedMemberState timedMemberState = factory.createTimedMemberState();
        MemberStateImpl memberState = timedMemberState.getMemberState();
        memberState.setAddress("memberStateAddress:Port");
        String uuid = UUID.randomUUID().toString();
        memberState.setUuid(uuid);
        String cpMemberUuid = UUID.randomUUID().toString();
        memberState.setCpMemberUuid(cpMemberUuid);
        memberState.setEndpoints(endpoints);
        memberState.putLocalMapStats("mapStats", new LocalMapStatsImpl());
        memberState.putLocalMultiMapStats("multiMapStats", new LocalMultiMapStatsImpl());
        memberState.putLocalQueueStats("queueStats", new LocalQueueStatsImpl());
        memberState.putLocalTopicStats("topicStats", new LocalTopicStatsImpl());
        memberState.putLocalReliableTopicStats("reliableTopicStats", new LocalTopicStatsImpl());
        memberState.putLocalPNCounterStats("pnCounterStats", new LocalPNCounterStatsImpl());
        memberState.putLocalExecutorStats("executorStats", new LocalExecutorStatsImpl());
        memberState.putLocalReplicatedMapStats("replicatedMapStats", replicatedMapStats);
        memberState.putLocalCacheStats("cacheStats", new LocalCacheStatsImpl(cacheStatistics));
        memberState.putLocalFlakeIdStats("flakeIdStats", new LocalFlakeIdGeneratorStatsImpl());
        memberState.setRuntimeProps(runtimeProps);
        memberState.setLocalMemoryStats(new LocalMemoryStatsImpl());
        memberState.setOperationStats(new LocalOperationStatsImpl());
        memberState.setClients(clients);
        memberState.setNodeState(state);
        memberState.setHotRestartState(hotRestartState);
        memberState.setWanSyncState(wanSyncState);
        memberState.setClientStats(clientStats);
        MemberStateImpl deserialized = new MemberStateImpl();
        deserialized.fromJson(memberState.toJson());
        Assert.assertEquals("memberStateAddress:Port", deserialized.getAddress());
        Assert.assertEquals(uuid, deserialized.getUuid());
        Assert.assertEquals(cpMemberUuid, deserialized.getCpMemberUuid());
        Assert.assertEquals(endpoints, deserialized.getEndpoints());
        Assert.assertNotNull(deserialized.getLocalMapStats("mapStats").toString());
        Assert.assertNotNull(deserialized.getLocalMultiMapStats("multiMapStats").toString());
        Assert.assertNotNull(deserialized.getLocalQueueStats("queueStats").toString());
        Assert.assertNotNull(deserialized.getLocalTopicStats("topicStats").toString());
        Assert.assertNotNull(deserialized.getReliableLocalTopicStats("reliableTopicStats").toString());
        Assert.assertNotNull(deserialized.getLocalPNCounterStats("pnCounterStats").toString());
        Assert.assertNotNull(deserialized.getLocalExecutorStats("executorStats").toString());
        Assert.assertNotNull(deserialized.getLocalReplicatedMapStats("replicatedMapStats").toString());
        Assert.assertEquals(1, deserialized.getLocalReplicatedMapStats("replicatedMapStats").getPutOperationCount());
        Assert.assertNotNull(deserialized.getLocalCacheStats("cacheStats").toString());
        Assert.assertNotNull(deserialized.getLocalFlakeIdGeneratorStats("flakeIdStats").toString());
        Assert.assertEquals(5, deserialized.getLocalCacheStats("cacheStats").getCacheHits());
        Assert.assertNotNull(deserialized.getRuntimeProps());
        Assert.assertEquals(Long.valueOf(598123L), deserialized.getRuntimeProps().get("prop1"));
        Assert.assertNotNull(deserialized.getLocalMemoryStats());
        Assert.assertNotNull(deserialized.getOperationStats());
        Assert.assertNotNull(deserialized.getMXBeans());
        client = deserialized.getClients().iterator().next();
        Assert.assertEquals("abc123456", client.uuid);
        Assert.assertEquals("localhost", client.address);
        Assert.assertEquals("undefined", client.clientType);
        Assert.assertEquals("aClient", client.name);
        HazelcastTestSupport.assertContains(client.labels, "label");
        NodeState deserializedState = deserialized.getNodeState();
        Assert.assertEquals(clusterState, deserializedState.getClusterState());
        Assert.assertEquals(nodeState, deserializedState.getNodeState());
        Assert.assertEquals(clusterVersion, deserializedState.getClusterVersion());
        Assert.assertEquals(memberVersion, deserializedState.getMemberVersion());
        final HotRestartState deserializedHotRestartState = deserialized.getHotRestartState();
        Assert.assertTrue(deserializedHotRestartState.isHotBackupEnabled());
        Assert.assertEquals(backupTaskStatus, deserializedHotRestartState.getBackupTaskStatus());
        Assert.assertEquals(backupDirectory, deserializedHotRestartState.getBackupDirectory());
        final WanSyncState deserializedWanSyncState = deserialized.getWanSyncState();
        Assert.assertEquals(IN_PROGRESS, deserializedWanSyncState.getStatus());
        Assert.assertEquals(86, deserializedWanSyncState.getSyncedPartitionCount());
        Assert.assertEquals("atob", deserializedWanSyncState.getActiveWanConfigName());
        Assert.assertEquals("B", deserializedWanSyncState.getActivePublisherName());
        ClusterHotRestartStatusDTO clusterHotRestartStatus = deserialized.getClusterHotRestartStatus();
        Assert.assertEquals(HotRestartClusterDataRecoveryPolicy.FULL_RECOVERY_ONLY, clusterHotRestartStatus.getDataRecoveryPolicy());
        Assert.assertEquals(UNKNOWN, clusterHotRestartStatus.getHotRestartStatus());
        Assert.assertEquals((-1), clusterHotRestartStatus.getRemainingValidationTimeMillis());
        Assert.assertEquals((-1), clusterHotRestartStatus.getRemainingDataLoadTimeMillis());
        Assert.assertTrue(clusterHotRestartStatus.getMemberHotRestartStatusMap().isEmpty());
        Map<String, String> deserializedClientStats = deserialized.getClientStats();
        Assert.assertEquals("someStats", deserializedClientStats.get("abc123456"));
    }
}

