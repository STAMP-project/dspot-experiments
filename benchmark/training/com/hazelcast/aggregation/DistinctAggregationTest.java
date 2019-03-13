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
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;


@RunWith(HazelcastParallelClassRunner.class)
@Category({ QuickTest.class, ParallelTest.class })
public class DistinctAggregationTest {
    private final InternalSerializationService ss = new DefaultSerializationServiceBuilder().build();

    @Test(timeout = TimeoutInMillis.MINUTE)
    public void testCountAggregator() {
        List<String> values = repeatTimes(3, TestSamples.sampleStrings());
        Set<String> expectation = new HashSet<String>(values);
        Aggregator<Map.Entry<String, String>, Set<String>> aggregation = Aggregators.distinct();
        for (String value : values) {
            aggregation.accumulate(TestSamples.createEntryWithValue(value));
        }
        Aggregator<Map.Entry<String, String>, Set<String>> resultAggregation = Aggregators.distinct();
        resultAggregation.combine(aggregation);
        Set<String> result = resultAggregation.aggregate();
        Assert.assertThat(result, Matchers.is(Matchers.equalTo(expectation)));
    }

    @Test(timeout = TimeoutInMillis.MINUTE)
    public void testCountAggregator_withNull() {
        List<String> values = repeatTimes(3, TestSamples.sampleStrings());
        values.add(null);
        values.add(null);
        Set<String> expectation = new HashSet<String>(values);
        Aggregator<Map.Entry<String, String>, Set<String>> aggregation = Aggregators.distinct();
        for (String value : values) {
            aggregation.accumulate(TestSamples.createEntryWithValue(value));
        }
        Aggregator<Map.Entry<String, String>, Set<String>> resultAggregation = Aggregators.distinct();
        resultAggregation.combine(aggregation);
        Set<String> result = resultAggregation.aggregate();
        Assert.assertThat(result, Matchers.is(Matchers.equalTo(expectation)));
    }

    @Test(timeout = TimeoutInMillis.MINUTE)
    public void testCountAggregator_withAttributePath() {
        Person[] people = new Person[]{ new Person(5.1), new Person(3.3) };
        Double[] ages = new Double[]{ 5.1, 3.3 };
        List<Person> values = repeatTimes(3, Arrays.asList(people));
        Set<Double> expectation = new HashSet<Double>(Arrays.asList(ages));
        Aggregator<Map.Entry<Person, Person>, Set<Double>> aggregation = Aggregators.distinct("age");
        for (Person value : values) {
            aggregation.accumulate(TestSamples.createExtractableEntryWithValue(value, ss));
        }
        Aggregator<Map.Entry<Person, Person>, Set<Double>> resultAggregation = Aggregators.distinct("age");
        resultAggregation.combine(aggregation);
        Set<Double> result = resultAggregation.aggregate();
        Assert.assertThat(result, Matchers.is(Matchers.equalTo(expectation)));
    }

    @Test(timeout = TimeoutInMillis.MINUTE)
    public void testCountAggregator_withAttributePath_withNull() {
        Person[] people = new Person[]{ new Person(5.1), new Person(null) };
        Double[] ages = new Double[]{ 5.1, null };
        List<Person> values = repeatTimes(3, Arrays.asList(people));
        Set<Double> expectation = new HashSet<Double>(Arrays.asList(ages));
        Aggregator<Map.Entry<Person, Person>, Set<Double>> aggregation = Aggregators.distinct("age");
        for (Person value : values) {
            aggregation.accumulate(TestSamples.createExtractableEntryWithValue(value, ss));
        }
        Aggregator<Map.Entry<Person, Person>, Set<Double>> resultAggregation = Aggregators.distinct("age");
        resultAggregation.combine(aggregation);
        Set<Double> result = resultAggregation.aggregate();
        Assert.assertThat(result, Matchers.is(Matchers.equalTo(expectation)));
    }
}

