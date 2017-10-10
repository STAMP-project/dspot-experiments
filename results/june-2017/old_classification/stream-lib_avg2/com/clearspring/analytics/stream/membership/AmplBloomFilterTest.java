/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */


package com.clearspring.analytics.stream.membership;


public class AmplBloomFilterTest {
    public com.clearspring.analytics.stream.membership.BloomFilter bf;

    public com.clearspring.analytics.stream.membership.BloomFilter bf2;

    public com.clearspring.analytics.stream.membership.BloomCalculations.BloomSpecification spec = com.clearspring.analytics.stream.membership.BloomCalculations.computeBucketsAndK(1.0E-4);

    static final int ELEMENTS = 10000;

    public AmplBloomFilterTest() {
        bf = new com.clearspring.analytics.stream.membership.BloomFilter(com.clearspring.analytics.stream.membership.AmplBloomFilterTest.ELEMENTS, spec.bucketsPerElement);
        bf2 = new com.clearspring.analytics.stream.membership.BloomFilter(com.clearspring.analytics.stream.membership.AmplBloomFilterTest.ELEMENTS, spec.bucketsPerElement);
        org.junit.Assert.assertNotNull(bf);
    }

    @org.junit.Before
    public void clear() {
        bf.clear();
    }

    @org.junit.Test
    public void testMerge() {
        bf.add("a");
        bf2.add("c");
        com.clearspring.analytics.stream.membership.BloomFilter[] bfs = new com.clearspring.analytics.stream.membership.BloomFilter[1];
        bfs[0] = bf;
        com.clearspring.analytics.stream.membership.BloomFilter mergeBf = ((com.clearspring.analytics.stream.membership.BloomFilter) (bf2.merge(bf)));
        org.junit.Assert.assertTrue(mergeBf.isPresent("a"));
        org.junit.Assert.assertFalse(mergeBf.isPresent("b"));
        org.junit.Assert.assertTrue(mergeBf.isPresent("c"));
    }

    @org.junit.Test(expected = java.lang.IllegalArgumentException.class)
    public void testMergeException() {
        com.clearspring.analytics.stream.membership.BloomFilter bf3 = new com.clearspring.analytics.stream.membership.BloomFilter(((com.clearspring.analytics.stream.membership.AmplBloomFilterTest.ELEMENTS) * 10), 1);
        com.clearspring.analytics.stream.membership.BloomFilter[] bfs = new com.clearspring.analytics.stream.membership.BloomFilter[1];
        bfs[0] = bf;
        com.clearspring.analytics.stream.membership.BloomFilter mergeBf = ((com.clearspring.analytics.stream.membership.BloomFilter) (bf3.merge(bf)));
    }

    @org.junit.Test
    public void testFalsePositivesInt() {
        com.clearspring.analytics.stream.membership.FilterTest.testFalsePositives(bf, com.clearspring.analytics.stream.membership.FilterTest.intKeys(), com.clearspring.analytics.stream.membership.FilterTest.randomKeys2());
    }

    @org.junit.Test
    public void testFalsePositivesRandom() {
        com.clearspring.analytics.stream.membership.FilterTest.testFalsePositives(bf, com.clearspring.analytics.stream.membership.FilterTest.randomKeys(), com.clearspring.analytics.stream.membership.FilterTest.randomKeys2());
    }

    @org.junit.Test
    public void testWords() {
        if ((com.clearspring.analytics.stream.membership.KeyGenerator.WordGenerator.WORDS) == 0) {
            return ;
        }
        com.clearspring.analytics.stream.membership.BloomFilter bf2 = new com.clearspring.analytics.stream.membership.BloomFilter(((com.clearspring.analytics.stream.membership.KeyGenerator.WordGenerator.WORDS) / 2), com.clearspring.analytics.stream.membership.FilterTest.spec.bucketsPerElement);
        int skipEven = (((com.clearspring.analytics.stream.membership.KeyGenerator.WordGenerator.WORDS) % 2) == 0) ? 0 : 2;
        com.clearspring.analytics.stream.membership.FilterTest.testFalsePositives(bf2, new com.clearspring.analytics.stream.membership.KeyGenerator.WordGenerator(skipEven, 2), new com.clearspring.analytics.stream.membership.KeyGenerator.WordGenerator(1, 2));
    }

    @org.junit.Test
    public void testSerialize() throws java.io.IOException {
        com.clearspring.analytics.stream.membership.FilterTest.testSerialize(bf);
    }

    @org.junit.Test
    public void testGetFalsePositiveProbability() {
        // These probabilities are taken from the bloom filter probability table at
        // http://pages.cs.wisc.edu/~cao/papers/summary-cache/node8.html
        java.lang.System.out.println("expectedFalsePositiveProbability");
        com.clearspring.analytics.stream.membership.BloomFilter instance = new com.clearspring.analytics.stream.membership.BloomFilter(100, 10);
        double expResult = 0.00819;// m/n=10, k=7
        
        double result = com.clearspring.analytics.stream.membership.BloomCalculations.getFalsePositiveProbability(10, 7);
        org.junit.Assert.assertEquals(7, instance.getHashCount());
        org.junit.Assert.assertEquals(expResult, result, 9.0E-6);
        instance = new com.clearspring.analytics.stream.membership.BloomFilter(10, 10);
        expResult = 0.00819;// m/n=10, k=7
        
        result = com.clearspring.analytics.stream.membership.BloomCalculations.getFalsePositiveProbability(10, 7);
        org.junit.Assert.assertEquals(7, instance.getHashCount());
        org.junit.Assert.assertEquals(expResult, result, 9.0E-6);
        instance = new com.clearspring.analytics.stream.membership.BloomFilter(10, 2);
        expResult = 0.393;// m/n=2, k=1
        
        result = com.clearspring.analytics.stream.membership.BloomCalculations.getFalsePositiveProbability(2, 1);
        org.junit.Assert.assertEquals(instance.getHashCount(), 1);
        org.junit.Assert.assertEquals(expResult, result, 5.0E-4);
        instance = new com.clearspring.analytics.stream.membership.BloomFilter(10, 11);
        expResult = 0.00509;// m/n=11, k=8
        
        result = com.clearspring.analytics.stream.membership.BloomCalculations.getFalsePositiveProbability(11, 8);
        org.junit.Assert.assertEquals(8, instance.getHashCount());
        org.junit.Assert.assertEquals(expResult, result, 1.0E-5);
    }

    /**
     * Test error rate
     *
     * @throws UnsupportedEncodingException
     */
    @org.junit.Test
    public void testFalsePositiveRate() throws java.io.UnsupportedEncodingException {
        // Numbers are from // http://pages.cs.wisc.edu/~cao/papers/summary-cache/node8.html
        for (int j = 10; j < 21; j++) {
            java.lang.System.out.print(((j - 9) + "/11"));
            java.util.Set<java.lang.String> v = new java.util.HashSet<java.lang.String>();
            com.clearspring.analytics.stream.membership.BloomFilter instance = new com.clearspring.analytics.stream.membership.BloomFilter(100, j);
            for (int i = 0; i < 100; i++) {
                java.lang.String key = java.util.UUID.randomUUID().toString();
                v.add(key);
                instance.add(key);
            }
            long r = 0;
            double tests = 100000;
            for (int i = 0; i < tests; i++) {
                java.lang.String s = java.util.UUID.randomUUID().toString();
                if (instance.isPresent(s)) {
                    if (!(v.contains(s))) {
                        r++;
                    }
                }
            }
            double ratio = r / tests;
            double expectedFalsePositiveProbability = (j < (com.clearspring.analytics.stream.membership.BloomCalculations.probs.length)) ? com.clearspring.analytics.stream.membership.BloomCalculations.probs[j][instance.getHashCount()] : com.clearspring.analytics.stream.membership.BloomCalculations.getFalsePositiveProbability(j, instance.getHashCount());
            java.lang.System.out.println((((" - got " + ratio) + ", math says ") + expectedFalsePositiveProbability));
            org.junit.Assert.assertEquals(expectedFalsePositiveProbability, ratio, 0.01);
        }
    }

    /**
     * Test for correct k *
     */
    @org.junit.Test
    public void testHashCount() {
        // Numbers are from http://pages.cs.wisc.edu/~cao/papers/summary-cache/node8.html
        com.clearspring.analytics.stream.membership.BloomFilter instance = null;
        instance = new com.clearspring.analytics.stream.membership.BloomFilter(1, 2);
        org.junit.Assert.assertEquals(1, instance.getHashCount());
        instance = new com.clearspring.analytics.stream.membership.BloomFilter(1, 3);
        org.junit.Assert.assertEquals(2, instance.getHashCount());
        instance = new com.clearspring.analytics.stream.membership.BloomFilter(1, 4);
        org.junit.Assert.assertEquals(3, instance.getHashCount());
        instance = new com.clearspring.analytics.stream.membership.BloomFilter(1, 5);
        org.junit.Assert.assertEquals(3, instance.getHashCount());
        instance = new com.clearspring.analytics.stream.membership.BloomFilter(1, 6);
        org.junit.Assert.assertEquals(4, instance.getHashCount());
        instance = new com.clearspring.analytics.stream.membership.BloomFilter(1, 7);
        org.junit.Assert.assertEquals(5, instance.getHashCount());
        /* Although technically 8*ln(2) = 5.545...
        we round down here for speed
         */
        instance = new com.clearspring.analytics.stream.membership.BloomFilter(1, 8);
        org.junit.Assert.assertEquals(5, instance.getHashCount());
        instance = new com.clearspring.analytics.stream.membership.BloomFilter(1, 9);
        org.junit.Assert.assertEquals(6, instance.getHashCount());
        instance = new com.clearspring.analytics.stream.membership.BloomFilter(1, 10);
        org.junit.Assert.assertEquals(7, instance.getHashCount());
        instance = new com.clearspring.analytics.stream.membership.BloomFilter(1, 11);
        org.junit.Assert.assertEquals(8, instance.getHashCount());
        instance = new com.clearspring.analytics.stream.membership.BloomFilter(1, 12);
        org.junit.Assert.assertEquals(8, instance.getHashCount());
    }

    @org.junit.Ignore
    @org.junit.Test
    public void timeSerialize() throws java.io.IOException {
        for (int i = 0; i < 1000; i++) {
            com.clearspring.analytics.stream.membership.BloomFilter f = new com.clearspring.analytics.stream.membership.BloomFilter(1000000, 0.01);
            serialize(f);
        }
    }

    private byte[] serialize(com.clearspring.analytics.stream.membership.BloomFilter f) throws java.io.IOException {
        com.clearspring.analytics.stream.membership.DataOutputBuffer out = new com.clearspring.analytics.stream.membership.DataOutputBuffer();
        f.getSerializer().serialize(f, out);
        out.close();
        return out.getData();
    }

    /* TODO move these into a nightly suite (they take 5-10 minutes each) */
    // run with -mx1G
    @org.junit.Ignore
    @org.junit.Test
    public void testBigInt() {
        int size = (100 * 1000) * 1000;
        bf = new com.clearspring.analytics.stream.membership.BloomFilter(size, com.clearspring.analytics.stream.membership.FilterTest.spec.bucketsPerElement);
        com.clearspring.analytics.stream.membership.FilterTest.testFalsePositives(bf, new com.clearspring.analytics.stream.membership.KeyGenerator.IntGenerator(size), new com.clearspring.analytics.stream.membership.KeyGenerator.IntGenerator(size, (size * 2)));
    }

    @org.junit.Ignore
    @org.junit.Test
    public void testBigRandom() {
        int size = (100 * 1000) * 1000;
        bf = new com.clearspring.analytics.stream.membership.BloomFilter(size, com.clearspring.analytics.stream.membership.FilterTest.spec.bucketsPerElement);
        com.clearspring.analytics.stream.membership.FilterTest.testFalsePositives(bf, new com.clearspring.analytics.stream.membership.KeyGenerator.RandomStringGenerator(new java.util.Random().nextInt(), size), new com.clearspring.analytics.stream.membership.KeyGenerator.RandomStringGenerator(new java.util.Random().nextInt(), size));
    }

    @org.junit.Ignore
    @org.junit.Test
    public void timeit() {
        int size = 300 * (com.clearspring.analytics.stream.membership.FilterTest.ELEMENTS);
        bf = new com.clearspring.analytics.stream.membership.BloomFilter(size, com.clearspring.analytics.stream.membership.FilterTest.spec.bucketsPerElement);
        for (int i = 0; i < 10; i++) {
            com.clearspring.analytics.stream.membership.FilterTest.testFalsePositives(bf, new com.clearspring.analytics.stream.membership.KeyGenerator.RandomStringGenerator(new java.util.Random().nextInt(), size), new com.clearspring.analytics.stream.membership.KeyGenerator.RandomStringGenerator(new java.util.Random().nextInt(), size));
            bf.clear();
        }
    }

    @org.junit.Test
    public void testSizing() throws java.io.IOException {
        com.clearspring.analytics.stream.membership.BloomFilter f = null;
        org.junit.Assert.assertEquals(128, (f = new com.clearspring.analytics.stream.membership.BloomFilter(10, 0.05)).buckets());
        org.junit.Assert.assertEquals(93, serialize(f).length);
        org.junit.Assert.assertEquals(768, new com.clearspring.analytics.stream.membership.BloomFilter(100, 0.05).buckets());
        org.junit.Assert.assertEquals(7040, new com.clearspring.analytics.stream.membership.BloomFilter(1000, 0.05).buckets());
        org.junit.Assert.assertEquals(70080, new com.clearspring.analytics.stream.membership.BloomFilter(10000, 0.05).buckets());
        org.junit.Assert.assertEquals(700032, new com.clearspring.analytics.stream.membership.BloomFilter(100000, 0.05).buckets());
        org.junit.Assert.assertEquals(7000064, new com.clearspring.analytics.stream.membership.BloomFilter(1000000, 0.05).buckets());
        org.junit.Assert.assertEquals(128, new com.clearspring.analytics.stream.membership.BloomFilter(10, 0.01).buckets());
        org.junit.Assert.assertEquals(1024, new com.clearspring.analytics.stream.membership.BloomFilter(100, 0.01).buckets());
        org.junit.Assert.assertEquals(10048, (f = new com.clearspring.analytics.stream.membership.BloomFilter(1000, 0.01)).buckets());
        org.junit.Assert.assertEquals(1333, serialize(f).length);
        org.junit.Assert.assertEquals(100032, (f = new com.clearspring.analytics.stream.membership.BloomFilter(10000, 0.01)).buckets());
        org.junit.Assert.assertEquals(12581, serialize(f).length);
        org.junit.Assert.assertEquals(1000064, (f = new com.clearspring.analytics.stream.membership.BloomFilter(100000, 0.01)).buckets());
        org.junit.Assert.assertEquals(125085, serialize(f).length);
        org.junit.Assert.assertEquals(10000064, (f = new com.clearspring.analytics.stream.membership.BloomFilter(1000000, 0.01)).buckets());
        org.junit.Assert.assertEquals(1250085, serialize(f).length);
        for (java.lang.String s : new com.clearspring.analytics.stream.membership.KeyGenerator.RandomStringGenerator(new java.util.Random().nextInt(), 1000000)) {
            f.add(s);
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((com.clearspring.analytics.stream.membership.BloomFilter)f).getHashCount(), 5);
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((com.clearspring.analytics.stream.membership.BloomFilter)f).buckets(), 10000064);
        }
        org.junit.Assert.assertEquals(10000064, f.buckets());
        org.junit.Assert.assertEquals(1250085, serialize(f).length);
    }

    @org.junit.Test
    public void testOne() {
        bf.add("a");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((com.clearspring.analytics.stream.membership.BloomFilter)bf).buckets(), 150080);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((com.clearspring.analytics.stream.membership.BloomFilter)bf).getHashCount(), 10);
        org.junit.Assert.assertTrue(bf.isPresent("a"));
        org.junit.Assert.assertFalse(bf.isPresent("b"));
    }

    /* amplification of com.clearspring.analytics.stream.membership.BloomFilterTest#testGetFalsePositiveProbability */
    @org.junit.Test(timeout = 10000)
    public void testGetFalsePositiveProbability_cf7609() {
        // These probabilities are taken from the bloom filter probability table at
        // http://pages.cs.wisc.edu/~cao/papers/summary-cache/node8.html
        java.lang.System.out.println("expectedFalsePositiveProbability");
        com.clearspring.analytics.stream.membership.BloomFilter instance = new com.clearspring.analytics.stream.membership.BloomFilter(100, 10);
        double expResult = 0.00819;// m/n=10, k=7
        
        double result = com.clearspring.analytics.stream.membership.BloomCalculations.getFalsePositiveProbability(10, 7);
        org.junit.Assert.assertEquals(7, instance.getHashCount());
        org.junit.Assert.assertEquals(expResult, result, 9.0E-6);
        instance = new com.clearspring.analytics.stream.membership.BloomFilter(10, 10);
        expResult = 0.00819;// m/n=10, k=7
        
        result = com.clearspring.analytics.stream.membership.BloomCalculations.getFalsePositiveProbability(10, 7);
        org.junit.Assert.assertEquals(7, instance.getHashCount());
        org.junit.Assert.assertEquals(expResult, result, 9.0E-6);
        instance = new com.clearspring.analytics.stream.membership.BloomFilter(10, 2);
        expResult = 0.393;// m/n=2, k=1
        
        result = com.clearspring.analytics.stream.membership.BloomCalculations.getFalsePositiveProbability(2, 1);
        org.junit.Assert.assertEquals(instance.getHashCount(), 1);
        org.junit.Assert.assertEquals(expResult, result, 5.0E-4);
        instance = new com.clearspring.analytics.stream.membership.BloomFilter(10, 11);
        expResult = 0.00509;// m/n=11, k=8
        
        result = com.clearspring.analytics.stream.membership.BloomCalculations.getFalsePositiveProbability(11, 8);
        org.junit.Assert.assertEquals(8, instance.getHashCount());
        // StatementAdderOnAssert create null value
        com.clearspring.analytics.stream.membership.BloomFilter vc_1550 = (com.clearspring.analytics.stream.membership.BloomFilter)null;
        // AssertGenerator replace invocation
        com.clearspring.analytics.stream.membership.BloomFilter o_testGetFalsePositiveProbability_cf7609__41 = // StatementAdderMethod cloned existing statement
vc_1550.alwaysMatchingBloomFilter();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((com.clearspring.analytics.stream.membership.BloomFilter)o_testGetFalsePositiveProbability_cf7609__41).buckets(), 64);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((com.clearspring.analytics.stream.membership.BloomFilter)o_testGetFalsePositiveProbability_cf7609__41).getHashCount(), 1);
        org.junit.Assert.assertEquals(expResult, result, 1.0E-5);
    }

    /* amplification of com.clearspring.analytics.stream.membership.BloomFilterTest#testGetFalsePositiveProbability */
    @org.junit.Test(timeout = 10000)
    public void testGetFalsePositiveProbability_cf7625() {
        // These probabilities are taken from the bloom filter probability table at
        // http://pages.cs.wisc.edu/~cao/papers/summary-cache/node8.html
        java.lang.System.out.println("expectedFalsePositiveProbability");
        com.clearspring.analytics.stream.membership.BloomFilter instance = new com.clearspring.analytics.stream.membership.BloomFilter(100, 10);
        double expResult = 0.00819;// m/n=10, k=7
        
        double result = com.clearspring.analytics.stream.membership.BloomCalculations.getFalsePositiveProbability(10, 7);
        org.junit.Assert.assertEquals(7, instance.getHashCount());
        org.junit.Assert.assertEquals(expResult, result, 9.0E-6);
        instance = new com.clearspring.analytics.stream.membership.BloomFilter(10, 10);
        expResult = 0.00819;// m/n=10, k=7
        
        result = com.clearspring.analytics.stream.membership.BloomCalculations.getFalsePositiveProbability(10, 7);
        org.junit.Assert.assertEquals(7, instance.getHashCount());
        org.junit.Assert.assertEquals(expResult, result, 9.0E-6);
        instance = new com.clearspring.analytics.stream.membership.BloomFilter(10, 2);
        expResult = 0.393;// m/n=2, k=1
        
        result = com.clearspring.analytics.stream.membership.BloomCalculations.getFalsePositiveProbability(2, 1);
        org.junit.Assert.assertEquals(instance.getHashCount(), 1);
        org.junit.Assert.assertEquals(expResult, result, 5.0E-4);
        instance = new com.clearspring.analytics.stream.membership.BloomFilter(10, 11);
        expResult = 0.00509;// m/n=11, k=8
        
        result = com.clearspring.analytics.stream.membership.BloomCalculations.getFalsePositiveProbability(11, 8);
        org.junit.Assert.assertEquals(8, instance.getHashCount());
        // StatementAdderOnAssert create null value
        com.clearspring.analytics.stream.membership.Filter[] vc_1558 = (com.clearspring.analytics.stream.membership.Filter[])null;
        // StatementAddOnAssert local variable replacement
        com.clearspring.analytics.stream.membership.BloomFilter mergeBf = ((com.clearspring.analytics.stream.membership.BloomFilter) (bf2.merge(bf)));
        // AssertGenerator replace invocation
        com.clearspring.analytics.stream.membership.Filter o_testGetFalsePositiveProbability_cf7625__44 = // StatementAdderMethod cloned existing statement
mergeBf.merge(vc_1558);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((com.clearspring.analytics.stream.membership.BloomFilter)o_testGetFalsePositiveProbability_cf7625__44).getHashCount(), 10);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((com.clearspring.analytics.stream.membership.BloomFilter)o_testGetFalsePositiveProbability_cf7625__44).buckets(), 150080);
        org.junit.Assert.assertEquals(expResult, result, 1.0E-5);
    }

    /* amplification of com.clearspring.analytics.stream.membership.BloomFilterTest#testGetFalsePositiveProbability */
    @org.junit.Test(timeout = 10000)
    public void testGetFalsePositiveProbability_cf7590() {
        // These probabilities are taken from the bloom filter probability table at
        // http://pages.cs.wisc.edu/~cao/papers/summary-cache/node8.html
        java.lang.System.out.println("expectedFalsePositiveProbability");
        com.clearspring.analytics.stream.membership.BloomFilter instance = new com.clearspring.analytics.stream.membership.BloomFilter(100, 10);
        double expResult = 0.00819;// m/n=10, k=7
        
        double result = com.clearspring.analytics.stream.membership.BloomCalculations.getFalsePositiveProbability(10, 7);
        org.junit.Assert.assertEquals(7, instance.getHashCount());
        org.junit.Assert.assertEquals(expResult, result, 9.0E-6);
        instance = new com.clearspring.analytics.stream.membership.BloomFilter(10, 10);
        expResult = 0.00819;// m/n=10, k=7
        
        result = com.clearspring.analytics.stream.membership.BloomCalculations.getFalsePositiveProbability(10, 7);
        org.junit.Assert.assertEquals(7, instance.getHashCount());
        org.junit.Assert.assertEquals(expResult, result, 9.0E-6);
        instance = new com.clearspring.analytics.stream.membership.BloomFilter(10, 2);
        expResult = 0.393;// m/n=2, k=1
        
        result = com.clearspring.analytics.stream.membership.BloomCalculations.getFalsePositiveProbability(2, 1);
        org.junit.Assert.assertEquals(instance.getHashCount(), 1);
        org.junit.Assert.assertEquals(expResult, result, 5.0E-4);
        instance = new com.clearspring.analytics.stream.membership.BloomFilter(10, 11);
        expResult = 0.00509;// m/n=11, k=8
        
        result = com.clearspring.analytics.stream.membership.BloomCalculations.getFalsePositiveProbability(11, 8);
        org.junit.Assert.assertEquals(8, instance.getHashCount());
        // StatementAdderOnAssert create random local variable
        byte[] vc_1543 = new byte []{109,86,75};
        // StatementAddOnAssert local variable replacement
        com.clearspring.analytics.stream.membership.BloomFilter mergeBf = ((com.clearspring.analytics.stream.membership.BloomFilter) (bf2.merge(bf)));
        // AssertGenerator replace invocation
        boolean o_testGetFalsePositiveProbability_cf7590__44 = // StatementAdderMethod cloned existing statement
mergeBf.isPresent(vc_1543);
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_testGetFalsePositiveProbability_cf7590__44);
        org.junit.Assert.assertEquals(expResult, result, 1.0E-5);
    }

    /* amplification of com.clearspring.analytics.stream.membership.BloomFilterTest#testGetFalsePositiveProbability */
    @org.junit.Test(timeout = 10000)
    public void testGetFalsePositiveProbability_cf7646() {
        // These probabilities are taken from the bloom filter probability table at
        // http://pages.cs.wisc.edu/~cao/papers/summary-cache/node8.html
        java.lang.System.out.println("expectedFalsePositiveProbability");
        com.clearspring.analytics.stream.membership.BloomFilter instance = new com.clearspring.analytics.stream.membership.BloomFilter(100, 10);
        double expResult = 0.00819;// m/n=10, k=7
        
        double result = com.clearspring.analytics.stream.membership.BloomCalculations.getFalsePositiveProbability(10, 7);
        org.junit.Assert.assertEquals(7, instance.getHashCount());
        org.junit.Assert.assertEquals(expResult, result, 9.0E-6);
        instance = new com.clearspring.analytics.stream.membership.BloomFilter(10, 10);
        expResult = 0.00819;// m/n=10, k=7
        
        result = com.clearspring.analytics.stream.membership.BloomCalculations.getFalsePositiveProbability(10, 7);
        org.junit.Assert.assertEquals(7, instance.getHashCount());
        org.junit.Assert.assertEquals(expResult, result, 9.0E-6);
        instance = new com.clearspring.analytics.stream.membership.BloomFilter(10, 2);
        expResult = 0.393;// m/n=2, k=1
        
        result = com.clearspring.analytics.stream.membership.BloomCalculations.getFalsePositiveProbability(2, 1);
        org.junit.Assert.assertEquals(instance.getHashCount(), 1);
        org.junit.Assert.assertEquals(expResult, result, 5.0E-4);
        instance = new com.clearspring.analytics.stream.membership.BloomFilter(10, 11);
        expResult = 0.00509;// m/n=11, k=8
        
        result = com.clearspring.analytics.stream.membership.BloomCalculations.getFalsePositiveProbability(11, 8);
        org.junit.Assert.assertEquals(8, instance.getHashCount());
        // AssertGenerator replace invocation
        java.lang.String o_testGetFalsePositiveProbability_cf7646__39 = // StatementAdderMethod cloned existing statement
instance.toString();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(o_testGetFalsePositiveProbability_cf7646__39, "{}");
        org.junit.Assert.assertEquals(expResult, result, 1.0E-5);
    }

    /* amplification of com.clearspring.analytics.stream.membership.BloomFilterTest#testGetFalsePositiveProbability */
    @org.junit.Test(timeout = 10000)
    public void testGetFalsePositiveProbability_cf7656() {
        // These probabilities are taken from the bloom filter probability table at
        // http://pages.cs.wisc.edu/~cao/papers/summary-cache/node8.html
        java.lang.System.out.println("expectedFalsePositiveProbability");
        com.clearspring.analytics.stream.membership.BloomFilter instance = new com.clearspring.analytics.stream.membership.BloomFilter(100, 10);
        double expResult = 0.00819;// m/n=10, k=7
        
        double result = com.clearspring.analytics.stream.membership.BloomCalculations.getFalsePositiveProbability(10, 7);
        org.junit.Assert.assertEquals(7, instance.getHashCount());
        org.junit.Assert.assertEquals(expResult, result, 9.0E-6);
        instance = new com.clearspring.analytics.stream.membership.BloomFilter(10, 10);
        expResult = 0.00819;// m/n=10, k=7
        
        result = com.clearspring.analytics.stream.membership.BloomCalculations.getFalsePositiveProbability(10, 7);
        org.junit.Assert.assertEquals(7, instance.getHashCount());
        org.junit.Assert.assertEquals(expResult, result, 9.0E-6);
        instance = new com.clearspring.analytics.stream.membership.BloomFilter(10, 2);
        expResult = 0.393;// m/n=2, k=1
        
        result = com.clearspring.analytics.stream.membership.BloomCalculations.getFalsePositiveProbability(2, 1);
        org.junit.Assert.assertEquals(instance.getHashCount(), 1);
        org.junit.Assert.assertEquals(expResult, result, 5.0E-4);
        instance = new com.clearspring.analytics.stream.membership.BloomFilter(10, 11);
        expResult = 0.00509;// m/n=11, k=8
        
        result = com.clearspring.analytics.stream.membership.BloomCalculations.getFalsePositiveProbability(11, 8);
        org.junit.Assert.assertEquals(8, instance.getHashCount());
        // StatementAdderOnAssert create random local variable
        byte[] vc_1575 = new byte []{};
        // StatementAdderMethod cloned existing statement
        instance.add(vc_1575);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((com.clearspring.analytics.stream.membership.BloomFilter)instance).getHashCount(), 8);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((com.clearspring.analytics.stream.membership.BloomFilter)instance).buckets(), 192);
        org.junit.Assert.assertEquals(expResult, result, 1.0E-5);
    }

    /* amplification of com.clearspring.analytics.stream.membership.BloomFilterTest#testGetFalsePositiveProbability */
    @org.junit.Test(timeout = 10000)
    public void testGetFalsePositiveProbability_cf7623() {
        // These probabilities are taken from the bloom filter probability table at
        // http://pages.cs.wisc.edu/~cao/papers/summary-cache/node8.html
        java.lang.System.out.println("expectedFalsePositiveProbability");
        com.clearspring.analytics.stream.membership.BloomFilter instance = new com.clearspring.analytics.stream.membership.BloomFilter(100, 10);
        double expResult = 0.00819;// m/n=10, k=7
        
        double result = com.clearspring.analytics.stream.membership.BloomCalculations.getFalsePositiveProbability(10, 7);
        org.junit.Assert.assertEquals(7, instance.getHashCount());
        org.junit.Assert.assertEquals(expResult, result, 9.0E-6);
        instance = new com.clearspring.analytics.stream.membership.BloomFilter(10, 10);
        expResult = 0.00819;// m/n=10, k=7
        
        result = com.clearspring.analytics.stream.membership.BloomCalculations.getFalsePositiveProbability(10, 7);
        org.junit.Assert.assertEquals(7, instance.getHashCount());
        org.junit.Assert.assertEquals(expResult, result, 9.0E-6);
        instance = new com.clearspring.analytics.stream.membership.BloomFilter(10, 2);
        expResult = 0.393;// m/n=2, k=1
        
        result = com.clearspring.analytics.stream.membership.BloomCalculations.getFalsePositiveProbability(2, 1);
        org.junit.Assert.assertEquals(instance.getHashCount(), 1);
        org.junit.Assert.assertEquals(expResult, result, 5.0E-4);
        instance = new com.clearspring.analytics.stream.membership.BloomFilter(10, 11);
        expResult = 0.00509;// m/n=11, k=8
        
        result = com.clearspring.analytics.stream.membership.BloomCalculations.getFalsePositiveProbability(11, 8);
        org.junit.Assert.assertEquals(8, instance.getHashCount());
        // StatementAdderOnAssert create null value
        com.clearspring.analytics.stream.membership.Filter[] vc_1558 = (com.clearspring.analytics.stream.membership.Filter[])null;
        // AssertGenerator replace invocation
        com.clearspring.analytics.stream.membership.Filter o_testGetFalsePositiveProbability_cf7623__41 = // StatementAdderMethod cloned existing statement
instance.merge(vc_1558);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((com.clearspring.analytics.stream.membership.BloomFilter)o_testGetFalsePositiveProbability_cf7623__41).buckets(), 192);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((com.clearspring.analytics.stream.membership.BloomFilter)o_testGetFalsePositiveProbability_cf7623__41).getHashCount(), 8);
        org.junit.Assert.assertEquals(expResult, result, 1.0E-5);
    }

    /* amplification of com.clearspring.analytics.stream.membership.BloomFilterTest#testGetFalsePositiveProbability */
    @org.junit.Test(timeout = 10000)
    public void testGetFalsePositiveProbability_cf7642() {
        // These probabilities are taken from the bloom filter probability table at
        // http://pages.cs.wisc.edu/~cao/papers/summary-cache/node8.html
        java.lang.System.out.println("expectedFalsePositiveProbability");
        com.clearspring.analytics.stream.membership.BloomFilter instance = new com.clearspring.analytics.stream.membership.BloomFilter(100, 10);
        double expResult = 0.00819;// m/n=10, k=7
        
        double result = com.clearspring.analytics.stream.membership.BloomCalculations.getFalsePositiveProbability(10, 7);
        org.junit.Assert.assertEquals(7, instance.getHashCount());
        org.junit.Assert.assertEquals(expResult, result, 9.0E-6);
        instance = new com.clearspring.analytics.stream.membership.BloomFilter(10, 10);
        expResult = 0.00819;// m/n=10, k=7
        
        result = com.clearspring.analytics.stream.membership.BloomCalculations.getFalsePositiveProbability(10, 7);
        org.junit.Assert.assertEquals(7, instance.getHashCount());
        org.junit.Assert.assertEquals(expResult, result, 9.0E-6);
        instance = new com.clearspring.analytics.stream.membership.BloomFilter(10, 2);
        expResult = 0.393;// m/n=2, k=1
        
        result = com.clearspring.analytics.stream.membership.BloomCalculations.getFalsePositiveProbability(2, 1);
        org.junit.Assert.assertEquals(instance.getHashCount(), 1);
        org.junit.Assert.assertEquals(expResult, result, 5.0E-4);
        instance = new com.clearspring.analytics.stream.membership.BloomFilter(10, 11);
        expResult = 0.00509;// m/n=11, k=8
        
        result = com.clearspring.analytics.stream.membership.BloomCalculations.getFalsePositiveProbability(11, 8);
        org.junit.Assert.assertEquals(8, instance.getHashCount());
        // AssertGenerator replace invocation
        int o_testGetFalsePositiveProbability_cf7642__39 = // StatementAdderMethod cloned existing statement
instance.emptyBuckets();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(o_testGetFalsePositiveProbability_cf7642__39, 192);
        org.junit.Assert.assertEquals(expResult, result, 1.0E-5);
    }

    /* amplification of com.clearspring.analytics.stream.membership.BloomFilterTest#testGetFalsePositiveProbability */
    /* amplification of com.clearspring.analytics.stream.membership.BloomFilterTest#testGetFalsePositiveProbability_cf7637 */
    @org.junit.Test(timeout = 10000)
    public void testGetFalsePositiveProbability_cf7637_failAssert73_literalMutation12458() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // These probabilities are taken from the bloom filter probability table at
            // http://pages.cs.wisc.edu/~cao/papers/summary-cache/node8.html
            java.lang.System.out.println("expectedFalsePositiveProbability");
            com.clearspring.analytics.stream.membership.BloomFilter instance = new com.clearspring.analytics.stream.membership.BloomFilter(100, 10);
            double expResult = 0.00819;// m/n=10, k=7
            
            double result = com.clearspring.analytics.stream.membership.BloomCalculations.getFalsePositiveProbability(10, 7);
            // MethodAssertGenerator build local variable
            Object o_10_0 = instance.getHashCount();
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(o_10_0, 7);
            instance = new com.clearspring.analytics.stream.membership.BloomFilter(10, 10);
            expResult = 0.00819;// m/n=10, k=7
            
            result = com.clearspring.analytics.stream.membership.BloomCalculations.getFalsePositiveProbability(10, 7);
            // MethodAssertGenerator build local variable
            Object o_19_0 = instance.getHashCount();
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(o_19_0, 7);
            instance = new com.clearspring.analytics.stream.membership.BloomFilter(10, 2);
            expResult = 0.393;// m/n=2, k=1
            
            result = com.clearspring.analytics.stream.membership.BloomCalculations.getFalsePositiveProbability(2, 1);
            // MethodAssertGenerator build local variable
            Object o_28_0 = instance.getHashCount();
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(o_28_0, 1);
            instance = new com.clearspring.analytics.stream.membership.BloomFilter(10, 22);
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((com.clearspring.analytics.stream.membership.BloomFilter)instance).getHashCount(), 14);
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((com.clearspring.analytics.stream.membership.BloomFilter)instance).buckets(), 256);
            expResult = 0.00509;// m/n=11, k=8
            
            result = com.clearspring.analytics.stream.membership.BloomCalculations.getFalsePositiveProbability(11, 8);
            // MethodAssertGenerator build local variable
            Object o_37_0 = instance.getHashCount();
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(o_37_0, 14);
            // StatementAdderOnAssert create null value
            com.clearspring.analytics.stream.membership.BloomFilter vc_1564 = (com.clearspring.analytics.stream.membership.BloomFilter)null;
            // StatementAdderMethod cloned existing statement
            vc_1564.buckets();
            org.junit.Assert.fail("testGetFalsePositiveProbability_cf7637 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    /* amplification of com.clearspring.analytics.stream.membership.BloomFilterTest#testGetFalsePositiveProbability */
    /* amplification of com.clearspring.analytics.stream.membership.BloomFilterTest#testGetFalsePositiveProbability_cf7643 */
    @org.junit.Test(timeout = 10000)
    public void testGetFalsePositiveProbability_cf7643_cf10382_failAssert0() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // MethodAssertGenerator build local variable
            Object o_44_1 = 150080;
            // These probabilities are taken from the bloom filter probability table at
            // http://pages.cs.wisc.edu/~cao/papers/summary-cache/node8.html
            java.lang.System.out.println("expectedFalsePositiveProbability");
            com.clearspring.analytics.stream.membership.BloomFilter instance = new com.clearspring.analytics.stream.membership.BloomFilter(100, 10);
            double expResult = 0.00819;// m/n=10, k=7
            
            double result = com.clearspring.analytics.stream.membership.BloomCalculations.getFalsePositiveProbability(10, 7);
            // MethodAssertGenerator build local variable
            Object o_10_0 = instance.getHashCount();
            instance = new com.clearspring.analytics.stream.membership.BloomFilter(10, 10);
            expResult = 0.00819;// m/n=10, k=7
            
            result = com.clearspring.analytics.stream.membership.BloomCalculations.getFalsePositiveProbability(10, 7);
            // MethodAssertGenerator build local variable
            Object o_19_0 = instance.getHashCount();
            instance = new com.clearspring.analytics.stream.membership.BloomFilter(10, 2);
            expResult = 0.393;// m/n=2, k=1
            
            result = com.clearspring.analytics.stream.membership.BloomCalculations.getFalsePositiveProbability(2, 1);
            // MethodAssertGenerator build local variable
            Object o_28_0 = instance.getHashCount();
            instance = new com.clearspring.analytics.stream.membership.BloomFilter(10, 11);
            expResult = 0.00509;// m/n=11, k=8
            
            result = com.clearspring.analytics.stream.membership.BloomCalculations.getFalsePositiveProbability(11, 8);
            // MethodAssertGenerator build local variable
            Object o_37_0 = instance.getHashCount();
            // StatementAddOnAssert local variable replacement
            com.clearspring.analytics.stream.membership.BloomFilter mergeBf = ((com.clearspring.analytics.stream.membership.BloomFilter) (bf2.merge(bf)));
            // AssertGenerator replace invocation
            int o_testGetFalsePositiveProbability_cf7643__42 = // StatementAdderMethod cloned existing statement
mergeBf.emptyBuckets();
            // MethodAssertGenerator build local variable
            Object o_44_0 = o_testGetFalsePositiveProbability_cf7643__42;
            // StatementAdderOnAssert create null value
            java.lang.String vc_2194 = (java.lang.String)null;
            // StatementAdderOnAssert create null value
            com.clearspring.analytics.stream.membership.BloomFilter vc_2192 = (com.clearspring.analytics.stream.membership.BloomFilter)null;
            // StatementAdderMethod cloned existing statement
            vc_2192.add(vc_2194);
            org.junit.Assert.fail("testGetFalsePositiveProbability_cf7643_cf10382 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    /* amplification of com.clearspring.analytics.stream.membership.BloomFilterTest#testGetFalsePositiveProbability */
    /* amplification of com.clearspring.analytics.stream.membership.BloomFilterTest#testGetFalsePositiveProbability_cf7643 */
    @org.junit.Test(timeout = 10000)
    public void testGetFalsePositiveProbability_cf7643_cf10382_failAssert0_literalMutation19342() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // MethodAssertGenerator build local variable
            Object o_44_1 = 150080;
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(o_44_1, 150080);
            // These probabilities are taken from the bloom filter probability table at
            // http://pages.cs.wisc.edu/~cao/papers/summary-cache/node8.html
            java.lang.System.out.println("expectedFalsePositiveProbability");
            com.clearspring.analytics.stream.membership.BloomFilter instance = new com.clearspring.analytics.stream.membership.BloomFilter(100, 10);
            double expResult = 0.00819;// m/n=10, k=7
            
            double result = com.clearspring.analytics.stream.membership.BloomCalculations.getFalsePositiveProbability(10, 7);
            // MethodAssertGenerator build local variable
            Object o_10_0 = instance.getHashCount();
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(o_10_0, 7);
            instance = new com.clearspring.analytics.stream.membership.BloomFilter(10, 10);
            expResult = 0.00819;// m/n=10, k=7
            
            result = com.clearspring.analytics.stream.membership.BloomCalculations.getFalsePositiveProbability(10, 7);
            // MethodAssertGenerator build local variable
            Object o_19_0 = instance.getHashCount();
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(o_19_0, 7);
            instance = new com.clearspring.analytics.stream.membership.BloomFilter(10, 2);
            expResult = 0.393;// m/n=2, k=1
            
            result = com.clearspring.analytics.stream.membership.BloomCalculations.getFalsePositiveProbability(2, 1);
            // MethodAssertGenerator build local variable
            Object o_28_0 = instance.getHashCount();
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(o_28_0, 1);
            instance = new com.clearspring.analytics.stream.membership.BloomFilter(10, 10);
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((com.clearspring.analytics.stream.membership.BloomFilter)instance).getHashCount(), 7);
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((com.clearspring.analytics.stream.membership.BloomFilter)instance).buckets(), 128);
            expResult = 0.00509;// m/n=11, k=8
            
            result = com.clearspring.analytics.stream.membership.BloomCalculations.getFalsePositiveProbability(11, 8);
            // MethodAssertGenerator build local variable
            Object o_37_0 = instance.getHashCount();
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(o_37_0, 7);
            // StatementAddOnAssert local variable replacement
            com.clearspring.analytics.stream.membership.BloomFilter mergeBf = ((com.clearspring.analytics.stream.membership.BloomFilter) (bf2.merge(bf)));
            // AssertGenerator replace invocation
            int o_testGetFalsePositiveProbability_cf7643__42 = // StatementAdderMethod cloned existing statement
mergeBf.emptyBuckets();
            // MethodAssertGenerator build local variable
            Object o_44_0 = o_testGetFalsePositiveProbability_cf7643__42;
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(o_44_0, 150080);
            // StatementAdderOnAssert create null value
            java.lang.String vc_2194 = (java.lang.String)null;
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(vc_2194);
            // StatementAdderOnAssert create null value
            com.clearspring.analytics.stream.membership.BloomFilter vc_2192 = (com.clearspring.analytics.stream.membership.BloomFilter)null;
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(vc_2192);
            // StatementAdderMethod cloned existing statement
            vc_2192.add(vc_2194);
            org.junit.Assert.fail("testGetFalsePositiveProbability_cf7643_cf10382 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    /**
     * Test for correct k *
     */
    /* amplification of com.clearspring.analytics.stream.membership.BloomFilterTest#testHashCount */
    @org.junit.Test(timeout = 10000)
    public void testHashCount_cf20613() {
        // Numbers are from http://pages.cs.wisc.edu/~cao/papers/summary-cache/node8.html
        com.clearspring.analytics.stream.membership.BloomFilter instance = null;
        instance = new com.clearspring.analytics.stream.membership.BloomFilter(1, 2);
        org.junit.Assert.assertEquals(1, instance.getHashCount());
        instance = new com.clearspring.analytics.stream.membership.BloomFilter(1, 3);
        org.junit.Assert.assertEquals(2, instance.getHashCount());
        instance = new com.clearspring.analytics.stream.membership.BloomFilter(1, 4);
        org.junit.Assert.assertEquals(3, instance.getHashCount());
        instance = new com.clearspring.analytics.stream.membership.BloomFilter(1, 5);
        org.junit.Assert.assertEquals(3, instance.getHashCount());
        instance = new com.clearspring.analytics.stream.membership.BloomFilter(1, 6);
        org.junit.Assert.assertEquals(4, instance.getHashCount());
        instance = new com.clearspring.analytics.stream.membership.BloomFilter(1, 7);
        org.junit.Assert.assertEquals(5, instance.getHashCount());
        /* Although technically 8*ln(2) = 5.545...
        we round down here for speed
         */
        instance = new com.clearspring.analytics.stream.membership.BloomFilter(1, 8);
        org.junit.Assert.assertEquals(5, instance.getHashCount());
        instance = new com.clearspring.analytics.stream.membership.BloomFilter(1, 9);
        org.junit.Assert.assertEquals(6, instance.getHashCount());
        instance = new com.clearspring.analytics.stream.membership.BloomFilter(1, 10);
        org.junit.Assert.assertEquals(7, instance.getHashCount());
        instance = new com.clearspring.analytics.stream.membership.BloomFilter(1, 11);
        org.junit.Assert.assertEquals(8, instance.getHashCount());
        instance = new com.clearspring.analytics.stream.membership.BloomFilter(1, 12);
        // AssertGenerator replace invocation
        int o_testHashCount_cf20613__46 = // StatementAdderMethod cloned existing statement
instance.emptyBuckets();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(o_testHashCount_cf20613__46, 64);
        org.junit.Assert.assertEquals(8, instance.getHashCount());
    }

    /**
     * Test for correct k *
     */
    /* amplification of com.clearspring.analytics.stream.membership.BloomFilterTest#testHashCount */
    @org.junit.Test(timeout = 10000)
    public void testHashCount_cf20627() {
        // Numbers are from http://pages.cs.wisc.edu/~cao/papers/summary-cache/node8.html
        com.clearspring.analytics.stream.membership.BloomFilter instance = null;
        instance = new com.clearspring.analytics.stream.membership.BloomFilter(1, 2);
        org.junit.Assert.assertEquals(1, instance.getHashCount());
        instance = new com.clearspring.analytics.stream.membership.BloomFilter(1, 3);
        org.junit.Assert.assertEquals(2, instance.getHashCount());
        instance = new com.clearspring.analytics.stream.membership.BloomFilter(1, 4);
        org.junit.Assert.assertEquals(3, instance.getHashCount());
        instance = new com.clearspring.analytics.stream.membership.BloomFilter(1, 5);
        org.junit.Assert.assertEquals(3, instance.getHashCount());
        instance = new com.clearspring.analytics.stream.membership.BloomFilter(1, 6);
        org.junit.Assert.assertEquals(4, instance.getHashCount());
        instance = new com.clearspring.analytics.stream.membership.BloomFilter(1, 7);
        org.junit.Assert.assertEquals(5, instance.getHashCount());
        /* Although technically 8*ln(2) = 5.545...
        we round down here for speed
         */
        instance = new com.clearspring.analytics.stream.membership.BloomFilter(1, 8);
        org.junit.Assert.assertEquals(5, instance.getHashCount());
        instance = new com.clearspring.analytics.stream.membership.BloomFilter(1, 9);
        org.junit.Assert.assertEquals(6, instance.getHashCount());
        instance = new com.clearspring.analytics.stream.membership.BloomFilter(1, 10);
        org.junit.Assert.assertEquals(7, instance.getHashCount());
        instance = new com.clearspring.analytics.stream.membership.BloomFilter(1, 11);
        org.junit.Assert.assertEquals(8, instance.getHashCount());
        instance = new com.clearspring.analytics.stream.membership.BloomFilter(1, 12);
        // StatementAdderOnAssert create random local variable
        byte[] vc_4215 = new byte []{85,38};
        // AssertGenerator add assertion
        byte[] array_624858880 = new byte[]{85, 38};
	byte[] array_1121919712 = (byte[])vc_4215;
	for(int ii = 0; ii <array_624858880.length; ii++) {
		org.junit.Assert.assertEquals(array_624858880[ii], array_1121919712[ii]);
	};
        // StatementAdderMethod cloned existing statement
        instance.add(vc_4215);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((com.clearspring.analytics.stream.membership.BloomFilter)instance).getHashCount(), 8);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((com.clearspring.analytics.stream.membership.BloomFilter)instance).buckets(), 64);
        org.junit.Assert.assertEquals(8, instance.getHashCount());
    }

    /**
     * Test for correct k *
     */
    /* amplification of com.clearspring.analytics.stream.membership.BloomFilterTest#testHashCount */
    @org.junit.Test(timeout = 10000)
    public void testHashCount_cf20618() {
        // Numbers are from http://pages.cs.wisc.edu/~cao/papers/summary-cache/node8.html
        com.clearspring.analytics.stream.membership.BloomFilter instance = null;
        instance = new com.clearspring.analytics.stream.membership.BloomFilter(1, 2);
        org.junit.Assert.assertEquals(1, instance.getHashCount());
        instance = new com.clearspring.analytics.stream.membership.BloomFilter(1, 3);
        org.junit.Assert.assertEquals(2, instance.getHashCount());
        instance = new com.clearspring.analytics.stream.membership.BloomFilter(1, 4);
        org.junit.Assert.assertEquals(3, instance.getHashCount());
        instance = new com.clearspring.analytics.stream.membership.BloomFilter(1, 5);
        org.junit.Assert.assertEquals(3, instance.getHashCount());
        instance = new com.clearspring.analytics.stream.membership.BloomFilter(1, 6);
        org.junit.Assert.assertEquals(4, instance.getHashCount());
        instance = new com.clearspring.analytics.stream.membership.BloomFilter(1, 7);
        org.junit.Assert.assertEquals(5, instance.getHashCount());
        /* Although technically 8*ln(2) = 5.545...
        we round down here for speed
         */
        instance = new com.clearspring.analytics.stream.membership.BloomFilter(1, 8);
        org.junit.Assert.assertEquals(5, instance.getHashCount());
        instance = new com.clearspring.analytics.stream.membership.BloomFilter(1, 9);
        org.junit.Assert.assertEquals(6, instance.getHashCount());
        instance = new com.clearspring.analytics.stream.membership.BloomFilter(1, 10);
        org.junit.Assert.assertEquals(7, instance.getHashCount());
        instance = new com.clearspring.analytics.stream.membership.BloomFilter(1, 11);
        org.junit.Assert.assertEquals(8, instance.getHashCount());
        instance = new com.clearspring.analytics.stream.membership.BloomFilter(1, 12);
        // StatementAddOnAssert local variable replacement
        com.clearspring.analytics.stream.membership.BloomFilter mergeBf = ((com.clearspring.analytics.stream.membership.BloomFilter) (bf2.merge(bf)));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((com.clearspring.analytics.stream.membership.BloomFilter)mergeBf).getHashCount(), 10);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((com.clearspring.analytics.stream.membership.BloomFilter)mergeBf).buckets(), 150080);
        // AssertGenerator replace invocation
        java.lang.String o_testHashCount_cf20618__49 = // StatementAdderMethod cloned existing statement
mergeBf.toString();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(o_testHashCount_cf20618__49, "{}");
        org.junit.Assert.assertEquals(8, instance.getHashCount());
    }

    /**
     * Test for correct k *
     */
    /* amplification of com.clearspring.analytics.stream.membership.BloomFilterTest#testHashCount */
    @org.junit.Test(timeout = 10000)
    public void testHashCount_cf20617() {
        // Numbers are from http://pages.cs.wisc.edu/~cao/papers/summary-cache/node8.html
        com.clearspring.analytics.stream.membership.BloomFilter instance = null;
        instance = new com.clearspring.analytics.stream.membership.BloomFilter(1, 2);
        org.junit.Assert.assertEquals(1, instance.getHashCount());
        instance = new com.clearspring.analytics.stream.membership.BloomFilter(1, 3);
        org.junit.Assert.assertEquals(2, instance.getHashCount());
        instance = new com.clearspring.analytics.stream.membership.BloomFilter(1, 4);
        org.junit.Assert.assertEquals(3, instance.getHashCount());
        instance = new com.clearspring.analytics.stream.membership.BloomFilter(1, 5);
        org.junit.Assert.assertEquals(3, instance.getHashCount());
        instance = new com.clearspring.analytics.stream.membership.BloomFilter(1, 6);
        org.junit.Assert.assertEquals(4, instance.getHashCount());
        instance = new com.clearspring.analytics.stream.membership.BloomFilter(1, 7);
        org.junit.Assert.assertEquals(5, instance.getHashCount());
        /* Although technically 8*ln(2) = 5.545...
        we round down here for speed
         */
        instance = new com.clearspring.analytics.stream.membership.BloomFilter(1, 8);
        org.junit.Assert.assertEquals(5, instance.getHashCount());
        instance = new com.clearspring.analytics.stream.membership.BloomFilter(1, 9);
        org.junit.Assert.assertEquals(6, instance.getHashCount());
        instance = new com.clearspring.analytics.stream.membership.BloomFilter(1, 10);
        org.junit.Assert.assertEquals(7, instance.getHashCount());
        instance = new com.clearspring.analytics.stream.membership.BloomFilter(1, 11);
        org.junit.Assert.assertEquals(8, instance.getHashCount());
        instance = new com.clearspring.analytics.stream.membership.BloomFilter(1, 12);
        // AssertGenerator replace invocation
        java.lang.String o_testHashCount_cf20617__46 = // StatementAdderMethod cloned existing statement
instance.toString();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(o_testHashCount_cf20617__46, "{}");
        org.junit.Assert.assertEquals(8, instance.getHashCount());
    }

    /**
     * Test for correct k *
     */
    /* amplification of com.clearspring.analytics.stream.membership.BloomFilterTest#testHashCount */
    @org.junit.Test(timeout = 10000)
    public void testHashCount_cf20581() {
        // Numbers are from http://pages.cs.wisc.edu/~cao/papers/summary-cache/node8.html
        com.clearspring.analytics.stream.membership.BloomFilter instance = null;
        instance = new com.clearspring.analytics.stream.membership.BloomFilter(1, 2);
        org.junit.Assert.assertEquals(1, instance.getHashCount());
        instance = new com.clearspring.analytics.stream.membership.BloomFilter(1, 3);
        org.junit.Assert.assertEquals(2, instance.getHashCount());
        instance = new com.clearspring.analytics.stream.membership.BloomFilter(1, 4);
        org.junit.Assert.assertEquals(3, instance.getHashCount());
        instance = new com.clearspring.analytics.stream.membership.BloomFilter(1, 5);
        org.junit.Assert.assertEquals(3, instance.getHashCount());
        instance = new com.clearspring.analytics.stream.membership.BloomFilter(1, 6);
        org.junit.Assert.assertEquals(4, instance.getHashCount());
        instance = new com.clearspring.analytics.stream.membership.BloomFilter(1, 7);
        org.junit.Assert.assertEquals(5, instance.getHashCount());
        /* Although technically 8*ln(2) = 5.545...
        we round down here for speed
         */
        instance = new com.clearspring.analytics.stream.membership.BloomFilter(1, 8);
        org.junit.Assert.assertEquals(5, instance.getHashCount());
        instance = new com.clearspring.analytics.stream.membership.BloomFilter(1, 9);
        org.junit.Assert.assertEquals(6, instance.getHashCount());
        instance = new com.clearspring.analytics.stream.membership.BloomFilter(1, 10);
        org.junit.Assert.assertEquals(7, instance.getHashCount());
        instance = new com.clearspring.analytics.stream.membership.BloomFilter(1, 11);
        org.junit.Assert.assertEquals(8, instance.getHashCount());
        instance = new com.clearspring.analytics.stream.membership.BloomFilter(1, 12);
        // AssertGenerator replace invocation
        com.clearspring.analytics.stream.membership.BloomFilter o_testHashCount_cf20581__46 = // StatementAdderMethod cloned existing statement
instance.alwaysMatchingBloomFilter();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((com.clearspring.analytics.stream.membership.BloomFilter)o_testHashCount_cf20581__46).getHashCount(), 1);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((com.clearspring.analytics.stream.membership.BloomFilter)o_testHashCount_cf20581__46).buckets(), 64);
        org.junit.Assert.assertEquals(8, instance.getHashCount());
    }

    /**
     * Test for correct k *
     */
    /* amplification of com.clearspring.analytics.stream.membership.BloomFilterTest#testHashCount */
    @org.junit.Test(timeout = 10000)
    public void testHashCount_cf20563() {
        // Numbers are from http://pages.cs.wisc.edu/~cao/papers/summary-cache/node8.html
        com.clearspring.analytics.stream.membership.BloomFilter instance = null;
        instance = new com.clearspring.analytics.stream.membership.BloomFilter(1, 2);
        org.junit.Assert.assertEquals(1, instance.getHashCount());
        instance = new com.clearspring.analytics.stream.membership.BloomFilter(1, 3);
        org.junit.Assert.assertEquals(2, instance.getHashCount());
        instance = new com.clearspring.analytics.stream.membership.BloomFilter(1, 4);
        org.junit.Assert.assertEquals(3, instance.getHashCount());
        instance = new com.clearspring.analytics.stream.membership.BloomFilter(1, 5);
        org.junit.Assert.assertEquals(3, instance.getHashCount());
        instance = new com.clearspring.analytics.stream.membership.BloomFilter(1, 6);
        org.junit.Assert.assertEquals(4, instance.getHashCount());
        instance = new com.clearspring.analytics.stream.membership.BloomFilter(1, 7);
        org.junit.Assert.assertEquals(5, instance.getHashCount());
        /* Although technically 8*ln(2) = 5.545...
        we round down here for speed
         */
        instance = new com.clearspring.analytics.stream.membership.BloomFilter(1, 8);
        org.junit.Assert.assertEquals(5, instance.getHashCount());
        instance = new com.clearspring.analytics.stream.membership.BloomFilter(1, 9);
        org.junit.Assert.assertEquals(6, instance.getHashCount());
        instance = new com.clearspring.analytics.stream.membership.BloomFilter(1, 10);
        org.junit.Assert.assertEquals(7, instance.getHashCount());
        instance = new com.clearspring.analytics.stream.membership.BloomFilter(1, 11);
        org.junit.Assert.assertEquals(8, instance.getHashCount());
        instance = new com.clearspring.analytics.stream.membership.BloomFilter(1, 12);
        // StatementAdderOnAssert create random local variable
        byte[] vc_4183 = new byte []{71,53,68,85};
        // AssertGenerator add assertion
        byte[] array_109448635 = new byte[]{71, 53, 68, 85};
	byte[] array_1764210420 = (byte[])vc_4183;
	for(int ii = 0; ii <array_109448635.length; ii++) {
		org.junit.Assert.assertEquals(array_109448635[ii], array_1764210420[ii]);
	};
        // AssertGenerator replace invocation
        boolean o_testHashCount_cf20563__48 = // StatementAdderMethod cloned existing statement
instance.isPresent(vc_4183);
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_testHashCount_cf20563__48);
        org.junit.Assert.assertEquals(8, instance.getHashCount());
    }

    /**
     * Test for correct k *
     */
    /* amplification of com.clearspring.analytics.stream.membership.BloomFilterTest#testHashCount */
    @org.junit.Test(timeout = 10000)
    public void testHashCount_cf20596() {
        // Numbers are from http://pages.cs.wisc.edu/~cao/papers/summary-cache/node8.html
        com.clearspring.analytics.stream.membership.BloomFilter instance = null;
        instance = new com.clearspring.analytics.stream.membership.BloomFilter(1, 2);
        org.junit.Assert.assertEquals(1, instance.getHashCount());
        instance = new com.clearspring.analytics.stream.membership.BloomFilter(1, 3);
        org.junit.Assert.assertEquals(2, instance.getHashCount());
        instance = new com.clearspring.analytics.stream.membership.BloomFilter(1, 4);
        org.junit.Assert.assertEquals(3, instance.getHashCount());
        instance = new com.clearspring.analytics.stream.membership.BloomFilter(1, 5);
        org.junit.Assert.assertEquals(3, instance.getHashCount());
        instance = new com.clearspring.analytics.stream.membership.BloomFilter(1, 6);
        org.junit.Assert.assertEquals(4, instance.getHashCount());
        instance = new com.clearspring.analytics.stream.membership.BloomFilter(1, 7);
        org.junit.Assert.assertEquals(5, instance.getHashCount());
        /* Although technically 8*ln(2) = 5.545...
        we round down here for speed
         */
        instance = new com.clearspring.analytics.stream.membership.BloomFilter(1, 8);
        org.junit.Assert.assertEquals(5, instance.getHashCount());
        instance = new com.clearspring.analytics.stream.membership.BloomFilter(1, 9);
        org.junit.Assert.assertEquals(6, instance.getHashCount());
        instance = new com.clearspring.analytics.stream.membership.BloomFilter(1, 10);
        org.junit.Assert.assertEquals(7, instance.getHashCount());
        instance = new com.clearspring.analytics.stream.membership.BloomFilter(1, 11);
        org.junit.Assert.assertEquals(8, instance.getHashCount());
        instance = new com.clearspring.analytics.stream.membership.BloomFilter(1, 12);
        // StatementAdderOnAssert create null value
        com.clearspring.analytics.stream.membership.Filter[] vc_4198 = (com.clearspring.analytics.stream.membership.Filter[])null;
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(vc_4198);
        // StatementAddOnAssert local variable replacement
        com.clearspring.analytics.stream.membership.BloomFilter mergeBf = ((com.clearspring.analytics.stream.membership.BloomFilter) (bf2.merge(bf)));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((com.clearspring.analytics.stream.membership.BloomFilter)mergeBf).getHashCount(), 10);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((com.clearspring.analytics.stream.membership.BloomFilter)mergeBf).buckets(), 150080);
        // AssertGenerator replace invocation
        com.clearspring.analytics.stream.membership.Filter o_testHashCount_cf20596__51 = // StatementAdderMethod cloned existing statement
mergeBf.merge(vc_4198);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((com.clearspring.analytics.stream.membership.BloomFilter)o_testHashCount_cf20596__51).buckets(), 150080);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((com.clearspring.analytics.stream.membership.BloomFilter)o_testHashCount_cf20596__51).getHashCount(), 10);
        org.junit.Assert.assertEquals(8, instance.getHashCount());
    }

    /**
     * Test for correct k *
     */
    /* amplification of com.clearspring.analytics.stream.membership.BloomFilterTest#testHashCount */
    @org.junit.Test(timeout = 10000)
    public void testHashCount_cf20628_failAssert101_literalMutation37730() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // Numbers are from http://pages.cs.wisc.edu/~cao/papers/summary-cache/node8.html
            com.clearspring.analytics.stream.membership.BloomFilter instance = null;
            instance = new com.clearspring.analytics.stream.membership.BloomFilter(1, 3);
            // MethodAssertGenerator build local variable
            Object o_5_0 = instance.getHashCount();
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(o_5_0, 2);
            instance = new com.clearspring.analytics.stream.membership.BloomFilter(1, 3);
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((com.clearspring.analytics.stream.membership.BloomFilter)instance).getHashCount(), 2);
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((com.clearspring.analytics.stream.membership.BloomFilter)instance).buckets(), 64);
            // MethodAssertGenerator build local variable
            Object o_9_0 = instance.getHashCount();
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(o_9_0, 2);
            instance = new com.clearspring.analytics.stream.membership.BloomFilter(1, 4);
            // MethodAssertGenerator build local variable
            Object o_13_0 = instance.getHashCount();
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(o_13_0, 3);
            instance = new com.clearspring.analytics.stream.membership.BloomFilter(1, 5);
            // MethodAssertGenerator build local variable
            Object o_17_0 = instance.getHashCount();
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(o_17_0, 3);
            instance = new com.clearspring.analytics.stream.membership.BloomFilter(1, 6);
            // MethodAssertGenerator build local variable
            Object o_21_0 = instance.getHashCount();
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(o_21_0, 4);
            instance = new com.clearspring.analytics.stream.membership.BloomFilter(1, 7);
            // MethodAssertGenerator build local variable
            Object o_25_0 = instance.getHashCount();
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(o_25_0, 5);
            /* Although technically 8*ln(2) = 5.545...
            we round down here for speed
             */
            instance = new com.clearspring.analytics.stream.membership.BloomFilter(1, 8);
            // MethodAssertGenerator build local variable
            Object o_30_0 = instance.getHashCount();
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(o_30_0, 5);
            instance = new com.clearspring.analytics.stream.membership.BloomFilter(1, 9);
            // MethodAssertGenerator build local variable
            Object o_34_0 = instance.getHashCount();
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(o_34_0, 6);
            instance = new com.clearspring.analytics.stream.membership.BloomFilter(1, 10);
            // MethodAssertGenerator build local variable
            Object o_38_0 = instance.getHashCount();
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(o_38_0, 7);
            instance = new com.clearspring.analytics.stream.membership.BloomFilter(1, 11);
            // MethodAssertGenerator build local variable
            Object o_42_0 = instance.getHashCount();
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(o_42_0, 8);
            instance = new com.clearspring.analytics.stream.membership.BloomFilter(1, 12);
            // StatementAdderOnAssert create null value
            byte[] vc_4214 = (byte[])null;
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(vc_4214);
            // StatementAddOnAssert local variable replacement
            com.clearspring.analytics.stream.membership.BloomFilter mergeBf = ((com.clearspring.analytics.stream.membership.BloomFilter) (bf2.merge(bf)));
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((com.clearspring.analytics.stream.membership.BloomFilter)mergeBf).getHashCount(), 10);
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((com.clearspring.analytics.stream.membership.BloomFilter)mergeBf).buckets(), 150080);
            // StatementAdderMethod cloned existing statement
            mergeBf.add(vc_4214);
            // MethodAssertGenerator build local variable
            Object o_53_0 = instance.getHashCount();
            org.junit.Assert.fail("testHashCount_cf20628 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    /**
     * Test for correct k *
     */
    /* amplification of com.clearspring.analytics.stream.membership.BloomFilterTest#testHashCount */
    @org.junit.Test(timeout = 10000)
    public void testHashCount_cf20582_cf30122_failAssert28_literalMutation44517() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // MethodAssertGenerator build local variable
            Object o_57_1 = 1;
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(o_57_1, 1);
            // MethodAssertGenerator build local variable
            Object o_55_1 = 64;
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(o_55_1, 64);
            // MethodAssertGenerator build local variable
            Object o_51_1 = 150080;
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(o_51_1, 150080);
            // MethodAssertGenerator build local variable
            Object o_49_1 = 10;
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(o_49_1, 10);
            // Numbers are from http://pages.cs.wisc.edu/~cao/papers/summary-cache/node8.html
            com.clearspring.analytics.stream.membership.BloomFilter instance = null;
            instance = new com.clearspring.analytics.stream.membership.BloomFilter(1, 2);
            // MethodAssertGenerator build local variable
            Object o_5_0 = instance.getHashCount();
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(o_5_0, 1);
            instance = new com.clearspring.analytics.stream.membership.BloomFilter(1, 3);
            // MethodAssertGenerator build local variable
            Object o_9_0 = instance.getHashCount();
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(o_9_0, 2);
            instance = new com.clearspring.analytics.stream.membership.BloomFilter(1, 4);
            // MethodAssertGenerator build local variable
            Object o_13_0 = instance.getHashCount();
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(o_13_0, 3);
            instance = new com.clearspring.analytics.stream.membership.BloomFilter(1, 5);
            // MethodAssertGenerator build local variable
            Object o_17_0 = instance.getHashCount();
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(o_17_0, 3);
            instance = new com.clearspring.analytics.stream.membership.BloomFilter(1, 6);
            // MethodAssertGenerator build local variable
            Object o_21_0 = instance.getHashCount();
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(o_21_0, 4);
            instance = new com.clearspring.analytics.stream.membership.BloomFilter(1, 7);
            // MethodAssertGenerator build local variable
            Object o_25_0 = instance.getHashCount();
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(o_25_0, 5);
            /* Although technically 8*ln(2) = 5.545...
            we round down here for speed
             */
            instance = new com.clearspring.analytics.stream.membership.BloomFilter(1, 8);
            // MethodAssertGenerator build local variable
            Object o_30_0 = instance.getHashCount();
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(o_30_0, 5);
            instance = new com.clearspring.analytics.stream.membership.BloomFilter(1, 9);
            // MethodAssertGenerator build local variable
            Object o_34_0 = instance.getHashCount();
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(o_34_0, 6);
            instance = new com.clearspring.analytics.stream.membership.BloomFilter(0, 10);
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((com.clearspring.analytics.stream.membership.BloomFilter)instance).getHashCount(), 7);
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((com.clearspring.analytics.stream.membership.BloomFilter)instance).buckets(), 64);
            // MethodAssertGenerator build local variable
            Object o_38_0 = instance.getHashCount();
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(o_38_0, 7);
            instance = new com.clearspring.analytics.stream.membership.BloomFilter(1, 11);
            // MethodAssertGenerator build local variable
            Object o_42_0 = instance.getHashCount();
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(o_42_0, 8);
            instance = new com.clearspring.analytics.stream.membership.BloomFilter(1, 12);
            // StatementAddOnAssert local variable replacement
            com.clearspring.analytics.stream.membership.BloomFilter mergeBf = ((com.clearspring.analytics.stream.membership.BloomFilter) (bf2.merge(bf)));
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((com.clearspring.analytics.stream.membership.BloomFilter)mergeBf).getHashCount(), 10);
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((com.clearspring.analytics.stream.membership.BloomFilter)mergeBf).buckets(), 150080);
            // MethodAssertGenerator build local variable
            Object o_49_0 = ((com.clearspring.analytics.stream.membership.BloomFilter)mergeBf).getHashCount();
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(o_49_0, 10);
            // MethodAssertGenerator build local variable
            Object o_51_0 = ((com.clearspring.analytics.stream.membership.BloomFilter)mergeBf).buckets();
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(o_51_0, 150080);
            // AssertGenerator replace invocation
            com.clearspring.analytics.stream.membership.BloomFilter o_testHashCount_cf20582__49 = // StatementAdderMethod cloned existing statement
mergeBf.alwaysMatchingBloomFilter();
            // MethodAssertGenerator build local variable
            Object o_55_0 = ((com.clearspring.analytics.stream.membership.BloomFilter)o_testHashCount_cf20582__49).buckets();
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(o_55_0, 64);
            // MethodAssertGenerator build local variable
            Object o_57_0 = ((com.clearspring.analytics.stream.membership.BloomFilter)o_testHashCount_cf20582__49).getHashCount();
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(o_57_0, 1);
            // StatementAdderOnAssert create random local variable
            byte[] vc_5943 = new byte []{64,52,125};
            // AssertGenerator add assertion
            byte[] array_1191924868 = new byte[]{64, 52, 125};
	byte[] array_504901887 = (byte[])vc_5943;
	for(int ii = 0; ii <array_1191924868.length; ii++) {
		org.junit.Assert.assertEquals(array_1191924868[ii], array_504901887[ii]);
	};
            // StatementAdderOnAssert create null value
            com.clearspring.analytics.stream.membership.BloomFilter vc_5940 = (com.clearspring.analytics.stream.membership.BloomFilter)null;
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(vc_5940);
            // StatementAdderMethod cloned existing statement
            vc_5940.isPresent(vc_5943);
            // MethodAssertGenerator build local variable
            Object o_65_0 = instance.getHashCount();
            org.junit.Assert.fail("testHashCount_cf20582_cf30122 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    /* amplification of com.clearspring.analytics.stream.membership.BloomFilterTest#testMerge */
    @org.junit.Test(timeout = 10000)
    public void testMerge_cf45298() {
        bf.add("a");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((com.clearspring.analytics.stream.membership.BloomFilter)bf).buckets(), 150080);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((com.clearspring.analytics.stream.membership.BloomFilter)bf).getHashCount(), 10);
        bf2.add("c");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((com.clearspring.analytics.stream.membership.BloomFilter)bf2).getHashCount(), 10);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((com.clearspring.analytics.stream.membership.BloomFilter)bf2).buckets(), 150080);
        com.clearspring.analytics.stream.membership.BloomFilter[] bfs = new com.clearspring.analytics.stream.membership.BloomFilter[1];
        bfs[0] = bf;
        com.clearspring.analytics.stream.membership.BloomFilter mergeBf = ((com.clearspring.analytics.stream.membership.BloomFilter) (bf2.merge(bf)));
        org.junit.Assert.assertTrue(mergeBf.isPresent("a"));
        org.junit.Assert.assertFalse(mergeBf.isPresent("b"));
        // StatementAdderOnAssert create random local variable
        byte[] vc_8527 = new byte []{};
        // StatementAdderMethod cloned existing statement
        mergeBf.add(vc_8527);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((com.clearspring.analytics.stream.membership.BloomFilter)mergeBf).getHashCount(), 10);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((com.clearspring.analytics.stream.membership.BloomFilter)mergeBf).buckets(), 150080);
        org.junit.Assert.assertTrue(mergeBf.isPresent("c"));
    }

    /* amplification of com.clearspring.analytics.stream.membership.BloomFilterTest#testMerge */
    @org.junit.Test(timeout = 10000)
    public void testMerge_cf45287() {
        bf.add("a");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((com.clearspring.analytics.stream.membership.BloomFilter)bf).buckets(), 150080);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((com.clearspring.analytics.stream.membership.BloomFilter)bf).getHashCount(), 10);
        bf2.add("c");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((com.clearspring.analytics.stream.membership.BloomFilter)bf2).getHashCount(), 10);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((com.clearspring.analytics.stream.membership.BloomFilter)bf2).buckets(), 150080);
        com.clearspring.analytics.stream.membership.BloomFilter[] bfs = new com.clearspring.analytics.stream.membership.BloomFilter[1];
        bfs[0] = bf;
        com.clearspring.analytics.stream.membership.BloomFilter mergeBf = ((com.clearspring.analytics.stream.membership.BloomFilter) (bf2.merge(bf)));
        org.junit.Assert.assertTrue(mergeBf.isPresent("a"));
        org.junit.Assert.assertFalse(mergeBf.isPresent("b"));
        // AssertGenerator replace invocation
        int o_testMerge_cf45287__11 = // StatementAdderMethod cloned existing statement
mergeBf.emptyBuckets();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(o_testMerge_cf45287__11, 150060);
        org.junit.Assert.assertTrue(mergeBf.isPresent("c"));
    }

    /* amplification of com.clearspring.analytics.stream.membership.BloomFilterTest#testMerge */
    @org.junit.Test(timeout = 10000)
    public void testMerge_cf45262() {
        bf.add("a");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((com.clearspring.analytics.stream.membership.BloomFilter)bf).buckets(), 150080);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((com.clearspring.analytics.stream.membership.BloomFilter)bf).getHashCount(), 10);
        bf2.add("c");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((com.clearspring.analytics.stream.membership.BloomFilter)bf2).getHashCount(), 10);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((com.clearspring.analytics.stream.membership.BloomFilter)bf2).buckets(), 150080);
        com.clearspring.analytics.stream.membership.BloomFilter[] bfs = new com.clearspring.analytics.stream.membership.BloomFilter[1];
        bfs[0] = bf;
        com.clearspring.analytics.stream.membership.BloomFilter mergeBf = ((com.clearspring.analytics.stream.membership.BloomFilter) (bf2.merge(bf)));
        org.junit.Assert.assertTrue(mergeBf.isPresent("a"));
        org.junit.Assert.assertFalse(mergeBf.isPresent("b"));
        // StatementAdderOnAssert create null value
        com.clearspring.analytics.stream.membership.BloomFilter vc_8502 = (com.clearspring.analytics.stream.membership.BloomFilter)null;
        // AssertGenerator replace invocation
        com.clearspring.analytics.stream.membership.BloomFilter o_testMerge_cf45262__13 = // StatementAdderMethod cloned existing statement
vc_8502.alwaysMatchingBloomFilter();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((com.clearspring.analytics.stream.membership.BloomFilter)o_testMerge_cf45262__13).getHashCount(), 1);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((com.clearspring.analytics.stream.membership.BloomFilter)o_testMerge_cf45262__13).buckets(), 64);
        org.junit.Assert.assertTrue(mergeBf.isPresent("c"));
    }

    /* amplification of com.clearspring.analytics.stream.membership.BloomFilterTest#testMerge */
    @org.junit.Test(timeout = 10000)
    public void testMerge_cf45273() {
        bf.add("a");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((com.clearspring.analytics.stream.membership.BloomFilter)bf).buckets(), 150080);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((com.clearspring.analytics.stream.membership.BloomFilter)bf).getHashCount(), 10);
        bf2.add("c");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((com.clearspring.analytics.stream.membership.BloomFilter)bf2).getHashCount(), 10);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((com.clearspring.analytics.stream.membership.BloomFilter)bf2).buckets(), 150080);
        com.clearspring.analytics.stream.membership.BloomFilter[] bfs = new com.clearspring.analytics.stream.membership.BloomFilter[1];
        bfs[0] = bf;
        com.clearspring.analytics.stream.membership.BloomFilter mergeBf = ((com.clearspring.analytics.stream.membership.BloomFilter) (bf2.merge(bf)));
        org.junit.Assert.assertTrue(mergeBf.isPresent("a"));
        org.junit.Assert.assertFalse(mergeBf.isPresent("b"));
        // StatementAdderOnAssert create null value
        com.clearspring.analytics.stream.membership.Filter[] vc_8510 = (com.clearspring.analytics.stream.membership.Filter[])null;
        // AssertGenerator replace invocation
        com.clearspring.analytics.stream.membership.Filter o_testMerge_cf45273__13 = // StatementAdderMethod cloned existing statement
mergeBf.merge(vc_8510);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((com.clearspring.analytics.stream.membership.BloomFilter)o_testMerge_cf45273__13).getHashCount(), 10);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((com.clearspring.analytics.stream.membership.BloomFilter)o_testMerge_cf45273__13).buckets(), 150080);
        org.junit.Assert.assertTrue(mergeBf.isPresent("c"));
    }

    /* amplification of com.clearspring.analytics.stream.membership.BloomFilterTest#testMerge */
    @org.junit.Test(timeout = 10000)
    public void testMerge_cf45290() {
        bf.add("a");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((com.clearspring.analytics.stream.membership.BloomFilter)bf).buckets(), 150080);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((com.clearspring.analytics.stream.membership.BloomFilter)bf).getHashCount(), 10);
        bf2.add("c");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((com.clearspring.analytics.stream.membership.BloomFilter)bf2).getHashCount(), 10);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((com.clearspring.analytics.stream.membership.BloomFilter)bf2).buckets(), 150080);
        com.clearspring.analytics.stream.membership.BloomFilter[] bfs = new com.clearspring.analytics.stream.membership.BloomFilter[1];
        bfs[0] = bf;
        com.clearspring.analytics.stream.membership.BloomFilter mergeBf = ((com.clearspring.analytics.stream.membership.BloomFilter) (bf2.merge(bf)));
        org.junit.Assert.assertTrue(mergeBf.isPresent("a"));
        org.junit.Assert.assertFalse(mergeBf.isPresent("b"));
        // AssertGenerator replace invocation
        java.lang.String o_testMerge_cf45290__11 = // StatementAdderMethod cloned existing statement
mergeBf.toString();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(o_testMerge_cf45290__11, "{951, 2007, 11239, 15801, 15859, 16325, 18759, 19815, 20945, 52329, 72505, 74807, 76519, 79893, 84513, 90313, 91369, 94327, 102371, 143461}");
        org.junit.Assert.assertTrue(mergeBf.isPresent("c"));
    }

    /* amplification of com.clearspring.analytics.stream.membership.BloomFilterTest#testMerge */
    @org.junit.Test(timeout = 10000)
    public void testMerge_cf45247() {
        bf.add("a");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((com.clearspring.analytics.stream.membership.BloomFilter)bf).buckets(), 150080);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((com.clearspring.analytics.stream.membership.BloomFilter)bf).getHashCount(), 10);
        bf2.add("c");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((com.clearspring.analytics.stream.membership.BloomFilter)bf2).getHashCount(), 10);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((com.clearspring.analytics.stream.membership.BloomFilter)bf2).buckets(), 150080);
        com.clearspring.analytics.stream.membership.BloomFilter[] bfs = new com.clearspring.analytics.stream.membership.BloomFilter[1];
        bfs[0] = bf;
        com.clearspring.analytics.stream.membership.BloomFilter mergeBf = ((com.clearspring.analytics.stream.membership.BloomFilter) (bf2.merge(bf)));
        org.junit.Assert.assertTrue(mergeBf.isPresent("a"));
        org.junit.Assert.assertFalse(mergeBf.isPresent("b"));
        // StatementAdderOnAssert create random local variable
        byte[] vc_8495 = new byte []{5};
        // AssertGenerator replace invocation
        boolean o_testMerge_cf45247__13 = // StatementAdderMethod cloned existing statement
mergeBf.isPresent(vc_8495);
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_testMerge_cf45247__13);
        org.junit.Assert.assertTrue(mergeBf.isPresent("c"));
    }

    /* amplification of com.clearspring.analytics.stream.membership.BloomFilterTest#testMerge */
    /* amplification of com.clearspring.analytics.stream.membership.BloomFilterTest#testMerge_cf45298 */
    @org.junit.Test(timeout = 10000)
    public void testMerge_cf45298_cf46885_failAssert18() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // MethodAssertGenerator build local variable
            Object o_25_1 = 150080;
            // MethodAssertGenerator build local variable
            Object o_23_1 = 10;
            // MethodAssertGenerator build local variable
            Object o_9_1 = 150080;
            // MethodAssertGenerator build local variable
            Object o_7_1 = 10;
            // MethodAssertGenerator build local variable
            Object o_4_1 = 10;
            // MethodAssertGenerator build local variable
            Object o_2_1 = 150080;
            bf.add("a");
            // MethodAssertGenerator build local variable
            Object o_2_0 = ((com.clearspring.analytics.stream.membership.BloomFilter)bf).buckets();
            // MethodAssertGenerator build local variable
            Object o_4_0 = ((com.clearspring.analytics.stream.membership.BloomFilter)bf).getHashCount();
            bf2.add("c");
            // MethodAssertGenerator build local variable
            Object o_7_0 = ((com.clearspring.analytics.stream.membership.BloomFilter)bf2).getHashCount();
            // MethodAssertGenerator build local variable
            Object o_9_0 = ((com.clearspring.analytics.stream.membership.BloomFilter)bf2).buckets();
            com.clearspring.analytics.stream.membership.BloomFilter[] bfs = new com.clearspring.analytics.stream.membership.BloomFilter[1];
            bfs[0] = bf;
            com.clearspring.analytics.stream.membership.BloomFilter mergeBf = ((com.clearspring.analytics.stream.membership.BloomFilter) (bf2.merge(bf)));
            // MethodAssertGenerator build local variable
            Object o_15_0 = mergeBf.isPresent("a");
            // MethodAssertGenerator build local variable
            Object o_17_0 = mergeBf.isPresent("b");
            // StatementAdderOnAssert create random local variable
            byte[] vc_8527 = new byte []{};
            // StatementAdderMethod cloned existing statement
            mergeBf.add(vc_8527);
            // MethodAssertGenerator build local variable
            Object o_23_0 = ((com.clearspring.analytics.stream.membership.BloomFilter)mergeBf).getHashCount();
            // MethodAssertGenerator build local variable
            Object o_25_0 = ((com.clearspring.analytics.stream.membership.BloomFilter)mergeBf).buckets();
            // StatementAdderOnAssert create null value
            byte[] vc_9342 = (byte[])null;
            // StatementAdderMethod cloned existing statement
            mergeBf.deserialize(vc_9342);
            // MethodAssertGenerator build local variable
            Object o_31_0 = mergeBf.isPresent("c");
            org.junit.Assert.fail("testMerge_cf45298_cf46885 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    /* amplification of com.clearspring.analytics.stream.membership.BloomFilterTest#testMerge */
    /* amplification of com.clearspring.analytics.stream.membership.BloomFilterTest#testMerge_literalMutation45237 */
    @org.junit.Test(timeout = 10000)
    public void testMerge_literalMutation45237_failAssert0_add47119() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // MethodCallAdder
            bf.add("a");
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((com.clearspring.analytics.stream.membership.BloomFilter)bf).buckets(), 150080);
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((com.clearspring.analytics.stream.membership.BloomFilter)bf).getHashCount(), 10);
            bf.add("a");
            bf2.add("c");
            com.clearspring.analytics.stream.membership.BloomFilter[] bfs = new com.clearspring.analytics.stream.membership.BloomFilter[// TestDataMutator on numbers
            0];
            bfs[0] = bf;
            com.clearspring.analytics.stream.membership.BloomFilter mergeBf = ((com.clearspring.analytics.stream.membership.BloomFilter) (bf2.merge(bf)));
            // MethodAssertGenerator build local variable
            Object o_8_0 = mergeBf.isPresent("a");
            // MethodAssertGenerator build local variable
            Object o_10_0 = mergeBf.isPresent("b");
            // MethodAssertGenerator build local variable
            Object o_12_0 = mergeBf.isPresent("c");
            org.junit.Assert.fail("testMerge_literalMutation45237 should have thrown ArrayIndexOutOfBoundsException");
        } catch (java.lang.ArrayIndexOutOfBoundsException eee) {
        }
    }

    /* amplification of com.clearspring.analytics.stream.membership.BloomFilterTest#testMerge */
    /* amplification of com.clearspring.analytics.stream.membership.BloomFilterTest#testMerge_cf45254 */
    @org.junit.Test(timeout = 10000)
    public void testMerge_cf45254_literalMutation45826_failAssert58_literalMutation48287() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // MethodAssertGenerator build local variable
            Object o_9_1 = 150080;
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(o_9_1, 150080);
            // MethodAssertGenerator build local variable
            Object o_7_1 = 10;
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(o_7_1, 10);
            // MethodAssertGenerator build local variable
            Object o_4_1 = 10;
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(o_4_1, 10);
            // MethodAssertGenerator build local variable
            Object o_2_1 = 150080;
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(o_2_1, 150080);
            bf.add("a");
            // MethodAssertGenerator build local variable
            Object o_2_0 = ((com.clearspring.analytics.stream.membership.BloomFilter)bf).buckets();
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(o_2_0, 150080);
            // MethodAssertGenerator build local variable
            Object o_4_0 = ((com.clearspring.analytics.stream.membership.BloomFilter)bf).getHashCount();
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(o_4_0, 10);
            bf2.add("c");
            // MethodAssertGenerator build local variable
            Object o_7_0 = ((com.clearspring.analytics.stream.membership.BloomFilter)bf2).getHashCount();
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(o_7_0, 10);
            // MethodAssertGenerator build local variable
            Object o_9_0 = ((com.clearspring.analytics.stream.membership.BloomFilter)bf2).buckets();
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(o_9_0, 150080);
            com.clearspring.analytics.stream.membership.BloomFilter[] bfs = new com.clearspring.analytics.stream.membership.BloomFilter[1];
            bfs[-2] = bf;
            com.clearspring.analytics.stream.membership.BloomFilter mergeBf = ((com.clearspring.analytics.stream.membership.BloomFilter) (bf2.merge(bf)));
            // MethodAssertGenerator build local variable
            Object o_16_0 = mergeBf.isPresent("a");
            // MethodAssertGenerator build local variable
            Object o_18_0 = mergeBf.isPresent("b");
            // StatementAdderOnAssert create literal from method
            java.lang.String String_vc_202 = "c";
            // AssertGenerator replace invocation
            boolean o_testMerge_cf45254__13 = // StatementAdderMethod cloned existing statement
mergeBf.isPresent(String_vc_202);
            // MethodAssertGenerator build local variable
            Object o_24_0 = o_testMerge_cf45254__13;
            // MethodAssertGenerator build local variable
            Object o_26_0 = mergeBf.isPresent("c");
            org.junit.Assert.fail("testMerge_cf45254_literalMutation45826 should have thrown ArrayIndexOutOfBoundsException");
        } catch (java.lang.ArrayIndexOutOfBoundsException eee) {
        }
    }

    /* amplification of com.clearspring.analytics.stream.membership.BloomFilterTest#testMergeException */
    /* amplification of com.clearspring.analytics.stream.membership.BloomFilterTest#testMergeException_literalMutation48474 */
    @org.junit.Test(expected = java.lang.IllegalArgumentException.class)
    public void testMergeException_literalMutation48474_literalMutation48712() {
        com.clearspring.analytics.stream.membership.BloomFilter bf3 = new com.clearspring.analytics.stream.membership.BloomFilter(((com.clearspring.analytics.stream.membership.AmplBloomFilterTest.ELEMENTS) * 10), 1);
        com.clearspring.analytics.stream.membership.BloomFilter[] bfs = new com.clearspring.analytics.stream.membership.BloomFilter[1];
        bfs[0] = bf;
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((com.clearspring.analytics.stream.membership.BloomFilter)bfs[0]).getHashCount(), 10);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((com.clearspring.analytics.stream.membership.BloomFilter)bfs[0]).buckets(), 150080);
        com.clearspring.analytics.stream.membership.BloomFilter mergeBf = ((com.clearspring.analytics.stream.membership.BloomFilter) (bf3.merge(bf)));
    }

    /* amplification of com.clearspring.analytics.stream.membership.BloomFilterTest#testMergeException */
    /* amplification of com.clearspring.analytics.stream.membership.BloomFilterTest#testMergeException_literalMutation48466 */
    @org.junit.Test(timeout = 10000)
    public void testMergeException_literalMutation48466_literalMutation48565_cf54763_failAssert63() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // MethodAssertGenerator build local variable
            Object o_11_1 = 1;
            // MethodAssertGenerator build local variable
            Object o_5_1 = 400064;
            com.clearspring.analytics.stream.membership.BloomFilter bf3 = new com.clearspring.analytics.stream.membership.BloomFilter(((com.clearspring.analytics.stream.membership.AmplBloomFilterTest.ELEMENTS) * 20), 2);
            // MethodAssertGenerator build local variable
            Object o_5_0 = ((com.clearspring.analytics.stream.membership.BloomFilter)bf3).buckets();
            // StatementAdderOnAssert create random local variable
            byte[] vc_12147 = new byte []{71,80};
            // StatementAdderMethod cloned existing statement
            bf3.isPresent(vc_12147);
            // MethodAssertGenerator build local variable
            Object o_11_0 = ((com.clearspring.analytics.stream.membership.BloomFilter)bf3).getHashCount();
            com.clearspring.analytics.stream.membership.BloomFilter[] bfs = new com.clearspring.analytics.stream.membership.BloomFilter[1];
            bfs[0] = bf;
            com.clearspring.analytics.stream.membership.BloomFilter mergeBf = ((com.clearspring.analytics.stream.membership.BloomFilter) (bf3.merge(bf)));
            org.junit.Assert.fail("testMergeException_literalMutation48466_literalMutation48565_cf54763 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of com.clearspring.analytics.stream.membership.BloomFilterTest#testMergeException */
    /* amplification of com.clearspring.analytics.stream.membership.BloomFilterTest#testMergeException_literalMutation48467 */
    @org.junit.Test(timeout = 10000)
    public void testMergeException_literalMutation48467_literalMutation48583_cf56086_failAssert20() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // MethodAssertGenerator build local variable
            Object o_11_1 = 1;
            // MethodAssertGenerator build local variable
            Object o_5_1 = 64;
            com.clearspring.analytics.stream.membership.BloomFilter bf3 = new com.clearspring.analytics.stream.membership.BloomFilter(((com.clearspring.analytics.stream.membership.AmplBloomFilterTest.ELEMENTS) * 11), 0);
            // MethodAssertGenerator build local variable
            Object o_5_0 = ((com.clearspring.analytics.stream.membership.BloomFilter)bf3).buckets();
            // StatementAdderOnAssert create random local variable
            byte[] vc_12707 = new byte []{107,86,58,122};
            // StatementAdderMethod cloned existing statement
            bf3.add(vc_12707);
            // MethodAssertGenerator build local variable
            Object o_11_0 = ((com.clearspring.analytics.stream.membership.BloomFilter)bf3).getHashCount();
            com.clearspring.analytics.stream.membership.BloomFilter[] bfs = new com.clearspring.analytics.stream.membership.BloomFilter[1];
            bfs[0] = bf;
            com.clearspring.analytics.stream.membership.BloomFilter mergeBf = ((com.clearspring.analytics.stream.membership.BloomFilter) (bf3.merge(bf)));
            org.junit.Assert.fail("testMergeException_literalMutation48467_literalMutation48583_cf56086 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of com.clearspring.analytics.stream.membership.BloomFilterTest#testMergeException */
    /* amplification of com.clearspring.analytics.stream.membership.BloomFilterTest#testMergeException_literalMutation48465 */
    @org.junit.Test(timeout = 10000)
    public void testMergeException_literalMutation48465_literalMutation48551_cf53661_failAssert49() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // MethodAssertGenerator build local variable
            Object o_9_1 = 1;
            // MethodAssertGenerator build local variable
            Object o_5_1 = 64;
            com.clearspring.analytics.stream.membership.BloomFilter bf3 = new com.clearspring.analytics.stream.membership.BloomFilter(((com.clearspring.analytics.stream.membership.AmplBloomFilterTest.ELEMENTS) * 11), 0);
            // MethodAssertGenerator build local variable
            Object o_5_0 = ((com.clearspring.analytics.stream.membership.BloomFilter)bf3).buckets();
            // StatementAdderMethod cloned existing statement
            bf3.emptyBuckets();
            // MethodAssertGenerator build local variable
            Object o_9_0 = ((com.clearspring.analytics.stream.membership.BloomFilter)bf3).getHashCount();
            com.clearspring.analytics.stream.membership.BloomFilter[] bfs = new com.clearspring.analytics.stream.membership.BloomFilter[1];
            bfs[0] = bf;
            com.clearspring.analytics.stream.membership.BloomFilter mergeBf = ((com.clearspring.analytics.stream.membership.BloomFilter) (bf3.merge(bf)));
            org.junit.Assert.fail("testMergeException_literalMutation48465_literalMutation48551_cf53661 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of com.clearspring.analytics.stream.membership.BloomFilterTest#testMergeException */
    /* amplification of com.clearspring.analytics.stream.membership.BloomFilterTest#testMergeException_literalMutation48476 */
    @org.junit.Test(timeout = 10000)
    public void testMergeException_literalMutation48476_literalMutation48733_cf55411_failAssert41() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // MethodAssertGenerator build local variable
            Object o_10_1 = 1;
            // MethodAssertGenerator build local variable
            Object o_4_1 = 90048;
            com.clearspring.analytics.stream.membership.BloomFilter bf3 = new com.clearspring.analytics.stream.membership.BloomFilter(((com.clearspring.analytics.stream.membership.AmplBloomFilterTest.ELEMENTS) * 9), 1);
            // MethodAssertGenerator build local variable
            Object o_4_0 = ((com.clearspring.analytics.stream.membership.BloomFilter)bf3).buckets();
            // StatementAdderOnAssert create null value
            com.clearspring.analytics.stream.membership.BloomFilter vc_12418 = (com.clearspring.analytics.stream.membership.BloomFilter)null;
            // StatementAdderMethod cloned existing statement
            vc_12418.alwaysMatchingBloomFilter();
            // MethodAssertGenerator build local variable
            Object o_10_0 = ((com.clearspring.analytics.stream.membership.BloomFilter)bf3).getHashCount();
            com.clearspring.analytics.stream.membership.BloomFilter[] bfs = new com.clearspring.analytics.stream.membership.BloomFilter[1];
            bfs[0] = bf;
            com.clearspring.analytics.stream.membership.BloomFilter mergeBf = ((com.clearspring.analytics.stream.membership.BloomFilter) (bf3.merge(bf)));
            org.junit.Assert.fail("testMergeException_literalMutation48476_literalMutation48733_cf55411 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of com.clearspring.analytics.stream.membership.BloomFilterTest#testMergeException */
    /* amplification of com.clearspring.analytics.stream.membership.BloomFilterTest#testMergeException_literalMutation48477 */
    @org.junit.Test(timeout = 10000)
    public void testMergeException_literalMutation48477_literalMutation48756_cf49969_failAssert16() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // MethodAssertGenerator build local variable
            Object o_10_1 = 1;
            // MethodAssertGenerator build local variable
            Object o_4_1 = 64;
            com.clearspring.analytics.stream.membership.BloomFilter bf3 = new com.clearspring.analytics.stream.membership.BloomFilter(((com.clearspring.analytics.stream.membership.AmplBloomFilterTest.ELEMENTS) * 10), 0);
            // MethodAssertGenerator build local variable
            Object o_4_0 = ((com.clearspring.analytics.stream.membership.BloomFilter)bf3).buckets();
            // StatementAdderOnAssert create null value
            com.clearspring.analytics.stream.membership.Filter[] vc_10138 = (com.clearspring.analytics.stream.membership.Filter[])null;
            // StatementAdderMethod cloned existing statement
            bf3.merge(vc_10138);
            // MethodAssertGenerator build local variable
            Object o_10_0 = ((com.clearspring.analytics.stream.membership.BloomFilter)bf3).getHashCount();
            com.clearspring.analytics.stream.membership.BloomFilter[] bfs = new com.clearspring.analytics.stream.membership.BloomFilter[1];
            bfs[0] = bf;
            com.clearspring.analytics.stream.membership.BloomFilter mergeBf = ((com.clearspring.analytics.stream.membership.BloomFilter) (bf3.merge(bf)));
            org.junit.Assert.fail("testMergeException_literalMutation48477_literalMutation48756_cf49969 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of com.clearspring.analytics.stream.membership.BloomFilterTest#testOne */
    @org.junit.Test(timeout = 10000)
    public void testOne_cf56235() {
        bf.add("a");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((com.clearspring.analytics.stream.membership.BloomFilter)bf).buckets(), 150080);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((com.clearspring.analytics.stream.membership.BloomFilter)bf).getHashCount(), 10);
        org.junit.Assert.assertTrue(bf.isPresent("a"));
        // StatementAdderOnAssert create null value
        com.clearspring.analytics.stream.membership.BloomFilter vc_12780 = (com.clearspring.analytics.stream.membership.BloomFilter)null;
        // StatementAdderMethod cloned existing statement
        vc_12780.serializer();
        org.junit.Assert.assertFalse(bf.isPresent("b"));
    }

    /* amplification of com.clearspring.analytics.stream.membership.BloomFilterTest#testOne */
    @org.junit.Test(timeout = 10000)
    public void testOne_cf56225() {
        bf.add("a");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((com.clearspring.analytics.stream.membership.BloomFilter)bf).buckets(), 150080);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((com.clearspring.analytics.stream.membership.BloomFilter)bf).getHashCount(), 10);
        org.junit.Assert.assertTrue(bf.isPresent("a"));
        // StatementAdderOnAssert create null value
        com.clearspring.analytics.stream.membership.BloomFilter vc_12770 = (com.clearspring.analytics.stream.membership.BloomFilter)null;
        // AssertGenerator replace invocation
        com.clearspring.analytics.stream.membership.BloomFilter o_testOne_cf56225__6 = // StatementAdderMethod cloned existing statement
vc_12770.alwaysMatchingBloomFilter();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((com.clearspring.analytics.stream.membership.BloomFilter)o_testOne_cf56225__6).buckets(), 64);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((com.clearspring.analytics.stream.membership.BloomFilter)o_testOne_cf56225__6).getHashCount(), 1);
        org.junit.Assert.assertFalse(bf.isPresent("b"));
    }

    /* amplification of com.clearspring.analytics.stream.membership.BloomFilterTest#testOne */
    /* amplification of com.clearspring.analytics.stream.membership.BloomFilterTest#testOne_add56212 */
    @org.junit.Test(timeout = 10000)
    public void testOne_add56212_cf56287_failAssert26_add56720() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // MethodAssertGenerator build local variable
            Object o_10_1 = 10;
            // MethodAssertGenerator build local variable
            Object o_8_1 = 150080;
            // MethodAssertGenerator build local variable
            Object o_5_1 = 10;
            // MethodAssertGenerator build local variable
            Object o_3_1 = 150080;
            // MethodCallAdder
            bf.add("a");
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((com.clearspring.analytics.stream.membership.BloomFilter)bf).buckets(), 150080);
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((com.clearspring.analytics.stream.membership.BloomFilter)bf).getHashCount(), 10);
            // MethodAssertGenerator build local variable
            Object o_3_0 = ((com.clearspring.analytics.stream.membership.BloomFilter)bf).buckets();
            // MethodAssertGenerator build local variable
            Object o_5_0 = ((com.clearspring.analytics.stream.membership.BloomFilter)bf).getHashCount();
            // MethodCallAdder
            bf.add("a");
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((com.clearspring.analytics.stream.membership.BloomFilter)bf).buckets(), 150080);
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((com.clearspring.analytics.stream.membership.BloomFilter)bf).getHashCount(), 10);
            bf.add("a");
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((com.clearspring.analytics.stream.membership.BloomFilter)bf).buckets(), 150080);
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((com.clearspring.analytics.stream.membership.BloomFilter)bf).getHashCount(), 10);
            // MethodAssertGenerator build local variable
            Object o_8_0 = ((com.clearspring.analytics.stream.membership.BloomFilter)bf).buckets();
            // MethodAssertGenerator build local variable
            Object o_10_0 = ((com.clearspring.analytics.stream.membership.BloomFilter)bf).getHashCount();
            // MethodAssertGenerator build local variable
            Object o_12_0 = bf.isPresent("a");
            // StatementAdderOnAssert create null value
            com.clearspring.analytics.stream.membership.BloomFilter vc_12826 = (com.clearspring.analytics.stream.membership.BloomFilter)null;
            // StatementAdderMethod cloned existing statement
            vc_12826.tserializer();
            // MethodAssertGenerator build local variable
            Object o_18_0 = bf.isPresent("b");
            org.junit.Assert.fail("testOne_add56212_cf56287 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    /* amplification of com.clearspring.analytics.stream.membership.BloomFilterTest#testSerialize */
    @org.junit.Test(timeout = 10000)
    public void testSerialize_add56834() throws java.io.IOException {
        // AssertGenerator replace invocation
        com.clearspring.analytics.stream.membership.Filter o_testSerialize_add56834__1 = // MethodCallAdder
com.clearspring.analytics.stream.membership.FilterTest.testSerialize(bf);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((com.clearspring.analytics.stream.membership.BloomFilter)o_testSerialize_add56834__1).getHashCount(), 10);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((com.clearspring.analytics.stream.membership.BloomFilter)o_testSerialize_add56834__1).buckets(), 150080);
        // AssertGenerator replace invocation
        com.clearspring.analytics.stream.membership.Filter o_testSerialize_add56834__3 = com.clearspring.analytics.stream.membership.FilterTest.testSerialize(bf);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((com.clearspring.analytics.stream.membership.BloomFilter)o_testSerialize_add56834__3).getHashCount(), 10);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((com.clearspring.analytics.stream.membership.BloomFilter)o_testSerialize_add56834__3).buckets(), 150080);
    }

    /* amplification of com.clearspring.analytics.stream.membership.BloomFilterTest#testSizing */
    @org.junit.Test(timeout = 10000)
    public void testSizing_cf57202() throws java.io.IOException {
        com.clearspring.analytics.stream.membership.BloomFilter f = null;
        org.junit.Assert.assertEquals(128, (f = new com.clearspring.analytics.stream.membership.BloomFilter(10, 0.05)).buckets());
        org.junit.Assert.assertEquals(93, serialize(f).length);
        org.junit.Assert.assertEquals(768, new com.clearspring.analytics.stream.membership.BloomFilter(100, 0.05).buckets());
        org.junit.Assert.assertEquals(7040, new com.clearspring.analytics.stream.membership.BloomFilter(1000, 0.05).buckets());
        org.junit.Assert.assertEquals(70080, new com.clearspring.analytics.stream.membership.BloomFilter(10000, 0.05).buckets());
        org.junit.Assert.assertEquals(700032, new com.clearspring.analytics.stream.membership.BloomFilter(100000, 0.05).buckets());
        org.junit.Assert.assertEquals(7000064, new com.clearspring.analytics.stream.membership.BloomFilter(1000000, 0.05).buckets());
        org.junit.Assert.assertEquals(128, new com.clearspring.analytics.stream.membership.BloomFilter(10, 0.01).buckets());
        org.junit.Assert.assertEquals(1024, new com.clearspring.analytics.stream.membership.BloomFilter(100, 0.01).buckets());
        org.junit.Assert.assertEquals(10048, (f = new com.clearspring.analytics.stream.membership.BloomFilter(1000, 0.01)).buckets());
        org.junit.Assert.assertEquals(1333, serialize(f).length);
        org.junit.Assert.assertEquals(100032, (f = new com.clearspring.analytics.stream.membership.BloomFilter(10000, 0.01)).buckets());
        org.junit.Assert.assertEquals(12581, serialize(f).length);
        org.junit.Assert.assertEquals(1000064, (f = new com.clearspring.analytics.stream.membership.BloomFilter(100000, 0.01)).buckets());
        org.junit.Assert.assertEquals(125085, serialize(f).length);
        org.junit.Assert.assertEquals(10000064, (f = new com.clearspring.analytics.stream.membership.BloomFilter(1000000, 0.01)).buckets());
        org.junit.Assert.assertEquals(1250085, serialize(f).length);
        for (java.lang.String s : new com.clearspring.analytics.stream.membership.KeyGenerator.RandomStringGenerator(new java.util.Random().nextInt(), 1000000)) {
            f.add(s);
        }
        org.junit.Assert.assertEquals(10000064, f.buckets());
        // StatementAddOnAssert local variable replacement
        com.clearspring.analytics.stream.membership.BloomFilter mergeBf = ((com.clearspring.analytics.stream.membership.BloomFilter) (bf2.merge(bf)));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((com.clearspring.analytics.stream.membership.BloomFilter)mergeBf).getHashCount(), 10);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((com.clearspring.analytics.stream.membership.BloomFilter)mergeBf).buckets(), 150080);
        // StatementAdderMethod cloned existing statement
        mergeBf.clear();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((com.clearspring.analytics.stream.membership.BloomFilter)mergeBf).getHashCount(), 10);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((com.clearspring.analytics.stream.membership.BloomFilter)mergeBf).buckets(), 150080);
        org.junit.Assert.assertEquals(1250085, serialize(f).length);
    }

    /* amplification of com.clearspring.analytics.stream.membership.BloomFilterTest#testSizing */
    @org.junit.Test(timeout = 10000)
    public void testSizing_cf57137() throws java.io.IOException {
        com.clearspring.analytics.stream.membership.BloomFilter f = null;
        org.junit.Assert.assertEquals(128, (f = new com.clearspring.analytics.stream.membership.BloomFilter(10, 0.05)).buckets());
        org.junit.Assert.assertEquals(93, serialize(f).length);
        org.junit.Assert.assertEquals(768, new com.clearspring.analytics.stream.membership.BloomFilter(100, 0.05).buckets());
        org.junit.Assert.assertEquals(7040, new com.clearspring.analytics.stream.membership.BloomFilter(1000, 0.05).buckets());
        org.junit.Assert.assertEquals(70080, new com.clearspring.analytics.stream.membership.BloomFilter(10000, 0.05).buckets());
        org.junit.Assert.assertEquals(700032, new com.clearspring.analytics.stream.membership.BloomFilter(100000, 0.05).buckets());
        org.junit.Assert.assertEquals(7000064, new com.clearspring.analytics.stream.membership.BloomFilter(1000000, 0.05).buckets());
        org.junit.Assert.assertEquals(128, new com.clearspring.analytics.stream.membership.BloomFilter(10, 0.01).buckets());
        org.junit.Assert.assertEquals(1024, new com.clearspring.analytics.stream.membership.BloomFilter(100, 0.01).buckets());
        org.junit.Assert.assertEquals(10048, (f = new com.clearspring.analytics.stream.membership.BloomFilter(1000, 0.01)).buckets());
        org.junit.Assert.assertEquals(1333, serialize(f).length);
        org.junit.Assert.assertEquals(100032, (f = new com.clearspring.analytics.stream.membership.BloomFilter(10000, 0.01)).buckets());
        org.junit.Assert.assertEquals(12581, serialize(f).length);
        org.junit.Assert.assertEquals(1000064, (f = new com.clearspring.analytics.stream.membership.BloomFilter(100000, 0.01)).buckets());
        org.junit.Assert.assertEquals(125085, serialize(f).length);
        org.junit.Assert.assertEquals(10000064, (f = new com.clearspring.analytics.stream.membership.BloomFilter(1000000, 0.01)).buckets());
        org.junit.Assert.assertEquals(1250085, serialize(f).length);
        for (java.lang.String s : new com.clearspring.analytics.stream.membership.KeyGenerator.RandomStringGenerator(new java.util.Random().nextInt(), 1000000)) {
            f.add(s);
        }
        org.junit.Assert.assertEquals(10000064, f.buckets());
        // AssertGenerator replace invocation
        com.clearspring.analytics.stream.membership.BloomFilter o_testSizing_cf57137__62 = // StatementAdderMethod cloned existing statement
f.alwaysMatchingBloomFilter();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((com.clearspring.analytics.stream.membership.BloomFilter)o_testSizing_cf57137__62).buckets(), 64);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((com.clearspring.analytics.stream.membership.BloomFilter)o_testSizing_cf57137__62).getHashCount(), 1);
        org.junit.Assert.assertEquals(1250085, serialize(f).length);
    }

    /* amplification of com.clearspring.analytics.stream.membership.BloomFilterTest#testSizing */
    @org.junit.Test(timeout = 10000)
    public void testSizing_cf57191() throws java.io.IOException {
        com.clearspring.analytics.stream.membership.BloomFilter f = null;
        org.junit.Assert.assertEquals(128, (f = new com.clearspring.analytics.stream.membership.BloomFilter(10, 0.05)).buckets());
        org.junit.Assert.assertEquals(93, serialize(f).length);
        org.junit.Assert.assertEquals(768, new com.clearspring.analytics.stream.membership.BloomFilter(100, 0.05).buckets());
        org.junit.Assert.assertEquals(7040, new com.clearspring.analytics.stream.membership.BloomFilter(1000, 0.05).buckets());
        org.junit.Assert.assertEquals(70080, new com.clearspring.analytics.stream.membership.BloomFilter(10000, 0.05).buckets());
        org.junit.Assert.assertEquals(700032, new com.clearspring.analytics.stream.membership.BloomFilter(100000, 0.05).buckets());
        org.junit.Assert.assertEquals(7000064, new com.clearspring.analytics.stream.membership.BloomFilter(1000000, 0.05).buckets());
        org.junit.Assert.assertEquals(128, new com.clearspring.analytics.stream.membership.BloomFilter(10, 0.01).buckets());
        org.junit.Assert.assertEquals(1024, new com.clearspring.analytics.stream.membership.BloomFilter(100, 0.01).buckets());
        org.junit.Assert.assertEquals(10048, (f = new com.clearspring.analytics.stream.membership.BloomFilter(1000, 0.01)).buckets());
        org.junit.Assert.assertEquals(1333, serialize(f).length);
        org.junit.Assert.assertEquals(100032, (f = new com.clearspring.analytics.stream.membership.BloomFilter(10000, 0.01)).buckets());
        org.junit.Assert.assertEquals(12581, serialize(f).length);
        org.junit.Assert.assertEquals(1000064, (f = new com.clearspring.analytics.stream.membership.BloomFilter(100000, 0.01)).buckets());
        org.junit.Assert.assertEquals(125085, serialize(f).length);
        org.junit.Assert.assertEquals(10000064, (f = new com.clearspring.analytics.stream.membership.BloomFilter(1000000, 0.01)).buckets());
        org.junit.Assert.assertEquals(1250085, serialize(f).length);
        for (java.lang.String s : new com.clearspring.analytics.stream.membership.KeyGenerator.RandomStringGenerator(new java.util.Random().nextInt(), 1000000)) {
            f.add(s);
        }
        org.junit.Assert.assertEquals(10000064, f.buckets());
        // StatementAdderOnAssert create random local variable
        java.lang.String vc_13063 = new java.lang.String();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(vc_13063, "");
        // StatementAdderMethod cloned existing statement
        f.add(vc_13063);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((com.clearspring.analytics.stream.membership.BloomFilter)f).getHashCount(), 5);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((com.clearspring.analytics.stream.membership.BloomFilter)f).buckets(), 10000064);
        org.junit.Assert.assertEquals(1250085, serialize(f).length);
    }

    /* amplification of com.clearspring.analytics.stream.membership.BloomFilterTest#testSizing */
    @org.junit.Test(timeout = 10000)
    public void testSizing_cf57170() throws java.io.IOException {
        com.clearspring.analytics.stream.membership.BloomFilter f = null;
        org.junit.Assert.assertEquals(128, (f = new com.clearspring.analytics.stream.membership.BloomFilter(10, 0.05)).buckets());
        org.junit.Assert.assertEquals(93, serialize(f).length);
        org.junit.Assert.assertEquals(768, new com.clearspring.analytics.stream.membership.BloomFilter(100, 0.05).buckets());
        org.junit.Assert.assertEquals(7040, new com.clearspring.analytics.stream.membership.BloomFilter(1000, 0.05).buckets());
        org.junit.Assert.assertEquals(70080, new com.clearspring.analytics.stream.membership.BloomFilter(10000, 0.05).buckets());
        org.junit.Assert.assertEquals(700032, new com.clearspring.analytics.stream.membership.BloomFilter(100000, 0.05).buckets());
        org.junit.Assert.assertEquals(7000064, new com.clearspring.analytics.stream.membership.BloomFilter(1000000, 0.05).buckets());
        org.junit.Assert.assertEquals(128, new com.clearspring.analytics.stream.membership.BloomFilter(10, 0.01).buckets());
        org.junit.Assert.assertEquals(1024, new com.clearspring.analytics.stream.membership.BloomFilter(100, 0.01).buckets());
        org.junit.Assert.assertEquals(10048, (f = new com.clearspring.analytics.stream.membership.BloomFilter(1000, 0.01)).buckets());
        org.junit.Assert.assertEquals(1333, serialize(f).length);
        org.junit.Assert.assertEquals(100032, (f = new com.clearspring.analytics.stream.membership.BloomFilter(10000, 0.01)).buckets());
        org.junit.Assert.assertEquals(12581, serialize(f).length);
        org.junit.Assert.assertEquals(1000064, (f = new com.clearspring.analytics.stream.membership.BloomFilter(100000, 0.01)).buckets());
        org.junit.Assert.assertEquals(125085, serialize(f).length);
        org.junit.Assert.assertEquals(10000064, (f = new com.clearspring.analytics.stream.membership.BloomFilter(1000000, 0.01)).buckets());
        org.junit.Assert.assertEquals(1250085, serialize(f).length);
        for (java.lang.String s : new com.clearspring.analytics.stream.membership.KeyGenerator.RandomStringGenerator(new java.util.Random().nextInt(), 1000000)) {
            f.add(s);
        }
        org.junit.Assert.assertEquals(10000064, f.buckets());
        // StatementAddOnAssert local variable replacement
        com.clearspring.analytics.stream.membership.BloomFilter mergeBf = ((com.clearspring.analytics.stream.membership.BloomFilter) (bf2.merge(bf)));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((com.clearspring.analytics.stream.membership.BloomFilter)mergeBf).getHashCount(), 10);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((com.clearspring.analytics.stream.membership.BloomFilter)mergeBf).buckets(), 150080);
        // AssertGenerator replace invocation
        int o_testSizing_cf57170__65 = // StatementAdderMethod cloned existing statement
mergeBf.emptyBuckets();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(o_testSizing_cf57170__65, 150080);
        org.junit.Assert.assertEquals(1250085, serialize(f).length);
    }

    /* amplification of com.clearspring.analytics.stream.membership.BloomFilterTest#testSizing */
    @org.junit.Test(timeout = 10000)
    public void testSizing_cf57183() throws java.io.IOException {
        com.clearspring.analytics.stream.membership.BloomFilter f = null;
        org.junit.Assert.assertEquals(128, (f = new com.clearspring.analytics.stream.membership.BloomFilter(10, 0.05)).buckets());
        org.junit.Assert.assertEquals(93, serialize(f).length);
        org.junit.Assert.assertEquals(768, new com.clearspring.analytics.stream.membership.BloomFilter(100, 0.05).buckets());
        org.junit.Assert.assertEquals(7040, new com.clearspring.analytics.stream.membership.BloomFilter(1000, 0.05).buckets());
        org.junit.Assert.assertEquals(70080, new com.clearspring.analytics.stream.membership.BloomFilter(10000, 0.05).buckets());
        org.junit.Assert.assertEquals(700032, new com.clearspring.analytics.stream.membership.BloomFilter(100000, 0.05).buckets());
        org.junit.Assert.assertEquals(7000064, new com.clearspring.analytics.stream.membership.BloomFilter(1000000, 0.05).buckets());
        org.junit.Assert.assertEquals(128, new com.clearspring.analytics.stream.membership.BloomFilter(10, 0.01).buckets());
        org.junit.Assert.assertEquals(1024, new com.clearspring.analytics.stream.membership.BloomFilter(100, 0.01).buckets());
        org.junit.Assert.assertEquals(10048, (f = new com.clearspring.analytics.stream.membership.BloomFilter(1000, 0.01)).buckets());
        org.junit.Assert.assertEquals(1333, serialize(f).length);
        org.junit.Assert.assertEquals(100032, (f = new com.clearspring.analytics.stream.membership.BloomFilter(10000, 0.01)).buckets());
        org.junit.Assert.assertEquals(12581, serialize(f).length);
        org.junit.Assert.assertEquals(1000064, (f = new com.clearspring.analytics.stream.membership.BloomFilter(100000, 0.01)).buckets());
        org.junit.Assert.assertEquals(125085, serialize(f).length);
        org.junit.Assert.assertEquals(10000064, (f = new com.clearspring.analytics.stream.membership.BloomFilter(1000000, 0.01)).buckets());
        org.junit.Assert.assertEquals(1250085, serialize(f).length);
        for (java.lang.String s : new com.clearspring.analytics.stream.membership.KeyGenerator.RandomStringGenerator(new java.util.Random().nextInt(), 1000000)) {
            f.add(s);
        }
        org.junit.Assert.assertEquals(10000064, f.buckets());
        // StatementAdderOnAssert create random local variable
        byte[] vc_13059 = new byte []{60,122,94,70};
        // AssertGenerator add assertion
        byte[] array_1205426656 = new byte[]{60, 122, 94, 70};
	byte[] array_227636935 = (byte[])vc_13059;
	for(int ii = 0; ii <array_1205426656.length; ii++) {
		org.junit.Assert.assertEquals(array_1205426656[ii], array_227636935[ii]);
	};
        // StatementAdderMethod cloned existing statement
        f.add(vc_13059);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((com.clearspring.analytics.stream.membership.BloomFilter)f).getHashCount(), 5);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((com.clearspring.analytics.stream.membership.BloomFilter)f).buckets(), 10000064);
        org.junit.Assert.assertEquals(1250085, serialize(f).length);
    }

    /* amplification of com.clearspring.analytics.stream.membership.BloomFilterTest#testSizing */
    @org.junit.Test(timeout = 10000)
    public void testSizing_cf57150() throws java.io.IOException {
        com.clearspring.analytics.stream.membership.BloomFilter f = null;
        org.junit.Assert.assertEquals(128, (f = new com.clearspring.analytics.stream.membership.BloomFilter(10, 0.05)).buckets());
        org.junit.Assert.assertEquals(93, serialize(f).length);
        org.junit.Assert.assertEquals(768, new com.clearspring.analytics.stream.membership.BloomFilter(100, 0.05).buckets());
        org.junit.Assert.assertEquals(7040, new com.clearspring.analytics.stream.membership.BloomFilter(1000, 0.05).buckets());
        org.junit.Assert.assertEquals(70080, new com.clearspring.analytics.stream.membership.BloomFilter(10000, 0.05).buckets());
        org.junit.Assert.assertEquals(700032, new com.clearspring.analytics.stream.membership.BloomFilter(100000, 0.05).buckets());
        org.junit.Assert.assertEquals(7000064, new com.clearspring.analytics.stream.membership.BloomFilter(1000000, 0.05).buckets());
        org.junit.Assert.assertEquals(128, new com.clearspring.analytics.stream.membership.BloomFilter(10, 0.01).buckets());
        org.junit.Assert.assertEquals(1024, new com.clearspring.analytics.stream.membership.BloomFilter(100, 0.01).buckets());
        org.junit.Assert.assertEquals(10048, (f = new com.clearspring.analytics.stream.membership.BloomFilter(1000, 0.01)).buckets());
        org.junit.Assert.assertEquals(1333, serialize(f).length);
        org.junit.Assert.assertEquals(100032, (f = new com.clearspring.analytics.stream.membership.BloomFilter(10000, 0.01)).buckets());
        org.junit.Assert.assertEquals(12581, serialize(f).length);
        org.junit.Assert.assertEquals(1000064, (f = new com.clearspring.analytics.stream.membership.BloomFilter(100000, 0.01)).buckets());
        org.junit.Assert.assertEquals(125085, serialize(f).length);
        org.junit.Assert.assertEquals(10000064, (f = new com.clearspring.analytics.stream.membership.BloomFilter(1000000, 0.01)).buckets());
        org.junit.Assert.assertEquals(1250085, serialize(f).length);
        for (java.lang.String s : new com.clearspring.analytics.stream.membership.KeyGenerator.RandomStringGenerator(new java.util.Random().nextInt(), 1000000)) {
            f.add(s);
        }
        org.junit.Assert.assertEquals(10000064, f.buckets());
        // StatementAdderOnAssert create null value
        com.clearspring.analytics.stream.membership.Filter[] vc_13042 = (com.clearspring.analytics.stream.membership.Filter[])null;
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(vc_13042);
        // AssertGenerator replace invocation
        com.clearspring.analytics.stream.membership.Filter o_testSizing_cf57150__64 = // StatementAdderMethod cloned existing statement
f.merge(vc_13042);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((com.clearspring.analytics.stream.membership.BloomFilter)o_testSizing_cf57150__64).buckets(), 10000064);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((com.clearspring.analytics.stream.membership.BloomFilter)o_testSizing_cf57150__64).getHashCount(), 5);
        org.junit.Assert.assertEquals(1250085, serialize(f).length);
    }

    /* amplification of com.clearspring.analytics.stream.membership.BloomFilterTest#testSizing */
    @org.junit.Test(timeout = 10000)
    public void testSizing_cf57121() throws java.io.IOException {
        com.clearspring.analytics.stream.membership.BloomFilter f = null;
        org.junit.Assert.assertEquals(128, (f = new com.clearspring.analytics.stream.membership.BloomFilter(10, 0.05)).buckets());
        org.junit.Assert.assertEquals(93, serialize(f).length);
        org.junit.Assert.assertEquals(768, new com.clearspring.analytics.stream.membership.BloomFilter(100, 0.05).buckets());
        org.junit.Assert.assertEquals(7040, new com.clearspring.analytics.stream.membership.BloomFilter(1000, 0.05).buckets());
        org.junit.Assert.assertEquals(70080, new com.clearspring.analytics.stream.membership.BloomFilter(10000, 0.05).buckets());
        org.junit.Assert.assertEquals(700032, new com.clearspring.analytics.stream.membership.BloomFilter(100000, 0.05).buckets());
        org.junit.Assert.assertEquals(7000064, new com.clearspring.analytics.stream.membership.BloomFilter(1000000, 0.05).buckets());
        org.junit.Assert.assertEquals(128, new com.clearspring.analytics.stream.membership.BloomFilter(10, 0.01).buckets());
        org.junit.Assert.assertEquals(1024, new com.clearspring.analytics.stream.membership.BloomFilter(100, 0.01).buckets());
        org.junit.Assert.assertEquals(10048, (f = new com.clearspring.analytics.stream.membership.BloomFilter(1000, 0.01)).buckets());
        org.junit.Assert.assertEquals(1333, serialize(f).length);
        org.junit.Assert.assertEquals(100032, (f = new com.clearspring.analytics.stream.membership.BloomFilter(10000, 0.01)).buckets());
        org.junit.Assert.assertEquals(12581, serialize(f).length);
        org.junit.Assert.assertEquals(1000064, (f = new com.clearspring.analytics.stream.membership.BloomFilter(100000, 0.01)).buckets());
        org.junit.Assert.assertEquals(125085, serialize(f).length);
        org.junit.Assert.assertEquals(10000064, (f = new com.clearspring.analytics.stream.membership.BloomFilter(1000000, 0.01)).buckets());
        org.junit.Assert.assertEquals(1250085, serialize(f).length);
        for (java.lang.String s : new com.clearspring.analytics.stream.membership.KeyGenerator.RandomStringGenerator(new java.util.Random().nextInt(), 1000000)) {
            f.add(s);
        }
        org.junit.Assert.assertEquals(10000064, f.buckets());
        // StatementAdderOnAssert create random local variable
        byte[] vc_13027 = new byte []{59,33,36};
        // AssertGenerator add assertion
        byte[] array_1528637260 = new byte[]{59, 33, 36};
	byte[] array_1529270037 = (byte[])vc_13027;
	for(int ii = 0; ii <array_1528637260.length; ii++) {
		org.junit.Assert.assertEquals(array_1528637260[ii], array_1529270037[ii]);
	};
        // StatementAddOnAssert local variable replacement
        com.clearspring.analytics.stream.membership.BloomFilter mergeBf = ((com.clearspring.analytics.stream.membership.BloomFilter) (bf2.merge(bf)));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((com.clearspring.analytics.stream.membership.BloomFilter)mergeBf).getHashCount(), 10);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((com.clearspring.analytics.stream.membership.BloomFilter)mergeBf).buckets(), 150080);
        // AssertGenerator replace invocation
        boolean o_testSizing_cf57121__67 = // StatementAdderMethod cloned existing statement
mergeBf.isPresent(vc_13027);
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_testSizing_cf57121__67);
        org.junit.Assert.assertEquals(1250085, serialize(f).length);
    }

    /* amplification of com.clearspring.analytics.stream.membership.BloomFilterTest#testSizing */
    @org.junit.Test(timeout = 10000)
    public void testSizing_cf57160_failAssert16_literalMutation61878() throws java.io.IOException {
        // AssertGenerator generate try/catch block with fail statement
        try {
            com.clearspring.analytics.stream.membership.BloomFilter f = null;
            // MethodAssertGenerator build local variable
            Object o_2_0 = (f = new com.clearspring.analytics.stream.membership.BloomFilter(10, 0.05)).buckets();
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(o_2_0, 128);
            // MethodAssertGenerator build local variable
            Object o_8_0 = new com.clearspring.analytics.stream.membership.BloomFilter(100, 0.05).buckets();
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(o_8_0, 768);
            // MethodAssertGenerator build local variable
            Object o_11_0 = new com.clearspring.analytics.stream.membership.BloomFilter(1000, 0.05).buckets();
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(o_11_0, 7040);
            // MethodAssertGenerator build local variable
            Object o_14_0 = new com.clearspring.analytics.stream.membership.BloomFilter(10000, 0.05).buckets();
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(o_14_0, 70080);
            // MethodAssertGenerator build local variable
            Object o_17_0 = new com.clearspring.analytics.stream.membership.BloomFilter(100000, 0.05).buckets();
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(o_17_0, 700032);
            // MethodAssertGenerator build local variable
            Object o_20_0 = new com.clearspring.analytics.stream.membership.BloomFilter(1000000, 0.05).buckets();
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(o_20_0, 7000064);
            // MethodAssertGenerator build local variable
            Object o_23_0 = new com.clearspring.analytics.stream.membership.BloomFilter(10, 0.01).buckets();
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(o_23_0, 128);
            // MethodAssertGenerator build local variable
            Object o_26_0 = new com.clearspring.analytics.stream.membership.BloomFilter(100, 0.01).buckets();
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(o_26_0, 1024);
            // MethodAssertGenerator build local variable
            Object o_29_0 = (f = new com.clearspring.analytics.stream.membership.BloomFilter(1000, 0.01)).buckets();
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(o_29_0, 10048);
            // MethodAssertGenerator build local variable
            Object o_35_0 = (f = new com.clearspring.analytics.stream.membership.BloomFilter(10000, 0.01)).buckets();
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(o_35_0, 100032);
            // MethodAssertGenerator build local variable
            Object o_41_0 = (f = new com.clearspring.analytics.stream.membership.BloomFilter(100000, 0.01)).buckets();
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(o_41_0, 1000064);
            // MethodAssertGenerator build local variable
            Object o_47_0 = (f = new com.clearspring.analytics.stream.membership.BloomFilter(1000000, 1.01)).buckets();
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(o_47_0, 1000064);
            for (java.lang.String s : new com.clearspring.analytics.stream.membership.KeyGenerator.RandomStringGenerator(new java.util.Random().nextInt(), 1000000)) {
                f.add(s);
            }
            // MethodAssertGenerator build local variable
            Object o_60_0 = f.buckets();
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(o_60_0, 1000064);
            // StatementAdderOnAssert create null value
            com.clearspring.analytics.stream.membership.BloomFilter vc_13046 = (com.clearspring.analytics.stream.membership.BloomFilter)null;
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(vc_13046);
            // StatementAdderMethod cloned existing statement
            vc_13046.tserializer();
            org.junit.Assert.fail("testSizing_cf57160 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    /* amplification of com.clearspring.analytics.stream.membership.BloomFilterTest#testSizing */
    @org.junit.Test(timeout = 10000)
    public void testSizing_cf57121_cf58414_failAssert78() throws java.io.IOException {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // MethodAssertGenerator build local variable
            Object o_71_1 = 150080;
            // MethodAssertGenerator build local variable
            Object o_69_1 = 10;
            com.clearspring.analytics.stream.membership.BloomFilter f = null;
            // MethodAssertGenerator build local variable
            Object o_2_0 = (f = new com.clearspring.analytics.stream.membership.BloomFilter(10, 0.05)).buckets();
            // MethodAssertGenerator build local variable
            Object o_8_0 = new com.clearspring.analytics.stream.membership.BloomFilter(100, 0.05).buckets();
            // MethodAssertGenerator build local variable
            Object o_11_0 = new com.clearspring.analytics.stream.membership.BloomFilter(1000, 0.05).buckets();
            // MethodAssertGenerator build local variable
            Object o_14_0 = new com.clearspring.analytics.stream.membership.BloomFilter(10000, 0.05).buckets();
            // MethodAssertGenerator build local variable
            Object o_17_0 = new com.clearspring.analytics.stream.membership.BloomFilter(100000, 0.05).buckets();
            // MethodAssertGenerator build local variable
            Object o_20_0 = new com.clearspring.analytics.stream.membership.BloomFilter(1000000, 0.05).buckets();
            // MethodAssertGenerator build local variable
            Object o_23_0 = new com.clearspring.analytics.stream.membership.BloomFilter(10, 0.01).buckets();
            // MethodAssertGenerator build local variable
            Object o_26_0 = new com.clearspring.analytics.stream.membership.BloomFilter(100, 0.01).buckets();
            // MethodAssertGenerator build local variable
            Object o_29_0 = (f = new com.clearspring.analytics.stream.membership.BloomFilter(1000, 0.01)).buckets();
            // MethodAssertGenerator build local variable
            Object o_35_0 = (f = new com.clearspring.analytics.stream.membership.BloomFilter(10000, 0.01)).buckets();
            // MethodAssertGenerator build local variable
            Object o_41_0 = (f = new com.clearspring.analytics.stream.membership.BloomFilter(100000, 0.01)).buckets();
            // MethodAssertGenerator build local variable
            Object o_47_0 = (f = new com.clearspring.analytics.stream.membership.BloomFilter(1000000, 0.01)).buckets();
            for (java.lang.String s : new com.clearspring.analytics.stream.membership.KeyGenerator.RandomStringGenerator(new java.util.Random().nextInt(), 1000000)) {
                f.add(s);
            }
            // MethodAssertGenerator build local variable
            Object o_60_0 = f.buckets();
            // StatementAdderOnAssert create random local variable
            byte[] vc_13027 = new byte []{59,33,36};
            // AssertGenerator add assertion
            byte[] array_1528637260 = new byte[]{59, 33, 36};
	byte[] array_1529270037 = (byte[])vc_13027;
	for(int ii = 0; ii <array_1528637260.length; ii++) {
		org.junit.Assert.assertEquals(array_1528637260[ii], array_1529270037[ii]);
	};
            // StatementAddOnAssert local variable replacement
            com.clearspring.analytics.stream.membership.BloomFilter mergeBf = ((com.clearspring.analytics.stream.membership.BloomFilter) (bf2.merge(bf)));
            // MethodAssertGenerator build local variable
            Object o_69_0 = ((com.clearspring.analytics.stream.membership.BloomFilter)mergeBf).getHashCount();
            // MethodAssertGenerator build local variable
            Object o_71_0 = ((com.clearspring.analytics.stream.membership.BloomFilter)mergeBf).buckets();
            // AssertGenerator replace invocation
            boolean o_testSizing_cf57121__67 = // StatementAdderMethod cloned existing statement
mergeBf.isPresent(vc_13027);
            // MethodAssertGenerator build local variable
            Object o_75_0 = o_testSizing_cf57121__67;
            // StatementAdderOnAssert create null value
            com.clearspring.analytics.stream.membership.BloomFilter vc_13228 = (com.clearspring.analytics.stream.membership.BloomFilter)null;
            // StatementAdderMethod cloned existing statement
            vc_13228.toString();
            org.junit.Assert.fail("testSizing_cf57121_cf58414 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    /* amplification of com.clearspring.analytics.stream.membership.BloomFilterTest#testSizing */
    @org.junit.Test(timeout = 10000)
    public void testSizing_cf57148_failAssert23_literalMutation61925() throws java.io.IOException {
        // AssertGenerator generate try/catch block with fail statement
        try {
            com.clearspring.analytics.stream.membership.BloomFilter f = null;
            // MethodAssertGenerator build local variable
            Object o_2_0 = (f = new com.clearspring.analytics.stream.membership.BloomFilter(10, 0.05)).buckets();
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(o_2_0, 128);
            // MethodAssertGenerator build local variable
            Object o_8_0 = new com.clearspring.analytics.stream.membership.BloomFilter(100, 0.05).buckets();
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(o_8_0, 768);
            // MethodAssertGenerator build local variable
            Object o_11_0 = new com.clearspring.analytics.stream.membership.BloomFilter(1000, 0.05).buckets();
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(o_11_0, 7040);
            // MethodAssertGenerator build local variable
            Object o_14_0 = new com.clearspring.analytics.stream.membership.BloomFilter(10000, -0.95).buckets();
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(o_14_0, 150080);
            // MethodAssertGenerator build local variable
            Object o_17_0 = new com.clearspring.analytics.stream.membership.BloomFilter(100000, 0.05).buckets();
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(o_17_0, 700032);
            // MethodAssertGenerator build local variable
            Object o_20_0 = new com.clearspring.analytics.stream.membership.BloomFilter(1000000, 0.05).buckets();
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(o_20_0, 7000064);
            // MethodAssertGenerator build local variable
            Object o_23_0 = new com.clearspring.analytics.stream.membership.BloomFilter(10, 0.01).buckets();
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(o_23_0, 128);
            // MethodAssertGenerator build local variable
            Object o_26_0 = new com.clearspring.analytics.stream.membership.BloomFilter(100, 0.01).buckets();
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(o_26_0, 1024);
            // MethodAssertGenerator build local variable
            Object o_29_0 = (f = new com.clearspring.analytics.stream.membership.BloomFilter(1000, 0.01)).buckets();
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(o_29_0, 10048);
            // MethodAssertGenerator build local variable
            Object o_35_0 = (f = new com.clearspring.analytics.stream.membership.BloomFilter(10000, 0.01)).buckets();
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(o_35_0, 100032);
            // MethodAssertGenerator build local variable
            Object o_41_0 = (f = new com.clearspring.analytics.stream.membership.BloomFilter(100000, 0.01)).buckets();
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(o_41_0, 1000064);
            // MethodAssertGenerator build local variable
            Object o_47_0 = (f = new com.clearspring.analytics.stream.membership.BloomFilter(1000000, 0.01)).buckets();
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(o_47_0, 10000064);
            for (java.lang.String s : new com.clearspring.analytics.stream.membership.KeyGenerator.RandomStringGenerator(new java.util.Random().nextInt(), 1000000)) {
                f.add(s);
            }
            // MethodAssertGenerator build local variable
            Object o_60_0 = f.buckets();
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(o_60_0, 10000064);
            // StatementAdderOnAssert create null value
            com.clearspring.analytics.stream.membership.Filter[] vc_13042 = (com.clearspring.analytics.stream.membership.Filter[])null;
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(vc_13042);
            // StatementAdderOnAssert create null value
            com.clearspring.analytics.stream.membership.BloomFilter vc_13040 = (com.clearspring.analytics.stream.membership.BloomFilter)null;
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(vc_13040);
            // StatementAdderMethod cloned existing statement
            vc_13040.merge(vc_13042);
            org.junit.Assert.fail("testSizing_cf57148 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    /* amplification of com.clearspring.analytics.stream.membership.BloomFilterTest#testSizing */
    @org.junit.Test(timeout = 10000)
    public void testSizing_cf57149_failAssert128_literalMutation63008() throws java.io.IOException {
        // AssertGenerator generate try/catch block with fail statement
        try {
            com.clearspring.analytics.stream.membership.BloomFilter f = null;
            // MethodAssertGenerator build local variable
            Object o_2_0 = (f = new com.clearspring.analytics.stream.membership.BloomFilter(10, 0.05)).buckets();
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(o_2_0, 128);
            // MethodAssertGenerator build local variable
            Object o_8_0 = new com.clearspring.analytics.stream.membership.BloomFilter(100, 0.05).buckets();
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(o_8_0, 768);
            // MethodAssertGenerator build local variable
            Object o_11_0 = new com.clearspring.analytics.stream.membership.BloomFilter(1000, 0.05).buckets();
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(o_11_0, 7040);
            // MethodAssertGenerator build local variable
            Object o_14_0 = new com.clearspring.analytics.stream.membership.BloomFilter(10000, 0.05).buckets();
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(o_14_0, 70080);
            // MethodAssertGenerator build local variable
            Object o_17_0 = new com.clearspring.analytics.stream.membership.BloomFilter(100000, 0.05).buckets();
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(o_17_0, 700032);
            // MethodAssertGenerator build local variable
            Object o_20_0 = new com.clearspring.analytics.stream.membership.BloomFilter(1000000, 0.05).buckets();
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(o_20_0, 7000064);
            // MethodAssertGenerator build local variable
            Object o_23_0 = new com.clearspring.analytics.stream.membership.BloomFilter(10, 0.01).buckets();
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(o_23_0, 128);
            // MethodAssertGenerator build local variable
            Object o_26_0 = new com.clearspring.analytics.stream.membership.BloomFilter(100, 0.01).buckets();
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(o_26_0, 1024);
            // MethodAssertGenerator build local variable
            Object o_29_0 = (f = new com.clearspring.analytics.stream.membership.BloomFilter(1000, 0.01)).buckets();
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(o_29_0, 10048);
            // MethodAssertGenerator build local variable
            Object o_35_0 = (f = new com.clearspring.analytics.stream.membership.BloomFilter(10000, 0.01)).buckets();
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(o_35_0, 100032);
            // MethodAssertGenerator build local variable
            Object o_41_0 = (f = new com.clearspring.analytics.stream.membership.BloomFilter(100000, 0.005)).buckets();
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(o_41_0, 1200064);
            // MethodAssertGenerator build local variable
            Object o_47_0 = (f = new com.clearspring.analytics.stream.membership.BloomFilter(1000000, 0.01)).buckets();
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(o_47_0, 10000064);
            for (java.lang.String s : new com.clearspring.analytics.stream.membership.KeyGenerator.RandomStringGenerator(new java.util.Random().nextInt(), 1000000)) {
                f.add(s);
            }
            // MethodAssertGenerator build local variable
            Object o_60_0 = f.buckets();
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(o_60_0, 10000064);
            // StatementAdderOnAssert create random local variable
            com.clearspring.analytics.stream.membership.Filter[] vc_13043 = new com.clearspring.analytics.stream.membership.Filter []{};
            // StatementAdderOnAssert create null value
            com.clearspring.analytics.stream.membership.BloomFilter vc_13040 = (com.clearspring.analytics.stream.membership.BloomFilter)null;
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(vc_13040);
            // StatementAdderMethod cloned existing statement
            vc_13040.merge(vc_13043);
            org.junit.Assert.fail("testSizing_cf57149 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    /* amplification of com.clearspring.analytics.stream.membership.BloomFilterTest#testSizing */
    @org.junit.Test(timeout = 10000)
    public void testSizing_cf57160_failAssert16_add61758() throws java.io.IOException {
        // AssertGenerator generate try/catch block with fail statement
        try {
            com.clearspring.analytics.stream.membership.BloomFilter f = null;
            // MethodAssertGenerator build local variable
            Object o_2_0 = (f = new com.clearspring.analytics.stream.membership.BloomFilter(10, 0.05)).buckets();
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(o_2_0, 128);
            // MethodAssertGenerator build local variable
            Object o_8_0 = new com.clearspring.analytics.stream.membership.BloomFilter(100, 0.05).buckets();
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(o_8_0, 768);
            // MethodAssertGenerator build local variable
            Object o_11_0 = new com.clearspring.analytics.stream.membership.BloomFilter(1000, 0.05).buckets();
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(o_11_0, 7040);
            // MethodAssertGenerator build local variable
            Object o_14_0 = new com.clearspring.analytics.stream.membership.BloomFilter(10000, 0.05).buckets();
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(o_14_0, 70080);
            // MethodAssertGenerator build local variable
            Object o_17_0 = new com.clearspring.analytics.stream.membership.BloomFilter(100000, 0.05).buckets();
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(o_17_0, 700032);
            // MethodAssertGenerator build local variable
            Object o_20_0 = new com.clearspring.analytics.stream.membership.BloomFilter(1000000, 0.05).buckets();
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(o_20_0, 7000064);
            // MethodAssertGenerator build local variable
            Object o_23_0 = new com.clearspring.analytics.stream.membership.BloomFilter(10, 0.01).buckets();
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(o_23_0, 128);
            // MethodAssertGenerator build local variable
            Object o_26_0 = new com.clearspring.analytics.stream.membership.BloomFilter(100, 0.01).buckets();
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(o_26_0, 1024);
            // MethodAssertGenerator build local variable
            Object o_29_0 = (f = new com.clearspring.analytics.stream.membership.BloomFilter(1000, 0.01)).buckets();
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(o_29_0, 10048);
            // MethodAssertGenerator build local variable
            Object o_35_0 = (f = new com.clearspring.analytics.stream.membership.BloomFilter(10000, 0.01)).buckets();
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(o_35_0, 100032);
            // MethodAssertGenerator build local variable
            Object o_41_0 = (f = new com.clearspring.analytics.stream.membership.BloomFilter(100000, 0.01)).buckets();
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(o_41_0, 1000064);
            // MethodAssertGenerator build local variable
            Object o_47_0 = (f = new com.clearspring.analytics.stream.membership.BloomFilter(1000000, 0.01)).buckets();
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(o_47_0, 10000064);
            for (java.lang.String s : new com.clearspring.analytics.stream.membership.KeyGenerator.RandomStringGenerator(new java.util.Random().nextInt(), 1000000)) {
                // MethodCallAdder
                f.add(s);
                // AssertGenerator add assertion
                org.junit.Assert.assertEquals(((com.clearspring.analytics.stream.membership.BloomFilter)f).getHashCount(), 5);
                // AssertGenerator add assertion
                org.junit.Assert.assertEquals(((com.clearspring.analytics.stream.membership.BloomFilter)f).buckets(), 10000064);
                f.add(s);
            }
            // MethodAssertGenerator build local variable
            Object o_60_0 = f.buckets();
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(o_60_0, 10000064);
            // StatementAdderOnAssert create null value
            com.clearspring.analytics.stream.membership.BloomFilter vc_13046 = (com.clearspring.analytics.stream.membership.BloomFilter)null;
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(vc_13046);
            // StatementAdderMethod cloned existing statement
            vc_13046.tserializer();
            org.junit.Assert.fail("testSizing_cf57160 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    /* amplification of com.clearspring.analytics.stream.membership.BloomFilterTest#testSizing */
    @org.junit.Test(timeout = 10000)
    public void testSizing_cf57176_failAssert132_literalMutation63053() throws java.io.IOException {
        // AssertGenerator generate try/catch block with fail statement
        try {
            com.clearspring.analytics.stream.membership.BloomFilter f = null;
            // MethodAssertGenerator build local variable
            Object o_2_0 = (f = new com.clearspring.analytics.stream.membership.BloomFilter(10, 0.05)).buckets();
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(o_2_0, 128);
            // MethodAssertGenerator build local variable
            Object o_8_0 = new com.clearspring.analytics.stream.membership.BloomFilter(100, 0.05).buckets();
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(o_8_0, 768);
            // MethodAssertGenerator build local variable
            Object o_11_0 = new com.clearspring.analytics.stream.membership.BloomFilter(500, 0.05).buckets();
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(o_11_0, 3520);
            // MethodAssertGenerator build local variable
            Object o_14_0 = new com.clearspring.analytics.stream.membership.BloomFilter(10000, 0.05).buckets();
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(o_14_0, 70080);
            // MethodAssertGenerator build local variable
            Object o_17_0 = new com.clearspring.analytics.stream.membership.BloomFilter(100000, 0.05).buckets();
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(o_17_0, 700032);
            // MethodAssertGenerator build local variable
            Object o_20_0 = new com.clearspring.analytics.stream.membership.BloomFilter(1000000, 0.05).buckets();
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(o_20_0, 7000064);
            // MethodAssertGenerator build local variable
            Object o_23_0 = new com.clearspring.analytics.stream.membership.BloomFilter(10, 0.01).buckets();
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(o_23_0, 128);
            // MethodAssertGenerator build local variable
            Object o_26_0 = new com.clearspring.analytics.stream.membership.BloomFilter(100, 0.01).buckets();
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(o_26_0, 1024);
            // MethodAssertGenerator build local variable
            Object o_29_0 = (f = new com.clearspring.analytics.stream.membership.BloomFilter(1000, 0.01)).buckets();
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(o_29_0, 10048);
            // MethodAssertGenerator build local variable
            Object o_35_0 = (f = new com.clearspring.analytics.stream.membership.BloomFilter(10000, 0.01)).buckets();
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(o_35_0, 100032);
            // MethodAssertGenerator build local variable
            Object o_41_0 = (f = new com.clearspring.analytics.stream.membership.BloomFilter(100000, 0.01)).buckets();
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(o_41_0, 1000064);
            // MethodAssertGenerator build local variable
            Object o_47_0 = (f = new com.clearspring.analytics.stream.membership.BloomFilter(1000000, 0.01)).buckets();
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(o_47_0, 10000064);
            for (java.lang.String s : new com.clearspring.analytics.stream.membership.KeyGenerator.RandomStringGenerator(new java.util.Random().nextInt(), 1000000)) {
                f.add(s);
            }
            // MethodAssertGenerator build local variable
            Object o_60_0 = f.buckets();
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(o_60_0, 10000064);
            // StatementAdderOnAssert create null value
            com.clearspring.analytics.stream.membership.BloomFilter vc_13054 = (com.clearspring.analytics.stream.membership.BloomFilter)null;
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(vc_13054);
            // StatementAdderMethod cloned existing statement
            vc_13054.filter();
            org.junit.Assert.fail("testSizing_cf57176 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    /* amplification of com.clearspring.analytics.stream.membership.BloomFilterTest#testSizing */
    @org.junit.Test(timeout = 10000)
    public void testSizing_cf57201_cf57492_failAssert20_literalMutation78094() throws java.io.IOException {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // MethodAssertGenerator build local variable
            Object o_66_1 = 10000064;
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(o_66_1, 10000064);
            // MethodAssertGenerator build local variable
            Object o_64_1 = 5;
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(o_64_1, 5);
            com.clearspring.analytics.stream.membership.BloomFilter f = null;
            // MethodAssertGenerator build local variable
            Object o_2_0 = (f = new com.clearspring.analytics.stream.membership.BloomFilter(10, 0.05)).buckets();
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(o_2_0, 128);
            // MethodAssertGenerator build local variable
            Object o_8_0 = new com.clearspring.analytics.stream.membership.BloomFilter(100, 1.05).buckets();
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(o_8_0, 128);
            // MethodAssertGenerator build local variable
            Object o_11_0 = new com.clearspring.analytics.stream.membership.BloomFilter(1000, 0.05).buckets();
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(o_11_0, 7040);
            // MethodAssertGenerator build local variable
            Object o_14_0 = new com.clearspring.analytics.stream.membership.BloomFilter(10000, 0.05).buckets();
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(o_14_0, 70080);
            // MethodAssertGenerator build local variable
            Object o_17_0 = new com.clearspring.analytics.stream.membership.BloomFilter(100000, 0.05).buckets();
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(o_17_0, 700032);
            // MethodAssertGenerator build local variable
            Object o_20_0 = new com.clearspring.analytics.stream.membership.BloomFilter(1000000, 0.05).buckets();
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(o_20_0, 7000064);
            // MethodAssertGenerator build local variable
            Object o_23_0 = new com.clearspring.analytics.stream.membership.BloomFilter(10, 0.01).buckets();
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(o_23_0, 128);
            // MethodAssertGenerator build local variable
            Object o_26_0 = new com.clearspring.analytics.stream.membership.BloomFilter(100, 0.01).buckets();
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(o_26_0, 1024);
            // MethodAssertGenerator build local variable
            Object o_29_0 = (f = new com.clearspring.analytics.stream.membership.BloomFilter(1000, 0.01)).buckets();
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(o_29_0, 10048);
            // MethodAssertGenerator build local variable
            Object o_35_0 = (f = new com.clearspring.analytics.stream.membership.BloomFilter(10000, 0.01)).buckets();
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(o_35_0, 100032);
            // MethodAssertGenerator build local variable
            Object o_41_0 = (f = new com.clearspring.analytics.stream.membership.BloomFilter(100000, 0.01)).buckets();
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(o_41_0, 1000064);
            // MethodAssertGenerator build local variable
            Object o_47_0 = (f = new com.clearspring.analytics.stream.membership.BloomFilter(1000000, 0.01)).buckets();
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(o_47_0, 10000064);
            for (java.lang.String s : new com.clearspring.analytics.stream.membership.KeyGenerator.RandomStringGenerator(new java.util.Random().nextInt(), 1000000)) {
                f.add(s);
            }
            // MethodAssertGenerator build local variable
            Object o_60_0 = f.buckets();
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(o_60_0, 10000064);
            // StatementAdderMethod cloned existing statement
            f.clear();
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((com.clearspring.analytics.stream.membership.BloomFilter)f).getHashCount(), 5);
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((com.clearspring.analytics.stream.membership.BloomFilter)f).buckets(), 10000064);
            // MethodAssertGenerator build local variable
            Object o_64_0 = ((com.clearspring.analytics.stream.membership.BloomFilter)f).getHashCount();
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(o_64_0, 5);
            // MethodAssertGenerator build local variable
            Object o_66_0 = ((com.clearspring.analytics.stream.membership.BloomFilter)f).buckets();
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(o_66_0, 10000064);
            // StatementAdderOnAssert create null value
            byte[] vc_13102 = (byte[])null;
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(vc_13102);
            // StatementAdderMethod cloned existing statement
            f.add(vc_13102);
            org.junit.Assert.fail("testSizing_cf57201_cf57492 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    /* amplification of com.clearspring.analytics.stream.membership.BloomFilterTest#testSizing */
    @org.junit.Test(timeout = 10000)
    public void testSizing_cf57150_cf58718_failAssert3_literalMutation77861() throws java.io.IOException {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // MethodAssertGenerator build local variable
            Object o_70_1 = 5;
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(o_70_1, 5);
            // MethodAssertGenerator build local variable
            Object o_68_1 = 10000064;
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(o_68_1, 10000064);
            com.clearspring.analytics.stream.membership.BloomFilter f = null;
            // MethodAssertGenerator build local variable
            Object o_2_0 = (f = new com.clearspring.analytics.stream.membership.BloomFilter(10, 0.05)).buckets();
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(o_2_0, 128);
            // MethodAssertGenerator build local variable
            Object o_8_0 = new com.clearspring.analytics.stream.membership.BloomFilter(100, 0.05).buckets();
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(o_8_0, 768);
            // MethodAssertGenerator build local variable
            Object o_11_0 = new com.clearspring.analytics.stream.membership.BloomFilter(1000, 0.05).buckets();
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(o_11_0, 7040);
            // MethodAssertGenerator build local variable
            Object o_14_0 = new com.clearspring.analytics.stream.membership.BloomFilter(10000, 0.05).buckets();
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(o_14_0, 70080);
            // MethodAssertGenerator build local variable
            Object o_17_0 = new com.clearspring.analytics.stream.membership.BloomFilter(100001, 0.05).buckets();
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(o_17_0, 700032);
            // MethodAssertGenerator build local variable
            Object o_20_0 = new com.clearspring.analytics.stream.membership.BloomFilter(1000000, 0.05).buckets();
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(o_20_0, 7000064);
            // MethodAssertGenerator build local variable
            Object o_23_0 = new com.clearspring.analytics.stream.membership.BloomFilter(10, 0.01).buckets();
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(o_23_0, 128);
            // MethodAssertGenerator build local variable
            Object o_26_0 = new com.clearspring.analytics.stream.membership.BloomFilter(100, 0.01).buckets();
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(o_26_0, 1024);
            // MethodAssertGenerator build local variable
            Object o_29_0 = (f = new com.clearspring.analytics.stream.membership.BloomFilter(1000, 0.01)).buckets();
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(o_29_0, 10048);
            // MethodAssertGenerator build local variable
            Object o_35_0 = (f = new com.clearspring.analytics.stream.membership.BloomFilter(10000, 0.01)).buckets();
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(o_35_0, 100032);
            // MethodAssertGenerator build local variable
            Object o_41_0 = (f = new com.clearspring.analytics.stream.membership.BloomFilter(100000, 0.01)).buckets();
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(o_41_0, 1000064);
            // MethodAssertGenerator build local variable
            Object o_47_0 = (f = new com.clearspring.analytics.stream.membership.BloomFilter(1000000, 0.01)).buckets();
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(o_47_0, 10000064);
            for (java.lang.String s : new com.clearspring.analytics.stream.membership.KeyGenerator.RandomStringGenerator(new java.util.Random().nextInt(), 1000000)) {
                f.add(s);
            }
            // MethodAssertGenerator build local variable
            Object o_60_0 = f.buckets();
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(o_60_0, 10000064);
            // StatementAdderOnAssert create null value
            com.clearspring.analytics.stream.membership.Filter[] vc_13042 = (com.clearspring.analytics.stream.membership.Filter[])null;
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(vc_13042);
            // MethodAssertGenerator build local variable
            Object o_64_0 = vc_13042;
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(o_64_0);
            // AssertGenerator replace invocation
            com.clearspring.analytics.stream.membership.Filter o_testSizing_cf57150__64 = // StatementAdderMethod cloned existing statement
f.merge(vc_13042);
            // MethodAssertGenerator build local variable
            Object o_68_0 = ((com.clearspring.analytics.stream.membership.BloomFilter)o_testSizing_cf57150__64).buckets();
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(o_68_0, 10000064);
            // MethodAssertGenerator build local variable
            Object o_70_0 = ((com.clearspring.analytics.stream.membership.BloomFilter)o_testSizing_cf57150__64).getHashCount();
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(o_70_0, 5);
            // StatementAdderOnAssert create null value
            com.clearspring.analytics.stream.membership.BloomFilter vc_13270 = (com.clearspring.analytics.stream.membership.BloomFilter)null;
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(vc_13270);
            // StatementAdderMethod cloned existing statement
            vc_13270.emptyBuckets();
            org.junit.Assert.fail("testSizing_cf57150_cf58718 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    /* amplification of com.clearspring.analytics.stream.membership.BloomFilterTest#testSizing */
    @org.junit.Test(timeout = 10000)
    public void testSizing_cf57143_cf60523_failAssert86_literalMutation79143() throws java.io.IOException {
        // AssertGenerator generate try/catch block with fail statement
        try {
            com.clearspring.analytics.stream.membership.BloomFilter f = null;
            // MethodAssertGenerator build local variable
            Object o_2_0 = (f = new com.clearspring.analytics.stream.membership.BloomFilter(10, 0.05)).buckets();
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(o_2_0, 128);
            // MethodAssertGenerator build local variable
            Object o_8_0 = new com.clearspring.analytics.stream.membership.BloomFilter(100, 0.05).buckets();
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(o_8_0, 768);
            // MethodAssertGenerator build local variable
            Object o_11_0 = new com.clearspring.analytics.stream.membership.BloomFilter(1000, 0.05).buckets();
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(o_11_0, 7040);
            // MethodAssertGenerator build local variable
            Object o_14_0 = new com.clearspring.analytics.stream.membership.BloomFilter(10000, 0.05).buckets();
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(o_14_0, 70080);
            // MethodAssertGenerator build local variable
            Object o_17_0 = new com.clearspring.analytics.stream.membership.BloomFilter(100000, 0.05).buckets();
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(o_17_0, 700032);
            // MethodAssertGenerator build local variable
            Object o_20_0 = new com.clearspring.analytics.stream.membership.BloomFilter(999999, 0.05).buckets();
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(o_20_0, 7000064);
            // MethodAssertGenerator build local variable
            Object o_23_0 = new com.clearspring.analytics.stream.membership.BloomFilter(10, 0.01).buckets();
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(o_23_0, 128);
            // MethodAssertGenerator build local variable
            Object o_26_0 = new com.clearspring.analytics.stream.membership.BloomFilter(100, 0.01).buckets();
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(o_26_0, 1024);
            // MethodAssertGenerator build local variable
            Object o_29_0 = (f = new com.clearspring.analytics.stream.membership.BloomFilter(1000, 0.01)).buckets();
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(o_29_0, 10048);
            // MethodAssertGenerator build local variable
            Object o_35_0 = (f = new com.clearspring.analytics.stream.membership.BloomFilter(10000, 0.01)).buckets();
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(o_35_0, 100032);
            // MethodAssertGenerator build local variable
            Object o_41_0 = (f = new com.clearspring.analytics.stream.membership.BloomFilter(100000, 0.01)).buckets();
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(o_41_0, 1000064);
            // MethodAssertGenerator build local variable
            Object o_47_0 = (f = new com.clearspring.analytics.stream.membership.BloomFilter(1000000, 0.01)).buckets();
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(o_47_0, 10000064);
            for (java.lang.String s : new com.clearspring.analytics.stream.membership.KeyGenerator.RandomStringGenerator(new java.util.Random().nextInt(), 1000000)) {
                f.add(s);
            }
            // MethodAssertGenerator build local variable
            Object o_60_0 = f.buckets();
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(o_60_0, 10000064);
            // StatementAdderOnAssert create random local variable
            byte[] vc_13039 = new byte []{8};
            // AssertGenerator add assertion
            byte[] array_8912359 = new byte[]{8};
	byte[] array_240794485 = (byte[])vc_13039;
	for(int ii = 0; ii <array_8912359.length; ii++) {
		org.junit.Assert.assertEquals(array_8912359[ii], array_240794485[ii]);
	};
            // AssertGenerator add assertion
            byte[] array_1573133690 = new byte[]{8};
	byte[] array_1310775977 = (byte[])vc_13039;
	for(int ii = 0; ii <array_1573133690.length; ii++) {
		org.junit.Assert.assertEquals(array_1573133690[ii], array_1310775977[ii]);
	};
            // AssertGenerator replace invocation
            com.clearspring.analytics.stream.membership.BloomFilter o_testSizing_cf57143__64 = // StatementAdderMethod cloned existing statement
f.deserialize(vc_13039);
            // MethodAssertGenerator build local variable
            Object o_68_0 = o_testSizing_cf57143__64;
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(o_68_0);
            // StatementAdderOnAssert create null value
            java.lang.String vc_13514 = (java.lang.String)null;
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(vc_13514);
            // StatementAddOnAssert local variable replacement
            com.clearspring.analytics.stream.membership.BloomFilter mergeBf = ((com.clearspring.analytics.stream.membership.BloomFilter) (bf2.merge(bf)));
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((com.clearspring.analytics.stream.membership.BloomFilter)mergeBf).getHashCount(), 10);
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((com.clearspring.analytics.stream.membership.BloomFilter)mergeBf).buckets(), 150080);
            // StatementAdderMethod cloned existing statement
            mergeBf.isPresent(vc_13514);
            org.junit.Assert.fail("testSizing_cf57143_cf60523 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    /* amplification of com.clearspring.analytics.stream.membership.BloomFilterTest#testSizing */
    @org.junit.Test(timeout = 10000)
    public void testSizing_cf57138_cf60228_failAssert44_literalMutation78444() throws java.io.IOException {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // MethodAssertGenerator build local variable
            Object o_73_1 = 1;
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(o_73_1, 1);
            // MethodAssertGenerator build local variable
            Object o_71_1 = 64;
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(o_71_1, 64);
            // MethodAssertGenerator build local variable
            Object o_67_1 = 150080;
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(o_67_1, 150080);
            // MethodAssertGenerator build local variable
            Object o_65_1 = 10;
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(o_65_1, 10);
            com.clearspring.analytics.stream.membership.BloomFilter f = null;
            // MethodAssertGenerator build local variable
            Object o_2_0 = (f = new com.clearspring.analytics.stream.membership.BloomFilter(10, 0.05)).buckets();
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(o_2_0, 128);
            // MethodAssertGenerator build local variable
            Object o_8_0 = new com.clearspring.analytics.stream.membership.BloomFilter(100, 0.05).buckets();
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(o_8_0, 768);
            // MethodAssertGenerator build local variable
            Object o_11_0 = new com.clearspring.analytics.stream.membership.BloomFilter(1000, 0.05).buckets();
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(o_11_0, 7040);
            // MethodAssertGenerator build local variable
            Object o_14_0 = new com.clearspring.analytics.stream.membership.BloomFilter(10000, 0.05).buckets();
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(o_14_0, 70080);
            // MethodAssertGenerator build local variable
            Object o_17_0 = new com.clearspring.analytics.stream.membership.BloomFilter(100000, 0.05).buckets();
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(o_17_0, 700032);
            // MethodAssertGenerator build local variable
            Object o_20_0 = new com.clearspring.analytics.stream.membership.BloomFilter(1000000, 0.05).buckets();
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(o_20_0, 7000064);
            // MethodAssertGenerator build local variable
            Object o_23_0 = new com.clearspring.analytics.stream.membership.BloomFilter(10, 0.01).buckets();
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(o_23_0, 128);
            // MethodAssertGenerator build local variable
            Object o_26_0 = new com.clearspring.analytics.stream.membership.BloomFilter(100, 0.01).buckets();
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(o_26_0, 1024);
            // MethodAssertGenerator build local variable
            Object o_29_0 = (f = new com.clearspring.analytics.stream.membership.BloomFilter(1000, 0.01)).buckets();
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(o_29_0, 10048);
            // MethodAssertGenerator build local variable
            Object o_35_0 = (f = new com.clearspring.analytics.stream.membership.BloomFilter(10000, 0.01)).buckets();
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(o_35_0, 100032);
            // MethodAssertGenerator build local variable
            Object o_41_0 = (f = new com.clearspring.analytics.stream.membership.BloomFilter(100000, 0.01)).buckets();
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(o_41_0, 1000064);
            // MethodAssertGenerator build local variable
            Object o_47_0 = (f = new com.clearspring.analytics.stream.membership.BloomFilter(1000000, 0.005)).buckets();
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(o_47_0, 12000064);
            for (java.lang.String s : new com.clearspring.analytics.stream.membership.KeyGenerator.RandomStringGenerator(new java.util.Random().nextInt(), 1000000)) {
                f.add(s);
            }
            // MethodAssertGenerator build local variable
            Object o_60_0 = f.buckets();
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(o_60_0, 12000064);
            // StatementAddOnAssert local variable replacement
            com.clearspring.analytics.stream.membership.BloomFilter mergeBf = ((com.clearspring.analytics.stream.membership.BloomFilter) (bf2.merge(bf)));
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((com.clearspring.analytics.stream.membership.BloomFilter)mergeBf).getHashCount(), 10);
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((com.clearspring.analytics.stream.membership.BloomFilter)mergeBf).buckets(), 150080);
            // MethodAssertGenerator build local variable
            Object o_65_0 = ((com.clearspring.analytics.stream.membership.BloomFilter)mergeBf).getHashCount();
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(o_65_0, 10);
            // MethodAssertGenerator build local variable
            Object o_67_0 = ((com.clearspring.analytics.stream.membership.BloomFilter)mergeBf).buckets();
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(o_67_0, 150080);
            // AssertGenerator replace invocation
            com.clearspring.analytics.stream.membership.BloomFilter o_testSizing_cf57138__65 = // StatementAdderMethod cloned existing statement
mergeBf.alwaysMatchingBloomFilter();
            // MethodAssertGenerator build local variable
            Object o_71_0 = ((com.clearspring.analytics.stream.membership.BloomFilter)o_testSizing_cf57138__65).buckets();
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(o_71_0, 64);
            // MethodAssertGenerator build local variable
            Object o_73_0 = ((com.clearspring.analytics.stream.membership.BloomFilter)o_testSizing_cf57138__65).getHashCount();
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(o_73_0, 1);
            // StatementAdderOnAssert create null value
            java.lang.String vc_13470 = (java.lang.String)null;
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(vc_13470);
            // StatementAdderMethod cloned existing statement
            f.isPresent(vc_13470);
            org.junit.Assert.fail("testSizing_cf57138_cf60228 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    /* amplification of com.clearspring.analytics.stream.membership.BloomFilterTest#testWords */
    /* amplification of com.clearspring.analytics.stream.membership.BloomFilterTest#testWords_literalMutation79352 */
    @org.junit.Test
    public void testWords_literalMutation79352_failAssert1_literalMutation79388_failAssert1() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // AssertGenerator generate try/catch block with fail statement
            try {
                if ((com.clearspring.analytics.stream.membership.KeyGenerator.WordGenerator.WORDS) == 0) {
                    return ;
                }
                com.clearspring.analytics.stream.membership.BloomFilter bf2 = new com.clearspring.analytics.stream.membership.BloomFilter(((com.clearspring.analytics.stream.membership.KeyGenerator.WordGenerator.WORDS) / -1), com.clearspring.analytics.stream.membership.FilterTest.spec.bucketsPerElement);
                int skipEven = (((com.clearspring.analytics.stream.membership.KeyGenerator.WordGenerator.WORDS) % 2) == 0) ? 0 : 2;
                com.clearspring.analytics.stream.membership.FilterTest.testFalsePositives(bf2, new com.clearspring.analytics.stream.membership.KeyGenerator.WordGenerator(skipEven, 2), new com.clearspring.analytics.stream.membership.KeyGenerator.WordGenerator(1, 2));
                org.junit.Assert.fail("testWords_literalMutation79352 should have thrown ArithmeticException");
            } catch (java.lang.ArithmeticException eee) {
            }
            org.junit.Assert.fail("testWords_literalMutation79352_failAssert1_literalMutation79388 should have thrown NegativeArraySizeException");
        } catch (java.lang.NegativeArraySizeException eee) {
        }
    }
}

