package com.alibaba.json.bvt.parser.deser;


import com.alibaba.fastjson.JSON;
import java.util.HashMap;
import junit.framework.TestCase;


/**
 * Created by wenshao on 25/03/2017.
 */
public class InnerClassDeser4 extends TestCase {
    public void test_for_inner_class() throws Exception {
        InnerClassDeser4.Model model = JSON.parseObject("{\"items\":{\"123\":{\"id\":123}}}", InnerClassDeser4.Model.class);
        TestCase.assertNotNull(model.items);
        TestCase.assertEquals(123, model.items.get("123").id);
    }

    public static class Model {
        public HashMap<String, InnerClassDeser4.Model.Item> items;

        public class Item {
            public int id;
        }
    }
}

