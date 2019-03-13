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
import BulkItemResponse.Failure;
import FetchElasticsearch.REL_RETRY;
import PutElasticsearch.BATCH_SIZE;
import PutElasticsearch.ID_ATTRIBUTE;
import PutElasticsearch.INDEX;
import PutElasticsearch.INDEX_OP;
import PutElasticsearch.REL_FAILURE;
import PutElasticsearch.REL_SUCCESS;
import PutElasticsearch.TYPE;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.concurrent.ExecutionException;
import org.apache.nifi.processor.ProcessContext;
import org.apache.nifi.processor.exception.ProcessException;
import org.apache.nifi.util.MockFlowFile;
import org.apache.nifi.util.TestRunner;
import org.apache.nifi.util.TestRunners;
import org.elasticsearch.ElasticsearchParseException;
import org.elasticsearch.ElasticsearchTimeoutException;
import org.elasticsearch.action.ActionListener;
import org.elasticsearch.action.ListenableActionFuture;
import org.elasticsearch.action.bulk.BulkAction;
import org.elasticsearch.action.bulk.BulkItemResponse;
import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexAction;
import org.elasticsearch.action.index.IndexRequestBuilder;
import org.elasticsearch.action.support.AdapterActionFuture;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.NoNodeAvailableException;
import org.elasticsearch.common.io.stream.StreamInput;
import org.elasticsearch.node.NodeClosedException;
import org.elasticsearch.transport.ReceiveTimeoutTransportException;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;


public class TestPutElasticsearch {
    private InputStream docExample;

    private TestRunner runner;

    @Test
    public void testPutElasticSearchOnTrigger() throws IOException {
        runner = TestRunners.newTestRunner(new TestPutElasticsearch.PutElasticsearchTestProcessor(false));// no failures

        runner.setProperty(CLUSTER_NAME, "elasticsearch");
        runner.setProperty(HOSTS, "127.0.0.1:9300");
        runner.setProperty(PING_TIMEOUT, "5s");
        runner.setProperty(SAMPLER_INTERVAL, "5s");
        runner.setProperty(INDEX, "doc");
        runner.assertNotValid();
        runner.setProperty(TYPE, "status");
        runner.setProperty(BATCH_SIZE, "1");
        runner.assertNotValid();
        runner.setProperty(ID_ATTRIBUTE, "doc_id");
        runner.assertValid();
        runner.enqueue(docExample, new HashMap<String, String>() {
            {
                put("doc_id", "28039652140");
            }
        });
        runner.run(1, true, true);
        runner.assertAllFlowFilesTransferred(REL_SUCCESS, 1);
        Assert.assertFalse(runner.getProvenanceEvents().isEmpty());
        runner.getProvenanceEvents().forEach(( event) -> assertEquals(event.getEventType(), ProvenanceEventType.SEND));
        final MockFlowFile out = runner.getFlowFilesForRelationship(REL_SUCCESS).get(0);
        Assert.assertNotNull(out);
        out.assertAttributeEquals("doc_id", "28039652140");
    }

    @Test
    public void testPutElasticSearchOnTriggerEL() throws IOException {
        runner = TestRunners.newTestRunner(new TestPutElasticsearch.PutElasticsearchTestProcessor(false));// no failures

        runner.setProperty(CLUSTER_NAME, "${cluster.name}");
        runner.setProperty(HOSTS, "${hosts}");
        runner.setProperty(PING_TIMEOUT, "${ping.timeout}");
        runner.setProperty(SAMPLER_INTERVAL, "${sampler.interval}");
        runner.setProperty(INDEX, "doc");
        runner.assertNotValid();
        runner.setProperty(TYPE, "status");
        runner.setProperty(BATCH_SIZE, "1");
        runner.assertNotValid();
        runner.setProperty(ID_ATTRIBUTE, "doc_id");
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
    public void testPutElasticSearchOnTriggerWithFailures() throws IOException {
        runner = TestRunners.newTestRunner(new TestPutElasticsearch.PutElasticsearchTestProcessor(true));// simulate failures

        runner.setProperty(CLUSTER_NAME, "elasticsearch");
        runner.setProperty(HOSTS, "127.0.0.1:9300");
        runner.setProperty(PING_TIMEOUT, "5s");
        runner.setProperty(SAMPLER_INTERVAL, "5s");
        runner.setProperty(INDEX, "doc");
        runner.setProperty(TYPE, "status");
        runner.setProperty(BATCH_SIZE, "2");
        runner.setProperty(ID_ATTRIBUTE, "doc_id");
        runner.enqueue(docExample, new HashMap<String, String>() {
            {
                put("doc_id", "28039652140");
            }
        });
        runner.enqueue(docExample, new HashMap<String, String>() {
            {
                put("doc_id", "28039652141");
            }
        });
        runner.run(1, true, true);
        runner.assertTransferCount(REL_FAILURE, 1);
        runner.assertTransferCount(REL_SUCCESS, 1);
        final MockFlowFile out = runner.getFlowFilesForRelationship(REL_FAILURE).get(0);
        Assert.assertNotNull(out);
        out.assertAttributeEquals("doc_id", "28039652140");
    }

    @Test
    public void testPutElasticsearchOnTriggerWithExceptions() throws IOException {
        TestPutElasticsearch.PutElasticsearchTestProcessor processor = new TestPutElasticsearch.PutElasticsearchTestProcessor(false);
        runner = TestRunners.newTestRunner(processor);
        runner.setProperty(CLUSTER_NAME, "elasticsearch");
        runner.setProperty(HOSTS, "127.0.0.1:9300");
        runner.setProperty(PING_TIMEOUT, "5s");
        runner.setProperty(SAMPLER_INTERVAL, "5s");
        runner.setProperty(INDEX, "doc");
        runner.setProperty(TYPE, "status");
        runner.setProperty(ID_ATTRIBUTE, "doc_id");
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
                put("doc_id", "28039652142");
            }
        });
        runner.run(1, true, true);
        runner.assertAllFlowFilesTransferred(REL_RETRY, 1);
        runner.clearTransferState();
        // Node Closed exception
        processor.setExceptionToThrow(new NodeClosedException(Mockito.mock(StreamInput.class)));
        runner.enqueue(docExample, new HashMap<String, String>() {
            {
                put("doc_id", "28039652143");
            }
        });
        runner.run(1, true, true);
        runner.assertAllFlowFilesTransferred(REL_RETRY, 1);
        runner.clearTransferState();
        // Elasticsearch Parse exception
        processor.setExceptionToThrow(new ElasticsearchParseException("test"));
        runner.enqueue(docExample, new HashMap<String, String>() {
            {
                put("doc_id", "28039652144");
            }
        });
        runner.run(1, true, true);
        // This test generates an exception on execute(),routes to failure
        runner.assertTransferCount(REL_FAILURE, 1);
    }

    @Test
    public void testPutElasticsearchOnTriggerWithNoIdAttribute() throws IOException {
        runner = TestRunners.newTestRunner(new TestPutElasticsearch.PutElasticsearchTestProcessor(true));// simulate failures

        runner.setProperty(CLUSTER_NAME, "elasticsearch");
        runner.setProperty(HOSTS, "127.0.0.1:9300");
        runner.setProperty(PING_TIMEOUT, "5s");
        runner.setProperty(SAMPLER_INTERVAL, "5s");
        runner.setProperty(INDEX, "doc");
        runner.setProperty(TYPE, "status");
        runner.setProperty(BATCH_SIZE, "1");
        runner.setProperty(ID_ATTRIBUTE, "doc_id");
        runner.enqueue(docExample);
        runner.run(1, true, true);
        runner.assertAllFlowFilesTransferred(REL_FAILURE, 1);
        final MockFlowFile out = runner.getFlowFilesForRelationship(REL_FAILURE).get(0);
        Assert.assertNotNull(out);
    }

    @Test
    public void testPutElasticsearchOnTriggerWithIndexFromAttribute() throws IOException {
        runner = TestRunners.newTestRunner(new TestPutElasticsearch.PutElasticsearchTestProcessor(false));
        runner.setProperty(CLUSTER_NAME, "elasticsearch");
        runner.setProperty(HOSTS, "127.0.0.1:9300");
        runner.setProperty(PING_TIMEOUT, "5s");
        runner.setProperty(SAMPLER_INTERVAL, "5s");
        runner.setProperty(INDEX, "${i}");
        runner.setProperty(TYPE, "${type}");
        runner.setProperty(BATCH_SIZE, "1");
        runner.setProperty(ID_ATTRIBUTE, "doc_id");
        runner.enqueue(docExample, new HashMap<String, String>() {
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
        runner.enqueue(docExample, new HashMap<String, String>() {
            {
                put("doc_id", "28039652144");
                put("type", "status");
            }
        });
        runner.run(1, true, true);
        runner.assertAllFlowFilesTransferred(PutElasticsearch.REL_RETRY, 1);
        final MockFlowFile out2 = runner.getFlowFilesForRelationship(PutElasticsearch.REL_RETRY).get(0);
        Assert.assertNotNull(out2);
    }

    @Test
    public void testPutElasticSearchOnTriggerWithInvalidIndexOp() throws IOException {
        runner = TestRunners.newTestRunner(new TestPutElasticsearch.PutElasticsearchTestProcessor(false));// no failures

        runner.setProperty(CLUSTER_NAME, "elasticsearch");
        runner.setProperty(HOSTS, "127.0.0.1:9300");
        runner.setProperty(PING_TIMEOUT, "5s");
        runner.setProperty(SAMPLER_INTERVAL, "5s");
        runner.setProperty(INDEX, "doc");
        runner.assertNotValid();
        runner.setProperty(TYPE, "status");
        runner.setProperty(BATCH_SIZE, "1");
        runner.assertNotValid();
        runner.setProperty(ID_ATTRIBUTE, "doc_id");
        runner.assertValid();
        runner.setProperty(INDEX_OP, "index_fail");
        runner.assertValid();
        runner.enqueue(docExample, new HashMap<String, String>() {
            {
                put("doc_id", "28039652140");
            }
        });
        runner.run(1, true, true);
        runner.assertAllFlowFilesTransferred(REL_FAILURE, 1);
        final MockFlowFile out = runner.getFlowFilesForRelationship(REL_FAILURE).get(0);
        Assert.assertNotNull(out);
    }

    /**
     * A Test class that extends the processor in order to inject/mock behavior
     */
    private static class PutElasticsearchTestProcessor extends PutElasticsearch {
        boolean responseHasFailures = false;

        Exception exceptionToThrow = null;

        public PutElasticsearchTestProcessor(boolean responseHasFailures) {
            this.responseHasFailures = responseHasFailures;
        }

        public void setExceptionToThrow(Exception exceptionToThrow) {
            this.exceptionToThrow = exceptionToThrow;
        }

        @Override
        public void createElasticsearchClient(ProcessContext context) throws ProcessException {
            final Client mockClient = Mockito.mock(Client.class);
            BulkRequestBuilder bulkRequestBuilder = Mockito.spy(new BulkRequestBuilder(mockClient, BulkAction.INSTANCE));
            if ((exceptionToThrow) != null) {
                Mockito.doThrow(exceptionToThrow).when(bulkRequestBuilder).execute();
            } else {
                Mockito.doReturn(new TestPutElasticsearch.PutElasticsearchTestProcessor.MockBulkRequestBuilderExecutor(responseHasFailures)).when(bulkRequestBuilder).execute();
            }
            Mockito.when(mockClient.prepareBulk()).thenReturn(bulkRequestBuilder);
            Mockito.when(mockClient.prepareIndex(ArgumentMatchers.anyString(), ArgumentMatchers.anyString(), ArgumentMatchers.anyString())).thenAnswer(new Answer<IndexRequestBuilder>() {
                @Override
                public IndexRequestBuilder answer(InvocationOnMock invocationOnMock) throws Throwable {
                    Object[] args = invocationOnMock.getArguments();
                    String arg1 = ((String) (args[0]));
                    if (arg1.isEmpty()) {
                        throw new NoNodeAvailableException("Needs index");
                    }
                    String arg2 = ((String) (args[1]));
                    if (arg2.isEmpty()) {
                        throw new NoNodeAvailableException("Needs doc type");
                    } else {
                        IndexRequestBuilder indexRequestBuilder = new IndexRequestBuilder(mockClient, IndexAction.INSTANCE);
                        return indexRequestBuilder;
                    }
                }
            });
            esClient.set(mockClient);
        }

        private static class MockBulkRequestBuilderExecutor extends AdapterActionFuture<BulkResponse, ActionListener<BulkResponse>> implements ListenableActionFuture<BulkResponse> {
            boolean responseHasFailures = false;

            public MockBulkRequestBuilderExecutor(boolean responseHasFailures) {
                this.responseHasFailures = responseHasFailures;
            }

            @Override
            protected BulkResponse convert(ActionListener<BulkResponse> bulkResponseActionListener) {
                return null;
            }

            @Override
            public void addListener(ActionListener<BulkResponse> actionListener) {
            }

            @Override
            public BulkResponse get() throws InterruptedException, ExecutionException {
                BulkResponse response = Mockito.mock(BulkResponse.class);
                Mockito.when(response.hasFailures()).thenReturn(responseHasFailures);
                BulkItemResponse item1 = Mockito.mock(BulkItemResponse.class);
                BulkItemResponse item2 = Mockito.mock(BulkItemResponse.class);
                Mockito.when(item1.getItemId()).thenReturn(1);
                Mockito.when(item1.isFailed()).thenReturn(true);
                BulkItemResponse.Failure failure = Mockito.mock(Failure.class);
                Mockito.when(failure.getMessage()).thenReturn("Bad message");
                Mockito.when(item1.getFailure()).thenReturn(failure);
                Mockito.when(item2.getItemId()).thenReturn(2);
                Mockito.when(item2.isFailed()).thenReturn(false);
                Mockito.when(response.getItems()).thenReturn(new BulkItemResponse[]{ item1, item2 });
                return response;
            }
        }
    }
}

