package com.jayway.jsonpath.matchers;


import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.ReadContext;
import com.jayway.jsonpath.matchers.helpers.ResourceHelpers;
import java.io.File;
import java.util.List;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;


public class JsonPathMatchersTest {
    private static final String BOOKS_JSON = ResourceHelpers.resource("books.json");

    private static final String INVALID_JSON = "{ invalid-json }";

    private static final File BOOKS_JSON_FILE = ResourceHelpers.resourceAsFile("books.json");

    @Test
    public void shouldMatchJsonPathToStringValue() {
        final String json = "{\"name\": \"Jessie\"}";
        Assert.assertThat(json, hasJsonPath("$.name"));
        Assert.assertThat(json, isJson(withJsonPath("$.name")));
        Assert.assertThat(json, hasJsonPath("$.name", equalTo("Jessie")));
        Assert.assertThat(json, isJson(withJsonPath("$.name", equalTo("Jessie"))));
        Assert.assertThat(json, not(hasJsonPath("$.name", equalTo("John"))));
        Assert.assertThat(json, not(isJson(withJsonPath("$.name", equalTo("John")))));
    }

    @Test
    public void shouldMatchJsonPathToIntegerValue() {
        final String json = "{\"number\": 10}";
        Assert.assertThat(json, hasJsonPath("$.number"));
        Assert.assertThat(json, isJson(withJsonPath("$.number")));
        Assert.assertThat(json, hasJsonPath("$.number", equalTo(10)));
        Assert.assertThat(json, isJson(withJsonPath("$.number", equalTo(10))));
        Assert.assertThat(json, not(hasJsonPath("$.number", equalTo(3))));
        Assert.assertThat(json, not(isJson(withJsonPath("$.number", equalTo(3)))));
    }

    @Test
    public void shouldMatchJsonPathToDoubleValue() {
        final String json = "{\"price\": 19.95}";
        Assert.assertThat(json, hasJsonPath("$.price"));
        Assert.assertThat(json, isJson(withJsonPath("$.price")));
        Assert.assertThat(json, hasJsonPath("$.price", equalTo(19.95)));
        Assert.assertThat(json, isJson(withJsonPath("$.price", equalTo(19.95))));
        Assert.assertThat(json, not(hasJsonPath("$.price", equalTo(3.3))));
        Assert.assertThat(json, not(isJson(withJsonPath("$.price", equalTo(42)))));
    }

    @Test
    public void shouldMatchJsonPathToBooleanValue() {
        final String json = "{\"flag\": false}";
        Assert.assertThat(json, hasJsonPath("$.flag"));
        Assert.assertThat(json, isJson(withJsonPath("$.flag")));
        Assert.assertThat(json, hasJsonPath("$.flag", equalTo(false)));
        Assert.assertThat(json, isJson(withJsonPath("$.flag", equalTo(false))));
        Assert.assertThat(json, not(hasJsonPath("$.flag", equalTo(true))));
        Assert.assertThat(json, not(isJson(withJsonPath("$.flag", equalTo(true)))));
    }

    @Test
    public void shouldMatchJsonPathToJsonObject() {
        final String json = "{\"object\": { \"name\":\"Oscar\"}}";
        Assert.assertThat(json, hasJsonPath("$.object"));
        Assert.assertThat(json, isJson(withJsonPath("$.object")));
        Assert.assertThat(json, hasJsonPath("$.object", instanceOf(Map.class)));
        Assert.assertThat(json, isJson(withJsonPath("$.object", instanceOf(Map.class))));
        Assert.assertThat(json, not(hasJsonPath("$.object", instanceOf(List.class))));
        Assert.assertThat(json, not(isJson(withJsonPath("$.object", instanceOf(List.class)))));
    }

    @Test
    public void shouldMatchJsonPathToEmptyJsonObject() {
        final String json = "{\"empty_object\": {}}";
        Assert.assertThat(json, hasJsonPath("$.empty_object"));
        Assert.assertThat(json, isJson(withJsonPath("$.empty_object")));
        Assert.assertThat(json, hasJsonPath("$.empty_object", instanceOf(Map.class)));
        Assert.assertThat(json, isJson(withJsonPath("$.empty_object", instanceOf(Map.class))));
        Assert.assertThat(json, not(hasJsonPath("$.empty_object", instanceOf(List.class))));
        Assert.assertThat(json, not(isJson(withJsonPath("$.empty_object", instanceOf(List.class)))));
    }

    @Test
    public void shouldMatchJsonPathToJsonArray() {
        final String json = "{\"list\": [ \"one\",\"two\",\"three\"]}";
        Assert.assertThat(json, hasJsonPath("$.list"));
        Assert.assertThat(json, hasJsonPath("$.list[*]"));
        Assert.assertThat(json, isJson(withJsonPath("$.list")));
        Assert.assertThat(json, isJson(withJsonPath("$.list[*]")));
        Assert.assertThat(json, hasJsonPath("$.list", contains("one", "two", "three")));
        Assert.assertThat(json, isJson(withJsonPath("$.list", hasItem("two"))));
        Assert.assertThat(json, not(hasJsonPath("$.list", hasSize(2))));
        Assert.assertThat(json, not(isJson(withJsonPath("$.list", contains("four")))));
    }

    @Test
    public void shouldMatchJsonPathToEmptyJsonArray() {
        final String json = "{\"empty_list\": []}";
        Assert.assertThat(json, hasJsonPath("$.empty_list"));
        Assert.assertThat(json, hasJsonPath("$.empty_list[*]"));
        Assert.assertThat(json, isJson(withJsonPath("$.empty_list")));
        Assert.assertThat(json, isJson(withJsonPath("$.empty_list[*]")));
        Assert.assertThat(json, hasJsonPath("$.empty_list", empty()));
        Assert.assertThat(json, isJson(withJsonPath("$.empty_list", hasSize(0))));
        Assert.assertThat(json, not(hasJsonPath("$.empty_list", hasSize(2))));
        Assert.assertThat(json, not(isJson(withJsonPath("$.empty_list", contains("four")))));
    }

    @Test
    public void willMatchIndefiniteJsonPathsEvaluatedToEmptyLists() {
        // This is just a test to demonstrate that indefinite paths
        // will always match, regardless of result. This is because
        // the evaluation of these expressions will return lists,
        // even though they may be empty.
        String json = "{\"items\": []}";
        Assert.assertThat(json, hasJsonPath("$.items[*].name"));
        Assert.assertThat(json, hasJsonPath("$.items[*]"));
        Assert.assertThat(json, hasJsonPath("$.items[*]", hasSize(0)));
    }

    @Test
    public void shouldMatchJsonPathToNullValue() {
        final String json = "{\"none\": null}";
        Assert.assertThat(json, hasJsonPath("$.none"));
        Assert.assertThat(json, isJson(withJsonPath("$.none")));
        Assert.assertThat(json, hasJsonPath("$.none", nullValue()));
        Assert.assertThat(json, isJson(withJsonPath("$.none", nullValue())));
        Assert.assertThat(json, not(hasJsonPath("$.none", equalTo("something"))));
        Assert.assertThat(json, not(isJson(withJsonPath("$.none", empty()))));
    }

    @Test
    public void shouldNotMatchNonExistingJsonPath() {
        final String json = "{}";
        Assert.assertThat(json, not(hasJsonPath("$.not_there")));
        Assert.assertThat(json, not(hasJsonPath("$.not_there", anything())));
        Assert.assertThat(json, not(hasJsonPath("$.not_there[*]")));
        Assert.assertThat(json, not(hasJsonPath("$.not_there[*]", anything())));
        Assert.assertThat(json, not(isJson(withJsonPath("$.not_there"))));
        Assert.assertThat(json, not(isJson(withJsonPath("$.not_there", anything()))));
        Assert.assertThat(json, not(isJson(withJsonPath("$.not_there[*]"))));
        Assert.assertThat(json, not(isJson(withJsonPath("$.not_there[*]", anything()))));
    }

    @Test
    public void shouldNotMatchInvalidJsonWithPath() {
        Assert.assertThat(JsonPathMatchersTest.INVALID_JSON, not(hasJsonPath("$.path")));
        Assert.assertThat(new Object(), not(hasJsonPath("$.path")));
        Assert.assertThat(null, not(hasJsonPath("$.path")));
    }

    @Test
    public void shouldNotMatchInvalidJsonWithPathAndValue() {
        Assert.assertThat(JsonPathMatchersTest.INVALID_JSON, not(hasJsonPath("$.path", anything())));
        Assert.assertThat(new Object(), not(hasJsonPath("$.path", anything())));
        Assert.assertThat(null, not(hasJsonPath("$.message", anything())));
    }

    @Test
    public void shouldMatchJsonPathOnFile() {
        Assert.assertThat(JsonPathMatchersTest.BOOKS_JSON_FILE, hasJsonPath("$.store.name", equalTo("Little Shop")));
    }

    @Test
    public void shouldNotMatchJsonPathOnNonExistingFile() {
        File nonExistingFile = new File("missing-file");
        Assert.assertThat(nonExistingFile, not(hasJsonPath("$..*", anything())));
    }

    @Test
    public void shouldMatchJsonPathOnParsedJsonObject() {
        Object json = Configuration.defaultConfiguration().jsonProvider().parse(JsonPathMatchersTest.BOOKS_JSON);
        Assert.assertThat(json, hasJsonPath("$.store.name", equalTo("Little Shop")));
    }

    @Test
    public void shouldMatchJsonPathOnReadContext() {
        String test = "{\"foo\":\"bar\"}";
        ReadContext context = JsonPath.parse(test);
        Assert.assertThat(context, hasJsonPath("$.foo"));
        Assert.assertThat(context, hasJsonPath("$.foo", equalTo("bar")));
        Assert.assertThat(context, hasNoJsonPath("$.zoo"));
    }
}

