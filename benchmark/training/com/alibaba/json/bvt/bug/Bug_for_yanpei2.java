package com.alibaba.json.bvt.bug;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import junit.framework.TestCase;
import org.junit.Assert;


public class Bug_for_yanpei2 extends TestCase {
    public void test_for_sepcial_chars() throws Exception {
        String text = "{\"answerAllow\":true,\"atUsers\":[],\"desc\":\"\u6d4b\u8bd5\u8d26\u53f7\\n\u6d4b\u8bd5\u8d26\u53f7\"}";
        JSONObject obj = JSON.parseObject(text);
        Assert.assertEquals(true, obj.get("answerAllow"));
        Assert.assertEquals(0, obj.getJSONArray("atUsers").size());
        Assert.assertEquals("\u6d4b\u8bd5\u8d26\u53f7\n\u6d4b\u8bd5\u8d26\u53f7", obj.get("desc"));
    }
}

