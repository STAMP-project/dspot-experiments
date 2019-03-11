/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.flink.streaming.runtime.operators.windowing;


import KeyMap.Entry;
import java.util.BitSet;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests for {@link KeyMap}.
 */
public class KeyMapPutTest {
    @Test
    public void testPutUniqueKeysAndGrowth() {
        try {
            KeyMap<Integer, Integer> map = new KeyMap();
            final int numElements = 1000000;
            for (int i = 0; i < numElements; i++) {
                map.put(i, ((2 * i) + 1));
                Assert.assertEquals((i + 1), map.size());
                Assert.assertTrue(((map.getCurrentTableCapacity()) > (map.size())));
                Assert.assertTrue(((map.getCurrentTableCapacity()) > (map.getRehashThreshold())));
                Assert.assertTrue(((map.size()) <= (map.getRehashThreshold())));
            }
            Assert.assertEquals(numElements, map.size());
            Assert.assertEquals(numElements, map.traverseAndCountElements());
            Assert.assertEquals((1 << 21), map.getCurrentTableCapacity());
            for (int i = 0; i < numElements; i++) {
                Assert.assertEquals(((2 * i) + 1), map.get(i).intValue());
            }
            for (int i = numElements - 1; i >= 0; i--) {
                Assert.assertEquals(((2 * i) + 1), map.get(i).intValue());
            }
            BitSet bitset = new BitSet();
            int numContained = 0;
            for (Entry<Integer, Integer> entry : map) {
                numContained++;
                Assert.assertEquals((((entry.getKey()) * 2) + 1), entry.getValue().intValue());
                Assert.assertFalse(bitset.get(entry.getKey()));
                bitset.set(entry.getKey());
            }
            Assert.assertEquals(numElements, numContained);
            Assert.assertEquals(numElements, bitset.cardinality());
            Assert.assertEquals(numElements, map.size());
            Assert.assertEquals(numElements, map.traverseAndCountElements());
            Assert.assertEquals((1 << 21), map.getCurrentTableCapacity());
            Assert.assertTrue(((map.getLongestChainLength()) <= 7));
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void testPutDuplicateKeysAndGrowth() {
        try {
            final KeyMap<Integer, Integer> map = new KeyMap();
            final int numElements = 1000000;
            for (int i = 0; i < numElements; i++) {
                Integer put = map.put(i, ((2 * i) + 1));
                Assert.assertNull(put);
            }
            for (int i = 0; i < numElements; i += 3) {
                Integer put = map.put(i, (2 * i));
                Assert.assertNotNull(put);
                Assert.assertEquals(((2 * i) + 1), put.intValue());
            }
            for (int i = 0; i < numElements; i++) {
                int expected = ((i % 3) == 0) ? 2 * i : (2 * i) + 1;
                Assert.assertEquals(expected, map.get(i).intValue());
            }
            Assert.assertEquals(numElements, map.size());
            Assert.assertEquals(numElements, map.traverseAndCountElements());
            Assert.assertEquals((1 << 21), map.getCurrentTableCapacity());
            Assert.assertTrue(((map.getLongestChainLength()) <= 7));
            BitSet bitset = new BitSet();
            int numContained = 0;
            for (Entry<Integer, Integer> entry : map) {
                numContained++;
                int key = entry.getKey();
                int expected = ((key % 3) == 0) ? 2 * key : (2 * key) + 1;
                Assert.assertEquals(expected, entry.getValue().intValue());
                Assert.assertFalse(bitset.get(key));
                bitset.set(key);
            }
            Assert.assertEquals(numElements, numContained);
            Assert.assertEquals(numElements, bitset.cardinality());
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }
    }
}

