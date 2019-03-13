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
package org.ehcache.impl.internal.spi.loaderwriter;


import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import org.ehcache.Cache;
import org.ehcache.CacheManager;
import org.ehcache.config.CacheConfiguration;
import org.ehcache.config.builders.CacheConfigurationBuilder;
import org.ehcache.config.builders.CacheManagerBuilder;
import org.ehcache.config.builders.ResourcePoolsBuilder;
import org.ehcache.core.config.DefaultConfiguration;
import org.ehcache.impl.config.loaderwriter.DefaultCacheLoaderWriterConfiguration;
import org.ehcache.impl.config.loaderwriter.DefaultCacheLoaderWriterProviderConfiguration;
import org.ehcache.spi.loaderwriter.CacheLoaderWriter;
import org.ehcache.spi.service.Service;
import org.ehcache.spi.service.ServiceConfiguration;
import org.ehcache.spi.service.ServiceProvider;
import org.hamcrest.CoreMatchers;
import org.hamcrest.core.IsCollectionContaining;
import org.hamcrest.core.IsInstanceOf;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;


public class DefaultCacheLoaderWriterProviderTest {
    @Test
    public void testCacheConfigUsage() {
        final CacheManager manager = CacheManagerBuilder.newCacheManagerBuilder().withCache("foo", CacheConfigurationBuilder.newCacheConfigurationBuilder(Object.class, Object.class, ResourcePoolsBuilder.heap(10)).add(new DefaultCacheLoaderWriterConfiguration(DefaultCacheLoaderWriterProviderTest.MyLoader.class)).build()).build(true);
        final Object foo = manager.getCache("foo", Object.class, Object.class).get(new Object());
        Assert.assertThat(foo, CoreMatchers.is(DefaultCacheLoaderWriterProviderTest.MyLoader.object));
    }

    @Test
    public void testCacheManagerConfigUsage() {
        final CacheConfiguration<Object, Object> cacheConfiguration = CacheConfigurationBuilder.newCacheConfigurationBuilder(Object.class, Object.class, ResourcePoolsBuilder.heap(10)).build();
        final Map<String, CacheConfiguration<?, ?>> caches = new HashMap<>();
        caches.put("foo", cacheConfiguration);
        final DefaultConfiguration configuration = new DefaultConfiguration(caches, null, new DefaultCacheLoaderWriterProviderConfiguration().addLoaderFor("foo", DefaultCacheLoaderWriterProviderTest.MyLoader.class));
        final CacheManager manager = CacheManagerBuilder.newCacheManager(configuration);
        manager.init();
        final Object foo = manager.getCache("foo", Object.class, Object.class).get(new Object());
        Assert.assertThat(foo, CoreMatchers.is(DefaultCacheLoaderWriterProviderTest.MyLoader.object));
    }

    @Test
    public void testCacheConfigOverridesCacheManagerConfig() {
        final CacheConfiguration<Object, Object> cacheConfiguration = CacheConfigurationBuilder.newCacheConfigurationBuilder(Object.class, Object.class, ResourcePoolsBuilder.heap(10)).add(new DefaultCacheLoaderWriterConfiguration(DefaultCacheLoaderWriterProviderTest.MyOtherLoader.class)).build();
        final Map<String, CacheConfiguration<?, ?>> caches = new HashMap<>();
        caches.put("foo", cacheConfiguration);
        final DefaultConfiguration configuration = new DefaultConfiguration(caches, null, new DefaultCacheLoaderWriterProviderConfiguration().addLoaderFor("foo", DefaultCacheLoaderWriterProviderTest.MyLoader.class));
        final CacheManager manager = CacheManagerBuilder.newCacheManager(configuration);
        manager.init();
        final Object foo = manager.getCache("foo", Object.class, Object.class).get(new Object());
        Assert.assertThat(foo, CoreMatchers.is(DefaultCacheLoaderWriterProviderTest.MyOtherLoader.object));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testAddingCacheLoaderWriterConfigurationAtCacheLevel() {
        CacheManagerBuilder<CacheManager> cacheManagerBuilder = CacheManagerBuilder.newCacheManagerBuilder();
        Class<CacheLoaderWriter<?, ?>> klazz = ((Class<CacheLoaderWriter<?, ?>>) ((Class) (DefaultCacheLoaderWriterProviderTest.MyLoader.class)));
        CacheManager cacheManager = cacheManagerBuilder.build(true);
        final Cache<Long, String> cache = cacheManager.createCache("cache", CacheConfigurationBuilder.newCacheConfigurationBuilder(Long.class, String.class, ResourcePoolsBuilder.heap(100)).add(new DefaultCacheLoaderWriterConfiguration(klazz)).build());
        Collection<ServiceConfiguration<?>> serviceConfiguration = cache.getRuntimeConfiguration().getServiceConfigurations();
        Assert.assertThat(serviceConfiguration, IsCollectionContaining.<ServiceConfiguration<?>>hasItem(IsInstanceOf.instanceOf(DefaultCacheLoaderWriterConfiguration.class)));
        cacheManager.close();
    }

    @Test
    public void testCreationConfigurationPreservedAfterStopStart() {
        DefaultCacheLoaderWriterProviderConfiguration configuration = new DefaultCacheLoaderWriterProviderConfiguration();
        configuration.addLoaderFor("cache", DefaultCacheLoaderWriterProviderTest.MyLoader.class);
        DefaultCacheLoaderWriterProvider loaderWriterProvider = new DefaultCacheLoaderWriterProvider(configuration);
        @SuppressWarnings("unchecked")
        ServiceProvider<Service> serviceProvider = Mockito.mock(ServiceProvider.class);
        loaderWriterProvider.start(serviceProvider);
        @SuppressWarnings("unchecked")
        CacheConfiguration<Object, Object> cacheConfiguration = Mockito.mock(CacheConfiguration.class);
        Assert.assertThat(loaderWriterProvider.createCacheLoaderWriter("cache", cacheConfiguration), CoreMatchers.instanceOf(DefaultCacheLoaderWriterProviderTest.MyLoader.class));
        loaderWriterProvider.stop();
        loaderWriterProvider.start(serviceProvider);
        Assert.assertThat(loaderWriterProvider.createCacheLoaderWriter("cache", cacheConfiguration), CoreMatchers.instanceOf(DefaultCacheLoaderWriterProviderTest.MyLoader.class));
    }

    public static class MyLoader implements CacheLoaderWriter<Object, Object> {
        private static final Object object = new Object() {
            @Override
            public String toString() {
                return (DefaultCacheLoaderWriterProviderTest.MyLoader.class.getName()) + "'s object";
            }
        };

        @Override
        public Object load(final Object key) {
            return DefaultCacheLoaderWriterProviderTest.MyLoader.object;
        }

        @Override
        public Map<Object, Object> loadAll(final Iterable<?> keys) {
            throw new UnsupportedOperationException("Implement me!");
        }

        private static Object lastWritten;

        @Override
        public void write(final Object key, final Object value) {
            DefaultCacheLoaderWriterProviderTest.MyLoader.lastWritten = value;
        }

        @Override
        public void writeAll(final Iterable<? extends Map.Entry<?, ?>> entries) {
            throw new UnsupportedOperationException("Implement me!");
        }

        @Override
        public void delete(final Object key) {
            throw new UnsupportedOperationException("Implement me!");
        }

        @Override
        public void deleteAll(final Iterable<?> keys) {
            throw new UnsupportedOperationException("Implement me!");
        }
    }

    public static class MyOtherLoader extends DefaultCacheLoaderWriterProviderTest.MyLoader {
        private static final Object object = new Object() {
            @Override
            public String toString() {
                return (DefaultCacheLoaderWriterProviderTest.MyOtherLoader.class.getName()) + "'s object";
            }
        };

        private static Object lastWritten;

        @Override
        public Object load(final Object key) {
            return DefaultCacheLoaderWriterProviderTest.MyOtherLoader.object;
        }

        @Override
        public void write(final Object key, final Object value) {
            DefaultCacheLoaderWriterProviderTest.MyOtherLoader.lastWritten = value;
        }
    }
}

