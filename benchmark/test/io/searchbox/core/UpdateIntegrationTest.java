package io.searchbox.core;


import ESIntegTestCase.ClusterScope;
import ESIntegTestCase.Scope;
import RestStatus.CONFLICT;
import WriteRequest.RefreshPolicy.IMMEDIATE;
import XContentType.JSON;
import io.searchbox.common.AbstractIntegrationTest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.junit.Test;


/**
 *
 *
 * @author Dogukan Sonmez
 */
@ClusterScope(scope = Scope.SUITE, numDataNodes = 1)
public class UpdateIntegrationTest extends AbstractIntegrationTest {
    private static final String INDEX = "twitter";

    private static final String TYPE = "tweet";

    @Test
    public void scriptedUpdateWithValidParameters() throws Exception {
        String id = "1";
        String script = "{\n" + ((((((("  \"script\": {\n" + "    \"lang\": \"painless\",\n") + "    \"inline\": \"ctx._source.tags += params.tag\",\n") + "    \"params\": {\n") + "      \"tag\": \"blue\"\n") + "    }\n") + "  }\n") + "}");
        client().index(new IndexRequest(UpdateIntegrationTest.INDEX, UpdateIntegrationTest.TYPE, id).source("{\"user\":\"kimchy\", \"tags\":\"That is test\"}", JSON).setRefreshPolicy(IMMEDIATE)).actionGet();
        DocumentResult result = client.execute(new Update.Builder(script).index(UpdateIntegrationTest.INDEX).type(UpdateIntegrationTest.TYPE).id(id).build());
        assertTrue(result.getErrorMessage(), result.isSucceeded());
        assertEquals(UpdateIntegrationTest.INDEX, result.getIndex());
        assertEquals(UpdateIntegrationTest.TYPE, result.getType());
        assertEquals(id, result.getId());
        GetResponse getResult = get(UpdateIntegrationTest.INDEX, UpdateIntegrationTest.TYPE, id);
        assertTrue(getResult.isExists());
        assertFalse(getResult.isSourceEmpty());
        assertEquals("That is testblue", getResult.getSource().get("tags"));
    }

    @Test
    public void partialDocUpdateWithValidParameters() throws Exception {
        String id = "2";
        String partialDoc = "{\n" + ((("    \"doc\" : {\n" + "        \"tags\" : \"blue\"\n") + "    }\n") + "}");
        client().index(new IndexRequest(UpdateIntegrationTest.INDEX, UpdateIntegrationTest.TYPE, id).source("{\"user\":\"kimchy\", \"tags\":\"That is test\"}", JSON).setRefreshPolicy(IMMEDIATE)).actionGet();
        DocumentResult result = client.execute(new Update.Builder(partialDoc).index(UpdateIntegrationTest.INDEX).type(UpdateIntegrationTest.TYPE).id(id).build());
        assertTrue(result.getErrorMessage(), result.isSucceeded());
        assertEquals(UpdateIntegrationTest.INDEX, result.getIndex());
        assertEquals(UpdateIntegrationTest.TYPE, result.getType());
        assertEquals(id, result.getId());
        GetResponse getResult = get(UpdateIntegrationTest.INDEX, UpdateIntegrationTest.TYPE, id);
        assertTrue(getResult.isExists());
        assertFalse(getResult.isSourceEmpty());
        assertEquals("blue", getResult.getSource().get("tags"));
    }

    @Test
    public void partialDocUpdateWithValidVersion() throws Exception {
        String id = "2";
        String partialDoc = "{\n" + ((("    \"doc\" : {\n" + "        \"tags\" : \"blue\"\n") + "    }\n") + "}");
        IndexResponse response = client().index(new IndexRequest(UpdateIntegrationTest.INDEX, UpdateIntegrationTest.TYPE, id).source("{\"user\":\"kimchy\", \"tags\":\"That is test\"}", JSON).setRefreshPolicy(IMMEDIATE)).actionGet();
        long version = response.getVersion();
        DocumentResult result = client.execute(new Update.VersionBuilder(partialDoc, version).index(UpdateIntegrationTest.INDEX).type(UpdateIntegrationTest.TYPE).id(id).build());
        assertTrue(result.getErrorMessage(), result.isSucceeded());
        assertEquals(UpdateIntegrationTest.INDEX, result.getIndex());
        assertEquals(UpdateIntegrationTest.TYPE, result.getType());
        assertEquals(id, result.getId());
        assertEquals((version + 1), result.getVersion().longValue());
        GetResponse getResult = get(UpdateIntegrationTest.INDEX, UpdateIntegrationTest.TYPE, id);
        assertTrue(getResult.isExists());
        assertFalse(getResult.isSourceEmpty());
        assertEquals("blue", getResult.getSource().get("tags"));
    }

    @Test
    public void partialDocUpdateWithInvalidVersion() throws Exception {
        String id = "2";
        String partialDoc = "{\n" + ((("    \"doc\" : {\n" + "        \"tags\" : \"blue\"\n") + "    }\n") + "}");
        String partialDoc2 = "{\n" + ((("    \"doc\" : {\n" + "        \"tags\" : \"red\"\n") + "    }\n") + "}");
        IndexResponse response = client().index(new IndexRequest(UpdateIntegrationTest.INDEX, UpdateIntegrationTest.TYPE, id).source("{\"user\":\"kimchy\", \"tags\":\"That is test\"}", JSON).setRefreshPolicy(IMMEDIATE)).actionGet();
        long version = response.getVersion();
        DocumentResult result = client.execute(new Update.VersionBuilder(partialDoc, version).index(UpdateIntegrationTest.INDEX).type(UpdateIntegrationTest.TYPE).id(id).build());
        assertTrue(result.getErrorMessage(), result.isSucceeded());
        // and again ...
        result = client.execute(new Update.VersionBuilder(partialDoc2, version).index(UpdateIntegrationTest.INDEX).type(UpdateIntegrationTest.TYPE).id(id).build());
        assertFalse(result.getErrorMessage(), result.isSucceeded());
        assertEquals("Invalid response code", CONFLICT.getStatus(), result.getResponseCode());
        GetResponse getResult = get(UpdateIntegrationTest.INDEX, UpdateIntegrationTest.TYPE, id);
        assertTrue(getResult.isExists());
        assertEquals((version + 1), getResult.getVersion());
        assertEquals("blue", getResult.getSource().get("tags"));
    }
}

