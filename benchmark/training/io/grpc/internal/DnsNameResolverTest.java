/**
 * Copyright 2015 The gRPC Authors
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


import Code.UNAVAILABLE;
import DnsNameResolver.NETWORKADDRESS_CACHE_TTL_PROPERTY;
import GrpcUtil.NOOP_PROXY_DETECTOR;
import NameResolver.Helper;
import NameResolver.Listener;
import com.google.common.base.Stopwatch;
import com.google.common.net.InetAddresses;
import com.google.common.testing.FakeTicker;
import io.grpc.Attributes;
import io.grpc.EquivalentAddressGroup;
import io.grpc.HttpConnectProxiedSocketAddress;
import io.grpc.NameResolver;
import io.grpc.ProxyDetector;
import io.grpc.Status;
import io.grpc.SynchronizationContext;
import io.grpc.internal.DnsNameResolver.AddressResolver;
import io.grpc.internal.DnsNameResolver.ResolutionResults;
import io.grpc.internal.DnsNameResolver.ResourceResolver;
import io.grpc.internal.DnsNameResolver.ResourceResolverFactory;
import io.grpc.internal.JndiResourceResolverFactory.RecordFetcher;
import io.grpc.internal.SharedResourceHolder.Resource;
import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.URI;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Nullable;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.DisableOnDebug;
import org.junit.rules.ExpectedException;
import org.junit.rules.TestRule;
import org.junit.rules.Timeout;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Captor;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;


/**
 * Unit tests for {@link DnsNameResolver}.
 */
@RunWith(JUnit4.class)
public class DnsNameResolverTest {
    @Rule
    public final TestRule globalTimeout = new DisableOnDebug(Timeout.seconds(10));

    @Rule
    public final MockitoRule mocks = MockitoJUnit.rule();

    @Rule
    public final ExpectedException thrown = ExpectedException.none();

    private final Map<String, ?> serviceConfig = new LinkedHashMap<>();

    private static final int DEFAULT_PORT = 887;

    private final SynchronizationContext syncContext = new SynchronizationContext(new Thread.UncaughtExceptionHandler() {
        @Override
        public void uncaughtException(Thread t, Throwable e) {
            throw new AssertionError(e);
        }
    });

    private final Helper helper = new NameResolver.Helper() {
        @Override
        public int getDefaultPort() {
            return DnsNameResolverTest.DEFAULT_PORT;
        }

        @Override
        public ProxyDetector getProxyDetector() {
            return GrpcUtil.getDefaultProxyDetector();
        }

        @Override
        public SynchronizationContext getSynchronizationContext() {
            return syncContext;
        }
    };

    private final DnsNameResolverProvider provider = new DnsNameResolverProvider();

    private final FakeClock fakeClock = new FakeClock();

    private final FakeClock fakeExecutor = new FakeClock();

    private final Resource<Executor> fakeExecutorResource = new Resource<Executor>() {
        @Override
        public Executor create() {
            return fakeExecutor.getScheduledExecutorService();
        }

        @Override
        public void close(Executor instance) {
        }
    };

    @Mock
    private Listener mockListener;

    @Captor
    private ArgumentCaptor<List<EquivalentAddressGroup>> resultCaptor;

    @Nullable
    private String networkaddressCacheTtlPropertyValue;

    @Mock
    private RecordFetcher recordFetcher;

    @Test
    public void invalidDnsName() throws Exception {
        testInvalidUri(new URI("dns", null, "/[invalid]", null));
    }

    @Test
    public void validIpv6() throws Exception {
        testValidUri(new URI("dns", null, "/[::1]", null), "[::1]", DnsNameResolverTest.DEFAULT_PORT);
    }

    @Test
    public void validDnsNameWithoutPort() throws Exception {
        testValidUri(new URI("dns", null, "/foo.googleapis.com", null), "foo.googleapis.com", DnsNameResolverTest.DEFAULT_PORT);
    }

    @Test
    public void validDnsNameWithPort() throws Exception {
        testValidUri(new URI("dns", null, "/foo.googleapis.com:456", null), "foo.googleapis.com:456", 456);
    }

    @Test
    public void nullDnsName() {
        try {
            newResolver(null, DnsNameResolverTest.DEFAULT_PORT);
            Assert.fail("Expected NullPointerException");
        } catch (NullPointerException e) {
            // expected
        }
    }

    @Test
    public void invalidDnsName_containsUnderscore() {
        try {
            newResolver("host_1", DnsNameResolverTest.DEFAULT_PORT);
            Assert.fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            // expected
        }
    }

    @Test
    public void resolve_androidIgnoresPropertyValue() throws Exception {
        System.setProperty(NETWORKADDRESS_CACHE_TTL_PROPERTY, Long.toString(2));
        resolveNeverCache(true);
    }

    @Test
    public void resolve_androidIgnoresPropertyValueCacheForever() throws Exception {
        System.setProperty(NETWORKADDRESS_CACHE_TTL_PROPERTY, Long.toString((-1)));
        resolveNeverCache(true);
    }

    @Test
    public void resolve_neverCache() throws Exception {
        System.setProperty(NETWORKADDRESS_CACHE_TTL_PROPERTY, "0");
        resolveNeverCache(false);
    }

    @Test
    public void resolveAll_failsOnEmptyResult() throws Exception {
        DnsNameResolver nr = newResolver("dns:///addr.fake:1234", 443);
        nr.setAddressResolver(new AddressResolver() {
            @Override
            public List<InetAddress> resolveAddress(String host) throws Exception {
                return Collections.emptyList();
            }
        });
        nr.start(mockListener);
        assertThat(fakeExecutor.runDueTasks()).isEqualTo(1);
        ArgumentCaptor<Status> ac = ArgumentCaptor.forClass(Status.class);
        Mockito.verify(mockListener).onError(ac.capture());
        Mockito.verifyNoMoreInteractions(mockListener);
        assertThat(ac.getValue().getCode()).isEqualTo(UNAVAILABLE);
        assertThat(ac.getValue().getDescription()).contains("No DNS backend or balancer addresses");
    }

    @Test
    public void resolve_cacheForever() throws Exception {
        System.setProperty(NETWORKADDRESS_CACHE_TTL_PROPERTY, "-1");
        final List<InetAddress> answer1 = createAddressList(2);
        String name = "foo.googleapis.com";
        FakeTicker fakeTicker = new FakeTicker();
        DnsNameResolver resolver = newResolver(name, 81, NOOP_PROXY_DETECTOR, Stopwatch.createUnstarted(fakeTicker));
        AddressResolver mockResolver = Mockito.mock(AddressResolver.class);
        Mockito.when(mockResolver.resolveAddress(Matchers.anyString())).thenReturn(answer1).thenThrow(new AssertionError("should not called twice"));
        resolver.setAddressResolver(mockResolver);
        resolver.start(mockListener);
        Assert.assertEquals(1, fakeExecutor.runDueTasks());
        Mockito.verify(mockListener).onAddresses(resultCaptor.capture(), ArgumentMatchers.any(Attributes.class));
        DnsNameResolverTest.assertAnswerMatches(answer1, 81, resultCaptor.getValue());
        Assert.assertEquals(0, fakeClock.numPendingTasks());
        fakeTicker.advance(1, TimeUnit.DAYS);
        resolver.refresh();
        Assert.assertEquals(0, fakeExecutor.runDueTasks());
        Assert.assertEquals(0, fakeClock.numPendingTasks());
        Mockito.verifyNoMoreInteractions(mockListener);
        resolver.shutdown();
        Mockito.verify(mockResolver).resolveAddress(Matchers.anyString());
    }

    @Test
    public void resolve_usingCache() throws Exception {
        long ttl = 60;
        System.setProperty(NETWORKADDRESS_CACHE_TTL_PROPERTY, Long.toString(ttl));
        final List<InetAddress> answer = createAddressList(2);
        String name = "foo.googleapis.com";
        FakeTicker fakeTicker = new FakeTicker();
        DnsNameResolver resolver = newResolver(name, 81, NOOP_PROXY_DETECTOR, Stopwatch.createUnstarted(fakeTicker));
        AddressResolver mockResolver = Mockito.mock(AddressResolver.class);
        Mockito.when(mockResolver.resolveAddress(Matchers.anyString())).thenReturn(answer).thenThrow(new AssertionError("should not reach here."));
        resolver.setAddressResolver(mockResolver);
        resolver.start(mockListener);
        Assert.assertEquals(1, fakeExecutor.runDueTasks());
        Mockito.verify(mockListener).onAddresses(resultCaptor.capture(), ArgumentMatchers.any(Attributes.class));
        DnsNameResolverTest.assertAnswerMatches(answer, 81, resultCaptor.getValue());
        Assert.assertEquals(0, fakeClock.numPendingTasks());
        // this refresh should return cached result
        fakeTicker.advance((ttl - 1), TimeUnit.SECONDS);
        resolver.refresh();
        Assert.assertEquals(0, fakeExecutor.runDueTasks());
        Assert.assertEquals(0, fakeClock.numPendingTasks());
        Mockito.verifyNoMoreInteractions(mockListener);
        resolver.shutdown();
        Mockito.verify(mockResolver).resolveAddress(Matchers.anyString());
    }

    @Test
    public void resolve_cacheExpired() throws Exception {
        long ttl = 60;
        System.setProperty(NETWORKADDRESS_CACHE_TTL_PROPERTY, Long.toString(ttl));
        final List<InetAddress> answer1 = createAddressList(2);
        final List<InetAddress> answer2 = createAddressList(1);
        String name = "foo.googleapis.com";
        FakeTicker fakeTicker = new FakeTicker();
        DnsNameResolver resolver = newResolver(name, 81, NOOP_PROXY_DETECTOR, Stopwatch.createUnstarted(fakeTicker));
        AddressResolver mockResolver = Mockito.mock(AddressResolver.class);
        Mockito.when(mockResolver.resolveAddress(Matchers.anyString())).thenReturn(answer1).thenReturn(answer2);
        resolver.setAddressResolver(mockResolver);
        resolver.start(mockListener);
        Assert.assertEquals(1, fakeExecutor.runDueTasks());
        Mockito.verify(mockListener).onAddresses(resultCaptor.capture(), ArgumentMatchers.any(Attributes.class));
        DnsNameResolverTest.assertAnswerMatches(answer1, 81, resultCaptor.getValue());
        Assert.assertEquals(0, fakeClock.numPendingTasks());
        fakeTicker.advance((ttl + 1), TimeUnit.SECONDS);
        resolver.refresh();
        Assert.assertEquals(1, fakeExecutor.runDueTasks());
        Mockito.verify(mockListener, Mockito.times(2)).onAddresses(resultCaptor.capture(), ArgumentMatchers.any(Attributes.class));
        DnsNameResolverTest.assertAnswerMatches(answer2, 81, resultCaptor.getValue());
        Assert.assertEquals(0, fakeClock.numPendingTasks());
        resolver.shutdown();
        Mockito.verify(mockResolver, Mockito.times(2)).resolveAddress(Matchers.anyString());
    }

    @Test
    public void resolve_invalidTtlPropertyValue() throws Exception {
        System.setProperty(NETWORKADDRESS_CACHE_TTL_PROPERTY, "not_a_number");
        resolveDefaultValue();
    }

    @Test
    public void resolve_noPropertyValue() throws Exception {
        System.clearProperty(NETWORKADDRESS_CACHE_TTL_PROPERTY);
        resolveDefaultValue();
    }

    @Test
    public void resolveAll_nullResourceResolver() throws Exception {
        final String hostname = "addr.fake";
        final Inet4Address backendAddr = InetAddresses.fromInteger(2130706433);
        AddressResolver mockResolver = Mockito.mock(AddressResolver.class);
        Mockito.when(mockResolver.resolveAddress(Matchers.anyString())).thenReturn(Collections.<InetAddress>singletonList(backendAddr));
        ResourceResolver resourceResolver = null;
        boolean resovleSrv = true;
        boolean resolveTxt = true;
        ResolutionResults res = DnsNameResolver.resolveAll(mockResolver, resourceResolver, resovleSrv, resolveTxt, hostname);
        assertThat(res.addresses).containsExactly(backendAddr);
        assertThat(res.balancerAddresses).isEmpty();
        assertThat(res.txtRecords).isEmpty();
        Mockito.verify(mockResolver).resolveAddress(hostname);
    }

    @Test
    public void resolveAll_nullResourceResolver_addressFailure() throws Exception {
        final String hostname = "addr.fake";
        AddressResolver mockResolver = Mockito.mock(AddressResolver.class);
        Mockito.when(mockResolver.resolveAddress(Matchers.anyString())).thenThrow(new IOException("no addr"));
        ResourceResolver resourceResolver = null;
        boolean resovleSrv = true;
        boolean resolveTxt = true;
        thrown.expect(RuntimeException.class);
        thrown.expectMessage("no addr");
        DnsNameResolver.resolveAll(mockResolver, resourceResolver, resovleSrv, resolveTxt, hostname);
    }

    @Test
    public void resolveAll_presentResourceResolver() throws Exception {
        final String hostname = "addr.fake";
        final Inet4Address backendAddr = InetAddresses.fromInteger(2130706433);
        final EquivalentAddressGroup balancerAddr = new EquivalentAddressGroup(new SocketAddress() {});
        AddressResolver mockAddressResolver = Mockito.mock(AddressResolver.class);
        Mockito.when(mockAddressResolver.resolveAddress(Matchers.anyString())).thenReturn(Collections.<InetAddress>singletonList(backendAddr));
        ResourceResolver mockResourceResolver = Mockito.mock(ResourceResolver.class);
        Mockito.when(mockResourceResolver.resolveTxt(Matchers.anyString())).thenReturn(Collections.singletonList("service config"));
        Mockito.when(mockResourceResolver.resolveSrv(Matchers.any(AddressResolver.class), Matchers.anyString())).thenReturn(Collections.singletonList(balancerAddr));
        boolean resovleSrv = true;
        boolean resolveTxt = true;
        ResolutionResults res = DnsNameResolver.resolveAll(mockAddressResolver, mockResourceResolver, resovleSrv, resolveTxt, hostname);
        assertThat(res.addresses).containsExactly(backendAddr);
        assertThat(res.balancerAddresses).containsExactly(balancerAddr);
        assertThat(res.txtRecords).containsExactly("service config");
        Mockito.verify(mockAddressResolver).resolveAddress(hostname);
        Mockito.verify(mockResourceResolver).resolveTxt(("_grpc_config." + hostname));
        Mockito.verify(mockResourceResolver).resolveSrv(mockAddressResolver, ("_grpclb._tcp." + hostname));
    }

    @Test
    public void resolveAll_onlyBalancers() throws Exception {
        String hostname = "addr.fake";
        EquivalentAddressGroup balancerAddr = new EquivalentAddressGroup(new SocketAddress() {});
        AddressResolver mockAddressResolver = Mockito.mock(AddressResolver.class);
        Mockito.when(mockAddressResolver.resolveAddress(Matchers.anyString())).thenThrow(new UnknownHostException("I really tried"));
        ResourceResolver mockResourceResolver = Mockito.mock(ResourceResolver.class);
        Mockito.when(mockResourceResolver.resolveTxt(Matchers.anyString())).thenReturn(Collections.<String>emptyList());
        Mockito.when(mockResourceResolver.resolveSrv(Matchers.any(AddressResolver.class), Matchers.anyString())).thenReturn(Collections.singletonList(balancerAddr));
        boolean resovleSrv = true;
        boolean resolveTxt = true;
        ResolutionResults res = DnsNameResolver.resolveAll(mockAddressResolver, mockResourceResolver, resovleSrv, resolveTxt, hostname);
        assertThat(res.addresses).isEmpty();
        assertThat(res.balancerAddresses).containsExactly(balancerAddr);
        assertThat(res.txtRecords).isEmpty();
        Mockito.verify(mockAddressResolver).resolveAddress(hostname);
        Mockito.verify(mockResourceResolver).resolveTxt(("_grpc_config." + hostname));
        Mockito.verify(mockResourceResolver).resolveSrv(mockAddressResolver, ("_grpclb._tcp." + hostname));
    }

    @Test
    public void resolveAll_balancerLookupFails() throws Exception {
        final String hostname = "addr.fake";
        final Inet4Address backendAddr = InetAddresses.fromInteger(2130706433);
        AddressResolver mockAddressResolver = Mockito.mock(AddressResolver.class);
        Mockito.when(mockAddressResolver.resolveAddress(Matchers.anyString())).thenReturn(Collections.<InetAddress>singletonList(backendAddr));
        ResourceResolver mockResourceResolver = Mockito.mock(ResourceResolver.class);
        Mockito.when(mockResourceResolver.resolveTxt(Matchers.anyString())).thenReturn(Collections.singletonList("service config"));
        Mockito.when(mockResourceResolver.resolveSrv(Matchers.any(AddressResolver.class), Matchers.anyString())).thenThrow(new Exception("something like javax.naming.NamingException"));
        boolean resovleSrv = true;
        boolean resolveTxt = true;
        ResolutionResults res = DnsNameResolver.resolveAll(mockAddressResolver, mockResourceResolver, resovleSrv, resolveTxt, hostname);
        assertThat(res.addresses).containsExactly(backendAddr);
        assertThat(res.balancerAddresses).isEmpty();
        assertThat(res.txtRecords).containsExactly("service config");
        Mockito.verify(mockAddressResolver).resolveAddress(hostname);
        Mockito.verify(mockResourceResolver).resolveTxt(("_grpc_config." + hostname));
        Mockito.verify(mockResourceResolver).resolveSrv(mockAddressResolver, ("_grpclb._tcp." + hostname));
    }

    @Test
    public void skipMissingJndiResolverResolver() throws Exception {
        ClassLoader cl = new ClassLoader() {
            @Override
            protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
                if ("io.grpc.internal.JndiResourceResolverFactory".equals(name)) {
                    throw new ClassNotFoundException();
                }
                return super.loadClass(name, resolve);
            }
        };
        ResourceResolverFactory factory = DnsNameResolver.getResourceResolverFactory(cl);
        assertThat(factory).isNull();
    }

    @Test
    public void doNotResolveWhenProxyDetected() throws Exception {
        final String name = "foo.googleapis.com";
        final int port = 81;
        final InetSocketAddress proxyAddress = new InetSocketAddress(InetAddress.getByName("10.0.0.1"), 1000);
        ProxyDetector alwaysDetectProxy = new ProxyDetector() {
            @Override
            public HttpConnectProxiedSocketAddress proxyFor(SocketAddress targetAddress) {
                return HttpConnectProxiedSocketAddress.newBuilder().setTargetAddress(((InetSocketAddress) (targetAddress))).setProxyAddress(proxyAddress).setUsername("username").setPassword("password").build();
            }
        };
        DnsNameResolver resolver = newResolver(name, port, alwaysDetectProxy, Stopwatch.createUnstarted());
        AddressResolver mockAddressResolver = Mockito.mock(AddressResolver.class);
        Mockito.when(mockAddressResolver.resolveAddress(Matchers.anyString())).thenThrow(new AssertionError());
        resolver.setAddressResolver(mockAddressResolver);
        resolver.start(mockListener);
        Assert.assertEquals(1, fakeExecutor.runDueTasks());
        Mockito.verify(mockListener).onAddresses(resultCaptor.capture(), ArgumentMatchers.any(Attributes.class));
        List<EquivalentAddressGroup> result = resultCaptor.getValue();
        assertThat(result).hasSize(1);
        EquivalentAddressGroup eag = result.get(0);
        assertThat(eag.getAddresses()).hasSize(1);
        HttpConnectProxiedSocketAddress socketAddress = ((HttpConnectProxiedSocketAddress) (eag.getAddresses().get(0)));
        Assert.assertSame(proxyAddress, socketAddress.getProxyAddress());
        Assert.assertEquals("username", socketAddress.getUsername());
        Assert.assertEquals("password", socketAddress.getPassword());
        Assert.assertTrue(socketAddress.getTargetAddress().isUnresolved());
    }

    @Test
    public void maybeChooseServiceConfig_failsOnMisspelling() {
        Map<String, Object> bad = new LinkedHashMap<>();
        bad.put("parcentage", 1.0);
        thrown.expectMessage("Bad key");
        DnsNameResolver.maybeChooseServiceConfig(bad, new Random(), "host");
    }

    @Test
    public void maybeChooseServiceConfig_clientLanguageMatchesJava() {
        Map<String, Object> choice = new LinkedHashMap<>();
        List<String> langs = new ArrayList<>();
        langs.add("java");
        choice.put("clientLanguage", langs);
        choice.put("serviceConfig", serviceConfig);
        Assert.assertNotNull(DnsNameResolver.maybeChooseServiceConfig(choice, new Random(), "host"));
    }

    @Test
    public void maybeChooseServiceConfig_clientLanguageDoesntMatchGo() {
        Map<String, Object> choice = new LinkedHashMap<>();
        List<String> langs = new ArrayList<>();
        langs.add("go");
        choice.put("clientLanguage", langs);
        choice.put("serviceConfig", serviceConfig);
        Assert.assertNull(DnsNameResolver.maybeChooseServiceConfig(choice, new Random(), "host"));
    }

    @Test
    public void maybeChooseServiceConfig_clientLanguageCaseInsensitive() {
        Map<String, Object> choice = new LinkedHashMap<>();
        List<String> langs = new ArrayList<>();
        langs.add("JAVA");
        choice.put("clientLanguage", langs);
        choice.put("serviceConfig", serviceConfig);
        Assert.assertNotNull(DnsNameResolver.maybeChooseServiceConfig(choice, new Random(), "host"));
    }

    @Test
    public void maybeChooseServiceConfig_clientLanguageMatchesEmtpy() {
        Map<String, Object> choice = new LinkedHashMap<>();
        List<String> langs = new ArrayList<>();
        choice.put("clientLanguage", langs);
        choice.put("serviceConfig", serviceConfig);
        Assert.assertNotNull(DnsNameResolver.maybeChooseServiceConfig(choice, new Random(), "host"));
    }

    @Test
    public void maybeChooseServiceConfig_clientLanguageMatchesMulti() {
        Map<String, Object> choice = new LinkedHashMap<>();
        List<String> langs = new ArrayList<>();
        langs.add("go");
        langs.add("java");
        choice.put("clientLanguage", langs);
        choice.put("serviceConfig", serviceConfig);
        Assert.assertNotNull(DnsNameResolver.maybeChooseServiceConfig(choice, new Random(), "host"));
    }

    @Test
    public void maybeChooseServiceConfig_percentageZeroAlwaysFails() {
        Map<String, Object> choice = new LinkedHashMap<>();
        choice.put("percentage", 0.0);
        choice.put("serviceConfig", serviceConfig);
        Assert.assertNull(DnsNameResolver.maybeChooseServiceConfig(choice, new Random(), "host"));
    }

    @Test
    public void maybeChooseServiceConfig_percentageHundredAlwaysSucceeds() {
        Map<String, Object> choice = new LinkedHashMap<>();
        choice.put("percentage", 100.0);
        choice.put("serviceConfig", serviceConfig);
        Assert.assertNotNull(DnsNameResolver.maybeChooseServiceConfig(choice, new Random(), "host"));
    }

    @Test
    public void maybeChooseServiceConfig_percentageAboveMatches50() {
        Map<String, Object> choice = new LinkedHashMap<>();
        choice.put("percentage", 50.0);
        choice.put("serviceConfig", serviceConfig);
        Random r = new Random() {
            @Override
            public int nextInt(int bound) {
                return 49;
            }
        };
        Assert.assertNotNull(DnsNameResolver.maybeChooseServiceConfig(choice, r, "host"));
    }

    @Test
    public void maybeChooseServiceConfig_percentageAtFails50() {
        Map<String, Object> choice = new LinkedHashMap<>();
        choice.put("percentage", 50.0);
        choice.put("serviceConfig", serviceConfig);
        Random r = new Random() {
            @Override
            public int nextInt(int bound) {
                return 50;
            }
        };
        Assert.assertNull(DnsNameResolver.maybeChooseServiceConfig(choice, r, "host"));
    }

    @Test
    public void maybeChooseServiceConfig_percentageAboveMatches99() {
        Map<String, Object> choice = new LinkedHashMap<>();
        choice.put("percentage", 99.0);
        choice.put("serviceConfig", serviceConfig);
        Random r = new Random() {
            @Override
            public int nextInt(int bound) {
                return 98;
            }
        };
        Assert.assertNotNull(DnsNameResolver.maybeChooseServiceConfig(choice, r, "host"));
    }

    @Test
    public void maybeChooseServiceConfig_percentageAtFails99() {
        Map<String, Object> choice = new LinkedHashMap<>();
        choice.put("percentage", 99.0);
        choice.put("serviceConfig", serviceConfig);
        Random r = new Random() {
            @Override
            public int nextInt(int bound) {
                return 99;
            }
        };
        Assert.assertNull(DnsNameResolver.maybeChooseServiceConfig(choice, r, "host"));
    }

    @Test
    public void maybeChooseServiceConfig_percentageAboveMatches1() {
        Map<String, Object> choice = new LinkedHashMap<>();
        choice.put("percentage", 1.0);
        choice.put("serviceConfig", serviceConfig);
        Random r = new Random() {
            @Override
            public int nextInt(int bound) {
                return 0;
            }
        };
        Assert.assertNotNull(DnsNameResolver.maybeChooseServiceConfig(choice, r, "host"));
    }

    @Test
    public void maybeChooseServiceConfig_percentageAtFails1() {
        Map<String, Object> choice = new LinkedHashMap<>();
        choice.put("percentage", 1.0);
        choice.put("serviceConfig", serviceConfig);
        Random r = new Random() {
            @Override
            public int nextInt(int bound) {
                return 1;
            }
        };
        Assert.assertNull(DnsNameResolver.maybeChooseServiceConfig(choice, r, "host"));
    }

    @Test
    public void maybeChooseServiceConfig_hostnameMatches() {
        Map<String, Object> choice = new LinkedHashMap<>();
        List<String> hosts = new ArrayList<>();
        hosts.add("localhost");
        choice.put("clientHostname", hosts);
        choice.put("serviceConfig", serviceConfig);
        Assert.assertNotNull(DnsNameResolver.maybeChooseServiceConfig(choice, new Random(), "localhost"));
    }

    @Test
    public void maybeChooseServiceConfig_hostnameDoesntMatch() {
        Map<String, Object> choice = new LinkedHashMap<>();
        List<String> hosts = new ArrayList<>();
        hosts.add("localhorse");
        choice.put("clientHostname", hosts);
        choice.put("serviceConfig", serviceConfig);
        Assert.assertNull(DnsNameResolver.maybeChooseServiceConfig(choice, new Random(), "localhost"));
    }

    @Test
    public void maybeChooseServiceConfig_clientLanguageCaseSensitive() {
        Map<String, Object> choice = new LinkedHashMap<>();
        List<String> hosts = new ArrayList<>();
        hosts.add("LOCALHOST");
        choice.put("clientHostname", hosts);
        choice.put("serviceConfig", serviceConfig);
        Assert.assertNull(DnsNameResolver.maybeChooseServiceConfig(choice, new Random(), "localhost"));
    }

    @Test
    public void maybeChooseServiceConfig_hostnameMatchesEmtpy() {
        Map<String, Object> choice = new LinkedHashMap<>();
        List<String> hosts = new ArrayList<>();
        choice.put("clientHostname", hosts);
        choice.put("serviceConfig", serviceConfig);
        Assert.assertNotNull(DnsNameResolver.maybeChooseServiceConfig(choice, new Random(), "host"));
    }

    @Test
    public void maybeChooseServiceConfig_hostnameMatchesMulti() {
        Map<String, Object> choice = new LinkedHashMap<>();
        List<String> hosts = new ArrayList<>();
        hosts.add("localhorse");
        hosts.add("localhost");
        choice.put("clientHostname", hosts);
        choice.put("serviceConfig", serviceConfig);
        Assert.assertNotNull(DnsNameResolver.maybeChooseServiceConfig(choice, new Random(), "localhost"));
    }

    @Test
    public void parseTxtResults_misspelledName() {
        List<String> txtRecords = new ArrayList<>();
        txtRecords.add("some_record");
        txtRecords.add("_grpc_config=[]");
        List<? extends Map<String, ?>> results = DnsNameResolver.parseTxtResults(txtRecords);
        assertThat(results).isEmpty();
    }

    @Test
    public void parseTxtResults_badTypeIgnored() {
        Logger logger = Logger.getLogger(DnsNameResolver.class.getName());
        Level level = logger.getLevel();
        logger.setLevel(Level.SEVERE);
        try {
            List<String> txtRecords = new ArrayList<>();
            txtRecords.add("some_record");
            txtRecords.add("grpc_config={}");
            List<? extends Map<String, ?>> results = DnsNameResolver.parseTxtResults(txtRecords);
            assertThat(results).isEmpty();
        } finally {
            logger.setLevel(level);
        }
    }

    @Test
    public void parseTxtResults_badInnerTypeIgnored() {
        Logger logger = Logger.getLogger(DnsNameResolver.class.getName());
        Level level = logger.getLevel();
        logger.setLevel(Level.SEVERE);
        try {
            List<String> txtRecords = new ArrayList<>();
            txtRecords.add("some_record");
            txtRecords.add("grpc_config=[\"bogus\"]");
            List<? extends Map<String, ?>> results = DnsNameResolver.parseTxtResults(txtRecords);
            assertThat(results).isEmpty();
        } finally {
            logger.setLevel(level);
        }
    }

    @Test
    public void parseTxtResults_combineAll() {
        Logger logger = Logger.getLogger(DnsNameResolver.class.getName());
        Level level = logger.getLevel();
        logger.setLevel(Level.SEVERE);
        try {
            List<String> txtRecords = new ArrayList<>();
            txtRecords.add("some_record");
            txtRecords.add("grpc_config=[\"bogus\", {}]");
            txtRecords.add("grpc_config=[{}, {}]");// 2 records

            txtRecords.add("grpc_config=[{\"\":{}}]");// 1 record

            List<? extends Map<String, ?>> results = DnsNameResolver.parseTxtResults(txtRecords);
            assertThat(results).hasSize((2 + 1));
        } finally {
            logger.setLevel(level);
        }
    }

    @Test
    public void shouldUseJndi_alwaysFalseIfDisabled() {
        boolean enableJndi = false;
        boolean enableJndiLocalhost = true;
        String host = "seemingly.valid.host";
        Assert.assertFalse(DnsNameResolver.shouldUseJndi(enableJndi, enableJndiLocalhost, host));
    }

    @Test
    public void shouldUseJndi_falseIfDisabledForLocalhost() {
        boolean enableJndi = true;
        boolean enableJndiLocalhost = false;
        Assert.assertFalse(DnsNameResolver.shouldUseJndi(enableJndi, enableJndiLocalhost, "localhost"));
        Assert.assertFalse(DnsNameResolver.shouldUseJndi(enableJndi, enableJndiLocalhost, "LOCALHOST"));
    }

    @Test
    public void shouldUseJndi_trueIfLocalhostOverriden() {
        boolean enableJndi = true;
        boolean enableJndiLocalhost = true;
        String host = "localhost";
        Assert.assertTrue(DnsNameResolver.shouldUseJndi(enableJndi, enableJndiLocalhost, host));
    }

    @Test
    public void shouldUseJndi_falseForIpv6() {
        boolean enableJndi = true;
        boolean enableJndiLocalhost = false;
        Assert.assertFalse(DnsNameResolver.shouldUseJndi(enableJndi, enableJndiLocalhost, "::"));
        Assert.assertFalse(DnsNameResolver.shouldUseJndi(enableJndi, enableJndiLocalhost, "::1"));
        Assert.assertFalse(DnsNameResolver.shouldUseJndi(enableJndi, enableJndiLocalhost, "2001:db8:1234::"));
        Assert.assertFalse(DnsNameResolver.shouldUseJndi(enableJndi, enableJndiLocalhost, "[2001:db8:1234::]"));
        Assert.assertFalse(DnsNameResolver.shouldUseJndi(enableJndi, enableJndiLocalhost, "2001:db8:1234::%3"));
    }

    @Test
    public void shouldUseJndi_falseForIpv4() {
        boolean enableJndi = true;
        boolean enableJndiLocalhost = false;
        Assert.assertFalse(DnsNameResolver.shouldUseJndi(enableJndi, enableJndiLocalhost, "127.0.0.1"));
        Assert.assertFalse(DnsNameResolver.shouldUseJndi(enableJndi, enableJndiLocalhost, "192.168.0.1"));
        Assert.assertFalse(DnsNameResolver.shouldUseJndi(enableJndi, enableJndiLocalhost, "134744072"));
    }

    @Test
    public void shouldUseJndi_falseForEmpty() {
        boolean enableJndi = true;
        boolean enableJndiLocalhost = false;
        Assert.assertFalse(DnsNameResolver.shouldUseJndi(enableJndi, enableJndiLocalhost, ""));
    }

    @Test
    public void shouldUseJndi_trueIfItMightPossiblyBeValid() {
        boolean enableJndi = true;
        boolean enableJndiLocalhost = false;
        Assert.assertTrue(DnsNameResolver.shouldUseJndi(enableJndi, enableJndiLocalhost, "remotehost"));
        Assert.assertTrue(DnsNameResolver.shouldUseJndi(enableJndi, enableJndiLocalhost, "remotehost.gov"));
        Assert.assertTrue(DnsNameResolver.shouldUseJndi(enableJndi, enableJndiLocalhost, "f.q.d.n."));
        Assert.assertTrue(DnsNameResolver.shouldUseJndi(enableJndi, enableJndiLocalhost, "8.8.8.8.in-addr.arpa."));
        Assert.assertTrue(DnsNameResolver.shouldUseJndi(enableJndi, enableJndiLocalhost, "2001-db8-1234--as3.ipv6-literal.net"));
    }

    private byte lastByte = 0;
}

