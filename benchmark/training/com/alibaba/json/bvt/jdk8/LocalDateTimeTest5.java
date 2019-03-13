package com.alibaba.json.bvt.jdk8;


import com.alibaba.fastjson.JSON;
import java.time.LocalDateTime;
import java.util.Locale;
import junit.framework.TestCase;
import org.junit.Assert;


public class LocalDateTimeTest5 extends TestCase {
    private Locale origin;

    public void test_for_long() throws Exception {
        LocalDateTimeTest5.VO vo = JSON.parseObject("{\"date\":1322874196000}", LocalDateTimeTest5.VO.class);
        Assert.assertEquals(2011, vo.date.getYear());
        Assert.assertEquals(12, vo.date.getMonthValue());
        Assert.assertEquals(3, vo.date.getDayOfMonth());
        // Assert.assertEquals(9, vo.date.getHour());
        Assert.assertEquals(3, vo.date.getMinute());
        Assert.assertEquals(16, vo.date.getSecond());
        Assert.assertEquals(0, vo.date.getNano());
    }

    public void test_for_normal() throws Exception {
        LocalDateTimeTest5.VO vo = JSON.parseObject("{\"date\":\"2011-12-03 09:03:16\"}", LocalDateTimeTest5.VO.class);
        Assert.assertEquals(2011, vo.date.getYear());
        Assert.assertEquals(12, vo.date.getMonthValue());
        Assert.assertEquals(3, vo.date.getDayOfMonth());
        Assert.assertEquals(9, vo.date.getHour());
        Assert.assertEquals(3, vo.date.getMinute());
        Assert.assertEquals(16, vo.date.getSecond());
        Assert.assertEquals(0, vo.date.getNano());
    }

    public void test_for_iso() throws Exception {
        LocalDateTimeTest5.VO vo = JSON.parseObject("{\"date\":\"2011-12-03T09:03:16\"}", LocalDateTimeTest5.VO.class);
        Assert.assertEquals(2011, vo.date.getYear());
        Assert.assertEquals(12, vo.date.getMonthValue());
        Assert.assertEquals(3, vo.date.getDayOfMonth());
        Assert.assertEquals(9, vo.date.getHour());
        Assert.assertEquals(3, vo.date.getMinute());
        Assert.assertEquals(16, vo.date.getSecond());
        Assert.assertEquals(0, vo.date.getNano());
    }

    public void test_for_tw() throws Exception {
        LocalDateTimeTest5.VO vo = JSON.parseObject("{\"date\":\"2016/05/06 09:03:16\"}", LocalDateTimeTest5.VO.class);
        Assert.assertEquals(2016, vo.date.getYear());
        Assert.assertEquals(5, vo.date.getMonthValue());
        Assert.assertEquals(6, vo.date.getDayOfMonth());
    }

    public void test_for_jp() throws Exception {
        LocalDateTimeTest5.VO vo = JSON.parseObject("{\"date\":\"2016\u5e745\u67086\u65e5 09:03:16\"}", LocalDateTimeTest5.VO.class);
        Assert.assertEquals(2016, vo.date.getYear());
        Assert.assertEquals(5, vo.date.getMonthValue());
        Assert.assertEquals(6, vo.date.getDayOfMonth());
    }

    public void test_for_cn() throws Exception {
        LocalDateTimeTest5.VO vo = JSON.parseObject("{\"date\":\"2016\u5e745\u67086\u65e5 9\u65f63\u520616\u79d2\"}", LocalDateTimeTest5.VO.class);
        Assert.assertEquals(2016, vo.date.getYear());
        Assert.assertEquals(5, vo.date.getMonthValue());
        Assert.assertEquals(6, vo.date.getDayOfMonth());
    }

    public void test_for_kr() throws Exception {
        LocalDateTimeTest5.VO vo = JSON.parseObject("{\"date\":\"2016\ub1445\uc6d46\uc77c 09:03:16\"}", LocalDateTimeTest5.VO.class);
        Assert.assertEquals(2016, vo.date.getYear());
        Assert.assertEquals(5, vo.date.getMonthValue());
        Assert.assertEquals(6, vo.date.getDayOfMonth());
    }

    public void test_for_us() throws Exception {
        LocalDateTimeTest5.VO vo = JSON.parseObject("{\"date\":\"05/26/2016 09:03:16\"}", LocalDateTimeTest5.VO.class);
        Assert.assertEquals(2016, vo.date.getYear());
        Assert.assertEquals(5, vo.date.getMonthValue());
        Assert.assertEquals(26, vo.date.getDayOfMonth());
    }

    public void test_for_eur() throws Exception {
        LocalDateTimeTest5.VO vo = JSON.parseObject("{\"date\":\"26/05/2016 09:03:16\"}", LocalDateTimeTest5.VO.class);
        Assert.assertEquals(2016, vo.date.getYear());
        Assert.assertEquals(5, vo.date.getMonthValue());
        Assert.assertEquals(26, vo.date.getDayOfMonth());
    }

    public void test_for_us_1() throws Exception {
        Locale.setDefault(Locale.US);
        LocalDateTimeTest5.VO vo = JSON.parseObject("{\"date\":\"05/06/2016 09:03:16\"}", LocalDateTimeTest5.VO.class);
        Assert.assertEquals(2016, vo.date.getYear());
        Assert.assertEquals(5, vo.date.getMonthValue());
        Assert.assertEquals(6, vo.date.getDayOfMonth());
    }

    public void test_for_br() throws Exception {
        Locale.setDefault(new Locale("pt", "BR"));
        LocalDateTimeTest5.VO vo = JSON.parseObject("{\"date\":\"06/05/2016 09:03:16\"}", LocalDateTimeTest5.VO.class);
        Assert.assertEquals(2016, vo.date.getYear());
        Assert.assertEquals(5, vo.date.getMonthValue());
        Assert.assertEquals(6, vo.date.getDayOfMonth());
    }

    public void test_for_au() throws Exception {
        Locale.setDefault(new Locale("en", "AU"));
        LocalDateTimeTest5.VO vo = JSON.parseObject("{\"date\":\"06/05/2016 09:03:16\"}", LocalDateTimeTest5.VO.class);
        Assert.assertEquals(2016, vo.date.getYear());
        Assert.assertEquals(5, vo.date.getMonthValue());
        Assert.assertEquals(6, vo.date.getDayOfMonth());
    }

    public void test_for_de() throws Exception {
        Locale.setDefault(new Locale("pt", "BR"));
        LocalDateTimeTest5.VO vo = JSON.parseObject("{\"date\":\"06.05.2016 09:03:16\"}", LocalDateTimeTest5.VO.class);
        Assert.assertEquals(2016, vo.date.getYear());
        Assert.assertEquals(5, vo.date.getMonthValue());
        Assert.assertEquals(6, vo.date.getDayOfMonth());
        Assert.assertEquals(9, vo.date.getHour());
        Assert.assertEquals(3, vo.date.getMinute());
        Assert.assertEquals(16, vo.date.getSecond());
        Assert.assertEquals(0, vo.date.getNano());
    }

    public void test_for_in() throws Exception {
        LocalDateTimeTest5.VO vo = JSON.parseObject("{\"date\":\"06-05-2016 09:03:16\"}", LocalDateTimeTest5.VO.class);
        Assert.assertEquals(2016, vo.date.getYear());
        Assert.assertEquals(5, vo.date.getMonthValue());
        Assert.assertEquals(6, vo.date.getDayOfMonth());
        Assert.assertEquals(9, vo.date.getHour());
        Assert.assertEquals(3, vo.date.getMinute());
        Assert.assertEquals(16, vo.date.getSecond());
        Assert.assertEquals(0, vo.date.getNano());
    }

    public static class VO {
        public LocalDateTime date;
    }
}

