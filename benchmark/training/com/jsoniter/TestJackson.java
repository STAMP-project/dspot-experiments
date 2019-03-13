package com.jsoniter;


import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jsoniter.extra.JacksonCompatibilityMode;
import java.io.IOException;
import junit.framework.TestCase;


public class TestJackson extends TestCase {
    static {
        // JsonIterator.setMode(DecodingMode.DYNAMIC_MODE_AND_MATCH_FIELD_WITH_HASH);
    }

    private ObjectMapper objectMapper;

    public static class TestObject1 {
        private int _id;

        private String _name;

        @JsonAnySetter
        public void setProperties(String key, Object value) {
            if (key.equals("name")) {
                _name = ((String) (value));
            } else
                if (key.equals("id")) {
                    _id = ((Number) (value)).intValue();
                }

        }
    }

    public void test_JsonAnySetter() throws IOException {
        TestJackson.TestObject1 obj = objectMapper.readValue("{\"name\":\"hello\",\"id\":100}", TestJackson.TestObject1.class);
        TestCase.assertEquals("hello", obj._name);
        TestCase.assertEquals(100, obj._id);
        obj = JsonIterator.deserialize(new JacksonCompatibilityMode.Builder().build(), "{\"name\":\"hello\",\"id\":100}", TestJackson.TestObject1.class);
        TestCase.assertEquals("hello", obj._name);
        TestCase.assertEquals(100, obj._id);
    }

    public static class TestObject2 {
        @JsonProperty("field-1")
        public String field1;
    }

    public void test_JsonProperty() throws IOException {
        TestJackson.TestObject2 obj = objectMapper.readValue("{\"field-1\":\"hello\"}", TestJackson.TestObject2.class);
        TestCase.assertEquals("hello", obj.field1);
        obj = JsonIterator.deserialize(new JacksonCompatibilityMode.Builder().build(), "{\"field-1\":\"hello\"}", TestJackson.TestObject2.class);
        TestCase.assertEquals("hello", obj.field1);
    }

    public static class TestObject3 {
        @JsonIgnore
        public String field1;
    }

    public void test_JsonIgnore() throws IOException {
        TestJackson.TestObject3 obj = objectMapper.readValue("{\"field1\":\"hello\"}", TestJackson.TestObject3.class);
        TestCase.assertNull(obj.field1);
        obj = JsonIterator.deserialize(new JacksonCompatibilityMode.Builder().build(), "{\"field1\":\"hello\"}", TestJackson.TestObject3.class);
        TestCase.assertNull(obj.field1);
    }
}

