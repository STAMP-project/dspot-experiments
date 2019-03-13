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
package org.apache.avro;


import Type.INT;
import Type.LONG;
import Type.NULL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.apache.avro.Schema.Field;
import org.junit.Assert;
import org.junit.Test;


public class TestSchema {
    @Test
    public void testSplitSchemaBuild() {
        Schema s = SchemaBuilder.record("HandshakeRequest").namespace("org.apache.avro.ipc").fields().name("clientProtocol").type().optional().stringType().name("meta").type().optional().map().values().bytesType().endRecord();
        String schemaString = s.toString();
        int mid = (schemaString.length()) / 2;
        Schema parsedStringSchema = new Schema.Parser().parse(s.toString());
        Schema parsedArrayOfStringSchema = new Schema.Parser().parse(schemaString.substring(0, mid), schemaString.substring(mid));
        Assert.assertNotNull(parsedStringSchema);
        Assert.assertNotNull(parsedArrayOfStringSchema);
        Assert.assertEquals(parsedStringSchema.toString(), parsedArrayOfStringSchema.toString());
    }

    @Test
    public void testDefaultRecordWithDuplicateFieldName() {
        String recordName = "name";
        Schema schema = Schema.createRecord(recordName, "doc", "namespace", false);
        List<Field> fields = new ArrayList<>();
        fields.add(new Field("field_name", Schema.create(NULL), null, null));
        fields.add(new Field("field_name", Schema.create(INT), null, null));
        try {
            schema.setFields(fields);
            Assert.fail("Should not be able to create a record with duplicate field name.");
        } catch (AvroRuntimeException are) {
            Assert.assertTrue(are.getMessage().contains(("Duplicate field field_name in record " + recordName)));
        }
    }

    @Test
    public void testCreateUnionVarargs() {
        List<Schema> types = new ArrayList<>();
        types.add(Schema.create(NULL));
        types.add(Schema.create(LONG));
        Schema expected = Schema.createUnion(types);
        Schema schema = Schema.createUnion(Schema.create(NULL), Schema.create(LONG));
        Assert.assertEquals(expected, schema);
    }

    @Test
    public void testRecordWithNullDoc() {
        Schema schema = Schema.createRecord("name", null, "namespace", false);
        String schemaString = schema.toString();
        Assert.assertNotNull(schemaString);
    }

    @Test
    public void testRecordWithNullNamespace() {
        Schema schema = Schema.createRecord("name", "doc", null, false);
        String schemaString = schema.toString();
        Assert.assertNotNull(schemaString);
    }

    @Test
    public void testEmptyRecordSchema() {
        Schema schema = createDefaultRecord();
        String schemaString = schema.toString();
        Assert.assertNotNull(schemaString);
    }

    @Test(expected = SchemaParseException.class)
    public void testParseEmptySchema() {
        Schema schema = new Schema.Parser().parse("");
    }

    @Test
    public void testSchemaWithFields() {
        List<Field> fields = new ArrayList<>();
        fields.add(new Field("field_name1", Schema.create(NULL), null, null));
        fields.add(new Field("field_name2", Schema.create(INT), null, null));
        Schema schema = createDefaultRecord();
        schema.setFields(fields);
        String schemaString = schema.toString();
        Assert.assertNotNull(schemaString);
        Assert.assertEquals(2, schema.getFields().size());
    }

    @Test(expected = NullPointerException.class)
    public void testSchemaWithNullFields() {
        Schema.createRecord("name", "doc", "namespace", false, null);
    }

    @Test
    public void testIsUnionOnUnionWithMultipleElements() {
        Schema schema = Schema.createUnion(Schema.create(NULL), Schema.create(LONG));
        Assert.assertTrue(schema.isUnion());
    }

    @Test
    public void testIsUnionOnUnionWithOneElement() {
        Schema schema = Schema.createUnion(Schema.create(LONG));
        Assert.assertTrue(schema.isUnion());
    }

    @Test
    public void testIsUnionOnRecord() {
        Schema schema = createDefaultRecord();
        Assert.assertFalse(schema.isUnion());
    }

    @Test
    public void testIsUnionOnArray() {
        Schema schema = Schema.createArray(Schema.create(LONG));
        Assert.assertFalse(schema.isUnion());
    }

    @Test
    public void testIsUnionOnEnum() {
        Schema schema = Schema.createEnum("name", "doc", "namespace", Collections.singletonList("value"));
        Assert.assertFalse(schema.isUnion());
    }

    @Test
    public void testIsUnionOnFixed() {
        Schema schema = Schema.createFixed("name", "doc", "space", 10);
        Assert.assertFalse(schema.isUnion());
    }

    @Test
    public void testIsUnionOnMap() {
        Schema schema = Schema.createMap(Schema.create(LONG));
        Assert.assertFalse(schema.isUnion());
    }

    @Test
    public void testIsNullableOnUnionWithNull() {
        Schema schema = Schema.createUnion(Schema.create(NULL), Schema.create(LONG));
        Assert.assertTrue(schema.isNullable());
    }

    @Test
    public void testIsNullableOnUnionWithoutNull() {
        Schema schema = Schema.createUnion(Schema.create(LONG));
        Assert.assertFalse(schema.isNullable());
    }

    @Test
    public void testIsNullableOnRecord() {
        Schema schema = createDefaultRecord();
        Assert.assertFalse(schema.isNullable());
    }
}

