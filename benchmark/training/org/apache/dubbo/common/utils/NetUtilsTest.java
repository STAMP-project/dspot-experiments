/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.dubbo.common.utils;


import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.mockito.Mockito;


public class NetUtilsTest {
    @Test
    public void testGetRandomPort() throws Exception {
        MatcherAssert.assertThat(NetUtils.getRandomPort(), Matchers.greaterThanOrEqualTo(30000));
        MatcherAssert.assertThat(NetUtils.getRandomPort(), Matchers.greaterThanOrEqualTo(30000));
        MatcherAssert.assertThat(NetUtils.getRandomPort(), Matchers.greaterThanOrEqualTo(30000));
    }

    @Test
    public void testGetAvailablePort() throws Exception {
        MatcherAssert.assertThat(NetUtils.getAvailablePort(), Matchers.greaterThan(0));
        MatcherAssert.assertThat(NetUtils.getAvailablePort(12345), Matchers.greaterThanOrEqualTo(12345));
        MatcherAssert.assertThat(NetUtils.getAvailablePort((-1)), Matchers.greaterThanOrEqualTo(30000));
    }

    @Test
    public void testValidAddress() throws Exception {
        Assertions.assertTrue(NetUtils.isValidAddress("10.20.130.230:20880"));
        Assertions.assertFalse(NetUtils.isValidAddress("10.20.130.230"));
        Assertions.assertFalse(NetUtils.isValidAddress("10.20.130.230:666666"));
    }

    @Test
    public void testIsInvalidPort() throws Exception {
        Assertions.assertTrue(NetUtils.isInvalidPort(0));
        Assertions.assertTrue(NetUtils.isInvalidPort(65536));
        Assertions.assertFalse(NetUtils.isInvalidPort(1024));
    }

    @Test
    public void testIsLocalHost() throws Exception {
        Assertions.assertTrue(NetUtils.isLocalHost("localhost"));
        Assertions.assertTrue(NetUtils.isLocalHost("127.1.2.3"));
        Assertions.assertFalse(NetUtils.isLocalHost("128.1.2.3"));
    }

    @Test
    public void testIsAnyHost() throws Exception {
        Assertions.assertTrue(NetUtils.isAnyHost("0.0.0.0"));
        Assertions.assertFalse(NetUtils.isAnyHost("1.1.1.1"));
    }

    @Test
    public void testIsInvalidLocalHost() throws Exception {
        Assertions.assertTrue(NetUtils.isInvalidLocalHost(null));
        Assertions.assertTrue(NetUtils.isInvalidLocalHost(""));
        Assertions.assertTrue(NetUtils.isInvalidLocalHost("localhost"));
        Assertions.assertTrue(NetUtils.isInvalidLocalHost("0.0.0.0"));
        Assertions.assertTrue(NetUtils.isInvalidLocalHost("127.1.2.3"));
    }

    @Test
    public void testIsValidLocalHost() throws Exception {
        Assertions.assertTrue(NetUtils.isValidLocalHost("1.2.3.4"));
    }

    @Test
    public void testGetLocalSocketAddress() throws Exception {
        InetSocketAddress address = NetUtils.getLocalSocketAddress("localhost", 12345);
        Assertions.assertTrue(address.getAddress().isAnyLocalAddress());
        Assertions.assertEquals(address.getPort(), 12345);
        address = NetUtils.getLocalSocketAddress("dubbo-addr", 12345);
        Assertions.assertEquals(address.getHostName(), "dubbo-addr");
        Assertions.assertEquals(address.getPort(), 12345);
    }

    @Test
    public void testIsValidAddress() throws Exception {
        InetAddress address = Mockito.mock(InetAddress.class);
        Mockito.when(address.isLoopbackAddress()).thenReturn(true);
        Assertions.assertFalse(NetUtils.isValidV4Address(address));
        address = Mockito.mock(InetAddress.class);
        Mockito.when(address.getHostAddress()).thenReturn("localhost");
        Assertions.assertFalse(NetUtils.isValidV4Address(address));
        address = Mockito.mock(InetAddress.class);
        Mockito.when(address.getHostAddress()).thenReturn("0.0.0.0");
        Assertions.assertFalse(NetUtils.isValidV4Address(address));
        address = Mockito.mock(InetAddress.class);
        Mockito.when(address.getHostAddress()).thenReturn("127.0.0.1");
        Assertions.assertFalse(NetUtils.isValidV4Address(address));
        address = Mockito.mock(InetAddress.class);
        Mockito.when(address.getHostAddress()).thenReturn("1.2.3.4");
        Assertions.assertTrue(NetUtils.isValidV4Address(address));
    }

    @Test
    public void testGetLocalHost() throws Exception {
        Assertions.assertNotNull(NetUtils.getLocalHost());
    }

    @Test
    public void testGetLocalAddress() throws Exception {
        InetAddress address = NetUtils.getLocalAddress();
        Assertions.assertNotNull(address);
    }

    @Test
    public void testFilterLocalHost() throws Exception {
        Assertions.assertNull(NetUtils.filterLocalHost(null));
        Assertions.assertEquals(NetUtils.filterLocalHost(""), "");
        String host = NetUtils.filterLocalHost("dubbo://127.0.0.1:8080/foo");
        MatcherAssert.assertThat(host, Matchers.equalTo((("dubbo://" + (NetUtils.getLocalHost())) + ":8080/foo")));
        host = NetUtils.filterLocalHost("127.0.0.1:8080");
        MatcherAssert.assertThat(host, Matchers.equalTo(((NetUtils.getLocalHost()) + ":8080")));
        host = NetUtils.filterLocalHost("0.0.0.0");
        MatcherAssert.assertThat(host, Matchers.equalTo(NetUtils.getLocalHost()));
        host = NetUtils.filterLocalHost("88.88.88.88");
        MatcherAssert.assertThat(host, Matchers.equalTo(host));
    }

    @Test
    public void testGetHostName() throws Exception {
        Assertions.assertNotNull(NetUtils.getHostName("127.0.0.1"));
    }

    @Test
    public void testGetIpByHost() throws Exception {
        MatcherAssert.assertThat(NetUtils.getIpByHost("localhost"), Matchers.equalTo("127.0.0.1"));
        MatcherAssert.assertThat(NetUtils.getIpByHost("dubbo"), Matchers.equalTo("dubbo"));
    }

    @Test
    public void testToAddressString() throws Exception {
        InetAddress address = Mockito.mock(InetAddress.class);
        Mockito.when(address.getHostAddress()).thenReturn("dubbo");
        InetSocketAddress socketAddress = new InetSocketAddress(address, 1234);
        MatcherAssert.assertThat(NetUtils.toAddressString(socketAddress), Matchers.equalTo("dubbo:1234"));
    }

    @Test
    public void testToAddress() throws Exception {
        InetSocketAddress address = NetUtils.toAddress("localhost:1234");
        MatcherAssert.assertThat(address.getHostName(), Matchers.equalTo("localhost"));
        MatcherAssert.assertThat(address.getPort(), Matchers.equalTo(1234));
        address = NetUtils.toAddress("localhost");
        MatcherAssert.assertThat(address.getHostName(), Matchers.equalTo("localhost"));
        MatcherAssert.assertThat(address.getPort(), Matchers.equalTo(0));
    }

    @Test
    public void testToURL() throws Exception {
        String url = NetUtils.toURL("dubbo", "host", 1234, "foo");
        MatcherAssert.assertThat(url, Matchers.equalTo("dubbo://host:1234/foo"));
    }

    @Test
    public void testIsValidV6Address() {
        String saved = System.getProperty("java.net.preferIPv6Addresses", "false");
        System.setProperty("java.net.preferIPv6Addresses", "true");
        InetAddress address = NetUtils.getLocalAddress();
        if (address instanceof Inet6Address) {
            MatcherAssert.assertThat(NetUtils.isValidV6Address(((Inet6Address) (address))), Matchers.equalTo(true));
        }
        System.setProperty("java.net.preferIPv6Addresses", saved);
    }

    @Test
    public void testMatchIpRangeMatchWhenIpv4() throws UnknownHostException {
        Assertions.assertTrue(NetUtils.matchIpRange("*.*.*.*", "192.168.1.63", 90));
        Assertions.assertTrue(NetUtils.matchIpRange("192.168.1.*", "192.168.1.63", 90));
        Assertions.assertTrue(NetUtils.matchIpRange("192.168.1.63", "192.168.1.63", 90));
        Assertions.assertTrue(NetUtils.matchIpRange("192.168.1.1-65", "192.168.1.63", 90));
        Assertions.assertFalse(NetUtils.matchIpRange("192.168.1.1-61", "192.168.1.63", 90));
        Assertions.assertFalse(NetUtils.matchIpRange("192.168.1.62", "192.168.1.63", 90));
    }

    @Test
    public void testMatchIpRangeMatchWhenIpv6() throws UnknownHostException {
        Assertions.assertTrue(NetUtils.matchIpRange("*.*.*.*", "192.168.1.63", 90));
        Assertions.assertTrue(NetUtils.matchIpRange("234e:0:4567:0:0:0:3d:*", "234e:0:4567::3d:ff", 90));
        Assertions.assertTrue(NetUtils.matchIpRange("234e:0:4567:0:0:0:3d:ee", "234e:0:4567::3d:ee", 90));
        Assertions.assertTrue(NetUtils.matchIpRange("234e:0:4567::3d:ee", "234e:0:4567::3d:ee", 90));
        Assertions.assertTrue(NetUtils.matchIpRange("234e:0:4567:0:0:0:3d:0-ff", "234e:0:4567::3d:ee", 90));
        Assertions.assertTrue(NetUtils.matchIpRange("234e:0:4567:0:0:0:3d:0-ee", "234e:0:4567::3d:ee", 90));
        Assertions.assertFalse(NetUtils.matchIpRange("234e:0:4567:0:0:0:3d:ff", "234e:0:4567::3d:ee", 90));
        Assertions.assertFalse(NetUtils.matchIpRange("234e:0:4567:0:0:0:3d:0-ea", "234e:0:4567::3d:ee", 90));
    }

    @Test
    public void testMatchIpRangeMatchWhenIpv6Exception() throws UnknownHostException {
        IllegalArgumentException thrown = Assertions.assertThrows(IllegalArgumentException.class, () -> NetUtils.matchIpRange("234e:0:4567::3d:*", "234e:0:4567::3d:ff", 90));
        Assertions.assertTrue(thrown.getMessage().contains("If you config ip expression that contains '*'"));
        thrown = Assertions.assertThrows(IllegalArgumentException.class, () -> NetUtils.matchIpRange("234e:0:4567:3d", "234e:0:4567::3d:ff", 90));
        Assertions.assertTrue(thrown.getMessage().contains("The host is ipv6, but the pattern is not ipv6 pattern"));
        thrown = Assertions.assertThrows(IllegalArgumentException.class, () -> NetUtils.matchIpRange("192.168.1.1-65-3", "192.168.1.63", 90));
        Assertions.assertTrue(thrown.getMessage().contains("There is wrong format of ip Address"));
    }

    @Test
    public void testMatchIpRangeMatchWhenIpWrongException() throws UnknownHostException {
        UnknownHostException thrown = Assertions.assertThrows(UnknownHostException.class, () -> NetUtils.matchIpRange("192.168.1.63", "192.168.1.ff", 90));
        Assertions.assertTrue(thrown.getMessage().contains("192.168.1.ff"));
    }

    @Test
    public void testMatchIpMatch() throws UnknownHostException {
        Assertions.assertTrue(NetUtils.matchIpExpression("192.168.1.*", "192.168.1.63", 90));
        Assertions.assertTrue(NetUtils.matchIpExpression("192.168.1.192/26", "192.168.1.199", 90));
    }

    @Test
    public void testMatchIpv6WithIpPort() throws UnknownHostException {
        Assertions.assertTrue(NetUtils.matchIpRange("[234e:0:4567::3d:ee]", "234e:0:4567::3d:ee", 8090));
        Assertions.assertTrue(NetUtils.matchIpRange("[234e:0:4567:0:0:0:3d:ee]", "234e:0:4567::3d:ee", 8090));
        Assertions.assertTrue(NetUtils.matchIpRange("[234e:0:4567:0:0:0:3d:ee]:8090", "234e:0:4567::3d:ee", 8090));
        Assertions.assertTrue(NetUtils.matchIpRange("[234e:0:4567:0:0:0:3d:0-ee]:8090", "234e:0:4567::3d:ee", 8090));
        Assertions.assertTrue(NetUtils.matchIpRange("[234e:0:4567:0:0:0:3d:ee-ff]:8090", "234e:0:4567::3d:ee", 8090));
        Assertions.assertTrue(NetUtils.matchIpRange("[234e:0:4567:0:0:0:3d:*]:90", "234e:0:4567::3d:ff", 90));
        Assertions.assertFalse(NetUtils.matchIpRange("[234e:0:4567:0:0:0:3d:ee]:7289", "234e:0:4567::3d:ee", 8090));
        Assertions.assertFalse(NetUtils.matchIpRange("[234e:0:4567:0:0:0:3d:ee-ff]:8090", "234e:0:4567::3d:ee", 9090));
    }

    @Test
    public void testMatchIpv4WithIpPort() throws UnknownHostException {
        NumberFormatException thrown = Assertions.assertThrows(NumberFormatException.class, () -> NetUtils.matchIpExpression("192.168.1.192/26:90", "192.168.1.199", 90));
        Assertions.assertTrue((thrown instanceof NumberFormatException));
        Assertions.assertTrue(NetUtils.matchIpRange("*.*.*.*:90", "192.168.1.63", 90));
        Assertions.assertTrue(NetUtils.matchIpRange("192.168.1.*:90", "192.168.1.63", 90));
        Assertions.assertTrue(NetUtils.matchIpRange("192.168.1.63:90", "192.168.1.63", 90));
        Assertions.assertTrue(NetUtils.matchIpRange("192.168.1.63-65:90", "192.168.1.63", 90));
        Assertions.assertTrue(NetUtils.matchIpRange("192.168.1.1-63:90", "192.168.1.63", 90));
        Assertions.assertFalse(NetUtils.matchIpRange("*.*.*.*:80", "192.168.1.63", 90));
        Assertions.assertFalse(NetUtils.matchIpRange("192.168.1.*:80", "192.168.1.63", 90));
        Assertions.assertFalse(NetUtils.matchIpRange("192.168.1.63:80", "192.168.1.63", 90));
        Assertions.assertFalse(NetUtils.matchIpRange("192.168.1.63-65:80", "192.168.1.63", 90));
        Assertions.assertFalse(NetUtils.matchIpRange("192.168.1.1-63:80", "192.168.1.63", 90));
        Assertions.assertFalse(NetUtils.matchIpRange("192.168.1.1-61:90", "192.168.1.62", 90));
        Assertions.assertFalse(NetUtils.matchIpRange("192.168.1.62:90", "192.168.1.63", 90));
    }
}

