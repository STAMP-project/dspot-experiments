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


import BloomCalculations.BloomSpecification;
import BloomCalculations.probs;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import org.junit.Assert;
import org.junit.Test;

import static BloomCalculations.probs;
import static com.clearspring.analytics.stream.membership.KeyGenerator.WordGenerator.WORDS;


public class BloomFilterTest {
    public BloomFilter bf;

    public BloomFilter bf2;

    public BloomSpecification spec = BloomCalculations.computeBucketsAndK(1.0E-4);

    static final int ELEMENTS = 10000;

    public BloomFilterTest() {
        bf = new BloomFilter(BloomFilterTest.ELEMENTS, spec.bucketsPerElement);
        bf2 = new BloomFilter(BloomFilterTest.ELEMENTS, spec.bucketsPerElement);
        Assert.assertNotNull(bf);
    }

    @Test
    public void testOne() {
        bf.add("a");
        Assert.assertTrue(bf.isPresent("a"));
        Assert.assertFalse(bf.isPresent("b"));
    }

    @Test
    public void testMerge() {
        bf.add("a");
        bf2.add("c");
        BloomFilter[] bfs = new BloomFilter[1];
        bfs[0] = bf;
        BloomFilter mergeBf = ((BloomFilter) (bf2.merge(bf)));
        Assert.assertTrue(mergeBf.isPresent("a"));
        Assert.assertFalse(mergeBf.isPresent("b"));
        Assert.assertTrue(mergeBf.isPresent("c"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testMergeException() {
        BloomFilter bf3 = new BloomFilter(((BloomFilterTest.ELEMENTS) * 10), 1);
        BloomFilter[] bfs = new BloomFilter[1];
        bfs[0] = bf;
        BloomFilter mergeBf = ((BloomFilter) (bf3.merge(bf)));
    }

    @Test
    public void testFalsePositivesInt() {
        FilterTest.testFalsePositives(bf, FilterTest.intKeys(), FilterTest.randomKeys2());
    }

    @Test
    public void testFalsePositivesRandom() {
        FilterTest.testFalsePositives(bf, FilterTest.randomKeys(), FilterTest.randomKeys2());
    }

    @Test
    public void testWords() {
        if ((WORDS) == 0) {
            return;
        }
        BloomFilter bf2 = new BloomFilter(((WORDS) / 2), FilterTest.spec.bucketsPerElement);
        int skipEven = (((WORDS) % 2) == 0) ? 0 : 2;
        FilterTest.testFalsePositives(bf2, new KeyGenerator.WordGenerator(skipEven, 2), new KeyGenerator.WordGenerator(1, 2));
    }

    @Test
    public void testSerialize() throws IOException {
        FilterTest.testSerialize(bf);
    }

    @Test
    public void testGetFalsePositiveProbability() {
        // These probabilities are taken from the bloom filter probability table at
        // http://pages.cs.wisc.edu/~cao/papers/summary-cache/node8.html
        System.out.println("expectedFalsePositiveProbability");
        BloomFilter instance = new BloomFilter(100, 10);
        double expResult = 0.00819;// m/n=10, k=7

        double result = BloomCalculations.getFalsePositiveProbability(10, 7);
        Assert.assertEquals(7, instance.getHashCount());
        Assert.assertEquals(expResult, result, 9.0E-6);
        instance = new BloomFilter(10, 10);
        expResult = 0.00819;// m/n=10, k=7

        result = BloomCalculations.getFalsePositiveProbability(10, 7);
        Assert.assertEquals(7, instance.getHashCount());
        Assert.assertEquals(expResult, result, 9.0E-6);
        instance = new BloomFilter(10, 2);
        expResult = 0.393;// m/n=2, k=1

        result = BloomCalculations.getFalsePositiveProbability(2, 1);
        Assert.assertEquals(instance.getHashCount(), 1);
        Assert.assertEquals(expResult, result, 5.0E-4);
        instance = new BloomFilter(10, 11);
        expResult = 0.00509;// m/n=11, k=8

        result = BloomCalculations.getFalsePositiveProbability(11, 8);
        Assert.assertEquals(8, instance.getHashCount());
        Assert.assertEquals(expResult, result, 1.0E-5);
    }

    /**
     * Test error rate
     *
     * @throws UnsupportedEncodingException
     * 		
     */
    @Test
    public void testFalsePositiveRate() throws UnsupportedEncodingException {
        // Numbers are from // http://pages.cs.wisc.edu/~cao/papers/summary-cache/node8.html
        for (int j = 10; j < 21; j++) {
            System.out.print(((j - 9) + "/11"));
            Set<String> v = new HashSet<String>();
            BloomFilter instance = new BloomFilter(100, j);
            for (int i = 0; i < 100; i++) {
                String key = UUID.randomUUID().toString();
                v.add(key);
                instance.add(key);
            }
            long r = 0;
            double tests = 100000;
            for (int i = 0; i < tests; i++) {
                String s = UUID.randomUUID().toString();
                if (instance.isPresent(s)) {
                    if (!(v.contains(s))) {
                        r++;
                    }
                }
            }
            double ratio = r / tests;
            double expectedFalsePositiveProbability = (j < (probs.length)) ? probs[j][instance.getHashCount()] : BloomCalculations.getFalsePositiveProbability(j, instance.getHashCount());
            System.out.println((((" - got " + ratio) + ", math says ") + expectedFalsePositiveProbability));
            Assert.assertEquals(expectedFalsePositiveProbability, ratio, 0.01);
        }
    }

    /**
     * Test for correct k *
     */
    @Test
    public void testHashCount() {
        // Numbers are from http://pages.cs.wisc.edu/~cao/papers/summary-cache/node8.html
        BloomFilter instance = null;
        instance = new BloomFilter(1, 2);
        Assert.assertEquals(1, instance.getHashCount());
        instance = new BloomFilter(1, 3);
        Assert.assertEquals(2, instance.getHashCount());
        instance = new BloomFilter(1, 4);
        Assert.assertEquals(3, instance.getHashCount());
        instance = new BloomFilter(1, 5);
        Assert.assertEquals(3, instance.getHashCount());
        instance = new BloomFilter(1, 6);
        Assert.assertEquals(4, instance.getHashCount());
        instance = new BloomFilter(1, 7);
        Assert.assertEquals(5, instance.getHashCount());
        /* Although technically 8*ln(2) = 5.545...
        we round down here for speed
         */
        instance = new BloomFilter(1, 8);
        Assert.assertEquals(5, instance.getHashCount());
        instance = new BloomFilter(1, 9);
        Assert.assertEquals(6, instance.getHashCount());
        instance = new BloomFilter(1, 10);
        Assert.assertEquals(7, instance.getHashCount());
        instance = new BloomFilter(1, 11);
        Assert.assertEquals(8, instance.getHashCount());
        instance = new BloomFilter(1, 12);
        Assert.assertEquals(8, instance.getHashCount());
    }

    @Test
    public void testSizing() throws IOException {
        BloomFilter f = null;
        Assert.assertEquals(128, (f = new BloomFilter(10, 0.05)).buckets());
        Assert.assertEquals(93, serialize(f).length);
        Assert.assertEquals(768, new BloomFilter(100, 0.05).buckets());
        Assert.assertEquals(7040, new BloomFilter(1000, 0.05).buckets());
        Assert.assertEquals(70080, new BloomFilter(10000, 0.05).buckets());
        Assert.assertEquals(700032, new BloomFilter(100000, 0.05).buckets());
        Assert.assertEquals(7000064, new BloomFilter(1000000, 0.05).buckets());
        Assert.assertEquals(128, new BloomFilter(10, 0.01).buckets());
        Assert.assertEquals(1024, new BloomFilter(100, 0.01).buckets());
        Assert.assertEquals(10048, (f = new BloomFilter(1000, 0.01)).buckets());
        Assert.assertEquals(1333, serialize(f).length);
        Assert.assertEquals(100032, (f = new BloomFilter(10000, 0.01)).buckets());
        Assert.assertEquals(12581, serialize(f).length);
        Assert.assertEquals(1000064, (f = new BloomFilter(100000, 0.01)).buckets());
        Assert.assertEquals(125085, serialize(f).length);
        Assert.assertEquals(10000064, (f = new BloomFilter(1000000, 0.01)).buckets());
        Assert.assertEquals(1250085, serialize(f).length);
        for (String s : new KeyGenerator.RandomStringGenerator(new Random().nextInt(), 1000000)) {
            f.add(s);
        }
        Assert.assertEquals(10000064, f.buckets());
        Assert.assertEquals(1250085, serialize(f).length);
    }
}

