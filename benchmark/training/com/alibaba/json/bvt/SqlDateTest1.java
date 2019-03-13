package com.alibaba.json.bvt;


import SerializerFeature.UseSingleQuotes;
import SerializerFeature.WriteClassName;
import SerializerFeature.WriteDateUseDateFormat;
import com.alibaba.fastjson.JSON;
import java.sql.Date;
import junit.framework.TestCase;
import org.junit.Assert;


public class SqlDateTest1 extends TestCase {
    public void test_date() throws Exception {
        long millis = 1324138987429L;
        Date date = new Date(millis);
        Assert.assertEquals("1324138987429", JSON.toJSONString(date));
        Assert.assertEquals("{\"@type\":\"java.sql.Date\",\"val\":1324138987429}", JSON.toJSONString(date, WriteClassName));
        Assert.assertEquals(1324138987429L, ((java.util.Date) (JSON.parse("{\"@type\":\"java.util.Date\",\"val\":1324138987429}"))).getTime());
        Assert.assertEquals("\"2011-12-18 00:23:07\"", JSON.toJSONString(date, WriteDateUseDateFormat));
        Assert.assertEquals("\"2011-12-18 00:23:07.429\"", JSON.toJSONStringWithDateFormat(date, "yyyy-MM-dd HH:mm:ss.SSS"));
        Assert.assertEquals("'2011-12-18 00:23:07.429'", JSON.toJSONStringWithDateFormat(date, "yyyy-MM-dd HH:mm:ss.SSS", UseSingleQuotes));
    }
}

