package com.jayway.jsonpath.matchers;


import com.jayway.jsonpath.matchers.helpers.ResourceHelpers;
import com.jayway.jsonpath.matchers.helpers.TestingMatchers;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.StringDescription;
import org.junit.Assert;
import org.junit.Test;


public class IsJsonStringTest {
    private static final String BOOKS_JSON = ResourceHelpers.resource("books.json");

    @Test
    public void shouldMatchJsonStringEvaluatedToTrue() {
        Assert.assertThat(IsJsonStringTest.BOOKS_JSON, JsonPathMatchers.isJsonString(TestingMatchers.withPathEvaluatedTo(true)));
    }

    @Test
    public void shouldNotMatchJsonStringEvaluatedToFalse() {
        Assert.assertThat(IsJsonStringTest.BOOKS_JSON, not(JsonPathMatchers.isJsonString(TestingMatchers.withPathEvaluatedTo(false))));
    }

    @Test
    public void shouldNotMatchInvalidJsonString() {
        Assert.assertThat("invalid-json", not(JsonPathMatchers.isJsonString(TestingMatchers.withPathEvaluatedTo(true))));
        Assert.assertThat("invalid-json", not(JsonPathMatchers.isJsonString(TestingMatchers.withPathEvaluatedTo(false))));
    }

    @Test
    public void shouldBeDescriptive() {
        Matcher<String> matcher = JsonPathMatchers.isJsonString(TestingMatchers.withPathEvaluatedTo(true));
        Description description = new StringDescription();
        matcher.describeTo(description);
        Assert.assertThat(description.toString(), startsWith("is json"));
        Assert.assertThat(description.toString(), containsString(TestingMatchers.MATCH_TRUE_TEXT));
    }

    @Test
    public void shouldDescribeMismatchOfValidJson() {
        Matcher<String> matcher = JsonPathMatchers.isJsonString(TestingMatchers.withPathEvaluatedTo(true));
        Description description = new StringDescription();
        matcher.describeMismatch(IsJsonStringTest.BOOKS_JSON, description);
        Assert.assertThat(description.toString(), containsString(TestingMatchers.MISMATCHED_TEXT));
    }

    @Test
    public void shouldDescribeMismatchOfInvalidJson() {
        Matcher<String> matcher = JsonPathMatchers.isJsonString(TestingMatchers.withPathEvaluatedTo(true));
        Description description = new StringDescription();
        matcher.describeMismatch("invalid-json", description);
        Assert.assertThat(description.toString(), containsString("\"invalid-json\""));
    }
}

