package io.searchbox.core;


import ElasticsearchVersion.UNKNOWN;
import com.google.gson.Gson;
import com.google.gson.JsonParser;
import java.util.Map;
import org.json.JSONException;
import org.junit.Assert;
import org.junit.Test;
import org.skyscreamer.jsonassert.JSONAssert;


/**
 *
 *
 * @author Dogukan Sonmez
 */
public class MultiSearchTest {
    @Test
    public void multiSearchHasCorrectContentType() throws JSONException {
        Search search = new Search.Builder("").build();
        MultiSearch multiSearch = build();
        Assert.assertEquals("POST", multiSearch.getRestMethodName());
        Assert.assertEquals("/_msearch", multiSearch.getURI(UNKNOWN));
        Assert.assertEquals("application/x-ndjson", multiSearch.getHeader("Content-Type"));
    }

    @Test
    public void singleMultiSearchWithoutIndex() throws JSONException {
        String expectedData = " {\"index\" : \"_all\"}\n" + "{\"query\" : {\"match_all\" : {}}}\n";
        Search search = new Search.Builder("{\"query\" : {\"match_all\" : {}}}").build();
        MultiSearch multiSearch = build();
        Assert.assertEquals("POST", multiSearch.getRestMethodName());
        Assert.assertEquals("/_msearch", multiSearch.getURI(UNKNOWN));
        JSONAssert.assertEquals(expectedData, multiSearch.getData(null).toString(), false);
    }

    @Test
    public void singleMultiSearchWithIndex() throws JSONException {
        String expectedData = " {\"index\" : \"twitter\"}\n" + "{\"query\" : {\"match_all\" : {}}}\n";
        Search search = new Search.Builder("{\"query\" : {\"match_all\" : {}}}").addIndex("twitter").build();
        MultiSearch multiSearch = build();
        Assert.assertEquals("POST", multiSearch.getRestMethodName());
        Assert.assertEquals("/_msearch", multiSearch.getURI(UNKNOWN));
        JSONAssert.assertEquals(expectedData, multiSearch.getData(null).toString(), false);
    }

    @Test
    public void multiSearchWithIndex() throws JSONException {
        String expectedData = " {\"index\" : \"twitter\"}\n" + (("{\"query\" : {\"match_all\" : {}}}\n" + "{\"index\" : \"_all\"}\n") + "{\"query\" : {\"match_all\" : {}}}\n");
        Search search = new Search.Builder("{\"query\" : {\"match_all\" : {}}}").addIndex("twitter").build();
        Search search2 = new Search.Builder("{\"query\" : {\"match_all\" : {}}}").build();
        MultiSearch multiSearch = build();
        Assert.assertEquals("POST", multiSearch.getRestMethodName());
        Assert.assertEquals("/_msearch", multiSearch.getURI(UNKNOWN));
        JSONAssert.assertEquals(expectedData, multiSearch.getData(null).toString(), false);
    }

    @Test
    public void multiSearchWithExtraParameters() throws JSONException {
        String expectedData = " {\"index\" : \"twitter\", " + ((((("\"search_type\" : \"query_then_fetch\"," + " \"routing\" : \"testRoute\",") + " \"ignore_unavailable\" : \"true\",") + " \"allow_no_indices\" : \"true\", ") + "\"expand_wildcards\" : \"true\"}\n") + "{\"query\" : {\"match_all\" : {}}}\n");
        Search search = new Search.Builder("{\"query\" : {\"match_all\" : {}}}").addIndex("twitter").setParameter(Parameters.ROUTING, "testRoute").setSearchType(SearchType.QUERY_THEN_FETCH).setParameter(Parameters.IGNORE_UNAVAILABLE, true).setParameter(Parameters.ALLOW_NO_INDICES, true).setParameter(Parameters.EXPAND_WILDCARDS, true).build();
        Search search2 = new Search.Builder("{\"query\" : {\"match_all\" : {}}}").build();
        MultiSearch multiSearch = build();
        Assert.assertEquals("POST", multiSearch.getRestMethodName());
        Assert.assertEquals("/_msearch", multiSearch.getURI(UNKNOWN));
        JSONAssert.assertEquals(expectedData, multiSearch.getData(null), false);
    }

    @Test
    public void equals() {
        Search search1 = new Search.Builder("{\"match_all\" : {}}").addIndex("twitter").build();
        Search search2 = new Search.Builder("{\"match_all\" : {}}").build();
        MultiSearch multiSearch1 = build();
        MultiSearch multiSearch1Duplicate = build();
        Assert.assertEquals(multiSearch1, multiSearch1Duplicate);
    }

    @Test
    public void equalsReturnsFalseForDifferentSearches() {
        Search search1 = new Search.Builder("{\"match_all\" : {}}").addIndex("twitter").build();
        Search search2 = new Search.Builder("{\"match_all\" : {}}").build();
        MultiSearch multiSearch1 = build();
        MultiSearch multiSearch1Duplicate = build();
        Assert.assertNotEquals(multiSearch1, multiSearch1Duplicate);
    }

    @Test
    public void multiSearchResponse() {
        String json = "{\n" + (((((((((((((((((((((((((((((((((((((((((((("   \"responses\": [\n" + "      {\n") + "          \"_shards\":{\n") + "              \"total\" : 5,\n") + "              \"successful\" : 5,\n") + "              \"failed\" : 0\n") + "          },\n") + "          \"hits\":{\n") + "              \"total\" : 1,\n") + "              \"hits\" : [\n") + "                  {\n") + "                      \"_index\" : \"twitter\",\n") + "                      \"_type\" : \"tweet\",\n") + "                      \"_id\" : \"1\",\n") + "                      \"_source\" : {\n") + "                          \"user\" : \"kimchy\",\n") + "                          \"postDate\" : \"2009-11-15T14:12:12\",\n") + "                          \"message\" : \"trying out Elasticsearch\"\n") + "                      },\n") + "                      \"sort\" : [\n") + "                           1234.5678\n") + "                      ]\n") + "                  }\n") + "              ]\n") + "          }\n") + "      },\n") + "      {\n") + "          \"_shards\":{\n") + "              \"total\" : 5,\n") + "              \"successful\" : 5,\n") + "              \"failed\" : 0\n") + "          },\n") + "          \"hits\":{\n") + "              \"total\" : 0,\n") + "              \"hits\" : [ ]\n") + "          }\n") + "      },\n") + "      {\n") + "        \"error\" : {\n") + "          \"type\" : \"search_phase_execution_exception\",\n") + "          \"reason\" : \"There was a \\\"test\\\" error\"\n") + "        }\n") + "      }\n") + "  ]\n") + "}");
        MultiSearchResult multiSearchResult = new MultiSearchResult(new Gson());
        multiSearchResult.setSucceeded(true);
        multiSearchResult.setJsonString(json);
        multiSearchResult.setJsonObject(new JsonParser().parse(json).getAsJsonObject());
        Assert.assertNotNull(multiSearchResult.getResponses());
        Assert.assertEquals(3, multiSearchResult.getResponses().size());
        Assert.assertFalse(multiSearchResult.getResponses().get(0).isError);
        Assert.assertNull(multiSearchResult.getResponses().get(0).errorMessage);
        Assert.assertNull(multiSearchResult.getResponses().get(0).searchResult.getMaxScore());
        Assert.assertEquals(1, multiSearchResult.getResponses().get(0).searchResult.getHits(Map.class).size());
        Assert.assertFalse(multiSearchResult.getResponses().get(1).isError);
        Assert.assertNull(multiSearchResult.getResponses().get(1).errorMessage);
        Assert.assertNull(multiSearchResult.getResponses().get(1).searchResult.getMaxScore());
        Assert.assertEquals(0, multiSearchResult.getResponses().get(1).searchResult.getHits(Map.class).size());
        Assert.assertTrue(multiSearchResult.getResponses().get(2).isError);
        Assert.assertEquals("There was a \"test\" error", multiSearchResult.getResponses().get(2).errorMessage);
        Assert.assertNull(multiSearchResult.getResponses().get(2).searchResult);
    }
}

