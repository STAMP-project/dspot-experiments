package com.alibaba.json.bvt.parser;


import com.alibaba.fastjson.JSON;
import java.util.ArrayList;
import java.util.List;
import junit.framework.TestCase;
import org.junit.Assert;


public class ReadOnlyCollectionTest_final_field_null extends TestCase {
    public void test_readOnlyNullList() throws Exception {
        String text = "{\"list\":[1,2,3]}";
        ReadOnlyCollectionTest_final_field_null.Entity entity = JSON.parseObject(text, ReadOnlyCollectionTest_final_field_null.Entity.class);
        Assert.assertNotNull(entity);
        Assert.assertEquals(3, entity.list.size());
    }

    public static class Entity {
        public final List<Object> list = new ArrayList<Object>();
    }
}

