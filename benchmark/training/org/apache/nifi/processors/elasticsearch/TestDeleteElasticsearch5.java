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
import DeleteElasticsearch5.DOCUMENT_ID;
import DeleteElasticsearch5.ES_ERROR_MESSAGE;
import DeleteElasticsearch5.ES_FILENAME;
import DeleteElasticsearch5.ES_INDEX;
import DeleteElasticsearch5.ES_REST_STATUS;
import DeleteElasticsearch5.ES_TYPE;
import DeleteElasticsearch5.INDEX;
import DeleteElasticsearch5.REL_FAILURE;
import DeleteElasticsearch5.REL_NOT_FOUND;
import DeleteElasticsearch5.REL_RETRY;
import DeleteElasticsearch5.REL_SUCCESS;
import DeleteElasticsearch5.TYPE;
import DeleteElasticsearch5.UNABLE_TO_DELETE_DOCUMENT_MESSAGE;
import java.io.IOException;
import java.util.HashMap;
import java.util.concurrent.ExecutionException;
import org.apache.nifi.processor.ProcessContext;
import org.apache.nifi.util.MockFlowFile;
import org.apache.nifi.util.TestRunner;
import org.apache.nifi.util.TestRunners;
import org.elasticsearch.ElasticsearchTimeoutException;
import org.elasticsearch.action.delete.DeleteRequestBuilder;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.rest.RestStatus;
import org.junit.Assert;
import org.junit.Test;


public class TestDeleteElasticsearch5 {
    private String documentId;

    private static final String TYPE1 = "type1";

    private static final String INDEX1 = "index1";

    private TestRunner runner;

    protected DeleteResponse deleteResponse;

    protected RestStatus restStatus;

    private DeleteElasticsearch5 mockDeleteProcessor;

    long currentTimeMillis;

    @Test
    public void testDeleteWithNoDocumentId() throws IOException {
        runner.enqueue(new byte[]{  });
        runner.run(1, true, true);
        runner.assertAllFlowFilesTransferred(REL_FAILURE, 1);
        final MockFlowFile out = runner.getFlowFilesForRelationship(REL_FAILURE).get(0);
        Assert.assertNotNull(out);
        Assert.assertEquals("Document id is required but was empty", out.getAttribute(ES_ERROR_MESSAGE));
    }

    @Test
    public void testDeleteWithNoIndex() throws IOException {
        runner.setProperty(INDEX, "${index}");
        runner.enqueue(new byte[]{  });
        runner.run(1, true, true);
        runner.assertAllFlowFilesTransferred(REL_FAILURE, 1);
        final MockFlowFile out = runner.getFlowFilesForRelationship(REL_FAILURE).get(0);
        Assert.assertNotNull(out);
        Assert.assertEquals("Index is required but was empty", out.getAttribute(ES_ERROR_MESSAGE));
    }

    @Test
    public void testDeleteWithNoType() throws IOException {
        runner.setProperty(TYPE, "${type}");
        runner.enqueue(new byte[]{  });
        runner.run(1, true, true);
        runner.assertAllFlowFilesTransferred(REL_FAILURE, 1);
        final MockFlowFile out = runner.getFlowFilesForRelationship(REL_FAILURE).get(0);
        Assert.assertNotNull(out);
        Assert.assertEquals("Document type is required but was empty", out.getAttribute(ES_ERROR_MESSAGE));
    }

    @Test
    public void testDeleteSuccessful() throws IOException {
        restStatus = RestStatus.OK;
        deleteResponse = new DeleteResponse(null, TestDeleteElasticsearch5.TYPE1, documentId, 1, true) {
            @Override
            public RestStatus status() {
                return restStatus;
            }
        };
        runner.enqueue(new byte[]{  }, new HashMap<String, String>() {
            {
                put("documentId", documentId);
            }
        });
        runner.run(1, true, true);
        runner.assertAllFlowFilesTransferred(REL_SUCCESS, 1);
        final MockFlowFile out = runner.getFlowFilesForRelationship(REL_SUCCESS).get(0);
        Assert.assertNotNull(out);
        Assert.assertEquals(null, out.getAttribute(ES_ERROR_MESSAGE));
        out.assertAttributeEquals(ES_FILENAME, documentId);
        out.assertAttributeEquals(ES_INDEX, TestDeleteElasticsearch5.INDEX1);
        out.assertAttributeEquals(ES_TYPE, TestDeleteElasticsearch5.TYPE1);
    }

    @Test
    public void testDeleteNotFound() throws IOException {
        restStatus = RestStatus.NOT_FOUND;
        deleteResponse = new DeleteResponse(null, TestDeleteElasticsearch5.TYPE1, documentId, 1, true) {
            @Override
            public RestStatus status() {
                return restStatus;
            }
        };
        runner.enqueue(new byte[]{  }, new HashMap<String, String>() {
            {
                put("documentId", documentId);
            }
        });
        runner.run(1, true, true);
        runner.assertAllFlowFilesTransferred(REL_NOT_FOUND, 1);
        final MockFlowFile out = runner.getFlowFilesForRelationship(REL_NOT_FOUND).get(0);
        Assert.assertNotNull(out);
        Assert.assertEquals(UNABLE_TO_DELETE_DOCUMENT_MESSAGE, out.getAttribute(ES_ERROR_MESSAGE));
        out.assertAttributeEquals(ES_REST_STATUS, restStatus.toString());
        out.assertAttributeEquals(ES_FILENAME, documentId);
        out.assertAttributeEquals(ES_INDEX, TestDeleteElasticsearch5.INDEX1);
        out.assertAttributeEquals(ES_TYPE, TestDeleteElasticsearch5.TYPE1);
    }

    @Test
    public void testDeleteServerFailure() throws IOException {
        restStatus = RestStatus.SERVICE_UNAVAILABLE;
        deleteResponse = new DeleteResponse(null, TestDeleteElasticsearch5.TYPE1, documentId, 1, true) {
            @Override
            public RestStatus status() {
                return restStatus;
            }
        };
        runner.enqueue(new byte[]{  }, new HashMap<String, String>() {
            {
                put("documentId", documentId);
            }
        });
        runner.run(1, true, true);
        runner.assertAllFlowFilesTransferred(REL_FAILURE, 1);
        final MockFlowFile out = runner.getFlowFilesForRelationship(REL_FAILURE).get(0);
        Assert.assertNotNull(out);
        Assert.assertEquals(UNABLE_TO_DELETE_DOCUMENT_MESSAGE, out.getAttribute(ES_ERROR_MESSAGE));
        out.assertAttributeEquals(ES_REST_STATUS, restStatus.toString());
        out.assertAttributeEquals(ES_FILENAME, documentId);
        out.assertAttributeEquals(ES_INDEX, TestDeleteElasticsearch5.INDEX1);
        out.assertAttributeEquals(ES_TYPE, TestDeleteElasticsearch5.TYPE1);
    }

    @Test
    public void testDeleteRetryableException() throws IOException {
        mockDeleteProcessor = new DeleteElasticsearch5() {
            @Override
            protected DeleteRequestBuilder prepareDeleteRequest(String index, String docId, String docType) {
                return null;
            }

            @Override
            protected DeleteResponse doDelete(DeleteRequestBuilder requestBuilder) throws InterruptedException, ExecutionException {
                throw new ElasticsearchTimeoutException("timeout");
            }

            @Override
            public void setup(ProcessContext context) {
            }
        };
        runner = TestRunners.newTestRunner(mockDeleteProcessor);
        runner.setProperty(CLUSTER_NAME, "elasticsearch");
        runner.setProperty(HOSTS, "127.0.0.1:9300");
        runner.setProperty(PING_TIMEOUT, "5s");
        runner.setProperty(SAMPLER_INTERVAL, "5s");
        runner.setProperty(INDEX, TestDeleteElasticsearch5.INDEX1);
        runner.setProperty(TYPE, TestDeleteElasticsearch5.TYPE1);
        runner.setProperty(DOCUMENT_ID, "${documentId}");
        runner.assertValid();
        runner.enqueue(new byte[]{  }, new HashMap<String, String>() {
            {
                put("documentId", documentId);
            }
        });
        runner.run(1, true, true);
        runner.assertAllFlowFilesTransferred(REL_RETRY, 1);
        final MockFlowFile out = runner.getFlowFilesForRelationship(REL_RETRY).get(0);
        Assert.assertNotNull(out);
        Assert.assertEquals("timeout", out.getAttribute(ES_ERROR_MESSAGE));
        out.assertAttributeEquals(ES_REST_STATUS, null);
        out.assertAttributeEquals(ES_FILENAME, documentId);
        out.assertAttributeEquals(ES_INDEX, TestDeleteElasticsearch5.INDEX1);
        out.assertAttributeEquals(ES_TYPE, TestDeleteElasticsearch5.TYPE1);
    }

    @Test
    public void testDeleteNonRetryableException() throws IOException {
        mockDeleteProcessor = new DeleteElasticsearch5() {
            @Override
            protected DeleteRequestBuilder prepareDeleteRequest(String index, String docId, String docType) {
                return null;
            }

            @Override
            protected DeleteResponse doDelete(DeleteRequestBuilder requestBuilder) throws InterruptedException, ExecutionException {
                throw new InterruptedException("exception");
            }

            @Override
            public void setup(ProcessContext context) {
            }
        };
        runner = TestRunners.newTestRunner(mockDeleteProcessor);
        runner.setProperty(CLUSTER_NAME, "elasticsearch");
        runner.setProperty(HOSTS, "127.0.0.1:9300");
        runner.setProperty(PING_TIMEOUT, "5s");
        runner.setProperty(SAMPLER_INTERVAL, "5s");
        runner.setProperty(INDEX, TestDeleteElasticsearch5.INDEX1);
        runner.setProperty(TYPE, TestDeleteElasticsearch5.TYPE1);
        runner.setProperty(DOCUMENT_ID, "${documentId}");
        runner.assertValid();
        runner.enqueue(new byte[]{  }, new HashMap<String, String>() {
            {
                put("documentId", documentId);
            }
        });
        runner.run(1, true, true);
        runner.assertAllFlowFilesTransferred(REL_FAILURE, 1);
        final MockFlowFile out = runner.getFlowFilesForRelationship(REL_FAILURE).get(0);
        Assert.assertNotNull(out);
        Assert.assertEquals("exception", out.getAttribute(ES_ERROR_MESSAGE));
        out.assertAttributeEquals(ES_REST_STATUS, null);
        out.assertAttributeEquals(ES_FILENAME, documentId);
        out.assertAttributeEquals(ES_INDEX, TestDeleteElasticsearch5.INDEX1);
        out.assertAttributeEquals(ES_TYPE, TestDeleteElasticsearch5.TYPE1);
    }
}

