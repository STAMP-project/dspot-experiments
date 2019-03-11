package com.alibaba.json.bvt;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializeConfig;
import junit.framework.TestCase;
import org.junit.Assert;


public class JSONTest_overflow extends TestCase {
    public void test_overflow() throws Exception {
        JSONTest_overflow.Entity entity = new JSONTest_overflow.Entity();
        entity.setSelf(entity);
        String text = JSON.toJSONString(entity, SerializeConfig.getGlobalInstance());
        JSONTest_overflow.Entity entity2 = JSON.parseObject(text, JSONTest_overflow.Entity.class);
        Assert.assertTrue((entity2 == (entity2.getSelf())));
    }

    public void test_overflow_1() throws Exception {
        JSONTest_overflow.Entity entity = new JSONTest_overflow.Entity();
        entity.setSelf(entity);
        String text = JSON.toJSONStringZ(entity, SerializeConfig.getGlobalInstance());
        JSONTest_overflow.Entity entity2 = JSON.parseObject(text, JSONTest_overflow.Entity.class);
        Assert.assertTrue((entity2 == (entity2.getSelf())));
    }

    public static class Entity {
        private JSONTest_overflow.Entity self;

        public JSONTest_overflow.Entity getSelf() {
            return self;
        }

        public void setSelf(JSONTest_overflow.Entity self) {
            this.self = self;
        }
    }
}

