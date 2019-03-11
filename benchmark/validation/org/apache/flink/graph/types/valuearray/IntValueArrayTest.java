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
package org.apache.flink.graph.types.valuearray;


import org.apache.flink.types.IntValue;
import org.junit.Assert;
import org.junit.Test;

import static IntValueArray.DEFAULT_CAPACITY_IN_BYTES;
import static IntValueArray.ELEMENT_LENGTH_IN_BYTES;


/**
 * Tests for {@link IntValueArray}.
 */
public class IntValueArrayTest {
    @Test
    public void testBoundedArray() {
        int count = (DEFAULT_CAPACITY_IN_BYTES) / (ELEMENT_LENGTH_IN_BYTES);
        ValueArray<IntValue> iva = new IntValueArray(DEFAULT_CAPACITY_IN_BYTES);
        // fill the array
        for (int i = 0; i < count; i++) {
            Assert.assertFalse(iva.isFull());
            Assert.assertEquals(i, iva.size());
            Assert.assertTrue(iva.add(new IntValue(i)));
            Assert.assertEquals((i + 1), iva.size());
        }
        // array is now full
        Assert.assertTrue(iva.isFull());
        Assert.assertEquals(count, iva.size());
        // verify the array values
        int idx = 0;
        for (IntValue lv : iva) {
            Assert.assertEquals((idx++), lv.getValue());
        }
        // add element past end of array
        Assert.assertFalse(iva.add(new IntValue(count)));
        Assert.assertFalse(iva.addAll(iva));
        // test copy
        Assert.assertEquals(iva, iva.copy());
        // test copyTo
        IntValueArray ivaTo = new IntValueArray();
        iva.copyTo(ivaTo);
        Assert.assertEquals(iva, ivaTo);
        // test clear
        iva.clear();
        Assert.assertEquals(0, iva.size());
    }

    @Test
    public void testUnboundedArray() {
        int count = 4096;
        ValueArray<IntValue> iva = new IntValueArray();
        // add several elements
        for (int i = 0; i < count; i++) {
            Assert.assertFalse(iva.isFull());
            Assert.assertEquals(i, iva.size());
            Assert.assertTrue(iva.add(new IntValue(i)));
            Assert.assertEquals((i + 1), iva.size());
        }
        // array never fills
        Assert.assertFalse(iva.isFull());
        Assert.assertEquals(count, iva.size());
        // verify the array values
        int idx = 0;
        for (IntValue lv : iva) {
            Assert.assertEquals((idx++), lv.getValue());
        }
        // add element past end of array
        Assert.assertTrue(iva.add(new IntValue(count)));
        Assert.assertTrue(iva.addAll(iva));
        // test copy
        Assert.assertEquals(iva, iva.copy());
        // test copyTo
        IntValueArray ivaTo = new IntValueArray();
        iva.copyTo(ivaTo);
        Assert.assertEquals(iva, ivaTo);
        // test mark/reset
        int size = iva.size();
        iva.mark();
        Assert.assertTrue(iva.add(new IntValue()));
        Assert.assertEquals((size + 1), iva.size());
        iva.reset();
        Assert.assertEquals(size, iva.size());
        // test clear
        iva.clear();
        Assert.assertEquals(0, iva.size());
    }
}

