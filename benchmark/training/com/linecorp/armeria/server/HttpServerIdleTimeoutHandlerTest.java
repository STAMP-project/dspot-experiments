/**
 * Copyright 2016 LINE Corporation
 *
 * LINE Corporation licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *   https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package com.linecorp.armeria.server;


import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.embedded.EmbeddedChannel;
import org.junit.Assert;
import org.junit.Test;

import static com.linecorp.armeria.common.SessionProtocol.H2C;


public class HttpServerIdleTimeoutHandlerTest {
    private static final long idleTimeoutMillis = 100;

    private HttpServerIdleTimeoutHandlerTest.MockHttpServerHandler server;

    private EmbeddedChannel ch;

    @Test
    public void testIdleTimeoutWithoutRequest() throws Exception {
        waitUntilTimeout();
        Assert.assertFalse(ch.isOpen());
    }

    @Test
    public void testIdleTimeout() throws Exception {
        readRequest();
        writeResponse();
        waitUntilTimeout();
        Assert.assertFalse(ch.isOpen());
    }

    @Test
    public void testPendingRequestExists() throws Exception {
        readRequest();
        Thread.sleep(HttpServerIdleTimeoutHandlerTest.idleTimeoutMillis);
        ch.runPendingTasks();
        Assert.assertTrue(ch.isOpen());
    }

    @Test
    public void testIdleTimeoutOccurredTwice() throws Exception {
        readRequest();
        waitUntilTimeout();
        // pending request count is 2
        Assert.assertTrue(ch.isOpen());
        writeResponse();
        // pending request count turns to 0
        waitUntilTimeout();
        Assert.assertFalse(ch.isOpen());
    }

    private static final class MockHttpServerHandler extends ChannelInboundHandlerAdapter implements HttpServer {
        int unfinishedRequests;

        @Override
        public com.linecorp.armeria.common.SessionProtocol protocol() {
            return H2C;
        }

        @Override
        public int unfinishedRequests() {
            return unfinishedRequests;
        }
    }
}

