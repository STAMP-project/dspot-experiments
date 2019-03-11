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


import Searches.DateHistogramInterval;
import Searches.DateHistogramInterval.MINUTE;
import io.searchbox.core.search.aggregation.HistogramAggregation;
import java.util.Collections;
import org.junit.Test;
import org.mockito.Mockito;


public class FieldHistogramResultTest {
    @Test
    public void testGetInterval() throws Exception {
        final FieldHistogramResult fieldHistogramResult = new FieldHistogramResult(Mockito.mock(HistogramAggregation.class), "", "", DateHistogramInterval.MINUTE, 42L);
        assertThat(fieldHistogramResult.getInterval()).isEqualTo(MINUTE);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void getResultsWorksWithZeroBuckets() throws Exception {
        final HistogramAggregation dateHistogram = Mockito.mock(HistogramAggregation.class);
        Mockito.when(dateHistogram.getBuckets()).thenReturn(Collections.emptyList());
        final FieldHistogramResult fieldHistogramResult = new FieldHistogramResult(dateHistogram, "", "", DateHistogramInterval.MINUTE, 42L);
        assertThat(fieldHistogramResult.getResults()).isEmpty();
    }
}

