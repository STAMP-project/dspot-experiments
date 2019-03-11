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


import HortonworksAttributeSchemaReferenceStrategy.SCHEMA_ID_ATTRIBUTE;
import HortonworksAttributeSchemaReferenceStrategy.SCHEMA_PROTOCOL_VERSION_ATTRIBUTE;
import HortonworksAttributeSchemaReferenceStrategy.SCHEMA_VERSION_ATTRIBUTE;
import HortonworksAttributeSchemaReferenceWriter.LATEST_PROTOCOL_VERSION;
import HortonworksAttributeSchemaReferenceWriter.SCHEMA_BRANCH_ATTRIBUTE;
import java.util.Map;
import org.apache.nifi.serialization.record.RecordSchema;
import org.apache.nifi.serialization.record.SchemaIdentifier;
import org.junit.Assert;
import org.junit.Test;


public class TestHortonworksAttributeSchemaReferenceWriter {
    @Test
    public void testValidateWithValidSchema() throws SchemaNotFoundException {
        final SchemaIdentifier schemaIdentifier = SchemaIdentifier.builder().id(123456L).version(2).build();
        final RecordSchema recordSchema = createRecordSchema(schemaIdentifier);
        final SchemaAccessWriter schemaAccessWriter = new HortonworksAttributeSchemaReferenceWriter();
        schemaAccessWriter.validateSchema(recordSchema);
    }

    @Test(expected = SchemaNotFoundException.class)
    public void testValidateWithInvalidSchema() throws SchemaNotFoundException {
        final SchemaIdentifier schemaIdentifier = SchemaIdentifier.builder().name("test").build();
        final RecordSchema recordSchema = createRecordSchema(schemaIdentifier);
        final SchemaAccessWriter schemaAccessWriter = new HortonworksAttributeSchemaReferenceWriter();
        schemaAccessWriter.validateSchema(recordSchema);
    }

    @Test
    public void testGetAttributesWithoutBranch() {
        final SchemaIdentifier schemaIdentifier = SchemaIdentifier.builder().id(123456L).version(2).build();
        final RecordSchema recordSchema = createRecordSchema(schemaIdentifier);
        final SchemaAccessWriter schemaAccessWriter = new HortonworksAttributeSchemaReferenceWriter();
        final Map<String, String> attributes = schemaAccessWriter.getAttributes(recordSchema);
        Assert.assertEquals(3, attributes.size());
        Assert.assertEquals(String.valueOf(schemaIdentifier.getIdentifier().getAsLong()), attributes.get(SCHEMA_ID_ATTRIBUTE));
        Assert.assertEquals(String.valueOf(schemaIdentifier.getVersion().getAsInt()), attributes.get(SCHEMA_VERSION_ATTRIBUTE));
        Assert.assertEquals(String.valueOf(LATEST_PROTOCOL_VERSION), attributes.get(SCHEMA_PROTOCOL_VERSION_ATTRIBUTE));
    }

    @Test
    public void testGetAttributesWithBranch() {
        final SchemaIdentifier schemaIdentifier = SchemaIdentifier.builder().id(123456L).version(2).branch("foo").build();
        final RecordSchema recordSchema = createRecordSchema(schemaIdentifier);
        final SchemaAccessWriter schemaAccessWriter = new HortonworksAttributeSchemaReferenceWriter();
        final Map<String, String> attributes = schemaAccessWriter.getAttributes(recordSchema);
        Assert.assertEquals(4, attributes.size());
        Assert.assertEquals(String.valueOf(schemaIdentifier.getIdentifier().getAsLong()), attributes.get(SCHEMA_ID_ATTRIBUTE));
        Assert.assertEquals(String.valueOf(schemaIdentifier.getVersion().getAsInt()), attributes.get(SCHEMA_VERSION_ATTRIBUTE));
        Assert.assertEquals(String.valueOf(LATEST_PROTOCOL_VERSION), attributes.get(SCHEMA_PROTOCOL_VERSION_ATTRIBUTE));
        Assert.assertEquals("foo", attributes.get(SCHEMA_BRANCH_ATTRIBUTE));
    }
}

