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
package org.apache.nifi.repository.schema;


import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;

import static FieldType.BOOLEAN;
import static FieldType.BYTE_ARRAY;
import static FieldType.INT;
import static FieldType.LONG;
import static FieldType.LONG_STRING;
import static FieldType.STRING;
import static Repetition.EXACTLY_ONE;
import static Repetition.ZERO_OR_MORE;
import static Repetition.ZERO_OR_ONE;


public class TestSchemaRecordReader {
    @Test
    public void testReadExactlyOnceFields() throws IOException {
        final List<RecordField> fields = new ArrayList<>();
        fields.add(new SimpleRecordField("int", INT, EXACTLY_ONE));
        fields.add(new SimpleRecordField("boolean", BOOLEAN, EXACTLY_ONE));
        fields.add(new SimpleRecordField("byte array", BYTE_ARRAY, EXACTLY_ONE));
        fields.add(new SimpleRecordField("long", LONG, EXACTLY_ONE));
        fields.add(new SimpleRecordField("string", STRING, EXACTLY_ONE));
        fields.add(new SimpleRecordField("long string", LONG_STRING, EXACTLY_ONE));
        fields.add(new ComplexRecordField("complex", EXACTLY_ONE, new SimpleRecordField("key", STRING, EXACTLY_ONE), new SimpleRecordField("value", STRING, EXACTLY_ONE)));
        fields.add(new MapRecordField("map", new SimpleRecordField("key", STRING, EXACTLY_ONE), new SimpleRecordField("value", STRING, ZERO_OR_ONE), EXACTLY_ONE));
        fields.add(new UnionRecordField("union1", EXACTLY_ONE, Arrays.asList(new RecordField[]{ new SimpleRecordField("one", STRING, EXACTLY_ONE), new SimpleRecordField("two", INT, EXACTLY_ONE) })));
        fields.add(new UnionRecordField("union2", EXACTLY_ONE, Arrays.asList(new RecordField[]{ new SimpleRecordField("one", STRING, EXACTLY_ONE), new SimpleRecordField("two", INT, EXACTLY_ONE) })));
        final RecordSchema schema = new RecordSchema(fields);
        final SchemaRecordReader reader = SchemaRecordReader.fromSchema(schema);
        final byte[] buffer;
        try (final ByteArrayOutputStream baos = new ByteArrayOutputStream();final DataOutputStream dos = new DataOutputStream(baos)) {
            dos.write(1);// sentinel byte

            dos.writeInt(42);
            dos.writeBoolean(true);
            final byte[] array = "hello".getBytes();
            dos.writeInt(array.length);
            dos.write(array);
            dos.writeLong(42L);
            dos.writeUTF("hello");
            final String longString = "hello";
            final byte[] longStringArray = longString.getBytes(StandardCharsets.UTF_8);
            dos.writeInt(longStringArray.length);
            dos.write(longStringArray);
            dos.writeUTF("key");
            dos.writeUTF("value");
            dos.writeInt(2);
            dos.writeUTF("key1");
            dos.writeBoolean(true);
            dos.writeUTF("value1");
            dos.writeUTF("key2");
            dos.writeBoolean(false);
            dos.writeUTF("one");
            dos.writeUTF("hello");
            dos.writeUTF("two");
            dos.writeInt(42);
            buffer = baos.toByteArray();
        }
        try (final ByteArrayInputStream in = new ByteArrayInputStream(buffer)) {
            final Record record = reader.readRecord(in);
            Assert.assertNotNull(record);
            Assert.assertEquals(42, record.getFieldValue("int"));
            Assert.assertTrue(((boolean) (record.getFieldValue("boolean"))));
            Assert.assertTrue(Arrays.equals("hello".getBytes(), ((byte[]) (record.getFieldValue("byte array")))));
            Assert.assertEquals(42L, record.getFieldValue("long"));
            Assert.assertEquals("hello", record.getFieldValue("string"));
            Assert.assertEquals("hello", record.getFieldValue("long string"));
            final Record complexRecord = ((Record) (record.getFieldValue("complex")));
            Assert.assertEquals("key", complexRecord.getFieldValue(new SimpleRecordField("key", STRING, EXACTLY_ONE)));
            Assert.assertEquals("value", complexRecord.getFieldValue(new SimpleRecordField("value", STRING, EXACTLY_ONE)));
            final Map<String, String> map = new HashMap<>();
            map.put("key1", "value1");
            map.put("key2", null);
            Assert.assertEquals(map, record.getFieldValue("map"));
            Assert.assertEquals("hello", record.getFieldValue("union1"));
            Assert.assertEquals(42, record.getFieldValue("union2"));
        }
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testReadZeroOrOneFields() throws IOException {
        final List<RecordField> fields = new ArrayList<>();
        fields.add(new SimpleRecordField("int", INT, ZERO_OR_ONE));
        fields.add(new SimpleRecordField("int present", INT, ZERO_OR_ONE));
        fields.add(new SimpleRecordField("boolean", BOOLEAN, ZERO_OR_ONE));
        fields.add(new SimpleRecordField("boolean present", BOOLEAN, ZERO_OR_ONE));
        fields.add(new SimpleRecordField("byte array", BYTE_ARRAY, ZERO_OR_ONE));
        fields.add(new SimpleRecordField("byte array present", BYTE_ARRAY, ZERO_OR_ONE));
        fields.add(new SimpleRecordField("long", LONG, ZERO_OR_ONE));
        fields.add(new SimpleRecordField("long present", LONG, ZERO_OR_ONE));
        fields.add(new SimpleRecordField("string", STRING, ZERO_OR_ONE));
        fields.add(new SimpleRecordField("string present", STRING, ZERO_OR_ONE));
        fields.add(new SimpleRecordField("long string", LONG_STRING, ZERO_OR_ONE));
        fields.add(new SimpleRecordField("long string present", LONG_STRING, ZERO_OR_ONE));
        fields.add(new ComplexRecordField("complex", ZERO_OR_ONE, new SimpleRecordField("key", STRING, ZERO_OR_ONE), new SimpleRecordField("value", STRING, ZERO_OR_ONE)));
        fields.add(new ComplexRecordField("complex present", ZERO_OR_ONE, new SimpleRecordField("key", STRING, ZERO_OR_ONE), new SimpleRecordField("value", STRING, ZERO_OR_ONE)));
        fields.add(new MapRecordField("map", new SimpleRecordField("key", STRING, ZERO_OR_ONE), new SimpleRecordField("value", STRING, ZERO_OR_MORE), ZERO_OR_ONE));
        fields.add(new MapRecordField("map present", new SimpleRecordField("key", STRING, ZERO_OR_ONE), new SimpleRecordField("value", STRING, ZERO_OR_MORE), ZERO_OR_ONE));
        fields.add(new UnionRecordField("union", ZERO_OR_ONE, Arrays.asList(new RecordField[]{ new SimpleRecordField("one", STRING, EXACTLY_ONE), new SimpleRecordField("two", INT, EXACTLY_ONE) })));
        fields.add(new UnionRecordField("union present", ZERO_OR_ONE, Arrays.asList(new RecordField[]{ new SimpleRecordField("one", STRING, EXACTLY_ONE), new SimpleRecordField("two", INT, ZERO_OR_MORE) })));
        final RecordSchema schema = new RecordSchema(fields);
        final SchemaRecordReader reader = SchemaRecordReader.fromSchema(schema);
        // for each field, make the first one missing and the second one present.
        final byte[] buffer;
        try (final ByteArrayOutputStream baos = new ByteArrayOutputStream();final DataOutputStream dos = new DataOutputStream(baos)) {
            dos.write(1);// sentinel byte

            dos.write(0);
            dos.writeByte(1);
            dos.writeInt(42);
            dos.write(0);
            dos.writeByte(1);
            dos.writeBoolean(true);
            final byte[] array = "hello".getBytes();
            dos.write(0);
            dos.writeByte(1);
            dos.writeInt(array.length);
            dos.write(array);
            dos.write(0);
            dos.writeByte(1);
            dos.writeLong(42L);
            dos.write(0);
            dos.writeByte(1);
            dos.writeUTF("hello");
            final String longString = "hello";
            final byte[] longStringArray = longString.getBytes(StandardCharsets.UTF_8);
            dos.write(0);
            dos.writeByte(1);
            dos.writeInt(longStringArray.length);
            dos.write(longStringArray);
            dos.write(0);
            dos.writeByte(1);
            dos.writeByte(1);
            dos.writeUTF("key");
            dos.writeByte(0);
            dos.writeBoolean(false);// map not present

            dos.writeBoolean(true);// map present

            dos.writeInt(2);// 2 entries in the map

            dos.writeBoolean(true);// key present

            dos.writeUTF("key1");
            dos.writeInt(2);// 2 values

            dos.writeUTF("one");
            dos.writeUTF("two");
            dos.writeBoolean(false);// key not present

            dos.writeInt(1);
            dos.writeUTF("three");
            dos.writeBoolean(false);
            dos.writeBoolean(true);
            dos.writeUTF("two");
            dos.writeInt(3);// 3 entries

            dos.writeInt(1);
            dos.writeInt(2);
            dos.writeInt(3);
            buffer = baos.toByteArray();
        }
        try (final ByteArrayInputStream in = new ByteArrayInputStream(buffer)) {
            final Record record = reader.readRecord(in);
            Assert.assertNotNull(record);
            // Read everything into a map and make sure that no value is missing that has a name ending in " present"
            final Map<String, Object> valueMap = new HashMap<>();
            for (final RecordField field : record.getSchema().getFields()) {
                final Object value = record.getFieldValue(field);
                if (value == null) {
                    Assert.assertFalse(field.getFieldName().endsWith(" present"));
                    continue;
                }
                valueMap.put(field.getFieldName(), value);
            }
            Assert.assertEquals(42, valueMap.get("int present"));
            Assert.assertTrue(((boolean) (valueMap.get("boolean present"))));
            Assert.assertTrue(Arrays.equals("hello".getBytes(), ((byte[]) (valueMap.get("byte array present")))));
            Assert.assertEquals(42L, valueMap.get("long present"));
            Assert.assertEquals("hello", valueMap.get("string present"));
            Assert.assertEquals("hello", valueMap.get("long string present"));
            final Record complexRecord = ((Record) (valueMap.get("complex present")));
            Assert.assertEquals("key", complexRecord.getFieldValue(new SimpleRecordField("key", STRING, EXACTLY_ONE)));
            Assert.assertNull(complexRecord.getFieldValue(new SimpleRecordField("value", STRING, EXACTLY_ONE)));
            final Map<String, List<String>> map = ((Map<String, List<String>>) (valueMap.get("map present")));
            Assert.assertNotNull(map);
            Assert.assertEquals(2, map.size());
            Assert.assertTrue(map.containsKey(null));
            Assert.assertTrue(map.containsKey("key1"));
            final List<String> key1Values = Arrays.asList(new String[]{ "one", "two" });
            Assert.assertEquals(key1Values, map.get("key1"));
            final List<String> nullKeyValues = Arrays.asList(new String[]{ "three" });
            Assert.assertEquals(nullKeyValues, map.get(null));
            final List<Integer> unionValues = ((List<Integer>) (valueMap.get("union present")));
            Assert.assertEquals(3, unionValues.size());
            Assert.assertEquals(1, unionValues.get(0).intValue());
            Assert.assertEquals(2, unionValues.get(1).intValue());
            Assert.assertEquals(3, unionValues.get(2).intValue());
        }
    }
}

