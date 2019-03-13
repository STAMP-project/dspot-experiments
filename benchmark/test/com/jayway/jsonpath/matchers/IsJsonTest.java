package com.jayway.jsonpath.matchers;


import com.jayway.jsonpath.matchers.helpers.ResourceHelpers;
import com.jayway.jsonpath.matchers.helpers.TestingMatchers;
import java.io.File;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.StringDescription;
import org.junit.Assert;
import org.junit.Test;


public class IsJsonTest {
    private static final String VALID_JSON = ResourceHelpers.resource("example.json");

    private static final String INVALID_JSON = "{ invalid-json }";

    private static final String BOOKS_JSON_STRING = ResourceHelpers.resource("books.json");

    private static final File BOOKS_JSON_FILE = ResourceHelpers.resourceAsFile("books.json");

    private static final Object BOOKS_JSON_PARSED = IsJsonTest.parseJson(IsJsonTest.BOOKS_JSON_STRING);

    @Test
    public void shouldMatchOnEmptyJsonObject() {
        Assert.assertThat("{}", JsonPathMatchers.isJson());
    }

    @Test
    public void shouldMatchOnJsonObject() {
        Assert.assertThat("{ \"hi\" : \"there\" }", JsonPathMatchers.isJson());
    }

    @Test
    public void shouldMatchOnEmptyJsonArray() {
        Assert.assertThat("[]", JsonPathMatchers.isJson());
    }

    @Test
    public void shouldMatchOnJsonArray() {
        Assert.assertThat("[\"hi\", \"there\"]", JsonPathMatchers.isJson());
    }

    @Test
    public void shouldMatchValidJson() {
        Assert.assertThat(IsJsonTest.VALID_JSON, JsonPathMatchers.isJson());
        Assert.assertThat(IsJsonTest.BOOKS_JSON_STRING, JsonPathMatchers.isJson());
    }

    @Test
    public void shouldNotMatchInvalidJson() {
        Assert.assertThat(IsJsonTest.INVALID_JSON, not(JsonPathMatchers.isJson()));
        Assert.assertThat(new Object(), not(JsonPathMatchers.isJson()));
        Assert.assertThat(new Object[]{  }, not(JsonPathMatchers.isJson()));
        Assert.assertThat("hi there", not(JsonPathMatchers.isJson()));
        Assert.assertThat(new Integer(42), not(JsonPathMatchers.isJson()));
        Assert.assertThat(Boolean.TRUE, not(JsonPathMatchers.isJson()));
        Assert.assertThat(false, not(JsonPathMatchers.isJson()));
        Assert.assertThat(null, not(JsonPathMatchers.isJson()));
    }

    @Test
    public void shouldMatchJsonObjectEvaluatedToTrue() {
        Assert.assertThat(IsJsonTest.BOOKS_JSON_PARSED, JsonPathMatchers.isJson(TestingMatchers.withPathEvaluatedTo(true)));
    }

    @Test
    public void shouldNotMatchJsonObjectEvaluatedToFalse() {
        Assert.assertThat(IsJsonTest.BOOKS_JSON_PARSED, not(JsonPathMatchers.isJson(TestingMatchers.withPathEvaluatedTo(false))));
    }

    @Test
    public void shouldMatchJsonStringEvaluatedToTrue() {
        Assert.assertThat(IsJsonTest.BOOKS_JSON_STRING, JsonPathMatchers.isJson(TestingMatchers.withPathEvaluatedTo(true)));
    }

    @Test
    public void shouldNotMatchJsonStringEvaluatedToFalse() {
        Assert.assertThat(IsJsonTest.BOOKS_JSON_STRING, not(JsonPathMatchers.isJson(TestingMatchers.withPathEvaluatedTo(false))));
    }

    @Test
    public void shouldMatchJsonFileEvaluatedToTrue() {
        Assert.assertThat(IsJsonTest.BOOKS_JSON_FILE, JsonPathMatchers.isJson(TestingMatchers.withPathEvaluatedTo(true)));
    }

    @Test
    public void shouldNotMatchJsonFileEvaluatedToFalse() {
        Assert.assertThat(IsJsonTest.BOOKS_JSON_FILE, not(JsonPathMatchers.isJson(TestingMatchers.withPathEvaluatedTo(false))));
    }

    @Test
    public void shouldNotMatchNonExistingJsonFile() {
        File nonExistingFile = new File("missing-file");
        Assert.assertThat(nonExistingFile, not(JsonPathMatchers.isJson()));
    }

    @Test
    public void shouldBeDescriptive() {
        Matcher<Object> matcher = JsonPathMatchers.isJson(TestingMatchers.withPathEvaluatedTo(true));
        Description description = new StringDescription();
        matcher.describeTo(description);
        Assert.assertThat(description.toString(), startsWith("is json"));
        Assert.assertThat(description.toString(), containsString(TestingMatchers.MATCH_TRUE_TEXT));
    }

    @Test
    public void shouldDescribeMismatchOfValidJson() {
        Matcher<Object> matcher = JsonPathMatchers.isJson(TestingMatchers.withPathEvaluatedTo(true));
        Description description = new StringDescription();
        matcher.describeMismatch(IsJsonTest.BOOKS_JSON_STRING, description);
        Assert.assertThat(description.toString(), containsString(TestingMatchers.MISMATCHED_TEXT));
    }

    @Test
    public void shouldDescribeMismatchOfInvalidJson() {
        Matcher<Object> matcher = JsonPathMatchers.isJson(TestingMatchers.withPathEvaluatedTo(true));
        Description description = new StringDescription();
        matcher.describeMismatch("invalid-json", description);
        Assert.assertThat(description.toString(), containsString("\"invalid-json\""));
    }
}

