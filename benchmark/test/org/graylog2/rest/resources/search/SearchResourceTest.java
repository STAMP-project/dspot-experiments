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
package org.graylog2.rest.resources.search;


import Period.ZERO;
import org.graylog2.decorators.DecoratorProcessor;
import org.graylog2.indexer.searches.Searches;
import org.graylog2.indexer.searches.SearchesClusterConfig;
import org.graylog2.plugin.cluster.ClusterConfigService;
import org.graylog2.plugin.indexer.searches.timeranges.AbsoluteRange;
import org.graylog2.plugin.indexer.searches.timeranges.TimeRange;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.Period;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;


public class SearchResourceTest {
    @Rule
    public final MockitoRule mockitoRule = MockitoJUnit.rule();

    @Mock
    private Searches searches;

    @Mock
    private ClusterConfigService clusterConfigService;

    @Mock
    private DecoratorProcessor decoratorProcessor;

    private SearchResource searchResource;

    private Period queryLimitPeriod;

    @Test
    public void restrictTimeRangeReturnsGivenTimeRangeWithinLimit() {
        Mockito.when(clusterConfigService.get(SearchesClusterConfig.class)).thenReturn(SearchesClusterConfig.createDefault().toBuilder().queryTimeRangeLimit(queryLimitPeriod).build());
        final DateTime from = new DateTime(2015, 1, 15, 12, 0, DateTimeZone.UTC);
        final DateTime to = from.plusHours(1);
        final TimeRange timeRange = AbsoluteRange.create(from, to);
        final TimeRange restrictedTimeRange = searchResource.restrictTimeRange(timeRange);
        assertThat(restrictedTimeRange).isNotNull();
        assertThat(restrictedTimeRange.getFrom()).isEqualTo(from);
        assertThat(restrictedTimeRange.getTo()).isEqualTo(to);
    }

    @Test
    public void restrictTimeRangeReturnsGivenTimeRangeIfNoLimitHasBeenSet() {
        Mockito.when(clusterConfigService.get(SearchesClusterConfig.class)).thenReturn(SearchesClusterConfig.createDefault().toBuilder().queryTimeRangeLimit(ZERO).build());
        final SearchResource resource = new SearchResource(searches, clusterConfigService, decoratorProcessor) {};
        final DateTime from = new DateTime(2015, 1, 15, 12, 0, DateTimeZone.UTC);
        final DateTime to = from.plusYears(1);
        final TimeRange timeRange = AbsoluteRange.create(from, to);
        final TimeRange restrictedTimeRange = resource.restrictTimeRange(timeRange);
        assertThat(restrictedTimeRange).isNotNull();
        assertThat(restrictedTimeRange.getFrom()).isEqualTo(from);
        assertThat(restrictedTimeRange.getTo()).isEqualTo(to);
    }

    @Test
    public void restrictTimeRangeReturnsLimitedTimeRange() {
        Mockito.when(clusterConfigService.get(SearchesClusterConfig.class)).thenReturn(SearchesClusterConfig.createDefault().toBuilder().queryTimeRangeLimit(queryLimitPeriod).build());
        final DateTime from = new DateTime(2015, 1, 15, 12, 0, DateTimeZone.UTC);
        final DateTime to = from.plus(queryLimitPeriod.multipliedBy(2));
        final TimeRange timeRange = AbsoluteRange.create(from, to);
        final TimeRange restrictedTimeRange = searchResource.restrictTimeRange(timeRange);
        assertThat(restrictedTimeRange).isNotNull();
        assertThat(restrictedTimeRange.getFrom()).isEqualTo(to.minus(queryLimitPeriod));
        assertThat(restrictedTimeRange.getTo()).isEqualTo(to);
    }
}

