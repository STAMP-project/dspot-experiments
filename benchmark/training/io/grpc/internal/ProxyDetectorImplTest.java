/**
 * Copyright 2017 The gRPC Authors
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
package io.grpc.internal;


import ProxyDetectorImpl.AuthenticationProvider;
import com.google.common.base.Supplier;
import com.google.common.collect.ImmutableList;
import io.grpc.HttpConnectProxiedSocketAddress;
import io.grpc.ProxiedSocketAddress;
import io.grpc.ProxyDetector;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.PasswordAuthentication;
import java.net.Proxy;
import java.net.ProxySelector;
import java.net.SocketAddress;
import java.net.URI;
import junit.framework.TestCase;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import static java.net.Proxy.Type.HTTP;


@RunWith(JUnit4.class)
public class ProxyDetectorImplTest {
    @Rule
    public final MockitoRule mocks = MockitoJUnit.rule();

    @Mock
    private ProxySelector proxySelector;

    @Mock
    private AuthenticationProvider authenticator;

    private InetSocketAddress destination = InetSocketAddress.createUnresolved("10.10.10.10", 5678);

    private Supplier<ProxySelector> proxySelectorSupplier;

    private ProxyDetector proxyDetector;

    private InetSocketAddress unresolvedProxy;

    private HttpConnectProxiedSocketAddress proxySocketAddress;

    private final int proxyPort = 1234;

    @Test
    public void override_hostPort() throws Exception {
        final String overrideHost = "10.99.99.99";
        final int overridePort = 1234;
        final String overrideHostWithPort = (overrideHost + ":") + overridePort;
        ProxyDetectorImpl proxyDetector = new ProxyDetectorImpl(proxySelectorSupplier, authenticator, overrideHostWithPort);
        ProxiedSocketAddress detected = proxyDetector.proxyFor(destination);
        Assert.assertNotNull(detected);
        Assert.assertEquals(HttpConnectProxiedSocketAddress.newBuilder().setTargetAddress(destination).setProxyAddress(new InetSocketAddress(InetAddress.getByName(overrideHost), overridePort)).build(), detected);
    }

    @Test
    public void override_hostOnly() throws Exception {
        final String overrideHostWithoutPort = "10.99.99.99";
        final int defaultPort = 80;
        ProxyDetectorImpl proxyDetector = new ProxyDetectorImpl(proxySelectorSupplier, authenticator, overrideHostWithoutPort);
        ProxiedSocketAddress detected = proxyDetector.proxyFor(destination);
        Assert.assertNotNull(detected);
        Assert.assertEquals(HttpConnectProxiedSocketAddress.newBuilder().setTargetAddress(destination).setProxyAddress(new InetSocketAddress(InetAddress.getByName(overrideHostWithoutPort), defaultPort)).build(), detected);
    }

    @Test
    public void returnNullWhenNoProxy() throws Exception {
        Mockito.when(proxySelector.select(ArgumentMatchers.any(URI.class))).thenReturn(ImmutableList.of(Proxy.NO_PROXY));
        Assert.assertNull(proxyDetector.proxyFor(destination));
    }

    @Test
    public void detectProxyForUnresolvedDestination() throws Exception {
        Proxy proxy = new Proxy(HTTP, unresolvedProxy);
        Mockito.when(proxySelector.select(ArgumentMatchers.any(URI.class))).thenReturn(ImmutableList.of(proxy));
        ProxiedSocketAddress detected = proxyDetector.proxyFor(destination);
        Assert.assertNotNull(detected);
        Assert.assertEquals(proxySocketAddress, detected);
    }

    @Test
    public void detectProxyForResolvedDestination() throws Exception {
        InetSocketAddress resolved = new InetSocketAddress(InetAddress.getByName("10.1.2.3"), 10);
        TestCase.assertFalse(resolved.isUnresolved());
        Proxy proxy = new Proxy(HTTP, unresolvedProxy);
        Mockito.when(proxySelector.select(ArgumentMatchers.any(URI.class))).thenReturn(ImmutableList.of(proxy));
        ProxiedSocketAddress detected = proxyDetector.proxyFor(resolved);
        Assert.assertNotNull(detected);
        HttpConnectProxiedSocketAddress expected = HttpConnectProxiedSocketAddress.newBuilder().setTargetAddress(resolved).setProxyAddress(new InetSocketAddress(InetAddress.getByName(unresolvedProxy.getHostName()), proxyPort)).build();
        Assert.assertEquals(expected, detected);
    }

    @Test
    public void unresolvedProxyAddressBecomesResolved() throws Exception {
        InetSocketAddress unresolvedProxy = InetSocketAddress.createUnresolved("10.0.0.100", 1234);
        Assert.assertTrue(unresolvedProxy.isUnresolved());
        Proxy proxy1 = new Proxy(HTTP, unresolvedProxy);
        Mockito.when(proxySelector.select(ArgumentMatchers.any(URI.class))).thenReturn(ImmutableList.of(proxy1));
        HttpConnectProxiedSocketAddress proxy = ((HttpConnectProxiedSocketAddress) (proxyDetector.proxyFor(destination)));
        TestCase.assertFalse(((InetSocketAddress) (proxy.getProxyAddress())).isUnresolved());
    }

    @Test
    public void pickFirstHttpProxy() throws Exception {
        InetSocketAddress otherProxy = InetSocketAddress.createUnresolved("10.0.0.2", 11111);
        Assert.assertNotEquals(unresolvedProxy, otherProxy);
        Proxy proxy1 = new Proxy(HTTP, unresolvedProxy);
        Proxy proxy2 = new Proxy(HTTP, otherProxy);
        Mockito.when(proxySelector.select(ArgumentMatchers.any(URI.class))).thenReturn(ImmutableList.of(proxy1, proxy2));
        ProxiedSocketAddress detected = proxyDetector.proxyFor(destination);
        Assert.assertNotNull(detected);
        Assert.assertEquals(proxySocketAddress, detected);
    }

    // Mainly for InProcessSocketAddress
    @Test
    public void noProxyForNonInetSocket() throws Exception {
        Assert.assertNull(proxyDetector.proxyFor(Mockito.mock(SocketAddress.class)));
    }

    @Test
    public void authRequired() throws Exception {
        Proxy proxy = new Proxy(HTTP, unresolvedProxy);
        final String proxyUser = "testuser";
        final String proxyPassword = "testpassword";
        PasswordAuthentication auth = new PasswordAuthentication(proxyUser, proxyPassword.toCharArray());
        Mockito.when(authenticator.requestPasswordAuthentication(ArgumentMatchers.any(String.class), ArgumentMatchers.any(InetAddress.class), ArgumentMatchers.anyInt(), ArgumentMatchers.any(String.class), ArgumentMatchers.any(String.class), ArgumentMatchers.any(String.class))).thenReturn(auth);
        Mockito.when(proxySelector.select(ArgumentMatchers.any(URI.class))).thenReturn(ImmutableList.of(proxy));
        ProxiedSocketAddress detected = proxyDetector.proxyFor(destination);
        Assert.assertEquals(HttpConnectProxiedSocketAddress.newBuilder().setTargetAddress(destination).setProxyAddress(new InetSocketAddress(InetAddress.getByName(unresolvedProxy.getHostName()), unresolvedProxy.getPort())).setUsername(proxyUser).setPassword(proxyPassword).build(), detected);
    }

    @Test
    public void proxySelectorReturnsNull() throws Exception {
        ProxyDetectorImpl proxyDetector = new ProxyDetectorImpl(new Supplier<ProxySelector>() {
            @Override
            public ProxySelector get() {
                return null;
            }
        }, authenticator, null);
        Assert.assertNull(proxyDetector.proxyFor(destination));
    }
}

