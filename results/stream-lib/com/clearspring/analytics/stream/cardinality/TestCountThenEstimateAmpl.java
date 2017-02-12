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


public class TestCountThenEstimateAmpl {
    @org.junit.Test
    public void testMerge() throws com.clearspring.analytics.stream.cardinality.CardinalityMergeException {
        int numToMerge = 10;
        int tippingPoint = 100;
        int cardinality = 1000;
        com.clearspring.analytics.stream.cardinality.CountThenEstimate[] ctes = new com.clearspring.analytics.stream.cardinality.CountThenEstimate[numToMerge];
        for (int i = 0; i < numToMerge; i++) {
            ctes[i] = new com.clearspring.analytics.stream.cardinality.CountThenEstimate(tippingPoint, com.clearspring.analytics.stream.cardinality.AdaptiveCounting.Builder.obyCount(100000));
            for (int j = 0; j < (tippingPoint - 1); j++) {
                ctes[i].offer(java.lang.Math.random());
            }
        }
        int expectedCardinality = numToMerge * (tippingPoint - 1);
        long mergedEstimate = com.clearspring.analytics.stream.cardinality.CountThenEstimate.mergeEstimators(ctes).cardinality();
        double error = (java.lang.Math.abs((mergedEstimate - expectedCardinality))) / ((double) (expectedCardinality));
        org.junit.Assert.assertEquals(0.01, error, 0.01);
        for (int i = 0; i < (numToMerge / 2); i++) {
            for (int j = tippingPoint - 1; j < cardinality; j++) {
                ctes[i].offer(java.lang.Math.random());
            }
        }
        expectedCardinality = (numToMerge / 2) * ((cardinality + tippingPoint) - 1);
        mergedEstimate = com.clearspring.analytics.stream.cardinality.CountThenEstimate.mergeEstimators(ctes).cardinality();
        error = (java.lang.Math.abs((mergedEstimate - expectedCardinality))) / ((double) (expectedCardinality));
        org.junit.Assert.assertEquals(0.01, error, 0.01);
        for (int i = numToMerge / 2; i < numToMerge; i++) {
            for (int j = tippingPoint - 1; j < cardinality; j++) {
                ctes[i].offer(java.lang.Math.random());
            }
        }
        expectedCardinality = numToMerge * cardinality;
        mergedEstimate = com.clearspring.analytics.stream.cardinality.CountThenEstimate.mergeEstimators(ctes).cardinality();
        error = (java.lang.Math.abs((mergedEstimate - expectedCardinality))) / ((double) (expectedCardinality));
        org.junit.Assert.assertEquals(0.01, error, 0.01);
    }

    @org.junit.Test
    public void testTip() throws java.io.IOException, java.lang.ClassNotFoundException {
        com.clearspring.analytics.stream.cardinality.CountThenEstimate cte = new com.clearspring.analytics.stream.cardinality.CountThenEstimate(10000, new com.clearspring.analytics.stream.cardinality.LinearCounting.Builder(1024));
        com.clearspring.analytics.stream.cardinality.CountThenEstimate clone = new com.clearspring.analytics.stream.cardinality.CountThenEstimate(cte.getBytes());
        assertCountThenEstimateEquals(cte, clone);
        for (int i = 0; i < 128; i++) {
            cte.offer(java.lang.Integer.toString(i));
        }
        clone = new com.clearspring.analytics.stream.cardinality.CountThenEstimate(cte.getBytes());
        org.junit.Assert.assertEquals(128, cte.cardinality());
        assertCountThenEstimateEquals(cte, clone);
        for (int i = 128; i < 256; i++) {
            cte.offer(java.lang.Integer.toString(i));
        }
        clone = new com.clearspring.analytics.stream.cardinality.CountThenEstimate(cte.getBytes());
        org.junit.Assert.assertFalse(cte.tipped());
        org.junit.Assert.assertEquals(256, cte.cardinality());
        org.junit.Assert.assertTrue(clone.tipped());
        double error = (java.lang.Math.abs(((cte.cardinality()) - 256))) / 256.0;
        org.junit.Assert.assertEquals(0.1, error, 0.1);
    }

    @org.junit.Test
    public void testLinearCountingSerialization() throws java.io.IOException, java.lang.ClassNotFoundException {
        com.clearspring.analytics.stream.cardinality.CountThenEstimate cte = new com.clearspring.analytics.stream.cardinality.CountThenEstimate(3, new com.clearspring.analytics.stream.cardinality.LinearCounting.Builder(1024));
        com.clearspring.analytics.stream.cardinality.CountThenEstimate clone = new com.clearspring.analytics.stream.cardinality.CountThenEstimate(cte.getBytes());
        assertCountThenEstimateEquals(cte, clone);
        cte.offer("1");
        cte.offer("2");
        cte.offer("3");
        org.junit.Assert.assertEquals(3, cte.cardinality());
        clone = new com.clearspring.analytics.stream.cardinality.CountThenEstimate(cte.getBytes());
        assertCountThenEstimateEquals(cte, clone);
        cte.offer("4");
        clone = new com.clearspring.analytics.stream.cardinality.CountThenEstimate(cte.getBytes());
        assertCountThenEstimateEquals(cte, clone);
        org.junit.Assert.assertEquals(0, clone.tippingPoint);
    }

    @org.junit.Test
    public void testHyperLogLogSerialization() throws java.io.IOException, java.lang.ClassNotFoundException {
        com.clearspring.analytics.stream.cardinality.CountThenEstimate cte = new com.clearspring.analytics.stream.cardinality.CountThenEstimate(3, new com.clearspring.analytics.stream.cardinality.HyperLogLog.Builder(0.05));
        com.clearspring.analytics.stream.cardinality.CountThenEstimate clone = new com.clearspring.analytics.stream.cardinality.CountThenEstimate(cte.getBytes());
        assertCountThenEstimateEquals(cte, clone);
        cte.offer("1");
        cte.offer("2");
        cte.offer("3");
        org.junit.Assert.assertEquals(3, cte.cardinality());
        clone = new com.clearspring.analytics.stream.cardinality.CountThenEstimate(cte.getBytes());
        assertCountThenEstimateEquals(cte, clone);
        cte.offer("4");
        clone = new com.clearspring.analytics.stream.cardinality.CountThenEstimate(cte.getBytes());
        assertCountThenEstimateEquals(cte, clone);
        org.junit.Assert.assertEquals(0, clone.tippingPoint);
    }

    @org.junit.Test
    public void testAdaptiveCountingSerialization() throws java.io.IOException, java.lang.ClassNotFoundException {
        com.clearspring.analytics.stream.cardinality.CountThenEstimate cte = new com.clearspring.analytics.stream.cardinality.CountThenEstimate(3, new com.clearspring.analytics.stream.cardinality.AdaptiveCounting.Builder(10));
        com.clearspring.analytics.stream.cardinality.CountThenEstimate clone = new com.clearspring.analytics.stream.cardinality.CountThenEstimate(cte.getBytes());
        assertCountThenEstimateEquals(cte, clone);
        cte.offer("1");
        cte.offer("2");
        cte.offer("3");
        org.junit.Assert.assertEquals(3, cte.cardinality());
        clone = new com.clearspring.analytics.stream.cardinality.CountThenEstimate(cte.getBytes());
        assertCountThenEstimateEquals(cte, clone);
        cte.offer("4");
        clone = new com.clearspring.analytics.stream.cardinality.CountThenEstimate(cte.getBytes());
        assertCountThenEstimateEquals(cte, clone);
        org.junit.Assert.assertEquals(0, clone.tippingPoint);
    }

    @org.junit.Test
    public void testAdaptiveCountingSerialization_withHyperLogLog() throws java.io.IOException, java.lang.ClassNotFoundException {
        com.clearspring.analytics.stream.cardinality.CountThenEstimate cte = new com.clearspring.analytics.stream.cardinality.CountThenEstimate(3, new com.clearspring.analytics.stream.cardinality.HyperLogLog.Builder(0.01));
        com.clearspring.analytics.stream.cardinality.CountThenEstimate clone = new com.clearspring.analytics.stream.cardinality.CountThenEstimate(cte.getBytes());
        assertCountThenEstimateEquals(cte, clone);
        cte.offer("1");
        cte.offer("2");
        cte.offer("3");
        cte.offer("3");
        cte.offer("2");
        org.junit.Assert.assertEquals(3, cte.cardinality());
        clone = new com.clearspring.analytics.stream.cardinality.CountThenEstimate(cte.getBytes());
        assertCountThenEstimateEquals(cte, clone);
        cte.offer("4");
        clone = new com.clearspring.analytics.stream.cardinality.CountThenEstimate(cte.getBytes());
        org.junit.Assert.assertEquals(4, clone.cardinality());
        org.junit.Assert.assertEquals(cte.cardinality(), clone.cardinality());
        assertCountThenEstimateEquals(cte, clone);
        org.junit.Assert.assertEquals(0, clone.tippingPoint);
    }

    private void assertCountThenEstimateEquals(com.clearspring.analytics.stream.cardinality.CountThenEstimate expected, com.clearspring.analytics.stream.cardinality.CountThenEstimate actual) throws java.io.IOException {
        org.junit.Assert.assertEquals(expected.tipped, actual.tipped);
        if (expected.tipped) {
            org.junit.Assert.assertArrayEquals(expected.estimator.getBytes(), actual.estimator.getBytes());
        }else {
            org.junit.Assert.assertEquals(expected.tippingPoint, actual.tippingPoint);
            if ((expected.builder) instanceof com.clearspring.analytics.stream.cardinality.LinearCounting.Builder) {
                org.junit.Assert.assertEquals(((com.clearspring.analytics.stream.cardinality.LinearCounting.Builder) (expected.builder)).size, ((com.clearspring.analytics.stream.cardinality.LinearCounting.Builder) (actual.builder)).size);
            }else
                if ((expected.builder) instanceof com.clearspring.analytics.stream.cardinality.AdaptiveCounting.Builder) {
                    org.junit.Assert.assertEquals(((com.clearspring.analytics.stream.cardinality.AdaptiveCounting.Builder) (expected.builder)).k, ((com.clearspring.analytics.stream.cardinality.AdaptiveCounting.Builder) (actual.builder)).k);
                }
            
            org.junit.Assert.assertEquals(expected.estimator, actual.estimator);
        }
        org.junit.Assert.assertEquals(expected.counter, actual.counter);
        org.junit.Assert.assertEquals(expected.cardinality(), actual.cardinality());
    }

    @org.junit.Test
    public void testSmallMerge() throws com.clearspring.analytics.stream.cardinality.CardinalityMergeException {
        // Untipped test case
        int numToMerge = 1000;
        int cardinalityPer = 5;
        com.clearspring.analytics.stream.cardinality.CountThenEstimate[] ctes = new com.clearspring.analytics.stream.cardinality.CountThenEstimate[numToMerge];
        for (int i = 0; i < numToMerge; i++) {
            ctes[i] = new com.clearspring.analytics.stream.cardinality.CountThenEstimate(10000, com.clearspring.analytics.stream.cardinality.AdaptiveCounting.Builder.obyCount(100000));
            for (int j = 0; j < cardinalityPer; j++) {
                // AssertGenerator replace invocation
                boolean o_testSmallMerge__16 = ctes[i].offer(java.lang.Math.random());
                // AssertGenerator add assertion
                junit.framework.Assert.assertTrue(o_testSmallMerge__16);
            }
        }
        int expectedCardinality = numToMerge * cardinalityPer;
        com.clearspring.analytics.stream.cardinality.CountThenEstimate merged = com.clearspring.analytics.stream.cardinality.CountThenEstimate.mergeEstimators(ctes);
        long mergedEstimate = merged.cardinality();
        org.junit.Assert.assertEquals(expectedCardinality, mergedEstimate);
        org.junit.Assert.assertFalse(merged.tipped);
        // Tipped test case
        numToMerge = 10;
        cardinalityPer = 100;
        ctes = new com.clearspring.analytics.stream.cardinality.CountThenEstimate[numToMerge];
        for (int i = 0; i < numToMerge; i++) {
            ctes[i] = new com.clearspring.analytics.stream.cardinality.CountThenEstimate((cardinalityPer + 1), com.clearspring.analytics.stream.cardinality.AdaptiveCounting.Builder.obyCount(100000));
            for (int j = 0; j < cardinalityPer; j++) {
                // AssertGenerator replace invocation
                boolean o_testSmallMerge__40 = ctes[i].offer(java.lang.Math.random());
                // AssertGenerator add assertion
                junit.framework.Assert.assertTrue(o_testSmallMerge__40);
            }
        }
        expectedCardinality = numToMerge * cardinalityPer;
        merged = com.clearspring.analytics.stream.cardinality.CountThenEstimate.mergeEstimators(ctes);
        mergedEstimate = merged.cardinality();
        double error = (java.lang.Math.abs((mergedEstimate - expectedCardinality))) / ((double) (expectedCardinality));
        org.junit.Assert.assertEquals(0.01, error, 0.01);
        org.junit.Assert.assertTrue(merged.tipped);
    }

    /* amplification of com.clearspring.analytics.stream.cardinality.TestCountThenEstimate#testAdaptiveCountingSerialization */
    @org.junit.Test(timeout = 1000)
    public void testAdaptiveCountingSerialization_cf35_failAssert17() throws java.io.IOException, java.lang.ClassNotFoundException {
        // AssertGenerator generate try/catch block with fail statement
        try {
            com.clearspring.analytics.stream.cardinality.CountThenEstimate cte = new com.clearspring.analytics.stream.cardinality.CountThenEstimate(3, new com.clearspring.analytics.stream.cardinality.AdaptiveCounting.Builder(10));
            com.clearspring.analytics.stream.cardinality.CountThenEstimate clone = new com.clearspring.analytics.stream.cardinality.CountThenEstimate(cte.getBytes());
            assertCountThenEstimateEquals(cte, clone);
            cte.offer("1");
            cte.offer("2");
            cte.offer("3");
            // MethodAssertGenerator build local variable
            Object o_11_0 = cte.cardinality();
            clone = new com.clearspring.analytics.stream.cardinality.CountThenEstimate(cte.getBytes());
            assertCountThenEstimateEquals(cte, clone);
            cte.offer("4");
            clone = new com.clearspring.analytics.stream.cardinality.CountThenEstimate(cte.getBytes());
            assertCountThenEstimateEquals(cte, clone);
            // StatementAdderOnAssert create literal from method
            int int_vc_0 = 10;
            // StatementAdderMethod cloned existing statement
            cte.offerHashed(int_vc_0);
            org.junit.Assert.fail("testAdaptiveCountingSerialization_cf35 should have thrown UnsupportedOperationException");
        } catch (java.lang.UnsupportedOperationException eee) {
        }
    }

    /* amplification of com.clearspring.analytics.stream.cardinality.TestCountThenEstimate#testAdaptiveCountingSerialization */
    @org.junit.Test(timeout = 1000)
    public void testAdaptiveCountingSerialization_cf42_failAssert22() throws java.io.IOException, java.lang.ClassNotFoundException {
        // AssertGenerator generate try/catch block with fail statement
        try {
            com.clearspring.analytics.stream.cardinality.CountThenEstimate cte = new com.clearspring.analytics.stream.cardinality.CountThenEstimate(3, new com.clearspring.analytics.stream.cardinality.AdaptiveCounting.Builder(10));
            com.clearspring.analytics.stream.cardinality.CountThenEstimate clone = new com.clearspring.analytics.stream.cardinality.CountThenEstimate(cte.getBytes());
            assertCountThenEstimateEquals(cte, clone);
            cte.offer("1");
            cte.offer("2");
            cte.offer("3");
            // MethodAssertGenerator build local variable
            Object o_11_0 = cte.cardinality();
            clone = new com.clearspring.analytics.stream.cardinality.CountThenEstimate(cte.getBytes());
            assertCountThenEstimateEquals(cte, clone);
            cte.offer("4");
            clone = new com.clearspring.analytics.stream.cardinality.CountThenEstimate(cte.getBytes());
            assertCountThenEstimateEquals(cte, clone);
            // StatementAdderOnAssert create random local variable
            long vc_9 = -197233561718010203L;
            // StatementAdderMethod cloned existing statement
            clone.offerHashed(vc_9);
            org.junit.Assert.fail("testAdaptiveCountingSerialization_cf42 should have thrown UnsupportedOperationException");
        } catch (java.lang.UnsupportedOperationException eee) {
        }
    }

    /* amplification of com.clearspring.analytics.stream.cardinality.TestCountThenEstimate#testHyperLogLogSerialization */
    @org.junit.Test(timeout = 1000)
    public void testHyperLogLogSerialization_cf26080_failAssert18() throws java.io.IOException, java.lang.ClassNotFoundException {
        // AssertGenerator generate try/catch block with fail statement
        try {
            com.clearspring.analytics.stream.cardinality.CountThenEstimate cte = new com.clearspring.analytics.stream.cardinality.CountThenEstimate(3, new com.clearspring.analytics.stream.cardinality.HyperLogLog.Builder(0.05));
            com.clearspring.analytics.stream.cardinality.CountThenEstimate clone = new com.clearspring.analytics.stream.cardinality.CountThenEstimate(cte.getBytes());
            assertCountThenEstimateEquals(cte, clone);
            cte.offer("1");
            cte.offer("2");
            cte.offer("3");
            // MethodAssertGenerator build local variable
            Object o_11_0 = cte.cardinality();
            clone = new com.clearspring.analytics.stream.cardinality.CountThenEstimate(cte.getBytes());
            assertCountThenEstimateEquals(cte, clone);
            cte.offer("4");
            clone = new com.clearspring.analytics.stream.cardinality.CountThenEstimate(cte.getBytes());
            assertCountThenEstimateEquals(cte, clone);
            // StatementAdderOnAssert create literal from method
            int int_vc_304 = 0;
            // StatementAdderMethod cloned existing statement
            clone.offerHashed(int_vc_304);
            org.junit.Assert.fail("testHyperLogLogSerialization_cf26080 should have thrown UnsupportedOperationException");
        } catch (java.lang.UnsupportedOperationException eee) {
        }
    }

    /* amplification of com.clearspring.analytics.stream.cardinality.TestCountThenEstimate#testHyperLogLogSerialization */
    @org.junit.Test(timeout = 1000)
    public void testHyperLogLogSerialization_cf26089_failAssert24() throws java.io.IOException, java.lang.ClassNotFoundException {
        // AssertGenerator generate try/catch block with fail statement
        try {
            com.clearspring.analytics.stream.cardinality.CountThenEstimate cte = new com.clearspring.analytics.stream.cardinality.CountThenEstimate(3, new com.clearspring.analytics.stream.cardinality.HyperLogLog.Builder(0.05));
            com.clearspring.analytics.stream.cardinality.CountThenEstimate clone = new com.clearspring.analytics.stream.cardinality.CountThenEstimate(cte.getBytes());
            assertCountThenEstimateEquals(cte, clone);
            cte.offer("1");
            cte.offer("2");
            cte.offer("3");
            // MethodAssertGenerator build local variable
            Object o_11_0 = cte.cardinality();
            clone = new com.clearspring.analytics.stream.cardinality.CountThenEstimate(cte.getBytes());
            assertCountThenEstimateEquals(cte, clone);
            cte.offer("4");
            clone = new com.clearspring.analytics.stream.cardinality.CountThenEstimate(cte.getBytes());
            assertCountThenEstimateEquals(cte, clone);
            // StatementAdderOnAssert create random local variable
            long vc_9835 = 8108045383916083485L;
            // StatementAdderOnAssert create random local variable
            com.clearspring.analytics.stream.cardinality.CountThenEstimate vc_9834 = new com.clearspring.analytics.stream.cardinality.CountThenEstimate();
            // StatementAdderMethod cloned existing statement
            vc_9834.offerHashed(vc_9835);
            org.junit.Assert.fail("testHyperLogLogSerialization_cf26089 should have thrown UnsupportedOperationException");
        } catch (java.lang.UnsupportedOperationException eee) {
        }
    }

    /* amplification of com.clearspring.analytics.stream.cardinality.TestCountThenEstimate#testHyperLogLogSerialization */
    @org.junit.Test
    public void testHyperLogLogSerialization_literalMutation26058_failAssert2() throws java.io.IOException, java.lang.ClassNotFoundException {
        // AssertGenerator generate try/catch block with fail statement
        try {
            com.clearspring.analytics.stream.cardinality.CountThenEstimate cte = new com.clearspring.analytics.stream.cardinality.CountThenEstimate(3, new com.clearspring.analytics.stream.cardinality.HyperLogLog.Builder(0.0));
            com.clearspring.analytics.stream.cardinality.CountThenEstimate clone = new com.clearspring.analytics.stream.cardinality.CountThenEstimate(cte.getBytes());
            assertCountThenEstimateEquals(cte, clone);
            cte.offer("1");
            cte.offer("2");
            cte.offer("3");
            // MethodAssertGenerator build local variable
            Object o_12_0 = cte.cardinality();
            clone = new com.clearspring.analytics.stream.cardinality.CountThenEstimate(cte.getBytes());
            assertCountThenEstimateEquals(cte, clone);
            cte.offer("4");
            clone = new com.clearspring.analytics.stream.cardinality.CountThenEstimate(cte.getBytes());
            assertCountThenEstimateEquals(cte, clone);
            org.junit.Assert.fail("testHyperLogLogSerialization_literalMutation26058 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of com.clearspring.analytics.stream.cardinality.TestCountThenEstimate#testLinearCountingSerialization */
    @org.junit.Test(timeout = 1000)
    public void testLinearCountingSerialization_cf26838_failAssert21() throws java.io.IOException, java.lang.ClassNotFoundException {
        // AssertGenerator generate try/catch block with fail statement
        try {
            com.clearspring.analytics.stream.cardinality.CountThenEstimate cte = new com.clearspring.analytics.stream.cardinality.CountThenEstimate(3, new com.clearspring.analytics.stream.cardinality.LinearCounting.Builder(1024));
            com.clearspring.analytics.stream.cardinality.CountThenEstimate clone = new com.clearspring.analytics.stream.cardinality.CountThenEstimate(cte.getBytes());
            assertCountThenEstimateEquals(cte, clone);
            cte.offer("1");
            cte.offer("2");
            cte.offer("3");
            // MethodAssertGenerator build local variable
            Object o_11_0 = cte.cardinality();
            clone = new com.clearspring.analytics.stream.cardinality.CountThenEstimate(cte.getBytes());
            assertCountThenEstimateEquals(cte, clone);
            cte.offer("4");
            clone = new com.clearspring.analytics.stream.cardinality.CountThenEstimate(cte.getBytes());
            assertCountThenEstimateEquals(cte, clone);
            // StatementAdderOnAssert create random local variable
            long vc_9869 = -5293284616120601425L;
            // StatementAdderOnAssert create random local variable
            com.clearspring.analytics.stream.cardinality.CountThenEstimate vc_9868 = new com.clearspring.analytics.stream.cardinality.CountThenEstimate();
            // StatementAdderMethod cloned existing statement
            vc_9868.offerHashed(vc_9869);
            org.junit.Assert.fail("testLinearCountingSerialization_cf26838 should have thrown UnsupportedOperationException");
        } catch (java.lang.UnsupportedOperationException eee) {
        }
    }

    /* amplification of com.clearspring.analytics.stream.cardinality.TestCountThenEstimate#testLinearCountingSerialization */
    @org.junit.Test(timeout = 1000)
    public void testLinearCountingSerialization_cf26830_failAssert16() throws java.io.IOException, java.lang.ClassNotFoundException {
        // AssertGenerator generate try/catch block with fail statement
        try {
            com.clearspring.analytics.stream.cardinality.CountThenEstimate cte = new com.clearspring.analytics.stream.cardinality.CountThenEstimate(3, new com.clearspring.analytics.stream.cardinality.LinearCounting.Builder(1024));
            com.clearspring.analytics.stream.cardinality.CountThenEstimate clone = new com.clearspring.analytics.stream.cardinality.CountThenEstimate(cte.getBytes());
            assertCountThenEstimateEquals(cte, clone);
            cte.offer("1");
            cte.offer("2");
            cte.offer("3");
            // MethodAssertGenerator build local variable
            Object o_11_0 = cte.cardinality();
            clone = new com.clearspring.analytics.stream.cardinality.CountThenEstimate(cte.getBytes());
            assertCountThenEstimateEquals(cte, clone);
            cte.offer("4");
            clone = new com.clearspring.analytics.stream.cardinality.CountThenEstimate(cte.getBytes());
            assertCountThenEstimateEquals(cte, clone);
            // StatementAdderOnAssert create random local variable
            int vc_9866 = -1915055781;
            // StatementAdderMethod cloned existing statement
            clone.offerHashed(vc_9866);
            org.junit.Assert.fail("testLinearCountingSerialization_cf26830 should have thrown UnsupportedOperationException");
        } catch (java.lang.UnsupportedOperationException eee) {
        }
    }

    /* amplification of com.clearspring.analytics.stream.cardinality.TestCountThenEstimate#testMerge */
    @org.junit.Test(timeout = 1000)
    public void testMerge_cf29849() throws com.clearspring.analytics.stream.cardinality.CardinalityMergeException {
        int numToMerge = 10;
        int tippingPoint = 100;
        int cardinality = 1000;
        com.clearspring.analytics.stream.cardinality.CountThenEstimate[] ctes = new com.clearspring.analytics.stream.cardinality.CountThenEstimate[numToMerge];
        for (int i = 0; i < numToMerge; i++) {
            ctes[i] = new com.clearspring.analytics.stream.cardinality.CountThenEstimate(tippingPoint, com.clearspring.analytics.stream.cardinality.AdaptiveCounting.Builder.obyCount(100000));
            for (int j = 0; j < (tippingPoint - 1); j++) {
                ctes[i].offer(java.lang.Math.random());
            }
        }
        int expectedCardinality = numToMerge * (tippingPoint - 1);
        long mergedEstimate = com.clearspring.analytics.stream.cardinality.CountThenEstimate.mergeEstimators(ctes).cardinality();
        double error = (java.lang.Math.abs((mergedEstimate - expectedCardinality))) / ((double) (expectedCardinality));
        org.junit.Assert.assertEquals(0.01, error, 0.01);
        for (int i = 0; i < (numToMerge / 2); i++) {
            for (int j = tippingPoint - 1; j < cardinality; j++) {
                ctes[i].offer(java.lang.Math.random());
            }
        }
        expectedCardinality = (numToMerge / 2) * ((cardinality + tippingPoint) - 1);
        mergedEstimate = com.clearspring.analytics.stream.cardinality.CountThenEstimate.mergeEstimators(ctes).cardinality();
        error = (java.lang.Math.abs((mergedEstimate - expectedCardinality))) / ((double) (expectedCardinality));
        org.junit.Assert.assertEquals(0.01, error, 0.01);
        for (int i = numToMerge / 2; i < numToMerge; i++) {
            for (int j = tippingPoint - 1; j < cardinality; j++) {
                ctes[i].offer(java.lang.Math.random());
            }
        }
        expectedCardinality = numToMerge * cardinality;
        mergedEstimate = com.clearspring.analytics.stream.cardinality.CountThenEstimate.mergeEstimators(ctes).cardinality();
        error = (java.lang.Math.abs((mergedEstimate - expectedCardinality))) / ((double) (expectedCardinality));
        // StatementAdderOnAssert create random local variable
        java.lang.Object vc_9897 = new java.lang.Object();
        // StatementAdderOnAssert create random local variable
        com.clearspring.analytics.stream.cardinality.CountThenEstimate vc_9895 = new com.clearspring.analytics.stream.cardinality.CountThenEstimate();
        // AssertGenerator add assertion
        junit.framework.Assert.assertFalse(((com.clearspring.analytics.stream.cardinality.CountThenEstimate)vc_9895).tipped());
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((com.clearspring.analytics.stream.cardinality.CountThenEstimate)vc_9895).sizeof(), -1);
        // AssertGenerator replace invocation
        boolean o_testMerge_cf29849__62 = // StatementAdderMethod cloned existing statement
vc_9895.offer(vc_9897);
        // AssertGenerator add assertion
        junit.framework.Assert.assertTrue(o_testMerge_cf29849__62);
        org.junit.Assert.assertEquals(0.01, error, 0.01);
    }

    /* amplification of com.clearspring.analytics.stream.cardinality.TestCountThenEstimate#testMerge */
    @org.junit.Test(timeout = 1000)
    public void testMerge_cf29846() throws com.clearspring.analytics.stream.cardinality.CardinalityMergeException {
        int numToMerge = 10;
        int tippingPoint = 100;
        int cardinality = 1000;
        com.clearspring.analytics.stream.cardinality.CountThenEstimate[] ctes = new com.clearspring.analytics.stream.cardinality.CountThenEstimate[numToMerge];
        for (int i = 0; i < numToMerge; i++) {
            ctes[i] = new com.clearspring.analytics.stream.cardinality.CountThenEstimate(tippingPoint, com.clearspring.analytics.stream.cardinality.AdaptiveCounting.Builder.obyCount(100000));
            for (int j = 0; j < (tippingPoint - 1); j++) {
                ctes[i].offer(java.lang.Math.random());
            }
        }
        int expectedCardinality = numToMerge * (tippingPoint - 1);
        long mergedEstimate = com.clearspring.analytics.stream.cardinality.CountThenEstimate.mergeEstimators(ctes).cardinality();
        double error = (java.lang.Math.abs((mergedEstimate - expectedCardinality))) / ((double) (expectedCardinality));
        org.junit.Assert.assertEquals(0.01, error, 0.01);
        for (int i = 0; i < (numToMerge / 2); i++) {
            for (int j = tippingPoint - 1; j < cardinality; j++) {
                ctes[i].offer(java.lang.Math.random());
            }
        }
        expectedCardinality = (numToMerge / 2) * ((cardinality + tippingPoint) - 1);
        mergedEstimate = com.clearspring.analytics.stream.cardinality.CountThenEstimate.mergeEstimators(ctes).cardinality();
        error = (java.lang.Math.abs((mergedEstimate - expectedCardinality))) / ((double) (expectedCardinality));
        org.junit.Assert.assertEquals(0.01, error, 0.01);
        for (int i = numToMerge / 2; i < numToMerge; i++) {
            for (int j = tippingPoint - 1; j < cardinality; j++) {
                ctes[i].offer(java.lang.Math.random());
            }
        }
        expectedCardinality = numToMerge * cardinality;
        mergedEstimate = com.clearspring.analytics.stream.cardinality.CountThenEstimate.mergeEstimators(ctes).cardinality();
        error = (java.lang.Math.abs((mergedEstimate - expectedCardinality))) / ((double) (expectedCardinality));
        // StatementAdderOnAssert create null value
        java.lang.Object vc_9896 = (java.lang.Object)null;
        // AssertGenerator add assertion
        junit.framework.Assert.assertNull(vc_9896);
        // StatementAddOnAssert local variable replacement
        com.clearspring.analytics.stream.cardinality.CountThenEstimate merged = com.clearspring.analytics.stream.cardinality.CountThenEstimate.mergeEstimators(ctes);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((com.clearspring.analytics.stream.cardinality.CountThenEstimate)merged).sizeof(), 3342);
        // AssertGenerator add assertion
        junit.framework.Assert.assertTrue(((com.clearspring.analytics.stream.cardinality.CountThenEstimate)merged).tipped());
        // StatementAdderMethod cloned existing statement
        merged.offer(vc_9896);
        org.junit.Assert.assertEquals(0.01, error, 0.01);
    }

    /* amplification of com.clearspring.analytics.stream.cardinality.TestCountThenEstimate#testMerge */
    @org.junit.Test(timeout = 1000)
    public void testMerge_cf29847() throws com.clearspring.analytics.stream.cardinality.CardinalityMergeException {
        int numToMerge = 10;
        int tippingPoint = 100;
        int cardinality = 1000;
        com.clearspring.analytics.stream.cardinality.CountThenEstimate[] ctes = new com.clearspring.analytics.stream.cardinality.CountThenEstimate[numToMerge];
        for (int i = 0; i < numToMerge; i++) {
            ctes[i] = new com.clearspring.analytics.stream.cardinality.CountThenEstimate(tippingPoint, com.clearspring.analytics.stream.cardinality.AdaptiveCounting.Builder.obyCount(100000));
            for (int j = 0; j < (tippingPoint - 1); j++) {
                ctes[i].offer(java.lang.Math.random());
            }
        }
        int expectedCardinality = numToMerge * (tippingPoint - 1);
        long mergedEstimate = com.clearspring.analytics.stream.cardinality.CountThenEstimate.mergeEstimators(ctes).cardinality();
        double error = (java.lang.Math.abs((mergedEstimate - expectedCardinality))) / ((double) (expectedCardinality));
        org.junit.Assert.assertEquals(0.01, error, 0.01);
        for (int i = 0; i < (numToMerge / 2); i++) {
            for (int j = tippingPoint - 1; j < cardinality; j++) {
                ctes[i].offer(java.lang.Math.random());
            }
        }
        expectedCardinality = (numToMerge / 2) * ((cardinality + tippingPoint) - 1);
        mergedEstimate = com.clearspring.analytics.stream.cardinality.CountThenEstimate.mergeEstimators(ctes).cardinality();
        error = (java.lang.Math.abs((mergedEstimate - expectedCardinality))) / ((double) (expectedCardinality));
        org.junit.Assert.assertEquals(0.01, error, 0.01);
        for (int i = numToMerge / 2; i < numToMerge; i++) {
            for (int j = tippingPoint - 1; j < cardinality; j++) {
                ctes[i].offer(java.lang.Math.random());
            }
        }
        expectedCardinality = numToMerge * cardinality;
        mergedEstimate = com.clearspring.analytics.stream.cardinality.CountThenEstimate.mergeEstimators(ctes).cardinality();
        error = (java.lang.Math.abs((mergedEstimate - expectedCardinality))) / ((double) (expectedCardinality));
        // StatementAdderOnAssert create random local variable
        java.lang.Object vc_9897 = new java.lang.Object();
        // StatementAddOnAssert local variable replacement
        com.clearspring.analytics.stream.cardinality.CountThenEstimate merged = com.clearspring.analytics.stream.cardinality.CountThenEstimate.mergeEstimators(ctes);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((com.clearspring.analytics.stream.cardinality.CountThenEstimate)merged).sizeof(), 3342);
        // AssertGenerator add assertion
        junit.framework.Assert.assertTrue(((com.clearspring.analytics.stream.cardinality.CountThenEstimate)merged).tipped());
        // StatementAdderMethod cloned existing statement
        merged.offer(vc_9897);
        org.junit.Assert.assertEquals(0.01, error, 0.01);
    }

    /* amplification of com.clearspring.analytics.stream.cardinality.TestCountThenEstimate#testMerge */
    @org.junit.Test(timeout = 1000)
    public void testMerge_cf29854_failAssert49() throws com.clearspring.analytics.stream.cardinality.CardinalityMergeException {
        // AssertGenerator generate try/catch block with fail statement
        try {
            int numToMerge = 10;
            int tippingPoint = 100;
            int cardinality = 1000;
            com.clearspring.analytics.stream.cardinality.CountThenEstimate[] ctes = new com.clearspring.analytics.stream.cardinality.CountThenEstimate[numToMerge];
            for (int i = 0; i < numToMerge; i++) {
                ctes[i] = new com.clearspring.analytics.stream.cardinality.CountThenEstimate(tippingPoint, com.clearspring.analytics.stream.cardinality.AdaptiveCounting.Builder.obyCount(100000));
                for (int j = 0; j < (tippingPoint - 1); j++) {
                    ctes[i].offer(java.lang.Math.random());
                }
            }
            int expectedCardinality = numToMerge * (tippingPoint - 1);
            long mergedEstimate = com.clearspring.analytics.stream.cardinality.CountThenEstimate.mergeEstimators(ctes).cardinality();
            double error = (java.lang.Math.abs((mergedEstimate - expectedCardinality))) / ((double) (expectedCardinality));
            for (int i = 0; i < (numToMerge / 2); i++) {
                for (int j = tippingPoint - 1; j < cardinality; j++) {
                    ctes[i].offer(java.lang.Math.random());
                }
            }
            expectedCardinality = (numToMerge / 2) * ((cardinality + tippingPoint) - 1);
            mergedEstimate = com.clearspring.analytics.stream.cardinality.CountThenEstimate.mergeEstimators(ctes).cardinality();
            error = (java.lang.Math.abs((mergedEstimate - expectedCardinality))) / ((double) (expectedCardinality));
            for (int i = numToMerge / 2; i < numToMerge; i++) {
                for (int j = tippingPoint - 1; j < cardinality; j++) {
                    ctes[i].offer(java.lang.Math.random());
                }
            }
            expectedCardinality = numToMerge * cardinality;
            mergedEstimate = com.clearspring.analytics.stream.cardinality.CountThenEstimate.mergeEstimators(ctes).cardinality();
            error = (java.lang.Math.abs((mergedEstimate - expectedCardinality))) / ((double) (expectedCardinality));
            // StatementAddOnAssert local variable replacement
            com.clearspring.analytics.stream.cardinality.CountThenEstimate merged = com.clearspring.analytics.stream.cardinality.CountThenEstimate.mergeEstimators(ctes);
            // StatementAdderMethod cloned existing statement
            merged.offerHashed(expectedCardinality);
            org.junit.Assert.fail("testMerge_cf29854 should have thrown UnsupportedOperationException");
        } catch (java.lang.UnsupportedOperationException eee) {
        }
    }

    /* amplification of com.clearspring.analytics.stream.cardinality.TestCountThenEstimate#testMerge */
    @org.junit.Test(timeout = 1000)
    public void testMerge_cf29865_failAssert57() throws com.clearspring.analytics.stream.cardinality.CardinalityMergeException {
        // AssertGenerator generate try/catch block with fail statement
        try {
            int numToMerge = 10;
            int tippingPoint = 100;
            int cardinality = 1000;
            com.clearspring.analytics.stream.cardinality.CountThenEstimate[] ctes = new com.clearspring.analytics.stream.cardinality.CountThenEstimate[numToMerge];
            for (int i = 0; i < numToMerge; i++) {
                ctes[i] = new com.clearspring.analytics.stream.cardinality.CountThenEstimate(tippingPoint, com.clearspring.analytics.stream.cardinality.AdaptiveCounting.Builder.obyCount(100000));
                for (int j = 0; j < (tippingPoint - 1); j++) {
                    ctes[i].offer(java.lang.Math.random());
                }
            }
            int expectedCardinality = numToMerge * (tippingPoint - 1);
            long mergedEstimate = com.clearspring.analytics.stream.cardinality.CountThenEstimate.mergeEstimators(ctes).cardinality();
            double error = (java.lang.Math.abs((mergedEstimate - expectedCardinality))) / ((double) (expectedCardinality));
            for (int i = 0; i < (numToMerge / 2); i++) {
                for (int j = tippingPoint - 1; j < cardinality; j++) {
                    ctes[i].offer(java.lang.Math.random());
                }
            }
            expectedCardinality = (numToMerge / 2) * ((cardinality + tippingPoint) - 1);
            mergedEstimate = com.clearspring.analytics.stream.cardinality.CountThenEstimate.mergeEstimators(ctes).cardinality();
            error = (java.lang.Math.abs((mergedEstimate - expectedCardinality))) / ((double) (expectedCardinality));
            for (int i = numToMerge / 2; i < numToMerge; i++) {
                for (int j = tippingPoint - 1; j < cardinality; j++) {
                    ctes[i].offer(java.lang.Math.random());
                }
            }
            expectedCardinality = numToMerge * cardinality;
            mergedEstimate = com.clearspring.analytics.stream.cardinality.CountThenEstimate.mergeEstimators(ctes).cardinality();
            error = (java.lang.Math.abs((mergedEstimate - expectedCardinality))) / ((double) (expectedCardinality));
            // StatementAddOnAssert local variable replacement
            com.clearspring.analytics.stream.cardinality.CountThenEstimate merged = com.clearspring.analytics.stream.cardinality.CountThenEstimate.mergeEstimators(ctes);
            // StatementAdderMethod cloned existing statement
            merged.offerHashed(mergedEstimate);
            org.junit.Assert.fail("testMerge_cf29865 should have thrown UnsupportedOperationException");
        } catch (java.lang.UnsupportedOperationException eee) {
        }
    }

    /* amplification of com.clearspring.analytics.stream.cardinality.TestCountThenEstimate#testMerge */
    @org.junit.Test
    public void testMerge_literalMutation29760_failAssert3() throws com.clearspring.analytics.stream.cardinality.CardinalityMergeException {
        // AssertGenerator generate try/catch block with fail statement
        try {
            int numToMerge = 0;
            int tippingPoint = 100;
            int cardinality = 1000;
            com.clearspring.analytics.stream.cardinality.CountThenEstimate[] ctes = new com.clearspring.analytics.stream.cardinality.CountThenEstimate[numToMerge];
            for (int i = 0; i < numToMerge; i++) {
                ctes[i] = new com.clearspring.analytics.stream.cardinality.CountThenEstimate(tippingPoint, com.clearspring.analytics.stream.cardinality.AdaptiveCounting.Builder.obyCount(100000));
                for (int j = 0; j < (tippingPoint - 1); j++) {
                    ctes[i].offer(java.lang.Math.random());
                }
            }
            int expectedCardinality = numToMerge * (tippingPoint - 1);
            long mergedEstimate = com.clearspring.analytics.stream.cardinality.CountThenEstimate.mergeEstimators(ctes).cardinality();
            double error = (java.lang.Math.abs((mergedEstimate - expectedCardinality))) / ((double) (expectedCardinality));
            for (int i = 0; i < (numToMerge / 2); i++) {
                for (int j = tippingPoint - 1; j < cardinality; j++) {
                    ctes[i].offer(java.lang.Math.random());
                }
            }
            expectedCardinality = (numToMerge / 2) * ((cardinality + tippingPoint) - 1);
            mergedEstimate = com.clearspring.analytics.stream.cardinality.CountThenEstimate.mergeEstimators(ctes).cardinality();
            error = (java.lang.Math.abs((mergedEstimate - expectedCardinality))) / ((double) (expectedCardinality));
            for (int i = numToMerge / 2; i < numToMerge; i++) {
                for (int j = tippingPoint - 1; j < cardinality; j++) {
                    ctes[i].offer(java.lang.Math.random());
                }
            }
            expectedCardinality = numToMerge * cardinality;
            mergedEstimate = com.clearspring.analytics.stream.cardinality.CountThenEstimate.mergeEstimators(ctes).cardinality();
            error = (java.lang.Math.abs((mergedEstimate - expectedCardinality))) / ((double) (expectedCardinality));
            org.junit.Assert.fail("testMerge_literalMutation29760 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    /* amplification of com.clearspring.analytics.stream.cardinality.TestCountThenEstimate#testMerge */
    @org.junit.Test(timeout = 1000)
    public void testMerge_cf29877() throws com.clearspring.analytics.stream.cardinality.CardinalityMergeException {
        int numToMerge = 10;
        int tippingPoint = 100;
        int cardinality = 1000;
        com.clearspring.analytics.stream.cardinality.CountThenEstimate[] ctes = new com.clearspring.analytics.stream.cardinality.CountThenEstimate[numToMerge];
        for (int i = 0; i < numToMerge; i++) {
            ctes[i] = new com.clearspring.analytics.stream.cardinality.CountThenEstimate(tippingPoint, com.clearspring.analytics.stream.cardinality.AdaptiveCounting.Builder.obyCount(100000));
            for (int j = 0; j < (tippingPoint - 1); j++) {
                ctes[i].offer(java.lang.Math.random());
            }
        }
        int expectedCardinality = numToMerge * (tippingPoint - 1);
        long mergedEstimate = com.clearspring.analytics.stream.cardinality.CountThenEstimate.mergeEstimators(ctes).cardinality();
        double error = (java.lang.Math.abs((mergedEstimate - expectedCardinality))) / ((double) (expectedCardinality));
        org.junit.Assert.assertEquals(0.01, error, 0.01);
        for (int i = 0; i < (numToMerge / 2); i++) {
            for (int j = tippingPoint - 1; j < cardinality; j++) {
                ctes[i].offer(java.lang.Math.random());
            }
        }
        expectedCardinality = (numToMerge / 2) * ((cardinality + tippingPoint) - 1);
        mergedEstimate = com.clearspring.analytics.stream.cardinality.CountThenEstimate.mergeEstimators(ctes).cardinality();
        error = (java.lang.Math.abs((mergedEstimate - expectedCardinality))) / ((double) (expectedCardinality));
        org.junit.Assert.assertEquals(0.01, error, 0.01);
        for (int i = numToMerge / 2; i < numToMerge; i++) {
            for (int j = tippingPoint - 1; j < cardinality; j++) {
                ctes[i].offer(java.lang.Math.random());
            }
        }
        expectedCardinality = numToMerge * cardinality;
        mergedEstimate = com.clearspring.analytics.stream.cardinality.CountThenEstimate.mergeEstimators(ctes).cardinality();
        error = (java.lang.Math.abs((mergedEstimate - expectedCardinality))) / ((double) (expectedCardinality));
        // StatementAdderOnAssert create null value
        com.clearspring.analytics.stream.cardinality.CountThenEstimate[] vc_9910 = (com.clearspring.analytics.stream.cardinality.CountThenEstimate[])null;
        // AssertGenerator add assertion
        junit.framework.Assert.assertNull(vc_9910);
        // StatementAdderOnAssert create null value
        com.clearspring.analytics.stream.cardinality.CountThenEstimate vc_9908 = (com.clearspring.analytics.stream.cardinality.CountThenEstimate)null;
        // AssertGenerator add assertion
        junit.framework.Assert.assertNull(vc_9908);
        // AssertGenerator replace invocation
        com.clearspring.analytics.stream.cardinality.CountThenEstimate o_testMerge_cf29877__62 = // StatementAdderMethod cloned existing statement
vc_9908.mergeEstimators(vc_9910);
        // AssertGenerator add assertion
        junit.framework.Assert.assertNull(o_testMerge_cf29877__62);
        org.junit.Assert.assertEquals(0.01, error, 0.01);
    }

    /* amplification of com.clearspring.analytics.stream.cardinality.TestCountThenEstimate#testMerge */
    @org.junit.Test(timeout = 1000)
    public void testMerge_cf29889() throws com.clearspring.analytics.stream.cardinality.CardinalityMergeException {
        int numToMerge = 10;
        int tippingPoint = 100;
        int cardinality = 1000;
        com.clearspring.analytics.stream.cardinality.CountThenEstimate[] ctes = new com.clearspring.analytics.stream.cardinality.CountThenEstimate[numToMerge];
        for (int i = 0; i < numToMerge; i++) {
            ctes[i] = new com.clearspring.analytics.stream.cardinality.CountThenEstimate(tippingPoint, com.clearspring.analytics.stream.cardinality.AdaptiveCounting.Builder.obyCount(100000));
            for (int j = 0; j < (tippingPoint - 1); j++) {
                ctes[i].offer(java.lang.Math.random());
            }
        }
        int expectedCardinality = numToMerge * (tippingPoint - 1);
        long mergedEstimate = com.clearspring.analytics.stream.cardinality.CountThenEstimate.mergeEstimators(ctes).cardinality();
        double error = (java.lang.Math.abs((mergedEstimate - expectedCardinality))) / ((double) (expectedCardinality));
        org.junit.Assert.assertEquals(0.01, error, 0.01);
        for (int i = 0; i < (numToMerge / 2); i++) {
            for (int j = tippingPoint - 1; j < cardinality; j++) {
                ctes[i].offer(java.lang.Math.random());
            }
        }
        expectedCardinality = (numToMerge / 2) * ((cardinality + tippingPoint) - 1);
        mergedEstimate = com.clearspring.analytics.stream.cardinality.CountThenEstimate.mergeEstimators(ctes).cardinality();
        error = (java.lang.Math.abs((mergedEstimate - expectedCardinality))) / ((double) (expectedCardinality));
        org.junit.Assert.assertEquals(0.01, error, 0.01);
        for (int i = numToMerge / 2; i < numToMerge; i++) {
            for (int j = tippingPoint - 1; j < cardinality; j++) {
                ctes[i].offer(java.lang.Math.random());
            }
        }
        expectedCardinality = numToMerge * cardinality;
        mergedEstimate = com.clearspring.analytics.stream.cardinality.CountThenEstimate.mergeEstimators(ctes).cardinality();
        error = (java.lang.Math.abs((mergedEstimate - expectedCardinality))) / ((double) (expectedCardinality));
        // StatementAdderOnAssert create null value
        com.clearspring.analytics.stream.cardinality.ICardinality[] vc_9914 = (com.clearspring.analytics.stream.cardinality.ICardinality[])null;
        // AssertGenerator add assertion
        junit.framework.Assert.assertNull(vc_9914);
        // StatementAddOnAssert local variable replacement
        com.clearspring.analytics.stream.cardinality.CountThenEstimate merged = com.clearspring.analytics.stream.cardinality.CountThenEstimate.mergeEstimators(ctes);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((com.clearspring.analytics.stream.cardinality.CountThenEstimate)merged).sizeof(), 3342);
        // AssertGenerator add assertion
        junit.framework.Assert.assertTrue(((com.clearspring.analytics.stream.cardinality.CountThenEstimate)merged).tipped());
        // AssertGenerator replace invocation
        com.clearspring.analytics.stream.cardinality.ICardinality o_testMerge_cf29889__63 = // StatementAdderMethod cloned existing statement
merged.merge(vc_9914);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((com.clearspring.analytics.stream.cardinality.CountThenEstimate)o_testMerge_cf29889__63).sizeof(), 3342);
        // AssertGenerator add assertion
        junit.framework.Assert.assertTrue(((com.clearspring.analytics.stream.cardinality.CountThenEstimate)o_testMerge_cf29889__63).tipped());
        org.junit.Assert.assertEquals(0.01, error, 0.01);
    }

    /* amplification of com.clearspring.analytics.stream.cardinality.TestCountThenEstimate#testMerge */
    @org.junit.Test(timeout = 1000)
    public void testMerge_cf29878() throws com.clearspring.analytics.stream.cardinality.CardinalityMergeException {
        int numToMerge = 10;
        int tippingPoint = 100;
        int cardinality = 1000;
        com.clearspring.analytics.stream.cardinality.CountThenEstimate[] ctes = new com.clearspring.analytics.stream.cardinality.CountThenEstimate[numToMerge];
        for (int i = 0; i < numToMerge; i++) {
            ctes[i] = new com.clearspring.analytics.stream.cardinality.CountThenEstimate(tippingPoint, com.clearspring.analytics.stream.cardinality.AdaptiveCounting.Builder.obyCount(100000));
            for (int j = 0; j < (tippingPoint - 1); j++) {
                ctes[i].offer(java.lang.Math.random());
            }
        }
        int expectedCardinality = numToMerge * (tippingPoint - 1);
        long mergedEstimate = com.clearspring.analytics.stream.cardinality.CountThenEstimate.mergeEstimators(ctes).cardinality();
        double error = (java.lang.Math.abs((mergedEstimate - expectedCardinality))) / ((double) (expectedCardinality));
        org.junit.Assert.assertEquals(0.01, error, 0.01);
        for (int i = 0; i < (numToMerge / 2); i++) {
            for (int j = tippingPoint - 1; j < cardinality; j++) {
                ctes[i].offer(java.lang.Math.random());
            }
        }
        expectedCardinality = (numToMerge / 2) * ((cardinality + tippingPoint) - 1);
        mergedEstimate = com.clearspring.analytics.stream.cardinality.CountThenEstimate.mergeEstimators(ctes).cardinality();
        error = (java.lang.Math.abs((mergedEstimate - expectedCardinality))) / ((double) (expectedCardinality));
        org.junit.Assert.assertEquals(0.01, error, 0.01);
        for (int i = numToMerge / 2; i < numToMerge; i++) {
            for (int j = tippingPoint - 1; j < cardinality; j++) {
                ctes[i].offer(java.lang.Math.random());
            }
        }
        expectedCardinality = numToMerge * cardinality;
        mergedEstimate = com.clearspring.analytics.stream.cardinality.CountThenEstimate.mergeEstimators(ctes).cardinality();
        error = (java.lang.Math.abs((mergedEstimate - expectedCardinality))) / ((double) (expectedCardinality));
        // StatementAdderOnAssert create null value
        com.clearspring.analytics.stream.cardinality.CountThenEstimate vc_9908 = (com.clearspring.analytics.stream.cardinality.CountThenEstimate)null;
        // AssertGenerator add assertion
        junit.framework.Assert.assertNull(vc_9908);
        // AssertGenerator replace invocation
        com.clearspring.analytics.stream.cardinality.CountThenEstimate o_testMerge_cf29878__60 = // StatementAdderMethod cloned existing statement
vc_9908.mergeEstimators(ctes);
        // AssertGenerator add assertion
        junit.framework.Assert.assertTrue(((com.clearspring.analytics.stream.cardinality.CountThenEstimate)o_testMerge_cf29878__60).tipped());
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((com.clearspring.analytics.stream.cardinality.CountThenEstimate)o_testMerge_cf29878__60).sizeof(), 3342);
        org.junit.Assert.assertEquals(0.01, error, 0.01);
    }

    /* amplification of com.clearspring.analytics.stream.cardinality.TestCountThenEstimate#testMerge */
    @org.junit.Test(timeout = 1000)
    public void testMerge_cf29872_literalMutation36459() throws com.clearspring.analytics.stream.cardinality.CardinalityMergeException {
        int numToMerge = 10;
        int tippingPoint = 100;
        int cardinality = 1000;
        com.clearspring.analytics.stream.cardinality.CountThenEstimate[] ctes = new com.clearspring.analytics.stream.cardinality.CountThenEstimate[numToMerge];
        for (int i = 0; i < numToMerge; i++) {
            ctes[i] = new com.clearspring.analytics.stream.cardinality.CountThenEstimate(tippingPoint, com.clearspring.analytics.stream.cardinality.AdaptiveCounting.Builder.obyCount(100000));
            for (int j = 0; j < (tippingPoint - 1); j++) {
                ctes[i].offer(java.lang.Math.random());
            }
        }
        int expectedCardinality = numToMerge * (tippingPoint - 0);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(expectedCardinality, 1000);
        long mergedEstimate = com.clearspring.analytics.stream.cardinality.CountThenEstimate.mergeEstimators(ctes).cardinality();
        double error = (java.lang.Math.abs((mergedEstimate - expectedCardinality))) / ((double) (expectedCardinality));
        org.junit.Assert.assertEquals(0.01, error, 0.01);
        for (int i = 0; i < (numToMerge / 2); i++) {
            for (int j = tippingPoint - 1; j < cardinality; j++) {
                ctes[i].offer(java.lang.Math.random());
            }
        }
        expectedCardinality = (numToMerge / 2) * ((cardinality + tippingPoint) - 1);
        mergedEstimate = com.clearspring.analytics.stream.cardinality.CountThenEstimate.mergeEstimators(ctes).cardinality();
        error = (java.lang.Math.abs((mergedEstimate - expectedCardinality))) / ((double) (expectedCardinality));
        org.junit.Assert.assertEquals(0.01, error, 0.01);
        for (int i = numToMerge / 2; i < numToMerge; i++) {
            for (int j = tippingPoint - 1; j < cardinality; j++) {
                ctes[i].offer(java.lang.Math.random());
            }
        }
        expectedCardinality = numToMerge * cardinality;
        mergedEstimate = com.clearspring.analytics.stream.cardinality.CountThenEstimate.mergeEstimators(ctes).cardinality();
        error = (java.lang.Math.abs((mergedEstimate - expectedCardinality))) / ((double) (expectedCardinality));
        // StatementAddOnAssert local variable replacement
        com.clearspring.analytics.stream.cardinality.CountThenEstimate merged = com.clearspring.analytics.stream.cardinality.CountThenEstimate.mergeEstimators(ctes);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((com.clearspring.analytics.stream.cardinality.CountThenEstimate)merged).sizeof(), 3342);
        // AssertGenerator add assertion
        junit.framework.Assert.assertTrue(((com.clearspring.analytics.stream.cardinality.CountThenEstimate)merged).tipped());
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((com.clearspring.analytics.stream.cardinality.CountThenEstimate)merged).sizeof(), 3342);
        // AssertGenerator add assertion
        junit.framework.Assert.assertTrue(((com.clearspring.analytics.stream.cardinality.CountThenEstimate)merged).tipped());
        // AssertGenerator replace invocation
        boolean o_testMerge_cf29872__61 = // StatementAdderMethod cloned existing statement
merged.tipped();
        // AssertGenerator add assertion
        junit.framework.Assert.assertTrue(o_testMerge_cf29872__61);
        org.junit.Assert.assertEquals(0.01, error, 0.01);
    }

    /* amplification of com.clearspring.analytics.stream.cardinality.TestCountThenEstimate#testMerge */
    @org.junit.Test(timeout = 1000)
    public void testMerge_cf29877_literalMutation36635() throws com.clearspring.analytics.stream.cardinality.CardinalityMergeException {
        int numToMerge = 10;
        int tippingPoint = 100;
        int cardinality = 1000;
        com.clearspring.analytics.stream.cardinality.CountThenEstimate[] ctes = new com.clearspring.analytics.stream.cardinality.CountThenEstimate[numToMerge];
        for (int i = 0; i < numToMerge; i++) {
            ctes[i] = new com.clearspring.analytics.stream.cardinality.CountThenEstimate(tippingPoint, com.clearspring.analytics.stream.cardinality.AdaptiveCounting.Builder.obyCount(100000));
            for (int j = 0; j < (tippingPoint - 0); j++) {
                ctes[i].offer(java.lang.Math.random());
            }
        }
        int expectedCardinality = numToMerge * (tippingPoint - 1);
        long mergedEstimate = com.clearspring.analytics.stream.cardinality.CountThenEstimate.mergeEstimators(ctes).cardinality();
        double error = (java.lang.Math.abs((mergedEstimate - expectedCardinality))) / ((double) (expectedCardinality));
        org.junit.Assert.assertEquals(0.01, error, 0.01);
        for (int i = 0; i < (numToMerge / 2); i++) {
            for (int j = tippingPoint - 1; j < cardinality; j++) {
                ctes[i].offer(java.lang.Math.random());
            }
        }
        expectedCardinality = (numToMerge / 2) * ((cardinality + tippingPoint) - 1);
        mergedEstimate = com.clearspring.analytics.stream.cardinality.CountThenEstimate.mergeEstimators(ctes).cardinality();
        error = (java.lang.Math.abs((mergedEstimate - expectedCardinality))) / ((double) (expectedCardinality));
        org.junit.Assert.assertEquals(0.01, error, 0.01);
        for (int i = numToMerge / 2; i < numToMerge; i++) {
            for (int j = tippingPoint - 1; j < cardinality; j++) {
                ctes[i].offer(java.lang.Math.random());
            }
        }
        expectedCardinality = numToMerge * cardinality;
        mergedEstimate = com.clearspring.analytics.stream.cardinality.CountThenEstimate.mergeEstimators(ctes).cardinality();
        error = (java.lang.Math.abs((mergedEstimate - expectedCardinality))) / ((double) (expectedCardinality));
        // StatementAdderOnAssert create null value
        com.clearspring.analytics.stream.cardinality.CountThenEstimate[] vc_9910 = (com.clearspring.analytics.stream.cardinality.CountThenEstimate[])null;
        // AssertGenerator add assertion
        junit.framework.Assert.assertNull(vc_9910);
        // AssertGenerator add assertion
        junit.framework.Assert.assertNull(vc_9910);
        // StatementAdderOnAssert create null value
        com.clearspring.analytics.stream.cardinality.CountThenEstimate vc_9908 = (com.clearspring.analytics.stream.cardinality.CountThenEstimate)null;
        // AssertGenerator add assertion
        junit.framework.Assert.assertNull(vc_9908);
        // AssertGenerator add assertion
        junit.framework.Assert.assertNull(vc_9908);
        // AssertGenerator replace invocation
        com.clearspring.analytics.stream.cardinality.CountThenEstimate o_testMerge_cf29877__62 = // StatementAdderMethod cloned existing statement
vc_9908.mergeEstimators(vc_9910);
        // AssertGenerator add assertion
        junit.framework.Assert.assertNull(o_testMerge_cf29877__62);
        org.junit.Assert.assertEquals(0.01, error, 0.01);
    }

    /* amplification of com.clearspring.analytics.stream.cardinality.TestCountThenEstimate#testMerge */
    @org.junit.Test
    public void testMerge_literalMutation29771_literalMutation31123() throws com.clearspring.analytics.stream.cardinality.CardinalityMergeException {
        int numToMerge = 10;
        int tippingPoint = 100;
        int cardinality = 1001;
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(cardinality, 1001);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(cardinality, 1001);
        com.clearspring.analytics.stream.cardinality.CountThenEstimate[] ctes = new com.clearspring.analytics.stream.cardinality.CountThenEstimate[numToMerge];
        for (int i = 0; i < numToMerge; i++) {
            ctes[i] = new com.clearspring.analytics.stream.cardinality.CountThenEstimate(tippingPoint, com.clearspring.analytics.stream.cardinality.AdaptiveCounting.Builder.obyCount(100000));
            for (int j = 0; j < (tippingPoint - 1); j++) {
                ctes[i].offer(java.lang.Math.random());
            }
        }
        int expectedCardinality = numToMerge * (tippingPoint - 0);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(expectedCardinality, 1000);
        long mergedEstimate = com.clearspring.analytics.stream.cardinality.CountThenEstimate.mergeEstimators(ctes).cardinality();
        double error = (java.lang.Math.abs((mergedEstimate - expectedCardinality))) / ((double) (expectedCardinality));
        org.junit.Assert.assertEquals(0.01, error, 0.01);
        for (int i = 0; i < (numToMerge / 2); i++) {
            for (int j = tippingPoint - 1; j < cardinality; j++) {
                ctes[i].offer(java.lang.Math.random());
            }
        }
        expectedCardinality = (numToMerge / 2) * ((cardinality + tippingPoint) - 1);
        mergedEstimate = com.clearspring.analytics.stream.cardinality.CountThenEstimate.mergeEstimators(ctes).cardinality();
        error = (java.lang.Math.abs((mergedEstimate - expectedCardinality))) / ((double) (expectedCardinality));
        org.junit.Assert.assertEquals(0.01, error, 0.01);
        for (int i = numToMerge / 2; i < numToMerge; i++) {
            for (int j = tippingPoint - 1; j < cardinality; j++) {
                ctes[i].offer(java.lang.Math.random());
            }
        }
        expectedCardinality = numToMerge * cardinality;
        mergedEstimate = com.clearspring.analytics.stream.cardinality.CountThenEstimate.mergeEstimators(ctes).cardinality();
        error = (java.lang.Math.abs((mergedEstimate - expectedCardinality))) / ((double) (expectedCardinality));
        org.junit.Assert.assertEquals(0.01, error, 0.01);
    }

    /* amplification of com.clearspring.analytics.stream.cardinality.TestCountThenEstimate#testMerge */
    @org.junit.Test
    public void testMerge_literalMutation29773_literalMutation31483() throws com.clearspring.analytics.stream.cardinality.CardinalityMergeException {
        int numToMerge = 10;
        int tippingPoint = 100;
        int cardinality = 500;
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(cardinality, 500);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(cardinality, 500);
        com.clearspring.analytics.stream.cardinality.CountThenEstimate[] ctes = new com.clearspring.analytics.stream.cardinality.CountThenEstimate[numToMerge];
        for (int i = 0; i < numToMerge; i++) {
            ctes[i] = new com.clearspring.analytics.stream.cardinality.CountThenEstimate(tippingPoint, com.clearspring.analytics.stream.cardinality.AdaptiveCounting.Builder.obyCount(100000));
            for (int j = 0; j < (tippingPoint - 1); j++) {
                ctes[i].offer(java.lang.Math.random());
            }
        }
        int expectedCardinality = numToMerge * (tippingPoint - 1);
        long mergedEstimate = com.clearspring.analytics.stream.cardinality.CountThenEstimate.mergeEstimators(ctes).cardinality();
        double error = (java.lang.Math.abs((mergedEstimate - expectedCardinality))) / ((double) (expectedCardinality));
        org.junit.Assert.assertEquals(0.01, error, 0.01);
        for (int i = 0; i < (numToMerge / 2); i++) {
            for (int j = tippingPoint - 1; j < cardinality; j++) {
                ctes[i].offer(java.lang.Math.random());
            }
        }
        expectedCardinality = (numToMerge / 2) * ((cardinality + tippingPoint) - 1);
        mergedEstimate = com.clearspring.analytics.stream.cardinality.CountThenEstimate.mergeEstimators(ctes).cardinality();
        error = (java.lang.Math.abs((mergedEstimate - expectedCardinality))) / ((double) (expectedCardinality));
        org.junit.Assert.assertEquals(0.01, error, 0.01);
        for (int i = numToMerge / 2; i < numToMerge; i++) {
            for (int j = tippingPoint - 1; j < cardinality; j++) {
                ctes[i].offer(java.lang.Math.random());
            }
        }
        expectedCardinality = numToMerge * cardinality;
        mergedEstimate = com.clearspring.analytics.stream.cardinality.CountThenEstimate.mergeEstimators(ctes).cardinality();
        error = (java.lang.Math.abs((mergedEstimate - expectedCardinality))) / ((double) (expectedCardinality));
        org.junit.Assert.assertEquals(0.0, error, 0.01);
    }

    /* amplification of com.clearspring.analytics.stream.cardinality.TestCountThenEstimate#testMerge */
    @org.junit.Test(timeout = 1000)
    public void testMerge_cf29899_literalMutation37937() throws com.clearspring.analytics.stream.cardinality.CardinalityMergeException {
        int numToMerge = 10;
        int tippingPoint = 100;
        int cardinality = 1000;
        com.clearspring.analytics.stream.cardinality.CountThenEstimate[] ctes = new com.clearspring.analytics.stream.cardinality.CountThenEstimate[numToMerge];
        for (int i = 0; i < numToMerge; i++) {
            ctes[i] = new com.clearspring.analytics.stream.cardinality.CountThenEstimate(tippingPoint, com.clearspring.analytics.stream.cardinality.AdaptiveCounting.Builder.obyCount(100000));
            for (int j = 0; j < (tippingPoint - 1); j++) {
                ctes[i].offer(java.lang.Math.random());
            }
        }
        int expectedCardinality = numToMerge * (tippingPoint - 1);
        long mergedEstimate = com.clearspring.analytics.stream.cardinality.CountThenEstimate.mergeEstimators(ctes).cardinality();
        double error = (java.lang.Math.abs((mergedEstimate - expectedCardinality))) / ((double) (expectedCardinality));
        org.junit.Assert.assertEquals(0.01, error, 0.01);
        for (int i = 0; i < (numToMerge / 2); i++) {
            for (int j = tippingPoint - 1; j < cardinality; j++) {
                ctes[i].offer(java.lang.Math.random());
            }
        }
        expectedCardinality = (numToMerge / 2) * ((cardinality + tippingPoint) - 1);
        mergedEstimate = com.clearspring.analytics.stream.cardinality.CountThenEstimate.mergeEstimators(ctes).cardinality();
        error = (java.lang.Math.abs((mergedEstimate - expectedCardinality))) / ((double) (expectedCardinality));
        org.junit.Assert.assertEquals(0.01, error, 0.01);
        for (int i = numToMerge / 2; i < numToMerge; i++) {
            for (int j = tippingPoint - 1; j < cardinality; j++) {
                ctes[i].offer(java.lang.Math.random());
            }
        }
        expectedCardinality = numToMerge * cardinality;
        mergedEstimate = com.clearspring.analytics.stream.cardinality.CountThenEstimate.mergeEstimators(ctes).cardinality();
        error = (java.lang.Math.abs((mergedEstimate - expectedCardinality))) / ((double) (expectedCardinality));
        // StatementAddOnAssert local variable replacement
        com.clearspring.analytics.stream.cardinality.CountThenEstimate merged = com.clearspring.analytics.stream.cardinality.CountThenEstimate.mergeEstimators(ctes);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((com.clearspring.analytics.stream.cardinality.CountThenEstimate)merged).sizeof(), 3342);
        // AssertGenerator add assertion
        junit.framework.Assert.assertTrue(((com.clearspring.analytics.stream.cardinality.CountThenEstimate)merged).tipped());
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((com.clearspring.analytics.stream.cardinality.CountThenEstimate)merged).sizeof(), 3342);
        // AssertGenerator add assertion
        junit.framework.Assert.assertTrue(((com.clearspring.analytics.stream.cardinality.CountThenEstimate)merged).tipped());
        // StatementAdderMethod cloned existing statement
        merged.cardinality();
        org.junit.Assert.assertEquals(0.01, error, 0.01);
    }

    /* amplification of com.clearspring.analytics.stream.cardinality.TestCountThenEstimate#testMerge */
    @org.junit.Test(timeout = 1000)
    public void testMerge_cf29881_cf37250_failAssert7() throws com.clearspring.analytics.stream.cardinality.CardinalityMergeException {
        // AssertGenerator generate try/catch block with fail statement
        try {
            int numToMerge = 10;
            int tippingPoint = 100;
            int cardinality = 1000;
            com.clearspring.analytics.stream.cardinality.CountThenEstimate[] ctes = new com.clearspring.analytics.stream.cardinality.CountThenEstimate[numToMerge];
            for (int i = 0; i < numToMerge; i++) {
                ctes[i] = new com.clearspring.analytics.stream.cardinality.CountThenEstimate(tippingPoint, com.clearspring.analytics.stream.cardinality.AdaptiveCounting.Builder.obyCount(100000));
                for (int j = 0; j < (tippingPoint - 1); j++) {
                    ctes[i].offer(java.lang.Math.random());
                }
            }
            int expectedCardinality = numToMerge * (tippingPoint - 1);
            long mergedEstimate = com.clearspring.analytics.stream.cardinality.CountThenEstimate.mergeEstimators(ctes).cardinality();
            double error = (java.lang.Math.abs((mergedEstimate - expectedCardinality))) / ((double) (expectedCardinality));
            for (int i = 0; i < (numToMerge / 2); i++) {
                for (int j = tippingPoint - 1; j < cardinality; j++) {
                    ctes[i].offer(java.lang.Math.random());
                }
            }
            expectedCardinality = (numToMerge / 2) * ((cardinality + tippingPoint) - 1);
            mergedEstimate = com.clearspring.analytics.stream.cardinality.CountThenEstimate.mergeEstimators(ctes).cardinality();
            error = (java.lang.Math.abs((mergedEstimate - expectedCardinality))) / ((double) (expectedCardinality));
            for (int i = numToMerge / 2; i < numToMerge; i++) {
                for (int j = tippingPoint - 1; j < cardinality; j++) {
                    ctes[i].offer(java.lang.Math.random());
                }
            }
            expectedCardinality = numToMerge * cardinality;
            mergedEstimate = com.clearspring.analytics.stream.cardinality.CountThenEstimate.mergeEstimators(ctes).cardinality();
            error = (java.lang.Math.abs((mergedEstimate - expectedCardinality))) / ((double) (expectedCardinality));
            // StatementAddOnAssert local variable replacement
            com.clearspring.analytics.stream.cardinality.CountThenEstimate merged = com.clearspring.analytics.stream.cardinality.CountThenEstimate.mergeEstimators(ctes);
            // AssertGenerator add assertion
            junit.framework.Assert.assertEquals(((com.clearspring.analytics.stream.cardinality.CountThenEstimate)merged).sizeof(), 3342);
            // AssertGenerator add assertion
            junit.framework.Assert.assertTrue(((com.clearspring.analytics.stream.cardinality.CountThenEstimate)merged).tipped());
            // AssertGenerator replace invocation
            com.clearspring.analytics.stream.cardinality.CountThenEstimate o_testMerge_cf29881__61 = // StatementAdderMethod cloned existing statement
merged.mergeEstimators(ctes);
            // AssertGenerator add assertion
            junit.framework.Assert.assertTrue(((com.clearspring.analytics.stream.cardinality.CountThenEstimate)o_testMerge_cf29881__61).tipped());
            // AssertGenerator add assertion
            junit.framework.Assert.assertEquals(((com.clearspring.analytics.stream.cardinality.CountThenEstimate)o_testMerge_cf29881__61).sizeof(), 3342);
            // StatementAdderOnAssert create literal from method
            int int_vc_329 = 2;
            // StatementAdderOnAssert create random local variable
            com.clearspring.analytics.stream.cardinality.CountThenEstimate vc_10681 = new com.clearspring.analytics.stream.cardinality.CountThenEstimate();
            // StatementAdderMethod cloned existing statement
            vc_10681.offerHashed(int_vc_329);
            org.junit.Assert.fail("testMerge_cf29881_cf37250 should have thrown UnsupportedOperationException");
        } catch (java.lang.UnsupportedOperationException eee) {
        }
    }

    /* amplification of com.clearspring.analytics.stream.cardinality.TestCountThenEstimate#testMerge */
    @org.junit.Test(timeout = 1000)
    public void testMerge_cf29877_literalMutation36640() throws com.clearspring.analytics.stream.cardinality.CardinalityMergeException {
        int numToMerge = 10;
        int tippingPoint = 100;
        int cardinality = 1000;
        com.clearspring.analytics.stream.cardinality.CountThenEstimate[] ctes = new com.clearspring.analytics.stream.cardinality.CountThenEstimate[numToMerge];
        for (int i = 0; i < numToMerge; i++) {
            ctes[i] = new com.clearspring.analytics.stream.cardinality.CountThenEstimate(tippingPoint, com.clearspring.analytics.stream.cardinality.AdaptiveCounting.Builder.obyCount(100000));
            for (int j = 0; j < (tippingPoint - 1); j++) {
                ctes[i].offer(java.lang.Math.random());
            }
        }
        int expectedCardinality = numToMerge * (tippingPoint - 0);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(expectedCardinality, 1000);
        long mergedEstimate = com.clearspring.analytics.stream.cardinality.CountThenEstimate.mergeEstimators(ctes).cardinality();
        double error = (java.lang.Math.abs((mergedEstimate - expectedCardinality))) / ((double) (expectedCardinality));
        org.junit.Assert.assertEquals(0.01, error, 0.01);
        for (int i = 0; i < (numToMerge / 2); i++) {
            for (int j = tippingPoint - 1; j < cardinality; j++) {
                ctes[i].offer(java.lang.Math.random());
            }
        }
        expectedCardinality = (numToMerge / 2) * ((cardinality + tippingPoint) - 1);
        mergedEstimate = com.clearspring.analytics.stream.cardinality.CountThenEstimate.mergeEstimators(ctes).cardinality();
        error = (java.lang.Math.abs((mergedEstimate - expectedCardinality))) / ((double) (expectedCardinality));
        org.junit.Assert.assertEquals(0.01, error, 0.01);
        for (int i = numToMerge / 2; i < numToMerge; i++) {
            for (int j = tippingPoint - 1; j < cardinality; j++) {
                ctes[i].offer(java.lang.Math.random());
            }
        }
        expectedCardinality = numToMerge * cardinality;
        mergedEstimate = com.clearspring.analytics.stream.cardinality.CountThenEstimate.mergeEstimators(ctes).cardinality();
        error = (java.lang.Math.abs((mergedEstimate - expectedCardinality))) / ((double) (expectedCardinality));
        // StatementAdderOnAssert create null value
        com.clearspring.analytics.stream.cardinality.CountThenEstimate[] vc_9910 = (com.clearspring.analytics.stream.cardinality.CountThenEstimate[])null;
        // AssertGenerator add assertion
        junit.framework.Assert.assertNull(vc_9910);
        // AssertGenerator add assertion
        junit.framework.Assert.assertNull(vc_9910);
        // StatementAdderOnAssert create null value
        com.clearspring.analytics.stream.cardinality.CountThenEstimate vc_9908 = (com.clearspring.analytics.stream.cardinality.CountThenEstimate)null;
        // AssertGenerator add assertion
        junit.framework.Assert.assertNull(vc_9908);
        // AssertGenerator add assertion
        junit.framework.Assert.assertNull(vc_9908);
        // AssertGenerator replace invocation
        com.clearspring.analytics.stream.cardinality.CountThenEstimate o_testMerge_cf29877__62 = // StatementAdderMethod cloned existing statement
vc_9908.mergeEstimators(vc_9910);
        // AssertGenerator add assertion
        junit.framework.Assert.assertNull(o_testMerge_cf29877__62);
        org.junit.Assert.assertEquals(0.01, error, 0.01);
    }

    /* amplification of com.clearspring.analytics.stream.cardinality.TestCountThenEstimate#testMerge */
    @org.junit.Test(timeout = 1000)
    public void testMerge_cf29899_literalMutation37896() throws com.clearspring.analytics.stream.cardinality.CardinalityMergeException {
        int numToMerge = 10;
        int tippingPoint = 100;
        int cardinality = 1000;
        com.clearspring.analytics.stream.cardinality.CountThenEstimate[] ctes = new com.clearspring.analytics.stream.cardinality.CountThenEstimate[numToMerge];
        for (int i = 0; i < numToMerge; i++) {
            ctes[i] = new com.clearspring.analytics.stream.cardinality.CountThenEstimate(tippingPoint, com.clearspring.analytics.stream.cardinality.AdaptiveCounting.Builder.obyCount(100000));
            for (int j = 0; j < (tippingPoint - 1); j++) {
                ctes[i].offer(java.lang.Math.random());
            }
        }
        int expectedCardinality = numToMerge * (tippingPoint - 1);
        long mergedEstimate = com.clearspring.analytics.stream.cardinality.CountThenEstimate.mergeEstimators(ctes).cardinality();
        double error = (java.lang.Math.abs((mergedEstimate - expectedCardinality))) / ((double) (expectedCardinality));
        org.junit.Assert.assertEquals(0.0, error, 0.01);
        for (int i = 0; i < (numToMerge / 2); i++) {
            for (int j = tippingPoint - 1; j < cardinality; j++) {
                ctes[i].offer(java.lang.Math.random());
            }
        }
        expectedCardinality = (numToMerge / 2) * ((cardinality + tippingPoint) - 1);
        mergedEstimate = com.clearspring.analytics.stream.cardinality.CountThenEstimate.mergeEstimators(ctes).cardinality();
        error = (java.lang.Math.abs((mergedEstimate - expectedCardinality))) / ((double) (expectedCardinality));
        org.junit.Assert.assertEquals(0.01, error, 0.01);
        for (int i = numToMerge / 2; i < numToMerge; i++) {
            for (int j = tippingPoint - 1; j < cardinality; j++) {
                ctes[i].offer(java.lang.Math.random());
            }
        }
        expectedCardinality = numToMerge * cardinality;
        mergedEstimate = com.clearspring.analytics.stream.cardinality.CountThenEstimate.mergeEstimators(ctes).cardinality();
        error = (java.lang.Math.abs((mergedEstimate - expectedCardinality))) / ((double) (expectedCardinality));
        // StatementAddOnAssert local variable replacement
        com.clearspring.analytics.stream.cardinality.CountThenEstimate merged = com.clearspring.analytics.stream.cardinality.CountThenEstimate.mergeEstimators(ctes);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((com.clearspring.analytics.stream.cardinality.CountThenEstimate)merged).sizeof(), 3342);
        // AssertGenerator add assertion
        junit.framework.Assert.assertTrue(((com.clearspring.analytics.stream.cardinality.CountThenEstimate)merged).tipped());
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((com.clearspring.analytics.stream.cardinality.CountThenEstimate)merged).sizeof(), 3342);
        // AssertGenerator add assertion
        junit.framework.Assert.assertTrue(((com.clearspring.analytics.stream.cardinality.CountThenEstimate)merged).tipped());
        // StatementAdderMethod cloned existing statement
        merged.cardinality();
        org.junit.Assert.assertEquals(0.01, error, 0.01);
    }

    /* amplification of com.clearspring.analytics.stream.cardinality.TestCountThenEstimate#testMerge */
    @org.junit.Test(timeout = 1000)
    public void testMerge_cf29865_failAssert57_literalMutation36164() throws com.clearspring.analytics.stream.cardinality.CardinalityMergeException {
        // AssertGenerator generate try/catch block with fail statement
        try {
            int numToMerge = 10;
            int tippingPoint = 100;
            int cardinality = 1000;
            com.clearspring.analytics.stream.cardinality.CountThenEstimate[] ctes = new com.clearspring.analytics.stream.cardinality.CountThenEstimate[numToMerge];
            for (int i = 0; i < numToMerge; i++) {
                ctes[i] = new com.clearspring.analytics.stream.cardinality.CountThenEstimate(tippingPoint, com.clearspring.analytics.stream.cardinality.AdaptiveCounting.Builder.obyCount(100000));
                for (int j = 0; j < (tippingPoint - 1); j++) {
                    ctes[i].offer(java.lang.Math.random());
                }
            }
            int expectedCardinality = numToMerge * (tippingPoint - 1);
            long mergedEstimate = com.clearspring.analytics.stream.cardinality.CountThenEstimate.mergeEstimators(ctes).cardinality();
            double error = (java.lang.Math.abs((mergedEstimate - expectedCardinality))) / ((double) (expectedCardinality));
            for (int i = 1; i < (numToMerge / 2); i++) {
                for (int j = tippingPoint - 1; j < cardinality; j++) {
                    ctes[i].offer(java.lang.Math.random());
                }
            }
            expectedCardinality = (numToMerge / 2) * ((cardinality + tippingPoint) - 1);
            mergedEstimate = com.clearspring.analytics.stream.cardinality.CountThenEstimate.mergeEstimators(ctes).cardinality();
            error = (java.lang.Math.abs((mergedEstimate - expectedCardinality))) / ((double) (expectedCardinality));
            for (int i = numToMerge / 2; i < numToMerge; i++) {
                for (int j = tippingPoint - 1; j < cardinality; j++) {
                    ctes[i].offer(java.lang.Math.random());
                }
            }
            expectedCardinality = numToMerge * cardinality;
            mergedEstimate = com.clearspring.analytics.stream.cardinality.CountThenEstimate.mergeEstimators(ctes).cardinality();
            error = (java.lang.Math.abs((mergedEstimate - expectedCardinality))) / ((double) (expectedCardinality));
            // StatementAddOnAssert local variable replacement
            com.clearspring.analytics.stream.cardinality.CountThenEstimate merged = com.clearspring.analytics.stream.cardinality.CountThenEstimate.mergeEstimators(ctes);
            // AssertGenerator add assertion
            junit.framework.Assert.assertEquals(((com.clearspring.analytics.stream.cardinality.CountThenEstimate)merged).sizeof(), 3342);
            // AssertGenerator add assertion
            junit.framework.Assert.assertTrue(((com.clearspring.analytics.stream.cardinality.CountThenEstimate)merged).tipped());
            // StatementAdderMethod cloned existing statement
            merged.offerHashed(mergedEstimate);
            org.junit.Assert.fail("testMerge_cf29865 should have thrown UnsupportedOperationException");
        } catch (java.lang.UnsupportedOperationException eee) {
        }
    }

    /* amplification of com.clearspring.analytics.stream.cardinality.TestCountThenEstimate#testMerge */
    @org.junit.Test
    public void testMerge_literalMutation29787_literalMutation32120() throws com.clearspring.analytics.stream.cardinality.CardinalityMergeException {
        int numToMerge = 10;
        int tippingPoint = 100;
        int cardinality = 1000;
        com.clearspring.analytics.stream.cardinality.CountThenEstimate[] ctes = new com.clearspring.analytics.stream.cardinality.CountThenEstimate[numToMerge];
        for (int i = 0; i < numToMerge; i++) {
            ctes[i] = new com.clearspring.analytics.stream.cardinality.CountThenEstimate(tippingPoint, com.clearspring.analytics.stream.cardinality.AdaptiveCounting.Builder.obyCount(100000));
            for (int j = 0; j < (tippingPoint - 1); j++) {
                ctes[i].offer(java.lang.Math.random());
            }
        }
        int expectedCardinality = numToMerge * (tippingPoint - 0);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(expectedCardinality, 1000);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(expectedCardinality, 1000);
        long mergedEstimate = com.clearspring.analytics.stream.cardinality.CountThenEstimate.mergeEstimators(ctes).cardinality();
        double error = (java.lang.Math.abs((mergedEstimate - expectedCardinality))) / ((double) (expectedCardinality));
        org.junit.Assert.assertEquals(0.01, error, 0.01);
        for (int i = 0; i < (numToMerge / 2); i++) {
            for (int j = tippingPoint - 1; j < cardinality; j++) {
                ctes[i].offer(java.lang.Math.random());
            }
        }
        expectedCardinality = (numToMerge / 2) * ((cardinality + tippingPoint) - 1);
        mergedEstimate = com.clearspring.analytics.stream.cardinality.CountThenEstimate.mergeEstimators(ctes).cardinality();
        error = (java.lang.Math.abs((mergedEstimate - expectedCardinality))) / ((double) (expectedCardinality));
        org.junit.Assert.assertEquals(0.01, error, 0.01);
        for (int i = numToMerge / 2; i < numToMerge; i++) {
            for (int j = tippingPoint - 1; j < cardinality; j++) {
                ctes[i].offer(java.lang.Math.random());
            }
        }
        expectedCardinality = numToMerge * cardinality;
        mergedEstimate = com.clearspring.analytics.stream.cardinality.CountThenEstimate.mergeEstimators(ctes).cardinality();
        error = (java.lang.Math.abs((mergedEstimate - expectedCardinality))) / ((double) (expectedCardinality));
        org.junit.Assert.assertEquals(0.01, error, 0.01);
    }

    /* amplification of com.clearspring.analytics.stream.cardinality.TestCountThenEstimate#testMerge */
    @org.junit.Test(timeout = 1000)
    public void testMerge_cf29846_cf34972_failAssert44_literalMutation47532() throws com.clearspring.analytics.stream.cardinality.CardinalityMergeException {
        // AssertGenerator generate try/catch block with fail statement
        try {
            int numToMerge = 101;
            // AssertGenerator add assertion
            junit.framework.Assert.assertEquals(numToMerge, 101);
            int tippingPoint = 100;
            int cardinality = 1000;
            com.clearspring.analytics.stream.cardinality.CountThenEstimate[] ctes = new com.clearspring.analytics.stream.cardinality.CountThenEstimate[numToMerge];
            for (int i = 0; i < numToMerge; i++) {
                ctes[i] = new com.clearspring.analytics.stream.cardinality.CountThenEstimate(tippingPoint, com.clearspring.analytics.stream.cardinality.AdaptiveCounting.Builder.obyCount(100000));
                for (int j = 0; j < (tippingPoint - 1); j++) {
                    ctes[i].offer(java.lang.Math.random());
                }
            }
            int expectedCardinality = numToMerge * (tippingPoint - 1);
            long mergedEstimate = com.clearspring.analytics.stream.cardinality.CountThenEstimate.mergeEstimators(ctes).cardinality();
            double error = (java.lang.Math.abs((mergedEstimate - expectedCardinality))) / ((double) (expectedCardinality));
            for (int i = 0; i < (numToMerge / 2); i++) {
                for (int j = tippingPoint - 1; j < cardinality; j++) {
                    ctes[i].offer(java.lang.Math.random());
                }
            }
            expectedCardinality = (numToMerge / 2) * ((cardinality + tippingPoint) - 1);
            mergedEstimate = com.clearspring.analytics.stream.cardinality.CountThenEstimate.mergeEstimators(ctes).cardinality();
            error = (java.lang.Math.abs((mergedEstimate - expectedCardinality))) / ((double) (expectedCardinality));
            for (int i = numToMerge / 2; i < numToMerge; i++) {
                for (int j = tippingPoint - 1; j < cardinality; j++) {
                    ctes[i].offer(java.lang.Math.random());
                }
            }
            expectedCardinality = numToMerge * cardinality;
            mergedEstimate = com.clearspring.analytics.stream.cardinality.CountThenEstimate.mergeEstimators(ctes).cardinality();
            error = (java.lang.Math.abs((mergedEstimate - expectedCardinality))) / ((double) (expectedCardinality));
            // StatementAdderOnAssert create null value
            java.lang.Object vc_9896 = (java.lang.Object)null;
            // AssertGenerator add assertion
            junit.framework.Assert.assertNull(vc_9896);
            // AssertGenerator add assertion
            junit.framework.Assert.assertNull(vc_9896);
            // StatementAddOnAssert local variable replacement
            com.clearspring.analytics.stream.cardinality.CountThenEstimate merged = com.clearspring.analytics.stream.cardinality.CountThenEstimate.mergeEstimators(ctes);
            // AssertGenerator add assertion
            junit.framework.Assert.assertEquals(((com.clearspring.analytics.stream.cardinality.CountThenEstimate)merged).sizeof(), 3342);
            // AssertGenerator add assertion
            junit.framework.Assert.assertTrue(((com.clearspring.analytics.stream.cardinality.CountThenEstimate)merged).tipped());
            // AssertGenerator add assertion
            junit.framework.Assert.assertEquals(((com.clearspring.analytics.stream.cardinality.CountThenEstimate)merged).sizeof(), 3342);
            // AssertGenerator add assertion
            junit.framework.Assert.assertTrue(((com.clearspring.analytics.stream.cardinality.CountThenEstimate)merged).tipped());
            // AssertGenerator replace invocation
            boolean o_testMerge_cf29846_cf34972_failAssert44_literalMutation47532__70 = // StatementAdderMethod cloned existing statement
merged.offer(vc_9896);
            // AssertGenerator add assertion
            junit.framework.Assert.assertFalse(o_testMerge_cf29846_cf34972_failAssert44_literalMutation47532__70);
            // StatementAdderOnAssert create null value
            com.clearspring.analytics.stream.cardinality.CountThenEstimate vc_10438 = (com.clearspring.analytics.stream.cardinality.CountThenEstimate)null;
            // AssertGenerator add assertion
            junit.framework.Assert.assertNull(vc_10438);
            // StatementAdderMethod cloned existing statement
            vc_10438.offer(vc_9896);
            org.junit.Assert.fail("testMerge_cf29846_cf34972 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    /* amplification of com.clearspring.analytics.stream.cardinality.TestCountThenEstimate#testMerge */
    @org.junit.Test(timeout = 1000)
    public void testMerge_cf29856_failAssert50_literalMutation35738_literalMutation43887() throws com.clearspring.analytics.stream.cardinality.CardinalityMergeException {
        // AssertGenerator generate try/catch block with fail statement
        try {
            int numToMerge = 10;
            int tippingPoint = 100;
            int cardinality = 1000;
            com.clearspring.analytics.stream.cardinality.CountThenEstimate[] ctes = new com.clearspring.analytics.stream.cardinality.CountThenEstimate[numToMerge];
            for (int i = 0; i < numToMerge; i++) {
                ctes[i] = new com.clearspring.analytics.stream.cardinality.CountThenEstimate(tippingPoint, com.clearspring.analytics.stream.cardinality.AdaptiveCounting.Builder.obyCount(100000));
                for (int j = 0; j < (tippingPoint - 1); j++) {
                    ctes[i].offer(java.lang.Math.random());
                }
            }
            int expectedCardinality = numToMerge * (tippingPoint - 1);
            long mergedEstimate = com.clearspring.analytics.stream.cardinality.CountThenEstimate.mergeEstimators(ctes).cardinality();
            double error = (java.lang.Math.abs((mergedEstimate - expectedCardinality))) / ((double) (expectedCardinality));
            for (int i = 1; i < (numToMerge / 2); i++) {
                for (int j = tippingPoint - 1; j < cardinality; j++) {
                    ctes[i].offer(java.lang.Math.random());
                }
            }
            expectedCardinality = (numToMerge / 2) * ((cardinality + tippingPoint) - 1);
            mergedEstimate = com.clearspring.analytics.stream.cardinality.CountThenEstimate.mergeEstimators(ctes).cardinality();
            error = (java.lang.Math.abs((mergedEstimate - expectedCardinality))) / ((double) (expectedCardinality));
            for (int i = numToMerge / 2; i < numToMerge; i++) {
                for (int j = tippingPoint - 1; j < cardinality; j++) {
                    ctes[i].offer(java.lang.Math.random());
                }
            }
            expectedCardinality = numToMerge * cardinality;
            mergedEstimate = com.clearspring.analytics.stream.cardinality.CountThenEstimate.mergeEstimators(ctes).cardinality();
            error = (java.lang.Math.abs((mergedEstimate - expectedCardinality))) / ((double) (expectedCardinality));
            // StatementAdderOnAssert create literal from method
            int int_vc_306 = 2;
            // AssertGenerator add assertion
            junit.framework.Assert.assertEquals(int_vc_306, 2);
            // AssertGenerator add assertion
            junit.framework.Assert.assertEquals(int_vc_306, 2);
            // StatementAddOnAssert local variable replacement
            com.clearspring.analytics.stream.cardinality.CountThenEstimate merged = com.clearspring.analytics.stream.cardinality.CountThenEstimate.mergeEstimators(ctes);
            // AssertGenerator add assertion
            junit.framework.Assert.assertEquals(((com.clearspring.analytics.stream.cardinality.CountThenEstimate)merged).sizeof(), 3342);
            // AssertGenerator add assertion
            junit.framework.Assert.assertTrue(((com.clearspring.analytics.stream.cardinality.CountThenEstimate)merged).tipped());
            // AssertGenerator add assertion
            junit.framework.Assert.assertEquals(((com.clearspring.analytics.stream.cardinality.CountThenEstimate)merged).sizeof(), 3342);
            // AssertGenerator add assertion
            junit.framework.Assert.assertTrue(((com.clearspring.analytics.stream.cardinality.CountThenEstimate)merged).tipped());
            // StatementAdderMethod cloned existing statement
            merged.offerHashed(int_vc_306);
            org.junit.Assert.fail("testMerge_cf29856 should have thrown UnsupportedOperationException");
        } catch (java.lang.UnsupportedOperationException eee) {
        }
    }

    /* amplification of com.clearspring.analytics.stream.cardinality.TestCountThenEstimate#testMerge */
    @org.junit.Test
    public void testMerge_literalMutation29786_literalMutation31994_literalMutation39164() throws com.clearspring.analytics.stream.cardinality.CardinalityMergeException {
        int numToMerge = 10;
        int tippingPoint = 100;
        int cardinality = 1000;
        com.clearspring.analytics.stream.cardinality.CountThenEstimate[] ctes = new com.clearspring.analytics.stream.cardinality.CountThenEstimate[numToMerge];
        for (int i = 0; i < numToMerge; i++) {
            ctes[i] = new com.clearspring.analytics.stream.cardinality.CountThenEstimate(tippingPoint, com.clearspring.analytics.stream.cardinality.AdaptiveCounting.Builder.obyCount(100000));
            for (int j = 0; j < (tippingPoint - 1); j++) {
                ctes[i].offer(java.lang.Math.random());
            }
        }
        int expectedCardinality = numToMerge * (tippingPoint - 2);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(expectedCardinality, 980);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(expectedCardinality, 980);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(expectedCardinality, 980);
        long mergedEstimate = com.clearspring.analytics.stream.cardinality.CountThenEstimate.mergeEstimators(ctes).cardinality();
        double error = (java.lang.Math.abs((mergedEstimate - expectedCardinality))) / ((double) (expectedCardinality));
        org.junit.Assert.assertEquals(0.01, error, 0.01);
        for (int i = 0; i < (numToMerge / 2); i++) {
            for (int j = tippingPoint - 0; j < cardinality; j++) {
                ctes[i].offer(java.lang.Math.random());
            }
        }
        expectedCardinality = (numToMerge / 2) * ((cardinality + tippingPoint) - 1);
        mergedEstimate = com.clearspring.analytics.stream.cardinality.CountThenEstimate.mergeEstimators(ctes).cardinality();
        error = (java.lang.Math.abs((mergedEstimate - expectedCardinality))) / ((double) (expectedCardinality));
        org.junit.Assert.assertEquals(0.01, error, 0.01);
        for (int i = numToMerge / 2; i < numToMerge; i++) {
            for (int j = tippingPoint - 1; j < cardinality; j++) {
                ctes[i].offer(java.lang.Math.random());
            }
        }
        expectedCardinality = numToMerge * cardinality;
        mergedEstimate = com.clearspring.analytics.stream.cardinality.CountThenEstimate.mergeEstimators(ctes).cardinality();
        error = (java.lang.Math.abs((mergedEstimate - expectedCardinality))) / ((double) (expectedCardinality));
        org.junit.Assert.assertEquals(0.01, error, 0.01);
    }

    /* amplification of com.clearspring.analytics.stream.cardinality.TestCountThenEstimate#testMerge */
    @org.junit.Test
    public void testMerge_literalMutation29771_literalMutation31175_literalMutation42975_failAssert16() throws com.clearspring.analytics.stream.cardinality.CardinalityMergeException {
        // AssertGenerator generate try/catch block with fail statement
        try {
            int numToMerge = 0;
            int tippingPoint = 100;
            int cardinality = 1001;
            // AssertGenerator add assertion
            junit.framework.Assert.assertEquals(cardinality, 1001);
            // AssertGenerator add assertion
            junit.framework.Assert.assertEquals(cardinality, 1001);
            com.clearspring.analytics.stream.cardinality.CountThenEstimate[] ctes = new com.clearspring.analytics.stream.cardinality.CountThenEstimate[numToMerge];
            for (int i = 0; i < numToMerge; i++) {
                ctes[i] = new com.clearspring.analytics.stream.cardinality.CountThenEstimate(tippingPoint, com.clearspring.analytics.stream.cardinality.AdaptiveCounting.Builder.obyCount(100000));
                for (int j = 0; j < (tippingPoint - 1); j++) {
                    ctes[i].offer(java.lang.Math.random());
                }
            }
            int expectedCardinality = numToMerge * (tippingPoint - 1);
            long mergedEstimate = com.clearspring.analytics.stream.cardinality.CountThenEstimate.mergeEstimators(ctes).cardinality();
            double error = (java.lang.Math.abs((mergedEstimate - expectedCardinality))) / ((double) (expectedCardinality));
            for (int i = 0; i < (numToMerge / 2); i++) {
                for (int j = tippingPoint - 1; j < cardinality; j++) {
                    ctes[i].offer(java.lang.Math.random());
                }
            }
            expectedCardinality = (numToMerge / 2) * ((cardinality + tippingPoint) - 1);
            mergedEstimate = com.clearspring.analytics.stream.cardinality.CountThenEstimate.mergeEstimators(ctes).cardinality();
            error = (java.lang.Math.abs((mergedEstimate - expectedCardinality))) / ((double) (expectedCardinality));
            for (int i = numToMerge / 2; i < numToMerge; i++) {
                for (int j = tippingPoint - 1; j < cardinality; j++) {
                    ctes[i].offer(java.lang.Math.random());
                }
            }
            expectedCardinality = numToMerge * cardinality;
            mergedEstimate = com.clearspring.analytics.stream.cardinality.CountThenEstimate.mergeEstimators(ctes).cardinality();
            error = (java.lang.Math.abs((mergedEstimate - expectedCardinality))) / ((double) (expectedCardinality));
            org.junit.Assert.fail("testMerge_literalMutation29771_literalMutation31175_literalMutation42975 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    /* amplification of com.clearspring.analytics.stream.cardinality.TestCountThenEstimate#testMerge */
    @org.junit.Test(timeout = 1000)
    public void testMerge_cf29878_literalMutation36853_literalMutation39480() throws com.clearspring.analytics.stream.cardinality.CardinalityMergeException {
        int numToMerge = 10;
        int tippingPoint = 100;
        int cardinality = 1000;
        com.clearspring.analytics.stream.cardinality.CountThenEstimate[] ctes = new com.clearspring.analytics.stream.cardinality.CountThenEstimate[numToMerge];
        for (int i = 0; i < numToMerge; i++) {
            ctes[i] = new com.clearspring.analytics.stream.cardinality.CountThenEstimate(tippingPoint, com.clearspring.analytics.stream.cardinality.AdaptiveCounting.Builder.obyCount(100000));
            for (int j = 0; j < (tippingPoint - 1); j++) {
                ctes[i].offer(java.lang.Math.random());
            }
        }
        int expectedCardinality = numToMerge * (tippingPoint - 1);
        long mergedEstimate = com.clearspring.analytics.stream.cardinality.CountThenEstimate.mergeEstimators(ctes).cardinality();
        double error = (java.lang.Math.abs((mergedEstimate - expectedCardinality))) / ((double) (expectedCardinality));
        org.junit.Assert.assertEquals(0.0, error, 0.01);
        for (int i = 0; i < (numToMerge / 2); i++) {
            for (int j = tippingPoint - 1; j < cardinality; j++) {
                ctes[i].offer(java.lang.Math.random());
            }
        }
        expectedCardinality = (numToMerge / 2) * ((cardinality + tippingPoint) - 1);
        mergedEstimate = com.clearspring.analytics.stream.cardinality.CountThenEstimate.mergeEstimators(ctes).cardinality();
        error = (java.lang.Math.abs((mergedEstimate - expectedCardinality))) / ((double) (expectedCardinality));
        org.junit.Assert.assertEquals(0.01, error, 1.0);
        for (int i = numToMerge / 2; i < numToMerge; i++) {
            for (int j = tippingPoint - 1; j < cardinality; j++) {
                ctes[i].offer(java.lang.Math.random());
            }
        }
        expectedCardinality = numToMerge * cardinality;
        mergedEstimate = com.clearspring.analytics.stream.cardinality.CountThenEstimate.mergeEstimators(ctes).cardinality();
        error = (java.lang.Math.abs((mergedEstimate - expectedCardinality))) / ((double) (expectedCardinality));
        // StatementAdderOnAssert create null value
        com.clearspring.analytics.stream.cardinality.CountThenEstimate vc_9908 = (com.clearspring.analytics.stream.cardinality.CountThenEstimate)null;
        // AssertGenerator add assertion
        junit.framework.Assert.assertNull(vc_9908);
        // AssertGenerator add assertion
        junit.framework.Assert.assertNull(vc_9908);
        // AssertGenerator add assertion
        junit.framework.Assert.assertNull(vc_9908);
        // AssertGenerator replace invocation
        com.clearspring.analytics.stream.cardinality.CountThenEstimate o_testMerge_cf29878__60 = // StatementAdderMethod cloned existing statement
vc_9908.mergeEstimators(ctes);
        // AssertGenerator add assertion
        junit.framework.Assert.assertTrue(((com.clearspring.analytics.stream.cardinality.CountThenEstimate)o_testMerge_cf29878__60).tipped());
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((com.clearspring.analytics.stream.cardinality.CountThenEstimate)o_testMerge_cf29878__60).sizeof(), 3342);
        org.junit.Assert.assertEquals(0.01, error, 0.01);
    }

    /* amplification of com.clearspring.analytics.stream.cardinality.TestCountThenEstimate#testMerge */
    @org.junit.Test(timeout = 1000)
    public void testMerge_cf29872_literalMutation36459_literalMutation45680() throws com.clearspring.analytics.stream.cardinality.CardinalityMergeException {
        int numToMerge = 10;
        int tippingPoint = 100;
        int cardinality = 1000;
        com.clearspring.analytics.stream.cardinality.CountThenEstimate[] ctes = new com.clearspring.analytics.stream.cardinality.CountThenEstimate[numToMerge];
        for (int i = 0; i < numToMerge; i++) {
            ctes[i] = new com.clearspring.analytics.stream.cardinality.CountThenEstimate(tippingPoint, com.clearspring.analytics.stream.cardinality.AdaptiveCounting.Builder.obyCount(100000));
            for (int j = 0; j < (tippingPoint - 1); j++) {
                ctes[i].offer(java.lang.Math.random());
            }
        }
        int expectedCardinality = numToMerge * (tippingPoint - 0);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(expectedCardinality, 1000);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(expectedCardinality, 1000);
        long mergedEstimate = com.clearspring.analytics.stream.cardinality.CountThenEstimate.mergeEstimators(ctes).cardinality();
        double error = (java.lang.Math.abs((mergedEstimate - expectedCardinality))) / ((double) (expectedCardinality));
        org.junit.Assert.assertEquals(0.01, error, 0.01);
        for (int i = 0; i < (numToMerge / 2); i++) {
            for (int j = tippingPoint - 1; j < cardinality; j++) {
                ctes[i].offer(java.lang.Math.random());
            }
        }
        expectedCardinality = (numToMerge / 2) * ((cardinality + tippingPoint) - 1);
        mergedEstimate = com.clearspring.analytics.stream.cardinality.CountThenEstimate.mergeEstimators(ctes).cardinality();
        error = (java.lang.Math.abs((mergedEstimate - expectedCardinality))) / ((double) (expectedCardinality));
        org.junit.Assert.assertEquals(0.01, error, 0.01);
        for (int i = numToMerge / 2; i < numToMerge; i++) {
            for (int j = tippingPoint - 1; j < cardinality; j++) {
                ctes[i].offer(java.lang.Math.random());
            }
        }
        expectedCardinality = numToMerge * cardinality;
        mergedEstimate = com.clearspring.analytics.stream.cardinality.CountThenEstimate.mergeEstimators(ctes).cardinality();
        error = (java.lang.Math.abs((mergedEstimate - expectedCardinality))) / ((double) (expectedCardinality));
        // StatementAddOnAssert local variable replacement
        com.clearspring.analytics.stream.cardinality.CountThenEstimate merged = com.clearspring.analytics.stream.cardinality.CountThenEstimate.mergeEstimators(ctes);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((com.clearspring.analytics.stream.cardinality.CountThenEstimate)merged).sizeof(), 3342);
        // AssertGenerator add assertion
        junit.framework.Assert.assertTrue(((com.clearspring.analytics.stream.cardinality.CountThenEstimate)merged).tipped());
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((com.clearspring.analytics.stream.cardinality.CountThenEstimate)merged).sizeof(), 3342);
        // AssertGenerator add assertion
        junit.framework.Assert.assertTrue(((com.clearspring.analytics.stream.cardinality.CountThenEstimate)merged).tipped());
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((com.clearspring.analytics.stream.cardinality.CountThenEstimate)merged).sizeof(), 3342);
        // AssertGenerator add assertion
        junit.framework.Assert.assertTrue(((com.clearspring.analytics.stream.cardinality.CountThenEstimate)merged).tipped());
        // AssertGenerator replace invocation
        boolean o_testMerge_cf29872__61 = // StatementAdderMethod cloned existing statement
merged.tipped();
        // AssertGenerator add assertion
        junit.framework.Assert.assertTrue(o_testMerge_cf29872__61);
        org.junit.Assert.assertEquals(0.01, error, 0.01);
    }

    /* amplification of com.clearspring.analytics.stream.cardinality.TestCountThenEstimate#testMerge */
    @org.junit.Test(timeout = 0)
    public void testMerge_cf29896_literalMutation37622_literalMutation43219() throws com.clearspring.analytics.stream.cardinality.CardinalityMergeException {
        int numToMerge = 10;
        int tippingPoint = 100;
        int cardinality = 1000;
        com.clearspring.analytics.stream.cardinality.CountThenEstimate[] ctes = new com.clearspring.analytics.stream.cardinality.CountThenEstimate[numToMerge];
        for (int i = 0; i < numToMerge; i++) {
            ctes[i] = new com.clearspring.analytics.stream.cardinality.CountThenEstimate(tippingPoint, com.clearspring.analytics.stream.cardinality.AdaptiveCounting.Builder.obyCount(100000));
            for (int j = 0; j < (tippingPoint - 1); j++) {
                ctes[i].offer(java.lang.Math.random());
            }
        }
        int expectedCardinality = numToMerge * (tippingPoint - 1);
        long mergedEstimate = com.clearspring.analytics.stream.cardinality.CountThenEstimate.mergeEstimators(ctes).cardinality();
        double error = (java.lang.Math.abs((mergedEstimate - expectedCardinality))) / ((double) (expectedCardinality));
        org.junit.Assert.assertEquals(0.01, error, 0.01);
        for (int i = 0; i < (numToMerge / 2); i++) {
            for (int j = tippingPoint - 1; j < cardinality; j++) {
                ctes[i].offer(java.lang.Math.random());
            }
        }
        expectedCardinality = (numToMerge / 2) * ((cardinality + tippingPoint) - 1);
        mergedEstimate = com.clearspring.analytics.stream.cardinality.CountThenEstimate.mergeEstimators(ctes).cardinality();
        error = (java.lang.Math.abs((mergedEstimate - expectedCardinality))) / ((double) (expectedCardinality));
        org.junit.Assert.assertEquals(0.01, error, 0.01);
        for (int i = numToMerge / 2; i < numToMerge; i++) {
            for (int j = tippingPoint - 1; j < cardinality; j++) {
                ctes[i].offer(java.lang.Math.random());
            }
        }
        expectedCardinality = numToMerge * cardinality;
        mergedEstimate = com.clearspring.analytics.stream.cardinality.CountThenEstimate.mergeEstimators(ctes).cardinality();
        error = (java.lang.Math.abs((mergedEstimate - expectedCardinality))) / ((double) (expectedCardinality));
        // StatementAddOnAssert local variable replacement
        com.clearspring.analytics.stream.cardinality.CountThenEstimate merged = com.clearspring.analytics.stream.cardinality.CountThenEstimate.mergeEstimators(ctes);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((com.clearspring.analytics.stream.cardinality.CountThenEstimate)merged).sizeof(), 3342);
        // AssertGenerator add assertion
        junit.framework.Assert.assertTrue(((com.clearspring.analytics.stream.cardinality.CountThenEstimate)merged).tipped());
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((com.clearspring.analytics.stream.cardinality.CountThenEstimate)merged).sizeof(), 3342);
        // AssertGenerator add assertion
        junit.framework.Assert.assertTrue(((com.clearspring.analytics.stream.cardinality.CountThenEstimate)merged).tipped());
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((com.clearspring.analytics.stream.cardinality.CountThenEstimate)merged).sizeof(), 3342);
        // AssertGenerator add assertion
        junit.framework.Assert.assertTrue(((com.clearspring.analytics.stream.cardinality.CountThenEstimate)merged).tipped());
        // AssertGenerator replace invocation
        int o_testMerge_cf29896__61 = // StatementAdderMethod cloned existing statement
merged.sizeof();
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(o_testMerge_cf29896__61, 3342);
        org.junit.Assert.assertEquals(0.01, error, 0.01);
    }

    /* amplification of com.clearspring.analytics.stream.cardinality.TestCountThenEstimate#testMerge */
    @org.junit.Test(timeout = 1000)
    public void testMerge_literalMutation29788_literalMutation32288_cf47850() throws com.clearspring.analytics.stream.cardinality.CardinalityMergeException {
        int numToMerge = 10;
        int tippingPoint = 100;
        int cardinality = 1000;
        com.clearspring.analytics.stream.cardinality.CountThenEstimate[] ctes = new com.clearspring.analytics.stream.cardinality.CountThenEstimate[numToMerge];
        for (int i = 0; i < numToMerge; i++) {
            ctes[i] = new com.clearspring.analytics.stream.cardinality.CountThenEstimate(tippingPoint, com.clearspring.analytics.stream.cardinality.AdaptiveCounting.Builder.obyCount(100000));
            for (int j = 0; j < (tippingPoint - 1); j++) {
                ctes[i].offer(java.lang.Math.random());
            }
        }
        int expectedCardinality = numToMerge * (tippingPoint - 0);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(expectedCardinality, 1000);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(expectedCardinality, 1000);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(expectedCardinality, 1000);
        long mergedEstimate = com.clearspring.analytics.stream.cardinality.CountThenEstimate.mergeEstimators(ctes).cardinality();
        double error = (java.lang.Math.abs((mergedEstimate - expectedCardinality))) / ((double) (expectedCardinality));
        org.junit.Assert.assertEquals(0.01, error, 0.01);
        for (int i = 0; i < (numToMerge / 2); i++) {
            for (int j = tippingPoint - 1; j < cardinality; j++) {
                ctes[i].offer(java.lang.Math.random());
            }
        }
        expectedCardinality = (numToMerge / 2) * ((cardinality + tippingPoint) - 1);
        mergedEstimate = com.clearspring.analytics.stream.cardinality.CountThenEstimate.mergeEstimators(ctes).cardinality();
        error = (java.lang.Math.abs((mergedEstimate - expectedCardinality))) / ((double) (expectedCardinality));
        org.junit.Assert.assertEquals(0.01, error, 0.01);
        for (int i = numToMerge / 2; i < numToMerge; i++) {
            for (int j = tippingPoint - 1; j < cardinality; j++) {
                ctes[i].offer(java.lang.Math.random());
            }
        }
        expectedCardinality = numToMerge * cardinality;
        mergedEstimate = com.clearspring.analytics.stream.cardinality.CountThenEstimate.mergeEstimators(ctes).cardinality();
        error = (java.lang.Math.abs((mergedEstimate - expectedCardinality))) / ((double) (expectedCardinality));
        // StatementAdderOnAssert create random local variable
        java.lang.Object vc_11767 = new java.lang.Object();
        // StatementAddOnAssert local variable replacement
        com.clearspring.analytics.stream.cardinality.CountThenEstimate merged = com.clearspring.analytics.stream.cardinality.CountThenEstimate.mergeEstimators(ctes);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((com.clearspring.analytics.stream.cardinality.CountThenEstimate)merged).sizeof(), 3342);
        // AssertGenerator add assertion
        junit.framework.Assert.assertTrue(((com.clearspring.analytics.stream.cardinality.CountThenEstimate)merged).tipped());
        // StatementAdderMethod cloned existing statement
        merged.offer(vc_11767);
        org.junit.Assert.assertEquals(0.01, error, 0.01);
    }

    /* amplification of com.clearspring.analytics.stream.cardinality.TestCountThenEstimate#testMerge */
    @org.junit.Test(timeout = 11)
    public void testMerge_cf29857_failAssert51_literalMutation35792_literalMutation44503() throws com.clearspring.analytics.stream.cardinality.CardinalityMergeException {
        // AssertGenerator generate try/catch block with fail statement
        try {
            int numToMerge = 10;
            int tippingPoint = 100;
            int cardinality = 1000;
            com.clearspring.analytics.stream.cardinality.CountThenEstimate[] ctes = new com.clearspring.analytics.stream.cardinality.CountThenEstimate[numToMerge];
            for (int i = 0; i < numToMerge; i++) {
                ctes[i] = new com.clearspring.analytics.stream.cardinality.CountThenEstimate(tippingPoint, com.clearspring.analytics.stream.cardinality.AdaptiveCounting.Builder.obyCount(100000));
                for (int j = 0; j < (tippingPoint - 0); j++) {
                    ctes[i].offer(java.lang.Math.random());
                }
            }
            int expectedCardinality = numToMerge * (tippingPoint - 1);
            long mergedEstimate = com.clearspring.analytics.stream.cardinality.CountThenEstimate.mergeEstimators(ctes).cardinality();
            double error = (java.lang.Math.abs((mergedEstimate - expectedCardinality))) / ((double) (expectedCardinality));
            for (int i = 0; i < (numToMerge / 2); i++) {
                for (int j = tippingPoint - 1; j < cardinality; j++) {
                    ctes[i].offer(java.lang.Math.random());
                }
            }
            expectedCardinality = (numToMerge / 2) * ((cardinality + tippingPoint) - 1);
            mergedEstimate = com.clearspring.analytics.stream.cardinality.CountThenEstimate.mergeEstimators(ctes).cardinality();
            error = (java.lang.Math.abs((mergedEstimate - expectedCardinality))) / ((double) (expectedCardinality));
            for (int i = numToMerge / 2; i < numToMerge; i++) {
                for (int j = tippingPoint - 1; j < cardinality; j++) {
                    ctes[i].offer(java.lang.Math.random());
                }
            }
            expectedCardinality = numToMerge * cardinality;
            mergedEstimate = com.clearspring.analytics.stream.cardinality.CountThenEstimate.mergeEstimators(ctes).cardinality();
            error = (java.lang.Math.abs((mergedEstimate - expectedCardinality))) / ((double) (expectedCardinality));
            // StatementAdderOnAssert create random local variable
            int vc_9900 = 421451695;
            // AssertGenerator add assertion
            junit.framework.Assert.assertEquals(vc_9900, 421451695);
            // AssertGenerator add assertion
            junit.framework.Assert.assertEquals(vc_9900, 421451695);
            // StatementAddOnAssert local variable replacement
            com.clearspring.analytics.stream.cardinality.CountThenEstimate merged = com.clearspring.analytics.stream.cardinality.CountThenEstimate.mergeEstimators(ctes);
            // AssertGenerator add assertion
            junit.framework.Assert.assertEquals(((com.clearspring.analytics.stream.cardinality.CountThenEstimate)merged).sizeof(), 3342);
            // AssertGenerator add assertion
            junit.framework.Assert.assertTrue(((com.clearspring.analytics.stream.cardinality.CountThenEstimate)merged).tipped());
            // AssertGenerator add assertion
            junit.framework.Assert.assertEquals(((com.clearspring.analytics.stream.cardinality.CountThenEstimate)merged).sizeof(), 3342);
            // AssertGenerator add assertion
            junit.framework.Assert.assertTrue(((com.clearspring.analytics.stream.cardinality.CountThenEstimate)merged).tipped());
            // StatementAdderMethod cloned existing statement
            merged.offerHashed(vc_9900);
            org.junit.Assert.fail("testMerge_cf29857 should have thrown UnsupportedOperationException");
        } catch (java.lang.UnsupportedOperationException eee) {
        }
    }

    /* amplification of com.clearspring.analytics.stream.cardinality.TestCountThenEstimate#testMerge */
    @org.junit.Test(timeout = 1000)
    public void testMerge_cf29899_literalMutation37896_literalMutation39685() throws com.clearspring.analytics.stream.cardinality.CardinalityMergeException {
        int numToMerge = 10;
        int tippingPoint = 100;
        int cardinality = 1000;
        com.clearspring.analytics.stream.cardinality.CountThenEstimate[] ctes = new com.clearspring.analytics.stream.cardinality.CountThenEstimate[numToMerge];
        for (int i = 0; i < numToMerge; i++) {
            ctes[i] = new com.clearspring.analytics.stream.cardinality.CountThenEstimate(tippingPoint, com.clearspring.analytics.stream.cardinality.AdaptiveCounting.Builder.obyCount(100000));
            for (int j = 0; j < (tippingPoint - 1); j++) {
                ctes[i].offer(java.lang.Math.random());
            }
        }
        int expectedCardinality = numToMerge * (tippingPoint - 1);
        long mergedEstimate = com.clearspring.analytics.stream.cardinality.CountThenEstimate.mergeEstimators(ctes).cardinality();
        double error = (java.lang.Math.abs((mergedEstimate - expectedCardinality))) / ((double) (expectedCardinality));
        org.junit.Assert.assertEquals(0.0, error, 0.01);
        for (int i = 0; i < (numToMerge / 2); i++) {
            for (int j = tippingPoint - 1; j < cardinality; j++) {
                ctes[i].offer(java.lang.Math.random());
            }
        }
        expectedCardinality = (numToMerge / 2) * ((cardinality + tippingPoint) - 1);
        mergedEstimate = com.clearspring.analytics.stream.cardinality.CountThenEstimate.mergeEstimators(ctes).cardinality();
        error = (java.lang.Math.abs((mergedEstimate - expectedCardinality))) / ((double) (expectedCardinality));
        org.junit.Assert.assertEquals(0.0, error, 0.01);
        for (int i = numToMerge / 2; i < numToMerge; i++) {
            for (int j = tippingPoint - 1; j < cardinality; j++) {
                ctes[i].offer(java.lang.Math.random());
            }
        }
        expectedCardinality = numToMerge * cardinality;
        mergedEstimate = com.clearspring.analytics.stream.cardinality.CountThenEstimate.mergeEstimators(ctes).cardinality();
        error = (java.lang.Math.abs((mergedEstimate - expectedCardinality))) / ((double) (expectedCardinality));
        // StatementAddOnAssert local variable replacement
        com.clearspring.analytics.stream.cardinality.CountThenEstimate merged = com.clearspring.analytics.stream.cardinality.CountThenEstimate.mergeEstimators(ctes);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((com.clearspring.analytics.stream.cardinality.CountThenEstimate)merged).sizeof(), 3342);
        // AssertGenerator add assertion
        junit.framework.Assert.assertTrue(((com.clearspring.analytics.stream.cardinality.CountThenEstimate)merged).tipped());
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((com.clearspring.analytics.stream.cardinality.CountThenEstimate)merged).sizeof(), 3342);
        // AssertGenerator add assertion
        junit.framework.Assert.assertTrue(((com.clearspring.analytics.stream.cardinality.CountThenEstimate)merged).tipped());
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((com.clearspring.analytics.stream.cardinality.CountThenEstimate)merged).sizeof(), 3342);
        // AssertGenerator add assertion
        junit.framework.Assert.assertTrue(((com.clearspring.analytics.stream.cardinality.CountThenEstimate)merged).tipped());
        // StatementAdderMethod cloned existing statement
        merged.cardinality();
        org.junit.Assert.assertEquals(0.01, error, 0.01);
    }

    /* amplification of com.clearspring.analytics.stream.cardinality.TestCountThenEstimate#testMerge */
    @org.junit.Test(timeout = 1000)
    public void testMerge_literalMutation29772_literalMutation31327_cf42378() throws com.clearspring.analytics.stream.cardinality.CardinalityMergeException {
        int numToMerge = 10;
        int tippingPoint = 100;
        int cardinality = 999;
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(cardinality, 999);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(cardinality, 999);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(cardinality, 999);
        com.clearspring.analytics.stream.cardinality.CountThenEstimate[] ctes = new com.clearspring.analytics.stream.cardinality.CountThenEstimate[numToMerge];
        for (int i = 0; i < numToMerge; i++) {
            ctes[i] = new com.clearspring.analytics.stream.cardinality.CountThenEstimate(tippingPoint, com.clearspring.analytics.stream.cardinality.AdaptiveCounting.Builder.obyCount(100000));
            for (int j = 0; j < (tippingPoint - 1); j++) {
                ctes[i].offer(java.lang.Math.random());
            }
        }
        int expectedCardinality = numToMerge * (tippingPoint - 1);
        long mergedEstimate = com.clearspring.analytics.stream.cardinality.CountThenEstimate.mergeEstimators(ctes).cardinality();
        double error = (java.lang.Math.abs((mergedEstimate - expectedCardinality))) / ((double) (expectedCardinality));
        org.junit.Assert.assertEquals(0.01, error, 0.01);
        for (int i = 0; i < (numToMerge / 2); i++) {
            for (int j = tippingPoint - 1; j < cardinality; j++) {
                ctes[i].offer(java.lang.Math.random());
            }
        }
        expectedCardinality = (numToMerge / 2) * ((cardinality + tippingPoint) - 1);
        mergedEstimate = com.clearspring.analytics.stream.cardinality.CountThenEstimate.mergeEstimators(ctes).cardinality();
        error = (java.lang.Math.abs((mergedEstimate - expectedCardinality))) / ((double) (expectedCardinality));
        org.junit.Assert.assertEquals(0.01, error, 0.01);
        for (int i = numToMerge / 2; i < numToMerge; i++) {
            for (int j = tippingPoint - 1; j < cardinality; j++) {
                ctes[i].offer(java.lang.Math.random());
            }
        }
        expectedCardinality = numToMerge * cardinality;
        mergedEstimate = com.clearspring.analytics.stream.cardinality.CountThenEstimate.mergeEstimators(ctes).cardinality();
        error = (java.lang.Math.abs((mergedEstimate - expectedCardinality))) / ((double) (expectedCardinality));
        // StatementAddOnAssert local variable replacement
        com.clearspring.analytics.stream.cardinality.CountThenEstimate merged = com.clearspring.analytics.stream.cardinality.CountThenEstimate.mergeEstimators(ctes);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((com.clearspring.analytics.stream.cardinality.CountThenEstimate)merged).sizeof(), 3342);
        // AssertGenerator add assertion
        junit.framework.Assert.assertTrue(((com.clearspring.analytics.stream.cardinality.CountThenEstimate)merged).tipped());
        // StatementAdderMethod cloned existing statement
        merged.cardinality();
        org.junit.Assert.assertEquals(0.0, error, 0.01);
    }

    /* amplification of com.clearspring.analytics.stream.cardinality.TestCountThenEstimate#testMerge */
    @org.junit.Test(timeout = 1000)
    public void testMerge_literalMutation29773_cf31514_failAssert27_literalMutation44412() throws com.clearspring.analytics.stream.cardinality.CardinalityMergeException {
        // AssertGenerator generate try/catch block with fail statement
        try {
            int numToMerge = 10;
            int tippingPoint = 100;
            int cardinality = 500;
            // AssertGenerator add assertion
            junit.framework.Assert.assertEquals(cardinality, 500);
            // AssertGenerator add assertion
            junit.framework.Assert.assertEquals(cardinality, 500);
            com.clearspring.analytics.stream.cardinality.CountThenEstimate[] ctes = new com.clearspring.analytics.stream.cardinality.CountThenEstimate[numToMerge];
            for (int i = 0; i < numToMerge; i++) {
                ctes[i] = new com.clearspring.analytics.stream.cardinality.CountThenEstimate(tippingPoint, com.clearspring.analytics.stream.cardinality.AdaptiveCounting.Builder.obyCount(100000));
                for (int j = 0; j < (tippingPoint - 1); j++) {
                    ctes[i].offer(java.lang.Math.random());
                }
            }
            int expectedCardinality = numToMerge * (tippingPoint - 2);
            // AssertGenerator add assertion
            junit.framework.Assert.assertEquals(expectedCardinality, 980);
            long mergedEstimate = com.clearspring.analytics.stream.cardinality.CountThenEstimate.mergeEstimators(ctes).cardinality();
            double error = (java.lang.Math.abs((mergedEstimate - expectedCardinality))) / ((double) (expectedCardinality));
            for (int i = 0; i < (numToMerge / 2); i++) {
                for (int j = tippingPoint - 1; j < cardinality; j++) {
                    ctes[i].offer(java.lang.Math.random());
                }
            }
            expectedCardinality = (numToMerge / 2) * ((cardinality + tippingPoint) - 1);
            mergedEstimate = com.clearspring.analytics.stream.cardinality.CountThenEstimate.mergeEstimators(ctes).cardinality();
            error = (java.lang.Math.abs((mergedEstimate - expectedCardinality))) / ((double) (expectedCardinality));
            for (int i = numToMerge / 2; i < numToMerge; i++) {
                for (int j = tippingPoint - 1; j < cardinality; j++) {
                    ctes[i].offer(java.lang.Math.random());
                }
            }
            expectedCardinality = numToMerge * cardinality;
            mergedEstimate = com.clearspring.analytics.stream.cardinality.CountThenEstimate.mergeEstimators(ctes).cardinality();
            error = (java.lang.Math.abs((mergedEstimate - expectedCardinality))) / ((double) (expectedCardinality));
            // StatementAdderOnAssert create random local variable
            long vc_10175 = 4887288428680332985L;
            // AssertGenerator add assertion
            junit.framework.Assert.assertEquals(vc_10175, 4887288428680332985L);
            // StatementAddOnAssert local variable replacement
            com.clearspring.analytics.stream.cardinality.CountThenEstimate merged = com.clearspring.analytics.stream.cardinality.CountThenEstimate.mergeEstimators(ctes);
            // AssertGenerator add assertion
            junit.framework.Assert.assertEquals(((com.clearspring.analytics.stream.cardinality.CountThenEstimate)merged).sizeof(), 3342);
            // AssertGenerator add assertion
            junit.framework.Assert.assertTrue(((com.clearspring.analytics.stream.cardinality.CountThenEstimate)merged).tipped());
            // StatementAdderMethod cloned existing statement
            merged.offerHashed(vc_10175);
            org.junit.Assert.fail("testMerge_literalMutation29773_cf31514 should have thrown UnsupportedOperationException");
        } catch (java.lang.UnsupportedOperationException eee) {
        }
    }

    /* amplification of com.clearspring.analytics.stream.cardinality.TestCountThenEstimate#testMerge */
    @org.junit.Test(timeout = 1000)
    public void testMerge_cf29899_literalMutation37896_literalMutation39642_failAssert40() throws com.clearspring.analytics.stream.cardinality.CardinalityMergeException {
        // AssertGenerator generate try/catch block with fail statement
        try {
            int numToMerge = 10;
            int tippingPoint = 100;
            int cardinality = 1000;
            com.clearspring.analytics.stream.cardinality.CountThenEstimate[] ctes = new com.clearspring.analytics.stream.cardinality.CountThenEstimate[numToMerge];
            for (int i = 0; i < numToMerge; i++) {
                ctes[i] = new com.clearspring.analytics.stream.cardinality.CountThenEstimate(tippingPoint, com.clearspring.analytics.stream.cardinality.AdaptiveCounting.Builder.obyCount(-1));
                for (int j = 0; j < (tippingPoint - 1); j++) {
                    ctes[i].offer(java.lang.Math.random());
                }
            }
            int expectedCardinality = numToMerge * (tippingPoint - 1);
            long mergedEstimate = com.clearspring.analytics.stream.cardinality.CountThenEstimate.mergeEstimators(ctes).cardinality();
            double error = (java.lang.Math.abs((mergedEstimate - expectedCardinality))) / ((double) (expectedCardinality));
            for (int i = 0; i < (numToMerge / 2); i++) {
                for (int j = tippingPoint - 1; j < cardinality; j++) {
                    ctes[i].offer(java.lang.Math.random());
                }
            }
            expectedCardinality = (numToMerge / 2) * ((cardinality + tippingPoint) - 1);
            mergedEstimate = com.clearspring.analytics.stream.cardinality.CountThenEstimate.mergeEstimators(ctes).cardinality();
            error = (java.lang.Math.abs((mergedEstimate - expectedCardinality))) / ((double) (expectedCardinality));
            for (int i = numToMerge / 2; i < numToMerge; i++) {
                for (int j = tippingPoint - 1; j < cardinality; j++) {
                    ctes[i].offer(java.lang.Math.random());
                }
            }
            expectedCardinality = numToMerge * cardinality;
            mergedEstimate = com.clearspring.analytics.stream.cardinality.CountThenEstimate.mergeEstimators(ctes).cardinality();
            error = (java.lang.Math.abs((mergedEstimate - expectedCardinality))) / ((double) (expectedCardinality));
            // StatementAddOnAssert local variable replacement
            com.clearspring.analytics.stream.cardinality.CountThenEstimate merged = com.clearspring.analytics.stream.cardinality.CountThenEstimate.mergeEstimators(ctes);
            // AssertGenerator add assertion
            junit.framework.Assert.assertEquals(((com.clearspring.analytics.stream.cardinality.CountThenEstimate)merged).sizeof(), 3342);
            // AssertGenerator add assertion
            junit.framework.Assert.assertTrue(((com.clearspring.analytics.stream.cardinality.CountThenEstimate)merged).tipped());
            // AssertGenerator add assertion
            junit.framework.Assert.assertEquals(((com.clearspring.analytics.stream.cardinality.CountThenEstimate)merged).sizeof(), 3342);
            // AssertGenerator add assertion
            junit.framework.Assert.assertTrue(((com.clearspring.analytics.stream.cardinality.CountThenEstimate)merged).tipped());
            // StatementAdderMethod cloned existing statement
            merged.cardinality();
            org.junit.Assert.fail("testMerge_cf29899_literalMutation37896_literalMutation39642 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of com.clearspring.analytics.stream.cardinality.TestCountThenEstimate#testSmallMerge */
    @org.junit.Test(timeout = 1000)
    public void testSmallMerge_cf49212_failAssert35() throws com.clearspring.analytics.stream.cardinality.CardinalityMergeException {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // Untipped test case
            int numToMerge = 1000;
            int cardinalityPer = 5;
            com.clearspring.analytics.stream.cardinality.CountThenEstimate[] ctes = new com.clearspring.analytics.stream.cardinality.CountThenEstimate[numToMerge];
            for (int i = 0; i < numToMerge; i++) {
                ctes[i] = new com.clearspring.analytics.stream.cardinality.CountThenEstimate(10000, com.clearspring.analytics.stream.cardinality.AdaptiveCounting.Builder.obyCount(100000));
                for (int j = 0; j < cardinalityPer; j++) {
                    ctes[i].offer(java.lang.Math.random());
                }
            }
            int expectedCardinality = numToMerge * cardinalityPer;
            com.clearspring.analytics.stream.cardinality.CountThenEstimate merged = com.clearspring.analytics.stream.cardinality.CountThenEstimate.mergeEstimators(ctes);
            long mergedEstimate = merged.cardinality();
            // Tipped test case
            numToMerge = 10;
            cardinalityPer = 100;
            ctes = new com.clearspring.analytics.stream.cardinality.CountThenEstimate[numToMerge];
            for (int i = 0; i < numToMerge; i++) {
                ctes[i] = new com.clearspring.analytics.stream.cardinality.CountThenEstimate((cardinalityPer + 1), com.clearspring.analytics.stream.cardinality.AdaptiveCounting.Builder.obyCount(100000));
                for (int j = 0; j < cardinalityPer; j++) {
                    ctes[i].offer(java.lang.Math.random());
                }
            }
            expectedCardinality = numToMerge * cardinalityPer;
            merged = com.clearspring.analytics.stream.cardinality.CountThenEstimate.mergeEstimators(ctes);
            mergedEstimate = merged.cardinality();
            double error = (java.lang.Math.abs((mergedEstimate - expectedCardinality))) / ((double) (expectedCardinality));
            // StatementAdderOnAssert create random local variable
            long vc_11909 = 8772540267875753972L;
            // StatementAdderMethod cloned existing statement
            merged.offerHashed(vc_11909);
            org.junit.Assert.fail("testSmallMerge_cf49212 should have thrown UnsupportedOperationException");
        } catch (java.lang.UnsupportedOperationException eee) {
        }
    }

    /* amplification of com.clearspring.analytics.stream.cardinality.TestCountThenEstimate#testSmallMerge */
    @org.junit.Test(timeout = 1000)
    public void testSmallMerge_cf49227() throws com.clearspring.analytics.stream.cardinality.CardinalityMergeException {
        // Untipped test case
        int numToMerge = 1000;
        int cardinalityPer = 5;
        com.clearspring.analytics.stream.cardinality.CountThenEstimate[] ctes = new com.clearspring.analytics.stream.cardinality.CountThenEstimate[numToMerge];
        for (int i = 0; i < numToMerge; i++) {
            ctes[i] = new com.clearspring.analytics.stream.cardinality.CountThenEstimate(10000, com.clearspring.analytics.stream.cardinality.AdaptiveCounting.Builder.obyCount(100000));
            for (int j = 0; j < cardinalityPer; j++) {
                ctes[i].offer(java.lang.Math.random());
            }
        }
        int expectedCardinality = numToMerge * cardinalityPer;
        com.clearspring.analytics.stream.cardinality.CountThenEstimate merged = com.clearspring.analytics.stream.cardinality.CountThenEstimate.mergeEstimators(ctes);
        long mergedEstimate = merged.cardinality();
        org.junit.Assert.assertEquals(expectedCardinality, mergedEstimate);
        org.junit.Assert.assertFalse(merged.tipped);
        // Tipped test case
        numToMerge = 10;
        cardinalityPer = 100;
        ctes = new com.clearspring.analytics.stream.cardinality.CountThenEstimate[numToMerge];
        for (int i = 0; i < numToMerge; i++) {
            ctes[i] = new com.clearspring.analytics.stream.cardinality.CountThenEstimate((cardinalityPer + 1), com.clearspring.analytics.stream.cardinality.AdaptiveCounting.Builder.obyCount(100000));
            for (int j = 0; j < cardinalityPer; j++) {
                ctes[i].offer(java.lang.Math.random());
            }
        }
        expectedCardinality = numToMerge * cardinalityPer;
        merged = com.clearspring.analytics.stream.cardinality.CountThenEstimate.mergeEstimators(ctes);
        mergedEstimate = merged.cardinality();
        double error = (java.lang.Math.abs((mergedEstimate - expectedCardinality))) / ((double) (expectedCardinality));
        org.junit.Assert.assertEquals(0.01, error, 0.01);
        // StatementAdderOnAssert create null value
        com.clearspring.analytics.stream.cardinality.CountThenEstimate[] vc_11916 = (com.clearspring.analytics.stream.cardinality.CountThenEstimate[])null;
        // AssertGenerator add assertion
        junit.framework.Assert.assertNull(vc_11916);
        // StatementAdderOnAssert create null value
        com.clearspring.analytics.stream.cardinality.CountThenEstimate vc_11914 = (com.clearspring.analytics.stream.cardinality.CountThenEstimate)null;
        // AssertGenerator add assertion
        junit.framework.Assert.assertNull(vc_11914);
        // AssertGenerator replace invocation
        com.clearspring.analytics.stream.cardinality.CountThenEstimate o_testSmallMerge_cf49227__54 = // StatementAdderMethod cloned existing statement
vc_11914.mergeEstimators(vc_11916);
        // AssertGenerator add assertion
        junit.framework.Assert.assertNull(o_testSmallMerge_cf49227__54);
        org.junit.Assert.assertTrue(merged.tipped);
    }

    /* amplification of com.clearspring.analytics.stream.cardinality.TestCountThenEstimate#testSmallMerge */
    @org.junit.Test
    public void testSmallMerge_literalMutation49137() throws com.clearspring.analytics.stream.cardinality.CardinalityMergeException {
        // Untipped test case
        int numToMerge = 1000;
        int cardinalityPer = 10;
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(cardinalityPer, 10);
        com.clearspring.analytics.stream.cardinality.CountThenEstimate[] ctes = new com.clearspring.analytics.stream.cardinality.CountThenEstimate[numToMerge];
        for (int i = 0; i < numToMerge; i++) {
            ctes[i] = new com.clearspring.analytics.stream.cardinality.CountThenEstimate(10000, com.clearspring.analytics.stream.cardinality.AdaptiveCounting.Builder.obyCount(100000));
            for (int j = 0; j < cardinalityPer; j++) {
                ctes[i].offer(java.lang.Math.random());
            }
        }
        int expectedCardinality = numToMerge * cardinalityPer;
        com.clearspring.analytics.stream.cardinality.CountThenEstimate merged = com.clearspring.analytics.stream.cardinality.CountThenEstimate.mergeEstimators(ctes);
        long mergedEstimate = merged.cardinality();
        org.junit.Assert.assertEquals(expectedCardinality, mergedEstimate);
        org.junit.Assert.assertFalse(merged.tipped);
        // Tipped test case
        numToMerge = 10;
        cardinalityPer = 100;
        ctes = new com.clearspring.analytics.stream.cardinality.CountThenEstimate[numToMerge];
        for (int i = 0; i < numToMerge; i++) {
            ctes[i] = new com.clearspring.analytics.stream.cardinality.CountThenEstimate((cardinalityPer + 1), com.clearspring.analytics.stream.cardinality.AdaptiveCounting.Builder.obyCount(100000));
            for (int j = 0; j < cardinalityPer; j++) {
                ctes[i].offer(java.lang.Math.random());
            }
        }
        expectedCardinality = numToMerge * cardinalityPer;
        merged = com.clearspring.analytics.stream.cardinality.CountThenEstimate.mergeEstimators(ctes);
        mergedEstimate = merged.cardinality();
        double error = (java.lang.Math.abs((mergedEstimate - expectedCardinality))) / ((double) (expectedCardinality));
        org.junit.Assert.assertEquals(0.01, error, 0.01);
        org.junit.Assert.assertTrue(merged.tipped);
    }

    /* amplification of com.clearspring.analytics.stream.cardinality.TestCountThenEstimate#testSmallMerge */
    @org.junit.Test
    public void testSmallMerge_literalMutation49129_failAssert2() throws com.clearspring.analytics.stream.cardinality.CardinalityMergeException {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // Untipped test case
            int numToMerge = 0;
            int cardinalityPer = 5;
            com.clearspring.analytics.stream.cardinality.CountThenEstimate[] ctes = new com.clearspring.analytics.stream.cardinality.CountThenEstimate[numToMerge];
            for (int i = 0; i < numToMerge; i++) {
                ctes[i] = new com.clearspring.analytics.stream.cardinality.CountThenEstimate(10000, com.clearspring.analytics.stream.cardinality.AdaptiveCounting.Builder.obyCount(100000));
                for (int j = 0; j < cardinalityPer; j++) {
                    ctes[i].offer(java.lang.Math.random());
                }
            }
            int expectedCardinality = numToMerge * cardinalityPer;
            com.clearspring.analytics.stream.cardinality.CountThenEstimate merged = com.clearspring.analytics.stream.cardinality.CountThenEstimate.mergeEstimators(ctes);
            long mergedEstimate = merged.cardinality();
            // Tipped test case
            numToMerge = 10;
            cardinalityPer = 100;
            ctes = new com.clearspring.analytics.stream.cardinality.CountThenEstimate[numToMerge];
            for (int i = 0; i < numToMerge; i++) {
                ctes[i] = new com.clearspring.analytics.stream.cardinality.CountThenEstimate((cardinalityPer + 1), com.clearspring.analytics.stream.cardinality.AdaptiveCounting.Builder.obyCount(100000));
                for (int j = 0; j < cardinalityPer; j++) {
                    ctes[i].offer(java.lang.Math.random());
                }
            }
            expectedCardinality = numToMerge * cardinalityPer;
            merged = com.clearspring.analytics.stream.cardinality.CountThenEstimate.mergeEstimators(ctes);
            mergedEstimate = merged.cardinality();
            double error = (java.lang.Math.abs((mergedEstimate - expectedCardinality))) / ((double) (expectedCardinality));
            org.junit.Assert.fail("testSmallMerge_literalMutation49129 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    /* amplification of com.clearspring.analytics.stream.cardinality.TestCountThenEstimate#testSmallMerge */
    @org.junit.Test(timeout = 1000)
    public void testSmallMerge_cf49205_failAssert30() throws com.clearspring.analytics.stream.cardinality.CardinalityMergeException {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // Untipped test case
            int numToMerge = 1000;
            int cardinalityPer = 5;
            com.clearspring.analytics.stream.cardinality.CountThenEstimate[] ctes = new com.clearspring.analytics.stream.cardinality.CountThenEstimate[numToMerge];
            for (int i = 0; i < numToMerge; i++) {
                ctes[i] = new com.clearspring.analytics.stream.cardinality.CountThenEstimate(10000, com.clearspring.analytics.stream.cardinality.AdaptiveCounting.Builder.obyCount(100000));
                for (int j = 0; j < cardinalityPer; j++) {
                    ctes[i].offer(java.lang.Math.random());
                }
            }
            int expectedCardinality = numToMerge * cardinalityPer;
            com.clearspring.analytics.stream.cardinality.CountThenEstimate merged = com.clearspring.analytics.stream.cardinality.CountThenEstimate.mergeEstimators(ctes);
            long mergedEstimate = merged.cardinality();
            // Tipped test case
            numToMerge = 10;
            cardinalityPer = 100;
            ctes = new com.clearspring.analytics.stream.cardinality.CountThenEstimate[numToMerge];
            for (int i = 0; i < numToMerge; i++) {
                ctes[i] = new com.clearspring.analytics.stream.cardinality.CountThenEstimate((cardinalityPer + 1), com.clearspring.analytics.stream.cardinality.AdaptiveCounting.Builder.obyCount(100000));
                for (int j = 0; j < cardinalityPer; j++) {
                    ctes[i].offer(java.lang.Math.random());
                }
            }
            expectedCardinality = numToMerge * cardinalityPer;
            merged = com.clearspring.analytics.stream.cardinality.CountThenEstimate.mergeEstimators(ctes);
            mergedEstimate = merged.cardinality();
            double error = (java.lang.Math.abs((mergedEstimate - expectedCardinality))) / ((double) (expectedCardinality));
            // StatementAdderOnAssert create literal from method
            int int_vc_365 = 0;
            // StatementAdderOnAssert create random local variable
            com.clearspring.analytics.stream.cardinality.CountThenEstimate vc_11905 = new com.clearspring.analytics.stream.cardinality.CountThenEstimate();
            // StatementAdderMethod cloned existing statement
            vc_11905.offerHashed(int_vc_365);
            org.junit.Assert.fail("testSmallMerge_cf49205 should have thrown UnsupportedOperationException");
        } catch (java.lang.UnsupportedOperationException eee) {
        }
    }

    /* amplification of com.clearspring.analytics.stream.cardinality.TestCountThenEstimate#testSmallMerge */
    @org.junit.Test(timeout = 1000)
    public void testSmallMerge_cf49242() throws com.clearspring.analytics.stream.cardinality.CardinalityMergeException {
        // Untipped test case
        int numToMerge = 1000;
        int cardinalityPer = 5;
        com.clearspring.analytics.stream.cardinality.CountThenEstimate[] ctes = new com.clearspring.analytics.stream.cardinality.CountThenEstimate[numToMerge];
        for (int i = 0; i < numToMerge; i++) {
            ctes[i] = new com.clearspring.analytics.stream.cardinality.CountThenEstimate(10000, com.clearspring.analytics.stream.cardinality.AdaptiveCounting.Builder.obyCount(100000));
            for (int j = 0; j < cardinalityPer; j++) {
                ctes[i].offer(java.lang.Math.random());
            }
        }
        int expectedCardinality = numToMerge * cardinalityPer;
        com.clearspring.analytics.stream.cardinality.CountThenEstimate merged = com.clearspring.analytics.stream.cardinality.CountThenEstimate.mergeEstimators(ctes);
        long mergedEstimate = merged.cardinality();
        org.junit.Assert.assertEquals(expectedCardinality, mergedEstimate);
        org.junit.Assert.assertFalse(merged.tipped);
        // Tipped test case
        numToMerge = 10;
        cardinalityPer = 100;
        ctes = new com.clearspring.analytics.stream.cardinality.CountThenEstimate[numToMerge];
        for (int i = 0; i < numToMerge; i++) {
            ctes[i] = new com.clearspring.analytics.stream.cardinality.CountThenEstimate((cardinalityPer + 1), com.clearspring.analytics.stream.cardinality.AdaptiveCounting.Builder.obyCount(100000));
            for (int j = 0; j < cardinalityPer; j++) {
                ctes[i].offer(java.lang.Math.random());
            }
        }
        expectedCardinality = numToMerge * cardinalityPer;
        merged = com.clearspring.analytics.stream.cardinality.CountThenEstimate.mergeEstimators(ctes);
        mergedEstimate = merged.cardinality();
        double error = (java.lang.Math.abs((mergedEstimate - expectedCardinality))) / ((double) (expectedCardinality));
        org.junit.Assert.assertEquals(0.01, error, 0.01);
        // StatementAdderOnAssert create null value
        com.clearspring.analytics.stream.cardinality.ICardinality[] vc_11920 = (com.clearspring.analytics.stream.cardinality.ICardinality[])null;
        // AssertGenerator add assertion
        junit.framework.Assert.assertNull(vc_11920);
        // AssertGenerator replace invocation
        com.clearspring.analytics.stream.cardinality.ICardinality o_testSmallMerge_cf49242__52 = // StatementAdderMethod cloned existing statement
merged.merge(vc_11920);
        // AssertGenerator add assertion
        junit.framework.Assert.assertTrue(((com.clearspring.analytics.stream.cardinality.CountThenEstimate)o_testSmallMerge_cf49242__52).tipped());
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((com.clearspring.analytics.stream.cardinality.CountThenEstimate)o_testSmallMerge_cf49242__52).sizeof(), 3342);
        org.junit.Assert.assertTrue(merged.tipped);
    }

    /* amplification of com.clearspring.analytics.stream.cardinality.TestCountThenEstimate#testSmallMerge */
    @org.junit.Test(timeout = 1000)
    public void testSmallMerge_cf49231() throws com.clearspring.analytics.stream.cardinality.CardinalityMergeException {
        // Untipped test case
        int numToMerge = 1000;
        int cardinalityPer = 5;
        com.clearspring.analytics.stream.cardinality.CountThenEstimate[] ctes = new com.clearspring.analytics.stream.cardinality.CountThenEstimate[numToMerge];
        for (int i = 0; i < numToMerge; i++) {
            ctes[i] = new com.clearspring.analytics.stream.cardinality.CountThenEstimate(10000, com.clearspring.analytics.stream.cardinality.AdaptiveCounting.Builder.obyCount(100000));
            for (int j = 0; j < cardinalityPer; j++) {
                ctes[i].offer(java.lang.Math.random());
            }
        }
        int expectedCardinality = numToMerge * cardinalityPer;
        com.clearspring.analytics.stream.cardinality.CountThenEstimate merged = com.clearspring.analytics.stream.cardinality.CountThenEstimate.mergeEstimators(ctes);
        long mergedEstimate = merged.cardinality();
        org.junit.Assert.assertEquals(expectedCardinality, mergedEstimate);
        org.junit.Assert.assertFalse(merged.tipped);
        // Tipped test case
        numToMerge = 10;
        cardinalityPer = 100;
        ctes = new com.clearspring.analytics.stream.cardinality.CountThenEstimate[numToMerge];
        for (int i = 0; i < numToMerge; i++) {
            ctes[i] = new com.clearspring.analytics.stream.cardinality.CountThenEstimate((cardinalityPer + 1), com.clearspring.analytics.stream.cardinality.AdaptiveCounting.Builder.obyCount(100000));
            for (int j = 0; j < cardinalityPer; j++) {
                ctes[i].offer(java.lang.Math.random());
            }
        }
        expectedCardinality = numToMerge * cardinalityPer;
        merged = com.clearspring.analytics.stream.cardinality.CountThenEstimate.mergeEstimators(ctes);
        mergedEstimate = merged.cardinality();
        double error = (java.lang.Math.abs((mergedEstimate - expectedCardinality))) / ((double) (expectedCardinality));
        org.junit.Assert.assertEquals(0.01, error, 0.01);
        // AssertGenerator replace invocation
        com.clearspring.analytics.stream.cardinality.CountThenEstimate o_testSmallMerge_cf49231__50 = // StatementAdderMethod cloned existing statement
merged.mergeEstimators(ctes);
        // AssertGenerator add assertion
        junit.framework.Assert.assertTrue(((com.clearspring.analytics.stream.cardinality.CountThenEstimate)o_testSmallMerge_cf49231__50).tipped());
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((com.clearspring.analytics.stream.cardinality.CountThenEstimate)o_testSmallMerge_cf49231__50).sizeof(), 3342);
        org.junit.Assert.assertTrue(merged.tipped);
    }

    /* amplification of com.clearspring.analytics.stream.cardinality.TestCountThenEstimate#testSmallMerge */
    @org.junit.Test(timeout = 1000)
    public void testSmallMerge_cf49186() throws com.clearspring.analytics.stream.cardinality.CardinalityMergeException {
        // Untipped test case
        int numToMerge = 1000;
        int cardinalityPer = 5;
        com.clearspring.analytics.stream.cardinality.CountThenEstimate[] ctes = new com.clearspring.analytics.stream.cardinality.CountThenEstimate[numToMerge];
        for (int i = 0; i < numToMerge; i++) {
            ctes[i] = new com.clearspring.analytics.stream.cardinality.CountThenEstimate(10000, com.clearspring.analytics.stream.cardinality.AdaptiveCounting.Builder.obyCount(100000));
            for (int j = 0; j < cardinalityPer; j++) {
                ctes[i].offer(java.lang.Math.random());
            }
        }
        int expectedCardinality = numToMerge * cardinalityPer;
        com.clearspring.analytics.stream.cardinality.CountThenEstimate merged = com.clearspring.analytics.stream.cardinality.CountThenEstimate.mergeEstimators(ctes);
        long mergedEstimate = merged.cardinality();
        org.junit.Assert.assertEquals(expectedCardinality, mergedEstimate);
        org.junit.Assert.assertFalse(merged.tipped);
        // Tipped test case
        numToMerge = 10;
        cardinalityPer = 100;
        ctes = new com.clearspring.analytics.stream.cardinality.CountThenEstimate[numToMerge];
        for (int i = 0; i < numToMerge; i++) {
            ctes[i] = new com.clearspring.analytics.stream.cardinality.CountThenEstimate((cardinalityPer + 1), com.clearspring.analytics.stream.cardinality.AdaptiveCounting.Builder.obyCount(100000));
            for (int j = 0; j < cardinalityPer; j++) {
                ctes[i].offer(java.lang.Math.random());
            }
        }
        expectedCardinality = numToMerge * cardinalityPer;
        merged = com.clearspring.analytics.stream.cardinality.CountThenEstimate.mergeEstimators(ctes);
        mergedEstimate = merged.cardinality();
        double error = (java.lang.Math.abs((mergedEstimate - expectedCardinality))) / ((double) (expectedCardinality));
        org.junit.Assert.assertEquals(0.01, error, 0.01);
        // StatementAdderOnAssert create random local variable
        java.lang.Object vc_11903 = new java.lang.Object();
        // AssertGenerator replace invocation
        boolean o_testSmallMerge_cf49186__52 = // StatementAdderMethod cloned existing statement
merged.offer(vc_11903);
        // AssertGenerator add assertion
        junit.framework.Assert.assertTrue(o_testSmallMerge_cf49186__52);
        org.junit.Assert.assertTrue(merged.tipped);
    }

    /* amplification of com.clearspring.analytics.stream.cardinality.TestCountThenEstimate#testSmallMerge */
    @org.junit.Test(timeout = 1000)
    public void testSmallMerge_cf49220_cf53896() throws com.clearspring.analytics.stream.cardinality.CardinalityMergeException {
        // Untipped test case
        int numToMerge = 1000;
        int cardinalityPer = 5;
        com.clearspring.analytics.stream.cardinality.CountThenEstimate[] ctes = new com.clearspring.analytics.stream.cardinality.CountThenEstimate[numToMerge];
        for (int i = 0; i < numToMerge; i++) {
            ctes[i] = new com.clearspring.analytics.stream.cardinality.CountThenEstimate(10000, com.clearspring.analytics.stream.cardinality.AdaptiveCounting.Builder.obyCount(100000));
            for (int j = 0; j < cardinalityPer; j++) {
                ctes[i].offer(java.lang.Math.random());
            }
        }
        int expectedCardinality = numToMerge * cardinalityPer;
        com.clearspring.analytics.stream.cardinality.CountThenEstimate merged = com.clearspring.analytics.stream.cardinality.CountThenEstimate.mergeEstimators(ctes);
        long mergedEstimate = merged.cardinality();
        org.junit.Assert.assertEquals(expectedCardinality, mergedEstimate);
        org.junit.Assert.assertFalse(merged.tipped);
        // Tipped test case
        numToMerge = 10;
        cardinalityPer = 100;
        ctes = new com.clearspring.analytics.stream.cardinality.CountThenEstimate[numToMerge];
        for (int i = 0; i < numToMerge; i++) {
            ctes[i] = new com.clearspring.analytics.stream.cardinality.CountThenEstimate((cardinalityPer + 1), com.clearspring.analytics.stream.cardinality.AdaptiveCounting.Builder.obyCount(100000));
            for (int j = 0; j < cardinalityPer; j++) {
                ctes[i].offer(java.lang.Math.random());
            }
        }
        expectedCardinality = numToMerge * cardinalityPer;
        merged = com.clearspring.analytics.stream.cardinality.CountThenEstimate.mergeEstimators(ctes);
        mergedEstimate = merged.cardinality();
        double error = (java.lang.Math.abs((mergedEstimate - expectedCardinality))) / ((double) (expectedCardinality));
        org.junit.Assert.assertEquals(0.01, error, 0.01);
        // AssertGenerator replace invocation
        boolean o_testSmallMerge_cf49220__50 = // StatementAdderMethod cloned existing statement
merged.tipped();
        // AssertGenerator add assertion
        junit.framework.Assert.assertTrue(o_testSmallMerge_cf49220__50);
        // AssertGenerator replace invocation
        com.clearspring.analytics.stream.cardinality.CountThenEstimate o_testSmallMerge_cf49220_cf53896__54 = // StatementAdderMethod cloned existing statement
merged.mergeEstimators(ctes);
        // AssertGenerator add assertion
        junit.framework.Assert.assertTrue(((com.clearspring.analytics.stream.cardinality.CountThenEstimate)o_testSmallMerge_cf49220_cf53896__54).tipped());
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((com.clearspring.analytics.stream.cardinality.CountThenEstimate)o_testSmallMerge_cf49220_cf53896__54).sizeof(), 3342);
        org.junit.Assert.assertTrue(merged.tipped);
    }

    /* amplification of com.clearspring.analytics.stream.cardinality.TestCountThenEstimate#testSmallMerge */
    @org.junit.Test(timeout = 1000)
    public void testSmallMerge_cf49230_literalMutation54289() throws com.clearspring.analytics.stream.cardinality.CardinalityMergeException {
        // Untipped test case
        int numToMerge = 1000;
        int cardinalityPer = 5;
        com.clearspring.analytics.stream.cardinality.CountThenEstimate[] ctes = new com.clearspring.analytics.stream.cardinality.CountThenEstimate[numToMerge];
        for (int i = 0; i < numToMerge; i++) {
            ctes[i] = new com.clearspring.analytics.stream.cardinality.CountThenEstimate(10000, com.clearspring.analytics.stream.cardinality.AdaptiveCounting.Builder.obyCount(100000));
            for (int j = 0; j < cardinalityPer; j++) {
                ctes[i].offer(java.lang.Math.random());
            }
        }
        int expectedCardinality = numToMerge * cardinalityPer;
        com.clearspring.analytics.stream.cardinality.CountThenEstimate merged = com.clearspring.analytics.stream.cardinality.CountThenEstimate.mergeEstimators(ctes);
        long mergedEstimate = merged.cardinality();
        org.junit.Assert.assertEquals(expectedCardinality, mergedEstimate);
        org.junit.Assert.assertFalse(merged.tipped);
        // Tipped test case
        numToMerge = 10;
        cardinalityPer = 100;
        ctes = new com.clearspring.analytics.stream.cardinality.CountThenEstimate[numToMerge];
        for (int i = 0; i < numToMerge; i++) {
            ctes[i] = new com.clearspring.analytics.stream.cardinality.CountThenEstimate((cardinalityPer + 1), com.clearspring.analytics.stream.cardinality.AdaptiveCounting.Builder.obyCount(100000));
            for (int j = 0; j < cardinalityPer; j++) {
                ctes[i].offer(java.lang.Math.random());
            }
        }
        expectedCardinality = numToMerge * cardinalityPer;
        merged = com.clearspring.analytics.stream.cardinality.CountThenEstimate.mergeEstimators(ctes);
        mergedEstimate = merged.cardinality();
        double error = (java.lang.Math.abs((mergedEstimate - expectedCardinality))) / ((double) (expectedCardinality));
        org.junit.Assert.assertEquals(0.0, error, 0.01);
        // StatementAdderOnAssert create null value
        com.clearspring.analytics.stream.cardinality.CountThenEstimate[] vc_11916 = (com.clearspring.analytics.stream.cardinality.CountThenEstimate[])null;
        // AssertGenerator add assertion
        junit.framework.Assert.assertNull(vc_11916);
        // AssertGenerator add assertion
        junit.framework.Assert.assertNull(vc_11916);
        // AssertGenerator replace invocation
        com.clearspring.analytics.stream.cardinality.CountThenEstimate o_testSmallMerge_cf49230__52 = // StatementAdderMethod cloned existing statement
merged.mergeEstimators(vc_11916);
        // AssertGenerator add assertion
        junit.framework.Assert.assertNull(o_testSmallMerge_cf49230__52);
        org.junit.Assert.assertTrue(merged.tipped);
    }

    /* amplification of com.clearspring.analytics.stream.cardinality.TestCountThenEstimate#testSmallMerge */
    @org.junit.Test
    public void testSmallMerge_literalMutation49133_literalMutation49902() throws com.clearspring.analytics.stream.cardinality.CardinalityMergeException {
        // Untipped test case
        int numToMerge = 2000;
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(numToMerge, 2000);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(numToMerge, 2000);
        int cardinalityPer = 5;
        com.clearspring.analytics.stream.cardinality.CountThenEstimate[] ctes = new com.clearspring.analytics.stream.cardinality.CountThenEstimate[numToMerge];
        for (int i = 0; i < numToMerge; i++) {
            ctes[i] = new com.clearspring.analytics.stream.cardinality.CountThenEstimate(10000, com.clearspring.analytics.stream.cardinality.AdaptiveCounting.Builder.obyCount(100000));
            for (int j = 0; j < cardinalityPer; j++) {
                ctes[i].offer(java.lang.Math.random());
            }
        }
        int expectedCardinality = numToMerge * cardinalityPer;
        com.clearspring.analytics.stream.cardinality.CountThenEstimate merged = com.clearspring.analytics.stream.cardinality.CountThenEstimate.mergeEstimators(ctes);
        long mergedEstimate = merged.cardinality();
        org.junit.Assert.assertEquals(expectedCardinality, mergedEstimate);
        org.junit.Assert.assertFalse(merged.tipped);
        // Tipped test case
        numToMerge = 10;
        cardinalityPer = 100;
        ctes = new com.clearspring.analytics.stream.cardinality.CountThenEstimate[numToMerge];
        for (int i = 0; i < numToMerge; i++) {
            ctes[i] = new com.clearspring.analytics.stream.cardinality.CountThenEstimate((cardinalityPer + 1), com.clearspring.analytics.stream.cardinality.AdaptiveCounting.Builder.obyCount(100000));
            for (int j = 0; j < cardinalityPer; j++) {
                ctes[i].offer(java.lang.Math.random());
            }
        }
        expectedCardinality = numToMerge * cardinalityPer;
        merged = com.clearspring.analytics.stream.cardinality.CountThenEstimate.mergeEstimators(ctes);
        mergedEstimate = merged.cardinality();
        double error = (java.lang.Math.abs((mergedEstimate - expectedCardinality))) / ((double) (expectedCardinality));
        org.junit.Assert.assertEquals(0.01, error, 0.01);
        org.junit.Assert.assertTrue(merged.tipped);
    }

    /* amplification of com.clearspring.analytics.stream.cardinality.TestCountThenEstimate#testSmallMerge */
    @org.junit.Test(timeout = 1000)
    public void testSmallMerge_cf49186_cf52946() throws com.clearspring.analytics.stream.cardinality.CardinalityMergeException {
        // Untipped test case
        int numToMerge = 1000;
        int cardinalityPer = 5;
        com.clearspring.analytics.stream.cardinality.CountThenEstimate[] ctes = new com.clearspring.analytics.stream.cardinality.CountThenEstimate[numToMerge];
        for (int i = 0; i < numToMerge; i++) {
            ctes[i] = new com.clearspring.analytics.stream.cardinality.CountThenEstimate(10000, com.clearspring.analytics.stream.cardinality.AdaptiveCounting.Builder.obyCount(100000));
            for (int j = 0; j < cardinalityPer; j++) {
                ctes[i].offer(java.lang.Math.random());
            }
        }
        int expectedCardinality = numToMerge * cardinalityPer;
        com.clearspring.analytics.stream.cardinality.CountThenEstimate merged = com.clearspring.analytics.stream.cardinality.CountThenEstimate.mergeEstimators(ctes);
        long mergedEstimate = merged.cardinality();
        org.junit.Assert.assertEquals(expectedCardinality, mergedEstimate);
        org.junit.Assert.assertFalse(merged.tipped);
        // Tipped test case
        numToMerge = 10;
        cardinalityPer = 100;
        ctes = new com.clearspring.analytics.stream.cardinality.CountThenEstimate[numToMerge];
        for (int i = 0; i < numToMerge; i++) {
            ctes[i] = new com.clearspring.analytics.stream.cardinality.CountThenEstimate((cardinalityPer + 1), com.clearspring.analytics.stream.cardinality.AdaptiveCounting.Builder.obyCount(100000));
            for (int j = 0; j < cardinalityPer; j++) {
                ctes[i].offer(java.lang.Math.random());
            }
        }
        expectedCardinality = numToMerge * cardinalityPer;
        merged = com.clearspring.analytics.stream.cardinality.CountThenEstimate.mergeEstimators(ctes);
        mergedEstimate = merged.cardinality();
        double error = (java.lang.Math.abs((mergedEstimate - expectedCardinality))) / ((double) (expectedCardinality));
        org.junit.Assert.assertEquals(0.01, error, 0.01);
        // StatementAdderOnAssert create random local variable
        java.lang.Object vc_11903 = new java.lang.Object();
        // AssertGenerator replace invocation
        boolean o_testSmallMerge_cf49186__52 = // StatementAdderMethod cloned existing statement
merged.offer(vc_11903);
        // AssertGenerator add assertion
        junit.framework.Assert.assertTrue(o_testSmallMerge_cf49186__52);
        // StatementAdderOnAssert create null value
        com.clearspring.analytics.stream.cardinality.CountThenEstimate vc_12526 = (com.clearspring.analytics.stream.cardinality.CountThenEstimate)null;
        // AssertGenerator add assertion
        junit.framework.Assert.assertNull(vc_12526);
        // AssertGenerator replace invocation
        com.clearspring.analytics.stream.cardinality.CountThenEstimate o_testSmallMerge_cf49186_cf52946__58 = // StatementAdderMethod cloned existing statement
vc_12526.mergeEstimators(ctes);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((com.clearspring.analytics.stream.cardinality.CountThenEstimate)o_testSmallMerge_cf49186_cf52946__58).sizeof(), 3342);
        // AssertGenerator add assertion
        junit.framework.Assert.assertTrue(((com.clearspring.analytics.stream.cardinality.CountThenEstimate)o_testSmallMerge_cf49186_cf52946__58).tipped());
        org.junit.Assert.assertTrue(merged.tipped);
    }

    /* amplification of com.clearspring.analytics.stream.cardinality.TestCountThenEstimate#testSmallMerge */
    @org.junit.Test(timeout = 1000)
    public void testSmallMerge_cf49242_cf54638() throws com.clearspring.analytics.stream.cardinality.CardinalityMergeException {
        // Untipped test case
        int numToMerge = 1000;
        int cardinalityPer = 5;
        com.clearspring.analytics.stream.cardinality.CountThenEstimate[] ctes = new com.clearspring.analytics.stream.cardinality.CountThenEstimate[numToMerge];
        for (int i = 0; i < numToMerge; i++) {
            ctes[i] = new com.clearspring.analytics.stream.cardinality.CountThenEstimate(10000, com.clearspring.analytics.stream.cardinality.AdaptiveCounting.Builder.obyCount(100000));
            for (int j = 0; j < cardinalityPer; j++) {
                ctes[i].offer(java.lang.Math.random());
            }
        }
        int expectedCardinality = numToMerge * cardinalityPer;
        com.clearspring.analytics.stream.cardinality.CountThenEstimate merged = com.clearspring.analytics.stream.cardinality.CountThenEstimate.mergeEstimators(ctes);
        long mergedEstimate = merged.cardinality();
        org.junit.Assert.assertEquals(expectedCardinality, mergedEstimate);
        org.junit.Assert.assertFalse(merged.tipped);
        // Tipped test case
        numToMerge = 10;
        cardinalityPer = 100;
        ctes = new com.clearspring.analytics.stream.cardinality.CountThenEstimate[numToMerge];
        for (int i = 0; i < numToMerge; i++) {
            ctes[i] = new com.clearspring.analytics.stream.cardinality.CountThenEstimate((cardinalityPer + 1), com.clearspring.analytics.stream.cardinality.AdaptiveCounting.Builder.obyCount(100000));
            for (int j = 0; j < cardinalityPer; j++) {
                ctes[i].offer(java.lang.Math.random());
            }
        }
        expectedCardinality = numToMerge * cardinalityPer;
        merged = com.clearspring.analytics.stream.cardinality.CountThenEstimate.mergeEstimators(ctes);
        mergedEstimate = merged.cardinality();
        double error = (java.lang.Math.abs((mergedEstimate - expectedCardinality))) / ((double) (expectedCardinality));
        org.junit.Assert.assertEquals(0.01, error, 0.01);
        // StatementAdderOnAssert create null value
        com.clearspring.analytics.stream.cardinality.ICardinality[] vc_11920 = (com.clearspring.analytics.stream.cardinality.ICardinality[])null;
        // AssertGenerator add assertion
        junit.framework.Assert.assertNull(vc_11920);
        // AssertGenerator add assertion
        junit.framework.Assert.assertNull(vc_11920);
        // AssertGenerator replace invocation
        com.clearspring.analytics.stream.cardinality.ICardinality o_testSmallMerge_cf49242__52 = // StatementAdderMethod cloned existing statement
merged.merge(vc_11920);
        // AssertGenerator add assertion
        junit.framework.Assert.assertTrue(((com.clearspring.analytics.stream.cardinality.CountThenEstimate)o_testSmallMerge_cf49242__52).tipped());
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((com.clearspring.analytics.stream.cardinality.CountThenEstimate)o_testSmallMerge_cf49242__52).sizeof(), 3342);
        // StatementAdderOnAssert create random local variable
        java.lang.Object vc_12719 = new java.lang.Object();
        // AssertGenerator replace invocation
        boolean o_testSmallMerge_cf49242_cf54638__62 = // StatementAdderMethod cloned existing statement
merged.offer(vc_12719);
        // AssertGenerator add assertion
        junit.framework.Assert.assertTrue(o_testSmallMerge_cf49242_cf54638__62);
        org.junit.Assert.assertTrue(merged.tipped);
    }

    /* amplification of com.clearspring.analytics.stream.cardinality.TestCountThenEstimate#testSmallMerge */
    @org.junit.Test
    public void testSmallMerge_literalMutation49156_literalMutation51598() throws com.clearspring.analytics.stream.cardinality.CardinalityMergeException {
        // Untipped test case
        int numToMerge = 1000;
        int cardinalityPer = 5;
        com.clearspring.analytics.stream.cardinality.CountThenEstimate[] ctes = new com.clearspring.analytics.stream.cardinality.CountThenEstimate[numToMerge];
        for (int i = 0; i < numToMerge; i++) {
            ctes[i] = new com.clearspring.analytics.stream.cardinality.CountThenEstimate(10000, com.clearspring.analytics.stream.cardinality.AdaptiveCounting.Builder.obyCount(100000));
            for (int j = 0; j < cardinalityPer; j++) {
                ctes[i].offer(java.lang.Math.random());
            }
        }
        int expectedCardinality = numToMerge * cardinalityPer;
        com.clearspring.analytics.stream.cardinality.CountThenEstimate merged = com.clearspring.analytics.stream.cardinality.CountThenEstimate.mergeEstimators(ctes);
        long mergedEstimate = merged.cardinality();
        org.junit.Assert.assertEquals(expectedCardinality, mergedEstimate);
        org.junit.Assert.assertFalse(merged.tipped);
        // Tipped test case
        numToMerge = 11;
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(numToMerge, 11);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(numToMerge, 11);
        cardinalityPer = 100;
        ctes = new com.clearspring.analytics.stream.cardinality.CountThenEstimate[numToMerge];
        for (int i = 0; i < numToMerge; i++) {
            ctes[i] = new com.clearspring.analytics.stream.cardinality.CountThenEstimate((cardinalityPer + 1), com.clearspring.analytics.stream.cardinality.AdaptiveCounting.Builder.obyCount(100000));
            for (int j = 0; j < cardinalityPer; j++) {
                ctes[i].offer(java.lang.Math.random());
            }
        }
        expectedCardinality = numToMerge * cardinalityPer;
        merged = com.clearspring.analytics.stream.cardinality.CountThenEstimate.mergeEstimators(ctes);
        mergedEstimate = merged.cardinality();
        double error = (java.lang.Math.abs((mergedEstimate - expectedCardinality))) / ((double) (expectedCardinality));
        org.junit.Assert.assertEquals(0.0, error, 0.01);
        org.junit.Assert.assertTrue(merged.tipped);
    }

    /* amplification of com.clearspring.analytics.stream.cardinality.TestCountThenEstimate#testSmallMerge */
    @org.junit.Test(timeout = 1000)
    public void testSmallMerge_cf49186_cf52915_failAssert0() throws com.clearspring.analytics.stream.cardinality.CardinalityMergeException {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // Untipped test case
            int numToMerge = 1000;
            int cardinalityPer = 5;
            com.clearspring.analytics.stream.cardinality.CountThenEstimate[] ctes = new com.clearspring.analytics.stream.cardinality.CountThenEstimate[numToMerge];
            for (int i = 0; i < numToMerge; i++) {
                ctes[i] = new com.clearspring.analytics.stream.cardinality.CountThenEstimate(10000, com.clearspring.analytics.stream.cardinality.AdaptiveCounting.Builder.obyCount(100000));
                for (int j = 0; j < cardinalityPer; j++) {
                    ctes[i].offer(java.lang.Math.random());
                }
            }
            int expectedCardinality = numToMerge * cardinalityPer;
            com.clearspring.analytics.stream.cardinality.CountThenEstimate merged = com.clearspring.analytics.stream.cardinality.CountThenEstimate.mergeEstimators(ctes);
            long mergedEstimate = merged.cardinality();
            // Tipped test case
            numToMerge = 10;
            cardinalityPer = 100;
            ctes = new com.clearspring.analytics.stream.cardinality.CountThenEstimate[numToMerge];
            for (int i = 0; i < numToMerge; i++) {
                ctes[i] = new com.clearspring.analytics.stream.cardinality.CountThenEstimate((cardinalityPer + 1), com.clearspring.analytics.stream.cardinality.AdaptiveCounting.Builder.obyCount(100000));
                for (int j = 0; j < cardinalityPer; j++) {
                    ctes[i].offer(java.lang.Math.random());
                }
            }
            expectedCardinality = numToMerge * cardinalityPer;
            merged = com.clearspring.analytics.stream.cardinality.CountThenEstimate.mergeEstimators(ctes);
            mergedEstimate = merged.cardinality();
            double error = (java.lang.Math.abs((mergedEstimate - expectedCardinality))) / ((double) (expectedCardinality));
            // StatementAdderOnAssert create random local variable
            java.lang.Object vc_11903 = new java.lang.Object();
            // AssertGenerator replace invocation
            boolean o_testSmallMerge_cf49186__52 = // StatementAdderMethod cloned existing statement
merged.offer(vc_11903);
            // AssertGenerator add assertion
            junit.framework.Assert.assertTrue(o_testSmallMerge_cf49186__52);
            // StatementAdderOnAssert create literal from method
            int int_vc_383 = 1;
            // StatementAdderMethod cloned existing statement
            merged.offerHashed(int_vc_383);
            org.junit.Assert.fail("testSmallMerge_cf49186_cf52915 should have thrown UnsupportedOperationException");
        } catch (java.lang.UnsupportedOperationException eee) {
        }
    }

    /* amplification of com.clearspring.analytics.stream.cardinality.TestCountThenEstimate#testSmallMerge */
    @org.junit.Test(timeout = 1000)
    public void testSmallMerge_cf49197_failAssert27_literalMutation53210() throws com.clearspring.analytics.stream.cardinality.CardinalityMergeException {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // Untipped test case
            int numToMerge = 6;
            // AssertGenerator add assertion
            junit.framework.Assert.assertEquals(numToMerge, 6);
            int cardinalityPer = 5;
            com.clearspring.analytics.stream.cardinality.CountThenEstimate[] ctes = new com.clearspring.analytics.stream.cardinality.CountThenEstimate[numToMerge];
            for (int i = 0; i < numToMerge; i++) {
                ctes[i] = new com.clearspring.analytics.stream.cardinality.CountThenEstimate(10000, com.clearspring.analytics.stream.cardinality.AdaptiveCounting.Builder.obyCount(100000));
                for (int j = 0; j < cardinalityPer; j++) {
                    ctes[i].offer(java.lang.Math.random());
                }
            }
            int expectedCardinality = numToMerge * cardinalityPer;
            com.clearspring.analytics.stream.cardinality.CountThenEstimate merged = com.clearspring.analytics.stream.cardinality.CountThenEstimate.mergeEstimators(ctes);
            long mergedEstimate = merged.cardinality();
            // Tipped test case
            numToMerge = 10;
            cardinalityPer = 100;
            ctes = new com.clearspring.analytics.stream.cardinality.CountThenEstimate[numToMerge];
            for (int i = 0; i < numToMerge; i++) {
                ctes[i] = new com.clearspring.analytics.stream.cardinality.CountThenEstimate((cardinalityPer + 1), com.clearspring.analytics.stream.cardinality.AdaptiveCounting.Builder.obyCount(100000));
                for (int j = 0; j < cardinalityPer; j++) {
                    ctes[i].offer(java.lang.Math.random());
                }
            }
            expectedCardinality = numToMerge * cardinalityPer;
            merged = com.clearspring.analytics.stream.cardinality.CountThenEstimate.mergeEstimators(ctes);
            mergedEstimate = merged.cardinality();
            double error = (java.lang.Math.abs((mergedEstimate - expectedCardinality))) / ((double) (expectedCardinality));
            // StatementAdderOnAssert create literal from method
            int int_vc_365 = 0;
            // AssertGenerator add assertion
            junit.framework.Assert.assertEquals(int_vc_365, 0);
            // StatementAdderMethod cloned existing statement
            merged.offerHashed(int_vc_365);
            org.junit.Assert.fail("testSmallMerge_cf49197 should have thrown UnsupportedOperationException");
        } catch (java.lang.UnsupportedOperationException eee) {
        }
    }

    /* amplification of com.clearspring.analytics.stream.cardinality.TestCountThenEstimate#testSmallMerge */
    @org.junit.Test(timeout = 999)
    public void testSmallMerge_cf49252_cf54860_failAssert8_literalMutation57274() throws com.clearspring.analytics.stream.cardinality.CardinalityMergeException {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // Untipped test case
            int numToMerge = 1000;
            int cardinalityPer = 5;
            com.clearspring.analytics.stream.cardinality.CountThenEstimate[] ctes = new com.clearspring.analytics.stream.cardinality.CountThenEstimate[numToMerge];
            for (int i = 0; i < numToMerge; i++) {
                ctes[i] = new com.clearspring.analytics.stream.cardinality.CountThenEstimate(10000, com.clearspring.analytics.stream.cardinality.AdaptiveCounting.Builder.obyCount(100000));
                for (int j = 0; j < cardinalityPer; j++) {
                    ctes[i].offer(java.lang.Math.random());
                }
            }
            int expectedCardinality = numToMerge * cardinalityPer;
            com.clearspring.analytics.stream.cardinality.CountThenEstimate merged = com.clearspring.analytics.stream.cardinality.CountThenEstimate.mergeEstimators(ctes);
            long mergedEstimate = merged.cardinality();
            // Tipped test case
            numToMerge = 10;
            cardinalityPer = 100;
            ctes = new com.clearspring.analytics.stream.cardinality.CountThenEstimate[numToMerge];
            for (int i = 0; i < numToMerge; i++) {
                ctes[i] = new com.clearspring.analytics.stream.cardinality.CountThenEstimate((cardinalityPer + 1), com.clearspring.analytics.stream.cardinality.AdaptiveCounting.Builder.obyCount(100000));
                for (int j = 0; j < cardinalityPer; j++) {
                    ctes[i].offer(java.lang.Math.random());
                }
            }
            expectedCardinality = numToMerge * cardinalityPer;
            merged = com.clearspring.analytics.stream.cardinality.CountThenEstimate.mergeEstimators(ctes);
            mergedEstimate = merged.cardinality();
            double error = (java.lang.Math.abs((mergedEstimate - expectedCardinality))) / ((double) (expectedCardinality));
            // AssertGenerator replace invocation
            int o_testSmallMerge_cf49252__50 = // StatementAdderMethod cloned existing statement
merged.sizeof();
            // AssertGenerator add assertion
            junit.framework.Assert.assertEquals(o_testSmallMerge_cf49252__50, 3342);
            // StatementAdderOnAssert create random local variable
            long vc_12759 = 6916334746836991493L;
            // AssertGenerator add assertion
            junit.framework.Assert.assertEquals(vc_12759, 6916334746836991493L);
            // StatementAdderOnAssert create null value
            com.clearspring.analytics.stream.cardinality.CountThenEstimate vc_12757 = (com.clearspring.analytics.stream.cardinality.CountThenEstimate)null;
            // AssertGenerator add assertion
            junit.framework.Assert.assertNull(vc_12757);
            // StatementAdderMethod cloned existing statement
            vc_12757.offerHashed(vc_12759);
            org.junit.Assert.fail("testSmallMerge_cf49252_cf54860 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    /* amplification of com.clearspring.analytics.stream.cardinality.TestCountThenEstimate#testSmallMerge */
    @org.junit.Test(timeout = 1000)
    public void testSmallMerge_cf49242_cf54638_cf57683_failAssert34() throws com.clearspring.analytics.stream.cardinality.CardinalityMergeException {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // Untipped test case
            int numToMerge = 1000;
            int cardinalityPer = 5;
            com.clearspring.analytics.stream.cardinality.CountThenEstimate[] ctes = new com.clearspring.analytics.stream.cardinality.CountThenEstimate[numToMerge];
            for (int i = 0; i < numToMerge; i++) {
                ctes[i] = new com.clearspring.analytics.stream.cardinality.CountThenEstimate(10000, com.clearspring.analytics.stream.cardinality.AdaptiveCounting.Builder.obyCount(100000));
                for (int j = 0; j < cardinalityPer; j++) {
                    ctes[i].offer(java.lang.Math.random());
                }
            }
            int expectedCardinality = numToMerge * cardinalityPer;
            com.clearspring.analytics.stream.cardinality.CountThenEstimate merged = com.clearspring.analytics.stream.cardinality.CountThenEstimate.mergeEstimators(ctes);
            long mergedEstimate = merged.cardinality();
            // Tipped test case
            numToMerge = 10;
            cardinalityPer = 100;
            ctes = new com.clearspring.analytics.stream.cardinality.CountThenEstimate[numToMerge];
            for (int i = 0; i < numToMerge; i++) {
                ctes[i] = new com.clearspring.analytics.stream.cardinality.CountThenEstimate((cardinalityPer + 1), com.clearspring.analytics.stream.cardinality.AdaptiveCounting.Builder.obyCount(100000));
                for (int j = 0; j < cardinalityPer; j++) {
                    ctes[i].offer(java.lang.Math.random());
                }
            }
            expectedCardinality = numToMerge * cardinalityPer;
            merged = com.clearspring.analytics.stream.cardinality.CountThenEstimate.mergeEstimators(ctes);
            mergedEstimate = merged.cardinality();
            double error = (java.lang.Math.abs((mergedEstimate - expectedCardinality))) / ((double) (expectedCardinality));
            // StatementAdderOnAssert create null value
            com.clearspring.analytics.stream.cardinality.ICardinality[] vc_11920 = (com.clearspring.analytics.stream.cardinality.ICardinality[])null;
            // AssertGenerator add assertion
            junit.framework.Assert.assertNull(vc_11920);
            // AssertGenerator add assertion
            junit.framework.Assert.assertNull(vc_11920);
            // AssertGenerator replace invocation
            com.clearspring.analytics.stream.cardinality.ICardinality o_testSmallMerge_cf49242__52 = // StatementAdderMethod cloned existing statement
merged.merge(vc_11920);
            // AssertGenerator add assertion
            junit.framework.Assert.assertTrue(((com.clearspring.analytics.stream.cardinality.CountThenEstimate)o_testSmallMerge_cf49242__52).tipped());
            // AssertGenerator add assertion
            junit.framework.Assert.assertEquals(((com.clearspring.analytics.stream.cardinality.CountThenEstimate)o_testSmallMerge_cf49242__52).sizeof(), 3342);
            // StatementAdderOnAssert create random local variable
            java.lang.Object vc_12719 = new java.lang.Object();
            // AssertGenerator replace invocation
            boolean o_testSmallMerge_cf49242_cf54638__62 = // StatementAdderMethod cloned existing statement
merged.offer(vc_12719);
            // AssertGenerator add assertion
            junit.framework.Assert.assertTrue(o_testSmallMerge_cf49242_cf54638__62);
            // StatementAdderOnAssert create null value
            com.clearspring.analytics.stream.cardinality.ICardinality[] vc_13178 = (com.clearspring.analytics.stream.cardinality.ICardinality[])null;
            // StatementAdderOnAssert create null value
            com.clearspring.analytics.stream.cardinality.CountThenEstimate vc_13176 = (com.clearspring.analytics.stream.cardinality.CountThenEstimate)null;
            // StatementAdderMethod cloned existing statement
            vc_13176.merge(vc_13178);
            org.junit.Assert.fail("testSmallMerge_cf49242_cf54638_cf57683 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    /* amplification of com.clearspring.analytics.stream.cardinality.TestCountThenEstimate#testSmallMerge */
    @org.junit.Test(timeout = 1000)
    public void testSmallMerge_cf49220_literalMutation53799_cf59740() throws com.clearspring.analytics.stream.cardinality.CardinalityMergeException {
        // Untipped test case
        int numToMerge = 4;
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(numToMerge, 4);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(numToMerge, 4);
        int cardinalityPer = 5;
        com.clearspring.analytics.stream.cardinality.CountThenEstimate[] ctes = new com.clearspring.analytics.stream.cardinality.CountThenEstimate[numToMerge];
        for (int i = 0; i < numToMerge; i++) {
            ctes[i] = new com.clearspring.analytics.stream.cardinality.CountThenEstimate(10000, com.clearspring.analytics.stream.cardinality.AdaptiveCounting.Builder.obyCount(100000));
            for (int j = 0; j < cardinalityPer; j++) {
                ctes[i].offer(java.lang.Math.random());
            }
        }
        int expectedCardinality = numToMerge * cardinalityPer;
        com.clearspring.analytics.stream.cardinality.CountThenEstimate merged = com.clearspring.analytics.stream.cardinality.CountThenEstimate.mergeEstimators(ctes);
        long mergedEstimate = merged.cardinality();
        org.junit.Assert.assertEquals(expectedCardinality, mergedEstimate);
        org.junit.Assert.assertFalse(merged.tipped);
        // Tipped test case
        numToMerge = 10;
        cardinalityPer = 100;
        ctes = new com.clearspring.analytics.stream.cardinality.CountThenEstimate[numToMerge];
        for (int i = 0; i < numToMerge; i++) {
            ctes[i] = new com.clearspring.analytics.stream.cardinality.CountThenEstimate((cardinalityPer + 1), com.clearspring.analytics.stream.cardinality.AdaptiveCounting.Builder.obyCount(100000));
            for (int j = 0; j < cardinalityPer; j++) {
                ctes[i].offer(java.lang.Math.random());
            }
        }
        expectedCardinality = numToMerge * cardinalityPer;
        merged = com.clearspring.analytics.stream.cardinality.CountThenEstimate.mergeEstimators(ctes);
        mergedEstimate = merged.cardinality();
        double error = (java.lang.Math.abs((mergedEstimate - expectedCardinality))) / ((double) (expectedCardinality));
        org.junit.Assert.assertEquals(0.01, error, 0.01);
        // AssertGenerator replace invocation
        boolean o_testSmallMerge_cf49220__50 = // StatementAdderMethod cloned existing statement
merged.tipped();
        // AssertGenerator add assertion
        junit.framework.Assert.assertTrue(o_testSmallMerge_cf49220__50);
        // StatementAdderOnAssert create null value
        com.clearspring.analytics.stream.cardinality.CountThenEstimate[] vc_13344 = (com.clearspring.analytics.stream.cardinality.CountThenEstimate[])null;
        // AssertGenerator add assertion
        junit.framework.Assert.assertNull(vc_13344);
        // AssertGenerator replace invocation
        com.clearspring.analytics.stream.cardinality.CountThenEstimate o_testSmallMerge_cf49220_literalMutation53799_cf59740__59 = // StatementAdderMethod cloned existing statement
merged.mergeEstimators(vc_13344);
        // AssertGenerator add assertion
        junit.framework.Assert.assertNull(o_testSmallMerge_cf49220_literalMutation53799_cf59740__59);
        org.junit.Assert.assertTrue(merged.tipped);
    }

    /* amplification of com.clearspring.analytics.stream.cardinality.TestCountThenEstimate#testSmallMerge */
    @org.junit.Test(timeout = 1000)
    public void testSmallMerge_cf49231_cf54466_failAssert20_literalMutation59467_failAssert29() throws com.clearspring.analytics.stream.cardinality.CardinalityMergeException {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // AssertGenerator generate try/catch block with fail statement
            try {
                // Untipped test case
                int numToMerge = 1000;
                int cardinalityPer = 5;
                com.clearspring.analytics.stream.cardinality.CountThenEstimate[] ctes = new com.clearspring.analytics.stream.cardinality.CountThenEstimate[numToMerge];
                for (int i = 0; i < numToMerge; i++) {
                    ctes[i] = new com.clearspring.analytics.stream.cardinality.CountThenEstimate(10000, com.clearspring.analytics.stream.cardinality.AdaptiveCounting.Builder.obyCount(-1));
                    for (int j = 0; j < cardinalityPer; j++) {
                        ctes[i].offer(java.lang.Math.random());
                    }
                }
                int expectedCardinality = numToMerge * cardinalityPer;
                com.clearspring.analytics.stream.cardinality.CountThenEstimate merged = com.clearspring.analytics.stream.cardinality.CountThenEstimate.mergeEstimators(ctes);
                long mergedEstimate = merged.cardinality();
                // Tipped test case
                numToMerge = 10;
                cardinalityPer = 100;
                ctes = new com.clearspring.analytics.stream.cardinality.CountThenEstimate[numToMerge];
                for (int i = 0; i < numToMerge; i++) {
                    ctes[i] = new com.clearspring.analytics.stream.cardinality.CountThenEstimate((cardinalityPer + 1), com.clearspring.analytics.stream.cardinality.AdaptiveCounting.Builder.obyCount(100000));
                    for (int j = 0; j < cardinalityPer; j++) {
                        ctes[i].offer(java.lang.Math.random());
                    }
                }
                expectedCardinality = numToMerge * cardinalityPer;
                merged = com.clearspring.analytics.stream.cardinality.CountThenEstimate.mergeEstimators(ctes);
                mergedEstimate = merged.cardinality();
                double error = (java.lang.Math.abs((mergedEstimate - expectedCardinality))) / ((double) (expectedCardinality));
                // AssertGenerator replace invocation
                com.clearspring.analytics.stream.cardinality.CountThenEstimate o_testSmallMerge_cf49231__50 = // StatementAdderMethod cloned existing statement
merged.mergeEstimators(ctes);
                // AssertGenerator add assertion
                junit.framework.Assert.assertTrue(((com.clearspring.analytics.stream.cardinality.CountThenEstimate)o_testSmallMerge_cf49231__50).tipped());
                // AssertGenerator add assertion
                junit.framework.Assert.assertEquals(((com.clearspring.analytics.stream.cardinality.CountThenEstimate)o_testSmallMerge_cf49231__50).sizeof(), 3342);
                // StatementAdderOnAssert create random local variable
                long vc_12691 = 1485297275898829735L;
                // StatementAdderOnAssert create null value
                com.clearspring.analytics.stream.cardinality.CountThenEstimate vc_12689 = (com.clearspring.analytics.stream.cardinality.CountThenEstimate)null;
                // StatementAdderMethod cloned existing statement
                vc_12689.offerHashed(vc_12691);
                org.junit.Assert.fail("testSmallMerge_cf49231_cf54466 should have thrown NullPointerException");
            } catch (java.lang.NullPointerException eee) {
            }
            org.junit.Assert.fail("testSmallMerge_cf49231_cf54466_failAssert20_literalMutation59467 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of com.clearspring.analytics.stream.cardinality.TestCountThenEstimate#testSmallMerge */
    @org.junit.Test(timeout = 1000)
    public void testSmallMerge_literalMutation49131_literalMutation49585_cf56936() throws com.clearspring.analytics.stream.cardinality.CardinalityMergeException {
        // Untipped test case
        int numToMerge = 999;
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(numToMerge, 999);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(numToMerge, 999);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(numToMerge, 999);
        int cardinalityPer = 5;
        com.clearspring.analytics.stream.cardinality.CountThenEstimate[] ctes = new com.clearspring.analytics.stream.cardinality.CountThenEstimate[numToMerge];
        for (int i = 0; i < numToMerge; i++) {
            ctes[i] = new com.clearspring.analytics.stream.cardinality.CountThenEstimate(10000, com.clearspring.analytics.stream.cardinality.AdaptiveCounting.Builder.obyCount(100000));
            for (int j = 0; j < cardinalityPer; j++) {
                ctes[i].offer(java.lang.Math.random());
            }
        }
        int expectedCardinality = numToMerge * cardinalityPer;
        com.clearspring.analytics.stream.cardinality.CountThenEstimate merged = com.clearspring.analytics.stream.cardinality.CountThenEstimate.mergeEstimators(ctes);
        long mergedEstimate = merged.cardinality();
        org.junit.Assert.assertEquals(expectedCardinality, mergedEstimate);
        org.junit.Assert.assertFalse(merged.tipped);
        // Tipped test case
        numToMerge = 10;
        cardinalityPer = 100;
        ctes = new com.clearspring.analytics.stream.cardinality.CountThenEstimate[numToMerge];
        for (int i = 0; i < numToMerge; i++) {
            ctes[i] = new com.clearspring.analytics.stream.cardinality.CountThenEstimate((cardinalityPer + 1), com.clearspring.analytics.stream.cardinality.AdaptiveCounting.Builder.obyCount(100000));
            for (int j = 0; j < cardinalityPer; j++) {
                ctes[i].offer(java.lang.Math.random());
            }
        }
        expectedCardinality = numToMerge * cardinalityPer;
        merged = com.clearspring.analytics.stream.cardinality.CountThenEstimate.mergeEstimators(ctes);
        mergedEstimate = merged.cardinality();
        double error = (java.lang.Math.abs((mergedEstimate - expectedCardinality))) / ((double) (expectedCardinality));
        org.junit.Assert.assertEquals(0.01, error, 0.01);
        // StatementAdderOnAssert create random local variable
        java.lang.Object vc_13059 = new java.lang.Object();
        // StatementAdderMethod cloned existing statement
        merged.offer(vc_13059);
        org.junit.Assert.assertTrue(merged.tipped);
    }

    /* amplification of com.clearspring.analytics.stream.cardinality.TestCountThenEstimate#testSmallMerge */
    @org.junit.Test(timeout = 1000)
    public void testSmallMerge_cf49184_failAssert22_literalMutation52832_literalMutation59263() throws com.clearspring.analytics.stream.cardinality.CardinalityMergeException {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // Untipped test case
            int numToMerge = 0;
            // AssertGenerator add assertion
            junit.framework.Assert.assertEquals(numToMerge, 0);
            int cardinalityPer = 5;
            com.clearspring.analytics.stream.cardinality.CountThenEstimate[] ctes = new com.clearspring.analytics.stream.cardinality.CountThenEstimate[numToMerge];
            for (int i = 0; i < numToMerge; i++) {
                ctes[i] = new com.clearspring.analytics.stream.cardinality.CountThenEstimate(10000, com.clearspring.analytics.stream.cardinality.AdaptiveCounting.Builder.obyCount(100000));
                for (int j = 0; j < cardinalityPer; j++) {
                    ctes[i].offer(java.lang.Math.random());
                }
            }
            int expectedCardinality = numToMerge * cardinalityPer;
            com.clearspring.analytics.stream.cardinality.CountThenEstimate merged = com.clearspring.analytics.stream.cardinality.CountThenEstimate.mergeEstimators(ctes);
            long mergedEstimate = merged.cardinality();
            // Tipped test case
            numToMerge = 10;
            cardinalityPer = 0;
            // AssertGenerator add assertion
            junit.framework.Assert.assertEquals(cardinalityPer, 0);
            ctes = new com.clearspring.analytics.stream.cardinality.CountThenEstimate[numToMerge];
            for (int i = 0; i < numToMerge; i++) {
                ctes[i] = new com.clearspring.analytics.stream.cardinality.CountThenEstimate((cardinalityPer + 1), com.clearspring.analytics.stream.cardinality.AdaptiveCounting.Builder.obyCount(100000));
                for (int j = 0; j < cardinalityPer; j++) {
                    ctes[i].offer(java.lang.Math.random());
                }
            }
            expectedCardinality = numToMerge * cardinalityPer;
            merged = com.clearspring.analytics.stream.cardinality.CountThenEstimate.mergeEstimators(ctes);
            mergedEstimate = merged.cardinality();
            double error = (java.lang.Math.abs((mergedEstimate - expectedCardinality))) / ((double) (expectedCardinality));
            // StatementAdderOnAssert create random local variable
            java.lang.Object vc_11903 = new java.lang.Object();
            // StatementAdderOnAssert create null value
            com.clearspring.analytics.stream.cardinality.CountThenEstimate vc_11900 = (com.clearspring.analytics.stream.cardinality.CountThenEstimate)null;
            // AssertGenerator add assertion
            junit.framework.Assert.assertNull(vc_11900);
            // StatementAdderMethod cloned existing statement
            vc_11900.offer(vc_11903);
            org.junit.Assert.fail("testSmallMerge_cf49184 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    /* amplification of com.clearspring.analytics.stream.cardinality.TestCountThenEstimate#testSmallMerge */
    @org.junit.Test(timeout = 1000)
    public void testSmallMerge_cf49197_failAssert27_add53200_literalMutation60536() throws com.clearspring.analytics.stream.cardinality.CardinalityMergeException {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // Untipped test case
            int numToMerge = 1000;
            int cardinalityPer = 5;
            com.clearspring.analytics.stream.cardinality.CountThenEstimate[] ctes = new com.clearspring.analytics.stream.cardinality.CountThenEstimate[numToMerge];
            for (int i = 0; i < numToMerge; i++) {
                ctes[i] = new com.clearspring.analytics.stream.cardinality.CountThenEstimate(10000, com.clearspring.analytics.stream.cardinality.AdaptiveCounting.Builder.obyCount(100000));
                for (int j = 11; j < cardinalityPer; j++) {
                    ctes[i].offer(java.lang.Math.random());
                }
            }
            int expectedCardinality = numToMerge * cardinalityPer;
            com.clearspring.analytics.stream.cardinality.CountThenEstimate merged = com.clearspring.analytics.stream.cardinality.CountThenEstimate.mergeEstimators(ctes);
            long mergedEstimate = merged.cardinality();
            // Tipped test case
            numToMerge = 10;
            cardinalityPer = 100;
            ctes = new com.clearspring.analytics.stream.cardinality.CountThenEstimate[numToMerge];
            for (int i = 0; i < numToMerge; i++) {
                ctes[i] = new com.clearspring.analytics.stream.cardinality.CountThenEstimate((cardinalityPer + 1), com.clearspring.analytics.stream.cardinality.AdaptiveCounting.Builder.obyCount(100000));
                for (int j = 0; j < cardinalityPer; j++) {
                    ctes[i].offer(java.lang.Math.random());
                }
            }
            expectedCardinality = numToMerge * cardinalityPer;
            merged = com.clearspring.analytics.stream.cardinality.CountThenEstimate.mergeEstimators(ctes);
            mergedEstimate = merged.cardinality();
            double error = (java.lang.Math.abs((mergedEstimate - expectedCardinality))) / ((double) (expectedCardinality));
            // StatementAdderOnAssert create literal from method
            int int_vc_365 = 0;
            // AssertGenerator add assertion
            junit.framework.Assert.assertEquals(int_vc_365, 0);
            // AssertGenerator add assertion
            junit.framework.Assert.assertEquals(int_vc_365, 0);
            // StatementAdderMethod cloned existing statement
            // MethodCallAdder
            merged.offerHashed(int_vc_365);
            // StatementAdderMethod cloned existing statement
            merged.offerHashed(int_vc_365);
            org.junit.Assert.fail("testSmallMerge_cf49197 should have thrown UnsupportedOperationException");
        } catch (java.lang.UnsupportedOperationException eee) {
        }
    }

    /* amplification of com.clearspring.analytics.stream.cardinality.TestCountThenEstimate#testSmallMerge */
    @org.junit.Test(timeout = 1000)
    public void testSmallMerge_literalMutation49158_literalMutation51752_cf60957() throws com.clearspring.analytics.stream.cardinality.CardinalityMergeException {
        // Untipped test case
        int numToMerge = 1000;
        int cardinalityPer = 2;
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(cardinalityPer, 2);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(cardinalityPer, 2);
        com.clearspring.analytics.stream.cardinality.CountThenEstimate[] ctes = new com.clearspring.analytics.stream.cardinality.CountThenEstimate[numToMerge];
        for (int i = 0; i < numToMerge; i++) {
            ctes[i] = new com.clearspring.analytics.stream.cardinality.CountThenEstimate(10000, com.clearspring.analytics.stream.cardinality.AdaptiveCounting.Builder.obyCount(100000));
            for (int j = 0; j < cardinalityPer; j++) {
                ctes[i].offer(java.lang.Math.random());
            }
        }
        int expectedCardinality = numToMerge * cardinalityPer;
        com.clearspring.analytics.stream.cardinality.CountThenEstimate merged = com.clearspring.analytics.stream.cardinality.CountThenEstimate.mergeEstimators(ctes);
        long mergedEstimate = merged.cardinality();
        org.junit.Assert.assertEquals(expectedCardinality, mergedEstimate);
        org.junit.Assert.assertFalse(merged.tipped);
        // Tipped test case
        numToMerge = 10;
        cardinalityPer = 99;
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(cardinalityPer, 99);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(cardinalityPer, 99);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(cardinalityPer, 99);
        ctes = new com.clearspring.analytics.stream.cardinality.CountThenEstimate[numToMerge];
        for (int i = 0; i < numToMerge; i++) {
            ctes[i] = new com.clearspring.analytics.stream.cardinality.CountThenEstimate((cardinalityPer + 1), com.clearspring.analytics.stream.cardinality.AdaptiveCounting.Builder.obyCount(100000));
            for (int j = 0; j < cardinalityPer; j++) {
                ctes[i].offer(java.lang.Math.random());
            }
        }
        expectedCardinality = numToMerge * cardinalityPer;
        merged = com.clearspring.analytics.stream.cardinality.CountThenEstimate.mergeEstimators(ctes);
        mergedEstimate = merged.cardinality();
        double error = (java.lang.Math.abs((mergedEstimate - expectedCardinality))) / ((double) (expectedCardinality));
        org.junit.Assert.assertEquals(0.01, error, 0.01);
        // StatementAdderOnAssert create null value
        java.lang.Object vc_13466 = (java.lang.Object)null;
        // AssertGenerator add assertion
        junit.framework.Assert.assertNull(vc_13466);
        // AssertGenerator replace invocation
        boolean o_testSmallMerge_literalMutation49158_literalMutation51752_cf60957__60 = // StatementAdderMethod cloned existing statement
merged.offer(vc_13466);
        // AssertGenerator add assertion
        junit.framework.Assert.assertTrue(o_testSmallMerge_literalMutation49158_literalMutation51752_cf60957__60);
        org.junit.Assert.assertTrue(merged.tipped);
    }

    /* amplification of com.clearspring.analytics.stream.cardinality.TestCountThenEstimate#testTip */
    @org.junit.Test(timeout = 1000)
    public void testTip_cf62877_failAssert48() throws java.io.IOException, java.lang.ClassNotFoundException {
        // AssertGenerator generate try/catch block with fail statement
        try {
            com.clearspring.analytics.stream.cardinality.CountThenEstimate cte = new com.clearspring.analytics.stream.cardinality.CountThenEstimate(10000, new com.clearspring.analytics.stream.cardinality.LinearCounting.Builder(1024));
            com.clearspring.analytics.stream.cardinality.CountThenEstimate clone = new com.clearspring.analytics.stream.cardinality.CountThenEstimate(cte.getBytes());
            assertCountThenEstimateEquals(cte, clone);
            for (int i = 0; i < 128; i++) {
                cte.offer(java.lang.Integer.toString(i));
            }
            clone = new com.clearspring.analytics.stream.cardinality.CountThenEstimate(cte.getBytes());
            // MethodAssertGenerator build local variable
            Object o_17_0 = cte.cardinality();
            assertCountThenEstimateEquals(cte, clone);
            for (int i = 128; i < 256; i++) {
                cte.offer(java.lang.Integer.toString(i));
            }
            clone = new com.clearspring.analytics.stream.cardinality.CountThenEstimate(cte.getBytes());
            // MethodAssertGenerator build local variable
            Object o_29_0 = cte.tipped();
            // MethodAssertGenerator build local variable
            Object o_31_0 = cte.cardinality();
            // MethodAssertGenerator build local variable
            Object o_33_0 = clone.tipped();
            double error = (java.lang.Math.abs(((cte.cardinality()) - 256))) / 256.0;
            // StatementAdderOnAssert create random local variable
            long vc_13609 = -6815768464057720306L;
            // StatementAdderMethod cloned existing statement
            cte.offerHashed(vc_13609);
            org.junit.Assert.fail("testTip_cf62877 should have thrown UnsupportedOperationException");
        } catch (java.lang.UnsupportedOperationException eee) {
        }
    }

    /* amplification of com.clearspring.analytics.stream.cardinality.TestCountThenEstimate#testTip */
    @org.junit.Test(timeout = 1000)
    public void testTip_cf62871_failAssert44() throws java.io.IOException, java.lang.ClassNotFoundException {
        // AssertGenerator generate try/catch block with fail statement
        try {
            com.clearspring.analytics.stream.cardinality.CountThenEstimate cte = new com.clearspring.analytics.stream.cardinality.CountThenEstimate(10000, new com.clearspring.analytics.stream.cardinality.LinearCounting.Builder(1024));
            com.clearspring.analytics.stream.cardinality.CountThenEstimate clone = new com.clearspring.analytics.stream.cardinality.CountThenEstimate(cte.getBytes());
            assertCountThenEstimateEquals(cte, clone);
            for (int i = 0; i < 128; i++) {
                cte.offer(java.lang.Integer.toString(i));
            }
            clone = new com.clearspring.analytics.stream.cardinality.CountThenEstimate(cte.getBytes());
            // MethodAssertGenerator build local variable
            Object o_17_0 = cte.cardinality();
            assertCountThenEstimateEquals(cte, clone);
            for (int i = 128; i < 256; i++) {
                cte.offer(java.lang.Integer.toString(i));
            }
            clone = new com.clearspring.analytics.stream.cardinality.CountThenEstimate(cte.getBytes());
            // MethodAssertGenerator build local variable
            Object o_29_0 = cte.tipped();
            // MethodAssertGenerator build local variable
            Object o_31_0 = cte.cardinality();
            // MethodAssertGenerator build local variable
            Object o_33_0 = clone.tipped();
            double error = (java.lang.Math.abs(((cte.cardinality()) - 256))) / 256.0;
            // StatementAdderOnAssert create random local variable
            int vc_13606 = 1802744749;
            // StatementAdderMethod cloned existing statement
            clone.offerHashed(vc_13606);
            org.junit.Assert.fail("testTip_cf62871 should have thrown UnsupportedOperationException");
        } catch (java.lang.UnsupportedOperationException eee) {
        }
    }
}

