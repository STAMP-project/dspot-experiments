package com.alibaba.json.bvt;


import com.alibaba.fastjson.JSON;
import java.util.ArrayList;
import java.util.List;
import junit.framework.TestCase;
import org.junit.Assert;


public class ArrayListFloatFieldTest extends TestCase {
    public void test_codec() throws Exception {
        ArrayListFloatFieldTest.User user = new ArrayListFloatFieldTest.User();
        user.setValue(new ArrayList<Float>());
        user.getValue().add(1.0F);
        String text = JSON.toJSONString(user);
        System.out.println(text);
        ArrayListFloatFieldTest.User user1 = JSON.parseObject(text, ArrayListFloatFieldTest.User.class);
        Assert.assertEquals(user.getValue(), user1.getValue());
    }

    public static class User {
        private ArrayList<Float> value;

        public User() {
        }

        public List<Float> getValue() {
            return value;
        }

        public void setValue(ArrayList<Float> value) {
            this.value = value;
        }
    }
}

