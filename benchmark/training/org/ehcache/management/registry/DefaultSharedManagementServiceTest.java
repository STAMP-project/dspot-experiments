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
package org.ehcache.management.registry;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.concurrent.ExecutionException;
import org.ehcache.CacheManager;
import org.ehcache.config.CacheConfiguration;
import org.ehcache.config.builders.CacheConfigurationBuilder;
import org.ehcache.config.builders.ResourcePoolsBuilder;
import org.ehcache.management.ManagementRegistryServiceConfiguration;
import org.ehcache.management.SharedManagementService;
import org.hamcrest.CoreMatchers;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.Timeout;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.terracotta.management.model.call.ContextualReturn;
import org.terracotta.management.model.capabilities.Capability;
import org.terracotta.management.model.context.Context;
import org.terracotta.management.model.context.ContextContainer;
import org.terracotta.management.model.stats.ContextualStatistics;
import org.terracotta.management.registry.ResultSet;
import org.terracotta.management.registry.StatisticQuery.Builder;


@RunWith(JUnit4.class)
public class DefaultSharedManagementServiceTest {
    CacheManager cacheManager1;

    CacheManager cacheManager2;

    SharedManagementService service;

    ManagementRegistryServiceConfiguration config1;

    ManagementRegistryServiceConfiguration config2;

    @Rule
    public final Timeout globalTimeout = Timeout.seconds(10);

    @Test
    public void testSharedContexts() {
        Assert.assertEquals(2, service.getContextContainers().size());
        ContextContainer contextContainer1 = service.getContextContainers().get(config1.getContext());
        ContextContainer contextContainer2 = service.getContextContainers().get(config2.getContext());
        Assert.assertThat(contextContainer1, CoreMatchers.is(CoreMatchers.notNullValue()));
        Assert.assertThat(contextContainer2, CoreMatchers.is(CoreMatchers.notNullValue()));
        Assert.assertThat(contextContainer1.getName(), CoreMatchers.equalTo("cacheManagerName"));
        Assert.assertThat(contextContainer1.getValue(), CoreMatchers.equalTo("myCM1"));
        Assert.assertThat(contextContainer2.getName(), CoreMatchers.equalTo("cacheManagerName"));
        Assert.assertThat(contextContainer2.getValue(), CoreMatchers.equalTo("myCM2"));
        Assert.assertThat(contextContainer1.getSubContexts().size(), CoreMatchers.equalTo(1));
        Assert.assertThat(contextContainer1.getSubContexts().iterator().next().getName(), CoreMatchers.equalTo("cacheName"));
        Assert.assertThat(contextContainer1.getSubContexts().iterator().next().getValue(), CoreMatchers.equalTo("aCache1"));
        Assert.assertThat(contextContainer2.getSubContexts().size(), CoreMatchers.equalTo(2));
        Assert.assertThat(contextContainer2.getSubContexts().iterator().next().getName(), CoreMatchers.equalTo("cacheName"));
        Assert.assertThat(new ArrayList(contextContainer2.getSubContexts()).get(1).getName(), CoreMatchers.equalTo("cacheName"));
        Assert.assertThat(new ArrayList(contextContainer2.getSubContexts()).get(0).getValue(), Matchers.isIn(Arrays.asList("aCache2", "aCache3")));
        Assert.assertThat(new ArrayList(contextContainer2.getSubContexts()).get(1).getValue(), Matchers.isIn(Arrays.asList("aCache2", "aCache3")));
    }

    @Test
    public void testSharedCapabilities() {
        Assert.assertEquals(2, service.getCapabilitiesByContext().size());
        Collection<? extends Capability> capabilities1 = service.getCapabilitiesByContext().get(config1.getContext());
        Collection<? extends Capability> capabilities2 = service.getCapabilitiesByContext().get(config2.getContext());
        Assert.assertThat(capabilities1, hasSize(4));
        Assert.assertThat(new ArrayList<Capability>(capabilities1).get(0).getName(), CoreMatchers.equalTo("ActionsCapability"));
        Assert.assertThat(new ArrayList<Capability>(capabilities1).get(1).getName(), CoreMatchers.equalTo("SettingsCapability"));
        Assert.assertThat(new ArrayList<Capability>(capabilities1).get(2).getName(), CoreMatchers.equalTo("StatisticCollectorCapability"));
        Assert.assertThat(new ArrayList<Capability>(capabilities1).get(3).getName(), CoreMatchers.equalTo("StatisticsCapability"));
        Assert.assertThat(capabilities2, hasSize(4));
        Assert.assertThat(new ArrayList<Capability>(capabilities2).get(0).getName(), CoreMatchers.equalTo("ActionsCapability"));
        Assert.assertThat(new ArrayList<Capability>(capabilities2).get(1).getName(), CoreMatchers.equalTo("SettingsCapability"));
        Assert.assertThat(new ArrayList<Capability>(capabilities2).get(2).getName(), CoreMatchers.equalTo("StatisticCollectorCapability"));
        Assert.assertThat(new ArrayList<Capability>(capabilities2).get(3).getName(), CoreMatchers.equalTo("StatisticsCapability"));
    }

    @Test
    public void testStats() {
        String statisticName = "Cache:MissCount";
        List<Context> contextList = Arrays.asList(Context.empty().with("cacheManagerName", "myCM1").with("cacheName", "aCache1"), Context.empty().with("cacheManagerName", "myCM2").with("cacheName", "aCache2"), Context.empty().with("cacheManagerName", "myCM2").with("cacheName", "aCache3"));
        cacheManager1.getCache("aCache1", Long.class, String.class).get(1L);
        cacheManager2.getCache("aCache2", Long.class, String.class).get(2L);
        cacheManager2.getCache("aCache3", Long.class, String.class).get(3L);
        Builder builder = service.withCapability("StatisticsCapability").queryStatistic(statisticName).on(contextList);
        ResultSet<ContextualStatistics> allCounters = DefaultSharedManagementServiceTest.getResultSet(builder, contextList, statisticName);
        Assert.assertThat(allCounters.size(), CoreMatchers.equalTo(3));
        Assert.assertThat(allCounters.getResult(contextList.get(0)).size(), CoreMatchers.equalTo(1));
        Assert.assertThat(allCounters.getResult(contextList.get(1)).size(), CoreMatchers.equalTo(1));
        Assert.assertThat(allCounters.getResult(contextList.get(2)).size(), CoreMatchers.equalTo(1));
        Assert.assertThat(allCounters.getResult(contextList.get(0)).getLatestSampleValue(statisticName).get(), CoreMatchers.equalTo(1L));
        Assert.assertThat(allCounters.getResult(contextList.get(1)).getLatestSampleValue(statisticName).get(), CoreMatchers.equalTo(1L));
        Assert.assertThat(allCounters.getResult(contextList.get(2)).getLatestSampleValue(statisticName).get(), CoreMatchers.equalTo(1L));
    }

    @Test
    public void testCall() throws ExecutionException {
        List<Context> contextList = Arrays.asList(Context.empty().with("cacheManagerName", "myCM1").with("cacheName", "aCache1"), Context.empty().with("cacheManagerName", "myCM1").with("cacheName", "aCache4"), Context.empty().with("cacheManagerName", "myCM2").with("cacheName", "aCache2"), Context.empty().with("cacheManagerName", "myCM55").with("cacheName", "aCache55"));
        cacheManager1.getCache("aCache1", Long.class, String.class).put(1L, "1");
        cacheManager2.getCache("aCache2", Long.class, String.class).put(2L, "2");
        Assert.assertThat(cacheManager1.getCache("aCache1", Long.class, String.class).get(1L), CoreMatchers.equalTo("1"));
        Assert.assertThat(cacheManager2.getCache("aCache2", Long.class, String.class).get(2L), CoreMatchers.equalTo("2"));
        CacheConfiguration<Long, String> cacheConfiguration = CacheConfigurationBuilder.newCacheConfigurationBuilder(Long.class, String.class, ResourcePoolsBuilder.heap(10)).build();
        cacheManager1.createCache("aCache4", cacheConfiguration);
        cacheManager1.getCache("aCache4", Long.class, String.class).put(4L, "4");
        Assert.assertThat(cacheManager1.getCache("aCache4", Long.class, String.class).get(4L), CoreMatchers.equalTo("4"));
        ResultSet<? extends ContextualReturn<?>> results = service.withCapability("ActionsCapability").call("clear").on(contextList).build().execute();
        Assert.assertThat(results.size(), Matchers.equalTo(4));
        Assert.assertThat(results.getResult(contextList.get(0)).hasExecuted(), CoreMatchers.is(true));
        Assert.assertThat(results.getResult(contextList.get(1)).hasExecuted(), CoreMatchers.is(true));
        Assert.assertThat(results.getResult(contextList.get(2)).hasExecuted(), CoreMatchers.is(true));
        Assert.assertThat(results.getResult(contextList.get(3)).hasExecuted(), CoreMatchers.is(false));
        Assert.assertThat(results.getResult(contextList.get(0)).getValue(), CoreMatchers.is(CoreMatchers.nullValue()));
        Assert.assertThat(results.getResult(contextList.get(1)).getValue(), CoreMatchers.is(CoreMatchers.nullValue()));
        Assert.assertThat(results.getResult(contextList.get(2)).getValue(), CoreMatchers.is(CoreMatchers.nullValue()));
        try {
            results.getResult(contextList.get(3)).getValue();
            Assert.fail();
        } catch (Exception e) {
            Assert.assertThat(e, CoreMatchers.instanceOf(NoSuchElementException.class));
        }
        Assert.assertThat(cacheManager1.getCache("aCache1", Long.class, String.class).get(1L), CoreMatchers.is(Matchers.nullValue()));
        Assert.assertThat(cacheManager2.getCache("aCache2", Long.class, String.class).get(2L), CoreMatchers.is(Matchers.nullValue()));
        Assert.assertThat(cacheManager1.getCache("aCache4", Long.class, String.class).get(4L), CoreMatchers.is(Matchers.nullValue()));
    }
}

