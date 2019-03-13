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


public class LongMaxKudafTest {
    @Test
    public void shouldFindCorrectMax() {
        final LongMaxKudaf longMaxKudaf = getLongMaxKudaf();
        final long[] values = new long[]{ 3L, 5L, 8L, 2L, 3L, 4L, 5L };
        long currentMax = Long.MIN_VALUE;
        for (final long i : values) {
            currentMax = longMaxKudaf.aggregate(i, currentMax);
        }
        Assert.assertThat(8L, CoreMatchers.equalTo(currentMax));
    }

    @Test
    public void shouldHandleNull() {
        final LongMaxKudaf longMaxKudaf = getLongMaxKudaf();
        final long[] values = new long[]{ 3L, 5L, 8L, 2L, 3L, 4L, 5L };
        long currentMax = Long.MIN_VALUE;
        // null before any aggregation
        currentMax = longMaxKudaf.aggregate(null, currentMax);
        Assert.assertThat(Long.MIN_VALUE, CoreMatchers.equalTo(currentMax));
        // now send each value to aggregation and verify
        for (final long i : values) {
            currentMax = longMaxKudaf.aggregate(i, currentMax);
        }
        Assert.assertThat(8L, CoreMatchers.equalTo(currentMax));
        // null should not impact result
        currentMax = longMaxKudaf.aggregate(null, currentMax);
        Assert.assertThat(8L, CoreMatchers.equalTo(currentMax));
    }

    @Test
    public void shouldFindCorrectMaxForMerge() {
        final LongMaxKudaf longMaxKudaf = getLongMaxKudaf();
        final Merger<String, Long> merger = longMaxKudaf.getMerger();
        final Long mergeResult1 = merger.apply("Key", 10L, 12L);
        Assert.assertThat(mergeResult1, CoreMatchers.equalTo(12L));
        final Long mergeResult2 = merger.apply("Key", 10L, (-12L));
        Assert.assertThat(mergeResult2, CoreMatchers.equalTo(10L));
        final Long mergeResult3 = merger.apply("Key", (-10L), 0L);
        Assert.assertThat(mergeResult3, CoreMatchers.equalTo(0L));
    }
}

