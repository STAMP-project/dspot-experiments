package com.alibaba.json.bvt.parser.deser.list;


import Feature.SupportArrayToBean;
import com.alibaba.fastjson.JSON;
import java.util.List;
import junit.framework.TestCase;
import org.junit.Assert;


public class ListStringFieldTest extends TestCase {
    public void test_list() throws Exception {
        String text = "[[\"a\",null,\"b\"]]";
        ListStringFieldTest.Model model = JSON.parseObject(text, ListStringFieldTest.Model.class, SupportArrayToBean);
        Assert.assertEquals(3, model.values.size());
        Assert.assertEquals("a", model.values.get(0));
        Assert.assertEquals(null, model.values.get(1));
        Assert.assertEquals("b", model.values.get(2));
    }

    public void test_null() throws Exception {
        String text = "[null]";
        ListStringFieldTest.Model model = JSON.parseObject(text, ListStringFieldTest.Model.class, SupportArrayToBean);
        Assert.assertNull(model.values);
    }

    public static class Model {
        private List<String> values;

        public List<String> getValues() {
            return values;
        }

        public void setValues(List<String> values) {
            this.values = values;
        }
    }
}

