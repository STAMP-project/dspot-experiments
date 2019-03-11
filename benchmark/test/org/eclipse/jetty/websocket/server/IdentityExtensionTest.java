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
import org.junit.jupiter.api.Test;


public class IdentityExtensionTest {
    private static SimpleServletServer server;

    private static BlockheadClient client;

    @Test
    public void testIdentityExtension() throws Exception {
        BlockheadClientRequest request = IdentityExtensionTest.client.newWsRequest(IdentityExtensionTest.server.getServerUri());
        request.header(SEC_WEBSOCKET_EXTENSIONS, "identity;param=0");
        request.header(SEC_WEBSOCKET_EXTENSIONS, "identity;param=1, identity ; param = '2' ; other = ' some = value '");
        request.header(SEC_WEBSOCKET_SUBPROTOCOL, "onConnect");
        request.idleTimeout(1, TimeUnit.SECONDS);
        Future<BlockheadConnection> connFut = request.sendAsync();
        try (BlockheadConnection clientConn = connFut.get(CONNECT, CONNECT_UNIT)) {
            HttpFields responseHeaders = clientConn.getUpgradeResponseHeaders();
            HttpField extensionHeader = responseHeaders.getField(SEC_WEBSOCKET_EXTENSIONS);
            MatcherAssert.assertThat("Response", extensionHeader.getValue(), Matchers.containsString("identity"));
            clientConn.write(new TextFrame().setPayload("Hello"));
            LinkedBlockingQueue<WebSocketFrame> frames = clientConn.getFrameQueue();
            WebSocketFrame frame = frames.poll(POLL_EVENT, POLL_EVENT_UNIT);
            MatcherAssert.assertThat("TEXT.payload", frame.getPayloadAsUTF8(), Matchers.is("Hello"));
        }
    }
}

