/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.nifi.serialization.record;


import RecordFieldType.ARRAY;
import RecordFieldType.BYTE;
import RecordFieldType.CHOICE;
import RecordFieldType.INT;
import RecordFieldType.MAP;
import RecordFieldType.RECORD;
import RecordFieldType.STRING;
import java.nio.charset.StandardCharsets;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.nifi.serialization.record.util.DataTypeUtils;
import org.junit.Assert;
import org.junit.Test;


public class TestDataTypeUtils {
    /**
     * This is a unit test to verify conversion java Date objects to Timestamps. Support for this was
     * required in order to help the MongoDB packages handle date/time logical types in the Record API.
     */
    @Test
    public void testDateToTimestamp() {
        Date date = new Date();
        Timestamp ts = DataTypeUtils.toTimestamp(date, null, null);
        Assert.assertNotNull(ts);
        Assert.assertEquals("Times didn't match", ts.getTime(), date.getTime());
        java.sql.Date sDate = new java.sql.Date(date.getTime());
        ts = DataTypeUtils.toTimestamp(date, null, null);
        Assert.assertNotNull(ts);
        Assert.assertEquals("Times didn't match", ts.getTime(), sDate.getTime());
    }

    /* This was a bug in NiFi 1.8 where converting from a Timestamp to a Date with the record path API
    would throw an exception.
     */
    @Test
    public void testTimestampToDate() {
        Date date = new Date();
        Timestamp ts = DataTypeUtils.toTimestamp(date, null, null);
        Assert.assertNotNull(ts);
        java.sql.Date output = DataTypeUtils.toDate(ts, null, null);
        Assert.assertNotNull(output);
        Assert.assertEquals("Timestamps didn't match", output.getTime(), ts.getTime());
    }

    @Test
    public void testConvertRecordMapToJavaMap() {
        Assert.assertNull(DataTypeUtils.convertRecordMapToJavaMap(null, null));
        Assert.assertNull(DataTypeUtils.convertRecordMapToJavaMap(null, MAP.getDataType()));
        Map<String, Object> resultMap = DataTypeUtils.convertRecordMapToJavaMap(new HashMap(), MAP.getDataType());
        Assert.assertNotNull(resultMap);
        Assert.assertTrue(resultMap.isEmpty());
        int[] intArray = new int[]{ 3, 2, 1 };
        Map<String, Object> inputMap = new HashMap<String, Object>() {
            {
                put("field1", "hello");
                put("field2", 1);
                put("field3", intArray);
            }
        };
        resultMap = DataTypeUtils.convertRecordMapToJavaMap(inputMap, STRING.getDataType());
        Assert.assertNotNull(resultMap);
        Assert.assertFalse(resultMap.isEmpty());
        Assert.assertEquals("hello", resultMap.get("field1"));
        Assert.assertEquals(1, resultMap.get("field2"));
        Assert.assertTrue(((resultMap.get("field3")) instanceof int[]));
        Assert.assertNull(resultMap.get("field4"));
    }

    @Test
    public void testConvertRecordArrayToJavaArray() {
        Assert.assertNull(DataTypeUtils.convertRecordArrayToJavaArray(null, null));
        Assert.assertNull(DataTypeUtils.convertRecordArrayToJavaArray(null, STRING.getDataType()));
        String[] stringArray = new String[]{ "Hello", "World!" };
        Object[] resultArray = DataTypeUtils.convertRecordArrayToJavaArray(stringArray, STRING.getDataType());
        Assert.assertNotNull(resultArray);
        for (Object o : resultArray) {
            Assert.assertTrue((o instanceof String));
        }
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testConvertRecordFieldToObject() {
        Assert.assertNull(DataTypeUtils.convertRecordFieldtoObject(null, null));
        Assert.assertNull(DataTypeUtils.convertRecordFieldtoObject(null, MAP.getDataType()));
        final List<RecordField> fields = new ArrayList<>();
        fields.add(new RecordField("defaultOfHello", STRING.getDataType(), "hello"));
        fields.add(new RecordField("noDefault", CHOICE.getChoiceDataType(STRING.getDataType())));
        fields.add(new RecordField("intField", INT.getDataType()));
        fields.add(new RecordField("intArray", ARRAY.getArrayDataType(INT.getDataType())));
        // Map of Records with Arrays
        List<RecordField> nestedRecordFields = new ArrayList<>();
        nestedRecordFields.add(new RecordField("a", ARRAY.getArrayDataType(INT.getDataType())));
        nestedRecordFields.add(new RecordField("b", ARRAY.getArrayDataType(STRING.getDataType())));
        RecordSchema nestedRecordSchema = new org.apache.nifi.serialization.SimpleRecordSchema(nestedRecordFields);
        fields.add(new RecordField("complex", MAP.getMapDataType(RECORD.getRecordDataType(nestedRecordSchema))));
        final RecordSchema schema = new org.apache.nifi.serialization.SimpleRecordSchema(fields);
        final Map<String, Object> values = new HashMap<>();
        values.put("noDefault", "world");
        values.put("intField", 5);
        values.put("intArray", new Integer[]{ 3, 2, 1 });
        final Map<String, Object> complexValues = new HashMap<>();
        final Map<String, Object> complexValueRecord1 = new HashMap<>();
        complexValueRecord1.put("a", new Integer[]{ 3, 2, 1 });
        complexValueRecord1.put("b", new Integer[]{ 5, 4, 3 });
        final Map<String, Object> complexValueRecord2 = new HashMap<>();
        complexValueRecord2.put("a", new String[]{ "hello", "world!" });
        complexValueRecord2.put("b", new String[]{ "5", "4", "3" });
        complexValues.put("complex1", DataTypeUtils.toRecord(complexValueRecord1, nestedRecordSchema, "complex1", StandardCharsets.UTF_8));
        complexValues.put("complex2", DataTypeUtils.toRecord(complexValueRecord2, nestedRecordSchema, "complex2", StandardCharsets.UTF_8));
        values.put("complex", complexValues);
        final Record inputRecord = new MapRecord(schema, values);
        Object o = DataTypeUtils.convertRecordFieldtoObject(inputRecord, RECORD.getRecordDataType(schema));
        Assert.assertTrue((o instanceof Map));
        Map<String, Object> outputMap = ((Map<String, Object>) (o));
        Assert.assertEquals("hello", outputMap.get("defaultOfHello"));
        Assert.assertEquals("world", outputMap.get("noDefault"));
        o = outputMap.get("intField");
        Assert.assertEquals(5, o);
        o = outputMap.get("intArray");
        Assert.assertTrue((o instanceof Integer[]));
        Integer[] intArray = ((Integer[]) (o));
        Assert.assertEquals(3, intArray.length);
        Assert.assertEquals(((Integer) (3)), intArray[0]);
        o = outputMap.get("complex");
        Assert.assertTrue((o instanceof Map));
        Map<String, Object> nestedOutputMap = ((Map<String, Object>) (o));
        o = nestedOutputMap.get("complex1");
        Assert.assertTrue((o instanceof Map));
        Map<String, Object> complex1 = ((Map<String, Object>) (o));
        o = complex1.get("a");
        Assert.assertTrue((o instanceof Integer[]));
        Assert.assertEquals(((Integer) (2)), ((Integer[]) (o))[1]);
        o = complex1.get("b");
        Assert.assertTrue((o instanceof Integer[]));
        Assert.assertEquals(((Integer) (3)), ((Integer[]) (o))[2]);
        o = nestedOutputMap.get("complex2");
        Assert.assertTrue((o instanceof Map));
        Map<String, Object> complex2 = ((Map<String, Object>) (o));
        o = complex2.get("a");
        Assert.assertTrue((o instanceof String[]));
        Assert.assertEquals("hello", ((String[]) (o))[0]);
        o = complex2.get("b");
        Assert.assertTrue((o instanceof String[]));
        Assert.assertEquals("4", ((String[]) (o))[1]);
    }

    @Test
    public void testToArray() {
        final List<String> list = Arrays.asList("Seven", "Eleven", "Thirteen");
        final Object[] array = DataTypeUtils.toArray(list, "list", null);
        Assert.assertEquals(list.size(), array.length);
        for (int i = 0; i < (list.size()); i++) {
            Assert.assertEquals(list.get(i), array[i]);
        }
    }

    @Test
    public void testStringToBytes() {
        Object bytes = DataTypeUtils.convertType("Hello", ARRAY.getArrayDataType(BYTE.getDataType()), null, StandardCharsets.UTF_8);
        Assert.assertTrue((bytes instanceof Byte[]));
        Assert.assertNotNull(bytes);
        Byte[] b = ((Byte[]) (bytes));
        Assert.assertEquals("Conversion from String to byte[] failed", ((long) (72)), ((long) (b[0])));// H

        Assert.assertEquals("Conversion from String to byte[] failed", ((long) (101)), ((long) (b[1])));// e

        Assert.assertEquals("Conversion from String to byte[] failed", ((long) (108)), ((long) (b[2])));// l

        Assert.assertEquals("Conversion from String to byte[] failed", ((long) (108)), ((long) (b[3])));// l

        Assert.assertEquals("Conversion from String to byte[] failed", ((long) (111)), ((long) (b[4])));// o

    }

    @Test
    public void testBytesToString() {
        Object s = DataTypeUtils.convertType("Hello".getBytes(StandardCharsets.UTF_16), STRING.getDataType(), null, StandardCharsets.UTF_16);
        Assert.assertNotNull(s);
        Assert.assertTrue((s instanceof String));
        Assert.assertEquals("Conversion from byte[] to String failed", "Hello", s);
    }

    @Test
    public void testBytesToBytes() {
        Object b = DataTypeUtils.convertType("Hello".getBytes(StandardCharsets.UTF_16), ARRAY.getArrayDataType(BYTE.getDataType()), null, StandardCharsets.UTF_16);
        Assert.assertNotNull(b);
        Assert.assertTrue((b instanceof Byte[]));
        Assert.assertEquals("Conversion from byte[] to String failed at char 0", ((Object) ("Hello".getBytes(StandardCharsets.UTF_16)[0])), ((Byte[]) (b))[0]);
    }

    @Test
    public void testFloatingPointCompatibility() {
        final String[] prefixes = new String[]{ "", "-", "+" };
        final String[] exponents = new String[]{ "e0", "e1", "e-1", "E0", "E1", "E-1" };
        final String[] decimals = new String[]{ "", ".0", ".1", "." };
        for (final String prefix : prefixes) {
            for (final String decimal : decimals) {
                for (final String exp : exponents) {
                    String toTest = ((prefix + "100") + decimal) + exp;
                    Assert.assertTrue((toTest + " not valid float"), DataTypeUtils.isFloatTypeCompatible(toTest));
                    Assert.assertTrue((toTest + " not valid double"), DataTypeUtils.isDoubleTypeCompatible(toTest));
                    Double.parseDouble(toTest);// ensure we can actually parse it

                    Float.parseFloat(toTest);
                    if ((decimal.length()) > 1) {
                        toTest = (prefix + decimal) + exp;
                        Assert.assertTrue((toTest + " not valid float"), DataTypeUtils.isFloatTypeCompatible(toTest));
                        Assert.assertTrue((toTest + " not valid double"), DataTypeUtils.isDoubleTypeCompatible(toTest));
                        Double.parseDouble(toTest);// ensure we can actually parse it

                        Float.parseFloat(toTest);
                    }
                }
            }
        }
    }
}

