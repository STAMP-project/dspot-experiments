package com.alibaba.json.bvt.asm;


import com.alibaba.fastjson.JSON;
import java.util.ArrayList;
import junit.framework.TestCase;
import org.junit.Assert;


public class ASMDeserTest2 extends TestCase {
    public void test_codec_1() throws Exception {
        String text = JSON.toJSONString(new ASMDeserTest2.VO());
        Assert.assertEquals("{\"value\":[]}", text);
        ASMDeserTest2.VO object = JSON.parseObject(text, ASMDeserTest2.VO.class);
        Assert.assertEquals(0, object.getValue().size());
    }

    public static class VO {
        private ASMDeserTest2.Entity value = new ASMDeserTest2.Entity();

        public ASMDeserTest2.Entity getValue() {
            return value;
        }

        public void setValue(ASMDeserTest2.Entity value) {
            this.value = value;
        }
    }

    public static class Entity extends ArrayList<String> {}
}

