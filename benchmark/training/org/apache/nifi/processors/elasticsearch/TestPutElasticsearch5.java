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


import AbstractElasticsearch5TransportClientProcessor.CLUSTER_NAME;
import AbstractElasticsearch5TransportClientProcessor.HOSTS;
import AbstractElasticsearch5TransportClientProcessor.PING_TIMEOUT;
import AbstractElasticsearch5TransportClientProcessor.SAMPLER_INTERVAL;
import FetchElasticsearch5.REL_RETRY;
import PutElasticsearch5.BATCH_SIZE;
import PutElasticsearch5.ID_ATTRIBUTE;
import PutElasticsearch5.INDEX;
import PutElasticsearch5.INDEX_OP;
import PutElasticsearch5.REL_FAILURE;
import PutElasticsearch5.REL_SUCCESS;
import PutElasticsearch5.TYPE;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;
import org.apache.nifi.logging.ComponentLog;
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
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.TransportAddress;
import org.elasticsearch.node.NodeClosedException;
import org.elasticsearch.transport.ReceiveTimeoutTransportException;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;


public class TestPutElasticsearch5 {
    private InputStream docExample;

    private TestRunner runner;

    @Test
    public void testPutElasticSearchOnTrigger() throws IOException {
        runner = TestRunners.newTestRunner(new TestPutElasticsearch5.PutElasticsearch5TestProcessor(false));// no failures

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
        final MockFlowFile out = runner.getFlowFilesForRelationship(REL_SUCCESS).get(0);
        Assert.assertNotNull(out);
        out.assertAttributeEquals("doc_id", "28039652140");
    }

    @Test
    public void testPutElasticSearchOnTriggerEL() throws IOException {
        runner = TestRunners.newTestRunner(new TestPutElasticsearch5.PutElasticsearch5TestProcessor(false));// no failures

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
    public void testPutElasticSearchOnTriggerBadDocIdentifier() throws IOException {
        runner = TestRunners.newTestRunner(new TestPutElasticsearch5.PutElasticsearch5TestProcessor(false));// no failures

        runner.setProperty(CLUSTER_NAME, "elasticsearch");
        runner.setProperty(HOSTS, "127.0.0.1:9300");
        runner.setProperty(PING_TIMEOUT, "5s");
        runner.setProperty(SAMPLER_INTERVAL, "5s");
        runner.setProperty(INDEX, "doc");
        runner.assertNotValid();
        runner.setProperty(TYPE, "status");
        runner.setProperty(BATCH_SIZE, "1");
        runner.assertNotValid();
        runner.setProperty(ID_ATTRIBUTE, "doc_id2");
        runner.assertValid();
        runner.enqueue(docExample, new HashMap<String, String>() {
            {
                put("doc_id", "28039652140");
            }
        });
        runner.run(1, true, true);
        runner.assertAllFlowFilesTransferred(REL_FAILURE, 1);
    }

    @Test
    public void testPutElasticSearchOnTriggerWithFailures() throws IOException {
        runner = TestRunners.newTestRunner(new TestPutElasticsearch5.PutElasticsearch5TestProcessor(true));// simulate failures

        runner.setProperty(CLUSTER_NAME, "elasticsearch");
        runner.setProperty(HOSTS, "127.0.0.1:9300");
        runner.setProperty(PING_TIMEOUT, "5s");
        runner.setProperty(SAMPLER_INTERVAL, "5s");
        runner.setProperty(INDEX, "doc");
        runner.setProperty(TYPE, "status");
        runner.setProperty(BATCH_SIZE, "1");
        runner.setProperty(ID_ATTRIBUTE, "doc_id");
        runner.enqueue(docExample, new HashMap<String, String>() {
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
    public void testPutElasticsearch5OnTriggerWithExceptions() throws IOException {
        TestPutElasticsearch5.PutElasticsearch5TestProcessor processor = new TestPutElasticsearch5.PutElasticsearch5TestProcessor(false);
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
        // Elasticsearch5 Timeout exception
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
        // Elasticsearch5 Parse exception
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
    public void testPutElasticsearch5OnTriggerWithNoIdAttribute() throws IOException {
        runner = TestRunners.newTestRunner(new TestPutElasticsearch5.PutElasticsearch5TestProcessor(true));// simulate failures

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
    public void testPutElasticsearch5OnTriggerWithIndexFromAttribute() throws IOException {
        runner = TestRunners.newTestRunner(new TestPutElasticsearch5.PutElasticsearch5TestProcessor(false));
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
        runner.assertAllFlowFilesTransferred(PutElasticsearch5.REL_RETRY, 1);
        final MockFlowFile out2 = runner.getFlowFilesForRelationship(PutElasticsearch5.REL_RETRY).get(0);
        Assert.assertNotNull(out2);
    }

    @Test
    public void testPutElasticSearchOnTriggerWithInvalidIndexOp() throws IOException {
        runner = TestRunners.newTestRunner(new TestPutElasticsearch5.PutElasticsearch5TestProcessor(false));// no failures

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
    private static class PutElasticsearch5TestProcessor extends PutElasticsearch5 {
        boolean responseHasFailures = false;

        Exception exceptionToThrow = null;

        public PutElasticsearch5TestProcessor(boolean responseHasFailures) {
            this.responseHasFailures = responseHasFailures;
        }

        public void setExceptionToThrow(Exception exceptionToThrow) {
            this.exceptionToThrow = exceptionToThrow;
        }

        @Override
        protected Client getTransportClient(Settings.Builder settingsBuilder, String xPackPath, String username, String password, List<InetSocketAddress> esHosts, ComponentLog log) throws MalformedURLException {
            final Client mockClient = Mockito.mock(Client.class);
            BulkRequestBuilder bulkRequestBuilder = Mockito.spy(new BulkRequestBuilder(mockClient, BulkAction.INSTANCE));
            if ((exceptionToThrow) != null) {
                Mockito.doThrow(exceptionToThrow).when(bulkRequestBuilder).execute();
            } else {
                Mockito.doReturn(new TestPutElasticsearch5.PutElasticsearch5TestProcessor.MockBulkRequestBuilderExecutor(responseHasFailures, esHosts.get(0))).when(bulkRequestBuilder).execute();
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
            return mockClient;
        }

        private static class MockBulkRequestBuilderExecutor extends AdapterActionFuture<BulkResponse, ActionListener<BulkResponse>> implements ListenableActionFuture<BulkResponse> {
            boolean responseHasFailures = false;

            InetSocketAddress address = null;

            public MockBulkRequestBuilderExecutor(boolean responseHasFailures, InetSocketAddress address) {
                this.responseHasFailures = responseHasFailures;
                this.address = address;
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
                BulkItemResponse item = Mockito.mock(BulkItemResponse.class);
                Mockito.when(item.getItemId()).thenReturn(1);
                Mockito.when(item.isFailed()).thenReturn(true);
                Mockito.when(response.getItems()).thenReturn(new BulkItemResponse[]{ item });
                TransportAddress remoteAddress = Mockito.mock(TransportAddress.class);
                Mockito.when(remoteAddress.getAddress()).thenReturn(address.toString());
                Mockito.when(response.remoteAddress()).thenReturn(remoteAddress);
                return response;
            }
        }
    }
}

