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
package org.jboss.netty.handler.codec.socks;


import SocksMessage.AddressType.DOMAIN;
import SocksMessage.AddressType.IPv4;
import SocksMessage.AddressType.IPv6;
import SocksMessage.AddressType.UNKNOWN;
import SocksMessage.CmdType;
import org.jboss.netty.logging.InternalLogger;
import org.jboss.netty.logging.InternalLoggerFactory;
import org.junit.Test;
import sun.net.util.IPAddressUtil;


public class SocksCmdRequestDecoderTest {
    private static final InternalLogger logger = InternalLoggerFactory.getInstance(SocksCmdRequestDecoderTest.class);

    @Test
    public void testCmdRequestDecoderIPv4() throws Exception {
        String[] hosts = new String[]{ "127.0.0.1" };
        int[] ports = new int[]{ 0, 32769, 65535 };
        for (SocksMessage.CmdType cmdType : CmdType.values()) {
            for (String host : hosts) {
                for (int port : ports) {
                    SocksCmdRequestDecoderTest.testSocksCmdRequestDecoderWithDifferentParams(cmdType, IPv4, host, port);
                }
            }
        }
    }

    @Test
    public void testCmdRequestDecoderIPv6() throws Exception {
        String[] hosts = new String[]{ SocksCommonUtils.ipv6toStr(IPAddressUtil.textToNumericFormatV6("::1")) };
        int[] ports = new int[]{ 0, 32769, 65535 };
        for (SocksMessage.CmdType cmdType : CmdType.values()) {
            for (String host : hosts) {
                for (int port : ports) {
                    SocksCmdRequestDecoderTest.testSocksCmdRequestDecoderWithDifferentParams(cmdType, IPv6, host, port);
                }
            }
        }
    }

    @Test
    public void testCmdRequestDecoderDomain() throws Exception {
        String[] hosts = new String[]{ "google.com", "????.??????", "??????????.??????", "????.???????", "??????.?????????", "????????.????", "??.??", "??.??", "??????.???????", "??.???", "??.???", "???????.???????" };
        int[] ports = new int[]{ 0, 32769, 65535 };
        for (SocksMessage.CmdType cmdType : CmdType.values()) {
            for (String host : hosts) {
                for (int port : ports) {
                    SocksCmdRequestDecoderTest.testSocksCmdRequestDecoderWithDifferentParams(cmdType, DOMAIN, host, port);
                }
            }
        }
    }

    @Test
    public void testCmdRequestDecoderUnknown() throws Exception {
        String host = "google.com";
        int port = 80;
        for (SocksMessage.CmdType cmdType : CmdType.values()) {
            SocksCmdRequestDecoderTest.testSocksCmdRequestDecoderWithDifferentParams(cmdType, UNKNOWN, host, port);
        }
    }
}

