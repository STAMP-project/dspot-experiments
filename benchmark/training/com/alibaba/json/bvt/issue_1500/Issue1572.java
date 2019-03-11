package com.alibaba.json.bvt.issue_1500;


import com.alibaba.fastjson.JSONPath;
import java.util.Map;
import java.util.Set;
import junit.framework.TestCase;


public class Issue1572 extends TestCase {
    public void test_for_issue() throws Exception {
        Issue1572.Person person = new Issue1572.Person();
        person.setId("1001");
        person.setName("1001");
        Map<String, Object> pathValues = JSONPath.paths(person);
        Set<String> paths = pathValues.keySet();
        TestCase.assertEquals(3, paths.size());
        TestCase.assertEquals("1001", pathValues.get("/id"));
        TestCase.assertEquals("1001", pathValues.get("/name"));
        TestCase.assertSame(person, pathValues.get("/"));
    }

    public static class Person {
        private String name;

        private String id;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }
    }
}

