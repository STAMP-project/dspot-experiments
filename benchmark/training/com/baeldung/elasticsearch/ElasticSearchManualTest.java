package com.baeldung.elasticsearch;


import Result.CREATED;
import Result.DELETED;
import SearchType.DFS_QUERY_THEN_FETCH;
import XContentType.JSON;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.junit.Assert;
import org.junit.Test;


/**
 * This Manual test requires:
 * * Elasticsearch instance running on host
 * * with cluster name = elasticsearch
 */
public class ElasticSearchManualTest {
    private List<Person> listOfPersons = new ArrayList<>();

    private Client client = null;

    @Test
    public void givenJsonString_whenJavaObject_thenIndexDocument() {
        String jsonObject = "{\"age\":20,\"dateOfBirth\":1471466076564,\"fullName\":\"John Doe\"}";
        IndexResponse response = client.prepareIndex("people", "Doe").setSource(jsonObject, JSON).get();
        String index = response.getIndex();
        String type = response.getType();
        Assert.assertEquals(CREATED, response.getResult());
        Assert.assertEquals(index, "people");
        Assert.assertEquals(type, "Doe");
    }

    @Test
    public void givenDocumentId_whenJavaObject_thenDeleteDocument() {
        String jsonObject = "{\"age\":10,\"dateOfBirth\":1471455886564,\"fullName\":\"Johan Doe\"}";
        IndexResponse response = client.prepareIndex("people", "Doe").setSource(jsonObject, JSON).get();
        String id = response.getId();
        DeleteResponse deleteResponse = client.prepareDelete("people", "Doe", id).get();
        Assert.assertEquals(DELETED, deleteResponse.getResult());
    }

    @Test
    public void givenSearchRequest_whenMatchAll_thenReturnAllResults() {
        SearchResponse response = client.prepareSearch().execute().actionGet();
        SearchHit[] searchHits = response.getHits().getHits();
        List<Person> results = Arrays.stream(searchHits).map(( hit) -> com.alibaba.fastjson.JSON.parseObject(hit.getSourceAsString(), .class)).collect(Collectors.toList());
    }

    @Test
    public void givenSearchParameters_thenReturnResults() {
        SearchResponse response = client.prepareSearch().setTypes().setSearchType(DFS_QUERY_THEN_FETCH).setPostFilter(QueryBuilders.rangeQuery("age").from(5).to(15)).setFrom(0).setSize(60).setExplain(true).execute().actionGet();
        SearchResponse response2 = client.prepareSearch().setTypes().setSearchType(DFS_QUERY_THEN_FETCH).setPostFilter(QueryBuilders.simpleQueryStringQuery("+John -Doe OR Janette")).setFrom(0).setSize(60).setExplain(true).execute().actionGet();
        SearchResponse response3 = client.prepareSearch().setTypes().setSearchType(DFS_QUERY_THEN_FETCH).setPostFilter(QueryBuilders.matchQuery("John", "Name*")).setFrom(0).setSize(60).setExplain(true).execute().actionGet();
        response2.getHits();
        response3.getHits();
        final List<Person> results = Arrays.stream(response.getHits().getHits()).map(( hit) -> com.alibaba.fastjson.JSON.parseObject(hit.getSourceAsString(), .class)).collect(Collectors.toList());
    }

    @Test
    public void givenContentBuilder_whenHelpers_thanIndexJson() throws IOException {
        XContentBuilder builder = XContentFactory.jsonBuilder().startObject().field("fullName", "Test").field("salary", "11500").field("age", "10").endObject();
        IndexResponse response = client.prepareIndex("people", "Doe").setSource(builder).get();
        Assert.assertEquals(CREATED, response.getResult());
    }
}

