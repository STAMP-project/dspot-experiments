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
public class DoubleNullTest extends TestCase {
    public void test_null() throws Exception {
        DoubleNullTest.Model model = JSON.parseObject("{\"v1\":null,\"v2\":null}", DoubleNullTest.Model.class);
        TestCase.assertNotNull(model);
        TestCase.assertNull(model.v1);
        TestCase.assertNull(model.v2);
    }

    public void test_null_quote() throws Exception {
        DoubleNullTest.Model model = JSON.parseObject("{\"v1\":\"null\",\"v2\":\"null\"}", DoubleNullTest.Model.class);
        TestCase.assertNotNull(model);
        TestCase.assertNull(model.v1);
        TestCase.assertNull(model.v2);
    }

    public void test_null_1() throws Exception {
        DoubleNullTest.Model model = JSON.parseObject("{\"v1\":null ,\"v2\":null }", DoubleNullTest.Model.class);
        TestCase.assertNotNull(model);
        TestCase.assertNull(model.v1);
        TestCase.assertNull(model.v2);
    }

    public void test_null_1_quote() throws Exception {
        DoubleNullTest.Model model = JSON.parseObject("{\"v1\":\"null\" ,\"v2\":\"null\" }", DoubleNullTest.Model.class);
        TestCase.assertNotNull(model);
        TestCase.assertNull(model.v1);
        TestCase.assertNull(model.v2);
    }

    public void test_null_array() throws Exception {
        DoubleNullTest.Model model = JSON.parseObject("[\"null\" ,\"null\"]", DoubleNullTest.Model.class, SupportArrayToBean);
        TestCase.assertNotNull(model);
        TestCase.assertNull(model.v1);
        TestCase.assertNull(model.v2);
    }

    public void test_null_array_reader() throws Exception {
        JSONReader reader = new JSONReader(new StringReader("[\"null\" ,\"null\"]"), Feature.SupportArrayToBean);
        DoubleNullTest.Model model = reader.readObject(DoubleNullTest.Model.class);
        TestCase.assertNotNull(model);
        TestCase.assertNull(model.v1);
        TestCase.assertNull(model.v2);
    }

    public static class Model {
        public Double v1;

        public Double v2;
    }
}

