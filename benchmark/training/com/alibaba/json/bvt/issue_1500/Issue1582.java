package com.alibaba.json.bvt.issue_1500;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.annotation.JSONField;
import junit.framework.TestCase;


public class Issue1582 extends TestCase {
    public void test_for_issue() throws Exception {
        TestCase.assertSame(Issue1582.Size.Big, JSON.parseObject("\"Big\"", Issue1582.Size.class));
        TestCase.assertSame(Issue1582.Size.Big, JSON.parseObject("\"big\"", Issue1582.Size.class));
        TestCase.assertNull(JSON.parseObject("\"Large\"", Issue1582.Size.class));
        TestCase.assertSame(Issue1582.Size.LL, JSON.parseObject("\"L3\"", Issue1582.Size.class));
        TestCase.assertSame(Issue1582.Size.Small, JSON.parseObject("\"Little\"", Issue1582.Size.class));
    }

    public void test_for_issue_1() throws Exception {
        JSONObject object = JSON.parseObject("{\"size\":\"Little\"}");
        Issue1582.Model model = object.toJavaObject(Issue1582.Model.class);
        TestCase.assertSame(Issue1582.Size.Small, model.size);
    }

    public static class Model {
        public Issue1582.Size size;
    }

    public static enum Size {

        Big,
        @JSONField(alternateNames = "Little")
        Small,
        @JSONField(name = "L3")
        LL;}
}

