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
package org.apache.geode.distributed;


import ServerLauncherParameters.INSTANCE;
import java.io.IOException;
import java.util.Collections;
import org.apache.geode.cache.Cache;
import org.apache.geode.cache.server.CacheServer;
import org.apache.geode.distributed.ServerLauncher.Builder;
import org.apache.geode.internal.cache.CacheConfig;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


/**
 * Unit tests for {@link ServerLauncher}.
 *
 * @since GemFire 7.0
 */
public class ServerLauncherTest {
    @Test
    public void constructorCorrectlySetsCacheServerLauncherParameters() {
        ServerLauncher launcher = new Builder().setServerBindAddress(null).setServerPort(11235).setMaxThreads(10).setMaxConnections(100).setMaxMessageCount(5).setMessageTimeToLive(10000).setSocketBufferSize(2048).setHostNameForClients("hostName4Clients").setDisableDefaultServer(Boolean.FALSE).build();
        assertThat(launcher).isNotNull();
        assertThat(INSTANCE).isNotNull();
        assertThat(INSTANCE.getPort()).isEqualTo(11235);
        assertThat(INSTANCE.getMaxThreads()).isEqualTo(10);
        assertThat(INSTANCE.getBindAddress()).isEqualTo(null);
        assertThat(INSTANCE.getMaxConnections()).isEqualTo(100);
        assertThat(INSTANCE.getMaxMessageCount()).isEqualTo(5);
        assertThat(INSTANCE.getSocketBufferSize()).isEqualTo(2048);
        assertThat(INSTANCE.getMessageTimeToLive()).isEqualTo(10000);
        assertThat(INSTANCE.getHostnameForClients()).isEqualTo("hostName4Clients");
        assertThat(INSTANCE.isDisableDefaultServer()).isFalse();
    }

    @Test
    public void canBeMocked() throws IOException {
        ServerLauncher launcher = Mockito.mock(ServerLauncher.class);
        Cache cache = Mockito.mock(Cache.class);
        CacheConfig cacheConfig = Mockito.mock(CacheConfig.class);
        Mockito.when(launcher.getCache()).thenReturn(cache);
        Mockito.when(launcher.getCacheConfig()).thenReturn(cacheConfig);
        Mockito.when(launcher.getId()).thenReturn("ID");
        Mockito.when(launcher.isWaiting(ArgumentMatchers.eq(cache))).thenReturn(true);
        Mockito.when(launcher.isHelping()).thenReturn(true);
        launcher.startCacheServer(cache);
        Mockito.verify(launcher, Mockito.times(1)).startCacheServer(cache);
        assertThat(launcher.getCache()).isSameAs(cache);
        assertThat(launcher.getCacheConfig()).isSameAs(cacheConfig);
        assertThat(launcher.getId()).isSameAs("ID");
        assertThat(launcher.isWaiting(cache)).isTrue();
        assertThat(launcher.isHelping()).isTrue();
    }

    @Test
    public void isServingReturnsTrueWhenCacheHasOneCacheServer() {
        Cache cache = Mockito.mock(Cache.class);
        CacheServer cacheServer = Mockito.mock(CacheServer.class);
        Mockito.when(cache.getCacheServers()).thenReturn(Collections.singletonList(cacheServer));
        ServerLauncher launcher = new Builder().build();
        assertThat(launcher.isServing(cache)).isTrue();
    }

    @Test
    public void isServingReturnsFalseWhenCacheHasZeroCacheServers() {
        Cache cache = Mockito.mock(Cache.class);
        Mockito.when(cache.getCacheServers()).thenReturn(Collections.emptyList());
        ServerLauncher launcher = new Builder().build();
        assertThat(launcher.isServing(cache)).isFalse();
    }

    @Test
    public void reconnectedCacheIsClosed() {
        Cache cache = Mockito.mock(Cache.class, "Cache");
        Cache reconnectedCache = Mockito.mock(Cache.class, "ReconnectedCache");
        Mockito.when(cache.isReconnecting()).thenReturn(false).thenReturn(false).thenReturn(true);
        Mockito.when(cache.getCacheServers()).thenReturn(Collections.emptyList());
        Mockito.when(cache.getReconnectedCache()).thenReturn(reconnectedCache);
        new Builder().setCache(cache).build().waitOnServer();
        Mockito.verify(cache, Mockito.atLeast(3)).isReconnecting();
        Mockito.verify(cache).getReconnectedCache();
        Mockito.verify(reconnectedCache).close();
    }

    @Test
    public void isRunningReturnsTrueWhenRunningIsSetTrue() {
        ServerLauncher launcher = new Builder().build();
        launcher.running.set(true);
        assertThat(launcher.isRunning()).isTrue();
    }

    @Test
    public void isRunningReturnsFalseWhenRunningIsSetFalse() {
        ServerLauncher launcher = new Builder().build();
        launcher.running.set(false);
        assertThat(launcher.isRunning()).isFalse();
    }

    @Test
    public void reconnectingDistributedSystemIsDisconnectedOnStop() {
        Cache cache = Mockito.mock(Cache.class, "Cache");
        DistributedSystem system = Mockito.mock(DistributedSystem.class, "DistributedSystem");
        Cache reconnectedCache = Mockito.mock(Cache.class, "ReconnectedCache");
        Mockito.when(cache.isReconnecting()).thenReturn(true);
        Mockito.when(cache.getReconnectedCache()).thenReturn(reconnectedCache);
        Mockito.when(reconnectedCache.isReconnecting()).thenReturn(true);
        Mockito.when(reconnectedCache.getReconnectedCache()).thenReturn(null);
        Mockito.when(reconnectedCache.getDistributedSystem()).thenReturn(system);
        ServerLauncher launcher = new Builder().setCache(cache).build();
        launcher.running.set(true);
        launcher.stop();
        Mockito.verify(cache, Mockito.times(1)).isReconnecting();
        Mockito.verify(cache, Mockito.times(1)).getReconnectedCache();
        Mockito.verify(cache, Mockito.times(1)).isReconnecting();
        Mockito.verify(cache, Mockito.times(1)).getReconnectedCache();
        Mockito.verify(reconnectedCache, Mockito.times(1)).getDistributedSystem();
        Mockito.verify(system, Mockito.times(1)).stopReconnecting();
        Mockito.verify(reconnectedCache, Mockito.times(1)).close();
    }

    @Test
    public void isWaitingReturnsTrueWhenSystemIsConnected() {
        Cache cache = Mockito.mock(Cache.class, "Cache");
        DistributedSystem system = Mockito.mock(DistributedSystem.class, "DistributedSystem");
        Mockito.when(cache.getDistributedSystem()).thenReturn(system);
        Mockito.when(system.isConnected()).thenReturn(true);
        ServerLauncher launcher = new Builder().build();
        launcher.running.set(true);
        assertThat(launcher.isWaiting(cache)).isTrue();
    }

    @Test
    public void isWaitingReturnsFalseWhenSystemIsNotConnected() {
        Cache cache = Mockito.mock(Cache.class, "Cache");
        DistributedSystem system = Mockito.mock(DistributedSystem.class, "DistributedSystem");
        Mockito.when(cache.getDistributedSystem()).thenReturn(system);
        Mockito.when(system.isConnected()).thenReturn(false);
        Mockito.when(cache.isReconnecting()).thenReturn(false);
        ServerLauncher launcher = new Builder().setMemberName("serverOne").build();
        launcher.running.set(true);
        assertThat(launcher.isWaiting(cache)).isFalse();
    }

    @Test
    public void isWaitingReturnsFalseByDefault() {
        ServerLauncher launcher = new Builder().build();
        assertThat(launcher.isWaiting(null)).isFalse();
    }

    @Test
    public void isWaitingReturnsFalseWhenNotRunning() {
        ServerLauncher launcher = new Builder().build();
        launcher.running.set(false);
        assertThat(launcher.isWaiting(null)).isFalse();
    }

    @Test
    public void isDisableDefaultServerReturnsFalseByDefault() {
        ServerLauncher launcher = new Builder().build();
        assertThat(launcher.isDisableDefaultServer()).isFalse();
    }

    @Test
    public void isDefaultServerEnabledForCacheReturnsTrueByDefault() {
        Cache cache = Mockito.mock(Cache.class, "Cache");
        ServerLauncher launcher = new Builder().build();
        assertThat(launcher.isDefaultServerEnabled(cache)).isTrue();
    }

    @Test
    public void isDefaultServerEnabledForNullThrowsNullPointerException() {
        ServerLauncher launcher = new Builder().build();
        assertThatThrownBy(() -> launcher.isDefaultServerEnabled(null)).isInstanceOf(NullPointerException.class);
    }

    @Test
    public void isDefaultServerEnabledReturnsFalseWhenCacheServersExist() {
        Cache cache = Mockito.mock(Cache.class, "Cache");
        CacheServer cacheServer = Mockito.mock(CacheServer.class, "CacheServer");
        Mockito.when(cache.getCacheServers()).thenReturn(Collections.singletonList(cacheServer));
        ServerLauncher launcher = new Builder().build();
        assertThat(launcher.isDefaultServerEnabled(cache)).isFalse();
    }

    @Test
    public void isDisableDefaultServerReturnsTrueWhenDisabled() {
        ServerLauncher launcher = new Builder().setDisableDefaultServer(true).build();
        assertThat(launcher.isDisableDefaultServer()).isTrue();
    }

    @Test
    public void isDefaultServerEnabledReturnsFalseWhenDefaultServerDisabledIsTrueAndNoCacheServersExist() {
        Cache cache = Mockito.mock(Cache.class, "Cache");
        Mockito.when(cache.getCacheServers()).thenReturn(Collections.emptyList());
        ServerLauncher launcher = new Builder().setDisableDefaultServer(true).build();
        assertThat(launcher.isDefaultServerEnabled(cache)).isFalse();
    }

    @Test
    public void isDefaultServerEnabledReturnsFalseWhenDefaultServerDisabledIsTrueAndCacheServersExist() {
        Cache cache = Mockito.mock(Cache.class, "Cache");
        CacheServer cacheServer = Mockito.mock(CacheServer.class, "CacheServer");
        Mockito.when(cache.getCacheServers()).thenReturn(Collections.singletonList(cacheServer));
        ServerLauncher launcher = new Builder().setDisableDefaultServer(true).build();
        assertThat(launcher.isDefaultServerEnabled(cache)).isFalse();
    }

    @Test
    public void startCacheServerStartsCacheServerWithBuilderValues() throws IOException {
        Cache cache = Mockito.mock(Cache.class, "Cache");
        CacheServer cacheServer = Mockito.mock(CacheServer.class, "CacheServer");
        Mockito.when(cache.getCacheServers()).thenReturn(Collections.emptyList());
        Mockito.when(cache.addCacheServer()).thenReturn(cacheServer);
        ServerLauncher launcher = new Builder().setServerBindAddress(null).setServerPort(11235).setMaxThreads(10).setMaxConnections(100).setMaxMessageCount(5).setMessageTimeToLive(10000).setSocketBufferSize(2048).setHostNameForClients("hostName4Clients").build();
        launcher.startCacheServer(cache);
        Mockito.verify(cacheServer, Mockito.times(1)).setBindAddress(null);
        Mockito.verify(cacheServer, Mockito.times(1)).setPort(ArgumentMatchers.eq(11235));
        Mockito.verify(cacheServer, Mockito.times(1)).setMaxThreads(10);
        Mockito.verify(cacheServer, Mockito.times(1)).setMaxConnections(100);
        Mockito.verify(cacheServer, Mockito.times(1)).setMaximumMessageCount(5);
        Mockito.verify(cacheServer, Mockito.times(1)).setMessageTimeToLive(10000);
        Mockito.verify(cacheServer, Mockito.times(1)).setSocketBufferSize(2048);
        Mockito.verify(cacheServer, Mockito.times(1)).setHostnameForClients("hostName4Clients");
        Mockito.verify(cacheServer, Mockito.times(1)).start();
    }

    @Test
    public void startCacheServerDoesNothingWhenDefaultServerDisabled() throws IOException {
        Cache cache = Mockito.mock(Cache.class, "Cache");
        CacheServer cacheServer = Mockito.mock(CacheServer.class, "CacheServer");
        Mockito.when(cache.getCacheServers()).thenReturn(Collections.emptyList());
        Mockito.when(cache.addCacheServer()).thenReturn(cacheServer);
        ServerLauncher launcher = new Builder().setDisableDefaultServer(true).build();
        launcher.startCacheServer(cache);
        Mockito.verify(cacheServer, Mockito.times(0)).setBindAddress(ArgumentMatchers.anyString());
        Mockito.verify(cacheServer, Mockito.times(0)).setPort(ArgumentMatchers.anyInt());
        Mockito.verify(cacheServer, Mockito.times(0)).setMaxThreads(ArgumentMatchers.anyInt());
        Mockito.verify(cacheServer, Mockito.times(0)).setMaxConnections(ArgumentMatchers.anyInt());
        Mockito.verify(cacheServer, Mockito.times(0)).setMaximumMessageCount(ArgumentMatchers.anyInt());
        Mockito.verify(cacheServer, Mockito.times(0)).setMessageTimeToLive(ArgumentMatchers.anyInt());
        Mockito.verify(cacheServer, Mockito.times(0)).setSocketBufferSize(ArgumentMatchers.anyInt());
        Mockito.verify(cacheServer, Mockito.times(0)).setHostnameForClients(ArgumentMatchers.anyString());
        Mockito.verify(cacheServer, Mockito.times(0)).start();
    }

    @Test
    public void startCacheServerDoesNothingWhenCacheServerAlreadyExists() throws IOException {
        Cache cache = Mockito.mock(Cache.class, "Cache");
        CacheServer cacheServer1 = Mockito.mock(CacheServer.class, "CacheServer1");
        CacheServer cacheServer2 = Mockito.mock(CacheServer.class, "CacheServer2");
        Mockito.when(cache.getCacheServers()).thenReturn(Collections.singletonList(cacheServer1));
        Mockito.when(cache.addCacheServer()).thenReturn(cacheServer1);
        ServerLauncher launcher = new Builder().build();
        launcher.startCacheServer(cache);
        Mockito.verify(cacheServer2, Mockito.times(0)).setBindAddress(ArgumentMatchers.anyString());
        Mockito.verify(cacheServer2, Mockito.times(0)).setPort(ArgumentMatchers.anyInt());
        Mockito.verify(cacheServer2, Mockito.times(0)).setMaxThreads(ArgumentMatchers.anyInt());
        Mockito.verify(cacheServer2, Mockito.times(0)).setMaxConnections(ArgumentMatchers.anyInt());
        Mockito.verify(cacheServer2, Mockito.times(0)).setMaximumMessageCount(ArgumentMatchers.anyInt());
        Mockito.verify(cacheServer2, Mockito.times(0)).setMessageTimeToLive(ArgumentMatchers.anyInt());
        Mockito.verify(cacheServer2, Mockito.times(0)).setSocketBufferSize(ArgumentMatchers.anyInt());
        Mockito.verify(cacheServer2, Mockito.times(0)).setHostnameForClients(ArgumentMatchers.anyString());
        Mockito.verify(cacheServer2, Mockito.times(0)).start();
    }
}

