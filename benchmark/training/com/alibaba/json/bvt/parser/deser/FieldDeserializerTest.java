package com.alibaba.json.bvt.parser.deser;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import junit.framework.TestCase;
import org.junit.Assert;


public class FieldDeserializerTest extends TestCase {
    public void test_deser() throws Exception {
        Exception error = null;
        try {
            JSON.parseObject("{'value':{}}", FieldDeserializerTest.Entity.class);
        } catch (JSONException ex) {
            error = ex;
        }
        Assert.assertNotNull(error);
    }

    private static class Entity {
        private FieldDeserializerTest.V1 value;

        public FieldDeserializerTest.V1 getValue() {
            return value;
        }

        public void setValue(FieldDeserializerTest.V1 value) {
            throw new RuntimeException();
        }
    }

    private static class V1 {}
}

