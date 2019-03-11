package com.alibaba.json.bvt.issue_1600;


import SerializerFeature.UseISO8601DateFormat;
import com.alibaba.fastjson.JSON;
import java.util.Date;
import junit.framework.TestCase;


public class Issue1679 extends TestCase {
    public void test_for_issue() throws Exception {
        String json = "{\"create\":\"2018-01-10 08:30:00\"}";
        Issue1679.User user = JSON.parseObject(json, Issue1679.User.class);
        TestCase.assertEquals("\"2018-01-10T08:30:00+08:00\"", JSON.toJSONString(user.create, UseISO8601DateFormat));
    }

    public static class User {
        public Date create;
    }
}

