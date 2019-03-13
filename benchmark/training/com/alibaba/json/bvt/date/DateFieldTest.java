package com.alibaba.json.bvt.date;


import SerializerFeature.WriteMapNullValue;
import SerializerFeature.WriteNullNumberAsZero;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializeConfig;
import java.util.Date;
import junit.framework.TestCase;
import org.junit.Assert;


public class DateFieldTest extends TestCase {
    public void test_codec() throws Exception {
        DateFieldTest.V0 v = new DateFieldTest.V0();
        v.setValue(new Date());
        String text = JSON.toJSONString(v);
        Assert.assertEquals((("{\"value\":" + (v.getValue().getTime())) + "}"), text);
        DateFieldTest.V0 v1 = JSON.parseObject(text, DateFieldTest.V0.class);
        Assert.assertEquals(v1.getValue(), v.getValue());
    }

    public void test_codec_null() throws Exception {
        DateFieldTest.V0 v = new DateFieldTest.V0();
        SerializeConfig mapping = new SerializeConfig();
        mapping.setAsmEnable(false);
        String text = JSON.toJSONString(v, mapping, WriteMapNullValue);
        Assert.assertEquals("{\"value\":null}", text);
        DateFieldTest.V0 v1 = JSON.parseObject(text, DateFieldTest.V0.class);
        Assert.assertEquals(v1.getValue(), v.getValue());
    }

    public void test_codec_null_asm() throws Exception {
        DateFieldTest.V0 v = new DateFieldTest.V0();
        SerializeConfig mapping = new SerializeConfig();
        mapping.setAsmEnable(true);
        String text = JSON.toJSONString(v, mapping, WriteMapNullValue);
        Assert.assertEquals("{\"value\":null}", text);
        DateFieldTest.V0 v1 = JSON.parseObject(text, DateFieldTest.V0.class);
        Assert.assertEquals(v1.getValue(), v.getValue());
    }

    public void test_codec_null_1() throws Exception {
        DateFieldTest.V0 v = new DateFieldTest.V0();
        SerializeConfig mapping = new SerializeConfig();
        mapping.setAsmEnable(false);
        String text = JSON.toJSONString(v, mapping, WriteMapNullValue, WriteNullNumberAsZero);
        Assert.assertEquals("{\"value\":null}", text);
        DateFieldTest.V0 v1 = JSON.parseObject(text, DateFieldTest.V0.class);
        Assert.assertEquals(null, v1.getValue());
    }

    public static class V0 {
        private Date value;

        public Date getValue() {
            return value;
        }

        public void setValue(Date value) {
            this.value = value;
        }
    }
}

