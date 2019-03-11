package com.alibaba.json.bvt.path;


import com.alibaba.fastjson.JSONPath;
import java.util.HashMap;
import java.util.Map;
import junit.framework.TestCase;
import org.junit.Assert;


public class JSONPath_set_test3 extends TestCase {
    public void test_jsonpath_leve_1() throws Exception {
        Map<String, Object> root = new HashMap<String, Object>();
        JSONPath.set(root, "/id", 1001);
        Assert.assertEquals(1001, JSONPath.eval(root, "/id"));
    }

    public void test_jsonpath() throws Exception {
        Map<String, Object> root = new HashMap<String, Object>();
        JSONPath.set(root, "/a/b/id", 1001);
        Assert.assertEquals(1001, JSONPath.eval(root, "a/b/id"));
    }
}

