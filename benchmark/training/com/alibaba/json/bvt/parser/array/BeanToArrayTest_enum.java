package com.alibaba.json.bvt.parser.array;


import Feature.SupportArrayToBean;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import junit.framework.TestCase;
import org.junit.Assert;


public class BeanToArrayTest_enum extends TestCase {
    public void test_enum() throws Exception {
        BeanToArrayTest_enum.Model model = JSON.parseObject("[\"A\",\"B\"]", BeanToArrayTest_enum.Model.class, SupportArrayToBean);
        Assert.assertEquals(BeanToArrayTest_enum.Type.A, model.v1);
        Assert.assertEquals(BeanToArrayTest_enum.Type.B, model.v2);
    }

    public void test_enum_space() throws Exception {
        BeanToArrayTest_enum.Model model = JSON.parseObject("[\"A\" ,\"B\" ]", BeanToArrayTest_enum.Model.class, SupportArrayToBean);
        Assert.assertEquals(BeanToArrayTest_enum.Type.A, model.v1);
        Assert.assertEquals(BeanToArrayTest_enum.Type.B, model.v2);
    }

    public void test_enum_num() throws Exception {
        BeanToArrayTest_enum.Model model = JSON.parseObject("[1,0]", BeanToArrayTest_enum.Model.class, SupportArrayToBean);
        Assert.assertEquals(BeanToArrayTest_enum.Type.B, model.v1);
        Assert.assertEquals(BeanToArrayTest_enum.Type.A, model.v2);
    }

    public void test_enum_error() throws Exception {
        Exception error = null;
        try {
            JSON.parseObject("[t,0]", BeanToArrayTest_enum.Model.class, SupportArrayToBean);
        } catch (JSONException ex) {
            error = ex;
        }
        Assert.assertNotNull(error);
    }

    public static class Model {
        public BeanToArrayTest_enum.Type v1;

        public BeanToArrayTest_enum.Type v2;
    }

    public static enum Type {

        A,
        B,
        C;}
}

