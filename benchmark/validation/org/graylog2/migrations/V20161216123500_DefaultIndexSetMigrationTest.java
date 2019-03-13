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
package org.graylog2.migrations;


import com.google.common.collect.ImmutableList;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.List;
import org.graylog2.configuration.ElasticsearchConfiguration;
import org.graylog2.indexer.indexset.DefaultIndexSetCreated;
import org.graylog2.indexer.indexset.IndexSetConfig;
import org.graylog2.indexer.indexset.IndexSetService;
import org.graylog2.indexer.indexset.V20161216123500_Succeeded;
import org.graylog2.plugin.cluster.ClusterConfigService;
import org.graylog2.plugin.indexer.retention.RetentionStrategyConfig;
import org.graylog2.plugin.indexer.rotation.RotationStrategyConfig;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;


public class V20161216123500_DefaultIndexSetMigrationTest {
    @Rule
    public final MockitoRule mockitoRule = MockitoJUnit.rule();

    @Rule
    public final ExpectedException expectedException = ExpectedException.none();

    @Mock
    private IndexSetService indexSetService;

    @Mock
    private ClusterConfigService clusterConfigService;

    private final ElasticsearchConfiguration elasticsearchConfiguration = new ElasticsearchConfiguration();

    private Migration migration;

    @Test
    @SuppressWarnings("deprecation")
    public void upgradeCreatesDefaultIndexSet() throws Exception {
        final RotationStrategyConfig rotationStrategyConfig = Mockito.mock(RotationStrategyConfig.class);
        final RetentionStrategyConfig retentionStrategyConfig = Mockito.mock(RetentionStrategyConfig.class);
        final IndexSetConfig defaultConfig = IndexSetConfig.builder().id("id").title("title").description("description").indexPrefix("prefix").shards(1).replicas(0).rotationStrategy(rotationStrategyConfig).retentionStrategy(retentionStrategyConfig).creationDate(ZonedDateTime.of(2016, 10, 12, 0, 0, 0, 0, ZoneOffset.UTC)).indexAnalyzer("standard").indexTemplateName("prefix-template").indexOptimizationMaxNumSegments(1).indexOptimizationDisabled(false).build();
        final IndexSetConfig additionalConfig = defaultConfig.toBuilder().id("foo").indexPrefix("foo").build();
        final IndexSetConfig savedDefaultConfig = defaultConfig.toBuilder().indexAnalyzer(elasticsearchConfiguration.getAnalyzer()).indexTemplateName(elasticsearchConfiguration.getTemplateName()).indexOptimizationMaxNumSegments(elasticsearchConfiguration.getIndexOptimizationMaxNumSegments()).indexOptimizationDisabled(elasticsearchConfiguration.isDisableIndexOptimization()).build();
        final IndexSetConfig savedAdditionalConfig = additionalConfig.toBuilder().indexAnalyzer(elasticsearchConfiguration.getAnalyzer()).indexTemplateName("foo-template").indexOptimizationMaxNumSegments(elasticsearchConfiguration.getIndexOptimizationMaxNumSegments()).indexOptimizationDisabled(elasticsearchConfiguration.isDisableIndexOptimization()).build();
        Mockito.when(indexSetService.save(ArgumentMatchers.any(IndexSetConfig.class))).thenReturn(savedAdditionalConfig, savedDefaultConfig);
        Mockito.when(indexSetService.getDefault()).thenReturn(defaultConfig);
        Mockito.when(indexSetService.findAll()).thenReturn(ImmutableList.of(defaultConfig, additionalConfig));
        Mockito.when(clusterConfigService.get(DefaultIndexSetCreated.class)).thenReturn(DefaultIndexSetCreated.create());
        final ArgumentCaptor<IndexSetConfig> indexSetConfigCaptor = ArgumentCaptor.forClass(IndexSetConfig.class);
        migration.upgrade();
        Mockito.verify(indexSetService, Mockito.times(2)).save(indexSetConfigCaptor.capture());
        Mockito.verify(clusterConfigService).write(V20161216123500_Succeeded.create());
        final List<IndexSetConfig> allValues = indexSetConfigCaptor.getAllValues();
        assertThat(allValues).hasSize(2);
        final IndexSetConfig capturedDefaultIndexSetConfig = allValues.get(0);
        assertThat(capturedDefaultIndexSetConfig.id()).isEqualTo("id");
        assertThat(capturedDefaultIndexSetConfig.title()).isEqualTo("title");
        assertThat(capturedDefaultIndexSetConfig.description()).isEqualTo("description");
        assertThat(capturedDefaultIndexSetConfig.indexPrefix()).isEqualTo("prefix");
        assertThat(capturedDefaultIndexSetConfig.shards()).isEqualTo(1);
        assertThat(capturedDefaultIndexSetConfig.replicas()).isEqualTo(0);
        assertThat(capturedDefaultIndexSetConfig.rotationStrategy()).isEqualTo(rotationStrategyConfig);
        assertThat(capturedDefaultIndexSetConfig.retentionStrategy()).isEqualTo(retentionStrategyConfig);
        assertThat(capturedDefaultIndexSetConfig.indexAnalyzer()).isEqualTo(elasticsearchConfiguration.getAnalyzer());
        assertThat(capturedDefaultIndexSetConfig.indexTemplateName()).isEqualTo(elasticsearchConfiguration.getTemplateName());
        assertThat(capturedDefaultIndexSetConfig.indexOptimizationMaxNumSegments()).isEqualTo(elasticsearchConfiguration.getIndexOptimizationMaxNumSegments());
        assertThat(capturedDefaultIndexSetConfig.indexOptimizationDisabled()).isEqualTo(elasticsearchConfiguration.isDisableIndexOptimization());
        final IndexSetConfig capturedAdditionalIndexSetConfig = allValues.get(1);
        assertThat(capturedAdditionalIndexSetConfig.id()).isEqualTo("foo");
        assertThat(capturedAdditionalIndexSetConfig.title()).isEqualTo("title");
        assertThat(capturedAdditionalIndexSetConfig.description()).isEqualTo("description");
        assertThat(capturedAdditionalIndexSetConfig.indexPrefix()).isEqualTo("foo");
        assertThat(capturedAdditionalIndexSetConfig.shards()).isEqualTo(1);
        assertThat(capturedAdditionalIndexSetConfig.replicas()).isEqualTo(0);
        assertThat(capturedAdditionalIndexSetConfig.rotationStrategy()).isEqualTo(rotationStrategyConfig);
        assertThat(capturedAdditionalIndexSetConfig.retentionStrategy()).isEqualTo(retentionStrategyConfig);
        assertThat(capturedAdditionalIndexSetConfig.indexAnalyzer()).isEqualTo(elasticsearchConfiguration.getAnalyzer());
        assertThat(capturedAdditionalIndexSetConfig.indexTemplateName()).isEqualTo("foo-template");
        assertThat(capturedAdditionalIndexSetConfig.indexOptimizationMaxNumSegments()).isEqualTo(elasticsearchConfiguration.getIndexOptimizationMaxNumSegments());
        assertThat(capturedAdditionalIndexSetConfig.indexOptimizationDisabled()).isEqualTo(elasticsearchConfiguration.isDisableIndexOptimization());
    }

    @Test
    public void upgradeFailsIfDefaultIndexSetHasNotBeenCreated() {
        Mockito.when(clusterConfigService.get(DefaultIndexSetCreated.class)).thenReturn(null);
        expectedException.expect(IllegalStateException.class);
        expectedException.expectMessage("The default index set hasn't been created yet. This is a bug!");
        migration.upgrade();
    }

    @Test
    public void migrationDoesNotRunAgainIfMigrationWasSuccessfulBefore() {
        Mockito.when(clusterConfigService.get(V20161216123500_Succeeded.class)).thenReturn(V20161216123500_Succeeded.create());
        migration.upgrade();
        Mockito.verify(clusterConfigService).get(V20161216123500_Succeeded.class);
        Mockito.verifyNoMoreInteractions(clusterConfigService);
        Mockito.verifyZeroInteractions(indexSetService);
    }
}

