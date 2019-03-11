package com.alibaba.json.bvt.bug;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import junit.framework.TestCase;
import org.junit.Assert;


public class Bug_for_yanpei extends TestCase {
    public void test_for_sepcial_chars() throws Exception {
        String text = "{\"answerAllow\":true,\"atUsers\":[],\"desc\":\"Halios 1000M \\\"Puck\\\"\u5f88\u5fae\u4f17\u7684\u54c1\u724c\uff0c\u51e0\u4e4e\u5168\u9760\u73a9\u5bb6\u53e3\u53e3\u76f8\u4f20\"} ";
        JSONObject obj = JSON.parseObject(text);
        Assert.assertEquals(true, obj.get("answerAllow"));
        Assert.assertEquals(0, obj.getJSONArray("atUsers").size());
        Assert.assertEquals("Halios 1000M \"Puck\"\u5f88\u5fae\u4f17\u7684\u54c1\u724c\uff0c\u51e0\u4e4e\u5168\u9760\u73a9\u5bb6\u53e3\u53e3\u76f8\u4f20", obj.get("desc"));
    }
}

