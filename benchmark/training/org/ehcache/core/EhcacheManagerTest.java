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
package org.ehcache.core;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import org.ehcache.Cache;
import org.ehcache.CacheManager;
import org.ehcache.CachePersistenceException;
import org.ehcache.PersistentCacheManager;
import org.ehcache.StateTransitionException;
import org.ehcache.Status;
import org.ehcache.UserManagedCache;
import org.ehcache.config.CacheConfiguration;
import org.ehcache.config.Configuration;
import org.ehcache.config.ResourceType;
import org.ehcache.core.config.BaseCacheConfiguration;
import org.ehcache.core.config.DefaultConfiguration;
import org.ehcache.core.config.ResourcePoolsHelper;
import org.ehcache.core.events.CacheEventDispatcher;
import org.ehcache.core.events.CacheEventDispatcherFactory;
import org.ehcache.core.events.CacheEventListenerProvider;
import org.ehcache.core.events.CacheManagerListener;
import org.ehcache.core.spi.service.LocalPersistenceService;
import org.ehcache.core.spi.store.Store;
import org.ehcache.core.util.ClassLoading;
import org.ehcache.spi.loaderwriter.CacheLoaderWriterProvider;
import org.ehcache.spi.loaderwriter.WriteBehindProvider;
import org.ehcache.spi.resilience.ResilienceStrategyProvider;
import org.ehcache.spi.service.MaintainableService;
import org.ehcache.spi.service.Service;
import org.ehcache.spi.service.ServiceConfiguration;
import org.ehcache.spi.service.ServiceCreationConfiguration;
import org.ehcache.spi.service.ServiceProvider;
import org.hamcrest.CoreMatchers;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;

import static org.ehcache.spi.service.MaintainableService.MaintenanceScope.CACHE;


@SuppressWarnings({ "unchecked", "rawtypes" })
public class EhcacheManagerTest {
    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    public void testCanDestroyAndClose() throws Exception {
        CacheConfiguration<Long, String> cacheConfiguration = new BaseCacheConfiguration<>(Long.class, String.class, null, null, null, ResourcePoolsHelper.createHeapOnlyPools(10));
        Store.Provider storeProvider = Mockito.mock(Store.Provider.class);
        Mockito.when(storeProvider.rank(ArgumentMatchers.any(Set.class), ArgumentMatchers.any(Collection.class))).thenReturn(1);
        Store store = Mockito.mock(Store.class);
        CacheEventDispatcherFactory cacheEventNotificationListenerServiceProvider = Mockito.mock(CacheEventDispatcherFactory.class);
        Mockito.when(storeProvider.createStore(ArgumentMatchers.any(Store.Configuration.class), ArgumentMatchers.<ServiceConfiguration>any())).thenReturn(store);
        Mockito.when(store.getConfigurationChangeListeners()).thenReturn(new ArrayList<>());
        Mockito.when(cacheEventNotificationListenerServiceProvider.createCacheEventDispatcher(store)).thenReturn(Mockito.mock(CacheEventDispatcher.class));
        Map<String, CacheConfiguration<?, ?>> caches = EhcacheManagerTest.newCacheMap();
        caches.put("aCache", cacheConfiguration);
        DefaultConfiguration config = new DefaultConfiguration(caches, null);
        PersistentCacheManager cacheManager = new EhcacheManager(config, Arrays.asList(storeProvider, Mockito.mock(CacheLoaderWriterProvider.class), Mockito.mock(WriteBehindProvider.class), cacheEventNotificationListenerServiceProvider, Mockito.mock(CacheEventListenerProvider.class), Mockito.mock(LocalPersistenceService.class), Mockito.mock(ResilienceStrategyProvider.class)));
        cacheManager.init();
        cacheManager.close();
        cacheManager.init();
        cacheManager.close();
        cacheManager.destroy();
        cacheManager.init();
        cacheManager.close();
    }

    @Test
    public void testConstructionThrowsWhenNotBeingToResolveService() {
        Map<String, CacheConfiguration<?, ?>> caches = EhcacheManagerTest.newCacheMap();
        final DefaultConfiguration config = new DefaultConfiguration(caches, null, ((ServiceCreationConfiguration<EhcacheManagerTest.NoSuchService>) (() -> EhcacheManagerTest.NoSuchService.class)));
        try {
            new EhcacheManager(config);
            Assert.fail("Should have thrown...");
        } catch (IllegalStateException e) {
            Assert.assertThat(e.getMessage(), CoreMatchers.containsString(EhcacheManagerTest.NoSuchService.class.getName()));
        }
    }

    @Test
    public void testCreationFailsOnDuplicateServiceCreationConfiguration() {
        Map<String, CacheConfiguration<?, ?>> caches = EhcacheManagerTest.newCacheMap();
        DefaultConfiguration config = new DefaultConfiguration(caches, null, ((ServiceCreationConfiguration<EhcacheManagerTest.NoSuchService>) (() -> EhcacheManagerTest.NoSuchService.class)), ((ServiceCreationConfiguration<EhcacheManagerTest.NoSuchService>) (() -> EhcacheManagerTest.NoSuchService.class)));
        try {
            new EhcacheManager(config);
            Assert.fail("Should have thrown ...");
        } catch (IllegalStateException e) {
            Assert.assertThat(e.getMessage(), CoreMatchers.containsString("NoSuchService"));
        }
    }

    @Test
    public void testStopAllServicesWhenCacheInitializationFails() {
        Map<String, CacheConfiguration<?, ?>> caches = EhcacheManagerTest.newCacheMap();
        caches.put("myCache", Mockito.mock(CacheConfiguration.class));
        DefaultConfiguration config = new DefaultConfiguration(caches, null);
        List<Service> services = minimumCacheManagerServices();
        EhcacheManager cacheManager = new EhcacheManager(config, services);
        Store.Provider storeProvider = ((Store.Provider) (services.get(0)));// because I know it's the first of the list

        try {
            cacheManager.init();
            Assert.fail("Should have thrown...");
        } catch (StateTransitionException ste) {
            Mockito.verify(storeProvider).stop();
        }
    }

    @Test
    public void testNoClassLoaderSpecified() {
        Map<String, CacheConfiguration<?, ?>> caches = EhcacheManagerTest.newCacheMap();
        caches.put("foo", new BaseCacheConfiguration<>(Object.class, Object.class, null, null, null, ResourcePoolsHelper.createHeapOnlyPools()));
        DefaultConfiguration config = new DefaultConfiguration(caches, null);
        final Store.Provider storeProvider = Mockito.mock(Store.Provider.class);
        Mockito.when(storeProvider.rank(ArgumentMatchers.any(Set.class), ArgumentMatchers.any(Collection.class))).thenReturn(1);
        final Store mock = Mockito.mock(Store.class);
        final CacheEventDispatcherFactory cenlProvider = Mockito.mock(CacheEventDispatcherFactory.class);
        final CacheEventDispatcher<Object, Object> cenlServiceMock = Mockito.mock(CacheEventDispatcher.class);
        Mockito.when(cenlProvider.createCacheEventDispatcher(mock)).thenReturn(cenlServiceMock);
        final Collection<Service> services = getServices(storeProvider, cenlProvider);
        Mockito.when(storeProvider.createStore(ArgumentMatchers.<Store.Configuration>any(), ArgumentMatchers.<ServiceConfiguration[]>any())).thenReturn(mock);
        EhcacheManager cacheManager = new EhcacheManager(config, services);
        cacheManager.init();
        Assert.assertSame(ClassLoading.getDefaultClassLoader(), cacheManager.getClassLoader());
        Assert.assertSame(cacheManager.getClassLoader(), cacheManager.getCache("foo", Object.class, Object.class).getRuntimeConfiguration().getClassLoader());
    }

    @Test
    public void testClassLoaderSpecified() {
        ClassLoader cl1 = new ClassLoader() {};
        ClassLoader cl2 = new ClassLoader() {};
        Assert.assertNotSame(cl1, cl2);
        Assert.assertNotSame(cl1.getClass(), cl2.getClass());
        Map<String, CacheConfiguration<?, ?>> caches = EhcacheManagerTest.newCacheMap();
        caches.put("foo1", new BaseCacheConfiguration<>(Object.class, Object.class, null, null, null, ResourcePoolsHelper.createHeapOnlyPools()));
        caches.put("foo2", new BaseCacheConfiguration<>(Object.class, Object.class, null, null, null, ResourcePoolsHelper.createHeapOnlyPools()));
        caches.put("foo3", new BaseCacheConfiguration<>(Object.class, Object.class, null, cl2, null, ResourcePoolsHelper.createHeapOnlyPools()));
        DefaultConfiguration config = new DefaultConfiguration(caches, cl1);
        final Store.Provider storeProvider = Mockito.mock(Store.Provider.class);
        Mockito.when(storeProvider.rank(ArgumentMatchers.any(Set.class), ArgumentMatchers.any(Collection.class))).thenReturn(1);
        final Store mock = Mockito.mock(Store.class);
        final CacheEventDispatcherFactory cenlProvider = Mockito.mock(CacheEventDispatcherFactory.class);
        final CacheEventDispatcher<Object, Object> cenlServiceMock = Mockito.mock(CacheEventDispatcher.class);
        Mockito.when(cenlProvider.createCacheEventDispatcher(mock)).thenReturn(cenlServiceMock);
        final Collection<Service> services = getServices(storeProvider, cenlProvider);
        Mockito.when(storeProvider.createStore(ArgumentMatchers.<Store.Configuration>any(), ArgumentMatchers.<ServiceConfiguration[]>any())).thenReturn(mock);
        EhcacheManager cacheManager = new EhcacheManager(config, services);
        cacheManager.init();
        Assert.assertSame(cl1, cacheManager.getClassLoader());
        Assert.assertSame(cl1, cacheManager.getCache("foo1", Object.class, Object.class).getRuntimeConfiguration().getClassLoader());
        Assert.assertSame(cl1, cacheManager.getCache("foo2", Object.class, Object.class).getRuntimeConfiguration().getClassLoader());
        Assert.assertSame(cl2, cacheManager.getCache("foo3", Object.class, Object.class).getRuntimeConfiguration().getClassLoader());
    }

    @Test
    public void testReturnsNullForNonExistCache() {
        Map<String, CacheConfiguration<?, ?>> caches = EhcacheManagerTest.newCacheMap();
        DefaultConfiguration config = new DefaultConfiguration(caches, null);
        EhcacheManager cacheManager = new EhcacheManager(config, getServices(null, null));
        cacheManager.init();
        Assert.assertThat(cacheManager.getCache("foo", Object.class, Object.class), CoreMatchers.nullValue());
    }

    @Test
    public void testThrowsWhenAddingExistingCache() {
        CacheConfiguration<Object, Object> cacheConfiguration = new BaseCacheConfiguration<>(Object.class, Object.class, null, null, null, ResourcePoolsHelper.createHeapOnlyPools());
        final Store.Provider storeProvider = Mockito.mock(Store.Provider.class);
        Mockito.when(storeProvider.rank(ArgumentMatchers.any(Set.class), ArgumentMatchers.any(Collection.class))).thenReturn(1);
        final Store mock = Mockito.mock(Store.class);
        final CacheEventDispatcherFactory cenlProvider = Mockito.mock(CacheEventDispatcherFactory.class);
        final CacheEventDispatcher<Object, Object> cenlServiceMock = Mockito.mock(CacheEventDispatcher.class);
        Mockito.when(cenlProvider.createCacheEventDispatcher(mock)).thenReturn(cenlServiceMock);
        final Collection<Service> services = getServices(storeProvider, cenlProvider);
        Mockito.when(storeProvider.createStore(ArgumentMatchers.<Store.Configuration>any(), ArgumentMatchers.<ServiceConfiguration[]>any())).thenReturn(mock);
        Map<String, CacheConfiguration<?, ?>> caches = EhcacheManagerTest.newCacheMap();
        caches.put("bar", cacheConfiguration);
        DefaultConfiguration config = new DefaultConfiguration(caches, null);
        EhcacheManager cacheManager = new EhcacheManager(config, services);
        cacheManager.init();
        final Cache<Object, Object> cache = cacheManager.getCache("bar", Object.class, Object.class);
        Assert.assertNotNull(cache);
        try {
            cacheManager.createCache("bar", cacheConfiguration);
            Assert.fail("Should have thrown");
        } catch (IllegalArgumentException e) {
            Assert.assertTrue(e.getMessage().contains("bar"));
        }
    }

    @Test
    public void testThrowsWhenNotInitialized() {
        final Store.Provider storeProvider = Mockito.mock(Store.Provider.class);
        Mockito.when(storeProvider.rank(ArgumentMatchers.any(Set.class), ArgumentMatchers.any(Collection.class))).thenReturn(1);
        final Store mock = Mockito.mock(Store.class);
        final CacheEventDispatcherFactory cenlProvider = Mockito.mock(CacheEventDispatcherFactory.class);
        final CacheEventDispatcher<Object, Object> cenlServiceMock = Mockito.mock(CacheEventDispatcher.class);
        Mockito.when(cenlProvider.createCacheEventDispatcher(mock)).thenReturn(cenlServiceMock);
        final Collection<Service> services = getServices(storeProvider, cenlProvider);
        Mockito.when(storeProvider.createStore(ArgumentMatchers.<Store.Configuration>any(), ArgumentMatchers.<ServiceConfiguration[]>any())).thenReturn(mock);
        final CacheConfiguration<Integer, String> cacheConfiguration = new BaseCacheConfiguration<>(Integer.class, String.class, null, null, null, ResourcePoolsHelper.createHeapOnlyPools());
        Map<String, CacheConfiguration<?, ?>> caches = EhcacheManagerTest.newCacheMap();
        caches.put("bar", cacheConfiguration);
        DefaultConfiguration config = new DefaultConfiguration(caches, null);
        EhcacheManager cacheManager = new EhcacheManager(config, services);
        try {
            cacheManager.removeCache("foo");
            Assert.fail();
        } catch (IllegalStateException e) {
            Assert.assertThat(e.getMessage().contains(Status.UNINITIALIZED.name()), Matchers.is(true));
        }
        try {
            cacheManager.createCache("foo", ((CacheConfiguration) (null)));
            Assert.fail();
        } catch (IllegalStateException e) {
            Assert.assertThat(e.getMessage().contains(Status.UNINITIALIZED.name()), Matchers.is(true));
        }
        try {
            cacheManager.getCache("foo", Object.class, Object.class);
            Assert.fail();
        } catch (IllegalStateException e) {
            Assert.assertThat(e.getMessage().contains(Status.UNINITIALIZED.name()), Matchers.is(true));
        }
    }

    @Test
    public void testThrowsWhenRetrievingCacheWithWrongTypes() {
        final Store.Provider storeProvider = Mockito.mock(Store.Provider.class);
        Mockito.when(storeProvider.rank(ArgumentMatchers.any(Set.class), ArgumentMatchers.any(Collection.class))).thenReturn(1);
        final Store mock = Mockito.mock(Store.class);
        final CacheEventDispatcherFactory cenlProvider = Mockito.mock(CacheEventDispatcherFactory.class);
        final CacheEventDispatcher<Object, Object> cenlServiceMock = Mockito.mock(CacheEventDispatcher.class);
        Mockito.when(cenlProvider.createCacheEventDispatcher(mock)).thenReturn(cenlServiceMock);
        final Collection<Service> services = getServices(storeProvider, cenlProvider);
        Mockito.when(storeProvider.createStore(ArgumentMatchers.<Store.Configuration>any(), ArgumentMatchers.<ServiceConfiguration[]>any())).thenReturn(mock);
        final CacheConfiguration<Integer, String> cacheConfiguration = new BaseCacheConfiguration<>(Integer.class, String.class, null, null, null, ResourcePoolsHelper.createHeapOnlyPools());
        Map<String, CacheConfiguration<?, ?>> caches = EhcacheManagerTest.newCacheMap();
        caches.put("bar", cacheConfiguration);
        DefaultConfiguration config = new DefaultConfiguration(caches, null);
        EhcacheManager cacheManager = new EhcacheManager(config, services);
        cacheManager.init();
        cacheManager.getCache("bar", Integer.class, String.class);
        try {
            cacheManager.getCache("bar", Integer.class, Integer.class);
            Assert.fail("Should have thrown");
        } catch (IllegalArgumentException e) {
            Assert.assertTrue(e.getMessage().contains("bar"));
            Assert.assertTrue(e.getMessage().contains("<java.lang.Integer, java.lang.String>"));
            Assert.assertTrue(e.getMessage().contains("<java.lang.Integer, java.lang.Integer>"));
        }
        try {
            cacheManager.getCache("bar", String.class, String.class);
            Assert.fail("Should have thrown");
        } catch (IllegalArgumentException e) {
            Assert.assertTrue(e.getMessage().contains("bar"));
            Assert.assertTrue(e.getMessage().contains("<java.lang.Integer, java.lang.String>"));
            Assert.assertTrue(e.getMessage().contains("<java.lang.String, java.lang.String>"));
        }
    }

    @Test
    public void testDoesNotifyAboutCache() {
        final CacheConfiguration<Object, Object> cacheConfiguration = new BaseCacheConfiguration<>(Object.class, Object.class, null, null, null, ResourcePoolsHelper.createHeapOnlyPools());
        final Store.Provider mock = Mockito.mock(Store.Provider.class);
        Mockito.when(mock.rank(ArgumentMatchers.any(Set.class), ArgumentMatchers.any(Collection.class))).thenReturn(1);
        final CacheEventDispatcherFactory cenlProvider = Mockito.mock(CacheEventDispatcherFactory.class);
        final CacheEventDispatcher<Object, Object> cenlServiceMock = Mockito.mock(CacheEventDispatcher.class);
        Mockito.when(cenlProvider.createCacheEventDispatcher(ArgumentMatchers.any(Store.class))).thenReturn(cenlServiceMock);
        final Collection<Service> services = getServices(mock, cenlProvider);
        Mockito.when(mock.createStore(ArgumentMatchers.<Store.Configuration>any())).thenReturn(Mockito.mock(Store.class));
        Map<String, CacheConfiguration<?, ?>> caches = EhcacheManagerTest.newCacheMap();
        DefaultConfiguration config = new DefaultConfiguration(caches, null);
        EhcacheManager cacheManager = new EhcacheManager(config, services);
        final CacheManagerListener listener = Mockito.mock(CacheManagerListener.class);
        cacheManager.registerListener(listener);
        cacheManager.init();
        final String cacheAlias = "bar";
        cacheManager.createCache(cacheAlias, cacheConfiguration);
        final Cache<Object, Object> bar = cacheManager.getCache(cacheAlias, Object.class, Object.class);
        Mockito.verify(listener).cacheAdded(cacheAlias, bar);
        cacheManager.removeCache(cacheAlias);
        Mockito.verify(listener).cacheRemoved(cacheAlias, bar);
    }

    @Test
    public void testDoesNotNotifyAboutCacheOnInitOrClose() {
        final CacheConfiguration<Object, Object> cacheConfiguration = new BaseCacheConfiguration<>(Object.class, Object.class, null, null, null, ResourcePoolsHelper.createHeapOnlyPools());
        final Store.Provider mock = Mockito.mock(Store.Provider.class);
        Mockito.when(mock.rank(ArgumentMatchers.any(Set.class), ArgumentMatchers.any(Collection.class))).thenReturn(1);
        final CacheEventDispatcherFactory cenlProvider = Mockito.mock(CacheEventDispatcherFactory.class);
        final CacheEventDispatcher<Object, Object> cenlServiceMock = Mockito.mock(CacheEventDispatcher.class);
        Mockito.when(cenlProvider.createCacheEventDispatcher(ArgumentMatchers.any(Store.class))).thenReturn(cenlServiceMock);
        final Collection<Service> services = getServices(mock, cenlProvider);
        Mockito.when(mock.createStore(ArgumentMatchers.<Store.Configuration>any())).thenReturn(Mockito.mock(Store.class));
        final String cacheAlias = "bar";
        Map<String, CacheConfiguration<?, ?>> caches = EhcacheManagerTest.newCacheMap();
        caches.put(cacheAlias, cacheConfiguration);
        DefaultConfiguration config = new DefaultConfiguration(caches, null);
        EhcacheManager cacheManager = new EhcacheManager(config, services);
        final CacheManagerListener listener = Mockito.mock(CacheManagerListener.class);
        cacheManager.registerListener(listener);
        cacheManager.init();
        final Cache<Object, Object> bar = cacheManager.getCache(cacheAlias, Object.class, Object.class);
        Mockito.verify(listener, Mockito.never()).cacheAdded(cacheAlias, bar);
        cacheManager.close();
        Mockito.verify(listener, Mockito.never()).cacheRemoved(cacheAlias, bar);
    }

    @Test
    public void testClosesStartedCachesDownWhenInitThrows() {
        final Set<Cache<?, ?>> caches = new HashSet<>();
        final CacheConfiguration<Object, Object> cacheConfiguration = new BaseCacheConfiguration<>(Object.class, Object.class, null, null, null, ResourcePoolsHelper.createHeapOnlyPools());
        final Store.Provider storeProvider = Mockito.mock(Store.Provider.class);
        Mockito.when(storeProvider.rank(ArgumentMatchers.any(Set.class), ArgumentMatchers.any(Collection.class))).thenReturn(1);
        final Collection<Service> services = getServices(storeProvider, null);
        final RuntimeException thrown = new RuntimeException();
        Mockito.when(storeProvider.createStore(ArgumentMatchers.<Store.Configuration>any())).thenReturn(Mockito.mock(Store.class));
        Map<String, CacheConfiguration<?, ?>> cacheMap = EhcacheManagerTest.newCacheMap();
        cacheMap.put("foo", cacheConfiguration);
        cacheMap.put("bar", cacheConfiguration);
        cacheMap.put("foobar", cacheConfiguration);
        DefaultConfiguration config = new DefaultConfiguration(cacheMap, null);
        EhcacheManager cacheManager = new EhcacheManager(config, services) {
            @Override
            <K, V> InternalCache<K, V> createNewEhcache(final String alias, final CacheConfiguration<K, V> config, final Class<K> keyType, final Class<V> valueType) {
                final InternalCache<K, V> ehcache = super.createNewEhcache(alias, config, keyType, valueType);
                caches.add(ehcache);
                if ((caches.size()) == 1) {
                    Mockito.when(storeProvider.createStore(ArgumentMatchers.<Store.Configuration<K, V>>any(), ArgumentMatchers.<ServiceConfiguration<?>>any())).thenThrow(thrown);
                }
                return ehcache;
            }

            @Override
            protected void closeEhcache(final String alias, final InternalCache<?, ?> ehcache) {
                super.closeEhcache(alias, ehcache);
                caches.remove(ehcache);
            }
        };
        try {
            cacheManager.init();
            Assert.fail();
        } catch (StateTransitionException e) {
            Assert.assertThat(cacheManager.getStatus(), Matchers.is(Status.UNINITIALIZED));
            final String message = e.getCause().getMessage();
            Assert.assertThat(message, CoreMatchers.startsWith("Cache '"));
            Assert.assertThat(message, CoreMatchers.containsString("' creation in "));
            Assert.assertThat(message, CoreMatchers.endsWith(" failed."));
        }
        Assert.assertThat(caches.isEmpty(), Matchers.is(true));
    }

    @Test
    public void testClosesAllCachesDownWhenCloseThrows() {
        final Set<String> caches = new HashSet<>();
        final CacheConfiguration<Object, Object> cacheConfiguration = new BaseCacheConfiguration<>(Object.class, Object.class, null, null, null, ResourcePoolsHelper.createHeapOnlyPools());
        final Store.Provider storeProvider = Mockito.mock(Store.Provider.class);
        Mockito.when(storeProvider.rank(ArgumentMatchers.any(Set.class), ArgumentMatchers.any(Collection.class))).thenReturn(1);
        final CacheEventDispatcherFactory cenlProvider = Mockito.mock(CacheEventDispatcherFactory.class);
        final CacheEventDispatcher<Object, Object> cenlServiceMock = Mockito.mock(CacheEventDispatcher.class);
        Mockito.when(cenlProvider.createCacheEventDispatcher(ArgumentMatchers.any(Store.class))).thenReturn(cenlServiceMock);
        final Collection<Service> services = getServices(storeProvider, cenlProvider);
        final RuntimeException thrown = new RuntimeException();
        Mockito.when(storeProvider.createStore(ArgumentMatchers.<Store.Configuration>any())).thenReturn(Mockito.mock(Store.class));
        Map<String, CacheConfiguration<?, ?>> cacheMap = EhcacheManagerTest.newCacheMap();
        cacheMap.put("foo", cacheConfiguration);
        cacheMap.put("bar", cacheConfiguration);
        cacheMap.put("foobar", cacheConfiguration);
        DefaultConfiguration config = new DefaultConfiguration(cacheMap, null);
        EhcacheManager cacheManager = new EhcacheManager(config, services) {
            @Override
            <K, V> InternalCache<K, V> createNewEhcache(final String alias, final CacheConfiguration<K, V> config, final Class<K> keyType, final Class<V> valueType) {
                final InternalCache<K, V> ehcache = super.createNewEhcache(alias, config, keyType, valueType);
                caches.add(alias);
                return ehcache;
            }

            @Override
            protected void closeEhcache(final String alias, final InternalCache<?, ?> ehcache) {
                super.closeEhcache(alias, ehcache);
                if (alias.equals("foobar")) {
                    throw thrown;
                }
                caches.remove(alias);
            }
        };
        cacheManager.init();
        try {
            cacheManager.close();
            Assert.fail();
        } catch (StateTransitionException e) {
            Assert.assertThat(cacheManager.getStatus(), Matchers.is(Status.UNINITIALIZED));
            Assert.assertThat(e.getCause(), CoreMatchers.<Throwable>sameInstance(thrown));
        }
        Assert.assertThat(caches.contains("foobar"), Matchers.is(true));
    }

    @Test
    public void testDoesNotifyAboutLifecycle() {
        Map<String, CacheConfiguration<?, ?>> caches = EhcacheManagerTest.newCacheMap();
        DefaultConfiguration config = new DefaultConfiguration(caches, null);
        EhcacheManager cacheManager = new EhcacheManager(config, getServices(null, null));
        final CacheManagerListener listener = Mockito.mock(CacheManagerListener.class);
        cacheManager.registerListener(listener);
        cacheManager.init();
        Mockito.verify(listener).stateTransition(Status.UNINITIALIZED, Status.AVAILABLE);
        cacheManager.close();
        Mockito.verify(listener).stateTransition(Status.AVAILABLE, Status.UNINITIALIZED);
    }

    @Test
    public void testCloseNoLoaderWriterAndCacheEventListener() throws Exception {
        final CacheConfiguration<Object, Object> cacheConfiguration = new BaseCacheConfiguration<>(Object.class, Object.class, null, null, null, ResourcePoolsHelper.createHeapOnlyPools());
        final Store.Provider storeProvider = Mockito.spy(new Store.Provider() {
            @Override
            public int rank(final Set<ResourceType<?>> resourceTypes, final Collection<ServiceConfiguration<?>> serviceConfigs) {
                return 1;
            }

            @Override
            public void stop() {
            }

            @Override
            public void start(ServiceProvider<Service> serviceProvider) {
            }

            @Override
            public void releaseStore(Store<?, ?> resource) {
            }

            @Override
            public void initStore(Store<?, ?> resource) {
            }

            @Override
            public <K, V> Store<K, V> createStore(Store.Configuration<K, V> storeConfig, ServiceConfiguration<?>... serviceConfigs) {
                return null;
            }
        });
        final CacheEventDispatcherFactory cenlProvider = Mockito.spy(new CacheEventDispatcherFactory() {
            @Override
            public void start(ServiceProvider<Service> serviceProvider) {
            }

            @Override
            public void stop() {
            }

            @Override
            public <K, V> CacheEventDispatcher<K, V> createCacheEventDispatcher(Store<K, V> store, ServiceConfiguration<?>... serviceConfigs) {
                return null;
            }

            @Override
            public <K, V> void releaseCacheEventDispatcher(CacheEventDispatcher<K, V> eventDispatcher) {
                eventDispatcher.shutdown();
            }
        });
        Store mockStore = Mockito.mock(Store.class);
        final CacheEventDispatcher<Object, Object> cenlServiceMock = Mockito.mock(CacheEventDispatcher.class);
        Mockito.when(cenlProvider.createCacheEventDispatcher(mockStore)).thenReturn(cenlServiceMock);
        final Collection<Service> services = getServices(storeProvider, cenlProvider);
        List<CacheConfigurationChangeListener> configurationChangeListenerList = new ArrayList<>();
        configurationChangeListenerList.add(Mockito.mock(CacheConfigurationChangeListener.class));
        Mockito.when(mockStore.getConfigurationChangeListeners()).thenReturn(configurationChangeListenerList);
        Mockito.when(storeProvider.createStore(ArgumentMatchers.<Store.Configuration>any())).thenReturn(mockStore);
        Map<String, CacheConfiguration<?, ?>> caches = EhcacheManagerTest.newCacheMap();
        caches.put("foo", cacheConfiguration);
        DefaultConfiguration config = new DefaultConfiguration(caches, null);
        EhcacheManager cacheManager = new EhcacheManager(config, services) {
            @Override
            <K, V> InternalCache<K, V> createNewEhcache(final String alias, final CacheConfiguration<K, V> config, final Class<K> keyType, final Class<V> valueType) {
                final InternalCache<K, V> ehcache = super.createNewEhcache(alias, config, keyType, valueType);
                return Mockito.spy(ehcache);
            }
        };
        cacheManager.init();
        Cache<Object, Object> testCache = cacheManager.getCache("foo", Object.class, Object.class);
        cacheManager.close();
        Mockito.verify(((UserManagedCache) (testCache))).close();
        Mockito.verify(cenlServiceMock, Mockito.times(1)).shutdown();
    }

    @Test
    public void testChangesToManagerAreReflectedInConfig() {
        Store.Provider storeProvider = Mockito.mock(Store.Provider.class);
        Mockito.when(storeProvider.rank(ArgumentMatchers.any(Set.class), ArgumentMatchers.any(Collection.class))).thenReturn(1);
        Store store = Mockito.mock(Store.class);
        CacheEventDispatcherFactory cacheEventNotificationListenerServiceProvider = Mockito.mock(CacheEventDispatcherFactory.class);
        Mockito.when(storeProvider.createStore(ArgumentMatchers.any(Store.Configuration.class), ArgumentMatchers.<ServiceConfiguration>any())).thenReturn(store);
        Mockito.when(store.getConfigurationChangeListeners()).thenReturn(new ArrayList<>());
        Mockito.when(cacheEventNotificationListenerServiceProvider.createCacheEventDispatcher(store)).thenReturn(Mockito.mock(CacheEventDispatcher.class));
        CacheConfiguration<Long, String> cache1Configuration = new BaseCacheConfiguration<>(Long.class, String.class, null, null, null, ResourcePoolsHelper.createHeapOnlyPools());
        Map<String, CacheConfiguration<?, ?>> caches = EhcacheManagerTest.newCacheMap();
        caches.put("cache1", cache1Configuration);
        DefaultConfiguration config = new DefaultConfiguration(caches, null);
        CacheManager cacheManager = new EhcacheManager(config, Arrays.asList(storeProvider, Mockito.mock(CacheLoaderWriterProvider.class), Mockito.mock(WriteBehindProvider.class), cacheEventNotificationListenerServiceProvider, Mockito.mock(CacheEventListenerProvider.class), Mockito.mock(LocalPersistenceService.class), Mockito.mock(ResilienceStrategyProvider.class)));
        cacheManager.init();
        try {
            final CacheConfiguration<Long, String> cache2Configuration = new BaseCacheConfiguration<>(Long.class, String.class, null, null, null, ResourcePoolsHelper.createHeapOnlyPools());
            final Cache<Long, String> cache = cacheManager.createCache("cache2", cache2Configuration);
            final CacheConfiguration<?, ?> cacheConfiguration = cacheManager.getRuntimeConfiguration().getCacheConfigurations().get("cache2");
            Assert.assertThat(cacheConfiguration, Matchers.notNullValue());
            final CacheConfiguration<?, ?> runtimeConfiguration = cache.getRuntimeConfiguration();
            Assert.assertThat((cacheConfiguration == runtimeConfiguration), Matchers.is(true));
            Assert.assertThat(((cacheManager.getRuntimeConfiguration().getCacheConfigurations().get("cache1")) == (cacheManager.getCache("cache1", Long.class, String.class).getRuntimeConfiguration())), Matchers.is(true));
            cacheManager.removeCache("cache1");
            Assert.assertThat(cacheManager.getRuntimeConfiguration().getCacheConfigurations().containsKey("cache1"), Matchers.is(false));
        } finally {
            cacheManager.close();
        }
    }

    @Test
    public void testCachesAddedAtRuntimeGetReInited() {
        Store.Provider storeProvider = Mockito.mock(Store.Provider.class);
        Mockito.when(storeProvider.rank(ArgumentMatchers.any(Set.class), ArgumentMatchers.any(Collection.class))).thenReturn(1);
        Store store = Mockito.mock(Store.class);
        CacheEventDispatcherFactory cacheEventNotificationListenerServiceProvider = Mockito.mock(CacheEventDispatcherFactory.class);
        Mockito.when(storeProvider.createStore(ArgumentMatchers.any(Store.Configuration.class), ArgumentMatchers.<ServiceConfiguration>any())).thenReturn(store);
        Mockito.when(store.getConfigurationChangeListeners()).thenReturn(new ArrayList<>());
        Mockito.when(cacheEventNotificationListenerServiceProvider.createCacheEventDispatcher(store)).thenReturn(Mockito.mock(CacheEventDispatcher.class));
        CacheConfiguration<Long, String> cache1Configuration = new BaseCacheConfiguration<>(Long.class, String.class, null, null, null, ResourcePoolsHelper.createHeapOnlyPools());
        Map<String, CacheConfiguration<?, ?>> caches = EhcacheManagerTest.newCacheMap();
        caches.put("cache1", cache1Configuration);
        DefaultConfiguration config = new DefaultConfiguration(caches, null);
        CacheManager cacheManager = new EhcacheManager(config, Arrays.asList(storeProvider, Mockito.mock(CacheLoaderWriterProvider.class), Mockito.mock(WriteBehindProvider.class), cacheEventNotificationListenerServiceProvider, Mockito.mock(CacheEventListenerProvider.class), Mockito.mock(LocalPersistenceService.class), Mockito.mock(ResilienceStrategyProvider.class)));
        cacheManager.init();
        CacheConfiguration<Long, String> cache2Configuration = new BaseCacheConfiguration<>(Long.class, String.class, null, null, null, ResourcePoolsHelper.createHeapOnlyPools());
        cacheManager.createCache("cache2", cache2Configuration);
        cacheManager.removeCache("cache1");
        cacheManager.close();
        cacheManager.init();
        try {
            Assert.assertThat(cacheManager.getCache("cache1", Long.class, String.class), CoreMatchers.nullValue());
            Assert.assertThat(cacheManager.getCache("cache2", Long.class, String.class), Matchers.notNullValue());
        } finally {
            cacheManager.close();
        }
    }

    @Test
    public void testCloseWhenRuntimeCacheCreationFails() throws Exception {
        Store.Provider storeProvider = Mockito.mock(Store.Provider.class);
        Mockito.when(storeProvider.rank(ArgumentMatchers.any(Set.class), ArgumentMatchers.any(Collection.class))).thenReturn(1);
        Mockito.doThrow(new Error("Test EhcacheManager close.")).when(storeProvider).createStore(ArgumentMatchers.any(Store.Configuration.class), ArgumentMatchers.<ServiceConfiguration>any());
        Map<String, CacheConfiguration<?, ?>> caches = EhcacheManagerTest.newCacheMap();
        DefaultConfiguration config = new DefaultConfiguration(caches, null);
        final CacheManager cacheManager = new EhcacheManager(config, Arrays.asList(storeProvider, Mockito.mock(CacheLoaderWriterProvider.class), Mockito.mock(WriteBehindProvider.class), Mockito.mock(CacheEventDispatcherFactory.class), Mockito.mock(CacheEventListenerProvider.class), Mockito.mock(LocalPersistenceService.class), Mockito.mock(ResilienceStrategyProvider.class)));
        cacheManager.init();
        CacheConfiguration<Long, String> cacheConfiguration = new BaseCacheConfiguration<>(Long.class, String.class, null, null, null, ResourcePoolsHelper.createHeapOnlyPools());
        try {
            cacheManager.createCache("cache", cacheConfiguration);
            Assert.fail();
        } catch (Error err) {
            Assert.assertThat(err.getMessage(), CoreMatchers.equalTo("Test EhcacheManager close."));
        }
        cacheManager.close();
        Assert.assertThat(cacheManager.getStatus(), Matchers.is(Status.UNINITIALIZED));
    }

    @Test(timeout = 2000L)
    public void testCloseWhenCacheCreationFailsDuringInitialization() throws Exception {
        Store.Provider storeProvider = Mockito.mock(Store.Provider.class);
        Mockito.when(storeProvider.rank(ArgumentMatchers.any(Set.class), ArgumentMatchers.any(Collection.class))).thenReturn(1);
        Mockito.doThrow(new Error("Test EhcacheManager close.")).when(storeProvider).createStore(ArgumentMatchers.any(Store.Configuration.class), ArgumentMatchers.<ServiceConfiguration>any());
        CacheConfiguration<Long, String> cacheConfiguration = new BaseCacheConfiguration<>(Long.class, String.class, null, null, null, ResourcePoolsHelper.createHeapOnlyPools());
        Map<String, CacheConfiguration<?, ?>> caches = EhcacheManagerTest.newCacheMap();
        caches.put("cache1", cacheConfiguration);
        DefaultConfiguration config = new DefaultConfiguration(caches, null);
        final CacheManager cacheManager = new EhcacheManager(config, Arrays.asList(storeProvider, Mockito.mock(CacheLoaderWriterProvider.class), Mockito.mock(WriteBehindProvider.class), Mockito.mock(CacheEventDispatcherFactory.class), Mockito.mock(CacheEventListenerProvider.class), Mockito.mock(LocalPersistenceService.class), Mockito.mock(ResilienceStrategyProvider.class)));
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        Executors.newSingleThreadExecutor().submit(() -> {
            try {
                cacheManager.init();
            } catch (Error err) {
                Assert.assertThat(err.getMessage(), CoreMatchers.equalTo("Test EhcacheManager close."));
                countDownLatch.countDown();
            }
        });
        countDownLatch.await();
        try {
            cacheManager.close();
        } catch (IllegalStateException e) {
            Assert.assertThat(e.getMessage(), Matchers.is("Close not supported from UNINITIALIZED"));
        }
        Assert.assertThat(cacheManager.getStatus(), Matchers.is(Status.UNINITIALIZED));
    }

    @Test
    public void testDestroyCacheFailsIfAlreadyInMaintenanceMode() throws InterruptedException, CachePersistenceException {
        Map<String, CacheConfiguration<?, ?>> caches = EhcacheManagerTest.newCacheMap();
        DefaultConfiguration config = new DefaultConfiguration(caches, null);
        final EhcacheManager manager = new EhcacheManager(config, minimumCacheManagerServices());
        Thread thread = new Thread(() -> manager.getStatusTransitioner().maintenance().succeeded());
        thread.start();
        thread.join(1000);
        expectedException.expect(IllegalStateException.class);
        expectedException.expectMessage("State is MAINTENANCE, yet you don't own it!");
        manager.destroyCache("test");
    }

    @Test
    public void testDestroyCacheFailsAndStopIfStartingServicesFails() throws InterruptedException, CachePersistenceException {
        Map<String, CacheConfiguration<?, ?>> caches = EhcacheManagerTest.newCacheMap();
        DefaultConfiguration config = new DefaultConfiguration(caches, null);
        List<Service> services = minimumCacheManagerServices();
        MaintainableService service = Mockito.mock(MaintainableService.class);
        Mockito.doThrow(new RuntimeException("failed")).when(service).startForMaintenance(Mockito.<ServiceProvider<MaintainableService>>any(), ArgumentMatchers.eq(CACHE));
        services.add(service);
        EhcacheManager manager = new EhcacheManager(config, services);
        expectedException.expect(StateTransitionException.class);
        expectedException.expectMessage("failed");
        manager.destroyCache("test");
        Assert.assertThat(manager.getStatus(), CoreMatchers.equalTo(Status.UNINITIALIZED));
    }

    static class NoSuchService implements Service {
        @Override
        public void start(final ServiceProvider<Service> serviceProvider) {
            throw new UnsupportedOperationException("Implement me!");
        }

        @Override
        public void stop() {
            throw new UnsupportedOperationException("Implement me!");
        }
    }
}

