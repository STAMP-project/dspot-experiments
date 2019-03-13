/**
 * Copyright 2018 NAVER Corp.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.navercorp.pinpoint.bootstrap.plugin.util;


import java.net.InetAddress;
import java.net.InetSocketAddress;
import org.junit.Assume;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 *
 *
 * @author HyunGil Jeong
 */
public class SocketAddressUtilsTest {
    private static final String VALID_HOST = "naver.com";

    private static final String INVALID_HOST = "pinpoint-none-existent-domain-hopefully";

    private static final Logger LOGGER = LoggerFactory.getLogger(SocketAddressUtilsTest.class);

    private static SocketAddressUtilsTest.NameServiceReplacer NAME_SERVICE_REPLACER;

    @Test
    public void fromValidHostName() {
        String hostName = SocketAddressUtilsTest.VALID_HOST;
        InetSocketAddress socketAddress = new InetSocketAddress(hostName, 80);
        SocketAddressUtilsTest.assertResolved(socketAddress);
        final String expectedHostName = hostName;
        final String expectedAddress = socketAddress.getAddress().getHostAddress();
        SocketAddressUtilsTest.verify(socketAddress, expectedHostName, expectedAddress);
    }

    @Test
    public void fromValidHostNameUnresolved() {
        String hostName = SocketAddressUtilsTest.VALID_HOST;
        InetSocketAddress socketAddress = InetSocketAddress.createUnresolved(hostName, 80);
        SocketAddressUtilsTest.assertUnresolved(socketAddress);
        final String expectedHostName = hostName;
        final String expectedAddress = hostName;
        SocketAddressUtilsTest.verify(socketAddress, expectedHostName, expectedAddress);
    }

    @Test
    public void fromInvalidHostName() {
        String hostName = SocketAddressUtilsTest.INVALID_HOST;
        InetSocketAddress socketAddress = new InetSocketAddress(hostName, 80);
        SocketAddressUtilsTest.assertUnresolved(socketAddress);
        final String expectedHostName = hostName;
        final String expectedAddress = hostName;
        SocketAddressUtilsTest.verify(socketAddress, expectedHostName, expectedAddress);
    }

    @Test
    public void fromAddress() {
        String hostName = SocketAddressUtilsTest.VALID_HOST;
        InetAddress inetAddress = SocketAddressUtilsTest.createAddressFromHostName(hostName);
        Assume.assumeNotNull(inetAddress);
        String address = inetAddress.getHostAddress();
        InetSocketAddress socketAddress = new InetSocketAddress(address, 80);
        SocketAddressUtilsTest.assertResolved(socketAddress);
        final String expectedHostName = address;
        final String expectedAddress = address;
        SocketAddressUtilsTest.verify(socketAddress, expectedHostName, expectedAddress);
    }

    @Test
    public void fromLookedUpAddress() {
        String hostName = SocketAddressUtilsTest.VALID_HOST;
        InetAddress inetAddress = SocketAddressUtilsTest.createAddressFromHostName(hostName);
        Assume.assumeNotNull(inetAddress);
        String address = inetAddress.getHostAddress();
        InetSocketAddress socketAddress = new InetSocketAddress(address, 80);
        SocketAddressUtilsTest.assertResolved(socketAddress);
        String expectedHostName = socketAddress.getHostName();// lookup host name

        String expectedAddress = address;
        SocketAddressUtilsTest.verify(socketAddress, expectedHostName, expectedAddress);
    }

    @Test
    public void fromLocalAddress() {
        InetAddress inetAddress = SocketAddressUtilsTest.createLocalAddress();
        Assume.assumeNotNull(inetAddress);
        String address = inetAddress.getHostAddress();
        InetSocketAddress socketAddress = new InetSocketAddress(address, 80);
        SocketAddressUtilsTest.assertResolved(socketAddress);
        final String expectedHostName = address;
        final String expectedAddress = address;
        SocketAddressUtilsTest.verify(socketAddress, expectedHostName, expectedAddress);
    }

    private interface NameServiceReplacer {
        void replace();

        void rollback();
    }
}

