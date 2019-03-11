package com.alibaba.json.bvt.parser.creator;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.annotation.JSONCreator;
import com.alibaba.fastjson.annotation.JSONField;
import junit.framework.TestCase;
import org.junit.Assert;


public class JSONCreatorTest_error3 extends TestCase {
    public void test_create() throws Exception {
        Exception error = null;
        try {
            JSON.parseObject("{\"id\":123,\"name\":\"abc\"}").toJavaObject(JSONCreatorTest_error3.Entity.class);
        } catch (JSONException ex) {
            error = ex;
        }
        Assert.assertNotNull(error);
    }

    public static class Entity {
        private final int id;

        private final String name;

        @JSONCreator
        public Entity(@JSONField(name = "id")
        int id, @JSONField(name = "name")
        String name) {
            throw new UnsupportedOperationException();
        }

        public int getId() {
            return id;
        }

        public String getName() {
            return name;
        }
    }
}

