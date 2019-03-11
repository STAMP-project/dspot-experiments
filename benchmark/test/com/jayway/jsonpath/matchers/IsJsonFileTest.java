package com.jayway.jsonpath.matchers;


import com.jayway.jsonpath.matchers.helpers.ResourceHelpers;
import com.jayway.jsonpath.matchers.helpers.TestingMatchers;
import java.io.File;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.StringDescription;
import org.junit.Assert;
import org.junit.Test;


public class IsJsonFileTest {
    private static final File BOOKS_JSON = ResourceHelpers.resourceAsFile("books.json");

    private static final File INVALID_JSON = ResourceHelpers.resourceAsFile("invalid.json");

    @Test
    public void shouldMatchJsonFileEvaluatedToTrue() {
        Assert.assertThat(IsJsonFileTest.BOOKS_JSON, JsonPathMatchers.isJsonFile(TestingMatchers.withPathEvaluatedTo(true)));
    }

    @Test
    public void shouldNotMatchJsonFileEvaluatedToFalse() {
        Assert.assertThat(IsJsonFileTest.BOOKS_JSON, not(JsonPathMatchers.isJsonFile(TestingMatchers.withPathEvaluatedTo(false))));
    }

    @Test
    public void shouldNotMatchInvalidJsonFile() {
        Assert.assertThat(IsJsonFileTest.INVALID_JSON, not(JsonPathMatchers.isJsonFile(TestingMatchers.withPathEvaluatedTo(true))));
        Assert.assertThat(IsJsonFileTest.INVALID_JSON, not(JsonPathMatchers.isJsonFile(TestingMatchers.withPathEvaluatedTo(false))));
    }

    @Test
    public void shouldBeDescriptive() {
        Matcher<File> matcher = JsonPathMatchers.isJsonFile(TestingMatchers.withPathEvaluatedTo(true));
        Description description = new StringDescription();
        matcher.describeTo(description);
        Assert.assertThat(description.toString(), startsWith("is json"));
        Assert.assertThat(description.toString(), containsString(TestingMatchers.MATCH_TRUE_TEXT));
    }

    @Test
    public void shouldDescribeMismatchOfValidJson() {
        Matcher<File> matcher = JsonPathMatchers.isJsonFile(TestingMatchers.withPathEvaluatedTo(true));
        Description description = new StringDescription();
        matcher.describeMismatch(IsJsonFileTest.BOOKS_JSON, description);
        Assert.assertThat(description.toString(), containsString(TestingMatchers.MISMATCHED_TEXT));
    }

    @Test
    public void shouldDescribeMismatchOfInvalidJson() {
        Matcher<File> matcher = JsonPathMatchers.isJsonFile(TestingMatchers.withPathEvaluatedTo(true));
        Description description = new StringDescription();
        matcher.describeMismatch(IsJsonFileTest.INVALID_JSON, description);
        Assert.assertThat(description.toString(), containsString("invalid.json"));
        Assert.assertThat(description.toString(), containsString("invalid-json"));
    }
}

