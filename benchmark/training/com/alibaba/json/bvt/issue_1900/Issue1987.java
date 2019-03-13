package com.alibaba.json.bvt.issue_1900;


import SerializerFeature.PrettyFormat;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import java.time.LocalDateTime;
import junit.framework.TestCase;


public class Issue1987 extends TestCase {
    public void test_for_issue() throws Exception {
        Issue1987.JsonExample example = new Issue1987.JsonExample();
        // test1 ????, test2, test3 ???? com.alibaba.fastjson.JSONException: can not cast to : java.time.LocalDateTime
        example.setTestLocalDateTime(LocalDateTime.now());
        // ??????0 ,test1,test2,test3 ??????
        // example.setTestLocalDateTime(LocalDateTime.now().withNano(0));
        String text = JSON.toJSONString(example, PrettyFormat);
        System.out.println(text);
        // test1, ????????
        Issue1987.JsonExample example1 = JSON.parseObject(text, Issue1987.JsonExample.class);
        System.out.println(JSON.toJSONString(example1));
        // test2  ????0, ??????, ??0???
        Issue1987.JsonExample example2 = JSONObject.parseObject(text).toJavaObject(Issue1987.JsonExample.class);
        System.out.println(JSON.toJSONString(example2));
        // test3 ????0, ??????, ??0???
        Issue1987.JsonExample example3 = JSON.parseObject(text).toJavaObject(Issue1987.JsonExample.class);
        System.out.println(JSON.toJSONString(example3));
    }

    public static class JsonExample {
        private LocalDateTime testLocalDateTime;

        public LocalDateTime getTestLocalDateTime() {
            return testLocalDateTime;
        }

        public void setTestLocalDateTime(LocalDateTime testLocalDateTime) {
            this.testLocalDateTime = testLocalDateTime;
        }
    }
}

