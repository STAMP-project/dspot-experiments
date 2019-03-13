package com.alibaba.json.bvt;


import com.alibaba.fastjson.JSON;
import junit.framework.TestCase;
import org.junit.Assert;


public class StringFieldTest_special_reader extends TestCase {
    public void test_special() throws Exception {
        StringFieldTest_special_reader.Model model = new StringFieldTest_special_reader.Model();
        model.name = "a\\bc";
        String text = JSON.toJSONString(model);
        Assert.assertEquals("{\"name\":\"a\\\\bc\"}", text);
        StringFieldTest_special_reader.Model model2 = JSON.parseObject(text, StringFieldTest_special_reader.Model.class);
        Assert.assertEquals(model.name, model2.name);
    }

    public void test_special_2() throws Exception {
        StringFieldTest_special_reader.Model model = new StringFieldTest_special_reader.Model();
        model.name = "a\\bc\"";
        String text = JSON.toJSONString(model);
        Assert.assertEquals("{\"name\":\"a\\\\bc\\\"\"}", text);
        StringFieldTest_special_reader.Model model2 = JSON.parseObject(text, StringFieldTest_special_reader.Model.class);
        Assert.assertEquals(model.name, model2.name);
    }

    public static class Model {
        public String name;
    }
}

