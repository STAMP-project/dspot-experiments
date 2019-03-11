/**
 * Licensed to CRATE Technology GmbH ("Crate") under one or more contributor
 * license agreements.  See the NOTICE file distributed with this work for
 * additional information regarding copyright ownership.  Crate licenses
 * this file to you under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.  You may
 * obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations
 * under the License.
 *
 * However, if you have executed another commercial license agreement
 * with Crate these terms will supersede the license and you may use the
 * software solely pursuant to the terms of the relevant commercial agreement.
 */
package io.crate.types;


import DataTypes.BOOLEAN;
import DataTypes.BYTE;
import DataTypes.DOUBLE;
import DataTypes.FLOAT;
import DataTypes.INTEGER;
import DataTypes.LONG;
import DataTypes.SHORT;
import DataTypes.STRING;
import DataTypes.TIMESTAMP;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Sets;
import io.crate.test.integration.CrateUnitTest;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.hamcrest.Matchers;
import org.junit.Test;

import static DataTypes.LONG;
import static DataTypes.STRING;


public class DataTypesTest extends CrateUnitTest {
    @Test
    public void testConvertBooleanToString() {
        String value = STRING.value(true);
        assertEquals("t", value);
    }

    @Test
    public void testConvertStringToBoolean() {
        assertEquals(true, BOOLEAN.value("t"));
        assertEquals(false, BOOLEAN.value("false"));
        assertEquals(false, BOOLEAN.value("FALSE"));
        assertEquals(false, BOOLEAN.value("f"));
        assertEquals(false, BOOLEAN.value("F"));
        assertEquals(true, BOOLEAN.value("true"));
        assertEquals(true, BOOLEAN.value("TRUE"));
        assertEquals(true, BOOLEAN.value("t"));
        assertEquals(true, BOOLEAN.value("T"));
    }

    @Test
    public void testLongToNumbers() {
        Long longValue = 123L;
        assertEquals(((Long) (123L)), LONG.value(longValue));
        assertEquals(((Integer) (123)), INTEGER.value(longValue));
        assertEquals(((Double) (123.0)), DOUBLE.value(longValue));
        assertEquals(((Float) (123.0F)), FLOAT.value(longValue));
        assertEquals(((Short) ((short) (123))), SHORT.value(longValue));
        assertEquals(((Byte) ((byte) (123))), BYTE.value(longValue));
        assertEquals(((Long) (123L)), TIMESTAMP.value(longValue));
        assertEquals("123", STRING.value(longValue));
    }

    @Test
    public void testConvertArrayToSet() throws Exception {
        ArrayType longArray = new ArrayType(LONG);
        SetType longSet = new SetType(LONG);
        assertThat(longArray.isConvertableTo(longSet), Matchers.is(true));
        assertThat(longSet.value(new Object[]{ 1L, 2L }), Matchers.is(((Set) (Sets.newHashSet(1L, 2L)))));
        assertThat(longSet.value(Arrays.asList(1L, 2L)), Matchers.is(((Set) (Sets.newHashSet(1L, 2L)))));
    }

    @Test
    public void testConvertSetToArray() throws Exception {
        ArrayType longArray = new ArrayType(LONG);
        SetType longSet = new SetType(LONG);
        assertThat(longSet.isConvertableTo(longArray), Matchers.is(true));
        assertThat(longArray.value(Sets.newHashSet(1L, 2L)), Matchers.is(new Object[]{ 1L, 2L }));
    }

    private static Map<String, Object> testMap = new HashMap<String, Object>() {
        {
            put("int", 1);
            put("boolean", false);
            put("double", 2.8);
            put("list", Arrays.asList(1, 3, 4));
        }
    };

    private static Map<String, Object> testCompareMap = new HashMap<String, Object>() {
        {
            put("int", 2);
            put("boolean", true);
            put("double", 2.9);
            put("list", Arrays.asList(9, 9, 9, 9));
        }
    };

    @Test
    public void testConvertToObject() {
        DataType objectType = ObjectType.untyped();
        assertThat(objectType.value(DataTypesTest.testMap), Matchers.is(DataTypesTest.testMap));
    }

    @Test(expected = ClassCastException.class)
    public void testMapToBoolean() {
        BOOLEAN.value(DataTypesTest.testMap);
    }

    @Test(expected = ClassCastException.class)
    public void testMapToLong() {
        LONG.value(DataTypesTest.testMap);
    }

    @Test(expected = ClassCastException.class)
    public void testMapToInteger() {
        INTEGER.value(DataTypesTest.testMap);
    }

    @Test(expected = ClassCastException.class)
    public void testConvertToDouble() {
        DOUBLE.value(DataTypesTest.testMap);
    }

    @Test(expected = ClassCastException.class)
    public void testConvertToFloat() {
        FLOAT.value(DataTypesTest.testMap);
    }

    @Test(expected = ClassCastException.class)
    public void testConvertToShort() {
        SHORT.value(DataTypesTest.testMap);
    }

    @Test(expected = ClassCastException.class)
    public void testConvertToByte() {
        BYTE.value(DataTypesTest.testMap);
    }

    @Test(expected = ClassCastException.class)
    public void testConvertMapToTimestamp() {
        TIMESTAMP.value(DataTypesTest.testMap);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testCompareTo() {
        Map testMapCopy = ImmutableMap.copyOf(DataTypesTest.testMap);
        Map emptyMap = ImmutableMap.of();
        DataType objectType = ObjectType.untyped();
        assertThat(objectType.compareValueTo(DataTypesTest.testMap, testMapCopy), Matchers.is(0));
        assertThat(objectType.compareValueTo(testMapCopy, DataTypesTest.testMap), Matchers.is(0));
        // first number of argument is checked
        assertThat(objectType.compareValueTo(DataTypesTest.testMap, emptyMap), Matchers.is(1));
        assertThat(objectType.compareValueTo(emptyMap, DataTypesTest.testMap), Matchers.is((-1)));
        // then values
        assertThat(objectType.compareValueTo(DataTypesTest.testMap, DataTypesTest.testCompareMap), Matchers.is(1));
        assertThat(objectType.compareValueTo(DataTypesTest.testCompareMap, DataTypesTest.testMap), Matchers.is(1));
    }

    @Test
    public void testSetTypeCompareToSameLength() {
        HashSet<String> set1 = Sets.newHashSet("alpha", "bravo", "charlie", "delta");
        HashSet<String> set2 = Sets.newHashSet("foo", "alpha", "beta", "bar");
        // only length is compared, not the actual values
        DataType type = new SetType(STRING);
        assertThat(type.compareValueTo(set1, set2), Matchers.is(0));
    }

    @Test
    public void testSetTypeCompareToDifferentLength() {
        HashSet<String> set1 = Sets.newHashSet("alpha", "bravo", "charlie", "delta");
        HashSet<String> set2 = Sets.newHashSet("foo", "alpha", "beta");
        DataType type = new SetType(STRING);
        assertThat(type.compareValueTo(set1, set2), Matchers.is(1));
    }

    @Test
    public void testStringConvertToNumbers() {
        String value = "123";
        assertEquals(((Long) (123L)), LONG.value(value));
        assertEquals(((Integer) (123)), INTEGER.value(value));
        assertEquals(((Double) (123.0)), DOUBLE.value(value));
        assertEquals(((Float) (123.0F)), FLOAT.value(value));
        assertEquals(((Short) ((short) (123))), SHORT.value(value));
        assertEquals(((Byte) ((byte) (123))), BYTE.value(value));
        assertEquals("123", STRING.value(value));
    }

    @Test
    public void testConvertStringsToTimestamp() {
        assertEquals(((Long) (1393555173000L)), TIMESTAMP.value("2014-02-28T02:39:33"));
        assertEquals(((Long) (1393545600000L)), TIMESTAMP.value("2014-02-28"));
    }

    @Test(expected = NumberFormatException.class)
    public void testConvertToUnsupportedNumberConversion() {
        LONG.value("hello");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testConvertToUnsupportedBooleanConversion() {
        BOOLEAN.value("hello");
    }

    @Test(expected = ClassCastException.class)
    public void testConvertBooleanToLong() {
        LONG.value(true);
    }

    @Test(expected = ClassCastException.class)
    public void testConvertBooleanToInteger() {
        INTEGER.value(true);
    }

    @Test(expected = ClassCastException.class)
    public void testConvertBooleanToDouble() {
        DOUBLE.value(true);
    }

    @Test(expected = ClassCastException.class)
    public void testConvertBooleanToFloat() {
        FLOAT.value(true);
    }

    @Test(expected = ClassCastException.class)
    public void testConvertBooleanToShort() {
        SHORT.value(true);
    }

    @Test(expected = ClassCastException.class)
    public void testConvertBooleanToByte() {
        BYTE.value(true);
    }

    @Test
    public void testLongTypeCompareValueToWith() {
        DataTypesTest.assertCompareValueTo(LONG, null, null, 0);
        DataTypesTest.assertCompareValueTo(null, 2L, (-1));
        DataTypesTest.assertCompareValueTo(3L, 2L, 1);
        DataTypesTest.assertCompareValueTo(2L, 2L, 0);
        DataTypesTest.assertCompareValueTo(2L, null, 1);
    }

    @Test
    public void testShortTypeCompareValueToWith() {
        DataTypesTest.assertCompareValueTo(LONG, null, null, 0);
        DataTypesTest.assertCompareValueTo(null, ((short) (2)), (-1));
        DataTypesTest.assertCompareValueTo(((short) (3)), ((short) (2)), 1);
        DataTypesTest.assertCompareValueTo(((short) (2)), ((short) (2)), 0);
        DataTypesTest.assertCompareValueTo(((short) (2)), null, 1);
    }

    @Test
    public void testIntTypeCompareValueTo() {
        DataTypesTest.assertCompareValueTo(INTEGER, null, null, 0);
        DataTypesTest.assertCompareValueTo(null, 2, (-1));
        DataTypesTest.assertCompareValueTo(3, 2, 1);
        DataTypesTest.assertCompareValueTo(2, 2, 0);
        DataTypesTest.assertCompareValueTo(2, null, 1);
    }

    @Test
    public void testDoubleTypeCompareValueTo() {
        DataTypesTest.assertCompareValueTo(DOUBLE, null, null, 0);
        DataTypesTest.assertCompareValueTo(null, 2.0, (-1));
        DataTypesTest.assertCompareValueTo(3.0, 2.0, 1);
        DataTypesTest.assertCompareValueTo(2.0, 2.0, 0);
        DataTypesTest.assertCompareValueTo(2.0, null, 1);
    }

    @Test
    public void testFloatTypeCompareValueTo() {
        DataTypesTest.assertCompareValueTo(FLOAT, null, null, 0);
        DataTypesTest.assertCompareValueTo(null, 2.0F, (-1));
        DataTypesTest.assertCompareValueTo(2.0F, 3.0F, (-1));
        DataTypesTest.assertCompareValueTo(2.0F, 2.0F, 0);
        DataTypesTest.assertCompareValueTo(2.0F, null, 1);
    }

    @Test
    public void testByteTypeCompareValueTo() {
        DataTypesTest.assertCompareValueTo(BYTE, null, null, 0);
        DataTypesTest.assertCompareValueTo(null, ((byte) (2)), (-1));
        DataTypesTest.assertCompareValueTo(((byte) (3)), ((byte) (2)), 1);
        DataTypesTest.assertCompareValueTo(((byte) (2)), ((byte) (2)), 0);
        DataTypesTest.assertCompareValueTo(((byte) (2)), null, 1);
    }

    @Test
    public void testBooleanTypeCompareValueTo() {
        DataTypesTest.assertCompareValueTo(BOOLEAN, null, null, 0);
        DataTypesTest.assertCompareValueTo(null, true, (-1));
        DataTypesTest.assertCompareValueTo(true, false, 1);
        DataTypesTest.assertCompareValueTo(true, null, 1);
    }

    @Test
    public void testSmallIntIsAliasedToShort() {
        assertThat(DataTypes.ofName("smallint"), Matchers.is(SHORT));
    }

    @Test
    public void testInt2IsAliasedToShort() {
        assertThat(DataTypes.ofName("int2"), Matchers.is(SHORT));
    }

    @Test
    public void testInt4IsAliasedToInteger() {
        assertThat(DataTypes.ofName("int4"), Matchers.is(INTEGER));
    }

    @Test
    public void testBigIntIsAliasedToLong() {
        assertThat(DataTypes.ofName("bigint"), Matchers.is(LONG));
    }

    @Test
    public void testInt8IsAliasedToLong() {
        assertThat(DataTypes.ofName("int8"), Matchers.is(LONG));
    }
}

