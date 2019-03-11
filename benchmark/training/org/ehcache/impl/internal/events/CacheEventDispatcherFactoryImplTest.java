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
package org.ehcache.impl.internal.events;


import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import org.ehcache.core.events.CacheEventDispatcher;
import org.ehcache.core.spi.service.ExecutionService;
import org.ehcache.core.spi.store.Store;
import org.ehcache.impl.config.event.DefaultCacheEventDispatcherConfiguration;
import org.ehcache.impl.events.CacheEventDispatcherImpl;
import org.ehcache.spi.service.Service;
import org.ehcache.spi.service.ServiceProvider;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


/**
 * CacheEventDispatcherFactoryImplTest
 */
public class CacheEventDispatcherFactoryImplTest {
    @Test
    public void testConfigurationOfThreadPoolAlias() {
        @SuppressWarnings("unchecked")
        ServiceProvider<Service> serviceProvider = Mockito.mock(ServiceProvider.class);
        Mockito.when(serviceProvider.getService(ExecutionService.class)).thenReturn(Mockito.mock(ExecutionService.class));
        CacheEventDispatcherFactoryImpl factory = new CacheEventDispatcherFactoryImpl();
        factory.start(serviceProvider);
        DefaultCacheEventDispatcherConfiguration config = Mockito.spy(new DefaultCacheEventDispatcherConfiguration("aName"));
        @SuppressWarnings("unchecked")
        Store<Object, Object> store = Mockito.mock(Store.class);
        factory.createCacheEventDispatcher(store, config);
        Mockito.verify(config).getThreadPoolAlias();
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testCreateCacheEventDispatcherReturnsDisabledDispatcherWhenNoThreadPool() throws Exception {
        ServiceProvider<Service> serviceProvider = Mockito.mock(ServiceProvider.class);
        ExecutionService executionService = Mockito.mock(ExecutionService.class);
        Mockito.when(serviceProvider.getService(ExecutionService.class)).thenReturn(executionService);
        Mockito.when(executionService.getOrderedExecutor(ArgumentMatchers.eq("myAlias"), ArgumentMatchers.any(BlockingQueue.class))).thenThrow(IllegalArgumentException.class);
        Mockito.when(executionService.getUnorderedExecutor(ArgumentMatchers.eq("myAlias"), ArgumentMatchers.any(BlockingQueue.class))).thenThrow(IllegalArgumentException.class);
        CacheEventDispatcherFactoryImpl cacheEventDispatcherFactory = new CacheEventDispatcherFactoryImpl();
        cacheEventDispatcherFactory.start(serviceProvider);
        @SuppressWarnings("unchecked")
        Store<Object, Object> store = Mockito.mock(Store.class);
        try {
            cacheEventDispatcherFactory.createCacheEventDispatcher(store, new DefaultCacheEventDispatcherConfiguration("myAlias"));
            Assert.fail("expected IllegalArgumentException");
        } catch (IllegalArgumentException iae) {
            // expected
        }
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testCreateCacheEventReturnsDisabledDispatcherWhenThreadPoolFound() throws Exception {
        ServiceProvider<Service> serviceProvider = Mockito.mock(ServiceProvider.class);
        ExecutionService executionService = Mockito.mock(ExecutionService.class);
        Mockito.when(serviceProvider.getService(ExecutionService.class)).thenReturn(executionService);
        Mockito.when(executionService.getOrderedExecutor(ArgumentMatchers.eq("myAlias"), ArgumentMatchers.any(BlockingQueue.class))).thenReturn(Mockito.mock(ExecutorService.class));
        Mockito.when(executionService.getUnorderedExecutor(ArgumentMatchers.eq("myAlias"), ArgumentMatchers.any(BlockingQueue.class))).thenReturn(Mockito.mock(ExecutorService.class));
        CacheEventDispatcherFactoryImpl cacheEventDispatcherFactory = new CacheEventDispatcherFactoryImpl();
        cacheEventDispatcherFactory.start(serviceProvider);
        Store<Object, Object> store = Mockito.mock(Store.class);
        CacheEventDispatcher<Object, Object> dispatcher = cacheEventDispatcherFactory.createCacheEventDispatcher(store, new DefaultCacheEventDispatcherConfiguration("myAlias"));
        Assert.assertThat(dispatcher, Matchers.instanceOf(CacheEventDispatcherImpl.class));
    }
}

