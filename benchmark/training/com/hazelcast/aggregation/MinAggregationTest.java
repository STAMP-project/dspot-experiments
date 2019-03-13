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
import java.math.BigInteger;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;

import static com.hazelcast.aggregation.ValueContainer.ValueType.BIG_DECIMAL;
import static com.hazelcast.aggregation.ValueContainer.ValueType.BIG_INTEGER;
import static com.hazelcast.aggregation.ValueContainer.ValueType.DOUBLE;
import static com.hazelcast.aggregation.ValueContainer.ValueType.INTEGER;
import static com.hazelcast.aggregation.ValueContainer.ValueType.LONG;
import static com.hazelcast.aggregation.ValueContainer.ValueType.STRING;


@RunWith(HazelcastParallelClassRunner.class)
@Category({ QuickTest.class, ParallelTest.class })
public class MinAggregationTest {
    public static final double ERROR = 1.0E-8;

    private final InternalSerializationService ss = new DefaultSerializationServiceBuilder().build();

    @Test(timeout = TimeoutInMillis.MINUTE)
    public void testBigDecimalMin() {
        List<BigDecimal> values = TestSamples.sampleBigDecimals();
        Collections.sort(values);
        BigDecimal expectation = values.get(0);
        Aggregator<Map.Entry<BigDecimal, BigDecimal>, BigDecimal> aggregation = Aggregators.bigDecimalMin();
        for (BigDecimal value : values) {
            aggregation.accumulate(TestSamples.createEntryWithValue(value));
        }
        Aggregator<Map.Entry<BigDecimal, BigDecimal>, BigDecimal> resultAggregation = Aggregators.bigDecimalMin();
        resultAggregation.combine(aggregation);
        BigDecimal result = resultAggregation.aggregate();
        Assert.assertThat(result, Matchers.is(Matchers.equalTo(expectation)));
    }

    @Test(timeout = TimeoutInMillis.MINUTE)
    public void testBigDecimalMin_withAttributePath() {
        List<ValueContainer> values = TestSamples.sampleValueContainers(BIG_DECIMAL);
        Collections.sort(values);
        BigDecimal expectation = values.get(0).bigDecimal;
        Aggregator<Map.Entry<ValueContainer, ValueContainer>, BigDecimal> aggregation = Aggregators.bigDecimalMin("bigDecimal");
        for (ValueContainer value : values) {
            aggregation.accumulate(TestSamples.createExtractableEntryWithValue(value, ss));
        }
        Aggregator<Map.Entry<ValueContainer, ValueContainer>, BigDecimal> resultAggregation = Aggregators.bigDecimalMin("bigDecimal");
        resultAggregation.combine(aggregation);
        BigDecimal result = resultAggregation.aggregate();
        Assert.assertThat(result, Matchers.is(Matchers.equalTo(expectation)));
    }

    @Test(timeout = TimeoutInMillis.MINUTE)
    public void testBigIntegerMin() {
        List<BigInteger> values = TestSamples.sampleBigIntegers();
        Collections.sort(values);
        BigInteger expectation = values.get(0);
        Aggregator<Map.Entry<BigInteger, BigInteger>, BigInteger> aggregation = Aggregators.bigIntegerMin();
        for (BigInteger value : values) {
            aggregation.accumulate(TestSamples.createEntryWithValue(value));
        }
        Aggregator<Map.Entry<BigInteger, BigInteger>, BigInteger> resultAggregation = Aggregators.bigIntegerMin();
        resultAggregation.combine(aggregation);
        BigInteger result = resultAggregation.aggregate();
        Assert.assertThat(result, Matchers.is(Matchers.equalTo(expectation)));
    }

    @Test(timeout = TimeoutInMillis.MINUTE)
    public void testBigIntegerMin_withAttributePath() {
        List<ValueContainer> values = TestSamples.sampleValueContainers(BIG_INTEGER);
        Collections.sort(values);
        BigInteger expectation = values.get(0).bigInteger;
        Aggregator<Map.Entry<ValueContainer, ValueContainer>, BigInteger> aggregation = Aggregators.bigIntegerMin("bigInteger");
        for (ValueContainer value : values) {
            aggregation.accumulate(TestSamples.createExtractableEntryWithValue(value, ss));
        }
        Aggregator<Map.Entry<ValueContainer, ValueContainer>, BigInteger> resultAggregation = Aggregators.bigIntegerMin("bigInteger");
        resultAggregation.combine(aggregation);
        BigInteger result = resultAggregation.aggregate();
        Assert.assertThat(result, Matchers.is(Matchers.equalTo(expectation)));
    }

    @Test(timeout = TimeoutInMillis.MINUTE)
    public void testDoubleMin() {
        List<Double> values = TestSamples.sampleDoubles();
        Collections.sort(values);
        double expectation = values.get(0);
        Aggregator<Map.Entry<Double, Double>, Double> aggregation = Aggregators.doubleMin();
        for (Double value : values) {
            aggregation.accumulate(TestSamples.createEntryWithValue(value));
        }
        Aggregator<Map.Entry<Double, Double>, Double> resultAggregation = Aggregators.doubleMin();
        resultAggregation.combine(aggregation);
        Double result = resultAggregation.aggregate();
        Assert.assertThat(result, Matchers.is(Matchers.closeTo(expectation, MinAggregationTest.ERROR)));
    }

    @Test(timeout = TimeoutInMillis.MINUTE)
    public void testDoubleMin_withAttributePath() {
        List<ValueContainer> values = TestSamples.sampleValueContainers(DOUBLE);
        Collections.sort(values);
        double expectation = values.get(0).doubleValue;
        Aggregator<Map.Entry<ValueContainer, ValueContainer>, Double> aggregation = Aggregators.doubleMin("doubleValue");
        for (ValueContainer value : values) {
            aggregation.accumulate(TestSamples.createExtractableEntryWithValue(value, ss));
        }
        Aggregator<Map.Entry<ValueContainer, ValueContainer>, Double> resultAggregation = Aggregators.doubleMin("doubleValue");
        resultAggregation.combine(aggregation);
        double result = resultAggregation.aggregate();
        Assert.assertThat(result, Matchers.is(Matchers.equalTo(expectation)));
    }

    @Test(timeout = TimeoutInMillis.MINUTE)
    public void testIntegerMin() {
        List<Integer> values = TestSamples.sampleIntegers();
        Collections.sort(values);
        long expectation = values.get(0);
        Aggregator<Map.Entry<Integer, Integer>, Integer> aggregation = Aggregators.integerMin();
        for (Integer value : values) {
            aggregation.accumulate(TestSamples.createEntryWithValue(value));
        }
        Aggregator<Map.Entry<Integer, Integer>, Integer> resultAggregation = Aggregators.integerMin();
        resultAggregation.combine(aggregation);
        long result = resultAggregation.aggregate();
        Assert.assertThat(result, Matchers.is(Matchers.equalTo(expectation)));
    }

    @Test(timeout = TimeoutInMillis.MINUTE)
    public void testIntegerMin_withAttributePath() {
        List<ValueContainer> values = TestSamples.sampleValueContainers(INTEGER);
        Collections.sort(values);
        int expectation = values.get(0).intValue;
        Aggregator<Map.Entry<ValueContainer, ValueContainer>, Integer> aggregation = Aggregators.integerMin("intValue");
        for (ValueContainer value : values) {
            aggregation.accumulate(TestSamples.createExtractableEntryWithValue(value, ss));
        }
        Aggregator<Map.Entry<ValueContainer, ValueContainer>, Integer> resultAggregation = Aggregators.integerMin("intValue");
        resultAggregation.combine(aggregation);
        int result = resultAggregation.aggregate();
        Assert.assertThat(result, Matchers.is(Matchers.equalTo(expectation)));
    }

    @Test(timeout = TimeoutInMillis.MINUTE)
    public void testLongMin() {
        List<Long> values = TestSamples.sampleLongs();
        Collections.sort(values);
        long expectation = values.get(0);
        Aggregator<Map.Entry<Long, Long>, Long> aggregation = Aggregators.longMin();
        for (Long value : values) {
            aggregation.accumulate(TestSamples.createEntryWithValue(value));
        }
        Aggregator<Map.Entry<Long, Long>, Long> resultAggregation = Aggregators.longMin();
        resultAggregation.combine(aggregation);
        long result = resultAggregation.aggregate();
        Assert.assertThat(result, Matchers.is(Matchers.equalTo(expectation)));
    }

    @Test(timeout = TimeoutInMillis.MINUTE)
    public void testLongMin_withAttributePath() {
        List<ValueContainer> values = TestSamples.sampleValueContainers(LONG);
        Collections.sort(values);
        long expectation = values.get(0).longValue;
        Aggregator<Map.Entry<ValueContainer, ValueContainer>, Long> aggregation = Aggregators.longMin("longValue");
        for (ValueContainer value : values) {
            aggregation.accumulate(TestSamples.createExtractableEntryWithValue(value, ss));
        }
        Aggregator<Map.Entry<ValueContainer, ValueContainer>, Long> resultAggregation = Aggregators.longMin("longValue");
        resultAggregation.combine(aggregation);
        long result = resultAggregation.aggregate();
        Assert.assertThat(result, Matchers.is(Matchers.equalTo(expectation)));
    }

    @Test(timeout = TimeoutInMillis.MINUTE)
    public void testComparableMin() {
        List<String> values = TestSamples.sampleStrings();
        Collections.sort(values);
        String expectation = values.get(0);
        Aggregator<Map.Entry<String, String>, String> aggregation = Aggregators.comparableMin();
        for (String value : values) {
            aggregation.accumulate(TestSamples.createEntryWithValue(value));
        }
        Aggregator<Map.Entry<String, String>, String> resultAggregation = Aggregators.comparableMin();
        resultAggregation.combine(aggregation);
        String result = resultAggregation.aggregate();
        Assert.assertThat(result, Matchers.is(Matchers.equalTo(expectation)));
    }

    @Test(timeout = TimeoutInMillis.MINUTE)
    public void testComparableMin_withAttributePath() {
        List<ValueContainer> values = TestSamples.sampleValueContainers(STRING);
        Collections.sort(values);
        String expectation = values.get(0).stringValue;
        Aggregator<Map.Entry<ValueContainer, ValueContainer>, String> aggregation = Aggregators.comparableMin("stringValue");
        for (ValueContainer value : values) {
            aggregation.accumulate(TestSamples.createExtractableEntryWithValue(value, ss));
        }
        Aggregator<Map.Entry<ValueContainer, ValueContainer>, String> resultAggregation = Aggregators.comparableMin("stringValue");
        resultAggregation.combine(aggregation);
        String result = resultAggregation.aggregate();
        Assert.assertThat(result, Matchers.is(Matchers.equalTo(expectation)));
    }

    @Test(timeout = TimeoutInMillis.MINUTE)
    public void testComparableMin_withNull() {
        List<String> values = TestSamples.sampleStrings();
        Collections.sort(values);
        String expectation = values.get(0);
        values.add(null);
        Aggregator<Map.Entry<String, String>, String> aggregation = Aggregators.comparableMin();
        for (String value : values) {
            aggregation.accumulate(TestSamples.createEntryWithValue(value));
        }
        Aggregator<Map.Entry<String, String>, String> resultAggregation = Aggregators.comparableMin();
        resultAggregation.combine(aggregation);
        String result = resultAggregation.aggregate();
        Assert.assertThat(result, Matchers.is(Matchers.equalTo(expectation)));
    }

    @Test(timeout = TimeoutInMillis.MINUTE)
    public void testComparableMin_withAttributePath_withNull() {
        List<ValueContainer> values = TestSamples.sampleValueContainers(STRING);
        Collections.sort(values);
        String expectation = values.get(0).stringValue;
        values.add(null);
        Aggregator<Map.Entry<ValueContainer, ValueContainer>, String> aggregation = Aggregators.comparableMin("stringValue");
        for (ValueContainer value : values) {
            aggregation.accumulate(TestSamples.createExtractableEntryWithValue(value, ss));
        }
        Aggregator<Map.Entry<ValueContainer, ValueContainer>, String> resultAggregation = Aggregators.comparableMin("stringValue");
        resultAggregation.combine(aggregation);
        String result = resultAggregation.aggregate();
        Assert.assertThat(result, Matchers.is(Matchers.equalTo(expectation)));
    }

    @Test(timeout = TimeoutInMillis.MINUTE)
    public void testMinBy_withAttributePath_withNull() {
        List<ValueContainer> values = TestSamples.sampleValueContainers(STRING);
        Collections.sort(values);
        Map.Entry<ValueContainer, ValueContainer> expectation = TestSamples.createExtractableEntryWithValue(values.get(0), ss);
        values.add(null);
        Aggregator<Map.Entry<ValueContainer, ValueContainer>, Map.Entry<ValueContainer, ValueContainer>> aggregation = Aggregators.minBy("stringValue");
        for (ValueContainer value : values) {
            aggregation.accumulate(TestSamples.createExtractableEntryWithValue(value, ss));
        }
        Aggregator<Map.Entry<ValueContainer, ValueContainer>, Map.Entry<ValueContainer, ValueContainer>> resultAggregation = Aggregators.minBy("stringValue");
        resultAggregation.combine(aggregation);
        Map.Entry<ValueContainer, ValueContainer> result = resultAggregation.aggregate();
        Assert.assertThat(result, Matchers.is(Matchers.equalTo(expectation)));
    }
}

