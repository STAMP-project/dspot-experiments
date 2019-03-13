package com.alibaba.json.bvt.parser.deser;


import com.alibaba.fastjson.JSON;
import junit.framework.TestCase;
import org.junit.Assert;


public class FieldDeserializerTest6 extends TestCase {
    public void test_0() throws Exception {
        Assert.assertTrue((33.0F == (JSON.parseObject("{\"id\":33\t}", FieldDeserializerTest6.VO.class).id)));
        Assert.assertTrue((33.0F == (JSON.parseObject("{\"id\":33\t}\n\t", FieldDeserializerTest6.VO.class).id)));
        Assert.assertTrue((33.0F == (JSON.parseObject("{\"id\":33 }", FieldDeserializerTest6.V1.class).id)));
        Assert.assertTrue((33.0F == (JSON.parseObject("{\"id\":33 }\n\t", FieldDeserializerTest6.V1.class).id)));
        Assert.assertTrue((33.0F == (JSON.parseObject("{\"id\":33L}", FieldDeserializerTest6.V1.class).id)));
    }

    public static class VO {
        public float id;
    }

    private static class V1 {
        public float id;
    }
}

