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
package org.apache.nifi.processors.avro;


import CoreAttributes.FILENAME;
import SplitAvro.BARE_RECORD_OUTPUT;
import SplitAvro.OUTPUT_SIZE;
import SplitAvro.OUTPUT_STRATEGY;
import SplitAvro.REL_FAILURE;
import SplitAvro.REL_ORIGINAL;
import SplitAvro.REL_SPLIT;
import SplitAvro.TRANSFER_METADATA;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.stream.IntStream;
import org.apache.avro.Schema;
import org.apache.avro.file.DataFileStream;
import org.apache.avro.generic.GenericRecord;
import org.apache.nifi.util.MockFlowFile;
import org.apache.nifi.util.TestRunner;
import org.apache.nifi.util.TestRunners;
import org.junit.Assert;
import org.junit.Test;


public class TestSplitAvro {
    static final String META_KEY1 = "metaKey1";

    static final String META_KEY2 = "metaKey2";

    static final String META_KEY3 = "metaKey3";

    static final String META_VALUE1 = "metaValue1";

    static final long META_VALUE2 = Long.valueOf(1234567);

    static final String META_VALUE3 = "metaValue3";

    private Schema schema;

    private ByteArrayOutputStream users;

    @Test
    public void testRecordSplitWithNoIncomingRecords() throws IOException {
        final TestRunner runner = TestRunners.newTestRunner(new SplitAvro());
        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        createUsers(0, out);
        runner.enqueue(out.toByteArray());
        runner.run();
        runner.assertTransferCount(REL_SPLIT, 0);
        runner.assertTransferCount(REL_ORIGINAL, 1);
        runner.assertTransferCount(REL_FAILURE, 0);
    }

    @Test
    public void testRecordSplitDatafileOutputWithSingleRecords() throws IOException {
        final TestRunner runner = TestRunners.newTestRunner(new SplitAvro());
        final String filename = "users.avro";
        runner.enqueue(users.toByteArray(), new HashMap<String, String>() {
            {
                put(FILENAME.key(), filename);
            }
        });
        runner.run();
        runner.assertTransferCount(REL_SPLIT, 100);
        runner.assertTransferCount(REL_ORIGINAL, 1);
        runner.assertTransferCount(REL_FAILURE, 0);
        final MockFlowFile originalFlowFile = runner.getFlowFilesForRelationship(REL_ORIGINAL).get(0);
        originalFlowFile.assertAttributeExists(FRAGMENT_ID.key());
        originalFlowFile.assertAttributeEquals(FRAGMENT_COUNT.key(), "100");
        final List<MockFlowFile> flowFiles = runner.getFlowFilesForRelationship(REL_SPLIT);
        checkDataFileSplitSize(flowFiles, 1, true);
        final String fragmentIdentifier = flowFiles.get(0).getAttribute("fragment.identifier");
        IntStream.range(0, flowFiles.size()).forEach(( i) -> {
            MockFlowFile flowFile = flowFiles.get(i);
            assertEquals(i, Integer.parseInt(flowFile.getAttribute("fragment.index")));
            assertEquals(fragmentIdentifier, flowFile.getAttribute("fragment.identifier"));
            assertEquals(flowFiles.size(), Integer.parseInt(flowFile.getAttribute(FRAGMENT_COUNT.key())));
            assertEquals(filename, flowFile.getAttribute("segment.original.filename"));
        });
    }

    @Test
    public void testRecordSplitDatafileOutputWithMultipleRecords() throws IOException {
        final TestRunner runner = TestRunners.newTestRunner(new SplitAvro());
        runner.setProperty(OUTPUT_SIZE, "20");
        runner.enqueue(users.toByteArray());
        runner.run();
        runner.assertTransferCount(REL_SPLIT, 5);
        runner.assertTransferCount(REL_ORIGINAL, 1);
        runner.assertTransferCount(REL_FAILURE, 0);
        runner.getFlowFilesForRelationship(REL_ORIGINAL).get(0).assertAttributeEquals(FRAGMENT_COUNT.key(), "5");
        final List<MockFlowFile> flowFiles = runner.getFlowFilesForRelationship(REL_SPLIT);
        checkDataFileSplitSize(flowFiles, 20, true);
    }

    @Test
    public void testRecordSplitDatafileOutputWithSplitSizeLarger() throws IOException {
        final TestRunner runner = TestRunners.newTestRunner(new SplitAvro());
        runner.setProperty(OUTPUT_SIZE, "200");
        runner.enqueue(users.toByteArray());
        runner.run();
        runner.assertTransferCount(REL_SPLIT, 1);
        runner.assertTransferCount(REL_ORIGINAL, 1);
        runner.assertTransferCount(REL_FAILURE, 0);
        runner.getFlowFilesForRelationship(REL_ORIGINAL).get(0).assertAttributeEquals(FRAGMENT_COUNT.key(), "1");
        final List<MockFlowFile> flowFiles = runner.getFlowFilesForRelationship(REL_SPLIT);
        checkDataFileSplitSize(flowFiles, 100, true);
    }

    @Test
    public void testRecordSplitDatafileOutputWithoutMetadata() throws IOException {
        final TestRunner runner = TestRunners.newTestRunner(new SplitAvro());
        runner.setProperty(TRANSFER_METADATA, "false");
        runner.enqueue(users.toByteArray());
        runner.run();
        runner.assertTransferCount(REL_SPLIT, 100);
        runner.assertTransferCount(REL_ORIGINAL, 1);
        runner.assertTransferCount(REL_FAILURE, 0);
        runner.getFlowFilesForRelationship(REL_ORIGINAL).get(0).assertAttributeEquals(FRAGMENT_COUNT.key(), "100");
        final List<MockFlowFile> flowFiles = runner.getFlowFilesForRelationship(REL_SPLIT);
        checkDataFileSplitSize(flowFiles, 1, false);
        for (final MockFlowFile flowFile : flowFiles) {
            try (final ByteArrayInputStream in = new ByteArrayInputStream(flowFile.toByteArray());final DataFileStream<GenericRecord> reader = new DataFileStream(in, new org.apache.avro.generic.GenericDatumReader<GenericRecord>())) {
                Assert.assertFalse(reader.getMetaKeys().contains(TestSplitAvro.META_KEY1));
                Assert.assertFalse(reader.getMetaKeys().contains(TestSplitAvro.META_KEY2));
                Assert.assertFalse(reader.getMetaKeys().contains(TestSplitAvro.META_KEY3));
            }
        }
    }

    @Test
    public void testRecordSplitBareOutputWithSingleRecords() throws IOException {
        final TestRunner runner = TestRunners.newTestRunner(new SplitAvro());
        runner.setProperty(OUTPUT_STRATEGY, BARE_RECORD_OUTPUT);
        runner.enqueue(users.toByteArray());
        runner.run();
        runner.assertTransferCount(REL_SPLIT, 100);
        runner.assertTransferCount(REL_ORIGINAL, 1);
        runner.assertTransferCount(REL_FAILURE, 0);
        runner.getFlowFilesForRelationship(REL_ORIGINAL).get(0).assertAttributeEquals(FRAGMENT_COUNT.key(), "100");
        final List<MockFlowFile> flowFiles = runner.getFlowFilesForRelationship(REL_SPLIT);
        checkBareRecordsSplitSize(flowFiles, 1, true);
    }

    @Test
    public void testRecordSplitBareOutputWithMultipleRecords() throws IOException {
        final TestRunner runner = TestRunners.newTestRunner(new SplitAvro());
        runner.setProperty(OUTPUT_STRATEGY, BARE_RECORD_OUTPUT);
        runner.setProperty(OUTPUT_SIZE, "20");
        runner.enqueue(users.toByteArray());
        runner.run();
        runner.assertTransferCount(REL_SPLIT, 5);
        runner.assertTransferCount(REL_ORIGINAL, 1);
        runner.assertTransferCount(REL_FAILURE, 0);
        runner.getFlowFilesForRelationship(REL_ORIGINAL).get(0).assertAttributeEquals(FRAGMENT_COUNT.key(), "5");
        final List<MockFlowFile> flowFiles = runner.getFlowFilesForRelationship(REL_SPLIT);
        checkBareRecordsSplitSize(flowFiles, 20, true);
    }

    @Test
    public void testRecordSplitBareOutputWithoutMetadata() throws IOException {
        final TestRunner runner = TestRunners.newTestRunner(new SplitAvro());
        runner.setProperty(OUTPUT_STRATEGY, BARE_RECORD_OUTPUT);
        runner.setProperty(OUTPUT_SIZE, "20");
        runner.setProperty(TRANSFER_METADATA, "false");
        runner.enqueue(users.toByteArray());
        runner.run();
        runner.assertTransferCount(REL_SPLIT, 5);
        runner.assertTransferCount(REL_ORIGINAL, 1);
        runner.assertTransferCount(REL_FAILURE, 0);
        runner.getFlowFilesForRelationship(REL_ORIGINAL).get(0).assertAttributeEquals(FRAGMENT_COUNT.key(), "5");
        final List<MockFlowFile> flowFiles = runner.getFlowFilesForRelationship(REL_SPLIT);
        checkBareRecordsSplitSize(flowFiles, 20, false);
        for (final MockFlowFile flowFile : flowFiles) {
            Assert.assertFalse(flowFile.getAttributes().containsKey(TestSplitAvro.META_KEY1));
            Assert.assertFalse(flowFile.getAttributes().containsKey(TestSplitAvro.META_KEY2));
            Assert.assertFalse(flowFile.getAttributes().containsKey(TestSplitAvro.META_KEY3));
        }
    }

    @Test
    public void testFailure() throws IOException {
        final TestRunner runner = TestRunners.newTestRunner(new SplitAvro());
        runner.setProperty(OUTPUT_SIZE, "200");
        runner.enqueue("not avro".getBytes("UTF-8"));
        runner.run();
        runner.assertTransferCount(REL_SPLIT, 0);
        runner.assertTransferCount(REL_ORIGINAL, 0);
        runner.assertTransferCount(REL_FAILURE, 1);
    }
}

