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
package org.apache.geode.internal.cache;


import java.io.NotSerializableException;
import java.util.Properties;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ThreadPoolExecutor;
import org.apache.geode.SerializationException;
import org.apache.geode.cache.CacheClosedException;
import org.apache.geode.cache.server.CacheServer;
import org.apache.geode.cache.wan.GatewayReceiver;
import org.apache.geode.distributed.internal.InternalDistributedSystem;
import org.apache.geode.internal.SystemTimer;
import org.apache.geode.internal.cache.control.InternalResourceManager;
import org.apache.geode.internal.cache.eviction.HeapEvictor;
import org.apache.geode.internal.cache.eviction.OffHeapEvictor;
import org.apache.geode.test.fake.Fakes;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

import static GemFireCacheImpl.EVENT_THREAD_LIMIT;
import static GemFireCacheImpl.PURGE_INTERVAL;


/**
 * Unit tests for {@link GemFireCacheImpl}.
 */
public class GemFireCacheImplTest {
    private GemFireCacheImpl gemFireCacheImpl;

    @Test
    public void canBeMocked() {
        GemFireCacheImpl mockGemFireCacheImpl = Mockito.mock(GemFireCacheImpl.class);
        InternalResourceManager mockInternalResourceManager = Mockito.mock(InternalResourceManager.class);
        Mockito.when(mockGemFireCacheImpl.getInternalResourceManager()).thenReturn(mockInternalResourceManager);
        assertThat(mockGemFireCacheImpl.getInternalResourceManager()).isSameAs(mockInternalResourceManager);
    }

    @Test
    public void checkPurgeCCPTimer() {
        SystemTimer cacheClientProxyTimer = Mockito.mock(SystemTimer.class);
        gemFireCacheImpl = GemFireCacheImplTest.createGemFireCacheWithTypeRegistry();
        gemFireCacheImpl.setCCPTimer(cacheClientProxyTimer);
        for (int i = 1; i < (PURGE_INTERVAL); i++) {
            gemFireCacheImpl.purgeCCPTimer();
            Mockito.verify(cacheClientProxyTimer, Mockito.times(0)).timerPurge();
        }
        gemFireCacheImpl.purgeCCPTimer();
        Mockito.verify(cacheClientProxyTimer, Mockito.times(1)).timerPurge();
        for (int i = 1; i < (PURGE_INTERVAL); i++) {
            gemFireCacheImpl.purgeCCPTimer();
            Mockito.verify(cacheClientProxyTimer, Mockito.times(1)).timerPurge();
        }
        gemFireCacheImpl.purgeCCPTimer();
        Mockito.verify(cacheClientProxyTimer, Mockito.times(2)).timerPurge();
    }

    @Test
    public void checkEvictorsClosed() {
        HeapEvictor heapEvictor = Mockito.mock(HeapEvictor.class);
        OffHeapEvictor offHeapEvictor = Mockito.mock(OffHeapEvictor.class);
        gemFireCacheImpl = GemFireCacheImplTest.createGemFireCacheWithTypeRegistry();
        gemFireCacheImpl.setHeapEvictor(heapEvictor);
        gemFireCacheImpl.setOffHeapEvictor(offHeapEvictor);
        gemFireCacheImpl.close();
        Mockito.verify(heapEvictor).close();
        Mockito.verify(offHeapEvictor).close();
    }

    @Test
    public void registerPdxMetaDataThrowsIfInstanceNotSerializable() {
        gemFireCacheImpl = GemFireCacheImplTest.createGemFireCacheWithTypeRegistry();
        assertThatThrownBy(() -> gemFireCacheImpl.registerPdxMetaData(new Object())).isInstanceOf(SerializationException.class).hasMessage("Serialization failed").hasCauseInstanceOf(NotSerializableException.class);
    }

    @Test
    public void registerPdxMetaDataThrowsIfInstanceIsNotPDX() {
        gemFireCacheImpl = GemFireCacheImplTest.createGemFireCacheWithTypeRegistry();
        assertThatThrownBy(() -> gemFireCacheImpl.registerPdxMetaData("string")).isInstanceOf(SerializationException.class).hasMessage("The instance is not PDX serializable");
    }

    @Test
    public void checkThatAsyncEventListenersUseAllThreadsInPool() {
        gemFireCacheImpl = GemFireCacheImplTest.createGemFireCacheWithTypeRegistry();
        ThreadPoolExecutor eventThreadPoolExecutor = ((ThreadPoolExecutor) (gemFireCacheImpl.getEventThreadPool()));
        Assert.assertEquals(0, eventThreadPoolExecutor.getCompletedTaskCount());
        Assert.assertEquals(0, eventThreadPoolExecutor.getActiveCount());
        int MAX_THREADS = EVENT_THREAD_LIMIT;
        final CountDownLatch threadLatch = new CountDownLatch(MAX_THREADS);
        for (int i = 1; i <= MAX_THREADS; i++) {
            eventThreadPoolExecutor.execute(() -> {
                threadLatch.countDown();
                try {
                    threadLatch.await();
                } catch (InterruptedException e) {
                }
            });
        }
        await().untilAsserted(() -> assertThat(eventThreadPoolExecutor.getCompletedTaskCount()).isEqualTo(MAX_THREADS));
    }

    @Test
    public void getCacheClosedExceptionWithNoReasonOrCauseGivesExceptionWithoutEither() {
        gemFireCacheImpl = GemFireCacheImplTest.createGemFireCacheImpl();
        CacheClosedException cacheClosedException = gemFireCacheImpl.getCacheClosedException(null, null);
        assertThat(cacheClosedException.getCause()).isNull();
        assertThat(cacheClosedException.getMessage()).isNull();
    }

    @Test
    public void getCacheClosedExceptionWithNoCauseGivesExceptionWithReason() {
        gemFireCacheImpl = GemFireCacheImplTest.createGemFireCacheImpl();
        CacheClosedException cacheClosedException = gemFireCacheImpl.getCacheClosedException("message", null);
        assertThat(cacheClosedException.getCause()).isNull();
        assertThat(cacheClosedException.getMessage()).isEqualTo("message");
    }

    @Test
    public void getCacheClosedExceptionReturnsExceptionWithProvidedCauseAndReason() {
        gemFireCacheImpl = GemFireCacheImplTest.createGemFireCacheImpl();
        Throwable cause = new Throwable();
        CacheClosedException cacheClosedException = gemFireCacheImpl.getCacheClosedException("message", cause);
        assertThat(cacheClosedException.getCause()).isEqualTo(cause);
        assertThat(cacheClosedException.getMessage()).isEqualTo("message");
    }

    @Test
    public void getCacheClosedExceptionWhenCauseGivenButDisconnectExceptionExistsPrefersCause() {
        gemFireCacheImpl = GemFireCacheImplTest.createGemFireCacheImpl();
        gemFireCacheImpl.disconnectCause = new Throwable("disconnectCause");
        Throwable cause = new Throwable();
        CacheClosedException cacheClosedException = gemFireCacheImpl.getCacheClosedException("message", cause);
        assertThat(cacheClosedException.getCause()).isEqualTo(cause);
        assertThat(cacheClosedException.getMessage()).isEqualTo("message");
    }

    @Test
    public void getCacheClosedExceptionWhenNoCauseGivenProvidesDisconnectExceptionIfExists() {
        gemFireCacheImpl = GemFireCacheImplTest.createGemFireCacheImpl();
        Throwable disconnectCause = new Throwable("disconnectCause");
        gemFireCacheImpl.disconnectCause = disconnectCause;
        CacheClosedException cacheClosedException = gemFireCacheImpl.getCacheClosedException("message", null);
        assertThat(cacheClosedException.getCause()).isEqualTo(disconnectCause);
        assertThat(cacheClosedException.getMessage()).isEqualTo("message");
    }

    @Test
    public void getCacheClosedExceptionReturnsExceptionWithProvidedReason() {
        gemFireCacheImpl = GemFireCacheImplTest.createGemFireCacheImpl();
        CacheClosedException cacheClosedException = gemFireCacheImpl.getCacheClosedException("message");
        assertThat(cacheClosedException.getMessage()).isEqualTo("message");
        assertThat(cacheClosedException.getCause()).isNull();
    }

    @Test
    public void getCacheClosedExceptionReturnsExceptionWithNoMessageWhenReasonNotGiven() {
        gemFireCacheImpl = GemFireCacheImplTest.createGemFireCacheImpl();
        CacheClosedException cacheClosedException = gemFireCacheImpl.getCacheClosedException(null);
        assertThat(cacheClosedException.getMessage()).isEqualTo(null);
        assertThat(cacheClosedException.getCause()).isNull();
    }

    @Test
    public void getCacheClosedExceptionReturnsExceptionWithDisconnectCause() {
        gemFireCacheImpl = GemFireCacheImplTest.createGemFireCacheImpl();
        Throwable disconnectCause = new Throwable("disconnectCause");
        gemFireCacheImpl.disconnectCause = disconnectCause;
        CacheClosedException cacheClosedException = gemFireCacheImpl.getCacheClosedException("message");
        assertThat(cacheClosedException.getMessage()).isEqualTo("message");
        assertThat(cacheClosedException.getCause()).isEqualTo(disconnectCause);
    }

    @Test
    public void removeGatewayReceiverShouldRemoveFromReceiversList() {
        GatewayReceiver receiver = Mockito.mock(GatewayReceiver.class);
        gemFireCacheImpl = GemFireCacheImplTest.createGemFireCacheImpl();
        gemFireCacheImpl.addGatewayReceiver(receiver);
        Assert.assertEquals(1, gemFireCacheImpl.getGatewayReceivers().size());
        gemFireCacheImpl.removeGatewayReceiver(receiver);
        Assert.assertEquals(0, gemFireCacheImpl.getGatewayReceivers().size());
    }

    @Test
    public void removeFromCacheServerShouldRemoveFromCacheServersList() {
        gemFireCacheImpl = GemFireCacheImplTest.createGemFireCacheImpl();
        CacheServer cacheServer = gemFireCacheImpl.addCacheServer(false);
        Assert.assertEquals(1, gemFireCacheImpl.getCacheServers().size());
        gemFireCacheImpl.removeCacheServer(cacheServer);
        Assert.assertEquals(0, gemFireCacheImpl.getCacheServers().size());
    }

    @Test
    public void testIsMisConfigured() {
        Properties clusterProps = new Properties();
        Properties serverProps = new Properties();
        // both does not have the key
        Assert.assertFalse(GemFireCacheImpl.isMisConfigured(clusterProps, serverProps, "key"));
        // cluster has the key, not the server
        clusterProps.setProperty("key", "value");
        Assert.assertFalse(GemFireCacheImpl.isMisConfigured(clusterProps, serverProps, "key"));
        clusterProps.setProperty("key", "");
        Assert.assertFalse(GemFireCacheImpl.isMisConfigured(clusterProps, serverProps, "key"));
        // server has the key, not the cluster
        clusterProps.clear();
        serverProps.clear();
        serverProps.setProperty("key", "value");
        Assert.assertTrue(GemFireCacheImpl.isMisConfigured(clusterProps, serverProps, "key"));
        serverProps.setProperty("key", "");
        Assert.assertFalse(GemFireCacheImpl.isMisConfigured(clusterProps, serverProps, "key"));
        // server has the key, not the cluster
        clusterProps.clear();
        serverProps.clear();
        clusterProps.setProperty("key", "");
        serverProps.setProperty("key", "value");
        Assert.assertTrue(GemFireCacheImpl.isMisConfigured(clusterProps, serverProps, "key"));
        serverProps.setProperty("key", "");
        Assert.assertFalse(GemFireCacheImpl.isMisConfigured(clusterProps, serverProps, "key"));
        // server and cluster has the same value
        clusterProps.clear();
        serverProps.clear();
        clusterProps.setProperty("key", "value");
        serverProps.setProperty("key", "value");
        Assert.assertFalse(GemFireCacheImpl.isMisConfigured(clusterProps, serverProps, "key"));
        clusterProps.setProperty("key", "");
        serverProps.setProperty("key", "");
        Assert.assertFalse(GemFireCacheImpl.isMisConfigured(clusterProps, serverProps, "key"));
        // server and cluster has the different value
        clusterProps.clear();
        serverProps.clear();
        clusterProps.setProperty("key", "value1");
        serverProps.setProperty("key", "value2");
        Assert.assertTrue(GemFireCacheImpl.isMisConfigured(clusterProps, serverProps, "key"));
        clusterProps.setProperty("key", "value1");
        serverProps.setProperty("key", "");
        Assert.assertFalse(GemFireCacheImpl.isMisConfigured(clusterProps, serverProps, "key"));
    }

    @Test
    public void clientCacheWouldNotRequestClusterConfig() {
        // we will need to set the value to true so that we can use a mock gemFireCacheImpl
        boolean oldValue = InternalDistributedSystem.ALLOW_MULTIPLE_SYSTEMS;
        InternalDistributedSystem.ALLOW_MULTIPLE_SYSTEMS = true;
        InternalDistributedSystem internalDistributedSystem = Fakes.distributedSystem();
        gemFireCacheImpl = Mockito.mock(GemFireCacheImpl.class);
        Mockito.when(internalDistributedSystem.getCache()).thenReturn(gemFireCacheImpl);
        new InternalCacheBuilder().setIsClient(true).create(internalDistributedSystem);
        Mockito.verify(gemFireCacheImpl, Mockito.times(0)).requestSharedConfiguration();
        Mockito.verify(gemFireCacheImpl, Mockito.times(0)).applyJarAndXmlFromClusterConfig();
        // reset it back to the old value
        InternalDistributedSystem.ALLOW_MULTIPLE_SYSTEMS = oldValue;
    }
}

