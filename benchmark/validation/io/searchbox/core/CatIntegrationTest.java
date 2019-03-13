package io.searchbox.core;


import ESIntegTestCase.ClusterScope;
import ESIntegTestCase.Scope;
import IndicesAliasesAction.INSTANCE;
import JSONCompareMode.LENIENT;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import io.searchbox.common.AbstractIntegrationTest;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.IntStream;
import org.json.JSONException;
import org.junit.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompare;


/**
 *
 *
 * @author Bartosz Polnik
 */
@ClusterScope(scope = Scope.TEST, numDataNodes = 1)
public class CatIntegrationTest extends AbstractIntegrationTest {
    static final String INDEX = "catintegrationindex";

    static final String ALIAS = "catintegrationalias";

    static final String INDEX2 = "catintegrationindex2";

    @Test
    public void shouldReturnEmptyPlainTextForIndices() throws IOException {
        CatResult result = client.execute(new Cat.IndicesBuilder().build());
        assertEquals(new JsonArray(), result.getJsonObject().get(result.getPathToResult()));
        assertArrayEquals(new String[0][0], result.getPlainText());
    }

    @Test
    public void shouldProperlyMapSingleResult() throws IOException, JSONException {
        createIndex(CatIntegrationTest.INDEX);
        ensureSearchable(CatIntegrationTest.INDEX);
        CatResult result = client.execute(new Cat.IndicesBuilder().setParameter("h", "index,docs.count").build());
        assertArrayEquals(new String[][]{ new String[]{ "index", "docs.count" }, new String[]{ CatIntegrationTest.INDEX, "0" } }, result.getPlainText());
        JSONAssert.assertEquals("[{\"index\":\"catintegrationindex\",\"docs.count\":\"0\"}]", result.getSourceAsString(), false);
    }

    @Test
    public void shouldFilterResultsToASingleIndex() throws IOException, JSONException {
        createIndex(CatIntegrationTest.INDEX, CatIntegrationTest.INDEX2);
        ensureSearchable(CatIntegrationTest.INDEX, CatIntegrationTest.INDEX2);
        CatResult result = client.execute(new Cat.IndicesBuilder().setParameter("h", "index,docs.count").addIndex(CatIntegrationTest.INDEX2).build());
        assertArrayEquals(new String[][]{ new String[]{ "index", "docs.count" }, new String[]{ CatIntegrationTest.INDEX2, "0" } }, result.getPlainText());
        JSONAssert.assertEquals("[{\"index\":\"catintegrationindex2\",\"docs.count\":\"0\"}]", result.getSourceAsString(), false);
    }

    @Test
    public void shouldDisplayAliasForSingleResult() throws IOException, JSONException {
        createIndex(CatIntegrationTest.INDEX);
        ensureSearchable(CatIntegrationTest.INDEX);
        INSTANCE.newRequestBuilder(client().admin().indices()).addAlias(CatIntegrationTest.INDEX, CatIntegrationTest.ALIAS).get();
        CatResult result = client.execute(new Cat.AliasesBuilder().setParameter("h", "alias,index").build());
        assertArrayEquals(new String[][]{ new String[]{ "alias", "index" }, new String[]{ CatIntegrationTest.ALIAS, CatIntegrationTest.INDEX } }, result.getPlainText());
        JSONAssert.assertEquals("[{\"alias\":\"catintegrationalias\",\"index\":\"catintegrationindex\"}]", result.getSourceAsString(), false);
    }

    @Test
    public void shouldDisplayRecoveryForSingleResult() throws IOException, JSONException {
        createIndex(CatIntegrationTest.INDEX);
        ensureSearchable(CatIntegrationTest.INDEX);
        CatResult result = client.execute(new Cat.RecoveryBuilder().addIndex(CatIntegrationTest.INDEX).setParameter("h", "index,stage").build());
        ArrayList<String[]> expectedPlainText = new ArrayList<>();
        ArrayList<String> recoveryResponsePerShared = new ArrayList<>();
        String expectedLine = "{\"index\":\"catintegrationindex\",\"stage\":\"done\"}";
        expectedPlainText.add(new String[]{ "index", "stage" });
        IntStream.range(0, getNumShards(CatIntegrationTest.INDEX).totalNumShards).forEach(( value) -> {
            expectedPlainText.add(new String[]{ INDEX, "done" });
            recoveryResponsePerShared.add(expectedLine);
        });
        assertArrayEquals(expectedPlainText.toArray(), result.getPlainText());
        String expectedSourceAsString = ("[" + (String.join(",", recoveryResponsePerShared))) + "]";
        JSONAssert.assertEquals(expectedSourceAsString, result.getSourceAsString(), false);
    }

    @Test
    public void shouldChangeOrderOfColumnsByspecifyingParameters() throws IOException, JSONException {
        createIndex(CatIntegrationTest.INDEX);
        ensureSearchable(CatIntegrationTest.INDEX);
        INSTANCE.newRequestBuilder(client().admin().indices()).addAlias(CatIntegrationTest.INDEX, CatIntegrationTest.ALIAS).get();
        CatResult result = client.execute(new Cat.AliasesBuilder().setParameter("h", "index,alias").build());
        assertArrayEquals(new String[][]{ new String[]{ "index", "alias" }, new String[]{ CatIntegrationTest.INDEX, CatIntegrationTest.ALIAS } }, result.getPlainText());
        JSONAssert.assertEquals("[{\"index\":\"catintegrationindex\",\"alias\":\"catintegrationalias\"}]", result.getSourceAsString(), false);
    }

    @Test
    public void catAllShards() throws IOException, JSONException {
        createIndex(CatIntegrationTest.INDEX);
        createIndex(CatIntegrationTest.INDEX2);
        ensureSearchable(CatIntegrationTest.INDEX);
        ensureSearchable(CatIntegrationTest.INDEX2);
        CatResult catResult = client.execute(new Cat.ShardsBuilder().setParameter("h", "index,docs").build());
        JsonArray shards = catResult.getJsonObject().get("result").getAsJsonArray();
        assertEquals(shards.size(), ((getNumShards(CatIntegrationTest.INDEX).totalNumShards) + (getNumShards(CatIntegrationTest.INDEX2).totalNumShards)));
        int index1Count = 0;
        int index2Count = 0;
        for (JsonElement shard : shards) {
            index1Count += (JSONCompare.compareJSON((("{\"index\":\"" + (CatIntegrationTest.INDEX)) + "\",\"docs\":\"0\"}"), shard.toString(), LENIENT).passed()) ? 1 : 0;
            index2Count += (JSONCompare.compareJSON((("{\"index\":\"" + (CatIntegrationTest.INDEX2)) + "\",\"docs\":\"0\"}"), shard.toString(), LENIENT).passed()) ? 1 : 0;
        }
        assertTrue((index1Count > 0));
        assertTrue((index2Count > 0));
        assertEquals((index1Count + index2Count), shards.size());
    }

    @Test
    public void catShardsSingleIndex() throws IOException, JSONException {
        createIndex(CatIntegrationTest.INDEX);
        createIndex(CatIntegrationTest.INDEX2);
        ensureSearchable(CatIntegrationTest.INDEX);
        ensureSearchable(CatIntegrationTest.INDEX2);
        CatResult catResult = client.execute(new Cat.ShardsBuilder().addIndex(CatIntegrationTest.INDEX).setParameter("h", "index,docs").build());
        JsonArray shards = catResult.getJsonObject().get("result").getAsJsonArray();
        assertEquals(shards.size(), getNumShards(CatIntegrationTest.INDEX).totalNumShards);
        for (JsonElement shard : shards) {
            JSONAssert.assertEquals("{\"index\":\"catintegrationindex\",\"docs\":\"0\"}", shard.toString(), false);
        }
    }

    @Test
    public void catNodes() throws IOException {
        CatResult catResult = client.execute(new Cat.NodesBuilder().setParameter("h", "name").build());
        JsonArray nodes = catResult.getJsonObject().get("result").getAsJsonArray();
        Set<String> expectedNodeNames = new HashSet(Arrays.asList(internalCluster().getNodeNames()));
        Set<String> actualNodeNames = new HashSet<>();
        for (JsonElement node : nodes) {
            actualNodeNames.add(node.getAsJsonObject().get("name").getAsString());
        }
        assertEquals(actualNodeNames, expectedNodeNames);
    }
}

