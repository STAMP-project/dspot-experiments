/**
 * Copyright Terracotta, Inc.
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
package org.ehcache.xml.service;


import com.pany.ehcache.integration.TestCacheEventListener;
import java.util.EnumSet;
import java.util.List;
import org.ehcache.config.CacheConfiguration;
import org.ehcache.config.builders.CacheConfigurationBuilder;
import org.ehcache.config.builders.ResourcePoolsBuilder;
import org.ehcache.core.spi.service.ServiceUtils;
import org.ehcache.event.EventFiring;
import org.ehcache.event.EventOrdering;
import org.ehcache.event.EventType;
import org.ehcache.impl.config.event.DefaultCacheEventListenerConfiguration;
import org.ehcache.xml.XmlConfiguration;
import org.ehcache.xml.exceptions.XmlConfigurationException;
import org.ehcache.xml.model.CacheType;
import org.ehcache.xml.model.EventFiringType;
import org.ehcache.xml.model.EventOrderingType;
import org.ehcache.xml.model.ListenersType;
import org.junit.Test;

import static org.ehcache.xml.model.EventType.CREATED;
import static org.ehcache.xml.model.EventType.REMOVED;


public class DefaultCacheEventListenerConfigurationParserTest {
    @Test
    public void parseServiceConfiguration() throws Exception {
        CacheConfiguration<?, ?> cacheConfiguration = new XmlConfiguration(getClass().getResource("/configs/ehcache-cacheEventListener.xml")).getCacheConfigurations().get("bar");
        DefaultCacheEventListenerConfiguration listenerConfig = ServiceUtils.findSingletonAmongst(DefaultCacheEventListenerConfiguration.class, cacheConfiguration.getServiceConfigurations());
        assertThat(listenerConfig).isNotNull();
        assertThat(listenerConfig.getClazz()).isEqualTo(TestCacheEventListener.class);
        assertThat(listenerConfig.firingMode()).isEqualTo(EventFiring.SYNCHRONOUS);
        assertThat(listenerConfig.orderingMode()).isEqualTo(EventOrdering.UNORDERED);
        assertThat(listenerConfig.fireOn()).containsExactlyInAnyOrder(EventType.values());
    }

    @Test
    public void unparseServiceConfiguration() {
        DefaultCacheEventListenerConfiguration listenerConfig = new DefaultCacheEventListenerConfiguration(EnumSet.of(EventType.CREATED, EventType.REMOVED), TestCacheEventListener.class);
        listenerConfig.setEventFiringMode(EventFiring.SYNCHRONOUS);
        listenerConfig.setEventOrderingMode(EventOrdering.UNORDERED);
        CacheConfiguration<?, ?> cacheConfig = CacheConfigurationBuilder.newCacheConfigurationBuilder(Object.class, Object.class, ResourcePoolsBuilder.heap(10)).add(listenerConfig).build();
        CacheType cacheType = new CacheType();
        cacheType = new DefaultCacheEventListenerConfigurationParser().unparseServiceConfiguration(cacheConfig, cacheType);
        List<ListenersType.Listener> listeners = cacheType.getListeners().getListener();
        assertThat(listeners).hasSize(1);
        ListenersType.Listener listener = listeners.get(0);
        assertThat(listener.getEventFiringMode()).isEqualTo(EventFiringType.SYNCHRONOUS);
        assertThat(listener.getEventOrderingMode()).isEqualTo(EventOrderingType.UNORDERED);
        assertThat(listener.getEventsToFireOn()).contains(CREATED, REMOVED);
    }

    @Test
    public void unparseServiceConfigurationWithInstance() {
        TestCacheEventListener testCacheEventListener = new TestCacheEventListener();
        DefaultCacheEventListenerConfiguration listenerConfig = new DefaultCacheEventListenerConfiguration(EnumSet.of(EventType.CREATED, EventType.REMOVED), testCacheEventListener);
        listenerConfig.setEventFiringMode(EventFiring.SYNCHRONOUS);
        listenerConfig.setEventOrderingMode(EventOrdering.UNORDERED);
        CacheConfiguration<?, ?> cacheConfig = CacheConfigurationBuilder.newCacheConfigurationBuilder(Object.class, Object.class, ResourcePoolsBuilder.heap(10)).add(listenerConfig).build();
        CacheType cacheType = new CacheType();
        assertThatExceptionOfType(XmlConfigurationException.class).isThrownBy(() -> new DefaultCacheEventListenerConfigurationParser().unparseServiceConfiguration(cacheConfig, cacheType)).withMessage("%s", ("XML translation for instance based initialization for " + "DefaultCacheEventListenerConfiguration is not supported"));
    }
}

