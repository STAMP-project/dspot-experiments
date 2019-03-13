package com.alibaba.json.bvt.parser.creator;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.annotation.JSONCreator;
import com.alibaba.fastjson.annotation.JSONField;
import java.util.List;
import junit.framework.TestCase;
import org.junit.Assert;


public class JSONCreatorTest7 extends TestCase {
    public void test_create() throws Exception {
        JSONCreatorTest7.Entity entity = JSON.parseObject("{\"values\":[{}]}", JSONCreatorTest7.Entity.class);
        Assert.assertEquals(1, entity.values.size());
        Assert.assertEquals(JSONCreatorTest7.Value.class, entity.values.get(0).getClass());
    }

    public static class Entity {
        private final List<JSONCreatorTest7.Value> values;

        @JSONCreator
        public Entity(@JSONField(name = "values")
        List<JSONCreatorTest7.Value> values) {
            this.values = values;
        }

        public List<JSONCreatorTest7.Value> getValues() {
            return values;
        }
    }

    public static class Value {}
}

