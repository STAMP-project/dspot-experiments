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
/**
 *
 */


package com.clearspring.analytics.stream;


public class TestStochasticTopperAmpl {
    private static final int NUM_ITERATIONS = 100000;

    private static final int NUM_ELEMENTS = 10;

    private com.clearspring.analytics.stream.StochasticTopper<java.lang.Integer> vs;

    private java.util.Random random;

    @org.junit.Before
    public void setUp() {
        vs = new com.clearspring.analytics.stream.StochasticTopper<java.lang.Integer>(200);
        random = new java.util.Random(340340990L);
    }

    @org.junit.Test
    public void testGaussianDistribution() {
        for (int i = 0; i < (com.clearspring.analytics.stream.TestStochasticTopperAmpl.NUM_ITERATIONS); i++) {
            vs.offer(new java.lang.Integer(((int) (java.lang.Math.round(((random.nextGaussian()) * (com.clearspring.analytics.stream.TestStochasticTopperAmpl.NUM_ELEMENTS)))))));
        }
        java.util.List<java.lang.Integer> top = vs.peek(5);
        java.lang.System.out.println("Gaussian:");
        for (java.lang.Integer e : top) {
            java.lang.System.out.println(e);
        }
        int tippyTop = top.get(0);
        org.junit.Assert.assertTrue(((tippyTop > (-15)) && (tippyTop < 15)));
    }

    @org.junit.Test
    public void testZipfianDistribution() {
        cern.jet.random.engine.RandomEngine re = cern.jet.random.engine.RandomEngine.makeDefault();
        for (int i = 0; i < (com.clearspring.analytics.stream.TestStochasticTopperAmpl.NUM_ITERATIONS); i++) {
            int z = cern.jet.random.Distributions.nextZipfInt(1.2, re);
            vs.offer(z);
        }
        java.util.List<java.lang.Integer> top = vs.peek(5);
        java.lang.System.out.println("Zipfian:");
        for (java.lang.Integer e : top) {
            java.lang.System.out.println(e);
        }
        int tippyTop = top.get(0);
        org.junit.Assert.assertTrue((tippyTop < 3));
    }

    @org.junit.Test
    public void testGeometricDistribution() {
        cern.jet.random.engine.RandomEngine re = cern.jet.random.engine.RandomEngine.makeDefault();
        for (int i = 0; i < (com.clearspring.analytics.stream.TestStochasticTopperAmpl.NUM_ITERATIONS); i++) {
            int z = cern.jet.random.Distributions.nextGeometric(0.25, re);
            vs.offer(z);
        }
        java.util.List<java.lang.Integer> top = vs.peek(5);
        java.lang.System.out.println("Geometric:");
        for (java.lang.Integer e : top) {
            java.lang.System.out.println(e);
        }
        int tippyTop = top.get(0);
        org.junit.Assert.assertTrue((tippyTop < 3));
    }

    @org.junit.Test
    public void testRandomEngine() {
        int[] maxcounts = new int[10];
        int[] counts = new int[20];
        cern.jet.random.engine.RandomEngine re = cern.jet.random.engine.RandomEngine.makeDefault();
        for (int i = 0; i < (com.clearspring.analytics.stream.TestStochasticTopperAmpl.NUM_ITERATIONS); i++) {
            // int z = Distributions.nextZipfInt(1.2D, re);
            int z = cern.jet.random.Distributions.nextGeometric(0.25, re);
            if (z > ((java.lang.Integer.MAX_VALUE) - 9)) {
                (maxcounts[((java.lang.Integer.MAX_VALUE) - z)])++;
            }
            if (z < 20) {
                (counts[z])++;
            }
        }
        for (int i = 0; i < 20; i++) {
            java.lang.System.out.println(((i + ": ") + (counts[i])));
        }
        for (int i = 9; i >= 0; i--) {
            java.lang.System.out.println(((((java.lang.Integer.MAX_VALUE) - i) + ": ") + (maxcounts[i])));
        }
    }

    /* amplification of com.clearspring.analytics.stream.TestStochasticTopper#testGeometricDistribution */
    @org.junit.Test(timeout = 1000)
    public void testGeometricDistribution_cf1459_failAssert0_literalMutation1475() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            cern.jet.random.engine.RandomEngine re = cern.jet.random.engine.RandomEngine.makeDefault();
            for (int i = 0; i < (com.clearspring.analytics.stream.TestStochasticTopperAmpl.NUM_ITERATIONS); i++) {
                int z = cern.jet.random.Distributions.nextGeometric(0.25, re);
                vs.offer(z);
            }
            java.util.List<java.lang.Integer> top = vs.peek(5);
            java.lang.System.out.println("Geometric:");
            for (java.lang.Integer e : top) {
                java.lang.System.out.println(e);
            }
            int tippyTop = top.get(4);
            // StatementAdderOnAssert create null value
            com.clearspring.analytics.stream.StochasticTopper vc_21 = (com.clearspring.analytics.stream.StochasticTopper)null;
            // AssertGenerator add assertion
            junit.framework.Assert.assertNull(vc_21);
            // StatementAdderMethod cloned existing statement
            vc_21.peek(tippyTop);
            // MethodAssertGenerator build local variable
            Object o_23_0 = tippyTop < 3;
            org.junit.Assert.fail("testGeometricDistribution_cf1459 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    /* amplification of com.clearspring.analytics.stream.TestStochasticTopper#testGeometricDistribution */
    @org.junit.Test(timeout = 1000)
    public void testGeometricDistribution_cf1459_failAssert0_literalMutation1476() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            cern.jet.random.engine.RandomEngine re = cern.jet.random.engine.RandomEngine.makeDefault();
            for (int i = 0; i < (com.clearspring.analytics.stream.TestStochasticTopperAmpl.NUM_ITERATIONS); i++) {
                int z = cern.jet.random.Distributions.nextGeometric(0.25, re);
                vs.offer(z);
            }
            java.util.List<java.lang.Integer> top = vs.peek(5);
            java.lang.System.out.println("Geometric:");
            for (java.lang.Integer e : top) {
                java.lang.System.out.println(e);
            }
            int tippyTop = top.get(2);
            // StatementAdderOnAssert create null value
            com.clearspring.analytics.stream.StochasticTopper vc_21 = (com.clearspring.analytics.stream.StochasticTopper)null;
            // AssertGenerator add assertion
            junit.framework.Assert.assertNull(vc_21);
            // StatementAdderMethod cloned existing statement
            vc_21.peek(tippyTop);
            // MethodAssertGenerator build local variable
            Object o_23_0 = tippyTop < 3;
            org.junit.Assert.fail("testGeometricDistribution_cf1459 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    /* amplification of com.clearspring.analytics.stream.TestStochasticTopper#testGeometricDistribution */
    @org.junit.Test(timeout = 1000)
    public void testGeometricDistribution_cf1461_failAssert2_literalMutation1501() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            cern.jet.random.engine.RandomEngine re = cern.jet.random.engine.RandomEngine.makeDefault();
            for (int i = 0; i < (com.clearspring.analytics.stream.TestStochasticTopperAmpl.NUM_ITERATIONS); i++) {
                int z = cern.jet.random.Distributions.nextGeometric(0.25, re);
                vs.offer(z);
            }
            java.util.List<java.lang.Integer> top = vs.peek(5);
            java.lang.System.out.println("Geometric:");
            for (java.lang.Integer e : top) {
                java.lang.System.out.println(e);
            }
            int tippyTop = top.get(4);
            // StatementAdderOnAssert create literal from method
            int int_vc_3 = 5;
            // AssertGenerator add assertion
            junit.framework.Assert.assertEquals(int_vc_3, 5);
            // StatementAdderOnAssert create null value
            com.clearspring.analytics.stream.StochasticTopper vc_21 = (com.clearspring.analytics.stream.StochasticTopper)null;
            // AssertGenerator add assertion
            junit.framework.Assert.assertNull(vc_21);
            // StatementAdderMethod cloned existing statement
            vc_21.peek(int_vc_3);
            // MethodAssertGenerator build local variable
            Object o_25_0 = tippyTop < 3;
            org.junit.Assert.fail("testGeometricDistribution_cf1461 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    /* amplification of com.clearspring.analytics.stream.TestStochasticTopper#testZipfianDistribution */
    @org.junit.Test(timeout = 1000)
    public void testZipfianDistribution_cf7748_failAssert4_literalMutation7811() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            cern.jet.random.engine.RandomEngine re = cern.jet.random.engine.RandomEngine.makeDefault();
            for (int i = 0; i < (com.clearspring.analytics.stream.TestStochasticTopperAmpl.NUM_ITERATIONS); i++) {
                int z = cern.jet.random.Distributions.nextZipfInt(1.2, re);
                vs.offer(z);
            }
            java.util.List<java.lang.Integer> top = vs.peek(5);
            java.lang.System.out.println("Zipfian:");
            for (java.lang.Integer e : top) {
                java.lang.System.out.println(e);
            }
            int tippyTop = top.get(2);
            // StatementAdderOnAssert create literal from method
            int int_vc_5 = 3;
            // AssertGenerator add assertion
            junit.framework.Assert.assertEquals(int_vc_5, 3);
            // StatementAdderOnAssert create null value
            com.clearspring.analytics.stream.StochasticTopper vc_33 = (com.clearspring.analytics.stream.StochasticTopper)null;
            // AssertGenerator add assertion
            junit.framework.Assert.assertNull(vc_33);
            // StatementAdderMethod cloned existing statement
            vc_33.peek(int_vc_5);
            // MethodAssertGenerator build local variable
            Object o_25_0 = tippyTop < 3;
            org.junit.Assert.fail("testZipfianDistribution_cf7748 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    /* amplification of com.clearspring.analytics.stream.TestStochasticTopper#testZipfianDistribution */
    @org.junit.Test(timeout = 1000)
    public void testZipfianDistribution_cf7748_failAssert4_literalMutation7810() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            cern.jet.random.engine.RandomEngine re = cern.jet.random.engine.RandomEngine.makeDefault();
            for (int i = 0; i < (com.clearspring.analytics.stream.TestStochasticTopperAmpl.NUM_ITERATIONS); i++) {
                int z = cern.jet.random.Distributions.nextZipfInt(1.2, re);
                vs.offer(z);
            }
            java.util.List<java.lang.Integer> top = vs.peek(5);
            java.lang.System.out.println("Zipfian:");
            for (java.lang.Integer e : top) {
                java.lang.System.out.println(e);
            }
            int tippyTop = top.get(4);
            // StatementAdderOnAssert create literal from method
            int int_vc_5 = 3;
            // AssertGenerator add assertion
            junit.framework.Assert.assertEquals(int_vc_5, 3);
            // StatementAdderOnAssert create null value
            com.clearspring.analytics.stream.StochasticTopper vc_33 = (com.clearspring.analytics.stream.StochasticTopper)null;
            // AssertGenerator add assertion
            junit.framework.Assert.assertNull(vc_33);
            // StatementAdderMethod cloned existing statement
            vc_33.peek(int_vc_5);
            // MethodAssertGenerator build local variable
            Object o_25_0 = tippyTop < 3;
            org.junit.Assert.fail("testZipfianDistribution_cf7748 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }
}

