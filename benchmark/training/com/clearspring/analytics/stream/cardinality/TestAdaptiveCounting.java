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


import java.util.Arrays;
import org.junit.Assert;
import org.junit.Test;


public class TestAdaptiveCounting {
    @Test
    public void testRho() {
        Assert.assertEquals(17, LogLog.rho(0, 16));
        Assert.assertEquals(16, LogLog.rho(1, 16));
        Assert.assertEquals(15, LogLog.rho(2, 16));
        Assert.assertEquals(1, LogLog.rho(32768, 16));
        Assert.assertEquals(23, LogLog.rho(0, 10));
        Assert.assertEquals(22, LogLog.rho(1, 10));
        Assert.assertEquals(21, LogLog.rho(2, 10));
        Assert.assertEquals(1, LogLog.rho(2097152, 10));
    }

    @Test
    public void testRhoL() {
        Assert.assertEquals(49, AdaptiveCounting.rho(0L, 16));
        Assert.assertEquals(48, AdaptiveCounting.rho(1L, 16));
        Assert.assertEquals(47, AdaptiveCounting.rho(2L, 16));
        Assert.assertEquals(1, AdaptiveCounting.rho(2147516416L, 32));
        Assert.assertEquals(55, AdaptiveCounting.rho(0L, 10));
        Assert.assertEquals(54, AdaptiveCounting.rho(1L, 10));
        Assert.assertEquals(53, AdaptiveCounting.rho(2L, 10));
        Assert.assertEquals(1, AdaptiveCounting.rho(9007199254740992L, 10));
        Assert.assertEquals(3, AdaptiveCounting.rho(-2404782631776564482L, 15));
    }

    @Test
    public void testJ() {
        long x = -2401053088335148290L;
        int k = 12;
        int j = ((int) (x >>> ((Long.SIZE) - k)));
        Assert.assertEquals(3562, j);
    }

    @Test
    public void testMerge() throws CardinalityMergeException {
        int numToMerge = 10;
        int cardinality = 10000;
        AdaptiveCounting[] lcs = new AdaptiveCounting[numToMerge];
        AdaptiveCounting baseline = new AdaptiveCounting(16);
        for (int i = 0; i < numToMerge; i++) {
            lcs[i] = new AdaptiveCounting(16);
            for (int j = 0; j < cardinality; j++) {
                double val = Math.random();
                lcs[i].offer(val);
                baseline.offer(val);
            }
        }
        int expectedCardinality = numToMerge * cardinality;
        long mergedEstimate = AdaptiveCounting.mergeEstimators(lcs).cardinality();
        double error = (Math.abs((mergedEstimate - expectedCardinality))) / ((double) (expectedCardinality));
        Assert.assertEquals(0.01, error, 0.01);
        AdaptiveCounting lc = lcs[0];
        lcs = Arrays.asList(lcs).subList(1, lcs.length).toArray(new AdaptiveCounting[0]);
        mergedEstimate = lc.merge(lcs).cardinality();
        error = (Math.abs((mergedEstimate - expectedCardinality))) / ((double) (expectedCardinality));
        Assert.assertEquals(0.01, error, 0.01);
        Assert.assertEquals(baseline.cardinality(), mergedEstimate);
    }

    @Test
    public void testSerialization() {
        AdaptiveCounting ac = new AdaptiveCounting(10);
        testSerialization(ac);
    }
}

