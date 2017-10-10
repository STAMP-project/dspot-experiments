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

    /* amplification of com.clearspring.analytics.stream.cardinality.TestHyperLogLogPlus#consistentBytes */
    @org.junit.Test
    public void consistentBytes_literalMutation9() throws java.lang.Throwable {
        int[] NUM_STRINGS = new int[]{ // TestDataMutator on numbers
        15 , 50 , 100 , 200 , 300 , 500 , 1000 , 10000 , 100000 };
        // AssertGenerator add assertion
        int[] array_653862509 = new int[]{15, 50, 100, 200, 300, 500, 1000, 10000, 100000};
	int[] array_924657465 = (int[])NUM_STRINGS;
	for(int ii = 0; ii <array_653862509.length; ii++) {
		org.junit.Assert.assertEquals(array_653862509[ii], array_924657465[ii]);
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

    /* amplification of com.clearspring.analytics.stream.cardinality.TestHyperLogLogPlus#consistentBytes */
    @org.junit.Test
    public void consistentBytes_literalMutation50() throws java.lang.Throwable {
        int[] NUM_STRINGS = new int[]{ 30 , 50 , 100 , 200 , 300 , 500 , 1000 , 10000 , // TestDataMutator on numbers
        50000 };
        // AssertGenerator add assertion
        int[] array_939023409 = new int[]{30, 50, 100, 200, 300, 500, 1000, 10000, 50000};
	int[] array_1280943795 = (int[])NUM_STRINGS;
	for(int ii = 0; ii <array_939023409.length; ii++) {
		org.junit.Assert.assertEquals(array_939023409[ii], array_1280943795[ii]);
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

    /* amplification of com.clearspring.analytics.stream.cardinality.TestHyperLogLogPlus#consistentBytes */
    @org.junit.Test
    public void consistentBytes_literalMutation27() throws java.lang.Throwable {
        int[] NUM_STRINGS = new int[]{ 30 , 50 , 100 , 200 , // TestDataMutator on numbers
        0 , 500 , 1000 , 10000 , 100000 };
        // AssertGenerator add assertion
        int[] array_426601082 = new int[]{30, 50, 100, 200, 0, 500, 1000, 10000, 100000};
	int[] array_2084542921 = (int[])NUM_STRINGS;
	for(int ii = 0; ii <array_426601082.length; ii++) {
		org.junit.Assert.assertEquals(array_426601082[ii], array_2084542921[ii]);
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

    /* amplification of com.clearspring.analytics.stream.cardinality.TestHyperLogLogPlus#consistentBytes */
    @org.junit.Test
    public void consistentBytes_literalMutation15() throws java.lang.Throwable {
        int[] NUM_STRINGS = new int[]{ 30 , // TestDataMutator on numbers
        25 , 100 , 200 , 300 , 500 , 1000 , 10000 , 100000 };
        // AssertGenerator add assertion
        int[] array_1784148363 = new int[]{30, 25, 100, 200, 300, 500, 1000, 10000, 100000};
	int[] array_1077117872 = (int[])NUM_STRINGS;
	for(int ii = 0; ii <array_1784148363.length; ii++) {
		org.junit.Assert.assertEquals(array_1784148363[ii], array_1077117872[ii]);
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

    /* amplification of com.clearspring.analytics.stream.cardinality.TestHyperLogLogPlus#consistentBytes */
    @org.junit.Test
    public void consistentBytes_literalMutation58_failAssert5() throws java.lang.Throwable {
        // AssertGenerator generate try/catch block with fail statement
        try {
            int[] NUM_STRINGS = new int[]{ 30 , 50 , 100 , 200 , 300 , 500 , 1000 , 10000 , 100000 };
            for (int n : NUM_STRINGS) {
                java.lang.String[] strings = new java.lang.String[n];
                for (int i = 0; i < n; i++) {
                    strings[i] = org.apache.commons.lang3.RandomStringUtils.randomAlphabetic(20);
                }
                com.clearspring.analytics.stream.cardinality.HyperLogLogPlus hllpp1 = new com.clearspring.analytics.stream.cardinality.HyperLogLogPlus(2, 5);
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
                        Object o_33_1 = hllpp2.hashCode();
                        // MethodAssertGenerator build local variable
                        Object o_33_0 = hllpp1.hashCode();
                        org.junit.Assert.assertArrayEquals(hllpp1.getBytes(), hllpp2.getBytes());
                    }else {
                        org.junit.Assert.assertNotEquals(hllpp1, hllpp2);
                    }
                } catch (java.lang.Throwable any) {
                    com.clearspring.analytics.stream.cardinality.TestHyperLogLogPlusAmpl.log.error("n={} format1={} format2={}", n, hllpp1.format, hllpp2.format, any);
                    throw any;
                }
            }
            org.junit.Assert.fail("consistentBytes_literalMutation58 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of com.clearspring.analytics.stream.cardinality.TestHyperLogLogPlus#consistentBytes */
    @org.junit.Test
    public void consistentBytes_literalMutation47() throws java.lang.Throwable {
        int[] NUM_STRINGS = new int[]{ 30 , 50 , 100 , 200 , 300 , 500 , 1000 , 10000 , // TestDataMutator on numbers
        0 };
        // AssertGenerator add assertion
        int[] array_1572371338 = new int[]{30, 50, 100, 200, 300, 500, 1000, 10000, 0};
	int[] array_659198863 = (int[])NUM_STRINGS;
	for(int ii = 0; ii <array_1572371338.length; ii++) {
		org.junit.Assert.assertEquals(array_1572371338[ii], array_659198863[ii]);
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
                        Object o_33_1 = hllpp2.hashCode();
                        // MethodAssertGenerator build local variable
                        Object o_33_0 = hllpp1.hashCode();
                        org.junit.Assert.assertArrayEquals(hllpp1.getBytes(), hllpp2.getBytes());
                    }else {
                        org.junit.Assert.assertNotEquals(hllpp1, hllpp2);
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
    @org.junit.Test
    public void consistentBytes_literalMutation60_failAssert7_literalMutation6053() throws java.lang.Throwable {
        // AssertGenerator generate try/catch block with fail statement
        try {
            int[] NUM_STRINGS = new int[]{ 30 , 50 , 100 , // TestDataMutator on numbers
            201 , 300 , 500 , 1000 , 10000 , 100000 };
            // AssertGenerator add assertion
            int[] array_854321467 = new int[]{30, 50, 100, 201, 300, 500, 1000, 10000, 100000};
	int[] array_932436529 = (int[])NUM_STRINGS;
	for(int ii = 0; ii <array_854321467.length; ii++) {
		org.junit.Assert.assertEquals(array_854321467[ii], array_932436529[ii]);
	};
            for (int n : NUM_STRINGS) {
                java.lang.String[] strings = new java.lang.String[n];
                for (int i = 0; i < n; i++) {
                    strings[i] = org.apache.commons.lang3.RandomStringUtils.randomAlphabetic(20);
                }
                com.clearspring.analytics.stream.cardinality.HyperLogLogPlus hllpp1 = new com.clearspring.analytics.stream.cardinality.HyperLogLogPlus(6, 5);
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
                        Object o_33_1 = hllpp2.hashCode();
                        // MethodAssertGenerator build local variable
                        Object o_33_0 = hllpp1.hashCode();
                        org.junit.Assert.assertArrayEquals(hllpp1.getBytes(), hllpp2.getBytes());
                    }else {
                        org.junit.Assert.assertNotEquals(hllpp1, hllpp2);
                    }
                } catch (java.lang.Throwable any) {
                    com.clearspring.analytics.stream.cardinality.TestHyperLogLogPlusAmpl.log.error("n={} format1={} format2={}", n, hllpp1.format, hllpp2.format, any);
                    throw any;
                }
            }
            org.junit.Assert.fail("consistentBytes_literalMutation60 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of com.clearspring.analytics.stream.cardinality.TestHyperLogLogPlus#consistentBytes */
    @org.junit.Test(timeout = 1000)
    public void consistentBytes_literalMutation40_cf3964() throws java.lang.Throwable {
        int[] NUM_STRINGS = new int[]{ 30 , 50 , 100 , 200 , 300 , 500 , // TestDataMutator on numbers
        500 , 10000 , 100000 };
        // AssertGenerator add assertion
        int[] array_1828705072 = new int[]{30, 50, 100, 200, 300, 500, 500, 10000, 100000};
	int[] array_190729677 = (int[])NUM_STRINGS;
	for(int ii = 0; ii <array_1828705072.length; ii++) {
		org.junit.Assert.assertEquals(array_1828705072[ii], array_190729677[ii]);
	};
        // AssertGenerator add assertion
        int[] array_862654579 = new int[]{30, 50, 100, 200, 300, 500, 500, 10000, 100000};
	int[] array_1605888379 = (int[])NUM_STRINGS;
	for(int ii = 0; ii <array_862654579.length; ii++) {
		org.junit.Assert.assertEquals(array_862654579[ii], array_1605888379[ii]);
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
                    // StatementAdderOnAssert create null value
                    com.clearspring.analytics.stream.cardinality.HyperLogLog vc_996 = (com.clearspring.analytics.stream.cardinality.HyperLogLog)null;
                    // StatementAdderMethod cloned existing statement
                    vc_996.getBytes();
                    org.junit.Assert.assertNotEquals(hllpp1, hllpp2);
                }
            } catch (java.lang.Throwable any) {
                com.clearspring.analytics.stream.cardinality.TestHyperLogLogPlusAmpl.log.error("n={} format1={} format2={}", n, hllpp1.format, hllpp2.format, any);
                throw any;
            }
        }
    }

    /* amplification of com.clearspring.analytics.stream.cardinality.TestHyperLogLogPlus#consistentBytes */
    @org.junit.Test(timeout = 1000)
    public void consistentBytes_literalMutation80_failAssert26_cf8294() throws java.lang.Throwable {
        // AssertGenerator generate try/catch block with fail statement
        try {
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
                    // AssertGenerator replace invocation
                    boolean o_consistentBytes_literalMutation80_failAssert26_cf8294__23 = hllpp2.offer(strings[((n - 2) - i)]);
                    // AssertGenerator add assertion
                    org.junit.Assert.assertTrue(o_consistentBytes_literalMutation80_failAssert26_cf8294__23);
                }
                // calling these here ensures their internal state (format type) is stable for the rest of these checks.
                // (end users have no need for this because they cannot access the format directly anyway)
                hllpp1.mergeTempList();
                hllpp2.mergeTempList();
                com.clearspring.analytics.stream.cardinality.TestHyperLogLogPlusAmpl.log.debug("n={} format1={} format2={}", n, hllpp1.format, hllpp2.format);
                try {
                    if ((hllpp1.format) == (hllpp2.format)) {
                        // MethodAssertGenerator build local variable
                        Object o_33_1 = hllpp2.hashCode();
                        // MethodAssertGenerator build local variable
                        Object o_33_0 = hllpp1.hashCode();
                        org.junit.Assert.assertArrayEquals(hllpp1.getBytes(), hllpp2.getBytes());
                    }else {
                        // StatementAdderOnAssert create random local variable
                        double vc_2110 = 0.9557070432089243;
                        // StatementAdderOnAssert create random local variable
                        int vc_2109 = -1302937073;
                        // StatementAdderOnAssert create null value
                        com.clearspring.analytics.stream.cardinality.HyperLogLog vc_2107 = (com.clearspring.analytics.stream.cardinality.HyperLogLog)null;
                        // StatementAdderMethod cloned existing statement
                        vc_2107.linearCounting(vc_2109, vc_2110);
                        org.junit.Assert.assertNotEquals(hllpp1, hllpp2);
                    }
                } catch (java.lang.Throwable any) {
                    com.clearspring.analytics.stream.cardinality.TestHyperLogLogPlusAmpl.log.error("n={} format1={} format2={}", n, hllpp1.format, hllpp2.format, any);
                    throw any;
                }
            }
            org.junit.Assert.fail("consistentBytes_literalMutation80 should have thrown ArrayIndexOutOfBoundsException");
        } catch (java.lang.ArrayIndexOutOfBoundsException eee) {
        }
    }

    /* amplification of com.clearspring.analytics.stream.cardinality.TestHyperLogLogPlus#consistentBytes */
    @org.junit.Test
    public void consistentBytes_literalMutation32_literalMutation3015_failAssert50() throws java.lang.Throwable {
        // AssertGenerator generate try/catch block with fail statement
        try {
            int[] NUM_STRINGS = new int[]{ 30 , 50 , 100 , 200 , 300 , // TestDataMutator on numbers
            0 , 1000 , 10000 , 100000 };
            // AssertGenerator add assertion
            int[] array_2104432292 = new int[]{30, 50, 100, 200, 300, 0, 1000, 10000, 100000};
	int[] array_230887614 = (int[])NUM_STRINGS;
	for(int ii = 0; ii <array_2104432292.length; ii++) {
		org.junit.Assert.assertEquals(array_2104432292[ii], array_230887614[ii]);
	};
            for (int n : NUM_STRINGS) {
                java.lang.String[] strings = new java.lang.String[n];
                for (int i = 0; i < n; i++) {
                    strings[i] = org.apache.commons.lang3.RandomStringUtils.randomAlphabetic(20);
                }
                com.clearspring.analytics.stream.cardinality.HyperLogLogPlus hllpp1 = new com.clearspring.analytics.stream.cardinality.HyperLogLogPlus(0, 5);
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
                        Object o_36_1 = hllpp2.hashCode();
                        // MethodAssertGenerator build local variable
                        Object o_36_0 = hllpp1.hashCode();
                        org.junit.Assert.assertArrayEquals(hllpp1.getBytes(), hllpp2.getBytes());
                    }else {
                        org.junit.Assert.assertNotEquals(hllpp1, hllpp2);
                    }
                } catch (java.lang.Throwable any) {
                    com.clearspring.analytics.stream.cardinality.TestHyperLogLogPlusAmpl.log.error("n={} format1={} format2={}", n, hllpp1.format, hllpp2.format, any);
                    throw any;
                }
            }
            org.junit.Assert.fail("consistentBytes_literalMutation32_literalMutation3015 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of com.clearspring.analytics.stream.cardinality.TestHyperLogLogPlus#consistentBytes */
    @org.junit.Test(timeout = 1000)
    public void consistentBytes_literalMutation24_cf2154() throws java.lang.Throwable {
        int[] NUM_STRINGS = new int[]{ 30 , 50 , 100 , // TestDataMutator on numbers
        201 , 300 , 500 , 1000 , 10000 , 100000 };
        // AssertGenerator add assertion
        int[] array_286212607 = new int[]{30, 50, 100, 201, 300, 500, 1000, 10000, 100000};
	int[] array_1647646810 = (int[])NUM_STRINGS;
	for(int ii = 0; ii <array_286212607.length; ii++) {
		org.junit.Assert.assertEquals(array_286212607[ii], array_1647646810[ii]);
	};
        // AssertGenerator add assertion
        int[] array_1065467695 = new int[]{30, 50, 100, 201, 300, 500, 1000, 10000, 100000};
	int[] array_1001100634 = (int[])NUM_STRINGS;
	for(int ii = 0; ii <array_1065467695.length; ii++) {
		org.junit.Assert.assertEquals(array_1065467695[ii], array_1001100634[ii]);
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
                    // StatementAdderOnAssert create random local variable
                    double vc_544 = 0.11953630040096597;
                    // StatementAdderOnAssert create random local variable
                    int vc_543 = 1351945873;
                    // StatementAdderOnAssert create null value
                    com.clearspring.analytics.stream.cardinality.HyperLogLog vc_541 = (com.clearspring.analytics.stream.cardinality.HyperLogLog)null;
                    // StatementAdderMethod cloned existing statement
                    vc_541.linearCounting(vc_543, vc_544);
                    org.junit.Assert.assertNotEquals(hllpp1, hllpp2);
                }
            } catch (java.lang.Throwable any) {
                com.clearspring.analytics.stream.cardinality.TestHyperLogLogPlusAmpl.log.error("n={} format1={} format2={}", n, hllpp1.format, hllpp2.format, any);
                throw any;
            }
        }
    }

    /* amplification of com.clearspring.analytics.stream.cardinality.TestHyperLogLogPlus#consistentBytes */
    @org.junit.Test(timeout = 1000)
    public void consistentBytes_literalMutation80_failAssert26_cf8275() throws java.lang.Throwable {
        // AssertGenerator generate try/catch block with fail statement
        try {
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
                    // AssertGenerator replace invocation
                    boolean o_consistentBytes_literalMutation80_failAssert26_cf8275__23 = hllpp2.offer(strings[((n - 2) - i)]);
                    // AssertGenerator add assertion
                    org.junit.Assert.assertTrue(o_consistentBytes_literalMutation80_failAssert26_cf8275__23);
                }
                // calling these here ensures their internal state (format type) is stable for the rest of these checks.
                // (end users have no need for this because they cannot access the format directly anyway)
                hllpp1.mergeTempList();
                hllpp2.mergeTempList();
                com.clearspring.analytics.stream.cardinality.TestHyperLogLogPlusAmpl.log.debug("n={} format1={} format2={}", n, hllpp1.format, hllpp2.format);
                try {
                    if ((hllpp1.format) == (hllpp2.format)) {
                        // MethodAssertGenerator build local variable
                        Object o_33_1 = hllpp2.hashCode();
                        // MethodAssertGenerator build local variable
                        Object o_33_0 = hllpp1.hashCode();
                        org.junit.Assert.assertArrayEquals(hllpp1.getBytes(), hllpp2.getBytes());
                    }else {
                        // StatementAdderOnAssert create literal from method
                        int int_vc_216 = 20;
                        // StatementAdderOnAssert create null value
                        com.clearspring.analytics.stream.cardinality.HyperLogLog vc_2092 = (com.clearspring.analytics.stream.cardinality.HyperLogLog)null;
                        // StatementAdderMethod cloned existing statement
                        vc_2092.offerHashed(int_vc_216);
                        org.junit.Assert.assertNotEquals(hllpp1, hllpp2);
                    }
                } catch (java.lang.Throwable any) {
                    com.clearspring.analytics.stream.cardinality.TestHyperLogLogPlusAmpl.log.error("n={} format1={} format2={}", n, hllpp1.format, hllpp2.format, any);
                    throw any;
                }
            }
            org.junit.Assert.fail("consistentBytes_literalMutation80 should have thrown ArrayIndexOutOfBoundsException");
        } catch (java.lang.ArrayIndexOutOfBoundsException eee) {
        }
    }

    /* amplification of com.clearspring.analytics.stream.cardinality.TestHyperLogLogPlus#consistentBytes */
    @org.junit.Test
    public void consistentBytes_literalMutation70_failAssert17_literalMutation7230() throws java.lang.Throwable {
        // AssertGenerator generate try/catch block with fail statement
        try {
            int[] NUM_STRINGS = new int[]{ 30 , 50 , 100 , 200 , 300 , 500 , 1000 , 10000 , 100000 };
            for (int n : NUM_STRINGS) {
                java.lang.String[] strings = new java.lang.String[n];
                for (int i = 0; i < n; i++) {
                    strings[i] = org.apache.commons.lang3.RandomStringUtils.randomAlphabetic(20);
                }
                com.clearspring.analytics.stream.cardinality.HyperLogLogPlus hllpp1 = new com.clearspring.analytics.stream.cardinality.HyperLogLogPlus(5, 6);
                // AssertGenerator add assertion
                org.junit.Assert.assertEquals(((com.clearspring.analytics.stream.cardinality.HyperLogLogPlus)hllpp1).cardinality(), 0L);
                // AssertGenerator add assertion
                byte[] array_310944008 = new byte[]{-1, -1, -1, -2, 5, 6, 1, 0};
	byte[] array_998591688 = (byte[])((com.clearspring.analytics.stream.cardinality.HyperLogLogPlus)hllpp1).getBytes();
	for(int ii = 0; ii <array_310944008.length; ii++) {
		org.junit.Assert.assertEquals(array_310944008[ii], array_998591688[ii]);
	};
                // AssertGenerator add assertion
                org.junit.Assert.assertEquals(((com.clearspring.analytics.stream.cardinality.HyperLogLogPlus)hllpp1).sizeof(), 24);
                com.clearspring.analytics.stream.cardinality.HyperLogLogPlus hllpp2 = new com.clearspring.analytics.stream.cardinality.HyperLogLogPlus(6, 5);
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
                        Object o_33_1 = hllpp2.hashCode();
                        // MethodAssertGenerator build local variable
                        Object o_33_0 = hllpp1.hashCode();
                        org.junit.Assert.assertArrayEquals(hllpp1.getBytes(), hllpp2.getBytes());
                    }else {
                        org.junit.Assert.assertNotEquals(hllpp1, hllpp2);
                    }
                } catch (java.lang.Throwable any) {
                    com.clearspring.analytics.stream.cardinality.TestHyperLogLogPlusAmpl.log.error("n={} format1={} format2={}", n, hllpp1.format, hllpp2.format, any);
                    throw any;
                }
            }
            org.junit.Assert.fail("consistentBytes_literalMutation70 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of com.clearspring.analytics.stream.cardinality.TestHyperLogLogPlus#consistentBytes */
    @org.junit.Test(timeout = 1000)
    public void consistentBytes_literalMutation60_failAssert7_literalMutation6064_add25173() throws java.lang.Throwable {
        // AssertGenerator generate try/catch block with fail statement
        try {
            int[] NUM_STRINGS = new int[]{ 30 , 50 , 100 , 200 , 300 , // TestDataMutator on numbers
            250 , 1000 , 10000 , 100000 };
            // AssertGenerator add assertion
            int[] array_1913695781 = new int[]{30, 50, 100, 200, 300, 250, 1000, 10000, 100000};
	int[] array_1600773250 = (int[])NUM_STRINGS;
	for(int ii = 0; ii <array_1913695781.length; ii++) {
		org.junit.Assert.assertEquals(array_1913695781[ii], array_1600773250[ii]);
	};
            // AssertGenerator add assertion
            int[] array_926589977 = new int[]{30, 50, 100, 200, 300, 250, 1000, 10000, 100000};
	int[] array_459875258 = (int[])NUM_STRINGS;
	for(int ii = 0; ii <array_926589977.length; ii++) {
		org.junit.Assert.assertEquals(array_926589977[ii], array_459875258[ii]);
	};
            for (int n : NUM_STRINGS) {
                java.lang.String[] strings = new java.lang.String[n];
                for (int i = 0; i < n; i++) {
                    strings[i] = org.apache.commons.lang3.RandomStringUtils.randomAlphabetic(20);
                }
                com.clearspring.analytics.stream.cardinality.HyperLogLogPlus hllpp1 = new com.clearspring.analytics.stream.cardinality.HyperLogLogPlus(6, 5);
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
                        Object o_33_1 = hllpp2.hashCode();
                        // MethodAssertGenerator build local variable
                        Object o_33_0 = hllpp1.hashCode();
                        org.junit.Assert.assertArrayEquals(hllpp1.getBytes(), hllpp2.getBytes());
                    }else {
                        org.junit.Assert.assertNotEquals(hllpp1, hllpp2);
                    }
                } catch (java.lang.Throwable any) {
                    // MethodCallAdder
                    com.clearspring.analytics.stream.cardinality.TestHyperLogLogPlusAmpl.log.error("n={} format1={} format2={}", n, hllpp1.format, hllpp2.format, any);
                    com.clearspring.analytics.stream.cardinality.TestHyperLogLogPlusAmpl.log.error("n={} format1={} format2={}", n, hllpp1.format, hllpp2.format, any);
                    throw any;
                }
            }
            org.junit.Assert.fail("consistentBytes_literalMutation60 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of com.clearspring.analytics.stream.cardinality.TestHyperLogLogPlus#consistentBytes */
    @org.junit.Test
    public void consistentBytes_literalMutation70_failAssert17_literalMutation7192_literalMutation9842() throws java.lang.Throwable {
        // AssertGenerator generate try/catch block with fail statement
        try {
            int[] NUM_STRINGS = new int[]{ 30 , 50 , 100 , 200 , // TestDataMutator on numbers
            0 , 500 , 1000 , 10000 , 100000 };
            // AssertGenerator add assertion
            int[] array_1930583964 = new int[]{30, 50, 100, 200, 0, 500, 1000, 10000, 100000};
	int[] array_1623363335 = (int[])NUM_STRINGS;
	for(int ii = 0; ii <array_1930583964.length; ii++) {
		org.junit.Assert.assertEquals(array_1930583964[ii], array_1623363335[ii]);
	};
            // AssertGenerator add assertion
            int[] array_1127343543 = new int[]{30, 50, 100, 200, 0, 500, 1000, 10000, 100000};
	int[] array_929251844 = (int[])NUM_STRINGS;
	for(int ii = 0; ii <array_1127343543.length; ii++) {
		org.junit.Assert.assertEquals(array_1127343543[ii], array_929251844[ii]);
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
                byte[] array_485016011 = new byte[]{-1, -1, -1, -2, 5, 0, 0, 24, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
	byte[] array_2055625258 = (byte[])((com.clearspring.analytics.stream.cardinality.HyperLogLogPlus)hllpp1).getBytes();
	for(int ii = 0; ii <array_485016011.length; ii++) {
		org.junit.Assert.assertEquals(array_485016011[ii], array_2055625258[ii]);
	};
                // AssertGenerator add assertion
                org.junit.Assert.assertEquals(((com.clearspring.analytics.stream.cardinality.HyperLogLogPlus)hllpp1).sizeof(), 24);
                com.clearspring.analytics.stream.cardinality.HyperLogLogPlus hllpp2 = new com.clearspring.analytics.stream.cardinality.HyperLogLogPlus(6, 5);
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
                        Object o_33_1 = hllpp2.hashCode();
                        // MethodAssertGenerator build local variable
                        Object o_33_0 = hllpp1.hashCode();
                        org.junit.Assert.assertArrayEquals(hllpp1.getBytes(), hllpp2.getBytes());
                    }else {
                        org.junit.Assert.assertNotEquals(hllpp1, hllpp2);
                    }
                } catch (java.lang.Throwable any) {
                    com.clearspring.analytics.stream.cardinality.TestHyperLogLogPlusAmpl.log.error("n={} format1={} format2={}", n, hllpp1.format, hllpp2.format, any);
                    throw any;
                }
            }
            org.junit.Assert.fail("consistentBytes_literalMutation70 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of com.clearspring.analytics.stream.cardinality.TestHyperLogLogPlus#consistentBytes */
    @org.junit.Test(timeout = 1000)
    public void consistentBytes_literalMutation48_cf4888_cf20739() throws java.lang.Throwable {
        int[] NUM_STRINGS = new int[]{ 30 , 50 , 100 , 200 , 300 , 500 , 1000 , 10000 , // TestDataMutator on numbers
        100001 };
        // AssertGenerator add assertion
        int[] array_1158435298 = new int[]{30, 50, 100, 200, 300, 500, 1000, 10000, 100001};
	int[] array_1091475661 = (int[])NUM_STRINGS;
	for(int ii = 0; ii <array_1158435298.length; ii++) {
		org.junit.Assert.assertEquals(array_1158435298[ii], array_1091475661[ii]);
	};
        // AssertGenerator add assertion
        int[] array_1116296362 = new int[]{30, 50, 100, 200, 300, 500, 1000, 10000, 100001};
	int[] array_552996038 = (int[])NUM_STRINGS;
	for(int ii = 0; ii <array_1116296362.length; ii++) {
		org.junit.Assert.assertEquals(array_1116296362[ii], array_552996038[ii]);
	};
        // AssertGenerator add assertion
        int[] array_904943871 = new int[]{30, 50, 100, 200, 300, 500, 1000, 10000, 100001};
	int[] array_1726789294 = (int[])NUM_STRINGS;
	for(int ii = 0; ii <array_904943871.length; ii++) {
		org.junit.Assert.assertEquals(array_904943871[ii], array_1726789294[ii]);
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
                    // StatementAdderOnAssert create null value
                    com.clearspring.analytics.stream.cardinality.HyperLogLog vc_1241 = (com.clearspring.analytics.stream.cardinality.HyperLogLog)null;
                    // StatementAdderMethod cloned existing statement
                    vc_1241.sizeof();
                    // StatementAdderOnAssert create random local variable
                    java.lang.Object vc_5165 = new java.lang.Object();
                    // StatementAdderMethod cloned existing statement
                    vc_1241.offer(vc_5165);
                    org.junit.Assert.assertNotEquals(hllpp1, hllpp2);
                }
            } catch (java.lang.Throwable any) {
                com.clearspring.analytics.stream.cardinality.TestHyperLogLogPlusAmpl.log.error("n={} format1={} format2={}", n, hllpp1.format, hllpp2.format, any);
                throw any;
            }
        }
    }

    /* amplification of com.clearspring.analytics.stream.cardinality.TestHyperLogLogPlus#testComputeCount */
    @org.junit.Test
    public void testComputeCount_literalMutation25627_failAssert2() {
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
            org.junit.Assert.fail("testComputeCount_literalMutation25627 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of com.clearspring.analytics.stream.cardinality.TestHyperLogLogPlus#testComputeCount */
    @org.junit.Test
    public void testComputeCount_literalMutation25638() {
        com.clearspring.analytics.stream.cardinality.HyperLogLogPlus hyperLogLogPlus = new com.clearspring.analytics.stream.cardinality.HyperLogLogPlus(14, 25);
        int count = 140000;
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(count, 140000);
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

    /* amplification of com.clearspring.analytics.stream.cardinality.TestHyperLogLogPlus#testComputeCount */
    @org.junit.Test
    public void testComputeCount_literalMutation25632_failAssert4() {
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
            org.junit.Assert.fail("testComputeCount_literalMutation25632 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of com.clearspring.analytics.stream.cardinality.TestHyperLogLogPlus#testComputeCount */
    @org.junit.Test(timeout = 1000)
    public void testComputeCount_cf25686_failAssert10_literalMutation27281_failAssert8() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // AssertGenerator generate try/catch block with fail statement
            try {
                com.clearspring.analytics.stream.cardinality.HyperLogLogPlus hyperLogLogPlus = new com.clearspring.analytics.stream.cardinality.HyperLogLogPlus(0, 25);
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
                java.lang.Object vc_6325 = new java.lang.Object();
                // StatementAdderOnAssert create null value
                com.clearspring.analytics.stream.cardinality.HyperLogLog vc_6322 = (com.clearspring.analytics.stream.cardinality.HyperLogLog)null;
                // StatementAdderMethod cloned existing statement
                vc_6322.offer(vc_6325);
                // MethodAssertGenerator build local variable
                Object o_23_0 = estimate <= (expectedCardinality + (3 * se));
                org.junit.Assert.fail("testComputeCount_cf25686 should have thrown NullPointerException");
            } catch (java.lang.NullPointerException eee) {
            }
            org.junit.Assert.fail("testComputeCount_cf25686_failAssert10_literalMutation27281 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of com.clearspring.analytics.stream.cardinality.TestHyperLogLogPlus#testComputeCount */
    @org.junit.Test
    public void testComputeCount_literalMutation25638_literalMutation26851() {
        com.clearspring.analytics.stream.cardinality.HyperLogLogPlus hyperLogLogPlus = new com.clearspring.analytics.stream.cardinality.HyperLogLogPlus(14, 25);
        int count = 140000;
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(count, 140000);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(count, 140000);
        for (int i = 0; i < count; i++) {
            hyperLogLogPlus.offer(("i" + i));
        }
        long estimate = hyperLogLogPlus.cardinality();
        double se = count * (1.04 / (java.lang.Math.sqrt(java.lang.Math.pow(2, 14))));
        long expectedCardinality = count;
        java.lang.System.out.println(((((("Expect estimate: " + estimate) + " is between ") + (expectedCardinality - (3 * se))) + " and ") + (expectedCardinality + (3 * se))));
        org.junit.Assert.assertTrue((estimate >= (expectedCardinality - (3 * se))));
        org.junit.Assert.assertTrue((estimate <= (expectedCardinality + (1 * se))));
    }

    /* amplification of com.clearspring.analytics.stream.cardinality.TestHyperLogLogPlus#testComputeCount */
    @org.junit.Test(timeout = 1000)
    public void testComputeCount_cf25784_failAssert16_literalMutation27682_failAssert4() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // AssertGenerator generate try/catch block with fail statement
            try {
                com.clearspring.analytics.stream.cardinality.HyperLogLogPlus hyperLogLogPlus = new com.clearspring.analytics.stream.cardinality.HyperLogLogPlus(26, 25);
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
                com.clearspring.analytics.stream.cardinality.HyperLogLog vc_6345 = (com.clearspring.analytics.stream.cardinality.HyperLogLog)null;
                // StatementAdderMethod cloned existing statement
                vc_6345.sizeof();
                // MethodAssertGenerator build local variable
                Object o_21_0 = estimate <= (expectedCardinality + (3 * se));
                org.junit.Assert.fail("testComputeCount_cf25784 should have thrown NullPointerException");
            } catch (java.lang.NullPointerException eee) {
            }
            org.junit.Assert.fail("testComputeCount_cf25784_failAssert16_literalMutation27682 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of com.clearspring.analytics.stream.cardinality.TestHyperLogLogPlus#testComputeCount */
    @org.junit.Test(timeout = 1000)
    public void testComputeCount_cf25691_failAssert11_literalMutation27351_failAssert9() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // AssertGenerator generate try/catch block with fail statement
            try {
                com.clearspring.analytics.stream.cardinality.HyperLogLogPlus hyperLogLogPlus = new com.clearspring.analytics.stream.cardinality.HyperLogLogPlus(14, 70001);
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
                com.clearspring.analytics.stream.cardinality.HyperLogLog vc_6326 = (com.clearspring.analytics.stream.cardinality.HyperLogLog)null;
                // StatementAdderMethod cloned existing statement
                vc_6326.offerHashed(count);
                // MethodAssertGenerator build local variable
                Object o_21_0 = estimate <= (expectedCardinality + (3 * se));
                org.junit.Assert.fail("testComputeCount_cf25691 should have thrown NullPointerException");
            } catch (java.lang.NullPointerException eee) {
            }
            org.junit.Assert.fail("testComputeCount_cf25691_failAssert11_literalMutation27351 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }
}

