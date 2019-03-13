package com.alibaba.json.bvt.serializer.date;


import JSON.defaultLocale;
import JSON.defaultTimeZone;
import SerializerFeature.UseISO8601DateFormat;
import com.alibaba.fastjson.JSON;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;
import junit.framework.TestCase;
import org.junit.Assert;


public class DateTest_ISO8601_TimeZone extends TestCase {
    public void test_date1() throws Exception {
        Map<String, Date> map = new HashMap<String, Date>();
        map.put("date", new Date(1425886057586L));
        String json = JSON.toJSONString(map, UseISO8601DateFormat);
        Assert.assertEquals("{\"date\":\"2015-03-09T15:27:37.586+08:00\"}", json);
        Map<String, Date> newMap = JSON.parseObject(json, new com.alibaba.fastjson.TypeReference<Map<String, Date>>() {});
        Assert.assertEquals(1425886057586L, newMap.get("date").getTime());
    }

    public void test_date2() throws Exception {
        Calendar c = Calendar.getInstance(defaultTimeZone, defaultLocale);
        c.setTimeZone(TimeZone.getTimeZone("GMT+10"));
        DateTest_ISO8601_TimeZone.VO v = new DateTest_ISO8601_TimeZone.VO();
        v.setGmtCreate(c);
        String json = JSON.toJSONString(v, UseISO8601DateFormat);
        System.out.println(json);
        Calendar cal = JSON.parseObject(json, DateTest_ISO8601_TimeZone.VO.class).getGmtCreate();
        Assert.assertEquals(10, ((cal.getTimeZone().getRawOffset()) / (3600 * 1000)));
    }

    public void test_date3() throws Exception {
        Calendar c = Calendar.getInstance(defaultTimeZone, defaultLocale);
        DateTest_ISO8601_TimeZone.VO v = new DateTest_ISO8601_TimeZone.VO();
        v.setGmtCreate(c);
        String json = JSON.toJSONString(v, UseISO8601DateFormat);
        System.out.println(json);
        Calendar cal = JSON.parseObject(json, DateTest_ISO8601_TimeZone.VO.class).getGmtCreate();
        Assert.assertEquals(8, ((cal.getTimeZone().getRawOffset()) / (3600 * 1000)));
    }

    public static class VO {
        private Calendar gmtCreate;

        public Calendar getGmtCreate() {
            return gmtCreate;
        }

        public void setGmtCreate(Calendar gmtCreate) {
            this.gmtCreate = gmtCreate;
        }
    }
}

