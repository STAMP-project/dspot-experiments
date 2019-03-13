/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.camel.component.elasticsearch;


import ElasticsearchConstants.PARAM_INDEX_ID;
import ElasticsearchConstants.PARAM_INDEX_NAME;
import ElasticsearchConstants.PARAM_INDEX_TYPE;
import ElasticsearchConstants.PARAM_OPERATION;
import ElasticsearchOperation.Index;
import java.util.HashMap;
import java.util.Map;
import org.apache.http.impl.client.BasicResponseHandler;
import org.elasticsearch.action.get.GetRequest;
import org.junit.Test;


public class ElasticsearchClusterIndexTest extends ElasticsearchClusterBaseTest {
    @Test
    public void indexWithIpAndPort() throws Exception {
        Map<String, String> map = createIndexedData();
        Map<String, Object> headers = new HashMap<>();
        headers.put(PARAM_OPERATION, Index);
        headers.put(PARAM_INDEX_NAME, "twitter");
        headers.put(PARAM_INDEX_TYPE, "tweet");
        headers.put(PARAM_INDEX_ID, "1");
        String indexId = template.requestBodyAndHeaders("direct:indexWithIpAndPort", map, headers, String.class);
        assertNotNull("indexId should be set", indexId);
        indexId = template.requestBodyAndHeaders("direct:indexWithIpAndPort", map, headers, String.class);
        assertNotNull("indexId should be set", indexId);
        assertEquals("Cluster must be of three nodes", ElasticsearchClusterBaseTest.runner.getNodeSize(), 3);
        assertEquals("Index id 1 must exists", true, ElasticsearchClusterBaseTest.client.get(new GetRequest("twitter", "tweet", "1")).isExists());
    }

    @Test
    public void indexWithSnifferEnable() throws Exception {
        Map<String, String> map = createIndexedData();
        Map<String, Object> headers = new HashMap<>();
        headers.put(PARAM_OPERATION, Index);
        headers.put(PARAM_INDEX_NAME, "facebook");
        headers.put(PARAM_INDEX_TYPE, "post");
        headers.put(PARAM_INDEX_ID, "4");
        String indexId = template.requestBodyAndHeaders("direct:indexWithSniffer", map, headers, String.class);
        assertNotNull("indexId should be set", indexId);
        assertEquals("Cluster must be of three nodes", ElasticsearchClusterBaseTest.runner.getNodeSize(), 3);
        assertEquals("Index id 4 must exists", true, ElasticsearchClusterBaseTest.client.get(new GetRequest("facebook", "post", "4")).isExists());
        final BasicResponseHandler responseHandler = new BasicResponseHandler();
        String body = responseHandler.handleEntity(ElasticsearchClusterBaseTest.restClient.performRequest("GET", "/_cluster/health?pretty").getEntity());
        assertStringContains(body, "\"number_of_data_nodes\" : 3");
    }
}

