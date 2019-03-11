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
package io.confluent.ksql.function.udaf.min;


import org.apache.kafka.streams.kstream.Merger;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public class IntegerMinKudafTest {
    @Test
    public void shouldFindCorrectMin() {
        final IntegerMinKudaf integerMinKudaf = getIntegerMinKudaf();
        final int[] values = new int[]{ 3, 5, 8, 2, 3, 4, 5 };
        int currentMin = Integer.MAX_VALUE;
        for (final int i : values) {
            currentMin = integerMinKudaf.aggregate(i, currentMin);
        }
        Assert.assertThat(2, CoreMatchers.equalTo(currentMin));
    }

    @Test
    public void shouldHandleNull() {
        final IntegerMinKudaf integerMinKudaf = getIntegerMinKudaf();
        final int[] values = new int[]{ 3, 5, 8, 2, 3, 4, 5 };
        int currentMin = Integer.MAX_VALUE;
        // aggregate null before any aggregation
        currentMin = integerMinKudaf.aggregate(null, currentMin);
        Assert.assertThat(Integer.MAX_VALUE, CoreMatchers.equalTo(currentMin));
        // now send each value to aggregation and verify
        for (final int i : values) {
            currentMin = integerMinKudaf.aggregate(i, currentMin);
        }
        Assert.assertThat(2, CoreMatchers.equalTo(currentMin));
        // null should not impact result
        currentMin = integerMinKudaf.aggregate(null, currentMin);
        Assert.assertThat(2, CoreMatchers.equalTo(currentMin));
    }

    @Test
    public void shouldFindCorrectMinForMerge() {
        final IntegerMinKudaf integerMinKudaf = getIntegerMinKudaf();
        final Merger<String, Integer> merger = integerMinKudaf.getMerger();
        final Integer mergeResult1 = merger.apply("Key", 10, 12);
        Assert.assertThat(mergeResult1, CoreMatchers.equalTo(10));
        final Integer mergeResult2 = merger.apply("Key", 10, (-12));
        Assert.assertThat(mergeResult2, CoreMatchers.equalTo((-12)));
        final Integer mergeResult3 = merger.apply("Key", (-10), 0);
        Assert.assertThat(mergeResult3, CoreMatchers.equalTo((-10)));
    }
}

