package com.alibaba.json.bvt.parser.deser;


import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import junit.framework.TestCase;
import org.junit.Assert;


public class ConstructorErrorTest_inner extends TestCase {
    public void test_error() throws Exception {
        JSONObject obj = new JSONObject();
        obj.put("value", new JSONObject());
        Exception error = null;
        try {
            obj.toJavaObject(ConstructorErrorTest_inner.Model.class);
        } catch (JSONException ex) {
            error = ex;
        }
        Assert.assertNotNull(error);
    }

    public static class Model {
        public ConstructorErrorTest_inner.Model.Value value;

        public Model() {
        }

        public class Value {
            public Value() {
                throw new IllegalStateException();
            }
        }
    }
}

