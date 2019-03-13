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
package org.apache.nifi.processors.standard;


import AttributeStrategyUtil.ATTRIBUTE_STRATEGY;
import AttributeStrategyUtil.ATTRIBUTE_STRATEGY_ALL_COMMON;
import AttributeStrategyUtil.ATTRIBUTE_STRATEGY_ALL_UNIQUE;
import CoreAttributes.FILENAME;
import CoreAttributes.MIME_TYPE;
import CoreAttributes.UUID;
import MergeContent.CORRELATION_ATTRIBUTE_NAME;
import MergeContent.DELIMITER_STRATEGY;
import MergeContent.DELIMITER_STRATEGY_FILENAME;
import MergeContent.DELIMITER_STRATEGY_TEXT;
import MergeContent.DEMARCATOR;
import MergeContent.FOOTER;
import MergeContent.FRAGMENT_COUNT_ATTRIBUTE;
import MergeContent.FRAGMENT_ID_ATTRIBUTE;
import MergeContent.FRAGMENT_INDEX_ATTRIBUTE;
import MergeContent.HEADER;
import MergeContent.MAX_BIN_AGE;
import MergeContent.MAX_BIN_COUNT;
import MergeContent.MAX_ENTRIES;
import MergeContent.MAX_SIZE;
import MergeContent.MERGE_BIN_AGE_ATTRIBUTE;
import MergeContent.MERGE_COUNT_ATTRIBUTE;
import MergeContent.MERGE_FORMAT;
import MergeContent.MERGE_FORMAT_AVRO;
import MergeContent.MERGE_FORMAT_CONCAT;
import MergeContent.MERGE_FORMAT_FLOWFILE_STREAM_V3;
import MergeContent.MERGE_FORMAT_TAR;
import MergeContent.MERGE_FORMAT_ZIP;
import MergeContent.MERGE_STRATEGY;
import MergeContent.MERGE_STRATEGY_BIN_PACK;
import MergeContent.MERGE_STRATEGY_DEFRAGMENT;
import MergeContent.MERGE_UUID_ATTRIBUTE;
import MergeContent.METADATA_STRATEGY;
import MergeContent.METADATA_STRATEGY_ALL_COMMON;
import MergeContent.METADATA_STRATEGY_DO_NOT_MERGE;
import MergeContent.METADATA_STRATEGY_IGNORE;
import MergeContent.METADATA_STRATEGY_USE_FIRST;
import MergeContent.MIN_ENTRIES;
import MergeContent.MIN_SIZE;
import MergeContent.REL_FAILURE;
import MergeContent.REL_MERGED;
import MergeContent.REL_ORIGINAL;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.zip.ZipInputStream;
import org.apache.avro.Schema;
import org.apache.avro.generic.GenericRecord;
import org.apache.avro.io.DatumWriter;
import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.io.IOUtils;
import org.apache.nifi.components.ValidationResult;
import org.apache.nifi.processor.ProcessContext;
import org.apache.nifi.util.MockFlowFile;
import org.apache.nifi.util.MockProcessContext;
import org.apache.nifi.util.TestRunner;
import org.apache.nifi.util.TestRunners;
import org.junit.Assert;
import org.junit.Test;


public class TestMergeContent {
    /**
     * This test will verify that if we have a FlowFile larger than the Max Size for a Bin, it will go into its
     * own bin and immediately be processed as its own bin.
     */
    @Test
    public void testFlowFileLargerThanBin() {
        final TestRunner runner = TestRunners.newTestRunner(new MergeContent());
        runner.setProperty(MERGE_STRATEGY, MERGE_STRATEGY_BIN_PACK);
        runner.setProperty(MIN_ENTRIES, "2");
        runner.setProperty(MAX_ENTRIES, "2");
        runner.setProperty(MIN_SIZE, "1 KB");
        runner.setProperty(MAX_SIZE, "5 KB");
        runner.enqueue(new byte[1026]);// add flowfile that fits within the bin limits

        runner.enqueue(new byte[1024 * 6]);// add flowfile that is larger than the bin limit

        runner.run(2);// run twice so that we have a chance to create two bins (though we shouldn't create 2, because only 1 bin will be full)

        runner.assertTransferCount(REL_ORIGINAL, 1);
        runner.assertTransferCount(REL_MERGED, 1);
        runner.assertTransferCount(REL_FAILURE, 0);
        Assert.assertEquals(runner.getFlowFilesForRelationship(REL_MERGED).get(0).getAttribute(UUID.key()), runner.getFlowFilesForRelationship(REL_ORIGINAL).get(0).getAttribute(MERGE_UUID_ATTRIBUTE));
        final MockFlowFile bundle = runner.getFlowFilesForRelationship(REL_MERGED).get(0);
        Assert.assertEquals((1024 * 6), bundle.getSize());
        // Queue should not be empty because the first FlowFile will be transferred back to the input queue
        // when we run out @OnStopped logic, since it won't be transferred to any bin.
        runner.assertQueueNotEmpty();
    }

    @Test
    public void testSimpleAvroConcat() throws IOException, InterruptedException {
        final TestRunner runner = TestRunners.newTestRunner(new MergeContent());
        runner.setProperty(MAX_ENTRIES, "3");
        runner.setProperty(MIN_ENTRIES, "3");
        runner.setProperty(MERGE_FORMAT, MERGE_FORMAT_AVRO);
        final Schema schema = new Schema.Parser().parse(new File("src/test/resources/TestMergeContent/user.avsc"));
        final GenericRecord user1 = new org.apache.avro.generic.GenericData.Record(schema);
        user1.put("name", "Alyssa");
        user1.put("favorite_number", 256);
        final GenericRecord user2 = new org.apache.avro.generic.GenericData.Record(schema);
        user2.put("name", "Ben");
        user2.put("favorite_number", 7);
        user2.put("favorite_color", "red");
        final GenericRecord user3 = new org.apache.avro.generic.GenericData.Record(schema);
        user3.put("name", "John");
        user3.put("favorite_number", 5);
        user3.put("favorite_color", "blue");
        final DatumWriter<GenericRecord> datumWriter = new org.apache.avro.generic.GenericDatumWriter(schema);
        final ByteArrayOutputStream out1 = serializeAvroRecord(schema, user1, datumWriter);
        final ByteArrayOutputStream out2 = serializeAvroRecord(schema, user2, datumWriter);
        final ByteArrayOutputStream out3 = serializeAvroRecord(schema, user3, datumWriter);
        runner.enqueue(out1.toByteArray());
        runner.enqueue(out2.toByteArray());
        runner.enqueue(out3.toByteArray());
        runner.run();
        runner.assertQueueEmpty();
        runner.assertTransferCount(REL_MERGED, 1);
        runner.assertTransferCount(REL_FAILURE, 0);
        runner.assertTransferCount(REL_ORIGINAL, 3);
        final MockFlowFile bundle = runner.getFlowFilesForRelationship(REL_MERGED).get(0);
        bundle.assertAttributeEquals(MIME_TYPE.key(), "application/avro-binary");
        // create a reader for the merged content
        byte[] data = runner.getContentAsByteArray(bundle);
        final Map<String, GenericRecord> users = getGenericRecordMap(data, schema, "name");
        Assert.assertEquals(3, users.size());
        Assert.assertTrue(users.containsKey("Alyssa"));
        Assert.assertTrue(users.containsKey("Ben"));
        Assert.assertTrue(users.containsKey("John"));
    }

    @Test
    public void testAvroConcatWithDifferentSchemas() throws IOException, InterruptedException {
        final TestRunner runner = TestRunners.newTestRunner(new MergeContent());
        runner.setProperty(MAX_ENTRIES, "3");
        runner.setProperty(MIN_ENTRIES, "3");
        runner.setProperty(MERGE_FORMAT, MERGE_FORMAT_AVRO);
        final Schema schema1 = new Schema.Parser().parse(new File("src/test/resources/TestMergeContent/user.avsc"));
        final Schema schema2 = new Schema.Parser().parse(new File("src/test/resources/TestMergeContent/place.avsc"));
        final GenericRecord record1 = new org.apache.avro.generic.GenericData.Record(schema1);
        record1.put("name", "Alyssa");
        record1.put("favorite_number", 256);
        final GenericRecord record2 = new org.apache.avro.generic.GenericData.Record(schema2);
        record2.put("name", "Some Place");
        final GenericRecord record3 = new org.apache.avro.generic.GenericData.Record(schema1);
        record3.put("name", "John");
        record3.put("favorite_number", 5);
        record3.put("favorite_color", "blue");
        final DatumWriter<GenericRecord> datumWriter = new org.apache.avro.generic.GenericDatumWriter(schema1);
        final ByteArrayOutputStream out1 = serializeAvroRecord(schema1, record1, datumWriter);
        final ByteArrayOutputStream out2 = serializeAvroRecord(schema2, record2, datumWriter);
        final ByteArrayOutputStream out3 = serializeAvroRecord(schema1, record3, datumWriter);
        runner.enqueue(out1.toByteArray());
        runner.enqueue(out2.toByteArray());
        runner.enqueue(out3.toByteArray());
        runner.run();
        runner.assertQueueEmpty();
        runner.assertTransferCount(REL_MERGED, 1);
        runner.assertTransferCount(REL_FAILURE, 1);
        runner.assertTransferCount(REL_ORIGINAL, 3);
        final MockFlowFile bundle = runner.getFlowFilesForRelationship(REL_MERGED).get(0);
        bundle.assertAttributeEquals(MIME_TYPE.key(), "application/avro-binary");
        final byte[] data = runner.getContentAsByteArray(bundle);
        final Map<String, GenericRecord> users = getGenericRecordMap(data, schema1, "name");
        Assert.assertEquals(2, users.size());
        Assert.assertTrue(users.containsKey("Alyssa"));
        Assert.assertTrue(users.containsKey("John"));
        final MockFlowFile failure = runner.getFlowFilesForRelationship(REL_FAILURE).get(0);
        final byte[] failureData = runner.getContentAsByteArray(failure);
        final Map<String, GenericRecord> places = getGenericRecordMap(failureData, schema2, "name");
        Assert.assertEquals(1, places.size());
        Assert.assertTrue(places.containsKey("Some Place"));
    }

    @Test
    public void testAvroConcatWithDifferentMetadataDoNotMerge() throws IOException, InterruptedException {
        final TestRunner runner = TestRunners.newTestRunner(new MergeContent());
        runner.setProperty(MAX_ENTRIES, "3");
        runner.setProperty(MIN_ENTRIES, "3");
        runner.setProperty(MERGE_FORMAT, MERGE_FORMAT_AVRO);
        runner.setProperty(METADATA_STRATEGY, METADATA_STRATEGY_DO_NOT_MERGE);
        final Schema schema = new Schema.Parser().parse(new File("src/test/resources/TestMergeContent/user.avsc"));
        final GenericRecord user1 = new org.apache.avro.generic.GenericData.Record(schema);
        user1.put("name", "Alyssa");
        user1.put("favorite_number", 256);
        final Map<String, String> userMeta1 = new HashMap<String, String>() {
            {
                put("test_metadata1", "Test 1");
            }
        };
        final GenericRecord user2 = new org.apache.avro.generic.GenericData.Record(schema);
        user2.put("name", "Ben");
        user2.put("favorite_number", 7);
        user2.put("favorite_color", "red");
        final Map<String, String> userMeta2 = new HashMap<String, String>() {
            {
                put("test_metadata1", "Test 2");// Test non-matching values

            }
        };
        final GenericRecord user3 = new org.apache.avro.generic.GenericData.Record(schema);
        user3.put("name", "John");
        user3.put("favorite_number", 5);
        user3.put("favorite_color", "blue");
        final Map<String, String> userMeta3 = new HashMap<String, String>() {
            {
                put("test_metadata1", "Test 1");
                put("test_metadata2", "Test");// Test unique

            }
        };
        final DatumWriter<GenericRecord> datumWriter = new org.apache.avro.generic.GenericDatumWriter(schema);
        final ByteArrayOutputStream out1 = serializeAvroRecord(schema, user1, datumWriter, userMeta1);
        final ByteArrayOutputStream out2 = serializeAvroRecord(schema, user2, datumWriter, userMeta2);
        final ByteArrayOutputStream out3 = serializeAvroRecord(schema, user3, datumWriter, userMeta3);
        runner.enqueue(out1.toByteArray());
        runner.enqueue(out2.toByteArray());
        runner.enqueue(out3.toByteArray());
        runner.run();
        runner.assertQueueEmpty();
        runner.assertTransferCount(REL_MERGED, 1);
        runner.assertTransferCount(REL_FAILURE, 2);
        runner.assertTransferCount(REL_ORIGINAL, 3);
        final MockFlowFile bundle = runner.getFlowFilesForRelationship(REL_MERGED).get(0);
        bundle.assertAttributeEquals(MIME_TYPE.key(), "application/avro-binary");
        // create a reader for the merged content
        byte[] data = runner.getContentAsByteArray(bundle);
        final Map<String, GenericRecord> users = getGenericRecordMap(data, schema, "name");
        Assert.assertEquals(1, users.size());
        Assert.assertTrue(users.containsKey("Alyssa"));
    }

    @Test
    public void testAvroConcatWithDifferentMetadataIgnore() throws IOException, InterruptedException {
        final TestRunner runner = TestRunners.newTestRunner(new MergeContent());
        runner.setProperty(MAX_ENTRIES, "3");
        runner.setProperty(MIN_ENTRIES, "3");
        runner.setProperty(MERGE_FORMAT, MERGE_FORMAT_AVRO);
        runner.setProperty(METADATA_STRATEGY, METADATA_STRATEGY_IGNORE);
        final Schema schema = new Schema.Parser().parse(new File("src/test/resources/TestMergeContent/user.avsc"));
        final GenericRecord user1 = new org.apache.avro.generic.GenericData.Record(schema);
        user1.put("name", "Alyssa");
        user1.put("favorite_number", 256);
        final Map<String, String> userMeta1 = new HashMap<String, String>() {
            {
                put("test_metadata1", "Test 1");
            }
        };
        final GenericRecord user2 = new org.apache.avro.generic.GenericData.Record(schema);
        user2.put("name", "Ben");
        user2.put("favorite_number", 7);
        user2.put("favorite_color", "red");
        final Map<String, String> userMeta2 = new HashMap<String, String>() {
            {
                put("test_metadata1", "Test 2");// Test non-matching values

            }
        };
        final GenericRecord user3 = new org.apache.avro.generic.GenericData.Record(schema);
        user3.put("name", "John");
        user3.put("favorite_number", 5);
        user3.put("favorite_color", "blue");
        final Map<String, String> userMeta3 = new HashMap<String, String>() {
            {
                put("test_metadata1", "Test 1");
                put("test_metadata2", "Test");// Test unique

            }
        };
        final DatumWriter<GenericRecord> datumWriter = new org.apache.avro.generic.GenericDatumWriter(schema);
        final ByteArrayOutputStream out1 = serializeAvroRecord(schema, user1, datumWriter, userMeta1);
        final ByteArrayOutputStream out2 = serializeAvroRecord(schema, user2, datumWriter, userMeta2);
        final ByteArrayOutputStream out3 = serializeAvroRecord(schema, user3, datumWriter, userMeta3);
        runner.enqueue(out1.toByteArray());
        runner.enqueue(out2.toByteArray());
        runner.enqueue(out3.toByteArray());
        runner.run();
        runner.assertQueueEmpty();
        runner.assertTransferCount(REL_MERGED, 1);
        runner.assertTransferCount(REL_FAILURE, 0);
        runner.assertTransferCount(REL_ORIGINAL, 3);
        final MockFlowFile bundle = runner.getFlowFilesForRelationship(REL_MERGED).get(0);
        bundle.assertAttributeEquals(MIME_TYPE.key(), "application/avro-binary");
        // create a reader for the merged content
        byte[] data = runner.getContentAsByteArray(bundle);
        final Map<String, GenericRecord> users = getGenericRecordMap(data, schema, "name");
        Assert.assertEquals(3, users.size());
        Assert.assertTrue(users.containsKey("Alyssa"));
        Assert.assertTrue(users.containsKey("Ben"));
        Assert.assertTrue(users.containsKey("John"));
    }

    @Test
    public void testAvroConcatWithDifferentMetadataUseFirst() throws IOException, InterruptedException {
        final TestRunner runner = TestRunners.newTestRunner(new MergeContent());
        runner.setProperty(MAX_ENTRIES, "3");
        runner.setProperty(MIN_ENTRIES, "3");
        runner.setProperty(MERGE_FORMAT, MERGE_FORMAT_AVRO);
        runner.setProperty(METADATA_STRATEGY, METADATA_STRATEGY_USE_FIRST);
        final Schema schema = new Schema.Parser().parse(new File("src/test/resources/TestMergeContent/user.avsc"));
        final GenericRecord user1 = new org.apache.avro.generic.GenericData.Record(schema);
        user1.put("name", "Alyssa");
        user1.put("favorite_number", 256);
        final Map<String, String> userMeta1 = new HashMap<String, String>() {
            {
                put("test_metadata1", "Test 1");
            }
        };
        final GenericRecord user2 = new org.apache.avro.generic.GenericData.Record(schema);
        user2.put("name", "Ben");
        user2.put("favorite_number", 7);
        user2.put("favorite_color", "red");
        final Map<String, String> userMeta2 = new HashMap<String, String>() {
            {
                put("test_metadata1", "Test 2");// Test non-matching values

            }
        };
        final GenericRecord user3 = new org.apache.avro.generic.GenericData.Record(schema);
        user3.put("name", "John");
        user3.put("favorite_number", 5);
        user3.put("favorite_color", "blue");
        final Map<String, String> userMeta3 = new HashMap<String, String>() {
            {
                put("test_metadata1", "Test 1");
                put("test_metadata2", "Test");// Test unique

            }
        };
        final DatumWriter<GenericRecord> datumWriter = new org.apache.avro.generic.GenericDatumWriter(schema);
        final ByteArrayOutputStream out1 = serializeAvroRecord(schema, user1, datumWriter, userMeta1);
        final ByteArrayOutputStream out2 = serializeAvroRecord(schema, user2, datumWriter, userMeta2);
        final ByteArrayOutputStream out3 = serializeAvroRecord(schema, user3, datumWriter, userMeta3);
        runner.enqueue(out1.toByteArray());
        runner.enqueue(out2.toByteArray());
        runner.enqueue(out3.toByteArray());
        runner.run();
        runner.assertQueueEmpty();
        runner.assertTransferCount(REL_MERGED, 1);
        runner.assertTransferCount(REL_FAILURE, 0);
        runner.assertTransferCount(REL_ORIGINAL, 3);
        final MockFlowFile bundle = runner.getFlowFilesForRelationship(REL_MERGED).get(0);
        bundle.assertAttributeEquals(MIME_TYPE.key(), "application/avro-binary");
        // create a reader for the merged content
        byte[] data = runner.getContentAsByteArray(bundle);
        final Map<String, GenericRecord> users = getGenericRecordMap(data, schema, "name");
        Assert.assertEquals(3, users.size());
        Assert.assertTrue(users.containsKey("Alyssa"));
        Assert.assertTrue(users.containsKey("Ben"));
        Assert.assertTrue(users.containsKey("John"));
    }

    @Test
    public void testAvroConcatWithDifferentMetadataKeepCommon() throws IOException, InterruptedException {
        final TestRunner runner = TestRunners.newTestRunner(new MergeContent());
        runner.setProperty(MAX_ENTRIES, "3");
        runner.setProperty(MIN_ENTRIES, "3");
        runner.setProperty(MERGE_FORMAT, MERGE_FORMAT_AVRO);
        runner.setProperty(METADATA_STRATEGY, METADATA_STRATEGY_ALL_COMMON);
        final Schema schema = new Schema.Parser().parse(new File("src/test/resources/TestMergeContent/user.avsc"));
        final GenericRecord user1 = new org.apache.avro.generic.GenericData.Record(schema);
        user1.put("name", "Alyssa");
        user1.put("favorite_number", 256);
        final Map<String, String> userMeta1 = new HashMap<String, String>() {
            {
                put("test_metadata1", "Test 1");
            }
        };
        final GenericRecord user2 = new org.apache.avro.generic.GenericData.Record(schema);
        user2.put("name", "Ben");
        user2.put("favorite_number", 7);
        user2.put("favorite_color", "red");
        final Map<String, String> userMeta2 = new HashMap<String, String>() {
            {
                put("test_metadata1", "Test 2");// Test non-matching values

            }
        };
        final GenericRecord user3 = new org.apache.avro.generic.GenericData.Record(schema);
        user3.put("name", "John");
        user3.put("favorite_number", 5);
        user3.put("favorite_color", "blue");
        final Map<String, String> userMeta3 = new HashMap<String, String>() {
            {
                put("test_metadata1", "Test 1");
                put("test_metadata2", "Test");// Test unique

            }
        };
        final DatumWriter<GenericRecord> datumWriter = new org.apache.avro.generic.GenericDatumWriter(schema);
        final ByteArrayOutputStream out1 = serializeAvroRecord(schema, user1, datumWriter, userMeta1);
        final ByteArrayOutputStream out2 = serializeAvroRecord(schema, user2, datumWriter, userMeta2);
        final ByteArrayOutputStream out3 = serializeAvroRecord(schema, user3, datumWriter, userMeta3);
        runner.enqueue(out1.toByteArray());
        runner.enqueue(out2.toByteArray());
        runner.enqueue(out3.toByteArray());
        runner.run();
        runner.assertQueueEmpty();
        runner.assertTransferCount(REL_MERGED, 1);
        runner.assertTransferCount(REL_FAILURE, 1);
        runner.assertTransferCount(REL_ORIGINAL, 3);
        final MockFlowFile bundle = runner.getFlowFilesForRelationship(REL_MERGED).get(0);
        bundle.assertAttributeEquals(MIME_TYPE.key(), "application/avro-binary");
        // create a reader for the merged content
        byte[] data = runner.getContentAsByteArray(bundle);
        final Map<String, GenericRecord> users = getGenericRecordMap(data, schema, "name");
        Assert.assertEquals(2, users.size());
        Assert.assertTrue(users.containsKey("Alyssa"));
        Assert.assertTrue(users.containsKey("John"));
    }

    @Test
    public void testSimpleBinaryConcat() throws IOException, InterruptedException {
        final TestRunner runner = TestRunners.newTestRunner(new MergeContent());
        runner.setProperty(MAX_BIN_AGE, "1 sec");
        runner.setProperty(MERGE_FORMAT, MERGE_FORMAT_CONCAT);
        createFlowFiles(runner);
        runner.run();
        runner.assertQueueEmpty();
        runner.assertTransferCount(REL_MERGED, 1);
        runner.assertTransferCount(REL_FAILURE, 0);
        runner.assertTransferCount(REL_ORIGINAL, 3);
        final MockFlowFile bundle = runner.getFlowFilesForRelationship(REL_MERGED).get(0);
        bundle.assertContentEquals("Hello, World!".getBytes("UTF-8"));
        bundle.assertAttributeEquals(MIME_TYPE.key(), "application/plain-text");
        runner.getFlowFilesForRelationship(REL_ORIGINAL).stream().forEach(( ff) -> assertEquals(bundle.getAttribute(CoreAttributes.UUID.key()), ff.getAttribute(MergeContent.MERGE_UUID_ATTRIBUTE)));
    }

    @Test
    public void testSimpleBinaryConcatSingleBin() throws IOException, InterruptedException {
        final TestRunner runner = TestRunners.newTestRunner(new MergeContent());
        runner.setProperty(MAX_BIN_AGE, "1 sec");
        runner.setProperty(MERGE_FORMAT, MERGE_FORMAT_CONCAT);
        runner.setProperty(MAX_BIN_COUNT, "1");
        createFlowFiles(runner);
        runner.run();
        runner.assertQueueEmpty();
        runner.assertTransferCount(REL_MERGED, 1);
        runner.assertTransferCount(REL_FAILURE, 0);
        runner.assertTransferCount(REL_ORIGINAL, 3);
        final MockFlowFile bundle = runner.getFlowFilesForRelationship(REL_MERGED).get(0);
        bundle.assertContentEquals("Hello, World!".getBytes("UTF-8"));
        bundle.assertAttributeEquals(MIME_TYPE.key(), "application/plain-text");
    }

    @Test
    public void testSimpleBinaryConcatWithTextDelimiters() throws IOException, InterruptedException {
        final TestRunner runner = TestRunners.newTestRunner(new MergeContent());
        runner.setProperty(MAX_BIN_AGE, "1 sec");
        runner.setProperty(MERGE_FORMAT, MERGE_FORMAT_CONCAT);
        runner.setProperty(DELIMITER_STRATEGY, DELIMITER_STRATEGY_TEXT);
        runner.setProperty(HEADER, "@");
        runner.setProperty(DEMARCATOR, "#");
        runner.setProperty(FOOTER, "$");
        createFlowFiles(runner);
        runner.run();
        runner.assertQueueEmpty();
        runner.assertTransferCount(REL_MERGED, 1);
        runner.assertTransferCount(REL_FAILURE, 0);
        runner.assertTransferCount(REL_ORIGINAL, 3);
        final MockFlowFile bundle = runner.getFlowFilesForRelationship(REL_MERGED).get(0);
        bundle.assertContentEquals("@Hello#, #World!$".getBytes("UTF-8"));
        bundle.assertAttributeEquals(MIME_TYPE.key(), "application/plain-text");
    }

    @Test
    public void testSimpleBinaryConcatWithTextDelimitersHeaderOnly() throws IOException, InterruptedException {
        final TestRunner runner = TestRunners.newTestRunner(new MergeContent());
        runner.setProperty(MAX_BIN_AGE, "1 sec");
        runner.setProperty(MERGE_FORMAT, MERGE_FORMAT_CONCAT);
        runner.setProperty(DELIMITER_STRATEGY, DELIMITER_STRATEGY_TEXT);
        runner.setProperty(HEADER, "@");
        createFlowFiles(runner);
        runner.run();
        runner.assertQueueEmpty();
        runner.assertTransferCount(REL_MERGED, 1);
        runner.assertTransferCount(REL_FAILURE, 0);
        runner.assertTransferCount(REL_ORIGINAL, 3);
        final MockFlowFile bundle = runner.getFlowFilesForRelationship(REL_MERGED).get(0);
        bundle.assertContentEquals("@Hello, World!".getBytes("UTF-8"));
        bundle.assertAttributeEquals(MIME_TYPE.key(), "application/plain-text");
    }

    @Test
    public void testSimpleBinaryConcatWithFileDelimiters() throws IOException, InterruptedException {
        final TestRunner runner = TestRunners.newTestRunner(new MergeContent());
        runner.setProperty(MAX_BIN_AGE, "1 sec");
        runner.setProperty(MERGE_FORMAT, MERGE_FORMAT_CONCAT);
        runner.setProperty(DELIMITER_STRATEGY, DELIMITER_STRATEGY_FILENAME);
        runner.setProperty(HEADER, "${header}");
        runner.setProperty(DEMARCATOR, "${demarcator}");
        runner.setProperty(FOOTER, "${footer}");
        final Map<String, String> attributes = new HashMap<>();
        attributes.put(MIME_TYPE.key(), "application/plain-text");
        attributes.put("header", "src/test/resources/TestMergeContent/head");
        attributes.put("demarcator", "src/test/resources/TestMergeContent/demarcate");
        attributes.put("footer", "src/test/resources/TestMergeContent/foot");
        runner.enqueue("Hello".getBytes("UTF-8"), attributes);
        runner.enqueue(", ".getBytes("UTF-8"), attributes);
        runner.enqueue("World!".getBytes("UTF-8"), attributes);
        runner.run();
        runner.assertQueueEmpty();
        runner.assertTransferCount(REL_MERGED, 1);
        runner.assertTransferCount(REL_FAILURE, 0);
        runner.assertTransferCount(REL_ORIGINAL, 3);
        final MockFlowFile bundle = runner.getFlowFilesForRelationship(REL_MERGED).get(0);
        bundle.assertContentEquals("(|)Hello***, ***World!___".getBytes("UTF-8"));
        bundle.assertAttributeEquals(MIME_TYPE.key(), "application/plain-text");
    }

    @Test
    public void testTextDelimitersValidation() throws IOException, InterruptedException {
        final TestRunner runner = TestRunners.newTestRunner(new MergeContent());
        runner.setProperty(MAX_BIN_AGE, "1 sec");
        runner.setProperty(MERGE_FORMAT, MERGE_FORMAT_CONCAT);
        runner.setProperty(DELIMITER_STRATEGY, DELIMITER_STRATEGY_TEXT);
        runner.setProperty(HEADER, "");
        runner.setProperty(DEMARCATOR, "");
        runner.setProperty(FOOTER, "");
        Collection<ValidationResult> results = new HashSet<>();
        ProcessContext context = runner.getProcessContext();
        if (context instanceof MockProcessContext) {
            MockProcessContext mockContext = ((MockProcessContext) (context));
            results = mockContext.validate();
        }
        Assert.assertEquals(3, results.size());
        for (ValidationResult vr : results) {
            Assert.assertTrue(vr.toString().contains("cannot be empty"));
        }
    }

    @Test
    public void testFileDelimitersValidation() throws IOException, InterruptedException {
        final String doesNotExistFile = "src/test/resources/TestMergeContent/does_not_exist";
        final TestRunner runner = TestRunners.newTestRunner(new MergeContent());
        runner.setProperty(MAX_BIN_AGE, "1 sec");
        runner.setProperty(MERGE_FORMAT, MERGE_FORMAT_CONCAT);
        runner.setProperty(DELIMITER_STRATEGY, DELIMITER_STRATEGY_FILENAME);
        runner.setProperty(HEADER, doesNotExistFile);
        runner.setProperty(DEMARCATOR, doesNotExistFile);
        runner.setProperty(FOOTER, doesNotExistFile);
        Collection<ValidationResult> results = new HashSet<>();
        ProcessContext context = runner.getProcessContext();
        if (context instanceof MockProcessContext) {
            MockProcessContext mockContext = ((MockProcessContext) (context));
            results = mockContext.validate();
        }
        Assert.assertEquals(3, results.size());
        for (ValidationResult vr : results) {
            Assert.assertTrue(vr.toString().contains((("is invalid because File " + (new File(doesNotExistFile).toString())) + " does not exist")));
        }
    }

    @Test
    public void testMimeTypeIsOctetStreamIfConflictingWithBinaryConcat() throws IOException, InterruptedException {
        final TestRunner runner = TestRunners.newTestRunner(new MergeContent());
        runner.setProperty(MAX_BIN_AGE, "1 sec");
        runner.setProperty(MERGE_FORMAT, MERGE_FORMAT_CONCAT);
        createFlowFiles(runner);
        final Map<String, String> attributes = new HashMap<>();
        attributes.put(MIME_TYPE.key(), "application/zip");
        runner.enqueue(new byte[0], attributes);
        runner.run();
        runner.assertQueueEmpty();
        runner.assertTransferCount(REL_MERGED, 1);
        runner.assertTransferCount(REL_FAILURE, 0);
        runner.assertTransferCount(REL_ORIGINAL, 4);
        final MockFlowFile bundle = runner.getFlowFilesForRelationship(REL_MERGED).get(0);
        bundle.assertContentEquals("Hello, World!".getBytes("UTF-8"));
        bundle.assertAttributeEquals(MIME_TYPE.key(), "application/octet-stream");
    }

    @Test
    public void testOldestBinIsExpired() throws IOException, InterruptedException {
        final TestRunner runner = TestRunners.newTestRunner(new MergeContent());
        runner.setProperty(MAX_BIN_AGE, "1 day");
        runner.setProperty(MAX_BIN_COUNT, "50");
        runner.setProperty(MIN_ENTRIES, "10");
        runner.setProperty(MAX_ENTRIES, "10");
        runner.setProperty(MERGE_FORMAT, MERGE_FORMAT_CONCAT);
        runner.setProperty(CORRELATION_ATTRIBUTE_NAME, "correlationId");
        final Map<String, String> attrs = new HashMap<>();
        for (int i = 0; i < 49; i++) {
            attrs.put("correlationId", String.valueOf(i));
            for (int j = 0; j < 5; j++) {
                runner.enqueue(new byte[0], attrs);
            }
        }
        runner.run();
        runner.assertQueueNotEmpty();// sessions rolled back.

        runner.assertTransferCount(REL_MERGED, 0);
        runner.assertTransferCount(REL_FAILURE, 0);
        runner.assertTransferCount(REL_ORIGINAL, 0);
        attrs.remove("correlationId");
        runner.clearTransferState();
        // Run a single iteration but do not perform the @OnStopped action because
        // we do not want to purge our Bin Manager. This causes some bins to get
        // created. We then enqueue a FlowFile with no correlation id. We do it this
        // way because if we just run a single iteration, then all FlowFiles will be
        // pulled in at once, and we don't know if the first bin to be created will
        // have 5 FlowFiles or 1 FlowFile, since this one that we are about to enqueue
        // will be in a separate bin.
        runner.run(1, false, true);
        runner.enqueue(new byte[0], attrs);
        runner.run();
        runner.assertTransferCount(REL_MERGED, 1);
        runner.assertTransferCount(REL_FAILURE, 0);
        runner.assertTransferCount(REL_ORIGINAL, 5);
    }

    @Test
    public void testSimpleBinaryConcatWaitsForMin() throws IOException, InterruptedException {
        final TestRunner runner = TestRunners.newTestRunner(new MergeContent());
        runner.setProperty(MERGE_FORMAT, MERGE_FORMAT_CONCAT);
        runner.setProperty(MIN_SIZE, "20 KB");
        createFlowFiles(runner);
        runner.run();
        runner.assertTransferCount(REL_MERGED, 0);
        runner.assertTransferCount(REL_FAILURE, 0);
        runner.assertTransferCount(REL_ORIGINAL, 0);
    }

    @Test
    public void testZip() throws IOException {
        final TestRunner runner = TestRunners.newTestRunner(new MergeContent());
        runner.setProperty(MAX_BIN_AGE, "1 sec");
        runner.setProperty(MERGE_FORMAT, MERGE_FORMAT_ZIP);
        createFlowFiles(runner);
        runner.run();
        runner.assertQueueEmpty();
        runner.assertTransferCount(REL_MERGED, 1);
        runner.assertTransferCount(REL_FAILURE, 0);
        runner.assertTransferCount(REL_ORIGINAL, 3);
        final MockFlowFile bundle = runner.getFlowFilesForRelationship(REL_MERGED).get(0);
        try (final InputStream rawIn = new ByteArrayInputStream(runner.getContentAsByteArray(bundle));final ZipInputStream in = new ZipInputStream(rawIn)) {
            Assert.assertNotNull(in.getNextEntry());
            final byte[] part1 = IOUtils.toByteArray(in);
            Assert.assertTrue(Arrays.equals("Hello".getBytes("UTF-8"), part1));
            in.getNextEntry();
            final byte[] part2 = IOUtils.toByteArray(in);
            Assert.assertTrue(Arrays.equals(", ".getBytes("UTF-8"), part2));
            in.getNextEntry();
            final byte[] part3 = IOUtils.toByteArray(in);
            Assert.assertTrue(Arrays.equals("World!".getBytes("UTF-8"), part3));
        }
        bundle.assertAttributeEquals(MIME_TYPE.key(), "application/zip");
    }

    @Test
    public void testZipException() throws IOException {
        final TestRunner runner = TestRunners.newTestRunner(new MergeContent());
        runner.setProperty(MAX_BIN_AGE, "1 sec");
        runner.setProperty(MERGE_FORMAT, MERGE_FORMAT_ZIP);
        final Map<String, String> attributes = new HashMap<>();
        attributes.put(MIME_TYPE.key(), "application/plain-text");
        attributes.put("filename", "duplicate-filename.txt");
        runner.enqueue("Hello".getBytes("UTF-8"), attributes);
        runner.enqueue(", ".getBytes("UTF-8"), attributes);
        runner.enqueue("World!".getBytes("UTF-8"), attributes);
        runner.run();
        runner.assertQueueEmpty();
        runner.assertTransferCount(REL_MERGED, 1);
        runner.assertTransferCount(REL_FAILURE, 2);
        runner.assertTransferCount(REL_ORIGINAL, 3);
    }

    @Test
    public void testTar() throws IOException {
        final TestRunner runner = TestRunners.newTestRunner(new MergeContent());
        runner.setProperty(MAX_BIN_AGE, "1 sec");
        runner.setProperty(MERGE_FORMAT, MERGE_FORMAT_TAR);
        final Map<String, String> attributes = new HashMap<>();
        attributes.put(MIME_TYPE.key(), "application/plain-text");
        attributes.put(FILENAME.key(), "AShortFileName");
        runner.enqueue("Hello".getBytes("UTF-8"), attributes);
        attributes.put(FILENAME.key(), "ALongerrrFileName");
        runner.enqueue(", ".getBytes("UTF-8"), attributes);
        attributes.put(FILENAME.key(), "AReallyLongggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggFileName");
        runner.enqueue("World!".getBytes("UTF-8"), attributes);
        runner.run();
        runner.assertQueueEmpty();
        runner.assertTransferCount(REL_MERGED, 1);
        runner.assertTransferCount(REL_FAILURE, 0);
        runner.assertTransferCount(REL_ORIGINAL, 3);
        final MockFlowFile bundle = runner.getFlowFilesForRelationship(REL_MERGED).get(0);
        try (final InputStream rawIn = new ByteArrayInputStream(runner.getContentAsByteArray(bundle));final TarArchiveInputStream in = new TarArchiveInputStream(rawIn)) {
            ArchiveEntry entry = in.getNextEntry();
            Assert.assertNotNull(entry);
            Assert.assertEquals("AShortFileName", entry.getName());
            final byte[] part1 = IOUtils.toByteArray(in);
            Assert.assertTrue(Arrays.equals("Hello".getBytes("UTF-8"), part1));
            entry = in.getNextEntry();
            Assert.assertEquals("ALongerrrFileName", entry.getName());
            final byte[] part2 = IOUtils.toByteArray(in);
            Assert.assertTrue(Arrays.equals(", ".getBytes("UTF-8"), part2));
            entry = in.getNextEntry();
            Assert.assertEquals("AReallyLongggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggFileName", entry.getName());
            final byte[] part3 = IOUtils.toByteArray(in);
            Assert.assertTrue(Arrays.equals("World!".getBytes("UTF-8"), part3));
        }
        bundle.assertAttributeEquals(MIME_TYPE.key(), "application/tar");
    }

    @Test
    public void testFlowFileStream() throws IOException {
        final TestRunner runner = TestRunners.newTestRunner(new MergeContent());
        runner.setProperty(MAX_BIN_AGE, "1 sec");
        runner.setProperty(MIN_ENTRIES, "2");
        runner.setProperty(MAX_ENTRIES, "2");
        runner.setProperty(MERGE_FORMAT, MERGE_FORMAT_FLOWFILE_STREAM_V3);
        final Map<String, String> attributes = new HashMap<>();
        attributes.put("path", "folder");
        runner.enqueue(Paths.get("src/test/resources/TestUnpackContent/folder/cal.txt"), attributes);
        runner.enqueue(Paths.get("src/test/resources/TestUnpackContent/folder/date.txt"), attributes);
        runner.run();
        runner.assertTransferCount(REL_MERGED, 1);
        runner.assertTransferCount(REL_FAILURE, 0);
        runner.assertTransferCount(REL_ORIGINAL, 2);
        final MockFlowFile merged = runner.getFlowFilesForRelationship(REL_MERGED).get(0);
        merged.assertAttributeEquals(MIME_TYPE.key(), "application/flowfile-v3");
    }

    @Test
    public void testDefragment() throws IOException {
        final TestRunner runner = TestRunners.newTestRunner(new MergeContent());
        runner.setProperty(MERGE_STRATEGY, MERGE_STRATEGY_DEFRAGMENT);
        runner.setProperty(MAX_BIN_AGE, "1 min");
        final Map<String, String> attributes = new HashMap<>();
        attributes.put(FRAGMENT_ID_ATTRIBUTE, "1");
        attributes.put(FRAGMENT_COUNT_ATTRIBUTE, "4");
        attributes.put(FRAGMENT_INDEX_ATTRIBUTE, "1");
        runner.enqueue("A Man ".getBytes("UTF-8"), attributes);
        attributes.put(FRAGMENT_INDEX_ATTRIBUTE, "2");
        runner.enqueue("A Plan ".getBytes("UTF-8"), attributes);
        attributes.put(FRAGMENT_INDEX_ATTRIBUTE, "3");
        runner.enqueue("A Canal ".getBytes("UTF-8"), attributes);
        attributes.put(FRAGMENT_INDEX_ATTRIBUTE, "4");
        runner.enqueue("Panama".getBytes("UTF-8"), attributes);
        runner.run();
        runner.assertTransferCount(REL_MERGED, 1);
        final MockFlowFile assembled = runner.getFlowFilesForRelationship(REL_MERGED).get(0);
        assembled.assertContentEquals("A Man A Plan A Canal Panama".getBytes("UTF-8"));
    }

    @Test
    public void testDefragmentDuplicateFragement() throws IOException, InterruptedException {
        final TestRunner runner = TestRunners.newTestRunner(new MergeContent());
        runner.setProperty(MERGE_STRATEGY, MERGE_STRATEGY_DEFRAGMENT);
        runner.setProperty(MAX_BIN_AGE, "1 sec");
        final Map<String, String> attributes = new HashMap<>();
        attributes.put(FRAGMENT_ID_ATTRIBUTE, "1");
        attributes.put(FRAGMENT_COUNT_ATTRIBUTE, "4");
        attributes.put(FRAGMENT_INDEX_ATTRIBUTE, "1");
        runner.enqueue("A Man ".getBytes("UTF-8"), attributes);
        attributes.put(FRAGMENT_INDEX_ATTRIBUTE, "2");
        runner.enqueue("A Plan ".getBytes("UTF-8"), attributes);
        // enqueue a duplicate fragment
        runner.enqueue("A Plan ".getBytes("UTF-8"), attributes);
        attributes.put(FRAGMENT_INDEX_ATTRIBUTE, "3");
        runner.enqueue("A Canal ".getBytes("UTF-8"), attributes);
        attributes.put(FRAGMENT_INDEX_ATTRIBUTE, "4");
        runner.enqueue("Panama".getBytes("UTF-8"), attributes);
        runner.run(1, false);
        runner.assertTransferCount(REL_FAILURE, 0);
        runner.assertTransferCount(REL_MERGED, 1);
        final MockFlowFile assembled = runner.getFlowFilesForRelationship(REL_MERGED).get(0);
        assembled.assertContentEquals("A Man A Plan A Canal Panama".getBytes("UTF-8"));
        runner.clearTransferState();
        Thread.sleep(1100L);
        runner.run();
        runner.assertTransferCount(REL_FAILURE, 1);
        runner.assertTransferCount(REL_MERGED, 0);
    }

    @Test
    public void testDefragmentWithTooManyFragements() throws IOException {
        final TestRunner runner = TestRunners.newTestRunner(new MergeContent());
        runner.setProperty(MERGE_STRATEGY, MERGE_STRATEGY_DEFRAGMENT);
        runner.setProperty(MAX_ENTRIES, "3");
        final Map<String, String> attributes = new HashMap<>();
        attributes.put(FRAGMENT_ID_ATTRIBUTE, "1");
        attributes.put(FRAGMENT_COUNT_ATTRIBUTE, "4");
        attributes.put(FRAGMENT_INDEX_ATTRIBUTE, "1");
        runner.enqueue("A Man ".getBytes("UTF-8"), attributes);
        attributes.put(FRAGMENT_INDEX_ATTRIBUTE, "2");
        runner.enqueue("A Plan ".getBytes("UTF-8"), attributes);
        attributes.put(FRAGMENT_INDEX_ATTRIBUTE, "3");
        runner.enqueue("A Canal ".getBytes("UTF-8"), attributes);
        attributes.put(FRAGMENT_INDEX_ATTRIBUTE, "4");
        runner.enqueue("Panama".getBytes("UTF-8"), attributes);
        runner.run();
        runner.assertTransferCount(REL_FAILURE, 0);
        runner.assertTransferCount(REL_MERGED, 1);
        final MockFlowFile assembled = runner.getFlowFilesForRelationship(REL_MERGED).get(0);
        assembled.assertContentEquals("A Man A Plan A Canal Panama".getBytes("UTF-8"));
    }

    @Test
    public void testDefragmentWithTooFewFragments() throws IOException {
        final TestRunner runner = TestRunners.newTestRunner(new MergeContent());
        runner.setProperty(MERGE_STRATEGY, MERGE_STRATEGY_DEFRAGMENT);
        runner.setProperty(MAX_BIN_AGE, "2 secs");
        final Map<String, String> attributes = new HashMap<>();
        attributes.put(FRAGMENT_ID_ATTRIBUTE, "1");
        attributes.put(FRAGMENT_COUNT_ATTRIBUTE, "5");
        attributes.put(FRAGMENT_INDEX_ATTRIBUTE, "1");
        runner.enqueue("A Man ".getBytes("UTF-8"), attributes);
        attributes.put(FRAGMENT_INDEX_ATTRIBUTE, "2");
        runner.enqueue("A Plan ".getBytes("UTF-8"), attributes);
        attributes.put(FRAGMENT_INDEX_ATTRIBUTE, "3");
        runner.enqueue("A Canal ".getBytes("UTF-8"), attributes);
        attributes.put(FRAGMENT_INDEX_ATTRIBUTE, "4");
        runner.enqueue("Panama".getBytes("UTF-8"), attributes);
        runner.run(1, false);
        while (true) {
            try {
                Thread.sleep(3000L);
                break;
            } catch (final InterruptedException ie) {
            }
        } 
        runner.run(1);
        runner.assertTransferCount(REL_FAILURE, 4);
    }

    @Test
    public void testDefragmentOutOfOrder() throws IOException {
        final TestRunner runner = TestRunners.newTestRunner(new MergeContent());
        runner.setProperty(MERGE_STRATEGY, MERGE_STRATEGY_DEFRAGMENT);
        runner.setProperty(MAX_BIN_AGE, "1 min");
        final Map<String, String> attributes = new HashMap<>();
        attributes.put(FRAGMENT_ID_ATTRIBUTE, "1");
        attributes.put(FRAGMENT_COUNT_ATTRIBUTE, "4");
        attributes.put(FRAGMENT_INDEX_ATTRIBUTE, "1");
        runner.enqueue("A Man ".getBytes("UTF-8"), attributes);
        attributes.put(FRAGMENT_INDEX_ATTRIBUTE, "3");
        runner.enqueue("A Canal ".getBytes("UTF-8"), attributes);
        attributes.put(FRAGMENT_INDEX_ATTRIBUTE, "4");
        runner.enqueue("Panama".getBytes("UTF-8"), attributes);
        attributes.put(FRAGMENT_INDEX_ATTRIBUTE, "2");
        runner.enqueue("A Plan ".getBytes("UTF-8"), attributes);
        runner.run();
        runner.assertTransferCount(REL_MERGED, 1);
        final MockFlowFile assembled = runner.getFlowFilesForRelationship(REL_MERGED).get(0);
        assembled.assertContentEquals("A Man A Plan A Canal Panama".getBytes("UTF-8"));
        runner.getFlowFilesForRelationship(REL_ORIGINAL).stream().forEach(( ff) -> assertEquals(assembled.getAttribute(CoreAttributes.UUID.key()), ff.getAttribute(MergeContent.MERGE_UUID_ATTRIBUTE)));
    }

    @Test
    public void testDefragmentMultipleMingledSegments() throws IOException {
        final TestRunner runner = TestRunners.newTestRunner(new MergeContent());
        runner.setProperty(MERGE_STRATEGY, MERGE_STRATEGY_DEFRAGMENT);
        runner.setProperty(MAX_BIN_AGE, "1 min");
        final Map<String, String> attributes = new HashMap<>();
        attributes.put(FRAGMENT_ID_ATTRIBUTE, "1");
        attributes.put(FRAGMENT_COUNT_ATTRIBUTE, "4");
        final Map<String, String> secondAttrs = new HashMap<>();
        secondAttrs.put(FRAGMENT_ID_ATTRIBUTE, "TWO");
        secondAttrs.put(FRAGMENT_COUNT_ATTRIBUTE, "3");
        attributes.put(FRAGMENT_INDEX_ATTRIBUTE, "1");
        runner.enqueue("A Man ".getBytes("UTF-8"), attributes);
        attributes.put(FRAGMENT_INDEX_ATTRIBUTE, "2");
        runner.enqueue("A Plan ".getBytes("UTF-8"), attributes);
        secondAttrs.put(FRAGMENT_INDEX_ATTRIBUTE, "1");
        runner.enqueue("No x ".getBytes("UTF-8"), secondAttrs);
        secondAttrs.put(FRAGMENT_INDEX_ATTRIBUTE, "2");
        runner.enqueue("in ".getBytes("UTF-8"), secondAttrs);
        secondAttrs.put(FRAGMENT_INDEX_ATTRIBUTE, "3");
        runner.enqueue("Nixon".getBytes("UTF-8"), secondAttrs);
        attributes.put(FRAGMENT_INDEX_ATTRIBUTE, "3");
        runner.enqueue("A Canal ".getBytes("UTF-8"), attributes);
        attributes.put(FRAGMENT_INDEX_ATTRIBUTE, "4");
        runner.enqueue("Panama".getBytes("UTF-8"), attributes);
        runner.run(1);
        runner.assertTransferCount(REL_MERGED, 2);
        final MockFlowFile assembled = runner.getFlowFilesForRelationship(REL_MERGED).get(0);
        assembled.assertContentEquals("A Man A Plan A Canal Panama".getBytes("UTF-8"));
        final MockFlowFile assembledTwo = runner.getFlowFilesForRelationship(REL_MERGED).get(1);
        assembledTwo.assertContentEquals("No x in Nixon".getBytes("UTF-8"));
    }

    @Test
    public void testDefragmentOldStyleAttributes() throws IOException {
        final TestRunner runner = TestRunners.newTestRunner(new MergeContent());
        runner.setProperty(MERGE_STRATEGY, MERGE_STRATEGY_DEFRAGMENT);
        runner.setProperty(MAX_BIN_AGE, "1 min");
        final Map<String, String> attributes = new HashMap<>();
        attributes.put("segment.identifier", "1");
        attributes.put("segment.count", "4");
        attributes.put("segment.index", "1");
        attributes.put("segment.original.filename", "originalfilename");
        runner.enqueue("A Man ".getBytes("UTF-8"), attributes);
        attributes.put("segment.index", "2");
        runner.enqueue("A Plan ".getBytes("UTF-8"), attributes);
        attributes.put("segment.index", "3");
        runner.enqueue("A Canal ".getBytes("UTF-8"), attributes);
        attributes.put("segment.index", "4");
        runner.enqueue("Panama".getBytes("UTF-8"), attributes);
        runner.run();
        runner.assertTransferCount(REL_MERGED, 1);
        final MockFlowFile assembled = runner.getFlowFilesForRelationship(REL_MERGED).get(0);
        assembled.assertContentEquals("A Man A Plan A Canal Panama".getBytes("UTF-8"));
        assembled.assertAttributeEquals(FILENAME.key(), "originalfilename");
    }

    @Test
    public void testDefragmentMultipleOnTriggers() throws IOException {
        final TestRunner runner = TestRunners.newTestRunner(new MergeContent());
        runner.setProperty(MERGE_STRATEGY, MERGE_STRATEGY_DEFRAGMENT);
        final Map<String, String> attributes = new HashMap<>();
        attributes.put(FRAGMENT_ID_ATTRIBUTE, "1");
        attributes.put(FRAGMENT_COUNT_ATTRIBUTE, "4");
        attributes.put(FRAGMENT_INDEX_ATTRIBUTE, "1");
        runner.enqueue("A Man ".getBytes("UTF-8"), attributes);
        runner.run();
        attributes.put(FRAGMENT_INDEX_ATTRIBUTE, "2");
        runner.enqueue("A Plan ".getBytes("UTF-8"), attributes);
        runner.run();
        attributes.put(FRAGMENT_INDEX_ATTRIBUTE, "3");
        runner.enqueue("A Canal ".getBytes("UTF-8"), attributes);
        runner.run();
        attributes.put(FRAGMENT_INDEX_ATTRIBUTE, "4");
        runner.enqueue("Panama".getBytes("UTF-8"), attributes);
        runner.run();
        runner.assertTransferCount(REL_MERGED, 1);
        final MockFlowFile assembled = runner.getFlowFilesForRelationship(REL_MERGED).get(0);
        assembled.assertContentEquals("A Man A Plan A Canal Panama".getBytes("UTF-8"));
    }

    @Test
    public void testMergeBasedOnCorrelation() throws IOException, InterruptedException {
        final TestRunner runner = TestRunners.newTestRunner(new MergeContent());
        runner.setProperty(MERGE_STRATEGY, MERGE_STRATEGY_BIN_PACK);
        runner.setProperty(MAX_BIN_AGE, "1 min");
        runner.setProperty(CORRELATION_ATTRIBUTE_NAME, "attr");
        runner.setProperty(MAX_ENTRIES, "3");
        runner.setProperty(MIN_ENTRIES, "1");
        final Map<String, String> attributes = new HashMap<>();
        attributes.put("attr", "b");
        runner.enqueue("A Man ".getBytes("UTF-8"), attributes);
        runner.enqueue("A Plan ".getBytes("UTF-8"), attributes);
        attributes.put("attr", "c");
        runner.enqueue("A Canal ".getBytes("UTF-8"), attributes);
        attributes.put("attr", "b");
        runner.enqueue("Panama".getBytes("UTF-8"), attributes);
        runner.run(1);
        runner.assertTransferCount(REL_MERGED, 2);
        final List<MockFlowFile> mergedFiles = runner.getFlowFilesForRelationship(REL_MERGED);
        final MockFlowFile merged1 = mergedFiles.get(0);
        final MockFlowFile merged2 = mergedFiles.get(1);
        final String attr1 = merged1.getAttribute("attr");
        final String attr2 = merged2.getAttribute("attr");
        if ("c".equals(attr1)) {
            Assert.assertEquals("b", attr2);
            merged1.assertContentEquals("A Canal ", "UTF-8");
            merged2.assertContentEquals("A Man A Plan Panama", "UTF-8");
        } else {
            Assert.assertEquals("b", attr1);
            Assert.assertEquals("c", attr2);
            merged1.assertContentEquals("A Man A Plan Panama", "UTF-8");
            merged2.assertContentEquals("A Canal ", "UTF-8");
        }
    }

    @Test
    public void testMaxBinAge() throws InterruptedException {
        final TestRunner runner = TestRunners.newTestRunner(new MergeContent());
        runner.setProperty(MERGE_STRATEGY, MERGE_STRATEGY_BIN_PACK);
        runner.setProperty(MAX_BIN_AGE, "2 sec");
        runner.setProperty(CORRELATION_ATTRIBUTE_NAME, "attr");
        runner.setProperty(MAX_ENTRIES, "500");
        runner.setProperty(MIN_ENTRIES, "500");
        for (int i = 0; i < 50; i++) {
            runner.enqueue(new byte[0]);
        }
        runner.run(5, false);
        runner.assertTransferCount(REL_MERGED, 0);
        runner.clearTransferState();
        Thread.sleep(3000L);
        runner.run();
        runner.assertTransferCount(REL_MERGED, 1);
    }

    @Test
    public void testUniqueAttributes() {
        final TestRunner runner = TestRunners.newTestRunner(new MergeContent());
        runner.setProperty(ATTRIBUTE_STRATEGY, ATTRIBUTE_STRATEGY_ALL_UNIQUE);
        runner.setProperty(MAX_SIZE, "2 B");
        runner.setProperty(MIN_SIZE, "2 B");
        final Map<String, String> attr1 = new HashMap<>();
        attr1.put("abc", "xyz");
        attr1.put("xyz", "123");
        attr1.put("hello", "good-bye");
        final Map<String, String> attr2 = new HashMap<>();
        attr2.put("abc", "xyz");
        attr2.put("xyz", "321");
        attr2.put("world", "aaa");
        runner.enqueue(new byte[1], attr1);
        runner.enqueue(new byte[1], attr2);
        runner.run();
        runner.assertTransferCount(REL_MERGED, 1);
        final MockFlowFile outFile = runner.getFlowFilesForRelationship(REL_MERGED).get(0);
        outFile.assertAttributeEquals("abc", "xyz");
        outFile.assertAttributeEquals("hello", "good-bye");
        outFile.assertAttributeEquals("world", "aaa");
        outFile.assertAttributeNotExists("xyz");
    }

    @Test
    public void testCommonAttributesOnly() {
        final TestRunner runner = TestRunners.newTestRunner(new MergeContent());
        runner.setProperty(ATTRIBUTE_STRATEGY, ATTRIBUTE_STRATEGY_ALL_COMMON);
        runner.setProperty(MAX_SIZE, "2 B");
        runner.setProperty(MIN_SIZE, "2 B");
        final Map<String, String> attr1 = new HashMap<>();
        attr1.put("abc", "xyz");
        attr1.put("xyz", "123");
        attr1.put("hello", "good-bye");
        final Map<String, String> attr2 = new HashMap<>();
        attr2.put("abc", "xyz");
        attr2.put("xyz", "321");
        attr2.put("world", "aaa");
        runner.enqueue(new byte[1], attr1);
        runner.enqueue(new byte[1], attr2);
        runner.run();
        runner.assertTransferCount(REL_MERGED, 1);
        final MockFlowFile outFile = runner.getFlowFilesForRelationship(REL_MERGED).get(0);
        outFile.assertAttributeEquals("abc", "xyz");
        outFile.assertAttributeNotExists("hello");
        outFile.assertAttributeNotExists("world");
        outFile.assertAttributeNotExists("xyz");
        final Set<String> uuids = new HashSet<>();
        for (final MockFlowFile mff : runner.getFlowFilesForRelationship(REL_ORIGINAL)) {
            uuids.add(mff.getAttribute(UUID.key()));
        }
        uuids.add(outFile.getAttribute(UUID.key()));
        Assert.assertEquals(3, uuids.size());
    }

    @Test
    public void testCountAttribute() throws IOException, InterruptedException {
        final TestRunner runner = TestRunners.newTestRunner(new MergeContent());
        runner.setProperty(MAX_BIN_AGE, "1 sec");
        runner.setProperty(MERGE_FORMAT, MERGE_FORMAT_CONCAT);
        createFlowFiles(runner);
        runner.run();
        runner.assertQueueEmpty();
        runner.assertTransferCount(REL_MERGED, 1);
        runner.assertTransferCount(REL_FAILURE, 0);
        runner.assertTransferCount(REL_ORIGINAL, 3);
        final MockFlowFile bundle = runner.getFlowFilesForRelationship(REL_MERGED).get(0);
        bundle.assertContentEquals("Hello, World!".getBytes("UTF-8"));
        bundle.assertAttributeEquals(MERGE_COUNT_ATTRIBUTE, "3");
        bundle.assertAttributeExists(MERGE_BIN_AGE_ATTRIBUTE);
    }

    @Test
    public void testLeavesSmallBinUnmerged() {
        final TestRunner runner = TestRunners.newTestRunner(new MergeContent());
        runner.setProperty(MIN_ENTRIES, "5");
        runner.setProperty(MAX_ENTRIES, "5");
        runner.setProperty(MAX_BIN_COUNT, "3");
        for (int i = 0; i < 17; i++) {
            runner.enqueue(((String.valueOf(i)) + "\n"));
        }
        runner.run(5);
        runner.assertTransferCount(REL_MERGED, 3);
        runner.assertTransferCount(REL_ORIGINAL, 15);
        Assert.assertEquals(2, runner.getQueueSize().getObjectCount());
    }
}

