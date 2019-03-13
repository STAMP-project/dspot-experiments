/**
 * Copyright (c) 2008-2019, Hazelcast, Inc. All Rights Reserved.
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
package com.hazelcast.util;


import com.hazelcast.test.HazelcastParallelClassRunner;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import java.util.Arrays;
import java.util.BitSet;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;


@RunWith(HazelcastParallelClassRunner.class)
@Category({ QuickTest.class, ParallelTest.class })
public class BitSetUtilsTest extends HazelcastTestSupport {
    private static final int SIZE = 10;

    private BitSet bitSet;

    private List<Integer> indexes;

    @Test
    public void testConstructor() {
        HazelcastTestSupport.assertUtilityConstructor(BitSetUtils.class);
    }

    @Test
    public void hasAtLeastOneBitSet_whenEmptyBitSet_thenReturnFalse() {
        boolean isBitSet = BitSetUtils.hasAtLeastOneBitSet(bitSet, indexes);
        Assert.assertFalse(isBitSet);
    }

    @Test
    public void hasAtLeastOneBitSet_whenBitIsSet_thenReturnTrue() {
        for (int position = 0; position < (BitSetUtilsTest.SIZE); position++) {
            bitSet = new BitSet(BitSetUtilsTest.SIZE);
            bitSet.set(position);
            boolean isBitSet = BitSetUtils.hasAtLeastOneBitSet(bitSet, indexes);
            Assert.assertTrue(isBitSet);
        }
    }

    @Test
    public void setBits_thenSetAllBits() {
        BitSetUtils.setBits(bitSet, indexes);
        BitSetUtilsTest.assertBitsAtPositionsAreSet(bitSet, indexes);
    }

    @Test
    public void hasAllBitsSet_true() {
        bitSet.set(1);
        bitSet.set(3);
        bitSet.set(5);
        Assert.assertTrue(BitSetUtils.hasAllBitsSet(bitSet, Arrays.asList(1, 3, 5)));
        Assert.assertTrue(BitSetUtils.hasAllBitsSet(bitSet, Arrays.asList(3, 5)));
    }

    @Test
    public void hasAllBitsSet_false() {
        bitSet.set(1);
        bitSet.set(3);
        bitSet.set(5);
        Assert.assertFalse(BitSetUtils.hasAllBitsSet(bitSet, Arrays.asList(2, 4, 6)));
        Assert.assertFalse(BitSetUtils.hasAllBitsSet(bitSet, Arrays.asList(3, 5, 6)));
    }

    @Test
    public void unsetBits() {
        bitSet.set(0, 4);
        BitSetUtils.unsetBits(bitSet, Arrays.asList(0, 1, 2, 3, 4));
        Assert.assertTrue(bitSet.isEmpty());
    }

    @Test
    public void unsetBits_individual() {
        bitSet.set(0, 2);
        BitSetUtils.unsetBits(bitSet, Arrays.asList(0));
        Assert.assertFalse(bitSet.get(0));
        Assert.assertTrue(bitSet.get(1));
    }
}

