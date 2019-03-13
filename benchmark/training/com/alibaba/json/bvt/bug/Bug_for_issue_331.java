package com.alibaba.json.bvt.bug;


import JSON.defaultLocale;
import JSON.defaultTimeZone;
import SerializerFeature.UseISO8601DateFormat;
import com.alibaba.fastjson.JSON;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import junit.framework.TestCase;
import org.junit.Assert;


public class Bug_for_issue_331 extends TestCase {
    public void test_for_issue() throws Exception {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", JSON.defaultLocale);
        format.setTimeZone(defaultTimeZone);
        Date date = format.parse("2015-05-23");
        Calendar c = Calendar.getInstance(defaultTimeZone, defaultLocale);
        c.setTime(date);
        Bug_for_issue_331.Model original = new Bug_for_issue_331.Model();
        original.setDate(date);
        original.setCalendar(c);
        String json = JSON.toJSONString(original, UseISO8601DateFormat);
        System.out.println(json);// V1.2.4 ??{"calendar":"2015-05-23","date":"2015-05-23"} , V1.2.6 ??{"calendar":"2015-05-23+08:00","date":"2015-05-23+08:00"}

        Bug_for_issue_331.Model actual = JSON.parseObject(json, Bug_for_issue_331.Model.class);
        Assert.assertNotNull(actual);
        Assert.assertNotNull(actual.getDate());
        Assert.assertNotNull(actual.getCalendar());
        Assert.assertEquals("??????????", original.getDate(), actual.getDate());
        Assert.assertEquals("?????Date ? Calendar ???", actual.getDate(), actual.getCalendar().getTime());
    }

    public static class Model {
        private Date date;

        private Calendar calendar;

        public Date getDate() {
            return date;
        }

        public void setDate(Date date) {
            this.date = date;
        }

        public Calendar getCalendar() {
            return calendar;
        }

        public void setCalendar(Calendar calendar) {
            this.calendar = calendar;
        }
    }
}

