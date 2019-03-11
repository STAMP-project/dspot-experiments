/**
 * This file is part of Graylog.
 *
 * Graylog is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Graylog is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Graylog.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.graylog2.indexer;


import MongoIndexSet.Factory;
import MongoIndexSetRegistry.IndexSetsCache;
import com.google.common.collect.ImmutableSet;
import com.google.common.eventbus.EventBus;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.graylog2.indexer.indexset.IndexSetConfig;
import org.graylog2.indexer.indexset.IndexSetService;
import org.graylog2.indexer.indexset.events.IndexSetCreatedEvent;
import org.graylog2.indexer.indexset.events.IndexSetDeletedEvent;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;


public class MongoIndexSetRegistryTest {
    @Rule
    public final MockitoRule mockitoRule = MockitoJUnit.rule();

    private MongoIndexSetRegistry indexSetRegistry;

    private IndexSetsCache indexSetsCache;

    @Mock
    private IndexSetService indexSetService;

    @Mock
    private Factory mongoIndexSetFactory;

    @Mock
    private EventBus serverEventBus;

    @Test
    public void indexSetsCacheShouldReturnCachedList() {
        final IndexSetConfig indexSetConfig = Mockito.mock(IndexSetConfig.class);
        final List<IndexSetConfig> indexSetConfigs = Collections.singletonList(indexSetConfig);
        Mockito.when(indexSetService.findAll()).thenReturn(indexSetConfigs);
        final List<IndexSetConfig> result = this.indexSetsCache.get();
        assertThat(result).isNotNull().hasSize(1).containsExactly(indexSetConfig);
        final List<IndexSetConfig> cachedResult = this.indexSetsCache.get();
        assertThat(cachedResult).isNotNull().hasSize(1).containsExactly(indexSetConfig);
        Mockito.verify(indexSetService, Mockito.times(1)).findAll();
    }

    @Test
    public void indexSetsCacheShouldReturnNewListAfterInvalidate() {
        final IndexSetConfig indexSetConfig = Mockito.mock(IndexSetConfig.class);
        final List<IndexSetConfig> indexSetConfigs = Collections.singletonList(indexSetConfig);
        Mockito.when(indexSetService.findAll()).thenReturn(indexSetConfigs);
        final List<IndexSetConfig> result = this.indexSetsCache.get();
        assertThat(result).isNotNull().hasSize(1).containsExactly(indexSetConfig);
        this.indexSetsCache.invalidate();
        final IndexSetConfig newIndexSetConfig = Mockito.mock(IndexSetConfig.class);
        final List<IndexSetConfig> newIndexSetConfigs = Collections.singletonList(newIndexSetConfig);
        Mockito.when(indexSetService.findAll()).thenReturn(newIndexSetConfigs);
        final List<IndexSetConfig> newResult = this.indexSetsCache.get();
        assertThat(newResult).isNotNull().hasSize(1).containsExactly(newIndexSetConfig);
        Mockito.verify(indexSetService, Mockito.times(2)).findAll();
    }

    @Test
    public void indexSetsCacheShouldBeInvalidatedForIndexSetCreation() {
        final IndexSetConfig indexSetConfig = Mockito.mock(IndexSetConfig.class);
        final List<IndexSetConfig> indexSetConfigs = Collections.singletonList(indexSetConfig);
        Mockito.when(indexSetService.findAll()).thenReturn(indexSetConfigs);
        final List<IndexSetConfig> result = this.indexSetsCache.get();
        assertThat(result).isNotNull().hasSize(1).containsExactly(indexSetConfig);
        this.indexSetsCache.handleIndexSetCreation(Mockito.mock(IndexSetCreatedEvent.class));
        final IndexSetConfig newIndexSetConfig = Mockito.mock(IndexSetConfig.class);
        final List<IndexSetConfig> newIndexSetConfigs = Collections.singletonList(newIndexSetConfig);
        Mockito.when(indexSetService.findAll()).thenReturn(newIndexSetConfigs);
        final List<IndexSetConfig> newResult = this.indexSetsCache.get();
        assertThat(newResult).isNotNull().hasSize(1).containsExactly(newIndexSetConfig);
        Mockito.verify(indexSetService, Mockito.times(2)).findAll();
    }

    @Test
    public void indexSetsCacheShouldBeInvalidatedForIndexSetDeletion() {
        final IndexSetConfig indexSetConfig = Mockito.mock(IndexSetConfig.class);
        final List<IndexSetConfig> indexSetConfigs = Collections.singletonList(indexSetConfig);
        Mockito.when(indexSetService.findAll()).thenReturn(indexSetConfigs);
        final List<IndexSetConfig> result = this.indexSetsCache.get();
        assertThat(result).isNotNull().hasSize(1).containsExactly(indexSetConfig);
        this.indexSetsCache.handleIndexSetDeletion(Mockito.mock(IndexSetDeletedEvent.class));
        final IndexSetConfig newIndexSetConfig = Mockito.mock(IndexSetConfig.class);
        final List<IndexSetConfig> newIndexSetConfigs = Collections.singletonList(newIndexSetConfig);
        Mockito.when(indexSetService.findAll()).thenReturn(newIndexSetConfigs);
        final List<IndexSetConfig> newResult = this.indexSetsCache.get();
        assertThat(newResult).isNotNull().hasSize(1).containsExactly(newIndexSetConfig);
        Mockito.verify(indexSetService, Mockito.times(2)).findAll();
    }

    @Test
    public void getAllShouldBeCachedForEmptyList() {
        final List<IndexSetConfig> indexSetConfigs = Collections.emptyList();
        Mockito.when(indexSetService.findAll()).thenReturn(indexSetConfigs);
        assertThat(this.indexSetRegistry.getAll()).isNotNull().isEmpty();
        assertThat(this.indexSetRegistry.getAll()).isNotNull().isEmpty();
        Mockito.verify(indexSetService, Mockito.times(1)).findAll();
    }

    @Test
    public void getAllShouldBeCachedForNonEmptyList() {
        final IndexSetConfig indexSetConfig = Mockito.mock(IndexSetConfig.class);
        final List<IndexSetConfig> indexSetConfigs = Collections.singletonList(indexSetConfig);
        final MongoIndexSet indexSet = Mockito.mock(MongoIndexSet.class);
        Mockito.when(mongoIndexSetFactory.create(indexSetConfig)).thenReturn(indexSet);
        Mockito.when(indexSetService.findAll()).thenReturn(indexSetConfigs);
        assertThat(this.indexSetRegistry.getAll()).isNotNull().isNotEmpty().hasSize(1).containsExactly(indexSet);
        assertThat(this.indexSetRegistry.getAll()).isNotNull().isNotEmpty().hasSize(1).containsExactly(indexSet);
        Mockito.verify(indexSetService, Mockito.times(1)).findAll();
    }

    @Test
    public void getAllShouldNotBeCachedForCallAfterInvalidate() {
        final IndexSetConfig indexSetConfig = Mockito.mock(IndexSetConfig.class);
        final List<IndexSetConfig> indexSetConfigs = Collections.singletonList(indexSetConfig);
        final MongoIndexSet indexSet = Mockito.mock(MongoIndexSet.class);
        Mockito.when(mongoIndexSetFactory.create(indexSetConfig)).thenReturn(indexSet);
        Mockito.when(indexSetService.findAll()).thenReturn(indexSetConfigs);
        assertThat(this.indexSetRegistry.getAll()).isNotNull().isNotEmpty().hasSize(1).containsExactly(indexSet);
        this.indexSetsCache.invalidate();
        assertThat(this.indexSetRegistry.getAll()).isNotNull().isNotEmpty().hasSize(1).containsExactly(indexSet);
        Mockito.verify(indexSetService, Mockito.times(2)).findAll();
    }

    @Test
    public void isManagedIndexReturnsAMapOfIndices() {
        final IndexSetConfig indexSetConfig = Mockito.mock(IndexSetConfig.class);
        final List<IndexSetConfig> indexSetConfigs = Collections.singletonList(indexSetConfig);
        final MongoIndexSet indexSet = Mockito.mock(MongoIndexSet.class);
        Mockito.when(mongoIndexSetFactory.create(indexSetConfig)).thenReturn(indexSet);
        Mockito.when(indexSetService.findAll()).thenReturn(indexSetConfigs);
        Mockito.when(indexSet.isManagedIndex("index1")).thenReturn(true);
        Mockito.when(indexSet.isManagedIndex("index2")).thenReturn(false);
        final Map<String, Boolean> managedStatus = indexSetRegistry.isManagedIndex(ImmutableSet.of("index1", "index2"));
        assertThat(managedStatus).containsEntry("index1", true).containsEntry("index2", false);
    }

    @Test
    public void isManagedIndexWithManagedIndexReturnsTrue() {
        final IndexSetConfig indexSetConfig = Mockito.mock(IndexSetConfig.class);
        final List<IndexSetConfig> indexSetConfigs = Collections.singletonList(indexSetConfig);
        final MongoIndexSet indexSet = Mockito.mock(MongoIndexSet.class);
        Mockito.when(mongoIndexSetFactory.create(indexSetConfig)).thenReturn(indexSet);
        Mockito.when(indexSetService.findAll()).thenReturn(indexSetConfigs);
        Mockito.when(indexSet.isManagedIndex("index")).thenReturn(true);
        assertThat(indexSetRegistry.isManagedIndex("index")).isTrue();
    }

    @Test
    public void isManagedIndexWithUnmanagedIndexReturnsFalse() {
        final IndexSetConfig indexSetConfig = Mockito.mock(IndexSetConfig.class);
        final List<IndexSetConfig> indexSetConfigs = Collections.singletonList(indexSetConfig);
        final MongoIndexSet indexSet = Mockito.mock(MongoIndexSet.class);
        Mockito.when(mongoIndexSetFactory.create(indexSetConfig)).thenReturn(indexSet);
        Mockito.when(indexSetService.findAll()).thenReturn(indexSetConfigs);
        Mockito.when(indexSet.isManagedIndex("index")).thenReturn(false);
        assertThat(indexSetRegistry.isManagedIndex("index")).isFalse();
    }
}

