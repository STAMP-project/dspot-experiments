package com.alibaba.json.bvt.jdk8;


import com.alibaba.fastjson.JSON;
import java.util.Optional;
import junit.framework.TestCase;
import org.junit.Assert;


public class OptionalTest2 extends TestCase {
    public void test_optional() throws Exception {
        OptionalTest2.Entity entity = new OptionalTest2.Entity();
        entity.setValue(Optional.of(123));
        String text = JSON.toJSONString(entity);
        Assert.assertEquals("{\"value\":123}", text);
        OptionalTest2.Entity entity2 = JSON.parseObject(text, OptionalTest2.Entity.class);
        Assert.assertEquals(entity.getValue().get(), entity2.getValue().get());
    }

    public static class Entity {
        private Optional<Integer> value;

        public Optional<Integer> getValue() {
            return value;
        }

        public void setValue(Optional<Integer> value) {
            this.value = value;
        }
    }
}

