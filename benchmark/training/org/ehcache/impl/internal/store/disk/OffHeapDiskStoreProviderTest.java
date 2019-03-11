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
package org.ehcache.impl.internal.store.disk;


import java.util.Collections;
import java.util.Set;
import org.ehcache.core.spi.ServiceLocator;
import org.ehcache.core.spi.service.DiskResourceService;
import org.ehcache.impl.internal.DefaultTimeSourceService;
import org.ehcache.spi.persistence.PersistableResourceService;
import org.ehcache.spi.serialization.SerializationProvider;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.terracotta.context.ContextManager;
import org.terracotta.context.TreeNode;
import org.terracotta.context.query.Matcher;
import org.terracotta.context.query.Matchers;
import org.terracotta.context.query.Query;
import org.terracotta.context.query.QueryBuilder;

import static org.hamcrest.Matchers.empty;


/**
 * OffHeapStoreProviderTest
 */
public class OffHeapDiskStoreProviderTest {
    @Test
    public void testStatisticsAssociations() throws Exception {
        OffHeapDiskStore.Provider provider = new OffHeapDiskStore.Provider();
        ServiceLocator serviceLocator = ServiceLocator.dependencySet().with(Mockito.mock(SerializationProvider.class)).with(new DefaultTimeSourceService(null)).with(Mockito.mock(DiskResourceService.class)).build();
        provider.start(serviceLocator);
        OffHeapDiskStore<Long, String> store = provider.createStore(getStoreConfig(), Mockito.mock(PersistableResourceService.PersistenceSpaceIdentifier.class));
        @SuppressWarnings("unchecked")
        Query storeQuery = QueryBuilder.queryBuilder().children().filter(Matchers.context(Matchers.attributes(Matchers.allOf(Matchers.hasAttribute("tags", new Matcher<Set<String>>() {
            @Override
            protected boolean matchesSafely(Set<String> object) {
                return object.contains("Disk");
            }
        }))))).build();
        Set<TreeNode> nodes = Collections.singleton(ContextManager.nodeFor(store));
        Set<TreeNode> storeResult = storeQuery.execute(nodes);
        Assert.assertThat(storeResult, CoreMatchers.not(empty()));
        provider.releaseStore(store);
        storeResult = storeQuery.execute(nodes);
        Assert.assertThat(storeResult, empty());
    }
}

