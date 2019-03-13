package com.alibaba.json.bvt.basicType;


import Feature.SupportArrayToBean;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONReader;
import com.alibaba.fastjson.parser.Feature;
import java.io.StringReader;
import junit.framework.TestCase;


/**
 * Created by wenshao on 10/08/2017.
 */
public class FloatNullTest extends TestCase {
    public void test_null() throws Exception {
        FloatNullTest.Model model = JSON.parseObject("{\"v1\":null,\"v2\":null}", FloatNullTest.Model.class);
        TestCase.assertNotNull(model);
        TestCase.assertNull(model.v1);
        TestCase.assertNull(model.v2);
    }

    public void test_null_quote() throws Exception {
        FloatNullTest.Model model = JSON.parseObject("{\"v1\":\"null\",\"v2\":\"null\"}", FloatNullTest.Model.class);
        TestCase.assertNotNull(model);
        TestCase.assertNull(model.v1);
        TestCase.assertNull(model.v2);
    }

    public void test_null_1() throws Exception {
        FloatNullTest.Model model = JSON.parseObject("{\"v1\":null ,\"v2\":null }", FloatNullTest.Model.class);
        TestCase.assertNotNull(model);
        TestCase.assertNull(model.v1);
        TestCase.assertNull(model.v2);
    }

    public void test_null_1_quote() throws Exception {
        FloatNullTest.Model model = JSON.parseObject("{\"v1\":\"null\" ,\"v2\":\"null\" }", FloatNullTest.Model.class);
        TestCase.assertNotNull(model);
        TestCase.assertNull(model.v1);
        TestCase.assertNull(model.v2);
    }

    public void test_null_array() throws Exception {
        FloatNullTest.Model model = JSON.parseObject("[\"null\" ,\"null\"]", FloatNullTest.Model.class, SupportArrayToBean);
        TestCase.assertNotNull(model);
        TestCase.assertNull(model.v1);
        TestCase.assertNull(model.v2);
    }

    public void test_null_array_reader() throws Exception {
        JSONReader reader = new JSONReader(new StringReader("[\"null\" ,\"null\"]"), Feature.SupportArrayToBean);
        FloatNullTest.Model model = reader.readObject(FloatNullTest.Model.class);
        TestCase.assertNotNull(model);
        TestCase.assertNull(model.v1);
        TestCase.assertNull(model.v2);
    }

    public static class Model {
        public Float v1;

        public Float v2;
    }
}

