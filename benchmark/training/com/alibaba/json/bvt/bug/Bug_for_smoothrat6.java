package com.alibaba.json.bvt.bug;


import SerializerFeature.WriteClassName;
import com.alibaba.fastjson.JSON;
import java.util.HashSet;
import java.util.Set;
import junit.framework.TestCase;
import org.junit.Assert;


public class Bug_for_smoothrat6 extends TestCase {
    public void test_set() throws Exception {
        Set<Object> set = new HashSet<Object>();
        set.add(3L);
        set.add(4L);
        Bug_for_smoothrat6.Entity entity = new Bug_for_smoothrat6.Entity();
        entity.setValue(set);
        String text = JSON.toJSONString(entity, WriteClassName);
        System.out.println(text);
        Assert.assertEquals("{\"@type\":\"com.alibaba.json.bvt.bug.Bug_for_smoothrat6$Entity\",\"value\":Set[3L,4L]}", text);
        Bug_for_smoothrat6.Entity entity2 = JSON.parseObject(text, Bug_for_smoothrat6.Entity.class);
        Assert.assertEquals(set, entity2.getValue());
        // Assert.assertEquals(set.getClass(), entity2.getValue().getClass());
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

