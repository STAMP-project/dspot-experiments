/**
 * Copyright 2016 KairosDB Authors
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package org.kairosdb.core.aggregator;


import java.util.Random;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.kairosdb.core.DataPoint;
import org.kairosdb.core.datapoints.DoubleDataPointFactoryImpl;
import org.kairosdb.core.datapoints.LongDataPoint;
import org.kairosdb.core.datastore.DataPointGroup;
import org.kairosdb.testing.ListDataPointGroup;


public class StdAggregatorTest {
    @Test
    public void test() {
        ListDataPointGroup group = new ListDataPointGroup("group");
        for (int i = 0; i < 10000; i++) {
            group.addDataPoint(new LongDataPoint(1, i));
        }
        StdAggregator aggregator = new StdAggregator(new DoubleDataPointFactoryImpl());
        DataPointGroup dataPointGroup = aggregator.aggregate(group);
        DataPoint stdev = dataPointGroup.next();
        MatcherAssert.assertThat(stdev.getDoubleValue(), Matchers.closeTo(2886.462, 0.44));
    }

    @Test
    public void test_random() {
        long seed = System.nanoTime();
        Random random = new Random(seed);
        long[] values = new long[1000];
        ListDataPointGroup group = new ListDataPointGroup("group");
        for (int i = 0; i < (values.length); i++) {
            long randomValue = random.nextLong();
            group.addDataPoint(new LongDataPoint(1, randomValue));
            values[i] = randomValue;
        }
        StdAggregator aggregator = new StdAggregator(new DoubleDataPointFactoryImpl());
        DataPointGroup dataPointGroup = aggregator.aggregate(group);
        DataPoint stdev = dataPointGroup.next();
        double expected = StdAggregatorTest.naiveStdDev(values);
        double epsilon = 0.001 * expected;
        MatcherAssert.assertThat(stdev.getDoubleValue(), Matchers.closeTo(expected, epsilon));
    }
}

