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
import java.net.CookieManager;
import java.net.HttpCookie;
import java.util.Collections;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import org.eclipse.jetty.util.log.Log;
import org.eclipse.jetty.util.log.Logger;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.WebSocketAdapter;
import org.eclipse.jetty.websocket.common.test.BlockheadConnection;
import org.eclipse.jetty.websocket.common.test.BlockheadServer;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


public class CookieTest {
    private static final Logger LOG = Log.getLogger(CookieTest.class);

    public static class CookieTrackingSocket extends WebSocketAdapter {
        public LinkedBlockingQueue<String> messageQueue = new LinkedBlockingQueue<>();

        public LinkedBlockingQueue<Throwable> errorQueue = new LinkedBlockingQueue<>();

        private CountDownLatch openLatch = new CountDownLatch(1);

        @Override
        public void onWebSocketConnect(Session sess) {
            openLatch.countDown();
            super.onWebSocketConnect(sess);
        }

        @Override
        public void onWebSocketText(String message) {
            System.err.printf("onTEXT - %s%n", message);
            messageQueue.add(message);
        }

        @Override
        public void onWebSocketError(Throwable cause) {
            System.err.printf("onERROR - %s%n", cause);
            errorQueue.add(cause);
        }

        public void awaitOpen(int duration, TimeUnit unit) throws InterruptedException {
            Assertions.assertTrue(openLatch.await(duration, unit), "Open Latch");
        }
    }

    private static BlockheadServer server;

    private WebSocketClient client;

    @Test
    public void testViaCookieManager() throws Exception {
        // Setup client
        CookieManager cookieMgr = new CookieManager();
        client.setCookieStore(cookieMgr.getCookieStore());
        HttpCookie cookie = new HttpCookie("hello", "world");
        cookie.setPath("/");
        cookie.setVersion(0);
        cookie.setMaxAge(100000);
        cookieMgr.getCookieStore().add(CookieTest.server.getWsUri(), cookie);
        cookie = new HttpCookie("foo", "bar is the word");
        cookie.setPath("/");
        cookie.setMaxAge(100000);
        cookieMgr.getCookieStore().add(CookieTest.server.getWsUri(), cookie);
        // Hook into server connection creation
        CompletableFuture<BlockheadConnection> serverConnFut = new CompletableFuture<>();
        CookieTest.server.addConnectFuture(serverConnFut);
        // Client connects
        CookieTest.CookieTrackingSocket clientSocket = new CookieTest.CookieTrackingSocket();
        Future<Session> clientConnectFuture = client.connect(clientSocket, CookieTest.server.getWsUri());
        try (BlockheadConnection serverConn = serverConnFut.get(CONNECT, CONNECT_UNIT)) {
            // client confirms upgrade and receipt of frame
            String serverCookies = confirmClientUpgradeAndCookies(clientSocket, clientConnectFuture, serverConn);
            MatcherAssert.assertThat("Cookies seen at server side", serverCookies, Matchers.containsString("hello=world"));
            MatcherAssert.assertThat("Cookies seen at server side", serverCookies, Matchers.containsString("foo=bar is the word"));
        }
    }

    @Test
    public void testViaServletUpgradeRequest() throws Exception {
        // Setup client
        HttpCookie cookie = new HttpCookie("hello", "world");
        cookie.setPath("/");
        cookie.setMaxAge(100000);
        ClientUpgradeRequest request = new ClientUpgradeRequest();
        request.setCookies(Collections.singletonList(cookie));
        // Hook into server connection creation
        CompletableFuture<BlockheadConnection> serverConnFut = new CompletableFuture<>();
        CookieTest.server.addConnectFuture(serverConnFut);
        // Client connects
        CookieTest.CookieTrackingSocket clientSocket = new CookieTest.CookieTrackingSocket();
        Future<Session> clientConnectFuture = client.connect(clientSocket, CookieTest.server.getWsUri(), request);
        try (BlockheadConnection serverConn = serverConnFut.get(CONNECT, CONNECT_UNIT)) {
            // client confirms upgrade and receipt of frame
            String serverCookies = confirmClientUpgradeAndCookies(clientSocket, clientConnectFuture, serverConn);
            MatcherAssert.assertThat("Cookies seen at server side", serverCookies, Matchers.containsString("hello=world"));
        }
    }
}

