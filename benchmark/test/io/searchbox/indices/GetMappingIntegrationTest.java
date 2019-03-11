package io.searchbox.indices;


import ESIntegTestCase.ClusterScope;
import ESIntegTestCase.Scope;
import XContentType.JSON;
import com.google.gson.JsonObject;
import io.searchbox.action.Action;
import io.searchbox.client.JestResult;
import io.searchbox.common.AbstractIntegrationTest;
import io.searchbox.indices.mapping.GetMapping;
import org.elasticsearch.action.admin.indices.mapping.put.PutMappingRequest;
import org.elasticsearch.action.admin.indices.mapping.put.PutMappingResponse;
import org.elasticsearch.action.admin.indices.refresh.RefreshRequest;
import org.elasticsearch.action.admin.indices.refresh.RefreshResponse;
import org.junit.Test;


/**
 *
 *
 * @author cihat keser
 */
@ClusterScope(scope = Scope.TEST, numDataNodes = 1)
public class GetMappingIntegrationTest extends AbstractIntegrationTest {
    static final String INDEX_1_NAME = "book";

    static final String INDEX_2_NAME = "video";

    static final String CUSTOM_TYPE = "science-fiction";

    @Test
    public void testWithoutParameters() throws Exception {
        createIndex(GetMappingIntegrationTest.INDEX_1_NAME, GetMappingIntegrationTest.INDEX_2_NAME);
        PutMappingResponse putMappingResponse = client().admin().indices().putMapping(new PutMappingRequest(GetMappingIntegrationTest.INDEX_1_NAME).type(GetMappingIntegrationTest.CUSTOM_TYPE).source(("{\"science-fiction\":{\"properties\":{\"title\":{\"store\":true,\"type\":\"text\"}," + "\"author\":{\"store\":true,\"type\":\"text\"}}}}"), JSON)).actionGet();
        assertTrue(putMappingResponse.isAcknowledged());
        assertConcreteMappingsOnAll(GetMappingIntegrationTest.INDEX_1_NAME, GetMappingIntegrationTest.CUSTOM_TYPE, "title", "author");
        RefreshResponse refreshResponse = client().admin().indices().refresh(new RefreshRequest(GetMappingIntegrationTest.INDEX_1_NAME, GetMappingIntegrationTest.INDEX_2_NAME)).actionGet();
        assertEquals("All shards should have been refreshed", 0, refreshResponse.getFailedShards());
        GetMapping getMapping = new GetMapping.Builder().build();
        JestResult result = client.execute(getMapping);
        assertTrue(result.getErrorMessage(), result.isSucceeded());
        JsonObject resultJson = result.getJsonObject();
        assertNotNull(("GetMapping response JSON should include the index " + (GetMappingIntegrationTest.INDEX_1_NAME)), resultJson.getAsJsonObject(GetMappingIntegrationTest.INDEX_1_NAME));
        assertNotNull(("GetMapping response JSON should include the index " + (GetMappingIntegrationTest.INDEX_2_NAME)), resultJson.getAsJsonObject(GetMappingIntegrationTest.INDEX_2_NAME));
    }

    @Test
    public void testWithSingleIndex() throws Exception {
        createIndex(GetMappingIntegrationTest.INDEX_1_NAME, GetMappingIntegrationTest.INDEX_2_NAME);
        PutMappingResponse putMappingResponse = client().admin().indices().putMapping(new PutMappingRequest(GetMappingIntegrationTest.INDEX_1_NAME).type(GetMappingIntegrationTest.CUSTOM_TYPE).source(("{\"science-fiction\":{\"properties\":{\"title\":{\"store\":true,\"type\":\"text\"}," + "\"author\":{\"store\":true,\"type\":\"text\"}}}}"), JSON)).actionGet();
        assertTrue(putMappingResponse.isAcknowledged());
        assertConcreteMappingsOnAll(GetMappingIntegrationTest.INDEX_1_NAME, GetMappingIntegrationTest.CUSTOM_TYPE, "title", "author");
        RefreshResponse refreshResponse = client().admin().indices().refresh(new RefreshRequest(GetMappingIntegrationTest.INDEX_1_NAME, GetMappingIntegrationTest.INDEX_2_NAME)).actionGet();
        assertEquals("All shards should have been refreshed", 0, refreshResponse.getFailedShards());
        Action getMapping = new GetMapping.Builder().addIndex(GetMappingIntegrationTest.INDEX_2_NAME).build();
        JestResult result = client.execute(getMapping);
        assertTrue(result.getErrorMessage(), result.isSucceeded());
        System.out.println(("result.getJsonString() = " + (result.getJsonString())));
        JsonObject resultJson = result.getJsonObject();
        assertNotNull(("GetMapping response JSON should include the index " + (GetMappingIntegrationTest.INDEX_2_NAME)), resultJson.getAsJsonObject(GetMappingIntegrationTest.INDEX_2_NAME));
    }

    @Test
    public void testWithMultipleIndices() throws Exception {
        createIndex(GetMappingIntegrationTest.INDEX_1_NAME, GetMappingIntegrationTest.INDEX_2_NAME, "irrelevant");
        PutMappingResponse putMappingResponse = client().admin().indices().putMapping(new PutMappingRequest(GetMappingIntegrationTest.INDEX_1_NAME).type(GetMappingIntegrationTest.CUSTOM_TYPE).source(("{\"science-fiction\":{\"properties\":{\"title\":{\"store\":true,\"type\":\"text\"}," + "\"author\":{\"store\":true,\"type\":\"text\"}}}}"), JSON)).actionGet();
        assertTrue(putMappingResponse.isAcknowledged());
        putMappingResponse = client().admin().indices().putMapping(new PutMappingRequest(GetMappingIntegrationTest.INDEX_2_NAME).type(GetMappingIntegrationTest.CUSTOM_TYPE).source(("{\"science-fiction\":{\"properties\":{\"title\":{\"store\":false,\"type\":\"text\"}," + "\"isbn\":{\"store\":true,\"type\":\"text\"}}}}"), JSON)).actionGet();
        assertTrue(putMappingResponse.isAcknowledged());
        assertConcreteMappingsOnAll(GetMappingIntegrationTest.INDEX_1_NAME, GetMappingIntegrationTest.CUSTOM_TYPE, "title", "author");
        assertConcreteMappingsOnAll(GetMappingIntegrationTest.INDEX_2_NAME, GetMappingIntegrationTest.CUSTOM_TYPE, "title", "isbn");
        RefreshResponse refreshResponse = client().admin().indices().refresh(new RefreshRequest(GetMappingIntegrationTest.INDEX_1_NAME, GetMappingIntegrationTest.INDEX_2_NAME)).actionGet();
        assertEquals("All shards should have been refreshed", 0, refreshResponse.getFailedShards());
        Action getMapping = new GetMapping.Builder().addIndex(GetMappingIntegrationTest.INDEX_2_NAME).addIndex(GetMappingIntegrationTest.INDEX_1_NAME).build();
        JestResult result = client.execute(getMapping);
        assertTrue(result.getErrorMessage(), result.isSucceeded());
        JsonObject resultJson = result.getJsonObject();
        assertNotNull(("GetMapping response JSON should include the index " + (GetMappingIntegrationTest.INDEX_1_NAME)), resultJson.getAsJsonObject(GetMappingIntegrationTest.INDEX_1_NAME));
        assertNotNull(("GetMapping response JSON should include the index " + (GetMappingIntegrationTest.INDEX_2_NAME)), resultJson.getAsJsonObject(GetMappingIntegrationTest.INDEX_2_NAME));
    }
}

