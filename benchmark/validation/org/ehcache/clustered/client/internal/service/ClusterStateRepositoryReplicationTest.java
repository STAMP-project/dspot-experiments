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
package org.ehcache.clustered.client.internal.service;


import ClusteringService.ClusteredCacheIdentifier;
import Consistency.STRONG;
import java.io.Serializable;
import java.net.URI;
import org.ehcache.clustered.client.config.ClusteringServiceConfiguration;
import org.ehcache.clustered.client.config.builders.ClusteredResourcePoolBuilder;
import org.ehcache.clustered.client.config.builders.ClusteringServiceConfigurationBuilder;
import org.ehcache.clustered.client.internal.store.ServerStoreProxy;
import org.ehcache.clustered.client.internal.store.ServerStoreProxy.ServerCallback;
import org.ehcache.clustered.client.internal.store.SimpleClusterTierClientEntity;
import org.ehcache.clustered.client.service.ClusteringService;
import org.ehcache.config.Eviction;
import org.ehcache.config.builders.ExpiryPolicyBuilder;
import org.ehcache.config.builders.ResourcePoolsBuilder;
import org.ehcache.config.units.MemoryUnit;
import org.ehcache.core.config.BaseCacheConfiguration;
import org.ehcache.spi.persistence.StateHolder;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.terracotta.passthrough.PassthroughClusterControl;


public class ClusterStateRepositoryReplicationTest {
    private PassthroughClusterControl clusterControl;

    private static String STRIPENAME = "stripe";

    private static String STRIPE_URI = "passthrough://" + (ClusterStateRepositoryReplicationTest.STRIPENAME);

    @Test
    public void testClusteredStateRepositoryReplication() throws Exception {
        ClusteringServiceConfiguration configuration = ClusteringServiceConfigurationBuilder.cluster(URI.create(ClusterStateRepositoryReplicationTest.STRIPE_URI)).autoCreate().build();
        ClusteringService service = new ClusteringServiceFactory().create(configuration);
        service.start(null);
        BaseCacheConfiguration<Long, String> config = new BaseCacheConfiguration(Long.class, String.class, Eviction.noAdvice(), null, ExpiryPolicyBuilder.noExpiration(), ResourcePoolsBuilder.newResourcePoolsBuilder().with(ClusteredResourcePoolBuilder.clusteredDedicated("test", 2, MemoryUnit.MB)).build());
        ClusteringService.ClusteredCacheIdentifier spaceIdentifier = ((ClusteringService.ClusteredCacheIdentifier) (service.getPersistenceSpaceIdentifier("test", config)));
        ServerStoreProxy serverStoreProxy = service.getServerStoreProxy(spaceIdentifier, new org.ehcache.core.store.StoreConfigurationImpl(config, 1, null, null), STRONG, Mockito.mock(ServerCallback.class));
        SimpleClusterTierClientEntity clientEntity = ClusterStateRepositoryReplicationTest.getEntity(serverStoreProxy);
        ClusterStateRepository stateRepository = new ClusterStateRepository(spaceIdentifier, "test", clientEntity);
        StateHolder<String, String> testHolder = stateRepository.getPersistentStateHolder("testHolder", String.class, String.class, ( c) -> true, null);
        testHolder.putIfAbsent("One", "One");
        testHolder.putIfAbsent("Two", "Two");
        clusterControl.terminateActive();
        clusterControl.waitForActive();
        Assert.assertThat(testHolder.get("One"), Matchers.is("One"));
        Assert.assertThat(testHolder.get("Two"), Matchers.is("Two"));
        service.stop();
    }

    @Test
    public void testClusteredStateRepositoryReplicationWithSerializableKV() throws Exception {
        ClusteringServiceConfiguration configuration = ClusteringServiceConfigurationBuilder.cluster(URI.create(ClusterStateRepositoryReplicationTest.STRIPE_URI)).autoCreate().build();
        ClusteringService service = new ClusteringServiceFactory().create(configuration);
        service.start(null);
        BaseCacheConfiguration<Long, String> config = new BaseCacheConfiguration(Long.class, String.class, Eviction.noAdvice(), null, ExpiryPolicyBuilder.noExpiration(), ResourcePoolsBuilder.newResourcePoolsBuilder().with(ClusteredResourcePoolBuilder.clusteredDedicated("test", 2, MemoryUnit.MB)).build());
        ClusteringService.ClusteredCacheIdentifier spaceIdentifier = ((ClusteringService.ClusteredCacheIdentifier) (service.getPersistenceSpaceIdentifier("test", config)));
        ServerStoreProxy serverStoreProxy = service.getServerStoreProxy(spaceIdentifier, new org.ehcache.core.store.StoreConfigurationImpl(config, 1, null, null), STRONG, Mockito.mock(ServerCallback.class));
        SimpleClusterTierClientEntity clientEntity = ClusterStateRepositoryReplicationTest.getEntity(serverStoreProxy);
        ClusterStateRepository stateRepository = new ClusterStateRepository(new ClusteringService.ClusteredCacheIdentifier() {
            @Override
            public String getId() {
                return "testStateRepo";
            }

            @Override
            public Class<ClusteringService> getServiceType() {
                return ClusteringService.class;
            }
        }, "test", clientEntity);
        StateHolder<ClusterStateRepositoryReplicationTest.TestVal, ClusterStateRepositoryReplicationTest.TestVal> testMap = stateRepository.getPersistentStateHolder("testMap", ClusterStateRepositoryReplicationTest.TestVal.class, ClusterStateRepositoryReplicationTest.TestVal.class, ( c) -> true, null);
        testMap.putIfAbsent(new ClusterStateRepositoryReplicationTest.TestVal("One"), new ClusterStateRepositoryReplicationTest.TestVal("One"));
        testMap.putIfAbsent(new ClusterStateRepositoryReplicationTest.TestVal("Two"), new ClusterStateRepositoryReplicationTest.TestVal("Two"));
        clusterControl.terminateActive();
        clusterControl.waitForActive();
        Assert.assertThat(testMap.get(new ClusterStateRepositoryReplicationTest.TestVal("One")), Matchers.is(new ClusterStateRepositoryReplicationTest.TestVal("One")));
        Assert.assertThat(testMap.get(new ClusterStateRepositoryReplicationTest.TestVal("Two")), Matchers.is(new ClusterStateRepositoryReplicationTest.TestVal("Two")));
        Assert.assertThat(testMap.entrySet(), Matchers.hasSize(2));
        service.stop();
    }

    private static class TestVal implements Serializable {
        private static final long serialVersionUID = 1L;

        final String val;

        private TestVal(String val) {
            this.val = val;
        }

        @Override
        public boolean equals(Object o) {
            if ((this) == o)
                return true;

            if ((o == null) || ((getClass()) != (o.getClass())))
                return false;

            ClusterStateRepositoryReplicationTest.TestVal testVal = ((ClusterStateRepositoryReplicationTest.TestVal) (o));
            return (val) != null ? val.equals(testVal.val) : (testVal.val) == null;
        }

        @Override
        public int hashCode() {
            return (val) != null ? val.hashCode() : 0;
        }
    }
}

