package com.alibaba.json.bvt.parser.deser;


import Feature.InitStringFieldAsEmpty;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import junit.framework.TestCase;
import org.junit.Assert;


public class ConstructorErrorTest_initError_private extends TestCase {
    public void test_error() throws Exception {
        Exception error = null;
        try {
            JSON.parseObject("{}", ConstructorErrorTest_initError_private.Model.class, InitStringFieldAsEmpty);
        } catch (JSONException ex) {
            error = ex;
        }
        Assert.assertNotNull(error);
    }

    private static class Model {
        public Model() {
        }

        public void setName(String name) {
            throw new IllegalStateException();
        }
    }
}

