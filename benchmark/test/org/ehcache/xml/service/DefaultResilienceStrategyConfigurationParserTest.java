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


import com.pany.ehcache.integration.TestResilienceStrategy;
import org.ehcache.config.CacheConfiguration;
import org.ehcache.config.builders.CacheConfigurationBuilder;
import org.ehcache.config.builders.ResourcePoolsBuilder;
import org.ehcache.core.spi.service.ServiceUtils;
import org.ehcache.impl.config.resilience.DefaultResilienceStrategyConfiguration;
import org.ehcache.xml.NiResilience;
import org.ehcache.xml.XmlConfiguration;
import org.ehcache.xml.exceptions.XmlConfigurationException;
import org.ehcache.xml.model.CacheType;
import org.junit.Test;


public class DefaultResilienceStrategyConfigurationParserTest {
    @Test
    public void parseServiceConfiguration() throws Exception {
        CacheConfiguration<?, ?> cacheConfiguration = new XmlConfiguration(getClass().getResource("/configs/resilience-config.xml")).getCacheConfigurations().get("ni");
        DefaultResilienceStrategyConfiguration resilienceStrategyConfig = ServiceUtils.findSingletonAmongst(DefaultResilienceStrategyConfiguration.class, cacheConfiguration.getServiceConfigurations());
        assertThat(resilienceStrategyConfig).isNotNull();
        assertThat(resilienceStrategyConfig.getClazz()).isEqualTo(NiResilience.class);
    }

    @Test
    public void unparseServiceConfiguration() {
        CacheConfiguration<?, ?> cacheConfig = CacheConfigurationBuilder.newCacheConfigurationBuilder(Object.class, Object.class, ResourcePoolsBuilder.heap(10)).add(new DefaultResilienceStrategyConfiguration(TestResilienceStrategy.class)).build();
        CacheType cacheType = new DefaultResilienceStrategyConfigurationParser().unparseServiceConfiguration(cacheConfig, new CacheType());
        assertThat(cacheType.getResilience()).isEqualTo(TestResilienceStrategy.class.getName());
    }

    @Test
    public void unparseServiceConfigurationWithInstance() {
        TestResilienceStrategy<Integer, Integer> testObject = new TestResilienceStrategy<>();
        CacheConfiguration<?, ?> cacheConfig = CacheConfigurationBuilder.newCacheConfigurationBuilder(Object.class, Object.class, ResourcePoolsBuilder.heap(10)).add(new DefaultResilienceStrategyConfiguration(testObject)).build();
        assertThatExceptionOfType(XmlConfigurationException.class).isThrownBy(() -> new DefaultResilienceStrategyConfigurationParser().unparseServiceConfiguration(cacheConfig, new CacheType())).withMessage("%s", ("XML translation for instance based initialization for " + "DefaultResilienceStrategyConfiguration is not supported"));
    }
}

