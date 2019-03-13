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
package io.netty.handler.codec.socksx.v5;


import Socks5AddressType.DOMAIN;
import Socks5AddressType.IPv4;
import Socks5AddressType.IPv6;
import Socks5CommandStatus.SUCCESS;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.util.Arrays;
import org.junit.Test;

import static Socks5CommandStatus.ADDRESS_UNSUPPORTED;
import static Socks5CommandStatus.COMMAND_UNSUPPORTED;
import static Socks5CommandStatus.CONNECTION_REFUSED;
import static Socks5CommandStatus.FAILURE;
import static Socks5CommandStatus.FORBIDDEN;
import static Socks5CommandStatus.HOST_UNREACHABLE;
import static Socks5CommandStatus.NETWORK_UNREACHABLE;
import static Socks5CommandStatus.SUCCESS;
import static Socks5CommandStatus.TTL_EXPIRED;


public class Socks5CommandResponseDecoderTest {
    private static final InternalLogger logger = InternalLoggerFactory.getInstance(Socks5CommandResponseDecoderTest.class);

    private static final Socks5CommandStatus[] STATUSES = new Socks5CommandStatus[]{ ADDRESS_UNSUPPORTED, COMMAND_UNSUPPORTED, CONNECTION_REFUSED, FAILURE, FORBIDDEN, HOST_UNREACHABLE, NETWORK_UNREACHABLE, SUCCESS, TTL_EXPIRED };

    /**
     * Verifies that sent socks messages are decoded correctly.
     */
    @Test
    public void testSocksCmdResponseDecoder() {
        for (Socks5CommandStatus cmdStatus : Socks5CommandResponseDecoderTest.STATUSES) {
            for (Socks5AddressType addressType : Arrays.asList(DOMAIN, IPv4, IPv6)) {
                Socks5CommandResponseDecoderTest.test(cmdStatus, addressType, null, 0);
            }
        }
    }

    /**
     * Verifies that invalid bound host will fail with IllegalArgumentException during encoding.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testInvalidAddress() {
        Socks5CommandResponseDecoderTest.test(SUCCESS, IPv4, "1", 80);
    }

    /**
     * Verifies that send socks messages are decoded correctly when bound host and port are set.
     */
    @Test
    public void testSocksCmdResponseDecoderIncludingHost() {
        for (Socks5CommandStatus cmdStatus : Socks5CommandResponseDecoderTest.STATUSES) {
            Socks5CommandResponseDecoderTest.test(cmdStatus, IPv4, "127.0.0.1", 80);
            Socks5CommandResponseDecoderTest.test(cmdStatus, DOMAIN, "testDomain.com", 80);
            Socks5CommandResponseDecoderTest.test(cmdStatus, IPv6, "2001:db8:85a3:42:1000:8a2e:370:7334", 80);
            Socks5CommandResponseDecoderTest.test(cmdStatus, IPv6, "1111:111:11:1::1", 80);
        }
    }
}

