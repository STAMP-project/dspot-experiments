package com.alibaba.json.bvt.parser.deser;


import Feature.IgnoreNotMatch;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.parser.DefaultJSONParser;
import com.alibaba.fastjson.parser.ParserConfig;
import junit.framework.TestCase;


public class DefaultObjectDeserializerTest4 extends TestCase {
    public void test_0() throws Exception {
        DefaultJSONParser parser = new DefaultJSONParser("{\"id\":3, \"name\":\"xx\"}", ParserConfig.getGlobalInstance());
        DefaultObjectDeserializerTest4.Entity entity = new DefaultObjectDeserializerTest4.Entity();
        parser.parseObject(entity);
    }

    public void test_1() throws Exception {
        JSON.parseObject("{\"id\":3, \"name\":\"xx\"}", DefaultObjectDeserializerTest4.Entity.class, 0, IgnoreNotMatch);
    }

    public static class Entity {
        private int id;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }
    }
}

