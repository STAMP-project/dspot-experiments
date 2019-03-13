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


import DeleteResponse.Result;
import ElasticsearchConstants.PARAM_INDEX_ID;
import ElasticsearchConstants.PARAM_INDEX_NAME;
import ElasticsearchConstants.PARAM_INDEX_TYPE;
import ElasticsearchConstants.PARAM_OPERATION;
import ElasticsearchOperation.Delete;
import ElasticsearchOperation.Exists;
import ElasticsearchOperation.GetById;
import ElasticsearchOperation.Index;
import ElasticsearchOperation.Update;
import java.util.HashMap;
import java.util.Map;
import org.elasticsearch.action.DocWriteResponse;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.MultiSearchRequest;
import org.elasticsearch.action.search.MultiSearchResponse.Item;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.search.SearchHits;
import org.hamcrest.CoreMatchers;
import org.junit.Test;


public class ElasticsearchGetSearchDeleteExistsUpdateTest extends ElasticsearchBaseTest {
    @Test
    public void testGet() throws Exception {
        // first, Index a value
        Map<String, String> map = createIndexedData();
        sendBody("direct:index", map);
        String indexId = template.requestBody("direct:index", map, String.class);
        assertNotNull("indexId should be set", indexId);
        // now, verify GET succeeded
        GetResponse response = template.requestBody("direct:get", indexId, GetResponse.class);
        assertNotNull("response should not be null", response);
        assertNotNull("response source should not be null", response.getSource());
    }

    @Test
    public void testDelete() throws Exception {
        // first, Index a value
        Map<String, String> map = createIndexedData();
        sendBody("direct:index", map);
        String indexId = template.requestBody("direct:index", map, String.class);
        assertNotNull("indexId should be set", indexId);
        // now, verify GET succeeded
        GetResponse response = template.requestBody("direct:get", indexId, GetResponse.class);
        assertNotNull("response should not be null", response);
        assertNotNull("response source should not be null", response.getSource());
        // now, perform Delete
        DeleteResponse.Result deleteResponse = template.requestBody("direct:delete", indexId, Result.class);
        assertNotNull("response should not be null", deleteResponse);
        // now, verify GET fails to find the indexed value
        response = template.requestBody("direct:get", indexId, GetResponse.class);
        assertNotNull("response should not be null", response);
        assertNull("response source should be null", response.getSource());
    }

    @Test
    public void testSearchWithMapQuery() throws Exception {
        // first, Index a value
        Map<String, String> map = createIndexedData();
        String indexId = template.requestBody("direct:index", map, String.class);
        assertNotNull("indexId should be set", indexId);
        // now, verify GET succeeded
        GetResponse getResponse = template.requestBody("direct:get", indexId, GetResponse.class);
        assertNotNull("response should not be null", getResponse);
        assertNotNull("response source should not be null", getResponse.getSource());
        // now, verify GET succeeded
        Map<String, Object> actualQuery = new HashMap<>();
        actualQuery.put("testsearchwithmapquery-key", "testsearchwithmapquery-value");
        Map<String, Object> match = new HashMap<>();
        match.put("match", actualQuery);
        Map<String, Object> query = new HashMap<>();
        query.put("query", match);
        SearchHits response = template.requestBody("direct:search", match, SearchHits.class);
        assertNotNull("response should not be null", response);
        assertEquals("response hits should be == 0", 0, response.totalHits);
    }

    @Test
    public void testSearchWithStringQuery() throws Exception {
        // first, Index a value
        Map<String, String> map = createIndexedData();
        String indexId = template.requestBody("direct:index", map, String.class);
        assertNotNull("indexId should be set", indexId);
        // now, verify GET succeeded
        GetResponse getResponse = template.requestBody("direct:get", indexId, GetResponse.class);
        assertNotNull("response should not be null", getResponse);
        assertNotNull("response source should not be null", getResponse.getSource());
        SearchRequest req = new SearchRequest();
        req.indices("twitter");
        req.types("tweet");
        SearchHits response = template.requestBody("direct:search", req, SearchHits.class);
        assertNotNull("response should not be null", response);
        assertEquals("response hits should be == 0", 0, response.totalHits);
    }

    @Test
    public void testMultiSearch() throws Exception {
        // first, Index a value
        Map<String, String> map = createIndexedData();
        String indexId = template.requestBody("direct:index", map, String.class);
        assertNotNull("indexId should be set", indexId);
        // now, verify GET succeeded
        GetResponse getResponse = template.requestBody("direct:get", indexId, GetResponse.class);
        assertNotNull("response should not be null", getResponse);
        assertNotNull("response source should not be null", getResponse.getSource());
        // now, verify GET succeeded
        SearchRequest req = new SearchRequest();
        req.indices("twitter");
        req.types("tweet");
        SearchRequest req1 = new SearchRequest();
        req.indices("twitter");
        req.types("tweets");
        MultiSearchRequest request = new MultiSearchRequest().add(req1).add(req);
        Item[] response = template.requestBody("direct:search", request, Item[].class);
        assertNotNull("response should not be null", response);
        assertEquals("response should be == 2", 2, response.length);
    }

    @Test
    public void testUpdate() throws Exception {
        Map<String, String> map = createIndexedData();
        String indexId = template.requestBody("direct:index", map, String.class);
        assertNotNull("indexId should be set", indexId);
        Map<String, String> newMap = new HashMap<>();
        newMap.put(((createPrefix()) + "key2"), ((createPrefix()) + "value2"));
        Map<String, Object> headers = new HashMap<>();
        headers.put(PARAM_INDEX_ID, indexId);
        indexId = template.requestBodyAndHeaders("direct:update", newMap, headers, String.class);
        assertNotNull("indexId should be set", indexId);
    }

    @Test
    public void testGetWithHeaders() throws Exception {
        // first, Index a value
        Map<String, String> map = createIndexedData();
        Map<String, Object> headers = new HashMap<>();
        headers.put(PARAM_OPERATION, Index);
        headers.put(PARAM_INDEX_NAME, "twitter");
        headers.put(PARAM_INDEX_TYPE, "tweet");
        String indexId = template.requestBodyAndHeaders("direct:start", map, headers, String.class);
        // now, verify GET
        headers.put(PARAM_OPERATION, GetById);
        GetResponse response = template.requestBodyAndHeaders("direct:start", indexId, headers, GetResponse.class);
        assertNotNull("response should not be null", response);
        assertNotNull("response source should not be null", response.getSource());
    }

    @Test
    public void testExistsWithHeaders() throws Exception {
        // first, Index a value
        Map<String, String> map = createIndexedData();
        Map<String, Object> headers = new HashMap<>();
        headers.put(PARAM_OPERATION, Index);
        headers.put(PARAM_INDEX_NAME, "twitter");
        headers.put(PARAM_INDEX_TYPE, "tweet");
        template.requestBodyAndHeaders("direct:start", map, headers, String.class);
        // now, verify GET
        headers.put(PARAM_OPERATION, Exists);
        headers.put(PARAM_INDEX_NAME, "twitter");
        Boolean exists = template.requestBodyAndHeaders("direct:exists", "", headers, Boolean.class);
        assertNotNull("response should not be null", exists);
        assertTrue("Index should exists", exists);
    }

    @Test
    public void testNotExistsWithHeaders() throws Exception {
        // first, Index a value
        Map<String, String> map = createIndexedData();
        Map<String, Object> headers = new HashMap<>();
        headers.put(PARAM_OPERATION, Index);
        headers.put(PARAM_INDEX_NAME, "twitter");
        headers.put(PARAM_INDEX_TYPE, "tweet");
        template.requestBodyAndHeaders("direct:start", map, headers, String.class);
        // now, verify GET
        headers.put(PARAM_OPERATION, Exists);
        headers.put(PARAM_INDEX_NAME, "twitter-tweet");
        Boolean exists = template.requestBodyAndHeaders("direct:exists", "", headers, Boolean.class);
        assertNotNull("response should not be null", exists);
        assertFalse("Index should not exists", exists);
    }

    @Test
    public void testDeleteWithHeaders() throws Exception {
        // first, Index a value
        Map<String, String> map = createIndexedData();
        Map<String, Object> headers = new HashMap<>();
        headers.put(PARAM_OPERATION, Index);
        headers.put(PARAM_INDEX_NAME, "twitter");
        headers.put(PARAM_INDEX_TYPE, "tweet");
        String indexId = template.requestBodyAndHeaders("direct:start", map, headers, String.class);
        // now, verify GET
        headers.put(PARAM_OPERATION, GetById);
        GetResponse response = template.requestBodyAndHeaders("direct:start", indexId, headers, GetResponse.class);
        assertNotNull("response should not be null", response);
        assertNotNull("response source should not be null", response.getSource());
        // now, perform Delete
        headers.put(PARAM_OPERATION, Delete);
        DocWriteResponse.Result deleteResponse = template.requestBodyAndHeaders("direct:start", indexId, headers, DocWriteResponse.Result.class);
        assertEquals("response should not be null", DocWriteResponse.Result.DELETED, deleteResponse);
        // now, verify GET fails to find the indexed value
        headers.put(PARAM_OPERATION, GetById);
        response = template.requestBodyAndHeaders("direct:start", indexId, headers, GetResponse.class);
        assertNotNull("response should not be null", response);
        assertNull("response source should be null", response.getSource());
    }

    @Test
    public void testUpdateWithIDInHeader() throws Exception {
        Map<String, String> map = createIndexedData();
        Map<String, Object> headers = new HashMap<>();
        headers.put(PARAM_OPERATION, Index);
        headers.put(PARAM_INDEX_NAME, "twitter");
        headers.put(PARAM_INDEX_TYPE, "tweet");
        headers.put(PARAM_INDEX_ID, "123");
        String indexId = template.requestBodyAndHeaders("direct:start", map, headers, String.class);
        assertNotNull("indexId should be set", indexId);
        assertEquals("indexId should be equals to the provided id", "123", indexId);
        headers.put(PARAM_OPERATION, Update);
        indexId = template.requestBodyAndHeaders("direct:start", map, headers, String.class);
        assertNotNull("indexId should be set", indexId);
        assertEquals("indexId should be equals to the provided id", "123", indexId);
    }

    @Test
    public void getRequestBody() throws Exception {
        String prefix = createPrefix();
        // given
        GetRequest request = new GetRequest((prefix + "foo")).type((prefix + "bar"));
        // when
        String documentId = template.requestBody("direct:index", new IndexRequest((prefix + "foo"), (prefix + "bar"), (prefix + "testId")).source((prefix + "content"), (prefix + "hello")), String.class);
        GetResponse response = template.requestBody("direct:get", request.id(documentId), GetResponse.class);
        // then
        assertThat(response, CoreMatchers.notNullValue());
        assertThat((prefix + "hello"), CoreMatchers.equalTo(response.getSourceAsMap().get((prefix + "content"))));
    }

    @Test
    public void deleteRequestBody() throws Exception {
        String prefix = createPrefix();
        // given
        DeleteRequest request = new DeleteRequest((prefix + "foo")).type((prefix + "bar"));
        // when
        String documentId = template.requestBody("direct:index", new IndexRequest((("" + prefix) + "foo"), (("" + prefix) + "bar"), (("" + prefix) + "testId")).source((prefix + "content"), (prefix + "hello")), String.class);
        DeleteResponse.Result response = template.requestBody("direct:delete", request.id(documentId), Result.class);
        // then
        assertThat(response, CoreMatchers.notNullValue());
    }

    @Test
    public void testStringUpdate() throws Exception {
        Map<String, String> map = createIndexedData();
        String indexId = template.requestBody("direct:index", map, String.class);
        assertNotNull("indexId should be set", indexId);
        String body = "{\"teststringupdate-key\" : \"teststringupdate-updated\"}";
        Map<String, Object> headers = new HashMap<>();
        headers.put(PARAM_INDEX_ID, indexId);
        indexId = template.requestBodyAndHeaders("direct:update", body, headers, String.class);
        assertNotNull("indexId should be set", indexId);
        GetResponse response = template.requestBody("direct:get", indexId, GetResponse.class);
        assertEquals("teststringupdate-updated", response.getSource().get("teststringupdate-key"));
    }
}

