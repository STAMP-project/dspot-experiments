package com.alibaba.json.bvt.writeAsArray;


import Feature.SupportArrayToBean;
import SerializerFeature.BeanToArray;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONReader;
import java.io.StringReader;
import junit.framework.TestCase;
import org.junit.Assert;


public class WriteAsArray_string_special_Reader extends TestCase {
    public void test_0() throws Exception {
        WriteAsArray_string_special_Reader.Model model = new WriteAsArray_string_special_Reader.Model();
        model.name = "a\\bc";
        String text = JSON.toJSONString(model, BeanToArray);
        Assert.assertEquals("[\"a\\\\bc\"]", text);
        JSONReader reader = new JSONReader(new StringReader(text));
        reader.config(SupportArrayToBean, true);
        WriteAsArray_string_special_Reader.Model model2 = reader.readObject(WriteAsArray_string_special_Reader.Model.class);
        Assert.assertEquals(model.name, model2.name);
        reader.close();
    }

    public void test_1() throws Exception {
        WriteAsArray_string_special_Reader.Model model = new WriteAsArray_string_special_Reader.Model();
        model.name = "a\\bc\"";
        String text = JSON.toJSONString(model, BeanToArray);
        Assert.assertEquals("[\"a\\\\bc\\\"\"]", text);
        JSONReader reader = new JSONReader(new StringReader(text));
        reader.config(SupportArrayToBean, true);
        WriteAsArray_string_special_Reader.Model model2 = reader.readObject(WriteAsArray_string_special_Reader.Model.class);
        Assert.assertEquals(model.name, model2.name);
        reader.close();
    }

    public static class Model {
        public String name;
    }
}

