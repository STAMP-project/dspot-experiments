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


public class TestHyperLogLogAmpl {
    @org.junit.Test
    public void testSerialization() throws java.io.IOException, java.lang.ClassNotFoundException {
        com.clearspring.analytics.stream.cardinality.HyperLogLog hll = new com.clearspring.analytics.stream.cardinality.HyperLogLog(8);
        hll.offer("a");
        hll.offer("b");
        hll.offer("c");
        hll.offer("d");
        hll.offer("e");
        com.clearspring.analytics.stream.cardinality.HyperLogLog hll2 = ((com.clearspring.analytics.stream.cardinality.HyperLogLog) (com.clearspring.analytics.TestUtils.deserialize(com.clearspring.analytics.TestUtils.serialize(hll))));
        org.junit.Assert.assertEquals(hll.cardinality(), hll2.cardinality());
    }

    @org.junit.Test
    public void testHighCardinality() {
        long start = java.lang.System.currentTimeMillis();
        com.clearspring.analytics.stream.cardinality.HyperLogLog hyperLogLog = new com.clearspring.analytics.stream.cardinality.HyperLogLog(10);
        int size = 10000000;
        for (int i = 0; i < size; i++) {
            hyperLogLog.offer(com.clearspring.analytics.stream.cardinality.TestICardinality.streamElement(i));
        }
        java.lang.System.out.println(("time: " + ((java.lang.System.currentTimeMillis()) - start)));
        long estimate = hyperLogLog.cardinality();
        double err = (java.lang.Math.abs((estimate - size))) / ((double) (size));
        java.lang.System.out.println(err);
        org.junit.Assert.assertTrue((err < 0.1));
    }

    @org.junit.Test
    public void testHighCardinality_withDefinedRSD() {
        long start = java.lang.System.currentTimeMillis();
        com.clearspring.analytics.stream.cardinality.HyperLogLog hyperLogLog = new com.clearspring.analytics.stream.cardinality.HyperLogLog(0.01);
        int size = 100000000;
        for (int i = 0; i < size; i++) {
            hyperLogLog.offer(com.clearspring.analytics.stream.cardinality.TestICardinality.streamElement(i));
        }
        java.lang.System.out.println(("time: " + ((java.lang.System.currentTimeMillis()) - start)));
        long estimate = hyperLogLog.cardinality();
        double err = (java.lang.Math.abs((estimate - size))) / ((double) (size));
        java.lang.System.out.println(err);
        org.junit.Assert.assertTrue((err < 0.1));
    }

    @org.junit.Test
    public void testMerge() throws com.clearspring.analytics.stream.cardinality.CardinalityMergeException {
        int numToMerge = 5;
        int bits = 16;
        int cardinality = 1000000;
        com.clearspring.analytics.stream.cardinality.HyperLogLog[] hyperLogLogs = new com.clearspring.analytics.stream.cardinality.HyperLogLog[numToMerge];
        com.clearspring.analytics.stream.cardinality.HyperLogLog baseline = new com.clearspring.analytics.stream.cardinality.HyperLogLog(bits);
        for (int i = 0; i < numToMerge; i++) {
            hyperLogLogs[i] = new com.clearspring.analytics.stream.cardinality.HyperLogLog(bits);
            for (int j = 0; j < cardinality; j++) {
                double val = java.lang.Math.random();
                hyperLogLogs[i].offer(val);
                baseline.offer(val);
            }
        }
        long expectedCardinality = numToMerge * cardinality;
        com.clearspring.analytics.stream.cardinality.HyperLogLog hll = hyperLogLogs[0];
        hyperLogLogs = java.util.Arrays.asList(hyperLogLogs).subList(1, hyperLogLogs.length).toArray(new com.clearspring.analytics.stream.cardinality.HyperLogLog[0]);
        long mergedEstimate = hll.merge(hyperLogLogs).cardinality();
        long baselineEstimate = baseline.cardinality();
        double se = expectedCardinality * (1.04 / (java.lang.Math.sqrt(java.lang.Math.pow(2, bits))));
        java.lang.System.out.println(("Baseline estimate: " + baselineEstimate));
        java.lang.System.out.println(((((("Expect estimate: " + mergedEstimate) + " is between ") + (expectedCardinality - (3 * se))) + " and ") + (expectedCardinality + (3 * se))));
        org.junit.Assert.assertTrue((mergedEstimate >= (expectedCardinality - (3 * se))));
        org.junit.Assert.assertTrue((mergedEstimate <= (expectedCardinality + (3 * se))));
        org.junit.Assert.assertEquals(mergedEstimate, baselineEstimate);
    }

    @org.junit.Test
    @org.junit.Ignore
    public void testPrecise() throws com.clearspring.analytics.stream.cardinality.CardinalityMergeException {
        int cardinality = 1000000000;
        int b = 12;
        com.clearspring.analytics.stream.cardinality.HyperLogLog baseline = new com.clearspring.analytics.stream.cardinality.HyperLogLog(b);
        com.clearspring.analytics.stream.cardinality.HyperLogLog guava128 = new com.clearspring.analytics.stream.cardinality.HyperLogLog(b);
        com.google.common.hash.HashFunction hf128 = com.google.common.hash.Hashing.murmur3_128();
        for (int j = 0; j < cardinality; j++) {
            java.lang.Double val = java.lang.Math.random();
            java.lang.String valString = val.toString();
            baseline.offer(valString);
            guava128.offerHashed(hf128.hashString(valString, com.google.common.base.Charsets.UTF_8).asLong());
            if ((j > 0) && ((j % 1000000) == 0)) {
                java.lang.System.out.println(("current count: " + j));
            }
        }
        long baselineEstimate = baseline.cardinality();
        long g128Estimate = guava128.cardinality();
        double se = cardinality * (1.04 / (java.lang.Math.sqrt(java.lang.Math.pow(2, b))));
        double baselineError = (baselineEstimate - cardinality) / ((double) (cardinality));
        double g128Error = (g128Estimate - cardinality) / ((double) (cardinality));
        java.lang.System.out.format("b: %f g128 %f", baselineError, g128Error);
        org.junit.Assert.assertTrue("baseline estimate bigger than expected", (baselineEstimate >= (cardinality - (2 * se))));
        org.junit.Assert.assertTrue("baseline estimate smaller than expected", (baselineEstimate <= (cardinality + (2 * se))));
        org.junit.Assert.assertTrue("g128 estimate bigger than expected", (g128Estimate >= (cardinality - (2 * se))));
        org.junit.Assert.assertTrue("g128 estimate smaller than expected", (g128Estimate <= (cardinality + (2 * se))));
    }

    /**
     * should not fail with HyperLogLogMergeException: "Cannot merge estimators of different sizes"
     */
    @org.junit.Test
    public void testMergeWithRegisterSet() throws com.clearspring.analytics.stream.cardinality.CardinalityMergeException {
        com.clearspring.analytics.stream.cardinality.HyperLogLog first = new com.clearspring.analytics.stream.cardinality.HyperLogLog(16, new com.clearspring.analytics.stream.cardinality.RegisterSet((1 << 20)));
        com.clearspring.analytics.stream.cardinality.HyperLogLog second = new com.clearspring.analytics.stream.cardinality.HyperLogLog(16, new com.clearspring.analytics.stream.cardinality.RegisterSet((1 << 20)));
        // AssertGenerator replace invocation
        boolean o_testMergeWithRegisterSet__7 = first.offer(0);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_testMergeWithRegisterSet__7);
        // AssertGenerator replace invocation
        boolean o_testMergeWithRegisterSet__8 = second.offer(1);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_testMergeWithRegisterSet__8);
        // AssertGenerator replace invocation
        com.clearspring.analytics.stream.cardinality.ICardinality o_testMergeWithRegisterSet__9 = first.merge(second);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((com.clearspring.analytics.stream.cardinality.HyperLogLog)o_testMergeWithRegisterSet__9).sizeof(), 699052);
    }

    @org.junit.Test
    public void testComputeCount() {
        com.clearspring.analytics.stream.cardinality.HyperLogLog hyperLogLog = new com.clearspring.analytics.stream.cardinality.HyperLogLog(16);
        // AssertGenerator replace invocation
        boolean o_testComputeCount__3 = hyperLogLog.offer(0);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_testComputeCount__3);
        // AssertGenerator replace invocation
        boolean o_testComputeCount__4 = hyperLogLog.offer(1);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_testComputeCount__4);
        // AssertGenerator replace invocation
        boolean o_testComputeCount__5 = hyperLogLog.offer(2);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_testComputeCount__5);
        // AssertGenerator replace invocation
        boolean o_testComputeCount__6 = hyperLogLog.offer(3);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_testComputeCount__6);
        // AssertGenerator replace invocation
        boolean o_testComputeCount__7 = hyperLogLog.offer(16);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_testComputeCount__7);
        // AssertGenerator replace invocation
        boolean o_testComputeCount__8 = hyperLogLog.offer(17);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_testComputeCount__8);
        // AssertGenerator replace invocation
        boolean o_testComputeCount__9 = hyperLogLog.offer(18);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_testComputeCount__9);
        // AssertGenerator replace invocation
        boolean o_testComputeCount__10 = hyperLogLog.offer(19);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_testComputeCount__10);
        // AssertGenerator replace invocation
        boolean o_testComputeCount__11 = hyperLogLog.offer(19);
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_testComputeCount__11);
        org.junit.Assert.assertEquals(8, hyperLogLog.cardinality());
    }

    @org.junit.Test
    public void testSerializationUsingBuilder() throws java.io.IOException {
        com.clearspring.analytics.stream.cardinality.HyperLogLog hll = new com.clearspring.analytics.stream.cardinality.HyperLogLog(8);
        // AssertGenerator replace invocation
        boolean o_testSerializationUsingBuilder__3 = hll.offer("a");
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_testSerializationUsingBuilder__3);
        // AssertGenerator replace invocation
        boolean o_testSerializationUsingBuilder__4 = hll.offer("b");
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_testSerializationUsingBuilder__4);
        // AssertGenerator replace invocation
        boolean o_testSerializationUsingBuilder__5 = hll.offer("c");
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_testSerializationUsingBuilder__5);
        // AssertGenerator replace invocation
        boolean o_testSerializationUsingBuilder__6 = hll.offer("d");
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_testSerializationUsingBuilder__6);
        // AssertGenerator replace invocation
        boolean o_testSerializationUsingBuilder__7 = hll.offer("e");
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_testSerializationUsingBuilder__7);
        com.clearspring.analytics.stream.cardinality.HyperLogLog hll2 = com.clearspring.analytics.stream.cardinality.HyperLogLog.Builder.build(hll.getBytes());
        org.junit.Assert.assertEquals(hll.cardinality(), hll2.cardinality());
    }

    /* amplification of com.clearspring.analytics.stream.cardinality.TestHyperLogLog#testComputeCount */
    @org.junit.Test
    public void testComputeCount_literalMutation12_failAssert1() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            com.clearspring.analytics.stream.cardinality.HyperLogLog hyperLogLog = new com.clearspring.analytics.stream.cardinality.HyperLogLog(0);
            hyperLogLog.offer(0);
            hyperLogLog.offer(1);
            hyperLogLog.offer(2);
            hyperLogLog.offer(3);
            hyperLogLog.offer(16);
            hyperLogLog.offer(17);
            hyperLogLog.offer(18);
            hyperLogLog.offer(19);
            hyperLogLog.offer(19);
            // MethodAssertGenerator build local variable
            Object o_13_0 = hyperLogLog.cardinality();
            org.junit.Assert.fail("testComputeCount_literalMutation12 should have thrown ArrayIndexOutOfBoundsException");
        } catch (java.lang.ArrayIndexOutOfBoundsException eee) {
        }
    }

    /* amplification of com.clearspring.analytics.stream.cardinality.TestHyperLogLog#testComputeCount */
    @org.junit.Test(timeout = 1000)
    public void testComputeCount_cf22() {
        com.clearspring.analytics.stream.cardinality.HyperLogLog hyperLogLog = new com.clearspring.analytics.stream.cardinality.HyperLogLog(16);
        hyperLogLog.offer(0);
        hyperLogLog.offer(1);
        hyperLogLog.offer(2);
        hyperLogLog.offer(3);
        hyperLogLog.offer(16);
        hyperLogLog.offer(17);
        hyperLogLog.offer(18);
        hyperLogLog.offer(19);
        hyperLogLog.offer(19);
        // StatementAdderOnAssert create null value
        java.lang.Object vc_2 = (java.lang.Object)null;
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(vc_2);
        // AssertGenerator replace invocation
        boolean o_testComputeCount_cf22__14 = // StatementAdderMethod cloned existing statement
hyperLogLog.offer(vc_2);
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_testComputeCount_cf22__14);
        org.junit.Assert.assertEquals(8, hyperLogLog.cardinality());
    }

    /* amplification of com.clearspring.analytics.stream.cardinality.TestHyperLogLog#testComputeCount */
    @org.junit.Test
    public void testComputeCount_literalMutation11_failAssert0() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            com.clearspring.analytics.stream.cardinality.HyperLogLog hyperLogLog = new com.clearspring.analytics.stream.cardinality.HyperLogLog(32);
            hyperLogLog.offer(0);
            hyperLogLog.offer(1);
            hyperLogLog.offer(2);
            hyperLogLog.offer(3);
            hyperLogLog.offer(16);
            hyperLogLog.offer(17);
            hyperLogLog.offer(18);
            hyperLogLog.offer(19);
            hyperLogLog.offer(19);
            // MethodAssertGenerator build local variable
            Object o_13_0 = hyperLogLog.cardinality();
            org.junit.Assert.fail("testComputeCount_literalMutation11 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of com.clearspring.analytics.stream.cardinality.TestHyperLogLog#testComputeCount */
    @org.junit.Test(timeout = 1000)
    public void testComputeCount_add7() {
        com.clearspring.analytics.stream.cardinality.HyperLogLog hyperLogLog = new com.clearspring.analytics.stream.cardinality.HyperLogLog(16);
        hyperLogLog.offer(0);
        hyperLogLog.offer(1);
        hyperLogLog.offer(2);
        hyperLogLog.offer(3);
        hyperLogLog.offer(16);
        hyperLogLog.offer(17);
        // AssertGenerator replace invocation
        boolean o_testComputeCount_add7__9 = // MethodCallAdder
hyperLogLog.offer(18);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_testComputeCount_add7__9);
        hyperLogLog.offer(18);
        hyperLogLog.offer(19);
        hyperLogLog.offer(19);
        org.junit.Assert.assertEquals(8, hyperLogLog.cardinality());
    }

    /* amplification of com.clearspring.analytics.stream.cardinality.TestHyperLogLog#testComputeCount */
    @org.junit.Test(timeout = 1000)
    public void testComputeCount_cf73() {
        com.clearspring.analytics.stream.cardinality.HyperLogLog hyperLogLog = new com.clearspring.analytics.stream.cardinality.HyperLogLog(16);
        hyperLogLog.offer(0);
        hyperLogLog.offer(1);
        hyperLogLog.offer(2);
        hyperLogLog.offer(3);
        hyperLogLog.offer(16);
        hyperLogLog.offer(17);
        hyperLogLog.offer(18);
        hyperLogLog.offer(19);
        hyperLogLog.offer(19);
        // AssertGenerator replace invocation
        int o_testComputeCount_cf73__12 = // StatementAdderMethod cloned existing statement
hyperLogLog.sizeof();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(o_testComputeCount_cf73__12, 43692);
        org.junit.Assert.assertEquals(8, hyperLogLog.cardinality());
    }

    /* amplification of com.clearspring.analytics.stream.cardinality.TestHyperLogLog#testComputeCount */
    @org.junit.Test(timeout = 1000)
    public void testComputeCount_cf30() {
        com.clearspring.analytics.stream.cardinality.HyperLogLog hyperLogLog = new com.clearspring.analytics.stream.cardinality.HyperLogLog(16);
        hyperLogLog.offer(0);
        hyperLogLog.offer(1);
        hyperLogLog.offer(2);
        hyperLogLog.offer(3);
        hyperLogLog.offer(16);
        hyperLogLog.offer(17);
        hyperLogLog.offer(18);
        hyperLogLog.offer(19);
        hyperLogLog.offer(19);
        // StatementAdderOnAssert create literal from method
        int int_vc_0 = 2;
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(int_vc_0, 2);
        // AssertGenerator replace invocation
        boolean o_testComputeCount_cf30__14 = // StatementAdderMethod cloned existing statement
hyperLogLog.offerHashed(int_vc_0);
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_testComputeCount_cf30__14);
        org.junit.Assert.assertEquals(8, hyperLogLog.cardinality());
    }

    /* amplification of com.clearspring.analytics.stream.cardinality.TestHyperLogLog#testHighCardinality */
    @org.junit.Test
    public void testHighCardinality_literalMutation5193_failAssert0() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            long start = java.lang.System.currentTimeMillis();
            com.clearspring.analytics.stream.cardinality.HyperLogLog hyperLogLog = new com.clearspring.analytics.stream.cardinality.HyperLogLog(0);
            int size = 10000000;
            for (int i = 0; i < size; i++) {
                hyperLogLog.offer(com.clearspring.analytics.stream.cardinality.TestICardinality.streamElement(i));
            }
            java.lang.System.out.println(("time: " + ((java.lang.System.currentTimeMillis()) - start)));
            long estimate = hyperLogLog.cardinality();
            double err = (java.lang.Math.abs((estimate - size))) / ((double) (size));
            java.lang.System.out.println(err);
            // MethodAssertGenerator build local variable
            Object o_20_0 = err < 0.1;
            org.junit.Assert.fail("testHighCardinality_literalMutation5193 should have thrown ArrayIndexOutOfBoundsException");
        } catch (java.lang.ArrayIndexOutOfBoundsException eee) {
        }
    }

    /* amplification of com.clearspring.analytics.stream.cardinality.TestHyperLogLog#testHighCardinality */
    @org.junit.Test
    public void testHighCardinality_literalMutation5196() {
        long start = java.lang.System.currentTimeMillis();
        com.clearspring.analytics.stream.cardinality.HyperLogLog hyperLogLog = new com.clearspring.analytics.stream.cardinality.HyperLogLog(20);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((com.clearspring.analytics.stream.cardinality.HyperLogLog)hyperLogLog).sizeof(), 699052);
        int size = 10000000;
        for (int i = 0; i < size; i++) {
            hyperLogLog.offer(com.clearspring.analytics.stream.cardinality.TestICardinality.streamElement(i));
        }
        java.lang.System.out.println(("time: " + ((java.lang.System.currentTimeMillis()) - start)));
        long estimate = hyperLogLog.cardinality();
        double err = (java.lang.Math.abs((estimate - size))) / ((double) (size));
        java.lang.System.out.println(err);
        org.junit.Assert.assertTrue((err < 0.1));
    }

    /* amplification of com.clearspring.analytics.stream.cardinality.TestHyperLogLog#testHighCardinality_withDefinedRSD */
    @org.junit.Test(timeout = 1000)
    public void testHighCardinality_withDefinedRSD_literalMutation9727_cf10362_literalMutation14196_failAssert1() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            long start = java.lang.System.currentTimeMillis();
            com.clearspring.analytics.stream.cardinality.HyperLogLog hyperLogLog = new com.clearspring.analytics.stream.cardinality.HyperLogLog(2.00000001E8);
            int size = 200000000;
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(size, 200000000);
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(size, 200000000);
            for (int i = 0; i < size; i++) {
                hyperLogLog.offer(com.clearspring.analytics.stream.cardinality.TestICardinality.streamElement(i));
            }
            java.lang.System.out.println(("time: " + ((java.lang.System.currentTimeMillis()) - start)));
            long estimate = hyperLogLog.cardinality();
            double err = (java.lang.Math.abs((estimate - size))) / ((double) (size));
            java.lang.System.out.println(err);
            // StatementAdderOnAssert create null value
            java.lang.Object vc_2061 = (java.lang.Object)null;
            // StatementAdderOnAssert create null value
            com.clearspring.analytics.stream.cardinality.HyperLogLog vc_2059 = (com.clearspring.analytics.stream.cardinality.HyperLogLog)null;
            // StatementAdderMethod cloned existing statement
            vc_2059.offer(vc_2061);
            // MethodAssertGenerator build local variable
            Object o_31_0 = err < 0.1;
            org.junit.Assert.fail("testHighCardinality_withDefinedRSD_literalMutation9727_cf10362_literalMutation14196 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of com.clearspring.analytics.stream.cardinality.TestHyperLogLog#testHighCardinality_withDefinedRSD */
    @org.junit.Test
    public void testHighCardinality_withDefinedRSD_literalMutation9725_literalMutation10010_literalMutation13650_failAssert11() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            long start = java.lang.System.currentTimeMillis();
            com.clearspring.analytics.stream.cardinality.HyperLogLog hyperLogLog = new com.clearspring.analytics.stream.cardinality.HyperLogLog(-0.99);
            int size = 100000001;
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(size, 100000001);
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(size, 100000001);
            for (int i = 0; i < size; i++) {
                hyperLogLog.offer(com.clearspring.analytics.stream.cardinality.TestICardinality.streamElement(i));
            }
            java.lang.System.out.println(("" + ((java.lang.System.currentTimeMillis()) - start)));
            long estimate = hyperLogLog.cardinality();
            double err = (java.lang.Math.abs((estimate - size))) / ((double) (size));
            java.lang.System.out.println(err);
            // MethodAssertGenerator build local variable
            Object o_25_0 = err < 0.1;
            org.junit.Assert.fail("testHighCardinality_withDefinedRSD_literalMutation9725_literalMutation10010_literalMutation13650 should have thrown ArrayIndexOutOfBoundsException");
        } catch (java.lang.ArrayIndexOutOfBoundsException eee) {
        }
    }

    /* amplification of com.clearspring.analytics.stream.cardinality.TestHyperLogLog#testMerge */
    @org.junit.Test(timeout = 1000)
    public void testMerge_cf18429() throws com.clearspring.analytics.stream.cardinality.CardinalityMergeException {
        int numToMerge = 5;
        int bits = 16;
        int cardinality = 1000000;
        com.clearspring.analytics.stream.cardinality.HyperLogLog[] hyperLogLogs = new com.clearspring.analytics.stream.cardinality.HyperLogLog[numToMerge];
        com.clearspring.analytics.stream.cardinality.HyperLogLog baseline = new com.clearspring.analytics.stream.cardinality.HyperLogLog(bits);
        for (int i = 0; i < numToMerge; i++) {
            hyperLogLogs[i] = new com.clearspring.analytics.stream.cardinality.HyperLogLog(bits);
            for (int j = 0; j < cardinality; j++) {
                double val = java.lang.Math.random();
                hyperLogLogs[i].offer(val);
                baseline.offer(val);
            }
        }
        long expectedCardinality = numToMerge * cardinality;
        com.clearspring.analytics.stream.cardinality.HyperLogLog hll = hyperLogLogs[0];
        hyperLogLogs = java.util.Arrays.asList(hyperLogLogs).subList(1, hyperLogLogs.length).toArray(new com.clearspring.analytics.stream.cardinality.HyperLogLog[0]);
        long mergedEstimate = hll.merge(hyperLogLogs).cardinality();
        long baselineEstimate = baseline.cardinality();
        double se = expectedCardinality * (1.04 / (java.lang.Math.sqrt(java.lang.Math.pow(2, bits))));
        java.lang.System.out.println(("Baseline estimate: " + baselineEstimate));
        java.lang.System.out.println(((((("Expect estimate: " + mergedEstimate) + " is between ") + (expectedCardinality - (3 * se))) + " and ") + (expectedCardinality + (3 * se))));
        org.junit.Assert.assertTrue((mergedEstimate >= (expectedCardinality - (3 * se))));
        org.junit.Assert.assertTrue((mergedEstimate <= (expectedCardinality + (3 * se))));
        // StatementAdderOnAssert create random local variable
        int vc_3341 = 312365930;
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(vc_3341, 312365930);
        // AssertGenerator replace invocation
        boolean o_testMerge_cf18429__41 = // StatementAdderMethod cloned existing statement
hll.offerHashed(vc_3341);
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_testMerge_cf18429__41);
        org.junit.Assert.assertEquals(mergedEstimate, baselineEstimate);
    }

    /* amplification of com.clearspring.analytics.stream.cardinality.TestHyperLogLog#testMerge */
    @org.junit.Test(timeout = 1000)
    public void testMerge_cf18441() throws com.clearspring.analytics.stream.cardinality.CardinalityMergeException {
        int numToMerge = 5;
        int bits = 16;
        int cardinality = 1000000;
        com.clearspring.analytics.stream.cardinality.HyperLogLog[] hyperLogLogs = new com.clearspring.analytics.stream.cardinality.HyperLogLog[numToMerge];
        com.clearspring.analytics.stream.cardinality.HyperLogLog baseline = new com.clearspring.analytics.stream.cardinality.HyperLogLog(bits);
        for (int i = 0; i < numToMerge; i++) {
            hyperLogLogs[i] = new com.clearspring.analytics.stream.cardinality.HyperLogLog(bits);
            for (int j = 0; j < cardinality; j++) {
                double val = java.lang.Math.random();
                hyperLogLogs[i].offer(val);
                baseline.offer(val);
            }
        }
        long expectedCardinality = numToMerge * cardinality;
        com.clearspring.analytics.stream.cardinality.HyperLogLog hll = hyperLogLogs[0];
        hyperLogLogs = java.util.Arrays.asList(hyperLogLogs).subList(1, hyperLogLogs.length).toArray(new com.clearspring.analytics.stream.cardinality.HyperLogLog[0]);
        long mergedEstimate = hll.merge(hyperLogLogs).cardinality();
        long baselineEstimate = baseline.cardinality();
        double se = expectedCardinality * (1.04 / (java.lang.Math.sqrt(java.lang.Math.pow(2, bits))));
        java.lang.System.out.println(("Baseline estimate: " + baselineEstimate));
        java.lang.System.out.println(((((("Expect estimate: " + mergedEstimate) + " is between ") + (expectedCardinality - (3 * se))) + " and ") + (expectedCardinality + (3 * se))));
        org.junit.Assert.assertTrue((mergedEstimate >= (expectedCardinality - (3 * se))));
        org.junit.Assert.assertTrue((mergedEstimate <= (expectedCardinality + (3 * se))));
        // AssertGenerator replace invocation
        boolean o_testMerge_cf18441__39 = // StatementAdderMethod cloned existing statement
baseline.offerHashed(baselineEstimate);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_testMerge_cf18441__39);
        org.junit.Assert.assertEquals(mergedEstimate, baselineEstimate);
    }

    /* amplification of com.clearspring.analytics.stream.cardinality.TestHyperLogLog#testMerge */
    @org.junit.Test
    public void testMerge_literalMutation18344_failAssert19() throws com.clearspring.analytics.stream.cardinality.CardinalityMergeException {
        // AssertGenerator generate try/catch block with fail statement
        try {
            int numToMerge = 5;
            int bits = 32;
            int cardinality = 1000000;
            com.clearspring.analytics.stream.cardinality.HyperLogLog[] hyperLogLogs = new com.clearspring.analytics.stream.cardinality.HyperLogLog[numToMerge];
            com.clearspring.analytics.stream.cardinality.HyperLogLog baseline = new com.clearspring.analytics.stream.cardinality.HyperLogLog(bits);
            for (int i = 0; i < numToMerge; i++) {
                hyperLogLogs[i] = new com.clearspring.analytics.stream.cardinality.HyperLogLog(bits);
                for (int j = 0; j < cardinality; j++) {
                    double val = java.lang.Math.random();
                    hyperLogLogs[i].offer(val);
                    baseline.offer(val);
                }
            }
            long expectedCardinality = numToMerge * cardinality;
            com.clearspring.analytics.stream.cardinality.HyperLogLog hll = hyperLogLogs[0];
            hyperLogLogs = java.util.Arrays.asList(hyperLogLogs).subList(1, hyperLogLogs.length).toArray(new com.clearspring.analytics.stream.cardinality.HyperLogLog[0]);
            long mergedEstimate = hll.merge(hyperLogLogs).cardinality();
            long baselineEstimate = baseline.cardinality();
            double se = expectedCardinality * (1.04 / (java.lang.Math.sqrt(java.lang.Math.pow(2, bits))));
            java.lang.System.out.println(("Baseline estimate: " + baselineEstimate));
            java.lang.System.out.println(((((("Expect estimate: " + mergedEstimate) + " is between ") + (expectedCardinality - (3 * se))) + " and ") + (expectedCardinality + (3 * se))));
            // MethodAssertGenerator build local variable
            Object o_38_0 = mergedEstimate >= (expectedCardinality - (3 * se));
            // MethodAssertGenerator build local variable
            Object o_39_0 = mergedEstimate <= (expectedCardinality + (3 * se));
            org.junit.Assert.fail("testMerge_literalMutation18344 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of com.clearspring.analytics.stream.cardinality.TestHyperLogLog#testMerge */
    @org.junit.Test(timeout = 1000)
    public void testMerge_cf18416() throws com.clearspring.analytics.stream.cardinality.CardinalityMergeException {
        int numToMerge = 5;
        int bits = 16;
        int cardinality = 1000000;
        com.clearspring.analytics.stream.cardinality.HyperLogLog[] hyperLogLogs = new com.clearspring.analytics.stream.cardinality.HyperLogLog[numToMerge];
        com.clearspring.analytics.stream.cardinality.HyperLogLog baseline = new com.clearspring.analytics.stream.cardinality.HyperLogLog(bits);
        for (int i = 0; i < numToMerge; i++) {
            hyperLogLogs[i] = new com.clearspring.analytics.stream.cardinality.HyperLogLog(bits);
            for (int j = 0; j < cardinality; j++) {
                double val = java.lang.Math.random();
                hyperLogLogs[i].offer(val);
                baseline.offer(val);
            }
        }
        long expectedCardinality = numToMerge * cardinality;
        com.clearspring.analytics.stream.cardinality.HyperLogLog hll = hyperLogLogs[0];
        hyperLogLogs = java.util.Arrays.asList(hyperLogLogs).subList(1, hyperLogLogs.length).toArray(new com.clearspring.analytics.stream.cardinality.HyperLogLog[0]);
        long mergedEstimate = hll.merge(hyperLogLogs).cardinality();
        long baselineEstimate = baseline.cardinality();
        double se = expectedCardinality * (1.04 / (java.lang.Math.sqrt(java.lang.Math.pow(2, bits))));
        java.lang.System.out.println(("Baseline estimate: " + baselineEstimate));
        java.lang.System.out.println(((((("Expect estimate: " + mergedEstimate) + " is between ") + (expectedCardinality - (3 * se))) + " and ") + (expectedCardinality + (3 * se))));
        org.junit.Assert.assertTrue((mergedEstimate >= (expectedCardinality - (3 * se))));
        org.junit.Assert.assertTrue((mergedEstimate <= (expectedCardinality + (3 * se))));
        // StatementAdderOnAssert create null value
        java.lang.Object vc_3337 = (java.lang.Object)null;
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(vc_3337);
        // AssertGenerator replace invocation
        boolean o_testMerge_cf18416__41 = // StatementAdderMethod cloned existing statement
baseline.offer(vc_3337);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_testMerge_cf18416__41);
        org.junit.Assert.assertEquals(mergedEstimate, baselineEstimate);
    }

    /* amplification of com.clearspring.analytics.stream.cardinality.TestHyperLogLog#testMerge */
    @org.junit.Test(timeout = 1000)
    public void testMerge_cf18547() throws com.clearspring.analytics.stream.cardinality.CardinalityMergeException {
        int numToMerge = 5;
        int bits = 16;
        int cardinality = 1000000;
        com.clearspring.analytics.stream.cardinality.HyperLogLog[] hyperLogLogs = new com.clearspring.analytics.stream.cardinality.HyperLogLog[numToMerge];
        com.clearspring.analytics.stream.cardinality.HyperLogLog baseline = new com.clearspring.analytics.stream.cardinality.HyperLogLog(bits);
        for (int i = 0; i < numToMerge; i++) {
            hyperLogLogs[i] = new com.clearspring.analytics.stream.cardinality.HyperLogLog(bits);
            for (int j = 0; j < cardinality; j++) {
                double val = java.lang.Math.random();
                hyperLogLogs[i].offer(val);
                baseline.offer(val);
            }
        }
        long expectedCardinality = numToMerge * cardinality;
        com.clearspring.analytics.stream.cardinality.HyperLogLog hll = hyperLogLogs[0];
        hyperLogLogs = java.util.Arrays.asList(hyperLogLogs).subList(1, hyperLogLogs.length).toArray(new com.clearspring.analytics.stream.cardinality.HyperLogLog[0]);
        long mergedEstimate = hll.merge(hyperLogLogs).cardinality();
        long baselineEstimate = baseline.cardinality();
        double se = expectedCardinality * (1.04 / (java.lang.Math.sqrt(java.lang.Math.pow(2, bits))));
        java.lang.System.out.println(("Baseline estimate: " + baselineEstimate));
        java.lang.System.out.println(((((("Expect estimate: " + mergedEstimate) + " is between ") + (expectedCardinality - (3 * se))) + " and ") + (expectedCardinality + (3 * se))));
        org.junit.Assert.assertTrue((mergedEstimate >= (expectedCardinality - (3 * se))));
        org.junit.Assert.assertTrue((mergedEstimate <= (expectedCardinality + (3 * se))));
        // AssertGenerator replace invocation
        int o_testMerge_cf18547__39 = // StatementAdderMethod cloned existing statement
baseline.sizeof();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(o_testMerge_cf18547__39, 43692);
        org.junit.Assert.assertEquals(mergedEstimate, baselineEstimate);
    }

    /**
     * should not fail with HyperLogLogMergeException: "Cannot merge estimators of different sizes"
     */
    /* amplification of com.clearspring.analytics.stream.cardinality.TestHyperLogLog#testMergeWithRegisterSet */
    @org.junit.Test
    public void testMergeWithRegisterSet_literalMutation34097_failAssert0() throws com.clearspring.analytics.stream.cardinality.CardinalityMergeException {
        // AssertGenerator generate try/catch block with fail statement
        try {
            com.clearspring.analytics.stream.cardinality.HyperLogLog first = new com.clearspring.analytics.stream.cardinality.HyperLogLog(32, new com.clearspring.analytics.stream.cardinality.RegisterSet((1 << 20)));
            com.clearspring.analytics.stream.cardinality.HyperLogLog second = new com.clearspring.analytics.stream.cardinality.HyperLogLog(16, new com.clearspring.analytics.stream.cardinality.RegisterSet((1 << 20)));
            first.offer(0);
            second.offer(1);
            first.merge(second);
            org.junit.Assert.fail("testMergeWithRegisterSet_literalMutation34097 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /**
     * should not fail with HyperLogLogMergeException: "Cannot merge estimators of different sizes"
     */
    /* amplification of com.clearspring.analytics.stream.cardinality.TestHyperLogLog#testMergeWithRegisterSet */
    @org.junit.Test
    public void testMergeWithRegisterSet_literalMutation34103_failAssert3() throws com.clearspring.analytics.stream.cardinality.CardinalityMergeException {
        // AssertGenerator generate try/catch block with fail statement
        try {
            com.clearspring.analytics.stream.cardinality.HyperLogLog first = new com.clearspring.analytics.stream.cardinality.HyperLogLog(16, new com.clearspring.analytics.stream.cardinality.RegisterSet((0 << 20)));
            com.clearspring.analytics.stream.cardinality.HyperLogLog second = new com.clearspring.analytics.stream.cardinality.HyperLogLog(16, new com.clearspring.analytics.stream.cardinality.RegisterSet((1 << 20)));
            first.offer(0);
            second.offer(1);
            first.merge(second);
            org.junit.Assert.fail("testMergeWithRegisterSet_literalMutation34103 should have thrown HyperLogLogMergeException");
        } catch (com.clearspring.analytics.stream.cardinality.HyperLogLog.HyperLogLogMergeException eee) {
        }
    }

    /**
     * should not fail with HyperLogLogMergeException: "Cannot merge estimators of different sizes"
     */
    /* amplification of com.clearspring.analytics.stream.cardinality.TestHyperLogLog#testMergeWithRegisterSet */
    @org.junit.Test(timeout = 1000)
    public void testMergeWithRegisterSet_add34094() throws com.clearspring.analytics.stream.cardinality.CardinalityMergeException {
        com.clearspring.analytics.stream.cardinality.HyperLogLog first = new com.clearspring.analytics.stream.cardinality.HyperLogLog(16, new com.clearspring.analytics.stream.cardinality.RegisterSet((1 << 20)));
        com.clearspring.analytics.stream.cardinality.HyperLogLog second = new com.clearspring.analytics.stream.cardinality.HyperLogLog(16, new com.clearspring.analytics.stream.cardinality.RegisterSet((1 << 20)));
        first.offer(0);
        // AssertGenerator replace invocation
        boolean o_testMergeWithRegisterSet_add34094__8 = // MethodCallAdder
second.offer(1);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_testMergeWithRegisterSet_add34094__8);
        second.offer(1);
        first.merge(second);
    }

    /**
     * should not fail with HyperLogLogMergeException: "Cannot merge estimators of different sizes"
     */
    /* amplification of com.clearspring.analytics.stream.cardinality.TestHyperLogLog#testMergeWithRegisterSet */
    @org.junit.Test
    public void testMergeWithRegisterSet_literalMutation34098() throws com.clearspring.analytics.stream.cardinality.CardinalityMergeException {
        com.clearspring.analytics.stream.cardinality.HyperLogLog first = new com.clearspring.analytics.stream.cardinality.HyperLogLog(0, new com.clearspring.analytics.stream.cardinality.RegisterSet((1 << 20)));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((com.clearspring.analytics.stream.cardinality.HyperLogLog)first).sizeof(), 699052);
        com.clearspring.analytics.stream.cardinality.HyperLogLog second = new com.clearspring.analytics.stream.cardinality.HyperLogLog(16, new com.clearspring.analytics.stream.cardinality.RegisterSet((1 << 20)));
        first.offer(0);
        second.offer(1);
        first.merge(second);
    }

    /**
     * should not fail with HyperLogLogMergeException: "Cannot merge estimators of different sizes"
     */
    /* amplification of com.clearspring.analytics.stream.cardinality.TestHyperLogLog#testMergeWithRegisterSet */
    @org.junit.Test
    public void testMergeWithRegisterSet_literalMutation34111_failAssert10() throws com.clearspring.analytics.stream.cardinality.CardinalityMergeException {
        // AssertGenerator generate try/catch block with fail statement
        try {
            com.clearspring.analytics.stream.cardinality.HyperLogLog first = new com.clearspring.analytics.stream.cardinality.HyperLogLog(16, new com.clearspring.analytics.stream.cardinality.RegisterSet((1 << 20)));
            com.clearspring.analytics.stream.cardinality.HyperLogLog second = new com.clearspring.analytics.stream.cardinality.HyperLogLog(0, new com.clearspring.analytics.stream.cardinality.RegisterSet((1 << 20)));
            first.offer(0);
            second.offer(1);
            first.merge(second);
            org.junit.Assert.fail("testMergeWithRegisterSet_literalMutation34111 should have thrown ArrayIndexOutOfBoundsException");
        } catch (java.lang.ArrayIndexOutOfBoundsException eee) {
        }
    }

    /**
     * should not fail with HyperLogLogMergeException: "Cannot merge estimators of different sizes"
     */
    /* amplification of com.clearspring.analytics.stream.cardinality.TestHyperLogLog#testMergeWithRegisterSet */
    @org.junit.Test
    public void testMergeWithRegisterSet_literalMutation34102_failAssert2_literalMutation34387_failAssert55() throws com.clearspring.analytics.stream.cardinality.CardinalityMergeException {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // AssertGenerator generate try/catch block with fail statement
            try {
                com.clearspring.analytics.stream.cardinality.HyperLogLog first = new com.clearspring.analytics.stream.cardinality.HyperLogLog(32, new com.clearspring.analytics.stream.cardinality.RegisterSet((0 << 20)));
                com.clearspring.analytics.stream.cardinality.HyperLogLog second = new com.clearspring.analytics.stream.cardinality.HyperLogLog(16, new com.clearspring.analytics.stream.cardinality.RegisterSet((1 << 20)));
                first.offer(0);
                second.offer(1);
                first.merge(second);
                org.junit.Assert.fail("testMergeWithRegisterSet_literalMutation34102 should have thrown HyperLogLogMergeException");
            } catch (com.clearspring.analytics.stream.cardinality.HyperLogLog.HyperLogLogMergeException eee) {
            }
            org.junit.Assert.fail("testMergeWithRegisterSet_literalMutation34102_failAssert2_literalMutation34387 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /**
     * should not fail with HyperLogLogMergeException: "Cannot merge estimators of different sizes"
     */
    /* amplification of com.clearspring.analytics.stream.cardinality.TestHyperLogLog#testMergeWithRegisterSet */
    @org.junit.Test(timeout = 1000)
    public void testMergeWithRegisterSet_literalMutation34111_failAssert10_add34645() throws com.clearspring.analytics.stream.cardinality.CardinalityMergeException {
        // AssertGenerator generate try/catch block with fail statement
        try {
            com.clearspring.analytics.stream.cardinality.HyperLogLog first = new com.clearspring.analytics.stream.cardinality.HyperLogLog(16, new com.clearspring.analytics.stream.cardinality.RegisterSet((1 << 20)));
            com.clearspring.analytics.stream.cardinality.HyperLogLog second = new com.clearspring.analytics.stream.cardinality.HyperLogLog(0, new com.clearspring.analytics.stream.cardinality.RegisterSet((1 << 20)));
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((com.clearspring.analytics.stream.cardinality.HyperLogLog)second).sizeof(), 699052);
            // AssertGenerator replace invocation
            boolean o_testMergeWithRegisterSet_literalMutation34111_failAssert10_add34645__10 = // MethodCallAdder
first.offer(0);
            // AssertGenerator add assertion
            org.junit.Assert.assertTrue(o_testMergeWithRegisterSet_literalMutation34111_failAssert10_add34645__10);
            first.offer(0);
            second.offer(1);
            first.merge(second);
            org.junit.Assert.fail("testMergeWithRegisterSet_literalMutation34111 should have thrown ArrayIndexOutOfBoundsException");
        } catch (java.lang.ArrayIndexOutOfBoundsException eee) {
        }
    }

    /**
     * should not fail with HyperLogLogMergeException: "Cannot merge estimators of different sizes"
     */
    /* amplification of com.clearspring.analytics.stream.cardinality.TestHyperLogLog#testMergeWithRegisterSet */
    @org.junit.Test
    public void testMergeWithRegisterSet_literalMutation34120_failAssert17_literalMutation34919() throws com.clearspring.analytics.stream.cardinality.CardinalityMergeException {
        // AssertGenerator generate try/catch block with fail statement
        try {
            com.clearspring.analytics.stream.cardinality.HyperLogLog first = new com.clearspring.analytics.stream.cardinality.HyperLogLog(16, new com.clearspring.analytics.stream.cardinality.RegisterSet((1 << 19)));
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((com.clearspring.analytics.stream.cardinality.HyperLogLog)first).sizeof(), 349528);
            com.clearspring.analytics.stream.cardinality.HyperLogLog second = new com.clearspring.analytics.stream.cardinality.HyperLogLog(16, new com.clearspring.analytics.stream.cardinality.RegisterSet((1 << 40)));
            first.offer(0);
            second.offer(1);
            first.merge(second);
            org.junit.Assert.fail("testMergeWithRegisterSet_literalMutation34120 should have thrown ArrayIndexOutOfBoundsException");
        } catch (java.lang.ArrayIndexOutOfBoundsException eee) {
        }
    }

    /**
     * should not fail with HyperLogLogMergeException: "Cannot merge estimators of different sizes"
     */
    /* amplification of com.clearspring.analytics.stream.cardinality.TestHyperLogLog#testMergeWithRegisterSet */
    @org.junit.Test
    public void testMergeWithRegisterSet_literalMutation34112_literalMutation34682_failAssert20() throws com.clearspring.analytics.stream.cardinality.CardinalityMergeException {
        // AssertGenerator generate try/catch block with fail statement
        try {
            com.clearspring.analytics.stream.cardinality.HyperLogLog first = new com.clearspring.analytics.stream.cardinality.HyperLogLog(16, new com.clearspring.analytics.stream.cardinality.RegisterSet((0 << 20)));
            com.clearspring.analytics.stream.cardinality.HyperLogLog second = new com.clearspring.analytics.stream.cardinality.HyperLogLog(17, new com.clearspring.analytics.stream.cardinality.RegisterSet((1 << 20)));
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((com.clearspring.analytics.stream.cardinality.HyperLogLog)second).sizeof(), 699052);
            first.offer(0);
            second.offer(1);
            first.merge(second);
            org.junit.Assert.fail("testMergeWithRegisterSet_literalMutation34112_literalMutation34682 should have thrown HyperLogLogMergeException");
        } catch (com.clearspring.analytics.stream.cardinality.HyperLogLog.HyperLogLogMergeException eee) {
        }
    }

    /**
     * should not fail with HyperLogLogMergeException: "Cannot merge estimators of different sizes"
     */
    /* amplification of com.clearspring.analytics.stream.cardinality.TestHyperLogLog#testMergeWithRegisterSet */
    @org.junit.Test
    public void testMergeWithRegisterSet_literalMutation34117_failAssert14_literalMutation34828() throws com.clearspring.analytics.stream.cardinality.CardinalityMergeException {
        // AssertGenerator generate try/catch block with fail statement
        try {
            com.clearspring.analytics.stream.cardinality.HyperLogLog first = new com.clearspring.analytics.stream.cardinality.HyperLogLog(15, new com.clearspring.analytics.stream.cardinality.RegisterSet((1 << 20)));
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((com.clearspring.analytics.stream.cardinality.HyperLogLog)first).sizeof(), 699052);
            com.clearspring.analytics.stream.cardinality.HyperLogLog second = new com.clearspring.analytics.stream.cardinality.HyperLogLog(16, new com.clearspring.analytics.stream.cardinality.RegisterSet((1 << 0)));
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((com.clearspring.analytics.stream.cardinality.HyperLogLog)second).cardinality(), 3097908906L);
            first.offer(0);
            second.offer(1);
            first.merge(second);
            org.junit.Assert.fail("testMergeWithRegisterSet_literalMutation34117 should have thrown ArrayIndexOutOfBoundsException");
        } catch (java.lang.ArrayIndexOutOfBoundsException eee) {
        }
    }

    /**
     * should not fail with HyperLogLogMergeException: "Cannot merge estimators of different sizes"
     */
    /* amplification of com.clearspring.analytics.stream.cardinality.TestHyperLogLog#testMergeWithRegisterSet */
    @org.junit.Test
    public void testMergeWithRegisterSet_literalMutation34098_literalMutation34284_failAssert7() throws com.clearspring.analytics.stream.cardinality.CardinalityMergeException {
        // AssertGenerator generate try/catch block with fail statement
        try {
            com.clearspring.analytics.stream.cardinality.HyperLogLog first = new com.clearspring.analytics.stream.cardinality.HyperLogLog(0, new com.clearspring.analytics.stream.cardinality.RegisterSet((1 << 20)));
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((com.clearspring.analytics.stream.cardinality.HyperLogLog)first).sizeof(), 699052);
            com.clearspring.analytics.stream.cardinality.HyperLogLog second = new com.clearspring.analytics.stream.cardinality.HyperLogLog(0, new com.clearspring.analytics.stream.cardinality.RegisterSet((1 << 20)));
            first.offer(0);
            second.offer(1);
            first.merge(second);
            org.junit.Assert.fail("testMergeWithRegisterSet_literalMutation34098_literalMutation34284 should have thrown ArrayIndexOutOfBoundsException");
        } catch (java.lang.ArrayIndexOutOfBoundsException eee) {
        }
    }

    /**
     * should not fail with HyperLogLogMergeException: "Cannot merge estimators of different sizes"
     */
    /* amplification of com.clearspring.analytics.stream.cardinality.TestHyperLogLog#testMergeWithRegisterSet */
    @org.junit.Test(timeout = 1000)
    public void testMergeWithRegisterSet_add34093_add34122() throws com.clearspring.analytics.stream.cardinality.CardinalityMergeException {
        com.clearspring.analytics.stream.cardinality.HyperLogLog first = new com.clearspring.analytics.stream.cardinality.HyperLogLog(16, new com.clearspring.analytics.stream.cardinality.RegisterSet((1 << 20)));
        com.clearspring.analytics.stream.cardinality.HyperLogLog second = new com.clearspring.analytics.stream.cardinality.HyperLogLog(16, new com.clearspring.analytics.stream.cardinality.RegisterSet((1 << 20)));
        // AssertGenerator replace invocation
        boolean o_testMergeWithRegisterSet_add34093__7 = // MethodCallAdder
first.offer(0);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_testMergeWithRegisterSet_add34093__7);
        // AssertGenerator replace invocation
        boolean o_testMergeWithRegisterSet_add34093_add34122__11 = // MethodCallAdder
first.offer(0);
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_testMergeWithRegisterSet_add34093_add34122__11);
        first.offer(0);
        second.offer(1);
        first.merge(second);
    }

    /**
     * should not fail with HyperLogLogMergeException: "Cannot merge estimators of different sizes"
     */
    /* amplification of com.clearspring.analytics.stream.cardinality.TestHyperLogLog#testMergeWithRegisterSet */
    @org.junit.Test(timeout = 1000)
    public void testMergeWithRegisterSet_add34094_literalMutation34174_failAssert61_literalMutation37884() throws com.clearspring.analytics.stream.cardinality.CardinalityMergeException {
        // AssertGenerator generate try/catch block with fail statement
        try {
            com.clearspring.analytics.stream.cardinality.HyperLogLog first = new com.clearspring.analytics.stream.cardinality.HyperLogLog(16, new com.clearspring.analytics.stream.cardinality.RegisterSet((1 << 20)));
            com.clearspring.analytics.stream.cardinality.HyperLogLog second = new com.clearspring.analytics.stream.cardinality.HyperLogLog(0, new com.clearspring.analytics.stream.cardinality.RegisterSet((21 << 20)));
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((com.clearspring.analytics.stream.cardinality.HyperLogLog)second).sizeof(), 14680064);
            first.offer(0);
            // AssertGenerator replace invocation
            boolean o_testMergeWithRegisterSet_add34094__8 = // MethodCallAdder
second.offer(1);
            // AssertGenerator add assertion
            org.junit.Assert.assertTrue(o_testMergeWithRegisterSet_add34094__8);
            second.offer(1);
            first.merge(second);
            org.junit.Assert.fail("testMergeWithRegisterSet_add34094_literalMutation34174 should have thrown ArrayIndexOutOfBoundsException");
        } catch (java.lang.ArrayIndexOutOfBoundsException eee) {
        }
    }

    /**
     * should not fail with HyperLogLogMergeException: "Cannot merge estimators of different sizes"
     */
    /* amplification of com.clearspring.analytics.stream.cardinality.TestHyperLogLog#testMergeWithRegisterSet */
    @org.junit.Test
    public void testMergeWithRegisterSet_literalMutation34096_literalMutation34236_failAssert17_literalMutation35846() throws com.clearspring.analytics.stream.cardinality.CardinalityMergeException {
        // AssertGenerator generate try/catch block with fail statement
        try {
            com.clearspring.analytics.stream.cardinality.HyperLogLog first = new com.clearspring.analytics.stream.cardinality.HyperLogLog(0, new com.clearspring.analytics.stream.cardinality.RegisterSet((1 << 20)));
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((com.clearspring.analytics.stream.cardinality.HyperLogLog)first).sizeof(), 699052);
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((com.clearspring.analytics.stream.cardinality.HyperLogLog)first).sizeof(), 699052);
            com.clearspring.analytics.stream.cardinality.HyperLogLog second = new com.clearspring.analytics.stream.cardinality.HyperLogLog(16, new com.clearspring.analytics.stream.cardinality.RegisterSet((1 << 40)));
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((com.clearspring.analytics.stream.cardinality.HyperLogLog)second).cardinality(), 12101207L);
            first.offer(0);
            second.offer(1);
            first.merge(second);
            org.junit.Assert.fail("testMergeWithRegisterSet_literalMutation34096_literalMutation34236 should have thrown ArrayIndexOutOfBoundsException");
        } catch (java.lang.ArrayIndexOutOfBoundsException eee) {
        }
    }

    /**
     * should not fail with HyperLogLogMergeException: "Cannot merge estimators of different sizes"
     */
    /* amplification of com.clearspring.analytics.stream.cardinality.TestHyperLogLog#testMergeWithRegisterSet */
    @org.junit.Test
    public void testMergeWithRegisterSet_literalMutation34099_literalMutation34320_failAssert6_literalMutation35308_failAssert38() throws com.clearspring.analytics.stream.cardinality.CardinalityMergeException {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // AssertGenerator generate try/catch block with fail statement
            try {
                com.clearspring.analytics.stream.cardinality.HyperLogLog first = new com.clearspring.analytics.stream.cardinality.HyperLogLog(17, new com.clearspring.analytics.stream.cardinality.RegisterSet((1 << 20)));
                // AssertGenerator add assertion
                org.junit.Assert.assertEquals(((com.clearspring.analytics.stream.cardinality.HyperLogLog)first).sizeof(), 699052);
                com.clearspring.analytics.stream.cardinality.HyperLogLog second = new com.clearspring.analytics.stream.cardinality.HyperLogLog(16, new com.clearspring.analytics.stream.cardinality.RegisterSet((0 << 19)));
                first.offer(0);
                second.offer(1);
                first.merge(second);
                org.junit.Assert.fail("testMergeWithRegisterSet_literalMutation34099_literalMutation34320 should have thrown HyperLogLogMergeException");
            } catch (com.clearspring.analytics.stream.cardinality.HyperLogLog.HyperLogLogMergeException eee) {
            }
            org.junit.Assert.fail("testMergeWithRegisterSet_literalMutation34099_literalMutation34320_failAssert6_literalMutation35308 should have thrown ArrayIndexOutOfBoundsException");
        } catch (java.lang.ArrayIndexOutOfBoundsException eee) {
        }
    }

    /**
     * should not fail with HyperLogLogMergeException: "Cannot merge estimators of different sizes"
     */
    /* amplification of com.clearspring.analytics.stream.cardinality.TestHyperLogLog#testMergeWithRegisterSet */
    @org.junit.Test(timeout = 1000)
    public void testMergeWithRegisterSet_literalMutation34113_add34702_literalMutation35512_failAssert23() throws com.clearspring.analytics.stream.cardinality.CardinalityMergeException {
        // AssertGenerator generate try/catch block with fail statement
        try {
            com.clearspring.analytics.stream.cardinality.HyperLogLog first = new com.clearspring.analytics.stream.cardinality.HyperLogLog(16, new com.clearspring.analytics.stream.cardinality.RegisterSet((1 << 20)));
            com.clearspring.analytics.stream.cardinality.HyperLogLog second = new com.clearspring.analytics.stream.cardinality.HyperLogLog(0, new com.clearspring.analytics.stream.cardinality.RegisterSet((1 << 20)));
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((com.clearspring.analytics.stream.cardinality.HyperLogLog)second).sizeof(), 699052);
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((com.clearspring.analytics.stream.cardinality.HyperLogLog)second).sizeof(), 699052);
            // AssertGenerator replace invocation
            boolean o_testMergeWithRegisterSet_literalMutation34113_add34702__10 = // MethodCallAdder
first.offer(0);
            // AssertGenerator add assertion
            org.junit.Assert.assertTrue(o_testMergeWithRegisterSet_literalMutation34113_add34702__10);
            first.offer(0);
            second.offer(1);
            first.merge(second);
            org.junit.Assert.fail("testMergeWithRegisterSet_literalMutation34113_add34702_literalMutation35512 should have thrown ArrayIndexOutOfBoundsException");
        } catch (java.lang.ArrayIndexOutOfBoundsException eee) {
        }
    }

    /**
     * should not fail with HyperLogLogMergeException: "Cannot merge estimators of different sizes"
     */
    /* amplification of com.clearspring.analytics.stream.cardinality.TestHyperLogLog#testMergeWithRegisterSet */
    @org.junit.Test(timeout = 1000)
    public void testMergeWithRegisterSet_add34093_literalMutation34143_literalMutation35042_failAssert47() throws com.clearspring.analytics.stream.cardinality.CardinalityMergeException {
        // AssertGenerator generate try/catch block with fail statement
        try {
            com.clearspring.analytics.stream.cardinality.HyperLogLog first = new com.clearspring.analytics.stream.cardinality.HyperLogLog(16, new com.clearspring.analytics.stream.cardinality.RegisterSet((1 << 3)));
            com.clearspring.analytics.stream.cardinality.HyperLogLog second = new com.clearspring.analytics.stream.cardinality.HyperLogLog(2, new com.clearspring.analytics.stream.cardinality.RegisterSet((1 << 20)));
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((com.clearspring.analytics.stream.cardinality.HyperLogLog)second).sizeof(), 699052);
            // AssertGenerator replace invocation
            boolean o_testMergeWithRegisterSet_add34093__7 = // MethodCallAdder
first.offer(0);
            // AssertGenerator add assertion
            org.junit.Assert.assertTrue(o_testMergeWithRegisterSet_add34093__7);
            first.offer(0);
            second.offer(1);
            first.merge(second);
            org.junit.Assert.fail("testMergeWithRegisterSet_add34093_literalMutation34143_literalMutation35042 should have thrown HyperLogLogMergeException");
        } catch (com.clearspring.analytics.stream.cardinality.HyperLogLog.HyperLogLogMergeException eee) {
        }
    }

    /**
     * should not fail with HyperLogLogMergeException: "Cannot merge estimators of different sizes"
     */
    /* amplification of com.clearspring.analytics.stream.cardinality.TestHyperLogLog#testMergeWithRegisterSet */
    @org.junit.Test
    public void testMergeWithRegisterSet_literalMutation34104_failAssert4_literalMutation34464_failAssert72_literalMutation38323_failAssert10() throws com.clearspring.analytics.stream.cardinality.CardinalityMergeException {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // AssertGenerator generate try/catch block with fail statement
            try {
                // AssertGenerator generate try/catch block with fail statement
                try {
                    com.clearspring.analytics.stream.cardinality.HyperLogLog first = new com.clearspring.analytics.stream.cardinality.HyperLogLog(32, new com.clearspring.analytics.stream.cardinality.RegisterSet((1 << 0)));
                    com.clearspring.analytics.stream.cardinality.HyperLogLog second = new com.clearspring.analytics.stream.cardinality.HyperLogLog(16, new com.clearspring.analytics.stream.cardinality.RegisterSet((0 << 20)));
                    first.offer(0);
                    second.offer(1);
                    first.merge(second);
                    org.junit.Assert.fail("testMergeWithRegisterSet_literalMutation34104 should have thrown HyperLogLogMergeException");
                } catch (com.clearspring.analytics.stream.cardinality.HyperLogLog.HyperLogLogMergeException eee) {
                }
                org.junit.Assert.fail("testMergeWithRegisterSet_literalMutation34104_failAssert4_literalMutation34464 should have thrown ArrayIndexOutOfBoundsException");
            } catch (java.lang.ArrayIndexOutOfBoundsException eee) {
            }
            org.junit.Assert.fail("testMergeWithRegisterSet_literalMutation34104_failAssert4_literalMutation34464_failAssert72_literalMutation38323 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /**
     * should not fail with HyperLogLogMergeException: "Cannot merge estimators of different sizes"
     */
    /* amplification of com.clearspring.analytics.stream.cardinality.TestHyperLogLog#testMergeWithRegisterSet */
    @org.junit.Test
    public void testMergeWithRegisterSet_literalMutation34117_failAssert14_literalMutation34828_literalMutation36957() throws com.clearspring.analytics.stream.cardinality.CardinalityMergeException {
        // AssertGenerator generate try/catch block with fail statement
        try {
            com.clearspring.analytics.stream.cardinality.HyperLogLog first = new com.clearspring.analytics.stream.cardinality.HyperLogLog(30, new com.clearspring.analytics.stream.cardinality.RegisterSet((1 << 20)));
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((com.clearspring.analytics.stream.cardinality.HyperLogLog)first).sizeof(), 699052);
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((com.clearspring.analytics.stream.cardinality.HyperLogLog)first).sizeof(), 699052);
            com.clearspring.analytics.stream.cardinality.HyperLogLog second = new com.clearspring.analytics.stream.cardinality.HyperLogLog(16, new com.clearspring.analytics.stream.cardinality.RegisterSet((1 << 0)));
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((com.clearspring.analytics.stream.cardinality.HyperLogLog)second).cardinality(), 3097908906L);
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((com.clearspring.analytics.stream.cardinality.HyperLogLog)second).cardinality(), 3097908906L);
            first.offer(0);
            second.offer(1);
            first.merge(second);
            org.junit.Assert.fail("testMergeWithRegisterSet_literalMutation34117 should have thrown ArrayIndexOutOfBoundsException");
        } catch (java.lang.ArrayIndexOutOfBoundsException eee) {
        }
    }

    /* amplification of com.clearspring.analytics.stream.cardinality.TestHyperLogLog#testSerialization */
    @org.junit.Test(timeout = 1000)
    public void testSerialization_add38787() throws java.io.IOException, java.lang.ClassNotFoundException {
        com.clearspring.analytics.stream.cardinality.HyperLogLog hll = new com.clearspring.analytics.stream.cardinality.HyperLogLog(8);
        hll.offer("a");
        hll.offer("b");
        hll.offer("c");
        // AssertGenerator replace invocation
        boolean o_testSerialization_add38787__6 = // MethodCallAdder
hll.offer("d");
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_testSerialization_add38787__6);
        hll.offer("d");
        hll.offer("e");
        com.clearspring.analytics.stream.cardinality.HyperLogLog hll2 = ((com.clearspring.analytics.stream.cardinality.HyperLogLog) (com.clearspring.analytics.TestUtils.deserialize(com.clearspring.analytics.TestUtils.serialize(hll))));
        org.junit.Assert.assertEquals(hll.cardinality(), hll2.cardinality());
    }

    /* amplification of com.clearspring.analytics.stream.cardinality.TestHyperLogLog#testSerialization */
    @org.junit.Test
    public void testSerialization_literalMutation38793() throws java.io.IOException, java.lang.ClassNotFoundException {
        com.clearspring.analytics.stream.cardinality.HyperLogLog hll = new com.clearspring.analytics.stream.cardinality.HyperLogLog(7);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((com.clearspring.analytics.stream.cardinality.HyperLogLog)hll).cardinality(), 0L);
        // AssertGenerator add assertion
        byte[] array_1747967350 = new byte[]{0, 0, 0, 7, 0, 0, 0, 88, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
	byte[] array_1458406307 = (byte[])((com.clearspring.analytics.stream.cardinality.HyperLogLog)hll).getBytes();
	for(int ii = 0; ii <array_1747967350.length; ii++) {
		org.junit.Assert.assertEquals(array_1747967350[ii], array_1458406307[ii]);
	};
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((com.clearspring.analytics.stream.cardinality.HyperLogLog)hll).sizeof(), 88);
        hll.offer("a");
        hll.offer("b");
        hll.offer("c");
        hll.offer("d");
        hll.offer("e");
        com.clearspring.analytics.stream.cardinality.HyperLogLog hll2 = ((com.clearspring.analytics.stream.cardinality.HyperLogLog) (com.clearspring.analytics.TestUtils.deserialize(com.clearspring.analytics.TestUtils.serialize(hll))));
        org.junit.Assert.assertEquals(hll.cardinality(), hll2.cardinality());
    }

    /* amplification of com.clearspring.analytics.stream.cardinality.TestHyperLogLog#testSerialization */
    @org.junit.Test(timeout = 1000)
    public void testSerialization_cf38813() throws java.io.IOException, java.lang.ClassNotFoundException {
        com.clearspring.analytics.stream.cardinality.HyperLogLog hll = new com.clearspring.analytics.stream.cardinality.HyperLogLog(8);
        hll.offer("a");
        hll.offer("b");
        hll.offer("c");
        hll.offer("d");
        hll.offer("e");
        com.clearspring.analytics.stream.cardinality.HyperLogLog hll2 = ((com.clearspring.analytics.stream.cardinality.HyperLogLog) (com.clearspring.analytics.TestUtils.deserialize(com.clearspring.analytics.TestUtils.serialize(hll))));
        // AssertGenerator replace invocation
        byte[] o_testSerialization_cf38813__11 = // StatementAdderMethod cloned existing statement
hll2.getBytes();
        // AssertGenerator add assertion
        byte[] array_51340384 = new byte[]{0, 0, 0, 8, 0, 0, 0, -84, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, -128, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 4, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 4, 0, 0, 0, 0, 32, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
	byte[] array_1122733226 = (byte[])o_testSerialization_cf38813__11;
	for(int ii = 0; ii <array_51340384.length; ii++) {
		org.junit.Assert.assertEquals(array_51340384[ii], array_1122733226[ii]);
	};
        org.junit.Assert.assertEquals(hll.cardinality(), hll2.cardinality());
    }

    /* amplification of com.clearspring.analytics.stream.cardinality.TestHyperLogLog#testSerializationUsingBuilder */
    @org.junit.Test(timeout = 1000)
    public void testSerializationUsingBuilder_literalMutation41406_cf41774_cf42456() throws java.io.IOException {
        com.clearspring.analytics.stream.cardinality.HyperLogLog hll = new com.clearspring.analytics.stream.cardinality.HyperLogLog(4);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((com.clearspring.analytics.stream.cardinality.HyperLogLog)hll).cardinality(), 0L);
        // AssertGenerator add assertion
        byte[] array_513459518 = new byte[]{0, 0, 0, 4, 0, 0, 0, 12, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
	byte[] array_640596440 = (byte[])((com.clearspring.analytics.stream.cardinality.HyperLogLog)hll).getBytes();
	for(int ii = 0; ii <array_513459518.length; ii++) {
		org.junit.Assert.assertEquals(array_513459518[ii], array_640596440[ii]);
	};
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((com.clearspring.analytics.stream.cardinality.HyperLogLog)hll).sizeof(), 12);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((com.clearspring.analytics.stream.cardinality.HyperLogLog)hll).cardinality(), 0L);
        // AssertGenerator add assertion
        byte[] array_1127198203 = new byte[]{0, 0, 0, 4, 0, 0, 0, 12, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
	byte[] array_1198502758 = (byte[])((com.clearspring.analytics.stream.cardinality.HyperLogLog)hll).getBytes();
	for(int ii = 0; ii <array_1127198203.length; ii++) {
		org.junit.Assert.assertEquals(array_1127198203[ii], array_1198502758[ii]);
	};
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((com.clearspring.analytics.stream.cardinality.HyperLogLog)hll).sizeof(), 12);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((com.clearspring.analytics.stream.cardinality.HyperLogLog)hll).cardinality(), 0L);
        // AssertGenerator add assertion
        byte[] array_71957458 = new byte[]{0, 0, 0, 4, 0, 0, 0, 12, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
	byte[] array_371946983 = (byte[])((com.clearspring.analytics.stream.cardinality.HyperLogLog)hll).getBytes();
	for(int ii = 0; ii <array_71957458.length; ii++) {
		org.junit.Assert.assertEquals(array_71957458[ii], array_371946983[ii]);
	};
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((com.clearspring.analytics.stream.cardinality.HyperLogLog)hll).sizeof(), 12);
        hll.offer("a");
        hll.offer("b");
        hll.offer("c");
        hll.offer("d");
        hll.offer("e");
        com.clearspring.analytics.stream.cardinality.HyperLogLog hll2 = com.clearspring.analytics.stream.cardinality.HyperLogLog.Builder.build(hll.getBytes());
        // StatementAdderOnAssert create random local variable
        long vc_6186 = 2045874576515668626L;
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(vc_6186, 2045874576515668626L);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(vc_6186, 2045874576515668626L);
        // AssertGenerator replace invocation
        boolean o_testSerializationUsingBuilder_literalMutation41406_cf41774__20 = // StatementAdderMethod cloned existing statement
hll2.offerHashed(vc_6186);
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_testSerializationUsingBuilder_literalMutation41406_cf41774__20);
        // AssertGenerator replace invocation
        long o_testSerializationUsingBuilder_literalMutation41406_cf41774_cf42456__32 = // StatementAdderMethod cloned existing statement
hll.cardinality();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(o_testSerializationUsingBuilder_literalMutation41406_cf41774_cf42456__32, 5L);
        org.junit.Assert.assertEquals(hll.cardinality(), hll2.cardinality());
    }

    /* amplification of com.clearspring.analytics.stream.cardinality.TestHyperLogLog#testSerializationUsingBuilder */
    @org.junit.Test(timeout = 1000)
    public void testSerializationUsingBuilder_add41402_add41518_add43389() throws java.io.IOException {
        com.clearspring.analytics.stream.cardinality.HyperLogLog hll = new com.clearspring.analytics.stream.cardinality.HyperLogLog(8);
        // AssertGenerator replace invocation
        boolean o_testSerializationUsingBuilder_add41402_add41518_add43389__3 = // MethodCallAdder
hll.offer("a");
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_testSerializationUsingBuilder_add41402_add41518_add43389__3);
        hll.offer("a");
        // AssertGenerator replace invocation
        boolean o_testSerializationUsingBuilder_add41402__4 = // MethodCallAdder
hll.offer("b");
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_testSerializationUsingBuilder_add41402__4);
        // AssertGenerator replace invocation
        boolean o_testSerializationUsingBuilder_add41402_add41518__8 = // MethodCallAdder
hll.offer("b");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_testSerializationUsingBuilder_add41402_add41518__8);
        hll.offer("b");
        hll.offer("c");
        hll.offer("d");
        hll.offer("e");
        com.clearspring.analytics.stream.cardinality.HyperLogLog hll2 = com.clearspring.analytics.stream.cardinality.HyperLogLog.Builder.build(hll.getBytes());
        org.junit.Assert.assertEquals(hll.cardinality(), hll2.cardinality());
    }

    /* amplification of com.clearspring.analytics.stream.cardinality.TestHyperLogLog#testSerializationUsingBuilder */
    @org.junit.Test(timeout = 1000)
    public void testSerializationUsingBuilder_literalMutation41406_add41749_cf43323() throws java.io.IOException {
        com.clearspring.analytics.stream.cardinality.HyperLogLog hll = new com.clearspring.analytics.stream.cardinality.HyperLogLog(4);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((com.clearspring.analytics.stream.cardinality.HyperLogLog)hll).cardinality(), 0L);
        // AssertGenerator add assertion
        byte[] array_1136783869 = new byte[]{0, 0, 0, 4, 0, 0, 0, 12, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
	byte[] array_276757390 = (byte[])((com.clearspring.analytics.stream.cardinality.HyperLogLog)hll).getBytes();
	for(int ii = 0; ii <array_1136783869.length; ii++) {
		org.junit.Assert.assertEquals(array_1136783869[ii], array_276757390[ii]);
	};
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((com.clearspring.analytics.stream.cardinality.HyperLogLog)hll).sizeof(), 12);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((com.clearspring.analytics.stream.cardinality.HyperLogLog)hll).cardinality(), 0L);
        // AssertGenerator add assertion
        byte[] array_1291997829 = new byte[]{0, 0, 0, 4, 0, 0, 0, 12, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
	byte[] array_151550294 = (byte[])((com.clearspring.analytics.stream.cardinality.HyperLogLog)hll).getBytes();
	for(int ii = 0; ii <array_1291997829.length; ii++) {
		org.junit.Assert.assertEquals(array_1291997829[ii], array_151550294[ii]);
	};
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((com.clearspring.analytics.stream.cardinality.HyperLogLog)hll).sizeof(), 12);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((com.clearspring.analytics.stream.cardinality.HyperLogLog)hll).cardinality(), 0L);
        // AssertGenerator add assertion
        byte[] array_71957458 = new byte[]{0, 0, 0, 4, 0, 0, 0, 12, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
	byte[] array_371946983 = (byte[])((com.clearspring.analytics.stream.cardinality.HyperLogLog)hll).getBytes();
	for(int ii = 0; ii <array_71957458.length; ii++) {
		org.junit.Assert.assertEquals(array_71957458[ii], array_371946983[ii]);
	};
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((com.clearspring.analytics.stream.cardinality.HyperLogLog)hll).sizeof(), 12);
        // AssertGenerator replace invocation
        boolean o_testSerializationUsingBuilder_literalMutation41406_add41749__10 = // MethodCallAdder
hll.offer("a");
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_testSerializationUsingBuilder_literalMutation41406_add41749__10);
        hll.offer("a");
        hll.offer("b");
        hll.offer("c");
        hll.offer("d");
        hll.offer("e");
        com.clearspring.analytics.stream.cardinality.HyperLogLog hll2 = com.clearspring.analytics.stream.cardinality.HyperLogLog.Builder.build(hll.getBytes());
        // AssertGenerator replace invocation
        int o_testSerializationUsingBuilder_literalMutation41406_add41749_cf43323__28 = // StatementAdderMethod cloned existing statement
hll2.sizeof();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(o_testSerializationUsingBuilder_literalMutation41406_add41749_cf43323__28, 12);
        org.junit.Assert.assertEquals(hll.cardinality(), hll2.cardinality());
    }
}

