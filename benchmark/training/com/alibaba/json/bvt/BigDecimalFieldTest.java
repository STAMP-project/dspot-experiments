package com.alibaba.json.bvt;


import SerializerFeature.WriteMapNullValue;
import SerializerFeature.WriteNullNumberAsZero;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializeConfig;
import java.math.BigDecimal;
import junit.framework.TestCase;
import org.junit.Assert;


public class BigDecimalFieldTest extends TestCase {
    public void test_codec_null() throws Exception {
        BigDecimalFieldTest.V0 v = new BigDecimalFieldTest.V0();
        SerializeConfig mapping = new SerializeConfig();
        mapping.setAsmEnable(false);
        String text = JSON.toJSONString(v, mapping, WriteMapNullValue);
        Assert.assertEquals("{\"value\":null}", text);
        BigDecimalFieldTest.V0 v1 = JSON.parseObject(text, BigDecimalFieldTest.V0.class);
        Assert.assertEquals(v1.getValue(), v.getValue());
    }

    public void test_codec_null_1() throws Exception {
        BigDecimalFieldTest.V0 v = new BigDecimalFieldTest.V0();
        SerializeConfig mapping = new SerializeConfig();
        mapping.setAsmEnable(false);
        String text = JSON.toJSONString(v, mapping, WriteMapNullValue, WriteNullNumberAsZero);
        Assert.assertEquals("{\"value\":0}", text);
    }

    public static class V0 {
        private BigDecimal value;

        public BigDecimal getValue() {
            return value;
        }

        public void setValue(BigDecimal value) {
            this.value = value;
        }
    }
}

