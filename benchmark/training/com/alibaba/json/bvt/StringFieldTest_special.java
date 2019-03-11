package com.alibaba.json.bvt;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONReader;
import java.io.StringReader;
import junit.framework.TestCase;
import org.junit.Assert;


public class StringFieldTest_special extends TestCase {
    public void test_special() throws Exception {
        StringFieldTest_special.Model model = new StringFieldTest_special.Model();
        model.name = "a\\bc";
        String text = JSON.toJSONString(model);
        Assert.assertEquals("{\"name\":\"a\\\\bc\"}", text);
        JSONReader reader = new JSONReader(new StringReader(text));
        StringFieldTest_special.Model model2 = reader.readObject(StringFieldTest_special.Model.class);
        Assert.assertEquals(model.name, model2.name);
        reader.close();
    }

    public void test_special_2() throws Exception {
        StringFieldTest_special.Model model = new StringFieldTest_special.Model();
        model.name = "a\\bc\"";
        String text = JSON.toJSONString(model);
        Assert.assertEquals("{\"name\":\"a\\\\bc\\\"\"}", text);
        JSONReader reader = new JSONReader(new StringReader(text));
        StringFieldTest_special.Model model2 = reader.readObject(StringFieldTest_special.Model.class);
        Assert.assertEquals(model.name, model2.name);
        reader.close();
    }

    public static class Model {
        public String name;
    }
}

