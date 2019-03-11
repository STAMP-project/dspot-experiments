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


import java.net.URI;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import org.ehcache.clustered.client.config.ClusteringServiceConfiguration;
import org.ehcache.clustered.client.internal.ClusterTierManagerClientEntity;
import org.ehcache.clustered.client.internal.lock.VoltronReadWriteLockClient;
import org.ehcache.clustered.client.internal.store.InternalClusterTierClientEntity;
import org.ehcache.clustered.common.ServerSideConfiguration;
import org.ehcache.clustered.common.internal.exceptions.DestroyInProgressException;
import org.ehcache.spi.service.MaintainableService;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.terracotta.connection.Connection;
import org.terracotta.connection.entity.EntityRef;
import org.terracotta.exception.EntityAlreadyExistsException;

import static org.ehcache.spi.service.MaintainableService.MaintenanceScope.CACHE_MANAGER;


/**
 * DefaultClusteringServiceDestroyTest
 */
public class DefaultClusteringServiceDestroyTest {
    @Mock
    private Connection connection;

    @Mock
    private EntityRef<ClusterTierManagerClientEntity, Object, Void> managerEntityRef;

    @Mock
    private EntityRef<InternalClusterTierClientEntity, Object, Void> tierEntityRef;

    @Mock
    private EntityRef<VoltronReadWriteLockClient, Object, Void> lockEntityRef;

    @Test
    public void testDestroyAllFullyMocked() throws Exception {
        mockLockForWriteLockSuccess();
        Mockito.when(getEntityRef(ClusterTierManagerClientEntity.class)).thenReturn(managerEntityRef);
        ClusterTierManagerClientEntity managerEntity = Mockito.mock(ClusterTierManagerClientEntity.class);
        Mockito.when(managerEntityRef.fetchEntity(null)).thenReturn(managerEntity);
        Set<String> stores = new HashSet<>();
        stores.add("store1");
        stores.add("store2");
        Mockito.when(managerEntity.prepareForDestroy()).thenReturn(stores);
        Mockito.when(getEntityRef(InternalClusterTierClientEntity.class)).thenReturn(tierEntityRef);
        Mockito.when(tierEntityRef.destroy()).thenReturn(true);
        Mockito.when(managerEntityRef.destroy()).thenReturn(true);
        DefaultClusteringService service = new DefaultClusteringService(new ClusteringServiceConfiguration(URI.create("mock://localhost/whatever")));
        service.startForMaintenance(null, CACHE_MANAGER);
        service.destroyAll();
        Mockito.verify(managerEntity).prepareForDestroy();
        Mockito.verify(tierEntityRef, Mockito.times(2)).destroy();
        Mockito.verify(managerEntityRef).destroy();
    }

    @Test
    public void testAutoCreateOnPartialDestroyState() throws Exception {
        ServerSideConfiguration serverConfig = new ServerSideConfiguration("default", Collections.emptyMap());
        mockLockForWriteLockSuccess();
        Mockito.when(getEntityRef(ClusterTierManagerClientEntity.class)).thenReturn(managerEntityRef);
        ClusterTierManagerClientEntity managerEntity = Mockito.mock(ClusterTierManagerClientEntity.class);
        // ClusterTierManager exists
        // Next time simulate creation
        Mockito.doThrow(new EntityAlreadyExistsException("className", "entityName")).doNothing().when(managerEntityRef).create(new org.ehcache.clustered.common.internal.ClusterTierManagerConfiguration("whatever", serverConfig));
        // And can be fetch
        Mockito.when(managerEntityRef.fetchEntity(null)).thenReturn(managerEntity);
        // However validate indicates destroy in progress
        // Next time validation succeeds
        Mockito.doThrow(new DestroyInProgressException("destroying")).doNothing().when(managerEntity).validate(serverConfig);
        Set<String> stores = new HashSet<>();
        stores.add("store1");
        stores.add("store2");
        Mockito.when(managerEntity.prepareForDestroy()).thenReturn(stores);
        Mockito.when(getEntityRef(InternalClusterTierClientEntity.class)).thenReturn(tierEntityRef);
        Mockito.when(tierEntityRef.destroy()).thenReturn(true);
        Mockito.when(managerEntityRef.destroy()).thenReturn(true);
        DefaultClusteringService service = new DefaultClusteringService(new ClusteringServiceConfiguration(URI.create("mock://localhost/whatever"), true, serverConfig));
        service.start(null);
        Mockito.verify(managerEntity).prepareForDestroy();
        Mockito.verify(tierEntityRef, Mockito.times(2)).destroy();
        Mockito.verify(managerEntityRef).destroy();
    }

    @Test
    public void testFetchOnPartialDestroyState() throws Exception {
        mockLockForReadLockSuccess();
        Mockito.when(getEntityRef(ClusterTierManagerClientEntity.class)).thenReturn(managerEntityRef);
        ClusterTierManagerClientEntity managerEntity = Mockito.mock(ClusterTierManagerClientEntity.class);
        // ClusterTierManager can be fetch
        Mockito.when(managerEntityRef.fetchEntity(null)).thenReturn(managerEntity);
        // However validate indicates destroy in progress
        Mockito.doThrow(new DestroyInProgressException("destroying")).when(managerEntity).validate(null);
        DefaultClusteringService service = new DefaultClusteringService(new ClusteringServiceConfiguration(URI.create("mock://localhost/whatever")));
        try {
            service.start(null);
            Assert.fail("IllegalStateException expected");
        } catch (IllegalStateException e) {
            Assert.assertThat(e.getMessage(), Matchers.containsString("does not exist"));
        }
    }

    @Test
    public void testDestroyOnPartialDestroyState() throws Exception {
        mockLockForWriteLockSuccess();
        Mockito.when(getEntityRef(ClusterTierManagerClientEntity.class)).thenReturn(managerEntityRef);
        ClusterTierManagerClientEntity managerEntity = Mockito.mock(ClusterTierManagerClientEntity.class);
        Mockito.when(managerEntityRef.fetchEntity(null)).thenReturn(managerEntity);
        Mockito.doThrow(new DestroyInProgressException("destroying")).when(managerEntity).validate(ArgumentMatchers.any());
        Set<String> stores = new HashSet<>();
        stores.add("store1");
        stores.add("store2");
        Mockito.when(managerEntity.prepareForDestroy()).thenReturn(stores);
        Mockito.when(getEntityRef(InternalClusterTierClientEntity.class)).thenReturn(tierEntityRef);
        Mockito.when(tierEntityRef.destroy()).thenReturn(true);
        Mockito.when(managerEntityRef.destroy()).thenReturn(true);
        DefaultClusteringService service = new DefaultClusteringService(new ClusteringServiceConfiguration(URI.create("mock://localhost/whatever")));
        service.startForMaintenance(null, CACHE_MANAGER);
        service.destroyAll();
        Mockito.verify(managerEntity).prepareForDestroy();
        Mockito.verify(tierEntityRef, Mockito.times(2)).destroy();
        Mockito.verify(managerEntityRef).destroy();
    }
}

