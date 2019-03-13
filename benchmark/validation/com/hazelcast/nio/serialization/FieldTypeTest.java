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
package com.hazelcast.nio.serialization;


import com.hazelcast.test.HazelcastParallelClassRunner;
import com.hazelcast.test.annotation.QuickTest;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;


@RunWith(HazelcastParallelClassRunner.class)
@Category(QuickTest.class)
public class FieldTypeTest {
    @Test
    public void correctNonArrayTypes() {
        Assert.assertFalse(FieldType.BYTE.isArrayType());
        Assert.assertFalse(FieldType.BOOLEAN.isArrayType());
        Assert.assertFalse(FieldType.CHAR.isArrayType());
        Assert.assertFalse(FieldType.SHORT.isArrayType());
        Assert.assertFalse(FieldType.INT.isArrayType());
        Assert.assertFalse(FieldType.LONG.isArrayType());
        Assert.assertFalse(FieldType.FLOAT.isArrayType());
        Assert.assertFalse(FieldType.DOUBLE.isArrayType());
        Assert.assertFalse(FieldType.UTF.isArrayType());
        Assert.assertFalse(FieldType.BYTE.isArrayType());
    }

    @Test
    public void correctArrayTypes() {
        Assert.assertTrue(FieldType.PORTABLE_ARRAY.isArrayType());
        Assert.assertTrue(FieldType.BYTE_ARRAY.isArrayType());
        Assert.assertTrue(FieldType.BOOLEAN_ARRAY.isArrayType());
        Assert.assertTrue(FieldType.CHAR_ARRAY.isArrayType());
        Assert.assertTrue(FieldType.SHORT_ARRAY.isArrayType());
        Assert.assertTrue(FieldType.INT_ARRAY.isArrayType());
        Assert.assertTrue(FieldType.LONG_ARRAY.isArrayType());
        Assert.assertTrue(FieldType.FLOAT_ARRAY.isArrayType());
        Assert.assertTrue(FieldType.DOUBLE_ARRAY.isArrayType());
        Assert.assertTrue(FieldType.UTF_ARRAY.isArrayType());
    }

    @Test
    public void correctSingleTypesConversion() {
        Assert.assertEquals(FieldType.PORTABLE, FieldType.PORTABLE_ARRAY.getSingleType());
        Assert.assertEquals(FieldType.BYTE, FieldType.BYTE_ARRAY.getSingleType());
        Assert.assertEquals(FieldType.BOOLEAN, FieldType.BOOLEAN_ARRAY.getSingleType());
        Assert.assertEquals(FieldType.CHAR, FieldType.CHAR_ARRAY.getSingleType());
        Assert.assertEquals(FieldType.SHORT, FieldType.SHORT_ARRAY.getSingleType());
        Assert.assertEquals(FieldType.INT, FieldType.INT_ARRAY.getSingleType());
        Assert.assertEquals(FieldType.LONG, FieldType.LONG_ARRAY.getSingleType());
        Assert.assertEquals(FieldType.FLOAT, FieldType.FLOAT_ARRAY.getSingleType());
        Assert.assertEquals(FieldType.DOUBLE, FieldType.DOUBLE_ARRAY.getSingleType());
        Assert.assertEquals(FieldType.UTF, FieldType.UTF_ARRAY.getSingleType());
    }

    @Test
    public void assertCorrectTypesCount() {
        Assert.assertEquals("Wrong types count! See isArrayType() implementation for details what will break", 20, FieldType.values().length);
    }
}

