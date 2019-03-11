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


import HttpHeader.SEC_WEBSOCKET_VERSION;
import Timeouts.CONNECT;
import Timeouts.CONNECT_UNIT;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import org.eclipse.jetty.websocket.api.UpgradeException;
import org.eclipse.jetty.websocket.common.test.BlockheadClient;
import org.eclipse.jetty.websocket.common.test.BlockheadClientRequest;
import org.eclipse.jetty.websocket.common.test.BlockheadConnection;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;


public class WebSocketInvalidVersionTest {
    private static BlockheadClient client;

    private static SimpleServletServer server;

    /**
     * Test the requirement of responding with an http 400 when using a Sec-WebSocket-Version that is unsupported.
     *
     * @throws Exception
     * 		on test failure
     */
    @Test
    public void testRequestVersion29() throws Exception {
        BlockheadClientRequest request = WebSocketInvalidVersionTest.client.newWsRequest(WebSocketInvalidVersionTest.server.getServerUri());
        // intentionally bad version
        request.header(SEC_WEBSOCKET_VERSION, "29");
        Future<BlockheadConnection> connFut = request.sendAsync();
        ExecutionException x = Assertions.assertThrows(ExecutionException.class, () -> {
            connFut.get(CONNECT, CONNECT_UNIT);
        });
        MatcherAssert.assertThat(x.getCause(), Matchers.instanceOf(UpgradeException.class));
        MatcherAssert.assertThat(x.getMessage(), Matchers.containsString("400 Unsupported websocket version specification"));
    }
}

