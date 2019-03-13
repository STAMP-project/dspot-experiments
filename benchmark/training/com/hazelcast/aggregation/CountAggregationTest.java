/**
 * Copyright (c) 2008-2019, Hazelcast, Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.hazelcast.aggregation;


import com.hazelcast.internal.serialization.InternalSerializationService;
import com.hazelcast.internal.serialization.impl.DefaultSerializationServiceBuilder;
import com.hazelcast.test.HazelcastParallelClassRunner;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;


@RunWith(HazelcastParallelClassRunner.class)
@Category({ QuickTest.class, ParallelTest.class })
public class CountAggregationTest {
    private final InternalSerializationService ss = new DefaultSerializationServiceBuilder().build();

    @Test(timeout = TimeoutInMillis.MINUTE)
    public void testCountAggregator() {
        List<BigDecimal> values = TestSamples.sampleBigDecimals();
        long expectation = values.size();
        Aggregator<Map.Entry<BigDecimal, BigDecimal>, Long> aggregation = Aggregators.count();
        for (BigDecimal value : values) {
            aggregation.accumulate(TestSamples.createEntryWithValue(value));
        }
        Aggregator<Map.Entry<BigDecimal, BigDecimal>, Long> resultAggregation = Aggregators.count();
        resultAggregation.combine(aggregation);
        long result = resultAggregation.aggregate();
        Assert.assertThat(result, Matchers.is(Matchers.equalTo(expectation)));
    }

    @Test(timeout = TimeoutInMillis.MINUTE)
    public void testCountAggregator_withAttributePath() {
        List<Person> values = TestSamples.samplePersons();
        long expectation = values.size();
        Aggregator<Map.Entry<Person, Person>, Long> aggregation = Aggregators.count("age");
        for (Person person : values) {
            aggregation.accumulate(TestSamples.createExtractableEntryWithValue(person, ss));
        }
        Aggregator<Map.Entry<BigDecimal, BigDecimal>, Long> resultAggregation = Aggregators.count("age");
        resultAggregation.combine(aggregation);
        long result = resultAggregation.aggregate();
        Assert.assertThat(result, Matchers.is(Matchers.equalTo(expectation)));
    }

    @Test(timeout = TimeoutInMillis.MINUTE)
    public void testCountAggregator_withNull() {
        List<BigDecimal> values = TestSamples.sampleBigDecimals();
        values.add(null);
        long expectation = values.size();
        Aggregator<Map.Entry<BigDecimal, BigDecimal>, Long> aggregation = Aggregators.count();
        for (BigDecimal value : values) {
            aggregation.accumulate(TestSamples.createEntryWithValue(value));
        }
        Aggregator<Map.Entry<BigDecimal, BigDecimal>, Long> resultAggregation = Aggregators.count();
        resultAggregation.combine(aggregation);
        long result = resultAggregation.aggregate();
        Assert.assertThat(result, Matchers.is(Matchers.equalTo(expectation)));
    }

    @Test(timeout = TimeoutInMillis.MINUTE)
    public void testCountAggregator_withAttributePath_withNull() {
        List<Person> values = TestSamples.samplePersons();
        values.add(null);
        long expectation = values.size();
        Aggregator<Map.Entry<Person, Person>, Long> aggregation = Aggregators.count("age");
        for (Person person : values) {
            aggregation.accumulate(TestSamples.createExtractableEntryWithValue(person, ss));
        }
        Aggregator<Map.Entry<BigDecimal, BigDecimal>, Long> resultAggregation = Aggregators.count("age");
        resultAggregation.combine(aggregation);
        long result = resultAggregation.aggregate();
        Assert.assertThat(result, Matchers.is(Matchers.equalTo(expectation)));
    }
}

