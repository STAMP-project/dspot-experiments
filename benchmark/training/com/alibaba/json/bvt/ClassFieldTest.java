package com.alibaba.json.bvt;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import junit.framework.TestCase;
import org.junit.Assert;


public class ClassFieldTest extends TestCase {
    public void test_codec() throws Exception {
        ClassFieldTest.User user = new ClassFieldTest.User();
        user.setValue(Object.class);
        String text = JSON.toJSONString(user);
        ClassFieldTest.User user1 = JSON.parseObject(text, ClassFieldTest.User.class);
        Assert.assertEquals(user1.getValue(), user.getValue());
    }

    public void test_error() throws Exception {
        Exception error = null;
        try {
            JSON.parseObject("{\"value\":123}", ClassFieldTest.User.class);
        } catch (JSONException ex) {
            error = ex;
        }
        Assert.assertNotNull(error);
    }

    public static class User {
        private Class<?> value;

        public Class<?> getValue() {
            return value;
        }

        public void setValue(Class<?> value) {
            this.value = value;
        }
    }
}

