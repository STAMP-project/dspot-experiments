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
package com.hazelcast.config.properties;


import PropertyTypeConverter.BOOLEAN;
import PropertyTypeConverter.DOUBLE;
import PropertyTypeConverter.FLOAT;
import PropertyTypeConverter.INTEGER;
import PropertyTypeConverter.LONG;
import PropertyTypeConverter.SHORT;
import PropertyTypeConverter.STRING;
import com.hazelcast.test.HazelcastParallelClassRunner;
import com.hazelcast.test.annotation.QuickTest;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;


@RunWith(HazelcastParallelClassRunner.class)
@Category(QuickTest.class)
public class PropertyTypeConverterTest {
    @Test(expected = NullPointerException.class)
    public void test_string_converter_thenNullPointerException() throws Exception {
        STRING.convert(null);
    }

    @Test
    public void test_string_converter() throws Exception {
        Assert.assertEquals("test", STRING.convert("test"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void test_short_converter_thenIllegalArgumentException() throws Exception {
        SHORT.convert(null);
    }

    @Test(expected = NumberFormatException.class)
    public void test_short_converter_thenNumberFormatException() throws Exception {
        SHORT.convert("test");
    }

    @Test
    public void test_short_converter() throws Exception {
        Assert.assertEquals(Short.MAX_VALUE, SHORT.convert(String.valueOf(Short.MAX_VALUE)));
    }

    @Test(expected = IllegalArgumentException.class)
    public void test_int_converter_thenIllegalArgumentException() throws Exception {
        INTEGER.convert(null);
    }

    @Test(expected = NumberFormatException.class)
    public void test_int_converter_thenNumberFormatException() throws Exception {
        INTEGER.convert("test");
    }

    @Test
    public void test_int_converter() throws Exception {
        Assert.assertEquals(Integer.MAX_VALUE, INTEGER.convert(String.valueOf(Integer.MAX_VALUE)));
    }

    @Test(expected = IllegalArgumentException.class)
    public void test_long_converter_thenIllegalArgumentException() throws Exception {
        LONG.convert(null);
    }

    @Test(expected = NumberFormatException.class)
    public void test_long_converter_thenNumberFormatException() throws Exception {
        LONG.convert("test");
    }

    @Test
    public void test_long_converter() throws Exception {
        Assert.assertEquals(Long.MAX_VALUE, LONG.convert(String.valueOf(Long.MAX_VALUE)));
    }

    @Test(expected = IllegalArgumentException.class)
    public void test_float_converter_thenIllegalArgumentException() throws Exception {
        FLOAT.convert(null);
    }

    @Test(expected = NumberFormatException.class)
    public void test_float_converter_thenNumberFormatException() throws Exception {
        FLOAT.convert("test");
    }

    @Test
    public void test_float_converter() throws Exception {
        Assert.assertEquals(Float.MAX_VALUE, FLOAT.convert(String.valueOf(Float.MAX_VALUE)));
    }

    @Test(expected = IllegalArgumentException.class)
    public void test_double_converter_thenIllegalArgumentException() throws Exception {
        DOUBLE.convert(null);
    }

    @Test(expected = NumberFormatException.class)
    public void test_double_converter_thenNumberFormatException() throws Exception {
        DOUBLE.convert("test");
    }

    @Test
    public void test_double_converter() throws Exception {
        Assert.assertEquals(Double.MAX_VALUE, DOUBLE.convert(String.valueOf(Double.MAX_VALUE)));
    }

    @Test(expected = IllegalArgumentException.class)
    public void test_boolean_converter_thenIllegalArgumentException() throws Exception {
        BOOLEAN.convert(null);
    }

    @Test
    public void test_boolean_converter_false() throws Exception {
        Assert.assertFalse(((Boolean) (BOOLEAN.convert("test"))));
    }

    @Test
    public void test_boolean_converter_true() throws Exception {
        Assert.assertTrue(((Boolean) (BOOLEAN.convert("true"))));
    }
}

