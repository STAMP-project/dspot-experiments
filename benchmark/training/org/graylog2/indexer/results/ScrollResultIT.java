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


import IndexMapping.TYPE_MESSAGE;
import Parameters.SCROLL;
import Parameters.SIZE;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lordofthejars.nosqlunit.annotation.UsingDataSet;
import com.lordofthejars.nosqlunit.core.LoadStrategyEnum;
import io.searchbox.core.Search;
import io.searchbox.core.SearchResult;
import java.io.IOException;
import java.util.Collections;
import org.graylog2.ElasticsearchBase;
import org.graylog2.indexer.cluster.jest.JestUtils;
import org.graylog2.shared.bindings.providers.ObjectMapperProvider;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;


public class ScrollResultIT extends ElasticsearchBase {
    @Rule
    public final MockitoRule mockitoRule = MockitoJUnit.rule();

    private static final String INDEX_NAME = "graylog_0";

    private final ObjectMapper objectMapper = new ObjectMapperProvider().get();

    @Test
    @UsingDataSet(loadStrategy = LoadStrategyEnum.CLEAN_INSERT)
    public void nextChunkDoesNotContainJestMetadata() throws IOException {
        final String query = org.elasticsearch.search.builder.SearchSourceBuilder.searchSource().query(matchAllQuery()).toString();
        final Search request = new Search.Builder(query).addIndex(ScrollResultIT.INDEX_NAME).addType(TYPE_MESSAGE).setParameter(SCROLL, "1m").setParameter(SIZE, 5).build();
        final SearchResult searchResult = JestUtils.execute(client(), request, () -> "Exception");
        assertThat(client()).isNotNull();
        final ScrollResult scrollResult = new ScrollResult(client(), objectMapper, searchResult, "*", Collections.singletonList("message"));
        scrollResult.nextChunk().getMessages().forEach(( message) -> assertThat(message.getMessage().getFields()).doesNotContainKeys("es_metadata_id", "es_metadata_version"));
        scrollResult.nextChunk().getMessages().forEach(( message) -> assertThat(message.getMessage().getFields()).doesNotContainKeys("es_metadata_id", "es_metadata_version"));
        assertThat(scrollResult.nextChunk()).isNull();
    }
}

