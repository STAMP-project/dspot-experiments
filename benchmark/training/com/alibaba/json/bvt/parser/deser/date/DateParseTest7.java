package com.alibaba.json.bvt.parser.deser.date;


import JSON.defaultLocale;
import JSON.defaultTimeZone;
import com.alibaba.fastjson.JSON;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import junit.framework.TestCase;
import org.junit.Assert;


public class DateParseTest7 extends TestCase {
    public void test_date() throws Exception {
        System.out.println(System.currentTimeMillis());
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", JSON.defaultLocale);
        dateFormat.setTimeZone(defaultTimeZone);
        System.out.println(dateFormat.parse("1970-01-01 20:00:01").getTime());
        System.out.println(new Date().toString());
        // 1369273142603
        String text = "\"19790714130723\"";
        Date date = JSON.parseObject(text, Date.class);
        Calendar calendar = Calendar.getInstance(defaultTimeZone, defaultLocale);
        calendar.setTime(date);
        Assert.assertEquals(1979, calendar.get(Calendar.YEAR));
        Assert.assertEquals(6, calendar.get(Calendar.MONTH));
        Assert.assertEquals(14, calendar.get(Calendar.DAY_OF_MONTH));
        Assert.assertEquals(13, calendar.get(Calendar.HOUR_OF_DAY));
        Assert.assertEquals(7, calendar.get(Calendar.MINUTE));
        Assert.assertEquals(23, calendar.get(Calendar.SECOND));
        Assert.assertEquals(0, calendar.get(Calendar.MILLISECOND));
    }
}

