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


import java.util.Collections;
import java.util.List;
import org.apache.geode.cache.Cache;
import org.apache.geode.cache.server.CacheServer;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;


public class ServerLauncherWaitOnServerTest {
    @Mock
    DistributedSystem system;

    @Mock
    Cache cache;

    private ServerLauncher serverLauncher;

    @Test(timeout = 60000)
    public void returnsWhenLauncherIsRunningAndSystemIsNotConnected() {
        serverLauncher.running.set(true);
        Mockito.when(cache.getCacheServers()).thenReturn(Collections.emptyList());
        Mockito.when(cache.isReconnecting()).thenReturn(false);
        // Connected for a while...
        Mockito.when(system.isConnected()).thenReturn(true, true, true).thenReturn(false);// ... then not

        serverLauncher.waitOnServer();
        // Four times: false, false, false, true
        Mockito.verify(system, Mockito.times(4)).isConnected();
    }

    @Test(timeout = 60000)
    public void returnsWhenLauncherIsRunningAndCacheIsNotReconnecting() {
        serverLauncher.running.set(true);
        Mockito.when(cache.getCacheServers()).thenReturn(Collections.emptyList());
        Mockito.when(system.isConnected()).thenReturn(false);
        // Reconnecting for a while...
        Mockito.when(cache.isReconnecting()).thenReturn(true, true, true).thenReturn(false);// ... then not

        serverLauncher.waitOnServer();
        // Four times: true, true, true, false
        Mockito.verify(cache, Mockito.times(4)).isReconnecting();
    }

    @Test(timeout = 60000)
    public void returnsWhenLauncherIsNotRunning() {
        Mockito.when(cache.getCacheServers()).thenReturn(Collections.emptyList());
        Mockito.when(system.isConnected()).thenReturn(true);
        Mockito.when(cache.isReconnecting()).thenReturn(false);
        // Running at first...
        serverLauncher.running.set(true);
        // Using isReconnecting() as a test hook to change running state
        // while we're in the while loop.
        Mockito.when(cache.isReconnecting()).thenReturn(false, false, false).thenAnswer(( invocation) -> {
            serverLauncher.running.set(false);// ... then not running

            return false;
        });
        serverLauncher.waitOnServer();
        assertThat(serverLauncher.isRunning()).isFalse();
    }

    @Test(timeout = 60000)
    public void returnsImmediatelyIfCacheHasServers() {
        serverLauncher.running.set(true);
        List<CacheServer> servers = Collections.singletonList(Mockito.mock(CacheServer.class));
        Mockito.when(cache.getCacheServers()).thenReturn(servers);
        Mockito.when(system.isConnected()).thenReturn(true);
        Mockito.when(cache.isReconnecting()).thenReturn(false);
        serverLauncher.waitOnServer();
    }
}

