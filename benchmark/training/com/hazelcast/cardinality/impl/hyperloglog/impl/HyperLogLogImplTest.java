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
package com.hazelcast.cardinality.impl.hyperloglog.impl;


import com.hazelcast.cardinality.impl.hyperloglog.HyperLogLog;
import com.hazelcast.test.HazelcastParametersRunnerFactory;
import com.hazelcast.test.annotation.SlowTest;
import com.hazelcast.util.HashUtil;
import com.hazelcast.util.collection.IntHashSet;
import java.nio.ByteBuffer;
import java.util.Random;
import org.HdrHistogram.Histogram;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;


@RunWith(Parameterized.class)
@Parameterized.UseParametersRunnerFactory(HazelcastParametersRunnerFactory.class)
@Category(SlowTest.class)
public class HyperLogLogImplTest {
    private static final int DEFAULT_RUN_LENGTH = 10000000;

    @Parameterized.Parameter
    public int precision;

    @Parameterized.Parameter(1)
    public double errorRange;

    private HyperLogLog hyperLogLog;

    @Test
    public void add() {
        hyperLogLog.add(1000L);
        Assert.assertEquals(1L, hyperLogLog.estimate());
    }

    @Test
    public void addAll() {
        hyperLogLog.addAll(new long[]{ 1L, 1L, 2000L, 3000L, 40000L });
        Assert.assertEquals(4L, hyperLogLog.estimate());
    }

    /**
     * <ul>
     * <li>Adds up to {@link #DEFAULT_RUN_LENGTH} random numbers on both a Set and a HyperLogLog encoder.</li>
     * <li>Samples the actual count, and the estimate respectively every 100 operations.</li>
     * <li>Computes the error rate, of the measurements and store it in a histogram.</li>
     * <li>Asserts that the 99th percentile of the histogram is less than the expected max error,
     * which is the result of std error (1.04 / sqrt(m)) + [2.0, 6.5]% (2% is the typical accuracy,
     * but tests with a lower precision need a higher error range).</li>
     * </ul>
     */
    @Test
    public void testEstimateErrorRateForBigCardinalities() {
        double stdError = (1.04F / (Math.sqrt((1 << (precision))))) * 100;
        double maxError = Math.ceil((stdError + (errorRange)));
        IntHashSet actualCount = new IntHashSet(HyperLogLogImplTest.DEFAULT_RUN_LENGTH, (-1));
        Random random = new Random();
        Histogram histogram = new Histogram(5);
        ByteBuffer bb = ByteBuffer.allocate(4);
        int sampleStep = 100;
        long expected;
        long actual;
        for (int i = 1; i <= (HyperLogLogImplTest.DEFAULT_RUN_LENGTH); i++) {
            int toCount = random.nextInt();
            actualCount.add(toCount);
            bb.clear();
            bb.putInt(toCount);
            hyperLogLog.add(HashUtil.MurmurHash3_x64_64(bb.array(), 0, bb.array().length));
            if ((i % sampleStep) == 0) {
                expected = actualCount.size();
                actual = hyperLogLog.estimate();
                double errorPct = ((actual * 100.0) / expected) - 100;
                histogram.recordValue(Math.abs(((long) (errorPct * 100))));
            }
        }
        double errorPerc99 = (histogram.getValueAtPercentile(99)) / 100.0;
        if (errorPerc99 > maxError) {
            Assert.fail((((((("For P=" + (precision)) + ": Expected max error=") + maxError) + "%. Actual error=") + errorPerc99) + "%."));
        }
    }
}

