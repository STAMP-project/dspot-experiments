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
package org.ehcache.clustered.client;


import bitronix.tm.BitronixTransactionManager;
import bitronix.tm.TransactionManagerServices;
import java.net.URI;
import java.util.Map;
import org.ehcache.PersistentCacheManager;
import org.ehcache.StateTransitionException;
import org.ehcache.clustered.client.config.builders.ClusteredResourcePoolBuilder;
import org.ehcache.clustered.client.config.builders.ClusteringServiceConfigurationBuilder;
import org.ehcache.config.CacheConfiguration;
import org.ehcache.config.builders.CacheConfigurationBuilder;
import org.ehcache.config.builders.CacheEventListenerConfigurationBuilder;
import org.ehcache.config.builders.CacheManagerBuilder;
import org.ehcache.config.builders.ResourcePoolsBuilder;
import org.ehcache.config.units.MemoryUnit;
import org.ehcache.event.CacheEvent;
import org.ehcache.event.CacheEventListener;
import org.ehcache.event.EventType;
import org.ehcache.spi.loaderwriter.CacheLoaderWriter;
import org.ehcache.transactions.xa.configuration.XAStoreConfiguration;
import org.ehcache.transactions.xa.txmgr.btm.BitronixTransactionManagerLookup;
import org.ehcache.transactions.xa.txmgr.provider.LookupTransactionManagerProviderConfiguration;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


/**
 * This class should be removed as and when following features are done.
 */
public class UnSupportedCombinationsWIthClusteredCacheTest {
    @Test
    public void testClusteredCacheWithEventListeners() {
        CacheEventListenerConfigurationBuilder cacheEventListenerConfiguration = // <1>
        CacheEventListenerConfigurationBuilder.newEventListenerConfiguration(new UnSupportedCombinationsWIthClusteredCacheTest.TestEventListener(), EventType.CREATED, EventType.UPDATED).unordered().asynchronous();// <2>

        final CacheManagerBuilder<PersistentCacheManager> clusteredCacheManagerBuilder = CacheManagerBuilder.newCacheManagerBuilder().with(ClusteringServiceConfigurationBuilder.cluster(URI.create("terracotta://localhost/my-application")).autoCreate());
        final PersistentCacheManager cacheManager = clusteredCacheManagerBuilder.build(true);
        try {
            CacheConfiguration<Long, String> config = CacheConfigurationBuilder.newCacheConfigurationBuilder(Long.class, String.class, ResourcePoolsBuilder.newResourcePoolsBuilder().with(ClusteredResourcePoolBuilder.clusteredDedicated("primary-server-resource", 8, MemoryUnit.MB))).add(cacheEventListenerConfiguration).build();
            cacheManager.createCache("test", config);
            Assert.fail("IllegalStateException expected");
        } catch (IllegalStateException e) {
            Assert.assertThat(e.getCause().getMessage(), Matchers.is("CacheEventListener is not supported with clustered tiers"));
        }
        cacheManager.close();
    }

    @Test
    public void testClusteredCacheWithXA() throws Exception {
        TransactionManagerServices.getConfiguration().setJournal("null");
        BitronixTransactionManager transactionManager = TransactionManagerServices.getTransactionManager();
        PersistentCacheManager persistentCacheManager = null;
        try {
            CacheManagerBuilder.newCacheManagerBuilder().using(new LookupTransactionManagerProviderConfiguration(BitronixTransactionManagerLookup.class)).with(ClusteringServiceConfigurationBuilder.cluster(URI.create("terracotta://localhost/my-application")).autoCreate()).withCache("xaCache", CacheConfigurationBuilder.newCacheConfigurationBuilder(Long.class, String.class, ResourcePoolsBuilder.newResourcePoolsBuilder().with(ClusteredResourcePoolBuilder.clusteredDedicated("primary-server-resource", 8, MemoryUnit.MB))).add(new XAStoreConfiguration("xaCache")).build()).build(true);
        } catch (StateTransitionException e) {
            Assert.assertThat(e.getCause().getCause().getMessage(), Matchers.is("Unsupported resource type : interface org.ehcache.clustered.client.config.DedicatedClusteredResourcePool"));
        }
        transactionManager.shutdown();
    }

    private static class TestLoaderWriter implements CacheLoaderWriter<Long, String> {
        @Override
        public String load(Long key) {
            return null;
        }

        @Override
        public Map<Long, String> loadAll(Iterable<? extends Long> keys) {
            return null;
        }

        @Override
        public void write(Long key, String value) {
        }

        @Override
        public void writeAll(Iterable<? extends Map.Entry<? extends Long, ? extends String>> entries) {
        }

        @Override
        public void delete(Long key) {
        }

        @Override
        public void deleteAll(Iterable<? extends Long> keys) {
        }
    }

    private static class TestEventListener implements CacheEventListener<Long, String> {
        @Override
        public void onEvent(CacheEvent<? extends Long, ? extends String> event) {
        }
    }
}

