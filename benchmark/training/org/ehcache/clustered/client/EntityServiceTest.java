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


import java.net.URI;
import org.ehcache.CacheManager;
import org.ehcache.clustered.client.config.builders.ClusteringServiceConfigurationBuilder;
import org.ehcache.clustered.client.internal.lock.VoltronReadWriteLockClient;
import org.ehcache.clustered.client.service.ClientEntityFactory;
import org.ehcache.clustered.client.service.EntityBusyException;
import org.ehcache.clustered.client.service.EntityService;
import org.ehcache.config.builders.CacheManagerBuilder;
import org.ehcache.spi.service.Service;
import org.ehcache.spi.service.ServiceDependencies;
import org.ehcache.spi.service.ServiceProvider;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.terracotta.exception.EntityAlreadyExistsException;


public class EntityServiceTest {
    private static final URI CLUSTER_URI = URI.create("terracotta://example.com:9540/EntityServiceTest");

    @Test
    public void test() throws Exception {
        EntityServiceTest.ClusteredManagementService clusteredManagementService = new EntityServiceTest.ClusteredManagementService();
        CacheManager cacheManager = CacheManagerBuilder.newCacheManagerBuilder().using(clusteredManagementService).with(ClusteringServiceConfigurationBuilder.cluster(EntityServiceTest.CLUSTER_URI).autoCreate()).build(true);
        Assert.assertThat(clusteredManagementService.clientEntityFactory, CoreMatchers.is(CoreMatchers.notNullValue()));
        clusteredManagementService.clientEntityFactory.create();
        try {
            clusteredManagementService.clientEntityFactory.create();
            Assert.fail();
        } catch (Exception e) {
            Assert.assertThat(e, CoreMatchers.instanceOf(EntityAlreadyExistsException.class));
        }
        VoltronReadWriteLockClient entity = clusteredManagementService.clientEntityFactory.retrieve();
        Assert.assertThat(entity, CoreMatchers.is(CoreMatchers.notNullValue()));
        try {
            clusteredManagementService.clientEntityFactory.destroy();
            Assert.fail();
        } catch (Exception e) {
            Assert.assertThat(e, CoreMatchers.instanceOf(EntityBusyException.class));
        }
        entity.close();
        clusteredManagementService.clientEntityFactory.destroy();
        cacheManager.close();
    }

    @ServiceDependencies(EntityService.class)
    public static class ClusteredManagementService implements Service {
        ClientEntityFactory<VoltronReadWriteLockClient, Void> clientEntityFactory;

        @Override
        public void start(ServiceProvider<Service> serviceProvider) {
            clientEntityFactory = serviceProvider.getService(EntityService.class).newClientEntityFactory("my-locker", VoltronReadWriteLockClient.class, 1L, null);
        }

        @Override
        public void stop() {
            clientEntityFactory = null;
        }
    }
}

