package com.lauren.simplenews.utils;


import org.junit.Assert;
import org.junit.Test;


/**
 * Description :
 * Author : lauren
 * Email  : lauren.liuling@gmail.com
 * Blog   : http://www.liuling123.com
 * Date   : 15/12/17
 */
public class JsonUtilsTest {
    class User {
        String name;

        String sex;

        public User(String name, String sex) {
            this.name = name;
            this.sex = sex;
        }
    }

    @Test
    public void testSerialize() throws Exception {
        Assert.assertEquals("{\"name\":\"Lauren\",\"sex\":\"\u7537\"}", JsonUtils.serialize(new JsonUtilsTest.User("Lauren", "?")));
    }

    @Test
    public void testDeserialize() throws Exception {
        Assert.assertNotNull(JsonUtils.deserialize("{\"name\":\"Lauren\",\"sex\":\"\u7537\"}", JsonUtilsTest.User.class));
    }

    @Test
    public void testDeserialize1() throws Exception {
        Assert.assertNotNull(JsonUtils.deserialize("{\"name\":\"Lauren\",\"sex\":\"\u7537\"}", JsonUtilsTest.User.class));
    }
}

