package com.alibaba.json.bvt;


import com.alibaba.fastjson.JSON;
import junit.framework.TestCase;


public class JSON_isValid_1_error extends TestCase {
    public void test_for_isValid_0() throws Exception {
        TestCase.assertFalse(JSON.isValid(null));
        TestCase.assertFalse(JSON.isValid(""));
    }

    public void test_for_isValid_value() throws Exception {
        TestCase.assertFalse(JSON.isValid("nul"));
        TestCase.assertFalse(JSON.isValid("null,null"));
        TestCase.assertFalse(JSON.isValid("123,"));
        TestCase.assertFalse(JSON.isValid("123,123"));
        TestCase.assertFalse(JSON.isValid("12.34,true"));
        TestCase.assertFalse(JSON.isValid("12.34,123"));
        TestCase.assertFalse(JSON.isValid("tru"));
        TestCase.assertFalse(JSON.isValid("true,123"));
        TestCase.assertFalse(JSON.isValid("fals"));
        TestCase.assertFalse(JSON.isValid("false,123"));
        TestCase.assertFalse(JSON.isValid("\"abc"));
        TestCase.assertFalse(JSON.isValid("\"abc\",123"));
    }

    public void test_for_isValid_obj() throws Exception {
        TestCase.assertFalse(JSON.isValid("{"));
        TestCase.assertFalse(JSON.isValid("{\"id\":123,}}"));
        TestCase.assertFalse(JSON.isValid("{\"id\":\"123}"));
        TestCase.assertFalse(JSON.isValid("{\"id\":{]}"));
        TestCase.assertFalse(JSON.isValid("{\"id\":{"));
    }

    public void test_for_isValid_obj_1() throws Exception {
        TestCase.assertFalse(JSON.isValidObject("{"));
        TestCase.assertFalse(JSON.isValidObject("{\"id\":123,}}"));
        TestCase.assertFalse(JSON.isValidObject("{\"id\":\"123}"));
        TestCase.assertFalse(JSON.isValidObject("{\"id\":{]}"));
        TestCase.assertFalse(JSON.isValidObject("{\"id\":{"));
    }

    public void test_for_isValid_array() throws Exception {
        TestCase.assertFalse(JSON.isValid("["));
        TestCase.assertFalse(JSON.isValid("[[,[]]"));
        TestCase.assertFalse(JSON.isValid("[{\"id\":123]"));
        TestCase.assertFalse(JSON.isValid("[{\"id\":\"123\"}"));
        TestCase.assertFalse(JSON.isValid("[{\"id\":true]"));
        TestCase.assertFalse(JSON.isValid("[{\"id\":{}]"));
    }

    public void test_for_isValid_array_1() throws Exception {
        TestCase.assertFalse(JSON.isValidArray("["));
        TestCase.assertFalse(JSON.isValidArray("[[,[]]"));
        TestCase.assertFalse(JSON.isValidArray("[{\"id\":123]"));
        TestCase.assertFalse(JSON.isValidArray("[{\"id\":\"123\"}"));
        TestCase.assertFalse(JSON.isValidArray("[{\"id\":true]"));
        TestCase.assertFalse(JSON.isValidArray("[{\"id\":{}]"));
    }
}

