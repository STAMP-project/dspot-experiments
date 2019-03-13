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
package org.graylog2.indexer.results;


import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import java.util.Collections;
import java.util.Set;
import org.graylog2.plugin.Message;
import org.junit.Test;
import org.mockito.Mockito;


public class SearchResultTest {
    private SearchResult searchResult;

    @Test
    public void extractFieldsForEmptyResult() throws Exception {
        final Set<String> result = searchResult.extractFields(Collections.emptyList());
        assertThat(result).isNotNull().isEmpty();
    }

    @Test
    public void extractFieldsForTwoMessagesContainingDifferentFields() throws Exception {
        final ResultMessage r1 = Mockito.mock(ResultMessage.class);
        final Message m1 = Mockito.mock(Message.class);
        Mockito.when(m1.getFieldNames()).thenReturn(ImmutableSet.of("message", "source", "timestamp", "http_response", "gl2_source_node", "_index"));
        Mockito.when(r1.getMessage()).thenReturn(m1);
        final ResultMessage r2 = Mockito.mock(ResultMessage.class);
        final Message m2 = Mockito.mock(Message.class);
        Mockito.when(m2.getFieldNames()).thenReturn(ImmutableSet.of("message", "source", "timestamp", "took_ms", "gl2_source_collector"));
        Mockito.when(r2.getMessage()).thenReturn(m2);
        final Set<String> result = searchResult.extractFields(ImmutableList.of(r1, r2));
        assertThat(result).isNotNull().isNotEmpty().hasSize(5).containsExactlyInAnyOrder("message", "source", "timestamp", "http_response", "took_ms");
    }
}

