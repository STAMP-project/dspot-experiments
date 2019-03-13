package com.alibaba.json.bvt.parser.deser;


import JSON.defaultTimeZone;
import com.alibaba.fastjson.JSON;
import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.Date;
import junit.framework.TestCase;
import org.junit.Assert;


public class TimeDeserializerTest extends TestCase {
    public void test_time() throws Exception {
        long millis = System.currentTimeMillis();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", JSON.defaultLocale);
        format.setTimeZone(defaultTimeZone);
        String text = format.format(new Date(millis));
        text += "T";
        SimpleDateFormat format2 = new SimpleDateFormat("HH:mm:ss.SSS", JSON.defaultLocale);
        format2.setTimeZone(defaultTimeZone);
        text += format2.format(new Date(millis));
        Assert.assertNull(JSON.parseObject("null", Time.class));
        Assert.assertNull(JSON.parseObject("\"\"", Time.class));
        Assert.assertNull(JSON.parseArray("null", Time.class));
        Assert.assertNull(JSON.parseArray("[null]", Time.class).get(0));
        Assert.assertNull(JSON.parseObject("{\"value\":null}", TimeDeserializerTest.VO.class).getValue());
        Assert.assertEquals(new Time(millis), JSON.parseObject(("" + millis), Time.class));
        Assert.assertEquals(new Time(millis), JSON.parseObject((("{\"@type\":\"java.sql.Time\",\"val\":" + millis) + "}"), Time.class));
        Assert.assertEquals(new Time(millis), JSON.parseObject((("\"" + millis) + "\""), Time.class));
        Assert.assertEquals(new Time(millis), JSON.parseObject((("\"" + text) + "\""), Time.class));
        // System.out.println(JSON.toJSONString(new Time(millis), SerializerFeature.WriteClassName));
    }

    public static class VO {
        private Time value;

        public Time getValue() {
            return value;
        }

        public void setValue(Time value) {
            this.value = value;
        }
    }
}

