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


import org.ehcache.config.CacheConfiguration;
import org.ehcache.impl.config.resilience.DefaultResilienceStrategyProviderConfiguration;
import org.ehcache.spi.resilience.RecoveryStore;
import org.ehcache.spi.resilience.ResilienceStrategy;
import org.ehcache.spi.resilience.ResilienceStrategyProvider;
import org.ehcache.spi.service.ServiceCreationConfiguration;
import org.ehcache.test.MockitoUtil;
import org.hamcrest.core.IsNull;
import org.hamcrest.core.IsSame;
import org.junit.Assert;
import org.junit.Test;


public class DefaultResilienceStrategyProviderFactoryTest {
    @Test
    public void testNullGivesValidFactory() {
        ResilienceStrategyProvider provider = new DefaultResilienceStrategyProviderFactory().create(null);
        Assert.assertThat(provider.createResilienceStrategy("test", MockitoUtil.mock(CacheConfiguration.class), MockitoUtil.mock(RecoveryStore.class)), IsNull.notNullValue());
    }

    @Test
    public void testWrongConfigTypeFails() {
        try {
            new DefaultResilienceStrategyProviderFactory().create(MockitoUtil.mock(ServiceCreationConfiguration.class));
            Assert.fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            // expected
        }
    }

    @Test
    public void testSpecifiedConfigIsPassed() {
        ResilienceStrategy<?, ?> resilienceStrategy = MockitoUtil.mock(ResilienceStrategy.class);
        DefaultResilienceStrategyProviderConfiguration configuration = new DefaultResilienceStrategyProviderConfiguration();
        configuration.setDefaultResilienceStrategy(resilienceStrategy);
        ResilienceStrategyProvider provider = new DefaultResilienceStrategyProviderFactory().create(configuration);
        Assert.assertThat(provider.createResilienceStrategy("foo", MockitoUtil.mock(CacheConfiguration.class), MockitoUtil.mock(RecoveryStore.class)), IsSame.sameInstance(resilienceStrategy));
    }
}

