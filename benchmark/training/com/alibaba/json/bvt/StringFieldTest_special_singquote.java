package com.alibaba.json.bvt;


import SerializerFeature.UseSingleQuotes;
import com.alibaba.fastjson.JSON;
import java.io.StringWriter;
import junit.framework.TestCase;
import org.junit.Assert;


public class StringFieldTest_special_singquote extends TestCase {
    public void test_special() throws Exception {
        StringFieldTest_special_singquote.Model model = new StringFieldTest_special_singquote.Model();
        StringBuilder buf = new StringBuilder();
        for (int i = Character.MIN_VALUE; i < (Character.MAX_VALUE); ++i) {
            buf.append(((char) (i)));
        }
        model.name = buf.toString();
        StringWriter writer = new StringWriter();
        JSON.writeJSONString(writer, model);
        StringFieldTest_special_singquote.Model model2 = JSON.parseObject(writer.toString(), StringFieldTest_special_singquote.Model.class);
        Assert.assertEquals(model.name, model2.name);
    }

    public void test_special_browsecue() throws Exception {
        StringFieldTest_special_singquote.Model model = new StringFieldTest_special_singquote.Model();
        StringBuilder buf = new StringBuilder();
        for (int i = Character.MIN_VALUE; i < (Character.MAX_VALUE); ++i) {
            buf.append(((char) (i)));
        }
        model.name = buf.toString();
        StringWriter writer = new StringWriter();
        JSON.writeJSONString(writer, model, UseSingleQuotes);
        StringFieldTest_special_singquote.Model model2 = JSON.parseObject(writer.toString(), StringFieldTest_special_singquote.Model.class);
        Assert.assertEquals(model.name, model2.name);
    }

    public void test_special_browsecompatible() throws Exception {
        StringFieldTest_special_singquote.Model model = new StringFieldTest_special_singquote.Model();
        StringBuilder buf = new StringBuilder();
        for (int i = Character.MIN_VALUE; i < (Character.MAX_VALUE); ++i) {
            buf.append(((char) (i)));
        }
        model.name = buf.toString();
        StringWriter writer = new StringWriter();
        JSON.writeJSONString(writer, model, UseSingleQuotes);
        StringFieldTest_special_singquote.Model model2 = JSON.parseObject(writer.toString(), StringFieldTest_special_singquote.Model.class);
        Assert.assertEquals(model.name, model2.name);
    }

    private static class Model {
        public String name;
    }
}

