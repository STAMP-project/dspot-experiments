package com.alibaba.json.bvt.parser.creator;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.annotation.JSONCreator;
import com.alibaba.fastjson.annotation.JSONField;
import com.alibaba.fastjson.parser.ParserConfig;
import junit.framework.TestCase;
import org.junit.Assert;


public class JSONCreatorTest_float_obj extends TestCase {
    public void test_create() throws Exception {
        JSONCreatorTest_float_obj.Entity entity = new JSONCreatorTest_float_obj.Entity(123.45F, "??");
        String text = JSON.toJSONString(entity);
        JSONCreatorTest_float_obj.Entity entity2 = JSON.parseObject(text, JSONCreatorTest_float_obj.Entity.class);
        Assert.assertTrue(((entity.getId().floatValue()) == (entity2.getId().floatValue())));
        Assert.assertEquals(entity.getName(), entity2.getName());
    }

    public void test_create_2() throws Exception {
        JSONCreatorTest_float_obj.Entity entity = new JSONCreatorTest_float_obj.Entity(123.45F, "??");
        String text = JSON.toJSONString(entity);
        ParserConfig config = new ParserConfig();
        JSONCreatorTest_float_obj.Entity entity2 = JSON.parseObject(text, JSONCreatorTest_float_obj.Entity.class, config, 0);
        Assert.assertTrue(((entity.getId().floatValue()) == (entity2.getId().floatValue())));
        Assert.assertEquals(entity.getName(), entity2.getName());
    }

    public static class Entity {
        private final Float id;

        private final String name;

        @JSONCreator
        public Entity(@JSONField(name = "id")
        Float id, @JSONField(name = "name")
        String name) {
            this.id = id;
            this.name = name;
        }

        public Float getId() {
            return id;
        }

        public String getName() {
            return name;
        }
    }
}

