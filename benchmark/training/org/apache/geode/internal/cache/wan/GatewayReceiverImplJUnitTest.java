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
package org.apache.geode.internal.cache.wan;


import java.io.IOException;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.concurrent.atomic.AtomicInteger;
import org.apache.geode.distributed.internal.InternalDistributedSystem;
import org.apache.geode.internal.cache.CacheServerImpl;
import org.apache.geode.internal.cache.InternalCache;
import org.apache.geode.internal.net.SocketCreator;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import static org.apache.geode.internal.Assert.assertTrue;


public class GatewayReceiverImplJUnitTest {
    @Test
    public void getHostOnUnstartedGatewayShouldReturnLocalhost() throws UnknownHostException {
        InternalCache cache = Mockito.mock(InternalCache.class);
        GatewayReceiverImpl gateway = new GatewayReceiverImpl(cache, 2000, 2001, 5, 100, null, null, null, true);
        Assert.assertEquals(SocketCreator.getLocalHost().getHostName(), gateway.getHost());
    }

    @Test
    public void getHostOnRunningGatewayShouldReturnCacheServerAddress() throws IOException {
        InternalCache cache = Mockito.mock(InternalCache.class);
        CacheServerImpl server = Mockito.mock(CacheServerImpl.class);
        InternalDistributedSystem system = Mockito.mock(InternalDistributedSystem.class);
        Mockito.when(cache.getInternalDistributedSystem()).thenReturn(system);
        Mockito.when(server.getExternalAddress()).thenReturn("hello");
        Mockito.when(cache.addCacheServer(ArgumentMatchers.eq(true))).thenReturn(server);
        GatewayReceiverImpl gateway = new GatewayReceiverImpl(cache, 2000, 2001, 5, 100, null, null, null, true);
        gateway.start();
        Assert.assertEquals("hello", gateway.getHost());
    }

    @Test
    public void destroyCalledOnRunningGatewayReceiverShouldThrowException() throws IOException {
        InternalCache cache = Mockito.mock(InternalCache.class);
        CacheServerImpl server = Mockito.mock(CacheServerImpl.class);
        InternalDistributedSystem system = Mockito.mock(InternalDistributedSystem.class);
        Mockito.when(cache.getInternalDistributedSystem()).thenReturn(system);
        Mockito.when(server.getExternalAddress()).thenReturn("hello");
        Mockito.when(server.isRunning()).thenReturn(true);
        Mockito.when(cache.addCacheServer(ArgumentMatchers.eq(true))).thenReturn(server);
        GatewayReceiverImpl gateway = new GatewayReceiverImpl(cache, 2000, 2001, 5, 100, null, null, null, true);
        gateway.start();
        try {
            gateway.destroy();
            Assert.fail();
        } catch (GatewayReceiverException e) {
            Assert.assertEquals("Gateway Receiver is running and needs to be stopped first", e.getMessage());
        }
    }

    @Test
    public void destroyCalledOnStoppedGatewayReceiverShouldRemoveReceiverFromCacheServers() throws IOException {
        InternalCache cache = Mockito.mock(InternalCache.class);
        CacheServerImpl server = Mockito.mock(CacheServerImpl.class);
        InternalDistributedSystem system = Mockito.mock(InternalDistributedSystem.class);
        Mockito.when(cache.getInternalDistributedSystem()).thenReturn(system);
        Mockito.when(server.getExternalAddress()).thenReturn("hello");
        Mockito.when(cache.addCacheServer(ArgumentMatchers.eq(true))).thenReturn(server);
        GatewayReceiverImpl gateway = new GatewayReceiverImpl(cache, 2000, 2001, 5, 100, null, null, null, true);
        gateway.start();
        // sender is mocked already to say running is false
        gateway.destroy();
        Mockito.verify(cache, Mockito.times(1)).removeCacheServer(server);
    }

    @Test
    public void destroyCalledOnStoppedGatewayReceiverShouldRemoveReceiverFromReceivers() throws IOException {
        InternalCache cache = Mockito.mock(InternalCache.class);
        CacheServerImpl server = Mockito.mock(CacheServerImpl.class);
        InternalDistributedSystem system = Mockito.mock(InternalDistributedSystem.class);
        Mockito.when(cache.getInternalDistributedSystem()).thenReturn(system);
        Mockito.when(server.getExternalAddress()).thenReturn("hello");
        Mockito.when(cache.addCacheServer(ArgumentMatchers.eq(true))).thenReturn(server);
        GatewayReceiverImpl gateway = new GatewayReceiverImpl(cache, 2000, 2001, 5, 100, null, null, null, true);
        gateway.start();
        // sender is mocked already to say running is false
        gateway.destroy();
        Mockito.verify(cache, Mockito.times(1)).removeGatewayReceiver(gateway);
    }

    @Test
    public void testFailToStartWith2NextPorts() throws IOException {
        InternalCache cache = Mockito.mock(InternalCache.class);
        CacheServerImpl server = Mockito.mock(CacheServerImpl.class);
        Mockito.when(cache.addCacheServer(ArgumentMatchers.eq(true))).thenReturn(server);
        Mockito.doThrow(new SocketException("Address already in use")).when(server).start();
        GatewayReceiverImpl gateway = new GatewayReceiverImpl(cache, 2000, 2001, 5, 100, null, null, null, true);
        assertThatThrownBy(() -> gateway.start()).isInstanceOf(GatewayReceiverException.class).hasMessageContaining("No available free port found in the given range");
        Mockito.verify(server, Mockito.times(2)).start();
    }

    @Test
    public void testFailToStartWithSamePort() throws IOException {
        InternalCache cache = Mockito.mock(InternalCache.class);
        CacheServerImpl server = Mockito.mock(CacheServerImpl.class);
        Mockito.when(cache.addCacheServer(ArgumentMatchers.eq(true))).thenReturn(server);
        Mockito.doThrow(new SocketException("Address already in use")).when(server).start();
        GatewayReceiverImpl gateway = new GatewayReceiverImpl(cache, 2000, 2000, 5, 100, null, null, null, true);
        assertThatThrownBy(() -> gateway.start()).isInstanceOf(GatewayReceiverException.class).hasMessageContaining("No available free port found in the given range");
        Mockito.verify(server, Mockito.times(1)).start();
    }

    @Test
    public void testFailToStartWithARangeOfPorts() throws IOException {
        InternalCache cache = Mockito.mock(InternalCache.class);
        CacheServerImpl server = Mockito.mock(CacheServerImpl.class);
        Mockito.when(cache.addCacheServer(ArgumentMatchers.eq(true))).thenReturn(server);
        Mockito.doThrow(new SocketException("Address already in use")).when(server).start();
        GatewayReceiverImpl gateway = new GatewayReceiverImpl(cache, 2000, 2100, 5, 100, null, null, null, true);
        assertThatThrownBy(() -> gateway.start()).isInstanceOf(GatewayReceiverException.class).hasMessageContaining("No available free port found in the given range");
        assertTrue(((gateway.getPort()) == 0));
        Mockito.verify(server, Mockito.times(101)).start();// 2000-2100: contains 101 ports

    }

    @Test
    public void testSuccessToStartAtSpecifiedPort() throws IOException {
        InternalCache cache = Mockito.mock(InternalCache.class);
        CacheServerImpl server = Mockito.mock(CacheServerImpl.class);
        InternalDistributedSystem system = Mockito.mock(InternalDistributedSystem.class);
        Mockito.when(cache.getInternalDistributedSystem()).thenReturn(system);
        Mockito.when(cache.addCacheServer(ArgumentMatchers.eq(true))).thenReturn(server);
        AtomicInteger callCount = new AtomicInteger();
        Mockito.doAnswer(( invocation) -> {
            // only throw IOException for 2 times
            if ((callCount.get()) < 2) {
                callCount.incrementAndGet();
                throw new SocketException("Address already in use");
            }
            return 0;
        }).when(server).start();
        GatewayReceiverImpl gateway = new GatewayReceiverImpl(cache, 2000, 2010, 5, 100, null, null, null, true);
        gateway.start();
        assertTrue(((gateway.getPort()) >= 2000));
        Assert.assertEquals(2, callCount.get());
        Mockito.verify(server, Mockito.times(3)).start();// 2 failed tries, 1 succeeded

    }
}

