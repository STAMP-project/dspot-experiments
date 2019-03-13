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


import SocksMessage.AddressType;
import SocksMessage.CmdType;
import org.junit.Assert;
import org.junit.Test;


public class SocksCmdRequestTest {
    @Test
    public void testConstructorParamsAreNotNull() {
        try {
            new SocksCmdRequest(null, AddressType.UNKNOWN, "", 0);
        } catch (Exception e) {
            Assert.assertTrue((e instanceof NullPointerException));
        }
        try {
            new SocksCmdRequest(CmdType.UNKNOWN, null, "", 0);
        } catch (Exception e) {
            Assert.assertTrue((e instanceof NullPointerException));
        }
        try {
            new SocksCmdRequest(CmdType.UNKNOWN, AddressType.UNKNOWN, null, 0);
        } catch (Exception e) {
            Assert.assertTrue((e instanceof NullPointerException));
        }
    }

    @Test
    public void testIPv4CorrectAddress() {
        try {
            new SocksCmdRequest(CmdType.BIND, AddressType.IPv4, "54.54.1111.253", 0);
        } catch (Exception e) {
            Assert.assertTrue((e instanceof IllegalArgumentException));
        }
    }

    @Test
    public void testIPv6CorrectAddress() {
        try {
            new SocksCmdRequest(CmdType.BIND, AddressType.IPv6, "xxx:xxx:xxx", 0);
        } catch (Exception e) {
            Assert.assertTrue((e instanceof IllegalArgumentException));
        }
    }

    @Test
    public void testIDNNotExceeds255CharsLimit() {
        try {
            new SocksCmdRequest(CmdType.BIND, AddressType.DOMAIN, ("??????????.????????????????.????????????????.????????????????.??????" + (("??????????.????????????????.????????????????.????????????????.??????" + "??????????.????????????????.????????????????.????????????????.??????") + "??????????.????????????????.????????????????.????????????????.??????")), 0);
        } catch (Exception e) {
            Assert.assertTrue((e instanceof IllegalArgumentException));
        }
    }

    @Test
    public void testValidPortRange() {
        try {
            new SocksCmdRequest(CmdType.BIND, AddressType.DOMAIN, "??????????.?????????????", (-1));
        } catch (Exception e) {
            Assert.assertTrue((e instanceof IllegalArgumentException));
        }
        try {
            new SocksCmdRequest(CmdType.BIND, AddressType.DOMAIN, "??????????.?????????????", 65536);
        } catch (Exception e) {
            Assert.assertTrue((e instanceof IllegalArgumentException));
        }
    }
}

