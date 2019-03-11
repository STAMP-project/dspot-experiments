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


import org.apache.flink.types.NullValue;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests for {@link NullValueArray}.
 */
public class NullValueArrayTest {
    @Test
    public void testUnboundedArray() {
        int count = 4096;
        ValueArray<NullValue> nva = new NullValueArray();
        // add several elements
        for (int i = 0; i < count; i++) {
            Assert.assertFalse(nva.isFull());
            Assert.assertEquals(i, nva.size());
            Assert.assertTrue(nva.add(NullValue.getInstance()));
            Assert.assertEquals((i + 1), nva.size());
        }
        // array never fills
        Assert.assertFalse(nva.isFull());
        Assert.assertEquals(count, nva.size());
        // verify the array values
        int idx = 0;
        for (NullValue nv : nva) {
            Assert.assertEquals(NullValue.getInstance(), nv);
        }
        // add element past end of array
        Assert.assertTrue(nva.add(NullValue.getInstance()));
        Assert.assertTrue(nva.addAll(nva));
        // test copy
        Assert.assertEquals(nva, nva.copy());
        // test copyTo
        NullValueArray nvaTo = new NullValueArray();
        nva.copyTo(nvaTo);
        Assert.assertEquals(nva, nvaTo);
        // test mark/reset
        int size = nva.size();
        nva.mark();
        Assert.assertTrue(nva.add(NullValue.getInstance()));
        Assert.assertEquals((size + 1), nva.size());
        nva.reset();
        Assert.assertEquals(size, nva.size());
        // test clear
        nva.clear();
        Assert.assertEquals(0, nva.size());
    }
}

