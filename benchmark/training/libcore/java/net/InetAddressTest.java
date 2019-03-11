/**
 * Copyright (C) 2011 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package libcore.java.net;


import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.UnknownHostException;
import java.util.Collections;
import junit.framework.TestCase;
import libcore.util.SerializationTester;


public class InetAddressTest extends TestCase {
    private static final byte[] LOOPBACK6_BYTES = new byte[]{ 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1 };

    private static final String[] INVALID_IPv4_NUMERIC_ADDRESSES = new String[]{ // IPv4 addresses may not be surrounded by square brackets.
    "[127.0.0.1]", // Trailing dots are not allowed.
    // RoboVM note: getaddrinfo() in Ubuntu 12.04 happily parses this one
    // "1.2.3.4.",
    // Nor is any kind of trailing junk.
    "1.2.3.4hello", // Out of range.
    "256.2.3.4", "1.256.3.4", "1.2.256.4", "1.2.3.256", // Deprecated.
    "1.2.3", "1.2", "1", "1234", "0"// Single out the deprecated form of the ANY address.
    , // Hex.
    "0x1.0x2.0x3.0x4", "0x7f.0x00.0x00.0x01", "7f.0.0.1", // Octal.
    // RoboVM note: getaddrinfo() in Mac OS X 10.8 happily parses this one
    // "0177.00.00.01", // Historically, this would have been interpreted as 127.0.0.1.
    // Negative numbers.
    "-1.0.0.1", "1.-1.0.1", "1.0.-1.1", "1.0.0.-1" };

    public void test_parseNumericAddress() throws Exception {
        // Regular IPv4.
        TestCase.assertEquals("/1.2.3.4", parseNumericAddress("1.2.3.4").toString());
        // Regular IPv6.
        TestCase.assertEquals("/2001:4860:800d::68", parseNumericAddress("2001:4860:800d::68").toString());
        // Optional square brackets around IPv6 addresses, including mapped IPv4.
        TestCase.assertEquals("/2001:4860:800d::68", parseNumericAddress("[2001:4860:800d::68]").toString());
        TestCase.assertEquals("/127.0.0.1", parseNumericAddress("[::ffff:127.0.0.1]").toString());
        try {
            parseNumericAddress("example.com");// Not numeric.

            TestCase.fail();
        } catch (IllegalArgumentException expected) {
        }
        for (String invalid : InetAddressTest.INVALID_IPv4_NUMERIC_ADDRESSES) {
            try {
                parseNumericAddress(invalid);
                TestCase.fail(invalid);
            } catch (IllegalArgumentException expected) {
            }
        }
        // Strange special cases, for compatibility with InetAddress.getByName.
        TestCase.assertTrue(InetAddress.parseNumericAddress(null).isLoopbackAddress());
        TestCase.assertTrue(parseNumericAddress("").isLoopbackAddress());
    }

    public void test_isNumeric() throws Exception {
        TestCase.assertTrue(isNumeric("1.2.3.4"));
        TestCase.assertTrue(isNumeric("127.0.0.1"));
        TestCase.assertFalse(isNumeric("example.com"));
        for (String invalid : InetAddressTest.INVALID_IPv4_NUMERIC_ADDRESSES) {
            TestCase.assertFalse(invalid, isNumeric(invalid));
        }
    }

    public void test_isLinkLocalAddress() throws Exception {
        TestCase.assertFalse(InetAddress.getByName("127.0.0.1").isLinkLocalAddress());
        TestCase.assertTrue(InetAddress.getByName("169.254.1.2").isLinkLocalAddress());
        TestCase.assertFalse(InetAddress.getByName("fec0::").isLinkLocalAddress());
        TestCase.assertTrue(InetAddress.getByName("fe80::").isLinkLocalAddress());
    }

    public void test_isMCSiteLocalAddress() throws Exception {
        TestCase.assertFalse(InetAddress.getByName("239.254.255.255").isMCSiteLocal());
        TestCase.assertTrue(InetAddress.getByName("239.255.0.0").isMCSiteLocal());
        TestCase.assertTrue(InetAddress.getByName("239.255.255.255").isMCSiteLocal());
        TestCase.assertFalse(InetAddress.getByName("240.0.0.0").isMCSiteLocal());
        TestCase.assertFalse(InetAddress.getByName("ff06::").isMCSiteLocal());
        TestCase.assertTrue(InetAddress.getByName("ff05::").isMCSiteLocal());
        TestCase.assertTrue(InetAddress.getByName("ff15::").isMCSiteLocal());
    }

    public void test_isReachable() throws Exception {
        // http://code.google.com/p/android/issues/detail?id=20203
        String s = "aced0005737200146a6176612e6e65742e496e6574416464726573732d9b57af" + (("9fe3ebdb0200034900076164647265737349000666616d696c794c0008686f737" + "44e616d657400124c6a6176612f6c616e672f537472696e673b78704a7d9d6300") + "00000274000e7777772e676f6f676c652e636f6d");
        InetAddress inetAddress = InetAddress.getByName("www.google.com");
        new SerializationTester<InetAddress>(inetAddress, s) {
            @Override
            protected void verify(InetAddress deserialized) throws Exception {
                deserialized.isReachable(500);
                for (NetworkInterface nif : Collections.list(NetworkInterface.getNetworkInterfaces())) {
                    deserialized.isReachable(nif, 20, 500);
                }
            }

            @Override
            protected boolean equals(InetAddress a, InetAddress b) {
                return a.getHostName().equals(b.getHostName());
            }
        }.test();
    }

    public void test_isSiteLocalAddress() throws Exception {
        TestCase.assertFalse(InetAddress.getByName("144.32.32.1").isSiteLocalAddress());
        TestCase.assertTrue(InetAddress.getByName("10.0.0.1").isSiteLocalAddress());
        TestCase.assertTrue(InetAddress.getByName("172.16.0.1").isSiteLocalAddress());
        TestCase.assertFalse(InetAddress.getByName("172.32.0.1").isSiteLocalAddress());
        TestCase.assertTrue(InetAddress.getByName("192.168.0.1").isSiteLocalAddress());
        TestCase.assertFalse(InetAddress.getByName("fc00::").isSiteLocalAddress());
        TestCase.assertTrue(InetAddress.getByName("fec0::").isSiteLocalAddress());
    }

    public void test_getByName() throws Exception {
        for (String invalid : InetAddressTest.INVALID_IPv4_NUMERIC_ADDRESSES) {
            try {
                InetAddress.getByName(invalid);
                TestCase.fail(invalid);
            } catch (UnknownHostException expected) {
            }
        }
    }

    public void test_getLoopbackAddress() throws Exception {
        TestCase.assertTrue(InetAddress.getLoopbackAddress().isLoopbackAddress());
    }

    public void test_equals() throws Exception {
        InetAddress addr = InetAddress.getByName("239.191.255.255");
        TestCase.assertTrue(addr.equals(addr));
        TestCase.assertTrue(InetAddressTest.loopback6().equals(InetAddressTest.localhost6()));
        TestCase.assertFalse(addr.equals(InetAddressTest.loopback6()));
        TestCase.assertTrue(LOOPBACK.equals(LOOPBACK));
        // http://b/4328294 - the scope id isn't included when comparing Inet6Address instances.
        byte[] bs = new byte[16];
        TestCase.assertEquals(Inet6Address.getByAddress("1", bs, 1), Inet6Address.getByAddress("2", bs, 2));
    }

    public void test_getHostAddress() throws Exception {
        TestCase.assertEquals("::1", InetAddressTest.localhost6().getHostAddress());
        TestCase.assertEquals("::1", InetAddress.getByName("::1").getHostAddress());
        TestCase.assertEquals("127.0.0.1", LOOPBACK.getHostAddress());
        InetAddress aAddr = InetAddress.getByName("224.0.0.0");
        TestCase.assertEquals("224.0.0.0", aAddr.getHostAddress());
        try {
            InetAddress.getByName("1");
            TestCase.fail();
        } catch (UnknownHostException expected) {
        }
        byte[] bAddr = new byte[]{ ((byte) (254)), ((byte) (128)), ((byte) (0)), ((byte) (0)), ((byte) (0)), ((byte) (0)), ((byte) (0)), ((byte) (0)), ((byte) (2)), ((byte) (17)), ((byte) (37)), ((byte) (255)), ((byte) (254)), ((byte) (248)), ((byte) (124)), ((byte) (178)) };
        aAddr = Inet6Address.getByAddress(bAddr);
        String aString = aAddr.getHostAddress();
        TestCase.assertTrue(((aString.equals("fe80:0:0:0:211:25ff:fef8:7cb2")) || (aString.equals("fe80::211:25ff:fef8:7cb2"))));
        byte[] cAddr = new byte[]{ ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)) };
        aAddr = Inet6Address.getByAddress(cAddr);
        TestCase.assertEquals("ffff:ffff:ffff:ffff:ffff:ffff:ffff:ffff", aAddr.getHostAddress());
        byte[] dAddr = new byte[]{ ((byte) (0)), ((byte) (0)), ((byte) (0)), ((byte) (0)), ((byte) (0)), ((byte) (0)), ((byte) (0)), ((byte) (0)), ((byte) (0)), ((byte) (0)), ((byte) (0)), ((byte) (0)), ((byte) (0)), ((byte) (0)), ((byte) (0)), ((byte) (0)) };
        aAddr = Inet6Address.getByAddress(dAddr);
        aString = aAddr.getHostAddress();
        TestCase.assertTrue(((aString.equals("0:0:0:0:0:0:0:0")) || (aString.equals("::"))));
        byte[] eAddr = new byte[]{ ((byte) (0)), ((byte) (1)), ((byte) (2)), ((byte) (3)), ((byte) (4)), ((byte) (5)), ((byte) (6)), ((byte) (7)), ((byte) (8)), ((byte) (9)), ((byte) (10)), ((byte) (11)), ((byte) (12)), ((byte) (13)), ((byte) (14)), ((byte) (15)) };
        aAddr = Inet6Address.getByAddress(eAddr);
        TestCase.assertEquals("1:203:405:607:809:a0b:c0d:e0f", aAddr.getHostAddress());
        byte[] fAddr = new byte[]{ ((byte) (0)), ((byte) (16)), ((byte) (32)), ((byte) (48)), ((byte) (64)), ((byte) (80)), ((byte) (96)), ((byte) (112)), ((byte) (128)), ((byte) (144)), ((byte) (160)), ((byte) (176)), ((byte) (192)), ((byte) (208)), ((byte) (224)), ((byte) (240)) };
        aAddr = Inet6Address.getByAddress(fAddr);
        TestCase.assertEquals("10:2030:4050:6070:8090:a0b0:c0d0:e0f0", aAddr.getHostAddress());
    }

    public void test_hashCode() throws Exception {
        InetAddress addr1 = InetAddress.getByName("1.0.0.1");
        InetAddress addr2 = InetAddress.getByName("1.0.0.1");
        TestCase.assertTrue(((addr1.hashCode()) == (addr2.hashCode())));
        TestCase.assertTrue(((InetAddressTest.loopback6().hashCode()) == (InetAddressTest.localhost6().hashCode())));
    }

    public void test_toString() throws Exception {
        String[] validIPAddresses = new String[]{ "::1.2.3.4", "::", "::", "1::0", "1::", "::1", "FFFF:FFFF:FFFF:FFFF:FFFF:FFFF:FFFF:FFFF", "FFFF:FFFF:FFFF:FFFF:FFFF:FFFF:255.255.255.255", "0:0:0:0:0:0:0:0", "0:0:0:0:0:0:0.0.0.0" };
        String[] resultStrings = new String[]{ "/::1.2.3.4", "/::", "/::", "/1::", "/1::", "/::1", "/ffff:ffff:ffff:ffff:ffff:ffff:ffff:ffff", "/ffff:ffff:ffff:ffff:ffff:ffff:ffff:ffff", "/::", "/::" };
        for (int i = 0; i < (validIPAddresses.length); i++) {
            InetAddress ia = InetAddress.getByName(validIPAddresses[i]);
            String result = ia.toString();
            TestCase.assertNotNull(result);
            TestCase.assertEquals(resultStrings[i], result);
        }
    }
}

