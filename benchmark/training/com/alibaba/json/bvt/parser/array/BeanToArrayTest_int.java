package com.alibaba.json.bvt.parser.array;


import Feature.SupportArrayToBean;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import junit.framework.TestCase;
import org.junit.Assert;


public class BeanToArrayTest_int extends TestCase {
    public void test_int() throws Exception {
        BeanToArrayTest_int.Model model = JSON.parseObject("[-100,100]", BeanToArrayTest_int.Model.class, SupportArrayToBean);
        Assert.assertEquals((-100L), model.v1);
        Assert.assertEquals(100L, model.v2);
    }

    public void test_int_space() throws Exception {
        BeanToArrayTest_int.Model model = JSON.parseObject("[-100 ,100 ]", BeanToArrayTest_int.Model.class, SupportArrayToBean);
        Assert.assertEquals((-100L), model.v1);
        Assert.assertEquals(100L, model.v2);
    }

    public void test_int_error() throws Exception {
        Exception error = null;
        try {
            JSON.parseObject("[-", BeanToArrayTest_int.Model.class, SupportArrayToBean);
        } catch (JSONException ex) {
            error = ex;
        }
        Assert.assertNotNull(error);
    }

    public void test_int_error_1() throws Exception {
        Exception error = null;
        try {
            JSON.parseObject("[-1:", BeanToArrayTest_int.Model.class, SupportArrayToBean);
        } catch (JSONException ex) {
            error = ex;
        }
        Assert.assertNotNull(error);
    }

    public void test_int_error_max() throws Exception {
        Exception error = null;
        try {
            JSON.parseObject("[1,92233720368547758000}", BeanToArrayTest_int.Model.class, SupportArrayToBean);
        } catch (JSONException ex) {
            error = ex;
        }
        Assert.assertNotNull(error);
    }

    public void test_bool_error_min() throws Exception {
        Exception error = null;
        try {
            JSON.parseObject("[1,-92233720368547758000}", BeanToArrayTest_int.Model.class, SupportArrayToBean);
        } catch (JSONException ex) {
            error = ex;
        }
        Assert.assertNotNull(error);
    }

    public static class Model {
        public int v1;

        public int v2;
    }
}

