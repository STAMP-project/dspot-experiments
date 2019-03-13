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
package org.apache.flink.streaming.util.keys;


import BasicArrayTypeInfo.STRING_ARRAY_TYPE_INFO;
import PrimitiveArrayTypeInfo.INT_PRIMITIVE_ARRAY_TYPE_INFO;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests key selectors on arrays.
 */
public class ArrayKeySelectorTest {
    @Test
    public void testObjectArrays() {
        try {
            String[] array1 = new String[]{ "a", "b", "c", "d", "e" };
            String[] array2 = new String[]{ "v", "w", "x", "y", "z" };
            KeySelectorUtil.ArrayKeySelector<String[]> singleFieldSelector = KeySelectorUtil.getSelectorForArray(new int[]{ 1 }, STRING_ARRAY_TYPE_INFO);
            Assert.assertEquals(new org.apache.flink.api.java.tuple.Tuple1("b"), singleFieldSelector.getKey(array1));
            Assert.assertEquals(new org.apache.flink.api.java.tuple.Tuple1("w"), singleFieldSelector.getKey(array2));
            KeySelectorUtil.ArrayKeySelector<String[]> twoFieldsSelector = KeySelectorUtil.getSelectorForArray(new int[]{ 3, 0 }, STRING_ARRAY_TYPE_INFO);
            Assert.assertEquals(new org.apache.flink.api.java.tuple.Tuple2("d", "a"), twoFieldsSelector.getKey(array1));
            Assert.assertEquals(new org.apache.flink.api.java.tuple.Tuple2("y", "v"), twoFieldsSelector.getKey(array2));
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void testPrimitiveArrays() {
        try {
            int[] array1 = new int[]{ 1, 2, 3, 4, 5 };
            int[] array2 = new int[]{ -5, -4, -3, -2, -1, 0 };
            KeySelectorUtil.ArrayKeySelector<int[]> singleFieldSelector = KeySelectorUtil.getSelectorForArray(new int[]{ 1 }, INT_PRIMITIVE_ARRAY_TYPE_INFO);
            Assert.assertEquals(new org.apache.flink.api.java.tuple.Tuple1(2), singleFieldSelector.getKey(array1));
            Assert.assertEquals(new org.apache.flink.api.java.tuple.Tuple1((-4)), singleFieldSelector.getKey(array2));
            KeySelectorUtil.ArrayKeySelector<int[]> twoFieldsSelector = KeySelectorUtil.getSelectorForArray(new int[]{ 3, 0 }, INT_PRIMITIVE_ARRAY_TYPE_INFO);
            Assert.assertEquals(new org.apache.flink.api.java.tuple.Tuple2(4, 1), twoFieldsSelector.getKey(array1));
            Assert.assertEquals(new org.apache.flink.api.java.tuple.Tuple2((-2), (-5)), twoFieldsSelector.getKey(array2));
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }
    }
}

