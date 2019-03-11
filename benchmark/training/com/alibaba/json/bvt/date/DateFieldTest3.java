package com.alibaba.json.bvt.date;


import JSON.defaultTimeZone;
import SerializerFeature.WriteMapNullValue;
import SerializerFeature.WriteNullNumberAsZero;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializeConfig;
import com.alibaba.fastjson.serializer.SimpleDateFormatSerializer;
import java.text.SimpleDateFormat;
import java.util.Date;
import junit.framework.TestCase;
import org.junit.Assert;


public class DateFieldTest3 extends TestCase {
    public void test_codec() throws Exception {
        SerializeConfig mapping = new SerializeConfig();
        mapping.put(Date.class, new SimpleDateFormatSerializer("yyyy-MM-dd"));
        DateFieldTest3.V0 v = new DateFieldTest3.V0();
        v.setValue(new Date());
        String text = JSON.toJSONString(v, mapping);
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", JSON.defaultLocale);
        format.setTimeZone(defaultTimeZone);
        Assert.assertEquals((("{\"value\":" + (JSON.toJSONString(format.format(v.getValue())))) + "}"), text);
    }

    public void test_codec_no_asm() throws Exception {
        DateFieldTest3.V0 v = new DateFieldTest3.V0();
        v.setValue(new Date());
        SerializeConfig mapping = new SerializeConfig();
        mapping.put(Date.class, new SimpleDateFormatSerializer("yyyy-MM-dd"));
        mapping.setAsmEnable(false);
        String text = JSON.toJSONString(v, mapping, WriteMapNullValue);
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", JSON.defaultLocale);
        format.setTimeZone(defaultTimeZone);
        Assert.assertEquals((("{\"value\":" + (JSON.toJSONString(format.format(v.getValue())))) + "}"), text);
    }

    public void test_codec_asm() throws Exception {
        DateFieldTest3.V0 v = new DateFieldTest3.V0();
        v.setValue(new Date());
        SerializeConfig mapping = new SerializeConfig();
        mapping.put(Date.class, new SimpleDateFormatSerializer("yyyy-MM-dd"));
        mapping.setAsmEnable(true);
        String text = JSON.toJSONString(v, mapping, WriteMapNullValue);
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", JSON.defaultLocale);
        format.setTimeZone(defaultTimeZone);
        Assert.assertEquals((("{\"value\":" + (JSON.toJSONString(format.format(v.getValue())))) + "}"), text);
    }

    public void test_codec_null_asm() throws Exception {
        DateFieldTest3.V0 v = new DateFieldTest3.V0();
        SerializeConfig mapping = new SerializeConfig();
        mapping.setAsmEnable(true);
        String text = JSON.toJSONString(v, mapping, WriteMapNullValue);
        mapping.put(Date.class, new SimpleDateFormatSerializer("yyyy-MM-dd"));
        Assert.assertEquals("{\"value\":null}", text);
        DateFieldTest3.V0 v1 = JSON.parseObject(text, DateFieldTest3.V0.class);
        Assert.assertEquals(v1.getValue(), v.getValue());
    }

    public void test_codec_null_no_asm() throws Exception {
        DateFieldTest3.V0 v = new DateFieldTest3.V0();
        SerializeConfig mapping = new SerializeConfig();
        mapping.put(Date.class, new SimpleDateFormatSerializer("yyyy-MM-dd"));
        mapping.setAsmEnable(false);
        String text = JSON.toJSONString(v, mapping, WriteMapNullValue);
        Assert.assertEquals("{\"value\":null}", text);
        DateFieldTest3.V0 v1 = JSON.parseObject(text, DateFieldTest3.V0.class);
        Assert.assertEquals(v1.getValue(), v.getValue());
    }

    public void test_codec_null_1() throws Exception {
        DateFieldTest3.V0 v = new DateFieldTest3.V0();
        SerializeConfig mapping = new SerializeConfig();
        mapping.setAsmEnable(false);
        String text = JSON.toJSONString(v, mapping, WriteMapNullValue, WriteNullNumberAsZero);
        Assert.assertEquals("{\"value\":null}", text);
        DateFieldTest3.V0 v1 = JSON.parseObject(text, DateFieldTest3.V0.class);
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

