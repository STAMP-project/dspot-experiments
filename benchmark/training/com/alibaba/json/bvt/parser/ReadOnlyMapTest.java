package com.alibaba.json.bvt.parser;


import com.alibaba.fastjson.JSON;
import java.util.HashMap;
import java.util.Map;
import junit.framework.TestCase;
import org.junit.Assert;


public class ReadOnlyMapTest extends TestCase {
    public void test_readOnlyNullList() throws Exception {
        String text = "{\"values\":{\"a\":{}}}";
        ReadOnlyMapTest.Entity entity = JSON.parseObject(text, ReadOnlyMapTest.Entity.class);
        Assert.assertNotNull(entity);
        Assert.assertNotNull(entity.values.get("a"));
        Assert.assertTrue(((entity.values.get("a")) instanceof ReadOnlyMapTest.A));
    }

    public static class Entity {
        private final Map<String, ReadOnlyMapTest.A> values = new HashMap<String, ReadOnlyMapTest.A>();

        public Map<String, ReadOnlyMapTest.A> getValues() {
            return values;
        }
    }

    public static class A {
        public A() {
        }
    }
}

