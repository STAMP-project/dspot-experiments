package io.searchbox.indices.aliases;


import ESIntegTestCase.ClusterScope;
import ESIntegTestCase.Scope;
import IndicesAliasesRequest.AliasActions;
import io.searchbox.client.JestResult;
import io.searchbox.common.AbstractIntegrationTest;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import org.elasticsearch.action.admin.indices.alias.IndicesAliasesRequest;
import org.elasticsearch.action.admin.indices.alias.IndicesAliasesResponse;
import org.junit.Test;


/**
 *
 *
 * @author cihat keser
 */
@ClusterScope(scope = Scope.SUITE, numDataNodes = 1)
public class GetAliasesIntegrationTest extends AbstractIntegrationTest {
    private static final String INDEX_NAME = "aliases_test_index";

    private static final String INDEX_NAME_2 = "aliases_test_index2";

    private static final String INDEX_NAME_3 = "aliases_test_index3";

    @Test
    public void testGetAliases() throws IOException {
        String alias = "myAlias000";
        IndicesAliasesRequest indicesAliasesRequest = new IndicesAliasesRequest();
        IndicesAliasesRequest.AliasActions action = AliasActions.add().index(GetAliasesIntegrationTest.INDEX_NAME).alias(alias);
        indicesAliasesRequest.addAliasAction(action);
        IndicesAliasesResponse indicesAliasesResponse = client().admin().indices().aliases(indicesAliasesRequest).actionGet(10, TimeUnit.SECONDS);
        assertNotNull(indicesAliasesResponse);
        assertTrue(indicesAliasesResponse.isAcknowledged());
        GetAliases getAliases = new GetAliases.Builder().build();
        JestResult result = client.execute(getAliases);
        assertTrue(result.getErrorMessage(), result.isSucceeded());
        assertEquals(1, result.getJsonObject().getAsJsonObject(GetAliasesIntegrationTest.INDEX_NAME).getAsJsonObject("aliases").entrySet().size());
        assertEquals(0, result.getJsonObject().getAsJsonObject(GetAliasesIntegrationTest.INDEX_NAME_2).getAsJsonObject("aliases").entrySet().size());
    }

    @Test
    public void testGetAliasesForSpecificIndex() throws IOException {
        String alias = "myAlias000";
        IndicesAliasesRequest indicesAliasesRequest = new IndicesAliasesRequest();
        IndicesAliasesRequest.AliasActions action = AliasActions.add().index(GetAliasesIntegrationTest.INDEX_NAME).alias(alias);
        indicesAliasesRequest.addAliasAction(action);
        IndicesAliasesResponse indicesAliasesResponse = client().admin().indices().aliases(indicesAliasesRequest).actionGet(10, TimeUnit.SECONDS);
        assertNotNull(indicesAliasesResponse);
        assertTrue(indicesAliasesResponse.isAcknowledged());
        GetAliases getAliases = new GetAliases.Builder().addIndex(GetAliasesIntegrationTest.INDEX_NAME).build();
        JestResult result = client.execute(getAliases);
        assertTrue(result.getErrorMessage(), result.isSucceeded());
        assertEquals(1, result.getJsonObject().entrySet().size());
        assertEquals(1, result.getJsonObject().getAsJsonObject(GetAliasesIntegrationTest.INDEX_NAME).getAsJsonObject("aliases").entrySet().size());
    }

    @Test
    public void testGetAliasesForMultipleSpecificIndices() throws IOException {
        GetAliases getAliases = new GetAliases.Builder().addIndex(GetAliasesIntegrationTest.INDEX_NAME).addIndex(GetAliasesIntegrationTest.INDEX_NAME_3).build();
        JestResult result = client.execute(getAliases);
        assertTrue(result.getErrorMessage(), result.isSucceeded());
        assertEquals(2, result.getJsonObject().entrySet().size());
        assertEquals(0, result.getJsonObject().getAsJsonObject(GetAliasesIntegrationTest.INDEX_NAME).getAsJsonObject("aliases").entrySet().size());
        assertEquals(0, result.getJsonObject().getAsJsonObject(GetAliasesIntegrationTest.INDEX_NAME_3).getAsJsonObject("aliases").entrySet().size());
    }
}

