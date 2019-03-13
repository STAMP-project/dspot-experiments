package com.alibaba.json.bvt.bug;


import SerializerFeature.WriteClassName;
import com.alibaba.fastjson.JSON;
import java.sql.Time;
import junit.framework.TestCase;
import org.junit.Assert;


public class Bug_for_smoothrat3 extends TestCase {
    public void test_0() throws Exception {
        long millis = System.currentTimeMillis();
        Time time = new Time(millis);
        Bug_for_smoothrat3.Entity entity = new Bug_for_smoothrat3.Entity();
        entity.setValue(new Time(millis));
        String text = JSON.toJSONString(entity, WriteClassName);
        System.out.println(text);
        Assert.assertEquals((("{\"@type\":\"com.alibaba.json.bvt.bug.Bug_for_smoothrat3$Entity\",\"value\":{\"@type\":\"java.sql.Time\",\"val\":" + millis) + "}}"), text);
        Bug_for_smoothrat3.Entity entity2 = JSON.parseObject(text, Bug_for_smoothrat3.Entity.class);
        Assert.assertEquals(time, entity2.getValue());
    }

    public static class Entity {
        private Object value;

        public Object getValue() {
            return value;
        }

        public void setValue(Object value) {
            this.value = value;
        }
    }
}

