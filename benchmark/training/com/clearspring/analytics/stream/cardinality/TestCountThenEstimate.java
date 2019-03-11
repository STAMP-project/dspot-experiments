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


import AdaptiveCounting.Builder;
import java.io.IOException;
import org.junit.Assert;
import org.junit.Test;


public class TestCountThenEstimate {
    @Test
    public void testMerge() throws CardinalityMergeException {
        int numToMerge = 10;
        int tippingPoint = 100;
        int cardinality = 1000;
        CountThenEstimate[] ctes = new CountThenEstimate[numToMerge];
        for (int i = 0; i < numToMerge; i++) {
            ctes[i] = new CountThenEstimate(tippingPoint, Builder.obyCount(100000));
            for (int j = 0; j < (tippingPoint - 1); j++) {
                ctes[i].offer(Math.random());
            }
        }
        int expectedCardinality = numToMerge * (tippingPoint - 1);
        long mergedEstimate = CountThenEstimate.mergeEstimators(ctes).cardinality();
        double error = (Math.abs((mergedEstimate - expectedCardinality))) / ((double) (expectedCardinality));
        Assert.assertEquals(0.01, error, 0.01);
        for (int i = 0; i < (numToMerge / 2); i++) {
            for (int j = tippingPoint - 1; j < cardinality; j++) {
                ctes[i].offer(Math.random());
            }
        }
        expectedCardinality = (numToMerge / 2) * ((cardinality + tippingPoint) - 1);
        mergedEstimate = CountThenEstimate.mergeEstimators(ctes).cardinality();
        error = (Math.abs((mergedEstimate - expectedCardinality))) / ((double) (expectedCardinality));
        Assert.assertEquals(0.01, error, 0.01);
        for (int i = numToMerge / 2; i < numToMerge; i++) {
            for (int j = tippingPoint - 1; j < cardinality; j++) {
                ctes[i].offer(Math.random());
            }
        }
        expectedCardinality = numToMerge * cardinality;
        mergedEstimate = CountThenEstimate.mergeEstimators(ctes).cardinality();
        error = (Math.abs((mergedEstimate - expectedCardinality))) / ((double) (expectedCardinality));
        Assert.assertEquals(0.01, error, 0.01);
    }

    @Test
    public void testSmallMerge() throws CardinalityMergeException {
        // Untipped test case
        int numToMerge = 1000;
        int cardinalityPer = 5;
        CountThenEstimate[] ctes = new CountThenEstimate[numToMerge];
        for (int i = 0; i < numToMerge; i++) {
            ctes[i] = new CountThenEstimate(10000, Builder.obyCount(100000));
            for (int j = 0; j < cardinalityPer; j++) {
                ctes[i].offer(Math.random());
            }
        }
        int expectedCardinality = numToMerge * cardinalityPer;
        CountThenEstimate merged = CountThenEstimate.mergeEstimators(ctes);
        long mergedEstimate = merged.cardinality();
        Assert.assertEquals(expectedCardinality, mergedEstimate);
        Assert.assertFalse(merged.tipped);
        // Tipped test case
        numToMerge = 10;
        cardinalityPer = 100;
        ctes = new CountThenEstimate[numToMerge];
        for (int i = 0; i < numToMerge; i++) {
            ctes[i] = new CountThenEstimate((cardinalityPer + 1), Builder.obyCount(100000));
            for (int j = 0; j < cardinalityPer; j++) {
                ctes[i].offer(Math.random());
            }
        }
        expectedCardinality = numToMerge * cardinalityPer;
        merged = CountThenEstimate.mergeEstimators(ctes);
        mergedEstimate = merged.cardinality();
        double error = (Math.abs((mergedEstimate - expectedCardinality))) / ((double) (expectedCardinality));
        Assert.assertEquals(0.01, error, 0.01);
        Assert.assertTrue(merged.tipped);
    }

    @Test
    public void testTip() throws IOException, ClassNotFoundException {
        CountThenEstimate cte = new CountThenEstimate(10000, new LinearCounting.Builder(1024));
        CountThenEstimate clone = new CountThenEstimate(cte.getBytes());
        assertCountThenEstimateEquals(cte, clone);
        for (int i = 0; i < 128; i++) {
            cte.offer(Integer.toString(i));
        }
        clone = new CountThenEstimate(cte.getBytes());
        Assert.assertEquals(128, cte.cardinality());
        assertCountThenEstimateEquals(cte, clone);
        for (int i = 128; i < 256; i++) {
            cte.offer(Integer.toString(i));
        }
        clone = new CountThenEstimate(cte.getBytes());
        Assert.assertFalse(cte.tipped());
        Assert.assertEquals(256, cte.cardinality());
        Assert.assertTrue(clone.tipped());
        double error = (Math.abs(((cte.cardinality()) - 256))) / 256.0;
        Assert.assertEquals(0.1, error, 0.1);
    }

    @Test
    public void testLinearCountingSerialization() throws IOException, ClassNotFoundException {
        CountThenEstimate cte = new CountThenEstimate(3, new LinearCounting.Builder(1024));
        CountThenEstimate clone = new CountThenEstimate(cte.getBytes());
        assertCountThenEstimateEquals(cte, clone);
        cte.offer("1");
        cte.offer("2");
        cte.offer("3");
        Assert.assertEquals(3, cte.cardinality());
        clone = new CountThenEstimate(cte.getBytes());
        assertCountThenEstimateEquals(cte, clone);
        cte.offer("4");
        clone = new CountThenEstimate(cte.getBytes());
        assertCountThenEstimateEquals(cte, clone);
        Assert.assertEquals(0, clone.tippingPoint);
    }

    @Test
    public void testHyperLogLogSerialization() throws IOException, ClassNotFoundException {
        CountThenEstimate cte = new CountThenEstimate(3, new HyperLogLog.Builder(0.05));
        CountThenEstimate clone = new CountThenEstimate(cte.getBytes());
        assertCountThenEstimateEquals(cte, clone);
        cte.offer("1");
        cte.offer("2");
        cte.offer("3");
        Assert.assertEquals(3, cte.cardinality());
        clone = new CountThenEstimate(cte.getBytes());
        assertCountThenEstimateEquals(cte, clone);
        cte.offer("4");
        clone = new CountThenEstimate(cte.getBytes());
        assertCountThenEstimateEquals(cte, clone);
        Assert.assertEquals(0, clone.tippingPoint);
    }

    @Test
    public void testAdaptiveCountingSerialization() throws IOException, ClassNotFoundException {
        CountThenEstimate cte = new CountThenEstimate(3, new AdaptiveCounting.Builder(10));
        CountThenEstimate clone = new CountThenEstimate(cte.getBytes());
        assertCountThenEstimateEquals(cte, clone);
        cte.offer("1");
        cte.offer("2");
        cte.offer("3");
        Assert.assertEquals(3, cte.cardinality());
        clone = new CountThenEstimate(cte.getBytes());
        assertCountThenEstimateEquals(cte, clone);
        cte.offer("4");
        clone = new CountThenEstimate(cte.getBytes());
        assertCountThenEstimateEquals(cte, clone);
        Assert.assertEquals(0, clone.tippingPoint);
    }

    @Test
    public void testAdaptiveCountingSerialization_withHyperLogLog() throws IOException, ClassNotFoundException {
        CountThenEstimate cte = new CountThenEstimate(3, new HyperLogLog.Builder(0.01));
        CountThenEstimate clone = new CountThenEstimate(cte.getBytes());
        assertCountThenEstimateEquals(cte, clone);
        cte.offer("1");
        cte.offer("2");
        cte.offer("3");
        cte.offer("3");
        cte.offer("2");
        Assert.assertEquals(3, cte.cardinality());
        clone = new CountThenEstimate(cte.getBytes());
        assertCountThenEstimateEquals(cte, clone);
        cte.offer("4");
        clone = new CountThenEstimate(cte.getBytes());
        Assert.assertEquals(4, clone.cardinality());
        Assert.assertEquals(cte.cardinality(), clone.cardinality());
        assertCountThenEstimateEquals(cte, clone);
        Assert.assertEquals(0, clone.tippingPoint);
    }
}

