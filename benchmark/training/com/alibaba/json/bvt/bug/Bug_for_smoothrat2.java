package com.alibaba.json.bvt.bug;


import com.alibaba.fastjson.JSON;
import java.sql.Time;
import junit.framework.TestCase;
import org.junit.Assert;


public class Bug_for_smoothrat2 extends TestCase {
    public void test_0() throws Exception {
        long millis = System.currentTimeMillis();
        Time time = new Time(millis);
        Bug_for_smoothrat2.Entity entity = new Bug_for_smoothrat2.Entity();
        entity.setValue(new Time(millis));
        String text = JSON.toJSONString(entity);
        Assert.assertEquals((("{\"value\":" + millis) + "}"), text);
        Bug_for_smoothrat2.Entity entity2 = JSON.parseObject(text, Bug_for_smoothrat2.Entity.class);
        Assert.assertEquals(time, entity2.getValue());
    }

    public static class Entity {
        private Time value;

        public Time getValue() {
            return value;
        }

        public void setValue(Time value) {
            this.value = value;
        }
    }
}

