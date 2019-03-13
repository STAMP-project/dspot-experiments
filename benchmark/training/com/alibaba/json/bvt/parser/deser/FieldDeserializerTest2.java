package com.alibaba.json.bvt.parser.deser;


import Feature.AllowArbitraryCommas;
import Feature.IgnoreNotMatch;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.parser.DefaultJSONParser;
import com.alibaba.fastjson.parser.ParserConfig;
import junit.framework.TestCase;
import org.junit.Assert;


public class FieldDeserializerTest2 extends TestCase {
    public void test_0() throws Exception {
        String input = "{,,,\"value\":null,,,,}";
        int featureValues = 0;
        featureValues |= AllowArbitraryCommas.getMask();
        DefaultJSONParser parser = new DefaultJSONParser(input, ParserConfig.getGlobalInstance(), featureValues);
        FieldDeserializerTest2.Entity object = new FieldDeserializerTest2.Entity();
        parser.parseObject(object);
    }

    public void test_1() throws Exception {
        String input = "{,,,\"value\":null,\"id\":123,,,,}";
        int featureValues = 0;
        featureValues |= AllowArbitraryCommas.getMask();
        featureValues |= IgnoreNotMatch.getMask();
        DefaultJSONParser parser = new DefaultJSONParser(input, ParserConfig.getGlobalInstance(), featureValues);
        FieldDeserializerTest2.Entity object = new FieldDeserializerTest2.Entity();
        parser.parseObject(object);
    }

    public void test_error_1() throws Exception {
        Exception error = null;
        try {
            String input = "{\"value\":null,\"id\":123}";
            int featureValues = 0;
            DefaultJSONParser parser = new DefaultJSONParser(input, ParserConfig.getGlobalInstance(), featureValues);
            FieldDeserializerTest2.Entity object = new FieldDeserializerTest2.Entity();
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
            this.value = value;
        }
    }
}

