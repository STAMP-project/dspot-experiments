package com.jayway.jsonpath.matchers;


import com.jayway.jsonpath.matchers.helpers.ResourceHelpers;
import java.io.File;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;


@Ignore
public class DemoTest {
    @Test
    public void shouldFailOnJsonString() {
        String json = ResourceHelpers.resource("books.json");
        Assert.assertThat(json, isJson(withJsonPath("$.store.name", Matchers.equalTo("The Shop"))));
    }

    @Test
    public void shouldFailOnJsonFile() {
        File json = ResourceHelpers.resourceAsFile("books.json");
        Assert.assertThat(json, isJson(withJsonPath("$.store.name", Matchers.equalTo("The Shop"))));
    }

    @Test
    public void shouldFailOnInvalidJsonString() {
        String json = ResourceHelpers.resource("invalid.json");
        Assert.assertThat(json, isJson(withJsonPath("$.store.name", Matchers.equalTo("The Shop"))));
    }

    @Test
    public void shouldFailOnInvalidJsonFile() {
        File json = ResourceHelpers.resourceAsFile("invalid.json");
        Assert.assertThat(json, isJson(withJsonPath("$.store.name", Matchers.equalTo("The Shop"))));
    }

    @Test
    public void shouldFailOnTypedJsonString() {
        String json = ResourceHelpers.resource("books.json");
        Assert.assertThat(json, isJsonString(withJsonPath("$.store.name", Matchers.equalTo("The Shop"))));
    }

    @Test
    public void shouldFailOnTypedJsonFile() {
        File json = ResourceHelpers.resourceAsFile("books.json");
        Assert.assertThat(json, isJsonFile(withJsonPath("$.store.name", Matchers.equalTo("The Shop"))));
    }

    @Test
    public void shouldFailOnTypedInvalidJsonString() {
        String json = ResourceHelpers.resource("invalid.json");
        Assert.assertThat(json, isJsonString(withJsonPath("$.store.name", Matchers.equalTo("The Shop"))));
    }

    @Test
    public void shouldFailOnTypedInvalidJsonFile() {
        File json = ResourceHelpers.resourceAsFile("invalid.json");
        Assert.assertThat(json, isJsonFile(withJsonPath("$.store.name", Matchers.equalTo("The Shop"))));
    }

    @Test
    public void shouldFailOnNonExistingJsonPath() {
        String json = ResourceHelpers.resource("books.json");
        Assert.assertThat(json, hasJsonPath("$.not-here"));
    }

    @Test
    public void shouldFailOnExistingJsonPath() {
        String json = ResourceHelpers.resource("books.json");
        Assert.assertThat(json, hasNoJsonPath("$.store.name"));
    }

    @Test
    public void shouldFailOnExistingJsonPathAlternative() {
        String json = ResourceHelpers.resource("books.json");
        Assert.assertThat(json, isJson(withoutJsonPath("$.store.name")));
    }
}

