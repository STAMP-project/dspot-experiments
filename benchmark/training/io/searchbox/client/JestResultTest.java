package io.searchbox.client;


import JestResult.ES_METADATA_ID;
import JestResult.ES_METADATA_VERSION;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import io.searchbox.annotations.JestId;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
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
public class JestResultTest {
    JestResult result = new JestResult(new Gson());

    @Test
    public void extractGetResource() {
        String response = "{\n" + (((((((("    \"_index\" : \"twitter\",\n" + "    \"_type\" : \"tweet\",\n") + "    \"_id\" : \"1\", \n") + "    \"_source\" : {\n") + "        \"user\" : \"kimchy\",\n") + "        \"postDate\" : \"2009-11-15T14:12:12\",\n") + "        \"message\" : \"trying out Elastic Search\"\n") + "    }\n") + "}\n");
        result.setJsonMap(new Gson().fromJson(response, Map.class));
        result.setPathToResult("_source");
        Map<String, Object> expectedResultMap = new LinkedHashMap<String, Object>();
        expectedResultMap.put("user", "kimchy");
        expectedResultMap.put("postDate", "2009-11-15T14:12:12");
        expectedResultMap.put("message", "trying out Elastic Search");
        expectedResultMap.put(ES_METADATA_ID, "1");
        JsonObject actualResultMap = result.extractSource().get(0).getAsJsonObject();
        Assert.assertEquals(expectedResultMap.size(), actualResultMap.entrySet().size());
        for (String key : expectedResultMap.keySet()) {
            Assert.assertEquals(expectedResultMap.get(key).toString(), actualResultMap.get(key).getAsString());
        }
    }

    @Test
    public void extractGetResourceWithoutMetadata() {
        String response = "{\n" + (((((((("    \"_index\" : \"twitter\",\n" + "    \"_type\" : \"tweet\",\n") + "    \"_id\" : \"1\", \n") + "    \"_source\" : {\n") + "        \"user\" : \"kimchy\",\n") + "        \"postDate\" : \"2009-11-15T14:12:12\",\n") + "        \"message\" : \"trying out Elastic Search\"\n") + "    }\n") + "}\n");
        result.setJsonMap(new Gson().fromJson(response, Map.class));
        result.setPathToResult("_source");
        Map<String, Object> expectedResultMap = new LinkedHashMap<String, Object>();
        expectedResultMap.put("user", "kimchy");
        expectedResultMap.put("postDate", "2009-11-15T14:12:12");
        expectedResultMap.put("message", "trying out Elastic Search");
        JsonObject actualResultMap = result.extractSource(false).get(0).getAsJsonObject();
        Assert.assertEquals(expectedResultMap.size(), actualResultMap.entrySet().size());
        for (String key : expectedResultMap.keySet()) {
            Assert.assertEquals(expectedResultMap.get(key).toString(), actualResultMap.get(key).getAsString());
        }
    }

    @Test
    public void extractGetResourceWithLongId() {
        Long actualId = (Integer.MAX_VALUE) + 10L;
        String response = ((((((((("{\n" + (("    \"_index\" : \"blog\",\n" + "    \"_type\" : \"comment\",\n") + "    \"_id\" : \"")) + (actualId.toString())) + "\", \n") + "    \"_source\" : {\n") + "        \"someIdName\" : \"") + (actualId.toString())) + "\"\n,") + "        \"message\" : \"trying out Elastic Search\"\n") + "    }\n") + "}\n";
        result.setJsonMap(new Gson().fromJson(response, Map.class));
        result.setPathToResult("_source");
        result.setSucceeded(true);
        JestResultTest.Comment actual = result.getSourceAsObject(JestResultTest.Comment.class);
        Assert.assertNotNull(actual);
        Assert.assertEquals(new Long(((Integer.MAX_VALUE) + 10L)), actual.getSomeIdName());
    }

    @Test
    public void extractGetResourceWithLongIdNotInSource() {
        Long actualId = (Integer.MAX_VALUE) + 10L;
        String response = (((((("{\n" + (("    \"_index\" : \"blog\",\n" + "    \"_type\" : \"comment\",\n") + "    \"_id\" : \"")) + (actualId.toString())) + "\", \n") + "    \"_source\" : {\n") + "        \"message\" : \"trying out Elastic Search\"\n") + "    }\n") + "}\n";
        result.setJsonMap(new Gson().fromJson(response, Map.class));
        result.setPathToResult("_source");
        result.setSucceeded(true);
        JestResultTest.SimpleComment actual = result.getSourceAsObject(JestResultTest.SimpleComment.class);
        Assert.assertNotNull(actual);
        Assert.assertEquals(new Long(((Integer.MAX_VALUE) + 10L)), actual.getSomeIdName());
    }

    @Test
    public void extractUnFoundGetResource() {
        String response = "{\"_index\":\"twitter\",\"_type\":\"tweet\",\"_id\":\"13333\",\"exists\":false}";
        result.setJsonMap(new Gson().fromJson(response, Map.class));
        result.setPathToResult("_source");
        List<JsonElement> resultList = result.extractSource();
        Assert.assertNotNull(resultList);
        Assert.assertEquals(0, resultList.size());
    }

    @Test
    public void getGetSourceAsObject() {
        String response = "{\n" + (((((((("    \"_index\" : \"twitter\",\n" + "    \"_type\" : \"tweet\",\n") + "    \"_id\" : \"1\", \n") + "    \"_source\" : {\n") + "        \"user\" : \"kimchy\",\n") + "        \"postDate\" : \"2009-11-15T14:12:12\",\n") + "        \"message\" : \"trying out Elastic Search\"\n") + "    }\n") + "}\n");
        result.setJsonMap(new Gson().fromJson(response, Map.class));
        result.setPathToResult("_source");
        result.setSucceeded(true);
        JestResultTest.Twitter twitter = result.getSourceAsObject(JestResultTest.Twitter.class);
        Assert.assertNotNull(twitter);
        Assert.assertEquals("kimchy", twitter.getUser());
        Assert.assertEquals("trying out Elastic Search", twitter.getMessage());
        Assert.assertEquals("2009-11-15T14:12:12", twitter.getPostDate());
    }

    @Test
    public void getGetSourceAsObjectWithoutMetadata() {
        String response = "{\n" + (((((((("    \"_index\" : \"twitter\",\n" + "    \"_type\" : \"tweet\",\n") + "    \"_id\" : \"1\", \n") + "    \"_source\" : {\n") + "        \"user\" : \"kimchy\",\n") + "        \"postDate\" : \"2009-11-15T14:12:12\",\n") + "        \"message\" : \"trying out Elastic Search\"\n") + "    }\n") + "}\n");
        result.setJsonMap(new Gson().fromJson(response, Map.class));
        result.setPathToResult("_source");
        result.setSucceeded(true);
        Map twitter = result.getSourceAsObject(Map.class, false);
        Assert.assertNotNull(twitter);
        Assert.assertEquals("kimchy", twitter.get("user"));
        Assert.assertEquals("trying out Elastic Search", twitter.get("message"));
        Assert.assertEquals("2009-11-15T14:12:12", twitter.get("postDate"));
        Assert.assertNull(twitter.get(ES_METADATA_ID));
        Assert.assertNull(twitter.get(ES_METADATA_VERSION));
    }

    @Test
    public void getGetSourceAsString() throws JSONException {
        String response = "{\n" + (((((((("    \"_index\" : \"twitter\",\n" + "    \"_type\" : \"tweet\",\n") + "    \"_id\" : \"1\", \n") + "    \"_source\" : {\n") + "        \"user\" : \"kimchy\",\n") + "        \"postDate\" : \"2009-11-15T14:12:12\",\n") + "        \"message\" : \"trying out Elastic Search\"\n") + "    }\n") + "}\n");
        result.setJsonMap(new Gson().fromJson(response, Map.class));
        result.setPathToResult("_source");
        result.setSucceeded(true);
        String onlySource = "{" + ((("\"user\":\"kimchy\"," + "\"postDate\":\"2009-11-15T14:12:12\",") + "\"message\":\"trying out Elastic Search\"") + "}");
        JSONAssert.assertEquals(onlySource, result.getSourceAsString(), false);
    }

    @Test
    public void getGetSourceAsStringArray() throws JSONException {
        String response = "{\n" + (((((((("    \"_index\" : \"twitter\",\n" + "    \"_type\" : \"tweet\",\n") + "    \"_id\" : \"1\", \n") + "    \"_source\" : [") + "        { \"user\" : \"kimch\" }, ") + "        { \"user\" : \"bello\" },") + "        { \"user\" : \"ionex\" }") + "    ]\n") + "}\n");
        result.setJsonMap(new Gson().fromJson(response, Map.class));
        result.setPathToResult("_source");
        result.setSucceeded(true);
        String onlySource = "[" + ((("{\"user\":\"kimch\"}," + "{\"user\":\"bello\"},") + "{\"user\":\"ionex\"}") + "]");
        JSONAssert.assertEquals(onlySource, result.getSourceAsString(), false);
    }

    @Test
    public void getGetSourceAsStringNoResult() {
        String response = "{\n" + ((("    \"_index\" : \"twitter\",\n" + "    \"_type\" : \"tweet\",\n") + "    \"_id\" : \"1\" \n") + "}\n");
        result.setJsonMap(new Gson().fromJson(response, Map.class));
        result.setPathToResult("_source");
        result.setSucceeded(true);
        Assert.assertNull(result.getSourceAsString());
    }

    @Test
    public void getUnFoundGetResultAsAnObject() {
        String response = "{\"_index\":\"twitter\",\"_type\":\"tweet\",\"_id\":\"13333\",\"exists\":false}";
        result.setJsonMap(new Gson().fromJson(response, Map.class));
        result.setPathToResult("_source");
        Assert.assertNull(result.getSourceAsObject(JestResultTest.Twitter.class));
    }

    @Test
    public void extractUnFoundMultiGetResource() {
        String response = "{\n" + ((((((("\n" + "\"docs\":\n") + "[\n") + "{\"_index\":\"test\",\"_type\":\"type\",\"_id\":\"1\",\"exists\":false},\n") + "{\"_index\":\"test\",\"_type\":\"type\",\"_id\":\"2\",\"exists\":false}\n") + "]\n") + "\n") + "}\n");
        result.setJsonMap(new Gson().fromJson(response, Map.class));
        result.setPathToResult("docs/_source");
        List<Map<String, Object>> expected = new ArrayList<Map<String, Object>>();
        List<JsonElement> actual = result.extractSource();
        Assert.assertEquals(expected.size(), actual.size());
    }

    @Test
    public void extractMultiGetWithSourcePartlyFound() {
        String response = "{\"docs\":" + (((((((("[" + "{\"_index\":\"test\",\"_type\":\"type\",\"_id\":\"2\",\"exists\":false},\n") + "{\"_index\":\"twitter\",\"_type\":\"tweet\",\"_id\":\"2\",\"_version\":2,\"exists\":true, ") + "\"_source\" : {\n") + "    \"user\" : \"kimchy\",\n") + "    \"post_date\" : \"2009-11-15T14:12:12\",\n") + "    \"message\" : \"trying out Elastic Search\"\n") + "}}") + "]}");
        result.setJsonMap(new Gson().fromJson(response, Map.class));
        result.setPathToResult("docs/_source");
        List<Map<String, Object>> expected = new ArrayList<Map<String, Object>>();
        Map<String, Object> expectedMap1 = new LinkedHashMap<String, Object>();
        expectedMap1.put("user", "kimchy");
        expectedMap1.put("post_date", "2009-11-15T14:12:12");
        expectedMap1.put("message", "trying out Elastic Search");
        expected.add(expectedMap1);
        List<JsonElement> actual = result.extractSource();
        Assert.assertEquals(expected.size(), actual.size());
        for (int i = 0; i < (expected.size()); i++) {
            Map<String, Object> expectedMap = expected.get(i);
            JsonObject actualMap = actual.get(i).getAsJsonObject();
            for (String key : expectedMap.keySet()) {
                Assert.assertEquals(expectedMap.get(key).toString(), actualMap.get(key).getAsString());
            }
        }
    }

    @Test
    public void extractMultiGetWithSource() {
        String response = "{\"docs\":" + ((((((((((((("[" + "{\"_index\":\"twitter\",\"_type\":\"tweet\",\"_id\":\"1\",\"_version\":9,\"exists\":true, ") + "\"_source\" : {\n") + "    \"user\" : \"kimchy\",\n") + "    \"post_date\" : \"2009-11-15T14:12:12\",\n") + "    \"message\" : \"trying out Elastic Search\"\n") + "}},") + "{\"_index\":\"twitter\",\"_type\":\"tweet\",\"_id\":\"2\",\"_version\":2,\"exists\":true, ") + "\"_source\" : {\n") + "    \"user\" : \"kimchy\",\n") + "    \"post_date\" : \"2009-11-15T14:12:12\",\n") + "    \"message\" : \"trying out Elastic Search\"\n") + "}}") + "]}");
        result.setJsonMap(new Gson().fromJson(response, Map.class));
        result.setPathToResult("docs/_source");
        List<Map<String, Object>> expected = new ArrayList<Map<String, Object>>();
        Map<String, Object> expectedMap1 = new LinkedHashMap<String, Object>();
        expectedMap1.put("user", "kimchy");
        expectedMap1.put("post_date", "2009-11-15T14:12:12");
        expectedMap1.put("message", "trying out Elastic Search");
        Map<String, Object> expectedMap2 = new LinkedHashMap<String, Object>();
        expectedMap2.put("user", "kimchy");
        expectedMap2.put("post_date", "2009-11-15T14:12:12");
        expectedMap2.put("message", "trying out Elastic Search");
        expected.add(expectedMap1);
        expected.add(expectedMap2);
        List<JsonElement> actual = result.extractSource();
        for (int i = 0; i < (expected.size()); i++) {
            Map<String, Object> expectedMap = expected.get(i);
            JsonObject actualMap = actual.get(i).getAsJsonObject();
            for (String key : expectedMap.keySet()) {
                Assert.assertEquals(expectedMap.get(key).toString(), actualMap.get(key).getAsString());
            }
        }
    }

    @Test
    public void getMultiGetSourceAsObject() {
        String response = "{\"docs\":" + ((((((((((((("[" + "{\"_index\":\"twitter\",\"_type\":\"tweet\",\"_id\":\"1\",\"_version\":9,\"exists\":true, ") + "\"_source\" : {\n") + "    \"user\" : \"kimchy\",\n") + "    \"postDate\" : \"2009-11-15T14:12:12\",\n") + "    \"message\" : \"trying out Elastic Search\"\n") + "}},") + "{\"_index\":\"twitter\",\"_type\":\"tweet\",\"_id\":\"2\",\"_version\":2,\"exists\":true, ") + "\"_source\" : {\n") + "    \"user\" : \"dogukan\",\n") + "    \"postDate\" : \"2012\",\n") + "    \"message\" : \"My message\"\n") + "}}") + "]}");
        result.setJsonMap(new Gson().fromJson(response, Map.class));
        result.setPathToResult("docs/_source");
        result.setSucceeded(true);
        List<JestResultTest.Twitter> twitterList = result.getSourceAsObjectList(JestResultTest.Twitter.class);
        Assert.assertEquals(2, twitterList.size());
        Assert.assertEquals("kimchy", twitterList.get(0).getUser());
        Assert.assertEquals("trying out Elastic Search", twitterList.get(0).getMessage());
        Assert.assertEquals("2009-11-15T14:12:12", twitterList.get(0).getPostDate());
        Assert.assertEquals("dogukan", twitterList.get(1).getUser());
        Assert.assertEquals("My message", twitterList.get(1).getMessage());
        Assert.assertEquals("2012", twitterList.get(1).getPostDate());
    }

    @Test
    public void getUnFoundMultiGetSourceAsObject() {
        String response = "{\n" + ((((((("\n" + "\"docs\":\n") + "[\n") + "{\"_index\":\"test\",\"_type\":\"type\",\"_id\":\"1\",\"exists\":false},\n") + "{\"_index\":\"test\",\"_type\":\"type\",\"_id\":\"2\",\"exists\":false}\n") + "]\n") + "\n") + "}\n");
        result.setJsonMap(new Gson().fromJson(response, Map.class));
        result.setPathToResult("docs/_source");
        result.setSucceeded(true);
        List<JestResultTest.Twitter> twitterList = result.getSourceAsObjectList(JestResultTest.Twitter.class);
        Assert.assertEquals(0, twitterList.size());
    }

    @Test
    public void extractEmptySearchSource() {
        String response = "{\"took\":60,\"timed_out\":false,\"_shards\":{\"total\":1,\"successful\":1," + "\"failed\":0},\"hits\":{\"total\":0,\"max_score\":null,\"hits\":[]}}";
        result.setJsonMap(new Gson().fromJson(response, Map.class));
        result.setPathToResult("hits/hits/_source");
        List<Map<String, Object>> expected = new ArrayList<Map<String, Object>>();
        List<JsonElement> actual = result.extractSource();
        Assert.assertEquals(expected.size(), actual.size());
    }

    @Test
    public void extractSearchSource() {
        String response = "{\n" + ((((((((((((((((((((("    \"_shards\":{\n" + "        \"total\" : 5,\n") + "        \"successful\" : 5,\n") + "        \"failed\" : 0\n") + "    },\n") + "    \"hits\":{\n") + "        \"total\" : 1,\n") + "        \"hits\" : [\n") + "            {\n") + "                \"_index\" : \"twitter\",\n") + "                \"_type\" : \"tweet\",\n") + "                \"_id\" : \"1\", \n") + "                \"_version\" : \"2\", \n") + "                \"_source\" : {\n") + "                    \"user\" : \"kimchy\",\n") + "                    \"postDate\" : \"2009-11-15T14:12:12\",\n") + "                    \"message\" : \"trying out Elastic Search\"\n") + "                }\n") + "            }\n") + "        ]\n") + "    }\n") + "}");
        result.setJsonMap(new Gson().fromJson(response, Map.class));
        result.setPathToResult("hits/hits/_source");
        Map<String, Object> expectedResultMap = new LinkedHashMap<String, Object>();
        expectedResultMap.put("user", "kimchy");
        expectedResultMap.put("postDate", "2009-11-15T14:12:12");
        expectedResultMap.put("message", "trying out Elastic Search");
        JsonObject actualResultMap = result.extractSource().get(0).getAsJsonObject();
        Assert.assertEquals(((expectedResultMap.size()) + 2), actualResultMap.entrySet().size());
        for (String key : expectedResultMap.keySet()) {
            Assert.assertEquals(expectedResultMap.get(key).toString(), actualResultMap.get(key).getAsString());
        }
    }

    @Test
    public void getSearchSourceAsObject() {
        String response = "{\n" + (((((((((((((((((((((((((((((("    \"_shards\":{\n" + "        \"total\" : 5,\n") + "        \"successful\" : 5,\n") + "        \"failed\" : 0\n") + "    },\n") + "    \"hits\":{\n") + "        \"total\" : 1,\n") + "        \"hits\" : [\n") + "            {\n") + "                \"_index\" : \"twitter\",\n") + "                \"_type\" : \"tweet\",\n") + "                \"_id\" : \"1\", \n") + "                \"_source\" : {\n") + "                    \"user\" : \"kimchy\",\n") + "                    \"postDate\" : \"2009-11-15T14:12:12\",\n") + "                    \"message\" : \"trying out Elastic Search\"\n") + "                }\n") + "            },\n") + "            {\n") + "                \"_index\" : \"twitter\",\n") + "                \"_type\" : \"tweet\",\n") + "                \"_id\" : \"1\", \n") + "                \"_source\" : {\n") + "                    \"user\" : \"dogukan\",\n") + "                    \"postDate\" : \"2012\",\n") + "                    \"message\" : \"My Search Result\"\n") + "                }\n") + "            }\n") + "        ]\n") + "    }\n") + "}");
        result.setJsonMap(new Gson().fromJson(response, Map.class));
        result.setPathToResult("hits/hits/_source");
        result.setSucceeded(true);
        List<JestResultTest.Twitter> twitterList = result.getSourceAsObjectList(JestResultTest.Twitter.class);
        Assert.assertEquals(2, twitterList.size());
        Assert.assertEquals("kimchy", twitterList.get(0).getUser());
        Assert.assertEquals("trying out Elastic Search", twitterList.get(0).getMessage());
        Assert.assertEquals("2009-11-15T14:12:12", twitterList.get(0).getPostDate());
        Assert.assertEquals("dogukan", twitterList.get(1).getUser());
        Assert.assertEquals("My Search Result", twitterList.get(1).getMessage());
        Assert.assertEquals("2012", twitterList.get(1).getPostDate());
    }

    @Test
    public void getSearchSourceAsObjectWithoutMetadata() {
        String response = "{\n" + (((((((((((((((((((("    \"_shards\":{\n" + "        \"total\" : 5,\n") + "        \"successful\" : 5,\n") + "        \"failed\" : 0\n") + "    },\n") + "    \"hits\":{\n") + "        \"total\" : 1,\n") + "        \"hits\" : [\n") + "            {\n") + "                \"_index\" : \"twitter\",\n") + "                \"_type\" : \"tweet\",\n") + "                \"_id\" : \"1\", \n") + "                \"_source\" : {\n") + "                    \"user\" : \"kimchy\",\n") + "                    \"postDate\" : \"2009-11-15T14:12:12\",\n") + "                    \"message\" : \"trying out Elastic Search\"\n") + "                }\n") + "            }\n") + "        ]\n") + "    }\n") + "}");
        result.setJsonMap(new Gson().fromJson(response, Map.class));
        result.setPathToResult("hits/hits/_source");
        result.setSucceeded(true);
        List<Map> twitterList = result.getSourceAsObjectList(Map.class, false);
        Assert.assertEquals(1, twitterList.size());
        Assert.assertEquals("kimchy", twitterList.get(0).get("user"));
        Assert.assertEquals("trying out Elastic Search", twitterList.get(0).get("message"));
        Assert.assertEquals("2009-11-15T14:12:12", twitterList.get(0).get("postDate"));
        Assert.assertNull(twitterList.get(0).get(ES_METADATA_ID));
        Assert.assertNull(twitterList.get(0).get(ES_METADATA_VERSION));
    }

    @Test
    public void extractIndexSource() {
        String response = "{\n" + (((("    \"ok\" : true,\n" + "    \"_index\" : \"twitter\",\n") + "    \"_type\" : \"tweet\",\n") + "    \"_id\" : \"1\"\n") + "}\n");
        result.setJsonMap(new Gson().fromJson(response, Map.class));
        List<Map<String, Object>> expected = new ArrayList<Map<String, Object>>();
        Map<String, Object> expectedMap = new LinkedHashMap<String, Object>();
        expectedMap.put("ok", true);
        expectedMap.put("_index", "twitter");
        expectedMap.put("_type", "tweet");
        expectedMap.put("_id", "1");
        expected.add(expectedMap);
        List<JsonElement> actual = result.extractSource();
        for (int i = 0; i < (expected.size()); i++) {
            Map<String, Object> map = expected.get(i);
            JsonObject actualMap = actual.get(i).getAsJsonObject();
            for (String key : map.keySet()) {
                Assert.assertEquals(map.get(key).toString(), actualMap.get(key).getAsString());
            }
        }
    }

    @Test
    public void extractCountResult() {
        String response = "{\n" + (((((("    \"count\" : 1,\n" + "    \"_shards\" : {\n") + "        \"total\" : 5,\n") + "        \"successful\" : 5,\n") + "        \"failed\" : 0\n") + "    }\n") + "}\n");
        result.setJsonMap(new Gson().fromJson(response, Map.class));
        result.setPathToResult("count");
        Double actual = result.extractSource().get(0).getAsDouble();
        Assert.assertEquals(1.0, actual, 0.01);
    }

    @Test
    public void getCountSourceAsObject() {
        String response = "{\n" + (((((("    \"count\" : 1,\n" + "    \"_shards\" : {\n") + "        \"total\" : 5,\n") + "        \"successful\" : 5,\n") + "        \"failed\" : 0\n") + "    }\n") + "}\n");
        result.setJsonMap(new Gson().fromJson(response, Map.class));
        result.setPathToResult("count");
        result.setSucceeded(true);
        Double count = result.getSourceAsObject(Double.class);
        Assert.assertEquals(1.0, count, 0.01);
    }

    @Test
    public void getKeysWithPathToResult() {
        result.setPathToResult("_source");
        String[] expected = new String[]{ "_source" };
        String[] actual = result.getKeys();
        Assert.assertEquals(1, actual.length);
        Assert.assertEquals(expected[0], actual[0]);
    }

    @Test
    public void getKeysWithoutPathToResult() {
        Assert.assertNull(result.getKeys());
    }

    class Twitter {
        String user;

        String postDate;

        String message;

        public String getUser() {
            return user;
        }

        public void setUser(String user) {
            this.user = user;
        }

        public String getPostDate() {
            return postDate;
        }

        public void setPostDate(String postDate) {
            this.postDate = postDate;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }
    }

    abstract class Base {
        @JestId
        Long someIdName;

        public Long getSomeIdName() {
            return someIdName;
        }

        public void setSomeIdName(Long someIdName) {
            this.someIdName = someIdName;
        }
    }

    class Comment extends JestResultTest.Base {
        String message;

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }
    }

    class SimpleComment {
        @JestId
        Long someIdName;

        String message;

        public Long getSomeIdName() {
            return someIdName;
        }

        public void setSomeIdName(Long someIdName) {
            this.someIdName = someIdName;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }
    }
}

