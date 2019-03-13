package com.alibaba.json.bvt.issue_1700;


import Feature.SupportArrayToBean;
import com.alibaba.fastjson.JSON;
import junit.framework.TestCase;


public class Issue1723 extends TestCase {
    public void test_for_issue() throws Exception {
        Issue1723.User user = JSON.parseObject("{\"age\":\"0.9390308260917664\"}", Issue1723.User.class);
        TestCase.assertEquals(0.9390308F, user.age);
    }

    public void test_for_issue_1() throws Exception {
        Issue1723.User user = JSON.parseObject("{\"age\":\"8.200000000000001\"}", Issue1723.User.class);
        TestCase.assertEquals(8.2F, user.age);
    }

    public void test_for_issue_2() throws Exception {
        Issue1723.User user = JSON.parseObject("[\"8.200000000000001\"]", Issue1723.User.class, SupportArrayToBean);
        TestCase.assertEquals(8.2F, user.age);
    }

    public static class User {
        private float age;

        public float getAge() {
            return age;
        }

        public void setAge(float age) {
            this.age = age;
        }

        @Override
        public String toString() {
            return (("User{" + "age=") + (age)) + '}';
        }
    }
}

