/**
 * Copyright (c) 2008-2019, Hazelcast, Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.hazelcast.cache;


import CacheService.SERVICE_NAME;
import com.hazelcast.cache.impl.HazelcastServerCachingProvider;
import com.hazelcast.cache.impl.event.CachePartitionLostListener;
import com.hazelcast.config.CachePartitionLostListenerConfig;
import com.hazelcast.config.CachePartitionLostListenerConfigReadOnly;
import com.hazelcast.config.CacheSimpleConfig;
import com.hazelcast.config.Config;
import com.hazelcast.config.XmlConfigBuilder;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.spi.EventRegistration;
import com.hazelcast.spi.EventService;
import com.hazelcast.test.AssertTask;
import com.hazelcast.test.HazelcastParallelClassRunner;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.TestHazelcastInstanceFactory;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import java.io.IOException;
import java.net.URL;
import java.util.Collection;
import java.util.EventListener;
import java.util.List;
import javax.cache.CacheManager;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.mockito.Mockito;


@RunWith(HazelcastParallelClassRunner.class)
@Category({ QuickTest.class, ParallelTest.class })
public class CachePartitionLostListenerConfigTest extends HazelcastTestSupport {
    private final URL configUrl = getClass().getClassLoader().getResource("test-hazelcast-jcache-partition-lost-listener.xml");

    @Test
    public void testCachePartitionLostListener_registeredViaImplementationInConfigObject() {
        final String cacheName = "myCache";
        Config config = new Config();
        CacheSimpleConfig cacheConfig = config.getCacheConfig(cacheName);
        CachePartitionLostListener listener = Mockito.mock(CachePartitionLostListener.class);
        cacheConfig.addCachePartitionLostListenerConfig(new CachePartitionLostListenerConfig(listener));
        cacheConfig.setBackupCount(0);
        TestHazelcastInstanceFactory factory = createHazelcastInstanceFactory();
        HazelcastInstance instance = factory.newHazelcastInstance(config);
        HazelcastServerCachingProvider cachingProvider = HazelcastServerCachingProvider.createCachingProvider(instance);
        CacheManager cacheManager = cachingProvider.getCacheManager();
        cacheManager.getCache(cacheName);
        final EventService eventService = HazelcastTestSupport.getNode(instance).getNodeEngine().getEventService();
        HazelcastTestSupport.assertTrueEventually(new AssertTask() {
            @Override
            public void run() throws Exception {
                Collection<EventRegistration> registrations = eventService.getRegistrations(SERVICE_NAME, cacheName);
                Assert.assertFalse(registrations.isEmpty());
            }
        });
    }

    @Test
    public void cacheConfigXmlTest() throws IOException {
        String cacheName = "cacheWithPartitionLostListener";
        Config config = new XmlConfigBuilder(configUrl).build();
        CacheSimpleConfig cacheConfig = config.getCacheConfig(cacheName);
        List<CachePartitionLostListenerConfig> configs = cacheConfig.getPartitionLostListenerConfigs();
        Assert.assertEquals(1, configs.size());
        Assert.assertEquals("DummyCachePartitionLostListenerImpl", configs.get(0).getClassName());
    }

    @Test
    public void testCachePartitionLostListenerConfig_setImplementation() {
        CachePartitionLostListener listener = Mockito.mock(CachePartitionLostListener.class);
        CachePartitionLostListenerConfig listenerConfig = new CachePartitionLostListenerConfig();
        listenerConfig.setImplementation(listener);
        Assert.assertEquals(listener, listenerConfig.getImplementation());
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testCachePartitionLostListenerReadOnlyConfig_withClassName() {
        CachePartitionLostListenerConfigReadOnly readOnly = new CachePartitionLostListenerConfig().getAsReadOnly();
        readOnly.setClassName("com.hz");
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testCachePartitionLostListenerReadOnlyConfig_withImplementation() {
        CachePartitionLostListener listener = Mockito.mock(CachePartitionLostListener.class);
        CachePartitionLostListenerConfig listenerConfig = new CachePartitionLostListenerConfig(listener);
        CachePartitionLostListenerConfigReadOnly readOnly = listenerConfig.getAsReadOnly();
        Assert.assertEquals(listener, readOnly.getImplementation());
        readOnly.setImplementation(Mockito.mock(CachePartitionLostListener.class));
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testCachePartitionLostListenerReadOnlyConfig_withEventListenerImplementation() {
        CachePartitionLostListenerConfigReadOnly readOnly = new CachePartitionLostListenerConfig().getAsReadOnly();
        readOnly.setImplementation(Mockito.mock(EventListener.class));
    }

    @Test
    public void testGetImplementation() {
        CachePartitionLostListener listener = new CachePartitionLostListenerTest.EventCollectingCachePartitionLostListener(0);
        CachePartitionLostListenerConfig listenerConfig = new CachePartitionLostListenerConfig(listener);
        Assert.assertEquals(listener, listenerConfig.getImplementation());
    }

    @Test
    public void testGetClassName() {
        String className = "EventCollectingCachePartitionLostListener";
        CachePartitionLostListenerConfig listenerConfig = new CachePartitionLostListenerConfig(className);
        Assert.assertEquals(className, listenerConfig.getClassName());
    }

    @Test
    public void testEqualsAndHashCode() {
        CachePartitionLostListener listener = new CachePartitionLostListenerTest.EventCollectingCachePartitionLostListener(0);
        CachePartitionLostListenerConfig listenerConfig1 = new CachePartitionLostListenerConfig();
        CachePartitionLostListenerConfig listenerConfig2 = new CachePartitionLostListenerConfig();
        Assert.assertEquals(listenerConfig1, listenerConfig1);
        Assert.assertEquals(listenerConfig1, new CachePartitionLostListenerConfig());
        Assert.assertNotEquals(listenerConfig1, null);
        Assert.assertNotEquals(listenerConfig1, new Object());
        listenerConfig1.setImplementation(listener);
        Assert.assertNotEquals(listenerConfig1, listenerConfig2);
        Assert.assertNotEquals(listenerConfig1.hashCode(), listenerConfig2.hashCode());
        listenerConfig2.setImplementation(listener);
        Assert.assertEquals(listenerConfig1, listenerConfig2);
        Assert.assertEquals(listenerConfig1.hashCode(), listenerConfig2.hashCode());
        listenerConfig1.setClassName("EventCollectingCachePartitionLostListener");
        listenerConfig2.setClassName("CachePartitionLostListenerConfig");
        Assert.assertNotEquals(listenerConfig1, listenerConfig2);
        Assert.assertNotEquals(listenerConfig1.hashCode(), listenerConfig2.hashCode());
        listenerConfig2.setClassName("EventCollectingCachePartitionLostListener");
        Assert.assertEquals(listenerConfig1, listenerConfig2);
        Assert.assertEquals(listenerConfig1.hashCode(), listenerConfig2.hashCode());
    }
}

