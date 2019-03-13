package com.alibaba.json.bvt.serializer;


import com.alibaba.fastjson.JSON;
import junit.framework.TestCase;


/**
 * Created by wenshao on 2016/11/2.
 */
public class FieldOrderTest extends TestCase {
    public void test_field_order() throws Exception {
        FieldOrderTest.Person p = new FieldOrderTest.Person();
        p.setName("njb");
        FieldOrderTest.School s = new FieldOrderTest.School();
        s.setName("llyz");
        p.setSchool(s);
        String json = JSON.toJSONString(p);
        TestCase.assertEquals("{\"name\":\"njb\",\"school\":{\"name\":\"llyz\"}}", json);
    }

    public static class Person {
        private String name;

        private FieldOrderTest.School school;

        public boolean isSchool() {
            return false;
        }

        public FieldOrderTest.School getSchool() {
            return school;
        }

        public void setSchool(FieldOrderTest.School school) {
            this.school = school;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

    public static class School {
        private String name;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }
}

