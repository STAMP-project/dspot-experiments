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
package org.ehcache.jsr107;


import java.lang.management.ManagementFactory;
import javax.cache.Cache;
import javax.cache.CacheManager;
import javax.cache.configuration.MutableConfiguration;
import javax.cache.spi.CachingProvider;
import javax.management.MBeanServer;
import org.ehcache.config.builders.CacheConfigurationBuilder;
import org.ehcache.config.builders.ResourcePoolsBuilder;
import org.ehcache.jsr107.config.ConfigurationElementState;
import org.ehcache.jsr107.config.Jsr107CacheConfiguration;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


/**
 * ConfigStatsManagementActivationTest
 */
public class ConfigStatsManagementActivationTest {
    private static final String MBEAN_MANAGEMENT_TYPE = "CacheConfiguration";

    private static final String MBEAN_STATISTICS_TYPE = "CacheStatistics";

    private MBeanServer server = ManagementFactory.getPlatformMBeanServer();

    private CachingProvider provider;

    @Test
    public void testEnabledAtCacheLevel() throws Exception {
        CacheManager cacheManager = provider.getCacheManager(getClass().getResource("/ehcache-107-mbeans-cache-config.xml").toURI(), provider.getDefaultClassLoader());
        Cache<String, String> cache = cacheManager.getCache("stringCache", String.class, String.class);
        @SuppressWarnings("unchecked")
        Eh107Configuration<String, String> configuration = cache.getConfiguration(Eh107Configuration.class);
        Assert.assertThat(configuration.isManagementEnabled(), Matchers.is(true));
        Assert.assertThat(configuration.isStatisticsEnabled(), Matchers.is(true));
        Assert.assertThat(isMbeanRegistered("stringCache", ConfigStatsManagementActivationTest.MBEAN_MANAGEMENT_TYPE), Matchers.is(true));
        Assert.assertThat(isMbeanRegistered("stringCache", ConfigStatsManagementActivationTest.MBEAN_STATISTICS_TYPE), Matchers.is(true));
    }

    @Test
    public void testEnabledAtCacheManagerLevel() throws Exception {
        CacheManager cacheManager = provider.getCacheManager(getClass().getResource("/org/ehcache/docs/ehcache-107-mbeans-cache-manager-config.xml").toURI(), provider.getDefaultClassLoader());
        Cache<String, String> cache = cacheManager.getCache("stringCache", String.class, String.class);
        @SuppressWarnings("unchecked")
        Eh107Configuration<String, String> configuration = cache.getConfiguration(Eh107Configuration.class);
        Assert.assertThat(configuration.isManagementEnabled(), Matchers.is(true));
        Assert.assertThat(configuration.isStatisticsEnabled(), Matchers.is(true));
        Assert.assertThat(isMbeanRegistered("stringCache", ConfigStatsManagementActivationTest.MBEAN_MANAGEMENT_TYPE), Matchers.is(true));
        Assert.assertThat(isMbeanRegistered("stringCache", ConfigStatsManagementActivationTest.MBEAN_STATISTICS_TYPE), Matchers.is(true));
    }

    @Test
    public void testCacheLevelOverridesCacheManagerLevel() throws Exception {
        CacheManager cacheManager = provider.getCacheManager(getClass().getResource("/org/ehcache/docs/ehcache-107-mbeans-cache-manager-config.xml").toURI(), provider.getDefaultClassLoader());
        Cache<String, String> cache = cacheManager.getCache("overrideCache", String.class, String.class);
        @SuppressWarnings("unchecked")
        Eh107Configuration<String, String> configuration = cache.getConfiguration(Eh107Configuration.class);
        Assert.assertThat(configuration.isManagementEnabled(), Matchers.is(false));
        Assert.assertThat(configuration.isStatisticsEnabled(), Matchers.is(false));
        Assert.assertThat(isMbeanRegistered("overrideCache", ConfigStatsManagementActivationTest.MBEAN_MANAGEMENT_TYPE), Matchers.is(false));
        Assert.assertThat(isMbeanRegistered("overrideCache", ConfigStatsManagementActivationTest.MBEAN_STATISTICS_TYPE), Matchers.is(false));
    }

    @Test
    public void testCacheLevelOnlyOneOverridesCacheManagerLevel() throws Exception {
        CacheManager cacheManager = provider.getCacheManager(getClass().getResource("/org/ehcache/docs/ehcache-107-mbeans-cache-manager-config.xml").toURI(), provider.getDefaultClassLoader());
        Cache<String, String> cache = cacheManager.getCache("overrideOneCache", String.class, String.class);
        @SuppressWarnings("unchecked")
        Eh107Configuration<String, String> configuration = cache.getConfiguration(Eh107Configuration.class);
        Assert.assertThat(configuration.isManagementEnabled(), Matchers.is(true));
        Assert.assertThat(configuration.isStatisticsEnabled(), Matchers.is(false));
        Assert.assertThat(isMbeanRegistered("overrideOneCache", ConfigStatsManagementActivationTest.MBEAN_MANAGEMENT_TYPE), Matchers.is(true));
        Assert.assertThat(isMbeanRegistered("overrideOneCache", ConfigStatsManagementActivationTest.MBEAN_STATISTICS_TYPE), Matchers.is(false));
    }

    @Test
    public void testEnableCacheLevelProgrammatic() throws Exception {
        CacheManager cacheManager = provider.getCacheManager();
        CacheConfigurationBuilder<Long, String> configurationBuilder = CacheConfigurationBuilder.newCacheConfigurationBuilder(Long.class, String.class, ResourcePoolsBuilder.heap(10)).add(new Jsr107CacheConfiguration(ConfigurationElementState.ENABLED, ConfigurationElementState.ENABLED));
        Cache<Long, String> cache = cacheManager.createCache("test", Eh107Configuration.fromEhcacheCacheConfiguration(configurationBuilder));
        @SuppressWarnings("unchecked")
        Eh107Configuration<Long, String> configuration = cache.getConfiguration(Eh107Configuration.class);
        Assert.assertThat(configuration.isManagementEnabled(), Matchers.is(true));
        Assert.assertThat(configuration.isStatisticsEnabled(), Matchers.is(true));
        Assert.assertThat(isMbeanRegistered("test", ConfigStatsManagementActivationTest.MBEAN_MANAGEMENT_TYPE), Matchers.is(true));
        Assert.assertThat(isMbeanRegistered("test", ConfigStatsManagementActivationTest.MBEAN_STATISTICS_TYPE), Matchers.is(true));
    }

    @Test
    public void testManagementDisabledOverriddenFromTemplate() throws Exception {
        CacheManager cacheManager = provider.getCacheManager(getClass().getResource("/ehcache-107-mbeans-template-config.xml").toURI(), provider.getDefaultClassLoader());
        MutableConfiguration<Long, String> configuration = new MutableConfiguration();
        configuration.setTypes(Long.class, String.class);
        configuration.setManagementEnabled(false);
        configuration.setStatisticsEnabled(false);
        Cache<Long, String> cache = cacheManager.createCache("enables-mbeans", configuration);
        @SuppressWarnings("unchecked")
        Eh107Configuration<Long, String> eh107Configuration = cache.getConfiguration(Eh107Configuration.class);
        Assert.assertThat(eh107Configuration.isManagementEnabled(), Matchers.is(true));
        Assert.assertThat(eh107Configuration.isStatisticsEnabled(), Matchers.is(true));
        Assert.assertThat(isMbeanRegistered("enables-mbeans", ConfigStatsManagementActivationTest.MBEAN_MANAGEMENT_TYPE), Matchers.is(true));
        Assert.assertThat(isMbeanRegistered("enables-mbeans", ConfigStatsManagementActivationTest.MBEAN_STATISTICS_TYPE), Matchers.is(true));
    }

    @Test
    public void testManagementEnabledOverriddenFromTemplate() throws Exception {
        CacheManager cacheManager = provider.getCacheManager(getClass().getResource("/ehcache-107-mbeans-template-config.xml").toURI(), provider.getDefaultClassLoader());
        MutableConfiguration<Long, String> configuration = new MutableConfiguration();
        configuration.setTypes(Long.class, String.class);
        configuration.setManagementEnabled(true);
        configuration.setStatisticsEnabled(true);
        Cache<Long, String> cache = cacheManager.createCache("disables-mbeans", configuration);
        @SuppressWarnings("unchecked")
        Eh107Configuration<Long, String> eh107Configuration = cache.getConfiguration(Eh107Configuration.class);
        Assert.assertThat(eh107Configuration.isManagementEnabled(), Matchers.is(false));
        Assert.assertThat(eh107Configuration.isStatisticsEnabled(), Matchers.is(false));
        Assert.assertThat(isMbeanRegistered("disables-mbeans", ConfigStatsManagementActivationTest.MBEAN_MANAGEMENT_TYPE), Matchers.is(false));
        Assert.assertThat(isMbeanRegistered("disables-mbeans", ConfigStatsManagementActivationTest.MBEAN_STATISTICS_TYPE), Matchers.is(false));
    }

    @Test
    public void basicJsr107StillWorks() throws Exception {
        CacheManager cacheManager = provider.getCacheManager();
        MutableConfiguration<Long, String> configuration = new MutableConfiguration();
        configuration.setTypes(Long.class, String.class);
        configuration.setManagementEnabled(true);
        configuration.setStatisticsEnabled(true);
        Cache<Long, String> cache = cacheManager.createCache("cache", configuration);
        @SuppressWarnings("unchecked")
        Eh107Configuration<Long, String> eh107Configuration = cache.getConfiguration(Eh107Configuration.class);
        Assert.assertThat(eh107Configuration.isManagementEnabled(), Matchers.is(true));
        Assert.assertThat(eh107Configuration.isStatisticsEnabled(), Matchers.is(true));
        Assert.assertThat(isMbeanRegistered("cache", ConfigStatsManagementActivationTest.MBEAN_MANAGEMENT_TYPE), Matchers.is(true));
        Assert.assertThat(isMbeanRegistered("cache", ConfigStatsManagementActivationTest.MBEAN_STATISTICS_TYPE), Matchers.is(true));
    }
}

