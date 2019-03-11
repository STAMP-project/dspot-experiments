/**
 * Copyright 2016 LinkedIn Corp. All rights reserved.
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
 */
package com.github.ambry.utils;


import BloomCalculations.probs;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import org.junit.Assert;
import org.junit.Test;

import static BloomCalculations.probs;
import static com.github.ambry.utils.KeyGenerator.WordGenerator.WORDS;


public class BloomFilterTest {
    public IFilter bf;

    public BloomFilterTest() {
        bf = FilterFactory.getFilter(10000L, FilterTestHelper.MAX_FAILURE_RATE);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testBloomLimits1() {
        int maxBuckets = (probs.length) - 1;
        int maxK = (probs[maxBuckets].length) - 1;
        // possible
        BloomCalculations.computeBloomSpec(maxBuckets, probs[maxBuckets][maxK]);
        // impossible, throws
        BloomCalculations.computeBloomSpec(maxBuckets, ((probs[maxBuckets][maxK]) / 2));
    }

    @Test
    public void testOne() {
        bf.add(ByteBuffer.wrap("a".getBytes()));
        assert bf.isPresent(ByteBuffer.wrap("a".getBytes()));
        assert !(bf.isPresent(ByteBuffer.wrap("b".getBytes())));
    }

    @Test
    public void testFalsePositivesInt() {
        FilterTestHelper.testFalsePositives(bf, FilterTestHelper.intKeys(), FilterTestHelper.randomKeys2());
    }

    @Test
    public void testFalsePositivesRandom() {
        FilterTestHelper.testFalsePositives(bf, FilterTestHelper.randomKeys(), FilterTestHelper.randomKeys2());
    }

    @Test
    public void testWords() {
        if ((WORDS) == 0) {
            return;
        }
        IFilter bf2 = FilterFactory.getFilter(((WORDS) / 2), FilterTestHelper.MAX_FAILURE_RATE);
        int skipEven = (((WORDS) % 2) == 0) ? 0 : 2;
        FilterTestHelper.testFalsePositives(bf2, new KeyGenerator.WordGenerator(skipEven, 2), new KeyGenerator.WordGenerator(1, 2));
    }

    @Test
    public void testSerialize() throws IOException {
        BloomFilterTest.testSerialize(bf);
    }

    @Test
    public void testManyRandom() {
        testManyHashes(FilterTestHelper.randomKeys());
    }

    @Test
    public void testHugeBFSerialization() throws IOException {
        ByteBuffer test = ByteBuffer.wrap(new byte[]{ 0, 1 });
        File f = File.createTempFile("bloomFilterTest-", ".dat");
        f.deleteOnExit();
        BloomFilter filter = ((BloomFilter) (FilterFactory.getFilter(((((long) (100000)) / 8) + 1), 0.01)));
        filter.add(test);
        DataOutputStream out = new DataOutputStream(new FileOutputStream(f));
        FilterFactory.serialize(filter, out);
        filter.bitset.serialize(out);
        out.close();
        DataInputStream in = new DataInputStream(new FileInputStream(f));
        BloomFilter filter2 = ((BloomFilter) (FilterFactory.deserialize(in)));
        Assert.assertTrue(filter2.isPresent(test));
        in.close();
    }
}

