package com.alibaba.json.bvt.parser;


import com.alibaba.fastjson.JSON;
import java.util.Map;
import junit.framework.TestCase;
import org.junit.Assert;


public class ReadOnlyMapTest2 extends TestCase {
    public void test_readOnlyNullList() throws Exception {
        String text = "{\"values\":{\"a\":{}}}";
        ReadOnlyMapTest2.Entity entity = JSON.parseObject(text, ReadOnlyMapTest2.Entity.class);
        Assert.assertNotNull(entity);
        Assert.assertNull(entity.values);
    }

    public static class Entity {
        private Map<String, ReadOnlyMapTest2.A> values;

        public Map<String, ReadOnlyMapTest2.A> getValues() {
            return values;
        }
    }

    public static class A {}
}

