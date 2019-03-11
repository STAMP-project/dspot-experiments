package com.alibaba.json.bvt.issue_1600;


import SerializerFeature.PrettyFormat;
import com.alibaba.fastjson.JSON;
import java.time.LocalDateTime;
import junit.framework.TestCase;


public class Issue1645 extends TestCase {
    public void test_for_issue() throws Exception {
        String test = "{\"name\":\"test\",\"testDateTime\":\"2017-12-08 14:55:16\"}";
        JSON.toJSONString(JSON.parseObject(test).toJavaObject(Issue1645.TestDateClass.class), PrettyFormat);
    }

    public static class TestDateClass {
        public String name;

        public LocalDateTime testDateTime;
    }
}

