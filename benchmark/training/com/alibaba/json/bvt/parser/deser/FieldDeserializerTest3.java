package com.alibaba.json.bvt.parser.deser;


import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.parser.DefaultJSONParser;
import com.alibaba.fastjson.parser.ParserConfig;
import junit.framework.TestCase;
import org.junit.Assert;


public class FieldDeserializerTest3 extends TestCase {
    public void test_error_1() throws Exception {
        Exception error = null;
        try {
            String input = "{\"value\":null}";
            int featureValues = 0;
            DefaultJSONParser parser = new DefaultJSONParser(input, ParserConfig.getGlobalInstance(), featureValues);
            FieldDeserializerTest3.Entity object = new FieldDeserializerTest3.Entity();
            parser.parseObject(object);
        } catch (JSONException ex) {
            error = ex;
        }
        Assert.assertNotNull(error);
    }

    public void test_error_2() throws Exception {
        Exception error = null;
        try {
            String input = "{,,\"value\":null}";
            int featureValues = 0;
            DefaultJSONParser parser = new DefaultJSONParser(input, ParserConfig.getGlobalInstance(), featureValues);
            FieldDeserializerTest3.Entity object = new FieldDeserializerTest3.Entity();
            parser.parseObject(object);
        } catch (JSONException ex) {
            error = ex;
        }
        Assert.assertNotNull(error);
    }

    private static class Entity {
        private String value;

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            throw new RuntimeException();
        }
    }
}

