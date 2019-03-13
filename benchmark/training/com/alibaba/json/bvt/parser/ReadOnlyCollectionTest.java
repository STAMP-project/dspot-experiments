package com.alibaba.json.bvt.parser;


import com.alibaba.fastjson.JSON;
import java.util.List;
import junit.framework.TestCase;
import org.junit.Assert;


public class ReadOnlyCollectionTest extends TestCase {
    public void test_readOnlyNullList() throws Exception {
        String text = "{\"list\":[]}";
        ReadOnlyCollectionTest.Entity entity = JSON.parseObject(text, ReadOnlyCollectionTest.Entity.class);
        Assert.assertNotNull(entity);
    }

    public static class Entity {
        private List<Object> list;

        public List<Object> getList() {
            return list;
        }
    }
}

