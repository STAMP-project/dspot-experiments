/**
 * Copyright 2012 The Netty Project
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
package io.netty.handler.codec.socksx.v4;


import Socks4CommandStatus.IDENTD_AUTH_FAILURE;
import Socks4CommandStatus.IDENTD_UNREACHABLE;
import Socks4CommandStatus.REJECTED_OR_FAILED;
import Socks4CommandStatus.SUCCESS;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class Socks4ClientDecoderTest {
    private static final Logger logger = LoggerFactory.getLogger(Socks4ClientDecoderTest.class);

    /**
     * Verifies that sent socks messages are decoded correctly.
     */
    @Test
    public void testSocksCmdResponseDecoder() {
        Socks4ClientDecoderTest.test(IDENTD_AUTH_FAILURE, null, 0);
        Socks4ClientDecoderTest.test(IDENTD_UNREACHABLE, null, 0);
        Socks4ClientDecoderTest.test(REJECTED_OR_FAILED, null, 0);
        Socks4ClientDecoderTest.test(SUCCESS, null, 0);
    }
}

