package com.alibaba.json.bvt.parser.deser;


import com.alibaba.fastjson.JSON;
import junit.framework.TestCase;


public class FieldDeserializerTest9 extends TestCase {
    public void test_0() throws Exception {
        TestCase.assertTrue(JSON.parseObject("{\"id\":true\t}", FieldDeserializerTest9.VO.class).id);
        TestCase.assertTrue(JSON.parseObject("{\"id\":true\t}\n\t", FieldDeserializerTest9.VO.class).id);
        TestCase.assertTrue(JSON.parseObject("{\"id\":true }", FieldDeserializerTest9.V1.class).id);
        TestCase.assertTrue(JSON.parseObject("{\"id\":true }\n\t", FieldDeserializerTest9.V1.class).id);
        TestCase.assertTrue(JSON.parseObject("{\"id\":true\n}", FieldDeserializerTest9.V1.class).id);
    }

    public void test_1() throws Exception {
        TestCase.assertFalse(JSON.parseObject("{\"id\":false\t}", FieldDeserializerTest9.VO.class).id);
        TestCase.assertFalse(JSON.parseObject("{\"id\":false\t}\n\t", FieldDeserializerTest9.VO.class).id);
        TestCase.assertFalse(JSON.parseObject("{\"id\":false }", FieldDeserializerTest9.V1.class).id);
        TestCase.assertFalse(JSON.parseObject("{\"id\":false }\n\t", FieldDeserializerTest9.V1.class).id);
        TestCase.assertFalse(JSON.parseObject("{\"id\":false\n}", FieldDeserializerTest9.V1.class).id);
    }

    public static class VO {
        public boolean id;
    }

    private static class V1 {
        public boolean id;
    }
}

