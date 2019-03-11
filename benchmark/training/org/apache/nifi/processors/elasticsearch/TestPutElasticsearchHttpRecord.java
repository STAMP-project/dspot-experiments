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
package org.apache.nifi.processors.elasticsearch;


import AbstractElasticsearchHttpProcessor.CONNECT_TIMEOUT;
import AbstractElasticsearchHttpProcessor.ES_URL;
import ProvenanceEventType.SEND;
import PutElasticsearchHttpRecord.DATE_FORMAT;
import PutElasticsearchHttpRecord.ID_RECORD_PATH;
import PutElasticsearchHttpRecord.INDEX;
import PutElasticsearchHttpRecord.INDEX_OP;
import PutElasticsearchHttpRecord.LOG_ALL_ERRORS;
import PutElasticsearchHttpRecord.REL_FAILURE;
import PutElasticsearchHttpRecord.REL_RETRY;
import PutElasticsearchHttpRecord.REL_SUCCESS;
import PutElasticsearchHttpRecord.TIMESTAMP_FORMAT;
import PutElasticsearchHttpRecord.TIME_FORMAT;
import PutElasticsearchHttpRecord.TYPE;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import okhttp3.Call;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okio.Buffer;
import org.apache.nifi.processor.ProcessContext;
import org.apache.nifi.processor.exception.ProcessException;
import org.apache.nifi.provenance.ProvenanceEventRecord;
import org.apache.nifi.util.MockFlowFile;
import org.apache.nifi.util.TestRunner;
import org.apache.nifi.util.TestRunners;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


public class TestPutElasticsearchHttpRecord {
    private TestRunner runner;

    @Test
    public void testPutElasticSearchOnTriggerIndex() throws IOException {
        TestPutElasticsearchHttpRecord.PutElasticsearchHttpRecordTestProcessor processor = new TestPutElasticsearchHttpRecord.PutElasticsearchHttpRecordTestProcessor(false);
        processor.setRecordChecks(( record) -> {
            Assert.assertEquals(1, record.get("id"));
            Assert.assertEquals("re?1", record.get("name"));
            Assert.assertEquals(101, record.get("code"));
            Assert.assertEquals("20/12/2018", record.get("date"));
            Assert.assertEquals("6:55 PM", record.get("time"));
            Assert.assertEquals("20/12/2018 6:55 PM", record.get("ts"));
        }, ( record) -> {
            Assert.assertEquals(2, record.get("id"));
            Assert.assertEquals("re?2", record.get("name"));
            Assert.assertEquals(102, record.get("code"));
            Assert.assertEquals("20/12/2018", record.get("date"));
            Assert.assertEquals("6:55 PM", record.get("time"));
            Assert.assertEquals("20/12/2018 6:55 PM", record.get("ts"));
        }, ( record) -> {
            Assert.assertEquals(3, record.get("id"));
            Assert.assertEquals("re?3", record.get("name"));
            Assert.assertEquals(103, record.get("code"));
            Assert.assertEquals("20/12/2018", record.get("date"));
            Assert.assertEquals("6:55 PM", record.get("time"));
            Assert.assertEquals("20/12/2018 6:55 PM", record.get("ts"));
        }, ( record) -> {
            Assert.assertEquals(4, record.get("id"));
            Assert.assertEquals("re?4", record.get("name"));
            Assert.assertEquals(104, record.get("code"));
            Assert.assertEquals("20/12/2018", record.get("date"));
            Assert.assertEquals("6:55 PM", record.get("time"));
            Assert.assertEquals("20/12/2018 6:55 PM", record.get("ts"));
        });
        runner = TestRunners.newTestRunner(processor);// no failures

        generateTestData();
        runner.setProperty(ES_URL, "http://127.0.0.1:9200");
        runner.setProperty(INDEX, "doc");
        runner.setProperty(TYPE, "status");
        runner.setProperty(ID_RECORD_PATH, "/id");
        runner.setProperty(DATE_FORMAT, "d/M/yyyy");
        runner.setProperty(TIME_FORMAT, "h:m a");
        runner.setProperty(TIMESTAMP_FORMAT, "d/M/yyyy h:m a");
        runner.enqueue(new byte[0], new HashMap<String, String>() {
            {
                put("doc_id", "28039652140");
            }
        });
        runner.run(1, true, true);
        runner.assertAllFlowFilesTransferred(REL_SUCCESS, 1);
        final MockFlowFile out = runner.getFlowFilesForRelationship(REL_SUCCESS).get(0);
        Assert.assertNotNull(out);
        out.assertAttributeEquals("doc_id", "28039652140");
        out.assertAttributeEquals("record.count", "4");
        List<ProvenanceEventRecord> provEvents = runner.getProvenanceEvents();
        Assert.assertNotNull(provEvents);
        Assert.assertEquals(1, provEvents.size());
        Assert.assertEquals(SEND, provEvents.get(0).getEventType());
    }

    @Test
    public void testPutElasticSearchOnTriggerUpdate() throws IOException {
        runner = TestRunners.newTestRunner(new TestPutElasticsearchHttpRecord.PutElasticsearchHttpRecordTestProcessor(false));// no failures

        generateTestData();
        runner.setProperty(ES_URL, "http://127.0.0.1:9200");
        runner.setProperty(INDEX, "doc");
        runner.setProperty(TYPE, "status");
        runner.setProperty(ID_RECORD_PATH, "/id");
        runner.setProperty(INDEX_OP, "Update");
        runner.enqueue(new byte[0], new HashMap<String, String>() {
            {
                put("doc_id", "28039652140");
            }
        });
        runner.run(1, true, true);
        runner.assertAllFlowFilesTransferred(REL_SUCCESS, 1);
        final MockFlowFile out = runner.getFlowFilesForRelationship(REL_SUCCESS).get(0);
        Assert.assertNotNull(out);
        out.assertAttributeEquals("doc_id", "28039652140");
    }

    @Test
    public void testPutElasticSearchOnTriggerDelete() throws IOException {
        runner = TestRunners.newTestRunner(new TestPutElasticsearchHttpRecord.PutElasticsearchHttpRecordTestProcessor(false));// no failures

        generateTestData();
        runner.setProperty(ES_URL, "http://127.0.0.1:9200");
        runner.setProperty(INDEX, "doc");
        runner.setProperty(TYPE, "status");
        runner.setProperty(ID_RECORD_PATH, "/id");
        runner.setProperty(INDEX_OP, "DELETE");
        runner.enqueue(new byte[0], new HashMap<String, String>() {
            {
                put("doc_id", "28039652140");
            }
        });
        runner.run(1, true, true);
        runner.assertAllFlowFilesTransferred(REL_SUCCESS, 1);
        final MockFlowFile out = runner.getFlowFilesForRelationship(REL_SUCCESS).get(0);
        Assert.assertNotNull(out);
        out.assertAttributeEquals("doc_id", "28039652140");
    }

    @Test
    public void testPutElasticSearchOnTriggerEL() throws IOException {
        runner = TestRunners.newTestRunner(new TestPutElasticsearchHttpRecord.PutElasticsearchHttpRecordTestProcessor(false));// no failures

        generateTestData();
        runner.setProperty(ES_URL, "${es.url}");
        runner.setProperty(INDEX, "doc");
        runner.setProperty(TYPE, "status");
        runner.setProperty(ID_RECORD_PATH, "/id");
        runner.setProperty(CONNECT_TIMEOUT, "${connect.timeout}");
        runner.assertValid();
        runner.setVariable("es.url", "http://127.0.0.1:9200");
        runner.setVariable("connect.timeout", "5s");
        runner.enqueue(new byte[0], new HashMap<String, String>() {
            {
                put("doc_id", "28039652140");
            }
        });
        runner.run(1, true, true);
        runner.assertAllFlowFilesTransferred(REL_SUCCESS, 1);
        final MockFlowFile out = runner.getFlowFilesForRelationship(REL_SUCCESS).get(0);
        Assert.assertNotNull(out);
        out.assertAttributeEquals("doc_id", "28039652140");
    }

    @Test
    public void testPutElasticSearchOnTriggerBadIndexOp() throws IOException {
        runner = TestRunners.newTestRunner(new TestPutElasticsearchHttpRecord.PutElasticsearchHttpRecordTestProcessor(false));// no failures

        generateTestData();
        runner.setProperty(ES_URL, "http://127.0.0.1:9200");
        runner.setProperty(INDEX, "doc");
        runner.setProperty(TYPE, "status");
        runner.setProperty(ID_RECORD_PATH, "/id");
        runner.setProperty(INDEX_OP, "${no.attr}");
        runner.enqueue(new byte[0], new HashMap<String, String>() {
            {
                put("doc_id", "28039652140");
            }
        });
        runner.run(1, true, true);
        runner.assertAllFlowFilesTransferred(REL_FAILURE, 1);
        final MockFlowFile out = runner.getFlowFilesForRelationship(REL_FAILURE).get(0);
        Assert.assertNotNull(out);
        out.assertAttributeEquals("doc_id", "28039652140");
    }

    @Test
    public void testPutElasticSearchInvalidConfig() throws IOException {
        runner = TestRunners.newTestRunner(new TestPutElasticsearchHttpRecord.PutElasticsearchHttpRecordTestProcessor(false));// no failures

        generateTestData();
        runner.setProperty(ES_URL, "http://127.0.0.1:9200");
        runner.setProperty(INDEX, "doc");
        runner.assertNotValid();
        runner.setProperty(TYPE, "status");
        runner.assertValid();
        runner.setProperty(INDEX_OP, "");
        runner.assertNotValid();
        runner.setProperty(INDEX_OP, "index");
        runner.assertValid();
        runner.setProperty(INDEX_OP, "upsert");
        runner.assertNotValid();
    }

    @Test
    public void testPutElasticSearchOnTriggerWithFailures() throws IOException {
        TestPutElasticsearchHttpRecord.PutElasticsearchHttpRecordTestProcessor processor = new TestPutElasticsearchHttpRecord.PutElasticsearchHttpRecordTestProcessor(true);
        processor.setStatus(100, "Should fail");
        runner = TestRunners.newTestRunner(processor);// simulate failures

        generateTestData();
        runner.setProperty(ES_URL, "http://127.0.0.1:9200");
        runner.setProperty(INDEX, "doc");
        runner.setProperty(TYPE, "status");
        runner.setProperty(ID_RECORD_PATH, "/id");
        runner.enqueue(new byte[0], new HashMap<String, String>() {
            {
                put("doc_id", "28039652140");
            }
        });
        runner.run(1, true, true);
        runner.assertAllFlowFilesTransferred(REL_FAILURE, 1);
        runner.clearTransferState();
        processor.setStatus(500, "Should retry");
        runner.enqueue(new byte[0], new HashMap<String, String>() {
            {
                put("doc_id", "28039652140");
            }
        });
        runner.run(1, true, true);
        runner.assertAllFlowFilesTransferred(REL_RETRY, 1);
    }

    @Test
    public void testPutElasticSearchOnTriggerWithConnectException() throws IOException {
        TestPutElasticsearchHttpRecord.PutElasticsearchHttpRecordTestProcessor processor = new TestPutElasticsearchHttpRecord.PutElasticsearchHttpRecordTestProcessor(true);
        processor.setStatus((-1), "Connection Exception");
        runner = TestRunners.newTestRunner(processor);// simulate failures

        generateTestData();
        runner.setProperty(ES_URL, "http://127.0.0.1:9200");
        runner.setProperty(INDEX, "doc");
        runner.setProperty(TYPE, "status");
        runner.setProperty(ID_RECORD_PATH, "/id");
        runner.enqueue(new byte[0], new HashMap<String, String>() {
            {
                put("doc_id", "28039652140");
            }
        });
        runner.run(1, true, true);
        runner.assertAllFlowFilesTransferred(REL_FAILURE, 1);
    }

    @Test
    public void testPutElasticsearchOnTriggerWithNoIdPath() throws Exception {
        runner = TestRunners.newTestRunner(new TestPutElasticsearchHttpRecord.PutElasticsearchHttpRecordTestProcessor(false));
        generateTestData();
        runner.setProperty(ES_URL, "http://127.0.0.1:9200");
        runner.setProperty(INDEX, "doc");
        runner.setProperty(TYPE, "status");
        runner.setProperty(ID_RECORD_PATH, "/none");// Field does not exist

        runner.enqueue(new byte[0]);
        runner.run(1, true, true);
        runner.assertTransferCount(REL_FAILURE, 1);
        runner.assertTransferCount(REL_SUCCESS, 0);
    }

    @Test
    public void testPutElasticsearchOnTriggerWithNoIdField() throws IOException {
        runner = TestRunners.newTestRunner(new TestPutElasticsearchHttpRecord.PutElasticsearchHttpRecordTestProcessor(true));// simulate failures

        generateTestData();
        runner.setProperty(ES_URL, "http://127.0.0.1:9200");
        runner.setProperty(INDEX, "doc");
        runner.setProperty(TYPE, "status");
        runner.enqueue(new byte[0]);
        runner.run(1, true, true);
        runner.assertTransferCount(REL_SUCCESS, 0);
        runner.assertTransferCount(REL_FAILURE, 1);
        MockFlowFile flowFile = runner.getFlowFilesForRelationship(REL_FAILURE).get(0);
        flowFile.assertAttributeEquals("failure.count", "1");
    }

    @Test
    public void testPutElasticsearchOnTriggerWithIndexFromAttribute() throws IOException {
        runner = TestRunners.newTestRunner(new TestPutElasticsearchHttpRecord.PutElasticsearchHttpRecordTestProcessor(false));
        generateTestData();
        runner.setProperty(ES_URL, "http://127.0.0.1:9200");
        runner.setProperty(INDEX, "${i}");
        runner.setProperty(TYPE, "${type}");
        runner.setProperty(ID_RECORD_PATH, "/id");
        runner.enqueue(new byte[0], new HashMap<String, String>() {
            {
                put("doc_id", "28039652144");
                put("i", "doc");
                put("type", "status");
            }
        });
        runner.run(1, true, true);
        runner.assertAllFlowFilesTransferred(REL_SUCCESS, 1);
        final MockFlowFile out = runner.getFlowFilesForRelationship(REL_SUCCESS).get(0);
        Assert.assertNotNull(out);
        runner.clearTransferState();
        // Now try an empty attribute value, should fail
        runner.enqueue(new byte[0], new HashMap<String, String>() {
            {
                put("doc_id", "28039652144");
                put("type", "status");
            }
        });
        runner.run(1, true, true);
        runner.assertAllFlowFilesTransferred(REL_FAILURE, 1);
        final MockFlowFile out2 = runner.getFlowFilesForRelationship(REL_FAILURE).get(0);
        Assert.assertNotNull(out2);
    }

    @Test
    public void testPutElasticSearchOnTriggerWithInvalidIndexOp() throws IOException {
        runner = TestRunners.newTestRunner(new TestPutElasticsearchHttpRecord.PutElasticsearchHttpRecordTestProcessor(false));// no failures

        generateTestData();
        runner.setProperty(ES_URL, "http://127.0.0.1:9200");
        runner.setProperty(INDEX, "doc");
        runner.assertNotValid();
        runner.setProperty(TYPE, "status");
        runner.assertValid();
        runner.setProperty(ID_RECORD_PATH, "/id");
        runner.assertValid();
        runner.setProperty(INDEX_OP, "index_fail");
        runner.assertValid();
        runner.enqueue(new byte[0], new HashMap<String, String>() {
            {
                put("doc_id", "28039652140");
            }
        });
        runner.run(1, true, true);
        runner.assertAllFlowFilesTransferred(REL_FAILURE, 1);
        final MockFlowFile out = runner.getFlowFilesForRelationship(REL_FAILURE).get(0);
        Assert.assertNotNull(out);
    }

    @Test
    public void testPutElasticSearchOnTriggerQueryParameter() throws IOException {
        TestPutElasticsearchHttpRecord.PutElasticsearchHttpRecordTestProcessor p = new TestPutElasticsearchHttpRecord.PutElasticsearchHttpRecordTestProcessor(false);// no failures

        p.setExpectedUrl("http://127.0.0.1:9200/_bulk?pipeline=my-pipeline");
        runner = TestRunners.newTestRunner(p);
        generateTestData();
        runner.setProperty(ES_URL, "http://127.0.0.1:9200");
        runner.setProperty(INDEX, "doc");
        runner.setProperty(TYPE, "status");
        runner.setProperty(ID_RECORD_PATH, "/id");
        // Set dynamic property, to be added to the URL as a query parameter
        runner.setProperty("pipeline", "my-pipeline");
        runner.enqueue(new byte[0], new HashMap<String, String>() {
            {
                put("doc_id", "28039652140");
            }
        });
        runner.run(1, true, true);
        runner.assertAllFlowFilesTransferred(REL_SUCCESS, 1);
        final MockFlowFile out = runner.getFlowFilesForRelationship(REL_SUCCESS).get(0);
        Assert.assertNotNull(out);
        out.assertAttributeEquals("doc_id", "28039652140");
        List<ProvenanceEventRecord> provEvents = runner.getProvenanceEvents();
        Assert.assertNotNull(provEvents);
        Assert.assertEquals(1, provEvents.size());
        Assert.assertEquals(SEND, provEvents.get(0).getEventType());
    }

    @Test
    public void testPutElasticsearchOnTriggerFailureWithWriter() throws IOException {
        runner = TestRunners.newTestRunner(new TestPutElasticsearchHttpRecord.PutElasticsearchHttpRecordTestProcessor(true));// simulate failures

        generateTestData(1);
        generateWriter();
        runner.setProperty(ES_URL, "http://127.0.0.1:9200");
        runner.setProperty(INDEX, "doc");
        runner.setProperty(TYPE, "status");
        runner.enqueue(new byte[0]);
        runner.run(1, true, true);
        runner.assertTransferCount(REL_SUCCESS, 0);
        runner.assertTransferCount(REL_FAILURE, 1);
        MockFlowFile flowFileFailure = runner.getFlowFilesForRelationship(REL_FAILURE).get(0);
        flowFileFailure.assertAttributeEquals("failure.count", "1");
    }

    @Test
    public void testPutElasticsearchOnTriggerFailureWithWriterMultipleRecords() throws IOException {
        runner = TestRunners.newTestRunner(new TestPutElasticsearchHttpRecord.PutElasticsearchHttpRecordTestProcessor(2));// simulate failures

        generateTestData();
        generateWriter();
        runner.setProperty(ES_URL, "http://127.0.0.1:9200");
        runner.setProperty(INDEX, "doc");
        runner.setProperty(TYPE, "status");
        runner.enqueue(new byte[0]);
        runner.run(1, true, true);
        runner.assertTransferCount(REL_SUCCESS, 1);
        runner.assertTransferCount(REL_FAILURE, 1);
        MockFlowFile flowFileSuccess = runner.getFlowFilesForRelationship(REL_SUCCESS).get(0);
        flowFileSuccess.assertAttributeEquals("record.count", "2");
        MockFlowFile flowFileFailure = runner.getFlowFilesForRelationship(REL_FAILURE).get(0);
        flowFileFailure.assertAttributeEquals("record.count", "2");
        flowFileFailure.assertAttributeEquals("failure.count", "2");
        Assert.assertEquals(1, runner.getLogger().getErrorMessages().size());
    }

    @Test
    public void testPutElasticsearchOnTriggerFailureWithWriterMultipleRecordsLogging() throws IOException {
        runner = TestRunners.newTestRunner(new TestPutElasticsearchHttpRecord.PutElasticsearchHttpRecordTestProcessor(2));// simulate failures

        generateTestData();
        generateWriter();
        runner.setProperty(ES_URL, "http://127.0.0.1:9200");
        runner.setProperty(INDEX, "doc");
        runner.setProperty(TYPE, "status");
        runner.setProperty(LOG_ALL_ERRORS, "true");
        runner.enqueue(new byte[0]);
        runner.run(1, true, true);
        runner.assertTransferCount(REL_SUCCESS, 1);
        runner.assertTransferCount(REL_FAILURE, 1);
        MockFlowFile flowFileSuccess = runner.getFlowFilesForRelationship(REL_SUCCESS).get(0);
        flowFileSuccess.assertAttributeEquals("record.count", "2");
        MockFlowFile flowFileFailure = runner.getFlowFilesForRelationship(REL_FAILURE).get(0);
        flowFileFailure.assertAttributeEquals("record.count", "2");
        flowFileFailure.assertAttributeEquals("failure.count", "2");
        Assert.assertEquals(2, runner.getLogger().getErrorMessages().size());
    }

    /**
     * A Test class that extends the processor in order to inject/mock behavior
     */
    private static class PutElasticsearchHttpRecordTestProcessor extends PutElasticsearchHttpRecord {
        int numResponseFailures = 0;

        OkHttpClient client;

        int statusCode = 200;

        String statusMessage = "OK";

        String expectedUrl = null;

        Consumer<Map>[] recordChecks;

        PutElasticsearchHttpRecordTestProcessor(boolean responseHasFailures) {
            this.numResponseFailures = (responseHasFailures) ? 1 : 0;
        }

        PutElasticsearchHttpRecordTestProcessor(int numResponseFailures) {
            this.numResponseFailures = numResponseFailures;
        }

        void setStatus(int code, String message) {
            statusCode = code;
            statusMessage = message;
        }

        void setExpectedUrl(String url) {
            expectedUrl = url;
        }

        @SafeVarargs
        final void setRecordChecks(Consumer<Map>... checks) {
            recordChecks = checks;
        }

        @Override
        protected void createElasticsearchClient(ProcessContext context) throws ProcessException {
            client = Mockito.mock(OkHttpClient.class);
            Mockito.when(client.newCall(ArgumentMatchers.any(Request.class))).thenAnswer(( invocationOnMock) -> {
                final Call call = mock(.class);
                if ((statusCode) != (-1)) {
                    Request realRequest = ((Request) (invocationOnMock.getArguments()[0]));
                    assertTrue((((expectedUrl) == null) || (expectedUrl.equals(realRequest.url().toString()))));
                    if ((recordChecks) != null) {
                        final ObjectMapper mapper = new ObjectMapper();
                        Buffer sink = new Buffer();
                        realRequest.body().writeTo(sink);
                        String line;
                        int recordIndex = 0;
                        boolean content = false;
                        while ((line = sink.readUtf8Line()) != null) {
                            if (content) {
                                content = false;
                                if (recordIndex < recordChecks.length) {
                                    recordChecks[(recordIndex++)].accept(mapper.readValue(line, .class));
                                }
                            } else {
                                content = true;
                            }
                        } 
                    }
                    StringBuilder sb = new StringBuilder("{\"took\": 1, \"errors\": \"");
                    sb.append(((numResponseFailures) > 0));
                    sb.append("\", \"items\": [");
                    for (int i = 0; i < (numResponseFailures); i++) {
                        // This case is for a status code of 200 for the bulk response itself, but with an error (of 400) inside
                        sb.append("{\"index\":{\"_index\":\"doc\",\"_type\":\"status\",\"_id\":\"28039652140\",\"status\":\"400\",");
                        sb.append("\"error\":{\"type\":\"mapper_parsing_exception\",\"reason\":\"failed to parse [gender]\",");
                        sb.append("\"caused_by\":{\"type\":\"json_parse_exception\",\"reason\":\"Unexpected end-of-input in VALUE_STRING\\n at ");
                        sb.append("[Source: org.elasticsearch.common.io.stream.InputStreamStreamInput@1a2e3ac4; line: 1, column: 39]\"}}}},");
                    }
                    sb.append("{\"index\":{\"_index\":\"doc\",\"_type\":\"status\",\"_id\":\"28039652140\",\"status\":");
                    sb.append(statusCode);
                    sb.append(",\"_source\":{\"text\": \"This is a test document\"}}}");
                    sb.append("]}");
                    Response mockResponse = new Response.Builder().request(realRequest).protocol(Protocol.HTTP_1_1).code(statusCode).message(statusMessage).body(ResponseBody.create(MediaType.parse("application/json"), sb.toString())).build();
                    when(call.execute()).thenReturn(mockResponse);
                } else {
                    when(call.execute()).thenThrow(.class);
                }
                return call;
            });
        }

        @Override
        protected OkHttpClient getClient() {
            return client;
        }
    }

    @Test(expected = AssertionError.class)
    public void testPutElasticSearchBadHostInEL() throws IOException {
        final TestRunner runner = TestRunners.newTestRunner(new PutElasticsearchHttpRecord());
        runner.setProperty(ES_URL, "${es.url}");
        runner.setProperty(INDEX, "doc");
        runner.setProperty(TYPE, "status");
        runner.setProperty(ID_RECORD_PATH, "/id");
        runner.assertValid();
        runner.enqueue(new byte[0], new HashMap<String, String>() {
            {
                put("doc_id", "1");
            }
        });
        runner.run();
    }
}

