package com.alibaba.json.bvt.parser.creator;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.annotation.JSONCreator;
import com.alibaba.fastjson.annotation.JSONField;
import com.alibaba.fastjson.parser.ParserConfig;
import junit.framework.TestCase;
import org.junit.Assert;


public class JSONCreatorFactoryTest extends TestCase {
    public void test_create() throws Exception {
        JSONCreatorFactoryTest.Entity entity = new JSONCreatorFactoryTest.Entity(123, "??");
        String text = JSON.toJSONString(entity);
        JSONCreatorFactoryTest.Entity entity2 = JSON.parseObject(text, JSONCreatorFactoryTest.Entity.class);
        Assert.assertEquals(entity.getId(), entity2.getId());
        Assert.assertEquals(entity.getName(), entity2.getName());
    }

    public void test_create_2() throws Exception {
        JSONCreatorFactoryTest.Entity entity = new JSONCreatorFactoryTest.Entity(123, "??");
        String text = JSON.toJSONString(entity);
        ParserConfig config = new ParserConfig();
        config.setAsmEnable(false);
        JSONCreatorFactoryTest.Entity entity2 = JSON.parseObject(text, JSONCreatorFactoryTest.Entity.class, config, 0);
        Assert.assertEquals(entity.getId(), entity2.getId());
        Assert.assertEquals(entity.getName(), entity2.getName());
    }

    public static class Entity {
        private final int id;

        private final String name;

        @JSONCreator
        public static JSONCreatorFactoryTest.Entity create(@JSONField(name = "id")
        int id, @JSONField(name = "name")
        String name) {
            return new JSONCreatorFactoryTest.Entity(id, name);
        }

        private Entity(int id, String name) {
            this.id = id;
            this.name = name;
        }

        public int getId() {
            return id;
        }

        public String getName() {
            return name;
        }
    }
}

