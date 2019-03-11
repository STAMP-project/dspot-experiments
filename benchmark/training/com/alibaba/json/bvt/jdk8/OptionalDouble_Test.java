package com.alibaba.json.bvt.jdk8;


import com.alibaba.fastjson.JSON;
import java.util.OptionalDouble;
import junit.framework.TestCase;
import org.junit.Assert;


public class OptionalDouble_Test extends TestCase {
    public void test_optional() throws Exception {
        OptionalDouble_Test.Model model = new OptionalDouble_Test.Model();
        model.value = OptionalDouble.empty();
        String text = JSON.toJSONString(model);
        Assert.assertEquals("{\"value\":null}", text);
        OptionalDouble_Test.Model model2 = JSON.parseObject(text, OptionalDouble_Test.Model.class);
        Assert.assertEquals(model2.value, model.value);
    }

    public static class Model {
        public OptionalDouble value;
    }
}

