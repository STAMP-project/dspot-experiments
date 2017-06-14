

package com.clearspring.analytics.stream.quantile;


public class AmplQDigestTest {
    private double[] actualRankOf(long x, long[] ys) {
        int numSmaller = 0;
        int numEqual = 0;
        for (long y : ys)
            if (y < x)
                numSmaller++;
            
        
        for (long y : ys)
            if (y == x)
                numEqual++;
            
        
        return new double[]{ (1.0 * numSmaller) / (ys.length) , (1.0 * (numSmaller + numEqual)) / (ys.length) };
    }

    /**
     * Test for bug identified and corrected by http://github.com/addthis/stream-lib/pull/52
     */
    @org.junit.Test
    public void testMerge() {
        int compressionFactor = 2;
        long[] aSamples = new long[]{ 0 , 0 , 1 , 0 , 1 , 1 };
        long[] bSamples = new long[]{ 0 , 1 , 0 , 0 , 0 , 3 };
        long[] allSamples = java.util.Arrays.copyOf(aSamples, ((aSamples.length) + (bSamples.length)));
        java.lang.System.arraycopy(bSamples, 0, allSamples, aSamples.length, bSamples.length);
        com.clearspring.analytics.stream.quantile.QDigest a = new com.clearspring.analytics.stream.quantile.QDigest(compressionFactor);
        com.clearspring.analytics.stream.quantile.QDigest b = new com.clearspring.analytics.stream.quantile.QDigest(compressionFactor);
        com.clearspring.analytics.stream.quantile.QDigest c = new com.clearspring.analytics.stream.quantile.QDigest(compressionFactor);
        for (long x : aSamples)
            a.offer(x);
        
        for (long x : bSamples)
            b.offer(x);
        
        for (long x : allSamples)
            c.offer(x);
        
        com.clearspring.analytics.stream.quantile.QDigest ab = com.clearspring.analytics.stream.quantile.QDigest.unionOf(a, b);
        java.lang.System.out.println(("a: " + a));
        java.lang.System.out.println(("b: " + b));
        java.lang.System.out.println(("ab: " + ab));
        java.lang.System.out.println(("c: " + c));
        org.junit.Assert.assertEquals(allSamples.length, c.computeActualSize());
        int logCapacity = 1;
        long max = 0;
        for (long x : allSamples)
            max = java.lang.Math.max(max, x);
        
        for (double scale = 1; scale < max; scale *= compressionFactor , logCapacity++) {
        }
        double eps = logCapacity / compressionFactor;
        for (double q = 0; q <= 1; q += 0.01) {
            long res = c.getQuantile(q);
            double[] actualRank = actualRankOf(res, allSamples);
            org.junit.Assert.assertTrue((((((actualRank[0]) + " .. ") + (actualRank[1])) + " outside error bound for  ") + q), ((q >= ((actualRank[0]) - eps)) && (q <= ((actualRank[1]) + eps))));
        }
    }

    /**
     * Test for bug identified and corrected by http://github.com/addthis/stream-lib/pull/53
     */
    @org.junit.Test
    public void testSerialization() {
        long[] samples = new long[]{ 0 , 20 };
        com.clearspring.analytics.stream.quantile.QDigest digestA = new com.clearspring.analytics.stream.quantile.QDigest(2);
        for (int i = 0; i < (samples.length); i++) {
            digestA.offer(samples[i]);
        }
        byte[] serialized = com.clearspring.analytics.stream.quantile.QDigest.serialize(digestA);
        com.clearspring.analytics.stream.quantile.QDigest deserializedA = com.clearspring.analytics.stream.quantile.QDigest.deserialize(serialized);
        com.clearspring.analytics.stream.quantile.QDigest digestB = new com.clearspring.analytics.stream.quantile.QDigest(2);
        for (int i = 0; i < (samples.length); i++) {
            digestB.offer(samples[i]);
        }
        com.clearspring.analytics.stream.quantile.QDigest.unionOf(digestA, deserializedA);
    }

    @org.junit.Test
    public void testComprehensiveOnMixture() {
        cern.jet.random.engine.RandomEngine r = new cern.jet.random.engine.MersenneTwister64(0);
        cern.jet.random.Normal[] dists = new cern.jet.random.Normal[]{ new cern.jet.random.Normal(100, 50, r) , new cern.jet.random.Normal(150, 20, r) , new cern.jet.random.Normal(500, 300, r) , new cern.jet.random.Normal(10000, 10000, r) , new cern.jet.random.Normal(1200, 300, r) };
        for (int numSamples : new int[]{ 1 , 10 , 100 , 1000 , 10000 }) {
            long[][] samples = new long[dists.length][];
            for (int i = 0; i < (dists.length); ++i) {
                samples[i] = new long[numSamples];
                for (int j = 0; j < (samples[i].length); ++j) {
                    samples[i][j] = ((long) (java.lang.Math.max(0, dists[i].nextDouble())));
                }
            }
            double compressionFactor = 1000;
            int logCapacity = 1;
            long max = 0;
            for (long[] s : samples) {
                for (long x : s)
                    max = java.lang.Math.max(max, x);
                
            }
            for (double scale = 1; scale < max; scale *= 2 , logCapacity++) {
            }
            double eps = logCapacity / compressionFactor;
            com.clearspring.analytics.stream.quantile.QDigest[] digests = new com.clearspring.analytics.stream.quantile.QDigest[dists.length];
            for (int i = 0; i < (digests.length); ++i) {
                digests[i] = new com.clearspring.analytics.stream.quantile.QDigest(compressionFactor);
                for (long x : samples[i]) {
                    digests[i].offer(x);
                    // AssertGenerator add assertion
                    org.junit.Assert.assertFalse(((java.util.ArrayList)((com.clearspring.analytics.stream.quantile.QDigest)digests[i]).toAscRanges()).isEmpty());
                }
                org.junit.Assert.assertEquals(samples[i].length, digests[i].computeActualSize());
            }
            int numTotal = 0;
            for (int i = 0; i < (digests.length); ++i) {
                for (double q = 0; q <= 1; q += 0.01) {
                    long res = digests[i].getQuantile(q);
                    double[] actualRank = actualRankOf(res, samples[i]);
                    org.junit.Assert.assertTrue((((((actualRank[0]) + " .. ") + (actualRank[1])) + " outside error bound for  ") + q), ((q >= ((actualRank[0]) - eps)) && (q <= ((actualRank[1]) + eps))));
                }
                // Test the same on the union of all distributions up to i-th
                numTotal += samples[i].length;
                long[] total = new long[numTotal];
                int offset = 0;
                com.clearspring.analytics.stream.quantile.QDigest totalDigest = new com.clearspring.analytics.stream.quantile.QDigest(compressionFactor);
                long expectedSize = 0;
                for (int j = 0; j <= i; ++j) {
                    java.lang.System.arraycopy(samples[j], 0, total, offset, samples[j].length);
                    offset += samples[j].length;
                    totalDigest = com.clearspring.analytics.stream.quantile.QDigest.unionOf(totalDigest, digests[j]);
                    expectedSize += samples[j].length;
                }
                org.junit.Assert.assertEquals(expectedSize, totalDigest.computeActualSize());
                for (double q = 0; q <= 1; q += 0.01) {
                    long res = totalDigest.getQuantile(q);
                    double[] actualRank = actualRankOf(res, total);
                    org.junit.Assert.assertTrue((((((actualRank[0]) + " .. ") + (actualRank[1])) + " outside error bound for  ") + q), ((q >= ((actualRank[0]) - eps)) && (q <= ((actualRank[1]) + eps))));
                }
            }
        }
    }

    /**
     * Test for bug identified and corrected by http://github.com/addthis/stream-lib/pull/52
     */
    /* amplification of com.clearspring.analytics.stream.quantile.QDigestTest#testMerge */
    @org.junit.Test(timeout = 10000)
    public void testMerge_literalMutation40748_cf46994() {
        int compressionFactor = 2;
        long[] aSamples = new long[]{ 0 , 0 , 1 , 0 , 1 , 1 };
        long[] bSamples = new long[]{ // TestDataMutator on numbers
        2 , 1 , 0 , 0 , 0 , 3 };
        // AssertGenerator add assertion
        long[] array_728492549 = new long[]{2, 1, 0, 0, 0, 3};
	long[] array_621546815 = (long[])bSamples;
	for(int ii = 0; ii <array_728492549.length; ii++) {
		org.junit.Assert.assertEquals(array_728492549[ii], array_621546815[ii]);
	};
        long[] allSamples = java.util.Arrays.copyOf(aSamples, ((aSamples.length) + (bSamples.length)));
        java.lang.System.arraycopy(bSamples, 0, allSamples, aSamples.length, bSamples.length);
        com.clearspring.analytics.stream.quantile.QDigest a = new com.clearspring.analytics.stream.quantile.QDigest(compressionFactor);
        com.clearspring.analytics.stream.quantile.QDigest b = new com.clearspring.analytics.stream.quantile.QDigest(compressionFactor);
        com.clearspring.analytics.stream.quantile.QDigest c = new com.clearspring.analytics.stream.quantile.QDigest(compressionFactor);
        for (long x : aSamples)
            a.offer(x);
        
        for (long x : bSamples)
            b.offer(x);
        
        for (long x : allSamples)
            c.offer(x);
        
        com.clearspring.analytics.stream.quantile.QDigest ab = com.clearspring.analytics.stream.quantile.QDigest.unionOf(a, b);
        java.lang.System.out.println(("a: " + a));
        java.lang.System.out.println(("b: " + b));
        java.lang.System.out.println(("ab: " + ab));
        java.lang.System.out.println(("c: " + c));
        org.junit.Assert.assertEquals(allSamples.length, c.computeActualSize());
        int logCapacity = 1;
        long max = 0;
        for (long x : allSamples)
            max = java.lang.Math.max(max, x);
        
        for (double scale = 1; scale < max; scale *= compressionFactor , logCapacity++) {
        }
        double eps = logCapacity / compressionFactor;
        for (double q = 0; q <= 1; q += 0.01) {
            long res = c.getQuantile(q);
            double[] actualRank = actualRankOf(res, allSamples);
            // StatementAddOnAssert local variable replacement
            com.clearspring.analytics.stream.quantile.QDigest totalDigest = new com.clearspring.analytics.stream.quantile.QDigest(compressionFactor);
            // AssertGenerator add assertion
            java.util.ArrayList collection_165709559 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_165709559, ((com.clearspring.analytics.stream.quantile.QDigest)totalDigest).toAscRanges());;
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((com.clearspring.analytics.stream.quantile.QDigest)totalDigest).computeActualSize(), 0L);
            // StatementAdderOnAssert create null value
            com.clearspring.analytics.stream.quantile.QDigest vc_4730 = (com.clearspring.analytics.stream.quantile.QDigest)null;
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(vc_4730);
            // AssertGenerator replace invocation
            byte[] o_testMerge_literalMutation40748_cf46994__60 = // StatementAdderMethod cloned existing statement
vc_4730.serialize(totalDigest);
            // AssertGenerator add assertion
            byte[] array_760736290 = new byte[]{0, 0, 0, 0, 0, 0, 0, 0, 64, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0};
	byte[] array_1656178734 = (byte[])o_testMerge_literalMutation40748_cf46994__60;
	for(int ii = 0; ii <array_760736290.length; ii++) {
		org.junit.Assert.assertEquals(array_760736290[ii], array_1656178734[ii]);
	};
            org.junit.Assert.assertTrue((((((actualRank[0]) + " .. ") + (actualRank[1])) + " outside error bound for  ") + q), ((q >= ((actualRank[0]) - eps)) && (q <= ((actualRank[1]) + eps))));
        }
    }

    /**
     * Test for bug identified and corrected by http://github.com/addthis/stream-lib/pull/52
     */
    /* amplification of com.clearspring.analytics.stream.quantile.QDigestTest#testMerge */
    @org.junit.Test(timeout = 10000)
    public void testMerge_literalMutation40728_cf43674() {
        int compressionFactor = 2;
        long[] aSamples = new long[]{ 0 , // TestDataMutator on numbers
        1 , 1 , 0 , 1 , 1 };
        // AssertGenerator add assertion
        long[] array_1404378431 = new long[]{0, 1, 1, 0, 1, 1};
	long[] array_1931970813 = (long[])aSamples;
	for(int ii = 0; ii <array_1404378431.length; ii++) {
		org.junit.Assert.assertEquals(array_1404378431[ii], array_1931970813[ii]);
	};
        long[] bSamples = new long[]{ 0 , 1 , 0 , 0 , 0 , 3 };
        long[] allSamples = java.util.Arrays.copyOf(aSamples, ((aSamples.length) + (bSamples.length)));
        java.lang.System.arraycopy(bSamples, 0, allSamples, aSamples.length, bSamples.length);
        com.clearspring.analytics.stream.quantile.QDigest a = new com.clearspring.analytics.stream.quantile.QDigest(compressionFactor);
        com.clearspring.analytics.stream.quantile.QDigest b = new com.clearspring.analytics.stream.quantile.QDigest(compressionFactor);
        com.clearspring.analytics.stream.quantile.QDigest c = new com.clearspring.analytics.stream.quantile.QDigest(compressionFactor);
        for (long x : aSamples)
            a.offer(x);
        
        for (long x : bSamples)
            b.offer(x);
        
        for (long x : allSamples)
            c.offer(x);
        
        com.clearspring.analytics.stream.quantile.QDigest ab = com.clearspring.analytics.stream.quantile.QDigest.unionOf(a, b);
        java.lang.System.out.println(("a: " + a));
        java.lang.System.out.println(("b: " + b));
        java.lang.System.out.println(("ab: " + ab));
        java.lang.System.out.println(("c: " + c));
        org.junit.Assert.assertEquals(allSamples.length, c.computeActualSize());
        int logCapacity = 1;
        long max = 0;
        for (long x : allSamples)
            max = java.lang.Math.max(max, x);
        
        for (double scale = 1; scale < max; scale *= compressionFactor , logCapacity++) {
        }
        double eps = logCapacity / compressionFactor;
        for (double q = 0; q <= 1; q += 0.01) {
            long res = c.getQuantile(q);
            double[] actualRank = actualRankOf(res, allSamples);
            // StatementAddOnAssert local variable replacement
            com.clearspring.analytics.stream.quantile.QDigest totalDigest = new com.clearspring.analytics.stream.quantile.QDigest(compressionFactor);
            // AssertGenerator add assertion
            java.util.ArrayList collection_1504991047 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_1504991047, ((com.clearspring.analytics.stream.quantile.QDigest)totalDigest).toAscRanges());;
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((com.clearspring.analytics.stream.quantile.QDigest)totalDigest).computeActualSize(), 0L);
            // AssertGenerator replace invocation
            byte[] o_testMerge_literalMutation40728_cf43674__58 = // StatementAdderMethod cloned existing statement
totalDigest.serialize(ab);
            // AssertGenerator add assertion
            byte[] array_480198533 = new byte[]{0, 0, 0, 0, 0, 0, 0, 12, 64, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 4, 0, 0, 0, 4, 0, 0, 0, 0, 0, 0, 0, 5, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 4, 0, 0, 0, 0, 0, 0, 0, 4, 0, 0, 0, 0, 0, 0, 0, 2, 0, 0, 0, 0, 0, 0, 0, 6, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 1};
	byte[] array_947475871 = (byte[])o_testMerge_literalMutation40728_cf43674__58;
	for(int ii = 0; ii <array_480198533.length; ii++) {
		org.junit.Assert.assertEquals(array_480198533[ii], array_947475871[ii]);
	};
            org.junit.Assert.assertTrue((((((actualRank[0]) + " .. ") + (actualRank[1])) + " outside error bound for  ") + q), ((q >= ((actualRank[0]) - eps)) && (q <= ((actualRank[1]) + eps))));
        }
    }

    /**
     * Test for bug identified and corrected by http://github.com/addthis/stream-lib/pull/52
     */
    /* amplification of com.clearspring.analytics.stream.quantile.QDigestTest#testMerge */
    @org.junit.Test
    public void testMerge_literalMutation40733_literalMutation44403() {
        int compressionFactor = 4;
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(compressionFactor, 4);
        long[] aSamples = new long[]{ 0 , 0 , // TestDataMutator on numbers
        0 , 0 , 1 , 1 };
        // AssertGenerator add assertion
        long[] array_223360523 = new long[]{0, 0, 0, 0, 1, 1};
	long[] array_1474324541 = (long[])aSamples;
	for(int ii = 0; ii <array_223360523.length; ii++) {
		org.junit.Assert.assertEquals(array_223360523[ii], array_1474324541[ii]);
	};
        long[] bSamples = new long[]{ 0 , 1 , 0 , 0 , 0 , 3 };
        long[] allSamples = java.util.Arrays.copyOf(aSamples, ((aSamples.length) + (bSamples.length)));
        java.lang.System.arraycopy(bSamples, 0, allSamples, aSamples.length, bSamples.length);
        com.clearspring.analytics.stream.quantile.QDigest a = new com.clearspring.analytics.stream.quantile.QDigest(compressionFactor);
        com.clearspring.analytics.stream.quantile.QDigest b = new com.clearspring.analytics.stream.quantile.QDigest(compressionFactor);
        com.clearspring.analytics.stream.quantile.QDigest c = new com.clearspring.analytics.stream.quantile.QDigest(compressionFactor);
        for (long x : aSamples)
            a.offer(x);
        
        for (long x : bSamples)
            b.offer(x);
        
        for (long x : allSamples)
            c.offer(x);
        
        com.clearspring.analytics.stream.quantile.QDigest ab = com.clearspring.analytics.stream.quantile.QDigest.unionOf(a, b);
        java.lang.System.out.println(("a: " + a));
        java.lang.System.out.println(("b: " + b));
        java.lang.System.out.println(("ab: " + ab));
        java.lang.System.out.println(("c: " + c));
        org.junit.Assert.assertEquals(allSamples.length, c.computeActualSize());
        int logCapacity = 1;
        long max = 0;
        for (long x : allSamples)
            max = java.lang.Math.max(max, x);
        
        for (double scale = 1; scale < max; scale *= compressionFactor , logCapacity++) {
        }
        double eps = logCapacity / compressionFactor;
        for (double q = 0; q <= 1; q += 0.01) {
            long res = c.getQuantile(q);
            double[] actualRank = actualRankOf(res, allSamples);
            org.junit.Assert.assertTrue((((((actualRank[0]) + " .. ") + (actualRank[1])) + " outside error bound for  ") + q), ((q >= ((actualRank[0]) - eps)) && (q <= ((actualRank[1]) + eps))));
        }
    }

    /**
     * Test for bug identified and corrected by http://github.com/addthis/stream-lib/pull/52
     */
    /* amplification of com.clearspring.analytics.stream.quantile.QDigestTest#testMerge */
    @org.junit.Test
    public void testMerge_literalMutation40733_literalMutation44493() {
        int compressionFactor = 2;
        long[] aSamples = new long[]{ 0 , 0 , // TestDataMutator on numbers
        0 , 0 , 1 , 1 };
        // AssertGenerator add assertion
        long[] array_1909597682 = new long[]{0, 0, 0, 0, 1, 1};
	long[] array_1675226554 = (long[])aSamples;
	for(int ii = 0; ii <array_1909597682.length; ii++) {
		org.junit.Assert.assertEquals(array_1909597682[ii], array_1675226554[ii]);
	};
        long[] bSamples = new long[]{ 0 , 1 , 0 , 0 , 0 , 3 };
        long[] allSamples = java.util.Arrays.copyOf(aSamples, ((aSamples.length) + (bSamples.length)));
        java.lang.System.arraycopy(bSamples, 0, allSamples, aSamples.length, bSamples.length);
        com.clearspring.analytics.stream.quantile.QDigest a = new com.clearspring.analytics.stream.quantile.QDigest(compressionFactor);
        com.clearspring.analytics.stream.quantile.QDigest b = new com.clearspring.analytics.stream.quantile.QDigest(compressionFactor);
        com.clearspring.analytics.stream.quantile.QDigest c = new com.clearspring.analytics.stream.quantile.QDigest(compressionFactor);
        for (long x : aSamples)
            a.offer(x);
        
        for (long x : bSamples)
            b.offer(x);
        
        for (long x : allSamples)
            c.offer(x);
        
        com.clearspring.analytics.stream.quantile.QDigest ab = com.clearspring.analytics.stream.quantile.QDigest.unionOf(a, b);
        java.lang.System.out.println(("a: " + a));
        java.lang.System.out.println(("b: " + b));
        java.lang.System.out.println(("ab: " + ab));
        java.lang.System.out.println(("c: " + c));
        org.junit.Assert.assertEquals(allSamples.length, c.computeActualSize());
        int logCapacity = 1;
        long max = 0;
        for (long x : allSamples)
            max = java.lang.Math.max(max, x);
        
        for (double scale = 1; scale < max; scale *= compressionFactor , logCapacity++) {
        }
        double eps = logCapacity / compressionFactor;
        for (double q = 0; q <= 1; q += 0.02) {
            long res = c.getQuantile(q);
            double[] actualRank = actualRankOf(res, allSamples);
            org.junit.Assert.assertTrue((((((actualRank[0]) + " .. ") + (actualRank[1])) + " outside error bound for  ") + q), ((q >= ((actualRank[0]) - eps)) && (q <= ((actualRank[1]) + eps))));
        }
    }

    /**
     * Test for bug identified and corrected by http://github.com/addthis/stream-lib/pull/53
     */
    /* amplification of com.clearspring.analytics.stream.quantile.QDigestTest#testSerialization */
    @org.junit.Test
    public void testSerialization_literalMutation66151() {
        long[] samples = new long[]{ 0 , 20 };
        com.clearspring.analytics.stream.quantile.QDigest digestA = new com.clearspring.analytics.stream.quantile.QDigest(4);
        // AssertGenerator add assertion
        java.util.ArrayList collection_355493530 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_355493530, ((com.clearspring.analytics.stream.quantile.QDigest)digestA).toAscRanges());;
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((com.clearspring.analytics.stream.quantile.QDigest)digestA).computeActualSize(), 0L);
        for (int i = 0; i < (samples.length); i++) {
            digestA.offer(samples[i]);
        }
        byte[] serialized = com.clearspring.analytics.stream.quantile.QDigest.serialize(digestA);
        com.clearspring.analytics.stream.quantile.QDigest deserializedA = com.clearspring.analytics.stream.quantile.QDigest.deserialize(serialized);
        com.clearspring.analytics.stream.quantile.QDigest digestB = new com.clearspring.analytics.stream.quantile.QDigest(2);
        for (int i = 0; i < (samples.length); i++) {
            digestB.offer(samples[i]);
        }
        com.clearspring.analytics.stream.quantile.QDigest.unionOf(digestA, deserializedA);
    }

    /**
     * Test for bug identified and corrected by http://github.com/addthis/stream-lib/pull/53
     */
    /* amplification of com.clearspring.analytics.stream.quantile.QDigestTest#testSerialization */
    @org.junit.Test
    public void testSerialization_literalMutation66142_failAssert0() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            long[] samples = new long[]{ // TestDataMutator on numbers
            -1 , 20 };
            com.clearspring.analytics.stream.quantile.QDigest digestA = new com.clearspring.analytics.stream.quantile.QDigest(2);
            for (int i = 0; i < (samples.length); i++) {
                digestA.offer(samples[i]);
            }
            byte[] serialized = com.clearspring.analytics.stream.quantile.QDigest.serialize(digestA);
            com.clearspring.analytics.stream.quantile.QDigest deserializedA = com.clearspring.analytics.stream.quantile.QDigest.deserialize(serialized);
            com.clearspring.analytics.stream.quantile.QDigest digestB = new com.clearspring.analytics.stream.quantile.QDigest(2);
            for (int i = 0; i < (samples.length); i++) {
                digestB.offer(samples[i]);
            }
            com.clearspring.analytics.stream.quantile.QDigest.unionOf(digestA, deserializedA);
            org.junit.Assert.fail("testSerialization_literalMutation66142 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /**
     * Test for bug identified and corrected by http://github.com/addthis/stream-lib/pull/53
     */
    /* amplification of com.clearspring.analytics.stream.quantile.QDigestTest#testSerialization */
    @org.junit.Test
    public void testSerialization_literalMutation66142_failAssert0_literalMutation67239() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            long[] samples = new long[]{ // TestDataMutator on numbers
            -1 , 20 };
            // AssertGenerator add assertion
            long[] array_474830504 = new long[]{-1, 20};
	long[] array_1742970213 = (long[])samples;
	for(int ii = 0; ii <array_474830504.length; ii++) {
		org.junit.Assert.assertEquals(array_474830504[ii], array_1742970213[ii]);
	};
            com.clearspring.analytics.stream.quantile.QDigest digestA = new com.clearspring.analytics.stream.quantile.QDigest(2);
            for (int i = 2; i < (samples.length); i++) {
                digestA.offer(samples[i]);
            }
            byte[] serialized = com.clearspring.analytics.stream.quantile.QDigest.serialize(digestA);
            com.clearspring.analytics.stream.quantile.QDigest deserializedA = com.clearspring.analytics.stream.quantile.QDigest.deserialize(serialized);
            com.clearspring.analytics.stream.quantile.QDigest digestB = new com.clearspring.analytics.stream.quantile.QDigest(2);
            for (int i = 0; i < (samples.length); i++) {
                digestB.offer(samples[i]);
            }
            com.clearspring.analytics.stream.quantile.QDigest.unionOf(digestA, deserializedA);
            org.junit.Assert.fail("testSerialization_literalMutation66142 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /**
     * Test for bug identified and corrected by http://github.com/addthis/stream-lib/pull/53
     */
    /* amplification of com.clearspring.analytics.stream.quantile.QDigestTest#testSerialization */
    @org.junit.Test(timeout = 10000)
    public void testSerialization_literalMutation66158_cf66912_failAssert29_literalMutation69768() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            long[] samples = new long[]{ 0 , 20 };
            com.clearspring.analytics.stream.quantile.QDigest digestA = new com.clearspring.analytics.stream.quantile.QDigest(2);
            for (int i = 2; i < (samples.length); i++) {
                digestA.offer(samples[i]);
            }
            byte[] serialized = com.clearspring.analytics.stream.quantile.QDigest.serialize(digestA);
            com.clearspring.analytics.stream.quantile.QDigest deserializedA = com.clearspring.analytics.stream.quantile.QDigest.deserialize(serialized);
            com.clearspring.analytics.stream.quantile.QDigest digestB = new com.clearspring.analytics.stream.quantile.QDigest(1);
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((com.clearspring.analytics.stream.quantile.QDigest)digestB).computeActualSize(), 0L);
            // AssertGenerator add assertion
            java.util.ArrayList collection_1796670028 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_1796670028, ((com.clearspring.analytics.stream.quantile.QDigest)digestB).toAscRanges());;
            // StatementAdderOnAssert create null value
            com.clearspring.analytics.stream.quantile.QDigest vc_7438 = (com.clearspring.analytics.stream.quantile.QDigest)null;
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(vc_7438);
            // StatementAdderOnAssert create null value
            com.clearspring.analytics.stream.quantile.QDigest vc_7436 = (com.clearspring.analytics.stream.quantile.QDigest)null;
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(vc_7436);
            // StatementAdderMethod cloned existing statement
            vc_7436.serialize(vc_7438);
            // MethodAssertGenerator build local variable
            Object o_22_0 = ((com.clearspring.analytics.stream.quantile.QDigest)digestB).computeActualSize();
            // AssertGenerator add assertion
            java.util.ArrayList collection_1011219714 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_1011219714, ((com.clearspring.analytics.stream.quantile.QDigest)digestB).toAscRanges());;
            for (int i = 0; i < (samples.length); i++) {
                digestB.offer(samples[i]);
            }
            com.clearspring.analytics.stream.quantile.QDigest.unionOf(digestA, deserializedA);
            org.junit.Assert.fail("testSerialization_literalMutation66158_cf66912 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }
}

