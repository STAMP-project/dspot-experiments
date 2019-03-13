package com.alibaba.json.bvt.issue_1400;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.annotation.JSONType;
import java.util.List;
import junit.framework.TestCase;


public class Issue1429 extends TestCase {
    public void test_for_issue() throws Exception {
        String json = "[{\n" + ((((((((("            \"@type\": \"com.alibaba.json.bvt.issue_1400.Issue1429$Student\",\n" + "            \"age\": 22,\n") + "            \"id\": 1,\n") + "            \"name\": \"hello\"\n") + "        }, {\n") + "            \"age\": 22,\n") + "            \"id\": 1,\n") + "            \"name\": \"hhh\",\n") + "            \"@type\": \"com.alibaba.json.bvt.issue_1400.Issue1429$Student\"\n") + "        }]");
        List list = JSON.parseArray(json);
        Issue1429.Student s0 = ((Issue1429.Student) (list.get(0)));
        TestCase.assertEquals(1, s0.id);
        TestCase.assertEquals(22, s0.age);
        TestCase.assertEquals("hello", s0.name);
        Issue1429.Student s1 = ((Issue1429.Student) (list.get(1)));
        TestCase.assertEquals(1, s1.id);
        TestCase.assertEquals(22, s1.age);
        TestCase.assertEquals("hhh", s1.name);
    }

    @JSONType
    public static class Student {
        public int id;

        public int age;

        public String name;
    }
}

