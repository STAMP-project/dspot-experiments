package com.alibaba.json.bvt.jdk8;


import com.alibaba.fastjson.JSON;
import java.util.OptionalInt;
import junit.framework.TestCase;
import org.junit.Assert;


public class OptionalInt_Test extends TestCase {
    public void test_optional() throws Exception {
        OptionalInt_Test.Model model = new OptionalInt_Test.Model();
        model.value = OptionalInt.empty();
        String text = JSON.toJSONString(model);
        Assert.assertEquals("{\"value\":null}", text);
        OptionalInt_Test.Model model2 = JSON.parseObject(text, OptionalInt_Test.Model.class);
        Assert.assertEquals(model2.value, model.value);
    }

    public static class Model {
        public OptionalInt value;
    }
}

