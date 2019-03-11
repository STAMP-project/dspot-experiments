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


import AbstractElasticsearchHttpProcessor.ES_URL;
import Protocol.HTTP_1_1;
import QueryElasticsearchHttp.INDEX;
import QueryElasticsearchHttp.PAGE_SIZE;
import QueryElasticsearchHttp.QUERY;
import QueryElasticsearchHttp.QueryInfoRouteStrategy.ALWAYS;
import QueryElasticsearchHttp.QueryInfoRouteStrategy.NEVER;
import QueryElasticsearchHttp.QueryInfoRouteStrategy.NOHIT;
import QueryElasticsearchHttp.ROUTING_QUERY_INFO_STRATEGY;
import QueryElasticsearchHttp.TYPE;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import okhttp3.Call;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import org.apache.nifi.processor.ProcessContext;
import org.apache.nifi.processor.exception.ProcessException;
import org.apache.nifi.util.TestRunner;
import org.apache.nifi.util.TestRunners;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.mockito.stubbing.OngoingStubbing;


public class TestQueryElasticsearchHttpNoHits {
    private TestRunner runner;

    @Test
    public void testQueryElasticsearchOnTrigger_NoHits_NoHits() throws IOException {
        runner = TestRunners.newTestRunner(new TestQueryElasticsearchHttpNoHits.QueryElasticsearchHttpTestProcessor());
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
        runner.setProperty(ROUTING_QUERY_INFO_STRATEGY, NOHIT.name());
        runner.assertValid();
        runner.setIncomingConnection(false);
        runAndVerify(0, 1, 0, true);
    }

    @Test
    public void testQueryElasticsearchOnTrigger_NoHits_Never() throws IOException {
        runner = TestRunners.newTestRunner(new TestQueryElasticsearchHttpNoHits.QueryElasticsearchHttpTestProcessor());
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
        runner.setProperty(ROUTING_QUERY_INFO_STRATEGY, NEVER.name());
        runner.assertValid();
        runner.setIncomingConnection(false);
        runAndVerify(0, 0, 0, true);
    }

    @Test
    public void testQueryElasticsearchOnTrigger_NoHits_Always() throws IOException {
        runner = TestRunners.newTestRunner(new TestQueryElasticsearchHttpNoHits.QueryElasticsearchHttpTestProcessor());
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
        runner.setProperty(ROUTING_QUERY_INFO_STRATEGY, ALWAYS.name());
        runner.assertValid();
        runner.setIncomingConnection(false);
        runAndVerify(0, 1, 0, true);
    }

    @Test
    public void testQueryElasticsearchOnTrigger_Hits_NoHits() throws IOException {
        runner = TestRunners.newTestRunner(new TestQueryElasticsearchHttpNoHits.QueryElasticsearchHttpTestProcessor(true));
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
        runner.setProperty(ROUTING_QUERY_INFO_STRATEGY, NOHIT.name());
        runner.assertValid();
        runner.setIncomingConnection(false);
        runAndVerify(3, 0, 0, true);
    }

    @Test
    public void testQueryElasticsearchOnTrigger_Hits_Never() throws IOException {
        runner = TestRunners.newTestRunner(new TestQueryElasticsearchHttpNoHits.QueryElasticsearchHttpTestProcessor(true));
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
        runner.setProperty(ROUTING_QUERY_INFO_STRATEGY, NEVER.name());
        runner.assertValid();
        runner.setIncomingConnection(false);
        runAndVerify(3, 0, 0, true);
    }

    @Test
    public void testQueryElasticsearchOnTrigger_Hits_Always() throws IOException {
        runner = TestRunners.newTestRunner(new TestQueryElasticsearchHttpNoHits.QueryElasticsearchHttpTestProcessor(true));
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
        runner.setProperty(ROUTING_QUERY_INFO_STRATEGY, ALWAYS.name());
        runner.assertValid();
        runner.setIncomingConnection(false);
        runAndVerify(3, 3, 2, true);
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

        boolean useHitPages;

        // query-page3 has no hits
        List<String> noHitPages = Arrays.asList(TestQueryElasticsearchHttpNoHits.getDoc("query-page3.json"));

        List<String> hitPages = Arrays.asList(TestQueryElasticsearchHttpNoHits.getDoc("query-page1.json"), TestQueryElasticsearchHttpNoHits.getDoc("query-page2.json"), TestQueryElasticsearchHttpNoHits.getDoc("query-page3.json"));

        String expectedParam = null;

        public QueryElasticsearchHttpTestProcessor() {
            this(false);
        }

        public QueryElasticsearchHttpTestProcessor(boolean useHitPages) {
            this.useHitPages = useHitPages;
        }

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
            List<String> pages;
            if (useHitPages) {
                pages = hitPages;
            } else {
                pages = noHitPages;
            }
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

        protected OkHttpClient getClient() {
            return client;
        }
    }
}

