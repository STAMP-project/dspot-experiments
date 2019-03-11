package com.alibaba.json.bvt.parser.creator;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.annotation.JSONCreator;
import com.alibaba.fastjson.annotation.JSONField;
import junit.framework.TestCase;


public class JSONCreatorTest4 extends TestCase {
    public void test_create_error() throws Exception {
        JSONCreatorTest4.Entity entity = JSON.parseObject("{\"id\":1001,\"name\":\"wenshao\",\"obj\":{\"$ref\":\"$\"}}", JSONCreatorTest4.Entity.class);
        TestCase.assertNotNull(entity);
        TestCase.assertEquals(1001, entity.id);
        TestCase.assertEquals("wenshao", entity.name);
        TestCase.assertSame(entity, entity.obj);
    }

    public static class Entity {
        private final int id;

        private final String name;

        private JSONCreatorTest4.Entity obj;

        @JSONCreator
        public Entity(@JSONField(name = "id")
        int id, @JSONField(name = "name")
        String name, JSONCreatorTest4.Entity obj) {
            this.id = id;
            this.name = name;
            this.obj = obj;
        }

        public int getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public JSONCreatorTest4.Entity getObj() {
            return obj;
        }

        public void setObj(JSONCreatorTest4.Entity obj) {
            this.obj = obj;
        }
    }
}

