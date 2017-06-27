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


public class AmplFilterTest {
    public void testManyHashes(java.util.Iterator<java.lang.String> keys) {
        int MAX_HASH_COUNT = 128;
        java.util.Set<java.lang.Integer> hashes = new java.util.HashSet<java.lang.Integer>();
        int collisions = 0;
        while (keys.hasNext()) {
            hashes.clear();
            for (int hashIndex : com.clearspring.analytics.stream.membership.Filter.getHashBuckets(keys.next(), MAX_HASH_COUNT, (1024 * 1024))) {
                hashes.add(hashIndex);
            }
            collisions += MAX_HASH_COUNT - (hashes.size());
        } 
        org.junit.Assert.assertTrue(("Collisions: " + collisions), (collisions <= 100));
    }

    @org.junit.Test
    public void testManyRandom() {
        testManyHashes(com.clearspring.analytics.stream.membership.AmplFilterTest.randomKeys());
    }

    // used by filter subclass tests
    static final double MAX_FAILURE_RATE = 0.1;

    public static final com.clearspring.analytics.stream.membership.BloomCalculations.BloomSpecification spec = com.clearspring.analytics.stream.membership.BloomCalculations.computeBucketsAndK(com.clearspring.analytics.stream.membership.AmplFilterTest.MAX_FAILURE_RATE);

    static final int ELEMENTS = 10000;

    static final com.clearspring.analytics.stream.membership.ResetableIterator<java.lang.String> intKeys() {
        return new com.clearspring.analytics.stream.membership.KeyGenerator.IntGenerator(com.clearspring.analytics.stream.membership.AmplFilterTest.ELEMENTS);
    }

    static final com.clearspring.analytics.stream.membership.ResetableIterator<java.lang.String> randomKeys() {
        return new com.clearspring.analytics.stream.membership.KeyGenerator.RandomStringGenerator(314159, com.clearspring.analytics.stream.membership.AmplFilterTest.ELEMENTS);
    }

    static final com.clearspring.analytics.stream.membership.ResetableIterator<java.lang.String> randomKeys2() {
        return new com.clearspring.analytics.stream.membership.KeyGenerator.RandomStringGenerator(271828, com.clearspring.analytics.stream.membership.AmplFilterTest.ELEMENTS);
    }

    public static void testFalsePositives(com.clearspring.analytics.stream.membership.Filter f, com.clearspring.analytics.stream.membership.ResetableIterator<java.lang.String> keys, com.clearspring.analytics.stream.membership.ResetableIterator<java.lang.String> otherkeys) {
        org.junit.Assert.assertEquals(keys.size(), otherkeys.size());
        while (keys.hasNext()) {
            f.add(keys.next());
        } 
        int fp = 0;
        while (otherkeys.hasNext()) {
            if (f.isPresent(otherkeys.next())) {
                fp++;
            }
        } 
        double fp_ratio = fp / ((keys.size()) * (com.clearspring.analytics.stream.membership.BloomCalculations.probs[com.clearspring.analytics.stream.membership.AmplFilterTest.spec.bucketsPerElement][com.clearspring.analytics.stream.membership.AmplFilterTest.spec.K]));
        org.junit.Assert.assertTrue(("FP ratio: " + fp_ratio), (fp_ratio < 1.03));
    }

    public static com.clearspring.analytics.stream.membership.Filter testSerialize(com.clearspring.analytics.stream.membership.Filter f) throws java.io.IOException {
        f.add("a");
        com.clearspring.analytics.stream.membership.DataOutputBuffer out = new com.clearspring.analytics.stream.membership.DataOutputBuffer();
        f.getSerializer().serialize(f, out);
        com.clearspring.analytics.stream.membership.DataInputBuffer in = new com.clearspring.analytics.stream.membership.DataInputBuffer();
        in.reset(out.getData(), out.getLength());
        com.clearspring.analytics.stream.membership.Filter f2 = f.getSerializer().deserialize(in);
        org.junit.Assert.assertTrue(f2.isPresent("a"));
        org.junit.Assert.assertFalse(f2.isPresent("b"));
        return f2;
    }
}

