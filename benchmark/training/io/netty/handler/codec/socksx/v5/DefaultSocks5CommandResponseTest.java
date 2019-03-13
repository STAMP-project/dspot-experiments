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


import io.netty.buffer.ByteBuf;
import org.junit.Assert;
import org.junit.Test;

import static Socks5AddressType.DOMAIN;
import static Socks5AddressType.IPv4;
import static Socks5CommandStatus.FAILURE;
import static Socks5CommandStatus.SUCCESS;


public class DefaultSocks5CommandResponseTest {
    @Test
    public void testConstructorParamsAreNotNull() {
        try {
            new DefaultSocks5CommandResponse(null, DOMAIN);
        } catch (Exception e) {
            Assert.assertTrue((e instanceof NullPointerException));
        }
        try {
            new DefaultSocks5CommandResponse(FAILURE, null);
        } catch (Exception e) {
            Assert.assertTrue((e instanceof NullPointerException));
        }
    }

    /**
     * Verifies content of the response when domain is not specified.
     */
    @Test
    public void testEmptyDomain() {
        Socks5CommandResponse socks5CmdResponse = new DefaultSocks5CommandResponse(SUCCESS, DOMAIN);
        Assert.assertNull(socks5CmdResponse.bndAddr());
        Assert.assertEquals(0, socks5CmdResponse.bndPort());
        ByteBuf buffer = Socks5CommonTestUtils.encodeServer(socks5CmdResponse);
        byte[] expected = new byte[]{ 5// version
        , 0// success reply
        , 0// reserved
        , 3// address type domain
        , 0// length of domain
        , 0// port value
        , 0 };
        DefaultSocks5CommandResponseTest.assertByteBufEquals(expected, buffer);
        buffer.release();
    }

    /**
     * Verifies content of the response when IPv4 address is specified.
     */
    @Test
    public void testIPv4Host() {
        Socks5CommandResponse socks5CmdResponse = new DefaultSocks5CommandResponse(SUCCESS, IPv4, "127.0.0.1", 80);
        Assert.assertEquals("127.0.0.1", socks5CmdResponse.bndAddr());
        Assert.assertEquals(80, socks5CmdResponse.bndPort());
        ByteBuf buffer = Socks5CommonTestUtils.encodeServer(socks5CmdResponse);
        byte[] expected = new byte[]{ 5// version
        , 0// success reply
        , 0// reserved
        , 1// address type IPv4
        , 127// address 127.0.0.1
        , 0, 0, 1, 0// port
        , 80 };
        DefaultSocks5CommandResponseTest.assertByteBufEquals(expected, buffer);
        buffer.release();
    }

    /**
     * Verifies that empty domain is allowed Response.
     */
    @Test
    public void testEmptyBoundAddress() {
        Socks5CommandResponse socks5CmdResponse = new DefaultSocks5CommandResponse(SUCCESS, DOMAIN, "", 80);
        Assert.assertEquals("", socks5CmdResponse.bndAddr());
        Assert.assertEquals(80, socks5CmdResponse.bndPort());
        ByteBuf buffer = Socks5CommonTestUtils.encodeServer(socks5CmdResponse);
        byte[] expected = new byte[]{ 5// version
        , 0// success reply
        , 0// reserved
        , 3// address type domain
        , 0// domain length
        , 0// port
        , 80 };
        DefaultSocks5CommandResponseTest.assertByteBufEquals(expected, buffer);
        buffer.release();
    }

    /**
     * Verifies that Response cannot be constructed with invalid IP.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testInvalidBoundAddress() {
        new DefaultSocks5CommandResponse(SUCCESS, IPv4, "127.0.0", 1000);
    }

    @Test
    public void testValidPortRange() {
        try {
            new DefaultSocks5CommandResponse(SUCCESS, IPv4, "127.0.0", 0);
        } catch (Exception e) {
            Assert.assertTrue((e instanceof IllegalArgumentException));
        }
        try {
            new DefaultSocks5CommandResponse(SUCCESS, IPv4, "127.0.0", 65536);
        } catch (Exception e) {
            Assert.assertTrue((e instanceof IllegalArgumentException));
        }
    }
}

