package com.alibaba.json.bvt;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.annotation.JSONField;
import junit.framework.TestCase;
import org.junit.Assert;


public class ByteArrayFieldTest_4 extends TestCase {
    public void test_0() throws Exception {
        ByteArrayFieldTest_4.Model model = new ByteArrayFieldTest_4.Model();
        model.value = "ABCDEG".getBytes();
        String json = JSON.toJSONString(model);
        TestCase.assertEquals("{\"value\":x\'414243444547\'}", json);
        ByteArrayFieldTest_4.Model model1 = JSON.parseObject(json, ByteArrayFieldTest_4.Model.class);
        Assert.assertArrayEquals(model.value, model1.value);
    }

    private static class Model {
        @JSONField(format = "hex")
        public byte[] value;
    }
}

