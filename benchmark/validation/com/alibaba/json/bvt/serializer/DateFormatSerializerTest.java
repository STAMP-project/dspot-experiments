package com.alibaba.json.bvt.serializer;


import JSON.defaultTimeZone;
import SerializerFeature.WriteClassName;
import SerializerFeature.WriteMapNullValue;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.JSONSerializer;
import com.alibaba.fastjson.serializer.SerializeConfig;
import com.alibaba.fastjson.serializer.SerializeWriter;
import java.text.SimpleDateFormat;
import junit.framework.TestCase;
import org.junit.Assert;


public class DateFormatSerializerTest extends TestCase {
    public void test_date() throws Exception {
        Assert.assertEquals("{\"format\":null}", JSON.toJSONString(new DateFormatSerializerTest.VO(), WriteMapNullValue));
    }

    public void test_date_2() throws Exception {
        SerializeWriter out = new SerializeWriter();
        SerializeConfig config = new SerializeConfig();
        JSONSerializer serializer = new JSONSerializer(out, config);
        serializer.config(WriteMapNullValue, true);
        serializer.write(new DateFormatSerializerTest.VO());
        Assert.assertEquals("{\"format\":null}", out.toString());
    }

    public void test_date_3() throws Exception {
        SerializeWriter out = new SerializeWriter();
        SerializeConfig config = new SerializeConfig();
        JSONSerializer serializer = new JSONSerializer(out, config);
        serializer.config(WriteClassName, true);
        serializer.write(new DateFormatSerializerTest.VO());
        Assert.assertEquals("{\"@type\":\"com.alibaba.json.bvt.serializer.DateFormatSerializerTest$VO\"}", out.toString());
    }

    public void test_date_4() throws Exception {
        SerializeWriter out = new SerializeWriter();
        SerializeConfig config = new SerializeConfig();
        JSONSerializer serializer = new JSONSerializer(out, config);
        SimpleDateFormat format = new SimpleDateFormat("yyyy");
        format.setTimeZone(defaultTimeZone);
        serializer.write(new DateFormatSerializerTest.VO(format));
        Assert.assertEquals("{\"format\":\"yyyy\"}", out.toString());
        JSON.parseObject(out.toString(), DateFormatSerializerTest.VO.class);
    }

    private static class VO {
        private SimpleDateFormat format;

        public VO() {
        }

        public VO(SimpleDateFormat format) {
            this.format = format;
        }

        public SimpleDateFormat getFormat() {
            return format;
        }

        public void setFormat(SimpleDateFormat format) {
            this.format = format;
        }
    }
}

