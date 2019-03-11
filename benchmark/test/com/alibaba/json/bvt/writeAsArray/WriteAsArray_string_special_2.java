package com.alibaba.json.bvt.writeAsArray;


import Feature.SupportArrayToBean;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import junit.framework.TestCase;
import org.junit.Assert;


public class WriteAsArray_string_special_2 extends TestCase {
    public void test_0() throws Exception {
        WriteAsArray_string_special_2.Model model2 = JSON.parseObject("[\"abc\\0\\1\\2\\3\\4\\5\\6\\7\\b\\t\\n\\v\\f\\F\\r\\\'\\/\\xFF\\u000B\"]", WriteAsArray_string_special_2.Model.class, SupportArrayToBean);
        Assert.assertEquals("abc\u0000\u0001\u0002\u0003\u0004\u0005\u0006\u0007\b\t\n\u000b\f\f\r\'/\u00ff\u000b", model2.name);
    }

    public void test_error() throws Exception {
        Exception error = null;
        try {
            JSON.parseObject("[\"abc\\k\"]", WriteAsArray_string_special_2.Model.class, SupportArrayToBean);
        } catch (JSONException ex) {
            error = ex;
        }
        Assert.assertNotNull(error);
    }

    public static class Model {
        public String name;
    }
}

