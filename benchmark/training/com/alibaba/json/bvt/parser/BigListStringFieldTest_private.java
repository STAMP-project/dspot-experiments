package com.alibaba.json.bvt.parser;


import SerializerFeature.BrowserCompatible;
import SerializerFeature.BrowserSecure;
import com.alibaba.fastjson.JSON;
import java.util.ArrayList;
import java.util.List;
import junit.framework.TestCase;
import org.junit.Assert;


public class BigListStringFieldTest_private extends TestCase {
    public void test_list() throws Exception {
        BigListStringFieldTest_private.Model model = new BigListStringFieldTest_private.Model();
        model.values = new ArrayList<String>(10000);
        for (int i = 0; i < 10000; ++i) {
            String value = random(100);
            model.values.add(value);
        }
        String text = JSON.toJSONString(model);
        BigListStringFieldTest_private.Model model2 = JSON.parseObject(text, BigListStringFieldTest_private.Model.class);
        Assert.assertEquals(model.values, model2.values);
    }

    public void test_list_browserComptible() throws Exception {
        BigListStringFieldTest_private.Model model = new BigListStringFieldTest_private.Model();
        model.values = new ArrayList<String>(10000);
        for (int i = 0; i < 10000; ++i) {
            String value = random(100);
            model.values.add(value);
        }
        String text = JSON.toJSONString(model, BrowserCompatible);
        BigListStringFieldTest_private.Model model2 = JSON.parseObject(text, BigListStringFieldTest_private.Model.class);
        Assert.assertEquals(model.values, model2.values);
    }

    public void test_list_browserSecure() throws Exception {
        BigListStringFieldTest_private.Model model = new BigListStringFieldTest_private.Model();
        model.values = new ArrayList<String>(10000);
        for (int i = 0; i < 10000; ++i) {
            String value = random(100);
            model.values.add(value);
        }
        String text = JSON.toJSONString(model, BrowserSecure);
        text = text.replaceAll("&lt;", "<");
        text = text.replaceAll("&gt;", ">");
        BigListStringFieldTest_private.Model model2 = JSON.parseObject(text, BigListStringFieldTest_private.Model.class);
        Assert.assertEquals(model.values, model2.values);
    }

    private static class Model {
        public List<String> values;
    }
}

