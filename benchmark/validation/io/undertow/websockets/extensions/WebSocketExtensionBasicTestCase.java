/**
 * JBoss, Home of Professional Open Source.
 * Copyright 2014 Red Hat, Inc., and individual contributors
 * as indicated by the @author tags.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package io.undertow.websockets.extensions;


import OptionMap.EMPTY;
import Options.CONNECTION_HIGH_WATER;
import Options.CONNECTION_LOW_WATER;
import Options.CORK;
import Options.TCP_NODELAY;
import Options.WORKER_IO_THREADS;
import Options.WORKER_TASK_CORE_THREADS;
import Options.WORKER_TASK_MAX_THREADS;
import WebSocketFrameType.TEXT;
import WebSocketLogger.ROOT_LOGGER;
import WebSocketVersion.V13;
import io.undertow.Handlers;
import io.undertow.testutils.DefaultServer;
import io.undertow.testutils.HttpOneOnly;
import io.undertow.util.StringWriteChannelListener;
import io.undertow.websockets.WebSocketExtension;
import io.undertow.websockets.WebSocketProtocolHandshakeHandler;
import io.undertow.websockets.client.WebSocketClient;
import io.undertow.websockets.client.WebSocketClientNegotiation;
import io.undertow.websockets.core.AbstractReceiveListener;
import io.undertow.websockets.core.BufferedBinaryMessage;
import io.undertow.websockets.core.BufferedTextMessage;
import io.undertow.websockets.core.StreamSinkFrameChannel;
import io.undertow.websockets.core.WebSocketChannel;
import io.undertow.websockets.core.WebSockets;
import java.io.IOException;
import java.net.URI;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.xnio.OptionMap;
import org.xnio.Xnio;
import org.xnio.XnioWorker;


/**
 * A test class for WebSocket client scenarios with extensions.
 *
 * @author Lucas Ponce
 */
@HttpOneOnly
@RunWith(DefaultServer.class)
public class WebSocketExtensionBasicTestCase {
    @Test
    public void testLongTextMessage() throws Exception {
        XnioWorker client;
        Xnio xnio = Xnio.getInstance(WebSocketExtensionBasicTestCase.class.getClassLoader());
        client = xnio.createWorker(OptionMap.builder().set(WORKER_IO_THREADS, 2).set(CONNECTION_HIGH_WATER, 1000000).set(CONNECTION_LOW_WATER, 1000000).set(WORKER_TASK_CORE_THREADS, 30).set(WORKER_TASK_MAX_THREADS, 30).set(TCP_NODELAY, true).set(CORK, true).getMap());
        WebSocketProtocolHandshakeHandler handler = WebSocketExtensionBasicTestCase.webSocketDebugHandler().addExtension(new PerMessageDeflateHandshake());
        DebugExtensionsHeaderHandler debug = new DebugExtensionsHeaderHandler(handler);
        DefaultServer.setRootHandler(Handlers.path().addPrefixPath("/", debug));
        final String SEC_WEBSOCKET_EXTENSIONS = "permessage-deflate; client_no_context_takeover; client_max_window_bits";
        List<WebSocketExtension> extensionsList = WebSocketExtension.parse(SEC_WEBSOCKET_EXTENSIONS);
        final WebSocketClientNegotiation negotiation = new WebSocketClientNegotiation(null, extensionsList);
        Set<ExtensionHandshake> extensionHandshakes = new HashSet<>();
        extensionHandshakes.add(new PerMessageDeflateHandshake(true));
        final WebSocketChannel clientChannel = WebSocketClient.connect(client, null, DefaultServer.getBufferPool(), EMPTY, new URI(DefaultServer.getDefaultServerURL()), V13, negotiation, extensionHandshakes).get();
        final CountDownLatch latch = new CountDownLatch(1);
        final AtomicReference<String> result = new AtomicReference<>();
        clientChannel.getReceiveSetter().set(new AbstractReceiveListener() {
            @Override
            protected void onFullTextMessage(WebSocketChannel channel, BufferedTextMessage message) throws IOException {
                String data = message.getData();
                // WebSocketLogger.ROOT_LOGGER.info("onFullTextMessage() - Client - Received: " + data.getBytes().length + " bytes.");
                result.set(data);
                latch.countDown();
            }

            @Override
            protected void onFullCloseMessage(WebSocketChannel channel, BufferedBinaryMessage message) throws IOException {
                message.getData().close();
                ROOT_LOGGER.info("onFullCloseMessage");
            }

            @Override
            protected void onError(WebSocketChannel channel, Throwable error) {
                ROOT_LOGGER.info("onError");
                super.onError(channel, error);
                error.printStackTrace();
                latch.countDown();
            }
        });
        clientChannel.resumeReceives();
        int LONG_MSG = 125 * 1024;
        StringBuilder longMsg = new StringBuilder(LONG_MSG);
        for (int i = 0; i < LONG_MSG; i++) {
            longMsg.append(Integer.toString(i).charAt(0));
        }
        WebSockets.sendTextBlocking(longMsg.toString(), clientChannel);
        latch.await(300, TimeUnit.SECONDS);
        Assert.assertEquals(longMsg.toString(), result.get());
        clientChannel.sendClose();
        client.shutdown();
    }

    /**
     * Simulate an extensions request.
     *
     * <pre>{@code GET / HTTP/1.1
     * User-Agent: AutobahnTestSuite/0.7.0-0.9.0
     * Host: localhost:7777
     * Upgrade: WebSocket
     * Connection: Upgrade
     * Pragma: no-cache
     * Cache-Control: no-cache
     * Sec-WebSocket-Key: pRAuwtkO0SUKzufqA2g+ig==
     * Sec-WebSocket-Extensions: permessage-deflate; client_no_context_takeover; client_max_window_bits
     * Sec-WebSocket-Version: 13}
     * </pre>
     */
    @Test
    public void testExtensionsHeaders() throws Exception {
        XnioWorker client;
        Xnio xnio = Xnio.getInstance(WebSocketExtensionBasicTestCase.class.getClassLoader());
        client = xnio.createWorker(OptionMap.builder().set(WORKER_IO_THREADS, 2).set(CONNECTION_HIGH_WATER, 1000000).set(CONNECTION_LOW_WATER, 1000000).set(WORKER_TASK_CORE_THREADS, 30).set(WORKER_TASK_MAX_THREADS, 30).set(TCP_NODELAY, true).set(CORK, true).getMap());
        WebSocketProtocolHandshakeHandler handler = WebSocketExtensionBasicTestCase.webSocketDebugHandler().addExtension(new PerMessageDeflateHandshake());
        DebugExtensionsHeaderHandler debug = new DebugExtensionsHeaderHandler(handler);
        DefaultServer.setRootHandler(Handlers.path().addPrefixPath("/", debug));
        final String SEC_WEBSOCKET_EXTENSIONS = "permessage-deflate; client_no_context_takeover; client_max_window_bits";
        final String SEC_WEBSOCKET_EXTENSIONS_EXPECTED = "[permessage-deflate; client_no_context_takeover]";// List format

        List<WebSocketExtension> extensions = WebSocketExtension.parse(SEC_WEBSOCKET_EXTENSIONS);
        final WebSocketClientNegotiation negotiation = new WebSocketClientNegotiation(null, extensions);
        Set<ExtensionHandshake> extensionHandshakes = new HashSet<>();
        extensionHandshakes.add(new PerMessageDeflateHandshake(true));
        final WebSocketChannel clientChannel = WebSocketClient.connect(client, null, DefaultServer.getBufferPool(), EMPTY, new URI(DefaultServer.getDefaultServerURL()), V13, negotiation, extensionHandshakes).get();
        final CountDownLatch latch = new CountDownLatch(1);
        final AtomicReference<String> result = new AtomicReference<>();
        clientChannel.getReceiveSetter().set(new AbstractReceiveListener() {
            @Override
            protected void onFullTextMessage(WebSocketChannel channel, BufferedTextMessage message) throws IOException {
                String data = message.getData();
                ROOT_LOGGER.info(((("onFullTextMessage - Client - Received: " + (data.getBytes().length)) + " bytes . Data: ") + data));
                result.set(data);
                latch.countDown();
            }

            @Override
            protected void onFullCloseMessage(WebSocketChannel channel, BufferedBinaryMessage message) throws IOException {
                message.getData().close();
                ROOT_LOGGER.info("onFullCloseMessage");
            }

            @Override
            protected void onError(WebSocketChannel channel, Throwable error) {
                ROOT_LOGGER.info("onError");
                super.onError(channel, error);
                error.printStackTrace();
                latch.countDown();
            }
        });
        clientChannel.resumeReceives();
        StreamSinkFrameChannel sendChannel = clientChannel.send(TEXT);
        new StringWriteChannelListener("Hello, World!").setup(sendChannel);
        latch.await(10, TimeUnit.SECONDS);
        Assert.assertEquals("Hello, World!", result.get());
        clientChannel.sendClose();
        client.shutdown();
        Assert.assertEquals(SEC_WEBSOCKET_EXTENSIONS_EXPECTED, debug.getResponseExtensions().toString());
    }
}

