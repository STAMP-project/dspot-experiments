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


public class TestHyperLogLogPlusAmpl {
    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(com.clearspring.analytics.stream.cardinality.TestHyperLogLogPlusAmpl.class);

    @org.junit.Test
    public void testEquals() {
        com.clearspring.analytics.stream.cardinality.HyperLogLogPlus hll1 = new com.clearspring.analytics.stream.cardinality.HyperLogLogPlus(5, 25);
        com.clearspring.analytics.stream.cardinality.HyperLogLogPlus hll2 = new com.clearspring.analytics.stream.cardinality.HyperLogLogPlus(5, 25);
        hll1.offer("A");
        hll2.offer("A");
        org.junit.Assert.assertEquals(hll1, hll2);
        hll2.offer("B");
        hll2.offer("C");
        hll2.offer("D");
        org.junit.Assert.assertNotEquals(hll1, hll2);
        com.clearspring.analytics.stream.cardinality.HyperLogLogPlus hll3 = new com.clearspring.analytics.stream.cardinality.HyperLogLogPlus(5, 25);
        for (int i = 0; i < 50000; i++) {
            hll3.offer(("" + i));
        }
        org.junit.Assert.assertNotEquals(hll1, hll3);
    }

    @org.junit.Test
    public void consistentBytes() throws java.lang.Throwable {
        int[] NUM_STRINGS = new int[]{ 30 , 50 , 100 , 200 , 300 , 500 , 1000 , 10000 , 100000 };
        for (int n : NUM_STRINGS) {
            java.lang.String[] strings = new java.lang.String[n];
            for (int i = 0; i < n; i++) {
                strings[i] = org.apache.commons.lang3.RandomStringUtils.randomAlphabetic(20);
            }
            com.clearspring.analytics.stream.cardinality.HyperLogLogPlus hllpp1 = new com.clearspring.analytics.stream.cardinality.HyperLogLogPlus(5, 5);
            com.clearspring.analytics.stream.cardinality.HyperLogLogPlus hllpp2 = new com.clearspring.analytics.stream.cardinality.HyperLogLogPlus(5, 5);
            for (int i = 0; i < n; i++) {
                hllpp1.offer(strings[i]);
                hllpp2.offer(strings[((n - 1) - i)]);
            }
            // calling these here ensures their internal state (format type) is stable for the rest of these checks.
            // (end users have no need for this because they cannot access the format directly anyway)
            hllpp1.mergeTempList();
            hllpp2.mergeTempList();
            com.clearspring.analytics.stream.cardinality.TestHyperLogLogPlusAmpl.log.debug("n={} format1={} format2={}", n, hllpp1.format, hllpp2.format);
            try {
                if ((hllpp1.format) == (hllpp2.format)) {
                    org.junit.Assert.assertEquals(hllpp1, hllpp2);
                    org.junit.Assert.assertEquals(hllpp1.hashCode(), hllpp2.hashCode());
                    org.junit.Assert.assertArrayEquals(hllpp1.getBytes(), hllpp2.getBytes());
                }else {
                    org.junit.Assert.assertNotEquals(hllpp1, hllpp2);
                }
            } catch (java.lang.Throwable any) {
                com.clearspring.analytics.stream.cardinality.TestHyperLogLogPlusAmpl.log.error("n={} format1={} format2={}", n, hllpp1.format, hllpp2.format, any);
                throw any;
            }
        }
    }

    public static void main(final java.lang.String[] args) throws java.lang.Throwable {
        long startTime = java.lang.System.currentTimeMillis();
        int numSets = 10;
        int setSize = (1 * 1000) * 1000;
        int repeats = 5;
        com.clearspring.analytics.stream.cardinality.HyperLogLogPlus[] counters = new com.clearspring.analytics.stream.cardinality.HyperLogLogPlus[numSets];
        for (int i = 0; i < numSets; i++) {
            counters[i] = new com.clearspring.analytics.stream.cardinality.HyperLogLogPlus(15, 15);
        }
        for (int i = 0; i < numSets; i++) {
            for (int j = 0; j < setSize; j++) {
                java.lang.String val = java.util.UUID.randomUUID().toString();
                for (int z = 0; z < repeats; z++) {
                    counters[i].offer(val);
                }
            }
        }
        com.clearspring.analytics.stream.cardinality.ICardinality merged = counters[0];
        long sum = merged.cardinality();
        for (int i = 1; i < numSets; i++) {
            sum += counters[i].cardinality();
            merged = merged.merge(counters[i]);
        }
        long trueSize = numSets * setSize;
        java.lang.System.out.println(("True Cardinality: " + trueSize));
        java.lang.System.out.println(("Summed Cardinality: " + sum));
        java.lang.System.out.println(("Merged Cardinality: " + (merged.cardinality())));
        java.lang.System.out.println(("Merged Error: " + (((merged.cardinality()) - trueSize) / ((float) (trueSize)))));
        java.lang.System.out.println((("Duration: " + (((java.lang.System.currentTimeMillis()) - startTime) / 1000)) + "s"));
    }

    @org.junit.Test
    public void testComputeCount() {
        com.clearspring.analytics.stream.cardinality.HyperLogLogPlus hyperLogLogPlus = new com.clearspring.analytics.stream.cardinality.HyperLogLogPlus(14, 25);
        int count = 70000;
        for (int i = 0; i < count; i++) {
            hyperLogLogPlus.offer(("i" + i));
        }
        long estimate = hyperLogLogPlus.cardinality();
        double se = count * (1.04 / (java.lang.Math.sqrt(java.lang.Math.pow(2, 14))));
        long expectedCardinality = count;
        java.lang.System.out.println(((((("Expect estimate: " + estimate) + " is between ") + (expectedCardinality - (3 * se))) + " and ") + (expectedCardinality + (3 * se))));
        org.junit.Assert.assertTrue((estimate >= (expectedCardinality - (3 * se))));
        org.junit.Assert.assertTrue((estimate <= (expectedCardinality + (3 * se))));
    }

    @org.junit.Test
    public void testSmallCardinalityRepeatedInsert() {
        com.clearspring.analytics.stream.cardinality.HyperLogLogPlus hyperLogLogPlus = new com.clearspring.analytics.stream.cardinality.HyperLogLogPlus(14, 25);
        int count = 15000;
        int maxAttempts = 200;
        java.util.Random r = new java.util.Random();
        for (int i = 0; i < count; i++) {
            int n = (r.nextInt(maxAttempts)) + 1;
            for (int j = 0; j < n; j++) {
                hyperLogLogPlus.offer(("i" + i));
            }
        }
        long estimate = hyperLogLogPlus.cardinality();
        double se = count * (1.04 / (java.lang.Math.sqrt(java.lang.Math.pow(2, 14))));
        long expectedCardinality = count;
        java.lang.System.out.println(((((("Expect estimate: " + estimate) + " is between ") + (expectedCardinality - (3 * se))) + " and ") + (expectedCardinality + (3 * se))));
        org.junit.Assert.assertTrue((estimate >= (expectedCardinality - (3 * se))));
        org.junit.Assert.assertTrue((estimate <= (expectedCardinality + (3 * se))));
    }

    @org.junit.Test
    public void testSerialization_Normal() throws java.io.IOException {
        com.clearspring.analytics.stream.cardinality.HyperLogLogPlus hll = new com.clearspring.analytics.stream.cardinality.HyperLogLogPlus(5, 25);
        for (int i = 0; i < 100000; i++) {
            hll.offer(("" + i));
        }
        java.lang.System.out.println(hll.cardinality());
        com.clearspring.analytics.stream.cardinality.HyperLogLogPlus hll2 = com.clearspring.analytics.stream.cardinality.HyperLogLogPlus.Builder.build(hll.getBytes());
        org.junit.Assert.assertEquals(hll.cardinality(), hll2.cardinality());
    }

    @org.junit.Test
    public void testSerialization() throws java.io.IOException, java.lang.ClassNotFoundException {
        com.clearspring.analytics.stream.cardinality.HyperLogLogPlus hll = new com.clearspring.analytics.stream.cardinality.HyperLogLogPlus(5, 25);
        for (int i = 0; i < 100000; i++) {
            hll.offer(("" + i));
        }
        java.lang.System.out.println(hll.cardinality());
        com.clearspring.analytics.stream.cardinality.HyperLogLogPlus hll2 = ((com.clearspring.analytics.stream.cardinality.HyperLogLogPlus) (com.clearspring.analytics.TestUtils.deserialize(com.clearspring.analytics.TestUtils.serialize(hll))));
        org.junit.Assert.assertEquals(hll.cardinality(), hll2.cardinality());
    }

    @org.junit.Test
    public void testSerialization_Sparse() throws java.io.IOException {
        com.clearspring.analytics.stream.cardinality.HyperLogLogPlus hll = new com.clearspring.analytics.stream.cardinality.HyperLogLogPlus(14, 25);
        hll.offer("a");
        hll.offer("b");
        hll.offer("c");
        hll.offer("d");
        hll.offer("e");
        com.clearspring.analytics.stream.cardinality.HyperLogLogPlus hll2 = com.clearspring.analytics.stream.cardinality.HyperLogLogPlus.Builder.build(hll.getBytes());
        org.junit.Assert.assertEquals(hll.cardinality(), hll2.cardinality());
    }

    @org.junit.Test
    public void testHighPrecisionInitialization() {
        for (int sp = 4; sp <= 32; sp++) {
            int expectedSm = ((int) (java.lang.Math.pow(2, sp)));
            for (int p = 4; p <= sp; p++) {
                int expectedM = ((int) (java.lang.Math.pow(2, p)));
                com.clearspring.analytics.stream.cardinality.HyperLogLogPlus hyperLogLogPlus = new com.clearspring.analytics.stream.cardinality.HyperLogLogPlus(p, sp);
                org.junit.Assert.assertEquals(expectedM, hyperLogLogPlus.getM());
                org.junit.Assert.assertEquals(expectedSm, hyperLogLogPlus.getSm());
            }
        }
    }

    @org.junit.Test
    public void testHighCardinality() {
        long start = java.lang.System.currentTimeMillis();
        com.clearspring.analytics.stream.cardinality.HyperLogLogPlus hyperLogLogPlus = new com.clearspring.analytics.stream.cardinality.HyperLogLogPlus(18, 25);
        int size = 10000000;
        for (int i = 0; i < size; i++) {
            hyperLogLogPlus.offer(com.clearspring.analytics.stream.cardinality.TestICardinality.streamElement(i));
        }
        java.lang.System.out.println(((((("expected: " + size) + ", estimate: ") + (hyperLogLogPlus.cardinality())) + ", time: ") + ((java.lang.System.currentTimeMillis()) - start)));
        long estimate = hyperLogLogPlus.cardinality();
        double err = (java.lang.Math.abs((estimate - size))) / ((double) (size));
        java.lang.System.out.println(("Percentage error  " + err));
        org.junit.Assert.assertTrue((err < 0.1));
    }

    @org.junit.Test
    public void testSortEncodedSet() {
        int[] testSet = new int[3];
        testSet[0] = 655403;
        testSet[1] = 655416;
        testSet[2] = 655425;
        com.clearspring.analytics.stream.cardinality.HyperLogLogPlus hyperLogLogPlus = new com.clearspring.analytics.stream.cardinality.HyperLogLogPlus(14, 25);
        testSet = hyperLogLogPlus.sortEncodedSet(testSet, 3);
        org.junit.Assert.assertEquals(655403, testSet[0]);
        org.junit.Assert.assertEquals(655425, testSet[1]);
        org.junit.Assert.assertEquals(655416, testSet[2]);
    }

    @org.junit.Test
    public void testMergeSelf_forceNormal() throws com.clearspring.analytics.stream.cardinality.CardinalityMergeException, java.io.IOException {
        final int[] cardinalities = new int[]{ 0 , 1 , 10 , 100 , 1000 , 10000 , 100000 , 1000000 };
        for (int cardinality : cardinalities) {
            for (int j = 4; j < 24; j++) {
                java.lang.System.out.println(("p=" + j));
                com.clearspring.analytics.stream.cardinality.HyperLogLogPlus hllPlus = new com.clearspring.analytics.stream.cardinality.HyperLogLogPlus(j, 0);
                for (int l = 0; l < cardinality; l++) {
                    hllPlus.offer(java.lang.Math.random());
                }
                java.lang.System.out.println(((("hllcardinality=" + (hllPlus.cardinality())) + " cardinality=") + cardinality));
                com.clearspring.analytics.stream.cardinality.HyperLogLogPlus deserialized = com.clearspring.analytics.stream.cardinality.HyperLogLogPlus.Builder.build(hllPlus.getBytes());
                org.junit.Assert.assertEquals(hllPlus.cardinality(), deserialized.cardinality());
                com.clearspring.analytics.stream.cardinality.ICardinality merged = hllPlus.merge(deserialized);
                java.lang.System.out.println((((merged.cardinality()) + " : ") + (hllPlus.cardinality())));
                org.junit.Assert.assertEquals(hllPlus.cardinality(), merged.cardinality());
            }
        }
    }

    @org.junit.Test
    public void testMergeSelf() throws com.clearspring.analytics.stream.cardinality.CardinalityMergeException, java.io.IOException {
        final int[] cardinalities = new int[]{ 0 , 1 , 10 , 100 , 1000 , 10000 , 100000 };
        final int[] ps = new int[]{ 4 , 5 , 6 , 7 , 8 , 9 , 10 , 11 , 12 , 13 , 14 , 15 , 16 , 17 , 18 , 19 , 20 };
        final int[] sps = new int[]{ 16 , 17 , 18 , 19 , 20 , 21 , 22 , 23 , 24 , 25 , 26 , 27 , 28 , 29 , 30 , 31 , 32 };
        for (int cardinality : cardinalities) {
            for (int j = 0; j < (ps.length); j++) {
                for (int sp : sps) {
                    if (sp < (ps[j])) {
                        continue;
                    }
                    com.clearspring.analytics.stream.cardinality.HyperLogLogPlus hllPlus = new com.clearspring.analytics.stream.cardinality.HyperLogLogPlus(ps[j], sp);
                    for (int l = 0; l < cardinality; l++) {
                        hllPlus.offer(java.lang.Math.random());
                    }
                    com.clearspring.analytics.stream.cardinality.HyperLogLogPlus deserialized = com.clearspring.analytics.stream.cardinality.HyperLogLogPlus.Builder.build(hllPlus.getBytes());
                    java.lang.System.out.println((((((((ps[j]) + "-") + sp) + ": ") + cardinality) + " -> ") + (hllPlus.cardinality())));
                    org.junit.Assert.assertEquals(hllPlus.cardinality(), deserialized.cardinality());
                    com.clearspring.analytics.stream.cardinality.ICardinality merged = hllPlus.merge(deserialized);
                    org.junit.Assert.assertEquals(hllPlus.cardinality(), merged.cardinality());
                }
            }
        }
    }

    @org.junit.Test
    public void testSparseSpace() throws java.io.IOException {
        com.clearspring.analytics.stream.cardinality.HyperLogLogPlus hllp = new com.clearspring.analytics.stream.cardinality.HyperLogLogPlus(14, 14);
        for (int i = 0; i < 10000; i++) {
            hllp.offer(i);
        }
        java.lang.System.out.println(("Size: " + (hllp.getBytes().length)));
    }

    @org.junit.Test
    public void testMerge_Sparse() throws com.clearspring.analytics.stream.cardinality.CardinalityMergeException {
        int numToMerge = 4;
        int bits = 18;
        int cardinality = 4000;
        com.clearspring.analytics.stream.cardinality.HyperLogLogPlus[] hyperLogLogs = new com.clearspring.analytics.stream.cardinality.HyperLogLogPlus[numToMerge];
        com.clearspring.analytics.stream.cardinality.HyperLogLogPlus baseline = new com.clearspring.analytics.stream.cardinality.HyperLogLogPlus(bits, 25);
        for (int i = 0; i < numToMerge; i++) {
            hyperLogLogs[i] = new com.clearspring.analytics.stream.cardinality.HyperLogLogPlus(bits, 25);
            for (int j = 0; j < cardinality; j++) {
                double val = java.lang.Math.random();
                hyperLogLogs[i].offer(val);
                baseline.offer(val);
            }
        }
        long expectedCardinality = numToMerge * cardinality;
        com.clearspring.analytics.stream.cardinality.HyperLogLogPlus hll = hyperLogLogs[0];
        hyperLogLogs = java.util.Arrays.asList(hyperLogLogs).subList(1, hyperLogLogs.length).toArray(new com.clearspring.analytics.stream.cardinality.HyperLogLogPlus[0]);
        long mergedEstimate = hll.merge(hyperLogLogs).cardinality();
        double se = expectedCardinality * (1.04 / (java.lang.Math.sqrt(java.lang.Math.pow(2, bits))));
        java.lang.System.out.println(((((("Expect estimate: " + mergedEstimate) + " is between ") + (expectedCardinality - (3 * se))) + " and ") + (expectedCardinality + (3 * se))));
        double err = (java.lang.Math.abs((mergedEstimate - expectedCardinality))) / ((double) (expectedCardinality));
        java.lang.System.out.println(("Percentage error  " + err));
        org.junit.Assert.assertTrue((err < 0.1));
        org.junit.Assert.assertTrue((mergedEstimate >= (expectedCardinality - (3 * se))));
        org.junit.Assert.assertTrue((mergedEstimate <= (expectedCardinality + (3 * se))));
    }

    @org.junit.Test
    public void testMerge_Normal() throws com.clearspring.analytics.stream.cardinality.CardinalityMergeException {
        int numToMerge = 4;
        int bits = 18;
        int cardinality = 5000;
        com.clearspring.analytics.stream.cardinality.HyperLogLogPlus[] hyperLogLogs = new com.clearspring.analytics.stream.cardinality.HyperLogLogPlus[numToMerge];
        com.clearspring.analytics.stream.cardinality.HyperLogLogPlus baseline = new com.clearspring.analytics.stream.cardinality.HyperLogLogPlus(bits, 25);
        for (int i = 0; i < numToMerge; i++) {
            hyperLogLogs[i] = new com.clearspring.analytics.stream.cardinality.HyperLogLogPlus(bits, 25);
            for (int j = 0; j < cardinality; j++) {
                double val = java.lang.Math.random();
                hyperLogLogs[i].offer(val);
                baseline.offer(val);
            }
        }
        long expectedCardinality = numToMerge * cardinality;
        com.clearspring.analytics.stream.cardinality.HyperLogLogPlus hll = hyperLogLogs[0];
        hyperLogLogs = java.util.Arrays.asList(hyperLogLogs).subList(1, hyperLogLogs.length).toArray(new com.clearspring.analytics.stream.cardinality.HyperLogLogPlus[0]);
        long mergedEstimate = hll.merge(hyperLogLogs).cardinality();
        double se = expectedCardinality * (1.04 / (java.lang.Math.sqrt(java.lang.Math.pow(2, bits))));
        java.lang.System.out.println(((((("Expect estimate: " + mergedEstimate) + " is between ") + (expectedCardinality - (3 * se))) + " and ") + (expectedCardinality + (3 * se))));
        org.junit.Assert.assertTrue((mergedEstimate >= (expectedCardinality - (3 * se))));
        org.junit.Assert.assertTrue((mergedEstimate <= (expectedCardinality + (3 * se))));
    }

    @org.junit.Test
    public void testLegacyCodec_normal() throws java.io.IOException {
        int bits = 18;
        int cardinality = 1000000;
        com.clearspring.analytics.stream.cardinality.HyperLogLogPlus baseline = new com.clearspring.analytics.stream.cardinality.HyperLogLogPlus(bits, 25);
        for (int j = 0; j < cardinality; j++) {
            double val = java.lang.Math.random();
            baseline.offer(val);
        }
        java.io.ByteArrayOutputStream baos = new java.io.ByteArrayOutputStream();
        java.io.DataOutputStream dos = new java.io.DataOutputStream(baos);
        dos.writeInt(bits);
        dos.writeInt(25);
        dos.writeInt(0);
        dos.writeInt(((baseline.getRegisterSet().size) * 4));
        for (int x : baseline.getRegisterSet().readOnlyBits()) {
            dos.writeInt(x);
        }
        byte[] legacyBytes = baos.toByteArray();
        // decode legacy
        com.clearspring.analytics.stream.cardinality.HyperLogLogPlus decoded = com.clearspring.analytics.stream.cardinality.HyperLogLogPlus.Builder.build(legacyBytes);
        org.junit.Assert.assertEquals(baseline.cardinality(), decoded.cardinality());
        byte[] newBytes = baseline.getBytes();
        org.junit.Assert.assertTrue(((newBytes.length) < (legacyBytes.length)));
    }

    @org.junit.Test
    public void testLegacyCodec_sparse() throws java.io.IOException {
        int bits = 18;
        int cardinality = 5000;
        com.clearspring.analytics.stream.cardinality.HyperLogLogPlus baseline = new com.clearspring.analytics.stream.cardinality.HyperLogLogPlus(bits, 25);
        for (int j = 0; j < cardinality; j++) {
            double val = java.lang.Math.random();
            baseline.offer(val);
        }
        java.io.ByteArrayOutputStream baos = new java.io.ByteArrayOutputStream();
        java.io.DataOutputStream dos = new java.io.DataOutputStream(baos);
        dos.writeInt(bits);
        dos.writeInt(25);
        dos.writeInt(1);
        baseline.mergeTempList();
        int[] sparseSet = baseline.getSparseSet();
        java.util.List<byte[]> sparseBytes = new java.util.ArrayList<byte[]>(sparseSet.length);
        int prevDelta = 0;
        for (int k : sparseSet) {
            sparseBytes.add(com.clearspring.analytics.util.Varint.writeUnsignedVarInt((k - prevDelta)));
            prevDelta = k;
        }
        for (byte[] bytes : sparseBytes) {
            dos.writeInt(bytes.length);
            dos.write(bytes);
        }
        dos.writeInt((-1));
        byte[] legacyBytes = baos.toByteArray();
        // decode legacy
        com.clearspring.analytics.stream.cardinality.HyperLogLogPlus decoded = com.clearspring.analytics.stream.cardinality.HyperLogLogPlus.Builder.build(legacyBytes);
        org.junit.Assert.assertEquals(baseline.cardinality(), decoded.cardinality());
        byte[] newBytes = baseline.getBytes();
        org.junit.Assert.assertTrue(((newBytes.length) < (legacyBytes.length)));
    }

    @org.junit.Test
    public void testMerge_SparseIntersection() throws com.clearspring.analytics.stream.cardinality.CardinalityMergeException {
        com.clearspring.analytics.stream.cardinality.HyperLogLogPlus a = new com.clearspring.analytics.stream.cardinality.HyperLogLogPlus(11, 16);
        com.clearspring.analytics.stream.cardinality.HyperLogLogPlus b = new com.clearspring.analytics.stream.cardinality.HyperLogLogPlus(11, 16);
        // Note that only one element, 41, is shared amongst the two sets,
        // and so the number of total unique elements is 14.
        int[] aInput = new int[]{ 12 , 13 , 22 , 34 , 38 , 40 , 41 , 46 , 49 };
        int[] bInput = new int[]{ 2 , 6 , 19 , 29 , 41 , 48 };
        java.util.Set<java.lang.Integer> testSet = new java.util.HashSet<java.lang.Integer>();
        for (java.lang.Integer in : aInput) {
            testSet.add(in);
            a.offer(in);
        }
        for (java.lang.Integer in : bInput) {
            testSet.add(in);
            b.offer(in);
        }
        org.junit.Assert.assertEquals(14, testSet.size());
        org.junit.Assert.assertEquals(9, a.cardinality());
        org.junit.Assert.assertEquals(6, b.cardinality());
        a.addAll(b);
        org.junit.Assert.assertEquals(14, a.cardinality());
    }

    @org.junit.Test
    public void testSerializationWithNewSortMethod() throws java.io.IOException {
        com.clearspring.analytics.stream.cardinality.HyperLogLogPlus hll = new com.clearspring.analytics.stream.cardinality.HyperLogLogPlus(14, 25);
        hll.offerHashed(0L);
        hll.offerHashed(9223372036854775807L);
        hll.offerHashed(-9223372036854775808L);
        hll.offerHashed(-1L);
        // test against old serialization
        org.junit.Assert.assertArrayEquals(new byte[]{ -1 , -1 , -1 , -2 , 14 , 25 , 1 , 4 , 25 , -27 , -1 , -1 , 15 , -101 , -128 , -128 , -16 , 7 , -27 , -1 , -1 , -97 , 8 }, hll.getBytes());
    }

    @org.junit.Test
    public void testOne() throws java.io.IOException {
        com.clearspring.analytics.stream.cardinality.HyperLogLogPlus one = new com.clearspring.analytics.stream.cardinality.HyperLogLogPlus(8, 25);
        // AssertGenerator replace invocation
        boolean o_testOne__3 = one.offer("a");
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_testOne__3);
        org.junit.Assert.assertEquals(1, one.cardinality());
    }

    @org.junit.Test
    public void testMerge_ManySparse() throws com.clearspring.analytics.stream.cardinality.CardinalityMergeException {
        int numToMerge = 20;
        int bits = 18;
        int cardinality = 10000;
        com.clearspring.analytics.stream.cardinality.HyperLogLogPlus[] hyperLogLogs = new com.clearspring.analytics.stream.cardinality.HyperLogLogPlus[numToMerge];
        com.clearspring.analytics.stream.cardinality.HyperLogLogPlus baseline = new com.clearspring.analytics.stream.cardinality.HyperLogLogPlus(bits, 25);
        for (int i = 0; i < numToMerge; i++) {
            hyperLogLogs[i] = new com.clearspring.analytics.stream.cardinality.HyperLogLogPlus(bits, 25);
            for (int j = 0; j < cardinality; j++) {
                double val = java.lang.Math.random();
                // AssertGenerator replace invocation
                boolean o_testMerge_ManySparse__19 = hyperLogLogs[i].offer(val);
                // AssertGenerator add assertion
                org.junit.Assert.assertTrue(o_testMerge_ManySparse__19);
                // AssertGenerator replace invocation
                boolean o_testMerge_ManySparse__20 = baseline.offer(val);
                // AssertGenerator add assertion
                org.junit.Assert.assertTrue(o_testMerge_ManySparse__20);
            }
        }
        long expectedCardinality = numToMerge * cardinality;
        com.clearspring.analytics.stream.cardinality.HyperLogLogPlus hll = hyperLogLogs[0];
        hyperLogLogs = java.util.Arrays.asList(hyperLogLogs).subList(1, hyperLogLogs.length).toArray(new com.clearspring.analytics.stream.cardinality.HyperLogLogPlus[0]);
        long mergedEstimate = hll.merge(hyperLogLogs).cardinality();
        double se = expectedCardinality * (1.04 / (java.lang.Math.sqrt(java.lang.Math.pow(2, bits))));
        java.lang.System.out.println(((((("Expect estimate: " + mergedEstimate) + " is between ") + (expectedCardinality - (3 * se))) + " and ") + (expectedCardinality + (3 * se))));
        org.junit.Assert.assertTrue((mergedEstimate >= (expectedCardinality - (3 * se))));
        org.junit.Assert.assertTrue((mergedEstimate <= (expectedCardinality + (3 * se))));
    }

    /* amplification of com.clearspring.analytics.stream.cardinality.TestHyperLogLogPlus#consistentBytes */
    @org.junit.Test
    public void consistentBytes_literalMutation59_failAssert6() throws java.lang.Throwable {
        // AssertGenerator generate try/catch block with fail statement
        try {
            int[] NUM_STRINGS = new int[]{ 30 , 50 , 100 , 200 , 300 , 500 , 1000 , 10000 , 100000 };
            for (int n : NUM_STRINGS) {
                java.lang.String[] strings = new java.lang.String[n];
                for (int i = 0; i < n; i++) {
                    strings[i] = org.apache.commons.lang3.RandomStringUtils.randomAlphabetic(20);
                }
                com.clearspring.analytics.stream.cardinality.HyperLogLogPlus hllpp1 = new com.clearspring.analytics.stream.cardinality.HyperLogLogPlus(10, 5);
                com.clearspring.analytics.stream.cardinality.HyperLogLogPlus hllpp2 = new com.clearspring.analytics.stream.cardinality.HyperLogLogPlus(5, 5);
                for (int i = 0; i < n; i++) {
                    hllpp1.offer(strings[i]);
                    hllpp2.offer(strings[((n - 1) - i)]);
                }
                // calling these here ensures their internal state (format type) is stable for the rest of these checks.
                // (end users have no need for this because they cannot access the format directly anyway)
                hllpp1.mergeTempList();
                hllpp2.mergeTempList();
                com.clearspring.analytics.stream.cardinality.TestHyperLogLogPlusAmpl.log.debug("n={} format1={} format2={}", n, hllpp1.format, hllpp2.format);
                try {
                    if ((hllpp1.format) == (hllpp2.format)) {
                        // MethodAssertGenerator build local variable
                        Object o_36_1 = hllpp2.getBytes();
                        // MethodAssertGenerator build local variable
                        Object o_33_1 = hllpp2.hashCode();
                        // MethodAssertGenerator build local variable
                        Object o_33_0 = hllpp1.hashCode();
                        // MethodAssertGenerator build local variable
                        Object o_36_0 = hllpp1.getBytes();
                    }else {
                    }
                } catch (java.lang.Throwable any) {
                    com.clearspring.analytics.stream.cardinality.TestHyperLogLogPlusAmpl.log.error("n={} format1={} format2={}", n, hllpp1.format, hllpp2.format, any);
                    throw any;
                }
            }
            org.junit.Assert.fail("consistentBytes_literalMutation59 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of com.clearspring.analytics.stream.cardinality.TestHyperLogLogPlus#consistentBytes */
    /* amplification of com.clearspring.analytics.stream.cardinality.TestHyperLogLogPlus#consistentBytes_literalMutation77 */
    @org.junit.Test
    public void consistentBytes_literalMutation77_failAssert24_literalMutation1247() throws java.lang.Throwable {
        // AssertGenerator generate try/catch block with fail statement
        try {
            int[] NUM_STRINGS = new int[]{ 30 , 50 , 100 , 200 , 300 , 500 , 1000 , 10000 , 100000 };
            for (int n : NUM_STRINGS) {
                java.lang.String[] strings = new java.lang.String[n];
                for (int i = 0; i < n; i++) {
                    strings[i] = org.apache.commons.lang3.RandomStringUtils.randomAlphabetic(20);
                }
                com.clearspring.analytics.stream.cardinality.HyperLogLogPlus hllpp1 = new com.clearspring.analytics.stream.cardinality.HyperLogLogPlus(5, 5);
                com.clearspring.analytics.stream.cardinality.HyperLogLogPlus hllpp2 = new com.clearspring.analytics.stream.cardinality.HyperLogLogPlus(5, 10);
                // AssertGenerator add assertion
                org.junit.Assert.assertEquals(((com.clearspring.analytics.stream.cardinality.HyperLogLogPlus)hllpp2).sizeof(), 24);
                // AssertGenerator add assertion
                org.junit.Assert.assertEquals(((com.clearspring.analytics.stream.cardinality.HyperLogLogPlus)hllpp2).cardinality(), 0L);
                // AssertGenerator add assertion
                byte[] array_1803703486 = new byte[]{-1, -1, -1, -2, 5, 10, 1, 0};
	byte[] array_421794528 = (byte[])((com.clearspring.analytics.stream.cardinality.HyperLogLogPlus)hllpp2).getBytes();
	for(int ii = 0; ii <array_1803703486.length; ii++) {
		org.junit.Assert.assertEquals(array_1803703486[ii], array_421794528[ii]);
	};
                // AssertGenerator add assertion
                org.junit.Assert.assertTrue(hllpp2.equals(hllpp1));
                for (int i = -1; i < n; i++) {
                    hllpp1.offer(strings[i]);
                    hllpp2.offer(strings[((n - 1) - i)]);
                }
                // calling these here ensures their internal state (format type) is stable for the rest of these checks.
                // (end users have no need for this because they cannot access the format directly anyway)
                hllpp1.mergeTempList();
                hllpp2.mergeTempList();
                com.clearspring.analytics.stream.cardinality.TestHyperLogLogPlusAmpl.log.debug("n={} format1={} format2={}", n, hllpp1.format, hllpp2.format);
                try {
                    if ((hllpp1.format) == (hllpp2.format)) {
                        // MethodAssertGenerator build local variable
                        Object o_36_1 = hllpp2.getBytes();
                        // MethodAssertGenerator build local variable
                        Object o_33_1 = hllpp2.hashCode();
                        // MethodAssertGenerator build local variable
                        Object o_33_0 = hllpp1.hashCode();
                        // MethodAssertGenerator build local variable
                        Object o_36_0 = hllpp1.getBytes();
                    }else {
                    }
                } catch (java.lang.Throwable any) {
                    com.clearspring.analytics.stream.cardinality.TestHyperLogLogPlusAmpl.log.error("n={} format1={} format2={}", n, hllpp1.format, hllpp2.format, any);
                    throw any;
                }
            }
            org.junit.Assert.fail("consistentBytes_literalMutation77 should have thrown ArrayIndexOutOfBoundsException");
        } catch (java.lang.ArrayIndexOutOfBoundsException eee) {
        }
    }

    /* amplification of com.clearspring.analytics.stream.cardinality.TestHyperLogLogPlus#consistentBytes */
    /* amplification of com.clearspring.analytics.stream.cardinality.TestHyperLogLogPlus#consistentBytes_literalMutation61 */
    @org.junit.Test
    public void consistentBytes_literalMutation61_failAssert8_literalMutation561() throws java.lang.Throwable {
        // AssertGenerator generate try/catch block with fail statement
        try {
            int[] NUM_STRINGS = new int[]{ 30 , 50 , 100 , 200 , 300 , 500 , // TestDataMutator on numbers
            500 , 10000 , 100000 };
            // AssertGenerator add assertion
            int[] array_2106203392 = new int[]{30, 50, 100, 200, 300, 500, 500, 10000, 100000};
	int[] array_648071255 = (int[])NUM_STRINGS;
	for(int ii = 0; ii <array_2106203392.length; ii++) {
		org.junit.Assert.assertEquals(array_2106203392[ii], array_648071255[ii]);
	};
            for (int n : NUM_STRINGS) {
                java.lang.String[] strings = new java.lang.String[n];
                for (int i = 0; i < n; i++) {
                    strings[i] = org.apache.commons.lang3.RandomStringUtils.randomAlphabetic(20);
                }
                com.clearspring.analytics.stream.cardinality.HyperLogLogPlus hllpp1 = new com.clearspring.analytics.stream.cardinality.HyperLogLogPlus(5, 4);
                com.clearspring.analytics.stream.cardinality.HyperLogLogPlus hllpp2 = new com.clearspring.analytics.stream.cardinality.HyperLogLogPlus(5, 5);
                for (int i = 0; i < n; i++) {
                    hllpp1.offer(strings[i]);
                    hllpp2.offer(strings[((n - 1) - i)]);
                }
                // calling these here ensures their internal state (format type) is stable for the rest of these checks.
                // (end users have no need for this because they cannot access the format directly anyway)
                hllpp1.mergeTempList();
                hllpp2.mergeTempList();
                com.clearspring.analytics.stream.cardinality.TestHyperLogLogPlusAmpl.log.debug("n={} format1={} format2={}", n, hllpp1.format, hllpp2.format);
                try {
                    if ((hllpp1.format) == (hllpp2.format)) {
                        // MethodAssertGenerator build local variable
                        Object o_36_1 = hllpp2.getBytes();
                        // MethodAssertGenerator build local variable
                        Object o_33_1 = hllpp2.hashCode();
                        // MethodAssertGenerator build local variable
                        Object o_33_0 = hllpp1.hashCode();
                        // MethodAssertGenerator build local variable
                        Object o_36_0 = hllpp1.getBytes();
                    }else {
                    }
                } catch (java.lang.Throwable any) {
                    com.clearspring.analytics.stream.cardinality.TestHyperLogLogPlusAmpl.log.error("n={} format1={} format2={}", n, hllpp1.format, hllpp2.format, any);
                    throw any;
                }
            }
            org.junit.Assert.fail("consistentBytes_literalMutation61 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of com.clearspring.analytics.stream.cardinality.TestHyperLogLogPlus#consistentBytes */
    /* amplification of com.clearspring.analytics.stream.cardinality.TestHyperLogLogPlus#consistentBytes_literalMutation77 */
    @org.junit.Test
    public void consistentBytes_literalMutation77_failAssert24_literalMutation1245() throws java.lang.Throwable {
        // AssertGenerator generate try/catch block with fail statement
        try {
            int[] NUM_STRINGS = new int[]{ 30 , 50 , 100 , 200 , 300 , 500 , 1000 , 10000 , 100000 };
            for (int n : NUM_STRINGS) {
                java.lang.String[] strings = new java.lang.String[n];
                for (int i = 0; i < n; i++) {
                    strings[i] = org.apache.commons.lang3.RandomStringUtils.randomAlphabetic(20);
                }
                com.clearspring.analytics.stream.cardinality.HyperLogLogPlus hllpp1 = new com.clearspring.analytics.stream.cardinality.HyperLogLogPlus(5, 5);
                com.clearspring.analytics.stream.cardinality.HyperLogLogPlus hllpp2 = new com.clearspring.analytics.stream.cardinality.HyperLogLogPlus(5, 0);
                // AssertGenerator add assertion
                org.junit.Assert.assertEquals(((com.clearspring.analytics.stream.cardinality.HyperLogLogPlus)hllpp2).sizeof(), 24);
                // AssertGenerator add assertion
                org.junit.Assert.assertEquals(((com.clearspring.analytics.stream.cardinality.HyperLogLogPlus)hllpp2).cardinality(), 0L);
                // AssertGenerator add assertion
                byte[] array_1607345563 = new byte[]{-1, -1, -1, -2, 5, 0, 0, 24, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
	byte[] array_708397045 = (byte[])((com.clearspring.analytics.stream.cardinality.HyperLogLogPlus)hllpp2).getBytes();
	for(int ii = 0; ii <array_1607345563.length; ii++) {
		org.junit.Assert.assertEquals(array_1607345563[ii], array_708397045[ii]);
	};
                for (int i = -1; i < n; i++) {
                    hllpp1.offer(strings[i]);
                    hllpp2.offer(strings[((n - 1) - i)]);
                }
                // calling these here ensures their internal state (format type) is stable for the rest of these checks.
                // (end users have no need for this because they cannot access the format directly anyway)
                hllpp1.mergeTempList();
                hllpp2.mergeTempList();
                com.clearspring.analytics.stream.cardinality.TestHyperLogLogPlusAmpl.log.debug("n={} format1={} format2={}", n, hllpp1.format, hllpp2.format);
                try {
                    if ((hllpp1.format) == (hllpp2.format)) {
                        // MethodAssertGenerator build local variable
                        Object o_36_1 = hllpp2.getBytes();
                        // MethodAssertGenerator build local variable
                        Object o_33_1 = hllpp2.hashCode();
                        // MethodAssertGenerator build local variable
                        Object o_33_0 = hllpp1.hashCode();
                        // MethodAssertGenerator build local variable
                        Object o_36_0 = hllpp1.getBytes();
                    }else {
                    }
                } catch (java.lang.Throwable any) {
                    com.clearspring.analytics.stream.cardinality.TestHyperLogLogPlusAmpl.log.error("n={} format1={} format2={}", n, hllpp1.format, hllpp2.format, any);
                    throw any;
                }
            }
            org.junit.Assert.fail("consistentBytes_literalMutation77 should have thrown ArrayIndexOutOfBoundsException");
        } catch (java.lang.ArrayIndexOutOfBoundsException eee) {
        }
    }

    /* amplification of com.clearspring.analytics.stream.cardinality.TestHyperLogLogPlus#consistentBytes */
    /* amplification of com.clearspring.analytics.stream.cardinality.TestHyperLogLogPlus#consistentBytes_literalMutation81 */
    @org.junit.Test
    public void consistentBytes_literalMutation81_failAssert27_literalMutation1344() throws java.lang.Throwable {
        // AssertGenerator generate try/catch block with fail statement
        try {
            int[] NUM_STRINGS = new int[]{ // TestDataMutator on numbers
            0 , 50 , 100 , 200 , 300 , 500 , 1000 , 10000 , 100000 };
            // AssertGenerator add assertion
            int[] array_853872246 = new int[]{0, 50, 100, 200, 300, 500, 1000, 10000, 100000};
	int[] array_1524036060 = (int[])NUM_STRINGS;
	for(int ii = 0; ii <array_853872246.length; ii++) {
		org.junit.Assert.assertEquals(array_853872246[ii], array_1524036060[ii]);
	};
            for (int n : NUM_STRINGS) {
                java.lang.String[] strings = new java.lang.String[n];
                for (int i = 0; i < n; i++) {
                    strings[i] = org.apache.commons.lang3.RandomStringUtils.randomAlphabetic(20);
                }
                com.clearspring.analytics.stream.cardinality.HyperLogLogPlus hllpp1 = new com.clearspring.analytics.stream.cardinality.HyperLogLogPlus(5, 5);
                com.clearspring.analytics.stream.cardinality.HyperLogLogPlus hllpp2 = new com.clearspring.analytics.stream.cardinality.HyperLogLogPlus(5, 5);
                for (int i = 0; i < n; i++) {
                    hllpp1.offer(strings[i]);
                    hllpp2.offer(strings[((n - 0) - i)]);
                }
                // calling these here ensures their internal state (format type) is stable for the rest of these checks.
                // (end users have no need for this because they cannot access the format directly anyway)
                hllpp1.mergeTempList();
                hllpp2.mergeTempList();
                com.clearspring.analytics.stream.cardinality.TestHyperLogLogPlusAmpl.log.debug("n={} format1={} format2={}", n, hllpp1.format, hllpp2.format);
                try {
                    if ((hllpp1.format) == (hllpp2.format)) {
                        // MethodAssertGenerator build local variable
                        Object o_36_1 = hllpp2.getBytes();
                        // AssertGenerator add assertion
                        byte[] array_93617651 = new byte[]{-1, -1, -1, -2, 5, 5, 1, 0};
	byte[] array_381509206 = (byte[])o_36_1;
	for(int ii = 0; ii <array_93617651.length; ii++) {
		org.junit.Assert.assertEquals(array_93617651[ii], array_381509206[ii]);
	};
                        // MethodAssertGenerator build local variable
                        Object o_33_1 = hllpp2.hashCode();
                        // AssertGenerator add assertion
                        org.junit.Assert.assertEquals(o_33_1, 1);
                        // MethodAssertGenerator build local variable
                        Object o_33_0 = hllpp1.hashCode();
                        // AssertGenerator add assertion
                        org.junit.Assert.assertEquals(o_33_0, 1);
                        // MethodAssertGenerator build local variable
                        Object o_36_0 = hllpp1.getBytes();
                        // AssertGenerator add assertion
                        byte[] array_1717580362 = new byte[]{-1, -1, -1, -2, 5, 5, 1, 0};
	byte[] array_1108246107 = (byte[])o_36_0;
	for(int ii = 0; ii <array_1717580362.length; ii++) {
		org.junit.Assert.assertEquals(array_1717580362[ii], array_1108246107[ii]);
	};
                    }else {
                    }
                } catch (java.lang.Throwable any) {
                    com.clearspring.analytics.stream.cardinality.TestHyperLogLogPlusAmpl.log.error("n={} format1={} format2={}", n, hllpp1.format, hllpp2.format, any);
                    throw any;
                }
            }
            org.junit.Assert.fail("consistentBytes_literalMutation81 should have thrown ArrayIndexOutOfBoundsException");
        } catch (java.lang.ArrayIndexOutOfBoundsException eee) {
        }
    }

    /* amplification of com.clearspring.analytics.stream.cardinality.TestHyperLogLogPlus#consistentBytes */
    /* amplification of com.clearspring.analytics.stream.cardinality.TestHyperLogLogPlus#consistentBytes_literalMutation73 */
    @org.junit.Test
    public void consistentBytes_literalMutation73_failAssert20_literalMutation1101_literalMutation10027() throws java.lang.Throwable {
        // AssertGenerator generate try/catch block with fail statement
        try {
            int[] NUM_STRINGS = new int[]{ // TestDataMutator on numbers
            15 , 50 , 100 , 200 , 300 , 500 , 1000 , 10000 , 100000 };
            // AssertGenerator add assertion
            int[] array_1148961842 = new int[]{15, 50, 100, 200, 300, 500, 1000, 10000, 100000};
	int[] array_2012671097 = (int[])NUM_STRINGS;
	for(int ii = 0; ii <array_1148961842.length; ii++) {
		org.junit.Assert.assertEquals(array_1148961842[ii], array_2012671097[ii]);
	};
            // AssertGenerator add assertion
            int[] array_1095319509 = new int[]{15, 50, 100, 200, 300, 500, 1000, 10000, 100000};
	int[] array_387884420 = (int[])NUM_STRINGS;
	for(int ii = 0; ii <array_1095319509.length; ii++) {
		org.junit.Assert.assertEquals(array_1095319509[ii], array_387884420[ii]);
	};
            for (int n : NUM_STRINGS) {
                java.lang.String[] strings = new java.lang.String[n];
                for (int i = 0; i < n; i++) {
                    strings[i] = org.apache.commons.lang3.RandomStringUtils.randomAlphabetic(20);
                }
                com.clearspring.analytics.stream.cardinality.HyperLogLogPlus hllpp1 = new com.clearspring.analytics.stream.cardinality.HyperLogLogPlus(5, 0);
                // AssertGenerator add assertion
                org.junit.Assert.assertEquals(((com.clearspring.analytics.stream.cardinality.HyperLogLogPlus)hllpp1).cardinality(), 0L);
                // AssertGenerator add assertion
                byte[] array_859198184 = new byte[]{-1, -1, -1, -2, 5, 0, 0, 24, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
	byte[] array_1784259162 = (byte[])((com.clearspring.analytics.stream.cardinality.HyperLogLogPlus)hllpp1).getBytes();
	for(int ii = 0; ii <array_859198184.length; ii++) {
		org.junit.Assert.assertEquals(array_859198184[ii], array_1784259162[ii]);
	};
                // AssertGenerator add assertion
                org.junit.Assert.assertEquals(((com.clearspring.analytics.stream.cardinality.HyperLogLogPlus)hllpp1).sizeof(), 24);
                com.clearspring.analytics.stream.cardinality.HyperLogLogPlus hllpp2 = new com.clearspring.analytics.stream.cardinality.HyperLogLogPlus(5, 2);
                for (int i = 0; i < n; i++) {
                    hllpp1.offer(strings[i]);
                    hllpp2.offer(strings[((n - 1) - i)]);
                }
                // calling these here ensures their internal state (format type) is stable for the rest of these checks.
                // (end users have no need for this because they cannot access the format directly anyway)
                hllpp1.mergeTempList();
                hllpp2.mergeTempList();
                com.clearspring.analytics.stream.cardinality.TestHyperLogLogPlusAmpl.log.debug("n={} format1={} format2={}", n, hllpp1.format, hllpp2.format);
                try {
                    if ((hllpp1.format) == (hllpp2.format)) {
                        // MethodAssertGenerator build local variable
                        Object o_36_1 = hllpp2.getBytes();
                        // MethodAssertGenerator build local variable
                        Object o_33_1 = hllpp2.hashCode();
                        // MethodAssertGenerator build local variable
                        Object o_33_0 = hllpp1.hashCode();
                        // MethodAssertGenerator build local variable
                        Object o_36_0 = hllpp1.getBytes();
                    }else {
                    }
                } catch (java.lang.Throwable any) {
                    com.clearspring.analytics.stream.cardinality.TestHyperLogLogPlusAmpl.log.error("n={} format1={} format2={}", n, hllpp1.format, hllpp2.format, any);
                    throw any;
                }
            }
            org.junit.Assert.fail("consistentBytes_literalMutation73 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of com.clearspring.analytics.stream.cardinality.TestHyperLogLogPlus#consistentBytes */
    /* amplification of com.clearspring.analytics.stream.cardinality.TestHyperLogLogPlus#consistentBytes_literalMutation81 */
    @org.junit.Test
    public void consistentBytes_literalMutation81_failAssert27_literalMutation1355_literalMutation5650() throws java.lang.Throwable {
        // AssertGenerator generate try/catch block with fail statement
        try {
            int[] NUM_STRINGS = new int[]{ 30 , 50 , // TestDataMutator on numbers
            99 , 200 , 300 , 500 , 1000 , 10000 , 100000 };
            // AssertGenerator add assertion
            int[] array_2079849543 = new int[]{30, 50, 99, 200, 300, 500, 1000, 10000, 100000};
	int[] array_508095557 = (int[])NUM_STRINGS;
	for(int ii = 0; ii <array_2079849543.length; ii++) {
		org.junit.Assert.assertEquals(array_2079849543[ii], array_508095557[ii]);
	};
            // AssertGenerator add assertion
            int[] array_1243378167 = new int[]{30, 50, 99, 200, 300, 500, 1000, 10000, 100000};
	int[] array_1078767001 = (int[])NUM_STRINGS;
	for(int ii = 0; ii <array_1243378167.length; ii++) {
		org.junit.Assert.assertEquals(array_1243378167[ii], array_1078767001[ii]);
	};
            for (int n : NUM_STRINGS) {
                java.lang.String[] strings = new java.lang.String[n];
                for (int i = 0; i < n; i++) {
                    strings[i] = org.apache.commons.lang3.RandomStringUtils.randomAlphabetic(20);
                }
                com.clearspring.analytics.stream.cardinality.HyperLogLogPlus hllpp1 = new com.clearspring.analytics.stream.cardinality.HyperLogLogPlus(5, 5);
                com.clearspring.analytics.stream.cardinality.HyperLogLogPlus hllpp2 = new com.clearspring.analytics.stream.cardinality.HyperLogLogPlus(5, 6);
                // AssertGenerator add assertion
                org.junit.Assert.assertEquals(((com.clearspring.analytics.stream.cardinality.HyperLogLogPlus)hllpp2).sizeof(), 24);
                // AssertGenerator add assertion
                org.junit.Assert.assertEquals(((com.clearspring.analytics.stream.cardinality.HyperLogLogPlus)hllpp2).cardinality(), 0L);
                // AssertGenerator add assertion
                byte[] array_603194799 = new byte[]{-1, -1, -1, -2, 5, 6, 1, 0};
	byte[] array_816940926 = (byte[])((com.clearspring.analytics.stream.cardinality.HyperLogLogPlus)hllpp2).getBytes();
	for(int ii = 0; ii <array_603194799.length; ii++) {
		org.junit.Assert.assertEquals(array_603194799[ii], array_816940926[ii]);
	};
                // AssertGenerator add assertion
                org.junit.Assert.assertTrue(hllpp2.equals(hllpp1));
                for (int i = 0; i < n; i++) {
                    hllpp1.offer(strings[i]);
                    hllpp2.offer(strings[((n - 0) - i)]);
                }
                // calling these here ensures their internal state (format type) is stable for the rest of these checks.
                // (end users have no need for this because they cannot access the format directly anyway)
                hllpp1.mergeTempList();
                hllpp2.mergeTempList();
                com.clearspring.analytics.stream.cardinality.TestHyperLogLogPlusAmpl.log.debug("n={} format1={} format2={}", n, hllpp1.format, hllpp2.format);
                try {
                    if ((hllpp1.format) == (hllpp2.format)) {
                        // MethodAssertGenerator build local variable
                        Object o_36_1 = hllpp2.getBytes();
                        // MethodAssertGenerator build local variable
                        Object o_33_1 = hllpp2.hashCode();
                        // MethodAssertGenerator build local variable
                        Object o_33_0 = hllpp1.hashCode();
                        // MethodAssertGenerator build local variable
                        Object o_36_0 = hllpp1.getBytes();
                    }else {
                    }
                } catch (java.lang.Throwable any) {
                    com.clearspring.analytics.stream.cardinality.TestHyperLogLogPlusAmpl.log.error("n={} format1={} format2={}", n, hllpp1.format, hllpp2.format, any);
                    throw any;
                }
            }
            org.junit.Assert.fail("consistentBytes_literalMutation81 should have thrown ArrayIndexOutOfBoundsException");
        } catch (java.lang.ArrayIndexOutOfBoundsException eee) {
        }
    }

    /* amplification of com.clearspring.analytics.stream.cardinality.TestHyperLogLogPlus#consistentBytes */
    /* amplification of com.clearspring.analytics.stream.cardinality.TestHyperLogLogPlus#consistentBytes_literalMutation77 */
    @org.junit.Test
    public void consistentBytes_literalMutation77_failAssert24_literalMutation1183_literalMutation6289_failAssert30() throws java.lang.Throwable {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // AssertGenerator generate try/catch block with fail statement
            try {
                int[] NUM_STRINGS = new int[]{ // TestDataMutator on numbers
                60 , 50 , 100 , 200 , 300 , 500 , 1000 , 10000 , 100000 };
                // AssertGenerator add assertion
                int[] array_541362158 = new int[]{60, 50, 100, 200, 300, 500, 1000, 10000, 100000};
	int[] array_473996267 = (int[])NUM_STRINGS;
	for(int ii = 0; ii <array_541362158.length; ii++) {
		org.junit.Assert.assertEquals(array_541362158[ii], array_473996267[ii]);
	};
                for (int n : NUM_STRINGS) {
                    java.lang.String[] strings = new java.lang.String[n];
                    for (int i = 0; i < n; i++) {
                        strings[i] = org.apache.commons.lang3.RandomStringUtils.randomAlphabetic(20);
                    }
                    com.clearspring.analytics.stream.cardinality.HyperLogLogPlus hllpp1 = new com.clearspring.analytics.stream.cardinality.HyperLogLogPlus(6, 5);
                    com.clearspring.analytics.stream.cardinality.HyperLogLogPlus hllpp2 = new com.clearspring.analytics.stream.cardinality.HyperLogLogPlus(5, 5);
                    for (int i = -1; i < n; i++) {
                        hllpp1.offer(strings[i]);
                        hllpp2.offer(strings[((n - 1) - i)]);
                    }
                    // calling these here ensures their internal state (format type) is stable for the rest of these checks.
                    // (end users have no need for this because they cannot access the format directly anyway)
                    hllpp1.mergeTempList();
                    hllpp2.mergeTempList();
                    com.clearspring.analytics.stream.cardinality.TestHyperLogLogPlusAmpl.log.debug("n={} format1={} format2={}", n, hllpp1.format, hllpp2.format);
                    try {
                        if ((hllpp1.format) == (hllpp2.format)) {
                            // MethodAssertGenerator build local variable
                            Object o_36_1 = hllpp2.getBytes();
                            // MethodAssertGenerator build local variable
                            Object o_33_1 = hllpp2.hashCode();
                            // MethodAssertGenerator build local variable
                            Object o_33_0 = hllpp1.hashCode();
                            // MethodAssertGenerator build local variable
                            Object o_36_0 = hllpp1.getBytes();
                        }else {
                        }
                    } catch (java.lang.Throwable any) {
                        com.clearspring.analytics.stream.cardinality.TestHyperLogLogPlusAmpl.log.error("n={} format1={} format2={}", n, hllpp1.format, hllpp2.format, any);
                        throw any;
                    }
                }
                org.junit.Assert.fail("consistentBytes_literalMutation77 should have thrown ArrayIndexOutOfBoundsException");
            } catch (java.lang.ArrayIndexOutOfBoundsException eee) {
            }
            org.junit.Assert.fail("consistentBytes_literalMutation77_failAssert24_literalMutation1183_literalMutation6289 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of com.clearspring.analytics.stream.cardinality.TestHyperLogLogPlus#consistentBytes */
    /* amplification of com.clearspring.analytics.stream.cardinality.TestHyperLogLogPlus#consistentBytes_literalMutation81 */
    @org.junit.Test
    public void consistentBytes_literalMutation81_failAssert27_literalMutation1355_literalMutation5658() throws java.lang.Throwable {
        // AssertGenerator generate try/catch block with fail statement
        try {
            int[] NUM_STRINGS = new int[]{ 30 , 50 , // TestDataMutator on numbers
            99 , 200 , 300 , 500 , 1000 , 10000 , 100000 };
            // AssertGenerator add assertion
            int[] array_355054697 = new int[]{30, 50, 99, 200, 300, 500, 1000, 10000, 100000};
	int[] array_631565541 = (int[])NUM_STRINGS;
	for(int ii = 0; ii <array_355054697.length; ii++) {
		org.junit.Assert.assertEquals(array_355054697[ii], array_631565541[ii]);
	};
            // AssertGenerator add assertion
            int[] array_1243378167 = new int[]{30, 50, 99, 200, 300, 500, 1000, 10000, 100000};
	int[] array_1078767001 = (int[])NUM_STRINGS;
	for(int ii = 0; ii <array_1243378167.length; ii++) {
		org.junit.Assert.assertEquals(array_1243378167[ii], array_1078767001[ii]);
	};
            for (int n : NUM_STRINGS) {
                java.lang.String[] strings = new java.lang.String[n];
                for (int i = 0; i < n; i++) {
                    strings[i] = org.apache.commons.lang3.RandomStringUtils.randomAlphabetic(20);
                }
                com.clearspring.analytics.stream.cardinality.HyperLogLogPlus hllpp1 = new com.clearspring.analytics.stream.cardinality.HyperLogLogPlus(5, 5);
                com.clearspring.analytics.stream.cardinality.HyperLogLogPlus hllpp2 = new com.clearspring.analytics.stream.cardinality.HyperLogLogPlus(5, 5);
                for (int i = 0; i < n; i++) {
                    hllpp1.offer(strings[i]);
                    // AssertGenerator replace invocation
                    boolean o_consistentBytes_literalMutation81_failAssert27_literalMutation1355_literalMutation5658__26 = hllpp2.offer(strings[((n - 2) - i)]);
                    // AssertGenerator add assertion
                    org.junit.Assert.assertTrue(o_consistentBytes_literalMutation81_failAssert27_literalMutation1355_literalMutation5658__26);
                }
                // calling these here ensures their internal state (format type) is stable for the rest of these checks.
                // (end users have no need for this because they cannot access the format directly anyway)
                hllpp1.mergeTempList();
                hllpp2.mergeTempList();
                com.clearspring.analytics.stream.cardinality.TestHyperLogLogPlusAmpl.log.debug("n={} format1={} format2={}", n, hllpp1.format, hllpp2.format);
                try {
                    if ((hllpp1.format) == (hllpp2.format)) {
                        // MethodAssertGenerator build local variable
                        Object o_36_1 = hllpp2.getBytes();
                        // MethodAssertGenerator build local variable
                        Object o_33_1 = hllpp2.hashCode();
                        // MethodAssertGenerator build local variable
                        Object o_33_0 = hllpp1.hashCode();
                        // MethodAssertGenerator build local variable
                        Object o_36_0 = hllpp1.getBytes();
                    }else {
                    }
                } catch (java.lang.Throwable any) {
                    com.clearspring.analytics.stream.cardinality.TestHyperLogLogPlusAmpl.log.error("n={} format1={} format2={}", n, hllpp1.format, hllpp2.format, any);
                    throw any;
                }
            }
            org.junit.Assert.fail("consistentBytes_literalMutation81 should have thrown ArrayIndexOutOfBoundsException");
        } catch (java.lang.ArrayIndexOutOfBoundsException eee) {
        }
    }

    /* amplification of com.clearspring.analytics.stream.cardinality.TestHyperLogLogPlus#testComputeCount */
    @org.junit.Test
    public void testComputeCount_literalMutation11645_failAssert4() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            com.clearspring.analytics.stream.cardinality.HyperLogLogPlus hyperLogLogPlus = new com.clearspring.analytics.stream.cardinality.HyperLogLogPlus(14, 50);
            int count = 70000;
            for (int i = 0; i < count; i++) {
                hyperLogLogPlus.offer(("i" + i));
            }
            long estimate = hyperLogLogPlus.cardinality();
            double se = count * (1.04 / (java.lang.Math.sqrt(java.lang.Math.pow(2, 14))));
            long expectedCardinality = count;
            java.lang.System.out.println(((((("Expect estimate: " + estimate) + " is between ") + (expectedCardinality - (3 * se))) + " and ") + (expectedCardinality + (3 * se))));
            // MethodAssertGenerator build local variable
            Object o_17_0 = estimate >= (expectedCardinality - (3 * se));
            // MethodAssertGenerator build local variable
            Object o_18_0 = estimate <= (expectedCardinality + (3 * se));
            org.junit.Assert.fail("testComputeCount_literalMutation11645 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of com.clearspring.analytics.stream.cardinality.TestHyperLogLogPlus#testComputeCount */
    @org.junit.Test
    public void testComputeCount_literalMutation11640_failAssert2() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            com.clearspring.analytics.stream.cardinality.HyperLogLogPlus hyperLogLogPlus = new com.clearspring.analytics.stream.cardinality.HyperLogLogPlus(28, 25);
            int count = 70000;
            for (int i = 0; i < count; i++) {
                hyperLogLogPlus.offer(("i" + i));
            }
            long estimate = hyperLogLogPlus.cardinality();
            double se = count * (1.04 / (java.lang.Math.sqrt(java.lang.Math.pow(2, 14))));
            long expectedCardinality = count;
            java.lang.System.out.println(((((("Expect estimate: " + estimate) + " is between ") + (expectedCardinality - (3 * se))) + " and ") + (expectedCardinality + (3 * se))));
            // MethodAssertGenerator build local variable
            Object o_17_0 = estimate >= (expectedCardinality - (3 * se));
            // MethodAssertGenerator build local variable
            Object o_18_0 = estimate <= (expectedCardinality + (3 * se));
            org.junit.Assert.fail("testComputeCount_literalMutation11640 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of com.clearspring.analytics.stream.cardinality.TestHyperLogLogPlus#testComputeCount */
    /* amplification of com.clearspring.analytics.stream.cardinality.TestHyperLogLogPlus#testComputeCount_cf11699 */
    @org.junit.Test(timeout = 10000)
    public void testComputeCount_cf11699_failAssert10_literalMutation12093_failAssert4() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // AssertGenerator generate try/catch block with fail statement
            try {
                com.clearspring.analytics.stream.cardinality.HyperLogLogPlus hyperLogLogPlus = new com.clearspring.analytics.stream.cardinality.HyperLogLogPlus(14, 12);
                int count = 70000;
                for (int i = 0; i < count; i++) {
                    hyperLogLogPlus.offer(("i" + i));
                }
                long estimate = hyperLogLogPlus.cardinality();
                double se = count * (1.04 / (java.lang.Math.sqrt(java.lang.Math.pow(2, 14))));
                long expectedCardinality = count;
                java.lang.System.out.println(((((("Expect estimate: " + estimate) + " is between ") + (expectedCardinality - (3 * se))) + " and ") + (expectedCardinality + (3 * se))));
                // MethodAssertGenerator build local variable
                Object o_16_0 = estimate >= (expectedCardinality - (3 * se));
                // StatementAdderOnAssert create random local variable
                java.lang.Object vc_177 = new java.lang.Object();
                // StatementAdderOnAssert create null value
                com.clearspring.analytics.stream.cardinality.HyperLogLog vc_174 = (com.clearspring.analytics.stream.cardinality.HyperLogLog)null;
                // StatementAdderMethod cloned existing statement
                vc_174.offer(vc_177);
                // MethodAssertGenerator build local variable
                Object o_23_0 = estimate <= (expectedCardinality + (3 * se));
                org.junit.Assert.fail("testComputeCount_cf11699 should have thrown NullPointerException");
            } catch (java.lang.NullPointerException eee) {
            }
            org.junit.Assert.fail("testComputeCount_cf11699_failAssert10_literalMutation12093 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of com.clearspring.analytics.stream.cardinality.TestHyperLogLogPlus#testComputeCount */
    /* amplification of com.clearspring.analytics.stream.cardinality.TestHyperLogLogPlus#testComputeCount_cf11710 */
    @org.junit.Test(timeout = 10000)
    public void testComputeCount_cf11710_failAssert14_literalMutation12360_failAssert3() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // AssertGenerator generate try/catch block with fail statement
            try {
                com.clearspring.analytics.stream.cardinality.HyperLogLogPlus hyperLogLogPlus = new com.clearspring.analytics.stream.cardinality.HyperLogLogPlus(14, 50);
                int count = 70000;
                for (int i = 0; i < count; i++) {
                    hyperLogLogPlus.offer(("i" + i));
                }
                long estimate = hyperLogLogPlus.cardinality();
                double se = count * (1.04 / (java.lang.Math.sqrt(java.lang.Math.pow(2, 14))));
                long expectedCardinality = count;
                java.lang.System.out.println(((((("Expect estimate: " + estimate) + " is between ") + (expectedCardinality - (3 * se))) + " and ") + (expectedCardinality + (3 * se))));
                // MethodAssertGenerator build local variable
                Object o_16_0 = estimate >= (expectedCardinality - (3 * se));
                // StatementAdderOnAssert create null value
                com.clearspring.analytics.stream.cardinality.HyperLogLog vc_181 = (com.clearspring.analytics.stream.cardinality.HyperLogLog)null;
                // StatementAdderMethod cloned existing statement
                vc_181.offerHashed(estimate);
                // MethodAssertGenerator build local variable
                Object o_21_0 = estimate <= (expectedCardinality + (3 * se));
                org.junit.Assert.fail("testComputeCount_cf11710 should have thrown NullPointerException");
            } catch (java.lang.NullPointerException eee) {
            }
            org.junit.Assert.fail("testComputeCount_cf11710_failAssert14_literalMutation12360 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of com.clearspring.analytics.stream.cardinality.TestHyperLogLogPlus#testEquals */
    @org.junit.Test
    public void testEquals_literalMutation13017_failAssert5() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            com.clearspring.analytics.stream.cardinality.HyperLogLogPlus hll1 = new com.clearspring.analytics.stream.cardinality.HyperLogLogPlus(5, 50);
            com.clearspring.analytics.stream.cardinality.HyperLogLogPlus hll2 = new com.clearspring.analytics.stream.cardinality.HyperLogLogPlus(5, 25);
            hll1.offer("A");
            hll2.offer("A");
            hll2.offer("B");
            hll2.offer("C");
            hll2.offer("D");
            com.clearspring.analytics.stream.cardinality.HyperLogLogPlus hll3 = new com.clearspring.analytics.stream.cardinality.HyperLogLogPlus(5, 25);
            for (int i = 0; i < 50000; i++) {
                hll3.offer(("" + i));
            }
            org.junit.Assert.fail("testEquals_literalMutation13017 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of com.clearspring.analytics.stream.cardinality.TestHyperLogLogPlus#testEquals */
    @org.junit.Test(timeout = 10000)
    public void testEquals_add13004() {
        com.clearspring.analytics.stream.cardinality.HyperLogLogPlus hll1 = new com.clearspring.analytics.stream.cardinality.HyperLogLogPlus(5, 25);
        com.clearspring.analytics.stream.cardinality.HyperLogLogPlus hll2 = new com.clearspring.analytics.stream.cardinality.HyperLogLogPlus(5, 25);
        // AssertGenerator replace invocation
        boolean o_testEquals_add13004__5 = hll1.offer("A");
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_testEquals_add13004__5);
        // AssertGenerator replace invocation
        boolean o_testEquals_add13004__6 = // MethodCallAdder
hll2.offer("A");
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_testEquals_add13004__6);
        // AssertGenerator replace invocation
        boolean o_testEquals_add13004__8 = hll2.offer("A");
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_testEquals_add13004__8);
        org.junit.Assert.assertEquals(hll1, hll2);
        // AssertGenerator replace invocation
        boolean o_testEquals_add13004__10 = hll2.offer("B");
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_testEquals_add13004__10);
        // AssertGenerator replace invocation
        boolean o_testEquals_add13004__11 = hll2.offer("C");
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_testEquals_add13004__11);
        // AssertGenerator replace invocation
        boolean o_testEquals_add13004__12 = hll2.offer("D");
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_testEquals_add13004__12);
        org.junit.Assert.assertNotEquals(hll1, hll2);
        com.clearspring.analytics.stream.cardinality.HyperLogLogPlus hll3 = new com.clearspring.analytics.stream.cardinality.HyperLogLogPlus(5, 25);
        for (int i = 0; i < 50000; i++) {
            hll3.offer(("" + i));
        }
        org.junit.Assert.assertNotEquals(hll1, hll3);
    }

    /* amplification of com.clearspring.analytics.stream.cardinality.TestHyperLogLogPlus#testEquals */
    @org.junit.Test
    public void testEquals_literalMutation13010_failAssert0() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            com.clearspring.analytics.stream.cardinality.HyperLogLogPlus hll1 = new com.clearspring.analytics.stream.cardinality.HyperLogLogPlus(0, 25);
            com.clearspring.analytics.stream.cardinality.HyperLogLogPlus hll2 = new com.clearspring.analytics.stream.cardinality.HyperLogLogPlus(5, 25);
            hll1.offer("A");
            hll2.offer("A");
            hll2.offer("B");
            hll2.offer("C");
            hll2.offer("D");
            com.clearspring.analytics.stream.cardinality.HyperLogLogPlus hll3 = new com.clearspring.analytics.stream.cardinality.HyperLogLogPlus(5, 25);
            for (int i = 0; i < 50000; i++) {
                hll3.offer(("" + i));
            }
            org.junit.Assert.fail("testEquals_literalMutation13010 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of com.clearspring.analytics.stream.cardinality.TestHyperLogLogPlus#testEquals */
    @org.junit.Test
    public void testEquals_literalMutation13043() {
        com.clearspring.analytics.stream.cardinality.HyperLogLogPlus hll1 = new com.clearspring.analytics.stream.cardinality.HyperLogLogPlus(5, 25);
        com.clearspring.analytics.stream.cardinality.HyperLogLogPlus hll2 = new com.clearspring.analytics.stream.cardinality.HyperLogLogPlus(5, 25);
        // AssertGenerator replace invocation
        boolean o_testEquals_literalMutation13043__5 = hll1.offer("A");
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_testEquals_literalMutation13043__5);
        // AssertGenerator replace invocation
        boolean o_testEquals_literalMutation13043__6 = hll2.offer("A");
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_testEquals_literalMutation13043__6);
        org.junit.Assert.assertEquals(hll1, hll2);
        // AssertGenerator replace invocation
        boolean o_testEquals_literalMutation13043__8 = hll2.offer("B");
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_testEquals_literalMutation13043__8);
        // AssertGenerator replace invocation
        boolean o_testEquals_literalMutation13043__9 = hll2.offer("C");
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_testEquals_literalMutation13043__9);
        // AssertGenerator replace invocation
        boolean o_testEquals_literalMutation13043__10 = hll2.offer("D");
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_testEquals_literalMutation13043__10);
        org.junit.Assert.assertNotEquals(hll1, hll2);
        com.clearspring.analytics.stream.cardinality.HyperLogLogPlus hll3 = new com.clearspring.analytics.stream.cardinality.HyperLogLogPlus(5, 25);
        for (int i = 0; i < 0; i++) {
            hll3.offer(("" + i));
        }
        org.junit.Assert.assertNotEquals(hll1, hll3);
    }

    /* amplification of com.clearspring.analytics.stream.cardinality.TestHyperLogLogPlus#testEquals */
    /* amplification of com.clearspring.analytics.stream.cardinality.TestHyperLogLogPlus#testEquals_literalMutation13036 */
    @org.junit.Test
    public void testEquals_literalMutation13036_literalMutation14344_failAssert15() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            com.clearspring.analytics.stream.cardinality.HyperLogLogPlus hll1 = new com.clearspring.analytics.stream.cardinality.HyperLogLogPlus(0, 25);
            com.clearspring.analytics.stream.cardinality.HyperLogLogPlus hll2 = new com.clearspring.analytics.stream.cardinality.HyperLogLogPlus(5, 25);
            // AssertGenerator replace invocation
            boolean o_testEquals_literalMutation13036__5 = hll1.offer("A");
            // MethodAssertGenerator build local variable
            Object o_8_0 = o_testEquals_literalMutation13036__5;
            // AssertGenerator replace invocation
            boolean o_testEquals_literalMutation13036__6 = hll2.offer("A");
            // MethodAssertGenerator build local variable
            Object o_12_0 = o_testEquals_literalMutation13036__6;
            // AssertGenerator replace invocation
            boolean o_testEquals_literalMutation13036__8 = hll2.offer("B");
            // MethodAssertGenerator build local variable
            Object o_17_0 = o_testEquals_literalMutation13036__8;
            // AssertGenerator replace invocation
            boolean o_testEquals_literalMutation13036__9 = hll2.offer("C");
            // MethodAssertGenerator build local variable
            Object o_21_0 = o_testEquals_literalMutation13036__9;
            // AssertGenerator replace invocation
            boolean o_testEquals_literalMutation13036__10 = hll2.offer("D");
            // MethodAssertGenerator build local variable
            Object o_25_0 = o_testEquals_literalMutation13036__10;
            com.clearspring.analytics.stream.cardinality.HyperLogLogPlus hll3 = new com.clearspring.analytics.stream.cardinality.HyperLogLogPlus(5, 12);
            for (int i = 0; i < 50000; i++) {
                hll3.offer(("" + i));
            }
            org.junit.Assert.fail("testEquals_literalMutation13036_literalMutation14344 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of com.clearspring.analytics.stream.cardinality.TestHyperLogLogPlus#testEquals */
    /* amplification of com.clearspring.analytics.stream.cardinality.TestHyperLogLogPlus#testEquals_literalMutation13038 */
    @org.junit.Test
    public void testEquals_literalMutation13038_literalMutation14425_failAssert12() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            com.clearspring.analytics.stream.cardinality.HyperLogLogPlus hll1 = new com.clearspring.analytics.stream.cardinality.HyperLogLogPlus(5, 50);
            com.clearspring.analytics.stream.cardinality.HyperLogLogPlus hll2 = new com.clearspring.analytics.stream.cardinality.HyperLogLogPlus(5, 25);
            // AssertGenerator replace invocation
            boolean o_testEquals_literalMutation13038__5 = hll1.offer("A");
            // MethodAssertGenerator build local variable
            Object o_8_0 = o_testEquals_literalMutation13038__5;
            // AssertGenerator replace invocation
            boolean o_testEquals_literalMutation13038__6 = hll2.offer("A");
            // MethodAssertGenerator build local variable
            Object o_12_0 = o_testEquals_literalMutation13038__6;
            // AssertGenerator replace invocation
            boolean o_testEquals_literalMutation13038__8 = hll2.offer("B");
            // MethodAssertGenerator build local variable
            Object o_17_0 = o_testEquals_literalMutation13038__8;
            // AssertGenerator replace invocation
            boolean o_testEquals_literalMutation13038__9 = hll2.offer("C");
            // MethodAssertGenerator build local variable
            Object o_21_0 = o_testEquals_literalMutation13038__9;
            // AssertGenerator replace invocation
            boolean o_testEquals_literalMutation13038__10 = hll2.offer("D");
            // MethodAssertGenerator build local variable
            Object o_25_0 = o_testEquals_literalMutation13038__10;
            com.clearspring.analytics.stream.cardinality.HyperLogLogPlus hll3 = new com.clearspring.analytics.stream.cardinality.HyperLogLogPlus(5, 26);
            for (int i = 0; i < 50000; i++) {
                hll3.offer(("" + i));
            }
            org.junit.Assert.fail("testEquals_literalMutation13038_literalMutation14425 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of com.clearspring.analytics.stream.cardinality.TestHyperLogLogPlus#testEquals */
    /* amplification of com.clearspring.analytics.stream.cardinality.TestHyperLogLogPlus#testEquals_cf13055 */
    @org.junit.Test(timeout = 10000)
    public void testEquals_cf13055_failAssert20_add15873() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            com.clearspring.analytics.stream.cardinality.HyperLogLogPlus hll1 = new com.clearspring.analytics.stream.cardinality.HyperLogLogPlus(5, 25);
            com.clearspring.analytics.stream.cardinality.HyperLogLogPlus hll2 = new com.clearspring.analytics.stream.cardinality.HyperLogLogPlus(5, 25);
            hll1.offer("A");
            hll2.offer("A");
            hll2.offer("B");
            // AssertGenerator replace invocation
            boolean o_testEquals_cf13055_failAssert20_add15873__10 = // MethodCallAdder
hll2.offer("C");
            // AssertGenerator add assertion
            org.junit.Assert.assertTrue(o_testEquals_cf13055_failAssert20_add15873__10);
            hll2.offer("C");
            hll2.offer("D");
            com.clearspring.analytics.stream.cardinality.HyperLogLogPlus hll3 = new com.clearspring.analytics.stream.cardinality.HyperLogLogPlus(5, 25);
            for (int i = 0; i < 50000; i++) {
                hll3.offer(("" + i));
            }
            // StatementAdderOnAssert create random local variable
            int vc_209 = -146470443;
            // StatementAdderOnAssert create null value
            com.clearspring.analytics.stream.cardinality.HyperLogLog vc_207 = (com.clearspring.analytics.stream.cardinality.HyperLogLog)null;
            // StatementAdderMethod cloned existing statement
            vc_207.offerHashed(vc_209);
            org.junit.Assert.fail("testEquals_cf13055 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    /* amplification of com.clearspring.analytics.stream.cardinality.TestHyperLogLogPlus#testEquals */
    /* amplification of com.clearspring.analytics.stream.cardinality.TestHyperLogLogPlus#testEquals_cf13051 */
    @org.junit.Test(timeout = 10000)
    public void testEquals_cf13051_failAssert18_literalMutation15804_failAssert41_add17554() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // AssertGenerator generate try/catch block with fail statement
            try {
                com.clearspring.analytics.stream.cardinality.HyperLogLogPlus hll1 = new com.clearspring.analytics.stream.cardinality.HyperLogLogPlus(5, 25);
                com.clearspring.analytics.stream.cardinality.HyperLogLogPlus hll2 = new com.clearspring.analytics.stream.cardinality.HyperLogLogPlus(5, 25);
                // AssertGenerator replace invocation
                boolean o_testEquals_cf13051_failAssert18_literalMutation15804_failAssert41_add17554__9 = // MethodCallAdder
hll1.offer("A");
                // AssertGenerator add assertion
                org.junit.Assert.assertTrue(o_testEquals_cf13051_failAssert18_literalMutation15804_failAssert41_add17554__9);
                hll1.offer("A");
                hll2.offer("A");
                hll2.offer("B");
                hll2.offer("C");
                hll2.offer("D");
                com.clearspring.analytics.stream.cardinality.HyperLogLogPlus hll3 = new com.clearspring.analytics.stream.cardinality.HyperLogLogPlus(5, 50);
                for (int i = 0; i < 50000; i++) {
                    hll3.offer(("" + i));
                }
                // StatementAdderOnAssert create random local variable
                java.lang.Object vc_206 = new java.lang.Object();
                // StatementAdderOnAssert create null value
                com.clearspring.analytics.stream.cardinality.HyperLogLog vc_203 = (com.clearspring.analytics.stream.cardinality.HyperLogLog)null;
                // StatementAdderMethod cloned existing statement
                vc_203.offer(vc_206);
                org.junit.Assert.fail("testEquals_cf13051 should have thrown NullPointerException");
            } catch (java.lang.NullPointerException eee) {
            }
            org.junit.Assert.fail("testEquals_cf13051_failAssert18_literalMutation15804 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of com.clearspring.analytics.stream.cardinality.TestHyperLogLogPlus#testEquals */
    /* amplification of com.clearspring.analytics.stream.cardinality.TestHyperLogLogPlus#testEquals_literalMutation13048 */
    @org.junit.Test(timeout = 10000)
    public void testEquals_literalMutation13048_cf15196_failAssert28_literalMutation17165_failAssert3() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // AssertGenerator generate try/catch block with fail statement
            try {
                com.clearspring.analytics.stream.cardinality.HyperLogLogPlus hll1 = new com.clearspring.analytics.stream.cardinality.HyperLogLogPlus(5, 50);
                com.clearspring.analytics.stream.cardinality.HyperLogLogPlus hll2 = new com.clearspring.analytics.stream.cardinality.HyperLogLogPlus(5, 25);
                // AssertGenerator replace invocation
                boolean o_testEquals_literalMutation13048__5 = hll1.offer("A");
                // MethodAssertGenerator build local variable
                Object o_7_0 = o_testEquals_literalMutation13048__5;
                // AssertGenerator replace invocation
                boolean o_testEquals_literalMutation13048__6 = hll2.offer("A");
                // MethodAssertGenerator build local variable
                Object o_11_0 = o_testEquals_literalMutation13048__6;
                // AssertGenerator replace invocation
                boolean o_testEquals_literalMutation13048__8 = hll2.offer("B");
                // MethodAssertGenerator build local variable
                Object o_16_0 = o_testEquals_literalMutation13048__8;
                // AssertGenerator replace invocation
                boolean o_testEquals_literalMutation13048__9 = hll2.offer("C");
                // MethodAssertGenerator build local variable
                Object o_20_0 = o_testEquals_literalMutation13048__9;
                // AssertGenerator replace invocation
                boolean o_testEquals_literalMutation13048__10 = hll2.offer("D");
                // MethodAssertGenerator build local variable
                Object o_24_0 = o_testEquals_literalMutation13048__10;
                com.clearspring.analytics.stream.cardinality.HyperLogLogPlus hll3 = new com.clearspring.analytics.stream.cardinality.HyperLogLogPlus(5, 25);
                for (int i = 0; i < 50000; i++) {
                    hll3.offer(("#" + i));
                }
                // StatementAdderOnAssert create random local variable
                java.lang.Object vc_1047 = new java.lang.Object();
                // StatementAdderOnAssert create null value
                com.clearspring.analytics.stream.cardinality.HyperLogLog vc_1044 = (com.clearspring.analytics.stream.cardinality.HyperLogLog)null;
                // StatementAdderMethod cloned existing statement
                vc_1044.offer(vc_1047);
                org.junit.Assert.fail("testEquals_literalMutation13048_cf15196 should have thrown NullPointerException");
            } catch (java.lang.NullPointerException eee) {
            }
            org.junit.Assert.fail("testEquals_literalMutation13048_cf15196_failAssert28_literalMutation17165 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of com.clearspring.analytics.stream.cardinality.TestHyperLogLogPlus#testEquals */
    /* amplification of com.clearspring.analytics.stream.cardinality.TestHyperLogLogPlus#testEquals_cf13078 */
    @org.junit.Test(timeout = 10000)
    public void testEquals_cf13078_failAssert23_add16023_literalMutation16080_failAssert2() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // AssertGenerator generate try/catch block with fail statement
            try {
                com.clearspring.analytics.stream.cardinality.HyperLogLogPlus hll1 = new com.clearspring.analytics.stream.cardinality.HyperLogLogPlus(2, 25);
                com.clearspring.analytics.stream.cardinality.HyperLogLogPlus hll2 = new com.clearspring.analytics.stream.cardinality.HyperLogLogPlus(5, 25);
                // AssertGenerator replace invocation
                boolean o_testEquals_cf13078_failAssert23_add16023__7 = // MethodCallAdder
hll1.offer("A");
                // MethodAssertGenerator build local variable
                Object o_10_0 = o_testEquals_cf13078_failAssert23_add16023__7;
                hll1.offer("A");
                hll2.offer("A");
                hll2.offer("B");
                hll2.offer("C");
                hll2.offer("D");
                com.clearspring.analytics.stream.cardinality.HyperLogLogPlus hll3 = new com.clearspring.analytics.stream.cardinality.HyperLogLogPlus(5, 25);
                for (int i = 0; i < 50000; i++) {
                    hll3.offer(("" + i));
                }
                // StatementAdderOnAssert create null value
                com.clearspring.analytics.stream.cardinality.HyperLogLog vc_228 = (com.clearspring.analytics.stream.cardinality.HyperLogLog)null;
                // StatementAdderMethod cloned existing statement
                vc_228.cardinality();
                org.junit.Assert.fail("testEquals_cf13078 should have thrown NullPointerException");
            } catch (java.lang.NullPointerException eee) {
            }
            org.junit.Assert.fail("testEquals_cf13078_failAssert23_add16023_literalMutation16080 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of com.clearspring.analytics.stream.cardinality.TestHyperLogLogPlus#testHighCardinality */
    @org.junit.Test
    public void testHighCardinality_literalMutation18076_failAssert2() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            long start = java.lang.System.currentTimeMillis();
            com.clearspring.analytics.stream.cardinality.HyperLogLogPlus hyperLogLogPlus = new com.clearspring.analytics.stream.cardinality.HyperLogLogPlus(36, 25);
            int size = 10000000;
            for (int i = 0; i < size; i++) {
                hyperLogLogPlus.offer(com.clearspring.analytics.stream.cardinality.TestICardinality.streamElement(i));
            }
            java.lang.System.out.println(((((("expected: " + size) + ", estimate: ") + (hyperLogLogPlus.cardinality())) + ", time: ") + ((java.lang.System.currentTimeMillis()) - start)));
            long estimate = hyperLogLogPlus.cardinality();
            double err = (java.lang.Math.abs((estimate - size))) / ((double) (size));
            java.lang.System.out.println(("Percentage error  " + err));
            // MethodAssertGenerator build local variable
            Object o_21_0 = err < 0.1;
            org.junit.Assert.fail("testHighCardinality_literalMutation18076 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of com.clearspring.analytics.stream.cardinality.TestHyperLogLogPlus#testHighCardinality */
    @org.junit.Test
    public void testHighCardinality_literalMutation18081_failAssert4() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            long start = java.lang.System.currentTimeMillis();
            com.clearspring.analytics.stream.cardinality.HyperLogLogPlus hyperLogLogPlus = new com.clearspring.analytics.stream.cardinality.HyperLogLogPlus(18, 50);
            int size = 10000000;
            for (int i = 0; i < size; i++) {
                hyperLogLogPlus.offer(com.clearspring.analytics.stream.cardinality.TestICardinality.streamElement(i));
            }
            java.lang.System.out.println(((((("expected: " + size) + ", estimate: ") + (hyperLogLogPlus.cardinality())) + ", time: ") + ((java.lang.System.currentTimeMillis()) - start)));
            long estimate = hyperLogLogPlus.cardinality();
            double err = (java.lang.Math.abs((estimate - size))) / ((double) (size));
            java.lang.System.out.println(("Percentage error  " + err));
            // MethodAssertGenerator build local variable
            Object o_21_0 = err < 0.1;
            org.junit.Assert.fail("testHighCardinality_literalMutation18081 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of com.clearspring.analytics.stream.cardinality.TestHyperLogLogPlus#testHighCardinality */
    /* amplification of com.clearspring.analytics.stream.cardinality.TestHyperLogLogPlus#testHighCardinality_cf18121 */
    @org.junit.Test(timeout = 10000)
    public void testHighCardinality_cf18121_failAssert10_literalMutation18477_failAssert5() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // AssertGenerator generate try/catch block with fail statement
            try {
                long start = java.lang.System.currentTimeMillis();
                com.clearspring.analytics.stream.cardinality.HyperLogLogPlus hyperLogLogPlus = new com.clearspring.analytics.stream.cardinality.HyperLogLogPlus(36, 25);
                int size = 10000000;
                for (int i = 0; i < size; i++) {
                    hyperLogLogPlus.offer(com.clearspring.analytics.stream.cardinality.TestICardinality.streamElement(i));
                }
                java.lang.System.out.println(((((("expected: " + size) + ", estimate: ") + (hyperLogLogPlus.cardinality())) + ", time: ") + ((java.lang.System.currentTimeMillis()) - start)));
                long estimate = hyperLogLogPlus.cardinality();
                double err = (java.lang.Math.abs((estimate - size))) / ((double) (size));
                java.lang.System.out.println(("Percentage error  " + err));
                // StatementAdderOnAssert create null value
                com.clearspring.analytics.stream.cardinality.HyperLogLog vc_1164 = (com.clearspring.analytics.stream.cardinality.HyperLogLog)null;
                // StatementAdderMethod cloned existing statement
                vc_1164.offerHashed(size);
                // MethodAssertGenerator build local variable
                Object o_24_0 = err < 0.1;
                org.junit.Assert.fail("testHighCardinality_cf18121 should have thrown NullPointerException");
            } catch (java.lang.NullPointerException eee) {
            }
            org.junit.Assert.fail("testHighCardinality_cf18121_failAssert10_literalMutation18477 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of com.clearspring.analytics.stream.cardinality.TestHyperLogLogPlus#testHighCardinality */
    /* amplification of com.clearspring.analytics.stream.cardinality.TestHyperLogLogPlus#testHighCardinality_cf18121 */
    @org.junit.Test(timeout = 10000)
    public void testHighCardinality_cf18121_failAssert10_literalMutation18482_failAssert7() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // AssertGenerator generate try/catch block with fail statement
            try {
                long start = java.lang.System.currentTimeMillis();
                com.clearspring.analytics.stream.cardinality.HyperLogLogPlus hyperLogLogPlus = new com.clearspring.analytics.stream.cardinality.HyperLogLogPlus(18, 50);
                int size = 10000000;
                for (int i = 0; i < size; i++) {
                    hyperLogLogPlus.offer(com.clearspring.analytics.stream.cardinality.TestICardinality.streamElement(i));
                }
                java.lang.System.out.println(((((("expected: " + size) + ", estimate: ") + (hyperLogLogPlus.cardinality())) + ", time: ") + ((java.lang.System.currentTimeMillis()) - start)));
                long estimate = hyperLogLogPlus.cardinality();
                double err = (java.lang.Math.abs((estimate - size))) / ((double) (size));
                java.lang.System.out.println(("Percentage error  " + err));
                // StatementAdderOnAssert create null value
                com.clearspring.analytics.stream.cardinality.HyperLogLog vc_1164 = (com.clearspring.analytics.stream.cardinality.HyperLogLog)null;
                // StatementAdderMethod cloned existing statement
                vc_1164.offerHashed(size);
                // MethodAssertGenerator build local variable
                Object o_24_0 = err < 0.1;
                org.junit.Assert.fail("testHighCardinality_cf18121 should have thrown NullPointerException");
            } catch (java.lang.NullPointerException eee) {
            }
            org.junit.Assert.fail("testHighCardinality_cf18121_failAssert10_literalMutation18482 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of com.clearspring.analytics.stream.cardinality.TestHyperLogLogPlus#testHighCardinality */
    /* amplification of com.clearspring.analytics.stream.cardinality.TestHyperLogLogPlus#testHighCardinality_cf18183 */
    @org.junit.Test(timeout = 10000)
    public void testHighCardinality_cf18183_failAssert16_literalMutation18780() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            long start = java.lang.System.currentTimeMillis();
            com.clearspring.analytics.stream.cardinality.HyperLogLogPlus hyperLogLogPlus = new com.clearspring.analytics.stream.cardinality.HyperLogLogPlus(18, 0);
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((com.clearspring.analytics.stream.cardinality.HyperLogLogPlus)hyperLogLogPlus).sizeof(), 174764);
            int size = 10000000;
            for (int i = 0; i < size; i++) {
                hyperLogLogPlus.offer(com.clearspring.analytics.stream.cardinality.TestICardinality.streamElement(i));
            }
            java.lang.System.out.println(((((("expected: " + size) + ", estimate: ") + (hyperLogLogPlus.cardinality())) + ", time: ") + ((java.lang.System.currentTimeMillis()) - start)));
            long estimate = hyperLogLogPlus.cardinality();
            double err = (java.lang.Math.abs((estimate - size))) / ((double) (size));
            java.lang.System.out.println(("Percentage error  " + err));
            // StatementAdderOnAssert create null value
            com.clearspring.analytics.stream.cardinality.HyperLogLog vc_1185 = (com.clearspring.analytics.stream.cardinality.HyperLogLog)null;
            // StatementAdderMethod cloned existing statement
            vc_1185.cardinality();
            // MethodAssertGenerator build local variable
            Object o_24_0 = err < 0.1;
            org.junit.Assert.fail("testHighCardinality_cf18183 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    /* amplification of com.clearspring.analytics.stream.cardinality.TestHyperLogLogPlus#testHighCardinality */
    /* amplification of com.clearspring.analytics.stream.cardinality.TestHyperLogLogPlus#testHighCardinality_cf18131 */
    @org.junit.Test(timeout = 10000)
    public void testHighCardinality_cf18131_failAssert14_literalMutation18688_literalMutation20376_failAssert1() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // AssertGenerator generate try/catch block with fail statement
            try {
                // MethodAssertGenerator build local variable
                Object o_10_1 = 20000000;
                long start = java.lang.System.currentTimeMillis();
                com.clearspring.analytics.stream.cardinality.HyperLogLogPlus hyperLogLogPlus = new com.clearspring.analytics.stream.cardinality.HyperLogLogPlus(0, 25);
                int size = 20000000;
                // MethodAssertGenerator build local variable
                Object o_10_0 = size;
                for (int i = 0; i < size; i++) {
                    hyperLogLogPlus.offer(com.clearspring.analytics.stream.cardinality.TestICardinality.streamElement(i));
                }
                java.lang.System.out.println(((((("expected: " + size) + ", estimate: ") + (hyperLogLogPlus.cardinality())) + ", time: ") + ((java.lang.System.currentTimeMillis()) - start)));
                long estimate = hyperLogLogPlus.cardinality();
                double err = (java.lang.Math.abs((estimate - size))) / ((double) (size));
                java.lang.System.out.println(("Percentage error  " + err));
                // StatementAdderOnAssert create random local variable
                long vc_1169 = -9005164045801625663L;
                // StatementAdderOnAssert create null value
                com.clearspring.analytics.stream.cardinality.HyperLogLog vc_1167 = (com.clearspring.analytics.stream.cardinality.HyperLogLog)null;
                // StatementAdderMethod cloned existing statement
                vc_1167.offerHashed(vc_1169);
                // MethodAssertGenerator build local variable
                Object o_26_0 = err < 0.1;
                org.junit.Assert.fail("testHighCardinality_cf18131 should have thrown NullPointerException");
            } catch (java.lang.NullPointerException eee) {
            }
            org.junit.Assert.fail("testHighCardinality_cf18131_failAssert14_literalMutation18688_literalMutation20376 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of com.clearspring.analytics.stream.cardinality.TestHyperLogLogPlus#testHighPrecisionInitialization */
    @org.junit.Test
    public void testHighPrecisionInitialization_literalMutation21565_failAssert0() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            for (int sp = 4; sp <= 33; sp++) {
                int expectedSm = ((int) (java.lang.Math.pow(2, sp)));
                for (int p = 4; p <= sp; p++) {
                    int expectedM = ((int) (java.lang.Math.pow(2, p)));
                    com.clearspring.analytics.stream.cardinality.HyperLogLogPlus hyperLogLogPlus = new com.clearspring.analytics.stream.cardinality.HyperLogLogPlus(p, sp);
                    // MethodAssertGenerator build local variable
                    Object o_16_0 = hyperLogLogPlus.getM();
                    // MethodAssertGenerator build local variable
                    Object o_18_0 = hyperLogLogPlus.getSm();
                }
            }
            org.junit.Assert.fail("testHighPrecisionInitialization_literalMutation21565 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of com.clearspring.analytics.stream.cardinality.TestHyperLogLogPlus#testHighPrecisionInitialization */
    @org.junit.Test
    public void testHighPrecisionInitialization_literalMutation21570_failAssert2() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            for (int sp = 4; sp <= 32; sp++) {
                int expectedSm = ((int) (java.lang.Math.pow(2, sp)));
                for (int p = 2; p <= sp; p++) {
                    int expectedM = ((int) (java.lang.Math.pow(2, p)));
                    com.clearspring.analytics.stream.cardinality.HyperLogLogPlus hyperLogLogPlus = new com.clearspring.analytics.stream.cardinality.HyperLogLogPlus(p, sp);
                    // MethodAssertGenerator build local variable
                    Object o_16_0 = hyperLogLogPlus.getM();
                    // MethodAssertGenerator build local variable
                    Object o_18_0 = hyperLogLogPlus.getSm();
                }
            }
            org.junit.Assert.fail("testHighPrecisionInitialization_literalMutation21570 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of com.clearspring.analytics.stream.cardinality.TestHyperLogLogPlus#testHighPrecisionInitialization */
    @org.junit.Test
    public void testHighPrecisionInitialization_literalMutation21567_failAssert1() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            for (int sp = 4; sp <= 64; sp++) {
                int expectedSm = ((int) (java.lang.Math.pow(2, sp)));
                for (int p = 4; p <= sp; p++) {
                    int expectedM = ((int) (java.lang.Math.pow(2, p)));
                    com.clearspring.analytics.stream.cardinality.HyperLogLogPlus hyperLogLogPlus = new com.clearspring.analytics.stream.cardinality.HyperLogLogPlus(p, sp);
                    // MethodAssertGenerator build local variable
                    Object o_16_0 = hyperLogLogPlus.getM();
                    // MethodAssertGenerator build local variable
                    Object o_18_0 = hyperLogLogPlus.getSm();
                }
            }
            org.junit.Assert.fail("testHighPrecisionInitialization_literalMutation21567 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of com.clearspring.analytics.stream.cardinality.TestHyperLogLogPlus#testHighPrecisionInitialization */
    /* amplification of com.clearspring.analytics.stream.cardinality.TestHyperLogLogPlus#testHighPrecisionInitialization_cf21580 */
    @org.junit.Test(timeout = 10000)
    public void testHighPrecisionInitialization_cf21580_failAssert8_literalMutation21751_failAssert37() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // AssertGenerator generate try/catch block with fail statement
            try {
                for (int sp = 4; sp <= 32; sp++) {
                    int expectedSm = ((int) (java.lang.Math.pow(2, sp)));
                    for (int p = 2; p <= sp; p++) {
                        int expectedM = ((int) (java.lang.Math.pow(2, p)));
                        com.clearspring.analytics.stream.cardinality.HyperLogLogPlus hyperLogLogPlus = new com.clearspring.analytics.stream.cardinality.HyperLogLogPlus(p, sp);
                        // MethodAssertGenerator build local variable
                        Object o_15_0 = hyperLogLogPlus.getM();
                        // StatementAdderOnAssert create literal from method
                        int int_vc_207 = 2;
                        // StatementAdderOnAssert create null value
                        com.clearspring.analytics.stream.cardinality.HyperLogLog vc_1744 = (com.clearspring.analytics.stream.cardinality.HyperLogLog)null;
                        // StatementAdderMethod cloned existing statement
                        vc_1744.offerHashed(int_vc_207);
                        // MethodAssertGenerator build local variable
                        Object o_23_0 = hyperLogLogPlus.getSm();
                    }
                }
                org.junit.Assert.fail("testHighPrecisionInitialization_cf21580 should have thrown NullPointerException");
            } catch (java.lang.NullPointerException eee) {
            }
            org.junit.Assert.fail("testHighPrecisionInitialization_cf21580_failAssert8_literalMutation21751 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of com.clearspring.analytics.stream.cardinality.TestHyperLogLogPlus#testHighPrecisionInitialization */
    /* amplification of com.clearspring.analytics.stream.cardinality.TestHyperLogLogPlus#testHighPrecisionInitialization_cf21613 */
    @org.junit.Test(timeout = 10000)
    public void testHighPrecisionInitialization_cf21613_failAssert11_literalMutation21808_literalMutation25091_failAssert8() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // AssertGenerator generate try/catch block with fail statement
            try {
                for (int sp = 4; sp <= 16; sp++) {
                    int expectedSm = ((int) (java.lang.Math.pow(2, sp)));
                    for (int p = 2; p <= sp; p++) {
                        // MethodAssertGenerator build local variable
                        Object o_22_1 = 16;
                        int expectedM = ((int) (java.lang.Math.pow(2, p)));
                        com.clearspring.analytics.stream.cardinality.HyperLogLogPlus hyperLogLogPlus = new com.clearspring.analytics.stream.cardinality.HyperLogLogPlus(p, sp);
                        // MethodAssertGenerator build local variable
                        Object o_15_0 = hyperLogLogPlus.getM();
                        // MethodAssertGenerator build local variable
                        Object o_22_0 = o_15_0;
                        // StatementAdderOnAssert create null value
                        com.clearspring.analytics.stream.cardinality.HyperLogLog vc_1763 = (com.clearspring.analytics.stream.cardinality.HyperLogLog)null;
                        // StatementAdderMethod cloned existing statement
                        vc_1763.sizeof();
                        // MethodAssertGenerator build local variable
                        Object o_21_0 = hyperLogLogPlus.getSm();
                    }
                }
                org.junit.Assert.fail("testHighPrecisionInitialization_cf21613 should have thrown NullPointerException");
            } catch (java.lang.NullPointerException eee) {
            }
            org.junit.Assert.fail("testHighPrecisionInitialization_cf21613_failAssert11_literalMutation21808_literalMutation25091 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }
}

