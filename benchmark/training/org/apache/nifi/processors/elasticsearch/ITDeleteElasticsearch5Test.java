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
import DeleteElasticsearch5.ES_FILENAME;
import DeleteElasticsearch5.ES_INDEX;
import DeleteElasticsearch5.ES_TYPE;
import DeleteElasticsearch5.REL_NOT_FOUND;
import PutElasticsearch5.BATCH_SIZE;
import PutElasticsearch5.ID_ATTRIBUTE;
import PutElasticsearch5.INDEX;
import PutElasticsearch5.REL_SUCCESS;
import PutElasticsearch5.TYPE;
import java.io.InputStream;
import java.util.HashMap;
import org.apache.nifi.util.MockFlowFile;
import org.apache.nifi.util.TestRunner;
import org.apache.nifi.util.TestRunners;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.rest.RestStatus;
import org.junit.Ignore;
import org.junit.Test;


/**
 * Integration test for delete processor. Please set the hosts, cluster name, index and type etc before running the integrations.
 */
@Ignore("Comment this out for es delete integration testing and set the appropriate cluster name, hosts, etc")
public class ITDeleteElasticsearch5Test {
    private static final String TYPE1 = "type1";

    private static final String INDEX1 = "index1";

    protected DeleteResponse deleteResponse;

    protected RestStatus restStatus;

    private InputStream inputDocument;

    protected String clusterName = "elasticsearch";

    private String documentId;

    @Test
    public void testPutAndDeleteIntegrationTestSuccess() {
        final TestRunner runnerPut = TestRunners.newTestRunner(new PutElasticsearch5());
        runnerPut.setProperty(CLUSTER_NAME, clusterName);
        runnerPut.setProperty(HOSTS, "127.0.0.1:9300");
        runnerPut.setProperty(PING_TIMEOUT, "5s");
        runnerPut.setProperty(SAMPLER_INTERVAL, "5s");
        runnerPut.setProperty(INDEX, ITDeleteElasticsearch5Test.INDEX1);
        runnerPut.setProperty(BATCH_SIZE, "1");
        runnerPut.setProperty(TYPE, ITDeleteElasticsearch5Test.TYPE1);
        runnerPut.setProperty(ID_ATTRIBUTE, "id");
        runnerPut.assertValid();
        runnerPut.enqueue(inputDocument, new HashMap<String, String>() {
            {
                put("id", documentId);
            }
        });
        runnerPut.enqueue(inputDocument);
        runnerPut.run(1, true, true);
        runnerPut.assertAllFlowFilesTransferred(REL_SUCCESS, 1);
        final TestRunner runnerDelete = TestRunners.newTestRunner(new DeleteElasticsearch5());
        runnerDelete.setProperty(CLUSTER_NAME, clusterName);
        runnerDelete.setProperty(HOSTS, "127.0.0.1:9300");
        runnerDelete.setProperty(PING_TIMEOUT, "5s");
        runnerDelete.setProperty(SAMPLER_INTERVAL, "5s");
        runnerDelete.setProperty(DeleteElasticsearch5.INDEX, ITDeleteElasticsearch5Test.INDEX1);
        runnerDelete.setProperty(DeleteElasticsearch5.TYPE, ITDeleteElasticsearch5Test.TYPE1);
        runnerDelete.setProperty(DOCUMENT_ID, "${documentId}");
        runnerDelete.assertValid();
        runnerDelete.enqueue(new byte[]{  }, new HashMap<String, String>() {
            {
                put("documentId", documentId);
            }
        });
        runnerDelete.enqueue(new byte[]{  });
        runnerDelete.run(1, true, true);
        runnerDelete.assertAllFlowFilesTransferred(REL_SUCCESS, 1);
    }

    @Test
    public void testDeleteIntegrationTestDocumentNotFound() {
        final TestRunner runnerDelete = TestRunners.newTestRunner(new DeleteElasticsearch5());
        runnerDelete.setProperty(CLUSTER_NAME, clusterName);
        runnerDelete.setProperty(HOSTS, "127.0.0.1:9300");
        runnerDelete.setProperty(PING_TIMEOUT, "5s");
        runnerDelete.setProperty(SAMPLER_INTERVAL, "5s");
        runnerDelete.setProperty(DeleteElasticsearch5.INDEX, ITDeleteElasticsearch5Test.INDEX1);
        runnerDelete.setProperty(DeleteElasticsearch5.TYPE, ITDeleteElasticsearch5Test.TYPE1);
        runnerDelete.setProperty(DOCUMENT_ID, "${documentId}");
        runnerDelete.assertValid();
        runnerDelete.enqueue(new byte[]{  }, new HashMap<String, String>() {
            {
                put("documentId", documentId);
            }
        });
        runnerDelete.enqueue(new byte[]{  });
        runnerDelete.run(1, true, true);
        runnerDelete.assertAllFlowFilesTransferred(REL_NOT_FOUND, 1);
        final MockFlowFile out = runnerDelete.getFlowFilesForRelationship(REL_NOT_FOUND).get(0);
        out.assertAttributeEquals(ES_FILENAME, documentId);
        out.assertAttributeEquals(ES_INDEX, ITDeleteElasticsearch5Test.INDEX1);
        out.assertAttributeEquals(ES_TYPE, ITDeleteElasticsearch5Test.TYPE1);
    }

    @Test
    public void testDeleteIntegrationTestBadIndex() {
        final TestRunner runnerDelete = TestRunners.newTestRunner(new DeleteElasticsearch5());
        runnerDelete.setProperty(CLUSTER_NAME, clusterName);
        runnerDelete.setProperty(HOSTS, "127.0.0.1:9300");
        runnerDelete.setProperty(PING_TIMEOUT, "5s");
        runnerDelete.setProperty(SAMPLER_INTERVAL, "5s");
        String index = String.valueOf(System.currentTimeMillis());
        runnerDelete.setProperty(DeleteElasticsearch5.INDEX, index);
        runnerDelete.setProperty(DeleteElasticsearch5.TYPE, ITDeleteElasticsearch5Test.TYPE1);
        runnerDelete.setProperty(DOCUMENT_ID, "${documentId}");
        runnerDelete.assertValid();
        runnerDelete.enqueue(new byte[]{  }, new HashMap<String, String>() {
            {
                put("documentId", documentId);
            }
        });
        runnerDelete.enqueue(new byte[]{  });
        runnerDelete.run(1, true, true);
        runnerDelete.assertAllFlowFilesTransferred(REL_NOT_FOUND, 1);
        final MockFlowFile out = runnerDelete.getFlowFilesForRelationship(REL_NOT_FOUND).get(0);
        out.assertAttributeEquals(ES_FILENAME, documentId);
        out.assertAttributeEquals(ES_INDEX, index);
        out.assertAttributeEquals(ES_TYPE, ITDeleteElasticsearch5Test.TYPE1);
    }

    @Test
    public void testDeleteIntegrationTestBadType() {
        final TestRunner runnerDelete = TestRunners.newTestRunner(new DeleteElasticsearch5());
        runnerDelete.setProperty(CLUSTER_NAME, clusterName);
        runnerDelete.setProperty(HOSTS, "127.0.0.1:9300");
        runnerDelete.setProperty(PING_TIMEOUT, "5s");
        runnerDelete.setProperty(SAMPLER_INTERVAL, "5s");
        runnerDelete.setProperty(DeleteElasticsearch5.INDEX, ITDeleteElasticsearch5Test.INDEX1);
        String type = String.valueOf(System.currentTimeMillis());
        runnerDelete.setProperty(DeleteElasticsearch5.TYPE, type);
        runnerDelete.setProperty(DOCUMENT_ID, "${documentId}");
        runnerDelete.assertValid();
        runnerDelete.enqueue(new byte[]{  }, new HashMap<String, String>() {
            {
                put("documentId", documentId);
            }
        });
        runnerDelete.enqueue(new byte[]{  });
        runnerDelete.run(1, true, true);
        runnerDelete.assertAllFlowFilesTransferred(REL_NOT_FOUND, 1);
        final MockFlowFile out = runnerDelete.getFlowFilesForRelationship(REL_NOT_FOUND).get(0);
        out.assertAttributeEquals(ES_FILENAME, documentId);
        out.assertAttributeEquals(ES_INDEX, ITDeleteElasticsearch5Test.INDEX1);
        out.assertAttributeEquals(ES_TYPE, type);
    }
}

