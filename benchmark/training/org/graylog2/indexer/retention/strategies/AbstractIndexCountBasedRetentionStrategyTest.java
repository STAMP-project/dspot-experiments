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
package org.graylog2.indexer.retention.strategies;


import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import org.graylog2.indexer.IndexSet;
import org.graylog2.indexer.indices.Indices;
import org.graylog2.shared.system.activities.Activity;
import org.graylog2.shared.system.activities.ActivityWriter;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;


public class AbstractIndexCountBasedRetentionStrategyTest {
    @Rule
    public final MockitoRule mockitoRule = MockitoJUnit.rule();

    private AbstractIndexCountBasedRetentionStrategy retentionStrategy;

    @Mock
    private Indices indices;

    @Mock
    private ActivityWriter activityWriter;

    @Mock
    private IndexSet indexSet;

    private Map<String, Set<String>> indexMap;

    @Test
    public void shouldRetainOldestIndex() throws Exception {
        retentionStrategy.retain(indexSet);
        final ArgumentCaptor<String> retainedIndexName = ArgumentCaptor.forClass(String.class);
        Mockito.verify(retentionStrategy, Mockito.times(1)).retain(retainedIndexName.capture(), ArgumentMatchers.eq(indexSet));
        assertThat(retainedIndexName.getValue()).isEqualTo("index1");
        Mockito.verify(activityWriter, Mockito.times(2)).write(ArgumentMatchers.any(Activity.class));
    }

    @Test
    public void shouldRetainOldestIndices() throws Exception {
        Mockito.when(retentionStrategy.getMaxNumberOfIndices(ArgumentMatchers.eq(indexSet))).thenReturn(Optional.of(4));
        retentionStrategy.retain(indexSet);
        final ArgumentCaptor<String> retainedIndexName = ArgumentCaptor.forClass(String.class);
        Mockito.verify(retentionStrategy, Mockito.times(2)).retain(retainedIndexName.capture(), ArgumentMatchers.eq(indexSet));
        assertThat(retainedIndexName.getAllValues()).contains("index1", "index2");
        Mockito.verify(activityWriter, Mockito.times(3)).write(ArgumentMatchers.any(Activity.class));
    }

    @Test
    public void shouldIgnoreReopenedIndexWhenCountingAgainstLimit() {
        Mockito.when(indices.isReopened(ArgumentMatchers.eq("index1"))).thenReturn(true);
        retentionStrategy.retain(indexSet);
        Mockito.verify(retentionStrategy, Mockito.never()).retain(ArgumentMatchers.anyString(), ArgumentMatchers.eq(indexSet));
        Mockito.verify(activityWriter, Mockito.never()).write(ArgumentMatchers.any(Activity.class));
    }

    @Test
    public void shouldIgnoreReopenedIndexWhenDeterminingRetainedIndices() {
        Mockito.when(retentionStrategy.getMaxNumberOfIndices(ArgumentMatchers.eq(indexSet))).thenReturn(Optional.of(4));
        Mockito.when(indices.isReopened(ArgumentMatchers.eq("index1"))).thenReturn(true);
        retentionStrategy.retain(indexSet);
        final ArgumentCaptor<String> retainedIndexName = ArgumentCaptor.forClass(String.class);
        Mockito.verify(retentionStrategy, Mockito.times(1)).retain(retainedIndexName.capture(), ArgumentMatchers.eq(indexSet));
        assertThat(retainedIndexName.getValue()).isEqualTo("index2");
        Mockito.verify(activityWriter, Mockito.times(2)).write(ArgumentMatchers.any(Activity.class));
    }

    @Test
    public void shouldIgnoreWriteAliasWhenDeterminingRetainedIndices() {
        final String indexWithWriteIndexAlias = "index1";
        final String writeIndexAlias = "WriteIndexAlias";
        Mockito.when(indexSet.getWriteIndexAlias()).thenReturn(writeIndexAlias);
        indexMap.put(indexWithWriteIndexAlias, Collections.singleton(writeIndexAlias));
        retentionStrategy.retain(indexSet);
        final ArgumentCaptor<String> retainedIndexName = ArgumentCaptor.forClass(String.class);
        Mockito.verify(retentionStrategy, Mockito.times(1)).retain(retainedIndexName.capture(), ArgumentMatchers.eq(indexSet));
        assertThat(retainedIndexName.getValue()).isEqualTo("index2");
        Mockito.verify(activityWriter, Mockito.times(2)).write(ArgumentMatchers.any(Activity.class));
    }
}

