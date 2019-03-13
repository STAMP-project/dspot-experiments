package io.searchbox.core;


import ElasticsearchVersion.UNKNOWN;
import com.google.gson.Gson;
import java.util.HashMap;
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
public class BulkTest {
    @Test
    public void emptyBulkOperation() {
        executeAsserts(new Bulk.Builder().build());
    }

    @Test
    public void bulkOperationWithIndex() throws JSONException {
        Map source = new HashMap();
        source.put("field", "value");
        Bulk bulk = new Bulk.Builder().addAction(new Index.Builder(source).index("twitter").type("tweet").id("1").build()).build();
        executeAsserts(bulk);
        String expectedData = "{\"index\":{\"_id\":\"1\",\"_index\":\"twitter\",\"_type\":\"tweet\"}}\n" + "{\"field\":\"value\"}";
        JSONAssert.assertEquals(expectedData, bulk.getData(new Gson()).toString(), false);
    }

    @Test
    public void bulkOperationWithSingleDelete() throws JSONException {
        Bulk bulk = new Bulk.Builder().addAction(new Delete.Builder("1").index("twitter").type("tweet").build()).build();
        executeAsserts(bulk);
        String expectedData = "{\"delete\":{\"_id\":\"1\",\"_index\":\"twitter\",\"_type\":\"tweet\"}}\n";
        JSONAssert.assertEquals(expectedData, bulk.getData(new Gson()).toString(), false);
    }

    @Test
    public void bulkOperationWithMultipleIndex() throws JSONException {
        Map source = new HashMap();
        source.put("field", "value");
        Bulk bulk = new Bulk.Builder().addAction(new Index.Builder(source).index("twitter").type("tweet").id("1").build()).addAction(new Index.Builder(source).index("elasticsearch").type("jest").id("2").build()).build();
        executeAsserts(bulk);
        String expectedData = "{\"index\":{\"_id\":\"1\",\"_index\":\"twitter\",\"_type\":\"tweet\"}}\n" + (("{\"field\":\"value\"}\n" + "{\"index\":{\"_id\":\"2\",\"_index\":\"elasticsearch\",\"_type\":\"jest\"}}\n") + "{\"field\":\"value\"}");
        JSONAssert.assertEquals(expectedData, bulk.getData(new Gson()).toString(), false);
    }

    @Test
    public void bulkOperationWithMultipleDelete() throws JSONException {
        Bulk bulk = new Bulk.Builder().addAction(new Delete.Builder("1").index("twitter").type("tweet").build()).addAction(new Delete.Builder("2").index("twitter").type("tweet").build()).build();
        executeAsserts(bulk);
        String expectedData = "{\"delete\":{\"_id\":\"1\",\"_index\":\"twitter\",\"_type\":\"tweet\"}}\n" + "{\"delete\":{\"_id\":\"2\",\"_index\":\"twitter\",\"_type\":\"tweet\"}}";
        JSONAssert.assertEquals(expectedData, bulk.getData(new Gson()).toString(), false);
    }

    @Test
    public void bulkOperationWithMultipleIndexAndDelete() throws JSONException {
        Map source = new HashMap();
        source.put("field", "value");
        Bulk bulk = new Bulk.Builder().addAction(new Index.Builder(source).index("twitter").type("tweet").id("1").build()).addAction(new Index.Builder(source).index("elasticsearch").type("jest").id("2").build()).addAction(new Delete.Builder("1").index("twitter").type("tweet").build()).addAction(new Delete.Builder("2").index("twitter").type("tweet").build()).build();
        executeAsserts(bulk);
        String expectedData = "{\"index\":{\"_id\":\"1\",\"_index\":\"twitter\",\"_type\":\"tweet\"}}\n" + (((("{\"field\":\"value\"}\n" + "{\"index\":{\"_id\":\"2\",\"_index\":\"elasticsearch\",\"_type\":\"jest\"}}\n") + "{\"field\":\"value\"}\n") + "{\"delete\":{\"_id\":\"1\",\"_index\":\"twitter\",\"_type\":\"tweet\"}}\n") + "{\"delete\":{\"_id\":\"2\",\"_index\":\"twitter\",\"_type\":\"tweet\"}}");
        JSONAssert.assertEquals(expectedData, bulk.getData(new Gson()), false);
    }

    @Test
    public void testUris() {
        Bulk bulkWitIndex = new Bulk.Builder().defaultIndex("twitter").build();
        Assert.assertEquals("twitter/_bulk", bulkWitIndex.getURI(UNKNOWN));
        Bulk bulkWitIndexAndType = new Bulk.Builder().defaultIndex("twitter").defaultType("tweet").build();
        Assert.assertEquals("twitter/tweet/_bulk", bulkWitIndexAndType.getURI(UNKNOWN));
    }
}

