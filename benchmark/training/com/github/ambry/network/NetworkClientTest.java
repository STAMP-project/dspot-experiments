/**
 * Copyright 2016 LinkedIn Corp. All rights reserved.
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
 */
package com.github.ambry.network;


import NetworkClientErrorCode.ConnectionUnavailable;
import NetworkClientErrorCode.NetworkError;
import PortType.PLAINTEXT;
import PortType.SSL;
import com.codahale.metrics.MetricRegistry;
import com.github.ambry.clustermap.DataNodeId;
import com.github.ambry.clustermap.MockClusterMap;
import com.github.ambry.clustermap.MockDataNodeId;
import com.github.ambry.config.NetworkConfig;
import com.github.ambry.config.VerifiableProperties;
import com.github.ambry.utils.MockTime;
import com.github.ambry.utils.Time;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;
import org.junit.Assert;
import org.junit.Test;

import static PortType.PLAINTEXT;
import static PortType.SSL;


/**
 * Test the {@link NetworkClient}
 */
public class NetworkClientTest {
    private final int CHECKOUT_TIMEOUT_MS = 1000;

    private final int MAX_PORTS_PLAIN_TEXT = 3;

    private final int MAX_PORTS_SSL = 3;

    private final Time time;

    MockSelector selector;

    NetworkClient networkClient;

    String host1 = "host1";

    Port port1 = new Port(2222, PLAINTEXT);

    String host2 = "host2";

    Port port2 = new Port(3333, SSL);

    /**
     * Test the {@link NetworkClientFactory}
     */
    @Test
    public void testNetworkClientFactory() throws IOException {
        Properties props = new Properties();
        props.setProperty("router.connection.checkout.timeout.ms", "1000");
        VerifiableProperties vprops = new VerifiableProperties(props);
        NetworkConfig networkConfig = new NetworkConfig(vprops);
        NetworkMetrics networkMetrics = new NetworkMetrics(new MetricRegistry());
        NetworkClientFactory networkClientFactory = new NetworkClientFactory(networkMetrics, networkConfig, null, MAX_PORTS_PLAIN_TEXT, MAX_PORTS_SSL, CHECKOUT_TIMEOUT_MS, new MockTime());
        Assert.assertNotNull("NetworkClient returned should be non-null", networkClientFactory.getNetworkClient());
    }

    public NetworkClientTest() throws IOException {
        Properties props = new Properties();
        VerifiableProperties vprops = new VerifiableProperties(props);
        NetworkConfig networkConfig = new NetworkConfig(vprops);
        selector = new MockSelector();
        time = new MockTime();
        networkClient = new NetworkClient(selector, networkConfig, new NetworkMetrics(new MetricRegistry()), MAX_PORTS_PLAIN_TEXT, MAX_PORTS_SSL, CHECKOUT_TIMEOUT_MS, time);
    }

    /**
     * Test {@link NetworkClient#warmUpConnections(List, int, long)}
     */
    @Test
    public void testWarmUpConnections() throws IOException {
        MockClusterMap mockClusterMap = new MockClusterMap(true, 9, 3, 3, false);
        List<DataNodeId> localDataNodeIds = mockClusterMap.getDataNodeIds().stream().filter(( dataNodeId) -> mockClusterMap.getDatacenterName(mockClusterMap.getLocalDatacenterId()).equals(dataNodeId.getDatacenterName())).collect(Collectors.toList());
        int maxPort = (mockClusterMap.isSslPortsEnabled()) ? MAX_PORTS_SSL : MAX_PORTS_PLAIN_TEXT;
        // warm up plain-text connections.
        doTestWarmUpConnections(localDataNodeIds, maxPort, PLAINTEXT);
        // enable SSL to local DC.
        for (DataNodeId dataNodeId : localDataNodeIds) {
            ((MockDataNodeId) (dataNodeId)).setSslEnabledDataCenters(Collections.singletonList(mockClusterMap.getDatacenterName(mockClusterMap.getLocalDatacenterId())));
        }
        // warm up SSL connections.`
        doTestWarmUpConnections(localDataNodeIds, maxPort, SSL);
    }

    /**
     * Test {@link NetworkClient#warmUpConnections(List, int, long)}
     */
    @Test
    public void testWarmUpConnectionsSsl() throws IOException {
        MockClusterMap mockClusterMap = new MockClusterMap();
        List<DataNodeId> localDataNodeIds = mockClusterMap.getDataNodeIds().stream().filter(( dataNodeId) -> mockClusterMap.getDatacenterName(mockClusterMap.getLocalDatacenterId()).equals(dataNodeId.getDatacenterName())).collect(Collectors.toList());
        int maxPort = (mockClusterMap.isSslPortsEnabled()) ? MAX_PORTS_SSL : MAX_PORTS_PLAIN_TEXT;
        Assert.assertEquals("Connection count is not expected", (maxPort * (localDataNodeIds.size())), networkClient.warmUpConnections(localDataNodeIds, 100, 2000));
        Assert.assertEquals("Connection count is not expected", (((50 * maxPort) / 100) * (localDataNodeIds.size())), networkClient.warmUpConnections(localDataNodeIds, 50, 2000));
        Assert.assertEquals("Connection count is not expected", 0, networkClient.warmUpConnections(localDataNodeIds, 0, 2000));
        selector.setState(MockSelectorState.FailConnectionInitiationOnPoll);
        Assert.assertEquals("Connection count is not expected", 0, networkClient.warmUpConnections(localDataNodeIds, 100, 2000));
        selector.setState(MockSelectorState.Good);
    }

    /**
     * Test connection warm up failed case.
     */
    @Test
    public void testWarmUpConnectionsFailedAll() throws IOException {
        MockClusterMap mockClusterMap = new MockClusterMap();
        List<DataNodeId> localDataNodeIds = mockClusterMap.getDataNodeIds().stream().filter(( dataNodeId) -> mockClusterMap.getDatacenterName(mockClusterMap.getLocalDatacenterId()).equals(dataNodeId.getDatacenterName())).collect(Collectors.toList());
        selector.setState(MockSelectorState.FailConnectionInitiationOnPoll);
        Assert.assertEquals("Connection count is not expected", 0, networkClient.warmUpConnections(localDataNodeIds, 2, 2000));
        selector.setState(MockSelectorState.Good);
    }

    /**
     * tests basic request sending, polling and receiving responses correctly associated with the requests.
     */
    @Test
    public void testBasicSendAndPoll() throws IOException {
        List<RequestInfo> requestInfoList = new ArrayList<RequestInfo>();
        List<ResponseInfo> responseInfoList;
        requestInfoList.add(new RequestInfo(host1, port1, new MockSend(1)));
        requestInfoList.add(new RequestInfo(host1, port1, new MockSend(2)));
        int requestCount = requestInfoList.size();
        int responseCount = 0;
        do {
            responseInfoList = networkClient.sendAndPoll(requestInfoList, 100);
            requestInfoList.clear();
            for (ResponseInfo responseInfo : responseInfoList) {
                MockSend send = ((MockSend) (responseInfo.getRequestInfo().getRequest()));
                NetworkClientErrorCode error = responseInfo.getError();
                ByteBuffer response = responseInfo.getResponse();
                Assert.assertNull("Should not have encountered an error", error);
                Assert.assertNotNull("Should receive a valid response", response);
                int correlationIdInRequest = send.getCorrelationId();
                int correlationIdInResponse = response.getInt();
                Assert.assertEquals("Received response for the wrong request", correlationIdInRequest, correlationIdInResponse);
                responseCount++;
            }
        } while (requestCount > responseCount );
        Assert.assertEquals("Should receive only as many responses as there were requests", requestCount, responseCount);
        responseInfoList = networkClient.sendAndPoll(requestInfoList, 100);
        requestInfoList.clear();
        Assert.assertEquals("No responses are expected at this time", 0, responseInfoList.size());
    }

    /**
     * Tests a failure scenario where requests remain too long in the {@link NetworkClient}'s pending requests queue.
     */
    @Test
    public void testConnectionUnavailable() throws IOException, InterruptedException {
        List<RequestInfo> requestInfoList = new ArrayList<RequestInfo>();
        List<ResponseInfo> responseInfoList;
        requestInfoList.add(new RequestInfo(host2, port2, new MockSend(3)));
        requestInfoList.add(new RequestInfo(host2, port2, new MockSend(4)));
        int requestCount = requestInfoList.size();
        int responseCount = 0;
        responseInfoList = networkClient.sendAndPoll(requestInfoList, 100);
        requestInfoList.clear();
        // The first sendAndPoll() initiates the connections. So, after the selector poll, new connections
        // would have been established, but no new responses or disconnects, so the NetworkClient should not have been
        // able to create any ResponseInfos.
        Assert.assertEquals("There are no responses expected", 0, responseInfoList.size());
        // the requests were queued. Now increment the time so that they get timed out in the next sendAndPoll.
        time.sleep(((CHECKOUT_TIMEOUT_MS) + 1));
        do {
            responseInfoList = networkClient.sendAndPoll(requestInfoList, 100);
            requestInfoList.clear();
            for (ResponseInfo responseInfo : responseInfoList) {
                NetworkClientErrorCode error = responseInfo.getError();
                ByteBuffer response = responseInfo.getResponse();
                Assert.assertNotNull("Should have encountered an error", error);
                Assert.assertEquals("Should have received a connection unavailable error", ConnectionUnavailable, error);
                Assert.assertNull("Should not have received a valid response", response);
                responseCount++;
            }
        } while (requestCount > responseCount );
        responseInfoList = networkClient.sendAndPoll(requestInfoList, 100);
        requestInfoList.clear();
        Assert.assertEquals("No responses are expected at this time", 0, responseInfoList.size());
    }

    /**
     * Tests a failure scenario where connections get disconnected after requests are sent out.
     */
    @Test
    public void testNetworkError() throws IOException, InterruptedException {
        List<RequestInfo> requestInfoList = new ArrayList<RequestInfo>();
        List<ResponseInfo> responseInfoList;
        requestInfoList.add(new RequestInfo(host2, port2, new MockSend(5)));
        requestInfoList.add(new RequestInfo(host2, port2, new MockSend(6)));
        int requestCount = requestInfoList.size();
        int responseCount = 0;
        // set beBad so that requests end up failing due to "network error".
        selector.setState(MockSelectorState.DisconnectOnSend);
        do {
            responseInfoList = networkClient.sendAndPoll(requestInfoList, 100);
            requestInfoList.clear();
            for (ResponseInfo responseInfo : responseInfoList) {
                NetworkClientErrorCode error = responseInfo.getError();
                ByteBuffer response = responseInfo.getResponse();
                Assert.assertNotNull("Should have encountered an error", error);
                Assert.assertEquals("Should have received a connection unavailable error", NetworkError, error);
                Assert.assertNull("Should not have received a valid response", response);
                responseCount++;
            }
        } while (requestCount > responseCount );
        responseInfoList = networkClient.sendAndPoll(requestInfoList, 100);
        requestInfoList.clear();
        Assert.assertEquals("No responses are expected at this time", 0, responseInfoList.size());
        selector.setState(MockSelectorState.Good);
    }

    /**
     * Test exception on connect
     */
    @Test
    public void testExceptionOnConnect() {
        List<RequestInfo> requestInfoList = new ArrayList<RequestInfo>();
        requestInfoList.add(new RequestInfo(host2, port2, new MockSend(3)));
        requestInfoList.add(new RequestInfo(host2, port2, new MockSend(4)));
        selector.setState(MockSelectorState.ThrowExceptionOnConnect);
        try {
            networkClient.sendAndPoll(requestInfoList, 100);
        } catch (Exception e) {
            Assert.fail("If selector throws on connect, sendAndPoll() should not throw");
        }
    }

    /**
     * Test to ensure two things:
     * 1. If a request comes in and there are no available connections to the destination,
     * only one connection is initiated, even if that connection is found to be pending when the
     * same request is looked at during a subsequent sendAndPoll.
     * 2. For the above situation, if the subsequent pending connection fails, then the request is
     * immediately failed.
     */
    @Test
    public void testConnectionInitializationFailures() throws Exception {
        List<RequestInfo> requestInfoList = new ArrayList<>();
        requestInfoList.add(new RequestInfo(host2, port2, new MockSend(0)));
        selector.setState(MockSelectorState.IdlePoll);
        Assert.assertEquals(0, selector.connectCallCount());
        // this sendAndPoll() should initiate a connect().
        List<ResponseInfo> responseInfoList = networkClient.sendAndPoll(requestInfoList, 100);
        // At this time a single connection would have been initiated for the above request.
        Assert.assertEquals(1, selector.connectCallCount());
        Assert.assertEquals(0, responseInfoList.size());
        requestInfoList.clear();
        // Subsequent calls to sendAndPoll() should not initiate any connections.
        responseInfoList = networkClient.sendAndPoll(requestInfoList, 100);
        Assert.assertEquals(1, selector.connectCallCount());
        Assert.assertEquals(0, responseInfoList.size());
        // Another connection should get initialized if a new request comes in for the same destination.
        requestInfoList.add(new RequestInfo(host2, port2, new MockSend(1)));
        responseInfoList = networkClient.sendAndPoll(requestInfoList, 100);
        Assert.assertEquals(2, selector.connectCallCount());
        Assert.assertEquals(0, responseInfoList.size());
        requestInfoList.clear();
        // Subsequent calls to sendAndPoll() should not initiate any more connections.
        responseInfoList = networkClient.sendAndPoll(requestInfoList, 100);
        Assert.assertEquals(2, selector.connectCallCount());
        Assert.assertEquals(0, responseInfoList.size());
        // Once connect failure kicks in, the pending requests should be failed immediately.
        selector.setState(MockSelectorState.FailConnectionInitiationOnPoll);
        responseInfoList = networkClient.sendAndPoll(requestInfoList, 100);
        Assert.assertEquals(2, selector.connectCallCount());
        Assert.assertEquals(2, responseInfoList.size());
        Assert.assertEquals(NetworkError, responseInfoList.get(0).getError());
        Assert.assertEquals(NetworkError, responseInfoList.get(1).getError());
        responseInfoList.clear();
    }

    /**
     * Test the following case:
     * Connection C1 gets initiated in the context of Request R1
     * Connection C2 gets initiated in the context of Request R2
     * Connection C2 gets established first.
     * Request R1 checks out connection C2 because it is earlier in the queue
     * (although C2 was initiated on behalf of R2)
     * Request R1 gets sent on C2
     * Connection C1 gets disconnected, which was initiated in the context of Request R1
     * Request R1 is completed.
     * Request R2 reuses C1 and gets completed.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testOutOfOrderConnectionEstablishment() throws Exception {
        selector.setState(MockSelectorState.DelayFailAlternateConnect);
        List<RequestInfo> requestInfoList = new ArrayList<>();
        requestInfoList.add(new RequestInfo(host2, port2, new MockSend(2)));
        requestInfoList.add(new RequestInfo(host2, port2, new MockSend(3)));
        List<ResponseInfo> responseInfoList = networkClient.sendAndPoll(requestInfoList, 100);
        requestInfoList.clear();
        Assert.assertEquals(2, selector.connectCallCount());
        Assert.assertEquals(0, responseInfoList.size());
        responseInfoList = networkClient.sendAndPoll(requestInfoList, 100);
        Assert.assertEquals(2, selector.connectCallCount());
        Assert.assertEquals(1, responseInfoList.size());
        Assert.assertEquals(null, responseInfoList.get(0).getError());
        Assert.assertEquals(2, ((MockSend) (responseInfoList.get(0).getRequestInfo().getRequest())).getCorrelationId());
        responseInfoList.clear();
        responseInfoList = networkClient.sendAndPoll(requestInfoList, 100);
        Assert.assertEquals(2, selector.connectCallCount());
        Assert.assertEquals(1, responseInfoList.size());
        Assert.assertEquals(null, responseInfoList.get(0).getError());
        Assert.assertEquals(3, ((MockSend) (responseInfoList.get(0).getRequestInfo().getRequest())).getCorrelationId());
        responseInfoList.clear();
        selector.setState(MockSelectorState.Good);
    }

    /**
     * Tests the case where a pending request for which a connection was initiated times out in the same
     * sendAndPoll cycle in which the connection disconnection is received.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testPendingRequestTimeOutWithDisconnection() throws Exception {
        List<RequestInfo> requestInfoList = new ArrayList<>();
        selector.setState(MockSelectorState.IdlePoll);
        requestInfoList.add(new RequestInfo(host2, port2, new MockSend(4)));
        List<ResponseInfo> responseInfoList = networkClient.sendAndPoll(requestInfoList, 100);
        Assert.assertEquals(0, responseInfoList.size());
        requestInfoList.clear();
        // now make the selector return any attempted connections as disconnections.
        selector.setState(MockSelectorState.FailConnectionInitiationOnPoll);
        // increment the time so that the request times out in the next cycle.
        time.sleep(2000);
        responseInfoList = networkClient.sendAndPoll(requestInfoList, 100);
        Assert.assertEquals(1, responseInfoList.size());
        Assert.assertEquals("Error received should be ConnectionUnavailable", ConnectionUnavailable, responseInfoList.get(0).getError());
        responseInfoList.clear();
        selector.setState(MockSelectorState.Good);
    }

    /**
     * Test exception on poll
     */
    @Test
    public void testExceptionOnPoll() {
        List<RequestInfo> requestInfoList = new ArrayList<RequestInfo>();
        requestInfoList.add(new RequestInfo(host2, port2, new MockSend(3)));
        requestInfoList.add(new RequestInfo(host2, port2, new MockSend(4)));
        selector.setState(MockSelectorState.ThrowExceptionOnPoll);
        try {
            networkClient.sendAndPoll(requestInfoList, 100);
        } catch (Exception e) {
            Assert.fail("If selector throws on poll, sendAndPoll() should not throw.");
        }
        selector.setState(MockSelectorState.Good);
    }

    /**
     * Test that the NetworkClient wakeup wakes up the associated Selector.
     */
    @Test
    public void testWakeup() {
        Assert.assertFalse("Selector should not have been woken up at this point", selector.getAndClearWokenUpStatus());
        networkClient.wakeup();
        Assert.assertTrue("Selector should have been woken up at this point", selector.getAndClearWokenUpStatus());
    }

    /**
     * Test to ensure subsequent operations after a close throw an {@link IllegalStateException}.
     */
    @Test
    public void testClose() throws IOException {
        List<RequestInfo> requestInfoList = new ArrayList<RequestInfo>();
        networkClient.close();
        try {
            networkClient.sendAndPoll(requestInfoList, 100);
            Assert.fail("Polling after close should throw");
        } catch (IllegalStateException e) {
        }
    }
}

