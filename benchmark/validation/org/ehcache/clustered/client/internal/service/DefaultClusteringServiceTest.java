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


import ConnectionPropertyNames.CONNECTION_NAME;
import Consistency.EVENTUAL;
import Consistency.STRONG;
import java.net.URI;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import org.ehcache.CachePersistenceException;
import org.ehcache.clustered.client.config.ClusteredResourcePool;
import org.ehcache.clustered.client.config.ClusteringServiceConfiguration;
import org.ehcache.clustered.client.config.builders.ClusteredResourcePoolBuilder;
import org.ehcache.clustered.client.config.builders.ClusteringServiceConfigurationBuilder;
import org.ehcache.clustered.client.internal.ClusterTierManagerClientEntityFactory;
import org.ehcache.clustered.client.internal.UnitTestConnectionService;
import org.ehcache.clustered.client.internal.config.DedicatedClusteredResourcePoolImpl;
import org.ehcache.clustered.client.internal.store.EventualServerStoreProxy;
import org.ehcache.clustered.client.internal.store.ServerStoreProxy;
import org.ehcache.clustered.client.internal.store.ServerStoreProxy.ServerCallback;
import org.ehcache.clustered.client.internal.store.StrongServerStoreProxy;
import org.ehcache.clustered.client.service.ClusteringService;
import org.ehcache.clustered.client.service.ClusteringService.ClusteredCacheIdentifier;
import org.ehcache.clustered.common.ServerSideConfiguration.Pool;
import org.ehcache.clustered.common.internal.exceptions.InvalidServerStoreConfigurationException;
import org.ehcache.clustered.server.ObservableEhcacheServerEntityService;
import org.ehcache.clustered.server.store.ObservableClusterTierServerEntityService;
import org.ehcache.config.ResourcePools;
import org.ehcache.config.ResourceType;
import org.ehcache.config.builders.CacheConfigurationBuilder;
import org.ehcache.config.builders.ResourcePoolsBuilder;
import org.ehcache.config.units.MemoryUnit;
import org.ehcache.core.spi.store.Store;
import org.ehcache.impl.internal.spi.serialization.DefaultSerializationProvider;
import org.ehcache.spi.persistence.PersistableResourceService;
import org.ehcache.spi.persistence.StateRepository;
import org.ehcache.spi.service.MaintainableService;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.terracotta.exception.EntityNotFoundException;

import static DefaultClusteringService.CONNECTION_PREFIX;
import static org.ehcache.config.ResourceType.Core.DISK;
import static org.ehcache.config.ResourceType.Core.HEAP;
import static org.ehcache.config.ResourceType.Core.OFFHEAP;
import static org.ehcache.spi.service.MaintainableService.MaintenanceScope.CACHE_MANAGER;


public class DefaultClusteringServiceTest {
    private static final String CLUSTER_URI_BASE = "terracotta://example.com:9540/";

    private ObservableEhcacheServerEntityService observableEhcacheServerEntityService;

    private ObservableClusterTierServerEntityService observableClusterTierServerEntityService;

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    public void testHandlesResourceType() throws Exception {
        ClusteringServiceConfiguration configuration = ClusteringServiceConfigurationBuilder.cluster(URI.create(((DefaultClusteringServiceTest.CLUSTER_URI_BASE) + "my-application"))).autoCreate().build();
        DefaultClusteringService service = new DefaultClusteringService(configuration);
        Assert.assertThat(service.handlesResourceType(DISK), Matchers.is(false));
        Assert.assertThat(service.handlesResourceType(HEAP), Matchers.is(false));
        Assert.assertThat(service.handlesResourceType(OFFHEAP), Matchers.is(false));
        Assert.assertThat(service.handlesResourceType(DEDICATED), Matchers.is(true));
        Assert.assertThat(service.handlesResourceType(SHARED), Matchers.is(true));
        Assert.assertThat(service.handlesResourceType(new org.ehcache.clustered.client.config.ClusteredResourceType<ClusteredResourcePool>() {
            @Override
            public Class<ClusteredResourcePool> getResourcePoolClass() {
                throw new UnsupportedOperationException(".getResourcePoolClass not implemented");
            }

            @Override
            public boolean isPersistable() {
                throw new UnsupportedOperationException(".isPersistable not implemented");
            }

            @Override
            public boolean requiresSerialization() {
                throw new UnsupportedOperationException(".requiresSerialization not implemented");
            }

            @Override
            public int getTierHeight() {
                throw new UnsupportedOperationException(".getTierHeight not implemented");
            }
        }), Matchers.is(false));
    }

    @Test
    public void testGetPersistenceSpaceIdentifier() throws Exception {
        ClusteringServiceConfiguration configuration = ClusteringServiceConfigurationBuilder.cluster(URI.create(((DefaultClusteringServiceTest.CLUSTER_URI_BASE) + "my-application"))).autoCreate().build();
        DefaultClusteringService service = new DefaultClusteringService(configuration);
        PersistableResourceService.PersistenceSpaceIdentifier<?> spaceIdentifier = service.getPersistenceSpaceIdentifier("cacheAlias", null);
        Assert.assertThat(spaceIdentifier, Matchers.is(Matchers.instanceOf(ClusteredCacheIdentifier.class)));
        Assert.assertThat(getId(), Matchers.is("cacheAlias"));
        Assert.assertThat(service.getPersistenceSpaceIdentifier("cacheAlias", null), Matchers.sameInstance(spaceIdentifier));
    }

    @Test
    public void testCreate() throws Exception {
        CacheConfigurationBuilder<Long, String> configBuilder = CacheConfigurationBuilder.newCacheConfigurationBuilder(Long.class, String.class, ResourcePoolsBuilder.newResourcePoolsBuilder().with(ClusteredResourcePoolBuilder.clusteredShared("primary")));
        ClusteringServiceConfiguration configuration = ClusteringServiceConfigurationBuilder.cluster(URI.create(((DefaultClusteringServiceTest.CLUSTER_URI_BASE) + "my-application"))).autoCreate().build();
        DefaultClusteringService service = new DefaultClusteringService(configuration);
        PersistableResourceService.PersistenceSpaceIdentifier<?> spaceIdentifier = service.getPersistenceSpaceIdentifier("cacheAlias", configBuilder.build());
        Assert.assertThat(spaceIdentifier, Matchers.instanceOf(ClusteredCacheIdentifier.class));
        Assert.assertThat(getId(), Matchers.is("cacheAlias"));
    }

    @Test
    public void testConnectionName() throws Exception {
        String entityIdentifier = "my-application";
        ClusteringServiceConfiguration configuration = new ClusteringServiceConfiguration(URI.create(((DefaultClusteringServiceTest.CLUSTER_URI_BASE) + entityIdentifier)), true, new org.ehcache.clustered.common.ServerSideConfiguration(Collections.<String, Pool>emptyMap()));
        DefaultClusteringService service = new DefaultClusteringService(configuration);
        service.start(null);
        Collection<Properties> propsCollection = UnitTestConnectionService.getConnectionProperties(URI.create(DefaultClusteringServiceTest.CLUSTER_URI_BASE));
        Assert.assertThat(propsCollection.size(), Matchers.is(1));
        Properties props = propsCollection.iterator().next();
        Assert.assertEquals(props.getProperty(CONNECTION_NAME), ((CONNECTION_PREFIX) + entityIdentifier));
    }

    @Test
    public void testStartStopAutoCreate() throws Exception {
        URI clusterUri = URI.create(((DefaultClusteringServiceTest.CLUSTER_URI_BASE) + "my-application"));
        ClusteringServiceConfiguration configuration = ClusteringServiceConfigurationBuilder.cluster(clusterUri).autoCreate().build();
        DefaultClusteringService service = new DefaultClusteringService(configuration);
        Assert.assertThat(service.isConnected(), Matchers.is(false));
        service.start(null);
        Assert.assertThat(service.isConnected(), Matchers.is(true));
        Assert.assertThat(UnitTestConnectionService.getConnectionProperties(clusterUri).size(), Matchers.is(1));
        List<ObservableEhcacheServerEntityService.ObservableEhcacheActiveEntity> activeEntities = observableEhcacheServerEntityService.getServedActiveEntities();
        Assert.assertThat(activeEntities.size(), Matchers.is(1));
        ObservableEhcacheServerEntityService.ObservableEhcacheActiveEntity activeEntity = activeEntities.get(0);
        Assert.assertThat(activeEntity.getDefaultServerResource(), Matchers.is(Matchers.nullValue()));
        Assert.assertThat(activeEntity.getSharedResourcePoolIds(), Matchers.is(Matchers.<String>empty()));
        Assert.assertThat(activeEntity.getDedicatedResourcePoolIds(), Matchers.is(Matchers.<String>empty()));
        Assert.assertThat(activeEntity.getConnectedClients().size(), Matchers.is(1));
        service.stop();
        Assert.assertThat(activeEntity.getConnectedClients().size(), Matchers.is(0));
        Assert.assertThat(UnitTestConnectionService.getConnectionProperties(clusterUri).size(), Matchers.is(0));
    }

    @Test
    public void testStartStopNoAutoCreate() throws Exception {
        URI clusterUri = URI.create(((DefaultClusteringServiceTest.CLUSTER_URI_BASE) + "my-application"));
        ClusteringServiceConfiguration configuration = ClusteringServiceConfigurationBuilder.cluster(clusterUri).build();
        DefaultClusteringService service = new DefaultClusteringService(configuration);
        try {
            service.start(null);
            Assert.fail("Expecting IllegalStateException");
        } catch (IllegalStateException e) {
            Assert.assertThat(e.getCause(), Matchers.is(Matchers.instanceOf(EntityNotFoundException.class)));
        }
        Assert.assertThat(UnitTestConnectionService.getConnectionProperties(clusterUri).size(), Matchers.is(0));
        List<ObservableEhcacheServerEntityService.ObservableEhcacheActiveEntity> activeEntities = observableEhcacheServerEntityService.getServedActiveEntities();
        Assert.assertThat(activeEntities.size(), Matchers.is(0));
        service.stop();
    }

    /**
     * Ensures a second client specifying auto-create can start {@link DefaultClusteringService} while the
     * creator is still connected.
     */
    @Test
    public void testStartStopAutoCreateTwiceA() throws Exception {
        ClusteringServiceConfiguration configuration = ClusteringServiceConfigurationBuilder.cluster(URI.create(((DefaultClusteringServiceTest.CLUSTER_URI_BASE) + "my-application"))).autoCreate().build();
        DefaultClusteringService firstService = new DefaultClusteringService(configuration);
        firstService.start(null);
        DefaultClusteringService secondService = new DefaultClusteringService(configuration);
        secondService.start(null);
        List<ObservableEhcacheServerEntityService.ObservableEhcacheActiveEntity> activeEntities = observableEhcacheServerEntityService.getServedActiveEntities();
        Assert.assertThat(activeEntities.size(), Matchers.is(1));
        ObservableEhcacheServerEntityService.ObservableEhcacheActiveEntity activeEntity = activeEntities.get(0);
        Assert.assertThat(activeEntity.getDefaultServerResource(), Matchers.is(Matchers.nullValue()));
        Assert.assertThat(activeEntity.getSharedResourcePoolIds(), Matchers.is(Matchers.<String>empty()));
        Assert.assertThat(activeEntity.getDedicatedResourcePoolIds(), Matchers.is(Matchers.<String>empty()));
        Assert.assertThat(activeEntity.getConnectedClients().size(), Matchers.is(2));
        firstService.stop();
        Assert.assertThat(activeEntity.getConnectedClients().size(), Matchers.is(1));
        secondService.stop();
        Assert.assertThat(activeEntity.getConnectedClients().size(), Matchers.is(0));
    }

    /**
     * Ensures a second client specifying auto-create can start {@link DefaultClusteringService} while the
     * creator is not connected.
     */
    @Test
    public void testStartStopAutoCreateTwiceB() throws Exception {
        ClusteringServiceConfiguration configuration = ClusteringServiceConfigurationBuilder.cluster(URI.create(((DefaultClusteringServiceTest.CLUSTER_URI_BASE) + "my-application"))).autoCreate().build();
        DefaultClusteringService firstService = new DefaultClusteringService(configuration);
        firstService.start(null);
        firstService.stop();
        List<ObservableEhcacheServerEntityService.ObservableEhcacheActiveEntity> activeEntities = observableEhcacheServerEntityService.getServedActiveEntities();
        Assert.assertThat(activeEntities.size(), Matchers.is(1));
        DefaultClusteringService secondService = new DefaultClusteringService(configuration);
        secondService.start(null);
        activeEntities = observableEhcacheServerEntityService.getServedActiveEntities();
        Assert.assertThat(activeEntities.size(), Matchers.is(1));
        ObservableEhcacheServerEntityService.ObservableEhcacheActiveEntity activeEntity = activeEntities.get(0);
        Assert.assertThat(activeEntity.getDefaultServerResource(), Matchers.is(Matchers.nullValue()));
        Assert.assertThat(activeEntity.getSharedResourcePoolIds(), Matchers.is(Matchers.<String>empty()));
        Assert.assertThat(activeEntity.getDedicatedResourcePoolIds(), Matchers.is(Matchers.<String>empty()));
        Assert.assertThat(activeEntity.getConnectedClients().size(), Matchers.is(1));
        secondService.stop();
        Assert.assertThat(activeEntity.getConnectedClients().size(), Matchers.is(0));
    }

    @Test
    public void testStartForMaintenanceAutoStart() throws Exception {
        URI clusterUri = URI.create(((DefaultClusteringServiceTest.CLUSTER_URI_BASE) + "my-application"));
        ClusteringServiceConfiguration configuration = ClusteringServiceConfigurationBuilder.cluster(clusterUri).autoCreate().build();
        DefaultClusteringService service = new DefaultClusteringService(configuration);
        Assert.assertThat(service.isConnected(), Matchers.is(false));
        service.startForMaintenance(null, CACHE_MANAGER);
        Assert.assertThat(service.isConnected(), Matchers.is(true));
        Assert.assertThat(UnitTestConnectionService.getConnectionProperties(clusterUri).size(), Matchers.is(1));
        List<ObservableEhcacheServerEntityService.ObservableEhcacheActiveEntity> activeEntities = observableEhcacheServerEntityService.getServedActiveEntities();
        Assert.assertThat(activeEntities.size(), Matchers.is(0));
        // startForMaintenance does **not** create an ClusterTierManagerActiveEntity
        service.stop();
        Assert.assertThat(UnitTestConnectionService.getConnectionProperties(clusterUri).size(), Matchers.is(0));
    }

    @Test
    public void testStartForMaintenanceOtherAutoCreate() throws Exception {
        ClusteringServiceConfiguration configuration = ClusteringServiceConfigurationBuilder.cluster(URI.create(((DefaultClusteringServiceTest.CLUSTER_URI_BASE) + "my-application"))).autoCreate().build();
        DefaultClusteringService createService = new DefaultClusteringService(configuration);
        createService.start(null);
        DefaultClusteringService maintenanceService = new DefaultClusteringService(configuration);
        try {
            maintenanceService.startForMaintenance(null, CACHE_MANAGER);
            Assert.fail("Expecting IllegalStateException");
        } catch (IllegalStateException e) {
            // Expected
        }
        List<ObservableEhcacheServerEntityService.ObservableEhcacheActiveEntity> activeEntities = observableEhcacheServerEntityService.getServedActiveEntities();
        Assert.assertThat(activeEntities.size(), Matchers.is(1));
        ObservableEhcacheServerEntityService.ObservableEhcacheActiveEntity activeEntity = activeEntities.get(0);
        Assert.assertThat(activeEntity.getDefaultServerResource(), Matchers.is(Matchers.nullValue()));
        Assert.assertThat(activeEntity.getSharedResourcePoolIds(), Matchers.is(Matchers.<String>empty()));
        Assert.assertThat(activeEntity.getDedicatedResourcePoolIds(), Matchers.is(Matchers.<String>empty()));
        Assert.assertThat(activeEntity.getConnectedClients().size(), Matchers.is(1));
        createService.stop();
        Assert.assertThat(activeEntity.getConnectedClients().size(), Matchers.is(0));
        maintenanceService.stop();
        Assert.assertThat(activeEntity.getConnectedClients().size(), Matchers.is(0));
    }

    @Test
    public void testStartForMaintenanceOtherCreated() throws Exception {
        ClusteringServiceConfiguration configuration = ClusteringServiceConfigurationBuilder.cluster(URI.create(((DefaultClusteringServiceTest.CLUSTER_URI_BASE) + "my-application"))).autoCreate().build();
        DefaultClusteringService createService = new DefaultClusteringService(configuration);
        createService.start(null);
        createService.stop();
        List<ObservableEhcacheServerEntityService.ObservableEhcacheActiveEntity> activeEntities = observableEhcacheServerEntityService.getServedActiveEntities();
        Assert.assertThat(activeEntities.size(), Matchers.is(1));
        ObservableEhcacheServerEntityService.ObservableEhcacheActiveEntity activeEntity = activeEntities.get(0);
        Assert.assertThat(activeEntity.getConnectedClients().size(), Matchers.is(0));
        DefaultClusteringService maintenanceService = new DefaultClusteringService(configuration);
        maintenanceService.startForMaintenance(null, CACHE_MANAGER);
        activeEntities = observableEhcacheServerEntityService.getServedActiveEntities();
        Assert.assertThat(activeEntities.size(), Matchers.is(1));
        activeEntity = activeEntities.get(0);
        Assert.assertThat(activeEntity.getDefaultServerResource(), Matchers.is(Matchers.nullValue()));
        Assert.assertThat(activeEntity.getSharedResourcePoolIds(), Matchers.is(Matchers.<String>empty()));
        Assert.assertThat(activeEntity.getDedicatedResourcePoolIds(), Matchers.is(Matchers.<String>empty()));
        // startForMaintenance does **not** establish a link with the ClusterTierManagerActiveEntity
        Assert.assertThat(activeEntity.getConnectedClients().size(), Matchers.is(0));
        maintenanceService.stop();
        Assert.assertThat(activeEntity.getConnectedClients().size(), Matchers.is(0));
    }

    @Test
    public void testMultipleAutoCreateClientsRunConcurrently() throws InterruptedException, ExecutionException {
        final ClusteringServiceConfiguration configuration = ClusteringServiceConfigurationBuilder.cluster(URI.create(((DefaultClusteringServiceTest.CLUSTER_URI_BASE) + "my-application"))).autoCreate().build();
        Callable<DefaultClusteringService> task = () -> {
            DefaultClusteringService service = new DefaultClusteringService(configuration);
            service.start(null);
            return service;
        };
        ExecutorService executor = Executors.newCachedThreadPool();
        try {
            List<Future<DefaultClusteringService>> results = executor.invokeAll(Collections.nCopies(4, task), 5, TimeUnit.MINUTES);
            for (Future<DefaultClusteringService> result : results) {
                Assert.assertThat(result.isDone(), Matchers.is(true));
            }
            for (Future<DefaultClusteringService> result : results) {
                result.get().stop();
            }
        } finally {
            executor.shutdown();
        }
    }

    @Test
    public void testStartForMaintenanceInterlock() throws Exception {
        ClusteringServiceConfiguration configuration = ClusteringServiceConfigurationBuilder.cluster(URI.create(((DefaultClusteringServiceTest.CLUSTER_URI_BASE) + "my-application"))).autoCreate().build();
        DefaultClusteringService maintenanceService1 = new DefaultClusteringService(configuration);
        maintenanceService1.startForMaintenance(null, CACHE_MANAGER);
        List<ObservableEhcacheServerEntityService.ObservableEhcacheActiveEntity> activeEntities = observableEhcacheServerEntityService.getServedActiveEntities();
        Assert.assertThat(activeEntities.size(), Matchers.is(0));
        DefaultClusteringService maintenanceService2 = new DefaultClusteringService(configuration);
        try {
            maintenanceService2.startForMaintenance(null, CACHE_MANAGER);
            Assert.fail("Expecting IllegalStateException");
        } catch (IllegalStateException e) {
            Assert.assertThat(e.getMessage(), Matchers.containsString(" acquire cluster-wide "));
        }
        maintenanceService1.stop();
    }

    @Test
    public void testStartForMaintenanceSequence() throws Exception {
        ClusteringServiceConfiguration configuration = ClusteringServiceConfigurationBuilder.cluster(URI.create(((DefaultClusteringServiceTest.CLUSTER_URI_BASE) + "my-application"))).autoCreate().build();
        DefaultClusteringService maintenanceService1 = new DefaultClusteringService(configuration);
        maintenanceService1.startForMaintenance(null, CACHE_MANAGER);
        maintenanceService1.stop();
        List<ObservableEhcacheServerEntityService.ObservableEhcacheActiveEntity> activeEntities = observableEhcacheServerEntityService.getServedActiveEntities();
        Assert.assertThat(activeEntities.size(), Matchers.is(0));
        DefaultClusteringService maintenanceService2 = new DefaultClusteringService(configuration);
        maintenanceService2.startForMaintenance(null, CACHE_MANAGER);
        maintenanceService2.stop();
        activeEntities = observableEhcacheServerEntityService.getServedActiveEntities();
        Assert.assertThat(activeEntities.size(), Matchers.is(0));
    }

    @Test
    public void testBasicConfiguration() throws Exception {
        ClusteringServiceConfiguration configuration = ClusteringServiceConfigurationBuilder.cluster(URI.create(((DefaultClusteringServiceTest.CLUSTER_URI_BASE) + "my-application"))).autoCreate().defaultServerResource("defaultResource").resourcePool("sharedPrimary", 2, MemoryUnit.MB, "serverResource1").resourcePool("sharedSecondary", 2, MemoryUnit.MB, "serverResource2").resourcePool("sharedTertiary", 4, MemoryUnit.MB).build();
        DefaultClusteringService createService = new DefaultClusteringService(configuration);
        createService.start(null);
        List<ObservableEhcacheServerEntityService.ObservableEhcacheActiveEntity> activeEntities = observableEhcacheServerEntityService.getServedActiveEntities();
        Assert.assertThat(activeEntities.size(), Matchers.is(1));
        ObservableEhcacheServerEntityService.ObservableEhcacheActiveEntity activeEntity = activeEntities.get(0);
        Assert.assertThat(activeEntity.getDefaultServerResource(), Matchers.is("defaultResource"));
        Assert.assertThat(activeEntity.getSharedResourcePoolIds(), Matchers.containsInAnyOrder("sharedPrimary", "sharedSecondary", "sharedTertiary"));
        Assert.assertThat(activeEntity.getDedicatedResourcePoolIds(), Matchers.is(Matchers.<String>empty()));
        Assert.assertThat(activeEntity.getConnectedClients().size(), Matchers.is(1));
        Assert.assertThat(activeEntity.getStores(), Matchers.is(Matchers.<String>empty()));
        createService.stop();
        activeEntities = observableEhcacheServerEntityService.getServedActiveEntities();
        Assert.assertThat(activeEntities.size(), Matchers.is(1));
        activeEntity = activeEntities.get(0);
        Assert.assertThat(activeEntity.getDefaultServerResource(), Matchers.is("defaultResource"));
        Assert.assertThat(activeEntity.getSharedResourcePoolIds(), Matchers.containsInAnyOrder("sharedPrimary", "sharedSecondary", "sharedTertiary"));
        Assert.assertThat(activeEntity.getDedicatedResourcePoolIds(), Matchers.is(Matchers.<String>empty()));
        Assert.assertThat(activeEntity.getConnectedClients().size(), Matchers.is(0));
        Assert.assertThat(activeEntity.getStores(), Matchers.is(Matchers.<String>empty()));
    }

    @Test
    public void testGetServerStoreProxySharedAutoCreate() throws Exception {
        String cacheAlias = "cacheAlias";
        String targetPool = "sharedPrimary";
        ClusteringServiceConfiguration configuration = ClusteringServiceConfigurationBuilder.cluster(URI.create(((DefaultClusteringServiceTest.CLUSTER_URI_BASE) + "my-application"))).autoCreate().defaultServerResource("defaultResource").resourcePool(targetPool, 2, MemoryUnit.MB, "serverResource1").resourcePool("sharedSecondary", 2, MemoryUnit.MB, "serverResource2").resourcePool("sharedTertiary", 4, MemoryUnit.MB).build();
        DefaultClusteringService service = new DefaultClusteringService(configuration);
        service.start(null);
        DefaultSerializationProvider serializationProvider = new DefaultSerializationProvider(null);
        serializationProvider.start(TestServiceProvider.providerContaining());
        Store.Configuration<Long, String> storeConfiguration = getSharedStoreConfig(targetPool, serializationProvider, Long.class, String.class);
        ServerStoreProxy serverStoreProxy = service.getServerStoreProxy(getClusteredCacheIdentifier(service, cacheAlias), storeConfiguration, EVENTUAL, Mockito.mock(ServerCallback.class));
        Assert.assertThat(serverStoreProxy.getCacheId(), Matchers.is(cacheAlias));
        List<ObservableEhcacheServerEntityService.ObservableEhcacheActiveEntity> activeEntities = observableEhcacheServerEntityService.getServedActiveEntities();
        Assert.assertThat(activeEntities.size(), Matchers.is(1));
        ObservableEhcacheServerEntityService.ObservableEhcacheActiveEntity activeEntity = activeEntities.get(0);
        Assert.assertThat(activeEntity.getDefaultServerResource(), Matchers.is("defaultResource"));
        Assert.assertThat(activeEntity.getSharedResourcePoolIds(), Matchers.containsInAnyOrder(targetPool, "sharedSecondary", "sharedTertiary"));
        Assert.assertThat(activeEntity.getDedicatedResourcePoolIds(), Matchers.is(Matchers.<String>empty()));
        Assert.assertThat(activeEntity.getConnectedClients().size(), Matchers.is(1));
        Assert.assertThat(activeEntity.getStores(), Matchers.containsInAnyOrder(cacheAlias));
        List<ObservableClusterTierServerEntityService.ObservableClusterTierActiveEntity> clusterTierActiveEntities = observableClusterTierServerEntityService.getServedActiveEntities();
        Assert.assertThat(clusterTierActiveEntities.size(), Matchers.is(1));
        ObservableClusterTierServerEntityService.ObservableClusterTierActiveEntity clusterTierActiveEntity = clusterTierActiveEntities.get(0);
        Assert.assertThat(clusterTierActiveEntity.getConnectedClients().size(), Matchers.is(1));
        service.stop();
        Assert.assertThat(activeEntity.getDefaultServerResource(), Matchers.is("defaultResource"));
        Assert.assertThat(activeEntity.getSharedResourcePoolIds(), Matchers.containsInAnyOrder(targetPool, "sharedSecondary", "sharedTertiary"));
        Assert.assertThat(activeEntity.getDedicatedResourcePoolIds(), Matchers.is(Matchers.<String>empty()));
        Assert.assertThat(activeEntity.getConnectedClients().size(), Matchers.is(0));
        Assert.assertThat(activeEntity.getStores(), Matchers.containsInAnyOrder(cacheAlias));
        Assert.assertThat(clusterTierActiveEntity.getConnectedClients(), Matchers.empty());
    }

    @Test
    public void testGetServerStoreProxySharedNoAutoCreateNonExistent() throws Exception {
        String cacheAlias = "cacheAlias";
        String targetPool = "sharedPrimary";
        ClusteringServiceConfiguration creationConfig = ClusteringServiceConfigurationBuilder.cluster(URI.create(((DefaultClusteringServiceTest.CLUSTER_URI_BASE) + "my-application"))).autoCreate().defaultServerResource("defaultResource").resourcePool(targetPool, 2, MemoryUnit.MB, "serverResource1").resourcePool("sharedSecondary", 2, MemoryUnit.MB, "serverResource2").resourcePool("sharedTertiary", 4, MemoryUnit.MB).build();
        DefaultClusteringService creationService = new DefaultClusteringService(creationConfig);
        creationService.start(null);
        creationService.stop();
        ClusteringServiceConfiguration accessConfig = ClusteringServiceConfigurationBuilder.cluster(URI.create(((DefaultClusteringServiceTest.CLUSTER_URI_BASE) + "my-application"))).expecting().defaultServerResource("defaultResource").resourcePool(targetPool, 2, MemoryUnit.MB, "serverResource1").resourcePool("sharedSecondary", 2, MemoryUnit.MB, "serverResource2").resourcePool("sharedTertiary", 4, MemoryUnit.MB).build();
        DefaultClusteringService accessService = new DefaultClusteringService(accessConfig);
        accessService.start(null);
        DefaultSerializationProvider serializationProvider = new DefaultSerializationProvider(null);
        serializationProvider.start(TestServiceProvider.providerContaining());
        Store.Configuration<Long, String> storeConfiguration = getSharedStoreConfig(targetPool, serializationProvider, Long.class, String.class);
        try {
            accessService.getServerStoreProxy(getClusteredCacheIdentifier(accessService, cacheAlias), storeConfiguration, EVENTUAL, Mockito.mock(ServerCallback.class));
            Assert.fail("Expecting CachePersistenceException");
        } catch (CachePersistenceException e) {
            Assert.assertThat(e.getMessage(), Matchers.containsString(" does not exist"));
        }
        List<ObservableEhcacheServerEntityService.ObservableEhcacheActiveEntity> activeEntities = observableEhcacheServerEntityService.getServedActiveEntities();
        Assert.assertThat(activeEntities.size(), Matchers.is(1));
        ObservableEhcacheServerEntityService.ObservableEhcacheActiveEntity activeEntity = activeEntities.get(0);
        Assert.assertThat(activeEntity.getDefaultServerResource(), Matchers.is("defaultResource"));
        Assert.assertThat(activeEntity.getSharedResourcePoolIds(), Matchers.containsInAnyOrder(targetPool, "sharedSecondary", "sharedTertiary"));
        Assert.assertThat(activeEntity.getDedicatedResourcePoolIds(), Matchers.is(Matchers.<String>empty()));
        Assert.assertThat(activeEntity.getConnectedClients().size(), Matchers.is(1));
        Assert.assertThat(activeEntity.getStores(), Matchers.is(Matchers.<String>empty()));
        accessService.stop();
        Assert.assertThat(activeEntity.getDefaultServerResource(), Matchers.is("defaultResource"));
        Assert.assertThat(activeEntity.getSharedResourcePoolIds(), Matchers.containsInAnyOrder(targetPool, "sharedSecondary", "sharedTertiary"));
        Assert.assertThat(activeEntity.getDedicatedResourcePoolIds(), Matchers.is(Matchers.<String>empty()));
        Assert.assertThat(activeEntity.getConnectedClients().size(), Matchers.is(0));
        Assert.assertThat(activeEntity.getStores(), Matchers.is(Matchers.<String>empty()));
    }

    @Test
    public void testGetServerStoreProxySharedNoAutoCreateExists() throws Exception {
        String cacheAlias = "cacheAlias";
        String targetPool = "sharedPrimary";
        ClusteringServiceConfiguration creationConfig = ClusteringServiceConfigurationBuilder.cluster(URI.create(((DefaultClusteringServiceTest.CLUSTER_URI_BASE) + "my-application"))).autoCreate().defaultServerResource("defaultResource").resourcePool(targetPool, 2, MemoryUnit.MB, "serverResource1").resourcePool("sharedSecondary", 2, MemoryUnit.MB, "serverResource2").resourcePool("sharedTertiary", 4, MemoryUnit.MB).build();
        DefaultClusteringService creationService = new DefaultClusteringService(creationConfig);
        creationService.start(null);
        DefaultSerializationProvider serializationProvider = new DefaultSerializationProvider(null);
        serializationProvider.start(TestServiceProvider.providerContaining());
        Store.Configuration<Long, String> creationStoreConfig = getSharedStoreConfig(targetPool, serializationProvider, Long.class, String.class);
        ServerStoreProxy creationServerStoreProxy = creationService.getServerStoreProxy(getClusteredCacheIdentifier(creationService, cacheAlias), creationStoreConfig, EVENTUAL, Mockito.mock(ServerCallback.class));
        Assert.assertThat(creationServerStoreProxy.getCacheId(), Matchers.is(cacheAlias));
        creationService.stop();
        List<ObservableEhcacheServerEntityService.ObservableEhcacheActiveEntity> activeEntities = observableEhcacheServerEntityService.getServedActiveEntities();
        Assert.assertThat(activeEntities.size(), Matchers.is(1));
        ObservableEhcacheServerEntityService.ObservableEhcacheActiveEntity activeEntity = activeEntities.get(0);
        Assert.assertThat(activeEntity.getDefaultServerResource(), Matchers.is("defaultResource"));
        Assert.assertThat(activeEntity.getSharedResourcePoolIds(), Matchers.containsInAnyOrder(targetPool, "sharedSecondary", "sharedTertiary"));
        Assert.assertThat(activeEntity.getDedicatedResourcePoolIds(), Matchers.is(Matchers.<String>empty()));
        Assert.assertThat(activeEntity.getConnectedClients().size(), Matchers.is(0));
        Assert.assertThat(activeEntity.getStores(), Matchers.containsInAnyOrder(cacheAlias));
        List<ObservableClusterTierServerEntityService.ObservableClusterTierActiveEntity> clusterTierActiveEntities = observableClusterTierServerEntityService.getServedActiveEntities();
        Assert.assertThat(clusterTierActiveEntities.size(), Matchers.is(1));
        ObservableClusterTierServerEntityService.ObservableClusterTierActiveEntity clusterTierActiveEntity = clusterTierActiveEntities.get(0);
        Assert.assertThat(clusterTierActiveEntity.getConnectedClients(), Matchers.empty());
        ClusteringServiceConfiguration accessConfig = ClusteringServiceConfigurationBuilder.cluster(URI.create(((DefaultClusteringServiceTest.CLUSTER_URI_BASE) + "my-application"))).expecting().defaultServerResource("defaultResource").resourcePool(targetPool, 2, MemoryUnit.MB, "serverResource1").resourcePool("sharedSecondary", 2, MemoryUnit.MB, "serverResource2").resourcePool("sharedTertiary", 4, MemoryUnit.MB).build();
        DefaultClusteringService accessService = new DefaultClusteringService(accessConfig);
        accessService.start(null);
        Store.Configuration<Long, String> accessStoreConfiguration = getSharedStoreConfig(targetPool, serializationProvider, Long.class, String.class);
        ServerStoreProxy accessServerStoreProxy = accessService.getServerStoreProxy(getClusteredCacheIdentifier(accessService, cacheAlias), accessStoreConfiguration, EVENTUAL, Mockito.mock(ServerCallback.class));
        Assert.assertThat(accessServerStoreProxy.getCacheId(), Matchers.is(cacheAlias));
        activeEntities = observableEhcacheServerEntityService.getServedActiveEntities();
        Assert.assertThat(activeEntities.size(), Matchers.is(1));
        activeEntity = activeEntities.get(0);
        Assert.assertThat(activeEntity.getDefaultServerResource(), Matchers.is("defaultResource"));
        Assert.assertThat(activeEntity.getSharedResourcePoolIds(), Matchers.containsInAnyOrder(targetPool, "sharedSecondary", "sharedTertiary"));
        Assert.assertThat(activeEntity.getDedicatedResourcePoolIds(), Matchers.is(Matchers.<String>empty()));
        Assert.assertThat(activeEntity.getConnectedClients().size(), Matchers.is(1));
        Assert.assertThat(clusterTierActiveEntity.getConnectedClients().size(), Matchers.is(1));
        accessService.stop();
        Assert.assertThat(activeEntity.getDefaultServerResource(), Matchers.is("defaultResource"));
        Assert.assertThat(activeEntity.getSharedResourcePoolIds(), Matchers.containsInAnyOrder(targetPool, "sharedSecondary", "sharedTertiary"));
        Assert.assertThat(activeEntity.getDedicatedResourcePoolIds(), Matchers.is(Matchers.<String>empty()));
        Assert.assertThat(activeEntity.getConnectedClients().size(), Matchers.is(0));
        Assert.assertThat(activeEntity.getStores(), Matchers.containsInAnyOrder(cacheAlias));
        Assert.assertThat(clusterTierActiveEntity.getConnectedClients(), Matchers.empty());
    }

    /**
     * Ensures that two clients using auto-create can gain access to the same {@code ServerStore}.
     */
    @Test
    public void testGetServerStoreProxySharedAutoCreateTwice() throws Exception {
        String cacheAlias = "cacheAlias";
        String targetPool = "sharedPrimary";
        ClusteringServiceConfiguration configuration = ClusteringServiceConfigurationBuilder.cluster(URI.create(((DefaultClusteringServiceTest.CLUSTER_URI_BASE) + "my-application"))).autoCreate().defaultServerResource("defaultResource").resourcePool(targetPool, 2, MemoryUnit.MB, "serverResource1").resourcePool("sharedSecondary", 2, MemoryUnit.MB, "serverResource2").resourcePool("sharedTertiary", 4, MemoryUnit.MB).build();
        DefaultClusteringService firstService = new DefaultClusteringService(configuration);
        firstService.start(null);
        DefaultSerializationProvider serializationProvider = new DefaultSerializationProvider(null);
        serializationProvider.start(TestServiceProvider.providerContaining());
        Store.Configuration<Long, String> firstSharedStoreConfig = getSharedStoreConfig(targetPool, serializationProvider, Long.class, String.class);
        ServerStoreProxy firstServerStoreProxy = firstService.getServerStoreProxy(getClusteredCacheIdentifier(firstService, cacheAlias), firstSharedStoreConfig, EVENTUAL, Mockito.mock(ServerCallback.class));
        Assert.assertThat(firstServerStoreProxy.getCacheId(), Matchers.is(cacheAlias));
        DefaultClusteringService secondService = new DefaultClusteringService(configuration);
        secondService.start(null);
        Store.Configuration<Long, String> secondSharedStoreConfig = getSharedStoreConfig(targetPool, serializationProvider, Long.class, String.class);
        ServerStoreProxy secondServerStoreProxy = secondService.getServerStoreProxy(getClusteredCacheIdentifier(secondService, cacheAlias), secondSharedStoreConfig, EVENTUAL, Mockito.mock(ServerCallback.class));
        Assert.assertThat(secondServerStoreProxy.getCacheId(), Matchers.is(cacheAlias));
        List<ObservableEhcacheServerEntityService.ObservableEhcacheActiveEntity> activeEntities = observableEhcacheServerEntityService.getServedActiveEntities();
        Assert.assertThat(activeEntities.size(), Matchers.is(1));
        ObservableEhcacheServerEntityService.ObservableEhcacheActiveEntity activeEntity = activeEntities.get(0);
        Assert.assertThat(activeEntity.getSharedResourcePoolIds(), Matchers.containsInAnyOrder(targetPool, "sharedSecondary", "sharedTertiary"));
        Assert.assertThat(activeEntity.getDedicatedResourcePoolIds(), Matchers.is(Matchers.<String>empty()));
        Assert.assertThat(activeEntity.getConnectedClients().size(), Matchers.is(2));
        Assert.assertThat(activeEntity.getStores(), Matchers.containsInAnyOrder(cacheAlias));
        List<ObservableClusterTierServerEntityService.ObservableClusterTierActiveEntity> clusterTierActiveEntities = observableClusterTierServerEntityService.getServedActiveEntities();
        Assert.assertThat(clusterTierActiveEntities.size(), Matchers.is(1));
        ObservableClusterTierServerEntityService.ObservableClusterTierActiveEntity clusterTierActiveEntity = clusterTierActiveEntities.get(0);
        Assert.assertThat(clusterTierActiveEntity.getConnectedClients().size(), Matchers.is(2));
        firstService.stop();
        Assert.assertThat(activeEntity.getSharedResourcePoolIds(), Matchers.containsInAnyOrder(targetPool, "sharedSecondary", "sharedTertiary"));
        Assert.assertThat(activeEntity.getConnectedClients().size(), Matchers.is(1));
        Assert.assertThat(activeEntity.getStores(), Matchers.containsInAnyOrder(cacheAlias));
        Assert.assertThat(clusterTierActiveEntity.getConnectedClients().size(), Matchers.is(1));
        secondService.stop();
        Assert.assertThat(activeEntity.getSharedResourcePoolIds(), Matchers.containsInAnyOrder(targetPool, "sharedSecondary", "sharedTertiary"));
        Assert.assertThat(activeEntity.getConnectedClients().size(), Matchers.is(0));
        Assert.assertThat(activeEntity.getStores(), Matchers.containsInAnyOrder(cacheAlias));
        Assert.assertThat(clusterTierActiveEntity.getConnectedClients(), Matchers.empty());
    }

    @Test
    public void testReleaseServerStoreProxyShared() throws Exception {
        String cacheAlias = "cacheAlias";
        String targetPool = "sharedPrimary";
        ClusteringServiceConfiguration configuration = ClusteringServiceConfigurationBuilder.cluster(URI.create(((DefaultClusteringServiceTest.CLUSTER_URI_BASE) + "my-application"))).autoCreate().defaultServerResource("defaultResource").resourcePool(targetPool, 2, MemoryUnit.MB, "serverResource1").resourcePool("sharedSecondary", 2, MemoryUnit.MB, "serverResource2").resourcePool("sharedTertiary", 4, MemoryUnit.MB).build();
        DefaultClusteringService creationService = new DefaultClusteringService(configuration);
        creationService.start(null);
        DefaultSerializationProvider serializationProvider = new DefaultSerializationProvider(null);
        serializationProvider.start(TestServiceProvider.providerContaining());
        Store.Configuration<Long, String> storeConfiguration = getSharedStoreConfig(targetPool, serializationProvider, Long.class, String.class);
        ServerStoreProxy serverStoreProxy = creationService.getServerStoreProxy(getClusteredCacheIdentifier(creationService, cacheAlias), storeConfiguration, EVENTUAL, Mockito.mock(ServerCallback.class));
        Assert.assertThat(serverStoreProxy.getCacheId(), Matchers.is(cacheAlias));
        List<ObservableEhcacheServerEntityService.ObservableEhcacheActiveEntity> activeEntities = observableEhcacheServerEntityService.getServedActiveEntities();
        ObservableEhcacheServerEntityService.ObservableEhcacheActiveEntity activeEntity = activeEntities.get(0);
        Assert.assertThat(activeEntity.getConnectedClients().size(), Matchers.is(1));
        Assert.assertThat(activeEntity.getStores(), Matchers.containsInAnyOrder(cacheAlias));
        List<ObservableClusterTierServerEntityService.ObservableClusterTierActiveEntity> clusterTierActiveEntities = observableClusterTierServerEntityService.getServedActiveEntities();
        Assert.assertThat(clusterTierActiveEntities.size(), Matchers.is(1));
        ObservableClusterTierServerEntityService.ObservableClusterTierActiveEntity clusterTierActiveEntity = clusterTierActiveEntities.get(0);
        Assert.assertThat(clusterTierActiveEntity.getConnectedClients().size(), Matchers.is(1));
        creationService.releaseServerStoreProxy(serverStoreProxy, false);
        Assert.assertThat(activeEntity.getConnectedClients().size(), Matchers.is(1));
        Assert.assertThat(activeEntity.getStores(), Matchers.containsInAnyOrder(cacheAlias));
        Assert.assertThat(clusterTierActiveEntity.getConnectedClients(), Matchers.empty());
        try {
            creationService.releaseServerStoreProxy(serverStoreProxy, false);
            Assert.fail("Expecting IllegalStateException");
        } catch (IllegalStateException e) {
            Assert.assertThat(e.getMessage(), Matchers.containsString("Endpoint closed"));
        }
        creationService.stop();
    }

    @Test
    public void testGetServerStoreProxyDedicatedAutoCreate() throws Exception {
        String cacheAlias = "cacheAlias";
        String targetResource = "serverResource2";
        ClusteringServiceConfiguration configuration = ClusteringServiceConfigurationBuilder.cluster(URI.create(((DefaultClusteringServiceTest.CLUSTER_URI_BASE) + "my-application"))).autoCreate().defaultServerResource("defaultResource").resourcePool("sharedPrimary", 2, MemoryUnit.MB, "serverResource1").resourcePool("sharedSecondary", 2, MemoryUnit.MB, "serverResource2").resourcePool("sharedTertiary", 4, MemoryUnit.MB).build();
        DefaultClusteringService service = new DefaultClusteringService(configuration);
        service.start(null);
        DefaultSerializationProvider serializationProvider = new DefaultSerializationProvider(null);
        serializationProvider.start(TestServiceProvider.providerContaining());
        Store.Configuration<Long, String> storeConfiguration = getDedicatedStoreConfig(targetResource, serializationProvider, Long.class, String.class);
        ServerStoreProxy serverStoreProxy = service.getServerStoreProxy(getClusteredCacheIdentifier(service, cacheAlias), storeConfiguration, EVENTUAL, Mockito.mock(ServerCallback.class));
        Assert.assertThat(serverStoreProxy.getCacheId(), Matchers.is(cacheAlias));
        List<ObservableEhcacheServerEntityService.ObservableEhcacheActiveEntity> activeEntities = observableEhcacheServerEntityService.getServedActiveEntities();
        Assert.assertThat(activeEntities.size(), Matchers.is(1));
        ObservableEhcacheServerEntityService.ObservableEhcacheActiveEntity activeEntity = activeEntities.get(0);
        Assert.assertThat(activeEntity.getDefaultServerResource(), Matchers.is("defaultResource"));
        Assert.assertThat(activeEntity.getSharedResourcePoolIds(), Matchers.containsInAnyOrder("sharedPrimary", "sharedSecondary", "sharedTertiary"));
        Assert.assertThat(activeEntity.getDedicatedResourcePoolIds(), Matchers.containsInAnyOrder(cacheAlias));
        Assert.assertThat(activeEntity.getConnectedClients().size(), Matchers.is(1));
        Assert.assertThat(activeEntity.getStores(), Matchers.containsInAnyOrder(cacheAlias));
        List<ObservableClusterTierServerEntityService.ObservableClusterTierActiveEntity> clusterTierActiveEntities = observableClusterTierServerEntityService.getServedActiveEntities();
        Assert.assertThat(clusterTierActiveEntities.size(), Matchers.is(1));
        ObservableClusterTierServerEntityService.ObservableClusterTierActiveEntity clusterTierActiveEntity = clusterTierActiveEntities.get(0);
        Assert.assertThat(clusterTierActiveEntity.getConnectedClients().size(), Matchers.is(1));
        service.stop();
        Assert.assertThat(activeEntity.getDefaultServerResource(), Matchers.is("defaultResource"));
        Assert.assertThat(activeEntity.getSharedResourcePoolIds(), Matchers.containsInAnyOrder("sharedPrimary", "sharedSecondary", "sharedTertiary"));
        Assert.assertThat(activeEntity.getDedicatedResourcePoolIds(), Matchers.containsInAnyOrder(cacheAlias));
        Assert.assertThat(activeEntity.getConnectedClients().size(), Matchers.is(0));
        Assert.assertThat(activeEntity.getStores(), Matchers.containsInAnyOrder(cacheAlias));
        Assert.assertThat(clusterTierActiveEntity.getConnectedClients(), Matchers.empty());
    }

    @Test
    public void testGetServerStoreProxyDedicatedNoAutoCreateNonExistent() throws Exception {
        String cacheAlias = "cacheAlias";
        String targetResource = "serverResource2";
        ClusteringServiceConfiguration creationConfig = ClusteringServiceConfigurationBuilder.cluster(URI.create(((DefaultClusteringServiceTest.CLUSTER_URI_BASE) + "my-application"))).autoCreate().defaultServerResource("defaultResource").resourcePool("sharedPrimary", 2, MemoryUnit.MB, "serverResource1").resourcePool("sharedSecondary", 2, MemoryUnit.MB, "serverResource2").resourcePool("sharedTertiary", 4, MemoryUnit.MB).build();
        DefaultClusteringService creationService = new DefaultClusteringService(creationConfig);
        creationService.start(null);
        creationService.stop();
        ClusteringServiceConfiguration accessConfig = ClusteringServiceConfigurationBuilder.cluster(URI.create(((DefaultClusteringServiceTest.CLUSTER_URI_BASE) + "my-application"))).expecting().defaultServerResource("defaultResource").resourcePool("sharedPrimary", 2, MemoryUnit.MB, "serverResource1").resourcePool("sharedSecondary", 2, MemoryUnit.MB, "serverResource2").resourcePool("sharedTertiary", 4, MemoryUnit.MB).build();
        DefaultClusteringService accessService = new DefaultClusteringService(accessConfig);
        accessService.start(null);
        DefaultSerializationProvider serializationProvider = new DefaultSerializationProvider(null);
        serializationProvider.start(TestServiceProvider.providerContaining());
        Store.Configuration<Long, String> storeConfiguration = getDedicatedStoreConfig(targetResource, serializationProvider, Long.class, String.class);
        try {
            accessService.getServerStoreProxy(getClusteredCacheIdentifier(accessService, cacheAlias), storeConfiguration, EVENTUAL, Mockito.mock(ServerCallback.class));
            Assert.fail("Expecting CachePersistenceException");
        } catch (CachePersistenceException e) {
            Assert.assertThat(e.getMessage(), Matchers.containsString(" does not exist"));
        }
        List<ObservableEhcacheServerEntityService.ObservableEhcacheActiveEntity> activeEntities = observableEhcacheServerEntityService.getServedActiveEntities();
        Assert.assertThat(activeEntities.size(), Matchers.is(1));
        ObservableEhcacheServerEntityService.ObservableEhcacheActiveEntity activeEntity = activeEntities.get(0);
        Assert.assertThat(activeEntity.getDefaultServerResource(), Matchers.is("defaultResource"));
        Assert.assertThat(activeEntity.getSharedResourcePoolIds(), Matchers.containsInAnyOrder("sharedPrimary", "sharedSecondary", "sharedTertiary"));
        Assert.assertThat(activeEntity.getDedicatedResourcePoolIds(), Matchers.is(Matchers.<String>empty()));
        Assert.assertThat(activeEntity.getConnectedClients().size(), Matchers.is(1));
        Assert.assertThat(activeEntity.getStores(), Matchers.is(Matchers.<String>empty()));
        accessService.stop();
        Assert.assertThat(activeEntity.getDefaultServerResource(), Matchers.is("defaultResource"));
        Assert.assertThat(activeEntity.getSharedResourcePoolIds(), Matchers.containsInAnyOrder("sharedPrimary", "sharedSecondary", "sharedTertiary"));
        Assert.assertThat(activeEntity.getDedicatedResourcePoolIds(), Matchers.is(Matchers.<String>empty()));
        Assert.assertThat(activeEntity.getConnectedClients().size(), Matchers.is(0));
        Assert.assertThat(activeEntity.getStores(), Matchers.is(Matchers.<String>empty()));
    }

    @Test
    public void testGetServerStoreProxyDedicatedNoAutoCreateExists() throws Exception {
        String cacheAlias = "cacheAlias";
        String targetResource = "serverResource2";
        ClusteringServiceConfiguration creationConfig = ClusteringServiceConfigurationBuilder.cluster(URI.create(((DefaultClusteringServiceTest.CLUSTER_URI_BASE) + "my-application"))).autoCreate().defaultServerResource("defaultResource").resourcePool("sharedPrimary", 2, MemoryUnit.MB, "serverResource1").resourcePool("sharedSecondary", 2, MemoryUnit.MB, "serverResource2").resourcePool("sharedTertiary", 4, MemoryUnit.MB).build();
        DefaultClusteringService creationService = new DefaultClusteringService(creationConfig);
        creationService.start(null);
        DefaultSerializationProvider serializationProvider = new DefaultSerializationProvider(null);
        serializationProvider.start(TestServiceProvider.providerContaining());
        Store.Configuration<Long, String> creationStoreConfig = getDedicatedStoreConfig(targetResource, serializationProvider, Long.class, String.class);
        ServerStoreProxy creationServerStoreProxy = creationService.getServerStoreProxy(getClusteredCacheIdentifier(creationService, cacheAlias), creationStoreConfig, EVENTUAL, Mockito.mock(ServerCallback.class));
        Assert.assertThat(creationServerStoreProxy.getCacheId(), Matchers.is(cacheAlias));
        creationService.stop();
        List<ObservableEhcacheServerEntityService.ObservableEhcacheActiveEntity> activeEntities = observableEhcacheServerEntityService.getServedActiveEntities();
        Assert.assertThat(activeEntities.size(), Matchers.is(1));
        ObservableEhcacheServerEntityService.ObservableEhcacheActiveEntity activeEntity = activeEntities.get(0);
        Assert.assertThat(activeEntity.getDefaultServerResource(), Matchers.is("defaultResource"));
        Assert.assertThat(activeEntity.getSharedResourcePoolIds(), Matchers.containsInAnyOrder("sharedPrimary", "sharedSecondary", "sharedTertiary"));
        Assert.assertThat(activeEntity.getDedicatedResourcePoolIds(), Matchers.containsInAnyOrder(cacheAlias));
        Assert.assertThat(activeEntity.getConnectedClients().size(), Matchers.is(0));
        Assert.assertThat(activeEntity.getStores(), Matchers.containsInAnyOrder(cacheAlias));
        List<ObservableClusterTierServerEntityService.ObservableClusterTierActiveEntity> clusterTierActiveEntities = observableClusterTierServerEntityService.getServedActiveEntities();
        Assert.assertThat(clusterTierActiveEntities.size(), Matchers.is(1));
        ObservableClusterTierServerEntityService.ObservableClusterTierActiveEntity clusterTierActiveEntity = clusterTierActiveEntities.get(0);
        Assert.assertThat(clusterTierActiveEntity.getConnectedClients(), Matchers.empty());
        ClusteringServiceConfiguration accessConfig = ClusteringServiceConfigurationBuilder.cluster(URI.create(((DefaultClusteringServiceTest.CLUSTER_URI_BASE) + "my-application"))).expecting().defaultServerResource("defaultResource").resourcePool("sharedPrimary", 2, MemoryUnit.MB, "serverResource1").resourcePool("sharedSecondary", 2, MemoryUnit.MB, "serverResource2").resourcePool("sharedTertiary", 4, MemoryUnit.MB).build();
        DefaultClusteringService accessService = new DefaultClusteringService(accessConfig);
        accessService.start(null);
        Store.Configuration<Long, String> accessStoreConfiguration = getDedicatedStoreConfig(targetResource, serializationProvider, Long.class, String.class);
        ServerStoreProxy accessServerStoreProxy = accessService.getServerStoreProxy(getClusteredCacheIdentifier(accessService, cacheAlias), accessStoreConfiguration, EVENTUAL, Mockito.mock(ServerCallback.class));
        Assert.assertThat(accessServerStoreProxy.getCacheId(), Matchers.is(cacheAlias));
        activeEntities = observableEhcacheServerEntityService.getServedActiveEntities();
        Assert.assertThat(activeEntities.size(), Matchers.is(1));
        activeEntity = activeEntities.get(0);
        Assert.assertThat(activeEntity.getDefaultServerResource(), Matchers.is("defaultResource"));
        Assert.assertThat(activeEntity.getSharedResourcePoolIds(), Matchers.containsInAnyOrder("sharedPrimary", "sharedSecondary", "sharedTertiary"));
        Assert.assertThat(activeEntity.getDedicatedResourcePoolIds(), Matchers.containsInAnyOrder(cacheAlias));
        Assert.assertThat(activeEntity.getConnectedClients().size(), Matchers.is(1));
        Assert.assertThat(clusterTierActiveEntity.getConnectedClients().size(), Matchers.is(1));
        accessService.stop();
        Assert.assertThat(activeEntity.getDefaultServerResource(), Matchers.is("defaultResource"));
        Assert.assertThat(activeEntity.getSharedResourcePoolIds(), Matchers.containsInAnyOrder("sharedPrimary", "sharedSecondary", "sharedTertiary"));
        Assert.assertThat(activeEntity.getDedicatedResourcePoolIds(), Matchers.containsInAnyOrder(cacheAlias));
        Assert.assertThat(activeEntity.getConnectedClients().size(), Matchers.is(0));
        Assert.assertThat(activeEntity.getStores(), Matchers.containsInAnyOrder(cacheAlias));
        Assert.assertThat(clusterTierActiveEntity.getConnectedClients(), Matchers.empty());
    }

    /**
     * Ensures that two clients using auto-create can gain access to the same {@code ServerStore}.
     */
    @Test
    public void testGetServerStoreProxyDedicatedAutoCreateTwice() throws Exception {
        String cacheAlias = "cacheAlias";
        String targetResource = "serverResource2";
        ClusteringServiceConfiguration configuration = ClusteringServiceConfigurationBuilder.cluster(URI.create(((DefaultClusteringServiceTest.CLUSTER_URI_BASE) + "my-application"))).autoCreate().defaultServerResource("defaultResource").resourcePool("sharedPrimary", 2, MemoryUnit.MB, "serverResource1").resourcePool("sharedSecondary", 2, MemoryUnit.MB, "serverResource2").resourcePool("sharedTertiary", 4, MemoryUnit.MB).build();
        DefaultClusteringService firstService = new DefaultClusteringService(configuration);
        firstService.start(null);
        DefaultSerializationProvider serializationProvider = new DefaultSerializationProvider(null);
        serializationProvider.start(TestServiceProvider.providerContaining());
        Store.Configuration<Long, String> firstSharedStoreConfig = getDedicatedStoreConfig(targetResource, serializationProvider, Long.class, String.class);
        ServerStoreProxy firstServerStoreProxy = firstService.getServerStoreProxy(getClusteredCacheIdentifier(firstService, cacheAlias), firstSharedStoreConfig, EVENTUAL, Mockito.mock(ServerCallback.class));
        Assert.assertThat(firstServerStoreProxy.getCacheId(), Matchers.is(cacheAlias));
        DefaultClusteringService secondService = new DefaultClusteringService(configuration);
        secondService.start(null);
        Store.Configuration<Long, String> secondSharedStoreConfig = getDedicatedStoreConfig(targetResource, serializationProvider, Long.class, String.class);
        ServerStoreProxy secondServerStoreProxy = secondService.getServerStoreProxy(getClusteredCacheIdentifier(secondService, cacheAlias), secondSharedStoreConfig, EVENTUAL, Mockito.mock(ServerCallback.class));
        Assert.assertThat(secondServerStoreProxy.getCacheId(), Matchers.is(cacheAlias));
        List<ObservableEhcacheServerEntityService.ObservableEhcacheActiveEntity> activeEntities = observableEhcacheServerEntityService.getServedActiveEntities();
        Assert.assertThat(activeEntities.size(), Matchers.is(1));
        ObservableEhcacheServerEntityService.ObservableEhcacheActiveEntity activeEntity = activeEntities.get(0);
        Assert.assertThat(activeEntity.getSharedResourcePoolIds(), Matchers.containsInAnyOrder("sharedPrimary", "sharedSecondary", "sharedTertiary"));
        Assert.assertThat(activeEntity.getDedicatedResourcePoolIds(), Matchers.containsInAnyOrder(cacheAlias));
        Assert.assertThat(activeEntity.getConnectedClients().size(), Matchers.is(2));
        Assert.assertThat(activeEntity.getStores(), Matchers.containsInAnyOrder(cacheAlias));
        List<ObservableClusterTierServerEntityService.ObservableClusterTierActiveEntity> clusterTierActiveEntities = observableClusterTierServerEntityService.getServedActiveEntities();
        Assert.assertThat(clusterTierActiveEntities.size(), Matchers.is(1));
        ObservableClusterTierServerEntityService.ObservableClusterTierActiveEntity clusterTierActiveEntity = clusterTierActiveEntities.get(0);
        Assert.assertThat(clusterTierActiveEntity.getConnectedClients().size(), Matchers.is(2));
        firstService.stop();
        Assert.assertThat(activeEntity.getSharedResourcePoolIds(), Matchers.containsInAnyOrder("sharedPrimary", "sharedSecondary", "sharedTertiary"));
        Assert.assertThat(activeEntity.getDedicatedResourcePoolIds(), Matchers.containsInAnyOrder(cacheAlias));
        Assert.assertThat(activeEntity.getConnectedClients().size(), Matchers.is(1));
        Assert.assertThat(activeEntity.getStores(), Matchers.containsInAnyOrder(cacheAlias));
        Assert.assertThat(clusterTierActiveEntity.getConnectedClients().size(), Matchers.is(1));
        secondService.stop();
        Assert.assertThat(activeEntity.getSharedResourcePoolIds(), Matchers.containsInAnyOrder("sharedPrimary", "sharedSecondary", "sharedTertiary"));
        Assert.assertThat(activeEntity.getDedicatedResourcePoolIds(), Matchers.containsInAnyOrder(cacheAlias));
        Assert.assertThat(activeEntity.getConnectedClients().size(), Matchers.is(0));
        Assert.assertThat(activeEntity.getStores(), Matchers.containsInAnyOrder(cacheAlias));
        Assert.assertThat(clusterTierActiveEntity.getConnectedClients(), Matchers.empty());
    }

    @Test
    public void testReleaseServerStoreProxyDedicated() throws Exception {
        String cacheAlias = "cacheAlias";
        String targetResource = "serverResource2";
        ClusteringServiceConfiguration configuration = ClusteringServiceConfigurationBuilder.cluster(URI.create(((DefaultClusteringServiceTest.CLUSTER_URI_BASE) + "my-application"))).autoCreate().defaultServerResource("defaultResource").resourcePool("sharedPrimary", 2, MemoryUnit.MB, "serverResource1").resourcePool("sharedSecondary", 2, MemoryUnit.MB, "serverResource2").resourcePool("sharedTertiary", 4, MemoryUnit.MB).build();
        DefaultClusteringService creationService = new DefaultClusteringService(configuration);
        creationService.start(null);
        DefaultSerializationProvider serializationProvider = new DefaultSerializationProvider(null);
        serializationProvider.start(TestServiceProvider.providerContaining());
        Store.Configuration<Long, String> storeConfiguration = getDedicatedStoreConfig(targetResource, serializationProvider, Long.class, String.class);
        ServerStoreProxy serverStoreProxy = creationService.getServerStoreProxy(getClusteredCacheIdentifier(creationService, cacheAlias), storeConfiguration, EVENTUAL, Mockito.mock(ServerCallback.class));
        Assert.assertThat(serverStoreProxy.getCacheId(), Matchers.is(cacheAlias));
        List<ObservableEhcacheServerEntityService.ObservableEhcacheActiveEntity> activeEntities = observableEhcacheServerEntityService.getServedActiveEntities();
        ObservableEhcacheServerEntityService.ObservableEhcacheActiveEntity activeEntity = activeEntities.get(0);
        Assert.assertThat(activeEntity.getDedicatedResourcePoolIds(), Matchers.containsInAnyOrder(cacheAlias));
        Assert.assertThat(activeEntity.getConnectedClients().size(), Matchers.is(1));
        Assert.assertThat(activeEntity.getStores(), Matchers.containsInAnyOrder(cacheAlias));
        List<ObservableClusterTierServerEntityService.ObservableClusterTierActiveEntity> clusterTierActiveEntities = observableClusterTierServerEntityService.getServedActiveEntities();
        Assert.assertThat(clusterTierActiveEntities.size(), Matchers.is(1));
        ObservableClusterTierServerEntityService.ObservableClusterTierActiveEntity clusterTierActiveEntity = clusterTierActiveEntities.get(0);
        Assert.assertThat(clusterTierActiveEntity.getConnectedClients(), Matchers.not(Matchers.empty()));
        creationService.releaseServerStoreProxy(serverStoreProxy, false);
        Assert.assertThat(activeEntity.getDedicatedResourcePoolIds(), Matchers.containsInAnyOrder(cacheAlias));
        Assert.assertThat(activeEntity.getConnectedClients().size(), Matchers.is(1));
        Assert.assertThat(activeEntity.getStores(), Matchers.containsInAnyOrder(cacheAlias));
        Assert.assertThat(clusterTierActiveEntity.getConnectedClients(), Matchers.empty());
        try {
            creationService.releaseServerStoreProxy(serverStoreProxy, false);
            Assert.fail("Expecting IllegalStateException");
        } catch (IllegalStateException e) {
            Assert.assertThat(e.getMessage(), Matchers.containsString("Endpoint closed"));
        }
        creationService.stop();
    }

    @Test
    public void testGetServerStoreProxySharedDestroy() throws Exception {
        String cacheAlias = "cacheAlias";
        String targetPool = "sharedPrimary";
        ClusteringServiceConfiguration configuration = ClusteringServiceConfigurationBuilder.cluster(URI.create(((DefaultClusteringServiceTest.CLUSTER_URI_BASE) + "my-application"))).autoCreate().defaultServerResource("defaultResource").resourcePool(targetPool, 2, MemoryUnit.MB, "serverResource1").resourcePool("sharedSecondary", 2, MemoryUnit.MB, "serverResource2").resourcePool("sharedTertiary", 4, MemoryUnit.MB).build();
        DefaultClusteringService creationService = new DefaultClusteringService(configuration);
        creationService.start(null);
        DefaultSerializationProvider serializationProvider = new DefaultSerializationProvider(null);
        serializationProvider.start(TestServiceProvider.providerContaining());
        Store.Configuration<Long, String> storeConfiguration = getSharedStoreConfig(targetPool, serializationProvider, Long.class, String.class);
        ServerStoreProxy serverStoreProxy = creationService.getServerStoreProxy(getClusteredCacheIdentifier(creationService, cacheAlias), storeConfiguration, EVENTUAL, Mockito.mock(ServerCallback.class));
        Assert.assertThat(serverStoreProxy.getCacheId(), Matchers.is(cacheAlias));
        List<ObservableEhcacheServerEntityService.ObservableEhcacheActiveEntity> activeEntities = observableEhcacheServerEntityService.getServedActiveEntities();
        ObservableEhcacheServerEntityService.ObservableEhcacheActiveEntity activeEntity = activeEntities.get(0);
        Assert.assertThat(activeEntity.getConnectedClients().size(), Matchers.is(1));
        Assert.assertThat(activeEntity.getStores(), Matchers.containsInAnyOrder(cacheAlias));
        List<ObservableClusterTierServerEntityService.ObservableClusterTierActiveEntity> clusterTierActiveEntities = observableClusterTierServerEntityService.getServedActiveEntities();
        Assert.assertThat(clusterTierActiveEntities.size(), Matchers.is(1));
        ObservableClusterTierServerEntityService.ObservableClusterTierActiveEntity clusterTierActiveEntity = clusterTierActiveEntities.get(0);
        Assert.assertThat(clusterTierActiveEntity.getConnectedClients().size(), Matchers.is(1));
        try {
            creationService.destroy(cacheAlias);
            Assert.fail("Expecting CachePersistenceException");
        } catch (CachePersistenceException e) {
            Assert.assertThat(DefaultClusteringServiceTest.getRootCause(e).getMessage(), Matchers.containsString(" in use by "));
        }
        creationService.releaseServerStoreProxy(serverStoreProxy, false);
        Assert.assertThat(activeEntity.getStores(), Matchers.containsInAnyOrder(cacheAlias));
        Assert.assertThat(clusterTierActiveEntity.getConnectedClients(), Matchers.empty());
        creationService.destroy(cacheAlias);
        Assert.assertThat(activeEntity.getConnectedClients().size(), Matchers.is(1));
        Assert.assertThat(activeEntity.getStores(), Matchers.is(Matchers.<String>empty()));
        creationService.stop();
    }

    @Test
    public void testGetServerStoreProxyDedicatedDestroy() throws Exception {
        String cacheAlias = "cacheAlias";
        String targetResource = "serverResource2";
        ClusteringServiceConfiguration configuration = ClusteringServiceConfigurationBuilder.cluster(URI.create(((DefaultClusteringServiceTest.CLUSTER_URI_BASE) + "my-application"))).autoCreate().defaultServerResource("defaultResource").resourcePool("sharedPrimary", 2, MemoryUnit.MB, "serverResource1").resourcePool("sharedSecondary", 2, MemoryUnit.MB, "serverResource2").resourcePool("sharedTertiary", 4, MemoryUnit.MB).build();
        DefaultClusteringService creationService = new DefaultClusteringService(configuration);
        creationService.start(null);
        DefaultSerializationProvider serializationProvider = new DefaultSerializationProvider(null);
        serializationProvider.start(TestServiceProvider.providerContaining());
        Store.Configuration<Long, String> storeConfiguration = getDedicatedStoreConfig(targetResource, serializationProvider, Long.class, String.class);
        ServerStoreProxy serverStoreProxy = creationService.getServerStoreProxy(getClusteredCacheIdentifier(creationService, cacheAlias), storeConfiguration, EVENTUAL, Mockito.mock(ServerCallback.class));
        Assert.assertThat(serverStoreProxy.getCacheId(), Matchers.is(cacheAlias));
        List<ObservableEhcacheServerEntityService.ObservableEhcacheActiveEntity> activeEntities = observableEhcacheServerEntityService.getServedActiveEntities();
        ObservableEhcacheServerEntityService.ObservableEhcacheActiveEntity activeEntity = activeEntities.get(0);
        Assert.assertThat(activeEntity.getDedicatedResourcePoolIds(), Matchers.containsInAnyOrder(cacheAlias));
        Assert.assertThat(activeEntity.getConnectedClients().size(), Matchers.is(1));
        List<ObservableClusterTierServerEntityService.ObservableClusterTierActiveEntity> clusterTierActiveEntities = observableClusterTierServerEntityService.getServedActiveEntities();
        Assert.assertThat(clusterTierActiveEntities.size(), Matchers.is(1));
        ObservableClusterTierServerEntityService.ObservableClusterTierActiveEntity clusterTierActiveEntity = clusterTierActiveEntities.get(0);
        Assert.assertThat(clusterTierActiveEntity.getConnectedClients(), Matchers.not(Matchers.empty()));
        try {
            creationService.destroy(cacheAlias);
            Assert.fail("Expecting CachePersistenceException");
        } catch (CachePersistenceException e) {
            Assert.assertThat(DefaultClusteringServiceTest.getRootCause(e).getMessage(), Matchers.containsString(" in use by "));
        }
        creationService.releaseServerStoreProxy(serverStoreProxy, false);
        Assert.assertThat(activeEntity.getDedicatedResourcePoolIds(), Matchers.containsInAnyOrder(cacheAlias));
        Assert.assertThat(activeEntity.getStores(), Matchers.containsInAnyOrder(cacheAlias));
        Assert.assertThat(clusterTierActiveEntity.getConnectedClients(), Matchers.empty());
        creationService.destroy(cacheAlias);
        Assert.assertThat(activeEntity.getDedicatedResourcePoolIds(), Matchers.is(Matchers.<String>empty()));
        Assert.assertThat(activeEntity.getConnectedClients().size(), Matchers.is(1));
        Assert.assertThat(activeEntity.getStores(), Matchers.is(Matchers.<String>empty()));
        creationService.stop();
    }

    @Test
    public void testDestroyCantBeCalledIfStopped() throws Exception {
        String cacheAlias = "cacheAlias";
        String targetResource = "serverResource2";
        ClusteringServiceConfiguration configuration = ClusteringServiceConfigurationBuilder.cluster(URI.create(((DefaultClusteringServiceTest.CLUSTER_URI_BASE) + "my-application"))).autoCreate().defaultServerResource("defaultResource").build();
        DefaultClusteringService creationService = new DefaultClusteringService(configuration);
        expectedException.expect(IllegalStateException.class);
        expectedException.expectMessage(Matchers.endsWith(" should be started to call destroy"));
        creationService.destroy(cacheAlias);
    }

    @Test
    public void testDestroyAllNoStores() throws Exception {
        ClusteringServiceConfiguration configuration = ClusteringServiceConfigurationBuilder.cluster(URI.create(((DefaultClusteringServiceTest.CLUSTER_URI_BASE) + "my-application"))).autoCreate().defaultServerResource("defaultResource").resourcePool("sharedPrimary", 2, MemoryUnit.MB, "serverResource1").resourcePool("sharedSecondary", 2, MemoryUnit.MB, "serverResource2").resourcePool("sharedTertiary", 4, MemoryUnit.MB).build();
        DefaultClusteringService createService = new DefaultClusteringService(configuration);
        createService.start(null);
        createService.stop();
        List<ObservableEhcacheServerEntityService.ObservableEhcacheActiveEntity> activeEntities = observableEhcacheServerEntityService.getServedActiveEntities();
        Assert.assertThat(activeEntities.size(), Matchers.is(1));
        ObservableEhcacheServerEntityService.ObservableEhcacheActiveEntity activeEntity = activeEntities.get(0);
        Assert.assertThat(activeEntity.getDefaultServerResource(), Matchers.is("defaultResource"));
        Assert.assertThat(activeEntity.getSharedResourcePoolIds(), Matchers.containsInAnyOrder("sharedPrimary", "sharedSecondary", "sharedTertiary"));
        Assert.assertThat(activeEntity.getDedicatedResourcePoolIds(), Matchers.is(Matchers.<String>empty()));
        Assert.assertThat(activeEntity.getConnectedClients().size(), Matchers.is(0));
        Assert.assertThat(activeEntity.getStores(), Matchers.is(Matchers.<String>empty()));
        try {
            createService.destroyAll();
            Assert.fail("Expecting IllegalStateException");
        } catch (IllegalStateException e) {
            Assert.assertThat(e.getMessage(), Matchers.containsString("Maintenance mode required"));
        }
        createService.startForMaintenance(null, CACHE_MANAGER);
        createService.destroyAll();
        activeEntities = observableEhcacheServerEntityService.getServedActiveEntities();
        Assert.assertThat(activeEntities.size(), Matchers.is(1));
        activeEntity = activeEntities.get(0);
        Assert.assertThat(activeEntity.getDefaultServerResource(), Matchers.is(Matchers.nullValue()));
        Assert.assertThat(activeEntity.getSharedResourcePoolIds(), Matchers.is(Matchers.<String>empty()));
        Assert.assertThat(activeEntity.getDedicatedResourcePoolIds(), Matchers.is(Matchers.<String>empty()));
        Assert.assertThat(activeEntity.getConnectedClients().size(), Matchers.is(0));
        Assert.assertThat(activeEntity.getStores(), Matchers.is(Matchers.<String>empty()));
    }

    @Test
    public void testDestroyAllWithStores() throws Exception {
        ClusteringServiceConfiguration configuration = ClusteringServiceConfigurationBuilder.cluster(URI.create(((DefaultClusteringServiceTest.CLUSTER_URI_BASE) + "my-application"))).autoCreate().defaultServerResource("defaultResource").resourcePool("sharedPrimary", 2, MemoryUnit.MB, "serverResource1").resourcePool("sharedSecondary", 2, MemoryUnit.MB, "serverResource2").resourcePool("sharedTertiary", 4, MemoryUnit.MB).build();
        DefaultClusteringService createService = new DefaultClusteringService(configuration);
        createService.start(null);
        DefaultSerializationProvider serializationProvider = new DefaultSerializationProvider(null);
        serializationProvider.start(TestServiceProvider.providerContaining());
        Store.Configuration<Long, String> sharedStoreConfiguration = getSharedStoreConfig("sharedPrimary", serializationProvider, Long.class, String.class);
        ServerStoreProxy sharedProxy = createService.getServerStoreProxy(getClusteredCacheIdentifier(createService, "sharedCache"), sharedStoreConfiguration, EVENTUAL, Mockito.mock(ServerCallback.class));
        Assert.assertThat(sharedProxy.getCacheId(), Matchers.is("sharedCache"));
        Store.Configuration<Long, String> storeConfiguration = getDedicatedStoreConfig("serverResource2", serializationProvider, Long.class, String.class);
        ServerStoreProxy dedicatedProxy = createService.getServerStoreProxy(getClusteredCacheIdentifier(createService, "dedicatedCache"), storeConfiguration, EVENTUAL, Mockito.mock(ServerCallback.class));
        Assert.assertThat(dedicatedProxy.getCacheId(), Matchers.is("dedicatedCache"));
        createService.stop();
        List<ObservableEhcacheServerEntityService.ObservableEhcacheActiveEntity> activeEntities = observableEhcacheServerEntityService.getServedActiveEntities();
        Assert.assertThat(activeEntities.size(), Matchers.is(1));
        ObservableEhcacheServerEntityService.ObservableEhcacheActiveEntity activeEntity = activeEntities.get(0);
        Assert.assertThat(activeEntity.getDefaultServerResource(), Matchers.is("defaultResource"));
        Assert.assertThat(activeEntity.getSharedResourcePoolIds(), Matchers.containsInAnyOrder("sharedPrimary", "sharedSecondary", "sharedTertiary"));
        Assert.assertThat(activeEntity.getDedicatedResourcePoolIds(), Matchers.containsInAnyOrder("dedicatedCache"));
        Assert.assertThat(activeEntity.getConnectedClients().size(), Matchers.is(0));
        Assert.assertThat(activeEntity.getStores(), Matchers.containsInAnyOrder("sharedCache", "dedicatedCache"));
        try {
            createService.destroyAll();
            Assert.fail("Expecting IllegalStateException");
        } catch (IllegalStateException e) {
            Assert.assertThat(e.getMessage(), Matchers.containsString("Maintenance mode required"));
        }
        createService.startForMaintenance(null, CACHE_MANAGER);
        createService.destroyAll();
        activeEntities = observableEhcacheServerEntityService.getServedActiveEntities();
        Assert.assertThat(activeEntities.size(), Matchers.is(1));
        activeEntity = activeEntities.get(0);
        Assert.assertThat(activeEntity.getDefaultServerResource(), Matchers.is(Matchers.nullValue()));
        Assert.assertThat(activeEntity.getSharedResourcePoolIds(), Matchers.is(Matchers.<String>empty()));
        Assert.assertThat(activeEntity.getDedicatedResourcePoolIds(), Matchers.is(Matchers.<String>empty()));
        Assert.assertThat(activeEntity.getConnectedClients().size(), Matchers.is(0));
        Assert.assertThat(activeEntity.getStores(), Matchers.is(Matchers.<String>empty()));
    }

    @Test
    public void testStartNoAutoCreateThenAutoCreate() throws Exception {
        ClusteringServiceConfiguration creationConfigBad = ClusteringServiceConfigurationBuilder.cluster(URI.create(((DefaultClusteringServiceTest.CLUSTER_URI_BASE) + "my-application"))).expecting().defaultServerResource("defaultResource").resourcePool("sharedPrimary", 2, MemoryUnit.MB, "serverResource1").resourcePool("sharedSecondary", 2, MemoryUnit.MB, "serverResource2").resourcePool("sharedTertiary", 4, MemoryUnit.MB).build();
        DefaultClusteringService creationServiceBad = new DefaultClusteringService(creationConfigBad);
        try {
            creationServiceBad.start(null);
            Assert.fail("Expecting IllegalStateException");
        } catch (IllegalStateException e) {
            // Expected
        }
        List<ObservableEhcacheServerEntityService.ObservableEhcacheActiveEntity> activeEntitiesBad = observableEhcacheServerEntityService.getServedActiveEntities();
        Assert.assertThat(activeEntitiesBad.size(), Matchers.is(0));
        ClusteringServiceConfiguration creationConfigGood = ClusteringServiceConfigurationBuilder.cluster(URI.create(((DefaultClusteringServiceTest.CLUSTER_URI_BASE) + "my-application"))).autoCreate().defaultServerResource("defaultResource").resourcePool("sharedPrimary", 2, MemoryUnit.MB, "serverResource1").resourcePool("sharedSecondary", 2, MemoryUnit.MB, "serverResource2").resourcePool("sharedTertiary", 4, MemoryUnit.MB).build();
        DefaultClusteringService creationServiceGood = new DefaultClusteringService(creationConfigGood);
        creationServiceGood.start(null);
    }

    @Test
    public void testStoreValidation_autoCreateConfigGood_autoCreateConfigBad() throws Exception {
        String cacheAlias = "cacheAlias";
        ClusteringServiceConfiguration config = ClusteringServiceConfigurationBuilder.cluster(URI.create(((DefaultClusteringServiceTest.CLUSTER_URI_BASE) + "my-application"))).autoCreate().defaultServerResource("defaultResource").resourcePool("sharedPrimary", 2, MemoryUnit.MB, "serverResource1").resourcePool("sharedSecondary", 2, MemoryUnit.MB, "serverResource2").resourcePool("sharedTertiary", 4, MemoryUnit.MB).build();
        DefaultClusteringService creationService = new DefaultClusteringService(config);
        creationService.start(null);
        DefaultSerializationProvider serializationProvider = new DefaultSerializationProvider(null);
        serializationProvider.start(TestServiceProvider.providerContaining());
        Store.Configuration<Long, String> createStoreConfig = getSharedStoreConfig("sharedPrimary", serializationProvider, Long.class, String.class);
        ClusteredCacheIdentifier clusteredCacheIdentifier = getClusteredCacheIdentifier(creationService, cacheAlias);
        creationService.getServerStoreProxy(clusteredCacheIdentifier, createStoreConfig, EVENTUAL, Mockito.mock(ServerCallback.class));
        creationService.stop();
        DefaultClusteringService accessService = new DefaultClusteringService(config);
        accessService.start(null);
        Store.Configuration<Long, Long> accessStoreConfigBad = getSharedStoreConfig("sharedPrimary", serializationProvider, Long.class, Long.class);// ValueType is invalid

        try {
            accessService.getServerStoreProxy(getClusteredCacheIdentifier(accessService, cacheAlias), accessStoreConfigBad, EVENTUAL, Mockito.mock(ServerCallback.class));
            Assert.fail("Expecting CachePersistenceException");
        } catch (CachePersistenceException e) {
            Assert.assertThat(DefaultClusteringServiceTest.getRootCause(e).getMessage(), Matchers.containsString("Existing ServerStore configuration is not compatible with the desired configuration"));
        }
        List<ObservableEhcacheServerEntityService.ObservableEhcacheActiveEntity> activeEntities = observableEhcacheServerEntityService.getServedActiveEntities();
        Assert.assertThat(activeEntities.size(), Matchers.is(1));
        ObservableEhcacheServerEntityService.ObservableEhcacheActiveEntity activeEntity = activeEntities.get(0);
        Assert.assertThat(activeEntity.getDefaultServerResource(), Matchers.is("defaultResource"));
        Assert.assertThat(activeEntity.getSharedResourcePoolIds(), Matchers.containsInAnyOrder("sharedPrimary", "sharedSecondary", "sharedTertiary"));
        Assert.assertThat(activeEntity.getDedicatedResourcePoolIds(), Matchers.is(Matchers.<String>empty()));
        Assert.assertThat(activeEntity.getConnectedClients().size(), Matchers.is(1));
        Assert.assertThat(activeEntity.getStores(), Matchers.containsInAnyOrder(cacheAlias));
        List<ObservableClusterTierServerEntityService.ObservableClusterTierActiveEntity> clusterTierActiveEntities = observableClusterTierServerEntityService.getServedActiveEntities();
        Assert.assertThat(clusterTierActiveEntities.size(), Matchers.is(1));
        ObservableClusterTierServerEntityService.ObservableClusterTierActiveEntity clusterTierActiveEntity = clusterTierActiveEntities.get(0);
        Assert.assertThat(clusterTierActiveEntity.getConnectedClients(), Matchers.empty());
        accessService.stop();
        Assert.assertThat(activeEntity.getDefaultServerResource(), Matchers.is("defaultResource"));
        Assert.assertThat(activeEntity.getSharedResourcePoolIds(), Matchers.containsInAnyOrder("sharedPrimary", "sharedSecondary", "sharedTertiary"));
        Assert.assertThat(activeEntity.getDedicatedResourcePoolIds(), Matchers.is(Matchers.<String>empty()));
        Assert.assertThat(activeEntity.getConnectedClients().size(), Matchers.is(0));
        Assert.assertThat(clusterTierActiveEntity.getConnectedClients(), Matchers.empty());
    }

    @Test
    public void testStoreValidation_autoCreateConfigGood_autoCreateConfigGood() throws Exception {
        String cacheAlias = "cacheAlias";
        ClusteringServiceConfiguration config = ClusteringServiceConfigurationBuilder.cluster(URI.create(((DefaultClusteringServiceTest.CLUSTER_URI_BASE) + "my-application"))).autoCreate().defaultServerResource("defaultResource").resourcePool("sharedPrimary", 2, MemoryUnit.MB, "serverResource1").resourcePool("sharedSecondary", 2, MemoryUnit.MB, "serverResource2").resourcePool("sharedTertiary", 4, MemoryUnit.MB).build();
        DefaultClusteringService creationService = new DefaultClusteringService(config);
        creationService.start(null);
        DefaultSerializationProvider serializationProvider = new DefaultSerializationProvider(null);
        serializationProvider.start(TestServiceProvider.providerContaining());
        Store.Configuration<Long, String> storeConfig = getSharedStoreConfig("sharedPrimary", serializationProvider, Long.class, String.class);
        creationService.getServerStoreProxy(getClusteredCacheIdentifier(creationService, cacheAlias), storeConfig, EVENTUAL, Mockito.mock(ServerCallback.class));
        creationService.stop();
        DefaultClusteringService accessService = new DefaultClusteringService(config);
        accessService.start(null);
        accessService.getServerStoreProxy(getClusteredCacheIdentifier(accessService, cacheAlias), storeConfig, EVENTUAL, Mockito.mock(ServerCallback.class));
        List<ObservableEhcacheServerEntityService.ObservableEhcacheActiveEntity> activeEntities = observableEhcacheServerEntityService.getServedActiveEntities();
        Assert.assertThat(activeEntities.size(), Matchers.is(1));
        ObservableEhcacheServerEntityService.ObservableEhcacheActiveEntity activeEntity = activeEntities.get(0);
        Assert.assertThat(activeEntity.getDefaultServerResource(), Matchers.is("defaultResource"));
        Assert.assertThat(activeEntity.getSharedResourcePoolIds(), Matchers.containsInAnyOrder("sharedPrimary", "sharedSecondary", "sharedTertiary"));
        Assert.assertThat(activeEntity.getDedicatedResourcePoolIds(), Matchers.is(Matchers.<String>empty()));
        Assert.assertThat(activeEntity.getConnectedClients().size(), Matchers.is(1));
        Assert.assertThat(activeEntity.getStores(), Matchers.containsInAnyOrder(cacheAlias));
        List<ObservableClusterTierServerEntityService.ObservableClusterTierActiveEntity> clusterTierActiveEntities = observableClusterTierServerEntityService.getServedActiveEntities();
        Assert.assertThat(clusterTierActiveEntities.size(), Matchers.is(1));
        ObservableClusterTierServerEntityService.ObservableClusterTierActiveEntity clusterTierActiveEntity = clusterTierActiveEntities.get(0);
        Assert.assertThat(clusterTierActiveEntity.getConnectedClients().size(), Matchers.is(1));
        accessService.stop();
        Assert.assertThat(activeEntity.getDefaultServerResource(), Matchers.is("defaultResource"));
        Assert.assertThat(activeEntity.getSharedResourcePoolIds(), Matchers.containsInAnyOrder("sharedPrimary", "sharedSecondary", "sharedTertiary"));
        Assert.assertThat(activeEntity.getDedicatedResourcePoolIds(), Matchers.is(Matchers.<String>empty()));
        Assert.assertThat(activeEntity.getConnectedClients().size(), Matchers.is(0));
        Assert.assertThat(clusterTierActiveEntity.getConnectedClients(), Matchers.empty());
    }

    @Test
    public void testStoreValidation_autoCreateConfigBad() throws Exception {
        String cacheAlias = "cacheAlias";
        ClusteringServiceConfiguration config = ClusteringServiceConfigurationBuilder.cluster(URI.create(((DefaultClusteringServiceTest.CLUSTER_URI_BASE) + "my-application"))).autoCreate().defaultServerResource("defaultResource").resourcePool("sharedPrimary", 2, MemoryUnit.MB, "serverResource1").resourcePool("sharedSecondary", 2, MemoryUnit.MB, "serverResource2").resourcePool("sharedTertiary", 4, MemoryUnit.MB).build();
        DefaultClusteringService creationService = new DefaultClusteringService(config);
        creationService.start(null);
        DefaultSerializationProvider serializationProvider = new DefaultSerializationProvider(null);
        serializationProvider.start(TestServiceProvider.providerContaining());
        Store.Configuration<Long, String> storeConfig = getSharedStoreConfig("dedicatedPrimary", serializationProvider, Long.class, String.class);
        ClusteredCacheIdentifier clusteredCacheIdentifier = getClusteredCacheIdentifier(creationService, cacheAlias);
        try {
            creationService.getServerStoreProxy(clusteredCacheIdentifier, storeConfig, EVENTUAL, Mockito.mock(ServerCallback.class));
            Assert.fail("Expecting CachePersistenceException");
        } catch (CachePersistenceException e) {
            // Expected
        }
        List<ObservableEhcacheServerEntityService.ObservableEhcacheActiveEntity> activeEntities = observableEhcacheServerEntityService.getServedActiveEntities();
        Assert.assertThat(activeEntities.size(), Matchers.is(1));
        ObservableEhcacheServerEntityService.ObservableEhcacheActiveEntity activeEntity = activeEntities.get(0);
        Assert.assertThat(activeEntity.getDefaultServerResource(), Matchers.is("defaultResource"));
        Assert.assertThat(activeEntity.getSharedResourcePoolIds(), Matchers.containsInAnyOrder("sharedPrimary", "sharedSecondary", "sharedTertiary"));
        Assert.assertThat(activeEntity.getDedicatedResourcePoolIds(), Matchers.is(Matchers.<String>empty()));
        Assert.assertThat(activeEntity.getConnectedClients().size(), Matchers.is(1));
        Assert.assertThat(activeEntity.getStores().size(), Matchers.is(0));
        creationService.stop();
        Assert.assertThat(activeEntity.getDefaultServerResource(), Matchers.is("defaultResource"));
        Assert.assertThat(activeEntity.getSharedResourcePoolIds(), Matchers.containsInAnyOrder("sharedPrimary", "sharedSecondary", "sharedTertiary"));
        Assert.assertThat(activeEntity.getDedicatedResourcePoolIds(), Matchers.is(Matchers.<String>empty()));
        Assert.assertThat(activeEntity.getConnectedClients().size(), Matchers.is(0));
        Assert.assertThat(activeEntity.getStores().size(), Matchers.is(0));
    }

    @Test
    public void testStoreValidation_autoCreateConfigGood_noAutoCreateConfigBad() throws Exception {
        String cacheAlias = "cacheAlias";
        ClusteringServiceConfiguration autoConfig = ClusteringServiceConfigurationBuilder.cluster(URI.create(((DefaultClusteringServiceTest.CLUSTER_URI_BASE) + "my-application"))).autoCreate().defaultServerResource("defaultResource").resourcePool("sharedPrimary", 2, MemoryUnit.MB, "serverResource1").resourcePool("sharedSecondary", 2, MemoryUnit.MB, "serverResource2").resourcePool("sharedTertiary", 4, MemoryUnit.MB).build();
        DefaultClusteringService creationService = new DefaultClusteringService(autoConfig);
        creationService.start(null);
        DefaultSerializationProvider serializationProvider = new DefaultSerializationProvider(null);
        serializationProvider.start(TestServiceProvider.providerContaining());
        Store.Configuration<Long, String> creationStoreConfig = getSharedStoreConfig("sharedPrimary", serializationProvider, Long.class, String.class);
        creationService.getServerStoreProxy(getClusteredCacheIdentifier(creationService, cacheAlias), creationStoreConfig, EVENTUAL, Mockito.mock(ServerCallback.class));
        ClusteringServiceConfiguration noAutoConfig = ClusteringServiceConfigurationBuilder.cluster(URI.create(((DefaultClusteringServiceTest.CLUSTER_URI_BASE) + "my-application"))).expecting().defaultServerResource("defaultResource").resourcePool("sharedPrimary", 2, MemoryUnit.MB, "serverResource1").resourcePool("sharedSecondary", 2, MemoryUnit.MB, "serverResource2").resourcePool("sharedTertiary", 4, MemoryUnit.MB).build();
        DefaultClusteringService accessService = new DefaultClusteringService(noAutoConfig);
        accessService.start(null);
        Store.Configuration<Long, Long> accessStoreConfig = getSharedStoreConfig("sharedPrimary", serializationProvider, Long.class, Long.class);
        try {
            accessService.getServerStoreProxy(getClusteredCacheIdentifier(accessService, cacheAlias), accessStoreConfig, EVENTUAL, Mockito.mock(ServerCallback.class));
            Assert.fail("Expecting CachePersistenceException");
        } catch (CachePersistenceException e) {
            Assert.assertThat(DefaultClusteringServiceTest.getRootCause(e).getMessage(), Matchers.containsString("Existing ServerStore configuration is not compatible with the desired configuration"));
        }
        List<ObservableEhcacheServerEntityService.ObservableEhcacheActiveEntity> activeEntities = observableEhcacheServerEntityService.getServedActiveEntities();
        Assert.assertThat(activeEntities.size(), Matchers.is(1));
        ObservableEhcacheServerEntityService.ObservableEhcacheActiveEntity activeEntity = activeEntities.get(0);
        Assert.assertThat(activeEntity.getDefaultServerResource(), Matchers.is("defaultResource"));
        Assert.assertThat(activeEntity.getSharedResourcePoolIds(), Matchers.containsInAnyOrder("sharedPrimary", "sharedSecondary", "sharedTertiary"));
        Assert.assertThat(activeEntity.getDedicatedResourcePoolIds().size(), Matchers.is(0));
        Assert.assertThat(activeEntity.getConnectedClients().size(), Matchers.is(2));
        Assert.assertThat(activeEntity.getStores(), Matchers.containsInAnyOrder(cacheAlias));
        List<ObservableClusterTierServerEntityService.ObservableClusterTierActiveEntity> clusterTierActiveEntities = observableClusterTierServerEntityService.getServedActiveEntities();
        Assert.assertThat(clusterTierActiveEntities.size(), Matchers.is(1));
        ObservableClusterTierServerEntityService.ObservableClusterTierActiveEntity clusterTierActiveEntity = clusterTierActiveEntities.get(0);
        Assert.assertThat(clusterTierActiveEntity.getConnectedClients().size(), Matchers.is(1));
        creationService.stop();
        accessService.stop();
        Assert.assertThat(activeEntity.getDefaultServerResource(), Matchers.is("defaultResource"));
        Assert.assertThat(activeEntity.getSharedResourcePoolIds(), Matchers.containsInAnyOrder("sharedPrimary", "sharedSecondary", "sharedTertiary"));
        Assert.assertThat(activeEntity.getDedicatedResourcePoolIds().size(), Matchers.is(0));
        Assert.assertThat(activeEntity.getConnectedClients().size(), Matchers.is(0));
        Assert.assertThat(clusterTierActiveEntity.getConnectedClients(), Matchers.empty());
    }

    @Test
    public void testStoreValidation_autoCreateConfigGood_noAutoCreateConfigGood() throws Exception {
        String cacheAlias = "cacheAlias";
        ClusteringServiceConfiguration autoConfig = ClusteringServiceConfigurationBuilder.cluster(URI.create(((DefaultClusteringServiceTest.CLUSTER_URI_BASE) + "my-application"))).autoCreate().defaultServerResource("defaultResource").resourcePool("sharedPrimary", 2, MemoryUnit.MB, "serverResource1").resourcePool("sharedSecondary", 2, MemoryUnit.MB, "serverResource2").resourcePool("sharedTertiary", 4, MemoryUnit.MB).build();
        DefaultClusteringService creationService = new DefaultClusteringService(autoConfig);
        creationService.start(null);
        DefaultSerializationProvider serializationProvider = new DefaultSerializationProvider(null);
        serializationProvider.start(TestServiceProvider.providerContaining());
        Store.Configuration<Long, String> storeConfig = getSharedStoreConfig("sharedPrimary", serializationProvider, Long.class, String.class);
        creationService.getServerStoreProxy(getClusteredCacheIdentifier(creationService, cacheAlias), storeConfig, EVENTUAL, Mockito.mock(ServerCallback.class));
        ClusteringServiceConfiguration noAutoConfig = ClusteringServiceConfigurationBuilder.cluster(URI.create(((DefaultClusteringServiceTest.CLUSTER_URI_BASE) + "my-application"))).expecting().defaultServerResource("defaultResource").resourcePool("sharedPrimary", 2, MemoryUnit.MB, "serverResource1").resourcePool("sharedSecondary", 2, MemoryUnit.MB, "serverResource2").resourcePool("sharedTertiary", 4, MemoryUnit.MB).build();
        DefaultClusteringService accessService = new DefaultClusteringService(noAutoConfig);
        accessService.start(null);
        accessService.getServerStoreProxy(getClusteredCacheIdentifier(accessService, cacheAlias), storeConfig, EVENTUAL, Mockito.mock(ServerCallback.class));
        List<ObservableEhcacheServerEntityService.ObservableEhcacheActiveEntity> activeEntities = observableEhcacheServerEntityService.getServedActiveEntities();
        Assert.assertThat(activeEntities.size(), Matchers.is(1));
        ObservableEhcacheServerEntityService.ObservableEhcacheActiveEntity activeEntity = activeEntities.get(0);
        Assert.assertThat(activeEntity.getDefaultServerResource(), Matchers.is("defaultResource"));
        Assert.assertThat(activeEntity.getSharedResourcePoolIds(), Matchers.containsInAnyOrder("sharedPrimary", "sharedSecondary", "sharedTertiary"));
        Assert.assertThat(activeEntity.getDedicatedResourcePoolIds().size(), Matchers.is(0));
        Assert.assertThat(activeEntity.getConnectedClients().size(), Matchers.is(2));
        Assert.assertThat(activeEntity.getStores(), Matchers.containsInAnyOrder(cacheAlias));
        List<ObservableClusterTierServerEntityService.ObservableClusterTierActiveEntity> clusterTierActiveEntities = observableClusterTierServerEntityService.getServedActiveEntities();
        Assert.assertThat(clusterTierActiveEntities.size(), Matchers.is(1));
        ObservableClusterTierServerEntityService.ObservableClusterTierActiveEntity clusterTierActiveEntity = clusterTierActiveEntities.get(0);
        Assert.assertThat(clusterTierActiveEntity.getConnectedClients().size(), Matchers.is(2));
        creationService.stop();
        accessService.stop();
        Assert.assertThat(activeEntity.getDefaultServerResource(), Matchers.is("defaultResource"));
        Assert.assertThat(activeEntity.getSharedResourcePoolIds(), Matchers.containsInAnyOrder("sharedPrimary", "sharedSecondary", "sharedTertiary"));
        Assert.assertThat(activeEntity.getDedicatedResourcePoolIds().size(), Matchers.is(0));
        Assert.assertThat(activeEntity.getConnectedClients().size(), Matchers.is(0));
        Assert.assertThat(clusterTierActiveEntity.getConnectedClients(), Matchers.empty());
    }

    @Test
    public void testStoreValidation_MismatchedPoolTypes_ConfiguredDedicatedValidateShared() throws Exception {
        String cacheAlias = "cacheAlias";
        ClusteringServiceConfiguration creationConfig = ClusteringServiceConfigurationBuilder.cluster(URI.create(((DefaultClusteringServiceTest.CLUSTER_URI_BASE) + "my-application"))).autoCreate().defaultServerResource("defaultResource").resourcePool("sharedPrimary", 2, MemoryUnit.MB, "serverResource1").resourcePool("sharedSecondary", 2, MemoryUnit.MB, "serverResource2").resourcePool("sharedTertiary", 4, MemoryUnit.MB).build();
        DefaultClusteringService creationService = new DefaultClusteringService(creationConfig);
        creationService.start(null);
        DefaultSerializationProvider serializationProvider = new DefaultSerializationProvider(null);
        serializationProvider.start(TestServiceProvider.providerContaining());
        Store.Configuration<Long, String> createStoreConfig = getDedicatedStoreConfig("serverResource1", serializationProvider, Long.class, String.class);
        creationService.getServerStoreProxy(getClusteredCacheIdentifier(creationService, "cacheAlias"), createStoreConfig, EVENTUAL, Mockito.mock(ServerCallback.class));
        creationService.stop();
        ClusteringServiceConfiguration accessConfig = ClusteringServiceConfigurationBuilder.cluster(URI.create(((DefaultClusteringServiceTest.CLUSTER_URI_BASE) + "my-application"))).autoCreate().defaultServerResource("defaultResource").resourcePool("sharedPrimary", 2, MemoryUnit.MB, "serverResource1").resourcePool("sharedSecondary", 2, MemoryUnit.MB, "serverResource2").resourcePool("sharedTertiary", 4, MemoryUnit.MB).build();
        DefaultClusteringService accessService = new DefaultClusteringService(accessConfig);
        accessService.start(null);
        Store.Configuration<Long, String> accessStoreConfig = getSharedStoreConfig("serverResource1", serializationProvider, Long.class, String.class);
        try {
            accessService.getServerStoreProxy(getClusteredCacheIdentifier(accessService, cacheAlias), accessStoreConfig, EVENTUAL, Mockito.mock(ServerCallback.class));
            Assert.fail("Expecting CachePersistenceException");
        } catch (CachePersistenceException e) {
            Assert.assertThat(DefaultClusteringServiceTest.getRootCause(e).getMessage(), Matchers.containsString("Existing ServerStore configuration is not compatible with the desired configuration"));
        }
        List<ObservableEhcacheServerEntityService.ObservableEhcacheActiveEntity> activeEntities = observableEhcacheServerEntityService.getServedActiveEntities();
        Assert.assertThat(activeEntities.size(), Matchers.is(1));
        ObservableEhcacheServerEntityService.ObservableEhcacheActiveEntity activeEntity = activeEntities.get(0);
        Assert.assertThat(activeEntity.getDefaultServerResource(), Matchers.is("defaultResource"));
        Assert.assertThat(activeEntity.getSharedResourcePoolIds(), Matchers.containsInAnyOrder("sharedPrimary", "sharedSecondary", "sharedTertiary"));
        Assert.assertThat(activeEntity.getDedicatedResourcePoolIds(), Matchers.containsInAnyOrder("cacheAlias"));
        Assert.assertThat(activeEntity.getConnectedClients().size(), Matchers.is(1));
        List<ObservableClusterTierServerEntityService.ObservableClusterTierActiveEntity> clusterTierActiveEntities = observableClusterTierServerEntityService.getServedActiveEntities();
        Assert.assertThat(clusterTierActiveEntities.size(), Matchers.is(1));
        ObservableClusterTierServerEntityService.ObservableClusterTierActiveEntity clusterTierActiveEntity = clusterTierActiveEntities.get(0);
        Assert.assertThat(clusterTierActiveEntity.getConnectedClients(), Matchers.empty());
        accessService.stop();
        Assert.assertThat(activeEntity.getDefaultServerResource(), Matchers.is("defaultResource"));
        Assert.assertThat(activeEntity.getSharedResourcePoolIds(), Matchers.containsInAnyOrder("sharedPrimary", "sharedSecondary", "sharedTertiary"));
        Assert.assertThat(activeEntity.getDedicatedResourcePoolIds(), Matchers.containsInAnyOrder(cacheAlias));
        Assert.assertThat(activeEntity.getConnectedClients().size(), Matchers.is(0));
        Assert.assertThat(clusterTierActiveEntity.getConnectedClients(), Matchers.empty());
    }

    @Test
    public void testStoreValidation_MismatchedPoolTypes_ConfiguredSharedValidateDedicated() throws Exception {
        String cacheAlias = "cacheAlias";
        ClusteringServiceConfiguration creationConfig = ClusteringServiceConfigurationBuilder.cluster(URI.create(((DefaultClusteringServiceTest.CLUSTER_URI_BASE) + "my-application"))).autoCreate().defaultServerResource("defaultResource").resourcePool("sharedPrimary", 2, MemoryUnit.MB, "serverResource1").resourcePool("sharedSecondary", 2, MemoryUnit.MB, "serverResource2").resourcePool("sharedTertiary", 4, MemoryUnit.MB).build();
        DefaultClusteringService creationService = new DefaultClusteringService(creationConfig);
        creationService.start(null);
        DefaultSerializationProvider serializationProvider = new DefaultSerializationProvider(null);
        serializationProvider.start(TestServiceProvider.providerContaining());
        Store.Configuration<Long, String> createStoreConfig = getSharedStoreConfig("sharedPrimary", serializationProvider, Long.class, String.class);
        creationService.getServerStoreProxy(getClusteredCacheIdentifier(creationService, cacheAlias), createStoreConfig, EVENTUAL, Mockito.mock(ServerCallback.class));
        creationService.stop();
        ClusteringServiceConfiguration accessConfig = ClusteringServiceConfigurationBuilder.cluster(URI.create(((DefaultClusteringServiceTest.CLUSTER_URI_BASE) + "my-application"))).autoCreate().defaultServerResource("defaultResource").resourcePool("sharedPrimary", 2, MemoryUnit.MB, "serverResource1").resourcePool("sharedSecondary", 2, MemoryUnit.MB, "serverResource2").resourcePool("sharedTertiary", 4, MemoryUnit.MB).build();
        DefaultClusteringService accessService = new DefaultClusteringService(accessConfig);
        accessService.start(null);
        Store.Configuration<Long, String> accessStoreConfig = getDedicatedStoreConfig("defaultResource", serializationProvider, Long.class, String.class);
        try {
            accessService.getServerStoreProxy(getClusteredCacheIdentifier(accessService, cacheAlias), accessStoreConfig, EVENTUAL, Mockito.mock(ServerCallback.class));
            Assert.fail("Expecting CachePersistenceException");
        } catch (CachePersistenceException e) {
            Assert.assertThat(DefaultClusteringServiceTest.getRootCause(e).getMessage(), Matchers.containsString("Existing ServerStore configuration is not compatible with the desired configuration"));
        }
        List<ObservableEhcacheServerEntityService.ObservableEhcacheActiveEntity> activeEntities = observableEhcacheServerEntityService.getServedActiveEntities();
        Assert.assertThat(activeEntities.size(), Matchers.is(1));
        ObservableEhcacheServerEntityService.ObservableEhcacheActiveEntity activeEntity = activeEntities.get(0);
        Assert.assertThat(activeEntity.getDefaultServerResource(), Matchers.is("defaultResource"));
        Assert.assertThat(activeEntity.getSharedResourcePoolIds(), Matchers.containsInAnyOrder("sharedPrimary", "sharedSecondary", "sharedTertiary"));
        Assert.assertThat(activeEntity.getDedicatedResourcePoolIds(), Matchers.is(Matchers.<String>empty()));
        Assert.assertThat(activeEntity.getConnectedClients().size(), Matchers.is(1));
        List<ObservableClusterTierServerEntityService.ObservableClusterTierActiveEntity> clusterTierActiveEntities = observableClusterTierServerEntityService.getServedActiveEntities();
        Assert.assertThat(clusterTierActiveEntities.size(), Matchers.is(1));
        ObservableClusterTierServerEntityService.ObservableClusterTierActiveEntity clusterTierActiveEntity = clusterTierActiveEntities.get(0);
        Assert.assertThat(clusterTierActiveEntity.getConnectedClients(), Matchers.empty());
        accessService.stop();
        Assert.assertThat(activeEntity.getDefaultServerResource(), Matchers.is("defaultResource"));
        Assert.assertThat(activeEntity.getSharedResourcePoolIds(), Matchers.containsInAnyOrder("sharedPrimary", "sharedSecondary", "sharedTertiary"));
        Assert.assertThat(activeEntity.getDedicatedResourcePoolIds(), Matchers.is(Matchers.<String>empty()));
        Assert.assertThat(activeEntity.getConnectedClients().size(), Matchers.is(0));
        Assert.assertThat(clusterTierActiveEntity.getConnectedClients(), Matchers.empty());
    }

    @Test
    public void testGetServerStoreProxyReturnsEventualStore() throws Exception {
        String entityIdentifier = "my-application";
        ClusteringServiceConfiguration configuration = new ClusteringServiceConfiguration(URI.create(((DefaultClusteringServiceTest.CLUSTER_URI_BASE) + entityIdentifier)), true, new org.ehcache.clustered.common.ServerSideConfiguration(Collections.<String, Pool>emptyMap()));
        DefaultClusteringService service = new DefaultClusteringService(configuration);
        service.start(null);
        ClusteringService.ClusteredCacheIdentifier cacheIdentifier = ((ClusteredCacheIdentifier) (service.getPersistenceSpaceIdentifier("my-cache", null)));
        ResourcePools resourcePools = Mockito.mock(ResourcePools.class);
        @SuppressWarnings("unchecked")
        Store.Configuration<String, Object> storeConfig = Mockito.mock(Store.Configuration.class);
        Mockito.when(storeConfig.getResourcePools()).thenReturn(resourcePools);
        Mockito.when(resourcePools.getPoolForResource(ArgumentMatchers.eq(DEDICATED))).thenReturn(new DedicatedClusteredResourcePoolImpl("serverResource1", 1L, MemoryUnit.MB));
        Mockito.when(storeConfig.getKeyType()).thenReturn(String.class);
        Mockito.when(storeConfig.getValueType()).thenReturn(Object.class);
        ServerStoreProxy serverStoreProxy = service.getServerStoreProxy(cacheIdentifier, storeConfig, EVENTUAL, Mockito.mock(ServerCallback.class));
        Assert.assertThat(serverStoreProxy, Matchers.instanceOf(EventualServerStoreProxy.class));
    }

    @Test
    public void testGetServerStoreProxyReturnsStrongStore() throws Exception {
        String entityIdentifier = "my-application";
        ClusteringServiceConfiguration configuration = new ClusteringServiceConfiguration(URI.create(((DefaultClusteringServiceTest.CLUSTER_URI_BASE) + entityIdentifier)), true, new org.ehcache.clustered.common.ServerSideConfiguration(Collections.<String, Pool>emptyMap()));
        DefaultClusteringService service = new DefaultClusteringService(configuration);
        service.start(null);
        ClusteringService.ClusteredCacheIdentifier cacheIdentifier = ((ClusteredCacheIdentifier) (service.getPersistenceSpaceIdentifier("my-cache", null)));
        ResourcePools resourcePools = Mockito.mock(ResourcePools.class);
        @SuppressWarnings("unchecked")
        Store.Configuration<String, Object> storeConfig = Mockito.mock(Store.Configuration.class);
        Mockito.when(storeConfig.getResourcePools()).thenReturn(resourcePools);
        Mockito.when(resourcePools.getPoolForResource(ArgumentMatchers.eq(DEDICATED))).thenReturn(new DedicatedClusteredResourcePoolImpl("serverResource1", 1L, MemoryUnit.MB));
        Mockito.when(storeConfig.getKeyType()).thenReturn(String.class);
        Mockito.when(storeConfig.getValueType()).thenReturn(Object.class);
        ServerStoreProxy serverStoreProxy = service.getServerStoreProxy(cacheIdentifier, storeConfig, STRONG, Mockito.mock(ServerCallback.class));
        Assert.assertThat(serverStoreProxy, Matchers.instanceOf(StrongServerStoreProxy.class));
    }

    @Test
    public void testGetServerStoreProxyFailureClearsEntityListeners() throws Exception {
        // Initial setup begin
        String entityIdentifier = "my-application";
        ClusteringServiceConfiguration configuration = new ClusteringServiceConfiguration(URI.create(((DefaultClusteringServiceTest.CLUSTER_URI_BASE) + entityIdentifier)), true, new org.ehcache.clustered.common.ServerSideConfiguration(Collections.<String, Pool>emptyMap()));
        DefaultClusteringService service = new DefaultClusteringService(configuration);
        service.start(null);
        ClusteringService.ClusteredCacheIdentifier cacheIdentifier = ((ClusteredCacheIdentifier) (service.getPersistenceSpaceIdentifier("my-cache", null)));
        ResourcePools resourcePools = Mockito.mock(ResourcePools.class);
        @SuppressWarnings("unchecked")
        Store.Configuration<String, Object> storeConfig = Mockito.mock(Store.Configuration.class);
        Mockito.when(storeConfig.getResourcePools()).thenReturn(resourcePools);
        Mockito.when(resourcePools.getPoolForResource(ArgumentMatchers.eq(DEDICATED))).thenReturn(new DedicatedClusteredResourcePoolImpl("serverResource1", 1L, MemoryUnit.MB));
        Mockito.when(storeConfig.getKeyType()).thenReturn(String.class);
        Mockito.when(storeConfig.getValueType()).thenReturn(Object.class);
        service.getServerStoreProxy(cacheIdentifier, storeConfig, STRONG, Mockito.mock(ServerCallback.class));// Creates the store

        service.stop();
        // Initial setup end
        service.start(null);
        Mockito.when(resourcePools.getPoolForResource(ArgumentMatchers.eq(DEDICATED))).thenReturn(new DedicatedClusteredResourcePoolImpl("serverResource1", 2L, MemoryUnit.MB));
        try {
            service.getServerStoreProxy(cacheIdentifier, storeConfig, STRONG, Mockito.mock(ServerCallback.class));
            Assert.fail("Server store proxy creation should have failed");
        } catch (CachePersistenceException cpe) {
            Assert.assertThat(cpe.getMessage(), Matchers.containsString("Unable to create cluster tier proxy"));
            Assert.assertThat(DefaultClusteringServiceTest.getRootCause(cpe), Matchers.instanceOf(InvalidServerStoreConfigurationException.class));
        }
    }

    @Test
    public void testGetServerStoreProxyFailureDoesNotClearOtherStoreEntityListeners() throws Exception {
        // Initial setup begin
        String entityIdentifier = "my-application";
        ClusteringServiceConfiguration configuration = new ClusteringServiceConfiguration(URI.create(((DefaultClusteringServiceTest.CLUSTER_URI_BASE) + entityIdentifier)), true, new org.ehcache.clustered.common.ServerSideConfiguration(Collections.<String, Pool>emptyMap()));
        DefaultClusteringService service = new DefaultClusteringService(configuration);
        service.start(null);
        ClusteringService.ClusteredCacheIdentifier cacheIdentifier = ((ClusteredCacheIdentifier) (service.getPersistenceSpaceIdentifier("my-cache", null)));
        ResourcePools resourcePools = Mockito.mock(ResourcePools.class);
        @SuppressWarnings("unchecked")
        Store.Configuration<String, Object> storeConfig = Mockito.mock(Store.Configuration.class);
        Mockito.when(storeConfig.getResourcePools()).thenReturn(resourcePools);
        Mockito.when(resourcePools.getPoolForResource(ArgumentMatchers.eq(DEDICATED))).thenReturn(new DedicatedClusteredResourcePoolImpl("serverResource1", 1L, MemoryUnit.MB));
        Mockito.when(storeConfig.getKeyType()).thenReturn(String.class);
        Mockito.when(storeConfig.getValueType()).thenReturn(Object.class);
        service.getServerStoreProxy(cacheIdentifier, storeConfig, STRONG, Mockito.mock(ServerCallback.class));// Creates the store

        service.stop();
        // Initial setup end
        service.start(null);
        ClusteringService.ClusteredCacheIdentifier otherCacheIdentifier = ((ClusteredCacheIdentifier) (service.getPersistenceSpaceIdentifier("my-other-cache", null)));
        service.getServerStoreProxy(otherCacheIdentifier, storeConfig, STRONG, Mockito.mock(ServerCallback.class));// Creates one more store

        Mockito.when(resourcePools.getPoolForResource(ArgumentMatchers.eq(DEDICATED))).thenReturn(new DedicatedClusteredResourcePoolImpl("serverResource1", 2L, MemoryUnit.MB));
        try {
            service.getServerStoreProxy(cacheIdentifier, storeConfig, STRONG, Mockito.mock(ServerCallback.class));
            Assert.fail("Server store proxy creation should have failed");
        } catch (CachePersistenceException cpe) {
            Assert.assertThat(cpe.getMessage(), Matchers.containsString("Unable to create cluster tier proxy"));
            Assert.assertThat(DefaultClusteringServiceTest.getRootCause(cpe), Matchers.instanceOf(InvalidServerStoreConfigurationException.class));
        }
    }

    @Test
    public void testGetStateRepositoryWithinTwiceWithSameName() throws Exception {
        ClusteringServiceConfiguration configuration = new ClusteringServiceConfiguration(URI.create(DefaultClusteringServiceTest.CLUSTER_URI_BASE), true, new org.ehcache.clustered.common.ServerSideConfiguration(Collections.<String, Pool>emptyMap()));
        DefaultClusteringService service = new DefaultClusteringService(configuration);
        PersistableResourceService.PersistenceSpaceIdentifier<?> cacheIdentifier = service.getPersistenceSpaceIdentifier("myCache", null);
        StateRepository repository1 = service.getStateRepositoryWithin(cacheIdentifier, "myRepo");
        StateRepository repository2 = service.getStateRepositoryWithin(cacheIdentifier, "myRepo");
        Assert.assertThat(repository1, Matchers.sameInstance(repository2));
    }

    @Test
    public void testGetStateRepositoryWithinTwiceWithSameNameDifferentPersistenceSpaceIdentifier() throws Exception {
        ClusteringServiceConfiguration configuration = new ClusteringServiceConfiguration(URI.create(DefaultClusteringServiceTest.CLUSTER_URI_BASE), true, new org.ehcache.clustered.common.ServerSideConfiguration(Collections.<String, Pool>emptyMap()));
        DefaultClusteringService service = new DefaultClusteringService(configuration);
        PersistableResourceService.PersistenceSpaceIdentifier<?> cacheIdentifier1 = service.getPersistenceSpaceIdentifier("myCache1", null);
        PersistableResourceService.PersistenceSpaceIdentifier<?> cacheIdentifier2 = service.getPersistenceSpaceIdentifier("myCache2", null);
        StateRepository repository1 = service.getStateRepositoryWithin(cacheIdentifier1, "myRepo");
        StateRepository repository2 = service.getStateRepositoryWithin(cacheIdentifier2, "myRepo");
        Assert.assertThat(repository1, Matchers.not(Matchers.sameInstance(repository2)));
    }

    @Test
    public void testGetStateRepositoryWithinWithNonExistentPersistenceSpaceIdentifier() throws Exception {
        expectedException.expect(CachePersistenceException.class);
        expectedException.expectMessage("Clustered space not found for identifier");
        ClusteringServiceConfiguration configuration = new ClusteringServiceConfiguration(URI.create(DefaultClusteringServiceTest.CLUSTER_URI_BASE), true, new org.ehcache.clustered.common.ServerSideConfiguration(Collections.<String, Pool>emptyMap()));
        DefaultClusteringService service = new DefaultClusteringService(configuration);
        ClusteredCacheIdentifier cacheIdentifier = Mockito.mock(ClusteredCacheIdentifier.class);
        getId();
        service.getStateRepositoryWithin(cacheIdentifier, "myRepo");
    }

    @Test
    public void testReleaseNonExistentPersistenceSpaceIdentifierTwice() throws Exception {
        expectedException.expect(CachePersistenceException.class);
        expectedException.expectMessage("Unknown identifier");
        ClusteringServiceConfiguration configuration = new ClusteringServiceConfiguration(URI.create(DefaultClusteringServiceTest.CLUSTER_URI_BASE), true, new org.ehcache.clustered.common.ServerSideConfiguration(Collections.<String, Pool>emptyMap()));
        DefaultClusteringService service = new DefaultClusteringService(configuration);
        ClusteredCacheIdentifier cacheIdentifier = Mockito.mock(ClusteredCacheIdentifier.class);
        getId();
        service.releasePersistenceSpaceIdentifier(cacheIdentifier);
    }

    @Test
    public void testReleasePersistenceSpaceIdentifierTwice() throws Exception {
        expectedException.expect(CachePersistenceException.class);
        expectedException.expectMessage("Unknown identifier");
        ClusteringServiceConfiguration configuration = new ClusteringServiceConfiguration(URI.create(DefaultClusteringServiceTest.CLUSTER_URI_BASE), true, new org.ehcache.clustered.common.ServerSideConfiguration(Collections.<String, Pool>emptyMap()));
        DefaultClusteringService service = new DefaultClusteringService(configuration);
        PersistableResourceService.PersistenceSpaceIdentifier<?> cacheIdentifier = service.getPersistenceSpaceIdentifier("myCache", null);
        try {
            service.releasePersistenceSpaceIdentifier(cacheIdentifier);
        } catch (CachePersistenceException e) {
            Assert.fail("First invocation of releasePersistenceSpaceIdentifier should not have failed");
        }
        service.releasePersistenceSpaceIdentifier(cacheIdentifier);
    }

    @Test
    public void releaseMaintenanceHoldsWhenConnectionClosedDuringDestruction() {
        ClusteringServiceConfiguration configuration = new ClusteringServiceConfiguration(URI.create(DefaultClusteringServiceTest.CLUSTER_URI_BASE), true, new org.ehcache.clustered.common.ServerSideConfiguration(Collections.<String, Pool>emptyMap()));
        DefaultClusteringService service = new DefaultClusteringService(configuration);
        Assert.assertThat(service.isConnected(), Matchers.is(false));
        service.startForMaintenance(null, CACHE_MANAGER);
        Assert.assertThat(service.isConnected(), Matchers.is(true));
        ConnectionState connectionState = service.getConnectionState();
        ClusterTierManagerClientEntityFactory clusterTierManagerClientEntityFactory = connectionState.getEntityFactory();
        Assert.assertEquals(clusterTierManagerClientEntityFactory.getMaintenanceHolds().size(), 1);
        connectionState.destroyState(false);
        Assert.assertEquals(clusterTierManagerClientEntityFactory.getMaintenanceHolds().size(), 0);
        service.stop();
    }
}

