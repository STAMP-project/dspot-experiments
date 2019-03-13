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
import Socks5CommandType.BIND;
import Socks5CommandType.CONNECT;
import Socks5CommandType.UDP_ASSOCIATE;
import io.netty.util.NetUtil;
import io.netty.util.internal.SocketUtils;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.net.UnknownHostException;
import java.util.Arrays;
import org.junit.Test;


public class Socks5CommandRequestDecoderTest {
    private static final InternalLogger logger = InternalLoggerFactory.getInstance(Socks5CommandRequestDecoderTest.class);

    @Test
    public void testCmdRequestDecoderIPv4() {
        String[] hosts = new String[]{ "127.0.0.1" };
        int[] ports = new int[]{ 1, 32769, 65535 };
        for (Socks5CommandType cmdType : Arrays.asList(BIND, CONNECT, UDP_ASSOCIATE)) {
            for (String host : hosts) {
                for (int port : ports) {
                    Socks5CommandRequestDecoderTest.test(cmdType, IPv4, host, port);
                }
            }
        }
    }

    @Test
    public void testCmdRequestDecoderIPv6() throws UnknownHostException {
        String[] hosts = new String[]{ NetUtil.bytesToIpAddress(SocketUtils.addressByName("::1").getAddress()) };
        int[] ports = new int[]{ 1, 32769, 65535 };
        for (Socks5CommandType cmdType : Arrays.asList(BIND, CONNECT, UDP_ASSOCIATE)) {
            for (String host : hosts) {
                for (int port : ports) {
                    Socks5CommandRequestDecoderTest.test(cmdType, IPv6, host, port);
                }
            }
        }
    }

    @Test
    public void testCmdRequestDecoderDomain() {
        String[] hosts = new String[]{ "google.com", "????.??????", "??????????.??????", "????.???????", "??????.?????????", "????????.????", "??.??", "??.??", "??????.???????", "??.???", "??.???", "???????.???????" };
        int[] ports = new int[]{ 1, 32769, 65535 };
        for (Socks5CommandType cmdType : Arrays.asList(BIND, CONNECT, UDP_ASSOCIATE)) {
            for (String host : hosts) {
                for (int port : ports) {
                    Socks5CommandRequestDecoderTest.test(cmdType, DOMAIN, host, port);
                }
            }
        }
    }
}

