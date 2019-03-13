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
package org.ehcache.impl.internal.store.tiering;


import java.io.File;
import org.ehcache.config.CacheConfiguration;
import org.ehcache.config.EvictionAdvisor;
import org.ehcache.config.ResourcePools;
import org.ehcache.config.builders.ExpiryPolicyBuilder;
import org.ehcache.config.builders.ResourcePoolsBuilder;
import org.ehcache.config.units.EntryUnit;
import org.ehcache.config.units.MemoryUnit;
import org.ehcache.core.spi.ServiceLocator;
import org.ehcache.core.spi.service.DiskResourceService;
import org.ehcache.core.spi.store.Store;
import org.ehcache.expiry.ExpiryPolicy;
import org.ehcache.impl.serialization.JavaSerializer;
import org.ehcache.spi.loaderwriter.CacheLoaderWriter;
import org.ehcache.spi.persistence.PersistableResourceService;
import org.ehcache.spi.serialization.Serializer;
import org.ehcache.test.MockitoUtil;
import org.hamcrest.core.Is;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.mockito.Mockito;


public class TieredStoreFlushWhileShutdownTest {
    @Rule
    public final TemporaryFolder folder = new TemporaryFolder();

    @Test
    public void testTieredStoreReleaseFlushesEntries() throws Exception {
        File persistenceLocation = folder.newFolder("testTieredStoreReleaseFlushesEntries");
        Store.Configuration<Number, String> configuration = new Store.Configuration<Number, String>() {
            @Override
            public Class<Number> getKeyType() {
                return Number.class;
            }

            @Override
            public Class<String> getValueType() {
                return String.class;
            }

            @Override
            public EvictionAdvisor<? super Number, ? super String> getEvictionAdvisor() {
                return null;
            }

            @Override
            public ClassLoader getClassLoader() {
                return getClass().getClassLoader();
            }

            @Override
            public ExpiryPolicy<? super Number, ? super String> getExpiry() {
                return ExpiryPolicyBuilder.noExpiration();
            }

            @Override
            public ResourcePools getResourcePools() {
                return ResourcePoolsBuilder.newResourcePoolsBuilder().heap(10, EntryUnit.ENTRIES).disk(10, MemoryUnit.MB, true).build();
            }

            @Override
            public Serializer<Number> getKeySerializer() {
                return new JavaSerializer<>(getClassLoader());
            }

            @Override
            public Serializer<String> getValueSerializer() {
                return new JavaSerializer<>(getClassLoader());
            }

            @Override
            public int getDispatcherConcurrency() {
                return 1;
            }

            @Override
            public CacheLoaderWriter<? super Number, String> getCacheLoaderWriter() {
                return null;
            }
        };
        ServiceLocator serviceLocator = getServiceLocator(persistenceLocation);
        serviceLocator.startAllServices();
        TieredStore.Provider tieredStoreProvider = new TieredStore.Provider();
        tieredStoreProvider.start(serviceLocator);
        CacheConfiguration<Number, String> cacheConfiguration = MockitoUtil.mock(CacheConfiguration.class);
        Mockito.when(cacheConfiguration.getResourcePools()).thenReturn(ResourcePoolsBuilder.newResourcePoolsBuilder().disk(1, MemoryUnit.MB, true).build());
        DiskResourceService diskResourceService = serviceLocator.getService(DiskResourceService.class);
        PersistableResourceService.PersistenceSpaceIdentifier<?> persistenceSpace = diskResourceService.getPersistenceSpaceIdentifier("testTieredStoreReleaseFlushesEntries", cacheConfiguration);
        Store<Number, String> tieredStore = tieredStoreProvider.createStore(configuration, persistenceSpace);
        tieredStoreProvider.initStore(tieredStore);
        for (int i = 0; i < 100; i++) {
            tieredStore.put(i, "hello");
        }
        for (int j = 0; j < 20; j++) {
            for (int i = 0; i < 20; i++) {
                tieredStore.get(i);
            }
        }
        // Keep the creation time to make sure we have them at restart
        long[] creationTimes = new long[20];
        for (int i = 0; i < 20; i++) {
            creationTimes[i] = tieredStore.get(i).creationTime();
        }
        tieredStoreProvider.releaseStore(tieredStore);
        tieredStoreProvider.stop();
        serviceLocator.stopAllServices();
        ServiceLocator serviceLocator1 = getServiceLocator(persistenceLocation);
        serviceLocator1.startAllServices();
        tieredStoreProvider.start(serviceLocator1);
        DiskResourceService diskResourceService1 = serviceLocator1.getService(DiskResourceService.class);
        PersistableResourceService.PersistenceSpaceIdentifier<?> persistenceSpace1 = diskResourceService1.getPersistenceSpaceIdentifier("testTieredStoreReleaseFlushesEntries", cacheConfiguration);
        tieredStore = tieredStoreProvider.createStore(configuration, persistenceSpace1);
        tieredStoreProvider.initStore(tieredStore);
        for (int i = 0; i < 20; i++) {
            Assert.assertThat(tieredStore.get(i).creationTime(), Is.is(creationTimes[i]));
        }
    }
}

