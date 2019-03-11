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
import QueryElasticsearchHttp.FIELDS;
import QueryElasticsearchHttp.INDEX;
import QueryElasticsearchHttp.LIMIT;
import QueryElasticsearchHttp.PAGE_SIZE;
import QueryElasticsearchHttp.PROP_SSL_CONTEXT_SERVICE;
import QueryElasticsearchHttp.QUERY;
import QueryElasticsearchHttp.REL_FAILURE;
import QueryElasticsearchHttp.REL_RETRY;
import QueryElasticsearchHttp.REL_SUCCESS;
import QueryElasticsearchHttp.SORT;
import QueryElasticsearchHttp.TARGET;
import QueryElasticsearchHttp.TARGET_FLOW_FILE_ATTRIBUTES;
import QueryElasticsearchHttp.TYPE;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import okhttp3.Call;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import org.apache.nifi.processor.ProcessContext;
import org.apache.nifi.processor.exception.ProcessException;
import org.apache.nifi.ssl.SSLContextService;
import org.apache.nifi.util.MockFlowFile;
import org.apache.nifi.util.TestRunner;
import org.apache.nifi.util.TestRunners;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.mockito.stubbing.OngoingStubbing;


public class TestQueryElasticsearchHttp {
    private TestRunner runner;

    @Test
    public void testQueryElasticsearchOnTrigger_withInput() throws IOException {
        runner = TestRunners.newTestRunner(new TestQueryElasticsearchHttp.QueryElasticsearchHttpTestProcessor());
        runner.setProperty(ES_URL, "http://127.0.0.1:9200");
        runner.setProperty(INDEX, "doc");
        runner.assertNotValid();
        runner.setProperty(TYPE, "status");
        runner.assertNotValid();
        runner.setProperty(QUERY, "source:Twitter AND identifier:\"${identifier}\"");
        runner.assertValid();
        runner.setProperty(PAGE_SIZE, "2");
        runner.assertValid();
        runAndVerifySuccess(true);
    }

    @Test
    public void testQueryElasticsearchOnTrigger_withInput_withQueryInAttrs() throws IOException {
        runner = TestRunners.newTestRunner(new TestQueryElasticsearchHttp.QueryElasticsearchHttpTestProcessor());
        runner.setValidateExpressionUsage(true);
        runner.setProperty(ES_URL, "http://127.0.0.1:9200");
        runner.setProperty(INDEX, "doc");
        runner.assertNotValid();
        runner.setProperty(TYPE, "status");
        runner.assertNotValid();
        runner.setProperty(QUERY, "source:Twitter AND identifier:\"${identifier}\"");
        runner.assertValid();
        runner.setProperty(PAGE_SIZE, "2");
        runner.assertValid();
        runAndVerifySuccess(true);
    }

    @Test
    public void testQueryElasticsearchOnTrigger_withInput_EL() throws IOException {
        runner = TestRunners.newTestRunner(new TestQueryElasticsearchHttp.QueryElasticsearchHttpTestProcessor());
        runner.setProperty(ES_URL, "${es.url}");
        runner.setProperty(INDEX, "doc");
        runner.assertNotValid();
        runner.setProperty(TYPE, "status");
        runner.assertNotValid();
        runner.setProperty(QUERY, "source:Twitter AND identifier:\"${identifier}\"");
        runner.assertValid();
        runner.setProperty(PAGE_SIZE, "2");
        runner.assertValid();
        runner.setProperty(CONNECT_TIMEOUT, "${connect.timeout}");
        runner.assertValid();
        runner.setVariable("es.url", "http://127.0.0.1:9200");
        runAndVerifySuccess(true);
    }

    @Test
    public void testQueryElasticsearchOnTrigger_withInput_attributeTarget() throws IOException {
        runner = TestRunners.newTestRunner(new TestQueryElasticsearchHttp.QueryElasticsearchHttpTestProcessor());
        runner.setProperty(ES_URL, "http://127.0.0.1:9200");
        runner.setProperty(INDEX, "doc");
        runner.assertNotValid();
        runner.setProperty(TYPE, "status");
        runner.assertNotValid();
        runner.setProperty(QUERY, "source:Twitter AND identifier:\"${identifier}\"");
        runner.assertValid();
        runner.setProperty(PAGE_SIZE, "2");
        runner.assertValid();
        runner.setProperty(TARGET, TARGET_FLOW_FILE_ATTRIBUTES);
        runAndVerifySuccess(false);
        final MockFlowFile out = runner.getFlowFilesForRelationship(REL_SUCCESS).get(0);
        Assert.assertNotNull(out);
        Assert.assertEquals("blah", new String(out.toByteArray()));
        Assert.assertEquals("arrays,are,supported,too", out.getAttribute("es.result.tags"));
        Assert.assertEquals("Twitter", out.getAttribute("es.result.source"));
    }

    @Test
    public void testQueryElasticsearchOnTrigger_withNoInput() throws IOException {
        runner = TestRunners.newTestRunner(new TestQueryElasticsearchHttp.QueryElasticsearchHttpTestProcessor());
        runner.setProperty(ES_URL, "http://127.0.0.1:9200");
        runner.setProperty(INDEX, "doc");
        runner.assertNotValid();
        runner.setProperty(TYPE, "status");
        runner.assertNotValid();
        runner.setProperty(QUERY, "source:Twitter AND identifier:\"${identifier}\"");
        runner.assertValid();
        runner.setProperty(PAGE_SIZE, "2");
        runner.assertValid();
        runner.setIncomingConnection(false);
        runAndVerifySuccess(true);
    }

    @Test
    public void testQueryElasticsearchOnTriggerWithFields() throws IOException {
        runner = TestRunners.newTestRunner(new TestQueryElasticsearchHttp.QueryElasticsearchHttpTestProcessor());
        runner.setProperty(ES_URL, "http://127.0.0.1:9200");
        runner.setProperty(INDEX, "doc");
        runner.assertNotValid();
        runner.setProperty(TYPE, "status");
        runner.assertNotValid();
        runner.setProperty(QUERY, "${doc_id}");
        runner.assertValid();
        runner.setProperty(FIELDS, "id,, userinfo.location");
        runner.assertValid();
        runner.setProperty(SORT, "timestamp:asc,identifier:desc");
        runner.assertValid();
        runAndVerifySuccess(true);
    }

    @Test
    public void testQueryElasticsearchOnTriggerWithLimit() throws IOException {
        runner = TestRunners.newTestRunner(new TestQueryElasticsearchHttp.QueryElasticsearchHttpTestProcessor());
        runner.setProperty(ES_URL, "http://127.0.0.1:9200");
        runner.setProperty(INDEX, "doc");
        runner.assertNotValid();
        runner.setProperty(TYPE, "status");
        runner.assertNotValid();
        runner.setProperty(QUERY, "${doc_id}");
        runner.assertValid();
        runner.setProperty(FIELDS, "id,, userinfo.location");
        runner.assertValid();
        runner.setProperty(SORT, "timestamp:asc,identifier:desc");
        runner.assertValid();
        runner.setProperty(LIMIT, "2");
        runAndVerifySuccess(2, true);
    }

    @Test
    public void testQueryElasticsearchOnTriggerWithServerErrorRetry() throws IOException {
        TestQueryElasticsearchHttp.QueryElasticsearchHttpTestProcessor processor = new TestQueryElasticsearchHttp.QueryElasticsearchHttpTestProcessor();
        processor.setStatus(500, "Server error");
        runner = TestRunners.newTestRunner(processor);// simulate doc not found

        runner.setProperty(ES_URL, "http://127.0.0.1:9200");
        runner.setProperty(INDEX, "doc");
        runner.setProperty(TYPE, "status");
        runner.setProperty(QUERY, "${doc_id}");
        runner.enqueue("".getBytes(), new HashMap<String, String>() {
            {
                put("identifier", "28039652140");
            }
        });
        runner.run(1, true, true);
        // This test generates a HTTP 500 "Server error"
        runner.assertAllFlowFilesTransferred(REL_RETRY, 1);
        final MockFlowFile out = runner.getFlowFilesForRelationship(REL_RETRY).get(0);
        Assert.assertNotNull(out);
    }

    @Test
    public void testQueryElasticsearchOnTriggerWithServerFail() throws IOException {
        TestQueryElasticsearchHttp.QueryElasticsearchHttpTestProcessor processor = new TestQueryElasticsearchHttp.QueryElasticsearchHttpTestProcessor();
        processor.setStatus(100, "Should fail");
        runner = TestRunners.newTestRunner(processor);// simulate doc not found

        runner.setProperty(ES_URL, "http://127.0.0.1:9200");
        runner.setProperty(INDEX, "doc");
        runner.setProperty(TYPE, "status");
        runner.setProperty(QUERY, "${doc_id}");
        runner.enqueue("".getBytes(), new HashMap<String, String>() {
            {
                put("identifier", "28039652140");
            }
        });
        runner.run(1, true, true);
        // This test generates a HTTP 100 "Should fail"
        runner.assertAllFlowFilesTransferred(REL_FAILURE, 1);
        final MockFlowFile out = runner.getFlowFilesForRelationship(REL_FAILURE).get(0);
        Assert.assertNotNull(out);
    }

    @Test
    public void testQueryElasticsearchOnTriggerWithIOException() throws IOException {
        TestQueryElasticsearchHttp.QueryElasticsearchHttpTestProcessor processor = new TestQueryElasticsearchHttp.QueryElasticsearchHttpTestProcessor();
        processor.setExceptionToThrow(new IOException("Error reading from disk"));
        runner = TestRunners.newTestRunner(processor);// simulate doc not found

        runner.setProperty(ES_URL, "http://127.0.0.1:9200");
        runner.setProperty(INDEX, "doc");
        runner.setProperty(TYPE, "status");
        runner.setProperty(QUERY, "${doc_id}");
        runner.enqueue("".getBytes(), new HashMap<String, String>() {
            {
                put("identifier", "28039652140");
            }
        });
        runner.run(1, true, true);
        // This test generates a HTTP 100 "Should fail"
        runner.assertAllFlowFilesTransferred(REL_RETRY, 1);
        final MockFlowFile out = runner.getFlowFilesForRelationship(REL_RETRY).get(0);
        Assert.assertNotNull(out);
    }

    @Test
    public void testQueryElasticsearchOnTriggerWithServerFailAfterSuccess() throws IOException {
        TestQueryElasticsearchHttp.QueryElasticsearchHttpTestProcessor processor = new TestQueryElasticsearchHttp.QueryElasticsearchHttpTestProcessor();
        processor.setStatus(100, "Should fail", 2);
        runner = TestRunners.newTestRunner(processor);// simulate doc not found

        runner.setProperty(ES_URL, "http://127.0.0.1:9200");
        runner.setProperty(INDEX, "doc");
        runner.setProperty(TYPE, "status");
        runner.setProperty(QUERY, "${doc_id}");
        runner.enqueue("".getBytes(), new HashMap<String, String>() {
            {
                put("identifier", "28039652140");
            }
        });
        runner.run(1, true, true);
        // This test generates a HTTP 100 "Should fail"
        runner.assertTransferCount(REL_SUCCESS, 2);
        runner.assertTransferCount(REL_FAILURE, 1);
        final MockFlowFile out = runner.getFlowFilesForRelationship(REL_FAILURE).get(0);
        Assert.assertNotNull(out);
    }

    @Test
    public void testQueryElasticsearchOnTriggerWithServerFailNoIncomingFlowFile() throws IOException {
        TestQueryElasticsearchHttp.QueryElasticsearchHttpTestProcessor processor = new TestQueryElasticsearchHttp.QueryElasticsearchHttpTestProcessor();
        processor.setStatus(100, "Should fail", 1);
        runner = TestRunners.newTestRunner(processor);// simulate doc not found

        runner.setProperty(ES_URL, "http://127.0.0.1:9200");
        runner.setProperty(INDEX, "doc");
        runner.setProperty(TYPE, "status");
        runner.setProperty(QUERY, "${doc_id}");
        runner.setIncomingConnection(false);
        runner.run(1, true, true);
        // This test generates a HTTP 100 with no incoming flow file, so nothing should be transferred
        getRelationships().forEach(( relationship) -> runner.assertTransferCount(relationship, 0));
        runner.assertTransferCount(REL_FAILURE, 0);
    }

    @Test
    public void testSetupSecureClient() throws Exception {
        TestQueryElasticsearchHttp.QueryElasticsearchHttpTestProcessor processor = new TestQueryElasticsearchHttp.QueryElasticsearchHttpTestProcessor();
        runner = TestRunners.newTestRunner(processor);
        SSLContextService sslService = Mockito.mock(SSLContextService.class);
        Mockito.when(sslService.getIdentifier()).thenReturn("ssl-context");
        runner.addControllerService("ssl-context", sslService);
        runner.enableControllerService(sslService);
        runner.setProperty(PROP_SSL_CONTEXT_SERVICE, "ssl-context");
        runner.setProperty(ES_URL, "http://127.0.0.1:9200");
        runner.setProperty(INDEX, "doc");
        runner.setProperty(QUERY, "${doc_id}");
        // Allow time for the controller service to fully initialize
        Thread.sleep(500);
        runner.enqueue("".getBytes(), new HashMap<String, String>() {
            {
                put("doc_id", "28039652140");
            }
        });
        runner.run(1, true, true);
    }

    @Test
    public void testQueryElasticsearchOnTrigger_withQueryParameters() throws IOException {
        TestQueryElasticsearchHttp.QueryElasticsearchHttpTestProcessor p = new TestQueryElasticsearchHttp.QueryElasticsearchHttpTestProcessor();
        p.setExpectedParam("myparam=myvalue");
        runner = TestRunners.newTestRunner(p);
        runner.setProperty(ES_URL, "http://127.0.0.1:9200");
        runner.setProperty(INDEX, "doc");
        runner.setProperty(TYPE, "status");
        runner.setProperty(QUERY, "source:Twitter");
        // Set dynamic property, to be added to the URL as a query parameter
        runner.setProperty("myparam", "myvalue");
        runAndVerifySuccess(true);
    }

    /**
     * A Test class that extends the processor in order to inject/mock behavior
     */
    private static class QueryElasticsearchHttpTestProcessor extends QueryElasticsearchHttp {
        Exception exceptionToThrow = null;

        OkHttpClient client;

        int goodStatusCode = 200;

        String goodStatusMessage = "OK";

        int badStatusCode;

        String badStatusMessage;

        int runNumber;

        List<String> pages = Arrays.asList(TestQueryElasticsearchHttp.getDoc("query-page1.json"), TestQueryElasticsearchHttp.getDoc("query-page2.json"), TestQueryElasticsearchHttp.getDoc("query-page3.json"));

        String expectedParam = null;

        public void setExceptionToThrow(Exception exceptionToThrow) {
            this.exceptionToThrow = exceptionToThrow;
        }

        /**
         * Sets the status code and message for the 1st query
         *
         * @param code
         * 		The status code to return
         * @param message
         * 		The status message
         */
        void setStatus(int code, String message) {
            this.setStatus(code, message, 1);
        }

        /**
         * Sets an query parameter (name=value) expected to be at the end of the URL for the query operation
         *
         * @param param
         * 		The parameter to expect
         */
        void setExpectedParam(String param) {
            expectedParam = param;
        }

        /**
         * Sets the status code and message for the runNumber-th query
         *
         * @param code
         * 		The status code to return
         * @param message
         * 		The status message
         * @param runNumber
         * 		The run number for which to set this status
         */
        void setStatus(int code, String message, int runNumber) {
            badStatusCode = code;
            badStatusMessage = message;
            this.runNumber = runNumber;
        }

        @Override
        protected void createElasticsearchClient(ProcessContext context) throws ProcessException {
            client = Mockito.mock(OkHttpClient.class);
            OngoingStubbing<Call> stub = Mockito.when(client.newCall(ArgumentMatchers.any(Request.class)));
            for (int i = 0; i < (pages.size()); i++) {
                String page = pages.get(i);
                if ((runNumber) == (i + 1)) {
                    stub = mockReturnDocument(stub, page, badStatusCode, badStatusMessage);
                } else {
                    stub = mockReturnDocument(stub, page, goodStatusCode, goodStatusMessage);
                }
            }
        }

        private OngoingStubbing<Call> mockReturnDocument(OngoingStubbing<Call> stub, final String document, int statusCode, String statusMessage) {
            return stub.thenAnswer(new Answer<Call>() {
                @Override
                public Call answer(InvocationOnMock invocationOnMock) throws Throwable {
                    Request realRequest = ((Request) (invocationOnMock.getArguments()[0]));
                    Assert.assertTrue((((expectedParam) == null) || (realRequest.url().toString().endsWith(expectedParam))));
                    Response mockResponse = new Response.Builder().request(realRequest).protocol(HTTP_1_1).code(statusCode).message(statusMessage).body(ResponseBody.create(MediaType.parse("application/json"), document)).build();
                    final Call call = Mockito.mock(Call.class);
                    if ((exceptionToThrow) != null) {
                        Mockito.when(call.execute()).thenThrow(exceptionToThrow);
                    } else {
                        Mockito.when(call.execute()).thenReturn(mockResponse);
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
}

