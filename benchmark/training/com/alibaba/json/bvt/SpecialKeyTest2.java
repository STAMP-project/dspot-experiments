package com.alibaba.json.bvt;


import com.alibaba.fastjson.JSON;
import java.util.Map;
import junit.framework.TestCase;
import org.junit.Assert;


public class SpecialKeyTest2 extends TestCase {
    public void test_0() throws Exception {
        SpecialKeyTest2.Model model = JSON.parseObject("{\"items\":{\"1\":{},\"1001\":{}},\"items1\":{\"$ref\":\"$.items\"}}", SpecialKeyTest2.Model.class);
        Assert.assertEquals(2, model.items.size());
        Assert.assertNotNull(model.items.get(1L));
        Assert.assertNotNull(model.items.get(1001L));
        Assert.assertSame(model.items, model.items1);
    }

    public static class Model {
        public Map<Long, SpecialKeyTest2.Item> items;

        public Map<Long, SpecialKeyTest2.Item> items1;
    }

    public static class Item {}
}

