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
package io.confluent.ksql.function.udaf.count;


import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public class CountKudafTest {
    @Test
    public void shouldGetCorrectCount() {
        final CountKudaf doubleCountKudaf = getDoubleCountKudaf();
        final double[] values = new double[]{ 3.0, 5.0, 8.0, 2.2, 3.5, 4.6, 5.0 };
        Long currentCount = 0L;
        for (final double i : values) {
            currentCount = doubleCountKudaf.aggregate(i, currentCount);
        }
        Assert.assertThat(7L, CoreMatchers.equalTo(currentCount));
    }

    @Test
    public void shouldHandleNullCount() {
        final CountKudaf doubleCountKudaf = getDoubleCountKudaf();
        final double[] values = new double[]{ 3.0, 5.0, 8.0, 2.2, 3.5, 4.6, 5.0 };
        Long currentCount = 0L;
        // aggregate null before any aggregation
        currentCount = doubleCountKudaf.aggregate(null, currentCount);
        Assert.assertThat(0L, CoreMatchers.equalTo(currentCount));
        // now send each value to aggregation and verify
        for (final double i : values) {
            currentCount = doubleCountKudaf.aggregate(i, currentCount);
        }
        Assert.assertThat(7L, CoreMatchers.equalTo(currentCount));
        // null should not affect count
        currentCount = doubleCountKudaf.aggregate(null, currentCount);
        Assert.assertThat(7L, CoreMatchers.equalTo(currentCount));
    }

    @Test
    public void shouldUndoElement() {
        final CountKudaf doubleCountKudaf = getDoubleCountKudaf();
        final double[] values = new double[]{ 3.0, 5.0, 8.0, 2.2, 3.5, 4.6, 5.0 };
        Long currentCount = 0L;
        for (final double i : values) {
            currentCount = doubleCountKudaf.aggregate(i, currentCount);
        }
        Assert.assertThat(7L, CoreMatchers.equalTo(currentCount));
        currentCount = doubleCountKudaf.undo(3.0, currentCount);
        Assert.assertThat(6L, CoreMatchers.equalTo(currentCount));
    }

    @Test
    public void shouldUndoElementHandleNull() {
        final CountKudaf doubleCountKudaf = getDoubleCountKudaf();
        final double[] values = new double[]{ 3.0, 5.0, 8.0, 2.2, 3.5, 4.6, 5.0 };
        Long currentCount = 0L;
        for (final double i : values) {
            currentCount = doubleCountKudaf.aggregate(i, currentCount);
        }
        Assert.assertThat(7L, CoreMatchers.equalTo(currentCount));
        currentCount = doubleCountKudaf.undo(null, currentCount);
        Assert.assertThat(7L, CoreMatchers.equalTo(currentCount));
    }
}

