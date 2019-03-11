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
package org.apache.hadoop.hdfs.server.namenode.ha;


import MockDomainNameResolver.BYTE_ADDR_1;
import MockDomainNameResolver.BYTE_ADDR_2;
import java.net.InetSocketAddress;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hdfs.protocol.ClientProtocol;
import org.apache.hadoop.net.MockDomainNameResolver;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mockito;


/**
 * Test {@link ConfiguredFailoverProxyProvider}.
 * This manages failover logic for a given set of nameservices/namenodes
 * (aka proxies).
 */
public class TestConfiguredFailoverProxyProvider {
    private Configuration conf;

    private int rpcPort = 8020;

    private URI ns1Uri;

    private URI ns2Uri;

    private URI ns3Uri;

    private String ns1;

    private String ns1nn1Hostname = "machine1.foo.bar";

    private InetSocketAddress ns1nn1 = new InetSocketAddress(ns1nn1Hostname, rpcPort);

    private String ns1nn2Hostname = "machine2.foo.bar";

    private InetSocketAddress ns1nn2 = new InetSocketAddress(ns1nn2Hostname, rpcPort);

    private String ns2;

    private String ns2nn1Hostname = "router1.foo.bar";

    private InetSocketAddress ns2nn1 = new InetSocketAddress(ns2nn1Hostname, rpcPort);

    private String ns2nn2Hostname = "router2.foo.bar";

    private InetSocketAddress ns2nn2 = new InetSocketAddress(ns2nn2Hostname, rpcPort);

    private String ns2nn3Hostname = "router3.foo.bar";

    private InetSocketAddress ns2nn3 = new InetSocketAddress(ns2nn3Hostname, rpcPort);

    private String ns3;

    private static final int NUM_ITERATIONS = 50;

    @Rule
    public final ExpectedException exception = ExpectedException.none();

    /**
     * Tests getProxy with random.order configuration set to false.
     * This expects the proxy order to be consistent every time a new
     * ConfiguredFailoverProxyProvider is created.
     */
    @Test
    public void testNonRandomGetProxy() throws Exception {
        final AtomicInteger nn1Count = new AtomicInteger(0);
        final AtomicInteger nn2Count = new AtomicInteger(0);
        Map<InetSocketAddress, ClientProtocol> proxyMap = new HashMap<>();
        final ClientProtocol nn1Mock = Mockito.mock(ClientProtocol.class);
        Mockito.when(nn1Mock.getStats()).thenAnswer(createAnswer(nn1Count, 1));
        proxyMap.put(ns1nn1, nn1Mock);
        final ClientProtocol nn2Mock = Mockito.mock(ClientProtocol.class);
        Mockito.when(nn2Mock.getStats()).thenAnswer(createAnswer(nn2Count, 2));
        proxyMap.put(ns1nn2, nn2Mock);
        ConfiguredFailoverProxyProvider<ClientProtocol> provider1 = new ConfiguredFailoverProxyProvider(conf, ns1Uri, ClientProtocol.class, createFactory(proxyMap));
        ClientProtocol proxy1 = provider1.getProxy().proxy;
        proxy1.getStats();
        Assert.assertEquals(1, nn1Count.get());
        Assert.assertEquals(0, nn2Count.get());
        proxy1.getStats();
        Assert.assertEquals(2, nn1Count.get());
        Assert.assertEquals(0, nn2Count.get());
        nn1Count.set(0);
        nn2Count.set(0);
        for (int i = 0; i < (TestConfiguredFailoverProxyProvider.NUM_ITERATIONS); i++) {
            ConfiguredFailoverProxyProvider<ClientProtocol> provider2 = new ConfiguredFailoverProxyProvider(conf, ns1Uri, ClientProtocol.class, createFactory(proxyMap));
            ClientProtocol proxy2 = provider2.getProxy().proxy;
            proxy2.getStats();
        }
        Assert.assertEquals(TestConfiguredFailoverProxyProvider.NUM_ITERATIONS, nn1Count.get());
        Assert.assertEquals(0, nn2Count.get());
    }

    /**
     * Tests getProxy with random.order configuration set to true.
     * This expects the proxy order to be random every time a new
     * ConfiguredFailoverProxyProvider is created.
     */
    @Test
    public void testRandomGetProxy() throws Exception {
        final AtomicInteger nn1Count = new AtomicInteger(0);
        final AtomicInteger nn2Count = new AtomicInteger(0);
        final AtomicInteger nn3Count = new AtomicInteger(0);
        Map<InetSocketAddress, ClientProtocol> proxyMap = new HashMap<>();
        final ClientProtocol nn1Mock = Mockito.mock(ClientProtocol.class);
        Mockito.when(nn1Mock.getStats()).thenAnswer(createAnswer(nn1Count, 1));
        proxyMap.put(ns2nn1, nn1Mock);
        final ClientProtocol nn2Mock = Mockito.mock(ClientProtocol.class);
        Mockito.when(nn2Mock.getStats()).thenAnswer(createAnswer(nn2Count, 2));
        proxyMap.put(ns2nn2, nn2Mock);
        final ClientProtocol nn3Mock = Mockito.mock(ClientProtocol.class);
        Mockito.when(nn3Mock.getStats()).thenAnswer(createAnswer(nn3Count, 3));
        proxyMap.put(ns2nn3, nn3Mock);
        for (int i = 0; i < (TestConfiguredFailoverProxyProvider.NUM_ITERATIONS); i++) {
            ConfiguredFailoverProxyProvider<ClientProtocol> provider = new ConfiguredFailoverProxyProvider(conf, ns2Uri, ClientProtocol.class, createFactory(proxyMap));
            ClientProtocol proxy = provider.getProxy().proxy;
            proxy.getStats();
        }
        Assert.assertTrue((((nn1Count.get()) < (TestConfiguredFailoverProxyProvider.NUM_ITERATIONS)) && ((nn1Count.get()) > 0)));
        Assert.assertTrue((((nn2Count.get()) < (TestConfiguredFailoverProxyProvider.NUM_ITERATIONS)) && ((nn2Count.get()) > 0)));
        Assert.assertTrue((((nn3Count.get()) < (TestConfiguredFailoverProxyProvider.NUM_ITERATIONS)) && ((nn3Count.get()) > 0)));
        Assert.assertEquals(TestConfiguredFailoverProxyProvider.NUM_ITERATIONS, (((nn1Count.get()) + (nn2Count.get())) + (nn3Count.get())));
    }

    @Test
    public void testResolveDomainNameUsingDNS() throws Exception {
        Configuration dnsConf = new Configuration(conf);
        addDNSSettings(dnsConf, true);
        // Mock ClientProtocol
        Map<InetSocketAddress, ClientProtocol> proxyMap = new HashMap<>();
        final AtomicInteger nn1Count = addClientMock(BYTE_ADDR_1, proxyMap);
        final AtomicInteger nn2Count = addClientMock(BYTE_ADDR_2, proxyMap);
        // Get a client multiple times
        final Map<String, AtomicInteger> proxyResults = new HashMap<>();
        for (int i = 0; i < (TestConfiguredFailoverProxyProvider.NUM_ITERATIONS); i++) {
            @SuppressWarnings("resource")
            ConfiguredFailoverProxyProvider<ClientProtocol> provider = new ConfiguredFailoverProxyProvider(dnsConf, ns3Uri, ClientProtocol.class, createFactory(proxyMap));
            ClientProtocol proxy = provider.getProxy().proxy;
            String proxyAddress = provider.getProxy().proxyInfo;
            if (proxyResults.containsKey(proxyAddress)) {
                proxyResults.get(proxyAddress).incrementAndGet();
            } else {
                proxyResults.put(proxyAddress, new AtomicInteger(1));
            }
            proxy.getStats();
        }
        // Check we got the proper addresses
        Assert.assertEquals(2, proxyResults.size());
        Assert.assertTrue(("nn1 wasn't returned: " + proxyResults), proxyResults.containsKey((("/" + (MockDomainNameResolver.ADDR_1)) + ":8020")));
        Assert.assertTrue(("nn2 wasn't returned: " + proxyResults), proxyResults.containsKey((("/" + (MockDomainNameResolver.ADDR_2)) + ":8020")));
        // Check that the Namenodes were invoked
        Assert.assertEquals(TestConfiguredFailoverProxyProvider.NUM_ITERATIONS, ((nn1Count.get()) + (nn2Count.get())));
        Assert.assertTrue(("nn1 was selected too much:" + (nn1Count.get())), ((nn1Count.get()) < (TestConfiguredFailoverProxyProvider.NUM_ITERATIONS)));
        Assert.assertTrue(("nn1 should have been selected: " + (nn1Count.get())), ((nn1Count.get()) > 0));
        Assert.assertTrue(("nn2 was selected too much:" + (nn2Count.get())), ((nn2Count.get()) < (TestConfiguredFailoverProxyProvider.NUM_ITERATIONS)));
        Assert.assertTrue(("nn2 should have been selected: " + (nn2Count.get())), ((nn2Count.get()) > 0));
    }

    @Test
    public void testResolveDomainNameUsingDNSUnknownHost() throws Exception {
        Configuration dnsConf = new Configuration(conf);
        addDNSSettings(dnsConf, false);
        Map<InetSocketAddress, ClientProtocol> proxyMap = new HashMap<>();
        exception.expect(RuntimeException.class);
        ConfiguredFailoverProxyProvider<ClientProtocol> provider = new ConfiguredFailoverProxyProvider(dnsConf, ns3Uri, ClientProtocol.class, createFactory(proxyMap));
        Assert.assertNull("failover proxy cannot be created due to unknownhost", provider);
    }
}

