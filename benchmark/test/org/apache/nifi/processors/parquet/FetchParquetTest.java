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
package org.apache.nifi.processors.parquet;


import CoreAttributes.FILENAME;
import CoreAttributes.MIME_TYPE;
import CoreAttributes.PATH;
import FetchParquet.FETCH_FAILURE_REASON_ATTR;
import FetchParquet.RECORD_COUNT_ATTR;
import FetchParquet.RECORD_WRITER;
import FetchParquet.REL_FAILURE;
import FetchParquet.REL_RETRY;
import FetchParquet.REL_SUCCESS;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import org.apache.avro.Schema;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.nifi.flowfile.FlowFile;
import org.apache.nifi.logging.ComponentLog;
import org.apache.nifi.processor.ProcessContext;
import org.apache.nifi.processors.hadoop.record.HDFSRecordReader;
import org.apache.nifi.reporting.InitializationException;
import org.apache.nifi.schema.access.SchemaNotFoundException;
import org.apache.nifi.serialization.RecordSetWriter;
import org.apache.nifi.serialization.RecordSetWriterFactory;
import org.apache.nifi.serialization.record.Record;
import org.apache.nifi.serialization.record.RecordSchema;
import org.apache.nifi.util.MockFlowFile;
import org.apache.nifi.util.TestRunner;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


public class FetchParquetTest {
    static final String DIRECTORY = "target";

    static final String TEST_CONF_PATH = "src/test/resources/core-site.xml";

    static final String RECORD_HEADER = "name,favorite_number,favorite_color";

    private Schema schema;

    private Schema schemaWithArray;

    private Schema schemaWithNullableArray;

    private Configuration testConf;

    private FetchParquet proc;

    private TestRunner testRunner;

    @Test
    public void testFetchParquetToCSV() throws IOException, InitializationException {
        configure(proc);
        final File parquetDir = new File(FetchParquetTest.DIRECTORY);
        final File parquetFile = new File(parquetDir, "testFetchParquetToCSV.parquet");
        final int numUsers = 10;
        writeParquetUsers(parquetFile, numUsers);
        final Map<String, String> attributes = new HashMap<>();
        attributes.put(PATH.key(), parquetDir.getAbsolutePath());
        attributes.put(FILENAME.key(), parquetFile.getName());
        testRunner.enqueue("TRIGGER", attributes);
        testRunner.run();
        testRunner.assertAllFlowFilesTransferred(REL_SUCCESS, 1);
        final MockFlowFile flowFile = testRunner.getFlowFilesForRelationship(REL_SUCCESS).get(0);
        flowFile.assertAttributeEquals(RECORD_COUNT_ATTR, String.valueOf(numUsers));
        flowFile.assertAttributeEquals(MIME_TYPE.key(), "text/plain");
        // the mock record writer will write the header for each record so replace those to get down to just the records
        String flowFileContent = new String(flowFile.toByteArray(), StandardCharsets.UTF_8);
        flowFileContent = flowFileContent.replaceAll(((FetchParquetTest.RECORD_HEADER) + "\n"), "");
        verifyCSVRecords(numUsers, flowFileContent);
    }

    @Test
    public void testFetchWhenELEvaluatesToEmptyShouldRouteFailure() throws InitializationException {
        configure(proc);
        testRunner.setProperty(FetchParquet.FILENAME, "${missing.attr}");
        testRunner.enqueue("TRIGGER");
        testRunner.run();
        testRunner.assertAllFlowFilesTransferred(REL_FAILURE, 1);
        final MockFlowFile flowFile = testRunner.getFlowFilesForRelationship(REL_FAILURE).get(0);
        flowFile.assertAttributeEquals(FETCH_FAILURE_REASON_ATTR, "Can not create a Path from an empty string");
        flowFile.assertContentEquals("TRIGGER");
    }

    @Test
    public void testFetchWhenDoesntExistShouldRouteToFailure() throws InitializationException {
        configure(proc);
        final String filename = "/tmp/does-not-exist-" + (System.currentTimeMillis());
        testRunner.setProperty(FetchParquet.FILENAME, filename);
        testRunner.enqueue("TRIGGER");
        testRunner.run();
        testRunner.assertAllFlowFilesTransferred(REL_FAILURE, 1);
        final MockFlowFile flowFile = testRunner.getFlowFilesForRelationship(REL_FAILURE).get(0);
        flowFile.assertAttributeEquals(FETCH_FAILURE_REASON_ATTR, (("File " + filename) + " does not exist"));
        flowFile.assertContentEquals("TRIGGER");
    }

    @Test
    public void testIOExceptionCreatingReaderShouldRouteToRetry() throws IOException, InitializationException {
        final FetchParquet proc = new FetchParquet() {
            @Override
            public HDFSRecordReader createHDFSRecordReader(ProcessContext context, FlowFile flowFile, Configuration conf, Path path) throws IOException {
                throw new IOException("IOException");
            }
        };
        configure(proc);
        final File parquetDir = new File(FetchParquetTest.DIRECTORY);
        final File parquetFile = new File(parquetDir, "testFetchParquetToCSV.parquet");
        final int numUsers = 10;
        writeParquetUsers(parquetFile, numUsers);
        final Map<String, String> attributes = new HashMap<>();
        attributes.put(PATH.key(), parquetDir.getAbsolutePath());
        attributes.put(FILENAME.key(), parquetFile.getName());
        testRunner.enqueue("TRIGGER", attributes);
        testRunner.run();
        testRunner.assertAllFlowFilesTransferred(REL_RETRY, 1);
        final MockFlowFile flowFile = testRunner.getFlowFilesForRelationship(REL_RETRY).get(0);
        flowFile.assertContentEquals("TRIGGER");
    }

    @Test
    public void testIOExceptionWhileReadingShouldRouteToRetry() throws IOException, InitializationException {
        final FetchParquet proc = new FetchParquet() {
            @Override
            public HDFSRecordReader createHDFSRecordReader(ProcessContext context, FlowFile flowFile, Configuration conf, Path path) throws IOException {
                return new HDFSRecordReader() {
                    @Override
                    public Record nextRecord() throws IOException {
                        throw new IOException("IOException");
                    }

                    @Override
                    public void close() throws IOException {
                    }
                };
            }
        };
        configure(proc);
        final File parquetDir = new File(FetchParquetTest.DIRECTORY);
        final File parquetFile = new File(parquetDir, "testFetchParquetToCSV.parquet");
        final int numUsers = 10;
        writeParquetUsers(parquetFile, numUsers);
        final Map<String, String> attributes = new HashMap<>();
        attributes.put(PATH.key(), parquetDir.getAbsolutePath());
        attributes.put(FILENAME.key(), parquetFile.getName());
        testRunner.enqueue("TRIGGER", attributes);
        testRunner.run();
        testRunner.assertAllFlowFilesTransferred(REL_RETRY, 1);
        final MockFlowFile flowFile = testRunner.getFlowFilesForRelationship(REL_RETRY).get(0);
        flowFile.assertContentEquals("TRIGGER");
    }

    @Test
    public void testIOExceptionWhileWritingShouldRouteToRetry() throws IOException, InitializationException, SchemaNotFoundException {
        configure(proc);
        final RecordSetWriter recordSetWriter = Mockito.mock(RecordSetWriter.class);
        Mockito.when(recordSetWriter.write(ArgumentMatchers.any(Record.class))).thenThrow(new IOException("IOException"));
        final RecordSetWriterFactory recordSetWriterFactory = Mockito.mock(RecordSetWriterFactory.class);
        Mockito.when(recordSetWriterFactory.getIdentifier()).thenReturn("mock-writer-factory");
        Mockito.when(recordSetWriterFactory.createWriter(ArgumentMatchers.any(ComponentLog.class), ArgumentMatchers.any(RecordSchema.class), ArgumentMatchers.any(OutputStream.class))).thenReturn(recordSetWriter);
        testRunner.addControllerService("mock-writer-factory", recordSetWriterFactory);
        testRunner.enableControllerService(recordSetWriterFactory);
        testRunner.setProperty(RECORD_WRITER, "mock-writer-factory");
        final File parquetDir = new File(FetchParquetTest.DIRECTORY);
        final File parquetFile = new File(parquetDir, "testFetchParquetToCSV.parquet");
        final int numUsers = 10;
        writeParquetUsers(parquetFile, numUsers);
        final Map<String, String> attributes = new HashMap<>();
        attributes.put(PATH.key(), parquetDir.getAbsolutePath());
        attributes.put(FILENAME.key(), parquetFile.getName());
        testRunner.enqueue("TRIGGER", attributes);
        testRunner.run();
        testRunner.assertAllFlowFilesTransferred(REL_RETRY, 1);
        final MockFlowFile flowFile = testRunner.getFlowFilesForRelationship(REL_RETRY).get(0);
        flowFile.assertContentEquals("TRIGGER");
    }

    @Test
    public void testFetchWithArray() throws IOException, InitializationException {
        configure(proc);
        final File parquetDir = new File(FetchParquetTest.DIRECTORY);
        final File parquetFile = new File(parquetDir, "testFetchParquetWithArrayToCSV.parquet");
        final int numUsers = 10;
        writeParquetUsersWithArray(parquetFile, numUsers);
        final Map<String, String> attributes = new HashMap<>();
        attributes.put(PATH.key(), parquetDir.getAbsolutePath());
        attributes.put(FILENAME.key(), parquetFile.getName());
        testRunner.enqueue("TRIGGER", attributes);
        testRunner.run();
        testRunner.assertAllFlowFilesTransferred(REL_SUCCESS, 1);
    }

    @Test
    public void testFetchWithNullableArray() throws IOException, InitializationException {
        configure(proc);
        final File parquetDir = new File(FetchParquetTest.DIRECTORY);
        final File parquetFile = new File(parquetDir, "testFetchParquetWithNullableArrayToCSV.parquet");
        final int numUsers = 10;
        writeParquetUsersWithNullableArray(parquetFile, numUsers);
        final Map<String, String> attributes = new HashMap<>();
        attributes.put(PATH.key(), parquetDir.getAbsolutePath());
        attributes.put(FILENAME.key(), parquetFile.getName());
        testRunner.enqueue("TRIGGER", attributes);
        testRunner.run();
        testRunner.assertAllFlowFilesTransferred(REL_SUCCESS, 1);
    }
}

