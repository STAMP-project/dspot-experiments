package com.alibaba.json.bvt.parser.deser;


import JSON.defaultTimeZone;
import com.alibaba.fastjson.JSON;
import java.text.SimpleDateFormat;
import java.util.Date;
import junit.framework.TestCase;
import org.junit.Assert;


public class SqlDateDeserializerTest2 extends TestCase {
    public void test_sqlDate() throws Exception {
        Date date = new Date();
        long millis = date.getTime();
        long millis2 = (millis / 1000) * 1000;
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", JSON.defaultLocale);
        dateFormat.setTimeZone(defaultTimeZone);
        String text = dateFormat.format(millis);
        text = text.replace(' ', 'T');
        SimpleDateFormat dateFormat2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", JSON.defaultLocale);
        dateFormat2.setTimeZone(defaultTimeZone);
        String text2 = dateFormat2.format(millis2);
        Assert.assertNull(JSON.parseObject("null", java.sql.Date.class));
        Assert.assertNull(JSON.parseObject("\"\"", java.sql.Date.class));
        Assert.assertNull(JSON.parseArray("null", java.sql.Date.class));
        Assert.assertNull(JSON.parseArray("[null]", java.sql.Date.class).get(0));
        Assert.assertNull(JSON.parseObject("{\"value\":null}", SqlDateDeserializerTest2.VO.class).getValue());
        Assert.assertEquals(new java.sql.Date(millis), JSON.parseObject(("" + millis), java.sql.Date.class));
        Assert.assertEquals(new java.sql.Date(millis), JSON.parseObject((("{\"@type\":\"java.sql.Date\",\"val\":" + millis) + "}"), java.sql.Date.class));
        Assert.assertEquals(new java.sql.Date(millis), JSON.parseObject((("\"" + millis) + "\""), java.sql.Date.class));
        Assert.assertEquals(new java.sql.Date(millis2), JSON.parseObject((("\"" + text2) + "\""), java.sql.Date.class));
        Assert.assertEquals(new java.sql.Date(millis), JSON.parseObject((("\"" + text) + "\""), java.sql.Date.class));
        // System.out.println(JSON.toJSONString(new Time(millis), SerializerFeature.WriteClassName));
    }

    public static class VO {
        private java.sql.Date value;

        public java.sql.Date getValue() {
            return value;
        }

        public void setValue(java.sql.Date value) {
            this.value = value;
        }
    }
}

