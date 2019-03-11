/**
 * Copyright 2016 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.google.cloud.logging;


import NullValue.NULL_VALUE;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.protobuf.ListValue;
import com.google.protobuf.Struct;
import com.google.protobuf.Value;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;


public class StructsTest {
    private static final Double NUMBER = 42.0;

    private static final String STRING = "string";

    private static final Boolean BOOLEAN = true;

    private static final List<Object> LIST = ImmutableList.<Object>of(StructsTest.NUMBER, StructsTest.STRING, StructsTest.BOOLEAN);

    private static final Map<String, Object> INNER_MAP = new HashMap<>();

    private static final Map<String, Object> MAP = new HashMap<>();

    private static final Value NULL_VALUE = Value.newBuilder().setNullValue(NullValue.NULL_VALUE).build();

    private static final Value NUMBER_VALUE = Value.newBuilder().setNumberValue(StructsTest.NUMBER).build();

    private static final Value STRING_VALUE = Value.newBuilder().setStringValue(StructsTest.STRING).build();

    private static final Value BOOLEAN_VALUE = Value.newBuilder().setBoolValue(StructsTest.BOOLEAN).build();

    private static final ListValue PROTO_LIST = ListValue.newBuilder().addAllValues(ImmutableList.of(StructsTest.NUMBER_VALUE, StructsTest.STRING_VALUE, StructsTest.BOOLEAN_VALUE)).build();

    private static final Value LIST_VALUE = Value.newBuilder().setListValue(StructsTest.PROTO_LIST).build();

    private static final Struct INNER_STRUCT = Struct.newBuilder().putAllFields(ImmutableMap.of("null", StructsTest.NULL_VALUE, "number", StructsTest.NUMBER_VALUE, "string", StructsTest.STRING_VALUE, "boolean", StructsTest.BOOLEAN_VALUE, "list", StructsTest.LIST_VALUE)).build();

    private static final Value STRUCT_VALUE = Value.newBuilder().setStructValue(StructsTest.INNER_STRUCT).build();

    private static final Map<String, Value> VALUE_MAP = ImmutableMap.<String, Value>builder().put("null", StructsTest.NULL_VALUE).put("number", StructsTest.NUMBER_VALUE).put("string", StructsTest.STRING_VALUE).put("boolean", StructsTest.BOOLEAN_VALUE).put("list", StructsTest.LIST_VALUE).put("struct", StructsTest.STRUCT_VALUE).build();

    private static final Struct STRUCT = Struct.newBuilder().putAllFields(StructsTest.VALUE_MAP).build();

    private static final Map<String, Object> EMPTY_MAP = Collections.emptyMap();

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void testAsMap() {
        Map<String, Object> map = Structs.asMap(StructsTest.STRUCT);
        checkMapField(map, "null", null);
        checkMapField(map, "number", StructsTest.NUMBER);
        checkMapField(map, "string", StructsTest.STRING);
        checkMapField(map, "boolean", StructsTest.BOOLEAN);
        checkMapField(map, "list", StructsTest.LIST);
        checkMapField(map, "struct", StructsTest.INNER_MAP);
        Assert.assertEquals(StructsTest.MAP, map);
    }

    @Test
    public void testAsMapPut() {
        Map<String, Object> map = Structs.asMap(StructsTest.STRUCT);
        thrown.expect(UnsupportedOperationException.class);
        map.put("key", "value");
    }

    @Test
    public void testAsMapRemove() {
        Map<String, Object> map = Structs.asMap(StructsTest.STRUCT);
        thrown.expect(UnsupportedOperationException.class);
        map.remove("null");
    }

    @Test
    public void testAsMapEmpty() {
        Map<String, Object> map = Structs.asMap(Struct.getDefaultInstance());
        Assert.assertTrue(map.isEmpty());
        Assert.assertEquals(StructsTest.EMPTY_MAP, map);
    }

    @Test
    public void testAsMapNull() {
        thrown.expect(NullPointerException.class);
        Structs.asMap(null);
    }

    @Test
    public void testNewStruct() {
        Struct struct = Structs.newStruct(StructsTest.MAP);
        checkStructField(struct, "null", StructsTest.NULL_VALUE);
        checkStructField(struct, "number", StructsTest.NUMBER_VALUE);
        checkStructField(struct, "string", StructsTest.STRING_VALUE);
        checkStructField(struct, "boolean", StructsTest.BOOLEAN_VALUE);
        checkStructField(struct, "list", StructsTest.LIST_VALUE);
        checkStructField(struct, "struct", StructsTest.STRUCT_VALUE);
        Assert.assertEquals(StructsTest.STRUCT, struct);
    }

    @Test
    public void testNewStructEmpty() {
        Struct struct = Structs.newStruct(StructsTest.EMPTY_MAP);
        Assert.assertTrue(struct.getFieldsMap().isEmpty());
    }

    @Test
    public void testNewStructNull() {
        thrown.expect(NullPointerException.class);
        Structs.newStruct(null);
    }

    @Test
    public void testNumbers() {
        int intNumber = Integer.MIN_VALUE;
        long longNumber = Long.MAX_VALUE;
        float floatNumber = Float.MIN_VALUE;
        double doubleNumber = Double.MAX_VALUE;
        Map<String, Object> map = ImmutableMap.<String, Object>of("int", intNumber, "long", longNumber, "float", floatNumber, "double", doubleNumber);
        Struct struct = Structs.newStruct(map);
        checkStructField(struct, "int", Value.newBuilder().setNumberValue(intNumber).build());
        checkStructField(struct, "long", Value.newBuilder().setNumberValue(longNumber).build());
        checkStructField(struct, "float", Value.newBuilder().setNumberValue(floatNumber).build());
        checkStructField(struct, "double", Value.newBuilder().setNumberValue(doubleNumber).build());
        Map<String, Object> convertedMap = Structs.asMap(struct);
        Assert.assertTrue(((convertedMap.get("int")) instanceof Double));
        Assert.assertTrue(((convertedMap.get("long")) instanceof Double));
        Assert.assertTrue(((convertedMap.get("float")) instanceof Double));
        Assert.assertTrue(((convertedMap.get("double")) instanceof Double));
        int convertedInteger = ((Double) (convertedMap.get("int"))).intValue();
        long convertedLong = ((Double) (convertedMap.get("long"))).longValue();
        float convertedFloat = ((Double) (convertedMap.get("float"))).floatValue();
        double convertedDouble = ((Double) (convertedMap.get("double")));
        Assert.assertEquals(intNumber, convertedInteger);
        Assert.assertEquals(longNumber, convertedLong);
        Assert.assertEquals(floatNumber, convertedFloat, 0);
        Assert.assertEquals(doubleNumber, convertedDouble, 0);
    }
}

