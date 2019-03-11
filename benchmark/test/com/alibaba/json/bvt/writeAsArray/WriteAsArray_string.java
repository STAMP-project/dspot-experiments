package com.alibaba.json.bvt.writeAsArray;


import Feature.SupportArrayToBean;
import SerializerFeature.BeanToArray;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import junit.framework.TestCase;
import org.junit.Assert;


public class WriteAsArray_string extends TestCase {
    public void test_0() throws Exception {
        WriteAsArray_string.Model model = new WriteAsArray_string.Model();
        String text = JSON.toJSONString(model, BeanToArray);
        Assert.assertEquals("[null]", text);
        WriteAsArray_string.Model model2 = JSON.parseObject(text, WriteAsArray_string.Model.class, SupportArrayToBean);
        Assert.assertNull(model2.name);
    }

    public void test_1() throws Exception {
        WriteAsArray_string.Model model = new WriteAsArray_string.Model();
        model.name = "abc";
        String text = JSON.toJSONString(model, BeanToArray);
        Assert.assertEquals("[\"abc\"]", text);
        WriteAsArray_string.Model model2 = JSON.parseObject(text, WriteAsArray_string.Model.class, SupportArrayToBean);
        Assert.assertEquals(model.name, model2.name);
    }

    public void test_error_0() throws Exception {
        Exception error = null;
        try {
            JSON.parseObject("[n", WriteAsArray_string.Model.class, SupportArrayToBean);
        } catch (JSONException ex) {
            error = ex;
        }
        Assert.assertNotNull(error);
    }

    public void test_error_1() throws Exception {
        Exception error = null;
        try {
            JSON.parseObject("[nu", WriteAsArray_string.Model.class, SupportArrayToBean);
        } catch (JSONException ex) {
            error = ex;
        }
        Assert.assertNotNull(error);
    }

    public void test_error_2() throws Exception {
        Exception error = null;
        try {
            JSON.parseObject("[nul", WriteAsArray_string.Model.class, SupportArrayToBean);
        } catch (JSONException ex) {
            error = ex;
        }
        Assert.assertNotNull(error);
    }

    public void test_error_3() throws Exception {
        Exception error = null;
        try {
            JSON.parseObject("[null", WriteAsArray_string.Model.class, SupportArrayToBean);
        } catch (JSONException ex) {
            error = ex;
        }
        Assert.assertNotNull(error);
    }

    public void test_error_4() throws Exception {
        Exception error = null;
        try {
            JSON.parseObject("[\"ab", WriteAsArray_string.Model.class, SupportArrayToBean);
        } catch (JSONException ex) {
            error = ex;
        }
        Assert.assertNotNull(error);
    }

    public void test_error_5() throws Exception {
        Exception error = null;
        try {
            JSON.parseObject("[\"ab\"", WriteAsArray_string.Model.class, SupportArrayToBean);
        } catch (JSONException ex) {
            error = ex;
        }
        Assert.assertNotNull(error);
    }

    public static class Model {
        public String name;
    }
}

