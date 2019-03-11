package com.alibaba.json.bvt.date;


import JSON.defaultLocale;
import JSON.defaultTimeZone;
import com.alibaba.fastjson.JSON;
import java.util.Calendar;
import java.util.Date;
import junit.framework.TestCase;
import org.junit.Assert;


public class DateFieldTest9 extends TestCase {
    public void test_tw() throws Exception {
        DateFieldTest9.Entity vo = JSON.parseObject("{\"date\":\"2016/05/06\"}", DateFieldTest9.Entity.class);
        Calendar calendar = Calendar.getInstance(defaultTimeZone, defaultLocale);
        calendar.setTime(vo.date);
        Assert.assertEquals(2016, calendar.get(Calendar.YEAR));
        Assert.assertEquals(4, calendar.get(Calendar.MONTH));
        Assert.assertEquals(6, calendar.get(Calendar.DAY_OF_MONTH));
        Assert.assertEquals(0, calendar.get(Calendar.HOUR_OF_DAY));
        Assert.assertEquals(0, calendar.get(Calendar.MINUTE));
        Assert.assertEquals(0, calendar.get(Calendar.SECOND));
        Assert.assertEquals(0, calendar.get(Calendar.MILLISECOND));
    }

    public void test_cn() throws Exception {
        DateFieldTest9.Entity vo = JSON.parseObject("{\"date\":\"2016-05-06\"}", DateFieldTest9.Entity.class);
        Calendar calendar = Calendar.getInstance(defaultTimeZone, defaultLocale);
        calendar.setTime(vo.date);
        Assert.assertEquals(2016, calendar.get(Calendar.YEAR));
        Assert.assertEquals(4, calendar.get(Calendar.MONTH));
        Assert.assertEquals(6, calendar.get(Calendar.DAY_OF_MONTH));
        Assert.assertEquals(0, calendar.get(Calendar.HOUR_OF_DAY));
        Assert.assertEquals(0, calendar.get(Calendar.MINUTE));
        Assert.assertEquals(0, calendar.get(Calendar.SECOND));
        Assert.assertEquals(0, calendar.get(Calendar.MILLISECOND));
    }

    public void test_cn_1() throws Exception {
        DateFieldTest9.Entity vo = JSON.parseObject("{\"date\":\"2016\u5e745\u67086\u65e5\"}", DateFieldTest9.Entity.class);
        Calendar calendar = Calendar.getInstance(defaultTimeZone, defaultLocale);
        calendar.setTime(vo.date);
        Assert.assertEquals(2016, calendar.get(Calendar.YEAR));
        Assert.assertEquals(4, calendar.get(Calendar.MONTH));
        Assert.assertEquals(6, calendar.get(Calendar.DAY_OF_MONTH));
        Assert.assertEquals(0, calendar.get(Calendar.HOUR_OF_DAY));
        Assert.assertEquals(0, calendar.get(Calendar.MINUTE));
        Assert.assertEquals(0, calendar.get(Calendar.SECOND));
        Assert.assertEquals(0, calendar.get(Calendar.MILLISECOND));
    }

    public void test_cn_2() throws Exception {
        DateFieldTest9.Entity vo = JSON.parseObject("{\"date\":\"2016\u5e745\u670806\u65e5\"}", DateFieldTest9.Entity.class);
        Calendar calendar = Calendar.getInstance(defaultTimeZone, defaultLocale);
        calendar.setTime(vo.date);
        Assert.assertEquals(2016, calendar.get(Calendar.YEAR));
        Assert.assertEquals(4, calendar.get(Calendar.MONTH));
        Assert.assertEquals(6, calendar.get(Calendar.DAY_OF_MONTH));
        Assert.assertEquals(0, calendar.get(Calendar.HOUR_OF_DAY));
        Assert.assertEquals(0, calendar.get(Calendar.MINUTE));
        Assert.assertEquals(0, calendar.get(Calendar.SECOND));
        Assert.assertEquals(0, calendar.get(Calendar.MILLISECOND));
    }

    public void test_cn_3() throws Exception {
        DateFieldTest9.Entity vo = JSON.parseObject("{\"date\":\"2016\u5e7405\u67086\u65e5\"}", DateFieldTest9.Entity.class);
        Calendar calendar = Calendar.getInstance(defaultTimeZone, defaultLocale);
        calendar.setTime(vo.date);
        Assert.assertEquals(2016, calendar.get(Calendar.YEAR));
        Assert.assertEquals(4, calendar.get(Calendar.MONTH));
        Assert.assertEquals(6, calendar.get(Calendar.DAY_OF_MONTH));
        Assert.assertEquals(0, calendar.get(Calendar.HOUR_OF_DAY));
        Assert.assertEquals(0, calendar.get(Calendar.MINUTE));
        Assert.assertEquals(0, calendar.get(Calendar.SECOND));
        Assert.assertEquals(0, calendar.get(Calendar.MILLISECOND));
    }

    public void test_cn_4() throws Exception {
        DateFieldTest9.Entity vo = JSON.parseObject("{\"date\":\"2016\u5e7405\u670806\u65e5\"}", DateFieldTest9.Entity.class);
        Calendar calendar = Calendar.getInstance(defaultTimeZone, defaultLocale);
        calendar.setTime(vo.date);
        Assert.assertEquals(2016, calendar.get(Calendar.YEAR));
        Assert.assertEquals(4, calendar.get(Calendar.MONTH));
        Assert.assertEquals(6, calendar.get(Calendar.DAY_OF_MONTH));
        Assert.assertEquals(0, calendar.get(Calendar.HOUR_OF_DAY));
        Assert.assertEquals(0, calendar.get(Calendar.MINUTE));
        Assert.assertEquals(0, calendar.get(Calendar.SECOND));
        Assert.assertEquals(0, calendar.get(Calendar.MILLISECOND));
    }

    public void test_kr_1() throws Exception {
        DateFieldTest9.Entity vo = JSON.parseObject("{\"date\":\"2016\ub1445\uc6d46\uc77c\"}", DateFieldTest9.Entity.class);
        Calendar calendar = Calendar.getInstance(defaultTimeZone, defaultLocale);
        calendar.setTime(vo.date);
        Assert.assertEquals(2016, calendar.get(Calendar.YEAR));
        Assert.assertEquals(4, calendar.get(Calendar.MONTH));
        Assert.assertEquals(6, calendar.get(Calendar.DAY_OF_MONTH));
        Assert.assertEquals(0, calendar.get(Calendar.HOUR_OF_DAY));
        Assert.assertEquals(0, calendar.get(Calendar.MINUTE));
        Assert.assertEquals(0, calendar.get(Calendar.SECOND));
        Assert.assertEquals(0, calendar.get(Calendar.MILLISECOND));
    }

    public void test_kr_2() throws Exception {
        DateFieldTest9.Entity vo = JSON.parseObject("{\"date\":\"2016\ub1445\uc6d406\uc77c\"}", DateFieldTest9.Entity.class);
        Calendar calendar = Calendar.getInstance(defaultTimeZone, defaultLocale);
        calendar.setTime(vo.date);
        Assert.assertEquals(2016, calendar.get(Calendar.YEAR));
        Assert.assertEquals(4, calendar.get(Calendar.MONTH));
        Assert.assertEquals(6, calendar.get(Calendar.DAY_OF_MONTH));
        Assert.assertEquals(0, calendar.get(Calendar.HOUR_OF_DAY));
        Assert.assertEquals(0, calendar.get(Calendar.MINUTE));
        Assert.assertEquals(0, calendar.get(Calendar.SECOND));
        Assert.assertEquals(0, calendar.get(Calendar.MILLISECOND));
    }

    public void test_kr_3() throws Exception {
        DateFieldTest9.Entity vo = JSON.parseObject("{\"date\":\"2016\ub14405\uc6d46\uc77c\"}", DateFieldTest9.Entity.class);
        Calendar calendar = Calendar.getInstance(defaultTimeZone, defaultLocale);
        calendar.setTime(vo.date);
        Assert.assertEquals(2016, calendar.get(Calendar.YEAR));
        Assert.assertEquals(4, calendar.get(Calendar.MONTH));
        Assert.assertEquals(6, calendar.get(Calendar.DAY_OF_MONTH));
        Assert.assertEquals(0, calendar.get(Calendar.HOUR_OF_DAY));
        Assert.assertEquals(0, calendar.get(Calendar.MINUTE));
        Assert.assertEquals(0, calendar.get(Calendar.SECOND));
        Assert.assertEquals(0, calendar.get(Calendar.MILLISECOND));
    }

    public void test_kr_4() throws Exception {
        DateFieldTest9.Entity vo = JSON.parseObject("{\"date\":\"2016\ub14405\uc6d406\uc77c\"}", DateFieldTest9.Entity.class);
        Calendar calendar = Calendar.getInstance(defaultTimeZone, defaultLocale);
        calendar.setTime(vo.date);
        Assert.assertEquals(2016, calendar.get(Calendar.YEAR));
        Assert.assertEquals(4, calendar.get(Calendar.MONTH));
        Assert.assertEquals(6, calendar.get(Calendar.DAY_OF_MONTH));
        Assert.assertEquals(0, calendar.get(Calendar.HOUR_OF_DAY));
        Assert.assertEquals(0, calendar.get(Calendar.MINUTE));
        Assert.assertEquals(0, calendar.get(Calendar.SECOND));
        Assert.assertEquals(0, calendar.get(Calendar.MILLISECOND));
    }

    public void test_de() throws Exception {
        DateFieldTest9.Entity vo = JSON.parseObject("{\"date\":\"06.05.2016\"}", DateFieldTest9.Entity.class);
        Calendar calendar = Calendar.getInstance(defaultTimeZone, defaultLocale);
        calendar.setTime(vo.date);
        Assert.assertEquals(2016, calendar.get(Calendar.YEAR));
        Assert.assertEquals(4, calendar.get(Calendar.MONTH));
        Assert.assertEquals(6, calendar.get(Calendar.DAY_OF_MONTH));
        Assert.assertEquals(0, calendar.get(Calendar.HOUR_OF_DAY));
        Assert.assertEquals(0, calendar.get(Calendar.MINUTE));
        Assert.assertEquals(0, calendar.get(Calendar.SECOND));
        Assert.assertEquals(0, calendar.get(Calendar.MILLISECOND));
    }

    public void test_in() throws Exception {
        DateFieldTest9.Entity vo = JSON.parseObject("{\"date\":\"06-05-2016\"}", DateFieldTest9.Entity.class);
        Calendar calendar = Calendar.getInstance(defaultTimeZone, defaultLocale);
        calendar.setTime(vo.date);
        Assert.assertEquals(2016, calendar.get(Calendar.YEAR));
        Assert.assertEquals(4, calendar.get(Calendar.MONTH));
        Assert.assertEquals(6, calendar.get(Calendar.DAY_OF_MONTH));
        Assert.assertEquals(0, calendar.get(Calendar.HOUR_OF_DAY));
        Assert.assertEquals(0, calendar.get(Calendar.MINUTE));
        Assert.assertEquals(0, calendar.get(Calendar.SECOND));
        Assert.assertEquals(0, calendar.get(Calendar.MILLISECOND));
    }

    public static class Entity {
        public Date date;
    }
}

