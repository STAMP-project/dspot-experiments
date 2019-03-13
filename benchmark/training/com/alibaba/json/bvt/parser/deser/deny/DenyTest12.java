package com.alibaba.json.bvt.parser.deser.deny;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import junit.framework.TestCase;


/**
 * Created by wenshao on 29/01/2017.
 */
public class DenyTest12 extends TestCase {
    public void test_deny() throws Exception {
        String text = "{\"value\":{\"@type\":\"java.lang.Thread\"}}";
        Exception error = null;
        try {
            JSON.parseObject(text, DenyTest12.Model.class);
        } catch (JSONException ex) {
            error = ex;
        }
        TestCase.assertNotNull(error);
    }

    public static class Model {
        public DenyTest12.Value value;
    }

    public static class Value {}
}

