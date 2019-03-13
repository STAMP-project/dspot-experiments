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


import PortType.SSL;
import SSLFactory.Mode.CLIENT;
import SSLFactory.Mode.SERVER;
import TestUtils.RANDOM;
import com.codahale.metrics.MetricRegistry;
import com.github.ambry.commons.SSLFactory;
import com.github.ambry.commons.TestSSLUtils;
import com.github.ambry.config.SSLConfig;
import com.github.ambry.utils.SystemTime;
import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import org.junit.Assert;
import org.junit.Test;


public class SSLSelectorTest {
    private static final int DEFAULT_SOCKET_BUF_SIZE = 4 * 1024;

    private final SSLFactory clientSSLFactory;

    private final int applicationBufferSize;

    private final EchoServer server;

    private Selector selector;

    private final File trustStoreFile;

    public SSLSelectorTest() throws Exception {
        trustStoreFile = File.createTempFile("truststore", ".jks");
        SSLConfig sslConfig = new SSLConfig(TestSSLUtils.createSslProps("DC1,DC2,DC3", SERVER, trustStoreFile, "server"));
        SSLConfig clientSSLConfig = new SSLConfig(TestSSLUtils.createSslProps("DC1,DC2,DC3", CLIENT, trustStoreFile, "client"));
        SSLFactory serverSSLFactory = SSLFactory.getNewInstance(sslConfig);
        clientSSLFactory = SSLFactory.getNewInstance(clientSSLConfig);
        server = new EchoServer(serverSSLFactory, 18383);
        server.start();
        applicationBufferSize = clientSSLFactory.createSSLEngine("localhost", server.port, CLIENT).getSession().getApplicationBufferSize();
        selector = new Selector(new NetworkMetrics(new MetricRegistry()), SystemTime.getInstance(), clientSSLFactory);
    }

    /**
     * Validate that when the server disconnects, a client send ends up with that node in the disconnected list.
     */
    @Test
    public void testServerDisconnect() throws Exception {
        // connect and do a simple request
        String connectionId = blockingSSLConnect(SSLSelectorTest.DEFAULT_SOCKET_BUF_SIZE);
        Assert.assertEquals("hello", blockingRequest(connectionId, "hello"));
        // disconnect
        server.closeConnections();
        while (!(selector.disconnected().contains(connectionId))) {
            selector.poll(1000L);
        } 
        // reconnect and do another request
        connectionId = blockingSSLConnect(SSLSelectorTest.DEFAULT_SOCKET_BUF_SIZE);
        Assert.assertEquals("hello", blockingRequest(connectionId, "hello"));
    }

    /**
     * Validate that the client can intentionally disconnect and reconnect
     */
    @Test
    public void testClientDisconnect() throws Exception {
        String connectionId = blockingSSLConnect(SSLSelectorTest.DEFAULT_SOCKET_BUF_SIZE);
        selector.disconnect(connectionId);
        selector.poll(10, Arrays.asList(SelectorTest.createSend(connectionId, "hello1")));
        Assert.assertEquals("Request should not have succeeded", 0, selector.completedSends().size());
        Assert.assertEquals("There should be a disconnect", 1, selector.disconnected().size());
        Assert.assertTrue("The disconnect should be from our node", selector.disconnected().contains(connectionId));
        connectionId = blockingSSLConnect(SSLSelectorTest.DEFAULT_SOCKET_BUF_SIZE);
        Assert.assertEquals("hello2", blockingRequest(connectionId, "hello2"));
    }

    /**
     * Sending a request with one already in flight should result in an exception
     */
    @Test(expected = IllegalStateException.class)
    public void testCantSendWithInProgress() throws Exception {
        String connectionId = blockingSSLConnect(SSLSelectorTest.DEFAULT_SOCKET_BUF_SIZE);
        selector.poll(1000L, Arrays.asList(SelectorTest.createSend(connectionId, "test1"), SelectorTest.createSend(connectionId, "test2")));
    }

    /**
     * Sending a request to a node with a bad hostname should result in an exception during connect
     */
    @Test(expected = IOException.class)
    public void testNoRouteToHost() throws Exception {
        selector.connect(new InetSocketAddress("asdf.asdf.dsc", server.port), SSLSelectorTest.DEFAULT_SOCKET_BUF_SIZE, SSLSelectorTest.DEFAULT_SOCKET_BUF_SIZE, SSL);
    }

    /**
     * Sending a request to a node not listening on that port should result in disconnection
     */
    @Test
    public void testConnectionRefused() throws Exception {
        String connectionId = selector.connect(new InetSocketAddress("localhost", 6668), SSLSelectorTest.DEFAULT_SOCKET_BUF_SIZE, SSLSelectorTest.DEFAULT_SOCKET_BUF_SIZE, SSL);
        while (selector.disconnected().contains(connectionId)) {
            selector.poll(1000L);
        } 
    }

    /**
     * Send multiple requests to several connections in parallel. Validate that responses are received in the order that
     * requests were sent.
     */
    @Test
    public void testNormalOperation() throws Exception {
        int conns = 5;
        // create connections
        ArrayList<String> connectionIds = new ArrayList<String>();
        for (int i = 0; i < conns; i++) {
            connectionIds.add(blockingSSLConnect(SSLSelectorTest.DEFAULT_SOCKET_BUF_SIZE));
        }
        // send echo requests and receive responses
        int responseCount = 0;
        List<NetworkSend> sends = new ArrayList<NetworkSend>();
        for (int i = 0; i < conns; i++) {
            String connectionId = connectionIds.get(i);
            sends.add(SelectorTest.createSend(connectionId, ((connectionId + "&") + 0)));
        }
        // loop until we complete all requests
        while (responseCount < conns) {
            // do the i/o
            selector.poll(0L, sends);
            Assert.assertEquals("No disconnects should have occurred.", 0, selector.disconnected().size());
            // handle any responses we may have gotten
            for (NetworkReceive receive : selector.completedReceives()) {
                String[] pieces = SelectorTest.asString(receive).split("&");
                Assert.assertEquals("Should be in the form 'conn-counter'", 2, pieces.length);
                Assert.assertEquals("Check the source", receive.getConnectionId(), pieces[0]);
                Assert.assertEquals("Check that the receive has kindly been rewound", 0, receive.getReceivedBytes().getPayload().position());
                Assert.assertTrue("Received connectionId is as expected ", connectionIds.contains(receive.getConnectionId()));
                Assert.assertEquals("Check the request counter", 0, Integer.parseInt(pieces[1]));
                responseCount++;
            }
            // prepare new sends for the next round
            sends.clear();
            for (NetworkSend send : selector.completedSends()) {
                String dest = send.getConnectionId();
                sends.add(SelectorTest.createSend(dest, ((dest + "&") + 0)));
            }
        } 
    }

    /**
     * Validate that we can send and receive a message larger than the receive and send buffer size
     */
    @Test
    public void testSendLargeRequest() throws Exception {
        String connectionId = blockingSSLConnect(SSLSelectorTest.DEFAULT_SOCKET_BUF_SIZE);
        String big = SelectorTest.randomString((10 * (SSLSelectorTest.DEFAULT_SOCKET_BUF_SIZE)), new Random());
        Assert.assertEquals(big, blockingRequest(connectionId, big));
    }

    /**
     * Test sending an empty string
     */
    @Test
    public void testEmptyRequest() throws Exception {
        String connectionId = blockingSSLConnect(SSLSelectorTest.DEFAULT_SOCKET_BUF_SIZE);
        Assert.assertEquals("", blockingRequest(connectionId, ""));
    }

    @Test
    public void testSSLConnect() throws IOException {
        String connectionId = selector.connect(new InetSocketAddress("localhost", server.port), SSLSelectorTest.DEFAULT_SOCKET_BUF_SIZE, SSLSelectorTest.DEFAULT_SOCKET_BUF_SIZE, SSL);
        while (!(selector.connected().contains(connectionId))) {
            selector.poll(10000L);
        } 
        Assert.assertTrue("Channel should have been ready by now ", selector.isChannelReady(connectionId));
    }

    @Test
    public void testCloseAfterConnectCall() throws IOException {
        String connectionId = selector.connect(new InetSocketAddress("localhost", server.port), SSLSelectorTest.DEFAULT_SOCKET_BUF_SIZE, SSLSelectorTest.DEFAULT_SOCKET_BUF_SIZE, SSL);
        selector.close(connectionId);
        selector.poll(0);
        Assert.assertTrue("Channel should have been added to disconnected list", selector.disconnected().contains(connectionId));
    }

    /**
     * selector.poll() should be able to fetch more data than netReadBuffer from the socket.
     */
    @Test
    public void testSelectorPollReadSize() throws Exception {
        for (int socketBufSize : new int[]{ applicationBufferSize, (applicationBufferSize) * 3, (applicationBufferSize) - 10 }) {
            String connectionId = blockingSSLConnect(socketBufSize);
            String message = SelectorTest.randomString(((socketBufSize * 2) + (RANDOM.nextInt(socketBufSize))), RANDOM);
            // Did not count the exact number of polls to completion since its hard to make that test deterministic.
            // i.e. EchoServer could respond slower than expected.
            Assert.assertEquals("Wrong echoed response", message, blockingRequest(connectionId, message));
        }
    }

    /**
     * Tests handling of BUFFER_UNDERFLOW during unwrap when network read buffer is smaller than SSL session packet buffer
     * size.
     */
    @Test
    public void testNetReadBufferResize() throws Exception {
        useCustomBufferSizeSelector(10, null, null);
        String connectionId = blockingSSLConnect(SSLSelectorTest.DEFAULT_SOCKET_BUF_SIZE);
        String message = SelectorTest.randomString((5 * (applicationBufferSize)), new Random());
        Assert.assertEquals("Wrong echoed response", message, blockingRequest(connectionId, message));
    }

    /**
     * Tests handling of BUFFER_OVERFLOW during wrap when network write buffer is smaller than SSL session packet buffer
     * size.
     */
    @Test
    public void testNetWriteBufferResize() throws Exception {
        useCustomBufferSizeSelector(null, 10, null);
        String connectionId = blockingSSLConnect(SSLSelectorTest.DEFAULT_SOCKET_BUF_SIZE);
        String message = SelectorTest.randomString(10, new Random());
        Assert.assertEquals("Wrong echoed response", message, blockingRequest(connectionId, message));
    }

    /**
     * Tests handling of BUFFER_OVERFLOW during unwrap when application read buffer is smaller than SSL session
     * application buffer size.
     */
    @Test
    public void testAppReadBufferResize() throws Exception {
        useCustomBufferSizeSelector(null, null, 10);
        String connectionId = blockingSSLConnect(SSLSelectorTest.DEFAULT_SOCKET_BUF_SIZE);
        String message = SelectorTest.randomString((5 * (applicationBufferSize)), new Random());
        Assert.assertEquals("Wrong echoed response", message, blockingRequest(connectionId, message));
    }
}

