/**
 *
 */
/**
 * ========================================================================
 */
/**
 * Copyright (c) 1995-2019 Mort Bay Consulting Pty. Ltd.
 */
/**
 * ------------------------------------------------------------------------
 */
/**
 * All rights reserved. This program and the accompanying materials
 */
/**
 * are made available under the terms of the Eclipse Public License v1.0
 */
/**
 * and Apache License v2.0 which accompanies this distribution.
 */
/**
 *
 */
/**
 * The Eclipse Public License is available at
 */
/**
 * http://www.eclipse.org/legal/epl-v10.html
 */
/**
 *
 */
/**
 * The Apache License v2.0 is available at
 */
/**
 * http://www.opensource.org/licenses/apache2.0.php
 */
/**
 *
 */
/**
 * You may elect to redistribute this code under either of these licenses.
 */
/**
 * ========================================================================
 */
/**
 *
 */
package org.eclipse.jetty.websocket.jsr356.server;


import ClientEndpointConfig.Builder;
import java.net.URI;
import java.util.Arrays;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import javax.websocket.ClientEndpointConfig;
import javax.websocket.Endpoint;
import javax.websocket.EndpointConfig;
import javax.websocket.Extension;
import javax.websocket.MessageHandler;
import javax.websocket.SendHandler;
import javax.websocket.SendResult;
import javax.websocket.Session;
import javax.websocket.WebSocketContainer;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.websocket.api.extensions.OutgoingFrames;
import org.eclipse.jetty.websocket.client.io.WebSocketClientConnection;
import org.eclipse.jetty.websocket.common.extensions.ExtensionStack;
import org.eclipse.jetty.websocket.common.extensions.compress.DeflateFrameExtension;
import org.eclipse.jetty.websocket.jsr356.JsrExtension;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


public class ExtensionStackProcessingTest {
    private Server server;

    private ServerConnector connector;

    private WebSocketContainer client;

    private ServletContextHandler servletContextHandler;

    @Test
    public void testDeflateFrameExtension() throws Exception {
        assumeDeflateFrameAvailable();
        ClientEndpointConfig config = Builder.create().extensions(Arrays.<Extension>asList(new JsrExtension("deflate-frame"))).build();
        final String content = "deflate_me";
        final CountDownLatch messageLatch = new CountDownLatch(1);
        URI uri = URI.create(("ws://localhost:" + (connector.getLocalPort())));
        Session session = client.connectToServer(new ExtensionStackProcessingTest.EndpointAdapter() {
            @Override
            public void onMessage(String message) {
                Assertions.assertEquals(content, message);
                messageLatch.countDown();
            }
        }, config, uri);
        // Make sure everything is wired properly.
        OutgoingFrames firstOut = getOutgoingHandler();
        Assertions.assertTrue((firstOut instanceof ExtensionStack));
        ExtensionStack extensionStack = ((ExtensionStack) (firstOut));
        Assertions.assertTrue(extensionStack.isRunning());
        OutgoingFrames secondOut = extensionStack.getNextOutgoing();
        Assertions.assertTrue((secondOut instanceof DeflateFrameExtension));
        DeflateFrameExtension deflateExtension = ((DeflateFrameExtension) (secondOut));
        Assertions.assertTrue(deflateExtension.isRunning());
        OutgoingFrames thirdOut = deflateExtension.getNextOutgoing();
        Assertions.assertTrue((thirdOut instanceof WebSocketClientConnection));
        final CountDownLatch latch = new CountDownLatch(1);
        session.getAsyncRemote().sendText(content, new SendHandler() {
            @Override
            public void onResult(SendResult result) {
                latch.countDown();
            }
        });
        Assertions.assertTrue(latch.await(5, TimeUnit.SECONDS));
        Assertions.assertTrue(messageLatch.await(5, TimeUnit.SECONDS));
    }

    @Test
    public void testPerMessageDeflateExtension() throws Exception {
        assumeDeflateFrameAvailable();
        ClientEndpointConfig config = Builder.create().extensions(Arrays.<Extension>asList(new JsrExtension("permessage-deflate"))).build();
        final String content = "deflate_me";
        final CountDownLatch messageLatch = new CountDownLatch(1);
        URI uri = URI.create(("ws://localhost:" + (connector.getLocalPort())));
        Session session = client.connectToServer(new ExtensionStackProcessingTest.EndpointAdapter() {
            @Override
            public void onMessage(String message) {
                Assertions.assertEquals(content, message);
                messageLatch.countDown();
            }
        }, config, uri);
        final CountDownLatch latch = new CountDownLatch(1);
        session.getAsyncRemote().sendText(content, new SendHandler() {
            @Override
            public void onResult(SendResult result) {
                latch.countDown();
            }
        });
        Assertions.assertTrue(latch.await(5, TimeUnit.SECONDS));
        Assertions.assertTrue(messageLatch.await(5, TimeUnit.SECONDS));
    }

    private abstract static class EndpointAdapter extends Endpoint implements MessageHandler.Whole<String> {
        @Override
        public void onOpen(Session session, EndpointConfig config) {
            session.addMessageHandler(this);
        }
    }
}

