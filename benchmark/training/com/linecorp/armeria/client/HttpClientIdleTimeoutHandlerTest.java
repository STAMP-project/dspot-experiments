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
package com.linecorp.armeria.client;


import com.linecorp.armeria.common.HttpRequest;
import com.linecorp.armeria.internal.InboundTrafficController;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.embedded.EmbeddedChannel;
import org.junit.Assert;
import org.junit.Test;

import static com.linecorp.armeria.common.SessionProtocol.H2C;


public class HttpClientIdleTimeoutHandlerTest {
    private static final long idleTimeoutMillis = 100;

    private HttpClientIdleTimeoutHandlerTest.MockHttpSessionHandler session;

    private EmbeddedChannel ch;

    @Test
    public void testIdleTimeoutWithoutRequest() throws Exception {
        waitUntilTimeout();
        Assert.assertFalse(ch.isOpen());
    }

    @Test
    public void testIdleTimeout() throws Exception {
        writeRequest();
        readResponse();
        waitUntilTimeout();
        Assert.assertFalse(ch.isOpen());
    }

    @Test
    public void testPendingRequestExists() throws Exception {
        writeRequest();
        Thread.sleep((((HttpClientIdleTimeoutHandlerTest.idleTimeoutMillis) * 3) / 2));
        ch.runPendingTasks();
        Assert.assertTrue(ch.isOpen());
    }

    @Test
    public void testIdleTimeoutOccurredTwice() throws Exception {
        writeRequest();
        waitUntilTimeout();
        // pending request count is 1
        Assert.assertTrue(ch.isOpen());
        readResponse();
        waitUntilTimeout();
        // pending request count turns to 0
        Assert.assertFalse(ch.isOpen());
    }

    private static final class MockHttpSessionHandler extends ChannelInboundHandlerAdapter implements HttpSession {
        int unfinishedResponses;

        @Override
        public com.linecorp.armeria.common.SessionProtocol protocol() {
            return H2C;
        }

        @Override
        public boolean canSendRequest() {
            return true;
        }

        @Override
        public InboundTrafficController inboundTrafficController() {
            return InboundTrafficController.disabled();
        }

        @Override
        public int unfinishedResponses() {
            return unfinishedResponses;
        }

        @Override
        public boolean invoke(ClientRequestContext ctx, HttpRequest req, DecodedHttpResponse res) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void retryWithH1C() {
            throw new UnsupportedOperationException();
        }

        @Override
        public void deactivate() {
            throw new UnsupportedOperationException();
        }
    }
}

