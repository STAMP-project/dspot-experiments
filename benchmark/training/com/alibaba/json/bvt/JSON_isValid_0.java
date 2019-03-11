package com.alibaba.json.bvt;


import com.alibaba.fastjson.JSON;
import junit.framework.TestCase;


public class JSON_isValid_0 extends TestCase {
    public void test_for_isValid_0() throws Exception {
        TestCase.assertFalse(JSON.isValid(null));
        TestCase.assertFalse(JSON.isValid(""));
    }

    public void test_for_isValid_value() throws Exception {
        TestCase.assertTrue(JSON.isValid("null"));
        TestCase.assertTrue(JSON.isValid("123"));
        TestCase.assertTrue(JSON.isValid("12.34"));
        TestCase.assertTrue(JSON.isValid("true"));
        TestCase.assertTrue(JSON.isValid("false"));
        TestCase.assertTrue(JSON.isValid("\"abc\""));
    }

    public void test_for_isValid_obj() throws Exception {
        TestCase.assertTrue(JSON.isValid("{}"));
        TestCase.assertTrue(JSON.isValid("{\"id\":123}"));
        TestCase.assertTrue(JSON.isValid("{\"id\":\"123\"}"));
        TestCase.assertTrue(JSON.isValid("{\"id\":true}"));
        TestCase.assertTrue(JSON.isValid("{\"id\":{}}"));
    }

    public void test_for_isValid_obj_1() throws Exception {
        TestCase.assertTrue(JSON.isValidObject("{}"));
        TestCase.assertTrue(JSON.isValidObject("{\"id\":123}"));
        TestCase.assertTrue(JSON.isValidObject("{\"id\":\"123\"}"));
        TestCase.assertTrue(JSON.isValidObject("{\"id\":true}"));
        TestCase.assertTrue(JSON.isValidObject("{\"id\":{}}"));
    }

    public void test_for_isValid_array() throws Exception {
        TestCase.assertTrue(JSON.isValid("[]"));
        TestCase.assertTrue(JSON.isValid("[[],[]]"));
        TestCase.assertTrue(JSON.isValid("[{\"id\":123}]"));
        TestCase.assertTrue(JSON.isValid("[{\"id\":\"123\"}]"));
        TestCase.assertTrue(JSON.isValid("[{\"id\":true}]"));
        TestCase.assertTrue(JSON.isValid("[{\"id\":{}}]"));
    }

    public void test_for_isValid_array_1() throws Exception {
        TestCase.assertTrue(JSON.isValidArray("[]"));
        TestCase.assertTrue(JSON.isValidArray("[[],[]]"));
        TestCase.assertTrue(JSON.isValidArray("[{\"id\":123}]"));
        TestCase.assertTrue(JSON.isValidArray("[{\"id\":\"123\"}]"));
        TestCase.assertTrue(JSON.isValidArray("[{\"id\":true}]"));
        TestCase.assertTrue(JSON.isValidArray("[{\"id\":{}}]"));
    }
}

