package com.alibaba.json.bvt.bug;


import SerializerFeature.WriteMapNullValue;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.annotation.JSONField;
import junit.framework.TestCase;


/**
 * Created by wenshao on 15/01/2017.
 */
public class Issue993 extends TestCase {
    public void test_for_issue() throws Exception {
        Issue993.Student student = new Issue993.Student();
        student.name = "??";
        String json = JSON.toJSONString(student, WriteMapNullValue);
        TestCase.assertEquals("{\"student_name\":\"\u5c0f\u521a\",\"student_age\":0,\"student_grade\":null}", json);
    }

    public static class Student {
        @JSONField(name = "student_name", ordinal = 0)
        public String name;

        @JSONField(name = "student_age", ordinal = 1)
        public int age;

        @JSONField(name = "student_grade", ordinal = 2)
        public String grade;
    }
}

