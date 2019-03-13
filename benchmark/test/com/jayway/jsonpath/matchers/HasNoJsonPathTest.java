package com.jayway.jsonpath.matchers;


import org.junit.Assert;
import org.junit.Test;


public class HasNoJsonPathTest {
    private static final String JSON_STRING = "{" + (("\"none\": null," + "\"name\": \"Jessie\"") + "}");

    @Test
    public void shouldMatchMissingJsonPath() {
        Assert.assertThat(HasNoJsonPathTest.JSON_STRING, JsonPathMatchers.hasNoJsonPath("$.not_there"));
    }

    @Test
    public void shouldNotMatchExistingJsonPath() {
        Assert.assertThat(HasNoJsonPathTest.JSON_STRING, not(JsonPathMatchers.hasNoJsonPath("$.name")));
    }

    @Test
    public void shouldNotMatchExplicitNull() {
        Assert.assertThat(HasNoJsonPathTest.JSON_STRING, not(JsonPathMatchers.hasNoJsonPath("$.none")));
    }

    @Test
    public void shouldBeDescriptive() {
        Assert.assertThat(JsonPathMatchers.hasNoJsonPath("$.name"), hasToString(equalTo("is json without json path \"$[\'name\']\"")));
    }
}

