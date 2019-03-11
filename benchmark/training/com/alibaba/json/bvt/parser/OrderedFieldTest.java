package com.alibaba.json.bvt.parser;


import Feature.OrderedField;
import com.alibaba.fastjson.JSON;
import junit.framework.TestCase;
import org.junit.Assert;


public class OrderedFieldTest extends TestCase {
    public void test_ordered_field() throws Exception {
        String text = "{\"id\":1001}";
        OrderedFieldTest.Model model = JSON.parseObject(text, OrderedFieldTest.Model.class, OrderedField);
        Assert.assertEquals(1001, model.getId());
        String text2 = JSON.toJSONString(model);
        Assert.assertEquals(text, text2);
    }

    public static interface Model {
        public int getId();

        public void setId(int value);
    }
}

