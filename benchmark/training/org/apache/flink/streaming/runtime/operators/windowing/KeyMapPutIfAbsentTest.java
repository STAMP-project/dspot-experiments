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


import org.junit.Assert;
import org.junit.Test;


/**
 * Tests for {@link KeyMap}.
 */
public class KeyMapPutIfAbsentTest {
    @Test
    public void testPutIfAbsentUniqueKeysAndGrowth() {
        try {
            KeyMap<Integer, Integer> map = new KeyMap();
            KeyMapPutIfAbsentTest.IntegerFactory factory = new KeyMapPutIfAbsentTest.IntegerFactory();
            final int numElements = 1000000;
            for (int i = 0; i < numElements; i++) {
                factory.set(((2 * i) + 1));
                map.putIfAbsent(i, factory);
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
    public void testPutIfAbsentDuplicateKeysAndGrowth() {
        try {
            KeyMap<Integer, Integer> map = new KeyMap();
            KeyMapPutIfAbsentTest.IntegerFactory factory = new KeyMapPutIfAbsentTest.IntegerFactory();
            final int numElements = 1000000;
            for (int i = 0; i < numElements; i++) {
                int val = (2 * i) + 1;
                factory.set(val);
                Integer put = map.putIfAbsent(i, factory);
                Assert.assertEquals(val, put.intValue());
            }
            for (int i = 0; i < numElements; i += 3) {
                factory.set((2 * i));
                Integer put = map.putIfAbsent(i, factory);
                Assert.assertEquals(((2 * i) + 1), put.intValue());
            }
            for (int i = 0; i < numElements; i++) {
                Assert.assertEquals(((2 * i) + 1), map.get(i).intValue());
            }
            Assert.assertEquals(numElements, map.size());
            Assert.assertEquals(numElements, map.traverseAndCountElements());
            Assert.assertEquals((1 << 21), map.getCurrentTableCapacity());
            Assert.assertTrue(((map.getLongestChainLength()) <= 7));
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }
    }

    // ------------------------------------------------------------------------
    private static class IntegerFactory implements KeyMap.LazyFactory<Integer> {
        private Integer toCreate;

        public void set(Integer toCreate) {
            this.toCreate = toCreate;
        }

        @Override
        public Integer create() {
            return toCreate;
        }
    }
}

