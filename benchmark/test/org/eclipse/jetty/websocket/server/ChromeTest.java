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


public class ChromeTest {
    private static BlockheadClient client;

    private static SimpleServletServer server;

    @Test
    public void testUpgradeWithWebkitDeflateExtension() throws Exception {
        Assumptions.assumeTrue(ChromeTest.server.getWebSocketServletFactory().getExtensionFactory().isAvailable("x-webkit-deflate-frame"), "Server has x-webkit-deflate-frame registered");
        Assumptions.assumeTrue(ChromeTest.client.getExtensionFactory().isAvailable("x-webkit-deflate-frame"), "Client has x-webkit-deflate-frame registered");
        BlockheadClientRequest request = ChromeTest.client.newWsRequest(ChromeTest.server.getServerUri());
        request.header(SEC_WEBSOCKET_EXTENSIONS, "x-webkit-deflate-frame");
        request.header(SEC_WEBSOCKET_SUBPROTOCOL, "chat");
        Future<BlockheadConnection> connFut = request.sendAsync();
        try (BlockheadConnection clientConn = connFut.get(CONNECT, CONNECT_UNIT)) {
            HttpFields responseFields = clientConn.getUpgradeResponseHeaders();
            HttpField extensionField = responseFields.getField(SEC_WEBSOCKET_EXTENSIONS);
            MatcherAssert.assertThat("Response", extensionField.getValue(), Matchers.containsString("x-webkit-deflate-frame"));
            // Generate text frame
            String msg = "this is an echo ... cho ... ho ... o";
            clientConn.write(new TextFrame().setPayload(msg));
            // Read frame (hopefully text frame)
            LinkedBlockingQueue<WebSocketFrame> frames = clientConn.getFrameQueue();
            WebSocketFrame tf = frames.poll(POLL_EVENT, POLL_EVENT_UNIT);
            MatcherAssert.assertThat("Text Frame.status code", tf.getPayloadAsUTF8(), Matchers.is(msg));
        }
    }
}

