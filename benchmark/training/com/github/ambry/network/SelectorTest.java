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


import PortType.PLAINTEXT;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import org.junit.Assert;
import org.junit.Test;


/**
 * A set of tests for the selector. These use a test harness that runs a simple socket server that echos back responses.
 */
public class SelectorTest {
    private static final int BUFFER_SIZE = 4 * 1024;

    private EchoServer server;

    private Selector selector;

    /**
     * Validate that when the server disconnects, a client send ends up with that node in the disconnected list.
     */
    @Test
    public void testServerDisconnect() throws Exception {
        // connect and do a simple request
        String connectionId = blockingConnect();
        Assert.assertEquals("hello", blockingRequest(connectionId, "hello"));
        // disconnect
        this.server.closeConnections();
        while (!(selector.disconnected().contains(connectionId))) {
            selector.poll(1000L);
        } 
        // reconnect and do another request
        connectionId = blockingConnect();
        Assert.assertEquals("hello", blockingRequest(connectionId, "hello"));
    }

    /**
     * Validate that the client can intentionally disconnect and reconnect
     */
    @Test
    public void testClientDisconnect() throws Exception {
        String connectionId = blockingConnect();
        selector.disconnect(connectionId);
        selector.poll(10, Arrays.asList(SelectorTest.createSend(connectionId, "hello1")));
        Assert.assertEquals("Request should not have succeeded", 0, selector.completedSends().size());
        Assert.assertEquals("There should be a disconnect", 1, selector.disconnected().size());
        Assert.assertTrue("The disconnect should be from our node", selector.disconnected().contains(connectionId));
        connectionId = blockingConnect();
        Assert.assertEquals("hello2", blockingRequest(connectionId, "hello2"));
    }

    /**
     * Validate that a closed connectionId is returned via disconnected list after close
     */
    @Test
    public void testDisconnectedListOnClose() throws Exception {
        String connectionId = blockingConnect();
        Assert.assertEquals("Disconnect list should be empty", 0, selector.disconnected().size());
        selector.close(connectionId);
        selector.poll(0);
        Assert.assertEquals("There should be a disconnect", 1, selector.disconnected().size());
        Assert.assertTrue((("Expected connectionId " + connectionId) + " missing from selector's disconnected list "), selector.disconnected().contains(connectionId));
        // make sure that the connection id is not returned via disconnected list after another poll()
        selector.poll(0);
        Assert.assertEquals("Disconnect list should be empty", 0, selector.disconnected().size());
    }

    /**
     * Sending a request with one already in flight should result in an exception
     */
    @Test(expected = IllegalStateException.class)
    public void testCantSendWithInProgress() throws Exception {
        String connectionId = blockingConnect();
        selector.poll(1000L, Arrays.asList(SelectorTest.createSend(connectionId, "test1"), SelectorTest.createSend(connectionId, "test2")));
    }

    /**
     * Sending a request to a node without an existing connection should result in an exception
     */
    @Test(expected = IllegalStateException.class)
    public void testCantSendWithoutConnecting() throws Exception {
        selector.poll(1000L, Arrays.asList(SelectorTest.createSend("testCantSendWithoutConnecting_test", "test")));
    }

    /**
     * Sending a request to a node with a bad hostname should result in an exception during connect
     */
    @Test(expected = IOException.class)
    public void testNoRouteToHost() throws Exception {
        selector.connect(new InetSocketAddress("asdf.asdf.dsc", server.port), SelectorTest.BUFFER_SIZE, SelectorTest.BUFFER_SIZE, PLAINTEXT);
    }

    /**
     * Sending a request to a node not listening on that port should result in disconnection
     */
    @Test
    public void testConnectionRefused() throws Exception {
        String connectionId = selector.connect(new InetSocketAddress("localhost", 6668), SelectorTest.BUFFER_SIZE, SelectorTest.BUFFER_SIZE, PLAINTEXT);
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
        int reqs = 500;
        // create connections
        InetSocketAddress addr = new InetSocketAddress("localhost", server.port);
        ArrayList<String> connectionIds = new ArrayList<String>();
        for (int i = 0; i < conns; i++) {
            String connectionId = selector.connect(addr, SelectorTest.BUFFER_SIZE, SelectorTest.BUFFER_SIZE, PLAINTEXT);
            connectionIds.add(connectionId);
        }
        // send echo requests and receive responses
        int[] requests = new int[conns];
        int[] responses = new int[conns];
        int responseCount = 0;
        List<NetworkSend> sends = new ArrayList<NetworkSend>();
        for (int i = 0; i < conns; i++) {
            String connectionId = connectionIds.get(i);
            sends.add(SelectorTest.createSend(connectionId, ((connectionId + "&") + 0)));
        }
        // loop until we complete all requests
        while (responseCount < (conns * reqs)) {
            // do the i/o
            selector.poll(0L, sends);
            Assert.assertEquals("No disconnects should have occurred.", 0, selector.disconnected().size());
            // handle any responses we may have gotten
            for (NetworkReceive receive : selector.completedReceives()) {
                String[] pieces = SelectorTest.asString(receive).split("&");
                Assert.assertEquals("Should be in the form 'conn-counter'", 2, pieces.length);
                Assert.assertEquals("Check the source", receive.getConnectionId(), pieces[0]);
                Assert.assertEquals("Check that the receive has kindly been rewound", 0, receive.getReceivedBytes().getPayload().position());
                int index = Integer.parseInt(receive.getConnectionId().split("_")[1]);
                Assert.assertEquals("Check the request counter", responses[index], Integer.parseInt(pieces[1]));
                (responses[index])++;// increment the expected counter

                responseCount++;
            }
            // prepare new sends for the next round
            sends.clear();
            for (NetworkSend send : selector.completedSends()) {
                String dest = send.getConnectionId();
                String[] pieces = dest.split("_");
                int index = Integer.parseInt(pieces[1]);
                (requests[index])++;
                if ((requests[index]) < reqs) {
                    sends.add(SelectorTest.createSend(dest, ((dest + "&") + (requests[index]))));
                }
            }
        } 
    }

    /**
     * Validate that we can send and receive a message larger than the receive and send buffer size
     */
    @Test
    public void testSendLargeRequest() throws Exception {
        String connectionId = blockingConnect();
        String big = SelectorTest.randomString((10 * (SelectorTest.BUFFER_SIZE)), new Random());
        Assert.assertEquals(big, blockingRequest(connectionId, big));
    }

    /**
     * Test sending an empty string
     */
    @Test
    public void testEmptyRequest() throws Exception {
        String connectionId = blockingConnect();
        Assert.assertEquals("", blockingRequest(connectionId, ""));
    }
}

