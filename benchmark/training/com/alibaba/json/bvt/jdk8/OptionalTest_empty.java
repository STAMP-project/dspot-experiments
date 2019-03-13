package com.alibaba.json.bvt.jdk8;


import com.alibaba.fastjson.JSON;
import java.util.Optional;
import junit.framework.TestCase;
import org.junit.Assert;


public class OptionalTest_empty extends TestCase {
    public void test_optional() throws Exception {
        OptionalTest_empty.Model model = new OptionalTest_empty.Model();
        model.value = Optional.empty();
        String text = JSON.toJSONString(model);
        Assert.assertEquals("{\"value\":null}", text);
        OptionalTest_empty.Model model2 = JSON.parseObject(text, OptionalTest_empty.Model.class);
        Assert.assertEquals(model2.value, model.value);
    }

    public static class Model {
        public Optional<OptionalTest_empty.Value> value;
    }

    public static class Value {}
}

