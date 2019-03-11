package com.alibaba.json.bvt.bug;


import SerializerFeature.NotWriteRootClassName;
import SerializerFeature.WriteClassName;
import com.alibaba.fastjson.JSON;
import java.lang.reflect.Field;
import java.util.List;
import junit.framework.TestCase;


/**
 * Created by wenshao on 16/01/2017.
 */
public class Issue998 extends TestCase {
    public void test_for_issue() throws Exception {
        Issue998.Model model = JSON.parseObject("{\"items\":[{\"id\":123}]}", Issue998.Model.class);
        TestCase.assertNotNull(model);
        TestCase.assertNotNull(model.items);
        TestCase.assertEquals(1, model.items.size());
        TestCase.assertEquals(123, model.items.get(0).getId());
        String json = JSON.toJSONString(model, NotWriteRootClassName, WriteClassName);
        TestCase.assertEquals("{\"items\":[{\"id\":123}]}", json);
    }

    public void test_for_issue_1() throws Exception {
        Field field = Issue998.Model.class.getField("items");
        List<Issue998.Item> items = ((List<Issue998.Item>) (JSON.parseObject("[{\"id\":123}]", field.getGenericType())));
        TestCase.assertNotNull(items);
        TestCase.assertEquals(1, items.size());
        TestCase.assertEquals(123, items.get(0).id);
    }

    public static class Model {
        public List<? extends Issue998.Item> items;
    }

    public static class Item {
        private int id;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }
    }
}

