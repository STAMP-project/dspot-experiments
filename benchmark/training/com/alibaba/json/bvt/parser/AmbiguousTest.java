package com.alibaba.json.bvt.parser;


import com.alibaba.fastjson.JSON;
import java.util.List;
import junit.framework.TestCase;


/**
 * Created by wenshao on 16/8/31.
 */
public class AmbiguousTest extends TestCase {
    public void test_for_issue() throws Exception {
        String text = "{\"items\":{\"id\":101}}";
        AmbiguousTest.Model model = JSON.parseObject(text, AmbiguousTest.Model.class);
        TestCase.assertEquals(1, model.items.size());
    }

    public static class Model {
        public List<AmbiguousTest.Item> items;
    }

    public static class Item {
        public int id;
    }
}

