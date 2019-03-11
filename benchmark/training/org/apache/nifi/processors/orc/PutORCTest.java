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
package org.apache.nifi.processors.orc;


import CoreAttributes.FILENAME;
import ProvenanceEventType.SEND;
import PutORC.ABSOLUTE_HDFS_PATH_ATTRIBUTE;
import PutORC.DIRECTORY;
import PutORC.HADOOP_CONFIGURATION_RESOURCES;
import PutORC.HIVE_DDL_ATTRIBUTE;
import PutORC.HIVE_TABLE_NAME;
import PutORC.RECORD_COUNT_ATTR;
import PutORC.RECORD_READER;
import PutORC.REL_FAILURE;
import PutORC.REL_RETRY;
import PutORC.REL_SUCCESS;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.sql.Date;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoField;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import org.apache.avro.Schema;
import org.apache.commons.io.IOUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hive.serde2.io.DateWritableV2;
import org.apache.hadoop.hive.serde2.io.DoubleWritable;
import org.apache.hadoop.hive.serde2.io.TimestampWritableV2;
import org.apache.hadoop.io.IntWritable;
import org.apache.nifi.avro.AvroTypeUtil;
import org.apache.nifi.flowfile.FlowFile;
import org.apache.nifi.logging.ComponentLog;
import org.apache.nifi.processor.ProcessContext;
import org.apache.nifi.processors.hadoop.exception.FailureException;
import org.apache.nifi.processors.hadoop.record.HDFSRecordWriter;
import org.apache.nifi.provenance.ProvenanceEventRecord;
import org.apache.nifi.reporting.InitializationException;
import org.apache.nifi.schema.access.SchemaNotFoundException;
import org.apache.nifi.serialization.MalformedRecordException;
import org.apache.nifi.serialization.RecordReader;
import org.apache.nifi.serialization.RecordReaderFactory;
import org.apache.nifi.serialization.record.MapRecord;
import org.apache.nifi.serialization.record.MockRecordParser;
import org.apache.nifi.serialization.record.RecordField;
import org.apache.nifi.serialization.record.RecordSchema;
import org.apache.nifi.serialization.record.RecordSet;
import org.apache.nifi.util.MockFlowFile;
import org.apache.nifi.util.TestRunner;
import org.apache.nifi.util.TestRunners;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


public class PutORCTest {
    private static final String DIRECTORY = "target";

    private static final String TEST_CONF_PATH = "src/test/resources/core-site.xml";

    private Schema schema;

    private Configuration testConf;

    private PutORC proc;

    private TestRunner testRunner;

    @Test
    public void testWriteORCWithDefaults() throws IOException, InitializationException {
        configure(proc, 100);
        final String filename = "testORCWithDefaults-" + (System.currentTimeMillis());
        final Map<String, String> flowFileAttributes = new HashMap<>();
        flowFileAttributes.put(FILENAME.key(), filename);
        testRunner.setProperty(HIVE_TABLE_NAME, "myTable");
        testRunner.enqueue("trigger", flowFileAttributes);
        testRunner.run();
        testRunner.assertAllFlowFilesTransferred(REL_SUCCESS, 1);
        final Path orcFile = new Path((((PutORCTest.DIRECTORY) + "/") + filename));
        // verify the successful flow file has the expected attributes
        final MockFlowFile mockFlowFile = testRunner.getFlowFilesForRelationship(REL_SUCCESS).get(0);
        mockFlowFile.assertAttributeEquals(ABSOLUTE_HDFS_PATH_ATTRIBUTE, orcFile.getParent().toString());
        mockFlowFile.assertAttributeEquals(FILENAME.key(), filename);
        mockFlowFile.assertAttributeEquals(RECORD_COUNT_ATTR, "100");
        mockFlowFile.assertAttributeEquals(HIVE_DDL_ATTRIBUTE, "CREATE EXTERNAL TABLE IF NOT EXISTS `myTable` (`name` STRING, `favorite_number` INT, `favorite_color` STRING, `scale` DOUBLE) STORED AS ORC");
        // verify we generated a provenance event
        final List<ProvenanceEventRecord> provEvents = testRunner.getProvenanceEvents();
        Assert.assertEquals(1, provEvents.size());
        // verify it was a SEND event with the correct URI
        final ProvenanceEventRecord provEvent = provEvents.get(0);
        Assert.assertEquals(SEND, provEvent.getEventType());
        // If it runs with a real HDFS, the protocol will be "hdfs://", but with a local filesystem, just assert the filename.
        Assert.assertTrue(provEvent.getTransitUri().endsWith((((PutORCTest.DIRECTORY) + "/") + filename)));
        // verify the content of the ORC file by reading it back in
        verifyORCUsers(orcFile, 100);
        // verify we don't have the temp dot file after success
        final File tempOrcFile = new File((((PutORCTest.DIRECTORY) + "/.") + filename));
        Assert.assertFalse(tempOrcFile.exists());
        // verify we DO have the CRC file after success
        final File crcAvroORCFile = new File(((((PutORCTest.DIRECTORY) + "/.") + filename) + ".crc"));
        Assert.assertTrue(crcAvroORCFile.exists());
    }

    @Test
    public void testWriteORCWithAvroLogicalTypes() throws IOException, InitializationException {
        final String avroSchema = IOUtils.toString(new FileInputStream("src/test/resources/user_logical_types.avsc"), StandardCharsets.UTF_8);
        schema = new Schema.Parser().parse(avroSchema);
        Calendar now = Calendar.getInstance();
        LocalTime nowTime = LocalTime.now();
        LocalDateTime nowDateTime = LocalDateTime.now();
        LocalDate epoch = LocalDate.ofEpochDay(0);
        LocalDate nowDate = LocalDate.now();
        final int timeMillis = nowTime.get(ChronoField.MILLI_OF_DAY);
        final Timestamp timestampMillis = Timestamp.valueOf(nowDateTime);
        final Date dt = Date.valueOf(nowDate);
        final double dec = 1234.56;
        configure(proc, 10, ( numUsers, readerFactory) -> {
            for (int i = 0; i < numUsers; i++) {
                readerFactory.addRecord(i, timeMillis, timestampMillis, dt, dec);
            }
            return null;
        });
        final String filename = "testORCWithDefaults-" + (System.currentTimeMillis());
        final Map<String, String> flowFileAttributes = new HashMap<>();
        flowFileAttributes.put(FILENAME.key(), filename);
        testRunner.setProperty(HIVE_TABLE_NAME, "myTable");
        testRunner.enqueue("trigger", flowFileAttributes);
        testRunner.run();
        testRunner.assertAllFlowFilesTransferred(REL_SUCCESS, 1);
        final Path orcFile = new Path((((PutORCTest.DIRECTORY) + "/") + filename));
        // verify the successful flow file has the expected attributes
        final MockFlowFile mockFlowFile = testRunner.getFlowFilesForRelationship(REL_SUCCESS).get(0);
        mockFlowFile.assertAttributeEquals(ABSOLUTE_HDFS_PATH_ATTRIBUTE, orcFile.getParent().toString());
        mockFlowFile.assertAttributeEquals(FILENAME.key(), filename);
        mockFlowFile.assertAttributeEquals(RECORD_COUNT_ATTR, "10");
        // DDL will be created with field names normalized (lowercased, e.g.) for Hive by default
        mockFlowFile.assertAttributeEquals(HIVE_DDL_ATTRIBUTE, "CREATE EXTERNAL TABLE IF NOT EXISTS `myTable` (`id` INT, `timemillis` INT, `timestampmillis` TIMESTAMP, `dt` DATE, `dec` DOUBLE) STORED AS ORC");
        // verify we generated a provenance event
        final List<ProvenanceEventRecord> provEvents = testRunner.getProvenanceEvents();
        Assert.assertEquals(1, provEvents.size());
        // verify it was a SEND event with the correct URI
        final ProvenanceEventRecord provEvent = provEvents.get(0);
        Assert.assertEquals(SEND, provEvent.getEventType());
        // If it runs with a real HDFS, the protocol will be "hdfs://", but with a local filesystem, just assert the filename.
        Assert.assertTrue(provEvent.getTransitUri().endsWith((((PutORCTest.DIRECTORY) + "/") + filename)));
        // verify the content of the ORC file by reading it back in
        verifyORCUsers(orcFile, 10, ( x, currUser) -> {
            assertEquals(((int) (currUser)), ((IntWritable) (x.get(0))).get());
            assertEquals(timeMillis, ((IntWritable) (x.get(1))).get());
            assertEquals(timestampMillis, ((TimestampWritableV2) (x.get(2))).getTimestamp().toSqlTimestamp());
            final DateFormat noTimeOfDayDateFormat = new SimpleDateFormat("yyyy-MM-dd");
            noTimeOfDayDateFormat.setTimeZone(TimeZone.getTimeZone("gmt"));
            assertEquals(noTimeOfDayDateFormat.format(dt), ((DateWritableV2) (x.get(3))).get().toString());
            assertEquals(dec, ((DoubleWritable) (x.get(4))).get(), Double.MIN_VALUE);
            return null;
        });
        // verify we don't have the temp dot file after success
        final File tempOrcFile = new File((((PutORCTest.DIRECTORY) + "/.") + filename));
        Assert.assertFalse(tempOrcFile.exists());
        // verify we DO have the CRC file after success
        final File crcAvroORCFile = new File(((((PutORCTest.DIRECTORY) + "/.") + filename) + ".crc"));
        Assert.assertTrue(crcAvroORCFile.exists());
    }

    @Test
    public void testValidSchemaWithELShouldBeSuccessful() throws InitializationException {
        configure(proc, 10);
        final String filename = "testValidSchemaWithELShouldBeSuccessful-" + (System.currentTimeMillis());
        // don't provide my.schema as an attribute
        final Map<String, String> flowFileAttributes = new HashMap<>();
        flowFileAttributes.put(FILENAME.key(), filename);
        flowFileAttributes.put("my.schema", schema.toString());
        testRunner.enqueue("trigger", flowFileAttributes);
        testRunner.run();
        testRunner.assertAllFlowFilesTransferred(REL_SUCCESS, 1);
    }

    @Test
    public void testMalformedRecordExceptionFromReaderShouldRouteToFailure() throws IOException, InitializationException, SchemaNotFoundException, MalformedRecordException {
        configure(proc, 10);
        final RecordReader recordReader = Mockito.mock(org.apache.hadoop.hive.ql.io.orc.RecordReader.class);
        Mockito.when(recordReader.nextRecord()).thenThrow(new MalformedRecordException("ERROR"));
        final RecordReaderFactory readerFactory = Mockito.mock(RecordReaderFactory.class);
        Mockito.when(readerFactory.getIdentifier()).thenReturn("mock-reader-factory");
        Mockito.when(readerFactory.createRecordReader(ArgumentMatchers.any(FlowFile.class), ArgumentMatchers.any(InputStream.class), ArgumentMatchers.any(ComponentLog.class))).thenReturn(recordReader);
        testRunner.addControllerService("mock-reader-factory", readerFactory);
        testRunner.enableControllerService(readerFactory);
        testRunner.setProperty(RECORD_READER, "mock-reader-factory");
        final String filename = "testMalformedRecordExceptionShouldRouteToFailure-" + (System.currentTimeMillis());
        final Map<String, String> flowFileAttributes = new HashMap<>();
        flowFileAttributes.put(FILENAME.key(), filename);
        testRunner.enqueue("trigger", flowFileAttributes);
        testRunner.run();
        testRunner.assertAllFlowFilesTransferred(REL_FAILURE, 1);
    }

    @Test
    public void testIOExceptionCreatingWriterShouldRouteToRetry() throws InitializationException {
        final PutORC proc = new PutORC() {
            @Override
            public HDFSRecordWriter createHDFSRecordWriter(ProcessContext context, FlowFile flowFile, Configuration conf, Path path, RecordSchema schema) throws IOException {
                throw new IOException("IOException");
            }
        };
        configure(proc, 0);
        final String filename = "testMalformedRecordExceptionShouldRouteToFailure-" + (System.currentTimeMillis());
        final Map<String, String> flowFileAttributes = new HashMap<>();
        flowFileAttributes.put(FILENAME.key(), filename);
        testRunner.enqueue("trigger", flowFileAttributes);
        testRunner.run();
        testRunner.assertAllFlowFilesTransferred(REL_RETRY, 1);
    }

    @Test
    public void testIOExceptionFromReaderShouldRouteToRetry() throws IOException, InitializationException, SchemaNotFoundException, MalformedRecordException {
        configure(proc, 10);
        final RecordSet recordSet = Mockito.mock(RecordSet.class);
        Mockito.when(recordSet.next()).thenThrow(new IOException("ERROR"));
        final RecordReader recordReader = Mockito.mock(org.apache.hadoop.hive.ql.io.orc.RecordReader.class);
        Mockito.when(recordReader.createRecordSet()).thenReturn(recordSet);
        Mockito.when(recordReader.getSchema()).thenReturn(AvroTypeUtil.createSchema(schema));
        final RecordReaderFactory readerFactory = Mockito.mock(RecordReaderFactory.class);
        Mockito.when(readerFactory.getIdentifier()).thenReturn("mock-reader-factory");
        Mockito.when(readerFactory.createRecordReader(ArgumentMatchers.any(FlowFile.class), ArgumentMatchers.any(InputStream.class), ArgumentMatchers.any(ComponentLog.class))).thenReturn(recordReader);
        testRunner.addControllerService("mock-reader-factory", readerFactory);
        testRunner.enableControllerService(readerFactory);
        testRunner.setProperty(RECORD_READER, "mock-reader-factory");
        final String filename = "testMalformedRecordExceptionShouldRouteToFailure-" + (System.currentTimeMillis());
        final Map<String, String> flowFileAttributes = new HashMap<>();
        flowFileAttributes.put(FILENAME.key(), filename);
        testRunner.enqueue("trigger", flowFileAttributes);
        testRunner.run();
        testRunner.assertAllFlowFilesTransferred(REL_RETRY, 1);
    }

    @Test
    public void testIOExceptionRenamingShouldRouteToRetry() throws InitializationException {
        final PutORC proc = new PutORC() {
            @Override
            protected void rename(FileSystem fileSystem, Path srcFile, Path destFile) throws IOException, InterruptedException, FailureException {
                throw new IOException("IOException renaming");
            }
        };
        configure(proc, 10);
        final String filename = "testIOExceptionRenamingShouldRouteToRetry-" + (System.currentTimeMillis());
        final Map<String, String> flowFileAttributes = new HashMap<>();
        flowFileAttributes.put(FILENAME.key(), filename);
        testRunner.enqueue("trigger", flowFileAttributes);
        testRunner.run();
        testRunner.assertAllFlowFilesTransferred(REL_RETRY, 1);
        // verify we don't have the temp dot file after success
        final File tempAvroORCFile = new File((((PutORCTest.DIRECTORY) + "/.") + filename));
        Assert.assertFalse(tempAvroORCFile.exists());
    }

    @Test
    public void testNestedRecords() throws Exception {
        testRunner = TestRunners.newTestRunner(proc);
        testRunner.setProperty(HADOOP_CONFIGURATION_RESOURCES, PutORCTest.TEST_CONF_PATH);
        testRunner.setProperty(PutORC.DIRECTORY, PutORCTest.DIRECTORY);
        MockRecordParser readerFactory = new MockRecordParser();
        final String avroSchema = IOUtils.toString(new FileInputStream("src/test/resources/nested_record.avsc"), StandardCharsets.UTF_8);
        schema = new Schema.Parser().parse(avroSchema);
        final RecordSchema recordSchema = AvroTypeUtil.createSchema(schema);
        for (final RecordField recordField : recordSchema.getFields()) {
            readerFactory.addSchemaField(recordField);
        }
        Map<String, Object> nestedRecordMap = new HashMap<>();
        nestedRecordMap.put("id", 11088000000001615L);
        nestedRecordMap.put("x", "Hello World!");
        RecordSchema nestedRecordSchema = AvroTypeUtil.createSchema(schema.getField("myField").schema());
        MapRecord nestedRecord = new MapRecord(nestedRecordSchema, nestedRecordMap);
        // This gets added in to its spot in the schema, which is already named "myField"
        readerFactory.addRecord(nestedRecord);
        testRunner.addControllerService("mock-reader-factory", readerFactory);
        testRunner.enableControllerService(readerFactory);
        testRunner.setProperty(RECORD_READER, "mock-reader-factory");
        testRunner.enqueue("trigger");
        testRunner.run();
        testRunner.assertAllFlowFilesTransferred(REL_SUCCESS, 1);
    }
}

