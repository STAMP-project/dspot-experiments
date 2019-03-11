package com.alibaba.json.bvt.parser;


import com.alibaba.fastjson.JSON;
import java.util.Map;
import junit.framework.TestCase;
import org.junit.Assert;


public class ReadOnlyMapTest2_final_field extends TestCase {
    public void test_readOnlyNullList() throws Exception {
        String text = "{\"values\":{\"a\":{}}}";
        ReadOnlyMapTest2_final_field.Entity entity = JSON.parseObject(text, ReadOnlyMapTest2_final_field.Entity.class);
        Assert.assertNotNull(entity);
        Assert.assertNull(entity.values);
    }

    public static class Entity {
        public final Map<String, ReadOnlyMapTest2_final_field.A> values = null;
    }

    public static class A {}
}

