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


import CharsetUtil.US_ASCII;
import SocksAddressType.DOMAIN;
import SocksAddressType.UNKNOWN;
import SocksCmdType.BIND;
import SocksProtocolVersion.SOCKS5;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import java.net.IDN;
import java.nio.CharBuffer;
import org.junit.Assert;
import org.junit.Test;

import static SocksAddressType.DOMAIN;
import static SocksAddressType.IPv4;
import static SocksAddressType.IPv6;
import static SocksAddressType.UNKNOWN;
import static SocksCmdType.BIND;


public class SocksCmdRequestTest {
    @Test
    public void testConstructorParamsAreNotNull() {
        try {
            new SocksCmdRequest(null, UNKNOWN, "", 1);
        } catch (Exception e) {
            Assert.assertTrue((e instanceof NullPointerException));
        }
        try {
            new SocksCmdRequest(SocksCmdType.UNKNOWN, null, "", 1);
        } catch (Exception e) {
            Assert.assertTrue((e instanceof NullPointerException));
        }
        try {
            new SocksCmdRequest(SocksCmdType.UNKNOWN, UNKNOWN, null, 1);
        } catch (Exception e) {
            Assert.assertTrue((e instanceof NullPointerException));
        }
    }

    @Test
    public void testIPv4CorrectAddress() {
        try {
            new SocksCmdRequest(BIND, IPv4, "54.54.1111.253", 1);
        } catch (Exception e) {
            Assert.assertTrue((e instanceof IllegalArgumentException));
        }
    }

    @Test
    public void testIPv6CorrectAddress() {
        try {
            new SocksCmdRequest(BIND, IPv6, "xxx:xxx:xxx", 1);
        } catch (Exception e) {
            Assert.assertTrue((e instanceof IllegalArgumentException));
        }
    }

    @Test
    public void testIDNNotExceeds255CharsLimit() {
        try {
            new SocksCmdRequest(BIND, DOMAIN, ("??????????.????????????????.????????????????.????????????????.??????" + (("??????????.????????????????.????????????????.????????????????.??????" + "??????????.????????????????.????????????????.????????????????.??????") + "??????????.????????????????.????????????????.????????????????.??????")), 1);
        } catch (Exception e) {
            Assert.assertTrue((e instanceof IllegalArgumentException));
        }
    }

    @Test
    public void testHostNotEncodedForUnknown() {
        String asciiHost = "xn--e1aybc.xn--p1ai";
        short port = 10000;
        SocksCmdRequest rq = new SocksCmdRequest(BIND, UNKNOWN, asciiHost, port);
        Assert.assertEquals(asciiHost, rq.host());
        ByteBuf buffer = Unpooled.buffer(16);
        rq.encodeAsByteBuf(buffer);
        buffer.resetReaderIndex();
        Assert.assertEquals(SOCKS5.byteValue(), buffer.readByte());
        Assert.assertEquals(BIND.byteValue(), buffer.readByte());
        Assert.assertEquals(((byte) (0)), buffer.readByte());
        Assert.assertEquals(UNKNOWN.byteValue(), buffer.readByte());
        Assert.assertFalse(buffer.isReadable());
        buffer.release();
    }

    @Test
    public void testIDNEncodeToAsciiForDomain() {
        String host = "????.??";
        CharBuffer asciiHost = CharBuffer.wrap(IDN.toASCII(host));
        short port = 10000;
        SocksCmdRequest rq = new SocksCmdRequest(BIND, DOMAIN, host, port);
        Assert.assertEquals(host, rq.host());
        ByteBuf buffer = Unpooled.buffer(24);
        rq.encodeAsByteBuf(buffer);
        buffer.resetReaderIndex();
        Assert.assertEquals(SOCKS5.byteValue(), buffer.readByte());
        Assert.assertEquals(BIND.byteValue(), buffer.readByte());
        Assert.assertEquals(((byte) (0)), buffer.readByte());
        Assert.assertEquals(DOMAIN.byteValue(), buffer.readByte());
        Assert.assertEquals(((byte) (asciiHost.length())), buffer.readUnsignedByte());
        Assert.assertEquals(asciiHost, CharBuffer.wrap(buffer.readCharSequence(asciiHost.length(), US_ASCII)));
        Assert.assertEquals(port, buffer.readUnsignedShort());
        buffer.release();
    }

    @Test
    public void testValidPortRange() {
        try {
            new SocksCmdRequest(BIND, DOMAIN, "??????????.?????????????", 0);
        } catch (Exception e) {
            Assert.assertTrue((e instanceof IllegalArgumentException));
        }
        try {
            new SocksCmdRequest(BIND, DOMAIN, "??????????.?????????????", 65536);
        } catch (Exception e) {
            Assert.assertTrue((e instanceof IllegalArgumentException));
        }
    }
}

