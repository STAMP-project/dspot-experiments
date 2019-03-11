/**
 * Licensed to the Apache Software Foundation (ASF) under one or more contributor license
 * agreements. See the NOTICE file distributed with this work for additional information regarding
 * copyright ownership. The ASF licenses this file to You under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance with the License. You may obtain a
 * copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package org.apache.geode.internal.cache.xmlcache;


import ServerLauncherParameters.INSTANCE;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import org.apache.geode.cache.CacheXmlException;
import org.apache.geode.cache.Declarable;
import org.apache.geode.cache.server.CacheServer;
import org.apache.geode.cache.wan.GatewayReceiver;
import org.apache.geode.cache.wan.GatewayReceiverFactory;
import org.apache.geode.internal.cache.CacheServerImpl;
import org.apache.geode.internal.cache.InternalCache;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.Mockito;


public class CacheCreationJUnitTest {
    @Mock
    private InternalCache cache;

    @Test
    public void verifyRunInitializerWithInitializerAndNullPropsCallsInitAndInitialize() {
        CacheCreation cacheCreation = new CacheCreation();
        Declarable initializer = Mockito.mock(Declarable.class);
        Properties props = null;
        cacheCreation.setInitializer(initializer, props);
        cacheCreation.runInitializer(this.cache);
        Mockito.verify(initializer, Mockito.times(1)).init(ArgumentMatchers.eq(props));
        Mockito.verify(initializer, Mockito.times(1)).initialize(ArgumentMatchers.eq(this.cache), ArgumentMatchers.eq(props));
    }

    @Test
    public void verifyRunInitializerWithInitializerAndPropsCallsInitAndInitialize() {
        CacheCreation cacheCreation = new CacheCreation();
        Declarable initializer = Mockito.mock(Declarable.class);
        Properties props = new Properties();
        props.setProperty("key", "value");
        cacheCreation.setInitializer(initializer, props);
        cacheCreation.runInitializer(this.cache);
        Mockito.verify(initializer, Mockito.times(1)).init(ArgumentMatchers.eq(props));
        Mockito.verify(initializer, Mockito.times(1)).initialize(ArgumentMatchers.eq(this.cache), ArgumentMatchers.eq(props));
    }

    @Test
    public void verifyInitializeDeclarablesMapWithNoDeclarablesPassesEmptyMap() {
        CacheCreation cacheCreation = new CacheCreation();
        Map<Declarable, Properties> expected = Collections.emptyMap();
        cacheCreation.initializeDeclarablesMap(this.cache);
        Mockito.verify(this.cache, Mockito.times(1)).addDeclarableProperties(ArgumentMatchers.eq(expected));
    }

    @Test
    public void verifyInitializeDeclarablesMapWithDeclarablesPassesExpectedMap() {
        CacheCreation cacheCreation = new CacheCreation();
        Map<Declarable, Properties> expected = new HashMap<>();
        Declarable d1 = Mockito.mock(Declarable.class);
        cacheCreation.addDeclarableProperties(d1, null);
        expected.put(d1, null);
        Declarable d2 = Mockito.mock(Declarable.class);
        Properties p2 = new Properties();
        p2.setProperty("k2", "v2");
        cacheCreation.addDeclarableProperties(d2, p2);
        expected.put(d2, p2);
        cacheCreation.initializeDeclarablesMap(this.cache);
        Mockito.verify(this.cache, Mockito.times(1)).addDeclarableProperties(ArgumentMatchers.eq(expected));
    }

    @Test
    public void verifyInitializeDeclarablesMapWithDeclarableCallInitAndInitialize() {
        CacheCreation cacheCreation = new CacheCreation();
        Declarable d2 = Mockito.mock(Declarable.class);
        Properties p2 = new Properties();
        p2.setProperty("k2", "v2");
        cacheCreation.addDeclarableProperties(d2, p2);
        cacheCreation.initializeDeclarablesMap(this.cache);
        Mockito.verify(d2, Mockito.times(1)).init(ArgumentMatchers.eq(p2));
        Mockito.verify(d2, Mockito.times(1)).initialize(ArgumentMatchers.eq(this.cache), ArgumentMatchers.eq(p2));
    }

    @Test
    public void verifyInitializeDeclarablesMapWithDeclarableThatThrowsWillThrowCacheXmlException() {
        CacheCreation cacheCreation = new CacheCreation();
        Declarable d2 = Mockito.mock(Declarable.class);
        Properties p2 = null;
        cacheCreation.addDeclarableProperties(d2, p2);
        Throwable cause = new RuntimeException("expected");
        Mockito.doThrow(cause).when(d2).initialize(this.cache, null);
        Throwable thrown = catchThrowable(() -> cacheCreation.initializeDeclarablesMap(this.cache));
        assertThat(thrown).isExactlyInstanceOf(CacheXmlException.class).hasMessageStartingWith("Exception while initializing an instance of").hasCause(cause);
    }

    @Test
    public void declarativeRegionIsCreated() {
        CacheCreation cacheCreation = new CacheCreation();
        RegionCreation declarativeRegion = Mockito.mock(RegionCreation.class);
        Mockito.when(declarativeRegion.getName()).thenReturn("testRegion");
        Map<String, org.apache.geode.cache.Region<?, ?>> declarativeRegions = new HashMap<>();
        declarativeRegions.put("testRegion", declarativeRegion);
        Mockito.when(this.cache.getRegion("testRegion")).thenReturn(null);
        cacheCreation.initializeRegions(declarativeRegions, this.cache);
        Mockito.verify(declarativeRegion, Mockito.times(1)).createRoot(this.cache);
    }

    @Test
    public void defaultCacheServerIsNotCreatedWithDefaultPortWhenNoDeclarativeServerIsConfigured() {
        CacheCreation cacheCreation = new CacheCreation();
        CacheServerImpl mockServer = Mockito.mock(CacheServerImpl.class);
        Mockito.when(this.cache.addCacheServer()).thenReturn(mockServer);
        List<CacheServer> cacheServers = new ArrayList<>();
        Mockito.when(this.cache.getCacheServers()).thenReturn(cacheServers);
        cacheCreation.startCacheServers(cacheCreation.getCacheServers(), this.cache, INSTANCE.withDisableDefaultServer(false));
        Mockito.verify(this.cache, Mockito.never()).addCacheServer();
    }

    @Test
    public void defaultCacheServerIsNotCreatedWhenDisableDefaultCacheServerIsTrue() {
        CacheCreation cacheCreation = new CacheCreation();
        CacheServerImpl mockServer = Mockito.mock(CacheServerImpl.class);
        Mockito.when(this.cache.addCacheServer()).thenReturn(mockServer);
        List<CacheServer> cacheServers = new ArrayList<>();
        Mockito.when(this.cache.getCacheServers()).thenReturn(cacheServers);
        cacheCreation.startCacheServers(cacheCreation.getCacheServers(), this.cache, INSTANCE.withDisableDefaultServer(false));
        Mockito.verify(this.cache, Mockito.never()).addCacheServer();
    }

    @Test
    public void defaultCacheServerIsCreatedWithConfiguredPortWhenNoDeclarativeServerIsConfigured() {
        CacheCreation cacheCreation = new CacheCreation();
        CacheServerImpl mockServer = Mockito.mock(CacheServerImpl.class);
        Mockito.when(this.cache.addCacheServer()).thenReturn(mockServer);
        List<CacheServer> cacheServers = new ArrayList<>();
        Mockito.when(this.cache.getCacheServers()).thenReturn(cacheServers);
        Boolean disableDefaultCacheServer = false;
        Integer configuredServerPort = 9999;
        cacheCreation.startCacheServers(cacheCreation.getCacheServers(), this.cache, INSTANCE.withPort(configuredServerPort).withDisableDefaultServer(disableDefaultCacheServer));
        Mockito.verify(this.cache, Mockito.times(1)).addCacheServer();
        Mockito.verify(mockServer).setPort(9999);
    }

    @Test
    public void declarativeCacheServerIsCreatedWithConfiguredServerPort() {
        CacheCreation cacheCreation = new CacheCreation();
        CacheServerCreation br1 = new CacheServerCreation(cacheCreation, false);
        br1.setPort(8888);
        cacheCreation.getCacheServers().add(br1);
        CacheServerImpl mockServer = Mockito.mock(CacheServerImpl.class);
        Mockito.when(this.cache.addCacheServer()).thenReturn(mockServer);
        Integer configuredServerPort = 9999;
        Boolean disableDefaultCacheServer = false;
        cacheCreation.startCacheServers(cacheCreation.getCacheServers(), this.cache, INSTANCE.withPort(configuredServerPort).withDisableDefaultServer(disableDefaultCacheServer));
        Mockito.verify(this.cache, Mockito.times(1)).addCacheServer();
        Mockito.verify(mockServer).setPort(configuredServerPort);
    }

    @Test
    public void cacheServerCreationIsSkippedWhenAServerExistsForAGivenPort() {
        CacheCreation cacheCreation = new CacheCreation();
        CacheServerCreation br1 = new CacheServerCreation(cacheCreation, false);
        br1.setPort(40406);
        cacheCreation.getCacheServers().add(br1);
        CacheServerImpl mockServer = Mockito.mock(CacheServerImpl.class);
        Mockito.when(this.cache.addCacheServer()).thenReturn(mockServer);
        Mockito.when(mockServer.getPort()).thenReturn(40406);
        List<CacheServer> cacheServers = new ArrayList<>();
        cacheServers.add(mockServer);
        Mockito.when(this.cache.getCacheServers()).thenReturn(cacheServers);
        cacheCreation.startCacheServers(cacheCreation.getCacheServers(), this.cache, INSTANCE.withDisableDefaultServer(false));
        Mockito.verify(this.cache, Mockito.never()).addCacheServer();
    }

    @Test
    public void userCanCreateMultipleCacheServersDeclaratively() {
        CacheCreation cacheCreation = new CacheCreation();
        CacheServerCreation br1 = new CacheServerCreation(cacheCreation, false);
        br1.setPort(40406);
        CacheServerCreation br2 = new CacheServerCreation(cacheCreation, false);
        br1.setPort(40407);
        cacheCreation.getCacheServers().add(br1);
        cacheCreation.getCacheServers().add(br2);
        CacheServerImpl mockServer = Mockito.mock(CacheServerImpl.class);
        Mockito.when(this.cache.addCacheServer()).thenReturn(mockServer);
        cacheCreation.startCacheServers(cacheCreation.getCacheServers(), this.cache, INSTANCE.withDisableDefaultServer(false));
        Mockito.verify(this.cache, Mockito.times(2)).addCacheServer();
        Mockito.verify(mockServer).configureFrom(br1);
        Mockito.verify(mockServer).configureFrom(br2);
    }

    @Test(expected = RuntimeException.class)
    public void shouldThrowExceptionWhenUserTriesToDeclareMultipleCacheServersWithPort() {
        CacheCreation cacheCreation = new CacheCreation();
        cacheCreation.getCacheServers().add(new CacheServerCreation(cacheCreation, false));
        cacheCreation.getCacheServers().add(new CacheServerCreation(cacheCreation, false));
        Integer configuredServerPort = 50505;
        String configuredServerBindAddress = "localhost[50505]";
        Boolean disableDefaultCacheServer = false;
        cacheCreation.startCacheServers(cacheCreation.getCacheServers(), this.cache, INSTANCE.withPort(configuredServerPort).withBindAddress(configuredServerBindAddress).withDisableDefaultServer(disableDefaultCacheServer));
    }

    @Test
    public void shouldCreateGatewaySenderAfterRegions() {
        CacheCreation cacheCreation = new CacheCreation();
        GatewayReceiver receiver = Mockito.mock(GatewayReceiver.class);
        cacheCreation.addGatewayReceiver(receiver);
        cacheCreation.addRootRegion(new RegionCreation(cacheCreation, "region"));
        InternalCache internalCache = Mockito.mock(InternalCache.class);
        GatewayReceiverFactory receiverFactory = Mockito.mock(GatewayReceiverFactory.class);
        Mockito.when(internalCache.createGatewayReceiverFactory()).thenReturn(receiverFactory);
        Mockito.when(receiverFactory.create()).thenReturn(receiver);
        InOrder inOrder = Mockito.inOrder(internalCache, receiverFactory);
        cacheCreation.create(internalCache);
        // inOrder.verify(cache).basicCreateRegion(eq("region"), any());
        inOrder.verify(internalCache).createGatewayReceiverFactory();
        inOrder.verify(receiverFactory).create();
    }

    @Test
    public void serverLauncherParametersShouldOverrideDefaultSettings() {
        CacheCreation cacheCreation = new CacheCreation();
        CacheServerCreation br1 = new CacheServerCreation(cacheCreation, false);
        cacheCreation.getCacheServers().add(br1);
        CacheServerImpl mockServer = Mockito.mock(CacheServerImpl.class);
        Mockito.when(this.cache.addCacheServer()).thenReturn(mockServer);
        Integer serverPort = 4444;
        Integer maxThreads = 5000;
        Integer maxConnections = 300;
        Integer maxMessageCount = 100;
        Integer socketBufferSize = 1024;
        String serverBindAddress = null;
        Integer messageTimeToLive = 500;
        String hostnameForClients = "hostnameForClients";
        Boolean disableDefaultServer = false;
        INSTANCE.withPort(serverPort).withMaxThreads(maxThreads).withBindAddress(serverBindAddress).withMaxConnections(maxConnections).withMaxMessageCount(maxMessageCount).withSocketBufferSize(socketBufferSize).withMessageTimeToLive(messageTimeToLive).withHostnameForClients(hostnameForClients).withDisableDefaultServer(disableDefaultServer);
        cacheCreation.startCacheServers(cacheCreation.getCacheServers(), this.cache, INSTANCE);
        Mockito.verify(this.cache, Mockito.times(1)).addCacheServer();
        Mockito.verify(mockServer, Mockito.times(1)).setPort(serverPort);
        Mockito.verify(mockServer, Mockito.times(1)).setMaxThreads(maxThreads);
        Mockito.verify(mockServer, Mockito.times(1)).setMaxConnections(maxConnections);
        Mockito.verify(mockServer, Mockito.times(1)).setMaximumMessageCount(maxMessageCount);
        Mockito.verify(mockServer, Mockito.times(1)).setSocketBufferSize(socketBufferSize);
        Mockito.verify(mockServer, Mockito.times(0)).setBindAddress(serverBindAddress);
        Mockito.verify(mockServer, Mockito.times(1)).setMessageTimeToLive(messageTimeToLive);
        Mockito.verify(mockServer, Mockito.times(1)).setHostnameForClients(hostnameForClients);
    }
}

