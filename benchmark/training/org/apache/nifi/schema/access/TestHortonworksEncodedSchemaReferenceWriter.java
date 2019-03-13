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
import java.util.Collections;
import org.apache.nifi.serialization.record.RecordSchema;
import org.apache.nifi.serialization.record.SchemaIdentifier;
import org.junit.Assert;
import org.junit.Test;


public class TestHortonworksEncodedSchemaReferenceWriter {
    @Test
    public void testHeader() throws IOException {
        final HortonworksEncodedSchemaReferenceWriter writer = new HortonworksEncodedSchemaReferenceWriter();
        final RecordSchema schema = new org.apache.nifi.serialization.SimpleRecordSchema(Collections.emptyList(), SchemaIdentifier.builder().name("name").id(48L).version(2).build());
        final byte[] header;
        try (final ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            writer.writeHeader(schema, baos);
            header = baos.toByteArray();
        }
        try (final DataInputStream dis = new DataInputStream(new ByteArrayInputStream(header))) {
            Assert.assertEquals(1, dis.read());// verify 'protocol version'

            Assert.assertEquals(48, dis.readLong());// verify schema id

            Assert.assertEquals(2, dis.readInt());// verify schema version

            Assert.assertEquals((-1), dis.read());// no more bytes

        }
    }
}

