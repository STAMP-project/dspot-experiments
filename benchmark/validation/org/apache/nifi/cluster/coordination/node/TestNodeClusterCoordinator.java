/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.nifi.cluster.coordination.node;


import DisconnectionCode.LACK_OF_HEARTBEAT;
import DisconnectionCode.NODE_SHUTDOWN;
import DisconnectionCode.USER_DISCONNECTED;
import NodeConnectionState.CONNECTED;
import NodeConnectionState.CONNECTING;
import NodeConnectionState.DISCONNECTED;
import NodeConnectionState.DISCONNECTING;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;
import org.apache.nifi.cluster.coordination.flow.FlowElection;
import org.apache.nifi.cluster.manager.exception.IllegalNodeDisconnectionException;
import org.apache.nifi.cluster.protocol.ConnectionRequest;
import org.apache.nifi.cluster.protocol.ConnectionResponse;
import org.apache.nifi.cluster.protocol.DataFlow;
import org.apache.nifi.cluster.protocol.NodeIdentifier;
import org.apache.nifi.cluster.protocol.StandardDataFlow;
import org.apache.nifi.cluster.protocol.impl.ClusterCoordinationProtocolSenderListener;
import org.apache.nifi.cluster.protocol.message.ConnectionRequestMessage;
import org.apache.nifi.cluster.protocol.message.ConnectionResponseMessage;
import org.apache.nifi.cluster.protocol.message.NodeStatusChangeMessage;
import org.apache.nifi.cluster.protocol.message.ProtocolMessage;
import org.apache.nifi.cluster.protocol.message.ReconnectionRequestMessage;
import org.apache.nifi.components.state.StateManagerProvider;
import org.apache.nifi.events.EventReporter;
import org.apache.nifi.services.FlowService;
import org.apache.nifi.web.revision.RevisionManager;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import static DisconnectionCode.BLOCKED_BY_FIREWALL;
import static DisconnectionCode.LACK_OF_HEARTBEAT;
import static NodeConnectionState.CONNECTED;
import static NodeConnectionState.CONNECTING;
import static NodeConnectionState.DISCONNECTED;
import static NodeConnectionState.DISCONNECTING;


public class TestNodeClusterCoordinator {
    private NodeClusterCoordinator coordinator;

    private ClusterCoordinationProtocolSenderListener senderListener;

    private List<NodeConnectionStatus> nodeStatuses;

    private StateManagerProvider stateManagerProvider;

    @Test
    public void testConnectionResponseIndicatesAllNodes() throws IOException {
        // Add a disconnected node
        coordinator.updateNodeStatus(new NodeConnectionStatus(createNodeId(1), LACK_OF_HEARTBEAT));
        coordinator.updateNodeStatus(new NodeConnectionStatus(createNodeId(2), DISCONNECTING));
        coordinator.updateNodeStatus(new NodeConnectionStatus(createNodeId(3), CONNECTING));
        coordinator.updateNodeStatus(new NodeConnectionStatus(createNodeId(4), CONNECTED));
        coordinator.updateNodeStatus(new NodeConnectionStatus(createNodeId(5), CONNECTED));
        // Create a connection request message and send to the coordinator
        final NodeIdentifier requestedNodeId = createNodeId(6);
        final ProtocolMessage protocolResponse = requestConnection(requestedNodeId, coordinator);
        Assert.assertNotNull(protocolResponse);
        Assert.assertTrue((protocolResponse instanceof ConnectionResponseMessage));
        final ConnectionResponse response = getConnectionResponse();
        Assert.assertNotNull(response);
        Assert.assertEquals(requestedNodeId, response.getNodeIdentifier());
        Assert.assertNull(response.getRejectionReason());
        final List<NodeConnectionStatus> statuses = response.getNodeConnectionStatuses();
        Assert.assertNotNull(statuses);
        Assert.assertEquals(6, statuses.size());
        final Map<NodeIdentifier, NodeConnectionStatus> statusMap = statuses.stream().collect(Collectors.toMap(( status) -> status.getNodeIdentifier(), ( status) -> status));
        Assert.assertEquals(LACK_OF_HEARTBEAT, statusMap.get(createNodeId(1)).getDisconnectCode());
        Assert.assertEquals(DISCONNECTING, statusMap.get(createNodeId(2)).getState());
        Assert.assertEquals(CONNECTING, statusMap.get(createNodeId(3)).getState());
        Assert.assertEquals(CONNECTED, statusMap.get(createNodeId(4)).getState());
        Assert.assertEquals(CONNECTED, statusMap.get(createNodeId(5)).getState());
        Assert.assertEquals(CONNECTING, statusMap.get(createNodeId(6)).getState());
    }

    @Test
    public void testTryAgainIfNoFlowServiceSet() throws IOException {
        final ClusterCoordinationProtocolSenderListener senderListener = Mockito.mock(ClusterCoordinationProtocolSenderListener.class);
        final EventReporter eventReporter = Mockito.mock(EventReporter.class);
        final RevisionManager revisionManager = Mockito.mock(RevisionManager.class);
        Mockito.when(revisionManager.getAllRevisions()).thenReturn(Collections.emptyList());
        final NodeClusterCoordinator coordinator = new NodeClusterCoordinator(senderListener, eventReporter, null, new TestNodeClusterCoordinator.FirstVoteWinsFlowElection(), null, revisionManager, createProperties(), null, stateManagerProvider) {
            @Override
            void notifyOthersOfNodeStatusChange(NodeConnectionStatus updatedStatus, boolean notifyAllNodes, boolean waitForCoordinator) {
            }
        };
        final NodeIdentifier requestedNodeId = createNodeId(6);
        final ConnectionRequest request = new ConnectionRequest(requestedNodeId, new StandardDataFlow(new byte[0], new byte[0], new byte[0], new HashSet()));
        final ConnectionRequestMessage requestMsg = new ConnectionRequestMessage();
        requestMsg.setConnectionRequest(request);
        coordinator.setConnected(true);
        final ProtocolMessage protocolResponse = coordinator.handle(requestMsg, Collections.emptySet());
        Assert.assertNotNull(protocolResponse);
        Assert.assertTrue((protocolResponse instanceof ConnectionResponseMessage));
        final ConnectionResponse response = getConnectionResponse();
        Assert.assertNotNull(response);
        Assert.assertEquals(5, response.getTryLaterSeconds());
    }

    @Test(timeout = 5000)
    public void testUnknownNodeAskedToConnectOnAttemptedConnectionComplete() throws IOException, InterruptedException {
        final ClusterCoordinationProtocolSenderListener senderListener = Mockito.mock(ClusterCoordinationProtocolSenderListener.class);
        final AtomicReference<ReconnectionRequestMessage> requestRef = new AtomicReference<>();
        Mockito.when(senderListener.requestReconnection(ArgumentMatchers.any(ReconnectionRequestMessage.class))).thenAnswer(new Answer<Object>() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                final ReconnectionRequestMessage msg = getArgumentAt(0, ReconnectionRequestMessage.class);
                requestRef.set(msg);
                return null;
            }
        });
        final EventReporter eventReporter = Mockito.mock(EventReporter.class);
        final RevisionManager revisionManager = Mockito.mock(RevisionManager.class);
        Mockito.when(revisionManager.getAllRevisions()).thenReturn(Collections.emptyList());
        final NodeClusterCoordinator coordinator = new NodeClusterCoordinator(senderListener, eventReporter, null, new TestNodeClusterCoordinator.FirstVoteWinsFlowElection(), null, revisionManager, createProperties(), null, stateManagerProvider) {
            @Override
            void notifyOthersOfNodeStatusChange(NodeConnectionStatus updatedStatus, boolean notifyAllNodes, boolean waitForCoordinator) {
            }
        };
        final FlowService flowService = Mockito.mock(FlowService.class);
        final StandardDataFlow dataFlow = new StandardDataFlow(new byte[50], new byte[50], new byte[50], new HashSet());
        Mockito.when(flowService.createDataFlowFromController()).thenReturn(dataFlow);
        coordinator.setFlowService(flowService);
        coordinator.setConnected(true);
        final NodeIdentifier nodeId = createNodeId(1);
        coordinator.finishNodeConnection(nodeId);
        while ((requestRef.get()) == null) {
            Thread.sleep(10L);
        } 
        final ReconnectionRequestMessage msg = requestRef.get();
        Assert.assertEquals(nodeId, msg.getNodeId());
        final StandardDataFlow df = msg.getDataFlow();
        Assert.assertNotNull(df);
        Assert.assertTrue(Arrays.equals(dataFlow.getFlow(), df.getFlow()));
        Assert.assertTrue(Arrays.equals(dataFlow.getSnippets(), df.getSnippets()));
    }

    @Test(timeout = 5000)
    public void testFinishNodeConnectionResultsInConnectedState() throws IOException, InterruptedException {
        final NodeIdentifier nodeId = createNodeId(1);
        // Create a connection request message and send to the coordinator
        requestConnection(createNodeId(1), coordinator);
        while (nodeStatuses.isEmpty()) {
            Thread.sleep(20L);
        } 
        Assert.assertEquals(CONNECTING, nodeStatuses.get(0).getState());
        nodeStatuses.clear();
        // Finish connecting. This should notify all that the status is now 'CONNECTED'
        coordinator.finishNodeConnection(nodeId);
        while (nodeStatuses.isEmpty()) {
            Thread.sleep(20L);
        } 
        Assert.assertEquals(CONNECTED, nodeStatuses.get(0).getState());
        Assert.assertEquals(CONNECTED, coordinator.getConnectionStatus(nodeId).getState());
    }

    @Test(timeout = 5000)
    public void testStatusChangesReplicated() throws IOException, InterruptedException {
        final RevisionManager revisionManager = Mockito.mock(RevisionManager.class);
        Mockito.when(revisionManager.getAllRevisions()).thenReturn(Collections.emptyList());
        // Create a connection request message and send to the coordinator
        final NodeIdentifier requestedNodeId = createNodeId(1);
        requestConnection(requestedNodeId, coordinator);
        // The above connection request should trigger a 'CONNECTING' state transition to be replicated
        while (nodeStatuses.isEmpty()) {
            Thread.sleep(20L);
        } 
        final NodeConnectionStatus connectingStatus = nodeStatuses.get(0);
        Assert.assertEquals(CONNECTING, connectingStatus.getState());
        Assert.assertEquals(requestedNodeId, connectingStatus.getNodeIdentifier());
        // set node status to connected
        coordinator.finishNodeConnection(requestedNodeId);
        // the above method will result in the node identifier becoming 'CONNECTED'. Wait for this to happen and clear the map
        while (nodeStatuses.isEmpty()) {
            Thread.sleep(20L);
        } 
        nodeStatuses.clear();
        coordinator.disconnectionRequestedByNode(requestedNodeId, NODE_SHUTDOWN, "Unit Test");
        while (nodeStatuses.isEmpty()) {
            Thread.sleep(20L);
        } 
        Assert.assertEquals(1, nodeStatuses.size());
        final NodeConnectionStatus statusChange = nodeStatuses.get(0);
        Assert.assertNotNull(statusChange);
        Assert.assertEquals(createNodeId(1), statusChange.getNodeIdentifier());
        Assert.assertEquals(NODE_SHUTDOWN, statusChange.getDisconnectCode());
        Assert.assertEquals("Unit Test", statusChange.getReason());
    }

    @Test
    public void testGetConnectionStates() throws IOException {
        // Add a disconnected node
        coordinator.updateNodeStatus(new NodeConnectionStatus(createNodeId(1), LACK_OF_HEARTBEAT));
        coordinator.updateNodeStatus(new NodeConnectionStatus(createNodeId(2), DISCONNECTING));
        coordinator.updateNodeStatus(new NodeConnectionStatus(createNodeId(3), CONNECTING));
        coordinator.updateNodeStatus(new NodeConnectionStatus(createNodeId(4), CONNECTED));
        coordinator.updateNodeStatus(new NodeConnectionStatus(createNodeId(5), CONNECTED));
        final Map<NodeConnectionState, List<NodeIdentifier>> stateMap = coordinator.getConnectionStates();
        Assert.assertEquals(4, stateMap.size());
        final List<NodeIdentifier> connectedIds = stateMap.get(CONNECTED);
        Assert.assertEquals(2, connectedIds.size());
        Assert.assertTrue(connectedIds.contains(createNodeId(4)));
        Assert.assertTrue(connectedIds.contains(createNodeId(5)));
        final List<NodeIdentifier> connectingIds = stateMap.get(CONNECTING);
        Assert.assertEquals(1, connectingIds.size());
        Assert.assertTrue(connectingIds.contains(createNodeId(3)));
        final List<NodeIdentifier> disconnectingIds = stateMap.get(DISCONNECTING);
        Assert.assertEquals(1, disconnectingIds.size());
        Assert.assertTrue(disconnectingIds.contains(createNodeId(2)));
        final List<NodeIdentifier> disconnectedIds = stateMap.get(DISCONNECTED);
        Assert.assertEquals(1, disconnectedIds.size());
        Assert.assertTrue(disconnectedIds.contains(createNodeId(1)));
    }

    @Test
    public void testGetNodeIdentifiers() throws IOException {
        // Add a disconnected node
        coordinator.updateNodeStatus(new NodeConnectionStatus(createNodeId(1), LACK_OF_HEARTBEAT));
        coordinator.updateNodeStatus(new NodeConnectionStatus(createNodeId(2), DISCONNECTING));
        coordinator.updateNodeStatus(new NodeConnectionStatus(createNodeId(3), CONNECTING));
        coordinator.updateNodeStatus(new NodeConnectionStatus(createNodeId(4), CONNECTED));
        coordinator.updateNodeStatus(new NodeConnectionStatus(createNodeId(5), CONNECTED));
        final Set<NodeIdentifier> connectedIds = coordinator.getNodeIdentifiers(CONNECTED);
        Assert.assertEquals(2, connectedIds.size());
        Assert.assertTrue(connectedIds.contains(createNodeId(4)));
        Assert.assertTrue(connectedIds.contains(createNodeId(5)));
        final Set<NodeIdentifier> connectingIds = coordinator.getNodeIdentifiers(CONNECTING);
        Assert.assertEquals(1, connectingIds.size());
        Assert.assertTrue(connectingIds.contains(createNodeId(3)));
        final Set<NodeIdentifier> disconnectingIds = coordinator.getNodeIdentifiers(DISCONNECTING);
        Assert.assertEquals(1, disconnectingIds.size());
        Assert.assertTrue(disconnectingIds.contains(createNodeId(2)));
        final Set<NodeIdentifier> disconnectedIds = coordinator.getNodeIdentifiers(DISCONNECTED);
        Assert.assertEquals(1, disconnectedIds.size());
        Assert.assertTrue(disconnectedIds.contains(createNodeId(1)));
    }

    @Test(timeout = 5000)
    public void testRequestNodeDisconnect() throws InterruptedException {
        // Add a connected node
        final NodeIdentifier nodeId1 = createNodeId(1);
        coordinator.updateNodeStatus(new NodeConnectionStatus(nodeId1, CONNECTED));
        coordinator.updateNodeStatus(new NodeConnectionStatus(createNodeId(2), CONNECTED));
        // wait for the status change message and clear it
        while (nodeStatuses.isEmpty()) {
            Thread.sleep(10L);
        } 
        nodeStatuses.clear();
        coordinator.requestNodeDisconnect(nodeId1, USER_DISCONNECTED, "Unit Test");
        Assert.assertEquals(DISCONNECTED, coordinator.getConnectionStatus(nodeId1).getState());
        while (nodeStatuses.isEmpty()) {
            Thread.sleep(10L);
        } 
        final NodeConnectionStatus status = nodeStatuses.get(0);
        Assert.assertEquals(nodeId1, status.getNodeIdentifier());
        Assert.assertEquals(DISCONNECTED, status.getState());
    }

    @Test(timeout = 5000)
    public void testCannotDisconnectLastNode() throws InterruptedException {
        // Add a connected node
        final NodeIdentifier nodeId1 = createNodeId(1);
        final NodeIdentifier nodeId2 = createNodeId(2);
        coordinator.updateNodeStatus(new NodeConnectionStatus(nodeId1, CONNECTED));
        coordinator.updateNodeStatus(new NodeConnectionStatus(nodeId2, CONNECTED));
        // wait for the status change message and clear it
        while (nodeStatuses.isEmpty()) {
            Thread.sleep(10L);
        } 
        nodeStatuses.clear();
        coordinator.requestNodeDisconnect(nodeId2, USER_DISCONNECTED, "Unit Test");
        try {
            coordinator.requestNodeDisconnect(nodeId1, USER_DISCONNECTED, "Unit Test");
            Assert.fail("Expected an IllegalNodeDisconnectionException when trying to disconnect last node but it wasn't thrown");
        } catch (final IllegalNodeDisconnectionException inde) {
            // expected
        }
        // Should still be able to request that node 2 disconnect, since it's not the node that is connected
        coordinator.requestNodeDisconnect(nodeId2, USER_DISCONNECTED, "Unit Test");
    }

    @Test(timeout = 5000)
    public void testUpdateNodeStatusOutOfOrder() throws InterruptedException {
        // Add a connected node
        final NodeIdentifier nodeId1 = createNodeId(1);
        final NodeIdentifier nodeId2 = createNodeId(2);
        coordinator.updateNodeStatus(new NodeConnectionStatus(nodeId1, CONNECTED));
        coordinator.updateNodeStatus(new NodeConnectionStatus(nodeId2, CONNECTED));
        // wait for the status change message and clear it
        while ((nodeStatuses.size()) < 2) {
            Thread.sleep(10L);
        } 
        nodeStatuses.clear();
        final NodeConnectionStatus oldStatus = new NodeConnectionStatus((-1L), nodeId1, DISCONNECTED, null, BLOCKED_BY_FIREWALL, null, 0L);
        final NodeStatusChangeMessage msg = new NodeStatusChangeMessage();
        msg.setNodeId(nodeId1);
        msg.setNodeConnectionStatus(oldStatus);
        coordinator.handle(msg, Collections.emptySet());
        // Ensure that no status change message was send
        Thread.sleep(1000);
        Assert.assertTrue(nodeStatuses.isEmpty());
    }

    @Test
    public void testProposedIdentifierResolvedIfConflict() {
        final NodeIdentifier id1 = new NodeIdentifier("1234", "localhost", 8000, "localhost", 9000, "localhost", 10000, 11000, false);
        final NodeIdentifier conflictingId = new NodeIdentifier("1234", "localhost", 8001, "localhost", 9000, "localhost", 10000, 11000, false);
        final ConnectionRequest connectionRequest = new ConnectionRequest(id1, new StandardDataFlow(new byte[0], new byte[0], new byte[0], new HashSet()));
        final ConnectionRequestMessage crm = new ConnectionRequestMessage();
        crm.setConnectionRequest(connectionRequest);
        final ProtocolMessage response = coordinator.handle(crm, Collections.emptySet());
        Assert.assertNotNull(response);
        Assert.assertTrue((response instanceof ConnectionResponseMessage));
        final ConnectionResponseMessage responseMessage = ((ConnectionResponseMessage) (response));
        final NodeIdentifier resolvedNodeId = responseMessage.getConnectionResponse().getNodeIdentifier();
        Assert.assertEquals(id1, resolvedNodeId);
        final ConnectionRequest conRequest2 = new ConnectionRequest(conflictingId, new StandardDataFlow(new byte[0], new byte[0], new byte[0], new HashSet()));
        final ConnectionRequestMessage crm2 = new ConnectionRequestMessage();
        crm2.setConnectionRequest(conRequest2);
        final ProtocolMessage conflictingResponse = coordinator.handle(crm2, Collections.emptySet());
        Assert.assertNotNull(conflictingResponse);
        Assert.assertTrue((conflictingResponse instanceof ConnectionResponseMessage));
        final ConnectionResponseMessage conflictingResponseMessage = ((ConnectionResponseMessage) (conflictingResponse));
        final NodeIdentifier conflictingNodeId = conflictingResponseMessage.getConnectionResponse().getNodeIdentifier();
        Assert.assertNotSame(id1.getId(), conflictingNodeId.getId());
        Assert.assertEquals(conflictingId.getApiAddress(), conflictingNodeId.getApiAddress());
        Assert.assertEquals(conflictingId.getApiPort(), conflictingNodeId.getApiPort());
        Assert.assertEquals(conflictingId.getSiteToSiteAddress(), conflictingNodeId.getSiteToSiteAddress());
        Assert.assertEquals(conflictingId.getSiteToSitePort(), conflictingNodeId.getSiteToSitePort());
        Assert.assertEquals(conflictingId.getSocketAddress(), conflictingNodeId.getSocketAddress());
        Assert.assertEquals(conflictingId.getSocketPort(), conflictingNodeId.getSocketPort());
    }

    private static class FirstVoteWinsFlowElection implements FlowElection {
        private DataFlow dataFlow;

        private NodeIdentifier voter;

        @Override
        public boolean isElectionComplete() {
            return (dataFlow) != null;
        }

        @Override
        public synchronized DataFlow castVote(DataFlow candidate, NodeIdentifier nodeIdentifier) {
            if ((dataFlow) == null) {
                dataFlow = candidate;
                voter = nodeIdentifier;
            }
            return dataFlow;
        }

        @Override
        public DataFlow getElectedDataFlow() {
            return dataFlow;
        }

        @Override
        public String getStatusDescription() {
            return "First Vote Wins";
        }

        @Override
        public boolean isVoteCounted(NodeIdentifier nodeIdentifier) {
            return ((voter) != null) && (voter.equals(nodeIdentifier));
        }
    }
}

