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


@org.junit.runner.RunWith(org.junit.runners.Parameterized.class)
public class TestICardinalityAmpl {
    private int N = 1000000;

    private com.clearspring.analytics.stream.cardinality.ICardinality cardinalityEstimator;

    private static java.util.Random prng = new java.util.Random();

    private static char[] hex = "0123456789abcdef".toCharArray();

    static int se = 0;

    public TestICardinalityAmpl(com.clearspring.analytics.stream.cardinality.ICardinality cardinalityEstimator) {
        super();
        this.cardinalityEstimator = cardinalityEstimator;
    }

    @org.junit.Test
    public void testOffer() {
        cardinalityEstimator.offer("A");
        cardinalityEstimator.offer("B");
        cardinalityEstimator.offer("C");
        org.junit.Assert.assertFalse(cardinalityEstimator.offer("C"));
        org.junit.Assert.assertFalse(cardinalityEstimator.offer("B"));
        org.junit.Assert.assertFalse(cardinalityEstimator.offer("A"));
        cardinalityEstimator.offer("ABCCBA");
        cardinalityEstimator.offer("CBAABC");
        cardinalityEstimator.offer("ABCABC");
        cardinalityEstimator.offer("CBACBA");
        org.junit.Assert.assertFalse(cardinalityEstimator.offer("ABCCBA"));
    }

    @org.junit.Test
    public void testICardinality() {
        java.lang.System.out.println((("size: " + (cardinalityEstimator.sizeof())) + " bytes"));
        for (int i = 0; i < (N); i++) {
            cardinalityEstimator.offer(com.clearspring.analytics.stream.cardinality.TestICardinalityAmpl.streamElement(i));
        }
        long estimate = cardinalityEstimator.cardinality();
        java.lang.System.out.println(estimate);
        double err = (java.lang.Math.abs((estimate - (N)))) / ((double) (N));
        java.lang.System.out.println(("% Error: " + (err * 100)));
    }

    protected static java.lang.Object streamElement(int i) {
        return java.lang.Long.toHexString(com.clearspring.analytics.stream.cardinality.TestICardinalityAmpl.prng.nextLong());
        // return se++;
    }

    @org.junit.runners.Parameterized.Parameters
    public static java.util.Collection<java.lang.Object[]> regExValues() {
        return java.util.Arrays.asList(// { new LogLog(10) },
        // { new LogLog(12) },
        // { new LogLog(14) },
        new java.lang.Object[][]{ // { new LinearCounting(65536) },
        // { new CountThenEstimate() },
        new java.lang.Object[]{ new com.clearspring.analytics.stream.cardinality.AdaptiveCounting(16) }// { new LogLog(10) },
        // { new LogLog(12) },
        // { new LogLog(14) },
         });
    }
}

