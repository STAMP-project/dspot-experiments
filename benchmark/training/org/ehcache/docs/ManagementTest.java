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
package org.ehcache.docs;


import CapabilityContext.Attribute;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import org.ehcache.Cache;
import org.ehcache.CacheManager;
import org.ehcache.config.CacheConfiguration;
import org.ehcache.config.builders.CacheConfigurationBuilder;
import org.ehcache.config.builders.CacheManagerBuilder;
import org.ehcache.config.builders.ResourcePoolsBuilder;
import org.ehcache.config.units.MemoryUnit;
import org.ehcache.management.ManagementRegistryService;
import org.ehcache.management.providers.statistics.StatsUtil;
import org.ehcache.management.registry.DefaultManagementRegistryConfiguration;
import org.ehcache.management.registry.DefaultManagementRegistryService;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.terracotta.management.model.call.Parameter;
import org.terracotta.management.model.capabilities.Capability;
import org.terracotta.management.model.capabilities.context.CapabilityContext;
import org.terracotta.management.model.capabilities.descriptors.Descriptor;
import org.terracotta.management.model.context.Context;
import org.terracotta.management.model.context.ContextContainer;
import org.terracotta.management.model.stats.ContextualStatistics;
import org.terracotta.management.registry.ResultSet;
import org.terracotta.management.registry.StatisticQuery;


public class ManagementTest {
    @Test
    public void usingManagementRegistry() throws Exception {
        // tag::usingManagementRegistry[]
        CacheManager cacheManager = null;
        try {
            DefaultManagementRegistryConfiguration registryConfiguration = new DefaultManagementRegistryConfiguration().setCacheManagerAlias("myCacheManager1");// <1>

            ManagementRegistryService managementRegistry = new DefaultManagementRegistryService(registryConfiguration);// <2>

            CacheConfiguration<Long, String> cacheConfiguration = CacheConfigurationBuilder.newCacheConfigurationBuilder(Long.class, String.class, ResourcePoolsBuilder.newResourcePoolsBuilder().heap(1, MemoryUnit.MB).offheap(2, MemoryUnit.MB)).build();
            cacheManager = // <3>
            CacheManagerBuilder.newCacheManagerBuilder().withCache("myCache", cacheConfiguration).using(managementRegistry).build(true);
            Object o = managementRegistry.withCapability("StatisticCollectorCapability").call("updateCollectedStatistics", new Parameter("StatisticsCapability"), new Parameter(Arrays.asList("Cache:HitCount", "Cache:MissCount"), Collection.class.getName())).on(Context.create("cacheManagerName", "myCacheManager1")).build().execute().getSingleResult();
            System.out.println(o);
            Cache<Long, String> aCache = cacheManager.getCache("myCache", Long.class, String.class);
            aCache.put(1L, "one");
            aCache.put(0L, "zero");
            aCache.get(1L);// <4>

            aCache.get(0L);// <4>

            aCache.get(0L);
            aCache.get(0L);
            Context context = StatsUtil.createContext(managementRegistry);// <5>

            StatisticQuery query = // <6>
            managementRegistry.withCapability("StatisticsCapability").queryStatistic("Cache:HitCount").on(context).build();
            ResultSet<ContextualStatistics> counters = query.execute();
            ContextualStatistics statisticsContext = counters.getResult(context);
            Assert.assertThat(counters.size(), Matchers.is(1));
        } finally {
            if (cacheManager != null)
                cacheManager.close();

        }
        // end::usingManagementRegistry[]
    }

    @Test
    public void capabilitiesAndContexts() throws Exception {
        // tag::capabilitiesAndContexts[]
        CacheConfiguration<Long, String> cacheConfiguration = CacheConfigurationBuilder.newCacheConfigurationBuilder(Long.class, String.class, ResourcePoolsBuilder.heap(10)).build();
        CacheManager cacheManager = null;
        try {
            ManagementRegistryService managementRegistry = new DefaultManagementRegistryService();
            cacheManager = CacheManagerBuilder.newCacheManagerBuilder().withCache("aCache", cacheConfiguration).using(managementRegistry).build(true);
            Collection<? extends Capability> capabilities = managementRegistry.getCapabilities();// <1>

            Assert.assertThat(capabilities.isEmpty(), Matchers.is(false));
            Capability capability = capabilities.iterator().next();
            String capabilityName = capability.getName();// <2>

            Collection<? extends Descriptor> capabilityDescriptions = capability.getDescriptors();// <3>

            Assert.assertThat(capabilityDescriptions.isEmpty(), Matchers.is(false));
            CapabilityContext capabilityContext = capability.getCapabilityContext();
            Collection<CapabilityContext.Attribute> attributes = capabilityContext.getAttributes();// <4>

            Assert.assertThat(attributes.size(), Matchers.is(2));
            Iterator<CapabilityContext.Attribute> iterator = attributes.iterator();
            CapabilityContext.Attribute attribute1 = iterator.next();
            Assert.assertThat(attribute1.getName(), Matchers.equalTo("cacheManagerName"));// <5>

            Assert.assertThat(attribute1.isRequired(), Matchers.is(true));
            CapabilityContext.Attribute attribute2 = iterator.next();
            Assert.assertThat(attribute2.getName(), Matchers.equalTo("cacheName"));// <6>

            Assert.assertThat(attribute2.isRequired(), Matchers.is(true));
            ContextContainer contextContainer = managementRegistry.getContextContainer();// <7>

            Assert.assertThat(contextContainer.getName(), Matchers.equalTo("cacheManagerName"));// <8>

            Assert.assertThat(contextContainer.getValue(), Matchers.startsWith("cache-manager-"));
            Collection<ContextContainer> subContexts = contextContainer.getSubContexts();
            Assert.assertThat(subContexts.size(), Matchers.is(1));
            ContextContainer subContextContainer = subContexts.iterator().next();
            Assert.assertThat(subContextContainer.getName(), Matchers.equalTo("cacheName"));// <9>

            Assert.assertThat(subContextContainer.getValue(), Matchers.equalTo("aCache"));
        } finally {
            if (cacheManager != null)
                cacheManager.close();

        }
        // end::capabilitiesAndContexts[]
    }

    @Test
    public void actionCall() throws Exception {
        // tag::actionCall[]
        CacheConfiguration<Long, String> cacheConfiguration = CacheConfigurationBuilder.newCacheConfigurationBuilder(Long.class, String.class, ResourcePoolsBuilder.heap(10)).build();
        CacheManager cacheManager = null;
        try {
            ManagementRegistryService managementRegistry = new DefaultManagementRegistryService();
            cacheManager = CacheManagerBuilder.newCacheManagerBuilder().withCache("aCache", cacheConfiguration).using(managementRegistry).build(true);
            Cache<Long, String> aCache = cacheManager.getCache("aCache", Long.class, String.class);
            aCache.put(0L, "zero");// <1>

            Context context = StatsUtil.createContext(managementRegistry);// <2>

            // <3>
            managementRegistry.withCapability("ActionsCapability").call("clear").on(context).build().execute();
            Assert.assertThat(aCache.get(0L), Matchers.is(Matchers.nullValue()));// <4>

        } finally {
            if (cacheManager != null)
                cacheManager.close();

        }
        // end::actionCall[]
    }
}

