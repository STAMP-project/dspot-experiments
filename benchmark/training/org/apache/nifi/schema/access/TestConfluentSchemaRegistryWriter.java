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


import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.IOException;
import org.apache.nifi.serialization.record.RecordSchema;
import org.apache.nifi.serialization.record.SchemaIdentifier;
import org.junit.Assert;
import org.junit.Test;


public class TestConfluentSchemaRegistryWriter {
    @Test
    public void testValidateValidSchema() throws SchemaNotFoundException {
        final SchemaIdentifier schemaIdentifier = SchemaIdentifier.builder().id(123456L).version(2).build();
        final RecordSchema recordSchema = createRecordSchema(schemaIdentifier);
        final SchemaAccessWriter schemaAccessWriter = new ConfluentSchemaRegistryWriter();
        schemaAccessWriter.validateSchema(recordSchema);
    }

    @Test(expected = SchemaNotFoundException.class)
    public void testValidateInvalidSchema() throws SchemaNotFoundException {
        final SchemaIdentifier schemaIdentifier = SchemaIdentifier.builder().name("test").build();
        final RecordSchema recordSchema = createRecordSchema(schemaIdentifier);
        final SchemaAccessWriter schemaAccessWriter = new ConfluentSchemaRegistryWriter();
        schemaAccessWriter.validateSchema(recordSchema);
    }

    @Test
    public void testWriteHeader() throws IOException {
        final SchemaIdentifier schemaIdentifier = SchemaIdentifier.builder().id(123456L).version(2).build();
        final RecordSchema recordSchema = createRecordSchema(schemaIdentifier);
        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        final SchemaAccessWriter schemaAccessWriter = new ConfluentSchemaRegistryWriter();
        schemaAccessWriter.writeHeader(recordSchema, out);
        try (final ByteArrayInputStream bytesIn = new ByteArrayInputStream(out.toByteArray());final DataInputStream in = new DataInputStream(bytesIn)) {
            Assert.assertEquals(0, in.readByte());
            Assert.assertEquals(((int) (schemaIdentifier.getIdentifier().getAsLong())), in.readInt());
        }
    }
}

