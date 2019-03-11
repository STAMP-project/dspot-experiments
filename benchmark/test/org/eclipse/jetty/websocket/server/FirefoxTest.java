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


import HttpHeader.CONNECTION;
import Timeouts.CONNECT;
import Timeouts.CONNECT_UNIT;
import Timeouts.POLL_EVENT;
import Timeouts.POLL_EVENT_UNIT;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import org.eclipse.jetty.websocket.common.WebSocketFrame;
import org.eclipse.jetty.websocket.common.frames.TextFrame;
import org.eclipse.jetty.websocket.common.test.BlockheadClient;
import org.eclipse.jetty.websocket.common.test.BlockheadClientRequest;
import org.eclipse.jetty.websocket.common.test.BlockheadConnection;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;


public class FirefoxTest {
    private static BlockheadClient client;

    private static SimpleServletServer server;

    @Test
    public void testConnectionKeepAlive() throws Exception {
        BlockheadClientRequest request = FirefoxTest.client.newWsRequest(FirefoxTest.server.getServerUri());
        // Odd Connection Header value seen in older Firefox versions
        request.header(CONNECTION, "keep-alive, Upgrade");
        Future<BlockheadConnection> connFut = request.sendAsync();
        try (BlockheadConnection conn = connFut.get(CONNECT, CONNECT_UNIT)) {
            // Generate text frame
            String msg = "this is an echo ... cho ... ho ... o";
            conn.write(new TextFrame().setPayload(msg));
            // Read frame (hopefully text frame)
            LinkedBlockingQueue<WebSocketFrame> frames = conn.getFrameQueue();
            WebSocketFrame tf = frames.poll(POLL_EVENT, POLL_EVENT_UNIT);
            MatcherAssert.assertThat("Text Frame.status code", tf.getPayloadAsUTF8(), Matchers.is(msg));
        }
    }
}

