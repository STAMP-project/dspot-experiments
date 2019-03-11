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
package org.apache.nifi.provenance;


import EventFieldNames.CONTENT_CLAIM;
import ProvenanceEventType.RECEIVE;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Consumer;
import org.apache.nifi.provenance.schema.EventFieldNames;
import org.apache.nifi.provenance.schema.ProvenanceEventSchema;
import org.apache.nifi.provenance.serialization.RecordReader;
import org.apache.nifi.provenance.toc.StandardTocReader;
import org.apache.nifi.provenance.toc.StandardTocWriter;
import org.apache.nifi.provenance.toc.TocReader;
import org.apache.nifi.provenance.toc.TocWriter;
import org.apache.nifi.repository.schema.FieldType;
import org.apache.nifi.repository.schema.Record;
import org.apache.nifi.repository.schema.RecordField;
import org.apache.nifi.repository.schema.RecordSchema;
import org.apache.nifi.repository.schema.Repetition;
import org.junit.Assert;
import org.junit.Test;


public class TestSchemaRecordReaderWriter extends AbstractTestRecordReaderWriter {
    private final AtomicLong idGenerator = new AtomicLong(0L);

    private File journalFile;

    private File tocFile;

    @Test
    public void testFieldAddedToSchema() throws IOException {
        final RecordField unitTestField = new org.apache.nifi.repository.schema.SimpleRecordField("Unit Test Field", FieldType.STRING, Repetition.EXACTLY_ONE);
        final Consumer<List<RecordField>> schemaModifier = ( fields) -> fields.add(unitTestField);
        final Map<RecordField, Object> toAdd = new HashMap<>();
        toAdd.put(unitTestField, "hello");
        try (final ByteArraySchemaRecordWriter writer = createSchemaWriter(schemaModifier, toAdd)) {
            writer.writeHeader(1L);
            writer.writeRecord(createEvent());
            writer.writeRecord(createEvent());
        }
        try (final InputStream in = new FileInputStream(journalFile);final TocReader tocReader = new StandardTocReader(tocFile);final RecordReader reader = createReader(in, journalFile.getName(), tocReader, 10000)) {
            for (int i = 0; i < 2; i++) {
                final StandardProvenanceEventRecord event = reader.nextRecord();
                Assert.assertNotNull(event);
                Assert.assertEquals("1234", event.getComponentId());
                Assert.assertEquals(RECEIVE, event.getEventType());
                Assert.assertNotNull(event.getUpdatedAttributes());
                Assert.assertFalse(event.getUpdatedAttributes().isEmpty());
            }
        }
    }

    @Test
    public void testFieldRemovedFromSchema() throws IOException {
        final TocWriter tocWriter = new StandardTocWriter(tocFile, false, false);
        try {
            // Create a schema that has the fields modified
            final RecordSchema schemaV1 = ProvenanceEventSchema.PROVENANCE_EVENT_SCHEMA_V1;
            final List<RecordField> fields = new java.util.ArrayList(schemaV1.getFields());
            fields.remove(new org.apache.nifi.repository.schema.SimpleRecordField(EventFieldNames.UPDATED_ATTRIBUTES, FieldType.STRING, Repetition.EXACTLY_ONE));
            fields.remove(new org.apache.nifi.repository.schema.SimpleRecordField(EventFieldNames.PREVIOUS_ATTRIBUTES, FieldType.STRING, Repetition.EXACTLY_ONE));
            final RecordSchema recordSchema = new RecordSchema(fields);
            // Create a record writer whose schema does not contain updated attributes or previous attributes.
            // This means that we must also override the method that writes out attributes so that we are able
            // to avoid actually writing them out.
            final ByteArraySchemaRecordWriter writer = new ByteArraySchemaRecordWriter(journalFile, idGenerator, tocWriter, false, 0) {
                @Override
                public void writeHeader(long firstEventId, DataOutputStream out) throws IOException {
                    final ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    recordSchema.writeTo(baos);
                    out.writeInt(baos.size());
                    baos.writeTo(out);
                }

                @Override
                protected Record createRecord(final ProvenanceEventRecord event, final long eventId) {
                    final RecordSchema contentClaimSchema = new RecordSchema(recordSchema.getField(CONTENT_CLAIM).getSubFields());
                    return new org.apache.nifi.provenance.schema.EventRecord(event, eventId, recordSchema, contentClaimSchema);
                }
            };
            try {
                writer.writeHeader(1L);
                writer.writeRecord(createEvent());
                writer.writeRecord(createEvent());
            } finally {
                writer.close();
            }
        } finally {
            tocWriter.close();
        }
        // Read the records in and make sure that they have the info that we expect.
        try (final InputStream in = new FileInputStream(journalFile);final TocReader tocReader = new StandardTocReader(tocFile);final RecordReader reader = createReader(in, journalFile.getName(), tocReader, 10000)) {
            for (int i = 0; i < 2; i++) {
                final StandardProvenanceEventRecord event = reader.nextRecord();
                Assert.assertNotNull(event);
                Assert.assertEquals(RECEIVE, event.getEventType());
                // We will still have a Map<String, String> for updated attributes because the
                // Provenance Event Builder will create an empty map.
                Assert.assertNotNull(event.getUpdatedAttributes());
                Assert.assertTrue(event.getUpdatedAttributes().isEmpty());
            }
        }
    }

    @Test
    public void testAddOneRecordReadTwice() throws IOException {
        final RecordField unitTestField = new org.apache.nifi.repository.schema.SimpleRecordField("Unit Test Field", FieldType.STRING, Repetition.EXACTLY_ONE);
        final Consumer<List<RecordField>> schemaModifier = ( fields) -> fields.add(unitTestField);
        final Map<RecordField, Object> toAdd = new HashMap<>();
        toAdd.put(unitTestField, "hello");
        try (final ByteArraySchemaRecordWriter writer = createSchemaWriter(schemaModifier, toAdd)) {
            writer.writeHeader(1L);
            writer.writeRecord(createEvent());
        }
        try (final InputStream in = new FileInputStream(journalFile);final TocReader tocReader = new StandardTocReader(tocFile);final RecordReader reader = createReader(in, journalFile.getName(), tocReader, 10000)) {
            final ProvenanceEventRecord firstEvent = reader.nextRecord();
            Assert.assertNotNull(firstEvent);
            final ProvenanceEventRecord secondEvent = reader.nextRecord();
            Assert.assertNull(secondEvent);
        }
    }
}

