package com.alibaba.json.bvt.parser.creator;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.annotation.JSONCreator;
import com.alibaba.fastjson.annotation.JSONField;
import junit.framework.TestCase;
import org.junit.Assert;


public class JSONCreatorTest6 extends TestCase {
    public void test_create_error() throws Exception {
        Exception error = null;
        try {
            JSON.parseObject("{\"id\":1001,\"name\":\"wenshao\",\"obj\":{\"$ref\":\"$\"}}", JSONCreatorTest6.Entity.class);
        } catch (JSONException ex) {
            error = ex;
        }
        Assert.assertNotNull(error);
    }

    public static class Entity {
        private final int id;

        private final String name;

        private final JSONCreatorTest6.Entity obj;

        private Entity(int id, String name, JSONCreatorTest6.Entity obj) {
            this.id = id;
            this.name = name;
            this.obj = obj;
        }

        @JSONCreator
        public static JSONCreatorTest6.Entity create(@JSONField(name = "id")
        int id, @JSONField(name = "name")
        String name, @JSONField(name = "obj")
        JSONCreatorTest6.Entity obj) {
            return new JSONCreatorTest6.Entity(id, name, obj);
        }

        @JSONCreator
        public static JSONCreatorTest6.Entity create1(@JSONField(name = "id")
        int id, @JSONField(name = "name")
        String name, @JSONField(name = "obj")
        JSONCreatorTest6.Entity obj) {
            return new JSONCreatorTest6.Entity(id, name, obj);
        }

        public int getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public JSONCreatorTest6.Entity getObj() {
            return obj;
        }
    }
}

