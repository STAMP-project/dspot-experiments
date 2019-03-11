package com.alibaba.json.bvt.bug;


import SerializerFeature.WriteClassName;
import com.alibaba.fastjson.JSON;
import junit.framework.TestCase;
import org.junit.Assert;


public class Bug_for_smoothrat4 extends TestCase {
    public void test_long() throws Exception {
        Bug_for_smoothrat4.Entity entity = new Bug_for_smoothrat4.Entity();
        entity.setValue(3L);
        String text = JSON.toJSONString(entity, WriteClassName);
        System.out.println(text);
        Assert.assertEquals("{\"@type\":\"com.alibaba.json.bvt.bug.Bug_for_smoothrat4$Entity\",\"value\":3L}", text);
        Bug_for_smoothrat4.Entity entity2 = JSON.parseObject(text, Bug_for_smoothrat4.Entity.class);
        Assert.assertEquals(Long.valueOf(3), entity2.getValue());
    }

    public void test_int() throws Exception {
        Bug_for_smoothrat4.Entity entity = new Bug_for_smoothrat4.Entity();
        entity.setValue(3);
        String text = JSON.toJSONString(entity, WriteClassName);
        System.out.println(text);
        Assert.assertEquals("{\"@type\":\"com.alibaba.json.bvt.bug.Bug_for_smoothrat4$Entity\",\"value\":3}", text);
        Bug_for_smoothrat4.Entity entity2 = JSON.parseObject(text, Bug_for_smoothrat4.Entity.class);
        Assert.assertEquals(Integer.valueOf(3), entity2.getValue());
    }

    public void test_short() throws Exception {
        Bug_for_smoothrat4.Entity entity = new Bug_for_smoothrat4.Entity();
        entity.setValue(((short) (3)));
        String text = JSON.toJSONString(entity, WriteClassName);
        System.out.println(text);
        Assert.assertEquals("{\"@type\":\"com.alibaba.json.bvt.bug.Bug_for_smoothrat4$Entity\",\"value\":3S}", text);
        Bug_for_smoothrat4.Entity entity2 = JSON.parseObject(text, Bug_for_smoothrat4.Entity.class);
        Assert.assertEquals(Short.valueOf(((short) (3))), entity2.getValue());
    }

    public void test_byte() throws Exception {
        Bug_for_smoothrat4.Entity entity = new Bug_for_smoothrat4.Entity();
        entity.setValue(((byte) (3)));
        String text = JSON.toJSONString(entity, WriteClassName);
        System.out.println(text);
        Assert.assertEquals("{\"@type\":\"com.alibaba.json.bvt.bug.Bug_for_smoothrat4$Entity\",\"value\":3B}", text);
        Bug_for_smoothrat4.Entity entity2 = JSON.parseObject(text, Bug_for_smoothrat4.Entity.class);
        Assert.assertEquals(Byte.valueOf(((byte) (3))), entity2.getValue());
    }

    public void test_float() throws Exception {
        Bug_for_smoothrat4.Entity entity = new Bug_for_smoothrat4.Entity();
        entity.setValue(3.0F);
        String text = JSON.toJSONString(entity, WriteClassName);
        System.out.println(text);
        Assert.assertEquals("{\"@type\":\"com.alibaba.json.bvt.bug.Bug_for_smoothrat4$Entity\",\"value\":3.0F}", text);
        Bug_for_smoothrat4.Entity entity2 = JSON.parseObject(text, Bug_for_smoothrat4.Entity.class);
        Assert.assertEquals(3.0F, entity2.getValue());
    }

    public void test_double() throws Exception {
        Bug_for_smoothrat4.Entity entity = new Bug_for_smoothrat4.Entity();
        entity.setValue(3.0);
        String text = JSON.toJSONString(entity, WriteClassName);
        System.out.println(text);
        Assert.assertEquals("{\"@type\":\"com.alibaba.json.bvt.bug.Bug_for_smoothrat4$Entity\",\"value\":3.0D}", text);
        Bug_for_smoothrat4.Entity entity2 = JSON.parseObject(text, Bug_for_smoothrat4.Entity.class);
        Assert.assertEquals(3.0, entity2.getValue());
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

