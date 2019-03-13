package com.jayway.jsonpath.old;


import Option.AS_PATH_LIST;
import Option.DEFAULT_PATH_LEAF_TO_NULL;
import Option.SUPPRESS_EXCEPTIONS;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.gson.JsonObject;
import com.jayway.jsonpath.BaseTest;
import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.Criteria;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.Filter;
import com.jayway.jsonpath.InvalidPathException;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.PathNotFoundException;
import com.jayway.jsonpath.internal.Utils;
import com.jayway.jsonpath.spi.cache.LRUCache;
import com.jayway.jsonpath.spi.json.GsonJsonProvider;
import com.jayway.jsonpath.spi.json.JacksonJsonNodeJsonProvider;
import com.jayway.jsonpath.spi.json.JsonProvider;
import com.jayway.jsonpath.spi.mapper.GsonMappingProvider;
import com.jayway.jsonpath.spi.mapper.JacksonMappingProvider;
import java.io.InputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.minidev.json.JSONAware;
import net.minidev.json.parser.JSONParser;
import org.assertj.core.api.Assertions;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;

import static junit.framework.Assert.assertNull;
import static junit.framework.Assert.assertTrue;


public class IssuesTest extends BaseTest {
    private static final JsonProvider jp = Configuration.defaultConfiguration().jsonProvider();

    @Test
    public void issue_143() {
        String json = "{ \"foo\": { \"bar\" : \"val\" }, \"moo\": { \"cow\" : \"val\" } }";
        Configuration configuration = Configuration.builder().options(AS_PATH_LIST).build();
        List<String> pathList = JsonPath.using(configuration).parse(json).read(JsonPath.compile("$.*.bar"));
        Assert.assertThat(pathList).containsExactly("$['foo']['bar']");
    }

    @Test
    public void issue_114_a() {
        String json = "{ \"p\":{\n" + (("\"s\": { \"u\": \"su\" }, \n" + "\"t\": { \"u\": \"tu\" }\n") + "}}");
        List<String> result = JsonPath.read(json, "$.p.['s', 't'].u");
        Assert.assertThat(result).containsExactly("su", "tu");
    }

    @Test
    public void issue_114_b() {
        String json = "{ \"p\": [\"valp\", \"valq\", \"valr\"] }";
        List<String> result = JsonPath.read(json, "$.p[?(@ == 'valp')]");
        Assert.assertThat(result).containsExactly("valp");
    }

    @Test
    public void issue_114_c() {
        String json = "{ \"p\": [\"valp\", \"valq\", \"valr\"] }";
        List<String> result = JsonPath.read(json, "$.p[?(@[0] == 'valp')]");
        Assert.assertThat(result).isEmpty();
    }

    @Test(expected = InvalidPathException.class)
    public void issue_114_d() {
        JsonPath.read(BaseTest.JSON_BOOK_DOCUMENT, "$..book[(@.length-1)] ");
    }

    @Test
    public void issue_151() {
        String json = "{\n" + (((((((((((((((((((((("\"datas\": {\n" + "    \"selling\": {\n") + "        \"3\": [\n") + "            26452067,\n") + "            31625950\n") + "        ],\n") + "        \"206\": [\n") + "            32381852,\n") + "            32489262\n") + "        ],\n") + "        \"208\": [\n") + "            458\n") + "        ],\n") + "        \"217\": [\n") + "            27364892\n") + "        ],\n") + "        \"226\": [\n") + "            30474109\n") + "        ]\n") + "    }\n") + "},\n") + "\"status\": 0\n") + "}");
        List<Integer> result = JsonPath.read(json, "$.datas.selling['3','206'].*");
        Assert.assertThat(result).containsExactly(26452067, 31625950, 32381852, 32489262);
    }

    @Test
    public void full_ones_can_be_filtered() {
        String json = "[\n" + ((" {\"kind\" : \"full\"},\n" + " {\"kind\" : \"empty\"}\n") + "]");
        List<Map<String, String>> fullOnes = JsonPath.read(json, "$[?(@.kind == 'full')]");
        Assert.assertEquals(1, fullOnes.size());
        Assert.assertEquals("full", fullOnes.get(0).get("kind"));
    }

    @Test
    public void issue_36() {
        String json = "{\n" + ((((("\n" + " \"arrayOfObjectsAndArrays\" : [ { \"k\" : [\"json\"] }, { \"k\":[\"path\"] }, { \"k\" : [\"is\"] }, { \"k\" : [\"cool\"] } ],\n") + "\n") + "  \"arrayOfObjects\" : [{\"k\" : \"json\"}, {\"k\":\"path\"}, {\"k\" : \"is\"}, {\"k\" : \"cool\"}]\n") + "\n") + " }");
        Object o1 = JsonPath.read(json, "$.arrayOfObjectsAndArrays..k ");
        Object o2 = JsonPath.read(json, "$.arrayOfObjects..k ");
        Assert.assertEquals("[[\"json\"],[\"path\"],[\"is\"],[\"cool\"]]", IssuesTest.jp.toJson(o1));
        Assert.assertEquals("[\"json\",\"path\",\"is\",\"cool\"]", IssuesTest.jp.toJson(o2));
    }

    @Test
    public void issue_11() throws Exception {
        String json = "{ \"foo\" : [] }";
        List<String> result = JsonPath.read(json, "$.foo[?(@.rel == 'item')][0].uri");
        assertTrue(result.isEmpty());
    }

    @Test(expected = PathNotFoundException.class)
    public void issue_11b() throws Exception {
        String json = "{ \"foo\" : [] }";
        JsonPath.read(json, "$.foo[0].uri");
    }

    @Test
    public void issue_15() throws Exception {
        String json = "{ \"store\": {\n" + ((((((((((((((((((((((((("    \"book\": [ \n" + "      { \"category\": \"reference\",\n") + "        \"author\": \"Nigel Rees\",\n") + "        \"title\": \"Sayings of the Century\",\n") + "        \"price\": 8.95\n") + "      },\n") + "      { \"category\": \"fiction\",\n") + "        \"author\": \"Herman Melville\",\n") + "        \"title\": \"Moby Dick\",\n") + "        \"isbn\": \"0-553-21311-3\",\n") + "        \"price\": 8.99,\n") + "        \"retailer\": null, \n") + "        \"children\": true,\n") + "        \"number\": -2.99\n") + "      },\n") + "      { \"category\": \"fiction\",\n") + "        \"author\": \"J. R. R. Tolkien\",\n") + "        \"title\": \"The Lord of the Rings\",\n") + "        \"isbn\": \"0-395-19395-8\",\n") + "        \"price\": 22.99,\n") + "        \"number\":0,\n") + "        \"children\": false\n") + "      }\n") + "    ]\n") + "  }\n") + "}");
        List<String> titles = JsonPath.read(json, "$.store.book[?(@.children==true)].title");
        Assert.assertThat(titles, Matchers.contains("Moby Dick"));
        Assert.assertEquals(1, titles.size());
    }

    @Test
    public void issue_24() {
        InputStream is = null;
        try {
            is = this.getClass().getResourceAsStream("/issue_24.json");
            // Object o = JsonPath.read(is, "$.project[?(@.template.@key == 'foo')].field[*].@key");
            Object o = JsonPath.read(is, "$.project.field[*].@key");
            // Object o = JsonPath.read(is, "$.project.template[?(@.@key == 'foo')].field[*].@key");
            is.close();
        } catch (Exception e) {
            // e.printStackTrace();
            Utils.closeQuietly(is);
        }
    }

    @Test
    public void issue_28_string() {
        String json = "{\"contents\": [\"one\",\"two\",\"three\"]}";
        List<String> result = JsonPath.read(json, "$.contents[?(@  == 'two')]");
        Assert.assertThat(result, Matchers.contains("two"));
        Assert.assertEquals(1, result.size());
    }

    @Test
    public void issue_37() {
        String json = "[\n" + ((((((((((((((("    {\n" + "        \"id\": \"9\",\n") + "        \"sku\": \"SKU-001\",\n") + "        \"compatible\": false\n") + "    },\n") + "    {\n") + "        \"id\": \"13\",\n") + "        \"sku\": \"SKU-005\",\n") + "        \"compatible\": true\n") + "    },\n") + "    {\n") + "        \"id\": \"11\",\n") + "        \"sku\": \"SKU-003\",\n") + "        \"compatible\": true\n") + "    }\n") + "]");
        List<String> result = JsonPath.read(json, "$[?(@.compatible == true)].sku");
        Assert.assertThat(result).containsExactly("SKU-005", "SKU-003");
    }

    @Test
    public void issue_38() {
        String json = "{\n" + (((((((((("   \"datapoints\":[\n" + "      [\n") + "         10.1,\n") + "         13.0\n") + "      ],\n") + "      [\n") + "         21.0,\n") + "         22.0\n") + "      ]\n") + "   ]\n") + "}");
        List<Double> result = JsonPath.read(json, "$.datapoints.[*].[0]");
        Assert.assertThat(result.get(0), Matchers.is(new Double(10.1)));
        Assert.assertThat(result.get(1), Matchers.is(new Double(21.0)));
    }

    @Test
    public void issue_39() {
        String json = "{\n" + (((((("    \"obj1\": {\n" + "        \"arr\": [\"1\", \"2\"]\n") + "    },\n") + "    \"obj2\": {\n") + "       \"arr\": [\"3\", \"4\"]\n") + "    }\n") + "}\n");
        List<String> result = JsonPath.read(json, "$..arr");
        Assert.assertThat(result.size(), Matchers.is(2));
    }

    @Test
    public void issue_28_int() {
        String json = "{\"contents\": [1,2,3]}";
        List<Integer> result = JsonPath.read(json, "$.contents[?(@ == 2)]");
        Assert.assertThat(result, Matchers.contains(2));
        Assert.assertEquals(1, result.size());
    }

    @Test
    public void issue_28_boolean() {
        String json = "{\"contents\": [true, true, false]}";
        List<Boolean> result = JsonPath.read(json, "$.contents[?(@  == true)]");
        Assert.assertThat(result, Matchers.contains(true, true));
        Assert.assertEquals(2, result.size());
    }

    @Test(expected = PathNotFoundException.class)
    public void issue_22() throws Exception {
        Configuration configuration = Configuration.defaultConfiguration();
        String json = "{\"a\":{\"b\":1,\"c\":2}}";
        JsonPath.parse(json, configuration).read("a.d");
    }

    @Test
    public void issue_22c() throws Exception {
        // Configuration configuration = Configuration.builder().build();
        Configuration configuration = Configuration.builder().options(SUPPRESS_EXCEPTIONS).build();
        String json = "{\"a\":{\"b\":1,\"c\":2}}";
        assertNull(JsonPath.parse(json, configuration).read("a.d"));
    }

    @Test
    public void issue_22b() throws Exception {
        String json = "{\"a\":[{\"b\":1,\"c\":2},{\"b\":5,\"c\":2}]}";
        List<Object> res = JsonPath.using(Configuration.defaultConfiguration().setOptions(DEFAULT_PATH_LEAF_TO_NULL)).parse(json).read("a[?(@.b==5)].d");
        Assert.assertThat(res).hasSize(1).containsNull();
    }

    @Test(expected = PathNotFoundException.class)
    public void issue_26() throws Exception {
        String json = "[{\"a\":[{\"b\":1,\"c\":2}]}]";
        Object o = JsonPath.read(json, "$.a");
    }

    @Test
    public void issue_29_a() throws Exception {
        String json = "{\"list\": [ { \"a\":\"atext\", \"b.b-a\":\"batext2\", \"b\":{ \"b-a\":\"batext\", \"b-b\":\"bbtext\" } }, { \"a\":\"atext2\", \"b\":{ \"b-a\":\"batext2\", \"b-b\":\"bbtext2\" } } ] }";
        List<Map<String, Object>> result = JsonPath.read(json, "$.list[?(@['b.b-a']=='batext2')]");
        Assert.assertEquals(1, result.size());
        Object a = result.get(0).get("a");
        Assert.assertEquals("atext", a);
        result = JsonPath.read(json, "$.list[?(@.b.b-a=='batext2')]");
        Assert.assertEquals(1, result.size());
        Assert.assertEquals("atext2", result.get(0).get("a"));
    }

    @Test
    public void issue_29_b() throws Exception {
        String json = "{\"list\": [ { \"a\":\"atext\", \"b\":{ \"b-a\":\"batext\", \"b-b\":\"bbtext\" } }, { \"a\":\"atext2\", \"b\":{ \"b-a\":\"batext2\", \"b-b\":\"bbtext2\" } } ] }";
        List<String> result = JsonPath.read(json, "$.list[?]", Filter.filter(Criteria.where("b.b-a").eq("batext2")));
        assertTrue(((result.size()) == 1));
    }

    @Test
    public void issue_30() throws Exception {
        String json = "{\"foo\" : {\"@id\" : \"123\", \"$\" : \"hello\"}}";
        Assert.assertEquals("123", JsonPath.read(json, "foo.@id"));
        Assert.assertEquals("hello", JsonPath.read(json, "foo.$"));
    }

    @Test
    public void issue_32() {
        String json = "{\"text\" : \"skill: \\\"Heuristic Evaluation\\\"\", \"country\" : \"\"}";
        Assert.assertEquals("skill: \"Heuristic Evaluation\"", JsonPath.read(json, "$.text"));
    }

    @Test
    public void issue_33() {
        String json = "{ \"store\": {\n" + ((((((((((((((((((((("    \"book\": [ \n" + "      { \"category\": \"reference\",\n") + "        \"author\": {\n") + "          \"name\": \"Author Name\",\n") + "          \"age\": 36\n") + "        },\n") + "        \"title\": \"Sayings of the Century\",\n") + "        \"price\": 8.95\n") + "      },\n") + "      { \"category\": \"fiction\",\n") + "        \"author\": \"Evelyn Waugh\",\n") + "        \"title\": \"Sword of Honour\",\n") + "        \"price\": 12.99,\n") + "        \"isbn\": \"0-553-21311-3\"\n") + "      }\n") + "    ],\n") + "    \"bicycle\": {\n") + "      \"color\": \"red\",\n") + "      \"price\": 19.95\n") + "    }\n") + "  }\n") + "}");
        List<Map<String, Object>> result = JsonPath.read(json, "$.store.book[?(@.author.age == 36)]");
        Assert.assertThat(result).hasSize(1);
        Assert.assertThat(result.get(0)).containsEntry("title", "Sayings of the Century");
    }

    @Test
    public void array_root() {
        String json = "[\n" + ((((("    {\n" + "        \"a\": 1,\n") + "        \"b\": 2,\n") + "        \"c\": 3\n") + "    }\n") + "]");
        Assert.assertEquals(Integer.valueOf(1), JsonPath.read(json, "$[0].a"));
    }

    @Test(expected = PathNotFoundException.class)
    public void a_test() {
        String json = "{\n" + (((((((("  \"success\": true,\n" + "  \"data\": {\n") + "    \"user\": 3,\n") + "    \"own\": null,\n") + "    \"passes\": null,\n") + "    \"completed\": null\n") + "  },\n") + "  \"version\": 1371160528774\n") + "}");
        Object read = JsonPath.read(json, "$.data.passes[0].id");
    }

    @Test
    public void issue_42() {
        String json = "{" + ((("        \"list\": [{" + "            \"name\": \"My (String)\" ") + "        }] ") + "    }");
        List<Map<String, String>> result = JsonPath.read(json, "$.list[?(@.name == 'My (String)')]");
        Assert.assertThat(result).containsExactly(Collections.singletonMap("name", "My (String)"));
    }

    @Test
    public void issue_43() {
        String json = "{\"test\":null}";
        Assert.assertThat(((String) (JsonPath.read(json, "test")))).isNull();
        Assert.assertThat(((String) (JsonPath.using(Configuration.defaultConfiguration().setOptions(SUPPRESS_EXCEPTIONS)).parse(json).read("nonExistingProperty")))).isNull();
        try {
            JsonPath.read(json, "nonExistingProperty");
            failBecauseExceptionWasNotThrown(PathNotFoundException.class);
        } catch (PathNotFoundException e) {
        }
        try {
            JsonPath.read(json, "nonExisting.property");
            failBecauseExceptionWasNotThrown(PathNotFoundException.class);
        } catch (PathNotFoundException e) {
        }
    }

    @Test
    public void issue_45() {
        String json = "{\"rootkey\":{\"sub.key\":\"value\"}}";
        Assert.assertThat(((String) (JsonPath.read(json, "rootkey['sub.key']")))).isEqualTo("value");
    }

    @Test
    public void issue_46() {
        String json = "{\"a\": {}}";
        Configuration configuration = Configuration.defaultConfiguration().setOptions(SUPPRESS_EXCEPTIONS);
        Assert.assertThat(((String) (JsonPath.using(configuration).parse(json).read("a.x")))).isNull();
        try {
            JsonPath.read(json, "a.x");
            failBecauseExceptionWasNotThrown(PathNotFoundException.class);
        } catch (PathNotFoundException e) {
            Assert.assertThat(e).hasMessage("No results for path: $['a']['x']");
        }
    }

    @Test
    public void issue_x() {
        String json = "{\n" + ((((" \"a\" : [\n" + "   {},\n") + "   { \"b\" : [ { \"c\" : \"foo\"} ] }\n") + " ]\n") + "}\n");
        List<String> result = JsonPath.read(json, "$.a.*.b.*.c");
        Assert.assertThat(result).containsExactly("foo");
    }

    @Test
    public void issue_60() {
        String json = "[\n" + ((((((((((((((((((((((((((((((((((((((((((("{\n" + "  \"mpTransactionId\": \"542986eae4b001fd500fdc5b-coreDisc_50-title\",\n") + "  \"resultType\": \"FAIL\",\n") + "  \"narratives\": [\n") + "    {\n") + "      \"ruleProcessingDate\": \"Nov 2, 2014 7:30:20 AM\",\n") + "      \"area\": \"Discovery\",\n") + "      \"phase\": \"Validation\",\n") + "      \"message\": \"Chain does not have a discovery event. Possible it was cut by the date that was picked\",\n") + "      \"ruleName\": \"Validate chain\\u0027s discovery event existence\",\n") + "      \"lastRule\": true\n") + "    }\n") + "  ]\n") + "},\n") + "{\n") + "  \"mpTransactionId\": \"54298649e4b001fd500fda3e-fixCoreDiscovery_3-title\",\n") + "  \"resultType\": \"FAIL\",\n") + "  \"narratives\": [\n") + "    {\n") + "      \"ruleProcessingDate\": \"Nov 2, 2014 7:30:20 AM\",\n") + "      \"area\": \"Discovery\",\n") + "      \"phase\": \"Validation\",\n") + "      \"message\": \"There is one and only discovery event ContentDiscoveredEvent(230) found.\",\n") + "      \"ruleName\": \"Marks existence of discovery event (230)\",\n") + "      \"lastRule\": false\n") + "    },\n") + "    {\n") + "      \"ruleProcessingDate\": \"Nov 2, 2014 7:30:20 AM\",\n") + "      \"area\": \"Discovery/Processing\",\n") + "      \"phase\": \"Validation\",\n") + "      \"message\": \"Chain does not have SLA start event (204) in Discovery or Processing. \",\n") + "      \"ruleName\": \"Check if SLA start event is not present (204). \",\n") + "      \"lastRule\": false\n") + "    },\n") + "    {\n") + "      \"ruleProcessingDate\": \"Nov 2, 2014 7:30:20 AM\",\n") + "      \"area\": \"Processing\",\n") + "      \"phase\": \"Transcode\",\n") + "      \"message\": \"No start transcoding events found\",\n") + "      \"ruleName\": \"Start transcoding events missing (240)\",\n") + "      \"lastRule\": true\n") + "    }\n") + "  ]\n") + "}]");
        List<String> problems = JsonPath.read(json, "$..narratives[?(@.lastRule==true)].message");
        Assert.assertThat(problems).containsExactly("Chain does not have a discovery event. Possible it was cut by the date that was picked", "No start transcoding events found");
    }

    // http://stackoverflow.com/questions/28596324/jsonpath-filtering-api
    @Test
    public void stack_overflow_question_1() {
        String json = "{\n" + (((((((((((((((((((((((((((((("\"store\": {\n" + "    \"book\": [\n") + "        {\n") + "            \"category\": \"reference\",\n") + "            \"authors\" : [\n") + "                 {\n") + "                     \"firstName\" : \"Nigel\",\n") + "                     \"lastName\" :  \"Rees\"\n") + "                  }\n") + "            ],\n") + "            \"title\": \"Sayings of the Century\",\n") + "            \"price\": 8.95\n") + "        },\n") + "        {\n") + "            \"category\": \"fiction\",\n") + "            \"authors\": [\n") + "                 {\n") + "                     \"firstName\" : \"Evelyn\",\n") + "                     \"lastName\" :  \"Waugh\"\n") + "                  },\n") + "                 {\n") + "                     \"firstName\" : \"Another\",\n") + "                     \"lastName\" :  \"Author\"\n") + "                  }\n") + "            ],\n") + "            \"title\": \"Sword of Honour\",\n") + "            \"price\": 12.99\n") + "        }\n") + "    ]\n") + "  }\n") + "}");
        Filter filter = Filter.filter(Criteria.where("authors[*].lastName").contains("Waugh"));
        Object read = JsonPath.parse(json).read("$.store.book[?]", filter);
    }

    @Test
    public void issue_71() {
        String json = "{\n" + (((((("    \"logs\": [\n" + "        {\n") + "            \"message\": \"it\'s here\",\n") + "            \"id\": 2\n") + "        }\n") + "    ]\n") + "}");
        List<String> result = JsonPath.read(json, "$.logs[?(@.message == \'it\\\'s here\')].message");
        Assert.assertThat(result).containsExactly("it's here");
    }

    @Test
    public void issue_76() throws Exception {
        String json = "{\n" + ((("    \"cpus\": -8.88178419700125e-16,\n" + "    \"disk\": 0,\n") + "    \"mem\": 0\n") + "}");
        JSONParser parser = new JSONParser(JSONParser.MODE_PERMISSIVE);
        JSONAware jsonModel = ((JSONAware) (parser.parse(json)));
        jsonModel.toJSONString();
    }

    @Test
    public void issue_79() throws Exception {
        String json = "{ \n" + (((((((("  \"c\": {\n" + "    \"d1\": {\n") + "      \"url\": [ \"url1\", \"url2\" ]\n") + "    },\n") + "    \"d2\": {\n") + "      \"url\": [ \"url3\", \"url4\",\"url5\" ]\n") + "    }\n") + "  }\n") + "}");
        List<String> res = JsonPath.read(json, "$.c.*.url[2]");
        Assert.assertThat(res).containsExactly("url5");
    }

    @Test
    public void issue_94_1() throws Exception {
        LRUCache cache = new LRUCache(200);
        JsonPath dummy = JsonPath.compile("$");
        for (int i = 0; i < 1000; ++i) {
            String key = String.valueOf(i);
            cache.get(key);
            cache.put(key, dummy);
        }
        Assert.assertThat(cache.size()).isEqualTo(200);
    }

    @Test
    public void issue_94_2() throws Exception {
        LRUCache cache = new LRUCache(5);
        JsonPath dummy = JsonPath.compile("$");
        cache.put("1", dummy);
        cache.put("2", dummy);
        cache.put("3", dummy);
        cache.put("4", dummy);
        cache.put("5", dummy);
        cache.put("6", dummy);
        cache.get("1");
        cache.get("2");
        cache.get("3");
        cache.get("4");
        cache.get("5");
        cache.get("6");
        cache.get("2");
        cache.get("3");
        cache.get("4");
        cache.get("5");
        cache.get("6");
        cache.get("3");
        cache.get("4");
        cache.get("5");
        cache.get("6");
        cache.get("4");
        cache.get("5");
        cache.get("6");
        cache.get("5");
        cache.get("6");
        cache.get("6");
        Assert.assertThat(cache.getSilent("6")).isNotNull();
        Assert.assertThat(cache.getSilent("5")).isNotNull();
        Assert.assertThat(cache.getSilent("4")).isNotNull();
        Assert.assertThat(cache.getSilent("3")).isNotNull();
        Assert.assertThat(cache.getSilent("2")).isNotNull();
        Assert.assertThat(cache.getSilent("1")).isNull();
    }

    @Test
    public void issue_97() throws Exception {
        String json = "{ \"books\": [ " + (((((((((("{ \"category\": \"fiction\" }, " + "{ \"category\": \"reference\" }, ") + "{ \"category\": \"fiction\" }, ") + "{ \"category\": \"fiction\" }, ") + "{ \"category\": \"reference\" }, ") + "{ \"category\": \"fiction\" }, ") + "{ \"category\": \"reference\" }, ") + "{ \"category\": \"reference\" }, ") + "{ \"category\": \"reference\" }, ") + "{ \"category\": \"reference\" }, ") + "{ \"category\": \"reference\" } ]  }");
        Configuration conf = Configuration.builder().jsonProvider(new GsonJsonProvider()).mappingProvider(new GsonMappingProvider()).build();
        DocumentContext context = JsonPath.using(conf).parse(json);
        context.delete("$.books[?(@.category == 'reference')]");
        List<String> categories = context.read("$..category", List.class);
        Assert.assertThat(categories).containsOnly("fiction");
    }

    @Test
    public void issue_99() throws Exception {
        String json = "{\n" + (((((((((((("    \"array1\": [\n" + "        {\n") + "            \"array2\": []\n") + "        },\n") + "        {\n") + "            \"array2\": [\n") + "                {\n") + "                    \"key\": \"test_key\"\n") + "                }\n") + "            ]\n") + "        }\n") + "    ]\n") + "}");
        Configuration configuration = Configuration.defaultConfiguration().addOptions(DEFAULT_PATH_LEAF_TO_NULL);
        List<String> keys = JsonPath.using(configuration).parse(json).read("$.array1[*].array2[0].key");
    }

    @Test
    public void issue_129() throws Exception {
        final Map<String, Integer> match = new HashMap<String, Integer>();
        match.put("a", 1);
        match.put("b", 2);
        Map<String, Integer> noMatch = new HashMap<String, Integer>();
        noMatch.put("a", (-1));
        noMatch.put("b", (-2));
        Filter orig = Filter.filter(Criteria.where("a").eq(1).and("b").eq(2));
        String filterAsString = orig.toString();
        Filter parsed = Filter.parse(filterAsString);
        Assert.assertThat(orig.apply(createPredicateContext(match))).isTrue();
        Assert.assertThat(parsed.apply(createPredicateContext(match))).isTrue();
        Assert.assertThat(orig.apply(createPredicateContext(noMatch))).isFalse();
        Assert.assertThat(parsed.apply(createPredicateContext(noMatch))).isFalse();
    }

    @Test
    public void issue_131() {
        String json = "[\n" + ((((((((("    {\n" + "        \"foo\": \"1\"\n") + "    },\n") + "    {\n") + "        \"foo\": null\n") + "    },\n") + "    {\n") + "        \"xxx\": null\n") + "    }\n") + "]");
        List<Map<String, String>> result = JsonPath.read(json, "$[?(@.foo)]");
        Assert.assertThat(result).extracting("foo").containsExactly("1", null);
    }

    @Test
    public void issue_131_2() {
        String json = "[\n" + ((((((((("    {\n" + "        \"foo\": { \"bar\" : \"0\"}\n") + "    },\n") + "    {\n") + "        \"foo\": null\n") + "    },\n") + "    {\n") + "        \"xxx\": null\n") + "    }\n") + "]");
        List<String> result = JsonPath.read(json, "$[?(@.foo != null)].foo.bar");
        Assert.assertThat(result).containsExactly("0");
        result = JsonPath.read(json, "$[?(@.foo.bar)].foo.bar");
        Assert.assertThat(result).containsExactly("0");
    }

    @Test
    public void issue_131_3() {
        String json = "[\n" + ((((((((("    1,\n" + "    2,\n") + "    {\n") + "        \"d\": {\n") + "            \"random\": null,\n") + "            \"date\": 1234\n") + "        },\n") + "        \"l\": \"filler\"\n") + "    }\n") + "]");
        List<Integer> result = JsonPath.read(json, "$[2]['d'][?(@.random)]['date']");
        Assert.assertThat(result).containsExactly(1234);
    }

    // https://groups.google.com/forum/#!topic/jsonpath/Ojv8XF6LgqM
    @Test
    public void using_square_bracket_literal_path() {
        String json = "{ \"valid key[@num = 2]\" : \"value\" }";
        String result = JsonPath.read(json, "$['valid key[@num = 2]']");
        Assertions.assertThat(result).isEqualTo("value");
    }

    @Test
    public void issue_90() {
        String json = "{\n" + (((((((((((((((((((("    \"store\": {\n" + "        \"book\": [\n") + "            {\n") + "                \"price\": \"120\"\n") + "            },\n") + "            {\n") + "                \"price\": 8.95\n") + "            },\n") + "            {\n") + "                \"price\": 12.99\n") + "            },\n") + "            {\n") + "                \"price\": 8.99\n") + "            },\n") + "            {\n") + "                \"price\": 22.99\n") + "            }\n") + "        ]\n") + "    },\n") + "    \"expensive\": 10\n") + "}");
        List<Double> numbers = JsonPath.read(json, "$.store.book[?(@.price <= 90)].price");
        Assert.assertThat(numbers).containsExactly(8.95, 12.99, 8.99, 22.99);
    }

    @Test(expected = PathNotFoundException.class)
    public void github_89() {
        JsonObject json = new JsonObject();
        json.addProperty("foo", "bar");
        JsonPath path = JsonPath.compile("$.foo");
        String object = path.read(json);
    }

    @Test
    public void issue_170() {
        String json = "{\n" + ((((("  \"array\": [\n" + "    0,\n") + "    1,\n") + "    2\n") + "  ]\n") + "}");
        DocumentContext context = JsonPath.using(BaseTest.JACKSON_JSON_NODE_CONFIGURATION).parse(json);
        context = context.set("$.array[0]", null);
        context = context.set("$.array[2]", null);
        List<Integer> list = context.read("$.array", List.class);
        Assert.assertThat(list).containsExactly(null, 1, null);
    }

    @Test
    public void issue_171() {
        String json = "{\n" + (("  \"can delete\": \"this\",\n" + "  \"can\'t delete\": \"this\"\n") + "}");
        DocumentContext context = JsonPath.using(BaseTest.JACKSON_JSON_NODE_CONFIGURATION).parse(json);
        context.set("$.['can delete']", null);
        context.set("$.[\'can\\\'t delete\']", null);
        ObjectNode objectNode = context.read("$");
        Assert.assertThat(objectNode.get("can delete").isNull());
        Assert.assertThat(objectNode.get("can't delete").isNull());
    }

    @Test
    public void issue_309() {
        String json = "{\n" + (((((((("\"jsonArr\": [\n" + "   {\n") + "       \"name\":\"nOne\"\n") + "   },\n") + "   {\n") + "       \"name\":\"nTwo\"\n") + "   }\n") + "   ]\n") + "}");
        DocumentContext doc = JsonPath.parse(json).set("$.jsonArr[1].name", "Jayway");
        Assert.assertThat(((String) (doc.read("$.jsonArr[0].name")))).isEqualTo("nOne");
        Assert.assertThat(((String) (doc.read("$.jsonArr[1].name")))).isEqualTo("Jayway");
    }

    @Test
    public void issue_378() {
        String json = "{\n" + ((((((("    \"nodes\": {\n" + "        \"unnamed1\": {\n") + "            \"ntpServers\": [\n") + "                \"1.2.3.4\"\n") + "            ]\n") + "        }\n") + "    }\n") + "}");
        Configuration configuration = Configuration.builder().jsonProvider(new JacksonJsonNodeJsonProvider()).mappingProvider(new JacksonMappingProvider()).build();
        DocumentContext ctx = JsonPath.using(configuration).parse(json);
        String path = "$.nodes[*][?(!([\"1.2.3.4\"] subsetof @.ntpServers))].ntpServers";
        JsonPath jsonPath = JsonPath.compile(path);
        ctx.read(jsonPath);
    }
}

