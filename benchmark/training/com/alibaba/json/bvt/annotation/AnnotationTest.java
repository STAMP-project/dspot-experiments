package com.alibaba.json.bvt.annotation;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import junit.framework.TestCase;


/**
 * Created by Helly on 2017/04/10.
 */
public class AnnotationTest extends TestCase {
    public void test_annoation() throws Exception {
        AnnotationTest.Bob bob = new AnnotationTest.Bob("Bob", 30, true);
        // JSONObject obj = (JSONObject) JSON.toJSON(bob);
        // assertEquals(3, obj.size());
        // assertEquals(Boolean.TRUE, obj.get("sex"));
        // assertEquals("Bob", obj.get("name"));
        // assertEquals(new Integer(30), obj.get("age"));
        AnnotationTest.PersonInfo info = AnnotationTest.Bob.class.getAnnotation(AnnotationTest.PersonInfo.class);
        JSONObject obj = ((JSONObject) (JSON.toJSON(info)));
        TestCase.assertEquals(3, obj.size());
        TestCase.assertEquals(Boolean.TRUE, obj.get("sex"));
        TestCase.assertEquals("Bob", obj.get("name"));
        TestCase.assertEquals(new Integer(30), obj.get("age"));
    }

    @AnnotationTest.PersonInfo(name = "Bob", age = 30, sex = true)
    public static class Bob implements AnnotationTest.Person {
        private String name;

        private int age;

        private boolean sex;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getAge() {
            return age;
        }

        public void setAge(int age) {
            this.age = age;
        }

        public boolean isSex() {
            return sex;
        }

        public void setSex(boolean sex) {
            this.sex = sex;
        }

        public Bob() {
        }

        public Bob(String name, int age, boolean sex) {
            this();
            this.name = name;
            this.age = age;
            this.sex = sex;
        }

        public void hello() {
            System.out.println("world");
        }
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.TYPE)
    public static @interface PersonInfo {
        String name();

        int age();

        boolean sex();
    }

    public static interface Person {
        void hello();
    }
}

