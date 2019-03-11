package com.alibaba.json.bvt.path;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONPath;
import junit.framework.TestCase;
import org.junit.Assert;


public class JSONPath_toString extends TestCase {
    public void test_toJSONString() throws Exception {
        JSONPath_toString.Model model = new JSONPath_toString.Model();
        model.path = new JSONPath("$");
        String text = JSON.toJSONString(model);
        Assert.assertEquals("{\"path\":\"$\"}", text);
        JSON.parseObject(text, JSONPath_toString.Model.class);
    }

    public static class Model {
        public JSONPath path;
    }
}

