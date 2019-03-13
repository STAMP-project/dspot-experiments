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
package io.confluent.ksql.function.udaf.topk;


import Schema.OPTIONAL_INT32_SCHEMA;
import com.google.common.collect.ImmutableList;
import io.confluent.ksql.function.KsqlAggregateFunction;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.function.BinaryOperator;
import java.util.function.IntFunction;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public class IntTopkKudafTest {
    private final List<Integer> valuesArray = ImmutableList.of(10, 30, 45, 10, 50, 60, 20, 60, 80, 35, 25);

    private KsqlAggregateFunction<Integer, List<Integer>> topkKudaf;

    @Test
    public void shouldAggregateTopK() {
        List<Integer> currentVal = new ArrayList<>();
        for (final Integer value : valuesArray) {
            currentVal = topkKudaf.aggregate(value, currentVal);
        }
        Assert.assertThat("Invalid results.", currentVal, CoreMatchers.equalTo(ImmutableList.of(80, 60, 60)));
    }

    @Test
    public void shouldAggregateTopKWithLessThanKValues() {
        List<Integer> currentVal = new ArrayList<>();
        currentVal = topkKudaf.aggregate(10, currentVal);
        Assert.assertThat("Invalid results.", currentVal, CoreMatchers.equalTo(ImmutableList.of(10)));
    }

    @Test
    public void shouldMergeTopK() {
        final List<Integer> array1 = ImmutableList.of(50, 45, 25);
        final List<Integer> array2 = ImmutableList.of(60, 55, 48);
        Assert.assertThat("Invalid results.", topkKudaf.getMerger().apply("key", array1, array2), CoreMatchers.equalTo(ImmutableList.of(60, 55, 50)));
    }

    @Test
    public void shouldMergeTopKWithNulls() {
        final List<Integer> array1 = ImmutableList.of(50, 45);
        final List<Integer> array2 = ImmutableList.of(60);
        Assert.assertThat("Invalid results.", topkKudaf.getMerger().apply("key", array1, array2), CoreMatchers.equalTo(ImmutableList.of(60, 50, 45)));
    }

    @Test
    public void shouldMergeTopKWithMoreNulls() {
        final List<Integer> array1 = ImmutableList.of(50);
        final List<Integer> array2 = ImmutableList.of(60);
        Assert.assertThat("Invalid results.", topkKudaf.getMerger().apply("key", array1, array2), CoreMatchers.equalTo(ImmutableList.of(60, 50)));
    }

    @Test
    public void shouldAggregateAndProducedOrderedTopK() {
        final List<Integer> aggregate = topkKudaf.aggregate(1, new ArrayList());
        Assert.assertThat(aggregate, CoreMatchers.equalTo(ImmutableList.of(1)));
        final List<Integer> agg2 = topkKudaf.aggregate(100, aggregate);
        Assert.assertThat(agg2, CoreMatchers.equalTo(ImmutableList.of(100, 1)));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void shouldWorkWithLargeValuesOfKay() {
        // Given:
        final int topKSize = 300;
        topkKudaf = new TopKAggregateFunctionFactory(topKSize).getProperAggregateFunction(Collections.singletonList(OPTIONAL_INT32_SCHEMA));
        final List<Integer> initialAggregate = IntStream.range(0, topKSize).boxed().collect(Collectors.toList());
        initialAggregate.sort(Comparator.reverseOrder());
        // When:
        final List<Integer> result = topkKudaf.aggregate(10, initialAggregate);
        final List<Integer> combined = topkKudaf.getMerger().apply("key", result, initialAggregate);
        // Then:
        Assert.assertThat(combined.get(0), CoreMatchers.is(299));
        Assert.assertThat(combined.get(1), CoreMatchers.is(299));
        Assert.assertThat(combined.get(2), CoreMatchers.is(298));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void shouldBeThreadSafe() {
        // Given:
        topkKudaf = new TopKAggregateFunctionFactory(12).getProperAggregateFunction(Collections.singletonList(OPTIONAL_INT32_SCHEMA));
        final List<Integer> values = ImmutableList.of(10, 30, 45, 10, 50, 60, 20, 70, 80, 35, 25);
        // When:
        final List<Integer> result = IntStream.range(0, 4).parallel().mapToObj(( threadNum) -> {
            List<Integer> aggregate = Arrays.asList(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0);
            for (int value : values) {
                aggregate = topkKudaf.aggregate((value + threadNum), aggregate);
            }
            return aggregate;
        }).reduce(( agg1, agg2) -> topkKudaf.getMerger().apply("blah", agg1, agg2)).orElse(new ArrayList<>());
        // Then:
        Assert.assertThat(result, CoreMatchers.is(ImmutableList.of(83, 82, 81, 80, 73, 72, 71, 70, 63, 62, 61, 60)));
    }
}

