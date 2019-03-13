package io.searchbox.indices.reindex;


import ESIntegTestCase.ClusterScope;
import ESIntegTestCase.Scope;
import com.google.common.collect.ImmutableMap;
import io.searchbox.client.JestResult;
import io.searchbox.common.AbstractIntegrationTest;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.elasticsearch.index.query.QueryBuilders;
import org.junit.Test;


/**
 *
 *
 * @author fabien baligand
 */
@ClusterScope(scope = Scope.SUITE)
public class ReindexIntegrationTest extends AbstractIntegrationTest {
    @Test
    public void testReindex() throws IOException, InterruptedException {
        String sourceIndex = "my_source_index";
        String destIndex = "my_dest_index";
        String documentType = "my_type";
        String documentId = "my_id";
        createIndex(sourceIndex);
        index(sourceIndex, documentType, documentId, "{}");
        flushAndRefresh(sourceIndex);
        ImmutableMap<String, Object> source = ImmutableMap.of("index", sourceIndex);
        ImmutableMap<String, Object> dest = ImmutableMap.of("index", destIndex);
        Reindex reindex = new Reindex.Builder(source, dest).refresh(true).build();
        JestResult result = client.execute(reindex);
        assertTrue(result.getErrorMessage(), result.isSucceeded());
        assertTrue(indexExists(destIndex));
        assertTrue(get(destIndex, documentType, documentId).isExists());
    }

    @Test
    public void documentShouldBeTransferredWithQuery() throws IOException {
        String sourceIndex = "my_source_index";
        String destIndex = "my_dest_index";
        String documentType = "my_type";
        String documentId = "my_id";
        createIndex(sourceIndex);
        index(sourceIndex, documentType, documentId, "{\"user\": \"kimchy\"}");
        flushAndRefresh(sourceIndex);
        Map<String, Object> source = new HashMap<>();
        source.put("index", sourceIndex);
        source.put("type", documentType);
        source.put("query", QueryBuilders.termQuery("user", "kimchy").toString());
        source = ImmutableMap.copyOf(source);
        ImmutableMap<String, Object> dest = ImmutableMap.of("index", destIndex);
        Reindex reindex = new Reindex.Builder(source, dest).refresh(true).build();
        JestResult result = client.execute(reindex);
        assertTrue(result.getErrorMessage(), result.isSucceeded());
        assertTrue(indexExists(destIndex));
        assertTrue(get(destIndex, documentType, documentId).isExists());
        assertTrue(documentExists(destIndex, documentType, documentId));
    }

    @Test
    public void documentShouldNotBeTransferredWithQuery() throws IOException {
        String sourceIndex = "my_source_index";
        String destIndex = "my_dest_index";
        String documentType = "my_type";
        String documentId = "my_id";
        createIndex(sourceIndex);
        index(sourceIndex, documentType, documentId, "{\"user\": \"kimchy\"}");
        flushAndRefresh(sourceIndex);
        Map<String, Object> source = new HashMap<>();
        source.put("index", sourceIndex);
        source.put("type", documentType);
        source.put("query", QueryBuilders.termQuery("user", "not-kimchy").toString());
        source = ImmutableMap.copyOf(source);
        ImmutableMap<String, Object> dest = ImmutableMap.of("index", destIndex);
        Reindex reindex = new Reindex.Builder(source, dest).refresh(true).build();
        JestResult result = client.execute(reindex);
        assertTrue(result.getErrorMessage(), result.isSucceeded());
        assertFalse(indexExists(destIndex));
    }

    @Test
    public void testWithScriptString() throws IOException {
        String sourceIndex = "my_source_index";
        String destIndex = "my_dest_index";
        String documentType = "my_type";
        String documentId = "my_id";
        createIndex(sourceIndex);
        index(sourceIndex, documentType, documentId, "{\"user\": \"kimchy\"}");
        flushAndRefresh(sourceIndex);
        String scriptString = "{\n" + (((((("    \"lang\": \"painless\",\n" + "    \"source\": \"ctx._source.last = params.last;\\nctx._source.nick = params.nick\",\n") + "    \"params\": {\n") + "      \"last\": \"gaudreau\",\n") + "      \"nick\": \"hockey\"\n") + "    }\n") + "  }");
        ImmutableMap<String, Object> source = ImmutableMap.of("index", sourceIndex);
        ImmutableMap<String, Object> dest = ImmutableMap.of("index", destIndex);
        Reindex reindex = new Reindex.Builder(source, dest).script(scriptString).refresh(true).build();
        JestResult result = client.execute(reindex);
        assertTrue(result.getErrorMessage(), result.isSucceeded());
        assertTrue(indexExists(destIndex));
        assertTrue(get(destIndex, documentType, documentId).isExists());
    }
}

