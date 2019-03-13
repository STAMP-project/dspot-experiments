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
package org.apache.nifi.controller.queue.clustered;


import LoadBalanceCompression.COMPRESS_ATTRIBUTES_AND_CONTENT;
import LoadBalanceCompression.COMPRESS_ATTRIBUTES_ONLY;
import NodeConnectionState.CONNECTED;
import RepositoryRecordType.CONTENTMISSING;
import RepositoryRecordType.DELETE;
import java.io.IOException;
import java.io.OutputStream;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import javax.net.ssl.SSLContext;
import org.apache.nifi.cluster.coordination.ClusterCoordinator;
import org.apache.nifi.cluster.coordination.ClusterTopologyEventListener;
import org.apache.nifi.cluster.coordination.node.NodeConnectionStatus;
import org.apache.nifi.cluster.protocol.NodeIdentifier;
import org.apache.nifi.controller.FlowController;
import org.apache.nifi.controller.MockFlowFileRecord;
import org.apache.nifi.controller.MockSwapManager;
import org.apache.nifi.controller.ProcessScheduler;
import org.apache.nifi.controller.queue.LoadBalanceCompression;
import org.apache.nifi.controller.queue.LoadBalancedFlowFileQueue;
import org.apache.nifi.controller.queue.NopConnectionEventListener;
import org.apache.nifi.controller.queue.clustered.client.async.nio.NioAsyncLoadBalanceClientRegistry;
import org.apache.nifi.controller.queue.clustered.client.async.nio.NioAsyncLoadBalanceClientTask;
import org.apache.nifi.controller.queue.clustered.partition.FlowFilePartitioner;
import org.apache.nifi.controller.queue.clustered.partition.QueuePartition;
import org.apache.nifi.controller.queue.clustered.partition.RoundRobinPartitioner;
import org.apache.nifi.controller.queue.clustered.server.ConnectionLoadBalanceServer;
import org.apache.nifi.controller.queue.clustered.server.LoadBalanceAuthorizer;
import org.apache.nifi.controller.queue.clustered.server.LoadBalanceProtocol;
import org.apache.nifi.controller.queue.clustered.server.NotAuthorizedException;
import org.apache.nifi.controller.repository.ContentRepository;
import org.apache.nifi.controller.repository.FlowFileRecord;
import org.apache.nifi.controller.repository.FlowFileRepository;
import org.apache.nifi.controller.repository.RepositoryRecord;
import org.apache.nifi.controller.repository.claim.ContentClaim;
import org.apache.nifi.controller.repository.claim.ResourceClaimManager;
import org.apache.nifi.events.EventReporter;
import org.apache.nifi.provenance.ProvenanceRepository;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;


public class LoadBalancedQueueIT {
    private final LoadBalanceAuthorizer ALWAYS_AUTHORIZED = ( sslSocket) -> sslSocket == null ? null : "authorized.mydomain.com";

    private final LoadBalanceAuthorizer NEVER_AUTHORIZED = ( sslSocket) -> {
        throw new NotAuthorizedException("Intentional Unit Test Failure - Not Authorized");
    };

    private final MockSwapManager flowFileSwapManager = new MockSwapManager();

    private final String queueId = "unit-test";

    private final EventReporter eventReporter = EventReporter.NO_OP;

    private final int swapThreshold = 10000;

    private Set<NodeIdentifier> nodeIdentifiers;

    private ClusterCoordinator clusterCoordinator;

    private NodeIdentifier localNodeId;

    private ProcessScheduler processScheduler;

    private ResourceClaimManager resourceClaimManager;

    private LoadBalancedFlowFileQueue serverQueue;

    private FlowController flowController;

    private ProvenanceRepository clientProvRepo;

    private ContentRepository clientContentRepo;

    private List<RepositoryRecord> clientRepoRecords;

    private FlowFileRepository clientFlowFileRepo;

    private ConcurrentMap<ContentClaim, byte[]> clientClaimContents;

    private ProvenanceRepository serverProvRepo;

    private List<RepositoryRecord> serverRepoRecords;

    private FlowFileRepository serverFlowFileRepo;

    private ConcurrentMap<ContentClaim, byte[]> serverClaimContents;

    private ContentRepository serverContentRepo;

    private SSLContext sslContext;

    private final Set<ClusterTopologyEventListener> clusterEventListeners = Collections.synchronizedSet(new HashSet<>());

    private final AtomicReference<LoadBalanceCompression> compressionReference = new AtomicReference<>();

    @Test(timeout = 20000)
    public void testNewNodeAdded() throws IOException, InterruptedException {
        localNodeId = new NodeIdentifier("unit-test-local", "localhost", 7090, "localhost", 7090, "localhost", 7090, null, null, null, false, null);
        nodeIdentifiers.add(localNodeId);
        // Create the server
        final int timeoutMillis = 1000;
        final LoadBalanceProtocol loadBalanceProtocol = new org.apache.nifi.controller.queue.clustered.server.StandardLoadBalanceProtocol(serverFlowFileRepo, serverContentRepo, serverProvRepo, flowController, ALWAYS_AUTHORIZED);
        final SSLContext sslContext = null;
        final NioAsyncLoadBalanceClientRegistry clientRegistry = new NioAsyncLoadBalanceClientRegistry(createClientFactory(sslContext), 1);
        clientRegistry.start();
        final NodeConnectionStatus connectionStatus = Mockito.mock(NodeConnectionStatus.class);
        Mockito.when(connectionStatus.getState()).thenReturn(CONNECTED);
        Mockito.when(clusterCoordinator.getConnectionStatus(ArgumentMatchers.any(NodeIdentifier.class))).thenReturn(connectionStatus);
        final NioAsyncLoadBalanceClientTask clientTask = new NioAsyncLoadBalanceClientTask(clientRegistry, clusterCoordinator, eventReporter);
        final Thread clientThread = new Thread(clientTask);
        final SocketLoadBalancedFlowFileQueue flowFileQueue = new SocketLoadBalancedFlowFileQueue(queueId, new NopConnectionEventListener(), processScheduler, clientFlowFileRepo, clientProvRepo, clientContentRepo, resourceClaimManager, clusterCoordinator, clientRegistry, flowFileSwapManager, swapThreshold, eventReporter);
        flowFileQueue.setFlowFilePartitioner(new RoundRobinPartitioner());
        final int serverCount = 5;
        final ConnectionLoadBalanceServer[] servers = new ConnectionLoadBalanceServer[serverCount];
        try {
            flowFileQueue.startLoadBalancing();
            clientThread.start();
            for (int i = 0; i < serverCount; i++) {
                final ConnectionLoadBalanceServer server = new ConnectionLoadBalanceServer("localhost", 0, sslContext, 8, loadBalanceProtocol, eventReporter, timeoutMillis);
                servers[i] = server;
                server.start();
                final int loadBalancePort = server.getPort();
                // Create the Load Balanced FlowFile Queue
                final NodeIdentifier nodeId = new NodeIdentifier(("unit-test-" + i), "localhost", (8090 + i), "localhost", 8090, "localhost", loadBalancePort, null, null, null, false, null);
                nodeIdentifiers.add(nodeId);
                clusterEventListeners.forEach(( listener) -> listener.onNodeAdded(nodeId));
                for (int j = 0; j < 2; j++) {
                    final Map<String, String> attributes = new HashMap<>();
                    attributes.put("greeting", "hello");
                    final MockFlowFileRecord flowFile = new MockFlowFileRecord(attributes, 0L);
                    flowFileQueue.put(flowFile);
                }
            }
            final int totalFlowFileCount = 7;
            // Wait up to 10 seconds for the server's FlowFile Repository to be updated
            final long endTime = (System.currentTimeMillis()) + (TimeUnit.SECONDS.toMillis(10L));
            while (((serverRepoRecords.size()) < totalFlowFileCount) && ((System.currentTimeMillis()) < endTime)) {
                Thread.sleep(10L);
            } 
            Assert.assertFalse("Server's FlowFile Repo was never fully updated", serverRepoRecords.isEmpty());
            Assert.assertEquals(totalFlowFileCount, serverRepoRecords.size());
            for (final RepositoryRecord serverRecord : serverRepoRecords) {
                final FlowFileRecord serverFlowFile = serverRecord.getCurrent();
                Assert.assertEquals("hello", serverFlowFile.getAttribute("greeting"));
            }
            while ((clientRepoRecords.size()) < totalFlowFileCount) {
                Thread.sleep(10L);
            } 
            Assert.assertEquals(totalFlowFileCount, clientRepoRecords.size());
            for (final RepositoryRecord clientRecord : clientRepoRecords) {
                Assert.assertEquals(DELETE, clientRecord.getType());
            }
        } finally {
            clientTask.stop();
            flowFileQueue.stopLoadBalancing();
            clientRegistry.getAllClients().forEach(AsyncLoadBalanceClient::stop);
            Arrays.stream(servers).filter(Objects::nonNull).forEach(ConnectionLoadBalanceServer::stop);
        }
    }

    @Test(timeout = 90000)
    public void testFailover() throws IOException, InterruptedException {
        localNodeId = new NodeIdentifier("unit-test-local", "localhost", 7090, "localhost", 7090, "localhost", 7090, null, null, null, false, null);
        nodeIdentifiers.add(localNodeId);
        // Create the server
        final int timeoutMillis = 1000;
        final LoadBalanceProtocol loadBalanceProtocol = new org.apache.nifi.controller.queue.clustered.server.StandardLoadBalanceProtocol(serverFlowFileRepo, serverContentRepo, serverProvRepo, flowController, ALWAYS_AUTHORIZED);
        final SSLContext sslContext = null;
        final ConnectionLoadBalanceServer server = new ConnectionLoadBalanceServer("localhost", 0, sslContext, 2, loadBalanceProtocol, eventReporter, timeoutMillis);
        server.start();
        try {
            final int loadBalancePort = server.getPort();
            // Create the Load Balanced FlowFile Queue
            final NodeIdentifier availableNodeId = new NodeIdentifier("unit-test", "localhost", 8090, "localhost", 8090, "localhost", loadBalancePort, null, null, null, false, null);
            nodeIdentifiers.add(availableNodeId);
            // Add a Node Identifier pointing to a non-existent server
            final NodeIdentifier inaccessibleNodeId = new NodeIdentifier("unit-test-invalid-host-does-not-exist", "invalid-host-does-not-exist", 8090, "invalid-host-does-not-exist", 8090, "invalid-host-does-not-exist", loadBalancePort, null, null, null, false, null);
            nodeIdentifiers.add(inaccessibleNodeId);
            final NioAsyncLoadBalanceClientRegistry clientRegistry = new NioAsyncLoadBalanceClientRegistry(createClientFactory(sslContext), 1);
            clientRegistry.start();
            final NodeConnectionStatus connectionStatus = Mockito.mock(NodeConnectionStatus.class);
            Mockito.when(connectionStatus.getState()).thenReturn(CONNECTED);
            Mockito.when(clusterCoordinator.getConnectionStatus(ArgumentMatchers.any(NodeIdentifier.class))).thenReturn(connectionStatus);
            final NioAsyncLoadBalanceClientTask clientTask = new NioAsyncLoadBalanceClientTask(clientRegistry, clusterCoordinator, eventReporter);
            final Thread clientThread = new Thread(clientTask);
            clientThread.setDaemon(true);
            final SocketLoadBalancedFlowFileQueue flowFileQueue = new SocketLoadBalancedFlowFileQueue(queueId, new NopConnectionEventListener(), processScheduler, clientFlowFileRepo, clientProvRepo, clientContentRepo, resourceClaimManager, clusterCoordinator, clientRegistry, flowFileSwapManager, swapThreshold, eventReporter);
            flowFileQueue.setFlowFilePartitioner(new RoundRobinPartitioner());
            try {
                final int numFlowFiles = 1200;
                for (int i = 0; i < numFlowFiles; i++) {
                    final ContentClaim contentClaim = createContentClaim("hello".getBytes());
                    final Map<String, String> attributes = new HashMap<>();
                    attributes.put("uuid", UUID.randomUUID().toString());
                    attributes.put("greeting", "hello");
                    final MockFlowFileRecord flowFile = new MockFlowFileRecord(attributes, 5L, contentClaim);
                    flowFileQueue.put(flowFile);
                }
                flowFileQueue.startLoadBalancing();
                clientThread.start();
                // Sending to one partition should fail. When that happens, half of the FlowFiles should go to the local partition,
                // the other half to the other node. So the total number of FlowFiles expected is ((numFlowFiles per node) / 3 * 1.5)
                final int flowFilesPerNode = numFlowFiles / 3;
                final int expectedFlowFileReceiveCount = flowFilesPerNode + (flowFilesPerNode / 2);
                // Wait up to 10 seconds for the server's FlowFile Repository to be updated
                final long endTime = (System.currentTimeMillis()) + (TimeUnit.SECONDS.toMillis(60L));
                while (((serverRepoRecords.size()) < expectedFlowFileReceiveCount) && ((System.currentTimeMillis()) < endTime)) {
                    Thread.sleep(10L);
                } 
                Assert.assertFalse("Server's FlowFile Repo was never fully updated", serverRepoRecords.isEmpty());
                Assert.assertEquals(expectedFlowFileReceiveCount, serverRepoRecords.size());
                for (final RepositoryRecord serverRecord : serverRepoRecords) {
                    final FlowFileRecord serverFlowFile = serverRecord.getCurrent();
                    Assert.assertEquals("hello", serverFlowFile.getAttribute("greeting"));
                    final ContentClaim serverContentClaim = serverFlowFile.getContentClaim();
                    final byte[] serverFlowFileContent = serverClaimContents.get(serverContentClaim);
                    Assert.assertArrayEquals("hello".getBytes(), Arrays.copyOfRange(serverFlowFileContent, ((serverFlowFileContent.length) - 5), serverFlowFileContent.length));
                }
                // We expect the client records to be numFlowFiles / 2 because half of the FlowFile will have gone to the other node
                // in the cluster and half would still be in the local partition.
                while ((clientRepoRecords.size()) < (numFlowFiles / 2)) {
                    Thread.sleep(10L);
                } 
                Assert.assertEquals((numFlowFiles / 2), clientRepoRecords.size());
                for (final RepositoryRecord clientRecord : clientRepoRecords) {
                    Assert.assertEquals(DELETE, clientRecord.getType());
                }
            } finally {
                flowFileQueue.stopLoadBalancing();
                clientRegistry.getAllClients().forEach(AsyncLoadBalanceClient::stop);
            }
        } finally {
            server.stop();
        }
    }

    @Test(timeout = 20000)
    public void testTransferToRemoteNode() throws IOException, InterruptedException {
        localNodeId = new NodeIdentifier("unit-test-local", "localhost", 7090, "localhost", 7090, "localhost", 7090, null, null, null, false, null);
        nodeIdentifiers.add(localNodeId);
        // Create the server
        final int timeoutMillis = 30000;
        final LoadBalanceProtocol loadBalanceProtocol = new org.apache.nifi.controller.queue.clustered.server.StandardLoadBalanceProtocol(serverFlowFileRepo, serverContentRepo, serverProvRepo, flowController, ALWAYS_AUTHORIZED);
        final SSLContext sslContext = null;
        final ConnectionLoadBalanceServer server = new ConnectionLoadBalanceServer("localhost", 0, sslContext, 2, loadBalanceProtocol, eventReporter, timeoutMillis);
        server.start();
        try {
            final int loadBalancePort = server.getPort();
            // Create the Load Balanced FlowFile Queue
            final NodeIdentifier remoteNodeId = new NodeIdentifier("unit-test", "localhost", 8090, "localhost", 8090, "localhost", loadBalancePort, null, null, null, false, null);
            nodeIdentifiers.add(remoteNodeId);
            final NioAsyncLoadBalanceClientRegistry clientRegistry = new NioAsyncLoadBalanceClientRegistry(createClientFactory(sslContext), 1);
            clientRegistry.start();
            final NodeConnectionStatus connectionStatus = Mockito.mock(NodeConnectionStatus.class);
            Mockito.when(connectionStatus.getState()).thenReturn(CONNECTED);
            Mockito.when(clusterCoordinator.getConnectionStatus(ArgumentMatchers.any(NodeIdentifier.class))).thenReturn(connectionStatus);
            final NioAsyncLoadBalanceClientTask clientTask = new NioAsyncLoadBalanceClientTask(clientRegistry, clusterCoordinator, eventReporter);
            final Thread clientThread = new Thread(clientTask);
            clientThread.setDaemon(true);
            clientThread.start();
            final SocketLoadBalancedFlowFileQueue flowFileQueue = new SocketLoadBalancedFlowFileQueue(queueId, new NopConnectionEventListener(), processScheduler, clientFlowFileRepo, clientProvRepo, clientContentRepo, resourceClaimManager, clusterCoordinator, clientRegistry, flowFileSwapManager, swapThreshold, eventReporter);
            flowFileQueue.setFlowFilePartitioner(new RoundRobinPartitioner());
            try {
                final MockFlowFileRecord firstFlowFile = new MockFlowFileRecord(0L);
                flowFileQueue.put(firstFlowFile);
                final Map<String, String> attributes = new HashMap<>();
                attributes.put("integration", "test");
                attributes.put("unit-test", "false");
                attributes.put("integration-test", "true");
                final ContentClaim contentClaim = createContentClaim("hello".getBytes());
                final MockFlowFileRecord secondFlowFile = new MockFlowFileRecord(attributes, 5L, contentClaim);
                flowFileQueue.put(secondFlowFile);
                flowFileQueue.startLoadBalancing();
                // Wait up to 10 seconds for the server's FlowFile Repository to be updated
                final long endTime = (System.currentTimeMillis()) + (TimeUnit.SECONDS.toMillis(10L));
                while ((serverRepoRecords.isEmpty()) && ((System.currentTimeMillis()) < endTime)) {
                    Thread.sleep(10L);
                } 
                Assert.assertFalse("Server's FlowFile Repo was never updated", serverRepoRecords.isEmpty());
                Assert.assertEquals(1, serverRepoRecords.size());
                final RepositoryRecord serverRecord = serverRepoRecords.iterator().next();
                final FlowFileRecord serverFlowFile = serverRecord.getCurrent();
                Assert.assertEquals("test", serverFlowFile.getAttribute("integration"));
                Assert.assertEquals("false", serverFlowFile.getAttribute("unit-test"));
                Assert.assertEquals("true", serverFlowFile.getAttribute("integration-test"));
                final ContentClaim serverContentClaim = serverFlowFile.getContentClaim();
                final byte[] serverFlowFileContent = serverClaimContents.get(serverContentClaim);
                Assert.assertArrayEquals("hello".getBytes(), serverFlowFileContent);
                while ((clientRepoRecords.size()) == 0) {
                    Thread.sleep(10L);
                } 
                Assert.assertEquals(1, clientRepoRecords.size());
                final RepositoryRecord clientRecord = clientRepoRecords.iterator().next();
                Assert.assertEquals(DELETE, clientRecord.getType());
            } finally {
                flowFileQueue.stopLoadBalancing();
                clientRegistry.getAllClients().forEach(AsyncLoadBalanceClient::stop);
            }
        } finally {
            server.stop();
        }
    }

    @Test(timeout = 20000)
    public void testContentNotFound() throws IOException, InterruptedException {
        localNodeId = new NodeIdentifier("unit-test-local", "localhost", 7090, "localhost", 7090, "localhost", 7090, null, null, null, false, null);
        nodeIdentifiers.add(localNodeId);
        // Create the server
        final int timeoutMillis = 30000;
        final LoadBalanceProtocol loadBalanceProtocol = new org.apache.nifi.controller.queue.clustered.server.StandardLoadBalanceProtocol(serverFlowFileRepo, serverContentRepo, serverProvRepo, flowController, ALWAYS_AUTHORIZED);
        final SSLContext sslContext = null;
        final ConnectionLoadBalanceServer server = new ConnectionLoadBalanceServer("localhost", 0, sslContext, 2, loadBalanceProtocol, eventReporter, timeoutMillis);
        server.start();
        try {
            final int loadBalancePort = server.getPort();
            // Create the Load Balanced FlowFile Queue
            final NodeIdentifier remoteNodeId = new NodeIdentifier("unit-test", "localhost", 8090, "localhost", 8090, "localhost", loadBalancePort, null, null, null, false, null);
            nodeIdentifiers.add(remoteNodeId);
            final NioAsyncLoadBalanceClientRegistry clientRegistry = new NioAsyncLoadBalanceClientRegistry(createClientFactory(sslContext), 1);
            clientRegistry.start();
            final NodeConnectionStatus connectionStatus = Mockito.mock(NodeConnectionStatus.class);
            Mockito.when(connectionStatus.getState()).thenReturn(CONNECTED);
            Mockito.when(clusterCoordinator.getConnectionStatus(ArgumentMatchers.any(NodeIdentifier.class))).thenReturn(connectionStatus);
            final NioAsyncLoadBalanceClientTask clientTask = new NioAsyncLoadBalanceClientTask(clientRegistry, clusterCoordinator, eventReporter);
            final Thread clientThread = new Thread(clientTask);
            clientThread.setDaemon(true);
            clientThread.start();
            final SocketLoadBalancedFlowFileQueue flowFileQueue = new SocketLoadBalancedFlowFileQueue(queueId, new NopConnectionEventListener(), processScheduler, clientFlowFileRepo, clientProvRepo, clientContentRepo, resourceClaimManager, clusterCoordinator, clientRegistry, flowFileSwapManager, swapThreshold, eventReporter);
            flowFileQueue.setFlowFilePartitioner(new RoundRobinPartitioner());
            try {
                final MockFlowFileRecord firstFlowFile = new MockFlowFileRecord(0L);
                flowFileQueue.put(firstFlowFile);
                final Map<String, String> attributes = new HashMap<>();
                attributes.put("integration", "test");
                attributes.put("unit-test", "false");
                attributes.put("integration-test", "true");
                final ContentClaim contentClaim = createContentClaim("hello".getBytes());
                this.clientClaimContents.remove(contentClaim);// cause ContentNotFoundException

                final MockFlowFileRecord secondFlowFile = new MockFlowFileRecord(attributes, 5L, contentClaim);
                flowFileQueue.put(secondFlowFile);
                flowFileQueue.startLoadBalancing();
                while ((clientRepoRecords.size()) == 0) {
                    Thread.sleep(10L);
                } 
                Assert.assertEquals(1, clientRepoRecords.size());
                final RepositoryRecord clientRecord = clientRepoRecords.iterator().next();
                Assert.assertEquals(CONTENTMISSING, clientRecord.getType());
            } finally {
                flowFileQueue.stopLoadBalancing();
                clientRegistry.getAllClients().forEach(AsyncLoadBalanceClient::stop);
            }
        } finally {
            server.stop();
        }
    }

    @Test(timeout = 20000)
    public void testTransferToRemoteNodeAttributeCompression() throws IOException, InterruptedException {
        localNodeId = new NodeIdentifier("unit-test-local", "localhost", 7090, "localhost", 7090, "localhost", 7090, null, null, null, false, null);
        nodeIdentifiers.add(localNodeId);
        compressionReference.set(COMPRESS_ATTRIBUTES_ONLY);
        // Create the server
        final int timeoutMillis = 30000;
        final LoadBalanceProtocol loadBalanceProtocol = new org.apache.nifi.controller.queue.clustered.server.StandardLoadBalanceProtocol(serverFlowFileRepo, serverContentRepo, serverProvRepo, flowController, ALWAYS_AUTHORIZED);
        final SSLContext sslContext = null;
        final ConnectionLoadBalanceServer server = new ConnectionLoadBalanceServer("localhost", 0, sslContext, 2, loadBalanceProtocol, eventReporter, timeoutMillis);
        server.start();
        try {
            final int loadBalancePort = server.getPort();
            // Create the Load Balanced FlowFile Queue
            final NodeIdentifier remoteNodeId = new NodeIdentifier("unit-test", "localhost", 8090, "localhost", 8090, "localhost", loadBalancePort, null, null, null, false, null);
            nodeIdentifiers.add(remoteNodeId);
            final NioAsyncLoadBalanceClientRegistry clientRegistry = new NioAsyncLoadBalanceClientRegistry(createClientFactory(sslContext), 1);
            clientRegistry.start();
            final NodeConnectionStatus connectionStatus = Mockito.mock(NodeConnectionStatus.class);
            Mockito.when(connectionStatus.getState()).thenReturn(CONNECTED);
            Mockito.when(clusterCoordinator.getConnectionStatus(ArgumentMatchers.any(NodeIdentifier.class))).thenReturn(connectionStatus);
            final NioAsyncLoadBalanceClientTask clientTask = new NioAsyncLoadBalanceClientTask(clientRegistry, clusterCoordinator, eventReporter);
            final Thread clientThread = new Thread(clientTask);
            clientThread.setDaemon(true);
            clientThread.start();
            final SocketLoadBalancedFlowFileQueue flowFileQueue = new SocketLoadBalancedFlowFileQueue(queueId, new NopConnectionEventListener(), processScheduler, clientFlowFileRepo, clientProvRepo, clientContentRepo, resourceClaimManager, clusterCoordinator, clientRegistry, flowFileSwapManager, swapThreshold, eventReporter);
            flowFileQueue.setFlowFilePartitioner(new RoundRobinPartitioner());
            flowFileQueue.setLoadBalanceCompression(COMPRESS_ATTRIBUTES_ONLY);
            try {
                final MockFlowFileRecord firstFlowFile = new MockFlowFileRecord(0L);
                flowFileQueue.put(firstFlowFile);
                final Map<String, String> attributes = new HashMap<>();
                attributes.put("integration", "test");
                attributes.put("unit-test", "false");
                attributes.put("integration-test", "true");
                final ContentClaim contentClaim = createContentClaim("hello".getBytes());
                final MockFlowFileRecord secondFlowFile = new MockFlowFileRecord(attributes, 5L, contentClaim);
                flowFileQueue.put(secondFlowFile);
                flowFileQueue.startLoadBalancing();
                // Wait up to 10 seconds for the server's FlowFile Repository to be updated
                final long endTime = (System.currentTimeMillis()) + (TimeUnit.SECONDS.toMillis(10L));
                while ((serverRepoRecords.isEmpty()) && ((System.currentTimeMillis()) < endTime)) {
                    Thread.sleep(10L);
                } 
                Assert.assertFalse("Server's FlowFile Repo was never updated", serverRepoRecords.isEmpty());
                Assert.assertEquals(1, serverRepoRecords.size());
                final RepositoryRecord serverRecord = serverRepoRecords.iterator().next();
                final FlowFileRecord serverFlowFile = serverRecord.getCurrent();
                Assert.assertEquals("test", serverFlowFile.getAttribute("integration"));
                Assert.assertEquals("false", serverFlowFile.getAttribute("unit-test"));
                Assert.assertEquals("true", serverFlowFile.getAttribute("integration-test"));
                final ContentClaim serverContentClaim = serverFlowFile.getContentClaim();
                final byte[] serverFlowFileContent = serverClaimContents.get(serverContentClaim);
                Assert.assertArrayEquals("hello".getBytes(), serverFlowFileContent);
                while ((clientRepoRecords.size()) == 0) {
                    Thread.sleep(10L);
                } 
                Assert.assertEquals(1, clientRepoRecords.size());
                final RepositoryRecord clientRecord = clientRepoRecords.iterator().next();
                Assert.assertEquals(DELETE, clientRecord.getType());
            } finally {
                flowFileQueue.stopLoadBalancing();
                clientRegistry.getAllClients().forEach(AsyncLoadBalanceClient::stop);
            }
        } finally {
            server.stop();
        }
    }

    @Test(timeout = 20000)
    public void testTransferToRemoteNodeContentCompression() throws IOException, InterruptedException {
        localNodeId = new NodeIdentifier("unit-test-local", "localhost", 7090, "localhost", 7090, "localhost", 7090, null, null, null, false, null);
        nodeIdentifiers.add(localNodeId);
        compressionReference.set(COMPRESS_ATTRIBUTES_AND_CONTENT);
        // Create the server
        final int timeoutMillis = 30000;
        final LoadBalanceProtocol loadBalanceProtocol = new org.apache.nifi.controller.queue.clustered.server.StandardLoadBalanceProtocol(serverFlowFileRepo, serverContentRepo, serverProvRepo, flowController, ALWAYS_AUTHORIZED);
        final SSLContext sslContext = null;
        final ConnectionLoadBalanceServer server = new ConnectionLoadBalanceServer("localhost", 0, sslContext, 2, loadBalanceProtocol, eventReporter, timeoutMillis);
        server.start();
        try {
            final int loadBalancePort = server.getPort();
            // Create the Load Balanced FlowFile Queue
            final NodeIdentifier remoteNodeId = new NodeIdentifier("unit-test", "localhost", 8090, "localhost", 8090, "localhost", loadBalancePort, null, null, null, false, null);
            nodeIdentifiers.add(remoteNodeId);
            final NioAsyncLoadBalanceClientRegistry clientRegistry = new NioAsyncLoadBalanceClientRegistry(createClientFactory(sslContext), 1);
            clientRegistry.start();
            final NodeConnectionStatus connectionStatus = Mockito.mock(NodeConnectionStatus.class);
            Mockito.when(connectionStatus.getState()).thenReturn(CONNECTED);
            Mockito.when(clusterCoordinator.getConnectionStatus(ArgumentMatchers.any(NodeIdentifier.class))).thenReturn(connectionStatus);
            final NioAsyncLoadBalanceClientTask clientTask = new NioAsyncLoadBalanceClientTask(clientRegistry, clusterCoordinator, eventReporter);
            final Thread clientThread = new Thread(clientTask);
            clientThread.setDaemon(true);
            clientThread.start();
            final SocketLoadBalancedFlowFileQueue flowFileQueue = new SocketLoadBalancedFlowFileQueue(queueId, new NopConnectionEventListener(), processScheduler, clientFlowFileRepo, clientProvRepo, clientContentRepo, resourceClaimManager, clusterCoordinator, clientRegistry, flowFileSwapManager, swapThreshold, eventReporter);
            flowFileQueue.setFlowFilePartitioner(new RoundRobinPartitioner());
            flowFileQueue.setLoadBalanceCompression(COMPRESS_ATTRIBUTES_AND_CONTENT);
            try {
                final MockFlowFileRecord firstFlowFile = new MockFlowFileRecord(0L);
                flowFileQueue.put(firstFlowFile);
                final Map<String, String> attributes = new HashMap<>();
                attributes.put("integration", "test");
                attributes.put("unit-test", "false");
                attributes.put("integration-test", "true");
                final ContentClaim contentClaim = createContentClaim("hello".getBytes());
                final MockFlowFileRecord secondFlowFile = new MockFlowFileRecord(attributes, 5L, contentClaim);
                flowFileQueue.put(secondFlowFile);
                flowFileQueue.startLoadBalancing();
                // Wait up to 10 seconds for the server's FlowFile Repository to be updated
                final long endTime = (System.currentTimeMillis()) + (TimeUnit.SECONDS.toMillis(10L));
                while ((serverRepoRecords.isEmpty()) && ((System.currentTimeMillis()) < endTime)) {
                    Thread.sleep(10L);
                } 
                Assert.assertFalse("Server's FlowFile Repo was never updated", serverRepoRecords.isEmpty());
                Assert.assertEquals(1, serverRepoRecords.size());
                final RepositoryRecord serverRecord = serverRepoRecords.iterator().next();
                final FlowFileRecord serverFlowFile = serverRecord.getCurrent();
                Assert.assertEquals("test", serverFlowFile.getAttribute("integration"));
                Assert.assertEquals("false", serverFlowFile.getAttribute("unit-test"));
                Assert.assertEquals("true", serverFlowFile.getAttribute("integration-test"));
                final ContentClaim serverContentClaim = serverFlowFile.getContentClaim();
                final byte[] serverFlowFileContent = serverClaimContents.get(serverContentClaim);
                Assert.assertArrayEquals("hello".getBytes(), serverFlowFileContent);
                while ((clientRepoRecords.size()) == 0) {
                    Thread.sleep(10L);
                } 
                Assert.assertEquals(1, clientRepoRecords.size());
                final RepositoryRecord clientRecord = clientRepoRecords.iterator().next();
                Assert.assertEquals(DELETE, clientRecord.getType());
            } finally {
                flowFileQueue.stopLoadBalancing();
                clientRegistry.getAllClients().forEach(AsyncLoadBalanceClient::stop);
            }
        } finally {
            server.stop();
        }
    }

    @Test(timeout = 20000)
    public void testWithSSLContext() throws IOException, InterruptedException, KeyManagementException, KeyStoreException, NoSuchAlgorithmException, UnrecoverableKeyException, CertificateException {
        localNodeId = new NodeIdentifier("unit-test-local", "localhost", 7090, "localhost", 7090, "localhost", 7090, null, null, null, false, null);
        nodeIdentifiers.add(localNodeId);
        // Create the server
        final int timeoutMillis = 30000;
        final LoadBalanceProtocol loadBalanceProtocol = new org.apache.nifi.controller.queue.clustered.server.StandardLoadBalanceProtocol(serverFlowFileRepo, serverContentRepo, serverProvRepo, flowController, ALWAYS_AUTHORIZED);
        final ConnectionLoadBalanceServer server = new ConnectionLoadBalanceServer("localhost", 0, sslContext, 2, loadBalanceProtocol, eventReporter, timeoutMillis);
        server.start();
        try {
            final int loadBalancePort = server.getPort();
            // Create the Load Balanced FlowFile Queue
            final NodeIdentifier remoteNodeId = new NodeIdentifier("unit-test", "localhost", 8090, "localhost", 8090, "localhost", loadBalancePort, null, null, null, false, null);
            nodeIdentifiers.add(remoteNodeId);
            final NioAsyncLoadBalanceClientRegistry clientRegistry = new NioAsyncLoadBalanceClientRegistry(createClientFactory(sslContext), 1);
            clientRegistry.start();
            final NodeConnectionStatus connectionStatus = Mockito.mock(NodeConnectionStatus.class);
            Mockito.when(connectionStatus.getState()).thenReturn(CONNECTED);
            Mockito.when(clusterCoordinator.getConnectionStatus(ArgumentMatchers.any(NodeIdentifier.class))).thenReturn(connectionStatus);
            final NioAsyncLoadBalanceClientTask clientTask = new NioAsyncLoadBalanceClientTask(clientRegistry, clusterCoordinator, eventReporter);
            final Thread clientThread = new Thread(clientTask);
            clientThread.setDaemon(true);
            clientThread.start();
            final SocketLoadBalancedFlowFileQueue flowFileQueue = new SocketLoadBalancedFlowFileQueue(queueId, new NopConnectionEventListener(), processScheduler, clientFlowFileRepo, clientProvRepo, clientContentRepo, resourceClaimManager, clusterCoordinator, clientRegistry, flowFileSwapManager, swapThreshold, eventReporter);
            flowFileQueue.setFlowFilePartitioner(new RoundRobinPartitioner());
            try {
                final MockFlowFileRecord firstFlowFile = new MockFlowFileRecord(0L);
                flowFileQueue.put(firstFlowFile);
                final Map<String, String> attributes = new HashMap<>();
                attributes.put("integration", "test");
                attributes.put("unit-test", "false");
                attributes.put("integration-test", "true");
                final ContentClaim contentClaim = createContentClaim("hello".getBytes());
                final MockFlowFileRecord secondFlowFile = new MockFlowFileRecord(attributes, 5L, contentClaim);
                flowFileQueue.put(secondFlowFile);
                flowFileQueue.startLoadBalancing();
                // Wait up to 10 seconds for the server's FlowFile Repository to be updated
                final long endTime = (System.currentTimeMillis()) + (TimeUnit.SECONDS.toMillis(10L));
                while ((serverRepoRecords.isEmpty()) && ((System.currentTimeMillis()) < endTime)) {
                    Thread.sleep(10L);
                } 
                Assert.assertFalse("Server's FlowFile Repo was never updated", serverRepoRecords.isEmpty());
                Assert.assertEquals(1, serverRepoRecords.size());
                final RepositoryRecord serverRecord = serverRepoRecords.iterator().next();
                final FlowFileRecord serverFlowFile = serverRecord.getCurrent();
                Assert.assertEquals("test", serverFlowFile.getAttribute("integration"));
                Assert.assertEquals("false", serverFlowFile.getAttribute("unit-test"));
                Assert.assertEquals("true", serverFlowFile.getAttribute("integration-test"));
                final ContentClaim serverContentClaim = serverFlowFile.getContentClaim();
                final byte[] serverFlowFileContent = serverClaimContents.get(serverContentClaim);
                Assert.assertArrayEquals("hello".getBytes(), serverFlowFileContent);
                while ((clientRepoRecords.size()) == 0) {
                    Thread.sleep(10L);
                } 
                Assert.assertEquals(1, clientRepoRecords.size());
                final RepositoryRecord clientRecord = clientRepoRecords.iterator().next();
                Assert.assertEquals(DELETE, clientRecord.getType());
            } finally {
                flowFileQueue.stopLoadBalancing();
                clientRegistry.getAllClients().forEach(AsyncLoadBalanceClient::stop);
            }
        } finally {
            server.stop();
        }
    }

    @Test(timeout = 60000)
    public void testReusingClient() throws IOException, InterruptedException, KeyManagementException, KeyStoreException, NoSuchAlgorithmException, UnrecoverableKeyException, CertificateException {
        localNodeId = new NodeIdentifier("unit-test-local", "localhost", 7090, "localhost", 7090, "localhost", 7090, null, null, null, false, null);
        nodeIdentifiers.add(localNodeId);
        // Create the server
        final int timeoutMillis = 30000;
        final LoadBalanceProtocol loadBalanceProtocol = new org.apache.nifi.controller.queue.clustered.server.StandardLoadBalanceProtocol(serverFlowFileRepo, serverContentRepo, serverProvRepo, flowController, ALWAYS_AUTHORIZED);
        final ConnectionLoadBalanceServer server = new ConnectionLoadBalanceServer("localhost", 0, sslContext, 2, loadBalanceProtocol, eventReporter, timeoutMillis);
        server.start();
        try {
            final int loadBalancePort = server.getPort();
            // Create the Load Balanced FlowFile Queue
            final NodeIdentifier remoteNodeId = new NodeIdentifier("unit-test", "localhost", 8090, "localhost", 8090, "localhost", loadBalancePort, null, null, null, false, null);
            nodeIdentifiers.add(remoteNodeId);
            final NioAsyncLoadBalanceClientRegistry clientRegistry = new NioAsyncLoadBalanceClientRegistry(createClientFactory(sslContext), 1);
            clientRegistry.start();
            final NodeConnectionStatus connectionStatus = Mockito.mock(NodeConnectionStatus.class);
            Mockito.when(connectionStatus.getState()).thenReturn(CONNECTED);
            Mockito.when(clusterCoordinator.getConnectionStatus(ArgumentMatchers.any(NodeIdentifier.class))).thenReturn(connectionStatus);
            final NioAsyncLoadBalanceClientTask clientTask = new NioAsyncLoadBalanceClientTask(clientRegistry, clusterCoordinator, eventReporter);
            final Thread clientThread = new Thread(clientTask);
            clientThread.setDaemon(true);
            clientThread.start();
            final SocketLoadBalancedFlowFileQueue flowFileQueue = new SocketLoadBalancedFlowFileQueue(queueId, new NopConnectionEventListener(), processScheduler, clientFlowFileRepo, clientProvRepo, clientContentRepo, resourceClaimManager, clusterCoordinator, clientRegistry, flowFileSwapManager, swapThreshold, eventReporter);
            flowFileQueue.setFlowFilePartitioner(new RoundRobinPartitioner());
            try {
                for (int i = 1; i <= 10; i++) {
                    final MockFlowFileRecord firstFlowFile = new MockFlowFileRecord(0L);
                    flowFileQueue.put(firstFlowFile);
                    final Map<String, String> attributes = new HashMap<>();
                    attributes.put("integration", "test");
                    attributes.put("unit-test", "false");
                    attributes.put("integration-test", "true");
                    final ContentClaim contentClaim = createContentClaim("hello".getBytes());
                    final MockFlowFileRecord secondFlowFile = new MockFlowFileRecord(attributes, 5L, contentClaim);
                    flowFileQueue.put(secondFlowFile);
                    flowFileQueue.startLoadBalancing();
                    // Wait up to 10 seconds for the server's FlowFile Repository to be updated
                    final long endTime = (System.currentTimeMillis()) + (TimeUnit.SECONDS.toMillis(10L));
                    while (((serverRepoRecords.size()) < i) && ((System.currentTimeMillis()) < endTime)) {
                        Thread.sleep(10L);
                    } 
                    Assert.assertEquals(i, serverRepoRecords.size());
                    final RepositoryRecord serverRecord = serverRepoRecords.iterator().next();
                    final FlowFileRecord serverFlowFile = serverRecord.getCurrent();
                    Assert.assertEquals("test", serverFlowFile.getAttribute("integration"));
                    Assert.assertEquals("false", serverFlowFile.getAttribute("unit-test"));
                    Assert.assertEquals("true", serverFlowFile.getAttribute("integration-test"));
                    final ContentClaim serverContentClaim = serverFlowFile.getContentClaim();
                    final byte[] serverFlowFileContent = serverClaimContents.get(serverContentClaim);
                    Assert.assertArrayEquals("hello".getBytes(), serverFlowFileContent);
                    while ((clientRepoRecords.size()) < i) {
                        Thread.sleep(10L);
                    } 
                    Assert.assertEquals(i, clientRepoRecords.size());
                    final RepositoryRecord clientRecord = clientRepoRecords.iterator().next();
                    Assert.assertEquals(DELETE, clientRecord.getType());
                }
            } finally {
                flowFileQueue.stopLoadBalancing();
                clientRegistry.getAllClients().forEach(AsyncLoadBalanceClient::stop);
            }
        } finally {
            server.stop();
        }
    }

    @Test(timeout = 20000)
    public void testLargePayload() throws IOException, InterruptedException, KeyManagementException, KeyStoreException, NoSuchAlgorithmException, UnrecoverableKeyException, CertificateException {
        localNodeId = new NodeIdentifier("unit-test-local", "localhost", 7090, "localhost", 7090, "localhost", 7090, null, null, null, false, null);
        nodeIdentifiers.add(localNodeId);
        // Create the server
        final int timeoutMillis = 30000;
        final LoadBalanceProtocol loadBalanceProtocol = new org.apache.nifi.controller.queue.clustered.server.StandardLoadBalanceProtocol(serverFlowFileRepo, serverContentRepo, serverProvRepo, flowController, ALWAYS_AUTHORIZED);
        final ConnectionLoadBalanceServer server = new ConnectionLoadBalanceServer("localhost", 0, sslContext, 2, loadBalanceProtocol, eventReporter, timeoutMillis);
        server.start();
        try {
            final int loadBalancePort = server.getPort();
            // Create the Load Balanced FlowFile Queue
            final NodeIdentifier remoteNodeId = new NodeIdentifier("unit-test", "localhost", 8090, "localhost", 8090, "localhost", loadBalancePort, null, null, null, false, null);
            nodeIdentifiers.add(remoteNodeId);
            final NioAsyncLoadBalanceClientRegistry clientRegistry = new NioAsyncLoadBalanceClientRegistry(createClientFactory(sslContext), 1);
            clientRegistry.start();
            final NodeConnectionStatus connectionStatus = Mockito.mock(NodeConnectionStatus.class);
            Mockito.when(connectionStatus.getState()).thenReturn(CONNECTED);
            Mockito.when(clusterCoordinator.getConnectionStatus(ArgumentMatchers.any(NodeIdentifier.class))).thenReturn(connectionStatus);
            final NioAsyncLoadBalanceClientTask clientTask = new NioAsyncLoadBalanceClientTask(clientRegistry, clusterCoordinator, eventReporter);
            final Thread clientThread = new Thread(clientTask);
            clientThread.setDaemon(true);
            clientThread.start();
            final SocketLoadBalancedFlowFileQueue flowFileQueue = new SocketLoadBalancedFlowFileQueue(queueId, new NopConnectionEventListener(), processScheduler, clientFlowFileRepo, clientProvRepo, clientContentRepo, resourceClaimManager, clusterCoordinator, clientRegistry, flowFileSwapManager, swapThreshold, eventReporter);
            flowFileQueue.setFlowFilePartitioner(new RoundRobinPartitioner());
            final byte[] payload = new byte[1024 * 1024];
            Arrays.fill(payload, ((byte) ('A')));
            try {
                final MockFlowFileRecord firstFlowFile = new MockFlowFileRecord(0L);
                flowFileQueue.put(firstFlowFile);
                final Map<String, String> attributes = new HashMap<>();
                attributes.put("integration", "test");
                attributes.put("unit-test", "false");
                attributes.put("integration-test", "true");
                final ContentClaim contentClaim = createContentClaim(payload);
                final MockFlowFileRecord secondFlowFile = new MockFlowFileRecord(attributes, payload.length, contentClaim);
                flowFileQueue.put(secondFlowFile);
                flowFileQueue.startLoadBalancing();
                // Wait up to 10 seconds for the server's FlowFile Repository to be updated
                final long endTime = (System.currentTimeMillis()) + (TimeUnit.SECONDS.toMillis(10L));
                while ((serverRepoRecords.isEmpty()) && ((System.currentTimeMillis()) < endTime)) {
                    Thread.sleep(10L);
                } 
                Assert.assertFalse("Server's FlowFile Repo was never updated", serverRepoRecords.isEmpty());
                Assert.assertEquals(1, serverRepoRecords.size());
                final RepositoryRecord serverRecord = serverRepoRecords.iterator().next();
                final FlowFileRecord serverFlowFile = serverRecord.getCurrent();
                Assert.assertEquals("test", serverFlowFile.getAttribute("integration"));
                Assert.assertEquals("false", serverFlowFile.getAttribute("unit-test"));
                Assert.assertEquals("true", serverFlowFile.getAttribute("integration-test"));
                final ContentClaim serverContentClaim = serverFlowFile.getContentClaim();
                final byte[] serverFlowFileContent = serverClaimContents.get(serverContentClaim);
                Assert.assertArrayEquals(payload, serverFlowFileContent);
                while ((clientRepoRecords.size()) == 0) {
                    Thread.sleep(10L);
                } 
                Assert.assertEquals(1, clientRepoRecords.size());
                final RepositoryRecord clientRecord = clientRepoRecords.iterator().next();
                Assert.assertEquals(DELETE, clientRecord.getType());
            } finally {
                flowFileQueue.stopLoadBalancing();
                clientRegistry.getAllClients().forEach(AsyncLoadBalanceClient::stop);
            }
        } finally {
            server.stop();
        }
    }

    @Test(timeout = 60000)
    public void testServerClosesUnexpectedly() throws IOException, InterruptedException {
        Mockito.doAnswer(new Answer<OutputStream>() {
            int iterations = 0;

            @Override
            public OutputStream answer(final InvocationOnMock invocation) {
                if (((iterations)++) < 5) {
                    return new OutputStream() {
                        @Override
                        public void write(final int b) throws IOException {
                            throw new IOException("Intentional unit test failure");
                        }
                    };
                }
                final ContentClaim contentClaim = getArgumentAt(0, ContentClaim.class);
                final java.io.ByteArrayOutputStream baos = new java.io.ByteArrayOutputStream() {
                    @Override
                    public void close() throws IOException {
                        super.close();
                        serverClaimContents.put(contentClaim, toByteArray());
                    }
                };
                return baos;
            }
        }).when(serverContentRepo).write(ArgumentMatchers.any(ContentClaim.class));
        // Create the server
        final int timeoutMillis = 30000;
        final LoadBalanceProtocol loadBalanceProtocol = new org.apache.nifi.controller.queue.clustered.server.StandardLoadBalanceProtocol(serverFlowFileRepo, serverContentRepo, serverProvRepo, flowController, ALWAYS_AUTHORIZED);
        final SSLContext sslContext = null;
        final ConnectionLoadBalanceServer server = new ConnectionLoadBalanceServer("localhost", 0, sslContext, 2, loadBalanceProtocol, eventReporter, timeoutMillis);
        server.start();
        try {
            final int loadBalancePort = server.getPort();
            // Create the Load Balanced FlowFile Queue
            final NodeIdentifier remoteNodeId = new NodeIdentifier("unit-test", "localhost", 8090, "localhost", 8090, "localhost", loadBalancePort, null, null, null, false, null);
            nodeIdentifiers.add(remoteNodeId);
            final NioAsyncLoadBalanceClientRegistry clientRegistry = new NioAsyncLoadBalanceClientRegistry(createClientFactory(sslContext), 1);
            clientRegistry.start();
            final NodeConnectionStatus connectionStatus = Mockito.mock(NodeConnectionStatus.class);
            Mockito.when(connectionStatus.getState()).thenReturn(CONNECTED);
            Mockito.when(clusterCoordinator.getConnectionStatus(ArgumentMatchers.any(NodeIdentifier.class))).thenReturn(connectionStatus);
            final NioAsyncLoadBalanceClientTask clientTask = new NioAsyncLoadBalanceClientTask(clientRegistry, clusterCoordinator, eventReporter);
            final Thread clientThread = new Thread(clientTask);
            clientThread.setDaemon(true);
            clientThread.start();
            final SocketLoadBalancedFlowFileQueue flowFileQueue = new SocketLoadBalancedFlowFileQueue(queueId, new NopConnectionEventListener(), processScheduler, clientFlowFileRepo, clientProvRepo, clientContentRepo, resourceClaimManager, clusterCoordinator, clientRegistry, flowFileSwapManager, swapThreshold, eventReporter);
            flowFileQueue.setFlowFilePartitioner(new FlowFilePartitioner() {
                @Override
                public QueuePartition getPartition(final FlowFileRecord flowFile, final QueuePartition[] partitions, final QueuePartition localPartition) {
                    for (final QueuePartition partition : partitions) {
                        if (partition != localPartition) {
                            return partition;
                        }
                    }
                    return null;
                }

                @Override
                public boolean isRebalanceOnClusterResize() {
                    return true;
                }

                @Override
                public boolean isRebalanceOnFailure() {
                    return true;
                }
            });
            try {
                final Map<String, String> attributes = new HashMap<>();
                attributes.put("integration", "test");
                attributes.put("unit-test", "false");
                attributes.put("integration-test", "true");
                final ContentClaim contentClaim = createContentClaim("hello".getBytes());
                final MockFlowFileRecord secondFlowFile = new MockFlowFileRecord(attributes, 5L, contentClaim);
                flowFileQueue.put(secondFlowFile);
                flowFileQueue.startLoadBalancing();
                // Wait up to 10 seconds for the server's FlowFile Repository to be updated
                final long endTime = (System.currentTimeMillis()) + (TimeUnit.SECONDS.toMillis(10L));
                while ((serverRepoRecords.isEmpty()) && ((System.currentTimeMillis()) < endTime)) {
                    Thread.sleep(10L);
                } 
                Assert.assertFalse("Server's FlowFile Repo was never updated", serverRepoRecords.isEmpty());
                Assert.assertEquals(1, serverRepoRecords.size());
                final RepositoryRecord serverRecord = serverRepoRecords.iterator().next();
                final FlowFileRecord serverFlowFile = serverRecord.getCurrent();
                Assert.assertEquals("test", serverFlowFile.getAttribute("integration"));
                Assert.assertEquals("false", serverFlowFile.getAttribute("unit-test"));
                Assert.assertEquals("true", serverFlowFile.getAttribute("integration-test"));
                final ContentClaim serverContentClaim = serverFlowFile.getContentClaim();
                final byte[] serverFlowFileContent = serverClaimContents.get(serverContentClaim);
                Assert.assertArrayEquals("hello".getBytes(), serverFlowFileContent);
                while ((clientRepoRecords.size()) == 0) {
                    Thread.sleep(10L);
                } 
                Assert.assertEquals(1, clientRepoRecords.size());
                final RepositoryRecord clientRecord = clientRepoRecords.iterator().next();
                Assert.assertEquals(DELETE, clientRecord.getType());
            } finally {
                flowFileQueue.stopLoadBalancing();
                clientRegistry.getAllClients().forEach(AsyncLoadBalanceClient::stop);
            }
        } finally {
            server.stop();
        }
    }

    @Test(timeout = 20000)
    public void testNotAuthorized() throws IOException, InterruptedException {
        localNodeId = new NodeIdentifier("unit-test-local", "localhost", 7090, "localhost", 7090, "localhost", 7090, null, null, null, false, null);
        nodeIdentifiers.add(localNodeId);
        // Create the server
        final int timeoutMillis = 30000;
        final LoadBalanceProtocol loadBalanceProtocol = new org.apache.nifi.controller.queue.clustered.server.StandardLoadBalanceProtocol(serverFlowFileRepo, serverContentRepo, serverProvRepo, flowController, NEVER_AUTHORIZED);
        final ConnectionLoadBalanceServer server = new ConnectionLoadBalanceServer("localhost", 0, sslContext, 2, loadBalanceProtocol, eventReporter, timeoutMillis);
        server.start();
        try {
            final int loadBalancePort = server.getPort();
            // Create the Load Balanced FlowFile Queue
            final NodeIdentifier remoteNodeId = new NodeIdentifier("unit-test", "localhost", 8090, "localhost", 8090, "localhost", loadBalancePort, null, null, null, false, null);
            nodeIdentifiers.add(remoteNodeId);
            final NioAsyncLoadBalanceClientRegistry clientRegistry = new NioAsyncLoadBalanceClientRegistry(createClientFactory(sslContext), 1);
            clientRegistry.start();
            final NodeConnectionStatus connectionStatus = Mockito.mock(NodeConnectionStatus.class);
            Mockito.when(connectionStatus.getState()).thenReturn(CONNECTED);
            Mockito.when(clusterCoordinator.getConnectionStatus(ArgumentMatchers.any(NodeIdentifier.class))).thenReturn(connectionStatus);
            final NioAsyncLoadBalanceClientTask clientTask = new NioAsyncLoadBalanceClientTask(clientRegistry, clusterCoordinator, eventReporter);
            final Thread clientThread = new Thread(clientTask);
            clientThread.setDaemon(true);
            clientThread.start();
            final SocketLoadBalancedFlowFileQueue flowFileQueue = new SocketLoadBalancedFlowFileQueue(queueId, new NopConnectionEventListener(), processScheduler, clientFlowFileRepo, clientProvRepo, clientContentRepo, resourceClaimManager, clusterCoordinator, clientRegistry, flowFileSwapManager, swapThreshold, eventReporter);
            flowFileQueue.setFlowFilePartitioner(new RoundRobinPartitioner());
            try {
                final MockFlowFileRecord firstFlowFile = new MockFlowFileRecord(0L);
                flowFileQueue.put(firstFlowFile);
                final Map<String, String> attributes = new HashMap<>();
                attributes.put("integration", "test");
                attributes.put("unit-test", "false");
                attributes.put("integration-test", "true");
                final ContentClaim contentClaim = createContentClaim("hello".getBytes());
                final MockFlowFileRecord secondFlowFile = new MockFlowFileRecord(attributes, 5L, contentClaim);
                flowFileQueue.put(secondFlowFile);
                flowFileQueue.startLoadBalancing();
                Thread.sleep(5000L);
                Assert.assertTrue("Server's FlowFile Repo was updated", serverRepoRecords.isEmpty());
                Assert.assertTrue(clientRepoRecords.isEmpty());
                Assert.assertEquals(2, flowFileQueue.size().getObjectCount());
            } finally {
                flowFileQueue.stopLoadBalancing();
                clientRegistry.getAllClients().forEach(AsyncLoadBalanceClient::stop);
            }
        } finally {
            server.stop();
        }
    }

    @Test(timeout = 35000)
    public void testDestinationNodeQueueFull() throws IOException, InterruptedException {
        localNodeId = new NodeIdentifier("unit-test-local", "localhost", 7090, "localhost", 7090, "localhost", 7090, null, null, null, false, null);
        nodeIdentifiers.add(localNodeId);
        Mockito.when(serverQueue.isLocalPartitionFull()).thenReturn(true);
        // Create the server
        final int timeoutMillis = 30000;
        final LoadBalanceProtocol loadBalanceProtocol = new org.apache.nifi.controller.queue.clustered.server.StandardLoadBalanceProtocol(serverFlowFileRepo, serverContentRepo, serverProvRepo, flowController, ALWAYS_AUTHORIZED);
        final ConnectionLoadBalanceServer server = new ConnectionLoadBalanceServer("localhost", 0, sslContext, 2, loadBalanceProtocol, eventReporter, timeoutMillis);
        server.start();
        try {
            final int loadBalancePort = server.getPort();
            // Create the Load Balanced FlowFile Queue
            final NodeIdentifier remoteNodeId = new NodeIdentifier("unit-test", "localhost", 8090, "localhost", 8090, "localhost", loadBalancePort, null, null, null, false, null);
            nodeIdentifiers.add(remoteNodeId);
            final NioAsyncLoadBalanceClientRegistry clientRegistry = new NioAsyncLoadBalanceClientRegistry(createClientFactory(sslContext), 1);
            clientRegistry.start();
            final NodeConnectionStatus connectionStatus = Mockito.mock(NodeConnectionStatus.class);
            Mockito.when(connectionStatus.getState()).thenReturn(CONNECTED);
            Mockito.when(clusterCoordinator.getConnectionStatus(ArgumentMatchers.any(NodeIdentifier.class))).thenReturn(connectionStatus);
            final NioAsyncLoadBalanceClientTask clientTask = new NioAsyncLoadBalanceClientTask(clientRegistry, clusterCoordinator, eventReporter);
            final Thread clientThread = new Thread(clientTask);
            clientThread.setDaemon(true);
            clientThread.start();
            final SocketLoadBalancedFlowFileQueue flowFileQueue = new SocketLoadBalancedFlowFileQueue(queueId, new NopConnectionEventListener(), processScheduler, clientFlowFileRepo, clientProvRepo, clientContentRepo, resourceClaimManager, clusterCoordinator, clientRegistry, flowFileSwapManager, swapThreshold, eventReporter);
            flowFileQueue.setFlowFilePartitioner(new RoundRobinPartitioner());
            try {
                final MockFlowFileRecord firstFlowFile = new MockFlowFileRecord(0L);
                flowFileQueue.put(firstFlowFile);
                final Map<String, String> attributes = new HashMap<>();
                attributes.put("integration", "test");
                attributes.put("unit-test", "false");
                attributes.put("integration-test", "true");
                final ContentClaim contentClaim = createContentClaim("hello".getBytes());
                final MockFlowFileRecord secondFlowFile = new MockFlowFileRecord(attributes, 5L, contentClaim);
                flowFileQueue.put(secondFlowFile);
                flowFileQueue.startLoadBalancing();
                Thread.sleep(5000L);
                Assert.assertTrue("Server's FlowFile Repo was updated", serverRepoRecords.isEmpty());
                Assert.assertTrue(clientRepoRecords.isEmpty());
                Assert.assertEquals(2, flowFileQueue.size().getObjectCount());
                // Enable data to be transferred
                Mockito.when(serverQueue.isLocalPartitionFull()).thenReturn(false);
                while ((clientRepoRecords.size()) != 1) {
                    Thread.sleep(10L);
                } 
                Assert.assertEquals(1, serverRepoRecords.size());
            } finally {
                flowFileQueue.stopLoadBalancing();
                clientRegistry.getAllClients().forEach(AsyncLoadBalanceClient::stop);
            }
        } finally {
            server.stop();
        }
    }
}

