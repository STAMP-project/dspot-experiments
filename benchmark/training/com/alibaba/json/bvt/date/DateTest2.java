package com.alibaba.json.bvt.date;


import SerializerFeature.UseISO8601DateFormat;
import com.alibaba.fastjson.JSON;
import java.util.Date;
import java.util.TimeZone;
import junit.framework.TestCase;


public class DateTest2 extends TestCase {
    private TimeZone timeZone;

    public void test_date() throws Exception {
        JSON.defaultTimeZone = TimeZone.getTimeZone("America/Chicago");
        Date date = new Date(1531928656055L);
        DateTest2.TestBean bean = new DateTest2.TestBean();
        bean.setDate(date);
        String iso = JSON.toJSONString(bean, UseISO8601DateFormat);
        TestCase.assertEquals("{\"date\":\"2018-07-18T10:44:16.055-05:00\"}", iso);
    }

    public static class TestBean {
        private Date date;

        public Date getDate() {
            return date;
        }

        public void setDate(Date date) {
            this.date = date;
        }
    }
}

