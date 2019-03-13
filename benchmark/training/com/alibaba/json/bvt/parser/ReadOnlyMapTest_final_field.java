package com.alibaba.json.bvt.parser;


import com.alibaba.fastjson.JSON;
import java.util.HashMap;
import java.util.Map;
import junit.framework.TestCase;
import org.junit.Assert;


public class ReadOnlyMapTest_final_field extends TestCase {
    public void test_readOnlyNullList() throws Exception {
        String text = "{\"values\":{\"a\":{}}}";
        ReadOnlyMapTest_final_field.Entity entity = JSON.parseObject(text, ReadOnlyMapTest_final_field.Entity.class);
        Assert.assertNotNull(entity);
        Assert.assertNotNull(entity.values.get("a"));
        Assert.assertTrue(((entity.values.get("a")) instanceof ReadOnlyMapTest_final_field.A));
    }

    public static class Entity {
        public final Map<String, ReadOnlyMapTest_final_field.A> values = new HashMap<String, ReadOnlyMapTest_final_field.A>();
    }

    public static class A {}
}

