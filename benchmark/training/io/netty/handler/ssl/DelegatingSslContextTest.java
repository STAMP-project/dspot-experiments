/**
 * Copyright 2017 The Netty Project
 *
 * The Netty Project licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package io.netty.handler.ssl;


import UnpooledByteBufAllocator.DEFAULT;
import javax.net.ssl.SSLEngine;
import org.junit.Assert;
import org.junit.Test;

import static SslUtils.PROTOCOL_TLS_V1_1;


public class DelegatingSslContextTest {
    private static final String[] EXPECTED_PROTOCOLS = new String[]{ PROTOCOL_TLS_V1_1 };

    @Test
    public void testInitEngineOnNewEngine() throws Exception {
        SslContext delegating = DelegatingSslContextTest.newDelegatingSslContext();
        SSLEngine engine = delegating.newEngine(DEFAULT);
        Assert.assertArrayEquals(DelegatingSslContextTest.EXPECTED_PROTOCOLS, engine.getEnabledProtocols());
        engine = delegating.newEngine(DEFAULT, "localhost", 9090);
        Assert.assertArrayEquals(DelegatingSslContextTest.EXPECTED_PROTOCOLS, engine.getEnabledProtocols());
    }

    @Test
    public void testInitEngineOnNewSslHandler() throws Exception {
        SslContext delegating = DelegatingSslContextTest.newDelegatingSslContext();
        SslHandler handler = delegating.newHandler(DEFAULT);
        Assert.assertArrayEquals(DelegatingSslContextTest.EXPECTED_PROTOCOLS, handler.engine().getEnabledProtocols());
        handler = delegating.newHandler(DEFAULT, "localhost", 9090);
        Assert.assertArrayEquals(DelegatingSslContextTest.EXPECTED_PROTOCOLS, handler.engine().getEnabledProtocols());
    }
}

