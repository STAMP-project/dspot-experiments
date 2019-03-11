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
package io.confluent.ksql.function.udaf.topkdistinct;


import Schema.OPTIONAL_INT32_SCHEMA;
import com.google.common.collect.ImmutableList;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.BinaryOperator;
import java.util.function.IntFunction;
import java.util.stream.IntStream;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public class IntTopkDistinctKudafTest {
    private final List<Integer> valuesArray = ImmutableList.of(10, 30, 45, 10, 50, 60, 20, 60, 80, 35, 25, 60, 80);

    private final TopkDistinctKudaf<Integer> intTopkDistinctKudaf = TopKDistinctTestUtils.getTopKDistinctKudaf(3, OPTIONAL_INT32_SCHEMA);

    @Test
    public void shouldAggregateTopK() {
        List<Integer> currentVal = new ArrayList<>();
        for (final Integer d : valuesArray) {
            currentVal = intTopkDistinctKudaf.aggregate(d, currentVal);
        }
        Assert.assertThat("Invalid results.", currentVal, CoreMatchers.equalTo(new ArrayList<Integer>(ImmutableList.of(80, 60, 50))));
    }

    @Test
    public void shouldAggregateTopKWithLessThanKValues() {
        List<Integer> currentVal = new ArrayList<>();
        currentVal = intTopkDistinctKudaf.aggregate(80, currentVal);
        Assert.assertThat("Invalid results.", currentVal, CoreMatchers.equalTo(ImmutableList.of(80)));
    }

    @Test
    public void shouldMergeTopK() {
        final List<Integer> array1 = ImmutableList.of(50, 45, 25);
        final List<Integer> array2 = ImmutableList.of(60, 50, 48);
        Assert.assertThat("Invalid results.", intTopkDistinctKudaf.getMerger().apply("key", array1, array2), CoreMatchers.equalTo(ImmutableList.of(60, 50, 48)));
    }

    @Test
    public void shouldMergeTopKWithNulls() {
        final List<Integer> array1 = ImmutableList.of(50, 45);
        final List<Integer> array2 = ImmutableList.of(60);
        Assert.assertThat("Invalid results.", intTopkDistinctKudaf.getMerger().apply("key", array1, array2), CoreMatchers.equalTo(ImmutableList.of(60, 50, 45)));
    }

    @Test
    public void shouldMergeTopKWithNullsDuplicates() {
        final List<Integer> array1 = ImmutableList.of(50, 45);
        final List<Integer> array2 = ImmutableList.of(60, 50);
        Assert.assertThat("Invalid results.", intTopkDistinctKudaf.getMerger().apply("key", array1, array2), CoreMatchers.equalTo(ImmutableList.of(60, 50, 45)));
    }

    @Test
    public void shouldMergeTopKWithMoreNulls() {
        final List<Integer> array1 = ImmutableList.of(60);
        final List<Integer> array2 = ImmutableList.of(60);
        Assert.assertThat("Invalid results.", intTopkDistinctKudaf.getMerger().apply("key", array1, array2), CoreMatchers.equalTo(new ArrayList<Integer>(ImmutableList.of(60))));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void shouldAggregateAndProducedOrderedTopK() {
        final List<Integer> aggregate = intTopkDistinctKudaf.aggregate(1, new ArrayList());
        Assert.assertThat(aggregate, CoreMatchers.equalTo(Collections.singletonList(1)));
        final List<Integer> agg2 = intTopkDistinctKudaf.aggregate(100, aggregate);
        Assert.assertThat(agg2, CoreMatchers.equalTo(ImmutableList.of(100, 1)));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void shouldBeThreadSafe() {
        // Given:
        final TopkDistinctKudaf<Integer> intTopkDistinctKudaf = TopKDistinctTestUtils.getTopKDistinctKudaf(12, OPTIONAL_INT32_SCHEMA);
        final List<Integer> values = ImmutableList.of(10, 30, 45, 10, 50, 60, 20, 70, 80, 35, 25);
        // When:
        final List<Integer> result = IntStream.range(0, 4).parallel().mapToObj(( threadNum) -> {
            List<Integer> aggregate = Arrays.asList(1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1);
            for (int value : values) {
                aggregate = intTopkDistinctKudaf.aggregate((value + threadNum), aggregate);
            }
            return aggregate;
        }).reduce(( agg1, agg2) -> intTopkDistinctKudaf.getMerger().apply("blah", agg1, agg2)).orElse(new ArrayList<>());
        // Then:
        Assert.assertThat(result, CoreMatchers.is(ImmutableList.of(83, 82, 81, 80, 73, 72, 71, 70, 63, 62, 61, 60)));
    }
}

