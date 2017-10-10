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


public class TestLinearCountingAmpl {
    @org.junit.Test
    public void testSaturation() {
        com.clearspring.analytics.stream.cardinality.LinearCounting lc = new com.clearspring.analytics.stream.cardinality.LinearCounting(1);
        for (int i = 0; i < 27; i++) {
            lc.offer(i);
        }
        org.junit.Assert.assertTrue(lc.isSaturated());
        org.junit.Assert.assertEquals(0, lc.getCount());
        org.junit.Assert.assertEquals(java.lang.Long.MAX_VALUE, lc.cardinality());
    }

    @org.junit.Test
    public void testBuilder() {
        org.junit.Assert.assertEquals(630, com.clearspring.analytics.stream.cardinality.LinearCounting.Builder.onePercentError(1).size);
        org.junit.Assert.assertEquals(630, com.clearspring.analytics.stream.cardinality.LinearCounting.Builder.onePercentError(99).size);
        org.junit.Assert.assertEquals(630, com.clearspring.analytics.stream.cardinality.LinearCounting.Builder.onePercentError(100).size);
        org.junit.Assert.assertEquals(630, com.clearspring.analytics.stream.cardinality.LinearCounting.Builder.onePercentError(101).size);
        org.junit.Assert.assertEquals(759, com.clearspring.analytics.stream.cardinality.LinearCounting.Builder.onePercentError(3375).size);
        org.junit.Assert.assertEquals(995, com.clearspring.analytics.stream.cardinality.LinearCounting.Builder.onePercentError(9999).size);
        org.junit.Assert.assertEquals(995, com.clearspring.analytics.stream.cardinality.LinearCounting.Builder.onePercentError(10000).size);
        org.junit.Assert.assertEquals(996, com.clearspring.analytics.stream.cardinality.LinearCounting.Builder.onePercentError(10001).size);
        org.junit.Assert.assertEquals(7501, com.clearspring.analytics.stream.cardinality.LinearCounting.Builder.onePercentError(305028).size);
        org.junit.Assert.assertEquals(19272, com.clearspring.analytics.stream.cardinality.LinearCounting.Builder.onePercentError(1000000).size);
        org.junit.Assert.assertEquals(23027, com.clearspring.analytics.stream.cardinality.LinearCounting.Builder.onePercentError(1250000).size);
        org.junit.Assert.assertEquals(74962, com.clearspring.analytics.stream.cardinality.LinearCounting.Builder.onePercentError(5000000).size);
        org.junit.Assert.assertEquals(81372, com.clearspring.analytics.stream.cardinality.LinearCounting.Builder.onePercentError(5500000).size);
        org.junit.Assert.assertEquals(131030, com.clearspring.analytics.stream.cardinality.LinearCounting.Builder.onePercentError(9500000).size);
        org.junit.Assert.assertEquals(137073, com.clearspring.analytics.stream.cardinality.LinearCounting.Builder.onePercentError(10000000).size);
        org.junit.Assert.assertEquals(137073, com.clearspring.analytics.stream.cardinality.LinearCounting.Builder.onePercentError(10000001).size);
        org.junit.Assert.assertEquals(355055, com.clearspring.analytics.stream.cardinality.LinearCounting.Builder.onePercentError(30000000).size);
        org.junit.Assert.assertEquals(573038, com.clearspring.analytics.stream.cardinality.LinearCounting.Builder.onePercentError(50000000).size);
        org.junit.Assert.assertEquals(822207, com.clearspring.analytics.stream.cardinality.LinearCounting.Builder.onePercentError(75000000).size);
        org.junit.Assert.assertEquals(1071377, com.clearspring.analytics.stream.cardinality.LinearCounting.Builder.onePercentError(100000000).size);
        org.junit.Assert.assertEquals(1167722, com.clearspring.analytics.stream.cardinality.LinearCounting.Builder.onePercentError(110000000).size);
        org.junit.Assert.assertEquals(1264067, com.clearspring.analytics.stream.cardinality.LinearCounting.Builder.onePercentError(120000000).size);
        org.junit.Assert.assertEquals(2500000, com.clearspring.analytics.stream.cardinality.LinearCounting.Builder.onePercentError(240000000).size);
    }

    @org.junit.Test
    public void testArbitraryStdErrorSize() {
        // Some sanity check with 1% error
        org.junit.Assert.assertEquals(630, com.clearspring.analytics.stream.cardinality.LinearCounting.Builder.withError(0.01, 100).size);
        org.junit.Assert.assertEquals(759, com.clearspring.analytics.stream.cardinality.LinearCounting.Builder.withError(0.01, 3375).size);
        // Checking for 10% error (values from original paper)
        org.junit.Assert.assertEquals(10, com.clearspring.analytics.stream.cardinality.LinearCounting.Builder.withError(0.1, 100).size);
        org.junit.Assert.assertEquals(34, com.clearspring.analytics.stream.cardinality.LinearCounting.Builder.withError(0.1, 1000).size);
        org.junit.Assert.assertEquals(214, com.clearspring.analytics.stream.cardinality.LinearCounting.Builder.withError(0.1, 10000).size);
        org.junit.Assert.assertEquals(1593, com.clearspring.analytics.stream.cardinality.LinearCounting.Builder.withError(0.1, 100000).size);
        org.junit.Assert.assertEquals(12610, com.clearspring.analytics.stream.cardinality.LinearCounting.Builder.withError(0.1, 1000000).size);
        org.junit.Assert.assertEquals(103977, com.clearspring.analytics.stream.cardinality.LinearCounting.Builder.withError(0.1, 10000000).size);
        org.junit.Assert.assertEquals(882720, com.clearspring.analytics.stream.cardinality.LinearCounting.Builder.withError(0.1, 100000000).size);
    }

    @org.junit.Test(expected = java.lang.IllegalArgumentException.class)
    public void testBuilderIllegalArgumentZero() {
        com.clearspring.analytics.stream.cardinality.LinearCounting.Builder.onePercentError(0);
    }

    @org.junit.Test(expected = java.lang.IllegalArgumentException.class)
    public void testBuilderIllegalArgumentNegative() {
        com.clearspring.analytics.stream.cardinality.LinearCounting.Builder.onePercentError((-1));
    }

    @org.junit.Test
    public void testMerge() throws com.clearspring.analytics.stream.cardinality.LinearCounting.LinearCountingMergeException {
        int numToMerge = 5;
        int size = 65536;
        int cardinality = 1000;
        com.clearspring.analytics.stream.cardinality.LinearCounting[] lcs = new com.clearspring.analytics.stream.cardinality.LinearCounting[numToMerge];
        com.clearspring.analytics.stream.cardinality.LinearCounting baseline = new com.clearspring.analytics.stream.cardinality.LinearCounting(size);
        for (int i = 0; i < numToMerge; i++) {
            lcs[i] = new com.clearspring.analytics.stream.cardinality.LinearCounting(size);
            for (int j = 0; j < cardinality; j++) {
                double val = java.lang.Math.random();
                lcs[i].offer(val);
                baseline.offer(val);
            }
        }
        int expectedCardinality = numToMerge * cardinality;
        long mergedEstimate = com.clearspring.analytics.stream.cardinality.LinearCounting.mergeEstimators(lcs).cardinality();
        double error = (java.lang.Math.abs((mergedEstimate - expectedCardinality))) / ((double) (expectedCardinality));
        org.junit.Assert.assertEquals(0.01, error, 0.01);
        com.clearspring.analytics.stream.cardinality.LinearCounting lc = lcs[0];
        lcs = java.util.Arrays.asList(lcs).subList(1, lcs.length).toArray(new com.clearspring.analytics.stream.cardinality.LinearCounting[0]);
        mergedEstimate = lc.merge(lcs).cardinality();
        error = (java.lang.Math.abs((mergedEstimate - expectedCardinality))) / ((double) (expectedCardinality));
        org.junit.Assert.assertEquals(0.01, error, 0.01);
        long baselineEstimate = baseline.cardinality();
        org.junit.Assert.assertEquals(baselineEstimate, mergedEstimate);
    }

    @org.junit.Test
    public void testSerialization() {
        com.clearspring.analytics.stream.cardinality.LinearCounting lc = new com.clearspring.analytics.stream.cardinality.LinearCounting(4);
        // AssertGenerator replace invocation
        boolean o_testSerialization__3 = lc.offer("a");
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_testSerialization__3);
        // AssertGenerator replace invocation
        boolean o_testSerialization__4 = lc.offer("b");
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_testSerialization__4);
        // AssertGenerator replace invocation
        boolean o_testSerialization__5 = lc.offer("c");
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_testSerialization__5);
        // AssertGenerator replace invocation
        boolean o_testSerialization__6 = lc.offer("d");
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_testSerialization__6);
        // AssertGenerator replace invocation
        boolean o_testSerialization__7 = lc.offer("e");
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_testSerialization__7);
        com.clearspring.analytics.stream.cardinality.LinearCounting lc2 = new com.clearspring.analytics.stream.cardinality.LinearCounting(lc.getBytes());
        org.junit.Assert.assertArrayEquals(lc.map, lc2.map);
        org.junit.Assert.assertEquals(lc.count, lc2.count);
        org.junit.Assert.assertEquals(lc.length, lc2.length);
    }

    @org.junit.Test
    public void testComputeCount() {
        com.clearspring.analytics.stream.cardinality.LinearCounting lc = new com.clearspring.analytics.stream.cardinality.LinearCounting(4);
        // AssertGenerator replace invocation
        boolean o_testComputeCount__3 = lc.offer(0);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_testComputeCount__3);
        // AssertGenerator replace invocation
        boolean o_testComputeCount__4 = lc.offer(1);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_testComputeCount__4);
        // AssertGenerator replace invocation
        boolean o_testComputeCount__5 = lc.offer(2);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_testComputeCount__5);
        // AssertGenerator replace invocation
        boolean o_testComputeCount__6 = lc.offer(3);
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_testComputeCount__6);
        // AssertGenerator replace invocation
        boolean o_testComputeCount__7 = lc.offer(16);
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_testComputeCount__7);
        // AssertGenerator replace invocation
        boolean o_testComputeCount__8 = lc.offer(17);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_testComputeCount__8);
        // AssertGenerator replace invocation
        boolean o_testComputeCount__9 = lc.offer(18);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_testComputeCount__9);
        // AssertGenerator replace invocation
        boolean o_testComputeCount__10 = lc.offer(19);
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_testComputeCount__10);
        org.junit.Assert.assertEquals(27, lc.computeCount());
    }

    /* amplification of com.clearspring.analytics.stream.cardinality.TestLinearCounting#testComputeCount */
    @org.junit.Test(timeout = 1000)
    public void testComputeCount_add577() {
        com.clearspring.analytics.stream.cardinality.LinearCounting lc = new com.clearspring.analytics.stream.cardinality.LinearCounting(4);
        lc.offer(0);
        lc.offer(1);
        lc.offer(2);
        lc.offer(3);
        lc.offer(16);
        lc.offer(17);
        lc.offer(18);
        // AssertGenerator replace invocation
        boolean o_testComputeCount_add577__10 = // MethodCallAdder
lc.offer(19);
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_testComputeCount_add577__10);
        lc.offer(19);
        org.junit.Assert.assertEquals(27, lc.computeCount());
    }

    /* amplification of com.clearspring.analytics.stream.cardinality.TestLinearCounting#testComputeCount */
    @org.junit.Test(timeout = 1000)
    public void testComputeCount_add575() {
        com.clearspring.analytics.stream.cardinality.LinearCounting lc = new com.clearspring.analytics.stream.cardinality.LinearCounting(4);
        lc.offer(0);
        lc.offer(1);
        lc.offer(2);
        lc.offer(3);
        lc.offer(16);
        // AssertGenerator replace invocation
        boolean o_testComputeCount_add575__8 = // MethodCallAdder
lc.offer(17);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_testComputeCount_add575__8);
        lc.offer(17);
        lc.offer(18);
        lc.offer(19);
        org.junit.Assert.assertEquals(27, lc.computeCount());
    }

    /* amplification of com.clearspring.analytics.stream.cardinality.TestLinearCounting#testComputeCount */
    @org.junit.Test(timeout = 1000)
    public void testComputeCount_cf611_failAssert21() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            com.clearspring.analytics.stream.cardinality.LinearCounting lc = new com.clearspring.analytics.stream.cardinality.LinearCounting(4);
            lc.offer(0);
            lc.offer(1);
            lc.offer(2);
            lc.offer(3);
            lc.offer(16);
            lc.offer(17);
            lc.offer(18);
            lc.offer(19);
            // StatementAdderOnAssert create random local variable
            long vc_79 = -318363846169550772L;
            // StatementAdderMethod cloned existing statement
            lc.offerHashed(vc_79);
            // MethodAssertGenerator build local variable
            Object o_15_0 = lc.computeCount();
            org.junit.Assert.fail("testComputeCount_cf611 should have thrown UnsupportedOperationException");
        } catch (java.lang.UnsupportedOperationException eee) {
        }
    }

    /* amplification of com.clearspring.analytics.stream.cardinality.TestLinearCounting#testComputeCount */
    @org.junit.Test(timeout = 1000)
    public void testComputeCount_cf649() {
        com.clearspring.analytics.stream.cardinality.LinearCounting lc = new com.clearspring.analytics.stream.cardinality.LinearCounting(4);
        lc.offer(0);
        lc.offer(1);
        lc.offer(2);
        lc.offer(3);
        lc.offer(16);
        lc.offer(17);
        lc.offer(18);
        lc.offer(19);
        // AssertGenerator replace invocation
        int o_testComputeCount_cf649__11 = // StatementAdderMethod cloned existing statement
lc.sizeof();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(o_testComputeCount_cf649__11, 4);
        org.junit.Assert.assertEquals(27, lc.computeCount());
    }

    /* amplification of com.clearspring.analytics.stream.cardinality.TestLinearCounting#testComputeCount */
    @org.junit.Test(timeout = 1000)
    public void testComputeCount_cf589() {
        com.clearspring.analytics.stream.cardinality.LinearCounting lc = new com.clearspring.analytics.stream.cardinality.LinearCounting(4);
        lc.offer(0);
        lc.offer(1);
        lc.offer(2);
        lc.offer(3);
        lc.offer(16);
        lc.offer(17);
        lc.offer(18);
        lc.offer(19);
        // AssertGenerator replace invocation
        boolean o_testComputeCount_cf589__11 = // StatementAdderMethod cloned existing statement
lc.isSaturated();
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_testComputeCount_cf589__11);
        org.junit.Assert.assertEquals(27, lc.computeCount());
    }

    /* amplification of com.clearspring.analytics.stream.cardinality.TestLinearCounting#testComputeCount */
    @org.junit.Test(timeout = 1000)
    public void testComputeCount_cf653() {
        com.clearspring.analytics.stream.cardinality.LinearCounting lc = new com.clearspring.analytics.stream.cardinality.LinearCounting(4);
        lc.offer(0);
        lc.offer(1);
        lc.offer(2);
        lc.offer(3);
        lc.offer(16);
        lc.offer(17);
        lc.offer(18);
        lc.offer(19);
        // AssertGenerator replace invocation
        java.lang.String o_testComputeCount_cf653__11 = // StatementAdderMethod cloned existing statement
lc.mapAsBitString();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(o_testComputeCount_cf653__11, "00000001000000000000010000100101");
        org.junit.Assert.assertEquals(27, lc.computeCount());
    }

    /* amplification of com.clearspring.analytics.stream.cardinality.TestLinearCounting#testComputeCount */
    @org.junit.Test(timeout = 1000)
    public void testComputeCount_cf594() {
        com.clearspring.analytics.stream.cardinality.LinearCounting lc = new com.clearspring.analytics.stream.cardinality.LinearCounting(4);
        lc.offer(0);
        lc.offer(1);
        lc.offer(2);
        lc.offer(3);
        lc.offer(16);
        lc.offer(17);
        lc.offer(18);
        lc.offer(19);
        // StatementAdderOnAssert create null value
        java.lang.Object vc_72 = (java.lang.Object)null;
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(vc_72);
        // AssertGenerator replace invocation
        boolean o_testComputeCount_cf594__13 = // StatementAdderMethod cloned existing statement
lc.offer(vc_72);
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_testComputeCount_cf594__13);
        org.junit.Assert.assertEquals(27, lc.computeCount());
    }

    /* amplification of com.clearspring.analytics.stream.cardinality.TestLinearCounting#testComputeCount */
    @org.junit.Test(timeout = 1000)
    public void testComputeCount_cf603_failAssert17() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            com.clearspring.analytics.stream.cardinality.LinearCounting lc = new com.clearspring.analytics.stream.cardinality.LinearCounting(4);
            lc.offer(0);
            lc.offer(1);
            lc.offer(2);
            lc.offer(3);
            lc.offer(16);
            lc.offer(17);
            lc.offer(18);
            lc.offer(19);
            // StatementAdderOnAssert create random local variable
            int vc_76 = -288675729;
            // StatementAdderMethod cloned existing statement
            lc.offerHashed(vc_76);
            // MethodAssertGenerator build local variable
            Object o_15_0 = lc.computeCount();
            org.junit.Assert.fail("testComputeCount_cf603 should have thrown UnsupportedOperationException");
        } catch (java.lang.UnsupportedOperationException eee) {
        }
    }

    /* amplification of com.clearspring.analytics.stream.cardinality.TestLinearCounting#testComputeCount */
    @org.junit.Test(timeout = 1000)
    public void testComputeCount_cf602_failAssert16_add1795() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            com.clearspring.analytics.stream.cardinality.LinearCounting lc = new com.clearspring.analytics.stream.cardinality.LinearCounting(4);
            lc.offer(0);
            lc.offer(1);
            lc.offer(2);
            lc.offer(3);
            lc.offer(16);
            // AssertGenerator replace invocation
            boolean o_testComputeCount_cf602_failAssert16_add1795__10 = // MethodCallAdder
lc.offer(17);
            // AssertGenerator add assertion
            org.junit.Assert.assertTrue(o_testComputeCount_cf602_failAssert16_add1795__10);
            lc.offer(17);
            lc.offer(18);
            lc.offer(19);
            // StatementAdderOnAssert create literal from method
            int int_vc_2 = 17;
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(int_vc_2, 17);
            // StatementAdderMethod cloned existing statement
            lc.offerHashed(int_vc_2);
            // MethodAssertGenerator build local variable
            Object o_15_0 = lc.computeCount();
            org.junit.Assert.fail("testComputeCount_cf602 should have thrown UnsupportedOperationException");
        } catch (java.lang.UnsupportedOperationException eee) {
        }
    }

    /* amplification of com.clearspring.analytics.stream.cardinality.TestLinearCounting#testComputeCount */
    @org.junit.Test(timeout = 1000)
    public void testComputeCount_cf641_cf2131() {
        com.clearspring.analytics.stream.cardinality.LinearCounting lc = new com.clearspring.analytics.stream.cardinality.LinearCounting(4);
        lc.offer(0);
        lc.offer(1);
        lc.offer(2);
        lc.offer(3);
        lc.offer(16);
        lc.offer(17);
        lc.offer(18);
        lc.offer(19);
        // AssertGenerator replace invocation
        int o_testComputeCount_cf641__11 = // StatementAdderMethod cloned existing statement
lc.computeCount();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(o_testComputeCount_cf641__11, 27);
        // AssertGenerator replace invocation
        int o_testComputeCount_cf641_cf2131__15 = // StatementAdderMethod cloned existing statement
lc.sizeof();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(o_testComputeCount_cf641_cf2131__15, 4);
        org.junit.Assert.assertEquals(27, lc.computeCount());
    }

    /* amplification of com.clearspring.analytics.stream.cardinality.TestLinearCounting#testComputeCount */
    @org.junit.Test(timeout = 1000)
    public void testComputeCount_add576_cf1219() {
        com.clearspring.analytics.stream.cardinality.LinearCounting lc = new com.clearspring.analytics.stream.cardinality.LinearCounting(4);
        lc.offer(0);
        lc.offer(1);
        lc.offer(2);
        lc.offer(3);
        lc.offer(16);
        lc.offer(17);
        // AssertGenerator replace invocation
        boolean o_testComputeCount_add576__9 = // MethodCallAdder
lc.offer(18);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_testComputeCount_add576__9);
        lc.offer(18);
        lc.offer(19);
        // AssertGenerator replace invocation
        boolean o_testComputeCount_add576_cf1219__15 = // StatementAdderMethod cloned existing statement
lc.isSaturated();
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_testComputeCount_add576_cf1219__15);
        org.junit.Assert.assertEquals(27, lc.computeCount());
    }

    /* amplification of com.clearspring.analytics.stream.cardinality.TestLinearCounting#testComputeCount */
    @org.junit.Test(timeout = 1000)
    public void testComputeCount_cf617_cf1975_failAssert40() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            com.clearspring.analytics.stream.cardinality.LinearCounting lc = new com.clearspring.analytics.stream.cardinality.LinearCounting(4);
            lc.offer(0);
            lc.offer(1);
            lc.offer(2);
            lc.offer(3);
            lc.offer(16);
            lc.offer(17);
            lc.offer(18);
            lc.offer(19);
            // AssertGenerator replace invocation
            byte[] o_testComputeCount_cf617__11 = // StatementAdderMethod cloned existing statement
lc.getBytes();
            // AssertGenerator add assertion
            byte[] array_2014778433 = new byte[]{1, 0, 4, 37};
	byte[] array_624141050 = (byte[])o_testComputeCount_cf617__11;
	for(int ii = 0; ii <array_2014778433.length; ii++) {
		org.junit.Assert.assertEquals(array_2014778433[ii], array_624141050[ii]);
	};
            // StatementAdderOnAssert create random local variable
            long vc_453 = -2588483112388889679L;
            // StatementAdderMethod cloned existing statement
            lc.offerHashed(vc_453);
            // MethodAssertGenerator build local variable
            Object o_19_0 = lc.computeCount();
            org.junit.Assert.fail("testComputeCount_cf617_cf1975 should have thrown UnsupportedOperationException");
        } catch (java.lang.UnsupportedOperationException eee) {
        }
    }

    /* amplification of com.clearspring.analytics.stream.cardinality.TestLinearCounting#testComputeCount */
    @org.junit.Test(timeout = 1000)
    public void testComputeCount_cf657_cf2551() {
        com.clearspring.analytics.stream.cardinality.LinearCounting lc = new com.clearspring.analytics.stream.cardinality.LinearCounting(4);
        lc.offer(0);
        lc.offer(1);
        lc.offer(2);
        lc.offer(3);
        lc.offer(16);
        lc.offer(17);
        lc.offer(18);
        lc.offer(19);
        // AssertGenerator replace invocation
        long o_testComputeCount_cf657__11 = // StatementAdderMethod cloned existing statement
lc.cardinality();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(o_testComputeCount_cf657__11, 5L);
        // AssertGenerator replace invocation
        java.lang.String o_testComputeCount_cf657_cf2551__15 = // StatementAdderMethod cloned existing statement
lc.mapAsBitString();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(o_testComputeCount_cf657_cf2551__15, "00000001000000000000010000100101");
        org.junit.Assert.assertEquals(27, lc.computeCount());
    }

    /* amplification of com.clearspring.analytics.stream.cardinality.TestLinearCounting#testComputeCount */
    @org.junit.Test(timeout = 1000)
    public void testComputeCount_cf640_failAssert25_add2039() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            com.clearspring.analytics.stream.cardinality.LinearCounting lc = new com.clearspring.analytics.stream.cardinality.LinearCounting(4);
            lc.offer(0);
            // AssertGenerator replace invocation
            boolean o_testComputeCount_cf640_failAssert25_add2039__6 = // MethodCallAdder
lc.offer(1);
            // AssertGenerator add assertion
            org.junit.Assert.assertTrue(o_testComputeCount_cf640_failAssert25_add2039__6);
            lc.offer(1);
            lc.offer(2);
            lc.offer(3);
            lc.offer(16);
            lc.offer(17);
            lc.offer(18);
            lc.offer(19);
            // StatementAdderOnAssert create null value
            com.clearspring.analytics.stream.cardinality.LinearCounting vc_92 = (com.clearspring.analytics.stream.cardinality.LinearCounting)null;
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(vc_92);
            // StatementAdderMethod cloned existing statement
            vc_92.computeCount();
            // MethodAssertGenerator build local variable
            Object o_15_0 = lc.computeCount();
            org.junit.Assert.fail("testComputeCount_cf640 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    /* amplification of com.clearspring.analytics.stream.cardinality.TestLinearCounting#testComputeCount */
    @org.junit.Test(timeout = 1000)
    public void testComputeCount_add575_cf1141_failAssert49_add4798() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            com.clearspring.analytics.stream.cardinality.LinearCounting lc = new com.clearspring.analytics.stream.cardinality.LinearCounting(4);
            lc.offer(0);
            lc.offer(1);
            lc.offer(2);
            lc.offer(3);
            lc.offer(16);
            // AssertGenerator replace invocation
            boolean o_testComputeCount_add575__8 = // MethodCallAdder
lc.offer(17);
            // AssertGenerator add assertion
            org.junit.Assert.assertTrue(o_testComputeCount_add575__8);
            lc.offer(17);
            lc.offer(18);
            // AssertGenerator replace invocation
            boolean o_testComputeCount_add575_cf1141_failAssert49_add4798__16 = // MethodCallAdder
lc.offer(19);
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(o_testComputeCount_add575_cf1141_failAssert49_add4798__16);
            lc.offer(19);
            // StatementAdderOnAssert create random local variable
            int vc_280 = -1293881218;
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(vc_280, -1293881218);
            // StatementAdderOnAssert create null value
            com.clearspring.analytics.stream.cardinality.LinearCounting vc_278 = (com.clearspring.analytics.stream.cardinality.LinearCounting)null;
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(vc_278);
            // StatementAdderMethod cloned existing statement
            vc_278.offerHashed(vc_280);
            // MethodAssertGenerator build local variable
            Object o_21_0 = lc.computeCount();
            org.junit.Assert.fail("testComputeCount_add575_cf1141 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    /* amplification of com.clearspring.analytics.stream.cardinality.TestLinearCounting#testComputeCount */
    @org.junit.Test(timeout = 1000)
    public void testComputeCount_cf645_cf2238_failAssert8_add3187() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            com.clearspring.analytics.stream.cardinality.LinearCounting lc = new com.clearspring.analytics.stream.cardinality.LinearCounting(4);
            // AssertGenerator replace invocation
            boolean o_testComputeCount_cf645_cf2238_failAssert8_add3187__5 = // MethodCallAdder
lc.offer(0);
            // AssertGenerator add assertion
            org.junit.Assert.assertTrue(o_testComputeCount_cf645_cf2238_failAssert8_add3187__5);
            lc.offer(0);
            lc.offer(1);
            lc.offer(2);
            lc.offer(3);
            lc.offer(16);
            lc.offer(17);
            lc.offer(18);
            lc.offer(19);
            // AssertGenerator replace invocation
            int o_testComputeCount_cf645__11 = // StatementAdderMethod cloned existing statement
lc.getCount();
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(o_testComputeCount_cf645__11, 27);
            // StatementAdderOnAssert create null value
            com.clearspring.analytics.stream.cardinality.LinearCounting vc_540 = (com.clearspring.analytics.stream.cardinality.LinearCounting)null;
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(vc_540);
            // StatementAdderMethod cloned existing statement
            vc_540.mapAsBitString();
            // MethodAssertGenerator build local variable
            Object o_19_0 = lc.computeCount();
            org.junit.Assert.fail("testComputeCount_cf645_cf2238 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    /* amplification of com.clearspring.analytics.stream.cardinality.TestLinearCounting#testComputeCount */
    @org.junit.Test(timeout = 1000)
    public void testComputeCount_add577_cf1337_cf4676_failAssert29() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            com.clearspring.analytics.stream.cardinality.LinearCounting lc = new com.clearspring.analytics.stream.cardinality.LinearCounting(4);
            lc.offer(0);
            lc.offer(1);
            lc.offer(2);
            lc.offer(3);
            lc.offer(16);
            lc.offer(17);
            lc.offer(18);
            // AssertGenerator replace invocation
            boolean o_testComputeCount_add577__10 = // MethodCallAdder
lc.offer(19);
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(o_testComputeCount_add577__10);
            lc.offer(19);
            // AssertGenerator replace invocation
            byte[] o_testComputeCount_add577_cf1337__15 = // StatementAdderMethod cloned existing statement
lc.getBytes();
            // AssertGenerator add assertion
            byte[] array_1081144457 = new byte[]{1, 0, 4, 37};
	byte[] array_1164379757 = (byte[])o_testComputeCount_add577_cf1337__15;
	for(int ii = 0; ii <array_1081144457.length; ii++) {
		org.junit.Assert.assertEquals(array_1081144457[ii], array_1164379757[ii]);
	};
            // StatementAdderOnAssert create random local variable
            int vc_1062 = -719277457;
            // StatementAdderMethod cloned existing statement
            lc.offerHashed(vc_1062);
            // MethodAssertGenerator build local variable
            Object o_23_0 = lc.computeCount();
            org.junit.Assert.fail("testComputeCount_add577_cf1337_cf4676 should have thrown UnsupportedOperationException");
        } catch (java.lang.UnsupportedOperationException eee) {
        }
    }

    /* amplification of com.clearspring.analytics.stream.cardinality.TestLinearCounting#testMerge */
    @org.junit.Test(timeout = 1000)
    public void testMerge_cf5853() throws com.clearspring.analytics.stream.cardinality.LinearCounting.LinearCountingMergeException {
        int numToMerge = 5;
        int size = 65536;
        int cardinality = 1000;
        com.clearspring.analytics.stream.cardinality.LinearCounting[] lcs = new com.clearspring.analytics.stream.cardinality.LinearCounting[numToMerge];
        com.clearspring.analytics.stream.cardinality.LinearCounting baseline = new com.clearspring.analytics.stream.cardinality.LinearCounting(size);
        for (int i = 0; i < numToMerge; i++) {
            lcs[i] = new com.clearspring.analytics.stream.cardinality.LinearCounting(size);
            for (int j = 0; j < cardinality; j++) {
                double val = java.lang.Math.random();
                lcs[i].offer(val);
                baseline.offer(val);
            }
        }
        int expectedCardinality = numToMerge * cardinality;
        long mergedEstimate = com.clearspring.analytics.stream.cardinality.LinearCounting.mergeEstimators(lcs).cardinality();
        double error = (java.lang.Math.abs((mergedEstimate - expectedCardinality))) / ((double) (expectedCardinality));
        org.junit.Assert.assertEquals(0.01, error, 0.01);
        com.clearspring.analytics.stream.cardinality.LinearCounting lc = lcs[0];
        lcs = java.util.Arrays.asList(lcs).subList(1, lcs.length).toArray(new com.clearspring.analytics.stream.cardinality.LinearCounting[0]);
        mergedEstimate = lc.merge(lcs).cardinality();
        error = (java.lang.Math.abs((mergedEstimate - expectedCardinality))) / ((double) (expectedCardinality));
        org.junit.Assert.assertEquals(0.01, error, 0.01);
        long baselineEstimate = baseline.cardinality();
        // StatementAdderOnAssert create null value
        com.clearspring.analytics.stream.cardinality.LinearCounting[] vc_1346 = (com.clearspring.analytics.stream.cardinality.LinearCounting[])null;
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(vc_1346);
        // StatementAdderOnAssert create null value
        com.clearspring.analytics.stream.cardinality.LinearCounting vc_1344 = (com.clearspring.analytics.stream.cardinality.LinearCounting)null;
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(vc_1344);
        // AssertGenerator replace invocation
        com.clearspring.analytics.stream.cardinality.LinearCounting o_testMerge_cf5853__45 = // StatementAdderMethod cloned existing statement
vc_1344.mergeEstimators(vc_1346);
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(o_testMerge_cf5853__45);
        org.junit.Assert.assertEquals(baselineEstimate, mergedEstimate);
    }

    /* amplification of com.clearspring.analytics.stream.cardinality.TestLinearCounting#testMerge */
    @org.junit.Test(timeout = 1000)
    public void testMerge_cf5883() throws com.clearspring.analytics.stream.cardinality.LinearCounting.LinearCountingMergeException {
        int numToMerge = 5;
        int size = 65536;
        int cardinality = 1000;
        com.clearspring.analytics.stream.cardinality.LinearCounting[] lcs = new com.clearspring.analytics.stream.cardinality.LinearCounting[numToMerge];
        com.clearspring.analytics.stream.cardinality.LinearCounting baseline = new com.clearspring.analytics.stream.cardinality.LinearCounting(size);
        for (int i = 0; i < numToMerge; i++) {
            lcs[i] = new com.clearspring.analytics.stream.cardinality.LinearCounting(size);
            for (int j = 0; j < cardinality; j++) {
                double val = java.lang.Math.random();
                lcs[i].offer(val);
                baseline.offer(val);
            }
        }
        int expectedCardinality = numToMerge * cardinality;
        long mergedEstimate = com.clearspring.analytics.stream.cardinality.LinearCounting.mergeEstimators(lcs).cardinality();
        double error = (java.lang.Math.abs((mergedEstimate - expectedCardinality))) / ((double) (expectedCardinality));
        org.junit.Assert.assertEquals(0.01, error, 0.01);
        com.clearspring.analytics.stream.cardinality.LinearCounting lc = lcs[0];
        lcs = java.util.Arrays.asList(lcs).subList(1, lcs.length).toArray(new com.clearspring.analytics.stream.cardinality.LinearCounting[0]);
        mergedEstimate = lc.merge(lcs).cardinality();
        error = (java.lang.Math.abs((mergedEstimate - expectedCardinality))) / ((double) (expectedCardinality));
        org.junit.Assert.assertEquals(0.01, error, 0.01);
        long baselineEstimate = baseline.cardinality();
        // StatementAddOnAssert local variable replacement
        com.clearspring.analytics.stream.cardinality.LinearCounting lc2 = new com.clearspring.analytics.stream.cardinality.LinearCounting(lc.getBytes());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((com.clearspring.analytics.stream.cardinality.LinearCounting)lc2).sizeof(), 65536);
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((com.clearspring.analytics.stream.cardinality.LinearCounting)lc2).isSaturated());
        // StatementAdderMethod cloned existing statement
        lc2.mapAsBitString();
        org.junit.Assert.assertEquals(baselineEstimate, mergedEstimate);
    }

    /* amplification of com.clearspring.analytics.stream.cardinality.TestLinearCounting#testMerge */
    @org.junit.Test(timeout = 1000)
    public void testMerge_cf5813_failAssert28() throws com.clearspring.analytics.stream.cardinality.LinearCounting.LinearCountingMergeException {
        // AssertGenerator generate try/catch block with fail statement
        try {
            int numToMerge = 5;
            int size = 65536;
            int cardinality = 1000;
            com.clearspring.analytics.stream.cardinality.LinearCounting[] lcs = new com.clearspring.analytics.stream.cardinality.LinearCounting[numToMerge];
            com.clearspring.analytics.stream.cardinality.LinearCounting baseline = new com.clearspring.analytics.stream.cardinality.LinearCounting(size);
            for (int i = 0; i < numToMerge; i++) {
                lcs[i] = new com.clearspring.analytics.stream.cardinality.LinearCounting(size);
                for (int j = 0; j < cardinality; j++) {
                    double val = java.lang.Math.random();
                    lcs[i].offer(val);
                    baseline.offer(val);
                }
            }
            int expectedCardinality = numToMerge * cardinality;
            long mergedEstimate = com.clearspring.analytics.stream.cardinality.LinearCounting.mergeEstimators(lcs).cardinality();
            double error = (java.lang.Math.abs((mergedEstimate - expectedCardinality))) / ((double) (expectedCardinality));
            com.clearspring.analytics.stream.cardinality.LinearCounting lc = lcs[0];
            lcs = java.util.Arrays.asList(lcs).subList(1, lcs.length).toArray(new com.clearspring.analytics.stream.cardinality.LinearCounting[0]);
            mergedEstimate = lc.merge(lcs).cardinality();
            error = (java.lang.Math.abs((mergedEstimate - expectedCardinality))) / ((double) (expectedCardinality));
            long baselineEstimate = baseline.cardinality();
            // StatementAdderMethod cloned existing statement
            lc.offerHashed(cardinality);
            org.junit.Assert.fail("testMerge_cf5813 should have thrown UnsupportedOperationException");
        } catch (java.lang.UnsupportedOperationException eee) {
        }
    }

    /* amplification of com.clearspring.analytics.stream.cardinality.TestLinearCounting#testMerge */
    @org.junit.Test(timeout = 1000)
    public void testMerge_cf5830_failAssert37() throws com.clearspring.analytics.stream.cardinality.LinearCounting.LinearCountingMergeException {
        // AssertGenerator generate try/catch block with fail statement
        try {
            int numToMerge = 5;
            int size = 65536;
            int cardinality = 1000;
            com.clearspring.analytics.stream.cardinality.LinearCounting[] lcs = new com.clearspring.analytics.stream.cardinality.LinearCounting[numToMerge];
            com.clearspring.analytics.stream.cardinality.LinearCounting baseline = new com.clearspring.analytics.stream.cardinality.LinearCounting(size);
            for (int i = 0; i < numToMerge; i++) {
                lcs[i] = new com.clearspring.analytics.stream.cardinality.LinearCounting(size);
                for (int j = 0; j < cardinality; j++) {
                    double val = java.lang.Math.random();
                    lcs[i].offer(val);
                    baseline.offer(val);
                }
            }
            int expectedCardinality = numToMerge * cardinality;
            long mergedEstimate = com.clearspring.analytics.stream.cardinality.LinearCounting.mergeEstimators(lcs).cardinality();
            double error = (java.lang.Math.abs((mergedEstimate - expectedCardinality))) / ((double) (expectedCardinality));
            com.clearspring.analytics.stream.cardinality.LinearCounting lc = lcs[0];
            lcs = java.util.Arrays.asList(lcs).subList(1, lcs.length).toArray(new com.clearspring.analytics.stream.cardinality.LinearCounting[0]);
            mergedEstimate = lc.merge(lcs).cardinality();
            error = (java.lang.Math.abs((mergedEstimate - expectedCardinality))) / ((double) (expectedCardinality));
            long baselineEstimate = baseline.cardinality();
            // StatementAdderOnAssert create random local variable
            long vc_1337 = -1922596824742785822L;
            // StatementAdderMethod cloned existing statement
            lc.offerHashed(vc_1337);
            org.junit.Assert.fail("testMerge_cf5830 should have thrown UnsupportedOperationException");
        } catch (java.lang.UnsupportedOperationException eee) {
        }
    }

    /* amplification of com.clearspring.analytics.stream.cardinality.TestLinearCounting#testMerge */
    @org.junit.Test(timeout = 1000)
    public void testMerge_cf5805() throws com.clearspring.analytics.stream.cardinality.LinearCounting.LinearCountingMergeException {
        int numToMerge = 5;
        int size = 65536;
        int cardinality = 1000;
        com.clearspring.analytics.stream.cardinality.LinearCounting[] lcs = new com.clearspring.analytics.stream.cardinality.LinearCounting[numToMerge];
        com.clearspring.analytics.stream.cardinality.LinearCounting baseline = new com.clearspring.analytics.stream.cardinality.LinearCounting(size);
        for (int i = 0; i < numToMerge; i++) {
            lcs[i] = new com.clearspring.analytics.stream.cardinality.LinearCounting(size);
            for (int j = 0; j < cardinality; j++) {
                double val = java.lang.Math.random();
                lcs[i].offer(val);
                baseline.offer(val);
            }
        }
        int expectedCardinality = numToMerge * cardinality;
        long mergedEstimate = com.clearspring.analytics.stream.cardinality.LinearCounting.mergeEstimators(lcs).cardinality();
        double error = (java.lang.Math.abs((mergedEstimate - expectedCardinality))) / ((double) (expectedCardinality));
        org.junit.Assert.assertEquals(0.01, error, 0.01);
        com.clearspring.analytics.stream.cardinality.LinearCounting lc = lcs[0];
        lcs = java.util.Arrays.asList(lcs).subList(1, lcs.length).toArray(new com.clearspring.analytics.stream.cardinality.LinearCounting[0]);
        mergedEstimate = lc.merge(lcs).cardinality();
        error = (java.lang.Math.abs((mergedEstimate - expectedCardinality))) / ((double) (expectedCardinality));
        org.junit.Assert.assertEquals(0.01, error, 0.01);
        long baselineEstimate = baseline.cardinality();
        // StatementAdderOnAssert create null value
        java.lang.Object vc_1330 = (java.lang.Object)null;
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(vc_1330);
        // StatementAddOnAssert local variable replacement
        com.clearspring.analytics.stream.cardinality.LinearCounting lc2 = new com.clearspring.analytics.stream.cardinality.LinearCounting(lc.getBytes());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((com.clearspring.analytics.stream.cardinality.LinearCounting)lc2).sizeof(), 65536);
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((com.clearspring.analytics.stream.cardinality.LinearCounting)lc2).isSaturated());
        // AssertGenerator replace invocation
        boolean o_testMerge_cf5805__47 = // StatementAdderMethod cloned existing statement
lc2.offer(vc_1330);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_testMerge_cf5805__47);
        org.junit.Assert.assertEquals(baselineEstimate, mergedEstimate);
    }

    /* amplification of com.clearspring.analytics.stream.cardinality.TestLinearCounting#testMerge */
    @org.junit.Test(timeout = 1000)
    public void testMerge_cf5804() throws com.clearspring.analytics.stream.cardinality.LinearCounting.LinearCountingMergeException {
        int numToMerge = 5;
        int size = 65536;
        int cardinality = 1000;
        com.clearspring.analytics.stream.cardinality.LinearCounting[] lcs = new com.clearspring.analytics.stream.cardinality.LinearCounting[numToMerge];
        com.clearspring.analytics.stream.cardinality.LinearCounting baseline = new com.clearspring.analytics.stream.cardinality.LinearCounting(size);
        for (int i = 0; i < numToMerge; i++) {
            lcs[i] = new com.clearspring.analytics.stream.cardinality.LinearCounting(size);
            for (int j = 0; j < cardinality; j++) {
                double val = java.lang.Math.random();
                lcs[i].offer(val);
                baseline.offer(val);
            }
        }
        int expectedCardinality = numToMerge * cardinality;
        long mergedEstimate = com.clearspring.analytics.stream.cardinality.LinearCounting.mergeEstimators(lcs).cardinality();
        double error = (java.lang.Math.abs((mergedEstimate - expectedCardinality))) / ((double) (expectedCardinality));
        org.junit.Assert.assertEquals(0.01, error, 0.01);
        com.clearspring.analytics.stream.cardinality.LinearCounting lc = lcs[0];
        lcs = java.util.Arrays.asList(lcs).subList(1, lcs.length).toArray(new com.clearspring.analytics.stream.cardinality.LinearCounting[0]);
        mergedEstimate = lc.merge(lcs).cardinality();
        error = (java.lang.Math.abs((mergedEstimate - expectedCardinality))) / ((double) (expectedCardinality));
        org.junit.Assert.assertEquals(0.01, error, 0.01);
        long baselineEstimate = baseline.cardinality();
        // StatementAdderOnAssert create random local variable
        java.lang.Object vc_1331 = new java.lang.Object();
        // AssertGenerator replace invocation
        boolean o_testMerge_cf5804__43 = // StatementAdderMethod cloned existing statement
baseline.offer(vc_1331);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_testMerge_cf5804__43);
        org.junit.Assert.assertEquals(baselineEstimate, mergedEstimate);
    }

    /* amplification of com.clearspring.analytics.stream.cardinality.TestLinearCounting#testMerge */
    @org.junit.Test
    public void testMerge_literalMutation5747_failAssert0() throws com.clearspring.analytics.stream.cardinality.LinearCounting.LinearCountingMergeException {
        // AssertGenerator generate try/catch block with fail statement
        try {
            int numToMerge = 0;
            int size = 65536;
            int cardinality = 1000;
            com.clearspring.analytics.stream.cardinality.LinearCounting[] lcs = new com.clearspring.analytics.stream.cardinality.LinearCounting[numToMerge];
            com.clearspring.analytics.stream.cardinality.LinearCounting baseline = new com.clearspring.analytics.stream.cardinality.LinearCounting(size);
            for (int i = 0; i < numToMerge; i++) {
                lcs[i] = new com.clearspring.analytics.stream.cardinality.LinearCounting(size);
                for (int j = 0; j < cardinality; j++) {
                    double val = java.lang.Math.random();
                    lcs[i].offer(val);
                    baseline.offer(val);
                }
            }
            int expectedCardinality = numToMerge * cardinality;
            long mergedEstimate = com.clearspring.analytics.stream.cardinality.LinearCounting.mergeEstimators(lcs).cardinality();
            double error = (java.lang.Math.abs((mergedEstimate - expectedCardinality))) / ((double) (expectedCardinality));
            com.clearspring.analytics.stream.cardinality.LinearCounting lc = lcs[0];
            lcs = java.util.Arrays.asList(lcs).subList(1, lcs.length).toArray(new com.clearspring.analytics.stream.cardinality.LinearCounting[0]);
            mergedEstimate = lc.merge(lcs).cardinality();
            error = (java.lang.Math.abs((mergedEstimate - expectedCardinality))) / ((double) (expectedCardinality));
            long baselineEstimate = baseline.cardinality();
            org.junit.Assert.fail("testMerge_literalMutation5747 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    /* amplification of com.clearspring.analytics.stream.cardinality.TestLinearCounting#testMerge */
    @org.junit.Test(timeout = 1000)
    public void testMerge_cf5878() throws com.clearspring.analytics.stream.cardinality.LinearCounting.LinearCountingMergeException {
        int numToMerge = 5;
        int size = 65536;
        int cardinality = 1000;
        com.clearspring.analytics.stream.cardinality.LinearCounting[] lcs = new com.clearspring.analytics.stream.cardinality.LinearCounting[numToMerge];
        com.clearspring.analytics.stream.cardinality.LinearCounting baseline = new com.clearspring.analytics.stream.cardinality.LinearCounting(size);
        for (int i = 0; i < numToMerge; i++) {
            lcs[i] = new com.clearspring.analytics.stream.cardinality.LinearCounting(size);
            for (int j = 0; j < cardinality; j++) {
                double val = java.lang.Math.random();
                lcs[i].offer(val);
                baseline.offer(val);
            }
        }
        int expectedCardinality = numToMerge * cardinality;
        long mergedEstimate = com.clearspring.analytics.stream.cardinality.LinearCounting.mergeEstimators(lcs).cardinality();
        double error = (java.lang.Math.abs((mergedEstimate - expectedCardinality))) / ((double) (expectedCardinality));
        org.junit.Assert.assertEquals(0.01, error, 0.01);
        com.clearspring.analytics.stream.cardinality.LinearCounting lc = lcs[0];
        lcs = java.util.Arrays.asList(lcs).subList(1, lcs.length).toArray(new com.clearspring.analytics.stream.cardinality.LinearCounting[0]);
        mergedEstimate = lc.merge(lcs).cardinality();
        error = (java.lang.Math.abs((mergedEstimate - expectedCardinality))) / ((double) (expectedCardinality));
        org.junit.Assert.assertEquals(0.01, error, 0.01);
        long baselineEstimate = baseline.cardinality();
        // AssertGenerator replace invocation
        int o_testMerge_cf5878__41 = // StatementAdderMethod cloned existing statement
baseline.sizeof();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(o_testMerge_cf5878__41, 65536);
        org.junit.Assert.assertEquals(baselineEstimate, mergedEstimate);
    }

    /* amplification of com.clearspring.analytics.stream.cardinality.TestLinearCounting#testMerge */
    @org.junit.Test(timeout = 1000)
    public void testMerge_cf5847() throws com.clearspring.analytics.stream.cardinality.LinearCounting.LinearCountingMergeException {
        int numToMerge = 5;
        int size = 65536;
        int cardinality = 1000;
        com.clearspring.analytics.stream.cardinality.LinearCounting[] lcs = new com.clearspring.analytics.stream.cardinality.LinearCounting[numToMerge];
        com.clearspring.analytics.stream.cardinality.LinearCounting baseline = new com.clearspring.analytics.stream.cardinality.LinearCounting(size);
        for (int i = 0; i < numToMerge; i++) {
            lcs[i] = new com.clearspring.analytics.stream.cardinality.LinearCounting(size);
            for (int j = 0; j < cardinality; j++) {
                double val = java.lang.Math.random();
                lcs[i].offer(val);
                baseline.offer(val);
            }
        }
        int expectedCardinality = numToMerge * cardinality;
        long mergedEstimate = com.clearspring.analytics.stream.cardinality.LinearCounting.mergeEstimators(lcs).cardinality();
        double error = (java.lang.Math.abs((mergedEstimate - expectedCardinality))) / ((double) (expectedCardinality));
        org.junit.Assert.assertEquals(0.01, error, 0.01);
        com.clearspring.analytics.stream.cardinality.LinearCounting lc = lcs[0];
        lcs = java.util.Arrays.asList(lcs).subList(1, lcs.length).toArray(new com.clearspring.analytics.stream.cardinality.LinearCounting[0]);
        mergedEstimate = lc.merge(lcs).cardinality();
        error = (java.lang.Math.abs((mergedEstimate - expectedCardinality))) / ((double) (expectedCardinality));
        org.junit.Assert.assertEquals(0.01, error, 0.01);
        long baselineEstimate = baseline.cardinality();
        // StatementAdderOnAssert create null value
        com.clearspring.analytics.stream.cardinality.ICardinality[] vc_1342 = (com.clearspring.analytics.stream.cardinality.ICardinality[])null;
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(vc_1342);
        // StatementAddOnAssert local variable replacement
        com.clearspring.analytics.stream.cardinality.LinearCounting lc2 = new com.clearspring.analytics.stream.cardinality.LinearCounting(lc.getBytes());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((com.clearspring.analytics.stream.cardinality.LinearCounting)lc2).sizeof(), 65536);
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((com.clearspring.analytics.stream.cardinality.LinearCounting)lc2).isSaturated());
        // AssertGenerator replace invocation
        com.clearspring.analytics.stream.cardinality.ICardinality o_testMerge_cf5847__47 = // StatementAdderMethod cloned existing statement
lc2.merge(vc_1342);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((com.clearspring.analytics.stream.cardinality.LinearCounting)o_testMerge_cf5847__47).sizeof(), 65536);
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((com.clearspring.analytics.stream.cardinality.LinearCounting)o_testMerge_cf5847__47).isSaturated());
        org.junit.Assert.assertEquals(baselineEstimate, mergedEstimate);
    }

    /* amplification of com.clearspring.analytics.stream.cardinality.TestLinearCounting#testMerge */
    @org.junit.Test(timeout = 1000)
    public void testMerge_cf5879() throws com.clearspring.analytics.stream.cardinality.LinearCounting.LinearCountingMergeException {
        int numToMerge = 5;
        int size = 65536;
        int cardinality = 1000;
        com.clearspring.analytics.stream.cardinality.LinearCounting[] lcs = new com.clearspring.analytics.stream.cardinality.LinearCounting[numToMerge];
        com.clearspring.analytics.stream.cardinality.LinearCounting baseline = new com.clearspring.analytics.stream.cardinality.LinearCounting(size);
        for (int i = 0; i < numToMerge; i++) {
            lcs[i] = new com.clearspring.analytics.stream.cardinality.LinearCounting(size);
            for (int j = 0; j < cardinality; j++) {
                double val = java.lang.Math.random();
                lcs[i].offer(val);
                baseline.offer(val);
            }
        }
        int expectedCardinality = numToMerge * cardinality;
        long mergedEstimate = com.clearspring.analytics.stream.cardinality.LinearCounting.mergeEstimators(lcs).cardinality();
        double error = (java.lang.Math.abs((mergedEstimate - expectedCardinality))) / ((double) (expectedCardinality));
        org.junit.Assert.assertEquals(0.01, error, 0.01);
        com.clearspring.analytics.stream.cardinality.LinearCounting lc = lcs[0];
        lcs = java.util.Arrays.asList(lcs).subList(1, lcs.length).toArray(new com.clearspring.analytics.stream.cardinality.LinearCounting[0]);
        mergedEstimate = lc.merge(lcs).cardinality();
        error = (java.lang.Math.abs((mergedEstimate - expectedCardinality))) / ((double) (expectedCardinality));
        org.junit.Assert.assertEquals(0.01, error, 0.01);
        long baselineEstimate = baseline.cardinality();
        // StatementAddOnAssert local variable replacement
        com.clearspring.analytics.stream.cardinality.LinearCounting lc2 = new com.clearspring.analytics.stream.cardinality.LinearCounting(lc.getBytes());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((com.clearspring.analytics.stream.cardinality.LinearCounting)lc2).sizeof(), 65536);
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((com.clearspring.analytics.stream.cardinality.LinearCounting)lc2).isSaturated());
        // AssertGenerator replace invocation
        int o_testMerge_cf5879__45 = // StatementAdderMethod cloned existing statement
lc2.sizeof();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(o_testMerge_cf5879__45, 65536);
        org.junit.Assert.assertEquals(baselineEstimate, mergedEstimate);
    }

    /* amplification of com.clearspring.analytics.stream.cardinality.TestLinearCounting#testMerge */
    @org.junit.Test(timeout = 1000)
    public void testMerge_cf5857() throws com.clearspring.analytics.stream.cardinality.LinearCounting.LinearCountingMergeException {
        int numToMerge = 5;
        int size = 65536;
        int cardinality = 1000;
        com.clearspring.analytics.stream.cardinality.LinearCounting[] lcs = new com.clearspring.analytics.stream.cardinality.LinearCounting[numToMerge];
        com.clearspring.analytics.stream.cardinality.LinearCounting baseline = new com.clearspring.analytics.stream.cardinality.LinearCounting(size);
        for (int i = 0; i < numToMerge; i++) {
            lcs[i] = new com.clearspring.analytics.stream.cardinality.LinearCounting(size);
            for (int j = 0; j < cardinality; j++) {
                double val = java.lang.Math.random();
                lcs[i].offer(val);
                baseline.offer(val);
            }
        }
        int expectedCardinality = numToMerge * cardinality;
        long mergedEstimate = com.clearspring.analytics.stream.cardinality.LinearCounting.mergeEstimators(lcs).cardinality();
        double error = (java.lang.Math.abs((mergedEstimate - expectedCardinality))) / ((double) (expectedCardinality));
        org.junit.Assert.assertEquals(0.01, error, 0.01);
        com.clearspring.analytics.stream.cardinality.LinearCounting lc = lcs[0];
        lcs = java.util.Arrays.asList(lcs).subList(1, lcs.length).toArray(new com.clearspring.analytics.stream.cardinality.LinearCounting[0]);
        mergedEstimate = lc.merge(lcs).cardinality();
        error = (java.lang.Math.abs((mergedEstimate - expectedCardinality))) / ((double) (expectedCardinality));
        org.junit.Assert.assertEquals(0.01, error, 0.01);
        long baselineEstimate = baseline.cardinality();
        // AssertGenerator replace invocation
        com.clearspring.analytics.stream.cardinality.LinearCounting o_testMerge_cf5857__41 = // StatementAdderMethod cloned existing statement
baseline.mergeEstimators(lcs);
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((com.clearspring.analytics.stream.cardinality.LinearCounting)o_testMerge_cf5857__41).isSaturated());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((com.clearspring.analytics.stream.cardinality.LinearCounting)o_testMerge_cf5857__41).sizeof(), 65536);
        org.junit.Assert.assertEquals(baselineEstimate, mergedEstimate);
    }

    /* amplification of com.clearspring.analytics.stream.cardinality.TestLinearCounting#testMerge */
    @org.junit.Test(timeout = 1000)
    public void testMerge_cf5847_literalMutation9885_failAssert53() throws com.clearspring.analytics.stream.cardinality.LinearCounting.LinearCountingMergeException {
        // AssertGenerator generate try/catch block with fail statement
        try {
            int numToMerge = 0;
            int size = 65536;
            int cardinality = 1000;
            com.clearspring.analytics.stream.cardinality.LinearCounting[] lcs = new com.clearspring.analytics.stream.cardinality.LinearCounting[numToMerge];
            com.clearspring.analytics.stream.cardinality.LinearCounting baseline = new com.clearspring.analytics.stream.cardinality.LinearCounting(size);
            for (int i = 0; i < numToMerge; i++) {
                lcs[i] = new com.clearspring.analytics.stream.cardinality.LinearCounting(size);
                for (int j = 0; j < cardinality; j++) {
                    double val = java.lang.Math.random();
                    lcs[i].offer(val);
                    baseline.offer(val);
                }
            }
            int expectedCardinality = numToMerge * cardinality;
            long mergedEstimate = com.clearspring.analytics.stream.cardinality.LinearCounting.mergeEstimators(lcs).cardinality();
            double error = (java.lang.Math.abs((mergedEstimate - expectedCardinality))) / ((double) (expectedCardinality));
            com.clearspring.analytics.stream.cardinality.LinearCounting lc = lcs[0];
            lcs = java.util.Arrays.asList(lcs).subList(1, lcs.length).toArray(new com.clearspring.analytics.stream.cardinality.LinearCounting[0]);
            mergedEstimate = lc.merge(lcs).cardinality();
            error = (java.lang.Math.abs((mergedEstimate - expectedCardinality))) / ((double) (expectedCardinality));
            long baselineEstimate = baseline.cardinality();
            // StatementAdderOnAssert create null value
            com.clearspring.analytics.stream.cardinality.ICardinality[] vc_1342 = (com.clearspring.analytics.stream.cardinality.ICardinality[])null;
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(vc_1342);
            // StatementAddOnAssert local variable replacement
            com.clearspring.analytics.stream.cardinality.LinearCounting lc2 = new com.clearspring.analytics.stream.cardinality.LinearCounting(lc.getBytes());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((com.clearspring.analytics.stream.cardinality.LinearCounting)lc2).sizeof(), 65536);
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((com.clearspring.analytics.stream.cardinality.LinearCounting)lc2).isSaturated());
            // AssertGenerator replace invocation
            com.clearspring.analytics.stream.cardinality.ICardinality o_testMerge_cf5847__47 = // StatementAdderMethod cloned existing statement
lc2.merge(vc_1342);
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((com.clearspring.analytics.stream.cardinality.LinearCounting)o_testMerge_cf5847__47).sizeof(), 65536);
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((com.clearspring.analytics.stream.cardinality.LinearCounting)o_testMerge_cf5847__47).isSaturated());
            org.junit.Assert.fail("testMerge_cf5847_literalMutation9885 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    /* amplification of com.clearspring.analytics.stream.cardinality.TestLinearCounting#testMerge */
    @org.junit.Test(timeout = 1000)
    public void testMerge_literalMutation5748_cf6174() throws com.clearspring.analytics.stream.cardinality.LinearCounting.LinearCountingMergeException {
        int numToMerge = 2;
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(numToMerge, 2);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(numToMerge, 2);
        int size = 65536;
        int cardinality = 1000;
        com.clearspring.analytics.stream.cardinality.LinearCounting[] lcs = new com.clearspring.analytics.stream.cardinality.LinearCounting[numToMerge];
        com.clearspring.analytics.stream.cardinality.LinearCounting baseline = new com.clearspring.analytics.stream.cardinality.LinearCounting(size);
        for (int i = 0; i < numToMerge; i++) {
            lcs[i] = new com.clearspring.analytics.stream.cardinality.LinearCounting(size);
            for (int j = 0; j < cardinality; j++) {
                double val = java.lang.Math.random();
                lcs[i].offer(val);
                baseline.offer(val);
            }
        }
        int expectedCardinality = numToMerge * cardinality;
        long mergedEstimate = com.clearspring.analytics.stream.cardinality.LinearCounting.mergeEstimators(lcs).cardinality();
        double error = (java.lang.Math.abs((mergedEstimate - expectedCardinality))) / ((double) (expectedCardinality));
        org.junit.Assert.assertEquals(0.01, error, 0.01);
        com.clearspring.analytics.stream.cardinality.LinearCounting lc = lcs[0];
        lcs = java.util.Arrays.asList(lcs).subList(1, lcs.length).toArray(new com.clearspring.analytics.stream.cardinality.LinearCounting[0]);
        mergedEstimate = lc.merge(lcs).cardinality();
        error = (java.lang.Math.abs((mergedEstimate - expectedCardinality))) / ((double) (expectedCardinality));
        org.junit.Assert.assertEquals(0.01, error, 0.01);
        long baselineEstimate = baseline.cardinality();
        // StatementAdderOnAssert create null value
        com.clearspring.analytics.stream.cardinality.LinearCounting[] vc_1414 = (com.clearspring.analytics.stream.cardinality.LinearCounting[])null;
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(vc_1414);
        // StatementAdderOnAssert create null value
        com.clearspring.analytics.stream.cardinality.LinearCounting vc_1412 = (com.clearspring.analytics.stream.cardinality.LinearCounting)null;
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(vc_1412);
        // AssertGenerator replace invocation
        com.clearspring.analytics.stream.cardinality.LinearCounting o_testMerge_literalMutation5748_cf6174__48 = // StatementAdderMethod cloned existing statement
vc_1412.mergeEstimators(vc_1414);
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(o_testMerge_literalMutation5748_cf6174__48);
        org.junit.Assert.assertEquals(baselineEstimate, mergedEstimate);
    }

    /* amplification of com.clearspring.analytics.stream.cardinality.TestLinearCounting#testMerge */
    @org.junit.Test(timeout = 1000)
    public void testMerge_cf5857_cf10528_failAssert70() throws com.clearspring.analytics.stream.cardinality.LinearCounting.LinearCountingMergeException {
        // AssertGenerator generate try/catch block with fail statement
        try {
            int numToMerge = 5;
            int size = 65536;
            int cardinality = 1000;
            com.clearspring.analytics.stream.cardinality.LinearCounting[] lcs = new com.clearspring.analytics.stream.cardinality.LinearCounting[numToMerge];
            com.clearspring.analytics.stream.cardinality.LinearCounting baseline = new com.clearspring.analytics.stream.cardinality.LinearCounting(size);
            for (int i = 0; i < numToMerge; i++) {
                lcs[i] = new com.clearspring.analytics.stream.cardinality.LinearCounting(size);
                for (int j = 0; j < cardinality; j++) {
                    double val = java.lang.Math.random();
                    lcs[i].offer(val);
                    baseline.offer(val);
                }
            }
            int expectedCardinality = numToMerge * cardinality;
            long mergedEstimate = com.clearspring.analytics.stream.cardinality.LinearCounting.mergeEstimators(lcs).cardinality();
            double error = (java.lang.Math.abs((mergedEstimate - expectedCardinality))) / ((double) (expectedCardinality));
            com.clearspring.analytics.stream.cardinality.LinearCounting lc = lcs[0];
            lcs = java.util.Arrays.asList(lcs).subList(1, lcs.length).toArray(new com.clearspring.analytics.stream.cardinality.LinearCounting[0]);
            mergedEstimate = lc.merge(lcs).cardinality();
            error = (java.lang.Math.abs((mergedEstimate - expectedCardinality))) / ((double) (expectedCardinality));
            long baselineEstimate = baseline.cardinality();
            // AssertGenerator replace invocation
            com.clearspring.analytics.stream.cardinality.LinearCounting o_testMerge_cf5857__41 = // StatementAdderMethod cloned existing statement
baseline.mergeEstimators(lcs);
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((com.clearspring.analytics.stream.cardinality.LinearCounting)o_testMerge_cf5857__41).isSaturated());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((com.clearspring.analytics.stream.cardinality.LinearCounting)o_testMerge_cf5857__41).sizeof(), 65536);
            // StatementAdderOnAssert create random local variable
            long vc_2119 = -3531390603615254690L;
            // StatementAdderMethod cloned existing statement
            lc.offerHashed(vc_2119);
            org.junit.Assert.fail("testMerge_cf5857_cf10528 should have thrown UnsupportedOperationException");
        } catch (java.lang.UnsupportedOperationException eee) {
        }
    }

    /* amplification of com.clearspring.analytics.stream.cardinality.TestLinearCounting#testMerge */
    @org.junit.Test(timeout = 1000)
    public void testMerge_literalMutation5748_cf6140_failAssert54() throws com.clearspring.analytics.stream.cardinality.LinearCounting.LinearCountingMergeException {
        // AssertGenerator generate try/catch block with fail statement
        try {
            int numToMerge = 2;
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(numToMerge, 2);
            int size = 65536;
            int cardinality = 1000;
            com.clearspring.analytics.stream.cardinality.LinearCounting[] lcs = new com.clearspring.analytics.stream.cardinality.LinearCounting[numToMerge];
            com.clearspring.analytics.stream.cardinality.LinearCounting baseline = new com.clearspring.analytics.stream.cardinality.LinearCounting(size);
            for (int i = 0; i < numToMerge; i++) {
                lcs[i] = new com.clearspring.analytics.stream.cardinality.LinearCounting(size);
                for (int j = 0; j < cardinality; j++) {
                    double val = java.lang.Math.random();
                    lcs[i].offer(val);
                    baseline.offer(val);
                }
            }
            int expectedCardinality = numToMerge * cardinality;
            long mergedEstimate = com.clearspring.analytics.stream.cardinality.LinearCounting.mergeEstimators(lcs).cardinality();
            double error = (java.lang.Math.abs((mergedEstimate - expectedCardinality))) / ((double) (expectedCardinality));
            com.clearspring.analytics.stream.cardinality.LinearCounting lc = lcs[0];
            lcs = java.util.Arrays.asList(lcs).subList(1, lcs.length).toArray(new com.clearspring.analytics.stream.cardinality.LinearCounting[0]);
            mergedEstimate = lc.merge(lcs).cardinality();
            error = (java.lang.Math.abs((mergedEstimate - expectedCardinality))) / ((double) (expectedCardinality));
            long baselineEstimate = baseline.cardinality();
            // StatementAdderOnAssert create literal from method
            int int_vc_41 = 2;
            // StatementAddOnAssert local variable replacement
            com.clearspring.analytics.stream.cardinality.LinearCounting lc2 = new com.clearspring.analytics.stream.cardinality.LinearCounting(lc.getBytes());
            // StatementAdderMethod cloned existing statement
            lc2.offerHashed(int_vc_41);
            org.junit.Assert.fail("testMerge_literalMutation5748_cf6140 should have thrown UnsupportedOperationException");
        } catch (java.lang.UnsupportedOperationException eee) {
        }
    }

    /* amplification of com.clearspring.analytics.stream.cardinality.TestLinearCounting#testMerge */
    @org.junit.Test(timeout = 1000)
    public void testMerge_literalMutation5757_cf7204() throws com.clearspring.analytics.stream.cardinality.LinearCounting.LinearCountingMergeException {
        int numToMerge = 5;
        int size = 65536;
        int cardinality = 1001;
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(cardinality, 1001);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(cardinality, 1001);
        com.clearspring.analytics.stream.cardinality.LinearCounting[] lcs = new com.clearspring.analytics.stream.cardinality.LinearCounting[numToMerge];
        com.clearspring.analytics.stream.cardinality.LinearCounting baseline = new com.clearspring.analytics.stream.cardinality.LinearCounting(size);
        for (int i = 0; i < numToMerge; i++) {
            lcs[i] = new com.clearspring.analytics.stream.cardinality.LinearCounting(size);
            for (int j = 0; j < cardinality; j++) {
                double val = java.lang.Math.random();
                lcs[i].offer(val);
                baseline.offer(val);
            }
        }
        int expectedCardinality = numToMerge * cardinality;
        long mergedEstimate = com.clearspring.analytics.stream.cardinality.LinearCounting.mergeEstimators(lcs).cardinality();
        double error = (java.lang.Math.abs((mergedEstimate - expectedCardinality))) / ((double) (expectedCardinality));
        org.junit.Assert.assertEquals(0.01, error, 0.01);
        com.clearspring.analytics.stream.cardinality.LinearCounting lc = lcs[0];
        lcs = java.util.Arrays.asList(lcs).subList(1, lcs.length).toArray(new com.clearspring.analytics.stream.cardinality.LinearCounting[0]);
        mergedEstimate = lc.merge(lcs).cardinality();
        error = (java.lang.Math.abs((mergedEstimate - expectedCardinality))) / ((double) (expectedCardinality));
        org.junit.Assert.assertEquals(0.01, error, 0.01);
        long baselineEstimate = baseline.cardinality();
        // StatementAdderOnAssert create random local variable
        java.lang.Object vc_1637 = new java.lang.Object();
        // AssertGenerator replace invocation
        boolean o_testMerge_literalMutation5757_cf7204__46 = // StatementAdderMethod cloned existing statement
lc.offer(vc_1637);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_testMerge_literalMutation5757_cf7204__46);
        org.junit.Assert.assertEquals(baselineEstimate, mergedEstimate);
    }

    /* amplification of com.clearspring.analytics.stream.cardinality.TestLinearCounting#testMerge */
    @org.junit.Test(timeout = 1000)
    public void testMerge_literalMutation5752_cf6783() throws com.clearspring.analytics.stream.cardinality.LinearCounting.LinearCountingMergeException {
        int numToMerge = 5;
        int size = 32768;
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(size, 32768);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(size, 32768);
        int cardinality = 1000;
        com.clearspring.analytics.stream.cardinality.LinearCounting[] lcs = new com.clearspring.analytics.stream.cardinality.LinearCounting[numToMerge];
        com.clearspring.analytics.stream.cardinality.LinearCounting baseline = new com.clearspring.analytics.stream.cardinality.LinearCounting(size);
        for (int i = 0; i < numToMerge; i++) {
            lcs[i] = new com.clearspring.analytics.stream.cardinality.LinearCounting(size);
            for (int j = 0; j < cardinality; j++) {
                double val = java.lang.Math.random();
                lcs[i].offer(val);
                baseline.offer(val);
            }
        }
        int expectedCardinality = numToMerge * cardinality;
        long mergedEstimate = com.clearspring.analytics.stream.cardinality.LinearCounting.mergeEstimators(lcs).cardinality();
        double error = (java.lang.Math.abs((mergedEstimate - expectedCardinality))) / ((double) (expectedCardinality));
        org.junit.Assert.assertEquals(0.01, error, 0.01);
        com.clearspring.analytics.stream.cardinality.LinearCounting lc = lcs[0];
        lcs = java.util.Arrays.asList(lcs).subList(1, lcs.length).toArray(new com.clearspring.analytics.stream.cardinality.LinearCounting[0]);
        mergedEstimate = lc.merge(lcs).cardinality();
        error = (java.lang.Math.abs((mergedEstimate - expectedCardinality))) / ((double) (expectedCardinality));
        org.junit.Assert.assertEquals(0.01, error, 0.01);
        long baselineEstimate = baseline.cardinality();
        // StatementAdderMethod cloned existing statement
        baseline.mapAsBitString();
        org.junit.Assert.assertEquals(baselineEstimate, mergedEstimate);
    }

    /* amplification of com.clearspring.analytics.stream.cardinality.TestLinearCounting#testMerge */
    @org.junit.Test
    public void testMerge_literalMutation5753_literalMutation6829() throws com.clearspring.analytics.stream.cardinality.LinearCounting.LinearCountingMergeException {
        int numToMerge = 5;
        int size = 131072;
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(size, 131072);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(size, 131072);
        int cardinality = 1000;
        com.clearspring.analytics.stream.cardinality.LinearCounting[] lcs = new com.clearspring.analytics.stream.cardinality.LinearCounting[numToMerge];
        com.clearspring.analytics.stream.cardinality.LinearCounting baseline = new com.clearspring.analytics.stream.cardinality.LinearCounting(size);
        for (int i = 0; i < numToMerge; i++) {
            lcs[i] = new com.clearspring.analytics.stream.cardinality.LinearCounting(size);
            for (int j = 0; j < cardinality; j++) {
                double val = java.lang.Math.random();
                lcs[i].offer(val);
                baseline.offer(val);
            }
        }
        int expectedCardinality = numToMerge * cardinality;
        long mergedEstimate = com.clearspring.analytics.stream.cardinality.LinearCounting.mergeEstimators(lcs).cardinality();
        double error = (java.lang.Math.abs((mergedEstimate - expectedCardinality))) / ((double) (expectedCardinality));
        org.junit.Assert.assertEquals(0.01, error, 0.01);
        com.clearspring.analytics.stream.cardinality.LinearCounting lc = lcs[0];
        lcs = java.util.Arrays.asList(lcs).subList(1, lcs.length).toArray(new com.clearspring.analytics.stream.cardinality.LinearCounting[// TestDataMutator on numbers
        1]);
        mergedEstimate = lc.merge(lcs).cardinality();
        error = (java.lang.Math.abs((mergedEstimate - expectedCardinality))) / ((double) (expectedCardinality));
        org.junit.Assert.assertEquals(0.01, error, 0.01);
        long baselineEstimate = baseline.cardinality();
        org.junit.Assert.assertEquals(baselineEstimate, mergedEstimate);
    }

    /* amplification of com.clearspring.analytics.stream.cardinality.TestLinearCounting#testMerge */
    @org.junit.Test(timeout = 1000)
    public void testMerge_literalMutation5746_add5890_cf13154() throws com.clearspring.analytics.stream.cardinality.LinearCounting.LinearCountingMergeException {
        int numToMerge = 4;
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(numToMerge, 4);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(numToMerge, 4);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(numToMerge, 4);
        int size = 65536;
        int cardinality = 1000;
        com.clearspring.analytics.stream.cardinality.LinearCounting[] lcs = new com.clearspring.analytics.stream.cardinality.LinearCounting[numToMerge];
        com.clearspring.analytics.stream.cardinality.LinearCounting baseline = new com.clearspring.analytics.stream.cardinality.LinearCounting(size);
        for (int i = 0; i < numToMerge; i++) {
            lcs[i] = new com.clearspring.analytics.stream.cardinality.LinearCounting(size);
            for (int j = 0; j < cardinality; j++) {
                double val = java.lang.Math.random();
                lcs[i].offer(val);
                // MethodCallAdder
                baseline.offer(val);
                baseline.offer(val);
            }
        }
        int expectedCardinality = numToMerge * cardinality;
        long mergedEstimate = com.clearspring.analytics.stream.cardinality.LinearCounting.mergeEstimators(lcs).cardinality();
        double error = (java.lang.Math.abs((mergedEstimate - expectedCardinality))) / ((double) (expectedCardinality));
        org.junit.Assert.assertEquals(0.01, error, 0.01);
        com.clearspring.analytics.stream.cardinality.LinearCounting lc = lcs[0];
        lcs = java.util.Arrays.asList(lcs).subList(1, lcs.length).toArray(new com.clearspring.analytics.stream.cardinality.LinearCounting[0]);
        mergedEstimate = lc.merge(lcs).cardinality();
        error = (java.lang.Math.abs((mergedEstimate - expectedCardinality))) / ((double) (expectedCardinality));
        org.junit.Assert.assertEquals(0.01, error, 0.01);
        long baselineEstimate = baseline.cardinality();
        // StatementAdderMethod cloned existing statement
        baseline.getUtilization();
        org.junit.Assert.assertEquals(baselineEstimate, mergedEstimate);
    }

    /* amplification of com.clearspring.analytics.stream.cardinality.TestLinearCounting#testMerge */
    @org.junit.Test(timeout = 1000)
    public void testMerge_cf5854_cf10286_failAssert43_literalMutation18310() throws com.clearspring.analytics.stream.cardinality.LinearCounting.LinearCountingMergeException {
        // AssertGenerator generate try/catch block with fail statement
        try {
            int numToMerge = 2;
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(numToMerge, 2);
            int size = 65536;
            int cardinality = 1000;
            com.clearspring.analytics.stream.cardinality.LinearCounting[] lcs = new com.clearspring.analytics.stream.cardinality.LinearCounting[numToMerge];
            com.clearspring.analytics.stream.cardinality.LinearCounting baseline = new com.clearspring.analytics.stream.cardinality.LinearCounting(size);
            for (int i = 0; i < numToMerge; i++) {
                lcs[i] = new com.clearspring.analytics.stream.cardinality.LinearCounting(size);
                for (int j = 0; j < cardinality; j++) {
                    double val = java.lang.Math.random();
                    lcs[i].offer(val);
                    baseline.offer(val);
                }
            }
            int expectedCardinality = numToMerge * cardinality;
            long mergedEstimate = com.clearspring.analytics.stream.cardinality.LinearCounting.mergeEstimators(lcs).cardinality();
            double error = (java.lang.Math.abs((mergedEstimate - expectedCardinality))) / ((double) (expectedCardinality));
            com.clearspring.analytics.stream.cardinality.LinearCounting lc = lcs[0];
            lcs = java.util.Arrays.asList(lcs).subList(1, lcs.length).toArray(new com.clearspring.analytics.stream.cardinality.LinearCounting[0]);
            mergedEstimate = lc.merge(lcs).cardinality();
            error = (java.lang.Math.abs((mergedEstimate - expectedCardinality))) / ((double) (expectedCardinality));
            long baselineEstimate = baseline.cardinality();
            // StatementAdderOnAssert create null value
            com.clearspring.analytics.stream.cardinality.LinearCounting vc_1344 = (com.clearspring.analytics.stream.cardinality.LinearCounting)null;
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(vc_1344);
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(vc_1344);
            // AssertGenerator replace invocation
            com.clearspring.analytics.stream.cardinality.LinearCounting o_testMerge_cf5854__43 = // StatementAdderMethod cloned existing statement
vc_1344.mergeEstimators(lcs);
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((com.clearspring.analytics.stream.cardinality.LinearCounting)o_testMerge_cf5854__43).isSaturated());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((com.clearspring.analytics.stream.cardinality.LinearCounting)o_testMerge_cf5854__43).sizeof(), 65536);
            // StatementAdderMethod cloned existing statement
            vc_1344.sizeof();
            org.junit.Assert.fail("testMerge_cf5854_cf10286 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    /* amplification of com.clearspring.analytics.stream.cardinality.TestLinearCounting#testMerge */
    @org.junit.Test(timeout = 1000)
    public void testMerge_literalMutation5757_cf7253_cf16384() throws com.clearspring.analytics.stream.cardinality.LinearCounting.LinearCountingMergeException {
        int numToMerge = 5;
        int size = 65536;
        int cardinality = 1001;
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(cardinality, 1001);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(cardinality, 1001);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(cardinality, 1001);
        com.clearspring.analytics.stream.cardinality.LinearCounting[] lcs = new com.clearspring.analytics.stream.cardinality.LinearCounting[numToMerge];
        com.clearspring.analytics.stream.cardinality.LinearCounting baseline = new com.clearspring.analytics.stream.cardinality.LinearCounting(size);
        for (int i = 0; i < numToMerge; i++) {
            lcs[i] = new com.clearspring.analytics.stream.cardinality.LinearCounting(size);
            for (int j = 0; j < cardinality; j++) {
                double val = java.lang.Math.random();
                lcs[i].offer(val);
                baseline.offer(val);
            }
        }
        int expectedCardinality = numToMerge * cardinality;
        long mergedEstimate = com.clearspring.analytics.stream.cardinality.LinearCounting.mergeEstimators(lcs).cardinality();
        double error = (java.lang.Math.abs((mergedEstimate - expectedCardinality))) / ((double) (expectedCardinality));
        org.junit.Assert.assertEquals(0.01, error, 0.01);
        com.clearspring.analytics.stream.cardinality.LinearCounting lc = lcs[0];
        lcs = java.util.Arrays.asList(lcs).subList(1, lcs.length).toArray(new com.clearspring.analytics.stream.cardinality.LinearCounting[0]);
        mergedEstimate = lc.merge(lcs).cardinality();
        error = (java.lang.Math.abs((mergedEstimate - expectedCardinality))) / ((double) (expectedCardinality));
        org.junit.Assert.assertEquals(0.01, error, 0.01);
        long baselineEstimate = baseline.cardinality();
        // StatementAdderOnAssert create null value
        com.clearspring.analytics.stream.cardinality.LinearCounting[] vc_1652 = (com.clearspring.analytics.stream.cardinality.LinearCounting[])null;
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(vc_1652);
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(vc_1652);
        // StatementAdderOnAssert create null value
        com.clearspring.analytics.stream.cardinality.LinearCounting vc_1650 = (com.clearspring.analytics.stream.cardinality.LinearCounting)null;
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(vc_1650);
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(vc_1650);
        // AssertGenerator replace invocation
        com.clearspring.analytics.stream.cardinality.LinearCounting o_testMerge_literalMutation5757_cf7253__48 = // StatementAdderMethod cloned existing statement
vc_1650.mergeEstimators(vc_1652);
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(o_testMerge_literalMutation5757_cf7253__48);
        // StatementAdderOnAssert create null value
        com.clearspring.analytics.stream.cardinality.ICardinality[] vc_3144 = (com.clearspring.analytics.stream.cardinality.ICardinality[])null;
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(vc_3144);
        // StatementAddOnAssert local variable replacement
        com.clearspring.analytics.stream.cardinality.LinearCounting lc2 = new com.clearspring.analytics.stream.cardinality.LinearCounting(lc.getBytes());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((com.clearspring.analytics.stream.cardinality.LinearCounting)lc2).sizeof(), 65536);
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((com.clearspring.analytics.stream.cardinality.LinearCounting)lc2).isSaturated());
        // AssertGenerator replace invocation
        com.clearspring.analytics.stream.cardinality.ICardinality o_testMerge_literalMutation5757_cf7253_cf16384__64 = // StatementAdderMethod cloned existing statement
lc2.merge(vc_3144);
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((com.clearspring.analytics.stream.cardinality.LinearCounting)o_testMerge_literalMutation5757_cf7253_cf16384__64).isSaturated());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((com.clearspring.analytics.stream.cardinality.LinearCounting)o_testMerge_literalMutation5757_cf7253_cf16384__64).sizeof(), 65536);
        org.junit.Assert.assertEquals(baselineEstimate, mergedEstimate);
    }

    /* amplification of com.clearspring.analytics.stream.cardinality.TestLinearCounting#testMerge */
    @org.junit.Test(timeout = 1000)
    public void testMerge_literalMutation5748_cf6174_cf14926_failAssert21() throws com.clearspring.analytics.stream.cardinality.LinearCounting.LinearCountingMergeException {
        // AssertGenerator generate try/catch block with fail statement
        try {
            int numToMerge = 2;
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(numToMerge, 2);
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(numToMerge, 2);
            int size = 65536;
            int cardinality = 1000;
            com.clearspring.analytics.stream.cardinality.LinearCounting[] lcs = new com.clearspring.analytics.stream.cardinality.LinearCounting[numToMerge];
            com.clearspring.analytics.stream.cardinality.LinearCounting baseline = new com.clearspring.analytics.stream.cardinality.LinearCounting(size);
            for (int i = 0; i < numToMerge; i++) {
                lcs[i] = new com.clearspring.analytics.stream.cardinality.LinearCounting(size);
                for (int j = 0; j < cardinality; j++) {
                    double val = java.lang.Math.random();
                    lcs[i].offer(val);
                    baseline.offer(val);
                }
            }
            int expectedCardinality = numToMerge * cardinality;
            long mergedEstimate = com.clearspring.analytics.stream.cardinality.LinearCounting.mergeEstimators(lcs).cardinality();
            double error = (java.lang.Math.abs((mergedEstimate - expectedCardinality))) / ((double) (expectedCardinality));
            com.clearspring.analytics.stream.cardinality.LinearCounting lc = lcs[0];
            lcs = java.util.Arrays.asList(lcs).subList(1, lcs.length).toArray(new com.clearspring.analytics.stream.cardinality.LinearCounting[0]);
            mergedEstimate = lc.merge(lcs).cardinality();
            error = (java.lang.Math.abs((mergedEstimate - expectedCardinality))) / ((double) (expectedCardinality));
            long baselineEstimate = baseline.cardinality();
            // StatementAdderOnAssert create null value
            com.clearspring.analytics.stream.cardinality.LinearCounting[] vc_1414 = (com.clearspring.analytics.stream.cardinality.LinearCounting[])null;
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(vc_1414);
            // StatementAdderOnAssert create null value
            com.clearspring.analytics.stream.cardinality.LinearCounting vc_1412 = (com.clearspring.analytics.stream.cardinality.LinearCounting)null;
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(vc_1412);
            // AssertGenerator replace invocation
            com.clearspring.analytics.stream.cardinality.LinearCounting o_testMerge_literalMutation5748_cf6174__48 = // StatementAdderMethod cloned existing statement
vc_1412.mergeEstimators(vc_1414);
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(o_testMerge_literalMutation5748_cf6174__48);
            // StatementAddOnAssert local variable replacement
            com.clearspring.analytics.stream.cardinality.LinearCounting lc2 = new com.clearspring.analytics.stream.cardinality.LinearCounting(lc.getBytes());
            // StatementAdderMethod cloned existing statement
            lc2.offerHashed(mergedEstimate);
            org.junit.Assert.fail("testMerge_literalMutation5748_cf6174_cf14926 should have thrown UnsupportedOperationException");
        } catch (java.lang.UnsupportedOperationException eee) {
        }
    }

    /* amplification of com.clearspring.analytics.stream.cardinality.TestLinearCounting#testMerge */
    @org.junit.Test(timeout = 1000)
    public void testMerge_cf5856_literalMutation10322_literalMutation21497() throws com.clearspring.analytics.stream.cardinality.LinearCounting.LinearCountingMergeException {
        int numToMerge = 2;
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(numToMerge, 2);
        int size = 65536;
        int cardinality = 1000;
        com.clearspring.analytics.stream.cardinality.LinearCounting[] lcs = new com.clearspring.analytics.stream.cardinality.LinearCounting[numToMerge];
        com.clearspring.analytics.stream.cardinality.LinearCounting baseline = new com.clearspring.analytics.stream.cardinality.LinearCounting(size);
        for (int i = 0; i < numToMerge; i++) {
            lcs[i] = new com.clearspring.analytics.stream.cardinality.LinearCounting(size);
            for (int j = 0; j < cardinality; j++) {
                double val = java.lang.Math.random();
                lcs[i].offer(val);
                baseline.offer(val);
            }
        }
        int expectedCardinality = numToMerge * cardinality;
        long mergedEstimate = com.clearspring.analytics.stream.cardinality.LinearCounting.mergeEstimators(lcs).cardinality();
        double error = (java.lang.Math.abs((mergedEstimate - expectedCardinality))) / ((double) (expectedCardinality));
        org.junit.Assert.assertEquals(0.005, error, 0.01);
        com.clearspring.analytics.stream.cardinality.LinearCounting lc = lcs[0];
        lcs = java.util.Arrays.asList(lcs).subList(1, lcs.length).toArray(new com.clearspring.analytics.stream.cardinality.LinearCounting[0]);
        mergedEstimate = lc.merge(lcs).cardinality();
        error = (java.lang.Math.abs((mergedEstimate - expectedCardinality))) / ((double) (expectedCardinality));
        org.junit.Assert.assertEquals(0.01, error, 0.01);
        long baselineEstimate = baseline.cardinality();
        // StatementAdderOnAssert create null value
        com.clearspring.analytics.stream.cardinality.LinearCounting[] vc_1346 = (com.clearspring.analytics.stream.cardinality.LinearCounting[])null;
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(vc_1346);
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(vc_1346);
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(vc_1346);
        // AssertGenerator replace invocation
        com.clearspring.analytics.stream.cardinality.LinearCounting o_testMerge_cf5856__43 = // StatementAdderMethod cloned existing statement
baseline.mergeEstimators(vc_1346);
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(o_testMerge_cf5856__43);
        org.junit.Assert.assertEquals(baselineEstimate, mergedEstimate);
    }

    /* amplification of com.clearspring.analytics.stream.cardinality.TestLinearCounting#testMerge */
    @org.junit.Test(timeout = 1000)
    public void testMerge_cf5798_literalMutation8395_cf17007_failAssert7() throws com.clearspring.analytics.stream.cardinality.LinearCounting.LinearCountingMergeException {
        // AssertGenerator generate try/catch block with fail statement
        try {
            int numToMerge = 5;
            int size = 65536;
            int cardinality = 500;
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(cardinality, 500);
            com.clearspring.analytics.stream.cardinality.LinearCounting[] lcs = new com.clearspring.analytics.stream.cardinality.LinearCounting[numToMerge];
            com.clearspring.analytics.stream.cardinality.LinearCounting baseline = new com.clearspring.analytics.stream.cardinality.LinearCounting(size);
            for (int i = 0; i < numToMerge; i++) {
                lcs[i] = new com.clearspring.analytics.stream.cardinality.LinearCounting(size);
                for (int j = 0; j < cardinality; j++) {
                    double val = java.lang.Math.random();
                    lcs[i].offer(val);
                    baseline.offer(val);
                }
            }
            int expectedCardinality = numToMerge * cardinality;
            long mergedEstimate = com.clearspring.analytics.stream.cardinality.LinearCounting.mergeEstimators(lcs).cardinality();
            double error = (java.lang.Math.abs((mergedEstimate - expectedCardinality))) / ((double) (expectedCardinality));
            com.clearspring.analytics.stream.cardinality.LinearCounting lc = lcs[0];
            lcs = java.util.Arrays.asList(lcs).subList(1, lcs.length).toArray(new com.clearspring.analytics.stream.cardinality.LinearCounting[0]);
            mergedEstimate = lc.merge(lcs).cardinality();
            error = (java.lang.Math.abs((mergedEstimate - expectedCardinality))) / ((double) (expectedCardinality));
            long baselineEstimate = baseline.cardinality();
            // AssertGenerator replace invocation
            boolean o_testMerge_cf5798__41 = // StatementAdderMethod cloned existing statement
lc.isSaturated();
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(o_testMerge_cf5798__41);
            // StatementAdderOnAssert create random local variable
            int vc_3272 = -638195819;
            // StatementAddOnAssert local variable replacement
            com.clearspring.analytics.stream.cardinality.LinearCounting lc2 = new com.clearspring.analytics.stream.cardinality.LinearCounting(lc.getBytes());
            // StatementAdderMethod cloned existing statement
            lc2.offerHashed(vc_3272);
            org.junit.Assert.fail("testMerge_cf5798_literalMutation8395_cf17007 should have thrown UnsupportedOperationException");
        } catch (java.lang.UnsupportedOperationException eee) {
        }
    }

    /* amplification of com.clearspring.analytics.stream.cardinality.TestLinearCounting#testMerge */
    @org.junit.Test(timeout = 1000)
    public void testMerge_literalMutation5758_cf7349_cf19941_failAssert50() throws com.clearspring.analytics.stream.cardinality.LinearCounting.LinearCountingMergeException {
        // AssertGenerator generate try/catch block with fail statement
        try {
            int numToMerge = 5;
            int size = 65536;
            int cardinality = 999;
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(cardinality, 999);
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(cardinality, 999);
            com.clearspring.analytics.stream.cardinality.LinearCounting[] lcs = new com.clearspring.analytics.stream.cardinality.LinearCounting[numToMerge];
            com.clearspring.analytics.stream.cardinality.LinearCounting baseline = new com.clearspring.analytics.stream.cardinality.LinearCounting(size);
            for (int i = 0; i < numToMerge; i++) {
                lcs[i] = new com.clearspring.analytics.stream.cardinality.LinearCounting(size);
                for (int j = 0; j < cardinality; j++) {
                    double val = java.lang.Math.random();
                    lcs[i].offer(val);
                    baseline.offer(val);
                }
            }
            int expectedCardinality = numToMerge * cardinality;
            long mergedEstimate = com.clearspring.analytics.stream.cardinality.LinearCounting.mergeEstimators(lcs).cardinality();
            double error = (java.lang.Math.abs((mergedEstimate - expectedCardinality))) / ((double) (expectedCardinality));
            com.clearspring.analytics.stream.cardinality.LinearCounting lc = lcs[0];
            lcs = java.util.Arrays.asList(lcs).subList(1, lcs.length).toArray(new com.clearspring.analytics.stream.cardinality.LinearCounting[0]);
            mergedEstimate = lc.merge(lcs).cardinality();
            error = (java.lang.Math.abs((mergedEstimate - expectedCardinality))) / ((double) (expectedCardinality));
            long baselineEstimate = baseline.cardinality();
            // StatementAdderOnAssert create random local variable
            java.lang.Object vc_1671 = new java.lang.Object();
            // AssertGenerator replace invocation
            boolean o_testMerge_literalMutation5758_cf7349__46 = // StatementAdderMethod cloned existing statement
baseline.offer(vc_1671);
            // AssertGenerator add assertion
            org.junit.Assert.assertTrue(o_testMerge_literalMutation5758_cf7349__46);
            // StatementAdderOnAssert create null value
            com.clearspring.analytics.stream.cardinality.LinearCounting vc_3742 = (com.clearspring.analytics.stream.cardinality.LinearCounting)null;
            // StatementAdderMethod cloned existing statement
            vc_3742.offer(vc_1671);
            org.junit.Assert.fail("testMerge_literalMutation5758_cf7349_cf19941 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    /* amplification of com.clearspring.analytics.stream.cardinality.TestLinearCounting#testMerge */
    @org.junit.Test(timeout = 1000)
    public void testMerge_cf5879_literalMutation11484_failAssert50_literalMutation19187() throws com.clearspring.analytics.stream.cardinality.LinearCounting.LinearCountingMergeException {
        // AssertGenerator generate try/catch block with fail statement
        try {
            int numToMerge = 0;
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(numToMerge, 0);
            int size = 65536;
            int cardinality = 1000;
            com.clearspring.analytics.stream.cardinality.LinearCounting[] lcs = new com.clearspring.analytics.stream.cardinality.LinearCounting[numToMerge];
            com.clearspring.analytics.stream.cardinality.LinearCounting baseline = new com.clearspring.analytics.stream.cardinality.LinearCounting(size);
            for (int i = 0; i < numToMerge; i++) {
                lcs[i] = new com.clearspring.analytics.stream.cardinality.LinearCounting(size);
                for (int j = 0; j < cardinality; j++) {
                    double val = java.lang.Math.random();
                    lcs[i].offer(val);
                    baseline.offer(val);
                }
            }
            int expectedCardinality = numToMerge * cardinality;
            long mergedEstimate = com.clearspring.analytics.stream.cardinality.LinearCounting.mergeEstimators(lcs).cardinality();
            double error = (java.lang.Math.abs((mergedEstimate - expectedCardinality))) / ((double) (expectedCardinality));
            com.clearspring.analytics.stream.cardinality.LinearCounting lc = lcs[0];
            lcs = java.util.Arrays.asList(lcs).subList(1, lcs.length).toArray(new com.clearspring.analytics.stream.cardinality.LinearCounting[// TestDataMutator on numbers
            2]);
            mergedEstimate = lc.merge(lcs).cardinality();
            error = (java.lang.Math.abs((mergedEstimate - expectedCardinality))) / ((double) (expectedCardinality));
            long baselineEstimate = baseline.cardinality();
            // StatementAddOnAssert local variable replacement
            com.clearspring.analytics.stream.cardinality.LinearCounting lc2 = new com.clearspring.analytics.stream.cardinality.LinearCounting(lc.getBytes());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((com.clearspring.analytics.stream.cardinality.LinearCounting)lc2).sizeof(), 65536);
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((com.clearspring.analytics.stream.cardinality.LinearCounting)lc2).isSaturated());
            // AssertGenerator replace invocation
            int o_testMerge_cf5879__45 = // StatementAdderMethod cloned existing statement
lc2.sizeof();
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(o_testMerge_cf5879__45, 65536);
            org.junit.Assert.fail("testMerge_cf5879_literalMutation11484 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    /* amplification of com.clearspring.analytics.stream.cardinality.TestLinearCounting#testSaturation */
    @org.junit.Test(timeout = 1000)
    public void testSaturation_cf21695() {
        com.clearspring.analytics.stream.cardinality.LinearCounting lc = new com.clearspring.analytics.stream.cardinality.LinearCounting(1);
        for (int i = 0; i < 27; i++) {
            lc.offer(i);
        }
        org.junit.Assert.assertTrue(lc.isSaturated());
        org.junit.Assert.assertEquals(0, lc.getCount());
        // StatementAdderOnAssert create null value
        java.lang.Object vc_3982 = (java.lang.Object)null;
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(vc_3982);
        // AssertGenerator replace invocation
        boolean o_testSaturation_cf21695__14 = // StatementAdderMethod cloned existing statement
lc.offer(vc_3982);
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_testSaturation_cf21695__14);
        org.junit.Assert.assertEquals(java.lang.Long.MAX_VALUE, lc.cardinality());
    }

    /* amplification of com.clearspring.analytics.stream.cardinality.TestLinearCounting#testSaturation */
    @org.junit.Test(timeout = 1000)
    public void testSaturation_cf21750() {
        com.clearspring.analytics.stream.cardinality.LinearCounting lc = new com.clearspring.analytics.stream.cardinality.LinearCounting(1);
        for (int i = 0; i < 27; i++) {
            lc.offer(i);
        }
        org.junit.Assert.assertTrue(lc.isSaturated());
        org.junit.Assert.assertEquals(0, lc.getCount());
        // AssertGenerator replace invocation
        int o_testSaturation_cf21750__12 = // StatementAdderMethod cloned existing statement
lc.sizeof();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(o_testSaturation_cf21750__12, 1);
        org.junit.Assert.assertEquals(java.lang.Long.MAX_VALUE, lc.cardinality());
    }

    /* amplification of com.clearspring.analytics.stream.cardinality.TestLinearCounting#testSaturation */
    @org.junit.Test(timeout = 1000)
    public void testSaturation_cf21696() {
        com.clearspring.analytics.stream.cardinality.LinearCounting lc = new com.clearspring.analytics.stream.cardinality.LinearCounting(1);
        for (int i = 0; i < 27; i++) {
            lc.offer(i);
        }
        org.junit.Assert.assertTrue(lc.isSaturated());
        org.junit.Assert.assertEquals(0, lc.getCount());
        // StatementAdderOnAssert create random local variable
        java.lang.Object vc_3983 = new java.lang.Object();
        // AssertGenerator replace invocation
        boolean o_testSaturation_cf21696__14 = // StatementAdderMethod cloned existing statement
lc.offer(vc_3983);
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_testSaturation_cf21696__14);
        org.junit.Assert.assertEquals(java.lang.Long.MAX_VALUE, lc.cardinality());
    }

    /* amplification of com.clearspring.analytics.stream.cardinality.TestLinearCounting#testSaturation */
    @org.junit.Test(timeout = 1000)
    public void testSaturation_cf21712_failAssert18() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            com.clearspring.analytics.stream.cardinality.LinearCounting lc = new com.clearspring.analytics.stream.cardinality.LinearCounting(1);
            for (int i = 0; i < 27; i++) {
                lc.offer(i);
            }
            // MethodAssertGenerator build local variable
            Object o_8_0 = lc.isSaturated();
            // MethodAssertGenerator build local variable
            Object o_10_0 = lc.getCount();
            // StatementAdderOnAssert create random local variable
            long vc_3989 = 551613129607496144L;
            // StatementAdderMethod cloned existing statement
            lc.offerHashed(vc_3989);
            // MethodAssertGenerator build local variable
            Object o_16_0 = lc.cardinality();
            org.junit.Assert.fail("testSaturation_cf21712 should have thrown UnsupportedOperationException");
        } catch (java.lang.UnsupportedOperationException eee) {
        }
    }

    /* amplification of com.clearspring.analytics.stream.cardinality.TestLinearCounting#testSaturation */
    @org.junit.Test(timeout = 1000)
    public void testSaturation_cf21706_failAssert16() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            com.clearspring.analytics.stream.cardinality.LinearCounting lc = new com.clearspring.analytics.stream.cardinality.LinearCounting(1);
            for (int i = 0; i < 27; i++) {
                lc.offer(i);
            }
            // MethodAssertGenerator build local variable
            Object o_8_0 = lc.isSaturated();
            // MethodAssertGenerator build local variable
            Object o_10_0 = lc.getCount();
            // StatementAdderOnAssert create random local variable
            int vc_3986 = -1379777117;
            // StatementAddOnAssert local variable replacement
            com.clearspring.analytics.stream.cardinality.LinearCounting lc2 = new com.clearspring.analytics.stream.cardinality.LinearCounting(lc.getBytes());
            // StatementAdderMethod cloned existing statement
            lc2.offerHashed(vc_3986);
            // MethodAssertGenerator build local variable
            Object o_20_0 = lc.cardinality();
            org.junit.Assert.fail("testSaturation_cf21706 should have thrown UnsupportedOperationException");
        } catch (java.lang.UnsupportedOperationException eee) {
        }
    }

    /* amplification of com.clearspring.analytics.stream.cardinality.TestLinearCounting#testSaturation */
    @org.junit.Test(timeout = 1000)
    public void testSaturation_cf21754() {
        com.clearspring.analytics.stream.cardinality.LinearCounting lc = new com.clearspring.analytics.stream.cardinality.LinearCounting(1);
        for (int i = 0; i < 27; i++) {
            lc.offer(i);
        }
        org.junit.Assert.assertTrue(lc.isSaturated());
        org.junit.Assert.assertEquals(0, lc.getCount());
        // AssertGenerator replace invocation
        java.lang.String o_testSaturation_cf21754__12 = // StatementAdderMethod cloned existing statement
lc.mapAsBitString();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(o_testSaturation_cf21754__12, "11111111111111111111111111111111");
        org.junit.Assert.assertEquals(java.lang.Long.MAX_VALUE, lc.cardinality());
    }

    /* amplification of com.clearspring.analytics.stream.cardinality.TestLinearCounting#testSaturation */
    @org.junit.Test(timeout = 1000)
    public void testSaturation_cf21695_cf22048_failAssert2() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            com.clearspring.analytics.stream.cardinality.LinearCounting lc = new com.clearspring.analytics.stream.cardinality.LinearCounting(1);
            for (int i = 0; i < 27; i++) {
                lc.offer(i);
            }
            // MethodAssertGenerator build local variable
            Object o_8_0 = lc.isSaturated();
            // MethodAssertGenerator build local variable
            Object o_10_0 = lc.getCount();
            // StatementAdderOnAssert create null value
            java.lang.Object vc_3982 = (java.lang.Object)null;
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(vc_3982);
            // AssertGenerator replace invocation
            boolean o_testSaturation_cf21695__14 = // StatementAdderMethod cloned existing statement
lc.offer(vc_3982);
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(o_testSaturation_cf21695__14);
            // StatementAdderOnAssert create random local variable
            long vc_4057 = -2594317594056987630L;
            // StatementAddOnAssert local variable replacement
            com.clearspring.analytics.stream.cardinality.LinearCounting lc2 = new com.clearspring.analytics.stream.cardinality.LinearCounting(lc.getBytes());
            // StatementAdderMethod cloned existing statement
            lc2.offerHashed(vc_4057);
            // MethodAssertGenerator build local variable
            Object o_28_0 = lc.cardinality();
            org.junit.Assert.fail("testSaturation_cf21695_cf22048 should have thrown UnsupportedOperationException");
        } catch (java.lang.UnsupportedOperationException eee) {
        }
    }

    /* amplification of com.clearspring.analytics.stream.cardinality.TestLinearCounting#testSaturation */
    @org.junit.Test(timeout = 1000)
    public void testSaturation_cf21754_literalMutation22802_failAssert28() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            com.clearspring.analytics.stream.cardinality.LinearCounting lc = new com.clearspring.analytics.stream.cardinality.LinearCounting(1);
            for (int i = 0; i < 0; i++) {
                lc.offer(i);
            }
            // MethodAssertGenerator build local variable
            Object o_9_0 = lc.isSaturated();
            // MethodAssertGenerator build local variable
            Object o_11_0 = lc.getCount();
            // AssertGenerator replace invocation
            java.lang.String o_testSaturation_cf21754__12 = // StatementAdderMethod cloned existing statement
lc.mapAsBitString();
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(o_testSaturation_cf21754__12, "11111111111111111111111111111111");
            // MethodAssertGenerator build local variable
            Object o_17_0 = lc.cardinality();
            org.junit.Assert.fail("testSaturation_cf21754_literalMutation22802 should have thrown Throwable");
        } catch (java.lang.Throwable eee) {
        }
    }

    /* amplification of com.clearspring.analytics.stream.cardinality.TestLinearCounting#testSaturation */
    @org.junit.Test(timeout = 1000)
    public void testSaturation_cf21754_cf22825_failAssert34() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            com.clearspring.analytics.stream.cardinality.LinearCounting lc = new com.clearspring.analytics.stream.cardinality.LinearCounting(1);
            for (int i = 0; i < 27; i++) {
                lc.offer(i);
            }
            // MethodAssertGenerator build local variable
            Object o_8_0 = lc.isSaturated();
            // MethodAssertGenerator build local variable
            Object o_10_0 = lc.getCount();
            // AssertGenerator replace invocation
            java.lang.String o_testSaturation_cf21754__12 = // StatementAdderMethod cloned existing statement
lc.mapAsBitString();
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(o_testSaturation_cf21754__12, "11111111111111111111111111111111");
            // StatementAdderOnAssert create literal from method
            int int_vc_125 = 1000;
            // StatementAdderMethod cloned existing statement
            lc.offerHashed(int_vc_125);
            // MethodAssertGenerator build local variable
            Object o_20_0 = lc.cardinality();
            org.junit.Assert.fail("testSaturation_cf21754_cf22825 should have thrown UnsupportedOperationException");
        } catch (java.lang.UnsupportedOperationException eee) {
        }
    }

    /* amplification of com.clearspring.analytics.stream.cardinality.TestLinearCounting#testSaturation */
    @org.junit.Test(timeout = 1000)
    public void testSaturation_cf21690_cf21959_cf24522_failAssert24() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            com.clearspring.analytics.stream.cardinality.LinearCounting lc = new com.clearspring.analytics.stream.cardinality.LinearCounting(1);
            for (int i = 0; i < 27; i++) {
                lc.offer(i);
            }
            // MethodAssertGenerator build local variable
            Object o_8_0 = lc.isSaturated();
            // MethodAssertGenerator build local variable
            Object o_10_0 = lc.getCount();
            // AssertGenerator replace invocation
            boolean o_testSaturation_cf21690__12 = // StatementAdderMethod cloned existing statement
lc.isSaturated();
            // AssertGenerator add assertion
            org.junit.Assert.assertTrue(o_testSaturation_cf21690__12);
            // AssertGenerator replace invocation
            int o_testSaturation_cf21690_cf21959__16 = // StatementAdderMethod cloned existing statement
lc.sizeof();
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(o_testSaturation_cf21690_cf21959__16, 1);
            // StatementAdderOnAssert create random local variable
            long vc_4669 = 4474956907352272035L;
            // StatementAdderOnAssert create null value
            com.clearspring.analytics.stream.cardinality.LinearCounting vc_4667 = (com.clearspring.analytics.stream.cardinality.LinearCounting)null;
            // StatementAdderMethod cloned existing statement
            vc_4667.offerHashed(vc_4669);
            // MethodAssertGenerator build local variable
            Object o_26_0 = lc.cardinality();
            org.junit.Assert.fail("testSaturation_cf21690_cf21959_cf24522 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    /* amplification of com.clearspring.analytics.stream.cardinality.TestLinearCounting#testSaturation */
    @org.junit.Test(timeout = 1000)
    public void testSaturation_cf21695_add21998_cf23780_failAssert11() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            com.clearspring.analytics.stream.cardinality.LinearCounting lc = new com.clearspring.analytics.stream.cardinality.LinearCounting(1);
            for (int i = 0; i < 27; i++) {
                // MethodCallAdder
                lc.offer(i);
                lc.offer(i);
            }
            // MethodAssertGenerator build local variable
            Object o_10_0 = lc.isSaturated();
            // MethodAssertGenerator build local variable
            Object o_12_0 = lc.getCount();
            // StatementAdderOnAssert create null value
            java.lang.Object vc_3982 = (java.lang.Object)null;
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(vc_3982);
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(vc_3982);
            // AssertGenerator replace invocation
            boolean o_testSaturation_cf21695__14 = // StatementAdderMethod cloned existing statement
lc.offer(vc_3982);
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(o_testSaturation_cf21695__14);
            // StatementAdderOnAssert create random local variable
            int vc_4496 = 1409713819;
            // StatementAddOnAssert local variable replacement
            com.clearspring.analytics.stream.cardinality.LinearCounting lc2 = new com.clearspring.analytics.stream.cardinality.LinearCounting(lc.getBytes());
            // StatementAdderMethod cloned existing statement
            lc2.offerHashed(vc_4496);
            // MethodAssertGenerator build local variable
            Object o_32_0 = lc.cardinality();
            org.junit.Assert.fail("testSaturation_cf21695_add21998_cf23780 should have thrown UnsupportedOperationException");
        } catch (java.lang.UnsupportedOperationException eee) {
        }
    }

    /* amplification of com.clearspring.analytics.stream.cardinality.TestLinearCounting#testSaturation */
    @org.junit.Test(timeout = 1000)
    public void testSaturation_cf21754_cf22818_cf24196_failAssert29() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            com.clearspring.analytics.stream.cardinality.LinearCounting lc = new com.clearspring.analytics.stream.cardinality.LinearCounting(1);
            for (int i = 0; i < 27; i++) {
                lc.offer(i);
            }
            // MethodAssertGenerator build local variable
            Object o_8_0 = lc.isSaturated();
            // MethodAssertGenerator build local variable
            Object o_10_0 = lc.getCount();
            // AssertGenerator replace invocation
            java.lang.String o_testSaturation_cf21754__12 = // StatementAdderMethod cloned existing statement
lc.mapAsBitString();
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(o_testSaturation_cf21754__12, "11111111111111111111111111111111");
            // StatementAdderOnAssert create random local variable
            java.lang.Object vc_4255 = new java.lang.Object();
            // AssertGenerator replace invocation
            boolean o_testSaturation_cf21754_cf22818__18 = // StatementAdderMethod cloned existing statement
lc.offer(vc_4255);
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(o_testSaturation_cf21754_cf22818__18);
            // StatementAdderOnAssert create random local variable
            long vc_4601 = -7349470929204233316L;
            // StatementAdderMethod cloned existing statement
            lc.offerHashed(vc_4601);
            // MethodAssertGenerator build local variable
            Object o_26_0 = lc.cardinality();
            org.junit.Assert.fail("testSaturation_cf21754_cf22818_cf24196 should have thrown UnsupportedOperationException");
        } catch (java.lang.UnsupportedOperationException eee) {
        }
    }

    /* amplification of com.clearspring.analytics.stream.cardinality.TestLinearCounting#testSaturation */
    @org.junit.Test(timeout = 1000)
    public void testSaturation_cf21746_cf22670_cf23668_failAssert18() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            com.clearspring.analytics.stream.cardinality.LinearCounting lc = new com.clearspring.analytics.stream.cardinality.LinearCounting(1);
            for (int i = 0; i < 27; i++) {
                lc.offer(i);
            }
            // MethodAssertGenerator build local variable
            Object o_8_0 = lc.isSaturated();
            // MethodAssertGenerator build local variable
            Object o_10_0 = lc.getCount();
            // AssertGenerator replace invocation
            int o_testSaturation_cf21746__12 = // StatementAdderMethod cloned existing statement
lc.getCount();
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(o_testSaturation_cf21746__12, 0);
            // AssertGenerator replace invocation
            java.lang.String o_testSaturation_cf21746_cf22670__16 = // StatementAdderMethod cloned existing statement
lc.mapAsBitString();
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(o_testSaturation_cf21746_cf22670__16, "11111111111111111111111111111111");
            // StatementAdderOnAssert create null value
            com.clearspring.analytics.stream.cardinality.LinearCounting vc_4484 = (com.clearspring.analytics.stream.cardinality.LinearCounting)null;
            // StatementAdderMethod cloned existing statement
            vc_4484.mapAsBitString();
            // MethodAssertGenerator build local variable
            Object o_24_0 = lc.cardinality();
            org.junit.Assert.fail("testSaturation_cf21746_cf22670_cf23668 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    /* amplification of com.clearspring.analytics.stream.cardinality.TestLinearCounting#testSaturation */
    @org.junit.Test(timeout = 1000)
    public void testSaturation_cf21695_cf22024_add23950() {
        com.clearspring.analytics.stream.cardinality.LinearCounting lc = new com.clearspring.analytics.stream.cardinality.LinearCounting(1);
        for (int i = 0; i < 27; i++) {
            // MethodCallAdder
            lc.offer(i);
            lc.offer(i);
        }
        org.junit.Assert.assertTrue(lc.isSaturated());
        org.junit.Assert.assertEquals(0, lc.getCount());
        // StatementAdderOnAssert create null value
        java.lang.Object vc_3982 = (java.lang.Object)null;
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(vc_3982);
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(vc_3982);
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(vc_3982);
        // AssertGenerator replace invocation
        boolean o_testSaturation_cf21695__14 = // StatementAdderMethod cloned existing statement
lc.offer(vc_3982);
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_testSaturation_cf21695__14);
        // StatementAdderOnAssert create random local variable
        java.lang.Object vc_4051 = new java.lang.Object();
        // AssertGenerator replace invocation
        boolean o_testSaturation_cf21695_cf22024__22 = // StatementAdderMethod cloned existing statement
lc.offer(vc_4051);
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_testSaturation_cf21695_cf22024__22);
        org.junit.Assert.assertEquals(java.lang.Long.MAX_VALUE, lc.cardinality());
    }

    /* amplification of com.clearspring.analytics.stream.cardinality.TestLinearCounting#testSerialization */
    @org.junit.Test(timeout = 1000)
    public void testSerialization_cf24809() {
        com.clearspring.analytics.stream.cardinality.LinearCounting lc = new com.clearspring.analytics.stream.cardinality.LinearCounting(4);
        lc.offer("a");
        lc.offer("b");
        lc.offer("c");
        lc.offer("d");
        lc.offer("e");
        com.clearspring.analytics.stream.cardinality.LinearCounting lc2 = new com.clearspring.analytics.stream.cardinality.LinearCounting(lc.getBytes());
        org.junit.Assert.assertArrayEquals(lc.map, lc2.map);
        org.junit.Assert.assertEquals(lc.count, lc2.count);
        // AssertGenerator replace invocation
        int o_testSerialization_cf24809__13 = // StatementAdderMethod cloned existing statement
lc.sizeof();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(o_testSerialization_cf24809__13, 4);
        org.junit.Assert.assertEquals(lc.length, lc2.length);
    }

    /* amplification of com.clearspring.analytics.stream.cardinality.TestLinearCounting#testSerialization */
    @org.junit.Test(timeout = 1000)
    public void testSerialization_add24755() {
        com.clearspring.analytics.stream.cardinality.LinearCounting lc = new com.clearspring.analytics.stream.cardinality.LinearCounting(4);
        lc.offer("a");
        lc.offer("b");
        // AssertGenerator replace invocation
        boolean o_testSerialization_add24755__5 = // MethodCallAdder
lc.offer("c");
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_testSerialization_add24755__5);
        lc.offer("c");
        lc.offer("d");
        lc.offer("e");
        com.clearspring.analytics.stream.cardinality.LinearCounting lc2 = new com.clearspring.analytics.stream.cardinality.LinearCounting(lc.getBytes());
        org.junit.Assert.assertArrayEquals(lc.map, lc2.map);
        org.junit.Assert.assertEquals(lc.count, lc2.count);
        org.junit.Assert.assertEquals(lc.length, lc2.length);
    }

    /* amplification of com.clearspring.analytics.stream.cardinality.TestLinearCounting#testSerialization */
    @org.junit.Test(timeout = 1000)
    public void testSerialization_cf24803() {
        com.clearspring.analytics.stream.cardinality.LinearCounting lc = new com.clearspring.analytics.stream.cardinality.LinearCounting(4);
        lc.offer("a");
        lc.offer("b");
        lc.offer("c");
        lc.offer("d");
        lc.offer("e");
        com.clearspring.analytics.stream.cardinality.LinearCounting lc2 = new com.clearspring.analytics.stream.cardinality.LinearCounting(lc.getBytes());
        org.junit.Assert.assertArrayEquals(lc.map, lc2.map);
        org.junit.Assert.assertEquals(lc.count, lc2.count);
        // AssertGenerator replace invocation
        int o_testSerialization_cf24803__13 = // StatementAdderMethod cloned existing statement
lc.computeCount();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(o_testSerialization_cf24803__13, 27);
        org.junit.Assert.assertEquals(lc.length, lc2.length);
    }

    /* amplification of com.clearspring.analytics.stream.cardinality.TestLinearCounting#testSerialization */
    @org.junit.Test(timeout = 1000)
    public void testSerialization_cf24775_failAssert7() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            com.clearspring.analytics.stream.cardinality.LinearCounting lc = new com.clearspring.analytics.stream.cardinality.LinearCounting(4);
            lc.offer("a");
            lc.offer("b");
            lc.offer("c");
            lc.offer("d");
            lc.offer("e");
            com.clearspring.analytics.stream.cardinality.LinearCounting lc2 = new com.clearspring.analytics.stream.cardinality.LinearCounting(lc.getBytes());
            org.junit.Assert.assertArrayEquals(lc.map, lc2.map);
            // StatementAdderOnAssert create random local variable
            int vc_4734 = 1610469563;
            // StatementAdderMethod cloned existing statement
            lc2.offerHashed(vc_4734);
            org.junit.Assert.fail("testSerialization_cf24775 should have thrown UnsupportedOperationException");
        } catch (java.lang.UnsupportedOperationException eee) {
        }
    }

    /* amplification of com.clearspring.analytics.stream.cardinality.TestLinearCounting#testSerialization */
    @org.junit.Test(timeout = 1000)
    public void testSerialization_cf24768() {
        com.clearspring.analytics.stream.cardinality.LinearCounting lc = new com.clearspring.analytics.stream.cardinality.LinearCounting(4);
        lc.offer("a");
        lc.offer("b");
        lc.offer("c");
        lc.offer("d");
        lc.offer("e");
        com.clearspring.analytics.stream.cardinality.LinearCounting lc2 = new com.clearspring.analytics.stream.cardinality.LinearCounting(lc.getBytes());
        org.junit.Assert.assertArrayEquals(lc.map, lc2.map);
        org.junit.Assert.assertEquals(lc.count, lc2.count);
        // StatementAdderOnAssert create null value
        java.lang.Object vc_4730 = (java.lang.Object)null;
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(vc_4730);
        // AssertGenerator replace invocation
        boolean o_testSerialization_cf24768__15 = // StatementAdderMethod cloned existing statement
lc.offer(vc_4730);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_testSerialization_cf24768__15);
        org.junit.Assert.assertEquals(lc.length, lc2.length);
    }

    /* amplification of com.clearspring.analytics.stream.cardinality.TestLinearCounting#testSerialization */
    @org.junit.Test(timeout = 1000)
    public void testSerialization_cf24812() {
        com.clearspring.analytics.stream.cardinality.LinearCounting lc = new com.clearspring.analytics.stream.cardinality.LinearCounting(4);
        lc.offer("a");
        lc.offer("b");
        lc.offer("c");
        lc.offer("d");
        lc.offer("e");
        com.clearspring.analytics.stream.cardinality.LinearCounting lc2 = new com.clearspring.analytics.stream.cardinality.LinearCounting(lc.getBytes());
        org.junit.Assert.assertArrayEquals(lc.map, lc2.map);
        org.junit.Assert.assertEquals(lc.count, lc2.count);
        // AssertGenerator replace invocation
        java.lang.String o_testSerialization_cf24812__13 = // StatementAdderMethod cloned existing statement
lc2.mapAsBitString();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(o_testSerialization_cf24812__13, "00000010000100000100000000100010");
        org.junit.Assert.assertEquals(lc.length, lc2.length);
    }

    /* amplification of com.clearspring.analytics.stream.cardinality.TestLinearCounting#testSerialization */
    @org.junit.Test(timeout = 1000)
    public void testSerialization_cf24781_failAssert9() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            com.clearspring.analytics.stream.cardinality.LinearCounting lc = new com.clearspring.analytics.stream.cardinality.LinearCounting(4);
            lc.offer("a");
            lc.offer("b");
            lc.offer("c");
            lc.offer("d");
            lc.offer("e");
            com.clearspring.analytics.stream.cardinality.LinearCounting lc2 = new com.clearspring.analytics.stream.cardinality.LinearCounting(lc.getBytes());
            org.junit.Assert.assertArrayEquals(lc.map, lc2.map);
            // StatementAdderOnAssert create random local variable
            long vc_4737 = -1984612431152386411L;
            // StatementAdderMethod cloned existing statement
            lc.offerHashed(vc_4737);
            org.junit.Assert.fail("testSerialization_cf24781 should have thrown UnsupportedOperationException");
        } catch (java.lang.UnsupportedOperationException eee) {
        }
    }

    /* amplification of com.clearspring.analytics.stream.cardinality.TestLinearCounting#testSerialization */
    @org.junit.Test(timeout = 1000)
    public void testSerialization_cf24764() {
        com.clearspring.analytics.stream.cardinality.LinearCounting lc = new com.clearspring.analytics.stream.cardinality.LinearCounting(4);
        lc.offer("a");
        lc.offer("b");
        lc.offer("c");
        lc.offer("d");
        lc.offer("e");
        com.clearspring.analytics.stream.cardinality.LinearCounting lc2 = new com.clearspring.analytics.stream.cardinality.LinearCounting(lc.getBytes());
        org.junit.Assert.assertArrayEquals(lc.map, lc2.map);
        org.junit.Assert.assertEquals(lc.count, lc2.count);
        // AssertGenerator replace invocation
        boolean o_testSerialization_cf24764__13 = // StatementAdderMethod cloned existing statement
lc.isSaturated();
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_testSerialization_cf24764__13);
        org.junit.Assert.assertEquals(lc.length, lc2.length);
    }

    /* amplification of com.clearspring.analytics.stream.cardinality.TestLinearCounting#testSerialization */
    @org.junit.Test(timeout = 1000)
    public void testSerialization_cf24809_cf26523() {
        com.clearspring.analytics.stream.cardinality.LinearCounting lc = new com.clearspring.analytics.stream.cardinality.LinearCounting(4);
        lc.offer("a");
        lc.offer("b");
        lc.offer("c");
        lc.offer("d");
        lc.offer("e");
        com.clearspring.analytics.stream.cardinality.LinearCounting lc2 = new com.clearspring.analytics.stream.cardinality.LinearCounting(lc.getBytes());
        org.junit.Assert.assertArrayEquals(lc.map, lc2.map);
        org.junit.Assert.assertEquals(lc.count, lc2.count);
        // AssertGenerator replace invocation
        int o_testSerialization_cf24809__13 = // StatementAdderMethod cloned existing statement
lc.sizeof();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(o_testSerialization_cf24809__13, 4);
        // AssertGenerator replace invocation
        int o_testSerialization_cf24809_cf26523__17 = // StatementAdderMethod cloned existing statement
lc.getCount();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(o_testSerialization_cf24809_cf26523__17, 27);
        org.junit.Assert.assertEquals(lc.length, lc2.length);
    }

    /* amplification of com.clearspring.analytics.stream.cardinality.TestLinearCounting#testSerialization */
    @org.junit.Test(timeout = 1000)
    public void testSerialization_cf24774_failAssert6_literalMutation25680() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            com.clearspring.analytics.stream.cardinality.LinearCounting lc = new com.clearspring.analytics.stream.cardinality.LinearCounting(4);
            lc.offer("a");
            lc.offer("b");
            lc.offer("c");
            lc.offer("d");
            lc.offer("e");
            com.clearspring.analytics.stream.cardinality.LinearCounting lc2 = new com.clearspring.analytics.stream.cardinality.LinearCounting(lc.getBytes());
            org.junit.Assert.assertArrayEquals(lc.map, lc2.map);
            // StatementAdderOnAssert create literal from method
            int int_vc_139 = 2;
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(int_vc_139, 2);
            // StatementAdderMethod cloned existing statement
            lc2.offerHashed(int_vc_139);
            org.junit.Assert.fail("testSerialization_cf24774 should have thrown UnsupportedOperationException");
        } catch (java.lang.UnsupportedOperationException eee) {
        }
    }

    /* amplification of com.clearspring.analytics.stream.cardinality.TestLinearCounting#testSerialization */
    @org.junit.Test(timeout = 1000)
    public void testSerialization_cf24809_cf26529() {
        com.clearspring.analytics.stream.cardinality.LinearCounting lc = new com.clearspring.analytics.stream.cardinality.LinearCounting(4);
        lc.offer("a");
        lc.offer("b");
        lc.offer("c");
        lc.offer("d");
        lc.offer("e");
        com.clearspring.analytics.stream.cardinality.LinearCounting lc2 = new com.clearspring.analytics.stream.cardinality.LinearCounting(lc.getBytes());
        org.junit.Assert.assertArrayEquals(lc.map, lc2.map);
        org.junit.Assert.assertEquals(lc.count, lc2.count);
        // AssertGenerator replace invocation
        int o_testSerialization_cf24809__13 = // StatementAdderMethod cloned existing statement
lc.sizeof();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(o_testSerialization_cf24809__13, 4);
        // AssertGenerator replace invocation
        java.lang.String o_testSerialization_cf24809_cf26529__17 = // StatementAdderMethod cloned existing statement
lc2.mapAsBitString();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(o_testSerialization_cf24809_cf26529__17, "00000010000100000100000000100010");
        org.junit.Assert.assertEquals(lc.length, lc2.length);
    }

    /* amplification of com.clearspring.analytics.stream.cardinality.TestLinearCounting#testSerialization */
    @org.junit.Test(timeout = 1000)
    public void testSerialization_cf24809_cf26485() {
        com.clearspring.analytics.stream.cardinality.LinearCounting lc = new com.clearspring.analytics.stream.cardinality.LinearCounting(4);
        lc.offer("a");
        lc.offer("b");
        lc.offer("c");
        lc.offer("d");
        lc.offer("e");
        com.clearspring.analytics.stream.cardinality.LinearCounting lc2 = new com.clearspring.analytics.stream.cardinality.LinearCounting(lc.getBytes());
        org.junit.Assert.assertArrayEquals(lc.map, lc2.map);
        org.junit.Assert.assertEquals(lc.count, lc2.count);
        // AssertGenerator replace invocation
        int o_testSerialization_cf24809__13 = // StatementAdderMethod cloned existing statement
lc.sizeof();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(o_testSerialization_cf24809__13, 4);
        // StatementAdderOnAssert create null value
        java.lang.Object vc_5614 = (java.lang.Object)null;
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(vc_5614);
        // AssertGenerator replace invocation
        boolean o_testSerialization_cf24809_cf26485__19 = // StatementAdderMethod cloned existing statement
lc.offer(vc_5614);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_testSerialization_cf24809_cf26485__19);
        org.junit.Assert.assertEquals(lc.length, lc2.length);
    }

    /* amplification of com.clearspring.analytics.stream.cardinality.TestLinearCounting#testSerialization */
    @org.junit.Test(timeout = 1000)
    public void testSerialization_cf24799_failAssert11_cf26094_cf31583() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            com.clearspring.analytics.stream.cardinality.LinearCounting lc = new com.clearspring.analytics.stream.cardinality.LinearCounting(4);
            lc.offer("a");
            lc.offer("b");
            lc.offer("c");
            lc.offer("d");
            lc.offer("e");
            com.clearspring.analytics.stream.cardinality.LinearCounting lc2 = new com.clearspring.analytics.stream.cardinality.LinearCounting(lc.getBytes());
            // AssertGenerator replace invocation
            boolean o_testSerialization_cf24799_failAssert11_cf26094__13 = // StatementAdderMethod cloned existing statement
lc2.isSaturated();
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(o_testSerialization_cf24799_failAssert11_cf26094__13);
            // StatementAdderOnAssert create random local variable
            java.lang.Object vc_8063 = new java.lang.Object();
            // StatementAdderMethod cloned existing statement
            lc.offer(vc_8063);
            org.junit.Assert.assertArrayEquals(lc.map, lc2.map);
            // StatementAdderOnAssert create null value
            com.clearspring.analytics.stream.cardinality.LinearCounting vc_4748 = (com.clearspring.analytics.stream.cardinality.LinearCounting)null;
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(vc_4748);
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(vc_4748);
            // StatementAdderMethod cloned existing statement
            vc_4748.getUtilization();
            org.junit.Assert.fail("testSerialization_cf24799 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    /* amplification of com.clearspring.analytics.stream.cardinality.TestLinearCounting#testSerialization */
    @org.junit.Test(timeout = 1000)
    public void testSerialization_cf24775_failAssert7_add25744_cf30711() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            com.clearspring.analytics.stream.cardinality.LinearCounting lc = new com.clearspring.analytics.stream.cardinality.LinearCounting(4);
            lc.offer("a");
            lc.offer("b");
            lc.offer("c");
            lc.offer("d");
            lc.offer("e");
            com.clearspring.analytics.stream.cardinality.LinearCounting lc2 = new com.clearspring.analytics.stream.cardinality.LinearCounting(lc.getBytes());
            // AssertGenerator replace invocation
            java.lang.String o_testSerialization_cf24775_failAssert7_add25744_cf30711__13 = // StatementAdderMethod cloned existing statement
lc2.mapAsBitString();
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(o_testSerialization_cf24775_failAssert7_add25744_cf30711__13, "00000010000100000100000000100010");
            org.junit.Assert.assertArrayEquals(lc.map, lc2.map);
            // StatementAdderOnAssert create random local variable
            int vc_4734 = 1610469563;
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(vc_4734, 1610469563);
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(vc_4734, 1610469563);
            // StatementAdderMethod cloned existing statement
            // MethodCallAdder
            lc2.offerHashed(vc_4734);
            // StatementAdderMethod cloned existing statement
            lc2.offerHashed(vc_4734);
            org.junit.Assert.fail("testSerialization_cf24775 should have thrown UnsupportedOperationException");
        } catch (java.lang.UnsupportedOperationException eee) {
        }
    }

    /* amplification of com.clearspring.analytics.stream.cardinality.TestLinearCounting#testSerialization */
    @org.junit.Test(timeout = 1000)
    public void testSerialization_add24757_add25073_cf26917_failAssert12() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            com.clearspring.analytics.stream.cardinality.LinearCounting lc = new com.clearspring.analytics.stream.cardinality.LinearCounting(4);
            // AssertGenerator replace invocation
            boolean o_testSerialization_add24757_add25073__3 = // MethodCallAdder
lc.offer("a");
            // AssertGenerator add assertion
            org.junit.Assert.assertTrue(o_testSerialization_add24757_add25073__3);
            lc.offer("a");
            lc.offer("b");
            lc.offer("c");
            lc.offer("d");
            // AssertGenerator replace invocation
            boolean o_testSerialization_add24757__7 = // MethodCallAdder
lc.offer("e");
            // AssertGenerator add assertion
            org.junit.Assert.assertTrue(o_testSerialization_add24757__7);
            lc.offer("e");
            com.clearspring.analytics.stream.cardinality.LinearCounting lc2 = new com.clearspring.analytics.stream.cardinality.LinearCounting(lc.getBytes());
            org.junit.Assert.assertArrayEquals(lc.map, lc2.map);
            // StatementAdderOnAssert create null value
            com.clearspring.analytics.stream.cardinality.LinearCounting vc_5846 = (com.clearspring.analytics.stream.cardinality.LinearCounting)null;
            // StatementAdderMethod cloned existing statement
            vc_5846.cardinality();
            org.junit.Assert.fail("testSerialization_add24757_add25073_cf26917 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    /* amplification of com.clearspring.analytics.stream.cardinality.TestLinearCounting#testSerialization */
    @org.junit.Test(timeout = 1000)
    public void testSerialization_add24757_cf25101_failAssert20_cf31014() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            com.clearspring.analytics.stream.cardinality.LinearCounting lc = new com.clearspring.analytics.stream.cardinality.LinearCounting(4);
            lc.offer("a");
            lc.offer("b");
            lc.offer("c");
            lc.offer("d");
            // AssertGenerator replace invocation
            boolean o_testSerialization_add24757__7 = // MethodCallAdder
lc.offer("e");
            // AssertGenerator add assertion
            org.junit.Assert.assertTrue(o_testSerialization_add24757__7);
            lc.offer("e");
            com.clearspring.analytics.stream.cardinality.LinearCounting lc2 = new com.clearspring.analytics.stream.cardinality.LinearCounting(lc.getBytes());
            // StatementAdderOnAssert create literal from method
            long long_vc_237 = 4588746652710906503L;
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(long_vc_237, 4588746652710906503L);
            // StatementAdderMethod cloned existing statement
            lc.offerHashed(long_vc_237);
            org.junit.Assert.assertArrayEquals(lc.map, lc2.map);
            // StatementAdderOnAssert create random local variable
            long vc_4907 = 4588746652710906503L;
            // StatementAdderMethod cloned existing statement
            lc2.offerHashed(vc_4907);
            org.junit.Assert.fail("testSerialization_add24757_cf25101 should have thrown UnsupportedOperationException");
        } catch (java.lang.UnsupportedOperationException eee) {
        }
    }

    /* amplification of com.clearspring.analytics.stream.cardinality.TestLinearCounting#testSerialization */
    @org.junit.Test(timeout = 1000)
    public void testSerialization_cf24799_failAssert11_cf26094_cf31577() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            com.clearspring.analytics.stream.cardinality.LinearCounting lc = new com.clearspring.analytics.stream.cardinality.LinearCounting(4);
            lc.offer("a");
            lc.offer("b");
            lc.offer("c");
            lc.offer("d");
            lc.offer("e");
            com.clearspring.analytics.stream.cardinality.LinearCounting lc2 = new com.clearspring.analytics.stream.cardinality.LinearCounting(lc.getBytes());
            // AssertGenerator replace invocation
            boolean o_testSerialization_cf24799_failAssert11_cf26094__13 = // StatementAdderMethod cloned existing statement
lc2.isSaturated();
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(o_testSerialization_cf24799_failAssert11_cf26094__13);
            // StatementAdderOnAssert create null value
            com.clearspring.analytics.stream.cardinality.LinearCounting vc_8058 = (com.clearspring.analytics.stream.cardinality.LinearCounting)null;
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(vc_8058);
            // StatementAdderMethod cloned existing statement
            vc_8058.isSaturated();
            org.junit.Assert.assertArrayEquals(lc.map, lc2.map);
            // StatementAdderOnAssert create null value
            com.clearspring.analytics.stream.cardinality.LinearCounting vc_4748 = (com.clearspring.analytics.stream.cardinality.LinearCounting)null;
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(vc_4748);
            // StatementAdderMethod cloned existing statement
            vc_4748.getUtilization();
            org.junit.Assert.fail("testSerialization_cf24799 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    /* amplification of com.clearspring.analytics.stream.cardinality.TestLinearCounting#testSerialization */
    @org.junit.Test(timeout = 1000)
    public void testSerialization_cf24781_failAssert9_add25881_cf33113() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            com.clearspring.analytics.stream.cardinality.LinearCounting lc = new com.clearspring.analytics.stream.cardinality.LinearCounting(4);
            // AssertGenerator replace invocation
            boolean o_testSerialization_cf24781_failAssert9_add25881__5 = // MethodCallAdder
lc.offer("a");
            // AssertGenerator add assertion
            org.junit.Assert.assertTrue(o_testSerialization_cf24781_failAssert9_add25881__5);
            lc.offer("a");
            lc.offer("b");
            lc.offer("c");
            lc.offer("d");
            lc.offer("e");
            com.clearspring.analytics.stream.cardinality.LinearCounting lc2 = new com.clearspring.analytics.stream.cardinality.LinearCounting(lc.getBytes());
            // AssertGenerator replace invocation
            int o_testSerialization_cf24781_failAssert9_add25881_cf33113__17 = // StatementAdderMethod cloned existing statement
lc2.sizeof();
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(o_testSerialization_cf24781_failAssert9_add25881_cf33113__17, 4);
            org.junit.Assert.assertArrayEquals(lc.map, lc2.map);
            // StatementAdderOnAssert create random local variable
            long vc_4737 = -1984612431152386411L;
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(vc_4737, -1984612431152386411L);
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(vc_4737, -1984612431152386411L);
            // StatementAdderMethod cloned existing statement
            lc.offerHashed(vc_4737);
            org.junit.Assert.fail("testSerialization_cf24781 should have thrown UnsupportedOperationException");
        } catch (java.lang.UnsupportedOperationException eee) {
        }
    }
}

