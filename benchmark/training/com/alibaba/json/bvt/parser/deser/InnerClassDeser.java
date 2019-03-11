package com.alibaba.json.bvt.parser.deser;


import com.alibaba.fastjson.JSON;
import junit.framework.TestCase;


/**
 * Created by wenshao on 25/03/2017.
 */
public class InnerClassDeser extends TestCase {
    public void test_for_inner_class() throws Exception {
        InnerClassDeser.Model model = JSON.parseObject("{\"item\":{\"id\":123}}", InnerClassDeser.Model.class);
        TestCase.assertNotNull(model.item);
        TestCase.assertEquals(123, model.item.id);
    }

    public static class Model {
        public InnerClassDeser.Model.Item item;

        public class Item {
            public int id;
        }
    }
}

