package com.jsoniter.output;


import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.jsoniter.extra.JacksonCompatibilityMode;
import java.util.HashMap;
import java.util.Map;
import junit.framework.TestCase;


public class TestJackson extends TestCase {
    static {
        // JsonStream.setMode(EncodingMode.DYNAMIC_MODE);
    }

    private ObjectMapper objectMapper;

    public static class TestObject1 {
        @JsonAnyGetter
        public Map<Integer, Object> getProperties() {
            HashMap<Integer, Object> properties = new HashMap<Integer, Object>();
            properties.put(100, "hello");
            return properties;
        }
    }

    public void test_JsonAnyGetter() throws JsonProcessingException {
        String output = objectMapper.writeValueAsString(new TestJackson.TestObject1());
        TestCase.assertEquals("{\"100\":\"hello\"}", output);
        output = JsonStream.serialize(new JacksonCompatibilityMode.Builder().build(), new TestJackson.TestObject1());
        TestCase.assertEquals("{\"100\":\"hello\"}", output);
    }

    public static class TestObject2 {
        @JsonProperty("field-1")
        public String field1;
    }

    public void test_JsonProperty() throws JsonProcessingException {
        TestJackson.TestObject2 obj = new TestJackson.TestObject2();
        obj.field1 = "hello";
        String output = objectMapper.writeValueAsString(obj);
        TestCase.assertEquals("{\"field-1\":\"hello\"}", output);
        output = JsonStream.serialize(new JacksonCompatibilityMode.Builder().build(), obj);
        TestCase.assertEquals("{\"field-1\":\"hello\"}", output);
    }

    public static class TestObject3 {
        @JsonIgnore
        public String field1;
    }

    public void test_JsonIgnore() throws JsonProcessingException {
        objectMapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
        TestJackson.TestObject3 obj = new TestJackson.TestObject3();
        obj.field1 = "hello";
        String output = objectMapper.writeValueAsString(obj);
        TestCase.assertEquals("{}", output);
        output = JsonStream.serialize(new JacksonCompatibilityMode.Builder().build(), obj);
        TestCase.assertEquals("{}", output);
    }
}

