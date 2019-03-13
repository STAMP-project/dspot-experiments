package com.alibaba.json.bvt.parser.deser;


import com.alibaba.fastjson.JSON;
import junit.framework.TestCase;
import org.junit.Assert;


public class FieldDeserializerTest8 extends TestCase {
    public void test_0() throws Exception {
        Assert.assertEquals("33", JSON.parseObject("{\"id\":\"33\"\t}", FieldDeserializerTest8.VO.class).id);
        Assert.assertEquals("33", JSON.parseObject("{\"id\":\"33\"\t}\n\t", FieldDeserializerTest8.VO.class).id);
        Assert.assertEquals("33", JSON.parseObject("{\"id\":\"33\" }", FieldDeserializerTest8.V1.class).id);
        Assert.assertEquals("33", JSON.parseObject("{\"id\":\"33\" }\n\t", FieldDeserializerTest8.V1.class).id);
        Assert.assertEquals("33", JSON.parseObject("{\"id\":\"33\"\n}", FieldDeserializerTest8.V1.class).id);
    }

    public static class VO {
        public String id;
    }

    private static class V1 {
        public String id;
    }
}

