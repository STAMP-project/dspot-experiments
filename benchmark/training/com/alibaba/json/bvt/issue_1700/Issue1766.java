package com.alibaba.json.bvt.issue_1700;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.annotation.JSONField;
import java.util.Date;
import junit.framework.TestCase;


public class Issue1766 extends TestCase {
    public void test_for_issue() throws Exception {
        // succ
        String json = "{\"name\":\"\u5f20\u4e09\"\n, \"birthday\":\"2017-01-01 01:01:01\"}";
        Issue1766.User user = JSON.parseObject(json, Issue1766.User.class);
        TestCase.assertEquals("??", user.getName());
        TestCase.assertNotNull(user.getBirthday());
        // failed
        json = "{\"name\":\"\u5f20\u4e09\", \"birthday\":\"2017-01-01 01:01:02\"\n}";
        user = JSON.parseObject(json, Issue1766.User.class);// will exception

        TestCase.assertEquals("??", user.getName());
        TestCase.assertNotNull(user.getBirthday());
    }

    public static class User {
        private String name;

        @JSONField(format = "yyyy-MM-dd HH:mm:ss")
        private Date birthday;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Date getBirthday() {
            return birthday;
        }

        public void setBirthday(Date birthday) {
            this.birthday = birthday;
        }
    }
}

