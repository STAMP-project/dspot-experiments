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
package org.eclipse.jetty.cdi.websocket.cdiapp;


import StatusCode.NORMAL;
import java.net.URI;
import java.util.concurrent.TimeUnit;
import org.eclipse.jetty.cdi.websocket.CheckSocket;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.util.log.Log;
import org.eclipse.jetty.util.log.Logger;
import org.eclipse.jetty.websocket.client.WebSocketClient;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;


public class CdiAppTest {
    private static final Logger LOG = Log.getLogger(CdiAppTest.class);

    private static Server server;

    private static URI serverWebsocketURI;

    @Test
    public void testWebSocketActivated() throws Exception {
        WebSocketClient client = new WebSocketClient();
        try {
            client.start();
            CheckSocket socket = new CheckSocket();
            client.connect(socket, CdiAppTest.serverWebsocketURI.resolve("/echo"));
            socket.awaitOpen(2, TimeUnit.SECONDS);
            socket.sendText("Hello");
            socket.close(NORMAL, "Test complete");
            socket.awaitClose(2, TimeUnit.SECONDS);
            MatcherAssert.assertThat("Messages received", socket.getTextMessages().size(), Matchers.is(1));
            MatcherAssert.assertThat("Message[0]", socket.getTextMessages().poll(), Matchers.is("Hello"));
        } finally {
            client.stop();
        }
    }

    @Test
    public void testWebSocket_Info_FieldPresence() throws Exception {
        WebSocketClient client = new WebSocketClient();
        try {
            client.start();
            CheckSocket socket = new CheckSocket();
            client.connect(socket, CdiAppTest.serverWebsocketURI.resolve("/cdi-info"));
            socket.awaitOpen(2, TimeUnit.SECONDS);
            socket.sendText("info");
            socket.close(NORMAL, "Test complete");
            socket.awaitClose(2, TimeUnit.SECONDS);
            MatcherAssert.assertThat("Messages received", socket.getTextMessages().size(), Matchers.is(1));
            String response = socket.getTextMessages().poll();
            System.err.println(response);
            MatcherAssert.assertThat("Message[0]", response, Matchers.allOf(Matchers.containsString("websocketSession is PRESENT"), Matchers.containsString("httpSession is PRESENT"), Matchers.containsString("servletContext is PRESENT")));
        } finally {
            client.stop();
        }
    }

    @Test
    public void testWebSocket_Info_DataFromCdi() throws Exception {
        WebSocketClient client = new WebSocketClient();
        try {
            client.start();
            CheckSocket socket = new CheckSocket();
            client.connect(socket, CdiAppTest.serverWebsocketURI.resolve("/cdi-info"));
            socket.awaitOpen(2, TimeUnit.SECONDS);
            socket.sendText("data|stuff");
            socket.close(NORMAL, "Test complete");
            socket.awaitClose(2, TimeUnit.SECONDS);
            MatcherAssert.assertThat("Messages received", socket.getTextMessages().size(), Matchers.is(2));
            String response = socket.getTextMessages().poll();
            System.out.println(("[0]" + response));
            MatcherAssert.assertThat("Message[0]", response, Matchers.containsString("Hello there stuff"));
            System.out.println(("[1]" + (socket.getTextMessages().poll())));
        } finally {
            client.stop();
        }
    }
}

