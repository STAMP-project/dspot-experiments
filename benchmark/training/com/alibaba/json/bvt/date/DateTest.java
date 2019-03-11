package com.alibaba.json.bvt.date;


import SerializerFeature.UseSingleQuotes;
import SerializerFeature.WriteClassName;
import SerializerFeature.WriteDateUseDateFormat;
import com.alibaba.fastjson.JSON;
import java.util.Date;
import junit.framework.TestCase;
import org.junit.Assert;


public class DateTest extends TestCase {
    public void test_date() throws Exception {
        long millis = 1324138987429L;
        Date date = new Date(millis);
        Assert.assertEquals("1324138987429", JSON.toJSONString(date));
        Assert.assertEquals("new Date(1324138987429)", JSON.toJSONString(date, WriteClassName));
        Assert.assertEquals("\"2011-12-18 00:23:07\"", JSON.toJSONString(date, WriteDateUseDateFormat));
        Assert.assertEquals("\"2011-12-18 00:23:07.429\"", JSON.toJSONStringWithDateFormat(date, "yyyy-MM-dd HH:mm:ss.SSS"));
        Assert.assertEquals("'2011-12-18 00:23:07.429'", JSON.toJSONStringWithDateFormat(date, "yyyy-MM-dd HH:mm:ss.SSS", UseSingleQuotes));
    }

    public void test_parse() throws Exception {
        Date date = JSON.parseObject("\"2018-10-12 09:48:22 +0800\"", Date.class);
        TestCase.assertEquals(1539308902000L, date.getTime());
    }
}

