package io.searchbox.core;


import DocWriteResponse.Result.CREATED;
import ESIntegTestCase.ClusterScope;
import ESIntegTestCase.Scope;
import SearchResult.Hit;
import XContentType.JSON;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import io.searchbox.client.JestResult;
import io.searchbox.common.AbstractIntegrationTest;
import java.io.IOException;
import java.util.List;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.junit.Test;
import org.skyscreamer.jsonassert.JSONAssert;


/**
 *
 *
 * @author Dogukan Sonmez
 */
@ClusterScope(scope = Scope.SUITE, numDataNodes = 1)
public class SearchIntegrationTest extends AbstractIntegrationTest {
    private static final String INDEX = "twitter";

    private static final String TYPE = "tweet";

    String query = "{\n" + (((((((((((((("  \"query\": {\n" + "    \"bool\": {\n") + "      \"must\": {\n") + "        \"match\": {\n") + "          \"content\": \"test\"\n") + "        }\n") + "      },\n") + "      \"filter\": {\n") + "        \"term\": {\n") + "          \"user\" : \"kimchy\"\n") + "        }\n") + "      }\n") + "    }\n") + "  }\n") + "}");

    @Test
    public void searchWithValidQuery() throws IOException {
        JestResult result = client.execute(new Search.Builder(query).build());
        assertTrue(result.getErrorMessage(), result.isSucceeded());
    }

    @Test
    public void searchWithNoHits() throws Exception {
        assertAcked(prepareCreate(SearchIntegrationTest.INDEX).addMapping(SearchIntegrationTest.TYPE, "{\"properties\":{\"user\":{\"type\":\"keyword\"}}}", JSON));
        assertTrue(index(SearchIntegrationTest.INDEX, SearchIntegrationTest.TYPE, "swmh1", "{\"user\":\"kimchy1\"}").getResult().equals(CREATED));
        assertTrue(index(SearchIntegrationTest.INDEX, SearchIntegrationTest.TYPE, "swmh2", "{\"user\":\"kimchy2\"}").getResult().equals(CREATED));
        assertTrue(index(SearchIntegrationTest.INDEX, SearchIntegrationTest.TYPE, "swmh3", "{\"user\":\"kimchy3\"}").getResult().equals(CREATED));
        refresh();
        ensureSearchable(SearchIntegrationTest.INDEX);
        String query = "{\n" + ((((("  \"query\": {\n" + "    \"match\": {\n") + "      \"user\": \"musab\"\n") + "    }\n") + "  }\n") + "}");
        SearchResult result = client.execute(new Search.Builder(query).build());
        assertTrue(result.getErrorMessage(), result.isSucceeded());
        assertEquals(null, result.getMaxScore());
    }

    @Test
    public void searchWithMultipleHits() throws Exception {
        assertAcked(prepareCreate(SearchIntegrationTest.INDEX).addMapping(SearchIntegrationTest.TYPE, "{\"properties\":{\"user\":{\"type\":\"keyword\"}}}", JSON));
        assertTrue(index(SearchIntegrationTest.INDEX, SearchIntegrationTest.TYPE, "swmh1", "{\"user\":\"kimchy1\"}").getResult().equals(CREATED));
        assertTrue(index(SearchIntegrationTest.INDEX, SearchIntegrationTest.TYPE, "swmh2", "{\"user\":\"kimchy2\"}").getResult().equals(CREATED));
        assertTrue(index(SearchIntegrationTest.INDEX, SearchIntegrationTest.TYPE, "swmh3", "{\"user\":\"kimchy3\"}").getResult().equals(CREATED));
        refresh();
        ensureSearchable(SearchIntegrationTest.INDEX);
        SearchResult result = client.execute(build());
        assertTrue(result.getErrorMessage(), result.isSucceeded());
        List<Hit<Object, Void>> hits = result.getHits(Object.class);
        assertEquals(3, hits.size());
        assertEquals(hits.get(0).id, "swmh1");
        assertEquals(hits.get(1).id, "swmh2");
        assertEquals(hits.get(2).id, "swmh3");
        JSONAssert.assertEquals(("{\"user\":\"kimchy1\"}," + ("{\"user\":\"kimchy2\"}," + "{\"user\":\"kimchy3\"}")), result.getSourceAsString(), false);
    }

    @Test
    public void searchWithSourceFilterByQuery() throws Exception {
        assertAcked(prepareCreate(SearchIntegrationTest.INDEX).addMapping(SearchIntegrationTest.TYPE, "{\"properties\":{\"includeFieldName\":{\"type\":\"keyword\"}}}", JSON));
        assertTrue(index(SearchIntegrationTest.INDEX, SearchIntegrationTest.TYPE, "Jeehong1", "{\"includeFieldName\":\"SeoHoo\",\"excludeFieldName\":\"SeongJeon\"}").getResult().equals(CREATED));
        assertTrue(index(SearchIntegrationTest.INDEX, SearchIntegrationTest.TYPE, "Jeehong2", "{\"includeFieldName\":\"Seola\",\"excludeFieldName\":\"SeongJeon\"}").getResult().equals(CREATED));
        refresh();
        ensureSearchable(SearchIntegrationTest.INDEX);
        SearchResult result = client.execute(build());
        assertTrue(result.getErrorMessage(), result.isSucceeded());
        List<Hit<Object, Void>> hits = result.getHits(Object.class);
        assertEquals(2, hits.size());
        assertEquals(("{\"includeFieldName\":\"SeoHoo\"}," + "{\"includeFieldName\":\"Seola\"}"), result.getSourceAsString());
    }

    @Test
    public void searchWithSourceFilterByParam() throws Exception {
        assertAcked(prepareCreate(SearchIntegrationTest.INDEX).addMapping(SearchIntegrationTest.TYPE, "{\"properties\":{\"includeFieldName\":{\"type\":\"keyword\"}}}", JSON));
        assertTrue(index(SearchIntegrationTest.INDEX, SearchIntegrationTest.TYPE, "Happyprg1", "{\"includeFieldName\":\"SeoHoo\",\"excludeFieldName\":\"SeongJeon\"}").getResult().equals(CREATED));
        assertTrue(index(SearchIntegrationTest.INDEX, SearchIntegrationTest.TYPE, "Happyprg2", "{\"includeFieldName\":\"Seola\",\"excludeFieldName\":\"SeongJeon\"}").getResult().equals(CREATED));
        refresh();
        ensureSearchable(SearchIntegrationTest.INDEX);
        SearchResult result = client.execute(build());
        assertTrue(result.getErrorMessage(), result.isSucceeded());
        List<Hit<Object, Void>> hits = result.getHits(Object.class);
        assertEquals(2, hits.size());
        assertEquals(("{\"includeFieldName\":\"SeoHoo\"}," + "{\"includeFieldName\":\"Seola\"}"), result.getSourceAsString());
    }

    @Test
    public void searchWithSort() throws Exception {
        assertAcked(prepareCreate(SearchIntegrationTest.INDEX).addMapping(SearchIntegrationTest.TYPE, "{\"properties\":{\"user\":{\"type\":\"keyword\"}}}", JSON));
        assertTrue(index(SearchIntegrationTest.INDEX, SearchIntegrationTest.TYPE, "sws1", "{\"user\":\"kimchy1\"}").getResult().equals(CREATED));
        assertTrue(index(SearchIntegrationTest.INDEX, SearchIntegrationTest.TYPE, "sws2", "{\"user\":\"kimchy2\"}").getResult().equals(CREATED));
        refresh();
        ensureSearchable(SearchIntegrationTest.INDEX);
        Search search = new Search.Builder("").setParameter("sort", "user").build();
        SearchResult result = client.execute(search);
        assertTrue(result.getErrorMessage(), result.isSucceeded());
        List<Hit<Object, Void>> hits = result.getHits(Object.class);
        assertEquals(1, hits.get(0).sort.size());
        assertEquals("kimchy1", hits.get(0).sort.get(0));
        assertEquals(null, hits.get(0).score);
        assertEquals(1, hits.get(1).sort.size());
        assertEquals("kimchy2", hits.get(1).sort.get(0));
        assertEquals(null, hits.get(1).score);
        search = build();
        result = client.execute(search);
        hits = result.getHits(Object.class);
        assertTrue(result.getErrorMessage(), result.isSucceeded());
        assertEquals(1, hits.get(0).sort.size());
        assertEquals("kimchy1", hits.get(0).sort.get(0));
        assertEquals(new Double(1.0), hits.get(0).score);
        assertEquals(1, hits.get(1).sort.size());
        assertEquals("kimchy2", hits.get(1).sort.get(0));
        assertEquals(new Double(1.0), hits.get(1).score);
    }

    @Test
    public void searchWithValidQueryAndExplain() throws IOException {
        assertTrue(index(SearchIntegrationTest.INDEX, SearchIntegrationTest.TYPE, "swvqae1", "{\"user\":\"kimchy\"}").getResult().equals(CREATED));
        refresh();
        ensureSearchable(SearchIntegrationTest.INDEX);
        String queryWithExplain = "{\n" + (((("    \"explain\": true,\n" + "    \"query\" : {\n") + "        \"term\" : { \"user\" : \"kimchy\" }\n") + "    }") + "}");
        SearchResult result = client.execute(new Search.Builder(queryWithExplain).build());
        assertTrue(result.getErrorMessage(), result.isSucceeded());
        JsonArray hits = result.getJsonObject().getAsJsonObject("hits").getAsJsonArray("hits");
        assertEquals(1, hits.size());
        JsonElement explanation = hits.get(0).getAsJsonObject().get("_explanation");
        assertNotNull(explanation);
        assertEquals(new Long(1L), result.getTotal());
    }

    @Test
    public void searchWithQueryBuilder() throws IOException {
        assertTrue(index(SearchIntegrationTest.INDEX, SearchIntegrationTest.TYPE, "swqb1", "{\"user\":\"kimchy\"}").getResult().equals(CREATED));
        refresh();
        ensureSearchable(SearchIntegrationTest.INDEX);
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(QueryBuilders.matchQuery("user", "kimchy"));
        JestResult result = client.execute(build());
        assertTrue(result.getErrorMessage(), result.isSucceeded());
    }

    @Test
    public void searchWithValidTermQuery() throws IOException {
        assertTrue(index(SearchIntegrationTest.INDEX, SearchIntegrationTest.TYPE, "1", "{\"user\":\"kimchy\", \"content\":\"That is test\"}").getResult().equals(CREATED));
        assertTrue(index(SearchIntegrationTest.INDEX, SearchIntegrationTest.TYPE, "2", "{\"user\":\"kimchy\", \"content\":\"That is test\"}").getResult().equals(CREATED));
        refresh();
        ensureSearchable(SearchIntegrationTest.INDEX);
        Search search = new Search.Builder(query).addIndex(SearchIntegrationTest.INDEX).addType(SearchIntegrationTest.TYPE).setParameter(Parameters.SIZE, 1).build();
        SearchResult result = client.execute(search);
        assertTrue(result.getErrorMessage(), result.isSucceeded());
        List<Object> resultList = result.getSourceAsObjectList(Object.class);
        assertEquals(1, resultList.size());
    }

    @Test
    public void searchIndexWithTypeWithNullJestId() throws Exception {
        TestArticleModel article = new TestArticleModel();
        article.setName("Jest");
        Index index = new Index.Builder(article).index("articles").type("article").refresh(true).build();
        client.execute(index);
        Search search = new Search.Builder(("{\n" + ((((("    \"query\":{\n" + "        \"query_string\":{\n") + "            \"query\":\"Jest\"\n") + "        }\n") + "    }\n") + "}"))).setSearchType(SearchType.QUERY_THEN_FETCH).addIndex("articles").addType("article").build();
        JestResult result = client.execute(search);
        List<TestArticleModel> articleResult = result.getSourceAsObjectList(TestArticleModel.class);
        assertNotNull(articleResult.get(0).getId());
    }

    @Test
    public void testWithEncodedURI() throws IOException {
        assertAcked(prepareCreate(SearchIntegrationTest.INDEX).addMapping(SearchIntegrationTest.TYPE, "{\"properties\":{\"user\":{\"type\":\"keyword\"}}}", JSON));
        Index index = new Index.Builder("{\"user\":\"kimchy1\"}").index(SearchIntegrationTest.INDEX).type(SearchIntegrationTest.TYPE).id("test%2f1").refresh(true).build();
        client.execute(index);
        String query = "{\n" + ((((("  \"query\": {\n" + "    \"terms\": {\n") + "      \"_id\": [\"test/1\"]\n") + "    }\n") + "  }\n") + "}");
        SearchResult result = client.execute(new Search.Builder(query).build());
        assertTrue(result.getErrorMessage(), result.isSucceeded());
        assertEquals(Long.valueOf(1), result.getTotal());
    }

    @Test
    public void searchWithPercolator() throws IOException {
        String index = "twitter";
        String type = "tweet";
        String mapping = "{\n" + (((((((("            \"properties\": {\n" + "                \"message\": {\n") + "                    \"type\": \"text\"\n") + "                },\n") + "                \"query\": {\n") + "                    \"type\": \"percolator\"\n") + "                }\n") + "            }\n") + "        }");
        assertAcked(prepareCreate(index).addMapping(type, mapping, JSON));
        String query = "{\n" + ((((("    \"query\" : {\n" + "        \"match\" : {\n") + "            \"message\" : \"bonsai tree\"\n") + "        }\n") + "    }\n") + "}\n");
        assertTrue(index(index, type, "1", query).getResult().equals(CREATED));
        refresh();
        ensureSearchable(index);
        // SearchResult result = client.execute(new Search.Builder(query).addIndex(myIndex).addType(myType).build());
        // assertTrue(result.getErrorMessage(), result.isSucceeded());
        String matchQuery = "{\n" + (((((((((("    \"query\" : {\n" + "        \"percolate\" : {\n") + "            \"field\" : \"query\",\n") + "            \"documents\" : [\n") + "              {\n") + "                \"message\" : \"A new bonsai tree in the office\"\n") + "            }\n") + "        ]\n") + "        }\n") + "    }\n") + "}");
        SearchResult myResult = client.execute(build());
        assertTrue(myResult.getErrorMessage(), myResult.isSucceeded());
        assertEquals(1, myResult.getJsonObject().get("hits").getAsJsonObject().get("total").getAsInt());
    }

    @Test
    public void suggestQuery() throws IOException {
        String index = "twitter";
        String type = "tweet";
        String mapping = "{\n" + ((((("            \"properties\": {\n" + "                \"message\": {\n") + "                    \"type\": \"text\"\n") + "                }\n") + "            }\n") + "        }");
        assertAcked(prepareCreate(index).addMapping(type, mapping, JSON));
        assertTrue(index(index, type, "1", "{\"message\":\"istanbul\"}").getResult().equals(CREATED));
        assertTrue(index(index, type, "2", "{\"message\":\"amsterdam\"}").getResult().equals(CREATED));
        assertTrue(index(index, type, "3", "{\"message\":\"rotterdam\"}").getResult().equals(CREATED));
        assertTrue(index(index, type, "4", "{\"message\":\"vienna\"}").getResult().equals(CREATED));
        assertTrue(index(index, type, "5", "{\"message\":\"london\"}").getResult().equals(CREATED));
        refresh();
        ensureSearchable(SearchIntegrationTest.INDEX);
        String query = "{\n" + ((((((((((((("  \"query\" : {\n" + "    \"match\": {\n") + "      \"message\": \"amsterdam\"\n") + "    }\n") + "  },\n") + "  \"suggest\" : {\n") + "    \"my-suggestion\" : {\n") + "      \"text\" : \"amsterdma\",\n") + "      \"term\" : {\n") + "        \"field\" : \"message\"\n") + "      }\n") + "    }\n") + "  }\n") + "}");
        SearchResult result = client.execute(build());
        assertTrue(result.getErrorMessage(), result.isSucceeded());
        assertEquals("amsterdma", result.getJsonObject().getAsJsonObject("suggest").getAsJsonArray("my-suggestion").get(0).getAsJsonObject().get("text").getAsString());
    }

    @Test
    public void searchInnerHits() throws Exception {
        String field = "comments";
        String mapping = (((((("{\n" + ("      \"properties\": {\n" + "        \"")) + field) + "\": {\n") + "          \"type\": \"nested\"\n") + "        }\n") + "      }\n") + "    }";
        assertAcked(prepareCreate(SearchIntegrationTest.INDEX).addMapping(SearchIntegrationTest.TYPE, mapping, JSON));
        String source = (((((((((((("{\n" + ("  \"title\": \"Test title\",\n" + "  \"")) + field) + "\": [\n") + "    {\n") + "      \"author\": \"ferhats\",\n") + "      \"number\": 1\n") + "    },\n") + "    {\n") + "      \"author\": \"musabg\",\n") + "      \"number\": 2\n") + "    }\n") + "  ]\n") + "}";
        assertTrue(index(SearchIntegrationTest.INDEX, SearchIntegrationTest.TYPE, null, source).getResult().equals(CREATED));
        refresh();
        ensureSearchable(SearchIntegrationTest.INDEX);
        String query = ((((((((((("{\n" + (("  \"query\": {\n" + "    \"nested\": {\n") + "      \"path\": \"")) + field) + "\",\n") + "      \"query\": {\n") + "        \"match\": {\"") + field) + ".number\" : 2}\n") + "      },\n") + "      \"inner_hits\": {} \n") + "    }\n") + "  }\n") + "}";
        SearchResult result = client.execute(new Search.Builder(query).build());
        assertTrue(result.getErrorMessage(), result.isSucceeded());
        JsonObject innerHits = result.getJsonObject().get("hits").getAsJsonObject().get("hits").getAsJsonArray().get(0).getAsJsonObject().get("inner_hits").getAsJsonObject();
        JsonObject innerHitsResult = innerHits.get(field).getAsJsonObject().get("hits").getAsJsonObject().get("hits").getAsJsonArray().get(0).getAsJsonObject();
        assertEquals("musabg", innerHitsResult.get("_source").getAsJsonObject().get("author").getAsString());
    }

    @Test
    public void searchAndGetFirstHitWithSource() throws IOException {
        searchAndGetFirstHit(true);
    }

    @Test
    public void searchAndGetFirstHitWithoutSource() throws IOException {
        searchAndGetFirstHit(false);
    }
}

