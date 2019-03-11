package io.searchbox.core;


import ElasticsearchVersion.UNKNOWN;
import Parameters.TRACK_SCORES;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.searchbox.action.AbstractAction;
import io.searchbox.action.Action;
import io.searchbox.core.search.sort.Sort;
import io.searchbox.core.search.sort.Sort.Sorting;
import java.util.Arrays;
import org.json.JSONException;
import org.junit.Assert;
import org.junit.Test;
import org.skyscreamer.jsonassert.JSONAssert;


/**
 *
 *
 * @author Dogukan Sonmez
 * @author Riccardo Tasso
 * @author cihat keser
 */
public class SearchTest {
    Sort sortByPopulationAsc = new Sort("population", Sorting.ASC);

    Sort sortByPopulationDesc = new Sort("population", Sorting.DESC);

    Sort sortByPopulation = new Sort("population");

    @Test
    public void getURIWithoutIndexAndType() {
        Search search = new Search.Builder("").build();
        Assert.assertEquals("_all/_search", search.getURI(UNKNOWN));
    }

    @Test
    public void getURIWithOnlyOneIndex() {
        Search search = new Search.Builder("").addIndex("twitter").build();
        Assert.assertEquals("twitter/_search", search.getURI(UNKNOWN));
    }

    @Test
    public void getURIWithOneIndexAndOneType() {
        Search search = new Search.Builder("").addIndex("twitter").addType("tweet").build();
        Assert.assertEquals("twitter/tweet/_search", search.getURI(UNKNOWN));
    }

    @Test
    public void getURIWithOnlyOneType() {
        Search search = new Search.Builder("").addType("tweet").build();
        Assert.assertEquals("_all/tweet/_search", search.getURI(UNKNOWN));
    }

    @Test
    public void getURIWithOnlyMultipleIndex() {
        Search search = new Search.Builder("").addIndex("twitter").addIndex("searchbox").build();
        Assert.assertEquals("twitter%2Csearchbox/_search", search.getURI(UNKNOWN));
    }

    @Test
    public void getURIWithOnlyMultipleType() {
        Search search = new Search.Builder("").addType("tweet").addType("jest").build();
        Assert.assertEquals("_all/tweet%2Cjest/_search", search.getURI(UNKNOWN));
    }

    @Test
    public void getURIWithMultipleIndexAndTypes() {
        Search search = new Search.Builder("").addIndex("twitter").addIndex("searchbox").addType("tweet").addType("jest").build();
        Assert.assertEquals("twitter%2Csearchbox/tweet%2Cjest/_search", search.getURI(UNKNOWN));
    }

    @Test
    public void getURIForTemplateWithoutIndexAndType() {
        Search search = new Search.TemplateBuilder("").build();
        Assert.assertEquals("_all/_search/template", search.getURI(UNKNOWN));
    }

    @Test
    public void getURIForTemplateWithIndexAndType() {
        Search search = new Search.TemplateBuilder("").addIndex("twitter").addType("tweet").build();
        Assert.assertEquals("twitter/tweet/_search/template", search.getURI(UNKNOWN));
    }

    @Test
    public void getURIWithVersion() {
        Search search = new Search.VersionBuilder("").addIndex("twitter").addType("tweet").build();
        Assert.assertTrue("Version Parameter missing", search.getURI(UNKNOWN).contains("version=true"));
    }

    @Test
    public void sourceFilteringByQueryTest() {
        String query = "{\"sort\":[],\"_source\":{\"exclude\":[\"excludeFieldName\"],\"include\":[\"includeFieldName\"]}}";
        Action search = new Search.Builder(query).build();
        JsonParser parser = new JsonParser();
        JsonElement parsed = parser.parse(search.getData(new Gson()).toString());
        JsonObject obj = parsed.getAsJsonObject();
        JsonObject source = obj.getAsJsonObject("_source");
        JsonArray includePattern = source.getAsJsonArray("include");
        Assert.assertEquals(1, includePattern.size());
        Assert.assertEquals("includeFieldName", includePattern.get(0).getAsString());
        JsonArray excludePattern = source.getAsJsonArray("exclude");
        Assert.assertEquals(1, excludePattern.size());
        Assert.assertEquals("excludeFieldName", excludePattern.get(0).getAsString());
    }

    @Test
    public void sourceFilteringParamTest() {
        String query = "{\"query\" : { \"term\" : { \"name\" : \"KangSungJeon\" } }}";
        String includePatternItem1 = "SeolaIncludeFieldName";
        String includePatternItem2 = "SeohooIncludeFieldName";
        String excludePatternItem1 = "SeolaExcludeField.*";
        String excludePatternItem2 = "SeohooExcludeField.*";
        Action search = new Search.Builder(query).addSourceIncludePattern(includePatternItem1).addSourceIncludePattern(includePatternItem2).addSourceExcludePattern(excludePatternItem1).addSourceExcludePattern(excludePatternItem2).build();
        JsonParser parser = new JsonParser();
        JsonElement parsed = parser.parse(search.getData(new Gson()).toString());
        JsonObject obj = parsed.getAsJsonObject();
        JsonObject source = obj.getAsJsonObject("_source");
        JsonArray includePattern = source.getAsJsonArray("includes");
        Assert.assertEquals(2, includePattern.size());
        Assert.assertEquals(includePatternItem1, includePattern.get(0).getAsString());
        Assert.assertEquals(includePatternItem2, includePattern.get(1).getAsString());
        JsonArray excludePattern = source.getAsJsonArray("excludes");
        Assert.assertEquals(2, excludePattern.size());
        Assert.assertEquals(excludePatternItem1, excludePattern.get(0).getAsString());
        Assert.assertEquals(excludePatternItem2, excludePattern.get(1).getAsString());
    }

    @Test
    public void supportElasticsearchPermissiveSourceFilteringSyntax() {
        String query = "{\"query\" : { \"term\" : { \"name\" : \"KangSungJeon\" } }, \"_source\": false}";
        String includePatternItem1 = "SeolaIncludeFieldName";
        String excludePatternItem1 = "SeolaExcludeField.*";
        Action search = new Search.Builder(query).addSourceIncludePattern(includePatternItem1).addSourceExcludePattern(excludePatternItem1).build();
        JsonParser parser = new JsonParser();
        JsonElement parsed = parser.parse(search.getData(new Gson()).toString());
        JsonObject obj = parsed.getAsJsonObject();
        JsonObject source = obj.getAsJsonObject("_source");
        JsonArray includePattern = source.getAsJsonArray("includes");
        Assert.assertEquals(1, includePattern.size());
        Assert.assertEquals(includePatternItem1, includePattern.get(0).getAsString());
        JsonArray excludePattern = source.getAsJsonArray("excludes");
        Assert.assertEquals(1, excludePattern.size());
        Assert.assertEquals(excludePatternItem1, excludePattern.get(0).getAsString());
        query = "{\"query\" : { \"term\" : { \"name\" : \"KangSungJeon\" } }, \"_source\": [\"includeFieldName1\", \"includeFieldName2\"]}";
        search = new Search.Builder(query).addSourceIncludePattern(includePatternItem1).addSourceExcludePattern(excludePatternItem1).build();
        parsed = parser.parse(search.getData(new Gson()).toString());
        obj = parsed.getAsJsonObject();
        source = obj.getAsJsonObject("_source");
        includePattern = source.getAsJsonArray("includes");
        Assert.assertEquals(3, includePattern.size());
        Assert.assertEquals("includeFieldName1", includePattern.get(0).getAsString());
        Assert.assertEquals("includeFieldName2", includePattern.get(1).getAsString());
        Assert.assertEquals(includePatternItem1, includePattern.get(2).getAsString());
        excludePattern = source.getAsJsonArray("excludes");
        Assert.assertEquals(1, excludePattern.size());
        Assert.assertEquals(excludePatternItem1, excludePattern.get(0).getAsString());
    }

    @Test
    public void sortTest() {
        String query = "{\"query\" : { \"term\" : { \"name\" : \"Milano\" } }}";
        Action search = new Search.Builder(query).addSort(Arrays.asList(sortByPopulationAsc, sortByPopulationDesc, sortByPopulation)).build();
        JsonParser parser = new JsonParser();
        JsonElement parsed = parser.parse(search.getData(new Gson()).toString());
        JsonObject obj = parsed.getAsJsonObject();
        JsonArray sort = obj.getAsJsonArray("sort");
        Assert.assertEquals(3, sort.size());
        // sort 0
        JsonObject test = sort.get(0).getAsJsonObject();
        Assert.assertTrue(test.has("population"));
        test = test.getAsJsonObject("population");
        Assert.assertTrue(test.has("order"));
        Assert.assertEquals("asc", test.get("order").getAsString());
        // sort 1
        test = sort.get(1).getAsJsonObject();
        Assert.assertTrue(test.has("population"));
        test = test.getAsJsonObject("population");
        Assert.assertTrue(test.has("order"));
        Assert.assertEquals("desc", test.get("order").getAsString());
        // sort 2
        test = sort.get(2).getAsJsonObject();
        Assert.assertTrue(test.has("population"));
        test = test.getAsJsonObject("population");
        Assert.assertFalse(test.has("order"));
        Assert.assertFalse(test.has("order"));
        AbstractAction srch = ((AbstractAction) (search));
        Assert.assertTrue(((srch.getParameter(TRACK_SCORES).size()) == 0));
        search = new Search.Builder(query).addSort(Arrays.asList(sortByPopulationAsc, sortByPopulationDesc, sortByPopulation)).enableTrackScores().build();
        srch = ((AbstractAction) (search));
        Assert.assertTrue(((srch.getParameter(TRACK_SCORES).size()) == 1));
        Assert.assertTrue(((Boolean) (srch.getParameter(TRACK_SCORES).iterator().next())));
    }

    @Test
    public void addSortShouldNotOverrideExistingSortDefinitions() throws JSONException {
        JsonArray sortClause = buildSortClause("{\"query\" : { \"term\" : { \"name\" : \"Milano\" } }, \"sort\": [{\"existing\": { \"order\": \"desc\" }}]}", Arrays.asList(sortByPopulationAsc, sortByPopulationDesc));
        Assert.assertNotNull(sortClause);
        Assert.assertEquals(3, sortClause.size());
        JSONAssert.assertEquals("{\"existing\":{\"order\":\"desc\"}}", sortClause.get(0).toString(), false);
        JSONAssert.assertEquals("{\"population\":{\"order\":\"asc\"}}", sortClause.get(1).toString(), false);
        JSONAssert.assertEquals("{\"population\":{\"order\":\"desc\"}}", sortClause.get(2).toString(), false);
    }

    @Test
    public void supportElasticsearchPermissiveSortSyntax() throws JSONException {
        JsonArray sortClause = buildSortClause("{\"query\" : { \"term\" : { \"name\" : \"Milano\" } }, \"sort\": \"existing\"}", Arrays.asList(sortByPopulationAsc));
        Assert.assertNotNull(sortClause);
        Assert.assertEquals(2, sortClause.size());
        Assert.assertEquals("{\"existing\":{\"order\":\"asc\"}}", sortClause.get(0).toString());
        Assert.assertEquals("{\"population\":{\"order\":\"asc\"}}", sortClause.get(1).toString());
        sortClause = buildSortClause("{\"query\" : { \"term\" : { \"name\" : \"Milano\" } }, \"sort\": \"_score\"}", Arrays.asList(sortByPopulationAsc));
        Assert.assertNotNull(sortClause);
        Assert.assertEquals(2, sortClause.size());
        Assert.assertEquals("{\"_score\":{\"order\":\"desc\"}}", sortClause.get(0).toString());
        Assert.assertEquals("{\"population\":{\"order\":\"asc\"}}", sortClause.get(1).toString());
        sortClause = buildSortClause("{\"query\" : { \"term\" : { \"name\" : \"Milano\" } }, \"sort\": { \"existing\": {\"order\":\"desc\"} }}", Arrays.asList(sortByPopulationAsc));
        Assert.assertNotNull(sortClause);
        Assert.assertEquals(2, sortClause.size());
        JSONAssert.assertEquals("{\"existing\":{\"order\":\"desc\"}}", sortClause.get(0).toString(), false);
        JSONAssert.assertEquals("{\"population\":{\"order\":\"asc\"}}", sortClause.get(1).toString(), false);
    }

    @Test
    public void equalsReturnsTrueForSameQueries() {
        Search search1 = new Search.Builder("query1").addIndex("twitter").addType("tweet").build();
        Search search1Duplicate = new Search.Builder("query1").addIndex("twitter").addType("tweet").build();
        Assert.assertEquals(search1, search1Duplicate);
    }

    @Test
    public void equalsReturnsFalseForDifferentQueries() {
        Search search1 = new Search.Builder("query1").addIndex("twitter").addType("tweet").build();
        Search search2 = new Search.Builder("query2").addIndex("twitter").addType("tweet").build();
        Assert.assertNotEquals(search1, search2);
    }

    @Test
    public void equalsReturnsTrueForSameSortList() {
        Search search1 = new Search.Builder("query1").addIndex("twitter").addType("tweet").addSort(Arrays.asList(sortByPopulationAsc, sortByPopulation)).build();
        Search search1Duplicate = new Search.Builder("query1").addIndex("twitter").addType("tweet").addSort(Arrays.asList(sortByPopulationAsc, sortByPopulation)).build();
        Assert.assertEquals(search1, search1Duplicate);
    }

    @Test
    public void equalsReturnsFalseForDifferentSortList() {
        Search search1 = new Search.Builder("query1").addIndex("twitter").addType("tweet").addSort(sortByPopulationAsc).build();
        Search search1Duplicate = new Search.Builder("query1").addIndex("twitter").addType("tweet").addSort(sortByPopulationDesc).build();
        Assert.assertNotEquals(search1, search1Duplicate);
    }
}

