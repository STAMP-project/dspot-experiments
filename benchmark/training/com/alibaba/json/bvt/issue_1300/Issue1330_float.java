package com.alibaba.json.bvt.issue_1300;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import junit.framework.TestCase;


/**
 * Created by wenshao on 30/07/2017.
 */
public class Issue1330_float extends TestCase {
    public void test_for_issue() throws Exception {
        Exception error = null;
        try {
            JSON.parseObject("{\"value\":\"ABC\"}", Issue1330_float.Model.class);
        } catch (JSONException e) {
            error = e;
        }
        TestCase.assertNotNull(error);
        TestCase.assertTrue(((error.getMessage().indexOf("parseLong error, field : value")) != (-1)));
    }

    public void test_for_issue_1() throws Exception {
        Exception error = null;
        try {
            JSON.parseObject("{\"value\":[]}", Issue1330_float.Model.class);
        } catch (JSONException e) {
            error = e;
        }
        TestCase.assertNotNull(error);
        TestCase.assertTrue(((error.getMessage().indexOf("parseLong error, field : value")) != (-1)));
    }

    public void test_for_issue_2() throws Exception {
        Exception error = null;
        try {
            JSON.parseObject("{\"value\":{}}", Issue1330_float.Model.class);
        } catch (JSONException e) {
            error = e;
        }
        TestCase.assertNotNull(error);
        TestCase.assertTrue(((error.getMessage().indexOf("parseLong error, field : value")) != (-1)));
    }

    public static class Model {
        public float value;
    }
}

