/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.kafka.connect.runtime.distributed;


import ConnectProtocol.Assignment;
import ConnectProtocol.Assignment.CONFIG_MISMATCH;
import ConnectProtocol.Assignment.NO_ERROR;
import ConnectProtocol.WorkerState;
import Errors.NONE;
import MockClient.RequestMatcher;
import WorkerCoordinator.DEFAULT_SUBPROTOCOL;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.kafka.clients.Metadata;
import org.apache.kafka.clients.MockClient;
import org.apache.kafka.clients.consumer.internals.ConsumerNetworkClient;
import org.apache.kafka.common.Node;
import org.apache.kafka.common.metrics.Metrics;
import org.apache.kafka.common.requests.AbstractRequest;
import org.apache.kafka.common.requests.JoinGroupRequest.ProtocolMetadata;
import org.apache.kafka.common.requests.SyncGroupRequest;
import org.apache.kafka.common.utils.MockTime;
import org.apache.kafka.connect.storage.KafkaConfigBackingStore;
import org.apache.kafka.connect.util.ConnectorTaskId;
import org.easymock.EasyMock;
import org.easymock.Mock;
import org.junit.Assert;
import org.junit.Test;
import org.powermock.api.easymock.PowerMock;
import org.powermock.reflect.Whitebox;


public class WorkerCoordinatorTest {
    private static final String LEADER_URL = "leaderUrl:8083";

    private static final String MEMBER_URL = "memberUrl:8083";

    private String connectorId1 = "connector1";

    private String connectorId2 = "connector2";

    private String connectorId3 = "connector3";

    private ConnectorTaskId taskId1x0 = new ConnectorTaskId(connectorId1, 0);

    private ConnectorTaskId taskId1x1 = new ConnectorTaskId(connectorId1, 1);

    private ConnectorTaskId taskId2x0 = new ConnectorTaskId(connectorId2, 0);

    private ConnectorTaskId taskId3x0 = new ConnectorTaskId(connectorId3, 0);

    private String groupId = "test-group";

    private int sessionTimeoutMs = 10;

    private int rebalanceTimeoutMs = 60;

    private int heartbeatIntervalMs = 2;

    private long retryBackoffMs = 100;

    private MockTime time;

    private MockClient client;

    private Node node;

    private Metadata metadata;

    private Metrics metrics;

    private ConsumerNetworkClient consumerClient;

    private WorkerCoordinatorTest.MockRebalanceListener rebalanceListener;

    @Mock
    private KafkaConfigBackingStore configStorage;

    private WorkerCoordinator coordinator;

    private ClusterConfigState configState1;

    private ClusterConfigState configState2;

    private ClusterConfigState configStateSingleTaskConnectors;

    // We only test functionality unique to WorkerCoordinator. Most functionality is already well tested via the tests
    // that cover AbstractCoordinator & ConsumerCoordinator.
    @Test
    public void testMetadata() {
        EasyMock.expect(configStorage.snapshot()).andReturn(configState1);
        PowerMock.replayAll();
        List<ProtocolMetadata> serialized = coordinator.metadata();
        Assert.assertEquals(1, serialized.size());
        ProtocolMetadata defaultMetadata = serialized.get(0);
        Assert.assertEquals(DEFAULT_SUBPROTOCOL, defaultMetadata.name());
        ConnectProtocol.WorkerState state = ConnectProtocol.deserializeMetadata(defaultMetadata.metadata());
        Assert.assertEquals(1, state.offset());
        PowerMock.verifyAll();
    }

    @Test
    public void testNormalJoinGroupLeader() {
        EasyMock.expect(configStorage.snapshot()).andReturn(configState1);
        PowerMock.replayAll();
        final String consumerId = "leader";
        client.prepareResponse(groupCoordinatorResponse(node, NONE));
        coordinator.ensureCoordinatorReady(time.timer(Long.MAX_VALUE));
        // normal join group
        Map<String, Long> memberConfigOffsets = new HashMap<>();
        memberConfigOffsets.put("leader", 1L);
        memberConfigOffsets.put("member", 1L);
        client.prepareResponse(joinGroupLeaderResponse(1, consumerId, memberConfigOffsets, NONE));
        client.prepareResponse(new MockClient.RequestMatcher() {
            @Override
            public boolean matches(AbstractRequest body) {
                SyncGroupRequest sync = ((SyncGroupRequest) (body));
                return ((sync.memberId().equals(consumerId)) && ((sync.generationId()) == 1)) && (sync.groupAssignment().containsKey(consumerId));
            }
        }, syncGroupResponse(NO_ERROR, "leader", 1L, Collections.singletonList(connectorId1), Collections.<ConnectorTaskId>emptyList(), NONE));
        coordinator.ensureActiveGroup();
        Assert.assertFalse(coordinator.rejoinNeededOrPending());
        Assert.assertEquals(0, rebalanceListener.revokedCount);
        Assert.assertEquals(1, rebalanceListener.assignedCount);
        Assert.assertFalse(rebalanceListener.assignment.failed());
        Assert.assertEquals(1L, rebalanceListener.assignment.offset());
        Assert.assertEquals("leader", rebalanceListener.assignment.leader());
        Assert.assertEquals(Collections.singletonList(connectorId1), rebalanceListener.assignment.connectors());
        Assert.assertEquals(Collections.emptyList(), rebalanceListener.assignment.tasks());
        PowerMock.verifyAll();
    }

    @Test
    public void testNormalJoinGroupFollower() {
        EasyMock.expect(configStorage.snapshot()).andReturn(configState1);
        PowerMock.replayAll();
        final String memberId = "member";
        client.prepareResponse(groupCoordinatorResponse(node, NONE));
        coordinator.ensureCoordinatorReady(time.timer(Long.MAX_VALUE));
        // normal join group
        client.prepareResponse(joinGroupFollowerResponse(1, memberId, "leader", NONE));
        client.prepareResponse(new MockClient.RequestMatcher() {
            @Override
            public boolean matches(AbstractRequest body) {
                SyncGroupRequest sync = ((SyncGroupRequest) (body));
                return ((sync.memberId().equals(memberId)) && ((sync.generationId()) == 1)) && (sync.groupAssignment().isEmpty());
            }
        }, syncGroupResponse(NO_ERROR, "leader", 1L, Collections.<String>emptyList(), Collections.singletonList(taskId1x0), NONE));
        coordinator.ensureActiveGroup();
        Assert.assertFalse(coordinator.rejoinNeededOrPending());
        Assert.assertEquals(0, rebalanceListener.revokedCount);
        Assert.assertEquals(1, rebalanceListener.assignedCount);
        Assert.assertFalse(rebalanceListener.assignment.failed());
        Assert.assertEquals(1L, rebalanceListener.assignment.offset());
        Assert.assertEquals(Collections.emptyList(), rebalanceListener.assignment.connectors());
        Assert.assertEquals(Collections.singletonList(taskId1x0), rebalanceListener.assignment.tasks());
        PowerMock.verifyAll();
    }

    @Test
    public void testJoinLeaderCannotAssign() {
        // If the selected leader can't get up to the maximum offset, it will fail to assign and we should immediately
        // need to retry the join.
        // When the first round fails, we'll take an updated config snapshot
        EasyMock.expect(configStorage.snapshot()).andReturn(configState1);
        EasyMock.expect(configStorage.snapshot()).andReturn(configState2);
        PowerMock.replayAll();
        final String memberId = "member";
        client.prepareResponse(groupCoordinatorResponse(node, NONE));
        coordinator.ensureCoordinatorReady(time.timer(Long.MAX_VALUE));
        // config mismatch results in assignment error
        client.prepareResponse(joinGroupFollowerResponse(1, memberId, "leader", NONE));
        MockClient.RequestMatcher matcher = new MockClient.RequestMatcher() {
            @Override
            public boolean matches(AbstractRequest body) {
                SyncGroupRequest sync = ((SyncGroupRequest) (body));
                return ((sync.memberId().equals(memberId)) && ((sync.generationId()) == 1)) && (sync.groupAssignment().isEmpty());
            }
        };
        client.prepareResponse(matcher, syncGroupResponse(CONFIG_MISMATCH, "leader", 10L, Collections.<String>emptyList(), Collections.<ConnectorTaskId>emptyList(), NONE));
        client.prepareResponse(joinGroupFollowerResponse(1, memberId, "leader", NONE));
        client.prepareResponse(matcher, syncGroupResponse(NO_ERROR, "leader", 1L, Collections.<String>emptyList(), Collections.singletonList(taskId1x0), NONE));
        coordinator.ensureActiveGroup();
        PowerMock.verifyAll();
    }

    @Test
    public void testRejoinGroup() {
        EasyMock.expect(configStorage.snapshot()).andReturn(configState1);
        EasyMock.expect(configStorage.snapshot()).andReturn(configState1);
        PowerMock.replayAll();
        client.prepareResponse(groupCoordinatorResponse(node, NONE));
        coordinator.ensureCoordinatorReady(time.timer(Long.MAX_VALUE));
        // join the group once
        client.prepareResponse(joinGroupFollowerResponse(1, "consumer", "leader", NONE));
        client.prepareResponse(syncGroupResponse(NO_ERROR, "leader", 1L, Collections.<String>emptyList(), Collections.singletonList(taskId1x0), NONE));
        coordinator.ensureActiveGroup();
        Assert.assertEquals(0, rebalanceListener.revokedCount);
        Assert.assertEquals(1, rebalanceListener.assignedCount);
        Assert.assertFalse(rebalanceListener.assignment.failed());
        Assert.assertEquals(1L, rebalanceListener.assignment.offset());
        Assert.assertEquals(Collections.emptyList(), rebalanceListener.assignment.connectors());
        Assert.assertEquals(Collections.singletonList(taskId1x0), rebalanceListener.assignment.tasks());
        // and join the group again
        coordinator.requestRejoin();
        client.prepareResponse(joinGroupFollowerResponse(1, "consumer", "leader", NONE));
        client.prepareResponse(syncGroupResponse(NO_ERROR, "leader", 1L, Collections.singletonList(connectorId1), Collections.<ConnectorTaskId>emptyList(), NONE));
        coordinator.ensureActiveGroup();
        Assert.assertEquals(1, rebalanceListener.revokedCount);
        Assert.assertEquals(Collections.emptyList(), rebalanceListener.revokedConnectors);
        Assert.assertEquals(Collections.singletonList(taskId1x0), rebalanceListener.revokedTasks);
        Assert.assertEquals(2, rebalanceListener.assignedCount);
        Assert.assertFalse(rebalanceListener.assignment.failed());
        Assert.assertEquals(1L, rebalanceListener.assignment.offset());
        Assert.assertEquals(Collections.singletonList(connectorId1), rebalanceListener.assignment.connectors());
        Assert.assertEquals(Collections.emptyList(), rebalanceListener.assignment.tasks());
        PowerMock.verifyAll();
    }

    @Test
    public void testLeaderPerformAssignment1() throws Exception {
        // Since all the protocol responses are mocked, the other tests validate doSync runs, but don't validate its
        // output. So we test it directly here.
        EasyMock.expect(configStorage.snapshot()).andReturn(configState1);
        PowerMock.replayAll();
        // Prime the current configuration state
        coordinator.metadata();
        Map<String, ByteBuffer> configs = new HashMap<>();
        // Mark everyone as in sync with configState1
        configs.put("leader", ConnectProtocol.serializeMetadata(new ConnectProtocol.WorkerState(WorkerCoordinatorTest.LEADER_URL, 1L)));
        configs.put("member", ConnectProtocol.serializeMetadata(new ConnectProtocol.WorkerState(WorkerCoordinatorTest.MEMBER_URL, 1L)));
        Map<String, ByteBuffer> result = Whitebox.invokeMethod(coordinator, "performAssignment", "leader", DEFAULT_SUBPROTOCOL, configs);
        // configState1 has 1 connector, 1 task
        ConnectProtocol.Assignment leaderAssignment = ConnectProtocol.deserializeAssignment(result.get("leader"));
        Assert.assertEquals(false, leaderAssignment.failed());
        Assert.assertEquals("leader", leaderAssignment.leader());
        Assert.assertEquals(1, leaderAssignment.offset());
        Assert.assertEquals(Collections.singletonList(connectorId1), leaderAssignment.connectors());
        Assert.assertEquals(Collections.emptyList(), leaderAssignment.tasks());
        ConnectProtocol.Assignment memberAssignment = ConnectProtocol.deserializeAssignment(result.get("member"));
        Assert.assertEquals(false, memberAssignment.failed());
        Assert.assertEquals("leader", memberAssignment.leader());
        Assert.assertEquals(1, memberAssignment.offset());
        Assert.assertEquals(Collections.emptyList(), memberAssignment.connectors());
        Assert.assertEquals(Collections.singletonList(taskId1x0), memberAssignment.tasks());
        PowerMock.verifyAll();
    }

    @Test
    public void testLeaderPerformAssignment2() throws Exception {
        // Since all the protocol responses are mocked, the other tests validate doSync runs, but don't validate its
        // output. So we test it directly here.
        EasyMock.expect(configStorage.snapshot()).andReturn(configState2);
        PowerMock.replayAll();
        // Prime the current configuration state
        coordinator.metadata();
        Map<String, ByteBuffer> configs = new HashMap<>();
        // Mark everyone as in sync with configState1
        configs.put("leader", ConnectProtocol.serializeMetadata(new ConnectProtocol.WorkerState(WorkerCoordinatorTest.LEADER_URL, 1L)));
        configs.put("member", ConnectProtocol.serializeMetadata(new ConnectProtocol.WorkerState(WorkerCoordinatorTest.MEMBER_URL, 1L)));
        Map<String, ByteBuffer> result = Whitebox.invokeMethod(coordinator, "performAssignment", "leader", DEFAULT_SUBPROTOCOL, configs);
        // configState2 has 2 connector, 3 tasks and should trigger round robin assignment
        ConnectProtocol.Assignment leaderAssignment = ConnectProtocol.deserializeAssignment(result.get("leader"));
        Assert.assertEquals(false, leaderAssignment.failed());
        Assert.assertEquals("leader", leaderAssignment.leader());
        Assert.assertEquals(1, leaderAssignment.offset());
        Assert.assertEquals(Collections.singletonList(connectorId1), leaderAssignment.connectors());
        Assert.assertEquals(Arrays.asList(taskId1x0, taskId2x0), leaderAssignment.tasks());
        ConnectProtocol.Assignment memberAssignment = ConnectProtocol.deserializeAssignment(result.get("member"));
        Assert.assertEquals(false, memberAssignment.failed());
        Assert.assertEquals("leader", memberAssignment.leader());
        Assert.assertEquals(1, memberAssignment.offset());
        Assert.assertEquals(Collections.singletonList(connectorId2), memberAssignment.connectors());
        Assert.assertEquals(Collections.singletonList(taskId1x1), memberAssignment.tasks());
        PowerMock.verifyAll();
    }

    @Test
    public void testLeaderPerformAssignmentSingleTaskConnectors() throws Exception {
        // Since all the protocol responses are mocked, the other tests validate doSync runs, but don't validate its
        // output. So we test it directly here.
        EasyMock.expect(configStorage.snapshot()).andReturn(configStateSingleTaskConnectors);
        PowerMock.replayAll();
        // Prime the current configuration state
        coordinator.metadata();
        Map<String, ByteBuffer> configs = new HashMap<>();
        // Mark everyone as in sync with configState1
        configs.put("leader", ConnectProtocol.serializeMetadata(new ConnectProtocol.WorkerState(WorkerCoordinatorTest.LEADER_URL, 1L)));
        configs.put("member", ConnectProtocol.serializeMetadata(new ConnectProtocol.WorkerState(WorkerCoordinatorTest.MEMBER_URL, 1L)));
        Map<String, ByteBuffer> result = Whitebox.invokeMethod(coordinator, "performAssignment", "leader", DEFAULT_SUBPROTOCOL, configs);
        // Round robin assignment when there are the same number of connectors and tasks should result in each being
        // evenly distributed across the workers, i.e. round robin assignment of connectors first, then followed by tasks
        ConnectProtocol.Assignment leaderAssignment = ConnectProtocol.deserializeAssignment(result.get("leader"));
        Assert.assertEquals(false, leaderAssignment.failed());
        Assert.assertEquals("leader", leaderAssignment.leader());
        Assert.assertEquals(1, leaderAssignment.offset());
        Assert.assertEquals(Arrays.asList(connectorId1, connectorId3), leaderAssignment.connectors());
        Assert.assertEquals(Arrays.asList(taskId2x0), leaderAssignment.tasks());
        ConnectProtocol.Assignment memberAssignment = ConnectProtocol.deserializeAssignment(result.get("member"));
        Assert.assertEquals(false, memberAssignment.failed());
        Assert.assertEquals("leader", memberAssignment.leader());
        Assert.assertEquals(1, memberAssignment.offset());
        Assert.assertEquals(Collections.singletonList(connectorId2), memberAssignment.connectors());
        Assert.assertEquals(Arrays.asList(taskId1x0, taskId3x0), memberAssignment.tasks());
        PowerMock.verifyAll();
    }

    private static class MockRebalanceListener implements WorkerRebalanceListener {
        public Assignment assignment = null;

        public String revokedLeader;

        public Collection<String> revokedConnectors;

        public Collection<ConnectorTaskId> revokedTasks;

        public int revokedCount = 0;

        public int assignedCount = 0;

        @Override
        public void onAssigned(ConnectProtocol.Assignment assignment, int generation) {
            this.assignment = assignment;
            (assignedCount)++;
        }

        @Override
        public void onRevoked(String leader, Collection<String> connectors, Collection<ConnectorTaskId> tasks) {
            this.revokedLeader = leader;
            this.revokedConnectors = connectors;
            this.revokedTasks = tasks;
            (revokedCount)++;
        }
    }
}

