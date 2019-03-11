/**
 * Copyright (C) 2011 Clearspring Technologies, Inc.
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
package com.clearspring.analytics.stream.cardinality;


import com.clearspring.analytics.stream.cardinality.LinearCounting.Builder;
import com.clearspring.analytics.stream.cardinality.LinearCounting.LinearCountingMergeException;
import java.util.Arrays;
import org.junit.Assert;
import org.junit.Test;


public class TestLinearCounting {
    @Test
    public void testComputeCount() {
        LinearCounting lc = new LinearCounting(4);
        lc.offer(0);
        lc.offer(1);
        lc.offer(2);
        lc.offer(3);
        lc.offer(16);
        lc.offer(17);
        lc.offer(18);
        lc.offer(19);
        Assert.assertEquals(27, lc.computeCount());
    }

    @Test
    public void testSaturation() {
        LinearCounting lc = new LinearCounting(1);
        for (int i = 0; i < 27; i++) {
            lc.offer(i);
        }
        Assert.assertTrue(lc.isSaturated());
        Assert.assertEquals(0, lc.getCount());
        Assert.assertEquals(Long.MAX_VALUE, lc.cardinality());
    }

    @Test
    public void testBuilder() {
        Assert.assertEquals(630, Builder.onePercentError(1).size);
        Assert.assertEquals(630, Builder.onePercentError(99).size);
        Assert.assertEquals(630, Builder.onePercentError(100).size);
        Assert.assertEquals(630, Builder.onePercentError(101).size);
        Assert.assertEquals(759, Builder.onePercentError(3375).size);
        Assert.assertEquals(995, Builder.onePercentError(9999).size);
        Assert.assertEquals(995, Builder.onePercentError(10000).size);
        Assert.assertEquals(996, Builder.onePercentError(10001).size);
        Assert.assertEquals(7501, Builder.onePercentError(305028).size);
        Assert.assertEquals(19272, Builder.onePercentError(1000000).size);
        Assert.assertEquals(23027, Builder.onePercentError(1250000).size);
        Assert.assertEquals(74962, Builder.onePercentError(5000000).size);
        Assert.assertEquals(81372, Builder.onePercentError(5500000).size);
        Assert.assertEquals(131030, Builder.onePercentError(9500000).size);
        Assert.assertEquals(137073, Builder.onePercentError(10000000).size);
        Assert.assertEquals(137073, Builder.onePercentError(10000001).size);
        Assert.assertEquals(355055, Builder.onePercentError(30000000).size);
        Assert.assertEquals(573038, Builder.onePercentError(50000000).size);
        Assert.assertEquals(822207, Builder.onePercentError(75000000).size);
        Assert.assertEquals(1071377, Builder.onePercentError(100000000).size);
        Assert.assertEquals(1167722, Builder.onePercentError(110000000).size);
        Assert.assertEquals(1264067, Builder.onePercentError(120000000).size);
        Assert.assertEquals(2500000, Builder.onePercentError(240000000).size);
    }

    @Test
    public void testArbitraryStdErrorSize() {
        // Some sanity check with 1% error
        Assert.assertEquals(630, Builder.withError(0.01, 100).size);
        Assert.assertEquals(759, Builder.withError(0.01, 3375).size);
        // Checking for 10% error (values from original paper)
        Assert.assertEquals(10, Builder.withError(0.1, 100).size);
        Assert.assertEquals(34, Builder.withError(0.1, 1000).size);
        Assert.assertEquals(214, Builder.withError(0.1, 10000).size);
        Assert.assertEquals(1593, Builder.withError(0.1, 100000).size);
        Assert.assertEquals(12610, Builder.withError(0.1, 1000000).size);
        Assert.assertEquals(103977, Builder.withError(0.1, 10000000).size);
        Assert.assertEquals(882720, Builder.withError(0.1, 100000000).size);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testBuilderIllegalArgumentZero() {
        Builder.onePercentError(0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testBuilderIllegalArgumentNegative() {
        Builder.onePercentError((-1));
    }

    @Test
    public void testSerialization() {
        LinearCounting lc = new LinearCounting(4);
        lc.offer("a");
        lc.offer("b");
        lc.offer("c");
        lc.offer("d");
        lc.offer("e");
        LinearCounting lc2 = new LinearCounting(lc.getBytes());
        Assert.assertArrayEquals(lc.map, lc2.map);
        Assert.assertEquals(lc.count, lc2.count);
        Assert.assertEquals(lc.length, lc2.length);
    }

    @Test
    public void testMerge() throws LinearCountingMergeException {
        int numToMerge = 5;
        int size = 65536;
        int cardinality = 1000;
        LinearCounting[] lcs = new LinearCounting[numToMerge];
        LinearCounting baseline = new LinearCounting(size);
        for (int i = 0; i < numToMerge; i++) {
            lcs[i] = new LinearCounting(size);
            for (int j = 0; j < cardinality; j++) {
                double val = Math.random();
                lcs[i].offer(val);
                baseline.offer(val);
            }
        }
        int expectedCardinality = numToMerge * cardinality;
        long mergedEstimate = LinearCounting.mergeEstimators(lcs).cardinality();
        double error = (Math.abs((mergedEstimate - expectedCardinality))) / ((double) (expectedCardinality));
        Assert.assertEquals(0.01, error, 0.01);
        LinearCounting lc = lcs[0];
        lcs = Arrays.asList(lcs).subList(1, lcs.length).toArray(new LinearCounting[0]);
        mergedEstimate = lc.merge(lcs).cardinality();
        error = (Math.abs((mergedEstimate - expectedCardinality))) / ((double) (expectedCardinality));
        Assert.assertEquals(0.01, error, 0.01);
        long baselineEstimate = baseline.cardinality();
        Assert.assertEquals(baselineEstimate, mergedEstimate);
    }
}

