/**
 * Copyright (c) 2015, Cloudera, Inc. All Rights Reserved.
 *
 * Cloudera, Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"). You may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * This software is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
 * CONDITIONS OF ANY KIND, either express or implied. See the License for
 * the specific language governing permissions and limitations under the
 * License.
 */
package com.cloudera.oryx.app.serving.als.model;


import LocalitySensitiveHash.MAX_HASHES;
import com.cloudera.oryx.common.OryxTest;
import java.util.Arrays;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public final class LocalitySensitiveHashTest extends OryxTest {
    private static final Logger log = LoggerFactory.getLogger(LocalitySensitiveHashTest.class);

    @Test
    public void testOneCore() {
        // 1 core, evaluate all: no hashes necessary at all
        LocalitySensitiveHashTest.doTestHashesBits(1.0, 1, 0, 0);
        // 1 core, evaluate half: 1 hash to split in half, evaluate only half (0 bits differ)
        LocalitySensitiveHashTest.doTestHashesBits(0.5, 1, 1, 0);
        // 1 core, evaluate <= 0.1: need 4 hashes to split in 1/16, then evaluate 1/16th (0 bits differ)
        LocalitySensitiveHashTest.doTestHashesBits(0.1, 1, 4, 0);
    }

    @Test
    public void testTwoCores() {
        // 2 cores, evaluate all: 1 hash to split, but evaluate both to keep 2 cores busy (the 1 bit can differ)
        LocalitySensitiveHashTest.doTestHashesBits(1.0, 2, 1, 1);
        // 2 cores, evaluate half: 2 hashes split in 1/4, but can only keep 1 core busy at 0 bits differing
        // Allow 1 bit differing even though means evaluating 3 partitions
        LocalitySensitiveHashTest.doTestHashesBits(0.75, 3, 2, 1);
    }

    @Test
    public void testManyCores() {
        // But 3 cores should allow 1 bit difference if 3/4 is to be evaluated
        LocalitySensitiveHashTest.doTestHashesBits(0.75, 3, 2, 1);
        // 2 cores, evaluate half: 2 hashes split in 1/4, but can only keep 1 core busy at 0 bits differing
        // Allow 1 bit differing even though means evaluating 3 partitions, but then that evaluates 3/4 = 0.75 of
        // candidates which is too much. Ends up needing 3 hashes.
        LocalitySensitiveHashTest.doTestHashesBits(0.5, 3, 3, 1);
        // Ends up needing 7 hashes, 1 bit differing (1+7=8 partitions to try) to achieve 8 / 2^7 <= 0.1 sampling
        LocalitySensitiveHashTest.doTestHashesBits(0.1, 8, 7, 1);
        LocalitySensitiveHashTest.doTestHashesBits(0.01, 8, 11, 1);
        LocalitySensitiveHashTest.doTestHashesBits(0.001, 8, 14, 1);
        // Near max hashes:
        LocalitySensitiveHashTest.doTestHashesBits(1.0E-4, 8, 16, 1);
        // Maxes out at 16 hashes
        LocalitySensitiveHashTest.doTestHashesBits(1.0E-5, 8, MAX_HASHES, 1);
    }

    @Test
    public void testHashDistribution() {
        LocalitySensitiveHashTest.doTestHashDistribution(200, 1.0, 16);
        LocalitySensitiveHashTest.doTestHashDistribution(200, 0.1, 16);
        LocalitySensitiveHashTest.doTestHashDistribution(40, 1.0, 8);
        LocalitySensitiveHashTest.doTestHashDistribution(40, 0.1, 8);
        LocalitySensitiveHashTest.doTestHashDistribution(40, 1.0, 1);
        LocalitySensitiveHashTest.doTestHashDistribution(40, 0.1, 1);
        LocalitySensitiveHashTest.doTestHashDistribution(10, 1.0, 1);
        LocalitySensitiveHashTest.doTestHashDistribution(10, 0.1, 1);
    }

    @Test
    public void testCandidateIndicesNoSample() {
        int features = 10;
        LocalitySensitiveHash lsh = new LocalitySensitiveHash(1.0, features, 8);
        float[] zeroVec = new float[features];
        int[] candidates = lsh.getCandidateIndices(zeroVec);
        int numHashes = 1 << (lsh.getNumHashes());
        assertEquals(numHashes, candidates.length);
        for (int i = 0; i < numHashes; i++) {
            assertEquals(i, candidates[i]);
        }
    }

    @Test
    public void testCandidateIndicesOneBit() {
        int features = 10;
        LocalitySensitiveHash lsh = new LocalitySensitiveHash(0.1, features, 8);
        assertEquals(1, lsh.getMaxBitsDiffering());
        float[] zeroVec = new float[features];
        int[] zeroCandidates = lsh.getCandidateIndices(zeroVec);
        assertEquals((1 + (lsh.getNumHashes())), zeroCandidates.length);
        assertEquals(0, zeroCandidates[0]);
        for (int i = 1; i < (zeroCandidates.length); i++) {
            assertEquals((1L << (i - 1)), zeroCandidates[i]);
        }
        float[] oneVec = new float[features];
        Arrays.fill(oneVec, 1.0F);
        int[] oneCandidates = lsh.getCandidateIndices(oneVec);
        for (int i = 1; i < (oneCandidates.length); i++) {
            assertEquals(((oneCandidates[0]) ^ (1L << (i - 1))), oneCandidates[i]);
        }
    }

    @Test
    public void testCandidateIndices() {
        int features = 10;
        LocalitySensitiveHash lsh = new LocalitySensitiveHash(0.5, features, 32);
        assertEquals(3, lsh.getMaxBitsDiffering());
        assertEquals(7, lsh.getNumHashes());
        float[] oneVec = new float[features];
        Arrays.fill(oneVec, 1.0F);
        int[] candidates = lsh.getCandidateIndices(oneVec);
        assertEquals(64, candidates.length);// 1 + 7 + 21 + 35

        for (int i = 1; i < 8; i++) {
            assertEquals(1, Integer.bitCount(((candidates[0]) ^ (candidates[i]))));
        }
        for (int i = 8; i < 29; i++) {
            assertEquals(2, Integer.bitCount(((candidates[0]) ^ (candidates[i]))));
        }
        for (int i = 29; i < 64; i++) {
            assertEquals(3, Integer.bitCount(((candidates[0]) ^ (candidates[i]))));
        }
    }
}

