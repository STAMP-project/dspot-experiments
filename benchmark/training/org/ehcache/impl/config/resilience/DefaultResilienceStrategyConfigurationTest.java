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
package org.ehcache.impl.config.resilience;


import org.ehcache.impl.internal.resilience.RobustResilienceStrategy;
import org.ehcache.spi.loaderwriter.CacheLoaderWriter;
import org.ehcache.spi.resilience.RecoveryStore;
import org.ehcache.spi.resilience.ResilienceStrategy;
import org.ehcache.test.MockitoUtil;
import org.hamcrest.core.IsNull;
import org.hamcrest.core.IsSame;
import org.junit.Assert;
import org.junit.Test;


public class DefaultResilienceStrategyConfigurationTest {
    @Test
    public void testBindOnInstanceConfigurationReturnsSelf() {
        DefaultResilienceStrategyConfiguration configuration = new DefaultResilienceStrategyConfiguration(MockitoUtil.mock(ResilienceStrategy.class));
        Assert.assertThat(configuration.bind(null), IsSame.sameInstance(configuration));
    }

    @Test
    public void testLoaderWriterBindOnInstanceConfigurationReturnsSelf() {
        DefaultResilienceStrategyConfiguration configuration = new DefaultResilienceStrategyConfiguration(MockitoUtil.mock(ResilienceStrategy.class));
        Assert.assertThat(configuration.bind(null, null), IsSame.sameInstance(configuration));
    }

    @Test
    public void testBindOnRegularConfigurationAppendsParameters() {
        Object foo = new Object();
        DefaultResilienceStrategyConfiguration configuration = new DefaultResilienceStrategyConfiguration(RobustResilienceStrategy.class, foo);
        RecoveryStore<?> recoveryStore = MockitoUtil.mock(RecoveryStore.class);
        DefaultResilienceStrategyConfiguration bound = configuration.bind(recoveryStore);
        Assert.assertThat(bound.getArguments(), arrayContaining(foo, recoveryStore));
        Assert.assertThat(bound.getClazz(), IsSame.sameInstance(RobustResilienceStrategy.class));
        Assert.assertThat(bound.getInstance(), IsNull.nullValue());
    }

    @Test
    public void testLoaderWriterBindOnInstanceConfigurationAppendsParameters() {
        Object foo = new Object();
        DefaultResilienceStrategyConfiguration configuration = new DefaultResilienceStrategyConfiguration(RobustResilienceStrategy.class, foo);
        RecoveryStore<?> recoveryStore = MockitoUtil.mock(RecoveryStore.class);
        CacheLoaderWriter<?, ?> loaderWriter = MockitoUtil.mock(CacheLoaderWriter.class);
        DefaultResilienceStrategyConfiguration bound = configuration.bind(recoveryStore, loaderWriter);
        Assert.assertThat(bound.getArguments(), arrayContaining(foo, recoveryStore, loaderWriter));
        Assert.assertThat(bound.getClazz(), IsSame.sameInstance(RobustResilienceStrategy.class));
        Assert.assertThat(bound.getInstance(), IsNull.nullValue());
    }

    @Test
    public void testAlreadyBoundConfigurationCannotBeBound() {
        DefaultResilienceStrategyConfiguration configuration = new DefaultResilienceStrategyConfiguration(RobustResilienceStrategy.class);
        RecoveryStore<?> recoveryStore = MockitoUtil.mock(RecoveryStore.class);
        CacheLoaderWriter<?, ?> loaderWriter = MockitoUtil.mock(CacheLoaderWriter.class);
        DefaultResilienceStrategyConfiguration bound = configuration.bind(recoveryStore, loaderWriter);
        try {
            bound.bind(recoveryStore);
            Assert.fail("Expected IllegalStateException");
        } catch (IllegalStateException e) {
            // expected
        }
    }

    @Test
    public void testAlreadyBoundLoaderWriterConfigurationCannotBeBound() {
        DefaultResilienceStrategyConfiguration configuration = new DefaultResilienceStrategyConfiguration(RobustResilienceStrategy.class);
        RecoveryStore<?> recoveryStore = MockitoUtil.mock(RecoveryStore.class);
        DefaultResilienceStrategyConfiguration bound = configuration.bind(recoveryStore);
        try {
            bound.bind(recoveryStore);
            Assert.fail("Expected IllegalStateException");
        } catch (IllegalStateException e) {
            // expected
        }
    }
}

