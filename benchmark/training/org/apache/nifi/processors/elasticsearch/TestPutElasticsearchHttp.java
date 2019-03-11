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
import Protocol.HTTP_1_1;
import PutElasticsearchHttp.BATCH_SIZE;
import PutElasticsearchHttp.ID_ATTRIBUTE;
import PutElasticsearchHttp.INDEX;
import PutElasticsearchHttp.INDEX_OP;
import PutElasticsearchHttp.REL_FAILURE;
import PutElasticsearchHttp.REL_RETRY;
import PutElasticsearchHttp.REL_SUCCESS;
import PutElasticsearchHttp.TYPE;
import java.io.IOException;
import java.net.ConnectException;
import java.util.HashMap;
import okhttp3.Call;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import org.apache.nifi.processor.ProcessContext;
import org.apache.nifi.processor.exception.ProcessException;
import org.apache.nifi.util.MockFlowFile;
import org.apache.nifi.util.TestRunner;
import org.apache.nifi.util.TestRunners;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;


public class TestPutElasticsearchHttp {
    private static byte[] docExample;

    private TestRunner runner;

    @Test
    public void testPutElasticSearchOnTriggerIndex() throws IOException {
        runner = TestRunners.newTestRunner(new TestPutElasticsearchHttp.PutElasticsearchTestProcessor(false));// no failures

        runner.setProperty(ES_URL, "http://127.0.0.1:9200");
        runner.setProperty(INDEX, "doc");
        runner.setProperty(TYPE, "status");
        runner.setProperty(BATCH_SIZE, "1");
        runner.setProperty(ID_ATTRIBUTE, "doc_id");
        runner.enqueue(TestPutElasticsearchHttp.docExample, new HashMap<String, String>() {
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
    public void testPutElasticSearchOnTriggerUpdate() throws IOException {
        runner = TestRunners.newTestRunner(new TestPutElasticsearchHttp.PutElasticsearchTestProcessor(false));// no failures

        runner.setProperty(ES_URL, "http://127.0.0.1:9200");
        runner.setProperty(INDEX, "doc");
        runner.setProperty(TYPE, "status");
        runner.setProperty(BATCH_SIZE, "1");
        runner.setProperty(ID_ATTRIBUTE, "doc_id");
        runner.setProperty(INDEX_OP, "Update");
        runner.enqueue(TestPutElasticsearchHttp.docExample, new HashMap<String, String>() {
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
        runner = TestRunners.newTestRunner(new TestPutElasticsearchHttp.PutElasticsearchTestProcessor(false));// no failures

        runner.setProperty(ES_URL, "http://127.0.0.1:9200");
        runner.setProperty(INDEX, "doc");
        runner.setProperty(TYPE, "status");
        runner.setProperty(BATCH_SIZE, "1");
        runner.setProperty(ID_ATTRIBUTE, "doc_id");
        runner.setProperty(INDEX_OP, "DELETE");
        runner.enqueue(TestPutElasticsearchHttp.docExample, new HashMap<String, String>() {
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
        runner = TestRunners.newTestRunner(new TestPutElasticsearchHttp.PutElasticsearchTestProcessor(false));// no failures

        runner.setProperty(ES_URL, "${es.url}");
        runner.setProperty(INDEX, "doc");
        runner.setProperty(TYPE, "status");
        runner.setProperty(BATCH_SIZE, "1");
        runner.setProperty(ID_ATTRIBUTE, "doc_id");
        runner.setProperty(CONNECT_TIMEOUT, "${connect.timeout}");
        runner.assertValid();
        runner.setVariable("es.url", "http://127.0.0.1:9200");
        runner.setVariable("connect.timeout", "5s");
        runner.enqueue(TestPutElasticsearchHttp.docExample, new HashMap<String, String>() {
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
        runner = TestRunners.newTestRunner(new TestPutElasticsearchHttp.PutElasticsearchTestProcessor(false));// no failures

        runner.setProperty(ES_URL, "http://127.0.0.1:9200");
        runner.setProperty(INDEX, "doc");
        runner.setProperty(TYPE, "status");
        runner.setProperty(BATCH_SIZE, "1");
        runner.setProperty(ID_ATTRIBUTE, "doc_id");
        runner.setProperty(INDEX_OP, "${no.attr}");
        runner.enqueue(TestPutElasticsearchHttp.docExample, new HashMap<String, String>() {
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
        runner = TestRunners.newTestRunner(new TestPutElasticsearchHttp.PutElasticsearchTestProcessor(false));// no failures

        runner.setProperty(ES_URL, "http://127.0.0.1:9200");
        runner.setProperty(INDEX, "doc");
        runner.assertNotValid();
        runner.setProperty(TYPE, "status");
        runner.setProperty(BATCH_SIZE, "1");
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
        TestPutElasticsearchHttp.PutElasticsearchTestProcessor processor = new TestPutElasticsearchHttp.PutElasticsearchTestProcessor(true);
        processor.setStatus(100, "Should fail");
        runner = TestRunners.newTestRunner(processor);// simulate failures

        runner.setProperty(ES_URL, "http://127.0.0.1:9200");
        runner.setProperty(INDEX, "doc");
        runner.setProperty(TYPE, "status");
        runner.setProperty(BATCH_SIZE, "1");
        runner.setProperty(ID_ATTRIBUTE, "doc_id");
        runner.enqueue(TestPutElasticsearchHttp.docExample, new HashMap<String, String>() {
            {
                put("doc_id", "28039652140");
            }
        });
        runner.run(1, true, true);
        runner.assertAllFlowFilesTransferred(REL_FAILURE, 1);
        runner.clearTransferState();
        processor.setStatus(500, "Should retry");
        runner.enqueue(TestPutElasticsearchHttp.docExample, new HashMap<String, String>() {
            {
                put("doc_id", "28039652140");
            }
        });
        runner.run(1, true, true);
        runner.assertAllFlowFilesTransferred(REL_RETRY, 1);
    }

    @Test
    public void testPutElasticSearchOnTriggerWithConnectException() throws IOException {
        TestPutElasticsearchHttp.PutElasticsearchTestProcessor processor = new TestPutElasticsearchHttp.PutElasticsearchTestProcessor(true);
        processor.setStatus((-1), "Connection Exception");
        runner = TestRunners.newTestRunner(processor);// simulate failures

        runner.setProperty(ES_URL, "http://127.0.0.1:9200");
        runner.setProperty(INDEX, "doc");
        runner.setProperty(TYPE, "status");
        runner.setProperty(BATCH_SIZE, "1");
        runner.setProperty(ID_ATTRIBUTE, "doc_id");
        runner.enqueue(TestPutElasticsearchHttp.docExample, new HashMap<String, String>() {
            {
                put("doc_id", "28039652140");
            }
        });
        runner.run(1, true, true);
        runner.assertAllFlowFilesTransferred(REL_FAILURE, 1);
    }

    @Test
    public void testPutElasticsearchOnTriggerWithNoIdAttribute() throws IOException {
        runner = TestRunners.newTestRunner(new TestPutElasticsearchHttp.PutElasticsearchTestProcessor(true));// simulate failures

        runner.setProperty(ES_URL, "http://127.0.0.1:9200");
        runner.setProperty(INDEX, "doc");
        runner.setProperty(TYPE, "status");
        runner.setProperty(BATCH_SIZE, "2");
        runner.setProperty(ID_ATTRIBUTE, "doc_id");
        runner.enqueue(TestPutElasticsearchHttp.docExample);
        runner.enqueue(TestPutElasticsearchHttp.docExample);
        runner.run(1, true, true);
        runner.assertTransferCount(REL_FAILURE, 1);
        runner.assertTransferCount(REL_SUCCESS, 1);
        final MockFlowFile out = runner.getFlowFilesForRelationship(REL_FAILURE).get(0);
        Assert.assertNotNull(out);
    }

    @Test
    public void testPutElasticsearchOnTriggerWithIndexFromAttribute() throws IOException {
        runner = TestRunners.newTestRunner(new TestPutElasticsearchHttp.PutElasticsearchTestProcessor(false));
        runner.setProperty(ES_URL, "http://127.0.0.1:9200");
        runner.setProperty(INDEX, "${i}");
        runner.setProperty(TYPE, "${type}");
        runner.setProperty(BATCH_SIZE, "1");
        runner.setProperty(ID_ATTRIBUTE, "doc_id");
        runner.enqueue(TestPutElasticsearchHttp.docExample, new HashMap<String, String>() {
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
        runner.enqueue(TestPutElasticsearchHttp.docExample, new HashMap<String, String>() {
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
        runner = TestRunners.newTestRunner(new TestPutElasticsearchHttp.PutElasticsearchTestProcessor(false));// no failures

        runner.setProperty(ES_URL, "http://127.0.0.1:9200");
        runner.setProperty(INDEX, "doc");
        runner.assertNotValid();
        runner.setProperty(TYPE, "status");
        runner.setProperty(BATCH_SIZE, "1");
        runner.assertValid();
        runner.setProperty(ID_ATTRIBUTE, "doc_id");
        runner.assertValid();
        runner.setProperty(INDEX_OP, "index_fail");
        runner.assertValid();
        runner.enqueue(TestPutElasticsearchHttp.docExample, new HashMap<String, String>() {
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
        TestPutElasticsearchHttp.PutElasticsearchTestProcessor p = new TestPutElasticsearchHttp.PutElasticsearchTestProcessor(false);// no failures

        p.setExpectedUrl("http://127.0.0.1:9200/_bulk?pipeline=my-pipeline");
        runner = TestRunners.newTestRunner(p);
        runner.setProperty(ES_URL, "http://127.0.0.1:9200");
        runner.setProperty(INDEX, "doc");
        runner.setProperty(TYPE, "status");
        runner.setProperty(BATCH_SIZE, "1");
        runner.setProperty(ID_ATTRIBUTE, "doc_id");
        // Set dynamic property, to be added to the URL as a query parameter
        runner.setProperty("pipeline", "my-pipeline");
        runner.enqueue(TestPutElasticsearchHttp.docExample, new HashMap<String, String>() {
            {
                put("doc_id", "28039652140");
            }
        });
        runner.run(1, true, true);
        runner.assertAllFlowFilesTransferred(REL_SUCCESS, 1);
        final MockFlowFile out = runner.getFlowFilesForRelationship(REL_SUCCESS).get(0);
        Assert.assertNotNull(out);
    }

    @Test
    public void testPutElasticSearchOnTriggerWithDocumentNotFound() throws IOException {
        TestPutElasticsearchHttp.PutElasticsearchTestProcessor processor = new TestPutElasticsearchHttp.PutElasticsearchTestProcessor(true);
        processor.setResultField("not_found");
        runner = TestRunners.newTestRunner(processor);// simulate failures

        runner.setProperty(ES_URL, "http://127.0.0.1:9200");
        runner.setProperty(INDEX_OP, "delete");
        runner.setProperty(INDEX, "doc");
        runner.setProperty(TYPE, "status");
        runner.setProperty(BATCH_SIZE, "1");
        runner.setProperty(ID_ATTRIBUTE, "doc_id");
        runner.enqueue(TestPutElasticsearchHttp.docExample, new HashMap<String, String>() {
            {
                put("doc_id", "28039652140");
            }
        });
        runner.run(1, true, true);
        runner.assertAllFlowFilesTransferred(REL_FAILURE, 1);
        runner.clearTransferState();
    }

    /**
     * A Test class that extends the processor in order to inject/mock behavior
     */
    private static class PutElasticsearchTestProcessor extends PutElasticsearchHttp {
        boolean responseHasFailures = false;

        OkHttpClient client;

        int statusCode = 200;

        String statusMessage = "OK";

        String expectedUrl = null;

        String resultField = null;

        PutElasticsearchTestProcessor(boolean responseHasFailures) {
            this.responseHasFailures = responseHasFailures;
        }

        void setStatus(int code, String message) {
            statusCode = code;
            statusMessage = message;
        }

        void setExpectedUrl(String url) {
            expectedUrl = url;
        }

        public void setResultField(String resultField) {
            this.resultField = resultField;
        }

        @Override
        protected void createElasticsearchClient(ProcessContext context) throws ProcessException {
            client = Mockito.mock(OkHttpClient.class);
            Mockito.when(client.newCall(ArgumentMatchers.any(Request.class))).thenAnswer(new Answer<Call>() {
                @Override
                public Call answer(InvocationOnMock invocationOnMock) throws Throwable {
                    final Call call = Mockito.mock(Call.class);
                    if ((statusCode) != (-1)) {
                        Request realRequest = ((Request) (invocationOnMock.getArguments()[0]));
                        Assert.assertTrue((((expectedUrl) == null) || (expectedUrl.equals(realRequest.url().toString()))));
                        StringBuilder sb = new StringBuilder("{\"took\": 1, \"errors\": \"");
                        sb.append(responseHasFailures);
                        sb.append("\", \"items\": [");
                        if (responseHasFailures) {
                            // This case is for a status code of 200 for the bulk response itself, but with an error (of 400) inside
                            sb.append("{\"index\":{\"_index\":\"doc\",\"_type\":\"status\",\"_id\":\"28039652140\",\"status\":\"400\",");
                            if ((resultField) != null) {
                                sb.append("\"result\":{\"not_found\",");
                            } else {
                                sb.append("\"error\":{\"type\":\"mapper_parsing_exception\",\"reason\":\"failed to parse [gender]\",");
                            }
                            sb.append("\"caused_by\":{\"type\":\"json_parse_exception\",\"reason\":\"Unexpected end-of-input in VALUE_STRING\\n at ");
                            sb.append("[Source: org.elasticsearch.common.io.stream.InputStreamStreamInput@1a2e3ac4; line: 1, column: 39]\"}}}},");
                        }
                        sb.append("{\"index\":{\"_index\":\"doc\",\"_type\":\"status\",\"_id\":\"28039652140\",\"status\":");
                        sb.append(statusCode);
                        sb.append(",\"_source\":{\"text\": \"This is a test document\"}}}");
                        sb.append("]}");
                        Response mockResponse = new Response.Builder().request(realRequest).protocol(HTTP_1_1).code(statusCode).message(statusMessage).body(ResponseBody.create(MediaType.parse("application/json"), sb.toString())).build();
                        Mockito.when(call.execute()).thenReturn(mockResponse);
                    } else {
                        Mockito.when(call.execute()).thenThrow(ConnectException.class);
                    }
                    return call;
                }
            });
        }

        @Override
        protected OkHttpClient getClient() {
            return client;
        }
    }

    @Test(expected = AssertionError.class)
    public void testPutElasticSearchBadHostInEL() throws IOException {
        runner = TestRunners.newTestRunner(new TestPutElasticsearchHttp.PutElasticsearchTestProcessor(false));// no failures

        runner.setProperty(ES_URL, "${es.url}");
        runner.setProperty(INDEX, "doc");
        runner.setProperty(TYPE, "status");
        runner.setProperty(BATCH_SIZE, "1");
        runner.setProperty(ID_ATTRIBUTE, "doc_id");
        runner.enqueue(TestPutElasticsearchHttp.docExample, new HashMap<String, String>() {
            {
                put("doc_id", "28039652140");
            }
        });
        runner.run(1, true, true);
    }
}

