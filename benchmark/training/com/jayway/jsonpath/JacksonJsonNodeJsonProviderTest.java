package com.jayway.jsonpath;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.jayway.jsonpath.spi.json.JacksonJsonNodeJsonProvider;
import com.jayway.jsonpath.spi.mapper.JacksonMappingProvider;
import com.jayway.jsonpath.spi.mapper.MappingException;
import java.io.IOException;
import java.util.List;
import java.util.UUID;
import org.junit.Test;


public class JacksonJsonNodeJsonProviderTest extends BaseTest {
    private static final String JSON = "[" + (((((((((((((((((("{\n" + "   \"foo\" : \"foo0\",\n") + "   \"bar\" : 0,\n") + "   \"baz\" : true,\n") + "   \"gen\" : {\"eric\" : \"yepp\"}") + "},") + "{\n") + "   \"foo\" : \"foo1\",\n") + "   \"bar\" : 1,\n") + "   \"baz\" : true,\n") + "   \"gen\" : {\"eric\" : \"yepp\"}") + "},") + "{\n") + "   \"foo\" : \"foo2\",\n") + "   \"bar\" : 2,\n") + "   \"baz\" : true,\n") + "   \"gen\" : {\"eric\" : \"yepp\"}") + "}") + "]");

    @Test
    public void json_can_be_parsed() {
        ObjectNode node = JsonPath.using(BaseTest.JACKSON_JSON_NODE_CONFIGURATION).parse(BaseTest.JSON_DOCUMENT).read("$");
        assertThat(node.get("string-property").asText()).isEqualTo("string-value");
    }

    @Test
    public void always_return_same_object() {
        // Test because of Bug #211
        DocumentContext context = JsonPath.using(BaseTest.JACKSON_JSON_NODE_CONFIGURATION).parse(BaseTest.JSON_DOCUMENT);
        ObjectNode node1 = context.read("$");
        ObjectNode child1 = new ObjectNode(JsonNodeFactory.instance);
        child1.put("name", "test");
        context.put("$", "child", child1);
        ObjectNode node2 = context.read("$");
        ObjectNode child2 = context.read("$.child");
        assertThat(node1).isSameAs(node2);
        assertThat(child1).isSameAs(child2);
    }

    @Test
    public void strings_are_unwrapped() {
        JsonNode node = JsonPath.using(BaseTest.JACKSON_JSON_NODE_CONFIGURATION).parse(BaseTest.JSON_DOCUMENT).read("$.string-property");
        String unwrapped = JsonPath.using(BaseTest.JACKSON_JSON_NODE_CONFIGURATION).parse(BaseTest.JSON_DOCUMENT).read("$.string-property", String.class);
        assertThat(unwrapped).isEqualTo("string-value");
        assertThat(unwrapped).isEqualTo(node.asText());
    }

    @Test
    public void ints_are_unwrapped() {
        JsonNode node = JsonPath.using(BaseTest.JACKSON_JSON_NODE_CONFIGURATION).parse(BaseTest.JSON_DOCUMENT).read("$.int-max-property");
        int unwrapped = JsonPath.using(BaseTest.JACKSON_JSON_NODE_CONFIGURATION).parse(BaseTest.JSON_DOCUMENT).read("$.int-max-property", int.class);
        assertThat(unwrapped).isEqualTo(Integer.MAX_VALUE);
        assertThat(unwrapped).isEqualTo(node.asInt());
    }

    @Test
    public void longs_are_unwrapped() {
        JsonNode node = JsonPath.using(BaseTest.JACKSON_JSON_NODE_CONFIGURATION).parse(BaseTest.JSON_DOCUMENT).read("$.long-max-property");
        long unwrapped = JsonPath.using(BaseTest.JACKSON_JSON_NODE_CONFIGURATION).parse(BaseTest.JSON_DOCUMENT).read("$.long-max-property", long.class);
        assertThat(unwrapped).isEqualTo(Long.MAX_VALUE);
        assertThat(unwrapped).isEqualTo(node.asLong());
    }

    @Test
    public void list_of_numbers() {
        ArrayNode objs = JsonPath.using(BaseTest.JACKSON_JSON_NODE_CONFIGURATION).parse(BaseTest.JSON_DOCUMENT).read("$.store.book[*].display-price");
        assertThat(objs.get(0).asDouble()).isEqualTo(8.95);
        assertThat(objs.get(1).asDouble()).isEqualTo(12.99);
        assertThat(objs.get(2).asDouble()).isEqualTo(8.99);
        assertThat(objs.get(3).asDouble()).isEqualTo(22.99);
    }

    @Test
    public void test_type_ref() throws IOException {
        TypeRef<List<JacksonJsonNodeJsonProviderTest.FooBarBaz<JacksonJsonNodeJsonProviderTest.Gen>>> typeRef = new TypeRef<List<JacksonJsonNodeJsonProviderTest.FooBarBaz<JacksonJsonNodeJsonProviderTest.Gen>>>() {};
        List<JacksonJsonNodeJsonProviderTest.FooBarBaz<JacksonJsonNodeJsonProviderTest.Gen>> list = JsonPath.using(BaseTest.JACKSON_JSON_NODE_CONFIGURATION).parse(JacksonJsonNodeJsonProviderTest.JSON).read("$", typeRef);
        assertThat(list.get(0).gen.eric).isEqualTo("yepp");
    }

    @Test(expected = MappingException.class)
    public void test_type_ref_fail() throws IOException {
        TypeRef<List<JacksonJsonNodeJsonProviderTest.FooBarBaz<Integer>>> typeRef = new TypeRef<List<JacksonJsonNodeJsonProviderTest.FooBarBaz<Integer>>>() {};
        JsonPath.using(BaseTest.JACKSON_JSON_NODE_CONFIGURATION).parse(JacksonJsonNodeJsonProviderTest.JSON).read("$", typeRef);
    }

    @Test
    public void mapPropertyWithPOJO() {
        String someJson = "" + ((("{\n" + "  \"a\": \"a\",\n") + "  \"b\": \"b\"\n") + "}");
        ObjectMapper om = new ObjectMapper();
        om.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        Configuration c = Configuration.builder().mappingProvider(new JacksonMappingProvider()).jsonProvider(new JacksonJsonNodeJsonProvider(om)).build();
        DocumentContext context = JsonPath.using(c).parse(someJson);
        String someJsonStr = context.jsonString();
        DocumentContext altered = context.map("$['a', 'b', 'c']", new MapFunction() {
            @Override
            public Object map(Object currentValue, Configuration configuration) {
                return currentValue;
            }
        });
        assertThat(altered.jsonString()).isEqualTo(someJsonStr);
    }

    // https://github.com/json-path/JsonPath/issues/364
    @Test
    public void setPropertyWithPOJO() {
        DocumentContext context = JsonPath.using(BaseTest.JACKSON_JSON_NODE_CONFIGURATION).parse("{}");
        UUID uuid = UUID.randomUUID();
        context.put("$", "data", new JacksonJsonNodeJsonProviderTest.Data(uuid));
        String id = context.read("$.data.id", String.class);
        assertThat(id).isEqualTo(uuid.toString());
    }

    public static class FooBarBaz<T> {
        public T gen;

        public String foo;

        public Long bar;

        public boolean baz;
    }

    public static class Gen {
        public String eric;
    }

    public static final class Data {
        @JsonProperty("id")
        UUID id;

        @JsonCreator
        Data(@JsonProperty("id")
        final UUID id) {
            this.id = id;
        }
    }
}

