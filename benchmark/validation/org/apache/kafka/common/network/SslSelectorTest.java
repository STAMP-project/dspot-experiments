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
package org.apache.kafka.common.network;


import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import javax.net.ssl.SSLEngine;
import org.apache.kafka.common.security.ssl.SslFactory;
import org.apache.kafka.common.utils.LogContext;
import org.apache.kafka.test.TestUtils;
import org.junit.Assert;
import org.junit.Test;

import static Mode.CLIENT;


/**
 * A set of tests for the selector. These use a test harness that runs a simple socket server that echos back responses.
 */
public class SslSelectorTest extends SelectorTest {
    private Map<String, Object> sslClientConfigs;

    @Test
    public void testDisconnectWithIntermediateBufferedBytes() throws Exception {
        int requestSize = 100 * 1024;
        final String node = "0";
        String request = TestUtils.randomString(requestSize);
        this.selector.close();
        this.channelBuilder = new SslSelectorTest.TestSslChannelBuilder(CLIENT);
        this.channelBuilder.configure(sslClientConfigs);
        this.selector = new Selector(5000, metrics, time, "MetricGroup", channelBuilder, new LogContext());
        connect(node, new InetSocketAddress("localhost", server.port));
        selector.send(createSend(node, request));
        waitForBytesBuffered(selector, node);
        selector.close(node);
        verifySelectorEmpty();
    }

    @Test
    public void testBytesBufferedChannelWithNoIncomingBytes() throws Exception {
        verifyNoUnnecessaryPollWithBytesBuffered(( key) -> key.interestOps(((key.interestOps()) & (~(SelectionKey.OP_READ)))));
    }

    @Test
    public void testBytesBufferedChannelAfterMute() throws Exception {
        verifyNoUnnecessaryPollWithBytesBuffered(( key) -> mute());
    }

    /**
     * Renegotiation is not supported since it is potentially unsafe and it has been removed in TLS 1.3
     */
    @Test
    public void testRenegotiationFails() throws Exception {
        String node = "0";
        // create connections
        InetSocketAddress addr = new InetSocketAddress("localhost", server.port);
        selector.connect(node, addr, SelectorTest.BUFFER_SIZE, SelectorTest.BUFFER_SIZE);
        // send echo requests and receive responses
        while (!(selector.isChannelReady(node))) {
            selector.poll(1000L);
        } 
        selector.send(createSend(node, ((node + "-") + 0)));
        selector.poll(0L);
        server.renegotiate();
        selector.send(createSend(node, ((node + "-") + 1)));
        long expiryTime = (System.currentTimeMillis()) + 2000;
        List<String> disconnected = new ArrayList<>();
        while ((!(disconnected.contains(node))) && ((System.currentTimeMillis()) < expiryTime)) {
            selector.poll(10);
            disconnected.addAll(selector.disconnected().keySet());
        } 
        Assert.assertTrue("Renegotiation should cause disconnection", disconnected.contains(node));
    }

    private static class TestSslChannelBuilder extends SslChannelBuilder {
        public TestSslChannelBuilder(Mode mode) {
            super(mode, null, false);
        }

        @Override
        protected SslTransportLayer buildTransportLayer(SslFactory sslFactory, String id, SelectionKey key, String host) throws IOException {
            SocketChannel socketChannel = ((SocketChannel) (key.channel()));
            SSLEngine sslEngine = sslFactory.createSslEngine(host, socketChannel.socket().getPort());
            SslSelectorTest.TestSslChannelBuilder.TestSslTransportLayer transportLayer = new SslSelectorTest.TestSslChannelBuilder.TestSslTransportLayer(id, key, sslEngine);
            return transportLayer;
        }

        /* TestSslTransportLayer will read from socket once every two tries. This increases
        the chance that there will be bytes buffered in the transport layer after read().
         */
        static class TestSslTransportLayer extends SslTransportLayer {
            static Map<String, SslSelectorTest.TestSslChannelBuilder.TestSslTransportLayer> transportLayers = new HashMap<>();

            boolean muteSocket = false;

            public TestSslTransportLayer(String channelId, SelectionKey key, SSLEngine sslEngine) throws IOException {
                super(channelId, key, sslEngine);
                SslSelectorTest.TestSslChannelBuilder.TestSslTransportLayer.transportLayers.put(channelId, this);
            }

            @Override
            protected int readFromSocketChannel() throws IOException {
                if (muteSocket) {
                    if (((selectionKey().interestOps()) & (SelectionKey.OP_READ)) != 0)
                        muteSocket = false;

                    return 0;
                }
                muteSocket = true;
                return super.readFromSocketChannel();
            }

            // Leave one byte in network read buffer so that some buffered bytes are present,
            // but not enough to make progress on a read.
            void truncateReadBuffer() throws Exception {
                netReadBuffer().position(1);
                appReadBuffer().position(0);
                muteSocket = true;
            }
        }
    }
}

