package com.alibaba.json.bvt.serializer;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import junit.framework.TestCase;


public class ErrorGetterTest extends TestCase {
    public void test_0() throws Exception {
        ErrorGetterTest.Model m = new ErrorGetterTest.Model();
        Exception error = null;
        try {
            JSON.toJSONString(m);
        } catch (JSONException ex) {
            error = ex;
        }
        TestCase.assertNotNull(error);
    }

    private static class Model {
        public int getValue() {
            throw new UnsupportedOperationException();
        }
    }
}

