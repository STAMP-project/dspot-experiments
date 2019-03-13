package com.alibaba.json.bvt.bug;


import com.alibaba.fastjson.JSON;
import junit.framework.TestCase;
import org.junit.Assert;


public class Bug_for_smoothrat extends TestCase {
    public void test_0() throws Exception {
        Bug_for_smoothrat.Entity entity = new Bug_for_smoothrat.Entity();
        entity.setValue("aaa123".toCharArray());
        String text = JSON.toJSONString(entity);
        Assert.assertEquals("{\"value\":\"aaa123\"}", text);
        Bug_for_smoothrat.Entity entity2 = JSON.parseObject(text, Bug_for_smoothrat.Entity.class);
        Assert.assertEquals(new String(entity.getValue()), new String(entity2.getValue()));
    }

    public static class Entity {
        private char[] value;

        public char[] getValue() {
            return value;
        }

        public void setValue(char[] value) {
            this.value = value;
        }
    }
}

