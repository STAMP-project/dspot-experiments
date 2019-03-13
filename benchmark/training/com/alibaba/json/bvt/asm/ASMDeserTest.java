package com.alibaba.json.bvt.asm;


import com.alibaba.fastjson.JSON;
import java.util.ArrayList;
import junit.framework.TestCase;
import org.junit.Assert;


public class ASMDeserTest extends TestCase {
    public void test_codec() throws Exception {
        String text = JSON.toJSONString(new ASMDeserTest.Entity());
        Assert.assertEquals("[]", text);
        ASMDeserTest.Entity object = JSON.parseObject(text, ASMDeserTest.Entity.class);
        Assert.assertEquals(0, object.size());
    }

    public void test_codec_1() throws Exception {
        String text = JSON.toJSONString(new ASMDeserTest.VO());
        Assert.assertEquals("{\"value\":[]}", text);
        ASMDeserTest.VO object = JSON.parseObject(text, ASMDeserTest.VO.class);
        Assert.assertEquals(0, object.getValue().size());
    }

    public void test_ArrayList() throws Exception {
        ArrayList object = JSON.parseObject("[]", ArrayList.class);
        Assert.assertEquals(0, object.size());
    }

    public void test_error() throws Exception {
        Exception error = null;
        try {
            JSON.parseObject("[]", ASMDeserTest.EntityError.class);
        } catch (Exception ex) {
            error = ex;
        }
        Assert.assertNotNull(error);
    }

    public static class VO {
        private ASMDeserTest.Entity value = new ASMDeserTest.Entity();

        public ASMDeserTest.Entity getValue() {
            return value;
        }

        public void setValue(ASMDeserTest.Entity value) {
            this.value = value;
        }
    }

    public static class Entity extends ArrayList<String> {}

    public static class EntityError extends ArrayList<String> {
        public EntityError() {
            throw new RuntimeException();
        }
    }
}

