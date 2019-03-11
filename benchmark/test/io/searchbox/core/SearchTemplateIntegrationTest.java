package io.searchbox.core;


import DocWriteResponse.Result.CREATED;
import ESIntegTestCase.ClusterScope;
import ESIntegTestCase.Scope;
import SearchResult.Hit;
import WriteRequest.RefreshPolicy.IMMEDIATE;
import XContentType.JSON;
import io.searchbox.client.JestResult;
import io.searchbox.common.AbstractIntegrationTest;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.common.bytes.BytesArray;
import org.junit.Test;


/**
 *
 *
 * @author Bobby Hubbard
 */
// TODO: Test file-based templates $ES_CONFIG/scripts/test.mustache
@ClusterScope(scope = Scope.SUITE, numDataNodes = 1)
public class SearchTemplateIntegrationTest extends AbstractIntegrationTest {
    private static final String INDEX = "twitter";

    private static final String TYPE = "tweet";

    private static final String QUERY = "{" + (((("    \"id\": \"templateId\"," + "    \"params\": {") + "        \"user\" : \"kimchy1\"") + "    }") + "}");

    private static final String INLINE = "{" + ((((((((((("    \"inline\": {" + "    \t\"query\": {") + "    \t\t\"term\": {") + "       \t\t\"user\" : \"{{user}}\"") + "    		}") + "    	},") + "    \t\"sort\": \"num\"") + "	},") + "   \"params\": {") + "        \"user\" : \"kimchy1\"") + "    }") + "}");

    private static final String SCRIPT = "{" + ((((((((((("    \"script\": {" + "       \"lang\": \"mustache\",") + "       \"source\": {") + "           \"query\": {") + "               \"match\": {") + "                   \"user\": \"{{user}}\"") + "               }") + "           },") + "    \t    \"sort\": \"num\"") + "       }") + "   }") + "}");

    @Test
    public void searchWithValidQuery() throws IOException {
        JestResult result = client.execute(new Search.TemplateBuilder(SearchTemplateIntegrationTest.INLINE).build());
        assertTrue(result.getErrorMessage(), result.isSucceeded());
    }

    @Test
    public void searchTemplateInlineScript() throws Exception {
        assertTrue(client().index(new IndexRequest(SearchTemplateIntegrationTest.INDEX, SearchTemplateIntegrationTest.TYPE).source("{\"user\":\"kimchy1\",\"num\":1}", JSON).setRefreshPolicy(IMMEDIATE)).actionGet().getResult().equals(CREATED));
        assertTrue(client().index(new IndexRequest(SearchTemplateIntegrationTest.INDEX, SearchTemplateIntegrationTest.TYPE).source("{\"user\":\"abcdef\",\"num\":2}", JSON).setRefreshPolicy(IMMEDIATE)).actionGet().getResult().equals(CREATED));
        assertTrue(client().index(new IndexRequest(SearchTemplateIntegrationTest.INDEX, SearchTemplateIntegrationTest.TYPE).source("{\"user\":\"abcdef\",\"num\":3}", JSON).setRefreshPolicy(IMMEDIATE)).actionGet().getResult().equals(CREATED));
        // template includes sort
        SearchResult result = client.execute(new Search.TemplateBuilder(SearchTemplateIntegrationTest.INLINE).build());
        assertTrue(result.getErrorMessage(), result.isSucceeded());
        List<Hit<Object, Void>> hits = result.getHits(Object.class);
        assertEquals(1, hits.size());
        io.searchbox.core.SearchResult.Hit<Object, Void> hit0 = hits.get(0);
        // check user
        assertEquals("kimchy1", ((Map) (hit0.source)).get("user"));
    }

    @Test
    public void searchTemplateInlineScriptWithSort() throws Exception {
        assertTrue(client().index(new IndexRequest(SearchTemplateIntegrationTest.INDEX, SearchTemplateIntegrationTest.TYPE).source("{\"user\":\"kimchy1\",\"num\":1}", JSON).setRefreshPolicy(IMMEDIATE)).actionGet().getResult().equals(CREATED));
        assertTrue(client().index(new IndexRequest(SearchTemplateIntegrationTest.INDEX, SearchTemplateIntegrationTest.TYPE).source("{\"user\":\"kimchy1\",\"num\":0}", JSON).setRefreshPolicy(IMMEDIATE)).actionGet().getResult().equals(CREATED));
        assertTrue(client().index(new IndexRequest(SearchTemplateIntegrationTest.INDEX, SearchTemplateIntegrationTest.TYPE).source("{\"user\":\"\"}", JSON).setRefreshPolicy(IMMEDIATE)).actionGet().getResult().equals(CREATED));
        // template includes sort
        SearchResult result = client.execute(new Search.TemplateBuilder(SearchTemplateIntegrationTest.INLINE).build());
        assertTrue(result.getErrorMessage(), result.isSucceeded());
        List<Hit<Object, Void>> hits = result.getHits(Object.class);
        assertEquals(2, hits.size());
        io.searchbox.core.SearchResult.Hit<Object, Void> hit0 = hits.get(0);
        io.searchbox.core.SearchResult.Hit<Object, Void> hit1 = hits.get(1);
        // check sort
        assertEquals(1, hit0.sort.size());
        assertEquals("0", hit0.sort.get(0));
        assertEquals(1, hit1.sort.size());
        assertEquals("1", hit1.sort.get(0));
        // check user
        assertEquals("kimchy1", ((Map) (hit0.source)).get("user"));
        assertEquals("kimchy1", ((Map) (hit1.source)).get("user"));
    }

    @Test
    public void searchTemplateIdScriptWithSort() throws Exception {
        assertAcked(client().admin().cluster().preparePutStoredScript().setId("templateId").setContent(new BytesArray(SearchTemplateIntegrationTest.SCRIPT), JSON).get());
        assertTrue(client().index(new IndexRequest(SearchTemplateIntegrationTest.INDEX, SearchTemplateIntegrationTest.TYPE).source("{\"user\":\"kimchy1\",\"num\":1}", JSON).setRefreshPolicy(IMMEDIATE)).actionGet().getResult().equals(CREATED));
        assertTrue(client().index(new IndexRequest(SearchTemplateIntegrationTest.INDEX, SearchTemplateIntegrationTest.TYPE).source("{\"user\":\"kimchy1\",\"num\":0}", JSON).setRefreshPolicy(IMMEDIATE)).actionGet().getResult().equals(CREATED));
        assertTrue(client().index(new IndexRequest(SearchTemplateIntegrationTest.INDEX, SearchTemplateIntegrationTest.TYPE).source("{\"user\":\"\"}", JSON).setRefreshPolicy(IMMEDIATE)).actionGet().getResult().equals(CREATED));
        // template includes sort
        SearchResult result = client.execute(new Search.TemplateBuilder(SearchTemplateIntegrationTest.QUERY).build());
        assertTrue(result.getErrorMessage(), result.isSucceeded());
        List<Hit<Object, Void>> hits = result.getHits(Object.class);
        assertEquals(2, hits.size());
        io.searchbox.core.SearchResult.Hit<Object, Void> hit0 = hits.get(0);
        io.searchbox.core.SearchResult.Hit<Object, Void> hit1 = hits.get(1);
        // check sort
        assertEquals(1, hit0.sort.size());
        assertEquals("0", hit0.sort.get(0));
        assertEquals(1, hit1.sort.size());
        assertEquals("1", hit1.sort.get(0));
        // check user
        assertEquals("kimchy1", ((Map) (hit0.source)).get("user"));
        assertEquals("kimchy1", ((Map) (hit1.source)).get("user"));
    }
}

