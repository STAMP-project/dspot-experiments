/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.flink.graph.types.valuearray;


import org.apache.flink.types.ByteValue;
import org.junit.Assert;
import org.junit.Test;

import static ByteValueArray.DEFAULT_CAPACITY_IN_BYTES;
import static ByteValueArray.ELEMENT_LENGTH_IN_BYTES;


/**
 * Tests for {@link ByteValueArray}.
 */
public class ByteValueArrayTest {
    @Test
    public void testBoundedArray() {
        int count = (DEFAULT_CAPACITY_IN_BYTES) / (ELEMENT_LENGTH_IN_BYTES);
        ValueArray<ByteValue> lva = new ByteValueArray(DEFAULT_CAPACITY_IN_BYTES);
        // fill the array
        for (int i = 0; i < count; i++) {
            Assert.assertFalse(lva.isFull());
            Assert.assertEquals(i, lva.size());
            Assert.assertTrue(lva.add(new ByteValue(((byte) (i)))));
            Assert.assertEquals((i + 1), lva.size());
        }
        // array is now full
        Assert.assertTrue(lva.isFull());
        Assert.assertEquals(count, lva.size());
        // verify the array values
        int idx = 0;
        for (ByteValue lv : lva) {
            Assert.assertEquals(((byte) (idx++)), lv.getValue());
        }
        // add element past end of array
        Assert.assertFalse(lva.add(new ByteValue(((byte) (count)))));
        Assert.assertFalse(lva.addAll(lva));
        // test copy
        Assert.assertEquals(lva, lva.copy());
        // test copyTo
        ByteValueArray lvaTo = new ByteValueArray();
        lva.copyTo(lvaTo);
        Assert.assertEquals(lva, lvaTo);
        // test clear
        lva.clear();
        Assert.assertEquals(0, lva.size());
    }

    @Test
    public void testUnboundedArray() {
        int count = 4096;
        ValueArray<ByteValue> lva = new ByteValueArray();
        // add several elements
        for (int i = 0; i < count; i++) {
            Assert.assertFalse(lva.isFull());
            Assert.assertEquals(i, lva.size());
            Assert.assertTrue(lva.add(new ByteValue(((byte) (i)))));
            Assert.assertEquals((i + 1), lva.size());
        }
        // array never fills
        Assert.assertFalse(lva.isFull());
        Assert.assertEquals(count, lva.size());
        // verify the array values
        int idx = 0;
        for (ByteValue lv : lva) {
            Assert.assertEquals(((byte) (idx++)), lv.getValue());
        }
        // add element past end of array
        Assert.assertTrue(lva.add(new ByteValue(((byte) (count)))));
        Assert.assertTrue(lva.addAll(lva));
        // test copy
        Assert.assertEquals(lva, lva.copy());
        // test copyTo
        ByteValueArray lvaTo = new ByteValueArray();
        lva.copyTo(lvaTo);
        Assert.assertEquals(lva, lvaTo);
        // test mark/reset
        int size = lva.size();
        lva.mark();
        Assert.assertTrue(lva.add(new ByteValue()));
        Assert.assertEquals((size + 1), lva.size());
        lva.reset();
        Assert.assertEquals(size, lva.size());
        // test clear
        lva.clear();
        Assert.assertEquals(0, lva.size());
    }
}

