package com.alibaba.json.bvt;


import SerializerFeature.BrowserCompatible;
import SerializerFeature.BrowserSecure;
import com.alibaba.fastjson.JSON;
import junit.framework.TestCase;
import org.junit.Assert;


public class StringFieldTest_special_1 extends TestCase {
    public void test_special() throws Exception {
        StringFieldTest_special_1.Model model = new StringFieldTest_special_1.Model();
        StringBuilder buf = new StringBuilder();
        for (int i = Character.MIN_VALUE; i < (Character.MAX_VALUE); ++i) {
            buf.append(((char) (i)));
        }
        model.name = buf.toString();
        String text = JSON.toJSONString(model);
        StringFieldTest_special_1.Model model2 = JSON.parseObject(text, StringFieldTest_special_1.Model.class);
        Assert.assertEquals(model.name, model2.name);
    }

    public void test_special_browsecue() throws Exception {
        StringFieldTest_special_1.Model model = new StringFieldTest_special_1.Model();
        StringBuilder buf = new StringBuilder();
        for (int i = Character.MIN_VALUE; i < (Character.MAX_VALUE); ++i) {
            buf.append(((char) (i)));
        }
        model.name = buf.toString();
        String text = JSON.toJSONString(model, BrowserSecure);
        text = text.replaceAll("&lt;", "<");
        text = text.replaceAll("&gt;", ">");
        StringFieldTest_special_1.Model model2 = JSON.parseObject(text, StringFieldTest_special_1.Model.class);
        Assert.assertEquals(model.name, model2.name);
    }

    public void test_special_browsecompatible() throws Exception {
        StringFieldTest_special_1.Model model = new StringFieldTest_special_1.Model();
        StringBuilder buf = new StringBuilder();
        for (int i = Character.MIN_VALUE; i < (Character.MAX_VALUE); ++i) {
            buf.append(((char) (i)));
        }
        model.name = buf.toString();
        String text = JSON.toJSONString(model, BrowserCompatible);
        StringFieldTest_special_1.Model model2 = JSON.parseObject(text, StringFieldTest_special_1.Model.class);
        Assert.assertEquals(model.name, model2.name);
    }

    public static class Model {
        public String name;
    }
}

