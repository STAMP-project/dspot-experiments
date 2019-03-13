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
public class IntegerNullTest extends TestCase {
    public void test_null() throws Exception {
        IntegerNullTest.Model model = JSON.parseObject("{\"v1\":null,\"v2\":null}", IntegerNullTest.Model.class);
        TestCase.assertNotNull(model);
        TestCase.assertNull(model.v1);
        TestCase.assertNull(model.v2);
    }

    public void test_null_quote() throws Exception {
        IntegerNullTest.Model model = JSON.parseObject("{\"v1\":\"null\",\"v2\":\"null\"}", IntegerNullTest.Model.class);
        TestCase.assertNotNull(model);
        TestCase.assertNull(model.v1);
        TestCase.assertNull(model.v2);
    }

    public void test_null_1() throws Exception {
        IntegerNullTest.Model model = JSON.parseObject("{\"v1\":null ,\"v2\":null }", IntegerNullTest.Model.class);
        TestCase.assertNotNull(model);
        TestCase.assertNull(model.v1);
        TestCase.assertNull(model.v2);
    }

    public void test_null_1_quote() throws Exception {
        IntegerNullTest.Model model = JSON.parseObject("{\"v1\":\"null\" ,\"v2\":\"null\" }", IntegerNullTest.Model.class);
        TestCase.assertNotNull(model);
        TestCase.assertNull(model.v1);
        TestCase.assertNull(model.v2);
    }

    public void test_null_array() throws Exception {
        IntegerNullTest.Model model = JSON.parseObject("[\"null\" ,\"null\"]", IntegerNullTest.Model.class, SupportArrayToBean);
        TestCase.assertNotNull(model);
        TestCase.assertNull(model.v1);
        TestCase.assertNull(model.v2);
    }

    public void test_null_array_reader() throws Exception {
        JSONReader reader = new JSONReader(new StringReader("[\"null\" ,\"null\"]"), Feature.SupportArrayToBean);
        IntegerNullTest.Model model = reader.readObject(IntegerNullTest.Model.class);
        TestCase.assertNotNull(model);
        TestCase.assertNull(model.v1);
        TestCase.assertNull(model.v2);
    }

    public void test_null_array_reader_1() throws Exception {
        JSONReader reader = new JSONReader(new StringReader("[null ,null]"), Feature.SupportArrayToBean);
        IntegerNullTest.Model model = reader.readObject(IntegerNullTest.Model.class);
        TestCase.assertNotNull(model);
        TestCase.assertNull(model.v1);
        TestCase.assertNull(model.v2);
    }

    public static class Model {
        public Integer v1;

        public Integer v2;
    }
}

