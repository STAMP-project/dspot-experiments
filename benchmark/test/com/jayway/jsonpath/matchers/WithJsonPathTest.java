package com.jayway.jsonpath.matchers;


import com.jayway.jsonpath.InvalidPathException;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.ReadContext;
import com.jayway.jsonpath.matchers.helpers.ResourceHelpers;
import java.util.Collection;
import java.util.List;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.StringDescription;
import org.junit.Assert;
import org.junit.Test;


public class WithJsonPathTest {
    private static final ReadContext BOOKS_JSON = JsonPath.parse(ResourceHelpers.resource("books.json"));

    @Test
    public void shouldMatchExistingCompiledJsonPath() {
        Assert.assertThat(WithJsonPathTest.BOOKS_JSON, JsonPathMatchers.withJsonPath(JsonPath.compile("$.expensive")));
        Assert.assertThat(WithJsonPathTest.BOOKS_JSON, JsonPathMatchers.withJsonPath(JsonPath.compile("$.store.bicycle")));
        Assert.assertThat(WithJsonPathTest.BOOKS_JSON, JsonPathMatchers.withJsonPath(JsonPath.compile("$.store.book[2].title")));
        Assert.assertThat(WithJsonPathTest.BOOKS_JSON, JsonPathMatchers.withJsonPath(JsonPath.compile("$.store.book[*].author")));
    }

    @Test
    public void shouldMatchExistingStringJsonPath() {
        Assert.assertThat(WithJsonPathTest.BOOKS_JSON, JsonPathMatchers.withJsonPath("$.expensive"));
        Assert.assertThat(WithJsonPathTest.BOOKS_JSON, JsonPathMatchers.withJsonPath("$.store.bicycle"));
        Assert.assertThat(WithJsonPathTest.BOOKS_JSON, JsonPathMatchers.withJsonPath("$.store.book[2].title"));
        Assert.assertThat(WithJsonPathTest.BOOKS_JSON, JsonPathMatchers.withJsonPath("$.store.book[*].author"));
    }

    @Test
    public void shouldNotMatchNonExistingJsonPath() {
        Assert.assertThat(WithJsonPathTest.BOOKS_JSON, not(JsonPathMatchers.withJsonPath(JsonPath.compile("$.not_there"))));
        Assert.assertThat(WithJsonPathTest.BOOKS_JSON, not(JsonPathMatchers.withJsonPath(JsonPath.compile("$.store.book[5].title"))));
        Assert.assertThat(WithJsonPathTest.BOOKS_JSON, not(JsonPathMatchers.withJsonPath(JsonPath.compile("$.store.book[1].not_there"))));
    }

    @Test
    public void shouldNotMatchNonExistingStringJsonPath() {
        Assert.assertThat(WithJsonPathTest.BOOKS_JSON, not(JsonPathMatchers.withJsonPath("$.not_there")));
        Assert.assertThat(WithJsonPathTest.BOOKS_JSON, not(JsonPathMatchers.withJsonPath("$.store.book[5].title")));
        Assert.assertThat(WithJsonPathTest.BOOKS_JSON, not(JsonPathMatchers.withJsonPath("$.store.book[1].not_there")));
    }

    @Test
    public void shouldMatchJsonPathEvaluatedToStringValue() {
        Assert.assertThat(WithJsonPathTest.BOOKS_JSON, JsonPathMatchers.withJsonPath(JsonPath.compile("$.store.bicycle.color"), equalTo("red")));
        Assert.assertThat(WithJsonPathTest.BOOKS_JSON, JsonPathMatchers.withJsonPath(JsonPath.compile("$.store.book[2].title"), equalTo("Moby Dick")));
        Assert.assertThat(WithJsonPathTest.BOOKS_JSON, JsonPathMatchers.withJsonPath("$.store.name", equalTo("Little Shop")));
        Assert.assertThat(WithJsonPathTest.BOOKS_JSON, JsonPathMatchers.withJsonPath("$.store.book[2].title", equalTo("Moby Dick")));
    }

    @Test
    public void shouldMatchJsonPathEvaluatedToIntegerValue() {
        Assert.assertThat(WithJsonPathTest.BOOKS_JSON, JsonPathMatchers.withJsonPath(JsonPath.compile("$.expensive"), equalTo(10)));
        Assert.assertThat(WithJsonPathTest.BOOKS_JSON, JsonPathMatchers.withJsonPath("$.expensive", equalTo(10)));
    }

    @Test
    public void shouldMatchJsonPathEvaluatedToDoubleValue() {
        Assert.assertThat(WithJsonPathTest.BOOKS_JSON, JsonPathMatchers.withJsonPath(JsonPath.compile("$.store.bicycle.price"), equalTo(19.95)));
        Assert.assertThat(WithJsonPathTest.BOOKS_JSON, JsonPathMatchers.withJsonPath("$.store.bicycle.price", equalTo(19.95)));
    }

    @Test
    public void shouldMatchJsonPathEvaluatedToCollectionValue() {
        Assert.assertThat(WithJsonPathTest.BOOKS_JSON, JsonPathMatchers.withJsonPath(JsonPath.compile("$.store.book[*].author"), instanceOf(List.class)));
        Assert.assertThat(WithJsonPathTest.BOOKS_JSON, JsonPathMatchers.withJsonPath(JsonPath.compile("$.store.book[*].author"), hasSize(4)));
        Assert.assertThat(WithJsonPathTest.BOOKS_JSON, JsonPathMatchers.withJsonPath(JsonPath.compile("$.store.book[*].author"), hasItem("Evelyn Waugh")));
        Assert.assertThat(WithJsonPathTest.BOOKS_JSON, JsonPathMatchers.withJsonPath(JsonPath.compile("$..book[2].title"), hasItem("Moby Dick")));
        Assert.assertThat(WithJsonPathTest.BOOKS_JSON, JsonPathMatchers.withJsonPath("$.store.book[*].author", instanceOf(Collection.class)));
        Assert.assertThat(WithJsonPathTest.BOOKS_JSON, JsonPathMatchers.withJsonPath("$.store.book[*].author", hasSize(4)));
        Assert.assertThat(WithJsonPathTest.BOOKS_JSON, JsonPathMatchers.withJsonPath("$.store.book[*].author", hasItem("Evelyn Waugh")));
        Assert.assertThat(WithJsonPathTest.BOOKS_JSON, JsonPathMatchers.withJsonPath("$..book[2].title", hasItem("Moby Dick")));
    }

    @Test(expected = InvalidPathException.class)
    public void shouldFailOnInvalidJsonPath() {
        JsonPathMatchers.withJsonPath("$[}");
    }

    @Test
    public void shouldNotMatchOnInvalidJson() {
        ReadContext invalidJson = JsonPath.parse("invalid-json");
        Assert.assertThat(invalidJson, not(JsonPathMatchers.withJsonPath("$.expensive", equalTo(10))));
    }

    @Test
    public void shouldBeDescriptive() {
        Matcher<? super ReadContext> matcher = JsonPathMatchers.withJsonPath("path", equalTo(2));
        Description description = new StringDescription();
        matcher.describeTo(description);
        Assert.assertThat(description.toString(), containsString("path"));
        Assert.assertThat(description.toString(), containsString("<2>"));
    }

    @Test
    public void shouldDescribeMismatchOfEvaluation() {
        Matcher<? super ReadContext> matcher = JsonPathMatchers.withJsonPath("expensive", equalTo(3));
        Description description = new StringDescription();
        matcher.describeMismatch(WithJsonPathTest.BOOKS_JSON, description);
        Assert.assertThat(description.toString(), containsString("expensive"));
        Assert.assertThat(description.toString(), containsString("<10>"));
    }

    @Test
    public void shouldDescribeMismatchOfPathNotFound() {
        Matcher<? super ReadContext> matcher = JsonPathMatchers.withJsonPath("not-here", equalTo(3));
        Description description = new StringDescription();
        matcher.describeMismatch(WithJsonPathTest.BOOKS_JSON, description);
        Assert.assertThat(description.toString(), containsString("not-here"));
        Assert.assertThat(description.toString(), containsString("was not found"));
    }
}

