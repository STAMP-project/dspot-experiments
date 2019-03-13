package com.alibaba.json.bvt.parser.deser.date;


import JSON.defaultLocale;
import JSON.defaultTimeZone;
import com.alibaba.fastjson.JSON;
import java.util.Calendar;
import java.util.Date;
import junit.framework.TestCase;
import org.junit.Assert;


public class DateParseTest1 extends TestCase {
    public void test_date() throws Exception {
        String text = "\"1979-07-14\"";
        Date date = JSON.parseObject(text, Date.class);
        Calendar calendar = Calendar.getInstance(defaultTimeZone, defaultLocale);
        calendar.setTime(date);
        Assert.assertEquals(1979, calendar.get(Calendar.YEAR));
        Assert.assertEquals(6, calendar.get(Calendar.MONTH));
        Assert.assertEquals(14, calendar.get(Calendar.DAY_OF_MONTH));
        Assert.assertEquals(0, calendar.get(Calendar.HOUR_OF_DAY));
        Assert.assertEquals(0, calendar.get(Calendar.MINUTE));
        Assert.assertEquals(0, calendar.get(Calendar.SECOND));
        Assert.assertEquals(0, calendar.get(Calendar.MILLISECOND));
    }
}

