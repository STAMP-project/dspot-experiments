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
import SocksCmdStatus.SUCCESS;
import SocksProtocolVersion.SOCKS5;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import java.net.IDN;
import java.nio.CharBuffer;
import org.junit.Assert;
import org.junit.Test;

import static SocksAddressType.DOMAIN;
import static SocksAddressType.IPv4;
import static SocksAddressType.UNKNOWN;
import static SocksCmdStatus.SUCCESS;
import static SocksCmdStatus.UNASSIGNED;


public class SocksCmdResponseTest {
    @Test
    public void testConstructorParamsAreNotNull() {
        try {
            new SocksCmdResponse(null, UNKNOWN);
        } catch (Exception e) {
            Assert.assertTrue((e instanceof NullPointerException));
        }
        try {
            new SocksCmdResponse(UNASSIGNED, null);
        } catch (Exception e) {
            Assert.assertTrue((e instanceof NullPointerException));
        }
    }

    /**
     * Verifies content of the response when domain is not specified.
     */
    @Test
    public void testEmptyDomain() {
        SocksCmdResponse socksCmdResponse = new SocksCmdResponse(SUCCESS, DOMAIN);
        Assert.assertNull(socksCmdResponse.host());
        Assert.assertEquals(0, socksCmdResponse.port());
        ByteBuf buffer = Unpooled.buffer(20);
        socksCmdResponse.encodeAsByteBuf(buffer);
        byte[] expected = new byte[]{ 5// version
        , 0// success reply
        , 0// reserved
        , 3// address type domain
        , 1// length of domain
        , 0// domain value
        , 0// port value
        , 0 };
        SocksCmdResponseTest.assertByteBufEquals(expected, buffer);
    }

    /**
     * Verifies content of the response when IPv4 address is specified.
     */
    @Test
    public void testIPv4Host() {
        SocksCmdResponse socksCmdResponse = new SocksCmdResponse(SUCCESS, IPv4, "127.0.0.1", 80);
        Assert.assertEquals("127.0.0.1", socksCmdResponse.host());
        Assert.assertEquals(80, socksCmdResponse.port());
        ByteBuf buffer = Unpooled.buffer(20);
        socksCmdResponse.encodeAsByteBuf(buffer);
        byte[] expected = new byte[]{ 5// version
        , 0// success reply
        , 0// reserved
        , 1// address type IPv4
        , 127// address 127.0.0.1
        , 0, 0, 1, 0// port
        , 80 };
        SocksCmdResponseTest.assertByteBufEquals(expected, buffer);
    }

    /**
     * Verifies that empty domain is allowed Response.
     */
    @Test
    public void testEmptyBoundAddress() {
        SocksCmdResponse socksCmdResponse = new SocksCmdResponse(SUCCESS, DOMAIN, "", 80);
        Assert.assertEquals("", socksCmdResponse.host());
        Assert.assertEquals(80, socksCmdResponse.port());
        ByteBuf buffer = Unpooled.buffer(20);
        socksCmdResponse.encodeAsByteBuf(buffer);
        byte[] expected = new byte[]{ 5// version
        , 0// success reply
        , 0// reserved
        , 3// address type domain
        , 0// domain length
        , 0// port
        , 80 };
        SocksCmdResponseTest.assertByteBufEquals(expected, buffer);
    }

    @Test
    public void testHostNotEncodedForUnknown() {
        String asciiHost = "xn--e1aybc.xn--p1ai";
        short port = 10000;
        SocksCmdResponse rs = new SocksCmdResponse(SUCCESS, UNKNOWN, asciiHost, port);
        Assert.assertEquals(asciiHost, rs.host());
        ByteBuf buffer = Unpooled.buffer(16);
        rs.encodeAsByteBuf(buffer);
        buffer.resetReaderIndex();
        Assert.assertEquals(SOCKS5.byteValue(), buffer.readByte());
        Assert.assertEquals(SUCCESS.byteValue(), buffer.readByte());
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
        SocksCmdResponse rs = new SocksCmdResponse(SUCCESS, DOMAIN, host, port);
        Assert.assertEquals(host, rs.host());
        ByteBuf buffer = Unpooled.buffer(24);
        rs.encodeAsByteBuf(buffer);
        buffer.resetReaderIndex();
        Assert.assertEquals(SOCKS5.byteValue(), buffer.readByte());
        Assert.assertEquals(SUCCESS.byteValue(), buffer.readByte());
        Assert.assertEquals(((byte) (0)), buffer.readByte());
        Assert.assertEquals(DOMAIN.byteValue(), buffer.readByte());
        Assert.assertEquals(((byte) (asciiHost.length())), buffer.readUnsignedByte());
        Assert.assertEquals(asciiHost, CharBuffer.wrap(buffer.readCharSequence(asciiHost.length(), US_ASCII)));
        Assert.assertEquals(port, buffer.readUnsignedShort());
        buffer.release();
    }

    /**
     * Verifies that Response cannot be constructed with invalid IP.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testInvalidBoundAddress() {
        new SocksCmdResponse(SUCCESS, IPv4, "127.0.0", 1000);
    }

    @Test
    public void testValidPortRange() {
        try {
            new SocksCmdResponse(SUCCESS, IPv4, "127.0.0", 0);
        } catch (Exception e) {
            Assert.assertTrue((e instanceof IllegalArgumentException));
        }
        try {
            new SocksCmdResponse(SUCCESS, IPv4, "127.0.0", 65536);
        } catch (Exception e) {
            Assert.assertTrue((e instanceof IllegalArgumentException));
        }
    }
}

