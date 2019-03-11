package com.alibaba.json.bvt.bug;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import junit.framework.TestCase;


public class Issue179 extends TestCase {
    public void test_for_issue_179() throws Exception {
        Issue179.Student student = new Issue179.Student();
        Issue179.School school = new Issue179.School();
        school.setStudent(student);
        student.setSchool(school);
        // String schoolJSONString = JSON.toJSONString(school);
        // System.out.println(schoolJSONString);
        // 
        // School fromJSONSchool = JSON.parseObject(schoolJSONString,
        // School.class);
        // 
        // System.out.println(JSON.toJSONString(fromJSONSchool));
        JSONObject object = new JSONObject();
        object.put("school", school);
        String jsonString = JSON.toJSONString(object);
        System.out.println(jsonString);
        JSONObject object2 = ((JSONObject) (JSON.parseObject(jsonString, JSONObject.class)));
        System.out.println(JSON.toJSONString(object2));
        Issue179.School school2 = object2.getObject("school", Issue179.School.class);
        System.out.println(school2);
    }

    public static class School {
        Issue179.Student student;

        public Issue179.Student getStudent() {
            return student;
        }

        public void setStudent(Issue179.Student student) {
            this.student = student;
        }
    }

    static class Student {
        public Issue179.School getSchool() {
            return school;
        }

        public void setSchool(Issue179.School school) {
            this.school = school;
        }

        Issue179.School school;
    }
}

