package com.alibaba.json.bvt.parser.deser;


import Feature.IgnoreNotMatch;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import junit.framework.TestCase;
import org.junit.Assert;


public class FieldDeserializerTest1 extends TestCase {
    public void test_error() throws Exception {
        Exception error = null;
        try {
            JSON.parseObject("{\"value\":[-}", FieldDeserializerTest1.Entity.class, 0);
        } catch (JSONException ex) {
            error = ex;
        }
        Assert.assertNotNull(error);
    }

    public void test_error_1() throws Exception {
        Exception error = null;
        try {
            JSON.parseObject("{,,,\"value\":null}", FieldDeserializerTest1.Entity.class, 0);
        } catch (JSONException ex) {
            error = ex;
        }
        Assert.assertNotNull(error);
    }

    public void test_error_2() throws Exception {
        Exception error = null;
        try {
            JSON.parseObject("{\"value\":null,\"id\":123}", FieldDeserializerTest1.Entity.class, 0);
        } catch (JSONException ex) {
            error = ex;
        }
        Assert.assertNotNull(error);
    }

    public void test_null() throws Exception {
        FieldDeserializerTest1.Entity object = JSON.parseObject("{\"value\":null}", FieldDeserializerTest1.Entity.class, 0);
        Assert.assertNull(object.getValue());
    }

    public void test_null_2() throws Exception {
        FieldDeserializerTest1.Entity object = JSON.parseObject("{\"value\":null,\"id\":123}", FieldDeserializerTest1.Entity.class, 0, IgnoreNotMatch);
        Assert.assertNull(object.getValue());
    }

    private static class Entity {
        private FieldDeserializerTest1.V1 value;

        public FieldDeserializerTest1.V1 getValue() {
            return value;
        }

        public void setValue(FieldDeserializerTest1.V1 value) {
            this.value = value;
        }
    }

    private static class V1 {}
}

