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
package io.confluent.ksql.function.udaf.max;


import org.apache.kafka.streams.kstream.Merger;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public class IntegerMaxKudafTest {
    @Test
    public void shouldFindCorrectMax() {
        final IntegerMaxKudaf integerMaxKudaf = getIntegerMaxKudaf();
        final int[] values = new int[]{ 3, 5, 8, 2, 3, 4, 5 };
        int currentMax = Integer.MIN_VALUE;
        for (final int i : values) {
            currentMax = integerMaxKudaf.aggregate(i, currentMax);
        }
        Assert.assertThat(8, CoreMatchers.equalTo(currentMax));
    }

    @Test
    public void shouldHandleNull() {
        final IntegerMaxKudaf integerMaxKudaf = getIntegerMaxKudaf();
        final int[] values = new int[]{ 3, 5, 8, 2, 3, 4, 5 };
        int currentMax = Integer.MIN_VALUE;
        // null before any aggregation
        currentMax = integerMaxKudaf.aggregate(null, currentMax);
        Assert.assertThat(Integer.MIN_VALUE, CoreMatchers.equalTo(currentMax));
        // now send each value to aggregation and verify
        for (final int i : values) {
            currentMax = integerMaxKudaf.aggregate(i, currentMax);
        }
        Assert.assertThat(8, CoreMatchers.equalTo(currentMax));
        // null should not impact result
        currentMax = integerMaxKudaf.aggregate(null, currentMax);
        Assert.assertThat(8, CoreMatchers.equalTo(currentMax));
    }

    @Test
    public void shouldFindCorrectMaxForMerge() {
        final IntegerMaxKudaf integerMaxKudaf = getIntegerMaxKudaf();
        final Merger<String, Integer> merger = integerMaxKudaf.getMerger();
        final Integer mergeResult1 = merger.apply("Key", 10, 12);
        Assert.assertThat(mergeResult1, CoreMatchers.equalTo(12));
        final Integer mergeResult2 = merger.apply("Key", 10, (-12));
        Assert.assertThat(mergeResult2, CoreMatchers.equalTo(10));
        final Integer mergeResult3 = merger.apply("Key", (-10), 0);
        Assert.assertThat(mergeResult3, CoreMatchers.equalTo(0));
    }
}

