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
package org.apache.nifi.schema.access;


import SchemaNameAsAttribute.SCHEMA_BRANCH_ATTRIBUTE;
import SchemaNameAsAttribute.SCHEMA_NAME_ATTRIBUTE;
import SchemaNameAsAttribute.SCHEMA_VERSION_ATTRIBUTE;
import java.util.List;
import java.util.Map;
import org.apache.nifi.serialization.record.RecordField;
import org.apache.nifi.serialization.record.RecordSchema;
import org.apache.nifi.serialization.record.SchemaIdentifier;
import org.junit.Assert;
import org.junit.Test;


public class TestSchemaNameAsAttribute {
    private List<RecordField> fields;

    private SchemaAccessWriter schemaAccessWriter;

    @Test
    public void testWriteNameBranchAndVersion() {
        final SchemaIdentifier schemaIdentifier = SchemaIdentifier.builder().name("person").branch("master").version(1).id(1L).build();
        final RecordSchema schema = new org.apache.nifi.serialization.SimpleRecordSchema(fields, schemaIdentifier);
        final Map<String, String> attributes = schemaAccessWriter.getAttributes(schema);
        Assert.assertEquals(3, attributes.size());
        Assert.assertEquals(schemaIdentifier.getName().get(), attributes.get(SCHEMA_NAME_ATTRIBUTE));
        Assert.assertEquals(schemaIdentifier.getBranch().get(), attributes.get(SCHEMA_BRANCH_ATTRIBUTE));
        Assert.assertEquals(String.valueOf(schemaIdentifier.getVersion().getAsInt()), attributes.get(SCHEMA_VERSION_ATTRIBUTE));
    }

    @Test
    public void testWriteOnlyName() {
        final SchemaIdentifier schemaIdentifier = SchemaIdentifier.builder().name("person").id(1L).build();
        final RecordSchema schema = new org.apache.nifi.serialization.SimpleRecordSchema(fields, schemaIdentifier);
        final Map<String, String> attributes = schemaAccessWriter.getAttributes(schema);
        Assert.assertEquals(1, attributes.size());
        Assert.assertEquals(schemaIdentifier.getName().get(), attributes.get(SCHEMA_NAME_ATTRIBUTE));
    }

    @Test
    public void testValidateSchemaWhenValid() throws SchemaNotFoundException {
        final SchemaIdentifier schemaIdentifier = SchemaIdentifier.builder().name("person").id(1L).build();
        final RecordSchema schema = new org.apache.nifi.serialization.SimpleRecordSchema(fields, schemaIdentifier);
        schemaAccessWriter.validateSchema(schema);
    }

    @Test(expected = SchemaNotFoundException.class)
    public void testValidateSchemaWhenNoIdentifier() throws SchemaNotFoundException {
        final RecordSchema schema = new org.apache.nifi.serialization.SimpleRecordSchema(fields, null);
        schemaAccessWriter.validateSchema(schema);
    }

    @Test(expected = SchemaNotFoundException.class)
    public void testValidateSchemaWhenNoName() throws SchemaNotFoundException {
        final SchemaIdentifier schemaIdentifier = SchemaIdentifier.builder().id(1L).build();
        final RecordSchema schema = new org.apache.nifi.serialization.SimpleRecordSchema(fields, schemaIdentifier);
        schemaAccessWriter.validateSchema(schema);
    }
}

