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
package io.confluent.ksql.function.udaf.sum;


import io.confluent.ksql.function.TableAggregationFunction;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.apache.kafka.streams.kstream.Merger;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;


@Ignore
public abstract class BaseSumKudafTest<T extends Number, AT extends TableAggregationFunction<T, T>> {
    protected interface TGenerator<TG> {
        TG fromInt(Integer s);
    }

    @Test
    public void shouldComputeCorrectSum() {
        final BaseSumKudafTest.TGenerator<T> tGenerator = getTGenerator();
        final AT sumKudaf = getSumKudaf();
        T currentVal = tGenerator.fromInt(0);
        final List<T> values = Stream.of(3, 5, 8, 2, 3, 4, 5).map(tGenerator::fromInt).collect(Collectors.toList());
        for (final T i : values) {
            currentVal = aggregate(i, currentVal);
        }
        Assert.assertThat(tGenerator.fromInt(30), CoreMatchers.equalTo(currentVal));
    }

    @Test
    public void shouldHandleNullsInSum() {
        final BaseSumKudafTest.TGenerator<T> tGenerator = getTGenerator();
        final AT sumKudaf = getSumKudaf();
        T currentVal = tGenerator.fromInt(0);
        final List<T> values = Stream.of(3, null, 8, 2, 3, 4, 5).map(tGenerator::fromInt).collect(Collectors.toList());
        for (final T i : values) {
            currentVal = aggregate(i, currentVal);
        }
        Assert.assertThat(tGenerator.fromInt(25), CoreMatchers.equalTo(currentVal));
    }

    @Test
    public void shouldComputeCorrectSubtraction() {
        final BaseSumKudafTest.TGenerator<T> tGenerator = getTGenerator();
        final AT sumKudaf = getSumKudaf();
        T currentVal = tGenerator.fromInt(30);
        final List<T> values = Stream.of(3, 5, 8, 2, 3, 4, 5).map(tGenerator::fromInt).collect(Collectors.toList());
        for (final T i : values) {
            currentVal = undo(i, currentVal);
        }
        Assert.assertThat(tGenerator.fromInt(0), CoreMatchers.equalTo(currentVal));
    }

    @Test
    public void shouldComputeCorrectSumMerge() {
        final BaseSumKudafTest.TGenerator<T> tGenerator = getTGenerator();
        final AT sumKudaf = getSumKudaf();
        final Merger<String, T> merger = getMerger();
        final T mergeResult1 = merger.apply("key", tGenerator.fromInt(10), tGenerator.fromInt(12));
        Assert.assertThat(mergeResult1, CoreMatchers.equalTo(tGenerator.fromInt(22)));
        final T mergeResult2 = merger.apply("key", tGenerator.fromInt(10), tGenerator.fromInt((-12)));
        Assert.assertThat(mergeResult2, CoreMatchers.equalTo(tGenerator.fromInt((-2))));
        final T mergeResult3 = merger.apply("key", tGenerator.fromInt((-10)), tGenerator.fromInt(0));
        Assert.assertThat(mergeResult3, CoreMatchers.equalTo(tGenerator.fromInt((-10))));
    }
}

