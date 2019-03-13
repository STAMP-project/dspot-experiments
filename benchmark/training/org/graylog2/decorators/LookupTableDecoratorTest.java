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
package org.graylog2.decorators;


import LookupTableService.Function;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMultimap;
import java.util.List;
import org.apache.commons.lang3.tuple.Pair;
import org.graylog2.lookup.LookupTableService;
import org.graylog2.plugin.lookup.LookupResult;
import org.graylog2.rest.models.messages.responses.ResultMessageSummary;
import org.graylog2.rest.resources.search.responses.SearchResponse;
import org.junit.Test;
import org.mockito.Mockito;


public class LookupTableDecoratorTest {
    @Test
    public void decorate() throws Exception {
        final String sourceField = "source";
        final String targetField = "source_decorated";
        final String lookupTableName = "test";
        final Decorator decorator = createDecorator(sourceField, targetField, lookupTableName);
        final Pair<LookupTableDecorator, LookupTableService.Function> lookupTableDecoratorPair = createLookupTableDecorator(decorator);
        final LookupTableDecorator lookupTableDecorator = lookupTableDecoratorPair.getLeft();
        final LookupTableService.Function function = lookupTableDecoratorPair.getRight();
        final List<ResultMessageSummary> messages = ImmutableList.of(ResultMessageSummary.create(ImmutableMultimap.of(), ImmutableMap.of("_id", "a", sourceField, "0"), "graylog_0"), ResultMessageSummary.create(ImmutableMultimap.of(), ImmutableMap.of("_id", "b", sourceField, "1"), "graylog_0"), ResultMessageSummary.create(ImmutableMultimap.of(), ImmutableMap.of("_id", "c", sourceField, "2"), "graylog_0"), ResultMessageSummary.create(ImmutableMultimap.of(), ImmutableMap.of("_id", "d", sourceField, "3"), "graylog_0"), ResultMessageSummary.create(ImmutableMultimap.of(), ImmutableMap.of("_id", "e", "invalid", "4"), "graylog_0"));
        final SearchResponse searchResponse = createSearchResponse(messages);
        Mockito.when(function.lookup("0")).thenReturn(LookupResult.single("zero"));
        Mockito.when(function.lookup("1")).thenReturn(LookupResult.single("one"));
        Mockito.when(function.lookup("2")).thenReturn(LookupResult.empty());
        Mockito.when(function.lookup("3")).thenReturn(null);
        final SearchResponse response = lookupTableDecorator.apply(searchResponse);
        assertThat(response.messages().get(0).message().get(sourceField)).isEqualTo("0");
        assertThat(response.messages().get(0).message().get(targetField)).isEqualTo("zero");
        assertThat(response.messages().get(1).message().get(sourceField)).isEqualTo("1");
        assertThat(response.messages().get(1).message().get(targetField)).isEqualTo("one");
        assertThat(response.messages().get(2).message().get(sourceField)).isEqualTo("2");
        assertThat(response.messages().get(2).message()).doesNotContainKey(targetField);
        assertThat(response.messages().get(3).message().get(sourceField)).isEqualTo("3");
        assertThat(response.messages().get(3).message()).doesNotContainKey(targetField);
        assertThat(response.messages().get(4).message().get("invalid")).isEqualTo("4");
        assertThat(response.messages().get(4).message()).doesNotContainKey(targetField);
    }

    @Test(expected = IllegalStateException.class)
    public void withNullSourceField() throws Exception {
        createLookupTableDecorator(createDecorator("", "bar", "test"));
    }

    @Test(expected = IllegalStateException.class)
    public void withNullTargetField() throws Exception {
        createLookupTableDecorator(createDecorator("foo", "", "test"));
    }

    @Test(expected = IllegalStateException.class)
    public void withoutLookupTableName() throws Exception {
        createLookupTableDecorator(createDecorator("foo", "bar", ""));
    }
}

