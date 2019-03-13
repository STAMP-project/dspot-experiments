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
package org.graylog2.indexer.counts;


import Bulk.Builder;
import com.google.common.collect.ImmutableMap;
import io.searchbox.core.Bulk;
import io.searchbox.core.BulkResult;
import io.searchbox.core.Index;
import java.util.Map;
import org.graylog2.ElasticsearchBase;
import org.graylog2.indexer.IndexNotFoundException;
import org.graylog2.indexer.IndexSet;
import org.graylog2.indexer.IndexSetRegistry;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;


public class CountsIT extends ElasticsearchBase {
    private static final String INDEX_NAME_1 = "index_set_1_counts_test_0";

    private static final String INDEX_NAME_2 = "index_set_2_counts_test_0";

    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();

    @Mock
    private IndexSetRegistry indexSetRegistry;

    @Mock
    private IndexSet indexSet1;

    @Mock
    private IndexSet indexSet2;

    private Counts counts;

    @Test
    public void totalReturnsZeroWithEmptyIndex() throws Exception {
        assertThat(counts.total()).isEqualTo(0L);
        assertThat(counts.total(indexSet1)).isEqualTo(0L);
        assertThat(counts.total(indexSet2)).isEqualTo(0L);
    }

    @Test
    public void totalReturnsZeroWithNoIndices() throws Exception {
        final Bulk.Builder bulkBuilder = new Bulk.Builder().refresh(true);
        for (int i = 0; i < 10; i++) {
            final Map<String, Object> source = ImmutableMap.of("foo", "bar", "counter", i);
            final Index indexRequest = new Index.Builder(source).index(CountsIT.INDEX_NAME_1).type("test").refresh(true).build();
            bulkBuilder.addAction(indexRequest);
        }
        final BulkResult bulkResult = client().execute(bulkBuilder.build());
        assertSucceeded(bulkResult);
        assertThat(bulkResult.getFailedItems()).isEmpty();
        // Simulate no indices for the second index set.
        Mockito.when(indexSet2.getManagedIndices()).thenReturn(new String[0]);
        assertThat(counts.total(indexSet1)).isEqualTo(10L);
        assertThat(counts.total(indexSet2)).isEqualTo(0L);
        // Simulate no indices for all index sets.
        Mockito.when(indexSetRegistry.getManagedIndices()).thenReturn(new String[0]);
        assertThat(counts.total()).isEqualTo(0L);
    }

    @Test
    public void totalReturnsNumberOfMessages() throws Exception {
        final Bulk.Builder bulkBuilder = new Bulk.Builder().refresh(true);
        final int count1 = 10;
        for (int i = 0; i < count1; i++) {
            final Map<String, Object> source = ImmutableMap.of("foo", "bar", "counter", i);
            final Index indexRequest = new Index.Builder(source).index(CountsIT.INDEX_NAME_1).type("test").refresh(true).build();
            bulkBuilder.addAction(indexRequest);
        }
        final int count2 = 5;
        for (int i = 0; i < count2; i++) {
            final Map<String, Object> source = ImmutableMap.of("foo", "bar", "counter", i);
            final Index indexRequest = new Index.Builder(source).index(CountsIT.INDEX_NAME_2).type("test").refresh(true).build();
            bulkBuilder.addAction(indexRequest);
        }
        final BulkResult bulkResult = client().execute(bulkBuilder.build());
        assertSucceeded(bulkResult);
        assertThat(bulkResult.getFailedItems()).isEmpty();
        assertThat(counts.total()).isEqualTo((count1 + count2));
        assertThat(counts.total(indexSet1)).isEqualTo(count1);
        assertThat(counts.total(indexSet2)).isEqualTo(count2);
    }

    @Test
    public void totalThrowsElasticsearchExceptionIfIndexDoesNotExist() throws Exception {
        final IndexSet indexSet = Mockito.mock(IndexSet.class);
        Mockito.when(indexSet.getManagedIndices()).thenReturn(new String[]{ "does_not_exist" });
        try {
            counts.total(indexSet);
            fail("Expected IndexNotFoundException");
        } catch (IndexNotFoundException e) {
            final String expectedErrorDetail = "Index not found for query: does_not_exist. Try recalculating your index ranges.";
            assertThat(e).hasMessageStartingWith("Fetching message count failed for indices [does_not_exist]").hasMessageEndingWith(expectedErrorDetail).hasNoSuppressedExceptions();
            assertThat(e.getErrorDetails()).containsExactly(expectedErrorDetail);
        }
    }

    @Test
    public void totalSucceedsWithListOfIndicesLargerThan4Kilobytes() throws Exception {
        final int numberOfIndices = 100;
        final String[] indexNames = new String[numberOfIndices];
        final String indexPrefix = "very_long_list_of_indices_0123456789_counts_it_";
        final IndexSet indexSet = Mockito.mock(IndexSet.class);
        try {
            for (int i = 0; i < numberOfIndices; i++) {
                final String indexName = indexPrefix + i;
                createIndex(indexName);
                indexNames[i] = indexName;
            }
            Mockito.when(indexSet.getManagedIndices()).thenReturn(indexNames);
            final String indicesString = String.join(",", indexNames);
            assertThat(indicesString.length()).isGreaterThanOrEqualTo(4096);
            assertThat(counts.total(indexSet)).isEqualTo(0L);
        } finally {
            deleteIndex(indexNames);
        }
    }
}

