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
package org.apache.nifi.avro;


import RecordFieldType.ARRAY;
import RecordFieldType.BOOLEAN;
import RecordFieldType.BYTE;
import RecordFieldType.DOUBLE;
import RecordFieldType.FLOAT;
import RecordFieldType.INT;
import RecordFieldType.LONG;
import RecordFieldType.MAP;
import RecordFieldType.RECORD;
import RecordFieldType.STRING;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.avro.Schema;
import org.apache.avro.generic.GenericRecord;
import org.apache.nifi.serialization.RecordSetWriter;
import org.apache.nifi.serialization.WriteResult;
import org.apache.nifi.serialization.record.DataType;
import org.apache.nifi.serialization.record.Record;
import org.apache.nifi.serialization.record.RecordField;
import org.apache.nifi.serialization.record.RecordSchema;
import org.apache.nifi.serialization.record.RecordSet;
import org.junit.Test;


public abstract class TestWriteAvroResult {
    @Test
    public void testLogicalTypes() throws IOException, ParseException {
        final Schema schema = new Schema.Parser().parse(new File("src/test/resources/avro/logical-types.avsc"));
        testLogicalTypes(schema);
    }

    @Test
    public void testNullableLogicalTypes() throws IOException, ParseException {
        final Schema schema = new Schema.Parser().parse(new File("src/test/resources/avro/logical-types-nullable.avsc"));
        testLogicalTypes(schema);
    }

    @Test
    public void testDataTypes() throws IOException {
        final Schema schema = new Schema.Parser().parse(new File("src/test/resources/avro/datatypes.avsc"));
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        final List<RecordField> subRecordFields = Collections.singletonList(new RecordField("field1", STRING.getDataType()));
        final RecordSchema subRecordSchema = new org.apache.nifi.serialization.SimpleRecordSchema(subRecordFields);
        final DataType subRecordDataType = RECORD.getRecordDataType(subRecordSchema);
        final List<RecordField> fields = new ArrayList<>();
        fields.add(new RecordField("string", STRING.getDataType()));
        fields.add(new RecordField("int", INT.getDataType()));
        fields.add(new RecordField("long", LONG.getDataType()));
        fields.add(new RecordField("double", DOUBLE.getDataType()));
        fields.add(new RecordField("float", FLOAT.getDataType()));
        fields.add(new RecordField("boolean", BOOLEAN.getDataType()));
        fields.add(new RecordField("bytes", ARRAY.getArrayDataType(BYTE.getDataType())));
        fields.add(new RecordField("nullOrLong", LONG.getDataType()));
        fields.add(new RecordField("array", ARRAY.getArrayDataType(INT.getDataType())));
        fields.add(new RecordField("record", subRecordDataType));
        fields.add(new RecordField("map", MAP.getMapDataType(subRecordDataType)));
        final RecordSchema recordSchema = new org.apache.nifi.serialization.SimpleRecordSchema(fields);
        final Record innerRecord = new org.apache.nifi.serialization.record.MapRecord(subRecordSchema, Collections.singletonMap("field1", "hello"));
        final Map<String, Object> innerMap = new HashMap<>();
        innerMap.put("key1", innerRecord);
        final Map<String, Object> values = new HashMap<>();
        values.put("string", "hello");
        values.put("int", 8);
        values.put("long", 42L);
        values.put("double", 3.14159);
        values.put("float", 1.23456F);
        values.put("boolean", true);
        values.put("bytes", AvroTypeUtil.convertByteArray("hello".getBytes()));
        values.put("nullOrLong", null);
        values.put("array", new Integer[]{ 1, 2, 3 });
        values.put("record", innerRecord);
        values.put("map", innerMap);
        final Record record = new org.apache.nifi.serialization.record.MapRecord(recordSchema, values);
        final WriteResult writeResult;
        try (final RecordSetWriter writer = createWriter(schema, baos)) {
            writeResult = writer.write(RecordSet.of(record.getSchema(), record));
        }
        verify(writeResult);
        final byte[] data = baos.toByteArray();
        try (final InputStream in = new ByteArrayInputStream(data)) {
            final GenericRecord avroRecord = readRecord(in, schema);
            assertMatch(record, avroRecord);
        }
    }
}

