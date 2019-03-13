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


import AbstractElasticsearchTransportClientProcessor.CLUSTER_NAME;
import AbstractElasticsearchTransportClientProcessor.HOSTS;
import AbstractElasticsearchTransportClientProcessor.PING_TIMEOUT;
import AbstractElasticsearchTransportClientProcessor.SAMPLER_INTERVAL;
import FetchElasticsearch.DOC_ID;
import FetchElasticsearch.INDEX;
import FetchElasticsearch.PROP_SSL_CONTEXT_SERVICE;
import FetchElasticsearch.REL_FAILURE;
import FetchElasticsearch.REL_NOT_FOUND;
import FetchElasticsearch.REL_RETRY;
import FetchElasticsearch.REL_SUCCESS;
import FetchElasticsearch.TYPE;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.concurrent.ExecutionException;
import org.apache.nifi.processor.ProcessContext;
import org.apache.nifi.processor.exception.ProcessException;
import org.apache.nifi.ssl.SSLContextService;
import org.apache.nifi.util.MockFlowFile;
import org.apache.nifi.util.MockProcessContext;
import org.apache.nifi.util.TestRunner;
import org.apache.nifi.util.TestRunners;
import org.elasticsearch.ElasticsearchParseException;
import org.elasticsearch.ElasticsearchTimeoutException;
import org.elasticsearch.action.ActionListener;
import org.elasticsearch.action.ListenableActionFuture;
import org.elasticsearch.action.get.GetAction;
import org.elasticsearch.action.get.GetRequestBuilder;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.support.AdapterActionFuture;
import org.elasticsearch.client.transport.NoNodeAvailableException;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.io.stream.StreamInput;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.node.NodeClosedException;
import org.elasticsearch.transport.ReceiveTimeoutTransportException;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


public class TestFetchElasticsearch {
    private InputStream docExample;

    private TestRunner runner;

    @Test
    public void testFetchElasticsearchOnTrigger() throws IOException {
        runner = TestRunners.newTestRunner(new TestFetchElasticsearch.FetchElasticsearchTestProcessor(true));// all docs are found

        runner.setProperty(CLUSTER_NAME, "elasticsearch");
        runner.setProperty(HOSTS, "127.0.0.1:9300");
        runner.setProperty(PING_TIMEOUT, "5s");
        runner.setProperty(SAMPLER_INTERVAL, "5s");
        runner.setProperty(INDEX, "doc");
        runner.assertNotValid();
        runner.setProperty(TYPE, "status");
        runner.assertNotValid();
        runner.setProperty(DOC_ID, "${doc_id}");
        runner.assertValid();
        runner.enqueue(docExample, new HashMap<String, String>() {
            {
                put("doc_id", "28039652140");
            }
        });
        runner.run(1, true, true);
        runner.assertAllFlowFilesTransferred(REL_SUCCESS, 1);
        Assert.assertFalse(runner.getProvenanceEvents().isEmpty());
        runner.getProvenanceEvents().forEach(( event) -> assertEquals(event.getEventType(), ProvenanceEventType.FETCH));
        final MockFlowFile out = runner.getFlowFilesForRelationship(REL_SUCCESS).get(0);
        Assert.assertNotNull(out);
        out.assertAttributeEquals("doc_id", "28039652140");
    }

    @Test
    public void testFetchElasticsearchOnTriggerEL() throws IOException {
        runner = TestRunners.newTestRunner(new TestFetchElasticsearch.FetchElasticsearchTestProcessor(true));// all docs are found

        runner.setProperty(CLUSTER_NAME, "${cluster.name}");
        runner.setProperty(HOSTS, "${hosts}");
        runner.setProperty(PING_TIMEOUT, "${ping.timeout}");
        runner.setProperty(SAMPLER_INTERVAL, "${sampler.interval}");
        runner.setProperty(INDEX, "doc");
        runner.assertNotValid();
        runner.setProperty(TYPE, "status");
        runner.assertNotValid();
        runner.setProperty(DOC_ID, "${doc_id}");
        runner.assertValid();
        runner.setVariable("cluster.name", "elasticsearch");
        runner.setVariable("hosts", "127.0.0.1:9300");
        runner.setVariable("ping.timeout", "5s");
        runner.setVariable("sampler.interval", "5s");
        runner.enqueue(docExample, new HashMap<String, String>() {
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
    public void testFetchElasticsearchOnTriggerWithFailures() throws IOException {
        runner = TestRunners.newTestRunner(new TestFetchElasticsearch.FetchElasticsearchTestProcessor(false));// simulate doc not found

        runner.setProperty(CLUSTER_NAME, "elasticsearch");
        runner.setProperty(HOSTS, "127.0.0.1:9300");
        runner.setProperty(PING_TIMEOUT, "5s");
        runner.setProperty(SAMPLER_INTERVAL, "5s");
        runner.setProperty(INDEX, "doc");
        runner.setProperty(TYPE, "status");
        runner.setProperty(DOC_ID, "${doc_id}");
        runner.enqueue(docExample, new HashMap<String, String>() {
            {
                put("doc_id", "28039652140");
            }
        });
        runner.run(1, true, true);
        // This test generates a "document not found"
        runner.assertAllFlowFilesTransferred(REL_NOT_FOUND, 1);
        final MockFlowFile out = runner.getFlowFilesForRelationship(REL_NOT_FOUND).get(0);
        Assert.assertNotNull(out);
        out.assertAttributeEquals("doc_id", "28039652140");
    }

    @Test
    public void testFetchElasticsearchWithBadHosts() throws IOException {
        runner = TestRunners.newTestRunner(new TestFetchElasticsearch.FetchElasticsearchTestProcessor(false));// simulate doc not found

        runner.setProperty(CLUSTER_NAME, "elasticsearch");
        runner.setProperty(HOSTS, "http://127.0.0.1:9300,127.0.0.2:9300");
        runner.setProperty(PING_TIMEOUT, "5s");
        runner.setProperty(SAMPLER_INTERVAL, "5s");
        runner.setProperty(INDEX, "doc");
        runner.setProperty(TYPE, "status");
        runner.setProperty(DOC_ID, "${doc_id}");
        runner.assertNotValid();
    }

    @Test
    public void testFetchElasticsearchOnTriggerWithExceptions() throws IOException {
        TestFetchElasticsearch.FetchElasticsearchTestProcessor processor = new TestFetchElasticsearch.FetchElasticsearchTestProcessor(true);
        runner = TestRunners.newTestRunner(processor);
        runner.setProperty(CLUSTER_NAME, "elasticsearch");
        runner.setProperty(HOSTS, "127.0.0.1:9300");
        runner.setProperty(PING_TIMEOUT, "5s");
        runner.setProperty(SAMPLER_INTERVAL, "5s");
        runner.setProperty(INDEX, "doc");
        runner.setProperty(TYPE, "status");
        runner.setProperty(DOC_ID, "${doc_id}");
        // No Node Available exception
        processor.setExceptionToThrow(new NoNodeAvailableException("test"));
        runner.enqueue(docExample, new HashMap<String, String>() {
            {
                put("doc_id", "28039652140");
            }
        });
        runner.run(1, true, true);
        runner.assertAllFlowFilesTransferred(REL_RETRY, 1);
        runner.clearTransferState();
        // Elasticsearch Timeout exception
        processor.setExceptionToThrow(new ElasticsearchTimeoutException("test"));
        runner.enqueue(docExample, new HashMap<String, String>() {
            {
                put("doc_id", "28039652141");
            }
        });
        runner.run(1, true, true);
        runner.assertAllFlowFilesTransferred(REL_RETRY, 1);
        runner.clearTransferState();
        // Receive Timeout Transport exception
        processor.setExceptionToThrow(new ReceiveTimeoutTransportException(Mockito.mock(StreamInput.class)));
        runner.enqueue(docExample, new HashMap<String, String>() {
            {
                put("doc_id", "28039652141");
            }
        });
        runner.run(1, true, true);
        runner.assertAllFlowFilesTransferred(REL_RETRY, 1);
        runner.clearTransferState();
        // Node Closed exception
        processor.setExceptionToThrow(new NodeClosedException(Mockito.mock(StreamInput.class)));
        runner.enqueue(docExample, new HashMap<String, String>() {
            {
                put("doc_id", "28039652141");
            }
        });
        runner.run(1, true, true);
        runner.assertAllFlowFilesTransferred(REL_RETRY, 1);
        runner.clearTransferState();
        // Elasticsearch Parse exception
        processor.setExceptionToThrow(new ElasticsearchParseException("test"));
        runner.enqueue(docExample, new HashMap<String, String>() {
            {
                put("doc_id", "28039652141");
            }
        });
        runner.run(1, true, true);
        // This test generates an exception on execute(),routes to failure
        runner.assertTransferCount(REL_FAILURE, 1);
    }

    @Test(expected = ProcessException.class)
    public void testCreateElasticsearchClientWithException() throws ProcessException {
        TestFetchElasticsearch.FetchElasticsearchTestProcessor processor = new TestFetchElasticsearch.FetchElasticsearchTestProcessor(true) {
            @Override
            protected TransportClient getTransportClient(Settings.Builder settingsBuilder, String shieldUrl, String username, String password) throws MalformedURLException {
                throw new MalformedURLException();
            }
        };
        MockProcessContext context = new MockProcessContext(processor);
        initialize(new org.apache.nifi.util.MockProcessorInitializationContext(processor, context));
        processor.callCreateElasticsearchClient(context);
    }

    @Test
    public void testSetupSecureClient() throws Exception {
        TestFetchElasticsearch.FetchElasticsearchTestProcessor processor = new TestFetchElasticsearch.FetchElasticsearchTestProcessor(true);
        runner = TestRunners.newTestRunner(processor);
        SSLContextService sslService = Mockito.mock(SSLContextService.class);
        Mockito.when(sslService.getIdentifier()).thenReturn("ssl-context");
        runner.addControllerService("ssl-context", sslService);
        runner.enableControllerService(sslService);
        runner.setProperty(PROP_SSL_CONTEXT_SERVICE, "ssl-context");
        runner.setProperty(CLUSTER_NAME, "elasticsearch");
        runner.setProperty(HOSTS, "127.0.0.1:9300");
        runner.setProperty(PING_TIMEOUT, "5s");
        runner.setProperty(SAMPLER_INTERVAL, "5s");
        runner.setProperty(INDEX, "doc");
        runner.setProperty(TYPE, "status");
        runner.setProperty(DOC_ID, "${doc_id}");
        // Allow time for the controller service to fully initialize
        Thread.sleep(500);
        runner.enqueue(docExample, new HashMap<String, String>() {
            {
                put("doc_id", "28039652140");
            }
        });
        runner.run(1, true, true);
    }

    /**
     * A Test class that extends the processor in order to inject/mock behavior
     */
    private static class FetchElasticsearchTestProcessor extends FetchElasticsearch {
        boolean documentExists = true;

        Exception exceptionToThrow = null;

        public FetchElasticsearchTestProcessor(boolean documentExists) {
            this.documentExists = documentExists;
        }

        public void setExceptionToThrow(Exception exceptionToThrow) {
            this.exceptionToThrow = exceptionToThrow;
        }

        @Override
        protected TransportClient getTransportClient(Settings.Builder settingsBuilder, String shieldUrl, String username, String password) throws MalformedURLException {
            TransportClient mockClient = Mockito.mock(TransportClient.class);
            GetRequestBuilder getRequestBuilder = Mockito.spy(new GetRequestBuilder(mockClient, GetAction.INSTANCE));
            if ((exceptionToThrow) != null) {
                Mockito.doThrow(exceptionToThrow).when(getRequestBuilder).execute();
            } else {
                Mockito.doReturn(new TestFetchElasticsearch.FetchElasticsearchTestProcessor.MockGetRequestBuilderExecutor(documentExists)).when(getRequestBuilder).execute();
            }
            Mockito.when(mockClient.prepareGet(ArgumentMatchers.anyString(), ArgumentMatchers.anyString(), ArgumentMatchers.anyString())).thenReturn(getRequestBuilder);
            return mockClient;
        }

        public void callCreateElasticsearchClient(ProcessContext context) {
            createElasticsearchClient(context);
        }

        private static class MockGetRequestBuilderExecutor extends AdapterActionFuture<GetResponse, ActionListener<GetResponse>> implements ListenableActionFuture<GetResponse> {
            boolean documentExists = true;

            public MockGetRequestBuilderExecutor(boolean documentExists) {
                this.documentExists = documentExists;
            }

            @Override
            protected GetResponse convert(ActionListener<GetResponse> bulkResponseActionListener) {
                return null;
            }

            @Override
            public void addListener(ActionListener<GetResponse> actionListener) {
            }

            @Override
            public GetResponse get() throws InterruptedException, ExecutionException {
                GetResponse response = Mockito.mock(GetResponse.class);
                Mockito.when(response.isExists()).thenReturn(documentExists);
                Mockito.when(response.getSourceAsBytes()).thenReturn("Success".getBytes());
                Mockito.when(response.getSourceAsString()).thenReturn("Success");
                return response;
            }

            @Override
            public GetResponse actionGet() {
                try {
                    return get();
                } catch (Exception e) {
                    Assert.fail(e.getMessage());
                }
                return null;
            }
        }
    }
}

