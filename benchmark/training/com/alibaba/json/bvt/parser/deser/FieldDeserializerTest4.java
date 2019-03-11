package com.alibaba.json.bvt.parser.deser;


import com.alibaba.fastjson.JSON;
import junit.framework.TestCase;
import org.junit.Assert;


public class FieldDeserializerTest4 extends TestCase {
    public void test_0() throws Exception {
        Assert.assertEquals(33, JSON.parseObject("{\"id\":33\t}", FieldDeserializerTest4.VO.class).id);
        Assert.assertEquals(33, JSON.parseObject("{\"id\":33\t}\n\t", FieldDeserializerTest4.VO.class).id);
        Assert.assertEquals(33, JSON.parseObject("{\"id\":33 }", FieldDeserializerTest4.V1.class).id);
        Assert.assertEquals(33, JSON.parseObject("{\"id\":33 }\n\t", FieldDeserializerTest4.V1.class).id);
        Assert.assertEquals(33, JSON.parseObject("{\"id\":33L}", FieldDeserializerTest4.V1.class).id);
    }

    public static class VO {
        public long id;
    }

    private static class V1 {
        public long id;
    }
}

