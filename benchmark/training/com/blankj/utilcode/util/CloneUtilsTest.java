package com.blankj.utilcode.util;


import org.junit.Assert;
import org.junit.Test;


/**
 * <pre>
 *     author: Blankj
 *     blog  : http://blankj.com
 *     time  : 2018/04/08
 *     desc  : test CloneUtils
 * </pre>
 */
public class CloneUtilsTest {
    @Test
    public void deepClone() {
        CloneUtilsTest.Result<CloneUtilsTest.Person> result = new CloneUtilsTest.Result<>(new CloneUtilsTest.Person("Blankj"));
        CloneUtilsTest.Result<CloneUtilsTest.Person> cloneResult = CloneUtils.deepClone(result, GsonUtils.getType(CloneUtilsTest.Result.class, CloneUtilsTest.Person.class));
        System.out.println(result);
        System.out.println(cloneResult);
        Assert.assertNotEquals(result, cloneResult);
    }

    static class Result<T> {
        int code;

        String message;

        T data;

        Result(T data) {
            this.code = 200;
            this.message = "success";
            this.data = data;
        }

        @Override
        public String toString() {
            return ((((("{\"code\":" + (CloneUtilsTest.primitive2String(code))) + ",\"message\":") + (CloneUtilsTest.primitive2String(message))) + ",\"data\":") + (CloneUtilsTest.primitive2String(data))) + "}";
        }
    }

    static class Person {
        String name;

        int gender;

        String address;

        Person(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return ((((("{\"name\":" + (CloneUtilsTest.primitive2String(name))) + ",\"gender\":") + (CloneUtilsTest.primitive2String(gender))) + ",\"address\":") + (CloneUtilsTest.primitive2String(address))) + "}";
        }
    }
}

