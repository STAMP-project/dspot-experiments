package com.alibaba.json.bvt.parser.deser.list;


import Feature.SupportArrayToBean;
import SerializerFeature.BeanToArray;
import com.alibaba.fastjson.JSON;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import junit.framework.TestCase;
import org.junit.Assert;


public class ListStringFieldTest_array_big extends TestCase {
    public void test_list() throws Exception {
        ListStringFieldTest_array_big.Model model = new ListStringFieldTest_array_big.Model();
        model.values = new ArrayList<String>();
        for (int i = 0; i < 1000; ++i) {
            char[] chars = new char[512];
            Arrays.fill(chars, ((char) ('0' + (i % 10))));
            model.values.add(new String(chars));
        }
        String text = JSON.toJSONString(model, BeanToArray);
        ListStringFieldTest_array_big.Model model2 = JSON.parseObject(text, ListStringFieldTest_array_big.Model.class, SupportArrayToBean);
        Assert.assertEquals(model.values.size(), model2.values.size());
        for (int i = 0; i < (model.values.size()); ++i) {
            Assert.assertEquals(model.values.get(i), model2.values.get(i));
        }
    }

    public void test_list_empty() throws Exception {
        ListStringFieldTest_array_big.Model model = new ListStringFieldTest_array_big.Model();
        model.values = new ArrayList<String>();
        String text = JSON.toJSONString(model, BeanToArray);
        ListStringFieldTest_array_big.Model model2 = JSON.parseObject(text, ListStringFieldTest_array_big.Model.class, SupportArrayToBean);
        Assert.assertEquals(model.values.size(), model2.values.size());
        for (int i = 0; i < (model.values.size()); ++i) {
            Assert.assertEquals(model.values.get(i), model2.values.get(i));
        }
    }

    public static class Model {
        public List<String> values;
    }
}

