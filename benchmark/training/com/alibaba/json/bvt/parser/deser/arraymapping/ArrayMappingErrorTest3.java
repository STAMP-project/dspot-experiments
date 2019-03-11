package com.alibaba.json.bvt.parser.deser.arraymapping;


import Feature.SupportArrayToBean;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import junit.framework.TestCase;
import org.junit.Assert;


public class ArrayMappingErrorTest3 extends TestCase {
    public void test_for_error() throws Exception {
        Exception error = null;
        try {
            JSON.parseObject("[1001,{}}", ArrayMappingErrorTest3.Model.class, SupportArrayToBean);
        } catch (JSONException ex) {
            error = ex;
        }
        Assert.assertNotNull(error);
    }

    public static class Model {
        public int id;

        public JSONObject obj;

        public JSONObject obj2;
    }
}

