/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.hadoop.net;


import NetUtils.HADOOP_WIKI;
import java.io.EOFException;
import java.io.IOException;
import java.net.BindException;
import java.net.ConnectException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.net.Socket;
import java.net.SocketException;
import java.net.URI;
import java.net.UnknownHostException;
import java.nio.charset.CharacterCodingException;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.security.KerberosAuthException;
import org.apache.hadoop.security.NetUtilsTestResolver;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class TestNetUtils {
    private static final Logger LOG = LoggerFactory.getLogger(TestNetUtils.class);

    private static final int DEST_PORT = 4040;

    private static final String DEST_PORT_NAME = Integer.toString(TestNetUtils.DEST_PORT);

    private static final int LOCAL_PORT = 8080;

    private static final String LOCAL_PORT_NAME = Integer.toString(TestNetUtils.LOCAL_PORT);

    /**
     * Some slop around expected times when making sure timeouts behave
     * as expected. We assume that they will be accurate to within
     * this threshold.
     */
    static final long TIME_FUDGE_MILLIS = 200;

    /**
     * Test that we can't accidentally connect back to the connecting socket due
     * to a quirk in the TCP spec.
     *
     * This is a regression test for HADOOP-6722.
     */
    @Test
    public void testAvoidLoopbackTcpSockets() throws Throwable {
        Configuration conf = new Configuration();
        Socket socket = NetUtils.getDefaultSocketFactory(conf).createSocket();
        socket.bind(new InetSocketAddress("127.0.0.1", 0));
        System.err.println(("local address: " + (socket.getLocalAddress())));
        System.err.println(("local port: " + (socket.getLocalPort())));
        try {
            NetUtils.connect(socket, new InetSocketAddress(socket.getLocalAddress(), socket.getLocalPort()), 20000);
            socket.close();
            Assert.fail("Should not have connected");
        } catch (ConnectException ce) {
            System.err.println(("Got exception: " + ce));
            assertInException(ce, "resulted in a loopback");
        } catch (SocketException se) {
            // Some TCP stacks will actually throw their own Invalid argument exception
            // here. This is also OK.
            assertInException(se, "Invalid argument");
        }
    }

    @Test
    public void testSocketReadTimeoutWithChannel() throws Exception {
        doSocketReadTimeoutTest(true);
    }

    @Test
    public void testSocketReadTimeoutWithoutChannel() throws Exception {
        doSocketReadTimeoutTest(false);
    }

    /**
     * Test for {
     *
     * @throws UnknownHostException
     * 		@link NetUtils#getLocalInetAddress(String)
     * @throws SocketException
     * 		
     */
    @Test
    public void testGetLocalInetAddress() throws Exception {
        Assert.assertNotNull(NetUtils.getLocalInetAddress("127.0.0.1"));
        Assert.assertNull(NetUtils.getLocalInetAddress("invalid-address-for-test"));
        Assert.assertNull(NetUtils.getLocalInetAddress(null));
    }

    @Test(expected = UnknownHostException.class)
    public void testVerifyHostnamesException() throws UnknownHostException {
        String[] names = new String[]{ "valid.host.com", "1.com", "invalid host here" };
        NetUtils.verifyHostnames(names);
    }

    @Test
    public void testVerifyHostnamesNoException() throws UnknownHostException {
        String[] names = new String[]{ "valid.host.com", "1.com" };
        NetUtils.verifyHostnames(names);
    }

    /**
     * Test for {@link NetUtils#isLocalAddress(java.net.InetAddress)}
     */
    @Test
    public void testIsLocalAddress() throws Exception {
        // Test - local host is local address
        Assert.assertTrue(NetUtils.isLocalAddress(InetAddress.getLocalHost()));
        // Test - all addresses bound network interface is local address
        Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
        if (interfaces != null) {
            // Iterate through all network interfaces
            while (interfaces.hasMoreElements()) {
                NetworkInterface i = interfaces.nextElement();
                Enumeration<InetAddress> addrs = i.getInetAddresses();
                if (addrs == null) {
                    continue;
                }
                // Iterate through all the addresses of a network interface
                while (addrs.hasMoreElements()) {
                    InetAddress addr = addrs.nextElement();
                    Assert.assertTrue(NetUtils.isLocalAddress(addr));
                } 
            } 
        }
        Assert.assertFalse(NetUtils.isLocalAddress(InetAddress.getByName("8.8.8.8")));
    }

    @Test
    public void testWrapConnectException() throws Throwable {
        IOException e = new ConnectException("failed");
        IOException wrapped = verifyExceptionClass(e, ConnectException.class);
        assertInException(wrapped, "failed");
        assertWikified(wrapped);
        assertInException(wrapped, "localhost");
        assertRemoteDetailsIncluded(wrapped);
        assertInException(wrapped, "/ConnectionRefused");
    }

    @Test
    public void testWrapBindException() throws Throwable {
        IOException e = new BindException("failed");
        IOException wrapped = verifyExceptionClass(e, BindException.class);
        assertInException(wrapped, "failed");
        assertLocalDetailsIncluded(wrapped);
        assertNotInException(wrapped, TestNetUtils.DEST_PORT_NAME);
        assertInException(wrapped, "/BindException");
    }

    @Test
    public void testWrapUnknownHostException() throws Throwable {
        IOException e = new UnknownHostException("failed");
        IOException wrapped = verifyExceptionClass(e, UnknownHostException.class);
        assertInException(wrapped, "failed");
        assertWikified(wrapped);
        assertInException(wrapped, "localhost");
        assertRemoteDetailsIncluded(wrapped);
        assertInException(wrapped, "/UnknownHost");
    }

    @Test
    public void testWrapEOFException() throws Throwable {
        IOException e = new EOFException("eof");
        IOException wrapped = verifyExceptionClass(e, EOFException.class);
        assertInException(wrapped, "eof");
        assertWikified(wrapped);
        assertInException(wrapped, "localhost");
        assertRemoteDetailsIncluded(wrapped);
        assertInException(wrapped, "/EOFException");
    }

    @Test
    public void testWrapKerbAuthException() throws Throwable {
        IOException e = new KerberosAuthException("socket timeout on connection");
        IOException wrapped = verifyExceptionClass(e, KerberosAuthException.class);
        assertInException(wrapped, "socket timeout on connection");
        assertInException(wrapped, "localhost");
        assertInException(wrapped, "DestHost:destPort ");
        assertInException(wrapped, "LocalHost:localPort");
        assertRemoteDetailsIncluded(wrapped);
        assertInException(wrapped, "KerberosAuthException");
    }

    @Test
    public void testWrapIOEWithNoStringConstructor() throws Throwable {
        IOException e = new CharacterCodingException();
        IOException wrapped = verifyExceptionClass(e, IOException.class);
        assertInException(wrapped, "Failed on local exception");
        assertNotInException(wrapped, HADOOP_WIKI);
        assertInException(wrapped, "Host Details ");
        assertRemoteDetailsIncluded(wrapped);
    }

    @Test
    public void testWrapIOEWithPrivateStringConstructor() throws Throwable {
        class TestIOException extends CharacterCodingException {
            private TestIOException(String cause) {
            }

            TestIOException() {
            }
        }
        IOException e = new TestIOException();
        IOException wrapped = verifyExceptionClass(e, IOException.class);
        assertInException(wrapped, "Failed on local exception");
        assertNotInException(wrapped, HADOOP_WIKI);
        assertInException(wrapped, "Host Details ");
        assertRemoteDetailsIncluded(wrapped);
    }

    @Test
    public void testWrapSocketException() throws Throwable {
        IOException wrapped = verifyExceptionClass(new SocketException("failed"), SocketException.class);
        assertInException(wrapped, "failed");
        assertWikified(wrapped);
        assertInException(wrapped, "localhost");
        assertRemoteDetailsIncluded(wrapped);
        assertInException(wrapped, "/SocketException");
    }

    @Test
    public void testGetConnectAddress() throws IOException {
        NetUtils.addStaticResolution("host", "127.0.0.1");
        InetSocketAddress addr = NetUtils.createSocketAddrForHost("host", 1);
        InetSocketAddress connectAddr = NetUtils.getConnectAddress(addr);
        Assert.assertEquals(addr.getHostName(), connectAddr.getHostName());
        addr = new InetSocketAddress(1);
        connectAddr = NetUtils.getConnectAddress(addr);
        Assert.assertEquals(InetAddress.getLocalHost().getHostName(), connectAddr.getHostName());
    }

    @Test
    public void testCreateSocketAddress() throws Throwable {
        InetSocketAddress addr = NetUtils.createSocketAddr("127.0.0.1:12345", 1000, "myconfig");
        Assert.assertEquals("127.0.0.1", addr.getAddress().getHostAddress());
        Assert.assertEquals(12345, addr.getPort());
        addr = NetUtils.createSocketAddr("127.0.0.1", 1000, "myconfig");
        Assert.assertEquals("127.0.0.1", addr.getAddress().getHostAddress());
        Assert.assertEquals(1000, addr.getPort());
        try {
            addr = NetUtils.createSocketAddr("127.0.0.1:blahblah", 1000, "myconfig");
            Assert.fail("Should have failed to parse bad port");
        } catch (IllegalArgumentException iae) {
            assertInException(iae, "myconfig");
        }
    }

    static NetUtilsTestResolver resolver;

    static Configuration config;

    @Test
    public void testResolverGetByExactNameUnqualified() {
        verifyGetByExactNameSearch("unknown", "unknown.");
    }

    @Test
    public void testResolverGetByExactNameUnqualifiedWithDomain() {
        verifyGetByExactNameSearch("unknown.domain", "unknown.domain.");
    }

    @Test
    public void testResolverGetByExactNameQualified() {
        verifyGetByExactNameSearch("unknown.", "unknown.");
    }

    @Test
    public void testResolverGetByExactNameQualifiedWithDomain() {
        verifyGetByExactNameSearch("unknown.domain.", "unknown.domain.");
    }

    @Test
    public void testResolverGetByNameWithSearchUnqualified() {
        String host = "unknown";
        verifyGetByNameWithSearch(host, (host + ".a.b."), (host + ".b."), (host + ".c."));
    }

    @Test
    public void testResolverGetByNameWithSearchUnqualifiedWithDomain() {
        String host = "unknown.domain";
        verifyGetByNameWithSearch(host, (host + ".a.b."), (host + ".b."), (host + ".c."));
    }

    @Test
    public void testResolverGetByNameWithSearchQualified() {
        String host = "unknown.";
        verifyGetByNameWithSearch(host, host);
    }

    @Test
    public void testResolverGetByNameWithSearchQualifiedWithDomain() {
        String host = "unknown.domain.";
        verifyGetByNameWithSearch(host, host);
    }

    @Test
    public void testResolverGetByNameQualified() {
        String host = "unknown.";
        verifyGetByName(host, host);
    }

    @Test
    public void testResolverGetByNameQualifiedWithDomain() {
        verifyGetByName("unknown.domain.", "unknown.domain.");
    }

    @Test
    public void testResolverGetByNameUnqualified() {
        String host = "unknown";
        verifyGetByName(host, (host + ".a.b."), (host + ".b."), (host + ".c."), (host + "."));
    }

    @Test
    public void testResolverGetByNameUnqualifiedWithDomain() {
        String host = "unknown.domain";
        verifyGetByName(host, (host + "."), (host + ".a.b."), (host + ".b."), (host + ".c."));
    }

    @Test
    public void testResolverUnqualified() {
        String host = "host";
        InetAddress addr = verifyResolve(host, (host + ".a.b."));
        verifyInetAddress(addr, "host.a.b", "1.1.1.1");
    }

    @Test
    public void testResolverUnqualifiedWithDomain() {
        String host = "host.a";
        InetAddress addr = verifyResolve(host, (host + "."), (host + ".a.b."), (host + ".b."));
        verifyInetAddress(addr, "host.a.b", "1.1.1.1");
    }

    @Test
    public void testResolverUnqualifedFull() {
        String host = "host.a.b";
        InetAddress addr = verifyResolve(host, (host + "."));
        verifyInetAddress(addr, host, "1.1.1.1");
    }

    @Test
    public void testResolverQualifed() {
        String host = "host.a.b.";
        InetAddress addr = verifyResolve(host, host);
        verifyInetAddress(addr, host, "1.1.1.1");
    }

    // localhost
    @Test
    public void testResolverLoopback() {
        String host = "Localhost";
        InetAddress addr = verifyResolve(host);// no lookup should occur

        verifyInetAddress(addr, "Localhost", "127.0.0.1");
    }

    @Test
    public void testResolverIP() {
        String host = "1.1.1.1";
        InetAddress addr = verifyResolve(host);// no lookup should occur for ips

        verifyInetAddress(addr, host, host);
    }

    // 
    @Test
    public void testCanonicalUriWithPort() {
        URI uri;
        uri = NetUtils.getCanonicalUri(URI.create("scheme://host:123"), 456);
        Assert.assertEquals("scheme://host.a.b:123", uri.toString());
        uri = NetUtils.getCanonicalUri(URI.create("scheme://host:123/"), 456);
        Assert.assertEquals("scheme://host.a.b:123/", uri.toString());
        uri = NetUtils.getCanonicalUri(URI.create("scheme://host:123/path"), 456);
        Assert.assertEquals("scheme://host.a.b:123/path", uri.toString());
        uri = NetUtils.getCanonicalUri(URI.create("scheme://host:123/path?q#frag"), 456);
        Assert.assertEquals("scheme://host.a.b:123/path?q#frag", uri.toString());
    }

    @Test
    public void testCanonicalUriWithDefaultPort() {
        URI uri;
        uri = NetUtils.getCanonicalUri(URI.create("scheme://host"), 123);
        Assert.assertEquals("scheme://host.a.b:123", uri.toString());
        uri = NetUtils.getCanonicalUri(URI.create("scheme://host/"), 123);
        Assert.assertEquals("scheme://host.a.b:123/", uri.toString());
        uri = NetUtils.getCanonicalUri(URI.create("scheme://host/path"), 123);
        Assert.assertEquals("scheme://host.a.b:123/path", uri.toString());
        uri = NetUtils.getCanonicalUri(URI.create("scheme://host/path?q#frag"), 123);
        Assert.assertEquals("scheme://host.a.b:123/path?q#frag", uri.toString());
    }

    @Test
    public void testCanonicalUriWithPath() {
        URI uri;
        uri = NetUtils.getCanonicalUri(URI.create("path"), 2);
        Assert.assertEquals("path", uri.toString());
        uri = NetUtils.getCanonicalUri(URI.create("/path"), 2);
        Assert.assertEquals("/path", uri.toString());
    }

    @Test
    public void testCanonicalUriWithNoAuthority() {
        URI uri;
        uri = NetUtils.getCanonicalUri(URI.create("scheme:/"), 2);
        Assert.assertEquals("scheme:/", uri.toString());
        uri = NetUtils.getCanonicalUri(URI.create("scheme:/path"), 2);
        Assert.assertEquals("scheme:/path", uri.toString());
        uri = NetUtils.getCanonicalUri(URI.create("scheme:///"), 2);
        Assert.assertEquals("scheme:///", uri.toString());
        uri = NetUtils.getCanonicalUri(URI.create("scheme:///path"), 2);
        Assert.assertEquals("scheme:///path", uri.toString());
    }

    @Test
    public void testCanonicalUriWithNoHost() {
        URI uri = NetUtils.getCanonicalUri(URI.create("scheme://:123/path"), 2);
        Assert.assertEquals("scheme://:123/path", uri.toString());
    }

    @Test
    public void testCanonicalUriWithNoPortNoDefaultPort() {
        URI uri = NetUtils.getCanonicalUri(URI.create("scheme://host/path"), (-1));
        Assert.assertEquals("scheme://host.a.b/path", uri.toString());
    }

    /**
     * Test for {@link NetUtils#normalizeHostNames}
     */
    @Test
    public void testNormalizeHostName() {
        String oneHost = "1.kanyezone.appspot.com";
        try {
            InetAddress.getByName(oneHost);
        } catch (UnknownHostException e) {
            Assume.assumeTrue(("Network not resolving " + oneHost), false);
        }
        List<String> hosts = Arrays.asList("127.0.0.1", "localhost", oneHost, "UnknownHost123");
        List<String> normalizedHosts = NetUtils.normalizeHostNames(hosts);
        String summary = (((("original [" + (StringUtils.join(hosts, ", "))) + "]") + " normalized [") + (StringUtils.join(normalizedHosts, ", "))) + "]";
        // when ipaddress is normalized, same address is expected in return
        Assert.assertEquals(summary, hosts.get(0), normalizedHosts.get(0));
        // for normalizing a resolvable hostname, resolved ipaddress is expected in return
        Assert.assertFalse(("Element 1 equal " + summary), normalizedHosts.get(1).equals(hosts.get(1)));
        Assert.assertEquals(summary, hosts.get(0), normalizedHosts.get(1));
        // this address HADOOP-8372: when normalizing a valid resolvable hostname start with numeric,
        // its ipaddress is expected to return
        Assert.assertFalse(("Element 2 equal " + summary), normalizedHosts.get(2).equals(hosts.get(2)));
        // return the same hostname after normalizing a irresolvable hostname.
        Assert.assertEquals(summary, hosts.get(3), normalizedHosts.get(3));
    }

    @Test
    public void testGetHostNameOfIP() {
        Assert.assertNull(NetUtils.getHostNameOfIP(null));
        Assert.assertNull(NetUtils.getHostNameOfIP(""));
        Assert.assertNull(NetUtils.getHostNameOfIP("crazytown"));
        Assert.assertNull(NetUtils.getHostNameOfIP("127.0.0.1:"));// no port

        Assert.assertNull(NetUtils.getHostNameOfIP("127.0.0.1:-1"));// bogus port

        Assert.assertNull(NetUtils.getHostNameOfIP("127.0.0.1:A"));// bogus port

        Assert.assertNotNull(NetUtils.getHostNameOfIP("127.0.0.1"));
        Assert.assertNotNull(NetUtils.getHostNameOfIP("127.0.0.1:1"));
    }

    @Test
    public void testTrimCreateSocketAddress() {
        Configuration conf = new Configuration();
        NetUtils.addStaticResolution("host", "127.0.0.1");
        final String defaultAddr = "host:1  ";
        InetSocketAddress addr = NetUtils.createSocketAddr(defaultAddr);
        conf.setSocketAddr("myAddress", addr);
        Assert.assertEquals(defaultAddr.trim(), NetUtils.getHostPortString(addr));
    }

    @Test
    public void testBindToLocalAddress() throws Exception {
        Assert.assertNotNull(NetUtils.bindToLocalAddress(NetUtils.getLocalInetAddress("127.0.0.1"), false));
        Assert.assertNull(NetUtils.bindToLocalAddress(NetUtils.getLocalInetAddress("127.0.0.1"), true));
    }
}

