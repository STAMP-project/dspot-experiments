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


import ProvenanceEventType.RECEIVE;
import StandardProvenanceEventRecord.Builder;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;
import org.apache.nifi.provenance.serialization.RecordReader;
import org.apache.nifi.provenance.serialization.RecordWriter;
import org.apache.nifi.provenance.toc.StandardTocReader;
import org.apache.nifi.provenance.toc.StandardTocWriter;
import org.apache.nifi.provenance.toc.TocReader;
import org.apache.nifi.provenance.toc.TocUtil;
import org.apache.nifi.provenance.toc.TocWriter;
import org.apache.nifi.util.file.FileUtils;
import org.junit.Assert;
import org.junit.Test;


public class TestEventIdFirstSchemaRecordReaderWriter extends AbstractTestRecordReaderWriter {
    private final AtomicLong idGenerator = new AtomicLong(0L);

    private File journalFile;

    private File tocFile;

    @Test
    public void testContentClaimUnchanged() throws IOException {
        final File journalFile = new File((("target/storage/" + (UUID.randomUUID().toString())) + "/testSimpleWrite.gz"));
        final File tocFile = TocUtil.getTocFile(journalFile);
        final TocWriter tocWriter = new StandardTocWriter(tocFile, false, false);
        final RecordWriter writer = createWriter(journalFile, tocWriter, true, 8192);
        final Map<String, String> attributes = new HashMap<>();
        attributes.put("filename", "1.txt");
        attributes.put("uuid", UUID.randomUUID().toString());
        final ProvenanceEventBuilder builder = new StandardProvenanceEventRecord.Builder();
        builder.setEventTime(System.currentTimeMillis());
        builder.setEventType(RECEIVE);
        builder.setTransitUri("nifi://unit-test");
        builder.fromFlowFile(TestUtil.createFlowFile(3L, 3000L, attributes));
        builder.setComponentId("1234");
        builder.setComponentType("dummy processor");
        builder.setPreviousContentClaim("container-1", "section-1", "identifier-1", 1L, 1L);
        builder.setCurrentContentClaim("container-1", "section-1", "identifier-1", 1L, 1L);
        final ProvenanceEventRecord record = builder.build();
        writer.writeHeader(1L);
        writer.writeRecord(record);
        writer.close();
        final TocReader tocReader = new StandardTocReader(tocFile);
        try (final FileInputStream fis = new FileInputStream(journalFile);final RecordReader reader = createReader(fis, journalFile.getName(), tocReader, 2048)) {
            Assert.assertEquals(0, reader.getBlockIndex());
            reader.skipToBlock(0);
            final StandardProvenanceEventRecord recovered = reader.nextRecord();
            Assert.assertNotNull(recovered);
            Assert.assertEquals("nifi://unit-test", recovered.getTransitUri());
            Assert.assertEquals("container-1", recovered.getPreviousContentClaimContainer());
            Assert.assertEquals("container-1", recovered.getContentClaimContainer());
            Assert.assertEquals("section-1", recovered.getPreviousContentClaimSection());
            Assert.assertEquals("section-1", recovered.getContentClaimSection());
            Assert.assertEquals("identifier-1", recovered.getPreviousContentClaimIdentifier());
            Assert.assertEquals("identifier-1", recovered.getContentClaimIdentifier());
            Assert.assertEquals(1L, recovered.getPreviousContentClaimOffset().longValue());
            Assert.assertEquals(1L, recovered.getContentClaimOffset().longValue());
            Assert.assertEquals(1L, recovered.getPreviousFileSize().longValue());
            Assert.assertEquals(1L, recovered.getContentClaimOffset().longValue());
            Assert.assertNull(reader.nextRecord());
        }
        FileUtils.deleteFile(journalFile.getParentFile(), true);
    }

    @Test
    public void testContentClaimRemoved() throws IOException {
        final File journalFile = new File((("target/storage/" + (UUID.randomUUID().toString())) + "/testSimpleWrite.gz"));
        final File tocFile = TocUtil.getTocFile(journalFile);
        final TocWriter tocWriter = new StandardTocWriter(tocFile, false, false);
        final RecordWriter writer = createWriter(journalFile, tocWriter, true, 8192);
        final Map<String, String> attributes = new HashMap<>();
        attributes.put("filename", "1.txt");
        attributes.put("uuid", UUID.randomUUID().toString());
        final ProvenanceEventBuilder builder = new StandardProvenanceEventRecord.Builder();
        builder.setEventTime(System.currentTimeMillis());
        builder.setEventType(RECEIVE);
        builder.setTransitUri("nifi://unit-test");
        builder.fromFlowFile(TestUtil.createFlowFile(3L, 3000L, attributes));
        builder.setComponentId("1234");
        builder.setComponentType("dummy processor");
        builder.setPreviousContentClaim("container-1", "section-1", "identifier-1", 1L, 1L);
        builder.setCurrentContentClaim(null, null, null, 0L, 0L);
        final ProvenanceEventRecord record = builder.build();
        writer.writeHeader(1L);
        writer.writeRecord(record);
        writer.close();
        final TocReader tocReader = new StandardTocReader(tocFile);
        try (final FileInputStream fis = new FileInputStream(journalFile);final RecordReader reader = createReader(fis, journalFile.getName(), tocReader, 2048)) {
            Assert.assertEquals(0, reader.getBlockIndex());
            reader.skipToBlock(0);
            final StandardProvenanceEventRecord recovered = reader.nextRecord();
            Assert.assertNotNull(recovered);
            Assert.assertEquals("nifi://unit-test", recovered.getTransitUri());
            Assert.assertEquals("container-1", recovered.getPreviousContentClaimContainer());
            Assert.assertNull(recovered.getContentClaimContainer());
            Assert.assertEquals("section-1", recovered.getPreviousContentClaimSection());
            Assert.assertNull(recovered.getContentClaimSection());
            Assert.assertEquals("identifier-1", recovered.getPreviousContentClaimIdentifier());
            Assert.assertNull(recovered.getContentClaimIdentifier());
            Assert.assertEquals(1L, recovered.getPreviousContentClaimOffset().longValue());
            Assert.assertNull(recovered.getContentClaimOffset());
            Assert.assertEquals(1L, recovered.getPreviousFileSize().longValue());
            Assert.assertEquals(0L, recovered.getFileSize());
            Assert.assertNull(reader.nextRecord());
        }
        FileUtils.deleteFile(journalFile.getParentFile(), true);
    }

    @Test
    public void testContentClaimAdded() throws IOException {
        final File journalFile = new File((("target/storage/" + (UUID.randomUUID().toString())) + "/testSimpleWrite.gz"));
        final File tocFile = TocUtil.getTocFile(journalFile);
        final TocWriter tocWriter = new StandardTocWriter(tocFile, false, false);
        final RecordWriter writer = createWriter(journalFile, tocWriter, true, 8192);
        final Map<String, String> attributes = new HashMap<>();
        attributes.put("filename", "1.txt");
        attributes.put("uuid", UUID.randomUUID().toString());
        final ProvenanceEventBuilder builder = new StandardProvenanceEventRecord.Builder();
        builder.setEventTime(System.currentTimeMillis());
        builder.setEventType(RECEIVE);
        builder.setTransitUri("nifi://unit-test");
        builder.fromFlowFile(TestUtil.createFlowFile(3L, 3000L, attributes));
        builder.setComponentId("1234");
        builder.setComponentType("dummy processor");
        builder.setCurrentContentClaim("container-1", "section-1", "identifier-1", 1L, 1L);
        final ProvenanceEventRecord record = builder.build();
        writer.writeHeader(1L);
        writer.writeRecord(record);
        writer.close();
        final TocReader tocReader = new StandardTocReader(tocFile);
        try (final FileInputStream fis = new FileInputStream(journalFile);final RecordReader reader = createReader(fis, journalFile.getName(), tocReader, 2048)) {
            Assert.assertEquals(0, reader.getBlockIndex());
            reader.skipToBlock(0);
            final StandardProvenanceEventRecord recovered = reader.nextRecord();
            Assert.assertNotNull(recovered);
            Assert.assertEquals("nifi://unit-test", recovered.getTransitUri());
            Assert.assertEquals("container-1", recovered.getContentClaimContainer());
            Assert.assertNull(recovered.getPreviousContentClaimContainer());
            Assert.assertEquals("section-1", recovered.getContentClaimSection());
            Assert.assertNull(recovered.getPreviousContentClaimSection());
            Assert.assertEquals("identifier-1", recovered.getContentClaimIdentifier());
            Assert.assertNull(recovered.getPreviousContentClaimIdentifier());
            Assert.assertEquals(1L, recovered.getContentClaimOffset().longValue());
            Assert.assertNull(recovered.getPreviousContentClaimOffset());
            Assert.assertEquals(1L, recovered.getFileSize());
            Assert.assertNull(recovered.getPreviousContentClaimOffset());
            Assert.assertNull(reader.nextRecord());
        }
        FileUtils.deleteFile(journalFile.getParentFile(), true);
    }

    @Test
    public void testContentClaimChanged() throws IOException {
        final File journalFile = new File((("target/storage/" + (UUID.randomUUID().toString())) + "/testSimpleWrite.gz"));
        final File tocFile = TocUtil.getTocFile(journalFile);
        final TocWriter tocWriter = new StandardTocWriter(tocFile, false, false);
        final RecordWriter writer = createWriter(journalFile, tocWriter, true, 8192);
        final Map<String, String> attributes = new HashMap<>();
        attributes.put("filename", "1.txt");
        attributes.put("uuid", UUID.randomUUID().toString());
        final ProvenanceEventBuilder builder = new StandardProvenanceEventRecord.Builder();
        builder.setEventTime(System.currentTimeMillis());
        builder.setEventType(RECEIVE);
        builder.setTransitUri("nifi://unit-test");
        builder.fromFlowFile(TestUtil.createFlowFile(3L, 3000L, attributes));
        builder.setComponentId("1234");
        builder.setComponentType("dummy processor");
        builder.setPreviousContentClaim("container-1", "section-1", "identifier-1", 1L, 1L);
        builder.setCurrentContentClaim("container-2", "section-2", "identifier-2", 2L, 2L);
        final ProvenanceEventRecord record = builder.build();
        writer.writeHeader(1L);
        writer.writeRecord(record);
        writer.close();
        final TocReader tocReader = new StandardTocReader(tocFile);
        try (final FileInputStream fis = new FileInputStream(journalFile);final RecordReader reader = createReader(fis, journalFile.getName(), tocReader, 2048)) {
            Assert.assertEquals(0, reader.getBlockIndex());
            reader.skipToBlock(0);
            final StandardProvenanceEventRecord recovered = reader.nextRecord();
            Assert.assertNotNull(recovered);
            Assert.assertEquals("nifi://unit-test", recovered.getTransitUri());
            Assert.assertEquals("container-1", recovered.getPreviousContentClaimContainer());
            Assert.assertEquals("container-2", recovered.getContentClaimContainer());
            Assert.assertEquals("section-1", recovered.getPreviousContentClaimSection());
            Assert.assertEquals("section-2", recovered.getContentClaimSection());
            Assert.assertEquals("identifier-1", recovered.getPreviousContentClaimIdentifier());
            Assert.assertEquals("identifier-2", recovered.getContentClaimIdentifier());
            Assert.assertEquals(1L, recovered.getPreviousContentClaimOffset().longValue());
            Assert.assertEquals(2L, recovered.getContentClaimOffset().longValue());
            Assert.assertEquals(1L, recovered.getPreviousFileSize().longValue());
            Assert.assertEquals(2L, recovered.getContentClaimOffset().longValue());
            Assert.assertNull(reader.nextRecord());
        }
        FileUtils.deleteFile(journalFile.getParentFile(), true);
    }

    @Test
    public void testEventIdAndTimestampCorrect() throws IOException {
        final File journalFile = new File((("target/storage/" + (UUID.randomUUID().toString())) + "/testSimpleWrite.gz"));
        final File tocFile = TocUtil.getTocFile(journalFile);
        final TocWriter tocWriter = new StandardTocWriter(tocFile, false, false);
        final RecordWriter writer = createWriter(journalFile, tocWriter, true, 8192);
        final Map<String, String> attributes = new HashMap<>();
        attributes.put("filename", "1.txt");
        attributes.put("uuid", UUID.randomUUID().toString());
        final long timestamp = (System.currentTimeMillis()) - 10000L;
        final StandardProvenanceEventRecord.Builder builder = new StandardProvenanceEventRecord.Builder();
        builder.setEventId(1000000);
        builder.setEventTime(timestamp);
        builder.setEventType(RECEIVE);
        builder.setTransitUri("nifi://unit-test");
        builder.fromFlowFile(TestUtil.createFlowFile(3L, 3000L, attributes));
        builder.setComponentId("1234");
        builder.setComponentType("dummy processor");
        builder.setPreviousContentClaim("container-1", "section-1", "identifier-1", 1L, 1L);
        builder.setCurrentContentClaim("container-2", "section-2", "identifier-2", 2L, 2L);
        final ProvenanceEventRecord record = builder.build();
        writer.writeHeader(500000L);
        writer.writeRecord(record);
        writer.close();
        final TocReader tocReader = new StandardTocReader(tocFile);
        try (final FileInputStream fis = new FileInputStream(journalFile);final RecordReader reader = createReader(fis, journalFile.getName(), tocReader, 2048)) {
            final ProvenanceEventRecord event = reader.nextRecord();
            Assert.assertNotNull(event);
            Assert.assertEquals(1000000L, event.getEventId());
            Assert.assertEquals(timestamp, event.getEventTime());
            Assert.assertNull(reader.nextRecord());
        }
        FileUtils.deleteFile(journalFile.getParentFile(), true);
    }

    @Test
    public void testComponentIdInlineAndLookup() throws IOException {
        final File journalFile = new File((("target/storage/" + (UUID.randomUUID().toString())) + "/testSimpleWrite.prov"));
        final File tocFile = TocUtil.getTocFile(journalFile);
        final TocWriter tocWriter = new StandardTocWriter(tocFile, false, false);
        final IdentifierLookup lookup = new IdentifierLookup() {
            @Override
            public List<String> getQueueIdentifiers() {
                return Collections.emptyList();
            }

            @Override
            public List<String> getComponentTypes() {
                return Collections.singletonList("unit-test-component-1");
            }

            @Override
            public List<String> getComponentIdentifiers() {
                return Collections.singletonList("1234");
            }
        };
        final RecordWriter writer = new EventIdFirstSchemaRecordWriter(journalFile, idGenerator, tocWriter, false, (1024 * 32), lookup);
        final Map<String, String> attributes = new HashMap<>();
        attributes.put("filename", "1.txt");
        attributes.put("uuid", UUID.randomUUID().toString());
        final StandardProvenanceEventRecord.Builder builder = new StandardProvenanceEventRecord.Builder();
        builder.setEventId(1000000);
        builder.setEventTime(System.currentTimeMillis());
        builder.setEventType(RECEIVE);
        builder.setTransitUri("nifi://unit-test");
        builder.fromFlowFile(TestUtil.createFlowFile(3L, 3000L, attributes));
        builder.setComponentId("1234");
        builder.setComponentType("unit-test-component-2");
        builder.setPreviousContentClaim("container-1", "section-1", "identifier-1", 1L, 1L);
        builder.setCurrentContentClaim("container-2", "section-2", "identifier-2", 2L, 2L);
        writer.writeHeader(500000L);
        writer.writeRecord(builder.build());
        builder.setEventId(1000001L);
        builder.setComponentId("4444");
        builder.setComponentType("unit-test-component-1");
        writer.writeRecord(builder.build());
        writer.close();
        final TocReader tocReader = new StandardTocReader(tocFile);
        try (final FileInputStream fis = new FileInputStream(journalFile);final RecordReader reader = createReader(fis, journalFile.getName(), tocReader, 2048)) {
            ProvenanceEventRecord event = reader.nextRecord();
            Assert.assertNotNull(event);
            Assert.assertEquals(1000000L, event.getEventId());
            Assert.assertEquals("1234", event.getComponentId());
            Assert.assertEquals("unit-test-component-2", event.getComponentType());
            event = reader.nextRecord();
            Assert.assertNotNull(event);
            Assert.assertEquals(1000001L, event.getEventId());
            Assert.assertEquals("4444", event.getComponentId());
            Assert.assertEquals("unit-test-component-1", event.getComponentType());
            Assert.assertNull(reader.nextRecord());
        }
        FileUtils.deleteFile(journalFile.getParentFile(), true);
    }
}

