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
package org.ehcache.impl.internal.spi.resilience;


import java.util.Collections;
import org.ehcache.config.CacheConfiguration;
import org.ehcache.impl.config.resilience.DefaultResilienceStrategyConfiguration;
import org.ehcache.impl.config.resilience.DefaultResilienceStrategyProviderConfiguration;
import org.ehcache.impl.internal.resilience.RobustResilienceStrategy;
import org.ehcache.spi.loaderwriter.CacheLoaderWriter;
import org.ehcache.spi.resilience.RecoveryStore;
import org.ehcache.spi.resilience.ResilienceStrategy;
import org.ehcache.test.MockitoUtil;
import org.hamcrest.core.Is;
import org.hamcrest.core.IsInstanceOf;
import org.hamcrest.core.IsSame;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;


public class DefaultResilienceStrategyProviderTest {
    @Test
    public void testDefaultInstanceReturned() {
        ResilienceStrategy<?, ?> resilienceStrategy = MockitoUtil.mock(ResilienceStrategy.class);
        DefaultResilienceStrategyProviderConfiguration configuration = new DefaultResilienceStrategyProviderConfiguration();
        configuration.setDefaultResilienceStrategy(resilienceStrategy);
        DefaultResilienceStrategyProvider provider = new DefaultResilienceStrategyProvider(configuration);
        Assert.assertThat(provider.createResilienceStrategy("foo", MockitoUtil.mock(CacheConfiguration.class), MockitoUtil.mock(RecoveryStore.class)), IsSame.sameInstance(resilienceStrategy));
    }

    @Test
    public void testDefaultLoaderWriterInstanceReturned() {
        ResilienceStrategy<?, ?> resilienceStrategy = MockitoUtil.mock(ResilienceStrategy.class);
        DefaultResilienceStrategyProviderConfiguration configuration = new DefaultResilienceStrategyProviderConfiguration();
        configuration.setDefaultLoaderWriterResilienceStrategy(resilienceStrategy);
        DefaultResilienceStrategyProvider provider = new DefaultResilienceStrategyProvider(configuration);
        Assert.assertThat(provider.createResilienceStrategy("foo", MockitoUtil.mock(CacheConfiguration.class), MockitoUtil.mock(RecoveryStore.class), MockitoUtil.mock(CacheLoaderWriter.class)), IsSame.sameInstance(resilienceStrategy));
    }

    @Test
    public void testDefaultInstanceConstructed() {
        DefaultResilienceStrategyProviderConfiguration configuration = new DefaultResilienceStrategyProviderConfiguration();
        configuration.setDefaultResilienceStrategy(DefaultResilienceStrategyProviderTest.TestResilienceStrategy.class, "FooBar");
        DefaultResilienceStrategyProvider provider = new DefaultResilienceStrategyProvider(configuration);
        ResilienceStrategy<?, ?> resilienceStrategy = provider.createResilienceStrategy("foo", MockitoUtil.mock(CacheConfiguration.class), MockitoUtil.mock(RecoveryStore.class));
        Assert.assertThat(resilienceStrategy, IsInstanceOf.instanceOf(DefaultResilienceStrategyProviderTest.TestResilienceStrategy.class));
        Assert.assertThat(((DefaultResilienceStrategyProviderTest.TestResilienceStrategy) (resilienceStrategy)).message, Is.is("FooBar"));
    }

    @Test
    public void testDefaultLoaderWriterInstanceConstructed() {
        DefaultResilienceStrategyProviderConfiguration configuration = new DefaultResilienceStrategyProviderConfiguration();
        configuration.setDefaultLoaderWriterResilienceStrategy(DefaultResilienceStrategyProviderTest.TestResilienceStrategy.class, "FooBar");
        DefaultResilienceStrategyProvider provider = new DefaultResilienceStrategyProvider(configuration);
        ResilienceStrategy<?, ?> resilienceStrategy = provider.createResilienceStrategy("foo", MockitoUtil.mock(CacheConfiguration.class), MockitoUtil.mock(RecoveryStore.class), MockitoUtil.mock(CacheLoaderWriter.class));
        Assert.assertThat(resilienceStrategy, IsInstanceOf.instanceOf(DefaultResilienceStrategyProviderTest.TestResilienceStrategy.class));
        Assert.assertThat(((DefaultResilienceStrategyProviderTest.TestResilienceStrategy) (resilienceStrategy)).message, Is.is("FooBar"));
    }

    @Test
    public void testPreconfiguredInstanceReturned() {
        ResilienceStrategy<?, ?> resilienceStrategy = MockitoUtil.mock(ResilienceStrategy.class);
        DefaultResilienceStrategyProviderConfiguration configuration = new DefaultResilienceStrategyProviderConfiguration();
        configuration.addResilienceStrategyFor("foo", resilienceStrategy);
        DefaultResilienceStrategyProvider provider = new DefaultResilienceStrategyProvider(configuration);
        Assert.assertThat(provider.createResilienceStrategy("foo", MockitoUtil.mock(CacheConfiguration.class), MockitoUtil.mock(RecoveryStore.class)), IsSame.sameInstance(resilienceStrategy));
    }

    @Test
    public void testPreconfiguredLoaderWriterInstanceReturned() {
        ResilienceStrategy<?, ?> resilienceStrategy = MockitoUtil.mock(ResilienceStrategy.class);
        DefaultResilienceStrategyProviderConfiguration configuration = new DefaultResilienceStrategyProviderConfiguration();
        configuration.addResilienceStrategyFor("foo", resilienceStrategy);
        DefaultResilienceStrategyProvider provider = new DefaultResilienceStrategyProvider(configuration);
        Assert.assertThat(provider.createResilienceStrategy("foo", MockitoUtil.mock(CacheConfiguration.class), MockitoUtil.mock(RecoveryStore.class), MockitoUtil.mock(CacheLoaderWriter.class)), IsSame.sameInstance(resilienceStrategy));
    }

    @Test
    public void testPreconfiguredInstanceConstructed() {
        DefaultResilienceStrategyProviderConfiguration configuration = new DefaultResilienceStrategyProviderConfiguration();
        configuration.addResilienceStrategyFor("foo", DefaultResilienceStrategyProviderTest.TestResilienceStrategy.class, "FooBar");
        DefaultResilienceStrategyProvider provider = new DefaultResilienceStrategyProvider(configuration);
        ResilienceStrategy<?, ?> resilienceStrategy = provider.createResilienceStrategy("foo", MockitoUtil.mock(CacheConfiguration.class), MockitoUtil.mock(RecoveryStore.class));
        Assert.assertThat(resilienceStrategy, IsInstanceOf.instanceOf(DefaultResilienceStrategyProviderTest.TestResilienceStrategy.class));
        Assert.assertThat(((DefaultResilienceStrategyProviderTest.TestResilienceStrategy) (resilienceStrategy)).message, Is.is("FooBar"));
    }

    @Test
    public void testPreconfiguredLoaderWriterInstanceConstructed() {
        DefaultResilienceStrategyProviderConfiguration configuration = new DefaultResilienceStrategyProviderConfiguration();
        configuration.addResilienceStrategyFor("foo", DefaultResilienceStrategyProviderTest.TestResilienceStrategy.class, "FooBar");
        DefaultResilienceStrategyProvider provider = new DefaultResilienceStrategyProvider(configuration);
        ResilienceStrategy<?, ?> resilienceStrategy = provider.createResilienceStrategy("foo", MockitoUtil.mock(CacheConfiguration.class), MockitoUtil.mock(RecoveryStore.class), MockitoUtil.mock(CacheLoaderWriter.class));
        Assert.assertThat(resilienceStrategy, IsInstanceOf.instanceOf(DefaultResilienceStrategyProviderTest.TestResilienceStrategy.class));
        Assert.assertThat(((DefaultResilienceStrategyProviderTest.TestResilienceStrategy) (resilienceStrategy)).message, Is.is("FooBar"));
    }

    @Test
    public void testProvidedInstanceReturned() {
        ResilienceStrategy<?, ?> resilienceStrategy = MockitoUtil.mock(ResilienceStrategy.class);
        DefaultResilienceStrategyProviderConfiguration configuration = new DefaultResilienceStrategyProviderConfiguration();
        DefaultResilienceStrategyProvider provider = new DefaultResilienceStrategyProvider(configuration);
        CacheConfiguration<?, ?> cacheConfiguration = MockitoUtil.mock(CacheConfiguration.class);
        Mockito.when(cacheConfiguration.getServiceConfigurations()).thenReturn(Collections.singleton(new DefaultResilienceStrategyConfiguration(resilienceStrategy)));
        Assert.assertThat(provider.createResilienceStrategy("foo", cacheConfiguration, MockitoUtil.mock(RecoveryStore.class)), IsSame.sameInstance(resilienceStrategy));
    }

    @Test
    public void testProvidedLoaderWriterInstanceReturned() {
        ResilienceStrategy<?, ?> resilienceStrategy = MockitoUtil.mock(ResilienceStrategy.class);
        DefaultResilienceStrategyProviderConfiguration configuration = new DefaultResilienceStrategyProviderConfiguration();
        DefaultResilienceStrategyProvider provider = new DefaultResilienceStrategyProvider(configuration);
        CacheConfiguration<?, ?> cacheConfiguration = MockitoUtil.mock(CacheConfiguration.class);
        Mockito.when(cacheConfiguration.getServiceConfigurations()).thenReturn(Collections.singleton(new DefaultResilienceStrategyConfiguration(resilienceStrategy)));
        Assert.assertThat(provider.createResilienceStrategy("foo", cacheConfiguration, MockitoUtil.mock(RecoveryStore.class), MockitoUtil.mock(CacheLoaderWriter.class)), IsSame.sameInstance(resilienceStrategy));
    }

    @Test
    public void testProvidedInstanceConstructed() {
        DefaultResilienceStrategyProviderConfiguration configuration = new DefaultResilienceStrategyProviderConfiguration();
        DefaultResilienceStrategyProvider provider = new DefaultResilienceStrategyProvider(configuration);
        CacheConfiguration<?, ?> cacheConfiguration = MockitoUtil.mock(CacheConfiguration.class);
        Mockito.when(cacheConfiguration.getServiceConfigurations()).thenReturn(Collections.singleton(new DefaultResilienceStrategyConfiguration(DefaultResilienceStrategyProviderTest.TestResilienceStrategy.class, "FooBar")));
        ResilienceStrategy<?, ?> resilienceStrategy = provider.createResilienceStrategy("foo", cacheConfiguration, MockitoUtil.mock(RecoveryStore.class));
        Assert.assertThat(resilienceStrategy, IsInstanceOf.instanceOf(DefaultResilienceStrategyProviderTest.TestResilienceStrategy.class));
        Assert.assertThat(((DefaultResilienceStrategyProviderTest.TestResilienceStrategy) (resilienceStrategy)).message, Is.is("FooBar"));
    }

    @Test
    public void testProvidedLoaderWriterInstanceConstructed() {
        DefaultResilienceStrategyProviderConfiguration configuration = new DefaultResilienceStrategyProviderConfiguration();
        DefaultResilienceStrategyProvider provider = new DefaultResilienceStrategyProvider(configuration);
        CacheConfiguration<?, ?> cacheConfiguration = MockitoUtil.mock(CacheConfiguration.class);
        Mockito.when(cacheConfiguration.getServiceConfigurations()).thenReturn(Collections.singleton(new DefaultResilienceStrategyConfiguration(DefaultResilienceStrategyProviderTest.TestResilienceStrategy.class, "FooBar")));
        ResilienceStrategy<?, ?> resilienceStrategy = provider.createResilienceStrategy("foo", cacheConfiguration, MockitoUtil.mock(RecoveryStore.class), MockitoUtil.mock(CacheLoaderWriter.class));
        Assert.assertThat(resilienceStrategy, IsInstanceOf.instanceOf(DefaultResilienceStrategyProviderTest.TestResilienceStrategy.class));
        Assert.assertThat(((DefaultResilienceStrategyProviderTest.TestResilienceStrategy) (resilienceStrategy)).message, Is.is("FooBar"));
    }

    public static class TestResilienceStrategy<K, V> extends RobustResilienceStrategy<K, V> {
        public final String message;

        public TestResilienceStrategy(String message, RecoveryStore<K> store) {
            super(store);
            this.message = message;
        }

        public TestResilienceStrategy(String message, RecoveryStore<K> store, CacheLoaderWriter<K, V> loaderWriter) {
            super(store);
            this.message = message;
        }
    }
}

