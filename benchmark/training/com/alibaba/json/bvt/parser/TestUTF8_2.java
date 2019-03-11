package com.alibaba.json.bvt.parser;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.json.bvt.util.ThreadLocalCacheTest;
import junit.framework.TestCase;
import org.junit.Assert;


public class TestUTF8_2 extends TestCase {
    public void test_utf_1() throws Exception {
        String content = new String(TestUTF8_2.decodeHex("F0A4ADA2".toCharArray()), "UTF-8");
        JSONObject json = new JSONObject();
        json.put("content", content);
        JSONObject obj = ((JSONObject) (JSON.parse(json.toJSONString().getBytes("UTF-8"))));
        Assert.assertEquals(1, obj.size());
        Assert.assertEquals(content, obj.get("content"));
    }

    public void test_utf_2() throws Exception {
        String content = new String(TestUTF8_2.decodeHex("E282AC".toCharArray()), "UTF-8");
        JSONObject json = new JSONObject();
        json.put("content", content);
        JSONObject obj = ((JSONObject) (JSON.parse(json.toJSONString().getBytes("UTF-8"))));
        Assert.assertEquals(1, obj.size());
        Assert.assertEquals(content, obj.get("content"));
    }

    public void test_utf_3() throws Exception {
        byte[] bytes = TestUTF8_2.decodeHex("C2A2".toCharArray());
        String content = new String(bytes, "UTF-8");
        JSONObject json = new JSONObject();
        json.put("content", content);
        JSONObject obj = ((JSONObject) (JSON.parse(json.toJSONString().getBytes("UTF-8"))));
        Assert.assertEquals(1, obj.size());
        Assert.assertEquals(content, obj.get("content"));
    }

    public void test_utf_4() throws Exception {
        ThreadLocalCacheTest.clearChars();
        byte[] bytes = TestUTF8_2.decodeHex("C2FF".toCharArray());
        String content = new String(bytes, "UTF-8");
        JSONObject json = new JSONObject();
        json.put("content", content);
        JSONObject obj = ((JSONObject) (JSON.parse(json.toJSONString().getBytes("UTF-8"))));
        Assert.assertEquals(1, obj.size());
        Assert.assertEquals(content, obj.get("content"));
    }
}

