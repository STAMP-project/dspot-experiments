/**
 * Copyright 2018 Confluent Inc.
 *
 * Licensed under the Confluent Community License (the "License"); you may not use
 * this file except in compliance with the License.  You may obtain a copy of the
 * License at
 *
 * http://www.confluent.io/confluent-community-license
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OF ANY KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations under the License.
 */
package io.confluent.ksql.function.udaf.map;


import io.confluent.ksql.function.udaf.TableUdaf;
import java.util.Map;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


public class HistogramUdafTest {
    @Test
    public void shouldCountStrings() {
        final TableUdaf<String, Map<String, Long>> udaf = HistogramUdaf.histogramString();
        Map<String, Long> agg = udaf.initialize();
        final String[] values = new String[]{ "foo", "bar", "foo", "foo", "baz" };
        for (final String thisValue : values) {
            agg = udaf.aggregate(thisValue, agg);
        }
        Assert.assertThat(agg.entrySet(), Matchers.hasSize(3));
        Assert.assertThat(agg, Matchers.hasEntry("foo", 3L));
        Assert.assertThat(agg, Matchers.hasEntry("bar", 1L));
        Assert.assertThat(agg, Matchers.hasEntry("baz", 1L));
    }

    @Test
    public void shouldMergeCountsIncludingNulls() {
        final TableUdaf<String, Map<String, Long>> udaf = HistogramUdaf.histogramString();
        Map<String, Long> lhs = udaf.initialize();
        final Integer[] leftValues = new Integer[]{ 1, 2, 1, 4 };
        for (final Integer thisValue : leftValues) {
            lhs = udaf.aggregate(String.valueOf(thisValue), lhs);
        }
        lhs = udaf.aggregate(null, lhs);
        Assert.assertThat(lhs.entrySet(), Matchers.hasSize(4));
        Assert.assertThat(lhs, Matchers.hasEntry("1", 2L));
        Assert.assertThat(lhs, Matchers.hasEntry("2", 1L));
        Assert.assertThat(lhs, Matchers.hasEntry("4", 1L));
        Assert.assertThat(lhs, Matchers.hasEntry(null, 1L));
        Map<String, Long> rhs = udaf.initialize();
        final Integer[] rightValues = new Integer[]{ 1, 3 };
        for (final Integer thisValue : rightValues) {
            rhs = udaf.aggregate(String.valueOf(thisValue), rhs);
        }
        rhs = udaf.aggregate(null, rhs);
        rhs = udaf.aggregate(null, rhs);
        Assert.assertThat(rhs.entrySet(), Matchers.hasSize(3));
        Assert.assertThat(rhs, Matchers.hasEntry("1", 1L));
        Assert.assertThat(rhs, Matchers.hasEntry("3", 1L));
        Assert.assertThat(rhs, Matchers.hasEntry(null, 2L));
        final Map<String, Long> merged = udaf.merge(lhs, rhs);
        Assert.assertThat(merged.entrySet(), Matchers.hasSize(5));
        Assert.assertThat(merged, Matchers.hasEntry("1", 3L));
        Assert.assertThat(merged, Matchers.hasEntry("2", 1L));
        Assert.assertThat(merged, Matchers.hasEntry("3", 1L));
        Assert.assertThat(merged, Matchers.hasEntry("4", 1L));
        Assert.assertThat(merged, Matchers.hasEntry(null, 3L));
    }

    @Test
    public void shouldUndoCountedValues() {
        final TableUdaf<String, Map<String, Long>> udaf = HistogramUdaf.histogramString();
        Map<String, Long> agg = udaf.initialize();
        final Boolean[] values = new Boolean[]{ true, true, false, null, true };
        for (final Boolean thisValue : values) {
            agg = udaf.aggregate(String.valueOf(thisValue), agg);
        }
        Assert.assertThat(agg.entrySet(), Matchers.hasSize(3));
        Assert.assertThat(agg, Matchers.hasEntry("true", 3L));
        Assert.assertThat(agg, Matchers.hasEntry("false", 1L));
        Assert.assertThat(agg, Matchers.hasEntry("null", 1L));
        agg = udaf.undo("true", agg);
        Assert.assertThat(agg.entrySet(), Matchers.hasSize(3));
        Assert.assertThat(agg, Matchers.hasEntry("true", 2L));
        Assert.assertThat(agg, Matchers.hasEntry("false", 1L));
        Assert.assertThat(agg, Matchers.hasEntry("null", 1L));
    }

    @Test
    public void shouldNotExceedSizeLimit() {
        final TableUdaf<String, Map<String, Long>> udaf = HistogramUdaf.histogramString();
        Map<String, Long> agg = udaf.initialize();
        for (int thisValue = 1; thisValue < 2500; thisValue++) {
            agg = udaf.aggregate(String.valueOf(thisValue), agg);
        }
        Assert.assertThat(agg.entrySet(), Matchers.hasSize(1000));
        Assert.assertThat(agg, Matchers.hasEntry("1", 1L));
        Assert.assertThat(agg, Matchers.hasEntry("1000", 1L));
        Assert.assertThat(agg, Matchers.not(Matchers.hasEntry("1001", 1L)));
    }
}

