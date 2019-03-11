package com.alibaba.json.bvt.issue_2200;


import com.alibaba.fastjson.JSON;
import java.util.List;
import junit.framework.TestCase;


public class Issue2234 extends TestCase {
    public void test_for_issue() throws Exception {
        String userStr = "{\"name\":\"asdfad\",\"ss\":\"\"}";
        Issue2234.User user = JSON.parseObject(userStr, Issue2234.User.class);
    }

    public static class User {
        public String name;

        public List ss;
    }
}

