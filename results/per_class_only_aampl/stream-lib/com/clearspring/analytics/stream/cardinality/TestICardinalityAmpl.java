

package com.clearspring.analytics.stream.cardinality;


@org.junit.runner.RunWith(value = org.junit.runners.Parameterized.class)
public class TestICardinalityAmpl {
    private int N = 1000000;

    private com.clearspring.analytics.stream.cardinality.ICardinality cardinalityEstimator;

    private static java.util.Random prng = new java.util.Random();

    private static char[] hex = "0123456789abcdef".toCharArray();

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

    static int se = 0;

    protected static java.lang.Object streamElement(int i) {
        return java.lang.Long.toHexString(com.clearspring.analytics.stream.cardinality.TestICardinalityAmpl.prng.nextLong());
    }

    @org.junit.runners.Parameterized.Parameters
    public static java.util.Collection<java.lang.Object[]> regExValues() {
        return java.util.Arrays.asList(new java.lang.Object[][]{ new java.lang.Object[]{ new com.clearspring.analytics.stream.cardinality.AdaptiveCounting(16) } });
    }
}

