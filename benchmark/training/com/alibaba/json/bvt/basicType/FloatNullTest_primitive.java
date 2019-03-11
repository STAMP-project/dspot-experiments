package com.alibaba.json.bvt.basicType;


import com.alibaba.fastjson.JSON;
import junit.framework.TestCase;


/**
 * Created by wenshao on 10/08/2017.
 */
public class FloatNullTest_primitive extends TestCase {
    public void test_null() throws Exception {
        FloatNullTest_primitive.Model model = JSON.parseObject("{\"v1\":null,\"v2\":null}", FloatNullTest_primitive.Model.class);
        TestCase.assertNotNull(model);
        TestCase.assertEquals(0.0F, model.v1);
        TestCase.assertEquals(0.0F, model.v2);
    }

    public void test_null_1() throws Exception {
        FloatNullTest_primitive.Model model = JSON.parseObject("{\"v1\":null ,\"v2\":null }", FloatNullTest_primitive.Model.class);
        TestCase.assertNotNull(model);
        TestCase.assertEquals(0.0F, model.v1);
        TestCase.assertEquals(0.0F, model.v2);
    }

    public void test_null_2() throws Exception {
        FloatNullTest_primitive.Model model = JSON.parseObject("{\"v1\":\"null\",\"v2\":\"null\" }", FloatNullTest_primitive.Model.class);
        TestCase.assertNotNull(model);
        TestCase.assertEquals(0.0F, model.v1);
        TestCase.assertEquals(0.0F, model.v2);
    }

    public static class Model {
        public float v1;

        public float v2;
    }
}

