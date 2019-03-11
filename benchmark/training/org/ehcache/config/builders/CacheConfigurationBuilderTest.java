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
package org.ehcache.config.builders;


import java.nio.ByteBuffer;
import org.ehcache.config.CacheConfiguration;
import org.ehcache.config.EvictionAdvisor;
import org.ehcache.config.ResourceType;
import org.ehcache.config.units.EntryUnit;
import org.ehcache.config.units.MemoryUnit;
import org.ehcache.core.spi.service.ServiceUtils;
import org.ehcache.expiry.ExpiryPolicy;
import org.ehcache.impl.config.copy.DefaultCopierConfiguration;
import org.ehcache.impl.config.loaderwriter.DefaultCacheLoaderWriterConfiguration;
import org.ehcache.impl.config.resilience.DefaultResilienceStrategyConfiguration;
import org.ehcache.impl.config.serializer.DefaultSerializerConfiguration;
import org.ehcache.impl.config.store.heap.DefaultSizeOfEngineConfiguration;
import org.ehcache.impl.internal.classes.ClassInstanceConfiguration;
import org.ehcache.impl.internal.resilience.RobustResilienceStrategy;
import org.ehcache.spi.copy.Copier;
import org.ehcache.spi.loaderwriter.CacheLoaderWriter;
import org.ehcache.spi.loaderwriter.CacheLoaderWriterConfiguration;
import org.ehcache.spi.resilience.RecoveryStore;
import org.ehcache.spi.resilience.ResilienceStrategy;
import org.ehcache.spi.serialization.Serializer;
import org.ehcache.spi.serialization.SerializerException;
import org.ehcache.spi.service.ServiceConfiguration;
import org.ehcache.test.MockitoUtil;
import org.hamcrest.Matcher;
import org.hamcrest.Matchers;
import org.hamcrest.core.IsSame;
import org.junit.Assert;
import org.junit.Test;

import static org.ehcache.config.ResourceType.Core.OFFHEAP;
import static org.ehcache.impl.config.serializer.DefaultSerializerConfiguration.Type.KEY;
import static org.ehcache.impl.config.serializer.DefaultSerializerConfiguration.Type.VALUE;


public class CacheConfigurationBuilderTest {
    @Test
    public void testEvictionAdvisor() throws Exception {
        EvictionAdvisor<Object, Object> evictionAdvisor = ( key, value) -> false;
        CacheConfiguration<Object, Object> cacheConfiguration = CacheConfigurationBuilder.newCacheConfigurationBuilder(Object.class, Object.class, ResourcePoolsBuilder.heap(10)).withEvictionAdvisor(evictionAdvisor).build();
        @SuppressWarnings("unchecked")
        Matcher<EvictionAdvisor<Object, Object>> evictionAdvisorMatcher = sameInstance(cacheConfiguration.getEvictionAdvisor());
        Assert.assertThat(evictionAdvisor, evictionAdvisorMatcher);
    }

    @Test
    public void testLoaderWriter() throws Exception {
        CacheLoaderWriter<Object, Object> loaderWriter = new CacheLoaderWriter<Object, Object>() {
            @Override
            public Object load(Object key) {
                return null;
            }

            @Override
            public void write(Object key, Object value) {
            }

            @Override
            public void delete(Object key) {
            }
        };
        CacheConfiguration<Object, Object> cacheConfiguration = CacheConfigurationBuilder.newCacheConfigurationBuilder(Object.class, Object.class, ResourcePoolsBuilder.heap(10)).withLoaderWriter(loaderWriter).build();
        CacheLoaderWriterConfiguration cacheLoaderWriterConfiguration = ServiceUtils.findSingletonAmongst(DefaultCacheLoaderWriterConfiguration.class, cacheConfiguration.getServiceConfigurations());
        Object instance = ((ClassInstanceConfiguration) (cacheLoaderWriterConfiguration)).getInstance();
        Assert.assertThat(instance, Matchers.sameInstance(loaderWriter));
    }

    @Test
    public void testKeySerializer() throws Exception {
        Serializer<Object> keySerializer = new Serializer<Object>() {
            @Override
            public ByteBuffer serialize(Object object) throws SerializerException {
                return null;
            }

            @Override
            public Object read(ByteBuffer binary) throws ClassNotFoundException, SerializerException {
                return null;
            }

            @Override
            public boolean equals(Object object, ByteBuffer binary) throws ClassNotFoundException, SerializerException {
                return false;
            }
        };
        CacheConfiguration<Object, Object> cacheConfiguration = CacheConfigurationBuilder.newCacheConfigurationBuilder(Object.class, Object.class, ResourcePoolsBuilder.heap(10)).withKeySerializer(keySerializer).build();
        DefaultSerializerConfiguration<?> serializerConfiguration = ServiceUtils.findSingletonAmongst(DefaultSerializerConfiguration.class, cacheConfiguration.getServiceConfigurations());
        Assert.assertThat(serializerConfiguration.getType(), is(KEY));
        Object instance = serializerConfiguration.getInstance();
        Assert.assertThat(instance, Matchers.sameInstance(keySerializer));
    }

    @Test
    public void testValueSerializer() throws Exception {
        Serializer<Object> valueSerializer = new Serializer<Object>() {
            @Override
            public ByteBuffer serialize(Object object) throws SerializerException {
                return null;
            }

            @Override
            public Object read(ByteBuffer binary) throws ClassNotFoundException, SerializerException {
                return null;
            }

            @Override
            public boolean equals(Object object, ByteBuffer binary) throws ClassNotFoundException, SerializerException {
                return false;
            }
        };
        CacheConfiguration<Object, Object> cacheConfiguration = CacheConfigurationBuilder.newCacheConfigurationBuilder(Object.class, Object.class, ResourcePoolsBuilder.heap(10)).withValueSerializer(valueSerializer).build();
        DefaultSerializerConfiguration<?> serializerConfiguration = ServiceUtils.findSingletonAmongst(DefaultSerializerConfiguration.class, cacheConfiguration.getServiceConfigurations());
        Assert.assertThat(serializerConfiguration.getType(), is(VALUE));
        Object instance = ((ClassInstanceConfiguration) (serializerConfiguration)).getInstance();
        Assert.assertThat(instance, Matchers.sameInstance(valueSerializer));
    }

    @Test
    public void testKeyCopier() throws Exception {
        Copier<Object> keyCopier = new Copier<Object>() {
            @Override
            public Long copyForRead(Object obj) {
                return null;
            }

            @Override
            public Long copyForWrite(Object obj) {
                return null;
            }
        };
        CacheConfiguration<Object, Object> cacheConfiguration = CacheConfigurationBuilder.newCacheConfigurationBuilder(Object.class, Object.class, ResourcePoolsBuilder.heap(10)).withKeyCopier(keyCopier).build();
        DefaultCopierConfiguration<?> copierConfiguration = ServiceUtils.findSingletonAmongst(DefaultCopierConfiguration.class, cacheConfiguration.getServiceConfigurations());
        Assert.assertThat(copierConfiguration.getType(), is(DefaultCopierConfiguration.Type.KEY));
        Object instance = copierConfiguration.getInstance();
        Assert.assertThat(instance, Matchers.sameInstance(keyCopier));
    }

    @Test
    public void testValueCopier() throws Exception {
        Copier<Object> valueCopier = new Copier<Object>() {
            @Override
            public Long copyForRead(Object obj) {
                return null;
            }

            @Override
            public Long copyForWrite(Object obj) {
                return null;
            }
        };
        CacheConfiguration<Object, Object> cacheConfiguration = CacheConfigurationBuilder.newCacheConfigurationBuilder(Object.class, Object.class, ResourcePoolsBuilder.heap(10)).withValueCopier(valueCopier).build();
        DefaultCopierConfiguration<?> copierConfiguration = ServiceUtils.findSingletonAmongst(DefaultCopierConfiguration.class, cacheConfiguration.getServiceConfigurations());
        Assert.assertThat(copierConfiguration.getType(), is(DefaultCopierConfiguration.Type.VALUE));
        Object instance = copierConfiguration.getInstance();
        Assert.assertThat(instance, Matchers.sameInstance(valueCopier));
    }

    @Test
    public void testNothing() {
        final CacheConfigurationBuilder<Long, CharSequence> builder = CacheConfigurationBuilder.newCacheConfigurationBuilder(Long.class, CharSequence.class, ResourcePoolsBuilder.heap(10));
        final ExpiryPolicy<Object, Object> expiry = ExpiryPolicyBuilder.timeToIdleExpiration(ExpiryPolicy.INFINITE);
        builder.withEvictionAdvisor(( key, value) -> (value.charAt(0)) == 'A').withExpiry(expiry).build();
    }

    @Test
    public void testOffheapGetsAddedToCacheConfiguration() {
        CacheConfigurationBuilder<Long, CharSequence> builder = CacheConfigurationBuilder.newCacheConfigurationBuilder(Long.class, CharSequence.class, ResourcePoolsBuilder.newResourcePoolsBuilder().heap(10, EntryUnit.ENTRIES).offheap(10, MemoryUnit.MB));
        final ExpiryPolicy<Object, Object> expiry = ExpiryPolicyBuilder.timeToIdleExpiration(ExpiryPolicy.INFINITE);
        CacheConfiguration<Long, CharSequence> config = builder.withEvictionAdvisor(( key, value) -> (value.charAt(0)) == 'A').withExpiry(expiry).build();
        Assert.assertThat(config.getResourcePools().getPoolForResource(OFFHEAP).getType(), Matchers.is(OFFHEAP));
        Assert.assertThat(config.getResourcePools().getPoolForResource(OFFHEAP).getUnit(), Matchers.is(MemoryUnit.MB));
    }

    @Test
    public void testSizeOf() {
        CacheConfigurationBuilder<String, String> builder = CacheConfigurationBuilder.newCacheConfigurationBuilder(String.class, String.class, ResourcePoolsBuilder.heap(10));
        builder = builder.withSizeOfMaxObjectSize(10, MemoryUnit.B).withSizeOfMaxObjectGraph(100);
        CacheConfiguration<String, String> configuration = builder.build();
        DefaultSizeOfEngineConfiguration sizeOfEngineConfiguration = ServiceUtils.findSingletonAmongst(DefaultSizeOfEngineConfiguration.class, configuration.getServiceConfigurations());
        Assert.assertThat(sizeOfEngineConfiguration, notNullValue());
        Assert.assertEquals(sizeOfEngineConfiguration.getMaxObjectSize(), 10);
        Assert.assertEquals(sizeOfEngineConfiguration.getUnit(), MemoryUnit.B);
        Assert.assertEquals(sizeOfEngineConfiguration.getMaxObjectGraphSize(), 100);
        builder = builder.withSizeOfMaxObjectGraph(1000);
        configuration = builder.build();
        sizeOfEngineConfiguration = ServiceUtils.findSingletonAmongst(DefaultSizeOfEngineConfiguration.class, configuration.getServiceConfigurations());
        Assert.assertEquals(sizeOfEngineConfiguration.getMaxObjectGraphSize(), 1000);
    }

    @Test
    public void testCopyingOfExistingConfiguration() {
        Class<Integer> keyClass = Integer.class;
        Class<String> valueClass = String.class;
        ClassLoader loader = MockitoUtil.mock(ClassLoader.class);
        @SuppressWarnings("unchecked")
        EvictionAdvisor<Integer, String> eviction = MockitoUtil.mock(EvictionAdvisor.class);
        @SuppressWarnings("unchecked")
        ExpiryPolicy<Integer, String> expiry = MockitoUtil.mock(ExpiryPolicy.class);
        ServiceConfiguration<?> service = MockitoUtil.mock(ServiceConfiguration.class);
        CacheConfiguration<Integer, String> configuration = CacheConfigurationBuilder.newCacheConfigurationBuilder(Integer.class, String.class, ResourcePoolsBuilder.heap(10)).withClassLoader(loader).withEvictionAdvisor(eviction).withExpiry(expiry).add(service).build();
        CacheConfiguration<Integer, String> copy = CacheConfigurationBuilder.newCacheConfigurationBuilder(configuration).build();
        Assert.assertThat(copy.getKeyType(), equalTo(keyClass));
        Assert.assertThat(copy.getValueType(), equalTo(valueClass));
        Assert.assertThat(copy.getClassLoader(), equalTo(loader));
        Assert.assertThat(copy.getEvictionAdvisor(), IsSame.sameInstance(eviction));
        Assert.assertThat(copy.getExpiryPolicy(), IsSame.sameInstance(expiry));
        Assert.assertThat(copy.getServiceConfigurations(), contains(IsSame.sameInstance(service)));
    }

    @Test
    public void testResilienceStrategyInstance() throws Exception {
        ResilienceStrategy<Object, Object> resilienceStrategy = MockitoUtil.mock(ResilienceStrategy.class);
        CacheConfiguration<Object, Object> cacheConfiguration = CacheConfigurationBuilder.newCacheConfigurationBuilder(Object.class, Object.class, ResourcePoolsBuilder.heap(10)).withResilienceStrategy(resilienceStrategy).build();
        DefaultResilienceStrategyConfiguration resilienceStrategyConfiguration = ServiceUtils.findSingletonAmongst(DefaultResilienceStrategyConfiguration.class, cacheConfiguration.getServiceConfigurations());
        Object instance = resilienceStrategyConfiguration.getInstance();
        Assert.assertThat(instance, sameInstance(resilienceStrategy));
    }

    @Test
    public void testResilienceStrategyClass() throws Exception {
        CacheConfiguration<Object, Object> cacheConfiguration = CacheConfigurationBuilder.newCacheConfigurationBuilder(Object.class, Object.class, ResourcePoolsBuilder.heap(10)).withResilienceStrategy(CacheConfigurationBuilderTest.CustomResilience.class, "Hello World").build();
        DefaultResilienceStrategyConfiguration resilienceStrategyConfiguration = ServiceUtils.findSingletonAmongst(DefaultResilienceStrategyConfiguration.class, cacheConfiguration.getServiceConfigurations());
        Assert.assertThat(resilienceStrategyConfiguration.getInstance(), nullValue());
        Assert.assertThat(resilienceStrategyConfiguration.getClazz(), sameInstance(CacheConfigurationBuilderTest.CustomResilience.class));
        Assert.assertThat(resilienceStrategyConfiguration.getArguments(), arrayContaining("Hello World"));
    }

    static class CustomResilience<K, V> extends RobustResilienceStrategy<K, V> {
        public CustomResilience(RecoveryStore<K> store, String blah) {
            super(store);
        }
    }
}

