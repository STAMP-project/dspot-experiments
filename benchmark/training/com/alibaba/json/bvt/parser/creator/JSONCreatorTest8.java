package com.alibaba.json.bvt.parser.creator;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.annotation.JSONCreator;
import com.alibaba.fastjson.annotation.JSONField;
import junit.framework.TestCase;


public class JSONCreatorTest8 extends TestCase {
    public void test_create() throws Exception {
        String json = "{\"id\":1001,\"name\":\"wenshao\"}";
        JSONCreatorTest8.Entity entity = JSON.parseObject(json, JSONCreatorTest8.Entity.class);
        TestCase.assertEquals(1001, entity.id);
        TestCase.assertEquals("wenshao", entity.name);
    }

    public static class Entity {
        private int id;

        private String name;

        @JSONCreator
        public Entity(@JSONField(name = "id")
        int id) {
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

