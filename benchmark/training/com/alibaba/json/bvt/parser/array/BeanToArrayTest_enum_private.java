package com.alibaba.json.bvt.parser.array;


import Feature.SupportArrayToBean;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import junit.framework.TestCase;
import org.junit.Assert;


public class BeanToArrayTest_enum_private extends TestCase {
    public void test_enum() throws Exception {
        BeanToArrayTest_enum_private.Model model = JSON.parseObject("[\"A\",\"B\"]", BeanToArrayTest_enum_private.Model.class, SupportArrayToBean);
        Assert.assertEquals(BeanToArrayTest_enum_private.Type.A, model.v1);
        Assert.assertEquals(BeanToArrayTest_enum_private.Type.B, model.v2);
    }

    public void test_enum_space() throws Exception {
        BeanToArrayTest_enum_private.Model model = JSON.parseObject("[\"A\" ,\"B\" ]", BeanToArrayTest_enum_private.Model.class, SupportArrayToBean);
        Assert.assertEquals(BeanToArrayTest_enum_private.Type.A, model.v1);
        Assert.assertEquals(BeanToArrayTest_enum_private.Type.B, model.v2);
    }

    public void test_enum_num() throws Exception {
        BeanToArrayTest_enum_private.Model model = JSON.parseObject("[1,0]", BeanToArrayTest_enum_private.Model.class, SupportArrayToBean);
        Assert.assertEquals(BeanToArrayTest_enum_private.Type.B, model.v1);
        Assert.assertEquals(BeanToArrayTest_enum_private.Type.A, model.v2);
    }

    public void test_enum_error() throws Exception {
        Exception error = null;
        try {
            JSON.parseObject("[t,0]", BeanToArrayTest_enum_private.Model.class, SupportArrayToBean);
        } catch (JSONException ex) {
            error = ex;
        }
        Assert.assertNotNull(error);
    }

    private static class Model {
        public BeanToArrayTest_enum_private.Type v1;

        public BeanToArrayTest_enum_private.Type v2;
    }

    private static enum Type {

        A,
        B,
        C;}
}

