package com.alibaba.json.bvt.parser.deser;


import com.alibaba.fastjson.JSON;
import junit.framework.TestCase;
import org.junit.Assert;


public class FieldDeserializerTest5 extends TestCase {
    public void test_0() throws Exception {
        Assert.assertEquals(33, JSON.parseObject("{\"id\":33\t}", FieldDeserializerTest5.VO.class).id);
        Assert.assertEquals(33, JSON.parseObject("{\"id\":33\t}\n\t", FieldDeserializerTest5.VO.class).id);
        Assert.assertEquals(33, JSON.parseObject("{\"id\":33 }", FieldDeserializerTest5.V1.class).id);
        Assert.assertEquals(33, JSON.parseObject("{\"id\":33 } ", FieldDeserializerTest5.V1.class).id);
        Assert.assertEquals(33, JSON.parseObject("{\"id\":33 }\n", FieldDeserializerTest5.V1.class).id);
        Assert.assertEquals(33, JSON.parseObject("{\"id\":33 }\t\n", FieldDeserializerTest5.V1.class).id);
        Assert.assertEquals(33, JSON.parseObject("{\"id\":33L}", FieldDeserializerTest5.V1.class).id);
    }

    public static class VO {
        public int id;
    }

    private static class V1 {
        public int id;
    }
}

