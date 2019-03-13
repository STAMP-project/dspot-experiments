/**
 * Copyright (C) 2012 Square, Inc.
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
package okhttp3.internal.connection;


import ConnectionSpec.CLEARTEXT;
import ConnectionSpec.COMPATIBLE_TLS;
import ConnectionSpec.MODERN_TLS;
import Protocol.HTTP_1_1;
import RouteSelector.Selection;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.ProxySelector;
import java.net.SocketAddress;
import java.net.URI;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;
import javax.net.SocketFactory;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSocketFactory;
import okhttp3.Address;
import okhttp3.Authenticator;
import okhttp3.ConnectionSpec;
import okhttp3.EventListener;
import okhttp3.FakeDns;
import okhttp3.Protocol;
import okhttp3.Route;
import okhttp3.internal.Util;
import okhttp3.internal.http.RecordingProxySelector;
import okhttp3.tls.HandshakeCertificates;
import org.junit.Assert;
import org.junit.Test;

import static java.net.Proxy.Type.HTTP;


public final class RouteSelectorTest {
    public final List<ConnectionSpec> connectionSpecs = Util.immutableList(MODERN_TLS, COMPATIBLE_TLS, CLEARTEXT);

    private static final int proxyAPort = 1001;

    private static final String proxyAHost = "proxya";

    private static final Proxy proxyA = new Proxy(HTTP, InetSocketAddress.createUnresolved(RouteSelectorTest.proxyAHost, RouteSelectorTest.proxyAPort));

    private static final int proxyBPort = 1002;

    private static final String proxyBHost = "proxyb";

    private static final Proxy proxyB = new Proxy(HTTP, InetSocketAddress.createUnresolved(RouteSelectorTest.proxyBHost, RouteSelectorTest.proxyBPort));

    private String uriHost = "hosta";

    private int uriPort = 1003;

    private SocketFactory socketFactory;

    private final HandshakeCertificates handshakeCertificates = localhost();

    private final SSLSocketFactory sslSocketFactory = handshakeCertificates.sslSocketFactory();

    private HostnameVerifier hostnameVerifier;

    private final Authenticator authenticator = Authenticator.NONE;

    private final List<Protocol> protocols = Arrays.asList(HTTP_1_1);

    private final FakeDns dns = new FakeDns();

    private final RecordingProxySelector proxySelector = new RecordingProxySelector();

    private RouteDatabase routeDatabase = new RouteDatabase();

    @Test
    public void singleRoute() throws Exception {
        Address address = httpAddress();
        RouteSelector routeSelector = new RouteSelector(address, routeDatabase, null, EventListener.NONE);
        Assert.assertTrue(routeSelector.hasNext());
        dns.set(uriHost, dns.allocate(1));
        RouteSelector.Selection selection = routeSelector.next();
        assertRoute(selection.next(), address, Proxy.NO_PROXY, dns.lookup(uriHost, 0), uriPort);
        dns.assertRequests(uriHost);
        Assert.assertFalse(selection.hasNext());
        try {
            selection.next();
            Assert.fail();
        } catch (NoSuchElementException expected) {
        }
        Assert.assertFalse(routeSelector.hasNext());
        try {
            routeSelector.next();
            Assert.fail();
        } catch (NoSuchElementException expected) {
        }
    }

    @Test
    public void singleRouteReturnsFailedRoute() throws Exception {
        Address address = httpAddress();
        RouteSelector routeSelector = new RouteSelector(address, routeDatabase, null, EventListener.NONE);
        Assert.assertTrue(routeSelector.hasNext());
        dns.set(uriHost, dns.allocate(1));
        RouteSelector.Selection selection = routeSelector.next();
        Route route = selection.next();
        routeDatabase.failed(route);
        routeSelector = new RouteSelector(address, routeDatabase, null, EventListener.NONE);
        selection = routeSelector.next();
        assertRoute(selection.next(), address, Proxy.NO_PROXY, dns.lookup(uriHost, 0), uriPort);
        Assert.assertFalse(selection.hasNext());
        try {
            selection.next();
            Assert.fail();
        } catch (NoSuchElementException expected) {
        }
        Assert.assertFalse(routeSelector.hasNext());
        try {
            routeSelector.next();
            Assert.fail();
        } catch (NoSuchElementException expected) {
        }
    }

    @Test
    public void explicitProxyTriesThatProxysAddressesOnly() throws Exception {
        Address address = new Address(uriHost, uriPort, dns, socketFactory, null, null, null, authenticator, RouteSelectorTest.proxyA, protocols, connectionSpecs, proxySelector);
        RouteSelector routeSelector = new RouteSelector(address, routeDatabase, null, EventListener.NONE);
        Assert.assertTrue(routeSelector.hasNext());
        dns.set(RouteSelectorTest.proxyAHost, dns.allocate(2));
        RouteSelector.Selection selection = routeSelector.next();
        assertRoute(selection.next(), address, RouteSelectorTest.proxyA, dns.lookup(RouteSelectorTest.proxyAHost, 0), RouteSelectorTest.proxyAPort);
        assertRoute(selection.next(), address, RouteSelectorTest.proxyA, dns.lookup(RouteSelectorTest.proxyAHost, 1), RouteSelectorTest.proxyAPort);
        Assert.assertFalse(selection.hasNext());
        Assert.assertFalse(routeSelector.hasNext());
        dns.assertRequests(RouteSelectorTest.proxyAHost);
        proxySelector.assertRequests();// No proxy selector requests!

    }

    @Test
    public void explicitDirectProxy() throws Exception {
        Address address = new Address(uriHost, uriPort, dns, socketFactory, null, null, null, authenticator, Proxy.NO_PROXY, protocols, connectionSpecs, proxySelector);
        RouteSelector routeSelector = new RouteSelector(address, routeDatabase, null, EventListener.NONE);
        Assert.assertTrue(routeSelector.hasNext());
        dns.set(uriHost, dns.allocate(2));
        RouteSelector.Selection selection = routeSelector.next();
        assertRoute(selection.next(), address, Proxy.NO_PROXY, dns.lookup(uriHost, 0), uriPort);
        assertRoute(selection.next(), address, Proxy.NO_PROXY, dns.lookup(uriHost, 1), uriPort);
        Assert.assertFalse(selection.hasNext());
        Assert.assertFalse(routeSelector.hasNext());
        dns.assertRequests(uriHost);
        proxySelector.assertRequests();// No proxy selector requests!

    }

    @Test
    public void proxySelectorReturnsNull() throws Exception {
        ProxySelector nullProxySelector = new ProxySelector() {
            @Override
            public List<Proxy> select(URI uri) {
                Assert.assertEquals(uriHost, uri.getHost());
                return null;
            }

            @Override
            public void connectFailed(URI uri, SocketAddress socketAddress, IOException e) {
                throw new AssertionError();
            }
        };
        Address address = new Address(uriHost, uriPort, dns, socketFactory, null, null, null, authenticator, null, protocols, connectionSpecs, nullProxySelector);
        RouteSelector routeSelector = new RouteSelector(address, routeDatabase, null, EventListener.NONE);
        Assert.assertTrue(routeSelector.hasNext());
        dns.set(uriHost, dns.allocate(1));
        RouteSelector.Selection selection = routeSelector.next();
        assertRoute(selection.next(), address, Proxy.NO_PROXY, dns.lookup(uriHost, 0), uriPort);
        dns.assertRequests(uriHost);
        Assert.assertFalse(selection.hasNext());
        Assert.assertFalse(routeSelector.hasNext());
    }

    @Test
    public void proxySelectorReturnsNoProxies() throws Exception {
        Address address = httpAddress();
        RouteSelector routeSelector = new RouteSelector(address, routeDatabase, null, EventListener.NONE);
        Assert.assertTrue(routeSelector.hasNext());
        dns.set(uriHost, dns.allocate(2));
        RouteSelector.Selection selection = routeSelector.next();
        assertRoute(selection.next(), address, Proxy.NO_PROXY, dns.lookup(uriHost, 0), uriPort);
        assertRoute(selection.next(), address, Proxy.NO_PROXY, dns.lookup(uriHost, 1), uriPort);
        Assert.assertFalse(selection.hasNext());
        Assert.assertFalse(routeSelector.hasNext());
        dns.assertRequests(uriHost);
        proxySelector.assertRequests(address.url().uri());
    }

    @Test
    public void proxySelectorReturnsMultipleProxies() throws Exception {
        Address address = httpAddress();
        proxySelector.proxies.add(RouteSelectorTest.proxyA);
        proxySelector.proxies.add(RouteSelectorTest.proxyB);
        RouteSelector routeSelector = new RouteSelector(address, routeDatabase, null, EventListener.NONE);
        proxySelector.assertRequests(address.url().uri());
        // First try the IP addresses of the first proxy, in sequence.
        Assert.assertTrue(routeSelector.hasNext());
        dns.set(RouteSelectorTest.proxyAHost, dns.allocate(2));
        RouteSelector.Selection selection1 = routeSelector.next();
        assertRoute(selection1.next(), address, RouteSelectorTest.proxyA, dns.lookup(RouteSelectorTest.proxyAHost, 0), RouteSelectorTest.proxyAPort);
        assertRoute(selection1.next(), address, RouteSelectorTest.proxyA, dns.lookup(RouteSelectorTest.proxyAHost, 1), RouteSelectorTest.proxyAPort);
        dns.assertRequests(RouteSelectorTest.proxyAHost);
        Assert.assertFalse(selection1.hasNext());
        // Next try the IP address of the second proxy.
        Assert.assertTrue(routeSelector.hasNext());
        dns.set(RouteSelectorTest.proxyBHost, dns.allocate(1));
        RouteSelector.Selection selection2 = routeSelector.next();
        assertRoute(selection2.next(), address, RouteSelectorTest.proxyB, dns.lookup(RouteSelectorTest.proxyBHost, 0), RouteSelectorTest.proxyBPort);
        dns.assertRequests(RouteSelectorTest.proxyBHost);
        Assert.assertFalse(selection2.hasNext());
        // No more proxies to try.
        Assert.assertFalse(routeSelector.hasNext());
    }

    @Test
    public void proxySelectorDirectConnectionsAreSkipped() throws Exception {
        Address address = httpAddress();
        proxySelector.proxies.add(Proxy.NO_PROXY);
        RouteSelector routeSelector = new RouteSelector(address, routeDatabase, null, EventListener.NONE);
        proxySelector.assertRequests(address.url().uri());
        // Only the origin server will be attempted.
        Assert.assertTrue(routeSelector.hasNext());
        dns.set(uriHost, dns.allocate(1));
        RouteSelector.Selection selection = routeSelector.next();
        assertRoute(selection.next(), address, Proxy.NO_PROXY, dns.lookup(uriHost, 0), uriPort);
        dns.assertRequests(uriHost);
        Assert.assertFalse(selection.hasNext());
        Assert.assertFalse(routeSelector.hasNext());
    }

    @Test
    public void proxyDnsFailureContinuesToNextProxy() throws Exception {
        Address address = httpAddress();
        proxySelector.proxies.add(RouteSelectorTest.proxyA);
        proxySelector.proxies.add(RouteSelectorTest.proxyB);
        proxySelector.proxies.add(RouteSelectorTest.proxyA);
        RouteSelector routeSelector = new RouteSelector(address, routeDatabase, null, EventListener.NONE);
        proxySelector.assertRequests(address.url().uri());
        Assert.assertTrue(routeSelector.hasNext());
        dns.set(RouteSelectorTest.proxyAHost, dns.allocate(1));
        RouteSelector.Selection selection1 = routeSelector.next();
        assertRoute(selection1.next(), address, RouteSelectorTest.proxyA, dns.lookup(RouteSelectorTest.proxyAHost, 0), RouteSelectorTest.proxyAPort);
        dns.assertRequests(RouteSelectorTest.proxyAHost);
        Assert.assertFalse(selection1.hasNext());
        Assert.assertTrue(routeSelector.hasNext());
        dns.clear(RouteSelectorTest.proxyBHost);
        try {
            routeSelector.next();
            Assert.fail();
        } catch (UnknownHostException expected) {
        }
        dns.assertRequests(RouteSelectorTest.proxyBHost);
        Assert.assertTrue(routeSelector.hasNext());
        dns.set(RouteSelectorTest.proxyAHost, dns.allocate(1));
        RouteSelector.Selection selection2 = routeSelector.next();
        assertRoute(selection2.next(), address, RouteSelectorTest.proxyA, dns.lookup(RouteSelectorTest.proxyAHost, 0), RouteSelectorTest.proxyAPort);
        dns.assertRequests(RouteSelectorTest.proxyAHost);
        Assert.assertFalse(selection2.hasNext());
        Assert.assertFalse(routeSelector.hasNext());
    }

    @Test
    public void multipleProxiesMultipleInetAddressesMultipleConfigurations() throws Exception {
        Address address = httpsAddress();
        proxySelector.proxies.add(RouteSelectorTest.proxyA);
        proxySelector.proxies.add(RouteSelectorTest.proxyB);
        RouteSelector routeSelector = new RouteSelector(address, routeDatabase, null, EventListener.NONE);
        // Proxy A
        dns.set(RouteSelectorTest.proxyAHost, dns.allocate(2));
        RouteSelector.Selection selection1 = routeSelector.next();
        assertRoute(selection1.next(), address, RouteSelectorTest.proxyA, dns.lookup(RouteSelectorTest.proxyAHost, 0), RouteSelectorTest.proxyAPort);
        dns.assertRequests(RouteSelectorTest.proxyAHost);
        assertRoute(selection1.next(), address, RouteSelectorTest.proxyA, dns.lookup(RouteSelectorTest.proxyAHost, 1), RouteSelectorTest.proxyAPort);
        Assert.assertFalse(selection1.hasNext());
        // Proxy B
        dns.set(RouteSelectorTest.proxyBHost, dns.allocate(2));
        RouteSelector.Selection selection2 = routeSelector.next();
        assertRoute(selection2.next(), address, RouteSelectorTest.proxyB, dns.lookup(RouteSelectorTest.proxyBHost, 0), RouteSelectorTest.proxyBPort);
        dns.assertRequests(RouteSelectorTest.proxyBHost);
        assertRoute(selection2.next(), address, RouteSelectorTest.proxyB, dns.lookup(RouteSelectorTest.proxyBHost, 1), RouteSelectorTest.proxyBPort);
        Assert.assertFalse(selection2.hasNext());
        // No more proxies to attempt.
        Assert.assertFalse(routeSelector.hasNext());
    }

    @Test
    public void failedRouteWithSingleProxy() throws Exception {
        Address address = httpsAddress();
        RouteSelector routeSelector = new RouteSelector(address, routeDatabase, null, EventListener.NONE);
        final int numberOfAddresses = 2;
        dns.set(uriHost, dns.allocate(numberOfAddresses));
        // Extract the regular sequence of routes from selector.
        RouteSelector.Selection selection1 = routeSelector.next();
        List<Route> regularRoutes = selection1.getAll();
        // Check that we do indeed have more than one route.
        Assert.assertEquals(numberOfAddresses, regularRoutes.size());
        // Add first regular route as failed.
        routeDatabase.failed(regularRoutes.get(0));
        // Reset selector
        routeSelector = new RouteSelector(address, routeDatabase, null, EventListener.NONE);
        // The first selection prioritizes the non-failed routes.
        RouteSelector.Selection selection2 = routeSelector.next();
        Assert.assertEquals(regularRoutes.get(1), selection2.next());
        Assert.assertFalse(selection2.hasNext());
        // The second selection will contain all failed routes.
        RouteSelector.Selection selection3 = routeSelector.next();
        Assert.assertEquals(regularRoutes.get(0), selection3.next());
        Assert.assertFalse(selection3.hasNext());
        Assert.assertFalse(routeSelector.hasNext());
    }

    @Test
    public void failedRouteWithMultipleProxies() throws IOException {
        Address address = httpsAddress();
        proxySelector.proxies.add(RouteSelectorTest.proxyA);
        proxySelector.proxies.add(RouteSelectorTest.proxyB);
        RouteSelector routeSelector = new RouteSelector(address, routeDatabase, null, EventListener.NONE);
        dns.set(RouteSelectorTest.proxyAHost, dns.allocate(1));
        dns.set(RouteSelectorTest.proxyBHost, dns.allocate(1));
        // Mark the ProxyA route as failed.
        RouteSelector.Selection selection = routeSelector.next();
        dns.assertRequests(RouteSelectorTest.proxyAHost);
        Route route = selection.next();
        assertRoute(route, address, RouteSelectorTest.proxyA, dns.lookup(RouteSelectorTest.proxyAHost, 0), RouteSelectorTest.proxyAPort);
        routeDatabase.failed(route);
        routeSelector = new RouteSelector(address, routeDatabase, null, EventListener.NONE);
        // Confirm we enumerate both proxies, giving preference to the route from ProxyB.
        RouteSelector.Selection selection2 = routeSelector.next();
        dns.assertRequests(RouteSelectorTest.proxyAHost, RouteSelectorTest.proxyBHost);
        assertRoute(selection2.next(), address, RouteSelectorTest.proxyB, dns.lookup(RouteSelectorTest.proxyBHost, 0), RouteSelectorTest.proxyBPort);
        Assert.assertFalse(selection2.hasNext());
        // Confirm the last selection contains the postponed route from ProxyA.
        RouteSelector.Selection selection3 = routeSelector.next();
        dns.assertRequests();
        assertRoute(selection3.next(), address, RouteSelectorTest.proxyA, dns.lookup(RouteSelectorTest.proxyAHost, 0), RouteSelectorTest.proxyAPort);
        Assert.assertFalse(selection3.hasNext());
        Assert.assertFalse(routeSelector.hasNext());
    }

    @Test
    public void queryForAllSelectedRoutes() throws IOException {
        Address address = httpAddress();
        RouteSelector routeSelector = new RouteSelector(address, routeDatabase, null, EventListener.NONE);
        dns.set(uriHost, dns.allocate(2));
        RouteSelector.Selection selection = routeSelector.next();
        dns.assertRequests(uriHost);
        List<Route> routes = selection.getAll();
        assertRoute(routes.get(0), address, Proxy.NO_PROXY, dns.lookup(uriHost, 0), uriPort);
        assertRoute(routes.get(1), address, Proxy.NO_PROXY, dns.lookup(uriHost, 1), uriPort);
        Assert.assertSame(routes.get(0), selection.next());
        Assert.assertSame(routes.get(1), selection.next());
        Assert.assertFalse(selection.hasNext());
        Assert.assertFalse(routeSelector.hasNext());
    }

    @Test
    public void getHostString() throws Exception {
        // Name proxy specification.
        InetSocketAddress socketAddress = InetSocketAddress.createUnresolved("host", 1234);
        Assert.assertEquals("host", RouteSelector.getHostString(socketAddress));
        socketAddress = InetSocketAddress.createUnresolved("127.0.0.1", 1234);
        Assert.assertEquals("127.0.0.1", RouteSelector.getHostString(socketAddress));
        // InetAddress proxy specification.
        socketAddress = new InetSocketAddress(InetAddress.getByName("localhost"), 1234);
        Assert.assertEquals("127.0.0.1", RouteSelector.getHostString(socketAddress));
        socketAddress = new InetSocketAddress(InetAddress.getByAddress(new byte[]{ 127, 0, 0, 1 }), 1234);
        Assert.assertEquals("127.0.0.1", RouteSelector.getHostString(socketAddress));
        socketAddress = new InetSocketAddress(InetAddress.getByAddress("foobar", new byte[]{ 127, 0, 0, 1 }), 1234);
        Assert.assertEquals("127.0.0.1", RouteSelector.getHostString(socketAddress));
    }

    @Test
    public void routeToString() throws Exception {
        Route route = new Route(httpAddress(), Proxy.NO_PROXY, InetSocketAddress.createUnresolved("host", 1234));
        Assert.assertEquals("Route{host:1234}", route.toString());
    }
}

