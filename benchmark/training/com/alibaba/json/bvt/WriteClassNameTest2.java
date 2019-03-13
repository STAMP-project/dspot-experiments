package com.alibaba.json.bvt;


import SerializerFeature.PrettyFormat;
import SerializerFeature.WriteClassName;
import com.alibaba.fastjson.JSON;
import junit.framework.TestCase;
import org.junit.Assert;


public class WriteClassNameTest2 extends TestCase {
    public void test_0() throws Exception {
        WriteClassNameTest2.Entity entity = new WriteClassNameTest2.Entity(3, "jobs");
        String text = JSON.toJSONString(entity, WriteClassName, PrettyFormat);
        System.out.println(text);
        WriteClassNameTest2.Entity entity2 = ((WriteClassNameTest2.Entity) (JSON.parseObject(text, Object.class)));
        Assert.assertEquals(entity.getId(), entity2.getId());
        Assert.assertEquals(entity.getName(), entity2.getName());
    }

    public static class Entity {
        private int id;

        private String name;

        public Entity() {
        }

        public Entity(int id, String name) {
            this.id = id;
            this.name = name;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }
}

