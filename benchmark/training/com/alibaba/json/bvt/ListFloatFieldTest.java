package com.alibaba.json.bvt;


import com.alibaba.fastjson.JSON;
import java.util.ArrayList;
import java.util.List;
import junit.framework.TestCase;
import org.junit.Assert;


public class ListFloatFieldTest extends TestCase {
    public void test_codec() throws Exception {
        ListFloatFieldTest.User user = new ListFloatFieldTest.User();
        user.setValue(new ArrayList<Float>());
        user.getValue().add(1.0F);
        String text = JSON.toJSONString(user);
        System.out.println(text);
        ListFloatFieldTest.User user1 = JSON.parseObject(text, ListFloatFieldTest.User.class);
        Assert.assertEquals(user1.getValue(), user.getValue());
    }

    public static class User {
        private List<Float> value;

        public List<Float> getValue() {
            return value;
        }

        public void setValue(List<Float> value) {
            this.value = value;
        }
    }
}

