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
package io.netty.handler.codec.socks;


import SocksAddressType.DOMAIN;
import SocksAddressType.IPv4;
import SocksAddressType.IPv6;
import SocksCmdStatus.SUCCESS;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import org.junit.Test;


public class SocksCmdResponseDecoderTest {
    private static final InternalLogger logger = InternalLoggerFactory.getInstance(SocksCmdResponseDecoderTest.class);

    /**
     * Verifies that sent socks messages are decoded correctly.
     */
    @Test
    public void testSocksCmdResponseDecoder() {
        for (SocksCmdStatus cmdStatus : SocksCmdStatus.values()) {
            for (SocksAddressType addressType : SocksAddressType.values()) {
                SocksCmdResponseDecoderTest.testSocksCmdResponseDecoderWithDifferentParams(cmdStatus, addressType, null, 0);
            }
        }
    }

    /**
     * Verifies that invalid bound host will fail with IllegalArgumentException during encoding.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testInvalidAddress() {
        SocksCmdResponseDecoderTest.testSocksCmdResponseDecoderWithDifferentParams(SUCCESS, IPv4, "1", 80);
    }

    /**
     * Verifies that send socks messages are decoded correctly when bound host and port are set.
     */
    @Test
    public void testSocksCmdResponseDecoderIncludingHost() {
        for (SocksCmdStatus cmdStatus : SocksCmdStatus.values()) {
            SocksCmdResponseDecoderTest.testSocksCmdResponseDecoderWithDifferentParams(cmdStatus, IPv4, "127.0.0.1", 80);
            SocksCmdResponseDecoderTest.testSocksCmdResponseDecoderWithDifferentParams(cmdStatus, DOMAIN, "testDomain.com", 80);
            SocksCmdResponseDecoderTest.testSocksCmdResponseDecoderWithDifferentParams(cmdStatus, IPv6, "2001:db8:85a3:42:1000:8a2e:370:7334", 80);
            SocksCmdResponseDecoderTest.testSocksCmdResponseDecoderWithDifferentParams(cmdStatus, IPv6, "1111:111:11:1:0:0:0:1", 80);
        }
    }
}

