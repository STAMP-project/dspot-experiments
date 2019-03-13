package com.alibaba.json.bvt.parser.creator;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.annotation.JSONCreator;
import com.alibaba.fastjson.annotation.JSONField;
import com.alibaba.fastjson.parser.ParserConfig;
import junit.framework.TestCase;
import org.junit.Assert;


public class JSONCreatorTest_float extends TestCase {
    public void test_create() throws Exception {
        JSONCreatorTest_float.Entity entity = new JSONCreatorTest_float.Entity(123.45F, "??");
        String text = JSON.toJSONString(entity);
        JSONCreatorTest_float.Entity entity2 = JSON.parseObject(text, JSONCreatorTest_float.Entity.class);
        Assert.assertTrue(((entity.getId()) == (entity2.getId())));
        Assert.assertEquals(entity.getName(), entity2.getName());
    }

    public void test_create_2() throws Exception {
        JSONCreatorTest_float.Entity entity = new JSONCreatorTest_float.Entity(123.45F, "??");
        String text = JSON.toJSONString(entity);
        ParserConfig config = new ParserConfig();
        JSONCreatorTest_float.Entity entity2 = JSON.parseObject(text, JSONCreatorTest_float.Entity.class, config, 0);
        Assert.assertTrue(((entity.getId()) == (entity2.getId())));
        Assert.assertEquals(entity.getName(), entity2.getName());
    }

    public static class Entity {
        private final float id;

        private final String name;

        @JSONCreator
        public Entity(@JSONField(name = "id")
        float id, @JSONField(name = "name")
        String name) {
            this.id = id;
            this.name = name;
        }

        public float getId() {
            return id;
        }

        public String getName() {
            return name;
        }
    }
}

