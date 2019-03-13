package com.alibaba.json.bvt.parser.creator;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.annotation.JSONCreator;
import com.alibaba.fastjson.annotation.JSONField;
import junit.framework.TestCase;
import org.junit.Assert;


public class JSONCreatorTest5 extends TestCase {
    public void test_create_error() throws Exception {
        Exception error = null;
        try {
            JSON.parseObject("{\"id\":1001,\"name\":\"wenshao\",\"obj\":{\"$ref\":\"$\"}}", JSONCreatorTest5.Entity.class);
        } catch (JSONException ex) {
            error = ex;
        }
        Assert.assertNotNull(error);
    }

    public static class Entity {
        private final int id;

        private final String name;

        private final JSONCreatorTest5.Entity obj;

        private Entity(int id, String name, JSONCreatorTest5.Entity obj) {
            this.id = id;
            this.name = name;
            this.obj = obj;
        }

        @JSONCreator
        public static JSONCreatorTest5.Entity create(@JSONField(name = "id")
        int id, @JSONField(name = "name")
        String name, JSONCreatorTest5.Entity obj) {
            return new JSONCreatorTest5.Entity(id, name, obj);
        }

        public int getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public JSONCreatorTest5.Entity getObj() {
            return obj;
        }
    }
}

