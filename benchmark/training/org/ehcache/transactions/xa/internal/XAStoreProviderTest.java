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
package org.ehcache.transactions.xa.internal;


import XAStore.Provider;
import java.util.Collections;
import org.ehcache.config.builders.ResourcePoolsBuilder;
import org.ehcache.config.units.MemoryUnit;
import org.ehcache.core.spi.store.Store;
import org.ehcache.core.spi.time.TimeSourceService;
import org.ehcache.impl.internal.DefaultTimeSourceService;
import org.ehcache.impl.internal.store.offheap.OffHeapStore;
import org.ehcache.spi.persistence.StateRepository;
import org.ehcache.spi.serialization.StatefulSerializer;
import org.ehcache.spi.service.Service;
import org.ehcache.spi.service.ServiceProvider;
import org.ehcache.transactions.xa.configuration.XAStoreConfiguration;
import org.ehcache.transactions.xa.internal.journal.Journal;
import org.ehcache.transactions.xa.internal.journal.JournalProvider;
import org.ehcache.transactions.xa.txmgr.TransactionManagerWrapper;
import org.ehcache.transactions.xa.txmgr.provider.TransactionManagerProvider;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


/**
 * XAStoreProviderTest
 */
public class XAStoreProviderTest {
    @SuppressWarnings("unchecked")
    @Test
    public void testXAStoreProviderStatefulSerializer() {
        OffHeapStore.Provider underlyingStoreProvider = new OffHeapStore.Provider();
        JournalProvider journalProvider = Mockito.mock(JournalProvider.class);
        Mockito.when(journalProvider.getJournal(null, null)).thenReturn(Mockito.mock(Journal.class));
        TransactionManagerProvider transactionManagerProvider = Mockito.mock(TransactionManagerProvider.class);
        Mockito.when(transactionManagerProvider.getTransactionManagerWrapper()).thenReturn(Mockito.mock(TransactionManagerWrapper.class));
        ServiceProvider<Service> serviceProvider = Mockito.mock(ServiceProvider.class);
        Mockito.when(serviceProvider.getService(JournalProvider.class)).thenReturn(journalProvider);
        Mockito.when(serviceProvider.getService(TimeSourceService.class)).thenReturn(new DefaultTimeSourceService(null));
        Mockito.when(serviceProvider.getService(TransactionManagerProvider.class)).thenReturn(transactionManagerProvider);
        Mockito.when(serviceProvider.getServicesOfType(Store.Provider.class)).thenReturn(Collections.singleton(underlyingStoreProvider));
        Store.Configuration<String, String> configuration = Mockito.mock(Store.Configuration.class);
        Mockito.when(configuration.getResourcePools()).thenReturn(ResourcePoolsBuilder.newResourcePoolsBuilder().offheap(1, MemoryUnit.MB).build());
        Mockito.when(configuration.getDispatcherConcurrency()).thenReturn(1);
        StatefulSerializer<String> valueSerializer = Mockito.mock(StatefulSerializer.class);
        Mockito.when(configuration.getValueSerializer()).thenReturn(valueSerializer);
        underlyingStoreProvider.start(serviceProvider);
        XAStore.Provider provider = new XAStore.Provider();
        provider.start(serviceProvider);
        Store<String, String> store = provider.createStore(configuration, Mockito.mock(XAStoreConfiguration.class));
        provider.initStore(store);
        Mockito.verify(valueSerializer).init(ArgumentMatchers.any(StateRepository.class));
    }
}

