package com.alibaba.json.bvt.bug;


import Feature.AllowISO8601DateFormat;
import JSON.defaultLocale;
import JSON.defaultTimeZone;
import SerializerFeature.UseISO8601DateFormat;
import SerializerFeature.WriteDateUseDateFormat;
import com.alibaba.fastjson.JSON;
import java.util.Calendar;
import java.util.Date;
import junit.framework.TestCase;
import org.junit.Assert;


public class Bug_for_issue_447 extends TestCase {
    public void test_for_issue() throws Exception {
        Calendar calendar = Calendar.getInstance(defaultTimeZone, defaultLocale);
        calendar.setTimeInMillis(1460563200000L);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        Bug_for_issue_447.Foo foo = new Bug_for_issue_447.Foo();
        foo.setCreateDate(calendar.getTime());
        String date = JSON.toJSONString(foo, UseISO8601DateFormat);
        Assert.assertEquals("{\"createDate\":\"2016-04-14+08:00\"}", date);
        Bug_for_issue_447.Foo foo2 = JSON.parseObject(date, Bug_for_issue_447.Foo.class, AllowISO8601DateFormat);
        Assert.assertEquals("{\"createDate\":\"2016-04-14 00:00:00\"}", JSON.toJSONString(foo2, WriteDateUseDateFormat));
    }

    public static class Foo {
        private String name;

        private Date createDate;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Date getCreateDate() {
            return createDate;
        }

        public void setCreateDate(Date createDate) {
            this.createDate = createDate;
        }
    }
}

