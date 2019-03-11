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


import StatusCode.ABNORMAL;
import StatusCode.MESSAGE_TOO_LARGE;
import StatusCode.NORMAL;
import StatusCode.SHUTDOWN;
import java.io.IOException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import org.eclipse.jetty.io.EndPoint;
import org.eclipse.jetty.io.EofException;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.util.log.Log;
import org.eclipse.jetty.util.log.Logger;
import org.eclipse.jetty.websocket.api.CloseException;
import org.eclipse.jetty.websocket.api.MessageTooLargeException;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.WebSocketFrameListener;
import org.eclipse.jetty.websocket.api.WebSocketListener;
import org.eclipse.jetty.websocket.api.extensions.Frame;
import org.eclipse.jetty.websocket.api.util.WSURI;
import org.eclipse.jetty.websocket.client.WebSocketClient;
import org.eclipse.jetty.websocket.common.CloseInfo;
import org.eclipse.jetty.websocket.common.OpCode;
import org.eclipse.jetty.websocket.tests.CloseTrackingEndpoint;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;


public class ClientCloseTest {
    private Server server;

    private WebSocketClient client;

    @Test
    public void testHalfClose() throws Exception {
        // Set client timeout
        final int timeout = 5000;
        client.setMaxIdleTimeout(timeout);
        ClientOpenSessionTracker clientSessionTracker = new ClientOpenSessionTracker(1);
        clientSessionTracker.addTo(client);
        // Client connects
        URI wsUri = WSURI.toWebsocket(server.getURI().resolve("/ws"));
        CloseTrackingEndpoint clientSocket = new CloseTrackingEndpoint();
        Future<Session> clientConnectFuture = client.connect(clientSocket, wsUri);
        try (Session session = confirmConnection(clientSocket, clientConnectFuture)) {
            // client confirms connection via echo
            // client sends close frame (code 1000, normal)
            final String origCloseReason = "send-more-frames";
            getSession().close(NORMAL, origCloseReason);
            // Verify received messages
            String recvMsg = clientSocket.messageQueue.poll(5, TimeUnit.SECONDS);
            MatcherAssert.assertThat("Received message 1", recvMsg, Matchers.is("Hello"));
            recvMsg = clientSocket.messageQueue.poll(5, TimeUnit.SECONDS);
            MatcherAssert.assertThat("Received message 2", recvMsg, Matchers.is("World"));
            // Verify that there are no errors
            MatcherAssert.assertThat("Error events", clientSocket.error.get(), Matchers.nullValue());
            // client close event on ws-endpoint
            clientSocket.assertReceivedCloseEvent(timeout, Matchers.is(NORMAL), Matchers.containsString(""));
        }
        clientSessionTracker.assertClosedProperly(client);
    }

    @Test
    public void testMessageTooLargeException() throws Exception {
        // Set client timeout
        final int timeout = 3000;
        client.setMaxIdleTimeout(timeout);
        ClientOpenSessionTracker clientSessionTracker = new ClientOpenSessionTracker(1);
        clientSessionTracker.addTo(client);
        // Client connects
        URI wsUri = WSURI.toWebsocket(server.getURI().resolve("/ws"));
        CloseTrackingEndpoint clientSocket = new CloseTrackingEndpoint();
        Future<Session> clientConnectFuture = client.connect(clientSocket, wsUri);
        try (Session session = confirmConnection(clientSocket, clientConnectFuture)) {
            // client confirms connection via echo
            session.getRemote().sendString("too-large-message");
            clientSocket.assertReceivedCloseEvent(timeout, Matchers.is(MESSAGE_TOO_LARGE), Matchers.containsString("exceeds maximum size"));
            // client should have noticed the error
            MatcherAssert.assertThat("OnError Latch", clientSocket.errorLatch.await(2, TimeUnit.SECONDS), Matchers.is(true));
            MatcherAssert.assertThat("OnError", clientSocket.error.get(), Matchers.instanceOf(MessageTooLargeException.class));
        }
        // client triggers close event on client ws-endpoint
        clientSessionTracker.assertClosedProperly(client);
    }

    @Test
    public void testRemoteDisconnect() throws Exception {
        // Set client timeout
        final int clientTimeout = 1000;
        client.setMaxIdleTimeout(clientTimeout);
        ClientOpenSessionTracker clientSessionTracker = new ClientOpenSessionTracker(1);
        clientSessionTracker.addTo(client);
        // Client connects
        URI wsUri = WSURI.toWebsocket(server.getURI().resolve("/ws"));
        CloseTrackingEndpoint clientSocket = new CloseTrackingEndpoint();
        Future<Session> clientConnectFuture = client.connect(clientSocket, wsUri);
        try (Session ignored = confirmConnection(clientSocket, clientConnectFuture)) {
            // client confirms connection via echo
            // client sends close frame (triggering server connection abort)
            final String origCloseReason = "abort";
            getSession().close(NORMAL, origCloseReason);
            // client reads -1 (EOF)
            // client triggers close event on client ws-endpoint
            clientSocket.assertReceivedCloseEvent((clientTimeout * 2), Matchers.is(SHUTDOWN), Matchers.containsString("timeout"));
        }
        clientSessionTracker.assertClosedProperly(client);
    }

    @Test
    public void testServerNoCloseHandshake() throws Exception {
        // Set client timeout
        final int clientTimeout = 1000;
        client.setMaxIdleTimeout(clientTimeout);
        ClientOpenSessionTracker clientSessionTracker = new ClientOpenSessionTracker(1);
        clientSessionTracker.addTo(client);
        // Client connects
        URI wsUri = WSURI.toWebsocket(server.getURI().resolve("/ws"));
        CloseTrackingEndpoint clientSocket = new CloseTrackingEndpoint();
        Future<Session> clientConnectFuture = client.connect(clientSocket, wsUri);
        try (Session ignored = confirmConnection(clientSocket, clientConnectFuture)) {
            // client confirms connection via echo
            // client sends close frame
            final String origCloseReason = "sleep|5000";
            getSession().close(NORMAL, origCloseReason);
            // client close should occur
            clientSocket.assertReceivedCloseEvent((clientTimeout * 2), Matchers.is(SHUTDOWN), Matchers.containsString("timeout"));
            // client idle timeout triggers close event on client ws-endpoint
            MatcherAssert.assertThat("OnError Latch", clientSocket.errorLatch.await(2, TimeUnit.SECONDS), Matchers.is(true));
            MatcherAssert.assertThat("OnError", clientSocket.error.get(), Matchers.instanceOf(CloseException.class));
            MatcherAssert.assertThat("OnError.cause", clientSocket.error.get().getCause(), Matchers.instanceOf(TimeoutException.class));
        }
        clientSessionTracker.assertClosedProperly(client);
    }

    @Test
    public void testStopLifecycle() throws Exception {
        // Set client timeout
        final int timeout = 1000;
        client.setMaxIdleTimeout(timeout);
        int sessionCount = 3;
        ClientOpenSessionTracker clientSessionTracker = new ClientOpenSessionTracker(sessionCount);
        clientSessionTracker.addTo(client);
        URI wsUri = WSURI.toWebsocket(server.getURI().resolve("/ws"));
        List<CloseTrackingEndpoint> clientSockets = new ArrayList<>();
        // Open Multiple Clients
        for (int i = 0; i < sessionCount; i++) {
            // Client Request Upgrade
            CloseTrackingEndpoint clientSocket = new CloseTrackingEndpoint();
            clientSockets.add(clientSocket);
            Future<Session> clientConnectFuture = client.connect(clientSocket, wsUri);
            // client confirms connection via echo
            confirmConnection(clientSocket, clientConnectFuture);
        }
        Assertions.assertTimeoutPreemptively(Duration.ofSeconds(5), () -> {
            // client lifecycle stop (the meat of this test)
            client.stop();
        });
        // clients disconnect
        for (int i = 0; i < sessionCount; i++) {
            clientSockets.get(i).assertReceivedCloseEvent(timeout, Matchers.is(ABNORMAL), Matchers.containsString("Disconnected"));
        }
        // ensure all Sessions are gone. connections are gone. etc. (client and server)
        // ensure ConnectionListener onClose is called 3 times
        clientSessionTracker.assertClosedProperly(client);
    }

    @Test
    public void testWriteException() throws Exception {
        // Set client timeout
        final int timeout = 2000;
        client.setMaxIdleTimeout(timeout);
        ClientOpenSessionTracker clientSessionTracker = new ClientOpenSessionTracker(1);
        clientSessionTracker.addTo(client);
        // Client connects
        URI wsUri = WSURI.toWebsocket(server.getURI().resolve("/ws"));
        CloseTrackingEndpoint clientSocket = new CloseTrackingEndpoint();
        Future<Session> clientConnectFuture = client.connect(clientSocket, wsUri);
        // client confirms connection via echo
        confirmConnection(clientSocket, clientConnectFuture);
        // setup client endpoint for write failure (test only)
        EndPoint endp = clientSocket.getEndPoint();
        endp.shutdownOutput();
        // TODO: race condition.  Client CLOSE actions racing SERVER close actions.
        // SECONDS.sleep(1); // let server detect EOF and respond
        // client enqueue close frame
        // should result in a client write failure
        final String origCloseReason = "Normal Close from Client";
        getSession().close(NORMAL, origCloseReason);
        MatcherAssert.assertThat("OnError Latch", clientSocket.errorLatch.await(2, TimeUnit.SECONDS), Matchers.is(true));
        MatcherAssert.assertThat("OnError", clientSocket.error.get(), Matchers.instanceOf(EofException.class));
        // client triggers close event on client ws-endpoint
        // assert - close code==1006 (abnormal)
        clientSocket.assertReceivedCloseEvent(timeout, Matchers.is(ABNORMAL), Matchers.containsString("Eof"));
        clientSessionTracker.assertClosedProperly(client);
    }

    public static class ServerEndpoint implements WebSocketFrameListener , WebSocketListener {
        private static final Logger LOG = Log.getLogger(ClientCloseTest.ServerEndpoint.class);

        private Session session;

        @Override
        public void onWebSocketBinary(byte[] payload, int offset, int len) {
        }

        @Override
        public void onWebSocketText(String message) {
            try {
                if (message.equals("too-large-message")) {
                    // send extra large message
                    byte[] buf = new byte[1024 * 1024];
                    Arrays.fill(buf, ((byte) ('x')));
                    String bigmsg = new String(buf, StandardCharsets.UTF_8);
                    session.getRemote().sendString(bigmsg);
                } else {
                    // simple echo
                    session.getRemote().sendString(message);
                }
            } catch (IOException ignore) {
                ClientCloseTest.ServerEndpoint.LOG.debug(ignore);
            }
        }

        @Override
        public void onWebSocketClose(int statusCode, String reason) {
        }

        @Override
        public void onWebSocketConnect(Session session) {
            this.session = session;
        }

        @Override
        public void onWebSocketError(Throwable cause) {
            if (ClientCloseTest.ServerEndpoint.LOG.isDebugEnabled()) {
                ClientCloseTest.ServerEndpoint.LOG.debug(cause);
            }
        }

        @Override
        public void onWebSocketFrame(Frame frame) {
            if ((frame.getOpCode()) == (OpCode.CLOSE)) {
                CloseInfo closeInfo = new CloseInfo(frame);
                String reason = closeInfo.getReason();
                if (reason.equals("send-more-frames")) {
                    try {
                        session.getRemote().sendString("Hello");
                        session.getRemote().sendString("World");
                    } catch (Throwable ignore) {
                        ClientCloseTest.ServerEndpoint.LOG.debug("OOPS", ignore);
                    }
                } else
                    if (reason.equals("abort")) {
                        try {
                            TimeUnit.SECONDS.sleep(1);
                            ClientCloseTest.ServerEndpoint.LOG.info("Server aborting session abruptly");
                            session.disconnect();
                        } catch (Throwable ignore) {
                            ClientCloseTest.ServerEndpoint.LOG.ignore(ignore);
                        }
                    } else
                        if (reason.startsWith("sleep|")) {
                            int idx = reason.indexOf('|');
                            int timeMs = Integer.parseInt(reason.substring((idx + 1)));
                            try {
                                ClientCloseTest.ServerEndpoint.LOG.info("Server Sleeping for {} ms", timeMs);
                                TimeUnit.MILLISECONDS.sleep(timeMs);
                            } catch (InterruptedException ignore) {
                                ClientCloseTest.ServerEndpoint.LOG.ignore(ignore);
                            }
                        }


            }
        }
    }
}

