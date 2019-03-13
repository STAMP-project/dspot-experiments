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
package com.hazelcast.client.mapreduce.aggregation;


import com.hazelcast.mapreduce.aggregation.Aggregation;
import com.hazelcast.mapreduce.aggregation.Aggregations;
import com.hazelcast.test.HazelcastParallelClassRunner;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Random;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;


// https://github.com/hazelcast/hazelcast/issues/5916
@RunWith(HazelcastParallelClassRunner.class)
@Category({ QuickTest.class, ParallelTest.class })
@Ignore
public class MaxAggregationMultiMapTest extends AbstractAggregationTest {
    @Test(timeout = 60000)
    public void testBigDecimalMax() throws Exception {
        BigDecimal[] values = AbstractAggregationTest.buildPlainValues(new AbstractAggregationTest.ValueProvider<BigDecimal>() {
            @Override
            public BigDecimal provideRandom(Random random) {
                return BigDecimal.valueOf((10000.0 + (AbstractAggregationTest.random(1000, 2000))));
            }
        }, BigDecimal.class);
        BigDecimal expectation = BigDecimal.ZERO;
        for (int i = 0; i < (values.length); i++) {
            BigDecimal value = values[i];
            expectation = (i == 0) ? value : expectation.max(value);
        }
        Aggregation<String, BigDecimal, BigDecimal> aggregation = Aggregations.bigDecimalMax();
        BigDecimal result = testMax(values, aggregation);
        Assert.assertEquals(expectation, result);
    }

    @Test(timeout = 60000)
    public void testBigIntegerMax() throws Exception {
        BigInteger[] values = AbstractAggregationTest.buildPlainValues(new AbstractAggregationTest.ValueProvider<BigInteger>() {
            @Override
            public BigInteger provideRandom(Random random) {
                return BigInteger.valueOf((10000L + (AbstractAggregationTest.random(1000, 2000))));
            }
        }, BigInteger.class);
        BigInteger expectation = BigInteger.ZERO;
        for (int i = 0; i < (values.length); i++) {
            BigInteger value = values[i];
            expectation = (i == 0) ? value : expectation.max(value);
        }
        Aggregation<String, BigInteger, BigInteger> aggregation = Aggregations.bigIntegerMax();
        BigInteger result = testMax(values, aggregation);
        Assert.assertEquals(expectation, result);
    }

    @Test(timeout = 60000)
    public void testDoubleMax() throws Exception {
        Double[] values = AbstractAggregationTest.buildPlainValues(new AbstractAggregationTest.ValueProvider<Double>() {
            @Override
            public Double provideRandom(Random random) {
                return 10000.0 + (AbstractAggregationTest.random(1000, 2000));
            }
        }, Double.class);
        double expectation = -(Double.MAX_VALUE);
        for (int i = 0; i < (values.length); i++) {
            double value = values[i];
            if (value > expectation) {
                expectation = value;
            }
        }
        Aggregation<String, Double, Double> aggregation = Aggregations.doubleMax();
        double result = testMax(values, aggregation);
        Assert.assertEquals(expectation, result, 0.0);
    }

    @Test(timeout = 60000)
    public void testIntegerMax() throws Exception {
        Integer[] values = AbstractAggregationTest.buildPlainValues(new AbstractAggregationTest.ValueProvider<Integer>() {
            @Override
            public Integer provideRandom(Random random) {
                return AbstractAggregationTest.random(1000, 2000);
            }
        }, Integer.class);
        int expectation = Integer.MIN_VALUE;
        for (int i = 0; i < (values.length); i++) {
            int value = values[i];
            if (value > expectation) {
                expectation = value;
            }
        }
        Aggregation<String, Integer, Integer> aggregation = Aggregations.integerMax();
        int result = testMax(values, aggregation);
        Assert.assertEquals(expectation, result);
    }

    @Test(timeout = 60000)
    public void testLongMax() throws Exception {
        Long[] values = AbstractAggregationTest.buildPlainValues(new AbstractAggregationTest.ValueProvider<Long>() {
            @Override
            public Long provideRandom(Random random) {
                return 10000L + (AbstractAggregationTest.random(1000, 2000));
            }
        }, Long.class);
        long expectation = Long.MIN_VALUE;
        for (int i = 0; i < (values.length); i++) {
            long value = values[i];
            if (value > expectation) {
                expectation = value;
            }
        }
        Aggregation<String, Long, Long> aggregation = Aggregations.longMax();
        long result = testMax(values, aggregation);
        Assert.assertEquals(expectation, result);
    }

    @Test(timeout = 60000)
    public void testBigDecimalMaxWithExtractor() throws Exception {
        AbstractAggregationTest.Value<BigDecimal>[] values = AbstractAggregationTest.buildValues(new AbstractAggregationTest.ValueProvider<BigDecimal>() {
            @Override
            public BigDecimal provideRandom(Random random) {
                return BigDecimal.valueOf((10000.0 + (AbstractAggregationTest.random(1000, 2000))));
            }
        });
        BigDecimal expectation = BigDecimal.ZERO;
        for (int i = 0; i < (values.length); i++) {
            AbstractAggregationTest.Value<BigDecimal> value = values[i];
            expectation = (i == 0) ? value.value : expectation.max(value.value);
        }
        Aggregation<String, BigDecimal, BigDecimal> aggregation = Aggregations.bigDecimalMax();
        BigDecimal result = testMaxWithExtractor(values, aggregation);
        Assert.assertEquals(expectation, result);
    }

    @Test(timeout = 60000)
    public void testBigIntegerMaxWithExtractor() throws Exception {
        AbstractAggregationTest.Value<BigInteger>[] values = AbstractAggregationTest.buildValues(new AbstractAggregationTest.ValueProvider<BigInteger>() {
            @Override
            public BigInteger provideRandom(Random random) {
                return BigInteger.valueOf((10000L + (AbstractAggregationTest.random(1000, 2000))));
            }
        });
        BigInteger expectation = BigInteger.ZERO;
        for (int i = 0; i < (values.length); i++) {
            AbstractAggregationTest.Value<BigInteger> value = values[i];
            expectation = (i == 0) ? value.value : expectation.max(value.value);
        }
        Aggregation<String, BigInteger, BigInteger> aggregation = Aggregations.bigIntegerMax();
        BigInteger result = testMaxWithExtractor(values, aggregation);
        Assert.assertEquals(expectation, result);
    }

    @Test(timeout = 60000)
    public void testDoubleMaxWithExtractor() throws Exception {
        AbstractAggregationTest.Value<Double>[] values = AbstractAggregationTest.buildValues(new AbstractAggregationTest.ValueProvider<Double>() {
            @Override
            public Double provideRandom(Random random) {
                return 10000.0 + (AbstractAggregationTest.random(1000, 2000));
            }
        });
        double expectation = -(Double.MAX_VALUE);
        for (int i = 0; i < (values.length); i++) {
            double value = values[i].value;
            if (value > expectation) {
                expectation = value;
            }
        }
        Aggregation<String, Double, Double> aggregation = Aggregations.doubleMax();
        double result = testMaxWithExtractor(values, aggregation);
        Assert.assertEquals(expectation, result, 0.0);
    }

    @Test(timeout = 60000)
    public void testIntegerMaxWithExtractor() throws Exception {
        AbstractAggregationTest.Value<Integer>[] values = AbstractAggregationTest.buildValues(new AbstractAggregationTest.ValueProvider<Integer>() {
            @Override
            public Integer provideRandom(Random random) {
                return AbstractAggregationTest.random(1000, 2000);
            }
        });
        int expectation = Integer.MIN_VALUE;
        for (int i = 0; i < (values.length); i++) {
            int value = values[i].value;
            if (value > expectation) {
                expectation = value;
            }
        }
        Aggregation<String, Integer, Integer> aggregation = Aggregations.integerMax();
        int result = testMaxWithExtractor(values, aggregation);
        Assert.assertEquals(expectation, result);
    }

    @Test(timeout = 60000)
    public void testLongMaxWithExtractor() throws Exception {
        AbstractAggregationTest.Value<Long>[] values = AbstractAggregationTest.buildValues(new AbstractAggregationTest.ValueProvider<Long>() {
            @Override
            public Long provideRandom(Random random) {
                return 10000L + (AbstractAggregationTest.random(1000, 2000));
            }
        });
        long expectation = Long.MIN_VALUE;
        for (int i = 0; i < (values.length); i++) {
            long value = values[i].value;
            if (value > expectation) {
                expectation = value;
            }
        }
        Aggregation<String, Long, Long> aggregation = Aggregations.longMax();
        long result = testMaxWithExtractor(values, aggregation);
        Assert.assertEquals(expectation, result);
    }
}

