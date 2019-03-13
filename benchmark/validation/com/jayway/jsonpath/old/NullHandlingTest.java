package com.jayway.jsonpath.old;


import Option.DEFAULT_PATH_LEAF_TO_NULL;
import Option.SUPPRESS_EXCEPTIONS;
import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.PathNotFoundException;
import java.util.List;
import junit.framework.Assert;
import org.assertj.core.api.Assertions;
import org.junit.Test;


public class NullHandlingTest {
    public static final String DOCUMENT = "{\n" + (((((((((((((((((("   \"root-property\": \"root-property-value\",\n" + "   \"root-property-null\": null,\n") + "   \"children\": [\n") + "      {\n") + "         \"id\": 0,\n") + "         \"name\": \"name-0\",\n") + "         \"age\": 0\n") + "      },\n") + "      {\n") + "         \"id\": 1,\n") + "         \"name\": \"name-1\",\n") + "         \"age\": null") + "      },\n") + "      {\n") + "         \"id\": 3,\n") + "         \"name\": \"name-3\"\n") + "      }\n") + "   ]\n") + "}");

    @Test(expected = PathNotFoundException.class)
    public void not_defined_property_throws_PathNotFoundException() {
        JsonPath.read(NullHandlingTest.DOCUMENT, "$.children[0].child.age");
    }

    @Test
    public void last_token_defaults_to_null() {
        Configuration configuration = Configuration.builder().options(DEFAULT_PATH_LEAF_TO_NULL).build();
        Assert.assertNull(JsonPath.parse(NullHandlingTest.DOCUMENT, configuration).read("$.children[2].age"));
    }

    @Test
    public void null_property_returns_null() {
        Integer age = JsonPath.read(NullHandlingTest.DOCUMENT, "$.children[1].age");
        Assert.assertEquals(null, age);
    }

    @Test
    public void the_age_of_all_with_age_defined() {
        // List<Integer> result = JsonPath.read(DOCUMENT, "$.children[*].age");
        List<Integer> result = JsonPath.using(Configuration.defaultConfiguration().setOptions(SUPPRESS_EXCEPTIONS)).parse(NullHandlingTest.DOCUMENT).read("$.children[*].age");
        Assertions.assertThat(result).containsSequence(0, null);
    }

    @Test
    public void path2() {
        List<Object> result = JsonPath.read("{\"a\":[{\"b\":1,\"c\":2},{\"b\":5,\"c\":2}]}", "a[?(@.b==4)].c");
        Assertions.assertThat(result).isEmpty();
    }

    @Test
    public void path() {
        String json = "{\"a\":[{\"b\":1,\"c\":2},{\"b\":5,\"c\":2}]}";
        List<Object> result = JsonPath.using(Configuration.defaultConfiguration().setOptions(DEFAULT_PATH_LEAF_TO_NULL)).parse(json).read("a[?(@.b==5)].d");
        Assertions.assertThat(result).hasSize(1);
        Assertions.assertThat(result.get(0)).isNull();
    }
}

