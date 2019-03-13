package io.searchbox.indices.reindex;


import ElasticsearchVersion.UNKNOWN;
import com.google.common.collect.ImmutableMap;
import com.google.gson.GsonBuilder;
import org.junit.Assert;
import org.junit.Test;
import org.skyscreamer.jsonassert.JSONAssert;


public class ReindexTest {
    private ImmutableMap<String, Object> source = ImmutableMap.<String, Object>of("index", "sourceIndex");

    private ImmutableMap<String, Object> dest = ImmutableMap.<String, Object>of("index", "destIndex");

    @Test
    public void testBasicUriGeneration() {
        Reindex reindex = new Reindex.Builder(source, dest).build();
        Assert.assertEquals("POST", reindex.getRestMethodName());
        Assert.assertEquals("/_reindex", reindex.getURI(UNKNOWN));
    }

    @Test
    public void testQueryStringGeneration() throws Exception {
        Reindex reindex = new Reindex.Builder(source, dest).waitForCompletion(true).waitForActiveShards(1).timeout(5000L).requestsPerSecond(5.0).build();
        String generatedURI = reindex.getURI(UNKNOWN);
        String expectedURI = "/_reindex?wait_for_completion=true&wait_for_active_shards=1&timeout=5000&requests_per_second=5.0";
        Assert.assertEquals(expectedURI, generatedURI);
    }

    @Test
    public void testDataGeneration() throws Exception {
        Reindex reindex = new Reindex.Builder(source, dest).conflicts("proceed").build();
        String generatedData = reindex.getData(new GsonBuilder().create());
        String expectedData = "{\"conflicts\":\"proceed\",\"dest\":{\"index\":\"destIndex\"},\"source\":{\"index\":\"sourceIndex\"}}";
        JSONAssert.assertEquals(expectedData, generatedData, false);
    }

    @Test
    public void equalsReturnsTrueForSameMappings() {
        Reindex reindex1 = new Reindex.Builder(source, dest).conflicts("proceed").build();
        Reindex reindex2 = new Reindex.Builder(source, dest).conflicts("proceed").build();
        Assert.assertEquals(reindex1, reindex2);
    }

    @Test
    public void equalsReturnsFalseForDifferentMappings() {
        Reindex reindex1 = new Reindex.Builder(source, dest).waitForCompletion(true).build();
        Reindex reindex2 = new Reindex.Builder(source, dest).waitForCompletion(false).build();
        Assert.assertNotEquals(reindex1, reindex2);
    }
}

