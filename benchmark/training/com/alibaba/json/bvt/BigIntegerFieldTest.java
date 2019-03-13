package com.alibaba.json.bvt;


import SerializerFeature.WriteMapNullValue;
import SerializerFeature.WriteNullNumberAsZero;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializeConfig;
import java.math.BigInteger;
import junit.framework.TestCase;
import org.junit.Assert;


public class BigIntegerFieldTest extends TestCase {
    public void test_codec_null() throws Exception {
        BigIntegerFieldTest.V0 v = new BigIntegerFieldTest.V0();
        SerializeConfig mapping = new SerializeConfig();
        mapping.setAsmEnable(false);
        String text = JSON.toJSONString(v, mapping, WriteMapNullValue);
        Assert.assertEquals("{\"value\":null}", text);
        BigIntegerFieldTest.V0 v1 = JSON.parseObject(text, BigIntegerFieldTest.V0.class);
        Assert.assertEquals(v1.getValue(), v.getValue());
    }

    public void test_codec_null_1() throws Exception {
        BigIntegerFieldTest.V0 v = new BigIntegerFieldTest.V0();
        SerializeConfig mapping = new SerializeConfig();
        mapping.setAsmEnable(false);
        String text = JSON.toJSONString(v, mapping, WriteMapNullValue, WriteNullNumberAsZero);
        Assert.assertEquals("{\"value\":0}", text);
    }

    public static class V0 {
        private BigInteger value;

        public BigInteger getValue() {
            return value;
        }

        public void setValue(BigInteger value) {
            this.value = value;
        }
    }
}

