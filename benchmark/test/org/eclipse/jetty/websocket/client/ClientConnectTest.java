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


import HttpHeader.AUTHORIZATION;
import Timeouts.CONNECT;
import Timeouts.CONNECT_UNIT;
import java.net.ConnectException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.SocketTimeoutException;
import java.net.URI;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.http.HttpField;
import org.eclipse.jetty.http.HttpFields;
import org.eclipse.jetty.io.ByteBufferPool;
import org.eclipse.jetty.io.MappedByteBufferPool;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.UpgradeException;
import org.eclipse.jetty.websocket.common.AcceptHash;
import org.eclipse.jetty.websocket.common.test.BlockheadConnection;
import org.eclipse.jetty.websocket.common.test.BlockheadServer;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;


/**
 * Various connect condition testing
 */
@SuppressWarnings("Duplicates")
public class ClientConnectTest {
    public ByteBufferPool bufferPool = new MappedByteBufferPool();

    private static BlockheadServer server;

    private WebSocketClient client;

    @Test
    public void testUpgradeRequest() throws Exception {
        JettyTrackingSocket wsocket = new JettyTrackingSocket();
        URI wsUri = ClientConnectTest.server.getWsUri();
        Future<Session> future = client.connect(wsocket, wsUri);
        Session sess = future.get(30, TimeUnit.SECONDS);
        wsocket.waitForConnected();
        MatcherAssert.assertThat("Connect.UpgradeRequest", wsocket.connectUpgradeRequest, Matchers.notNullValue());
        MatcherAssert.assertThat("Connect.UpgradeResponse", wsocket.connectUpgradeResponse, Matchers.notNullValue());
        sess.close();
    }

    @Test
    public void testAltConnect() throws Exception {
        JettyTrackingSocket wsocket = new JettyTrackingSocket();
        URI wsUri = ClientConnectTest.server.getWsUri();
        HttpClient httpClient = new HttpClient();
        try {
            httpClient.start();
            WebSocketUpgradeRequest req = new WebSocketUpgradeRequest(new WebSocketClient(), httpClient, wsUri, wsocket);
            req.header("X-Foo", "Req");
            CompletableFuture<Session> sess = req.sendAsync();
            sess.thenAccept(( s) -> {
                System.out.printf("Session: %s%n", s);
                s.close();
                assertThat("Connect.UpgradeRequest", wsocket.connectUpgradeRequest, notNullValue());
                assertThat("Connect.UpgradeResponse", wsocket.connectUpgradeResponse, notNullValue());
            });
        } finally {
            httpClient.stop();
        }
    }

    @Test
    public void testUpgradeWithAuthorizationHeader() throws Exception {
        JettyTrackingSocket wsocket = new JettyTrackingSocket();
        // Hook into server connection creation
        CompletableFuture<BlockheadConnection> serverConnFut = new CompletableFuture<>();
        ClientConnectTest.server.addConnectFuture(serverConnFut);
        URI wsUri = ClientConnectTest.server.getWsUri();
        ClientUpgradeRequest upgradeRequest = new ClientUpgradeRequest();
        // actual value for this test is irrelevant, its important that this
        // header actually be sent with a value (the value specified)
        upgradeRequest.setHeader("Authorization", "Basic YWxhZGRpbjpvcGVuc2VzYW1l");
        Future<Session> future = client.connect(wsocket, wsUri, upgradeRequest);
        try (BlockheadConnection serverConn = serverConnFut.get(CONNECT, CONNECT_UNIT)) {
            HttpFields upgradeRequestHeaders = serverConn.getUpgradeRequestHeaders();
            Session sess = future.get(30, TimeUnit.SECONDS);
            HttpField authHeader = upgradeRequestHeaders.getField(AUTHORIZATION);
            MatcherAssert.assertThat("Server Request Authorization Header", authHeader, Matchers.is(Matchers.notNullValue()));
            MatcherAssert.assertThat("Server Request Authorization Value", authHeader.getValue(), Matchers.is("Basic YWxhZGRpbjpvcGVuc2VzYW1l"));
            MatcherAssert.assertThat("Connect.UpgradeRequest", wsocket.connectUpgradeRequest, Matchers.notNullValue());
            MatcherAssert.assertThat("Connect.UpgradeResponse", wsocket.connectUpgradeResponse, Matchers.notNullValue());
            sess.close();
        }
    }

    @Test
    public void testBadHandshake() throws Exception {
        JettyTrackingSocket wsocket = new JettyTrackingSocket();
        // Force 404 response, no upgrade for this test
        ClientConnectTest.server.setRequestHandling(( req, resp) -> {
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return true;
        });
        URI wsUri = ClientConnectTest.server.getWsUri();
        Future<Session> future = client.connect(wsocket, wsUri);
        // The attempt to get upgrade response future should throw error
        ExecutionException e = Assertions.assertThrows(ExecutionException.class, () -> future.get(30, TimeUnit.SECONDS));
        UpgradeException ue = assertExpectedError(e, wsocket, Matchers.instanceOf(UpgradeException.class));
        MatcherAssert.assertThat("UpgradeException.requestURI", ue.getRequestURI(), Matchers.notNullValue());
        MatcherAssert.assertThat("UpgradeException.requestURI", ue.getRequestURI().toASCIIString(), Matchers.is(wsUri.toASCIIString()));
        MatcherAssert.assertThat("UpgradeException.responseStatusCode", ue.getResponseStatusCode(), Matchers.is(404));
    }

    @Test
    public void testBadHandshake_GetOK() throws Exception {
        JettyTrackingSocket wsocket = new JettyTrackingSocket();
        // Force 200 response, no response body content, no upgrade for this test
        ClientConnectTest.server.setRequestHandling(( req, resp) -> {
            resp.setStatus(HttpServletResponse.SC_OK);
            return true;
        });
        URI wsUri = ClientConnectTest.server.getWsUri();
        Future<Session> future = client.connect(wsocket, wsUri);
        // The attempt to get upgrade response future should throw error
        ExecutionException e = Assertions.assertThrows(ExecutionException.class, () -> future.get(30, TimeUnit.SECONDS));
        UpgradeException ue = assertExpectedError(e, wsocket, Matchers.instanceOf(UpgradeException.class));
        MatcherAssert.assertThat("UpgradeException.requestURI", ue.getRequestURI(), Matchers.notNullValue());
        MatcherAssert.assertThat("UpgradeException.requestURI", ue.getRequestURI().toASCIIString(), Matchers.is(wsUri.toASCIIString()));
        MatcherAssert.assertThat("UpgradeException.responseStatusCode", ue.getResponseStatusCode(), Matchers.is(200));
    }

    @Test
    public void testBadHandshake_GetOK_WithSecWebSocketAccept() throws Exception {
        JettyTrackingSocket wsocket = new JettyTrackingSocket();
        // Force 200 response, no response body content, incomplete websocket response headers, no actual upgrade for this test
        ClientConnectTest.server.setRequestHandling(( req, resp) -> {
            String key = req.getHeader(HttpHeader.SEC_WEBSOCKET_KEY.toString());
            resp.setStatus(HttpServletResponse.SC_OK);
            resp.setHeader(HttpHeader.SEC_WEBSOCKET_ACCEPT.toString(), AcceptHash.hashKey(key));
            return true;
        });
        URI wsUri = ClientConnectTest.server.getWsUri();
        Future<Session> future = client.connect(wsocket, wsUri);
        // The attempt to get upgrade response future should throw error
        ExecutionException e = Assertions.assertThrows(ExecutionException.class, () -> future.get(30, TimeUnit.SECONDS));
        UpgradeException ue = assertExpectedError(e, wsocket, Matchers.instanceOf(UpgradeException.class));
        MatcherAssert.assertThat("UpgradeException.requestURI", ue.getRequestURI(), Matchers.notNullValue());
        MatcherAssert.assertThat("UpgradeException.requestURI", ue.getRequestURI().toASCIIString(), Matchers.is(wsUri.toASCIIString()));
        MatcherAssert.assertThat("UpgradeException.responseStatusCode", ue.getResponseStatusCode(), Matchers.is(200));
    }

    @Test
    public void testBadHandshake_SwitchingProtocols_InvalidConnectionHeader() throws Exception {
        JettyTrackingSocket wsocket = new JettyTrackingSocket();
        // Force 101 response, with invalid Connection header, invalid handshake
        ClientConnectTest.server.setRequestHandling(( req, resp) -> {
            String key = req.getHeader(HttpHeader.SEC_WEBSOCKET_KEY.toString());
            resp.setStatus(HttpServletResponse.SC_SWITCHING_PROTOCOLS);
            resp.setHeader(HttpHeader.CONNECTION.toString(), "close");
            resp.setHeader(HttpHeader.SEC_WEBSOCKET_ACCEPT.toString(), AcceptHash.hashKey(key));
            return true;
        });
        URI wsUri = ClientConnectTest.server.getWsUri();
        Future<Session> future = client.connect(wsocket, wsUri);
        // The attempt to get upgrade response future should throw error
        ExecutionException e = Assertions.assertThrows(ExecutionException.class, () -> future.get(30, TimeUnit.SECONDS));
        UpgradeException ue = assertExpectedError(e, wsocket, Matchers.instanceOf(UpgradeException.class));
        MatcherAssert.assertThat("UpgradeException.requestURI", ue.getRequestURI(), Matchers.notNullValue());
        MatcherAssert.assertThat("UpgradeException.requestURI", ue.getRequestURI().toASCIIString(), Matchers.is(wsUri.toASCIIString()));
        MatcherAssert.assertThat("UpgradeException.responseStatusCode", ue.getResponseStatusCode(), Matchers.is(101));
    }

    @Test
    public void testBadHandshake_SwitchingProtocols_NoConnectionHeader() throws Exception {
        JettyTrackingSocket wsocket = new JettyTrackingSocket();
        // Force 101 response, with no Connection header, invalid handshake
        ClientConnectTest.server.setRequestHandling(( req, resp) -> {
            String key = req.getHeader(HttpHeader.SEC_WEBSOCKET_KEY.toString());
            resp.setStatus(HttpServletResponse.SC_SWITCHING_PROTOCOLS);
            // Intentionally leave out Connection header
            resp.setHeader(HttpHeader.SEC_WEBSOCKET_ACCEPT.toString(), AcceptHash.hashKey(key));
            return true;
        });
        URI wsUri = ClientConnectTest.server.getWsUri();
        Future<Session> future = client.connect(wsocket, wsUri);
        // The attempt to get upgrade response future should throw error
        ExecutionException e = Assertions.assertThrows(ExecutionException.class, () -> future.get(30, TimeUnit.SECONDS));
        UpgradeException ue = assertExpectedError(e, wsocket, Matchers.instanceOf(UpgradeException.class));
        MatcherAssert.assertThat("UpgradeException.requestURI", ue.getRequestURI(), Matchers.notNullValue());
        MatcherAssert.assertThat("UpgradeException.requestURI", ue.getRequestURI().toASCIIString(), Matchers.is(wsUri.toASCIIString()));
        MatcherAssert.assertThat("UpgradeException.responseStatusCode", ue.getResponseStatusCode(), Matchers.is(101));
    }

    @Test
    public void testBadUpgrade() throws Exception {
        JettyTrackingSocket wsocket = new JettyTrackingSocket();
        // Force 101 response, with invalid response accept header
        ClientConnectTest.server.setRequestHandling(( req, resp) -> {
            resp.setStatus(HttpServletResponse.SC_SWITCHING_PROTOCOLS);
            resp.setHeader(HttpHeader.SEC_WEBSOCKET_ACCEPT.toString(), "rubbish");
            return true;
        });
        URI wsUri = ClientConnectTest.server.getWsUri();
        Future<Session> future = client.connect(wsocket, wsUri);
        // The attempt to get upgrade response future should throw error
        ExecutionException e = Assertions.assertThrows(ExecutionException.class, () -> future.get(30, TimeUnit.SECONDS));
        UpgradeException ue = assertExpectedError(e, wsocket, Matchers.instanceOf(UpgradeException.class));
        MatcherAssert.assertThat("UpgradeException.requestURI", ue.getRequestURI(), Matchers.notNullValue());
        MatcherAssert.assertThat("UpgradeException.requestURI", ue.getRequestURI().toASCIIString(), Matchers.is(wsUri.toASCIIString()));
        MatcherAssert.assertThat("UpgradeException.responseStatusCode", ue.getResponseStatusCode(), Matchers.is(101));
    }

    @Test
    public void testConnectionNotAccepted() throws Exception {
        JettyTrackingSocket wsocket = new JettyTrackingSocket();
        try (ServerSocket serverSocket = new ServerSocket()) {
            InetAddress addr = InetAddress.getByName("localhost");
            InetSocketAddress endpoint = new InetSocketAddress(addr, 0);
            serverSocket.bind(endpoint, 1);
            int port = serverSocket.getLocalPort();
            URI wsUri = URI.create(String.format("ws://%s:%d/", addr.getHostAddress(), port));
            Future<Session> future = client.connect(wsocket, wsUri);
            // Intentionally not accept incoming socket.
            // serverSocket.accept();
            try {
                future.get(3, TimeUnit.SECONDS);
                Assertions.fail("Should have Timed Out");
            } catch (ExecutionException e) {
                assertExpectedError(e, wsocket, Matchers.instanceOf(UpgradeException.class));
                // Possible Passing Path (active session wait timeout)
                wsocket.assertNotOpened();
            } catch (TimeoutException e) {
                // Possible Passing Path (concurrency timeout)
                wsocket.assertNotOpened();
            }
        }
    }

    @Test
    public void testConnectionRefused() throws Exception {
        JettyTrackingSocket wsocket = new JettyTrackingSocket();
        // Intentionally bad port with nothing listening on it
        URI wsUri = new URI("ws://127.0.0.1:1");
        try {
            Future<Session> future = client.connect(wsocket, wsUri);
            // The attempt to get upgrade response future should throw error
            future.get(3, TimeUnit.SECONDS);
            Assertions.fail("Expected ExecutionException -> ConnectException");
        } catch (ConnectException e) {
            Throwable t = wsocket.errorQueue.remove();
            MatcherAssert.assertThat("Error Queue[0]", t, Matchers.instanceOf(ConnectException.class));
            wsocket.assertNotOpened();
        } catch (ExecutionException e) {
            assertExpectedError(e, wsocket, Matchers.anyOf(Matchers.instanceOf(UpgradeException.class), Matchers.instanceOf(SocketTimeoutException.class), Matchers.instanceOf(ConnectException.class)));
        }
    }

    @Test
    public void testConnectionTimeout_Concurrent() throws Exception {
        JettyTrackingSocket wsocket = new JettyTrackingSocket();
        try (ServerSocket serverSocket = new ServerSocket()) {
            InetAddress addr = InetAddress.getByName("localhost");
            InetSocketAddress endpoint = new InetSocketAddress(addr, 0);
            serverSocket.bind(endpoint, 1);
            int port = serverSocket.getLocalPort();
            URI wsUri = URI.create(String.format("ws://%s:%d/", addr.getHostAddress(), port));
            Future<Session> future = client.connect(wsocket, wsUri);
            // Accept the connection, but do nothing on it (no response, no upgrade, etc)
            serverSocket.accept();
            // The attempt to get upgrade response future should throw error
            Exception e = Assertions.assertThrows(Exception.class, () -> future.get(3, TimeUnit.SECONDS));
            if (e instanceof ExecutionException) {
                assertExpectedError(((ExecutionException) (e)), wsocket, Matchers.anyOf(Matchers.instanceOf(ConnectException.class), Matchers.instanceOf(UpgradeException.class)));
            } else {
                MatcherAssert.assertThat("Should have been a TimeoutException", e, Matchers.instanceOf(TimeoutException.class));
            }
        }
    }
}

