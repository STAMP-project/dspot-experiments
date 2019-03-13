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
package org.jboss.netty.util;


import java.util.HashMap;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;


public class NetUtilTest {
    private static final Map<String, byte[]> validIpV4Hosts = new HashMap<String, byte[]>() {
        private static final long serialVersionUID = 2629792739366724032L;

        {
            put("192.168.1.0", new byte[]{ ((byte) (192)), ((byte) (168)), 1, 0 });
            put("10.255.255.254", new byte[]{ 10, ((byte) (255)), ((byte) (255)), ((byte) (254)) });
            put("172.18.5.4", new byte[]{ ((byte) (172)), 18, 5, 4 });
            put("0.0.0.0", new byte[]{ 0, 0, 0, 0 });
            put("127.0.0.1", new byte[]{ 127, 0, 0, 1 });
        }
    };

    private static final Map<String, byte[]> invalidIpV4Hosts = new HashMap<String, byte[]>() {
        private static final long serialVersionUID = 1299215199895717282L;

        {
            put("1.256.3.4", null);
            put("256.0.0.1", null);
            put("1.1.1.1.1", null);
        }
    };

    private static final Map<String, byte[]> validIpV6Hosts = new HashMap<String, byte[]>() {
        private static final long serialVersionUID = 3999763170377573184L;

        {
            put("::ffff:5.6.7.8", new byte[]{ 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, ((byte) (255)), ((byte) (255)), 5, 6, 7, 8 });
            put("fdf8:f53b:82e4::53", new byte[]{ ((byte) (253)), ((byte) (248)), ((byte) (245)), 59, ((byte) (130)), ((byte) (228)), 0, 0, 0, 0, 0, 0, 0, 0, 0, 83 });
            put("fe80::200:5aee:feaa:20a2", new byte[]{ ((byte) (254)), ((byte) (128)), 0, 0, 0, 0, 0, 0, 2, 0, 90, ((byte) (238)), ((byte) (254)), ((byte) (170)), 32, ((byte) (162)) });
            put("2001::1", new byte[]{ 32, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1 });
            put("2001:0000:4136:e378:8000:63bf:3fff:fdd2", new byte[]{ 32, 1, 0, 0, 65, 54, ((byte) (227)), 120, ((byte) (128)), 0, 99, ((byte) (191)), 63, ((byte) (255)), ((byte) (253)), ((byte) (210)) });
            put("2001:0002:6c::430", new byte[]{ 32, 1, 0, 2, 0, 108, 0, 0, 0, 0, 0, 0, 0, 0, 4, 48 });
            put("2001:10:240:ab::a", new byte[]{ 32, 1, 0, 16, 2, 64, 0, ((byte) (171)), 0, 0, 0, 0, 0, 0, 0, 10 });
            put("2002:cb0a:3cdd:1::1", new byte[]{ 32, 2, ((byte) (203)), 10, 60, ((byte) (221)), 0, 1, 0, 0, 0, 0, 0, 0, 0, 1 });
            put("2001:db8:8:4::2", new byte[]{ 32, 1, 13, ((byte) (184)), 0, 8, 0, 4, 0, 0, 0, 0, 0, 0, 0, 2 });
            put("ff01:0:0:0:0:0:0:2", new byte[]{ ((byte) (255)), 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 2 });
            put("[fdf8:f53b:82e4::53]", new byte[]{ ((byte) (253)), ((byte) (248)), ((byte) (245)), 59, ((byte) (130)), ((byte) (228)), 0, 0, 0, 0, 0, 0, 0, 0, 0, 83 });
            put("[fe80::200:5aee:feaa:20a2]", new byte[]{ ((byte) (254)), ((byte) (128)), 0, 0, 0, 0, 0, 0, 2, 0, 90, ((byte) (238)), ((byte) (254)), ((byte) (170)), 32, ((byte) (162)) });
            put("[2001::1]", new byte[]{ 32, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1 });
            put("[2001:0000:4136:e378:8000:63bf:3fff:fdd2]", new byte[]{ 32, 1, 0, 0, 65, 54, ((byte) (227)), 120, ((byte) (128)), 0, 99, ((byte) (191)), 63, ((byte) (255)), ((byte) (253)), ((byte) (210)) });
            put("0:1:2:3:4:5:6:789a", new byte[]{ 0, 0, 0, 1, 0, 2, 0, 3, 0, 4, 0, 5, 0, 6, 120, ((byte) (154)) });
            put("0:1:2:3::f", new byte[]{ 0, 0, 0, 1, 0, 2, 0, 3, 0, 0, 0, 0, 0, 0, 0, 15 });
            put("0:0:0:0:0:0:10.0.0.1", new byte[]{ 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 10, 0, 0, 1 });
            put("::ffff:192.168.0.1", new byte[]{ 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, ((byte) (255)), ((byte) (255)), ((byte) (192)), ((byte) (168)), 0, 1 });
        }
    };

    private static final Map<String, byte[]> invalidIpV6Hosts = new HashMap<String, byte[]>() {
        private static final long serialVersionUID = -5870810805409009696L;

        {
            // Test method with garbage.
            put("Obvious Garbage", null);
            // Test method with preferred style, too many :
            put("0:1:2:3:4:5:6:7:8", null);
            // Test method with preferred style, not enough :
            put("0:1:2:3:4:5:6", null);
            // Test method with preferred style, bad digits.
            put("0:1:2:3:4:5:6:x", null);
            // Test method with preferred style, adjacent :
            put("0:1:2:3:4:5:6::7", null);
            // Test method with preferred style, too many digits.
            put("0:1:2:3:4:5:6:789abcdef", null);
            // Test method with compressed style, bad digits.
            put("0:1:2:3::x", null);
            // Test method with compressed style, too many adjacent :
            put("0:1:2:::3", null);
            // Test method with compressed style, too many digits.
            put("0:1:2:3::abcde", null);
            // Test method with preferred style, too many :
            put("0:1:2:3:4:5:6:7:8", null);
            // Test method with compressed style, not enough :
            put("0:1", null);
            // Test method with ipv4 style, bad ipv6 digits.
            put("0:0:0:0:0:x:10.0.0.1", null);
            // Test method with ipv4 style, bad ipv4 digits.
            put("0:0:0:0:0:0:10.0.0.x", null);
            // Test method with ipv4 style, adjacent :
            put("0:0:0:0:0::0:10.0.0.1", null);
            // Test method with ipv4 style, too many ipv6 digits.
            put("0:0:0:0:0:00000:10.0.0.1", null);
            // Test method with ipv4 style, too many :
            put("0:0:0:0:0:0:0:10.0.0.1", null);
            // Test method with ipv4 style, not enough :
            put("0:0:0:0:0:10.0.0.1", null);
            // Test method with ipv4 style, too many .
            put("0:0:0:0:0:0:10.0.0.0.1", null);
            // Test method with ipv4 style, not enough .
            put("0:0:0:0:0:0:10.0.1", null);
            // Test method with ipv4 style, adjacent .
            put("0:0:0:0:0:0:10..0.0.1", null);
            // Test method with compressed ipv4 style, bad ipv6 digits.
            put("::fffx:192.168.0.1", null);
            // Test method with compressed ipv4 style, bad ipv4 digits.
            put("::ffff:192.168.0.x", null);
            // Test method with compressed ipv4 style, too many adjacent :
            put(":::ffff:192.168.0.1", null);
            // Test method with compressed ipv4 style, too many ipv6 digits.
            put("::fffff:192.168.0.1", null);
            // Test method with compressed ipv4 style, too many ipv4 digits.
            put("::ffff:1923.168.0.1", null);
            // Test method with compressed ipv4 style, not enough :
            put(":ffff:192.168.0.1", null);
            // Test method with compressed ipv4 style, too many .
            put("::ffff:192.168.0.1.2", null);
            // Test method with compressed ipv4 style, not enough .
            put("::ffff:192.168.0", null);
            // Test method with compressed ipv4 style, adjacent .
            put("::ffff:192.168..0.1", null);
            // Test method, garbage.
            put("absolute, and utter garbage", null);
            // Test method, bad ipv6 digits.
            put("x:0:0:0:0:0:10.0.0.1", null);
            // Test method, bad ipv4 digits.
            put("0:0:0:0:0:0:x.0.0.1", null);
            // Test method, too many ipv6 digits.
            put("00000:0:0:0:0:0:10.0.0.1", null);
            // Test method, too many ipv4 digits.
            put("0:0:0:0:0:0:10.0.0.1000", null);
            // Test method, too many :
            put("0:0:0:0:0:0:0:10.0.0.1", null);
            // Test method, not enough :
            put("0:0:0:0:0:10.0.0.1", null);
            // Test method, too many .
            put("0:0:0:0:0:0:10.0.0.0.1", null);
            // Test method, not enough .
            put("0:0:0:0:0:0:10.0.1", null);
            // Test method, adjacent .
            put("0:0:0:0:0:0:10.0.0..1", null);
        }
    };

    @Test
    public void testIsValidIpV4Address() {
        for (String host : NetUtilTest.validIpV4Hosts.keySet()) {
            Assert.assertTrue(NetUtil.isValidIpV4Address(host));
        }
        for (String host : NetUtilTest.invalidIpV4Hosts.keySet()) {
            Assert.assertFalse(NetUtil.isValidIpV4Address(host));
        }
    }

    @Test
    public void testIsValidIpV6Address() {
        for (String host : NetUtilTest.validIpV6Hosts.keySet()) {
            Assert.assertTrue(NetUtil.isValidIpV6Address(host));
        }
        for (String host : NetUtilTest.invalidIpV6Hosts.keySet()) {
            Assert.assertFalse(NetUtil.isValidIpV6Address(host));
        }
    }

    @Test
    public void testCreateByteArrayFromIpAddressString() {
        for (Map.Entry<String, byte[]> stringEntry : NetUtilTest.validIpV4Hosts.entrySet()) {
            Assert.assertArrayEquals(stringEntry.getValue(), NetUtil.createByteArrayFromIpAddressString(stringEntry.getKey()));
        }
        for (Map.Entry<String, byte[]> stringEntry : NetUtilTest.invalidIpV4Hosts.entrySet()) {
            Assert.assertArrayEquals(stringEntry.getValue(), NetUtil.createByteArrayFromIpAddressString(stringEntry.getKey()));
        }
        for (Map.Entry<String, byte[]> stringEntry : NetUtilTest.validIpV6Hosts.entrySet()) {
            Assert.assertArrayEquals(stringEntry.getValue(), NetUtil.createByteArrayFromIpAddressString(stringEntry.getKey()));
        }
        for (Map.Entry<String, byte[]> stringEntry : NetUtilTest.invalidIpV6Hosts.entrySet()) {
            Assert.assertArrayEquals(stringEntry.getValue(), NetUtil.createByteArrayFromIpAddressString(stringEntry.getKey()));
        }
    }
}

