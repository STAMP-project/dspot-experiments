package com.alibaba.json.bvt.serializer;


import SerializerFeature.WriteMapNullValue;
import com.alibaba.fastjson.JSON;
import junit.framework.TestCase;
import org.junit.Assert;


public class ShortSerializerTest extends TestCase {
    public void test_null() throws Exception {
        ShortSerializerTest.VO vo = new ShortSerializerTest.VO();
        Assert.assertEquals("{\"value\":null}", JSON.toJSONString(vo, WriteMapNullValue));
    }

    private static class VO {
        private Short value;

        public Short getValue() {
            return value;
        }

        public void setValue(Short value) {
            this.value = value;
        }
    }
}

