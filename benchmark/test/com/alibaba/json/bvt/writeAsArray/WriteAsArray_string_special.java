package com.alibaba.json.bvt.writeAsArray;


import Feature.SupportArrayToBean;
import SerializerFeature.BeanToArray;
import com.alibaba.fastjson.JSON;
import junit.framework.TestCase;
import org.junit.Assert;


public class WriteAsArray_string_special extends TestCase {
    public void test_0() throws Exception {
        WriteAsArray_string_special.Model model = new WriteAsArray_string_special.Model();
        model.name = "a\\bc";
        String text = JSON.toJSONString(model, BeanToArray);
        Assert.assertEquals("[\"a\\\\bc\"]", text);
        WriteAsArray_string_special.Model model2 = JSON.parseObject(text, WriteAsArray_string_special.Model.class, SupportArrayToBean);
        Assert.assertEquals(model.name, model2.name);
    }

    public void test_1() throws Exception {
        WriteAsArray_string_special.Model model = new WriteAsArray_string_special.Model();
        model.name = "a\\bc\"";
        String text = JSON.toJSONString(model, BeanToArray);
        Assert.assertEquals("[\"a\\\\bc\\\"\"]", text);
        WriteAsArray_string_special.Model model2 = JSON.parseObject(text, WriteAsArray_string_special.Model.class, SupportArrayToBean);
        Assert.assertEquals(model.name, model2.name);
    }

    public static class Model {
        public String name;
    }
}

