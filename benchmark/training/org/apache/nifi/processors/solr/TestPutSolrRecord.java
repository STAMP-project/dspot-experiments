/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.nifi.processors.solr;


import PutSolrRecord.COLLECTION_PARAM_NAME;
import PutSolrRecord.RECORD_READER;
import PutSolrRecord.REL_CONNECTION_FAILURE;
import PutSolrRecord.REL_FAILURE;
import PutSolrRecord.REL_SUCCESS;
import PutSolrRecord.UPDATE_PATH;
import RecordFieldType.ARRAY;
import RecordFieldType.INT;
import RecordFieldType.RECORD;
import RecordFieldType.STRING;
import SolrException.ErrorCode;
import SolrUtils.BASIC_PASSWORD;
import SolrUtils.BASIC_USERNAME;
import SolrUtils.COLLECTION;
import SolrUtils.SOLR_LOCATION;
import SolrUtils.SOLR_TYPE;
import SolrUtils.SOLR_TYPE_CLOUD;
import SolrUtils.SOLR_TYPE_STANDARD;
import SolrUtils.SSL_CONTEXT_SERVICE;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.net.ssl.SSLContext;
import org.apache.nifi.controller.AbstractControllerService;
import org.apache.nifi.processor.ProcessContext;
import org.apache.nifi.processor.exception.ProcessException;
import org.apache.nifi.reporting.InitializationException;
import org.apache.nifi.serialization.record.MockRecordParser;
import org.apache.nifi.serialization.record.Record;
import org.apache.nifi.serialization.record.RecordField;
import org.apache.nifi.serialization.record.RecordSchema;
import org.apache.nifi.ssl.SSLContextService;
import org.apache.nifi.util.TestRunner;
import org.apache.nifi.util.TestRunners;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrRequest;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.util.NamedList;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


/**
 * Test for PutSolrRecord Processor
 */
public class TestPutSolrRecord {
    private static final String DEFAULT_SOLR_CORE = "testCollection";

    @Test
    public void testPutSolrOnTriggerIndex() throws IOException, InitializationException, SolrServerException {
        final SolrClient solrClient = TestPutSolrRecord.createEmbeddedSolrClient(TestPutSolrRecord.DEFAULT_SOLR_CORE);
        TestPutSolrRecord.TestableProcessor proc = new TestPutSolrRecord.TestableProcessor(solrClient);
        TestRunner runner = TestPutSolrRecord.createDefaultTestRunner(proc);
        MockRecordParser recordParser = new MockRecordParser();
        runner.addControllerService("parser", recordParser);
        runner.enableControllerService(recordParser);
        runner.setProperty(RECORD_READER, "parser");
        runner.setProperty(UPDATE_PATH, "/update");
        recordParser.addSchemaField("id", INT);
        recordParser.addSchemaField("first", STRING);
        recordParser.addSchemaField("last", STRING);
        recordParser.addSchemaField("grade", INT);
        recordParser.addSchemaField("subject", STRING);
        recordParser.addSchemaField("test", STRING);
        recordParser.addSchemaField("marks", INT);
        SolrDocument solrDocument = new SolrDocument();
        solrDocument.put("id", 1);
        solrDocument.put("first", "Abhinav");
        solrDocument.put("last", "R");
        solrDocument.put("grade", 8);
        solrDocument.put("subject", "Chemistry");
        solrDocument.put("test", "term1");
        solrDocument.put("marks", 98);
        recordParser.addRecord(1, "Abhinav", "R", 8, "Chemistry", "term1", 98);
        try {
            runner.enqueue(new byte[0], new HashMap<String, String>() {
                {
                    put("id", "1");
                }
            });
            runner.run(1, false);
            TestPutSolrRecord.verifySolrDocuments(getSolrClient(), Collections.singletonList(solrDocument));
            runner.assertTransferCount(REL_FAILURE, 0);
            runner.assertTransferCount(REL_CONNECTION_FAILURE, 0);
            runner.assertTransferCount(REL_SUCCESS, 1);
        } finally {
            try {
                getSolrClient().close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Test
    public void testPutSolrOnTriggerIndexForANestedRecord() throws IOException, InitializationException, SolrServerException {
        final SolrClient solrClient = TestPutSolrRecord.createEmbeddedSolrClient(TestPutSolrRecord.DEFAULT_SOLR_CORE);
        TestPutSolrRecord.TestableProcessor proc = new TestPutSolrRecord.TestableProcessor(solrClient);
        TestRunner runner = TestPutSolrRecord.createDefaultTestRunner(proc);
        MockRecordParser recordParser = new MockRecordParser();
        runner.addControllerService("parser", recordParser);
        runner.enableControllerService(recordParser);
        runner.setProperty(RECORD_READER, "parser");
        runner.setProperty(UPDATE_PATH, "/update");
        recordParser.addSchemaField("id", INT);
        recordParser.addSchemaField("first", STRING);
        recordParser.addSchemaField("last", STRING);
        recordParser.addSchemaField("grade", INT);
        recordParser.addSchemaField("exam", RECORD);
        final List<RecordField> fields = new ArrayList<>();
        fields.add(new RecordField("subject", STRING.getDataType()));
        fields.add(new RecordField("test", STRING.getDataType()));
        fields.add(new RecordField("marks", INT.getDataType()));
        RecordSchema schema = new org.apache.nifi.serialization.SimpleRecordSchema(fields);
        Map<String, Object> values = new HashMap<>();
        values.put("subject", "Chemistry");
        values.put("test", "term1");
        values.put("marks", 98);
        final Record record = new org.apache.nifi.serialization.record.MapRecord(schema, values);
        recordParser.addRecord(1, "Abhinav", "R", 8, record);
        SolrDocument solrDocument = new SolrDocument();
        solrDocument.put("id", 1);
        solrDocument.put("first", "Abhinav");
        solrDocument.put("last", "R");
        solrDocument.put("grade", 8);
        solrDocument.put("exam_subject", "Chemistry");
        solrDocument.put("exam_test", "term1");
        solrDocument.put("exam_marks", 98);
        try {
            runner.enqueue(new byte[0], new HashMap<String, String>() {
                {
                    put("id", "1");
                }
            });
            runner.run(1, false);
            runner.assertTransferCount(REL_FAILURE, 0);
            runner.assertTransferCount(REL_CONNECTION_FAILURE, 0);
            runner.assertTransferCount(REL_SUCCESS, 1);
            TestPutSolrRecord.verifySolrDocuments(getSolrClient(), Collections.singletonList(solrDocument));
        } finally {
            try {
                getSolrClient().close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Test
    public void testRecordParserExceptionShouldRoutToFailure() throws IOException, InitializationException, SolrServerException {
        final SolrClient solrClient = TestPutSolrRecord.createEmbeddedSolrClient(TestPutSolrRecord.DEFAULT_SOLR_CORE);
        TestPutSolrRecord.TestableProcessor proc = new TestPutSolrRecord.TestableProcessor(solrClient);
        TestRunner runner = TestPutSolrRecord.createDefaultTestRunner(proc);
        MockRecordParser recordParser = new MockRecordParser();
        runner.addControllerService("parser", recordParser);
        runner.enableControllerService(recordParser);
        runner.setProperty(RECORD_READER, "parser");
        runner.setProperty(UPDATE_PATH, "/update");
        recordParser.addSchemaField("id", INT);
        recordParser.addSchemaField("first", STRING);
        recordParser.addSchemaField("last", STRING);
        recordParser.addSchemaField("grade", INT);
        recordParser.failAfter(0);
        try {
            runner.enqueue(new byte[0], new HashMap<String, String>() {
                {
                    put("id", "1");
                }
            });
            runner.run(1, false);
            runner.assertTransferCount(REL_FAILURE, 1);
            runner.assertTransferCount(REL_CONNECTION_FAILURE, 0);
            runner.assertTransferCount(REL_SUCCESS, 0);
            TestPutSolrRecord.verifySolrDocuments(getSolrClient(), Collections.EMPTY_LIST);
        } finally {
            try {
                getSolrClient().close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Test
    public void testPutSolrOnTriggerIndexForAnArrayOfNestedRecord() throws IOException, InitializationException, SolrServerException {
        final SolrClient solrClient = TestPutSolrRecord.createEmbeddedSolrClient(TestPutSolrRecord.DEFAULT_SOLR_CORE);
        TestPutSolrRecord.TestableProcessor proc = new TestPutSolrRecord.TestableProcessor(solrClient);
        TestRunner runner = TestPutSolrRecord.createDefaultTestRunner(proc);
        MockRecordParser recordParser = new MockRecordParser();
        runner.addControllerService("parser", recordParser);
        runner.enableControllerService(recordParser);
        runner.setProperty(RECORD_READER, "parser");
        runner.setProperty(UPDATE_PATH, "/update");
        recordParser.addSchemaField("id", INT);
        recordParser.addSchemaField("first", STRING);
        recordParser.addSchemaField("last", STRING);
        recordParser.addSchemaField("grade", INT);
        recordParser.addSchemaField("exams", ARRAY);
        final List<RecordField> fields = new ArrayList<>();
        fields.add(new RecordField("subject", STRING.getDataType()));
        fields.add(new RecordField("test", STRING.getDataType()));
        fields.add(new RecordField("marks", INT.getDataType()));
        RecordSchema schema = new org.apache.nifi.serialization.SimpleRecordSchema(fields);
        Map<String, Object> values1 = new HashMap<>();
        values1.put("subject", "Chemistry");
        values1.put("test", "term1");
        values1.put("marks", 98);
        final Record record1 = new org.apache.nifi.serialization.record.MapRecord(schema, values1);
        Map<String, Object> values2 = new HashMap<>();
        values2.put("subject", "Maths");
        values2.put("test", "term1");
        values2.put("marks", 98);
        final Record record2 = new org.apache.nifi.serialization.record.MapRecord(schema, values2);
        recordParser.addRecord(1, "Abhinav", "R", 8, new Record[]{ record1, record2 });
        SolrDocument solrDocument = new SolrDocument();
        solrDocument.put("id", 1);
        solrDocument.put("first", "Abhinav");
        solrDocument.put("last", "R");
        solrDocument.put("grade", 8);
        solrDocument.put("exams_subject", Stream.of("Chemistry", "Maths").collect(Collectors.toList()));
        solrDocument.put("exams_test", Stream.of("term1", "term1").collect(Collectors.toList()));
        solrDocument.put("exams_marks", Stream.of(98, 98).collect(Collectors.toList()));
        try {
            runner.enqueue(new byte[0], new HashMap<String, String>() {
                {
                    put("id", "1");
                }
            });
            runner.run(1, false);
            TestPutSolrRecord.verifySolrDocuments(solrClient, Collections.singletonList(solrDocument));
            runner.assertTransferCount(REL_FAILURE, 0);
            runner.assertTransferCount(REL_CONNECTION_FAILURE, 0);
            runner.assertTransferCount(REL_SUCCESS, 1);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                getSolrClient().close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Test
    public void testCollectionExpressionLanguage() throws IOException, InitializationException, SolrServerException {
        final String collection = "collection1";
        final TestPutSolrRecord.CollectionVerifyingProcessor proc = new TestPutSolrRecord.CollectionVerifyingProcessor(collection);
        TestRunner runner = TestRunners.newTestRunner(proc);
        MockRecordParser recordParser = new MockRecordParser();
        runner.addControllerService("parser", recordParser);
        runner.enableControllerService(recordParser);
        runner.setProperty(RECORD_READER, "parser");
        recordParser.addSchemaField("id", INT);
        recordParser.addSchemaField("first", STRING);
        recordParser.addSchemaField("last", STRING);
        recordParser.addSchemaField("grade", INT);
        recordParser.addSchemaField("subject", STRING);
        recordParser.addSchemaField("test", STRING);
        recordParser.addSchemaField("marks", INT);
        recordParser.addRecord(1, "Abhinav", "R", 8, "Chemistry", "term1", 98);
        runner.setProperty(SOLR_TYPE, SOLR_TYPE_CLOUD.getValue());
        runner.setProperty(SOLR_LOCATION, "localhost:9983");
        runner.setProperty(COLLECTION, "${solr.collection}");
        final Map<String, String> attributes = new HashMap<>();
        attributes.put("solr.collection", collection);
        attributes.put("id", "1");
        try {
            runner.enqueue(new byte[0], attributes);
            runner.run(1, false);
            runner.assertTransferCount(REL_FAILURE, 0);
            runner.assertTransferCount(REL_CONNECTION_FAILURE, 0);
            runner.assertTransferCount(REL_SUCCESS, 1);
        } finally {
            try {
                getSolrClient().close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Test
    public void testSolrServerExceptionShouldRouteToFailure() throws IOException, InitializationException, SolrServerException {
        final Throwable throwable = new SolrServerException("Invalid Document");
        final TestPutSolrRecord.ExceptionThrowingProcessor proc = new TestPutSolrRecord.ExceptionThrowingProcessor(throwable);
        final TestRunner runner = TestPutSolrRecord.createDefaultTestRunner(proc);
        runner.setProperty(UPDATE_PATH, "/update");
        MockRecordParser recordParser = new MockRecordParser();
        recordParser.addRecord(1, "Abhinav", "R", 8, "Chemistry", "term1", 98);
        runner.addControllerService("parser", recordParser);
        runner.enableControllerService(recordParser);
        runner.setProperty(RECORD_READER, "parser");
        try {
            runner.enqueue(new byte[0], new HashMap<String, String>() {
                {
                    put("id", "1");
                }
            });
            runner.run(1, false);
            runner.assertAllFlowFilesTransferred(REL_FAILURE, 1);
            Mockito.verify(getSolrClient(), Mockito.times(1)).request(ArgumentMatchers.any(SolrRequest.class), ArgumentMatchers.eq(((String) (null))));
        } finally {
            try {
                getSolrClient().close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Test
    public void testSolrServerExceptionCausedByIOExceptionShouldRouteToConnectionFailure() throws IOException, InitializationException, SolrServerException {
        final Throwable throwable = new SolrServerException(new IOException("Error communicating with Solr"));
        final TestPutSolrRecord.ExceptionThrowingProcessor proc = new TestPutSolrRecord.ExceptionThrowingProcessor(throwable);
        final TestRunner runner = TestPutSolrRecord.createDefaultTestRunner(proc);
        runner.setProperty(UPDATE_PATH, "/update");
        MockRecordParser recordParser = new MockRecordParser();
        recordParser.addRecord(1, "Abhinav", "R", 8, "Chemistry", "term1", 98);
        runner.addControllerService("parser", recordParser);
        runner.enableControllerService(recordParser);
        runner.setProperty(RECORD_READER, "parser");
        try {
            runner.enqueue(new byte[0], new HashMap<String, String>() {
                {
                    put("id", "1");
                }
            });
            runner.run();
            runner.assertAllFlowFilesTransferred(REL_CONNECTION_FAILURE, 1);
            Mockito.verify(getSolrClient(), Mockito.times(1)).request(ArgumentMatchers.any(SolrRequest.class), ArgumentMatchers.eq(((String) (null))));
        } finally {
            try {
                getSolrClient().close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Test
    public void testSolrExceptionShouldRouteToFailure() throws IOException, InitializationException, SolrServerException {
        final Throwable throwable = new org.apache.solr.common.SolrException(ErrorCode.BAD_REQUEST, "Error");
        final TestPutSolrRecord.ExceptionThrowingProcessor proc = new TestPutSolrRecord.ExceptionThrowingProcessor(throwable);
        final TestRunner runner = TestPutSolrRecord.createDefaultTestRunner(proc);
        runner.setProperty(UPDATE_PATH, "/update");
        MockRecordParser recordParser = new MockRecordParser();
        recordParser.addRecord(1, "Abhinav", "R", 8, "Chemistry", "term1", 98);
        runner.addControllerService("parser", recordParser);
        runner.enableControllerService(recordParser);
        runner.setProperty(RECORD_READER, "parser");
        try {
            runner.enqueue(new byte[0], new HashMap<String, String>() {
                {
                    put("id", "1");
                }
            });
            runner.run();
            runner.assertAllFlowFilesTransferred(REL_FAILURE, 1);
            Mockito.verify(getSolrClient(), Mockito.times(1)).request(ArgumentMatchers.any(SolrRequest.class), ArgumentMatchers.eq(((String) (null))));
        } finally {
            try {
                getSolrClient().close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Test
    public void testRemoteSolrExceptionShouldRouteToFailure() throws IOException, InitializationException, SolrServerException {
        final Throwable throwable = new HttpSolrClient.RemoteSolrException("host", 401, "error", new NumberFormatException());
        final TestPutSolrRecord.ExceptionThrowingProcessor proc = new TestPutSolrRecord.ExceptionThrowingProcessor(throwable);
        final TestRunner runner = TestPutSolrRecord.createDefaultTestRunner(proc);
        runner.setProperty(UPDATE_PATH, "/update");
        MockRecordParser recordParser = new MockRecordParser();
        recordParser.addRecord(1, "Abhinav", "R", 8, "Chemistry", "term1", 98);
        runner.addControllerService("parser", recordParser);
        runner.enableControllerService(recordParser);
        runner.setProperty(RECORD_READER, "parser");
        try {
            runner.enqueue(new byte[0], new HashMap<String, String>() {
                {
                    put("id", "1");
                }
            });
            runner.run();
            runner.assertAllFlowFilesTransferred(REL_FAILURE, 1);
            Mockito.verify(getSolrClient(), Mockito.times(1)).request(ArgumentMatchers.any(SolrRequest.class), ArgumentMatchers.eq(((String) (null))));
        } finally {
            try {
                getSolrClient().close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Test
    public void testIOExceptionShouldRouteToConnectionFailure() throws IOException, InitializationException, SolrServerException {
        final Throwable throwable = new IOException("Error communicating with Solr");
        final TestPutSolrRecord.ExceptionThrowingProcessor proc = new TestPutSolrRecord.ExceptionThrowingProcessor(throwable);
        final TestRunner runner = TestPutSolrRecord.createDefaultTestRunner(proc);
        runner.setProperty(UPDATE_PATH, "/update");
        MockRecordParser recordParser = new MockRecordParser();
        recordParser.addRecord(1, "Abhinav", "R", 8, "Chemistry", "term1", 98);
        runner.addControllerService("parser", recordParser);
        runner.enableControllerService(recordParser);
        runner.setProperty(RECORD_READER, "parser");
        try {
            runner.enqueue(new byte[0], new HashMap<String, String>() {
                {
                    put("id", "1");
                }
            });
            runner.run();
            runner.assertAllFlowFilesTransferred(REL_CONNECTION_FAILURE, 1);
            Mockito.verify(getSolrClient(), Mockito.times(1)).request(ArgumentMatchers.any(SolrRequest.class), ArgumentMatchers.eq(((String) (null))));
        } finally {
            try {
                getSolrClient().close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Test
    public void testSolrTypeCloudShouldRequireCollection() throws InitializationException {
        final TestRunner runner = TestRunners.newTestRunner(PutSolrRecord.class);
        MockRecordParser recordParser = new MockRecordParser();
        recordParser.addRecord(1, "Abhinav", "R", 8, "Chemistry", "term1", 98);
        runner.addControllerService("parser", recordParser);
        runner.enableControllerService(recordParser);
        runner.setProperty(RECORD_READER, "parser");
        runner.setProperty(SOLR_TYPE, SOLR_TYPE_CLOUD.getValue());
        runner.setProperty(SOLR_LOCATION, "http://localhost:8443/solr");
        runner.assertNotValid();
        runner.setProperty(COLLECTION, "someCollection1");
        runner.assertValid();
    }

    @Test
    public void testSolrTypeStandardShouldNotRequireCollection() throws InitializationException {
        final TestRunner runner = TestRunners.newTestRunner(PutSolrRecord.class);
        MockRecordParser recordParser = new MockRecordParser();
        recordParser.addRecord(1, "Abhinav", "R", 8, "Chemistry", "term1", 98);
        runner.addControllerService("parser", recordParser);
        runner.enableControllerService(recordParser);
        runner.setProperty(RECORD_READER, "parser");
        runner.setProperty(SOLR_TYPE, SOLR_TYPE_STANDARD.getValue());
        runner.setProperty(SOLR_LOCATION, "http://localhost:8443/solr");
        runner.assertValid();
    }

    @Test
    public void testHttpsUrlShouldRequireSSLContext() throws InitializationException {
        final TestRunner runner = TestRunners.newTestRunner(PutSolrRecord.class);
        MockRecordParser recordParser = new MockRecordParser();
        recordParser.addRecord(1, "Abhinav", "R", 8, "Chemistry", "term1", 98);
        runner.addControllerService("parser", recordParser);
        runner.enableControllerService(recordParser);
        runner.setProperty(RECORD_READER, "parser");
        runner.setProperty(SOLR_TYPE, SOLR_TYPE_STANDARD.getValue());
        runner.setProperty(SOLR_LOCATION, "https://localhost:8443/solr");
        runner.assertNotValid();
        final SSLContextService sslContextService = new TestPutSolrRecord.MockSSLContextService();
        runner.addControllerService("ssl-context", sslContextService);
        runner.enableControllerService(sslContextService);
        runner.setProperty(SSL_CONTEXT_SERVICE, "ssl-context");
        runner.assertValid();
    }

    @Test
    public void testHttpUrlShouldNotAllowSSLContext() throws InitializationException {
        final TestRunner runner = TestRunners.newTestRunner(PutSolrRecord.class);
        MockRecordParser recordParser = new MockRecordParser();
        recordParser.addRecord(1, "Abhinav", "R", 8, "Chemistry", "term1", 98);
        runner.addControllerService("parser", recordParser);
        runner.enableControllerService(recordParser);
        runner.setProperty(RECORD_READER, "parser");
        runner.setProperty(SOLR_TYPE, SOLR_TYPE_STANDARD.getValue());
        runner.setProperty(SOLR_LOCATION, "http://localhost:8443/solr");
        runner.assertValid();
        final SSLContextService sslContextService = new TestPutSolrRecord.MockSSLContextService();
        runner.addControllerService("ssl-context", sslContextService);
        runner.enableControllerService(sslContextService);
        runner.setProperty(SSL_CONTEXT_SERVICE, "ssl-context");
        runner.assertNotValid();
    }

    @Test
    public void testUsernamePasswordValidation() throws InitializationException {
        final TestRunner runner = TestRunners.newTestRunner(PutSolrRecord.class);
        MockRecordParser recordParser = new MockRecordParser();
        recordParser.addRecord(1, "Abhinav", "R", 8, "Chemistry", "term1", 98);
        runner.addControllerService("parser", recordParser);
        runner.enableControllerService(recordParser);
        runner.setProperty(RECORD_READER, "parser");
        runner.setProperty(SOLR_TYPE, SOLR_TYPE_STANDARD.getValue());
        runner.setProperty(SOLR_LOCATION, "http://localhost:8443/solr");
        runner.assertValid();
        runner.setProperty(BASIC_USERNAME, "user1");
        runner.assertNotValid();
        runner.setProperty(BASIC_PASSWORD, "password");
        runner.assertValid();
        runner.setProperty(BASIC_USERNAME, "");
        runner.assertNotValid();
        runner.setProperty(BASIC_USERNAME, "${solr.user}");
        runner.assertNotValid();
        runner.setVariable("solr.user", "solrRocks");
        runner.assertValid();
        runner.setProperty(BASIC_PASSWORD, "${solr.password}");
        runner.assertNotValid();
        runner.setVariable("solr.password", "solrRocksPassword");
        runner.assertValid();
    }

    // Override createSolrClient and return the passed in SolrClient
    private class TestableProcessor extends PutSolrRecord {
        private SolrClient solrClient;

        public TestableProcessor(SolrClient solrClient) {
            this.solrClient = solrClient;
        }

        @Override
        protected SolrClient createSolrClient(ProcessContext context, String solrLocation) {
            return solrClient;
        }
    }

    // Override the createSolrClient method to inject a custom SolrClient.
    private class CollectionVerifyingProcessor extends PutSolrRecord {
        private SolrClient mockSolrClient;

        private final String expectedCollection;

        public CollectionVerifyingProcessor(final String expectedCollection) {
            this.expectedCollection = expectedCollection;
        }

        @Override
        protected SolrClient createSolrClient(ProcessContext context, String solrLocation) {
            mockSolrClient = new SolrClient() {
                @Override
                public NamedList<Object> request(SolrRequest solrRequest, String s) throws IOException, SolrServerException {
                    Assert.assertEquals(expectedCollection, solrRequest.getParams().get(COLLECTION_PARAM_NAME));
                    return new NamedList();
                }

                @Override
                public void close() {
                }
            };
            return mockSolrClient;
        }
    }

    // Override the createSolrClient method to inject a Mock.
    private class ExceptionThrowingProcessor extends PutSolrRecord {
        private SolrClient mockSolrClient;

        private Throwable throwable;

        public ExceptionThrowingProcessor(Throwable throwable) {
            this.throwable = throwable;
        }

        @Override
        protected SolrClient createSolrClient(ProcessContext context, String solrLocation) {
            mockSolrClient = Mockito.mock(SolrClient.class);
            try {
                Mockito.when(mockSolrClient.request(ArgumentMatchers.any(SolrRequest.class), ArgumentMatchers.eq(((String) (null))))).thenThrow(throwable);
            } catch (SolrServerException | IOException e) {
                Assert.fail(getMessage());
            }
            return mockSolrClient;
        }
    }

    /**
     * Mock implementation so we don't need to have a real keystore/truststore available for testing.
     */
    private class MockSSLContextService extends AbstractControllerService implements SSLContextService {
        @Override
        public SSLContext createSSLContext(ClientAuth clientAuth) throws ProcessException {
            return null;
        }

        @Override
        public String getTrustStoreFile() {
            return null;
        }

        @Override
        public String getTrustStoreType() {
            return null;
        }

        @Override
        public String getTrustStorePassword() {
            return null;
        }

        @Override
        public boolean isTrustStoreConfigured() {
            return false;
        }

        @Override
        public String getKeyStoreFile() {
            return null;
        }

        @Override
        public String getKeyStoreType() {
            return null;
        }

        @Override
        public String getKeyStorePassword() {
            return null;
        }

        @Override
        public String getKeyPassword() {
            return null;
        }

        @Override
        public boolean isKeyStoreConfigured() {
            return false;
        }

        @Override
        public String getSslAlgorithm() {
            return null;
        }
    }
}

