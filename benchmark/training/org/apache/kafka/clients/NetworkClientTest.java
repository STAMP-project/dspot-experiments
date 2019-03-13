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
package org.apache.kafka.clients;


import ApiKeys.PRODUCE;
import CommonFields.THROTTLE_TIME_MS;
import MetadataRequest.Builder;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.apache.kafka.common.Node;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.protocol.types.Struct;
import org.apache.kafka.common.record.MemoryRecords;
import org.apache.kafka.common.requests.MetadataRequest;
import org.apache.kafka.common.requests.ProduceRequest;
import org.apache.kafka.common.requests.ResponseHeader;
import org.apache.kafka.common.utils.MockTime;
import org.apache.kafka.test.MockSelector;
import org.apache.kafka.test.TestUtils;
import org.junit.Assert;
import org.junit.Test;


public class NetworkClientTest {
    protected final int defaultRequestTimeoutMs = 1000;

    protected final MockTime time = new MockTime();

    protected final MockSelector selector = new MockSelector(time);

    protected final Node node = TestUtils.singletonCluster().nodes().iterator().next();

    protected final long reconnectBackoffMsTest = 10 * 1000;

    protected final long reconnectBackoffMaxMsTest = 10 * 10000;

    private final NetworkClient client = createNetworkClient(reconnectBackoffMaxMsTest);

    private final NetworkClient clientWithNoExponentialBackoff = createNetworkClient(reconnectBackoffMsTest);

    private final NetworkClient clientWithStaticNodes = createNetworkClientWithStaticNodes();

    private final NetworkClient clientWithNoVersionDiscovery = createNetworkClientWithNoVersionDiscovery();

    @Test(expected = IllegalStateException.class)
    public void testSendToUnreadyNode() {
        MetadataRequest.Builder builder = new MetadataRequest.Builder(Arrays.asList("test"), true);
        long now = time.milliseconds();
        ClientRequest request = client.newClientRequest("5", builder, now, false);
        client.send(request, now);
        client.poll(1, time.milliseconds());
    }

    @Test
    public void testSimpleRequestResponse() {
        checkSimpleRequestResponse(client);
    }

    @Test
    public void testSimpleRequestResponseWithStaticNodes() {
        checkSimpleRequestResponse(clientWithStaticNodes);
    }

    @Test
    public void testSimpleRequestResponseWithNoBrokerDiscovery() {
        checkSimpleRequestResponse(clientWithNoVersionDiscovery);
    }

    @Test
    public void testDnsLookupFailure() {
        /* Fail cleanly when the node has a bad hostname */
        Assert.assertFalse(client.ready(new Node(1234, "badhost", 1234), time.milliseconds()));
    }

    @Test
    public void testClose() {
        client.ready(node, time.milliseconds());
        awaitReady(client, node);
        client.poll(1, time.milliseconds());
        Assert.assertTrue("The client should be ready", client.isReady(node, time.milliseconds()));
        ProduceRequest.Builder builder = ProduceRequest.Builder.forCurrentMagic(((short) (1)), 1000, Collections.<TopicPartition, MemoryRecords>emptyMap());
        ClientRequest request = client.newClientRequest(node.idString(), builder, time.milliseconds(), true);
        client.send(request, time.milliseconds());
        Assert.assertEquals("There should be 1 in-flight request after send", 1, client.inFlightRequestCount(node.idString()));
        Assert.assertTrue(client.hasInFlightRequests(node.idString()));
        Assert.assertTrue(client.hasInFlightRequests());
        client.close(node.idString());
        Assert.assertEquals("There should be no in-flight request after close", 0, client.inFlightRequestCount(node.idString()));
        Assert.assertFalse(client.hasInFlightRequests(node.idString()));
        Assert.assertFalse(client.hasInFlightRequests());
        Assert.assertFalse("Connection should not be ready after close", client.isReady(node, 0));
    }

    @Test
    public void testRequestTimeout() {
        awaitReady(client, node);// has to be before creating any request, as it may send ApiVersionsRequest and its response is mocked with correlation id 0

        ProduceRequest.Builder builder = ProduceRequest.Builder.forCurrentMagic(((short) (1)), 1000, Collections.emptyMap());
        NetworkClientTest.TestCallbackHandler handler = new NetworkClientTest.TestCallbackHandler();
        int requestTimeoutMs = (defaultRequestTimeoutMs) + 5000;
        ClientRequest request = client.newClientRequest(node.idString(), builder, time.milliseconds(), true, requestTimeoutMs, handler);
        Assert.assertEquals(requestTimeoutMs, request.requestTimeoutMs());
        testRequestTimeout(request);
    }

    @Test
    public void testDefaultRequestTimeout() {
        awaitReady(client, node);// has to be before creating any request, as it may send ApiVersionsRequest and its response is mocked with correlation id 0

        ProduceRequest.Builder builder = ProduceRequest.Builder.forCurrentMagic(((short) (1)), 1000, Collections.emptyMap());
        ClientRequest request = client.newClientRequest(node.idString(), builder, time.milliseconds(), true);
        Assert.assertEquals(defaultRequestTimeoutMs, request.requestTimeoutMs());
        testRequestTimeout(request);
    }

    @Test
    public void testConnectionThrottling() {
        // Instrument the test to return a response with a 100ms throttle delay.
        awaitReady(client, node);
        ProduceRequest.Builder builder = ProduceRequest.Builder.forCurrentMagic(((short) (1)), 1000, Collections.emptyMap());
        NetworkClientTest.TestCallbackHandler handler = new NetworkClientTest.TestCallbackHandler();
        ClientRequest request = client.newClientRequest(node.idString(), builder, time.milliseconds(), true, defaultRequestTimeoutMs, handler);
        client.send(request, time.milliseconds());
        client.poll(1, time.milliseconds());
        ResponseHeader respHeader = new ResponseHeader(request.correlationId());
        Struct resp = new Struct(PRODUCE.responseSchema(PRODUCE.latestVersion()));
        resp.set("responses", new Object[0]);
        resp.set(THROTTLE_TIME_MS, 100);
        Struct responseHeaderStruct = respHeader.toStruct();
        int size = (responseHeaderStruct.sizeOf()) + (resp.sizeOf());
        ByteBuffer buffer = ByteBuffer.allocate(size);
        responseHeaderStruct.writeTo(buffer);
        resp.writeTo(buffer);
        buffer.flip();
        selector.completeReceive(new org.apache.kafka.common.network.NetworkReceive(node.idString(), buffer));
        client.poll(1, time.milliseconds());
        // The connection is not ready due to throttling.
        Assert.assertFalse(client.ready(node, time.milliseconds()));
        Assert.assertEquals(100, client.throttleDelayMs(node, time.milliseconds()));
        // After 50ms, the connection is not ready yet.
        time.sleep(50);
        Assert.assertFalse(client.ready(node, time.milliseconds()));
        Assert.assertEquals(50, client.throttleDelayMs(node, time.milliseconds()));
        // After another 50ms, the throttling is done and the connection becomes ready again.
        time.sleep(50);
        Assert.assertTrue(client.ready(node, time.milliseconds()));
        Assert.assertEquals(0, client.throttleDelayMs(node, time.milliseconds()));
    }

    @Test
    public void testThrottlingNotEnabledForConnectionToOlderBroker() {
        // Instrument the test so that the max protocol version for PRODUCE returned from the node is 5 and thus
        // client-side throttling is not enabled. Also, return a response with a 100ms throttle delay.
        setExpectedApiVersionsResponse(createExpectedApiVersionsResponse(node, PRODUCE, ((short) (5))));
        while (!(client.ready(node, time.milliseconds())))
            client.poll(1, time.milliseconds());

        selector.clear();
        ProduceRequest.Builder builder = ProduceRequest.Builder.forCurrentMagic(((short) (1)), 1000, Collections.emptyMap());
        NetworkClientTest.TestCallbackHandler handler = new NetworkClientTest.TestCallbackHandler();
        ClientRequest request = client.newClientRequest(node.idString(), builder, time.milliseconds(), true, defaultRequestTimeoutMs, handler);
        client.send(request, time.milliseconds());
        client.poll(1, time.milliseconds());
        ResponseHeader respHeader = new ResponseHeader(request.correlationId());
        Struct resp = new Struct(PRODUCE.responseSchema(PRODUCE.latestVersion()));
        resp.set("responses", new Object[0]);
        resp.set(THROTTLE_TIME_MS, 100);
        Struct responseHeaderStruct = respHeader.toStruct();
        int size = (responseHeaderStruct.sizeOf()) + (resp.sizeOf());
        ByteBuffer buffer = ByteBuffer.allocate(size);
        responseHeaderStruct.writeTo(buffer);
        resp.writeTo(buffer);
        buffer.flip();
        selector.completeReceive(new org.apache.kafka.common.network.NetworkReceive(node.idString(), buffer));
        client.poll(1, time.milliseconds());
        // Since client-side throttling is disabled, the connection is ready even though the response indicated a
        // throttle delay.
        Assert.assertTrue(client.ready(node, time.milliseconds()));
        Assert.assertEquals(0, client.throttleDelayMs(node, time.milliseconds()));
    }

    @Test
    public void testLeastLoadedNode() {
        client.ready(node, time.milliseconds());
        awaitReady(client, node);
        client.poll(1, time.milliseconds());
        Assert.assertTrue("The client should be ready", client.isReady(node, time.milliseconds()));
        // leastloadednode should be our single node
        Node leastNode = client.leastLoadedNode(time.milliseconds());
        Assert.assertEquals("There should be one leastloadednode", leastNode.id(), node.id());
        // sleep for longer than reconnect backoff
        time.sleep(reconnectBackoffMsTest);
        // CLOSE node
        selector.serverDisconnect(node.idString());
        client.poll(1, time.milliseconds());
        Assert.assertFalse("After we forced the disconnection the client is no longer ready.", client.ready(node, time.milliseconds()));
        leastNode = client.leastLoadedNode(time.milliseconds());
        Assert.assertNull("There should be NO leastloadednode", leastNode);
    }

    @Test
    public void testConnectionDelayWithNoExponentialBackoff() {
        long now = time.milliseconds();
        long delay = clientWithNoExponentialBackoff.connectionDelay(node, now);
        Assert.assertEquals(0, delay);
    }

    @Test
    public void testConnectionDelayConnectedWithNoExponentialBackoff() {
        awaitReady(clientWithNoExponentialBackoff, node);
        long now = time.milliseconds();
        long delay = clientWithNoExponentialBackoff.connectionDelay(node, now);
        Assert.assertEquals(Long.MAX_VALUE, delay);
    }

    @Test
    public void testConnectionDelayDisconnectedWithNoExponentialBackoff() {
        awaitReady(clientWithNoExponentialBackoff, node);
        selector.serverDisconnect(node.idString());
        clientWithNoExponentialBackoff.poll(defaultRequestTimeoutMs, time.milliseconds());
        long delay = clientWithNoExponentialBackoff.connectionDelay(node, time.milliseconds());
        Assert.assertEquals(reconnectBackoffMsTest, delay);
        // Sleep until there is no connection delay
        time.sleep(delay);
        Assert.assertEquals(0, clientWithNoExponentialBackoff.connectionDelay(node, time.milliseconds()));
        // Start connecting and disconnect before the connection is established
        client.ready(node, time.milliseconds());
        selector.serverDisconnect(node.idString());
        client.poll(defaultRequestTimeoutMs, time.milliseconds());
        // Second attempt should have the same behaviour as exponential backoff is disabled
        Assert.assertEquals(reconnectBackoffMsTest, delay);
    }

    @Test
    public void testConnectionDelay() {
        long now = time.milliseconds();
        long delay = client.connectionDelay(node, now);
        Assert.assertEquals(0, delay);
    }

    @Test
    public void testConnectionDelayConnected() {
        awaitReady(client, node);
        long now = time.milliseconds();
        long delay = client.connectionDelay(node, now);
        Assert.assertEquals(Long.MAX_VALUE, delay);
    }

    @Test
    public void testConnectionDelayDisconnected() {
        awaitReady(client, node);
        // First disconnection
        selector.serverDisconnect(node.idString());
        client.poll(defaultRequestTimeoutMs, time.milliseconds());
        long delay = client.connectionDelay(node, time.milliseconds());
        long expectedDelay = reconnectBackoffMsTest;
        double jitter = 0.3;
        Assert.assertEquals(expectedDelay, delay, (expectedDelay * jitter));
        // Sleep until there is no connection delay
        time.sleep(delay);
        Assert.assertEquals(0, client.connectionDelay(node, time.milliseconds()));
        // Start connecting and disconnect before the connection is established
        client.ready(node, time.milliseconds());
        selector.serverDisconnect(node.idString());
        client.poll(defaultRequestTimeoutMs, time.milliseconds());
        // Second attempt should take twice as long with twice the jitter
        expectedDelay = Math.round((delay * 2));
        delay = client.connectionDelay(node, time.milliseconds());
        jitter = 0.6;
        Assert.assertEquals(expectedDelay, delay, (expectedDelay * jitter));
    }

    @Test
    public void testDisconnectDuringUserMetadataRequest() {
        // this test ensures that the default metadata updater does not intercept a user-initiated
        // metadata request when the remote node disconnects with the request in-flight.
        awaitReady(client, node);
        MetadataRequest.Builder builder = new MetadataRequest.Builder(Collections.<String>emptyList(), true);
        long now = time.milliseconds();
        ClientRequest request = client.newClientRequest(node.idString(), builder, now, true);
        client.send(request, now);
        client.poll(defaultRequestTimeoutMs, now);
        Assert.assertEquals(1, client.inFlightRequestCount(node.idString()));
        Assert.assertTrue(client.hasInFlightRequests(node.idString()));
        Assert.assertTrue(client.hasInFlightRequests());
        selector.close(node.idString());
        List<ClientResponse> responses = client.poll(defaultRequestTimeoutMs, time.milliseconds());
        Assert.assertEquals(1, responses.size());
        Assert.assertTrue(responses.iterator().next().wasDisconnected());
    }

    @Test
    public void testServerDisconnectAfterInternalApiVersionRequest() throws Exception {
        awaitInFlightApiVersionRequest();
        selector.serverDisconnect(node.idString());
        // The failed ApiVersion request should not be forwarded to upper layers
        List<ClientResponse> responses = client.poll(0, time.milliseconds());
        Assert.assertFalse(client.hasInFlightRequests(node.idString()));
        Assert.assertTrue(responses.isEmpty());
    }

    @Test
    public void testClientDisconnectAfterInternalApiVersionRequest() throws Exception {
        awaitInFlightApiVersionRequest();
        client.disconnect(node.idString());
        Assert.assertFalse(client.hasInFlightRequests(node.idString()));
        // The failed ApiVersion request should not be forwarded to upper layers
        List<ClientResponse> responses = client.poll(0, time.milliseconds());
        Assert.assertTrue(responses.isEmpty());
    }

    @Test
    public void testDisconnectWithMultipleInFlights() {
        NetworkClient client = this.clientWithNoVersionDiscovery;
        awaitReady(client, node);
        Assert.assertTrue(("Expected NetworkClient to be ready to send to node " + (node.idString())), client.isReady(node, time.milliseconds()));
        MetadataRequest.Builder builder = new MetadataRequest.Builder(Collections.<String>emptyList(), true);
        long now = time.milliseconds();
        final List<ClientResponse> callbackResponses = new ArrayList<>();
        RequestCompletionHandler callback = new RequestCompletionHandler() {
            @Override
            public void onComplete(ClientResponse response) {
                callbackResponses.add(response);
            }
        };
        ClientRequest request1 = client.newClientRequest(node.idString(), builder, now, true, defaultRequestTimeoutMs, callback);
        client.send(request1, now);
        client.poll(0, now);
        ClientRequest request2 = client.newClientRequest(node.idString(), builder, now, true, defaultRequestTimeoutMs, callback);
        client.send(request2, now);
        client.poll(0, now);
        Assert.assertNotEquals(request1.correlationId(), request2.correlationId());
        Assert.assertEquals(2, client.inFlightRequestCount());
        Assert.assertEquals(2, client.inFlightRequestCount(node.idString()));
        client.disconnect(node.idString());
        List<ClientResponse> responses = client.poll(0, time.milliseconds());
        Assert.assertEquals(2, responses.size());
        Assert.assertEquals(responses, callbackResponses);
        Assert.assertEquals(0, client.inFlightRequestCount());
        Assert.assertEquals(0, client.inFlightRequestCount(node.idString()));
        // Ensure that the responses are returned in the order they were sent
        ClientResponse response1 = responses.get(0);
        Assert.assertTrue(response1.wasDisconnected());
        Assert.assertEquals(request1.correlationId(), response1.requestHeader().correlationId());
        ClientResponse response2 = responses.get(1);
        Assert.assertTrue(response2.wasDisconnected());
        Assert.assertEquals(request2.correlationId(), response2.requestHeader().correlationId());
    }

    @Test
    public void testCallDisconnect() throws Exception {
        awaitReady(client, node);
        Assert.assertTrue(("Expected NetworkClient to be ready to send to node " + (node.idString())), client.isReady(node, time.milliseconds()));
        Assert.assertFalse((("Did not expect connection to node " + (node.idString())) + " to be failed"), client.connectionFailed(node));
        client.disconnect(node.idString());
        Assert.assertFalse((("Expected node " + (node.idString())) + " to be disconnected."), client.isReady(node, time.milliseconds()));
        Assert.assertTrue((("Expected connection to node " + (node.idString())) + " to be failed after disconnect"), client.connectionFailed(node));
        Assert.assertFalse(client.canConnect(node, time.milliseconds()));
        // ensure disconnect does not reset blackout period if already disconnected
        time.sleep(reconnectBackoffMaxMsTest);
        Assert.assertTrue(client.canConnect(node, time.milliseconds()));
        client.disconnect(node.idString());
        Assert.assertTrue(client.canConnect(node, time.milliseconds()));
    }

    private static class TestCallbackHandler implements RequestCompletionHandler {
        public boolean executed = false;

        public ClientResponse response;

        public void onComplete(ClientResponse response) {
            this.executed = true;
            this.response = response;
        }
    }
}

