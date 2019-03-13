package com.alibaba.json.bvt.path;


import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.JSONPath;
import java.math.BigDecimal;
import java.util.List;
import junit.framework.TestCase;
import org.junit.Assert;


@SuppressWarnings("unchecked")
public class JSONPointTest_0 extends TestCase {
    private JSONObject json;

    public void test_list() throws Exception {
        List<Object> list = ((List<Object>) (JSONPath.eval(json, "/foo")));
        Assert.assertEquals(2, list.size());
        Assert.assertEquals("bar", list.get(0));
        Assert.assertEquals("baz", list.get(1));
    }

    public void test_list_0() throws Exception {
        Object val = JSONPath.eval(json, "/foo/0");
        Assert.assertEquals("bar", val);
    }

    public void test_list_1() throws Exception {
        Object val = JSONPath.eval(json, "/foo/1");
        Assert.assertEquals("baz", val);
    }

    public void test_key() throws Exception {
        Object val = JSONPath.eval(json, "/pi");
        Assert.assertEquals(new BigDecimal("3.1416"), val);
    }

    public void test_key_1() throws Exception {
        Object val = JSONPath.eval(json, "/ext/ex1");
        Assert.assertEquals(1, val);
    }
}

