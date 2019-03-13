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
package org.graylog2.rest.resources.system.indexer;


import IndexSetCleanupJob.Factory;
import IndexStats.DocsStats;
import IndexStats.TimeAndTotalStats;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.Optional;
import javax.inject.Provider;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.ClientErrorException;
import javax.ws.rs.ForbiddenException;
import javax.ws.rs.NotFoundException;
import org.apache.shiro.subject.Subject;
import org.graylog2.indexer.IndexSet;
import org.graylog2.indexer.IndexSetRegistry;
import org.graylog2.indexer.IndexSetStatsCreator;
import org.graylog2.indexer.IndexSetValidator;
import org.graylog2.indexer.indexset.DefaultIndexSetConfig;
import org.graylog2.indexer.indexset.IndexSetConfig;
import org.graylog2.indexer.indexset.IndexSetService;
import org.graylog2.indexer.indices.Indices;
import org.graylog2.indexer.indices.jobs.IndexSetCleanupJob;
import org.graylog2.indexer.indices.stats.IndexStatistics;
import org.graylog2.indexer.retention.strategies.NoopRetentionStrategy;
import org.graylog2.indexer.retention.strategies.NoopRetentionStrategyConfig;
import org.graylog2.indexer.rotation.strategies.MessageCountRotationStrategy;
import org.graylog2.indexer.rotation.strategies.MessageCountRotationStrategyConfig;
import org.graylog2.plugin.cluster.ClusterConfigService;
import org.graylog2.rest.models.system.indexer.responses.IndexStats;
import org.graylog2.rest.resources.system.indexer.requests.IndexSetUpdateRequest;
import org.graylog2.rest.resources.system.indexer.responses.IndexSetResponse;
import org.graylog2.rest.resources.system.indexer.responses.IndexSetStats;
import org.graylog2.rest.resources.system.indexer.responses.IndexSetSummary;
import org.graylog2.shared.bindings.GuiceInjectorHolder;
import org.graylog2.system.jobs.SystemJobManager;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import static org.mockito.ArgumentMatchers.any;


public class IndexSetsResourceTest {
    @Rule
    public final MockitoRule mockitoRule = MockitoJUnit.rule();

    @Rule
    public final ExpectedException expectedException = ExpectedException.none();

    @Mock
    private Indices indices;

    @Mock
    private IndexSetService indexSetService;

    @Mock
    private IndexSetRegistry indexSetRegistry;

    @Mock
    private IndexSetValidator indexSetValidator;

    @Mock
    private Factory indexSetCleanupJobFactory;

    @Mock
    private IndexSetStatsCreator indexSetStatsCreator;

    @Mock
    private SystemJobManager systemJobManager;

    @Mock
    private ClusterConfigService clusterConfigService;

    public IndexSetsResourceTest() {
        GuiceInjectorHolder.createInjector(Collections.emptyList());
    }

    private IndexSetsResource indexSetsResource;

    private Boolean permitted;

    @Test
    public void list() {
        final IndexSetConfig indexSetConfig = IndexSetConfig.create("id", "title", "description", true, "prefix", 1, 0, MessageCountRotationStrategy.class.getCanonicalName(), MessageCountRotationStrategyConfig.create(1000), NoopRetentionStrategy.class.getCanonicalName(), NoopRetentionStrategyConfig.create(1), ZonedDateTime.of(2016, 10, 10, 12, 0, 0, 0, ZoneOffset.UTC), "standard", "index-template", 1, false);
        Mockito.when(indexSetService.findAll()).thenReturn(Collections.singletonList(indexSetConfig));
        final IndexSetResponse list = indexSetsResource.list(0, 0, false);
        Mockito.verify(indexSetService, Mockito.times(1)).findAll();
        Mockito.verify(indexSetService, Mockito.times(1)).getDefault();
        Mockito.verifyNoMoreInteractions(indexSetService);
        assertThat(list.total()).isEqualTo(1);
        assertThat(list.indexSets()).containsExactly(IndexSetSummary.fromIndexSetConfig(indexSetConfig, false));
    }

    @Test
    public void listDenied() {
        notPermitted();
        final IndexSetConfig indexSetConfig = IndexSetConfig.create("id", "title", "description", true, "prefix", 1, 0, MessageCountRotationStrategy.class.getCanonicalName(), MessageCountRotationStrategyConfig.create(1000), NoopRetentionStrategy.class.getCanonicalName(), NoopRetentionStrategyConfig.create(1), ZonedDateTime.of(2016, 10, 10, 12, 0, 0, 0, ZoneOffset.UTC), "standard", "index-template", 1, false);
        Mockito.when(indexSetService.findAll()).thenReturn(Collections.singletonList(indexSetConfig));
        final IndexSetResponse list = indexSetsResource.list(0, 0, false);
        Mockito.verify(indexSetService, Mockito.times(1)).findAll();
        Mockito.verify(indexSetService, Mockito.times(1)).getDefault();
        Mockito.verifyNoMoreInteractions(indexSetService);
        assertThat(list.total()).isEqualTo(0);
        assertThat(list.indexSets()).isEmpty();
    }

    @Test
    public void list0() {
        Mockito.when(indexSetService.findAll()).thenReturn(Collections.emptyList());
        final IndexSetResponse list = indexSetsResource.list(0, 0, false);
        Mockito.verify(indexSetService, Mockito.times(1)).findAll();
        Mockito.verify(indexSetService, Mockito.times(1)).getDefault();
        Mockito.verifyNoMoreInteractions(indexSetService);
        assertThat(list.total()).isEqualTo(0);
        assertThat(list.indexSets()).isEmpty();
    }

    @Test
    public void get() {
        final IndexSetConfig indexSetConfig = IndexSetConfig.create("id", "title", "description", true, "prefix", 1, 0, MessageCountRotationStrategy.class.getCanonicalName(), MessageCountRotationStrategyConfig.create(1000), NoopRetentionStrategy.class.getCanonicalName(), NoopRetentionStrategyConfig.create(1), ZonedDateTime.of(2016, 10, 10, 12, 0, 0, 0, ZoneOffset.UTC), "standard", "index-template", 1, false);
        Mockito.when(indexSetService.get("id")).thenReturn(Optional.of(indexSetConfig));
        final IndexSetSummary summary = indexSetsResource.get("id");
        Mockito.verify(indexSetService, Mockito.times(1)).get("id");
        Mockito.verify(indexSetService, Mockito.times(1)).getDefault();
        Mockito.verifyNoMoreInteractions(indexSetService);
        assertThat(summary).isEqualTo(IndexSetSummary.fromIndexSetConfig(indexSetConfig, false));
    }

    @Test
    public void get0() {
        Mockito.when(indexSetService.get("id")).thenReturn(Optional.empty());
        expectedException.expect(NotFoundException.class);
        expectedException.expectMessage("Couldn't load index set with ID <id>");
        try {
            indexSetsResource.get("id");
        } finally {
            Mockito.verify(indexSetService, Mockito.times(1)).get("id");
            Mockito.verify(indexSetService, Mockito.times(1)).getDefault();
            Mockito.verifyNoMoreInteractions(indexSetService);
        }
    }

    @Test
    public void getDenied() {
        notPermitted();
        expectedException.expect(ForbiddenException.class);
        expectedException.expectMessage("Not authorized to access resource id <id>");
        try {
            indexSetsResource.get("id");
        } finally {
            Mockito.verifyZeroInteractions(indexSetService);
        }
    }

    @Test
    public void indexSetStatistics() {
        final IndexSet indexSet = Mockito.mock(IndexSet.class);
        final IndexSetStats indexSetStats = IndexSetStats.create(5L, 23L, 42L);
        Mockito.when(indexSetRegistry.get("id")).thenReturn(Optional.of(indexSet));
        Mockito.when(indexSetStatsCreator.getForIndexSet(indexSet)).thenReturn(indexSetStats);
        assertThat(indexSetsResource.indexSetStatistics("id")).isEqualTo(indexSetStats);
    }

    @Test
    public void indexSetStatistics0() {
        Mockito.when(indexSetRegistry.get("id")).thenReturn(Optional.empty());
        expectedException.expect(NotFoundException.class);
        expectedException.expectMessage("Couldn't load index set with ID <id>");
        try {
            indexSetsResource.indexSetStatistics("id");
        } finally {
            Mockito.verify(indexSetRegistry, Mockito.times(1)).get("id");
            Mockito.verifyNoMoreInteractions(indexSetRegistry);
        }
    }

    @Test
    public void indexSetStatisticsDenied() {
        notPermitted();
        expectedException.expect(ForbiddenException.class);
        expectedException.expectMessage("Not authorized to access resource id <id>");
        try {
            indexSetsResource.indexSetStatistics("id");
        } finally {
            Mockito.verifyZeroInteractions(indexSetRegistry);
        }
    }

    @Test
    public void save() {
        final IndexSetConfig indexSetConfig = IndexSetConfig.create("title", "description", true, "prefix", 1, 0, MessageCountRotationStrategy.class.getCanonicalName(), MessageCountRotationStrategyConfig.create(1000), NoopRetentionStrategy.class.getCanonicalName(), NoopRetentionStrategyConfig.create(1), ZonedDateTime.of(2016, 10, 10, 12, 0, 0, 0, ZoneOffset.UTC), "standard", "prefix-template", 1, false);
        final IndexSetConfig savedIndexSetConfig = indexSetConfig.toBuilder().id("id").build();
        Mockito.when(indexSetService.save(indexSetConfig)).thenReturn(savedIndexSetConfig);
        final IndexSetSummary summary = indexSetsResource.save(IndexSetSummary.fromIndexSetConfig(indexSetConfig, false));
        Mockito.verify(indexSetService, Mockito.times(1)).save(indexSetConfig);
        Mockito.verify(indexSetService, Mockito.times(1)).getDefault();
        Mockito.verifyNoMoreInteractions(indexSetService);
        assertThat(summary.toIndexSetConfig()).isEqualTo(savedIndexSetConfig);
    }

    @Test
    public void update() {
        final IndexSetConfig indexSetConfig = IndexSetConfig.create("id", "new title", "description", true, "prefix", 1, 0, MessageCountRotationStrategy.class.getCanonicalName(), MessageCountRotationStrategyConfig.create(1000), NoopRetentionStrategy.class.getCanonicalName(), NoopRetentionStrategyConfig.create(1), ZonedDateTime.of(2016, 10, 10, 12, 0, 0, 0, ZoneOffset.UTC), "standard", "index-template", 1, false);
        final IndexSetConfig updatedIndexSetConfig = indexSetConfig.toBuilder().title("new title").build();
        Mockito.when(indexSetService.get("id")).thenReturn(Optional.of(indexSetConfig));
        Mockito.when(indexSetService.save(indexSetConfig)).thenReturn(updatedIndexSetConfig);
        final IndexSetSummary summary = indexSetsResource.update("id", IndexSetUpdateRequest.fromIndexSetConfig(indexSetConfig));
        Mockito.verify(indexSetService, Mockito.times(1)).get("id");
        Mockito.verify(indexSetService, Mockito.times(1)).save(indexSetConfig);
        Mockito.verify(indexSetService, Mockito.times(1)).getDefault();
        Mockito.verifyNoMoreInteractions(indexSetService);
        // The real update wouldn't replace the index template name?
        final IndexSetConfig actual = summary.toIndexSetConfig().toBuilder().indexTemplateName("index-template").build();
        assertThat(actual).isEqualTo(updatedIndexSetConfig);
    }

    @Test
    public void updateDenied() {
        notPermitted();
        final IndexSetConfig indexSetConfig = IndexSetConfig.create("id", "title", "description", true, "prefix", 1, 0, MessageCountRotationStrategy.class.getCanonicalName(), MessageCountRotationStrategyConfig.create(1000), NoopRetentionStrategy.class.getCanonicalName(), NoopRetentionStrategyConfig.create(1), ZonedDateTime.of(2016, 10, 10, 12, 0, 0, 0, ZoneOffset.UTC), "standard", "index-template", 1, false);
        expectedException.expect(ForbiddenException.class);
        expectedException.expectMessage("Not authorized to access resource id <wrong-id>");
        try {
            indexSetsResource.update("wrong-id", IndexSetUpdateRequest.fromIndexSetConfig(indexSetConfig));
        } finally {
            Mockito.verifyZeroInteractions(indexSetService);
        }
    }

    @Test
    public void updateFailsWhenDefaultSetIsSetReadOnly() throws Exception {
        final String defaultIndexSetId = "defaultIndexSet";
        final IndexSetConfig defaultIndexSetConfig = IndexSetConfig.create(defaultIndexSetId, "title", "description", true, "prefix", 1, 0, MessageCountRotationStrategy.class.getCanonicalName(), MessageCountRotationStrategyConfig.create(1000), NoopRetentionStrategy.class.getCanonicalName(), NoopRetentionStrategyConfig.create(1), ZonedDateTime.of(2016, 10, 10, 12, 0, 0, 0, ZoneOffset.UTC), "standard", "index-template", 1, false);
        Mockito.when(indexSetService.getDefault()).thenReturn(defaultIndexSetConfig);
        Mockito.when(indexSetService.get(defaultIndexSetId)).thenReturn(Optional.of(defaultIndexSetConfig));
        final IndexSetConfig defaultIndexSetConfigSetReadOnly = defaultIndexSetConfig.toBuilder().isWritable(false).build();
        expectedException.expect(ClientErrorException.class);
        expectedException.expectMessage("Default index set must be writable.");
        try {
            indexSetsResource.update("defaultIndexSet", IndexSetUpdateRequest.fromIndexSetConfig(defaultIndexSetConfigSetReadOnly));
        } finally {
            Mockito.verify(indexSetService, Mockito.never()).save(any());
        }
    }

    @Test
    public void delete() throws Exception {
        final IndexSet indexSet = Mockito.mock(IndexSet.class);
        final IndexSetConfig indexSetConfig = Mockito.mock(IndexSetConfig.class);
        Mockito.when(indexSet.getConfig()).thenReturn(indexSetConfig);
        Mockito.when(indexSetRegistry.get("id")).thenReturn(Optional.of(indexSet));
        Mockito.when(indexSetCleanupJobFactory.create(indexSet)).thenReturn(Mockito.mock(IndexSetCleanupJob.class));
        Mockito.when(indexSetRegistry.getDefault()).thenReturn(null);
        Mockito.when(indexSetService.delete("id")).thenReturn(1);
        indexSetsResource.delete("id", false);
        indexSetsResource.delete("id", true);
        Mockito.verify(indexSetRegistry, Mockito.times(2)).getDefault();
        Mockito.verify(indexSetService, Mockito.times(2)).delete("id");
        Mockito.verify(systemJobManager, Mockito.times(1)).submit(ArgumentMatchers.any(IndexSetCleanupJob.class));
        Mockito.verifyNoMoreInteractions(indexSetService);
    }

    @Test
    public void delete0() throws Exception {
        final IndexSet indexSet = Mockito.mock(IndexSet.class);
        final IndexSetConfig indexSetConfig = Mockito.mock(IndexSetConfig.class);
        Mockito.when(indexSet.getConfig()).thenReturn(indexSetConfig);
        Mockito.when(indexSetRegistry.getDefault()).thenReturn(null);
        Mockito.when(indexSetRegistry.get("id")).thenReturn(Optional.of(indexSet));
        Mockito.when(indexSetService.delete("id")).thenReturn(0);
        expectedException.expect(NotFoundException.class);
        expectedException.expectMessage("Couldn't delete index set with ID <id>");
        try {
            indexSetsResource.delete("id", false);
        } finally {
            Mockito.verify(indexSetRegistry, Mockito.times(1)).getDefault();
            Mockito.verify(indexSetService, Mockito.times(1)).delete("id");
            Mockito.verifyNoMoreInteractions(indexSetService);
        }
    }

    @Test
    public void deleteDefaultIndexSet() throws Exception {
        final IndexSet indexSet = Mockito.mock(IndexSet.class);
        final IndexSetConfig indexSetConfig = Mockito.mock(IndexSetConfig.class);
        Mockito.when(indexSet.getConfig()).thenReturn(indexSetConfig);
        Mockito.when(indexSetRegistry.getDefault()).thenReturn(indexSet);
        Mockito.when(indexSetRegistry.get("id")).thenReturn(Optional.of(indexSet));
        Mockito.when(indexSetCleanupJobFactory.create(indexSet)).thenReturn(Mockito.mock(IndexSetCleanupJob.class));
        Mockito.when(indexSetService.delete("id")).thenReturn(1);
        expectedException.expect(BadRequestException.class);
        indexSetsResource.delete("id", false);
        indexSetsResource.delete("id", true);
        Mockito.verify(indexSetService, Mockito.never()).delete("id");
        Mockito.verify(systemJobManager, Mockito.never()).submit(ArgumentMatchers.any(IndexSetCleanupJob.class));
        Mockito.verifyNoMoreInteractions(indexSetService);
    }

    @Test
    public void deleteDenied() {
        notPermitted();
        expectedException.expect(ForbiddenException.class);
        expectedException.expectMessage("Not authorized to access resource id <id>");
        try {
            indexSetsResource.delete("id", false);
        } finally {
            Mockito.verifyZeroInteractions(indexSetService);
        }
    }

    @Test
    public void globalStats() throws Exception {
        final IndexStatistics indexStatistics = IndexStatistics.create("prefix_0", IndexStats.create(TimeAndTotalStats.create(0L, 0L), TimeAndTotalStats.create(0L, 0L), TimeAndTotalStats.create(0L, 0L), TimeAndTotalStats.create(0L, 0L), TimeAndTotalStats.create(0L, 0L), TimeAndTotalStats.create(0L, 0L), TimeAndTotalStats.create(0L, 0L), 0L, 23L, 2L, DocsStats.create(42L, 0L)), IndexStats.create(TimeAndTotalStats.create(0L, 0L), TimeAndTotalStats.create(0L, 0L), TimeAndTotalStats.create(0L, 0L), TimeAndTotalStats.create(0L, 0L), TimeAndTotalStats.create(0L, 0L), TimeAndTotalStats.create(0L, 0L), TimeAndTotalStats.create(0L, 0L), 0L, 23L, 2L, DocsStats.create(42L, 0L)), Collections.emptyList());
        Mockito.when(indices.getClosedIndices(ArgumentMatchers.anyCollection())).thenReturn(Collections.singleton("closed_index_0"));
        Mockito.when(indices.getIndicesStats(ArgumentMatchers.anyCollection())).thenReturn(Collections.singleton(indexStatistics));
        final IndexSetStats indexSetStats = indexSetsResource.globalStats();
        assertThat(indexSetStats).isNotNull();
        assertThat(indexSetStats.indices()).isEqualTo(2L);
        assertThat(indexSetStats.documents()).isEqualTo(42L);
        assertThat(indexSetStats.size()).isEqualTo(23L);
    }

    @Test
    public void globalStats0() throws Exception {
        Mockito.when(indexSetRegistry.getAll()).thenReturn(Collections.emptySet());
        Mockito.when(indices.getIndicesStats(ArgumentMatchers.anyCollection())).thenReturn(Collections.emptySet());
        final IndexSetStats indexSetStats = indexSetsResource.globalStats();
        assertThat(indexSetStats).isNotNull();
        assertThat(indexSetStats.indices()).isEqualTo(0L);
        assertThat(indexSetStats.documents()).isEqualTo(0L);
        assertThat(indexSetStats.size()).isEqualTo(0L);
    }

    @Test
    public void globalStatsDenied() {
        notPermitted();
        expectedException.expect(ForbiddenException.class);
        expectedException.expectMessage("Not authorized");
        try {
            indexSetsResource.globalStats();
        } finally {
            Mockito.verifyZeroInteractions(indexSetService);
        }
    }

    @Test
    public void setDefaultMakesIndexDefaultIfWritable() throws Exception {
        final String indexSetId = "newDefaultIndexSetId";
        final IndexSet indexSet = Mockito.mock(IndexSet.class);
        final IndexSetConfig indexSetConfig = IndexSetConfig.create(indexSetId, "title", "description", true, "prefix", 1, 0, MessageCountRotationStrategy.class.getCanonicalName(), MessageCountRotationStrategyConfig.create(1000), NoopRetentionStrategy.class.getCanonicalName(), NoopRetentionStrategyConfig.create(1), ZonedDateTime.of(2016, 10, 10, 12, 0, 0, 0, ZoneOffset.UTC), "standard", "index-template", 1, false);
        Mockito.when(indexSet.getConfig()).thenReturn(indexSetConfig);
        Mockito.when(indexSetService.get(indexSetId)).thenReturn(Optional.of(indexSetConfig));
        indexSetsResource.setDefault(indexSetId);
        final ArgumentCaptor<DefaultIndexSetConfig> defaultIndexSetIdCaptor = ArgumentCaptor.forClass(DefaultIndexSetConfig.class);
        Mockito.verify(clusterConfigService, Mockito.times(1)).write(defaultIndexSetIdCaptor.capture());
        final DefaultIndexSetConfig defaultIndexSetConfig = defaultIndexSetIdCaptor.getValue();
        assertThat(defaultIndexSetConfig).isNotNull();
        assertThat(defaultIndexSetConfig.defaultIndexSetId()).isEqualTo(indexSetId);
    }

    @Test
    public void setDefaultDoesNotDoAnyThingIfNotPermitted() throws Exception {
        notPermitted();
        expectedException.expect(ForbiddenException.class);
        expectedException.expectMessage("Not authorized to access resource id <someIndexSetId>");
        try {
            indexSetsResource.setDefault("someIndexSetId");
        } finally {
            Mockito.verifyZeroInteractions(indexSetService);
            Mockito.verifyZeroInteractions(clusterConfigService);
        }
    }

    @Test
    public void setDefaultDoesNotDoAnythingForInvalidId() throws Exception {
        final String nonExistingIndexSetId = "nonExistingId";
        Mockito.when(indexSetService.get(nonExistingIndexSetId)).thenReturn(Optional.empty());
        expectedException.expect(NotFoundException.class);
        expectedException.expectMessage((("Index set <" + nonExistingIndexSetId) + "> does not exist"));
        try {
            indexSetsResource.setDefault(nonExistingIndexSetId);
        } finally {
            Mockito.verifyZeroInteractions(clusterConfigService);
        }
    }

    @Test
    public void setDefaultDoesNotDoAnythingIfIndexSetIsNotWritable() throws Exception {
        final String readOnlyIndexSetId = "newDefaultIndexSetId";
        final IndexSet readOnlyIndexSet = Mockito.mock(IndexSet.class);
        final IndexSetConfig readOnlyIndexSetConfig = IndexSetConfig.create(readOnlyIndexSetId, "title", "description", false, "prefix", 1, 0, MessageCountRotationStrategy.class.getCanonicalName(), MessageCountRotationStrategyConfig.create(1000), NoopRetentionStrategy.class.getCanonicalName(), NoopRetentionStrategyConfig.create(1), ZonedDateTime.of(2016, 10, 10, 12, 0, 0, 0, ZoneOffset.UTC), "standard", "index-template", 1, false);
        Mockito.when(readOnlyIndexSet.getConfig()).thenReturn(readOnlyIndexSetConfig);
        Mockito.when(indexSetService.get(readOnlyIndexSetId)).thenReturn(Optional.of(readOnlyIndexSetConfig));
        expectedException.expect(ClientErrorException.class);
        expectedException.expectMessage("Default index set must be writable.");
        try {
            indexSetsResource.setDefault(readOnlyIndexSetId);
        } finally {
            Mockito.verifyZeroInteractions(clusterConfigService);
        }
    }

    private static class TestResource extends IndexSetsResource {
        private final Provider<Boolean> permitted;

        TestResource(Indices indices, IndexSetService indexSetService, IndexSetRegistry indexSetRegistry, IndexSetValidator indexSetValidator, IndexSetCleanupJob.Factory indexSetCleanupJobFactory, IndexSetStatsCreator indexSetStatsCreator, ClusterConfigService clusterConfigService, SystemJobManager systemJobManager, Provider<Boolean> permitted) {
            super(indices, indexSetService, indexSetRegistry, indexSetValidator, indexSetCleanupJobFactory, indexSetStatsCreator, clusterConfigService, systemJobManager);
            this.permitted = permitted;
        }

        @Override
        protected Subject getSubject() {
            final Subject mockSubject = Mockito.mock(Subject.class);
            Mockito.when(mockSubject.isPermitted(ArgumentMatchers.anyString())).thenReturn(permitted.get());
            Mockito.when(mockSubject.getPrincipal()).thenReturn("test-user");
            return mockSubject;
        }
    }
}

