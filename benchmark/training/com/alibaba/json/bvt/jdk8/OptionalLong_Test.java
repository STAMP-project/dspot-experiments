package com.alibaba.json.bvt.jdk8;


import com.alibaba.fastjson.JSON;
import java.util.OptionalLong;
import junit.framework.TestCase;
import org.junit.Assert;


public class OptionalLong_Test extends TestCase {
    public void test_optional() throws Exception {
        OptionalLong_Test.Model model = new OptionalLong_Test.Model();
        model.value = OptionalLong.empty();
        String text = JSON.toJSONString(model);
        Assert.assertEquals("{\"value\":null}", text);
        OptionalLong_Test.Model model2 = JSON.parseObject(text, OptionalLong_Test.Model.class);
        Assert.assertEquals(model2.value, model.value);
    }

    public static class Model {
        public OptionalLong value;
    }
}

