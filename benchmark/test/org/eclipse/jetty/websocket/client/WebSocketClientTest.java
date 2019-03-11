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
package org.eclipse.jetty.websocket.client;


import Timeouts.CONNECT;
import Timeouts.CONNECT_UNIT;
import Timeouts.POLL_EVENT;
import Timeouts.POLL_EVENT_UNIT;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.URI;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import org.eclipse.jetty.util.StringUtil;
import org.eclipse.jetty.websocket.api.BatchMode;
import org.eclipse.jetty.websocket.api.RemoteEndpoint;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.UpgradeRequest;
import org.eclipse.jetty.websocket.common.WebSocketFrame;
import org.eclipse.jetty.websocket.common.WebSocketSession;
import org.eclipse.jetty.websocket.common.frames.TextFrame;
import org.eclipse.jetty.websocket.common.io.FutureWriteCallback;
import org.eclipse.jetty.websocket.common.test.BlockheadConnection;
import org.eclipse.jetty.websocket.common.test.BlockheadServer;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;


public class WebSocketClientTest {
    private static BlockheadServer server;

    private WebSocketClient client;

    @Test
    public void testAddExtension_NotInstalled() throws Exception {
        JettyTrackingSocket cliSock = new JettyTrackingSocket();
        client.getPolicy().setIdleTimeout(10000);
        URI wsUri = WebSocketClientTest.server.getWsUri();
        ClientUpgradeRequest request = new ClientUpgradeRequest();
        request.setSubProtocols("echo");
        request.addExtensions("x-bad");
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            // Should trigger failure on bad extension
            client.connect(cliSock, wsUri, request);
        });
    }

    @Test
    public void testBasicEcho_FromClient() throws Exception {
        JettyTrackingSocket cliSock = new JettyTrackingSocket();
        client.getPolicy().setIdleTimeout(10000);
        // Hook into server connection creation
        CompletableFuture<BlockheadConnection> serverConnFut = new CompletableFuture<>();
        WebSocketClientTest.server.addConnectFuture(serverConnFut);
        URI wsUri = WebSocketClientTest.server.getWsUri();
        ClientUpgradeRequest request = new ClientUpgradeRequest();
        request.setSubProtocols("echo");
        Future<Session> future = client.connect(cliSock, wsUri, request);
        try (BlockheadConnection serverConn = serverConnFut.get(CONNECT, CONNECT_UNIT)) {
            // Setup echo of frames on server side
            serverConn.setIncomingFrameConsumer(( frame) -> {
                WebSocketFrame copy = WebSocketFrame.copy(frame);
                copy.setMask(null);// strip client mask (if present)

                serverConn.write(copy);
            });
            Session sess = future.get(30, TimeUnit.SECONDS);
            MatcherAssert.assertThat("Session", sess, Matchers.notNullValue());
            MatcherAssert.assertThat("Session.open", sess.isOpen(), Matchers.is(true));
            MatcherAssert.assertThat("Session.upgradeRequest", sess.getUpgradeRequest(), Matchers.notNullValue());
            MatcherAssert.assertThat("Session.upgradeResponse", sess.getUpgradeResponse(), Matchers.notNullValue());
            cliSock.assertWasOpened();
            cliSock.assertNotClosed();
            Collection<WebSocketSession> sessions = client.getOpenSessions();
            MatcherAssert.assertThat("client.connectionManager.sessions.size", sessions.size(), Matchers.is(1));
            RemoteEndpoint remote = getSession().getRemote();
            remote.sendStringByFuture("Hello World!");
            if ((remote.getBatchMode()) == (BatchMode.ON))
                remote.flush();

            // wait for response from server
            String received = cliSock.messageQueue.poll(POLL_EVENT, POLL_EVENT_UNIT);
            MatcherAssert.assertThat("Message", received, Matchers.containsString("Hello World"));
        }
    }

    @Test
    public void testBasicEcho_UsingCallback() throws Exception {
        client.setMaxIdleTimeout(160000);
        JettyTrackingSocket cliSock = new JettyTrackingSocket();
        // Hook into server connection creation
        CompletableFuture<BlockheadConnection> serverConnFut = new CompletableFuture<>();
        WebSocketClientTest.server.addConnectFuture(serverConnFut);
        URI wsUri = WebSocketClientTest.server.getWsUri();
        ClientUpgradeRequest request = new ClientUpgradeRequest();
        request.setSubProtocols("echo");
        Future<Session> future = client.connect(cliSock, wsUri, request);
        try (BlockheadConnection serverConn = serverConnFut.get(CONNECT, CONNECT_UNIT)) {
            Session sess = future.get(30, TimeUnit.SECONDS);
            MatcherAssert.assertThat("Session", sess, Matchers.notNullValue());
            MatcherAssert.assertThat("Session.open", sess.isOpen(), Matchers.is(true));
            MatcherAssert.assertThat("Session.upgradeRequest", sess.getUpgradeRequest(), Matchers.notNullValue());
            MatcherAssert.assertThat("Session.upgradeResponse", sess.getUpgradeResponse(), Matchers.notNullValue());
            cliSock.assertWasOpened();
            cliSock.assertNotClosed();
            Collection<WebSocketSession> sessions = client.getBeans(WebSocketSession.class);
            MatcherAssert.assertThat("client.connectionManager.sessions.size", sessions.size(), Matchers.is(1));
            FutureWriteCallback callback = new FutureWriteCallback();
            getSession().getRemote().sendString("Hello World!", callback);
            callback.get(1, TimeUnit.SECONDS);
        }
    }

    @Test
    public void testBasicEcho_FromServer() throws Exception {
        // Hook into server connection creation
        CompletableFuture<BlockheadConnection> serverConnFut = new CompletableFuture<>();
        WebSocketClientTest.server.addConnectFuture(serverConnFut);
        JettyTrackingSocket wsocket = new JettyTrackingSocket();
        Future<Session> future = client.connect(wsocket, WebSocketClientTest.server.getWsUri());
        try (BlockheadConnection serverConn = serverConnFut.get(CONNECT, CONNECT_UNIT)) {
            // Validate connect
            Session sess = future.get(30, TimeUnit.SECONDS);
            MatcherAssert.assertThat("Session", sess, Matchers.notNullValue());
            MatcherAssert.assertThat("Session.open", sess.isOpen(), Matchers.is(true));
            MatcherAssert.assertThat("Session.upgradeRequest", sess.getUpgradeRequest(), Matchers.notNullValue());
            MatcherAssert.assertThat("Session.upgradeResponse", sess.getUpgradeResponse(), Matchers.notNullValue());
            // Have server send initial message
            serverConn.write(new TextFrame().setPayload("Hello World"));
            // Verify connect
            future.get(30, TimeUnit.SECONDS);
            wsocket.assertWasOpened();
            String received = wsocket.messageQueue.poll(POLL_EVENT, POLL_EVENT_UNIT);
            MatcherAssert.assertThat("Message", received, Matchers.containsString("Hello World"));
        }
    }

    @Test
    public void testLocalRemoteAddress() throws Exception {
        JettyTrackingSocket wsocket = new JettyTrackingSocket();
        URI wsUri = WebSocketClientTest.server.getWsUri();
        Future<Session> future = client.connect(wsocket, wsUri);
        future.get(30, TimeUnit.SECONDS);
        Assertions.assertTrue(wsocket.openLatch.await(1, TimeUnit.SECONDS));
        InetSocketAddress local = getSession().getLocalAddress();
        InetSocketAddress remote = getSession().getRemoteAddress();
        MatcherAssert.assertThat("Local Socket Address", local, Matchers.notNullValue());
        MatcherAssert.assertThat("Remote Socket Address", remote, Matchers.notNullValue());
        // Hard to validate (in a portable unit test) the local address that was used/bound in the low level Jetty Endpoint
        MatcherAssert.assertThat("Local Socket Address / Host", local.getAddress().getHostAddress(), Matchers.notNullValue());
        MatcherAssert.assertThat("Local Socket Address / Port", local.getPort(), Matchers.greaterThan(0));
        String uriHostAddress = InetAddress.getByName(wsUri.getHost()).getHostAddress();
        MatcherAssert.assertThat("Remote Socket Address / Host", remote.getAddress().getHostAddress(), Matchers.is(uriHostAddress));
        MatcherAssert.assertThat("Remote Socket Address / Port", remote.getPort(), Matchers.greaterThan(0));
    }

    /**
     * Ensure that <code>@WebSocket(maxTextMessageSize = 100*1024)</code> behaves as expected.
     *
     * @throws Exception
     * 		on test failure
     */
    @Test
    public void testMaxMessageSize() throws Exception {
        MaxMessageSocket wsocket = new MaxMessageSocket();
        // Hook into server connection creation
        CompletableFuture<BlockheadConnection> serverConnFut = new CompletableFuture<>();
        WebSocketClientTest.server.addConnectFuture(serverConnFut);
        URI wsUri = WebSocketClientTest.server.getWsUri();
        Future<Session> future = client.connect(wsocket, wsUri);
        try (BlockheadConnection serverConn = serverConnFut.get(CONNECT, CONNECT_UNIT)) {
            // Setup echo of frames on server side
            serverConn.setIncomingFrameConsumer(( frame) -> {
                WebSocketFrame copy = WebSocketFrame.copy(frame);
                copy.setMask(null);// strip client mask (if present)

                serverConn.write(copy);
            });
            wsocket.awaitConnect(1, TimeUnit.SECONDS);
            Session sess = future.get(30, TimeUnit.SECONDS);
            MatcherAssert.assertThat("Session", sess, Matchers.notNullValue());
            MatcherAssert.assertThat("Session.open", sess.isOpen(), Matchers.is(true));
            // Create string that is larger than default size of 64k
            // but smaller than maxMessageSize of 100k
            byte[] buf = new byte[80 * 1024];
            Arrays.fill(buf, ((byte) ('x')));
            String msg = StringUtil.toUTF8String(buf, 0, buf.length);
            wsocket.getSession().getRemote().sendStringByFuture(msg);
            // wait for response from server
            wsocket.waitForMessage(1, TimeUnit.SECONDS);
            wsocket.assertMessage(msg);
            Assertions.assertTrue(wsocket.dataLatch.await(2, TimeUnit.SECONDS));
        }
    }

    @Test
    public void testParameterMap() throws Exception {
        JettyTrackingSocket wsocket = new JettyTrackingSocket();
        URI wsUri = WebSocketClientTest.server.getWsUri().resolve("/test?snack=cashews&amount=handful&brand=off");
        Future<Session> future = client.connect(wsocket, wsUri);
        future.get(30, TimeUnit.SECONDS);
        Assertions.assertTrue(wsocket.openLatch.await(1, TimeUnit.SECONDS));
        Session session = wsocket.getSession();
        UpgradeRequest req = session.getUpgradeRequest();
        MatcherAssert.assertThat("Upgrade Request", req, Matchers.notNullValue());
        Map<String, List<String>> parameterMap = req.getParameterMap();
        MatcherAssert.assertThat("Parameter Map", parameterMap, Matchers.notNullValue());
        MatcherAssert.assertThat("Parameter[snack]", parameterMap.get("snack"), Matchers.is(Arrays.asList(new String[]{ "cashews" })));
        MatcherAssert.assertThat("Parameter[amount]", parameterMap.get("amount"), Matchers.is(Arrays.asList(new String[]{ "handful" })));
        MatcherAssert.assertThat("Parameter[brand]", parameterMap.get("brand"), Matchers.is(Arrays.asList(new String[]{ "off" })));
        MatcherAssert.assertThat("Parameter[cost]", parameterMap.get("cost"), Matchers.nullValue());
    }
}

