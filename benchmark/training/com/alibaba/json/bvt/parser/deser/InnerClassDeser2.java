package com.alibaba.json.bvt.parser.deser;


import com.alibaba.fastjson.JSON;
import java.util.List;
import junit.framework.TestCase;


/**
 * Created by wenshao on 25/03/2017.
 */
public class InnerClassDeser2 extends TestCase {
    public void test_for_inner_class() throws Exception {
        InnerClassDeser2.Model model = JSON.parseObject("{\"items\":[{\"id\":123}]}", InnerClassDeser2.Model.class);
        TestCase.assertNotNull(model.items);
        TestCase.assertEquals(123, model.items.get(0).id);
    }

    public static class Model {
        public List<InnerClassDeser2.Model.Item> items;

        public class Item {
            public int id;
        }
    }
}

