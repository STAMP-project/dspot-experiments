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
package org.eclipse.jetty.websocket.server;


import Timeouts.CONNECT;
import Timeouts.CONNECT_UNIT;
import Timeouts.POLL_EVENT;
import Timeouts.POLL_EVENT_UNIT;
import java.net.URI;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import org.eclipse.jetty.websocket.common.WebSocketFrame;
import org.eclipse.jetty.websocket.common.frames.TextFrame;
import org.eclipse.jetty.websocket.common.test.BlockheadClient;
import org.eclipse.jetty.websocket.common.test.BlockheadClientRequest;
import org.eclipse.jetty.websocket.common.test.BlockheadConnection;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;


/**
 * Testing various aspects of the server side support for WebSocket {@link org.eclipse.jetty.websocket.api.Session}
 */
public class WebSocketServerSessionTest {
    private static SimpleServletServer server;

    private static BlockheadClient client;

    @Test
    public void testDisconnect() throws Exception {
        URI uri = WebSocketServerSessionTest.server.getServerUri().resolve("/test/disconnect");
        BlockheadClientRequest request = WebSocketServerSessionTest.client.newWsRequest(uri);
        Future<BlockheadConnection> connFut = request.sendAsync();
        try (BlockheadConnection clientConn = connFut.get(CONNECT, CONNECT_UNIT)) {
            clientConn.write(new TextFrame().setPayload("harsh-disconnect"));
            clientConn.write(new TextFrame().setPayload("this shouldn't be seen by server"));
            TimeUnit.SECONDS.sleep(10);
            // clientConn.awaitDisconnect(1, TimeUnit.SECONDS);
        }
    }

    @Test
    public void testUpgradeRequestResponse() throws Exception {
        URI uri = WebSocketServerSessionTest.server.getServerUri().resolve("/test?snack=cashews&amount=handful&brand=off");
        BlockheadClientRequest request = WebSocketServerSessionTest.client.newWsRequest(uri);
        Future<BlockheadConnection> connFut = request.sendAsync();
        try (BlockheadConnection clientConn = connFut.get(CONNECT, CONNECT_UNIT)) {
            // Ask the server socket for specific parameter map info
            clientConn.write(new TextFrame().setPayload("getParameterMap|snack"));
            clientConn.write(new TextFrame().setPayload("getParameterMap|amount"));
            clientConn.write(new TextFrame().setPayload("getParameterMap|brand"));
            clientConn.write(new TextFrame().setPayload("getParameterMap|cost"));// intentionally invalid

            // Read frame (hopefully text frame)
            LinkedBlockingQueue<WebSocketFrame> frames = clientConn.getFrameQueue();
            WebSocketFrame tf = frames.poll(POLL_EVENT, POLL_EVENT_UNIT);
            MatcherAssert.assertThat("Parameter Map[snack]", tf.getPayloadAsUTF8(), Matchers.is("[cashews]"));
            tf = frames.poll(POLL_EVENT, POLL_EVENT_UNIT);
            MatcherAssert.assertThat("Parameter Map[amount]", tf.getPayloadAsUTF8(), Matchers.is("[handful]"));
            tf = frames.poll(POLL_EVENT, POLL_EVENT_UNIT);
            MatcherAssert.assertThat("Parameter Map[brand]", tf.getPayloadAsUTF8(), Matchers.is("[off]"));
            tf = frames.poll(POLL_EVENT, POLL_EVENT_UNIT);
            MatcherAssert.assertThat("Parameter Map[cost]", tf.getPayloadAsUTF8(), Matchers.is("<null>"));
        }
    }
}

