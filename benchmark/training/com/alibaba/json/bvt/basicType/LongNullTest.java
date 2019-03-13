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
public class LongNullTest extends TestCase {
    public void test_null() throws Exception {
        LongNullTest.Model model = JSON.parseObject("{\"v1\":null,\"v2\":null}", LongNullTest.Model.class);
        TestCase.assertNotNull(model);
        TestCase.assertNull(model.v1);
        TestCase.assertNull(model.v2);
    }

    public void test_null_quote() throws Exception {
        LongNullTest.Model model = JSON.parseObject("{\"v1\":\"null\",\"v2\":\"null\"}", LongNullTest.Model.class);
        TestCase.assertNotNull(model);
        TestCase.assertNull(model.v1);
        TestCase.assertNull(model.v2);
    }

    public void test_null_1() throws Exception {
        LongNullTest.Model model = JSON.parseObject("{\"v1\":null ,\"v2\":null }", LongNullTest.Model.class);
        TestCase.assertNotNull(model);
        TestCase.assertNull(model.v1);
        TestCase.assertNull(model.v2);
    }

    public void test_null_1_quote() throws Exception {
        LongNullTest.Model model = JSON.parseObject("{\"v1\":\"null\" ,\"v2\":\"null\" }", LongNullTest.Model.class);
        TestCase.assertNotNull(model);
        TestCase.assertNull(model.v1);
        TestCase.assertNull(model.v2);
    }

    public void test_null_array() throws Exception {
        LongNullTest.Model model = JSON.parseObject("[\"null\" ,\"null\"]", LongNullTest.Model.class, SupportArrayToBean);
        TestCase.assertNotNull(model);
        TestCase.assertNull(model.v1);
        TestCase.assertNull(model.v2);
    }

    public void test_null_array_reader() throws Exception {
        JSONReader reader = new JSONReader(new StringReader("[\"null\" ,\"null\"]"), Feature.SupportArrayToBean);
        LongNullTest.Model model = reader.readObject(LongNullTest.Model.class);
        TestCase.assertNotNull(model);
        TestCase.assertNull(model.v1);
        TestCase.assertNull(model.v2);
    }

    public void test_null_array_reader_1() throws Exception {
        JSONReader reader = new JSONReader(new StringReader("[null ,null]"), Feature.SupportArrayToBean);
        LongNullTest.Model model = reader.readObject(LongNullTest.Model.class);
        TestCase.assertNotNull(model);
        TestCase.assertNull(model.v1);
        TestCase.assertNull(model.v2);
    }

    public static class Model {
        public Long v1;

        public Long v2;
    }
}

