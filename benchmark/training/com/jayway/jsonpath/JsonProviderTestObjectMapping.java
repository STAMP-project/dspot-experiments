package com.jayway.jsonpath;


import java.io.IOException;
import java.util.List;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;


@RunWith(Parameterized.class)
public class JsonProviderTestObjectMapping extends BaseTest {
    private static final String JSON = "[" + (((((((((((((((((("{\n" + "   \"foo\" : \"foo0\",\n") + "   \"bar\" : 0,\n") + "   \"baz\" : true,\n") + "   \"gen\" : {\"prop\" : \"yepp0\"}") + "},") + "{\n") + "   \"foo\" : \"foo1\",\n") + "   \"bar\" : 1,\n") + "   \"baz\" : true,\n") + "   \"gen\" : {\"prop\" : \"yepp1\"}") + "},") + "{\n") + "   \"foo\" : \"foo2\",\n") + "   \"bar\" : 2,\n") + "   \"baz\" : true,\n") + "   \"gen\" : {\"prop\" : \"yepp2\"}") + "}") + "]");

    private final Configuration conf;

    public JsonProviderTestObjectMapping(Configuration conf) {
        this.conf = conf;
    }

    @Test
    public void list_of_numbers() {
        TypeRef<List<Double>> typeRef = new TypeRef<List<Double>>() {};
        assertThat(JsonPath.using(conf).parse(BaseTest.JSON_DOCUMENT).read("$.store.book[*].display-price", typeRef)).containsExactly(8.95, 12.99, 8.99, 22.99);
    }

    @Test
    public void test_type_ref() throws IOException {
        TypeRef<List<JsonProviderTestObjectMapping.FooBarBaz<JsonProviderTestObjectMapping.Sub>>> typeRef = new TypeRef<List<JsonProviderTestObjectMapping.FooBarBaz<JsonProviderTestObjectMapping.Sub>>>() {};
        assertThat(JsonPath.using(conf).parse(JsonProviderTestObjectMapping.JSON).read("$", typeRef)).extracting("foo").containsExactly("foo0", "foo1", "foo2");
    }

    public static class FooBarBaz<T> {
        public T gen;

        public String foo;

        public Long bar;

        public boolean baz;
    }

    public static class Sub {
        public String prop;
    }
}

