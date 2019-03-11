package com.alibaba.json.bvt;


import com.alibaba.fastjson.JSON;
import junit.framework.TestCase;
import org.junit.Assert;


public class FloatFieldTest extends TestCase {
    public void test_codec() throws Exception {
        FloatFieldTest.User user = new FloatFieldTest.User();
        user.setValue(1001.0F);
        String text = JSON.toJSONString(user);
        System.out.println(text);
        FloatFieldTest.User user1 = JSON.parseObject(text, FloatFieldTest.User.class);
        Assert.assertTrue(((user1.getValue()) == (user.getValue())));
    }

    public static class User {
        private float value;

        public float getValue() {
            return value;
        }

        public void setValue(float value) {
            this.value = value;
        }
    }
}

