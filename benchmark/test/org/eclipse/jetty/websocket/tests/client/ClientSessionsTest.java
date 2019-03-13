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
package org.eclipse.jetty.websocket.tests.client;


import StatusCode.NORMAL;
import java.net.URI;
import java.util.Collection;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.websocket.api.RemoteEndpoint;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.util.WSURI;
import org.eclipse.jetty.websocket.client.ClientUpgradeRequest;
import org.eclipse.jetty.websocket.client.WebSocketClient;
import org.eclipse.jetty.websocket.common.WebSocketSession;
import org.eclipse.jetty.websocket.common.WebSocketSessionListener;
import org.eclipse.jetty.websocket.tests.CloseTrackingEndpoint;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


public class ClientSessionsTest {
    private Server server;

    @Test
    public void testBasicEcho_FromClient() throws Exception {
        WebSocketClient client = new WebSocketClient();
        CountDownLatch onSessionCloseLatch = new CountDownLatch(1);
        client.addSessionListener(new WebSocketSessionListener() {
            @Override
            public void onSessionOpened(WebSocketSession session) {
            }

            @Override
            public void onSessionClosed(WebSocketSession session) {
                onSessionCloseLatch.countDown();
            }
        });
        client.start();
        try {
            CloseTrackingEndpoint cliSock = new CloseTrackingEndpoint();
            client.getPolicy().setIdleTimeout(10000);
            URI wsUri = WSURI.toWebsocket(server.getURI().resolve("/ws"));
            ClientUpgradeRequest request = new ClientUpgradeRequest();
            request.setSubProtocols("echo");
            Future<Session> future = client.connect(cliSock, wsUri, request);
            Session sess = null;
            try {
                sess = future.get(30000, TimeUnit.MILLISECONDS);
                MatcherAssert.assertThat("Session", sess, Matchers.notNullValue());
                MatcherAssert.assertThat("Session.open", sess.isOpen(), Matchers.is(true));
                MatcherAssert.assertThat("Session.upgradeRequest", sess.getUpgradeRequest(), Matchers.notNullValue());
                MatcherAssert.assertThat("Session.upgradeResponse", sess.getUpgradeResponse(), Matchers.notNullValue());
                Collection<WebSocketSession> sessions = client.getBeans(WebSocketSession.class);
                MatcherAssert.assertThat("client.connectionManager.sessions.size", sessions.size(), Matchers.is(1));
                RemoteEndpoint remote = sess.getRemote();
                remote.sendString("Hello World!");
                Set<WebSocketSession> open = client.getOpenSessions();
                MatcherAssert.assertThat("(Before Close) Open Sessions.size", open.size(), Matchers.is(1));
                String received = cliSock.messageQueue.poll(5, TimeUnit.SECONDS);
                MatcherAssert.assertThat("Message", received, Matchers.containsString("Hello World!"));
            } finally {
                getSession().close();
            }
            cliSock.assertReceivedCloseEvent(30000, Matchers.is(NORMAL));
            Assertions.assertTrue(onSessionCloseLatch.await(5, TimeUnit.SECONDS), "Saw onSessionClose events");
            TimeUnit.SECONDS.sleep(1);
            Set<WebSocketSession> open = client.getOpenSessions();
            MatcherAssert.assertThat("(After Close) Open Sessions.size", open.size(), Matchers.is(0));
        } finally {
            client.stop();
        }
    }
}

