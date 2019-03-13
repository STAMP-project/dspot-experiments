package com.alibaba.json.bvt.fullSer;


import SerializerFeature.WriteClassName;
import com.alibaba.fastjson.JSON;
import junit.framework.TestCase;
import org.junit.Assert;


public class LongTest extends TestCase {
    public void test_0() throws Exception {
        LongTest.VO vo = new LongTest.VO();
        vo.setValue(33L);
        String text = JSON.toJSONString(vo, WriteClassName);
        System.out.println(text);
        Assert.assertEquals("{\"@type\":\"com.alibaba.json.bvt.fullSer.LongTest$VO\",\"value\":33}", text);
    }

    public static class VO {
        private Long value;

        public Long getValue() {
            return value;
        }

        public void setValue(Long value) {
            this.value = value;
        }
    }
}

