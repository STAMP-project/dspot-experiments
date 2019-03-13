package com.alibaba.json.bvt.date;


import JSON.defaultLocale;
import JSON.defaultTimeZone;
import SerializerFeature.UseISO8601DateFormat;
import com.alibaba.fastjson.JSON;
import java.util.Calendar;
import junit.framework.TestCase;
import org.junit.Assert;


public class CalendarTest extends TestCase {
    public void test_null() throws Exception {
        String text = "{\"calendar\":null}";
        CalendarTest.VO vo = JSON.parseObject(text, CalendarTest.VO.class);
        Assert.assertNull(vo.getCalendar());
    }

    public void test_codec() throws Exception {
        Calendar calendar = Calendar.getInstance(defaultTimeZone, defaultLocale);
        CalendarTest.VO vo = new CalendarTest.VO();
        vo.setCalendar(calendar);
        String text = JSON.toJSONString(vo);
        CalendarTest.VO vo2 = JSON.parseObject(text, CalendarTest.VO.class);
        Assert.assertEquals(vo.getCalendar().getTimeInMillis(), vo2.getCalendar().getTimeInMillis());
    }

    public void test_codec_iso88591() throws Exception {
        Calendar calendar = Calendar.getInstance(defaultTimeZone, defaultLocale);
        CalendarTest.VO vo = new CalendarTest.VO();
        vo.setCalendar(calendar);
        String text = JSON.toJSONString(vo, UseISO8601DateFormat);
        CalendarTest.VO vo2 = JSON.parseObject(text, CalendarTest.VO.class);
        Assert.assertEquals(vo.getCalendar().getTimeInMillis(), vo2.getCalendar().getTimeInMillis());
    }

    public void test_codec_iso88591_2() throws Exception {
        Calendar calendar = Calendar.getInstance(defaultTimeZone, defaultLocale);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        CalendarTest.VO vo = new CalendarTest.VO();
        vo.setCalendar(calendar);
        String text = JSON.toJSONString(vo, UseISO8601DateFormat);
        CalendarTest.VO vo2 = JSON.parseObject(text, CalendarTest.VO.class);
        Assert.assertEquals(vo.getCalendar().getTimeInMillis(), vo2.getCalendar().getTimeInMillis());
    }

    public static class VO {
        private Calendar calendar;

        public Calendar getCalendar() {
            return calendar;
        }

        public void setCalendar(Calendar calendar) {
            this.calendar = calendar;
        }
    }
}

