package com.alibaba.json.bvt.serializer;


import SerializerFeature.WriteMapNullValue;
import com.alibaba.fastjson.JSON;
import junit.framework.TestCase;
import org.junit.Assert;


public class IntegerSerializerTest extends TestCase {
    public void test_null() throws Exception {
        IntegerSerializerTest.VO vo = new IntegerSerializerTest.VO();
        Assert.assertEquals("{\"value\":null}", JSON.toJSONString(vo, WriteMapNullValue));
    }

    private static class VO {
        private Integer value;

        public Integer getValue() {
            return value;
        }

        public void setValue(Integer value) {
            this.value = value;
        }
    }
}

