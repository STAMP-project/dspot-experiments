package com.alibaba.json.bvt.bug;


import SerializerFeature.BrowserCompatible;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import junit.framework.TestCase;
import org.junit.Assert;


public class Issue743 extends TestCase {
    public void test_for_issue() throws Exception {
        String temp = "{\"option_1\": \"\\u4e0d\\u5403\\u6216\\u5c11\\u4e8e1\\u6b21\"}";
        JSONObject object = JSON.parseObject(temp);
        Assert.assertEquals("{\"option_1\":\"\u4e0d\u5403\u6216\u5c11\u4e8e1\u6b21\"}", JSON.toJSONString(object));
        Assert.assertEquals("{\"option_1\":\"\\u4E0D\\u5403\\u6216\\u5C11\\u4E8E1\\u6B21\"}", JSON.toJSONString(object, BrowserCompatible));
    }
}

