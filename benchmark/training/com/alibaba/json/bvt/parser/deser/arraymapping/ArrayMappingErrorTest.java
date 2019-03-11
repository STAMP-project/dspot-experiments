package com.alibaba.json.bvt.parser.deser.arraymapping;


import Feature.SupportArrayToBean;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import junit.framework.TestCase;
import org.junit.Assert;


public class ArrayMappingErrorTest extends TestCase {
    public void test_for_error() throws Exception {
        Exception error = null;
        try {
            JSON.parseObject("[1001,2002]", ArrayMappingErrorTest.Model.class, SupportArrayToBean);
        } catch (JSONException ex) {
            error = ex;
        }
        Assert.assertNotNull(error);
    }

    public static class Model {
        public int id;

        public String name;
    }
}

