package com.alibaba.json.bvt.parser.deser;


import com.alibaba.fastjson.JSON;
import junit.framework.TestCase;
import org.junit.Assert;


public class FieldDeserializerTest10 extends TestCase {
    public void test_0() throws Exception {
        Assert.assertEquals(FieldDeserializerTest10.Type.Big, JSON.parseObject("{\"id\":\"Big\"\t}", FieldDeserializerTest10.VO.class).id);
        Assert.assertEquals(FieldDeserializerTest10.Type.Big, JSON.parseObject("{\"id\":\"Big\"\t}\n\t", FieldDeserializerTest10.VO.class).id);
        Assert.assertEquals(FieldDeserializerTest10.Type.Big, JSON.parseObject("{\"id\":\"Big\" }", FieldDeserializerTest10.V1.class).id);
        Assert.assertEquals(FieldDeserializerTest10.Type.Big, JSON.parseObject("{\"id\":\"Big\" }\n", FieldDeserializerTest10.V1.class).id);
        Assert.assertEquals(FieldDeserializerTest10.Type.Big, JSON.parseObject("{\"id\":\"Big\" }\n\t", FieldDeserializerTest10.V1.class).id);
        Assert.assertEquals(FieldDeserializerTest10.Type.Big, JSON.parseObject("{\"id\":\"Big\"\n}", FieldDeserializerTest10.V1.class).id);
    }

    public static class VO {
        public FieldDeserializerTest10.Type id;
    }

    private static class V1 {
        public FieldDeserializerTest10.Type id;
    }

    public static enum Type {

        Big,
        Small;}
}

