package com.alibaba.json.bvt.bug;


import SerializerFeature.WriteClassName;
import com.alibaba.fastjson.JSON;
import java.util.LinkedHashMap;
import java.util.Map;
import junit.framework.TestCase;
import org.junit.Assert;


public class Bug_for_smoothrat8 extends TestCase {
    public void test_set() throws Exception {
        Map<Integer, Object> map = new LinkedHashMap<Integer, Object>();
        map.put(1, "a");
        map.put(2, "b");
        Bug_for_smoothrat8.Entity entity = new Bug_for_smoothrat8.Entity();
        entity.setValue(map);
        String text = JSON.toJSONString(entity, WriteClassName);
        System.out.println(text);
        Assert.assertEquals("{\"@type\":\"com.alibaba.json.bvt.bug.Bug_for_smoothrat8$Entity\",\"value\":{\"@type\":\"java.util.LinkedHashMap\",1:\"a\",2:\"b\"}}", text);
        Bug_for_smoothrat8.Entity entity2 = JSON.parseObject(text, Bug_for_smoothrat8.Entity.class);
        Assert.assertEquals(map, entity2.getValue());
        Assert.assertEquals(map.getClass(), entity2.getValue().getClass());
        Assert.assertEquals(Integer.class, ((Map) (entity2.getValue())).keySet().iterator().next().getClass());
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

