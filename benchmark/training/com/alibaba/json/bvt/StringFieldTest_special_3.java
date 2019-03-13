package com.alibaba.json.bvt;


import SerializerFeature.BrowserCompatible;
import SerializerFeature.BrowserSecure;
import com.alibaba.fastjson.JSON;
import java.io.StringWriter;
import junit.framework.TestCase;
import org.junit.Assert;


public class StringFieldTest_special_3 extends TestCase {
    public void test_special() throws Exception {
        StringFieldTest_special_3.Model model = new StringFieldTest_special_3.Model();
        StringBuilder buf = new StringBuilder();
        for (int i = Character.MIN_VALUE; i < (Character.MAX_VALUE); ++i) {
            buf.append(((char) (i)));
        }
        model.name = buf.toString();
        StringWriter writer = new StringWriter();
        JSON.writeJSONString(writer, model);
        String json = writer.toString();
        StringFieldTest_special_3.Model model2 = JSON.parseObject(json, StringFieldTest_special_3.Model.class);
        Assert.assertEquals(model.name, model2.name);
    }

    public void test_special_browsecue() throws Exception {
        StringFieldTest_special_3.Model model = new StringFieldTest_special_3.Model();
        StringBuilder buf = new StringBuilder();
        for (int i = Character.MIN_VALUE; i < (Character.MAX_VALUE); ++i) {
            buf.append(((char) (i)));
        }
        model.name = buf.toString();
        StringWriter writer = new StringWriter();
        JSON.writeJSONString(writer, model, BrowserSecure);
        String text = writer.toString();
        text = text.replaceAll("&lt;", "<");
        text = text.replaceAll("&gt;", ">");
        StringFieldTest_special_3.Model model2 = JSON.parseObject(text, StringFieldTest_special_3.Model.class);
        TestCase.assertEquals(model.name, model2.name);
    }

    public void test_special_browsecompatible() throws Exception {
        StringFieldTest_special_3.Model model = new StringFieldTest_special_3.Model();
        StringBuilder buf = new StringBuilder();
        for (int i = Character.MIN_VALUE; i < (Character.MAX_VALUE); ++i) {
            buf.append(((char) (i)));
        }
        model.name = buf.toString();
        StringWriter writer = new StringWriter();
        JSON.writeJSONString(writer, model, BrowserCompatible);
        StringFieldTest_special_3.Model model2 = JSON.parseObject(writer.toString(), StringFieldTest_special_3.Model.class);
        Assert.assertEquals(model.name, model2.name);
    }

    private static class Model {
        public String name;
    }
}

