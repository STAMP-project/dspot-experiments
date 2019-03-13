/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.beam.runners.dataflow.worker.counters;


import java.util.List;
import org.apache.beam.runners.dataflow.worker.counters.CounterFactory.CounterDistribution;
import org.apache.beam.vendor.guava.v20_0.com.google.common.collect.ImmutableList;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Unit tests for {@link CounterFactory} and nested classes.
 */
@RunWith(JUnit4.class)
public class CounterFactoryTest {
    /**
     * Validate boundary values in the full range of positive long integers that they are placed in
     * the correct CounterDistribution histogram bucket. The naive bucketing implementation of using
     * Math.log10 introduces error for large values due to precision loss in converting large integers
     * to and from floating point.
     */
    @Test
    public void testCounterDistributionCalculateBucket() {
        Assert.assertEquals(0, CounterDistribution.calculateBucket(0));
        int bucket = 1;
        long powerOfTen = 1;
        /* break on overflow */
        while (powerOfTen > 0) {
            for (int multiplier : new int[]{ 1, 2, 5 }) {
                long value = powerOfTen * multiplier;
                verifyDistributionBucket((value - 1), (bucket - 1));
                verifyDistributionBucket(value, bucket);
                bucket++;
            }
            powerOfTen *= 10;
        } 
    }

    @Test
    public void testCounterDistributionAddValue() {
        CounterDistribution counter = CounterDistribution.empty();
        List<Long> expectedBuckets = ImmutableList.of(1L, 3L, 0L, 0L, 0L, 0L, 0L, 0L, 1L, 1L);
        for (long value : new long[]{ 1, 500, 2, 3, 1000, 4 }) {
            counter = counter.addValue(value);
        }
        Assert.assertEquals(expectedBuckets, counter.getBuckets());
        Assert.assertEquals(1250030.0, counter.getSumOfSquares(), 0);
        Assert.assertEquals(1510, counter.getSum());
        Assert.assertEquals(1, counter.getFirstBucketOffset());
        Assert.assertEquals(6, counter.getCount());
        Assert.assertEquals(1, counter.getMin());
        Assert.assertEquals(1000, counter.getMax());
    }
}

