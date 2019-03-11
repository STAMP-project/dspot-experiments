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


import HttpHeader.SEC_WEBSOCKET_EXTENSIONS;
import HttpHeader.SEC_WEBSOCKET_SUBPROTOCOL;
import Timeouts.CONNECT;
import Timeouts.CONNECT_UNIT;
import Timeouts.POLL_EVENT;
import Timeouts.POLL_EVENT_UNIT;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import org.eclipse.jetty.http.HttpField;
import org.eclipse.jetty.http.HttpFields;
import org.eclipse.jetty.websocket.common.WebSocketFrame;
import org.eclipse.jetty.websocket.common.frames.TextFrame;
import org.eclipse.jetty.websocket.common.test.BlockheadClient;
import org.eclipse.jetty.websocket.common.test.BlockheadClientRequest;
import org.eclipse.jetty.websocket.common.test.BlockheadConnection;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.Test;


public class FragmentExtensionTest {
    private static SimpleServletServer server;

    private static BlockheadClient client;

    @Test
    public void testFragmentExtension() throws Exception {
        Assumptions.assumeTrue(FragmentExtensionTest.server.getWebSocketServletFactory().getExtensionFactory().isAvailable("fragment"), "Server has fragment registered");
        Assumptions.assumeTrue(FragmentExtensionTest.client.getExtensionFactory().isAvailable("fragment"), "Client has fragment registered");
        int fragSize = 4;
        BlockheadClientRequest request = FragmentExtensionTest.client.newWsRequest(FragmentExtensionTest.server.getServerUri());
        request.header(SEC_WEBSOCKET_EXTENSIONS, ("fragment;maxLength=" + fragSize));
        request.header(SEC_WEBSOCKET_SUBPROTOCOL, "onConnect");
        request.idleTimeout(1, TimeUnit.SECONDS);
        Future<BlockheadConnection> connFut = request.sendAsync();
        try (BlockheadConnection clientConn = connFut.get(CONNECT, CONNECT_UNIT)) {
            // Make sure the read times out if there are problems with the implementation
            HttpFields responseHeaders = clientConn.getUpgradeResponseHeaders();
            HttpField extensionHeader = responseHeaders.getField(SEC_WEBSOCKET_EXTENSIONS);
            MatcherAssert.assertThat("Response", extensionHeader.getValue(), Matchers.containsString("fragment"));
            String msg = "Sent as a long message that should be split";
            clientConn.write(new TextFrame().setPayload(msg));
            String[] parts = split(msg, fragSize);
            LinkedBlockingQueue<WebSocketFrame> frames = clientConn.getFrameQueue();
            for (int i = 0; i < (parts.length); i++) {
                WebSocketFrame frame = frames.poll(POLL_EVENT, POLL_EVENT_UNIT);
                MatcherAssert.assertThat((("text[" + i) + "].payload"), frame.getPayloadAsUTF8(), Matchers.is(parts[i]));
            }
        }
    }
}

