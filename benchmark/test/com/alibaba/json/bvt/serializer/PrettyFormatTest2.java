package com.alibaba.json.bvt.serializer;


import SerializerFeature.PrettyFormat;
import com.alibaba.fastjson.JSON;
import junit.framework.TestCase;
import org.junit.Assert;


public class PrettyFormatTest2 extends TestCase {
    public void test_0() throws Exception {
        PrettyFormatTest2.Model model = new PrettyFormatTest2.Model();
        model.id = 123;
        model.name = "wenshao";
        String text = JSON.toJSONString(model, PrettyFormat);
        TestCase.assertEquals(("{\n" + (("\t\"id\":123,\n" + "\t\"name\":\"wenshao\"\n") + "}")), text);
        Assert.assertEquals("[\n\t{},\n\t{}\n]", JSON.toJSONString(new Object[]{ new Object(), new Object() }, PrettyFormat));
    }

    public static class Model {
        public int id;

        public String name;
    }
}

