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


import DeleteByQueryElasticsearch.ERROR_ATTRIBUTE;
import DeleteByQueryElasticsearch.INDEX;
import DeleteByQueryElasticsearch.QUERY;
import DeleteByQueryElasticsearch.QUERY_ATTRIBUTE;
import DeleteByQueryElasticsearch.REL_FAILURE;
import DeleteByQueryElasticsearch.REL_SUCCESS;
import DeleteByQueryElasticsearch.TYPE;
import java.util.HashMap;
import java.util.Map;
import org.apache.nifi.util.MockFlowFile;
import org.apache.nifi.util.TestRunner;
import org.apache.nifi.util.TestRunners;
import org.junit.Assert;
import org.junit.Test;


public class DeleteByQueryElasticsearchTest {
    private static final String INDEX = "test_idx";

    private static final String TYPE = "test_type";

    private static final String QUERY_ATTR = "es.delete.query";

    private static final String CLIENT_NAME = "clientService";

    private TestElasticSearchClientService client;

    @Test
    public void testWithFlowfileInput() throws Exception {
        String query = "{ \"query\": { \"match_all\": {} }}";
        TestRunner runner = TestRunners.newTestRunner(DeleteByQueryElasticsearch.class);
        runner.setProperty(DeleteByQueryElasticsearch.INDEX, DeleteByQueryElasticsearchTest.INDEX);
        runner.setProperty(DeleteByQueryElasticsearch.TYPE, DeleteByQueryElasticsearchTest.TYPE);
        runner.setProperty(QUERY_ATTRIBUTE, DeleteByQueryElasticsearchTest.QUERY_ATTR);
        initClient(runner);
        runner.assertValid();
        runner.enqueue(query);
        runner.run();
        postTest(runner, query);
    }

    @Test
    public void testWithQuery() throws Exception {
        String query = "{\n" + ((((("\t\"query\": {\n" + "\t\t\"match\": {\n") + "\t\t\t\"${field.name}.keyword\": \"test\"\n") + "\t\t}\n") + "\t}\n") + "}");
        Map<String, String> attrs = new HashMap<String, String>() {
            {
                put("field.name", "test_field");
            }
        };
        TestRunner runner = TestRunners.newTestRunner(DeleteByQueryElasticsearch.class);
        runner.setProperty(QUERY, query);
        runner.setProperty(DeleteByQueryElasticsearch.INDEX, DeleteByQueryElasticsearchTest.INDEX);
        runner.setProperty(DeleteByQueryElasticsearch.TYPE, DeleteByQueryElasticsearchTest.TYPE);
        runner.setProperty(QUERY_ATTRIBUTE, DeleteByQueryElasticsearchTest.QUERY_ATTR);
        initClient(runner);
        runner.assertValid();
        runner.enqueue("", attrs);
        runner.run();
        postTest(runner, query.replace("${field.name}", "test_field"));
        runner.clearTransferState();
        query = "{\n" + ((((("\t\"query\": {\n" + "\t\t\"match\": {\n") + "\t\t\t\"test_field.keyword\": \"test\"\n") + "\t\t}\n") + "\t}\n") + "}");
        runner.setProperty(QUERY, query);
        runner.setIncomingConnection(false);
        runner.assertValid();
        runner.run();
        postTest(runner, query);
    }

    @Test
    public void testErrorAttribute() throws Exception {
        String query = "{ \"query\": { \"match_all\": {} }}";
        TestRunner runner = TestRunners.newTestRunner(DeleteByQueryElasticsearch.class);
        runner.setProperty(QUERY, query);
        runner.setProperty(DeleteByQueryElasticsearch.INDEX, DeleteByQueryElasticsearchTest.INDEX);
        runner.setProperty(DeleteByQueryElasticsearch.TYPE, DeleteByQueryElasticsearchTest.TYPE);
        runner.setProperty(QUERY_ATTRIBUTE, DeleteByQueryElasticsearchTest.QUERY_ATTR);
        initClient(runner);
        client.setThrowErrorInDelete(true);
        runner.assertValid();
        runner.enqueue("");
        runner.run();
        runner.assertTransferCount(REL_SUCCESS, 0);
        runner.assertTransferCount(REL_FAILURE, 1);
        MockFlowFile mockFlowFile = runner.getFlowFilesForRelationship(REL_FAILURE).get(0);
        String attr = mockFlowFile.getAttribute(ERROR_ATTRIBUTE);
        Assert.assertNotNull(attr);
    }
}

