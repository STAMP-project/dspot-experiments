package io.searchbox.core;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;


public class BulkResultTest {
    static String indexFailedResult = "{\n" + ((((((((((((("    \"took\": 10,\n" + "    \"errors\": true,\n") + "    \"items\": [\n") + "        {\n") + "            \"index\": {\n") + "                \"_index\": \"index-name\",\n") + "                \"_type\": \"type-name\",\n") + "                \"_id\": null,\n") + "                \"status\": 400,\n") + "                \"error\": \"MapperParsingException[mapping [type-name]]; nested: MapperParsingException[No type specified for property [field-name]]; \"\n") + "            }\n") + "        }\n") + "    ]\n") + "}");

    static String indexSuccessResult = "{\n" + (((((((((((((((((("    \"took\": 36,\n" + "    \"errors\": false,\n") + "    \"items\": [\n") + "        {\n") + "            \"index\": {\n") + "                \"_index\": \"foo\",\n") + "                \"_type\": \"FooBar\",\n") + "                \"_id\": \"12345\",\n") + "                \"status\": 201,\n") + "                \"_version\": 3,\n") + "                \"_shards\": {\n") + "                    \"total\": 1,\n") + "                    \"successful\": 1,\n") + "                    \"failed\": 0\n") + "                }\n") + "            }\n") + "        }\n") + "    ]\n") + "}");

    static String indexFailedResultObject = "{\n" + ((((((((((((((((("    \"took\": 10,\n" + "    \"errors\": true,\n") + "    \"items\": [\n") + "        {\n") + "            \"index\": {\n") + "                \"_index\": \"index-name\",\n") + "                \"_type\": \"type-name\",\n") + "                \"_id\": null,\n") + "                \"status\": 400,\n") + "                \"error\": {\n") + "                    \"type\": \"type_missing_exception\",\n") + "                    \"reason\": \"Reason is missing type\",\n") + "                    \"index\": \"foo\"\n") + "                }\n") + "            }\n") + "        }\n") + "    ]\n") + "}");

    @SuppressWarnings("unchecked")
    @Test
    public void bulkResultWithFailures() {
        BulkResult bulkResult = new BulkResult(new GsonBuilder().serializeNulls().create());
        bulkResult.setJsonString(BulkResultTest.indexFailedResult);
        bulkResult.setJsonMap(new Gson().fromJson(BulkResultTest.indexFailedResult, Map.class));
        bulkResult.setSucceeded(false);
        Assert.assertEquals(1, bulkResult.getItems().size());
        Assert.assertEquals(1, bulkResult.getFailedItems().size());
        Assert.assertNull(bulkResult.getItems().get(0).version);
        Assert.assertEquals("\"MapperParsingException[mapping [type-name]]; nested: MapperParsingException[No type specified for property [field-name]]; \"", bulkResult.getItems().get(0).error);
        Assert.assertNull(bulkResult.getItems().get(0).errorType);
        Assert.assertNull(bulkResult.getItems().get(0).errorReason);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void bulkResultWithSuccess() {
        BulkResult bulkResult = new BulkResult(new GsonBuilder().serializeNulls().create());
        bulkResult.setJsonString(BulkResultTest.indexSuccessResult);
        bulkResult.setJsonMap(new Gson().fromJson(BulkResultTest.indexSuccessResult, Map.class));
        bulkResult.setSucceeded(true);
        Assert.assertEquals(1, bulkResult.getItems().size());
        Assert.assertEquals(0, bulkResult.getFailedItems().size());
        Assert.assertEquals(201, bulkResult.getItems().get(0).status);
        Assert.assertEquals(Integer.valueOf(3), bulkResult.getItems().get(0).version);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void bulkResultWithFailuresObject() {
        BulkResult bulkResult = new BulkResult(new GsonBuilder().serializeNulls().create());
        bulkResult.setJsonString(BulkResultTest.indexFailedResultObject);
        bulkResult.setJsonMap(new Gson().fromJson(BulkResultTest.indexFailedResultObject, Map.class));
        bulkResult.setSucceeded(false);
        Assert.assertEquals(1, bulkResult.getItems().size());
        Assert.assertEquals(1, bulkResult.getFailedItems().size());
        Assert.assertNull(bulkResult.getItems().get(0).version);
        Assert.assertEquals("type_missing_exception", bulkResult.getItems().get(0).errorType);
        Assert.assertEquals("Reason is missing type", bulkResult.getItems().get(0).errorReason);
    }
}

