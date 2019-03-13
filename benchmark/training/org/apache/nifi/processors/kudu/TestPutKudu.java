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
package org.apache.nifi.processors.kudu;


import CoreAttributes.FILENAME;
import FlushMode.AUTO_FLUSH_BACKGROUND;
import FlushMode.AUTO_FLUSH_SYNC;
import FlushMode.MANUAL_FLUSH;
import OperationType.UPSERT;
import ProvenanceEventType.SEND;
import PutKudu.INSERT_OPERATION;
import PutKudu.KERBEROS_CREDENTIALS_SERVICE;
import PutKudu.RECORD_COUNT_ATTR;
import PutKudu.RECORD_READER;
import PutKudu.REL_FAILURE;
import PutKudu.REL_SUCCESS;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.nifi.controller.AbstractControllerService;
import org.apache.nifi.flowfile.FlowFile;
import org.apache.nifi.kerberos.KerberosCredentialsService;
import org.apache.nifi.logging.ComponentLog;
import org.apache.nifi.provenance.ProvenanceEventRecord;
import org.apache.nifi.reporting.InitializationException;
import org.apache.nifi.schema.access.SchemaNotFoundException;
import org.apache.nifi.serialization.MalformedRecordException;
import org.apache.nifi.serialization.RecordReader;
import org.apache.nifi.serialization.RecordReaderFactory;
import org.apache.nifi.serialization.record.MockRecordParser;
import org.apache.nifi.util.MockFlowFile;
import org.apache.nifi.util.TestRunner;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


public class TestPutKudu {
    public static final String DEFAULT_TABLE_NAME = "Nifi-Kudu-Table";

    public static final String DEFAULT_MASTERS = "testLocalHost:7051";

    public static final String SKIP_HEAD_LINE = "false";

    public static final String TABLE_SCHEMA = "id,stringVal,num32Val,doubleVal";

    private TestRunner testRunner;

    private MockPutKudu processor;

    private MockRecordParser readerFactory;

    @Test
    public void testWriteKuduWithDefaults() throws IOException, InitializationException {
        createRecordReader(100);
        final String filename = "testWriteKudu-" + (System.currentTimeMillis());
        final Map<String, String> flowFileAttributes = new HashMap<>();
        flowFileAttributes.put(FILENAME.key(), filename);
        testRunner.enqueue("trigger", flowFileAttributes);
        testRunner.run();
        testRunner.assertAllFlowFilesTransferred(REL_SUCCESS, 1);
        // verify the successful flow file has the expected content & attributes
        final MockFlowFile mockFlowFile = testRunner.getFlowFilesForRelationship(REL_SUCCESS).get(0);
        mockFlowFile.assertAttributeEquals(FILENAME.key(), filename);
        mockFlowFile.assertAttributeEquals(RECORD_COUNT_ATTR, "100");
        mockFlowFile.assertContentEquals("trigger");
        // verify we generated a provenance event
        final List<ProvenanceEventRecord> provEvents = testRunner.getProvenanceEvents();
        Assert.assertEquals(1, provEvents.size());
        // verify it was a SEND event with the correct URI
        final ProvenanceEventRecord provEvent = provEvents.get(0);
        Assert.assertEquals(SEND, provEvent.getEventType());
    }

    @Test
    public void testKerberosEnabled() throws InitializationException {
        createRecordReader(1);
        final KerberosCredentialsService kerberosCredentialsService = new TestPutKudu.MockKerberosCredentialsService("unit-test-principal", "unit-test-keytab");
        testRunner.addControllerService("kerb", kerberosCredentialsService);
        testRunner.enableControllerService(kerberosCredentialsService);
        testRunner.setProperty(KERBEROS_CREDENTIALS_SERVICE, "kerb");
        testRunner.run(1, false);
        final MockPutKudu proc = ((MockPutKudu) (testRunner.getProcessor()));
        Assert.assertTrue(proc.loggedIn());
        Assert.assertFalse(proc.loggedOut());
        testRunner.run(1, true, false);
        Assert.assertTrue(proc.loggedOut());
    }

    @Test
    public void testInsecureClient() throws InitializationException {
        createRecordReader(1);
        testRunner.run(1, false);
        final MockPutKudu proc = ((MockPutKudu) (testRunner.getProcessor()));
        Assert.assertFalse(proc.loggedIn());
        Assert.assertFalse(proc.loggedOut());
        testRunner.run(1, true, false);
        Assert.assertFalse(proc.loggedOut());
    }

    @Test
    public void testInvalidReaderShouldRouteToFailure() throws IOException, InitializationException, SchemaNotFoundException, MalformedRecordException {
        createRecordReader(0);
        // simulate throwing an IOException when the factory creates a reader which is what would happen when
        // invalid Avro is passed to the Avro reader factory
        final RecordReaderFactory readerFactory = Mockito.mock(RecordReaderFactory.class);
        Mockito.when(readerFactory.getIdentifier()).thenReturn("mock-reader-factory");
        Mockito.when(readerFactory.createRecordReader(ArgumentMatchers.any(FlowFile.class), ArgumentMatchers.any(InputStream.class), ArgumentMatchers.any(ComponentLog.class))).thenThrow(new IOException("NOT AVRO"));
        testRunner.addControllerService("mock-reader-factory", readerFactory);
        testRunner.enableControllerService(readerFactory);
        testRunner.setProperty(RECORD_READER, "mock-reader-factory");
        final String filename = "testInvalidAvroShouldRouteToFailure-" + (System.currentTimeMillis());
        final Map<String, String> flowFileAttributes = new HashMap<>();
        flowFileAttributes.put(FILENAME.key(), filename);
        testRunner.enqueue("trigger", flowFileAttributes);
        testRunner.run();
        testRunner.assertAllFlowFilesTransferred(REL_FAILURE, 1);
    }

    @Test
    public void testValidSchemaShouldBeSuccessful() throws IOException, InitializationException {
        createRecordReader(10);
        final String filename = "testValidSchemaShouldBeSuccessful-" + (System.currentTimeMillis());
        // don't provide my.schema as an attribute
        final Map<String, String> flowFileAttributes = new HashMap<>();
        flowFileAttributes.put(FILENAME.key(), filename);
        flowFileAttributes.put("my.schema", TestPutKudu.TABLE_SCHEMA);
        testRunner.enqueue("trigger", flowFileAttributes);
        testRunner.run();
        testRunner.assertAllFlowFilesTransferred(REL_SUCCESS, 1);
    }

    @Test
    public void testMalformedRecordExceptionFromReaderShouldRouteToFailure() throws IOException, InitializationException, SchemaNotFoundException, MalformedRecordException {
        createRecordReader(10);
        final RecordReader recordReader = Mockito.mock(RecordReader.class);
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
    public void testReadAsStringAndWriteAsInt() throws IOException, InitializationException {
        createRecordReader(0);
        // add the favorite color as a string
        readerFactory.addRecord(1, "name0", "0", "89.89");
        final String filename = "testReadAsStringAndWriteAsInt-" + (System.currentTimeMillis());
        final Map<String, String> flowFileAttributes = new HashMap<>();
        flowFileAttributes.put(FILENAME.key(), filename);
        testRunner.enqueue("trigger", flowFileAttributes);
        testRunner.run();
        testRunner.assertAllFlowFilesTransferred(REL_SUCCESS, 1);
    }

    @Test
    public void testMissingColumInReader() throws IOException, InitializationException {
        createRecordReader(0);
        readerFactory.addRecord("name0", "0", "89.89");// missing id

        final String filename = "testMissingColumInReader-" + (System.currentTimeMillis());
        final Map<String, String> flowFileAttributes = new HashMap<>();
        flowFileAttributes.put(FILENAME.key(), filename);
        testRunner.enqueue("trigger", flowFileAttributes);
        testRunner.run();
        testRunner.assertAllFlowFilesTransferred(REL_FAILURE, 1);
    }

    @Test
    public void testInsertManyFlowFiles() throws Exception {
        createRecordReader(50);
        final String content1 = "{ \"field1\" : \"value1\", \"field2\" : \"valu11\" }";
        final String content2 = "{ \"field1\" : \"value1\", \"field2\" : \"value11\" }";
        final String content3 = "{ \"field1\" : \"value3\", \"field2\" : \"value33\" }";
        testRunner.enqueue(content1.getBytes());
        testRunner.enqueue(content2.getBytes());
        testRunner.enqueue(content3.getBytes());
        testRunner.run(3);
        testRunner.assertAllFlowFilesTransferred(REL_SUCCESS, 3);
        List<MockFlowFile> flowFiles = testRunner.getFlowFilesForRelationship(REL_SUCCESS);
        flowFiles.get(0).assertContentEquals(content1.getBytes());
        flowFiles.get(0).assertAttributeEquals(RECORD_COUNT_ATTR, "50");
        flowFiles.get(1).assertContentEquals(content2.getBytes());
        flowFiles.get(1).assertAttributeEquals(RECORD_COUNT_ATTR, "50");
        flowFiles.get(2).assertContentEquals(content3.getBytes());
        flowFiles.get(2).assertAttributeEquals(RECORD_COUNT_ATTR, "50");
    }

    @Test
    public void testUpsertFlowFiles() throws Exception {
        createRecordReader(50);
        testRunner.setProperty(INSERT_OPERATION, UPSERT.toString());
        testRunner.enqueue("string".getBytes());
        testRunner.run();
        testRunner.assertAllFlowFilesTransferred(REL_SUCCESS, 1);
        MockFlowFile flowFile = testRunner.getFlowFilesForRelationship(REL_SUCCESS).get(0);
        flowFile.assertContentEquals("string".getBytes());
        flowFile.assertAttributeEquals(RECORD_COUNT_ATTR, "50");
    }

    @Test
    public void testBuildRow() {
        buildPartialRow(((long) (1)), "foo", ((short) (10)));
    }

    @Test
    public void testBuildPartialRowNullable() {
        buildPartialRow(((long) (1)), null, ((short) (10)));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testBuildPartialRowNullPrimaryKey() {
        buildPartialRow(null, "foo", ((short) (10)));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testBuildPartialRowNotNullable() {
        buildPartialRow(((long) (1)), "foo", null);
    }

    enum ResultCode {

        OK,
        FAIL,
        EXCEPTION;}

    @Test
    public void testKuduPartialFailuresOnAutoFlushSync() throws Exception {
        testKuduPartialFailure(AUTO_FLUSH_SYNC);
    }

    @Test
    public void testKuduPartialFailuresOnAutoFlushBackground() throws Exception {
        testKuduPartialFailure(AUTO_FLUSH_BACKGROUND);
    }

    @Test
    public void testKuduPartialFailuresOnManualFlush() throws Exception {
        testKuduPartialFailure(MANUAL_FLUSH);
    }

    public static class MockKerberosCredentialsService extends AbstractControllerService implements KerberosCredentialsService {
        private final String keytab;

        private final String principal;

        public MockKerberosCredentialsService(final String keytab, final String principal) {
            this.keytab = keytab;
            this.principal = principal;
        }

        @Override
        public String getKeytab() {
            return keytab;
        }

        @Override
        public String getPrincipal() {
            return principal;
        }
    }
}

