package com.alibaba.json.bvt.parser.array;


import Feature.SupportArrayToBean;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import junit.framework.TestCase;
import org.junit.Assert;


public class BeanToArrayTest2 extends TestCase {
    public void test_bool() throws Exception {
        BeanToArrayTest2.Model model = JSON.parseObject("[true,false]", BeanToArrayTest2.Model.class, SupportArrayToBean);
        Assert.assertEquals(true, model.v1);
        Assert.assertEquals(false, model.v2);
    }

    public void test_bool_space() throws Exception {
        BeanToArrayTest2.Model model = JSON.parseObject("[true ,false ]", BeanToArrayTest2.Model.class, SupportArrayToBean);
        Assert.assertEquals(true, model.v1);
        Assert.assertEquals(false, model.v2);
    }

    public void test_bool_num() throws Exception {
        BeanToArrayTest2.Model model = JSON.parseObject("[1,0]", BeanToArrayTest2.Model.class, SupportArrayToBean);
        Assert.assertEquals(true, model.v1);
        Assert.assertEquals(false, model.v2);
    }

    public void test_bool_error() throws Exception {
        Exception error = null;
        try {
            JSON.parseObject("[t,0]", BeanToArrayTest2.Model.class, SupportArrayToBean);
        } catch (JSONException ex) {
            error = ex;
        }
        Assert.assertNotNull(error);
    }

    public static class Model {
        public boolean v1;

        public boolean v2;
    }
}

