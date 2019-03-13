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
package org.graylog2.alerts.types;


import AlertCondition.CheckResult;
import DateTimeZone.UTC;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Sets;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import org.graylog2.Configuration;
import org.graylog2.alerts.AbstractAlertCondition;
import org.graylog2.alerts.AlertConditionTest;
import org.graylog2.indexer.ranges.IndexRange;
import org.graylog2.indexer.ranges.MongoIndexRange;
import org.graylog2.indexer.results.ResultMessage;
import org.graylog2.indexer.results.SearchResult;
import org.graylog2.indexer.searches.Searches;
import org.graylog2.indexer.searches.Sorting;
import org.graylog2.plugin.alarms.AlertCondition;
import org.graylog2.plugin.indexer.searches.timeranges.RelativeRange;
import org.graylog2.plugin.streams.Stream;
import org.joda.time.DateTime;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


public class FieldContentValueAlertConditionTest extends AlertConditionTest {
    @Test
    public void testConstructor() throws Exception {
        final Map<String, Object> parameters = getParametersMap(0, "field", "value");
        final FieldContentValueAlertCondition condition = getCondition(parameters, AlertConditionTest.alertConditionTitle);
        Assert.assertNotNull(condition);
        Assert.assertNotNull(condition.getDescription());
    }

    @Test
    public void testRunMatchingMessagesInStream() throws Exception {
        final ResultMessage searchHit = ResultMessage.parseFromSource("some_id", "graylog_test", Collections.singletonMap("message", "something is in here"));
        final DateTime now = DateTime.now(UTC);
        final IndexRange indexRange = MongoIndexRange.create("graylog_test", now.minusDays(1), now, now, 0);
        final Set<IndexRange> indexRanges = Sets.newHashSet(indexRange);
        final SearchResult searchResult = Mockito.spy(new SearchResult(Collections.singletonList(searchHit), 1L, indexRanges, "message:something", null, 100L));
        Mockito.when(searchResult.getTotalResults()).thenReturn(1L);
        Mockito.when(searches.search(ArgumentMatchers.anyString(), ArgumentMatchers.anyString(), ArgumentMatchers.any(RelativeRange.class), ArgumentMatchers.anyInt(), ArgumentMatchers.anyInt(), ArgumentMatchers.any(Sorting.class))).thenReturn(searchResult);
        final FieldContentValueAlertCondition condition = getCondition(getParametersMap(0, "message", "something"), "Alert Condition for testing");
        final AlertCondition.CheckResult result = condition.runCheck();
        assertTriggered(condition, result);
    }

    @Test
    public void testRunNoMatchingMessages() throws Exception {
        final DateTime now = DateTime.now(UTC);
        final IndexRange indexRange = MongoIndexRange.create("graylog_test", now.minusDays(1), now, now, 0);
        final Set<IndexRange> indexRanges = Sets.newHashSet(indexRange);
        final SearchResult searchResult = Mockito.spy(new SearchResult(Collections.emptyList(), 0L, indexRanges, "message:something", null, 100L));
        Mockito.when(searches.search(ArgumentMatchers.anyString(), ArgumentMatchers.anyString(), ArgumentMatchers.any(RelativeRange.class), ArgumentMatchers.anyInt(), ArgumentMatchers.anyInt(), ArgumentMatchers.any(Sorting.class))).thenReturn(searchResult);
        final FieldContentValueAlertCondition condition = getCondition(getParametersMap(0, "message", "something"), AlertConditionTest.alertConditionTitle);
        final AlertCondition.CheckResult result = condition.runCheck();
        assertNotTriggered(result);
    }

    @Test
    public void testCorrectUsageOfRelativeRange() throws Exception {
        final Stream stream = Mockito.mock(Stream.class);
        final Searches searches = Mockito.mock(Searches.class);
        final Configuration configuration = Mockito.mock(Configuration.class);
        final SearchResult searchResult = Mockito.mock(SearchResult.class);
        final int alertCheckInterval = 42;
        final RelativeRange relativeRange = RelativeRange.create(alertCheckInterval);
        Mockito.when(stream.getId()).thenReturn("stream-id");
        Mockito.when(configuration.getAlertCheckInterval()).thenReturn(alertCheckInterval);
        Mockito.when(searches.search(ArgumentMatchers.anyString(), ArgumentMatchers.anyString(), ArgumentMatchers.eq(relativeRange), ArgumentMatchers.anyInt(), ArgumentMatchers.anyInt(), ArgumentMatchers.any(Sorting.class))).thenReturn(searchResult);
        final FieldContentValueAlertCondition alertCondition = new FieldContentValueAlertCondition(searches, configuration, stream, null, DateTime.now(UTC), "mockuser", ImmutableMap.<String, Object>of("field", "test", "value", "test"), "Field Content Value Test COndition");
        final AbstractAlertCondition.CheckResult result = alertCondition.runCheck();
    }
}

